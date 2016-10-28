package com.jfinal.ext.plugin.redis;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.jfinal.plugin.IPlugin;

public class JedisPlugin implements IPlugin{
	private String host;
	private Integer port;
	private String password;
	private JedisPoolConfig config;
	private JedisPool pool;
	public JedisPlugin(JedisPool pool){
		this.pool = pool;
	}
	public JedisPlugin(String host, int port){
		this.host = host;
		this.port = port;
	}
	public JedisPlugin(String host, int port, String password){
		this.host = host;
		this.port = port;
		this.password = password;
	}
	public JedisPlugin(JedisPoolConfig config, String host, int port, String password){
		this.host = host;
		this.port = port;
		this.password = password;
		this.config = config;
	}
	public boolean start() {
		if(pool != null){
			JedisKit.init(pool);
		}
		else{
			JedisKit.init(config, host, port, password);
		}
		return true;
	}

	public boolean stop() {
		JedisKit.close();
		return true;
	}
}
