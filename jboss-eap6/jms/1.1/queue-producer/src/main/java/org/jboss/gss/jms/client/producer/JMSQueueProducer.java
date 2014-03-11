package org.jboss.gss.jms.client.producer;

import java.util.logging.Level;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.Message;
import javax.jms.DeliveryMode;
import java.util.logging.Logger;
import org.jboss.gss.jms.client.Globals;
//import org.jboss.gss.jms.client.utils.UserProperties;


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
		threadName = Thread.currentThread().getName();
		
		logger.info("<<< Starting producer thread [" + threadName + "] >>>");

        up.put(Globals.MESSAGE_THROW_EXCEPTION_PROP_NAME,new Boolean(true));
        up.put(Globals.JMSX_DELIVERY_COUNT_PROP,new Integer(0));
        up.put(Globals.TOTAL_MESSAGE_COUNT_PROP,new Integer(0));


		try {
                        
            if (Globals.sessionTransacted){
				
                queueSession = queueConnection.createQueueSession(true,Session.SESSION_TRANSACTED);
				
            } else {
			
			    queueSession = queueConnection.createQueueSession(false,Session.AUTO_ACKNOWLEDGE);
				
            }
						
            queueSender = queueSession.createSender(queue);
			
            textMsg = queueSession.createTextMessage();

            startTime = System.currentTimeMillis();
			
            while (true){
				
				textMsg.setText(String.format(messageText,i,Globals.localHostName,messageCount));

                //UserProperties up = new UserProperties();

				textMsg.setIntProperty(Globals.TOTAL_MESSAGE_COUNT_PROP, Globals.totalMsg);

				if ( i == 5 && Globals.msgThrowExc){

					textMsg.setBooleanProperty(Globals.MESSAGE_THROW_EXCEPTION_PROP_NAME,true);

				} else {

					textMsg.setBooleanProperty(Globals.MESSAGE_THROW_EXCEPTION_PROP_NAME,false);

				}
				
				if (Globals.msgExpire == 0){

                    queueSender.send(textMsg);

                }   else {

                    queueSender.send(queue,textMsg,DeliveryMode.PERSISTENT,Message.DEFAULT_PRIORITY,Globals.msgExpire);
                }

				
				if ( Globals.sessionTransacted && ((  i % Globals.batchSize ) == 0)){

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

			logger.log(Level.WARNING,"This thread has been interruped.",interp);

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
