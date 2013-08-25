/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.as.jms.client.consumer;

import javax.jms.JMSException;
import javax.naming.NamingException;

/**
 *
 * @author tomr
 */
public interface ConnectionManager {
 
    public <T> T getConnection() throws JMSException,NamingException;
    //public <T> T getObject(final String url) throws NamingException;
    
}
