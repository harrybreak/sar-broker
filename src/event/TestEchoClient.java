package event;

public class TestEchoClient implements Runnable {
	
	Broker b;
	
	class CListener implements Broker.ConnectListener {
		@Override
		public void connected(Channel c) {
			
			System.out.println("I well received the client-side channel!");
		}

		@Override
		public void refused() {
			
			System.out.println("The reception of the client-side channel has been refused...");
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
