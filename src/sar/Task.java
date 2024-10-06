package sar;

public class Task extends Thread {
	
	QueueBroker qbRef;
    Broker brokerRef;
    
    Runnable exeCore;
    
    @Override
    public void run() { this.exeCore.run(); }
    
    public Task(Broker b, Runnable r) {
    	
        this.brokerRef = b;
        this.qbRef = null;
        
        this.exeCore = r;
    }
    
    public Task(QueueBroker qb, Runnable r) {
    	
    	this.brokerRef = qb.b;
    	this.qbRef = qb;
    	
    	this.exeCore = r;
    }
    
    synchronized public static Task getTask() {
    	
    	return (Task)Thread.currentThread();
    }

    synchronized public static Broker getBroker() {
    	
        return ((Task)(Thread.currentThread())).brokerRef;
    }
    
    synchronized public static QueueBroker getQueueBroker() {
    	
    	return ((Task)(Thread.currentThread())).qbRef;
    }
}
