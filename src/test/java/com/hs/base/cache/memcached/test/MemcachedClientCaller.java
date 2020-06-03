package com.hs.base.cache.memcached.test;

import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MemcachedClientCaller {
	private static Logger logger = LoggerFactory.getLogger(MemcachedClientCaller.class);
	
	@Autowired
	private MemcachedClientService memcachedClientService;
	
	@PostConstruct
	private void init() {
		new Timer().scheduleAtFixedRate(new TimerTask() {
			public void run() {
				call();
			}
		} , 5000 , 5000); 
	}
	
	private void call() {
		logger.info(memcachedClientService.setTest("t", "1233")?"设置成功" : "设置失败");
		
		logger.info(memcachedClientService.getTest("t"));
		
		logger.info(memcachedClientService.delTest("t")? "删除成功" : "删除失败");
		
		logger.info(memcachedClientService.getTest("t"));
	}
}
