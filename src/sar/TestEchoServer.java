package sar;

public class TestEchoServer extends Task {
	
	static class Execution implements Runnable {
		
		public Execution() {}
		

		@Override
		public void run() throws DisconnectChannelException {

			Broker b = Task.getBroker();
			Channel c = b.accept(TestMain.PORT);
			
			byte data_received[] = {0,0,0,0,0,0};
			
			c.read(data_received, 0, 5);

			System.out.println("Data received:");
            for (int i = 0 ; i < 6 ; i++)
           	 	System.out.printf("|%d|\n", data_received[i]);
		}
	}
	

	public TestEchoServer(Broker b, Runnable r) { super(b, r); }


	public static void main(String args[]) throws InterruptedException {
        
		Broker broker = new Broker("Server-Side");
		TestEchoServer server = new TestEchoServer(broker, new Execution());
		
		server.start();
		server.join();
    }
}
