/**
 * Licensed to the Apache Software under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.foi.nwtis.karsimuno.slusaci;

import java.io.Serializable;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.foi.nwtis.karsimuno.JMSPorukaMqtt;
import org.foi.nwtis.karsimuno.ejb.eb.Poruke;
import org.foi.nwtis.karsimuno.ejb.sb.PorukeFacade;
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.CallbackConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

/**
 * Uses an callback based interface to MQTT. Callback based interfaces are
 * harder to use but are slightly more efficient.
 */
class SlusacMqtt extends Thread {

    private PorukeFacade porukeFacade = null;
    private CallbackConnection connection;
    private int slot;
    private int obradjenihPoruka;
    private MQTT mqtt;
    private long pocetakObrade = System.currentTimeMillis();
    private List<String> tekstovi = new ArrayList<>();
    final String destination = "/NWTiS/karsimuno";

    public SlusacMqtt(int slot) {
        this.slot = slot;
        setName("SlusacMQTT");
    }

    @Override
    public void interrupt() {
        super.interrupt();

        if (connection != null) {
            connection.kill(new Callback<Void>() {
                @Override
                public void onSuccess(Void t) {
                }

                @Override
                public void onFailure(Throwable thrwbl) {
                }
            });
        }
    }

    @Override
    public void run() {
        super.run();

        String user = "karsimuno";
        String password = "aCXwp";
        String host = "nwtis.foi.hr";
        int port = 61613;

        mqtt = new MQTT();
        try {
            mqtt.setHost(host, port);
        } catch (URISyntaxException ex) {
            Logger.getLogger(SlusacMqtt.class.getName()).log(Level.SEVERE, null, ex);
        }
        mqtt.setUserName(user);
        mqtt.setPassword(password);

        connection = mqtt.callbackConnection();
        connection.listener(new org.fusesource.mqtt.client.Listener() {
            int count = 0;

            @Override
            public void onConnected() {
                System.out.println("Otvorena veza na MQTT");
            }

            @Override
            public void onDisconnected() {
                System.out.println("Prekinuta veza na MQTT");
                System.exit(0);
            }

            @Override
            public void onFailure(Throwable value) {
                System.out.println("Problem u vezi na MQTT");
                System.exit(-2);
            }

            @Override
            public void onPublish(UTF8Buffer topic, Buffer msg, Runnable ack) {
                System.out.println("Stigla poruka br: " + count);
                count++;
                obradjenihPoruka++;

                String body = msg.utf8().toString();
                JsonReader reader = Json.createReader(new StringReader(body));
                JsonObject jo = reader.readObject();
                if (porukeFacade == null) {
                    porukeFacade = lookupPorukeFacadeBean();
                }
                DateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
                Timestamp ts = new Timestamp(System.currentTimeMillis());
                try {
                    Date date = format.parse(jo.getString("vrijeme"));
                    ts = new Timestamp(date.getTime());
                } catch (ParseException ex) {
                    Logger.getLogger(SlusacMqtt.class.getName()).log(Level.SEVERE, null, ex);
                }
                porukeFacade.create(new Poruke(null, jo.getInt("IoT"), ts, jo.getString("tekst"), Integer.parseInt(jo.getString("status"))));

                if (obradjenihPoruka >= slot) {
                    try {
                        JMSPorukaMqtt jmsPoruka = new JMSPorukaMqtt(count, pocetakObrade, System.currentTimeMillis(), obradjenihPoruka, tekstovi);
                        sendJMSMessageToNWTiS_karsimuno_2(jmsPoruka);
                    } catch (JMSException | NamingException ex) {
                        Logger.getLogger(SlusacMqtt.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    pocetakObrade = System.currentTimeMillis();
                    obradjenihPoruka = 0;
                    tekstovi = new ArrayList<>();
                }
            }
        });
        connection.connect(new Callback<Void>() {
            @Override
            public void onSuccess(Void value) {
                Topic[] topics = {new Topic(destination, QoS.AT_LEAST_ONCE)};
                connection.subscribe(topics, new Callback<byte[]>() {
                    @Override
                    public void onSuccess(byte[] qoses) {
                        System.out.println("Pretplata na: " + destination);
                    }

                    @Override
                    public void onFailure(Throwable value) {
                        System.out.println("Problem kod pretplate na: " + destination);
                        System.exit(-2);
                    }
                });

            }

            @Override
            public void onFailure(Throwable value) {
                System.out.println("Neuspjela pretplata na: " + destination);
                System.exit(-2);
            }
        });

        // Wait forever..
        synchronized (SlusacMqtt.class) {
            while (true) {
                try {
                    SlusacMqtt.class.wait();
                } catch (InterruptedException ex) {
                    System.out.println("MQTT Slusac prisilno ugasen!");
                }
            }
        }
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    private PorukeFacade lookupPorukeFacadeBean() {
        try {
            Context c = new InitialContext();
            return (PorukeFacade) c.lookup("java:global/karsimuno_aplikacija_2/karsimuno_aplikacija_2_1/PorukeFacade!org.foi.nwtis.karsimuno.ejb.sb.PorukeFacade");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    synchronized private void sendJMSMessageToNWTiS_karsimuno_2(Object messageData) throws JMSException, NamingException {
        Context c = new InitialContext();
        ConnectionFactory cf = (ConnectionFactory) c.lookup("java:comp/DefaultJMSConnectionFactory");
        Connection conn = null;
        Session s = null;
        try {
            conn = cf.createConnection();
            s = conn.createSession(false, s.AUTO_ACKNOWLEDGE);
            Destination destination = (Destination) c.lookup("jms/NWTiS_karsimuno_2");
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

}
