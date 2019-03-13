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


import Activation.RELU;
import Activation.SOFTMAX;
import OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT;
import SubsamplingLayer.PoolingType.MAX;
import WeightInit.XAVIER;
import java.util.Arrays;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.exception.DL4JInvalidConfigException;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ComputationGraph;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;


/**
 * Created by nyghtowl on 1/15/16.
 */
// @Ignore
public class TestCompGraphCNN extends BaseDL4JTest {
    protected ComputationGraphConfiguration conf;

    protected ComputationGraph graph;

    protected DataSetIterator dataSetIterator;

    protected DataSet ds;

    @Test
    public void testConfigBasic() {
        // Check the order. there are 2 possible valid orders here
        int[] order = graph.topologicalSortOrder();
        int[] expOrder1 = new int[]{ 0, 1, 2, 4, 3, 5, 6 };// First of 2 possible valid orders

        int[] expOrder2 = new int[]{ 0, 2, 1, 4, 3, 5, 6 };// Second of 2 possible valid orders

        boolean orderOK = (Arrays.equals(expOrder1, order)) || (Arrays.equals(expOrder2, order));
        Assert.assertTrue(orderOK);
        INDArray params = graph.params();
        Assert.assertNotNull(params);
        // confirm param shape is what is expected
        int nParams = TestCompGraphCNN.getNumParams();
        Assert.assertEquals(nParams, params.length());
        INDArray arr = Nd4j.linspace(0, nParams, nParams, Nd4j.dataType());
        Assert.assertEquals(nParams, arr.length());
        // params are set
        graph.setParams(arr);
        params = graph.params();
        Assert.assertEquals(arr, params);
        // Number of inputs and outputs:
        Assert.assertEquals(1, graph.getNumInputArrays());
        Assert.assertEquals(1, graph.getNumOutputArrays());
    }

    @Test(expected = DL4JInvalidConfigException.class)
    public void testCNNComputationGraphKernelTooLarge() {
        int imageWidth = 23;
        int imageHeight = 19;
        int nChannels = 1;
        int classes = 2;
        int numSamples = 200;
        int kernelHeight = 3;
        int kernelWidth = imageWidth;
        DataSet trainInput;
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).seed(123).graphBuilder().addInputs("input").setInputTypes(InputType.convolutional(nChannels, imageWidth, imageHeight)).addLayer("conv1", new ConvolutionLayer.Builder().kernelSize(kernelHeight, kernelWidth).stride(1, 1).nIn(nChannels).nOut(2).weightInit(XAVIER).activation(RELU).build(), "input").addLayer("pool1", new SubsamplingLayer.Builder().poolingType(MAX).kernelSize(((imageHeight - kernelHeight) + 1), 1).stride(1, 1).build(), "conv1").addLayer("output", new OutputLayer.Builder().nOut(classes).activation(SOFTMAX).build(), "pool1").setOutputs("output").build();
        ComputationGraph model = new ComputationGraph(conf);
        model.init();
        INDArray emptyFeatures = Nd4j.zeros(numSamples, ((imageWidth * imageHeight) * nChannels));
        INDArray emptyLables = Nd4j.zeros(numSamples, classes);
        trainInput = new DataSet(emptyFeatures, emptyLables);
        model.fit(trainInput);
    }
}

