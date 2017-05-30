package org.foi.nwtis.karsimuno.dretve;

import java.io.Serializable;
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.foi.nwtis.karsimuno.JMSPoruka;
import org.foi.nwtis.karsimuno.konfiguracije.Konfiguracija;
import org.foi.nwtis.karsimuno.ejb.sb.SingletonSB;

/**
 *
 * @author Administrator
 */
public class ObradaMaila extends Thread {

    private boolean prekidObrade = false;
    private int brojPoruka;
    Konfiguracija konf;

    @Override
    public void interrupt() {
        super.interrupt();
        prekidObrade = true;
    }

    //TODO brojac jms poruka koji se inicijalno preuzima 
    @Override
    public void run() {
        super.run();

        String server = konf.dajPostavku("mail.server");
        String korisnik = konf.dajPostavku("mail.usernameThread");
        String lozinka = konf.dajPostavku("mail.passwordThread");
        String predmet = konf.dajPostavku("mail.subject");
        String mapa = konf.dajPostavku("mail.folderNWTiS");

        int trajanjeCiklusa = Integer.parseInt(konf.dajPostavku("mail.timeSecThread"));

        java.util.Properties properties = System.getProperties();
        properties.put("mail.smtp.host", server);

        while (!prekidObrade) {
            Session session = Session.getInstance(properties, null);
            long pocetakObrade = System.currentTimeMillis();
            int brojProcitanihPoruka = 0;
            int brojNWTiSPoruka = 0;

            try {
                Store store = session.getStore("imap");
                store.connect(server, korisnik, lozinka);

                Folder folder = store.getFolder("INBOX");
                folder.open(Folder.READ_ONLY);

                if (folder.getMessageCount() != 0) {
                    Message[] messages = folder.getMessages();

                    for (Message m : messages) {
                        if (m.getSubject().equals(predmet) && !m.isSet(Flags.Flag.SEEN) && m.isMimeType("text/plain")) {
                            prebaciuFolder(m, mapa);
                            brojProcitanihPoruka++;
                        }
                    }
                }

                folder = store.getFolder(mapa);
                brojNWTiSPoruka = folder.getMessageCount();
                
                store.close();
                long zavrsetakObrade = System.currentTimeMillis();
                long cekaj = trajanjeCiklusa * 1000 - (zavrsetakObrade - pocetakObrade) / 1000;
                if (cekaj <= 0) {
                    cekaj = 1;
                }

                JMSPoruka jmsPoruka = new JMSPoruka((brojPoruka+1), pocetakObrade, zavrsetakObrade, brojProcitanihPoruka, brojNWTiSPoruka);
                sendJMSMessageToNWTiS_karsimuno_1(jmsPoruka);
                prebrojiJmsPoruke();

                sleep(cekaj);
            } catch (InterruptedException | JMSException | MessagingException | NamingException ex) {
                Logger.getLogger(ObradaMaila.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void prebaciuFolder(Message message, String novaMapa) {
        Folder folder = message.getFolder();

        try {
            Folder newFolder = folder.getStore().getFolder(novaMapa);

            if (newFolder.exists() == false && !newFolder.create(Folder.HOLDS_MESSAGES)) {
                System.out.println("ERROR creating folder!");
                return;
            }

            message.setFlag(Flags.Flag.SEEN, true);
            List<Message> tempList = new ArrayList<>();
            tempList.add(message);
            Message[] tempMessageArray = tempList.toArray(new Message[tempList.size()]);
            folder.copyMessages(tempMessageArray, newFolder);
            message.setFlag(Flags.Flag.DELETED, true);

        } catch (MessagingException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public synchronized void start() {
        super.start();
        konf = SingletonSB.konf;
        brojPoruka = prebrojiJmsPoruke();
    }

    private void sendJMSMessageToNWTiS_karsimuno_1(Object messageData) throws JMSException, NamingException {
        Context c = new InitialContext();
        ConnectionFactory cf = (ConnectionFactory) c.lookup("java:comp/DefaultJMSConnectionFactory");
        Connection conn = null;
        javax.jms.Session s = null;
        try {
            conn = cf.createConnection();
            s = conn.createSession(false, s.AUTO_ACKNOWLEDGE);
            Destination destination = (Destination) c.lookup("jms/NWTiS_karsimuno_1");
            MessageProducer mp = s.createProducer(destination);

            ObjectMessage objMessage = s.createObjectMessage();
            objMessage.setObject((Serializable) messageData);
            mp.send(objMessage);

        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (JMSException e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot close session", e);
                }
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    private int prebrojiJmsPoruke() {
        int numMsgs = 0;
        try {
            InitialContext ctx = new InitialContext();

            Queue queue = (Queue) ctx.lookup("jms/NWTiS_karsimuno_1");
            QueueConnectionFactory connFactory = (QueueConnectionFactory) ctx.lookup("java:comp/DefaultJMSConnectionFactory");

            QueueConnection queueConn = connFactory.createQueueConnection();
            QueueSession queueSession = queueConn.createQueueSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
            QueueBrowser queueBrowser = queueSession.createBrowser(queue);
            queueConn.start();

            Enumeration e = queueBrowser.getEnumeration();
            while (e.hasMoreElements()) {
                e.nextElement();
                numMsgs++;
            }
            queueConn.close();
        } catch (NamingException | JMSException ex) {
            Logger.getLogger(ObradaMaila.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return numMsgs;
        }
    }
}
