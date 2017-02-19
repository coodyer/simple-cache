# Simple-cache documentation
## 1. Note:
### strongly recommend the use of the system business cache, large curvature to ease the database pressure

## 2. Problem-Oriented:
### 1) to ease the database pressure to enhance the server side of the upper limit to enhance the efficiency of business code execution.

## 3. Core object (note):
### 1) @CacheWrite
#### Write the cache, the method is executed, the results written to the cache.
### 2) @CacheWipe:
#### Clean up a single cache, after the method is executed, clean up the cache

## 4. Features:
### 1) Weaken the concept of cache "layer"
## 2) Support cache KEY
### 3) Support cache with parameters to write and clean up
## 4) Any Bean method supports caching
### 5) support redis, memcached, localCache and other cache (all support time-out cache can be).

## 5. How to add a cache for a method
### 1) simple to use, do not require cleanup, do not emphasize real-time (default cache time 10 seconds)
### @ CacheWrite
#### public TagForUser loadUserTags (Integer uid, Integer tid) {
####}
### Note: After the method is executed, the program will generate a method KEY according to the class, method, parameter value, and write the result of the method into the cache, and set the validTime valid length, the default 10 seconds, the next call directly return cache ### will enter the method code logic.

## 2) Simple to use, do not require cleanup, distinguish between parameters, set the cache time

### // single parameter
#### @ CacheWrite (fields = "uid")
#### public TagForUser loadUserTags (Integer uid, Integer tid) {
####}
### // multiple parameters
#### @ CacheWrite (fields = {"uid", "tid"})
#### public TagForUser loadUserTags (Integer uid, Integer tid) {
####}
### // Set the cache time
#### @ CacheWrite (fields = {"uid", "tid"}, validTime = 60)
#### public TagForUser loadUserTags (Integer uid, Integer tid) {
####}

#### Note: After the method is executed, the program will generate a method KEY according to the class, method, pkField specified parameter value, and write the result of the method into cache, and set validTime valid length, default 10 seconds, ### Direct return to the cache, will not enter the method code logic.

### 3) precise use, specify the key, you can clean up:

#### @ CacheWrite (key = CacheFinal.SYSTEM_TAGS, validTime = 3600)
#### public List <TagForSys> loadSysTags () {
####}
#### // Specify the parameter / time
## @ CacheWrite (key = CacheFinal.ANCHOR_TAGS, validTime = 60, fields = "uid")
#### public List <TagForAnchor> loadAnchorTags (Integer uid) {
####}
#### Note: After the method is executed, the program will generate the cache according to the key specified by CacheWrite. If the pkField field is specified, the program generates a cache based on the value of the key + pkField field. And set validTime valid when #### long, the default 10 seconds, the next call directly return to the cache, will not enter the method code logic.

#### 6. Data update, how to clean up the cache 1) regular cleanup, clean up the specified key

### // do not emphasize parameters, only clean up
#### @ CacheWipe (key = CacheFinal.ANCHOR_TAGS)
#### public Integer saveUserTags (TagForUser userTag) {
####}
### // emphasize parameters, clean up
#### @ CacheWipe (key = CacheFinal.ANCHOR_TAGS, fields = "userTag.tid")
#### public Integer saveUserTags (TagForUser userTag) {
####}
#### Note: After the method is executed, the program clears the cache according to the KEY specified by CacheWipe. If the pkField field is specified, the program clears the cache according to the value of the key + pkField field.
#### 2) multi-cache cleanup, clean up multiple sets of keys
#### // Do not emphasize parameters, clean up multiple sets of cache
#### @ CacheWipe (key = CacheFinal.PET_YEAR_VALUE_CACHE),
#### @ CacheWipe (key = CacheFinal.PET_YEAR_DAY_VALUE_CACHE)
#### public Integer addSendValue (Integer uid, Integer sendValue) {
####}
#### @ CacheWipe (key = CacheFinal.PET_YEAR_VALUE_CACHE, fields = "uid")
#### @ CacheWipe (key = CacheFinal.PET_YEAR_DAY_VALUE_CACHE, fields = "uid")
#### public Integer addSendValue (Integer uid, Integer sendValue) {
####}

#### Note: After the method is executed, the program reads the CacheWipe collection according to CacheWipes and executes the regular cleanup rules

## 7. Run the environment
### Version JAR: simple-cache-1.0.jar Download: https: //pan.baidu.com/s/1kUVgxhX

### depend on JAR: aspectjrt.jar spring-core.jar log4j.jar

### Runtime Environment: JDK1.8

## 8. Configuration File (Spring):
### <aop: aspectj-autoproxy />
### <aop: aspectj-autoproxy proxy-target-class = "true" />
### <! - configuration program cache needs to implement com.simple.cache.sample.CacheFace interface ->
### <bean id = "localCache" class = "com.app.server.cache.LocalCache"> </ bean>
### <! - Configure the section cache ->
### <bean id = "simpleCacheHandle" class = "com.simple.cache.handle.CacheHandle">
### <property name = "cacheFace" ref = "localCache" />
### </ bean>
### <aop: config>
### <aop: aspect id = "superAop" ref = "simpleCacheHandle">
### <aop: pointcut id = "cachePointcut"
### expression = "@ annotation (com.simple.cache.annotation.CacheWrite) || @annotation (com.simple.cache.annotation.CacheWipe)" />
### <aop: around method = "CacheProcess" pointcut-ref = "cachePointcut" />
### </ aop: aspect>
### </ aop: config>
## 9. Copyright and author
### Author: WebSOS

### Feedback E-mail: 644556636@qq.com

### Please keep the copyright.
