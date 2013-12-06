package org.jboss.as.jms2;

import javax.jms.JMSContext;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.JMSProducer;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: tomr
 * Date: 23/08/2013
 * Time: 15:47
 * To change this template use File | Settings | File Templates.
 */

public class QueueProducer {

    private static final Logger log = Logger.getLogger(QueueProducer.class.getName());

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
    private JMSContext jmsCtx = null;
    private JMSProducer jmsProducer = null;
    private Queue queue = null;

    public QueueProducer(){

        try {

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

    public void sendMessage(){

        try (JMSContext jmsCtx = qcf.createContext(userName,password,JMSContext.AUTO_ACKNOWLEDGE)) {

            MyMessage myMsg = new MyMessage(0,"This is my message to the world.");

            jmsProducer = jmsCtx. createProducer().send(queue,myMsg);

            log.info("Message '" + myMsg + "' sent to destination '" + queue.getQueueName() + "'.");

        } catch (Exception ex) {

            log.log(Level.SEVERE,"Error",ex);
        }
    }

}
