package org.jboss.as.jms.client;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import java.util.logging.Logger;

public class Globals {
	private static final Logger logger = Logger.getLogger(Globals.class.getName());
	
	private static final Object lock = Globals.class;
	
	private static final Lock countDownLock = new ReentrantLock();
	
	private static Globals globals = null;

    public static final String JBOSS_INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";

	public static final long RECEIVE_TIME_OUT = 3000000;
	
	public static final String HOST_NAME_PROP = "host.name";
	
	public static final String hostName = System.getProperty(Globals.HOST_NAME_PROP, "localhost");
	
	public static final String BIND_PORT_PORP = "bind.port";
	
	public static final String bindPort = System.getProperty(Globals.BIND_PORT_PORP, "4447");
	
	public static final String TOPIC_NAME_PROP = "topic.name";
	
	public static final String topicName = System.getProperty(Globals.TOPIC_NAME_PROP, "jms/topic/testTopic");
	
	public static final String CONNECTION_NAME_PROP = "connection.name";
	
	public static final String connectionName = System.getProperty(Globals.CONNECTION_NAME_PROP, "jms/RemoteConnectionFactory");
	
    public static final String CONNECTION_TYPE_PROP = "connection.type";
        
    public static final String connectionType = System.getProperty(Globals.CONNECTION_TYPE_PROP, "topic");
        
	public static final String MESSAGE_COUNT_PROP = "message.number";
	
	public static final String messageNumber = System.getProperty(Globals.MESSAGE_COUNT_PROP, "1");
	
	public static final String TOTAL_MESSAGE_COUNT_PROP = "TotalMessageCount";
	
	public static final String NUMBER_OF_CLIENTS_PROP = "client.number";
	
	public static final String clientNumber = System.getProperty(Globals.NUMBER_OF_CLIENTS_PROP, "1");
	
	public static final int clientCnt = Integer.parseInt(Globals.clientNumber);
	
	public static final String SESSION_TRANSACTED_PROP = "session.transacted";
	
	public static final boolean sessionTransacted = Boolean.parseBoolean(System.getProperty(Globals.SESSION_TRANSACTED_PROP, "false"));
	
	public static final String BATCH_SIZE_PROP = "btach.size";
	
	public static final int batchSize = Integer.parseInt(System.getProperty(Globals.BATCH_SIZE_PROP,"1"));
	
	public static final String MESSAGE_THROW_EXCEPTION_PROP = "message.throw.exception";
		
	public static final boolean msgThrowExc = Boolean.parseBoolean(System.getProperty(Globals.MESSAGE_THROW_EXCEPTION_PROP, "false"));
	
	public static final String MESSAGE_DELAY_PROP = "message.delay";
	
	public static final long msgDelay = Long.parseLong(System.getProperty(Globals.MESSAGE_DELAY_PROP, "0"));
        
    public static final String USER_NAME_PROP = "hq.user.name";
        
    public static final String userName = System.getProperty(Globals.USER_NAME_PROP, "guest");
        
    public static final String USER_PASSWORD_PROP = "hq.user.password";
        
    public static final String userPassword = System.getProperty(Globals.USER_PASSWORD_PROP, "quick123+");
        
	public static final CountDownLatch latch = new CountDownLatch(clientCnt);
	
	public static int exitStatus = 0;
	
	private static ExecutorService executor = null;
	
	public Globals() {
		
            executor = Executors.newFixedThreadPool(clientCnt);
		
	}
	
	public static Globals getGlobals() {
        if (globals == null) {
            synchronized(lock) {
                if (globals == null)
                    globals = new Globals();
            }
        }
        
        return globals;
    }

	public void init(){
				
	}
	
	public void waitTillDone(){
		
		try {
			
			latch.await();
			
		} catch (InterruptedException e) {
	
			logger.log(Level.WARNING,"Got Interrupted exception ",e);
			
		}
		
	}
	
	public void countDown(){
		
		try {
			
			countDownLock.lock();
			
			latch.countDown();

			logger.log(Level.FINE,"Current latch count = " + latch.getCount());
			
		} finally {
			
			countDownLock.unlock();
		}
	}
	
	public ExecutorService getExecutor(){
		
		return executor;
	}

    public void printHelp(){

        logger.info("************************************");
        logger.info("mvn exec:java -D[property=value]");
        logger.info("List of properties:");
        logger.info("\thost.name");
        logger.info("\tbind.port");
        logger.info("\tmessage.number");
        logger.info("\tmessage.delay");
        logger.info("\tmessage.throw.exception");
        logger.info("\tusername");
        logger.info("\tpassword");
        logger.info("\tconnection.name");
        logger.info("\ttopic.name");
        logger.info("\tclient.number");
        logger.info("\tsession.transacted");
        logger.info("************************************");
    }
}
