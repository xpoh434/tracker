package me.tracker.config;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;


public interface InfraConfig {
    public Scheduler scheduler() throws SchedulerException;
	
}
