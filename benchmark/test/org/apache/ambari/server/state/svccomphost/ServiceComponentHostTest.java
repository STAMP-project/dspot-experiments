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
package org.apache.ambari.server.state.svccomphost;


import MaintenanceState.OFF;
import MaintenanceState.ON;
import RepositoryVersionState.CURRENT;
import RepositoryVersionState.OUT_OF_SYNC;
import ServiceComponentHostEventType.HOST_SVCCOMP_DISABLE;
import ServiceComponentHostEventType.HOST_SVCCOMP_INSTALL;
import ServiceComponentHostEventType.HOST_SVCCOMP_START;
import ServiceComponentHostEventType.HOST_SVCCOMP_STOP;
import ServiceComponentHostEventType.HOST_SVCCOMP_UNINSTALL;
import ServiceComponentHostEventType.HOST_SVCCOMP_WIPEOUT;
import State.DISABLED;
import State.INIT;
import State.INSTALLED;
import State.INSTALLING;
import State.INSTALL_FAILED;
import State.STARTED;
import State.STARTING;
import State.STOPPING;
import State.UNINSTALLED;
import State.UNINSTALLING;
import State.UNKNOWN;
import State.UPGRADING;
import State.WIPING_OUT;
import UpgradeState.COMPLETE;
import com.google.inject.Inject;
import com.google.inject.Injector;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.controller.ServiceComponentHostResponse;
import org.apache.ambari.server.orm.OrmTestHelper;
import org.apache.ambari.server.orm.dao.ClusterDAO;
import org.apache.ambari.server.orm.dao.HostComponentDesiredStateDAO;
import org.apache.ambari.server.orm.dao.HostComponentStateDAO;
import org.apache.ambari.server.orm.dao.HostDAO;
import org.apache.ambari.server.orm.entities.HostComponentDesiredStateEntity;
import org.apache.ambari.server.orm.entities.HostComponentStateEntity;
import org.apache.ambari.server.orm.entities.HostEntity;
import org.apache.ambari.server.orm.entities.HostVersionEntity;
import org.apache.ambari.server.orm.entities.RepositoryVersionEntity;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.ConfigFactory;
import org.apache.ambari.server.state.ServiceComponentFactory;
import org.apache.ambari.server.state.ServiceComponentHost;
import org.apache.ambari.server.state.ServiceComponentHostEvent;
import org.apache.ambari.server.state.ServiceComponentHostFactory;
import org.apache.ambari.server.state.ServiceFactory;
import org.apache.ambari.server.state.StackId;
import org.apache.ambari.server.state.State;
import org.apache.ambari.server.state.configgroup.ConfigGroupFactory;
import org.apache.ambari.server.state.fsm.InvalidStateTransitionException;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ServiceComponentHostTest {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceComponentHostTest.class);

    @Inject
    private Injector injector;

    @Inject
    private Clusters clusters;

    @Inject
    private ServiceFactory serviceFactory;

    @Inject
    private ServiceComponentFactory serviceComponentFactory;

    @Inject
    private ServiceComponentHostFactory serviceComponentHostFactory;

    @Inject
    private ConfigFactory configFactory;

    @Inject
    private ConfigGroupFactory configGroupFactory;

    @Inject
    private OrmTestHelper helper;

    @Inject
    private ClusterDAO clusterDAO;

    @Inject
    private HostDAO hostDAO;

    @Inject
    private HostComponentDesiredStateDAO hostComponentDesiredStateDAO;

    @Inject
    private HostComponentStateDAO hostComponentStateDAO;

    private String clusterName = "c1";

    private String hostName1 = "h1";

    private Map<String, String> hostAttributes = new HashMap<>();

    private RepositoryVersionEntity repositoryVersion;

    @Test
    public void testClientStateFlow() throws Exception {
        ServiceComponentHostImpl impl = ((ServiceComponentHostImpl) (createNewServiceComponentHost(clusterName, "HDFS", "HDFS_CLIENT", hostName1, true)));
        runStateChanges(impl, HOST_SVCCOMP_INSTALL, INIT, INSTALLING, INSTALL_FAILED, INSTALLED);
        boolean exceptionThrown = false;
        try {
            runStateChanges(impl, HOST_SVCCOMP_START, INSTALLED, STARTING, INSTALLED, STARTED);
        } catch (Exception e) {
            exceptionThrown = true;
        }
        Assert.assertTrue("Exception not thrown on invalid event", exceptionThrown);
        runStateChanges(impl, HOST_SVCCOMP_UNINSTALL, INSTALLED, UNINSTALLING, UNINSTALLING, UNINSTALLED);
        runStateChanges(impl, HOST_SVCCOMP_WIPEOUT, UNINSTALLED, WIPING_OUT, WIPING_OUT, INIT);
        // check can be removed
        for (State state : State.values()) {
            impl.setState(state);
            if (state.isRemovableState()) {
                Assert.assertTrue(impl.canBeRemoved());
            } else {
                Assert.assertFalse(impl.canBeRemoved());
            }
        }
    }

    @Test
    public void testDaemonStateFlow() throws Exception {
        ServiceComponentHostImpl impl = ((ServiceComponentHostImpl) (createNewServiceComponentHost(clusterName, "HDFS", "DATANODE", hostName1, false)));
        runStateChanges(impl, HOST_SVCCOMP_INSTALL, INIT, INSTALLING, INSTALL_FAILED, INSTALLED);
        runStateChanges(impl, HOST_SVCCOMP_START, INSTALLED, STARTING, INSTALLED, STARTED);
        runStateChanges(impl, HOST_SVCCOMP_STOP, STARTED, STOPPING, STARTED, INSTALLED);
        runStateChanges(impl, HOST_SVCCOMP_UNINSTALL, INSTALLED, UNINSTALLING, UNINSTALLING, UNINSTALLED);
        runStateChanges(impl, HOST_SVCCOMP_WIPEOUT, UNINSTALLED, WIPING_OUT, WIPING_OUT, INIT);
    }

    @Test
    public void testGetAndSetBasicInfo() throws AmbariException {
        ServiceComponentHost sch = createNewServiceComponentHost(clusterName, "HDFS", "NAMENODE", hostName1, false);
        sch.setDesiredState(INSTALLED);
        sch.setState(INSTALLING);
        Assert.assertEquals(INSTALLING, sch.getState());
        Assert.assertEquals(INSTALLED, sch.getDesiredState());
    }

    @Test
    public void testConvertToResponse() throws AmbariException {
        ServiceComponentHost sch = createNewServiceComponentHost(clusterName, "HDFS", "DATANODE", hostName1, false);
        sch.setDesiredState(INSTALLED);
        sch.setState(INSTALLING);
        ServiceComponentHostResponse r = sch.convertToResponse(null);
        Assert.assertEquals("HDFS", r.getServiceName());
        Assert.assertEquals("DATANODE", r.getComponentName());
        Assert.assertEquals(hostName1, r.getHostname());
        Assert.assertEquals(clusterName, r.getClusterName());
        Assert.assertEquals(INSTALLED.toString(), r.getDesiredState());
        Assert.assertEquals(INSTALLING.toString(), r.getLiveState());
        Assert.assertEquals(repositoryVersion.getStackId().toString(), r.getDesiredStackVersion());
        Assert.assertFalse(r.isStaleConfig());
        // TODO check configs
        StringBuilder sb = new StringBuilder();
        sch.debugDump(sb);
        Assert.assertFalse(sb.toString().isEmpty());
    }

    @Test
    public void testStopInVariousStates() throws AmbariException, InvalidStateTransitionException {
        ServiceComponentHost sch = createNewServiceComponentHost(clusterName, "HDFS", "DATANODE", hostName1, false);
        ServiceComponentHostImpl impl = ((ServiceComponentHostImpl) (sch));
        sch.setDesiredState(STARTED);
        sch.setState(INSTALLED);
        long timestamp = 0;
        ServiceComponentHostEvent stopEvent = createEvent(impl, (++timestamp), HOST_SVCCOMP_STOP);
        long startTime = timestamp;
        impl.handleEvent(stopEvent);
        Assert.assertEquals(startTime, impl.getLastOpStartTime());
        Assert.assertEquals((-1), impl.getLastOpLastUpdateTime());
        Assert.assertEquals((-1), impl.getLastOpEndTime());
        Assert.assertEquals(STOPPING, impl.getState());
        sch.setState(INSTALL_FAILED);
        boolean exceptionThrown = false;
        try {
            impl.handleEvent(stopEvent);
        } catch (Exception e) {
            exceptionThrown = true;
        }
        Assert.assertTrue("Exception not thrown on invalid event", exceptionThrown);
        Assert.assertEquals(startTime, impl.getLastOpStartTime());
        Assert.assertEquals((-1), impl.getLastOpLastUpdateTime());
        Assert.assertEquals((-1), impl.getLastOpEndTime());
        sch.setState(INSTALLED);
        ServiceComponentHostEvent stopEvent2 = createEvent(impl, (++timestamp), HOST_SVCCOMP_STOP);
        startTime = timestamp;
        impl.handleEvent(stopEvent2);
        Assert.assertEquals(startTime, impl.getLastOpStartTime());
        Assert.assertEquals((-1), impl.getLastOpLastUpdateTime());
        Assert.assertEquals((-1), impl.getLastOpEndTime());
        Assert.assertEquals(STOPPING, impl.getState());
    }

    @Test
    public void testDisableInVariousStates() throws AmbariException, InvalidStateTransitionException {
        ServiceComponentHost sch = createNewServiceComponentHost(clusterName, "HDFS", "DATANODE", hostName1, false);
        ServiceComponentHostImpl impl = ((ServiceComponentHostImpl) (sch));
        // Test valid states in which host component can be disabled
        long timestamp = 0;
        HashSet<State> validStates = new HashSet<>();
        validStates.add(INSTALLED);
        validStates.add(INSTALL_FAILED);
        validStates.add(UNKNOWN);
        validStates.add(DISABLED);
        for (State state : validStates) {
            sch.setState(state);
            ServiceComponentHostEvent disableEvent = createEvent(impl, (++timestamp), HOST_SVCCOMP_DISABLE);
            impl.handleEvent(disableEvent);
            // TODO: At present operation timestamps are not getting updated.
            Assert.assertEquals((-1), impl.getLastOpStartTime());
            Assert.assertEquals((-1), impl.getLastOpLastUpdateTime());
            Assert.assertEquals((-1), impl.getLastOpEndTime());
            Assert.assertEquals(DISABLED, impl.getState());
        }
        // Test invalid states in which host component cannot be disabled
        HashSet<State> invalidStates = new HashSet<>();
        invalidStates.add(INIT);
        invalidStates.add(INSTALLING);
        invalidStates.add(STARTING);
        invalidStates.add(STARTED);
        invalidStates.add(STOPPING);
        invalidStates.add(UNINSTALLING);
        invalidStates.add(UNINSTALLED);
        invalidStates.add(UPGRADING);
        for (State state : invalidStates) {
            sch.setState(state);
            ServiceComponentHostEvent disableEvent = createEvent(impl, (++timestamp), HOST_SVCCOMP_DISABLE);
            boolean exceptionThrown = false;
            try {
                impl.handleEvent(disableEvent);
            } catch (Exception e) {
                exceptionThrown = true;
            }
            Assert.assertTrue("Exception not thrown on invalid event", exceptionThrown);
            // TODO: At present operation timestamps are not getting updated.
            Assert.assertEquals((-1), impl.getLastOpStartTime());
            Assert.assertEquals((-1), impl.getLastOpLastUpdateTime());
            Assert.assertEquals((-1), impl.getLastOpEndTime());
        }
    }

    @Test
    public void testMaintenance() throws Exception {
        String stackVersion = "HDP-2.0.6";
        StackId stackId = new StackId(stackVersion);
        String clusterName = "c2";
        createCluster(stackId, clusterName);
        final String hostName = "h3";
        Set<String> hostNames = new HashSet<>();
        hostNames.add(hostName);
        addHostsToCluster(clusterName, hostAttributes, hostNames);
        Cluster cluster = clusters.getCluster(clusterName);
        helper.getOrCreateRepositoryVersion(stackId, stackId.getStackVersion());
        HostEntity hostEntity = hostDAO.findByName(hostName);
        Assert.assertNotNull(hostEntity);
        ServiceComponentHost sch1 = createNewServiceComponentHost(cluster, "HDFS", "NAMENODE", hostName);
        // ServiceComponentHost sch2 = createNewServiceComponentHost(cluster, "HDFS", "DATANODE", hostName);
        // ServiceComponentHost sch3 = createNewServiceComponentHost(cluster, "MAPREDUCE2", "HISTORYSERVER", hostName);
        HostComponentDesiredStateEntity entity = hostComponentDesiredStateDAO.findByIndex(cluster.getClusterId(), sch1.getServiceName(), sch1.getServiceComponentName(), hostEntity.getHostId());
        Assert.assertEquals(OFF, entity.getMaintenanceState());
        Assert.assertEquals(OFF, sch1.getMaintenanceState());
        sch1.setMaintenanceState(ON);
        Assert.assertEquals(ON, sch1.getMaintenanceState());
        entity = hostComponentDesiredStateDAO.findByIndex(cluster.getClusterId(), sch1.getServiceName(), sch1.getServiceComponentName(), hostEntity.getHostId());
        Assert.assertEquals(ON, entity.getMaintenanceState());
    }

    /**
     * Tests that the host version for a repository can transition properly to
     * CURRENT even if other components on that host have not reported in correct
     * for their own repo versions. This assures that the host version logic is
     * scoped to the repo that is transitioning and is not affected by other
     * components.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testHostVersionTransitionIsScopedByRepository() throws Exception {
        // put the existing host versions OUT_OF_SYNC
        HostEntity hostEntity = hostDAO.findByName(hostName1);
        Collection<HostVersionEntity> hostVersions = hostEntity.getHostVersionEntities();
        Assert.assertEquals(1, hostVersions.size());
        hostVersions.iterator().next().setState(OUT_OF_SYNC);
        hostDAO.merge(hostEntity);
        ServiceComponentHost namenode = createNewServiceComponentHost(clusterName, "HDFS", "NAMENODE", hostName1, false);
        namenode.setDesiredState(STARTED);
        namenode.setState(STARTED);
        ServiceComponentHost datanode = createNewServiceComponentHost(clusterName, "HDFS", "DATANODE", hostName1, false);
        datanode.setDesiredState(STARTED);
        datanode.setState(STARTED);
        ServiceComponentHost zkServer = createNewServiceComponentHost(clusterName, "ZOOKEEPER", "ZOOKEEPER_SERVER", hostName1, false);
        zkServer.setDesiredState(STARTED);
        zkServer.setState(STARTED);
        ServiceComponentHost zkClient = createNewServiceComponentHost(clusterName, "ZOOKEEPER", "ZOOKEEPER_CLIENT", hostName1, true);
        zkClient.setDesiredState(STARTED);
        zkClient.setState(STARTED);
        // put some host components into a bad state
        hostEntity = hostDAO.findByName(hostName1);
        Collection<HostComponentStateEntity> hostComponentStates = hostEntity.getHostComponentStateEntities();
        for (HostComponentStateEntity hostComponentState : hostComponentStates) {
            if (StringUtils.equals("HDFS", hostComponentState.getServiceName())) {
                hostComponentState.setVersion(UNKNOWN.name());
                hostComponentStateDAO.merge(hostComponentState);
            }
        }
        // create the repo just for ZK
        StackId stackId = new StackId("HDP-2.2.0");
        RepositoryVersionEntity patchRepositoryVersion = helper.getOrCreateRepositoryVersion(stackId, "2.2.0.0-1");
        // create the new host version
        zkServer.getServiceComponent().setDesiredRepositoryVersion(patchRepositoryVersion);
        zkClient.getServiceComponent().setDesiredRepositoryVersion(patchRepositoryVersion);
        helper.createHostVersion(hostName1, patchRepositoryVersion, RepositoryVersionState.INSTALLED);
        // ?move ZK components to UPGRADED and reporting the new version
        hostEntity = hostDAO.findByName(hostName1);
        hostComponentStates = hostEntity.getHostComponentStateEntities();
        for (HostComponentStateEntity hostComponentState : hostComponentStates) {
            if (StringUtils.equals("ZOOKEEPER", hostComponentState.getServiceName())) {
                hostComponentState.setVersion(patchRepositoryVersion.getVersion());
                hostComponentState.setUpgradeState(COMPLETE);
                hostComponentStateDAO.merge(hostComponentState);
            }
        }
        hostEntity = hostDAO.merge(hostEntity);
        zkServer.recalculateHostVersionState();
        // very transition to CURRENT
        hostVersions = hostEntity.getHostVersionEntities();
        Assert.assertEquals(2, hostVersions.size());
        for (HostVersionEntity hostVersion : hostVersions) {
            if (hostVersion.getRepositoryVersion().equals(repositoryVersion)) {
                Assert.assertEquals(OUT_OF_SYNC, hostVersion.getState());
            } else
                if (hostVersion.getRepositoryVersion().equals(patchRepositoryVersion)) {
                    Assert.assertEquals(CURRENT, hostVersion.getState());
                } else {
                    Assert.fail("Unexpected repository version");
                }

        }
    }
}

