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
package org.deeplearning4j.nn.conf;


import Activation.RELU;
import LossFunctions.LossFunction;
import MultiLayerConfiguration.Builder;
import OptimizationAlgorithm.CONJUGATE_GRADIENT;
import WeightInit.XAVIER;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Properties;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.exception.DL4JInvalidConfigException;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.conf.distribution.NormalDistribution;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.preprocessor.CnnToFeedForwardPreProcessor;
import org.deeplearning4j.nn.conf.weightnoise.DropConnect;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;


/**
 * Created by agibsonccc on 11/27/14.
 */
public class MultiLayerNeuralNetConfigurationTest extends BaseDL4JTest {
    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

    @Test
    public void testJson() throws Exception {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().list().layer(0, build()).inputPreProcessor(0, new CnnToFeedForwardPreProcessor()).build();
        String json = conf.toJson();
        MultiLayerConfiguration from = MultiLayerConfiguration.fromJson(json);
        Assert.assertEquals(conf.getConf(0), from.getConf(0));
        Properties props = new Properties();
        props.put("json", json);
        String key = props.getProperty("json");
        Assert.assertEquals(json, key);
        File f = testDir.newFile("props");
        f.deleteOnExit();
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
        props.store(bos, "");
        bos.flush();
        bos.close();
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
        Properties props2 = new Properties();
        props2.load(bis);
        bis.close();
        Assert.assertEquals(props2.getProperty("json"), props.getProperty("json"));
        String json2 = props2.getProperty("json");
        MultiLayerConfiguration conf3 = MultiLayerConfiguration.fromJson(json2);
        Assert.assertEquals(conf.getConf(0), conf3.getConf(0));
    }

    @Test
    public void testConvnetJson() {
        final int numRows = 76;
        final int numColumns = 76;
        int nChannels = 3;
        int outputNum = 6;
        int seed = 123;
        // setup the network
        MultiLayerConfiguration.Builder builder = new NeuralNetConfiguration.Builder().seed(seed).l1(0.1).l2(2.0E-4).weightNoise(new DropConnect(0.5)).miniBatch(true).optimizationAlgo(CONJUGATE_GRADIENT).list().layer(0, build()).layer(1, build()).layer(2, build()).layer(3, build()).layer(4, build()).layer(5, build()).setInputType(InputType.convolutional(numRows, numColumns, nChannels));
        MultiLayerConfiguration conf = builder.build();
        String json = conf.toJson();
        MultiLayerConfiguration conf2 = MultiLayerConfiguration.fromJson(json);
        Assert.assertEquals(conf, conf2);
    }

    @Test
    public void testUpsamplingConvnetJson() {
        final int numRows = 76;
        final int numColumns = 76;
        int nChannels = 3;
        int outputNum = 6;
        int seed = 123;
        // setup the network
        MultiLayerConfiguration.Builder builder = new NeuralNetConfiguration.Builder().seed(seed).l1(0.1).l2(2.0E-4).dropOut(0.5).miniBatch(true).optimizationAlgo(CONJUGATE_GRADIENT).list().layer(build()).layer(build()).layer(2, build()).layer(build()).layer(4, build()).layer(5, build()).setInputType(InputType.convolutional(numRows, numColumns, nChannels));
        MultiLayerConfiguration conf = builder.build();
        String json = conf.toJson();
        MultiLayerConfiguration conf2 = MultiLayerConfiguration.fromJson(json);
        Assert.assertEquals(conf, conf2);
    }

    @Test
    public void testGlobalPoolingJson() {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().updater(new org.nd4j.linalg.learning.config.NoOp()).dist(new NormalDistribution(0, 1.0)).seed(12345L).list().layer(0, build()).layer(1, build()).layer(2, build()).setInputType(InputType.convolutional(32, 32, 1)).build();
        String str = conf.toJson();
        MultiLayerConfiguration fromJson = conf.fromJson(str);
        Assert.assertEquals(conf, fromJson);
    }

    @Test
    public void testYaml() throws Exception {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().list().layer(0, build()).inputPreProcessor(0, new CnnToFeedForwardPreProcessor()).build();
        String json = conf.toYaml();
        MultiLayerConfiguration from = MultiLayerConfiguration.fromYaml(json);
        Assert.assertEquals(conf.getConf(0), from.getConf(0));
        Properties props = new Properties();
        props.put("json", json);
        String key = props.getProperty("json");
        Assert.assertEquals(json, key);
        File f = testDir.newFile("props");
        f.deleteOnExit();
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
        props.store(bos, "");
        bos.flush();
        bos.close();
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
        Properties props2 = new Properties();
        props2.load(bis);
        bis.close();
        Assert.assertEquals(props2.getProperty("json"), props.getProperty("json"));
        String yaml = props2.getProperty("json");
        MultiLayerConfiguration conf3 = MultiLayerConfiguration.fromYaml(yaml);
        Assert.assertEquals(conf.getConf(0), conf3.getConf(0));
    }

    @Test
    public void testClone() {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().list().layer(0, new DenseLayer.Builder().build()).layer(1, build()).inputPreProcessor(1, new CnnToFeedForwardPreProcessor()).build();
        MultiLayerConfiguration conf2 = conf.clone();
        Assert.assertEquals(conf, conf2);
        Assert.assertNotSame(conf, conf2);
        Assert.assertNotSame(conf.getConfs(), conf2.getConfs());
        for (int i = 0; i < (conf.getConfs().size()); i++) {
            Assert.assertNotSame(conf.getConf(i), conf2.getConf(i));
        }
        Assert.assertNotSame(conf.getInputPreProcessors(), conf2.getInputPreProcessors());
        for (Integer layer : conf.getInputPreProcessors().keySet()) {
            Assert.assertNotSame(conf.getInputPreProcess(layer), conf2.getInputPreProcess(layer));
        }
    }

    @Test
    public void testRandomWeightInit() {
        MultiLayerNetwork model1 = new MultiLayerNetwork(MultiLayerNeuralNetConfigurationTest.getConf());
        model1.init();
        Nd4j.getRandom().setSeed(12345L);
        MultiLayerNetwork model2 = new MultiLayerNetwork(MultiLayerNeuralNetConfigurationTest.getConf());
        model2.init();
        float[] p1 = model1.params().data().asFloat();
        float[] p2 = model2.params().data().asFloat();
        System.out.println(Arrays.toString(p1));
        System.out.println(Arrays.toString(p2));
        Assert.assertArrayEquals(p1, p2, 0.0F);
    }

    @Test
    public void testTrainingListener() {
        MultiLayerNetwork model1 = new MultiLayerNetwork(MultiLayerNeuralNetConfigurationTest.getConf());
        model1.init();
        model1.addListeners(new ScoreIterationListener(1));
        MultiLayerNetwork model2 = new MultiLayerNetwork(MultiLayerNeuralNetConfigurationTest.getConf());
        model2.addListeners(new ScoreIterationListener(1));
        model2.init();
        Layer[] l1 = model1.getLayers();
        for (int i = 0; i < (l1.length); i++)
            Assert.assertTrue((((l1[i].getListeners()) != null) && ((l1[i].getListeners().size()) == 1)));

        Layer[] l2 = model2.getLayers();
        for (int i = 0; i < (l2.length); i++)
            Assert.assertTrue((((l2[i].getListeners()) != null) && ((l2[i].getListeners().size()) == 1)));

    }

    @Test
    public void testInvalidConfig() {
        try {
            MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).list().build();
            MultiLayerNetwork net = new MultiLayerNetwork(conf);
            net.init();
            Assert.fail("No exception thrown for invalid configuration");
        } catch (IllegalStateException e) {
            // OK
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail("Unexpected exception thrown for invalid config");
        }
        try {
            MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).list().layer(1, build()).layer(2, build()).build();
            MultiLayerNetwork net = new MultiLayerNetwork(conf);
            net.init();
            Assert.fail("No exception thrown for invalid configuration");
        } catch (IllegalStateException e) {
            // OK
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail("Unexpected exception thrown for invalid config");
        }
        try {
            MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).list().layer(0, build()).layer(2, build()).build();
            MultiLayerNetwork net = new MultiLayerNetwork(conf);
            net.init();
            Assert.fail("No exception thrown for invalid configuration");
        } catch (IllegalStateException e) {
            // OK
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail("Unexpected exception thrown for invalid config");
        }
    }

    @Test
    public void testListOverloads() {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).list().layer(0, build()).layer(1, build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        DenseLayer dl = ((DenseLayer) (conf.getConf(0).getLayer()));
        Assert.assertEquals(3, dl.getNIn());
        Assert.assertEquals(4, dl.getNOut());
        OutputLayer ol = ((OutputLayer) (conf.getConf(1).getLayer()));
        Assert.assertEquals(4, ol.getNIn());
        Assert.assertEquals(5, ol.getNOut());
        MultiLayerConfiguration conf2 = new NeuralNetConfiguration.Builder().seed(12345).list().layer(0, build()).layer(1, build()).build();
        MultiLayerNetwork net2 = new MultiLayerNetwork(conf2);
        net2.init();
        MultiLayerConfiguration conf3 = new NeuralNetConfiguration.Builder().seed(12345).list(build(), build()).build();
        MultiLayerNetwork net3 = new MultiLayerNetwork(conf3);
        net3.init();
        Assert.assertEquals(conf, conf2);
        Assert.assertEquals(conf, conf3);
    }

    @Test
    public void testBiasLr() {
        // setup the network
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).updater(new org.nd4j.linalg.learning.config.Adam(0.01)).biasUpdater(new org.nd4j.linalg.learning.config.Adam(0.5)).list().layer(0, build()).layer(1, build()).layer(2, build()).layer(3, build()).setInputType(InputType.convolutional(28, 28, 1)).build();
        org.deeplearning4j.nn.conf.layers.BaseLayer l0 = ((BaseLayer) (conf.getConf(0).getLayer()));
        org.deeplearning4j.nn.conf.layers.BaseLayer l1 = ((BaseLayer) (conf.getConf(1).getLayer()));
        org.deeplearning4j.nn.conf.layers.BaseLayer l2 = ((BaseLayer) (conf.getConf(2).getLayer()));
        org.deeplearning4j.nn.conf.layers.BaseLayer l3 = ((BaseLayer) (conf.getConf(3).getLayer()));
        Assert.assertEquals(0.5, getLearningRate(), 1.0E-6);
        Assert.assertEquals(0.01, getLearningRate(), 1.0E-6);
        Assert.assertEquals(0.5, getLearningRate(), 1.0E-6);
        Assert.assertEquals(0.01, getLearningRate(), 1.0E-6);
        Assert.assertEquals(0.5, getLearningRate(), 1.0E-6);
        Assert.assertEquals(0.01, getLearningRate(), 1.0E-6);
        Assert.assertEquals(0.5, getLearningRate(), 1.0E-6);
        Assert.assertEquals(0.01, getLearningRate(), 1.0E-6);
    }

    @Test
    public void testInvalidOutputLayer() {
        /* Test case (invalid configs)
        1. nOut=1 + softmax
        2. mcxent + tanh
        3. xent + softmax
        4. xent + relu
        5. mcxent + sigmoid
         */
        LossFunctions[] lf = new LossFunctions.LossFunction[]{ LossFunction.MCXENT, LossFunction.MCXENT, LossFunction.XENT, LossFunction.XENT, LossFunction.MCXENT };
        int[] nOut = new int[]{ 1, 3, 3, 3, 3 };
        Activation[] activations = new Activation[]{ Activation.SOFTMAX, Activation.TANH, Activation.SOFTMAX, Activation.RELU, Activation.SIGMOID };
        for (int i = 0; i < (lf.length); i++) {
            for (boolean lossLayer : new boolean[]{ false, true }) {
                for (boolean validate : new boolean[]{ true, false }) {
                    String s = (((((("nOut=" + (nOut[i])) + ",lossFn=") + (lf[i])) + ",lossLayer=") + lossLayer) + ",validate=") + validate;
                    if (((nOut[i]) == 1) && lossLayer)
                        continue;
                    // nOuts are not availabel in loss layer, can't expect it to detect this case

                    try {
                        build();
                        if (validate) {
                            Assert.fail(("Expected exception: " + s));
                        }
                    } catch (DL4JInvalidConfigException e) {
                        if (validate) {
                            Assert.assertTrue(s, e.getMessage().toLowerCase().contains("invalid output"));
                        } else {
                            Assert.fail("Validation should not be enabled");
                        }
                    }
                }
            }
        }
    }
}

