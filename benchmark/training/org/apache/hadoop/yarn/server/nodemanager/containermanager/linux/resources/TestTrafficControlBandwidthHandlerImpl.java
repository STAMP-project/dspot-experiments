/**
 * *
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements. See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership. The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * /
 */
package org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.resources;


import CGroupsHandler.CGroupController.NET_CLS;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.Container;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.privileged.PrivilegedOperationExecutor;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestTrafficControlBandwidthHandlerImpl {
    private static final Logger LOG = LoggerFactory.getLogger(TestTrafficControlBandwidthHandlerImpl.class);

    private static final int ROOT_BANDWIDTH_MBIT = 100;

    private static final int YARN_BANDWIDTH_MBIT = 70;

    private static final int TEST_CLASSID = 100;

    private static final String TEST_CLASSID_STR = "42:100";

    private static final String TEST_CONTAINER_ID_STR = "container_01";

    private static final String TEST_TASKS_FILE = "testTasksFile";

    private PrivilegedOperationExecutor privilegedOperationExecutorMock;

    private CGroupsHandler cGroupsHandlerMock;

    private TrafficController trafficControllerMock;

    private Configuration conf;

    private String tmpPath;

    private String device;

    ContainerId containerIdMock;

    Container containerMock;

    @Test
    public void testBootstrap() {
        TrafficControlBandwidthHandlerImpl handlerImpl = new TrafficControlBandwidthHandlerImpl(privilegedOperationExecutorMock, cGroupsHandlerMock, trafficControllerMock);
        try {
            handlerImpl.bootstrap(conf);
            Mockito.verify(cGroupsHandlerMock).initializeCGroupController(ArgumentMatchers.eq(NET_CLS));
            Mockito.verifyNoMoreInteractions(cGroupsHandlerMock);
            Mockito.verify(trafficControllerMock).bootstrap(ArgumentMatchers.eq(device), ArgumentMatchers.eq(TestTrafficControlBandwidthHandlerImpl.ROOT_BANDWIDTH_MBIT), ArgumentMatchers.eq(TestTrafficControlBandwidthHandlerImpl.YARN_BANDWIDTH_MBIT));
            Mockito.verifyNoMoreInteractions(trafficControllerMock);
        } catch (ResourceHandlerException e) {
            TestTrafficControlBandwidthHandlerImpl.LOG.error(("Unexpected exception: " + e));
            Assert.fail("Caught unexpected ResourceHandlerException!");
        }
    }

    @Test
    public void testLifeCycle() {
        TrafficController trafficControllerSpy = Mockito.spy(new TrafficController(conf, privilegedOperationExecutorMock));
        TrafficControlBandwidthHandlerImpl handlerImpl = new TrafficControlBandwidthHandlerImpl(privilegedOperationExecutorMock, cGroupsHandlerMock, trafficControllerSpy);
        try {
            handlerImpl.bootstrap(conf);
            testPreStart(trafficControllerSpy, handlerImpl);
            testPostComplete(trafficControllerSpy, handlerImpl);
        } catch (ResourceHandlerException e) {
            TestTrafficControlBandwidthHandlerImpl.LOG.error(("Unexpected exception: " + e));
            Assert.fail("Caught unexpected ResourceHandlerException!");
        }
    }
}

