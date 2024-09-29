package sar;

public class QueueBroker {
	
	Broker b;
	
	public QueueBroker(Broker broker) { this.b = broker; }
	
	public String name() { return this.b.getName(); }
	
	public MessageQueue accept(int port) { return null; }
	
	public MessageQueue connect(String name, int port) { return null; }

}
