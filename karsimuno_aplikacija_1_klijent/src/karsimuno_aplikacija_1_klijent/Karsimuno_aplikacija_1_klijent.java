/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package karsimuno_aplikacija_1_klijent;

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
public class Karsimuno_aplikacija_1_klijent {

    HashMap<String, String> opcijePokretanja;
    Socket socket = null;
    InputStream is = null;
    OutputStream os = null;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

        Karsimuno_aplikacija_1_klijent t = new Karsimuno_aplikacija_1_klijent();
        t.go();
    }

    public void go() {
        opcijePokretanja = new HashMap<>();
        opcijePokretanja.put("server", "localhost");
        opcijePokretanja.put("port", "4123");
        try {
            socket = new Socket(opcijePokretanja.get("server"), Integer.parseInt(opcijePokretanja.get("port")));

            is = socket.getInputStream();
            os = socket.getOutputStream();
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }

        String zahtjev
                = "USER " + opcijePokretanja.get("korisnik") + "; "
                + "PASSWD " + opcijePokretanja.get("lozinka") + "; PAUSE;";

        try {
            os.write(zahtjev.getBytes());
            os.flush();
            socket.shutdownOutput();

            StringBuilder sb = new StringBuilder();
            while (true) {
                int znak = is.read();
                if (znak == -1) {
                    break;
                }
                sb.append((char) znak);
            }
            handleServerResponse(sb);
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeServerConnection();
        }
    }

    /**
     * Otvara vezu sa serverom
     */
    private void openServerConnection() {
        try {
            socket = new Socket(opcijePokretanja.get("server"), Integer.parseInt(opcijePokretanja.get("port")));

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

    /**
     * Obrađuje odgovor servera na način da ga samo ispiše u konzolu ili i
     * pretvori u objekt tipa Evidencija
     *
     * @param odgovor Poruka koju je poslao server
     */
    private void handleServerResponse(StringBuilder odgovor) {
        System.out.println(odgovor);
    }
}
