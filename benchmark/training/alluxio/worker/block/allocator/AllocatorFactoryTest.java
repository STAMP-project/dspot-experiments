/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.worker.block.allocator;


import Allocator.Factory;
import PropertyKey.WORKER_ALLOCATOR_CLASS;
import alluxio.conf.ServerConfiguration;
import alluxio.worker.block.BlockMetadataManagerView;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Test {@link Allocator.Factory} by passing different allocate strategy class names with alluxio
 * conf and test if it generates the correct {@link Allocator} instance.
 */
public final class AllocatorFactoryTest {
    private BlockMetadataManagerView mManagerView;

    /**
     * Rule to create a new temporary folder during each test.
     */
    @Rule
    public TemporaryFolder mTestFolder = new TemporaryFolder();

    /**
     * Tests the creation of the {@link GreedyAllocator} via the
     * {@link Allocator.Factory#create(BlockMetadataManagerView)} method.
     */
    @Test
    public void createGreedyAllocator() {
        ServerConfiguration.set(WORKER_ALLOCATOR_CLASS, GreedyAllocator.class.getName());
        Allocator allocator = Factory.create(mManagerView);
        Assert.assertTrue((allocator instanceof GreedyAllocator));
    }

    /**
     * Tests the creation of the {@link MaxFreeAllocator} via the
     * {@link Allocator.Factory#create(BlockMetadataManagerView)} method.
     */
    @Test
    public void createMaxFreeAllocator() {
        ServerConfiguration.set(WORKER_ALLOCATOR_CLASS, MaxFreeAllocator.class.getName());
        Allocator allocator = Factory.create(mManagerView);
        Assert.assertTrue((allocator instanceof MaxFreeAllocator));
    }

    /**
     * Tests the creation of the {@link RoundRobinAllocator} via the
     * {@link Allocator.Factory#create(BlockMetadataManagerView)} method.
     */
    @Test
    public void createRoundRobinAllocator() {
        ServerConfiguration.set(WORKER_ALLOCATOR_CLASS, RoundRobinAllocator.class.getName());
        Allocator allocator = Factory.create(mManagerView);
        Assert.assertTrue((allocator instanceof RoundRobinAllocator));
    }

    /**
     * Tests the creation of the default allocator via the
     * {@link Allocator.Factory#create(BlockMetadataManagerView)} method.
     */
    @Test
    public void createDefaultAllocator() {
        // Create a new instance of Alluxio configuration with original properties to test the default
        // behavior of create.
        Allocator allocator = Factory.create(mManagerView);
        Assert.assertTrue((allocator instanceof MaxFreeAllocator));
    }
}

