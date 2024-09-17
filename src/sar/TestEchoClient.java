package sar;

public class TestEchoClient {
	
	public final static int N = 256;
	public final static int PORT = 80;
	
    public static void main(String args[]) {
    	
    	Broker clientBroker = new Broker("client");
        
    	while (true) {
    		
    		byte data_sent[] = new byte[N];
    		byte data_received[] = new byte[N];
    		
    		for (int i = 0 ; i < N ; i += 1)
    			data_sent[i] = (byte)i;
    		
    		Channel c = clientBroker.connect("server", PORT);
    		
    		assert c != null : "Client broker cannot return channel";
    		
    		int sent = c.write(data_sent, 0, N);
    		
    		assert sent == N : "Client-side writing failed, data sent:"+sent;
    		
    		int received = c.read(data_received, 0, N);
    		
    		assert received == N : "Client-side reading failed, data read:"+received;
    		
    		for (int i = 0 ; i < N ; i += 1)
    			assert data_sent[i] == data_received[i] : "Data sent is not equal to data received at "+i;
    	}
    	
    }
}
