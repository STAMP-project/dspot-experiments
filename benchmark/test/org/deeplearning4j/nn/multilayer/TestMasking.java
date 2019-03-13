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
package org.deeplearning4j.nn.multilayer;


import Activation.IDENTITY;
import Activation.RELU;
import Activation.SIGMOID;
import Activation.TANH;
import ConvolutionMode.Same;
import DataType.DOUBLE;
import LossFunctions.LossFunction;
import LossFunctions.LossFunction.MEAN_ABSOLUTE_ERROR;
import LossFunctions.LossFunction.XENT;
import WeightInit.XAVIER;
import java.util.Collections;
import lombok.val;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.eval.EvaluationBinary;
import org.deeplearning4j.gradientcheck.LossFunctionGradientCheck;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.conf.distribution.NormalDistribution;
import org.deeplearning4j.nn.conf.graph.MergeVertex;
import org.deeplearning4j.nn.conf.graph.StackVertex;
import org.deeplearning4j.nn.conf.graph.UnstackVertex;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.preprocessor.CnnToRnnPreProcessor;
import org.deeplearning4j.nn.conf.preprocessor.RnnToCnnPreProcessor;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.buffer.util.DataTypeUtil;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.learning.config.NoOp;
import org.nd4j.linalg.lossfunctions.ILossFunction;

import static BackpropType.Standard;
import static BackpropType.TruncatedBPTT;


/**
 * Created by Alex on 20/01/2017.
 */
public class TestMasking extends BaseDL4JTest {
    static {
        DataTypeUtil.setDTypeForContext(DOUBLE);
    }

    @Test
    public void checkMaskArrayClearance() {
        for (boolean tbptt : new boolean[]{ true, false }) {
            // Simple "does it throw an exception" type test...
            MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).list().layer(0, new RnnOutputLayer.Builder(LossFunction.MSE).activation(IDENTITY).nIn(1).nOut(1).build()).backpropType((tbptt ? TruncatedBPTT : Standard)).tBPTTForwardLength(8).tBPTTBackwardLength(8).build();
            MultiLayerNetwork net = new MultiLayerNetwork(conf);
            net.init();
            DataSet data = new DataSet(Nd4j.linspace(1, 10, 10).reshape(1, 1, 10), Nd4j.linspace(2, 20, 10).reshape(1, 1, 10), Nd4j.ones(1, 10), Nd4j.ones(1, 10));
            net.fit(data);
            for (Layer l : net.getLayers()) {
                Assert.assertNull(l.getMaskArray());
            }
            net.fit(data.getFeatures(), data.getLabels(), data.getFeaturesMaskArray(), data.getLabelsMaskArray());
            for (Layer l : net.getLayers()) {
                Assert.assertNull(l.getMaskArray());
            }
            DataSetIterator iter = new org.deeplearning4j.datasets.iterator.ExistingDataSetIterator(Collections.singletonList(data).iterator());
            net.fit(iter);
            for (Layer l : net.getLayers()) {
                Assert.assertNull(l.getMaskArray());
            }
        }
    }

    @Test
    public void testPerOutputMaskingMLN() {
        // Idea: for per-output masking, the contents of the masked label entries should make zero difference to either
        // the score or the gradients
        int nIn = 6;
        int layerSize = 4;
        INDArray mask1 = Nd4j.create(new double[]{ 1, 0, 0, 1, 0 }, new long[]{ 1, 5 });
        INDArray mask3 = Nd4j.create(new double[][]{ new double[]{ 1, 1, 1, 1, 1 }, new double[]{ 0, 1, 0, 1, 0 }, new double[]{ 1, 0, 0, 1, 1 } });
        INDArray[] labelMasks = new INDArray[]{ mask1, mask3 };
        ILossFunction[] lossFunctions = new ILossFunction[]{ new LossBinaryXENT(), // new LossCosineProximity(),    //Doesn't support per-output masking, as it doesn't make sense for cosine proximity
        new LossHinge(), new LossKLD(), new LossKLD(), new LossL1(), new LossL2(), new LossMAE(), new LossMAE(), new LossMAPE(), new LossMAPE(), // new LossMCXENT(),             //Per output masking on MCXENT+Softmax: not yet supported
        new LossMCXENT(), new LossMSE(), new LossMSE(), new LossMSLE(), new LossMSLE(), new LossNegativeLogLikelihood(), new LossPoisson(), new LossSquaredHinge() };
        Activation[] act = new Activation[]{ Activation.SIGMOID// XENT
        , // Activation.TANH,
        Activation.TANH// Hinge
        , Activation.SIGMOID// KLD
        , Activation.SOFTMAX// KLD + softmax
        , Activation.TANH// L1
        , Activation.TANH// L2
        , Activation.TANH// MAE
        , Activation.SOFTMAX// MAE + softmax
        , Activation.TANH// MAPE
        , Activation.SOFTMAX// MAPE + softmax
        , // Activation.SOFTMAX, //MCXENT + softmax: see comment above
        Activation.SIGMOID// MCXENT + sigmoid
        , Activation.TANH// MSE
        , Activation.SOFTMAX// MSE + softmax
        , Activation.SIGMOID// MSLE - needs positive labels/activations (due to log)
        , Activation.SOFTMAX// MSLE + softmax
        , Activation.SIGMOID// NLL
        , Activation.SIGMOID// Poisson
        , Activation.TANH// Squared hinge
         };
        for (INDArray labelMask : labelMasks) {
            val minibatch = labelMask.size(0);
            val nOut = labelMask.size(1);
            for (int i = 0; i < (lossFunctions.length); i++) {
                ILossFunction lf = lossFunctions[i];
                Activation a = act[i];
                MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().updater(new NoOp()).dist(new NormalDistribution(0, 1)).seed(12345).list().layer(0, new DenseLayer.Builder().nIn(nIn).nOut(layerSize).activation(TANH).build()).layer(1, new OutputLayer.Builder().nIn(layerSize).nOut(nOut).lossFunction(lf).activation(a).build()).validateOutputLayerConfig(false).build();
                MultiLayerNetwork net = new MultiLayerNetwork(conf);
                net.init();
                net.setLayerMaskArrays(null, labelMask);
                INDArray[] fl = LossFunctionGradientCheck.getFeaturesAndLabels(lf, minibatch, nIn, nOut, 12345);
                INDArray features = fl[0];
                INDArray labels = fl[1];
                net.setInput(features);
                net.setLabels(labels);
                net.computeGradientAndScore();
                double score1 = net.score();
                INDArray grad1 = net.gradient().gradient();
                // Now: change the label values for the masked steps. The
                INDArray maskZeroLocations = labelMask.rsub(1.0);// rsub(1): swap 0s and 1s

                INDArray rand = Nd4j.rand(maskZeroLocations.shape()).muli(0.5);
                INDArray newLabels = labels.add(rand.muli(maskZeroLocations));// Only the masked values are changed

                net.setLabels(newLabels);
                net.computeGradientAndScore();
                Assert.assertNotEquals(labels, newLabels);
                double score2 = net.score();
                INDArray grad2 = net.gradient().gradient();
                Assert.assertEquals(score1, score2, 1.0E-6);
                Assert.assertEquals(grad1, grad2);
                // Do the same for CompGraph
                ComputationGraphConfiguration conf2 = new NeuralNetConfiguration.Builder().updater(new NoOp()).dist(new NormalDistribution(0, 1)).seed(12345).graphBuilder().addInputs("in").addLayer("0", new DenseLayer.Builder().nIn(nIn).nOut(layerSize).activation(TANH).build(), "in").addLayer("1", new OutputLayer.Builder().nIn(layerSize).nOut(nOut).lossFunction(lf).activation(a).build(), "0").setOutputs("1").validateOutputLayerConfig(false).build();
                ComputationGraph graph = new ComputationGraph(conf2);
                graph.init();
                graph.setLayerMaskArrays(null, new INDArray[]{ labelMask });
                graph.setInputs(features);
                graph.setLabels(labels);
                graph.computeGradientAndScore();
                double gScore1 = graph.score();
                INDArray gGrad1 = graph.gradient().gradient();
                graph.setLayerMaskArrays(null, new INDArray[]{ labelMask });
                graph.setInputs(features);
                graph.setLabels(newLabels);
                graph.computeGradientAndScore();
                double gScore2 = graph.score();
                INDArray gGrad2 = graph.gradient().gradient();
                Assert.assertEquals(gScore1, gScore2, 1.0E-6);
                Assert.assertEquals(gGrad1, gGrad2);
            }
        }
    }

    @Test
    public void testCompGraphEvalWithMask() {
        int minibatch = 3;
        int layerSize = 6;
        int nIn = 5;
        int nOut = 4;
        ComputationGraphConfiguration conf2 = new NeuralNetConfiguration.Builder().updater(new NoOp()).dist(new NormalDistribution(0, 1)).seed(12345).graphBuilder().addInputs("in").addLayer("0", new DenseLayer.Builder().nIn(nIn).nOut(layerSize).activation(TANH).build(), "in").addLayer("1", new OutputLayer.Builder().nIn(layerSize).nOut(nOut).lossFunction(XENT).activation(SIGMOID).build(), "0").setOutputs("1").build();
        ComputationGraph graph = new ComputationGraph(conf2);
        graph.init();
        INDArray f = Nd4j.create(minibatch, nIn);
        INDArray l = Nd4j.create(minibatch, nOut);
        INDArray lMask = Nd4j.ones(minibatch, nOut);
        DataSet ds = new DataSet(f, l, null, lMask);
        DataSetIterator iter = new org.deeplearning4j.datasets.iterator.ExistingDataSetIterator(Collections.singletonList(ds).iterator());
        EvaluationBinary eb = new EvaluationBinary();
        graph.doEvaluation(iter, eb);
    }

    @Test
    public void testRnnCnnMaskingSimple() {
        int kernelSize1 = 2;
        int padding = 0;
        int cnnStride1 = 1;
        int channels = 1;
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).weightInit(XAVIER).convolutionMode(Same).graphBuilder().addInputs("inputs").addLayer("cnn1", new ConvolutionLayer.Builder(new int[]{ kernelSize1, kernelSize1 }, new int[]{ cnnStride1, cnnStride1 }, new int[]{ padding, padding }).nIn(channels).nOut(2).build(), "inputs").addLayer("lstm1", new LSTM.Builder().nIn(((7 * 7) * 2)).nOut(2).build(), "cnn1").addLayer("output", new RnnOutputLayer.Builder(LossFunction.MSE).activation(RELU).nIn(2).nOut(2).build(), "lstm1").setOutputs("output").setInputTypes(InputType.recurrent((7 * 7), 1)).inputPreProcessor("cnn1", new RnnToCnnPreProcessor(7, 7, channels)).inputPreProcessor("lstm1", new CnnToRnnPreProcessor(7, 7, 2)).build();
        ComputationGraph cg = new ComputationGraph(conf);
        cg.init();
        cg.fit(new DataSet(Nd4j.create(1, (7 * 7), 5), Nd4j.create(1, 2, 5), Nd4j.ones(1, 5), Nd4j.ones(1, 5)));
    }

    @Test
    public void testMaskingStackUnstack() {
        ComputationGraphConfiguration nnConfig = new NeuralNetConfiguration.Builder().updater(new Adam(0.02)).graphBuilder().setInputTypes(InputType.recurrent(3), InputType.recurrent(3)).addInputs("m1", "m2").addVertex("stack", new StackVertex(), "m1", "m2").addLayer("lastUnStacked", new org.deeplearning4j.nn.conf.layers.recurrent.LastTimeStep(new LSTM.Builder().nIn(3).nOut(1).activation(TANH).build()), "stack").addVertex("unstacked1", new UnstackVertex(0, 2), "lastUnStacked").addVertex("unstacked2", new UnstackVertex(1, 2), "lastUnStacked").addVertex("restacked", new StackVertex(), "unstacked1", "unstacked2").addVertex("un1", new UnstackVertex(0, 2), "restacked").addVertex("un2", new UnstackVertex(1, 2), "restacked").addVertex("q", new MergeVertex(), "un1", "un2").addLayer("probability", new OutputLayer.Builder().nIn(2).nOut(6).lossFunction(MEAN_ABSOLUTE_ERROR).build(), "q").setOutputs("probability").build();
        ComputationGraph cg = new ComputationGraph(nnConfig);
        cg.init();
        INDArray i1 = Nd4j.create(1, 3, 5);
        INDArray i2 = Nd4j.create(1, 3, 5);
        INDArray fm1 = Nd4j.ones(1, 5);
        INDArray fm2 = Nd4j.ones(1, 5);
        // First: check no masks case
        INDArray o1 = cg.output(false, new INDArray[]{ i1, i2 }, null)[0];
        // Second: check null mask arrays case
        INDArray o2 = cg.output(false, new INDArray[]{ i1, i2 }, new INDArray[]{ null, null })[0];
        // Third: masks present case
        INDArray o3 = cg.output(false, new INDArray[]{ i1, i2 }, new INDArray[]{ fm1, fm2 })[0];
        Assert.assertEquals(o1, o2);
        Assert.assertEquals(o1, o3);
    }
}

