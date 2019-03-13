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
package org.deeplearning4j.nn.graph;


import Activation.SOFTMAX;
import Activation.TANH;
import BackpropType.Standard;
import BackpropType.TruncatedBPTT;
import GravesLSTM.STATE_KEY_PREV_ACTIVATION;
import OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT;
import WorkspaceMode.NONE;
import java.util.Collections;
import java.util.Map;
import lombok.val;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.distribution.NormalDistribution;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.GlobalPoolingLayer;
import org.deeplearning4j.nn.conf.layers.GravesLSTM.Builder;
import org.deeplearning4j.nn.conf.preprocessor.FeedForwardToRnnPreProcessor;
import org.deeplearning4j.nn.conf.preprocessor.RnnToFeedForwardPreProcessor;
import org.deeplearning4j.nn.gradient.Gradient;
import org.deeplearning4j.nn.layers.recurrent.BaseRecurrentLayer;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.MultiDataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.iterator.MultiDataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.primitives.Pair;


public class ComputationGraphTestRNN extends BaseDL4JTest {
    @Test
    public void testRnnTimeStepGravesLSTM() {
        Nd4j.getRandom().setSeed(12345);
        int timeSeriesLength = 12;
        // 4 layer network: 2 GravesLSTM + DenseLayer + RnnOutputLayer. Hence also tests preprocessors.
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).graphBuilder().addInputs("in").addLayer("0", new Builder().nIn(5).nOut(7).activation(TANH).dist(new NormalDistribution(0, 0.5)).build(), "in").addLayer("1", new Builder().nIn(7).nOut(8).activation(TANH).dist(new NormalDistribution(0, 0.5)).build(), "0").addLayer("2", new DenseLayer.Builder().nIn(8).nOut(9).activation(TANH).dist(new NormalDistribution(0, 0.5)).build(), "1").addLayer("3", nIn(9).nOut(4).activation(SOFTMAX).dist(new NormalDistribution(0, 0.5)).build(), "2").setOutputs("3").inputPreProcessor("2", new RnnToFeedForwardPreProcessor()).inputPreProcessor("3", new FeedForwardToRnnPreProcessor()).build();
        ComputationGraph graph = new ComputationGraph(conf);
        graph.init();
        INDArray input = Nd4j.rand(new int[]{ 3, 5, timeSeriesLength });
        Map<String, INDArray> allOutputActivations = graph.feedForward(input, true);
        INDArray fullOutL0 = allOutputActivations.get("0");
        INDArray fullOutL1 = allOutputActivations.get("1");
        INDArray fullOutL3 = allOutputActivations.get("3");
        Assert.assertArrayEquals(new long[]{ 3, 7, timeSeriesLength }, fullOutL0.shape());
        Assert.assertArrayEquals(new long[]{ 3, 8, timeSeriesLength }, fullOutL1.shape());
        Assert.assertArrayEquals(new long[]{ 3, 4, timeSeriesLength }, fullOutL3.shape());
        int[] inputLengths = new int[]{ 1, 2, 3, 4, 6, 12 };
        // Do steps of length 1, then of length 2, ..., 12
        // Should get the same result regardless of step size; should be identical to standard forward pass
        for (int i = 0; i < (inputLengths.length); i++) {
            int inLength = inputLengths[i];
            int nSteps = timeSeriesLength / inLength;// each of length inLength

            graph.rnnClearPreviousState();
            for (int j = 0; j < nSteps; j++) {
                int startTimeRange = j * inLength;
                int endTimeRange = startTimeRange + inLength;
                INDArray inputSubset = input.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(startTimeRange, endTimeRange));
                if (inLength > 1)
                    Assert.assertTrue(((inputSubset.size(2)) == inLength));

                INDArray[] outArr = graph.rnnTimeStep(inputSubset);
                Assert.assertEquals(1, outArr.length);
                INDArray out = outArr[0];
                INDArray expOutSubset;
                if (inLength == 1) {
                    val sizes = new long[]{ fullOutL3.size(0), fullOutL3.size(1), 1 };
                    expOutSubset = Nd4j.create(sizes);
                    expOutSubset.tensorAlongDimension(0, 1, 0).assign(fullOutL3.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(startTimeRange)));
                } else {
                    expOutSubset = fullOutL3.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(startTimeRange, endTimeRange));
                }
                Assert.assertEquals(expOutSubset, out);
                Map<String, INDArray> currL0State = graph.rnnGetPreviousState("0");
                Map<String, INDArray> currL1State = graph.rnnGetPreviousState("1");
                INDArray lastActL0 = currL0State.get(STATE_KEY_PREV_ACTIVATION);
                INDArray lastActL1 = currL1State.get(STATE_KEY_PREV_ACTIVATION);
                INDArray expLastActL0 = fullOutL0.tensorAlongDimension((endTimeRange - 1), 1, 0);
                INDArray expLastActL1 = fullOutL1.tensorAlongDimension((endTimeRange - 1), 1, 0);
                Assert.assertEquals(expLastActL0, lastActL0);
                Assert.assertEquals(expLastActL1, lastActL1);
            }
        }
    }

    @Test
    public void testRnnTimeStep2dInput() {
        Nd4j.getRandom().setSeed(12345);
        int timeSeriesLength = 6;
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().graphBuilder().addInputs("in").addLayer("0", new Builder().nIn(5).nOut(7).activation(TANH).dist(new NormalDistribution(0, 0.5)).build(), "in").addLayer("1", new Builder().nIn(7).nOut(8).activation(TANH).dist(new NormalDistribution(0, 0.5)).build(), "0").addLayer("2", nIn(8).nOut(4).activation(SOFTMAX).dist(new NormalDistribution(0, 0.5)).build(), "1").setOutputs("2").build();
        ComputationGraph graph = new ComputationGraph(conf);
        graph.init();
        INDArray input3d = Nd4j.rand(new int[]{ 3, 5, timeSeriesLength });
        INDArray out3d = graph.rnnTimeStep(input3d)[0];
        Assert.assertArrayEquals(out3d.shape(), new long[]{ 3, 4, timeSeriesLength });
        graph.rnnClearPreviousState();
        for (int i = 0; i < timeSeriesLength; i++) {
            INDArray input2d = input3d.tensorAlongDimension(i, 1, 0);
            INDArray out2d = graph.rnnTimeStep(input2d)[0];
            Assert.assertArrayEquals(out2d.shape(), new long[]{ 3, 4 });
            INDArray expOut2d = out3d.tensorAlongDimension(i, 1, 0);
            Assert.assertEquals(out2d, expOut2d);
        }
        // Check same but for input of size [3,5,1]. Expect [3,4,1] out
        graph.rnnClearPreviousState();
        for (int i = 0; i < timeSeriesLength; i++) {
            INDArray temp = Nd4j.create(new int[]{ 3, 5, 1 });
            temp.tensorAlongDimension(0, 1, 0).assign(input3d.tensorAlongDimension(i, 1, 0));
            INDArray out3dSlice = graph.rnnTimeStep(temp)[0];
            Assert.assertArrayEquals(out3dSlice.shape(), new long[]{ 3, 4, 1 });
            Assert.assertTrue(out3dSlice.tensorAlongDimension(0, 1, 0).equals(out3d.tensorAlongDimension(i, 1, 0)));
        }
    }

    @Test
    public void testRnnTimeStepMultipleInOut() {
        // Test rnnTimeStep functionality with multiple inputs and outputs...
        Nd4j.getRandom().setSeed(12345);
        int timeSeriesLength = 12;
        // 4 layer network: 2 GravesLSTM + DenseLayer + RnnOutputLayer. Hence also tests preprocessors.
        // Network architecture: lstm0 -> Dense -> RnnOutputLayer0
        // and lstm1 -> Dense -> RnnOutputLayer1
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).graphBuilder().addInputs("in0", "in1").addLayer("lstm0", new Builder().nIn(5).nOut(6).activation(TANH).dist(new NormalDistribution(0, 0.5)).build(), "in0").addLayer("lstm1", new Builder().nIn(4).nOut(5).activation(TANH).dist(new NormalDistribution(0, 0.5)).build(), "in1").addLayer("dense", new DenseLayer.Builder().nIn((6 + 5)).nOut(9).activation(TANH).dist(new NormalDistribution(0, 0.5)).build(), "lstm0", "lstm1").addLayer("out0", nIn(9).nOut(3).activation(SOFTMAX).dist(new NormalDistribution(0, 0.5)).build(), "dense").addLayer("out1", nIn(9).nOut(4).activation(SOFTMAX).dist(new NormalDistribution(0, 0.5)).build(), "dense").setOutputs("out0", "out1").inputPreProcessor("dense", new RnnToFeedForwardPreProcessor()).inputPreProcessor("out0", new FeedForwardToRnnPreProcessor()).inputPreProcessor("out1", new FeedForwardToRnnPreProcessor()).build();
        ComputationGraph graph = new ComputationGraph(conf);
        graph.init();
        INDArray input0 = Nd4j.rand(new int[]{ 3, 5, timeSeriesLength });
        INDArray input1 = Nd4j.rand(new int[]{ 3, 4, timeSeriesLength });
        Map<String, INDArray> allOutputActivations = graph.feedForward(new INDArray[]{ input0, input1 }, true);
        INDArray fullActLSTM0 = allOutputActivations.get("lstm0");
        INDArray fullActLSTM1 = allOutputActivations.get("lstm1");
        INDArray fullActOut0 = allOutputActivations.get("out0");
        INDArray fullActOut1 = allOutputActivations.get("out1");
        Assert.assertArrayEquals(new long[]{ 3, 6, timeSeriesLength }, fullActLSTM0.shape());
        Assert.assertArrayEquals(new long[]{ 3, 5, timeSeriesLength }, fullActLSTM1.shape());
        Assert.assertArrayEquals(new long[]{ 3, 3, timeSeriesLength }, fullActOut0.shape());
        Assert.assertArrayEquals(new long[]{ 3, 4, timeSeriesLength }, fullActOut1.shape());
        int[] inputLengths = new int[]{ 1, 2, 3, 4, 6, 12 };
        // Do steps of length 1, then of length 2, ..., 12
        // Should get the same result regardless of step size; should be identical to standard forward pass
        for (int i = 0; i < (inputLengths.length); i++) {
            int inLength = inputLengths[i];
            int nSteps = timeSeriesLength / inLength;// each of length inLength

            graph.rnnClearPreviousState();
            for (int j = 0; j < nSteps; j++) {
                int startTimeRange = j * inLength;
                int endTimeRange = startTimeRange + inLength;
                INDArray inputSubset0 = input0.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(startTimeRange, endTimeRange));
                if (inLength > 1)
                    Assert.assertTrue(((inputSubset0.size(2)) == inLength));

                INDArray inputSubset1 = input1.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(startTimeRange, endTimeRange));
                if (inLength > 1)
                    Assert.assertTrue(((inputSubset1.size(2)) == inLength));

                INDArray[] outArr = graph.rnnTimeStep(inputSubset0, inputSubset1);
                Assert.assertEquals(2, outArr.length);
                INDArray out0 = outArr[0];
                INDArray out1 = outArr[1];
                INDArray expOutSubset0;
                if (inLength == 1) {
                    val sizes = new long[]{ fullActOut0.size(0), fullActOut0.size(1), 1 };
                    expOutSubset0 = Nd4j.create(sizes);
                    expOutSubset0.tensorAlongDimension(0, 1, 0).assign(fullActOut0.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(startTimeRange)));
                } else {
                    expOutSubset0 = fullActOut0.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(startTimeRange, endTimeRange));
                }
                INDArray expOutSubset1;
                if (inLength == 1) {
                    val sizes = new long[]{ fullActOut1.size(0), fullActOut1.size(1), 1 };
                    expOutSubset1 = Nd4j.create(sizes);
                    expOutSubset1.tensorAlongDimension(0, 1, 0).assign(fullActOut1.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(startTimeRange)));
                } else {
                    expOutSubset1 = fullActOut1.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(startTimeRange, endTimeRange));
                }
                Assert.assertEquals(expOutSubset0, out0);
                Assert.assertEquals(expOutSubset1, out1);
                Map<String, INDArray> currLSTM0State = graph.rnnGetPreviousState("lstm0");
                Map<String, INDArray> currLSTM1State = graph.rnnGetPreviousState("lstm1");
                INDArray lastActL0 = currLSTM0State.get(STATE_KEY_PREV_ACTIVATION);
                INDArray lastActL1 = currLSTM1State.get(STATE_KEY_PREV_ACTIVATION);
                INDArray expLastActL0 = fullActLSTM0.tensorAlongDimension((endTimeRange - 1), 1, 0);
                INDArray expLastActL1 = fullActLSTM1.tensorAlongDimension((endTimeRange - 1), 1, 0);
                Assert.assertEquals(expLastActL0, lastActL0);
                Assert.assertEquals(expLastActL1, lastActL1);
            }
        }
    }

    @Test
    public void testTruncatedBPTTVsBPTT() {
        // Under some (limited) circumstances, we expect BPTT and truncated BPTT to be identical
        // Specifically TBPTT over entire data vector
        int timeSeriesLength = 12;
        int miniBatchSize = 7;
        int nIn = 5;
        int nOut = 4;
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).trainingWorkspaceMode(NONE).inferenceWorkspaceMode(NONE).graphBuilder().addInputs("in").addLayer("0", new Builder().nIn(nIn).nOut(7).activation(TANH).dist(new NormalDistribution(0, 0.5)).build(), "in").addLayer("1", new Builder().nIn(7).nOut(8).activation(TANH).dist(new NormalDistribution(0, 0.5)).build(), "0").addLayer("out", nIn(8).nOut(nOut).activation(SOFTMAX).dist(new NormalDistribution(0, 0.5)).build(), "1").setOutputs("out").build();
        Assert.assertEquals(Standard, conf.getBackpropType());
        ComputationGraphConfiguration confTBPTT = new NeuralNetConfiguration.Builder().seed(12345).trainingWorkspaceMode(NONE).inferenceWorkspaceMode(NONE).graphBuilder().addInputs("in").addLayer("0", new Builder().nIn(nIn).nOut(7).activation(TANH).dist(new NormalDistribution(0, 0.5)).build(), "in").addLayer("1", new Builder().nIn(7).nOut(8).activation(TANH).dist(new NormalDistribution(0, 0.5)).build(), "0").addLayer("out", nIn(8).nOut(nOut).activation(SOFTMAX).dist(new NormalDistribution(0, 0.5)).build(), "1").setOutputs("out").backpropType(TruncatedBPTT).tBPTTForwardLength(timeSeriesLength).tBPTTBackwardLength(timeSeriesLength).build();
        Assert.assertEquals(TruncatedBPTT, confTBPTT.getBackpropType());
        Nd4j.getRandom().setSeed(12345);
        ComputationGraph graph = new ComputationGraph(conf);
        graph.init();
        Nd4j.getRandom().setSeed(12345);
        ComputationGraph graphTBPTT = new ComputationGraph(confTBPTT);
        graphTBPTT.init();
        graphTBPTT.clearTbpttState = false;
        Assert.assertEquals(TruncatedBPTT, graphTBPTT.getConfiguration().getBackpropType());
        Assert.assertEquals(timeSeriesLength, graphTBPTT.getConfiguration().getTbpttFwdLength());
        Assert.assertEquals(timeSeriesLength, graphTBPTT.getConfiguration().getTbpttBackLength());
        INDArray inputData = Nd4j.rand(new int[]{ miniBatchSize, nIn, timeSeriesLength });
        INDArray labels = Nd4j.rand(new int[]{ miniBatchSize, nOut, timeSeriesLength });
        graph.setInput(0, inputData);
        graph.setLabel(0, labels);
        graphTBPTT.setInput(0, inputData);
        graphTBPTT.setLabel(0, labels);
        graph.computeGradientAndScore();
        graphTBPTT.computeGradientAndScore();
        Pair<Gradient, Double> graphPair = graph.gradientAndScore();
        Pair<Gradient, Double> graphTbpttPair = graphTBPTT.gradientAndScore();
        Assert.assertEquals(graphPair.getFirst().gradientForVariable(), graphTbpttPair.getFirst().gradientForVariable());
        Assert.assertEquals(graphPair.getSecond(), graphTbpttPair.getSecond(), 1.0E-8);
        // Check states: expect stateMap to be empty but tBpttStateMap to not be
        Map<String, INDArray> l0StateMLN = graph.rnnGetPreviousState(0);
        Map<String, INDArray> l0StateTBPTT = graphTBPTT.rnnGetPreviousState(0);
        Map<String, INDArray> l1StateMLN = graph.rnnGetPreviousState(0);
        Map<String, INDArray> l1StateTBPTT = graphTBPTT.rnnGetPreviousState(0);
        Map<String, INDArray> l0TBPTTState = ((BaseRecurrentLayer<?>) (graph.getLayer(0))).rnnGetTBPTTState();
        Map<String, INDArray> l0TBPTTStateTBPTT = ((BaseRecurrentLayer<?>) (graphTBPTT.getLayer(0))).rnnGetTBPTTState();
        Map<String, INDArray> l1TBPTTState = ((BaseRecurrentLayer<?>) (graph.getLayer(1))).rnnGetTBPTTState();
        Map<String, INDArray> l1TBPTTStateTBPTT = ((BaseRecurrentLayer<?>) (graphTBPTT.getLayer(1))).rnnGetTBPTTState();
        Assert.assertTrue(l0StateMLN.isEmpty());
        Assert.assertTrue(l0StateTBPTT.isEmpty());
        Assert.assertTrue(l1StateMLN.isEmpty());
        Assert.assertTrue(l1StateTBPTT.isEmpty());
        Assert.assertTrue(l0TBPTTState.isEmpty());
        Assert.assertEquals(2, l0TBPTTStateTBPTT.size());
        Assert.assertTrue(l1TBPTTState.isEmpty());
        Assert.assertEquals(2, l1TBPTTStateTBPTT.size());
        INDArray tbpttActL0 = l0TBPTTStateTBPTT.get(STATE_KEY_PREV_ACTIVATION);
        INDArray tbpttActL1 = l1TBPTTStateTBPTT.get(STATE_KEY_PREV_ACTIVATION);
        Map<String, INDArray> activations = graph.feedForward(inputData, true);
        INDArray l0Act = activations.get("0");
        INDArray l1Act = activations.get("1");
        INDArray expL0Act = l0Act.tensorAlongDimension((timeSeriesLength - 1), 1, 0);
        INDArray expL1Act = l1Act.tensorAlongDimension((timeSeriesLength - 1), 1, 0);
        Assert.assertEquals(tbpttActL0, expL0Act);
        Assert.assertEquals(tbpttActL1, expL1Act);
    }

    @Test
    public void testTruncatedBPTTSimple() {
        // Extremely simple test of the 'does it throw an exception' variety
        int timeSeriesLength = 12;
        int miniBatchSize = 7;
        int nIn = 5;
        int nOut = 4;
        int nTimeSlices = 20;
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).graphBuilder().addInputs("in").addLayer("0", new Builder().nIn(nIn).nOut(7).activation(TANH).dist(new NormalDistribution(0, 0.5)).build(), "in").addLayer("1", new Builder().nIn(7).nOut(8).activation(TANH).dist(new NormalDistribution(0, 0.5)).build(), "0").addLayer("out", nIn(8).nOut(nOut).activation(SOFTMAX).dist(new NormalDistribution(0, 0.5)).build(), "1").setOutputs("out").backpropType(TruncatedBPTT).tBPTTBackwardLength(timeSeriesLength).tBPTTForwardLength(timeSeriesLength).build();
        Nd4j.getRandom().setSeed(12345);
        ComputationGraph graph = new ComputationGraph(conf);
        graph.init();
        INDArray inputLong = Nd4j.rand(new int[]{ miniBatchSize, nIn, nTimeSlices * timeSeriesLength });
        INDArray labelsLong = Nd4j.rand(new int[]{ miniBatchSize, nOut, nTimeSlices * timeSeriesLength });
        graph.fit(new INDArray[]{ inputLong }, new INDArray[]{ labelsLong });
    }

    @Test
    public void testTBPTTLongerThanTS() {
        int tbpttLength = 100;
        int timeSeriesLength = 20;
        int miniBatchSize = 7;
        int nIn = 5;
        int nOut = 4;
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).graphBuilder().addInputs("in").addLayer("0", new Builder().nIn(nIn).nOut(7).activation(TANH).dist(new NormalDistribution(0, 0.5)).build(), "in").addLayer("1", new Builder().nIn(7).nOut(8).activation(TANH).dist(new NormalDistribution(0, 0.5)).build(), "0").addLayer("out", nIn(8).nOut(nOut).activation(SOFTMAX).dist(new NormalDistribution(0, 0.5)).build(), "1").setOutputs("out").backpropType(TruncatedBPTT).tBPTTBackwardLength(tbpttLength).tBPTTForwardLength(tbpttLength).build();
        Nd4j.getRandom().setSeed(12345);
        ComputationGraph graph = new ComputationGraph(conf);
        graph.init();
        INDArray inputLong = Nd4j.rand(new int[]{ miniBatchSize, nIn, timeSeriesLength });
        INDArray labelsLong = Nd4j.rand(new int[]{ miniBatchSize, nOut, timeSeriesLength });
        INDArray initialParams = graph.params().dup();
        graph.fit(new INDArray[]{ inputLong }, new INDArray[]{ labelsLong });
        INDArray afterParams = graph.params();
        Assert.assertNotEquals(initialParams, afterParams);
    }

    @Test
    public void testTbpttMasking() {
        // Simple "does it throw an exception" type test...
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).graphBuilder().addInputs("in").addLayer("out", nIn(1).nOut(1).build(), "in").setOutputs("out").backpropType(TruncatedBPTT).tBPTTForwardLength(8).tBPTTBackwardLength(8).build();
        ComputationGraph net = new ComputationGraph(conf);
        net.init();
        MultiDataSet data = new MultiDataSet(new INDArray[]{ Nd4j.linspace(1, 10, 10, Nd4j.dataType()).reshape(1, 1, 10) }, new INDArray[]{ Nd4j.linspace(2, 20, 10, Nd4j.dataType()).reshape(1, 1, 10) }, null, new INDArray[]{ Nd4j.ones(1, 10) });
        net.fit(data);
    }

    @Test
    public void checkMaskArrayClearance() {
        for (boolean tbptt : new boolean[]{ true, false }) {
            // Simple "does it throw an exception" type test...
            ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).graphBuilder().addInputs("in").addLayer("out", nIn(1).nOut(1).build(), "in").setOutputs("out").backpropType((tbptt ? BackpropType.TruncatedBPTT : BackpropType.Standard)).tBPTTForwardLength(8).tBPTTBackwardLength(8).build();
            ComputationGraph net = new ComputationGraph(conf);
            net.init();
            MultiDataSet data = new MultiDataSet(new INDArray[]{ Nd4j.linspace(1, 10, 10, Nd4j.dataType()).reshape(1, 1, 10) }, new INDArray[]{ Nd4j.linspace(2, 20, 10, Nd4j.dataType()).reshape(1, 1, 10) }, new INDArray[]{ Nd4j.ones(1, 10) }, new INDArray[]{ Nd4j.ones(1, 10) });
            net.fit(data);
            Assert.assertNull(net.getInputMaskArrays());
            Assert.assertNull(net.getLabelMaskArrays());
            for (Layer l : net.getLayers()) {
                Assert.assertNull(l.getMaskArray());
            }
            DataSet ds = new DataSet(data.getFeatures(0), data.getLabels(0), data.getFeaturesMaskArray(0), data.getLabelsMaskArray(0));
            net.fit(ds);
            Assert.assertNull(net.getInputMaskArrays());
            Assert.assertNull(net.getLabelMaskArrays());
            for (Layer l : net.getLayers()) {
                Assert.assertNull(l.getMaskArray());
            }
            net.fit(data.getFeatures(), data.getLabels(), data.getFeaturesMaskArrays(), data.getLabelsMaskArrays());
            Assert.assertNull(net.getInputMaskArrays());
            Assert.assertNull(net.getLabelMaskArrays());
            for (Layer l : net.getLayers()) {
                Assert.assertNull(l.getMaskArray());
            }
            MultiDataSetIterator iter = new org.deeplearning4j.datasets.iterator.IteratorMultiDataSetIterator(Collections.singletonList(((org.nd4j.linalg.dataset.api.MultiDataSet) (data))).iterator(), 1);
            net.fit(iter);
            Assert.assertNull(net.getInputMaskArrays());
            Assert.assertNull(net.getLabelMaskArrays());
            for (Layer l : net.getLayers()) {
                Assert.assertNull(l.getMaskArray());
            }
            DataSetIterator iter2 = new org.deeplearning4j.datasets.iterator.IteratorDataSetIterator(Collections.singletonList(ds).iterator(), 1);
            net.fit(iter2);
            Assert.assertNull(net.getInputMaskArrays());
            Assert.assertNull(net.getLabelMaskArrays());
            for (Layer l : net.getLayers()) {
                Assert.assertNull(l.getMaskArray());
            }
        }
    }

    @Test
    public void testInvalidTPBTT() {
        int nIn = 8;
        int nOut = 25;
        int nHiddenUnits = 17;
        try {
            new NeuralNetConfiguration.Builder().graphBuilder().addInputs("in").layer("0", new org.deeplearning4j.nn.conf.layers.LSTM.Builder().nIn(nIn).nOut(nHiddenUnits).build(), "in").layer("1", new GlobalPoolingLayer(), "0").layer("2", nIn(nHiddenUnits).nOut(nOut).activation(TANH).build(), "1").setOutputs("2").backpropType(TruncatedBPTT).build();
            Assert.fail("Exception expected");
        } catch (IllegalStateException e) {
            // e.printStackTrace();
            Assert.assertTrue(((e.getMessage().contains("TBPTT")) && (e.getMessage().contains("validateTbpttConfig"))));
        }
    }
}

