package sar;

import java.util.HashMap;

public class QueueBroker {
	
	Broker b;

	HashMap<Integer, AcceptorThread> bindingThreads;
	HashMap<Integer, ConnectorThread> connectingThreads;
	
	public QueueBroker(String name) {
		
		this.b = new Broker(name);
		
		this.bindingThreads = new HashMap<>();
		this.connectingThreads = new HashMap<>();
	}
	
	public String getName() {
		
		return this.b.getName();
	}
	
	public interface AcceptListener {
		//@Override
		void accepted(MessageQueue queue);
	};
	
	public boolean bind(int port, AcceptListener listener) {
		
		this.bindingThreads.put(port, new AcceptorThread(new AcceptorWorker(listener, this.b, port)));
		
		return true;
	}
	
	public boolean unbind(int port) {
		
		this.bindingThreads.get(port).requestStop();
		
		return true;
	}
	
	public interface ConnectListener {
		
		void connected(MessageQueue queue);
		void refused();
	};
	
	public boolean connect(String name, int port, ConnectListener listener) {
		
		this.connectingThreads.put(port, new ConnectorThread(new ConnectorWorker(listener, this.b, name, port)));
		
		return true;
	}
}
