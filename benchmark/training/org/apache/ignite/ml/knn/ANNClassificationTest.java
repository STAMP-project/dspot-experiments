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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.ignite.ml.TestUtils;
import org.apache.ignite.ml.common.TrainerTest;
import org.apache.ignite.ml.knn.ann.ANNClassificationModel;
import org.apache.ignite.ml.knn.ann.ANNClassificationTrainer;
import org.apache.ignite.ml.math.distances.EuclideanDistance;
import org.apache.ignite.ml.math.primitives.vector.VectorUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests behaviour of ANNClassificationTest.
 */
public class ANNClassificationTest extends TrainerTest {
    /**
     *
     */
    @Test
    public void testBinaryClassification() {
        Map<Integer, double[]> cacheMock = new HashMap<>();
        for (int i = 0; i < (ANNClassificationTest.twoClusters.length); i++)
            cacheMock.put(i, TrainerTest.twoClusters[i]);

        ANNClassificationTrainer trainer = new ANNClassificationTrainer().withK(10).withMaxIterations(10).withEpsilon(1.0E-4).withDistance(new EuclideanDistance());
        Assert.assertEquals(10, trainer.getK());
        Assert.assertEquals(10, trainer.getMaxIterations());
        TestUtils.assertEquals(1.0E-4, trainer.getEpsilon(), TrainerTest.PRECISION);
        Assert.assertEquals(new EuclideanDistance(), trainer.getDistance());
        NNClassificationModel mdl = trainer.fit(cacheMock, parts, ( k, v) -> VectorUtils.of(Arrays.copyOfRange(v, 1, v.length)), ( k, v) -> v[0]).withK(3).withDistanceMeasure(new EuclideanDistance()).withStrategy(SIMPLE);
        Assert.assertNotNull(getCandidates());
        Assert.assertTrue(mdl.toString().contains(SIMPLE.name()));
        Assert.assertTrue(mdl.toString(true).contains(SIMPLE.name()));
        Assert.assertTrue(mdl.toString(false).contains(SIMPLE.name()));
    }

    /**
     *
     */
    @Test
    public void testUpdate() {
        Map<Integer, double[]> cacheMock = new HashMap<>();
        for (int i = 0; i < (ANNClassificationTest.twoClusters.length); i++)
            cacheMock.put(i, TrainerTest.twoClusters[i]);

        ANNClassificationTrainer trainer = new ANNClassificationTrainer().withK(10).withMaxIterations(10).withEpsilon(1.0E-4).withDistance(new EuclideanDistance());
        ANNClassificationModel originalMdl = ((ANNClassificationModel) (trainer.fit(cacheMock, parts, ( k, v) -> VectorUtils.of(Arrays.copyOfRange(v, 1, v.length)), ( k, v) -> v[0]).withK(3).withDistanceMeasure(new EuclideanDistance()).withStrategy(SIMPLE)));
        ANNClassificationModel updatedOnSameDataset = ((ANNClassificationModel) (trainer.update(originalMdl, cacheMock, parts, ( k, v) -> VectorUtils.of(Arrays.copyOfRange(v, 0, (v.length - 1))), ( k, v) -> v[2]).withK(3).withDistanceMeasure(new EuclideanDistance()).withStrategy(SIMPLE)));
        ANNClassificationModel updatedOnEmptyDataset = ((ANNClassificationModel) (trainer.update(originalMdl, new HashMap<Integer, double[]>(), parts, ( k, v) -> VectorUtils.of(Arrays.copyOfRange(v, 0, (v.length - 1))), ( k, v) -> v[2]).withK(3).withDistanceMeasure(new EuclideanDistance()).withStrategy(SIMPLE)));
        Assert.assertNotNull(updatedOnSameDataset.getCandidates());
        Assert.assertTrue(updatedOnSameDataset.toString().contains(SIMPLE.name()));
        Assert.assertTrue(updatedOnSameDataset.toString(true).contains(SIMPLE.name()));
        Assert.assertTrue(updatedOnSameDataset.toString(false).contains(SIMPLE.name()));
        Assert.assertNotNull(updatedOnEmptyDataset.getCandidates());
        Assert.assertTrue(updatedOnEmptyDataset.toString().contains(SIMPLE.name()));
        Assert.assertTrue(updatedOnEmptyDataset.toString(true).contains(SIMPLE.name()));
        Assert.assertTrue(updatedOnEmptyDataset.toString(false).contains(SIMPLE.name()));
    }
}

