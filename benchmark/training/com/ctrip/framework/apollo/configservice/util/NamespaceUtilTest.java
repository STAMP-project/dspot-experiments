package com.ctrip.framework.apollo.configservice.util;


import com.ctrip.framework.apollo.common.entity.AppNamespace;
import com.ctrip.framework.apollo.configservice.service.AppNamespaceServiceWithCache;
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
public class NamespaceUtilTest {
    private NamespaceUtil namespaceUtil;

    @Mock
    private AppNamespaceServiceWithCache appNamespaceServiceWithCache;

    @Test
    public void testFilterNamespaceName() throws Exception {
        String someName = "a.properties";
        Assert.assertEquals("a", namespaceUtil.filterNamespaceName(someName));
    }

    @Test
    public void testFilterNamespaceNameUnchanged() throws Exception {
        String someName = "a.xml";
        Assert.assertEquals(someName, namespaceUtil.filterNamespaceName(someName));
    }

    @Test
    public void testFilterNamespaceNameWithMultiplePropertiesSuffix() throws Exception {
        String someName = "a.properties.properties";
        Assert.assertEquals("a.properties", namespaceUtil.filterNamespaceName(someName));
    }

    @Test
    public void testFilterNamespaceNameWithRandomCase() throws Exception {
        String someName = "AbC.ProPErties";
        Assert.assertEquals("AbC", namespaceUtil.filterNamespaceName(someName));
    }

    @Test
    public void testFilterNamespaceNameWithRandomCaseUnchanged() throws Exception {
        String someName = "AbCD.xMl";
        Assert.assertEquals(someName, namespaceUtil.filterNamespaceName(someName));
    }

    @Test
    public void testNormalizeNamespaceWithPrivateNamespace() throws Exception {
        String someAppId = "someAppId";
        String someNamespaceName = "someNamespaceName";
        String someNormalizedNamespaceName = "someNormalizedNamespaceName";
        AppNamespace someAppNamespace = Mockito.mock(AppNamespace.class);
        Mockito.when(someAppNamespace.getName()).thenReturn(someNormalizedNamespaceName);
        Mockito.when(appNamespaceServiceWithCache.findByAppIdAndNamespace(someAppId, someNamespaceName)).thenReturn(someAppNamespace);
        Assert.assertEquals(someNormalizedNamespaceName, namespaceUtil.normalizeNamespace(someAppId, someNamespaceName));
        Mockito.verify(appNamespaceServiceWithCache, Mockito.times(1)).findByAppIdAndNamespace(someAppId, someNamespaceName);
        Mockito.verify(appNamespaceServiceWithCache, Mockito.never()).findPublicNamespaceByName(someNamespaceName);
    }

    @Test
    public void testNormalizeNamespaceWithPublicNamespace() throws Exception {
        String someAppId = "someAppId";
        String someNamespaceName = "someNamespaceName";
        String someNormalizedNamespaceName = "someNormalizedNamespaceName";
        AppNamespace someAppNamespace = Mockito.mock(AppNamespace.class);
        Mockito.when(someAppNamespace.getName()).thenReturn(someNormalizedNamespaceName);
        Mockito.when(appNamespaceServiceWithCache.findByAppIdAndNamespace(someAppId, someNamespaceName)).thenReturn(null);
        Mockito.when(appNamespaceServiceWithCache.findPublicNamespaceByName(someNamespaceName)).thenReturn(someAppNamespace);
        Assert.assertEquals(someNormalizedNamespaceName, namespaceUtil.normalizeNamespace(someAppId, someNamespaceName));
        Mockito.verify(appNamespaceServiceWithCache, Mockito.times(1)).findByAppIdAndNamespace(someAppId, someNamespaceName);
        Mockito.verify(appNamespaceServiceWithCache, Mockito.times(1)).findPublicNamespaceByName(someNamespaceName);
    }

    @Test
    public void testNormalizeNamespaceFailed() throws Exception {
        String someAppId = "someAppId";
        String someNamespaceName = "someNamespaceName";
        Mockito.when(appNamespaceServiceWithCache.findByAppIdAndNamespace(someAppId, someNamespaceName)).thenReturn(null);
        Mockito.when(appNamespaceServiceWithCache.findPublicNamespaceByName(someNamespaceName)).thenReturn(null);
        Assert.assertEquals(someNamespaceName, namespaceUtil.normalizeNamespace(someAppId, someNamespaceName));
        Mockito.verify(appNamespaceServiceWithCache, Mockito.times(1)).findByAppIdAndNamespace(someAppId, someNamespaceName);
        Mockito.verify(appNamespaceServiceWithCache, Mockito.times(1)).findPublicNamespaceByName(someNamespaceName);
    }
}

