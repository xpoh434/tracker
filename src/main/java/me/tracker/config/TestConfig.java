package me.tracker.config;

import static org.mockito.Mockito.*;
import me.tracker.repositories.PriceRepository;

import org.quartz.CronScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class TestConfig implements InfraConfig {
    @Bean
    public Scheduler scheduler() throws SchedulerException {
		  return mock(Scheduler.class);
    }
    
    @Bean
    public CronScheduleBuilder[] cronSchedules() {
    	return new CronScheduleBuilder[0];
    }
    
    @Bean
    public CronScheduleBuilder[] testSchedules() {
    	return new CronScheduleBuilder[0];
    }    
    
	@Bean
    public PriceRepository priceRepository() {
    	return mock(PriceRepository.class);
    }
}

