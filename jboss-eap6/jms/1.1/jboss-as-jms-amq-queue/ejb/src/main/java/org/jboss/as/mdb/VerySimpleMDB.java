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
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "IN.QUEUE"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")
})

//@ResourceAdapter("amq-ra")
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

    @Resource(mappedName = "java:jboss/jms/activemq/ConnectionFactory")
    private ConnectionFactory cf;
    @Resource(mappedName = "java:jboss/jms/amq/queue/outQueue")
    private Queue outQueue;

    private Connection con;
    private Session session;
    private MessageProducer msgProducer;


    private int totalMsgCnt = 0;
    private boolean throwException = false;

    public VerySimpleMDB(){
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

                session = con.createSession(true,Session.SESSION_TRANSACTED);

                msgProducer = session.createProducer(outQueue);

                msgProducer.send(txtMsg);

                log.info("Message '" + txtMsg.getText() + "' sent to '" + outQueue.getQueueName() + "'.");

            }  else {

                log.warning("MDB[" + mdbName + ":" + mdbID + "] Unknown message type.");

            }

        } catch (JMSException jmsEx){

            ctx.setRollbackOnly();

            log.log(Level.SEVERE,"MDB[" + mdbName + ":" + mdbID + "] JMS Error",jmsEx);

        } finally {

            try {
                if (msgProducer != null) {
                    msgProducer.close();
                }

                if (session != null) {
                    session.close();
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

}
