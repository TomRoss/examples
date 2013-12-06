package org.jboss.gss.jms.client;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jboss.gss.jms.client.producer.JMSQueueProducer;
import org.jboss.gss.jms.client.utils.TestConsumerException;

import javax.jms.JMSException;
import javax.naming.NamingException;


public class Client {
	private static Globals globals = Globals.getGlobals();
	private static final Logger logger = Logger.getLogger(Client.class.getName());
	private ExecutorService executor = null;
    private JMSQueueProducer queueProducer = null;
	public Client() {

		executor = globals.getExecutor();
	}

	public void runClient() {
		
		int clientCnt = Globals.clientCnt;

        logger.info("<<< Starting client program >>>");

        try {

		    for (int i = 0; i < clientCnt;i++){
			
			    queueProducer = new JMSQueueProducer();

                queueProducer.initClient();

			    executor.execute(queueProducer);
		    }
        } catch ( TestConsumerException tcEx){

            logger.log(Level.SEVERE,"Exiting because of ",tcEx);

            Globals.exitStatus = -1;

            System.exit(Globals.exitStatus);


        }catch (JMSException jmsEx) {

            logger.log(Level.SEVERE,"Got JMSException: ", jmsEx);

        } catch (NamingException naimingEx) {

            logger.log(Level.SEVERE,"Got NaimingException: ", naimingEx);

            System.exit(-1);
        }

        logger.info("All producers started.");

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
