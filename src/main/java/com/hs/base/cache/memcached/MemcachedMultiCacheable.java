package com.hs.base.cache.memcached;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MemcachedMultiCacheable {
	/**
	 * 缓存组名字，字符串
	 * @return
	 */
	String group();
	
	/**
	 * 操作类型，字符串，包括:
	 * mget: 批量获取缓存，方法的返回值定义为Map<String , Object>类型
	 * mdel: 批量删除，方法的返回值定义为Map<String , Object>类型
	 * mset: 批量设置，方法的返回值定义为Map<String , Object>类型
	 * @return
	 */
	String opt();
	
	/**
	 * 缓存的数据KV-MAP变量名字，是一个Map<String , Object>
	 * @return
	 */
	String kvMap();
	
	/**
	 * key超时时间，单位：秒
	 * @return
	 */
	int expire() default 30;
	
	/**
	 * 分片策略实现类
	 * @return
	 */
	Class<?> strategy();
}
