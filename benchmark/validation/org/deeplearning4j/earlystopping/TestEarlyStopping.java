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
package org.deeplearning4j.earlystopping;


import Activation.ELU;
import Activation.IDENTITY;
import Activation.SIGMOID;
import Activation.SOFTMAX;
import Activation.TANH;
import EarlyStoppingResult.TerminationReason.EpochTerminationCondition;
import EarlyStoppingResult.TerminationReason.IterationTerminationCondition;
import Evaluation.Metric;
import GradientNormalization.ClipElementWiseAbsoluteValue;
import LossFunctions.LossFunction;
import LossFunctions.LossFunction.MCXENT;
import LossFunctions.LossFunction.MSE;
import OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT;
import ROCScoreCalculator.ROCType;
import WeightInit.XAVIER;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.TestUtils;
import org.deeplearning4j.datasets.iterator.MultipleEpochsIterator;
import org.deeplearning4j.datasets.iterator.impl.IrisDataSetIterator;
import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.deeplearning4j.earlystopping.listener.EarlyStoppingListener;
import org.deeplearning4j.earlystopping.saver.LocalFileModelSaver;
import org.deeplearning4j.earlystopping.trainer.EarlyStoppingTrainer;
import org.deeplearning4j.earlystopping.trainer.IEarlyStoppingTrainer;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.variational.VariationalAutoencoder;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.api.BaseTrainingListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.optimize.solvers.BaseOptimizer;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.evaluation.classification.ROCBinary;
import org.nd4j.evaluation.regression.RegressionEvaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.learning.config.Sgd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ROCScoreCalculator.Metric.AUC;
import static ROCScoreCalculator.Metric.AUPRC;
import static RegressionEvaluation.Metric.MAE;
import static RegressionEvaluation.Metric.MSE;


@Slf4j
public class TestEarlyStopping extends BaseDL4JTest {
    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

    @Test
    public void testEarlyStoppingIris() {
        DataSetIterator irisIter = new IrisDataSetIterator(150, 150);
        for (int i = 0; i < 6; i++) {
            Nd4j.getRandom().setSeed(12345);
            ScoreCalculator sc;
            boolean min;
            switch (i) {
                case 0 :
                    sc = new DataSetLossCalculator(irisIter, true);
                    min = true;
                    break;
                case 1 :
                    sc = new ClassificationScoreCalculator(Metric.ACCURACY, irisIter);
                    min = false;
                    break;
                case 2 :
                    sc = new ClassificationScoreCalculator(Metric.F1, irisIter);
                    min = false;
                    break;
                case 3 :
                    sc = new RegressionScoreCalculator(MSE, irisIter);
                    min = true;
                    break;
                case 4 :
                    sc = new ROCScoreCalculator(ROCType.MULTICLASS, AUC, irisIter);
                    min = false;
                    break;
                case 5 :
                    sc = new ROCScoreCalculator(ROCType.MULTICLASS, AUPRC, irisIter);
                    min = false;
                    break;
                case 6 :
                    sc = new ROCScoreCalculator(ROCType.BINARY, AUC, irisIter);
                    min = false;
                    break;
                default :
                    throw new RuntimeException();
            }
            String msg = (i + " - ") + (sc.getClass().getSimpleName());
            log.info("Starting test - {}", msg);
            MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).updater(new Sgd(0.5)).weightInit(XAVIER).list().layer(new DenseLayer.Builder().nIn(4).nOut(4).activation(TANH).build()).layer(new OutputLayer.Builder().nIn(4).nOut(3).activation(SOFTMAX).lossFunction(MCXENT).build()).build();
            MultiLayerNetwork net = new MultiLayerNetwork(conf);
            net.init();
            // net.setListeners(new ScoreIterationListener(1));
            EarlyStoppingModelSaver<MultiLayerNetwork> saver = new org.deeplearning4j.earlystopping.saver.InMemoryModelSaver();
            EarlyStoppingConfiguration<MultiLayerNetwork> esConf = new EarlyStoppingConfiguration.Builder<MultiLayerNetwork>().epochTerminationConditions(new MaxEpochsTerminationCondition(5)).iterationTerminationConditions(new MaxTimeIterationTerminationCondition(1, TimeUnit.MINUTES)).scoreCalculator(sc).modelSaver(saver).build();
            IEarlyStoppingTrainer<MultiLayerNetwork> trainer = new EarlyStoppingTrainer(esConf, net, irisIter);
            EarlyStoppingResult<MultiLayerNetwork> result = trainer.fit();
            System.out.println(result);
            Assert.assertEquals(5, result.getTotalEpochs());
            Assert.assertEquals(EpochTerminationCondition, result.getTerminationReason());
            Map<Integer, Double> scoreVsIter = result.getScoreVsEpoch();
            Assert.assertEquals(5, scoreVsIter.size());
            String expDetails = esConf.getEpochTerminationConditions().get(0).toString();
            Assert.assertEquals(expDetails, result.getTerminationDetails());
            MultiLayerNetwork out = result.getBestModel();
            Assert.assertNotNull(out);
            // Validate that it is in fact the best model:
            int bestEpoch = -1;
            double bestScore = (min) ? Double.MAX_VALUE : -(Double.MAX_VALUE);
            for (int j = 0; j < 5; j++) {
                double s = scoreVsIter.get(j);
                if ((min && (s < bestScore)) || ((!min) && (s > bestScore))) {
                    bestScore = s;
                    bestEpoch = j;
                }
            }
            Assert.assertEquals(msg, bestEpoch, out.getEpochCount());
            Assert.assertEquals(msg, bestScore, result.getBestModelScore(), 1.0E-5);
            // Check that best score actually matches (returned model vs. manually calculated score)
            MultiLayerNetwork bestNetwork = result.getBestModel();
            irisIter.reset();
            double score;
            switch (i) {
                case 0 :
                    score = bestNetwork.score(irisIter.next());
                    break;
                case 1 :
                    score = bestNetwork.evaluate(irisIter).accuracy();
                    break;
                case 2 :
                    score = bestNetwork.evaluate(irisIter).f1();
                    break;
                case 3 :
                    score = bestNetwork.evaluateRegression(irisIter).averageMeanSquaredError();
                    break;
                case 4 :
                    score = bestNetwork.evaluateROCMultiClass(irisIter).calculateAverageAUC();
                    break;
                case 5 :
                    score = bestNetwork.evaluateROCMultiClass(irisIter).calculateAverageAUCPR();
                    break;
                case 6 :
                    score = bestNetwork.doEvaluation(irisIter, new ROCBinary())[0].calculateAverageAuc();
                    break;
                default :
                    throw new RuntimeException();
            }
            Assert.assertEquals(msg, result.getBestModelScore(), score, 0.01);
        }
    }

    @Test
    public void testEarlyStoppingEveryNEpoch() {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).updater(new Sgd(0.01)).weightInit(XAVIER).list().layer(0, new OutputLayer.Builder().nIn(4).nOut(3).activation(SOFTMAX).lossFunction(MCXENT).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.setListeners(new ScoreIterationListener(1));
        DataSetIterator irisIter = new IrisDataSetIterator(150, 150);
        EarlyStoppingModelSaver<MultiLayerNetwork> saver = new org.deeplearning4j.earlystopping.saver.InMemoryModelSaver();
        EarlyStoppingConfiguration<MultiLayerNetwork> esConf = new EarlyStoppingConfiguration.Builder<MultiLayerNetwork>().epochTerminationConditions(new MaxEpochsTerminationCondition(5)).scoreCalculator(new DataSetLossCalculator(irisIter, true)).evaluateEveryNEpochs(2).modelSaver(saver).build();
        IEarlyStoppingTrainer<MultiLayerNetwork> trainer = new EarlyStoppingTrainer(esConf, net, irisIter);
        EarlyStoppingResult<MultiLayerNetwork> result = trainer.fit();
        System.out.println(result);
        Assert.assertEquals(5, result.getTotalEpochs());
        Assert.assertEquals(EpochTerminationCondition, result.getTerminationReason());
    }

    @Test
    public void testEarlyStoppingIrisMultiEpoch() {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).updater(new Sgd(0.001)).weightInit(XAVIER).list().layer(0, new OutputLayer.Builder().nIn(4).nOut(3).activation(SOFTMAX).lossFunction(MCXENT).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.setListeners(new ScoreIterationListener(1));
        DataSetIterator irisIter = new IrisDataSetIterator(150, 150);
        MultipleEpochsIterator mIter = new MultipleEpochsIterator(10, irisIter);
        EarlyStoppingModelSaver<MultiLayerNetwork> saver = new org.deeplearning4j.earlystopping.saver.InMemoryModelSaver();
        EarlyStoppingConfiguration<MultiLayerNetwork> esConf = new EarlyStoppingConfiguration.Builder<MultiLayerNetwork>().epochTerminationConditions(new MaxEpochsTerminationCondition(5)).iterationTerminationConditions(new MaxTimeIterationTerminationCondition(1, TimeUnit.MINUTES)).scoreCalculator(new DataSetLossCalculator(irisIter, true)).modelSaver(saver).build();
        IEarlyStoppingTrainer<MultiLayerNetwork> trainer = new EarlyStoppingTrainer(esConf, net, mIter);
        EarlyStoppingResult<MultiLayerNetwork> result = trainer.fit();
        System.out.println(result);
        Assert.assertEquals(5, result.getTotalEpochs());
        Assert.assertEquals(EpochTerminationCondition, result.getTerminationReason());
        Map<Integer, Double> scoreVsIter = result.getScoreVsEpoch();
        Assert.assertEquals(5, scoreVsIter.size());
        String expDetails = esConf.getEpochTerminationConditions().get(0).toString();
        Assert.assertEquals(expDetails, result.getTerminationDetails());
        MultiLayerNetwork out = result.getBestModel();
        Assert.assertNotNull(out);
        // Check that best score actually matches (returned model vs. manually calculated score)
        MultiLayerNetwork bestNetwork = result.getBestModel();
        irisIter.reset();
        double score = bestNetwork.score(irisIter.next(), false);
        Assert.assertEquals(result.getBestModelScore(), score, 0.01);
    }

    @Test
    public void testBadTuning() {
        // Test poor tuning (high LR): should terminate on MaxScoreIterationTerminationCondition
        Nd4j.getRandom().setSeed(12345);
        MultiLayerConfiguration conf = // Intentionally huge LR
        new NeuralNetConfiguration.Builder().seed(12345).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).updater(new Sgd(5.0)).weightInit(XAVIER).list().layer(0, new OutputLayer.Builder().nIn(4).nOut(3).activation(SOFTMAX).lossFunction(MCXENT).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.setListeners(new ScoreIterationListener(1));
        DataSetIterator irisIter = new IrisDataSetIterator(150, 150);
        EarlyStoppingModelSaver<MultiLayerNetwork> saver = new org.deeplearning4j.earlystopping.saver.InMemoryModelSaver();
        EarlyStoppingConfiguration<MultiLayerNetwork> esConf = // Initial score is ~2.5
        new EarlyStoppingConfiguration.Builder<MultiLayerNetwork>().epochTerminationConditions(new MaxEpochsTerminationCondition(5000)).iterationTerminationConditions(new MaxTimeIterationTerminationCondition(1, TimeUnit.MINUTES), new MaxScoreIterationTerminationCondition(10)).scoreCalculator(new DataSetLossCalculator(irisIter, true)).modelSaver(saver).build();
        IEarlyStoppingTrainer trainer = new EarlyStoppingTrainer(esConf, net, irisIter);
        EarlyStoppingResult result = trainer.fit();
        Assert.assertTrue(((result.getTotalEpochs()) < 5));
        Assert.assertEquals(IterationTerminationCondition, result.getTerminationReason());
        String expDetails = new MaxScoreIterationTerminationCondition(10).toString();
        Assert.assertEquals(expDetails, result.getTerminationDetails());
        Assert.assertEquals(0, result.getBestModelEpoch());
        Assert.assertNotNull(result.getBestModel());
    }

    @Test
    public void testTimeTermination() {
        // test termination after max time
        Nd4j.getRandom().setSeed(12345);
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).updater(new Sgd(1.0E-6)).weightInit(XAVIER).list().layer(0, new OutputLayer.Builder().nIn(4).nOut(3).activation(SOFTMAX).lossFunction(MCXENT).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.setListeners(new ScoreIterationListener(1));
        DataSetIterator irisIter = new IrisDataSetIterator(150, 150);
        EarlyStoppingModelSaver<MultiLayerNetwork> saver = new org.deeplearning4j.earlystopping.saver.InMemoryModelSaver();
        EarlyStoppingConfiguration<MultiLayerNetwork> esConf = // Initial score is ~8
        new EarlyStoppingConfiguration.Builder<MultiLayerNetwork>().epochTerminationConditions(new MaxEpochsTerminationCondition(10000)).iterationTerminationConditions(new MaxTimeIterationTerminationCondition(3, TimeUnit.SECONDS), new MaxScoreIterationTerminationCondition(50)).scoreCalculator(new DataSetLossCalculator(irisIter, true)).modelSaver(saver).build();
        IEarlyStoppingTrainer<MultiLayerNetwork> trainer = new EarlyStoppingTrainer(esConf, net, irisIter);
        long startTime = System.currentTimeMillis();
        EarlyStoppingResult result = trainer.fit();
        long endTime = System.currentTimeMillis();
        int durationSeconds = ((int) (endTime - startTime)) / 1000;
        Assert.assertTrue((durationSeconds >= 3));
        Assert.assertTrue((durationSeconds <= 9));
        Assert.assertEquals(IterationTerminationCondition, result.getTerminationReason());
        String expDetails = new MaxTimeIterationTerminationCondition(3, TimeUnit.SECONDS).toString();
        Assert.assertEquals(expDetails, result.getTerminationDetails());
    }

    @Test
    public void testNoImprovementNEpochsTermination() {
        // Idea: terminate training if score (test set loss) does not improve for 5 consecutive epochs
        // Simulate this by setting LR = 0.0
        Nd4j.getRandom().setSeed(12345);
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).updater(new Sgd(0.0)).weightInit(XAVIER).list().layer(0, new OutputLayer.Builder().nIn(4).nOut(3).activation(SOFTMAX).lossFunction(MCXENT).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.setListeners(new ScoreIterationListener(1));
        DataSetIterator irisIter = new IrisDataSetIterator(150, 150);
        EarlyStoppingModelSaver<MultiLayerNetwork> saver = new org.deeplearning4j.earlystopping.saver.InMemoryModelSaver();
        EarlyStoppingConfiguration<MultiLayerNetwork> esConf = // Initial score is ~8
        new EarlyStoppingConfiguration.Builder<MultiLayerNetwork>().epochTerminationConditions(new MaxEpochsTerminationCondition(100), new ScoreImprovementEpochTerminationCondition(5)).iterationTerminationConditions(new MaxTimeIterationTerminationCondition(3, TimeUnit.SECONDS), new MaxScoreIterationTerminationCondition(50)).scoreCalculator(new DataSetLossCalculator(irisIter, true)).modelSaver(saver).build();
        IEarlyStoppingTrainer trainer = new EarlyStoppingTrainer(esConf, net, irisIter);
        EarlyStoppingResult result = trainer.fit();
        // Expect no score change due to 0 LR -> terminate after 6 total epochs
        Assert.assertEquals(6, result.getTotalEpochs());
        Assert.assertEquals(0, result.getBestModelEpoch());
        Assert.assertEquals(EpochTerminationCondition, result.getTerminationReason());
        String expDetails = new ScoreImprovementEpochTerminationCondition(5).toString();
        Assert.assertEquals(expDetails, result.getTerminationDetails());
    }

    @Test
    public void testMinImprovementNEpochsTermination() {
        // Idea: terminate training if score (test set loss) does not improve more than minImprovement for 5 consecutive epochs
        // Simulate this by setting LR = 0.0
        Random rng = new Random(123);
        Nd4j.getRandom().setSeed(12345);
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(123).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).updater(new org.nd4j.linalg.learning.config.Nesterovs(0.0, 0.9)).list().layer(0, new DenseLayer.Builder().nIn(1).nOut(20).weightInit(XAVIER).activation(TANH).build()).layer(1, new OutputLayer.Builder(LossFunction.MSE).weightInit(XAVIER).activation(IDENTITY).weightInit(XAVIER).nIn(20).nOut(1).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.setListeners(new ScoreIterationListener(1));
        int nSamples = 100;
        // Generate the training data
        INDArray x = Nd4j.linspace((-10), 10, nSamples).reshape(nSamples, 1);
        INDArray y = Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.impl.transforms.strict.Sin(x.dup()));
        DataSet allData = new DataSet(x, y);
        List<DataSet> list = allData.asList();
        Collections.shuffle(list, rng);
        DataSetIterator training = new org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator(list, nSamples);
        double minImprovement = 9.0E-4;
        EarlyStoppingModelSaver<MultiLayerNetwork> saver = new org.deeplearning4j.earlystopping.saver.InMemoryModelSaver();
        EarlyStoppingConfiguration<MultiLayerNetwork> esConf = // Go on for max 5 epochs without any improvements that are greater than minImprovement
        new EarlyStoppingConfiguration.Builder<MultiLayerNetwork>().epochTerminationConditions(new MaxEpochsTerminationCondition(1000), new ScoreImprovementEpochTerminationCondition(5, minImprovement)).iterationTerminationConditions(new MaxTimeIterationTerminationCondition(3, TimeUnit.MINUTES)).scoreCalculator(new DataSetLossCalculator(training, true)).modelSaver(saver).build();
        IEarlyStoppingTrainer trainer = new EarlyStoppingTrainer(esConf, net, training);
        EarlyStoppingResult result = trainer.fit();
        Assert.assertEquals(6, result.getTotalEpochs());
        Assert.assertEquals(EpochTerminationCondition, result.getTerminationReason());
        String expDetails = new ScoreImprovementEpochTerminationCondition(5, minImprovement).toString();
        Assert.assertEquals(expDetails, result.getTerminationDetails());
    }

    @Test
    public void testEarlyStoppingGetBestModel() {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).updater(new Sgd(0.001)).weightInit(XAVIER).list().layer(0, new OutputLayer.Builder().nIn(4).nOut(3).activation(SOFTMAX).lossFunction(MCXENT).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.setListeners(new ScoreIterationListener(1));
        DataSetIterator irisIter = new IrisDataSetIterator(150, 150);
        MultipleEpochsIterator mIter = new MultipleEpochsIterator(10, irisIter);
        EarlyStoppingModelSaver<MultiLayerNetwork> saver = new org.deeplearning4j.earlystopping.saver.InMemoryModelSaver();
        EarlyStoppingConfiguration<MultiLayerNetwork> esConf = new EarlyStoppingConfiguration.Builder<MultiLayerNetwork>().epochTerminationConditions(new MaxEpochsTerminationCondition(5)).iterationTerminationConditions(new MaxTimeIterationTerminationCondition(1, TimeUnit.MINUTES)).scoreCalculator(new DataSetLossCalculator(irisIter, true)).modelSaver(saver).build();
        IEarlyStoppingTrainer<MultiLayerNetwork> trainer = new EarlyStoppingTrainer(esConf, net, mIter);
        EarlyStoppingResult<MultiLayerNetwork> result = trainer.fit();
        System.out.println(result);
        MultiLayerNetwork mln = result.getBestModel();
        Assert.assertEquals(net.getnLayers(), mln.getnLayers());
        Assert.assertEquals(net.conf().getOptimizationAlgo(), mln.conf().getOptimizationAlgo());
        BaseLayer bl = ((BaseLayer) (net.conf().getLayer()));
        Assert.assertEquals(bl.getActivationFn().toString(), getActivationFn().toString());
        Assert.assertEquals(bl.getIUpdater(), getIUpdater());
    }

    @Test
    public void testListeners() {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).updater(new Sgd(0.001)).weightInit(XAVIER).list().layer(0, new OutputLayer.Builder().nIn(4).nOut(3).activation(SOFTMAX).lossFunction(MCXENT).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.setListeners(new ScoreIterationListener(1));
        DataSetIterator irisIter = new IrisDataSetIterator(150, 150);
        EarlyStoppingModelSaver<MultiLayerNetwork> saver = new org.deeplearning4j.earlystopping.saver.InMemoryModelSaver();
        EarlyStoppingConfiguration<MultiLayerNetwork> esConf = new EarlyStoppingConfiguration.Builder<MultiLayerNetwork>().epochTerminationConditions(new MaxEpochsTerminationCondition(5)).iterationTerminationConditions(new MaxTimeIterationTerminationCondition(1, TimeUnit.MINUTES)).scoreCalculator(new DataSetLossCalculator(irisIter, true)).modelSaver(saver).build();
        TestEarlyStopping.LoggingEarlyStoppingListener listener = new TestEarlyStopping.LoggingEarlyStoppingListener();
        IEarlyStoppingTrainer trainer = new EarlyStoppingTrainer(esConf, net, irisIter, listener);
        trainer.fit();
        Assert.assertEquals(1, listener.onStartCallCount);
        Assert.assertEquals(5, listener.onEpochCallCount);
        Assert.assertEquals(1, listener.onCompletionCallCount);
    }

    private static class LoggingEarlyStoppingListener implements EarlyStoppingListener<MultiLayerNetwork> {
        private static Logger log = LoggerFactory.getLogger(TestEarlyStopping.LoggingEarlyStoppingListener.class);

        private int onStartCallCount = 0;

        private int onEpochCallCount = 0;

        private int onCompletionCallCount = 0;

        @Override
        public void onStart(EarlyStoppingConfiguration esConfig, MultiLayerNetwork net) {
            TestEarlyStopping.LoggingEarlyStoppingListener.log.info("EarlyStopping: onStart called");
            (onStartCallCount)++;
        }

        @Override
        public void onEpoch(int epochNum, double score, EarlyStoppingConfiguration esConfig, MultiLayerNetwork net) {
            TestEarlyStopping.LoggingEarlyStoppingListener.log.info("EarlyStopping: onEpoch called (epochNum={}, score={}}", epochNum, score);
            (onEpochCallCount)++;
        }

        @Override
        public void onCompletion(EarlyStoppingResult esResult) {
            TestEarlyStopping.LoggingEarlyStoppingListener.log.info("EarlyStopping: onCompletion called (result: {})", esResult);
            (onCompletionCallCount)++;
        }
    }

    @Test
    public void testRegressionScoreFunctionSimple() throws Exception {
        for (RegressionEvaluation.Metric metric : new RegressionEvaluation.Metric[]{ MSE, MAE }) {
            log.info(("Metric: " + metric));
            MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().list().layer(new DenseLayer.Builder().nIn(784).nOut(32).build()).layer(new OutputLayer.Builder().nIn(32).nOut(784).activation(SIGMOID).lossFunction(MSE).build()).build();
            MultiLayerNetwork net = new MultiLayerNetwork(conf);
            net.init();
            DataSetIterator iter = new MnistDataSetIterator(32, false, 12345);
            List<DataSet> l = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                DataSet ds = iter.next();
                l.add(new DataSet(ds.getFeatures(), ds.getFeatures()));
            }
            iter = new org.deeplearning4j.datasets.iterator.ExistingDataSetIterator(l);
            EarlyStoppingModelSaver<MultiLayerNetwork> saver = new org.deeplearning4j.earlystopping.saver.InMemoryModelSaver();
            EarlyStoppingConfiguration<MultiLayerNetwork> esConf = new EarlyStoppingConfiguration.Builder<MultiLayerNetwork>().epochTerminationConditions(new MaxEpochsTerminationCondition(5)).iterationTerminationConditions(new MaxTimeIterationTerminationCondition(1, TimeUnit.MINUTES)).scoreCalculator(new RegressionScoreCalculator(metric, iter)).modelSaver(saver).build();
            EarlyStoppingTrainer trainer = new EarlyStoppingTrainer(esConf, net, iter);
            EarlyStoppingResult<MultiLayerNetwork> result = trainer.fit();
            Assert.assertNotNull(result.getBestModel());
            Assert.assertTrue(((result.getBestModelScore()) > 0.0));
        }
    }

    @Test
    public void testAEScoreFunctionSimple() throws Exception {
        for (RegressionEvaluation.Metric metric : new RegressionEvaluation.Metric[]{ MSE, MAE }) {
            log.info(("Metric: " + metric));
            MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().list().layer(new AutoEncoder.Builder().nIn(784).nOut(32).build()).build();
            MultiLayerNetwork net = new MultiLayerNetwork(conf);
            net.init();
            DataSetIterator iter = new MnistDataSetIterator(32, false, 12345);
            List<DataSet> l = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                DataSet ds = iter.next();
                l.add(new DataSet(ds.getFeatures(), ds.getFeatures()));
            }
            iter = new org.deeplearning4j.datasets.iterator.ExistingDataSetIterator(l);
            EarlyStoppingModelSaver<MultiLayerNetwork> saver = new org.deeplearning4j.earlystopping.saver.InMemoryModelSaver();
            EarlyStoppingConfiguration<MultiLayerNetwork> esConf = new EarlyStoppingConfiguration.Builder<MultiLayerNetwork>().epochTerminationConditions(new MaxEpochsTerminationCondition(5)).iterationTerminationConditions(new MaxTimeIterationTerminationCondition(1, TimeUnit.MINUTES)).scoreCalculator(new AutoencoderScoreCalculator(metric, iter)).modelSaver(saver).build();
            EarlyStoppingTrainer trainer = new EarlyStoppingTrainer(esConf, net, iter);
            EarlyStoppingResult<MultiLayerNetwork> result = trainer.pretrain();
            Assert.assertNotNull(result.getBestModel());
            Assert.assertTrue(((result.getBestModelScore()) > 0.0));
        }
    }

    @Test
    public void testVAEScoreFunctionSimple() throws Exception {
        for (RegressionEvaluation.Metric metric : new RegressionEvaluation.Metric[]{ MSE, MAE }) {
            log.info(("Metric: " + metric));
            MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().list().layer(new VariationalAutoencoder.Builder().nIn(784).nOut(32).encoderLayerSizes(64).decoderLayerSizes(64).build()).build();
            MultiLayerNetwork net = new MultiLayerNetwork(conf);
            net.init();
            DataSetIterator iter = new MnistDataSetIterator(32, false, 12345);
            List<DataSet> l = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                DataSet ds = iter.next();
                l.add(new DataSet(ds.getFeatures(), ds.getFeatures()));
            }
            iter = new org.deeplearning4j.datasets.iterator.ExistingDataSetIterator(l);
            EarlyStoppingModelSaver<MultiLayerNetwork> saver = new org.deeplearning4j.earlystopping.saver.InMemoryModelSaver();
            EarlyStoppingConfiguration<MultiLayerNetwork> esConf = new EarlyStoppingConfiguration.Builder<MultiLayerNetwork>().epochTerminationConditions(new MaxEpochsTerminationCondition(5)).iterationTerminationConditions(new MaxTimeIterationTerminationCondition(1, TimeUnit.MINUTES)).scoreCalculator(new VAEReconErrorScoreCalculator(metric, iter)).modelSaver(saver).build();
            EarlyStoppingTrainer trainer = new EarlyStoppingTrainer(esConf, net, iter);
            EarlyStoppingResult<MultiLayerNetwork> result = trainer.pretrain();
            Assert.assertNotNull(result.getBestModel());
            Assert.assertTrue(((result.getBestModelScore()) > 0.0));
        }
    }

    @Test
    public void testVAEScoreFunctionReconstructionProbSimple() throws Exception {
        for (boolean logProb : new boolean[]{ false, true }) {
            MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().list().layer(new VariationalAutoencoder.Builder().nIn(784).nOut(32).encoderLayerSizes(64).decoderLayerSizes(64).reconstructionDistribution(new org.deeplearning4j.nn.conf.layers.variational.BernoulliReconstructionDistribution(Activation.SIGMOID)).build()).build();
            MultiLayerNetwork net = new MultiLayerNetwork(conf);
            net.init();
            DataSetIterator iter = new MnistDataSetIterator(32, false, 12345);
            List<DataSet> l = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                DataSet ds = iter.next();
                l.add(new DataSet(ds.getFeatures(), ds.getFeatures()));
            }
            iter = new org.deeplearning4j.datasets.iterator.ExistingDataSetIterator(l);
            EarlyStoppingModelSaver<MultiLayerNetwork> saver = new org.deeplearning4j.earlystopping.saver.InMemoryModelSaver();
            EarlyStoppingConfiguration<MultiLayerNetwork> esConf = new EarlyStoppingConfiguration.Builder<MultiLayerNetwork>().epochTerminationConditions(new MaxEpochsTerminationCondition(5)).iterationTerminationConditions(new MaxTimeIterationTerminationCondition(1, TimeUnit.MINUTES)).scoreCalculator(new VAEReconProbScoreCalculator(iter, 20, logProb)).modelSaver(saver).build();
            EarlyStoppingTrainer trainer = new EarlyStoppingTrainer(esConf, net, iter);
            EarlyStoppingResult<MultiLayerNetwork> result = trainer.pretrain();
            Assert.assertNotNull(result.getBestModel());
            Assert.assertTrue(((result.getBestModelScore()) > 0.0));
        }
    }

    @Test
    public void testClassificationScoreFunctionSimple() throws Exception {
        for (Evaluation.Metric metric : Metric.values()) {
            log.info(("Metric: " + metric));
            MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().list().layer(new DenseLayer.Builder().nIn(784).nOut(32).build()).layer(new OutputLayer.Builder().nIn(32).nOut(10).activation(SOFTMAX).build()).build();
            MultiLayerNetwork net = new MultiLayerNetwork(conf);
            net.init();
            DataSetIterator iter = new MnistDataSetIterator(32, false, 12345);
            List<DataSet> l = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                DataSet ds = iter.next();
                l.add(ds);
            }
            iter = new org.deeplearning4j.datasets.iterator.ExistingDataSetIterator(l);
            EarlyStoppingModelSaver<MultiLayerNetwork> saver = new org.deeplearning4j.earlystopping.saver.InMemoryModelSaver();
            EarlyStoppingConfiguration<MultiLayerNetwork> esConf = new EarlyStoppingConfiguration.Builder<MultiLayerNetwork>().epochTerminationConditions(new MaxEpochsTerminationCondition(5)).iterationTerminationConditions(new MaxTimeIterationTerminationCondition(1, TimeUnit.MINUTES)).scoreCalculator(new ClassificationScoreCalculator(metric, iter)).modelSaver(saver).build();
            EarlyStoppingTrainer trainer = new EarlyStoppingTrainer(esConf, net, iter);
            EarlyStoppingResult<MultiLayerNetwork> result = trainer.fit();
            Assert.assertNotNull(result.getBestModel());
        }
    }

    @Test
    public void testEarlyStoppingListeners() {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().updater(new Sgd(0.001)).weightInit(XAVIER).list().layer(0, new OutputLayer.Builder().nIn(4).nOut(3).activation(SOFTMAX).lossFunction(MCXENT).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        TestEarlyStopping.TestListener tl = new TestEarlyStopping.TestListener();
        net.setListeners(tl);
        DataSetIterator irisIter = new IrisDataSetIterator(50, 150);
        EarlyStoppingModelSaver<MultiLayerNetwork> saver = new org.deeplearning4j.earlystopping.saver.InMemoryModelSaver();
        EarlyStoppingConfiguration<MultiLayerNetwork> esConf = new EarlyStoppingConfiguration.Builder<MultiLayerNetwork>().epochTerminationConditions(new MaxEpochsTerminationCondition(5)).iterationTerminationConditions(new MaxTimeIterationTerminationCondition(1, TimeUnit.MINUTES)).scoreCalculator(new DataSetLossCalculator(irisIter, true)).modelSaver(saver).build();
        IEarlyStoppingTrainer<MultiLayerNetwork> trainer = new EarlyStoppingTrainer(esConf, net, irisIter);
        EarlyStoppingResult<MultiLayerNetwork> result = trainer.fit();
        Assert.assertEquals(5, tl.countEpochStart);
        Assert.assertEquals(5, tl.countEpochEnd);
        Assert.assertEquals(((5 * 150) / 50), tl.iterCount);
        Assert.assertEquals(4, tl.maxEpochStart);
        Assert.assertEquals(4, tl.maxEpochEnd);
    }

    @Data
    public static class TestListener extends BaseTrainingListener {
        private int countEpochStart = 0;

        private int countEpochEnd = 0;

        private int iterCount = 0;

        private int maxEpochStart = -1;

        private int maxEpochEnd = -1;

        @Override
        public void onEpochStart(Model model) {
            (countEpochStart)++;
            maxEpochStart = Math.max(maxEpochStart, BaseOptimizer.getEpochCount(model));
        }

        @Override
        public void onEpochEnd(Model model) {
            (countEpochEnd)++;
            maxEpochEnd = Math.max(maxEpochEnd, BaseOptimizer.getEpochCount(model));
        }

        @Override
        public void iterationDone(Model model, int iteration, int epoch) {
            (iterCount)++;
        }
    }

    @Test
    public void testEarlyStoppingMaximizeScore() throws Exception {
        Nd4j.getRandom().setSeed(12345);
        int outputs = 2;
        DataSet ds = new DataSet(Nd4j.rand(new int[]{ 3, 10, 50 }), TestUtils.randomOneHotTimeSeries(3, outputs, 50, 12345));
        DataSetIterator train = new org.deeplearning4j.datasets.iterator.ExistingDataSetIterator(Arrays.asList(ds, ds, ds, ds, ds, ds, ds, ds, ds, ds));
        DataSetIterator test = new org.deeplearning4j.datasets.iterator.impl.SingletonDataSetIterator(ds);
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(123).weightInit(XAVIER).updater(new Adam(0.1)).activation(ELU).l2(1.0E-5).gradientNormalization(ClipElementWiseAbsoluteValue).gradientNormalizationThreshold(1.0).list().layer(0, new LSTM.Builder().nIn(10).nOut(10).activation(TANH).gateActivationFunction(SIGMOID).dropOut(0.5).build()).layer(1, new RnnOutputLayer.Builder().nIn(10).nOut(outputs).activation(SOFTMAX).lossFunction(MCXENT).build()).build();
        File f = testDir.newFolder();
        EarlyStoppingModelSaver<MultiLayerNetwork> saver = new LocalFileModelSaver(f.getAbsolutePath());
        EarlyStoppingConfiguration<MultiLayerNetwork> esConf = new EarlyStoppingConfiguration.Builder<MultiLayerNetwork>().epochTerminationConditions(new MaxEpochsTerminationCondition(10), new ScoreImprovementEpochTerminationCondition(1)).iterationTerminationConditions(new MaxTimeIterationTerminationCondition(10, TimeUnit.MINUTES)).scoreCalculator(new ClassificationScoreCalculator(Metric.F1, test)).modelSaver(saver).saveLastModel(true).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        EarlyStoppingTrainer t = new EarlyStoppingTrainer(esConf, net, train);
        EarlyStoppingResult<MultiLayerNetwork> result = t.fit();
        Map<Integer, Double> map = result.getScoreVsEpoch();
        for (int i = 1; i < (map.size()); i++) {
            if (i == ((map.size()) - 1)) {
                Assert.assertTrue(((map.get(i)) < (+(map.get((i - 1))))));
            } else {
                Assert.assertTrue(((map.get(i)) > (map.get((i - 1)))));
            }
        }
    }

    @Test
    public void testConditionJson() throws Exception {
        EpochTerminationCondition[] etc = new EpochTerminationCondition[]{ new BestScoreEpochTerminationCondition(0.5), new MaxEpochsTerminationCondition(10), new ScoreImprovementEpochTerminationCondition(3, 0.5) };
        for (EpochTerminationCondition e : etc) {
            String s = NeuralNetConfiguration.mapper().writeValueAsString(e);
            EpochTerminationCondition c = NeuralNetConfiguration.mapper().readValue(s, EpochTerminationCondition.class);
            Assert.assertEquals(e, c);
        }
        IterationTerminationCondition[] itc = new IterationTerminationCondition[]{ new InvalidScoreIterationTerminationCondition(), new MaxScoreIterationTerminationCondition(10.0), new MaxTimeIterationTerminationCondition(10, TimeUnit.MINUTES) };
        for (IterationTerminationCondition i : itc) {
            String s = NeuralNetConfiguration.mapper().writeValueAsString(i);
            IterationTerminationCondition c = NeuralNetConfiguration.mapper().readValue(s, IterationTerminationCondition.class);
            Assert.assertEquals(i, c);
        }
    }
}

