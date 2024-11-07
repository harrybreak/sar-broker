package event;

public class TestClient implements Runnable {
	
	boolean useQueueBrokerInstead;
	Broker b;
	QueueBroker qb;
	
	class CListener implements Broker.ConnectListener {
		@Override
		public void connected(Channel c) {
			
			System.out.println("[INFO][CLIENT] Channel well received");
			
			class ClientListener implements Channel.RWListener {
				@Override
				public void received(byte[] msg) {
					
					System.out.println("[INFO][CLIENT] Message received of length " + msg.length);
				}
				@Override
				public void sent(byte[] frame) {

					System.out.println("[INFO][CLIENT] Successfully sent " + frame.length + " bytes");
				}
				@Override
				public void closed() {

					System.out.println("[INFO][CLIENT] Connection closed");
				}
			}
			
			c.setListener(new ClientListener());
			
			c.send(TestMain.message1);
		}

		@Override
		public void refused() {
			
			System.out.println("[ERROR][CLIENT] The reception of the channel has been refused...");
		}
	}
	
	CListener l;
	
	class QCListener implements QueueBroker.ConnectListener {

		@Override
		public void connected(MessageQueue mq) {
			
			System.out.println("[INFO][CLIENT] Message queue well received");
			
			class ClientListener implements MessageQueue.RWListener {
				
				@Override
				public void received(byte[] msg) {
					
					System.out.println("[INFO][CLIENT] Message received of length " + msg.length);
				}
				
				@Override
				public void sent(byte[] frame) {

					System.out.println("[INFO][CLIENT] Successfully sent " + frame.length + " bytes");
				}
				
				@Override
				public void closed() {

					System.out.println("[INFO][CLIENT] Connection closed");
				}
			}
			
			mq.setListener(new ClientListener());
			
			mq.send(TestMain.message1);
		}

		@Override
		public void refused() {
			
			System.out.println("[ERROR][CLIENT] The reception of the message queue has been refused...");
		}
	}
	
	QCListener ql;
	
	public TestClient(boolean useQueueBrokerInstead) {

		this.b = new Broker(TestMain.CLIENT_NAME);
		this.qb = new QueueBroker(this.b);
		
		this.l = new CListener();
		this.ql = new QCListener();
		
		this.useQueueBrokerInstead = useQueueBrokerInstead;
	}
	
	@Override
	public void run() {

		if (this.useQueueBrokerInstead)
			this.qb.connect(this.ql, TestMain.SERVER_NAME, TestMain.PORT);
			
		else
			this.b.connect(this.l, TestMain.SERVER_NAME, TestMain.PORT);
	}
}
