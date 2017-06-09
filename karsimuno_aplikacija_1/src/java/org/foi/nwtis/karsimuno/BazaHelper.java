package org.foi.nwtis.karsimuno;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.foi.nwtis.karsimuno.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.karsimuno.slusaci.SlusacAplikacije;

/**
 *
 * @author Karlo
 */
public class BazaHelper {

    private ResultSet rs = null;
    private Connection conn = null;
    private PreparedStatement stmt = null;

    public BazaHelper() {
    }

    public Connection spojiBazu() throws SQLException, ClassNotFoundException {
        BP_Konfiguracija BP_Konf = (BP_Konfiguracija) SlusacAplikacije.getContext().getAttribute("BP_Konfig");

        String database = BP_Konf.getServerDatabase() + BP_Konf.getUserDatabase()+"?useUnicode=true&characterEncoding=utf-8";
        String user = BP_Konf.getUserUsername();
        String pass = BP_Konf.getUserPassword();

        Class.forName(BP_Konf.getDriverDatabase());
        return conn = DriverManager.getConnection(database, user, pass);
    }

    public void otkvaciBazu() {
        try {
            if (conn != null) {
                conn.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException ex) {
        }
    }
}
