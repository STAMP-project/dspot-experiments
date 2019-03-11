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
package org.apache.ambari.server.controller.internal;


import Resource.Type;
import Resource.Type.Cluster;
import Resource.Type.Configuration;
import Resource.Type.Service;
import com.google.inject.Injector;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.controller.ClusterRequest;
import org.apache.ambari.server.controller.ConfigurationRequest;
import org.apache.ambari.server.controller.MaintenanceStateHelper;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.controller.spi.ResourceProvider;
import org.apache.ambari.server.orm.dao.RepositoryVersionDAO;
import org.apache.ambari.server.orm.entities.RepositoryVersionEntity;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.ServiceComponent;
import org.apache.ambari.server.state.ServiceComponentHost;
import org.apache.ambari.server.state.StackId;
import org.junit.Assert;
import org.junit.Test;


public class JMXHostProviderTest {
    private Injector injector;

    private Clusters clusters;

    static AmbariManagementController controller;

    private static final String NAMENODE_PORT_V1 = "dfs.http.address";

    private static final String NAMENODE_PORT_V2 = "dfs.namenode.http-address";

    private static final String DATANODE_PORT = "dfs.datanode.http.address";

    private static final String DATANODE_HTTPS_PORT = "dfs.datanode.https.address";

    private static final String RESOURCEMANAGER_PORT = "yarn.resourcemanager.webapp.address";

    private static final String RESOURCEMANAGER_HTTPS_PORT = "yarn.resourcemanager.webapp.https.address";

    private static final String YARN_HTTPS_POLICY = "yarn.http.policy";

    private static final String NODEMANAGER_PORT = "yarn.nodemanager.webapp.address";

    private static final String NODEMANAGER_HTTPS_PORT = "yarn.nodemanager.webapp.https.address";

    private static final String JOURNALNODE_HTTPS_PORT = "dfs.journalnode.https-address";

    private static final String HDFS_HTTPS_POLICY = "dfs.http.policy";

    private static final String MAPREDUCE_HTTPS_POLICY = "mapreduce.jobhistory.http.policy";

    private static final String MAPREDUCE_HTTPS_PORT = "mapreduce.jobhistory.webapp.https.address";

    private final String STACK_VERSION = "2.0.6";

    private final String REPO_VERSION = "2.0.6-1234";

    private final StackId STACK_ID = new StackId("HDP", STACK_VERSION);

    private RepositoryVersionEntity m_repositoryVersion;

    @Test
    public void testJMXPortMapInitAtServiceLevelVersion1() throws Exception {
        createHDFSServiceConfigs(true);
        JMXHostProviderTest.JMXHostProviderModule providerModule = new JMXHostProviderTest.JMXHostProviderModule(JMXHostProviderTest.controller);
        providerModule.registerResourceProvider(Service);
        providerModule.registerResourceProvider(Configuration);
        // Non default port addresses
        Assert.assertEquals("70070", getPort("c1", "NAMENODE", "localhost", false));
        Assert.assertEquals("70075", getPort("c1", "DATANODE", "localhost", false));
        // Default port addresses
        Assert.assertEquals(null, getPort("c1", "JOBTRACKER", "localhost", false));
        Assert.assertEquals(null, getPort("c1", "TASKTRACKER", "localhost", false));
        Assert.assertEquals(null, getPort("c1", "HBASE_MASTER", "localhost", false));
    }

    @Test
    public void testJMXPortMapInitAtServiceLevelVersion2() throws Exception {
        createHDFSServiceConfigs(false);
        JMXHostProviderTest.JMXHostProviderModule providerModule = new JMXHostProviderTest.JMXHostProviderModule(JMXHostProviderTest.controller);
        providerModule.registerResourceProvider(Service);
        providerModule.registerResourceProvider(Configuration);
        // Non default port addresses
        Assert.assertEquals("70071", getPort("c1", "NAMENODE", "localhost", false));
        Assert.assertEquals("70075", getPort("c1", "DATANODE", "localhost", false));
        // Default port addresses
        Assert.assertEquals(null, getPort("c1", "JOBTRACKER", "localhost", false));
        Assert.assertEquals(null, getPort("c1", "TASKTRACKER", "localhost", false));
        Assert.assertEquals(null, getPort("c1", "HBASE_MASTER", "localhost", false));
    }

    @Test
    public void testJMXPortMapNameNodeHa() throws Exception {
        createConfigsNameNodeHa();
        JMXHostProviderTest.JMXHostProviderModule providerModule = new JMXHostProviderTest.JMXHostProviderModule(JMXHostProviderTest.controller);
        providerModule.registerResourceProvider(Service);
        providerModule.registerResourceProvider(Configuration);
        Assert.assertEquals("50071", getPort("nnha", "NAMENODE", "h1", false));
        Assert.assertEquals("50072", getPort("nnha", "NAMENODE", "h2", false));
    }

    @Test
    public void testJMXPortMapInitAtClusterLevel() throws Exception {
        createConfigs();
        JMXHostProviderTest.JMXHostProviderModule providerModule = new JMXHostProviderTest.JMXHostProviderModule(JMXHostProviderTest.controller);
        providerModule.registerResourceProvider(Cluster);
        providerModule.registerResourceProvider(Configuration);
        // Non default port addresses
        Assert.assertEquals("70070", getPort("c1", "NAMENODE", "localhost", false));
        Assert.assertEquals("70075", getPort("c1", "DATANODE", "localhost", false));
        // Default port addresses
        Assert.assertEquals(null, getPort("c1", "JOBTRACKER", "localhost", false));
        Assert.assertEquals(null, getPort("c1", "TASKTRACKER", "localhost", false));
        Assert.assertEquals(null, getPort("c1", "HBASE_MASTER", "localhost", false));
    }

    @Test
    public void testGetHostNames() throws AmbariException {
        AmbariManagementController managementControllerMock = createNiceMock(AmbariManagementController.class);
        JMXHostProviderTest.JMXHostProviderModule providerModule = new JMXHostProviderTest.JMXHostProviderModule(managementControllerMock);
        Clusters clustersMock = createNiceMock(Clusters.class);
        org.apache.ambari.server.state.Cluster clusterMock = createNiceMock(org.apache.ambari.server.state.Cluster.class);
        org.apache.ambari.server.state.Service serviceMock = createNiceMock(org.apache.ambari.server.state.Service.class);
        ServiceComponent serviceComponentMock = createNiceMock(ServiceComponent.class);
        Map<String, ServiceComponentHost> hostComponents = new HashMap<>();
        hostComponents.put("host1", null);
        expect(managementControllerMock.getClusters()).andReturn(clustersMock).anyTimes();
        expect(managementControllerMock.findServiceName(clusterMock, "DATANODE")).andReturn("HDFS");
        expect(clustersMock.getCluster("c1")).andReturn(clusterMock).anyTimes();
        expect(clusterMock.getService("HDFS")).andReturn(serviceMock).anyTimes();
        expect(serviceMock.getServiceComponent("DATANODE")).andReturn(serviceComponentMock).anyTimes();
        expect(serviceComponentMock.getServiceComponentHosts()).andReturn(hostComponents).anyTimes();
        replay(managementControllerMock, clustersMock, clusterMock, serviceMock, serviceComponentMock);
        Set<String> result = getHostNames("c1", "DATANODE");
        Assert.assertTrue(result.iterator().next().equals("host1"));
    }

    @Test
    public void testJMXHttpsPort() throws Exception {
        createConfigs();
        JMXHostProviderTest.JMXHostProviderModule providerModule = new JMXHostProviderTest.JMXHostProviderModule(JMXHostProviderTest.controller);
        providerModule.registerResourceProvider(Cluster);
        providerModule.registerResourceProvider(Configuration);
        Assert.assertEquals("https", getJMXProtocol("c1", "RESOURCEMANAGER"));
        Assert.assertEquals("8090", getPort("c1", "RESOURCEMANAGER", "localhost", true));
        Assert.assertEquals("https", getJMXProtocol("c1", "NODEMANAGER"));
        Assert.assertEquals("8044", getPort("c1", "NODEMANAGER", "localhost", true));
    }

    @Test
    public void testJMXHistoryServerHttpsPort() throws Exception {
        createConfigs();
        JMXHostProviderTest.JMXHostProviderModule providerModule = new JMXHostProviderTest.JMXHostProviderModule(JMXHostProviderTest.controller);
        providerModule.registerResourceProvider(Cluster);
        providerModule.registerResourceProvider(Configuration);
        Assert.assertEquals("https", getJMXProtocol("c1", "HISTORYSERVER"));
        Assert.assertEquals("19889", getPort("c1", "HISTORYSERVER", "localhost", true));
    }

    @Test
    public void testJMXJournalNodeHttpsPort() throws Exception {
        createConfigs();
        JMXHostProviderTest.JMXHostProviderModule providerModule = new JMXHostProviderTest.JMXHostProviderModule(JMXHostProviderTest.controller);
        providerModule.registerResourceProvider(Cluster);
        providerModule.registerResourceProvider(Configuration);
        Assert.assertEquals("https", getJMXProtocol("c1", "JOURNALNODE"));
        Assert.assertEquals("8481", getPort("c1", "JOURNALNODE", "localhost", true));
    }

    @Test
    public void testJMXDataNodeHttpsPort() throws Exception {
        createConfigs();
        JMXHostProviderTest.JMXHostProviderModule providerModule = new JMXHostProviderTest.JMXHostProviderModule(JMXHostProviderTest.controller);
        providerModule.registerResourceProvider(Cluster);
        providerModule.registerResourceProvider(Configuration);
        Assert.assertEquals("https", getJMXProtocol("c1", "DATANODE"));
        Assert.assertEquals("50475", getPort("c1", "DATANODE", "localhost", true));
    }

    @Test
    public void testJMXHbaseMasterHttps() throws Exception {
        createConfigs();
        JMXHostProviderTest.JMXHostProviderModule providerModule = new JMXHostProviderTest.JMXHostProviderModule(JMXHostProviderTest.controller);
        providerModule.registerResourceProvider(Cluster);
        providerModule.registerResourceProvider(Configuration);
        Assert.assertEquals("https", getJMXProtocol("c1", "HBASE_MASTER"));
        Assert.assertEquals("https", getJMXProtocol("c1", "HBASE_REGIONSERVER"));
    }

    @Test
    public void testJMXPortMapUpdate() throws Exception {
        createConfigs();
        JMXHostProviderTest.JMXHostProviderModule providerModule = new JMXHostProviderTest.JMXHostProviderModule(JMXHostProviderTest.controller);
        providerModule.registerResourceProvider(Cluster);
        providerModule.registerResourceProvider(Configuration);
        // Non default port addresses
        Assert.assertEquals("8088", getPort("c1", "RESOURCEMANAGER", "localhost", false));
        Map<String, String> yarnConfigs = new HashMap<>();
        yarnConfigs.put(JMXHostProviderTest.RESOURCEMANAGER_PORT, "localhost:50030");
        yarnConfigs.put(JMXHostProviderTest.NODEMANAGER_PORT, "localhost:11111");
        ConfigurationRequest cr2 = new ConfigurationRequest("c1", "yarn-site", "versionN+1", yarnConfigs, null);
        ClusterRequest crReq = new ClusterRequest(1L, "c1", null, null);
        crReq.setDesiredConfig(Collections.singletonList(cr2));
        JMXHostProviderTest.controller.updateClusters(Collections.singleton(crReq), null);
        Assert.assertEquals("50030", getPort("c1", "RESOURCEMANAGER", "localhost", false));
        Assert.assertEquals("11111", getPort("c1", "NODEMANAGER", "localhost", false));
        // Unrelated ports
        Assert.assertEquals("70070", getPort("c1", "NAMENODE", "localhost", false));
        Assert.assertEquals(null, getPort("c1", "JOBTRACKER", "localhost", false));
        // test another host and component without property
        Assert.assertNull(getPort("c1", "HBASE_REGIONSERVER", "remotehost1", false));
    }

    private static class JMXHostProviderModule extends AbstractProviderModule {
        ResourceProvider clusterResourceProvider = new ClusterResourceProvider(JMXHostProviderTest.controller);

        Injector injector = createNiceMock(Injector.class);

        MaintenanceStateHelper maintenanceStateHelper = createNiceMock(MaintenanceStateHelper.class);

        RepositoryVersionDAO repositoryVersionDAO = createNiceMock(RepositoryVersionDAO.class);

        {
            expect(injector.getInstance(Clusters.class)).andReturn(null);
            replay(maintenanceStateHelper, injector);
        }

        ResourceProvider serviceResourceProvider = new ServiceResourceProvider(JMXHostProviderTest.controller, maintenanceStateHelper, repositoryVersionDAO);

        ResourceProvider hostCompResourceProvider = new HostComponentResourceProvider(JMXHostProviderTest.controller);

        ResourceProvider configResourceProvider = new ConfigurationResourceProvider(JMXHostProviderTest.controller);

        JMXHostProviderModule(AmbariManagementController ambariManagementController) {
            super();
            managementController = ambariManagementController;
        }

        @Override
        protected ResourceProvider createResourceProvider(Resource.Type type) {
            if (type == (Type.Cluster)) {
                return clusterResourceProvider;
            }
            if (type == (Type.Service)) {
                return serviceResourceProvider;
            } else
                if (type == (Type.HostComponent)) {
                    return hostCompResourceProvider;
                } else
                    if (type == (Type.Configuration)) {
                        return configResourceProvider;
                    }


            return null;
        }
    }
}

