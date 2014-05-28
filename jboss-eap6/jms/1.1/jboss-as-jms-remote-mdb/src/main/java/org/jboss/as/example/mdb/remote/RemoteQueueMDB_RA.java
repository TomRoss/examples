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
package org.jboss.as.example.mdb.remote;

import org.jboss.ejb3.annotation.ResourceAdapter;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: tomr
 * Date: 14/11/2013
 * Time: 08:47
 * To change this template use File | Settings | File Templates.
 */

@MessageDriven(name = "RemoteQueueMDBRA", activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = "testQueueRA"),
    @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
    @ActivationConfigProperty(propertyName = "useJNDI", propertyValue = "true"),
    //@ActivationConfigProperty(propertyName = "connectorClassName", propertyValue = "org.hornetq.core.remoting.impl.netty.NettyConnectorFactory"),
    //@ActivationConfigProperty(propertyName = "connectionParameters", propertyValue = "${my.remote.location}"),
    //@ActivationConfigProperty(propertyName = "user", propertyValue = "quickuser"),
    //@ActivationConfigProperty(propertyName = "password",propertyValue = "quick123+")

},mappedName = "java:jboss/jms/queue/testQueueRA")

@ResourceAdapter("remote-hornetq-ra")

public class RemoteQueueMDB_RA implements MessageListener {

    private final static Logger log = Logger.getLogger(RemoteQueueMDB_RA.class.toString());
    private static AtomicInteger mdbCnt = new AtomicInteger(0);
    private static final String mdbName = "remote-queue-mdb";
    private int msgCnt = 0;
    private int mdbID = 0;
    private TextMessage txtMsg = null;

    @Resource
    private MessageDrivenContext ctx;

    public RemoteQueueMDB_RA() {
        mdbID = mdbCnt.getAndIncrement();
    }


	/**
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
    public void onMessage(Message message) {
    
        try {
			
            if (message instanceof TextMessage) {
                
                txtMsg = (TextMessage) message;
            
                log.info("MDB[" + mdbName + ":" + mdbID + "] Received Message: " + txtMsg.getText());
                
                msgCnt++;
                
            } else {
                
                log.warning("MDB[" + mdbName + ":" + mdbID + "] Message of wrong type: " + message.getClass().getName());
            }
            
        } catch (JMSException jmsEx) {
            
            ctx.setRollbackOnly();
            
            log.log(Level.SEVERE,"MDB[" + mdbName + ":" + mdbID + "] Got error while excuting onMessage() method.",jmsEx);
            
            throw new RuntimeException(jmsEx);
            
	}
    }
    
    @PreDestroy
    public void printStats(){
        log.info("MDB[" + mdbName + ":" + mdbID + "] Processed " + msgCnt + " messages.");
        log.info("MDB[" + mdbName + ":" + mdbID + "] Closing.");
    }

    @PostConstruct
    public void init(){
        log.info("MDB[" + mdbName + ":" + mdbID + "] created.");
    }
}
