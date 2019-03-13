package com.ctrip.framework.apollo.portal.service;


import AdminServiceAPI.ItemAPI;
import AdminServiceAPI.NamespaceAPI;
import AdminServiceAPI.ReleaseAPI;
import ConfigFileFormat.Properties;
import Env.DEV;
import com.ctrip.framework.apollo.common.dto.ItemChangeSets;
import com.ctrip.framework.apollo.common.dto.ItemDTO;
import com.ctrip.framework.apollo.common.dto.NamespaceDTO;
import com.ctrip.framework.apollo.core.ConfigConsts;
import com.ctrip.framework.apollo.core.enums.Env;
import com.ctrip.framework.apollo.portal.AbstractUnitTest;
import com.ctrip.framework.apollo.portal.component.txtresolver.PropertyResolver;
import com.ctrip.framework.apollo.portal.entity.bo.UserInfo;
import com.ctrip.framework.apollo.portal.entity.model.NamespaceTextModel;
import com.ctrip.framework.apollo.portal.entity.vo.ItemDiffs;
import com.ctrip.framework.apollo.portal.entity.vo.NamespaceIdentifier;
import com.ctrip.framework.apollo.portal.spi.UserInfoHolder;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


public class ConfigServiceTest extends AbstractUnitTest {
    @Mock
    private NamespaceAPI namespaceAPI;

    @Mock
    private ReleaseAPI releaseAPI;

    @Mock
    private ItemAPI itemAPI;

    @Mock
    private PropertyResolver resolver;

    @Mock
    private UserInfoHolder userInfoHolder;

    @InjectMocks
    private ItemService configService;

    @Test
    public void testUpdateConfigByText() {
        String appId = "6666";
        String clusterName = "default";
        String namespaceName = "application";
        NamespaceTextModel model = new NamespaceTextModel();
        model.setEnv("DEV");
        model.setNamespaceName(namespaceName);
        model.setClusterName(clusterName);
        model.setAppId(appId);
        model.setConfigText("a=b\nb=c\nc=d\nd=e");
        model.setFormat(Properties.getValue());
        List<ItemDTO> itemDTOs = mockBaseItemHas3Key();
        ItemChangeSets changeSets = new ItemChangeSets();
        changeSets.addCreateItem(new ItemDTO("d", "c", "", 4));
        Mockito.when(itemAPI.findItems(appId, DEV, clusterName, namespaceName)).thenReturn(itemDTOs);
        Mockito.when(resolver.resolve(0, model.getConfigText(), itemDTOs)).thenReturn(changeSets);
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId("test");
        Mockito.when(userInfoHolder.getUser()).thenReturn(userInfo);
        try {
            configService.updateConfigItemByText(model);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testCompareTargetNamespaceHasNoItems() {
        ItemDTO sourceItem1 = new ItemDTO("a", "b", "comment", 1);
        List<ItemDTO> sourceItems = Arrays.asList(sourceItem1);
        String appId = "6666";
        String env = "LOCAL";
        String clusterName = ConfigConsts.CLUSTER_NAME_DEFAULT;
        String namespaceName = ConfigConsts.NAMESPACE_APPLICATION;
        List<NamespaceIdentifier> namespaceIdentifiers = generateNamespaceIdentifier(appId, env, clusterName, namespaceName);
        NamespaceDTO namespaceDTO = generateNamespaceDTO(appId, clusterName, namespaceName);
        Mockito.when(namespaceAPI.loadNamespace(appId, Env.valueOf(env), clusterName, namespaceName)).thenReturn(namespaceDTO);
        Mockito.when(itemAPI.findItems(appId, Env.valueOf(env), clusterName, namespaceName)).thenReturn(null);
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId("test");
        Mockito.when(userInfoHolder.getUser()).thenReturn(userInfo);
        List<ItemDiffs> itemDiffses = configService.compare(namespaceIdentifiers, sourceItems);
        Assert.assertEquals(1, itemDiffses.size());
        ItemDiffs itemDiffs = itemDiffses.get(0);
        ItemChangeSets changeSets = itemDiffs.getDiffs();
        Assert.assertEquals(0, changeSets.getUpdateItems().size());
        Assert.assertEquals(0, changeSets.getDeleteItems().size());
        List<ItemDTO> createItems = changeSets.getCreateItems();
        ItemDTO createItem = createItems.get(0);
        Assert.assertEquals(1, createItem.getLineNum());
        Assert.assertEquals("a", createItem.getKey());
        Assert.assertEquals("b", createItem.getValue());
        Assert.assertEquals("comment", createItem.getComment());
    }

    @Test
    public void testCompare() {
        ItemDTO sourceItem1 = new ItemDTO("a", "b", "comment", 1);// not modified

        ItemDTO sourceItem2 = new ItemDTO("newKey", "c", "comment", 2);// new item

        ItemDTO sourceItem3 = new ItemDTO("c", "newValue", "comment", 3);// update value

        ItemDTO sourceItem4 = new ItemDTO("d", "b", "newComment", 4);// update comment

        List<ItemDTO> sourceItems = Arrays.asList(sourceItem1, sourceItem2, sourceItem3, sourceItem4);
        ItemDTO targetItem1 = new ItemDTO("a", "b", "comment", 1);
        ItemDTO targetItem2 = new ItemDTO("c", "oldValue", "comment", 2);
        ItemDTO targetItem3 = new ItemDTO("d", "b", "oldComment", 3);
        List<ItemDTO> targetItems = Arrays.asList(targetItem1, targetItem2, targetItem3);
        String appId = "6666";
        String env = "LOCAL";
        String clusterName = ConfigConsts.CLUSTER_NAME_DEFAULT;
        String namespaceName = ConfigConsts.NAMESPACE_APPLICATION;
        List<NamespaceIdentifier> namespaceIdentifiers = generateNamespaceIdentifier(appId, env, clusterName, namespaceName);
        NamespaceDTO namespaceDTO = generateNamespaceDTO(appId, clusterName, namespaceName);
        Mockito.when(namespaceAPI.loadNamespace(appId, Env.valueOf(env), clusterName, namespaceName)).thenReturn(namespaceDTO);
        Mockito.when(itemAPI.findItems(appId, Env.valueOf(env), clusterName, namespaceName)).thenReturn(targetItems);
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId("test");
        Mockito.when(userInfoHolder.getUser()).thenReturn(userInfo);
        List<ItemDiffs> itemDiffses = configService.compare(namespaceIdentifiers, sourceItems);
        Assert.assertEquals(1, itemDiffses.size());
        ItemDiffs itemDiffs = itemDiffses.get(0);
        ItemChangeSets changeSets = itemDiffs.getDiffs();
        Assert.assertEquals(0, changeSets.getDeleteItems().size());
        Assert.assertEquals(2, changeSets.getUpdateItems().size());
        Assert.assertEquals(1, changeSets.getCreateItems().size());
        NamespaceIdentifier namespaceIdentifier = itemDiffs.getNamespace();
        Assert.assertEquals(appId, namespaceIdentifier.getAppId());
        Assert.assertEquals(Env.valueOf("LOCAL"), namespaceIdentifier.getEnv());
        Assert.assertEquals(clusterName, namespaceIdentifier.getClusterName());
        Assert.assertEquals(namespaceName, namespaceIdentifier.getNamespaceName());
        ItemDTO createdItem = changeSets.getCreateItems().get(0);
        Assert.assertEquals("newKey", createdItem.getKey());
        Assert.assertEquals("c", createdItem.getValue());
        Assert.assertEquals("comment", createdItem.getComment());
        Assert.assertEquals(4, createdItem.getLineNum());
        List<ItemDTO> updateItems = changeSets.getUpdateItems();
        ItemDTO updateItem1 = updateItems.get(0);
        ItemDTO updateItem2 = updateItems.get(1);
        Assert.assertEquals("c", updateItem1.getKey());
        Assert.assertEquals("newValue", updateItem1.getValue());
        Assert.assertEquals("comment", updateItem1.getComment());
        Assert.assertEquals(2, updateItem1.getLineNum());
        Assert.assertEquals("d", updateItem2.getKey());
        Assert.assertEquals("b", updateItem2.getValue());
        Assert.assertEquals("newComment", updateItem2.getComment());
        Assert.assertEquals(3, updateItem2.getLineNum());
    }
}

