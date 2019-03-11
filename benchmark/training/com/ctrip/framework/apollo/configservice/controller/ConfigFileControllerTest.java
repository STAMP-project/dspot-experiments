package com.ctrip.framework.apollo.configservice.controller;


import ConfigFileController.ConfigFileOutputFormat.PROPERTIES;
import HttpStatus.OK;
import Topics.APOLLO_RELEASE_TOPIC;
import com.ctrip.framework.apollo.biz.entity.ReleaseMessage;
import com.ctrip.framework.apollo.biz.grayReleaseRule.GrayReleaseRulesHolder;
import com.ctrip.framework.apollo.configservice.util.NamespaceUtil;
import com.ctrip.framework.apollo.configservice.util.WatchKeysUtil;
import com.ctrip.framework.apollo.core.dto.ApolloConfig;
import com.google.common.cache.Cache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;


/**
 *
 *
 * @author Jason Song(song_s@ctrip.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class ConfigFileControllerTest {
    @Mock
    private ConfigController configController;

    @Mock
    private WatchKeysUtil watchKeysUtil;

    @Mock
    private NamespaceUtil namespaceUtil;

    @Mock
    private GrayReleaseRulesHolder grayReleaseRulesHolder;

    private ConfigFileController configFileController;

    private String someAppId;

    private String someClusterName;

    private String someNamespace;

    private String someDataCenter;

    private String someClientIp;

    @Mock
    private HttpServletResponse someResponse;

    @Mock
    private HttpServletRequest someRequest;

    Multimap<String, String> watchedKeys2CacheKey;

    Multimap<String, String> cacheKey2WatchedKeys;

    @Test
    public void testQueryConfigAsProperties() throws Exception {
        String someKey = "someKey";
        String someValue = "someValue";
        String anotherKey = "anotherKey";
        String anotherValue = "anotherValue";
        String someWatchKey = "someWatchKey";
        String anotherWatchKey = "anotherWatchKey";
        Set<String> watchKeys = Sets.newHashSet(someWatchKey, anotherWatchKey);
        String cacheKey = configFileController.assembleCacheKey(PROPERTIES, someAppId, someClusterName, someNamespace, someDataCenter);
        Map<String, String> configurations = ImmutableMap.of(someKey, someValue, anotherKey, anotherValue);
        ApolloConfig someApolloConfig = Mockito.mock(ApolloConfig.class);
        Mockito.when(someApolloConfig.getConfigurations()).thenReturn(configurations);
        Mockito.when(configController.queryConfig(someAppId, someClusterName, someNamespace, someDataCenter, "-1", someClientIp, null, someRequest, someResponse)).thenReturn(someApolloConfig);
        Mockito.when(watchKeysUtil.assembleAllWatchKeys(someAppId, someClusterName, someNamespace, someDataCenter)).thenReturn(watchKeys);
        ResponseEntity<String> response = configFileController.queryConfigAsProperties(someAppId, someClusterName, someNamespace, someDataCenter, someClientIp, someRequest, someResponse);
        Assert.assertEquals(2, watchedKeys2CacheKey.size());
        Assert.assertEquals(2, cacheKey2WatchedKeys.size());
        Assert.assertTrue(watchedKeys2CacheKey.containsEntry(someWatchKey, cacheKey));
        Assert.assertTrue(watchedKeys2CacheKey.containsEntry(anotherWatchKey, cacheKey));
        Assert.assertTrue(cacheKey2WatchedKeys.containsEntry(cacheKey, someWatchKey));
        Assert.assertTrue(cacheKey2WatchedKeys.containsEntry(cacheKey, anotherWatchKey));
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertTrue(response.getBody().contains(String.format("%s=%s", someKey, someValue)));
        Assert.assertTrue(response.getBody().contains(String.format("%s=%s", anotherKey, anotherValue)));
        ResponseEntity<String> anotherResponse = configFileController.queryConfigAsProperties(someAppId, someClusterName, someNamespace, someDataCenter, someClientIp, someRequest, someResponse);
        Assert.assertEquals(response, anotherResponse);
        Mockito.verify(configController, Mockito.times(1)).queryConfig(someAppId, someClusterName, someNamespace, someDataCenter, "-1", someClientIp, null, someRequest, someResponse);
    }

    @Test
    public void testQueryConfigAsJson() throws Exception {
        String someKey = "someKey";
        String someValue = "someValue";
        Gson gson = new Gson();
        Type responseType = new TypeToken<Map<String, String>>() {}.getType();
        String someWatchKey = "someWatchKey";
        Set<String> watchKeys = Sets.newHashSet(someWatchKey);
        Map<String, String> configurations = ImmutableMap.of(someKey, someValue);
        ApolloConfig someApolloConfig = Mockito.mock(ApolloConfig.class);
        Mockito.when(configController.queryConfig(someAppId, someClusterName, someNamespace, someDataCenter, "-1", someClientIp, null, someRequest, someResponse)).thenReturn(someApolloConfig);
        Mockito.when(someApolloConfig.getConfigurations()).thenReturn(configurations);
        Mockito.when(watchKeysUtil.assembleAllWatchKeys(someAppId, someClusterName, someNamespace, someDataCenter)).thenReturn(watchKeys);
        ResponseEntity<String> response = configFileController.queryConfigAsJson(someAppId, someClusterName, someNamespace, someDataCenter, someClientIp, someRequest, someResponse);
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertEquals(configurations, gson.fromJson(response.getBody(), responseType));
    }

    @Test
    public void testQueryConfigWithGrayRelease() throws Exception {
        String someKey = "someKey";
        String someValue = "someValue";
        Gson gson = new Gson();
        Type responseType = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> configurations = ImmutableMap.of(someKey, someValue);
        Mockito.when(grayReleaseRulesHolder.hasGrayReleaseRule(someAppId, someClientIp, someNamespace)).thenReturn(true);
        ApolloConfig someApolloConfig = Mockito.mock(ApolloConfig.class);
        Mockito.when(someApolloConfig.getConfigurations()).thenReturn(configurations);
        Mockito.when(configController.queryConfig(someAppId, someClusterName, someNamespace, someDataCenter, "-1", someClientIp, null, someRequest, someResponse)).thenReturn(someApolloConfig);
        ResponseEntity<String> response = configFileController.queryConfigAsJson(someAppId, someClusterName, someNamespace, someDataCenter, someClientIp, someRequest, someResponse);
        ResponseEntity<String> anotherResponse = configFileController.queryConfigAsJson(someAppId, someClusterName, someNamespace, someDataCenter, someClientIp, someRequest, someResponse);
        Mockito.verify(configController, Mockito.times(2)).queryConfig(someAppId, someClusterName, someNamespace, someDataCenter, "-1", someClientIp, null, someRequest, someResponse);
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertEquals(configurations, gson.fromJson(response.getBody(), responseType));
        Assert.assertTrue(watchedKeys2CacheKey.isEmpty());
        Assert.assertTrue(cacheKey2WatchedKeys.isEmpty());
    }

    @Test
    public void testHandleMessage() throws Exception {
        String someWatchKey = "someWatchKey";
        String anotherWatchKey = "anotherWatchKey";
        String someCacheKey = "someCacheKey";
        String anotherCacheKey = "anotherCacheKey";
        String someValue = "someValue";
        ReleaseMessage someReleaseMessage = Mockito.mock(ReleaseMessage.class);
        Mockito.when(someReleaseMessage.getMessage()).thenReturn(someWatchKey);
        Cache<String, String> cache = ((Cache<String, String>) (ReflectionTestUtils.getField(configFileController, "localCache")));
        cache.put(someCacheKey, someValue);
        cache.put(anotherCacheKey, someValue);
        watchedKeys2CacheKey.putAll(someWatchKey, Lists.newArrayList(someCacheKey, anotherCacheKey));
        watchedKeys2CacheKey.putAll(anotherWatchKey, Lists.newArrayList(someCacheKey, anotherCacheKey));
        cacheKey2WatchedKeys.putAll(someCacheKey, Lists.newArrayList(someWatchKey, anotherWatchKey));
        cacheKey2WatchedKeys.putAll(anotherCacheKey, Lists.newArrayList(someWatchKey, anotherWatchKey));
        configFileController.handleMessage(someReleaseMessage, APOLLO_RELEASE_TOPIC);
        Assert.assertTrue(watchedKeys2CacheKey.isEmpty());
        Assert.assertTrue(cacheKey2WatchedKeys.isEmpty());
    }
}

