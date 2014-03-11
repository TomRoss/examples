package org.jboss.as.example.mdb.remote;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.MessageDrivenContext;

/**
 * Created with IntelliJ IDEA.
 * User: tomr
 * Date: 14/11/2013
 * Time: 08:47
 * To change this template use File | Settings | File Templates.
 */
@MessageDriven(name = "HelloWorldTopicMDB", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "testTopic"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "connectorClassName", propertyValue = "org.hornetq.core.remoting.impl.netty.NettyConnectorFactory"),
        @ActivationConfigProperty(propertyName = "connectionParameters", propertyValue = "host=localhost;port=5545"),
        @ActivationConfigProperty(propertyName = "useJNDI", propertyValue = "false"),
        @ActivationConfigProperty(propertyName = "user",propertyValue = "quickuser"),
        @ActivationConfigProperty(propertyName = "password",propertyValue = "quick123+"),
},mappedName = "java:jboss/jms/topic/testTopic")
          

public class HelloWorldRemoteTopicMDB implements MessageListener {

    private final static Logger log = Logger.getLogger(HelloWorldRemoteTopicMDB.class.toString());
    private static AtomicInteger mdbCnt = new AtomicInteger(0);
    private static final String mdbName = "remote-topic-mdb";
    private int msgCnt = 0;
    private int mdbID = 0;
    private TextMessage txtMsg = null;
    
    @Resource
    private MessageDrivenContext ctx;

    public HelloWorldRemoteTopicMDB() {
        
        mdbID = mdbCnt.getAndIncrement();
        
    }

    
    /**
     * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
     */
    public void onMessage(Message message) {
        
        try {
            if (message instanceof TextMessage) {
                txtMsg = (TextMessage) message;
                
                log.info("MDB[" + mdbName + ":" + mdbID + "] Received Message: " + txtMsg.getText());
                
                msgCnt++;
                
            } else {
                
                log.warning("MDB[" + mdbName + ":" + mdbID + "] Message of wrong type: " + message.getClass().getName());
                
            }
            
        } catch (JMSException jmsEx) {
            
            ctx.setRollbackOnly();
            
            log.log(Level.SEVERE, "MDB[" + mdbName + ":" + mdbID + "] Got error while excuting onMessage() method.", jmsEx);
            
            throw new RuntimeException(jmsEx);
            
        }
    }
 
    @PreDestroy
    public void printStats(){
        
        log.info("MDB[" + mdbName + ":" + mdbID + "] Processed " + msgCnt + " messages.");
        log.info("MDB[" + mdbName + ":" + mdbID + "] Closing.");
    }

    @PostConstruct
    public void init(){
        log.info("MDB[" + mdbName + ":" + mdbID + "] created.");
    }

}

