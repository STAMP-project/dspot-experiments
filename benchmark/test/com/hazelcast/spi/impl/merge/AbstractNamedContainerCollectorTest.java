/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.spi.impl.merge;


import AbstractNamedContainerCollector.ContainerIterator;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class AbstractNamedContainerCollectorTest extends HazelcastTestSupport {
    private NodeEngineImpl nodeEngine;

    @Test
    public void testAbstractNamedContainerCollector() {
        TestNamedContainerCollector collector = new TestNamedContainerCollector(nodeEngine, true, true);
        HazelcastTestSupport.assertEqualsStringFormat("Expected the to have %d containers, but found %d", 1, collector.containers.size());
        run();
        HazelcastTestSupport.assertEqualsStringFormat("Expected %d merging values, but found %d", 1, collector.getMergingValueCount());
        Assert.assertEquals("Expected the collected containers to be removed from the container map", 0, collector.containers.size());
    }

    @Test
    public void testNonPartitionedCollector_withoutContainers() {
        TestNamedContainerCollector collector = new TestNamedContainerCollector(nodeEngine, false, true);
        HazelcastTestSupport.assertEqualsStringFormat("Expected the to have %d containers, but found %d", 0, collector.containers.size());
        run();
        HazelcastTestSupport.assertEqualsStringFormat("Expected %d merging values, but found %d", 0, collector.getMergingValueCount());
        Assert.assertEquals("Expected the collected containers to be removed from the container map", 0, collector.containers.size());
    }

    @Test
    public void testNonPartitionedCollector_withoutMergeableContainers() {
        TestNamedContainerCollector collector = new TestNamedContainerCollector(nodeEngine, true, false);
        HazelcastTestSupport.assertEqualsStringFormat("Expected the to have %d containers, but found %d", 1, collector.containers.size());
        run();
        HazelcastTestSupport.assertEqualsStringFormat("Expected %d merging values, but found %d", 0, collector.getMergingValueCount());
        Assert.assertEquals("Expected the collected containers to be removed from the container map", 0, collector.containers.size());
    }

    @Test
    public void testContainerIterator() {
        TestNamedContainerCollector collector = new TestNamedContainerCollector(nodeEngine, true, true);
        Assert.assertEquals(1, collector.containers.size());
        int partitionId = getContainerPartitionId("myContainer");
        Iterator<Object> iterator = containerIterator(partitionId);
        HazelcastTestSupport.assertInstanceOf(ContainerIterator.class, iterator);
        Assert.assertTrue("Expected next elements in iterator", iterator.hasNext());
        Assert.assertNotNull("", iterator.next());
        // iterator.remove() should remove the current container
        iterator.remove();
        Assert.assertEquals(0, collector.containers.size());
    }

    @Test
    public void testContainerIterator_onEmptyPartition() {
        TestNamedContainerCollector collector = new TestNamedContainerCollector(nodeEngine, true, true);
        Assert.assertEquals(1, collector.containers.size());
        int partitionId = (collector.getContainerPartitionId("myContainer")) + 1;
        Iterator<Object> iterator = containerIterator(partitionId);
        HazelcastTestSupport.assertInstanceOf(ContainerIterator.class, iterator);
        Assert.assertFalse("Expected no next elements in iterator", iterator.hasNext());
        try {
            iterator.next();
            Assert.fail("Expected ContainerIterator.next() to throw NoSuchElementException");
        } catch (NoSuchElementException expected) {
            HazelcastTestSupport.ignore(expected);
        }
        // iterator.remove() should not remove anything, since the container belongs to another partition
        iterator.remove();
        Assert.assertEquals(1, collector.containers.size());
    }

    @Test
    public void testContainerIterator_withoutContainers() {
        TestNamedContainerCollector collector = new TestNamedContainerCollector(nodeEngine, false, false);
        Assert.assertEquals(0, collector.containers.size());
        int partitionId = getContainerPartitionId("myContainer");
        Iterator<Object> iterator = containerIterator(partitionId);
        HazelcastTestSupport.assertInstanceOf(ContainerIterator.class, iterator);
        Assert.assertFalse("Expected no next elements in iterator", iterator.hasNext());
        try {
            iterator.next();
            Assert.fail("Expected ContainerIterator.next() to throw NoSuchElementException");
        } catch (NoSuchElementException expected) {
            HazelcastTestSupport.ignore(expected);
        }
        // iterator.remove() should do nothing
        iterator.remove();
        Assert.assertEquals(0, collector.containers.size());
    }

    @Test(expected = IllegalStateException.class)
    public void testContainerIterator_whenNextCalledBeforeHasNext() {
        TestNamedContainerCollector collector = new TestNamedContainerCollector(nodeEngine, true, true);
        Assert.assertEquals(1, collector.containers.size());
        int partitionId = getContainerPartitionId("myContainer");
        Iterator<Object> iterator = containerIterator(partitionId);
        HazelcastTestSupport.assertInstanceOf(ContainerIterator.class, iterator);
        iterator.next();
    }
}

