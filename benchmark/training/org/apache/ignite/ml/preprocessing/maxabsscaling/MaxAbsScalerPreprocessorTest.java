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
package org.apache.ignite.ml.preprocessing.maxabsscaling;


import org.apache.ignite.ml.math.primitives.vector.Vector;
import org.apache.ignite.ml.math.primitives.vector.VectorUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link MaxAbsScalerPreprocessor}.
 */
public class MaxAbsScalerPreprocessorTest {
    /**
     * Tests {@code apply()} method.
     */
    @Test
    public void testApply() {
        double[][] data = new double[][]{ new double[]{ 2.0, 4.0, 1.0 }, new double[]{ 1.0, 8.0, 22.0 }, new double[]{ -4.0, 10.0, 100.0 }, new double[]{ 0.0, 22.0, 300.0 } };
        double[] maxAbs = new double[]{ 4, 22, 300 };
        MaxAbsScalerPreprocessor<Integer, Vector> preprocessor = new MaxAbsScalerPreprocessor(maxAbs, ( k, v) -> v);
        double[][] expData = new double[][]{ new double[]{ 0.5, 4.0 / 22, 1.0 / 300 }, new double[]{ 0.25, 8.0 / 22, 22.0 / 300 }, new double[]{ -1.0, 10.0 / 22, 100.0 / 300 }, new double[]{ 0.0, 22.0 / 22, 300.0 / 300 } };
        for (int i = 0; i < (data.length); i++)
            Assert.assertArrayEquals(expData[i], preprocessor.apply(i, VectorUtils.of(data[i])).asArray(), 1.0E-8);

    }
}

