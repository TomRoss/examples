package org.jboss.gss.jms.client.consumer;

import java.util.Hashtable;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.logging.Logger;
import org.jboss.gss.jms.client.Globals;
import org.jboss.gss.jms.client.utils.ObjectStoreManager;
import org.jboss.gss.jms.client.utils.TestConsumerException;

public class JMSClient implements Runnable {
	private static final Logger logger = Logger.getLogger(JMSClient.class.getName());
	
	private static final Lock lock = new ReentrantLock();
		
	public static final Globals globals = Globals.getGlobals();
	
	public static final String MESSAGE_TEXT = "This is message '%d' from host '%s'. Total message count is '%d'.";
	
	public static Properties config = null;
	
	public static InitialContext ctx = null;
	
	public QueueConnectionFactory qcf = null;
	
	public QueueConnection queueConnection = null;
	
	public QueueSession queueSession = null;
	
	public QueueSender queueSender = null;
	
	public QueueReceiver queueReceiver = null;
	
	public Queue queue = null;
	
	public TextMessage textMsg = null;
	
	public Hashtable<String,String> env = null;
	
	public long startTime = 0;
	public long finishTime = 0;
	public long totalTime = 0;
	
	public int messagesReceived = 0;
	
	public String threadName = null;

    public ConnectionManager conMgr = null;

    private ObjectStoreManager objMgr = null;

	public JMSClient() {
		
		threadName = Thread.currentThread().getName();
		
		config = new Properties();
		config.put(Globals.HOST_NAME_PROP,Globals.hostName);
		config.put(Globals.BIND_PORT_PORP,Globals.bindPort);
		config.put(Globals.CONNECTION_NAME_PROP,Globals.connectionName);
		config.put(Globals.QUEUE_NAME_PROP, Globals.queueName);
		config.put(Globals.MESSAGE_COUNT_PROP, Globals.messageNumber);

        objMgr = globals.getObjectStore();

        conMgr = new ConnectionMangerImpl(config);

        logger.fine("JMSClient created.");

         
	}
	
	public void initConsumer() throws TestConsumerException, NamingException, JMSException {

        queueConnection = conMgr.createConnection();

        queue = objMgr.getObject(Globals.queueName);

	}

	
	public void cleanUp() throws JMSException{
		
		if ( queueSender != null){
			
			queueSender.close();
			
			logger.fine("[" + threadName + "] Queue sender closed.");
		}
		
		if (queueReceiver != null){
			
			queueReceiver.close();
			
			logger.fine("[" + threadName + "] Queue receiver closed.");
		}
		
		if ( queueSession != null){
			
			queueSession.close();
			
			logger.fine("[" + threadName + "] Queue session closed.");
		}
		
		if ( queueConnection != null){
			
			queueConnection.stop();
			
			logger.fine("[" + threadName + "] Queue connection stopped.");
			
			queueConnection.close();
			
			logger.fine("[" + threadName + "] Queue connection closed.");
		}
	}

	public void results(String threadName, long time,long messageCnt){
	
		logger.info("[" + threadName + "] ********************************");
				
		if ( time <= 1000){
			
			logger.info("[" + threadName + "] processed '" + messagesReceived +"' messages in '" + time + "' milliseconds.");
			logger.info("[" + threadName + "] Average ration per messages/millisecond is '" + ((float)messagesReceived/(float)time) +"'.");
			
		} else {
			
			logger.info("[" + threadName + "] processed '" + messagesReceived +"' messages in '" + (time/1000) + "' seconds.");
			logger.info("[" + threadName + "] Average ration per messages/second is '" + messagesReceived/(time/1000) +"'.");
		}
		
		logger.info("[" + threadName + "] ********************************");
		
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
