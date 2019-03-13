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
package org.apache.ignite.ml.tree.impurity.gini;


import java.util.HashMap;
import java.util.Map;
import org.apache.ignite.ml.tree.data.DecisionTreeData;
import org.apache.ignite.ml.tree.impurity.util.StepFunction;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests for {@link GiniImpurityMeasureCalculator}.
 */
@RunWith(Parameterized.class)
public class GiniImpurityMeasureCalculatorTest {
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
        double[][] data = new double[][]{ new double[]{ 0, 1 }, new double[]{ 1, 0 }, new double[]{ 2, 2 }, new double[]{ 3, 3 } };
        double[] labels = new double[]{ 0, 1, 1, 1 };
        Map<Double, Integer> encoder = new HashMap<>();
        encoder.put(0.0, 0);
        encoder.put(1.0, 1);
        GiniImpurityMeasureCalculator calculator = new GiniImpurityMeasureCalculator(encoder, useIdx);
        StepFunction<GiniImpurityMeasure>[] impurity = calculator.calculate(new DecisionTreeData(data, labels, useIdx), ( fs) -> true, 0);
        Assert.assertEquals(2, impurity.length);
        // Check Gini calculated for the first column.
        Assert.assertArrayEquals(new double[]{ Double.NEGATIVE_INFINITY, 0, 1, 2, 3 }, impurity[0].getX(), 1.0E-10);
        Assert.assertEquals((-2.5), impurity[0].getY()[0].impurity(), 0.001);
        Assert.assertEquals((-4.0), impurity[0].getY()[1].impurity(), 0.001);
        Assert.assertEquals((-3.0), impurity[0].getY()[2].impurity(), 0.001);
        Assert.assertEquals((-2.666), impurity[0].getY()[3].impurity(), 0.001);
        Assert.assertEquals((-2.5), impurity[0].getY()[4].impurity(), 0.001);
        // Check Gini calculated for the second column.
        Assert.assertArrayEquals(new double[]{ Double.NEGATIVE_INFINITY, 0, 1, 2, 3 }, impurity[1].getX(), 1.0E-10);
        Assert.assertEquals((-2.5), impurity[1].getY()[0].impurity(), 0.001);
        Assert.assertEquals((-2.666), impurity[1].getY()[1].impurity(), 0.001);
        Assert.assertEquals((-3.0), impurity[1].getY()[2].impurity(), 0.001);
        Assert.assertEquals((-2.666), impurity[1].getY()[3].impurity(), 0.001);
        Assert.assertEquals((-2.5), impurity[1].getY()[4].impurity(), 0.001);
    }

    /**
     *
     */
    @Test
    public void testCalculateWithRepeatedData() {
        double[][] data = new double[][]{ new double[]{ 0 }, new double[]{ 1 }, new double[]{ 2 }, new double[]{ 2 }, new double[]{ 3 } };
        double[] labels = new double[]{ 0, 1, 1, 1, 1 };
        Map<Double, Integer> encoder = new HashMap<>();
        encoder.put(0.0, 0);
        encoder.put(1.0, 1);
        GiniImpurityMeasureCalculator calculator = new GiniImpurityMeasureCalculator(encoder, useIdx);
        StepFunction<GiniImpurityMeasure>[] impurity = calculator.calculate(new DecisionTreeData(data, labels, useIdx), ( fs) -> true, 0);
        Assert.assertEquals(1, impurity.length);
        // Check Gini calculated for the first column.
        Assert.assertArrayEquals(new double[]{ Double.NEGATIVE_INFINITY, 0, 1, 2, 3 }, impurity[0].getX(), 1.0E-10);
        Assert.assertEquals((-3.4), impurity[0].getY()[0].impurity(), 0.001);
        Assert.assertEquals((-5.0), impurity[0].getY()[1].impurity(), 0.001);
        Assert.assertEquals((-4.0), impurity[0].getY()[2].impurity(), 0.001);
        Assert.assertEquals((-3.5), impurity[0].getY()[3].impurity(), 0.001);
        Assert.assertEquals((-3.4), impurity[0].getY()[4].impurity(), 0.001);
    }

    /**
     *
     */
    @Test
    public void testGetLabelCode() {
        Map<Double, Integer> encoder = new HashMap<>();
        encoder.put(0.0, 0);
        encoder.put(1.0, 1);
        encoder.put(2.0, 2);
        GiniImpurityMeasureCalculator calculator = new GiniImpurityMeasureCalculator(encoder, useIdx);
        Assert.assertEquals(0, calculator.getLabelCode(0.0));
        Assert.assertEquals(1, calculator.getLabelCode(1.0));
        Assert.assertEquals(2, calculator.getLabelCode(2.0));
    }
}

