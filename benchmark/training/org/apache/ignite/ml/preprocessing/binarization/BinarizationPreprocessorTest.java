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
package org.apache.ignite.ml.preprocessing.binarization;


import org.apache.ignite.ml.math.primitives.vector.VectorUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link BinarizationPreprocessor}.
 */
public class BinarizationPreprocessorTest {
    /**
     * Tests {@code apply()} method.
     */
    @Test
    public void testApply() {
        double[][] data = new double[][]{ new double[]{ 1, 2, 3 }, new double[]{ 1, 4, 8 }, new double[]{ 1, 8, 16 } };
        BinarizationPreprocessor<Integer, double[]> preprocessor = new BinarizationPreprocessor(7, ( k, v) -> VectorUtils.of(v));
        double[][] postProcessedData = new double[][]{ new double[]{ 0, 0, 0 }, new double[]{ 0, 0, 1 }, new double[]{ 0, 1, 1 } };
        for (int i = 0; i < (data.length); i++)
            Assert.assertArrayEquals(postProcessedData[i], preprocessor.apply(i, data[i]).asArray(), 1.0E-8);

    }
}

