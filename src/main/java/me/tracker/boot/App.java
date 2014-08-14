package me.tracker.boot;



import org.slf4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Hello world!
 * 
 */
public class App {
	
	@Log
	Logger logger;
	
	public static void main(String[] args) throws Exception {
		  AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		  ctx.scan("me.tracker");
		  ctx.refresh();
		  ctx.registerShutdownHook();	
		  
		  ctx.getBean(App.class).start();
	}
	
	public void start() {
		logger.info("started");
	}
}
