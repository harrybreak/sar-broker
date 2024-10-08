package sar;

public class TestEchoServer extends Task {
	
	static class Execution implements Runnable {
		
		MessageQueue mq;
		
		public Execution() {
			
			this.mq = null;
		}

		@Override
		public void run() throws DisconnectChannelException {

	        class AListener implements QueueBroker.AcceptListener {
				@Override
				public void accepted(MessageQueue queue) {
					
					mq = queue;
					System.out.println("I well received the connected queue");
					
			        class RWListener implements MessageQueue.Listener {
						@Override
						public void received(Message msg) {}
						@Override
						public void sent(Message msg) {
							System.out.println("Message has been sent!");
						}
						@Override
						public void closed() {}        	 
			        }
			         
			        RWListener rwl = new RWListener();
			         
			        mq.setListener(rwl);
			         
			        mq.send(new Message(TestMain.data_sent));
				}
	        }
	        
	        // Create one instance of the accepting listener...
	        AListener l = new AListener();
	        // ... and send it to the QueueBroker
	        Task.getQueueBroker().bind(TestMain.PORT, l);
		}
	}
	

	public TestEchoServer(QueueBroker b, Runnable r) { super(b, r); }

	// Launch the TestEchoClient application instead
	//
	// public static void main(String args[]) throws InterruptedException {}
}
