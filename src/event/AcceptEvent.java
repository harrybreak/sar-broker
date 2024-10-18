package event;

public class AcceptEvent implements Runnable {
	
	boolean shallRun;
	RdV rendezvous;
	int port;
	Broker broker;
	Broker.AcceptListener listener;
	
	AcceptEvent(Broker b, Broker.AcceptListener l, int port) {
		
		this.shallRun = true;
		this.listener = l;
		this.port = port;
		this.broker = b;
		
		this.rendezvous = new RdV(this.broker, this.listener, this.port);
	}
	
	void requestStop() {
		
		this.shallRun = false;
	}
	
	public RdV getRdV() {
		
		return this.rendezvous;
	}

	@Override
	public void run() {
		
		if (this.rendezvous.met())
			this.rendezvous = new RdV(this.broker, this.listener, this.port);
		
		if (this.shallRun)
			EventPump.inst().post(this);
		
		else
			System.out.println("Port " + this.port + " is no longer listening!");
	}
}
