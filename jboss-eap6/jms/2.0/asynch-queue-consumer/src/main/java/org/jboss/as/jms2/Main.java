package org.jboss.as.jms2;

import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: tomr
 * Date: 24/08/2013
 * Time: 14:20
 * To change this template use File | Settings | File Templates.
 */
public class Main {
    private static final Logger log = Logger.getLogger(Main.class.getName());

    public static void main(String[] args){

        log.info("<<< Starting Asynchronous Queue Consumer (JMS2) >>>");

        AsynchQueueConsumer asynchQc = new AsynchQueueConsumer();

        asynchQc.readMessages();

        System.exit(0);

    }
}
