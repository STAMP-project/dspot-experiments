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
package org.deeplearning4j.regressiontest;


import BackpropType.TruncatedBPTT;
import ConvolutionMode.Same;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.TestUtils;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.variational.VariationalAutoencoder;
import org.deeplearning4j.nn.conf.serde.legacyformat.LegacyLayerDeserializer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInitXavier;
import org.deeplearning4j.regressiontest.customlayer100a.CustomLayer;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.io.ClassPathResource;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.learning.config.RmsProp;
import org.nd4j.linalg.learning.regularization.WeightDecay;


public class RegressionTest100a extends BaseDL4JTest {
    @Test
    public void testCustomLayer() throws Exception {
        File f = new ClassPathResource("regression_testing/100a/CustomLayerExample_100a.bin").getTempFileFromArchive();
        try {
            MultiLayerNetwork.load(f, true);
            Assert.fail("Expected exception");
        } catch (Exception e) {
            String msg = e.getMessage();
            Assert.assertTrue(msg, msg.contains("NeuralNetConfiguration.registerLegacyCustomClassesForJSON"));
        }
        NeuralNetConfiguration.registerLegacyCustomClassesForJSON(CustomLayer.class);
        MultiLayerNetwork net = MultiLayerNetwork.load(f, true);
        DenseLayer l0 = ((DenseLayer) (net.getLayer(0).conf().getLayer()));
        Assert.assertEquals(new ActivationTanH(), l0.getActivationFn());
        Assert.assertEquals(new WeightDecay(0.03, false), TestUtils.getWeightDecayReg(l0));
        Assert.assertEquals(new RmsProp(0.95), l0.getIUpdater());
        CustomLayer l1 = ((CustomLayer) (net.getLayer(1).conf().getLayer()));
        Assert.assertEquals(new ActivationTanH(), getActivationFn());
        Assert.assertEquals(new ActivationSigmoid(), l1.getSecondActivationFunction());
        Assert.assertEquals(new RmsProp(0.95), getIUpdater());
        INDArray outExp;
        File f2 = new ClassPathResource("regression_testing/100a/CustomLayerExample_Output_100a.bin").getTempFileFromArchive();
        try (DataInputStream dis = new DataInputStream(new FileInputStream(f2))) {
            outExp = Nd4j.read(dis);
        }
        INDArray in;
        File f3 = new ClassPathResource("regression_testing/100a/CustomLayerExample_Input_100a.bin").getTempFileFromArchive();
        try (DataInputStream dis = new DataInputStream(new FileInputStream(f3))) {
            in = Nd4j.read(dis);
        }
        INDArray outAct = net.output(in);
        Assert.assertEquals(outExp, outAct);
        // Check graph
        f = new ClassPathResource("regression_testing/100a/CustomLayerExample_Graph_100a.bin").getTempFileFromArchive();
        // Deregister custom class:
        new LegacyLayerDeserializer().getLegacyNamesMap().remove("CustomLayer");
        try {
            ComputationGraph.load(f, true);
            Assert.fail("Expected exception");
        } catch (Exception e) {
            String msg = e.getMessage();
            Assert.assertTrue(msg, msg.contains("NeuralNetConfiguration.registerLegacyCustomClassesForJSON"));
        }
        NeuralNetConfiguration.registerLegacyCustomClassesForJSON(CustomLayer.class);
        ComputationGraph graph = ComputationGraph.load(f, true);
        f2 = new ClassPathResource("regression_testing/100a/CustomLayerExample_Graph_Output_100a.bin").getTempFileFromArchive();
        try (DataInputStream dis = new DataInputStream(new FileInputStream(f2))) {
            outExp = Nd4j.read(dis);
        }
        outAct = graph.outputSingle(in);
        Assert.assertEquals(outExp, outAct);
    }

    @Test
    public void testGravesLSTM() throws Exception {
        File f = new ClassPathResource("regression_testing/100a/GravesLSTMCharModelingExample_100a.bin").getTempFileFromArchive();
        MultiLayerNetwork net = MultiLayerNetwork.load(f, true);
        GravesLSTM l0 = ((GravesLSTM) (net.getLayer(0).conf().getLayer()));
        Assert.assertEquals(new ActivationTanH(), l0.getActivationFn());
        Assert.assertEquals(200, l0.getNOut());
        Assert.assertEquals(new WeightInitXavier(), l0.getWeightInitFn());
        Assert.assertEquals(new WeightDecay(0.001, false), TestUtils.getWeightDecayReg(l0));
        Assert.assertEquals(new RmsProp(0.1), l0.getIUpdater());
        GravesLSTM l1 = ((GravesLSTM) (net.getLayer(1).conf().getLayer()));
        Assert.assertEquals(new ActivationTanH(), l1.getActivationFn());
        Assert.assertEquals(200, l1.getNOut());
        Assert.assertEquals(new WeightInitXavier(), l1.getWeightInitFn());
        Assert.assertEquals(new WeightDecay(0.001, false), TestUtils.getWeightDecayReg(l1));
        Assert.assertEquals(new RmsProp(0.1), l1.getIUpdater());
        RnnOutputLayer l2 = ((RnnOutputLayer) (net.getLayer(2).conf().getLayer()));
        Assert.assertEquals(new ActivationSoftmax(), l2.getActivationFn());
        Assert.assertEquals(77, l2.getNOut());
        Assert.assertEquals(new WeightInitXavier(), l2.getWeightInitFn());
        Assert.assertEquals(new WeightDecay(0.001, false), TestUtils.getWeightDecayReg(l0));
        Assert.assertEquals(new RmsProp(0.1), l0.getIUpdater());
        Assert.assertEquals(TruncatedBPTT, net.getLayerWiseConfigurations().getBackpropType());
        Assert.assertEquals(50, net.getLayerWiseConfigurations().getTbpttBackLength());
        Assert.assertEquals(50, net.getLayerWiseConfigurations().getTbpttFwdLength());
        INDArray outExp;
        File f2 = new ClassPathResource("regression_testing/100a/GravesLSTMCharModelingExample_Output_100a.bin").getTempFileFromArchive();
        try (DataInputStream dis = new DataInputStream(new FileInputStream(f2))) {
            outExp = Nd4j.read(dis);
        }
        INDArray in;
        File f3 = new ClassPathResource("regression_testing/100a/GravesLSTMCharModelingExample_Input_100a.bin").getTempFileFromArchive();
        try (DataInputStream dis = new DataInputStream(new FileInputStream(f3))) {
            in = Nd4j.read(dis);
        }
        INDArray outAct = net.output(in);
        Assert.assertEquals(outExp, outAct);
    }

    @Test
    public void testVae() throws Exception {
        File f = new ClassPathResource("regression_testing/100a/VaeMNISTAnomaly_100a.bin").getTempFileFromArchive();
        MultiLayerNetwork net = MultiLayerNetwork.load(f, true);
        VariationalAutoencoder l0 = ((VariationalAutoencoder) (net.getLayer(0).conf().getLayer()));
        Assert.assertEquals(new ActivationLReLU(), l0.getActivationFn());
        Assert.assertEquals(32, l0.getNOut());
        Assert.assertArrayEquals(new int[]{ 256, 256 }, l0.getEncoderLayerSizes());
        Assert.assertArrayEquals(new int[]{ 256, 256 }, l0.getDecoderLayerSizes());
        Assert.assertEquals(new WeightInitXavier(), l0.getWeightInitFn());
        Assert.assertEquals(new WeightDecay(1.0E-4, false), TestUtils.getWeightDecayReg(l0));
        Assert.assertEquals(new Adam(0.05), l0.getIUpdater());
        INDArray outExp;
        File f2 = new ClassPathResource("regression_testing/100a/VaeMNISTAnomaly_Output_100a.bin").getTempFileFromArchive();
        try (DataInputStream dis = new DataInputStream(new FileInputStream(f2))) {
            outExp = Nd4j.read(dis);
        }
        INDArray in;
        File f3 = new ClassPathResource("regression_testing/100a/VaeMNISTAnomaly_Input_100a.bin").getTempFileFromArchive();
        try (DataInputStream dis = new DataInputStream(new FileInputStream(f3))) {
            in = Nd4j.read(dis);
        }
        INDArray outAct = net.output(in);
        Assert.assertEquals(outExp, outAct);
    }

    @Test
    public void testYoloHouseNumber() throws Exception {
        File f = new ClassPathResource("regression_testing/100a/HouseNumberDetection_100a.bin").getTempFileFromArchive();
        ComputationGraph net = ComputationGraph.load(f, true);
        int nBoxes = 5;
        int nClasses = 10;
        ConvolutionLayer cl = ((ConvolutionLayer) (getLayerConf().getLayer()));
        Assert.assertEquals((nBoxes * (5 + nClasses)), cl.getNOut());
        Assert.assertEquals(new ActivationIdentity(), cl.getActivationFn());
        Assert.assertEquals(Same, cl.getConvolutionMode());
        Assert.assertEquals(new WeightInitXavier(), cl.getWeightInitFn());
        Assert.assertArrayEquals(new int[]{ 1, 1 }, cl.getKernelSize());
        Assert.assertArrayEquals(new int[]{ 1, 1 }, cl.getKernelSize());
        INDArray outExp;
        File f2 = new ClassPathResource("regression_testing/100a/HouseNumberDetection_Output_100a.bin").getTempFileFromArchive();
        try (DataInputStream dis = new DataInputStream(new FileInputStream(f2))) {
            outExp = Nd4j.read(dis);
        }
        INDArray in;
        File f3 = new ClassPathResource("regression_testing/100a/HouseNumberDetection_Input_100a.bin").getTempFileFromArchive();
        try (DataInputStream dis = new DataInputStream(new FileInputStream(f3))) {
            in = Nd4j.read(dis);
        }
        // Minor bug in 1.0.0-beta and earlier: not adding epsilon value to forward pass for batch norm
        // Which means: the record output doesn't have this. To account for this, we'll manually set eps to 0.0 here
        // https://github.com/deeplearning4j/deeplearning4j/issues/5836#issuecomment-405526228
        for (Layer l : net.getLayers()) {
            if ((l.conf().getLayer()) instanceof BatchNormalization) {
                BatchNormalization bn = ((BatchNormalization) (l.conf().getLayer()));
                bn.setEps(0.0);
            }
        }
        INDArray outAct = net.outputSingle(in);
        Assert.assertEquals(outExp, outAct.castTo(outExp.dataType()));
    }

    @Test
    public void testUpsampling2d() throws Exception {
        File f = new ClassPathResource("regression_testing/100a/upsampling/net.bin").getFile();
        MultiLayerNetwork net = MultiLayerNetwork.load(f, true);
        INDArray in;
        File fIn = new ClassPathResource("regression_testing/100a/upsampling/in.bin").getTempFileFromArchive();
        try (DataInputStream dis = new DataInputStream(new FileInputStream(fIn))) {
            in = Nd4j.read(dis);
        }
        INDArray label;
        File fLabels = new ClassPathResource("regression_testing/100a/upsampling/labels.bin").getTempFileFromArchive();
        try (DataInputStream dis = new DataInputStream(new FileInputStream(fLabels))) {
            label = Nd4j.read(dis);
        }
        INDArray outExp;
        File fOutExp = new ClassPathResource("regression_testing/100a/upsampling/out.bin").getTempFileFromArchive();
        try (DataInputStream dis = new DataInputStream(new FileInputStream(fOutExp))) {
            outExp = Nd4j.read(dis);
        }
        INDArray gradExp;
        File fGradExp = new ClassPathResource("regression_testing/100a/upsampling/gradient.bin").getTempFileFromArchive();
        try (DataInputStream dis = new DataInputStream(new FileInputStream(fGradExp))) {
            gradExp = Nd4j.read(dis);
        }
        INDArray out = net.output(in, false);
        Assert.assertEquals(outExp, out);
        net.setInput(in);
        net.setLabels(label);
        net.computeGradientAndScore();
        INDArray grad = net.getFlattenedGradients();
        Assert.assertEquals(gradExp, grad);
    }
}

