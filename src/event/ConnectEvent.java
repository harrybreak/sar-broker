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
		
		try {

			Broker remote = BrokerManager.getSelf().get(this.name);
			
			RdV r = remote.listening.get(this.port).getRdV();
			
			Channel c = r.join(this.broker, this.port);
			
			this.listener.connected(c);
			
		} catch (NullPointerException e) {
			
			EventPump.inst().post(this);
		}
		
	}
}
