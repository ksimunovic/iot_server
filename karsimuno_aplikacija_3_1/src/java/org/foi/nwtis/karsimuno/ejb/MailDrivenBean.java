package org.foi.nwtis.karsimuno.ejb;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import org.foi.nwtis.karsimuno.poruke.JMSPorukaMail;
import org.foi.nwtis.karsimuno.ejb.sb.SingletonSB;

/**
 *
 * @author Karlo
 */
@MessageDriven
        (activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/NWTiS_karsimuno_1")
    ,
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class MailDrivenBean implements MessageListener {

    public SingletonSB ssb = null;

    public MailDrivenBean() {
    }

    @Override
    public void onMessage(Message message) {
        if (ssb == null) {
            ssb = SingletonSB.getInstance();
            ssb.ucitajSpremnik();
        }
        JMSPorukaMail jmsPoruka = null;
        ObjectMessage objectMessage = (ObjectMessage) message;
        try {
            jmsPoruka = (JMSPorukaMail) objectMessage.getObject();
        } catch (JMSException ex) {
            Logger.getLogger(MailDrivenBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        ssb.dodajMail(jmsPoruka);
    }
}
