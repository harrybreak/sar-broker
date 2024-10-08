package sar;

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
					System.out.println("I well received the client queue!");
					
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
				}
	        }
	        
	        // Create one instance of the accepting listener...
	        CListener l = new CListener();
	        // ... and send it to the QueueBroker
	        Task.getQueueBroker().connect("Server-side", TestMain.PORT, l);
		}
	}
	

	public TestEchoClient(QueueBroker b, Runnable r) { super(b, r); }


	public static void main(String args[]) throws InterruptedException {

		QueueBroker serbroker = new QueueBroker("Server-Side");
		TestEchoServer server = new TestEchoServer(serbroker,
				new TestEchoServer.Execution());
		
		QueueBroker clibroker = new QueueBroker("Client-Side");
		TestEchoClient client = new TestEchoClient(clibroker,
				new TestEchoClient.Execution());

		server.start();
		client.start();

		server.join();
		client.join();
    }
}
