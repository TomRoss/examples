/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the 
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstarts.mdb;

import java.util.logging.Logger;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * Created with IntelliJ IDEA.
 * User: tomr
 * Date: 14/11/2013
 * Time: 08:47
 * To change this template use File | Settings | File Templates.
 */

@MessageDriven(name = "HelloWorldQueueMDB", activationConfig = {
	@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
	@ActivationConfigProperty(propertyName = "destination", propertyValue = "tomr.testQueue"),
    @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
    @ActivationConfigProperty(propertyName = "useJNDI", propertyValue = "false"),
	@ActivationConfigProperty(propertyName = "connectorClassName", propertyValue = "org.hornetq.core.remoting.impl.netty.NettyConnectorFactory"),
	@ActivationConfigProperty(propertyName = "connectionParameters", propertyValue = "host=ragga;port=5445"),
    @ActivationConfigProperty(propertyName = "user", propertyValue = "quickuser"),
    @ActivationConfigProperty(propertyName = "password",propertyValue = "quick123+")

},mappedName = "java:jboss/jms/queue/testQueue")

public class HelloWorldQueueMDB implements MessageListener {

	private final static Logger LOGGER = Logger.getLogger(HelloWorldQueueMDB.class
			.toString());

	/**
	 * @see MessageListener#onMessage(Message)
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
