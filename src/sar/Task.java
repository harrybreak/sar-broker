package sar;

public class Task extends Thread {
    Broker broker;
    Runnable exec;
    
    Task(Broker b, Runnable r) {
        this.broker = b;
        this.exec = r;
    }

    Broker getBroker() {
        return this.broker;
    }
    
    Channel connect(String name, int port) {
    	return null;
    }
    
    Channel accept(int port) {
    	return null;
    }
}
