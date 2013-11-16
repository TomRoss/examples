package org.jboss.as.jms.client;

import java.util.concurrent.ExecutorService;
import java.util.logging.Level;

import java.util.logging.Logger;
import org.jboss.as.jms.client.producer.JMSTopicPublisher;


public class Client {
	private static Globals globals = Globals.getGlobals();
	private static final Logger logger = Logger.getLogger(Client.class.getName());
	private ExecutorService executor = null;
	
	public Client() {
		executor = globals.getExecutor();
	}

	public void runClient() {
		
		int clientCnt = Globals.clientCnt;
		JMSTopicPublisher topicPublisher = null;

		logger.info("<<< Starting client program >>>");
		
		for (int i = 0; i < clientCnt;i++){
			
			topicPublisher = new JMSTopicPublisher();
			
			executor.execute(topicPublisher);
		}
		
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
						
						logger.log(Level.WARNING,"Thread interrupted.",e);
                                                
					}
				}
			}
		}
				
		logger.info(" === Client finished === ");
		
	}
}
