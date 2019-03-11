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
package org.apache.ambari.server.state.host;


import HealthStatus.UNKNOWN;
import HostState.HEALTHY;
import HostState.HEARTBEAT_LOST;
import HostState.INIT;
import HostState.UNHEALTHY;
import HostState.WAITING_FOR_HOST_STATUS_UPDATES;
import MaintenanceState.OFF;
import MaintenanceState.ON;
import com.google.inject.Injector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.actionmanager.ActionManager;
import org.apache.ambari.server.agent.DiskInfo;
import org.apache.ambari.server.agent.HeartBeatHandler;
import org.apache.ambari.server.agent.HostInfo;
import org.apache.ambari.server.api.services.AmbariMetaInfo;
import org.apache.ambari.server.events.publishers.AmbariEventPublisher;
import org.apache.ambari.server.orm.OrmTestHelper;
import org.apache.ambari.server.orm.dao.HostDAO;
import org.apache.ambari.server.orm.entities.HostEntity;
import org.apache.ambari.server.orm.entities.HostStateEntity;
import org.apache.ambari.server.security.encryption.Encryptor;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.Config;
import org.apache.ambari.server.state.ConfigFactory;
import org.apache.ambari.server.state.DesiredConfig;
import org.apache.ambari.server.state.Host;
import org.apache.ambari.server.state.StackId;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HostTest {
    private Injector injector;

    private Clusters clusters;

    private HostDAO hostDAO;

    private OrmTestHelper helper;

    private static final Logger LOG = LoggerFactory.getLogger(HostTest.class);

    @Test
    public void testHostInfoImport() throws AmbariException {
        HostInfo info = new HostInfo();
        info.setMemorySize(100);
        info.setProcessorCount(10);
        info.setPhysicalProcessorCount(2);
        List<DiskInfo> mounts = new ArrayList<>();
        mounts.add(new DiskInfo("/dev/sda", "/mnt/disk1", "5000000", "4000000", "10%", "size", "fstype"));
        info.setMounts(mounts);
        info.setHostName("foo");
        info.setInterfaces("fip_4");
        info.setArchitecture("os_arch");
        info.setOS("os_type");
        info.setMemoryTotal(10);
        clusters.addHost("foo");
        Host host = clusters.getHost("foo");
        host.importHostInfo(info);
        Assert.assertEquals(info.getHostName(), host.getHostName());
        Assert.assertEquals(info.getFreeMemory(), host.getAvailableMemBytes());
        Assert.assertEquals(info.getMemoryTotal(), host.getTotalMemBytes());
        Assert.assertEquals(info.getProcessorCount(), host.getCpuCount());
        Assert.assertEquals(info.getPhysicalProcessorCount(), host.getPhCpuCount());
        Assert.assertEquals(info.getMounts().size(), host.getDisksInfo().size());
        Assert.assertEquals(info.getArchitecture(), host.getOsArch());
        Assert.assertEquals(info.getOS(), host.getOsType());
    }

    @Test
    public void testHostOs() throws Exception {
        Clusters clusters = Mockito.mock(Clusters.class);
        ActionManager manager = Mockito.mock(ActionManager.class);
        Injector injector = Mockito.mock(Injector.class);
        Mockito.doNothing().when(injector).injectMembers(ArgumentMatchers.any());
        Mockito.when(injector.getInstance(AmbariEventPublisher.class)).thenReturn(Mockito.mock(AmbariEventPublisher.class));
        HeartBeatHandler handler = new HeartBeatHandler(clusters, manager, Encryptor.NONE, injector);
        String os = handler.getOsType("RedHat", "6.1");
        Assert.assertEquals("redhat6", os);
        os = handler.getOsType("RedHat", "6");
        Assert.assertEquals("redhat6", os);
        os = handler.getOsType("RedHat6", "");
        Assert.assertEquals("redhat6", os);
    }

    @Test
    public void testHostFSMInit() throws AmbariException {
        clusters.addHost("foo");
        Host host = clusters.getHost("foo");
        verifyHostState(host, INIT);
    }

    @Test
    public void testHostRegistrationFlow() throws Exception {
        clusters.addHost("foo");
        Host host = clusters.getHost("foo");
        registerHost(host);
        verifyHostState(host, WAITING_FOR_HOST_STATUS_UPDATES);
        boolean exceptionThrown = false;
        try {
            registerHost(host);
        } catch (Exception e) {
            // Expected
            exceptionThrown = true;
        }
        if (!exceptionThrown) {
            Assert.fail("Expected invalid transition exception to be thrown");
        }
        ensureHostUpdatesReceived(host);
        verifyHostState(host, HEALTHY);
        exceptionThrown = false;
        try {
            ensureHostUpdatesReceived(host);
        } catch (Exception e) {
            // Expected
            exceptionThrown = true;
        }
        if (!exceptionThrown) {
            Assert.fail("Expected invalid transition exception to be thrown");
        }
    }

    @Test
    public void testHostHeartbeatFlow() throws Exception {
        clusters.addHost("foo");
        Host host = clusters.getHost("foo");
        registerHost(host);
        ensureHostUpdatesReceived(host);
        // TODO need to verify audit logs generated
        // TODO need to verify health status updated properly
        long counter = 0;
        sendHealthyHeartbeat(host, (++counter));
        verifyHostState(host, HEALTHY);
        Assert.assertEquals(counter, host.getLastHeartbeatTime());
        sendHealthyHeartbeat(host, (++counter));
        verifyHostState(host, HEALTHY);
        Assert.assertEquals(counter, host.getLastHeartbeatTime());
        Assert.assertEquals(HealthStatus.HEALTHY, host.getHealthStatus().getHealthStatus());
        sendUnhealthyHeartbeat(host, (++counter));
        verifyHostState(host, UNHEALTHY);
        Assert.assertEquals(counter, host.getLastHeartbeatTime());
        Assert.assertEquals(HealthStatus.UNHEALTHY, host.getHealthStatus().getHealthStatus());
        sendUnhealthyHeartbeat(host, (++counter));
        verifyHostState(host, UNHEALTHY);
        Assert.assertEquals(counter, host.getLastHeartbeatTime());
        Assert.assertEquals(HealthStatus.UNHEALTHY, host.getHealthStatus().getHealthStatus());
        sendHealthyHeartbeat(host, (++counter));
        verifyHostState(host, HEALTHY);
        Assert.assertEquals(counter, host.getLastHeartbeatTime());
        Assert.assertEquals(HealthStatus.HEALTHY, host.getHealthStatus().getHealthStatus());
        timeoutHost(host);
        verifyHostState(host, HEARTBEAT_LOST);
        Assert.assertEquals(counter, host.getLastHeartbeatTime());
        Assert.assertEquals(UNKNOWN, host.getHealthStatus().getHealthStatus());
        timeoutHost(host);
        verifyHostState(host, HEARTBEAT_LOST);
        Assert.assertEquals(counter, host.getLastHeartbeatTime());
        Assert.assertEquals(UNKNOWN, host.getHealthStatus().getHealthStatus());
        try {
            sendUnhealthyHeartbeat(host, (++counter));
            Assert.fail("Invalid event should have triggered an exception");
        } catch (Exception e) {
            // Expected
        }
        verifyHostState(host, HEARTBEAT_LOST);
        try {
            sendHealthyHeartbeat(host, (++counter));
            Assert.fail("Invalid event should have triggered an exception");
        } catch (Exception e) {
            // Expected
        }
        verifyHostState(host, HEARTBEAT_LOST);
    }

    @Test
    public void testHostRegistrationsInAnyState() throws Exception {
        clusters.addHost("foo");
        Host host = clusters.getHost("foo");
        host.setIPv4("ipv4");
        host.setIPv6("ipv6");
        long counter = 0;
        registerHost(host);
        ensureHostUpdatesReceived(host);
        registerHost(host, false);
        ensureHostUpdatesReceived(host);
        sendHealthyHeartbeat(host, (++counter));
        verifyHostState(host, HEALTHY);
        registerHost(host, false);
        ensureHostUpdatesReceived(host);
        sendUnhealthyHeartbeat(host, (++counter));
        verifyHostState(host, UNHEALTHY);
        registerHost(host, false);
        ensureHostUpdatesReceived(host);
        timeoutHost(host);
        verifyHostState(host, HEARTBEAT_LOST);
        registerHost(host, false);
        ensureHostUpdatesReceived(host);
        host.setState(INIT);
        registerHost(host, false);
    }

    @Test
    public void testHostDesiredConfig() throws Exception {
        AmbariMetaInfo metaInfo = injector.getInstance(AmbariMetaInfo.class);
        StackId stackId = new StackId("HDP-0.1");
        clusters.addCluster("c1", stackId);
        Cluster c1 = clusters.getCluster("c1");
        helper.getOrCreateRepositoryVersion(stackId, stackId.getStackVersion());
        Assert.assertEquals("c1", c1.getClusterName());
        clusters.addHost("h1");
        Host host = clusters.getHost("h1");
        host.setIPv4("ipv4");
        host.setIPv6("ipv6");
        Map<String, String> hostAttributes = new HashMap<>();
        hostAttributes.put("os_family", "redhat");
        hostAttributes.put("os_release_version", "6.3");
        host.setHostAttributes(hostAttributes);
        c1.setDesiredStackVersion(stackId);
        clusters.mapHostToCluster("h1", "c1");
        ConfigFactory configFactory = injector.getInstance(ConfigFactory.class);
        Config config = configFactory.createNew(c1, "global", "v1", new HashMap<String, String>() {
            {
                put("a", "b");
                put("x", "y");
            }
        }, new HashMap());
        try {
            host.addDesiredConfig(c1.getClusterId(), true, null, config);
            Assert.fail("Expect failure when user is not specified.");
        } catch (Exception e) {
            // testing exception
        }
        host.addDesiredConfig(c1.getClusterId(), true, "_test", config);
        Map<String, DesiredConfig> map = host.getDesiredConfigs(c1.getClusterId());
        Assert.assertTrue("Expect desired config to contain global", map.containsKey("global"));
        config = configFactory.createNew(c1, "global", "v2", new HashMap<String, String>() {
            {
                put("c", "d");
            }
        }, new HashMap());
        host.addDesiredConfig(c1.getClusterId(), true, "_test1", config);
        map = host.getDesiredConfigs(c1.getClusterId());
        Assert.assertTrue("Expect desired config to contain global", map.containsKey("global"));
        Assert.assertEquals("Expect version to be 'v2'", "v2", map.get("global").getTag());
        host.addDesiredConfig(c1.getClusterId(), false, "_test2", config);
        map = host.getDesiredConfigs(c1.getClusterId());
        Assert.assertEquals("Expect no mapping configs", 0, map.size());
    }

    @Test
    public void testHostMaintenance() throws Exception {
        AmbariMetaInfo metaInfo = injector.getInstance(AmbariMetaInfo.class);
        StackId stackId = new StackId("HDP-0.1");
        clusters.addCluster("c1", stackId);
        Cluster c1 = clusters.getCluster("c1");
        Assert.assertEquals("c1", c1.getClusterName());
        clusters.addHost("h1");
        Host host = clusters.getHost("h1");
        host.setIPv4("ipv4");
        host.setIPv6("ipv6");
        Map<String, String> hostAttributes = new HashMap<>();
        hostAttributes.put("os_family", "redhat");
        hostAttributes.put("os_release_version", "6.3");
        host.setHostAttributes(hostAttributes);
        helper.getOrCreateRepositoryVersion(stackId, stackId.getStackVersion());
        c1.setDesiredStackVersion(stackId);
        clusters.mapHostToCluster("h1", "c1");
        HostEntity entity = hostDAO.findByName("h1");
        HostStateEntity stateEntity = entity.getHostStateEntity();
        Assert.assertNull(stateEntity.getMaintenanceState());
        Assert.assertEquals(OFF, host.getMaintenanceState(c1.getClusterId()));
        host.setMaintenanceState(c1.getClusterId(), ON);
        entity = hostDAO.findByName("h1");
        stateEntity = entity.getHostStateEntity();
        Assert.assertNotNull(stateEntity.getMaintenanceState());
        Assert.assertEquals(ON, host.getMaintenanceState(c1.getClusterId()));
    }

    @Test
    public void testHostPersist() throws Exception {
        clusters.addHost("foo");
        Host host = clusters.getHost("foo");
        String rackInfo = "rackInfo";
        long lastRegistrationTime = System.currentTimeMillis();
        host.setRackInfo(rackInfo);
        host.setLastRegistrationTime(lastRegistrationTime);
        Assert.assertEquals(rackInfo, host.getRackInfo());
        Assert.assertEquals(lastRegistrationTime, host.getLastRegistrationTime());
    }
}

