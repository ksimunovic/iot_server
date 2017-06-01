package org.foi.nwtis.karsimuno;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Karlo
 */
public class ServerHelper {

    HashMap<String, String> opcijePokretanja;
    Socket socket = null;
    InputStream is = null;
    OutputStream os = null;
    int port;
    String server = "localhost";
    static String zahtjev = "";

    public ServerHelper(int port) {
        this.port = port;
    }

    public String posaljiNaredbu(String zahtjev) {
        StringBuilder sb = null;
        openServerConnection();
        try {
            is = socket.getInputStream();
            os = socket.getOutputStream();
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }

        try {
            os.write(zahtjev.getBytes());
            os.flush();
            socket.shutdownOutput();

            sb = new StringBuilder();
            while (true) {
                int znak = is.read();
                if (znak == -1) {
                    break;
                }
                sb.append((char) znak);
            }
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeServerConnection();
        }
        return sb.toString();
    }

    /**
     * Otvara vezu sa serverom
     */
    private void openServerConnection() {
        try {
            socket = new Socket(server, port);
            is = socket.getInputStream();
            os = socket.getOutputStream();
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Zatvara vezu sa serverom
     */
    private void closeServerConnection() {
        try {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

}
