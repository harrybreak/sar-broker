package sar;

import java.util.HashMap;

public class QueueBroker {
	
	class Mario implements Runnable {
		
		boolean shallRun;
		
		Mario() {
			
			this.shallRun = true;
		}

		@Override
		public void run() {}
		
		void stop() {
			
			this.shallRun = false;
		}
	}
	
	class Bowser extends Thread {

		Mario r;
		
		Bowser(Mario r) {

			super(r);
			this.r = r;
		}
		
		void requestStop() {
			
			this.r.stop();
		}
	}
	
	Broker b;

	HashMap<Integer, Bowser> bindingThreads;
	HashMap<Integer, Bowser> connectingThreads;
	
	public QueueBroker(String name) {
		
		this.b = new Broker(name);
		
		this.bindingThreads = new HashMap<Integer, Bowser>();
		this.connectingThreads = new HashMap<Integer, Bowser>();
	}
	
	public String getName() {
		
		return this.b.getName();
	}
	
	public interface AcceptListener {
		//@Override
		void accepted(MessageQueue queue);
	};
	
	public boolean bind(int port, AcceptListener listener) {
		
		this.bindingThreads.put(port, new Bowser(new Mario() {
			@Override
			public void run() {
				
				Channel c = b.accept(port);
				if (shallRun)
					listener.accepted(new MessageQueue(c));
			}
		}));
		
		this.bindingThreads.get(port).start();
		
		return true;
	}
	
	public boolean unbind(int port) {
		
		this.bindingThreads.get(port).requestStop();
		
		return true;
	}
	
	public interface ConnectListener {
		
		void connected(MessageQueue queue);
		void refused();
	};
	
	public boolean connect(String name, int port, ConnectListener listener) {
		
		this.connectingThreads.put(port, new Bowser(new Mario() {
			@Override
			public void run() {
				
				try {
					Channel c = b.connect(name, port);
					listener.connected(new MessageQueue(c));
				} catch (IllegalStateException e) {
					listener.refused();
				}
			}
		}));
		
		this.connectingThreads.get(port).start();
		
		return true;
	}
}