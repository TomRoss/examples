package org.jboss.gss.jms.client.consumer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jboss.gss.jms.client.Globals;
import org.jboss.gss.jms.client.utils.TestConsumerException;

public class JMSQueueConsumer extends JMSClient {
	private static final Logger logger = Logger.getLogger(JMSQueueConsumer.class.getName());
	private final int messageCount = Integer.parseInt(config.getProperty(Globals.MESSAGE_COUNT_PROP));
	
	
	public JMSQueueConsumer(){
		
	}

	private void consumeMessages() throws TestConsumerException {
		int i = 1;
		String messageText = JMSQueueConsumer.MESSAGE_TEXT;
		String hName = Globals.hostName;
		Message message = null;
		boolean msgTransacted = Globals.sessionTransacted;
		
		
		
		logger.info("[" + threadName + "]<<< Starting consumer thread >>>");
		
		try {
			
			if (msgTransacted){
				
				queueSession = queueConnection.createQueueSession(true,Session.SESSION_TRANSACTED);
				
			} else {
				
				queueSession = queueConnection.createQueueSession(false,Session.AUTO_ACKNOWLEDGE);
			}
			
			logger.info("[" + threadName + "] Created queue session '" + this.sessionTypeToString(queueSession.getAcknowledgeMode()) + "'.");
			
			queueReceiver = queueSession.createReceiver(queue);
			
			logger.info("[" + threadName + "] Queue receiver for queue '" + queueReceiver.getQueue().getQueueName() + "' created.");
			
			queueConnection.start();
			
			logger.info("[" + threadName + "] Connection started. Starting receiving messages.");
			
			startTime = System.currentTimeMillis();
			
			while (true){
				
				message = queueReceiver.receive(Globals.receiveTimeOut);
				
				if (message != null && message instanceof TextMessage){
				
					int msgCnt = message.getIntProperty(Globals.TOTAL_MESSAGE_COUNT_PROP);
					
				    textMsg = (TextMessage) message;
						
					logger.info("[" + threadName + "] Messages '" + i + "' consumed. Message text '" + textMsg.getText() + "'.");
					logger.info("[" + threadName + "] Message count property = '" + msgCnt +"'.");
						
					
					if ( i == messageCount){
						
						messagesReceived = i;
						
						break;
					}
					
					i++;
					
					messagesReceived = i;
					
					if ( Globals.messageDelay != 0){
						
						try {
							
							Thread.sleep(Globals.messageDelay);
							
						} catch(InterruptedException intEx){
							
							logger.log(Level.WARNING,"No no no",intEx);
							
						}
					}

                    if ( Globals.sessionTransacted && ((  i % Globals.batchSize ) == 0)){

                        queueSession.commit();
                    }

				} else if ( message == null){
					
					logger.info("[" + threadName + "] Receive() method timed out after '" + (Globals.receiveTimeOut/1000) + "' seconds.");
					
					break;
					
				} else {
					
					logger.log(Level.WARNING,"[" + threadName + "] Received unknow message type. Ignoring.");
					
					break;
					
				}
			}
			
			finishTime = System.currentTimeMillis();
			
			totalTime = finishTime - startTime;
			
			results(threadName,totalTime,messageCount);
			
		} catch (JMSException jmsEx) {
			
			throw new TestConsumerException("[" + threadName + "] Exiting while consuming messages. ",jmsEx);
			
		} catch (Exception ex){
			
			throw new TestConsumerException("[" + threadName + "] Exiting while consuming messages. ",ex);
			
		} finally {
		
			
			try {
				
				cleanUp();
				
				logger.info("[" + threadName + "] Consumer finished.");
				
				globals.countDown();
				
			} catch (JMSException jmsEx) {
				
				logger.log(Level.SEVERE,"[" + threadName + "] Got JMS Exception while cleaning up JMS resources - ",jmsEx);
				
				throw new TestConsumerException("[" + threadName + "] Exiting while cleaning up JMS resources.",jmsEx);
			}
		}
		 
	}
	
	public void run(){
		
		try {
			
			consumeMessages();
			
		} catch (TestConsumerException exitError) {
			
			logger.log(Level.SEVERE,"ERROR",exitError);
			
			if (totalTime == 0){
				totalTime = System.currentTimeMillis() - startTime;
			}
			results(Thread.currentThread().getName(),totalTime,messageCount);
		}
		
	}
}
