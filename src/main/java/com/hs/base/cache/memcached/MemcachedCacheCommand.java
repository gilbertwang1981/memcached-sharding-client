package com.hs.base.cache.memcached;

public enum MemcachedCacheCommand {
	CACHE_SET("set"),
	CACHE_DEL("del"),
	CACHE_GET("get"),
	CACHE_MGET("mget"),
	CACHE_MSET("mset"),
	CACHE_MDEL("mdel");
	
	private String cmd;
	
	private MemcachedCacheCommand(String cmd) {
		this.setCmd(cmd);
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}
}
