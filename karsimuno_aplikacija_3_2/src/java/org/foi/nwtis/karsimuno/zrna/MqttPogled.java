package org.foi.nwtis.karsimuno.zrna;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.foi.nwtis.karsimuno.ejb.sb.SingletonSB;

/**
 *
 * @author Karlo
 */
@Named(value = "mqttPogled")
@RequestScoped
@ServerEndpoint("/websocket")
public class MqttPogled implements SlusacPoruke {

    @EJB
    private SingletonSB singletonSB;

    private static final Set<Session> SESSIONS = ConcurrentHashMap.newKeySet();

    /**
     * Creates a new instance of MqttPogled
     */
    public MqttPogled() {
    }

    @OnOpen
    public void onOpen(Session session) {
        SESSIONS.add(session);
        SingletonSB.getInstance().setMqtt(this);
    }

    @OnClose
    public void onClose(Session session) {
        SESSIONS.remove(session);
    }

    @Override
    public void novaPoruka(Object message) {
        sendAll((String) message);
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

}
