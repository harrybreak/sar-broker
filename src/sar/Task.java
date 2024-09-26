package sar;

public class Task extends Thread {
	
    Broker brokerRef;
    Runnable exeCore;
    
    Task(Broker b, Runnable r) {
        this.brokerRef = b;
        this.exeCore = r;
    }

    synchronized static Broker getBroker() {
        return ((Task)(Thread.currentThread())).brokerRef;
    }
}
