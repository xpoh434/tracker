/*
* Copyright 2012 Jeanfrancois Arcand
*
* Licensed under the Apache License, Version 2.0 (the "License"); you may not
* use this file except in compliance with the License. You may obtain a copy of
* the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations under
* the License.
*/
package me.tracker.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import me.tracker.StockUpdateScheduler;
import me.tracker.web.test.EventsLogger;
import me.tracker.web.test.Message;
import me.tracker.web.test.Response;

import org.atmosphere.annotation.Broadcast;
import org.atmosphere.annotation.Suspend;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.jersey.Broadcastable;

@Path("/stock/{topic}")
public class StockHandler {
	
	@Inject
	private StockUpdateScheduler scheduler;

	@PathParam("topic")
    private Broadcaster topic;
    
    /**
* Suspend the response without writing anything back to the client.
* @return a white space
*/
    @Suspend(contentType = "application/json", listeners={EventsLogger.class})
    @GET
    public Broadcastable suspend() {
        return new Broadcastable(topic);
    }

    /**
* Broadcast the received message object to all suspended response. Do not write back the message to the calling connection.
* @param message a {@link Message}
* @return a {@link Response}
*/
    @Broadcast(writeEntity = false)
    @POST
    @Produces("application/json")
    public StockResponse subscribe(StockMessage message) {
    	List<StockUpdateScheduler.Response> rep  = new ArrayList<StockUpdateScheduler.Response>();
    	String[] stocks;
    	if(message != null && message.stock.trim().length() > 0) {
	    	stocks = message.stock.trim().split("\\s+");
	    	for(String stock : stocks) {
	    		rep.add(scheduler.schedule(stock));
	    	}
    	} else {
    		stocks = new String[] {"{empty}"};
    		rep.add(StockUpdateScheduler.Response.invalidResponse());
    	}
    	String txt = createMessage(stocks,rep);
    	return new StockResponse(message.stock, txt, new Date().getTime());
    }

	private String createMessage(String[] stocks,
			List<me.tracker.StockUpdateScheduler.Response> rep) {
		StringBuilder sb = new StringBuilder();
		int i=0;
		for(StockUpdateScheduler.Response r : rep) {
			sb.append(stocks[i++]).append(":").append(r.message).append("\n");
		}
		return sb.toString();
	}

}