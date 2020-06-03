package com.hs.base.cache.memcached.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages="com.hs")
public class MemcachedClientTest {
	public static void main(String [] args) {
		SpringApplication.run(MemcachedClientTest.class , args);
	}
}
