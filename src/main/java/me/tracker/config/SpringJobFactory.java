package me.tracker.config;

import javax.annotation.Resource;

import me.tracker.boot.Log;

import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.slf4j.Logger;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

public class SpringJobFactory implements JobFactory {
	
	@Log
	Logger logger;
	
	@Resource(name="jobFactory")
	JobFactory jobFactory;

	@Override
	public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler)
			throws SchedulerException {
		
		Class<? extends Job> jobClass = bundle.getJobDetail().getJobClass();
        logger.trace("lookup({})", jobClass);
		      
        WebApplicationContext cc = ContextLoader.getCurrentWebApplicationContext();
        
        Job job = null;
        if(cc!=null) {
        	job = cc.getBean(jobClass);
        } else {
        	logger.warn("WebApplicationContext not found, no spring bean look up is done, revert to default JobFactory");        	
        }
	
		if(job == null) {
			job = this.jobFactory.newJob(bundle, scheduler);
		}
        
		return job;
	}

}
