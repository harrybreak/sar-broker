package sar;

public class QueueBroker {
	
	Broker b;
	String name;
	
	public QueueBroker(String name) {
		this.name = name;
	}
	
	public interface AcceptListener {
		void accepted(MessageQueue queue);
	};
	
	public boolean bind(int port, AcceptListener listener) {
		return false;
	}
	
	public boolean unbind(int port) {
		return false;
	}
	
	public interface ConnectListener {
		void connected(MessageQueue queue);
		void refused();
	};
	
	public boolean connect(String name, int port, ConnectListener listener) {
		return false;
	}
}