package sar;

abstract class QueueBroker {
	
	Broker b;
	String name;
	
	QueueBroker(String name) {
		this.name = name;
	}
	
	interface AcceptListener {
		void accepted(MessageQueue queue);
	};
	
	boolean bind(int port, AcceptListener listener) {
		return false;
	}
	boolean unbind(int port) {
		return false;
	}
	
	interface ConnectListener {
		void connected(MessageQueue queue);
		void refused();
	};
	
	boolean connect(String name, int port, ConnectListener listener) {
		return false;
	}
}