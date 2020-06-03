package com.hs.base.cache.memcached;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MemcachedCacheAspect {
	@Autowired
	private MemcachedRepository memcachedRepository;

	private static final String SEPARATOR = ":";
	
	@Pointcut("@annotation(com.hs.base.cache.memcached.MemcachedCacheable)")
    public void cachePointCut() {
    }
	
	private int getShards(Object keyPrefix , MemcachedCacheable cacheable) throws InstantiationException, IllegalAccessException {
		MemcachedShardStrategy strategy = (MemcachedShardStrategy)cacheable.strategy().newInstance();
        int shard = 0;
        if (strategy != null) {
        	shard = strategy.sharding(keyPrefix , memcachedRepository.getShardNum());
        }
        
        return shard;
	}
	
	private Object execute(int shard , ProceedingJoinPoint point , Object keyPrefix , Object valuePrefix , MemcachedCacheable cacheable) throws Throwable {
    	if (keyPrefix != null && MemcachedCacheCommand.CACHE_GET.getCmd().equalsIgnoreCase(cacheable.opt())) {
    		Object value = memcachedRepository.get(shard , cacheable.group() + SEPARATOR + keyPrefix);
    		if (value != null) {
    			return value;
    		} else {
    			value = point.proceed();
    			if (value != null &&
    				memcachedRepository.set(shard , cacheable.group() + SEPARATOR + keyPrefix , value , cacheable.expire())) {
    				
    				return value;
    			} else {
    				return null;
    			}
    		}
        } else if (keyPrefix != null && MemcachedCacheCommand.CACHE_DEL.getCmd().equalsIgnoreCase(cacheable.opt())) {
        	return memcachedRepository.del(shard , cacheable.group() + SEPARATOR + keyPrefix);
        } else if (keyPrefix != null && MemcachedCacheCommand.CACHE_SET.getCmd().equalsIgnoreCase(cacheable.opt())) {
        	return memcachedRepository.set(shard , cacheable.group() + SEPARATOR + keyPrefix , valuePrefix , cacheable.expire());
        } else {
        	return point.proceed();
        }		
	}
	
	@Around("cachePointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
		MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        MemcachedCacheable cacheable = method.getAnnotation(MemcachedCacheable.class);
        
        try {
        	int index = 0;
        	Object keyPrefix = null;
        	Object valuePrefix = null;
        	for (String parameterName : signature.getParameterNames()) {
	        	if (parameterName.equalsIgnoreCase(cacheable.keyName())) {
	        		keyPrefix = point.getArgs()[index].toString();
	        	} else if (parameterName.equalsIgnoreCase(cacheable.valueName())) {
	        		valuePrefix = point.getArgs()[index].toString();
	        	}
	        	
	        	index ++;
        	}
        	
        	return execute(getShards(keyPrefix , cacheable) , point , keyPrefix , valuePrefix , cacheable);
        } catch (Exception e) {
        	throw e;
        }
	}
}
