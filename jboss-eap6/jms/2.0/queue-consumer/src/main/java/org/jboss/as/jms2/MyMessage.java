package org.jboss.as.jms2;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: tomr
 * Date: 24/08/13
 * Time: 11:35
 * To change this template use File | Settings | File Templates.
 */
public class MyMessage implements Serializable {
    private int indx;
    private String str = null;

    MyMessage(int indx,String str) {
        this.indx = indx;
        this.str = str;
    }

    public int getIndx() {
        return this.indx;
    }

    public String getStr(){

        return this.str;
    }

    public String toString(){

        StringBuilder buf = new StringBuilder();

        buf.append("[indx = " + this.indx + " - str = " + str + "]");

        return buf.toString();
    }
}
