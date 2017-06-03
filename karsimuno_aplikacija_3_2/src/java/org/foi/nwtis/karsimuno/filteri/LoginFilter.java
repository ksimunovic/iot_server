package org.foi.nwtis.karsimuno.filteri;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
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

/**
 *
 * @author Karlo
 */
@WebFilter(filterName = "FilterAplikacije", urlPatterns = {"/*"})
public class LoginFilter implements Filter {

    private final boolean debug = false;
    private static final String[] GOST = new String[] {"/login.xhtml"};

    // The filter configuration object we are associated with.  If
    // this value is null, this filter instance is not currently
    // configured. 
    private FilterConfig filterConfig = null;

    public LoginFilter() {
    }

    private void doBeforeProcessing(ServletRequest req, ServletResponse res)
            throws IOException, ServletException {
    }

    private void doAfterProcessing(ServletRequest req, ServletResponse res)
            throws IOException, ServletException {
        if (debug) {
            log("FilterAplikacije:DoAfterProcessing");
        }
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
    @Override
    public void doFilter(ServletRequest req, ServletResponse res,
            FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (debug) {
            log("FilterAplikacije:doFilter()");
        }

        doBeforeProcessing(req, res);

        if (debug) {
            log("FilterAplikacije:DoBeforeProcessing " + request.getServletPath());
        }

        String loginURI = request.getContextPath() + "/login.xhtml";
        String putanja = request.getServletPath();
        HttpSession session = request.getSession(false);
        if (session == null) {
            session = request.getSession(true);
        }
        
        if (!Arrays.asList(GOST).contains(putanja) && session.getAttribute("korisnik") == null && putanja.contains(".xhtml")) {
            response.sendRedirect(loginURI);
            return;
        }

        if (putanja.equals("/logout.xhtml") && session.getAttribute("korisnik") != null && putanja.contains(".xhtml")) {
            session.removeAttribute("korisnik");
            session.invalidate();
            response.sendRedirect(loginURI);
            return;
        }

        if (Arrays.asList(GOST).contains(putanja) && !putanja.equals("/lokalizacija.xhtml") && session.getAttribute("korisnik") != null && putanja.contains(".xhtml")) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        request.setCharacterEncoding("UTF-8");
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
                log("FilterAplikacije:Initializing filter");
            }
        }
    }

    /**
     * Return a String representation of this object.
     */
    @Override
    public String toString() {
        if (filterConfig == null) {
            return ("FilterAplikacije()");
        }
        StringBuffer sb = new StringBuffer("FilterAplikacije(");
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
