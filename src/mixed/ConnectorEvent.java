package mixed;

public class ConnectorEvent implements Runnable {
	
	MessageQueue remote;
	QueueBroker.ConnectListener listener;
	
	/**
	 * Once a connection has been established, this is the event that is sent in the event pump
	 * to call the listener and inform the server that this accepting has been established.
	 */
	ConnectorEvent(MessageQueue c, QueueBroker.ConnectListener l) {
		
		this.remote = c;
		this.listener = l;
	}

	@Override
	public void run() {

		this.listener.connected(this.remote);
	}
}
