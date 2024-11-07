package event;

public class RdV {
	
	boolean closed;
	int port;
	Broker acceptingBroker;
	Broker connectingBroker;
	Broker.AcceptListener listener;
	
	RdV(Broker b, Broker.AcceptListener l, int port) {
		
		this.port = port;
		this.connectingBroker = b;
		this.acceptingBroker = null;
		this.listener = l;
		this.closed = false;
	}
	
	Channel join(Broker b, int port) {
		
		this.acceptingBroker = b;
		
		Channel left  = (this.closed) ? null : new Channel(port);
		Channel right = (this.closed) ? null : new Channel(port);
		
		left.plug(right);
		
		this.listener.accepted(left);
		
		return right;
	}
	
	boolean met() {
		
		return (this.acceptingBroker != null) && (this.connectingBroker != null);
	}
}
