/**
 * -
 * -\-\-
 * Helios Services
 * --
 * Copyright (C) 2016 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.helios;


import Goal.START;
import Goal.STOP;
import com.google.common.collect.ImmutableList;
import com.spotify.helios.common.descriptors.Deployment;
import com.spotify.helios.common.descriptors.DeploymentGroup;
import com.spotify.helios.common.descriptors.HostSelector;
import com.spotify.helios.common.descriptors.Job;
import com.spotify.helios.common.descriptors.JobId;
import com.spotify.helios.common.descriptors.RolloutOptions;
import com.spotify.helios.master.DeploymentGroupDoesNotExistException;
import com.spotify.helios.master.DeploymentGroupExistsException;
import com.spotify.helios.master.HostNotFoundException;
import com.spotify.helios.master.JobDoesNotExistException;
import com.spotify.helios.master.JobNotDeployedException;
import com.spotify.helios.master.JobStillDeployedException;
import com.spotify.helios.master.ZooKeeperMasterModel;
import com.spotify.helios.servicescommon.EventSender;
import java.util.List;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ZooKeeperMasterModelIntegrationTest {
    private static final String IMAGE = "IMAGE";

    private static final String COMMAND = "COMMAND";

    private static final String JOB_NAME = "JOB_NAME";

    private static final String HOST = "HOST";

    private static final Job JOB = Job.newBuilder().setCommand(ImmutableList.of(ZooKeeperMasterModelIntegrationTest.COMMAND)).setImage(ZooKeeperMasterModelIntegrationTest.IMAGE).setName(ZooKeeperMasterModelIntegrationTest.JOB_NAME).setVersion("VERSION").build();

    private static final JobId JOB_ID = ZooKeeperMasterModelIntegrationTest.JOB.getId();

    private static final String DEPLOYMENT_GROUP_NAME = "my_group";

    private static final DeploymentGroup DEPLOYMENT_GROUP = DeploymentGroup.newBuilder().setName(ZooKeeperMasterModelIntegrationTest.DEPLOYMENT_GROUP_NAME).setHostSelectors(ImmutableList.of(HostSelector.parse("role=foo"))).setJobId(ZooKeeperMasterModelIntegrationTest.JOB_ID).setRolloutOptions(RolloutOptions.getDefault()).build();

    private ZooKeeperMasterModel model;

    private final EventSender eventSender = Mockito.mock(EventSender.class);

    private final String deploymentGroupEventTopic = "deploymentGroupEventTopic";

    private final ZooKeeperTestManager zk = new ZooKeeperTestingServerManager();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testHostListing() throws Exception {
        final String secondHost = "SECOND";
        MatcherAssert.assertThat(model.listHosts(), Matchers.empty());
        model.registerHost(ZooKeeperMasterModelIntegrationTest.HOST, "foo");
        MatcherAssert.assertThat(model.listHosts(), Matchers.contains(ZooKeeperMasterModelIntegrationTest.HOST));
        model.registerHost(secondHost, "bar");
        MatcherAssert.assertThat(model.listHosts(), Matchers.contains(ZooKeeperMasterModelIntegrationTest.HOST, secondHost));
        model.deregisterHost(ZooKeeperMasterModelIntegrationTest.HOST);
        MatcherAssert.assertThat(model.listHosts(), Matchers.contains(secondHost));
    }

    @Test
    public void testHostListingWithNamePatternFilter() throws Exception {
        // sanity check that no hosts exist
        MatcherAssert.assertThat(model.listHosts(), Matchers.empty());
        final String hostname = "host1";
        model.registerHost(hostname, "foo");
        for (int i = 1; i <= (hostname.length()); i++) {
            MatcherAssert.assertThat(model.listHosts(hostname.substring(0, i)), Matchers.contains(hostname));
        }
        // negative match
        MatcherAssert.assertThat(model.listHosts("host2"), Matchers.is(Matchers.empty()));
        final String secondHost = "host2";
        model.registerHost(secondHost, "bar");
        MatcherAssert.assertThat(model.listHosts("host"), Matchers.contains(hostname, secondHost));
        model.deregisterHost(hostname);
        MatcherAssert.assertThat(model.listHosts(secondHost), Matchers.contains(secondHost));
    }

    @Test
    public void testJobCreation() throws Exception {
        MatcherAssert.assertThat(model.getJobs().entrySet(), Matchers.empty());
        model.addJob(ZooKeeperMasterModelIntegrationTest.JOB);
        Assert.assertEquals(model.getJobs().get(ZooKeeperMasterModelIntegrationTest.JOB_ID), ZooKeeperMasterModelIntegrationTest.JOB);
        Assert.assertEquals(model.getJob(ZooKeeperMasterModelIntegrationTest.JOB_ID), ZooKeeperMasterModelIntegrationTest.JOB);
        final Job secondJob = Job.newBuilder().setCommand(ImmutableList.of(ZooKeeperMasterModelIntegrationTest.COMMAND)).setImage(ZooKeeperMasterModelIntegrationTest.IMAGE).setName(ZooKeeperMasterModelIntegrationTest.JOB_NAME).setVersion("SECOND").build();
        model.addJob(secondJob);
        Assert.assertEquals(model.getJob(secondJob.getId()), secondJob);
        Assert.assertEquals(2, model.getJobs().size());
    }

    @Test
    public void testJobRemove() throws Exception {
        model.addJob(ZooKeeperMasterModelIntegrationTest.JOB);
        model.registerHost(ZooKeeperMasterModelIntegrationTest.HOST, "foo");
        model.deployJob(ZooKeeperMasterModelIntegrationTest.HOST, Deployment.newBuilder().setGoal(START).setJobId(ZooKeeperMasterModelIntegrationTest.JOB_ID).build());
        try {
            model.removeJob(ZooKeeperMasterModelIntegrationTest.JOB_ID);
            Assert.fail("should have thrown an exception");
        } catch (JobStillDeployedException e) {
            Assert.assertTrue(true);
        }
        model.undeployJob(ZooKeeperMasterModelIntegrationTest.HOST, ZooKeeperMasterModelIntegrationTest.JOB_ID);
        Assert.assertNotNull(model.getJobs().get(ZooKeeperMasterModelIntegrationTest.JOB_ID));
        model.removeJob(ZooKeeperMasterModelIntegrationTest.JOB_ID);// should succeed

        Assert.assertNull(model.getJobs().get(ZooKeeperMasterModelIntegrationTest.JOB_ID));
    }

    @Test
    public void testDeploy() throws Exception {
        try {
            model.deployJob(ZooKeeperMasterModelIntegrationTest.HOST, Deployment.newBuilder().setGoal(START).setJobId(ZooKeeperMasterModelIntegrationTest.JOB_ID).build());
            Assert.fail("should throw");
        } catch (JobDoesNotExistException | HostNotFoundException e) {
            Assert.assertTrue(true);
        }
        model.addJob(ZooKeeperMasterModelIntegrationTest.JOB);
        try {
            model.deployJob(ZooKeeperMasterModelIntegrationTest.HOST, Deployment.newBuilder().setGoal(START).setJobId(ZooKeeperMasterModelIntegrationTest.JOB_ID).build());
            Assert.fail("should throw");
        } catch (HostNotFoundException e) {
            Assert.assertTrue(true);
        }
        model.registerHost(ZooKeeperMasterModelIntegrationTest.HOST, "foo");
        model.deployJob(ZooKeeperMasterModelIntegrationTest.HOST, Deployment.newBuilder().setGoal(START).setJobId(ZooKeeperMasterModelIntegrationTest.JOB_ID).build());
        model.undeployJob(ZooKeeperMasterModelIntegrationTest.HOST, ZooKeeperMasterModelIntegrationTest.JOB_ID);
        model.removeJob(ZooKeeperMasterModelIntegrationTest.JOB_ID);
        try {
            model.deployJob(ZooKeeperMasterModelIntegrationTest.HOST, Deployment.newBuilder().setGoal(START).setJobId(ZooKeeperMasterModelIntegrationTest.JOB_ID).build());
            Assert.fail("should throw");
        } catch (JobDoesNotExistException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testHostRegistration() throws Exception {
        model.registerHost(ZooKeeperMasterModelIntegrationTest.HOST, "foo");
        final List<String> hosts1 = model.listHosts();
        MatcherAssert.assertThat(hosts1, Matchers.hasItem(ZooKeeperMasterModelIntegrationTest.HOST));
        model.deregisterHost(ZooKeeperMasterModelIntegrationTest.HOST);
        final List<String> hosts2 = model.listHosts();
        Assert.assertEquals(0, hosts2.size());
    }

    @Test
    public void testUpdateDeploy() throws Exception {
        try {
            stopJob(model, ZooKeeperMasterModelIntegrationTest.JOB);
            Assert.fail("should have thrown JobNotDeployedException");
        } catch (JobNotDeployedException e) {
            Assert.assertTrue(true);
        } catch (Exception e) {
            Assert.fail(("Should have thrown an JobNotDeployedException, got " + (e.getClass())));
        }
        model.addJob(ZooKeeperMasterModelIntegrationTest.JOB);
        try {
            stopJob(model, ZooKeeperMasterModelIntegrationTest.JOB);
            Assert.fail("should have thrown exception");
        } catch (HostNotFoundException e) {
            Assert.assertTrue(true);
        } catch (Exception e) {
            Assert.fail("Should have thrown an HostNotFoundException");
        }
        model.registerHost(ZooKeeperMasterModelIntegrationTest.HOST, "foo");
        final List<String> hosts = model.listHosts();
        MatcherAssert.assertThat(hosts, Matchers.hasItem(ZooKeeperMasterModelIntegrationTest.HOST));
        try {
            stopJob(model, ZooKeeperMasterModelIntegrationTest.JOB);
            Assert.fail("should have thrown exception");
        } catch (JobNotDeployedException e) {
            Assert.assertTrue(true);
        } catch (Exception e) {
            Assert.fail("Should have thrown an JobNotDeployedException");
        }
        model.deployJob(ZooKeeperMasterModelIntegrationTest.HOST, Deployment.newBuilder().setGoal(START).setJobId(ZooKeeperMasterModelIntegrationTest.JOB.getId()).build());
        final Map<JobId, Job> jobsOnHost = model.getJobs();
        Assert.assertEquals(1, jobsOnHost.size());
        final Job descriptor = jobsOnHost.get(ZooKeeperMasterModelIntegrationTest.JOB.getId());
        Assert.assertEquals(ZooKeeperMasterModelIntegrationTest.JOB, descriptor);
        stopJob(model, ZooKeeperMasterModelIntegrationTest.JOB);// should succeed this time!

        final Deployment jobCfg = model.getDeployment(ZooKeeperMasterModelIntegrationTest.HOST, ZooKeeperMasterModelIntegrationTest.JOB.getId());
        Assert.assertEquals(STOP, jobCfg.getGoal());
    }

    @Test
    public void testAddDeploymentGroup() throws Exception {
        model.addDeploymentGroup(ZooKeeperMasterModelIntegrationTest.DEPLOYMENT_GROUP);
        Assert.assertEquals(ZooKeeperMasterModelIntegrationTest.DEPLOYMENT_GROUP, model.getDeploymentGroup(ZooKeeperMasterModelIntegrationTest.DEPLOYMENT_GROUP_NAME));
    }

    @Test
    public void testAddExistingDeploymentGroup() throws Exception {
        model.addDeploymentGroup(ZooKeeperMasterModelIntegrationTest.DEPLOYMENT_GROUP);
        exception.expect(DeploymentGroupExistsException.class);
        model.addDeploymentGroup(ZooKeeperMasterModelIntegrationTest.DEPLOYMENT_GROUP);
    }

    @Test
    public void testRemoveDeploymentGroup() throws Exception {
        model.addDeploymentGroup(ZooKeeperMasterModelIntegrationTest.DEPLOYMENT_GROUP);
        model.removeDeploymentGroup(ZooKeeperMasterModelIntegrationTest.DEPLOYMENT_GROUP_NAME);
        exception.expect(DeploymentGroupDoesNotExistException.class);
        model.getDeploymentGroup(ZooKeeperMasterModelIntegrationTest.DEPLOYMENT_GROUP_NAME);
    }

    @Test
    public void testRemoveNonExistingDeploymentGroup() throws Exception {
        model.removeDeploymentGroup(ZooKeeperMasterModelIntegrationTest.DEPLOYMENT_GROUP_NAME);
    }

    @Test
    public void testUpdateDeploymentGroupHostsSendsEvent() throws Exception {
        model.addDeploymentGroup(ZooKeeperMasterModelIntegrationTest.DEPLOYMENT_GROUP);
        model.updateDeploymentGroupHosts(ZooKeeperMasterModelIntegrationTest.DEPLOYMENT_GROUP_NAME, ImmutableList.of(ZooKeeperMasterModelIntegrationTest.HOST));
        Mockito.verify(eventSender, Mockito.times(2)).send(ArgumentMatchers.eq(deploymentGroupEventTopic), ArgumentMatchers.any(byte[].class));
        Mockito.verifyNoMoreInteractions(eventSender);
    }

    @Test
    public void testRollingUpdateSendsEvent() throws Exception {
        model.addDeploymentGroup(ZooKeeperMasterModelIntegrationTest.DEPLOYMENT_GROUP);
        model.addJob(ZooKeeperMasterModelIntegrationTest.JOB);
        model.rollingUpdate(ZooKeeperMasterModelIntegrationTest.DEPLOYMENT_GROUP, ZooKeeperMasterModelIntegrationTest.JOB_ID, RolloutOptions.getDefault());
        Mockito.verify(eventSender, Mockito.times(2)).send(ArgumentMatchers.eq(deploymentGroupEventTopic), ArgumentMatchers.any(byte[].class));
        Mockito.verifyNoMoreInteractions(eventSender);
    }
}

