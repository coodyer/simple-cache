package com.simple.cache.util;

import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.util.List;

import com.simple.cache.entity.BeanEntity;


public class AspectUtil {

	public static String getMethodCacheKey(Method method){
		String key=getMethodKey(method);
		key=key.replace(".", "_");
		key=key.replace(",", "_");
		return key;
	}
	public static String getMethodKey(Method method) {
		StringBuilder sb = new StringBuilder();
		Class<?> clazz = PropertUtil.getClass(method);
		sb.append(clazz.getName()).append(".").append(method.getName());
		Class<?>[] paraTypes = method.getParameterTypes();
		sb.append("(");
		if (!StringUtil.isNullOrEmpty(paraTypes)) {
			for (int i = 0; i < paraTypes.length; i++) {
				sb.append(paraTypes[i].getName());
				if (i < paraTypes.length - 1) {
					sb.append(",");
				}
			}
		}
		sb.append(")");
		return  sb.toString();
	}

	public static String getMethodKey(Class<?> clazz, Method method) {
		StringBuilder sb = new StringBuilder();
		sb.append(clazz.getName()).append(".").append(method.getName());
		Class<?>[] paraTypes = method.getParameterTypes();
		sb.append("(");
		if (!StringUtil.isNullOrEmpty(paraTypes)) {
			for (int i = 0; i < paraTypes.length; i++) {
				sb.append(paraTypes[i].getName());
				if (i < paraTypes.length - 1) {
					sb.append(",");
				}
			}
		}
		sb.append(")");
		return sb.toString();
	}
	public static String getFieldKey(Method method, Object[] paras,
			String key, String[] fields){
		if(StringUtil.isNullOrEmpty(key)){
			key=getMethodKey(method);
			key=key.replace(".", "_");
			key=key.replace(",", "_");
		}
		StringBuilder paraKey = new StringBuilder();
		for (String field : fields) {
			Object paraValue = AspectUtil.getMethodPara(method, field, paras);
			if (StringUtil.isNullOrEmpty(paraValue)) {
				paraValue = "";
			}
			paraKey.append("_")
					.append(JSONWriter.write(paraValue));
		}
		key=key+"_"+md5Code(paraKey.toString());
		return key;
	}
	public static String md5Code(String pwd) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(pwd.getBytes("UTF-8"));
			byte b[] = md.digest();

			int i;

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			return buf.toString();

		} catch (Exception e) {

		}
		return "";
	}
	public static Object getMethodPara(Method method, String fieldName,
			Object[] args) {
		List<BeanEntity> beanEntitys = PropertUtil.getMethodParas(method);
		if (StringUtil.isNullOrEmpty(beanEntitys)) {
			return "";
		}
		String[] fields = fieldName.split("\\.");
		BeanEntity entity = (BeanEntity) PropertUtil.getByList(
				beanEntitys, "fieldName", fields[0]);
		if (StringUtil.isNullOrEmpty(entity)) {
			return "";
		}
		Object para = args[beanEntitys.indexOf(entity)];
		if (fields.length > 1 && para != null) {
			for (int i = 1; i < fields.length; i++) {
				para = PropertUtil.getFieldValue(para, fields[i]);
			}
		}
		return para;
	}
	// 将对象内所有字段名、字段值拼接成字符串，用于缓存Key
	public static String getBeanKey(Object... obj) {
		if (StringUtil.isNullOrEmpty(obj)) {
			return "";
		}
		String str = JSONWriter.write(obj);
		return md5Code(str);
	}
}
