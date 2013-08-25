package org.jboss.as.jms.client;

import java.util.logging.Logger;
import java.util.logging.Level;

public class Main {
	private static final Logger logger = Logger.getLogger(Main.class.getName());
	
	
	public Main() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		
		logger.info("<<< Starting Simple JMS Queue Consumer >>>");
		
		Client client = new Client();
		
		client.runClient();
		
		logger.info("<<< shutting down Simple JMS Queue Consumer >>>");
		
		System.exit(Globals.exitStatus);
	}

}
