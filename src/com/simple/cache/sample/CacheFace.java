package com.simple.cache.sample;

public interface CacheFace {

	/**
	 * 查询缓存
	 * 
	 * @param key
	 *            缓存key
	 * @return
	 */
	public Object getCache(String key);
	
	/**
	 * 设置缓存
	 * 
	 * @param key
	 *            缓存key
	 * @param value
	 *            缓存内容
	 * @param timeOut
	 *            缓存超时时间/秒
	 */
	public void setCache(String key, Object value, Integer timeOut);
	
	/**
	 * 设置缓存
	 * 
	 * @param key
	 *            缓存key
	 * @param value
	 *            缓存内容
	 */
	public void setCache(String key, Object value);
	
	/**
	 * 删除缓存
	 * 
	 * @param key
	 *            缓存key
	 */
	public void delCache(String key) ;
	
	/**
	 * 模糊删除缓存
	 * 
	 * @param key
	 *            缓存key
	 */
	public void delCacheFuzz(String key);
}
