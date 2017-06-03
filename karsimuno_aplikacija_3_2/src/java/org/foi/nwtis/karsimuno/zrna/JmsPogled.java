package org.foi.nwtis.karsimuno.zrna;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.foi.nwtis.karsimuno.poruke.JMSPorukaMqtt;
import org.foi.nwtis.karsimuno.SlusacPoruke;
import org.foi.nwtis.karsimuno.ejb.sb.SingletonSB;
import org.foi.nwtis.karsimuno.poruke.JMSPorukaMail;

/**
 *
 * @author Karlo
 */
@Named(value = "jmsPogled")
@RequestScoped
@ServerEndpoint("/websocket")
public class JmsPogled implements SlusacPoruke {

    private static final Set<Session> SESSIONS = ConcurrentHashMap.newKeySet();

    /**
     * Creates a new instance of MqttPogled
     */
    public JmsPogled() {
    }

    @OnOpen
    public void onOpen(Session session) {
        SESSIONS.add(session);
        SingletonSB.getInstance().addSlusac(this);
    }

    @Override
    public void novaPoruka(Object message) {
//        JMSPorukaMqtt jmsPoruka = (JMSPorukaMqtt) message;
        sendAll("a");
    }

    @OnClose
    public void onClose(Session session) {
        SESSIONS.remove(session);
        SingletonSB.getInstance().removeSlusac(this);
    }

    public static void sendAll(String text) {
        synchronized (SESSIONS) {
            for (Session session : SESSIONS) {
                if (session.isOpen()) {
                    session.getAsyncRemote().sendText(text);
                }
            }
        }
    }

    public List<JMSPorukaMqtt> getMqttPoruke() {
        List<JMSPorukaMqtt> orig = SingletonSB.getInstance().spremnikMqtt;
        List<JMSPorukaMqtt> temp = new ArrayList<>();

        if (orig != null) {
            for (JMSPorukaMqtt jMSPorukaMqtt : orig) {
                if (jMSPorukaMqtt != null) {
                    temp.add(jMSPorukaMqtt);
                }
            }
            Collections.reverse(temp);
        }
        return temp;
    }
    
    public List<JMSPorukaMail> getMailPoruke() {
        List<JMSPorukaMail> orig = SingletonSB.getInstance().spremnikMail;
        List<JMSPorukaMail> temp = new ArrayList<>();

        if (orig != null) {
            for (JMSPorukaMail jMSPorukaMail : orig) {
                if (jMSPorukaMail != null) {
                    temp.add(jMSPorukaMail);
                }
            }
            Collections.reverse(temp);
        }
        return temp;
    }

    public void updateView() {
    }
    
    public void clearMessages(String tip){
        if(tip.equals("mqtt")){
            SingletonSB.getInstance().spremnikMqtt = new ArrayList<>();
        }
        if(tip.equals("mail")){
            SingletonSB.getInstance().spremnikMail = new ArrayList<>();
        }
    }

}
