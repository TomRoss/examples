package org.jboss.as.jms2;

import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: tomr
 * Date: 23/08/2013
 * Time: 15:36
 * To change this template use File | Settings | File Templates.
 */
public class Main {

    private static final Logger log = Logger.getLogger(Main.class.getName());

    public static void main(String[] args){

        log.info("<<< Starting Queue Producer (JMS2) >>>");

        QueueProducer qp = new QueueProducer();

        qp.sendMessage();

        System.exit(0);

    }


}
