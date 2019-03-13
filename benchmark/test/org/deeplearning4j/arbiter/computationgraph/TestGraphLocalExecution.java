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
package org.deeplearning4j.arbiter.computationgraph;


import Activation.SOFTMAX;
import DataSetIteratorFactoryProvider.FACTORY_KEY;
import LossFunctions.LossFunction.MCXENT;
import OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.arbiter.ComputationGraphSpace;
import org.deeplearning4j.arbiter.evaluator.multilayer.ClassificationEvaluator;
import org.deeplearning4j.arbiter.layers.DenseLayerSpace;
import org.deeplearning4j.arbiter.layers.OutputLayerSpace;
import org.deeplearning4j.arbiter.multilayernetwork.TestDL4JLocalExecution;
import org.deeplearning4j.arbiter.optimize.api.CandidateGenerator;
import org.deeplearning4j.arbiter.optimize.api.data.DataProvider;
import org.deeplearning4j.arbiter.optimize.api.data.DataSetIteratorFactoryProvider;
import org.deeplearning4j.arbiter.optimize.api.data.DataSource;
import org.deeplearning4j.arbiter.optimize.api.saving.ResultReference;
import org.deeplearning4j.arbiter.optimize.api.termination.MaxCandidatesCondition;
import org.deeplearning4j.arbiter.optimize.api.termination.MaxTimeCondition;
import org.deeplearning4j.arbiter.optimize.config.OptimizationConfiguration;
import org.deeplearning4j.arbiter.optimize.parameter.continuous.ContinuousParameterSpace;
import org.deeplearning4j.arbiter.optimize.parameter.integer.IntegerParameterSpace;
import org.deeplearning4j.arbiter.optimize.runner.IOptimizationRunner;
import org.deeplearning4j.arbiter.saver.local.FileModelSaver;
import org.deeplearning4j.arbiter.scoring.ScoreFunctions;
import org.deeplearning4j.arbiter.scoring.impl.TestSetLossScoreFunction;
import org.deeplearning4j.arbiter.task.ComputationGraphTaskCreator;
import org.deeplearning4j.arbiter.util.TestDataFactoryProviderMnist;
import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.deeplearning4j.datasets.iterator.impl.MultiDataSetIteratorAdapter;
import org.deeplearning4j.earlystopping.EarlyStoppingConfiguration;
import org.deeplearning4j.earlystopping.saver.InMemoryModelSaver;
import org.deeplearning4j.earlystopping.scorecalc.ScoreCalculator;
import org.deeplearning4j.earlystopping.termination.MaxEpochsTerminationCondition;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.iterator.MultiDataSetIterator;
import org.nd4j.linalg.function.Supplier;
import org.nd4j.shade.jackson.annotation.JsonProperty;


@Slf4j
public class TestGraphLocalExecution {
    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

    @Test
    public void testLocalExecutionDataSources() throws Exception {
        for (int dataApproach = 0; dataApproach < 3; dataApproach++) {
            log.info("////////////////// Starting Test: {} ///////////////////", dataApproach);
            // Define: network config (hyperparameter space)
            ComputationGraphSpace mls = // 1-2 identical layers (except nIn)
            new ComputationGraphSpace.Builder().optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).updater(new org.deeplearning4j.arbiter.conf.updater.SgdSpace(new ContinuousParameterSpace(1.0E-4, 0.1))).l2(new ContinuousParameterSpace(1.0E-4, 0.01)).addInputs("in").addLayer("0", new DenseLayerSpace.Builder().nIn(784).nOut(new IntegerParameterSpace(10, 20)).activation(new org.deeplearning4j.arbiter.optimize.parameter.discrete.DiscreteParameterSpace(Activation.RELU, Activation.TANH)).build(), "in").addLayer("1", new OutputLayerSpace.Builder().nOut(10).activation(SOFTMAX).lossFunction(MCXENT).build(), "0").setOutputs("1").setInputTypes(InputType.feedForward(784)).numEpochs(3).build();
            DataProvider dp = null;
            Class<? extends DataSource> ds = null;
            Properties dsP = null;
            CandidateGenerator candidateGenerator;
            if (dataApproach == 0) {
                ds = TestDL4JLocalExecution.MnistDataSource.class;
                dsP = new Properties();
                dsP.setProperty("minibatch", "8");
                candidateGenerator = new org.deeplearning4j.arbiter.optimize.generator.RandomSearchGenerator(mls);
            } else
                if (dataApproach == 1) {
                    // DataProvider approach
                    dp = new TestDL4JLocalExecution.MnistDataProvider();
                    candidateGenerator = new org.deeplearning4j.arbiter.optimize.generator.RandomSearchGenerator(mls);
                } else {
                    // Factory approach
                    Map<String, Object> commands = new HashMap<>();
                    commands.put(FACTORY_KEY, TestDataFactoryProviderMnist.class.getCanonicalName());
                    candidateGenerator = new org.deeplearning4j.arbiter.optimize.generator.RandomSearchGenerator(mls, commands);
                    dp = new DataSetIteratorFactoryProvider();
                }

            File f = testDir.newFolder();
            File modelSave = new File(f, "modelSaveDir");
            OptimizationConfiguration configuration = new OptimizationConfiguration.Builder().candidateGenerator(candidateGenerator).dataProvider(dp).dataSource(ds, dsP).modelSaver(new FileModelSaver(modelSave)).scoreFunction(new TestSetLossScoreFunction()).terminationConditions(new MaxTimeCondition(2, TimeUnit.MINUTES), new MaxCandidatesCondition(5)).build();
            IOptimizationRunner runner = new org.deeplearning4j.arbiter.optimize.runner.LocalOptimizationRunner(configuration, new ComputationGraphTaskCreator(new ClassificationEvaluator()));
            runner.execute();
            List<ResultReference> results = runner.getResults();
            Assert.assertEquals(5, results.size());
            System.out.println((("----- COMPLETE - " + (results.size())) + " results -----"));
        }
    }

    @Test
    public void testLocalExecution() throws Exception {
        Map<String, Object> commands = new HashMap<>();
        commands.put(FACTORY_KEY, TestDataFactoryProviderMnist.class.getCanonicalName());
        // Define: network config (hyperparameter space)
        ComputationGraphSpace mls = new ComputationGraphSpace.Builder().optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).updater(new org.deeplearning4j.arbiter.conf.updater.SgdSpace(new ContinuousParameterSpace(1.0E-4, 0.1))).l2(new ContinuousParameterSpace(1.0E-4, 0.01)).addInputs("in").setInputTypes(InputType.feedForward(4)).addLayer("layer0", new DenseLayerSpace.Builder().nIn(784).nOut(new IntegerParameterSpace(2, 10)).activation(new org.deeplearning4j.arbiter.optimize.parameter.discrete.DiscreteParameterSpace(Activation.RELU, Activation.TANH)).build(), "in").addLayer("out", new OutputLayerSpace.Builder().nOut(10).activation(SOFTMAX).lossFunction(MCXENT).build(), "layer0").setOutputs("out").numEpochs(3).build();
        // Define configuration:
        CandidateGenerator candidateGenerator = new org.deeplearning4j.arbiter.optimize.generator.RandomSearchGenerator(mls, commands);
        DataProvider dataProvider = new DataSetIteratorFactoryProvider();
        String modelSavePath = new File(System.getProperty("java.io.tmpdir"), "ArbiterDL4JTest\\").getAbsolutePath();
        File f = new File(modelSavePath);
        if (f.exists())
            f.delete();

        f.mkdir();
        f.deleteOnExit();
        if (!(f.exists()))
            throw new RuntimeException();

        OptimizationConfiguration configuration = new OptimizationConfiguration.Builder().candidateGenerator(candidateGenerator).dataProvider(dataProvider).modelSaver(new FileModelSaver(modelSavePath)).scoreFunction(ScoreFunctions.testSetLoss(true)).terminationConditions(new MaxTimeCondition(20, TimeUnit.SECONDS), new MaxCandidatesCondition(10)).build();
        IOptimizationRunner runner = new org.deeplearning4j.arbiter.optimize.runner.LocalOptimizationRunner(configuration, new ComputationGraphTaskCreator(new ClassificationEvaluator()));
        runner.execute();
        Assert.assertEquals(0, runner.numCandidatesFailed());
        Assert.assertTrue(((runner.numCandidatesCompleted()) > 0));
    }

    @Test
    public void testLocalExecutionMDS() throws Exception {
        // Define: network config (hyperparameter space)
        ComputationGraphSpace mls = new ComputationGraphSpace.Builder().optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).updater(new org.deeplearning4j.arbiter.conf.updater.SgdSpace(new ContinuousParameterSpace(1.0E-4, 0.1))).l2(new ContinuousParameterSpace(1.0E-4, 0.01)).addInputs("in").setInputTypes(InputType.feedForward(4)).addLayer("layer0", new DenseLayerSpace.Builder().nIn(784).nOut(new IntegerParameterSpace(2, 10)).activation(new org.deeplearning4j.arbiter.optimize.parameter.discrete.DiscreteParameterSpace(Activation.RELU, Activation.TANH)).build(), "in").addLayer("out", new OutputLayerSpace.Builder().nOut(10).activation(SOFTMAX).lossFunction(MCXENT).build(), "layer0").setOutputs("out").numEpochs(3).build();
        // Define configuration:
        CandidateGenerator candidateGenerator = new org.deeplearning4j.arbiter.optimize.generator.RandomSearchGenerator(mls, null);
        String modelSavePath = new File(System.getProperty("java.io.tmpdir"), "ArbiterDL4JTest\\").getAbsolutePath();
        File f = new File(modelSavePath);
        if (f.exists())
            f.delete();

        f.mkdir();
        f.deleteOnExit();
        if (!(f.exists()))
            throw new RuntimeException();

        OptimizationConfiguration configuration = new OptimizationConfiguration.Builder().candidateGenerator(candidateGenerator).dataProvider(new TestGraphLocalExecution.TestMdsDataProvider(1, 32)).modelSaver(new FileModelSaver(modelSavePath)).scoreFunction(ScoreFunctions.testSetLoss(true)).terminationConditions(new MaxTimeCondition(20, TimeUnit.SECONDS), new MaxCandidatesCondition(10)).scoreFunction(ScoreFunctions.testSetAccuracy()).build();
        IOptimizationRunner runner = new org.deeplearning4j.arbiter.optimize.runner.LocalOptimizationRunner(configuration, new ComputationGraphTaskCreator());
        runner.execute();
        Assert.assertEquals(0, runner.numCandidatesFailed());
        Assert.assertTrue(((runner.numCandidatesCompleted()) > 0));
    }

    public static class TestMdsDataProvider implements DataProvider {
        private int numEpochs;

        private int batchSize;

        public TestMdsDataProvider(@JsonProperty("numEpochs")
        int numEpochs, @JsonProperty("batchSize")
        int batchSize) {
            this.numEpochs = numEpochs;
            this.batchSize = batchSize;
        }

        private TestMdsDataProvider() {
        }

        @Override
        public Object trainData(Map<String, Object> dataParameters) {
            try {
                DataSetIterator underlying = new MnistDataSetIterator(batchSize, Math.min(60000, (10 * (batchSize))), false, true, true, 12345);
                return new MultiDataSetIteratorAdapter(new org.deeplearning4j.datasets.iterator.MultipleEpochsIterator(numEpochs, underlying));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Object testData(Map<String, Object> dataParameters) {
            try {
                DataSetIterator underlying = new MnistDataSetIterator(batchSize, Math.min(10000, (5 * (batchSize))), false, false, false, 12345);
                return new MultiDataSetIteratorAdapter(underlying);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Class<?> getDataType() {
            return MultiDataSetIterator.class;
        }
    }

    @Test
    public void testLocalExecutionEarlyStopping() throws Exception {
        EarlyStoppingConfiguration<ComputationGraph> esConf = new EarlyStoppingConfiguration.Builder<ComputationGraph>().epochTerminationConditions(new MaxEpochsTerminationCondition(15)).scoreCalculator(new TestGraphLocalExecution.ScoreProvider()).modelSaver(new InMemoryModelSaver()).build();
        Map<String, Object> commands = new HashMap<>();
        commands.put(FACTORY_KEY, TestDataFactoryProviderMnist.class.getCanonicalName());
        // Define: network config (hyperparameter space)
        ComputationGraphSpace cgs = // 1-2 identical layers (except nIn)
        new ComputationGraphSpace.Builder().optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).updater(new org.deeplearning4j.arbiter.conf.updater.AdamSpace(new ContinuousParameterSpace(1.0E-4, 0.1))).l2(new ContinuousParameterSpace(1.0E-4, 0.01)).addInputs("in").setInputTypes(InputType.feedForward(784)).addLayer("first", new DenseLayerSpace.Builder().nIn(784).nOut(new IntegerParameterSpace(2, 10)).activation(new org.deeplearning4j.arbiter.optimize.parameter.discrete.DiscreteParameterSpace(Activation.RELU, Activation.TANH)).build(), "in").addLayer("out", new OutputLayerSpace.Builder().nOut(10).activation(SOFTMAX).lossFunction(MCXENT).build(), "first").setOutputs("out").earlyStoppingConfiguration(esConf).build();
        // Define configuration:
        CandidateGenerator candidateGenerator = new org.deeplearning4j.arbiter.optimize.generator.RandomSearchGenerator(cgs, commands);
        DataProvider dataProvider = new DataSetIteratorFactoryProvider();
        String modelSavePath = new File(System.getProperty("java.io.tmpdir"), "ArbiterDL4JTest2CG\\").getAbsolutePath();
        File f = new File(modelSavePath);
        if (f.exists())
            f.delete();

        f.mkdir();
        f.deleteOnExit();
        if (!(f.exists()))
            throw new RuntimeException();

        OptimizationConfiguration configuration = new OptimizationConfiguration.Builder().candidateGenerator(candidateGenerator).dataProvider(dataProvider).scoreFunction(ScoreFunctions.testSetF1()).modelSaver(new FileModelSaver(modelSavePath)).terminationConditions(new MaxTimeCondition(30, TimeUnit.SECONDS), new MaxCandidatesCondition(10)).build();
        IOptimizationRunner runner = new org.deeplearning4j.arbiter.optimize.runner.LocalOptimizationRunner(configuration, new ComputationGraphTaskCreator());
        runner.execute();
        Assert.assertEquals(0, runner.numCandidatesFailed());
        Assert.assertTrue(((runner.numCandidatesCompleted()) > 0));
    }

    private static class ScoreProvider implements Serializable , Supplier<ScoreCalculator> {
        @Override
        public ScoreCalculator get() {
            try {
                return new org.deeplearning4j.earlystopping.scorecalc.DataSetLossCalculatorCG(new MnistDataSetIterator(128, 1280), true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}

