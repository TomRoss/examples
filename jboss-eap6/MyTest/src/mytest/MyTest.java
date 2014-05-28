/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mytest;

import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 *
 * @author tomr
 */
public class MyTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Hashtable env = new Hashtable();
            
            env.put("java.naming.provider.url","file:///Users/tomr");
            
            env.put("java.naming.factory.initial","com.sun.jndi.fscontext.RefFSContextFactory");
            
            InitialContext ctx = new InitialContext(env);
            
            NamingEnumeration ne = ctx.list("");
            
            while (ne.hasMore()) {
                System.out.println(ne.next());
            }
            
            Object obj = ctx.lookup("wsmqXAConnectionFactory");
            
            if (obj != null){
                //QueueConnectionFactory qcf = (QueueConnectionFactory) obj;
                System.out.println("Object class = " + obj.getClass().getName());
            } 
        } catch (NamingException ex) {
            Logger.getLogger(MyTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
