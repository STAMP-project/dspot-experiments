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


import org.apache.ignite.ml.math.primitives.vector.Vector;
import org.apache.ignite.ml.math.primitives.vector.VectorUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link StandardScalerPreprocessor}.
 */
public class StandardScalerPreprocessorTest {
    /**
     * Test {@code apply()} method.
     */
    @Test
    public void testApply() {
        double[][] inputData = new double[][]{ new double[]{ 0, 2.0, 4.0, 0.1 }, new double[]{ 0, 1.0, -18.0, 2.2 }, new double[]{ 1, 4.0, 10.0, -0.1 }, new double[]{ 1, 0.0, 22.0, 1.3 } };
        double[] means = new double[]{ 0.5, 1.75, 4.5, 0.875 };
        double[] sigmas = new double[]{ 0.5, 1.47901995, 14.51723114, 0.93374247 };
        StandardScalerPreprocessor<Integer, Vector> preprocessor = new StandardScalerPreprocessor(means, sigmas, ( k, v) -> v);
        double[][] expectedData = new double[][]{ new double[]{ -1.0, 0.16903085, -0.03444183, -0.82999331 }, new double[]{ -1.0, -0.50709255, -1.54988233, 1.41902081 }, new double[]{ 1.0, 1.52127766, 0.37886012, -1.04418513 }, new double[]{ 1.0, -1.18321596, 1.20546403, 0.45515762 } };
        for (int i = 0; i < (inputData.length); i++)
            Assert.assertArrayEquals(expectedData[i], preprocessor.apply(i, VectorUtils.of(inputData[i])).asArray(), 1.0E-8);

    }
}

