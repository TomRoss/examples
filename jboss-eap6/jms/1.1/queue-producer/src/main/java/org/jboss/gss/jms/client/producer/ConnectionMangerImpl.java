/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.gss.jms.client.producer;

import java.util.Hashtable;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.QueueConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.jboss.gss.jms.client.Globals;
//import static org.jboss.gss.jms.client.producer.JMSClient.ctx;

/**
 *
 * @author tomr
 */
public class ConnectionMangerImpl implements ConnectionManager {
    private static final Logger logger = Logger.getLogger(ConnectionMangerImpl.class.getName());

    private Hashtable<String,String> env = null;
    private Properties config = null;
    private QueueConnectionFactory qcf =null;
    
    public ConnectionMangerImpl(Properties config){
        
        this.config = config;
        logger.fine("Connection manager created.");
    }



    public <T> T createConnection() throws NamingException, JMSException{

        qcf = Globals.getGlobals().getObjectStore().getObject(Globals.connectionName);

        logger.log(Level.FINE,"Creating connection for host:bindPort " + Globals.hostName + ":" + Globals.bindPort + " with user:password" + Globals.userName + ":" + Globals.userPassword + "'.");

        return (T) qcf.createQueueConnection(Globals.userName, Globals.userPassword);

    }
    


    
    public String toString(){
        StringBuilder str = new StringBuilder();
        
        str.append("Connection Manager: host='");
        str.append(Globals.hostName);
        str.append("' bindport='");
        str.append(Globals.bindPort);
        str.append(" connection type='");
        str.append("'.");

        return str.toString();
    }
}
