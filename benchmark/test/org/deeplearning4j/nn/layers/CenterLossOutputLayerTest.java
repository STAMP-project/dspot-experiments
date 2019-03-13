/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.deeplearning4j.nn.layers;


import java.util.Random;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;


/**
 * Test CenterLossOutputLayer.
 *
 * @author Justin Long (@crockpotveggies)
 */
public class CenterLossOutputLayerTest extends BaseDL4JTest {
    @Test
    public void testLambdaConf() {
        double[] lambdas = new double[]{ 0.1, 0.01 };
        double[] results = new double[2];
        int numClasses = 2;
        INDArray input = Nd4j.rand(150, 4);
        INDArray labels = Nd4j.zeros(150, numClasses);
        Random r = new Random(12345);
        for (int i = 0; i < 150; i++) {
            labels.putScalar(i, r.nextInt(numClasses), 1.0);
        }
        ComputationGraph graph;
        for (int i = 0; i < (lambdas.length); i++) {
            graph = getGraph(numClasses, lambdas[i]);
            graph.setInput(0, input);
            graph.setLabel(0, labels);
            graph.computeGradientAndScore();
            results[i] = graph.score();
        }
        Assert.assertNotEquals(results[0], results[1]);
    }
}

