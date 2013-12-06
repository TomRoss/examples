package org.jboss.as.jms2;


import java.io.Serializable;


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
