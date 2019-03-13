package com.ctrip.framework.apollo.configservice.controller;


import ConfigConsts.CLUSTER_NAMESPACE_SEPARATOR;
import HttpStatus.OK;
import Topics.APOLLO_RELEASE_TOPIC;
import com.ctrip.framework.apollo.biz.config.BizConfig;
import com.ctrip.framework.apollo.biz.entity.ReleaseMessage;
import com.ctrip.framework.apollo.biz.utils.EntityManagerUtil;
import com.ctrip.framework.apollo.configservice.service.ReleaseMessageServiceWithCache;
import com.ctrip.framework.apollo.configservice.util.NamespaceUtil;
import com.ctrip.framework.apollo.configservice.util.WatchKeysUtil;
import com.ctrip.framework.apollo.configservice.wrapper.DeferredResultWrapper;
import com.ctrip.framework.apollo.core.dto.ApolloConfigNotification;
import com.ctrip.framework.apollo.core.dto.ApolloNotificationMessages;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;


/**
 *
 *
 * @author Jason Song(song_s@ctrip.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class NotificationControllerV2Test {
    private NotificationControllerV2 controller;

    private String someAppId;

    private String someCluster;

    private String defaultCluster;

    private String defaultNamespace;

    private String somePublicNamespace;

    private String someDataCenter;

    private long someNotificationId;

    private String someClientIp;

    @Mock
    private ReleaseMessageServiceWithCache releaseMessageService;

    @Mock
    private EntityManagerUtil entityManagerUtil;

    @Mock
    private NamespaceUtil namespaceUtil;

    @Mock
    private WatchKeysUtil watchKeysUtil;

    @Mock
    private BizConfig bizConfig;

    private Gson gson;

    private Multimap<String, DeferredResultWrapper> deferredResults;

    @Test
    public void testPollNotificationWithDefaultNamespace() throws Exception {
        String someWatchKey = "someKey";
        String anotherWatchKey = "anotherKey";
        Multimap<String, String> watchKeysMap = assembleMultiMap(defaultNamespace, Lists.newArrayList(someWatchKey, anotherWatchKey));
        String notificationAsString = transformApolloConfigNotificationsToString(defaultNamespace, someNotificationId);
        Mockito.when(watchKeysUtil.assembleAllWatchKeys(someAppId, someCluster, Sets.newHashSet(defaultNamespace), someDataCenter)).thenReturn(watchKeysMap);
        DeferredResult<ResponseEntity<List<ApolloConfigNotification>>> deferredResult = controller.pollNotification(someAppId, someCluster, notificationAsString, someDataCenter, someClientIp);
        Assert.assertEquals(watchKeysMap.size(), deferredResults.size());
        assertWatchKeys(watchKeysMap, deferredResult);
    }

    @Test
    public void testPollNotificationWithDefaultNamespaceAsFile() throws Exception {
        String namespace = String.format("%s.%s", defaultNamespace, "properties");
        Mockito.when(namespaceUtil.filterNamespaceName(namespace)).thenReturn(defaultNamespace);
        String someWatchKey = "someKey";
        String anotherWatchKey = "anotherKey";
        Multimap<String, String> watchKeysMap = assembleMultiMap(defaultNamespace, Lists.newArrayList(someWatchKey, anotherWatchKey));
        String notificationAsString = transformApolloConfigNotificationsToString(namespace, someNotificationId);
        Mockito.when(watchKeysUtil.assembleAllWatchKeys(someAppId, someCluster, Sets.newHashSet(defaultNamespace), someDataCenter)).thenReturn(watchKeysMap);
        DeferredResult<ResponseEntity<List<ApolloConfigNotification>>> deferredResult = controller.pollNotification(someAppId, someCluster, notificationAsString, someDataCenter, someClientIp);
        Assert.assertEquals(watchKeysMap.size(), deferredResults.size());
        assertWatchKeys(watchKeysMap, deferredResult);
    }

    @Test
    public void testPollNotificationWithMultipleNamespaces() throws Exception {
        String defaultNamespaceAsFile = (defaultNamespace) + ".properties";
        String somePublicNamespaceAsFile = (somePublicNamespace) + ".xml";
        Mockito.when(namespaceUtil.filterNamespaceName(defaultNamespaceAsFile)).thenReturn(defaultNamespace);
        Mockito.when(namespaceUtil.filterNamespaceName(somePublicNamespaceAsFile)).thenReturn(somePublicNamespaceAsFile);
        Mockito.when(namespaceUtil.normalizeNamespace(someAppId, somePublicNamespaceAsFile)).thenReturn(somePublicNamespaceAsFile);
        String someWatchKey = "someKey";
        String anotherWatchKey = "anotherKey";
        String somePublicWatchKey = "somePublicWatchKey";
        String somePublicFileWatchKey = "somePublicFileWatchKey";
        Multimap<String, String> watchKeysMap = assembleMultiMap(defaultNamespace, Lists.newArrayList(someWatchKey, anotherWatchKey));
        watchKeysMap.putAll(assembleMultiMap(somePublicNamespace, Lists.newArrayList(somePublicWatchKey)));
        watchKeysMap.putAll(assembleMultiMap(somePublicNamespaceAsFile, Lists.newArrayList(somePublicFileWatchKey)));
        String notificationAsString = transformApolloConfigNotificationsToString(defaultNamespaceAsFile, someNotificationId, somePublicNamespace, someNotificationId, somePublicNamespaceAsFile, someNotificationId);
        Mockito.when(watchKeysUtil.assembleAllWatchKeys(someAppId, someCluster, Sets.newHashSet(defaultNamespace, somePublicNamespace, somePublicNamespaceAsFile), someDataCenter)).thenReturn(watchKeysMap);
        DeferredResult<ResponseEntity<List<ApolloConfigNotification>>> deferredResult = controller.pollNotification(someAppId, someCluster, notificationAsString, someDataCenter, someClientIp);
        Assert.assertEquals(watchKeysMap.size(), deferredResults.size());
        assertWatchKeys(watchKeysMap, deferredResult);
        Mockito.verify(watchKeysUtil, Mockito.times(1)).assembleAllWatchKeys(someAppId, someCluster, Sets.newHashSet(defaultNamespace, somePublicNamespace, somePublicNamespaceAsFile), someDataCenter);
    }

    @Test
    public void testPollNotificationWithMultipleNamespaceWithNotificationIdOutDated() throws Exception {
        String someWatchKey = "someKey";
        String anotherWatchKey = Joiner.on(CLUSTER_NAMESPACE_SEPARATOR).join(someAppId, someCluster, somePublicNamespace);
        String yetAnotherWatchKey = Joiner.on(CLUSTER_NAMESPACE_SEPARATOR).join(someAppId, defaultCluster, somePublicNamespace);
        long notificationId = (someNotificationId) + 1;
        long yetAnotherNotificationId = someNotificationId;
        Multimap<String, String> watchKeysMap = assembleMultiMap(defaultNamespace, Lists.newArrayList(someWatchKey));
        watchKeysMap.putAll(assembleMultiMap(somePublicNamespace, Lists.newArrayList(anotherWatchKey, yetAnotherWatchKey)));
        Mockito.when(watchKeysUtil.assembleAllWatchKeys(someAppId, someCluster, Sets.newHashSet(defaultNamespace, somePublicNamespace), someDataCenter)).thenReturn(watchKeysMap);
        ReleaseMessage someReleaseMessage = Mockito.mock(ReleaseMessage.class);
        Mockito.when(someReleaseMessage.getId()).thenReturn(notificationId);
        Mockito.when(someReleaseMessage.getMessage()).thenReturn(anotherWatchKey);
        ReleaseMessage yetAnotherReleaseMessage = Mockito.mock(ReleaseMessage.class);
        Mockito.when(yetAnotherReleaseMessage.getId()).thenReturn(yetAnotherNotificationId);
        Mockito.when(yetAnotherReleaseMessage.getMessage()).thenReturn(yetAnotherWatchKey);
        Mockito.when(releaseMessageService.findLatestReleaseMessagesGroupByMessages(Sets.newHashSet(watchKeysMap.values()))).thenReturn(Lists.newArrayList(someReleaseMessage, yetAnotherReleaseMessage));
        String notificationAsString = transformApolloConfigNotificationsToString(defaultNamespace, someNotificationId, somePublicNamespace, someNotificationId);
        DeferredResult<ResponseEntity<List<ApolloConfigNotification>>> deferredResult = controller.pollNotification(someAppId, someCluster, notificationAsString, someDataCenter, someClientIp);
        ResponseEntity<List<ApolloConfigNotification>> result = ((ResponseEntity<List<ApolloConfigNotification>>) (deferredResult.getResult()));
        Assert.assertEquals(OK, result.getStatusCode());
        Assert.assertEquals(1, result.getBody().size());
        Assert.assertEquals(somePublicNamespace, result.getBody().get(0).getNamespaceName());
        Assert.assertEquals(notificationId, result.getBody().get(0).getNotificationId());
        ApolloNotificationMessages notificationMessages = result.getBody().get(0).getMessages();
        Assert.assertEquals(2, notificationMessages.getDetails().size());
        Assert.assertEquals(notificationId, notificationMessages.get(anotherWatchKey).longValue());
        Assert.assertEquals(yetAnotherNotificationId, notificationMessages.get(yetAnotherWatchKey).longValue());
    }

    @Test
    public void testPollNotificationWithMultipleNamespacesAndHandleMessage() throws Exception {
        String someWatchKey = "someKey";
        String anotherWatchKey = Joiner.on(CLUSTER_NAMESPACE_SEPARATOR).join(someAppId, someCluster, somePublicNamespace);
        Multimap<String, String> watchKeysMap = assembleMultiMap(defaultNamespace, Lists.newArrayList(someWatchKey));
        watchKeysMap.putAll(assembleMultiMap(somePublicNamespace, Lists.newArrayList(anotherWatchKey)));
        Mockito.when(watchKeysUtil.assembleAllWatchKeys(someAppId, someCluster, Sets.newHashSet(defaultNamespace, somePublicNamespace), someDataCenter)).thenReturn(watchKeysMap);
        String notificationAsString = transformApolloConfigNotificationsToString(defaultNamespace, someNotificationId, somePublicNamespace, someNotificationId);
        DeferredResult<ResponseEntity<List<ApolloConfigNotification>>> deferredResult = controller.pollNotification(someAppId, someCluster, notificationAsString, someDataCenter, someClientIp);
        Assert.assertEquals(watchKeysMap.size(), deferredResults.size());
        long someId = 1;
        ReleaseMessage someReleaseMessage = new ReleaseMessage(anotherWatchKey);
        someReleaseMessage.setId(someId);
        controller.handleMessage(someReleaseMessage, APOLLO_RELEASE_TOPIC);
        ResponseEntity<List<ApolloConfigNotification>> response = ((ResponseEntity<List<ApolloConfigNotification>>) (deferredResult.getResult()));
        Assert.assertEquals(1, response.getBody().size());
        ApolloConfigNotification notification = response.getBody().get(0);
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertEquals(somePublicNamespace, notification.getNamespaceName());
        Assert.assertEquals(someId, notification.getNotificationId());
        ApolloNotificationMessages notificationMessages = response.getBody().get(0).getMessages();
        Assert.assertEquals(1, notificationMessages.getDetails().size());
        Assert.assertEquals(someId, notificationMessages.get(anotherWatchKey).longValue());
    }

    @Test
    public void testPollNotificationWithHandleMessageInBatch() throws Exception {
        String someWatchKey = Joiner.on(CLUSTER_NAMESPACE_SEPARATOR).join(someAppId, someCluster, defaultNamespace);
        int someBatch = 1;
        int someBatchInterval = 10;
        Multimap<String, String> watchKeysMap = assembleMultiMap(defaultNamespace, Lists.newArrayList(someWatchKey));
        String notificationAsString = transformApolloConfigNotificationsToString(defaultNamespace, someNotificationId);
        Mockito.when(watchKeysUtil.assembleAllWatchKeys(someAppId, someCluster, Sets.newHashSet(defaultNamespace), someDataCenter)).thenReturn(watchKeysMap);
        Mockito.when(bizConfig.releaseMessageNotificationBatch()).thenReturn(someBatch);
        Mockito.when(bizConfig.releaseMessageNotificationBatchIntervalInMilli()).thenReturn(someBatchInterval);
        DeferredResult<ResponseEntity<List<ApolloConfigNotification>>> deferredResult = controller.pollNotification(someAppId, someCluster, notificationAsString, someDataCenter, someClientIp);
        DeferredResult<ResponseEntity<List<ApolloConfigNotification>>> anotherDeferredResult = controller.pollNotification(someAppId, someCluster, notificationAsString, someDataCenter, someClientIp);
        long someId = 1;
        ReleaseMessage someReleaseMessage = new ReleaseMessage(someWatchKey);
        someReleaseMessage.setId(someId);
        controller.handleMessage(someReleaseMessage, APOLLO_RELEASE_TOPIC);
        // in batch mode, at most one of them should have result
        Assert.assertFalse(((deferredResult.hasResult()) && (anotherDeferredResult.hasResult())));
        TimeUnit.MILLISECONDS.sleep((someBatchInterval * 10));
        // now both of them should have result
        Assert.assertTrue(((deferredResult.hasResult()) && (anotherDeferredResult.hasResult())));
    }
}

