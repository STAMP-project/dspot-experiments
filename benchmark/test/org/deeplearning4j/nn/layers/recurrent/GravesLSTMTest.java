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


import Activation.TANH;
import LossFunctions.LossFunction.MSE;
import OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import lombok.val;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.distribution.UniformDistribution;
import org.deeplearning4j.nn.conf.layers.GravesLSTM.Builder;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.workspace.LayerWorkspaceMgr;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.learning.config.Sgd;


public class GravesLSTMTest extends BaseDL4JTest {
    @Test
    public void testLSTMGravesForwardBasic() {
        // Very basic test of forward prop. of LSTM layer with a time series.
        // Essentially make sure it doesn't throw any exceptions, and provides output in the correct shape.
        int nIn = 13;
        int nHiddenUnits = 17;
        NeuralNetConfiguration conf = new NeuralNetConfiguration.Builder().layer(new Builder().nIn(nIn).nOut(nHiddenUnits).activation(TANH).build()).build();
        val numParams = conf.getLayer().initializer().numParams(conf);
        INDArray params = Nd4j.create(1, numParams);
        GravesLSTM layer = ((GravesLSTM) (conf.getLayer().instantiate(conf, null, 0, params, true)));
        // Data: has shape [miniBatchSize,nIn,timeSeriesLength];
        // Output/activations has shape [miniBatchsize,nHiddenUnits,timeSeriesLength];
        INDArray dataSingleExampleTimeLength1 = Nd4j.ones(1, nIn, 1);
        INDArray activations1 = layer.activate(dataSingleExampleTimeLength1, false, LayerWorkspaceMgr.noWorkspaces());
        Assert.assertArrayEquals(activations1.shape(), new long[]{ 1, nHiddenUnits, 1 });
        INDArray dataMultiExampleLength1 = Nd4j.ones(10, nIn, 1);
        INDArray activations2 = layer.activate(dataMultiExampleLength1, false, LayerWorkspaceMgr.noWorkspaces());
        Assert.assertArrayEquals(activations2.shape(), new long[]{ 10, nHiddenUnits, 1 });
        INDArray dataSingleExampleLength12 = Nd4j.ones(1, nIn, 12);
        INDArray activations3 = layer.activate(dataSingleExampleLength12, false, LayerWorkspaceMgr.noWorkspaces());
        Assert.assertArrayEquals(activations3.shape(), new long[]{ 1, nHiddenUnits, 12 });
        INDArray dataMultiExampleLength15 = Nd4j.ones(10, nIn, 15);
        INDArray activations4 = layer.activate(dataMultiExampleLength15, false, LayerWorkspaceMgr.noWorkspaces());
        Assert.assertArrayEquals(activations4.shape(), new long[]{ 10, nHiddenUnits, 15 });
    }

    @Test
    public void testLSTMGravesBackwardBasic() {
        // Very basic test of backprop for mini-batch + time series
        // Essentially make sure it doesn't throw any exceptions, and provides output in the correct shape.
        GravesLSTMTest.testGravesBackwardBasicHelper(13, 3, 17, 10, 7);
        GravesLSTMTest.testGravesBackwardBasicHelper(13, 3, 17, 1, 7);// Edge case: miniBatchSize = 1

        GravesLSTMTest.testGravesBackwardBasicHelper(13, 3, 17, 10, 1);// Edge case: timeSeriesLength = 1

        GravesLSTMTest.testGravesBackwardBasicHelper(13, 3, 17, 1, 1);// Edge case: both miniBatchSize = 1 and timeSeriesLength = 1

    }

    @Test
    public void testGravesLSTMForwardPassHelper() throws Exception {
        // GravesLSTM.activateHelper() has different behaviour (due to optimizations) when forBackprop==true vs false
        // But should otherwise provide identical activations
        Nd4j.getRandom().setSeed(12345);
        int nIn = 10;
        int layerSize = 15;
        int miniBatchSize = 4;
        int timeSeriesLength = 7;
        NeuralNetConfiguration conf = new NeuralNetConfiguration.Builder().layer(new Builder().nIn(nIn).nOut(layerSize).dist(new UniformDistribution(0, 1)).activation(TANH).build()).build();
        val numParams = conf.getLayer().initializer().numParams(conf);
        INDArray params = Nd4j.create(1, numParams);
        GravesLSTM lstm = ((GravesLSTM) (conf.getLayer().instantiate(conf, null, 0, params, true)));
        INDArray input = Nd4j.rand(new int[]{ miniBatchSize, nIn, timeSeriesLength });
        lstm.setInput(input, LayerWorkspaceMgr.noWorkspaces());
        Method actHelper = GravesLSTM.class.getDeclaredMethod("activateHelper", boolean.class, INDArray.class, INDArray.class, boolean.class, LayerWorkspaceMgr.class);
        actHelper.setAccessible(true);
        // Call activateHelper with both forBackprop == true, and forBackprop == false and compare
        Class<?> innerClass = Class.forName("org.deeplearning4j.nn.layers.recurrent.FwdPassReturn");
        Object oFalse = actHelper.invoke(lstm, false, null, null, false, LayerWorkspaceMgr.noWorkspacesImmutable());// GravesLSTM.FwdPassReturn object; want fwdPassOutput INDArray

        Object oTrue = actHelper.invoke(lstm, false, null, null, true, LayerWorkspaceMgr.noWorkspacesImmutable());// want fwdPassOutputAsArrays object

        Field fwdPassOutput = innerClass.getDeclaredField("fwdPassOutput");
        fwdPassOutput.setAccessible(true);
        Field fwdPassOutputAsArrays = innerClass.getDeclaredField("fwdPassOutputAsArrays");
        fwdPassOutputAsArrays.setAccessible(true);
        INDArray fwdPassFalse = ((INDArray) (fwdPassOutput.get(oFalse)));
        INDArray[] fwdPassTrue = ((INDArray[]) (fwdPassOutputAsArrays.get(oTrue)));
        for (int i = 0; i < timeSeriesLength; i++) {
            INDArray sliceFalse = fwdPassFalse.tensorAlongDimension(i, 1, 0);
            INDArray sliceTrue = fwdPassTrue[i];
            Assert.assertTrue(sliceFalse.equals(sliceTrue));
        }
    }

    @Test
    public void testSingleExample() {
        Nd4j.getRandom().setSeed(12345);
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).updater(new Sgd(0.1)).seed(12345).list().layer(0, new Builder().activation(TANH).nIn(2).nOut(2).build()).layer(1, new org.deeplearning4j.nn.conf.layers.RnnOutputLayer.Builder().lossFunction(MSE).nIn(2).nOut(1).activation(TANH).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        INDArray in1 = Nd4j.rand(new int[]{ 1, 2, 4 });
        INDArray in2 = Nd4j.rand(new int[]{ 1, 2, 5 });
        in2.put(new INDArrayIndex[]{ NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(0, 4) }, in1);
        Assert.assertEquals(in1, in2.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(0, 4)));
        INDArray labels1 = Nd4j.rand(new int[]{ 1, 1, 4 });
        INDArray labels2 = Nd4j.create(1, 1, 5);
        labels2.put(new INDArrayIndex[]{ NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(0, 4) }, labels1);
        Assert.assertEquals(labels1, labels2.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(0, 4)));
        INDArray out1 = net.output(in1);
        INDArray out2 = net.output(in2);
        System.out.println(Arrays.toString(net.output(in1).data().asFloat()));
        System.out.println(Arrays.toString(net.output(in2).data().asFloat()));
        List<INDArray> activations1 = net.feedForward(in1);
        List<INDArray> activations2 = net.feedForward(in2);
        for (int i = 0; i < 3; i++) {
            System.out.println(("-----\n" + i));
            System.out.println(Arrays.toString(activations1.get(i).dup().data().asDouble()));
            System.out.println(Arrays.toString(activations2.get(i).dup().data().asDouble()));
            System.out.println(activations1.get(i));
            System.out.println(activations2.get(i));
        }
        // Expect first 4 time steps to be indentical...
        for (int i = 0; i < 4; i++) {
            double d1 = out1.getDouble(i);
            double d2 = out2.getDouble(i);
            Assert.assertEquals(d1, d2, 0.0);
        }
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

