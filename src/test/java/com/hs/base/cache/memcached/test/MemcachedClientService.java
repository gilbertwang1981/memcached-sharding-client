package com.hs.base.cache.memcached.test;

import org.springframework.stereotype.Component;

import com.hs.base.cache.memcached.MemcachedCacheable;

@Component
public class MemcachedClientService {
	
	@MemcachedCacheable(
			group = "default-group" , 
			opt = "get" , 
			keyName = "key" , 
			strategy = MemcachedClientShardingStrategy.class)
	public String getTest(String key) {
		return null;
	}
	
	@MemcachedCacheable(
			group = "default-group" , 
			opt = "del" , 
			keyName = "key" ,  
			strategy = MemcachedClientShardingStrategy.class)
	public Boolean delTest(String key) {
		return true;
	}
	
	@MemcachedCacheable(
			group = "default-group" , 
			opt = "set" , 
			keyName = "key" ,
			valueName = "value" ,
			expire = 10 ,
			strategy = MemcachedClientShardingStrategy.class)
	public Boolean setTest(String key , String value) {
		return true;
	}
}
