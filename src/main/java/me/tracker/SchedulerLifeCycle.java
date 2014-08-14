package me.tracker;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import me.tracker.boot.Log;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;

public class SchedulerLifeCycle {
	
	@Log
	Logger logger;

	@Resource
	Scheduler scheduler;
	
	@PostConstruct
	public void start() {
		try {
			scheduler.start();
			
		
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

	@PreDestroy
	public void stop() {
		try {
			scheduler.shutdown(true);
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

}
