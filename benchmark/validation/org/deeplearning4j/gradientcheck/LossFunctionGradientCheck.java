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


import Activation.TANH;
import DataType.DOUBLE;
import OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.TestUtils;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.distribution.NormalDistribution;
import org.deeplearning4j.nn.conf.distribution.UniformDistribution;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.LossLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.activations.impl.ActivationIdentity;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.NoOp;
import org.nd4j.linalg.lossfunctions.ILossFunction;
import org.nd4j.linalg.primitives.Pair;
import org.nd4j.shade.jackson.databind.ObjectMapper;


/**
 * Created by Alex on 12/09/2016.
 */
@Slf4j
public class LossFunctionGradientCheck extends BaseDL4JTest {
    static {
        Nd4j.setDataType(DOUBLE);
    }

    private static final boolean PRINT_RESULTS = true;

    private static final boolean RETURN_ON_FIRST_FAILURE = false;

    private static final double DEFAULT_EPS = 1.0E-6;

    private static final double DEFAULT_MAX_REL_ERROR = 1.0E-5;

    private static final double DEFAULT_MIN_ABS_ERROR = 1.0E-8;

    @Test
    public void lossFunctionGradientCheck() {
        ILossFunction[] lossFunctions = new ILossFunction[]{ new LossBinaryXENT(), new LossBinaryXENT(), new LossCosineProximity(), new LossHinge(), new LossKLD(), new LossKLD(), new LossL1(), new LossL1(), new LossL1(), new LossL2(), new LossL2(), new LossMAE(), new LossMAE(), new LossMAPE(), new LossMAPE(), new LossMCXENT(), new LossMSE(), new LossMSE(), new LossMSLE(), new LossMSLE(), new LossNegativeLogLikelihood(), new LossNegativeLogLikelihood(), new LossPoisson(), new LossSquaredHinge(), new LossFMeasure(), new LossFMeasure(2.0), new LossFMeasure(), new LossFMeasure(2.0), LossMixtureDensity.builder().gaussians(2).labelWidth(3).build(), LossMixtureDensity.builder().gaussians(2).labelWidth(3).build(), new LossMultiLabel() };
        Activation[] outputActivationFn = new Activation[]{ Activation.SIGMOID// xent
        , Activation.SIGMOID// xent
        , Activation.TANH// cosine
        , Activation.TANH// hinge -> trying to predict 1 or -1
        , Activation.SIGMOID// kld -> probab so should be between 0 and 1
        , Activation.SOFTMAX// kld + softmax
        , Activation.TANH// l1
        , Activation.RATIONALTANH// l1
        , Activation.SOFTMAX// l1 + softmax
        , Activation.TANH// l2
        , Activation.SOFTMAX// l2 + softmax
        , Activation.IDENTITY// mae
        , Activation.SOFTMAX// mae + softmax
        , Activation.IDENTITY// mape
        , Activation.SOFTMAX// mape + softmax
        , Activation.SOFTMAX// mcxent
        , Activation.IDENTITY// mse
        , Activation.SOFTMAX// mse + softmax
        , Activation.SIGMOID// msle  -   requires positive labels/activations due to log
        , Activation.SOFTMAX// msle + softmax
        , Activation.SIGMOID// nll
        , Activation.SOFTMAX// nll + softmax
        , Activation.SIGMOID// poisson - requires positive predictions due to log... not sure if this is the best option
        , Activation.TANH// squared hinge
        , Activation.SIGMOID// f-measure (binary, single sigmoid output)
        , Activation.SIGMOID// f-measure (binary, single sigmoid output)
        , Activation.SOFTMAX// f-measure (binary, 2-label softmax output)
        , Activation.SOFTMAX// f-measure (binary, 2-label softmax output)
        , Activation.IDENTITY// MixtureDensity
        , Activation.TANH// MixtureDensity + tanh
        , Activation.TANH// MultiLabel, doesn't require any special activation, but tanh was used in paper
         };
        int[] nOut = new int[]{ 1// xent
        , 3// xent
        , 5// cosine
        , 3// hinge
        , 3// kld
        , 3// kld + softmax
        , 3// l1
        , 3// l1
        , 3// l1 + softmax
        , 3// l2
        , 3// l2 + softmax
        , 3// mae
        , 3// mae + softmax
        , 3// mape
        , 3// mape + softmax
        , 3// mcxent
        , 3// mse
        , 3// mse + softmax
        , 3// msle
        , 3// msle + softmax
        , 3// nll
        , 3// nll + softmax
        , 3// poisson
        , 3// squared hinge
        , 1// f-measure (binary, single sigmoid output)
        , 1// f-measure (binary, single sigmoid output)
        , 2// f-measure (binary, 2-label softmax output)
        , 2// f-measure (binary, 2-label softmax output)
        , 10// Mixture Density
        , 10// Mixture Density + tanh
        , 10// MultiLabel
         };
        int[] minibatchSizes = new int[]{ 1, 3 };
        List<String> passed = new ArrayList<>();
        List<String> failed = new ArrayList<>();
        for (int i = 0; i < (lossFunctions.length); i++) {
            for (int j = 0; j < (minibatchSizes.length); j++) {
                String testName = ((((lossFunctions[i]) + " - ") + (outputActivationFn[i])) + " - minibatchSize = ") + (minibatchSizes[j]);
                Nd4j.getRandom().setSeed(12345);
                MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).seed(12345).updater(new NoOp()).dist(new UniformDistribution((-2), 2)).list().layer(0, new DenseLayer.Builder().nIn(4).nOut(4).activation(TANH).build()).layer(1, new OutputLayer.Builder().lossFunction(lossFunctions[i]).activation(outputActivationFn[i]).nIn(4).nOut(nOut[i]).build()).validateOutputLayerConfig(false).build();
                MultiLayerNetwork net = new MultiLayerNetwork(conf);
                net.init();
                INDArray[] inOut = LossFunctionGradientCheck.getFeaturesAndLabels(lossFunctions[i], minibatchSizes[j], 4, nOut[i], 12345);
                INDArray input = inOut[0];
                INDArray labels = inOut[1];
                log.info(" ***** Starting test: {} *****", testName);
                // System.out.println(Arrays.toString(labels.data().asDouble()));
                // System.out.println(Arrays.toString(net.output(input,false).data().asDouble()));
                // System.out.println(net.score(new DataSet(input,labels)));
                boolean gradOK;
                try {
                    gradOK = GradientCheckUtil.checkGradients(net, LossFunctionGradientCheck.DEFAULT_EPS, LossFunctionGradientCheck.DEFAULT_MAX_REL_ERROR, LossFunctionGradientCheck.DEFAULT_MIN_ABS_ERROR, LossFunctionGradientCheck.PRINT_RESULTS, LossFunctionGradientCheck.RETURN_ON_FIRST_FAILURE, input, labels);
                } catch (Exception e) {
                    e.printStackTrace();
                    failed.add(((testName + "\t") + "EXCEPTION"));
                    continue;
                }
                if (gradOK) {
                    passed.add(testName);
                } else {
                    failed.add(testName);
                }
                System.out.println("\n\n");
                TestUtils.testModelSerialization(net);
            }
        }
        System.out.println("---- Passed ----");
        for (String s : passed) {
            System.out.println(s);
        }
        System.out.println("---- Failed ----");
        for (String s : failed) {
            System.out.println(s);
        }
        Assert.assertEquals("Tests failed", 0, failed.size());
    }

    @Test
    public void lossFunctionGradientCheckLossLayer() {
        ILossFunction[] lossFunctions = new ILossFunction[]{ new LossBinaryXENT(), new LossBinaryXENT(), new LossCosineProximity(), new LossHinge(), new LossKLD(), new LossKLD(), new LossL1(), new LossL1(), new LossL2(), new LossL2(), new LossMAE(), new LossMAE(), new LossMAPE(), new LossMAPE(), new LossMCXENT(), new LossMSE(), new LossMSE(), new LossMSLE(), new LossMSLE(), new LossNegativeLogLikelihood(), new LossNegativeLogLikelihood(), new LossPoisson(), new LossSquaredHinge(), new LossFMeasure(), new LossFMeasure(2.0), new LossFMeasure(), new LossFMeasure(2.0), LossMixtureDensity.builder().gaussians(2).labelWidth(3).build(), LossMixtureDensity.builder().gaussians(2).labelWidth(3).build(), new LossMultiLabel() };
        Activation[] outputActivationFn = new Activation[]{ Activation.SIGMOID// xent
        , Activation.SIGMOID// xent
        , Activation.TANH// cosine
        , Activation.TANH// hinge -> trying to predict 1 or -1
        , Activation.SIGMOID// kld -> probab so should be between 0 and 1
        , Activation.SOFTMAX// kld + softmax
        , Activation.TANH// l1
        , Activation.SOFTMAX// l1 + softmax
        , Activation.TANH// l2
        , Activation.SOFTMAX// l2 + softmax
        , Activation.IDENTITY// mae
        , Activation.SOFTMAX// mae + softmax
        , Activation.IDENTITY// mape
        , Activation.SOFTMAX// mape + softmax
        , Activation.SOFTMAX// mcxent
        , Activation.IDENTITY// mse
        , Activation.SOFTMAX// mse + softmax
        , Activation.SIGMOID// msle  -   requires positive labels/activations due to log
        , Activation.SOFTMAX// msle + softmax
        , Activation.SIGMOID// nll
        , Activation.SOFTMAX// nll + softmax
        , Activation.SIGMOID// poisson - requires positive predictions due to log... not sure if this is the best option
        , Activation.TANH// squared hinge
        , Activation.SIGMOID// f-measure (binary, single sigmoid output)
        , Activation.SIGMOID// f-measure (binary, single sigmoid output)
        , Activation.SOFTMAX// f-measure (binary, 2-label softmax output)
        , Activation.SOFTMAX// f-measure (binary, 2-label softmax output)
        , Activation.IDENTITY// MixtureDensity
        , Activation.TANH// MixtureDensity + tanh
        , Activation.TANH// MultiLabel
         };
        int[] nOut = new int[]{ 1// xent
        , 3// xent
        , 5// cosine
        , 3// hinge
        , 3// kld
        , 3// kld + softmax
        , 3// l1
        , 3// l1 + softmax
        , 3// l2
        , 3// l2 + softmax
        , 3// mae
        , 3// mae + softmax
        , 3// mape
        , 3// mape + softmax
        , 3// mcxent
        , 3// mse
        , 3// mse + softmax
        , 3// msle
        , 3// msle + softmax
        , 3// nll
        , 3// nll + softmax
        , 3// poisson
        , 3// squared hinge
        , 1// f-measure (binary, single sigmoid output)
        , 1// f-measure (binary, single sigmoid output)
        , 2// f-measure (binary, 2-label softmax output)
        , 2// f-measure (binary, 2-label softmax output)
        , 10// Mixture Density
        , 10// Mixture Density + tanh
        , 10// MultiLabel
         };
        int[] minibatchSizes = new int[]{ 1, 3 };
        // int[] minibatchSizes = new int[]{3};
        List<String> passed = new ArrayList<>();
        List<String> failed = new ArrayList<>();
        for (int i = 0; i < (lossFunctions.length); i++) {
            for (int j = 0; j < (minibatchSizes.length); j++) {
                String testName = ((((lossFunctions[i]) + " - ") + (outputActivationFn[i])) + " - minibatchSize = ") + (minibatchSizes[j]);
                // Serialize and de-serialize loss function
                // to ensure that we carry the parameters through
                // the serializer.
                try {
                    ObjectMapper m = NeuralNetConfiguration.mapper();
                    String s = m.writeValueAsString(lossFunctions[i]);
                    ILossFunction lf2 = m.readValue(s, lossFunctions[i].getClass());
                    lossFunctions[i] = lf2;
                } catch (IOException ex) {
                    ex.printStackTrace();
                    Assert.assertEquals(("Tests failed: serialization of " + (lossFunctions[i])), 0, 1);
                }
                Nd4j.getRandom().setSeed(12345);
                MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).seed(12345).updater(new NoOp()).dist(new UniformDistribution((-2), 2)).list().layer(0, new DenseLayer.Builder().nIn(4).nOut(nOut[i]).activation(TANH).build()).layer(1, new LossLayer.Builder().lossFunction(lossFunctions[i]).activation(outputActivationFn[i]).build()).validateOutputLayerConfig(false).build();
                MultiLayerNetwork net = new MultiLayerNetwork(conf);
                net.init();
                Assert.assertTrue(((getLossFn().getClass()) == (lossFunctions[i].getClass())));
                INDArray[] inOut = LossFunctionGradientCheck.getFeaturesAndLabels(lossFunctions[i], minibatchSizes[j], 4, nOut[i], 12345);
                INDArray input = inOut[0];
                INDArray labels = inOut[1];
                log.info(" ***** Starting test: {} *****", testName);
                // System.out.println(Arrays.toString(labels.data().asDouble()));
                // System.out.println(Arrays.toString(net.output(input,false).data().asDouble()));
                // System.out.println(net.score(new DataSet(input,labels)));
                boolean gradOK;
                try {
                    gradOK = GradientCheckUtil.checkGradients(net, LossFunctionGradientCheck.DEFAULT_EPS, LossFunctionGradientCheck.DEFAULT_MAX_REL_ERROR, LossFunctionGradientCheck.DEFAULT_MIN_ABS_ERROR, LossFunctionGradientCheck.PRINT_RESULTS, LossFunctionGradientCheck.RETURN_ON_FIRST_FAILURE, input, labels);
                } catch (Exception e) {
                    e.printStackTrace();
                    failed.add(((testName + "\t") + "EXCEPTION"));
                    continue;
                }
                if (gradOK) {
                    passed.add(testName);
                } else {
                    failed.add(testName);
                }
                System.out.println("\n\n");
                TestUtils.testModelSerialization(net);
            }
        }
        System.out.println("---- Passed ----");
        for (String s : passed) {
            System.out.println(s);
        }
        System.out.println("---- Failed ----");
        for (String s : failed) {
            System.out.println(s);
        }
        Assert.assertEquals("Tests failed", 0, failed.size());
    }

    @Test
    public void lossMultiLabelEdgeCases() {
        INDArray labels;
        Pair<Double, INDArray> gradientAndScore;
        final ActivationIdentity activationFn = new ActivationIdentity();
        final LossMultiLabel lossMultiLabel = new LossMultiLabel();
        final INDArray preOutput = Nd4j.rand(3, 3);
        // Base Case: Labels are NOT all 1 or 0
        labels = Nd4j.diag(Nd4j.ones(3));
        gradientAndScore = lossMultiLabel.computeGradientAndScore(labels, preOutput, activationFn, null, true);
        Assert.assertTrue((!(gradientAndScore.getFirst().isNaN())));
        Assert.assertTrue((!(gradientAndScore.getFirst().isInfinite())));
        // Edge Case: Labels are all 1
        labels = Nd4j.ones(3, 3);
        gradientAndScore = lossMultiLabel.computeGradientAndScore(labels, preOutput, activationFn, null, true);
        Assert.assertTrue((!(gradientAndScore.getFirst().isNaN())));
        Assert.assertTrue((!(gradientAndScore.getFirst().isInfinite())));
        // Edge Case: Labels are all 0
        labels = Nd4j.zeros(3, 3);
        gradientAndScore = lossMultiLabel.computeGradientAndScore(labels, preOutput, activationFn, null, true);
        Assert.assertTrue((!(gradientAndScore.getFirst().isNaN())));
        Assert.assertTrue((!(gradientAndScore.getFirst().isInfinite())));
    }

    @Test
    public void lossFunctionWeightedGradientCheck() {
        Nd4j.getRandom().setSeed(12345);
        INDArray[] weights = new INDArray[]{ Nd4j.create(new double[]{ 0.2, 0.3, 0.5 }), Nd4j.create(new double[]{ 1.0, 0.5, 2.0 }) };
        List<String> passed = new ArrayList<>();
        List<String> failed = new ArrayList<>();
        for (INDArray w : weights) {
            ILossFunction[] lossFunctions = new ILossFunction[]{ new LossBinaryXENT(w), new LossL1(w), new LossL1(w), new LossL2(w), new LossL2(w), new LossMAE(w), new LossMAE(w), new LossMAPE(w), new LossMAPE(w), new LossMCXENT(w), new LossMSE(w), new LossMSE(w), new LossMSLE(w), new LossMSLE(w), new LossNegativeLogLikelihood(w), new LossNegativeLogLikelihood(w) };
            Activation[] outputActivationFn = new Activation[]{ Activation.SIGMOID// xent
            , Activation.TANH// l1
            , Activation.SOFTMAX// l1 + softmax
            , Activation.TANH// l2
            , Activation.SOFTMAX// l2 + softmax
            , Activation.IDENTITY// mae
            , Activation.SOFTMAX// mae + softmax
            , Activation.IDENTITY// mape
            , Activation.SOFTMAX// mape + softmax
            , Activation.SOFTMAX// mcxent
            , Activation.IDENTITY// mse
            , Activation.SOFTMAX// mse + softmax
            , Activation.SIGMOID// msle  -   requires positive labels/activations due to log
            , Activation.SOFTMAX// msle + softmax
            , Activation.SIGMOID// nll
            , Activation.SOFTMAX// nll + softmax
             };
            int[] minibatchSizes = new int[]{ 1, 3 };
            for (int i = 0; i < (lossFunctions.length); i++) {
                for (int j = 0; j < (minibatchSizes.length); j++) {
                    String testName = ((((((lossFunctions[i]) + " - ") + (outputActivationFn[i])) + " - minibatchSize = ") + (minibatchSizes[j])) + "; weights = ") + w;
                    Nd4j.getRandom().setSeed(12345);
                    MultiLayerConfiguration conf = // .dist(new UniformDistribution(-3, 3))
                    new NeuralNetConfiguration.Builder().optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).seed(12345).updater(new NoOp()).dist(new NormalDistribution(0, 1)).list().layer(0, new DenseLayer.Builder().nIn(4).nOut(4).activation(TANH).build()).layer(1, new OutputLayer.Builder().lossFunction(lossFunctions[i]).activation(outputActivationFn[i]).nIn(4).nOut(3).build()).validateOutputLayerConfig(false).build();
                    MultiLayerNetwork net = new MultiLayerNetwork(conf);
                    net.init();
                    // Check params to avoid test flakiness on small or large params
                    INDArray params = net.params();
                    for (int x = 0; x < (params.length()); x++) {
                        while (((Math.abs(params.getDouble(x))) < 0.01) || ((Math.abs(params.getDouble(x))) > 1.5)) {
                            double d = Nd4j.getRandom().nextDouble();
                            params.putScalar(x, ((-1.5) + (d * 3)));
                        } 
                    }
                    INDArray[] inOut = LossFunctionGradientCheck.getFeaturesAndLabels(lossFunctions[i], minibatchSizes[j], 4, 3, 12345);
                    INDArray input = inOut[0];
                    INDArray labels = inOut[1];
                    log.info(" ***** Starting test: {} *****", testName);
                    // System.out.println(Arrays.toString(labels.data().asDouble()));
                    // System.out.println(Arrays.toString(net.output(input,false).data().asDouble()));
                    // System.out.println(net.score(new DataSet(input,labels)));
                    boolean gradOK;
                    try {
                        gradOK = GradientCheckUtil.checkGradients(net, LossFunctionGradientCheck.DEFAULT_EPS, LossFunctionGradientCheck.DEFAULT_MAX_REL_ERROR, LossFunctionGradientCheck.DEFAULT_MIN_ABS_ERROR, LossFunctionGradientCheck.PRINT_RESULTS, LossFunctionGradientCheck.RETURN_ON_FIRST_FAILURE, input, labels);
                    } catch (Exception e) {
                        e.printStackTrace();
                        failed.add(((testName + "\t") + "EXCEPTION"));
                        continue;
                    }
                    if (gradOK) {
                        passed.add(testName);
                    } else {
                        failed.add(testName);
                    }
                    System.out.println("\n\n");
                }
            }
        }
        System.out.println("---- Passed ----");
        for (String s : passed) {
            System.out.println(s);
        }
        System.out.println("---- Failed ----");
        for (String s : failed) {
            System.out.println(s);
        }
        Assert.assertEquals("Tests failed", 0, failed.size());
    }
}

