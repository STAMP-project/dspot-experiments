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
package org.apache.flink.streaming.connectors.kafka.internals;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.flink.streaming.connectors.kafka.testutils.TestPartitionDiscoverer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests that the partition assignment in the partition discoverer is
 * deterministic and stable, with both fixed and growing partitions.
 */
@RunWith(Parameterized.class)
public class AbstractPartitionDiscovererTest {
    private static final String TEST_TOPIC = "test-topic";

    private static final String TEST_TOPIC_PATTERN = ("^" + (AbstractPartitionDiscovererTest.TEST_TOPIC)) + "[0-9]*$";

    private final KafkaTopicsDescriptor topicsDescriptor;

    public AbstractPartitionDiscovererTest(KafkaTopicsDescriptor topicsDescriptor) {
        this.topicsDescriptor = topicsDescriptor;
    }

    @Test
    public void testPartitionsEqualConsumersFixedPartitions() throws Exception {
        List<KafkaTopicPartition> mockGetAllPartitionsForTopicsReturn = Arrays.asList(new KafkaTopicPartition(AbstractPartitionDiscovererTest.TEST_TOPIC, 0), new KafkaTopicPartition(AbstractPartitionDiscovererTest.TEST_TOPIC, 1), new KafkaTopicPartition(AbstractPartitionDiscovererTest.TEST_TOPIC, 2), new KafkaTopicPartition(AbstractPartitionDiscovererTest.TEST_TOPIC, 3));
        int numSubtasks = mockGetAllPartitionsForTopicsReturn.size();
        // get the start index; the assertions below will fail if the assignment logic does not meet correct contracts
        int numConsumers = KafkaTopicPartitionAssigner.assign(mockGetAllPartitionsForTopicsReturn.get(0), numSubtasks);
        for (int subtaskIndex = 0; subtaskIndex < (mockGetAllPartitionsForTopicsReturn.size()); subtaskIndex++) {
            TestPartitionDiscoverer partitionDiscoverer = new TestPartitionDiscoverer(topicsDescriptor, subtaskIndex, mockGetAllPartitionsForTopicsReturn.size(), TestPartitionDiscoverer.createMockGetAllTopicsSequenceFromFixedReturn(Collections.singletonList(AbstractPartitionDiscovererTest.TEST_TOPIC)), TestPartitionDiscoverer.createMockGetAllPartitionsFromTopicsSequenceFromFixedReturn(mockGetAllPartitionsForTopicsReturn));
            open();
            List<KafkaTopicPartition> initialDiscovery = discoverPartitions();
            Assert.assertEquals(1, initialDiscovery.size());
            Assert.assertTrue(contains(mockGetAllPartitionsForTopicsReturn, initialDiscovery.get(0).getPartition()));
            Assert.assertEquals(AbstractPartitionDiscovererTest.getExpectedSubtaskIndex(initialDiscovery.get(0), numConsumers, numSubtasks), subtaskIndex);
            // subsequent discoveries should not find anything
            List<KafkaTopicPartition> secondDiscovery = discoverPartitions();
            List<KafkaTopicPartition> thirdDiscovery = discoverPartitions();
            Assert.assertEquals(0, secondDiscovery.size());
            Assert.assertEquals(0, thirdDiscovery.size());
        }
    }

    @Test
    public void testMultiplePartitionsPerConsumersFixedPartitions() {
        try {
            final int[] partitionIDs = new int[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
            final List<KafkaTopicPartition> mockGetAllPartitionsForTopicsReturn = new ArrayList<>();
            final Set<KafkaTopicPartition> allPartitions = new HashSet<>();
            for (int p : partitionIDs) {
                KafkaTopicPartition part = new KafkaTopicPartition(AbstractPartitionDiscovererTest.TEST_TOPIC, p);
                mockGetAllPartitionsForTopicsReturn.add(part);
                allPartitions.add(part);
            }
            final int numConsumers = 3;
            final int minPartitionsPerConsumer = (mockGetAllPartitionsForTopicsReturn.size()) / numConsumers;
            final int maxPartitionsPerConsumer = ((mockGetAllPartitionsForTopicsReturn.size()) / numConsumers) + 1;
            // get the start index; the assertions below will fail if the assignment logic does not meet correct contracts
            int startIndex = KafkaTopicPartitionAssigner.assign(mockGetAllPartitionsForTopicsReturn.get(0), numConsumers);
            for (int subtaskIndex = 0; subtaskIndex < numConsumers; subtaskIndex++) {
                TestPartitionDiscoverer partitionDiscoverer = new TestPartitionDiscoverer(topicsDescriptor, subtaskIndex, numConsumers, TestPartitionDiscoverer.createMockGetAllTopicsSequenceFromFixedReturn(Collections.singletonList(AbstractPartitionDiscovererTest.TEST_TOPIC)), TestPartitionDiscoverer.createMockGetAllPartitionsFromTopicsSequenceFromFixedReturn(mockGetAllPartitionsForTopicsReturn));
                open();
                List<KafkaTopicPartition> initialDiscovery = discoverPartitions();
                Assert.assertTrue(((initialDiscovery.size()) >= minPartitionsPerConsumer));
                Assert.assertTrue(((initialDiscovery.size()) <= maxPartitionsPerConsumer));
                for (KafkaTopicPartition p : initialDiscovery) {
                    // check that the element was actually contained
                    Assert.assertTrue(allPartitions.remove(p));
                    Assert.assertEquals(AbstractPartitionDiscovererTest.getExpectedSubtaskIndex(p, startIndex, numConsumers), subtaskIndex);
                }
                // subsequent discoveries should not find anything
                List<KafkaTopicPartition> secondDiscovery = discoverPartitions();
                List<KafkaTopicPartition> thirdDiscovery = discoverPartitions();
                Assert.assertEquals(0, secondDiscovery.size());
                Assert.assertEquals(0, thirdDiscovery.size());
            }
            // all partitions must have been assigned
            Assert.assertTrue(allPartitions.isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testPartitionsFewerThanConsumersFixedPartitions() {
        try {
            List<KafkaTopicPartition> mockGetAllPartitionsForTopicsReturn = Arrays.asList(new KafkaTopicPartition(AbstractPartitionDiscovererTest.TEST_TOPIC, 0), new KafkaTopicPartition(AbstractPartitionDiscovererTest.TEST_TOPIC, 1), new KafkaTopicPartition(AbstractPartitionDiscovererTest.TEST_TOPIC, 2), new KafkaTopicPartition(AbstractPartitionDiscovererTest.TEST_TOPIC, 3));
            final Set<KafkaTopicPartition> allPartitions = new HashSet<>();
            allPartitions.addAll(mockGetAllPartitionsForTopicsReturn);
            final int numConsumers = (2 * (mockGetAllPartitionsForTopicsReturn.size())) + 3;
            // get the start index; the assertions below will fail if the assignment logic does not meet correct contracts
            int startIndex = KafkaTopicPartitionAssigner.assign(mockGetAllPartitionsForTopicsReturn.get(0), numConsumers);
            for (int subtaskIndex = 0; subtaskIndex < numConsumers; subtaskIndex++) {
                TestPartitionDiscoverer partitionDiscoverer = new TestPartitionDiscoverer(topicsDescriptor, subtaskIndex, numConsumers, TestPartitionDiscoverer.createMockGetAllTopicsSequenceFromFixedReturn(Collections.singletonList(AbstractPartitionDiscovererTest.TEST_TOPIC)), TestPartitionDiscoverer.createMockGetAllPartitionsFromTopicsSequenceFromFixedReturn(mockGetAllPartitionsForTopicsReturn));
                open();
                List<KafkaTopicPartition> initialDiscovery = discoverPartitions();
                Assert.assertTrue(((initialDiscovery.size()) <= 1));
                for (KafkaTopicPartition p : initialDiscovery) {
                    // check that the element was actually contained
                    Assert.assertTrue(allPartitions.remove(p));
                    Assert.assertEquals(AbstractPartitionDiscovererTest.getExpectedSubtaskIndex(p, startIndex, numConsumers), subtaskIndex);
                }
                // subsequent discoveries should not find anything
                List<KafkaTopicPartition> secondDiscovery = discoverPartitions();
                List<KafkaTopicPartition> thirdDiscovery = discoverPartitions();
                Assert.assertEquals(0, secondDiscovery.size());
                Assert.assertEquals(0, thirdDiscovery.size());
            }
            // all partitions must have been assigned
            Assert.assertTrue(allPartitions.isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testGrowingPartitions() {
        try {
            final int[] newPartitionIDs = new int[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
            List<KafkaTopicPartition> allPartitions = new ArrayList<>(11);
            for (int p : newPartitionIDs) {
                KafkaTopicPartition part = new KafkaTopicPartition(AbstractPartitionDiscovererTest.TEST_TOPIC, p);
                allPartitions.add(part);
            }
            // first discovery returns an initial subset of the partitions; second returns all partitions
            List<List<KafkaTopicPartition>> mockGetAllPartitionsForTopicsReturnSequence = Arrays.asList(new ArrayList(allPartitions.subList(0, 7)), allPartitions);
            final Set<KafkaTopicPartition> allNewPartitions = new HashSet(allPartitions);
            final Set<KafkaTopicPartition> allInitialPartitions = new HashSet(mockGetAllPartitionsForTopicsReturnSequence.get(0));
            final int numConsumers = 3;
            final int minInitialPartitionsPerConsumer = (mockGetAllPartitionsForTopicsReturnSequence.get(0).size()) / numConsumers;
            final int maxInitialPartitionsPerConsumer = ((mockGetAllPartitionsForTopicsReturnSequence.get(0).size()) / numConsumers) + 1;
            final int minNewPartitionsPerConsumer = (allPartitions.size()) / numConsumers;
            final int maxNewPartitionsPerConsumer = ((allPartitions.size()) / numConsumers) + 1;
            // get the start index; the assertions below will fail if the assignment logic does not meet correct contracts
            int startIndex = KafkaTopicPartitionAssigner.assign(allPartitions.get(0), numConsumers);
            TestPartitionDiscoverer partitionDiscovererSubtask0 = new TestPartitionDiscoverer(topicsDescriptor, 0, numConsumers, TestPartitionDiscoverer.createMockGetAllTopicsSequenceFromFixedReturn(Collections.singletonList(AbstractPartitionDiscovererTest.TEST_TOPIC)), deepClone(mockGetAllPartitionsForTopicsReturnSequence));
            open();
            TestPartitionDiscoverer partitionDiscovererSubtask1 = new TestPartitionDiscoverer(topicsDescriptor, 1, numConsumers, TestPartitionDiscoverer.createMockGetAllTopicsSequenceFromFixedReturn(Collections.singletonList(AbstractPartitionDiscovererTest.TEST_TOPIC)), deepClone(mockGetAllPartitionsForTopicsReturnSequence));
            open();
            TestPartitionDiscoverer partitionDiscovererSubtask2 = new TestPartitionDiscoverer(topicsDescriptor, 2, numConsumers, TestPartitionDiscoverer.createMockGetAllTopicsSequenceFromFixedReturn(Collections.singletonList(AbstractPartitionDiscovererTest.TEST_TOPIC)), deepClone(mockGetAllPartitionsForTopicsReturnSequence));
            open();
            List<KafkaTopicPartition> initialDiscoverySubtask0 = discoverPartitions();
            List<KafkaTopicPartition> initialDiscoverySubtask1 = discoverPartitions();
            List<KafkaTopicPartition> initialDiscoverySubtask2 = discoverPartitions();
            Assert.assertTrue(((initialDiscoverySubtask0.size()) >= minInitialPartitionsPerConsumer));
            Assert.assertTrue(((initialDiscoverySubtask0.size()) <= maxInitialPartitionsPerConsumer));
            Assert.assertTrue(((initialDiscoverySubtask1.size()) >= minInitialPartitionsPerConsumer));
            Assert.assertTrue(((initialDiscoverySubtask1.size()) <= maxInitialPartitionsPerConsumer));
            Assert.assertTrue(((initialDiscoverySubtask2.size()) >= minInitialPartitionsPerConsumer));
            Assert.assertTrue(((initialDiscoverySubtask2.size()) <= maxInitialPartitionsPerConsumer));
            for (KafkaTopicPartition p : initialDiscoverySubtask0) {
                // check that the element was actually contained
                Assert.assertTrue(allInitialPartitions.remove(p));
                Assert.assertEquals(AbstractPartitionDiscovererTest.getExpectedSubtaskIndex(p, startIndex, numConsumers), 0);
            }
            for (KafkaTopicPartition p : initialDiscoverySubtask1) {
                // check that the element was actually contained
                Assert.assertTrue(allInitialPartitions.remove(p));
                Assert.assertEquals(AbstractPartitionDiscovererTest.getExpectedSubtaskIndex(p, startIndex, numConsumers), 1);
            }
            for (KafkaTopicPartition p : initialDiscoverySubtask2) {
                // check that the element was actually contained
                Assert.assertTrue(allInitialPartitions.remove(p));
                Assert.assertEquals(AbstractPartitionDiscovererTest.getExpectedSubtaskIndex(p, startIndex, numConsumers), 2);
            }
            // all partitions must have been assigned
            Assert.assertTrue(allInitialPartitions.isEmpty());
            // now, execute discover again (should find the extra new partitions)
            List<KafkaTopicPartition> secondDiscoverySubtask0 = discoverPartitions();
            List<KafkaTopicPartition> secondDiscoverySubtask1 = discoverPartitions();
            List<KafkaTopicPartition> secondDiscoverySubtask2 = discoverPartitions();
            // new discovered partitions must not have been discovered before
            Assert.assertTrue(Collections.disjoint(secondDiscoverySubtask0, initialDiscoverySubtask0));
            Assert.assertTrue(Collections.disjoint(secondDiscoverySubtask1, initialDiscoverySubtask1));
            Assert.assertTrue(Collections.disjoint(secondDiscoverySubtask2, initialDiscoverySubtask2));
            Assert.assertTrue((((secondDiscoverySubtask0.size()) + (initialDiscoverySubtask0.size())) >= minNewPartitionsPerConsumer));
            Assert.assertTrue((((secondDiscoverySubtask0.size()) + (initialDiscoverySubtask0.size())) <= maxNewPartitionsPerConsumer));
            Assert.assertTrue((((secondDiscoverySubtask1.size()) + (initialDiscoverySubtask1.size())) >= minNewPartitionsPerConsumer));
            Assert.assertTrue((((secondDiscoverySubtask1.size()) + (initialDiscoverySubtask1.size())) <= maxNewPartitionsPerConsumer));
            Assert.assertTrue((((secondDiscoverySubtask2.size()) + (initialDiscoverySubtask2.size())) >= minNewPartitionsPerConsumer));
            Assert.assertTrue((((secondDiscoverySubtask2.size()) + (initialDiscoverySubtask2.size())) <= maxNewPartitionsPerConsumer));
            // check that the two discoveries combined form all partitions
            for (KafkaTopicPartition p : initialDiscoverySubtask0) {
                Assert.assertTrue(allNewPartitions.remove(p));
                Assert.assertEquals(AbstractPartitionDiscovererTest.getExpectedSubtaskIndex(p, startIndex, numConsumers), 0);
            }
            for (KafkaTopicPartition p : initialDiscoverySubtask1) {
                Assert.assertTrue(allNewPartitions.remove(p));
                Assert.assertEquals(AbstractPartitionDiscovererTest.getExpectedSubtaskIndex(p, startIndex, numConsumers), 1);
            }
            for (KafkaTopicPartition p : initialDiscoverySubtask2) {
                Assert.assertTrue(allNewPartitions.remove(p));
                Assert.assertEquals(AbstractPartitionDiscovererTest.getExpectedSubtaskIndex(p, startIndex, numConsumers), 2);
            }
            for (KafkaTopicPartition p : secondDiscoverySubtask0) {
                Assert.assertTrue(allNewPartitions.remove(p));
                Assert.assertEquals(AbstractPartitionDiscovererTest.getExpectedSubtaskIndex(p, startIndex, numConsumers), 0);
            }
            for (KafkaTopicPartition p : secondDiscoverySubtask1) {
                Assert.assertTrue(allNewPartitions.remove(p));
                Assert.assertEquals(AbstractPartitionDiscovererTest.getExpectedSubtaskIndex(p, startIndex, numConsumers), 1);
            }
            for (KafkaTopicPartition p : secondDiscoverySubtask2) {
                Assert.assertTrue(allNewPartitions.remove(p));
                Assert.assertEquals(AbstractPartitionDiscovererTest.getExpectedSubtaskIndex(p, startIndex, numConsumers), 2);
            }
            // all partitions must have been assigned
            Assert.assertTrue(allNewPartitions.isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testDeterministicAssignmentWithDifferentFetchedPartitionOrdering() throws Exception {
        int numSubtasks = 4;
        List<KafkaTopicPartition> mockGetAllPartitionsForTopicsReturn = Arrays.asList(new KafkaTopicPartition("test-topic", 0), new KafkaTopicPartition("test-topic", 1), new KafkaTopicPartition("test-topic", 2), new KafkaTopicPartition("test-topic", 3), new KafkaTopicPartition("test-topic2", 0), new KafkaTopicPartition("test-topic2", 1));
        List<KafkaTopicPartition> mockGetAllPartitionsForTopicsReturnOutOfOrder = Arrays.asList(new KafkaTopicPartition("test-topic", 3), new KafkaTopicPartition("test-topic", 1), new KafkaTopicPartition("test-topic2", 1), new KafkaTopicPartition("test-topic", 0), new KafkaTopicPartition("test-topic2", 0), new KafkaTopicPartition("test-topic", 2));
        for (int subtaskIndex = 0; subtaskIndex < numSubtasks; subtaskIndex++) {
            TestPartitionDiscoverer partitionDiscoverer = new TestPartitionDiscoverer(topicsDescriptor, subtaskIndex, numSubtasks, TestPartitionDiscoverer.createMockGetAllTopicsSequenceFromFixedReturn(Arrays.asList("test-topic", "test-topic2")), TestPartitionDiscoverer.createMockGetAllPartitionsFromTopicsSequenceFromFixedReturn(mockGetAllPartitionsForTopicsReturn));
            open();
            TestPartitionDiscoverer partitionDiscovererOutOfOrder = new TestPartitionDiscoverer(topicsDescriptor, subtaskIndex, numSubtasks, TestPartitionDiscoverer.createMockGetAllTopicsSequenceFromFixedReturn(Arrays.asList("test-topic", "test-topic2")), TestPartitionDiscoverer.createMockGetAllPartitionsFromTopicsSequenceFromFixedReturn(mockGetAllPartitionsForTopicsReturnOutOfOrder));
            open();
            List<KafkaTopicPartition> discoveredPartitions = discoverPartitions();
            List<KafkaTopicPartition> discoveredPartitionsOutOfOrder = discoverPartitions();
            // the subscribed partitions should be identical, regardless of the input partition ordering
            Collections.sort(discoveredPartitions, new KafkaTopicPartition.Comparator());
            Collections.sort(discoveredPartitionsOutOfOrder, new KafkaTopicPartition.Comparator());
            Assert.assertEquals(discoveredPartitions, discoveredPartitionsOutOfOrder);
        }
    }

    @Test
    public void testNonContiguousPartitionIdDiscovery() throws Exception {
        List<KafkaTopicPartition> mockGetAllPartitionsForTopicsReturn1 = Arrays.asList(new KafkaTopicPartition("test-topic", 1), new KafkaTopicPartition("test-topic", 4));
        List<KafkaTopicPartition> mockGetAllPartitionsForTopicsReturn2 = Arrays.asList(new KafkaTopicPartition("test-topic", 0), new KafkaTopicPartition("test-topic", 1), new KafkaTopicPartition("test-topic", 2), new KafkaTopicPartition("test-topic", 3), new KafkaTopicPartition("test-topic", 4));
        TestPartitionDiscoverer partitionDiscoverer = // first metadata fetch has missing partitions that appears only in the second fetch;
        // need to create new modifiable lists for each fetch, since internally Iterable.remove() is used.
        new TestPartitionDiscoverer(topicsDescriptor, 0, 1, TestPartitionDiscoverer.createMockGetAllTopicsSequenceFromFixedReturn(Collections.singletonList("test-topic")), Arrays.asList(new ArrayList(mockGetAllPartitionsForTopicsReturn1), new ArrayList(mockGetAllPartitionsForTopicsReturn2)));
        open();
        List<KafkaTopicPartition> discoveredPartitions1 = discoverPartitions();
        Assert.assertEquals(2, discoveredPartitions1.size());
        Assert.assertTrue(discoveredPartitions1.contains(new KafkaTopicPartition("test-topic", 1)));
        Assert.assertTrue(discoveredPartitions1.contains(new KafkaTopicPartition("test-topic", 4)));
        List<KafkaTopicPartition> discoveredPartitions2 = discoverPartitions();
        Assert.assertEquals(3, discoveredPartitions2.size());
        Assert.assertTrue(discoveredPartitions2.contains(new KafkaTopicPartition("test-topic", 0)));
        Assert.assertTrue(discoveredPartitions2.contains(new KafkaTopicPartition("test-topic", 2)));
        Assert.assertTrue(discoveredPartitions2.contains(new KafkaTopicPartition("test-topic", 3)));
    }
}

