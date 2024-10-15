package sar;

public class ConnectorThread extends Thread {
	
	/**
	 * The thread that is in charge of attempting to connect to another broker.
	 * 
	 * It provides a connection between with the remote QueueBroker.
	 */

	ConnectorWorker r;
	
	public ConnectorThread(ConnectorWorker r) {

		super(r);
		this.r = r;
		super.start();
	}
}
