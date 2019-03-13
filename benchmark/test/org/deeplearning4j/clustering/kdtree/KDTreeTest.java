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
package org.deeplearning4j.clustering.kdtree;


import DataType.FLOAT;
import com.google.common.primitives.Doubles;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.primitives.Pair;


/**
 * Created by agibsonccc on 1/1/15.
 */
public class KDTreeTest {
    @Test
    public void testTree() {
        KDTree tree = new KDTree(2);
        INDArray half = Nd4j.create(new double[]{ 0.5, 0.5 }, new long[]{ 1, 2 }).castTo(FLOAT);
        INDArray one = Nd4j.create(new double[]{ 1, 1 }, new long[]{ 1, 2 }).castTo(FLOAT);
        tree.insert(half);
        tree.insert(one);
        Pair<Double, INDArray> pair = tree.nn(Nd4j.create(new double[]{ 0.5, 0.5 }, new long[]{ 1, 2 }).castTo(FLOAT));
        Assert.assertEquals(half, pair.getValue());
    }

    @Test
    public void testNN() {
        int n = 10;
        // make a KD-tree of dimension {#n}
        KDTree kdTree = new KDTree(n);
        for (int i = -1; i < n; i++) {
            // Insert a unit vector along each dimension
            List<Double> vec = new ArrayList<>(n);
            // i = -1 ensures the origin is in the Tree
            for (int k = 0; k < n; k++) {
                vec.add((k == i ? 1.0 : 0.0));
            }
            INDArray indVec = Nd4j.create(Doubles.toArray(vec), new long[]{ 1, vec.size() }, FLOAT);
            kdTree.insert(indVec);
        }
        Random rand = new Random();
        // random point in the Hypercube
        List<Double> pt = new ArrayList(n);
        for (int k = 0; k < n; k++) {
            pt.add(rand.nextDouble());
        }
        Pair<Double, INDArray> result = kdTree.nn(Nd4j.create(Doubles.toArray(pt), new long[]{ 1, pt.size() }, FLOAT));
        // Always true for points in the unitary hypercube
        Assert.assertTrue(((result.getKey()) < (Double.MAX_VALUE)));
    }
}

