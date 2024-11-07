package event;

public class ConnectEvent implements Runnable {
	
	int port;
	String name;
	Broker broker;
	Broker.ConnectListener listener;
	
	ConnectEvent(Broker b, Broker.ConnectListener l, String n, int p) {
		
		this.port = p;
		this.listener = l;
		this.name = n;
		this.broker = b;
	}

	@Override
	public void run() {
		
		Broker remote;
		
		try {

			remote = BrokerManager.getSelf().get(this.name);
		
		} catch (NullPointerException e) { // This broker name is not known by the universe
			
			this.listener.refused();
			return;
		}
		
		try {
			
			RdV r = remote.listening.get(this.port).getRdV();
			
			if (r.closed) { // The other side closed this accepting event, so it is refused
				
				this.listener.refused();
				return;
			}
			
			Channel c = r.join(this.broker, this.port);
			
			this.listener.connected(c);
			
		} catch (NullPointerException e) { // Still not created an accepting event from the other side
			
			EventPump.inst().post(this);
		}
	}
}
