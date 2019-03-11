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


/**
 * Test NetworkPacketTagging Handler.
 */
public class TestNetworkPacketTaggingHandlerImpl {
    private static final Logger LOG = LoggerFactory.getLogger(TestNetworkPacketTaggingHandlerImpl.class);

    private static final String TEST_CLASSID = "0x100001";

    private static final String TEST_CONTAINER_ID_STR = "container_01";

    private static final String TEST_TASKS_FILE = "testTasksFile";

    private NetworkTagMappingManager mockManager;

    private PrivilegedOperationExecutor privilegedOperationExecutorMock;

    private CGroupsHandler cGroupsHandlerMock;

    private Configuration conf;

    private String tmpPath;

    private ContainerId containerIdMock;

    private Container containerMock;

    @Test
    public void testBootstrap() {
        NetworkPacketTaggingHandlerImpl handlerImpl = createNetworkPacketTaggingHandlerImpl();
        try {
            handlerImpl.bootstrap(conf);
            Mockito.verify(cGroupsHandlerMock).initializeCGroupController(ArgumentMatchers.eq(NET_CLS));
            Mockito.verifyNoMoreInteractions(cGroupsHandlerMock);
        } catch (ResourceHandlerException e) {
            TestNetworkPacketTaggingHandlerImpl.LOG.error(("Unexpected exception: " + e));
            Assert.fail("Caught unexpected ResourceHandlerException!");
        }
    }

    @Test
    public void testLifeCycle() {
        NetworkPacketTaggingHandlerImpl handlerImpl = createNetworkPacketTaggingHandlerImpl();
        try {
            handlerImpl.bootstrap(conf);
            testPreStart(handlerImpl);
            testPostComplete(handlerImpl);
        } catch (ResourceHandlerException e) {
            TestNetworkPacketTaggingHandlerImpl.LOG.error(("Unexpected exception: " + e));
            Assert.fail("Caught unexpected ResourceHandlerException!");
        }
    }
}

