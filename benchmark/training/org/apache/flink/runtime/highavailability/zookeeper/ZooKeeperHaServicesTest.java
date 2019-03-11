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
package org.apache.flink.runtime.highavailability.zookeeper;


import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.curator.framework.CuratorFramework;
import org.apache.flink.api.common.JobID;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.blob.BlobKey;
import org.apache.flink.runtime.blob.BlobStoreService;
import org.apache.flink.runtime.zookeeper.ZooKeeperResource;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;


/**
 * Tests for the {@link ZooKeeperHaServices}.
 */
public class ZooKeeperHaServicesTest extends TestLogger {
    @ClassRule
    public static final ZooKeeperResource ZOO_KEEPER_RESOURCE = new ZooKeeperResource();

    private static CuratorFramework client;

    /**
     * Tests that a simple {@link ZooKeeperHaServices#close()} does not delete ZooKeeper paths.
     */
    @Test
    public void testSimpleClose() throws Exception {
        final String rootPath = "/foo/bar/flink";
        final Configuration configuration = createConfiguration(rootPath);
        final ZooKeeperHaServicesTest.TestingBlobStoreService blobStoreService = new ZooKeeperHaServicesTest.TestingBlobStoreService();
        runCleanupTest(configuration, blobStoreService, ZooKeeperHaServices::close);
        Assert.assertThat(blobStoreService.isClosed(), Matchers.is(true));
        Assert.assertThat(blobStoreService.isClosedAndCleanedUpAllData(), Matchers.is(false));
        final List<String> children = ZooKeeperHaServicesTest.client.getChildren().forPath(rootPath);
        Assert.assertThat(children, Matchers.is(Matchers.not(Matchers.empty())));
    }

    /**
     * Tests that the {@link ZooKeeperHaServices} cleans up all paths if
     * it is closed via {@link ZooKeeperHaServices#closeAndCleanupAllData()}.
     */
    @Test
    public void testSimpleCloseAndCleanupAllData() throws Exception {
        final Configuration configuration = createConfiguration("/foo/bar/flink");
        final ZooKeeperHaServicesTest.TestingBlobStoreService blobStoreService = new ZooKeeperHaServicesTest.TestingBlobStoreService();
        final List<String> initialChildren = ZooKeeperHaServicesTest.client.getChildren().forPath("/");
        runCleanupTest(configuration, blobStoreService, ZooKeeperHaServices::closeAndCleanupAllData);
        Assert.assertThat(blobStoreService.isClosedAndCleanedUpAllData(), Matchers.is(true));
        final List<String> children = ZooKeeperHaServicesTest.client.getChildren().forPath("/");
        Assert.assertThat(children, Matchers.is(Matchers.equalTo(initialChildren)));
    }

    /**
     * Tests that we can only delete the parent znodes as long as they are empty.
     */
    @Test
    public void testCloseAndCleanupAllDataWithUncle() throws Exception {
        final String prefix = "/foo/bar";
        final String flinkPath = prefix + "/flink";
        final Configuration configuration = createConfiguration(flinkPath);
        final ZooKeeperHaServicesTest.TestingBlobStoreService blobStoreService = new ZooKeeperHaServicesTest.TestingBlobStoreService();
        final String unclePath = prefix + "/foobar";
        ZooKeeperHaServicesTest.client.create().creatingParentContainersIfNeeded().forPath(unclePath);
        runCleanupTest(configuration, blobStoreService, ZooKeeperHaServices::closeAndCleanupAllData);
        Assert.assertThat(blobStoreService.isClosedAndCleanedUpAllData(), Matchers.is(true));
        Assert.assertThat(ZooKeeperHaServicesTest.client.checkExists().forPath(flinkPath), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(ZooKeeperHaServicesTest.client.checkExists().forPath(unclePath), Matchers.is(Matchers.notNullValue()));
    }

    private static class TestingBlobStoreService implements BlobStoreService {
        private boolean closedAndCleanedUpAllData = false;

        private boolean closed = false;

        @Override
        public void closeAndCleanupAllData() {
            closedAndCleanedUpAllData = true;
        }

        @Override
        public void close() throws IOException {
            closed = true;
        }

        @Override
        public boolean put(File localFile, JobID jobId, BlobKey blobKey) {
            return false;
        }

        @Override
        public boolean delete(JobID jobId, BlobKey blobKey) {
            return false;
        }

        @Override
        public boolean deleteAll(JobID jobId) {
            return false;
        }

        @Override
        public boolean get(JobID jobId, BlobKey blobKey, File localFile) {
            return false;
        }

        private boolean isClosed() {
            return closed;
        }

        private boolean isClosedAndCleanedUpAllData() {
            return closedAndCleanedUpAllData;
        }
    }
}

