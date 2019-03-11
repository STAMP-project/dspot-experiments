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
package org.apache.ignite.ml.preprocessing.encoding;


import EncoderSortingStrategy.FREQUENCY_ASC;
import EncoderType.ONE_HOT_ENCODER;
import EncoderType.STRING_ENCODER;
import java.util.HashMap;
import java.util.Map;
import org.apache.ignite.ml.TestUtils;
import org.apache.ignite.ml.common.TrainerTest;
import org.apache.ignite.ml.dataset.DatasetBuilder;
import org.apache.ignite.ml.math.exceptions.preprocessing.UnknownCategorialFeatureValue;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link EncoderTrainer}.
 */
public class EncoderTrainerTest extends TrainerTest {
    /**
     * Tests {@code fit()} method.
     */
    @Test
    public void testFitOnStringCategorialFeatures() {
        Map<Integer, String[]> data = new HashMap<>();
        data.put(1, new String[]{ "Monday", "September" });
        data.put(2, new String[]{ "Monday", "August" });
        data.put(3, new String[]{ "Monday", "August" });
        data.put(4, new String[]{ "Friday", "June" });
        data.put(5, new String[]{ "Friday", "June" });
        data.put(6, new String[]{ "Sunday", "August" });
        DatasetBuilder<Integer, String[]> datasetBuilder = new org.apache.ignite.ml.dataset.impl.local.LocalDatasetBuilder(data, parts);
        EncoderTrainer<Integer, String[]> strEncoderTrainer = new EncoderTrainer<Integer, String[]>().withEncoderType(STRING_ENCODER).withEncodedFeature(0).withEncodedFeature(1);
        EncoderPreprocessor<Integer, String[]> preprocessor = strEncoderTrainer.fit(TestUtils.testEnvBuilder(), datasetBuilder, ( k, v) -> v);
        Assert.assertArrayEquals(new double[]{ 0.0, 2.0 }, preprocessor.apply(7, new String[]{ "Monday", "September" }).asArray(), 1.0E-8);
    }

    /**
     * Tests {@code fit()} method.
     */
    @Test
    public void testFitOnIntegerCategorialFeatures() {
        Map<Integer, Object[]> data = new HashMap<>();
        data.put(1, new Object[]{ 3.0, 0.0 });
        data.put(2, new Object[]{ 3.0, 12.0 });
        data.put(3, new Object[]{ 3.0, 12.0 });
        data.put(4, new Object[]{ 2.0, 45.0 });
        data.put(5, new Object[]{ 2.0, 45.0 });
        data.put(6, new Object[]{ 14.0, 12.0 });
        DatasetBuilder<Integer, Object[]> datasetBuilder = new org.apache.ignite.ml.dataset.impl.local.LocalDatasetBuilder(data, parts);
        EncoderTrainer<Integer, Object[]> strEncoderTrainer = new EncoderTrainer<Integer, Object[]>().withEncoderType(ONE_HOT_ENCODER).withEncodedFeature(0).withEncodedFeature(1);
        EncoderPreprocessor<Integer, Object[]> preprocessor = strEncoderTrainer.fit(TestUtils.testEnvBuilder(), datasetBuilder, ( k, v) -> v);
        Assert.assertArrayEquals(new double[]{ 0.0, 2.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0 }, preprocessor.apply(7, new Double[]{ 3.0, 0.0 }).asArray(), 1.0E-8);
        Assert.assertArrayEquals(new double[]{ 1.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0 }, preprocessor.apply(8, new Double[]{ 2.0, 12.0 }).asArray(), 1.0E-8);
    }

    /**
     * Tests {@code fit()} method.
     */
    @Test
    public void testFitWithUnknownStringValueInTheGivenData() {
        Map<Integer, Object[]> data = new HashMap<>();
        data.put(1, new Object[]{ 3.0, 0.0 });
        data.put(2, new Object[]{ 3.0, 12.0 });
        data.put(3, new Object[]{ 3.0, 12.0 });
        data.put(4, new Object[]{ 2.0, 45.0 });
        data.put(5, new Object[]{ 2.0, 45.0 });
        data.put(6, new Object[]{ 14.0, 12.0 });
        DatasetBuilder<Integer, Object[]> datasetBuilder = new org.apache.ignite.ml.dataset.impl.local.LocalDatasetBuilder(data, parts);
        EncoderTrainer<Integer, Object[]> strEncoderTrainer = new EncoderTrainer<Integer, Object[]>().withEncoderType(STRING_ENCODER).withEncodedFeature(0).withEncodedFeature(1);
        EncoderPreprocessor<Integer, Object[]> preprocessor = strEncoderTrainer.fit(TestUtils.testEnvBuilder(), datasetBuilder, ( k, v) -> v);
        try {
            preprocessor.apply(7, new String[]{ "Monday", "September" }).asArray();
            Assert.fail("UnknownCategorialFeatureValue");
        } catch (UnknownCategorialFeatureValue e) {
            return;
        }
        Assert.fail("UnknownCategorialFeatureValue");
    }

    /**
     * Tests {@code fit()} method.
     */
    @Test
    public void testFitOnStringCategorialFeaturesWithReversedOrder() {
        Map<Integer, String[]> data = new HashMap<>();
        data.put(1, new String[]{ "Monday", "September" });
        data.put(2, new String[]{ "Monday", "August" });
        data.put(3, new String[]{ "Monday", "August" });
        data.put(4, new String[]{ "Friday", "June" });
        data.put(5, new String[]{ "Friday", "June" });
        data.put(6, new String[]{ "Sunday", "August" });
        DatasetBuilder<Integer, String[]> datasetBuilder = new org.apache.ignite.ml.dataset.impl.local.LocalDatasetBuilder(data, parts);
        EncoderTrainer<Integer, String[]> strEncoderTrainer = new EncoderTrainer<Integer, String[]>().withEncoderType(STRING_ENCODER).withEncoderIndexingStrategy(FREQUENCY_ASC).withEncodedFeature(0).withEncodedFeature(1);
        EncoderPreprocessor<Integer, String[]> preprocessor = strEncoderTrainer.fit(TestUtils.testEnvBuilder(), datasetBuilder, ( k, v) -> v);
        Assert.assertArrayEquals(new double[]{ 2.0, 0.0 }, preprocessor.apply(7, new String[]{ "Monday", "September" }).asArray(), 1.0E-8);
    }
}

