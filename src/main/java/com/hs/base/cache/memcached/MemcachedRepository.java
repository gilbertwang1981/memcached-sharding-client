package com.hs.base.cache.memcached;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.spy.memcached.MemcachedClient;

@Component
public class MemcachedRepository {
	@Autowired
	private MemcachedCacheConfiguration memcachedCacheConfiguration;
	
	private Map<Integer , MemcachedClient> clients = new ConcurrentHashMap<>();
	
	private Integer shardNum; 
	
	@PostConstruct
	void init() {
		String shards = memcachedCacheConfiguration.getShards();
		StringTokenizer st = new StringTokenizer(shards , ";");
		int index = 0;
		shardNum = 0;
		while (st.hasMoreTokens()) {
			String shard = st.nextToken();
			StringTokenizer stn = new StringTokenizer(shard , ":");
			String ip = stn.nextToken();
			Integer port = Integer.parseInt(stn.nextToken());
			
			try {
				shardNum ++;
				clients.put(index ++ , new MemcachedClient(new InetSocketAddress(ip,port)));
			} catch (IOException e) {
				continue;
			}
		}
	}
	
	public Boolean set(int shard , String key , Object value , int exp) {
		MemcachedClient client = clients.get(shard);
		if (client == null) {
			return Boolean.FALSE;
		}
		
		Future<Boolean> ret = null;
		try {			
			ret = client.set(key, exp, value);
			
			return (ret != null && ret.get(memcachedCacheConfiguration.getOptTimeout() , TimeUnit.MILLISECONDS));
		} catch (Exception e) {
			if (ret != null) {
				ret.cancel(false);
			}
			return Boolean.FALSE;
		}
	}
	
	public Boolean del(int shard , String key) {
		MemcachedClient client = clients.get(shard);
		if (client == null) {
			return Boolean.FALSE;
		}
		
		Future<Boolean> ret = null;
		try {
			ret = client.delete(key);
			
			return (ret != null && ret.get(memcachedCacheConfiguration.getOptTimeout() , TimeUnit.MILLISECONDS));
		} catch (Exception e) {
			if (ret != null) {
				ret.cancel(false);
			}
			
			return Boolean.FALSE;
		}
	}
	
	public Object get(int shard , String key) {
		MemcachedClient client = clients.get(shard);
		if (client == null) {
			return null;
		}

		Future<Object> ret = null;
		try {
			ret = client.asyncGet(key);
			if (ret != null) {
				return ret.get(memcachedCacheConfiguration.getOptTimeout() , TimeUnit.MILLISECONDS);
			} else {
				return null;
			}
		} catch (Exception e) {
			if (ret != null) {
				ret.cancel(false);
			}
			
			return null;
		}
	}
	
	public Map<String, Object> mget(int shard , List<String> keys) {
		MemcachedClient client = clients.get(shard);
		if (client == null) {
			return null;
		}

		Future<Map<String, Object>> ret = null;
		try {
			ret = client.asyncGetBulk(keys);
			if (ret != null) {
				return ret.get(memcachedCacheConfiguration.getOptTimeout() , TimeUnit.MILLISECONDS);
			} else {
				return null;
			}
		} catch (Exception e) {
			if (ret != null) {
				ret.cancel(false);
			}
			return null;
		}		
	}

	public Integer getShardNum() {
		return shardNum;
	}

	public void setShardNum(Integer shardNum) {
		this.shardNum = shardNum;
	}
}
