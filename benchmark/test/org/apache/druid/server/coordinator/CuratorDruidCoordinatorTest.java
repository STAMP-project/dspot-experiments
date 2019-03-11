/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.server.coordinator;


import PathChildrenCache.StartMode.POST_INITIALIZED_EVENT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.utils.ZKPaths;
import org.apache.druid.client.BatchServerInventoryView;
import org.apache.druid.client.CoordinatorServerView;
import org.apache.druid.client.DruidServer;
import org.apache.druid.client.ImmutableDruidDataSource;
import org.apache.druid.common.config.JacksonConfigManager;
import org.apache.druid.curator.CuratorTestBase;
import org.apache.druid.discovery.DruidLeaderSelector;
import org.apache.druid.java.util.common.Pair;
import org.apache.druid.java.util.common.concurrent.ScheduledExecutorFactory;
import org.apache.druid.metadata.MetadataRuleManager;
import org.apache.druid.metadata.MetadataSegmentManager;
import org.apache.druid.segment.TestHelper;
import org.apache.druid.server.DruidNode;
import org.apache.druid.server.coordination.ServerType;
import org.apache.druid.server.initialization.ZkPathsConfig;
import org.apache.druid.testing.DeadlockDetectingTimeout;
import org.apache.druid.timeline.DataSegment;
import org.apache.druid.timeline.SegmentId;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;


/**
 * This tests zookeeper specific coordinator/load queue/historical interactions, such as moving segments by the balancer
 */
public class CuratorDruidCoordinatorTest extends CuratorTestBase {
    private DruidCoordinator coordinator;

    private MetadataSegmentManager databaseSegmentManager;

    private ScheduledExecutorFactory scheduledExecutorFactory;

    private ConcurrentMap<String, LoadQueuePeon> loadManagementPeons;

    private LoadQueuePeon sourceLoadQueuePeon;

    private LoadQueuePeon destinationLoadQueuePeon;

    private MetadataRuleManager metadataRuleManager;

    private CountDownLatch leaderAnnouncerLatch;

    private CountDownLatch leaderUnannouncerLatch;

    private PathChildrenCache sourceLoadQueueChildrenCache;

    private PathChildrenCache destinationLoadQueueChildrenCache;

    private DruidCoordinatorConfig druidCoordinatorConfig;

    private ObjectMapper objectMapper;

    private JacksonConfigManager configManager;

    private DruidNode druidNode;

    private static final String SEGPATH = "/druid/segments";

    private static final String SOURCE_LOAD_PATH = "/druid/loadQueue/localhost:1";

    private static final String DESTINATION_LOAD_PATH = "/druid/loadQueue/localhost:2";

    private static final long COORDINATOR_START_DELAY = 1;

    private static final long COORDINATOR_PERIOD = 100;

    private BatchServerInventoryView baseView;

    private CoordinatorServerView serverView;

    private CountDownLatch segmentViewInitLatch;

    /**
     * The following two fields are changed during {@link #testMoveSegment()}, the change might not be visible from the
     * thread, that runs the callback, registered in {@link #setupView()}. volatile modificator doesn't guarantee
     * visibility either, but somewhat increases the chances.
     */
    private volatile CountDownLatch segmentAddedLatch;

    private volatile CountDownLatch segmentRemovedLatch;

    private final ObjectMapper jsonMapper;

    private final ZkPathsConfig zkPathsConfig;

    public CuratorDruidCoordinatorTest() {
        jsonMapper = TestHelper.makeJsonMapper();
        zkPathsConfig = new ZkPathsConfig();
    }

    @Rule
    public final TestRule timeout = new DeadlockDetectingTimeout(60, TimeUnit.SECONDS);

    @Test
    public void testMoveSegment() throws Exception {
        segmentViewInitLatch = new CountDownLatch(1);
        segmentAddedLatch = new CountDownLatch(4);
        segmentRemovedLatch = new CountDownLatch(0);
        CountDownLatch destCountdown = new CountDownLatch(1);
        CountDownLatch srcCountdown = new CountDownLatch(1);
        setupView();
        DruidServer source = new DruidServer("localhost:1", "localhost:1", null, 10000000L, ServerType.HISTORICAL, "default_tier", 0);
        DruidServer dest = new DruidServer("localhost:2", "localhost:2", null, 10000000L, ServerType.HISTORICAL, "default_tier", 0);
        setupZNodeForServer(source, zkPathsConfig, jsonMapper);
        setupZNodeForServer(dest, zkPathsConfig, jsonMapper);
        final List<DataSegment> sourceSegments = Lists.transform(ImmutableList.of(Pair.of("2011-04-01/2011-04-03", "v1"), Pair.of("2011-04-03/2011-04-06", "v1"), Pair.of("2011-04-06/2011-04-09", "v1")), ( input) -> dataSegmentWithIntervalAndVersion(input.lhs, input.rhs));
        final List<DataSegment> destinationSegments = Lists.transform(ImmutableList.of(Pair.of("2011-03-31/2011-04-01", "v1")), ( input) -> dataSegmentWithIntervalAndVersion(input.lhs, input.rhs));
        DataSegment segmentToMove = sourceSegments.get(2);
        List<String> sourceSegKeys = new ArrayList<>();
        for (DataSegment segment : sourceSegments) {
            sourceSegKeys.add(announceBatchSegmentsForServer(source, ImmutableSet.of(segment), zkPathsConfig, jsonMapper));
        }
        for (DataSegment segment : destinationSegments) {
            announceBatchSegmentsForServer(dest, ImmutableSet.of(segment), zkPathsConfig, jsonMapper);
        }
        Assert.assertTrue(timing.forWaiting().awaitLatch(segmentViewInitLatch));
        Assert.assertTrue(timing.forWaiting().awaitLatch(segmentAddedLatch));
        // these child watchers are used to simulate actions of historicals, announcing a segment on noticing a load queue
        // for the destination and unannouncing from source server when noticing a drop request
        sourceLoadQueueChildrenCache.getListenable().addListener(( curatorFramework, pathChildrenCacheEvent) -> {
            if (pathChildrenCacheEvent.getType().equals(PathChildrenCacheEvent.Type.INITIALIZED)) {
                srcCountdown.countDown();
            } else
                if (pathChildrenCacheEvent.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)) {
                    // Simulate source server dropping segment
                    unannounceSegmentFromBatchForServer(source, segmentToMove, sourceSegKeys.get(2), zkPathsConfig);
                }

        });
        destinationLoadQueueChildrenCache.getListenable().addListener(( curatorFramework, pathChildrenCacheEvent) -> {
            if (pathChildrenCacheEvent.getType().equals(PathChildrenCacheEvent.Type.INITIALIZED)) {
                destCountdown.countDown();
            } else
                if (pathChildrenCacheEvent.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)) {
                    // Simulate destination server loading segment
                    announceBatchSegmentsForServer(dest, ImmutableSet.of(segmentToMove), zkPathsConfig, jsonMapper);
                }

        });
        sourceLoadQueueChildrenCache.start(POST_INITIALIZED_EVENT);
        destinationLoadQueueChildrenCache.start(POST_INITIALIZED_EVENT);
        Assert.assertTrue(timing.forWaiting().awaitLatch(srcCountdown));
        Assert.assertTrue(timing.forWaiting().awaitLatch(destCountdown));
        loadManagementPeons.put("localhost:1", sourceLoadQueuePeon);
        loadManagementPeons.put("localhost:2", destinationLoadQueuePeon);
        segmentRemovedLatch = new CountDownLatch(1);
        segmentAddedLatch = new CountDownLatch(1);
        ImmutableDruidDataSource druidDataSource = EasyMock.createNiceMock(ImmutableDruidDataSource.class);
        EasyMock.expect(druidDataSource.getSegment(EasyMock.anyObject(SegmentId.class))).andReturn(sourceSegments.get(2));
        EasyMock.replay(druidDataSource);
        EasyMock.expect(databaseSegmentManager.getDataSource(EasyMock.anyString())).andReturn(druidDataSource);
        EasyMock.replay(databaseSegmentManager);
        coordinator.moveSegment(source.toImmutableDruidServer(), dest.toImmutableDruidServer(), sourceSegments.get(2), null);
        // wait for destination server to load segment
        Assert.assertTrue(timing.forWaiting().awaitLatch(segmentAddedLatch));
        // remove load queue key from destination server to trigger adding drop to load queue
        curator.delete().guaranteed().forPath(ZKPaths.makePath(CuratorDruidCoordinatorTest.DESTINATION_LOAD_PATH, segmentToMove.getId().toString()));
        // wait for drop
        Assert.assertTrue(timing.forWaiting().awaitLatch(segmentRemovedLatch));
        // clean up drop from load queue
        curator.delete().guaranteed().forPath(ZKPaths.makePath(CuratorDruidCoordinatorTest.SOURCE_LOAD_PATH, segmentToMove.getId().toString()));
        List<DruidServer> servers = new ArrayList(serverView.getInventory());
        Assert.assertEquals(2, servers.get(0).getTotalSegments());
        Assert.assertEquals(2, servers.get(1).getTotalSegments());
    }

    private static class TestDruidLeaderSelector implements DruidLeaderSelector {
        private volatile Listener listener;

        private volatile String leader;

        @Override
        public String getCurrentLeader() {
            return leader;
        }

        @Override
        public boolean isLeader() {
            return (leader) != null;
        }

        @Override
        public int localTerm() {
            return 0;
        }

        @Override
        public void registerListener(Listener listener) {
            this.listener = listener;
            leader = "what:1234";
            listener.becomeLeader();
        }

        @Override
        public void unregisterListener() {
            leader = null;
            listener.stopBeingLeader();
        }
    }
}

