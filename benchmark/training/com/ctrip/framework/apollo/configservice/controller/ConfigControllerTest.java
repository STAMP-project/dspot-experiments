package com.ctrip.framework.apollo.configservice.controller;


import ConfigConsts.CLUSTER_NAMESPACE_SEPARATOR;
import HttpServletResponse.SC_NOT_FOUND;
import HttpServletResponse.SC_NOT_MODIFIED;
import com.ctrip.framework.apollo.biz.entity.Release;
import com.ctrip.framework.apollo.common.entity.AppNamespace;
import com.ctrip.framework.apollo.configservice.service.AppNamespaceServiceWithCache;
import com.ctrip.framework.apollo.configservice.service.config.ConfigService;
import com.ctrip.framework.apollo.configservice.util.InstanceConfigAuditUtil;
import com.ctrip.framework.apollo.configservice.util.NamespaceUtil;
import com.ctrip.framework.apollo.core.ConfigConsts;
import com.ctrip.framework.apollo.core.dto.ApolloConfig;
import com.ctrip.framework.apollo.core.dto.ApolloNotificationMessages;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 *
 * @author Jason Song(song_s@ctrip.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class ConfigControllerTest {
    private ConfigController configController;

    @Mock
    private ConfigService configService;

    @Mock
    private AppNamespaceServiceWithCache appNamespaceService;

    private String someAppId;

    private String someClusterName;

    private String defaultClusterName;

    private String defaultNamespaceName;

    private String somePublicNamespaceName;

    private String someDataCenter;

    private String someClientIp;

    private String someMessagesAsString;

    @Mock
    private ApolloNotificationMessages someNotificationMessages;

    @Mock
    private Release someRelease;

    @Mock
    private Release somePublicRelease;

    @Mock
    private NamespaceUtil namespaceUtil;

    @Mock
    private InstanceConfigAuditUtil instanceConfigAuditUtil;

    @Mock
    private HttpServletRequest someRequest;

    private Gson gson = new Gson();

    @Test
    public void testQueryConfig() throws Exception {
        String someClientSideReleaseKey = "1";
        String someServerSideNewReleaseKey = "2";
        HttpServletResponse someResponse = Mockito.mock(HttpServletResponse.class);
        Mockito.when(configService.loadConfig(someAppId, someClientIp, someAppId, someClusterName, defaultNamespaceName, someDataCenter, someNotificationMessages)).thenReturn(someRelease);
        Mockito.when(someRelease.getReleaseKey()).thenReturn(someServerSideNewReleaseKey);
        Mockito.when(someRelease.getNamespaceName()).thenReturn(defaultNamespaceName);
        ApolloConfig result = configController.queryConfig(someAppId, someClusterName, defaultNamespaceName, someDataCenter, someClientSideReleaseKey, someClientIp, someMessagesAsString, someRequest, someResponse);
        Mockito.verify(configService, Mockito.times(1)).loadConfig(someAppId, someClientIp, someAppId, someClusterName, defaultNamespaceName, someDataCenter, someNotificationMessages);
        Assert.assertEquals(someAppId, result.getAppId());
        Assert.assertEquals(someClusterName, result.getCluster());
        Assert.assertEquals(defaultNamespaceName, result.getNamespaceName());
        Assert.assertEquals(someServerSideNewReleaseKey, result.getReleaseKey());
        Mockito.verify(instanceConfigAuditUtil, Mockito.times(1)).audit(someAppId, someClusterName, someDataCenter, someClientIp, someAppId, someClusterName, defaultNamespaceName, someServerSideNewReleaseKey);
    }

    @Test
    public void testQueryConfigFile() throws Exception {
        String someClientSideReleaseKey = "1";
        String someServerSideNewReleaseKey = "2";
        HttpServletResponse someResponse = Mockito.mock(HttpServletResponse.class);
        String someNamespaceName = String.format("%s.%s", defaultClusterName, "properties");
        Mockito.when(configService.loadConfig(someAppId, someClientIp, someAppId, someClusterName, defaultNamespaceName, someDataCenter, someNotificationMessages)).thenReturn(someRelease);
        Mockito.when(someRelease.getReleaseKey()).thenReturn(someServerSideNewReleaseKey);
        Mockito.when(namespaceUtil.filterNamespaceName(someNamespaceName)).thenReturn(defaultNamespaceName);
        Mockito.when(namespaceUtil.normalizeNamespace(someAppId, defaultNamespaceName)).thenReturn(defaultNamespaceName);
        ApolloConfig result = configController.queryConfig(someAppId, someClusterName, someNamespaceName, someDataCenter, someClientSideReleaseKey, someClientIp, someMessagesAsString, someRequest, someResponse);
        Mockito.verify(configService, Mockito.times(1)).loadConfig(someAppId, someClientIp, someAppId, someClusterName, defaultNamespaceName, someDataCenter, someNotificationMessages);
        Assert.assertEquals(someAppId, result.getAppId());
        Assert.assertEquals(someClusterName, result.getCluster());
        Assert.assertEquals(someNamespaceName, result.getNamespaceName());
        Assert.assertEquals(someServerSideNewReleaseKey, result.getReleaseKey());
    }

    @Test
    public void testQueryConfigFileWithPrivateNamespace() throws Exception {
        String someClientSideReleaseKey = "1";
        String someServerSideNewReleaseKey = "2";
        String somePrivateNamespace = "datasource";
        HttpServletResponse someResponse = Mockito.mock(HttpServletResponse.class);
        String somePrivateNamespaceName = String.format("%s.%s", somePrivateNamespace, "xml");
        AppNamespace appNamespace = Mockito.mock(AppNamespace.class);
        Mockito.when(configService.loadConfig(someAppId, someClientIp, someAppId, someClusterName, somePrivateNamespace, someDataCenter, someNotificationMessages)).thenReturn(someRelease);
        Mockito.when(someRelease.getReleaseKey()).thenReturn(someServerSideNewReleaseKey);
        Mockito.when(namespaceUtil.filterNamespaceName(somePrivateNamespaceName)).thenReturn(somePrivateNamespace);
        Mockito.when(namespaceUtil.normalizeNamespace(someAppId, somePrivateNamespace)).thenReturn(somePrivateNamespace);
        Mockito.when(appNamespaceService.findByAppIdAndNamespace(someAppId, somePrivateNamespace)).thenReturn(appNamespace);
        ApolloConfig result = configController.queryConfig(someAppId, someClusterName, somePrivateNamespaceName, someDataCenter, someClientSideReleaseKey, someClientIp, someMessagesAsString, someRequest, someResponse);
        Assert.assertEquals(someAppId, result.getAppId());
        Assert.assertEquals(someClusterName, result.getCluster());
        Assert.assertEquals(somePrivateNamespaceName, result.getNamespaceName());
        Assert.assertEquals(someServerSideNewReleaseKey, result.getReleaseKey());
    }

    @Test
    public void testQueryConfigWithReleaseNotFound() throws Exception {
        String someClientSideReleaseKey = "1";
        HttpServletResponse someResponse = Mockito.mock(HttpServletResponse.class);
        Mockito.when(configService.loadConfig(someAppId, someClientIp, someAppId, someClusterName, defaultNamespaceName, someDataCenter, someNotificationMessages)).thenReturn(null);
        ApolloConfig result = configController.queryConfig(someAppId, someClusterName, defaultNamespaceName, someDataCenter, someClientSideReleaseKey, someClientIp, someMessagesAsString, someRequest, someResponse);
        Assert.assertNull(result);
        Mockito.verify(someResponse, Mockito.times(1)).sendError(ArgumentMatchers.eq(SC_NOT_FOUND), ArgumentMatchers.anyString());
    }

    @Test
    public void testQueryConfigWithApolloConfigNotModified() throws Exception {
        String someClientSideReleaseKey = "1";
        String someServerSideReleaseKey = someClientSideReleaseKey;
        HttpServletResponse someResponse = Mockito.mock(HttpServletResponse.class);
        Mockito.when(configService.loadConfig(someAppId, someClientIp, someAppId, someClusterName, defaultNamespaceName, someDataCenter, someNotificationMessages)).thenReturn(someRelease);
        Mockito.when(someRelease.getReleaseKey()).thenReturn(someServerSideReleaseKey);
        ApolloConfig result = configController.queryConfig(someAppId, someClusterName, defaultNamespaceName, someDataCenter, String.valueOf(someClientSideReleaseKey), someClientIp, someMessagesAsString, someRequest, someResponse);
        Assert.assertNull(result);
        Mockito.verify(someResponse, Mockito.times(1)).setStatus(SC_NOT_MODIFIED);
    }

    @Test
    public void testQueryConfigWithAppOwnNamespace() throws Exception {
        String someClientSideReleaseKey = "1";
        String someServerSideReleaseKey = "2";
        String someAppOwnNamespaceName = "someAppOwn";
        HttpServletResponse someResponse = Mockito.mock(HttpServletResponse.class);
        AppNamespace someAppOwnNamespace = assemblePublicAppNamespace(someAppId, someAppOwnNamespaceName);
        Mockito.when(configService.loadConfig(someAppId, someClientIp, someAppId, someClusterName, someAppOwnNamespaceName, someDataCenter, someNotificationMessages)).thenReturn(someRelease);
        Mockito.when(appNamespaceService.findPublicNamespaceByName(someAppOwnNamespaceName)).thenReturn(someAppOwnNamespace);
        Mockito.when(someRelease.getReleaseKey()).thenReturn(someServerSideReleaseKey);
        Mockito.when(namespaceUtil.filterNamespaceName(someAppOwnNamespaceName)).thenReturn(someAppOwnNamespaceName);
        Mockito.when(namespaceUtil.normalizeNamespace(someAppId, someAppOwnNamespaceName)).thenReturn(someAppOwnNamespaceName);
        ApolloConfig result = configController.queryConfig(someAppId, someClusterName, someAppOwnNamespaceName, someDataCenter, someClientSideReleaseKey, someClientIp, someMessagesAsString, someRequest, someResponse);
        Assert.assertEquals(someServerSideReleaseKey, result.getReleaseKey());
        Assert.assertEquals(someAppId, result.getAppId());
        Assert.assertEquals(someClusterName, result.getCluster());
        Assert.assertEquals(someAppOwnNamespaceName, result.getNamespaceName());
        Assert.assertEquals("foo", result.getConfigurations().get("apollo.bar"));
    }

    @Test
    public void testQueryConfigWithPubicNamespaceAndNoAppOverride() throws Exception {
        String someClientSideReleaseKey = "1";
        String someServerSideReleaseKey = "2";
        HttpServletResponse someResponse = Mockito.mock(HttpServletResponse.class);
        String somePublicAppId = "somePublicAppId";
        String somePublicClusterName = "somePublicClusterName";
        AppNamespace somePublicAppNamespace = assemblePublicAppNamespace(somePublicAppId, somePublicNamespaceName);
        Mockito.when(configService.loadConfig(someAppId, someClientIp, someAppId, someClusterName, somePublicNamespaceName, someDataCenter, someNotificationMessages)).thenReturn(null);
        Mockito.when(appNamespaceService.findPublicNamespaceByName(somePublicNamespaceName)).thenReturn(somePublicAppNamespace);
        Mockito.when(configService.loadConfig(someAppId, someClientIp, somePublicAppId, someClusterName, somePublicNamespaceName, someDataCenter, someNotificationMessages)).thenReturn(somePublicRelease);
        Mockito.when(somePublicRelease.getReleaseKey()).thenReturn(someServerSideReleaseKey);
        Mockito.when(somePublicRelease.getAppId()).thenReturn(somePublicAppId);
        Mockito.when(somePublicRelease.getClusterName()).thenReturn(somePublicClusterName);
        Mockito.when(somePublicRelease.getNamespaceName()).thenReturn(somePublicNamespaceName);
        ApolloConfig result = configController.queryConfig(someAppId, someClusterName, somePublicNamespaceName, someDataCenter, someClientSideReleaseKey, someClientIp, someMessagesAsString, someRequest, someResponse);
        Assert.assertEquals(someServerSideReleaseKey, result.getReleaseKey());
        Assert.assertEquals(someAppId, result.getAppId());
        Assert.assertEquals(someClusterName, result.getCluster());
        Assert.assertEquals(somePublicNamespaceName, result.getNamespaceName());
        Assert.assertEquals("foo", result.getConfigurations().get("apollo.public.bar"));
        Mockito.verify(instanceConfigAuditUtil, Mockito.times(1)).audit(someAppId, someClusterName, someDataCenter, someClientIp, somePublicAppId, somePublicClusterName, somePublicNamespaceName, someServerSideReleaseKey);
    }

    @Test
    public void testQueryConfigFileWithPublicNamespaceAndNoAppOverride() throws Exception {
        String someClientSideReleaseKey = "1";
        String someServerSideReleaseKey = "2";
        HttpServletResponse someResponse = Mockito.mock(HttpServletResponse.class);
        String somePublicAppId = "somePublicAppId";
        String someNamespace = String.format("%s.%s", somePublicNamespaceName, "properties");
        AppNamespace somePublicAppNamespace = assemblePublicAppNamespace(somePublicAppId, somePublicNamespaceName);
        Mockito.when(configService.loadConfig(someAppId, someClientIp, someAppId, someClusterName, somePublicNamespaceName, someDataCenter, someNotificationMessages)).thenReturn(null);
        Mockito.when(appNamespaceService.findPublicNamespaceByName(somePublicNamespaceName)).thenReturn(somePublicAppNamespace);
        Mockito.when(configService.loadConfig(someAppId, someClientIp, somePublicAppId, someClusterName, somePublicNamespaceName, someDataCenter, someNotificationMessages)).thenReturn(somePublicRelease);
        Mockito.when(somePublicRelease.getReleaseKey()).thenReturn(someServerSideReleaseKey);
        Mockito.when(namespaceUtil.filterNamespaceName(someNamespace)).thenReturn(somePublicNamespaceName);
        Mockito.when(namespaceUtil.normalizeNamespace(someAppId, somePublicNamespaceName)).thenReturn(somePublicNamespaceName);
        Mockito.when(appNamespaceService.findByAppIdAndNamespace(someAppId, somePublicNamespaceName)).thenReturn(null);
        ApolloConfig result = configController.queryConfig(someAppId, someClusterName, someNamespace, someDataCenter, someClientSideReleaseKey, someClientIp, someMessagesAsString, someRequest, someResponse);
        Assert.assertEquals(someServerSideReleaseKey, result.getReleaseKey());
        Assert.assertEquals(someAppId, result.getAppId());
        Assert.assertEquals(someClusterName, result.getCluster());
        Assert.assertEquals(someNamespace, result.getNamespaceName());
        Assert.assertEquals("foo", result.getConfigurations().get("apollo.public.bar"));
    }

    @Test
    public void testQueryConfigWithPublicNamespaceAndAppOverride() throws Exception {
        String someAppSideReleaseKey = "1";
        String somePublicAppSideReleaseKey = "2";
        HttpServletResponse someResponse = Mockito.mock(HttpServletResponse.class);
        String somePublicAppId = "somePublicAppId";
        AppNamespace somePublicAppNamespace = assemblePublicAppNamespace(somePublicAppId, somePublicNamespaceName);
        Mockito.when(someRelease.getConfigurations()).thenReturn("{\"apollo.public.foo\": \"foo-override\"}");
        Mockito.when(somePublicRelease.getConfigurations()).thenReturn("{\"apollo.public.foo\": \"foo\", \"apollo.public.bar\": \"bar\"}");
        Mockito.when(configService.loadConfig(someAppId, someClientIp, someAppId, someClusterName, somePublicNamespaceName, someDataCenter, someNotificationMessages)).thenReturn(someRelease);
        Mockito.when(someRelease.getReleaseKey()).thenReturn(someAppSideReleaseKey);
        Mockito.when(someRelease.getNamespaceName()).thenReturn(somePublicNamespaceName);
        Mockito.when(appNamespaceService.findPublicNamespaceByName(somePublicNamespaceName)).thenReturn(somePublicAppNamespace);
        Mockito.when(configService.loadConfig(someAppId, someClientIp, somePublicAppId, someClusterName, somePublicNamespaceName, someDataCenter, someNotificationMessages)).thenReturn(somePublicRelease);
        Mockito.when(somePublicRelease.getReleaseKey()).thenReturn(somePublicAppSideReleaseKey);
        Mockito.when(somePublicRelease.getAppId()).thenReturn(somePublicAppId);
        Mockito.when(somePublicRelease.getClusterName()).thenReturn(someDataCenter);
        Mockito.when(somePublicRelease.getNamespaceName()).thenReturn(somePublicNamespaceName);
        ApolloConfig result = configController.queryConfig(someAppId, someClusterName, somePublicNamespaceName, someDataCenter, someAppSideReleaseKey, someClientIp, someMessagesAsString, someRequest, someResponse);
        Assert.assertEquals(Joiner.on(CLUSTER_NAMESPACE_SEPARATOR).join(someAppSideReleaseKey, somePublicAppSideReleaseKey), result.getReleaseKey());
        Assert.assertEquals(someAppId, result.getAppId());
        Assert.assertEquals(someClusterName, result.getCluster());
        Assert.assertEquals(somePublicNamespaceName, result.getNamespaceName());
        Assert.assertEquals("foo-override", result.getConfigurations().get("apollo.public.foo"));
        Assert.assertEquals("bar", result.getConfigurations().get("apollo.public.bar"));
        Mockito.verify(instanceConfigAuditUtil, Mockito.times(1)).audit(someAppId, someClusterName, someDataCenter, someClientIp, someAppId, someClusterName, somePublicNamespaceName, someAppSideReleaseKey);
        Mockito.verify(instanceConfigAuditUtil, Mockito.times(1)).audit(someAppId, someClusterName, someDataCenter, someClientIp, somePublicAppId, someDataCenter, somePublicNamespaceName, somePublicAppSideReleaseKey);
    }

    @Test
    public void testMergeConfigurations() throws Exception {
        Gson gson = new Gson();
        String key1 = "key1";
        String value1 = "value1";
        String anotherValue1 = "anotherValue1";
        String key2 = "key2";
        String value2 = "value2";
        Map<String, String> config = ImmutableMap.of(key1, anotherValue1);
        Map<String, String> anotherConfig = ImmutableMap.of(key1, value1, key2, value2);
        Release releaseWithHighPriority = new Release();
        releaseWithHighPriority.setConfigurations(gson.toJson(config));
        Release releaseWithLowPriority = new Release();
        releaseWithLowPriority.setConfigurations(gson.toJson(anotherConfig));
        Map<String, String> result = configController.mergeReleaseConfigurations(Lists.newArrayList(releaseWithHighPriority, releaseWithLowPriority));
        Assert.assertEquals(2, result.keySet().size());
        Assert.assertEquals(anotherValue1, result.get(key1));
        Assert.assertEquals(value2, result.get(key2));
    }

    @Test(expected = JsonSyntaxException.class)
    public void testTransformConfigurationToMapFailed() throws Exception {
        String someInvalidConfiguration = "xxx";
        Release someRelease = new Release();
        someRelease.setConfigurations(someInvalidConfiguration);
        configController.mergeReleaseConfigurations(Lists.newArrayList(someRelease));
    }

    @Test
    public void testQueryConfigForNoAppIdPlaceHolder() throws Exception {
        String someClientSideReleaseKey = "1";
        HttpServletResponse someResponse = Mockito.mock(HttpServletResponse.class);
        String appId = ConfigConsts.NO_APPID_PLACEHOLDER;
        ApolloConfig result = configController.queryConfig(appId, someClusterName, defaultNamespaceName, someDataCenter, someClientSideReleaseKey, someClientIp, someMessagesAsString, someRequest, someResponse);
        Mockito.verify(configService, Mockito.never()).loadConfig(appId, someClientIp, someAppId, someClusterName, defaultNamespaceName, someDataCenter, someNotificationMessages);
        Mockito.verify(appNamespaceService, Mockito.never()).findPublicNamespaceByName(defaultNamespaceName);
        Assert.assertNull(result);
        Mockito.verify(someResponse, Mockito.times(1)).sendError(ArgumentMatchers.eq(SC_NOT_FOUND), ArgumentMatchers.anyString());
    }

    @Test
    public void testQueryConfigForNoAppIdPlaceHolderWithPublicNamespace() throws Exception {
        String someClientSideReleaseKey = "1";
        String someServerSideReleaseKey = "2";
        HttpServletResponse someResponse = Mockito.mock(HttpServletResponse.class);
        String somePublicAppId = "somePublicAppId";
        AppNamespace somePublicAppNamespace = assemblePublicAppNamespace(somePublicAppId, somePublicNamespaceName);
        String appId = ConfigConsts.NO_APPID_PLACEHOLDER;
        Mockito.when(appNamespaceService.findPublicNamespaceByName(somePublicNamespaceName)).thenReturn(somePublicAppNamespace);
        Mockito.when(configService.loadConfig(appId, someClientIp, somePublicAppId, someClusterName, somePublicNamespaceName, someDataCenter, someNotificationMessages)).thenReturn(somePublicRelease);
        Mockito.when(somePublicRelease.getReleaseKey()).thenReturn(someServerSideReleaseKey);
        Mockito.when(namespaceUtil.normalizeNamespace(appId, somePublicNamespaceName)).thenReturn(somePublicNamespaceName);
        ApolloConfig result = configController.queryConfig(appId, someClusterName, somePublicNamespaceName, someDataCenter, someClientSideReleaseKey, someClientIp, someMessagesAsString, someRequest, someResponse);
        Mockito.verify(configService, Mockito.never()).loadConfig(appId, someClientIp, appId, someClusterName, somePublicNamespaceName, someDataCenter, someNotificationMessages);
        Assert.assertEquals(someServerSideReleaseKey, result.getReleaseKey());
        Assert.assertEquals(appId, result.getAppId());
        Assert.assertEquals(someClusterName, result.getCluster());
        Assert.assertEquals(somePublicNamespaceName, result.getNamespaceName());
        Assert.assertEquals("foo", result.getConfigurations().get("apollo.public.bar"));
    }

    @Test
    public void testTransformMessages() throws Exception {
        String someKey = "someKey";
        long someNotificationId = 1;
        String anotherKey = "anotherKey";
        long anotherNotificationId = 2;
        ApolloNotificationMessages notificationMessages = new ApolloNotificationMessages();
        notificationMessages.put(someKey, someNotificationId);
        notificationMessages.put(anotherKey, anotherNotificationId);
        String someMessagesAsString = gson.toJson(notificationMessages);
        ApolloNotificationMessages result = configController.transformMessages(someMessagesAsString);
        Assert.assertEquals(notificationMessages.getDetails(), result.getDetails());
    }

    @Test
    public void testTransformInvalidMessages() throws Exception {
        String someInvalidMessages = "someInvalidMessages";
        Assert.assertNull(configController.transformMessages(someInvalidMessages));
    }
}

