package org.jboss.as.jms2;

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

        QueueConsumer qp = new QueueConsumer();

        qp.readMessage();

        System.exit(0);

    }
}
