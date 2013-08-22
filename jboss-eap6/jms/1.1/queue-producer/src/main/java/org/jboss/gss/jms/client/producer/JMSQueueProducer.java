package org.jboss.gss.jms.client.producer;

import java.util.logging.Level;
import javax.jms.JMSException;
import javax.jms.Session;

import java.util.logging.Logger;
import org.jboss.gss.jms.client.Globals;



public class JMSQueueProducer extends JMSClient {
	private static final Logger logger = Logger.getLogger(JMSQueueProducer.class.getName());
	private final int messageCount = Integer.parseInt(config.getProperty(Globals.MESSAGE_COUNT_PROP));
	
	
	public JMSQueueProducer() {
            
            super();
            
            logger.info("Connecting to '" + config.getProperty(Globals.HOST_NAME_PROP) + ":" + config.getProperty(Globals.BIND_PORT_PORP) + "'.");
	}

	public void run(){
		
		int i = 1;
		String messageText = JMSClient.MESSAGE_TEXT;
		String hName = config.getProperty(Globals.HOST_NAME_PROP);
		threadName = Thread.currentThread().getName();
		
		logger.info("<<< Starting producer thread [" + threadName + "] >>>");
		
		try {
			
                    queueConnection = conMgr.getConnection();
                        
                    queue = conMgr.getObject(Globals.queueName);
                        
                    if (Globals.sessionTransacted){
				
                        queueSession = queueConnection.createQueueSession(true,Session.SESSION_TRANSACTED);
				
                    } else {
			
			queueSession = queueConnection.createQueueSession(false,Session.AUTO_ACKNOWLEDGE);
				
                    }
						
                    queueSender = queueSession.createSender(queue);
			
                    textMsg = queueSession.createTextMessage();
			
                    startTime = System.currentTimeMillis();
			
                    while (true){
				
				textMsg.setText(String.format(messageText,i,hName,messageCount));
				
				textMsg.setLongProperty(Globals.TOTAL_MESSAGE_COUNT_PROP, messageCount);
				
				/*if ( (i % 2) == 0 ){
					textMsg.setStringProperty("consumer", "node-one");
				} else {
					textMsg.setStringProperty("consumer", "node-two");
				}*/
				
				if ( i == 5 && Globals.msgThrowExc){
					textMsg.setBooleanProperty("throwException",true);
				} else {
					textMsg.setBooleanProperty("throwException",false);
				}
				
				
				queueSender.send(textMsg);
							
				logger.info("Message sent.");
				
				if ( Globals.sessionTransacted && (  i % Globals.batchSize ) == 0){
					queueSession.commit();
				}
				
				if ( (i % 1000) == 0){
					logger.info("[" + threadName + "] Message '" + i + "' sent.");
				}
				
				if ( i == messageCount){
					break;
				}
				
				i++;
				
				if (Globals.msgDelay != 0){
					
					Thread.sleep(Globals.msgDelay * 1000);
				}
			}
			
			finishTime = System.currentTimeMillis();
			
			totalTime = finishTime - startTime;
			
			results(Thread.currentThread().getName(),totalTime,messageCount);
			
		} catch (JMSException jmsEx) {
			
                    logger.log(Level.SEVERE,"[" + threadName + "] Got JMS Exception - ",jmsEx);
			
		} catch (InterruptedException interp){
			// ignore this
		} catch (Exception ex){
			
                    logger.log(Level.SEVERE,"[" + threadName + "] Got Exception - ",ex);
			
		} finally {
		
			
                    try {
			
                        cleanUp();
				
                        logger.info("[" + threadName + "] Producer finished.");
				
                        globals.countDown();
				
			} catch (JMSException jmsEx) {
				
                            logger.log(Level.SEVERE,"[" + threadName + "] Got JMS Exception - ",jmsEx);
				
			}
		}
		 
		
	}
}
