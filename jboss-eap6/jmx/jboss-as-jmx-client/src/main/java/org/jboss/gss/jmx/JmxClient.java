package org.jboss.gss.jmx;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: tomr
 * Date: 04/04/2014
 * Time: 14:25
 * To change this template use File | Settings | File Templates.
 */

public class JmxClient {
    private static final Logger log = Logger.getLogger(JmxClient.class.getName());

    public static void main(String[] args){

        JmxClient jmxClient = new JmxClient();

        try {

            jmxClient.printMBeanCount();

        } catch (Exception ex ) {

            log.log(Level.SEVERE, "ERROR", ex);

        }
        System.exit(0);
    }

    public int printMBeanCount() throws Exception {
        String host = "localhost";
        int port = 9999;  // management-native port
        String urlString = System.getProperty("jmx.service.url","service:jmx:remoting-jmx://" + host + ":" + port);
        JMXConnector jmxConnector = null;
        Map<String, Object> env = new HashMap<String,Object>();
        String[] credentials = new String[]{"admin","quick123+"};
        ObjectName objectName = new ObjectName("jboss.as:management-root=server");

        env.put("jmx.remote.credentials", credentials);

        try {

            JMXServiceURL serviceURL = new JMXServiceURL(urlString);

            jmxConnector = JMXConnectorFactory.connect(serviceURL, env);

            MBeanServerConnection connection = jmxConnector.getMBeanServerConnection();

            log.info("Bean count = " + connection.getMBeanCount());

            log.info("Server state = " + connection.getAttribute(objectName,"serverState"));

            return connection.getMBeanCount();

        } finally {

            if(jmxConnector != null) jmxConnector.close();

        }
    }

}
