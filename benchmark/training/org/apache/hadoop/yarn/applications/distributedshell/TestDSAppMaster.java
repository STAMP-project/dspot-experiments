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
package org.apache.hadoop.yarn.applications.distributedshell;


import AMRMClient.ContainerRequest;
import ApplicationMaster.RMCallbackHandler;
import ContainerExitStatus.ABORTED;
import ContainerExitStatus.SUCCESS;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.client.api.async.AMRMClientAsync;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.server.utils.BuilderUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * A bunch of tests to make sure that the container allocations
 * and releases occur correctly.
 */
public class TestDSAppMaster {
    static class TestAppMaster extends ApplicationMaster {
        private int threadsLaunched = 0;

        public List<String> yarnShellIds = new ArrayList<String>();

        @Override
        protected Thread createLaunchContainerThread(Container allocatedContainer, String shellId) {
            (threadsLaunched)++;
            launchedContainers.add(allocatedContainer.getId());
            yarnShellIds.add(shellId);
            return new Thread();
        }

        void setNumTotalContainers(int numTotalContainers) {
            this.numTotalContainers = numTotalContainers;
        }

        int getAllocatedContainers() {
            return this.numAllocatedContainers.get();
        }

        @Override
        void startTimelineClient(final Configuration conf) throws IOException, InterruptedException, YarnException {
            timelineClient = null;
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testDSAppMasterAllocateHandler() throws Exception {
        TestDSAppMaster.TestAppMaster master = new TestDSAppMaster.TestAppMaster();
        int targetContainers = 2;
        AMRMClientAsync mockClient = Mockito.mock(AMRMClientAsync.class);
        master.setAmRMClient(mockClient);
        master.setNumTotalContainers(targetContainers);
        Mockito.doNothing().when(mockClient).addContainerRequest(ArgumentMatchers.any(ContainerRequest.class));
        ApplicationMaster.RMCallbackHandler handler = getRMCallbackHandler();
        List<Container> containers = new ArrayList<>(1);
        ContainerId id1 = BuilderUtils.newContainerId(1, 1, 1, 1);
        containers.add(generateContainer(id1));
        master.numRequestedContainers.set(targetContainers);
        // first allocate a single container, everything should be fine
        handler.onContainersAllocated(containers);
        Assert.assertEquals("Wrong container allocation count", 1, master.getAllocatedContainers());
        Assert.assertEquals("Incorrect number of threads launched", 1, master.threadsLaunched);
        Assert.assertEquals("Incorrect YARN Shell IDs", Arrays.asList("1"), master.yarnShellIds);
        // now send 3 extra containers
        containers.clear();
        ContainerId id2 = BuilderUtils.newContainerId(1, 1, 1, 2);
        containers.add(generateContainer(id2));
        ContainerId id3 = BuilderUtils.newContainerId(1, 1, 1, 3);
        containers.add(generateContainer(id3));
        ContainerId id4 = BuilderUtils.newContainerId(1, 1, 1, 4);
        containers.add(generateContainer(id4));
        handler.onContainersAllocated(containers);
        Assert.assertEquals("Wrong final container allocation count", 2, master.getAllocatedContainers());
        Assert.assertEquals("Incorrect number of threads launched", 2, master.threadsLaunched);
        Assert.assertEquals("Incorrect YARN Shell IDs", Arrays.asList("1", "2"), master.yarnShellIds);
        // make sure we handle completion events correctly
        List<ContainerStatus> status = new ArrayList<>();
        status.add(generateContainerStatus(id1, SUCCESS));
        status.add(generateContainerStatus(id2, SUCCESS));
        status.add(generateContainerStatus(id3, ABORTED));
        status.add(generateContainerStatus(id4, ABORTED));
        handler.onContainersCompleted(status);
        Assert.assertEquals("Unexpected number of completed containers", targetContainers, getNumCompletedContainers());
        Assert.assertTrue("Master didn't finish containers as expected", getDone());
        // test for events from containers we know nothing about
        // these events should be ignored
        status = new ArrayList();
        ContainerId id5 = BuilderUtils.newContainerId(1, 1, 1, 5);
        status.add(generateContainerStatus(id5, ABORTED));
        Assert.assertEquals("Unexpected number of completed containers", targetContainers, getNumCompletedContainers());
        Assert.assertTrue("Master didn't finish containers as expected", getDone());
        status.add(generateContainerStatus(id5, SUCCESS));
        Assert.assertEquals("Unexpected number of completed containers", targetContainers, getNumCompletedContainers());
        Assert.assertTrue("Master didn't finish containers as expected", getDone());
    }

    @Test
    public void testTimelineClientInDSAppMasterV1() throws Exception {
        runTimelineClientInDSAppMaster(true, false);
    }

    @Test
    public void testTimelineClientInDSAppMasterV2() throws Exception {
        runTimelineClientInDSAppMaster(false, true);
    }

    @Test
    public void testTimelineClientInDSAppMasterV1V2() throws Exception {
        runTimelineClientInDSAppMaster(true, true);
    }

    @Test
    public void testTimelineClientInDSAppMasterDisabled() throws Exception {
        runTimelineClientInDSAppMaster(false, false);
    }
}

