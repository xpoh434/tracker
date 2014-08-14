package me.tracker.config;
import static me.tracker.boot.ConfigConst.*;
import static org.quartz.CronScheduleBuilder.*;

import java.util.Properties;
import java.util.TimeZone;

import me.tracker.boot.Password;
import me.tracker.boot.PropertyConfig;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.quartz.Calendar;
import org.quartz.CronScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.calendar.HolidayCalendar;
import org.quartz.impl.jdbcjobstore.JobStoreTX;
import org.quartz.impl.jdbcjobstore.StdJDBCDelegate;
import org.quartz.simpl.SimpleJobFactory;
import org.quartz.spi.JobFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("prod")
@Import(PropertyConfig.class)
public class ProdConfig implements InfraConfig {
    
	private static final String ASIA_HONG_KONG = "Asia/Hong_Kong";
	@Value($_ME_TRACKER_CONFIG_JDBC_URL)
    private String jdbcUrl;
	@Value($_ME_TRACKER_CONFIG_JDBC_USER)
	private String jdbcUser;
	@Value($_ME_TRACKER_CONFIG_JDBC_PASSWORD)
	private Password jdbcPassword;

	final static public Logger logger = LoggerFactory.getLogger(ProdConfig.class); 
	@Bean
    public JobFactory jobFactory() {
    	return new SimpleJobFactory();
    }
    
    
    @Bean
    public Scheduler scheduler() throws SchedulerException {
    	StdSchedulerFactory sf = new StdSchedulerFactory();
		  Properties props = new Properties();
		  props.setProperty("org.quartz.scheduler.skipUpdateCheck", "true");
		  props.setProperty("org.quartz.threadPool.threadCount", "4");
		  props.setProperty("org.quartz.jobStore.class", JobStoreTX.class.getName());
		  props.setProperty("org.quartz.jobStore.driverDelegateClass", StdJDBCDelegate.class.getName());
		  props.setProperty("org.quartz.jobStore.tablePrefix", "QRTZ_");
		  props.setProperty("org.quartz.jobStore.dataSource", "myDS");
		  props.setProperty("org.quartz.jobStore.useProperties", "true");
		  props.setProperty("org.quartz.dataSource.myDS.driver", "com.mysql.jdbc.Driver");
		  props.setProperty("org.quartz.dataSource.myDS.URL", jdbcUrl);
		  props.setProperty("org.quartz.dataSource.myDS.user", jdbcUser);
		  props.setProperty("org.quartz.dataSource.myDS.password", jdbcPassword.toString());
		  sf.initialize(props);
		  
		  Scheduler scheduler = sf.getScheduler();
		  Calendar cal = scheduler.getCalendar("hkHoliday");
		  if(cal == null) {
			  logger.info("creating calendar: hkHoliday");
			  HolidayCalendar c = new HolidayCalendar();
			  c.setTimeZone(TimeZone.getTimeZone(ASIA_HONG_KONG));
			  c.addExcludedDate(new DateTime(2013,1,1,0,0,DateTimeZone.forID(ASIA_HONG_KONG)).toDate());
			  c.addExcludedDate(new DateTime(2013,2,11,0,0,DateTimeZone.forID(ASIA_HONG_KONG)).toDate());
			  c.addExcludedDate(new DateTime(2013,2,12,0,0,DateTimeZone.forID(ASIA_HONG_KONG)).toDate());
			  c.addExcludedDate(new DateTime(2013,2,13,0,0,DateTimeZone.forID(ASIA_HONG_KONG)).toDate());
			  c.addExcludedDate(new DateTime(2013,3,29,0,0,DateTimeZone.forID(ASIA_HONG_KONG)).toDate());
			  c.addExcludedDate(new DateTime(2013,4,1,0,0,DateTimeZone.forID(ASIA_HONG_KONG)).toDate());
			  c.addExcludedDate(new DateTime(2013,4,4,0,0,DateTimeZone.forID(ASIA_HONG_KONG)).toDate());
			  c.addExcludedDate(new DateTime(2013,5,1,0,0,DateTimeZone.forID(ASIA_HONG_KONG)).toDate());
			  c.addExcludedDate(new DateTime(2013,5,17,0,0,DateTimeZone.forID(ASIA_HONG_KONG)).toDate());
			  c.addExcludedDate(new DateTime(2013,6,12,0,0,DateTimeZone.forID(ASIA_HONG_KONG)).toDate());
			  c.addExcludedDate(new DateTime(2013,7,1,0,0,DateTimeZone.forID(ASIA_HONG_KONG)).toDate());
			  c.addExcludedDate(new DateTime(2013,9,20,0,0,DateTimeZone.forID(ASIA_HONG_KONG)).toDate());
			  c.addExcludedDate(new DateTime(2013,10,1,0,0,DateTimeZone.forID(ASIA_HONG_KONG)).toDate());
			  c.addExcludedDate(new DateTime(2013,10,14,0,0,DateTimeZone.forID(ASIA_HONG_KONG)).toDate());
			  c.addExcludedDate(new DateTime(2013,12,25,0,0,DateTimeZone.forID(ASIA_HONG_KONG)).toDate());
			  c.addExcludedDate(new DateTime(2013,12,26,0,0,DateTimeZone.forID(ASIA_HONG_KONG)).toDate());
			  scheduler.addCalendar("hkHoliday", c, true, true);
			  
		  }
		  scheduler.setJobFactory(springJobFactory());
		  return scheduler;
    }
       
    @Bean
    public CronScheduleBuilder[] cronSchedules() {
    	
    	return new CronScheduleBuilder[] {
    			cronSchedule("0 * 10-12,13-16 ? * MON-FRI")
	    		.withMisfireHandlingInstructionFireAndProceed()
	    		.inTimeZone(TimeZone.getTimeZone(ASIA_HONG_KONG)), 
	    		cronSchedule("0 30-59 9 ? * MON-FRI")
		    	.withMisfireHandlingInstructionFireAndProceed()
		    	.inTimeZone(TimeZone.getTimeZone(ASIA_HONG_KONG))
	    		};
    }
    
    @Bean
    public CronScheduleBuilder[] testSchedules() {
    	return new CronScheduleBuilder[] {cronSchedule("0/5 * * * * ?")
		.withMisfireHandlingInstructionFireAndProceed()}; 
    	
    }

    @Bean
    public SpringJobFactory springJobFactory() {
    	return new SpringJobFactory();
    }
}

