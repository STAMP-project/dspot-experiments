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
import Activation.SOFTMAX;
import Activation.TANH;
import DataType.DOUBLE;
import LossFunctions.LossFunction;
import LossFunctions.LossFunction.MCXENT;
import MultiLayerConfiguration.Builder;
import OpExecutioner.ProfilingMode.NAN_PANIC;
import OptimizationAlgorithm.CONJUGATE_GRADIENT;
import OptimizationAlgorithm.LINE_GRADIENT_DESCENT;
import WeightInit.XAVIER;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.TestUtils;
import org.deeplearning4j.datasets.iterator.impl.IrisDataSetIterator;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.distribution.NormalDistribution;
import org.deeplearning4j.nn.conf.distribution.UniformDistribution;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.NoOp;
import org.nd4j.linalg.lossfunctions.LossFunctions;


/**
 *
 */
public class BNGradientCheckTest extends BaseDL4JTest {
    private static final boolean PRINT_RESULTS = true;

    private static final boolean RETURN_ON_FIRST_FAILURE = false;

    private static final double DEFAULT_EPS = 1.0E-5;

    private static final double DEFAULT_MAX_REL_ERROR = 1.0E-5;

    private static final double DEFAULT_MIN_ABS_ERROR = 1.0E-9;

    static {
        Nd4j.setDataType(DOUBLE);
    }

    @Test
    public void testGradient2dSimple() {
        DataNormalization scaler = new NormalizerMinMaxScaler();
        DataSetIterator iter = new IrisDataSetIterator(150, 150);
        scaler.fit(iter);
        iter.setPreProcessor(scaler);
        DataSet ds = iter.next();
        INDArray input = ds.getFeatures();
        INDArray labels = ds.getLabels();
        for (boolean useLogStd : new boolean[]{ true, false }) {
            MultiLayerConfiguration.Builder builder = new NeuralNetConfiguration.Builder().updater(new NoOp()).seed(12345L).dist(new NormalDistribution(0, 1)).list().layer(0, new DenseLayer.Builder().nIn(4).nOut(3).activation(IDENTITY).build()).layer(1, new BatchNormalization.Builder().useLogStd(useLogStd).nOut(3).build()).layer(2, new ActivationLayer.Builder().activation(TANH).build()).layer(3, new OutputLayer.Builder(LossFunction.MCXENT).activation(SOFTMAX).nIn(3).nOut(3).build());
            MultiLayerNetwork mln = new MultiLayerNetwork(builder.build());
            mln.init();
            if (BNGradientCheckTest.PRINT_RESULTS) {
                for (int j = 0; j < (mln.getnLayers()); j++)
                    System.out.println(((("Layer " + j) + " # params: ") + (mln.getLayer(j).numParams())));

            }
            // Mean and variance vars are not gradient checkable; mean/variance "gradient" is used to implement running mean/variance calc
            // i.e., runningMean = decay * runningMean + (1-decay) * batchMean
            // However, numerical gradient will be 0 as forward pass doesn't depend on this "parameter"
            Set<String> excludeParams = new HashSet<>(Arrays.asList("1_mean", "1_var", "1_log10stdev"));
            boolean gradOK = GradientCheckUtil.checkGradients(mln, BNGradientCheckTest.DEFAULT_EPS, BNGradientCheckTest.DEFAULT_MAX_REL_ERROR, BNGradientCheckTest.DEFAULT_MIN_ABS_ERROR, BNGradientCheckTest.PRINT_RESULTS, BNGradientCheckTest.RETURN_ON_FIRST_FAILURE, input, labels, excludeParams);
            Assert.assertTrue(gradOK);
            TestUtils.testModelSerialization(mln);
        }
    }

    @Test
    public void testGradientCnnSimple() {
        Nd4j.getRandom().setSeed(12345);
        int minibatch = 10;
        int depth = 1;
        int hw = 4;
        int nOut = 4;
        INDArray input = Nd4j.rand(new int[]{ minibatch, depth, hw, hw });
        INDArray labels = Nd4j.zeros(minibatch, nOut);
        Random r = new Random(12345);
        for (int i = 0; i < minibatch; i++) {
            labels.putScalar(i, r.nextInt(nOut), 1.0);
        }
        for (boolean useLogStd : new boolean[]{ true, false }) {
            MultiLayerConfiguration.Builder builder = new NeuralNetConfiguration.Builder().updater(new NoOp()).seed(12345L).dist(new NormalDistribution(0, 2)).list().layer(0, new ConvolutionLayer.Builder().kernelSize(2, 2).stride(1, 1).nIn(depth).nOut(2).activation(IDENTITY).build()).layer(1, new BatchNormalization.Builder().useLogStd(useLogStd).build()).layer(2, new ActivationLayer.Builder().activation(TANH).build()).layer(3, new OutputLayer.Builder(LossFunction.MCXENT).activation(SOFTMAX).nOut(nOut).build()).setInputType(InputType.convolutional(hw, hw, depth));
            MultiLayerNetwork mln = new MultiLayerNetwork(builder.build());
            mln.init();
            if (BNGradientCheckTest.PRINT_RESULTS) {
                for (int j = 0; j < (mln.getnLayers()); j++)
                    System.out.println(((("Layer " + j) + " # params: ") + (mln.getLayer(j).numParams())));

            }
            // Mean and variance vars are not gradient checkable; mean/variance "gradient" is used to implement running mean/variance calc
            // i.e., runningMean = decay * runningMean + (1-decay) * batchMean
            // However, numerical gradient will be 0 as forward pass doesn't depend on this "parameter"
            Set<String> excludeParams = new HashSet<>(Arrays.asList("1_mean", "1_var", "1_log10stdev"));
            boolean gradOK = GradientCheckUtil.checkGradients(mln, BNGradientCheckTest.DEFAULT_EPS, BNGradientCheckTest.DEFAULT_MAX_REL_ERROR, BNGradientCheckTest.DEFAULT_MIN_ABS_ERROR, BNGradientCheckTest.PRINT_RESULTS, BNGradientCheckTest.RETURN_ON_FIRST_FAILURE, input, labels, excludeParams);
            Assert.assertTrue(gradOK);
            TestUtils.testModelSerialization(mln);
        }
    }

    @Test
    public void testGradientBNWithCNNandSubsampling() {
        Nd4j.getExecutioner().setProfilingMode(NAN_PANIC);
        // Parameterized test, testing combinations of:
        // (a) activation function
        // (b) Whether to test at random initialization, or after some learning (i.e., 'characteristic mode of operation')
        // (c) Loss function (with specified output activations)
        // (d) l1 and l2 values
        Activation[] activFns = new Activation[]{ Activation.SIGMOID, Activation.TANH, Activation.IDENTITY };
        boolean[] characteristic = new boolean[]{ false, true };// If true: run some backprop steps first

        LossFunctions[] lossFunctions = new LossFunction[]{ LossFunction.NEGATIVELOGLIKELIHOOD, LossFunction.MSE };
        Activation[] outputActivations = new Activation[]{ Activation.SOFTMAX, Activation.TANH };// i.e., lossFunctions[i] used with outputActivations[i] here

        double[] l2vals = new double[]{ 0.0, 0.1, 0.1 };
        double[] l1vals = new double[]{ 0.0, 0.0, 0.2 };// i.e., use l2vals[j] with l1vals[j]

        Nd4j.getRandom().setSeed(12345);
        int minibatch = 10;
        int depth = 2;
        int hw = 5;
        int nOut = 3;
        INDArray input = Nd4j.rand(new int[]{ minibatch, depth, hw, hw }).muli(5).subi(2.5);
        INDArray labels = Nd4j.zeros(minibatch, nOut);
        Random r = new Random(12345);
        for (int i = 0; i < minibatch; i++) {
            labels.putScalar(i, r.nextInt(nOut), 1.0);
        }
        DataSet ds = new DataSet(input, labels);
        for (boolean useLogStd : new boolean[]{ true, false }) {
            for (Activation afn : activFns) {
                for (boolean doLearningFirst : characteristic) {
                    for (int i = 0; i < (lossFunctions.length); i++) {
                        for (int j = 0; j < (l2vals.length); j++) {
                            LossFunctions.LossFunction lf = lossFunctions[i];
                            Activation outputActivation = outputActivations[i];
                            MultiLayerConfiguration.Builder builder = new NeuralNetConfiguration.Builder().seed(12345).l2(l2vals[j]).optimizationAlgo(LINE_GRADIENT_DESCENT).updater(new NoOp()).dist(new UniformDistribution((-2), 2)).seed(12345L).list().layer(0, new ConvolutionLayer.Builder(2, 2).stride(1, 1).nOut(3).activation(afn).build()).layer(1, new BatchNormalization.Builder().useLogStd(useLogStd).build()).layer(2, kernelSize(2, 2).stride(1, 1).build()).layer(3, new BatchNormalization()).layer(4, new ActivationLayer.Builder().activation(afn).build()).layer(5, new OutputLayer.Builder(lf).activation(outputActivation).nOut(nOut).build()).setInputType(InputType.convolutional(hw, hw, depth));
                            MultiLayerConfiguration conf = builder.build();
                            MultiLayerNetwork mln = new MultiLayerNetwork(conf);
                            mln.init();
                            String name = new Object() {}.getClass().getEnclosingMethod().getName();
                            System.out.println(("Num params: " + (mln.numParams())));
                            if (doLearningFirst) {
                                // Run a number of iterations of learning
                                mln.setInput(ds.getFeatures());
                                mln.setLabels(ds.getLabels());
                                mln.computeGradientAndScore();
                                double scoreBefore = mln.score();
                                for (int k = 0; k < 20; k++)
                                    mln.fit(ds);

                                mln.computeGradientAndScore();
                                double scoreAfter = mln.score();
                                // Can't test in 'characteristic mode of operation' if not learning
                                String msg = ((((((((((((name + " - score did not (sufficiently) decrease during learning - activationFn=") + afn) + ", lossFn=") + lf) + ", outputActivation=") + outputActivation) + ", doLearningFirst= ") + doLearningFirst) + " (before=") + scoreBefore) + ", scoreAfter=") + scoreAfter) + ")";
                                Assert.assertTrue(msg, (scoreAfter < (0.9 * scoreBefore)));
                            }
                            if (BNGradientCheckTest.PRINT_RESULTS) {
                                System.out.println(((((((((((((name + " - activationFn=") + afn) + ", lossFn=") + lf) + ", outputActivation=") + outputActivation) + ", doLearningFirst=") + doLearningFirst) + ", l1=") + (l1vals[j])) + ", l2=") + (l2vals[j])));
                                for (int k = 0; k < (mln.getnLayers()); k++)
                                    System.out.println(((("Layer " + k) + " # params: ") + (mln.getLayer(k).numParams())));

                            }
                            // Mean and variance vars are not gradient checkable; mean/variance "gradient" is used to implement running mean/variance calc
                            // i.e., runningMean = decay * runningMean + (1-decay) * batchMean
                            // However, numerical gradient will be 0 as forward pass doesn't depend on this "parameter"
                            Set<String> excludeParams = new HashSet<>(Arrays.asList("1_mean", "1_var", "3_mean", "3_var", "1_log10stdev", "3_log10stdev"));
                            boolean gradOK = GradientCheckUtil.checkGradients(mln, BNGradientCheckTest.DEFAULT_EPS, BNGradientCheckTest.DEFAULT_MAX_REL_ERROR, BNGradientCheckTest.DEFAULT_MIN_ABS_ERROR, BNGradientCheckTest.PRINT_RESULTS, BNGradientCheckTest.RETURN_ON_FIRST_FAILURE, input, labels, excludeParams);
                            Assert.assertTrue(gradOK);
                            TestUtils.testModelSerialization(mln);
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testGradientDense() {
        // Parameterized test, testing combinations of:
        // (a) activation function
        // (b) Whether to test at random initialization, or after some learning (i.e., 'characteristic mode of operation')
        // (c) Loss function (with specified output activations)
        // (d) l1 and l2 values
        Activation[] activFns = new Activation[]{ Activation.SIGMOID, Activation.TANH, Activation.IDENTITY };
        boolean[] characteristic = new boolean[]{ false, true };// If true: run some backprop steps first

        LossFunctions[] lossFunctions = new LossFunction[]{ LossFunction.NEGATIVELOGLIKELIHOOD, LossFunction.MSE };
        Activation[] outputActivations = new Activation[]{ Activation.SOFTMAX, Activation.TANH };// i.e., lossFunctions[i] used with outputActivations[i] here

        double[] l2vals = new double[]{ 0.0, 0.1, 0.1 };
        double[] l1vals = new double[]{ 0.0, 0.0, 0.2 };// i.e., use l2vals[j] with l1vals[j]

        Nd4j.getRandom().setSeed(12345);
        int minibatch = 10;
        int nIn = 5;
        int nOut = 3;
        INDArray input = Nd4j.rand(new int[]{ minibatch, nIn });
        INDArray labels = Nd4j.zeros(minibatch, nOut);
        Random r = new Random(12345);
        for (int i = 0; i < minibatch; i++) {
            labels.putScalar(i, r.nextInt(nOut), 1.0);
        }
        DataSet ds = new DataSet(input, labels);
        for (boolean useLogStd : new boolean[]{ true, false }) {
            for (Activation afn : activFns) {
                for (boolean doLearningFirst : characteristic) {
                    for (int i = 0; i < (lossFunctions.length); i++) {
                        for (int j = 0; j < (l2vals.length); j++) {
                            LossFunctions.LossFunction lf = lossFunctions[i];
                            Activation outputActivation = outputActivations[i];
                            MultiLayerConfiguration.Builder builder = new NeuralNetConfiguration.Builder().l2(l2vals[j]).optimizationAlgo(CONJUGATE_GRADIENT).updater(new NoOp()).dist(new UniformDistribution((-2), 2)).seed(12345L).list().layer(0, new DenseLayer.Builder().nIn(nIn).nOut(4).activation(afn).build()).layer(1, new BatchNormalization.Builder().useLogStd(useLogStd).build()).layer(2, new DenseLayer.Builder().nIn(4).nOut(4).build()).layer(3, new BatchNormalization.Builder().useLogStd(useLogStd).build()).layer(4, new OutputLayer.Builder(lf).activation(outputActivation).nOut(nOut).build());
                            MultiLayerConfiguration conf = builder.build();
                            MultiLayerNetwork mln = new MultiLayerNetwork(conf);
                            mln.init();
                            String name = new Object() {}.getClass().getEnclosingMethod().getName();
                            if (doLearningFirst) {
                                // Run a number of iterations of learning
                                mln.setInput(ds.getFeatures());
                                mln.setLabels(ds.getLabels());
                                mln.computeGradientAndScore();
                                double scoreBefore = mln.score();
                                for (int k = 0; k < 10; k++)
                                    mln.fit(ds);

                                mln.computeGradientAndScore();
                                double scoreAfter = mln.score();
                                // Can't test in 'characteristic mode of operation' if not learning
                                String msg = ((((((((((((name + " - score did not (sufficiently) decrease during learning - activationFn=") + afn) + ", lossFn=") + lf) + ", outputActivation=") + outputActivation) + ", doLearningFirst= ") + doLearningFirst) + " (before=") + scoreBefore) + ", scoreAfter=") + scoreAfter) + ")";
                                Assert.assertTrue(msg, (scoreAfter < (0.8 * scoreBefore)));
                            }
                            if (BNGradientCheckTest.PRINT_RESULTS) {
                                System.out.println(((((((((((((name + " - activationFn=") + afn) + ", lossFn=") + lf) + ", outputActivation=") + outputActivation) + ", doLearningFirst=") + doLearningFirst) + ", l1=") + (l1vals[j])) + ", l2=") + (l2vals[j])));
                                for (int k = 0; k < (mln.getnLayers()); k++)
                                    System.out.println(((("Layer " + k) + " # params: ") + (mln.getLayer(k).numParams())));

                            }
                            // Mean and variance vars are not gradient checkable; mean/variance "gradient" is used to implement running mean/variance calc
                            // i.e., runningMean = decay * runningMean + (1-decay) * batchMean
                            // However, numerical gradient will be 0 as forward pass doesn't depend on this "parameter"
                            Set<String> excludeParams = new HashSet<>(Arrays.asList("1_mean", "1_var", "3_mean", "3_var", "1_log10stdev", "3_log10stdev"));
                            boolean gradOK = GradientCheckUtil.checkGradients(mln, BNGradientCheckTest.DEFAULT_EPS, BNGradientCheckTest.DEFAULT_MAX_REL_ERROR, BNGradientCheckTest.DEFAULT_MIN_ABS_ERROR, BNGradientCheckTest.PRINT_RESULTS, BNGradientCheckTest.RETURN_ON_FIRST_FAILURE, input, labels, excludeParams);
                            Assert.assertTrue(gradOK);
                            TestUtils.testModelSerialization(mln);
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testGradient2dFixedGammaBeta() {
        DataNormalization scaler = new NormalizerMinMaxScaler();
        DataSetIterator iter = new IrisDataSetIterator(150, 150);
        scaler.fit(iter);
        iter.setPreProcessor(scaler);
        DataSet ds = iter.next();
        INDArray input = ds.getFeatures();
        INDArray labels = ds.getLabels();
        for (boolean useLogStd : new boolean[]{ true, false }) {
            MultiLayerConfiguration.Builder builder = new NeuralNetConfiguration.Builder().updater(new NoOp()).seed(12345L).dist(new NormalDistribution(0, 1)).list().layer(0, new DenseLayer.Builder().nIn(4).nOut(3).activation(IDENTITY).build()).layer(1, new BatchNormalization.Builder().useLogStd(useLogStd).lockGammaBeta(true).gamma(2.0).beta(0.5).nOut(3).build()).layer(2, new ActivationLayer.Builder().activation(TANH).build()).layer(3, new OutputLayer.Builder(LossFunction.MCXENT).activation(SOFTMAX).nIn(3).nOut(3).build());
            MultiLayerNetwork mln = new MultiLayerNetwork(builder.build());
            mln.init();
            if (BNGradientCheckTest.PRINT_RESULTS) {
                for (int j = 0; j < (mln.getnLayers()); j++)
                    System.out.println(((("Layer " + j) + " # params: ") + (mln.getLayer(j).numParams())));

            }
            // Mean and variance vars are not gradient checkable; mean/variance "gradient" is used to implement running mean/variance calc
            // i.e., runningMean = decay * runningMean + (1-decay) * batchMean
            // However, numerical gradient will be 0 as forward pass doesn't depend on this "parameter"
            Set<String> excludeParams = new HashSet<>(Arrays.asList("1_mean", "1_var", "1_log10stdev"));
            boolean gradOK = GradientCheckUtil.checkGradients(mln, BNGradientCheckTest.DEFAULT_EPS, BNGradientCheckTest.DEFAULT_MAX_REL_ERROR, BNGradientCheckTest.DEFAULT_MIN_ABS_ERROR, BNGradientCheckTest.PRINT_RESULTS, BNGradientCheckTest.RETURN_ON_FIRST_FAILURE, input, labels, excludeParams);
            Assert.assertTrue(gradOK);
            TestUtils.testModelSerialization(mln);
        }
    }

    @Test
    public void testGradientCnnFixedGammaBeta() {
        Nd4j.getRandom().setSeed(12345);
        int minibatch = 10;
        int depth = 1;
        int hw = 4;
        int nOut = 4;
        INDArray input = Nd4j.rand(new int[]{ minibatch, depth, hw, hw });
        INDArray labels = Nd4j.zeros(minibatch, nOut);
        Random r = new Random(12345);
        for (int i = 0; i < minibatch; i++) {
            labels.putScalar(i, r.nextInt(nOut), 1.0);
        }
        for (boolean useLogStd : new boolean[]{ true, false }) {
            MultiLayerConfiguration.Builder builder = new NeuralNetConfiguration.Builder().updater(new NoOp()).seed(12345L).dist(new NormalDistribution(0, 2)).list().layer(0, new ConvolutionLayer.Builder().kernelSize(2, 2).stride(1, 1).nIn(depth).nOut(2).activation(IDENTITY).build()).layer(1, new BatchNormalization.Builder().useLogStd(useLogStd).lockGammaBeta(true).gamma(2.0).beta(0.5).build()).layer(2, new ActivationLayer.Builder().activation(TANH).build()).layer(3, new OutputLayer.Builder(LossFunction.MCXENT).activation(SOFTMAX).nOut(nOut).build()).setInputType(InputType.convolutional(hw, hw, depth));
            MultiLayerNetwork mln = new MultiLayerNetwork(builder.build());
            mln.init();
            if (BNGradientCheckTest.PRINT_RESULTS) {
                for (int j = 0; j < (mln.getnLayers()); j++)
                    System.out.println(((("Layer " + j) + " # params: ") + (mln.getLayer(j).numParams())));

            }
            // Mean and variance vars are not gradient checkable; mean/variance "gradient" is used to implement running mean/variance calc
            // i.e., runningMean = decay * runningMean + (1-decay) * batchMean
            // However, numerical gradient will be 0 as forward pass doesn't depend on this "parameter"
            Set<String> excludeParams = new HashSet<>(Arrays.asList("1_mean", "1_var", "1_log10stdev"));
            boolean gradOK = GradientCheckUtil.checkGradients(mln, BNGradientCheckTest.DEFAULT_EPS, BNGradientCheckTest.DEFAULT_MAX_REL_ERROR, BNGradientCheckTest.DEFAULT_MIN_ABS_ERROR, BNGradientCheckTest.PRINT_RESULTS, BNGradientCheckTest.RETURN_ON_FIRST_FAILURE, input, labels, excludeParams);
            Assert.assertTrue(gradOK);
            TestUtils.testModelSerialization(mln);
        }
    }

    @Test
    public void testBatchNormCompGraphSimple() {
        int numClasses = 2;
        int height = 3;
        int width = 3;
        int channels = 1;
        long seed = 123;
        int minibatchSize = 3;
        for (boolean useLogStd : new boolean[]{ true, false }) {
            ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(seed).updater(new NoOp()).weightInit(XAVIER).graphBuilder().addInputs("in").setInputTypes(InputType.convolutional(height, width, channels)).addLayer("bn", new BatchNormalization.Builder().useLogStd(useLogStd).build(), "in").addLayer("out", new OutputLayer.Builder().lossFunction(MCXENT).activation(SOFTMAX).nOut(numClasses).build(), "bn").setOutputs("out").build();
            ComputationGraph net = new ComputationGraph(conf);
            net.init();
            Random r = new Random(12345);
            INDArray input = Nd4j.rand(new int[]{ minibatchSize, channels, height, width });// Order: examples, channels, height, width

            INDArray labels = Nd4j.zeros(minibatchSize, numClasses);
            for (int i = 0; i < minibatchSize; i++) {
                labels.putScalar(new int[]{ i, r.nextInt(numClasses) }, 1.0);
            }
            // Mean and variance vars are not gradient checkable; mean/variance "gradient" is used to implement running mean/variance calc
            // i.e., runningMean = decay * runningMean + (1-decay) * batchMean
            // However, numerical gradient will be 0 as forward pass doesn't depend on this "parameter"
            Set<String> excludeParams = new HashSet<>(Arrays.asList("bn_mean", "bn_var"));
            boolean gradOK = GradientCheckUtil.checkGradients(net, BNGradientCheckTest.DEFAULT_EPS, BNGradientCheckTest.DEFAULT_MAX_REL_ERROR, BNGradientCheckTest.DEFAULT_MIN_ABS_ERROR, BNGradientCheckTest.PRINT_RESULTS, BNGradientCheckTest.RETURN_ON_FIRST_FAILURE, new INDArray[]{ input }, new INDArray[]{ labels }, null, null, excludeParams);
            Assert.assertTrue(gradOK);
            TestUtils.testModelSerialization(net);
        }
    }

    @Test
    public void testGradientBNWithCNNandSubsamplingCompGraph() {
        // Parameterized test, testing combinations of:
        // (a) activation function
        // (b) Whether to test at random initialization, or after some learning (i.e., 'characteristic mode of operation')
        // (c) Loss function (with specified output activations)
        // (d) l1 and l2 values
        Activation[] activFns = new Activation[]{ Activation.SIGMOID, Activation.TANH, Activation.IDENTITY };
        boolean[] characteristic = new boolean[]{ false, true };// If true: run some backprop steps first

        LossFunctions[] lossFunctions = new LossFunction[]{ LossFunction.NEGATIVELOGLIKELIHOOD, LossFunction.MSE };
        Activation[] outputActivations = new Activation[]{ Activation.SOFTMAX, Activation.TANH };// i.e., lossFunctions[i] used with outputActivations[i] here

        double[] l2vals = new double[]{ 0.0, 0.1, 0.1 };
        double[] l1vals = new double[]{ 0.0, 0.0, 0.2 };// i.e., use l2vals[j] with l1vals[j]

        Nd4j.getRandom().setSeed(12345);
        int minibatch = 10;
        int depth = 2;
        int hw = 5;
        int nOut = 3;
        INDArray input = Nd4j.rand(new int[]{ minibatch, depth, hw, hw });
        INDArray labels = Nd4j.zeros(minibatch, nOut);
        Random r = new Random(12345);
        for (int i = 0; i < minibatch; i++) {
            labels.putScalar(i, r.nextInt(nOut), 1.0);
        }
        DataSet ds = new DataSet(input, labels);
        for (boolean useLogStd : new boolean[]{ true, false }) {
            for (Activation afn : activFns) {
                for (boolean doLearningFirst : characteristic) {
                    for (int i = 0; i < (lossFunctions.length); i++) {
                        for (int j = 0; j < (l2vals.length); j++) {
                            LossFunctions.LossFunction lf = lossFunctions[i];
                            Activation outputActivation = outputActivations[i];
                            ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).optimizationAlgo(LINE_GRADIENT_DESCENT).updater(new NoOp()).dist(new UniformDistribution((-2), 2)).seed(12345L).graphBuilder().addInputs("in").addLayer("0", new ConvolutionLayer.Builder(2, 2).stride(1, 1).nOut(3).activation(afn).build(), "in").addLayer("1", new BatchNormalization.Builder().useLogStd(useLogStd).build(), "0").addLayer("2", kernelSize(2, 2).stride(1, 1).build(), "1").addLayer("3", new BatchNormalization.Builder().useLogStd(useLogStd).build(), "2").addLayer("4", new ActivationLayer.Builder().activation(afn).build(), "3").addLayer("5", new OutputLayer.Builder(lf).activation(outputActivation).nOut(nOut).build(), "4").setOutputs("5").setInputTypes(InputType.convolutional(hw, hw, depth)).build();
                            ComputationGraph net = new ComputationGraph(conf);
                            net.init();
                            String name = new Object() {}.getClass().getEnclosingMethod().getName();
                            if (doLearningFirst) {
                                // Run a number of iterations of learning
                                net.setInput(0, ds.getFeatures());
                                net.setLabels(ds.getLabels());
                                net.computeGradientAndScore();
                                double scoreBefore = net.score();
                                for (int k = 0; k < 20; k++)
                                    net.fit(ds);

                                net.computeGradientAndScore();
                                double scoreAfter = net.score();
                                // Can't test in 'characteristic mode of operation' if not learning
                                String msg = ((((((((((((name + " - score did not (sufficiently) decrease during learning - activationFn=") + afn) + ", lossFn=") + lf) + ", outputActivation=") + outputActivation) + ", doLearningFirst= ") + doLearningFirst) + " (before=") + scoreBefore) + ", scoreAfter=") + scoreAfter) + ")";
                                Assert.assertTrue(msg, (scoreAfter < (0.9 * scoreBefore)));
                            }
                            if (BNGradientCheckTest.PRINT_RESULTS) {
                                System.out.println(((((((((((((name + " - activationFn=") + afn) + ", lossFn=") + lf) + ", outputActivation=") + outputActivation) + ", doLearningFirst=") + doLearningFirst) + ", l1=") + (l1vals[j])) + ", l2=") + (l2vals[j])));
                                for (int k = 0; k < (net.getNumLayers()); k++)
                                    System.out.println(((("Layer " + k) + " # params: ") + (net.getLayer(k).numParams())));

                            }
                            // Mean and variance vars are not gradient checkable; mean/variance "gradient" is used to implement running mean/variance calc
                            // i.e., runningMean = decay * runningMean + (1-decay) * batchMean
                            // However, numerical gradient will be 0 as forward pass doesn't depend on this "parameter"
                            Set<String> excludeParams = new HashSet<>(Arrays.asList("1_mean", "1_var", "3_mean", "3_var", "1_log10stdev", "3_log10stdev"));
                            boolean gradOK = GradientCheckUtil.checkGradients(net, BNGradientCheckTest.DEFAULT_EPS, BNGradientCheckTest.DEFAULT_MAX_REL_ERROR, BNGradientCheckTest.DEFAULT_MIN_ABS_ERROR, BNGradientCheckTest.PRINT_RESULTS, BNGradientCheckTest.RETURN_ON_FIRST_FAILURE, new INDArray[]{ input }, new INDArray[]{ labels }, null, null, excludeParams);
                            Assert.assertTrue(gradOK);
                            TestUtils.testModelSerialization(net);
                        }
                    }
                }
            }
        }
    }
}

