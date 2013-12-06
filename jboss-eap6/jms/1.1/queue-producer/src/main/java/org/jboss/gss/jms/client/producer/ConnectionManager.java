/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.gss.jms.client.producer;

import javax.jms.JMSException;
import javax.naming.NamingException;

/**
 *
 * @author tomr
 */
public interface ConnectionManager {
 
    public <T> T createConnection() throws JMSException,NamingException;
    //public <T> T getObject(final String url) throws NamingException;
    
}
