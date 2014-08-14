package me.tracker.boot;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;



import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;

public class ServerLifeCycle {
	
	@Log
	Logger logger;

	@Resource
	Server server;

	@PostConstruct
	public void start() {
		try {
			server.start();		
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@PreDestroy
	public void stop() {
		try {
			server.stop();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
