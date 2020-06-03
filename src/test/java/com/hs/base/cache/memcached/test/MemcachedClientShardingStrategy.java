package com.hs.base.cache.memcached.test;

import com.hs.base.cache.memcached.MemcachedShardStrategy;

public class MemcachedClientShardingStrategy implements MemcachedShardStrategy {

	@Override
	public Integer sharding(Object key, Integer shard) {
		return Math.abs(key.hashCode()) % shard;
	}
}
