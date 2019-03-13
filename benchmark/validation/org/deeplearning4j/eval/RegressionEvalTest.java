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
package org.deeplearning4j.eval;


import Activation.TANH;
import LossFunctions.LossFunction.MSE;
import WeightInit.ZERO;
import java.util.Collections;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;


/**
 *
 *
 * @author Alex Black
 */
public class RegressionEvalTest extends BaseDL4JTest {
    @Test
    public void testRegressionEvalMethods() {
        // Basic sanity check
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().weightInit(ZERO).list().layer(0, new OutputLayer.Builder().activation(TANH).lossFunction(MSE).nIn(10).nOut(5).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        INDArray f = Nd4j.zeros(4, 10);
        INDArray l = Nd4j.ones(4, 5);
        DataSet ds = new DataSet(f, l);
        DataSetIterator iter = new org.deeplearning4j.datasets.iterator.ExistingDataSetIterator(Collections.singletonList(ds));
        RegressionEvaluation re = net.evaluateRegression(iter);
        for (int i = 0; i < 5; i++) {
            Assert.assertEquals(1.0, re.meanSquaredError(i), 1.0E-6);
            Assert.assertEquals(1.0, re.meanAbsoluteError(i), 1.0E-6);
        }
        ComputationGraphConfiguration graphConf = new NeuralNetConfiguration.Builder().weightInit(ZERO).graphBuilder().addInputs("in").addLayer("0", new OutputLayer.Builder().lossFunction(MSE).activation(TANH).nIn(10).nOut(5).build(), "in").setOutputs("0").build();
        ComputationGraph cg = new ComputationGraph(graphConf);
        cg.init();
        RegressionEvaluation re2 = cg.evaluateRegression(iter);
        for (int i = 0; i < 5; i++) {
            Assert.assertEquals(1.0, re2.meanSquaredError(i), 1.0E-6);
            Assert.assertEquals(1.0, re2.meanAbsoluteError(i), 1.0E-6);
        }
    }

    @Test
    public void testRegressionEvalPerOutputMasking() {
        INDArray l = Nd4j.create(new double[][]{ new double[]{ 1, 2, 3 }, new double[]{ 10, 20, 30 }, new double[]{ -5, -10, -20 } });
        INDArray predictions = Nd4j.zeros(l.shape());
        INDArray mask = Nd4j.create(new double[][]{ new double[]{ 0, 1, 1 }, new double[]{ 1, 1, 0 }, new double[]{ 0, 1, 0 } });
        RegressionEvaluation re = new RegressionEvaluation();
        re.eval(l, predictions, mask);
        double[] mse = new double[]{ (10 * 10) / 1.0, (((2 * 2) + (20 * 20)) + (10 * 10)) / 3, (3 * 3) / 1.0 };
        double[] mae = new double[]{ 10.0, ((2 + 20) + 10) / 3.0, 3.0 };
        double[] rmse = new double[]{ 10.0, Math.sqrt(((((2 * 2) + (20 * 20)) + (10 * 10)) / 3.0)), 3.0 };
        for (int i = 0; i < 3; i++) {
            Assert.assertEquals(mse[i], re.meanSquaredError(i), 1.0E-6);
            Assert.assertEquals(mae[i], re.meanAbsoluteError(i), 1.0E-6);
            Assert.assertEquals(rmse[i], re.rootMeanSquaredError(i), 1.0E-6);
        }
    }

    @Test
    public void testRegressionEvalTimeSeriesSplit() {
        INDArray out1 = Nd4j.rand(new int[]{ 3, 5, 20 });
        INDArray outSub1 = out1.get(all(), all(), interval(0, 10));
        INDArray outSub2 = out1.get(all(), all(), interval(10, 20));
        INDArray label1 = Nd4j.rand(new int[]{ 3, 5, 20 });
        INDArray labelSub1 = label1.get(all(), all(), interval(0, 10));
        INDArray labelSub2 = label1.get(all(), all(), interval(10, 20));
        RegressionEvaluation e1 = new RegressionEvaluation();
        RegressionEvaluation e2 = new RegressionEvaluation();
        e1.eval(label1, out1);
        e2.eval(labelSub1, outSub1);
        e2.eval(labelSub2, outSub2);
        Assert.assertEquals(e1, e2);
    }
}

