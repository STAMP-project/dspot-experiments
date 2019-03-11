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
package org.deeplearning4j.spark;


import Activation.IDENTITY;
import EarlyStoppingResult.TerminationReason.EpochTerminationCondition;
import EarlyStoppingResult.TerminationReason.IterationTerminationCondition;
import LossFunctions.LossFunction.MCXENT;
import LossFunctions.LossFunction.MSE;
import OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT;
import WeightInit.XAVIER;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.spark.api.java.JavaRDD;
import org.deeplearning4j.datasets.iterator.impl.IrisDataSetIterator;
import org.deeplearning4j.earlystopping.EarlyStoppingConfiguration;
import org.deeplearning4j.earlystopping.EarlyStoppingModelSaver;
import org.deeplearning4j.earlystopping.EarlyStoppingResult;
import org.deeplearning4j.earlystopping.listener.EarlyStoppingListener;
import org.deeplearning4j.earlystopping.termination.MaxEpochsTerminationCondition;
import org.deeplearning4j.earlystopping.termination.MaxScoreIterationTerminationCondition;
import org.deeplearning4j.earlystopping.termination.MaxTimeIterationTerminationCondition;
import org.deeplearning4j.earlystopping.termination.ScoreImprovementEpochTerminationCondition;
import org.deeplearning4j.earlystopping.trainer.IEarlyStoppingTrainer;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.spark.api.TrainingMaster;
import org.deeplearning4j.spark.impl.graph.dataset.DataSetToMultiDataSetFn;
import org.deeplearning4j.spark.impl.paramavg.ParameterAveragingTrainingMaster;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Sgd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestEarlyStoppingSparkCompGraph extends BaseSparkTest {
    @Test
    public void testEarlyStoppingIris() {
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).updater(new Sgd()).weightInit(XAVIER).graphBuilder().addInputs("in").addLayer("0", new OutputLayer.Builder().nIn(4).nOut(3).lossFunction(MCXENT).build(), "in").setOutputs("0").build();
        ComputationGraph net = new ComputationGraph(conf);
        net.setListeners(new ScoreIterationListener(1));
        JavaRDD<DataSet> irisData = getIris();
        EarlyStoppingModelSaver<ComputationGraph> saver = new org.deeplearning4j.earlystopping.saver.InMemoryModelSaver();
        EarlyStoppingConfiguration<ComputationGraph> esConf = new EarlyStoppingConfiguration.Builder<ComputationGraph>().epochTerminationConditions(new MaxEpochsTerminationCondition(5)).iterationTerminationConditions(new MaxTimeIterationTerminationCondition(1, TimeUnit.MINUTES)).scoreCalculator(new org.deeplearning4j.spark.earlystopping.SparkLossCalculatorComputationGraph(irisData.map(new DataSetToMultiDataSetFn()), true, sc.sc())).modelSaver(saver).build();
        TrainingMaster tm = new ParameterAveragingTrainingMaster(true, numExecutors(), 1, 10, 1, 0);
        IEarlyStoppingTrainer<ComputationGraph> trainer = new org.deeplearning4j.spark.earlystopping.SparkEarlyStoppingGraphTrainer(getContext().sc(), tm, esConf, net, irisData.map(new DataSetToMultiDataSetFn()));
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
        double score = bestNetwork.score(new IrisDataSetIterator(150, 150).next());
        double bestModelScore = result.getBestModelScore();
        Assert.assertEquals(bestModelScore, score, 0.001);
    }

    @Test
    public void testBadTuning() {
        // Test poor tuning (high LR): should terminate on MaxScoreIterationTerminationCondition
        Nd4j.getRandom().setSeed(12345);
        ComputationGraphConfiguration conf = // Intentionally huge LR
        new NeuralNetConfiguration.Builder().seed(12345).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).updater(new Sgd(2.0)).weightInit(XAVIER).graphBuilder().addInputs("in").addLayer("0", new OutputLayer.Builder().nIn(4).nOut(3).activation(IDENTITY).lossFunction(MSE).build(), "in").setOutputs("0").build();
        ComputationGraph net = new ComputationGraph(conf);
        net.setListeners(new ScoreIterationListener(1));
        JavaRDD<DataSet> irisData = getIris();
        EarlyStoppingModelSaver<ComputationGraph> saver = new org.deeplearning4j.earlystopping.saver.InMemoryModelSaver();
        EarlyStoppingConfiguration<ComputationGraph> esConf = // Initial score is ~2.5
        new EarlyStoppingConfiguration.Builder<ComputationGraph>().epochTerminationConditions(new MaxEpochsTerminationCondition(5000)).iterationTerminationConditions(new MaxTimeIterationTerminationCondition(1, TimeUnit.MINUTES), new MaxScoreIterationTerminationCondition(7.5)).scoreCalculator(new org.deeplearning4j.spark.earlystopping.SparkLossCalculatorComputationGraph(irisData.map(new DataSetToMultiDataSetFn()), true, sc.sc())).modelSaver(saver).build();
        TrainingMaster tm = new ParameterAveragingTrainingMaster(true, numExecutors(), 1, 10, 1, 0);
        IEarlyStoppingTrainer<ComputationGraph> trainer = new org.deeplearning4j.spark.earlystopping.SparkEarlyStoppingGraphTrainer(getContext().sc(), tm, esConf, net, irisData.map(new DataSetToMultiDataSetFn()));
        EarlyStoppingResult result = trainer.fit();
        Assert.assertTrue(((result.getTotalEpochs()) < 5));
        Assert.assertEquals(IterationTerminationCondition, result.getTerminationReason());
        String expDetails = new MaxScoreIterationTerminationCondition(7.5).toString();
        Assert.assertEquals(expDetails, result.getTerminationDetails());
    }

    @Test
    public void testTimeTermination() {
        // test termination after max time
        Nd4j.getRandom().setSeed(12345);
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).updater(new Sgd(1.0E-6)).weightInit(XAVIER).graphBuilder().addInputs("in").addLayer("0", new OutputLayer.Builder().nIn(4).nOut(3).lossFunction(MCXENT).build(), "in").setOutputs("0").build();
        ComputationGraph net = new ComputationGraph(conf);
        net.setListeners(new ScoreIterationListener(1));
        JavaRDD<DataSet> irisData = getIris();
        EarlyStoppingModelSaver<ComputationGraph> saver = new org.deeplearning4j.earlystopping.saver.InMemoryModelSaver();
        EarlyStoppingConfiguration<ComputationGraph> esConf = // Initial score is ~2.5
        new EarlyStoppingConfiguration.Builder<ComputationGraph>().epochTerminationConditions(new MaxEpochsTerminationCondition(10000)).iterationTerminationConditions(new MaxTimeIterationTerminationCondition(3, TimeUnit.SECONDS), new MaxScoreIterationTerminationCondition(7.5)).scoreCalculator(new org.deeplearning4j.spark.earlystopping.SparkLossCalculatorComputationGraph(irisData.map(new DataSetToMultiDataSetFn()), true, sc.sc())).modelSaver(saver).build();
        TrainingMaster tm = new ParameterAveragingTrainingMaster(true, numExecutors(), 1, 10, 1, 0);
        IEarlyStoppingTrainer<ComputationGraph> trainer = new org.deeplearning4j.spark.earlystopping.SparkEarlyStoppingGraphTrainer(getContext().sc(), tm, esConf, net, irisData.map(new DataSetToMultiDataSetFn()));
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
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).updater(new Sgd(0.0)).weightInit(XAVIER).graphBuilder().addInputs("in").addLayer("0", new OutputLayer.Builder().nIn(4).nOut(3).lossFunction(MCXENT).build(), "in").setOutputs("0").build();
        ComputationGraph net = new ComputationGraph(conf);
        net.setListeners(new ScoreIterationListener(1));
        JavaRDD<DataSet> irisData = getIris();
        EarlyStoppingModelSaver<ComputationGraph> saver = new org.deeplearning4j.earlystopping.saver.InMemoryModelSaver();
        EarlyStoppingConfiguration<ComputationGraph> esConf = // Initial score is ~2.5
        new EarlyStoppingConfiguration.Builder<ComputationGraph>().epochTerminationConditions(new MaxEpochsTerminationCondition(100), new ScoreImprovementEpochTerminationCondition(5)).iterationTerminationConditions(new MaxScoreIterationTerminationCondition(7.5)).scoreCalculator(new org.deeplearning4j.spark.earlystopping.SparkLossCalculatorComputationGraph(irisData.map(new DataSetToMultiDataSetFn()), true, sc.sc())).modelSaver(saver).build();
        TrainingMaster tm = new ParameterAveragingTrainingMaster(true, numExecutors(), 1, 10, 1, 0);
        IEarlyStoppingTrainer<ComputationGraph> trainer = new org.deeplearning4j.spark.earlystopping.SparkEarlyStoppingGraphTrainer(getContext().sc(), tm, esConf, net, irisData.map(new DataSetToMultiDataSetFn()));
        EarlyStoppingResult result = trainer.fit();
        // Expect no score change due to 0 LR -> terminate after 6 total epochs
        Assert.assertTrue(((result.getTotalEpochs()) < 12));// Normally expect 6 epochs exactly; get a little more than that here due to rounding + order of operations

        Assert.assertEquals(EpochTerminationCondition, result.getTerminationReason());
        String expDetails = new ScoreImprovementEpochTerminationCondition(5).toString();
        Assert.assertEquals(expDetails, result.getTerminationDetails());
    }

    @Test
    public void testListeners() {
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).updater(new Sgd()).weightInit(XAVIER).graphBuilder().addInputs("in").addLayer("0", new OutputLayer.Builder().nIn(4).nOut(3).lossFunction(MCXENT).build(), "in").setOutputs("0").build();
        ComputationGraph net = new ComputationGraph(conf);
        net.setListeners(new ScoreIterationListener(1));
        JavaRDD<DataSet> irisData = getIris();
        EarlyStoppingModelSaver<ComputationGraph> saver = new org.deeplearning4j.earlystopping.saver.InMemoryModelSaver();
        EarlyStoppingConfiguration<ComputationGraph> esConf = new EarlyStoppingConfiguration.Builder<ComputationGraph>().epochTerminationConditions(new MaxEpochsTerminationCondition(5)).iterationTerminationConditions(new MaxTimeIterationTerminationCondition(1, TimeUnit.MINUTES)).scoreCalculator(new org.deeplearning4j.spark.earlystopping.SparkLossCalculatorComputationGraph(irisData.map(new DataSetToMultiDataSetFn()), true, sc.sc())).modelSaver(saver).build();
        TestEarlyStoppingSparkCompGraph.LoggingEarlyStoppingListener listener = new TestEarlyStoppingSparkCompGraph.LoggingEarlyStoppingListener();
        TrainingMaster tm = new ParameterAveragingTrainingMaster(true, numExecutors(), 1, 10, 1, 0);
        IEarlyStoppingTrainer<ComputationGraph> trainer = new org.deeplearning4j.spark.earlystopping.SparkEarlyStoppingGraphTrainer(getContext().sc(), tm, esConf, net, irisData.map(new DataSetToMultiDataSetFn()));
        trainer.setListener(listener);
        trainer.fit();
        Assert.assertEquals(1, listener.onStartCallCount);
        Assert.assertEquals(5, listener.onEpochCallCount);
        Assert.assertEquals(1, listener.onCompletionCallCount);
    }

    private static class LoggingEarlyStoppingListener implements EarlyStoppingListener<ComputationGraph> {
        private static Logger log = LoggerFactory.getLogger(TestEarlyStoppingSparkCompGraph.LoggingEarlyStoppingListener.class);

        private int onStartCallCount = 0;

        private int onEpochCallCount = 0;

        private int onCompletionCallCount = 0;

        @Override
        public void onStart(EarlyStoppingConfiguration esConfig, ComputationGraph net) {
            TestEarlyStoppingSparkCompGraph.LoggingEarlyStoppingListener.log.info("EarlyStopping: onStart called");
            (onStartCallCount)++;
        }

        @Override
        public void onEpoch(int epochNum, double score, EarlyStoppingConfiguration esConfig, ComputationGraph net) {
            TestEarlyStoppingSparkCompGraph.LoggingEarlyStoppingListener.log.info("EarlyStopping: onEpoch called (epochNum={}, score={}}", epochNum, score);
            (onEpochCallCount)++;
        }

        @Override
        public void onCompletion(EarlyStoppingResult esResult) {
            TestEarlyStoppingSparkCompGraph.LoggingEarlyStoppingListener.log.info("EorlyStopping: onCompletion called (result: {})", esResult);
            (onCompletionCallCount)++;
        }
    }
}

