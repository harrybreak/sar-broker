package event;

public class QueueBroker {

	Broker brokerRef;
	
	public QueueBroker(Broker b) {
		
		this.brokerRef = b;
	}
	
	public QueueBroker(String name) {
		
		this.brokerRef = new Broker(name);
	}
	
	public interface AcceptListener {
		
		public void accepted(MessageQueue mq);
	}
	
	public void bind(QueueBroker.AcceptListener l, int port) {
		
		class AListener implements Broker.AcceptListener {

			@Override
			public void accepted(Channel c) {

				l.accepted(new MessageQueue(c));
			}
		}
		
		this.brokerRef.bind(new AListener(), port);
	}
	
	public interface ConnectListener {
		
		public void connected(MessageQueue mq);
		public void refused();
	}
	
	public void connect(QueueBroker.ConnectListener l, String name, int port) {
		
		class CListener implements Broker.ConnectListener {
			
			@Override
			public void connected(Channel c) {
				
				l.connected(new MessageQueue(c));
			}
			
			@Override
			public void refused() {
				
				l.refused();
			}
		}
		
		this.brokerRef.connect(new CListener(), name, port);
	}
}
