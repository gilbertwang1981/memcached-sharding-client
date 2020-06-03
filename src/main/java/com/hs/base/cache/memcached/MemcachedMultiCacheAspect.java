package com.hs.base.cache.memcached;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MemcachedMultiCacheAspect {
	
	private static final String SEPARATOR = ":";
	
	@Autowired
	private MemcachedRepository memcachedRepository;
	
	private int getShards(Object keyPrefix , MemcachedMultiCacheable cacheable) throws InstantiationException, IllegalAccessException {
		MemcachedShardStrategy strategy = (MemcachedShardStrategy)cacheable.strategy().newInstance();
        int shard = 0;
        if (strategy != null) {
        	shard = strategy.sharding(keyPrefix , memcachedRepository.getShardNum());
        }
        
        return shard;
	}
	
	private Map<Integer , List<MemcachedMultiKv>> aggregate(Map<String , String> kvs , MemcachedMultiCacheable cacheable) throws InstantiationException, IllegalAccessException {
		Map<Integer , List<MemcachedMultiKv>> dataMap = new HashMap<>();
		for (Map.Entry<String, String> entry : kvs.entrySet()) {
			int shard = getShards(entry.getKey() , cacheable);
			List<MemcachedMultiKv> dataValue = dataMap.get(shard);
			if (dataValue == null) {
				dataValue = new ArrayList<>();
				dataValue.add(new MemcachedMultiKv(entry.getKey() , entry.getValue()));
				dataMap.put(shard , dataValue);
			} else {
				dataValue.add(new MemcachedMultiKv(entry.getKey() , entry.getValue()));
			}
		}
		
		return dataMap;
	}
	
	private Map<String, Object> multiExecute(int shard , List<MemcachedMultiKv> kvs , MemcachedMultiCacheable cacheable) {
		Map<String, Object> values = null;
		if (MemcachedCacheCommand.CACHE_MGET.getCmd().equalsIgnoreCase(cacheable.opt())) {
			List<String> params = new ArrayList<>();
			for (MemcachedMultiKv kv : kvs) {
				params.add(cacheable.group() + SEPARATOR + kv.getKey());
			}
			values = memcachedRepository.mget(shard, params);
		} else if (MemcachedCacheCommand.CACHE_MSET.getCmd().equalsIgnoreCase(cacheable.opt())) {
			values = new HashMap<>();
			for (MemcachedMultiKv kv : kvs) {
				values.put(kv.getKey() , memcachedRepository.set(shard, cacheable.group() + SEPARATOR + kv.getKey() , kv.getValue() , cacheable.expire()));
			}
		} else if (MemcachedCacheCommand.CACHE_MDEL.getCmd().equalsIgnoreCase(cacheable.opt())) {
			values = new HashMap<>();
			for (MemcachedMultiKv kv : kvs) {
				values.put(kv.getKey() , memcachedRepository.del(shard, cacheable.group() + SEPARATOR + kv.getKey()));
			}			
		}
		
		return values;
	}

	@Pointcut("@annotation(com.hs.base.cache.memcached.MemcachedMultiCacheable)")
    public void cacheMultiPointCut() {
    }
	
	@SuppressWarnings("unchecked")
	@Around("cacheMultiPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
		MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        MemcachedMultiCacheable cacheable = method.getAnnotation(MemcachedMultiCacheable.class);
        
        try {
        	Map<String , String> kvs = null;
        	int index = 0;
        	for (String parameterName : signature.getParameterNames()) {
	        	if (parameterName.equalsIgnoreCase(cacheable.kvMap())) {
	        		kvs = (Map<String, String>) point.getArgs()[index];
	        		break;
	        	}
	        	index ++;
        	}
        	
        	Map<Integer , List<MemcachedMultiKv>> sharding = aggregate(kvs , cacheable);
        	Map<String, Object> valueMap = null;
        	for (Map.Entry<Integer, List<MemcachedMultiKv>> entry : sharding.entrySet()) {
        		Map<String, Object> valueMapPartial = multiExecute(entry.getKey() , entry.getValue() , cacheable);
        		if (valueMapPartial != null) {
        			if (valueMap == null) {
        				valueMap = valueMapPartial;
        			} else {
        				valueMap.putAll(valueMapPartial);
        			}
        		}
        	}
            
    		return valueMap;
        } catch (Exception e) {
        	throw e;
        }
	}
}
