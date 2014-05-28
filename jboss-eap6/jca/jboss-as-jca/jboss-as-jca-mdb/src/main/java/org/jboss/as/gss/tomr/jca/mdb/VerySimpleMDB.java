/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jboss.as.gss.tomr.jca.mdb;

import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
/**
 *
 * @author tomr
 */
@MessageDriven(name = "VerySimpleMDB",mappedName = "java:jboss/jms/queue/sourceQueue", activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = "targetQueue"),
    @ActivationConfigProperty(propertyName = "useJNDI", propertyValue = "false")

},description = "Very simpel MDB example with resource injection.")

public class VerySimpleMDB implements MessageListener {
    private static final Logger log = Logger.getLogger(VerySimpleMDB.class.getName());

    @Resource(lookup = "java:/JmsXA")
    private QueueConnectionFactory qcf;

    @Resource(lookup = "java:jboss/jms/queue/targetQueue")
    private Queue queue;

    public VerySimpleMDB() {
    }
    
    @Override
    public void onMessage(Message message) {
    }


    @PostConstruct
    public void init(){

    }

    @PreDestroy
    public void cleanUp(){

    }
}
