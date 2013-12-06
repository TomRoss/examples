package org.jboss.as.jms.producer;

import org.jboss.as.jms.producer.AsynchQueueProducer;

import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: tomr
 * Date: 24/08/13
 * Time: 11:17
 * To change this template use File | Settings | File Templates.
 */
public class Main {
    private static final Logger log = Logger.getLogger(Main.class.getName());

    public static void main(String[] args){

        log.info("<<< Starting Queue Producer (JMS2) >>>");

        AsynchQueueProducer qp = new AsynchQueueProducer();

        qp.sendMessages();

        System.exit(0);

    }
}
