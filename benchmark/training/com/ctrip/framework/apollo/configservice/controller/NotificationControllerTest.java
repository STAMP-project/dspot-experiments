package com.ctrip.framework.apollo.configservice.controller;


import ConfigConsts.CLUSTER_NAMESPACE_SEPARATOR;
import HttpStatus.OK;
import Topics.APOLLO_RELEASE_TOPIC;
import com.ctrip.framework.apollo.biz.entity.ReleaseMessage;
import com.ctrip.framework.apollo.biz.utils.EntityManagerUtil;
import com.ctrip.framework.apollo.configservice.service.ReleaseMessageServiceWithCache;
import com.ctrip.framework.apollo.configservice.util.NamespaceUtil;
import com.ctrip.framework.apollo.configservice.util.WatchKeysUtil;
import com.ctrip.framework.apollo.core.dto.ApolloConfigNotification;
import com.google.common.base.Joiner;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.util.Set;
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
public class NotificationControllerTest {
    private NotificationController controller;

    private String someAppId;

    private String someCluster;

    private String defaultNamespace;

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

    private Multimap<String, DeferredResult<ResponseEntity<ApolloConfigNotification>>> deferredResults;

    @Test
    public void testPollNotificationWithDefaultNamespace() throws Exception {
        String someWatchKey = "someKey";
        String anotherWatchKey = "anotherKey";
        Set<String> watchKeys = Sets.newHashSet(someWatchKey, anotherWatchKey);
        Mockito.when(watchKeysUtil.assembleAllWatchKeys(someAppId, someCluster, defaultNamespace, someDataCenter)).thenReturn(watchKeys);
        DeferredResult<ResponseEntity<ApolloConfigNotification>> deferredResult = controller.pollNotification(someAppId, someCluster, defaultNamespace, someDataCenter, someNotificationId, someClientIp);
        Assert.assertEquals(watchKeys.size(), deferredResults.size());
        for (String watchKey : watchKeys) {
            Assert.assertTrue(deferredResults.get(watchKey).contains(deferredResult));
        }
    }

    @Test
    public void testPollNotificationWithDefaultNamespaceAsFile() throws Exception {
        String namespace = String.format("%s.%s", defaultNamespace, "properties");
        Mockito.when(namespaceUtil.filterNamespaceName(namespace)).thenReturn(defaultNamespace);
        String someWatchKey = "someKey";
        String anotherWatchKey = "anotherKey";
        Set<String> watchKeys = Sets.newHashSet(someWatchKey, anotherWatchKey);
        Mockito.when(watchKeysUtil.assembleAllWatchKeys(someAppId, someCluster, defaultNamespace, someDataCenter)).thenReturn(watchKeys);
        DeferredResult<ResponseEntity<ApolloConfigNotification>> deferredResult = controller.pollNotification(someAppId, someCluster, namespace, someDataCenter, someNotificationId, someClientIp);
        Assert.assertEquals(watchKeys.size(), deferredResults.size());
        for (String watchKey : watchKeys) {
            Assert.assertTrue(deferredResults.get(watchKey).contains(deferredResult));
        }
    }

    @Test
    public void testPollNotificationWithSomeNamespaceAsFile() throws Exception {
        String namespace = String.format("someNamespace.xml");
        Mockito.when(namespaceUtil.filterNamespaceName(namespace)).thenReturn(namespace);
        String someWatchKey = "someKey";
        Set<String> watchKeys = Sets.newHashSet(someWatchKey);
        Mockito.when(watchKeysUtil.assembleAllWatchKeys(someAppId, someCluster, namespace, someDataCenter)).thenReturn(watchKeys);
        DeferredResult<ResponseEntity<ApolloConfigNotification>> deferredResult = controller.pollNotification(someAppId, someCluster, namespace, someDataCenter, someNotificationId, someClientIp);
        Assert.assertEquals(watchKeys.size(), deferredResults.size());
        for (String watchKey : watchKeys) {
            Assert.assertTrue(deferredResults.get(watchKey).contains(deferredResult));
        }
    }

    @Test
    public void testPollNotificationWithDefaultNamespaceWithNotificationIdOutDated() throws Exception {
        long notificationId = (someNotificationId) + 1;
        ReleaseMessage someReleaseMessage = Mockito.mock(ReleaseMessage.class);
        String someWatchKey = "someKey";
        Set<String> watchKeys = Sets.newHashSet(someWatchKey);
        Mockito.when(watchKeysUtil.assembleAllWatchKeys(someAppId, someCluster, defaultNamespace, someDataCenter)).thenReturn(watchKeys);
        Mockito.when(someReleaseMessage.getId()).thenReturn(notificationId);
        Mockito.when(releaseMessageService.findLatestReleaseMessageForMessages(watchKeys)).thenReturn(someReleaseMessage);
        DeferredResult<ResponseEntity<ApolloConfigNotification>> deferredResult = controller.pollNotification(someAppId, someCluster, defaultNamespace, someDataCenter, someNotificationId, someClientIp);
        ResponseEntity<ApolloConfigNotification> result = ((ResponseEntity<ApolloConfigNotification>) (deferredResult.getResult()));
        Assert.assertEquals(OK, result.getStatusCode());
        Assert.assertEquals(defaultNamespace, result.getBody().getNamespaceName());
        Assert.assertEquals(notificationId, result.getBody().getNotificationId());
    }

    @Test
    public void testPollNotificationWithDefaultNamespaceAndHandleMessage() throws Exception {
        String someWatchKey = "someKey";
        String anotherWatchKey = Joiner.on(CLUSTER_NAMESPACE_SEPARATOR).join(someAppId, someCluster, defaultNamespace);
        Set<String> watchKeys = Sets.newHashSet(someWatchKey, anotherWatchKey);
        Mockito.when(watchKeysUtil.assembleAllWatchKeys(someAppId, someCluster, defaultNamespace, someDataCenter)).thenReturn(watchKeys);
        DeferredResult<ResponseEntity<ApolloConfigNotification>> deferredResult = controller.pollNotification(someAppId, someCluster, defaultNamespace, someDataCenter, someNotificationId, someClientIp);
        long someId = 1;
        ReleaseMessage someReleaseMessage = new ReleaseMessage(anotherWatchKey);
        someReleaseMessage.setId(someId);
        controller.handleMessage(someReleaseMessage, APOLLO_RELEASE_TOPIC);
        ResponseEntity<ApolloConfigNotification> response = ((ResponseEntity<ApolloConfigNotification>) (deferredResult.getResult()));
        ApolloConfigNotification notification = response.getBody();
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertEquals(defaultNamespace, notification.getNamespaceName());
        Assert.assertEquals(someId, notification.getNotificationId());
    }
}

