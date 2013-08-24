package org.jboss.as.jms2;



import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: tomr
 * Date: 24/08/2013
 * Time: 14:28
 * To change this template use File | Settings | File Templates.
 */
public class AsynchQueueConsumer {
    private static final Logger log = Logger.getLogger(AsynchQueueConsumer.class.getName());
    private static CountDownLatch latch  = null;

    private static final String DEFAULT_MESSAGE = "Hello, World!";
    private static final String DEFAULT_CONNECTION_FACTORY_NAME = "jms/RemoteConnectionFactory";
    private static final String DEFAULT_QUEUE_NAME = "jms/queue/testQueue";
    private static final String DEFAULT_MESSAGE_COUNT = "1";
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

    public AsynchQueueConsumer(){

        try {

            latch = new CountDownLatch(5);

            log.info("Count down latch initialised to read 5 messages and terminate.");

            env = new Hashtable<String,String>();
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

        } catch (NamingException namingEx) {

            log.log(Level.SEVERE,"ERROR",namingEx);

            System.exit(1);
        }
    }

    public void readMessages(){

        
        try (javax.jms.JMSContext jmsCtx = qcf.createContext(userName,password,JMSContext.AUTO_ACKNOWLEDGE);
             javax.jms.JMSConsumer jmsConsumer = jmsCtx.createConsumer(queue)) {

            log.info("JMS resources context and consumer have bee created.");

            jmsConsumer.setMessageListener(new ObjectMessageListener());

            try {

                boolean s = latch.await(5, TimeUnit.MINUTES);

                if (!s){

                    log.warning("Latch timed out before reaching 0.");
                }

            } catch (InterruptedException e) {

                log.warning("Ignoring InterruptedException");

            }

        } catch (JMSRuntimeException jmsEx) {

            log.log(Level.SEVERE,"Error",jmsEx);
        }

    }

    class ObjectMessageListener implements MessageListener {

        @Override
        public void onMessage(Message message) {


            if (message instanceof ObjectMessage){


                try {

                    if (message.isBodyAssignableTo(MyMessage.class)){

                        MyMessage myMsg = message.getBody(MyMessage.class);

                        log.info("Received ObjectMessage '" + myMsg + "'.");

                        latch.countDown();

                    }

                } catch (JMSException jmsEx){

                    log.warning("Couldn't retrieve message body fom the message.");
                }

                log.info("Latch remaining count = " + latch.getCount());

            } else {

                log.warning("Received wrong message type.");
            }
        }
    }
}
