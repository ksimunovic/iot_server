/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.karsimuno.filteri;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.karsimuno.BazaHelper;

/**
 *
 * @author Karlo
 */
@WebFilter(filterName = "ServisiFilter", urlPatterns = {"/*"})
public class ServisiFilter implements Filter {

    private static final boolean debug = false;
    long startVrijeme;

    // The filter configuration object we are associated with.  If
    // this value is null, this filter instance is not currently
    // configured. 
    private FilterConfig filterConfig = null;

    public ServisiFilter() {
    }

    /**
     *
     * @param req The servlet request we are processing
     * @param res The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    public void doFilter(ServletRequest req, ServletResponse res,
            FilterChain chain)
            throws IOException, ServletException {
        startVrijeme = System.currentTimeMillis();

        if (debug) {
            log("ServisiFilter:doFilter()");
        }

        doBeforeProcessing(req, res);

        Throwable problem = null;
        try {
            chain.doFilter(req, res);
        } catch (Throwable t) {
            // If an exception is thrown somewhere down the filter chain,
            // we still want to execute our after processing, and then
            // rethrow the problem after that.
            problem = t;
            t.printStackTrace();
        }

        doAfterProcessing(req, res);

        // If there was a problem, we want to rethrow it if it is
        // a known type, otherwise log it.
        if (problem != null) {
            if (problem instanceof ServletException) {
                throw (ServletException) problem;
            }
            if (problem instanceof IOException) {
                throw (IOException) problem;
            }
            sendProcessingError(problem, res);
        }
    }

    private void doAfterProcessing(ServletRequest req, ServletResponse res)
            throws IOException, ServletException {
        if (debug) {
            log("ServisiFilter:DoAfterProcessing");
        }

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String putanja = request.getServletPath();

        if (putanja.contains("MeteoSOAP") || putanja.contains("webresources")) {
            zabiljeziPozivServisa(request, System.currentTimeMillis() - startVrijeme, response.getStatus());
        }
    }

    private void zabiljeziPozivServisa(HttpServletRequest request, long vrijemeProteklo, int status) {
        BazaHelper baza = new BazaHelper();
//        ResultSet rs = null;
        PreparedStatement stmt = null;
        String korisnickoIme;

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("korisnik") == null) {
            korisnickoIme = "gost";
        } else {
            korisnickoIme = (String) session.getAttribute("korisnik");
        }

//        String url = "";
//        if (request.getHeader("SOAPAction") != null) {
//            url = request.getHeader("SOAPAction");
//        } else {
//            url = request.getRequestURL().toString();
//        }
        try {
            Connection conn = baza.spojiBazu();

            String sql = "INSERT INTO DNEVNIK (korisnik, url, ipadresa, vrijeme, trajanje, status) VALUES (?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, korisnickoIme);
            stmt.setString(2, request.getRequestURL().toString());
            stmt.setString(3, request.getRemoteAddr());
            stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            stmt.setLong(5, vrijemeProteklo);
            stmt.setInt(6, status);

            stmt.executeUpdate();
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            baza.otkvaciBazu();
        }
    }

    private void doBeforeProcessing(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {
        if (debug) {
            log("ServisiFilter:DoBeforeProcessing");
        }
    }

    /**
     * Return the filter configuration object for this filter.
     */
    public FilterConfig getFilterConfig() {
        return (this.filterConfig);
    }

    /**
     * Set the filter configuration object for this filter.
     *
     * @param filterConfig The filter configuration object
     */
    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    /**
     * Destroy method for this filter
     */
    public void destroy() {
    }

    /**
     * Init method for this filter
     */
    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
        if (filterConfig != null) {
            if (debug) {
                log("ServisiFilter:Initializing filter");
            }
        }
    }

    /**
     * Return a String representation of this object.
     */
    @Override
    public String toString() {
        if (filterConfig == null) {
            return ("ServisiFilter()");
        }
        StringBuffer sb = new StringBuffer("ServisiFilter(");
        sb.append(filterConfig);
        sb.append(")");
        return (sb.toString());
    }

    private void sendProcessingError(Throwable t, ServletResponse response) {
        String stackTrace = getStackTrace(t);

        if (stackTrace != null && !stackTrace.equals("")) {
            try {
                response.setContentType("text/html");
                PrintStream ps = new PrintStream(response.getOutputStream());
                PrintWriter pw = new PrintWriter(ps);
                pw.print("<html>\n<head>\n<title>Error</title>\n</head>\n<body>\n"); //NOI18N

                pw.print("<h1>The resource did not process correctly</h1>\n<pre>\n");
                pw.print(stackTrace);
                pw.print("</pre></body>\n</html>"); //NOI18N
                pw.close();
                ps.close();
                response.getOutputStream().close();
            } catch (Exception ex) {
            }
        } else {
            try {
                PrintStream ps = new PrintStream(response.getOutputStream());
                t.printStackTrace(ps);
                ps.close();
                response.getOutputStream().close();
            } catch (Exception ex) {
            }
        }
    }

    public static String getStackTrace(Throwable t) {
        String stackTrace = null;
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            pw.close();
            sw.close();
            stackTrace = sw.getBuffer().toString();
        } catch (Exception ex) {
        }
        return stackTrace;
    }

    public void log(String msg) {
        filterConfig.getServletContext().log(msg);
    }

}
