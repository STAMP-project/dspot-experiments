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
package org.deeplearning4j.nn.layers.convolution;


import Activation.RELU;
import Activation.SOFTMAX;
import SubsamplingLayer.PoolingType.AVG;
import SubsamplingLayer.PoolingType.MAX;
import SubsamplingLayer.PoolingType.SUM;
import WeightInit.XAVIER;
import java.util.Arrays;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer.Builder;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.gradient.Gradient;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.workspace.LayerWorkspaceMgr;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.primitives.Pair;


/**
 *
 *
 * @author Adam Gibson
 */
public class SubsamplingLayerTest extends BaseDL4JTest {
    private int nExamples = 1;

    private int depth = 20;// channels & nOut


    private int nChannelsIn = 1;

    private int inputWidth = 28;

    private int inputHeight = 28;

    private int[] kernelSize = new int[]{ 2, 2 };

    private int[] stride = new int[]{ 2, 2 };

    int featureMapWidth = (((inputWidth) - (kernelSize[0])) / (stride[0])) + 1;

    int featureMapHeight = (((inputHeight) - (kernelSize[1])) / (stride[0])) + 1;

    private INDArray epsilon = Nd4j.ones(nExamples, depth, featureMapHeight, featureMapWidth);

    @Test
    public void testSubSampleMaxActivate() throws Exception {
        INDArray containedExpectedOut = Nd4j.create(new double[]{ 5.0, 7.0, 6.0, 8.0, 4.0, 7.0, 5.0, 9.0 }, new long[]{ 1, 2, 2, 2 }).castTo(Nd4j.defaultFloatingPointType());
        INDArray containedInput = getContainedData();
        INDArray input = getData();
        Layer layer = getSubsamplingLayer(MAX);
        INDArray containedOutput = layer.activate(containedInput, false, LayerWorkspaceMgr.noWorkspaces());
        Assert.assertTrue(Arrays.equals(containedExpectedOut.shape(), containedOutput.shape()));
        Assert.assertEquals(containedExpectedOut, containedOutput);
        INDArray output = layer.activate(input, false, LayerWorkspaceMgr.noWorkspaces());
        Assert.assertTrue(Arrays.equals(new long[]{ nExamples, nChannelsIn, featureMapWidth, featureMapHeight }, output.shape()));
        Assert.assertEquals(nChannelsIn, output.size(1), 1.0E-4);// channels retained

    }

    @Test
    public void testSubSampleMeanActivate() throws Exception {
        INDArray containedExpectedOut = Nd4j.create(new double[]{ 2.0, 4.0, 3.0, 5.0, 3.5, 6.5, 4.5, 8.5 }, new int[]{ 1, 2, 2, 2 }).castTo(Nd4j.defaultFloatingPointType());
        INDArray containedInput = getContainedData();
        INDArray input = getData();
        Layer layer = getSubsamplingLayer(AVG);
        INDArray containedOutput = layer.activate(containedInput, false, LayerWorkspaceMgr.noWorkspaces());
        Assert.assertTrue(Arrays.equals(containedExpectedOut.shape(), containedOutput.shape()));
        Assert.assertEquals(containedExpectedOut, containedOutput);
        INDArray output = layer.activate(input, false, LayerWorkspaceMgr.noWorkspaces());
        Assert.assertTrue(Arrays.equals(new long[]{ nExamples, nChannelsIn, featureMapWidth, featureMapHeight }, output.shape()));
        Assert.assertEquals(nChannelsIn, output.size(1), 1.0E-4);// channels retained

    }

    // ////////////////////////////////////////////////////////////////////////////////
    @Test
    public void testSubSampleLayerMaxBackprop() throws Exception {
        INDArray expectedContainedEpsilonInput = Nd4j.create(new double[]{ 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0 }, new int[]{ 1, 2, 2, 2 }).castTo(Nd4j.defaultFloatingPointType());
        INDArray expectedContainedEpsilonResult = Nd4j.create(new double[]{ 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0 }, new int[]{ 1, 2, 4, 4 }).castTo(Nd4j.defaultFloatingPointType());
        INDArray input = getContainedData();
        Layer layer = getSubsamplingLayer(MAX);
        layer.activate(input, false, LayerWorkspaceMgr.noWorkspaces());
        Pair<Gradient, INDArray> containedOutput = layer.backpropGradient(expectedContainedEpsilonInput, LayerWorkspaceMgr.noWorkspaces());
        Assert.assertEquals(expectedContainedEpsilonResult, containedOutput.getSecond());
        Assert.assertEquals(null, containedOutput.getFirst().getGradientFor("W"));
        Assert.assertEquals(expectedContainedEpsilonResult.shape().length, containedOutput.getSecond().shape().length);
        INDArray input2 = getData();
        layer.activate(input2, false, LayerWorkspaceMgr.noWorkspaces());
        long depth = input2.size(1);
        epsilon = Nd4j.ones(5, depth, featureMapHeight, featureMapWidth);
        Pair<Gradient, INDArray> out = layer.backpropGradient(epsilon, LayerWorkspaceMgr.noWorkspaces());
        Assert.assertEquals(input.shape().length, out.getSecond().shape().length);
        Assert.assertEquals(depth, out.getSecond().size(1));// channels retained

    }

    @Test
    public void testSubSampleLayerAvgBackprop() throws Exception {
        INDArray expectedContainedEpsilonInput = Nd4j.create(new double[]{ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0 }, new int[]{ 1, 2, 2, 2 }).castTo(Nd4j.defaultFloatingPointType());
        INDArray expectedContainedEpsilonResult = Nd4j.create(new double[]{ 0.25, 0.25, 0.5, 0.5, 0.25, 0.25, 0.5, 0.5, 0.75, 0.75, 1.0, 1.0, 0.75, 0.75, 1.0, 1.0, 1.25, 1.25, 1.5, 1.5, 1.25, 1.25, 1.5, 1.5, 1.75, 1.75, 2.0, 2.0, 1.75, 1.75, 2.0, 2.0 }, new int[]{ 1, 2, 4, 4 }).castTo(Nd4j.defaultFloatingPointType());
        INDArray input = getContainedData();
        Layer layer = getSubsamplingLayer(AVG);
        layer.activate(input, false, LayerWorkspaceMgr.noWorkspaces());
        Pair<Gradient, INDArray> containedOutput = layer.backpropGradient(expectedContainedEpsilonInput, LayerWorkspaceMgr.noWorkspaces());
        Assert.assertEquals(expectedContainedEpsilonResult, containedOutput.getSecond());
        Assert.assertEquals(null, containedOutput.getFirst().getGradientFor("W"));
        Assert.assertArrayEquals(expectedContainedEpsilonResult.shape(), containedOutput.getSecond().shape());
    }

    @Test(expected = IllegalStateException.class)
    public void testSubSampleLayerSumBackprop() throws Exception {
        Layer layer = getSubsamplingLayer(SUM);
        INDArray input = getData();
        layer.setInput(input, LayerWorkspaceMgr.noWorkspaces());
        layer.backpropGradient(epsilon, LayerWorkspaceMgr.noWorkspaces());
    }

    // ////////////////////////////////////////////////////////////////////////////////
    @Test(expected = Exception.class)
    public void testSubTooLargeKernel() {
        int imageHeight = 20;
        int imageWidth = 23;
        int nChannels = 1;
        int classes = 2;
        int numSamples = 200;
        int kernelHeight = 3;
        int kernelWidth = 3;
        DataSet trainInput;
        MultiLayerConfiguration.Builder builder = new NeuralNetConfiguration.Builder().seed(123).list().layer(0, new Builder(kernelHeight, kernelWidth).stride(1, 1).nOut(2).activation(RELU).weightInit(XAVIER).build()).layer(1, // imageHeight-kernelHeight+1 is ok: full height
        new SubsamplingLayer.Builder().poolingType(MAX).kernelSize(((imageHeight - kernelHeight) + 2), 1).stride(1, 1).build()).layer(2, new OutputLayer.Builder().nOut(classes).weightInit(XAVIER).activation(SOFTMAX).build()).setInputType(InputType.convolutional(imageHeight, imageWidth, nChannels));
        MultiLayerConfiguration conf = builder.build();
        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
        INDArray emptyFeatures = Nd4j.zeros(numSamples, ((imageWidth * imageHeight) * nChannels));
        INDArray emptyLables = Nd4j.zeros(numSamples, classes);
        trainInput = new DataSet(emptyFeatures, emptyLables);
        model.fit(trainInput);
    }
}

