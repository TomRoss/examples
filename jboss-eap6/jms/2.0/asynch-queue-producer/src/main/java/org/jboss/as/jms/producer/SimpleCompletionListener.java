package org.jboss.as.jms.producer;

import javax.jms.CompletionListener;
import javax.jms.Message;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: tomr
 * Date: 25/08/13
 * Time: 11:09
 * To change this template use File | Settings | File Templates.
 */
public class SimpleCompletionListener implements CompletionListener {
    private static final Logger log = Logger.getLogger(SimpleCompletionListener.class.getName());
    private Exception ex = null;
    private CountDownLatch latch = null;

    public SimpleCompletionListener(CountDownLatch latch){

        this.latch = latch;

    }

    @Override
    public void onCompletion(Message message) {

        log.info("Message '" + message + "' delivered.");

        latch.countDown();
    }

    @Override
    public void onException(Message message, Exception ex) {

        this.ex = ex;

        log.log(Level.SEVERE,"Error occurred during message send operation.",ex);

        latch.countDown();
    }

    public Exception getException(){

        return this.ex;
    }
}
