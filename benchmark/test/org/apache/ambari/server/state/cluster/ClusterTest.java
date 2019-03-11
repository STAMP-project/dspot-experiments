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
package org.apache.ambari.server.state.cluster;


import HostHealthStatus.HealthStatus;
import HostState.HEALTHY;
import HostState.HEARTBEAT_LOST;
import HostState.UNHEALTHY;
import MaintenanceState.ON;
import RepositoryVersionState.CURRENT;
import RepositoryVersionState.INSTALLED;
import RepositoryVersionState.INSTALLING;
import RepositoryVersionState.INSTALL_FAILED;
import RepositoryVersionState.NOT_REQUIRED;
import RepositoryVersionState.OUT_OF_SYNC;
import ServiceConfigVersionResponse.DEFAULT_CONFIG_GROUP_NAME;
import ServiceConfigVersionResponse.DELETED_CONFIG_GROUP_NAME;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManager;
import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.agent.AgentEnv;
import org.apache.ambari.server.agent.AgentEnv.Directory;
import org.apache.ambari.server.agent.DiskInfo;
import org.apache.ambari.server.agent.HostInfo;
import org.apache.ambari.server.api.services.AmbariMetaInfo;
import org.apache.ambari.server.controller.ClusterResponse;
import org.apache.ambari.server.controller.ConfigurationResponse;
import org.apache.ambari.server.controller.ServiceConfigVersionResponse;
import org.apache.ambari.server.controller.internal.DeleteHostComponentStatusMetaData;
import org.apache.ambari.server.events.ClusterConfigChangedEvent;
import org.apache.ambari.server.events.publishers.AmbariEventPublisher;
import org.apache.ambari.server.orm.OrmTestHelper;
import org.apache.ambari.server.orm.dao.ClusterDAO;
import org.apache.ambari.server.orm.dao.HostComponentStateDAO;
import org.apache.ambari.server.orm.dao.HostDAO;
import org.apache.ambari.server.orm.dao.HostVersionDAO;
import org.apache.ambari.server.orm.dao.RepositoryVersionDAO;
import org.apache.ambari.server.orm.dao.StackDAO;
import org.apache.ambari.server.orm.entities.ClusterConfigEntity;
import org.apache.ambari.server.orm.entities.ClusterEntity;
import org.apache.ambari.server.orm.entities.HostComponentStateEntity;
import org.apache.ambari.server.orm.entities.HostEntity;
import org.apache.ambari.server.orm.entities.HostVersionEntity;
import org.apache.ambari.server.orm.entities.RepositoryVersionEntity;
import org.apache.ambari.server.orm.entities.StackEntity;
import org.apache.ambari.server.state.AgentVersion;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.ComponentInfo;
import org.apache.ambari.server.state.Config;
import org.apache.ambari.server.state.ConfigFactory;
import org.apache.ambari.server.state.ConfigHelper;
import org.apache.ambari.server.state.DesiredConfig;
import org.apache.ambari.server.state.Host;
import org.apache.ambari.server.state.RepositoryVersionState;
import org.apache.ambari.server.state.Service;
import org.apache.ambari.server.state.ServiceComponent;
import org.apache.ambari.server.state.ServiceComponentFactory;
import org.apache.ambari.server.state.ServiceComponentHost;
import org.apache.ambari.server.state.ServiceComponentHostFactory;
import org.apache.ambari.server.state.ServiceFactory;
import org.apache.ambari.server.state.StackId;
import org.apache.ambari.server.state.configgroup.ConfigGroup;
import org.apache.ambari.server.state.configgroup.ConfigGroupFactory;
import org.apache.ambari.server.state.fsm.InvalidStateTransitionException;
import org.apache.ambari.server.state.host.HostHealthyHeartbeatEvent;
import org.apache.ambari.server.utils.EventBusSynchronizer;
import org.apache.commons.lang.StringUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;


public class ClusterTest {
    private static final EnumSet<RepositoryVersionState> TERMINAL_VERSION_STATES = EnumSet.of(CURRENT, INSTALLED);

    private Clusters clusters;

    private Cluster c1;

    private Injector injector;

    private ServiceFactory serviceFactory;

    private ServiceComponentFactory serviceComponentFactory;

    private ServiceComponentHostFactory serviceComponentHostFactory;

    private AmbariMetaInfo metaInfo;

    private ConfigFactory configFactory;

    private ConfigGroupFactory configGroupFactory;

    private OrmTestHelper helper;

    private StackDAO stackDAO;

    private ClusterDAO clusterDAO;

    private HostDAO hostDAO;

    private HostVersionDAO hostVersionDAO;

    private HostComponentStateDAO hostComponentStateDAO;

    private RepositoryVersionDAO repositoryVersionDAO;

    private Gson gson;

    private static class MockModule extends AbstractModule {
        @Override
        protected void configure() {
            EventBusSynchronizer.synchronizeAmbariEventPublisher(binder());
        }
    }

    @Test
    public void testAddHost() throws Exception {
        createDefaultCluster();
        clusters.addHost("h3");
        try {
            clusters.addHost("h3");
            Assert.fail("Duplicate add should fail");
        } catch (AmbariException e) {
            // Expected
        }
    }

    @Test
    public void testGetHostState() throws Exception {
        createDefaultCluster();
        junit.framework.Assert.assertEquals(HostState.INIT, clusters.getHost("h1").getState());
    }

    @Test
    public void testSetHostState() throws Exception {
        createDefaultCluster();
        clusters.getHost("h1").setState(HEARTBEAT_LOST);
        junit.framework.Assert.assertEquals(HEARTBEAT_LOST, clusters.getHost("h1").getState());
    }

    @Test
    public void testHostEvent() throws Exception, InvalidStateTransitionException {
        createDefaultCluster();
        HostInfo hostInfo = new HostInfo();
        hostInfo.setHostName("h1");
        hostInfo.setInterfaces("fip_4");
        hostInfo.setArchitecture("os_arch");
        hostInfo.setOS("os_type");
        hostInfo.setMemoryTotal(10);
        hostInfo.setMemorySize(100);
        hostInfo.setProcessorCount(10);
        List<DiskInfo> mounts = new ArrayList<>();
        mounts.add(new DiskInfo("/dev/sda", "/mnt/disk1", "5000000", "4000000", "10%", "size", "fstype"));
        hostInfo.setMounts(mounts);
        AgentEnv agentEnv = new AgentEnv();
        Directory dir1 = new Directory();
        dir1.setName("/etc/hadoop");
        dir1.setType("not_exist");
        Directory dir2 = new Directory();
        dir2.setName("/var/log/hadoop");
        dir2.setType("not_exist");
        agentEnv.setStackFoldersAndFiles(new Directory[]{ dir1, dir2 });
        AgentVersion agentVersion = new AgentVersion("0.0.x");
        long currentTime = 1001;
        clusters.getHost("h1").handleEvent(new org.apache.ambari.server.state.host.HostRegistrationRequestEvent("h1", agentVersion, currentTime, hostInfo, agentEnv, currentTime));
        junit.framework.Assert.assertEquals(HostState.WAITING_FOR_HOST_STATUS_UPDATES, clusters.getHost("h1").getState());
        clusters.getHost("h1").setState(HEARTBEAT_LOST);
        try {
            clusters.getHost("h1").handleEvent(new HostHealthyHeartbeatEvent("h1", currentTime, null, null));
            Assert.fail("Exception should be thrown on invalid event");
        } catch (InvalidStateTransitionException e) {
            // Expected
        }
    }

    @Test
    public void testBasicClusterSetup() throws Exception {
        StackId stackVersion = new StackId("HDP-1.2.0");
        createDefaultCluster();
        String clusterName = "c2";
        try {
            clusters.getCluster(clusterName);
            Assert.fail("Exception expected for invalid cluster");
        } catch (Exception e) {
            // Expected
        }
        clusters.addCluster(clusterName, stackVersion);
        Cluster c2 = clusters.getCluster(clusterName);
        assertNotNull(c2);
        junit.framework.Assert.assertEquals(clusterName, c2.getClusterName());
        c2.setClusterName("foo2");
        junit.framework.Assert.assertEquals("foo2", c2.getClusterName());
        assertNotNull(c2.getDesiredStackVersion());
        junit.framework.Assert.assertEquals("HDP-1.2.0", c2.getDesiredStackVersion().getStackId());
    }

    @Test
    public void testAddAndGetServices() throws Exception {
        createDefaultCluster();
        // TODO write unit tests for
        // public void addService(Service service) throws AmbariException;
        // public Service getService(String serviceName) throws AmbariException;
        // public Map<String, Service> getServices();
        RepositoryVersionEntity repositoryVersion = helper.getOrCreateRepositoryVersion(c1);
        serviceFactory.createNew(c1, "HDFS", repositoryVersion);
        serviceFactory.createNew(c1, "MAPREDUCE", repositoryVersion);
        Service s = c1.getService("HDFS");
        assertNotNull(s);
        junit.framework.Assert.assertEquals("HDFS", s.getName());
        junit.framework.Assert.assertEquals(c1.getClusterId(), s.getClusterId());
        try {
            c1.getService("HBASE");
            Assert.fail("Expected error for unknown service");
        } catch (Exception e) {
            // Expected
        }
        Map<String, Service> services = c1.getServices();
        assertEquals(2, services.size());
        assertTrue(services.containsKey("HDFS"));
        assertTrue(services.containsKey("MAPREDUCE"));
    }

    @Test
    public void testGetServiceComponentHosts() throws Exception {
        createDefaultCluster();
        // TODO write unit tests
        // public List<ServiceComponentHost> getServiceComponentHosts(String hostname);
        RepositoryVersionEntity repositoryVersion = helper.getOrCreateRepositoryVersion(c1);
        Service s = serviceFactory.createNew(c1, "HDFS", repositoryVersion);
        c1.addService(s);
        ServiceComponent sc = serviceComponentFactory.createNew(s, "NAMENODE");
        s.addServiceComponent(sc);
        ServiceComponentHost sch = serviceComponentHostFactory.createNew(sc, "h1");
        sc.addServiceComponentHost(sch);
        List<ServiceComponentHost> scHosts = c1.getServiceComponentHosts("h1");
        assertEquals(1, scHosts.size());
        Iterator<ServiceComponentHost> iterator = scHosts.iterator();
        // Try to iterate on sch and modify it in loop
        try {
            while (iterator.hasNext()) {
                iterator.next();
                Service s1 = serviceFactory.createNew(c1, "PIG", repositoryVersion);
                c1.addService(s1);
                ServiceComponent sc1 = serviceComponentFactory.createNew(s1, "PIG");
                s1.addServiceComponent(sc1);
                ServiceComponentHost sch1 = serviceComponentHostFactory.createNew(sc1, "h1");
                sc1.addServiceComponentHost(sch1);
            } 
        } catch (ConcurrentModificationException e) {
            junit.framework.Assert.assertTrue("Failed to work concurrently with sch", false);
        }
        scHosts = c1.getServiceComponentHosts("h1");
        assertEquals(2, scHosts.size());
    }

    @Test
    public void testGetServiceComponentHosts_ForService() throws Exception {
        createDefaultCluster();
        RepositoryVersionEntity repositoryVersion = helper.getOrCreateRepositoryVersion(c1);
        Service s = serviceFactory.createNew(c1, "HDFS", repositoryVersion);
        c1.addService(s);
        ServiceComponent scNN = serviceComponentFactory.createNew(s, "NAMENODE");
        s.addServiceComponent(scNN);
        ServiceComponentHost schNNH1 = serviceComponentHostFactory.createNew(scNN, "h1");
        scNN.addServiceComponentHost(schNNH1);
        ServiceComponent scDN = serviceComponentFactory.createNew(s, "DATANODE");
        s.addServiceComponent(scDN);
        ServiceComponentHost scDNH1 = serviceComponentHostFactory.createNew(scDN, "h1");
        scDN.addServiceComponentHost(scDNH1);
        ServiceComponentHost scDNH2 = serviceComponentHostFactory.createNew(scDN, "h2");
        scDN.addServiceComponentHost(scDNH2);
        List<ServiceComponentHost> scHosts;
        scHosts = c1.getServiceComponentHosts("HDFS", null);
        assertEquals(3, scHosts.size());
        scHosts = c1.getServiceComponentHosts("UNKNOWN SERVICE", null);
        assertEquals(0, scHosts.size());
    }

    @Test
    public void testGetServiceComponentHosts_ForServiceComponent() throws Exception {
        createDefaultCluster();
        RepositoryVersionEntity repositoryVersion = helper.getOrCreateRepositoryVersion(c1);
        Service s = serviceFactory.createNew(c1, "HDFS", repositoryVersion);
        c1.addService(s);
        ServiceComponent scNN = serviceComponentFactory.createNew(s, "NAMENODE");
        s.addServiceComponent(scNN);
        ServiceComponentHost schNNH1 = serviceComponentHostFactory.createNew(scNN, "h1");
        scNN.addServiceComponentHost(schNNH1);
        ServiceComponent scDN = serviceComponentFactory.createNew(s, "DATANODE");
        s.addServiceComponent(scDN);
        ServiceComponentHost scDNH1 = serviceComponentHostFactory.createNew(scDN, "h1");
        scDN.addServiceComponentHost(scDNH1);
        ServiceComponentHost scDNH2 = serviceComponentHostFactory.createNew(scDN, "h2");
        scDN.addServiceComponentHost(scDNH2);
        List<ServiceComponentHost> scHosts;
        scHosts = c1.getServiceComponentHosts("HDFS", "DATANODE");
        assertEquals(2, scHosts.size());
        scHosts = c1.getServiceComponentHosts("HDFS", "UNKNOWN COMPONENT");
        assertEquals(0, scHosts.size());
        scHosts = c1.getServiceComponentHosts("UNKNOWN SERVICE", "DATANODE");
        assertEquals(0, scHosts.size());
        scHosts = c1.getServiceComponentHosts("UNKNOWN SERVICE", "UNKNOWN COMPONENT");
        assertEquals(0, scHosts.size());
    }

    @Test
    public void testGetServiceComponentHostMap() throws Exception {
        createDefaultCluster();
        RepositoryVersionEntity repositoryVersion = helper.getOrCreateRepositoryVersion(c1);
        Service s = serviceFactory.createNew(c1, "HDFS", repositoryVersion);
        c1.addService(s);
        ServiceComponent scNN = serviceComponentFactory.createNew(s, "NAMENODE");
        s.addServiceComponent(scNN);
        ServiceComponentHost schNNH1 = serviceComponentHostFactory.createNew(scNN, "h1");
        scNN.addServiceComponentHost(schNNH1);
        ServiceComponent scDN = serviceComponentFactory.createNew(s, "DATANODE");
        s.addServiceComponent(scDN);
        ServiceComponentHost scDNH1 = serviceComponentHostFactory.createNew(scDN, "h1");
        scDN.addServiceComponentHost(scDNH1);
        ServiceComponentHost scDNH2 = serviceComponentHostFactory.createNew(scDN, "h2");
        scDN.addServiceComponentHost(scDNH2);
        Map<String, Set<String>> componentHostMap;
        componentHostMap = c1.getServiceComponentHostMap(null, null);
        junit.framework.Assert.assertEquals(2, componentHostMap.size());
        junit.framework.Assert.assertEquals(1, componentHostMap.get("NAMENODE").size());
        assertTrue(componentHostMap.get("NAMENODE").contains("h1"));
        junit.framework.Assert.assertEquals(2, componentHostMap.get("DATANODE").size());
        assertTrue(componentHostMap.get("DATANODE").contains("h1"));
        assertTrue(componentHostMap.get("DATANODE").contains("h2"));
    }

    @Test
    public void testGetServiceComponentHostMap_ForService() throws Exception {
        createDefaultCluster();
        RepositoryVersionEntity repositoryVersion = helper.getOrCreateRepositoryVersion(c1);
        Service sfHDFS = serviceFactory.createNew(c1, "HDFS", repositoryVersion);
        c1.addService(sfHDFS);
        Service sfMR = serviceFactory.createNew(c1, "MAPREDUCE", repositoryVersion);
        c1.addService(sfMR);
        ServiceComponent scNN = serviceComponentFactory.createNew(sfHDFS, "NAMENODE");
        sfHDFS.addServiceComponent(scNN);
        ServiceComponentHost schNNH1 = serviceComponentHostFactory.createNew(scNN, "h1");
        scNN.addServiceComponentHost(schNNH1);
        ServiceComponent scDN = serviceComponentFactory.createNew(sfHDFS, "DATANODE");
        sfHDFS.addServiceComponent(scDN);
        ServiceComponentHost scDNH1 = serviceComponentHostFactory.createNew(scDN, "h1");
        scDN.addServiceComponentHost(scDNH1);
        ServiceComponentHost scDNH2 = serviceComponentHostFactory.createNew(scDN, "h2");
        scDN.addServiceComponentHost(scDNH2);
        ServiceComponent scJT = serviceComponentFactory.createNew(sfMR, "JOBTRACKER");
        sfMR.addServiceComponent(scJT);
        ServiceComponentHost schJTH1 = serviceComponentHostFactory.createNew(scJT, "h1");
        scJT.addServiceComponentHost(schJTH1);
        Map<String, Set<String>> componentHostMap;
        componentHostMap = c1.getServiceComponentHostMap(null, Collections.singleton("HDFS"));
        junit.framework.Assert.assertEquals(2, componentHostMap.size());
        junit.framework.Assert.assertEquals(1, componentHostMap.get("NAMENODE").size());
        assertTrue(componentHostMap.get("NAMENODE").contains("h1"));
        junit.framework.Assert.assertEquals(2, componentHostMap.get("DATANODE").size());
        assertTrue(componentHostMap.get("DATANODE").contains("h1"));
        assertTrue(componentHostMap.get("DATANODE").contains("h2"));
        componentHostMap = c1.getServiceComponentHostMap(null, Collections.singleton("MAPREDUCE"));
        junit.framework.Assert.assertEquals(1, componentHostMap.size());
        junit.framework.Assert.assertEquals(1, componentHostMap.get("JOBTRACKER").size());
        assertTrue(componentHostMap.get("JOBTRACKER").contains("h1"));
        componentHostMap = c1.getServiceComponentHostMap(null, new HashSet(Arrays.asList("HDFS", "MAPREDUCE")));
        junit.framework.Assert.assertEquals(3, componentHostMap.size());
        junit.framework.Assert.assertEquals(1, componentHostMap.get("NAMENODE").size());
        assertTrue(componentHostMap.get("NAMENODE").contains("h1"));
        junit.framework.Assert.assertEquals(2, componentHostMap.get("DATANODE").size());
        assertTrue(componentHostMap.get("DATANODE").contains("h1"));
        assertTrue(componentHostMap.get("DATANODE").contains("h2"));
        junit.framework.Assert.assertEquals(1, componentHostMap.get("JOBTRACKER").size());
        assertTrue(componentHostMap.get("JOBTRACKER").contains("h1"));
        componentHostMap = c1.getServiceComponentHostMap(null, Collections.singleton("UNKNOWN"));
        junit.framework.Assert.assertEquals(0, componentHostMap.size());
    }

    @Test
    public void testGetServiceComponentHostMap_ForHost() throws Exception {
        createDefaultCluster();
        RepositoryVersionEntity repositoryVersion = helper.getOrCreateRepositoryVersion(c1);
        Service sfHDFS = serviceFactory.createNew(c1, "HDFS", repositoryVersion);
        c1.addService(sfHDFS);
        Service sfMR = serviceFactory.createNew(c1, "MAPREDUCE", repositoryVersion);
        c1.addService(sfMR);
        ServiceComponent scNN = serviceComponentFactory.createNew(sfHDFS, "NAMENODE");
        sfHDFS.addServiceComponent(scNN);
        ServiceComponentHost schNNH1 = serviceComponentHostFactory.createNew(scNN, "h1");
        scNN.addServiceComponentHost(schNNH1);
        ServiceComponent scDN = serviceComponentFactory.createNew(sfHDFS, "DATANODE");
        sfHDFS.addServiceComponent(scDN);
        ServiceComponentHost scDNH1 = serviceComponentHostFactory.createNew(scDN, "h1");
        scDN.addServiceComponentHost(scDNH1);
        ServiceComponentHost scDNH2 = serviceComponentHostFactory.createNew(scDN, "h2");
        scDN.addServiceComponentHost(scDNH2);
        ServiceComponent scJT = serviceComponentFactory.createNew(sfMR, "JOBTRACKER");
        sfMR.addServiceComponent(scJT);
        ServiceComponentHost schJTH1 = serviceComponentHostFactory.createNew(scJT, "h1");
        scJT.addServiceComponentHost(schJTH1);
        Map<String, Set<String>> componentHostMap;
        componentHostMap = c1.getServiceComponentHostMap(Collections.singleton("h1"), null);
        junit.framework.Assert.assertEquals(3, componentHostMap.size());
        junit.framework.Assert.assertEquals(1, componentHostMap.get("NAMENODE").size());
        assertTrue(componentHostMap.get("NAMENODE").contains("h1"));
        junit.framework.Assert.assertEquals(1, componentHostMap.get("DATANODE").size());
        assertTrue(componentHostMap.get("DATANODE").contains("h1"));
        junit.framework.Assert.assertEquals(1, componentHostMap.get("JOBTRACKER").size());
        assertTrue(componentHostMap.get("JOBTRACKER").contains("h1"));
        componentHostMap = c1.getServiceComponentHostMap(Collections.singleton("h2"), null);
        junit.framework.Assert.assertEquals(1, componentHostMap.size());
        junit.framework.Assert.assertEquals(1, componentHostMap.get("DATANODE").size());
        assertTrue(componentHostMap.get("DATANODE").contains("h2"));
        componentHostMap = c1.getServiceComponentHostMap(new HashSet(Arrays.asList("h1", "h2", "h3")), null);
        junit.framework.Assert.assertEquals(3, componentHostMap.size());
        junit.framework.Assert.assertEquals(1, componentHostMap.get("NAMENODE").size());
        assertTrue(componentHostMap.get("NAMENODE").contains("h1"));
        junit.framework.Assert.assertEquals(2, componentHostMap.get("DATANODE").size());
        assertTrue(componentHostMap.get("DATANODE").contains("h1"));
        assertTrue(componentHostMap.get("DATANODE").contains("h2"));
        junit.framework.Assert.assertEquals(1, componentHostMap.get("JOBTRACKER").size());
        assertTrue(componentHostMap.get("JOBTRACKER").contains("h1"));
        componentHostMap = c1.getServiceComponentHostMap(Collections.singleton("unknown"), null);
        junit.framework.Assert.assertEquals(0, componentHostMap.size());
    }

    @Test
    public void testGetServiceComponentHostMap_ForHostAndService() throws Exception {
        createDefaultCluster();
        RepositoryVersionEntity repositoryVersion = helper.getOrCreateRepositoryVersion(c1);
        Service sfHDFS = serviceFactory.createNew(c1, "HDFS", repositoryVersion);
        c1.addService(sfHDFS);
        Service sfMR = serviceFactory.createNew(c1, "MAPREDUCE", repositoryVersion);
        c1.addService(sfMR);
        ServiceComponent scNN = serviceComponentFactory.createNew(sfHDFS, "NAMENODE");
        sfHDFS.addServiceComponent(scNN);
        ServiceComponentHost schNNH1 = serviceComponentHostFactory.createNew(scNN, "h1");
        scNN.addServiceComponentHost(schNNH1);
        ServiceComponent scDN = serviceComponentFactory.createNew(sfHDFS, "DATANODE");
        sfHDFS.addServiceComponent(scDN);
        ServiceComponentHost scDNH1 = serviceComponentHostFactory.createNew(scDN, "h1");
        scDN.addServiceComponentHost(scDNH1);
        ServiceComponentHost scDNH2 = serviceComponentHostFactory.createNew(scDN, "h2");
        scDN.addServiceComponentHost(scDNH2);
        ServiceComponent scJT = serviceComponentFactory.createNew(sfMR, "JOBTRACKER");
        sfMR.addServiceComponent(scJT);
        ServiceComponentHost schJTH1 = serviceComponentHostFactory.createNew(scJT, "h1");
        scJT.addServiceComponentHost(schJTH1);
        Map<String, Set<String>> componentHostMap;
        componentHostMap = c1.getServiceComponentHostMap(Collections.singleton("h1"), Collections.singleton("HDFS"));
        junit.framework.Assert.assertEquals(2, componentHostMap.size());
        junit.framework.Assert.assertEquals(1, componentHostMap.get("DATANODE").size());
        assertTrue(componentHostMap.get("DATANODE").contains("h1"));
        junit.framework.Assert.assertEquals(1, componentHostMap.get("NAMENODE").size());
        assertTrue(componentHostMap.get("NAMENODE").contains("h1"));
        componentHostMap = c1.getServiceComponentHostMap(Collections.singleton("h2"), Collections.singleton("HDFS"));
        junit.framework.Assert.assertEquals(1, componentHostMap.size());
        junit.framework.Assert.assertEquals(1, componentHostMap.get("DATANODE").size());
        assertTrue(componentHostMap.get("DATANODE").contains("h2"));
        componentHostMap = c1.getServiceComponentHostMap(Collections.singleton("h3"), Collections.singleton("HDFS"));
        junit.framework.Assert.assertEquals(0, componentHostMap.size());
    }

    @Test
    public void testGetAndSetConfigs() throws Exception {
        createDefaultCluster();
        Map<String, Map<String, String>> c1PropAttributes = new HashMap<>();
        c1PropAttributes.put("final", new HashMap<>());
        c1PropAttributes.get("final").put("a", "true");
        Map<String, Map<String, String>> c2PropAttributes = new HashMap<>();
        c2PropAttributes.put("final", new HashMap<>());
        c2PropAttributes.get("final").put("x", "true");
        Config config1 = configFactory.createNew(c1, "global", "version1", new HashMap<String, String>() {
            {
                put("a", "b");
            }
        }, c1PropAttributes);
        Config config2 = configFactory.createNew(c1, "global", "version2", new HashMap<String, String>() {
            {
                put("x", "y");
            }
        }, c2PropAttributes);
        configFactory.createNew(c1, "core-site", "version2", new HashMap<String, String>() {
            {
                put("x", "y");
            }
        }, new HashMap());
        c1.addDesiredConfig("_test", Collections.singleton(config1));
        Config res = c1.getDesiredConfigByType("global");
        junit.framework.Assert.assertNotNull("Expected non-null config", res);
        junit.framework.Assert.assertEquals("true", res.getPropertiesAttributes().get("final").get("a"));
        res = c1.getDesiredConfigByType("core-site");
        assertNull("Expected null config", res);
        Thread.sleep(1);
        c1.addDesiredConfig("_test", Collections.singleton(config2));
        res = c1.getDesiredConfigByType("global");
        junit.framework.Assert.assertEquals("Expected version tag to be 'version2'", "version2", res.getTag());
        junit.framework.Assert.assertEquals("true", res.getPropertiesAttributes().get("final").get("x"));
    }

    @Test
    public void testDesiredConfigs() throws Exception {
        createDefaultCluster();
        Config config1 = configFactory.createNew(c1, "global", "version1", new HashMap<String, String>() {
            {
                put("a", "b");
            }
        }, new HashMap());
        Config config2 = configFactory.createNew(c1, "global", "version2", new HashMap<String, String>() {
            {
                put("x", "y");
            }
        }, new HashMap());
        Config config3 = configFactory.createNew(c1, "core-site", "version2", new HashMap<String, String>() {
            {
                put("x", "y");
            }
        }, new HashMap());
        try {
            c1.addDesiredConfig(null, Collections.singleton(config1));
            Assert.fail("Cannot set a null user with config");
        } catch (Exception e) {
            // test failure
        }
        c1.addDesiredConfig("_test1", Collections.singleton(config1));
        Thread.sleep(1);
        c1.addDesiredConfig("_test3", Collections.singleton(config3));
        Map<String, DesiredConfig> desiredConfigs = c1.getDesiredConfigs();
        junit.framework.Assert.assertFalse("Expect desired config not contain 'mapred-site'", desiredConfigs.containsKey("mapred-site"));
        junit.framework.Assert.assertTrue(("Expect desired config contain " + (config1.getType())), desiredConfigs.containsKey("global"));
        junit.framework.Assert.assertTrue(("Expect desired config contain " + (config3.getType())), desiredConfigs.containsKey("core-site"));
        junit.framework.Assert.assertEquals(("Expect desired config for global should be " + (config1.getTag())), config1.getTag(), desiredConfigs.get(config1.getType()).getTag());
        DesiredConfig dc = desiredConfigs.get(config1.getType());
        junit.framework.Assert.assertTrue("Expect no host-level overrides", ((null == (dc.getHostOverrides())) || ((dc.getHostOverrides().size()) == 0)));
        Thread.sleep(1);
        c1.addDesiredConfig("_test2", Collections.singleton(config2));
        Thread.sleep(1);
        c1.addDesiredConfig("_test1", Collections.singleton(config1));
        // setup a host that also has a config override
        Host host = clusters.getHost("h1");
        host.addDesiredConfig(c1.getClusterId(), true, "_test2", config2);
        desiredConfigs = c1.getDesiredConfigs();
        dc = desiredConfigs.get(config1.getType());
        junit.framework.Assert.assertNotNull("Expect host-level overrides", dc.getHostOverrides());
        junit.framework.Assert.assertEquals("Expect one host-level override", 1, dc.getHostOverrides().size());
    }

    @Test
    public void testConvertToResponse() throws Exception {
        createDefaultCluster();
        ClusterResponse r = c1.convertToResponse();
        junit.framework.Assert.assertEquals(c1.getClusterId(), r.getClusterId());
        junit.framework.Assert.assertEquals(c1.getClusterName(), r.getClusterName());
        assertEquals(2, r.getTotalHosts());
        assertEquals(0, r.getClusterHealthReport().getAlertStatusHosts());
        assertEquals(0, r.getClusterHealthReport().getHealthyStatusHosts());
        assertEquals(0, r.getClusterHealthReport().getUnhealthyStatusHosts());
        assertEquals(2, r.getClusterHealthReport().getUnknownStatusHosts());
        assertEquals(0, r.getClusterHealthReport().getStaleConfigsHosts());
        assertEquals(0, r.getClusterHealthReport().getMaintenanceStateHosts());
        assertEquals(0, r.getClusterHealthReport().getHealthyStateHosts());
        assertEquals(0, r.getClusterHealthReport().getHeartbeatLostStateHosts());
        assertEquals(2, r.getClusterHealthReport().getInitStateHosts());
        assertEquals(0, r.getClusterHealthReport().getUnhealthyStateHosts());
        clusters.addHost("h3");
        Host host = clusters.getHost("h3");
        host.setIPv4("ipv4");
        host.setIPv6("ipv6");
        Map<String, String> hostAttributes = new HashMap<>();
        hostAttributes.put("os_family", "redhat");
        hostAttributes.put("os_release_version", "5.9");
        host.setHostAttributes(hostAttributes);
        host.setState(HEALTHY);
        host.setHealthStatus(new org.apache.ambari.server.state.HostHealthStatus(HealthStatus.HEALTHY, ""));
        host.setStatus(host.getHealthStatus().getHealthStatus().name());
        c1.setDesiredStackVersion(new StackId("HDP-2.0.6"));
        clusters.mapHostToCluster("h3", "c1");
        r = c1.convertToResponse();
        assertEquals(3, r.getTotalHosts());
        assertEquals(0, r.getClusterHealthReport().getAlertStatusHosts());
        assertEquals(1, r.getClusterHealthReport().getHealthyStatusHosts());
        assertEquals(0, r.getClusterHealthReport().getUnhealthyStatusHosts());
        assertEquals(2, r.getClusterHealthReport().getUnknownStatusHosts());
        assertEquals(0, r.getClusterHealthReport().getStaleConfigsHosts());
        assertEquals(0, r.getClusterHealthReport().getMaintenanceStateHosts());
        assertEquals(1, r.getClusterHealthReport().getHealthyStateHosts());
        assertEquals(0, r.getClusterHealthReport().getHeartbeatLostStateHosts());
        assertEquals(2, r.getClusterHealthReport().getInitStateHosts());
        assertEquals(0, r.getClusterHealthReport().getUnhealthyStateHosts());
        // TODO write unit tests for debug dump
        StringBuilder sb = new StringBuilder();
        c1.debugDump(sb);
    }

    @Test
    public void testDeleteService() throws Exception {
        createDefaultCluster();
        RepositoryVersionEntity repositoryVersion = helper.getOrCreateRepositoryVersion(c1);
        c1.addService("MAPREDUCE", repositoryVersion);
        Service hdfs = c1.addService("HDFS", repositoryVersion);
        hdfs.addServiceComponent("NAMENODE");
        Assert.assertEquals(2, c1.getServices().size());
        Assert.assertEquals(2, injector.getProvider(EntityManager.class).get().createQuery("SELECT service FROM ClusterServiceEntity service").getResultList().size());
        c1.deleteService("HDFS", new DeleteHostComponentStatusMetaData());
        Assert.assertEquals(1, c1.getServices().size());
        Assert.assertEquals(1, injector.getProvider(EntityManager.class).get().createQuery("SELECT service FROM ClusterServiceEntity service").getResultList().size());
    }

    @Test
    public void testDeleteServiceWithConfigHistory() throws Exception {
        createDefaultCluster();
        RepositoryVersionEntity repositoryVersion = helper.getOrCreateRepositoryVersion(c1);
        c1.addService("HDFS", repositoryVersion);
        Config config1 = configFactory.createNew(c1, "hdfs-site", "version1", new HashMap<String, String>() {
            {
                put("a", "b");
            }
        }, new HashMap());
        Config config2 = configFactory.createNew(c1, "core-site", "version2", new HashMap<String, String>() {
            {
                put("x", "y");
            }
        }, new HashMap());
        Set<Config> configs = new HashSet<>();
        configs.add(config1);
        configs.add(config2);
        c1.addDesiredConfig("admin", configs);
        List<ServiceConfigVersionResponse> serviceConfigVersions = c1.getServiceConfigVersions();
        assertNotNull(serviceConfigVersions);
        // Single serviceConfigVersion for multiple configs
        assertEquals(1, serviceConfigVersions.size());
        assertEquals(Long.valueOf(1), serviceConfigVersions.get(0).getVersion());
        assertEquals(2, c1.getDesiredConfigs().size());
        junit.framework.Assert.assertEquals("version1", c1.getDesiredConfigByType("hdfs-site").getTag());
        junit.framework.Assert.assertEquals("version2", c1.getDesiredConfigByType("core-site").getTag());
        Map<String, Collection<ServiceConfigVersionResponse>> activeServiceConfigVersions = c1.getActiveServiceConfigVersions();
        assertEquals(1, activeServiceConfigVersions.size());
        c1.deleteService("HDFS", new DeleteHostComponentStatusMetaData());
        assertEquals(0, c1.getServices().size());
        assertEquals(0, c1.getServiceConfigVersions().size());
        EntityManager em = injector.getProvider(EntityManager.class).get();
        // ServiceConfig
        assertEquals(0, em.createQuery("SELECT serviceConfig from ServiceConfigEntity serviceConfig").getResultList().size());
        // ClusterConfig
        List<ClusterConfigEntity> clusterConfigs = em.createQuery("SELECT config from ClusterConfigEntity config", ClusterConfigEntity.class).getResultList();
        assertEquals(2, clusterConfigs.size());
        for (ClusterConfigEntity configEntity : clusterConfigs) {
            if (StringUtils.equals(configEntity.getType(), "core-site")) {
                Assert.assertEquals("core-site is not part of HDFS in test stack, should remain mapped to cluster", true, configEntity.isSelected());
            }
            if (StringUtils.equals(configEntity.getType(), "hdfs-site")) {
                Assert.assertEquals("hdfs-site should be unmapped from cluster when HDFS service is removed", false, configEntity.isSelected());
            }
        }
        // ServiceConfigMapping
        assertEquals(0, em.createNativeQuery("SELECT * from serviceconfigmapping").getResultList().size());
    }

    @Test
    public void testGetHostsDesiredConfigs() throws Exception {
        createDefaultCluster();
        Host host1 = clusters.getHost("h1");
        HostEntity hostEntity1 = hostDAO.findByName("h1");
        Map<String, Map<String, String>> propAttributes = new HashMap<>();
        propAttributes.put("final", new HashMap<>());
        propAttributes.get("final").put("test", "true");
        Config config = configFactory.createNew(c1, "hdfs-site", "1", new HashMap<String, String>() {
            {
                put("test", "test");
            }
        }, propAttributes);
        host1.addDesiredConfig(c1.getClusterId(), true, "test", config);
        Map<Long, Map<String, DesiredConfig>> configs = c1.getAllHostsDesiredConfigs();
        Assert.assertTrue(configs.containsKey(hostEntity1.getHostId()));
        Assert.assertEquals(1, configs.get(hostEntity1.getHostId()).size());
        List<Long> hostIds = new ArrayList<>();
        hostIds.add(hostEntity1.getHostId());
        configs = c1.getHostsDesiredConfigs(hostIds);
        Assert.assertTrue(configs.containsKey(hostEntity1.getHostId()));
        Assert.assertEquals(1, configs.get(hostEntity1.getHostId()).size());
    }

    @Test
    public void testProvisioningState() throws Exception {
        createDefaultCluster();
        c1.setProvisioningState(State.INIT);
        junit.framework.Assert.assertEquals(State.INIT, c1.getProvisioningState());
        c1.setProvisioningState(State.INSTALLED);
        junit.framework.Assert.assertEquals(State.INSTALLED, c1.getProvisioningState());
    }

    @Test
    public void testServiceConfigVersions() throws Exception {
        createDefaultCluster();
        c1.addService("HDFS", helper.getOrCreateRepositoryVersion(new StackId("HDP", "0.1"), "0.1"));
        Config config1 = configFactory.createNew(c1, "hdfs-site", "version1", new HashMap<String, String>() {
            {
                put("a", "b");
            }
        }, new HashMap());
        Config config2 = configFactory.createNew(c1, "hdfs-site", "version2", new HashMap<String, String>() {
            {
                put("x", "y");
            }
        }, new HashMap());
        c1.addDesiredConfig("admin", Collections.singleton(config1));
        List<ServiceConfigVersionResponse> serviceConfigVersions = c1.getServiceConfigVersions();
        assertNotNull(serviceConfigVersions);
        assertEquals(1, serviceConfigVersions.size());
        Map<String, Collection<ServiceConfigVersionResponse>> activeServiceConfigVersions = c1.getActiveServiceConfigVersions();
        assertEquals(1, activeServiceConfigVersions.size());
        ServiceConfigVersionResponse hdfsResponse = activeServiceConfigVersions.get("HDFS").iterator().next();
        junit.framework.Assert.assertEquals("HDFS", hdfsResponse.getServiceName());
        junit.framework.Assert.assertEquals("c1", hdfsResponse.getClusterName());
        junit.framework.Assert.assertEquals("admin", hdfsResponse.getUserName());
        junit.framework.Assert.assertEquals("Default", hdfsResponse.getGroupName());
        assertEquals(Long.valueOf((-1)), hdfsResponse.getGroupId());
        assertEquals(Long.valueOf(1), hdfsResponse.getVersion());
        c1.addDesiredConfig("admin", Collections.singleton(config2));
        serviceConfigVersions = c1.getServiceConfigVersions();
        assertNotNull(serviceConfigVersions);
        // created new ServiceConfigVersion
        assertEquals(2, serviceConfigVersions.size());
        activeServiceConfigVersions = c1.getActiveServiceConfigVersions();
        assertEquals(1, activeServiceConfigVersions.size());
        hdfsResponse = activeServiceConfigVersions.get("HDFS").iterator().next();
        junit.framework.Assert.assertEquals("HDFS", hdfsResponse.getServiceName());
        junit.framework.Assert.assertEquals("c1", hdfsResponse.getClusterName());
        junit.framework.Assert.assertEquals("admin", hdfsResponse.getUserName());
        Assert.assertEquals(Long.valueOf(2), hdfsResponse.getVersion());
        // Rollback , clonning version1 config, created new ServiceConfigVersion
        c1.setServiceConfigVersion("HDFS", 1L, "admin", "test_note");
        serviceConfigVersions = c1.getServiceConfigVersions();
        assertNotNull(serviceConfigVersions);
        // created new ServiceConfigVersion
        assertEquals(3, serviceConfigVersions.size());
        // active version still 1
        activeServiceConfigVersions = c1.getActiveServiceConfigVersions();
        assertEquals(1, activeServiceConfigVersions.size());
        hdfsResponse = activeServiceConfigVersions.get("HDFS").iterator().next();
        junit.framework.Assert.assertEquals("HDFS", hdfsResponse.getServiceName());
        junit.framework.Assert.assertEquals("c1", hdfsResponse.getClusterName());
        junit.framework.Assert.assertEquals("admin", hdfsResponse.getUserName());
        Assert.assertEquals(Long.valueOf(3), hdfsResponse.getVersion());
    }

    @Test
    public void testSingleServiceVersionForMultipleConfigs() throws Exception {
        createDefaultCluster();
        c1.addService("HDFS", helper.getOrCreateRepositoryVersion(new StackId("HDP", "0.1"), "0.1"));
        Config config1 = configFactory.createNew(c1, "hdfs-site", "version1", new HashMap<String, String>() {
            {
                put("a", "b");
            }
        }, new HashMap());
        Config config2 = configFactory.createNew(c1, "core-site", "version2", new HashMap<String, String>() {
            {
                put("x", "y");
            }
        }, new HashMap());
        Set<Config> configs = new HashSet<>();
        configs.add(config1);
        configs.add(config2);
        c1.addDesiredConfig("admin", configs);
        List<ServiceConfigVersionResponse> serviceConfigVersions = c1.getServiceConfigVersions();
        assertNotNull(serviceConfigVersions);
        // Single serviceConfigVersion for multiple configs
        assertEquals(1, serviceConfigVersions.size());
        assertEquals(Long.valueOf(1), serviceConfigVersions.get(0).getVersion());
        assertEquals(2, c1.getDesiredConfigs().size());
        junit.framework.Assert.assertEquals("version1", c1.getDesiredConfigByType("hdfs-site").getTag());
        junit.framework.Assert.assertEquals("version2", c1.getDesiredConfigByType("core-site").getTag());
        Map<String, Collection<ServiceConfigVersionResponse>> activeServiceConfigVersions = c1.getActiveServiceConfigVersions();
        assertEquals(1, activeServiceConfigVersions.size());
    }

    @Test
    public void testServiceConfigVersionsForGroups() throws Exception {
        createDefaultCluster();
        RepositoryVersionEntity repositoryVersion = helper.getOrCreateRepositoryVersion(c1);
        c1.addService("HDFS", repositoryVersion);
        Config config1 = configFactory.createNew(c1, "hdfs-site", "version1", new HashMap<String, String>() {
            {
                put("a", "b");
            }
        }, new HashMap());
        ServiceConfigVersionResponse scvResponse = c1.addDesiredConfig("admin", Collections.singleton(config1));
        Assert.assertEquals("SCV 1 should be created", Long.valueOf(1), scvResponse.getVersion());
        Map<String, Collection<ServiceConfigVersionResponse>> activeServiceConfigVersions = c1.getActiveServiceConfigVersions();
        junit.framework.Assert.assertEquals("Only one scv should be active", 1, activeServiceConfigVersions.get("HDFS").size());
        // create config group
        Config config2 = configFactory.createNew(c1, "hdfs-site", "version2", new HashMap<String, String>() {
            {
                put("a", "c");
            }
        }, new HashMap());
        ConfigGroup configGroup = configGroupFactory.createNew(c1, "HDFS", "test group", "HDFS", "descr", Collections.singletonMap("hdfs-site", config2), Collections.emptyMap());
        c1.addConfigGroup(configGroup);
        scvResponse = c1.createServiceConfigVersion("HDFS", "admin", "test note", configGroup);
        Assert.assertEquals("SCV 2 should be created", Long.valueOf(2), scvResponse.getVersion());
        // two scv active
        activeServiceConfigVersions = c1.getActiveServiceConfigVersions();
        junit.framework.Assert.assertEquals("Two service config versions should be active, for default and test groups", 2, activeServiceConfigVersions.get("HDFS").size());
        Config config3 = configFactory.createNew(c1, "hdfs-site", "version3", new HashMap<String, String>() {
            {
                put("a", "d");
            }
        }, new HashMap());
        configGroup.setConfigurations(Collections.singletonMap("hdfs-site", config3));
        scvResponse = c1.createServiceConfigVersion("HDFS", "admin", "test note", configGroup);
        Assert.assertEquals("SCV 3 should be created", Long.valueOf(3), scvResponse.getVersion());
        // still two scv active, 3 total
        activeServiceConfigVersions = c1.getActiveServiceConfigVersions();
        junit.framework.Assert.assertEquals("Two service config versions should be active, for default and test groups", 2, activeServiceConfigVersions.get("HDFS").size());
        Assert.assertEquals(3, c1.getServiceConfigVersions().size());
        // rollback group
        scvResponse = c1.setServiceConfigVersion("HDFS", 2L, "admin", "group rollback");
        Assert.assertEquals("SCV 4 should be created", Long.valueOf(4), scvResponse.getVersion());
        configGroup = c1.getConfigGroups().get(configGroup.getId());// refresh?

        // still two scv active, 4 total
        activeServiceConfigVersions = c1.getActiveServiceConfigVersions();
        junit.framework.Assert.assertEquals("Two service config versions should be active, for default and test groups", 2, activeServiceConfigVersions.get("HDFS").size());
        Assert.assertEquals(4, c1.getServiceConfigVersions().size());
        // check properties rolled back
        Map<String, String> configProperties = configGroup.getConfigurations().get("hdfs-site").getProperties();
        Assert.assertEquals("Configurations should be rolled back to a:c ", "c", configProperties.get("a"));
        // check config with empty cluster
        Config config4 = configFactory.createReadOnly("hdfs-site", "version4", Collections.singletonMap("a", "b"), null);
        ConfigGroup configGroup2 = configGroupFactory.createNew(c1, "HDFS", "test group 2", "HDFS", "descr", new HashMap(Collections.singletonMap("hdfs-site", config4)), Collections.emptyMap());
        c1.addConfigGroup(configGroup2);
        scvResponse = c1.createServiceConfigVersion("HDFS", "admin", "test note", configGroup2);
        Assert.assertEquals("SCV 5 should be created", Long.valueOf(5), scvResponse.getVersion());
        activeServiceConfigVersions = c1.getActiveServiceConfigVersions();
        junit.framework.Assert.assertEquals("Three service config versions should be active, for default and test groups", 3, activeServiceConfigVersions.get("HDFS").size());
        Assert.assertEquals("Five total scvs", 5, c1.getServiceConfigVersions().size());
    }

    @Test
    public void testAllServiceConfigVersionsWithConfigGroups() throws Exception {
        // Given
        createDefaultCluster();
        c1.addService("HDFS", helper.getOrCreateRepositoryVersion(new StackId("HDP", "0.1"), "0.1"));
        Config hdfsSiteConfigV1 = configFactory.createNew(c1, "hdfs-site", "version1", ImmutableMap.of("p1", "v1"), ImmutableMap.of());
        ServiceConfigVersionResponse hdfsSiteConfigResponseV1 = c1.addDesiredConfig("admin", Collections.singleton(hdfsSiteConfigV1));
        List<ConfigurationResponse> configResponsesDefaultGroup = Collections.singletonList(new ConfigurationResponse(c1.getClusterName(), hdfsSiteConfigV1.getStackId(), hdfsSiteConfigV1.getType(), hdfsSiteConfigV1.getTag(), hdfsSiteConfigV1.getVersion(), hdfsSiteConfigV1.getProperties(), hdfsSiteConfigV1.getPropertiesAttributes(), hdfsSiteConfigV1.getPropertiesTypes()));
        hdfsSiteConfigResponseV1.setConfigurations(configResponsesDefaultGroup);
        Config hdfsSiteConfigV2 = configFactory.createNew(c1, "hdfs-site", "version2", ImmutableMap.of("p1", "v2"), ImmutableMap.of());
        ConfigGroup configGroup = configGroupFactory.createNew(c1, "HDFS", "configGroup1", "version1", "test description", ImmutableMap.of(hdfsSiteConfigV2.getType(), hdfsSiteConfigV2), ImmutableMap.of());
        c1.addConfigGroup(configGroup);
        ServiceConfigVersionResponse hdfsSiteConfigResponseV2 = c1.createServiceConfigVersion("HDFS", "admin", "test note", configGroup);
        hdfsSiteConfigResponseV2.setConfigurations(Collections.singletonList(new ConfigurationResponse(c1.getClusterName(), hdfsSiteConfigV2.getStackId(), hdfsSiteConfigV2.getType(), hdfsSiteConfigV2.getTag(), hdfsSiteConfigV2.getVersion(), hdfsSiteConfigV2.getProperties(), hdfsSiteConfigV2.getPropertiesAttributes(), hdfsSiteConfigV2.getPropertiesTypes())));
        hdfsSiteConfigResponseV2.setIsCurrent(true);// this is the active config in 'configGroup1' config group as it's the solely service config

        // hdfs config v3
        ServiceConfigVersionResponse hdfsSiteConfigResponseV3 = c1.createServiceConfigVersion("HDFS", "admin", "new config in default group", null);
        hdfsSiteConfigResponseV3.setConfigurations(configResponsesDefaultGroup);
        hdfsSiteConfigResponseV3.setIsCurrent(true);// this is the active config in default config group as it's more recent than V1

        // When
        List<ServiceConfigVersionResponse> expectedServiceConfigResponses = ImmutableList.of(hdfsSiteConfigResponseV1, hdfsSiteConfigResponseV2, hdfsSiteConfigResponseV3);
        List<ServiceConfigVersionResponse> allServiceConfigResponses = c1.getServiceConfigVersions();
        Collections.sort(allServiceConfigResponses, new Comparator<ServiceConfigVersionResponse>() {
            @Override
            public int compare(ServiceConfigVersionResponse o1, ServiceConfigVersionResponse o2) {
                return o1.getVersion().compareTo(o2.getVersion());
            }
        });
        // Then
        Assert.assertThat(allServiceConfigResponses, CoreMatchers.is(expectedServiceConfigResponses));
    }

    @Test
    public void testAllServiceConfigVersionsWithDeletedConfigGroups() throws Exception {
        // Given
        createDefaultCluster();
        c1.addService("HDFS", helper.getOrCreateRepositoryVersion(new StackId("HDP", "0.1"), "0.1"));
        Config hdfsSiteConfigV1 = configFactory.createNew(c1, "hdfs-site", "version1", ImmutableMap.of("p1", "v1"), ImmutableMap.of());
        ServiceConfigVersionResponse hdfsSiteConfigResponseV1 = c1.addDesiredConfig("admin", Collections.singleton(hdfsSiteConfigV1));
        List<ConfigurationResponse> configResponsesDefaultGroup = Collections.singletonList(new ConfigurationResponse(c1.getClusterName(), hdfsSiteConfigV1.getStackId(), hdfsSiteConfigV1.getType(), hdfsSiteConfigV1.getTag(), hdfsSiteConfigV1.getVersion(), hdfsSiteConfigV1.getProperties(), hdfsSiteConfigV1.getPropertiesAttributes(), hdfsSiteConfigV1.getPropertiesTypes()));
        hdfsSiteConfigResponseV1.setConfigurations(configResponsesDefaultGroup);
        Config hdfsSiteConfigV2 = configFactory.createNew(c1, "hdfs-site", "version2", ImmutableMap.of("p1", "v2"), ImmutableMap.of());
        ConfigGroup configGroup = configGroupFactory.createNew(c1, "HDFS", "configGroup1", "version1", "test description", ImmutableMap.of(hdfsSiteConfigV2.getType(), hdfsSiteConfigV2), ImmutableMap.of());
        c1.addConfigGroup(configGroup);
        ServiceConfigVersionResponse hdfsSiteConfigResponseV2 = c1.createServiceConfigVersion("HDFS", "admin", "test note", configGroup);
        hdfsSiteConfigResponseV2.setConfigurations(Collections.singletonList(new ConfigurationResponse(c1.getClusterName(), hdfsSiteConfigV2.getStackId(), hdfsSiteConfigV2.getType(), hdfsSiteConfigV2.getTag(), hdfsSiteConfigV2.getVersion(), hdfsSiteConfigV2.getProperties(), hdfsSiteConfigV2.getPropertiesAttributes(), hdfsSiteConfigV2.getPropertiesTypes())));
        // delete the config group
        c1.deleteConfigGroup(configGroup.getId());
        // hdfs config v3
        ServiceConfigVersionResponse hdfsSiteConfigResponseV3 = c1.createServiceConfigVersion("HDFS", "admin", "new config in default group", null);
        hdfsSiteConfigResponseV3.setConfigurations(configResponsesDefaultGroup);
        hdfsSiteConfigResponseV3.setIsCurrent(true);// this is the active config in default config group as it's more recent than V1

        // When
        List<ServiceConfigVersionResponse> allServiceConfigResponses = c1.getServiceConfigVersions();
        Collections.sort(allServiceConfigResponses, new Comparator<ServiceConfigVersionResponse>() {
            @Override
            public int compare(ServiceConfigVersionResponse o1, ServiceConfigVersionResponse o2) {
                return o1.getVersion().compareTo(o2.getVersion());
            }
        });
        // Then
        Assert.assertEquals(3, allServiceConfigResponses.size());
        // all configs that was created as member of config group 'configGroup1' should be marked as 'not current'
        // as the parent config group has been deleted
        // default group
        Assert.assertEquals(false, allServiceConfigResponses.get(0).getIsCurrent());
        Assert.assertEquals(DEFAULT_CONFIG_GROUP_NAME, allServiceConfigResponses.get(0).getGroupName());
        Assert.assertEquals(true, allServiceConfigResponses.get(2).getIsCurrent());
        Assert.assertEquals(DEFAULT_CONFIG_GROUP_NAME, allServiceConfigResponses.get(2).getGroupName());
        // deleted group
        Assert.assertEquals(false, allServiceConfigResponses.get(1).getIsCurrent());
        Assert.assertEquals(DELETED_CONFIG_GROUP_NAME, allServiceConfigResponses.get(1).getGroupName());
    }

    /**
     * Tests that hosts can be correctly transitioned into the "INSTALLING" state.
     * This method also tests that hosts in MM will not be transitioned, as per
     * the contract of
     * {@link Cluster#transitionHostsToInstalling(RepositoryVersionEntity, org.apache.ambari.server.state.repository.VersionDefinitionXml, boolean)}.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testTransitionHostsToInstalling() throws Exception {
        // this will create a cluster with a few hosts and no host components
        StackId originalStackId = new StackId("HDP", "2.0.5");
        createDefaultCluster(Sets.newHashSet("h1", "h2"), originalStackId);
        List<HostVersionEntity> hostVersionsH1Before = hostVersionDAO.findByClusterAndHost("c1", "h1");
        Assert.assertEquals(1, hostVersionsH1Before.size());
        StackId stackId = new StackId("HDP", "2.0.6");
        RepositoryVersionEntity repositoryVersion = helper.getOrCreateRepositoryVersion(stackId, stackId.getStackVersion());
        // this should move both to NOT_REQUIRED since they have no versionable
        // components
        c1.transitionHostsToInstalling(repositoryVersion, null, false);
        List<HostVersionEntity> hostVersionsH1After = hostVersionDAO.findByClusterAndHost("c1", "h1");
        Assert.assertEquals(2, hostVersionsH1After.size());
        boolean checked = false;
        for (HostVersionEntity entity : hostVersionsH1After) {
            StackEntity repoVersionStackEntity = entity.getRepositoryVersion().getStack();
            if ((repoVersionStackEntity.getStackName().equals("HDP")) && (repoVersionStackEntity.getStackVersion().equals("2.0.6"))) {
                Assert.assertEquals(NOT_REQUIRED, entity.getState());
                checked = true;
                break;
            }
        }
        Assert.assertTrue(checked);
        // add some host components
        Service hdfs = serviceFactory.createNew(c1, "HDFS", repositoryVersion);
        c1.addService(hdfs);
        // Add HDFS components
        ServiceComponent datanode = serviceComponentFactory.createNew(hdfs, "NAMENODE");
        ServiceComponent namenode = serviceComponentFactory.createNew(hdfs, "DATANODE");
        hdfs.addServiceComponent(datanode);
        hdfs.addServiceComponent(namenode);
        // add to hosts
        ServiceComponentHost namenodeHost1 = serviceComponentHostFactory.createNew(namenode, "h1");
        ServiceComponentHost datanodeHost2 = serviceComponentHostFactory.createNew(datanode, "h2");
        Assert.assertNotNull(namenodeHost1);
        Assert.assertNotNull(datanodeHost2);
        // with hosts now having components which report versions, we should have
        // two in the INSTALLING state
        c1.transitionHostsToInstalling(repositoryVersion, null, false);
        hostVersionsH1After = hostVersionDAO.findByClusterAndHost("c1", "h1");
        Assert.assertEquals(2, hostVersionsH1After.size());
        checked = false;
        for (HostVersionEntity entity : hostVersionsH1After) {
            StackEntity repoVersionStackEntity = entity.getRepositoryVersion().getStack();
            if ((repoVersionStackEntity.getStackName().equals("HDP")) && (repoVersionStackEntity.getStackVersion().equals("2.0.6"))) {
                Assert.assertEquals(INSTALLING, entity.getState());
                checked = true;
                break;
            }
        }
        Assert.assertTrue(checked);
        // reset all to INSTALL_FAILED
        List<HostVersionEntity> hostVersionEntities = hostVersionDAO.findAll();
        for (HostVersionEntity hostVersionEntity : hostVersionEntities) {
            hostVersionEntity.setState(INSTALL_FAILED);
            hostVersionDAO.merge(hostVersionEntity);
        }
        // verify they have been transition to INSTALL_FAILED
        hostVersionEntities = hostVersionDAO.findAll();
        for (HostVersionEntity hostVersionEntity : hostVersionEntities) {
            Assert.assertEquals(INSTALL_FAILED, hostVersionEntity.getState());
        }
        // put 1 host in maintenance mode
        Collection<Host> hosts = c1.getHosts();
        Iterator<Host> iterator = hosts.iterator();
        Host hostInMaintenanceMode = iterator.next();
        Host hostNotInMaintenanceMode = iterator.next();
        hostInMaintenanceMode.setMaintenanceState(c1.getClusterId(), ON);
        // transition host versions to INSTALLING
        c1.transitionHostsToInstalling(repositoryVersion, null, false);
        List<HostVersionEntity> hostInMaintModeVersions = hostVersionDAO.findByClusterAndHost("c1", hostInMaintenanceMode.getHostName());
        List<HostVersionEntity> otherHostVersions = hostVersionDAO.findByClusterAndHost("c1", hostNotInMaintenanceMode.getHostName());
        // verify the MM host has moved to OUT_OF_SYNC
        for (HostVersionEntity hostVersionEntity : hostInMaintModeVersions) {
            StackEntity repoVersionStackEntity = hostVersionEntity.getRepositoryVersion().getStack();
            if ((repoVersionStackEntity.getStackName().equals("HDP")) && (repoVersionStackEntity.getStackVersion().equals("2.0.6"))) {
                Assert.assertEquals(OUT_OF_SYNC, hostVersionEntity.getState());
            }
        }
        // verify the other host is in INSTALLING
        for (HostVersionEntity hostVersionEntity : otherHostVersions) {
            StackEntity repoVersionStackEntity = hostVersionEntity.getRepositoryVersion().getStack();
            if ((repoVersionStackEntity.getStackName().equals("HDP")) && (repoVersionStackEntity.getStackVersion().equals("2.0.6"))) {
                Assert.assertEquals(INSTALLING, hostVersionEntity.getState());
            }
        }
    }

    /**
     * Comprehensive test for host versions. It creates a cluster with 3 hosts and
     * 3 services, one of which does not advertise a version. It then verifies
     * that all 3 hosts have a version of CURRENT, and so does the cluster. It
     * then adds one more host with a component, so its HostVersion will
     * initialize in CURRENT. Next, it distributes a repo so that it is INSTALLED
     * on the 4 hosts. It then adds one more host, whose HostVersion will be
     * OUT_OF_SYNC for the new repo. After redistributing bits again, it simulates
     * an RU. Finally, some of the hosts will end up with a HostVersion in
     * UPGRADED, and others still in INSTALLED.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testTransitionHostVersionAdvanced() throws Exception {
        String clusterName = "c1";
        String v1 = "2.2.0-123";
        StackId stackId = new StackId("HDP-2.2.0");
        RepositoryVersionEntity rv1 = helper.getOrCreateRepositoryVersion(stackId, v1);
        Map<String, String> hostAttributes = new HashMap<>();
        hostAttributes.put("os_family", "redhat");
        hostAttributes.put("os_release_version", "6.4");
        Cluster cluster = createClusterForRU(clusterName, rv1, hostAttributes);
        // Begin install by starting to advertise versions
        // Set the version for the HostComponentState objects
        int versionedComponentCount = 0;
        List<HostComponentStateEntity> hostComponentStates = hostComponentStateDAO.findAll();
        for (int i = 0; i < (hostComponentStates.size()); i++) {
            HostComponentStateEntity hce = hostComponentStates.get(i);
            ComponentInfo compInfo = metaInfo.getComponent(stackId.getStackName(), stackId.getStackVersion(), hce.getServiceName(), hce.getComponentName());
            if (compInfo.isVersionAdvertised()) {
                hce.setVersion(v1);
                hostComponentStateDAO.merge(hce);
                versionedComponentCount++;
            }
            // Simulate the StackVersionListener during the installation of the first Stack Version
            Service svc = cluster.getService(hce.getServiceName());
            ServiceComponent svcComp = svc.getServiceComponent(hce.getComponentName());
            ServiceComponentHost scHost = svcComp.getServiceComponentHost(hce.getHostName());
            scHost.recalculateHostVersionState();
            if (versionedComponentCount > 0) {
                // On the first component with a version, a RepoVersion should have been created
                RepositoryVersionEntity repositoryVersion = repositoryVersionDAO.findByStackAndVersion(stackId, v1);
                assertNotNull(repositoryVersion);
            }
        }
        // Add another Host with components ZK Server, ZK Client, and Ganglia Monitor.
        // This host should get a HostVersion in CURRENT, and the ClusterVersion should stay in CURRENT
        addHost("h-4", hostAttributes);
        clusters.mapHostToCluster("h-4", clusterName);
        Service svc2 = cluster.getService("ZOOKEEPER");
        Service svc3 = cluster.getService("GANGLIA");
        ServiceComponent sc2CompA = svc2.getServiceComponent("ZOOKEEPER_SERVER");
        ServiceComponent sc2CompB = svc2.getServiceComponent("ZOOKEEPER_CLIENT");
        ServiceComponent sc3CompB = svc3.getServiceComponent("GANGLIA_MONITOR");
        ServiceComponentHost schHost4Serv2CompA = serviceComponentHostFactory.createNew(sc2CompA, "h-4");
        ServiceComponentHost schHost4Serv2CompB = serviceComponentHostFactory.createNew(sc2CompB, "h-4");
        ServiceComponentHost schHost4Serv3CompB = serviceComponentHostFactory.createNew(sc3CompB, "h-4");
        sc2CompA.addServiceComponentHost(schHost4Serv2CompA);
        sc2CompB.addServiceComponentHost(schHost4Serv2CompB);
        sc3CompB.addServiceComponentHost(schHost4Serv3CompB);
        simulateStackVersionListener(stackId, v1, cluster, hostComponentStateDAO.findByHost("h-4"));
        Collection<HostVersionEntity> hostVersions = hostVersionDAO.findAll();
        // h-4 doesn't have a host version record yet
        junit.framework.Assert.assertEquals(hostVersions.size(), clusters.getHosts().size());
        HostVersionEntity h4Version1 = hostVersionDAO.findByClusterStackVersionAndHost(clusterName, stackId, v1, "h-4");
        assertNotNull(h4Version1);
        junit.framework.Assert.assertEquals(h4Version1.getState(), CURRENT);
        // Distribute bits for a new repo
        String v2 = "2.2.0-456";
        RepositoryVersionEntity rv2 = helper.getOrCreateRepositoryVersion(stackId, v2);
        for (String hostName : clusters.getHostsForCluster(clusterName).keySet()) {
            HostEntity host = hostDAO.findByName(hostName);
            HostVersionEntity hve = new HostVersionEntity(host, rv2, RepositoryVersionState.INSTALLED);
            hostVersionDAO.create(hve);
        }
        // Add one more Host, with only Ganglia on it. It should have a HostVersion in NOT_REQUIRED for v2,
        // as Ganglia isn't versionable
        Host host5 = addHost("h-5", hostAttributes);
        clusters.mapAndPublishHostsToCluster(Collections.singleton("h-5"), clusterName);
        // verify that the new host version was added for the existing repo
        HostVersionEntity h5Version1 = hostVersionDAO.findHostVersionByHostAndRepository(host5.getHostEntity(), rv1);
        HostVersionEntity h5Version2 = hostVersionDAO.findHostVersionByHostAndRepository(host5.getHostEntity(), rv2);
        junit.framework.Assert.assertEquals(NOT_REQUIRED, h5Version1.getState());
        junit.framework.Assert.assertEquals(NOT_REQUIRED, h5Version2.getState());
        ServiceComponentHost schHost5Serv3CompB = serviceComponentHostFactory.createNew(sc3CompB, "h-5");
        sc3CompB.addServiceComponentHost(schHost5Serv3CompB);
        // Host 5 will be in OUT_OF_SYNC, so redistribute bits to it so that it reaches a state of INSTALLED
        h5Version2 = hostVersionDAO.findByClusterStackVersionAndHost(clusterName, stackId, v2, "h-5");
        assertNotNull(h5Version2);
        junit.framework.Assert.assertEquals(NOT_REQUIRED, h5Version2.getState());
        h5Version2.setState(INSTALLED);
        hostVersionDAO.merge(h5Version2);
        // Perform an RU.
        // Verify that on first component with the new version, the ClusterVersion transitions to UPGRADING.
        // For hosts with only components that advertise a version, they HostVersion should be in UPGRADING.
        // For the remaining hosts, the HostVersion should stay in INSTALLED.
        versionedComponentCount = 0;
        hostComponentStates = hostComponentStateDAO.findAll();
        for (int i = 0; i < (hostComponentStates.size()); i++) {
            HostComponentStateEntity hce = hostComponentStates.get(i);
            ComponentInfo compInfo = metaInfo.getComponent(stackId.getStackName(), stackId.getStackVersion(), hce.getServiceName(), hce.getComponentName());
            if (compInfo.isVersionAdvertised()) {
                hce.setVersion(v2);
                hostComponentStateDAO.merge(hce);
                versionedComponentCount++;
            }
            // Simulate the StackVersionListener during the installation of the first Stack Version
            Service svc = cluster.getService(hce.getServiceName());
            ServiceComponent svcComp = svc.getServiceComponent(hce.getComponentName());
            ServiceComponentHost scHost = svcComp.getServiceComponentHost(hce.getHostName());
            scHost.recalculateHostVersionState();
            if (versionedComponentCount > 0) {
                // On the first component with a version, a RepoVersion should have been created
                RepositoryVersionEntity repositoryVersion = repositoryVersionDAO.findByStackAndVersion(stackId, v2);
                assertNotNull(repositoryVersion);
            }
        }
        Collection<HostVersionEntity> v2HostVersions = hostVersionDAO.findByClusterStackAndVersion(clusterName, stackId, v2);
        junit.framework.Assert.assertEquals(v2HostVersions.size(), clusters.getHostsForCluster(clusterName).size());
        for (HostVersionEntity hve : v2HostVersions) {
            assertTrue(ClusterTest.TERMINAL_VERSION_STATES.contains(hve.getState()));
        }
    }

    @Test
    public void testBootstrapHostVersion() throws Exception {
        String clusterName = "c1";
        String v1 = "2.2.0-123";
        StackId stackId = new StackId("HDP-2.2.0");
        RepositoryVersionEntity rv1 = helper.getOrCreateRepositoryVersion(stackId, v1);
        Map<String, String> hostAttributes = new HashMap<>();
        hostAttributes.put("os_family", "redhat");
        hostAttributes.put("os_release_version", "6.4");
        Cluster cluster = createClusterForRU(clusterName, rv1, hostAttributes);
        // Make one host unhealthy
        Host deadHost = cluster.getHosts().iterator().next();
        deadHost.setState(UNHEALTHY);
        // Begin bootstrap by starting to advertise versions
        // Set the version for the HostComponentState objects
        int versionedComponentCount = 0;
        List<HostComponentStateEntity> hostComponentStates = hostComponentStateDAO.findAll();
        for (int i = 0; i < (hostComponentStates.size()); i++) {
            HostComponentStateEntity hce = hostComponentStates.get(i);
            ComponentInfo compInfo = metaInfo.getComponent(stackId.getStackName(), stackId.getStackVersion(), hce.getServiceName(), hce.getComponentName());
            if (hce.getHostName().equals(deadHost.getHostName())) {
                continue;// Skip setting version

            }
            if (compInfo.isVersionAdvertised()) {
                hce.setVersion(v1);
                hostComponentStateDAO.merge(hce);
                versionedComponentCount++;
            }
            // Simulate the StackVersionListener during the installation of the first Stack Version
            Service svc = cluster.getService(hce.getServiceName());
            ServiceComponent svcComp = svc.getServiceComponent(hce.getComponentName());
            ServiceComponentHost scHost = svcComp.getServiceComponentHost(hce.getHostName());
            scHost.recalculateHostVersionState();
            if (versionedComponentCount > 0) {
                // On the first component with a version, a RepoVersion should have been created
                RepositoryVersionEntity repositoryVersion = repositoryVersionDAO.findByStackAndVersion(stackId, v1);
                assertNotNull(repositoryVersion);
            }
        }
    }

    @Test
    public void testTransitionNonReportableHost() throws Exception {
        StackId stackId = new StackId("HDP-2.0.5");
        String clusterName = "c1";
        clusters.addCluster(clusterName, stackId);
        Cluster c1 = clusters.getCluster(clusterName);
        junit.framework.Assert.assertEquals(clusterName, c1.getClusterName());
        clusters.addHost("h-1");
        clusters.addHost("h-2");
        clusters.addHost("h-3");
        for (String hostName : new String[]{ "h-1", "h-2", "h-3" }) {
            Host h = clusters.getHost(hostName);
            h.setIPv4("ipv4");
            h.setIPv6("ipv6");
            Map<String, String> hostAttributes = new HashMap<>();
            hostAttributes.put("os_family", "redhat");
            hostAttributes.put("os_release_version", "5.9");
            h.setHostAttributes(hostAttributes);
        }
        String v1 = "2.0.5-1";
        String v2 = "2.0.5-2";
        c1.setDesiredStackVersion(stackId);
        helper.getOrCreateRepositoryVersion(stackId, v1);
        helper.getOrCreateRepositoryVersion(stackId, v2);
        c1.setCurrentStackVersion(stackId);
        clusters.mapHostToCluster("h-1", clusterName);
        clusters.mapHostToCluster("h-2", clusterName);
        clusters.mapHostToCluster("h-3", clusterName);
        RepositoryVersionEntity repositoryVersion = helper.getOrCreateRepositoryVersion(c1);
        Service service = c1.addService("ZOOKEEPER", repositoryVersion);
        ServiceComponent sc = service.addServiceComponent("ZOOKEEPER_SERVER");
        sc.addServiceComponentHost("h-1");
        sc.addServiceComponentHost("h-2");
        service = c1.addService("SQOOP", repositoryVersion);
        sc = service.addServiceComponent("SQOOP");
        sc.addServiceComponentHost("h-3");
        HostEntity hostEntity = hostDAO.findByName("h-3");
        Assert.assertNotNull(hostEntity);
        List<HostVersionEntity> entities = hostVersionDAO.findByClusterAndHost(clusterName, "h-3");
        Assert.assertTrue("Expected no host versions", ((null == entities) || (0 == (entities.size()))));
        List<ServiceComponentHost> componentsOnHost3 = c1.getServiceComponentHosts("h-3");
        componentsOnHost3.iterator().next().recalculateHostVersionState();
        entities = hostVersionDAO.findByClusterAndHost(clusterName, "h-3");
        Assert.assertEquals(1, entities.size());
    }

    /**
     * Checks case when there are 2 cluster stack versions present (CURRENT and OUT_OF_SYNC),
     * and we add a new host to cluster. On a new host, both CURRENT and OUT_OF_SYNC host
     * versions should be present
     */
    /**
     * Tests that an existing configuration can be successfully updated without
     * creating a new version.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testClusterConfigMergingWithoutNewVersion() throws Exception {
        createDefaultCluster();
        Cluster cluster = clusters.getCluster("c1");
        ClusterEntity clusterEntity = clusterDAO.findByName("c1");
        Assert.assertEquals(0, clusterEntity.getClusterConfigEntities().size());
        final Config originalConfig = configFactory.createNew(cluster, "foo-site", "version3", new HashMap<String, String>() {
            {
                put("one", "two");
            }
        }, new HashMap());
        ConfigGroup configGroup = configGroupFactory.createNew(cluster, "HDFS", "g1", "t1", "", new HashMap<String, Config>() {
            {
                put("foo-site", originalConfig);
            }
        }, Collections.emptyMap());
        cluster.addConfigGroup(configGroup);
        clusterEntity = clusterDAO.findByName("c1");
        Assert.assertEquals(1, clusterEntity.getClusterConfigEntities().size());
        Map<String, Config> configsByType = cluster.getConfigsByType("foo-site");
        Config config = configsByType.entrySet().iterator().next().getValue();
        Map<String, String> properties = config.getProperties();
        properties.put("three", "four");
        config.setProperties(properties);
        config.save();
        clusterEntity = clusterDAO.findByName("c1");
        Assert.assertEquals(1, clusterEntity.getClusterConfigEntities().size());
        ClusterConfigEntity clusterConfigEntity = clusterEntity.getClusterConfigEntities().iterator().next();
        Assert.assertTrue(clusterConfigEntity.getData().contains("one"));
        Assert.assertTrue(clusterConfigEntity.getData().contains("two"));
        Assert.assertTrue(clusterConfigEntity.getData().contains("three"));
        Assert.assertTrue(clusterConfigEntity.getData().contains("four"));
        cluster.refresh();
        clusterEntity = clusterDAO.findByName("c1");
        Assert.assertEquals(1, clusterEntity.getClusterConfigEntities().size());
        clusterConfigEntity = clusterEntity.getClusterConfigEntities().iterator().next();
        Assert.assertTrue(clusterConfigEntity.getData().contains("one"));
        Assert.assertTrue(clusterConfigEntity.getData().contains("two"));
        Assert.assertTrue(clusterConfigEntity.getData().contains("three"));
        Assert.assertTrue(clusterConfigEntity.getData().contains("four"));
    }

    /**
     * Tests that {@link Cluster#applyLatestConfigurations(StackId, String)} sets the
     * right configs to enabled.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testApplyLatestConfigurations() throws Exception {
        StackId stackId = new StackId("HDP-2.0.6");
        StackId newStackId = new StackId("HDP-2.2.0");
        createDefaultCluster(Sets.newHashSet("host-1"), stackId);
        Cluster cluster = clusters.getCluster("c1");
        ClusterEntity clusterEntity = clusterDAO.findByName("c1");
        RepositoryVersionEntity repoVersion220 = helper.getOrCreateRepositoryVersion(newStackId, "2.2.0-1234");
        StackEntity currentStack = stackDAO.find(stackId.getStackName(), stackId.getStackVersion());
        StackEntity newStack = stackDAO.find(newStackId.getStackName(), newStackId.getStackVersion());
        assertFalse(stackId.equals(newStackId));
        // add a service
        String serviceName = "ZOOKEEPER";
        RepositoryVersionEntity repositoryVersion = helper.getOrCreateRepositoryVersion(c1);
        Service service = cluster.addService(serviceName, repositoryVersion);
        String configType = "zoo.cfg";
        ClusterConfigEntity clusterConfig1 = new ClusterConfigEntity();
        clusterConfig1.setClusterEntity(clusterEntity);
        clusterConfig1.setConfigId(1L);
        clusterConfig1.setStack(currentStack);
        clusterConfig1.setTag("version-1");
        clusterConfig1.setData("{}");
        clusterConfig1.setType(configType);
        clusterConfig1.setTimestamp(1L);
        clusterConfig1.setVersion(1L);
        clusterConfig1.setSelected(true);
        clusterDAO.createConfig(clusterConfig1);
        clusterEntity.getClusterConfigEntities().add(clusterConfig1);
        clusterEntity = clusterDAO.merge(clusterEntity);
        Config config = configFactory.createExisting(cluster, clusterConfig1);
        cluster.addConfig(config);
        cluster.createServiceConfigVersion(serviceName, "", "version-1", null);
        ClusterConfigEntity clusterConfig2 = new ClusterConfigEntity();
        clusterConfig2.setClusterEntity(clusterEntity);
        clusterConfig2.setConfigId(2L);
        clusterConfig2.setStack(newStack);
        clusterConfig2.setTag("version-2");
        clusterConfig2.setData("{}");
        clusterConfig2.setType(configType);
        clusterConfig2.setTimestamp(2L);
        clusterConfig2.setVersion(2L);
        clusterConfig2.setSelected(false);
        clusterDAO.createConfig(clusterConfig2);
        clusterEntity.getClusterConfigEntities().add(clusterConfig2);
        clusterEntity = clusterDAO.merge(clusterEntity);
        config = configFactory.createExisting(cluster, clusterConfig1);
        cluster.addConfig(config);
        // before creating the new service config version, we need to push the
        // service's desired repository forward
        service.setDesiredRepositoryVersion(repoVersion220);
        cluster.createServiceConfigVersion(serviceName, "", "version-2", null);
        // check that the original config is enabled
        Collection<ClusterConfigEntity> clusterConfigs = clusterEntity.getClusterConfigEntities();
        assertEquals(2, clusterConfigs.size());
        for (ClusterConfigEntity clusterConfig : clusterConfigs) {
            if (clusterConfig.getTag().equals("version-1")) {
                assertTrue(clusterConfig.isSelected());
            } else {
                assertFalse(clusterConfig.isSelected());
            }
        }
        cluster.applyLatestConfigurations(newStackId, serviceName);
        clusterEntity = clusterDAO.findByName("c1");
        // now check that the new config is enabled
        clusterConfigs = clusterEntity.getClusterConfigEntities();
        assertEquals(2, clusterConfigs.size());
        for (ClusterConfigEntity clusterConfig : clusterConfigs) {
            if (clusterConfig.getTag().equals("version-1")) {
                assertFalse(clusterConfig.isSelected());
            } else {
                assertTrue(clusterConfig.isSelected());
            }
        }
    }

    /**
     * Tests that {@link Cluster#applyLatestConfigurations(StackId, String)} sets the
     * right configs to enabled when setting them to a prior stack which has
     * several configs.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testApplyLatestConfigurationsToPreviousStack() throws Exception {
        StackId stackId = new StackId("HDP-2.0.6");
        StackId newStackId = new StackId("HDP-2.2.0");
        createDefaultCluster(Sets.newHashSet("host-1"), stackId);
        Cluster cluster = clusters.getCluster("c1");
        ClusterEntity clusterEntity = clusterDAO.findByName("c1");
        RepositoryVersionEntity repoVersion220 = helper.getOrCreateRepositoryVersion(newStackId, "2.2.0-1234");
        StackEntity currentStack = stackDAO.find(stackId.getStackName(), stackId.getStackVersion());
        StackEntity newStack = stackDAO.find(newStackId.getStackName(), newStackId.getStackVersion());
        assertFalse(stackId.equals(newStackId));
        // add a service
        String serviceName = "ZOOKEEPER";
        RepositoryVersionEntity repositoryVersion = helper.getOrCreateRepositoryVersion(c1);
        Service service = cluster.addService(serviceName, repositoryVersion);
        String configType = "zoo.cfg";
        // create 5 configurations in the current stack
        for (int i = 1; i <= 5; i++) {
            ClusterConfigEntity clusterConfig = new ClusterConfigEntity();
            clusterConfig.setClusterEntity(clusterEntity);
            clusterConfig.setConfigId(Long.valueOf(i));
            clusterConfig.setStack(currentStack);
            clusterConfig.setTag(("version-" + i));
            clusterConfig.setData("{}");
            clusterConfig.setType(configType);
            clusterConfig.setTimestamp(System.currentTimeMillis());
            clusterConfig.setVersion(Long.valueOf(i));
            // set to true, then back to false to get a value populated in the
            // selected timestamp
            clusterConfig.setSelected(true);
            clusterConfig.setSelected(false);
            clusterDAO.createConfig(clusterConfig);
            clusterEntity.getClusterConfigEntities().add(clusterConfig);
            // ensure there is at least some pause between to ensure that the
            // timestamps are different
            Thread.sleep(5);
        }
        // save them all
        clusterEntity = clusterDAO.merge(clusterEntity);
        // create a service configuration for them
        cluster.createServiceConfigVersion(serviceName, "", "version-1", null);
        // create a new configuration in the new stack and enable it
        ClusterConfigEntity clusterConfigNewStack = new ClusterConfigEntity();
        clusterConfigNewStack.setClusterEntity(clusterEntity);
        clusterConfigNewStack.setConfigId(6L);
        clusterConfigNewStack.setStack(newStack);
        clusterConfigNewStack.setTag("version-6");
        clusterConfigNewStack.setData("{}");
        clusterConfigNewStack.setType(configType);
        clusterConfigNewStack.setTimestamp(System.currentTimeMillis());
        clusterConfigNewStack.setVersion(6L);
        clusterConfigNewStack.setSelected(true);
        clusterDAO.createConfig(clusterConfigNewStack);
        clusterEntity.getClusterConfigEntities().add(clusterConfigNewStack);
        clusterEntity = clusterDAO.merge(clusterEntity);
        Config config = configFactory.createExisting(cluster, clusterConfigNewStack);
        cluster.addConfig(config);
        // before creating the new service config version, we need to push the
        // service's desired repository forward
        service.setDesiredRepositoryVersion(repoVersion220);
        cluster.createServiceConfigVersion(serviceName, "", "version-2", null);
        // check that only the newest configuration is enabled
        ClusterConfigEntity clusterConfig = clusterDAO.findEnabledConfigByType(clusterEntity.getClusterId(), configType);
        assertTrue(clusterConfig.isSelected());
        junit.framework.Assert.assertEquals(clusterConfigNewStack.getTag(), clusterConfig.getTag());
        // move back to the original stack
        cluster.applyLatestConfigurations(stackId, serviceName);
        clusterEntity = clusterDAO.findByName("c1");
        // now check that latest config from the original stack is enabled
        clusterConfig = clusterDAO.findEnabledConfigByType(clusterEntity.getClusterId(), configType);
        assertTrue(clusterConfig.isSelected());
        junit.framework.Assert.assertEquals("version-5", clusterConfig.getTag());
    }

    /**
     * Tests that applying configurations for a given stack correctly sets
     * {@link DesiredConfig}s.
     */
    @Test
    public void testDesiredConfigurationsAfterApplyingLatestForStack() throws Exception {
        StackId stackId = new StackId("HDP-2.0.6");
        StackId newStackId = new StackId("HDP-2.2.0");
        createDefaultCluster(Sets.newHashSet("host-1"), stackId);
        Cluster cluster = clusters.getCluster("c1");
        cluster.setCurrentStackVersion(stackId);
        cluster.setDesiredStackVersion(stackId);
        RepositoryVersionEntity repoVersion220 = helper.getOrCreateRepositoryVersion(newStackId, "2.2.0-1234");
        ConfigHelper configHelper = injector.getInstance(ConfigHelper.class);
        // make sure the stacks are different
        assertFalse(stackId.equals(newStackId));
        // add a service
        String serviceName = "ZOOKEEPER";
        RepositoryVersionEntity repositoryVersion = helper.getOrCreateRepositoryVersion(c1);
        Service service = cluster.addService(serviceName, repositoryVersion);
        String configType = "zoo.cfg";
        Map<String, String> properties = new HashMap<>();
        Map<String, Map<String, String>> propertiesAttributes = new HashMap<>();
        // config for v1 on current stack
        properties.put("foo-property-1", "foo-value-1");
        Config c1 = configFactory.createNew(stackId, cluster, configType, "version-1", properties, propertiesAttributes);
        // make v1 "current"
        cluster.addDesiredConfig("admin", Sets.newHashSet(c1), "note-1");
        // bump the repo version and the desired stack
        service.setDesiredRepositoryVersion(repoVersion220);
        cluster.setDesiredStackVersion(newStackId);
        // save v2
        // config for v2 on new stack
        properties.put("foo-property-2", "foo-value-2");
        Config c2 = configFactory.createNew(newStackId, cluster, configType, "version-2", properties, propertiesAttributes);
        // make v2 "current"
        cluster.addDesiredConfig("admin", Sets.newHashSet(c2), "note-2");
        // check desired config
        Map<String, DesiredConfig> desiredConfigs = cluster.getDesiredConfigs();
        DesiredConfig desiredConfig = desiredConfigs.get(configType);
        Assert.assertNotNull(desiredConfig);
        Assert.assertEquals(Long.valueOf(2), desiredConfig.getVersion());
        Assert.assertEquals("version-2", desiredConfig.getTag());
        String hostName = cluster.getHosts().iterator().next().getHostName();
        // {config-type={tag=version-2}}
        Map<String, Map<String, String>> effectiveDesiredTags = configHelper.getEffectiveDesiredTags(cluster, hostName);
        Assert.assertEquals("version-2", effectiveDesiredTags.get(configType).get("tag"));
        // move the service back to the old repo version / stack
        service.setDesiredRepositoryVersion(repositoryVersion);
        // apply the configs for the old stack
        cluster.applyLatestConfigurations(stackId, serviceName);
        // {config-type={tag=version-1}}
        effectiveDesiredTags = configHelper.getEffectiveDesiredTags(cluster, hostName);
        Assert.assertEquals("version-1", effectiveDesiredTags.get(configType).get("tag"));
        desiredConfigs = cluster.getDesiredConfigs();
        desiredConfig = desiredConfigs.get(configType);
        Assert.assertNotNull(desiredConfig);
        Assert.assertEquals(Long.valueOf(1), desiredConfig.getVersion());
        Assert.assertEquals("version-1", desiredConfig.getTag());
    }

    /**
     * Tests removing configurations and configuration mappings by stack.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRemoveConfigurations() throws Exception {
        StackId stackId = new StackId("HDP-2.0.6");
        StackId newStackId = new StackId("HDP-2.2.0");
        createDefaultCluster(Sets.newHashSet("host-1"), stackId);
        Cluster cluster = clusters.getCluster("c1");
        ClusterEntity clusterEntity = clusterDAO.findByName("c1");
        RepositoryVersionEntity repoVersion220 = helper.getOrCreateRepositoryVersion(newStackId, "2.2.0-1234");
        StackEntity currentStack = stackDAO.find(stackId.getStackName(), stackId.getStackVersion());
        StackEntity newStack = stackDAO.find(newStackId.getStackName(), newStackId.getStackVersion());
        assertFalse(stackId.equals(newStackId));
        // add a service
        String serviceName = "ZOOKEEPER";
        RepositoryVersionEntity repositoryVersion = helper.getOrCreateRepositoryVersion(c1);
        Service service = cluster.addService(serviceName, repositoryVersion);
        String configType = "zoo.cfg";
        ClusterConfigEntity clusterConfig = new ClusterConfigEntity();
        clusterConfig.setClusterEntity(clusterEntity);
        clusterConfig.setConfigId(1L);
        clusterConfig.setStack(currentStack);
        clusterConfig.setTag("version-1");
        clusterConfig.setData("{}");
        clusterConfig.setType(configType);
        clusterConfig.setTimestamp(1L);
        clusterConfig.setVersion(1L);
        clusterConfig.setSelected(true);
        clusterDAO.createConfig(clusterConfig);
        clusterEntity.getClusterConfigEntities().add(clusterConfig);
        clusterEntity = clusterDAO.merge(clusterEntity);
        Config config = configFactory.createExisting(cluster, clusterConfig);
        cluster.addConfig(config);
        // create the service version association
        cluster.createServiceConfigVersion(serviceName, "", "version-1", null);
        // now un-select it and create a new config
        clusterConfig.setSelected(false);
        clusterConfig = clusterDAO.merge(clusterConfig);
        ClusterConfigEntity newClusterConfig = new ClusterConfigEntity();
        newClusterConfig.setClusterEntity(clusterEntity);
        newClusterConfig.setConfigId(2L);
        newClusterConfig.setStack(newStack);
        newClusterConfig.setTag("version-2");
        newClusterConfig.setData("{}");
        newClusterConfig.setType(configType);
        newClusterConfig.setTimestamp(2L);
        newClusterConfig.setVersion(2L);
        newClusterConfig.setSelected(true);
        clusterDAO.createConfig(newClusterConfig);
        clusterEntity.getClusterConfigEntities().add(newClusterConfig);
        clusterEntity = clusterDAO.merge(clusterEntity);
        config = configFactory.createExisting(cluster, newClusterConfig);
        cluster.addConfig(config);
        // before creating the new service config version, we need to push the
        // service's desired repository forward
        service.setDesiredRepositoryVersion(repoVersion220);
        cluster.createServiceConfigVersion(serviceName, "", "version-2", null);
        cluster.applyLatestConfigurations(newStackId, serviceName);
        // get back the cluster configs for the new stack
        List<ClusterConfigEntity> clusterConfigs = clusterDAO.getAllConfigurations(cluster.getClusterId(), newStackId);
        assertEquals(1, clusterConfigs.size());
        // remove the configs
        cluster.removeConfigurations(newStackId, serviceName);
        clusterConfigs = clusterDAO.getAllConfigurations(cluster.getClusterId(), newStackId);
        assertEquals(0, clusterConfigs.size());
    }

    /**
     * Tests that properties request from {@code cluster-env} are correctly cached
     * and invalidated.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testCachedClusterProperties() throws Exception {
        EventBusSynchronizer.synchronizeAmbariEventPublisher(injector);
        AmbariEventPublisher publisher = injector.getInstance(AmbariEventPublisher.class);
        createDefaultCluster();
        Cluster cluster = clusters.getCluster("c1");
        Assert.assertFalse(isClusterPropertyCached("foo"));
        String property = cluster.getClusterProperty("foo", "bar");
        Assert.assertEquals("bar", property);
        Assert.assertTrue(isClusterPropertyCached("foo"));
        // cause a cache invalidation
        ClusterConfigChangedEvent event = new ClusterConfigChangedEvent(cluster.getClusterName(), ConfigHelper.CLUSTER_ENV, null, 1L);
        publisher.publish(event);
        Assert.assertFalse(isClusterPropertyCached("foo"));
    }
}

