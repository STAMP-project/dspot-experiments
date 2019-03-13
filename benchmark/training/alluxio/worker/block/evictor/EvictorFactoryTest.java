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
package alluxio.worker.block.evictor;


import Allocator.Factory;
import PropertyKey.WORKER_ALLOCATOR_CLASS;
import PropertyKey.WORKER_EVICTOR_CLASS;
import alluxio.conf.ServerConfiguration;
import alluxio.worker.block.BlockMetadataManager;
import alluxio.worker.block.BlockMetadataManagerView;
import alluxio.worker.block.allocator.Allocator;
import alluxio.worker.block.allocator.MaxFreeAllocator;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Test {@link Evictor.Factory} by passing in different evictor strategy class names through the
 * Alluxio configuration and verifying the correct Evictor instance is created.
 */
public class EvictorFactoryTest {
    private static BlockMetadataManager sBlockMetadataManager;

    private static BlockMetadataManagerView sBlockMetadataManagerView;

    /**
     * Rule to create a new temporary folder during each test.
     */
    @Rule
    public TemporaryFolder mTestFolder = new TemporaryFolder();

    /**
     * Tests that a {@link GreedyEvictor} can be created from
     * {@link alluxio.worker.block.evictor.Evictor.Factory#create(
     *        BlockMetadataManagerView, Allocator)}.
     */
    @Test
    public void createGreedyEvictor() {
        ServerConfiguration.set(WORKER_EVICTOR_CLASS, GreedyEvictor.class.getName());
        ServerConfiguration.set(WORKER_ALLOCATOR_CLASS, MaxFreeAllocator.class.getName());
        Allocator allocator = Factory.create(EvictorFactoryTest.sBlockMetadataManagerView);
        Evictor evictor = Evictor.Factory.create(EvictorFactoryTest.sBlockMetadataManagerView, allocator);
        Assert.assertTrue((evictor instanceof GreedyEvictor));
    }

    /**
     * Tests that a {@link LRUEvictor} can be created from
     * {@link alluxio.worker.block.evictor.Evictor.Factory#create(
     *        BlockMetadataManagerView, Allocator)}.
     */
    @Test
    public void createLRUEvictor() {
        ServerConfiguration.set(WORKER_EVICTOR_CLASS, LRUEvictor.class.getName());
        ServerConfiguration.set(WORKER_ALLOCATOR_CLASS, MaxFreeAllocator.class.getName());
        Allocator allocator = Factory.create(EvictorFactoryTest.sBlockMetadataManagerView);
        Evictor evictor = Evictor.Factory.create(EvictorFactoryTest.sBlockMetadataManagerView, allocator);
        Assert.assertTrue((evictor instanceof LRUEvictor));
    }

    /**
     * Tests that the default evictor can be created from
     * {@link alluxio.worker.block.evictor.Evictor.Factory#create(
     *        BlockMetadataManagerView, Allocator)}.
     */
    @Test
    public void createDefaultEvictor() {
        ServerConfiguration.set(WORKER_ALLOCATOR_CLASS, MaxFreeAllocator.class.getName());
        Allocator allocator = Factory.create(EvictorFactoryTest.sBlockMetadataManagerView);
        Evictor evictor = Evictor.Factory.create(EvictorFactoryTest.sBlockMetadataManagerView, allocator);
        Assert.assertTrue((evictor instanceof LRUEvictor));
    }
}

