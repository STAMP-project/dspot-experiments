package com.ctrip.framework.apollo.configservice.service.config;


import com.ctrip.framework.apollo.biz.entity.Release;
import com.ctrip.framework.apollo.biz.grayReleaseRule.GrayReleaseRulesHolder;
import com.ctrip.framework.apollo.biz.service.ReleaseService;
import com.ctrip.framework.apollo.core.dto.ApolloNotificationMessages;
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
public class DefaultConfigServiceTest {
    private DefaultConfigService configService;

    private String someClientAppId;

    private String someConfigAppId;

    private String someClusterName;

    private String defaultClusterName;

    private String defaultNamespaceName;

    private String someDataCenter;

    private String someClientIp;

    @Mock
    private ApolloNotificationMessages someNotificationMessages;

    @Mock
    private ReleaseService releaseService;

    @Mock
    private GrayReleaseRulesHolder grayReleaseRulesHolder;

    @Mock
    private Release someRelease;

    @Test
    public void testLoadConfig() throws Exception {
        Mockito.when(releaseService.findLatestActiveRelease(someConfigAppId, someClusterName, defaultNamespaceName)).thenReturn(someRelease);
        Release release = configService.loadConfig(someClientAppId, someClientIp, someConfigAppId, someClusterName, defaultNamespaceName, someDataCenter, someNotificationMessages);
        Mockito.verify(releaseService, Mockito.times(1)).findLatestActiveRelease(someConfigAppId, someClusterName, defaultNamespaceName);
        Assert.assertEquals(someRelease, release);
    }

    @Test
    public void testLoadConfigWithGrayRelease() throws Exception {
        Release grayRelease = Mockito.mock(Release.class);
        long grayReleaseId = 999;
        Mockito.when(grayReleaseRulesHolder.findReleaseIdFromGrayReleaseRule(someClientAppId, someClientIp, someConfigAppId, someClusterName, defaultNamespaceName)).thenReturn(grayReleaseId);
        Mockito.when(releaseService.findActiveOne(grayReleaseId)).thenReturn(grayRelease);
        Release release = configService.loadConfig(someClientAppId, someClientIp, someConfigAppId, someClusterName, defaultNamespaceName, someDataCenter, someNotificationMessages);
        Mockito.verify(releaseService, Mockito.times(1)).findActiveOne(grayReleaseId);
        Mockito.verify(releaseService, Mockito.never()).findLatestActiveRelease(someConfigAppId, someClusterName, defaultNamespaceName);
        Assert.assertEquals(grayRelease, release);
    }

    @Test
    public void testLoadConfigWithReleaseNotFound() throws Exception {
        Mockito.when(releaseService.findLatestActiveRelease(someConfigAppId, someClusterName, defaultNamespaceName)).thenReturn(null);
        Release release = configService.loadConfig(someClientAppId, someClientIp, someConfigAppId, someClusterName, defaultNamespaceName, someDataCenter, someNotificationMessages);
        Assert.assertNull(release);
    }

    @Test
    public void testLoadConfigWithDefaultClusterWithDataCenterRelease() throws Exception {
        Mockito.when(releaseService.findLatestActiveRelease(someConfigAppId, someDataCenter, defaultNamespaceName)).thenReturn(someRelease);
        Release release = configService.loadConfig(someClientAppId, someClientIp, someConfigAppId, defaultClusterName, defaultNamespaceName, someDataCenter, someNotificationMessages);
        Mockito.verify(releaseService, Mockito.times(1)).findLatestActiveRelease(someConfigAppId, someDataCenter, defaultNamespaceName);
        Assert.assertEquals(someRelease, release);
    }

    @Test
    public void testLoadConfigWithDefaultClusterWithNoDataCenterRelease() throws Exception {
        Mockito.when(releaseService.findLatestActiveRelease(someConfigAppId, someDataCenter, defaultNamespaceName)).thenReturn(null);
        Mockito.when(releaseService.findLatestActiveRelease(someConfigAppId, defaultClusterName, defaultNamespaceName)).thenReturn(someRelease);
        Release release = configService.loadConfig(someClientAppId, someClientIp, someConfigAppId, defaultClusterName, defaultNamespaceName, someDataCenter, someNotificationMessages);
        Mockito.verify(releaseService, Mockito.times(1)).findLatestActiveRelease(someConfigAppId, someDataCenter, defaultNamespaceName);
        Mockito.verify(releaseService, Mockito.times(1)).findLatestActiveRelease(someConfigAppId, defaultClusterName, defaultNamespaceName);
        Assert.assertEquals(someRelease, release);
    }
}

