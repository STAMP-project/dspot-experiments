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
package org.apache.hadoop.yarn.service;


import ComponentState.FLEXING;
import ComponentState.STABLE;
import FinalApplicationStatus.ENDED;
import PlacementScope.NODE;
import PlacementType.ANTI_AFFINITY;
import ServiceState.EXPRESS_UPGRADING;
import ServiceState.FAILED;
import ServiceState.STARTED;
import ServiceState.UPGRADING;
import SignalContainerCommand.FORCEFUL_SHUTDOWN;
import SignalContainerCommand.GRACEFUL_SHUTDOWN;
import YarnConfiguration.NM_RESOURCEMANAGER_CONNECT_RETRY_INTERVAL_MS;
import YarnConfiguration.RECOVERY_ENABLED;
import YarnConfiguration.RM_PLACEMENT_CONSTRAINTS_HANDLER;
import YarnConfiguration.RM_WORK_PRESERVING_RECOVERY_ENABLED;
import YarnConfiguration.SCHEDULER_RM_PLACEMENT_CONSTRAINTS_HANDLER;
import YarnConfiguration.YARN_MINICLUSTER_FIXED_PORTS;
import YarnConfiguration.YARN_MINICLUSTER_USE_RPC;
import YarnServiceConstants.APP_TYPE;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.registry.client.binding.RegistryUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainersRequest;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import org.apache.hadoop.yarn.service.api.records.Component;
import org.apache.hadoop.yarn.service.api.records.Configuration;
import org.apache.hadoop.yarn.service.api.records.Container;
import org.apache.hadoop.yarn.service.api.records.PlacementConstraint;
import org.apache.hadoop.yarn.service.api.records.PlacementPolicy;
import org.apache.hadoop.yarn.service.api.records.Service;
import org.apache.hadoop.yarn.service.client.ServiceClient;
import org.apache.hadoop.yarn.service.utils.ServiceApiUtil;
import org.apache.hadoop.yarn.service.utils.SliderFileSystem;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * End to end tests to test deploying services with MiniYarnCluster and a in-JVM
 * ZK testing cluster.
 */
public class TestYarnNativeServices extends ServiceTestUtils {
    private static final Logger LOG = LoggerFactory.getLogger(TestYarnNativeServices.class);

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    // End-to-end test to use ServiceClient to deploy a service.
    // 1. Create a service with 2 components, each of which has 2 containers
    // 2. Flex up each component to 3 containers and check the component instance names
    // 3. Flex down each component to 1 container and check the component instance names
    // 4. Flex up each component to 2 containers and check the component instance names
    // 5. Stop the service
    // 6. Destroy the service
    @Test(timeout = 200000)
    public void testCreateFlexStopDestroyService() throws Exception {
        setupInternal(ServiceTestUtils.NUM_NMS);
        ServiceClient client = ServiceTestUtils.createClient(getConf());
        Service exampleApp = ServiceTestUtils.createExampleApplication();
        client.actionCreate(exampleApp);
        SliderFileSystem fileSystem = new SliderFileSystem(getConf());
        Path appDir = fileSystem.buildClusterDirPath(exampleApp.getName());
        // check app.json is persisted.
        Assert.assertTrue(getFS().exists(new Path(appDir, ((exampleApp.getName()) + ".json"))));
        waitForServiceToBeStable(client, exampleApp);
        // Flex two components, each from 2 container to 3 containers.
        flexComponents(client, exampleApp, 3L);
        // wait for flex to be completed, increase from 2 to 3 containers.
        waitForServiceToBeStable(client, exampleApp);
        // check all instances name for each component are in sequential order.
        checkCompInstancesInOrder(client, exampleApp);
        // flex down to 1
        flexComponents(client, exampleApp, 1L);
        waitForServiceToBeStable(client, exampleApp);
        checkCompInstancesInOrder(client, exampleApp);
        // check component dir and registry are cleaned up.
        // flex up again to 2
        flexComponents(client, exampleApp, 2L);
        waitForServiceToBeStable(client, exampleApp);
        checkCompInstancesInOrder(client, exampleApp);
        // stop the service
        TestYarnNativeServices.LOG.info("Stop the service");
        client.actionStop(exampleApp.getName(), true);
        ApplicationReport report = client.getYarnClient().getApplicationReport(ApplicationId.fromString(exampleApp.getId()));
        // AM unregisters with RM successfully
        Assert.assertEquals(YarnApplicationState.FINISHED, report.getYarnApplicationState());
        Assert.assertEquals(ENDED, report.getFinalApplicationStatus());
        String serviceZKPath = RegistryUtils.servicePath(RegistryUtils.currentUser(), APP_TYPE, exampleApp.getName());
        Assert.assertFalse("Registry ZK service path still exists after stop", getCuratorService().zkPathExists(serviceZKPath));
        TestYarnNativeServices.LOG.info("Destroy the service");
        // destroy the service and check the app dir is deleted from fs.
        Assert.assertEquals(0, client.actionDestroy(exampleApp.getName()));
        // check the service dir on hdfs (in this case, local fs) are deleted.
        Assert.assertFalse(getFS().exists(appDir));
        // check that destroying again does not succeed
        Assert.assertEquals(EXIT_NOT_FOUND, client.actionDestroy(exampleApp.getName()));
    }

    // Save a service without starting it and ensure that stop does not NPE and
    // that service can be successfully destroyed
    @Test(timeout = 200000)
    public void testStopDestroySavedService() throws Exception {
        setupInternal(ServiceTestUtils.NUM_NMS);
        ServiceClient client = ServiceTestUtils.createClient(getConf());
        Service exampleApp = ServiceTestUtils.createExampleApplication();
        client.actionBuild(exampleApp);
        Assert.assertEquals(EXIT_COMMAND_ARGUMENT_ERROR, client.actionStop(exampleApp.getName()));
        Assert.assertEquals(0, client.actionDestroy(exampleApp.getName()));
    }

    // Create compa with 2 containers
    // Create compb with 2 containers which depends on compa
    // Create compc with 2 containers which depends on compb
    // Check containers for compa started before containers for compb before
    // containers for compc
    @Test(timeout = 200000)
    public void testComponentStartOrder() throws Exception {
        setupInternal(ServiceTestUtils.NUM_NMS);
        ServiceClient client = ServiceTestUtils.createClient(getConf());
        Service exampleApp = new Service();
        exampleApp.setName("teststartorder");
        exampleApp.setVersion("v1");
        exampleApp.addComponent(ServiceTestUtils.createComponent("compa", 2, "sleep 1000"));
        // Let compb depend on compa
        Component compb = ServiceTestUtils.createComponent("compb", 2, "sleep 1000");
        compb.setDependencies(Collections.singletonList("compa"));
        exampleApp.addComponent(compb);
        // Let compc depend on compb
        Component compc = ServiceTestUtils.createComponent("compc", 2, "sleep 1000");
        compc.setDependencies(Collections.singletonList("compb"));
        exampleApp.addComponent(compc);
        client.actionCreate(exampleApp);
        waitForServiceToBeStable(client, exampleApp);
        // check that containers for compa are launched before containers for compb
        checkContainerLaunchDependencies(client, exampleApp, "compa", "compb", "compc");
        client.actionStop(exampleApp.getName(), true);
        client.actionDestroy(exampleApp.getName());
    }

    @Test(timeout = 200000)
    public void testCreateServiceSameNameDifferentUser() throws Exception {
        String sameAppName = "same-name";
        String userA = "usera";
        String userB = "userb";
        setupInternal(ServiceTestUtils.NUM_NMS);
        ServiceClient client = ServiceTestUtils.createClient(getConf());
        String origBasePath = getConf().get(YARN_SERVICE_BASE_PATH);
        Service userAApp = new Service();
        userAApp.setName(sameAppName);
        userAApp.setVersion("v1");
        userAApp.addComponent(ServiceTestUtils.createComponent("comp", 1, "sleep 1000"));
        Service userBApp = new Service();
        userBApp.setName(sameAppName);
        userBApp.setVersion("v1");
        userBApp.addComponent(ServiceTestUtils.createComponent("comp", 1, "sleep 1000"));
        File userABasePath = null;
        File userBBasePath = null;
        try {
            userABasePath = new File(origBasePath, userA);
            userABasePath.mkdirs();
            getConf().set(YARN_SERVICE_BASE_PATH, userABasePath.getAbsolutePath());
            client.actionCreate(userAApp);
            waitForServiceToBeStarted(client, userAApp);
            userBBasePath = new File(origBasePath, userB);
            userBBasePath.mkdirs();
            getConf().set(YARN_SERVICE_BASE_PATH, userBBasePath.getAbsolutePath());
            client.actionBuild(userBApp);
        } catch (Exception e) {
            Assert.fail(("Exception should not be thrown - " + (e.getLocalizedMessage())));
        } finally {
            if (userABasePath != null) {
                getConf().set(YARN_SERVICE_BASE_PATH, userABasePath.getAbsolutePath());
                client.actionStop(sameAppName, true);
                client.actionDestroy(sameAppName);
            }
            if (userBBasePath != null) {
                getConf().set(YARN_SERVICE_BASE_PATH, userBBasePath.getAbsolutePath());
                client.actionDestroy(sameAppName);
            }
        }
        // Need to extend this test to validate that different users can create
        // apps of exact same name. So far only create followed by build is tested.
        // Need to test create followed by create.
    }

    @Test(timeout = 200000)
    public void testCreateServiceSameNameSameUser() throws Exception {
        String sameAppName = "same-name";
        String user = UserGroupInformation.getCurrentUser().getUserName();
        System.setProperty("user.name", user);
        setupInternal(ServiceTestUtils.NUM_NMS);
        ServiceClient client = ServiceTestUtils.createClient(getConf());
        Service appA = new Service();
        appA.setName(sameAppName);
        appA.setVersion("v1");
        appA.addComponent(ServiceTestUtils.createComponent("comp", 1, "sleep 1000"));
        Service appB = new Service();
        appB.setName(sameAppName);
        appB.setVersion("v1");
        appB.addComponent(ServiceTestUtils.createComponent("comp", 1, "sleep 1000"));
        try {
            client.actionBuild(appA);
            client.actionBuild(appB);
        } catch (Exception e) {
            String expectedMsg = "Service Instance dir already exists:";
            if ((e.getLocalizedMessage()) != null) {
                Assert.assertThat(e.getLocalizedMessage(), CoreMatchers.containsString(expectedMsg));
            } else {
                Assert.fail(("Message cannot be null. It has to say - " + expectedMsg));
            }
        } finally {
            // cleanup
            client.actionDestroy(sameAppName);
        }
        try {
            client.actionCreate(appA);
            waitForServiceToBeStarted(client, appA);
            client.actionCreate(appB);
            waitForServiceToBeStarted(client, appB);
        } catch (Exception e) {
            String expectedMsg = ("Failed to create service " + sameAppName) + ", because it already exists.";
            if ((e.getLocalizedMessage()) != null) {
                Assert.assertThat(e.getLocalizedMessage(), CoreMatchers.containsString(expectedMsg));
            } else {
                Assert.fail(("Message cannot be null. It has to say - " + expectedMsg));
            }
        } finally {
            // cleanup
            client.actionStop(sameAppName, true);
            client.actionDestroy(sameAppName);
        }
    }

    // Test to verify recovery of SeviceMaster after RM is restarted.
    // 1. Create an example service.
    // 2. Restart RM.
    // 3. Fail the application attempt.
    // 4. Verify ServiceMaster recovers.
    @Test(timeout = 200000)
    public void testRecoverComponentsAfterRMRestart() throws Exception {
        YarnConfiguration conf = new YarnConfiguration();
        conf.setBoolean(RECOVERY_ENABLED, true);
        conf.setBoolean(RM_WORK_PRESERVING_RECOVERY_ENABLED, true);
        conf.setLong(NM_RESOURCEMANAGER_CONNECT_RETRY_INTERVAL_MS, 500L);
        conf.setBoolean(YARN_MINICLUSTER_FIXED_PORTS, true);
        conf.setBoolean(YARN_MINICLUSTER_USE_RPC, true);
        setConf(conf);
        setupInternal(ServiceTestUtils.NUM_NMS);
        ServiceClient client = ServiceTestUtils.createClient(getConf());
        Service exampleApp = ServiceTestUtils.createExampleApplication();
        client.actionCreate(exampleApp);
        Multimap<String, String> containersBeforeFailure = waitForAllCompToBeReady(client, exampleApp);
        TestYarnNativeServices.LOG.info("Restart the resource manager");
        getYarnCluster().restartResourceManager(getYarnCluster().getActiveRMIndex());
        GenericTestUtils.waitFor(() -> (getYarnCluster().getResourceManager().getServiceState()) == org.apache.hadoop.service.Service.STATE.STARTED, 2000, 200000);
        Assert.assertTrue("node managers connected", getYarnCluster().waitForNodeManagersToConnect(5000));
        ApplicationId exampleAppId = ApplicationId.fromString(exampleApp.getId());
        ApplicationAttemptId applicationAttemptId = client.getYarnClient().getApplicationReport(exampleAppId).getCurrentApplicationAttemptId();
        TestYarnNativeServices.LOG.info("Fail the application attempt {}", applicationAttemptId);
        client.getYarnClient().failApplicationAttempt(applicationAttemptId);
        // wait until attempt 2 is running
        GenericTestUtils.waitFor(() -> {
            try {
                ApplicationReport ar = client.getYarnClient().getApplicationReport(exampleAppId);
                return ((ar.getCurrentApplicationAttemptId().getAttemptId()) == 2) && ((ar.getYarnApplicationState()) == YarnApplicationState.RUNNING);
            } catch (YarnException | IOException e) {
                throw new <e>RuntimeException("while waiting");
            }
        }, 2000, 200000);
        Multimap<String, String> containersAfterFailure = waitForAllCompToBeReady(client, exampleApp);
        containersBeforeFailure.keys().forEach(( compName) -> {
            Assert.assertEquals(("num containers after by restart for " + compName), containersBeforeFailure.get(compName).size(), ((containersAfterFailure.get(compName)) == null ? 0 : containersAfterFailure.get(compName).size()));
        });
        TestYarnNativeServices.LOG.info("Stop/destroy service {}", exampleApp);
        client.actionStop(exampleApp.getName(), true);
        client.actionDestroy(exampleApp.getName());
    }

    @Test(timeout = 200000)
    public void testUpgrade() throws Exception {
        setupInternal(ServiceTestUtils.NUM_NMS);
        getConf().setBoolean(YARN_SERVICE_UPGRADE_ENABLED, true);
        ServiceClient client = ServiceTestUtils.createClient(getConf());
        Service service = ServiceTestUtils.createExampleApplication();
        client.actionCreate(service);
        waitForServiceToBeStable(client, service);
        // upgrade the service
        Component component = service.getComponents().iterator().next();
        service.setState(UPGRADING);
        service.setVersion("v2");
        component.getConfiguration().getEnv().put("key1", "val1");
        client.initiateUpgrade(service);
        // wait for service to be in upgrade state
        waitForServiceToBeInState(client, service, UPGRADING);
        SliderFileSystem fs = new SliderFileSystem(getConf());
        Service fromFs = ServiceApiUtil.loadServiceUpgrade(fs, service.getName(), service.getVersion());
        Assert.assertEquals(service.getName(), fromFs.getName());
        Assert.assertEquals(service.getVersion(), fromFs.getVersion());
        // upgrade containers
        Service liveService = client.getStatus(service.getName());
        client.actionUpgrade(service, liveService.getComponent(component.getName()).getContainers());
        waitForAllCompToBeReady(client, service);
        // finalize the upgrade
        client.actionStart(service.getName());
        waitForServiceToBeStable(client, service);
        Service active = client.getStatus(service.getName());
        Assert.assertEquals("component not stable", STABLE, active.getComponent(component.getName()).getState());
        Assert.assertEquals("comp does not have new env", "val1", active.getComponent(component.getName()).getConfiguration().getEnv("key1"));
        TestYarnNativeServices.LOG.info("Stop/destroy service {}", service);
        client.actionStop(service.getName(), true);
        client.actionDestroy(service.getName());
    }

    @Test(timeout = 200000)
    public void testExpressUpgrade() throws Exception {
        setupInternal(ServiceTestUtils.NUM_NMS);
        getConf().setBoolean(YARN_SERVICE_UPGRADE_ENABLED, true);
        ServiceClient client = ServiceTestUtils.createClient(getConf());
        Service service = ServiceTestUtils.createExampleApplication();
        client.actionCreate(service);
        waitForServiceToBeStable(client, service);
        // upgrade the service
        Component component = service.getComponents().iterator().next();
        service.setState(EXPRESS_UPGRADING);
        service.setVersion("v2");
        component.getConfiguration().getEnv().put("key1", "val1");
        Component component2 = service.getComponent("compb");
        component2.getConfiguration().getEnv().put("key2", "val2");
        client.actionUpgradeExpress(service);
        // wait for upgrade to complete
        waitForServiceToBeStable(client, service);
        Service active = client.getStatus(service.getName());
        Assert.assertEquals("version mismatch", service.getVersion(), active.getVersion());
        Assert.assertEquals("component not stable", STABLE, active.getComponent(component.getName()).getState());
        Assert.assertEquals("compa does not have new env", "val1", active.getComponent(component.getName()).getConfiguration().getEnv("key1"));
        Assert.assertEquals("compb does not have new env", "val2", active.getComponent(component2.getName()).getConfiguration().getEnv("key2"));
        TestYarnNativeServices.LOG.info("Stop/destroy service {}", service);
        client.actionStop(service.getName(), true);
        client.actionDestroy(service.getName());
    }

    @Test(timeout = 200000)
    public void testCancelUpgrade() throws Exception {
        setupInternal(ServiceTestUtils.NUM_NMS);
        getConf().setBoolean(YARN_SERVICE_UPGRADE_ENABLED, true);
        ServiceClient client = ServiceTestUtils.createClient(getConf());
        Service service = ServiceTestUtils.createExampleApplication();
        Component component = service.getComponents().iterator().next();
        component.getConfiguration().getEnv().put("key1", "val0");
        client.actionCreate(service);
        waitForServiceToBeStable(client, service);
        // upgrade the service
        service.setState(UPGRADING);
        service.setVersion("v2");
        component.getConfiguration().getEnv().put("key1", "val1");
        client.initiateUpgrade(service);
        // wait for service to be in upgrade state
        waitForServiceToBeInState(client, service, UPGRADING);
        // upgrade 1 container
        Service liveService = client.getStatus(service.getName());
        Container container = liveService.getComponent(component.getName()).getContainers().iterator().next();
        client.actionUpgrade(service, Lists.newArrayList(container));
        Thread.sleep(500);
        // cancel the upgrade
        client.actionCancelUpgrade(service.getName());
        waitForServiceToBeStable(client, service);
        Service active = client.getStatus(service.getName());
        Assert.assertEquals("component not stable", STABLE, active.getComponent(component.getName()).getState());
        Assert.assertEquals("comp does not have new env", "val0", active.getComponent(component.getName()).getConfiguration().getEnv("key1"));
        TestYarnNativeServices.LOG.info("Stop/destroy service {}", service);
        client.actionStop(service.getName(), true);
        client.actionDestroy(service.getName());
    }

    // Test to verify ANTI_AFFINITY placement policy
    // 1. Start mini cluster
    // with 3 NMs and scheduler placement-constraint handler
    // 2. Create an example service with 3 containers
    // 3. Verify no more than 1 container comes up in each of the 3 NMs
    // 4. Flex the component to 4 containers
    // 5. Verify that the 4th container does not even get allocated since there
    // are only 3 NMs
    @Test(timeout = 200000)
    public void testCreateServiceWithPlacementPolicy() throws Exception {
        // We need to enable scheduler placement-constraint at the cluster level to
        // let apps use placement policies.
        YarnConfiguration conf = new YarnConfiguration();
        conf.set(RM_PLACEMENT_CONSTRAINTS_HANDLER, SCHEDULER_RM_PLACEMENT_CONSTRAINTS_HANDLER);
        setConf(conf);
        setupInternal(3);
        ServiceClient client = ServiceTestUtils.createClient(getConf());
        Service exampleApp = new Service();
        exampleApp.setName("example-app");
        exampleApp.setVersion("v1");
        Component comp = ServiceTestUtils.createComponent("compa", 3L, "sleep 1000");
        PlacementPolicy pp = new PlacementPolicy();
        PlacementConstraint pc = new PlacementConstraint();
        pc.setName("CA1");
        pc.setTargetTags(Collections.singletonList("compa"));
        pc.setScope(NODE);
        pc.setType(ANTI_AFFINITY);
        pp.setConstraints(Collections.singletonList(pc));
        comp.setPlacementPolicy(pp);
        exampleApp.addComponent(comp);
        client.actionCreate(exampleApp);
        waitForServiceToBeStable(client, exampleApp);
        // Check service is stable and all 3 containers are running
        Service service = client.getStatus(exampleApp.getName());
        Component component = service.getComponent("compa");
        Assert.assertEquals("Service state should be STABLE", ServiceState.STABLE, service.getState());
        Assert.assertEquals("3 containers are expected to be running", 3, component.getContainers().size());
        // Prepare a map of non-AM containers for later lookup
        Set<String> nonAMContainerIdSet = new HashSet<>();
        for (Container cont : component.getContainers()) {
            nonAMContainerIdSet.add(cont.getId());
        }
        // Verify that no more than 1 non-AM container came up on each of the 3 NMs
        Set<String> hosts = new HashSet<>();
        ApplicationReport report = client.getYarnClient().getApplicationReport(ApplicationId.fromString(exampleApp.getId()));
        GetContainersRequest req = GetContainersRequest.newInstance(report.getCurrentApplicationAttemptId());
        ResourceManager rm = getYarnCluster().getResourceManager();
        for (ContainerReport contReport : rm.getClientRMService().getContainers(req).getContainerList()) {
            if (!(nonAMContainerIdSet.contains(contReport.getContainerId().toString()))) {
                continue;
            }
            if (hosts.contains(contReport.getNodeHttpAddress())) {
                Assert.fail((("Container " + (contReport.getContainerId())) + " came up in the same host as another container."));
            } else {
                hosts.add(contReport.getNodeHttpAddress());
            }
        }
        // Flex compa up to 5, which is more containers than the no of NMs
        Map<String, Long> compCounts = new HashMap<>();
        compCounts.put("compa", 5L);
        exampleApp.getComponent("compa").setNumberOfContainers(5L);
        client.flexByRestService(exampleApp.getName(), compCounts);
        try {
            // 10 secs is enough for the container to be started. The down side of
            // this test is that it has to wait that long. Setting a higher wait time
            // will add to the total time taken by tests to run.
            waitForServiceToBeStable(client, exampleApp, 10000);
            Assert.fail(("Service should not be in a stable state. It should throw " + "a timeout exception."));
        } catch (Exception e) {
            // Check that service state is not STABLE and only 3 containers are
            // running and the fourth one should not get allocated.
            service = client.getStatus(exampleApp.getName());
            component = service.getComponent("compa");
            Assert.assertNotEquals("Service state should not be STABLE", ServiceState.STABLE, service.getState());
            Assert.assertEquals("Component state should be FLEXING", FLEXING, component.getState());
            Assert.assertEquals("3 containers are expected to be running", 3, component.getContainers().size());
        }
        // Flex compa down to 4 now, which is still more containers than the no of
        // NMs. This tests the usecase that flex down does not kill any of the
        // currently running containers since the required number of containers are
        // still higher than the currently running number of containers. However,
        // component state will still be FLEXING and service state not STABLE.
        compCounts = new HashMap<>();
        compCounts.put("compa", 4L);
        exampleApp.getComponent("compa").setNumberOfContainers(4L);
        client.flexByRestService(exampleApp.getName(), compCounts);
        try {
            // 10 secs is enough for the container to be started. The down side of
            // this test is that it has to wait that long. Setting a higher wait time
            // will add to the total time taken by tests to run.
            waitForServiceToBeStable(client, exampleApp, 10000);
            Assert.fail(("Service should not be in a stable state. It should throw " + "a timeout exception."));
        } catch (Exception e) {
            // Check that service state is not STABLE and only 3 containers are
            // running and the fourth one should not get allocated.
            service = client.getStatus(exampleApp.getName());
            component = service.getComponent("compa");
            Assert.assertNotEquals("Service state should not be STABLE", ServiceState.STABLE, service.getState());
            Assert.assertEquals("Component state should be FLEXING", FLEXING, component.getState());
            Assert.assertEquals("3 containers are expected to be running", 3, component.getContainers().size());
        }
        // Finally flex compa down to 3, which is exactly the number of containers
        // currently running. This will bring the component and service states to
        // STABLE.
        compCounts = new HashMap<>();
        compCounts.put("compa", 3L);
        exampleApp.getComponent("compa").setNumberOfContainers(3L);
        client.flexByRestService(exampleApp.getName(), compCounts);
        waitForServiceToBeStable(client, exampleApp);
        TestYarnNativeServices.LOG.info("Stop/destroy service {}", exampleApp);
        client.actionStop(exampleApp.getName(), true);
        client.actionDestroy(exampleApp.getName());
    }

    @Test(timeout = 200000)
    public void testAMSigtermDoesNotKillApplication() throws Exception {
        runAMSignalTest(GRACEFUL_SHUTDOWN);
    }

    @Test(timeout = 200000)
    public void testAMSigkillDoesNotKillApplication() throws Exception {
        runAMSignalTest(FORCEFUL_SHUTDOWN);
    }

    // Test to verify component health threshold monitor. It uses anti-affinity
    // placement policy to make it easier to simulate container failure by
    // allocating more containers than the no of NMs.
    // 1. Start mini cluster with 3 NMs and scheduler placement-constraint handler
    // 2. Create an example service of 3 containers with anti-affinity placement
    // policy and health threshold = 65%, window = 3 secs, init-delay = 0 secs,
    // poll-frequency = 1 secs
    // 3. Flex the component to 4 containers. This makes health = 75%, so based on
    // threshold the service will continue to run beyond the window of 3 secs.
    // 4. Flex the component to 5 containers. This makes health = 60%, so based on
    // threshold the service will be stopped after the window of 3 secs.
    @Test(timeout = 200000)
    public void testComponentHealthThresholdMonitor() throws Exception {
        // We need to enable scheduler placement-constraint at the cluster level to
        // let apps use placement policies.
        YarnConfiguration conf = new YarnConfiguration();
        conf.set(RM_PLACEMENT_CONSTRAINTS_HANDLER, SCHEDULER_RM_PLACEMENT_CONSTRAINTS_HANDLER);
        setConf(conf);
        setupInternal(3);
        ServiceClient client = ServiceTestUtils.createClient(getConf());
        Service exampleApp = new Service();
        exampleApp.setName("example-app");
        exampleApp.setVersion("v1");
        Component comp = ServiceTestUtils.createComponent("compa", 3L, "sleep 1000");
        PlacementPolicy pp = new PlacementPolicy();
        PlacementConstraint pc = new PlacementConstraint();
        pc.setName("CA1");
        pc.setTargetTags(Collections.singletonList("compa"));
        pc.setScope(NODE);
        pc.setType(ANTI_AFFINITY);
        pp.setConstraints(Collections.singletonList(pc));
        comp.setPlacementPolicy(pp);
        Configuration config = new Configuration();
        config.setProperty(CONTAINER_HEALTH_THRESHOLD_PERCENT, "65");
        config.setProperty(CONTAINER_HEALTH_THRESHOLD_WINDOW_SEC, "3");
        config.setProperty(CONTAINER_HEALTH_THRESHOLD_INIT_DELAY_SEC, "0");
        config.setProperty(CONTAINER_HEALTH_THRESHOLD_POLL_FREQUENCY_SEC, "1");
        config.setProperty(DEFAULT_READINESS_CHECK_ENABLED, "false");
        comp.setConfiguration(config);
        exampleApp.addComponent(comp);
        // Make sure AM does not come up after service is killed for this test
        Configuration serviceConfig = new Configuration();
        serviceConfig.setProperty(AM_RESTART_MAX, "1");
        exampleApp.setConfiguration(serviceConfig);
        client.actionCreate(exampleApp);
        waitForServiceToBeStable(client, exampleApp);
        // Check service is stable and all 3 containers are running
        Service service = client.getStatus(exampleApp.getName());
        Component component = service.getComponent("compa");
        Assert.assertEquals("Service state should be STABLE", ServiceState.STABLE, service.getState());
        Assert.assertEquals("3 containers are expected to be running", 3, component.getContainers().size());
        // Flex compa up to 4 - will make health 75% (3 out of 4 running), but still
        // above threshold of 65%, so service will continue to run.
        Map<String, Long> compCounts = new HashMap<>();
        compCounts.put("compa", 4L);
        exampleApp.getComponent("compa").setNumberOfContainers(4L);
        client.flexByRestService(exampleApp.getName(), compCounts);
        try {
            // Wait for 6 secs (window 3 secs + 1 for next poll + 2 for buffer). Since
            // the service will never go to stable state (because of anti-affinity the
            // 4th container will never be allocated) it will timeout. However, after
            // the timeout the service should continue to run since health is 75%
            // which is above the threshold of 65%.
            waitForServiceToBeStable(client, exampleApp, 6000);
            Assert.fail(("Service should not be in a stable state. It should throw " + "a timeout exception."));
        } catch (Exception e) {
            // Check that service state is STARTED and only 3 containers are running
            service = client.getStatus(exampleApp.getName());
            component = service.getComponent("compa");
            Assert.assertEquals("Service state should be STARTED", STARTED, service.getState());
            Assert.assertEquals("Component state should be FLEXING", FLEXING, component.getState());
            Assert.assertEquals("3 containers are expected to be running", 3, component.getContainers().size());
        }
        // Flex compa up to 5 - will make health 60% (3 out of 5 running), so
        // service will stop since it is below threshold of 65%.
        compCounts.put("compa", 5L);
        exampleApp.getComponent("compa").setNumberOfContainers(5L);
        client.flexByRestService(exampleApp.getName(), compCounts);
        try {
            // Wait for 14 secs (window 3 secs + 1 for next poll + 2 for buffer + 5
            // secs of service wait before shutting down + 3 secs app cleanup so that
            // API returns that service is in FAILED state). Note, because of
            // anti-affinity the 4th and 5th container will never be allocated.
            waitForServiceToBeInState(client, exampleApp, FAILED, 14000);
        } catch (Exception e) {
            Assert.fail("Should not have thrown exception");
        }
        TestYarnNativeServices.LOG.info("Destroy service {}", exampleApp);
        client.actionDestroy(exampleApp.getName());
    }
}

