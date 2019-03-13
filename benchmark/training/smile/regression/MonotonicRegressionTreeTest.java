/**
 * *****************************************************************************
 * Copyright (c) 2010 Haifeng Li
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */
package smile.regression;


import java.util.Arrays;
import java.util.Set;
import java.util.function.DoubleUnaryOperator;
import java.util.function.IntToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;


public class MonotonicRegressionTreeTest {
    @Test
    public void test() {
        // Example inspired by http://xgboost.readthedocs.io/en/latest/tutorials/monotonic.html
        int observations = 1000;
        double[][] x = new double[observations][];
        double[] y = new double[observations];
        for (int i = 0; i < observations; i++) {
            double[] features = Math.random(1);
            x[i] = features;
            y[i] = function(features[0]);
        }
        double[] monotonicRegression = new double[]{ 1 };
        RegressionTree regressionTree = new RegressionTree(null, x, y, 100, 5, x[0].length, null, null, null, monotonicRegression);
        double[] monotonicX = IntStream.range(0, 100).mapToDouble(( i) -> i / 200.0).toArray();
        double[] preds = Arrays.stream(monotonicX).map(( input) -> {
            double[] features = new double[]{ input };
            return regressionTree.predict(features);
        }).toArray();
        Set<Double> predsUniq = Arrays.stream(preds).boxed().collect(Collectors.toSet());
        Assert.assertTrue(("Sanity check failed - only one unique value in prediction array :" + (predsUniq.iterator().next())), ((predsUniq.size()) > 1));
        assertMonotonic(preds);
    }
}

