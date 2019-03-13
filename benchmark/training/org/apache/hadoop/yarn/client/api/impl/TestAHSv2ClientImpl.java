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
package org.apache.hadoop.yarn.client.api.impl;


import YarnApplicationState.FINISHED;
import java.io.IOException;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptReport;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerReport;
import org.apache.hadoop.yarn.client.api.TimelineReaderClient;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * This class is to test class {@link AHSv2ClientImpl).
 */
public class TestAHSv2ClientImpl {
    private AHSv2ClientImpl client;

    private TimelineReaderClient spyTimelineReaderClient;

    @Test
    public void testGetContainerReport() throws IOException, YarnException {
        final ApplicationId appId = ApplicationId.newInstance(0, 1);
        final ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 1);
        final ContainerId containerId = ContainerId.newContainerId(appAttemptId, 1);
        Mockito.when(spyTimelineReaderClient.getContainerEntity(containerId, "ALL", null)).thenReturn(TestAHSv2ClientImpl.createContainerEntity(containerId));
        Mockito.when(spyTimelineReaderClient.getApplicationEntity(appId, "ALL", null)).thenReturn(TestAHSv2ClientImpl.createApplicationTimelineEntity(appId, true, false));
        ContainerReport report = client.getContainerReport(containerId);
        Assert.assertEquals(report.getContainerId(), containerId);
        Assert.assertEquals(report.getAssignedNode().getHost(), "test host");
        Assert.assertEquals(report.getAssignedNode().getPort(), 100);
        Assert.assertEquals(report.getAllocatedResource().getVirtualCores(), 8);
        Assert.assertEquals(report.getCreationTime(), 123456);
        Assert.assertEquals(report.getLogUrl(), ("https://localhost:8188/ahs/logs/test host:100/" + "container_0_0001_01_000001/container_0_0001_01_000001/user1"));
    }

    @Test
    public void testGetAppAttemptReport() throws IOException, YarnException {
        final ApplicationId appId = ApplicationId.newInstance(0, 1);
        final ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 1);
        Mockito.when(spyTimelineReaderClient.getApplicationAttemptEntity(appAttemptId, "ALL", null)).thenReturn(TestAHSv2ClientImpl.createAppAttemptTimelineEntity(appAttemptId));
        ApplicationAttemptReport report = client.getApplicationAttemptReport(appAttemptId);
        Assert.assertEquals(report.getApplicationAttemptId(), appAttemptId);
        Assert.assertEquals(report.getFinishTime(), ((Integer.MAX_VALUE) + 2L));
        Assert.assertEquals(report.getOriginalTrackingUrl(), "test original tracking url");
    }

    @Test
    public void testGetAppReport() throws IOException, YarnException {
        final ApplicationId appId = ApplicationId.newInstance(0, 1);
        Mockito.when(spyTimelineReaderClient.getApplicationEntity(appId, "ALL", null)).thenReturn(TestAHSv2ClientImpl.createApplicationTimelineEntity(appId, false, false));
        ApplicationReport report = client.getApplicationReport(appId);
        Assert.assertEquals(report.getApplicationId(), appId);
        Assert.assertEquals(report.getAppNodeLabelExpression(), "test_node_label");
        Assert.assertTrue(report.getApplicationTags().contains("Test_APP_TAGS_1"));
        Assert.assertEquals(report.getYarnApplicationState(), FINISHED);
    }
}

