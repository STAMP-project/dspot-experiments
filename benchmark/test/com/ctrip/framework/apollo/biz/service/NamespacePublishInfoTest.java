package com.ctrip.framework.apollo.biz.service;


import ConfigConsts.CLUSTER_NAME_DEFAULT;
import ConfigConsts.NAMESPACE_APPLICATION;
import com.ctrip.framework.apollo.biz.AbstractUnitTest;
import com.ctrip.framework.apollo.biz.entity.Cluster;
import com.ctrip.framework.apollo.biz.entity.Item;
import com.ctrip.framework.apollo.biz.entity.Namespace;
import com.ctrip.framework.apollo.biz.entity.Release;
import com.ctrip.framework.apollo.biz.repository.NamespaceRepository;
import java.util.Collections;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


public class NamespacePublishInfoTest extends AbstractUnitTest {
    @Mock
    private ClusterService clusterService;

    @Mock
    private ReleaseService releaseService;

    @Mock
    private ItemService itemService;

    @Mock
    private NamespaceRepository namespaceRepository;

    @InjectMocks
    private NamespaceService namespaceService;

    private String testApp = "testApp";

    @Test
    public void testNamespaceNotEverPublishedButHasItems() {
        Cluster cluster = createCluster(CLUSTER_NAME_DEFAULT);
        Namespace namespace = createNamespace(CLUSTER_NAME_DEFAULT, NAMESPACE_APPLICATION);
        Item item = createItem(namespace.getId(), "a", "b");
        Mockito.when(clusterService.findParentClusters(testApp)).thenReturn(Collections.singletonList(cluster));
        Mockito.when(namespaceRepository.findByAppIdAndClusterNameOrderByIdAsc(testApp, CLUSTER_NAME_DEFAULT)).thenReturn(Collections.singletonList(namespace));
        Mockito.when(itemService.findLastOne(ArgumentMatchers.anyLong())).thenReturn(item);
        Map<String, Boolean> result = namespaceService.namespacePublishInfo(testApp);
        Assert.assertEquals(1, result.size());
        Assert.assertTrue(result.get(CLUSTER_NAME_DEFAULT));
    }

    @Test
    public void testNamespaceEverPublishedAndNotModifiedAfter() {
        Cluster cluster = createCluster(CLUSTER_NAME_DEFAULT);
        Namespace namespace = createNamespace(CLUSTER_NAME_DEFAULT, NAMESPACE_APPLICATION);
        Item item = createItem(namespace.getId(), "a", "b");
        Release release = createRelease("{\"a\":\"b\"}");
        Mockito.when(clusterService.findParentClusters(testApp)).thenReturn(Collections.singletonList(cluster));
        Mockito.when(namespaceRepository.findByAppIdAndClusterNameOrderByIdAsc(testApp, CLUSTER_NAME_DEFAULT)).thenReturn(Collections.singletonList(namespace));
        Mockito.when(releaseService.findLatestActiveRelease(namespace)).thenReturn(release);
        Mockito.when(itemService.findItemsModifiedAfterDate(ArgumentMatchers.anyLong(), ArgumentMatchers.any())).thenReturn(Collections.singletonList(item));
        Map<String, Boolean> result = namespaceService.namespacePublishInfo(testApp);
        Assert.assertEquals(1, result.size());
        Assert.assertFalse(result.get(CLUSTER_NAME_DEFAULT));
    }

    @Test
    public void testNamespaceEverPublishedAndModifiedAfter() {
        Cluster cluster = createCluster(CLUSTER_NAME_DEFAULT);
        Namespace namespace = createNamespace(CLUSTER_NAME_DEFAULT, NAMESPACE_APPLICATION);
        Item item = createItem(namespace.getId(), "a", "b");
        Release release = createRelease("{\"a\":\"c\"}");
        Mockito.when(clusterService.findParentClusters(testApp)).thenReturn(Collections.singletonList(cluster));
        Mockito.when(namespaceRepository.findByAppIdAndClusterNameOrderByIdAsc(testApp, CLUSTER_NAME_DEFAULT)).thenReturn(Collections.singletonList(namespace));
        Mockito.when(releaseService.findLatestActiveRelease(namespace)).thenReturn(release);
        Mockito.when(itemService.findItemsModifiedAfterDate(ArgumentMatchers.anyLong(), ArgumentMatchers.any())).thenReturn(Collections.singletonList(item));
        Map<String, Boolean> result = namespaceService.namespacePublishInfo(testApp);
        Assert.assertEquals(1, result.size());
        Assert.assertTrue(result.get(CLUSTER_NAME_DEFAULT));
    }
}

