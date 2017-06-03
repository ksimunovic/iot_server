package org.foi.nwtis.karsimuno.ejb;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import org.foi.nwtis.karsimuno.poruke.JMSPorukaMqtt;
import org.foi.nwtis.karsimuno.ejb.sb.SingletonSB;

/**
 *
 * @author Karlo
 */
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/NWTiS_karsimuno_2")
    ,
@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")})
public class MqttDrivenBean implements MessageListener {

    public SingletonSB ssb = null;

    public MqttDrivenBean() {
    }

    @Override
    public void onMessage(Message message) {
        if (ssb == null) {
            ssb = SingletonSB.getInstance();
            ssb.ucitajSpremnik();
        }
        JMSPorukaMqtt jmsPoruka = null;
        ObjectMessage objectMessage = (ObjectMessage) message;
        try {
            jmsPoruka = (JMSPorukaMqtt) objectMessage.getObject();
        } catch (JMSException ex) {
            Logger.getLogger(MqttDrivenBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        ssb.dodajMqtt(jmsPoruka);
    }
}
