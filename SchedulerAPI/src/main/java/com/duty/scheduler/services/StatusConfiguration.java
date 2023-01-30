package com.duty.scheduler.services;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
public class StatusConfiguration {
	 @Bean
	 @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	 public DBStatus getStatusService() {
	  return new DBStatus();
	 }
}
