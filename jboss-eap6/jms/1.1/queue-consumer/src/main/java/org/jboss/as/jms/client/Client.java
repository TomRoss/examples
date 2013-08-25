package org.jboss.as.jms.client;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import org.jboss.as.jms.client.consumer.JMSQueueConsumer;
import org.jboss.as.jms.client.utils.TestConsumerException;

import javax.jms.JMSException;
import javax.naming.NamingException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Client {
	private static Globals globals = Globals.getGlobals();
	private static final Logger logger = Logger.getLogger(Client.class.getName());
	private ExecutorService executor = null;
	private JMSQueueConsumer queueConsumer = null;
	
	public Client() {
		executor = globals.getExecutor();
	}

	public void runClient() {
		
		int clientCnt = Globals.clientCnt;
		

		logger.info("<<< Starting JMS clients (" + clientCnt +  ") >>>");
		
		try {
		
			for (int i = 0; i < clientCnt;i++){
			
				queueConsumer = new JMSQueueConsumer();
			
			    queueConsumer.initConsumer();
				
				executor.execute(queueConsumer);
			}
		
		} catch ( TestConsumerException tcEx){
			
			logger.log(Level.SEVERE,"Exiting because of ",tcEx);
			
			Globals.exitStatus = -1;
			
			System.exit(Globals.exitStatus);
		} catch (JMSException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NamingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        logger.info("All consumers started.");
		
		globals.waitTillDone();

		this.executor.shutdown();
	
		
		if (!this.executor.isShutdown())
		{
			
			while(true){
				
				if (!this.executor.isShutdown()){
					break;
				} else {
					
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
				
		logger.info(" === Client finished === ");
		
	}
}
