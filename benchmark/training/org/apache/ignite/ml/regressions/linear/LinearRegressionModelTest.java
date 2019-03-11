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
package org.apache.ignite.ml.regressions.linear;


import org.apache.ignite.ml.TestUtils;
import org.apache.ignite.ml.math.exceptions.CardinalityException;
import org.apache.ignite.ml.math.primitives.vector.Vector;
import org.apache.ignite.ml.math.primitives.vector.impl.DenseVector;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link LinearRegressionModel}.
 */
public class LinearRegressionModelTest {
    /**
     *
     */
    private static final double PRECISION = 1.0E-6;

    /**
     *
     */
    @Test
    public void testPredict() {
        Vector weights = new DenseVector(new double[]{ 2.0, 3.0 });
        LinearRegressionModel mdl = new LinearRegressionModel(weights, 1.0);
        Assert.assertTrue((!(mdl.toString().isEmpty())));
        Assert.assertTrue((!(mdl.toString(true).isEmpty())));
        Assert.assertTrue((!(mdl.toString(false).isEmpty())));
        Vector observation = new DenseVector(new double[]{ 1.0, 1.0 });
        TestUtils.assertEquals(((1.0 + (2.0 * 1.0)) + (3.0 * 1.0)), mdl.predict(observation), LinearRegressionModelTest.PRECISION);
        observation = new DenseVector(new double[]{ 2.0, 1.0 });
        TestUtils.assertEquals(((1.0 + (2.0 * 2.0)) + (3.0 * 1.0)), mdl.predict(observation), LinearRegressionModelTest.PRECISION);
        observation = new DenseVector(new double[]{ 1.0, 2.0 });
        TestUtils.assertEquals(((1.0 + (2.0 * 1.0)) + (3.0 * 2.0)), mdl.predict(observation), LinearRegressionModelTest.PRECISION);
        observation = new DenseVector(new double[]{ -2.0, 1.0 });
        TestUtils.assertEquals(((1.0 - (2.0 * 2.0)) + (3.0 * 1.0)), mdl.predict(observation), LinearRegressionModelTest.PRECISION);
        observation = new DenseVector(new double[]{ 1.0, -2.0 });
        TestUtils.assertEquals(((1.0 + (2.0 * 1.0)) - (3.0 * 2.0)), mdl.predict(observation), LinearRegressionModelTest.PRECISION);
    }

    /**
     *
     */
    @Test(expected = CardinalityException.class)
    public void testPredictOnAnObservationWithWrongCardinality() {
        Vector weights = new DenseVector(new double[]{ 2.0, 3.0 });
        LinearRegressionModel mdl = new LinearRegressionModel(weights, 1.0);
        Vector observation = new DenseVector(new double[]{ 1.0 });
        mdl.predict(observation);
    }
}

