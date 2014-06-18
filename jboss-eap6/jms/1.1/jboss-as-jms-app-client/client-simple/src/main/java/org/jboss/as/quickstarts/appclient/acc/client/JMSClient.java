/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jboss.as.quickstarts.appclient.acc.client;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
/**
 *
 * @author tomr
 */
public class JMSClient implements MessageListener {
    private static final Logger LOG = Logger.getLogger(JMSClient.class.getName());
    
    private ConnectionFactory cf = null;
    private Connection con = null;
    @Resource(mappedName = "java:jboss/jms/topic/testTopic")
    private Topic topic;
    private TopicSubscriber subscriber;
    private TopicSession session = null;
    private TextMessage txtMsg = null;
    
    public JMSClient(ConnectionFactory cf){
        
         LOG.info("JMS Client created.");
         
         this.cf = cf;
    }
    
    public void init(){
     
        try {
            
            if ( cf != null){
                
                con = cf.createConnection();
                
                LOG.info("Connection created.");
                
            } else {
                
                LOG.log(Level.SEVERE,"Error: Connection Factory = null.");
            }
            
            
         
            
        } catch (JMSException jmsEx) {
            
            LOG.log(Level.SEVERE,"Error",jmsEx);
        }
        
    }
    
    public void consumeMessage(){
        
        try {
            con.start();
        
            Thread.sleep(60000);
            
        } catch (JMSException jmsEx){
            
            LOG.log(Level.SEVERE, null, jmsEx);
            
        } catch (InterruptedException ex) {
            
            LOG.log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void onMessage(Message msg) {
        LOG.info("Got message = " + msg.toString());
    }
    
}
