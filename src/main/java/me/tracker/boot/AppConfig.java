package me.tracker.boot;

import java.net.URL;
import java.security.ProtectionDomain;

import me.Main;

import org.atmosphere.cpr.ApplicationConfig;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import ch.qos.logback.access.jetty.RequestLogImpl;

@Configuration
@Import(PropertyConfig.class)
public class AppConfig {
	@Value("${me.tracker.boot.AppConfig.keystorePass}")
	Password keystorePass;
	@Value("${me.tracker.boot.AppConfig.httpsPort}")
	int httpsPort;
	@Value("${me.tracker.boot.AppConfig.httpPort}")
	int httpPort;

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	public App app() {
		return new App();
	}

	@Bean
	public RequestLogHandler requestLogHandler() {
		RequestLogHandler requestLogHandler = new RequestLogHandler();
		RequestLogImpl log = new ch.qos.logback.access.jetty.RequestLogImpl();
		log.setResource("/logback-access.xml");
		requestLogHandler.setRequestLog(log);
		return requestLogHandler;
	}

	@Bean
	public Server server() {
		Server server = new Server(httpPort);
		
		HandlerCollection handlers = new HandlerCollection();
		ContextHandlerCollection contexts = new ContextHandlerCollection();
		handlers.setHandlers(new Handler[]{contexts,new DefaultHandler(),requestLogHandler()});
		server.setHandler(handlers);
		
		contexts.addHandler(web());	
		
		//server.setHandler(web());
		SslContextFactory factory = new SslContextFactory();
		factory.setKeyStorePath("keystore");
		factory.setKeyStorePassword(keystorePass.toString());
		factory.setKeyManagerPassword(keystorePass.toString());
		factory.setTrustStore("keystore");
		factory.setTrustStorePassword(keystorePass.toString());

		// jetty 9

		// // HTTP Configuration
		// HttpConfiguration http_config = new HttpConfiguration();
		// // http_config.setSecureScheme("https");
		// http_config.setSecurePort(8443);
		// // http_config.setOutputBufferSize(32768);
		// // http_config.setRequestHeaderSize(8192);
		// // http_config.setResponseHeaderSize(8192);
		// // http_config.setSendServerVersion(true);
		// // http_config.setSendDateHeader(false);
		//
		// HttpConfiguration https_config = new HttpConfiguration(http_config);
		// https_config.addCustomizer(new SecureRequestCustomizer());
		//
		// ServerConnector connector = new ServerConnector(server,
		// new SslConnectionFactory(factory, HttpVersion.HTTP_1_1.asString()),
		// new HttpConnectionFactory(https_config));

		SslSelectChannelConnector connector = new SslSelectChannelConnector(
				factory);
		connector.setPort(httpsPort);
		connector.setMaxIdleTime(30000);
		server.addConnector(connector);

		return server;
	}

	@Bean
	public Handler web() {
		WebAppContext webapp = new WebAppContext();

		ProtectionDomain protectionDomain = Main.class.getProtectionDomain();
		URL location = protectionDomain.getCodeSource().getLocation();
		webapp.setWar(location.toExternalForm());
		webapp.setContextPath("/");
		webapp.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed",
				"false");
		webapp.setInitParameter(
				"org.eclipse.jetty.servlet.Default.useFileMappedBuffer",
				"false");
		webapp.setInitParameter("spring.profiles.active", "prod");
		webapp.setInitParameter("contextClass",
				AnnotationConfigWebApplicationContext.class.getName());
		webapp.setInitParameter("contextConfigLocation",
				"me.tracker.config.WebAppConfig");

		webapp.addEventListener(new ContextLoaderListener());
		webapp.addEventListener(new RequestContextListener());

		ServletHolder servlet = webapp.addServlet(
				org.eclipse.jetty.servlet.DefaultServlet.class, "/*");
		servlet.setInitOrder(0);
		servlet.setInitParameter("useFileMappedBuffer", "false");
		servlet.setInitParameter("dirAllowed", "false");

		servlet = webapp.addServlet(org.atmosphere.cpr.AtmosphereServlet.class,
				"/as/*");
		servlet.setInitOrder(0);
		servlet.setInitParameter("com.sun.jersey.config.property.packages",
				"me.tracker.web");
		// servlet.setInitParameter("org.atmosphere.cpr.atmosphereHandlerPath",
		// "/me/tracker/web/");
		servlet.setInitParameter(ApplicationConfig.WEBSOCKET_CONTENT_TYPE,
				"application/json");
		servlet.setInitParameter(
				ApplicationConfig.BROADCASTER_MESSAGE_PROCESSING_THREADPOOL_MAXSIZE,
				"10");
		servlet.setInitParameter(
				ApplicationConfig.BROADCASTER_ASYNC_WRITE_THREADPOOL_MAXSIZE,
				"10");
		servlet.setInitParameter(ApplicationConfig.OUT_OF_ORDER_BROADCAST,
				"true");
		servlet.setAsyncSupported(true);

		Constraint constraint = new Constraint();
		constraint.setName(Constraint.__BASIC_AUTH);
		constraint.setRoles(new String[] { "user" });
		constraint.setAuthenticate(true);

		Constraint free = new Constraint();
		free.setAuthenticate(false);

		ConstraintMapping cm = new ConstraintMapping();
		cm.setConstraint(constraint);
		cm.setPathSpec("/*");

		ConstraintMapping freeCm = new ConstraintMapping();
		freeCm.setConstraint(free);
		freeCm.setPathSpec("/as/*");

		ConstraintSecurityHandler sh = new ConstraintSecurityHandler();
		sh.setConstraintMappings(new ConstraintMapping[] { cm, freeCm });

		HashLoginService loginService = new HashLoginService();
		loginService.setName("MyRealm");
		loginService.setConfig("file:realm.properties");
		loginService.setRefreshInterval(0);

		sh.setLoginService(loginService);

		webapp.setSecurityHandler(sh);

		return webapp;
	}

	@Bean
	public ServerLifeCycle serverLifeCycle() {
		return new ServerLifeCycle();
	}
	
}
