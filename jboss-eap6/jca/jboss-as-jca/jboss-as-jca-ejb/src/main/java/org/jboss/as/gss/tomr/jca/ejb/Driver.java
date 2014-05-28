/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jboss.as.gss.tomr.jca.ejb;

import org.jboss.as.gss.tomr.jca.ejb.db.DBAccess;
import org.jboss.as.gss.tomr.jca.ejb.db.DBAccessLocal;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Schedule;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.logging.Logger;

/**
 *
 * @author tomr
 */

@Singleton
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class Driver {
    private static final Logger log = Logger.getLogger(Driver.class.getName());

    @EJB
    private DBAccessLocal db;

    @Schedule(minute = "*/2",hour = "*",persistent = false)
    public void runDriver(){

    }

}
