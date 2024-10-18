package threaded;

public class QueueBroker {
	
	Broker b;
	
	public QueueBroker(String name) {
		
		this.b = new Broker(name);
	}
	
	public QueueBroker(Broker broker) {
		
		this.b = broker;
	}
	
	public String name() {
		
		return this.b.getName();
	}
	
	public MessageQueue accept(int port) {
		
		Channel c = this.b.accept(port);
		
		return new MessageQueue(c);
	}
	
	public MessageQueue connect(String name, int port) {
		
		Channel c = this.b.connect(name, port);
		
		return new MessageQueue(c);
	}
}
