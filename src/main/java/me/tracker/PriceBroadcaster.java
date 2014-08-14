package me.tracker;

import me.tracker.boot.Log;
import me.tracker.web.PriceMessage;

import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.slf4j.Logger;

public class PriceBroadcaster {
	@Log
	Logger logger;
	
	public void broadcast(PriceMessage price) {
		if(BroadcasterFactory.getDefault()!=null) {
			Broadcaster b = BroadcasterFactory.getDefault().lookup("update");
			if(b !=null) {
				b.broadcast(price);
				logger.info("try to broadcast: " + price);
			}
			else
				logger.warn("broadcaster not found");
		}
		else
			logger.warn("broadcaster factory not found");
	}
}
