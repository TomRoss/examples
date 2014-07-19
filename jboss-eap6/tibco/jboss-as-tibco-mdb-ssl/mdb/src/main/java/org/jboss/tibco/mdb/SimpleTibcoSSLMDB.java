package org.jboss.tibco.mdb;

import org.jboss.ejb3.annotation.ResourceAdapter;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.jms.JMSException;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

/**
 * Created with IntelliJ IDEA.
 * User: tomr
 * Date: 03/01/2014
 * Time: 12:16
 * To change this template use File | Settings | File Templates.
 */
@MessageDriven(name = "VerySimpleTIBCOMDB", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "testQueueSSL"),
        //@ActivationConfigProperty(propertyName = "jndiParameters", propertyValue = "javax.net.ssl.trustStore=/Volumes/RedHat/Work/JBoss/EAP/JBoss-6/current/home/standalone-tibco/configuration/ssl/ca.jks;javax.net.ssl.keyStore=/Volumes/RedHat/Work/JBoss/EAP/JBoss-6/current/home/standalone-tibco/configuration/ssl/mars.pagzie.net.keystore.jks;javax.net.ssl.keyStorePassword=redhat;java.naming.security.principal=admin;java.naming.security.credentials=admin;java.naming.factory.initial=com.tibco.tibjms.naming.TibjmsInitialContextFactory;java.naming.provider.url=ssl://ragga:7243"),
        @ActivationConfigProperty(propertyName = "jndiParameters", propertyValue = "javax.net.ssl.trustStore=/Volumes/RedHat/Work/JBoss/EAP/JBoss-6/current/home/standalone-tibco/configuration/ssl/ca.jks;javax.net.ssl.keyStore=/Volumes/RedHat/Work/JBoss/EAP/JBoss-6/current/home/standalone-tibco/configuration/ssl/mars.pagzie.net.keystore.jks;javax.net.ssl.keyStorePassword=redhat;java.naming.factory.initial=com.tibco.tibjms.naming.TibjmsInitialContextFactory;java.naming.provider.url=ssl://ragga:7243"),
        @ActivationConfigProperty(propertyName = "connectionFactory", propertyValue = "XACFSSL"),
        @ActivationConfigProperty(propertyName = "user", propertyValue = "admin"),
        @ActivationConfigProperty(propertyName = "password", propertyValue = "admin")
        
})


//@ResourceAdapter("org.jboss.genericjms")
//@ResourceAdapter("generic-jms-ra-jar-1.0.1.Final-redhat-1.jar")
public class SimpleTibcoSSLMDB implements MessageListener {
    private static final Logger log = Logger.getLogger(SimpleTibcoSSLMDB.class.getName());
    private static final String mdbName = "very-simple-tibco-ssl-mdb";
    private static AtomicInteger mdbCnt = new AtomicInteger(0);
    private int msgCnt = 0;
    private int mdbID = 0;
    @Resource
    private MessageDrivenContext ctx;
    private TextMessage txtMsg = null;
    private ObjectMessage objMsg = null;

    private int totalMsgCnt = 0;
    private boolean throwException = false;

    public SimpleTibcoSSLMDB() {
        mdbCnt.incrementAndGet();
        mdbID = mdbCnt.get();
    }
    
    
    @Override
    public void onMessage(Message message) {
         try {

            log.info("MDB[" + mdbName + ":" + mdbID + "] Got message '" + message + ".");
            
            if ( message instanceof TextMessage){

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

}
