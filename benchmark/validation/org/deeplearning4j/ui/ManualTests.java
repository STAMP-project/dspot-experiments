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
package org.deeplearning4j.ui;


import Activation.IDENTITY;
import Activation.RELU;
import Activation.SOFTMAX;
import DataType.DOUBLE;
import GradientNormalization.RenormalizeL2PerLayer;
import MultiLayerConfiguration.Builder;
import WeightInit.XAVIER;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.apache.commons.io.IOUtils;
import org.datavec.image.loader.LFWLoader;
import org.deeplearning4j.datasets.iterator.impl.LFWDataSetIterator;
import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.weightnoise.DropConnect;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.plot.BarnesHutTsne;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.weights.ConvolutionalIterationListener;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nd4j.linalg.api.buffer.util.DataTypeUtil;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.io.ClassPathResource;
import org.nd4j.linalg.learning.config.AdaGrad;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test environment for building/debugging UI.
 *
 * Please, do NOT remove @Ignore annotation
 *
 * @author raver119@gmail.com
 */
@Ignore
public class ManualTests {
    private static Logger log = LoggerFactory.getLogger(ManualTests.class);

    @Test
    public void testLaunch() throws Exception {
        // UiServer server = UiServer.getInstance();
        // 
        // System.out.println("http://localhost:" + server.getPort()+ "/");
        Thread.sleep(10000000000L);
        new ScoreIterationListener(100);
        Assert.fail("not implemneted");
    }

    @Test
    public void testTsne() throws Exception {
        DataTypeUtil.setDTypeForContext(DOUBLE);
        Nd4j.getRandom().setSeed(123);
        BarnesHutTsne b = new BarnesHutTsne.Builder().stopLyingIteration(10).setMaxIter(10).theta(0.5).learningRate(500).useAdaGrad(true).build();
        ClassPathResource resource = new ClassPathResource("/mnist2500_X.txt");
        File f = resource.getTempFileFromArchive();
        INDArray data = Nd4j.readNumpy(f.getAbsolutePath(), "   ").get(NDArrayIndex.interval(0, 100), NDArrayIndex.interval(0, 784));
        ClassPathResource labels = new ClassPathResource("mnist2500_labels.txt");
        List<String> labelsList = IOUtils.readLines(labels.getInputStream()).subList(0, 100);
        b.fit(data);
        File save = new File(System.getProperty("java.io.tmpdir"), ("labels-" + (UUID.randomUUID().toString())));
        System.out.println(("Saved to " + (save.getAbsolutePath())));
        save.deleteOnExit();
        b.saveAsFile(labelsList, save.getAbsolutePath());
        INDArray output = b.getData();
        System.out.println("Coordinates");
        UIServer server = UIServer.getInstance();
        Thread.sleep(10000000000L);
    }

    /**
     * This test is for manual execution only, since it's here just to get working CNN and visualize it's layers
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testCNNActivationsVisualization() throws Exception {
        final int numRows = 40;
        final int numColumns = 40;
        int nChannels = 3;
        int outputNum = LFWLoader.NUM_LABELS;
        int numSamples = LFWLoader.NUM_IMAGES;
        boolean useSubset = false;
        int batchSize = 200;// numSamples/10;

        int iterations = 5;
        int splitTrainNum = ((int) (batchSize * 0.8));
        int seed = 123;
        int listenerFreq = iterations / 5;
        DataSet lfwNext;
        SplitTestAndTrain trainTest;
        DataSet trainInput;
        List<INDArray> testInput = new ArrayList<>();
        List<INDArray> testLabels = new ArrayList<>();
        ManualTests.log.info("Load data....");
        DataSetIterator lfw = new LFWDataSetIterator(batchSize, numSamples, new int[]{ numRows, numColumns, nChannels }, outputNum, useSubset, true, 1.0, new Random(seed));
        ManualTests.log.info("Build model....");
        MultiLayerConfiguration.Builder builder = new NeuralNetConfiguration.Builder().seed(seed).activation(RELU).weightInit(XAVIER).gradientNormalization(RenormalizeL2PerLayer).updater(new AdaGrad(0.01)).weightNoise(new DropConnect(0.5)).list().layer(0, nOut(20).build()).layer(1, name("pool1").build()).layer(2, nOut(40).build()).layer(3, name("pool2").build()).layer(4, nOut(60).build()).layer(5, name("pool3").build()).layer(6, nOut(80).build()).layer(7, nOut(160).dropOut(0.5).build()).layer(8, nOut(outputNum).activation(SOFTMAX).build()).setInputType(InputType.convolutional(numRows, numColumns, nChannels));
        MultiLayerNetwork model = new MultiLayerNetwork(builder.build());
        model.init();
        ManualTests.log.info("Train model....");
        model.setListeners(new ScoreIterationListener(listenerFreq), new ConvolutionalIterationListener(listenerFreq));
        while (lfw.hasNext()) {
            lfwNext = lfw.next();
            lfwNext.scale();
            trainTest = lfwNext.splitTestAndTrain(splitTrainNum, new Random(seed));// train set that is the result

            trainInput = trainTest.getTrain();// get feature matrix and labels for training

            testInput.add(trainTest.getTest().getFeatures());
            testLabels.add(trainTest.getTest().getLabels());
            model.fit(trainInput);
        } 
        ManualTests.log.info("Evaluate model....");
        Evaluation eval = new Evaluation(lfw.getLabels());
        for (int i = 0; i < (testInput.size()); i++) {
            INDArray output = model.output(testInput.get(i));
            eval.eval(testLabels.get(i), output);
        }
        INDArray output = model.output(testInput.get(0));
        eval.eval(testLabels.get(0), output);
        ManualTests.log.info(eval.stats());
        ManualTests.log.info("****************Example finished********************");
    }

    @Test
    public void testWord2VecPlot() throws Exception {
        File inputFile = new ClassPathResource("/big/raw_sentences.txt").getFile();
        SentenceIterator iter = new BasicLineIterator(inputFile.getAbsolutePath());
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        Word2Vec vec = new Word2Vec.Builder().minWordFrequency(5).iterations(2).batchSize(1000).learningRate(0.025).layerSize(100).seed(42).sampling(0).negativeSample(0).windowSize(5).modelUtils(new org.deeplearning4j.models.embeddings.reader.impl.BasicModelUtils<org.deeplearning4j.models.word2vec.VocabWord>()).useAdaGrad(false).iterate(iter).workers(10).tokenizerFactory(t).build();
        vec.fit();
        // UiConnectionInfo connectionInfo = UiServer.getInstance().getConnectionInfo();
        // vec.getLookupTable().plotVocab(100, connectionInfo);
        Thread.sleep(10000000000L);
        Assert.fail("Not implemented");
    }

    @Test
    public void testImage() throws Exception {
        INDArray array = Nd4j.create(11, 13);
        for (int i = 0; i < (array.rows()); i++) {
            array.putRow(i, Nd4j.create(new double[]{ 0.0F, 0.1F, 0.2F, 0.3F, 0.4F, 0.5F, 0.6F, 0.7F, 0.8F, 0.9F, 1.0F, 1.2F, 1.3F }));
        }
        writeImage(array, new File("test.png"));
    }

    @Test
    public void testCNNActivations2() throws Exception {
        int nChannels = 1;
        int outputNum = 10;
        int batchSize = 64;
        int nEpochs = 10;
        int seed = 123;
        ManualTests.log.info("Load data....");
        DataSetIterator mnistTrain = new MnistDataSetIterator(batchSize, true, 12345);
        DataSetIterator mnistTest = new MnistDataSetIterator(batchSize, false, 12345);
        ManualTests.log.info("Build model....");
        MultiLayerConfiguration.Builder builder = new NeuralNetConfiguration.Builder().seed(seed).l2(5.0E-4).weightInit(XAVIER).updater(new Nesterovs(0.01, 0.9)).list().layer(0, nOut(20).activation(IDENTITY).build()).layer(1, kernelSize(2, 2).stride(2, 2).build()).layer(2, nOut(50).activation(IDENTITY).build()).layer(3, kernelSize(2, 2).stride(2, 2).build()).layer(4, nOut(500).build()).layer(5, nOut(outputNum).activation(SOFTMAX).build()).setInputType(InputType.convolutional(28, 28, nChannels));
        MultiLayerConfiguration conf = builder.build();
        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
        /* ParallelWrapper wrapper = new ParallelWrapper.Builder(model)
        .averagingFrequency(1)
        .prefetchBuffer(12)
        .workers(2)
        .reportScoreAfterAveraging(false)
        .useLegacyAveraging(false)
        .build();
         */
        ManualTests.log.info("Train model....");
        model.setListeners(new ConvolutionalIterationListener(1));
        // ((NativeOpExecutioner) Nd4j.getExecutioner()).getLoop().setOmpNumThreads(8);
        long timeX = System.currentTimeMillis();
        // nEpochs = 2;
        for (int i = 0; i < nEpochs; i++) {
            long time1 = System.currentTimeMillis();
            model.fit(mnistTrain);
            // wrapper.fit(mnistTrain);
            long time2 = System.currentTimeMillis();
            ManualTests.log.info("*** Completed epoch {}, Time elapsed: {} ***", i, (time2 - time1));
        }
        long timeY = System.currentTimeMillis();
        ManualTests.log.info("Evaluate model....");
        Evaluation eval = new Evaluation(outputNum);
        while (mnistTest.hasNext()) {
            DataSet ds = mnistTest.next();
            INDArray output = model.output(ds.getFeatures(), false);
            eval.eval(ds.getLabels(), output);
        } 
        ManualTests.log.info(eval.stats());
        mnistTest.reset();
        ManualTests.log.info("****************Example finished********************");
    }

    @Test
    public void testCNNActivationsFrozen() throws Exception {
        int nChannels = 1;
        int outputNum = 10;
        int batchSize = 64;
        int nEpochs = 10;
        int seed = 123;
        ManualTests.log.info("Load data....");
        DataSetIterator mnistTrain = new MnistDataSetIterator(batchSize, true, 12345);
        ManualTests.log.info("Build model....");
        MultiLayerConfiguration.Builder builder = new NeuralNetConfiguration.Builder().seed(seed).l2(5.0E-4).weightInit(XAVIER).updater(new Nesterovs(0.01, 0.9)).list().layer(0, new org.deeplearning4j.nn.conf.layers.misc.FrozenLayer(nOut(20).activation(IDENTITY).build())).layer(1, new org.deeplearning4j.nn.conf.layers.misc.FrozenLayer(kernelSize(2, 2).stride(2, 2).build())).layer(2, new org.deeplearning4j.nn.conf.layers.misc.FrozenLayer(nOut(500).build())).layer(3, nOut(outputNum).activation(SOFTMAX).build()).setInputType(InputType.convolutionalFlat(28, 28, nChannels));
        MultiLayerConfiguration conf = builder.build();
        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
        ManualTests.log.info("Train model....");
        model.setListeners(new ConvolutionalIterationListener(1));
        for (int i = 0; i < nEpochs; i++) {
            model.fit(mnistTrain);
        }
    }
}

