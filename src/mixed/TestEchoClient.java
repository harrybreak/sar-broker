package mixed;

public class TestEchoClient extends Task {
	
	static class Execution implements Runnable {
		
		MessageQueue mq;
		
		public Execution() {
			
			this.mq = null;
		}

		@Override
		public void run() throws DisconnectChannelException {

	        class CListener implements QueueBroker.ConnectListener {
				@Override
				public void connected(MessageQueue queue) {
					
					mq = queue;
					
					if (mq != null)
						System.out.println("I well received the client queue!");
					
					else
						System.out.println("I received a null client queue...");
					
			        class RWListener implements MessageQueue.Listener {
						@Override
						public void received(Message msg) {
							
							Message check = new Message(TestMain.data_sent);
							
							if (check.equals(msg)) {
								System.out.println("Test réussi!");
							} else {
								System.out.println("Test échoué...");
							}
						}
						@Override
						public void sent(Message msg) {
						}
						@Override
						public void closed() {
						}        	 
			        }
			        
			        RWListener rwl = new RWListener();
			        
			        mq.setListener(rwl);
				}
				@Override
				public void refused() {
					
					System.out.println("This is life...");
				}
	        }
	        
	        // Create one instance of the connecting listener...
	        CListener l = new CListener();
	        // ... and send it to the QueueBroker
	        Task.getQueueBroker().connect(TestMain.SERVER_NAME, TestMain.PORT, l);
		}
	}
	

	public TestEchoClient(QueueBroker b, Runnable r) { super(b, r); }


	public static void main(String args[]) throws InterruptedException {

		QueueBroker serbroker = new QueueBroker(TestMain.SERVER_NAME);
		TestEchoServer server = new TestEchoServer(serbroker,
				new TestEchoServer.Execution());
		
		QueueBroker clibroker = new QueueBroker(TestMain.CLIENT_NAME);
		TestEchoClient client = new TestEchoClient(clibroker,
				new TestEchoClient.Execution());

		server.start();
		client.start();

		server.join();
		client.join();
    }
}
