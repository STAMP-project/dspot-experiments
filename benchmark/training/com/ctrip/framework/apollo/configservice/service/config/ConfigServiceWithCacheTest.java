package com.ctrip.framework.apollo.configservice.service.config;


import Topics.APOLLO_RELEASE_TOPIC;
import com.ctrip.framework.apollo.biz.entity.Release;
import com.ctrip.framework.apollo.biz.entity.ReleaseMessage;
import com.ctrip.framework.apollo.biz.service.ReleaseMessageService;
import com.ctrip.framework.apollo.biz.service.ReleaseService;
import com.ctrip.framework.apollo.core.dto.ApolloNotificationMessages;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 *
 * @author Jason Song(song_s@ctrip.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class ConfigServiceWithCacheTest {
    private ConfigServiceWithCache configServiceWithCache;

    @Mock
    private ReleaseService releaseService;

    @Mock
    private ReleaseMessageService releaseMessageService;

    @Mock
    private Release someRelease;

    @Mock
    private ReleaseMessage someReleaseMessage;

    private String someAppId;

    private String someClusterName;

    private String someNamespaceName;

    private String someKey;

    private long someNotificationId;

    private ApolloNotificationMessages someNotificationMessages;

    @Test
    public void testFindActiveOne() throws Exception {
        long someId = 1;
        Mockito.when(releaseService.findActiveOne(someId)).thenReturn(someRelease);
        Assert.assertEquals(someRelease, configServiceWithCache.findActiveOne(someId, someNotificationMessages));
        Mockito.verify(releaseService, Mockito.times(1)).findActiveOne(someId);
    }

    @Test
    public void testFindActiveOneWithSameIdMultipleTimes() throws Exception {
        long someId = 1;
        Mockito.when(releaseService.findActiveOne(someId)).thenReturn(someRelease);
        Assert.assertEquals(someRelease, configServiceWithCache.findActiveOne(someId, someNotificationMessages));
        Assert.assertEquals(someRelease, configServiceWithCache.findActiveOne(someId, someNotificationMessages));
        Assert.assertEquals(someRelease, configServiceWithCache.findActiveOne(someId, someNotificationMessages));
        Mockito.verify(releaseService, Mockito.times(1)).findActiveOne(someId);
    }

    @Test
    public void testFindActiveOneWithMultipleIdMultipleTimes() throws Exception {
        long someId = 1;
        long anotherId = 2;
        Release anotherRelease = Mockito.mock(Release.class);
        Mockito.when(releaseService.findActiveOne(someId)).thenReturn(someRelease);
        Mockito.when(releaseService.findActiveOne(anotherId)).thenReturn(anotherRelease);
        Assert.assertEquals(someRelease, configServiceWithCache.findActiveOne(someId, someNotificationMessages));
        Assert.assertEquals(someRelease, configServiceWithCache.findActiveOne(someId, someNotificationMessages));
        Assert.assertEquals(anotherRelease, configServiceWithCache.findActiveOne(anotherId, someNotificationMessages));
        Assert.assertEquals(anotherRelease, configServiceWithCache.findActiveOne(anotherId, someNotificationMessages));
        Mockito.verify(releaseService, Mockito.times(1)).findActiveOne(someId);
        Mockito.verify(releaseService, Mockito.times(1)).findActiveOne(anotherId);
    }

    @Test
    public void testFindActiveOneWithReleaseNotFoundMultipleTimes() throws Exception {
        long someId = 1;
        Mockito.when(releaseService.findActiveOne(someId)).thenReturn(null);
        Assert.assertNull(configServiceWithCache.findActiveOne(someId, someNotificationMessages));
        Assert.assertNull(configServiceWithCache.findActiveOne(someId, someNotificationMessages));
        Assert.assertNull(configServiceWithCache.findActiveOne(someId, someNotificationMessages));
        Mockito.verify(releaseService, Mockito.times(1)).findActiveOne(someId);
    }

    @Test
    public void testFindLatestActiveRelease() throws Exception {
        Mockito.when(releaseMessageService.findLatestReleaseMessageForMessages(Lists.newArrayList(someKey))).thenReturn(someReleaseMessage);
        Mockito.when(releaseService.findLatestActiveRelease(someAppId, someClusterName, someNamespaceName)).thenReturn(someRelease);
        Mockito.when(someReleaseMessage.getId()).thenReturn(someNotificationId);
        Release release = configServiceWithCache.findLatestActiveRelease(someAppId, someClusterName, someNamespaceName, someNotificationMessages);
        Release anotherRelease = configServiceWithCache.findLatestActiveRelease(someAppId, someClusterName, someNamespaceName, someNotificationMessages);
        int retryTimes = 100;
        for (int i = 0; i < retryTimes; i++) {
            configServiceWithCache.findLatestActiveRelease(someAppId, someClusterName, someNamespaceName, someNotificationMessages);
        }
        Assert.assertEquals(someRelease, release);
        Assert.assertEquals(someRelease, anotherRelease);
        Mockito.verify(releaseMessageService, Mockito.times(1)).findLatestReleaseMessageForMessages(Lists.newArrayList(someKey));
        Mockito.verify(releaseService, Mockito.times(1)).findLatestActiveRelease(someAppId, someClusterName, someNamespaceName);
    }

    @Test
    public void testFindLatestActiveReleaseWithReleaseNotFound() throws Exception {
        Mockito.when(releaseMessageService.findLatestReleaseMessageForMessages(Lists.newArrayList(someKey))).thenReturn(null);
        Mockito.when(releaseService.findLatestActiveRelease(someAppId, someClusterName, someNamespaceName)).thenReturn(null);
        Release release = configServiceWithCache.findLatestActiveRelease(someAppId, someClusterName, someNamespaceName, someNotificationMessages);
        Release anotherRelease = configServiceWithCache.findLatestActiveRelease(someAppId, someClusterName, someNamespaceName, someNotificationMessages);
        int retryTimes = 100;
        for (int i = 0; i < retryTimes; i++) {
            configServiceWithCache.findLatestActiveRelease(someAppId, someClusterName, someNamespaceName, someNotificationMessages);
        }
        Assert.assertNull(release);
        Assert.assertNull(anotherRelease);
        Mockito.verify(releaseMessageService, Mockito.times(1)).findLatestReleaseMessageForMessages(Lists.newArrayList(someKey));
        Mockito.verify(releaseService, Mockito.times(1)).findLatestActiveRelease(someAppId, someClusterName, someNamespaceName);
    }

    @Test
    public void testFindLatestActiveReleaseWithDirtyRelease() throws Exception {
        long someNewNotificationId = (someNotificationId) + 1;
        ReleaseMessage anotherReleaseMessage = Mockito.mock(ReleaseMessage.class);
        Release anotherRelease = Mockito.mock(Release.class);
        Mockito.when(releaseMessageService.findLatestReleaseMessageForMessages(Lists.newArrayList(someKey))).thenReturn(someReleaseMessage);
        Mockito.when(releaseService.findLatestActiveRelease(someAppId, someClusterName, someNamespaceName)).thenReturn(someRelease);
        Mockito.when(someReleaseMessage.getId()).thenReturn(someNotificationId);
        Release release = configServiceWithCache.findLatestActiveRelease(someAppId, someClusterName, someNamespaceName, someNotificationMessages);
        Mockito.when(releaseMessageService.findLatestReleaseMessageForMessages(Lists.newArrayList(someKey))).thenReturn(anotherReleaseMessage);
        Mockito.when(releaseService.findLatestActiveRelease(someAppId, someClusterName, someNamespaceName)).thenReturn(anotherRelease);
        Mockito.when(anotherReleaseMessage.getId()).thenReturn(someNewNotificationId);
        Release stillOldRelease = configServiceWithCache.findLatestActiveRelease(someAppId, someClusterName, someNamespaceName, someNotificationMessages);
        someNotificationMessages.put(someKey, someNewNotificationId);
        Release shouldBeNewRelease = configServiceWithCache.findLatestActiveRelease(someAppId, someClusterName, someNamespaceName, someNotificationMessages);
        Assert.assertEquals(someRelease, release);
        Assert.assertEquals(someRelease, stillOldRelease);
        Assert.assertEquals(anotherRelease, shouldBeNewRelease);
        Mockito.verify(releaseMessageService, Mockito.times(2)).findLatestReleaseMessageForMessages(Lists.newArrayList(someKey));
        Mockito.verify(releaseService, Mockito.times(2)).findLatestActiveRelease(someAppId, someClusterName, someNamespaceName);
    }

    @Test
    public void testFindLatestActiveReleaseWithReleaseMessageNotification() throws Exception {
        long someNewNotificationId = (someNotificationId) + 1;
        ReleaseMessage anotherReleaseMessage = Mockito.mock(ReleaseMessage.class);
        Release anotherRelease = Mockito.mock(Release.class);
        Mockito.when(releaseMessageService.findLatestReleaseMessageForMessages(Lists.newArrayList(someKey))).thenReturn(someReleaseMessage);
        Mockito.when(releaseService.findLatestActiveRelease(someAppId, someClusterName, someNamespaceName)).thenReturn(someRelease);
        Mockito.when(someReleaseMessage.getId()).thenReturn(someNotificationId);
        Release release = configServiceWithCache.findLatestActiveRelease(someAppId, someClusterName, someNamespaceName, someNotificationMessages);
        Mockito.when(releaseMessageService.findLatestReleaseMessageForMessages(Lists.newArrayList(someKey))).thenReturn(anotherReleaseMessage);
        Mockito.when(releaseService.findLatestActiveRelease(someAppId, someClusterName, someNamespaceName)).thenReturn(anotherRelease);
        Mockito.when(anotherReleaseMessage.getMessage()).thenReturn(someKey);
        Mockito.when(anotherReleaseMessage.getId()).thenReturn(someNewNotificationId);
        Release stillOldRelease = configServiceWithCache.findLatestActiveRelease(someAppId, someClusterName, someNamespaceName, someNotificationMessages);
        configServiceWithCache.handleMessage(anotherReleaseMessage, APOLLO_RELEASE_TOPIC);
        Release shouldBeNewRelease = configServiceWithCache.findLatestActiveRelease(someAppId, someClusterName, someNamespaceName, someNotificationMessages);
        Assert.assertEquals(someRelease, release);
        Assert.assertEquals(someRelease, stillOldRelease);
        Assert.assertEquals(anotherRelease, shouldBeNewRelease);
        Mockito.verify(releaseMessageService, Mockito.times(2)).findLatestReleaseMessageForMessages(Lists.newArrayList(someKey));
        Mockito.verify(releaseService, Mockito.times(2)).findLatestActiveRelease(someAppId, someClusterName, someNamespaceName);
    }

    @Test
    public void testFindLatestActiveReleaseWithIrrelevantMessages() throws Exception {
        long someNewNotificationId = (someNotificationId) + 1;
        String someIrrelevantKey = "someIrrelevantKey";
        Mockito.when(releaseMessageService.findLatestReleaseMessageForMessages(Lists.newArrayList(someKey))).thenReturn(someReleaseMessage);
        Mockito.when(releaseService.findLatestActiveRelease(someAppId, someClusterName, someNamespaceName)).thenReturn(someRelease);
        Mockito.when(someReleaseMessage.getId()).thenReturn(someNotificationId);
        Release release = configServiceWithCache.findLatestActiveRelease(someAppId, someClusterName, someNamespaceName, someNotificationMessages);
        Release stillOldRelease = configServiceWithCache.findLatestActiveRelease(someAppId, someClusterName, someNamespaceName, someNotificationMessages);
        someNotificationMessages.put(someIrrelevantKey, someNewNotificationId);
        Release shouldStillBeOldRelease = configServiceWithCache.findLatestActiveRelease(someAppId, someClusterName, someNamespaceName, someNotificationMessages);
        Assert.assertEquals(someRelease, release);
        Assert.assertEquals(someRelease, stillOldRelease);
        Assert.assertEquals(someRelease, shouldStillBeOldRelease);
        Mockito.verify(releaseMessageService, Mockito.times(1)).findLatestReleaseMessageForMessages(Lists.newArrayList(someKey));
        Mockito.verify(releaseService, Mockito.times(1)).findLatestActiveRelease(someAppId, someClusterName, someNamespaceName);
    }
}

