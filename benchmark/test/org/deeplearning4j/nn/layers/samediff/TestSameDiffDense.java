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
import DefaultParamInitializer.BIAS_KEY;
import DefaultParamInitializer.WEIGHT_KEY;
import LossFunctions.LossFunction.MCXENT;
import WeightInit.XAVIER;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.TestUtils;
import org.deeplearning4j.datasets.iterator.impl.IrisDataSetIterator;
import org.deeplearning4j.gradientcheck.GradientCheckUtil;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.gradient.Gradient;
import org.deeplearning4j.nn.layers.samediff.testlayers.SameDiffDense;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.learning.config.NoOp;


@Slf4j
public class TestSameDiffDense extends BaseDL4JTest {
    private static final boolean PRINT_RESULTS = true;

    private static final boolean RETURN_ON_FIRST_FAILURE = false;

    private static final double DEFAULT_EPS = 1.0E-6;

    private static final double DEFAULT_MAX_REL_ERROR = 0.001;

    private static final double DEFAULT_MIN_ABS_ERROR = 1.0E-8;

    @Test
    public void testSameDiffDenseBasic() {
        int nIn = 3;
        int nOut = 4;
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().list().layer(new SameDiffDense.Builder().nIn(nIn).nOut(nOut).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        Map<String, INDArray> pt1 = net.getLayer(0).paramTable();
        Assert.assertNotNull(pt1);
        Assert.assertEquals(2, pt1.size());
        Assert.assertNotNull(pt1.get(WEIGHT_KEY));
        Assert.assertNotNull(pt1.get(BIAS_KEY));
        Assert.assertArrayEquals(new long[]{ nIn, nOut }, pt1.get(WEIGHT_KEY).shape());
        Assert.assertArrayEquals(new long[]{ 1, nOut }, pt1.get(BIAS_KEY).shape());
    }

    @Test
    public void testSameDiffDenseForward() {
        for (int minibatch : new int[]{ 5, 1 }) {
            int nIn = 3;
            int nOut = 4;
            Activation[] afns = new Activation[]{ Activation.TANH, Activation.SIGMOID, Activation.ELU, Activation.IDENTITY, Activation.SOFTPLUS, Activation.SOFTSIGN, Activation.CUBE, Activation.HARDTANH, Activation.RELU };
            for (Activation a : afns) {
                log.info(("Starting test - " + a));
                MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().list().layer(new SameDiffDense.Builder().nIn(nIn).nOut(nOut).activation(a).build()).build();
                MultiLayerNetwork net = new MultiLayerNetwork(conf);
                net.init();
                Assert.assertNotNull(net.paramTable());
                MultiLayerConfiguration conf2 = new NeuralNetConfiguration.Builder().list().layer(new DenseLayer.Builder().activation(a).nIn(nIn).nOut(nOut).build()).build();
                MultiLayerNetwork net2 = new MultiLayerNetwork(conf2);
                net2.init();
                net.params().assign(net2.params());
                // Check params:
                Assert.assertEquals(net2.params(), net.params());
                Map<String, INDArray> params1 = net.paramTable();
                Map<String, INDArray> params2 = net2.paramTable();
                Assert.assertEquals(params2, params1);
                INDArray in = Nd4j.rand(minibatch, nIn);
                INDArray out = net.output(in);
                INDArray outExp = net2.output(in);
                Assert.assertEquals(outExp, out);
                // Also check serialization:
                MultiLayerNetwork netLoaded = TestUtils.testModelSerialization(net);
                INDArray outLoaded = netLoaded.output(in);
                Assert.assertEquals(outExp, outLoaded);
            }
        }
    }

    @Test
    public void testSameDiffDenseForwardMultiLayer() {
        for (int minibatch : new int[]{ 5, 1 }) {
            int nIn = 3;
            int nOut = 4;
            Activation[] afns = new Activation[]{ Activation.TANH, Activation.SIGMOID, Activation.ELU, Activation.IDENTITY, Activation.SOFTPLUS, Activation.SOFTSIGN, Activation.CUBE// https://github.com/deeplearning4j/nd4j/issues/2426
            , Activation.HARDTANH, Activation.RELU// JVM crash
             };
            for (Activation a : afns) {
                log.info(("Starting test - " + a));
                MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).list().layer(new SameDiffDense.Builder().nIn(nIn).nOut(nOut).weightInit(XAVIER).activation(a).build()).layer(new SameDiffDense.Builder().nIn(nOut).nOut(nOut).weightInit(XAVIER).activation(a).build()).layer(new OutputLayer.Builder().nIn(nOut).nOut(nOut).weightInit(XAVIER).activation(a).build()).validateOutputLayerConfig(false).build();
                MultiLayerNetwork net = new MultiLayerNetwork(conf);
                net.init();
                Assert.assertNotNull(net.paramTable());
                MultiLayerConfiguration conf2 = new NeuralNetConfiguration.Builder().seed(12345).weightInit(XAVIER).list().layer(new DenseLayer.Builder().activation(a).nIn(nIn).nOut(nOut).build()).layer(new DenseLayer.Builder().activation(a).nIn(nOut).nOut(nOut).build()).layer(new OutputLayer.Builder().nIn(nOut).nOut(nOut).activation(a).build()).validateOutputLayerConfig(false).build();
                MultiLayerNetwork net2 = new MultiLayerNetwork(conf2);
                net2.init();
                // net.params().assign(net2.params());
                Assert.assertEquals(net2.params(), net.params());
                // Check params:
                Assert.assertEquals(net2.params(), net.params());
                Map<String, INDArray> params1 = net.paramTable();
                Map<String, INDArray> params2 = net2.paramTable();
                Assert.assertEquals(params2, params1);
                INDArray in = Nd4j.rand(minibatch, nIn);
                INDArray out = net.output(in);
                INDArray outExp = net2.output(in);
                Assert.assertEquals(outExp, out);
                // Also check serialization:
                MultiLayerNetwork netLoaded = TestUtils.testModelSerialization(net);
                INDArray outLoaded = netLoaded.output(in);
                Assert.assertEquals(outExp, outLoaded);
                // Sanity check different minibatch sizes
                in = Nd4j.rand((2 * minibatch), nIn);
                out = net.output(in);
                outExp = net2.output(in);
                Assert.assertEquals(outExp, out);
            }
        }
    }

    @Test
    public void testSameDiffDenseBackward() {
        int nIn = 3;
        int nOut = 4;
        for (boolean workspaces : new boolean[]{ false, true }) {
            for (int minibatch : new int[]{ 5, 1 }) {
                Activation[] afns = new Activation[]{ Activation.TANH, Activation.SIGMOID, Activation.ELU, Activation.IDENTITY, Activation.SOFTPLUS, Activation.SOFTSIGN, Activation.HARDTANH, Activation.CUBE// https://github.com/deeplearning4j/nd4j/issues/2426
                , Activation.RELU// JVM crash
                 };
                for (Activation a : afns) {
                    log.info(((((("Starting test - " + a) + " - minibatch ") + minibatch) + ", workspaces: ") + workspaces));
                    MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().trainingWorkspaceMode((workspaces ? WorkspaceMode.ENABLED : WorkspaceMode.NONE)).inferenceWorkspaceMode((workspaces ? WorkspaceMode.ENABLED : WorkspaceMode.NONE)).list().layer(new SameDiffDense.Builder().nIn(nIn).nOut(nOut).activation(a).build()).layer(new OutputLayer.Builder().nIn(nOut).nOut(nOut).activation(SOFTMAX).lossFunction(MCXENT).build()).build();
                    MultiLayerNetwork netSD = new MultiLayerNetwork(conf);
                    netSD.init();
                    MultiLayerConfiguration conf2 = new NeuralNetConfiguration.Builder().list().layer(new DenseLayer.Builder().activation(a).nIn(nIn).nOut(nOut).build()).layer(new OutputLayer.Builder().nIn(nOut).nOut(nOut).activation(SOFTMAX).lossFunction(MCXENT).build()).build();
                    MultiLayerNetwork netStandard = new MultiLayerNetwork(conf2);
                    netStandard.init();
                    netSD.params().assign(netStandard.params());
                    // Check params:
                    Assert.assertEquals(netStandard.params(), netSD.params());
                    Assert.assertEquals(netStandard.paramTable(), netSD.paramTable());
                    INDArray in = Nd4j.rand(minibatch, nIn);
                    INDArray l = TestUtils.randomOneHot(minibatch, nOut, 12345);
                    netSD.setInput(in);
                    netStandard.setInput(in);
                    netSD.setLabels(l);
                    netStandard.setLabels(l);
                    netSD.computeGradientAndScore();
                    netStandard.computeGradientAndScore();
                    Gradient gSD = netSD.gradient();
                    Gradient gStd = netStandard.gradient();
                    Map<String, INDArray> m1 = gSD.gradientForVariable();
                    Map<String, INDArray> m2 = gStd.gradientForVariable();
                    Assert.assertEquals(m2.keySet(), m1.keySet());
                    for (String s : m1.keySet()) {
                        INDArray i1 = m1.get(s);
                        INDArray i2 = m2.get(s);
                        Assert.assertEquals(s, i2, i1);
                    }
                    Assert.assertEquals(gStd.gradient(), gSD.gradient());
                    // Sanity check: different minibatch size
                    in = Nd4j.rand((2 * minibatch), nIn);
                    l = TestUtils.randomOneHot((2 * minibatch), nOut, 12345);
                    netSD.setInput(in);
                    netStandard.setInput(in);
                    netSD.setLabels(l);
                    netStandard.setLabels(l);
                    netSD.computeGradientAndScore();
                    // netStandard.computeGradientAndScore();
                    // assertEquals(netStandard.gradient().gradient(), netSD.gradient().gradient());
                }
            }
        }
    }

    @Test
    public void testSameDiffDenseTraining() {
        Nd4j.getRandom().setSeed(12345);
        int nIn = 4;
        int nOut = 3;
        boolean workspaces = true;
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).trainingWorkspaceMode((workspaces ? WorkspaceMode.ENABLED : WorkspaceMode.NONE)).inferenceWorkspaceMode((workspaces ? WorkspaceMode.ENABLED : WorkspaceMode.NONE)).updater(new Adam(0.1)).list().layer(new SameDiffDense.Builder().nIn(nIn).nOut(5).activation(TANH).build()).layer(new SameDiffDense.Builder().nIn(5).nOut(5).activation(TANH).build()).layer(new OutputLayer.Builder().nIn(5).nOut(nOut).activation(SOFTMAX).lossFunction(MCXENT).build()).build();
        MultiLayerNetwork netSD = new MultiLayerNetwork(conf);
        netSD.init();
        MultiLayerConfiguration conf2 = new NeuralNetConfiguration.Builder().seed(12345).updater(new Adam(0.1)).list().layer(new DenseLayer.Builder().activation(TANH).nIn(nIn).nOut(5).build()).layer(new DenseLayer.Builder().activation(TANH).nIn(5).nOut(5).build()).layer(new OutputLayer.Builder().nIn(5).nOut(nOut).activation(SOFTMAX).lossFunction(MCXENT).build()).build();
        MultiLayerNetwork netStandard = new MultiLayerNetwork(conf2);
        netStandard.init();
        netSD.params().assign(netStandard.params());
        // Check params:
        Assert.assertEquals(netStandard.params(), netSD.params());
        Assert.assertEquals(netStandard.paramTable(), netSD.paramTable());
        DataSetIterator iter = new IrisDataSetIterator(150, 150);
        DataSet ds = iter.next();
        INDArray outSD = netSD.output(ds.getFeatures());
        INDArray outStd = netStandard.output(ds.getFeatures());
        Assert.assertEquals(outStd, outSD);
        for (int i = 0; i < 3; i++) {
            netSD.fit(ds);
            netStandard.fit(ds);
            String s = String.valueOf(i);
            Assert.assertEquals(s, netStandard.getFlattenedGradients(), netSD.getFlattenedGradients());
            Assert.assertEquals(s, netStandard.params(), netSD.params());
            Assert.assertEquals(s, netStandard.getUpdater().getStateViewArray(), netSD.getUpdater().getStateViewArray());
        }
    }

    @Test
    public void gradientCheck() {
        int nIn = 4;
        int nOut = 4;
        for (boolean workspaces : new boolean[]{ false, true }) {
            for (Activation a : new Activation[]{ Activation.TANH, Activation.IDENTITY }) {
                String msg = (("workspaces: " + workspaces) + ", ") + a;
                Nd4j.getRandom().setSeed(12345);
                MultiLayerConfiguration conf = // .setInputType(InputType.feedForward(nIn))     //TODO
                new NeuralNetConfiguration.Builder().seed(12345).updater(new NoOp()).trainingWorkspaceMode((workspaces ? WorkspaceMode.ENABLED : WorkspaceMode.NONE)).inferenceWorkspaceMode((workspaces ? WorkspaceMode.ENABLED : WorkspaceMode.NONE)).list().layer(new SameDiffDense.Builder().nIn(nIn).nOut(nOut).activation(a).build()).layer(new SameDiffDense.Builder().nIn(nOut).nOut(nOut).activation(a).build()).layer(new OutputLayer.Builder().nIn(nOut).nOut(nOut).activation(SOFTMAX).lossFunction(MCXENT).build()).build();
                MultiLayerNetwork net = new MultiLayerNetwork(conf);
                net.init();
                INDArray f = Nd4j.rand(3, nIn);
                INDArray l = TestUtils.randomOneHot(3, nOut);
                log.info(("Starting: " + msg));
                boolean gradOK = GradientCheckUtil.checkGradients(net, TestSameDiffDense.DEFAULT_EPS, TestSameDiffDense.DEFAULT_MAX_REL_ERROR, TestSameDiffDense.DEFAULT_MIN_ABS_ERROR, TestSameDiffDense.PRINT_RESULTS, TestSameDiffDense.RETURN_ON_FIRST_FAILURE, f, l);
                Assert.assertTrue(msg, gradOK);
                TestUtils.testModelSerialization(net);
            }
        }
    }
}

