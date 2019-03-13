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
package org.apache.hadoop.yarn.server.applicationhistoryservice;


import ApplicationHistoryManagerOnTimelineStore.UNAVAILABLE;
import AuthMethod.SIMPLE;
import ContainerState.COMPLETE;
import FinalApplicationStatus.UNDEFINED;
import YarnApplicationState.FINISHED;
import YarnConfiguration.YARN_ACL_ENABLE;
import YarnConfiguration.YARN_ADMIN_ACL;
import java.security.PrivilegedExceptionAction;
import java.util.Collection;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.authorize.AuthorizationException;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptReport;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.ApplicationResourceUsageReport;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerReport;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.timeline.TimelineStore;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class TestApplicationHistoryManagerOnTimelineStore {
    private static final int SCALE = 5;

    private static TimelineStore store;

    private ApplicationHistoryManagerOnTimelineStore historyManager;

    private UserGroupInformation callerUGI;

    private Configuration conf;

    public TestApplicationHistoryManagerOnTimelineStore(String caller) {
        conf = new YarnConfiguration();
        if (!(caller.equals(""))) {
            callerUGI = UserGroupInformation.createRemoteUser(caller, SIMPLE);
            conf.setBoolean(YARN_ACL_ENABLE, true);
            conf.set(YARN_ADMIN_ACL, "admin");
        }
    }

    @Test
    public void testGetApplicationReport() throws Exception {
        for (int i = 1; i <= 3; ++i) {
            final ApplicationId appId = ApplicationId.newInstance(0, i);
            ApplicationReport app;
            if ((callerUGI) == null) {
                app = historyManager.getApplication(appId);
            } else {
                app = callerUGI.doAs(new PrivilegedExceptionAction<ApplicationReport>() {
                    @Override
                    public ApplicationReport run() throws Exception {
                        return historyManager.getApplication(appId);
                    }
                });
            }
            Assert.assertNotNull(app);
            Assert.assertEquals(appId, app.getApplicationId());
            Assert.assertEquals("test app", app.getName());
            Assert.assertEquals("test app type", app.getApplicationType());
            Assert.assertEquals("user1", app.getUser());
            if (i == 2) {
                // Change event is fired only in case of app with ID 2, hence verify
                // with updated changes. And make sure last updated change is accepted.
                Assert.assertEquals("changed queue1", app.getQueue());
                Assert.assertEquals(Priority.newInstance(6), app.getPriority());
            } else {
                Assert.assertEquals("test queue", app.getQueue());
                Assert.assertEquals(Priority.newInstance(0), app.getPriority());
            }
            Assert.assertEquals((((Integer.MAX_VALUE) + 2L) + (app.getApplicationId().getId())), app.getStartTime());
            Assert.assertEquals(((Integer.MAX_VALUE) + 1L), app.getSubmitTime());
            Assert.assertEquals((((Integer.MAX_VALUE) + 3L) + (+(app.getApplicationId().getId()))), app.getFinishTime());
            Assert.assertTrue(((Math.abs(((app.getProgress()) - 1.0F))) < 1.0E-4));
            Assert.assertEquals(2, app.getApplicationTags().size());
            Assert.assertTrue(app.getApplicationTags().contains("Test_APP_TAGS_1"));
            Assert.assertTrue(app.getApplicationTags().contains("Test_APP_TAGS_2"));
            // App 2 doesn't have the ACLs, such that the default ACLs " " will be used.
            // Nobody except admin and owner has access to the details of the app.
            if ((((i != 2) && ((callerUGI) != null)) && (callerUGI.getShortUserName().equals("user3"))) || (((i == 2) && ((callerUGI) != null)) && ((callerUGI.getShortUserName().equals("user2")) || (callerUGI.getShortUserName().equals("user3"))))) {
                Assert.assertEquals(ApplicationAttemptId.newInstance(appId, (-1)), app.getCurrentApplicationAttemptId());
                Assert.assertEquals(UNAVAILABLE, app.getHost());
                Assert.assertEquals((-1), app.getRpcPort());
                Assert.assertEquals(UNAVAILABLE, app.getTrackingUrl());
                Assert.assertEquals(UNAVAILABLE, app.getOriginalTrackingUrl());
                Assert.assertEquals("", app.getDiagnostics());
            } else {
                Assert.assertEquals(ApplicationAttemptId.newInstance(appId, 1), app.getCurrentApplicationAttemptId());
                Assert.assertEquals("test host", app.getHost());
                Assert.assertEquals(100, app.getRpcPort());
                Assert.assertEquals("test tracking url", app.getTrackingUrl());
                Assert.assertEquals("test original tracking url", app.getOriginalTrackingUrl());
                Assert.assertEquals("test diagnostics info", app.getDiagnostics());
            }
            ApplicationResourceUsageReport applicationResourceUsageReport = app.getApplicationResourceUsageReport();
            Assert.assertEquals(123, applicationResourceUsageReport.getMemorySeconds());
            Assert.assertEquals(345, applicationResourceUsageReport.getVcoreSeconds());
            long expectedPreemptMemSecs = 456;
            long expectedPreemptVcoreSecs = 789;
            if (i == 3) {
                expectedPreemptMemSecs = 0;
                expectedPreemptVcoreSecs = 0;
            }
            Assert.assertEquals(expectedPreemptMemSecs, applicationResourceUsageReport.getPreemptedMemorySeconds());
            Assert.assertEquals(expectedPreemptVcoreSecs, applicationResourceUsageReport.getPreemptedVcoreSeconds());
            Assert.assertEquals(UNDEFINED, app.getFinalApplicationStatus());
            Assert.assertEquals(FINISHED, app.getYarnApplicationState());
        }
    }

    @Test
    public void testGetApplicationReportWithNotAttempt() throws Exception {
        final ApplicationId appId = ApplicationId.newInstance(0, ((TestApplicationHistoryManagerOnTimelineStore.SCALE) + 1));
        ApplicationReport app;
        if ((callerUGI) == null) {
            app = historyManager.getApplication(appId);
        } else {
            app = callerUGI.doAs(new PrivilegedExceptionAction<ApplicationReport>() {
                @Override
                public ApplicationReport run() throws Exception {
                    return historyManager.getApplication(appId);
                }
            });
        }
        Assert.assertNotNull(app);
        Assert.assertEquals(appId, app.getApplicationId());
        Assert.assertEquals(ApplicationAttemptId.newInstance(appId, (-1)), app.getCurrentApplicationAttemptId());
    }

    @Test
    public void testGetApplicationAttemptReport() throws Exception {
        final ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(ApplicationId.newInstance(0, 1), 1);
        ApplicationAttemptReport appAttempt;
        if ((callerUGI) == null) {
            appAttempt = historyManager.getApplicationAttempt(appAttemptId);
        } else {
            try {
                appAttempt = callerUGI.doAs(new PrivilegedExceptionAction<ApplicationAttemptReport>() {
                    @Override
                    public ApplicationAttemptReport run() throws Exception {
                        return historyManager.getApplicationAttempt(appAttemptId);
                    }
                });
                if (((callerUGI) != null) && (callerUGI.getShortUserName().equals("user3"))) {
                    // The exception is expected
                    Assert.fail();
                }
            } catch (AuthorizationException e) {
                if (((callerUGI) != null) && (callerUGI.getShortUserName().equals("user3"))) {
                    // The exception is expected
                    return;
                }
                throw e;
            }
        }
        Assert.assertNotNull(appAttempt);
        Assert.assertEquals(appAttemptId, appAttempt.getApplicationAttemptId());
        Assert.assertEquals(ContainerId.newContainerId(appAttemptId, 1), appAttempt.getAMContainerId());
        Assert.assertEquals("test host", appAttempt.getHost());
        Assert.assertEquals(100, appAttempt.getRpcPort());
        Assert.assertEquals("test tracking url", appAttempt.getTrackingUrl());
        Assert.assertEquals("test original tracking url", appAttempt.getOriginalTrackingUrl());
        Assert.assertEquals("test diagnostics info", appAttempt.getDiagnostics());
        Assert.assertEquals(YarnApplicationAttemptState.FINISHED, appAttempt.getYarnApplicationAttemptState());
    }

    @Test
    public void testGetContainerReport() throws Exception {
        final ContainerId containerId = ContainerId.newContainerId(ApplicationAttemptId.newInstance(ApplicationId.newInstance(0, 1), 1), 1);
        ContainerReport container;
        if ((callerUGI) == null) {
            container = historyManager.getContainer(containerId);
        } else {
            try {
                container = callerUGI.doAs(new PrivilegedExceptionAction<ContainerReport>() {
                    @Override
                    public ContainerReport run() throws Exception {
                        return historyManager.getContainer(containerId);
                    }
                });
                if (((callerUGI) != null) && (callerUGI.getShortUserName().equals("user3"))) {
                    // The exception is expected
                    Assert.fail();
                }
            } catch (AuthorizationException e) {
                if (((callerUGI) != null) && (callerUGI.getShortUserName().equals("user3"))) {
                    // The exception is expected
                    return;
                }
                throw e;
            }
        }
        Assert.assertNotNull(container);
        Assert.assertEquals(((Integer.MAX_VALUE) + 1L), container.getCreationTime());
        Assert.assertEquals(((Integer.MAX_VALUE) + 2L), container.getFinishTime());
        Assert.assertEquals(Resource.newInstance((-1), (-1)), container.getAllocatedResource());
        Assert.assertEquals(NodeId.newInstance("test host", 100), container.getAssignedNode());
        Assert.assertEquals(Priority.UNDEFINED, container.getPriority());
        Assert.assertEquals("test diagnostics info", container.getDiagnosticsInfo());
        Assert.assertEquals(COMPLETE, container.getContainerState());
        Assert.assertEquals((-1), container.getContainerExitStatus());
        Assert.assertEquals(("http://0.0.0.0:8188/applicationhistory/logs/" + ("test host:100/container_0_0001_01_000001/" + "container_0_0001_01_000001/user1")), container.getLogUrl());
    }

    @Test
    public void testGetApplications() throws Exception {
        Collection<ApplicationReport> apps = historyManager.getApplications(Long.MAX_VALUE, 0L, Long.MAX_VALUE).values();
        Assert.assertNotNull(apps);
        Assert.assertEquals(((TestApplicationHistoryManagerOnTimelineStore.SCALE) + 2), apps.size());
        ApplicationId ignoredAppId = ApplicationId.newInstance(0, ((TestApplicationHistoryManagerOnTimelineStore.SCALE) + 2));
        for (ApplicationReport app : apps) {
            Assert.assertNotEquals(ignoredAppId, app.getApplicationId());
        }
        // Get apps by given appStartedTime period
        apps = historyManager.getApplications(Long.MAX_VALUE, 2147483653L, Long.MAX_VALUE).values();
        Assert.assertNotNull(apps);
        Assert.assertEquals(2, apps.size());
    }

    @Test
    public void testGetApplicationAttempts() throws Exception {
        final ApplicationId appId = ApplicationId.newInstance(0, 1);
        Collection<ApplicationAttemptReport> appAttempts;
        if ((callerUGI) == null) {
            appAttempts = historyManager.getApplicationAttempts(appId).values();
        } else {
            try {
                appAttempts = callerUGI.doAs(new PrivilegedExceptionAction<Collection<ApplicationAttemptReport>>() {
                    @Override
                    public Collection<ApplicationAttemptReport> run() throws Exception {
                        return historyManager.getApplicationAttempts(appId).values();
                    }
                });
                if (((callerUGI) != null) && (callerUGI.getShortUserName().equals("user3"))) {
                    // The exception is expected
                    Assert.fail();
                }
            } catch (AuthorizationException e) {
                if (((callerUGI) != null) && (callerUGI.getShortUserName().equals("user3"))) {
                    // The exception is expected
                    return;
                }
                throw e;
            }
        }
        Assert.assertNotNull(appAttempts);
        Assert.assertEquals(TestApplicationHistoryManagerOnTimelineStore.SCALE, appAttempts.size());
    }

    @Test
    public void testGetContainers() throws Exception {
        final ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(ApplicationId.newInstance(0, 1), 1);
        Collection<ContainerReport> containers;
        if ((callerUGI) == null) {
            containers = historyManager.getContainers(appAttemptId).values();
        } else {
            try {
                containers = callerUGI.doAs(new PrivilegedExceptionAction<Collection<ContainerReport>>() {
                    @Override
                    public Collection<ContainerReport> run() throws Exception {
                        return historyManager.getContainers(appAttemptId).values();
                    }
                });
                if (((callerUGI) != null) && (callerUGI.getShortUserName().equals("user3"))) {
                    // The exception is expected
                    Assert.fail();
                }
            } catch (AuthorizationException e) {
                if (((callerUGI) != null) && (callerUGI.getShortUserName().equals("user3"))) {
                    // The exception is expected
                    return;
                }
                throw e;
            }
        }
        Assert.assertNotNull(containers);
        Assert.assertEquals(TestApplicationHistoryManagerOnTimelineStore.SCALE, containers.size());
    }

    @Test
    public void testGetAMContainer() throws Exception {
        final ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(ApplicationId.newInstance(0, 1), 1);
        ContainerReport container;
        if ((callerUGI) == null) {
            container = historyManager.getAMContainer(appAttemptId);
        } else {
            try {
                container = callerUGI.doAs(new PrivilegedExceptionAction<ContainerReport>() {
                    @Override
                    public ContainerReport run() throws Exception {
                        return historyManager.getAMContainer(appAttemptId);
                    }
                });
                if (((callerUGI) != null) && (callerUGI.getShortUserName().equals("user3"))) {
                    // The exception is expected
                    Assert.fail();
                }
            } catch (AuthorizationException e) {
                if (((callerUGI) != null) && (callerUGI.getShortUserName().equals("user3"))) {
                    // The exception is expected
                    return;
                }
                throw e;
            }
        }
        Assert.assertNotNull(container);
        Assert.assertEquals(appAttemptId, container.getContainerId().getApplicationAttemptId());
    }
}

