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
package org.apache.ignite.ml.clustering;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.ignite.ml.clustering.kmeans.KMeansModel;
import org.apache.ignite.ml.clustering.kmeans.KMeansTrainer;
import org.apache.ignite.ml.common.TrainerTest;
import org.apache.ignite.ml.math.primitives.vector.Vector;
import org.apache.ignite.ml.math.primitives.vector.VectorUtils;
import org.apache.ignite.ml.math.primitives.vector.impl.DenseVector;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link KMeansTrainer}.
 */
public class KMeansTrainerTest extends TrainerTest {
    /**
     * Precision in test checks.
     */
    private static final double PRECISION = 0.01;

    /**
     * Data.
     */
    private static final Map<Integer, double[]> data = new HashMap<>();

    static {
        KMeansTrainerTest.data.put(0, new double[]{ 1.0, 1.0, 1.0 });
        KMeansTrainerTest.data.put(1, new double[]{ 1.0, 2.0, 1.0 });
        KMeansTrainerTest.data.put(2, new double[]{ 2.0, 1.0, 1.0 });
        KMeansTrainerTest.data.put(3, new double[]{ -1.0, -1.0, 2.0 });
        KMeansTrainerTest.data.put(4, new double[]{ -1.0, -2.0, 2.0 });
        KMeansTrainerTest.data.put(5, new double[]{ -2.0, -1.0, 2.0 });
    }

    /**
     * A few points, one cluster, one iteration
     */
    @Test
    public void findOneClusters() {
        KMeansTrainer trainer = createAndCheckTrainer();
        KMeansModel knnMdl = trainer.withAmountOfClusters(1).fit(new org.apache.ignite.ml.dataset.impl.local.LocalDatasetBuilder(KMeansTrainerTest.data, parts), ( k, v) -> VectorUtils.of(Arrays.copyOfRange(v, 0, (v.length - 1))), ( k, v) -> v[2]);
        Vector firstVector = new DenseVector(new double[]{ 2.0, 2.0 });
        Assert.assertEquals(knnMdl.predict(firstVector), 0.0, KMeansTrainerTest.PRECISION);
        Vector secondVector = new DenseVector(new double[]{ -2.0, -2.0 });
        Assert.assertEquals(knnMdl.predict(secondVector), 0.0, KMeansTrainerTest.PRECISION);
        Assert.assertEquals(trainer.getMaxIterations(), 1);
        Assert.assertEquals(trainer.getEpsilon(), KMeansTrainerTest.PRECISION, KMeansTrainerTest.PRECISION);
    }

    /**
     *
     */
    @Test
    public void testUpdateMdl() {
        KMeansTrainer trainer = createAndCheckTrainer();
        KMeansModel originalMdl = trainer.withAmountOfClusters(1).fit(new org.apache.ignite.ml.dataset.impl.local.LocalDatasetBuilder(KMeansTrainerTest.data, parts), ( k, v) -> VectorUtils.of(Arrays.copyOfRange(v, 0, (v.length - 1))), ( k, v) -> v[2]);
        KMeansModel updatedMdlOnSameDataset = trainer.update(originalMdl, new org.apache.ignite.ml.dataset.impl.local.LocalDatasetBuilder(KMeansTrainerTest.data, parts), ( k, v) -> VectorUtils.of(Arrays.copyOfRange(v, 0, (v.length - 1))), ( k, v) -> v[2]);
        KMeansModel updatedMdlOnEmptyDataset = trainer.update(originalMdl, new org.apache.ignite.ml.dataset.impl.local.LocalDatasetBuilder(new HashMap<Integer, double[]>(), parts), ( k, v) -> VectorUtils.of(Arrays.copyOfRange(v, 0, (v.length - 1))), ( k, v) -> v[2]);
        Vector firstVector = new DenseVector(new double[]{ 2.0, 2.0 });
        Vector secondVector = new DenseVector(new double[]{ -2.0, -2.0 });
        Assert.assertEquals(originalMdl.predict(firstVector), updatedMdlOnSameDataset.predict(firstVector), KMeansTrainerTest.PRECISION);
        Assert.assertEquals(originalMdl.predict(secondVector), updatedMdlOnSameDataset.predict(secondVector), KMeansTrainerTest.PRECISION);
        Assert.assertEquals(originalMdl.predict(firstVector), updatedMdlOnEmptyDataset.predict(firstVector), KMeansTrainerTest.PRECISION);
        Assert.assertEquals(originalMdl.predict(secondVector), updatedMdlOnEmptyDataset.predict(secondVector), KMeansTrainerTest.PRECISION);
    }
}

