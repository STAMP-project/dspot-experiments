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


import SubsamplingLayer.PoolingType.MAX;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.layers.convolution.ConvolutionLayer;
import org.deeplearning4j.nn.layers.convolution.subsampling.SubsamplingLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.Test;
import org.nd4j.linalg.activations.IActivation;
import org.nd4j.linalg.activations.impl.ActivationIdentity;
import org.nd4j.linalg.activations.impl.ActivationSoftmax;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.impl.LossNegativeLogLikelihood;
import org.nd4j.linalg.schedule.ScheduleType;


@Slf4j
public class ValidateCuDNN extends BaseDL4JTest {
    @Test
    public void validateConvLayers() {
        Nd4j.getRandom().setSeed(12345);
        int numClasses = 10;
        // imageHeight,imageWidth,channels
        int imageHeight = 240;
        int imageWidth = 240;
        int channels = 3;
        IActivation activation = new ActivationIdentity();
        MultiLayerConfiguration multiLayerConfiguration = new org.deeplearning4j.nn.conf.NeuralNetConfiguration.Builder().weightInit(WeightInit.XAVIER).seed(42).activation(new org.nd4j.linalg.activations.impl.ActivationELU()).updater(new Nesterovs(0.01, 0.9)).list(new Convolution2D.Builder().nOut(96).kernelSize(11, 11).biasInit(0.0).stride(4, 4).build(), new ActivationLayer.Builder().activation(activation).build(), new Pooling2D.Builder().poolingType(MAX).kernelSize(3, 3).stride(2, 2).build(), new Convolution2D.Builder().nOut(256).kernelSize(5, 5).padding(2, 2).biasInit(0.0).stride(1, 1).build(), new ActivationLayer.Builder().activation(activation).build(), new Pooling2D.Builder().poolingType(MAX).kernelSize(3, 3).stride(2, 2).build(), new Convolution2D.Builder().nOut(384).kernelSize(3, 3).padding(1, 1).biasInit(0.0).stride(1, 1).build(), new ActivationLayer.Builder().activation(activation).build(), new Convolution2D.Builder().nOut(256).kernelSize(3, 3).padding(1, 1).stride(1, 1).build(), new ActivationLayer.Builder().activation(activation).build(), new Pooling2D.Builder().poolingType(MAX).kernelSize(3, 3).stride(2, 2).build(), new DenseLayer.Builder().nOut(4096).biasInit(0.0).build(), new ActivationLayer.Builder().activation(activation).build(), new OutputLayer.Builder().activation(new ActivationSoftmax()).lossFunction(new LossNegativeLogLikelihood()).nOut(numClasses).biasInit(0.0).build()).setInputType(InputType.convolutionalFlat(imageHeight, imageWidth, channels)).build();
        MultiLayerNetwork net = new MultiLayerNetwork(multiLayerConfiguration);
        net.init();
        int[] fShape = new int[]{ 32, channels, imageHeight, imageWidth };
        int[] lShape = new int[]{ 32, numClasses };
        List<Class<?>> classesToTest = new ArrayList<>();
        classesToTest.add(ConvolutionLayer.class);
        classesToTest.add(SubsamplingLayer.class);
        ValidateCuDNN.validateLayers(net, classesToTest, true, fShape, lShape);
    }

    @Test
    public void validateConvLayersSimpleBN() {
        // Test ONLY BN - no other CuDNN functionality (i.e., DL4J impls for everything else)
        Nd4j.getRandom().setSeed(12345);
        int numClasses = 10;
        // imageHeight,imageWidth,channels
        int imageHeight = 240;
        int imageWidth = 240;
        int channels = 3;
        IActivation activation = new ActivationIdentity();
        MultiLayerConfiguration multiLayerConfiguration = new org.deeplearning4j.nn.conf.NeuralNetConfiguration.Builder().weightInit(WeightInit.XAVIER).seed(42).activation(new org.nd4j.linalg.activations.impl.ActivationELU()).updater(Nesterovs.builder().momentum(0.9).learningRateSchedule(new org.nd4j.linalg.schedule.StepSchedule(ScheduleType.EPOCH, 0.01, 0.1, 20)).build()).list(new Convolution2D.Builder().nOut(96).kernelSize(11, 11).biasInit(0.0).stride(4, 4).build(), new ActivationLayer.Builder().activation(activation).build(), new BatchNormalization.Builder().build(), new Pooling2D.Builder().poolingType(MAX).kernelSize(3, 3).stride(2, 2).build(), new DenseLayer.Builder().nOut(128).biasInit(0.0).build(), new ActivationLayer.Builder().activation(activation).build(), new OutputLayer.Builder().activation(new ActivationSoftmax()).lossFunction(new LossNegativeLogLikelihood()).nOut(numClasses).biasInit(0.0).build()).setInputType(InputType.convolutionalFlat(imageHeight, imageWidth, channels)).build();
        MultiLayerNetwork net = new MultiLayerNetwork(multiLayerConfiguration);
        net.init();
        int[] fShape = new int[]{ 32, channels, imageHeight, imageWidth };
        int[] lShape = new int[]{ 32, numClasses };
        List<Class<?>> classesToTest = new ArrayList<>();
        classesToTest.add(org.deeplearning4j.nn.layers.normalization.BatchNormalization.class);
        ValidateCuDNN.validateLayers(net, classesToTest, false, fShape, lShape);
    }

    @Test
    public void validateConvLayersLRN() {
        // Test ONLY LRN - no other CuDNN functionality (i.e., DL4J impls for everything else)
        Nd4j.getRandom().setSeed(12345);
        int numClasses = 10;
        // imageHeight,imageWidth,channels
        int imageHeight = 240;
        int imageWidth = 240;
        int channels = 3;
        IActivation activation = new ActivationIdentity();
        MultiLayerConfiguration multiLayerConfiguration = new org.deeplearning4j.nn.conf.NeuralNetConfiguration.Builder().weightInit(WeightInit.XAVIER).seed(42).activation(new org.nd4j.linalg.activations.impl.ActivationELU()).updater(Nesterovs.builder().momentum(0.9).learningRateSchedule(new org.nd4j.linalg.schedule.StepSchedule(ScheduleType.EPOCH, 0.01, 0.1, 20)).build()).list(new Convolution2D.Builder().nOut(96).kernelSize(11, 11).biasInit(0.0).stride(4, 4).build(), new ActivationLayer.Builder().activation(activation).build(), new LocalResponseNormalization.Builder().alpha(0.001).beta(0.75).k(2).n(5).build(), new Pooling2D.Builder().poolingType(MAX).kernelSize(3, 3).stride(2, 2).build(), new Convolution2D.Builder().nOut(256).kernelSize(5, 5).padding(2, 2).biasInit(0.0).stride(1, 1).build(), new ActivationLayer.Builder().activation(activation).build(), new OutputLayer.Builder().activation(new ActivationSoftmax()).lossFunction(new LossNegativeLogLikelihood()).nOut(numClasses).biasInit(0.0).build()).setInputType(InputType.convolutionalFlat(imageHeight, imageWidth, channels)).build();
        MultiLayerNetwork net = new MultiLayerNetwork(multiLayerConfiguration);
        net.init();
        int[] fShape = new int[]{ 32, channels, imageHeight, imageWidth };
        int[] lShape = new int[]{ 32, numClasses };
        List<Class<?>> classesToTest = new ArrayList<>();
        classesToTest.add(org.deeplearning4j.nn.layers.normalization.LocalResponseNormalization.class);
        ValidateCuDNN.validateLayers(net, classesToTest, false, fShape, lShape);
    }
}

