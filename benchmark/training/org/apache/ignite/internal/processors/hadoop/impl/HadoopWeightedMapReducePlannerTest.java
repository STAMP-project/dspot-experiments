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
package org.apache.ignite.internal.processors.hadoop.impl;


import IgniteNodeAttributes.ATTR_MACS;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import org.apache.ignite.cluster.ClusterMetrics;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.hadoop.HadoopInputSplit;
import org.apache.ignite.hadoop.HadoopMapReducePlan;
import org.apache.ignite.hadoop.mapreduce.IgniteHadoopWeightedMapReducePlanner;
import org.apache.ignite.igfs.IgfsBlockLocation;
import org.apache.ignite.igfs.IgfsPath;
import org.apache.ignite.internal.processors.hadoop.HadoopFileBlock;
import org.apache.ignite.internal.processors.igfs.IgfsMock;
import org.apache.ignite.internal.util.typedef.F;
import org.apache.ignite.lang.IgniteProductVersion;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;


/**
 * Tests for weighted map-reduce planned.
 */
public class HadoopWeightedMapReducePlannerTest extends GridCommonAbstractTest {
    /**
     * ID 1.
     */
    private static final UUID ID_1 = new UUID(0, 1);

    /**
     * ID 2.
     */
    private static final UUID ID_2 = new UUID(0, 2);

    /**
     * ID 3.
     */
    private static final UUID ID_3 = new UUID(0, 3);

    /**
     * MAC 1.
     */
    private static final String MAC_1 = "mac1";

    /**
     * MAC 2.
     */
    private static final String MAC_2 = "mac2";

    /**
     * MAC 3.
     */
    private static final String MAC_3 = "mac3";

    /**
     * Host 1.
     */
    private static final String HOST_1 = "host1";

    /**
     * Host 2.
     */
    private static final String HOST_2 = "host2";

    /**
     * Host 3.
     */
    private static final String HOST_3 = "host3";

    /**
     * Host 4.
     */
    private static final String HOST_4 = "host4";

    /**
     * Host 5.
     */
    private static final String HOST_5 = "host5";

    /**
     * Standard node 1.
     */
    private static final HadoopWeightedMapReducePlannerTest.MockNode NODE_1 = new HadoopWeightedMapReducePlannerTest.MockNode(HadoopWeightedMapReducePlannerTest.ID_1, HadoopWeightedMapReducePlannerTest.MAC_1, HadoopWeightedMapReducePlannerTest.HOST_1);

    /**
     * Standard node 2.
     */
    private static final HadoopWeightedMapReducePlannerTest.MockNode NODE_2 = new HadoopWeightedMapReducePlannerTest.MockNode(HadoopWeightedMapReducePlannerTest.ID_2, HadoopWeightedMapReducePlannerTest.MAC_2, HadoopWeightedMapReducePlannerTest.HOST_2);

    /**
     * Standard node 3.
     */
    private static final HadoopWeightedMapReducePlannerTest.MockNode NODE_3 = new HadoopWeightedMapReducePlannerTest.MockNode(HadoopWeightedMapReducePlannerTest.ID_3, HadoopWeightedMapReducePlannerTest.MAC_3, HadoopWeightedMapReducePlannerTest.HOST_3);

    /**
     * Standard nodes.
     */
    private static final Collection<ClusterNode> NODES;

    /**
     * Static initializer.
     */
    static {
        NODES = new ArrayList();
        HadoopWeightedMapReducePlannerTest.NODES.add(HadoopWeightedMapReducePlannerTest.NODE_1);
        HadoopWeightedMapReducePlannerTest.NODES.add(HadoopWeightedMapReducePlannerTest.NODE_2);
        HadoopWeightedMapReducePlannerTest.NODES.add(HadoopWeightedMapReducePlannerTest.NODE_3);
    }

    /**
     * Test one IGFS split being assigned to affinity node.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testOneIgfsSplitAffinity() throws Exception {
        IgfsMock igfs = HadoopWeightedMapReducePlannerTest.LocationsBuilder.create().add(0, HadoopWeightedMapReducePlannerTest.NODE_1).add(50, HadoopWeightedMapReducePlannerTest.NODE_2).add(100, HadoopWeightedMapReducePlannerTest.NODE_3).buildIgfs();
        List<HadoopInputSplit> splits = new ArrayList<>();
        splits.add(new HadoopFileBlock(new String[]{ HadoopWeightedMapReducePlannerTest.HOST_1 }, URI.create("igfs://igfs@/file"), 0, 50));
        final int expReducers = 4;
        HadoopPlannerMockJob job = new HadoopPlannerMockJob(splits, expReducers);
        IgniteHadoopWeightedMapReducePlanner planner = HadoopWeightedMapReducePlannerTest.createPlanner(igfs);
        HadoopMapReducePlan plan = planner.preparePlan(job, HadoopWeightedMapReducePlannerTest.NODES, null);
        assert (plan.mappers()) == 1;
        assert (plan.mapperNodeIds().size()) == 1;
        assert plan.mapperNodeIds().contains(HadoopWeightedMapReducePlannerTest.ID_1);
        /* only 1 split */
        HadoopWeightedMapReducePlannerTest.checkPlanMappers(plan, splits, HadoopWeightedMapReducePlannerTest.NODES, false);
        /* because of threshold behavior. */
        HadoopWeightedMapReducePlannerTest.checkPlanReducers(plan, HadoopWeightedMapReducePlannerTest.NODES, expReducers, false);
    }

    /**
     * Test one HDFS splits.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testHdfsSplitsAffinity() throws Exception {
        IgfsMock igfs = HadoopWeightedMapReducePlannerTest.LocationsBuilder.create().add(0, HadoopWeightedMapReducePlannerTest.NODE_1).add(50, HadoopWeightedMapReducePlannerTest.NODE_2).add(100, HadoopWeightedMapReducePlannerTest.NODE_3).buildIgfs();
        final List<HadoopInputSplit> splits = new ArrayList<>();
        splits.add(new HadoopFileBlock(new String[]{ HadoopWeightedMapReducePlannerTest.HOST_1 }, URI.create((("hfds://" + (HadoopWeightedMapReducePlannerTest.HOST_1)) + "/x")), 0, 50));
        splits.add(new HadoopFileBlock(new String[]{ HadoopWeightedMapReducePlannerTest.HOST_2 }, URI.create((("hfds://" + (HadoopWeightedMapReducePlannerTest.HOST_2)) + "/x")), 50, 100));
        splits.add(new HadoopFileBlock(new String[]{ HadoopWeightedMapReducePlannerTest.HOST_3 }, URI.create((("hfds://" + (HadoopWeightedMapReducePlannerTest.HOST_3)) + "/x")), 100, 37));
        // The following splits belong to hosts that are out of Ignite topology at all.
        // This means that these splits should be assigned to any least loaded modes:
        splits.add(new HadoopFileBlock(new String[]{ HadoopWeightedMapReducePlannerTest.HOST_4 }, URI.create((("hfds://" + (HadoopWeightedMapReducePlannerTest.HOST_4)) + "/x")), 138, 2));
        splits.add(new HadoopFileBlock(new String[]{ HadoopWeightedMapReducePlannerTest.HOST_5 }, URI.create((("hfds://" + (HadoopWeightedMapReducePlannerTest.HOST_5)) + "/x")), 140, 3));
        final int expReducers = 7;
        HadoopPlannerMockJob job = new HadoopPlannerMockJob(splits, expReducers);
        IgniteHadoopWeightedMapReducePlanner planner = HadoopWeightedMapReducePlannerTest.createPlanner(igfs);
        final HadoopMapReducePlan plan = planner.preparePlan(job, HadoopWeightedMapReducePlannerTest.NODES, null);
        HadoopWeightedMapReducePlannerTest.checkPlanMappers(plan, splits, HadoopWeightedMapReducePlannerTest.NODES, true);
        HadoopWeightedMapReducePlannerTest.checkPlanReducers(plan, HadoopWeightedMapReducePlannerTest.NODES, expReducers, true);
    }

    /**
     * Test HDFS splits with Replication == 3.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testHdfsSplitsReplication() throws Exception {
        IgfsMock igfs = HadoopWeightedMapReducePlannerTest.LocationsBuilder.create().add(0, HadoopWeightedMapReducePlannerTest.NODE_1).add(50, HadoopWeightedMapReducePlannerTest.NODE_2).add(100, HadoopWeightedMapReducePlannerTest.NODE_3).buildIgfs();
        final List<HadoopInputSplit> splits = new ArrayList<>();
        splits.add(new HadoopFileBlock(new String[]{ HadoopWeightedMapReducePlannerTest.HOST_1, HadoopWeightedMapReducePlannerTest.HOST_2, HadoopWeightedMapReducePlannerTest.HOST_3 }, URI.create((("hfds://" + (HadoopWeightedMapReducePlannerTest.HOST_1)) + "/x")), 0, 50));
        splits.add(new HadoopFileBlock(new String[]{ HadoopWeightedMapReducePlannerTest.HOST_2, HadoopWeightedMapReducePlannerTest.HOST_3, HadoopWeightedMapReducePlannerTest.HOST_4 }, URI.create((("hfds://" + (HadoopWeightedMapReducePlannerTest.HOST_2)) + "/x")), 50, 100));
        splits.add(new HadoopFileBlock(new String[]{ HadoopWeightedMapReducePlannerTest.HOST_3, HadoopWeightedMapReducePlannerTest.HOST_4, HadoopWeightedMapReducePlannerTest.HOST_5 }, URI.create((("hfds://" + (HadoopWeightedMapReducePlannerTest.HOST_3)) + "/x")), 100, 37));
        // The following splits belong to hosts that are out of Ignite topology at all.
        // This means that these splits should be assigned to any least loaded modes:
        splits.add(new HadoopFileBlock(new String[]{ HadoopWeightedMapReducePlannerTest.HOST_4, HadoopWeightedMapReducePlannerTest.HOST_5, HadoopWeightedMapReducePlannerTest.HOST_1 }, URI.create((("hfds://" + (HadoopWeightedMapReducePlannerTest.HOST_4)) + "/x")), 138, 2));
        splits.add(new HadoopFileBlock(new String[]{ HadoopWeightedMapReducePlannerTest.HOST_5, HadoopWeightedMapReducePlannerTest.HOST_1, HadoopWeightedMapReducePlannerTest.HOST_2 }, URI.create((("hfds://" + (HadoopWeightedMapReducePlannerTest.HOST_5)) + "/x")), 140, 3));
        final int expReducers = 8;
        HadoopPlannerMockJob job = new HadoopPlannerMockJob(splits, expReducers);
        IgniteHadoopWeightedMapReducePlanner planner = HadoopWeightedMapReducePlannerTest.createPlanner(igfs);
        final HadoopMapReducePlan plan = planner.preparePlan(job, HadoopWeightedMapReducePlannerTest.NODES, null);
        HadoopWeightedMapReducePlannerTest.checkPlanMappers(plan, splits, HadoopWeightedMapReducePlannerTest.NODES, true);
        HadoopWeightedMapReducePlannerTest.checkPlanReducers(plan, HadoopWeightedMapReducePlannerTest.NODES, expReducers, true);
    }

    /**
     * Mocked node.
     */
    private static class MockNode implements ClusterNode {
        /**
         * ID.
         */
        private final UUID id;

        /**
         * MAC addresses.
         */
        private final String macs;

        /**
         * Addresses.
         */
        private final List<String> addrs;

        /**
         * Constructor.
         *
         * @param id
         * 		Node ID.
         * @param macs
         * 		MAC addresses.
         * @param addrs
         * 		Addresses.
         */
        public MockNode(UUID id, String macs, String... addrs) {
            assert addrs != null;
            this.id = id;
            this.macs = macs;
            this.addrs = Arrays.asList(addrs);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public UUID id() {
            return id;
        }

        /**
         * {@inheritDoc }
         */
        @SuppressWarnings("unchecked")
        @Nullable
        @Override
        public <T> T attribute(String name) {
            if (F.eq(name, ATTR_MACS))
                return ((T) (macs));

            HadoopWeightedMapReducePlannerTest.throwUnsupported();
            return null;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Collection<String> addresses() {
            return addrs;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Object consistentId() {
            HadoopWeightedMapReducePlannerTest.throwUnsupported();
            return null;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public ClusterMetrics metrics() {
            HadoopWeightedMapReducePlannerTest.throwUnsupported();
            return null;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Map<String, Object> attributes() {
            HadoopWeightedMapReducePlannerTest.throwUnsupported();
            return null;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Collection<String> hostNames() {
            HadoopWeightedMapReducePlannerTest.throwUnsupported();
            return null;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public long order() {
            HadoopWeightedMapReducePlannerTest.throwUnsupported();
            return 0;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public IgniteProductVersion version() {
            HadoopWeightedMapReducePlannerTest.throwUnsupported();
            return null;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean isLocal() {
            HadoopWeightedMapReducePlannerTest.throwUnsupported();
            return false;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean isDaemon() {
            HadoopWeightedMapReducePlannerTest.throwUnsupported();
            return false;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean isClient() {
            HadoopWeightedMapReducePlannerTest.throwUnsupported();
            return false;
        }
    }

    /**
     * Locations builder.
     */
    private static class LocationsBuilder {
        /**
         * Locations.
         */
        private final TreeMap<Long, Collection<HadoopWeightedMapReducePlannerTest.MockNode>> locs = new TreeMap<>();

        /**
         * Create new locations builder.
         *
         * @return Locations builder.
         */
        public static HadoopWeightedMapReducePlannerTest.LocationsBuilder create() {
            return new HadoopWeightedMapReducePlannerTest.LocationsBuilder();
        }

        /**
         * Add locations.
         *
         * @param start
         * 		Start.
         * @param nodes
         * 		Nodes.
         * @return This builder for chaining.
         */
        public HadoopWeightedMapReducePlannerTest.LocationsBuilder add(long start, HadoopWeightedMapReducePlannerTest.MockNode... nodes) {
            locs.put(start, Arrays.asList(nodes));
            return this;
        }

        /**
         * Build locations.
         *
         * @return Locations.
         */
        public TreeMap<Long, Collection<HadoopWeightedMapReducePlannerTest.MockNode>> build() {
            return locs;
        }

        /**
         * Build IGFS.
         *
         * @return IGFS.
         */
        public HadoopWeightedMapReducePlannerTest.MockIgfs buildIgfs() {
            return new HadoopWeightedMapReducePlannerTest.MockIgfs(build());
        }
    }

    /**
     * Mocked IGFS.
     */
    private static class MockIgfs extends IgfsMock {
        /**
         * Block locations.
         */
        private final TreeMap<Long, Collection<HadoopWeightedMapReducePlannerTest.MockNode>> locs;

        /**
         * Constructor.
         *
         * @param locs
         * 		Block locations.
         */
        public MockIgfs(TreeMap<Long, Collection<HadoopWeightedMapReducePlannerTest.MockNode>> locs) {
            super("igfs");
            this.locs = locs;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Collection<IgfsBlockLocation> affinity(IgfsPath path, long start, long len) {
            Collection<IgfsBlockLocation> res = new ArrayList<>();
            long cur = start;
            long remaining = len;
            long prevLocStart = -1;
            Collection<HadoopWeightedMapReducePlannerTest.MockNode> prevLocNodes = null;
            for (Map.Entry<Long, Collection<HadoopWeightedMapReducePlannerTest.MockNode>> locEntry : locs.entrySet()) {
                long locStart = locEntry.getKey();
                Collection<HadoopWeightedMapReducePlannerTest.MockNode> locNodes = locEntry.getValue();
                if (prevLocNodes != null) {
                    if (cur < locStart) {
                        // Add part from previous block.
                        long prevLen = locStart - prevLocStart;
                        res.add(new HadoopWeightedMapReducePlannerTest.IgfsBlockLocationMock(cur, prevLen, prevLocNodes));
                        cur = locStart;
                        remaining -= prevLen;
                    }
                }
                prevLocStart = locStart;
                prevLocNodes = locNodes;
                if (remaining == 0)
                    break;

            }
            // Add remainder.
            if (remaining != 0)
                res.add(new HadoopWeightedMapReducePlannerTest.IgfsBlockLocationMock(cur, remaining, prevLocNodes));

            return res;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean exists(IgfsPath path) {
            return true;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean isProxy(URI path) {
            return false;
        }
    }

    /**
     * Mocked block location.
     */
    private static class IgfsBlockLocationMock implements IgfsBlockLocation {
        /**
         * Start.
         */
        private final long start;

        /**
         * Length.
         */
        private final long len;

        /**
         * Node IDs.
         */
        private final List<UUID> nodeIds;

        /**
         * Constructor.
         *
         * @param start
         * 		Start.
         * @param len
         * 		Length.
         * @param nodes
         * 		Nodes.
         */
        public IgfsBlockLocationMock(long start, long len, Collection<HadoopWeightedMapReducePlannerTest.MockNode> nodes) {
            this.start = start;
            this.len = len;
            this.nodeIds = new ArrayList<>(nodes.size());
            for (HadoopWeightedMapReducePlannerTest.MockNode node : nodes)
                nodeIds.add(node.id);

        }

        /**
         * {@inheritDoc }
         */
        @Override
        public long start() {
            return start;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public long length() {
            return len;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Collection<UUID> nodeIds() {
            return nodeIds;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Collection<String> names() {
            HadoopWeightedMapReducePlannerTest.throwUnsupported();
            return null;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Collection<String> hosts() {
            HadoopWeightedMapReducePlannerTest.throwUnsupported();
            return null;
        }
    }
}

