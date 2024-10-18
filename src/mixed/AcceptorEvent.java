package mixed;

public class AcceptorEvent implements Runnable {
	
	MessageQueue remote;
	QueueBroker.AcceptListener listener;
	
	/**
	 * Once a connection has been established, this is the event that is sent in the event pump
	 * to call the listener and inform the server that this accepting has been established.
	 */
	AcceptorEvent(MessageQueue c, QueueBroker.AcceptListener l) {
		
		this.remote = c;
		this.listener = l;
	}

	@Override
	public void run() {

		this.listener.accepted(this.remote);
	}
}
