/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.as.jms.client.consumer;

import org.jboss.gss.jms.client.Globals;

import javax.jms.JMSException;
import javax.jms.QueueConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author tomr
 */
public class ConnectionMangerImpl implements ConnectionManager {
    private static final Logger logger = Logger.getLogger(ConnectionMangerImpl.class.getName());
    private static final Lock lock = new ReentrantLock();
    private static final String INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
    private static final String PROVIDER_URL = "remote://localhost:4447";
    private static InitialContext ctx = null;

    //private Globals globals = Globals.getGlobals();

    private Hashtable<String,String> env = null;
    private Properties config = null;
    private String conType = null;
    private String eapVer = null;
    private QueueConnectionFactory qcf =null;
    
    public ConnectionMangerImpl(Properties config){
        
        this.config = config;
        
        conType = config.getProperty(Globals.CONNECTION_TYPE_PROP);
        eapVer = config.getProperty(Globals.EAP_VERSION_PROP);
     
        logger.fine("Connection manager created. Connection type '" + conType + "' EAP Ver '" + eapVer + "'.");
    }
    
    @Override
    public <T> T getConnection() throws JMSException, NamingException {
        
        logger.log(Level.FINE,"Getting connection for type '" + conType + " and EAP ver '" + eapVer + "'.");
        
        if ( conType.equals("queue") && eapVer.equals("EAP6") ){
            
            return (T) getConnectionV6();
                           
        } else if (conType.equals("queue") && eapVer.equals("EAP5")) {
            
            return (T) getConnectionV5();
        }
        
        return null;
    }

    private <T> T getConnectionV6() throws NamingException, JMSException{
        
        /*env = new Hashtable<String,String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
        env.put(Context.PROVIDER_URL, "remote://" + config.getProperty(Globals.HOST_NAME_PROP) + ":" + config.getProperty(Globals.BIND_PORT_PORP));
        env.put(Context.SECURITY_PRINCIPAL, System.getProperty("username", Globals.userName));
        env.put(Context.SECURITY_CREDENTIALS, System.getProperty("password", Globals.userPassword));
          */
        qcf = Globals.getGlobals().getObjectStore().getObject(Globals.connectionName);
        
        logger.log(Level.FINE,"Creating connection for host:bindPort '" + Globals.hostName + ":" + Globals.bindPort + "'.");
        
        return (T) qcf.createQueueConnection(Globals.userName, Globals.userPassword);
      
    }
    
    private <T> T getConnectionV5(){
     //env = new Hashtable<String,String>();
                    
                //env.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.NamingContextFactory");
                    
                //env.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");

                //env.put(Context.PROVIDER_URL, "jnp://" + config.getProperty(Globals.HOST_NAME_PROP) + ":" + config.getProperty(Globals.BIND_PORT_PORP));
                    
                /*try {
                    
                    qcf = (QueueConnectionFactory) getObject(config.getProperty(Globals.CONNECTION_NAME_PROP));
                            
                    queue = (Queue) getObject(config.getProperty(Globals.QUEUE_NAME_PROP));
                            
                } catch (NamingException nameEx) {
                            
                    logger.log(Level.SEVERE,"Got naiming exception - " , nameEx);
                            
                    System.exit(-1);
                    
                } catch (Exception ex){
                    
                    logger.log(Level.SEVERE,"Got exception - " , ex);
                    
                    System.exit(-1);
                } */
        throw new UnsupportedOperationException("Not implemented yet");
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
        str.append("' EAP ver='");
        str.append(this.eapVer);
        str.append("'.");

        return str.toString();
    }
}
