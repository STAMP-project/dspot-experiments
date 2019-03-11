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
package org.apache.ambari.server.state;


import MaintenanceState.ON;
import State.INSTALLED;
import State.INSTALLING;
import State.STARTED;
import com.google.inject.Injector;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.controller.ServiceResponse;
import org.apache.ambari.server.controller.internal.DeleteHostComponentStatusMetaData;
import org.apache.ambari.server.orm.OrmTestHelper;
import org.apache.ambari.server.orm.dao.ClusterServiceDAO;
import org.apache.ambari.server.orm.entities.ClusterConfigEntity;
import org.apache.ambari.server.orm.entities.ClusterServiceEntity;
import org.apache.ambari.server.orm.entities.RepositoryVersionEntity;
import org.apache.ambari.server.state.configgroup.ConfigGroup;
import org.apache.commons.collections.MapUtils;
import org.junit.Assert;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;


public class ServiceTest {
    private Clusters clusters;

    private Cluster cluster;

    private String clusterName;

    private Injector injector;

    private ServiceFactory serviceFactory;

    private ServiceComponentFactory serviceComponentFactory;

    private ServiceComponentHostFactory serviceComponentHostFactory;

    private OrmTestHelper ormTestHelper;

    private ConfigFactory configFactory;

    private final String STACK_VERSION = "0.1";

    private final String REPO_VERSION = "0.1-1234";

    private final StackId STACK_ID = new StackId("HDP", STACK_VERSION);

    private RepositoryVersionEntity repositoryVersion;

    @Test
    public void testCanBeRemoved() throws Exception {
        Service service = cluster.addService("HDFS", repositoryVersion);
        for (State state : State.values()) {
            service.setDesiredState(state);
            // service does not have any components, so it can be removed,
            // even if the service is in non-removable state.
            Assert.assertTrue(service.canBeRemoved());
        }
        ServiceComponent component = service.addServiceComponent("NAMENODE");
        // component can be removed
        component.setDesiredState(INSTALLED);
        for (State state : State.values()) {
            service.setDesiredState(state);
            // should always be true if the sub component can be removed
            Assert.assertTrue(service.canBeRemoved());
        }
        // can remove a STARTED component as whether a service can be removed
        // is ultimately decided based on if the host components can be removed
        component.setDesiredState(INSTALLED);
        addHostToCluster("h1", service.getCluster().getClusterName());
        ServiceComponentHost sch = serviceComponentHostFactory.createNew(component, "h1");
        component.addServiceComponentHost(sch);
        sch.setDesiredState(STARTED);
        sch.setState(STARTED);
        for (State state : State.values()) {
            service.setDesiredState(state);
            // should always be false if the sub component can not be removed
            Assert.assertFalse(service.canBeRemoved());
        }
        sch.setDesiredState(INSTALLED);
        sch.setState(INSTALLED);
    }

    @Test
    public void testGetAndSetServiceInfo() throws AmbariException {
        String serviceName = "HDFS";
        Service s = serviceFactory.createNew(cluster, serviceName, repositoryVersion);
        cluster.addService(s);
        Service service = cluster.getService(serviceName);
        assertNotNull(service);
        StackId desiredStackId = new StackId("HDP-1.2.0");
        String desiredVersion = "1.2.0-1234";
        RepositoryVersionEntity desiredRepositoryVersion = ormTestHelper.getOrCreateRepositoryVersion(desiredStackId, desiredVersion);
        service.setDesiredRepositoryVersion(desiredRepositoryVersion);
        assertEquals(desiredStackId, service.getDesiredStackId());
        service.setDesiredState(INSTALLING);
        assertEquals(INSTALLING, service.getDesiredState());
        // FIXME todo use DAO to verify persisted object maps to inmemory state
    }

    @Test
    public void testAddGetDeleteServiceComponents() throws AmbariException {
        String serviceName = "HDFS";
        Service s = serviceFactory.createNew(cluster, serviceName, repositoryVersion);
        cluster.addService(s);
        Service service = cluster.getService(serviceName);
        assertNotNull(service);
        assertEquals(serviceName, service.getName());
        assertEquals(cluster.getClusterId(), service.getCluster().getClusterId());
        assertEquals(cluster.getClusterName(), service.getCluster().getClusterName());
        assertEquals(State.INIT, service.getDesiredState());
        assertFalse(service.getDesiredStackId().getStackId().isEmpty());
        assertTrue(s.getServiceComponents().isEmpty());
        ServiceComponent sc1 = serviceComponentFactory.createNew(s, "NAMENODE");
        ServiceComponent sc2 = serviceComponentFactory.createNew(s, "DATANODE1");
        ServiceComponent sc3 = serviceComponentFactory.createNew(s, "DATANODE2");
        Map<String, ServiceComponent> comps = new HashMap<>();
        comps.put(sc1.getName(), sc1);
        comps.put(sc2.getName(), sc2);
        s.addServiceComponents(comps);
        junit.framework.Assert.assertEquals(2, s.getServiceComponents().size());
        assertNotNull(s.getServiceComponent(sc1.getName()));
        assertNotNull(s.getServiceComponent(sc2.getName()));
        try {
            s.getServiceComponent(sc3.getName());
            Assert.fail("Expected error when looking for invalid component");
        } catch (Exception e) {
            // Expected
        }
        s.addServiceComponent(sc3);
        ServiceComponent sc4 = s.addServiceComponent("HDFS_CLIENT");
        assertNotNull(s.getServiceComponent(sc4.getName()));
        assertEquals(State.INIT, s.getServiceComponent("HDFS_CLIENT").getDesiredState());
        assertTrue(sc4.isClientComponent());
        junit.framework.Assert.assertEquals(4, s.getServiceComponents().size());
        assertNotNull(s.getServiceComponent(sc3.getName()));
        assertEquals(sc3.getName(), s.getServiceComponent(sc3.getName()).getName());
        assertEquals(s.getName(), s.getServiceComponent(sc3.getName()).getServiceName());
        assertEquals(cluster.getClusterName(), s.getServiceComponent(sc3.getName()).getClusterName());
        sc4.setDesiredState(INSTALLING);
        assertEquals(INSTALLING, s.getServiceComponent("HDFS_CLIENT").getDesiredState());
        // delete service component
        s.deleteServiceComponent("NAMENODE", new DeleteHostComponentStatusMetaData());
        Assert.assertEquals(3, s.getServiceComponents().size());
    }

    @Test
    public void testGetAndSetConfigs() {
        // FIXME add unit tests for configs once impl done
        /* public Map<String, Config> getDesiredConfigs();
        public void updateDesiredConfigs(Map<String, Config> configs);
         */
    }

    @Test
    public void testConvertToResponse() throws AmbariException {
        String serviceName = "HDFS";
        Service s = serviceFactory.createNew(cluster, serviceName, repositoryVersion);
        cluster.addService(s);
        Service service = cluster.getService(serviceName);
        assertNotNull(service);
        ServiceResponse r = s.convertToResponse();
        assertEquals(s.getName(), r.getServiceName());
        assertEquals(s.getCluster().getClusterName(), r.getClusterName());
        assertEquals(s.getDesiredStackId().getStackId(), r.getDesiredStackId());
        assertEquals(s.getDesiredState().toString(), r.getDesiredState());
        StackId desiredStackId = new StackId("HDP-1.2.0");
        String desiredVersion = "1.2.0-1234";
        RepositoryVersionEntity desiredRepositoryVersion = ormTestHelper.getOrCreateRepositoryVersion(desiredStackId, desiredVersion);
        service.setDesiredRepositoryVersion(desiredRepositoryVersion);
        service.setDesiredState(INSTALLING);
        r = s.convertToResponse();
        assertEquals(s.getName(), r.getServiceName());
        assertEquals(s.getCluster().getClusterName(), r.getClusterName());
        assertEquals(s.getDesiredStackId().getStackId(), r.getDesiredStackId());
        assertEquals(s.getDesiredState().toString(), r.getDesiredState());
        // FIXME add checks for configs
        StringBuilder sb = new StringBuilder();
        s.debugDump(sb);
        // TODO better checks?
        assertFalse(sb.toString().isEmpty());
    }

    @Test
    public void testServiceMaintenance() throws Exception {
        String serviceName = "HDFS";
        Service s = serviceFactory.createNew(cluster, serviceName, repositoryVersion);
        cluster.addService(s);
        Service service = cluster.getService(serviceName);
        assertNotNull(service);
        ClusterServiceDAO dao = injector.getInstance(ClusterServiceDAO.class);
        ClusterServiceEntity entity = dao.findByClusterAndServiceNames(clusterName, serviceName);
        assertNotNull(entity);
        assertEquals(MaintenanceState.OFF, entity.getServiceDesiredStateEntity().getMaintenanceState());
        assertEquals(MaintenanceState.OFF, service.getMaintenanceState());
        service.setMaintenanceState(ON);
        assertEquals(ON, service.getMaintenanceState());
        entity = dao.findByClusterAndServiceNames(clusterName, serviceName);
        assertNotNull(entity);
        assertEquals(ON, entity.getServiceDesiredStateEntity().getMaintenanceState());
    }

    /**
     * Tests the kerberosEnabledTest value set in the HDFS metainfo file (stacks/HDP/0.1/services/HDFS/metainfo.xml):
     * <pre>
     * {
     *   "or": [
     *     {
     *       "equals": [
     *         "core-site/hadoop.security.authentication",
     *         "kerberos"
     *       ]
     *     },
     *     {
     *       "equals": [
     *         "hdfs-site/hadoop.security.authentication",
     *         "kerberos"
     *       ]
     *     }
     *   ]
     * }
     * </pre>
     */
    @Test
    public void testServiceKerberosEnabledTest() throws Exception {
        String serviceName = "HDFS";
        Service s = serviceFactory.createNew(cluster, serviceName, repositoryVersion);
        cluster.addService(s);
        Service service = cluster.getService(serviceName);
        assertNotNull(service);
        Map<String, Map<String, String>> map = new HashMap<>();
        assertFalse(service.isKerberosEnabled(null));
        assertFalse(service.isKerberosEnabled(map));
        map.put("core-site", Collections.singletonMap("hadoop.security.authentication", "none"));
        map.put("hdfs-site", Collections.singletonMap("hadoop.security.authentication", "none"));
        assertFalse(service.isKerberosEnabled(map));
        map.put("core-site", Collections.singletonMap("hadoop.security.authentication", "kerberos"));
        map.put("hdfs-site", Collections.singletonMap("hadoop.security.authentication", "none"));
        assertTrue(service.isKerberosEnabled(map));
        map.put("core-site", Collections.singletonMap("hadoop.security.authentication", "none"));
        map.put("hdfs-site", Collections.singletonMap("hadoop.security.authentication", "kerberos"));
        assertTrue(service.isKerberosEnabled(map));
        map.put("core-site", Collections.singletonMap("hadoop.security.authentication", "kerberos"));
        map.put("hdfs-site", Collections.singletonMap("hadoop.security.authentication", "kerberos"));
        assertTrue(service.isKerberosEnabled(map));
    }

    @Test
    public void testClusterConfigsUnmappingOnDeleteAllServiceConfigs() throws AmbariException {
        String serviceName = "HDFS";
        Service s = serviceFactory.createNew(cluster, serviceName, repositoryVersion);
        cluster.addService(s);
        Service service = cluster.getService(serviceName);
        // add some cluster and service configs
        Config config1 = configFactory.createNew(cluster, "hdfs-site", "version1", new HashMap<String, String>() {
            {
                put("a", "b");
            }
        }, new HashMap());
        cluster.addDesiredConfig("admin", Collections.singleton(config1));
        Config config2 = configFactory.createNew(cluster, "hdfs-site", "version2", new HashMap<String, String>() {
            {
                put("a", "b");
            }
        }, new HashMap());
        cluster.addDesiredConfig("admin", Collections.singleton(config2));
        // check all cluster configs are mapped
        Collection<ClusterConfigEntity> clusterConfigEntities = cluster.getClusterEntity().getClusterConfigEntities();
        for (ClusterConfigEntity clusterConfigEntity : clusterConfigEntities) {
            Assert.assertFalse(clusterConfigEntity.isUnmapped());
        }
        deleteAllServiceConfigs();
        // check all cluster configs are unmapped
        clusterConfigEntities = cluster.getClusterEntity().getClusterConfigEntities();
        for (ClusterConfigEntity clusterConfigEntity : clusterConfigEntities) {
            Assert.assertTrue(clusterConfigEntity.isUnmapped());
        }
    }

    @Test
    public void testDeleteAllServiceConfigGroups() throws AmbariException {
        String serviceName = "HDFS";
        Service s = serviceFactory.createNew(cluster, serviceName, repositoryVersion);
        cluster.addService(s);
        Service service = cluster.getService(serviceName);
        ConfigGroup configGroup1 = createNiceMock(ConfigGroup.class);
        ConfigGroup configGroup2 = createNiceMock(ConfigGroup.class);
        expect(configGroup1.getId()).andReturn(1L).anyTimes();
        expect(configGroup2.getId()).andReturn(2L).anyTimes();
        expect(configGroup1.getServiceName()).andReturn(serviceName).anyTimes();
        expect(configGroup2.getServiceName()).andReturn(serviceName).anyTimes();
        expect(configGroup1.getClusterName()).andReturn(cluster.getClusterName()).anyTimes();
        expect(configGroup2.getClusterName()).andReturn(cluster.getClusterName()).anyTimes();
        replay(configGroup1, configGroup2);
        cluster.addConfigGroup(configGroup1);
        cluster.addConfigGroup(configGroup2);
        deleteAllServiceConfigGroups();
        Assert.assertTrue(MapUtils.isEmpty(cluster.getConfigGroupsByServiceName(serviceName)));
        verify(configGroup1, configGroup2);
    }
}

