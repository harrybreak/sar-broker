package event;

public class TestEchoClient implements Runnable {
	
	Broker b;
	
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
				public void sent(int number) {

					System.out.println("[INFO][CLIENT] Successfully sent " + number + " bytes");
				}
				@Override
				public void closed() {

					System.out.println("[INFO][CLIENT] Connection closed");
				}
			}
			
			c.setListener(new ClientListener());
			
			c.send(TestEchoMain.message1);
		}

		@Override
		public void refused() {
			
			System.out.println("[ERROR][CLIENT] The reception of the channel has been refused...");
		}
	}
	
	CListener l;
	
	public TestEchoClient() {

		this.b = new Broker(TestEchoMain.CLIENT_NAME);
		this.l = new CListener();
	}
	
	@Override
	public void run() {

		this.b.connect(this.l, TestEchoMain.SERVER_NAME, TestEchoMain.PORT);
	}
}
