package org.jboss.genericjms.provider;

import java.util.Hashtable; 
import java.util.Properties;
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

            //if (jndi.startsWith("java:/")) {
            //    jndi = jndi.replaceAll("java:/", "");

            //    log.info("Removing java:/ from object name.");
            //}

            final Properties env = new Properties(); 
	
            env.put(Context.INITIAL_CONTEXT_FACTORY,"com.tibco.tibjms.naming.TibjmsInitialContextFactory");
            env.put(Context.SECURITY_PRINCIPAL,"admin");
            env.put(Context.SECURITY_CREDENTIALS,"admin");
            env.put(Context.URL_PKG_PREFIXES, "com.tibco.tibjms.naming"); 
            env.put(Context.PROVIDER_URL, "tcp://ragga:7222");
	
            context = new InitialContext(env); 
	
            Object o = context.lookup(jndi);

            log.info("Found object '" + o + "'. Class '" + o.getClass().getName());


            return o;
            
        } catch (NamingException e) {
            
            e.printStackTrace();
            
            throw e; 
            
        } finally {
            
            context.close();
            
        }
    }
}




