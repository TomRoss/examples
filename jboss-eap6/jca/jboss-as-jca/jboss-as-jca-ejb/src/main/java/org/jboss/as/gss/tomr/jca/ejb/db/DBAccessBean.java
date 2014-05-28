package org.jboss.as.gss.tomr.jca.ejb.db;

import javax.ejb.Stateless;

/**
 * Created with IntelliJ IDEA.
 * User: tomr
 * Date: 28/02/2014
 * Time: 17:39
 * To change this template use File | Settings | File Templates.
 */

@Stateless(description = "Database access bean.")
public class DBAccessBean implements DBAccessLocal, DBAccessRemote {
}
