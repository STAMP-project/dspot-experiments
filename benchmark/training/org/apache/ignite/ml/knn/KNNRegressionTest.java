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
package org.apache.ignite.ml.knn;


import NNStrategy.SIMPLE;
import NNStrategy.WEIGHTED;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.ignite.ml.common.TrainerTest;
import org.apache.ignite.ml.knn.regression.KNNRegressionModel;
import org.apache.ignite.ml.knn.regression.KNNRegressionTrainer;
import org.apache.ignite.ml.math.distances.EuclideanDistance;
import org.apache.ignite.ml.math.primitives.vector.Vector;
import org.apache.ignite.ml.math.primitives.vector.VectorUtils;
import org.apache.ignite.ml.math.primitives.vector.impl.DenseVector;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link KNNRegressionTrainer}.
 */
public class KNNRegressionTest extends TrainerTest {
    /**
     *
     */
    @Test
    public void testSimpleRegressionWithOneNeighbour() {
        Map<Integer, double[]> data = new HashMap<>();
        data.put(0, new double[]{ 11.0, 0, 0, 0, 0, 0 });
        data.put(1, new double[]{ 12.0, 2.0, 0, 0, 0, 0 });
        data.put(2, new double[]{ 13.0, 0, 3.0, 0, 0, 0 });
        data.put(3, new double[]{ 14.0, 0, 0, 4.0, 0, 0 });
        data.put(4, new double[]{ 15.0, 0, 0, 0, 5.0, 0 });
        data.put(5, new double[]{ 16.0, 0, 0, 0, 0, 6.0 });
        KNNRegressionTrainer trainer = new KNNRegressionTrainer();
        KNNRegressionModel knnMdl = ((KNNRegressionModel) (trainer.fit(new org.apache.ignite.ml.dataset.impl.local.LocalDatasetBuilder(data, parts), ( k, v) -> VectorUtils.of(Arrays.copyOfRange(v, 1, v.length)), ( k, v) -> v[0]).withK(1).withDistanceMeasure(new EuclideanDistance()).withStrategy(SIMPLE)));
        Vector vector = new DenseVector(new double[]{ 0, 0, 0, 5.0, 0.0 });
        System.out.println(knnMdl.predict(vector));
        Assert.assertEquals(15, knnMdl.predict(vector), 1.0E-12);
    }

    /**
     *
     */
    @Test
    public void testLongly() {
        testLongly(SIMPLE);
    }

    /**
     *
     */
    @Test
    public void testLonglyWithWeightedStrategy() {
        testLongly(WEIGHTED);
    }

    /**
     *
     */
    @Test
    public void testUpdate() {
        Map<Integer, double[]> data = new HashMap<>();
        data.put(0, new double[]{ 11.0, 0, 0, 0, 0, 0 });
        data.put(1, new double[]{ 12.0, 2.0, 0, 0, 0, 0 });
        data.put(2, new double[]{ 13.0, 0, 3.0, 0, 0, 0 });
        data.put(3, new double[]{ 14.0, 0, 0, 4.0, 0, 0 });
        data.put(4, new double[]{ 15.0, 0, 0, 0, 5.0, 0 });
        data.put(5, new double[]{ 16.0, 0, 0, 0, 0, 6.0 });
        KNNRegressionTrainer trainer = new KNNRegressionTrainer();
        KNNRegressionModel originalMdl = ((KNNRegressionModel) (trainer.fit(new org.apache.ignite.ml.dataset.impl.local.LocalDatasetBuilder(data, parts), ( k, v) -> VectorUtils.of(Arrays.copyOfRange(v, 1, v.length)), ( k, v) -> v[0]).withK(1).withDistanceMeasure(new EuclideanDistance()).withStrategy(SIMPLE)));
        KNNRegressionModel updatedOnSameDataset = trainer.update(originalMdl, data, parts, ( k, v) -> VectorUtils.of(Arrays.copyOfRange(v, 0, (v.length - 1))), ( k, v) -> v[2]);
        KNNRegressionModel updatedOnEmptyDataset = trainer.update(originalMdl, new HashMap<Integer, double[]>(), parts, ( k, v) -> VectorUtils.of(Arrays.copyOfRange(v, 0, (v.length - 1))), ( k, v) -> v[2]);
        Vector vector = new DenseVector(new double[]{ 0, 0, 0, 5.0, 0.0 });
        Assert.assertEquals(originalMdl.predict(vector), updatedOnSameDataset.predict(vector));
        Assert.assertEquals(originalMdl.predict(vector), updatedOnEmptyDataset.predict(vector));
    }
}

