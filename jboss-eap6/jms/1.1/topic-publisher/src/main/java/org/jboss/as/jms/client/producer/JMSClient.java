package org.jboss.as.jms.client.producer;

import java.util.Hashtable;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.logging.Logger;
import org.jboss.as.jms.client.Globals;

public class JMSClient implements Runnable {
	
	private static final Logger logger = Logger.getLogger(JMSClient.class.getName());
	
	private static final Lock lock = new ReentrantLock();
		
	public static final Globals globals = Globals.getGlobals();
	
	public static final String MESSAGE_TEXT = "This is message '%d' from host '%s'. Total message count is '%d'.";
	
	public static Properties config = null;
	
	public static InitialContext ctx = null;
	
	public TopicConnectionFactory qcf = null;
	
	public TopicConnection topicConnection = null;
	
	public TopicSession topicSession = null;
	
	public TopicPublisher topicPublisher = null;
	
	public Topic topic = null;
	
	public TextMessage textMsg = null;
	
	public Hashtable<String,String> env = null;
	
	public long startTime = 0;
	public long finishTime = 0;
	public long totalTime = 0;
	
	public String threadName = null;
	
    public ConnectionManager conMgr = null;
        
	public JMSClient() {
		
            config = new Properties();
	        config.put(Globals.CONNECTION_TYPE_PROP,Globals.connectionType);
            config.put(Globals.HOST_NAME_PROP, Globals.hostName);
            config.put(Globals.BIND_PORT_PORP, Globals.bindPort);
            config.put(Globals.CONNECTION_NAME_PROP,Globals.connectionName);
            config.put(Globals.TOPIC_NAME_PROP, Globals.topicName);
            config.put(Globals.MESSAGE_COUNT_PROP, Globals.messageNumber);
            
            conMgr = new ConnectionMangerImpl(config);
            
            logger.fine("JMSClient created.");
	}
	


	
	public void cleanUp() throws JMSException{
		
		if (logger.isLoggable(Level.FINE)){
			
			logger.log(Level.FINE,"Cleaning up JMS resources");
			
		}
			
		if ( topicPublisher != null){

            topicPublisher.close();
			if (logger.isLoggable(Level.FINE))
				logger.log(Level.FINE,"Sender closed.");
		}
		
		if ( topicSession != null){
			
			topicSession.close();
			if (logger.isLoggable(Level.FINE))
				logger.fine("Session closed.");
		}
		
		if ( topicConnection != null){
						
			topicConnection.close();
			if (logger.isLoggable(Level.FINE))
				logger.fine("connection closed.");
		}
	}

	public void results(String threadName, long time,long messageCnt){
	
		logger.info("********************************");
		
		
		if ((time/1000) > 0){
			logger.info("Thread [" + threadName + "] processed '" + messageCnt +"' messages in '" + (time/1000) + "' seconds.");
			logger.info("Thread [" + threadName + "] Average ration per messages/second is '" + messageCnt/(time/1000) +"'.");
		} else {
			logger.info("Thread [" + threadName + "] processed '" + messageCnt +"' messages in '" + time + "' microseconds.");
			logger.info("Thread [" + threadName + "] Average ration per messages/second is '" + messageCnt + "/" + time + "' microseconds.");
		}
		
		
		logger.info("********************************");
		
	}
	
	public String sessionTypeToString(int type){
		
		switch (type){
			case Session.AUTO_ACKNOWLEDGE:
				return "Auto-Acknowledge";
			case Session.CLIENT_ACKNOWLEDGE:
				return "Client-Acknowledge";
			case Session.DUPS_OK_ACKNOWLEDGE:
				return "Dups-OK_Acknowledge";
			case Session.SESSION_TRANSACTED:
				return "Session-Transacted";
			default:
				return "Unknown";
		}
	}
	
	public void run() {
		
		throw new RuntimeException("This method show never be called.");
		
	}
}
