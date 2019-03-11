package com.ctrip.framework.apollo.biz.service;


import ConfigConsts.CLUSTER_NAME_DEFAULT;
import com.ctrip.framework.apollo.biz.AbstractUnitTest;
import com.ctrip.framework.apollo.biz.MockBeanFactory;
import com.ctrip.framework.apollo.biz.entity.Namespace;
import com.ctrip.framework.apollo.biz.repository.NamespaceRepository;
import com.ctrip.framework.apollo.common.entity.AppNamespace;
import com.ctrip.framework.apollo.common.exception.BadRequestException;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


public class NamespaceServiceTest extends AbstractUnitTest {
    @Mock
    private AppNamespaceService appNamespaceService;

    @Mock
    private NamespaceRepository namespaceRepository;

    @Spy
    @InjectMocks
    private NamespaceService namespaceService;

    private String testPublicAppNamespace = "publicAppNamespace";

    @Test(expected = BadRequestException.class)
    public void testFindPublicAppNamespaceWithWrongNamespace() {
        Pageable page = PageRequest.of(0, 10);
        Mockito.when(appNamespaceService.findPublicNamespaceByName(testPublicAppNamespace)).thenReturn(null);
        namespaceService.findPublicAppNamespaceAllNamespaces(testPublicAppNamespace, page);
    }

    @Test
    public void testFindPublicAppNamespace() {
        AppNamespace publicAppNamespace = MockBeanFactory.mockAppNamespace(null, testPublicAppNamespace, true);
        Mockito.when(appNamespaceService.findPublicNamespaceByName(testPublicAppNamespace)).thenReturn(publicAppNamespace);
        Namespace firstParentNamespace = MockBeanFactory.mockNamespace("app", CLUSTER_NAME_DEFAULT, testPublicAppNamespace);
        Namespace secondParentNamespace = MockBeanFactory.mockNamespace("app1", CLUSTER_NAME_DEFAULT, testPublicAppNamespace);
        Pageable page = PageRequest.of(0, 10);
        Mockito.when(namespaceRepository.findByNamespaceName(testPublicAppNamespace, page)).thenReturn(Arrays.asList(firstParentNamespace, secondParentNamespace));
        Mockito.doReturn(false).when(namespaceService).isChildNamespace(firstParentNamespace);
        Mockito.doReturn(false).when(namespaceService).isChildNamespace(secondParentNamespace);
        List<Namespace> namespaces = namespaceService.findPublicAppNamespaceAllNamespaces(testPublicAppNamespace, page);
        Assert.assertEquals(2, namespaces.size());
    }
}

