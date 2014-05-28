package org.jboss.as.mdb.queue;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.jms.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by tomr on 20/12/13.
 */

@MessageDriven(name = "VerySimpleMDB", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        //@ActivationConfigProperty(propertyName = "destination", propertyValue = "${my.test.queue}"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "/jms/queue/testQueue"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "useJNDI",propertyValue = "true"),
        @ActivationConfigProperty(propertyName = "hA", propertyValue = "true")
},mappedName = "java:jboss/jms/queue/testQueue")

public class VerySimpleMDB implements MessageListener {
    private static final Logger log = Logger.getLogger(VerySimpleMDB.class.getName());
    private static final String mdbName = "very-simple-queue-mdb";
    private static AtomicInteger mdbCnt = new AtomicInteger(0);
    private static String hostName= null;
    private int msgCnt = 0;
    private int mdbID = 0;
    @Resource
    private MessageDrivenContext ctx;
    private TextMessage txtMsg = null;
    private ObjectMessage objMsg = null;


    private int totalMsgCnt = 0;
    private boolean throwException = false;

    public VerySimpleMDB(){
        mdbCnt.incrementAndGet();
        mdbID = mdbCnt.get();
    }

    public void onMessage(Message message){

        try {

            log.info("MDB[" + mdbName + ":" + mdbID + "] Got message - '" + message);

            if ( message instanceof TextMessage){

                msgCnt++;

                txtMsg = (TextMessage) message;

                log.info("MDB[" + mdbName + ":" + mdbID + "] Got text message '" + txtMsg.getText() + "'.");

                throwException = txtMsg.getBooleanProperty("ThrowException");

                if (throwException){

                    log.info("MDB[" + mdbName + ":" + mdbID + "] This message asked to throw RuntimeException.");

                    throw new RuntimeException("This is a dummy exception thrown from onMessage() method.");
                }

            } else if (message instanceof ObjectMessage){

                objMsg = (ObjectMessage) message;

                log.info("MDB[" + mdbName + ":" + mdbID + "] Got object message of class '" + objMsg.getObject().getClass().getName() + "'.");

            }  else {

                log.warning("MDB[" + mdbName + ":" + mdbID + "] Unknown message type.");

            }

        } catch (JMSException jmsEx){

            ctx.setRollbackOnly();

            log.log(Level.SEVERE,"MDB[" + mdbName + ":" + mdbID + "] JMS Error",jmsEx);

        }  finally {


        }
    }

    @PostConstruct
    public void init(){
        log.info("MDB[" + mdbName + ":" + mdbID + "] created.");

        if (hostName == null){

            try {

                hostName = InetAddress.getLocalHost().getHostName();

            } catch (UnknownHostException e) {

                log.warning("Problem obtaining host name, setting hostName to 'unknown'.");

                hostName = "unknown";
            }
        }
    }

    @PreDestroy
    public void cleanUp(){
        log.info("MDB[" + mdbName + ":" + mdbID + "] Processed " + msgCnt + " messages.");
        log.info("MDB[" + mdbName + ":" + mdbID + "] Closing.");
    }

    private void dumpMessage(Message message) throws JMSException {


        log.log(Level.INFO,"JMS Properties: -");
        log.log(Level.INFO,"\tJMSMessageID = '" + message.getJMSMessageID() + "'.");
        log.log(Level.INFO,"\tJMSCorrelationID() = '" + message.getJMSCorrelationID() + "'.");
        log.log(Level.INFO,"\tJMSDeliveryMode() = '" + message.getJMSDeliveryMode() + "'.");
        log.log(Level.INFO,"\tJMSDestination() = '" + message.getJMSDestination() + "'.");
        log.log(Level.INFO,"\tJMSExpiration() = '" + message.getJMSExpiration() + "'.");
        log.log(Level.INFO,"\tJMSPriority() = '" + message.getJMSPriority() + "'.");
        log.log(Level.INFO,"\tJMSRedelivered() = '" + message.getJMSRedelivered() + "'.");
        log.log(Level.INFO,"\tJMSReplyTo() = '" + message.getJMSReplyTo() + "'.");
        log.log(Level.INFO,"\tJMSTimestamp = '" + message.getJMSTimestamp() + "'.");
        log.log(Level.INFO,"\tJMSType = '" + message.getJMSType() + "'.");

        log.log(Level.INFO,"User Properties: -");

        if ( message instanceof TextMessage){

            TextMessage txtMsg = (TextMessage) message;

            Enumeration<String> e = txtMsg.getPropertyNames();

            while (e.hasMoreElements()){

                String s = e.nextElement();

                log.info("\tProperty name '" + s + "'.");

                //message.getP

            }
        }

    }

}
