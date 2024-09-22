package sar;

import java.util.HashMap;

public class BrokerManager {

	HashMap<String, Broker> map;
	
	BrokerManager() {
		this.map = new HashMap<String, Broker>();
	}
	
	Broker requestBroker(Broker originBroker, String brokerName) {
		if (!map.containsKey(originBroker.name)) {
			map.put(originBroker.name, originBroker);
		}
		return map.get(brokerName);
	}
}
