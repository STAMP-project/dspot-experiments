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
package org.deeplearning4j.nn.layers.samediff;


import Activation.SOFTMAX;
import Activation.TANH;
import ElementWiseVertex.Op;
import LossFunctions.LossFunction.MCXENT;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.TestUtils;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.graph.ScaleVertex;
import org.deeplearning4j.nn.conf.graph.ShiftVertex;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.layers.samediff.testlayers.SameDiffSimpleLambdaLayer;
import org.deeplearning4j.nn.layers.samediff.testlayers.SameDiffSimpleLambdaVertex;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.MultiDataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;


@Slf4j
public class TestSameDiffLambda extends BaseDL4JTest {
    private static final boolean PRINT_RESULTS = true;

    private static final boolean RETURN_ON_FIRST_FAILURE = false;

    private static final double DEFAULT_EPS = 1.0E-6;

    private static final double DEFAULT_MAX_REL_ERROR = 0.001;

    private static final double DEFAULT_MIN_ABS_ERROR = 1.0E-8;

    @Test
    public void testSameDiffLamdaLayerBasic() {
        Nd4j.getRandom().setSeed(12345);
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).updater(new Adam(0.01)).graphBuilder().addInputs("in").addLayer("0", new DenseLayer.Builder().nIn(5).nOut(5).activation(TANH).build(), "in").addLayer("1", new SameDiffSimpleLambdaLayer(), "0").addLayer("2", new OutputLayer.Builder().nIn(5).nOut(5).activation(SOFTMAX).lossFunction(MCXENT).build(), "1").setOutputs("2").build();
        // Equavalent, not using SameDiff Lambda:
        ComputationGraphConfiguration confStd = new NeuralNetConfiguration.Builder().seed(12345).updater(new Adam(0.01)).graphBuilder().addInputs("in").addLayer("0", new DenseLayer.Builder().nIn(5).nOut(5).activation(TANH).build(), "in").addVertex("1", new ShiftVertex(1.0), "0").addVertex("2", new ScaleVertex(2.0), "1").addLayer("3", new OutputLayer.Builder().nIn(5).nOut(5).activation(SOFTMAX).lossFunction(MCXENT).build(), "2").setOutputs("3").build();
        ComputationGraph lambda = new ComputationGraph(conf);
        lambda.init();
        ComputationGraph std = new ComputationGraph(confStd);
        std.init();
        lambda.setParams(std.params());
        INDArray in = Nd4j.rand(3, 5);
        INDArray labels = TestUtils.randomOneHot(3, 5);
        DataSet ds = new DataSet(in, labels);
        INDArray outLambda = lambda.outputSingle(in);
        INDArray outStd = std.outputSingle(in);
        Assert.assertEquals(outLambda, outStd);
        double scoreLambda = lambda.score(ds);
        double scoreStd = std.score(ds);
        Assert.assertEquals(scoreStd, scoreLambda, 1.0E-6);
        for (int i = 0; i < 3; i++) {
            lambda.fit(ds);
            std.fit(ds);
            String s = String.valueOf(i);
            Assert.assertEquals(s, std.params(), lambda.params());
            Assert.assertEquals(s, std.getFlattenedGradients(), lambda.getFlattenedGradients());
        }
        ComputationGraph loaded = TestUtils.testModelSerialization(lambda);
        outLambda = loaded.outputSingle(in);
        outStd = std.outputSingle(in);
        Assert.assertEquals(outStd, outLambda);
    }

    @Test
    public void testSameDiffLamdaVertexBasic() {
        Nd4j.getRandom().setSeed(12345);
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).updater(new Adam(0.01)).graphBuilder().addInputs("in1", "in2").addLayer("0", new DenseLayer.Builder().nIn(5).nOut(5).activation(TANH).build(), "in1").addLayer("1", new DenseLayer.Builder().nIn(5).nOut(5).activation(TANH).build(), "in2").addVertex("lambda", new SameDiffSimpleLambdaVertex(), "0", "1").addLayer("2", new OutputLayer.Builder().nIn(5).nOut(5).activation(SOFTMAX).lossFunction(MCXENT).build(), "lambda").setOutputs("2").build();
        // Equavalent, not using SameDiff Lambda:
        ComputationGraphConfiguration confStd = new NeuralNetConfiguration.Builder().seed(12345).updater(new Adam(0.01)).graphBuilder().addInputs("in1", "in2").addLayer("0", new DenseLayer.Builder().nIn(5).nOut(5).activation(TANH).build(), "in1").addLayer("1", new DenseLayer.Builder().nIn(5).nOut(5).activation(TANH).build(), "in2").addVertex("elementwise", new org.deeplearning4j.nn.conf.graph.ElementWiseVertex(Op.Product), "0", "1").addLayer("3", new OutputLayer.Builder().nIn(5).nOut(5).activation(SOFTMAX).lossFunction(MCXENT).build(), "elementwise").setOutputs("3").build();
        ComputationGraph lambda = new ComputationGraph(conf);
        lambda.init();
        ComputationGraph std = new ComputationGraph(confStd);
        std.init();
        lambda.setParams(std.params());
        INDArray in1 = Nd4j.rand(3, 5);
        INDArray in2 = Nd4j.rand(3, 5);
        INDArray labels = TestUtils.randomOneHot(3, 5);
        MultiDataSet mds = new org.nd4j.linalg.dataset.MultiDataSet(new INDArray[]{ in1, in2 }, new INDArray[]{ labels });
        INDArray outLambda = lambda.output(in1, in2)[0];
        INDArray outStd = std.output(in1, in2)[0];
        Assert.assertEquals(outLambda, outStd);
        double scoreLambda = lambda.score(mds);
        double scoreStd = std.score(mds);
        Assert.assertEquals(scoreStd, scoreLambda, 1.0E-6);
        for (int i = 0; i < 3; i++) {
            lambda.fit(mds);
            std.fit(mds);
            String s = String.valueOf(i);
            Assert.assertEquals(s, std.params(), lambda.params());
            Assert.assertEquals(s, std.getFlattenedGradients(), lambda.getFlattenedGradients());
        }
        ComputationGraph loaded = TestUtils.testModelSerialization(lambda);
        outLambda = loaded.output(in1, in2)[0];
        outStd = std.output(in1, in2)[0];
        Assert.assertEquals(outStd, outLambda);
    }
}

