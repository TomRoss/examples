package org.jboss.genericjms.provider;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;

public class RemoteJMSObjectFactory implements ObjectFactory { 

    private static final Logger log = Logger.getLogger(RemoteJMSObjectFactory.class.getName());

    private Context context = null;

    public RemoteJMSObjectFactory() {

        log.info("Generic (TIBCO) Remote Factory created.");

    }

    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception { 
	
        try {
	
            String jndi = (String) obj;

            log.info("Looking up object '" + jndi + "'.");

            if (log.isLoggable(Level.FINER)) {

                Enumeration elements = environment.elements();

                while (elements.hasMoreElements()){

                    String val = (String) elements.nextElement();

                    log.finer("JNDI environment = " + val);
                }
            }

            context = new InitialContext(environment);
	
            Object o = context.lookup(jndi);

            log.info("Found object '" + o + "'. Class '" + o.getClass().getName());

            return o;
            
        } catch (NamingException e) {
            
            log.log(Level.SEVERE,"ERROR",e);
            
            throw e; 
            
        } finally {
            
            context.close();
            
        }
    }
}




