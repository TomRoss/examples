package org.jboss.as.mdb;

import org.jboss.ejb3.annotation.ResourceAdapter;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.jms.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by tomr on 08/05/2014.
 */
@MessageDriven(name = "VerySimpleConsumerMDB", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "OUT.TOPIC"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")
        //@ActivationConfigProperty(propertyName = "useJNDI",propertyValue = "false"),
        //@ActivationConfigProperty(propertyName = "hA", propertyValue = "true")
})

@ResourceAdapter("amq-ragga")
public class TopicOutMDB implements MessageListener {
    private static final Logger log = Logger.getLogger(TopicOutMDB.class.getName());
    private static final String mdbName = "very-simple-topic-mdb";
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

    public TopicOutMDB(){
        mdbCnt.incrementAndGet();
        mdbID = mdbCnt.get();
    }

    public void onMessage(Message message){

        try {

            log.info("Got message - '" + message);

            if (message instanceof TextMessage){

                txtMsg = (TextMessage) message;

                log.info("MDB[" + mdbName + ":" + mdbID + "] Got text message '" + txtMsg.getText() + "'.");


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



}
