/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.checks;


import MaintenanceState.OFF;
import Role.HDFS_SERVICE_CHECK;
import Role.ZOOKEEPER_QUORUM_SERVICE_CHECK;
import UpgradeCheckStatus.FAIL;
import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.Collections;
import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.api.services.AmbariMetaInfo;
import org.apache.ambari.server.metadata.ActionMetadata;
import org.apache.ambari.server.orm.dao.HostRoleCommandDAO;
import org.apache.ambari.server.orm.dao.HostRoleCommandDAO.LastServiceCheckDTO;
import org.apache.ambari.server.orm.dao.ServiceConfigDAO;
import org.apache.ambari.server.orm.entities.ServiceConfigEntity;
import org.apache.ambari.server.state.Service;
import org.apache.ambari.server.state.ServiceComponent;
import org.apache.ambari.spi.ClusterInformation;
import org.apache.ambari.spi.upgrade.UpgradeCheckRequest;
import org.apache.ambari.spi.upgrade.UpgradeCheckResult;
import org.apache.ambari.spi.upgrade.UpgradeType;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ServiceCheckValidityCheckTest {
    private static final String CLUSTER_NAME = "cluster1";

    private static final long CLUSTER_ID = 1L;

    private static final String SERVICE_NAME = "HDFS";

    private static final long CONFIG_CREATE_TIMESTAMP = 1461518722202L;

    private static final long SERVICE_CHECK_START_TIME = (ServiceCheckValidityCheckTest.CONFIG_CREATE_TIMESTAMP) - 2000L;

    private static final String SERVICE_COMPONENT_NAME = "service component";

    private ServiceCheckValidityCheck serviceCheckValidityCheck;

    private ServiceConfigDAO serviceConfigDAO;

    private HostRoleCommandDAO hostRoleCommandDAO;

    private Service service;

    private AmbariMetaInfo ambariMetaInfo;

    private ActionMetadata actionMetadata;

    @Test
    public void testWithNullCommandDetailAtCommand() throws AmbariException {
        ServiceComponent serviceComponent = Mockito.mock(ServiceComponent.class);
        Mockito.when(serviceComponent.isVersionAdvertised()).thenReturn(true);
        Mockito.when(service.getMaintenanceState()).thenReturn(OFF);
        Mockito.when(service.getServiceComponents()).thenReturn(ImmutableMap.of(ServiceCheckValidityCheckTest.SERVICE_COMPONENT_NAME, serviceComponent));
        ServiceConfigEntity serviceConfigEntity = new ServiceConfigEntity();
        serviceConfigEntity.setServiceName(ServiceCheckValidityCheckTest.SERVICE_NAME);
        serviceConfigEntity.setCreateTimestamp(ServiceCheckValidityCheckTest.CONFIG_CREATE_TIMESTAMP);
        LastServiceCheckDTO lastServiceCheckDTO1 = new LastServiceCheckDTO(ZOOKEEPER_QUORUM_SERVICE_CHECK.name(), ServiceCheckValidityCheckTest.SERVICE_CHECK_START_TIME);
        LastServiceCheckDTO lastServiceCheckDTO2 = new LastServiceCheckDTO(HDFS_SERVICE_CHECK.name(), ServiceCheckValidityCheckTest.SERVICE_CHECK_START_TIME);
        Mockito.when(serviceConfigDAO.getLastServiceConfig(ArgumentMatchers.eq(ServiceCheckValidityCheckTest.CLUSTER_ID), ArgumentMatchers.eq(ServiceCheckValidityCheckTest.SERVICE_NAME))).thenReturn(serviceConfigEntity);
        Mockito.when(hostRoleCommandDAO.getLatestServiceChecksByRole(ArgumentMatchers.any(Long.class))).thenReturn(Arrays.asList(lastServiceCheckDTO1, lastServiceCheckDTO2));
        ClusterInformation clusterInformation = new ClusterInformation(ServiceCheckValidityCheckTest.CLUSTER_NAME, false, null, null, null);
        UpgradeCheckRequest request = new UpgradeCheckRequest(clusterInformation, UpgradeType.ROLLING, null, null, null);
        try {
            UpgradeCheckResult result = serviceCheckValidityCheck.perform(request);
            Assert.assertEquals(FAIL, result.getStatus());
        } catch (NullPointerException ex) {
            Assert.fail("serviceCheckValidityCheck failed due to null at start_time were not handled");
        }
    }

    @Test
    public void testFailWhenServiceWithOutdatedServiceCheckExists() throws AmbariException {
        ServiceComponent serviceComponent = Mockito.mock(ServiceComponent.class);
        Mockito.when(serviceComponent.isVersionAdvertised()).thenReturn(true);
        Mockito.when(service.getMaintenanceState()).thenReturn(OFF);
        Mockito.when(service.getServiceComponents()).thenReturn(ImmutableMap.of(ServiceCheckValidityCheckTest.SERVICE_COMPONENT_NAME, serviceComponent));
        ServiceConfigEntity serviceConfigEntity = new ServiceConfigEntity();
        serviceConfigEntity.setServiceName(ServiceCheckValidityCheckTest.SERVICE_NAME);
        serviceConfigEntity.setCreateTimestamp(ServiceCheckValidityCheckTest.CONFIG_CREATE_TIMESTAMP);
        LastServiceCheckDTO lastServiceCheckDTO = new LastServiceCheckDTO(HDFS_SERVICE_CHECK.name(), ServiceCheckValidityCheckTest.SERVICE_CHECK_START_TIME);
        Mockito.when(serviceConfigDAO.getLastServiceConfig(ArgumentMatchers.eq(ServiceCheckValidityCheckTest.CLUSTER_ID), ArgumentMatchers.eq(ServiceCheckValidityCheckTest.SERVICE_NAME))).thenReturn(serviceConfigEntity);
        Mockito.when(hostRoleCommandDAO.getLatestServiceChecksByRole(ArgumentMatchers.any(Long.class))).thenReturn(Collections.singletonList(lastServiceCheckDTO));
        ClusterInformation clusterInformation = new ClusterInformation(ServiceCheckValidityCheckTest.CLUSTER_NAME, false, null, null, null);
        UpgradeCheckRequest request = new UpgradeCheckRequest(clusterInformation, UpgradeType.ROLLING, null, null, null);
        UpgradeCheckResult result = serviceCheckValidityCheck.perform(request);
        Assert.assertEquals(FAIL, result.getStatus());
    }

    @Test
    public void testFailWhenServiceWithNoServiceCheckExists() throws AmbariException {
        ServiceComponent serviceComponent = Mockito.mock(ServiceComponent.class);
        Mockito.when(serviceComponent.isVersionAdvertised()).thenReturn(true);
        Mockito.when(service.getMaintenanceState()).thenReturn(OFF);
        Mockito.when(service.getServiceComponents()).thenReturn(ImmutableMap.of(ServiceCheckValidityCheckTest.SERVICE_COMPONENT_NAME, serviceComponent));
        ServiceConfigEntity serviceConfigEntity = new ServiceConfigEntity();
        serviceConfigEntity.setServiceName(ServiceCheckValidityCheckTest.SERVICE_NAME);
        serviceConfigEntity.setCreateTimestamp(ServiceCheckValidityCheckTest.CONFIG_CREATE_TIMESTAMP);
        Mockito.when(serviceConfigDAO.getLastServiceConfig(ArgumentMatchers.eq(ServiceCheckValidityCheckTest.CLUSTER_ID), ArgumentMatchers.eq(ServiceCheckValidityCheckTest.SERVICE_NAME))).thenReturn(serviceConfigEntity);
        Mockito.when(hostRoleCommandDAO.getLatestServiceChecksByRole(ArgumentMatchers.any(Long.class))).thenReturn(Collections.<LastServiceCheckDTO>emptyList());
        ClusterInformation clusterInformation = new ClusterInformation(ServiceCheckValidityCheckTest.CLUSTER_NAME, false, null, null, null);
        UpgradeCheckRequest request = new UpgradeCheckRequest(clusterInformation, UpgradeType.ROLLING, null, null, null);
        UpgradeCheckResult result = serviceCheckValidityCheck.perform(request);
        Assert.assertEquals(FAIL, result.getStatus());
    }

    @Test
    public void testFailWhenServiceWithOutdatedServiceCheckExistsRepeated() throws AmbariException {
        ServiceComponent serviceComponent = Mockito.mock(ServiceComponent.class);
        Mockito.when(serviceComponent.isVersionAdvertised()).thenReturn(true);
        Mockito.when(service.getMaintenanceState()).thenReturn(OFF);
        Mockito.when(service.getServiceComponents()).thenReturn(ImmutableMap.of(ServiceCheckValidityCheckTest.SERVICE_COMPONENT_NAME, serviceComponent));
        ServiceConfigEntity serviceConfigEntity = new ServiceConfigEntity();
        serviceConfigEntity.setServiceName(ServiceCheckValidityCheckTest.SERVICE_NAME);
        serviceConfigEntity.setCreateTimestamp(ServiceCheckValidityCheckTest.CONFIG_CREATE_TIMESTAMP);
        LastServiceCheckDTO lastServiceCheckDTO1 = new LastServiceCheckDTO(HDFS_SERVICE_CHECK.name(), ServiceCheckValidityCheckTest.SERVICE_CHECK_START_TIME);
        LastServiceCheckDTO lastServiceCheckDTO2 = new LastServiceCheckDTO(HDFS_SERVICE_CHECK.name(), ((ServiceCheckValidityCheckTest.CONFIG_CREATE_TIMESTAMP) - 1L));
        Mockito.when(serviceConfigDAO.getLastServiceConfig(ArgumentMatchers.eq(ServiceCheckValidityCheckTest.CLUSTER_ID), ArgumentMatchers.eq(ServiceCheckValidityCheckTest.SERVICE_NAME))).thenReturn(serviceConfigEntity);
        Mockito.when(hostRoleCommandDAO.getLatestServiceChecksByRole(ArgumentMatchers.any(Long.class))).thenReturn(Arrays.asList(lastServiceCheckDTO1, lastServiceCheckDTO2));
        ClusterInformation clusterInformation = new ClusterInformation(ServiceCheckValidityCheckTest.CLUSTER_NAME, false, null, null, null);
        UpgradeCheckRequest request = new UpgradeCheckRequest(clusterInformation, UpgradeType.ROLLING, null, null, null);
        UpgradeCheckResult result = serviceCheckValidityCheck.perform(request);
        Assert.assertEquals(FAIL, result.getStatus());
    }

    /**
     * Tests that old, oudated service checks for the FOO2 service doesn't cause
     * problems when checking values for the FOO service.
     * <p/>
     * The specific test case here is that the FOO2 service was added a long time
     * ago and then removed. We don't want old service checks for FOO2 to match
     * when querying for FOO.
     *
     * @throws AmbariException
     * 		
     */
    @Test
    public void testPassWhenSimilarlyNamedServiceIsOutdated() throws AmbariException {
        ServiceComponent serviceComponent = Mockito.mock(ServiceComponent.class);
        Mockito.when(serviceComponent.isVersionAdvertised()).thenReturn(true);
        Mockito.when(service.getMaintenanceState()).thenReturn(OFF);
        Mockito.when(service.getServiceComponents()).thenReturn(ImmutableMap.of(ServiceCheckValidityCheckTest.SERVICE_COMPONENT_NAME, serviceComponent));
        ServiceConfigEntity serviceConfigEntity = new ServiceConfigEntity();
        serviceConfigEntity.setServiceName(ServiceCheckValidityCheckTest.SERVICE_NAME);
        serviceConfigEntity.setCreateTimestamp(ServiceCheckValidityCheckTest.CONFIG_CREATE_TIMESTAMP);
        String hdfsRole = HDFS_SERVICE_CHECK.name();
        String hdfs2Role = hdfsRole.replace("HDFS", "HDFS2");
        LastServiceCheckDTO lastServiceCheckDTO1 = new LastServiceCheckDTO(hdfsRole, ServiceCheckValidityCheckTest.SERVICE_CHECK_START_TIME);
        LastServiceCheckDTO lastServiceCheckDTO2 = new LastServiceCheckDTO(hdfs2Role, ((ServiceCheckValidityCheckTest.CONFIG_CREATE_TIMESTAMP) - 1L));
        Mockito.when(serviceConfigDAO.getLastServiceConfig(ArgumentMatchers.eq(ServiceCheckValidityCheckTest.CLUSTER_ID), ArgumentMatchers.eq(ServiceCheckValidityCheckTest.SERVICE_NAME))).thenReturn(serviceConfigEntity);
        Mockito.when(hostRoleCommandDAO.getLatestServiceChecksByRole(ArgumentMatchers.any(Long.class))).thenReturn(Arrays.asList(lastServiceCheckDTO1, lastServiceCheckDTO2));
        ClusterInformation clusterInformation = new ClusterInformation(ServiceCheckValidityCheckTest.CLUSTER_NAME, false, null, null, null);
        UpgradeCheckRequest request = new UpgradeCheckRequest(clusterInformation, UpgradeType.ROLLING, null, null, null);
        UpgradeCheckResult result = serviceCheckValidityCheck.perform(request);
        Assert.assertEquals(FAIL, result.getStatus());
    }
}

