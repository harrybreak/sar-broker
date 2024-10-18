package mixed;

/**
 * Accepting worker that implements Runnable
 * 
 * This is created when a binding request is provided from the server.
 * The bind function creates a new thread that loops ever accepting calls
 * and stop when a unbind is requested by the server.
 */
public class AcceptorWorker implements Runnable {
	
	boolean shallRun;
	QueueBroker.AcceptListener listener;
	Broker brokerRef;
	int port;
	
	AcceptorWorker(QueueBroker.AcceptListener l, Broker bRef, int p) {
		
		this.shallRun = true;
		
		this.listener = l;
		this.brokerRef = bRef;
		this.port = p;
	}

	@Override
	public void run() {
		
		while (this.shallRun) {
			
			Channel c = this.brokerRef.accept(port);
			
			if (this.shallRun)
				EventPump.inst().post(new AcceptorEvent(new MessageQueue(c), this.listener));
			
			else
				EventPump.inst().post(new AcceptorEvent(null, this.listener));
		}
	}
	
	void stop() {
		
		this.shallRun = false;
	}
}
