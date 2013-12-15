/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.as.jms.client.producer;

import java.util.Hashtable;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.QueueConnectionFactory;
import javax.jms.TopicConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.jboss.as.jms.client.Globals;


/**
 *
 * @author tomr
 */
public class ConnectionMangerImpl implements ConnectionManager {
    private static final Logger logger = Logger.getLogger(ConnectionMangerImpl.class.getName());
    private static final Lock lock = new ReentrantLock();
    private static InitialContext ctx = null;
    private Hashtable<String,String> env = null;
    private Properties config = null;
    private String conType = null;
    private String eapVer = null;
    private Object obj =null;
    
    public ConnectionMangerImpl(Properties config){
        
        this.config = config;

        env = new Hashtable<String,String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, Globals.JBOSS_INITIAL_CONTEXT_FACTORY);
        env.put(Context.PROVIDER_URL, "remote://" + config.getProperty(Globals.HOST_NAME_PROP) + ":" + config.getProperty(Globals.BIND_PORT_PORP));
        env.put(Context.SECURITY_PRINCIPAL, Globals.userName);
        env.put(Context.SECURITY_CREDENTIALS, Globals.userPassword);

        logger.fine("Connection manager created with configuration '" + this.toString() + "'.");
    }
    
    @Override
    public <T> T getConnection() throws JMSException, NamingException {
        
        logger.log(Level.FINE,"Getting connection for type '" + conType + " and EAP ver '" + eapVer + "'.");

        return (T) getConnectionV6();

    }

    private <T> T getConnectionV6() throws NamingException, JMSException{
        Object conObj = null;

        
        obj = getObject(Globals.connectionName);
        
        logger.log(Level.FINE,"Creating connection for host:bindPort '" + Globals.hostName + ":" + Globals.bindPort + "'.");

        if ( obj instanceof QueueConnectionFactory){

            conObj = ((QueueConnectionFactory) obj).createQueueConnection(Globals.userName,Globals.userPassword);

        } else if (obj instanceof TopicConnectionFactory){

            conObj = ((TopicConnectionFactory) obj).createTopicConnection(Globals.userName,Globals.userPassword);

        }

        return (T) conObj;

    }
    

    
    @Override
    public <T> T getObject(String url) throws NamingException {
        Object obj = null;
		
		try {
			
			lock.lock();
			
			ctx = new InitialContext(env);
			
            if (ctx != null){
                            
                obj = ctx.lookup(url);
                            
            }
			
		} finally{
			
            if ( ctx != null){

                ctx.close();

            }
                    
            lock.unlock();
                    
		}
		
		return (T) obj;
    }

    
    public String toString(){
        StringBuilder str = new StringBuilder();
        
        str.append("Connection Manager: host='");
        str.append(Globals.hostName);
        str.append("' bindport='");
        str.append(Globals.bindPort);
        str.append(" connection type='");
        str.append(this.conType);
        str.append("'.");

        return str.toString();
    }
}
