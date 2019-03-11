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
package org.deeplearning4j.arbiter.multilayernetwork;


import Activation.HARDSIGMOID;
import Activation.SOFTMAX;
import DataSetIteratorFactoryProvider.FACTORY_KEY;
import LossFunctions.LossFunction.MCXENT;
import OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.arbiter.MultiLayerSpace;
import org.deeplearning4j.arbiter.evaluator.multilayer.ClassificationEvaluator;
import org.deeplearning4j.arbiter.layers.DenseLayerSpace;
import org.deeplearning4j.arbiter.layers.OCNNLayerSpace;
import org.deeplearning4j.arbiter.layers.OutputLayerSpace;
import org.deeplearning4j.arbiter.optimize.api.Candidate;
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
import org.deeplearning4j.arbiter.scoring.impl.TestSetLossScoreFunction;
import org.deeplearning4j.arbiter.util.TestDataFactoryProviderMnist;
import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;


@Slf4j
public class TestDL4JLocalExecution {
    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

    @Test
    public void testLocalExecution() throws Exception {
        for (int dataApproach = 0; dataApproach < 3; dataApproach++) {
            log.info("////////////////// Starting Test: {} ///////////////////", dataApproach);
            // Define: network config (hyperparameter space)
            MultiLayerSpace mls = // 1-2 identical layers (except nIn)
            new MultiLayerSpace.Builder().optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).updater(new org.deeplearning4j.arbiter.conf.updater.SgdSpace(new ContinuousParameterSpace(1.0E-4, 0.1))).l2(new ContinuousParameterSpace(1.0E-4, 0.01)).addLayer(new DenseLayerSpace.Builder().nIn(784).nOut(new IntegerParameterSpace(10, 20)).activation(new org.deeplearning4j.arbiter.optimize.parameter.discrete.DiscreteParameterSpace(Activation.RELU, Activation.TANH)).build()).addLayer(new OutputLayerSpace.Builder().nOut(10).activation(SOFTMAX).lossFunction(MCXENT).build()).numEpochs(3).build();
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
            IOptimizationRunner runner = new org.deeplearning4j.arbiter.optimize.runner.LocalOptimizationRunner(configuration, new org.deeplearning4j.arbiter.task.MultiLayerNetworkTaskCreator(new ClassificationEvaluator()));
            runner.execute();
            List<ResultReference> results = runner.getResults();
            Assert.assertEquals(5, results.size());
            System.out.println((("----- COMPLETE - " + (results.size())) + " results -----"));
        }
    }

    public static class MnistDataSource implements DataSource {
        private int minibatch;

        public MnistDataSource() {
        }

        @Override
        public void configure(Properties properties) {
            this.minibatch = Integer.parseInt(properties.getProperty("minibatch", "16"));
        }

        @Override
        public Object trainData() {
            try {
                return new org.deeplearning4j.datasets.iterator.EarlyTerminationDataSetIterator(new MnistDataSetIterator(minibatch, true, 12345), 3);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Object testData() {
            try {
                return new org.deeplearning4j.datasets.iterator.EarlyTerminationDataSetIterator(new MnistDataSetIterator(minibatch, true, 12345), 3);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Class<?> getDataType() {
            return DataSetIterator.class;
        }
    }

    public static class MnistDataProvider implements DataProvider {
        private int minibatch = 8;

        @Override
        public Object trainData(Map<String, Object> dataParameters) {
            try {
                return new org.deeplearning4j.datasets.iterator.EarlyTerminationDataSetIterator(new MnistDataSetIterator(minibatch, true, 12345), 3);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Object testData(Map<String, Object> dataParameters) {
            try {
                return new org.deeplearning4j.datasets.iterator.EarlyTerminationDataSetIterator(new MnistDataSetIterator(minibatch, true, 12345), 3);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Class<?> getDataType() {
            return DataSetIterator.class;
        }
    }

    @Test
    public void testOcnn() {
        Map<String, Object> commands = new HashMap<>();
        commands.put(FACTORY_KEY, TestDataFactoryProviderMnist.class.getCanonicalName());
        // Define: network config (hyperparameter space)
        MultiLayerSpace mls = // 1-2 identical layers (except nIn)
        new MultiLayerSpace.Builder().optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).updater(new org.deeplearning4j.arbiter.conf.updater.SgdSpace(new ContinuousParameterSpace(1.0E-4, 0.1))).l2(new ContinuousParameterSpace(1.0E-4, 0.01)).addLayer(new DenseLayerSpace.Builder().nOut(new IntegerParameterSpace(250, 500)).activation(new org.deeplearning4j.arbiter.optimize.parameter.discrete.DiscreteParameterSpace(Activation.RELU, Activation.TANH)).build(), new IntegerParameterSpace(1, 2)).addLayer(new OCNNLayerSpace.Builder().nu(new ContinuousParameterSpace(1.0E-4, 0.1)).numHidden(new org.deeplearning4j.arbiter.optimize.parameter.discrete.DiscreteParameterSpace<Integer>((784 / 2), (784 / 4))).activation(HARDSIGMOID).lossFunction(MCXENT).build()).setInputType(InputType.convolutionalFlat(28, 28, 1)).build();
        // Define configuration:
        CandidateGenerator candidateGenerator = new org.deeplearning4j.arbiter.optimize.generator.RandomSearchGenerator(mls, commands);
        DataProvider dataProvider = new DataSetIteratorFactoryProvider();
        String modelSavePath = new File(System.getProperty("java.io.tmpdir"), "ArbiterDL4JTest3\\").getAbsolutePath();
        File f = new File(modelSavePath);
        if (f.exists())
            f.delete();

        f.mkdir();
        f.deleteOnExit();
        if (!(f.exists()))
            throw new RuntimeException();

        OptimizationConfiguration configuration = new OptimizationConfiguration.Builder().candidateGenerator(candidateGenerator).dataProvider(dataProvider).modelSaver(new FileModelSaver(modelSavePath)).scoreFunction(new TestSetLossScoreFunction()).terminationConditions(new MaxTimeCondition(2, TimeUnit.MINUTES), new MaxCandidatesCondition(100)).build();
        // candidate generation: uncomment execute if you want to run
        IOptimizationRunner runner = new org.deeplearning4j.arbiter.optimize.runner.LocalOptimizationRunner(configuration, new org.deeplearning4j.arbiter.task.MultiLayerNetworkTaskCreator(new ClassificationEvaluator()));
        Candidate candidate = candidateGenerator.getCandidate();
        // runner.execute();
        System.out.println("----- COMPLETE -----");
    }
}

