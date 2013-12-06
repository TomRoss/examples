package org.jboss.gss.jms.client.utils;


public class TestConsumerException extends Exception {

	public TestConsumerException() {
		super();
	}
	
	public TestConsumerException(String message) {
		
		super(message);
	}

	public TestConsumerException(String message, Throwable cause) {
		super(message,cause);
	}
}
