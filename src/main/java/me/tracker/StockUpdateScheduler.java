package me.tracker;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;

import java.net.URL;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Inject;

import me.tracker.boot.Log;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;

public class StockUpdateScheduler {
	@Resource
	Scheduler scheduler;

	@Resource(name="cronSchedules")
	CronScheduleBuilder[] cronSchedules;

	@Resource(name="testSchedules")
	CronScheduleBuilder[] testSchedules;

	@Inject
	WebScaper webScaper;

	@Log
	Logger logger;

	public Response schedule(String stockCode) {

		try {
			boolean exist = false;
			for(int i = 0;i < cronSchedules.length;i++) {
				JobKey key = new JobKey(stockCode+ "_" +i);
				if(scheduler.checkExists(key)) {
					scheduler.deleteJob(key);
					exist = true;
				}
			}
			if(exist) {
				return Response.removedResponse();
			} else {
				
				boolean valid = validate(stockCode);

				if (valid) {
					CronScheduleBuilder[] schedules = null;
					if(stockCode.startsWith("TEST")) {
						schedules = testSchedules;
					} else {
						schedules = cronSchedules;
					}
					for(int i = 0; i< schedules.length; i++) {
						JobDetail job = newJob(PriceUpdateJob.class)
								.withIdentity(stockCode + "_" + i)
								.usingJobData("stockCode", stockCode)
								.build();
								
						TriggerBuilder<CronTrigger> t = newTrigger()
				    		    .withIdentity(stockCode + "_" + i)
				    		    .startNow()
				    		    .withSchedule(schedules[i]);
				    	if(!stockCode.startsWith("TEST")) {
				    			t = t.modifiedByCalendar("hkHoliday");
				    	}
				    	Trigger trg = t.build();

						scheduler.scheduleJob(job, trg);
					}
					return Response.addedResponse();
				} else {
					return Response.invalidResponse();
				}
			}
		} catch (SchedulerException e) {
			logger.error("error occurred", e);
			return Response.errorResponse(e.getMessage());
		}
	}

	boolean validate(String stockCode) {
		if(stockCode.startsWith("TEST")) return true;
		try {
			Pattern title = Pattern.compile("<title>" + stockCode);
			String[][] results = webScaper.get(new URL(PriceUpdateJob.baseURL + stockCode), new Pattern[]{title});
			if(results[0] == null) {
				logger.warn("invalid stock code: " + stockCode);
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.warn("validation of " + stockCode + " failed.",e);
			return false;
		}
	}

	public static class Response {
		public String message;
		public boolean added;
		public boolean error;

		public static Response errorResponse(String msg) {
			Response resp = new Response();
			resp.message = msg;
			resp.error = true;
			return resp;
		}

		public static Response invalidResponse() {
			Response resp = new Response();
			resp.message = "invalid stock code";
			resp.added = false;
			return resp;
		}

		public static Response addedResponse() {
			Response resp = new Response();
			resp.message = "scheduled";
			resp.added = true;
			return resp;
		}

		public static Response removedResponse() {
			Response resp = new Response();
			resp.message = "unscheduled";
			return resp;
		}

	}
}
