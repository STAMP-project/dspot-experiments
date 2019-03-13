/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.yarn;


import AMRMClient.ContainerRequest;
import ClusterProperties.IGNITE_MEMORY_OVERHEAD_PER_NODE;
import ClusterProperties.IGNITE_MEMORY_PER_NODE;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.util.Progressable;
import org.apache.hadoop.yarn.api.protocolrecords.RegisterApplicationMasterResponse;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.client.api.AMRMClient;
import org.apache.hadoop.yarn.client.api.NMClient;
import org.apache.hadoop.yarn.client.api.async.AMRMClientAsync;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Application master tests.
 */
public class IgniteApplicationMasterSelfTest {
    /**
     *
     */
    private ApplicationMaster appMaster;

    /**
     *
     */
    private ClusterProperties props;

    /**
     *
     */
    private IgniteApplicationMasterSelfTest.RMMock rmMock = new IgniteApplicationMasterSelfTest.RMMock();

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testContainerAllocate() throws Exception {
        appMaster.setRmClient(rmMock);
        appMaster.setNmClient(new IgniteApplicationMasterSelfTest.NMMock());
        props.cpusPerNode(2);
        props.memoryPerNode(1024);
        props.instances(3);
        Thread thread = IgniteApplicationMasterSelfTest.runAppMaster(appMaster);
        List<AMRMClient.ContainerRequest> contRequests = collectRequests(rmMock, 2, 1000);
        IgniteApplicationMasterSelfTest.interruptedThread(thread);
        Assert.assertEquals(3, contRequests.size());
        for (AMRMClient.ContainerRequest req : contRequests) {
            Assert.assertEquals(2, req.getCapability().getVirtualCores());
            Assert.assertEquals(1024, req.getCapability().getMemory());
        }
    }

    /**
     * Tests whether memory overhead is allocated within container memory.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testMemoryOverHeadAllocation() throws Exception {
        appMaster.setRmClient(rmMock);
        appMaster.setNmClient(new IgniteApplicationMasterSelfTest.NMMock());
        props.cpusPerNode(2);
        props.memoryPerNode(1024);
        props.memoryOverHeadPerNode(512);
        props.instances(3);
        Thread thread = IgniteApplicationMasterSelfTest.runAppMaster(appMaster);
        List<AMRMClient.ContainerRequest> contRequests = collectRequests(rmMock, 1, 1000);
        IgniteApplicationMasterSelfTest.interruptedThread(thread);
        Assert.assertEquals(3, contRequests.size());
        for (AMRMClient.ContainerRequest req : contRequests) {
            Assert.assertEquals(2, req.getCapability().getVirtualCores());
            Assert.assertEquals((1024 + 512), req.getCapability().getMemory());
        }
    }

    /**
     * Tests whether memory overhead prevents from allocating container.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testMemoryOverHeadPreventAllocation() throws Exception {
        rmMock.availableRes(new IgniteApplicationMasterSelfTest.MockResource(1024, 2));
        appMaster.setRmClient(rmMock);
        appMaster.setNmClient(new IgniteApplicationMasterSelfTest.NMMock());
        props.cpusPerNode(2);
        props.memoryPerNode(1024);
        props.memoryOverHeadPerNode(512);
        props.instances(3);
        Thread thread = IgniteApplicationMasterSelfTest.runAppMaster(appMaster);
        List<AMRMClient.ContainerRequest> contRequests = collectRequests(rmMock, 1, 1000);
        IgniteApplicationMasterSelfTest.interruptedThread(thread);
        Assert.assertEquals(0, contRequests.size());
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testClusterResource() throws Exception {
        rmMock.availableRes(new IgniteApplicationMasterSelfTest.MockResource(1024, 2));
        appMaster.setRmClient(rmMock);
        appMaster.setNmClient(new IgniteApplicationMasterSelfTest.NMMock());
        props.cpusPerNode(8);
        props.memoryPerNode(10240);
        props.instances(3);
        Thread thread = IgniteApplicationMasterSelfTest.runAppMaster(appMaster);
        List<AMRMClient.ContainerRequest> contRequests = collectRequests(rmMock, 1, 1000);
        IgniteApplicationMasterSelfTest.interruptedThread(thread);
        Assert.assertEquals(0, contRequests.size());
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testClusterAllocatedResource() throws Exception {
        rmMock.availableRes(new IgniteApplicationMasterSelfTest.MockResource(1024, 2));
        appMaster.setRmClient(rmMock);
        appMaster.setNmClient(new IgniteApplicationMasterSelfTest.NMMock());
        appMaster.setFs(new IgniteApplicationMasterSelfTest.MockFileSystem());
        props.cpusPerNode(8);
        props.memoryPerNode(5000);
        props.instances(3);
        // Check that container resources
        appMaster.onContainersAllocated(Collections.singletonList(createContainer("simple", 5, 2000)));
        Assert.assertEquals(0, appMaster.getContainers().size());
        appMaster.onContainersAllocated(Collections.singletonList(createContainer("simple", 10, 2000)));
        Assert.assertEquals(0, appMaster.getContainers().size());
        appMaster.onContainersAllocated(Collections.singletonList(createContainer("simple", 1, 7000)));
        Assert.assertEquals(0, appMaster.getContainers().size());
        appMaster.onContainersAllocated(Collections.singletonList(createContainer("simple", 8, 5000)));
        Assert.assertEquals(1, appMaster.getContainers().size());
        appMaster.onContainersAllocated(Collections.singletonList(createContainer("simple", 10, 7000)));
        Assert.assertEquals(2, appMaster.getContainers().size());
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testStartReleaseContainer() throws Exception {
        rmMock.availableRes(new IgniteApplicationMasterSelfTest.MockResource(1024, 2));
        IgniteApplicationMasterSelfTest.NMMock nmClient = new IgniteApplicationMasterSelfTest.NMMock();
        appMaster.setRmClient(rmMock);
        appMaster.setNmClient(nmClient);
        appMaster.setFs(new IgniteApplicationMasterSelfTest.MockFileSystem());
        props.cpusPerNode(8);
        props.memoryPerNode(5000);
        props.instances(3);
        // Check that container resources
        appMaster.onContainersAllocated(Collections.singletonList(createContainer("simple", 5, 2000)));
        Assert.assertEquals(1, rmMock.releasedResources().size());
        appMaster.onContainersAllocated(Collections.singletonList(createContainer("simple", 5, 7000)));
        Assert.assertEquals(2, rmMock.releasedResources().size());
        appMaster.onContainersAllocated(Collections.singletonList(createContainer("simple", 9, 2000)));
        Assert.assertEquals(3, rmMock.releasedResources().size());
        appMaster.onContainersAllocated(Collections.singletonList(createContainer("simple", 8, 5000)));
        Assert.assertEquals(3, rmMock.releasedResources().size());
        Assert.assertEquals(1, nmClient.startedContainer().size());
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testHostnameConstraint() throws Exception {
        rmMock.availableRes(new IgniteApplicationMasterSelfTest.MockResource(1024, 2));
        IgniteApplicationMasterSelfTest.NMMock nmClient = new IgniteApplicationMasterSelfTest.NMMock();
        appMaster.setRmClient(rmMock);
        appMaster.setNmClient(nmClient);
        appMaster.setFs(new IgniteApplicationMasterSelfTest.MockFileSystem());
        props.cpusPerNode(8);
        props.memoryPerNode(5000);
        props.instances(3);
        props.hostnameConstraint(Pattern.compile("ignoreHost"));
        // Check that container resources
        appMaster.onContainersAllocated(Collections.singletonList(createContainer("simple", 8, 5000)));
        Assert.assertEquals(0, rmMock.releasedResources().size());
        Assert.assertEquals(1, nmClient.startedContainer().size());
        appMaster.onContainersAllocated(Collections.singletonList(createContainer("ignoreHost", 8, 5000)));
        Assert.assertEquals(1, rmMock.releasedResources().size());
        Assert.assertEquals(1, nmClient.startedContainer().size());
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testContainerEnvironment() throws Exception {
        props.memoryPerNode(1001);
        props.memoryOverHeadPerNode(2002);
        // Properties are used to initialize AM container environment
        Map<String, String> result = props.toEnvs();
        Assert.assertEquals(1001, ((int) (Double.parseDouble(result.get(IGNITE_MEMORY_PER_NODE)))));
        Assert.assertEquals(2002, ((int) (Double.parseDouble(result.get(IGNITE_MEMORY_OVERHEAD_PER_NODE)))));
    }

    /**
     * Resource manager mock.
     */
    private static class RMMock extends AMRMClientAsync {
        /**
         *
         */
        private List<AMRMClient.ContainerRequest> contRequests = new ArrayList<>();

        /**
         *
         */
        private List<ContainerId> releasedConts = new ArrayList<>();

        /**
         *
         */
        private Resource availableRes;

        /**
         *
         */
        public RMMock() {
            super(0, null);
        }

        /**
         *
         *
         * @return Requests.
         */
        public List<AMRMClient.ContainerRequest> requests() {
            return contRequests;
        }

        /**
         *
         *
         * @return Released resources.
         */
        public List<ContainerId> releasedResources() {
            return releasedConts;
        }

        /**
         * Sets resource.
         *
         * @param availableRes
         * 		Available resource.
         */
        public void availableRes(Resource availableRes) {
            this.availableRes = availableRes;
        }

        /**
         * Clear internal state.
         */
        public void clear() {
            contRequests.clear();
            releasedConts.clear();
            availableRes = null;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public List<? extends Collection> getMatchingRequests(Priority priority, String resourceName, Resource capability) {
            return null;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public RegisterApplicationMasterResponse registerApplicationMaster(String appHostName, int appHostPort, String appTrackingUrl) throws IOException, YarnException {
            return null;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void unregisterApplicationMaster(FinalApplicationStatus appStatus, String appMessage, String appTrackingUrl) throws IOException, YarnException {
            // No-op.
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void addContainerRequest(AMRMClient.ContainerRequest req) {
            contRequests.add(req);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void removeContainerRequest(AMRMClient.ContainerRequest req) {
            // No-op.
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void releaseAssignedContainer(ContainerId containerId) {
            releasedConts.add(containerId);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Resource getAvailableResources() {
            return availableRes;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int getClusterNodeCount() {
            return 0;
        }

        /**
         * Update application's blacklist with addition or removal resources.
         *
         * @param blacklistAdditions
         * 		list of resources which should be added to the
         * 		application blacklist
         * @param blacklistRemovals
         * 		list of resources which should be removed from the
         * 		application blacklist
         */
        @Override
        public void updateBlacklist(List blacklistAdditions, List blacklistRemovals) {
            // No-op.
        }
    }

    /**
     * Network manager mock.
     */
    public static class NMMock extends NMClient {
        /**
         *
         */
        private List<ContainerLaunchContext> startedContainer = new ArrayList<>();

        /**
         *
         */
        public NMMock() {
            super("name");
        }

        /**
         *
         *
         * @return Started containers.
         */
        public List<ContainerLaunchContext> startedContainer() {
            return startedContainer;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Map<String, ByteBuffer> startContainer(Container container, ContainerLaunchContext containerLaunchContext) throws IOException, YarnException {
            startedContainer.add(containerLaunchContext);
            return null;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void stopContainer(ContainerId containerId, NodeId nodeId) throws IOException, YarnException {
            // No-op.
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public ContainerStatus getContainerStatus(ContainerId containerId, NodeId nodeId) throws IOException, YarnException {
            return null;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void cleanupRunningContainersOnStop(boolean enabled) {
            // No-op.
        }
    }

    /**
     * Resource.
     */
    public static class MockResource extends Resource {
        /**
         * Memory.
         */
        private int mem;

        /**
         * CPU.
         */
        private int cpu;

        /**
         *
         *
         * @param mem
         * 		Memory.
         * @param cpu
         * 		CPU.
         */
        public MockResource(int mem, int cpu) {
            this.mem = mem;
            this.cpu = cpu;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int getMemory() {
            return mem;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void setMemory(int memory) {
            this.mem = memory;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int getVirtualCores() {
            return cpu;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void setVirtualCores(int vCores) {
            this.cpu = vCores;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int compareTo(Resource resource) {
            return 0;
        }
    }

    /**
     * Mock file system.
     */
    public static class MockFileSystem extends FileSystem {
        /**
         *
         */
        public MockFileSystem() {
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Path makeQualified(Path path) {
            return new Path("/test/path");
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public FileStatus getFileStatus(Path f) throws IOException {
            return new FileStatus();
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean mkdirs(Path f, FsPermission permission) throws IOException {
            return false;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Path getWorkingDirectory() {
            return null;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void setWorkingDirectory(Path new_dir) {
            // No-op.
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public FileStatus[] listStatus(Path f) throws FileNotFoundException, IOException {
            return new FileStatus[0];
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean delete(Path f, boolean recursive) throws IOException {
            return false;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean rename(Path src, Path dst) throws IOException {
            return false;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public FSDataOutputStream append(Path f, int bufferSize, Progressable progress) throws IOException {
            return null;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public FSDataOutputStream create(Path f, FsPermission permission, boolean overwrite, int bufferSize, short replication, long blockSize, Progressable progress) throws IOException {
            return null;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public FSDataInputStream open(Path f, int bufferSize) throws IOException {
            return null;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public URI getUri() {
            return null;
        }
    }
}

