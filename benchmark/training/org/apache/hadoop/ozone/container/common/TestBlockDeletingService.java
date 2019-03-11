/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership.  The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.ozone.container.common;


import BackgroundService.LOG;
import ScmConfigKeys.OZONE_SCM_KEY_VALUE_CONTAINER_DELETION_CHOOSING_POLICY;
import com.google.common.collect.Lists;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.ozone.OzoneConfigKeys;
import org.apache.hadoop.ozone.container.common.impl.ContainerData;
import org.apache.hadoop.ozone.container.common.impl.ContainerSet;
import org.apache.hadoop.ozone.container.common.impl.RandomContainerDeletionChoosingPolicy;
import org.apache.hadoop.ozone.container.common.interfaces.Container;
import org.apache.hadoop.ozone.container.keyvalue.KeyValueContainerData;
import org.apache.hadoop.ozone.container.keyvalue.helpers.BlockUtils;
import org.apache.hadoop.ozone.container.keyvalue.statemachine.background.BlockDeletingService;
import org.apache.hadoop.ozone.container.testutils.BlockDeletingServiceTestImpl;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.GenericTestUtils.LogCapturer;
import org.apache.hadoop.utils.MetadataStore;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests to test block deleting service.
 */
public class TestBlockDeletingService {
    private static final Logger LOG = LoggerFactory.getLogger(TestBlockDeletingService.class);

    private static File testRoot;

    private static String scmId;

    private static String clusterID;

    @Test
    public void testBlockDeletion() throws Exception {
        Configuration conf = new OzoneConfiguration();
        conf.set(OZONE_SCM_KEY_VALUE_CONTAINER_DELETION_CHOOSING_POLICY, RandomContainerDeletionChoosingPolicy.class.getName());
        conf.setInt(OzoneConfigKeys.OZONE_BLOCK_DELETING_CONTAINER_LIMIT_PER_INTERVAL, 10);
        conf.setInt(OzoneConfigKeys.OZONE_BLOCK_DELETING_LIMIT_PER_CONTAINER, 2);
        ContainerSet containerSet = new ContainerSet();
        createToDeleteBlocks(containerSet, conf, 1, 3, 1);
        BlockDeletingServiceTestImpl svc = new BlockDeletingServiceTestImpl(containerSet, 1000, conf);
        svc.start();
        GenericTestUtils.waitFor(() -> svc.isStarted(), 100, 3000);
        // Ensure 1 container was created
        List<ContainerData> containerData = Lists.newArrayList();
        containerSet.listContainer(0L, 1, containerData);
        Assert.assertEquals(1, containerData.size());
        MetadataStore meta = BlockUtils.getDB(((KeyValueContainerData) (containerData.get(0))), conf);
        Map<Long, Container> containerMap = containerSet.getContainerMapCopy();
        // NOTE: this test assumes that all the container is KetValueContainer and
        // have DeleteTransactionId in KetValueContainerData. If other
        // types is going to be added, this test should be checked.
        long transactionId = getDeleteTransactionId();
        // Number of deleted blocks in container should be equal to 0 before
        // block delete
        Assert.assertEquals(0, transactionId);
        // Ensure there are 3 blocks under deletion and 0 deleted blocks
        Assert.assertEquals(3, getUnderDeletionBlocksCount(meta));
        Assert.assertEquals(0, getDeletedBlocksCount(meta));
        // An interval will delete 1 * 2 blocks
        deleteAndWait(svc, 1);
        Assert.assertEquals(1, getUnderDeletionBlocksCount(meta));
        Assert.assertEquals(2, getDeletedBlocksCount(meta));
        deleteAndWait(svc, 2);
        Assert.assertEquals(0, getUnderDeletionBlocksCount(meta));
        Assert.assertEquals(3, getDeletedBlocksCount(meta));
        deleteAndWait(svc, 3);
        Assert.assertEquals(0, getUnderDeletionBlocksCount(meta));
        Assert.assertEquals(3, getDeletedBlocksCount(meta));
        svc.shutdown();
    }

    @Test
    public void testShutdownService() throws Exception {
        Configuration conf = new OzoneConfiguration();
        conf.set(OZONE_SCM_KEY_VALUE_CONTAINER_DELETION_CHOOSING_POLICY, RandomContainerDeletionChoosingPolicy.class.getName());
        conf.setTimeDuration(OzoneConfigKeys.OZONE_BLOCK_DELETING_SERVICE_INTERVAL, 500, TimeUnit.MILLISECONDS);
        conf.setInt(OzoneConfigKeys.OZONE_BLOCK_DELETING_CONTAINER_LIMIT_PER_INTERVAL, 10);
        conf.setInt(OzoneConfigKeys.OZONE_BLOCK_DELETING_LIMIT_PER_CONTAINER, 10);
        ContainerSet containerSet = new ContainerSet();
        // Create 1 container with 100 blocks
        createToDeleteBlocks(containerSet, conf, 1, 100, 1);
        BlockDeletingServiceTestImpl service = new BlockDeletingServiceTestImpl(containerSet, 1000, conf);
        service.start();
        GenericTestUtils.waitFor(() -> service.isStarted(), 100, 3000);
        // Run some deleting tasks and verify there are threads running
        service.runDeletingTasks();
        GenericTestUtils.waitFor(() -> (service.getThreadCount()) > 0, 100, 1000);
        // Wait for 1 or 2 intervals
        Thread.sleep(1000);
        // Shutdown service and verify all threads are stopped
        service.shutdown();
        GenericTestUtils.waitFor(() -> (service.getThreadCount()) == 0, 100, 1000);
    }

    @Test
    public void testBlockDeletionTimeout() throws Exception {
        Configuration conf = new OzoneConfiguration();
        conf.set(OZONE_SCM_KEY_VALUE_CONTAINER_DELETION_CHOOSING_POLICY, RandomContainerDeletionChoosingPolicy.class.getName());
        conf.setInt(OzoneConfigKeys.OZONE_BLOCK_DELETING_CONTAINER_LIMIT_PER_INTERVAL, 10);
        conf.setInt(OzoneConfigKeys.OZONE_BLOCK_DELETING_LIMIT_PER_CONTAINER, 2);
        ContainerSet containerSet = new ContainerSet();
        createToDeleteBlocks(containerSet, conf, 1, 3, 1);
        // set timeout value as 1ns to trigger timeout behavior
        long timeout = 1;
        BlockDeletingService svc = new BlockDeletingService(containerSet, TimeUnit.MILLISECONDS.toNanos(1000), timeout, TimeUnit.NANOSECONDS, conf);
        svc.start();
        LogCapturer log = LogCapturer.captureLogs(BackgroundService.LOG);
        GenericTestUtils.waitFor(() -> {
            if (log.getOutput().contains("Background task executes timed out, retrying in next interval")) {
                log.stopCapturing();
                return true;
            }
            return false;
        }, 1000, 100000);
        log.stopCapturing();
        svc.shutdown();
        // test for normal case that doesn't have timeout limitation
        timeout = 0;
        createToDeleteBlocks(containerSet, conf, 1, 3, 1);
        svc = new BlockDeletingService(containerSet, TimeUnit.MILLISECONDS.toNanos(1000), timeout, TimeUnit.MILLISECONDS, conf);
        svc.start();
        // get container meta data
        List<ContainerData> containerData = Lists.newArrayList();
        containerSet.listContainer(0L, 1, containerData);
        MetadataStore meta = BlockUtils.getDB(((KeyValueContainerData) (containerData.get(0))), conf);
        LogCapturer newLog = LogCapturer.captureLogs(BackgroundService.LOG);
        GenericTestUtils.waitFor(() -> {
            try {
                if ((getUnderDeletionBlocksCount(meta)) == 0) {
                    return true;
                }
            } catch ( ignored) {
            }
            return false;
        }, 1000, 100000);
        newLog.stopCapturing();
        // The block deleting successfully and shouldn't catch timed
        // out warning log.
        Assert.assertTrue((!(newLog.getOutput().contains("Background task executes timed out, retrying in next interval"))));
        svc.shutdown();
    }

    @Test(timeout = 30000)
    public void testContainerThrottle() throws Exception {
        // Properties :
        // - Number of containers : 2
        // - Number of blocks per container : 1
        // - Number of chunks per block : 10
        // - Container limit per interval : 1
        // - Block limit per container : 1
        // 
        // Each time only 1 container can be processed, so each time
        // 1 block from 1 container can be deleted.
        Configuration conf = new OzoneConfiguration();
        // Process 1 container per interval
        conf.set(OZONE_SCM_KEY_VALUE_CONTAINER_DELETION_CHOOSING_POLICY, RandomContainerDeletionChoosingPolicy.class.getName());
        conf.setInt(OzoneConfigKeys.OZONE_BLOCK_DELETING_CONTAINER_LIMIT_PER_INTERVAL, 1);
        conf.setInt(OzoneConfigKeys.OZONE_BLOCK_DELETING_LIMIT_PER_CONTAINER, 1);
        ContainerSet containerSet = new ContainerSet();
        createToDeleteBlocks(containerSet, conf, 2, 1, 10);
        BlockDeletingServiceTestImpl service = new BlockDeletingServiceTestImpl(containerSet, 1000, conf);
        service.start();
        try {
            GenericTestUtils.waitFor(() -> service.isStarted(), 100, 3000);
            // 1st interval processes 1 container 1 block and 10 chunks
            deleteAndWait(service, 1);
            Assert.assertEquals(10, getNumberOfChunksInContainers(containerSet));
            AtomicInteger timesToProcess = new AtomicInteger(1);
            GenericTestUtils.waitFor(() -> {
                try {
                    timesToProcess.incrementAndGet();
                    deleteAndWait(service, timesToProcess.get());
                    if ((getNumberOfChunksInContainers(containerSet)) == 0) {
                        return true;
                    }
                } catch ( e) {
                }
                return false;
            }, 100, 100000);
            Assert.assertEquals(0, getNumberOfChunksInContainers(containerSet));
        } finally {
            service.shutdown();
        }
    }

    @Test(timeout = 30000)
    public void testBlockThrottle() throws Exception {
        // Properties :
        // - Number of containers : 5
        // - Number of blocks per container : 3
        // - Number of chunks per block : 1
        // - Container limit per interval : 10
        // - Block limit per container : 2
        // 
        // Each time containers can be all scanned, but only 2 blocks
        // per container can be actually deleted. So it requires 2 waves
        // to cleanup all blocks.
        Configuration conf = new OzoneConfiguration();
        conf.set(OZONE_SCM_KEY_VALUE_CONTAINER_DELETION_CHOOSING_POLICY, RandomContainerDeletionChoosingPolicy.class.getName());
        conf.setInt(OzoneConfigKeys.OZONE_BLOCK_DELETING_CONTAINER_LIMIT_PER_INTERVAL, 10);
        conf.setInt(OzoneConfigKeys.OZONE_BLOCK_DELETING_LIMIT_PER_CONTAINER, 2);
        ContainerSet containerSet = new ContainerSet();
        createToDeleteBlocks(containerSet, conf, 5, 3, 1);
        // Make sure chunks are created
        Assert.assertEquals(15, getNumberOfChunksInContainers(containerSet));
        BlockDeletingServiceTestImpl service = new BlockDeletingServiceTestImpl(containerSet, 1000, conf);
        service.start();
        try {
            GenericTestUtils.waitFor(() -> service.isStarted(), 100, 3000);
            // Total blocks = 3 * 5 = 15
            // block per task = 2
            // number of containers = 5
            // each interval will at most runDeletingTasks 5 * 2 = 10 blocks
            deleteAndWait(service, 1);
            Assert.assertEquals(5, getNumberOfChunksInContainers(containerSet));
            // There is only 5 blocks left to runDeletingTasks
            deleteAndWait(service, 2);
            Assert.assertEquals(0, getNumberOfChunksInContainers(containerSet));
        } finally {
            service.shutdown();
        }
    }
}

