package org.jboss.gss.jms.client;

import org.jboss.gss.jms.client.utils.ObjectStoreManager;

import java.net.InetAddress;
import java.net.UnknownHostException;
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

    public static final String INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";

	public static final long RECEIVE_TIME_OUT = 30000;
	
	public static final String HOST_NAME_PROP = "host.name";
	
	public static final String hostName = System.getProperty(Globals.HOST_NAME_PROP, "localhost");
	
	public static final String BIND_PORT_PORP = "bind.port";
	
	public static final String bindPort = System.getProperty(Globals.BIND_PORT_PORP, "4447");
	
	public static final String QUEUE_NAME_PROP = "queue.name";
	
	public static final String queueName = System.getProperty(Globals.QUEUE_NAME_PROP, "/jms/queue/testQueue");
	
	public static final String CONNECTION_NAME_PROP = "connection.name";
	
	public static final String connectionName = System.getProperty(Globals.CONNECTION_NAME_PROP, "jms/RemoteConnectionFactory");
        
	public static final String MESSAGE_COUNT_PROP = "message.number";
	
	public static final String messageNumber = System.getProperty(Globals.MESSAGE_COUNT_PROP, "1");

    public static final String MESSAGE_THROW_EXCEPTION_PROP = "message.throw.exception";

    public static final String MESSAGE_THROW_EXCEPTION_PROP_NAME = "ThrowException";

    public static final String JMSX_DELIVERY_COUNT_PROP = "JMSXDeliveryCount";

    public static final boolean msgThrowExc = Boolean.parseBoolean(System.getProperty(Globals.MESSAGE_THROW_EXCEPTION_PROP, "false"));

    public static final String MESSAGE_DELAY_PROP = "message.delay";

    public static final long msgDelay = Long.parseLong(System.getProperty(Globals.MESSAGE_DELAY_PROP, "0"));

    public static final String MESSAGE_EXPIRE_PROP = "message.expire";

    public static final long msgExpire = Long.parseLong(System.getProperty(Globals.MESSAGE_EXPIRE_PROP,"0"));

    public static final int totalMsg = Integer.parseInt(messageNumber);

    public static final String TOTAL_MESSAGE_COUNT_PROP = "TotalMessageCount";

    public static final String MESSAGE_GROUP_PROP = "message.group";

    public static final String messageGroup = System.getProperty(Globals.MESSAGE_GROUP_PROP,null);

	public static final String NUMBER_OF_CLIENTS_PROP = "client.number";
	
	public static final String clientNumber = System.getProperty(Globals.NUMBER_OF_CLIENTS_PROP, "1");
	
	public static final int clientCnt = Integer.parseInt(Globals.clientNumber);
	
	public static final String SESSION_TRANSACTED_PROP = "session.transacted";
	
	public static final boolean sessionTransacted = Boolean.parseBoolean(System.getProperty(Globals.SESSION_TRANSACTED_PROP, "false"));
	
	public static final String BATCH_SIZE_PROP = "btach.size";
	
	public static final int batchSize = Integer.parseInt(System.getProperty(Globals.BATCH_SIZE_PROP,"1"));

    public static final String USER_NAME_PROP = "username";
        
    public static final String userName = System.getProperty(Globals.USER_NAME_PROP, "quickuser");
        
    public static final String USER_PASSWORD_PROP = "password";
        
    public static final String userPassword = System.getProperty(Globals.USER_PASSWORD_PROP, "quick123+");

    public static String localHostName = null;

	public static final CountDownLatch latch = new CountDownLatch(clientCnt);
	
	public static int exitStatus = 0;
	
	private static ExecutorService executor = null;

    private ObjectStoreManager objMgr = null;

	public Globals() {

        if (Boolean.parseBoolean(System.getProperty("help","false"))){
            printHelp();
            System.exit(0);
        }

        executor = Executors.newFixedThreadPool(clientCnt);

        objMgr = new ObjectStoreManager();

        try {

            localHostName = InetAddress.getLocalHost().getHostName();

            logger.fine("Client running on host '" + localHostName + "'.");

        }  catch (UnknownHostException ex){

            logger.warning("Can't obtain host name.");
        }
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

    public ObjectStoreManager getObjectStore(){

        return this.objMgr;
    }

    public void printHelp(){

        logger.info("************************************");
        logger.info("mvn exec:java -D[property=value]");
        logger.info("List of properties with [default values]:");
        logger.info("\thost.name - message broker host name. [localhost]");
        logger.info("\tbind.port - message broker port number. [4447]");
        logger.info("\tmessage.number - number of message to send. [1]");
        logger.info("\tmessage.delay - deploy between each message send [0].");
        logger.info("\tmessage.throw.exception - throw exception when consuming message (only MDB).");
        logger.info("\tmessage.expire - expire message after. [0]");
        logger.info("\tmessage.group - sets the value of JMSXGroupID");
        logger.info("\tusername - user name. [quickuser]");
        logger.info("\tpassword - user password. [quick123+]");
        logger.info("\tconnection.name - connection factory name. [jms/RemoteConnectionFactory]");
        logger.info("\tqueue.name - queue name. [jms/queue/testQueue]");
        logger.info("\tclient.number - number of JMS clients. [1]");
        logger.info("\tsession.transacted - is the JMS session transacted. [false]");
        logger.info("\tbtach.size - transaction batch size. [1]");
        logger.info("************************************");
    }
}
