package com.hs.base.cache.memcached;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MemcachedCacheable {
	/**
	 * 缓存组名字，字符串
	 * @return
	 */
	String group();
	
	/**
	 * 操作类型，字符串，包括:
	 * get：如果能从缓存获取到key，返回值为缓存的value，方法的返回值定义为Object
	 * set：设置，成功返回true，失败为false，方法的返回值定义为Boolean
	 * del：删除，成功返回true，失败为false，方法的返回值定义为Boolean
	 * @return
	 */
	String opt();
	
	/**
	 * 缓存key变量的名字，字符串
	 * @return
	 */
	String keyName();
	
	/**
	 * 缓存value变量的名字，字符串
	 * @return
	 */
	String valueName() default "";
	
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
