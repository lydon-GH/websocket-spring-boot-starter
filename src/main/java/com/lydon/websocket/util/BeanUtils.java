package com.lydon.websocket.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;


public class BeanUtils implements ApplicationContextAware {

	private static ApplicationContext applicationContext = null;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		BeanUtils.applicationContext = applicationContext;
	}

	public static <T> T getBean(Class<T> cla) {
		return applicationContext.getBean(cla);
	}

	public static <T> T getBean(String claName) {
		return (T) applicationContext.getBean(claName);
	}

	public static <T> T getBean(String name, Class<T> cal) {
		return applicationContext.getBean(name, cal);
	}

	public static String getProperty(String key) {
		return applicationContext.getBean(Environment.class).getProperty(key);
	}

	public static void registerBeanDefinition(String beanName,BeanDefinition beanDefinition){
		ConfigurableApplicationContext configurableApplicationContext=(ConfigurableApplicationContext)applicationContext;
		BeanDefinitionRegistry beanDefinitionRegistry= (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
		beanDefinitionRegistry.registerBeanDefinition(beanName,beanDefinition);
	}

}
