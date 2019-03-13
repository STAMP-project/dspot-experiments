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


import Activation.SIGMOID;
import Activation.SOFTMAX;
import EarlyStoppingResult.TerminationReason.EpochTerminationCondition;
import EarlyStoppingResult.TerminationReason.IterationTerminationCondition;
import LossFunctions.LossFunction.MCXENT;
import LossFunctions.LossFunction.MSE;
import OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT;
import RegressionEvaluation.Metric;
import WeightInit.XAVIER;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.datasets.iterator.impl.IrisDataSetIterator;
import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.deeplearning4j.earlystopping.listener.EarlyStoppingListener;
import org.deeplearning4j.earlystopping.termination.MaxEpochsTerminationCondition;
import org.deeplearning4j.earlystopping.termination.MaxScoreIterationTerminationCondition;
import org.deeplearning4j.earlystopping.termination.MaxTimeIterationTerminationCondition;
import org.deeplearning4j.earlystopping.termination.ScoreImprovementEpochTerminationCondition;
import org.deeplearning4j.earlystopping.trainer.EarlyStoppingGraphTrainer;
import org.deeplearning4j.earlystopping.trainer.IEarlyStoppingTrainer;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.AutoEncoder;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.variational.VariationalAutoencoder;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.evaluation.regression.RegressionEvaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.learning.config.Sgd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Slf4j
public class TestEarlyStoppingCompGraph extends BaseDL4JTest {
    @Test
    public void testEarlyStoppingIris() {
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).updater(new Sgd(0.001)).weightInit(XAVIER).graphBuilder().addInputs("in").addLayer("0", new OutputLayer.Builder().nIn(4).nOut(3).activation(SOFTMAX).lossFunction(MCXENT).build(), "in").setOutputs("0").build();
        ComputationGraph net = new ComputationGraph(conf);
        net.setListeners(new ScoreIterationListener(1));
        DataSetIterator irisIter = new IrisDataSetIterator(150, 150);
        EarlyStoppingModelSaver<ComputationGraph> saver = new org.deeplearning4j.earlystopping.saver.InMemoryModelSaver();
        EarlyStoppingConfiguration<ComputationGraph> esConf = new EarlyStoppingConfiguration.Builder<ComputationGraph>().epochTerminationConditions(new MaxEpochsTerminationCondition(5)).iterationTerminationConditions(new MaxTimeIterationTerminationCondition(1, TimeUnit.MINUTES)).scoreCalculator(new DataSetLossCalculatorCG(irisIter, true)).modelSaver(saver).build();
        IEarlyStoppingTrainer<ComputationGraph> trainer = new EarlyStoppingGraphTrainer(esConf, net, irisIter);
        EarlyStoppingResult<ComputationGraph> result = trainer.fit();
        System.out.println(result);
        Assert.assertEquals(5, result.getTotalEpochs());
        Assert.assertEquals(EpochTerminationCondition, result.getTerminationReason());
        Map<Integer, Double> scoreVsIter = result.getScoreVsEpoch();
        Assert.assertEquals(5, scoreVsIter.size());
        String expDetails = esConf.getEpochTerminationConditions().get(0).toString();
        Assert.assertEquals(expDetails, result.getTerminationDetails());
        ComputationGraph out = result.getBestModel();
        Assert.assertNotNull(out);
        // Check that best score actually matches (returned model vs. manually calculated score)
        ComputationGraph bestNetwork = result.getBestModel();
        irisIter.reset();
        double score = bestNetwork.score(irisIter.next());
        Assert.assertEquals(result.getBestModelScore(), score, 0.01);
    }

    @Test
    public void testBadTuning() {
        // Test poor tuning (high LR): should terminate on MaxScoreIterationTerminationCondition
        Nd4j.getRandom().setSeed(12345);
        ComputationGraphConfiguration conf = // Intentionally huge LR
        new NeuralNetConfiguration.Builder().seed(12345).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).updater(new Sgd(5.0)).weightInit(XAVIER).graphBuilder().addInputs("in").addLayer("0", new OutputLayer.Builder().nIn(4).nOut(3).activation(SOFTMAX).lossFunction(MCXENT).build(), "in").setOutputs("0").build();
        ComputationGraph net = new ComputationGraph(conf);
        net.setListeners(new ScoreIterationListener(1));
        DataSetIterator irisIter = new IrisDataSetIterator(150, 150);
        EarlyStoppingModelSaver<ComputationGraph> saver = new org.deeplearning4j.earlystopping.saver.InMemoryModelSaver();
        EarlyStoppingConfiguration<ComputationGraph> esConf = // Initial score is ~2.5
        new EarlyStoppingConfiguration.Builder<ComputationGraph>().epochTerminationConditions(new MaxEpochsTerminationCondition(5000)).iterationTerminationConditions(new MaxTimeIterationTerminationCondition(1, TimeUnit.MINUTES), new MaxScoreIterationTerminationCondition(10)).scoreCalculator(new DataSetLossCalculatorCG(irisIter, true)).modelSaver(saver).build();
        IEarlyStoppingTrainer trainer = new EarlyStoppingGraphTrainer(esConf, net, irisIter);
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
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).updater(new Sgd(1.0E-6)).weightInit(XAVIER).graphBuilder().addInputs("in").addLayer("0", new OutputLayer.Builder().nIn(4).nOut(3).activation(SOFTMAX).lossFunction(MCXENT).build(), "in").setOutputs("0").build();
        ComputationGraph net = new ComputationGraph(conf);
        net.setListeners(new ScoreIterationListener(1));
        DataSetIterator irisIter = new IrisDataSetIterator(150, 150);
        EarlyStoppingModelSaver<ComputationGraph> saver = new org.deeplearning4j.earlystopping.saver.InMemoryModelSaver();
        EarlyStoppingConfiguration<ComputationGraph> esConf = // Initial score is ~8
        new EarlyStoppingConfiguration.Builder<ComputationGraph>().epochTerminationConditions(new MaxEpochsTerminationCondition(10000)).iterationTerminationConditions(new MaxTimeIterationTerminationCondition(3, TimeUnit.SECONDS), new MaxScoreIterationTerminationCondition(50)).scoreCalculator(new DataSetLossCalculator(irisIter, true)).modelSaver(saver).build();
        IEarlyStoppingTrainer trainer = new EarlyStoppingGraphTrainer(esConf, net, irisIter);
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
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).updater(new Sgd(0.0)).weightInit(XAVIER).graphBuilder().addInputs("in").addLayer("0", new OutputLayer.Builder().nIn(4).nOut(3).activation(SOFTMAX).lossFunction(MCXENT).build(), "in").setOutputs("0").build();
        ComputationGraph net = new ComputationGraph(conf);
        net.setListeners(new ScoreIterationListener(1));
        DataSetIterator irisIter = new IrisDataSetIterator(150, 150);
        EarlyStoppingModelSaver<ComputationGraph> saver = new org.deeplearning4j.earlystopping.saver.InMemoryModelSaver();
        EarlyStoppingConfiguration<ComputationGraph> esConf = // Initial score is ~8
        new EarlyStoppingConfiguration.Builder<ComputationGraph>().epochTerminationConditions(new MaxEpochsTerminationCondition(100), new ScoreImprovementEpochTerminationCondition(5)).iterationTerminationConditions(new MaxTimeIterationTerminationCondition(3, TimeUnit.SECONDS), new MaxScoreIterationTerminationCondition(50)).scoreCalculator(new DataSetLossCalculatorCG(irisIter, true)).modelSaver(saver).build();
        IEarlyStoppingTrainer trainer = new EarlyStoppingGraphTrainer(esConf, net, irisIter);
        EarlyStoppingResult result = trainer.fit();
        // Expect no score change due to 0 LR -> terminate after 6 total epochs
        Assert.assertEquals(6, result.getTotalEpochs());
        Assert.assertEquals(0, result.getBestModelEpoch());
        Assert.assertEquals(EpochTerminationCondition, result.getTerminationReason());
        String expDetails = new ScoreImprovementEpochTerminationCondition(5).toString();
        Assert.assertEquals(expDetails, result.getTerminationDetails());
    }

    @Test
    public void testListeners() {
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).updater(new Sgd(0.001)).weightInit(XAVIER).graphBuilder().addInputs("in").addLayer("0", new OutputLayer.Builder().nIn(4).nOut(3).activation(SOFTMAX).lossFunction(MCXENT).build(), "in").setOutputs("0").build();
        ComputationGraph net = new ComputationGraph(conf);
        net.setListeners(new ScoreIterationListener(1));
        DataSetIterator irisIter = new IrisDataSetIterator(150, 150);
        EarlyStoppingModelSaver<ComputationGraph> saver = new org.deeplearning4j.earlystopping.saver.InMemoryModelSaver();
        EarlyStoppingConfiguration<ComputationGraph> esConf = new EarlyStoppingConfiguration.Builder<ComputationGraph>().epochTerminationConditions(new MaxEpochsTerminationCondition(5)).iterationTerminationConditions(new MaxTimeIterationTerminationCondition(1, TimeUnit.MINUTES)).scoreCalculator(new DataSetLossCalculatorCG(irisIter, true)).modelSaver(saver).build();
        TestEarlyStoppingCompGraph.LoggingEarlyStoppingListener listener = new TestEarlyStoppingCompGraph.LoggingEarlyStoppingListener();
        IEarlyStoppingTrainer trainer = new EarlyStoppingGraphTrainer(esConf, net, irisIter, listener);
        trainer.fit();
        Assert.assertEquals(1, listener.onStartCallCount);
        Assert.assertEquals(5, listener.onEpochCallCount);
        Assert.assertEquals(1, listener.onCompletionCallCount);
    }

    private static class LoggingEarlyStoppingListener implements EarlyStoppingListener<ComputationGraph> {
        private static Logger log = LoggerFactory.getLogger(TestEarlyStoppingCompGraph.LoggingEarlyStoppingListener.class);

        private int onStartCallCount = 0;

        private int onEpochCallCount = 0;

        private int onCompletionCallCount = 0;

        @Override
        public void onStart(EarlyStoppingConfiguration esConfig, ComputationGraph net) {
            TestEarlyStoppingCompGraph.LoggingEarlyStoppingListener.log.info("EarlyStopping: onStart called");
            (onStartCallCount)++;
        }

        @Override
        public void onEpoch(int epochNum, double score, EarlyStoppingConfiguration esConfig, ComputationGraph net) {
            TestEarlyStoppingCompGraph.LoggingEarlyStoppingListener.log.info("EarlyStopping: onEpoch called (epochNum={}, score={}}", epochNum, score);
            (onEpochCallCount)++;
        }

        @Override
        public void onCompletion(EarlyStoppingResult esResult) {
            TestEarlyStoppingCompGraph.LoggingEarlyStoppingListener.log.info("EarlyStopping: onCompletion called (result: {})", esResult);
            (onCompletionCallCount)++;
        }
    }

    @Test
    public void testRegressionScoreFunctionSimple() throws Exception {
        for (RegressionEvaluation.Metric metric : new RegressionEvaluation.Metric[]{ Metric.MSE, Metric.MAE }) {
            log.info(("Metric: " + metric));
            ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().graphBuilder().addInputs("in").layer("0", new DenseLayer.Builder().nIn(784).nOut(32).build(), "in").layer("1", new OutputLayer.Builder().nIn(32).nOut(784).activation(SIGMOID).lossFunction(MSE).build(), "0").setOutputs("1").build();
            ComputationGraph net = new ComputationGraph(conf);
            net.init();
            DataSetIterator iter = new MnistDataSetIterator(32, false, 12345);
            List<DataSet> l = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                DataSet ds = iter.next();
                l.add(new DataSet(ds.getFeatures(), ds.getFeatures()));
            }
            iter = new org.deeplearning4j.datasets.iterator.ExistingDataSetIterator(l);
            EarlyStoppingModelSaver<ComputationGraph> saver = new org.deeplearning4j.earlystopping.saver.InMemoryModelSaver();
            EarlyStoppingConfiguration<ComputationGraph> esConf = new EarlyStoppingConfiguration.Builder<ComputationGraph>().epochTerminationConditions(new MaxEpochsTerminationCondition(5)).iterationTerminationConditions(new MaxTimeIterationTerminationCondition(1, TimeUnit.MINUTES)).scoreCalculator(new RegressionScoreCalculator(metric, iter)).modelSaver(saver).build();
            EarlyStoppingGraphTrainer trainer = new EarlyStoppingGraphTrainer(esConf, net, iter);
            EarlyStoppingResult<ComputationGraph> result = trainer.fit();
            Assert.assertNotNull(result.getBestModel());
            Assert.assertTrue(((result.getBestModelScore()) > 0.0));
        }
    }

    @Test
    public void testAEScoreFunctionSimple() throws Exception {
        DataType dt = Nd4j.defaultFloatingPointType();
        for (RegressionEvaluation.Metric metric : new RegressionEvaluation.Metric[]{ Metric.MSE, Metric.MAE }) {
            log.info(("Metric: " + metric));
            ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().graphBuilder().addInputs("in").layer("0", new AutoEncoder.Builder().nIn(784).nOut(32).build(), "in").setOutputs("0").build();
            ComputationGraph net = new ComputationGraph(conf);
            net.init();
            DataSetIterator iter = new MnistDataSetIterator(32, false, 12345);
            List<DataSet> l = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                DataSet ds = iter.next();
                l.add(new DataSet(ds.getFeatures(), ds.getFeatures()));
            }
            iter = new org.deeplearning4j.datasets.iterator.ExistingDataSetIterator(l);
            EarlyStoppingModelSaver<ComputationGraph> saver = new org.deeplearning4j.earlystopping.saver.InMemoryModelSaver();
            EarlyStoppingConfiguration<ComputationGraph> esConf = new EarlyStoppingConfiguration.Builder<ComputationGraph>().epochTerminationConditions(new MaxEpochsTerminationCondition(5)).iterationTerminationConditions(new MaxTimeIterationTerminationCondition(1, TimeUnit.MINUTES)).scoreCalculator(new AutoencoderScoreCalculator(metric, iter)).modelSaver(saver).build();
            EarlyStoppingGraphTrainer trainer = new EarlyStoppingGraphTrainer(esConf, net, iter);
            EarlyStoppingResult<ComputationGraph> result = trainer.pretrain();
            Assert.assertNotNull(result.getBestModel());
            Assert.assertTrue(((result.getBestModelScore()) > 0.0));
        }
    }

    @Test
    public void testVAEScoreFunctionSimple() throws Exception {
        for (RegressionEvaluation.Metric metric : new RegressionEvaluation.Metric[]{ Metric.MSE, Metric.MAE }) {
            log.info(("Metric: " + metric));
            ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().graphBuilder().addInputs("in").layer("0", new VariationalAutoencoder.Builder().nIn(784).nOut(32).encoderLayerSizes(64).decoderLayerSizes(64).build(), "in").setOutputs("0").build();
            ComputationGraph net = new ComputationGraph(conf);
            net.init();
            DataSetIterator iter = new MnistDataSetIterator(32, false, 12345);
            List<DataSet> l = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                DataSet ds = iter.next();
                l.add(new DataSet(ds.getFeatures(), ds.getFeatures()));
            }
            iter = new org.deeplearning4j.datasets.iterator.ExistingDataSetIterator(l);
            EarlyStoppingModelSaver<ComputationGraph> saver = new org.deeplearning4j.earlystopping.saver.InMemoryModelSaver();
            EarlyStoppingConfiguration<ComputationGraph> esConf = new EarlyStoppingConfiguration.Builder<ComputationGraph>().epochTerminationConditions(new MaxEpochsTerminationCondition(5)).iterationTerminationConditions(new MaxTimeIterationTerminationCondition(1, TimeUnit.MINUTES)).scoreCalculator(new VAEReconErrorScoreCalculator(metric, iter)).modelSaver(saver).build();
            EarlyStoppingGraphTrainer trainer = new EarlyStoppingGraphTrainer(esConf, net, iter);
            EarlyStoppingResult<ComputationGraph> result = trainer.pretrain();
            Assert.assertNotNull(result.getBestModel());
            Assert.assertTrue(((result.getBestModelScore()) > 0.0));
        }
    }

    @Test
    public void testVAEScoreFunctionReconstructionProbSimple() throws Exception {
        for (boolean logProb : new boolean[]{ false, true }) {
            ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().updater(new Adam(1.0E-5)).graphBuilder().addInputs("in").layer("0", new VariationalAutoencoder.Builder().nIn(784).nOut(32).encoderLayerSizes(64).decoderLayerSizes(64).reconstructionDistribution(new org.deeplearning4j.nn.conf.layers.variational.BernoulliReconstructionDistribution(Activation.SIGMOID)).build(), "in").setOutputs("0").build();
            ComputationGraph net = new ComputationGraph(conf);
            net.init();
            DataSetIterator iter = new MnistDataSetIterator(32, false, 12345);
            List<DataSet> l = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                DataSet ds = iter.next();
                l.add(new DataSet(ds.getFeatures(), ds.getFeatures()));
            }
            iter = new org.deeplearning4j.datasets.iterator.ExistingDataSetIterator(l);
            EarlyStoppingModelSaver<ComputationGraph> saver = new org.deeplearning4j.earlystopping.saver.InMemoryModelSaver();
            EarlyStoppingConfiguration<ComputationGraph> esConf = new EarlyStoppingConfiguration.Builder<ComputationGraph>().epochTerminationConditions(new MaxEpochsTerminationCondition(5)).iterationTerminationConditions(new MaxTimeIterationTerminationCondition(1, TimeUnit.MINUTES)).scoreCalculator(new VAEReconProbScoreCalculator(iter, 20, logProb)).modelSaver(saver).build();
            EarlyStoppingGraphTrainer trainer = new EarlyStoppingGraphTrainer(esConf, net, iter);
            EarlyStoppingResult<ComputationGraph> result = trainer.pretrain();
            Assert.assertNotNull(result.getBestModel());
            Assert.assertTrue(((result.getBestModelScore()) > 0.0));
        }
    }

    @Test
    public void testClassificationScoreFunctionSimple() throws Exception {
        for (Evaluation.Metric metric : Evaluation.Metric.values()) {
            log.info(("Metric: " + metric));
            ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().graphBuilder().addInputs("in").layer("0", new DenseLayer.Builder().nIn(784).nOut(32).build(), "in").layer("1", new OutputLayer.Builder().nIn(32).nOut(10).activation(SOFTMAX).build(), "0").setOutputs("1").build();
            ComputationGraph net = new ComputationGraph(conf);
            net.init();
            DataSetIterator iter = new MnistDataSetIterator(32, false, 12345);
            List<DataSet> l = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                DataSet ds = iter.next();
                l.add(ds);
            }
            iter = new org.deeplearning4j.datasets.iterator.ExistingDataSetIterator(l);
            EarlyStoppingModelSaver<ComputationGraph> saver = new org.deeplearning4j.earlystopping.saver.InMemoryModelSaver();
            EarlyStoppingConfiguration<ComputationGraph> esConf = new EarlyStoppingConfiguration.Builder<ComputationGraph>().epochTerminationConditions(new MaxEpochsTerminationCondition(5)).iterationTerminationConditions(new MaxTimeIterationTerminationCondition(1, TimeUnit.MINUTES)).scoreCalculator(new ClassificationScoreCalculator(metric, iter)).modelSaver(saver).build();
            EarlyStoppingGraphTrainer trainer = new EarlyStoppingGraphTrainer(esConf, net, iter);
            EarlyStoppingResult<ComputationGraph> result = trainer.fit();
            Assert.assertNotNull(result.getBestModel());
        }
    }

    @Test
    public void testEarlyStoppingListenersCG() {
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().updater(new Sgd(0.001)).weightInit(XAVIER).graphBuilder().addInputs("in").layer("0", new OutputLayer.Builder().nIn(4).nOut(3).activation(SOFTMAX).lossFunction(MCXENT).build(), "in").setOutputs("0").build();
        ComputationGraph net = new ComputationGraph(conf);
        TestEarlyStopping.TestListener tl = new TestEarlyStopping.TestListener();
        net.setListeners(tl);
        DataSetIterator irisIter = new IrisDataSetIterator(50, 150);
        EarlyStoppingModelSaver<ComputationGraph> saver = new org.deeplearning4j.earlystopping.saver.InMemoryModelSaver();
        EarlyStoppingConfiguration<ComputationGraph> esConf = new EarlyStoppingConfiguration.Builder<ComputationGraph>().epochTerminationConditions(new MaxEpochsTerminationCondition(5)).iterationTerminationConditions(new MaxTimeIterationTerminationCondition(1, TimeUnit.MINUTES)).scoreCalculator(new DataSetLossCalculator(irisIter, true)).modelSaver(saver).build();
        IEarlyStoppingTrainer<ComputationGraph> trainer = new EarlyStoppingGraphTrainer(esConf, net, irisIter);
        trainer.fit();
        Assert.assertEquals(5, getCountEpochStart());
        Assert.assertEquals(5, getCountEpochEnd());
        Assert.assertEquals(((5 * 150) / 50), getIterCount());
        Assert.assertEquals(4, getMaxEpochStart());
        Assert.assertEquals(4, getMaxEpochEnd());
    }
}

