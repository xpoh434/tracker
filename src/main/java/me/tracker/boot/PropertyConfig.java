package me.tracker.boot;



import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.config.CustomEditorConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource("file:config.properties")
public class PropertyConfig {
 
  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }
  
  @Bean
  public static CustomEditorConfigurer customEditorConfigurer() {
	  CustomEditorConfigurer c = new CustomEditorConfigurer();
	  Map<String,String> m = Collections.singletonMap(Password.class.getName(),PasswordPropertyEditor.class.getName());
	  
	  c.setCustomEditors(m);
	  return c;
  }
}
