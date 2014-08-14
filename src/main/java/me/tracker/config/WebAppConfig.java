package me.tracker.config;

import me.tracker.PriceBroadcaster;
import me.tracker.PriceUpdateJob;
import me.tracker.SchedulerLifeCycle;
import me.tracker.StockContentAnalyzer;
import me.tracker.StockUpdateScheduler;
import me.tracker.WebScaper;
import me.tracker.boot.LoggerBeanPostProcessor;
import me.tracker.web.StockHandler;

import org.quartz.Job;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;

@Configuration
@Import({JPAConfig.class,ProdConfig.class})
@Profile({"prod","test"})
public class WebAppConfig {   
    @Bean
    public Job priceUpdateJob() {
    	return new PriceUpdateJob();
    }
    
    @Bean
    public SchedulerLifeCycle schedulerLifeCycle() {
    	return new SchedulerLifeCycle();
    }

    @Bean
    public StockUpdateScheduler stockUpdateScheduler() {
    	return new StockUpdateScheduler();
    }
    
    @Bean @Scope("prototype")
    public StockHandler stockHandler() {
    	return new StockHandler();
    }
    
    @Bean
    public LoggerBeanPostProcessor loggerBeanPosProcessor() {
    	return new LoggerBeanPostProcessor();
    }
    
    @Bean
    public StockContentAnalyzer stockContentAnalyzer() {
    	return new StockContentAnalyzer();
    }

    @Bean
    public WebScaper webScaper() {
    	return new WebScaper();
    }
    
    @Bean
    public PriceBroadcaster priceBroadcaster() {
    	return new PriceBroadcaster();
    }
}

