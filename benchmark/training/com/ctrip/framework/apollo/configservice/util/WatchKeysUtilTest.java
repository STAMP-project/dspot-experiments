package com.ctrip.framework.apollo.configservice.util;


import ConfigConsts.NO_APPID_PLACEHOLDER;
import com.ctrip.framework.apollo.common.entity.AppNamespace;
import com.ctrip.framework.apollo.configservice.service.AppNamespaceServiceWithCache;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 *
 * @author Jason Song(song_s@ctrip.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class WatchKeysUtilTest {
    @Mock
    private AppNamespaceServiceWithCache appNamespaceService;

    @Mock
    private AppNamespace someAppNamespace;

    @Mock
    private AppNamespace anotherAppNamespace;

    @Mock
    private AppNamespace somePublicAppNamespace;

    private WatchKeysUtil watchKeysUtil;

    private String someAppId;

    private String someCluster;

    private String someNamespace;

    private String anotherNamespace;

    private String somePublicNamespace;

    private String defaultCluster;

    private String someDC;

    private String somePublicAppId;

    @Test
    public void testAssembleAllWatchKeysWithOneNamespaceAndDefaultCluster() throws Exception {
        Set<String> watchKeys = watchKeysUtil.assembleAllWatchKeys(someAppId, defaultCluster, someNamespace, null);
        Set<String> clusters = Sets.newHashSet(defaultCluster);
        Assert.assertEquals(clusters.size(), watchKeys.size());
        assertWatchKeys(someAppId, clusters, someNamespace, watchKeys);
    }

    @Test
    public void testAssembleAllWatchKeysWithOneNamespaceAndSomeDC() throws Exception {
        Set<String> watchKeys = watchKeysUtil.assembleAllWatchKeys(someAppId, someDC, someNamespace, someDC);
        Set<String> clusters = Sets.newHashSet(defaultCluster, someDC);
        Assert.assertEquals(clusters.size(), watchKeys.size());
        assertWatchKeys(someAppId, clusters, someNamespace, watchKeys);
    }

    @Test
    public void testAssembleAllWatchKeysWithOneNamespaceAndSomeDCAndSomeCluster() throws Exception {
        Set<String> watchKeys = watchKeysUtil.assembleAllWatchKeys(someAppId, someCluster, someNamespace, someDC);
        Set<String> clusters = Sets.newHashSet(defaultCluster, someCluster, someDC);
        Assert.assertEquals(clusters.size(), watchKeys.size());
        assertWatchKeys(someAppId, clusters, someNamespace, watchKeys);
    }

    @Test
    public void testAssembleAllWatchKeysWithMultipleNamespaces() throws Exception {
        Multimap<String, String> watchKeysMap = watchKeysUtil.assembleAllWatchKeys(someAppId, someCluster, Sets.newHashSet(someNamespace, anotherNamespace), someDC);
        Set<String> clusters = Sets.newHashSet(defaultCluster, someCluster, someDC);
        Assert.assertEquals(((clusters.size()) * 2), watchKeysMap.size());
        assertWatchKeys(someAppId, clusters, someNamespace, watchKeysMap.get(someNamespace));
        assertWatchKeys(someAppId, clusters, anotherNamespace, watchKeysMap.get(anotherNamespace));
    }

    @Test
    public void testAssembleAllWatchKeysWithPrivateAndPublicNamespaces() throws Exception {
        Multimap<String, String> watchKeysMap = watchKeysUtil.assembleAllWatchKeys(someAppId, someCluster, Sets.newHashSet(someNamespace, anotherNamespace, somePublicNamespace), someDC);
        Set<String> clusters = Sets.newHashSet(defaultCluster, someCluster, someDC);
        Assert.assertEquals(((clusters.size()) * 4), watchKeysMap.size());
        assertWatchKeys(someAppId, clusters, someNamespace, watchKeysMap.get(someNamespace));
        assertWatchKeys(someAppId, clusters, anotherNamespace, watchKeysMap.get(anotherNamespace));
        assertWatchKeys(someAppId, clusters, somePublicNamespace, watchKeysMap.get(somePublicNamespace));
        assertWatchKeys(somePublicAppId, clusters, somePublicNamespace, watchKeysMap.get(somePublicNamespace));
    }

    @Test
    public void testAssembleWatchKeysForNoAppIdPlaceHolder() throws Exception {
        Multimap<String, String> watchKeysMap = watchKeysUtil.assembleAllWatchKeys(NO_APPID_PLACEHOLDER, someCluster, Sets.newHashSet(someNamespace, anotherNamespace), someDC);
        Assert.assertTrue(watchKeysMap.isEmpty());
    }

    @Test
    public void testAssembleWatchKeysForNoAppIdPlaceHolderAndPublicNamespace() throws Exception {
        Multimap<String, String> watchKeysMap = watchKeysUtil.assembleAllWatchKeys(NO_APPID_PLACEHOLDER, someCluster, Sets.newHashSet(someNamespace, somePublicNamespace), someDC);
        Set<String> clusters = Sets.newHashSet(defaultCluster, someCluster, someDC);
        Assert.assertEquals(clusters.size(), watchKeysMap.size());
        assertWatchKeys(somePublicAppId, clusters, somePublicNamespace, watchKeysMap.get(somePublicNamespace));
    }
}

