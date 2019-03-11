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
package org.apache.ignite.ml.tree.impurity.mse;


import org.apache.ignite.ml.tree.data.DecisionTreeData;
import org.apache.ignite.ml.tree.impurity.util.StepFunction;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests for {@link MSEImpurityMeasureCalculator}.
 */
@RunWith(Parameterized.class)
public class MSEImpurityMeasureCalculatorTest {
    /**
     * Use index.
     */
    @Parameterized.Parameter
    public boolean useIdx;

    /**
     *
     */
    @Test
    public void testCalculate() {
        double[][] data = new double[][]{ new double[]{ 0, 2 }, new double[]{ 1, 1 }, new double[]{ 2, 0 }, new double[]{ 3, 3 } };
        double[] labels = new double[]{ 1, 2, 2, 1 };
        MSEImpurityMeasureCalculator calculator = new MSEImpurityMeasureCalculator(useIdx);
        StepFunction<MSEImpurityMeasure>[] impurity = calculator.calculate(new DecisionTreeData(data, labels, useIdx), ( fs) -> true, 0);
        Assert.assertEquals(2, impurity.length);
        // Test MSE calculated for the first column.
        Assert.assertArrayEquals(new double[]{ Double.NEGATIVE_INFINITY, 0, 1, 2, 3 }, impurity[0].getX(), 1.0E-10);
        Assert.assertEquals(1.0, impurity[0].getY()[0].impurity(), 0.001);
        Assert.assertEquals(0.666, impurity[0].getY()[1].impurity(), 0.001);
        Assert.assertEquals(1.0, impurity[0].getY()[2].impurity(), 0.001);
        Assert.assertEquals(0.666, impurity[0].getY()[3].impurity(), 0.001);
        Assert.assertEquals(1.0, impurity[0].getY()[4].impurity(), 0.001);
        // Test MSE calculated for the second column.
        Assert.assertArrayEquals(new double[]{ Double.NEGATIVE_INFINITY, 0, 1, 2, 3 }, impurity[1].getX(), 1.0E-10);
        Assert.assertEquals(1.0, impurity[1].getY()[0].impurity(), 0.001);
        Assert.assertEquals(0.666, impurity[1].getY()[1].impurity(), 0.001);
        Assert.assertEquals(0.0, impurity[1].getY()[2].impurity(), 0.001);
        Assert.assertEquals(0.666, impurity[1].getY()[3].impurity(), 0.001);
        Assert.assertEquals(1.0, impurity[1].getY()[4].impurity(), 0.001);
    }
}

