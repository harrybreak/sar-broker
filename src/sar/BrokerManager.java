package sar;

import java.util.HashMap;

public class BrokerManager {
	
	private static BrokerManager self;
	
	static BrokerManager getSelf() {
		return self;
	} static { // JVM exclusively executes this code when self is not initialized at "getSelf" first call
		self = new BrokerManager();
	}
	
	HashMap<String, Broker> brokers;
	
	private BrokerManager() {
		brokers = new HashMap<String, Broker>();
	}
	
	public synchronized void add(Broker broker) {
		String name = broker.getName();
		Broker b = brokers.get(name);
		if (b != null) throw new IllegalStateException("Broker " + name + " already exists!");
		brokers.put(name, broker);
	}
	
	public synchronized void remove(Broker broker) {
		String name = broker.getName();
		brokers.remove(name);
	}
	
	public synchronized Broker get(String name) {
		return brokers.get(name);
	}
	
	public int size() {
		return brokers.size();
	}
	
};
