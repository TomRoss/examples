package org.jboss.as.jms.producer;

import javax.jms.JMSContext;
import javax.jms.QueueConnectionFactory;
import javax.jms.Queue;
import javax.jms.Message;
import javax.jms.JMSRuntimeException;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Hashtable;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;
import java.util.logging.Level;


import javax.naming.NamingException;

/**
 * Created with IntelliJ IDEA.
 * User: tomr
 * Date: 25/08/13
 * Time: 10:38
 * To change this template use File | Settings | File Templates.
 */

public class AsynchQueueProducer {
    private static final Logger log = Logger.getLogger(AsynchQueueProducer.class.getName());

    private static final String DEFAULT_MESSAGE = "Hello, World!!!";
    private static final String DEFAULT_CONNECTION_FACTORY_NAME = "jms/RemoteConnectionFactory";
    private static final String DEFAULT_QUEUE_NAME = "jms/queue/testQueue";
    private static final String DEFAULT_USERNAME = "quickuser";
    private static final String DEFAULT_PASSWORD = "quick123+";
    private static final String INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";

    private String hostName = System.getProperty("host.name","localhost");
    private String bindPort = System.getProperty("bind.port","8080");
    private String connectionFactoryName = System.getProperty("connection.factory", DEFAULT_CONNECTION_FACTORY_NAME);
    private String queueName = System.getProperty("queue.name",DEFAULT_QUEUE_NAME);
    private String userName = System.getProperty("username", DEFAULT_USERNAME);
    private String password =  System.getProperty("password", DEFAULT_PASSWORD);
    private String providerUrl = "http-remoting://" + hostName + ":" + bindPort;

    private Hashtable<String,String> env = null;
    private InitialContext ctx = null;

    private QueueConnectionFactory qcf = null;
    private Queue queue = null;
    private Message msg = null;
    private SimpleCompletionListener completionListener = null;

    private CountDownLatch latch = new CountDownLatch(1);

    public AsynchQueueProducer(){

        try {

            env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
            env.put(Context.PROVIDER_URL, System.getProperty(Context.PROVIDER_URL, providerUrl));
            env.put(Context.SECURITY_PRINCIPAL, userName);
            env.put(Context.SECURITY_CREDENTIALS, password);

            ctx = new InitialContext(env);

            log.fine("Initial context initialised.");

            Object obj = ctx.lookup(queueName);

            if ( obj != null){

                queue = (Queue) obj;
            }

            obj = ctx.lookup(connectionFactoryName);

            if (obj != null){

                qcf = (QueueConnectionFactory) obj;

            }

            log.info("All objects found in JNDI store.");

        } catch (NamingException namingEx) {

            log.log(Level.SEVERE,"ERROR",namingEx);

            System.exit(1);
        }
    }

    public void sendMessages()  {

        try (JMSContext context = qcf.createContext(userName,password)){

            completionListener = new SimpleCompletionListener(latch);

            context.createProducer().setAsync(completionListener).send(queue,DEFAULT_MESSAGE);

            log.info("Message sent, now waiting for reply");


            try {

                latch.await();

            } catch (InterruptedException e) {

                log.warning("Latch interupted.");

            }

            if (completionListener.getException()==null){

                log.info("Message successfully delivered, reply received from server");

            } else {

                throw new JMSRuntimeException("ERROR","",completionListener.getException());

            }
        }

    }

    @Override
    public String toString() {
        return "AsynchQueueProducer{" +
                "hostName='" + hostName + '\'' +
                ", bindPort='" + bindPort + '\'' +
                ", connectionFactoryName='" + connectionFactoryName + '\'' +
                ", queueName='" + queueName + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", providerUrl='" + providerUrl + '\'' +
                ", env=" + env +
                ", ctx=" + ctx +
                ", qcf=" + qcf +
                ", queue=" + queue +
                ", msg=" + msg +
                ", completionListener=" + completionListener +
                ", latch=" + latch +
                '}';
    }
}
