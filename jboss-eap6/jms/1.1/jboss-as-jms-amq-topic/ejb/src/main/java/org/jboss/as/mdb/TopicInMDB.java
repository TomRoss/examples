package org.jboss.as.mdb;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.*;
import javax.ejb.MessageDrivenContext;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jboss.ejb3.annotation.ResourceAdapter;

/**
 * Created by tomr on 20/12/13.
 */

@MessageDriven(name = "VerySimpleMDB", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "IN.TOPIC"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")
        //@ActivationConfigProperty(propertyName = "useJNDI",propertyValue = "false"),
        //@ActivationConfigProperty(propertyName = "hA", propertyValue = "true")
})

@ResourceAdapter("amq-ragga")
public class TopicInMDB implements MessageListener {
    private static final Logger log = Logger.getLogger(TopicInMDB.class.getName());
    private static final String mdbName = "topic-in-mdb";
    private static AtomicInteger mdbCnt = new AtomicInteger(0);
    private static String hostName= null;
    private int msgCnt = 0;
    private int mdbID = 0;
    @Resource
    private MessageDrivenContext ctx;
    private TextMessage txtMsg = null;
    private ObjectMessage objMsg = null;

    @Resource(lookup = "java:jboss/jms/activemq/ConnectionFactory")
    private ConnectionFactory cf;
    private Connection con;
    private TopicSession topicSession;
    private TopicPublisher topicPublsiher;
    @Resource(mappedName = "java:jboss/jms/amq/topic/outTopic")
    private Topic outTopic;

    private int totalMsgCnt = 0;
    private boolean throwException = false;

    public TopicInMDB(){
        mdbCnt.incrementAndGet();
        mdbID = mdbCnt.get();
    }

    public void onMessage(Message message){

        try {

            log.info("Got message - '" + message);

            if ( message instanceof TextMessage){

                txtMsg = (TextMessage) message;

                log.info("MDB[" + mdbName + ":" + mdbID + "] Got text message '" + txtMsg.getText() + "'.");

                con = cf.createConnection();

                con.setExceptionListener(new MyExceptionListener());

                topicSession = (TopicSession) con.createSession(true,Session.SESSION_TRANSACTED);

                topicPublsiher = topicSession.createPublisher(outTopic);

                topicPublsiher.publish(txtMsg);

                log.info("MDB[" + mdbName + ":" + mdbID + "] Message sent.");

            } else if (message instanceof ObjectMessage){

                objMsg = (ObjectMessage) message;

                log.info("MDB[" + mdbName + ":" + mdbID + "] Got object message of class '" + objMsg.getObject().getClass().getName() + "'.");

            }  else {

                log.warning("MDB[" + mdbName + ":" + mdbID + "] Unknown message type.");

            }

        } catch (JMSException jmsEx){

            ctx.setRollbackOnly();

            log.log(Level.SEVERE,"MDB[" + mdbName + ":" + mdbID + "] JMS Error",jmsEx);

        } finally {

            try {
                if (topicPublsiher != null) {
                    topicPublsiher.close();
                }

                if (topicSession != null) {
                    topicSession.close();
                }

                if (con != null) {

                    con.close();

                }
            } catch (JMSException jmsEx) {

                log.warning("Caught JMSException while cleaning up JMS resources.");
            }
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

    class MyExceptionListener implements ExceptionListener{
        @Override
        public void onException(JMSException e)
        {

            log.log(Level.WARNING,"Received",e);
        }
    }
}
