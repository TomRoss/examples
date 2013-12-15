package org.jboss.as.jms.client;

import java.util.logging.Logger;

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
		
		logger.info("<<< Starting Simple JMS Topic Publisher >>>");
		
		Client client = new Client();
		
		client.runClient();
		
		logger.info("<<< Shutting down Simple JMS Topic Publisher >>>");
		
		System.exit(Globals.exitStatus);
	}

}
