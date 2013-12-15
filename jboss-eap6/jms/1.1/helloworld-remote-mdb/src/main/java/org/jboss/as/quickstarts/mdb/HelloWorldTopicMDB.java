package org.jboss.as.quickstarts.mdb;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: tomr
 * Date: 14/11/2013
 * Time: 08:47
 * To change this template use File | Settings | File Templates.
 */
@MessageDriven(name = "HelloWorldTopicMDB", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "tomr.testTopic"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "useJNDI", propertyValue = "false"),
        @ActivationConfigProperty(propertyName = "connectorClassName", propertyValue = "org.hornetq.core.remoting.impl.netty.NettyConnectorFactory"),
        @ActivationConfigProperty(propertyName = "connectionParameters", propertyValue = "host=ragga;port=5445"),
        @ActivationConfigProperty(propertyName = "useJNDI", propertyValue = "false"),
        @ActivationConfigProperty(propertyName = "user",propertyValue = "quickuser"),
        @ActivationConfigProperty(propertyName = "password",propertyValue = "quick123+"),
},mappedName = "java:jboss/jms/topic/testTopic")
              //java:jboss/jboss/gss/jms/topic/testTopic"

public class HelloWorldTopicMDB implements MessageListener {

    private final static Logger LOGGER = Logger.getLogger(HelloWorldTopicMDB.class
            .toString());

    /**
     * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
     */
 public void onMessage(Message rcvMessage) {
        TextMessage msg = null;
        try {
            if (rcvMessage instanceof TextMessage) {
                msg = (TextMessage) rcvMessage;
                LOGGER.info("Received Message: " + msg.getText());
            } else {
                LOGGER.warning("Message of wrong type: "
                        + rcvMessage.getClass().getName());
            }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

}

