package com.ctrip.framework.apollo.integration;


import HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import HttpServletResponse.SC_OK;
import com.ctrip.framework.apollo.BaseIntegrationTest;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.core.dto.ApolloConfig;
import com.ctrip.framework.apollo.core.dto.ApolloConfigNotification;
import com.ctrip.framework.apollo.internals.RemoteConfigLongPollService;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.SettableFuture;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Jason Song(song_s@ctrip.com)
 */
public class ConfigIntegrationTest extends BaseIntegrationTest {
    private String someReleaseKey;

    private File configDir;

    private String defaultNamespace;

    private String someOtherNamespace;

    private RemoteConfigLongPollService remoteConfigLongPollService;

    @Test
    public void testGetConfigWithNoLocalFileButWithRemoteConfig() throws Exception {
        String someKey = "someKey";
        String someValue = "someValue";
        String someNonExistedKey = "someNonExistedKey";
        String someDefaultValue = "someDefaultValue";
        ApolloConfig apolloConfig = assembleApolloConfig(ImmutableMap.of(someKey, someValue));
        ContextHandler handler = mockConfigServerHandler(SC_OK, apolloConfig);
        startServerWithHandlers(handler);
        Config config = ConfigService.getAppConfig();
        Assert.assertEquals(someValue, config.getProperty(someKey, null));
        Assert.assertEquals(someDefaultValue, config.getProperty(someNonExistedKey, someDefaultValue));
    }

    @Test
    public void testGetConfigWithLocalFileAndWithRemoteConfig() throws Exception {
        String someKey = "someKey";
        String someValue = "someValue";
        String anotherValue = "anotherValue";
        Properties properties = new Properties();
        properties.put(someKey, someValue);
        createLocalCachePropertyFile(properties);
        ApolloConfig apolloConfig = assembleApolloConfig(ImmutableMap.of(someKey, anotherValue));
        ContextHandler handler = mockConfigServerHandler(SC_OK, apolloConfig);
        startServerWithHandlers(handler);
        Config config = ConfigService.getAppConfig();
        Assert.assertEquals(anotherValue, config.getProperty(someKey, null));
    }

    @Test
    public void testGetConfigWithNoLocalFileAndRemoteConfigError() throws Exception {
        ContextHandler handler = mockConfigServerHandler(SC_INTERNAL_SERVER_ERROR, null);
        startServerWithHandlers(handler);
        Config config = ConfigService.getAppConfig();
        String someKey = "someKey";
        String someDefaultValue = "defaultValue" + (Math.random());
        Assert.assertEquals(someDefaultValue, config.getProperty(someKey, someDefaultValue));
    }

    @Test
    public void testGetConfigWithLocalFileAndRemoteConfigError() throws Exception {
        String someKey = "someKey";
        String someValue = "someValue";
        Properties properties = new Properties();
        properties.put(someKey, someValue);
        createLocalCachePropertyFile(properties);
        ContextHandler handler = mockConfigServerHandler(SC_INTERNAL_SERVER_ERROR, null);
        startServerWithHandlers(handler);
        Config config = ConfigService.getAppConfig();
        Assert.assertEquals(someValue, config.getProperty(someKey, null));
    }

    @Test
    public void testGetConfigWithNoLocalFileAndRemoteMetaServiceRetry() throws Exception {
        String someKey = "someKey";
        String someValue = "someValue";
        ApolloConfig apolloConfig = assembleApolloConfig(ImmutableMap.of(someKey, someValue));
        ContextHandler configHandler = mockConfigServerHandler(SC_OK, apolloConfig);
        boolean failAtFirstTime = true;
        ContextHandler metaServerHandler = mockMetaServerHandler(failAtFirstTime);
        startServerWithHandlers(metaServerHandler, configHandler);
        Config config = ConfigService.getAppConfig();
        Assert.assertEquals(someValue, config.getProperty(someKey, null));
    }

    @Test
    public void testGetConfigWithNoLocalFileAndRemoteConfigServiceRetry() throws Exception {
        String someKey = "someKey";
        String someValue = "someValue";
        ApolloConfig apolloConfig = assembleApolloConfig(ImmutableMap.of(someKey, someValue));
        boolean failedAtFirstTime = true;
        ContextHandler handler = mockConfigServerHandler(SC_OK, apolloConfig, failedAtFirstTime);
        startServerWithHandlers(handler);
        Config config = ConfigService.getAppConfig();
        Assert.assertEquals(someValue, config.getProperty(someKey, null));
    }

    @Test
    public void testRefreshConfig() throws Exception {
        final String someKey = "someKey";
        final String someValue = "someValue";
        final String anotherValue = "anotherValue";
        int someRefreshInterval = 500;
        TimeUnit someRefreshTimeUnit = TimeUnit.MILLISECONDS;
        setRefreshInterval(someRefreshInterval);
        setRefreshTimeUnit(someRefreshTimeUnit);
        Map<String, String> configurations = Maps.newHashMap();
        configurations.put(someKey, someValue);
        ApolloConfig apolloConfig = assembleApolloConfig(configurations);
        ContextHandler handler = mockConfigServerHandler(SC_OK, apolloConfig);
        startServerWithHandlers(handler);
        Config config = ConfigService.getAppConfig();
        final List<ConfigChangeEvent> changeEvents = Lists.newArrayList();
        final SettableFuture<Boolean> refreshFinished = SettableFuture.create();
        config.addChangeListener(new ConfigChangeListener() {
            AtomicInteger counter = new AtomicInteger(0);

            @Override
            public void onChange(ConfigChangeEvent changeEvent) {
                // only need to assert once
                if ((counter.incrementAndGet()) > 1) {
                    return;
                }
                Assert.assertEquals(1, changeEvent.changedKeys().size());
                Assert.assertTrue(changeEvent.isChanged(someKey));
                Assert.assertEquals(someValue, changeEvent.getChange(someKey).getOldValue());
                Assert.assertEquals(anotherValue, changeEvent.getChange(someKey).getNewValue());
                // if there is any assertion failed above, this line won't be executed
                changeEvents.add(changeEvent);
                refreshFinished.set(true);
            }
        });
        apolloConfig.getConfigurations().put(someKey, anotherValue);
        refreshFinished.get((someRefreshInterval * 5), someRefreshTimeUnit);
        Assert.assertThat("Change event's size should equal to one or there must be some assertion failed in change listener", 1, IsEqual.equalTo(changeEvents.size()));
        Assert.assertEquals(anotherValue, config.getProperty(someKey, null));
    }

    @Test
    public void testLongPollRefresh() throws Exception {
        final String someKey = "someKey";
        final String someValue = "someValue";
        final String anotherValue = "anotherValue";
        long someNotificationId = 1;
        long pollTimeoutInMS = 50;
        Map<String, String> configurations = Maps.newHashMap();
        configurations.put(someKey, someValue);
        ApolloConfig apolloConfig = assembleApolloConfig(configurations);
        ContextHandler configHandler = mockConfigServerHandler(SC_OK, apolloConfig);
        ContextHandler pollHandler = mockPollNotificationHandler(pollTimeoutInMS, SC_OK, Lists.newArrayList(new ApolloConfigNotification(apolloConfig.getNamespaceName(), someNotificationId)), false);
        startServerWithHandlers(configHandler, pollHandler);
        Config config = ConfigService.getAppConfig();
        Assert.assertEquals(someValue, config.getProperty(someKey, null));
        final SettableFuture<Boolean> longPollFinished = SettableFuture.create();
        config.addChangeListener(new ConfigChangeListener() {
            @Override
            public void onChange(ConfigChangeEvent changeEvent) {
                longPollFinished.set(true);
            }
        });
        apolloConfig.getConfigurations().put(someKey, anotherValue);
        longPollFinished.get((pollTimeoutInMS * 20), TimeUnit.MILLISECONDS);
        Assert.assertEquals(anotherValue, config.getProperty(someKey, null));
    }

    @Test
    public void testLongPollRefreshWithMultipleNamespacesAndOnlyOneNamespaceNotified() throws Exception {
        final String someKey = "someKey";
        final String someValue = "someValue";
        final String anotherValue = "anotherValue";
        long someNotificationId = 1;
        long pollTimeoutInMS = 50;
        Map<String, String> configurations = Maps.newHashMap();
        configurations.put(someKey, someValue);
        ApolloConfig apolloConfig = assembleApolloConfig(configurations);
        ContextHandler configHandler = mockConfigServerHandler(SC_OK, apolloConfig);
        ContextHandler pollHandler = mockPollNotificationHandler(pollTimeoutInMS, SC_OK, Lists.newArrayList(new ApolloConfigNotification(apolloConfig.getNamespaceName(), someNotificationId)), false);
        startServerWithHandlers(configHandler, pollHandler);
        Config someOtherConfig = ConfigService.getConfig(someOtherNamespace);
        Config config = ConfigService.getAppConfig();
        Assert.assertEquals(someValue, config.getProperty(someKey, null));
        Assert.assertEquals(someValue, someOtherConfig.getProperty(someKey, null));
        final SettableFuture<Boolean> longPollFinished = SettableFuture.create();
        config.addChangeListener(new ConfigChangeListener() {
            @Override
            public void onChange(ConfigChangeEvent changeEvent) {
                longPollFinished.set(true);
            }
        });
        apolloConfig.getConfigurations().put(someKey, anotherValue);
        longPollFinished.get(5000, TimeUnit.MILLISECONDS);
        Assert.assertEquals(anotherValue, config.getProperty(someKey, null));
        TimeUnit.MILLISECONDS.sleep((pollTimeoutInMS * 10));
        Assert.assertEquals(someValue, someOtherConfig.getProperty(someKey, null));
    }

    @Test
    public void testLongPollRefreshWithMultipleNamespacesAndMultipleNamespaceNotified() throws Exception {
        final String someKey = "someKey";
        final String someValue = "someValue";
        final String anotherValue = "anotherValue";
        long someNotificationId = 1;
        long pollTimeoutInMS = 50;
        Map<String, String> configurations = Maps.newHashMap();
        configurations.put(someKey, someValue);
        ApolloConfig apolloConfig = assembleApolloConfig(configurations);
        ContextHandler configHandler = mockConfigServerHandler(SC_OK, apolloConfig);
        ContextHandler pollHandler = mockPollNotificationHandler(pollTimeoutInMS, SC_OK, Lists.newArrayList(new ApolloConfigNotification(apolloConfig.getNamespaceName(), someNotificationId), new ApolloConfigNotification(someOtherNamespace, someNotificationId)), false);
        startServerWithHandlers(configHandler, pollHandler);
        Config config = ConfigService.getAppConfig();
        Config someOtherConfig = ConfigService.getConfig(someOtherNamespace);
        Assert.assertEquals(someValue, config.getProperty(someKey, null));
        Assert.assertEquals(someValue, someOtherConfig.getProperty(someKey, null));
        final SettableFuture<Boolean> longPollFinished = SettableFuture.create();
        final SettableFuture<Boolean> someOtherNamespacelongPollFinished = SettableFuture.create();
        config.addChangeListener(new ConfigChangeListener() {
            @Override
            public void onChange(ConfigChangeEvent changeEvent) {
                longPollFinished.set(true);
            }
        });
        someOtherConfig.addChangeListener(new ConfigChangeListener() {
            @Override
            public void onChange(ConfigChangeEvent changeEvent) {
                someOtherNamespacelongPollFinished.set(true);
            }
        });
        apolloConfig.getConfigurations().put(someKey, anotherValue);
        longPollFinished.get(5000, TimeUnit.MILLISECONDS);
        someOtherNamespacelongPollFinished.get(5000, TimeUnit.MILLISECONDS);
        Assert.assertEquals(anotherValue, config.getProperty(someKey, null));
        Assert.assertEquals(anotherValue, someOtherConfig.getProperty(someKey, null));
    }
}

