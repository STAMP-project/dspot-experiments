package com.ctrip.framework.apollo.portal.service;


import AdminServiceAPI.NamespaceAPI;
import ConfigFileFormat.Properties;
import ConfigFileFormat.XML;
import Env.DEV;
import com.ctrip.framework.apollo.common.dto.ItemDTO;
import com.ctrip.framework.apollo.common.dto.NamespaceDTO;
import com.ctrip.framework.apollo.common.dto.ReleaseDTO;
import com.ctrip.framework.apollo.common.entity.AppNamespace;
import com.ctrip.framework.apollo.common.exception.BadRequestException;
import com.ctrip.framework.apollo.core.enums.Env;
import com.ctrip.framework.apollo.portal.AbstractUnitTest;
import com.ctrip.framework.apollo.portal.component.txtresolver.PropertyResolver;
import com.ctrip.framework.apollo.portal.entity.bo.NamespaceBO;
import com.ctrip.framework.apollo.portal.spi.UserInfoHolder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


public class NamespaceServiceTest extends AbstractUnitTest {
    @Mock
    private NamespaceAPI namespaceAPI;

    @Mock
    private ReleaseService releaseService;

    @Mock
    private ItemService itemService;

    @Mock
    private PropertyResolver resolver;

    @Mock
    private AppNamespaceService appNamespaceService;

    @Mock
    private InstanceService instanceService;

    @Mock
    private NamespaceBranchService branchService;

    @Mock
    private UserInfoHolder userInfoHolder;

    @InjectMocks
    private NamespaceService namespaceService;

    private String testAppId = "6666";

    private String testClusterName = "default";

    private String testNamespaceName = "application";

    private Env testEnv = Env.DEV;

    @Test
    public void testFindNamespace() {
        AppNamespace applicationAppNamespace = Mockito.mock(AppNamespace.class);
        AppNamespace hermesAppNamespace = Mockito.mock(AppNamespace.class);
        NamespaceDTO application = new NamespaceDTO();
        application.setId(1);
        application.setClusterName(testClusterName);
        application.setAppId(testAppId);
        application.setNamespaceName(testNamespaceName);
        NamespaceDTO hermes = new NamespaceDTO();
        hermes.setId(2);
        hermes.setClusterName("default");
        hermes.setAppId(testAppId);
        hermes.setNamespaceName("hermes");
        List<NamespaceDTO> namespaces = Arrays.asList(application, hermes);
        ReleaseDTO someRelease = new ReleaseDTO();
        someRelease.setConfigurations("{\"a\":\"123\",\"b\":\"123\"}");
        ItemDTO i1 = new ItemDTO("a", "123", "", 1);
        ItemDTO i2 = new ItemDTO("b", "1", "", 2);
        ItemDTO i3 = new ItemDTO("", "", "#dddd", 3);
        ItemDTO i4 = new ItemDTO("c", "1", "", 4);
        List<ItemDTO> someItems = Arrays.asList(i1, i2, i3, i4);
        Mockito.when(applicationAppNamespace.getFormat()).thenReturn(Properties.getValue());
        Mockito.when(hermesAppNamespace.getFormat()).thenReturn(XML.getValue());
        Mockito.when(appNamespaceService.findByAppIdAndName(testAppId, testNamespaceName)).thenReturn(applicationAppNamespace);
        Mockito.when(appNamespaceService.findPublicAppNamespace("hermes")).thenReturn(hermesAppNamespace);
        Mockito.when(namespaceAPI.findNamespaceByCluster(testAppId, DEV, testClusterName)).thenReturn(namespaces);
        Mockito.when(releaseService.loadLatestRelease(testAppId, DEV, testClusterName, testNamespaceName)).thenReturn(someRelease);
        Mockito.when(releaseService.loadLatestRelease(testAppId, DEV, testClusterName, "hermes")).thenReturn(someRelease);
        Mockito.when(itemService.findItems(testAppId, DEV, testClusterName, testNamespaceName)).thenReturn(someItems);
        List<NamespaceBO> namespaceVOs = namespaceService.findNamespaceBOs(testAppId, DEV, testClusterName);
        Assert.assertEquals(2, namespaceVOs.size());
        NamespaceBO namespaceVO = namespaceVOs.get(0);
        Assert.assertEquals(4, namespaceVO.getItems().size());
        Assert.assertEquals("a", namespaceVO.getItems().get(0).getItem().getKey());
        Assert.assertEquals(2, namespaceVO.getItemModifiedCnt());
        Assert.assertEquals(testAppId, namespaceVO.getBaseInfo().getAppId());
        Assert.assertEquals(testClusterName, namespaceVO.getBaseInfo().getClusterName());
        Assert.assertEquals(testNamespaceName, namespaceVO.getBaseInfo().getNamespaceName());
    }

    @Test
    public void testDeletePrivateNamespace() {
        String operator = "user";
        AppNamespace privateNamespace = createAppNamespace(testAppId, testNamespaceName, false);
        Mockito.when(appNamespaceService.findByAppIdAndName(testAppId, testNamespaceName)).thenReturn(privateNamespace);
        Mockito.when(userInfoHolder.getUser()).thenReturn(createUser(operator));
        namespaceService.deleteNamespace(testAppId, testEnv, testClusterName, testNamespaceName);
        Mockito.verify(namespaceAPI, Mockito.times(1)).deleteNamespace(testEnv, testAppId, testClusterName, testNamespaceName, operator);
    }

    @Test(expected = BadRequestException.class)
    public void testDeleteNamespaceHasInstance() {
        AppNamespace publicNamespace = createAppNamespace(testAppId, testNamespaceName, true);
        Mockito.when(appNamespaceService.findByAppIdAndName(testAppId, testNamespaceName)).thenReturn(publicNamespace);
        Mockito.when(instanceService.getInstanceCountByNamepsace(testAppId, testEnv, testClusterName, testNamespaceName)).thenReturn(10);
        namespaceService.deleteNamespace(testAppId, testEnv, testClusterName, testNamespaceName);
    }

    @Test(expected = BadRequestException.class)
    public void testDeleteNamespaceBranchHasInstance() {
        AppNamespace publicNamespace = createAppNamespace(testAppId, testNamespaceName, true);
        String branchName = "branch";
        NamespaceDTO branch = createNamespace(testAppId, branchName, testNamespaceName);
        Mockito.when(appNamespaceService.findByAppIdAndName(testAppId, testNamespaceName)).thenReturn(publicNamespace);
        Mockito.when(instanceService.getInstanceCountByNamepsace(testAppId, testEnv, testClusterName, testNamespaceName)).thenReturn(0);
        Mockito.when(branchService.findBranchBaseInfo(testAppId, testEnv, testClusterName, testNamespaceName)).thenReturn(branch);
        Mockito.when(instanceService.getInstanceCountByNamepsace(testAppId, testEnv, branchName, testNamespaceName)).thenReturn(10);
        namespaceService.deleteNamespace(testAppId, testEnv, testClusterName, testNamespaceName);
    }

    @Test(expected = BadRequestException.class)
    public void testDeleteNamespaceWithAssociatedNamespace() {
        AppNamespace publicNamespace = createAppNamespace(testAppId, testNamespaceName, true);
        String branchName = "branch";
        NamespaceDTO branch = createNamespace(testAppId, branchName, testNamespaceName);
        Mockito.when(appNamespaceService.findByAppIdAndName(testAppId, testNamespaceName)).thenReturn(publicNamespace);
        Mockito.when(instanceService.getInstanceCountByNamepsace(testAppId, testEnv, testClusterName, testNamespaceName)).thenReturn(0);
        Mockito.when(branchService.findBranchBaseInfo(testAppId, testEnv, testClusterName, testNamespaceName)).thenReturn(branch);
        Mockito.when(instanceService.getInstanceCountByNamepsace(testAppId, testEnv, branchName, testNamespaceName)).thenReturn(0);
        Mockito.when(appNamespaceService.findPublicAppNamespace(testNamespaceName)).thenReturn(publicNamespace);
        Mockito.when(namespaceAPI.countPublicAppNamespaceAssociatedNamespaces(testEnv, testNamespaceName)).thenReturn(10);
        namespaceService.deleteNamespace(testAppId, testEnv, testClusterName, testNamespaceName);
    }

    @Test
    public void testDeleteEmptyNamespace() {
        String branchName = "branch";
        String operator = "user";
        AppNamespace publicNamespace = createAppNamespace(testAppId, testNamespaceName, true);
        NamespaceDTO branch = createNamespace(testAppId, branchName, testNamespaceName);
        Mockito.when(appNamespaceService.findByAppIdAndName(testAppId, testNamespaceName)).thenReturn(publicNamespace);
        Mockito.when(instanceService.getInstanceCountByNamepsace(testAppId, testEnv, testClusterName, testNamespaceName)).thenReturn(0);
        Mockito.when(branchService.findBranchBaseInfo(testAppId, testEnv, testClusterName, testNamespaceName)).thenReturn(branch);
        Mockito.when(instanceService.getInstanceCountByNamepsace(testAppId, testEnv, branchName, testNamespaceName)).thenReturn(0);
        Mockito.when(appNamespaceService.findPublicAppNamespace(testNamespaceName)).thenReturn(publicNamespace);
        NamespaceDTO namespace = createNamespace(testAppId, testClusterName, testNamespaceName);
        Mockito.when(namespaceAPI.getPublicAppNamespaceAllNamespaces(testEnv, testNamespaceName, 0, 10)).thenReturn(Collections.singletonList(namespace));
        Mockito.when(userInfoHolder.getUser()).thenReturn(createUser(operator));
        namespaceService.deleteNamespace(testAppId, testEnv, testClusterName, testNamespaceName);
        Mockito.verify(namespaceAPI, Mockito.times(1)).deleteNamespace(testEnv, testAppId, testClusterName, testNamespaceName, operator);
    }
}

