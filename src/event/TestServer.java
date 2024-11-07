package event;

public class TestServer implements Runnable {
	
	Broker b;
	QueueBroker qb;
	boolean useQueueBrokerInstead;
	
	class AListener implements Broker.AcceptListener {
		@Override
		public void accepted(Channel c) {
			
			System.out.println("[INFO][SERVER] Channel well received");
			
			b.unbind(TestMain.PORT);
			
			class ServerListener implements Channel.RWListener {
				
				@Override
				public void received(byte[] msg) {
					
					System.out.println("[INFO][SERVER] Message received:");
					for (byte a: msg)
						System.out.printf("%c", a);
					System.out.printf("\n");
				}
				
				@Override
				public void sent(byte[] frame) {

					System.out.println("[INFO][SERVER] Successfully sent " + frame.length + " bytes");
				}
				
				@Override
				public void closed() {

					System.out.println("[INFO][SERVER] Connection closed");
				}
			}
			
			c.setListener(new ServerListener());

			c.send(TestMain.message0, 3, 3150);
		}
	}
	
	AListener l;
	
	class QAListener implements QueueBroker.AcceptListener {

		@Override
		public void accepted(MessageQueue mq) {
			
			System.out.println("[INFO][SERVER] Message queue well received");
			
			b.unbind(TestMain.PORT);
			
			class ServerListener implements MessageQueue.RWListener {
				
				@Override
				public void received(byte[] msg) {
					
					System.out.println("[INFO][SERVER] Message received:");
					for (byte a: msg)
						System.out.printf("%c", a);
					System.out.printf("\n");
				}
				
				@Override
				public void sent(byte[] frame) {

					System.out.println("[INFO][SERVER] Successfully sent " + frame.length + " bytes");
				}
				
				@Override
				public void closed() {

					System.out.println("[INFO][SERVER] Connection closed");
				}
			}
			
			mq.setListener(new ServerListener());

			mq.send(TestMain.message0);
		}
	}
	
	QAListener ql;
	
	public TestServer(boolean useQueueBrokerInstead) {

		this.b = new Broker(TestMain.SERVER_NAME);
		this.qb = new QueueBroker(this.b);
		
		this.l = new AListener();
		this.ql = new QAListener();
		
		this.useQueueBrokerInstead = useQueueBrokerInstead;
	}
	
	@Override
	public void run() {
		
		if (this.useQueueBrokerInstead)
			this.qb.bind(this.ql, TestMain.PORT);
		
		else
			this.b.bind(this.l, TestMain.PORT);
	}
}
