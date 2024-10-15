package sar;

/**
 * Connecting worker that implements Runnable
 * 
 * This is created when a connect request is provided from the client.
 * The connect function creates a new thread that attempts to connect to
 * a remote broker.
 */
public class ConnectorWorker implements Runnable {
	
	QueueBroker.ConnectListener listener;
	Broker brokerRef;
	int port;
	String remoteBrokerName;
	
	ConnectorWorker(QueueBroker.ConnectListener l, Broker bRef, String n, int p) {
		
		this.listener = l;
		this.brokerRef = bRef;
		this.remoteBrokerName = n;
		this.port = p;
	}

	@Override
	public void run() {
		
		Channel c = this.brokerRef.connect(this.remoteBrokerName, this.port);
		
		EventPump.inst().post(new ConnectorEvent(new MessageQueue(c), this.listener));
	}
}
