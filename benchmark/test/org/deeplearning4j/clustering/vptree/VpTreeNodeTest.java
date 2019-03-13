/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.deeplearning4j.clustering.vptree;


import java.util.ArrayList;
import java.util.List;
import org.deeplearning4j.clustering.sptree.DataPoint;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.exception.ND4JIllegalStateException;
import org.nd4j.linalg.factory.Nd4j;


/**
 *
 *
 * @author Anatoly Borisov
 */
public class VpTreeNodeTest {
    private static class DistIndex implements Comparable<VpTreeNodeTest.DistIndex> {
        public double dist;

        public int index;

        public int compareTo(VpTreeNodeTest.DistIndex r) {
            return Double.compare(dist, r.dist);
        }
    }

    @Test
    public void testKnnK() {
        INDArray arr = Nd4j.randn(10, 5);
        VPTree t = new VPTree(arr, false);
        List<DataPoint> resultList = new ArrayList<>();
        List<Double> distances = new ArrayList<>();
        t.search(arr.getRow(0), 5, resultList, distances);
        Assert.assertEquals(5, resultList.size());
    }

    @Test
    public void testParallel() {
        Nd4j.getRandom().setSeed(7);
        INDArray randn = Nd4j.rand(1000, 100);
        VPTree vpTree = new VPTree(randn, false, 2);
        Nd4j.getRandom().setSeed(7);
        VPTree vpTreeNoParallel = new VPTree(randn, false, 1);
        List<DataPoint> results = new ArrayList<>();
        List<Double> distances = new ArrayList<>();
        List<DataPoint> noParallelResults = new ArrayList<>();
        List<Double> noDistances = new ArrayList<>();
        vpTree.search(randn.getRow(0), 10, results, distances);
        vpTreeNoParallel.search(randn.getRow(0), 10, noParallelResults, noDistances);
        Assert.assertEquals(noParallelResults.size(), results.size());
        Assert.assertEquals(noParallelResults, results);
        Assert.assertEquals(noDistances, distances);
    }

    @Test
    public void knnManualRandom() {
        VpTreeNodeTest.knnManual(Nd4j.randn(3, 5));
    }

    @Test
    public void knnManualNaturals() {
        VpTreeNodeTest.knnManual(VpTreeNodeTest.generateNaturalsMatrix(20, 2));
    }

    @Test
    public void vpTreeTest() {
        List<DataPoint> points = new ArrayList<>();
        points.add(new DataPoint(0, Nd4j.create(new double[]{ 55, 55 })));
        points.add(new DataPoint(1, Nd4j.create(new double[]{ 60, 60 })));
        points.add(new DataPoint(2, Nd4j.create(new double[]{ 65, 65 })));
        VPTree tree = new VPTree(points, "euclidean");
        List<DataPoint> add = new ArrayList<>();
        List<Double> distances = new ArrayList<>();
        tree.search(Nd4j.create(new double[]{ 50, 50 }), 1, add, distances);
        DataPoint assertion = add.get(0);
        Assert.assertEquals(new DataPoint(0, Nd4j.create(new double[]{ 55, 55 })), assertion);
        tree.search(Nd4j.create(new double[]{ 60, 60 }), 1, add, distances);
        assertion = add.get(0);
        Assert.assertEquals(Nd4j.create(new double[]{ 60, 60 }), assertion.getPoint());
    }

    @Test(expected = ND4JIllegalStateException.class)
    public void vpTreeTest2() {
        List<DataPoint> points = new ArrayList<>();
        points.add(new DataPoint(0, Nd4j.create(new double[]{ 55, 55 })));
        points.add(new DataPoint(1, Nd4j.create(new double[]{ 60, 60 })));
        points.add(new DataPoint(2, Nd4j.create(new double[]{ 65, 65 })));
        VPTree tree = new VPTree(points, "euclidean");
        tree.search(Nd4j.create(1, 10), 2, new ArrayList<DataPoint>(), new ArrayList<Double>());
    }

    @Test(expected = ND4JIllegalStateException.class)
    public void vpTreeTest3() {
        List<DataPoint> points = new ArrayList<>();
        points.add(new DataPoint(0, Nd4j.create(new double[]{ 55, 55 })));
        points.add(new DataPoint(1, Nd4j.create(new double[]{ 60, 60 })));
        points.add(new DataPoint(2, Nd4j.create(new double[]{ 65, 65 })));
        VPTree tree = new VPTree(points, "euclidean");
        tree.search(Nd4j.create(2, 10), 2, new ArrayList<DataPoint>(), new ArrayList<Double>());
    }

    @Test(expected = ND4JIllegalStateException.class)
    public void vpTreeTest4() {
        List<DataPoint> points = new ArrayList<>();
        points.add(new DataPoint(0, Nd4j.create(new double[]{ 55, 55 })));
        points.add(new DataPoint(1, Nd4j.create(new double[]{ 60, 60 })));
        points.add(new DataPoint(2, Nd4j.create(new double[]{ 65, 65 })));
        VPTree tree = new VPTree(points, "euclidean");
        tree.search(Nd4j.create(2, 10, 10), 2, new ArrayList<DataPoint>(), new ArrayList<Double>());
    }

    @Test
    public void testVPSearchOverNaturals1D() throws Exception {
        VpTreeNodeTest.testVPSearchOverNaturalsPD(20, 1, 5);
    }

    @Test
    public void testVPSearchOverNaturals2D() throws Exception {
        VpTreeNodeTest.testVPSearchOverNaturalsPD(20, 2, 5);
    }
}

