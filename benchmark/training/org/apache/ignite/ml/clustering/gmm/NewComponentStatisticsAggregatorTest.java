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
package org.apache.ignite.ml.clustering.gmm;


import java.util.Arrays;
import org.apache.ignite.ml.math.primitives.vector.VectorUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link NewComponentStatisticsAggregator} class.
 */
public class NewComponentStatisticsAggregatorTest {
    /**
     *
     */
    GmmPartitionData data1 = new GmmPartitionData(Arrays.asList(vec(1, 0), vec(0, 1), vec(3, 7)), new double[3][]);

    /**
     *
     */
    GmmPartitionData data2 = new GmmPartitionData(Arrays.asList(vec(3, 1), vec(1, 4), vec(1, 3)), new double[3][]);

    /**
     *
     */
    GmmModel model;

    /**
     *
     */
    @Test
    public void testAdd() {
        NewComponentStatisticsAggregator agg = new NewComponentStatisticsAggregator();
        int rowCount = 10;
        for (int i = 0; i < rowCount; i++)
            agg.add(VectorUtils.of(0, 1, 2), ((i % 2) == 0));

        Assert.assertEquals((rowCount / 2), agg.rowCountForNewCluster());
        Assert.assertEquals(rowCount, agg.totalRowCount());
        Assert.assertArrayEquals(new double[]{ 0, 1, 2 }, agg.mean().asArray(), 1.0E-4);
    }

    /**
     *
     */
    @Test
    public void testPlus() {
        NewComponentStatisticsAggregator agg1 = new NewComponentStatisticsAggregator();
        NewComponentStatisticsAggregator agg2 = new NewComponentStatisticsAggregator();
        int rowCount = 10;
        for (int i = 0; i < rowCount; i++)
            agg1.add(VectorUtils.of(0, 1, 2), ((i % 2) == 0));

        for (int i = 0; i < rowCount; i++)
            agg2.add(VectorUtils.of(2, 1, 0), ((i % 2) == 1));

        NewComponentStatisticsAggregator sum = agg1.plus(agg2);
        Assert.assertEquals(rowCount, sum.rowCountForNewCluster());
        Assert.assertEquals((rowCount * 2), sum.totalRowCount());
        Assert.assertArrayEquals(new double[]{ 1, 1, 1 }, sum.mean().asArray(), 1.0E-4);
    }

    /**
     *
     */
    @Test
    public void testMap() {
        NewComponentStatisticsAggregator agg = NewComponentStatisticsAggregator.computeNewMeanMap(data1, 1.0, 2, model);
        Assert.assertEquals(2, agg.rowCountForNewCluster());
        Assert.assertEquals(data1.size(), agg.totalRowCount());
        Assert.assertArrayEquals(new double[]{ 0.5, 0.5 }, agg.mean().asArray(), 1.0E-4);
    }

    /**
     *
     */
    @Test
    public void testReduce() {
        double maxXsProb = 1.0;
        int maxProbDivergence = 2;
        NewComponentStatisticsAggregator agg1 = NewComponentStatisticsAggregator.computeNewMeanMap(data1, maxXsProb, maxProbDivergence, model);
        NewComponentStatisticsAggregator agg2 = NewComponentStatisticsAggregator.computeNewMeanMap(data2, maxXsProb, maxProbDivergence, model);
        NewComponentStatisticsAggregator res = NewComponentStatisticsAggregator.computeNewMeanReduce(agg1, null);
        Assert.assertEquals(agg1.rowCountForNewCluster(), res.rowCountForNewCluster());
        Assert.assertEquals(agg1.totalRowCount(), res.totalRowCount());
        Assert.assertArrayEquals(agg1.mean().asArray(), res.mean().asArray(), 1.0E-4);
        res = NewComponentStatisticsAggregator.computeNewMeanReduce(null, agg1);
        Assert.assertEquals(agg1.rowCountForNewCluster(), res.rowCountForNewCluster());
        Assert.assertEquals(agg1.totalRowCount(), res.totalRowCount());
        Assert.assertArrayEquals(agg1.mean().asArray(), res.mean().asArray(), 1.0E-4);
        res = NewComponentStatisticsAggregator.computeNewMeanReduce(agg2, agg1);
        Assert.assertEquals(4, res.rowCountForNewCluster());
        Assert.assertEquals(6, res.totalRowCount());
        Assert.assertArrayEquals(new double[]{ 1.25, 1.25 }, res.mean().asArray(), 1.0E-4);
        res = NewComponentStatisticsAggregator.computeNewMeanReduce(agg1, agg2);
        Assert.assertEquals(4, res.rowCountForNewCluster());
        Assert.assertEquals(6, res.totalRowCount());
        Assert.assertArrayEquals(new double[]{ 1.25, 1.25 }, res.mean().asArray(), 1.0E-4);
    }
}

