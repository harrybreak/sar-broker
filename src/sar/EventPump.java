package sar;

import java.util.ArrayList;

public class EventPump {
	
	ArrayList<Runnable> events;
	
	public EventPump() {
		
		this.events = new ArrayList<Runnable>();
		
		System.out.println("No need to create this for task3 I guess...");
	}

}
