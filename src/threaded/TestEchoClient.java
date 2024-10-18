package threaded;

public class TestEchoClient extends Task {
	
	static class Execution implements Runnable {
		
		public Execution() {}
		

		@Override
		public void run() throws DisconnectChannelException {

			Broker b = Task.getBroker();
			Channel c = b.connect("Server-Side", TestMain.PORT);
			
			byte data_sent[] = {1,4,9,16,25,36};
			
			c.write(data_sent, 1, 5);

			System.out.println("Data successfully sent!");
		}
	}
	

	public TestEchoClient(Broker b, Runnable r) { super(b, r); }


	public static void main(String args[]) throws InterruptedException {
        
		Broker broker = new Broker("Client-Side");
		TestEchoClient client = new TestEchoClient(broker, new Execution());
		
		client.start();
		client.join();
    }
}
