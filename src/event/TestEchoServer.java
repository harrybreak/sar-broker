package event;

public class TestEchoServer implements Runnable {
	
	Broker b;
	
	class AListener implements Broker.AcceptListener {
		@Override
		public void accepted(Channel c) {
			
			System.out.println("I well received the server-side channel!");
			
			b.unbind(TestEchoMain.PORT);
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
