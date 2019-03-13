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
package org.apache.ignite.ml.naivebayes.discrete;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.ignite.ml.common.TrainerTest;
import org.apache.ignite.ml.math.primitives.vector.VectorUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for {@link DiscreteNaiveBayesTrainer}
 */
public class DiscreteNaiveBayesTrainerTest extends TrainerTest {
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
     * Binary data.
     */
    private static final Map<Integer, double[]> binarizedData = new HashMap<>();

    /**
     * Data.
     */
    private static final Map<Integer, double[]> data = new HashMap<>();

    /**
     *
     */
    private static final double[][] binarizedDatathresholds = new double[][]{ new double[]{ 0.5 }, new double[]{ 0.5 }, new double[]{ 0.5 }, new double[]{ 0.5 }, new double[]{ 0.5 } };

    /**
     *
     */
    private static final double[][] thresholds = new double[][]{ new double[]{ 4, 8 }, new double[]{ 0.5 }, new double[]{ 0.3, 0.4, 0.5 }, new double[]{ 250, 500, 750 } };

    static {
        DiscreteNaiveBayesTrainerTest.binarizedData.put(0, new double[]{ 0, 0, 1, 1, 1, DiscreteNaiveBayesTrainerTest.LABEL_1 });
        DiscreteNaiveBayesTrainerTest.binarizedData.put(1, new double[]{ 1, 0, 1, 1, 0, DiscreteNaiveBayesTrainerTest.LABEL_1 });
        DiscreteNaiveBayesTrainerTest.binarizedData.put(2, new double[]{ 1, 1, 0, 0, 1, DiscreteNaiveBayesTrainerTest.LABEL_1 });
        DiscreteNaiveBayesTrainerTest.binarizedData.put(3, new double[]{ 1, 1, 0, 0, 0, DiscreteNaiveBayesTrainerTest.LABEL_1 });
        DiscreteNaiveBayesTrainerTest.binarizedData.put(4, new double[]{ 0, 1, 0, 0, 1, DiscreteNaiveBayesTrainerTest.LABEL_1 });
        DiscreteNaiveBayesTrainerTest.binarizedData.put(5, new double[]{ 0, 0, 0, 1, 0, DiscreteNaiveBayesTrainerTest.LABEL_1 });
        DiscreteNaiveBayesTrainerTest.binarizedData.put(6, new double[]{ 1, 0, 0, 1, 1, DiscreteNaiveBayesTrainerTest.LABEL_2 });
        DiscreteNaiveBayesTrainerTest.binarizedData.put(7, new double[]{ 1, 1, 0, 0, 1, DiscreteNaiveBayesTrainerTest.LABEL_2 });
        DiscreteNaiveBayesTrainerTest.binarizedData.put(8, new double[]{ 1, 1, 1, 1, 0, DiscreteNaiveBayesTrainerTest.LABEL_2 });
        DiscreteNaiveBayesTrainerTest.binarizedData.put(9, new double[]{ 1, 1, 0, 1, 0, DiscreteNaiveBayesTrainerTest.LABEL_2 });
        DiscreteNaiveBayesTrainerTest.binarizedData.put(10, new double[]{ 1, 1, 0, 1, 1, DiscreteNaiveBayesTrainerTest.LABEL_2 });
        DiscreteNaiveBayesTrainerTest.binarizedData.put(11, new double[]{ 1, 0, 1, 1, 0, DiscreteNaiveBayesTrainerTest.LABEL_2 });
        DiscreteNaiveBayesTrainerTest.binarizedData.put(12, new double[]{ 1, 0, 1, 0, 0, DiscreteNaiveBayesTrainerTest.LABEL_2 });
        DiscreteNaiveBayesTrainerTest.data.put(0, new double[]{ 2, 0, 0.34, 123, DiscreteNaiveBayesTrainerTest.LABEL_1 });
        DiscreteNaiveBayesTrainerTest.data.put(1, new double[]{ 8, 0, 0.37, 561, DiscreteNaiveBayesTrainerTest.LABEL_1 });
        DiscreteNaiveBayesTrainerTest.data.put(2, new double[]{ 5, 1, 0.01, 678, DiscreteNaiveBayesTrainerTest.LABEL_1 });
        DiscreteNaiveBayesTrainerTest.data.put(3, new double[]{ 2, 1, 0.32, 453, DiscreteNaiveBayesTrainerTest.LABEL_1 });
        DiscreteNaiveBayesTrainerTest.data.put(4, new double[]{ 7, 1, 0.67, 980, DiscreteNaiveBayesTrainerTest.LABEL_1 });
        DiscreteNaiveBayesTrainerTest.data.put(5, new double[]{ 2, 1, 0.69, 912, DiscreteNaiveBayesTrainerTest.LABEL_1 });
        DiscreteNaiveBayesTrainerTest.data.put(6, new double[]{ 8, 0, 0.43, 453, DiscreteNaiveBayesTrainerTest.LABEL_1 });
        DiscreteNaiveBayesTrainerTest.data.put(7, new double[]{ 2, 0, 0.45, 752, DiscreteNaiveBayesTrainerTest.LABEL_1 });
        DiscreteNaiveBayesTrainerTest.data.put(8, new double[]{ 7, 1, 0.01, 132, DiscreteNaiveBayesTrainerTest.LABEL_2 });
        DiscreteNaiveBayesTrainerTest.data.put(9, new double[]{ 2, 1, 0.68, 169, DiscreteNaiveBayesTrainerTest.LABEL_2 });
        DiscreteNaiveBayesTrainerTest.data.put(10, new double[]{ 8, 0, 0.43, 453, DiscreteNaiveBayesTrainerTest.LABEL_2 });
        DiscreteNaiveBayesTrainerTest.data.put(11, new double[]{ 2, 1, 0.45, 748, DiscreteNaiveBayesTrainerTest.LABEL_2 });
    }

    /**
     *
     */
    private DiscreteNaiveBayesTrainer trainer;

    /**
     *
     */
    @Test
    public void testReturnsCorrectLabelProbalities() {
        DiscreteNaiveBayesModel model = trainer.fit(new org.apache.ignite.ml.dataset.impl.local.LocalDatasetBuilder(DiscreteNaiveBayesTrainerTest.binarizedData, parts), ( k, v) -> VectorUtils.of(Arrays.copyOfRange(v, 0, (v.length - 1))), ( k, v) -> v[(v.length - 1)]);
        double[] expectedProbabilities = new double[]{ 6.0 / (DiscreteNaiveBayesTrainerTest.binarizedData.size()), 7.0 / (DiscreteNaiveBayesTrainerTest.binarizedData.size()) };
        Assert.assertArrayEquals(expectedProbabilities, model.getClsProbabilities(), DiscreteNaiveBayesTrainerTest.PRECISION);
    }

    /**
     *
     */
    @Test
    public void testReturnsEquivalentProbalitiesWhenSetEquiprobableClasses_() {
        DiscreteNaiveBayesTrainer trainer = new DiscreteNaiveBayesTrainer().setBucketThresholds(DiscreteNaiveBayesTrainerTest.binarizedDatathresholds).withEquiprobableClasses();
        DiscreteNaiveBayesModel model = trainer.fit(new org.apache.ignite.ml.dataset.impl.local.LocalDatasetBuilder(DiscreteNaiveBayesTrainerTest.binarizedData, parts), ( k, v) -> VectorUtils.of(Arrays.copyOfRange(v, 0, (v.length - 1))), ( k, v) -> v[(v.length - 1)]);
        Assert.assertArrayEquals(new double[]{ 0.5, 0.5 }, model.getClsProbabilities(), DiscreteNaiveBayesTrainerTest.PRECISION);
    }

    /**
     *
     */
    @Test
    public void testReturnsPresetProbalitiesWhenSetPriorProbabilities() {
        double[] priorProbabilities = new double[]{ 0.35, 0.65 };
        DiscreteNaiveBayesTrainer trainer = new DiscreteNaiveBayesTrainer().setBucketThresholds(DiscreteNaiveBayesTrainerTest.binarizedDatathresholds).setPriorProbabilities(priorProbabilities);
        DiscreteNaiveBayesModel model = trainer.fit(new org.apache.ignite.ml.dataset.impl.local.LocalDatasetBuilder(DiscreteNaiveBayesTrainerTest.binarizedData, parts), ( k, v) -> VectorUtils.of(Arrays.copyOfRange(v, 0, (v.length - 1))), ( k, v) -> v[(v.length - 1)]);
        Assert.assertArrayEquals(priorProbabilities, model.getClsProbabilities(), DiscreteNaiveBayesTrainerTest.PRECISION);
    }

    /**
     *
     */
    @Test
    public void testReturnsCorrectPriorProbabilities() {
        double[][][] expectedPriorProbabilites = new double[][][]{ new double[][]{ new double[]{ 0.5, 0.5 }, new double[]{ 0.5, 0.5 }, new double[]{ 2.0 / 3.0, 1.0 / 3.0 }, new double[]{ 0.5, 0.5 }, new double[]{ 0.5, 0.5 } }, new double[][]{ new double[]{ 0, 1 }, new double[]{ 3.0 / 7, 4.0 / 7 }, new double[]{ 4.0 / 7, 3.0 / 7 }, new double[]{ 2.0 / 7, 5.0 / 7 }, new double[]{ 4.0 / 7, 3.0 / 7 } } };
        DiscreteNaiveBayesModel model = trainer.fit(new org.apache.ignite.ml.dataset.impl.local.LocalDatasetBuilder(DiscreteNaiveBayesTrainerTest.binarizedData, parts), ( k, v) -> VectorUtils.of(Arrays.copyOfRange(v, 0, (v.length - 1))), ( k, v) -> v[(v.length - 1)]);
        for (int i = 0; i < (expectedPriorProbabilites.length); i++) {
            for (int j = 0; j < (expectedPriorProbabilites[i].length); j++)
                Assert.assertArrayEquals(expectedPriorProbabilites[i][j], model.getProbabilities()[i][j], DiscreteNaiveBayesTrainerTest.PRECISION);

        }
    }

    /**
     *
     */
    @Test
    public void testReturnsCorrectPriorProbabilitiesWithDefferentThresholds() {
        double[][][] expectedPriorProbabilites = new double[][][]{ new double[][]{ new double[]{ 4.0 / 8, 2.0 / 8, 2.0 / 8 }, new double[]{ 0.5, 0.5 }, new double[]{ 1.0 / 8, 3.0 / 8, 2.0 / 8, 2.0 / 8 }, new double[]{ 1.0 / 8, 2.0 / 8, 2.0 / 8, 3.0 / 8 } }, new double[][]{ new double[]{ 2.0 / 4, 1.0 / 4, 1.0 / 4 }, new double[]{ 1.0 / 4, 3.0 / 4 }, new double[]{ 1.0 / 4, 0, 2.0 / 4, 1.0 / 4 }, new double[]{ 2.0 / 4, 1.0 / 4, 1.0 / 4, 0 } } };
        DiscreteNaiveBayesModel model = trainer.setBucketThresholds(DiscreteNaiveBayesTrainerTest.thresholds).fit(new org.apache.ignite.ml.dataset.impl.local.LocalDatasetBuilder(DiscreteNaiveBayesTrainerTest.data, parts), ( k, v) -> VectorUtils.of(Arrays.copyOfRange(v, 0, (v.length - 1))), ( k, v) -> v[(v.length - 1)]);
        for (int i = 0; i < (expectedPriorProbabilites.length); i++) {
            for (int j = 0; j < (expectedPriorProbabilites[i].length); j++)
                Assert.assertArrayEquals(expectedPriorProbabilites[i][j], model.getProbabilities()[i][j], DiscreteNaiveBayesTrainerTest.PRECISION);

        }
    }
}

