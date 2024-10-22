package event;

public class TestEchoServer implements Runnable {
	
	Broker b;
	
	class AListener implements Broker.AcceptListener {
		@Override
		public void accepted(Channel c) {
			
			System.out.println("[INFO][SERVER] Channel well received");
			
			b.unbind(TestEchoMain.PORT);
			
			class ServerListener implements Channel.RWListener {
				@Override
				public void received(byte[] msg) {
					
					System.out.println("[INFO][SERVER] Message received:");
					for (byte a: msg)
						System.out.printf("%c", a);
					System.out.printf("\n");
				}
				@Override
				public void sent(int number) {

					System.out.println("[INFO][SERVER] Successfully sent " + number + " bytes");
				}
				@Override
				public void closed() {

					System.out.println("[INFO][SERVER] Connection closed");
				}
			}
			
			c.setListener(new ServerListener());
			
			c.send(TestEchoMain.message0, 3, 3150);
		}
	}
	
	AListener l;
	
	public TestEchoServer() {

		this.b = new Broker(TestEchoMain.SERVER_NAME);
		this.l = new AListener();
	}
	
	@Override
	public void run() {
		
		this.b.bind(this.l, TestEchoMain.PORT);
	}
}
