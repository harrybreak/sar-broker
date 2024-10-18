package event;

public class RdV {
	
	int port;
	Broker acceptingBroker;
	Broker connectingBroker;
	Broker.AcceptListener listener;
	
	RdV(Broker b, Broker.AcceptListener l, int port) {
		
		this.port = port;
		this.connectingBroker = b;
		this.acceptingBroker = null;
		this.listener = l;
	}
	
	Channel join(Broker b, int port) {
		
		this.acceptingBroker = b;
		
		Channel left  = new Channel(port);
		Channel right = new Channel(port);
		
		left.plug(right);
		
		this.listener.accepted(left);
		
		return right;
	}
	
	boolean met() {
		
		return (this.acceptingBroker != null) && (this.connectingBroker != null);
	}
}
