package sar;

public class TestEchoServer {
	
	public final static int N = 256;
	public final static int PORT = 80;
    // @SuppressWarnings("unused")
    public static void main(String args[]) {
        
    	Broker serverBroker = new Broker("server");
    	
    	while (true) {
    		
    		Channel c = serverBroker.accept(PORT);
    		assert c != null : "ServerBroker cannot return channel";
    		
    		byte data_echoed[] = new byte[N];
    		
    		int data_read = c.read(data_echoed, 0, N);
    		
    		assert data_read == N : "Server-side reading failed, data read:"+data_read;
    		
    		int data_back = c.write(data_echoed, 0, N);
    		
    		assert data_back == N : "Server-side writing failed, data sent:"+data_back;
    	}
    }
}
