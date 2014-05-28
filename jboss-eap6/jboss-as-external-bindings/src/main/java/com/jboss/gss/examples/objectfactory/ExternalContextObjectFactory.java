package com.jboss.exmaples.objectfactory;

import java.lang.reflect.Constructor;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;

/**
 * Hello world!
 *
 */
public class ExternalContextObjectFactory implements ObjectFactory {
    private static final Logger log = Logger.getLogger(ExternalContextObjectFactory.class.getName());

    public ExternalContextObjectFactory(){

        log.info("ExternalContextObjectFactory() called.");
    }

    public ExternalContextObjectFactory(Hashtable environment ){

        log.info("ExternalContextObjectFactory(Hashtable environment) called.");

    }

    @Override
    public Object getObjectInstance(final Object obj, final Name name, final Context nameCtx, final Hashtable environment) throws Exception {
        String initialContextClassName = (String) environment.get("InitialContext");

        log.info("getObjectInstance() InitialContext = " + initialContextClassName);

        if(initialContextClassName == null)
        {
            return new InitialContext((Hashtable) environment);
        }
        else
        {
            Class initialContextClass = Class.forName(initialContextClassName);
            InitialContext initialContext = (InitialContext) initialContextClass.newInstance();
            //initialContext.init((Hashtable) environment);
            for(Object key : environment.keySet()) {
                initialContext.addToEnvironment((String)key, environment.get(key));
            }
            return initialContext;
        }
    }
}
