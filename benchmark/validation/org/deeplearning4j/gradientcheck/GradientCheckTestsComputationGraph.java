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
import Activation.RELU;
import Activation.SIGMOID;
import Activation.SOFTMAX;
import Activation.SOFTSIGN;
import Activation.TANH;
import DataType.DOUBLE;
import ElementWiseVertex.Op;
import LossFunctions.LossFunction.L2;
import LossFunctions.LossFunction.MCXENT;
import LossFunctions.LossFunction.MSE;
import OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT;
import PoolingType.AVG;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.TestUtils;
import org.deeplearning4j.datasets.iterator.impl.IrisDataSetIterator;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.distribution.GaussianDistribution;
import org.deeplearning4j.nn.conf.distribution.NormalDistribution;
import org.deeplearning4j.nn.conf.distribution.UniformDistribution;
import org.deeplearning4j.nn.conf.graph.rnn.DuplicateToTimeSeriesVertex;
import org.deeplearning4j.nn.conf.graph.rnn.LastTimeStepVertex;
import org.deeplearning4j.nn.conf.graph.rnn.ReverseTimeSeriesVertex;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.preprocessor.CnnToFeedForwardPreProcessor;
import org.deeplearning4j.nn.conf.preprocessor.FeedForwardToRnnPreProcessor;
import org.deeplearning4j.nn.conf.preprocessor.RnnToFeedForwardPreProcessor;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.learning.config.NoOp;


public class GradientCheckTestsComputationGraph extends BaseDL4JTest {
    public static final boolean PRINT_RESULTS = true;

    private static final boolean RETURN_ON_FIRST_FAILURE = false;

    private static final double DEFAULT_EPS = 1.0E-6;

    private static final double DEFAULT_MAX_REL_ERROR = 0.001;

    private static final double DEFAULT_MIN_ABS_ERROR = 1.0E-9;

    static {
        Nd4j.setDataType(DOUBLE);
    }

    @Test
    public void testBasicIris() {
        Nd4j.getRandom().setSeed(12345);
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).dist(new NormalDistribution(0, 1)).updater(new NoOp()).graphBuilder().addInputs("input").addLayer("firstLayer", build(), "input").addLayer("outputLayer", build(), "firstLayer").setOutputs("outputLayer").build();
        ComputationGraph graph = new ComputationGraph(conf);
        graph.init();
        Nd4j.getRandom().setSeed(12345);
        long nParams = graph.numParams();
        INDArray newParams = Nd4j.rand(new long[]{ 1, nParams });
        graph.setParams(newParams);
        DataSet ds = new IrisDataSetIterator(150, 150).next();
        INDArray min = ds.getFeatures().min(0);
        INDArray max = ds.getFeatures().max(0);
        ds.getFeatures().subiRowVector(min).diviRowVector(max.sub(min));
        INDArray input = ds.getFeatures();
        INDArray labels = ds.getLabels();
        if (GradientCheckTestsComputationGraph.PRINT_RESULTS) {
            System.out.println("testBasicIris()");
            for (int j = 0; j < (graph.getNumLayers()); j++)
                System.out.println(((("Layer " + j) + " # params: ") + (graph.getLayer(j).numParams())));

        }
        boolean gradOK = GradientCheckUtil.checkGradients(graph, GradientCheckTestsComputationGraph.DEFAULT_EPS, GradientCheckTestsComputationGraph.DEFAULT_MAX_REL_ERROR, GradientCheckTestsComputationGraph.DEFAULT_MIN_ABS_ERROR, GradientCheckTestsComputationGraph.PRINT_RESULTS, GradientCheckTestsComputationGraph.RETURN_ON_FIRST_FAILURE, new INDArray[]{ input }, new INDArray[]{ labels });
        String msg = "testBasicIris()";
        Assert.assertTrue(msg, gradOK);
        TestUtils.testModelSerialization(graph);
    }

    @Test
    public void testBasicIrisWithMerging() {
        Nd4j.getRandom().setSeed(12345);
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).dist(new NormalDistribution(0, 1)).updater(new NoOp()).graphBuilder().addInputs("input").addLayer("l1", build(), "input").addLayer("l2", build(), "input").addVertex("merge", new MergeVertex(), "l1", "l2").addLayer("outputLayer", build(), "merge").setOutputs("outputLayer").build();
        ComputationGraph graph = new ComputationGraph(conf);
        graph.init();
        int numParams = (((4 * 5) + 5) + ((4 * 5) + 5)) + ((10 * 3) + 3);
        Assert.assertEquals(numParams, graph.numParams());
        Nd4j.getRandom().setSeed(12345);
        long nParams = graph.numParams();
        INDArray newParams = Nd4j.rand(new long[]{ 1, nParams });
        graph.setParams(newParams);
        DataSet ds = new IrisDataSetIterator(150, 150).next();
        INDArray min = ds.getFeatures().min(0);
        INDArray max = ds.getFeatures().max(0);
        ds.getFeatures().subiRowVector(min).diviRowVector(max.sub(min));
        INDArray input = ds.getFeatures();
        INDArray labels = ds.getLabels();
        if (GradientCheckTestsComputationGraph.PRINT_RESULTS) {
            System.out.println("testBasicIrisWithMerging()");
            for (int j = 0; j < (graph.getNumLayers()); j++)
                System.out.println(((("Layer " + j) + " # params: ") + (graph.getLayer(j).numParams())));

        }
        boolean gradOK = GradientCheckUtil.checkGradients(graph, GradientCheckTestsComputationGraph.DEFAULT_EPS, GradientCheckTestsComputationGraph.DEFAULT_MAX_REL_ERROR, GradientCheckTestsComputationGraph.DEFAULT_MIN_ABS_ERROR, GradientCheckTestsComputationGraph.PRINT_RESULTS, GradientCheckTestsComputationGraph.RETURN_ON_FIRST_FAILURE, new INDArray[]{ input }, new INDArray[]{ labels });
        String msg = "testBasicIrisWithMerging()";
        Assert.assertTrue(msg, gradOK);
        TestUtils.testModelSerialization(graph);
    }

    @Test
    public void testBasicIrisWithElementWiseNode() {
        ElementWiseVertex[] ops = new ElementWiseVertex.Op[]{ Op.Add, Op.Subtract, Op.Product, Op.Average, Op.Max };
        for (ElementWiseVertex.Op op : ops) {
            Nd4j.getRandom().setSeed(12345);
            ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).dist(new NormalDistribution(0, 1)).updater(new NoOp()).graphBuilder().addInputs("input").addLayer("l1", build(), "input").addLayer("l2", build(), "input").addVertex("elementwise", new ElementWiseVertex(op), "l1", "l2").addLayer("outputLayer", build(), "elementwise").setOutputs("outputLayer").build();
            ComputationGraph graph = new ComputationGraph(conf);
            graph.init();
            int numParams = (((4 * 5) + 5) + ((4 * 5) + 5)) + ((5 * 3) + 3);
            Assert.assertEquals(numParams, graph.numParams());
            Nd4j.getRandom().setSeed(12345);
            long nParams = graph.numParams();
            INDArray newParams = Nd4j.rand(new long[]{ 1, nParams });
            graph.setParams(newParams);
            DataSet ds = new IrisDataSetIterator(150, 150).next();
            INDArray min = ds.getFeatures().min(0);
            INDArray max = ds.getFeatures().max(0);
            ds.getFeatures().subiRowVector(min).diviRowVector(max.sub(min));
            INDArray input = ds.getFeatures();
            INDArray labels = ds.getLabels();
            if (GradientCheckTestsComputationGraph.PRINT_RESULTS) {
                System.out.println((("testBasicIrisWithElementWiseVertex(op=" + op) + ")"));
                for (int j = 0; j < (graph.getNumLayers()); j++)
                    System.out.println(((("Layer " + j) + " # params: ") + (graph.getLayer(j).numParams())));

            }
            boolean gradOK = GradientCheckUtil.checkGradients(graph, GradientCheckTestsComputationGraph.DEFAULT_EPS, GradientCheckTestsComputationGraph.DEFAULT_MAX_REL_ERROR, GradientCheckTestsComputationGraph.DEFAULT_MIN_ABS_ERROR, GradientCheckTestsComputationGraph.PRINT_RESULTS, GradientCheckTestsComputationGraph.RETURN_ON_FIRST_FAILURE, new INDArray[]{ input }, new INDArray[]{ labels });
            String msg = ("testBasicIrisWithElementWiseVertex(op=" + op) + ")";
            Assert.assertTrue(msg, gradOK);
            TestUtils.testModelSerialization(graph);
        }
    }

    @Test
    public void testBasicIrisWithElementWiseNodeInputSizeGreaterThanTwo() {
        ElementWiseVertex[] ops = new ElementWiseVertex.Op[]{ Op.Add, Op.Product, Op.Average, Op.Max };
        for (ElementWiseVertex.Op op : ops) {
            Nd4j.getRandom().setSeed(12345);
            ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).dist(new NormalDistribution(0, 1)).updater(new NoOp()).graphBuilder().addInputs("input").addLayer("l1", build(), "input").addLayer("l2", build(), "input").addLayer("l3", build(), "input").addVertex("elementwise", new ElementWiseVertex(op), "l1", "l2", "l3").addLayer("outputLayer", build(), "elementwise").setOutputs("outputLayer").build();
            ComputationGraph graph = new ComputationGraph(conf);
            graph.init();
            int numParams = ((((4 * 5) + 5) + ((4 * 5) + 5)) + ((4 * 5) + 5)) + ((5 * 3) + 3);
            Assert.assertEquals(numParams, graph.numParams());
            Nd4j.getRandom().setSeed(12345);
            long nParams = graph.numParams();
            INDArray newParams = Nd4j.rand(new long[]{ 1, nParams });
            graph.setParams(newParams);
            DataSet ds = new IrisDataSetIterator(150, 150).next();
            INDArray min = ds.getFeatures().min(0);
            INDArray max = ds.getFeatures().max(0);
            ds.getFeatures().subiRowVector(min).diviRowVector(max.sub(min));
            INDArray input = ds.getFeatures();
            INDArray labels = ds.getLabels();
            if (GradientCheckTestsComputationGraph.PRINT_RESULTS) {
                System.out.println((("testBasicIrisWithElementWiseVertex(op=" + op) + ")"));
                for (int j = 0; j < (graph.getNumLayers()); j++)
                    System.out.println(((("Layer " + j) + " # params: ") + (graph.getLayer(j).numParams())));

            }
            boolean gradOK = GradientCheckUtil.checkGradients(graph, GradientCheckTestsComputationGraph.DEFAULT_EPS, GradientCheckTestsComputationGraph.DEFAULT_MAX_REL_ERROR, GradientCheckTestsComputationGraph.DEFAULT_MIN_ABS_ERROR, GradientCheckTestsComputationGraph.PRINT_RESULTS, GradientCheckTestsComputationGraph.RETURN_ON_FIRST_FAILURE, new INDArray[]{ input }, new INDArray[]{ labels });
            String msg = ("testBasicIrisWithElementWiseVertex(op=" + op) + ")";
            Assert.assertTrue(msg, gradOK);
            TestUtils.testModelSerialization(graph);
        }
    }

    @Test
    public void testCnnDepthMerge() {
        Nd4j.getRandom().setSeed(12345);
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).dist(new NormalDistribution(0, 0.1)).updater(new NoOp()).graphBuilder().addInputs("input").addLayer("l1", build(), "input").addLayer("l2", build(), "input").addVertex("merge", new MergeVertex(), "l1", "l2").addLayer("outputLayer", build(), "merge").setOutputs("outputLayer").inputPreProcessor("outputLayer", new CnnToFeedForwardPreProcessor(5, 5, 4)).build();
        ComputationGraph graph = new ComputationGraph(conf);
        graph.init();
        Random r = new Random(12345);
        INDArray input = Nd4j.rand(new int[]{ 5, 2, 6, 6 });// Order: examples, channels, height, width

        INDArray labels = Nd4j.zeros(5, 3);
        for (int i = 0; i < 5; i++)
            labels.putScalar(new int[]{ i, r.nextInt(3) }, 1.0);

        if (GradientCheckTestsComputationGraph.PRINT_RESULTS) {
            System.out.println("testCnnDepthMerge()");
            for (int j = 0; j < (graph.getNumLayers()); j++)
                System.out.println(((("Layer " + j) + " # params: ") + (graph.getLayer(j).numParams())));

        }
        boolean gradOK = GradientCheckUtil.checkGradients(graph, GradientCheckTestsComputationGraph.DEFAULT_EPS, GradientCheckTestsComputationGraph.DEFAULT_MAX_REL_ERROR, GradientCheckTestsComputationGraph.DEFAULT_MIN_ABS_ERROR, GradientCheckTestsComputationGraph.PRINT_RESULTS, GradientCheckTestsComputationGraph.RETURN_ON_FIRST_FAILURE, new INDArray[]{ input }, new INDArray[]{ labels });
        String msg = "testCnnDepthMerge()";
        Assert.assertTrue(msg, gradOK);
        TestUtils.testModelSerialization(graph);
    }

    @Test
    public void testLSTMWithMerging() {
        Nd4j.getRandom().setSeed(12345);
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).dist(new UniformDistribution(0.2, 0.6)).updater(new NoOp()).graphBuilder().addInputs("input").setOutputs("out").addLayer("lstm1", build(), "input").addLayer("lstm2", build(), "lstm1").addLayer("dense1", build(), "lstm1").addLayer("lstm3", build(), "dense1").addVertex("merge", new MergeVertex(), "lstm2", "lstm3").addLayer("out", build(), "merge").inputPreProcessor("dense1", new RnnToFeedForwardPreProcessor()).inputPreProcessor("lstm3", new FeedForwardToRnnPreProcessor()).build();
        ComputationGraph graph = new ComputationGraph(conf);
        graph.init();
        Random r = new Random(12345);
        INDArray input = Nd4j.rand(new int[]{ 3, 3, 5 });
        INDArray labels = Nd4j.zeros(3, 3, 5);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 5; j++) {
                labels.putScalar(new int[]{ i, r.nextInt(3), j }, 1.0);
            }
        }
        if (GradientCheckTestsComputationGraph.PRINT_RESULTS) {
            System.out.println("testLSTMWithMerging()");
            for (int j = 0; j < (graph.getNumLayers()); j++)
                System.out.println(((("Layer " + j) + " # params: ") + (graph.getLayer(j).numParams())));

        }
        boolean gradOK = GradientCheckUtil.checkGradients(graph, GradientCheckTestsComputationGraph.DEFAULT_EPS, GradientCheckTestsComputationGraph.DEFAULT_MAX_REL_ERROR, GradientCheckTestsComputationGraph.DEFAULT_MIN_ABS_ERROR, GradientCheckTestsComputationGraph.PRINT_RESULTS, GradientCheckTestsComputationGraph.RETURN_ON_FIRST_FAILURE, new INDArray[]{ input }, new INDArray[]{ labels });
        String msg = "testLSTMWithMerging()";
        Assert.assertTrue(msg, gradOK);
        TestUtils.testModelSerialization(graph);
    }

    @Test
    public void testLSTMWithSubset() {
        Nd4j.getRandom().setSeed(1234);
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(1234).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).dist(new NormalDistribution(0, 1)).updater(new NoOp()).graphBuilder().addInputs("input").setOutputs("out").addLayer("lstm1", build(), "input").addVertex("subset", new SubsetVertex(0, 3), "lstm1").addLayer("out", build(), "subset").build();
        ComputationGraph graph = new ComputationGraph(conf);
        graph.init();
        Random r = new Random(12345);
        INDArray input = Nd4j.rand(new int[]{ 3, 3, 5 });
        INDArray labels = Nd4j.zeros(3, 3, 5);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 5; j++) {
                labels.putScalar(new int[]{ i, r.nextInt(3), j }, 1.0);
            }
        }
        if (GradientCheckTestsComputationGraph.PRINT_RESULTS) {
            System.out.println("testLSTMWithSubset()");
            for (int j = 0; j < (graph.getNumLayers()); j++)
                System.out.println(((("Layer " + j) + " # params: ") + (graph.getLayer(j).numParams())));

        }
        boolean gradOK = GradientCheckUtil.checkGradients(graph, GradientCheckTestsComputationGraph.DEFAULT_EPS, GradientCheckTestsComputationGraph.DEFAULT_MAX_REL_ERROR, GradientCheckTestsComputationGraph.DEFAULT_MIN_ABS_ERROR, GradientCheckTestsComputationGraph.PRINT_RESULTS, GradientCheckTestsComputationGraph.RETURN_ON_FIRST_FAILURE, new INDArray[]{ input }, new INDArray[]{ labels });
        String msg = "testLSTMWithSubset()";
        Assert.assertTrue(msg, gradOK);
        TestUtils.testModelSerialization(graph);
    }

    @Test
    public void testLSTMWithLastTimeStepVertex() {
        Nd4j.getRandom().setSeed(12345);
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).dist(new NormalDistribution(0, 1)).updater(new NoOp()).graphBuilder().addInputs("input").setOutputs("out").addLayer("lstm1", build(), "input").addVertex("lastTS", new LastTimeStepVertex("input"), "lstm1").addLayer("out", build(), "lastTS").build();
        ComputationGraph graph = new ComputationGraph(conf);
        graph.init();
        Random r = new Random(12345);
        INDArray input = Nd4j.rand(new int[]{ 3, 3, 5 });
        INDArray labels = Nd4j.zeros(3, 3);// Here: labels are 2d (due to LastTimeStepVertex)

        for (int i = 0; i < 3; i++) {
            labels.putScalar(new int[]{ i, r.nextInt(3) }, 1.0);
        }
        if (GradientCheckTestsComputationGraph.PRINT_RESULTS) {
            System.out.println("testLSTMWithLastTimeStepVertex()");
            for (int j = 0; j < (graph.getNumLayers()); j++)
                System.out.println(((("Layer " + j) + " # params: ") + (graph.getLayer(j).numParams())));

        }
        // First: test with no input mask array
        boolean gradOK = GradientCheckUtil.checkGradients(graph, GradientCheckTestsComputationGraph.DEFAULT_EPS, GradientCheckTestsComputationGraph.DEFAULT_MAX_REL_ERROR, GradientCheckTestsComputationGraph.DEFAULT_MIN_ABS_ERROR, GradientCheckTestsComputationGraph.PRINT_RESULTS, GradientCheckTestsComputationGraph.RETURN_ON_FIRST_FAILURE, new INDArray[]{ input }, new INDArray[]{ labels });
        String msg = "testLSTMWithLastTimeStepVertex()";
        Assert.assertTrue(msg, gradOK);
        // Second: test with input mask arrays.
        INDArray inMask = Nd4j.zeros(3, 5);
        inMask.putRow(0, Nd4j.create(new double[]{ 1, 1, 1, 0, 0 }));
        inMask.putRow(1, Nd4j.create(new double[]{ 1, 1, 1, 1, 0 }));
        inMask.putRow(2, Nd4j.create(new double[]{ 1, 1, 1, 1, 1 }));
        graph.setLayerMaskArrays(new INDArray[]{ inMask }, null);
        gradOK = GradientCheckUtil.checkGradients(graph, GradientCheckTestsComputationGraph.DEFAULT_EPS, GradientCheckTestsComputationGraph.DEFAULT_MAX_REL_ERROR, GradientCheckTestsComputationGraph.DEFAULT_MIN_ABS_ERROR, GradientCheckTestsComputationGraph.PRINT_RESULTS, GradientCheckTestsComputationGraph.RETURN_ON_FIRST_FAILURE, new INDArray[]{ input }, new INDArray[]{ labels });
        Assert.assertTrue(msg, gradOK);
        TestUtils.testModelSerialization(graph);
    }

    @Test
    public void testLSTMWithDuplicateToTimeSeries() {
        Nd4j.getRandom().setSeed(12345);
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).dist(new NormalDistribution(0, 1)).updater(new NoOp()).graphBuilder().addInputs("input1", "input2").setOutputs("out").addLayer("lstm1", build(), "input1").addLayer("lstm2", build(), "input2").addVertex("lastTS", new LastTimeStepVertex("input2"), "lstm2").addVertex("duplicate", new DuplicateToTimeSeriesVertex("input2"), "lastTS").addLayer("out", build(), "lstm1", "duplicate").build();
        ComputationGraph graph = new ComputationGraph(conf);
        graph.init();
        Random r = new Random(12345);
        INDArray input1 = Nd4j.rand(new int[]{ 3, 3, 5 });
        INDArray input2 = Nd4j.rand(new int[]{ 3, 4, 5 });
        INDArray labels = Nd4j.zeros(3, 3, 5);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 5; j++) {
                labels.putScalar(new int[]{ i, r.nextInt(3), j }, 1.0);
            }
        }
        if (GradientCheckTestsComputationGraph.PRINT_RESULTS) {
            System.out.println("testLSTMWithDuplicateToTimeSeries()");
            for (int j = 0; j < (graph.getNumLayers()); j++)
                System.out.println(((("Layer " + j) + " # params: ") + (graph.getLayer(j).numParams())));

        }
        boolean gradOK = GradientCheckUtil.checkGradients(graph, GradientCheckTestsComputationGraph.DEFAULT_EPS, GradientCheckTestsComputationGraph.DEFAULT_MAX_REL_ERROR, GradientCheckTestsComputationGraph.DEFAULT_MIN_ABS_ERROR, GradientCheckTestsComputationGraph.PRINT_RESULTS, GradientCheckTestsComputationGraph.RETURN_ON_FIRST_FAILURE, new INDArray[]{ input1, input2 }, new INDArray[]{ labels });
        String msg = "testLSTMWithDuplicateToTimeSeries()";
        Assert.assertTrue(msg, gradOK);
        TestUtils.testModelSerialization(graph);
    }

    @Test
    public void testLSTMWithReverseTimeSeriesVertex() {
        Nd4j.getRandom().setSeed(12345);
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).dist(new NormalDistribution(0, 1)).updater(new NoOp()).graphBuilder().addInputs("input").setOutputs("out").addLayer("lstm_a", build(), "input").addVertex("input_rev", new ReverseTimeSeriesVertex("input"), "input").addLayer("lstm_b", build(), "input_rev").addVertex("lstm_b_rev", new ReverseTimeSeriesVertex("input"), "lstm_b").addLayer("out", build(), "lstm_a", "lstm_b_rev").build();
        ComputationGraph graph = new ComputationGraph(conf);
        graph.init();
        Random r = new Random(12345);
        INDArray input = Nd4j.rand(new int[]{ 3, 3, 5 });
        INDArray labels = Nd4j.zeros(3, 3, 5);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 5; j++) {
                labels.putScalar(new int[]{ i, r.nextInt(3), j }, 1.0);
            }
        }
        if (GradientCheckTestsComputationGraph.PRINT_RESULTS) {
            System.out.println("testLSTMWithReverseTimeSeriesVertex()");
            for (int j = 0; j < (graph.getNumLayers()); j++)
                System.out.println(((("Layer " + j) + " # params: ") + (graph.getLayer(j).numParams())));

        }
        boolean gradOK = GradientCheckUtil.checkGradients(graph, GradientCheckTestsComputationGraph.DEFAULT_EPS, GradientCheckTestsComputationGraph.DEFAULT_MAX_REL_ERROR, GradientCheckTestsComputationGraph.DEFAULT_MIN_ABS_ERROR, GradientCheckTestsComputationGraph.PRINT_RESULTS, GradientCheckTestsComputationGraph.RETURN_ON_FIRST_FAILURE, new INDArray[]{ input }, new INDArray[]{ labels });
        String msg = "testLSTMWithDuplicateToTimeSeries()";
        Assert.assertTrue(msg, gradOK);
        // Second: test with input mask arrays.
        INDArray inMask = Nd4j.zeros(3, 5);
        inMask.putRow(0, Nd4j.create(new double[]{ 1, 1, 1, 0, 0 }));
        inMask.putRow(1, Nd4j.create(new double[]{ 1, 1, 0, 1, 0 }));
        inMask.putRow(2, Nd4j.create(new double[]{ 1, 1, 1, 1, 1 }));
        graph.setLayerMaskArrays(new INDArray[]{ inMask }, null);
        gradOK = GradientCheckUtil.checkGradients(graph, GradientCheckTestsComputationGraph.DEFAULT_EPS, GradientCheckTestsComputationGraph.DEFAULT_MAX_REL_ERROR, GradientCheckTestsComputationGraph.DEFAULT_MIN_ABS_ERROR, GradientCheckTestsComputationGraph.PRINT_RESULTS, GradientCheckTestsComputationGraph.RETURN_ON_FIRST_FAILURE, new INDArray[]{ input }, new INDArray[]{ labels });
        Assert.assertTrue(msg, gradOK);
        TestUtils.testModelSerialization(graph);
    }

    @Test
    public void testMultipleInputsLayer() {
        Nd4j.getRandom().setSeed(12345);
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).dist(new NormalDistribution(0, 1)).updater(new NoOp()).activation(TANH).graphBuilder().addInputs("i0", "i1", "i2").addLayer("d0", build(), "i0").addLayer("d1", build(), "i1").addLayer("d2", build(), "i2").addLayer("d3", build(), "d0", "d1", "d2").addLayer("out", build(), "d3").setOutputs("out").build();
        ComputationGraph graph = new ComputationGraph(conf);
        graph.init();
        int[] minibatchSizes = new int[]{ 1, 3 };
        for (int mb : minibatchSizes) {
            INDArray[] inputs = new INDArray[3];
            for (int i = 0; i < 3; i++) {
                inputs[i] = Nd4j.rand(mb, 2);
            }
            INDArray out = Nd4j.rand(mb, 2);
            String msg = "testMultipleInputsLayer() - minibatchSize = " + mb;
            if (GradientCheckTestsComputationGraph.PRINT_RESULTS) {
                System.out.println(msg);
                for (int j = 0; j < (graph.getNumLayers()); j++)
                    System.out.println(((("Layer " + j) + " # params: ") + (graph.getLayer(j).numParams())));

            }
            boolean gradOK = GradientCheckUtil.checkGradients(graph, GradientCheckTestsComputationGraph.DEFAULT_EPS, GradientCheckTestsComputationGraph.DEFAULT_MAX_REL_ERROR, GradientCheckTestsComputationGraph.DEFAULT_MIN_ABS_ERROR, GradientCheckTestsComputationGraph.PRINT_RESULTS, GradientCheckTestsComputationGraph.RETURN_ON_FIRST_FAILURE, inputs, new INDArray[]{ out });
            Assert.assertTrue(msg, gradOK);
            TestUtils.testModelSerialization(graph);
        }
    }

    @Test
    public void testMultipleOutputsLayer() {
        Nd4j.getRandom().setSeed(12345);
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).dist(new NormalDistribution(0, 1)).updater(new NoOp()).activation(TANH).graphBuilder().addInputs("i0").addLayer("d0", build(), "i0").addLayer("d1", build(), "d0").addLayer("d2", build(), "d0").addLayer("d3", build(), "d0").addLayer("out", build(), "d1", "d2", "d3").setOutputs("out").build();
        ComputationGraph graph = new ComputationGraph(conf);
        graph.init();
        int[] minibatchSizes = new int[]{ 1, 3 };
        for (int mb : minibatchSizes) {
            INDArray input = Nd4j.rand(mb, 2);
            INDArray out = Nd4j.rand(mb, 2);
            String msg = "testMultipleOutputsLayer() - minibatchSize = " + mb;
            if (GradientCheckTestsComputationGraph.PRINT_RESULTS) {
                System.out.println(msg);
                for (int j = 0; j < (graph.getNumLayers()); j++)
                    System.out.println(((("Layer " + j) + " # params: ") + (graph.getLayer(j).numParams())));

            }
            boolean gradOK = GradientCheckUtil.checkGradients(graph, GradientCheckTestsComputationGraph.DEFAULT_EPS, GradientCheckTestsComputationGraph.DEFAULT_MAX_REL_ERROR, GradientCheckTestsComputationGraph.DEFAULT_MIN_ABS_ERROR, GradientCheckTestsComputationGraph.PRINT_RESULTS, GradientCheckTestsComputationGraph.RETURN_ON_FIRST_FAILURE, new INDArray[]{ input }, new INDArray[]{ out });
            Assert.assertTrue(msg, gradOK);
            TestUtils.testModelSerialization(graph);
        }
    }

    @Test
    public void testMultipleOutputsMergeVertex() {
        Nd4j.getRandom().setSeed(12345);
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).dist(new NormalDistribution(0, 1)).updater(new NoOp()).activation(TANH).graphBuilder().addInputs("i0", "i1", "i2").addLayer("d0", build(), "i0").addLayer("d1", build(), "i1").addLayer("d2", build(), "i2").addVertex("m", new MergeVertex(), "d0", "d1", "d2").addLayer("D0", build(), "m").addLayer("D1", build(), "m").addLayer("D2", build(), "m").addLayer("out", build(), "D0", "D1", "D2").setOutputs("out").build();
        ComputationGraph graph = new ComputationGraph(conf);
        graph.init();
        int[] minibatchSizes = new int[]{ 1, 3 };
        for (int mb : minibatchSizes) {
            INDArray[] input = new INDArray[3];
            for (int i = 0; i < 3; i++) {
                input[i] = Nd4j.rand(mb, 2);
            }
            INDArray out = Nd4j.rand(mb, 2);
            String msg = "testMultipleOutputsMergeVertex() - minibatchSize = " + mb;
            if (GradientCheckTestsComputationGraph.PRINT_RESULTS) {
                System.out.println(msg);
                for (int j = 0; j < (graph.getNumLayers()); j++)
                    System.out.println(((("Layer " + j) + " # params: ") + (graph.getLayer(j).numParams())));

            }
            boolean gradOK = GradientCheckUtil.checkGradients(graph, GradientCheckTestsComputationGraph.DEFAULT_EPS, GradientCheckTestsComputationGraph.DEFAULT_MAX_REL_ERROR, GradientCheckTestsComputationGraph.DEFAULT_MIN_ABS_ERROR, GradientCheckTestsComputationGraph.PRINT_RESULTS, GradientCheckTestsComputationGraph.RETURN_ON_FIRST_FAILURE, input, new INDArray[]{ out });
            Assert.assertTrue(msg, gradOK);
            TestUtils.testModelSerialization(graph);
        }
    }

    @Test
    public void testMultipleOutputsMergeCnn() {
        int inH = 7;
        int inW = 7;
        Nd4j.getRandom().setSeed(12345);
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).dist(new NormalDistribution(0, 1)).updater(new NoOp()).activation(TANH).graphBuilder().addInputs("input").addLayer("l0", build(), "input").addLayer("l1", build(), "l0").addLayer("l2", build(), "l0").addVertex("m", new MergeVertex(), "l1", "l2").addLayer("l3", build(), "m").addLayer("l4", build(), "m").addLayer("out", build(), "l3", "l4").setOutputs("out").setInputTypes(InputType.convolutional(inH, inW, 2)).build();
        ComputationGraph graph = new ComputationGraph(conf);
        graph.init();
        int[] minibatchSizes = new int[]{ 1, 3 };
        for (int mb : minibatchSizes) {
            INDArray input = Nd4j.rand(new int[]{ mb, 2, inH, inW }).muli(4);// Order: examples, channels, height, width

            INDArray out = Nd4j.rand(mb, 2);
            String msg = "testMultipleOutputsMergeVertex() - minibatchSize = " + mb;
            if (GradientCheckTestsComputationGraph.PRINT_RESULTS) {
                System.out.println(msg);
                for (int j = 0; j < (graph.getNumLayers()); j++)
                    System.out.println(((("Layer " + j) + " # params: ") + (graph.getLayer(j).numParams())));

            }
            boolean gradOK = GradientCheckUtil.checkGradients(graph, GradientCheckTestsComputationGraph.DEFAULT_EPS, GradientCheckTestsComputationGraph.DEFAULT_MAX_REL_ERROR, GradientCheckTestsComputationGraph.DEFAULT_MIN_ABS_ERROR, GradientCheckTestsComputationGraph.PRINT_RESULTS, GradientCheckTestsComputationGraph.RETURN_ON_FIRST_FAILURE, new INDArray[]{ input }, new INDArray[]{ out });
            Assert.assertTrue(msg, gradOK);
            TestUtils.testModelSerialization(graph);
        }
    }

    @Test
    public void testBasicIrisTripletStackingL2Loss() {
        Nd4j.getRandom().setSeed(12345);
        ComputationGraphConfiguration conf = // x - x+
        // x - x-
        new NeuralNetConfiguration.Builder().seed(12345).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).dist(new NormalDistribution(0, 1)).updater(new NoOp()).graphBuilder().addInputs("input1", "input2", "input3").addVertex("stack1", new StackVertex(), "input1", "input2", "input3").addLayer("l1", build(), "stack1").addVertex("unstack0", new UnstackVertex(0, 3), "l1").addVertex("unstack1", new UnstackVertex(1, 3), "l1").addVertex("unstack2", new UnstackVertex(2, 3), "l1").addVertex("l2-1", new L2Vertex(), "unstack1", "unstack0").addVertex("l2-2", new L2Vertex(), "unstack1", "unstack2").addLayer("lossLayer", build(), "l2-1", "l2-2").setOutputs("lossLayer").build();
        ComputationGraph graph = new ComputationGraph(conf);
        graph.init();
        int numParams = (4 * 5) + 5;
        Assert.assertEquals(numParams, graph.numParams());
        Nd4j.getRandom().setSeed(12345);
        long nParams = graph.numParams();
        INDArray newParams = Nd4j.rand(new long[]{ 1, nParams });
        graph.setParams(newParams);
        INDArray pos = Nd4j.rand(150, 4);
        INDArray anc = Nd4j.rand(150, 4);
        INDArray neg = Nd4j.rand(150, 4);
        INDArray labels = Nd4j.zeros(150, 2);
        Random r = new Random(12345);
        for (int i = 0; i < 150; i++) {
            labels.putScalar(i, r.nextInt(2), 1.0);
        }
        Map<String, INDArray> out = graph.feedForward(new INDArray[]{ pos, anc, neg }, true);
        for (String s : out.keySet()) {
            System.out.println(((s + "\t") + (Arrays.toString(out.get(s).shape()))));
        }
        if (GradientCheckTestsComputationGraph.PRINT_RESULTS) {
            System.out.println("testBasicIrisTripletStackingL2Loss()");
            for (int j = 0; j < (graph.getNumLayers()); j++)
                System.out.println(((("Layer " + j) + " # params: ") + (graph.getLayer(j).numParams())));

        }
        boolean gradOK = GradientCheckUtil.checkGradients(graph, GradientCheckTestsComputationGraph.DEFAULT_EPS, GradientCheckTestsComputationGraph.DEFAULT_MAX_REL_ERROR, GradientCheckTestsComputationGraph.DEFAULT_MIN_ABS_ERROR, GradientCheckTestsComputationGraph.PRINT_RESULTS, GradientCheckTestsComputationGraph.RETURN_ON_FIRST_FAILURE, new INDArray[]{ pos, anc, neg }, new INDArray[]{ labels });
        String msg = "testBasicIrisTripletStackingL2Loss()";
        Assert.assertTrue(msg, gradOK);
        TestUtils.testModelSerialization(graph);
    }

    @Test
    public void testBasicCenterLoss() {
        Nd4j.getRandom().setSeed(12345);
        int numLabels = 2;
        boolean[] trainFirst = new boolean[]{ false, true };
        for (boolean train : trainFirst) {
            for (double lambda : new double[]{ 0.0, 0.5, 2.0 }) {
                ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).dist(new GaussianDistribution(0, 1)).updater(new NoOp()).graphBuilder().addInputs("input1").addLayer("l1", build(), "input1").addLayer("cl", build(), "l1").setOutputs("cl").build();
                ComputationGraph graph = new ComputationGraph(conf);
                graph.init();
                INDArray example = Nd4j.rand(150, 4);
                INDArray labels = Nd4j.zeros(150, numLabels);
                Random r = new Random(12345);
                for (int i = 0; i < 150; i++) {
                    labels.putScalar(i, r.nextInt(numLabels), 1.0);
                }
                if (train) {
                    for (int i = 0; i < 10; i++) {
                        INDArray f = Nd4j.rand(10, 4);
                        INDArray l = Nd4j.zeros(10, numLabels);
                        for (int j = 0; j < 10; j++) {
                            l.putScalar(j, r.nextInt(numLabels), 1.0);
                        }
                        graph.fit(new INDArray[]{ f }, new INDArray[]{ l });
                    }
                }
                String msg = (("testBasicCenterLoss() - lambda = " + lambda) + ", trainFirst = ") + train;
                if (GradientCheckTestsComputationGraph.PRINT_RESULTS) {
                    System.out.println(msg);
                    for (int j = 0; j < (graph.getNumLayers()); j++)
                        System.out.println(((("Layer " + j) + " # params: ") + (graph.getLayer(j).numParams())));

                }
                boolean gradOK = GradientCheckUtil.checkGradients(graph, GradientCheckTestsComputationGraph.DEFAULT_EPS, GradientCheckTestsComputationGraph.DEFAULT_MAX_REL_ERROR, GradientCheckTestsComputationGraph.DEFAULT_MIN_ABS_ERROR, GradientCheckTestsComputationGraph.PRINT_RESULTS, GradientCheckTestsComputationGraph.RETURN_ON_FIRST_FAILURE, new INDArray[]{ example }, new INDArray[]{ labels });
                Assert.assertTrue(msg, gradOK);
                TestUtils.testModelSerialization(graph);
            }
        }
    }

    @Test
    public void testCnnPoolCenterLoss() {
        Nd4j.getRandom().setSeed(12345);
        int numLabels = 2;
        boolean[] trainFirst = new boolean[]{ false, true };
        int inputH = 5;
        int inputW = 4;
        int inputDepth = 3;
        for (boolean train : trainFirst) {
            for (double lambda : new double[]{ 0.0, 0.5, 2.0 }) {
                MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().updater(new NoOp()).dist(new NormalDistribution(0, 1.0)).seed(12345L).list().layer(0, build()).layer(1, build()).layer(2, build()).setInputType(InputType.convolutional(inputH, inputW, inputDepth)).build();
                MultiLayerNetwork net = new MultiLayerNetwork(conf);
                net.init();
                INDArray example = Nd4j.rand(new int[]{ 150, inputDepth, inputH, inputW });
                INDArray labels = Nd4j.zeros(150, numLabels);
                Random r = new Random(12345);
                for (int i = 0; i < 150; i++) {
                    labels.putScalar(i, r.nextInt(numLabels), 1.0);
                }
                if (train) {
                    for (int i = 0; i < 10; i++) {
                        INDArray f = Nd4j.rand(new int[]{ 10, inputDepth, inputH, inputW });
                        INDArray l = Nd4j.zeros(10, numLabels);
                        for (int j = 0; j < 10; j++) {
                            l.putScalar(j, r.nextInt(numLabels), 1.0);
                        }
                        net.fit(f, l);
                    }
                }
                String msg = "testBasicCenterLoss() - trainFirst = " + train;
                if (GradientCheckTestsComputationGraph.PRINT_RESULTS) {
                    System.out.println(msg);
                    for (int j = 0; j < (net.getnLayers()); j++)
                        System.out.println(((("Layer " + j) + " # params: ") + (net.getLayer(j).numParams())));

                }
                boolean gradOK = GradientCheckUtil.checkGradients(net, GradientCheckTestsComputationGraph.DEFAULT_EPS, GradientCheckTestsComputationGraph.DEFAULT_MAX_REL_ERROR, GradientCheckTestsComputationGraph.DEFAULT_MIN_ABS_ERROR, GradientCheckTestsComputationGraph.PRINT_RESULTS, GradientCheckTestsComputationGraph.RETURN_ON_FIRST_FAILURE, example, labels);
                Assert.assertTrue(msg, gradOK);
                TestUtils.testModelSerialization(net);
            }
        }
    }

    @Test
    public void testBasicL2() {
        Nd4j.getRandom().setSeed(12345);
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).dist(new NormalDistribution(0, 1)).activation(TANH).updater(new NoOp()).graphBuilder().addInputs("in1", "in2").addLayer("d0", build(), "in1").addLayer("d1", build(), "in2").addVertex("l2", new L2Vertex(), "d0", "d1").addLayer("out", build(), "l2").setOutputs("out").build();
        ComputationGraph graph = new ComputationGraph(conf);
        graph.init();
        Nd4j.getRandom().setSeed(12345);
        long nParams = graph.numParams();
        INDArray newParams = Nd4j.rand(new long[]{ 1, nParams });
        graph.setParams(newParams);
        int[] mbSizes = new int[]{ 1, 3, 10 };
        for (int minibatch : mbSizes) {
            INDArray in1 = Nd4j.rand(minibatch, 2);
            INDArray in2 = Nd4j.rand(minibatch, 2);
            INDArray labels = Nd4j.rand(minibatch, 1);
            String testName = "testBasicL2() - minibatch = " + minibatch;
            if (GradientCheckTestsComputationGraph.PRINT_RESULTS) {
                System.out.println(testName);
                for (int j = 0; j < (graph.getNumLayers()); j++)
                    System.out.println(((("Layer " + j) + " # params: ") + (graph.getLayer(j).numParams())));

            }
            boolean gradOK = GradientCheckUtil.checkGradients(graph, GradientCheckTestsComputationGraph.DEFAULT_EPS, GradientCheckTestsComputationGraph.DEFAULT_MAX_REL_ERROR, GradientCheckTestsComputationGraph.DEFAULT_MIN_ABS_ERROR, GradientCheckTestsComputationGraph.PRINT_RESULTS, GradientCheckTestsComputationGraph.RETURN_ON_FIRST_FAILURE, new INDArray[]{ in1, in2 }, new INDArray[]{ labels });
            Assert.assertTrue(testName, gradOK);
            TestUtils.testModelSerialization(graph);
        }
    }

    @Test
    public void testBasicStackUnstack() {
        int layerSizes = 2;
        Nd4j.getRandom().setSeed(12345);
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).dist(new NormalDistribution(0, 1)).activation(TANH).updater(new NoOp()).graphBuilder().addInputs("in1", "in2").addLayer("d0", build(), "in1").addLayer("d1", build(), "in2").addVertex("stack", new StackVertex(), "d0", "d1").addLayer("d2", build(), "stack").addVertex("u1", new UnstackVertex(0, 2), "d2").addVertex("u2", new UnstackVertex(1, 2), "d2").addLayer("out1", build(), "u1").addLayer("out2", build(), "u2").setOutputs("out1", "out2").build();
        ComputationGraph graph = new ComputationGraph(conf);
        graph.init();
        Nd4j.getRandom().setSeed(12345);
        long nParams = graph.numParams();
        INDArray newParams = Nd4j.rand(new long[]{ 1, nParams });
        graph.setParams(newParams);
        int[] mbSizes = new int[]{ 1, 3, 10 };
        for (int minibatch : mbSizes) {
            INDArray in1 = Nd4j.rand(minibatch, layerSizes);
            INDArray in2 = Nd4j.rand(minibatch, layerSizes);
            INDArray labels1 = Nd4j.rand(minibatch, 2);
            INDArray labels2 = Nd4j.rand(minibatch, 2);
            String testName = "testBasicStackUnstack() - minibatch = " + minibatch;
            if (GradientCheckTestsComputationGraph.PRINT_RESULTS) {
                System.out.println(testName);
                for (int j = 0; j < (graph.getNumLayers()); j++)
                    System.out.println(((("Layer " + j) + " # params: ") + (graph.getLayer(j).numParams())));

            }
            boolean gradOK = GradientCheckUtil.checkGradients(graph, GradientCheckTestsComputationGraph.DEFAULT_EPS, GradientCheckTestsComputationGraph.DEFAULT_MAX_REL_ERROR, GradientCheckTestsComputationGraph.DEFAULT_MIN_ABS_ERROR, GradientCheckTestsComputationGraph.PRINT_RESULTS, GradientCheckTestsComputationGraph.RETURN_ON_FIRST_FAILURE, new INDArray[]{ in1, in2 }, new INDArray[]{ labels1, labels2 });
            Assert.assertTrue(testName, gradOK);
            TestUtils.testModelSerialization(graph);
        }
    }

    @Test
    public void testBasicStackUnstackDebug() {
        Nd4j.getRandom().setSeed(12345);
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).dist(new NormalDistribution(0, 1)).activation(TANH).updater(new NoOp()).graphBuilder().addInputs("in1", "in2").addLayer("d0", build(), "in1").addLayer("d1", build(), "in2").addVertex("stack", new StackVertex(), "d0", "d1").addVertex("u0", new UnstackVertex(0, 2), "stack").addVertex("u1", new UnstackVertex(1, 2), "stack").addLayer("out1", build(), "u0").addLayer("out2", build(), "u1").setOutputs("out1", "out2").build();
        ComputationGraph graph = new ComputationGraph(conf);
        graph.init();
        Nd4j.getRandom().setSeed(12345);
        long nParams = graph.numParams();
        INDArray newParams = Nd4j.rand(new long[]{ 1, nParams });
        graph.setParams(newParams);
        int[] mbSizes = new int[]{ 1, 3, 10 };
        for (int minibatch : mbSizes) {
            INDArray in1 = Nd4j.rand(minibatch, 2);
            INDArray in2 = Nd4j.rand(minibatch, 2);
            INDArray labels1 = Nd4j.rand(minibatch, 2);
            INDArray labels2 = Nd4j.rand(minibatch, 2);
            String testName = "testBasicStackUnstack() - minibatch = " + minibatch;
            if (GradientCheckTestsComputationGraph.PRINT_RESULTS) {
                System.out.println(testName);
                for (int j = 0; j < (graph.getNumLayers()); j++)
                    System.out.println(((("Layer " + j) + " # params: ") + (graph.getLayer(j).numParams())));

            }
            boolean gradOK = GradientCheckUtil.checkGradients(graph, GradientCheckTestsComputationGraph.DEFAULT_EPS, GradientCheckTestsComputationGraph.DEFAULT_MAX_REL_ERROR, GradientCheckTestsComputationGraph.DEFAULT_MIN_ABS_ERROR, GradientCheckTestsComputationGraph.PRINT_RESULTS, GradientCheckTestsComputationGraph.RETURN_ON_FIRST_FAILURE, new INDArray[]{ in1, in2 }, new INDArray[]{ labels1, labels2 });
            Assert.assertTrue(testName, gradOK);
            TestUtils.testModelSerialization(graph);
        }
    }

    @Test
    public void testBasicStackUnstackVariableLengthTS() {
        int layerSizes = 2;
        Nd4j.getRandom().setSeed(12345);
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).dist(new NormalDistribution(0, 1)).activation(TANH).updater(new NoOp()).graphBuilder().addInputs("in1", "in2").addLayer("d0", build(), "in1").addLayer("d1", build(), "in2").addVertex("stack", new StackVertex(), "d0", "d1").addLayer("d2", build(), "stack").addVertex("u1", new UnstackVertex(0, 2), "d2").addVertex("u2", new UnstackVertex(1, 2), "d2").addLayer("p1", build(), "u1").addLayer("p2", build(), "u2").addLayer("out1", build(), "p1").addLayer("out2", build(), "p2").setOutputs("out1", "out2").build();
        ComputationGraph graph = new ComputationGraph(conf);
        graph.init();
        Nd4j.getRandom().setSeed(12345);
        long nParams = graph.numParams();
        INDArray newParams = Nd4j.rand(new long[]{ 1, nParams });
        graph.setParams(newParams);
        int[] mbSizes = new int[]{ 1, 3, 10 };
        for (int minibatch : mbSizes) {
            INDArray in1 = Nd4j.rand(new int[]{ minibatch, layerSizes, 4 });
            INDArray in2 = Nd4j.rand(new int[]{ minibatch, layerSizes, 5 });
            INDArray inMask1 = Nd4j.zeros(minibatch, 4);
            inMask1.get(NDArrayIndex.all(), NDArrayIndex.interval(0, 3)).assign(1);
            INDArray inMask2 = Nd4j.zeros(minibatch, 5);
            inMask2.get(NDArrayIndex.all(), NDArrayIndex.interval(0, 4)).assign(1);
            INDArray labels1 = Nd4j.rand(new int[]{ minibatch, 2 });
            INDArray labels2 = Nd4j.rand(new int[]{ minibatch, 2 });
            String testName = "testBasicStackUnstackVariableLengthTS() - minibatch = " + minibatch;
            if (GradientCheckTestsComputationGraph.PRINT_RESULTS) {
                System.out.println(testName);
                for (int j = 0; j < (graph.getNumLayers()); j++)
                    System.out.println(((("Layer " + j) + " # params: ") + (graph.getLayer(j).numParams())));

            }
            graph.setLayerMaskArrays(new INDArray[]{ inMask1, inMask2 }, null);
            boolean gradOK = GradientCheckUtil.checkGradients(graph, GradientCheckTestsComputationGraph.DEFAULT_EPS, GradientCheckTestsComputationGraph.DEFAULT_MAX_REL_ERROR, GradientCheckTestsComputationGraph.DEFAULT_MIN_ABS_ERROR, GradientCheckTestsComputationGraph.PRINT_RESULTS, GradientCheckTestsComputationGraph.RETURN_ON_FIRST_FAILURE, new INDArray[]{ in1, in2 }, new INDArray[]{ labels1, labels2 }, new INDArray[]{ inMask1, inMask2 }, null);
            Assert.assertTrue(testName, gradOK);
            TestUtils.testModelSerialization(graph);
        }
    }

    @Test
    public void testBasicTwoOutputs() {
        Nd4j.getRandom().setSeed(12345);
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).dist(new NormalDistribution(0, 1)).activation(TANH).updater(new NoOp()).graphBuilder().addInputs("in1", "in2").addLayer("d0", build(), "in1").addLayer("d1", build(), "in2").addLayer("out1", build(), "d0").addLayer("out2", build(), "d1").setOutputs("out1", "out2").build();
        ComputationGraph graph = new ComputationGraph(conf);
        graph.init();
        System.out.println(("Num layers: " + (graph.getNumLayers())));
        System.out.println(("Num params: " + (graph.numParams())));
        Nd4j.getRandom().setSeed(12345);
        long nParams = graph.numParams();
        INDArray newParams = Nd4j.rand(new long[]{ 1, nParams });
        graph.setParams(newParams);
        int[] mbSizes = new int[]{ 1, 3, 10 };
        for (int minibatch : mbSizes) {
            INDArray in1 = Nd4j.rand(minibatch, 2);
            INDArray in2 = Nd4j.rand(minibatch, 2);
            INDArray labels1 = Nd4j.rand(minibatch, 2);
            INDArray labels2 = Nd4j.rand(minibatch, 2);
            String testName = "testBasicStackUnstack() - minibatch = " + minibatch;
            if (GradientCheckTestsComputationGraph.PRINT_RESULTS) {
                System.out.println(testName);
                for (int j = 0; j < (graph.getNumLayers()); j++)
                    System.out.println(((("Layer " + j) + " # params: ") + (graph.getLayer(j).numParams())));

            }
            boolean gradOK = GradientCheckUtil.checkGradients(graph, GradientCheckTestsComputationGraph.DEFAULT_EPS, GradientCheckTestsComputationGraph.DEFAULT_MAX_REL_ERROR, GradientCheckTestsComputationGraph.DEFAULT_MIN_ABS_ERROR, GradientCheckTestsComputationGraph.PRINT_RESULTS, GradientCheckTestsComputationGraph.RETURN_ON_FIRST_FAILURE, new INDArray[]{ in1, in2 }, new INDArray[]{ labels1, labels2 });
            Assert.assertTrue(testName, gradOK);
            TestUtils.testModelSerialization(graph);
        }
    }

    @Test
    public void testL2NormalizeVertex2d() {
        Nd4j.getRandom().setSeed(12345);
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).dist(new NormalDistribution(0, 1)).activation(TANH).updater(new NoOp()).graphBuilder().addInputs("in1").addLayer("d1", build(), "in1").addVertex("norm", new L2NormalizeVertex(), "d1").addLayer("out1", build(), "norm").setOutputs("out1").build();
        ComputationGraph graph = new ComputationGraph(conf);
        graph.init();
        int[] mbSizes = new int[]{ 1, 3, 10 };
        for (int minibatch : mbSizes) {
            INDArray in1 = Nd4j.rand(minibatch, 2);
            INDArray labels1 = Nd4j.rand(minibatch, 2);
            String testName = "testL2NormalizeVertex2d() - minibatch = " + minibatch;
            if (GradientCheckTestsComputationGraph.PRINT_RESULTS) {
                System.out.println(testName);
                for (int j = 0; j < (graph.getNumLayers()); j++)
                    System.out.println(((("Layer " + j) + " # params: ") + (graph.getLayer(j).numParams())));

            }
            boolean gradOK = GradientCheckUtil.checkGradients(graph, GradientCheckTestsComputationGraph.DEFAULT_EPS, GradientCheckTestsComputationGraph.DEFAULT_MAX_REL_ERROR, GradientCheckTestsComputationGraph.DEFAULT_MIN_ABS_ERROR, GradientCheckTestsComputationGraph.PRINT_RESULTS, GradientCheckTestsComputationGraph.RETURN_ON_FIRST_FAILURE, new INDArray[]{ in1 }, new INDArray[]{ labels1 });
            Assert.assertTrue(testName, gradOK);
            TestUtils.testModelSerialization(graph);
        }
    }

    @Test
    public void testL2NormalizeVertex4d() {
        Nd4j.getRandom().setSeed(12345);
        int h = 4;
        int w = 4;
        int dIn = 2;
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).dist(new NormalDistribution(0, 1)).activation(TANH).updater(new NoOp()).graphBuilder().addInputs("in1").addLayer("d1", build(), "in1").addVertex("norm", new L2NormalizeVertex(), "d1").addLayer("out1", build(), "norm").setOutputs("out1").setInputTypes(InputType.convolutional(h, w, dIn)).build();
        ComputationGraph graph = new ComputationGraph(conf);
        graph.init();
        int[] mbSizes = new int[]{ 1, 3, 10 };
        for (int minibatch : mbSizes) {
            INDArray in1 = Nd4j.rand(new int[]{ minibatch, dIn, h, w });
            INDArray labels1 = Nd4j.rand(minibatch, 2);
            String testName = "testL2NormalizeVertex4d() - minibatch = " + minibatch;
            if (GradientCheckTestsComputationGraph.PRINT_RESULTS) {
                System.out.println(testName);
                for (int j = 0; j < (graph.getNumLayers()); j++)
                    System.out.println(((("Layer " + j) + " # params: ") + (graph.getLayer(j).numParams())));

            }
            boolean gradOK = GradientCheckUtil.checkGradients(graph, GradientCheckTestsComputationGraph.DEFAULT_EPS, GradientCheckTestsComputationGraph.DEFAULT_MAX_REL_ERROR, GradientCheckTestsComputationGraph.DEFAULT_MIN_ABS_ERROR, GradientCheckTestsComputationGraph.PRINT_RESULTS, GradientCheckTestsComputationGraph.RETURN_ON_FIRST_FAILURE, new INDArray[]{ in1 }, new INDArray[]{ labels1 });
            Assert.assertTrue(testName, gradOK);
            TestUtils.testModelSerialization(graph);
        }
    }

    @Test
    public void testGraphEmbeddingLayerSimple() {
        Random r = new Random(12345);
        int nExamples = 5;
        INDArray input = Nd4j.zeros(nExamples, 1);
        INDArray labels = Nd4j.zeros(nExamples, 3);
        for (int i = 0; i < nExamples; i++) {
            input.putScalar(i, r.nextInt(4));
            labels.putScalar(new int[]{ i, r.nextInt(3) }, 1.0);
        }
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().l2(0.2).l1(0.1).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).seed(12345L).updater(new NoOp()).graphBuilder().addInputs("in").addLayer("0", build(), "in").addLayer("1", build(), "0").setOutputs("1").build();
        ComputationGraph cg = new ComputationGraph(conf);
        cg.init();
        if (GradientCheckTestsComputationGraph.PRINT_RESULTS) {
            System.out.println("testGraphEmbeddingLayerSimple");
            for (int j = 0; j < (cg.getNumLayers()); j++)
                System.out.println(((("Layer " + j) + " # params: ") + (cg.getLayer(j).numParams())));

        }
        boolean gradOK = GradientCheckUtil.checkGradients(cg, GradientCheckTestsComputationGraph.DEFAULT_EPS, GradientCheckTestsComputationGraph.DEFAULT_MAX_REL_ERROR, GradientCheckTestsComputationGraph.DEFAULT_MIN_ABS_ERROR, GradientCheckTestsComputationGraph.PRINT_RESULTS, GradientCheckTestsComputationGraph.RETURN_ON_FIRST_FAILURE, new INDArray[]{ input }, new INDArray[]{ labels });
        String msg = "testGraphEmbeddingLayerSimple";
        Assert.assertTrue(msg, gradOK);
        TestUtils.testModelSerialization(cg);
    }
}

