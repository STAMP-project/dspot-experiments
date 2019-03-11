package com.ctrip.framework.apollo.adminservice.aop;


import com.ctrip.framework.apollo.biz.entity.Item;
import com.ctrip.framework.apollo.biz.entity.Namespace;
import com.ctrip.framework.apollo.biz.entity.Release;
import com.ctrip.framework.apollo.biz.service.ItemService;
import com.ctrip.framework.apollo.biz.service.NamespaceService;
import com.ctrip.framework.apollo.biz.service.ReleaseService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class NamespaceUnlockAspectTest {
    @Mock
    private ReleaseService releaseService;

    @Mock
    private ItemService itemService;

    @Mock
    private NamespaceService namespaceService;

    @InjectMocks
    private NamespaceUnlockAspect namespaceUnlockAspect;

    @Test
    public void testNamespaceHasNoNormalItemsAndRelease() {
        long namespaceId = 1;
        Namespace namespace = createNamespace(namespaceId);
        Mockito.when(releaseService.findLatestActiveRelease(namespace)).thenReturn(null);
        Mockito.when(itemService.findItemsWithoutOrdered(namespaceId)).thenReturn(Collections.singletonList(createItem("", "")));
        boolean isModified = namespaceUnlockAspect.isModified(namespace);
        Assert.assertFalse(isModified);
    }

    @Test
    public void testNamespaceAddItem() {
        long namespaceId = 1;
        Namespace namespace = createNamespace(namespaceId);
        Release release = createRelease("{\"k1\":\"v1\"}");
        List<Item> items = Arrays.asList(createItem("k1", "v1"), createItem("k2", "v2"));
        Mockito.when(releaseService.findLatestActiveRelease(namespace)).thenReturn(release);
        Mockito.when(itemService.findItemsWithoutOrdered(namespaceId)).thenReturn(items);
        Mockito.when(namespaceService.findParentNamespace(namespace)).thenReturn(null);
        boolean isModified = namespaceUnlockAspect.isModified(namespace);
        Assert.assertTrue(isModified);
    }

    @Test
    public void testNamespaceModifyItem() {
        long namespaceId = 1;
        Namespace namespace = createNamespace(namespaceId);
        Release release = createRelease("{\"k1\":\"v1\"}");
        List<Item> items = Arrays.asList(createItem("k1", "v2"));
        Mockito.when(releaseService.findLatestActiveRelease(namespace)).thenReturn(release);
        Mockito.when(itemService.findItemsWithoutOrdered(namespaceId)).thenReturn(items);
        Mockito.when(namespaceService.findParentNamespace(namespace)).thenReturn(null);
        boolean isModified = namespaceUnlockAspect.isModified(namespace);
        Assert.assertTrue(isModified);
    }

    @Test
    public void testNamespaceDeleteItem() {
        long namespaceId = 1;
        Namespace namespace = createNamespace(namespaceId);
        Release release = createRelease("{\"k1\":\"v1\"}");
        List<Item> items = Arrays.asList(createItem("k2", "v2"));
        Mockito.when(releaseService.findLatestActiveRelease(namespace)).thenReturn(release);
        Mockito.when(itemService.findItemsWithoutOrdered(namespaceId)).thenReturn(items);
        Mockito.when(namespaceService.findParentNamespace(namespace)).thenReturn(null);
        boolean isModified = namespaceUnlockAspect.isModified(namespace);
        Assert.assertTrue(isModified);
    }

    @Test
    public void testChildNamespaceModified() {
        long childNamespaceId = 1;
        long parentNamespaceId = 2;
        Namespace childNamespace = createNamespace(childNamespaceId);
        Namespace parentNamespace = createNamespace(parentNamespaceId);
        Release childRelease = createRelease("{\"k1\":\"v1\", \"k2\":\"v2\"}");
        List<Item> childItems = Arrays.asList(createItem("k1", "v3"));
        Release parentRelease = createRelease("{\"k1\":\"v1\", \"k2\":\"v2\"}");
        Mockito.when(releaseService.findLatestActiveRelease(childNamespace)).thenReturn(childRelease);
        Mockito.when(releaseService.findLatestActiveRelease(parentNamespace)).thenReturn(parentRelease);
        Mockito.when(itemService.findItemsWithoutOrdered(childNamespaceId)).thenReturn(childItems);
        Mockito.when(namespaceService.findParentNamespace(childNamespace)).thenReturn(parentNamespace);
        boolean isModified = namespaceUnlockAspect.isModified(childNamespace);
        Assert.assertTrue(isModified);
    }

    @Test
    public void testChildNamespaceNotModified() {
        long childNamespaceId = 1;
        long parentNamespaceId = 2;
        Namespace childNamespace = createNamespace(childNamespaceId);
        Namespace parentNamespace = createNamespace(parentNamespaceId);
        Release childRelease = createRelease("{\"k1\":\"v3\", \"k2\":\"v2\"}");
        List<Item> childItems = Arrays.asList(createItem("k1", "v3"));
        Release parentRelease = createRelease("{\"k1\":\"v1\", \"k2\":\"v2\"}");
        Mockito.when(releaseService.findLatestActiveRelease(childNamespace)).thenReturn(childRelease);
        Mockito.when(releaseService.findLatestActiveRelease(parentNamespace)).thenReturn(parentRelease);
        Mockito.when(itemService.findItemsWithoutOrdered(childNamespaceId)).thenReturn(childItems);
        Mockito.when(namespaceService.findParentNamespace(childNamespace)).thenReturn(parentNamespace);
        boolean isModified = namespaceUnlockAspect.isModified(childNamespace);
        Assert.assertFalse(isModified);
    }

    @Test
    public void testParentNamespaceNotReleased() {
        long childNamespaceId = 1;
        long parentNamespaceId = 2;
        Namespace childNamespace = createNamespace(childNamespaceId);
        Namespace parentNamespace = createNamespace(parentNamespaceId);
        Release childRelease = createRelease("{\"k1\":\"v3\", \"k2\":\"v2\"}");
        List<Item> childItems = Arrays.asList(createItem("k1", "v2"), createItem("k2", "v2"));
        Mockito.when(releaseService.findLatestActiveRelease(childNamespace)).thenReturn(childRelease);
        Mockito.when(releaseService.findLatestActiveRelease(parentNamespace)).thenReturn(null);
        Mockito.when(itemService.findItemsWithoutOrdered(childNamespaceId)).thenReturn(childItems);
        Mockito.when(namespaceService.findParentNamespace(childNamespace)).thenReturn(parentNamespace);
        boolean isModified = namespaceUnlockAspect.isModified(childNamespace);
        Assert.assertTrue(isModified);
    }
}

