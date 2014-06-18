package org.jboss.genericjms.provider;

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
    private static final String TIBCO_HOST_NAME = "tibco.host.name";
    private static final String TIBCO_BIND_PORT = "tibco.bind.port";
    private static final String STRIP_JAVA_CONTEXT = "strip.java.context";

    private Context context = null;
    private String tibcoHostName = null;
    private String tibcoBindPort = null;
    private boolean stripJavaConext = false;

    public RemoteJMSObjectFactory() {
        //
        // tibco.host.name - host name of the host hosting tibco message broker (default localhost)
        // tibco.bind.port - binding port of tibco message broker (default 7222)
        // strip-java-conect - strip off 'java:/' from JNDI name
        //

        tibcoHostName = System.getProperty(RemoteJMSObjectFactory.TIBCO_HOST_NAME,"localhost");
        tibcoBindPort = System.getProperty(RemoteJMSObjectFactory.TIBCO_BIND_PORT,"7222");
        stripJavaConext = Boolean.parseBoolean(System.getProperty(RemoteJMSObjectFactory.STRIP_JAVA_CONTEXT,"false"));

        log.info("Generic (TIBCO) Remote Factory created. Host=" + tibcoHostName + ":BindPort=" + tibcoBindPort + " strip-java-conext=" + stripJavaConext);

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
            env.put(Context.PROVIDER_URL, "tcp://" + this.tibcoHostName + ":" + this.tibcoBindPort);
	
            context = new InitialContext(env); 
	
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




