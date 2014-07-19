package org.jboss.as.mdb.topic;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.jms.*;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by tomr on 20/12/13.
 */

@MessageDriven(name = "VerySimpleTopicMDB", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "testTopic"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "useJNDI",propertyValue = "false"),
        //@ActivationConfigProperty(propertyName = "clientID", propertyValue = "quickuser"),
        //@ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "Durable"),
        //@ActivationConfigProperty(propertyName = "subscriptionName", propertyValue = "hello-world"),
        //@ActivationConfigProperty(propertyName = "shareSubscriptions", propertyValue="true"),
        @ActivationConfigProperty(propertyName = "hA", propertyValue = "true")
},mappedName = "java:jboss/jms/topic/testTopic")

public class VerySimpleTopicMDB implements MessageListener {
    private static final Logger log = Logger.getLogger(VerySimpleTopicMDB.class.getName());
    private static final String mdbName = "very-simple-topic-mdb";
    private static AtomicInteger mdbCnt = new AtomicInteger(0);
    private int msgCnt = 0;
    private int mdbID = 0;
    @Resource
    private MessageDrivenContext ctx;
    private TextMessage txtMsg = null;
    private ObjectMessage objMsg = null;

    @Resource(mappedName = "java:/JmsXA")
    private ConnectionFactory cf;
    @Resource(mappedName = "java:jboss/jms/topic/outTopic")
    private Topic outTopic;
    private Connection con = null;
    private Session ses = null;

    private int totalMsgCnt = 0;
    private boolean throwException = false;

    public VerySimpleTopicMDB(){
        mdbCnt.incrementAndGet();
        mdbID = mdbCnt.get();
    }

    public void onMessage(Message message){

        try {

            // total number of messages sent to destination
            //totalMsgCnt = message.getIntProperty("TotalMessageCount");

            // throw exception from onMessage() method
            //throwException = message.getBooleanProperty("ThrowException");

            if ( message instanceof TextMessage){

                txtMsg = (TextMessage) message;

                log.info("MDB[" + mdbName + ":" + mdbID + "] Got text message '" + txtMsg + "' message ID = '" + txtMsg.getJMSMessageID() + "' message text = '" + txtMsg.getText() + "'.");

                con = cf.createConnection();

                ses = con.createSession(true,Session.SESSION_TRANSACTED);

                con.setExceptionListener(new MyExceptionListener());

                //dumpMessage(message);


            } else if (message instanceof ObjectMessage){

                objMsg = (ObjectMessage) message;

                log.info("MDB[" + mdbName + ":" + mdbID + "] Got object message of class '" + objMsg.getObject().getClass().getName() + "'.");

            }  else {

                log.warning("MDB[" + mdbName + ":" + mdbID + "] Unknown message type.");

            }

        } catch (JMSException jmsEx){

            ctx.setRollbackOnly();

            log.log(Level.SEVERE,"MDB[" + mdbName + ":" + mdbID + "] JMS Error",jmsEx);

        }
    }

    @PostConstruct
    public void init(){
        log.info("MDB[" + mdbName + ":" + mdbID + "] created.");
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
