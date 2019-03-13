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
package org.deeplearning4j.gradientcheck;


import Activation.IDENTITY;
import ConvolutionMode.Same;
import DataType.DOUBLE;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.image.recordreader.objdetect.impl.VocLabelProvider;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.TestUtils;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.distribution.GaussianDistribution;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.conf.layers.objdetect.Yolo2OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.io.ClassPathResource;
import org.nd4j.linalg.learning.config.NoOp;


/**
 *
 *
 * @author Alex Black
 */
public class YoloGradientCheckTests extends BaseDL4JTest {
    private static final boolean PRINT_RESULTS = true;

    private static final boolean RETURN_ON_FIRST_FAILURE = false;

    private static final double DEFAULT_EPS = 1.0E-6;

    private static final double DEFAULT_MAX_REL_ERROR = 0.001;

    private static final double DEFAULT_MIN_ABS_ERROR = 1.0E-8;

    static {
        Nd4j.setDataType(DOUBLE);
    }

    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

    @Test
    public void testYoloOutputLayer() {
        int depthIn = 2;
        int[] minibatchSizes = new int[]{ 1, 3 };
        int[] widths = new int[]{ 4, 7 };
        int[] heights = new int[]{ 4, 5 };
        int c = 3;
        int b = 3;
        int yoloDepth = b * (5 + c);
        Activation a = Activation.TANH;
        Nd4j.getRandom().setSeed(1234567);
        double[] l1 = new double[]{ 0.0, 0.3 };
        double[] l2 = new double[]{ 0.0, 0.4 };
        for (int wh = 0; wh < (widths.length); wh++) {
            int w = widths[wh];
            int h = heights[wh];
            Nd4j.getRandom().setSeed(12345);
            INDArray bbPrior = Nd4j.rand(b, 2).muliRowVector(Nd4j.create(new double[]{ w, h })).addi(0.1);
            for (int mb : minibatchSizes) {
                for (int i = 0; i < (l1.length); i++) {
                    Nd4j.getRandom().setSeed(12345);
                    INDArray input = Nd4j.rand(new int[]{ mb, depthIn, h, w });
                    INDArray labels = YoloGradientCheckTests.yoloLabels(mb, c, h, w);
                    MultiLayerConfiguration conf = // output: (5-2+0)/1+1 = 4
                    new NeuralNetConfiguration.Builder().seed(12345).updater(new NoOp()).activation(a).l1(l1[i]).l2(l2[i]).convolutionMode(Same).list().layer(new ConvolutionLayer.Builder().kernelSize(2, 2).stride(1, 1).nIn(depthIn).nOut(yoloDepth).build()).layer(new Yolo2OutputLayer.Builder().boundingBoxPriors(bbPrior).build()).build();
                    MultiLayerNetwork net = new MultiLayerNetwork(conf);
                    net.init();
                    String msg = (((((((("testYoloOutputLayer() - minibatch = " + mb) + ", w=") + w) + ", h=") + h) + ", l1=") + (l1[i])) + ", l2=") + (l2[i]);
                    System.out.println(msg);
                    boolean gradOK = GradientCheckUtil.checkGradients(net, YoloGradientCheckTests.DEFAULT_EPS, YoloGradientCheckTests.DEFAULT_MAX_REL_ERROR, YoloGradientCheckTests.DEFAULT_MIN_ABS_ERROR, YoloGradientCheckTests.PRINT_RESULTS, YoloGradientCheckTests.RETURN_ON_FIRST_FAILURE, input, labels);
                    Assert.assertTrue(msg, gradOK);
                    TestUtils.testModelSerialization(net);
                }
            }
        }
    }

    @Test
    public void yoloGradientCheckRealData() throws Exception {
        Nd4j.getRandom().setSeed(12345);
        InputStream is1 = new ClassPathResource("yolo/VOC_TwoImage/JPEGImages/2007_009346.jpg").getInputStream();
        InputStream is2 = new ClassPathResource("yolo/VOC_TwoImage/Annotations/2007_009346.xml").getInputStream();
        InputStream is3 = new ClassPathResource("yolo/VOC_TwoImage/JPEGImages/2008_003344.jpg").getInputStream();
        InputStream is4 = new ClassPathResource("yolo/VOC_TwoImage/Annotations/2008_003344.xml").getInputStream();
        File dir = testDir.newFolder("testYoloOverfitting");
        File jpg = new File(dir, "JPEGImages");
        File annot = new File(dir, "Annotations");
        jpg.mkdirs();
        annot.mkdirs();
        File imgOut = new File(jpg, "2007_009346.jpg");
        File annotationOut = new File(annot, "2007_009346.xml");
        try (FileOutputStream fos = new FileOutputStream(imgOut)) {
            IOUtils.copy(is1, fos);
        } finally {
            is1.close();
        }
        try (FileOutputStream fos = new FileOutputStream(annotationOut)) {
            IOUtils.copy(is2, fos);
        } finally {
            is2.close();
        }
        imgOut = new File(jpg, "2008_003344.jpg");
        annotationOut = new File(annot, "2008_003344.xml");
        try (FileOutputStream fos = new FileOutputStream(imgOut)) {
            IOUtils.copy(is3, fos);
        } finally {
            is3.close();
        }
        try (FileOutputStream fos = new FileOutputStream(annotationOut)) {
            IOUtils.copy(is4, fos);
        } finally {
            is4.close();
        }
        INDArray bbPriors = Nd4j.create(new double[][]{ new double[]{ 3, 3 }, new double[]{ 3, 5 }, new double[]{ 5, 7 } });
        // 2x downsampling to 13x13 = 26x26 input images
        // Required depth at output layer: 5B+C, with B=3, C=20 object classes, for VOC
        VocLabelProvider lp = new VocLabelProvider(dir.getPath());
        int h = 26;
        int w = 26;
        int c = 3;
        RecordReader rr = new org.datavec.image.recordreader.objdetect.ObjectDetectionRecordReader(h, w, c, 13, 13, lp);
        rr.initialize(new FileSplit(jpg));
        int nClasses = rr.getLabels().size();
        long depthOut = (bbPriors.size(0)) * (5 + nClasses);
        DataSetIterator iter = new org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator(rr, 2, 1, 1, true);
        iter.setPreProcessor(new ImagePreProcessingScaler());
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().convolutionMode(Same).updater(new NoOp()).dist(new GaussianDistribution(0, 0.1)).seed(12345).list().layer(new ConvolutionLayer.Builder().kernelSize(3, 3).stride(1, 1).nOut(4).build()).layer(new SubsamplingLayer.Builder().kernelSize(2, 2).stride(2, 2).build()).layer(new ConvolutionLayer.Builder().activation(IDENTITY).kernelSize(3, 3).stride(1, 1).nOut(depthOut).build()).layer(new Yolo2OutputLayer.Builder().boundingBoxPriors(bbPriors).build()).setInputType(InputType.convolutional(h, w, c)).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        DataSet ds = iter.next();
        INDArray f = ds.getFeatures();
        INDArray l = ds.getLabels();
        boolean ok = GradientCheckUtil.checkGradients(net, YoloGradientCheckTests.DEFAULT_EPS, YoloGradientCheckTests.DEFAULT_MAX_REL_ERROR, YoloGradientCheckTests.DEFAULT_MIN_ABS_ERROR, YoloGradientCheckTests.PRINT_RESULTS, YoloGradientCheckTests.RETURN_ON_FIRST_FAILURE, f, l);
        Assert.assertTrue(ok);
        TestUtils.testModelSerialization(net);
    }
}

