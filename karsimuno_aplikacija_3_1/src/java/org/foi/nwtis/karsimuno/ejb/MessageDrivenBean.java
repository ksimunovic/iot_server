package org.foi.nwtis.karsimuno.ejb;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.Startup;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import org.foi.nwtis.karsimuno.JMSPorukaMqtt;
import org.foi.nwtis.karsimuno.ejb.sb.SingletonSB;

/**
 *
 * @author Administrator
 */
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/NWTiS_karsimuno_2")
    ,
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
//@Startup
public class MessageDrivenBean implements MessageListener {
    

    public MessageDrivenBean() {
        System.out.println("PALUI MDB!!!");
    }

    @Override
    public void onMessage(Message message) {
        JMSPorukaMqtt jmsPoruka = null;

        ObjectMessage objectMessage = (ObjectMessage) message;
        try {
            jmsPoruka = (JMSPorukaMqtt) objectMessage.getObject();
        } catch (JMSException ex) {
            Logger.getLogger(MessageDrivenBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("DOHVATIO SAM PORUKU" + jmsPoruka.toString());
        SingletonSB.spremnikMqtt.add(jmsPoruka);

    }

}
