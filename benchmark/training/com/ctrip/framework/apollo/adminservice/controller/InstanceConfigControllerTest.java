package com.ctrip.framework.apollo.adminservice.controller;


import com.ctrip.framework.apollo.biz.entity.Instance;
import com.ctrip.framework.apollo.biz.entity.InstanceConfig;
import com.ctrip.framework.apollo.biz.entity.Release;
import com.ctrip.framework.apollo.biz.service.InstanceService;
import com.ctrip.framework.apollo.biz.service.ReleaseService;
import com.ctrip.framework.apollo.common.dto.InstanceDTO;
import com.ctrip.framework.apollo.common.dto.PageDTO;
import com.ctrip.framework.apollo.common.exception.NotFoundException;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


/**
 *
 *
 * @author Jason Song(song_s@ctrip.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class InstanceConfigControllerTest {
    private InstanceConfigController instanceConfigController;

    @Mock
    private ReleaseService releaseService;

    @Mock
    private InstanceService instanceService;

    private Pageable pageable;

    @Test
    public void getByRelease() throws Exception {
        long someReleaseId = 1;
        long someInstanceId = 1;
        long anotherInstanceId = 2;
        String someReleaseKey = "someKey";
        Release someRelease = new Release();
        someRelease.setReleaseKey(someReleaseKey);
        String someAppId = "someAppId";
        String anotherAppId = "anotherAppId";
        String someCluster = "someCluster";
        String someDataCenter = "someDC";
        String someConfigAppId = "someConfigAppId";
        String someConfigNamespace = "someNamespace";
        String someIp = "someIp";
        Date someReleaseDeliveryTime = new Date();
        Date anotherReleaseDeliveryTime = new Date();
        Mockito.when(releaseService.findOne(someReleaseId)).thenReturn(someRelease);
        InstanceConfig someInstanceConfig = assembleInstanceConfig(someInstanceId, someConfigAppId, someConfigNamespace, someReleaseKey, someReleaseDeliveryTime);
        InstanceConfig anotherInstanceConfig = assembleInstanceConfig(anotherInstanceId, someConfigAppId, someConfigNamespace, someReleaseKey, anotherReleaseDeliveryTime);
        List<InstanceConfig> instanceConfigs = Lists.newArrayList(someInstanceConfig, anotherInstanceConfig);
        Page<InstanceConfig> instanceConfigPage = new org.springframework.data.domain.PageImpl(instanceConfigs, pageable, instanceConfigs.size());
        Mockito.when(instanceService.findActiveInstanceConfigsByReleaseKey(someReleaseKey, pageable)).thenReturn(instanceConfigPage);
        Instance someInstance = assembleInstance(someInstanceId, someAppId, someCluster, someDataCenter, someIp);
        Instance anotherInstance = assembleInstance(anotherInstanceId, anotherAppId, someCluster, someDataCenter, someIp);
        List<Instance> instances = Lists.newArrayList(someInstance, anotherInstance);
        Set<Long> instanceIds = Sets.newHashSet(someInstanceId, anotherInstanceId);
        Mockito.when(instanceService.findInstancesByIds(instanceIds)).thenReturn(instances);
        PageDTO<InstanceDTO> result = instanceConfigController.getByRelease(someReleaseId, pageable);
        Assert.assertEquals(2, result.getContent().size());
        InstanceDTO someInstanceDto = null;
        InstanceDTO anotherInstanceDto = null;
        for (InstanceDTO instanceDTO : result.getContent()) {
            if ((instanceDTO.getId()) == someInstanceId) {
                someInstanceDto = instanceDTO;
            } else
                if ((instanceDTO.getId()) == anotherInstanceId) {
                    anotherInstanceDto = instanceDTO;
                }

        }
        verifyInstance(someInstance, someInstanceDto);
        verifyInstance(anotherInstance, anotherInstanceDto);
        Assert.assertEquals(1, someInstanceDto.getConfigs().size());
        Assert.assertEquals(someReleaseDeliveryTime, someInstanceDto.getConfigs().get(0).getReleaseDeliveryTime());
        Assert.assertEquals(1, anotherInstanceDto.getConfigs().size());
        Assert.assertEquals(anotherReleaseDeliveryTime, anotherInstanceDto.getConfigs().get(0).getReleaseDeliveryTime());
    }

    @Test(expected = NotFoundException.class)
    public void testGetByReleaseWhenReleaseIsNotFound() throws Exception {
        long someReleaseIdNotExists = 1;
        Mockito.when(releaseService.findOne(someReleaseIdNotExists)).thenReturn(null);
        instanceConfigController.getByRelease(someReleaseIdNotExists, pageable);
    }

    @Test
    public void testGetByReleasesNotIn() throws Exception {
        String someConfigAppId = "someConfigAppId";
        String someConfigClusterName = "someConfigClusterName";
        String someConfigNamespaceName = "someConfigNamespaceName";
        long someReleaseId = 1;
        long anotherReleaseId = 2;
        String releaseIds = Joiner.on(",").join(someReleaseId, anotherReleaseId);
        Date someReleaseDeliveryTime = new Date();
        Date anotherReleaseDeliveryTime = new Date();
        Release someRelease = Mockito.mock(Release.class);
        Release anotherRelease = Mockito.mock(Release.class);
        String someReleaseKey = "someReleaseKey";
        String anotherReleaseKey = "anotherReleaseKey";
        Mockito.when(someRelease.getReleaseKey()).thenReturn(someReleaseKey);
        Mockito.when(anotherRelease.getReleaseKey()).thenReturn(anotherReleaseKey);
        Mockito.when(releaseService.findByReleaseIds(Sets.newHashSet(someReleaseId, anotherReleaseId))).thenReturn(Lists.newArrayList(someRelease, anotherRelease));
        long someInstanceId = 1;
        long anotherInstanceId = 2;
        String someInstanceConfigReleaseKey = "someInstanceConfigReleaseKey";
        String anotherInstanceConfigReleaseKey = "anotherInstanceConfigReleaseKey";
        InstanceConfig someInstanceConfig = Mockito.mock(InstanceConfig.class);
        InstanceConfig anotherInstanceConfig = Mockito.mock(InstanceConfig.class);
        Mockito.when(someInstanceConfig.getInstanceId()).thenReturn(someInstanceId);
        Mockito.when(anotherInstanceConfig.getInstanceId()).thenReturn(anotherInstanceId);
        Mockito.when(someInstanceConfig.getReleaseKey()).thenReturn(someInstanceConfigReleaseKey);
        Mockito.when(anotherInstanceConfig.getReleaseKey()).thenReturn(anotherInstanceConfigReleaseKey);
        Mockito.when(someInstanceConfig.getReleaseDeliveryTime()).thenReturn(someReleaseDeliveryTime);
        Mockito.when(anotherInstanceConfig.getReleaseDeliveryTime()).thenReturn(anotherReleaseDeliveryTime);
        Mockito.when(instanceService.findInstanceConfigsByNamespaceWithReleaseKeysNotIn(someConfigAppId, someConfigClusterName, someConfigNamespaceName, Sets.newHashSet(someReleaseKey, anotherReleaseKey))).thenReturn(Lists.newArrayList(someInstanceConfig, anotherInstanceConfig));
        String someInstanceAppId = "someInstanceAppId";
        String someInstanceClusterName = "someInstanceClusterName";
        String someInstanceNamespaceName = "someInstanceNamespaceName";
        String someIp = "someIp";
        String anotherIp = "anotherIp";
        Instance someInstance = assembleInstance(someInstanceId, someInstanceAppId, someInstanceClusterName, someInstanceNamespaceName, someIp);
        Instance anotherInstance = assembleInstance(anotherInstanceId, someInstanceAppId, someInstanceClusterName, someInstanceNamespaceName, anotherIp);
        Mockito.when(instanceService.findInstancesByIds(Sets.newHashSet(someInstanceId, anotherInstanceId))).thenReturn(Lists.newArrayList(someInstance, anotherInstance));
        Release someInstanceConfigRelease = new Release();
        someInstanceConfigRelease.setReleaseKey(someInstanceConfigReleaseKey);
        Release anotherInstanceConfigRelease = new Release();
        anotherInstanceConfigRelease.setReleaseKey(anotherInstanceConfigReleaseKey);
        Mockito.when(releaseService.findByReleaseKeys(Sets.newHashSet(someInstanceConfigReleaseKey, anotherInstanceConfigReleaseKey))).thenReturn(Lists.newArrayList(someInstanceConfigRelease, anotherInstanceConfigRelease));
        List<InstanceDTO> result = instanceConfigController.getByReleasesNotIn(someConfigAppId, someConfigClusterName, someConfigNamespaceName, releaseIds);
        Assert.assertEquals(2, result.size());
        InstanceDTO someInstanceDto = null;
        InstanceDTO anotherInstanceDto = null;
        for (InstanceDTO instanceDTO : result) {
            if ((instanceDTO.getId()) == someInstanceId) {
                someInstanceDto = instanceDTO;
            } else
                if ((instanceDTO.getId()) == anotherInstanceId) {
                    anotherInstanceDto = instanceDTO;
                }

        }
        verifyInstance(someInstance, someInstanceDto);
        verifyInstance(anotherInstance, anotherInstanceDto);
        Assert.assertEquals(someInstanceConfigReleaseKey, someInstanceDto.getConfigs().get(0).getRelease().getReleaseKey());
        Assert.assertEquals(anotherInstanceConfigReleaseKey, anotherInstanceDto.getConfigs().get(0).getRelease().getReleaseKey());
        Assert.assertEquals(someReleaseDeliveryTime, someInstanceDto.getConfigs().get(0).getReleaseDeliveryTime());
        Assert.assertEquals(anotherReleaseDeliveryTime, anotherInstanceDto.getConfigs().get(0).getReleaseDeliveryTime());
    }

    @Test
    public void testGetInstancesByNamespace() throws Exception {
        String someAppId = "someAppId";
        String someClusterName = "someClusterName";
        String someNamespaceName = "someNamespaceName";
        String someIp = "someIp";
        long someInstanceId = 1;
        long anotherInstanceId = 2;
        Instance someInstance = assembleInstance(someInstanceId, someAppId, someClusterName, someNamespaceName, someIp);
        Instance anotherInstance = assembleInstance(anotherInstanceId, someAppId, someClusterName, someNamespaceName, someIp);
        Page<Instance> instances = new org.springframework.data.domain.PageImpl(Lists.newArrayList(someInstance, anotherInstance), pageable, 2);
        Mockito.when(instanceService.findInstancesByNamespace(someAppId, someClusterName, someNamespaceName, pageable)).thenReturn(instances);
        PageDTO<InstanceDTO> result = instanceConfigController.getInstancesByNamespace(someAppId, someClusterName, someNamespaceName, null, pageable);
        Assert.assertEquals(2, result.getContent().size());
        InstanceDTO someInstanceDto = null;
        InstanceDTO anotherInstanceDto = null;
        for (InstanceDTO instanceDTO : result.getContent()) {
            if ((instanceDTO.getId()) == someInstanceId) {
                someInstanceDto = instanceDTO;
            } else
                if ((instanceDTO.getId()) == anotherInstanceId) {
                    anotherInstanceDto = instanceDTO;
                }

        }
        verifyInstance(someInstance, someInstanceDto);
        verifyInstance(anotherInstance, anotherInstanceDto);
    }

    @Test
    public void testGetInstancesByNamespaceAndInstanceAppId() throws Exception {
        String someInstanceAppId = "someInstanceAppId";
        String someAppId = "someAppId";
        String someClusterName = "someClusterName";
        String someNamespaceName = "someNamespaceName";
        String someIp = "someIp";
        long someInstanceId = 1;
        long anotherInstanceId = 2;
        Instance someInstance = assembleInstance(someInstanceId, someAppId, someClusterName, someNamespaceName, someIp);
        Instance anotherInstance = assembleInstance(anotherInstanceId, someAppId, someClusterName, someNamespaceName, someIp);
        Page<Instance> instances = new org.springframework.data.domain.PageImpl(Lists.newArrayList(someInstance, anotherInstance), pageable, 2);
        Mockito.when(instanceService.findInstancesByNamespaceAndInstanceAppId(someInstanceAppId, someAppId, someClusterName, someNamespaceName, pageable)).thenReturn(instances);
        PageDTO<InstanceDTO> result = instanceConfigController.getInstancesByNamespace(someAppId, someClusterName, someNamespaceName, someInstanceAppId, pageable);
        Assert.assertEquals(2, result.getContent().size());
        InstanceDTO someInstanceDto = null;
        InstanceDTO anotherInstanceDto = null;
        for (InstanceDTO instanceDTO : result.getContent()) {
            if ((instanceDTO.getId()) == someInstanceId) {
                someInstanceDto = instanceDTO;
            } else
                if ((instanceDTO.getId()) == anotherInstanceId) {
                    anotherInstanceDto = instanceDTO;
                }

        }
        verifyInstance(someInstance, someInstanceDto);
        verifyInstance(anotherInstance, anotherInstanceDto);
    }

    @Test
    public void testGetInstancesCountByNamespace() throws Exception {
        String someAppId = "someAppId";
        String someClusterName = "someClusterName";
        String someNamespaceName = "someNamespaceName";
        Page<Instance> instances = new org.springframework.data.domain.PageImpl(Collections.emptyList(), pageable, 2);
        Mockito.when(instanceService.findInstancesByNamespace(ArgumentMatchers.eq(someAppId), ArgumentMatchers.eq(someClusterName), ArgumentMatchers.eq(someNamespaceName), ArgumentMatchers.any(Pageable.class))).thenReturn(instances);
        long result = instanceConfigController.getInstancesCountByNamespace(someAppId, someClusterName, someNamespaceName);
        Assert.assertEquals(2, result);
    }
}

