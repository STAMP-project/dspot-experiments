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
package org.apache.ignite.ml.tree.data;


import org.apache.ignite.ml.tree.TreeFilter;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for {@link TreeDataIndex}.
 */
public class TreeDataIndexTest {
    /**
     *
     */
    private double[][] features = new double[][]{ new double[]{ 1.0, 2.0, 3.0, 4.0 }, new double[]{ 2.0, 3.0, 4.0, 1.0 }, new double[]{ 3.0, 4.0, 1.0, 2.0 }, new double[]{ 4.0, 1.0, 2.0, 3.0 } };

    /**
     *
     */
    private double[] labels = new double[]{ 1.0, 2.0, 3, 4.0 };

    /**
     *
     */
    private double[][] labelsInSortedOrder = new double[][]{ new double[]{ 1.0, 4.0, 3.0, 2.0 }, new double[]{ 2.0, 1.0, 4.0, 3.0 }, new double[]{ 3.0, 2.0, 1.0, 4.0 }, new double[]{ 4.0, 3.0, 2.0, 1.0 } };

    /**
     *
     */
    private double[][][] featuresInSortedOrder = new double[][][]{ new double[][]{ new double[]{ 1.0, 2.0, 3.0, 4.0 }, new double[]{ 4.0, 1.0, 2.0, 3.0 }, new double[]{ 3.0, 4.0, 1.0, 2.0 }, new double[]{ 2.0, 3.0, 4.0, 1.0 } }, new double[][]{ new double[]{ 2.0, 3.0, 4.0, 1.0 }, new double[]{ 1.0, 2.0, 3.0, 4.0 }, new double[]{ 4.0, 1.0, 2.0, 3.0 }, new double[]{ 3.0, 4.0, 1.0, 2.0 } }, new double[][]{ new double[]{ 3.0, 4.0, 1.0, 2.0 }, new double[]{ 2.0, 3.0, 4.0, 1.0 }, new double[]{ 1.0, 2.0, 3.0, 4.0 }, new double[]{ 4.0, 1.0, 2.0, 3.0 } }, new double[][]{ new double[]{ 4.0, 1.0, 2.0, 3.0 }, new double[]{ 3.0, 4.0, 1.0, 2.0 }, new double[]{ 2.0, 3.0, 4.0, 1.0 }, new double[]{ 1.0, 2.0, 3.0, 4.0 } } };

    /**
     *
     */
    private TreeDataIndex idx = new TreeDataIndex(features, labels);

    /**
     *
     */
    @Test
    public void labelInSortedOrderTest() {
        Assert.assertEquals(features.length, idx.rowsCount());
        Assert.assertEquals(features[0].length, idx.columnsCount());
        for (int k = 0; k < (idx.rowsCount()); k++) {
            for (int featureId = 0; featureId < (idx.columnsCount()); featureId++)
                Assert.assertEquals(labelsInSortedOrder[k][featureId], idx.labelInSortedOrder(k, featureId), 0.01);

        }
    }

    /**
     *
     */
    @Test
    public void featuresInSortedOrderTest() {
        Assert.assertEquals(features.length, idx.rowsCount());
        Assert.assertEquals(features[0].length, idx.columnsCount());
        for (int k = 0; k < (idx.rowsCount()); k++) {
            for (int featureId = 0; featureId < (idx.columnsCount()); featureId++)
                Assert.assertArrayEquals(featuresInSortedOrder[k][featureId], idx.featuresInSortedOrder(k, featureId), 0.01);

        }
    }

    /**
     *
     */
    @Test
    public void featureInSortedOrderTest() {
        Assert.assertEquals(features.length, idx.rowsCount());
        Assert.assertEquals(features[0].length, idx.columnsCount());
        for (int k = 0; k < (idx.rowsCount()); k++) {
            for (int featureId = 0; featureId < (idx.columnsCount()); featureId++)
                Assert.assertEquals((((double) (k)) + 1), idx.featureInSortedOrder(k, featureId), 0.01);

        }
    }

    /**
     *
     */
    @Test
    public void filterTest() {
        TreeFilter filter1 = ( features) -> (features[0]) > 2;
        TreeFilter filter2 = ( features) -> (features[1]) > 2;
        TreeFilter filterAnd = filter1.and(( features) -> (features[1]) > 2);
        TreeDataIndex filtered1 = idx.filter(filter1);
        TreeDataIndex filtered2 = filtered1.filter(filter2);
        TreeDataIndex filtered3 = idx.filter(filterAnd);
        Assert.assertEquals(2, filtered1.rowsCount());
        Assert.assertEquals(4, filtered1.columnsCount());
        Assert.assertEquals(1, filtered2.rowsCount());
        Assert.assertEquals(4, filtered2.columnsCount());
        Assert.assertEquals(1, filtered3.rowsCount());
        Assert.assertEquals(4, filtered3.columnsCount());
        double[] obj1 = new double[]{ 3, 4, 1, 2 };
        double[] obj2 = new double[]{ 4, 1, 2, 3 };
        double[][] restObjs = new double[][]{ obj1, obj2 };
        int[][] restObjIndxInSortedOrderPerFeatures = new int[][]{ new int[]{ 0, 1 }// feature 0
        // feature 0
        // feature 0
        , new int[]{ 1, 0 }// feature 1
        // feature 1
        // feature 1
        , new int[]{ 0, 1 }// feature 2
        // feature 2
        // feature 2
        , new int[]{ 0, 1 }// feature 3
        // feature 3
        // feature 3
         };
        for (int featureId = 0; featureId < (filtered1.columnsCount()); featureId++) {
            for (int k = 0; k < (filtered1.rowsCount()); k++) {
                int objId = restObjIndxInSortedOrderPerFeatures[featureId][k];
                double[] obj = restObjs[objId];
                Assert.assertArrayEquals(obj, filtered1.featuresInSortedOrder(k, featureId), 0.01);
            }
        }
        for (int featureId = 0; featureId < (filtered2.columnsCount()); featureId++) {
            for (int k = 0; k < (filtered2.rowsCount()); k++) {
                Assert.assertArrayEquals(obj1, filtered2.featuresInSortedOrder(k, featureId), 0.01);
                Assert.assertArrayEquals(obj1, filtered3.featuresInSortedOrder(k, featureId), 0.01);
            }
        }
    }
}

