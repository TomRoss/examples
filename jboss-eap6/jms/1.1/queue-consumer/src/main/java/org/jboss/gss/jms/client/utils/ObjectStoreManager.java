package org.jboss.gss.jms.client.utils;

import org.jboss.gss.jms.client.Globals;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: tomr
 * Date: 20/08/2013
 * Time: 13:15
 * To change this template use File | Settings | File Templates.
 */
public class ObjectStoreManager {
    private static final Logger log = Logger.getLogger(ObjectStoreManager.class.getName());
    private static final Lock lock = new ReentrantLock();
    private static InitialContext ctx = null;
    private Hashtable env = null;


    public ObjectStoreManager(){

        env = new Hashtable<String,String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, Globals.INITIAL_CONTEXT_FACTORY);
        env.put(Context.PROVIDER_URL, "remote://" + Globals.hostName + ":" + Globals.bindPort);
        env.put(Context.SECURITY_PRINCIPAL, System.getProperty("username", Globals.userName));
        env.put(Context.SECURITY_CREDENTIALS, System.getProperty("password", Globals.userPassword));

    }

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
}
