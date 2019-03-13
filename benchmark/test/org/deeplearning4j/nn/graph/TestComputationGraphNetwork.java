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


import Activation.IDENTITY;
import Activation.RELU;
import Activation.SIGMOID;
import Activation.SOFTMAX;
import Activation.TANH;
import BackpropType.Standard;
import ComputationGraphConfiguration.GraphBuilder;
import LossFunctions.LossFunction;
import LossFunctions.LossFunction.KL_DIVERGENCE;
import NeuralNetConfiguration.Builder;
import OpExecutioner.ProfilingMode;
import OpExecutioner.ProfilingMode.DISABLED;
import OpExecutioner.ProfilingMode.NAN_PANIC;
import OpExecutioner.ProfilingMode.SCOPE_PANIC;
import OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT;
import WeightInit.XAVIER;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.TestUtils;
import org.deeplearning4j.datasets.datavec.RecordReaderMultiDataSetIterator;
import org.deeplearning4j.datasets.iterator.impl.IrisDataSetIterator;
import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.exception.DL4JException;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.distribution.UniformDistribution;
import org.deeplearning4j.nn.conf.dropout.IDropout;
import org.deeplearning4j.nn.conf.graph.rnn.DuplicateToTimeSeriesVertex;
import org.deeplearning4j.nn.conf.graph.rnn.LastTimeStepVertex;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.variational.VariationalAutoencoder;
import org.deeplearning4j.nn.conf.weightnoise.DropConnect;
import org.deeplearning4j.nn.gradient.DefaultGradient;
import org.deeplearning4j.nn.gradient.Gradient;
import org.deeplearning4j.nn.graph.util.GraphIndices;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.multilayer.MultiLayerTest;
import org.deeplearning4j.nn.workspace.LayerWorkspaceMgr;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.MultiDataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.iterator.MultiDataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.io.ClassPathResource;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.primitives.Pair;


@Slf4j
public class TestComputationGraphNetwork extends BaseDL4JTest {
    private static ProfilingMode origMode;

    @Test
    public void testFeedForwardToLayer() {
        ComputationGraphConfiguration configuration = TestComputationGraphNetwork.getIrisGraphConfiguration();
        ComputationGraph graph = new ComputationGraph(configuration);
        graph.init();
        MultiLayerConfiguration mlc = TestComputationGraphNetwork.getIrisMLNConfiguration();
        MultiLayerNetwork net = new MultiLayerNetwork(mlc);
        net.init();
        DataSetIterator iris = new IrisDataSetIterator(150, 150);
        DataSet ds = iris.next();
        graph.setInput(0, ds.getFeatures());
        net.setParams(graph.params());
        Map<String, INDArray> activations = graph.feedForward(false);
        List<INDArray> feedForward = net.feedForward(ds.getFeatures());
        Assert.assertEquals(activations.size(), feedForward.size());
        Assert.assertEquals(activations.get("outputLayer"), feedForward.get(((feedForward.size()) - 1)));
        Map<String, INDArray> graphForward = graph.feedForward(ds.getFeatures(), 0, false);
        List<INDArray> networkForward = net.feedForwardToLayer(0, ds.getFeatures(), false);
        Assert.assertEquals(graphForward.get("firstLayer"), networkForward.get(1));
    }

    @Test
    public void testConfigurationBasic() {
        ComputationGraphConfiguration configuration = TestComputationGraphNetwork.getIrisGraphConfiguration();
        ComputationGraph graph = new ComputationGraph(configuration);
        graph.init();
        // Get topological sort order
        int[] order = graph.topologicalSortOrder();
        int[] expOrder = new int[]{ 0, 1, 2 };
        Assert.assertArrayEquals(expOrder, order);// Only one valid order: 0 (input) -> 1 (firstlayer) -> 2 (outputlayer)

        INDArray params = graph.params();
        Assert.assertNotNull(params);
        int nParams = TestComputationGraphNetwork.getNumParams();
        Assert.assertEquals(nParams, params.length());
        INDArray arr = Nd4j.linspace(0, nParams, nParams);
        Assert.assertEquals(nParams, arr.length());
        graph.setParams(arr);
        params = graph.params();
        Assert.assertEquals(arr, params);
        // Number of inputs and outputs:
        Assert.assertEquals(1, graph.getNumInputArrays());
        Assert.assertEquals(1, graph.getNumOutputArrays());
    }

    @Test
    public void testForwardBasicIris() {
        ComputationGraphConfiguration configuration = TestComputationGraphNetwork.getIrisGraphConfiguration();
        ComputationGraph graph = new ComputationGraph(configuration);
        graph.init();
        MultiLayerConfiguration mlc = TestComputationGraphNetwork.getIrisMLNConfiguration();
        MultiLayerNetwork net = new MultiLayerNetwork(mlc);
        net.init();
        DataSetIterator iris = new IrisDataSetIterator(150, 150);
        DataSet ds = iris.next();
        graph.setInput(0, ds.getFeatures());
        Map<String, INDArray> activations = graph.feedForward(false);
        Assert.assertEquals(3, activations.size());// 2 layers + 1 input node

        Assert.assertTrue(activations.containsKey("input"));
        Assert.assertTrue(activations.containsKey("firstLayer"));
        Assert.assertTrue(activations.containsKey("outputLayer"));
        // Now: set parameters of both networks to be identical. Then feedforward, and check we get the same outputs
        Nd4j.getRandom().setSeed(12345);
        int nParams = TestComputationGraphNetwork.getNumParams();
        INDArray params = Nd4j.rand(1, nParams);
        graph.setParams(params.dup());
        net.setParams(params.dup());
        List<INDArray> mlnAct = net.feedForward(ds.getFeatures(), false);
        activations = graph.feedForward(ds.getFeatures(), false);
        Assert.assertEquals(mlnAct.get(0), activations.get("input"));
        Assert.assertEquals(mlnAct.get(1), activations.get("firstLayer"));
        Assert.assertEquals(mlnAct.get(2), activations.get("outputLayer"));
    }

    @Test
    public void testBackwardIrisBasic() {
        ComputationGraphConfiguration configuration = TestComputationGraphNetwork.getIrisGraphConfiguration();
        ComputationGraph graph = new ComputationGraph(configuration);
        graph.init();
        MultiLayerConfiguration mlc = TestComputationGraphNetwork.getIrisMLNConfiguration();
        MultiLayerNetwork net = new MultiLayerNetwork(mlc);
        net.init();
        DataSetIterator iris = new IrisDataSetIterator(150, 150);
        DataSet ds = iris.next();
        // Now: set parameters of both networks to be identical. Then feedforward, and check we get the same outputs
        Nd4j.getRandom().setSeed(12345);
        int nParams = ((4 * 5) + 5) + ((5 * 3) + 3);
        INDArray params = Nd4j.rand(1, nParams);
        graph.setParams(params.dup());
        net.setParams(params.dup());
        INDArray input = ds.getFeatures();
        INDArray labels = ds.getLabels();
        graph.setInput(0, input.dup());
        graph.setLabel(0, labels.dup());
        net.setInput(input.dup());
        net.setLabels(labels.dup());
        // Compute gradients
        net.computeGradientAndScore();
        Pair<Gradient, Double> netGradScore = net.gradientAndScore();
        graph.computeGradientAndScore();
        Pair<Gradient, Double> graphGradScore = graph.gradientAndScore();
        Assert.assertEquals(netGradScore.getSecond(), graphGradScore.getSecond(), 0.001);
        // Compare gradients
        Gradient netGrad = netGradScore.getFirst();
        Gradient graphGrad = graphGradScore.getFirst();
        Assert.assertNotNull(graphGrad);
        Assert.assertEquals(netGrad.gradientForVariable().size(), graphGrad.gradientForVariable().size());
        Assert.assertEquals(netGrad.getGradientFor("0_W"), graphGrad.getGradientFor("firstLayer_W"));
        Assert.assertEquals(netGrad.getGradientFor("0_b"), graphGrad.getGradientFor("firstLayer_b"));
        Assert.assertEquals(netGrad.getGradientFor("1_W"), graphGrad.getGradientFor("outputLayer_W"));
        Assert.assertEquals(netGrad.getGradientFor("1_b"), graphGrad.getGradientFor("outputLayer_b"));
    }

    @Test
    public void testIrisFit() {
        ComputationGraphConfiguration configuration = TestComputationGraphNetwork.getIrisGraphConfiguration();
        ComputationGraph graph = new ComputationGraph(configuration);
        graph.init();
        MultiLayerConfiguration mlnConfig = TestComputationGraphNetwork.getIrisMLNConfiguration();
        MultiLayerNetwork net = new MultiLayerNetwork(mlnConfig);
        net.init();
        Nd4j.getRandom().setSeed(12345);
        int nParams = TestComputationGraphNetwork.getNumParams();
        INDArray params = Nd4j.rand(1, nParams);
        graph.setParams(params.dup());
        net.setParams(params.dup());
        DataSetIterator iris = new IrisDataSetIterator(75, 150);
        net.fit(iris);
        iris.reset();
        graph.fit(iris);
        // Check that parameters are equal for both models after fitting:
        INDArray paramsMLN = net.params();
        INDArray paramsGraph = graph.params();
        Assert.assertNotEquals(params, paramsGraph);
        Assert.assertEquals(paramsMLN, paramsGraph);
    }

    @Test
    public void testIrisFitMultiDataSetIterator() throws Exception {
        RecordReader rr = new CSVRecordReader(0, ',');
        rr.initialize(new org.datavec.api.split.FileSplit(new ClassPathResource("iris.txt").getTempFileFromArchive()));
        MultiDataSetIterator iter = new RecordReaderMultiDataSetIterator.Builder(10).addReader("iris", rr).addInput("iris", 0, 3).addOutputOneHot("iris", 4, 3).build();
        ComputationGraphConfiguration config = new NeuralNetConfiguration.Builder().updater(new Sgd(0.1)).graphBuilder().addInputs("in").addLayer("dense", build(), "in").addLayer("out", build(), "dense").setOutputs("out").build();
        ComputationGraph cg = new ComputationGraph(config);
        cg.init();
        cg.fit(iter);
        rr.reset();
        iter = build();
        while (iter.hasNext()) {
            cg.fit(iter.next());
        } 
    }

    @Test
    public void testCloning() {
        Nd4j.getRandom().setSeed(12345);
        ComputationGraphConfiguration conf = TestComputationGraphNetwork.getIrisGraphConfiguration();
        ComputationGraph graph = new ComputationGraph(conf);
        graph.init();
        ComputationGraph g2 = graph.clone();
        DataSetIterator iris = new IrisDataSetIterator(150, 150);
        INDArray in = iris.next().getFeatures();
        Map<String, INDArray> activations = graph.feedForward(in, false);
        Map<String, INDArray> activations2 = g2.feedForward(in, false);
        Assert.assertEquals(activations, activations2);
    }

    @Test
    public void testScoringDataSet() {
        ComputationGraphConfiguration configuration = TestComputationGraphNetwork.getIrisGraphConfiguration();
        ComputationGraph graph = new ComputationGraph(configuration);
        graph.init();
        MultiLayerConfiguration mlc = TestComputationGraphNetwork.getIrisMLNConfiguration();
        MultiLayerNetwork net = new MultiLayerNetwork(mlc);
        net.init();
        DataSetIterator iris = new IrisDataSetIterator(150, 150);
        DataSet ds = iris.next();
        // Now: set parameters of both networks to be identical. Then feedforward, and check we get the same score
        Nd4j.getRandom().setSeed(12345);
        int nParams = TestComputationGraphNetwork.getNumParams();
        INDArray params = Nd4j.rand(1, nParams);
        graph.setParams(params.dup());
        net.setParams(params.dup());
        double scoreMLN = net.score(ds, false);
        double scoreCG = graph.score(ds, false);
        Assert.assertEquals(scoreMLN, scoreCG, 1.0E-4);
    }

    @Test
    public void testPreprocessorAddition() {
        // Also check that nIns are set automatically
        // First: check FF -> RNN
        ComputationGraphConfiguration conf1 = new NeuralNetConfiguration.Builder().graphBuilder().addInputs("in").setInputTypes(InputType.feedForward(5)).addLayer("rnn", build(), "in").addLayer("out", build(), "rnn").setOutputs("out").build();
        Assert.assertEquals(5, getNIn());
        Assert.assertEquals(5, getNIn());
        LayerVertex lv1 = ((LayerVertex) (conf1.getVertices().get("rnn")));
        Assert.assertTrue(((lv1.getPreProcessor()) instanceof FeedForwardToRnnPreProcessor));
        LayerVertex lv2 = ((LayerVertex) (conf1.getVertices().get("out")));
        Assert.assertNull(lv2.getPreProcessor());
        // Check RNN -> FF -> RNN
        ComputationGraphConfiguration conf2 = new NeuralNetConfiguration.Builder().graphBuilder().addInputs("in").setInputTypes(InputType.recurrent(5)).addLayer("ff", build(), "in").addLayer("out", build(), "ff").setOutputs("out").build();
        Assert.assertEquals(5, getNIn());
        Assert.assertEquals(5, getNIn());
        lv1 = ((LayerVertex) (conf2.getVertices().get("ff")));
        Assert.assertTrue(((lv1.getPreProcessor()) instanceof RnnToFeedForwardPreProcessor));
        lv2 = ((LayerVertex) (conf2.getVertices().get("out")));
        Assert.assertTrue(((lv2.getPreProcessor()) instanceof FeedForwardToRnnPreProcessor));
        // CNN -> Dense
        ComputationGraphConfiguration conf3 = // (14-2+0)/2+1=7
        // (28-2+0)/2+1 = 14
        new NeuralNetConfiguration.Builder().graphBuilder().addInputs("in").setInputTypes(InputType.convolutional(28, 28, 1)).addLayer("cnn", build(), "in").addLayer("pool", build(), "cnn").addLayer("dense", build(), "pool").addLayer("out", build(), "dense").setOutputs("out").build();
        // Check preprocessors:
        lv1 = ((LayerVertex) (conf3.getVertices().get("cnn")));
        Assert.assertNull(lv1.getPreProcessor());// Shouldn't be adding preprocessor here

        lv2 = ((LayerVertex) (conf3.getVertices().get("pool")));
        Assert.assertNull(lv2.getPreProcessor());
        LayerVertex lv3 = ((LayerVertex) (conf3.getVertices().get("dense")));
        Assert.assertTrue(((lv3.getPreProcessor()) instanceof CnnToFeedForwardPreProcessor));
        CnnToFeedForwardPreProcessor proc = ((CnnToFeedForwardPreProcessor) (lv3.getPreProcessor()));
        Assert.assertEquals(3, proc.getNumChannels());
        Assert.assertEquals(7, proc.getInputHeight());
        Assert.assertEquals(7, proc.getInputWidth());
        LayerVertex lv4 = ((LayerVertex) (conf3.getVertices().get("out")));
        Assert.assertNull(lv4.getPreProcessor());
        // Check nIns:
        Assert.assertEquals(((7 * 7) * 3), getNIn());
        // CNN->Dense, RNN->Dense, Dense->RNN
        ComputationGraphConfiguration conf4 = // (14-2+0)/2+1=7
        // (28-2+0)/2+1 = 14
        new NeuralNetConfiguration.Builder().graphBuilder().addInputs("inCNN", "inRNN").setInputTypes(InputType.convolutional(28, 28, 1), InputType.recurrent(5)).addLayer("cnn", build(), "inCNN").addLayer("pool", build(), "cnn").addLayer("dense", build(), "pool").addLayer("dense2", build(), "inRNN").addVertex("merge", new MergeVertex(), "dense", "dense2").addLayer("out", build(), "merge").setOutputs("out").build();
        // Check preprocessors:
        lv1 = ((LayerVertex) (conf4.getVertices().get("cnn")));
        Assert.assertNull(lv1.getPreProcessor());// Expect no preprocessor: cnn data -> cnn layer

        lv2 = ((LayerVertex) (conf4.getVertices().get("pool")));
        Assert.assertNull(lv2.getPreProcessor());
        lv3 = ((LayerVertex) (conf4.getVertices().get("dense")));
        Assert.assertTrue(((lv3.getPreProcessor()) instanceof CnnToFeedForwardPreProcessor));
        proc = ((CnnToFeedForwardPreProcessor) (lv3.getPreProcessor()));
        Assert.assertEquals(3, proc.getNumChannels());
        Assert.assertEquals(7, proc.getInputHeight());
        Assert.assertEquals(7, proc.getInputWidth());
        lv4 = ((LayerVertex) (conf4.getVertices().get("dense2")));
        Assert.assertTrue(((lv4.getPreProcessor()) instanceof RnnToFeedForwardPreProcessor));
        LayerVertex lv5 = ((LayerVertex) (conf4.getVertices().get("out")));
        Assert.assertTrue(((lv5.getPreProcessor()) instanceof FeedForwardToRnnPreProcessor));
        // Check nIns:
        Assert.assertEquals(((7 * 7) * 3), getNIn());
        Assert.assertEquals(5, getNIn());
        Assert.assertEquals(20, getNIn());// 10+10 out of the merge vertex -> 20 in to output layer vertex

        // Input to 2 CNN layers:
        ComputationGraphConfiguration conf5 = // .nIn(7 * 7 * 6)
        new NeuralNetConfiguration.Builder().optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).graphBuilder().addInputs("input").setInputTypes(InputType.convolutional(28, 28, 1)).addLayer("cnn_1", build(), "input").addLayer("cnn_2", build(), "input").addLayer("max_1", build(), "cnn_1", "cnn_2").addLayer("output", build(), "max_1").setOutputs("output").build();
        lv1 = ((LayerVertex) (conf5.getVertices().get("cnn_1")));
        Assert.assertNull(lv1.getPreProcessor());// Expect no preprocessor: cnn data -> cnn layer

        lv2 = ((LayerVertex) (conf5.getVertices().get("cnn_2")));
        Assert.assertNull(lv2.getPreProcessor());// Expect no preprocessor: cnn data -> cnn layer

        Assert.assertNull(getPreProcessor());
        lv3 = ((LayerVertex) (conf5.getVertices().get("output")));
        Assert.assertTrue(((lv3.getPreProcessor()) instanceof CnnToFeedForwardPreProcessor));
        CnnToFeedForwardPreProcessor cnnff = ((CnnToFeedForwardPreProcessor) (lv3.getPreProcessor()));
        Assert.assertEquals(6, cnnff.getNumChannels());
        Assert.assertEquals(7, cnnff.getInputHeight());
        Assert.assertEquals(7, cnnff.getInputWidth());
        ComputationGraph graph = new ComputationGraph(conf1);
        graph.init();
        System.out.println(graph.summary());
        System.out.println(graph.summary(InputType.feedForward(5)));
        graph = new ComputationGraph(conf2);
        graph.init();
        System.out.println(graph.summary());
        System.out.println(graph.summary(InputType.recurrent(5)));
        graph = new ComputationGraph(conf3);
        graph.init();
        System.out.println(graph.summary());
        System.out.println(graph.summary(InputType.convolutional(28, 28, 1)));
        graph = new ComputationGraph(conf4);
        graph.init();
        System.out.println(graph.summary());
        System.out.println(graph.summary(InputType.convolutional(28, 28, 1), InputType.recurrent(5)));
        graph = new ComputationGraph(conf5);
        graph.init();
        System.out.println(graph.summary());
        System.out.println(graph.summary(InputType.convolutional(28, 28, 1)));
    }

    @Test
    public void testCompGraphUnderscores() {
        // Problem: underscores in names could be problematic for ComputationGraphUpdater, HistogramIterationListener
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).graphBuilder().addInputs("input").addLayer("first_layer", build(), "input").addLayer("output_layer", build(), "first_layer").setOutputs("output_layer").build();
        ComputationGraph net = new ComputationGraph(conf);
        net.init();
        DataSetIterator iris = new IrisDataSetIterator(10, 150);
        while (iris.hasNext()) {
            net.fit(iris.next());
        } 
    }

    @Test
    public void testPreTraining() {
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).updater(new Sgd(1.0E-6)).l2(2.0E-4).graphBuilder().addInputs("in").addLayer("layer0", build(), "in").addLayer("layer1", build(), "in").addLayer("layer2", build(), "layer1").addLayer("out", build(), "layer0", "layer2").setOutputs("out").build();
        ComputationGraph net = new ComputationGraph(conf);
        net.init();
        net.setListeners(new ScoreIterationListener(1));
        DataSetIterator iter = new IrisDataSetIterator(10, 150);
        net.pretrain(iter);
    }

    @Test
    public void testScoreExamples() {
        Nd4j.getRandom().setSeed(12345);
        int nIn = 5;
        int nOut = 6;
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).l1(0.01).l2(0.01).updater(new Sgd(0.1)).activation(TANH).weightInit(XAVIER).graphBuilder().addInputs("in").addLayer("0", build(), "in").addLayer("1", build(), "0").addLayer("2", build(), "1").setOutputs("2").build();
        ComputationGraphConfiguration confNoReg = new NeuralNetConfiguration.Builder().seed(12345).updater(new Sgd(0.1)).activation(TANH).weightInit(XAVIER).graphBuilder().addInputs("in").addLayer("0", build(), "in").addLayer("1", build(), "0").addLayer("2", build(), "1").setOutputs("2").build();
        ComputationGraph net = new ComputationGraph(conf);
        net.init();
        ComputationGraph netNoReg = new ComputationGraph(confNoReg);
        netNoReg.init();
        netNoReg.setParams(net.params().dup());
        // Score single example, and compare to scoreExamples:
        INDArray input = Nd4j.rand(3, nIn);
        INDArray output = Nd4j.rand(3, nOut);
        DataSet ds = new DataSet(input, output);
        INDArray scoresWithRegularization = net.scoreExamples(ds, true);
        INDArray scoresNoRegularization = net.scoreExamples(ds, false);
        Assert.assertArrayEquals(new long[]{ 3, 1 }, scoresWithRegularization.shape());
        Assert.assertArrayEquals(new long[]{ 3, 1 }, scoresNoRegularization.shape());
        for (int i = 0; i < 3; i++) {
            DataSet singleEx = new DataSet(input.getRow(i), output.getRow(i));
            double score = net.score(singleEx);
            double scoreNoReg = netNoReg.score(singleEx);
            double scoreUsingScoreExamples = scoresWithRegularization.getDouble(i);
            double scoreUsingScoreExamplesNoReg = scoresNoRegularization.getDouble(i);
            Assert.assertEquals(score, scoreUsingScoreExamples, 1.0E-4);
            Assert.assertEquals(scoreNoReg, scoreUsingScoreExamplesNoReg, 1.0E-4);
            Assert.assertTrue((scoreUsingScoreExamples > scoreUsingScoreExamplesNoReg));// Regularization term increases score

            // System.out.println(score + "\t" + scoreUsingScoreExamples + "\t|\t" + scoreNoReg + "\t" + scoreUsingScoreExamplesNoReg);
        }
    }

    @Test
    public void testExternalErrors() {
        // Simple test: same network, but in one case: one less layer (the OutputLayer), where the epsilons are passed in externally
        // instead. Should get identical results
        for (WorkspaceMode ws : WorkspaceMode.values()) {
            log.info(("Workspace mode: " + ws));
            Nd4j.getRandom().setSeed(12345);
            INDArray inData = Nd4j.rand(3, 10);
            INDArray outData = Nd4j.rand(3, 10);
            Nd4j.getRandom().setSeed(12345);
            ComputationGraphConfiguration standard = new NeuralNetConfiguration.Builder().updater(new Sgd(0.1)).trainingWorkspaceMode(ws).inferenceWorkspaceMode(ws).seed(12345).graphBuilder().addInputs("in").addLayer("l0", build(), "in").addLayer("out", build(), "l0").setOutputs("out").build();
            ComputationGraph s = new ComputationGraph(standard);
            s.init();
            Nd4j.getRandom().setSeed(12345);
            ComputationGraphConfiguration external = new NeuralNetConfiguration.Builder().updater(new Sgd(0.1)).trainingWorkspaceMode(ws).inferenceWorkspaceMode(ws).seed(12345).graphBuilder().addInputs("in").addLayer("l0", build(), "in").setOutputs("l0").build();
            ComputationGraph e = new ComputationGraph(external);
            e.init();
            s.setInputs(inData);
            s.setLabels(outData);
            s.computeGradientAndScore();
            Gradient sGrad = s.gradient();
            s.feedForward(new INDArray[]{ inData }, true, false);// FF without clearing inputs as we need them later

            org.deeplearning4j.nn.layers.OutputLayer ol = ((org.deeplearning4j.nn.layers.OutputLayer) (s.getLayer(1)));
            ol.setLabels(outData);
            Pair<Gradient, INDArray> olPairStd = ol.backpropGradient(null, LayerWorkspaceMgr.noWorkspaces());
            INDArray olEpsilon = olPairStd.getSecond();
            e.feedForward(new INDArray[]{ inData }, true, false);// FF without clearing inputs as we need them later

            Gradient extErrorGrad = e.backpropGradient(olEpsilon);
            int nParamsDense = (10 * 10) + 10;
            Assert.assertEquals(sGrad.gradient().get(NDArrayIndex.point(0), NDArrayIndex.interval(0, nParamsDense)), extErrorGrad.gradient());
            Nd4j.getWorkspaceManager().destroyAllWorkspacesForCurrentThread();
        }
    }

    @Test
    public void testExternalErrors2() {
        Nd4j.getExecutioner().setProfilingMode(SCOPE_PANIC);
        int nIn = 4;
        int nOut = 3;
        for (WorkspaceMode ws : WorkspaceMode.values()) {
            System.out.println(("***** WORKSPACE: " + ws));
            ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().updater(new Adam(0.01)).trainingWorkspaceMode(ws).inferenceWorkspaceMode(ws).graphBuilder().addInputs("features").addVertex("rnn2ffn", new PreprocessorVertex(new RnnToFeedForwardPreProcessor()), "features").addLayer("predict", build(), "rnn2ffn").addVertex("ffn2rnn", new PreprocessorVertex(new FeedForwardToRnnPreProcessor()), "predict").addLayer("output", build(), "ffn2rnn").setOutputs("output").build();
            ComputationGraph graph = new ComputationGraph(conf);
            graph.init();
            final int minibatch = 5;
            final int seqLen = 6;
            INDArray param = Nd4j.create(new double[]{ 0.54, 0.31, 0.98, -0.3, -0.66, -0.19, -0.29, -0.62, 0.13, -0.32, 0.01, -0.03, 0.0, 0.0, 0.0 });
            graph.setParams(param);
            INDArray input = Nd4j.rand(new int[]{ minibatch, nIn, seqLen }, 12);
            INDArray expected = Nd4j.ones(minibatch, nOut, seqLen);
            INDArray output = graph.outputSingle(false, false, input);
            INDArray error = output.sub(expected);
            for (Layer l : graph.getLayers()) {
                Assert.assertNotNull(l.input());
                Assert.assertFalse(l.input().isAttached());
            }
            // Compute Gradient
            Gradient gradient = graph.backpropGradient(error);
            graph.getUpdater().update(gradient, 0, 0, minibatch, LayerWorkspaceMgr.noWorkspaces());
            Nd4j.getWorkspaceManager().destroyAllWorkspacesForCurrentThread();
        }
        Nd4j.getExecutioner().setProfilingMode(DISABLED);
    }

    @Test
    public void testExternalErrorsInvalid() {
        int nIn = 2;
        int nOut = 4;
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().graphBuilder().addInputs("in").addLayer("0", build(), "in").addLayer("1", build(), "0").addLayer("out", build(), "1").setOutputs("out").setInputTypes(InputType.feedForward(nIn)).build();
        ComputationGraph cg = new ComputationGraph(conf);
        cg.init();
        INDArray actionInput = Nd4j.randn(3, nIn);
        INDArray[] input = new INDArray[]{ actionInput };
        cg.setInputs(input);
        cg.feedForward(input, true, false);
        INDArray externalError = Nd4j.rand(3, nIn);
        try {
            cg.backpropGradient(externalError);
            Assert.fail("Expected exception");
        } catch (IllegalStateException e) {
            Assert.assertTrue(e.getMessage().contains("output layer"));
        }
    }

    @Test
    public void testGradientUpdate() {
        DataSetIterator iter = new IrisDataSetIterator(1, 1);
        Gradient expectedGradient = new DefaultGradient();
        expectedGradient.setGradientFor("first_W", Nd4j.ones(4, 5));
        expectedGradient.setGradientFor("first_b", Nd4j.ones(1, 5));
        expectedGradient.setGradientFor("output_W", Nd4j.ones(5, 3));
        expectedGradient.setGradientFor("output_b", Nd4j.ones(1, 3));
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).graphBuilder().addInputs("input").addLayer("first", build(), "input").addLayer("output", build(), "first").setOutputs("output").build();
        ComputationGraph net = new ComputationGraph(conf);
        net.init();
        net.fit(iter.next());
        Gradient actualGradient = net.gradient;
        Assert.assertNotEquals(expectedGradient.getGradientFor("first_W"), actualGradient.getGradientFor("first_W"));
        net.update(expectedGradient);
        actualGradient = net.gradient;
        Assert.assertEquals(expectedGradient.getGradientFor("first_W"), actualGradient.getGradientFor("first_W"));
        // Update params with set
        net.setParam("first_W", Nd4j.ones(4, 5));
        net.setParam("first_b", Nd4j.ones(1, 5));
        net.setParam("output_W", Nd4j.ones(5, 3));
        net.setParam("output_b", Nd4j.ones(1, 3));
        INDArray actualParams = net.params();
        // Confirm params
        Assert.assertEquals(Nd4j.ones(1, 43), actualParams);
        net.update(expectedGradient);
        actualParams = net.params();
        Assert.assertEquals(Nd4j.ones(1, 43).addi(1), actualParams);
    }

    @Test
    public void testCnnFlatInputType1() {
        // First: check conv input type. Expect: no preprocessor, nIn set appropriately
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().graphBuilder().addInputs("in").setInputTypes(InputType.convolutional(10, 8, 3)).addLayer("layer", build(), "in").addLayer("out", build(), "layer").setOutputs("out").build();
        LayerVertex lv = ((LayerVertex) (conf.getVertices().get("layer")));
        FeedForwardLayer l = ((FeedForwardLayer) (lv.getLayerConf().getLayer()));
        Assert.assertEquals(3, l.getNIn());
        Assert.assertNull(lv.getPreProcessor());
        // Check the equivalent config, but with flat conv data input instead
        // In this case, the only difference should be the addition of a preprocessor
        // First: check conv input type. Expect: no preprocessor, nIn set appropriately
        conf = build();
        lv = ((LayerVertex) (conf.getVertices().get("layer")));
        l = ((FeedForwardLayer) (lv.getLayerConf().getLayer()));
        Assert.assertEquals(3, l.getNIn());
        Assert.assertNotNull(lv.getPreProcessor());
        InputPreProcessor preProcessor = lv.getPreProcessor();
        Assert.assertTrue((preProcessor instanceof FeedForwardToCnnPreProcessor));
        FeedForwardToCnnPreProcessor preproc = ((FeedForwardToCnnPreProcessor) (preProcessor));
        Assert.assertEquals(10, preproc.getInputHeight());
        Assert.assertEquals(8, preproc.getInputWidth());
        Assert.assertEquals(3, preproc.getNumChannels());
        // Finally, check configuration with a subsampling layer
        conf = build();
        // Check subsampling layer:
        lv = ((LayerVertex) (conf.getVertices().get("l0")));
        SubsamplingLayer sl = ((SubsamplingLayer) (lv.getLayerConf().getLayer()));
        Assert.assertNotNull(lv.getPreProcessor());
        preProcessor = lv.getPreProcessor();
        Assert.assertTrue((preProcessor instanceof FeedForwardToCnnPreProcessor));
        preproc = ((FeedForwardToCnnPreProcessor) (preProcessor));
        Assert.assertEquals(10, preproc.getInputHeight());
        Assert.assertEquals(8, preproc.getInputWidth());
        Assert.assertEquals(3, preproc.getNumChannels());
        // Check dense layer
        lv = ((LayerVertex) (conf.getVertices().get("layer")));
        l = ((FeedForwardLayer) (lv.getLayerConf().getLayer()));
        Assert.assertEquals(3, l.getNIn());
        Assert.assertNull(lv.getPreProcessor());
    }

    @Test
    public void testCGEvaluation() {
        Nd4j.getRandom().setSeed(12345);
        ComputationGraphConfiguration configuration = TestComputationGraphNetwork.getIrisGraphConfiguration();
        ComputationGraph graph = new ComputationGraph(configuration);
        graph.init();
        Nd4j.getRandom().setSeed(12345);
        MultiLayerConfiguration mlnConfig = TestComputationGraphNetwork.getIrisMLNConfiguration();
        MultiLayerNetwork net = new MultiLayerNetwork(mlnConfig);
        net.init();
        DataSetIterator iris = new IrisDataSetIterator(75, 150);
        net.fit(iris);
        iris.reset();
        graph.fit(iris);
        iris.reset();
        Evaluation evalExpected = net.evaluate(iris);
        iris.reset();
        Evaluation evalActual = graph.evaluate(iris);
        Assert.assertEquals(evalExpected.accuracy(), evalActual.accuracy(), 0.0);
    }

    @Test
    public void testOptimizationAlgorithmsSearchBasic() {
        DataSetIterator iter = new IrisDataSetIterator(1, 1);
        OptimizationAlgorithm[] oas = new OptimizationAlgorithm[]{ OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT, OptimizationAlgorithm.LINE_GRADIENT_DESCENT, OptimizationAlgorithm.CONJUGATE_GRADIENT, OptimizationAlgorithm.LBFGS };
        for (OptimizationAlgorithm oa : oas) {
            System.out.println(oa);
            ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().optimizationAlgo(oa).graphBuilder().addInputs("input").addLayer("first", build(), "input").addLayer("output", build(), "first").setOutputs("output").build();
            ComputationGraph net = new ComputationGraph(conf);
            net.init();
            net.fit(iter.next());
        }
    }

    @Test
    public void testIterationCountAndPersistence() throws IOException {
        Nd4j.getRandom().setSeed(123);
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).seed(123).graphBuilder().addInputs("in").addLayer("0", build(), "in").addLayer("1", build(), "0").setOutputs("1").build();
        ComputationGraph network = new ComputationGraph(conf);
        network.init();
        DataSetIterator iter = new IrisDataSetIterator(50, 150);
        Assert.assertEquals(0, network.getConfiguration().getIterationCount());
        network.fit(iter);
        Assert.assertEquals(3, network.getConfiguration().getIterationCount());
        iter.reset();
        network.fit(iter);
        Assert.assertEquals(6, network.getConfiguration().getIterationCount());
        iter.reset();
        network.fit(iter.next());
        Assert.assertEquals(7, network.getConfiguration().getIterationCount());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ModelSerializer.writeModel(network, baos, true);
        byte[] asBytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(asBytes);
        ComputationGraph net = ModelSerializer.restoreComputationGraph(bais, true);
        Assert.assertEquals(7, net.getConfiguration().getIterationCount());
    }

    @Test
    public void printSummary() {
        NeuralNetConfiguration.Builder overallConf = new NeuralNetConfiguration.Builder().updater(new Sgd(0.1)).activation(IDENTITY);
        ComputationGraphConfiguration conf = overallConf.graphBuilder().addInputs("inCentre", "inRight").addLayer("denseCentre0", build(), "inCentre").addLayer("denseCentre1", build(), "denseCentre0").addLayer("denseCentre2", build(), "denseCentre1").addLayer("denseCentre3", build(), "denseCentre2").addLayer("outCentre", build(), "denseCentre3").addVertex("subsetLeft", new SubsetVertex(0, 3), "denseCentre1").addLayer("denseLeft0", build(), "subsetLeft").addLayer("outLeft", build(), "denseLeft0").addLayer("denseRight", build(), "denseCentre2").addLayer("denseRight0", build(), "inRight").addVertex("mergeRight", new MergeVertex(), "denseRight", "denseRight0").addLayer("denseRight1", build(), "mergeRight").addLayer("outRight", build(), "denseRight1").setOutputs("outLeft", "outCentre", "outRight").build();
        ComputationGraph modelToTune = new ComputationGraph(conf);
        modelToTune.init();
        System.out.println(modelToTune.summary());
        ComputationGraph modelNow = setFeatureExtractor("denseCentre2").build();
        System.out.println(modelNow.summary());
        System.out.println(modelNow.summary(InputType.feedForward(10), InputType.feedForward(2)));
    }

    @Test
    public void testFeedForwardIncludeNonLayerVertices() {
        ComputationGraphConfiguration c = new NeuralNetConfiguration.Builder().graphBuilder().addInputs("in").addLayer("0", build(), "in").addLayer("1", build(), "in").addVertex("merge", new MergeVertex(), "0", "1").addLayer("out", build(), "merge").setOutputs("out").build();
        ComputationGraph cg = new ComputationGraph(c);
        cg.init();
        cg.setInputs(Nd4j.ones(1, 5));
        Map<String, INDArray> layersOnly = cg.feedForward(true, false, false);
        Map<String, INDArray> alsoVertices = cg.feedForward(true, false, true);
        Assert.assertEquals(4, layersOnly.size());// 3 layers + 1 input

        Assert.assertEquals(5, alsoVertices.size());// 3 layers + 1 input + merge vertex

        Assert.assertFalse(layersOnly.containsKey("merge"));
        Assert.assertTrue(alsoVertices.containsKey("merge"));
    }

    @Test
    public void testSetOutputsMultipleCalls() {
        // Users generally shouldn't do this, but multiple setOutputs calls should *replace* not *add* outputs
        ComputationGraphConfiguration c = new NeuralNetConfiguration.Builder().graphBuilder().addInputs("in").addLayer("out", build(), "in").setOutputs("out").setOutputs("out").build();
        List<String> l = c.getNetworkOutputs();
        Assert.assertEquals(1, l.size());
    }

    @Test
    public void testDropoutValidation() {
        // At one point: this threw an exception due to incorrect validation
        for (boolean dropConnect : new boolean[]{ false, true }) {
            build();
        }
    }

    @Test
    public void testNoParamLayersL1L2() {
        // Don't care about this being valid
        ComputationGraphConfiguration c = new NeuralNetConfiguration.Builder().l1(0.5).l2(0.6).graphBuilder().addInputs("in").addLayer("sub1", new SubsamplingLayer.Builder(2, 2).build(), "in").addLayer("sub2", new Subsampling1DLayer.Builder(2).build(), "sub1").addLayer("act", build(), "sub2").addLayer("pad", new ZeroPaddingLayer.Builder(2, 3).build(), "act").addLayer("lrn", new LocalResponseNormalization.Builder().build(), "pad").addLayer("pool", build(), "act").addLayer("drop", new DropoutLayer.Builder(0.5).build(), "pool").addLayer("dense", build(), "drop").addLayer("loss", build(), "dense").allowDisconnected(true).setOutputs("loss").build();
        ComputationGraph g = new ComputationGraph(c);
        g.init();
        g.calcRegularizationScore(true);
        g.calcRegularizationScore(false);
    }

    @Test(expected = DL4JException.class)
    public void testErrorNoOutputLayer() {
        ComputationGraphConfiguration c = new NeuralNetConfiguration.Builder().graphBuilder().addInputs("in").addLayer("dense", build(), "in").setOutputs("dense").build();
        ComputationGraph cg = new ComputationGraph(c);
        cg.init();
        INDArray f = Nd4j.create(1, 10);
        INDArray l = Nd4j.create(1, 10);
        cg.setInputs(f);
        cg.setLabels(l);
        cg.computeGradientAndScore();
    }

    @Test
    public void testMergeVertexAddition() {
        // When a vertex supports only one input, and gets multiple inputs - we should automatically add a merge
        // vertex
        NeuralNetConfiguration nnc = new NeuralNetConfiguration();
        nnc.setLayer(new DenseLayer.Builder().build());
        GraphVertex[] singleInputVertices = new GraphVertex[]{ new L2NormalizeVertex(), new LayerVertex(nnc, null), new PoolHelperVertex(), new PreprocessorVertex(), new ReshapeVertex(new int[]{ 1, 1 }), new ScaleVertex(1.0), new ShiftVertex(1.0), new SubsetVertex(1, 1), new UnstackVertex(0, 2), new DuplicateToTimeSeriesVertex("in1"), new LastTimeStepVertex("in1") };
        for (GraphVertex gv : singleInputVertices) {
            ComputationGraphConfiguration c = new NeuralNetConfiguration.Builder().graphBuilder().addInputs("in1", "in2").addVertex("gv", gv, "in1", "in2").setOutputs("gv").build();
            boolean foundMerge = false;
            for (GraphVertex g : c.getVertices().values()) {
                if (g instanceof MergeVertex) {
                    foundMerge = true;
                    break;
                }
            }
            if (!foundMerge) {
                Assert.fail(("Network did not add merge vertex for vertex " + (gv.getClass())));
            }
        }
    }

    @Test
    public void testVertexAsOutput() {
        // Simple sanity check: vertex is the last output...
        int minibatch = 10;
        int height = 24;
        int width = 24;
        int depth = 3;
        INDArray img = Nd4j.ones(minibatch, depth, height, width);
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().graphBuilder().addInputs("input").addLayer("L1", build(), "input").addVertex("L2", new ReshapeVertex(minibatch, 1, 36, 48), "L1").setOutputs("L2").build();
        ComputationGraph net = new ComputationGraph(conf);
        net.init();
        INDArray[] out = net.output(img);
        Assert.assertNotNull(out);
        Assert.assertEquals(1, out.length);
        Assert.assertNotNull(out[0]);
        Assert.assertArrayEquals(new long[]{ minibatch, 1, 36, 48 }, out[0].shape());
    }

    @Test
    public void testEpochCounter() throws Exception {
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().graphBuilder().addInputs("in").addLayer("out", build(), "in").setOutputs("out").build();
        ComputationGraph net = new ComputationGraph(conf);
        net.init();
        Assert.assertEquals(0, net.getConfiguration().getEpochCount());
        DataSetIterator iter = new IrisDataSetIterator(150, 150);
        for (int i = 0; i < 4; i++) {
            Assert.assertEquals(i, net.getConfiguration().getEpochCount());
            net.fit(iter);
            Assert.assertEquals((i + 1), net.getConfiguration().getEpochCount());
        }
        Assert.assertEquals(4, net.getConfiguration().getEpochCount());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ModelSerializer.writeModel(net, baos, true);
        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ComputationGraph restored = ModelSerializer.restoreComputationGraph(bais, true);
        Assert.assertEquals(4, restored.getConfiguration().getEpochCount());
    }

    @Test
    public void testSummary() {
        int V_WIDTH = 130;
        int V_HEIGHT = 130;
        int V_NFRAMES = 150;
        ComputationGraphConfiguration confForArchitecture = // Output: (15-3+0)/2+1 = 7 -> 7*7*10 = 490
        // (31-3+0)/2+1 = 15
        // Output: (130-10+0)/4+1 = 31 -> 31*31*30
        // l2 regularization on all layers
        new NeuralNetConfiguration.Builder().seed(12345).l2(0.001).updater(new org.nd4j.linalg.learning.config.AdaGrad(0.4)).graphBuilder().addInputs("in").addLayer("layer0", build(), "in").addLayer("layer1", build(), "layer0").addLayer("layer2", build(), "layer1").addLayer("layer3", build(), "layer2").addLayer("layer4", build(), "layer3").addLayer("layer5", build(), "layer4").setOutputs("layer5").inputPreProcessor("layer0", new RnnToCnnPreProcessor(V_HEIGHT, V_WIDTH, 3)).inputPreProcessor("layer3", new CnnToFeedForwardPreProcessor(7, 7, 10)).inputPreProcessor("layer4", new FeedForwardToRnnPreProcessor()).backpropType(BackpropType.TruncatedBPTT).tBPTTForwardLength((V_NFRAMES / 5)).tBPTTBackwardLength((V_NFRAMES / 5)).build();
        ComputationGraph modelExpectedArch = new ComputationGraph(confForArchitecture);
        modelExpectedArch.init();
        ComputationGraph modelMow = setFeatureExtractor("layer2").build();
        System.out.println(modelExpectedArch.summary());
        System.out.println(modelMow.summary());
        System.out.println(modelExpectedArch.summary(InputType.recurrent(((V_HEIGHT * V_WIDTH) * 3))));
    }

    @Test
    public void testInputClearance() throws Exception {
        // Activations should be cleared - if not, it's possible for out of (workspace) scope arrays to be around
        // which can cause a crash
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().convolutionMode(ConvolutionMode.Same).graphBuilder().addInputs("in").addLayer("0", build(), "in").addLayer("1", build(), "0").addLayer("2", build(), "1").addLayer("3", build(), "2").setOutputs("3").setInputTypes(InputType.convolutional(28, 28, 1)).build();
        ComputationGraph net = new ComputationGraph(conf);
        net.init();
        INDArray content = Nd4j.create(1, 1, 28, 28);
        // Check output:
        net.output(content);
        for (Layer l : net.getLayers()) {
            Assert.assertNull(l.input());
        }
        // Check feedForward:
        net.feedForward(content, false);
        for (Layer l : net.getLayers()) {
            Assert.assertNull(l.input());
        }
    }

    @Test
    public void testDisconnectedVertex() {
        for (boolean allowDisconnected : new boolean[]{ false, true }) {
            try {
                ComputationGraphConfiguration.GraphBuilder b = // Disconnected
                new NeuralNetConfiguration.Builder().graphBuilder().addInputs("in").addLayer("0", build(), "in").addLayer("1", build(), "in").addLayer("O", build(), "0").setOutputs("O").setInputTypes(InputType.feedForward(8));
                if (allowDisconnected) {
                    build();// No exception

                } else {
                    b.build();// Expect exception here

                    Assert.fail("Expected exception for disconnected vertex");
                }
            } catch (Exception e) {
                // e.printStackTrace();
                if (allowDisconnected) {
                    Assert.fail("No exception expected");
                } else {
                    String msg = e.getMessage().toLowerCase();
                    Assert.assertTrue(msg.contains("disconnected"));
                }
            }
        }
    }

    @Test
    public void testLayerSize() {
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().graphBuilder().addInputs("in").layer("0", build(), "in").layer("1", build(), "0").layer("2", build(), "1").layer("3", build(), "2").setOutputs("3").setInputTypes(InputType.convolutional(28, 28, 3)).build();
        ComputationGraph net = new ComputationGraph(conf);
        net.init();
        Assert.assertEquals(6, net.layerSize(0));
        Assert.assertEquals(0, net.layerSize(1));
        Assert.assertEquals(30, net.layerSize(2));
        Assert.assertEquals(13, net.layerSize(3));
        Assert.assertEquals(3, net.layerInputSize(0));
        Assert.assertEquals(0, net.layerInputSize(1));
        Assert.assertEquals(getNIn(), net.layerInputSize(2));
        Assert.assertEquals(30, net.layerInputSize(3));
        Assert.assertEquals(6, net.layerSize("0"));
        Assert.assertEquals(0, net.layerSize("1"));
        Assert.assertEquals(30, net.layerSize("2"));
        Assert.assertEquals(13, net.layerSize("3"));
        Assert.assertEquals(3, net.layerInputSize("0"));
        Assert.assertEquals(0, net.layerInputSize("1"));
        Assert.assertEquals(getNIn(), net.layerInputSize("2"));
        Assert.assertEquals(30, net.layerInputSize("3"));
    }

    @Test
    public void testZeroParamNet() throws Exception {
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().graphBuilder().addInputs("in").layer("0", build(), "in").layer("1", build(), "0").setOutputs("1").setInputTypes(InputType.convolutionalFlat(28, 28, 1)).build();
        ComputationGraph net = new ComputationGraph(conf);
        net.init();
        DataSet ds = new MnistDataSetIterator(16, true, 12345).next();
        INDArray out = net.outputSingle(ds.getFeatures());
        INDArray labelTemp = Nd4j.create(out.shape());
        ds.setLabels(labelTemp);
        net.fit(ds);
        ComputationGraph net2 = TestUtils.testModelSerialization(net);
        INDArray out2 = net2.outputSingle(ds.getFeatures());
        Assert.assertEquals(out, out2);
    }

    @Test
    public void scaleVertexGraphTest() {
        final double scaleFactor = 2;
        final float[] inputArr = new float[]{ -2, -1, 0, 1, 2 };// IntStream.rangeClosed(-2, 2).mapToDouble(i -> i).toArray();

        final float[] expected = new float[inputArr.length];// DoubleStream.of(inputArr).map(i -> i * scaleFactor).toArray();

        for (int i = 0; i < (expected.length); i++) {
            expected[i] = ((float) ((inputArr[i]) * scaleFactor));
        }
        final INDArray input = TestComputationGraphNetwork.getInputArray4d(inputArr);// Replacing this line with the line below is enough to make test pass

        // final INDArray input = Nd4j.create(new float[][]{inputArr});
        final String inputName = "input";
        final String outputName = "output";
        final String scaleName = "scale";
        final ComputationGraph graph = new ComputationGraph(build());
        graph.init();
        // graph.fit(new DataSet(input, Nd4j.ones(input.length()))); // Does not help
        // graph.feedForward(new INDArray[] {input}, false); // Uncommenting this line is enough to make test pass
        // Hack output layer to be identity mapping
        graph.getOutputLayer(0).setParam("W", Nd4j.eye(input.length()));
        graph.getOutputLayer(0).setParam("b", Nd4j.zeros(input.length()));
        Assert.assertEquals("Incorrect output", Nd4j.create(expected), graph.outputSingle(input));
    }

    @Test
    public void testGraphOutputIterators() {
        DataSet all = new IrisDataSetIterator(150, 150).next();
        DataSetIterator iter = new IrisDataSetIterator(5, 150);
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).graphBuilder().addInputs("in").layer("layer", build(), "in").setOutputs("layer").build();
        ComputationGraph cg = new ComputationGraph(conf);
        cg.init();
        INDArray outAll = cg.outputSingle(all.getFeatures());
        INDArray outIter = cg.outputSingle(iter);
        Assert.assertEquals(outAll, outIter);
    }

    @Test
    public void testComputationGraphConfgurationActivationTypes() {
        // Test for a simple net:
        ComputationGraphConfiguration.GraphBuilder builder = new NeuralNetConfiguration.Builder().graphBuilder().addInputs("in1", "in2").layer("0", build(), "in1").layer("1", build(), "in1", "in2").layer("2", build(), "in2").layer("3", build(), "0").layer("4", build(), "1", "2").setInputTypes(InputType.feedForward(5), InputType.feedForward(6)).allowNoOutput(true);
        ComputationGraphConfiguration conf = builder.build();
        Map<String, InputType> actBuilder = builder.getLayerActivationTypes();
        Map<String, InputType> actConf = conf.getLayerActivationTypes(InputType.feedForward(5), InputType.feedForward(6));
        Map<String, InputType> exp = new HashMap<>();
        exp.put("in1", InputType.feedForward(5));
        exp.put("in2", InputType.feedForward(6));
        exp.put("0", InputType.feedForward(10));
        exp.put("1", InputType.feedForward(9));
        exp.put("1-merge", InputType.feedForward((5 + 6)));
        exp.put("2", InputType.feedForward(8));
        exp.put("3", InputType.feedForward(7));
        exp.put("4", InputType.feedForward(6));
        exp.put("4-merge", InputType.feedForward((9 + 8)));
        Assert.assertEquals(exp, actBuilder);
        Assert.assertEquals(exp, actConf);
    }

    @Test
    public void testTopoSortSaving() {
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().graphBuilder().addInputs("in1", "in2").addLayer("l0", build(), "in1").addLayer("l1", build(), "in1", "in2").addLayer("l2", build(), "in2").addLayer("l3", build(), "l0").addLayer("l4", build(), "l1").addLayer("l5", build(), "l2").addLayer("l6", build(), "l3", "l5").addLayer("l7", build(), "l4").setOutputs("l6", "l7").build();
        INDArray[] in = new INDArray[]{ Nd4j.rand(3, 10), Nd4j.rand(3, 10) };
        ComputationGraph cg = new ComputationGraph(conf);
        cg.init();
        GraphIndices indices = cg.calculateIndices();
        int[] order = cg.topologicalSortOrder();
        List<String> strOrder = cg.getConfiguration().getTopologicalOrderStr();
        INDArray[] out1 = cg.output(in);
        // Check it's the same after loading:
        ComputationGraph cg2 = TestUtils.testModelSerialization(cg);
        int[] order2 = cg2.topologicalSortOrder();
        List<String> strOrder2 = cg.getConfiguration().getTopologicalOrderStr();
        Assert.assertArrayEquals(order, order2);
        Assert.assertEquals(strOrder, strOrder2);
        INDArray[] out2 = cg2.output(in);
        Assert.assertArrayEquals(out1, out2);
        // Delete the topological order, ensure it gets recreated properly:
        ComputationGraphConfiguration conf3 = cg2.getConfiguration().clone();
        conf3.setTopologicalOrder(null);
        conf3.setTopologicalOrderStr(null);
        ComputationGraph cg3 = new ComputationGraph(conf3);
        cg3.init();
        cg3.setParams(cg2.params());
        int[] order3 = cg3.topologicalSortOrder();
        List<String> strOrder3 = cg.getConfiguration().getTopologicalOrderStr();
        INDArray[] out3 = cg3.output(in);
        Assert.assertArrayEquals(order, order3);
        Assert.assertEquals(strOrder, strOrder3);
        Assert.assertArrayEquals(out1, out3);
        // Now, change the order, and ensure the net is the same... note that we can do [l0, l1, l2] in any order
        List<List<String>> someValidOrders = new ArrayList<>();
        someValidOrders.add(Arrays.asList("in1", "in2", "l0", "l1-merge", "l1", "l2", "l3", "l4", "l5", "l6-merge", "l6", "l7"));
        someValidOrders.add(Arrays.asList("in1", "in2", "l1-merge", "l1", "l0", "l2", "l3", "l4", "l5", "l6-merge", "l6", "l7"));
        someValidOrders.add(Arrays.asList("in1", "in2", "l2", "l1-merge", "l1", "l0", "l3", "l4", "l5", "l6-merge", "l6", "l7"));
        someValidOrders.add(Arrays.asList("in1", "in2", "l2", "l5", "l0", "l1-merge", "l1", "l3", "l4", "l7", "l6-merge", "l6"));
        for (List<String> l : someValidOrders) {
            Assert.assertEquals(strOrder.size(), l.size());
        }
        for (int i = 0; i < (someValidOrders.size()); i++) {
            List<String> l = someValidOrders.get(i);
            int[] arr = new int[l.size()];
            int j = 0;
            for (String s : l) {
                arr[(j++)] = indices.getNameToIdx().get(s);
            }
            ComputationGraphConfiguration conf2 = conf.clone();
            conf2.setTopologicalOrderStr(l);
            conf2.setTopologicalOrder(arr);
            ComputationGraph g = new ComputationGraph(conf2);
            g.init();
            g.setParamTable(cg.paramTable());
            int[] origOrder = g.topologicalSortOrder();
            INDArray[] out4 = g.output(in);
            Assert.assertArrayEquals(out1, out4);
            ComputationGraph g2 = TestUtils.testModelSerialization(g);
            int[] loadedOrder = g2.topologicalSortOrder();
            Assert.assertArrayEquals(origOrder, loadedOrder);
            INDArray[] out5 = g2.output(in);
            Assert.assertArrayEquals(out1, out5);
        }
    }

    @Test
    public void testPretrainFitMethods() {
        // The fit methods should *not* do layerwise pretraining:
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().graphBuilder().addInputs("in").layer("0", build(), "in").layer("1", build(), "0").setOutputs("1").build();
        ComputationGraph net = new ComputationGraph(conf);
        net.init();
        Set<Class<?>> exp = new HashSet<>();
        exp.add(ComputationGraph.class);
        MultiLayerTest.CheckModelsListener listener = new MultiLayerTest.CheckModelsListener();
        net.setListeners(listener);
        INDArray f = Nd4j.create(1, 10);
        INDArray l = Nd4j.create(1, 10);
        DataSet ds = new DataSet(f, l);
        MultiDataSet mds = new org.nd4j.linalg.dataset.MultiDataSet(f, l);
        DataSetIterator iter = new org.deeplearning4j.datasets.iterator.ExistingDataSetIterator(Collections.singletonList(ds));
        net.fit(ds);
        Assert.assertEquals(exp, getModelClasses());
        net.fit(iter);
        Assert.assertEquals(exp, getModelClasses());
        net.fit(new INDArray[]{ f }, new INDArray[]{ l });
        Assert.assertEquals(exp, getModelClasses());
        net.fit(new INDArray[]{ f }, new INDArray[]{ l }, null, null);
        Assert.assertEquals(exp, getModelClasses());
        net.fit(mds);
        Assert.assertEquals(exp, getModelClasses());
        net.fit(new org.deeplearning4j.datasets.iterator.impl.SingletonMultiDataSetIterator(mds));
        Assert.assertEquals(exp, getModelClasses());
    }

    @Test
    public void testAllowInputModification() {
        ComputationGraphConfiguration conf = // Input from merge vertex - allowed
        // Last in topo sort - allowed
        // Second in topo sort - not allowed
        // First in topo sort for using this input - not allowed
        // Modification SHOULD be allowed
        // Modification should not be allowed on input
        // Modification should not be allowed on input
        new NeuralNetConfiguration.Builder().graphBuilder().addInputs("in1", "in2").layer("0", build(), "in1").layer("1", build(), "in2").layer("2", build(), "0").layer("3", build(), "1").layer("4", build(), "1").layer("5", build(), "1").layer("6", build(), "2", "3", "4", "5").setOutputs("6").setInputTypes(InputType.feedForward(10), InputType.feedForward(10)).build();
        ComputationGraph cg = new ComputationGraph(conf);
        cg.init();
        Map<String, Boolean> exp = new HashMap<>();
        exp.put("0", false);
        exp.put("1", false);
        exp.put("2", true);
        exp.put("3", false);
        exp.put("4", false);
        exp.put("5", true);
        exp.put("6", true);
        for (String s : exp.keySet()) {
            boolean allowed = isInputModificationAllowed();
            // System.out.println(s + "\t" + allowed);
            Assert.assertEquals(s, exp.get(s), allowed);
        }
    }

    @Test
    public void testCompGraphDropoutOutputLayers() {
        // https://github.com/deeplearning4j/deeplearning4j/issues/6326
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().dropOut(0.8).graphBuilder().addInputs("in1", "in2").addVertex("merge", new MergeVertex(), "in1", "in2").addLayer("lstm", new org.deeplearning4j.nn.conf.layers.recurrent.Bidirectional(Bidirectional.Mode.CONCAT, build()), "merge").addLayer("out1", build(), "lstm").addLayer("out2", build(), "lstm").setOutputs("out1", "out2").build();
        ComputationGraph net = new ComputationGraph(conf);
        net.init();
        INDArray[] features = new INDArray[]{ Nd4j.create(1, 5, 5), Nd4j.create(1, 5, 5) };
        INDArray[] labels = new INDArray[]{ Nd4j.create(1, 6, 5), Nd4j.create(1, 4, 5) };
        MultiDataSet mds = new org.nd4j.linalg.dataset.MultiDataSet(features, labels);
        net.fit(mds);
    }

    @Test
    public void testCompGraphDropoutOutputLayers2() {
        // https://github.com/deeplearning4j/deeplearning4j/issues/6326
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().dropOut(0.8).graphBuilder().addInputs("in1", "in2").addVertex("merge", new MergeVertex(), "in1", "in2").addLayer("dense", build(), "merge").addLayer("out1", build(), "dense").addLayer("out2", build(), "dense").setOutputs("out1", "out2").build();
        ComputationGraph net = new ComputationGraph(conf);
        net.init();
        INDArray[] features = new INDArray[]{ Nd4j.create(1, 5), Nd4j.create(1, 5) };
        INDArray[] labels = new INDArray[]{ Nd4j.create(1, 6), Nd4j.create(1, 4) };
        MultiDataSet mds = new org.nd4j.linalg.dataset.MultiDataSet(features, labels);
        net.fit(mds);
    }

    @Test
    public void testAddRemoveVertex() {
        new NeuralNetConfiguration.Builder().graphBuilder().addVertex("toRemove", new ScaleVertex(0), "don't care").addVertex("test", new ScaleVertex(0), "toRemove").removeVertex("toRemove", true);
    }

    @Test
    public void testGetSetParamUnderscores() {
        // Test get/set param with underscores in layer nome
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().graphBuilder().addInputs("in").layer("layer_zero", build(), "in").layer("layer_one", build(), "layer_zero").setOutputs("layer_one").build();
        ComputationGraph cg = new ComputationGraph(conf);
        cg.init();
        cg.params().assign(Nd4j.linspace(1, 220, 220).reshape(1, (-11)));
        INDArray p0w = cg.getParam("layer_zero_W");
        Assert.assertEquals(Nd4j.linspace(1, 100, 100).reshape('f', 10, 10), p0w);
        INDArray p1b = cg.getParam("layer_one_b");
        Assert.assertEquals(Nd4j.linspace(211, 220, 10).reshape(1, 10), p1b);
        INDArray newP1b = Nd4j.valueArrayOf(new long[]{ 1, 10 }, (-1.0));
        cg.setParam("layer_one_b", newP1b);
        Assert.assertEquals(newP1b, p1b);
    }

    @Test
    public void testOutputSpecificLayers() {
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).graphBuilder().addInputs("in").layer("0", build(), "in").layer("1", build(), "0").layer("2", build(), "1").layer("3", build(), "2").setOutputs("3").build();
        ComputationGraph cg = new ComputationGraph(conf);
        cg.init();
        INDArray in = Nd4j.rand(1, 10);
        Map<String, INDArray> outMap = cg.feedForward(in, false);
        INDArray[] outSpecific = cg.output(Arrays.asList("1", "3"), false, new INDArray[]{ in }, null);
        Assert.assertEquals(2, outSpecific.length);
        Assert.assertEquals(outMap.get("1"), outSpecific[0]);
        Assert.assertEquals(outMap.get("3"), outSpecific[1]);
    }

    @Test
    public void singleInputElemVertex() {
        final InputType inputType = InputType.convolutional(10, 10, 2);
        final ComputationGraph graph = new ComputationGraph(build());
        graph.init();
        graph.outputSingle(Nd4j.randn(1, 2, 10, 10));
    }

    @Test
    public void testCloneDropoutIndependence() {
        val modelConf = new NeuralNetConfiguration.Builder().updater(new Adam(0.01)).weightInit(WeightInit.XAVIER_UNIFORM).biasInit(0).graphBuilder().addInputs("input").addLayer("dense", build(), "input").addLayer("output", build(), "dense").setOutputs("output").build();
        ComputationGraph model = new ComputationGraph(modelConf);
        model.init();
        ComputationGraph cg2 = model.clone();
        IDropout d1 = model.getLayer(0).conf().getLayer().getIDropout();
        IDropout d2 = cg2.getLayer(0).conf().getLayer().getIDropout();
        Assert.assertFalse((d1 == d2));// Should not be same object!

        Assert.assertEquals(d1, d2);// But should be equal

    }

    @Test
    public void testVerticesAndMasking7027() {
        // https://github.com/deeplearning4j/deeplearning4j/issues/7027
        int inputSize = 300;
        int hiddenSize = 100;
        ComputationGraphConfiguration configuration = new NeuralNetConfiguration.Builder().updater(new Adam()).graphBuilder().addInputs("x_emb").setInputTypes(InputType.recurrent(inputSize)).addLayer("agg_lstm", new org.deeplearning4j.nn.conf.layers.recurrent.Bidirectional(org.deeplearning4j.nn.conf.layers.recurrent.Bidirectional.Mode.CONCAT, build()), "x_emb").addLayer("agg_att", build(), "agg_lstm").addVertex("att", new PreprocessorVertex(new ComposableInputPreProcessor(new FeedForwardToRnnPreProcessor(), new org.deeplearning4j.nn.modelimport.keras.preprocessors.PermutePreprocessor(new int[]{ 0, 2, 1 }), new RnnToFeedForwardPreProcessor())), "agg_att").addLayer("att_repeat", new org.deeplearning4j.nn.conf.layers.misc.RepeatVector.Builder(hiddenSize).build(), "att").addVertex("att_trans", new PreprocessorVertex(new org.deeplearning4j.nn.modelimport.keras.preprocessors.PermutePreprocessor(new int[]{ 0, 2, 1 })), "att_repeat").addVertex("mult", new ElementWiseVertex(ElementWiseVertex.Op.Product), "agg_lstm", "att_trans").addLayer("sum", new GlobalPoolingLayer.Builder().build(), "mult").addLayer("agg_out", build(), "sum").addLayer("output", build(), "agg_out").setOutputs("output").build();
        ComputationGraph net = new ComputationGraph(configuration);
        net.init();
        int dataSize = 10;
        int seqLen = 5;
        INDArray features = Nd4j.rand(new int[]{ dataSize, inputSize, seqLen });
        INDArray labels = Nd4j.rand(new int[]{ dataSize, 6 });
        INDArray featuresMask = Nd4j.ones(dataSize, seqLen);
        INDArray labelsMask = Nd4j.ones(dataSize, 6);
        DataSet dataSet1 = new DataSet(features, labels);
        net.fit(dataSet1);
        DataSet dataSet2 = new DataSet(features, labels, featuresMask, labelsMask);
        net.fit(dataSet2);
    }

    @Test
    public void testCompGraphUpdaterBlocks() {
        // Check that setting learning rate results in correct rearrangement of updater state within updater blocks
        // https://github.com/deeplearning4j/deeplearning4j/issues/6809#issuecomment-463892644
        double lr = 0.001;
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).weightInit(XAVIER).updater(new Adam(lr)).graphBuilder().backpropType(Standard).addInputs("in").setOutputs("out").addLayer("0", build(), "in").addLayer("1", build(), "0").addLayer("out", build(), "1").build();
        ComputationGraph cg = new ComputationGraph(conf);
        cg.init();
        INDArray in = Nd4j.rand(1, 5);
        INDArray lbl = Nd4j.rand(1, 1);
        cg.fit(new DataSet(in, lbl));
        INDArray viewArray = cg.getUpdater().getUpdaterStateViewArray();
        INDArray viewArrayCopy = viewArray.dup();
        // Initially updater view array is set out like:
        // [m0w, m0b, m1w, m1b, m2w, m2b][v0w, v0b, v1w, v1b, v2w, v2b]
        long soFar = 0;
        INDArray m0w = viewArray.get(NDArrayIndex.point(0), NDArrayIndex.interval(soFar, (soFar + (5 * 3)))).assign(0);// m0w

        soFar += 5 * 3;
        INDArray m0b = viewArray.get(NDArrayIndex.point(0), NDArrayIndex.interval(soFar, (soFar + 3))).assign(1);// m0b

        soFar += 3;
        INDArray m1w = viewArray.get(NDArrayIndex.point(0), NDArrayIndex.interval(soFar, (soFar + (3 * 2)))).assign(2);// m1w

        soFar += 3 * 2;
        INDArray m1b = viewArray.get(NDArrayIndex.point(0), NDArrayIndex.interval(soFar, (soFar + 2))).assign(3);// m1b

        soFar += 2;
        INDArray m2w = viewArray.get(NDArrayIndex.point(0), NDArrayIndex.interval(soFar, (soFar + (2 * 1)))).assign(4);// m2w

        soFar += 2 * 1;
        INDArray m2b = viewArray.get(NDArrayIndex.point(0), NDArrayIndex.interval(soFar, (soFar + 1))).assign(5);// m2b

        soFar += 1;
        INDArray v0w = viewArray.get(NDArrayIndex.point(0), NDArrayIndex.interval(soFar, (soFar + (5 * 3)))).assign(6);// v0w

        soFar += 5 * 3;
        INDArray v0b = viewArray.get(NDArrayIndex.point(0), NDArrayIndex.interval(soFar, (soFar + 3))).assign(7);// v0b

        soFar += 3;
        INDArray v1w = viewArray.get(NDArrayIndex.point(0), NDArrayIndex.interval(soFar, (soFar + (3 * 2)))).assign(8);// v1w

        soFar += 3 * 2;
        INDArray v1b = viewArray.get(NDArrayIndex.point(0), NDArrayIndex.interval(soFar, (soFar + 2))).assign(9);// v1b

        soFar += 2;
        INDArray v2w = viewArray.get(NDArrayIndex.point(0), NDArrayIndex.interval(soFar, (soFar + (2 * 1)))).assign(10);// v2w

        soFar += 2 * 1;
        INDArray v2b = viewArray.get(NDArrayIndex.point(0), NDArrayIndex.interval(soFar, (soFar + 1))).assign(11);// v2b

        soFar += 1;
        cg.setLearningRate("0", 0.0);
        // Expect new updater state to look like:
        // [m0w, m0b][v0w,v0b], [m1w, m1b, m2w, m2b][v1w, v1b, v2w, v2b]
        INDArray exp = Nd4j.concat(1, m0w, m0b, v0w, v0b, m1w, m1b, m2w, m2b, v1w, v1b, v2w, v2b);
        INDArray act = cg.getUpdater().getUpdaterStateViewArray();
        // System.out.println(exp);
        // System.out.println(act);
        Assert.assertEquals(exp, act);
        // And set layer 1 LR:
        cg.setLearningRate("1", 0.2);
        exp = Nd4j.concat(1, m0w, m0b, v0w, v0b, m1w, m1b, v1w, v1b, m2w, m2b, v2w, v2b);
        Assert.assertEquals(exp, cg.getUpdater().getStateViewArray());
        // Set all back to original LR and check again:
        cg.setLearningRate("1", lr);
        cg.setLearningRate("0", lr);
        exp = Nd4j.concat(1, m0w, m0b, m1w, m1b, m2w, m2b, v0w, v0b, v1w, v1b, v2w, v2b);
        Assert.assertEquals(exp, cg.getUpdater().getStateViewArray());
        // Finally, training sanity check (if things are wrong, we get -ve values in adam V, which causes NaNs)
        cg.getUpdater().getStateViewArray().assign(viewArrayCopy);
        cg.setLearningRate("0", 0.0);
        Nd4j.getExecutioner().setProfilingMode(NAN_PANIC);
        cg.fit(new DataSet(in, lbl));
        Nd4j.getExecutioner().setProfilingMode(SCOPE_PANIC);
    }
}

