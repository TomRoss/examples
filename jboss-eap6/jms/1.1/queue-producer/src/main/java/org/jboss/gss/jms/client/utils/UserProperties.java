package org.jboss.gss.jms.client.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: tomr
 * Date: 22/12/2013
 * Time: 19:28
 * To change this template use File | Settings | File Templates.
 */
public class UserProperties implements Serializable {
    private Map<String,Object> props = null;

    public UserProperties(){

        props = new HashMap<String,Object>();

    }

    public void put(String s, Object o){

        props.put(s,o);

    }

    public Object get(String s){

        return props.get(s);

    }
}
