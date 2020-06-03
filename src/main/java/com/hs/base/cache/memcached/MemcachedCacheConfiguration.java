package com.hs.base.cache.memcached;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "memcached")
@Component
public class MemcachedCacheConfiguration extends CachingConfigurerSupport {
	private String shards;
	private Long optTimeout;

	public String getShards() {
		return shards;
	}

	public void setShards(String shards) {
		this.shards = shards;
	}

	public Long getOptTimeout() {
		return optTimeout;
	}

	public void setOptTimeout(Long optTimeout) {
		this.optTimeout = optTimeout;
	}
}
