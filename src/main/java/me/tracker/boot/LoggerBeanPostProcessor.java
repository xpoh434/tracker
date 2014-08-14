package me.tracker.boot;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

@Component
public class LoggerBeanPostProcessor implements BeanPostProcessor {

	@Override
	public Object postProcessBeforeInitialization(final Object bean, String beanName)
			throws BeansException {
		 ReflectionUtils.doWithFields(bean.getClass(), new ReflectionUtils.FieldCallback() {
	         public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
	             if (field.getAnnotation(me.tracker.boot.Log.class) != null && field.getType().equals(Logger.class)) {
	                 ReflectionUtils.makeAccessible(field);
	                 field.set(bean, LoggerFactory.getLogger(bean.getClass()));
	             }
	         }
	       });
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}

}
