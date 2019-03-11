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
package org.apache.ignite.ml.naivebayes.gaussian;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.ignite.ml.TestUtils;
import org.apache.ignite.ml.common.TrainerTest;
import org.apache.ignite.ml.math.primitives.vector.VectorUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link GaussianNaiveBayesTrainer}.
 */
public class GaussianNaiveBayesTrainerTest extends TrainerTest {
    /**
     * Precision in test checks.
     */
    private static final double PRECISION = 0.01;

    /**
     *
     */
    private static final double LABEL_1 = 1.0;

    /**
     *
     */
    private static final double LABEL_2 = 2.0;

    /**
     * Data.
     */
    private static final Map<Integer, double[]> data = new HashMap<>();

    /**
     *
     */
    private static final Map<Integer, double[]> singleLabeldata1 = new HashMap<>();

    /**
     *
     */
    private static final Map<Integer, double[]> singleLabeldata2 = new HashMap<>();

    static {
        GaussianNaiveBayesTrainerTest.data.put(0, new double[]{ 1.0, -1.0, GaussianNaiveBayesTrainerTest.LABEL_1 });
        GaussianNaiveBayesTrainerTest.data.put(1, new double[]{ -1.0, 2.0, GaussianNaiveBayesTrainerTest.LABEL_1 });
        GaussianNaiveBayesTrainerTest.data.put(2, new double[]{ 6.0, 1.0, GaussianNaiveBayesTrainerTest.LABEL_1 });
        GaussianNaiveBayesTrainerTest.data.put(3, new double[]{ -3.0, 2.0, GaussianNaiveBayesTrainerTest.LABEL_2 });
        GaussianNaiveBayesTrainerTest.data.put(4, new double[]{ -5.0, -2.0, GaussianNaiveBayesTrainerTest.LABEL_2 });
        GaussianNaiveBayesTrainerTest.singleLabeldata1.put(0, new double[]{ 1.0, -1.0, GaussianNaiveBayesTrainerTest.LABEL_1 });
        GaussianNaiveBayesTrainerTest.singleLabeldata1.put(1, new double[]{ -1.0, 2.0, GaussianNaiveBayesTrainerTest.LABEL_1 });
        GaussianNaiveBayesTrainerTest.singleLabeldata1.put(2, new double[]{ 6.0, 1.0, GaussianNaiveBayesTrainerTest.LABEL_1 });
        GaussianNaiveBayesTrainerTest.singleLabeldata2.put(0, new double[]{ -3.0, 2.0, GaussianNaiveBayesTrainerTest.LABEL_2 });
        GaussianNaiveBayesTrainerTest.singleLabeldata2.put(1, new double[]{ -5.0, -2.0, GaussianNaiveBayesTrainerTest.LABEL_2 });
    }

    /**
     * Trainer.
     */
    private GaussianNaiveBayesTrainer trainer;

    /**
     *
     */
    @Test
    public void testWithLinearlySeparableData() {
        Map<Integer, double[]> cacheMock = new HashMap<>();
        for (int i = 0; i < (GaussianNaiveBayesTrainerTest.twoLinearlySeparableClasses.length); i++)
            cacheMock.put(i, TrainerTest.twoLinearlySeparableClasses[i]);

        GaussianNaiveBayesModel mdl = trainer.fit(cacheMock, parts, ( k, v) -> VectorUtils.of(Arrays.copyOfRange(v, 1, v.length)), ( k, v) -> v[0]);
        TestUtils.assertEquals(0, mdl.predict(VectorUtils.of(100, 10)), GaussianNaiveBayesTrainerTest.PRECISION);
        TestUtils.assertEquals(1, mdl.predict(VectorUtils.of(10, 100)), GaussianNaiveBayesTrainerTest.PRECISION);
    }

    /**
     *
     */
    @Test
    public void testReturnsCorrectLabelProbalities() {
        GaussianNaiveBayesModel model = trainer.fit(new org.apache.ignite.ml.dataset.impl.local.LocalDatasetBuilder(GaussianNaiveBayesTrainerTest.data, parts), ( k, v) -> VectorUtils.of(Arrays.copyOfRange(v, 0, (v.length - 1))), ( k, v) -> v[2]);
        Assert.assertEquals((3.0 / (GaussianNaiveBayesTrainerTest.data.size())), model.getClassProbabilities()[0], GaussianNaiveBayesTrainerTest.PRECISION);
        Assert.assertEquals((2.0 / (GaussianNaiveBayesTrainerTest.data.size())), model.getClassProbabilities()[1], GaussianNaiveBayesTrainerTest.PRECISION);
    }

    /**
     *
     */
    @Test
    public void testReturnsEquivalentProbalitiesWhenSetEquiprobableClasses_() {
        GaussianNaiveBayesTrainer trainer = new GaussianNaiveBayesTrainer().withEquiprobableClasses();
        GaussianNaiveBayesModel model = trainer.fit(new org.apache.ignite.ml.dataset.impl.local.LocalDatasetBuilder(GaussianNaiveBayesTrainerTest.data, parts), ( k, v) -> VectorUtils.of(Arrays.copyOfRange(v, 0, (v.length - 1))), ( k, v) -> v[2]);
        Assert.assertEquals(0.5, model.getClassProbabilities()[0], GaussianNaiveBayesTrainerTest.PRECISION);
        Assert.assertEquals(0.5, model.getClassProbabilities()[1], GaussianNaiveBayesTrainerTest.PRECISION);
    }

    /**
     *
     */
    @Test
    public void testReturnsPresetProbalitiesWhenSetPriorProbabilities() {
        double[] priorProbabilities = new double[]{ 0.35, 0.65 };
        GaussianNaiveBayesTrainer trainer = new GaussianNaiveBayesTrainer().setPriorProbabilities(priorProbabilities);
        GaussianNaiveBayesModel model = trainer.fit(new org.apache.ignite.ml.dataset.impl.local.LocalDatasetBuilder(GaussianNaiveBayesTrainerTest.data, parts), ( k, v) -> VectorUtils.of(Arrays.copyOfRange(v, 0, (v.length - 1))), ( k, v) -> v[2]);
        Assert.assertEquals(priorProbabilities[0], model.getClassProbabilities()[0], GaussianNaiveBayesTrainerTest.PRECISION);
        Assert.assertEquals(priorProbabilities[1], model.getClassProbabilities()[1], GaussianNaiveBayesTrainerTest.PRECISION);
    }

    /**
     *
     */
    @Test
    public void testReturnsCorrectMeans() {
        GaussianNaiveBayesModel model = trainer.fit(new org.apache.ignite.ml.dataset.impl.local.LocalDatasetBuilder(GaussianNaiveBayesTrainerTest.singleLabeldata1, parts), ( k, v) -> VectorUtils.of(Arrays.copyOfRange(v, 0, (v.length - 1))), ( k, v) -> v[2]);
        Assert.assertArrayEquals(new double[]{ 2.0, 2.0 / 3.0 }, model.getMeans()[0], GaussianNaiveBayesTrainerTest.PRECISION);
    }

    /**
     *
     */
    @Test
    public void testReturnsCorrectVariances() {
        GaussianNaiveBayesModel model = trainer.fit(new org.apache.ignite.ml.dataset.impl.local.LocalDatasetBuilder(GaussianNaiveBayesTrainerTest.singleLabeldata1, parts), ( k, v) -> VectorUtils.of(Arrays.copyOfRange(v, 0, (v.length - 1))), ( k, v) -> v[2]);
        double[] expectedVars = new double[]{ 8.666666666666666, 1.5555555555555556 };
        Assert.assertArrayEquals(expectedVars, model.getVariances()[0], GaussianNaiveBayesTrainerTest.PRECISION);
    }

    /**
     *
     */
    @Test
    public void testUpdatigModel() {
        GaussianNaiveBayesModel model = trainer.fit(new org.apache.ignite.ml.dataset.impl.local.LocalDatasetBuilder(GaussianNaiveBayesTrainerTest.singleLabeldata1, parts), ( k, v) -> VectorUtils.of(Arrays.copyOfRange(v, 0, (v.length - 1))), ( k, v) -> v[2]);
        GaussianNaiveBayesModel updatedModel = trainer.updateModel(model, new org.apache.ignite.ml.dataset.impl.local.LocalDatasetBuilder(GaussianNaiveBayesTrainerTest.singleLabeldata2, parts), ( k, v) -> VectorUtils.of(Arrays.copyOfRange(v, 0, (v.length - 1))), ( k, v) -> v[2]);
        Assert.assertEquals((3.0 / (GaussianNaiveBayesTrainerTest.data.size())), updatedModel.getClassProbabilities()[0], GaussianNaiveBayesTrainerTest.PRECISION);
        Assert.assertEquals((2.0 / (GaussianNaiveBayesTrainerTest.data.size())), updatedModel.getClassProbabilities()[1], GaussianNaiveBayesTrainerTest.PRECISION);
    }
}

