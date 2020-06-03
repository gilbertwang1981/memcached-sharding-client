package com.hs.base.cache.memcached;

public interface MemcachedShardStrategy {
	public Integer sharding(Object key , Integer shard);
}
