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
package org.deeplearning4j.nn.layers.recurrent;


import Activation.SOFTMAX;
import Activation.TANH;
import CacheMode.NONE;
import GravesBidirectionalLSTMParamInitializer.BIAS_KEY_BACKWARDS;
import GravesBidirectionalLSTMParamInitializer.BIAS_KEY_FORWARDS;
import GravesBidirectionalLSTMParamInitializer.INPUT_WEIGHT_KEY_BACKWARDS;
import GravesBidirectionalLSTMParamInitializer.INPUT_WEIGHT_KEY_FORWARDS;
import GravesBidirectionalLSTMParamInitializer.RECURRENT_WEIGHT_KEY_BACKWARDS;
import GravesBidirectionalLSTMParamInitializer.RECURRENT_WEIGHT_KEY_FORWARDS;
import GravesLSTMParamInitializer.BIAS_KEY;
import GravesLSTMParamInitializer.INPUT_WEIGHT_KEY;
import GravesLSTMParamInitializer.RECURRENT_WEIGHT_KEY;
import LossFunctions.LossFunction.MCXENT;
import LossFunctions.LossFunction.MSE;
import OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT;
import WeightInit.ZERO;
import junit.framework.TestCase;
import lombok.val;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.distribution.UniformDistribution;
import org.deeplearning4j.nn.conf.layers.GravesBidirectionalLSTM.Builder;
import org.deeplearning4j.nn.gradient.Gradient;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.workspace.LayerWorkspaceMgr;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.activations.impl.ActivationSigmoid;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.AdaGrad;
import org.nd4j.linalg.learning.config.NoOp;
import org.nd4j.linalg.primitives.Pair;


public class GravesBidirectionalLSTMTest extends BaseDL4JTest {
    private double score = 0.0;

    @Test
    public void testBidirectionalLSTMGravesForwardBasic() {
        // Very basic test of forward prop. of LSTM layer with a time series.
        // Essentially make sure it doesn't throw any exceptions, and provides output in the correct shape.
        int nIn = 13;
        int nHiddenUnits = 17;
        final NeuralNetConfiguration conf = new NeuralNetConfiguration.Builder().layer(new Builder().nIn(nIn).nOut(nHiddenUnits).activation(TANH).build()).build();
        val numParams = conf.getLayer().initializer().numParams(conf);
        INDArray params = Nd4j.create(1, numParams);
        final GravesBidirectionalLSTM layer = ((GravesBidirectionalLSTM) (conf.getLayer().instantiate(conf, null, 0, params, true)));
        // Data: has shape [miniBatchSize,nIn,timeSeriesLength];
        // Output/activations has shape [miniBatchsize,nHiddenUnits,timeSeriesLength];
        final INDArray dataSingleExampleTimeLength1 = Nd4j.ones(1, nIn, 1);
        final INDArray activations1 = layer.activate(dataSingleExampleTimeLength1, false, LayerWorkspaceMgr.noWorkspaces());
        Assert.assertArrayEquals(activations1.shape(), new long[]{ 1, nHiddenUnits, 1 });
        final INDArray dataMultiExampleLength1 = Nd4j.ones(10, nIn, 1);
        final INDArray activations2 = layer.activate(dataMultiExampleLength1, false, LayerWorkspaceMgr.noWorkspaces());
        Assert.assertArrayEquals(activations2.shape(), new long[]{ 10, nHiddenUnits, 1 });
        final INDArray dataSingleExampleLength12 = Nd4j.ones(1, nIn, 12);
        final INDArray activations3 = layer.activate(dataSingleExampleLength12, false, LayerWorkspaceMgr.noWorkspaces());
        Assert.assertArrayEquals(activations3.shape(), new long[]{ 1, nHiddenUnits, 12 });
        final INDArray dataMultiExampleLength15 = Nd4j.ones(10, nIn, 15);
        final INDArray activations4 = layer.activate(dataMultiExampleLength15, false, LayerWorkspaceMgr.noWorkspaces());
        Assert.assertArrayEquals(activations4.shape(), new long[]{ 10, nHiddenUnits, 15 });
    }

    @Test
    public void testBidirectionalLSTMGravesBackwardBasic() {
        // Very basic test of backprop for mini-batch + time series
        // Essentially make sure it doesn't throw any exceptions, and provides output in the correct shape.
        GravesBidirectionalLSTMTest.testGravesBackwardBasicHelper(13, 3, 17, 10, 7);
        GravesBidirectionalLSTMTest.testGravesBackwardBasicHelper(13, 3, 17, 1, 7);// Edge case: miniBatchSize = 1

        GravesBidirectionalLSTMTest.testGravesBackwardBasicHelper(13, 3, 17, 10, 1);// Edge case: timeSeriesLength = 1

        GravesBidirectionalLSTMTest.testGravesBackwardBasicHelper(13, 3, 17, 1, 1);// Edge case: both miniBatchSize = 1 and timeSeriesLength = 1

    }

    @Test
    public void testGravesBidirectionalLSTMForwardPassHelper() throws Exception {
        // GravesBidirectionalLSTM.activateHelper() has different behaviour (due to optimizations) when forBackprop==true vs false
        // But should otherwise provide identical activations
        Nd4j.getRandom().setSeed(12345);
        final int nIn = 10;
        final int layerSize = 15;
        final int miniBatchSize = 4;
        final int timeSeriesLength = 7;
        final NeuralNetConfiguration conf = new NeuralNetConfiguration.Builder().layer(new Builder().nIn(nIn).nOut(layerSize).dist(new UniformDistribution(0, 1)).activation(TANH).build()).build();
        long numParams = conf.getLayer().initializer().numParams(conf);
        INDArray params = Nd4j.create(1, numParams);
        final GravesBidirectionalLSTM lstm = ((GravesBidirectionalLSTM) (conf.getLayer().instantiate(conf, null, 0, params, true)));
        final INDArray input = Nd4j.rand(new int[]{ miniBatchSize, nIn, timeSeriesLength });
        lstm.setInput(input, LayerWorkspaceMgr.noWorkspaces());
        final INDArray fwdPassFalse = LSTMHelpers.activateHelper(lstm, lstm.conf(), new ActivationSigmoid(), lstm.input(), lstm.getParam(RECURRENT_WEIGHT_KEY_FORWARDS), lstm.getParam(INPUT_WEIGHT_KEY_FORWARDS), lstm.getParam(BIAS_KEY_FORWARDS), false, null, null, false, true, INPUT_WEIGHT_KEY_FORWARDS, null, true, null, NONE, LayerWorkspaceMgr.noWorkspaces()).fwdPassOutput;
        final INDArray[] fwdPassTrue = LSTMHelpers.activateHelper(lstm, lstm.conf(), new ActivationSigmoid(), lstm.input(), lstm.getParam(RECURRENT_WEIGHT_KEY_FORWARDS), lstm.getParam(INPUT_WEIGHT_KEY_FORWARDS), lstm.getParam(BIAS_KEY_FORWARDS), false, null, null, true, true, INPUT_WEIGHT_KEY_FORWARDS, null, true, null, NONE, LayerWorkspaceMgr.noWorkspaces()).fwdPassOutputAsArrays;
        // I have no idea what the heck this does --Ben
        for (int i = 0; i < timeSeriesLength; i++) {
            final INDArray sliceFalse = fwdPassFalse.tensorAlongDimension(i, 1, 0);
            final INDArray sliceTrue = fwdPassTrue[i];
            Assert.assertTrue(sliceFalse.equals(sliceTrue));
        }
    }

    @Test
    public void testGetSetParmas() {
        final int nIn = 2;
        final int layerSize = 3;
        final int miniBatchSize = 2;
        final int timeSeriesLength = 10;
        Nd4j.getRandom().setSeed(12345);
        final NeuralNetConfiguration confBidirectional = new NeuralNetConfiguration.Builder().layer(new Builder().nIn(nIn).nOut(layerSize).dist(new UniformDistribution((-0.1), 0.1)).activation(TANH).build()).build();
        long numParams = confBidirectional.getLayer().initializer().numParams(confBidirectional);
        INDArray params = Nd4j.create(1, numParams);
        final GravesBidirectionalLSTM bidirectionalLSTM = ((GravesBidirectionalLSTM) (confBidirectional.getLayer().instantiate(confBidirectional, null, 0, params, true)));
        final INDArray sig = Nd4j.rand(new int[]{ miniBatchSize, nIn, timeSeriesLength });
        final INDArray act1 = bidirectionalLSTM.activate(sig, false, LayerWorkspaceMgr.noWorkspaces());
        params = bidirectionalLSTM.params();
        bidirectionalLSTM.setParams(params);
        final INDArray act2 = bidirectionalLSTM.activate(sig, false, LayerWorkspaceMgr.noWorkspaces());
        Assert.assertArrayEquals(act2.data().asDouble(), act1.data().asDouble(), 1.0E-8);
    }

    @Test
    public void testSimpleForwardsAndBackwardsActivation() {
        final int nIn = 2;
        final int layerSize = 3;
        final int miniBatchSize = 1;
        final int timeSeriesLength = 5;
        Nd4j.getRandom().setSeed(12345);
        final NeuralNetConfiguration confBidirectional = new NeuralNetConfiguration.Builder().layer(new Builder().nIn(nIn).nOut(layerSize).dist(new UniformDistribution((-0.1), 0.1)).activation(TANH).updater(new NoOp()).build()).build();
        final NeuralNetConfiguration confForwards = new NeuralNetConfiguration.Builder().layer(new org.deeplearning4j.nn.conf.layers.GravesLSTM.Builder().nIn(nIn).nOut(layerSize).weightInit(ZERO).activation(TANH).build()).build();
        long numParams = confForwards.getLayer().initializer().numParams(confForwards);
        INDArray params = Nd4j.create(1, numParams);
        long numParamsBD = confBidirectional.getLayer().initializer().numParams(confBidirectional);
        INDArray paramsBD = Nd4j.create(1, numParamsBD);
        final GravesBidirectionalLSTM bidirectionalLSTM = ((GravesBidirectionalLSTM) (confBidirectional.getLayer().instantiate(confBidirectional, null, 0, paramsBD, true)));
        final GravesLSTM forwardsLSTM = ((GravesLSTM) (confForwards.getLayer().instantiate(confForwards, null, 0, params, true)));
        bidirectionalLSTM.setBackpropGradientsViewArray(Nd4j.create(1, confBidirectional.getLayer().initializer().numParams(confBidirectional)));
        forwardsLSTM.setBackpropGradientsViewArray(Nd4j.create(1, confForwards.getLayer().initializer().numParams(confForwards)));
        final INDArray sig = Nd4j.rand(new int[]{ miniBatchSize, nIn, timeSeriesLength });
        final INDArray sigb = sig.dup();
        GravesBidirectionalLSTMTest.reverseColumnsInPlace(sigb.slice(0));
        final INDArray recurrentWeightsF = bidirectionalLSTM.getParam(RECURRENT_WEIGHT_KEY_FORWARDS);
        final INDArray inputWeightsF = bidirectionalLSTM.getParam(INPUT_WEIGHT_KEY_FORWARDS);
        final INDArray biasWeightsF = bidirectionalLSTM.getParam(BIAS_KEY_FORWARDS);
        final INDArray recurrentWeightsF2 = forwardsLSTM.getParam(RECURRENT_WEIGHT_KEY);
        final INDArray inputWeightsF2 = forwardsLSTM.getParam(INPUT_WEIGHT_KEY);
        final INDArray biasWeightsF2 = forwardsLSTM.getParam(BIAS_KEY);
        // assert that the forwards part of the bidirectional layer is equal to that of the regular LSTM
        Assert.assertArrayEquals(recurrentWeightsF2.shape(), recurrentWeightsF.shape());
        Assert.assertArrayEquals(inputWeightsF2.shape(), inputWeightsF.shape());
        Assert.assertArrayEquals(biasWeightsF2.shape(), biasWeightsF.shape());
        forwardsLSTM.setParam(RECURRENT_WEIGHT_KEY, recurrentWeightsF);
        forwardsLSTM.setParam(INPUT_WEIGHT_KEY, inputWeightsF);
        forwardsLSTM.setParam(BIAS_KEY, biasWeightsF);
        // copy forwards weights to make the forwards activations do the same thing
        final INDArray recurrentWeightsB = bidirectionalLSTM.getParam(RECURRENT_WEIGHT_KEY_BACKWARDS);
        final INDArray inputWeightsB = bidirectionalLSTM.getParam(INPUT_WEIGHT_KEY_BACKWARDS);
        final INDArray biasWeightsB = bidirectionalLSTM.getParam(BIAS_KEY_BACKWARDS);
        // assert that the forwards and backwards are the same shapes
        Assert.assertArrayEquals(recurrentWeightsF.shape(), recurrentWeightsB.shape());
        Assert.assertArrayEquals(inputWeightsF.shape(), inputWeightsB.shape());
        Assert.assertArrayEquals(biasWeightsF.shape(), biasWeightsB.shape());
        // zero out backwards layer
        bidirectionalLSTM.setParam(RECURRENT_WEIGHT_KEY_BACKWARDS, Nd4j.zeros(recurrentWeightsB.shape()));
        bidirectionalLSTM.setParam(INPUT_WEIGHT_KEY_BACKWARDS, Nd4j.zeros(inputWeightsB.shape()));
        bidirectionalLSTM.setParam(BIAS_KEY_BACKWARDS, Nd4j.zeros(biasWeightsB.shape()));
        forwardsLSTM.setInput(sig, LayerWorkspaceMgr.noWorkspaces());
        // compare activations
        final INDArray activation1 = forwardsLSTM.activate(sig, false, LayerWorkspaceMgr.noWorkspaces()).slice(0);
        final INDArray activation2 = bidirectionalLSTM.activate(sig, false, LayerWorkspaceMgr.noWorkspaces()).slice(0);
        Assert.assertArrayEquals(activation1.data().asFloat(), activation2.data().asFloat(), 1.0E-5F);
        final INDArray randSig = Nd4j.rand(new int[]{ 1, layerSize, timeSeriesLength });
        final INDArray randSigBackwards = randSig.dup();
        GravesBidirectionalLSTMTest.reverseColumnsInPlace(randSigBackwards.slice(0));
        final Pair<Gradient, INDArray> backprop1 = forwardsLSTM.backpropGradient(randSig, LayerWorkspaceMgr.noWorkspaces());
        final Pair<Gradient, INDArray> backprop2 = bidirectionalLSTM.backpropGradient(randSig, LayerWorkspaceMgr.noWorkspaces());
        // compare gradients
        Assert.assertArrayEquals(backprop1.getFirst().getGradientFor(RECURRENT_WEIGHT_KEY).dup().data().asFloat(), backprop2.getFirst().getGradientFor(RECURRENT_WEIGHT_KEY_FORWARDS).dup().data().asFloat(), 1.0E-5F);
        Assert.assertArrayEquals(backprop1.getFirst().getGradientFor(INPUT_WEIGHT_KEY).dup().data().asFloat(), backprop2.getFirst().getGradientFor(INPUT_WEIGHT_KEY_FORWARDS).dup().data().asFloat(), 1.0E-5F);
        Assert.assertArrayEquals(backprop1.getFirst().getGradientFor(BIAS_KEY).dup().data().asFloat(), backprop2.getFirst().getGradientFor(BIAS_KEY_FORWARDS).dup().data().asFloat(), 1.0E-5F);
        // copy forwards to backwards
        bidirectionalLSTM.setParam(RECURRENT_WEIGHT_KEY_BACKWARDS, bidirectionalLSTM.getParam(RECURRENT_WEIGHT_KEY_FORWARDS));
        bidirectionalLSTM.setParam(INPUT_WEIGHT_KEY_BACKWARDS, bidirectionalLSTM.getParam(INPUT_WEIGHT_KEY_FORWARDS));
        bidirectionalLSTM.setParam(BIAS_KEY_BACKWARDS, bidirectionalLSTM.getParam(BIAS_KEY_FORWARDS));
        // zero out forwards layer
        bidirectionalLSTM.setParam(RECURRENT_WEIGHT_KEY_FORWARDS, Nd4j.zeros(recurrentWeightsB.shape()));
        bidirectionalLSTM.setParam(INPUT_WEIGHT_KEY_FORWARDS, Nd4j.zeros(inputWeightsB.shape()));
        bidirectionalLSTM.setParam(BIAS_KEY_FORWARDS, Nd4j.zeros(biasWeightsB.shape()));
        // run on reversed signal
        final INDArray activation3 = bidirectionalLSTM.activate(sigb, false, LayerWorkspaceMgr.noWorkspaces()).slice(0);
        final INDArray activation3Reverse = activation3.dup();
        GravesBidirectionalLSTMTest.reverseColumnsInPlace(activation3Reverse);
        Assert.assertEquals(activation3Reverse, activation1);
        Assert.assertArrayEquals(activation3Reverse.shape(), activation1.shape());
        // test backprop now
        final INDArray refBackGradientReccurrent = backprop1.getFirst().getGradientFor(RECURRENT_WEIGHT_KEY);
        final INDArray refBackGradientInput = backprop1.getFirst().getGradientFor(INPUT_WEIGHT_KEY);
        final INDArray refBackGradientBias = backprop1.getFirst().getGradientFor(BIAS_KEY);
        // reverse weights only with backwards signal should yield same result as forwards weights with forwards signal
        final Pair<Gradient, INDArray> backprop3 = bidirectionalLSTM.backpropGradient(randSigBackwards, LayerWorkspaceMgr.noWorkspaces());
        final INDArray backGradientRecurrent = backprop3.getFirst().getGradientFor(RECURRENT_WEIGHT_KEY_BACKWARDS);
        final INDArray backGradientInput = backprop3.getFirst().getGradientFor(INPUT_WEIGHT_KEY_BACKWARDS);
        final INDArray backGradientBias = backprop3.getFirst().getGradientFor(BIAS_KEY_BACKWARDS);
        Assert.assertArrayEquals(refBackGradientBias.dup().data().asDouble(), backGradientBias.dup().data().asDouble(), 1.0E-6);
        Assert.assertArrayEquals(refBackGradientInput.dup().data().asDouble(), backGradientInput.dup().data().asDouble(), 1.0E-6);
        Assert.assertArrayEquals(refBackGradientReccurrent.dup().data().asDouble(), backGradientRecurrent.dup().data().asDouble(), 1.0E-6);
        final INDArray refEpsilon = backprop1.getSecond().dup();
        final INDArray backEpsilon = backprop3.getSecond().dup();
        GravesBidirectionalLSTMTest.reverseColumnsInPlace(refEpsilon.slice(0));
        Assert.assertArrayEquals(backEpsilon.dup().data().asDouble(), refEpsilon.dup().data().asDouble(), 1.0E-6);
    }

    @Test
    public void testSerialization() {
        final MultiLayerConfiguration conf1 = new NeuralNetConfiguration.Builder().optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).updater(new AdaGrad(0.1)).l2(0.001).seed(12345).list().layer(0, new Builder().activation(TANH).nIn(2).nOut(2).dist(new UniformDistribution((-0.05), 0.05)).build()).layer(1, new Builder().activation(TANH).nIn(2).nOut(2).dist(new UniformDistribution((-0.05), 0.05)).build()).layer(2, new org.deeplearning4j.nn.conf.layers.RnnOutputLayer.Builder().activation(SOFTMAX).lossFunction(MCXENT).nIn(2).nOut(2).build()).build();
        final String json1 = conf1.toJson();
        final MultiLayerConfiguration conf2 = MultiLayerConfiguration.fromJson(json1);
        final String json2 = conf1.toJson();
        TestCase.assertEquals(json1, json2);
    }

    @Test
    public void testGateActivationFnsSanityCheck() {
        for (String gateAfn : new String[]{ "sigmoid", "hardsigmoid" }) {
            MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).seed(12345).list().layer(0, new Builder().gateActivationFunction(gateAfn).activation(TANH).nIn(2).nOut(2).build()).layer(1, new org.deeplearning4j.nn.conf.layers.RnnOutputLayer.Builder().lossFunction(MSE).nIn(2).nOut(2).activation(TANH).build()).build();
            MultiLayerNetwork net = new MultiLayerNetwork(conf);
            net.init();
            Assert.assertEquals(gateAfn, getGateActivationFn().toString());
            INDArray in = Nd4j.rand(new int[]{ 3, 2, 5 });
            INDArray labels = Nd4j.rand(new int[]{ 3, 2, 5 });
            net.fit(in, labels);
        }
    }
}

