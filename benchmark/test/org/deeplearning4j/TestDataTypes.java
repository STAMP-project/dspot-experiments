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
package org.deeplearning4j;


import Activation.SOFTMAX;
import Activation.TANH;
import ConvolutionMode.Same;
import DataType.DOUBLE;
import DataType.FLOAT;
import DataType.HALF;
import LossFunctions.LossFunction.MCXENT;
import WeightInit.XAVIER;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.BatchNormalization;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;


@Slf4j
public class TestDataTypes extends BaseDL4JTest {
    private static DataType typeBefore;

    @Test
    public void testDataTypesSimple() throws Exception {
        Map<DataType, INDArray> outMapTrain = new HashMap<>();
        Map<DataType, INDArray> outMapTest = new HashMap<>();
        for (DataType type : new DataType[]{ DataType.HALF, DataType.FLOAT, DataType.DOUBLE }) {
            log.info("Starting test: {}", type);
            Nd4j.setDataType(type);
            Assert.assertEquals(type, Nd4j.dataType());
            MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().convolutionMode(Same).activation(TANH).seed(12345).weightInit(XAVIER).list().layer(new ConvolutionLayer.Builder().kernelSize(2, 2).stride(1, 1).padding(0, 0).nOut(3).build()).layer(new SubsamplingLayer.Builder().kernelSize(2, 2).stride(1, 1).padding(0, 0).build()).layer(new BatchNormalization()).layer(new ConvolutionLayer.Builder().kernelSize(2, 2).stride(1, 1).padding(0, 0).nOut(3).build()).layer(new OutputLayer.Builder().nOut(10).activation(SOFTMAX).lossFunction(MCXENT).build()).setInputType(InputType.convolutionalFlat(28, 28, 1)).build();
            MultiLayerNetwork net = new MultiLayerNetwork(conf);
            net.init();
            Field f1 = ConvolutionLayer.class.getDeclaredField("helper");
            f1.setAccessible(true);
            Field f2 = SubsamplingLayer.class.getDeclaredField("helper");
            f2.setAccessible(true);
            Field f3 = BatchNormalization.class.getDeclaredField("helper");
            f3.setAccessible(true);
            Assert.assertNotNull(f1.get(net.getLayer(0)));
            Assert.assertNotNull(f2.get(net.getLayer(1)));
            Assert.assertNotNull(f3.get(net.getLayer(2)));
            Assert.assertNotNull(f1.get(net.getLayer(3)));
            DataSet ds = new MnistDataSetIterator(32, true, 12345).next();
            // Simple sanity checks:
            // System.out.println("STARTING FIT");
            net.fit(ds);
            net.fit(ds);
            // System.out.println("STARTING OUTPUT");
            INDArray outTrain = net.output(ds.getFeatures(), false);
            INDArray outTest = net.output(ds.getFeatures(), true);
            outMapTrain.put(type, outTrain.castTo(DOUBLE));
            outMapTest.put(type, outTest.castTo(DOUBLE));
        }
        Nd4j.setDataType(DOUBLE);
        INDArray fp64Train = outMapTrain.get(DOUBLE);
        INDArray fp32Train = outMapTrain.get(FLOAT).castTo(DOUBLE);
        INDArray fp16Train = outMapTrain.get(HALF).castTo(DOUBLE);
        Assert.assertTrue(fp64Train.equalsWithEps(fp32Train, 0.001));
        Assert.assertTrue(fp64Train.equalsWithEps(fp16Train, 0.01));
    }
}

