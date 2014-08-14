package me.tracker.config;

import static me.tracker.boot.ConfigConst.*;

import javax.sql.DataSource;

import me.tracker.boot.Password;
import me.tracker.boot.PropertyConfig;

import org.hibernate.dialect.MySQLDialect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import com.mchange.v2.c3p0.ComboPooledDataSource;
@Configuration
@Profile({"jpa","prod"})
@EnableJpaRepositories("me.tracker.repositories")
@Import(PropertyConfig.class)
public class JPAConfig {
	@Value($_ME_TRACKER_CONFIG_JDBC_PASSWORD)
    Password jdbcPassword;
	@Value($_ME_TRACKER_CONFIG_JDBC_USER)
	String jdbcUser;
	@Value($_ME_TRACKER_CONFIG_JDBC_URL)
	String jdbcUrl;
	@Value($_ME_TRACKER_CONFIG_JDBC_ACQUIRE_INCREMENT)
	int jdbcAcquireIncrement;
	@Value($_ME_TRACKER_CONFIG_JDBC_MIN_POOL_SIZE)
	int jdbcMinPoolSize;
	@Value($_ME_TRACKER_CONFIG_JDBC_MAX_POOL_SIZE)
	int jdbcMaxPoolSize;
	@Value($_ME_TRACKER_CONFIG_JDBC_MAX_IDLE_TIME)
	int jdbcMaxIdleTime;
	

	@Bean 
    public HibernateExceptionTranslator hibernateExceptionTranslator(){ 
      return new HibernateExceptionTranslator(); 
    }
    
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws Exception {
    	LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
    	emf.setDataSource(dataSource());
    	HibernateJpaVendorAdapter jva = new HibernateJpaVendorAdapter();
    	jva.setShowSql(true);
    	jva.setDatabasePlatform(MySQLDialect.class.getName());
    	jva.setGenerateDdl(false);
    	emf.getJpaPropertyMap().put("hibernate.id.new_generator_mappings", "true");
    	emf.getJpaPropertyMap().put("hibernate.archive.autodetection", "");
//    	emf.getJpaPropertyMap().put("hibernate.cache.use_second_level_cache","true");
//    	emf.getJpaPropertyMap().put("hibernate.cache.use_query_cache","true");
//    	emf.getJpaPropertyMap().put("hibernate.cache.region.factory_class","org.hibernate.cache.ehcache.EhCacheRegionFactory");
		
    	emf.setJpaVendorAdapter(jva);
    	emf.setPackagesToScan("me.tracker.db.jpa.entities");
    	return emf;
    }

	@Bean
	public PlatformTransactionManager transactionManager() {
		return new JpaTransactionManager();
	}
	
	@Bean
	public DataSource dataSource() throws Exception {
		ComboPooledDataSource ds = new ComboPooledDataSource();
		ds.setDriverClass("com.mysql.jdbc.Driver");
		ds.setJdbcUrl(jdbcUrl);
		ds.setUser(jdbcUser);
		ds.setPassword(jdbcPassword.toString());
		
		ds.setAcquireIncrement(jdbcAcquireIncrement);
		ds.setMinPoolSize(jdbcMinPoolSize);
		ds.setMaxPoolSize(jdbcMaxPoolSize);
		ds.setMaxIdleTime(jdbcMaxIdleTime);
		
		return ds;
	}
}

