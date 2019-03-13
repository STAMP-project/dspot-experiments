/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.streams.processor.internals;


import InternalTopologyBuilder.TopicsInfo;
import PartitionAssignor.Assignment;
import PartitionAssignor.Subscription;
import StreamsConfig.APPLICATION_SERVER_CONFIG;
import StreamsConfig.InternalConfig.ASSIGNMENT_ERROR_CODE;
import StreamsConfig.InternalConfig.TASK_MANAGER_FOR_PARTITION_ASSIGNOR;
import StreamsConfig.NUM_STANDBY_REPLICAS_CONFIG;
import StreamsConfig.PARTITION_GROUPER_CLASS_CONFIG;
import StreamsConfig.UPGRADE_FROM_0100;
import StreamsConfig.UPGRADE_FROM_0101;
import StreamsConfig.UPGRADE_FROM_0102;
import StreamsConfig.UPGRADE_FROM_0110;
import StreamsConfig.UPGRADE_FROM_10;
import StreamsConfig.UPGRADE_FROM_11;
import StreamsConfig.UPGRADE_FROM_CONFIG;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.apache.kafka.clients.consumer.internals.PartitionAssignor;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.common.utils.Utils;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TopologyWrapper;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.ValueJoiner;
import org.apache.kafka.streams.processor.TaskId;
import org.apache.kafka.streams.processor.internals.assignment.AssignmentInfo;
import org.apache.kafka.streams.processor.internals.assignment.SubscriptionInfo;
import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.test.MockClientSupplier;
import org.apache.kafka.test.MockInternalTopicManager;
import org.apache.kafka.test.MockKeyValueStoreBuilder;
import org.apache.kafka.test.MockProcessorSupplier;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("unchecked")
public class StreamsPartitionAssignorTest {
    private final TopicPartition t1p0 = new TopicPartition("topic1", 0);

    private final TopicPartition t1p1 = new TopicPartition("topic1", 1);

    private final TopicPartition t1p2 = new TopicPartition("topic1", 2);

    private final TopicPartition t1p3 = new TopicPartition("topic1", 3);

    private final TopicPartition t2p0 = new TopicPartition("topic2", 0);

    private final TopicPartition t2p1 = new TopicPartition("topic2", 1);

    private final TopicPartition t2p2 = new TopicPartition("topic2", 2);

    private final TopicPartition t2p3 = new TopicPartition("topic2", 3);

    private final TopicPartition t3p0 = new TopicPartition("topic3", 0);

    private final TopicPartition t3p1 = new TopicPartition("topic3", 1);

    private final TopicPartition t3p2 = new TopicPartition("topic3", 2);

    private final TopicPartition t3p3 = new TopicPartition("topic3", 3);

    private final Set<String> allTopics = Utils.mkSet("topic1", "topic2");

    private final List<PartitionInfo> infos = Arrays.asList(new PartitionInfo("topic1", 0, Node.noNode(), new Node[0], new Node[0]), new PartitionInfo("topic1", 1, Node.noNode(), new Node[0], new Node[0]), new PartitionInfo("topic1", 2, Node.noNode(), new Node[0], new Node[0]), new PartitionInfo("topic2", 0, Node.noNode(), new Node[0], new Node[0]), new PartitionInfo("topic2", 1, Node.noNode(), new Node[0], new Node[0]), new PartitionInfo("topic2", 2, Node.noNode(), new Node[0], new Node[0]), new PartitionInfo("topic3", 0, Node.noNode(), new Node[0], new Node[0]), new PartitionInfo("topic3", 1, Node.noNode(), new Node[0], new Node[0]), new PartitionInfo("topic3", 2, Node.noNode(), new Node[0], new Node[0]), new PartitionInfo("topic3", 3, Node.noNode(), new Node[0], new Node[0]));

    private final Cluster metadata = new Cluster("cluster", Collections.singletonList(Node.noNode()), infos, Collections.emptySet(), Collections.emptySet());

    private final TaskId task0 = new TaskId(0, 0);

    private final TaskId task1 = new TaskId(0, 1);

    private final TaskId task2 = new TaskId(0, 2);

    private final TaskId task3 = new TaskId(0, 3);

    private final StreamsPartitionAssignor partitionAssignor = new StreamsPartitionAssignor();

    private final MockClientSupplier mockClientSupplier = new MockClientSupplier();

    private final InternalTopologyBuilder builder = new InternalTopologyBuilder();

    private final StreamsConfig streamsConfig = new StreamsConfig(configProps());

    private final String userEndPoint = "localhost:8080";

    private final String applicationId = "stream-partition-assignor-test";

    private final TaskManager taskManager = EasyMock.createNiceMock(TaskManager.class);

    @Test
    public void shouldInterleaveTasksByGroupId() {
        final TaskId taskIdA0 = new TaskId(0, 0);
        final TaskId taskIdA1 = new TaskId(0, 1);
        final TaskId taskIdA2 = new TaskId(0, 2);
        final TaskId taskIdA3 = new TaskId(0, 3);
        final TaskId taskIdB0 = new TaskId(1, 0);
        final TaskId taskIdB1 = new TaskId(1, 1);
        final TaskId taskIdB2 = new TaskId(1, 2);
        final TaskId taskIdC0 = new TaskId(2, 0);
        final TaskId taskIdC1 = new TaskId(2, 1);
        final List<TaskId> expectedSubList1 = Arrays.asList(taskIdA0, taskIdA3, taskIdB2);
        final List<TaskId> expectedSubList2 = Arrays.asList(taskIdA1, taskIdB0, taskIdC0);
        final List<TaskId> expectedSubList3 = Arrays.asList(taskIdA2, taskIdB1, taskIdC1);
        final List<List<TaskId>> embeddedList = Arrays.asList(expectedSubList1, expectedSubList2, expectedSubList3);
        final List<TaskId> tasks = Arrays.asList(taskIdC0, taskIdC1, taskIdB0, taskIdB1, taskIdB2, taskIdA0, taskIdA1, taskIdA2, taskIdA3);
        Collections.shuffle(tasks);
        final List<List<TaskId>> interleavedTaskIds = partitionAssignor.interleaveTasksByGroupId(tasks, 3);
        MatcherAssert.assertThat(interleavedTaskIds, CoreMatchers.equalTo(embeddedList));
    }

    @Test
    public void testSubscription() {
        builder.addSource(null, "source1", null, null, null, "topic1");
        builder.addSource(null, "source2", null, null, null, "topic2");
        builder.addProcessor("processor", new MockProcessorSupplier(), "source1", "source2");
        final Set<TaskId> prevTasks = Utils.mkSet(new TaskId(0, 1), new TaskId(1, 1), new TaskId(2, 1));
        final Set<TaskId> cachedTasks = Utils.mkSet(new TaskId(0, 1), new TaskId(1, 1), new TaskId(2, 1), new TaskId(0, 2), new TaskId(1, 2), new TaskId(2, 2));
        final UUID processId = UUID.randomUUID();
        mockTaskManager(prevTasks, cachedTasks, processId, builder);
        configurePartitionAssignor(Collections.emptyMap());
        final PartitionAssignor.Subscription subscription = partitionAssignor.subscription(Utils.mkSet("topic1", "topic2"));
        Collections.sort(subscription.topics());
        Assert.assertEquals(Arrays.asList("topic1", "topic2"), subscription.topics());
        final Set<TaskId> standbyTasks = new HashSet(cachedTasks);
        standbyTasks.removeAll(prevTasks);
        final SubscriptionInfo info = new SubscriptionInfo(processId, prevTasks, standbyTasks, null);
        Assert.assertEquals(info.encode(), subscription.userData());
    }

    @Test
    public void testAssignBasic() {
        builder.addSource(null, "source1", null, null, null, "topic1");
        builder.addSource(null, "source2", null, null, null, "topic2");
        builder.addProcessor("processor", new MockProcessorSupplier(), "source1", "source2");
        final List<String> topics = Arrays.asList("topic1", "topic2");
        final Set<TaskId> allTasks = Utils.mkSet(task0, task1, task2);
        final Set<TaskId> prevTasks10 = Utils.mkSet(task0);
        final Set<TaskId> prevTasks11 = Utils.mkSet(task1);
        final Set<TaskId> prevTasks20 = Utils.mkSet(task2);
        final Set<TaskId> standbyTasks10 = Utils.mkSet(task1);
        final Set<TaskId> standbyTasks11 = Utils.mkSet(task2);
        final Set<TaskId> standbyTasks20 = Utils.mkSet(task0);
        final UUID uuid1 = UUID.randomUUID();
        final UUID uuid2 = UUID.randomUUID();
        mockTaskManager(prevTasks10, standbyTasks10, uuid1, builder);
        configurePartitionAssignor(Collections.emptyMap());
        partitionAssignor.setInternalTopicManager(new MockInternalTopicManager(streamsConfig, mockClientSupplier.restoreConsumer));
        final Map<String, PartitionAssignor.Subscription> subscriptions = new HashMap<>();
        subscriptions.put("consumer10", new PartitionAssignor.Subscription(topics, encode()));
        subscriptions.put("consumer11", new PartitionAssignor.Subscription(topics, encode()));
        subscriptions.put("consumer20", new PartitionAssignor.Subscription(topics, encode()));
        final Map<String, PartitionAssignor.Assignment> assignments = partitionAssignor.assign(metadata, subscriptions);
        // check assigned partitions
        Assert.assertEquals(Utils.mkSet(Utils.mkSet(t1p0, t2p0), Utils.mkSet(t1p1, t2p1)), Utils.mkSet(new HashSet(assignments.get("consumer10").partitions()), new HashSet(assignments.get("consumer11").partitions())));
        Assert.assertEquals(Utils.mkSet(t1p2, t2p2), new HashSet(assignments.get("consumer20").partitions()));
        // check assignment info
        // the first consumer
        final AssignmentInfo info10 = checkAssignment(allTopics, assignments.get("consumer10"));
        final Set<TaskId> allActiveTasks = new HashSet(info10.activeTasks());
        // the second consumer
        final AssignmentInfo info11 = checkAssignment(allTopics, assignments.get("consumer11"));
        allActiveTasks.addAll(info11.activeTasks());
        Assert.assertEquals(Utils.mkSet(task0, task1), allActiveTasks);
        // the third consumer
        final AssignmentInfo info20 = checkAssignment(allTopics, assignments.get("consumer20"));
        allActiveTasks.addAll(info20.activeTasks());
        Assert.assertEquals(3, allActiveTasks.size());
        Assert.assertEquals(allTasks, new HashSet(allActiveTasks));
        Assert.assertEquals(3, allActiveTasks.size());
        Assert.assertEquals(allTasks, allActiveTasks);
    }

    @Test
    public void shouldAssignEvenlyAcrossConsumersOneClientMultipleThreads() {
        builder.addSource(null, "source1", null, null, null, "topic1");
        builder.addSource(null, "source2", null, null, null, "topic2");
        builder.addProcessor("processor", new MockProcessorSupplier(), "source1");
        builder.addProcessor("processorII", new MockProcessorSupplier(), "source2");
        final List<PartitionInfo> localInfos = Arrays.asList(new PartitionInfo("topic1", 0, Node.noNode(), new Node[0], new Node[0]), new PartitionInfo("topic1", 1, Node.noNode(), new Node[0], new Node[0]), new PartitionInfo("topic1", 2, Node.noNode(), new Node[0], new Node[0]), new PartitionInfo("topic1", 3, Node.noNode(), new Node[0], new Node[0]), new PartitionInfo("topic2", 0, Node.noNode(), new Node[0], new Node[0]), new PartitionInfo("topic2", 1, Node.noNode(), new Node[0], new Node[0]), new PartitionInfo("topic2", 2, Node.noNode(), new Node[0], new Node[0]), new PartitionInfo("topic2", 3, Node.noNode(), new Node[0], new Node[0]));
        final Cluster localMetadata = new Cluster("cluster", Collections.singletonList(Node.noNode()), localInfos, Collections.emptySet(), Collections.emptySet());
        final List<String> topics = Arrays.asList("topic1", "topic2");
        final TaskId taskIdA0 = new TaskId(0, 0);
        final TaskId taskIdA1 = new TaskId(0, 1);
        final TaskId taskIdA2 = new TaskId(0, 2);
        final TaskId taskIdA3 = new TaskId(0, 3);
        final TaskId taskIdB0 = new TaskId(1, 0);
        final TaskId taskIdB1 = new TaskId(1, 1);
        final TaskId taskIdB2 = new TaskId(1, 2);
        final TaskId taskIdB3 = new TaskId(1, 3);
        final UUID uuid1 = UUID.randomUUID();
        mockTaskManager(new HashSet(), new HashSet(), uuid1, builder);
        configurePartitionAssignor(Collections.emptyMap());
        partitionAssignor.setInternalTopicManager(new MockInternalTopicManager(streamsConfig, mockClientSupplier.restoreConsumer));
        final Map<String, PartitionAssignor.Subscription> subscriptions = new HashMap<>();
        subscriptions.put("consumer10", new PartitionAssignor.Subscription(topics, new SubscriptionInfo(uuid1, new HashSet(), new HashSet(), userEndPoint).encode()));
        subscriptions.put("consumer11", new PartitionAssignor.Subscription(topics, new SubscriptionInfo(uuid1, new HashSet(), new HashSet(), userEndPoint).encode()));
        final Map<String, PartitionAssignor.Assignment> assignments = partitionAssignor.assign(localMetadata, subscriptions);
        // check assigned partitions
        Assert.assertEquals(Utils.mkSet(Utils.mkSet(t2p2, t1p0, t1p2, t2p0), Utils.mkSet(t1p1, t2p1, t1p3, t2p3)), Utils.mkSet(new HashSet(assignments.get("consumer10").partitions()), new HashSet(assignments.get("consumer11").partitions())));
        // the first consumer
        final AssignmentInfo info10 = AssignmentInfo.decode(assignments.get("consumer10").userData());
        final List<TaskId> expectedInfo10TaskIds = Arrays.asList(taskIdA1, taskIdA3, taskIdB1, taskIdB3);
        Assert.assertEquals(expectedInfo10TaskIds, info10.activeTasks());
        // the second consumer
        final AssignmentInfo info11 = AssignmentInfo.decode(assignments.get("consumer11").userData());
        final List<TaskId> expectedInfo11TaskIds = Arrays.asList(taskIdA0, taskIdA2, taskIdB0, taskIdB2);
        Assert.assertEquals(expectedInfo11TaskIds, info11.activeTasks());
    }

    @Test
    public void testAssignWithPartialTopology() {
        builder.addSource(null, "source1", null, null, null, "topic1");
        builder.addProcessor("processor1", new MockProcessorSupplier(), "source1");
        builder.addStateStore(new MockKeyValueStoreBuilder("store1", false), "processor1");
        builder.addSource(null, "source2", null, null, null, "topic2");
        builder.addProcessor("processor2", new MockProcessorSupplier(), "source2");
        builder.addStateStore(new MockKeyValueStoreBuilder("store2", false), "processor2");
        final List<String> topics = Arrays.asList("topic1", "topic2");
        final Set<TaskId> allTasks = Utils.mkSet(task0, task1, task2);
        final UUID uuid1 = UUID.randomUUID();
        mockTaskManager(Collections.emptySet(), Collections.emptySet(), uuid1, builder);
        configurePartitionAssignor(Collections.singletonMap(PARTITION_GROUPER_CLASS_CONFIG, SingleGroupPartitionGrouperStub.class));
        partitionAssignor.setInternalTopicManager(new MockInternalTopicManager(streamsConfig, mockClientSupplier.restoreConsumer));
        final Map<String, PartitionAssignor.Subscription> subscriptions = new HashMap<>();
        subscriptions.put("consumer10", new PartitionAssignor.Subscription(topics, new SubscriptionInfo(uuid1, Collections.emptySet(), Collections.emptySet(), userEndPoint).encode()));
        // will throw exception if it fails
        final Map<String, PartitionAssignor.Assignment> assignments = partitionAssignor.assign(metadata, subscriptions);
        // check assignment info
        final AssignmentInfo info10 = checkAssignment(Utils.mkSet("topic1"), assignments.get("consumer10"));
        final Set<TaskId> allActiveTasks = new HashSet(info10.activeTasks());
        Assert.assertEquals(3, allActiveTasks.size());
        Assert.assertEquals(allTasks, new HashSet(allActiveTasks));
    }

    @Test
    public void testAssignEmptyMetadata() {
        builder.addSource(null, "source1", null, null, null, "topic1");
        builder.addSource(null, "source2", null, null, null, "topic2");
        builder.addProcessor("processor", new MockProcessorSupplier(), "source1", "source2");
        final List<String> topics = Arrays.asList("topic1", "topic2");
        final Set<TaskId> allTasks = Utils.mkSet(task0, task1, task2);
        final Set<TaskId> prevTasks10 = Utils.mkSet(task0);
        final Set<TaskId> standbyTasks10 = Utils.mkSet(task1);
        final Cluster emptyMetadata = new Cluster("cluster", Collections.singletonList(Node.noNode()), Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
        final UUID uuid1 = UUID.randomUUID();
        mockTaskManager(prevTasks10, standbyTasks10, uuid1, builder);
        configurePartitionAssignor(Collections.emptyMap());
        final Map<String, PartitionAssignor.Subscription> subscriptions = new HashMap<>();
        subscriptions.put("consumer10", new PartitionAssignor.Subscription(topics, encode()));
        // initially metadata is empty
        Map<String, PartitionAssignor.Assignment> assignments = partitionAssignor.assign(emptyMetadata, subscriptions);
        // check assigned partitions
        Assert.assertEquals(Collections.emptySet(), new HashSet(assignments.get("consumer10").partitions()));
        // check assignment info
        AssignmentInfo info10 = checkAssignment(Collections.emptySet(), assignments.get("consumer10"));
        final Set<TaskId> allActiveTasks = new HashSet(info10.activeTasks());
        Assert.assertEquals(0, allActiveTasks.size());
        Assert.assertEquals(Collections.emptySet(), new HashSet(allActiveTasks));
        // then metadata gets populated
        assignments = partitionAssignor.assign(metadata, subscriptions);
        // check assigned partitions
        Assert.assertEquals(Utils.mkSet(Utils.mkSet(t1p0, t2p0, t1p0, t2p0, t1p1, t2p1, t1p2, t2p2)), Utils.mkSet(new HashSet(assignments.get("consumer10").partitions())));
        // the first consumer
        info10 = checkAssignment(allTopics, assignments.get("consumer10"));
        allActiveTasks.addAll(info10.activeTasks());
        Assert.assertEquals(3, allActiveTasks.size());
        Assert.assertEquals(allTasks, new HashSet(allActiveTasks));
        Assert.assertEquals(3, allActiveTasks.size());
        Assert.assertEquals(allTasks, allActiveTasks);
    }

    @Test
    public void testAssignWithNewTasks() {
        builder.addSource(null, "source1", null, null, null, "topic1");
        builder.addSource(null, "source2", null, null, null, "topic2");
        builder.addSource(null, "source3", null, null, null, "topic3");
        builder.addProcessor("processor", new MockProcessorSupplier(), "source1", "source2", "source3");
        final List<String> topics = Arrays.asList("topic1", "topic2", "topic3");
        final Set<TaskId> allTasks = Utils.mkSet(task0, task1, task2, task3);
        // assuming that previous tasks do not have topic3
        final Set<TaskId> prevTasks10 = Utils.mkSet(task0);
        final Set<TaskId> prevTasks11 = Utils.mkSet(task1);
        final Set<TaskId> prevTasks20 = Utils.mkSet(task2);
        final UUID uuid1 = UUID.randomUUID();
        final UUID uuid2 = UUID.randomUUID();
        mockTaskManager(prevTasks10, Collections.emptySet(), uuid1, builder);
        configurePartitionAssignor(Collections.emptyMap());
        partitionAssignor.setInternalTopicManager(new MockInternalTopicManager(streamsConfig, mockClientSupplier.restoreConsumer));
        final Map<String, PartitionAssignor.Subscription> subscriptions = new HashMap<>();
        subscriptions.put("consumer10", new PartitionAssignor.Subscription(topics, encode()));
        subscriptions.put("consumer11", new PartitionAssignor.Subscription(topics, encode()));
        subscriptions.put("consumer20", new PartitionAssignor.Subscription(topics, encode()));
        final Map<String, PartitionAssignor.Assignment> assignments = partitionAssignor.assign(metadata, subscriptions);
        // check assigned partitions: since there is no previous task for topic 3 it will be assigned randomly so we cannot check exact match
        // also note that previously assigned partitions / tasks may not stay on the previous host since we may assign the new task first and
        // then later ones will be re-assigned to other hosts due to load balancing
        AssignmentInfo info = AssignmentInfo.decode(assignments.get("consumer10").userData());
        final Set<TaskId> allActiveTasks = new HashSet(info.activeTasks());
        final Set<TopicPartition> allPartitions = new HashSet(assignments.get("consumer10").partitions());
        info = AssignmentInfo.decode(assignments.get("consumer11").userData());
        allActiveTasks.addAll(info.activeTasks());
        allPartitions.addAll(assignments.get("consumer11").partitions());
        info = AssignmentInfo.decode(assignments.get("consumer20").userData());
        allActiveTasks.addAll(info.activeTasks());
        allPartitions.addAll(assignments.get("consumer20").partitions());
        Assert.assertEquals(allTasks, allActiveTasks);
        Assert.assertEquals(Utils.mkSet(t1p0, t1p1, t1p2, t2p0, t2p1, t2p2, t3p0, t3p1, t3p2, t3p3), allPartitions);
    }

    @Test
    public void testAssignWithStates() {
        builder.setApplicationId(applicationId);
        builder.addSource(null, "source1", null, null, null, "topic1");
        builder.addSource(null, "source2", null, null, null, "topic2");
        builder.addProcessor("processor-1", new MockProcessorSupplier(), "source1");
        builder.addStateStore(new MockKeyValueStoreBuilder("store1", false), "processor-1");
        builder.addProcessor("processor-2", new MockProcessorSupplier(), "source2");
        builder.addStateStore(new MockKeyValueStoreBuilder("store2", false), "processor-2");
        builder.addStateStore(new MockKeyValueStoreBuilder("store3", false), "processor-2");
        final List<String> topics = Arrays.asList("topic1", "topic2");
        final TaskId task00 = new TaskId(0, 0);
        final TaskId task01 = new TaskId(0, 1);
        final TaskId task02 = new TaskId(0, 2);
        final TaskId task10 = new TaskId(1, 0);
        final TaskId task11 = new TaskId(1, 1);
        final TaskId task12 = new TaskId(1, 2);
        final List<TaskId> tasks = Arrays.asList(task00, task01, task02, task10, task11, task12);
        final UUID uuid1 = UUID.randomUUID();
        final UUID uuid2 = UUID.randomUUID();
        mockTaskManager(Collections.emptySet(), Collections.emptySet(), uuid1, builder);
        configurePartitionAssignor(Collections.emptyMap());
        partitionAssignor.setInternalTopicManager(new MockInternalTopicManager(streamsConfig, mockClientSupplier.restoreConsumer));
        final Map<String, PartitionAssignor.Subscription> subscriptions = new HashMap<>();
        subscriptions.put("consumer10", new PartitionAssignor.Subscription(topics, new SubscriptionInfo(uuid1, Collections.emptySet(), Collections.emptySet(), userEndPoint).encode()));
        subscriptions.put("consumer11", new PartitionAssignor.Subscription(topics, new SubscriptionInfo(uuid1, Collections.emptySet(), Collections.emptySet(), userEndPoint).encode()));
        subscriptions.put("consumer20", new PartitionAssignor.Subscription(topics, new SubscriptionInfo(uuid2, Collections.emptySet(), Collections.emptySet(), userEndPoint).encode()));
        final Map<String, PartitionAssignor.Assignment> assignments = partitionAssignor.assign(metadata, subscriptions);
        // check assigned partition size: since there is no previous task and there are two sub-topologies the assignment is random so we cannot check exact match
        Assert.assertEquals(2, assignments.get("consumer10").partitions().size());
        Assert.assertEquals(2, assignments.get("consumer11").partitions().size());
        Assert.assertEquals(2, assignments.get("consumer20").partitions().size());
        final AssignmentInfo info10 = AssignmentInfo.decode(assignments.get("consumer10").userData());
        final AssignmentInfo info11 = AssignmentInfo.decode(assignments.get("consumer11").userData());
        final AssignmentInfo info20 = AssignmentInfo.decode(assignments.get("consumer20").userData());
        Assert.assertEquals(2, info10.activeTasks().size());
        Assert.assertEquals(2, info11.activeTasks().size());
        Assert.assertEquals(2, info20.activeTasks().size());
        final Set<TaskId> allTasks = new HashSet<>();
        allTasks.addAll(info10.activeTasks());
        allTasks.addAll(info11.activeTasks());
        allTasks.addAll(info20.activeTasks());
        Assert.assertEquals(new HashSet(tasks), allTasks);
        // check tasks for state topics
        final Map<Integer, InternalTopologyBuilder.TopicsInfo> topicGroups = builder.topicGroups();
        Assert.assertEquals(Utils.mkSet(task00, task01, task02), tasksForState("store1", tasks, topicGroups));
        Assert.assertEquals(Utils.mkSet(task10, task11, task12), tasksForState("store2", tasks, topicGroups));
        Assert.assertEquals(Utils.mkSet(task10, task11, task12), tasksForState("store3", tasks, topicGroups));
    }

    @Test
    public void testAssignWithStandbyReplicas() {
        final Map<String, Object> props = configProps();
        props.put(NUM_STANDBY_REPLICAS_CONFIG, "1");
        final StreamsConfig streamsConfig = new StreamsConfig(props);
        builder.addSource(null, "source1", null, null, null, "topic1");
        builder.addSource(null, "source2", null, null, null, "topic2");
        builder.addProcessor("processor", new MockProcessorSupplier(), "source1", "source2");
        final List<String> topics = Arrays.asList("topic1", "topic2");
        final Set<TaskId> allTasks = Utils.mkSet(task0, task1, task2);
        final Set<TaskId> prevTasks00 = Utils.mkSet(task0);
        final Set<TaskId> prevTasks01 = Utils.mkSet(task1);
        final Set<TaskId> prevTasks02 = Utils.mkSet(task2);
        final Set<TaskId> standbyTasks01 = Utils.mkSet(task1);
        final Set<TaskId> standbyTasks02 = Utils.mkSet(task2);
        final Set<TaskId> standbyTasks00 = Utils.mkSet(task0);
        final UUID uuid1 = UUID.randomUUID();
        final UUID uuid2 = UUID.randomUUID();
        mockTaskManager(prevTasks00, standbyTasks01, uuid1, builder);
        configurePartitionAssignor(Collections.singletonMap(NUM_STANDBY_REPLICAS_CONFIG, 1));
        partitionAssignor.setInternalTopicManager(new MockInternalTopicManager(streamsConfig, mockClientSupplier.restoreConsumer));
        final Map<String, PartitionAssignor.Subscription> subscriptions = new HashMap<>();
        subscriptions.put("consumer10", new PartitionAssignor.Subscription(topics, encode()));
        subscriptions.put("consumer11", new PartitionAssignor.Subscription(topics, encode()));
        subscriptions.put("consumer20", new PartitionAssignor.Subscription(topics, encode()));
        final Map<String, PartitionAssignor.Assignment> assignments = partitionAssignor.assign(metadata, subscriptions);
        // the first consumer
        final AssignmentInfo info10 = checkAssignment(allTopics, assignments.get("consumer10"));
        final Set<TaskId> allActiveTasks = new HashSet(info10.activeTasks());
        final Set<TaskId> allStandbyTasks = new HashSet(info10.standbyTasks().keySet());
        // the second consumer
        final AssignmentInfo info11 = checkAssignment(allTopics, assignments.get("consumer11"));
        allActiveTasks.addAll(info11.activeTasks());
        allStandbyTasks.addAll(info11.standbyTasks().keySet());
        Assert.assertNotEquals("same processId has same set of standby tasks", info11.standbyTasks().keySet(), info10.standbyTasks().keySet());
        // check active tasks assigned to the first client
        Assert.assertEquals(Utils.mkSet(task0, task1), new HashSet(allActiveTasks));
        Assert.assertEquals(Utils.mkSet(task2), new HashSet(allStandbyTasks));
        // the third consumer
        final AssignmentInfo info20 = checkAssignment(allTopics, assignments.get("consumer20"));
        allActiveTasks.addAll(info20.activeTasks());
        allStandbyTasks.addAll(info20.standbyTasks().keySet());
        // all task ids are in the active tasks and also in the standby tasks
        Assert.assertEquals(3, allActiveTasks.size());
        Assert.assertEquals(allTasks, allActiveTasks);
        Assert.assertEquals(3, allStandbyTasks.size());
        Assert.assertEquals(allTasks, allStandbyTasks);
    }

    @Test
    public void testOnAssignment() {
        configurePartitionAssignor(Collections.emptyMap());
        final List<TaskId> activeTaskList = Arrays.asList(task0, task3);
        final Map<TaskId, Set<TopicPartition>> activeTasks = new HashMap<>();
        final Map<TaskId, Set<TopicPartition>> standbyTasks = new HashMap<>();
        final Map<HostInfo, Set<TopicPartition>> hostState = Collections.singletonMap(new HostInfo("localhost", 9090), Utils.mkSet(t3p0, t3p3));
        activeTasks.put(task0, Utils.mkSet(t3p0));
        activeTasks.put(task3, Utils.mkSet(t3p3));
        standbyTasks.put(task1, Utils.mkSet(t3p1));
        standbyTasks.put(task2, Utils.mkSet(t3p2));
        final AssignmentInfo info = new AssignmentInfo(activeTaskList, standbyTasks, hostState);
        final PartitionAssignor.Assignment assignment = new PartitionAssignor.Assignment(Arrays.asList(t3p0, t3p3), info.encode());
        final Capture<Cluster> capturedCluster = EasyMock.newCapture();
        taskManager.setPartitionsByHostState(hostState);
        EasyMock.expectLastCall();
        taskManager.setAssignmentMetadata(activeTasks, standbyTasks);
        EasyMock.expectLastCall();
        taskManager.setClusterMetadata(EasyMock.capture(capturedCluster));
        EasyMock.expectLastCall();
        EasyMock.replay(taskManager);
        partitionAssignor.onAssignment(assignment);
        EasyMock.verify(taskManager);
        Assert.assertEquals(Collections.singleton(t3p0.topic()), capturedCluster.getValue().topics());
        Assert.assertEquals(2, capturedCluster.getValue().partitionsForTopic(t3p0.topic()).size());
    }

    @Test
    public void testAssignWithInternalTopics() {
        builder.setApplicationId(applicationId);
        builder.addInternalTopic("topicX");
        builder.addSource(null, "source1", null, null, null, "topic1");
        builder.addProcessor("processor1", new MockProcessorSupplier(), "source1");
        builder.addSink("sink1", "topicX", null, null, null, "processor1");
        builder.addSource(null, "source2", null, null, null, "topicX");
        builder.addProcessor("processor2", new MockProcessorSupplier(), "source2");
        final List<String> topics = Arrays.asList("topic1", ((applicationId) + "-topicX"));
        final Set<TaskId> allTasks = Utils.mkSet(task0, task1, task2);
        final UUID uuid1 = UUID.randomUUID();
        mockTaskManager(Collections.emptySet(), Collections.emptySet(), uuid1, builder);
        configurePartitionAssignor(Collections.emptyMap());
        final MockInternalTopicManager internalTopicManager = new MockInternalTopicManager(streamsConfig, mockClientSupplier.restoreConsumer);
        partitionAssignor.setInternalTopicManager(internalTopicManager);
        final Map<String, PartitionAssignor.Subscription> subscriptions = new HashMap<>();
        final Set<TaskId> emptyTasks = Collections.emptySet();
        subscriptions.put("consumer10", new PartitionAssignor.Subscription(topics, encode()));
        partitionAssignor.assign(metadata, subscriptions);
        // check prepared internal topics
        Assert.assertEquals(1, internalTopicManager.readyTopics.size());
        Assert.assertEquals(allTasks.size(), ((long) (internalTopicManager.readyTopics.get(((applicationId) + "-topicX")))));
    }

    @Test
    public void testAssignWithInternalTopicThatsSourceIsAnotherInternalTopic() {
        final String applicationId = "test";
        builder.setApplicationId(applicationId);
        builder.addInternalTopic("topicX");
        builder.addSource(null, "source1", null, null, null, "topic1");
        builder.addProcessor("processor1", new MockProcessorSupplier(), "source1");
        builder.addSink("sink1", "topicX", null, null, null, "processor1");
        builder.addSource(null, "source2", null, null, null, "topicX");
        builder.addInternalTopic("topicZ");
        builder.addProcessor("processor2", new MockProcessorSupplier(), "source2");
        builder.addSink("sink2", "topicZ", null, null, null, "processor2");
        builder.addSource(null, "source3", null, null, null, "topicZ");
        final List<String> topics = Arrays.asList("topic1", "test-topicX", "test-topicZ");
        final Set<TaskId> allTasks = Utils.mkSet(task0, task1, task2);
        final UUID uuid1 = UUID.randomUUID();
        mockTaskManager(Collections.emptySet(), Collections.emptySet(), uuid1, builder);
        configurePartitionAssignor(Collections.emptyMap());
        final MockInternalTopicManager internalTopicManager = new MockInternalTopicManager(streamsConfig, mockClientSupplier.restoreConsumer);
        partitionAssignor.setInternalTopicManager(internalTopicManager);
        final Map<String, PartitionAssignor.Subscription> subscriptions = new HashMap<>();
        final Set<TaskId> emptyTasks = Collections.emptySet();
        subscriptions.put("consumer10", new PartitionAssignor.Subscription(topics, encode()));
        partitionAssignor.assign(metadata, subscriptions);
        // check prepared internal topics
        Assert.assertEquals(2, internalTopicManager.readyTopics.size());
        Assert.assertEquals(allTasks.size(), ((long) (internalTopicManager.readyTopics.get("test-topicZ"))));
    }

    @Test
    public void shouldGenerateTasksForAllCreatedPartitions() {
        final StreamsBuilder builder = new StreamsBuilder();
        // KStream with 3 partitions
        final KStream<Object, Object> stream1 = // force creation of internal repartition topic
        builder.stream("topic1").map(((KeyValueMapper<Object, Object, KeyValue<Object, Object>>) (KeyValue::new)));
        // KTable with 4 partitions
        final KTable<Object, Long> table1 = // force creation of internal repartition topic
        builder.table("topic3").groupBy(KeyValue::new).count();
        // joining the stream and the table
        // this triggers the enforceCopartitioning() routine in the StreamsPartitionAssignor,
        // forcing the stream.map to get repartitioned to a topic with four partitions.
        stream1.join(table1, ((ValueJoiner) (( value1, value2) -> null)));
        final UUID uuid = UUID.randomUUID();
        final String client = "client1";
        final InternalTopologyBuilder internalTopologyBuilder = TopologyWrapper.getInternalTopologyBuilder(builder.build());
        internalTopologyBuilder.setApplicationId(applicationId);
        mockTaskManager(Collections.emptySet(), Collections.emptySet(), UUID.randomUUID(), internalTopologyBuilder);
        configurePartitionAssignor(Collections.emptyMap());
        final MockInternalTopicManager mockInternalTopicManager = new MockInternalTopicManager(streamsConfig, mockClientSupplier.restoreConsumer);
        partitionAssignor.setInternalTopicManager(mockInternalTopicManager);
        final Map<String, PartitionAssignor.Subscription> subscriptions = new HashMap<>();
        final Set<TaskId> emptyTasks = Collections.emptySet();
        subscriptions.put(client, new PartitionAssignor.Subscription(Arrays.asList("topic1", "topic3"), encode()));
        final Map<String, PartitionAssignor.Assignment> assignment = partitionAssignor.assign(metadata, subscriptions);
        final Map<String, Integer> expectedCreatedInternalTopics = new HashMap<>();
        expectedCreatedInternalTopics.put(((applicationId) + "-KTABLE-AGGREGATE-STATE-STORE-0000000006-repartition"), 4);
        expectedCreatedInternalTopics.put(((applicationId) + "-KTABLE-AGGREGATE-STATE-STORE-0000000006-changelog"), 4);
        expectedCreatedInternalTopics.put(((applicationId) + "-topic3-STATE-STORE-0000000002-changelog"), 4);
        expectedCreatedInternalTopics.put(((applicationId) + "-KSTREAM-MAP-0000000001-repartition"), 4);
        // check if all internal topics were created as expected
        MatcherAssert.assertThat(mockInternalTopicManager.readyTopics, CoreMatchers.equalTo(expectedCreatedInternalTopics));
        final List<TopicPartition> expectedAssignment = Arrays.asList(new TopicPartition("topic1", 0), new TopicPartition("topic1", 1), new TopicPartition("topic1", 2), new TopicPartition("topic3", 0), new TopicPartition("topic3", 1), new TopicPartition("topic3", 2), new TopicPartition("topic3", 3), new TopicPartition(((applicationId) + "-KTABLE-AGGREGATE-STATE-STORE-0000000006-repartition"), 0), new TopicPartition(((applicationId) + "-KTABLE-AGGREGATE-STATE-STORE-0000000006-repartition"), 1), new TopicPartition(((applicationId) + "-KTABLE-AGGREGATE-STATE-STORE-0000000006-repartition"), 2), new TopicPartition(((applicationId) + "-KTABLE-AGGREGATE-STATE-STORE-0000000006-repartition"), 3), new TopicPartition(((applicationId) + "-KSTREAM-MAP-0000000001-repartition"), 0), new TopicPartition(((applicationId) + "-KSTREAM-MAP-0000000001-repartition"), 1), new TopicPartition(((applicationId) + "-KSTREAM-MAP-0000000001-repartition"), 2), new TopicPartition(((applicationId) + "-KSTREAM-MAP-0000000001-repartition"), 3));
        // check if we created a task for all expected topicPartitions.
        MatcherAssert.assertThat(new HashSet(assignment.get(client).partitions()), CoreMatchers.equalTo(new HashSet(expectedAssignment)));
    }

    @Test
    public void shouldAddUserDefinedEndPointToSubscription() {
        builder.setApplicationId(applicationId);
        builder.addSource(null, "source", null, null, null, "input");
        builder.addProcessor("processor", new MockProcessorSupplier(), "source");
        builder.addSink("sink", "output", null, null, null, "processor");
        final UUID uuid1 = UUID.randomUUID();
        mockTaskManager(Collections.emptySet(), Collections.emptySet(), uuid1, builder);
        configurePartitionAssignor(Collections.singletonMap(APPLICATION_SERVER_CONFIG, userEndPoint));
        final PartitionAssignor.Subscription subscription = partitionAssignor.subscription(Utils.mkSet("input"));
        final SubscriptionInfo subscriptionInfo = SubscriptionInfo.decode(subscription.userData());
        Assert.assertEquals("localhost:8080", subscriptionInfo.userEndPoint());
    }

    @Test
    public void shouldMapUserEndPointToTopicPartitions() {
        builder.setApplicationId(applicationId);
        builder.addSource(null, "source", null, null, null, "topic1");
        builder.addProcessor("processor", new MockProcessorSupplier(), "source");
        builder.addSink("sink", "output", null, null, null, "processor");
        final List<String> topics = Collections.singletonList("topic1");
        final UUID uuid1 = UUID.randomUUID();
        mockTaskManager(Collections.emptySet(), Collections.emptySet(), uuid1, builder);
        configurePartitionAssignor(Collections.singletonMap(APPLICATION_SERVER_CONFIG, userEndPoint));
        partitionAssignor.setInternalTopicManager(new MockInternalTopicManager(streamsConfig, mockClientSupplier.restoreConsumer));
        final Map<String, PartitionAssignor.Subscription> subscriptions = new HashMap<>();
        final Set<TaskId> emptyTasks = Collections.emptySet();
        subscriptions.put("consumer1", new PartitionAssignor.Subscription(topics, encode()));
        final Map<String, PartitionAssignor.Assignment> assignments = partitionAssignor.assign(metadata, subscriptions);
        final PartitionAssignor.Assignment consumerAssignment = assignments.get("consumer1");
        final AssignmentInfo assignmentInfo = AssignmentInfo.decode(consumerAssignment.userData());
        final Set<TopicPartition> topicPartitions = assignmentInfo.partitionsByHost().get(new HostInfo("localhost", 8080));
        Assert.assertEquals(Utils.mkSet(new TopicPartition("topic1", 0), new TopicPartition("topic1", 1), new TopicPartition("topic1", 2)), topicPartitions);
    }

    @Test
    public void shouldThrowExceptionIfApplicationServerConfigIsNotHostPortPair() {
        builder.setApplicationId(applicationId);
        mockTaskManager(Collections.emptySet(), Collections.emptySet(), UUID.randomUUID(), builder);
        partitionAssignor.setInternalTopicManager(new MockInternalTopicManager(streamsConfig, mockClientSupplier.restoreConsumer));
        try {
            configurePartitionAssignor(Collections.singletonMap(APPLICATION_SERVER_CONFIG, "localhost"));
            Assert.fail("expected to an exception due to invalid config");
        } catch (final ConfigException e) {
            // pass
        }
    }

    @Test
    public void shouldThrowExceptionIfApplicationServerConfigPortIsNotAnInteger() {
        builder.setApplicationId(applicationId);
        try {
            configurePartitionAssignor(Collections.singletonMap(APPLICATION_SERVER_CONFIG, "localhost:j87yhk"));
            Assert.fail("expected to an exception due to invalid config");
        } catch (final ConfigException e) {
            // pass
        }
    }

    @Test
    public void shouldNotLoopInfinitelyOnMissingMetadataAndShouldNotCreateRelatedTasks() {
        final StreamsBuilder builder = new StreamsBuilder();
        final KStream<Object, Object> stream1 = // force repartitioning for join, but second join input topic unknown
        // -> internal repartitioning topic should not get created
        // Task 2 (should get created):
        // create repartioning and changelog topic as task 1 exists
        // force repartitioning for aggregation
        // Task 1 (should get created):
        builder.stream("topic1").selectKey(( key, value) -> null).groupByKey().count(Materialized.as("count")).toStream().map(((KeyValueMapper<Object, Long, KeyValue<Object, Object>>) (( key, value) -> null)));
        // Task 4 (should not get created because input topics unknown)
        // should not create any of both input repartition topics or any of both changelog topics
        // force repartitioning for join, but input topic unknown
        // -> thus should not create internal repartitioning topic
        // Task 3 (should not get created because input topic unknown)
        builder.stream("unknownTopic").selectKey(( key, value) -> null).join(stream1, ((ValueJoiner) (( value1, value2) -> null)), JoinWindows.of(Duration.ofMillis(0)));
        final UUID uuid = UUID.randomUUID();
        final String client = "client1";
        final InternalTopologyBuilder internalTopologyBuilder = TopologyWrapper.getInternalTopologyBuilder(builder.build());
        internalTopologyBuilder.setApplicationId(applicationId);
        mockTaskManager(Collections.emptySet(), Collections.emptySet(), UUID.randomUUID(), internalTopologyBuilder);
        configurePartitionAssignor(Collections.emptyMap());
        final MockInternalTopicManager mockInternalTopicManager = new MockInternalTopicManager(streamsConfig, mockClientSupplier.restoreConsumer);
        partitionAssignor.setInternalTopicManager(mockInternalTopicManager);
        final Map<String, PartitionAssignor.Subscription> subscriptions = new HashMap<>();
        final Set<TaskId> emptyTasks = Collections.emptySet();
        subscriptions.put(client, new PartitionAssignor.Subscription(Collections.singletonList("unknownTopic"), encode()));
        final Map<String, PartitionAssignor.Assignment> assignment = partitionAssignor.assign(metadata, subscriptions);
        MatcherAssert.assertThat(mockInternalTopicManager.readyTopics.isEmpty(), CoreMatchers.equalTo(true));
        MatcherAssert.assertThat(assignment.get(client).partitions().isEmpty(), CoreMatchers.equalTo(true));
    }

    @Test
    public void shouldUpdateClusterMetadataAndHostInfoOnAssignment() {
        final TopicPartition partitionOne = new TopicPartition("topic", 1);
        final TopicPartition partitionTwo = new TopicPartition("topic", 2);
        final Map<HostInfo, Set<TopicPartition>> hostState = Collections.singletonMap(new HostInfo("localhost", 9090), Utils.mkSet(partitionOne, partitionTwo));
        configurePartitionAssignor(Collections.emptyMap());
        taskManager.setPartitionsByHostState(hostState);
        EasyMock.expectLastCall();
        EasyMock.replay(taskManager);
        partitionAssignor.onAssignment(createAssignment(hostState));
        EasyMock.verify(taskManager);
    }

    @Test
    public void shouldNotAddStandbyTaskPartitionsToPartitionsForHost() {
        final StreamsBuilder builder = new StreamsBuilder();
        builder.stream("topic1").groupByKey().count();
        final InternalTopologyBuilder internalTopologyBuilder = TopologyWrapper.getInternalTopologyBuilder(builder.build());
        internalTopologyBuilder.setApplicationId(applicationId);
        final UUID uuid = UUID.randomUUID();
        mockTaskManager(Collections.emptySet(), Collections.emptySet(), uuid, internalTopologyBuilder);
        final Map<String, Object> props = new HashMap<>();
        props.put(NUM_STANDBY_REPLICAS_CONFIG, 1);
        props.put(APPLICATION_SERVER_CONFIG, userEndPoint);
        configurePartitionAssignor(props);
        partitionAssignor.setInternalTopicManager(new MockInternalTopicManager(streamsConfig, mockClientSupplier.restoreConsumer));
        final Map<String, PartitionAssignor.Subscription> subscriptions = new HashMap<>();
        final Set<TaskId> emptyTasks = Collections.emptySet();
        subscriptions.put("consumer1", new PartitionAssignor.Subscription(Collections.singletonList("topic1"), encode()));
        subscriptions.put("consumer2", new PartitionAssignor.Subscription(Collections.singletonList("topic1"), encode()));
        final Set<TopicPartition> allPartitions = Utils.mkSet(t1p0, t1p1, t1p2);
        final Map<String, PartitionAssignor.Assignment> assign = partitionAssignor.assign(metadata, subscriptions);
        final PartitionAssignor.Assignment consumer1Assignment = assign.get("consumer1");
        final AssignmentInfo assignmentInfo = AssignmentInfo.decode(consumer1Assignment.userData());
        final Set<TopicPartition> consumer1partitions = assignmentInfo.partitionsByHost().get(new HostInfo("localhost", 8080));
        final Set<TopicPartition> consumer2Partitions = assignmentInfo.partitionsByHost().get(new HostInfo("other", 9090));
        final HashSet<TopicPartition> allAssignedPartitions = new HashSet(consumer1partitions);
        allAssignedPartitions.addAll(consumer2Partitions);
        MatcherAssert.assertThat(consumer1partitions, CoreMatchers.not(allPartitions));
        MatcherAssert.assertThat(consumer2Partitions, CoreMatchers.not(allPartitions));
        MatcherAssert.assertThat(allAssignedPartitions, CoreMatchers.equalTo(allPartitions));
    }

    @Test
    public void shouldThrowKafkaExceptionIfTaskMangerNotConfigured() {
        final Map<String, Object> config = configProps();
        config.remove(TASK_MANAGER_FOR_PARTITION_ASSIGNOR);
        try {
            partitionAssignor.configure(config);
            Assert.fail("Should have thrown KafkaException");
        } catch (final KafkaException expected) {
            MatcherAssert.assertThat(expected.getMessage(), CoreMatchers.equalTo("TaskManager is not specified"));
        }
    }

    @Test
    public void shouldThrowKafkaExceptionIfTaskMangerConfigIsNotTaskManagerInstance() {
        final Map<String, Object> config = configProps();
        config.put(TASK_MANAGER_FOR_PARTITION_ASSIGNOR, "i am not a task manager");
        try {
            partitionAssignor.configure(config);
            Assert.fail("Should have thrown KafkaException");
        } catch (final KafkaException expected) {
            MatcherAssert.assertThat(expected.getMessage(), CoreMatchers.equalTo("java.lang.String is not an instance of org.apache.kafka.streams.processor.internals.TaskManager"));
        }
    }

    @Test
    public void shouldThrowKafkaExceptionAssignmentErrorCodeNotConfigured() {
        final Map<String, Object> config = configProps();
        config.remove(ASSIGNMENT_ERROR_CODE);
        try {
            partitionAssignor.configure(config);
            Assert.fail("Should have thrown KafkaException");
        } catch (final KafkaException expected) {
            MatcherAssert.assertThat(expected.getMessage(), CoreMatchers.equalTo("assignmentErrorCode is not specified"));
        }
    }

    @Test
    public void shouldThrowKafkaExceptionIfVersionProbingFlagConfigIsNotAtomicInteger() {
        final Map<String, Object> config = configProps();
        config.put(ASSIGNMENT_ERROR_CODE, "i am not an AtomicInteger");
        try {
            partitionAssignor.configure(config);
            Assert.fail("Should have thrown KafkaException");
        } catch (final KafkaException expected) {
            MatcherAssert.assertThat(expected.getMessage(), CoreMatchers.equalTo("java.lang.String is not an instance of java.util.concurrent.atomic.AtomicInteger"));
        }
    }

    @Test
    public void shouldReturnLowestAssignmentVersionForDifferentSubscriptionVersionsV1V2() {
        shouldReturnLowestAssignmentVersionForDifferentSubscriptionVersions(1, 2);
    }

    @Test
    public void shouldReturnLowestAssignmentVersionForDifferentSubscriptionVersionsV1V3() {
        shouldReturnLowestAssignmentVersionForDifferentSubscriptionVersions(1, 3);
    }

    @Test
    public void shouldReturnLowestAssignmentVersionForDifferentSubscriptionVersionsV2V3() {
        shouldReturnLowestAssignmentVersionForDifferentSubscriptionVersions(2, 3);
    }

    @Test
    public void shouldDownGradeSubscriptionToVersion1() {
        final Set<TaskId> emptyTasks = Collections.emptySet();
        mockTaskManager(emptyTasks, emptyTasks, UUID.randomUUID(), builder);
        configurePartitionAssignor(Collections.singletonMap(UPGRADE_FROM_CONFIG, UPGRADE_FROM_0100));
        final PartitionAssignor.Subscription subscription = partitionAssignor.subscription(Utils.mkSet("topic1"));
        MatcherAssert.assertThat(SubscriptionInfo.decode(subscription.userData()).version(), CoreMatchers.equalTo(1));
    }

    @Test
    public void shouldDownGradeSubscriptionToVersion2For0101() {
        shouldDownGradeSubscriptionToVersion2(UPGRADE_FROM_0101);
    }

    @Test
    public void shouldDownGradeSubscriptionToVersion2For0102() {
        shouldDownGradeSubscriptionToVersion2(UPGRADE_FROM_0102);
    }

    @Test
    public void shouldDownGradeSubscriptionToVersion2For0110() {
        shouldDownGradeSubscriptionToVersion2(UPGRADE_FROM_0110);
    }

    @Test
    public void shouldDownGradeSubscriptionToVersion2For10() {
        shouldDownGradeSubscriptionToVersion2(UPGRADE_FROM_10);
    }

    @Test
    public void shouldDownGradeSubscriptionToVersion2For11() {
        shouldDownGradeSubscriptionToVersion2(UPGRADE_FROM_11);
    }

    @Test
    public void shouldReturnUnchangedAssignmentForOldInstancesAndEmptyAssignmentForFutureInstances() {
        builder.addSource(null, "source1", null, null, null, "topic1");
        final Map<String, PartitionAssignor.Subscription> subscriptions = new HashMap<>();
        final Set<TaskId> allTasks = Utils.mkSet(task0, task1, task2);
        final Set<TaskId> activeTasks = Utils.mkSet(task0, task1);
        final Set<TaskId> standbyTasks = Utils.mkSet(task2);
        final Map<TaskId, Set<TopicPartition>> standbyTaskMap = new HashMap<TaskId, Set<TopicPartition>>() {
            {
                put(task2, Collections.singleton(t1p2));
            }
        };
        subscriptions.put("consumer1", new PartitionAssignor.Subscription(Collections.singletonList("topic1"), encode()));
        subscriptions.put("future-consumer", new PartitionAssignor.Subscription(Collections.singletonList("topic1"), encodeFutureSubscription()));
        mockTaskManager(allTasks, allTasks, UUID.randomUUID(), builder);
        partitionAssignor.configure(configProps());
        final Map<String, PartitionAssignor.Assignment> assignment = partitionAssignor.assign(metadata, subscriptions);
        MatcherAssert.assertThat(assignment.size(), CoreMatchers.equalTo(2));
        MatcherAssert.assertThat(AssignmentInfo.decode(assignment.get("consumer1").userData()), CoreMatchers.equalTo(new AssignmentInfo(new java.util.ArrayList(activeTasks), standbyTaskMap, Collections.emptyMap())));
        MatcherAssert.assertThat(assignment.get("consumer1").partitions(), CoreMatchers.equalTo(Arrays.asList(t1p0, t1p1)));
        MatcherAssert.assertThat(AssignmentInfo.decode(assignment.get("future-consumer").userData()), CoreMatchers.equalTo(new AssignmentInfo()));
        MatcherAssert.assertThat(assignment.get("future-consumer").partitions().size(), CoreMatchers.equalTo(0));
    }

    @Test
    public void shouldThrowIfV1SubscriptionAndFutureSubscriptionIsMixed() {
        shouldThrowIfPreVersionProbingSubscriptionAndFutureSubscriptionIsMixed(1);
    }

    @Test
    public void shouldThrowIfV2SubscriptionAndFutureSubscriptionIsMixed() {
        shouldThrowIfPreVersionProbingSubscriptionAndFutureSubscriptionIsMixed(2);
    }
}

