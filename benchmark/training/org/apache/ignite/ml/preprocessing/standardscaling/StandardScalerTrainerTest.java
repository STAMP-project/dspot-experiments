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
package org.apache.ignite.ml.preprocessing.standardscaling;


import org.apache.ignite.ml.TestUtils;
import org.apache.ignite.ml.common.TrainerTest;
import org.apache.ignite.ml.dataset.DatasetBuilder;
import org.apache.ignite.ml.math.primitives.vector.Vector;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link StandardScalerTrainer}.
 */
public class StandardScalerTrainerTest extends TrainerTest {
    /**
     * Data.
     */
    private DatasetBuilder<Integer, Vector> datasetBuilder;

    /**
     * Trainer to be tested.
     */
    private StandardScalerTrainer<Integer, Vector> standardizationTrainer;

    /**
     * Test {@code fit()} method.
     */
    @Test
    public void testCalculatesCorrectMeans() {
        double[] expectedMeans = new double[]{ 0.5, 1.75, 4.5, 0.875 };
        StandardScalerPreprocessor<Integer, Vector> preprocessor = standardizationTrainer.fit(TestUtils.testEnvBuilder(), datasetBuilder, ( k, v) -> v);
        Assert.assertArrayEquals(expectedMeans, preprocessor.getMeans(), 1.0E-8);
    }

    /**
     * Test {@code fit()} method.
     */
    @Test
    public void testCalculatesCorrectStandardDeviations() {
        double[] expectedSigmas = new double[]{ 0.5, 1.47901995, 14.51723114, 0.93374247 };
        StandardScalerPreprocessor<Integer, Vector> preprocessor = standardizationTrainer.fit(TestUtils.testEnvBuilder(), datasetBuilder, ( k, v) -> v);
        Assert.assertArrayEquals(expectedSigmas, preprocessor.getSigmas(), 1.0E-8);
    }
}

