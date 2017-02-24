package com.simple.cache.handle;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.StopWatch;

import com.simple.cache.annotation.CacheWipe;
import com.simple.cache.annotation.CacheWrite;
import com.simple.cache.sample.CacheFace;
import com.simple.cache.util.AspectUtil;
import com.simple.cache.util.StringUtil;

public class CacheHandle {

	Logger logger=Logger.getLogger(this.getClass());
	
	CacheFace cacheFace;

	public CacheFace getCacheFace() {
		return cacheFace;
	}

	public void setCacheFace(CacheFace cacheFace) {
		this.cacheFace = cacheFace;
	}

	public Object CacheProcess(ProceedingJoinPoint pjp) throws Throwable {
		StopWatch sw = new StopWatch(getClass().getSimpleName());
		try {
			// AOP启动监听
			sw.start(pjp.getSignature().toShortString());
			// AOP获取方法执行信息
			Signature signature = pjp.getSignature();
			MethodSignature methodSignature = (MethodSignature) signature;
			Method method = methodSignature.getMethod();
			//查缓存逻辑
			CacheWrite handle = method.getAnnotation(CacheWrite.class);
			if(handle!=null){
				return CacheWrite(pjp,method);
			}
			//清缓存逻辑
			CacheWipe[] handles = method.getAnnotationsByType(CacheWipe.class);
			if(!StringUtil.isNullOrEmpty(handles)){
				return CacheWipe(pjp,method);
			}
			return pjp.proceed();
		} finally {
			sw.stop();
		}
	}

	private Object CacheWrite(ProceedingJoinPoint pjp,Method method) throws Throwable {
			// AOP获取方法执行信息
			if (method == null) {
				return pjp.proceed();
			}
			// 获取注解
			CacheWrite write = method.getAnnotation(CacheWrite.class);
			if (write == null) {
				return pjp.proceed();
			}
			// 封装缓存KEY
			Object[] paras = pjp.getArgs();
			String key = write.key();
			try {
				if (StringUtil.isNullOrEmpty(key)) {
					key = AspectUtil.getMethodCacheKey(method);
				}
				if (StringUtil.isNullOrEmpty(write.fields())) {
					key += "_";
					key += AspectUtil.getBeanKey(paras);
				}
				if (!StringUtil.isNullOrEmpty(write.fields())) {
					key = AspectUtil.getFieldKey(method, paras, key,
							write.fields());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Integer cacheTimer = ((write.validTime() == 0) ? 24 * 3600
					: write.validTime());
			try {
				Object result = cacheFace.getCache(key);
				logger.debug("获取缓存:"+key+";缓存内容:"+result);
				if (!StringUtil.isNullOrEmpty(result)) {
					return result;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Object result = pjp.proceed();
			if (result != null) {
				try {
					cacheFace.setCache(key, result, cacheTimer);
					logger.debug("写入缓存:"+key+";缓存内容:"+result);
				} catch (Exception e) {
				}
			}
			return result;
	}

	/**
	 * 缓存清理
	 * 
	 * @param pjp
	 * @return
	 * @throws Throwable
	 */
	private Object CacheWipe(ProceedingJoinPoint pjp,Method method) throws Throwable {
			if (method == null) {
				return pjp.proceed();
			}
			Object[] paras = pjp.getArgs();
			Object result = pjp.proceed();
			CacheWipe[] handles = method.getAnnotationsByType(CacheWipe.class);
			if (StringUtil.isNullOrEmpty(handles)) {
				return result;
			}
			for (CacheWipe wipe : handles) {
				try {
					String key = wipe.key();
					if (StringUtil.isNullOrEmpty(wipe.key())) {
						key = (AspectUtil.getMethodCacheKey(method));
					}
					if (StringUtil.isNullOrEmpty(wipe.fields())) {
						key += "_";
						key += AspectUtil.getBeanKey(paras);
					}
					if (!StringUtil.isNullOrEmpty(wipe.fields())) {
						key = AspectUtil.getFieldKey(method, paras, key,
								wipe.fields());
					}
					cacheFace.delCache(key);
					logger.debug("清理缓存:"+key);
				} catch (Exception e) {
				}
			}
			return result;
	}
}
