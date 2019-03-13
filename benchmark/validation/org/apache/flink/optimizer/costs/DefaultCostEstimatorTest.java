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
package org.apache.flink.optimizer.costs;


import org.apache.flink.optimizer.dag.EstimateProvider;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the cost formulas in the {@link DefaultCostEstimator}. Most of the tests establish relative
 * relationships.
 */
public class DefaultCostEstimatorTest {
    // estimates
    private static final long SMALL_DATA_SIZE = 10000;

    private static final long SMALL_RECORD_COUNT = 100;

    private static final long MEDIUM_DATA_SIZE = 500000000L;

    private static final long MEDIUM_RECORD_COUNT = 500000L;

    private static final long BIG_DATA_SIZE = 100000000000L;

    private static final long BIG_RECORD_COUNT = 100000000L;

    private static final EstimateProvider UNKNOWN_ESTIMATES = new DefaultCostEstimatorTest.UnknownEstimates();

    private static final EstimateProvider ZERO_ESTIMATES = new DefaultCostEstimatorTest.Estimates(0, 0);

    private static final EstimateProvider SMALL_ESTIMATES = new DefaultCostEstimatorTest.Estimates(DefaultCostEstimatorTest.SMALL_DATA_SIZE, DefaultCostEstimatorTest.SMALL_RECORD_COUNT);

    private static final EstimateProvider MEDIUM_ESTIMATES = new DefaultCostEstimatorTest.Estimates(DefaultCostEstimatorTest.MEDIUM_DATA_SIZE, DefaultCostEstimatorTest.MEDIUM_RECORD_COUNT);

    private static final EstimateProvider BIG_ESTIMATES = new DefaultCostEstimatorTest.Estimates(DefaultCostEstimatorTest.BIG_DATA_SIZE, DefaultCostEstimatorTest.BIG_RECORD_COUNT);

    private final CostEstimator costEstimator = new DefaultCostEstimator();

    // --------------------------------------------------------------------------------------------
    @Test
    public void testShipStrategiesIsolated() {
        testShipStrategiesIsolated(DefaultCostEstimatorTest.UNKNOWN_ESTIMATES, 1);
        testShipStrategiesIsolated(DefaultCostEstimatorTest.UNKNOWN_ESTIMATES, 10);
        testShipStrategiesIsolated(DefaultCostEstimatorTest.ZERO_ESTIMATES, 1);
        testShipStrategiesIsolated(DefaultCostEstimatorTest.ZERO_ESTIMATES, 10);
        testShipStrategiesIsolated(DefaultCostEstimatorTest.SMALL_ESTIMATES, 1);
        testShipStrategiesIsolated(DefaultCostEstimatorTest.SMALL_ESTIMATES, 10);
        testShipStrategiesIsolated(DefaultCostEstimatorTest.BIG_ESTIMATES, 1);
        testShipStrategiesIsolated(DefaultCostEstimatorTest.BIG_ESTIMATES, 10);
    }

    // --------------------------------------------------------------------------------------------
    @Test
    public void testShipStrategyCombinationsPlain() {
        Costs hashBothSmall = new Costs();
        Costs hashSmallAndLarge = new Costs();
        Costs hashBothLarge = new Costs();
        Costs hashSmallBcLarge10 = new Costs();
        Costs hashLargeBcSmall10 = new Costs();
        Costs hashSmallBcLarge1000 = new Costs();
        Costs hashLargeBcSmall1000 = new Costs();
        Costs forwardSmallBcLarge10 = new Costs();
        Costs forwardLargeBcSmall10 = new Costs();
        Costs forwardSmallBcLarge1000 = new Costs();
        Costs forwardLargeBcSmall1000 = new Costs();
        costEstimator.addHashPartitioningCost(DefaultCostEstimatorTest.MEDIUM_ESTIMATES, hashBothSmall);
        costEstimator.addHashPartitioningCost(DefaultCostEstimatorTest.MEDIUM_ESTIMATES, hashBothSmall);
        costEstimator.addHashPartitioningCost(DefaultCostEstimatorTest.MEDIUM_ESTIMATES, hashSmallAndLarge);
        costEstimator.addHashPartitioningCost(DefaultCostEstimatorTest.BIG_ESTIMATES, hashSmallAndLarge);
        costEstimator.addHashPartitioningCost(DefaultCostEstimatorTest.BIG_ESTIMATES, hashBothLarge);
        costEstimator.addHashPartitioningCost(DefaultCostEstimatorTest.BIG_ESTIMATES, hashBothLarge);
        costEstimator.addHashPartitioningCost(DefaultCostEstimatorTest.MEDIUM_ESTIMATES, hashSmallBcLarge10);
        costEstimator.addBroadcastCost(DefaultCostEstimatorTest.BIG_ESTIMATES, 10, hashSmallBcLarge10);
        costEstimator.addHashPartitioningCost(DefaultCostEstimatorTest.BIG_ESTIMATES, hashLargeBcSmall10);
        costEstimator.addBroadcastCost(DefaultCostEstimatorTest.MEDIUM_ESTIMATES, 10, hashLargeBcSmall10);
        costEstimator.addHashPartitioningCost(DefaultCostEstimatorTest.MEDIUM_ESTIMATES, hashSmallBcLarge1000);
        costEstimator.addBroadcastCost(DefaultCostEstimatorTest.BIG_ESTIMATES, 1000, hashSmallBcLarge1000);
        costEstimator.addHashPartitioningCost(DefaultCostEstimatorTest.BIG_ESTIMATES, hashLargeBcSmall1000);
        costEstimator.addBroadcastCost(DefaultCostEstimatorTest.MEDIUM_ESTIMATES, 1000, hashLargeBcSmall1000);
        costEstimator.addBroadcastCost(DefaultCostEstimatorTest.BIG_ESTIMATES, 10, forwardSmallBcLarge10);
        costEstimator.addBroadcastCost(DefaultCostEstimatorTest.MEDIUM_ESTIMATES, 10, forwardLargeBcSmall10);
        costEstimator.addBroadcastCost(DefaultCostEstimatorTest.BIG_ESTIMATES, 1000, forwardSmallBcLarge1000);
        costEstimator.addBroadcastCost(DefaultCostEstimatorTest.MEDIUM_ESTIMATES, 1000, forwardLargeBcSmall1000);
        // hash cost is roughly monotonous
        Assert.assertTrue(((hashBothSmall.compareTo(hashSmallAndLarge)) < 0));
        Assert.assertTrue(((hashSmallAndLarge.compareTo(hashBothLarge)) < 0));
        // broadcast the smaller is better
        Assert.assertTrue(((hashLargeBcSmall10.compareTo(hashSmallBcLarge10)) < 0));
        Assert.assertTrue(((forwardLargeBcSmall10.compareTo(forwardSmallBcLarge10)) < 0));
        Assert.assertTrue(((hashLargeBcSmall1000.compareTo(hashSmallBcLarge1000)) < 0));
        Assert.assertTrue(((forwardLargeBcSmall1000.compareTo(forwardSmallBcLarge1000)) < 0));
        // broadcasting small and forwarding large is better than partition both, given size difference
        Assert.assertTrue(((forwardLargeBcSmall10.compareTo(hashSmallAndLarge)) < 0));
        // broadcasting too far is expensive again
        Assert.assertTrue(((forwardLargeBcSmall1000.compareTo(hashSmallAndLarge)) > 0));
        // assert weight is respected
        Assert.assertTrue(((hashSmallBcLarge10.compareTo(hashSmallBcLarge1000)) < 0));
        Assert.assertTrue(((hashLargeBcSmall10.compareTo(hashLargeBcSmall1000)) < 0));
        Assert.assertTrue(((forwardSmallBcLarge10.compareTo(forwardSmallBcLarge1000)) < 0));
        Assert.assertTrue(((forwardLargeBcSmall10.compareTo(forwardLargeBcSmall1000)) < 0));
        // forward versus hash
        Assert.assertTrue(((forwardSmallBcLarge10.compareTo(hashSmallBcLarge10)) < 0));
        Assert.assertTrue(((forwardSmallBcLarge1000.compareTo(hashSmallBcLarge1000)) < 0));
        Assert.assertTrue(((forwardLargeBcSmall10.compareTo(hashLargeBcSmall10)) < 0));
        Assert.assertTrue(((forwardLargeBcSmall1000.compareTo(hashLargeBcSmall1000)) < 0));
    }

    // --------------------------------------------------------------------------------------------
    @Test
    public void testShipStrategyCombinationsWithUnknowns() {
        testShipStrategyCombinationsWithUnknowns(DefaultCostEstimatorTest.UNKNOWN_ESTIMATES);
        testShipStrategyCombinationsWithUnknowns(DefaultCostEstimatorTest.ZERO_ESTIMATES);
        testShipStrategyCombinationsWithUnknowns(DefaultCostEstimatorTest.SMALL_ESTIMATES);
        testShipStrategyCombinationsWithUnknowns(DefaultCostEstimatorTest.MEDIUM_ESTIMATES);
        testShipStrategyCombinationsWithUnknowns(DefaultCostEstimatorTest.BIG_ESTIMATES);
    }

    // --------------------------------------------------------------------------------------------
    @Test
    public void testJoinCostFormulasPlain() {
        // hash join costs
        Costs hashBothSmall = new Costs();
        Costs hashBothLarge = new Costs();
        Costs hashSmallBuild = new Costs();
        Costs hashLargeBuild = new Costs();
        costEstimator.addHybridHashCosts(DefaultCostEstimatorTest.SMALL_ESTIMATES, DefaultCostEstimatorTest.BIG_ESTIMATES, hashSmallBuild, 1);
        costEstimator.addHybridHashCosts(DefaultCostEstimatorTest.BIG_ESTIMATES, DefaultCostEstimatorTest.SMALL_ESTIMATES, hashLargeBuild, 1);
        costEstimator.addHybridHashCosts(DefaultCostEstimatorTest.SMALL_ESTIMATES, DefaultCostEstimatorTest.SMALL_ESTIMATES, hashBothSmall, 1);
        costEstimator.addHybridHashCosts(DefaultCostEstimatorTest.BIG_ESTIMATES, DefaultCostEstimatorTest.BIG_ESTIMATES, hashBothLarge, 1);
        Assert.assertTrue(((hashBothSmall.compareTo(hashSmallBuild)) < 0));
        Assert.assertTrue(((hashSmallBuild.compareTo(hashLargeBuild)) < 0));
        Assert.assertTrue(((hashLargeBuild.compareTo(hashBothLarge)) < 0));
        // merge join costs
        Costs mergeBothSmall = new Costs();
        Costs mergeBothLarge = new Costs();
        Costs mergeSmallFirst = new Costs();
        Costs mergeSmallSecond = new Costs();
        costEstimator.addLocalSortCost(DefaultCostEstimatorTest.SMALL_ESTIMATES, mergeSmallFirst);
        costEstimator.addLocalSortCost(DefaultCostEstimatorTest.BIG_ESTIMATES, mergeSmallFirst);
        costEstimator.addLocalMergeCost(DefaultCostEstimatorTest.SMALL_ESTIMATES, DefaultCostEstimatorTest.BIG_ESTIMATES, mergeSmallFirst, 1);
        costEstimator.addLocalSortCost(DefaultCostEstimatorTest.BIG_ESTIMATES, mergeSmallSecond);
        costEstimator.addLocalSortCost(DefaultCostEstimatorTest.SMALL_ESTIMATES, mergeSmallSecond);
        costEstimator.addLocalMergeCost(DefaultCostEstimatorTest.BIG_ESTIMATES, DefaultCostEstimatorTest.SMALL_ESTIMATES, mergeSmallSecond, 1);
        costEstimator.addLocalSortCost(DefaultCostEstimatorTest.SMALL_ESTIMATES, mergeBothSmall);
        costEstimator.addLocalSortCost(DefaultCostEstimatorTest.SMALL_ESTIMATES, mergeBothSmall);
        costEstimator.addLocalMergeCost(DefaultCostEstimatorTest.SMALL_ESTIMATES, DefaultCostEstimatorTest.SMALL_ESTIMATES, mergeBothSmall, 1);
        costEstimator.addLocalSortCost(DefaultCostEstimatorTest.BIG_ESTIMATES, mergeBothLarge);
        costEstimator.addLocalSortCost(DefaultCostEstimatorTest.BIG_ESTIMATES, mergeBothLarge);
        costEstimator.addLocalMergeCost(DefaultCostEstimatorTest.BIG_ESTIMATES, DefaultCostEstimatorTest.BIG_ESTIMATES, mergeBothLarge, 1);
        Assert.assertTrue(((mergeBothSmall.compareTo(mergeSmallFirst)) < 0));
        Assert.assertTrue(((mergeBothSmall.compareTo(mergeSmallSecond)) < 0));
        Assert.assertTrue(((mergeSmallFirst.compareTo(mergeSmallSecond)) == 0));
        Assert.assertTrue(((mergeSmallFirst.compareTo(mergeBothLarge)) < 0));
        Assert.assertTrue(((mergeSmallSecond.compareTo(mergeBothLarge)) < 0));
        // compare merge join and hash join costs
        Assert.assertTrue(((hashBothSmall.compareTo(mergeBothSmall)) < 0));
        Assert.assertTrue(((hashBothLarge.compareTo(mergeBothLarge)) < 0));
        Assert.assertTrue(((hashSmallBuild.compareTo(mergeSmallFirst)) < 0));
        Assert.assertTrue(((hashSmallBuild.compareTo(mergeSmallSecond)) < 0));
        Assert.assertTrue(((hashLargeBuild.compareTo(mergeSmallFirst)) < 0));
        Assert.assertTrue(((hashLargeBuild.compareTo(mergeSmallSecond)) < 0));
    }

    // --------------------------------------------------------------------------------------------
    @Test
    public void testJoinCostFormulasWithWeights() {
        testJoinCostFormulasWithWeights(DefaultCostEstimatorTest.UNKNOWN_ESTIMATES, DefaultCostEstimatorTest.SMALL_ESTIMATES);
        testJoinCostFormulasWithWeights(DefaultCostEstimatorTest.SMALL_ESTIMATES, DefaultCostEstimatorTest.UNKNOWN_ESTIMATES);
        testJoinCostFormulasWithWeights(DefaultCostEstimatorTest.UNKNOWN_ESTIMATES, DefaultCostEstimatorTest.MEDIUM_ESTIMATES);
        testJoinCostFormulasWithWeights(DefaultCostEstimatorTest.MEDIUM_ESTIMATES, DefaultCostEstimatorTest.UNKNOWN_ESTIMATES);
        testJoinCostFormulasWithWeights(DefaultCostEstimatorTest.BIG_ESTIMATES, DefaultCostEstimatorTest.MEDIUM_ESTIMATES);
        testJoinCostFormulasWithWeights(DefaultCostEstimatorTest.MEDIUM_ESTIMATES, DefaultCostEstimatorTest.BIG_ESTIMATES);
    }

    // --------------------------------------------------------------------------------------------
    @Test
    public void testHashJoinCostFormulasWithCaches() {
        Costs hashBothUnknown10 = new Costs();
        Costs hashBothUnknownCached10 = new Costs();
        Costs hashBothSmall10 = new Costs();
        Costs hashBothSmallCached10 = new Costs();
        Costs hashSmallLarge10 = new Costs();
        Costs hashSmallLargeCached10 = new Costs();
        Costs hashLargeSmall10 = new Costs();
        Costs hashLargeSmallCached10 = new Costs();
        Costs hashLargeSmall1 = new Costs();
        Costs hashLargeSmallCached1 = new Costs();
        costEstimator.addHybridHashCosts(DefaultCostEstimatorTest.UNKNOWN_ESTIMATES, DefaultCostEstimatorTest.UNKNOWN_ESTIMATES, hashBothUnknown10, 10);
        costEstimator.addCachedHybridHashCosts(DefaultCostEstimatorTest.UNKNOWN_ESTIMATES, DefaultCostEstimatorTest.UNKNOWN_ESTIMATES, hashBothUnknownCached10, 10);
        costEstimator.addHybridHashCosts(DefaultCostEstimatorTest.MEDIUM_ESTIMATES, DefaultCostEstimatorTest.MEDIUM_ESTIMATES, hashBothSmall10, 10);
        costEstimator.addCachedHybridHashCosts(DefaultCostEstimatorTest.MEDIUM_ESTIMATES, DefaultCostEstimatorTest.MEDIUM_ESTIMATES, hashBothSmallCached10, 10);
        costEstimator.addHybridHashCosts(DefaultCostEstimatorTest.MEDIUM_ESTIMATES, DefaultCostEstimatorTest.BIG_ESTIMATES, hashSmallLarge10, 10);
        costEstimator.addCachedHybridHashCosts(DefaultCostEstimatorTest.MEDIUM_ESTIMATES, DefaultCostEstimatorTest.BIG_ESTIMATES, hashSmallLargeCached10, 10);
        costEstimator.addHybridHashCosts(DefaultCostEstimatorTest.BIG_ESTIMATES, DefaultCostEstimatorTest.MEDIUM_ESTIMATES, hashLargeSmall10, 10);
        costEstimator.addCachedHybridHashCosts(DefaultCostEstimatorTest.BIG_ESTIMATES, DefaultCostEstimatorTest.MEDIUM_ESTIMATES, hashLargeSmallCached10, 10);
        costEstimator.addHybridHashCosts(DefaultCostEstimatorTest.BIG_ESTIMATES, DefaultCostEstimatorTest.MEDIUM_ESTIMATES, hashLargeSmall1, 1);
        costEstimator.addCachedHybridHashCosts(DefaultCostEstimatorTest.BIG_ESTIMATES, DefaultCostEstimatorTest.MEDIUM_ESTIMATES, hashLargeSmallCached1, 1);
        // cached variant is always cheaper
        Assert.assertTrue(((hashBothUnknown10.compareTo(hashBothUnknownCached10)) > 0));
        Assert.assertTrue(((hashBothSmall10.compareTo(hashBothSmallCached10)) > 0));
        Assert.assertTrue(((hashSmallLarge10.compareTo(hashSmallLargeCached10)) > 0));
        Assert.assertTrue(((hashLargeSmall10.compareTo(hashLargeSmallCached10)) > 0));
        // caching the large side is better, because then the small one is the one with additional I/O
        Assert.assertTrue(((hashLargeSmallCached10.compareTo(hashSmallLargeCached10)) < 0));
        // a weight of one makes the caching the same as the non-cached variant
        Assert.assertTrue(((hashLargeSmall1.compareTo(hashLargeSmallCached1)) == 0));
    }

    // --------------------------------------------------------------------------------------------
    // Estimate providers
    // --------------------------------------------------------------------------------------------
    private static final class UnknownEstimates implements EstimateProvider {
        @Override
        public long getEstimatedOutputSize() {
            return -1;
        }

        @Override
        public long getEstimatedNumRecords() {
            return -1;
        }

        @Override
        public float getEstimatedAvgWidthPerOutputRecord() {
            return -1.0F;
        }
    }

    private static final class Estimates implements EstimateProvider {
        private final long size;

        private final long records;

        private final float width;

        public Estimates(long size, long records) {
            this(size, records, (-1.0F));
        }

        public Estimates(long size, long records, float width) {
            this.size = size;
            this.records = records;
            this.width = width;
        }

        @Override
        public long getEstimatedOutputSize() {
            return this.size;
        }

        @Override
        public long getEstimatedNumRecords() {
            return this.records;
        }

        @Override
        public float getEstimatedAvgWidthPerOutputRecord() {
            return this.width;
        }
    }
}

