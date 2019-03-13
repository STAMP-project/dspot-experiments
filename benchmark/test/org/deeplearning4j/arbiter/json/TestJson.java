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
package org.deeplearning4j.arbiter.json;


import Activation.SOFTMAX;
import DataSetIteratorFactoryProvider.FACTORY_KEY;
import LossFunctions.LossFunction.MCXENT;
import OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT;
import RegressionValue.MAE;
import RegressionValue.RMSE;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.deeplearning4j.arbiter.ComputationGraphSpace;
import org.deeplearning4j.arbiter.MultiLayerSpace;
import org.deeplearning4j.arbiter.layers.DenseLayerSpace;
import org.deeplearning4j.arbiter.layers.OutputLayerSpace;
import org.deeplearning4j.arbiter.optimize.api.CandidateGenerator;
import org.deeplearning4j.arbiter.optimize.api.ParameterSpace;
import org.deeplearning4j.arbiter.optimize.api.data.DataProvider;
import org.deeplearning4j.arbiter.optimize.api.data.DataSetIteratorFactoryProvider;
import org.deeplearning4j.arbiter.optimize.api.data.DataSource;
import org.deeplearning4j.arbiter.optimize.api.score.ScoreFunction;
import org.deeplearning4j.arbiter.optimize.api.termination.MaxCandidatesCondition;
import org.deeplearning4j.arbiter.optimize.api.termination.MaxTimeCondition;
import org.deeplearning4j.arbiter.optimize.config.OptimizationConfiguration;
import org.deeplearning4j.arbiter.optimize.parameter.continuous.ContinuousParameterSpace;
import org.deeplearning4j.arbiter.optimize.parameter.integer.IntegerParameterSpace;
import org.deeplearning4j.arbiter.optimize.serde.jackson.JsonMapper;
import org.deeplearning4j.arbiter.scoring.ScoreFunctions;
import org.deeplearning4j.arbiter.scoring.impl.TestSetLossScoreFunction;
import org.deeplearning4j.arbiter.util.TestDataFactoryProviderMnist;
import org.deeplearning4j.datasets.iterator.impl.IrisDataSetIterator;
import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.deeplearning4j.earlystopping.EarlyStoppingConfiguration;
import org.deeplearning4j.earlystopping.termination.MaxEpochsTerminationCondition;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;


/**
 * Created by Alex on 14/02/2017.
 */
public class TestJson {
    @Test
    public void testMultiLayerSpaceJson() {
        MultiLayerSpace mls = // 0 to 1 layers
        // 1-2 identical layers
        new MultiLayerSpace.Builder().optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).updater(new org.deeplearning4j.arbiter.conf.updater.SgdSpace(new ContinuousParameterSpace(1.0E-4, 0.2))).l2(new ContinuousParameterSpace(1.0E-4, 0.05)).addLayer(new DenseLayerSpace.Builder().nIn(1).nOut(new IntegerParameterSpace(5, 30)).activation(new org.deeplearning4j.arbiter.optimize.parameter.discrete.DiscreteParameterSpace(Activation.RELU, Activation.SOFTPLUS, Activation.LEAKYRELU)).build(), new IntegerParameterSpace(1, 2), true).addLayer(new DenseLayerSpace.Builder().nIn(4).nOut(new IntegerParameterSpace(2, 10)).activation(new org.deeplearning4j.arbiter.optimize.parameter.discrete.DiscreteParameterSpace(Activation.RELU, Activation.TANH)).build(), new IntegerParameterSpace(0, 1), true).addLayer(new OutputLayerSpace.Builder().nOut(10).activation(SOFTMAX).iLossFunction(MCXENT.getILossFunction()).build()).setInputType(InputType.convolutional(28, 28, 1)).build();
        String asJson = mls.toJson();
        // System.out.println(asJson);
        MultiLayerSpace fromJson = MultiLayerSpace.fromJson(asJson);
        Assert.assertEquals(mls, fromJson);
    }

    @Test
    public void testOptimizationFromJson() {
        EarlyStoppingConfiguration<ComputationGraph> esConf = new EarlyStoppingConfiguration.Builder<ComputationGraph>().epochTerminationConditions(new MaxEpochsTerminationCondition(100)).scoreCalculator(new org.deeplearning4j.earlystopping.scorecalc.DataSetLossCalculatorCG(new IrisDataSetIterator(150, 150), true)).modelSaver(new org.deeplearning4j.earlystopping.saver.InMemoryModelSaver<ComputationGraph>()).build();
        // Define: network config (hyperparameter space)
        ComputationGraphSpace cgs = // 1-2 identical layers (except nIn)
        new ComputationGraphSpace.Builder().optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).updater(new org.deeplearning4j.arbiter.conf.updater.AdaMaxSpace(new ContinuousParameterSpace(1.0E-4, 0.1))).l2(new ContinuousParameterSpace(1.0E-4, 0.01)).addInputs("in").setInputTypes(InputType.feedForward(4)).addLayer("first", new DenseLayerSpace.Builder().nIn(4).nOut(new IntegerParameterSpace(2, 10)).activation(new org.deeplearning4j.arbiter.optimize.parameter.discrete.DiscreteParameterSpace(Activation.RELU, Activation.TANH)).build(), "in").addLayer("out", new OutputLayerSpace.Builder().nOut(3).activation(SOFTMAX).lossFunction(MCXENT).build(), "first").setOutputs("out").earlyStoppingConfiguration(esConf).build();
        // Define configuration:
        Map<String, Object> commands = new HashMap<>();
        commands.put(FACTORY_KEY, TestDataFactoryProviderMnist.class.getCanonicalName());
        CandidateGenerator candidateGenerator = new org.deeplearning4j.arbiter.optimize.generator.RandomSearchGenerator(cgs, commands);
        DataProvider dataProvider = new DataSetIteratorFactoryProvider();
        OptimizationConfiguration configuration = new OptimizationConfiguration.Builder().candidateGenerator(candidateGenerator).dataProvider(dataProvider).scoreFunction(new TestSetLossScoreFunction()).terminationConditions(new MaxTimeCondition(2, TimeUnit.MINUTES), new MaxCandidatesCondition(100)).build();
        String json = configuration.toJson();
        OptimizationConfiguration loadConf = OptimizationConfiguration.fromJson(json);
        Assert.assertEquals(configuration, loadConf);
    }

    @Test
    public void testOptimizationFromJsonDataSource() {
        for (boolean withProperties : new boolean[]{ false, true }) {
            // Define: network config (hyperparameter space)
            ComputationGraphSpace cgs = // 1-2 identical layers (except nIn)
            new ComputationGraphSpace.Builder().optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).updater(new org.deeplearning4j.arbiter.conf.updater.AdaMaxSpace(new ContinuousParameterSpace(1.0E-4, 0.1))).l2(new ContinuousParameterSpace(1.0E-4, 0.01)).addInputs("in").setInputTypes(InputType.feedForward(4)).addLayer("first", new DenseLayerSpace.Builder().nIn(4).nOut(new IntegerParameterSpace(2, 10)).activation(new org.deeplearning4j.arbiter.optimize.parameter.discrete.DiscreteParameterSpace(Activation.RELU, Activation.TANH)).build(), "in").addLayer("out", new OutputLayerSpace.Builder().nOut(3).activation(SOFTMAX).lossFunction(MCXENT).build(), "first").setOutputs("out").build();
            // Define configuration:
            Map<String, Object> commands = new HashMap<>();
            commands.put(FACTORY_KEY, TestDataFactoryProviderMnist.class.getCanonicalName());
            CandidateGenerator candidateGenerator = new org.deeplearning4j.arbiter.optimize.generator.RandomSearchGenerator(cgs, commands);
            Properties p = new Properties();
            p.setProperty("minibatch", "16");
            OptimizationConfiguration configuration = new OptimizationConfiguration.Builder().candidateGenerator(candidateGenerator).dataSource(TestJson.MnistDataSource.class, (withProperties ? p : null)).scoreFunction(new TestSetLossScoreFunction()).terminationConditions(new MaxTimeCondition(2, TimeUnit.MINUTES), new MaxCandidatesCondition(100)).build();
            String json = configuration.toJson();
            OptimizationConfiguration loadConf = OptimizationConfiguration.fromJson(json);
            Assert.assertEquals(configuration, loadConf);
            Assert.assertNotNull(loadConf.getDataSource());
            if (withProperties) {
                Assert.assertNotNull(loadConf.getDataSourceProperties());
            }
        }
    }

    @Test
    public void testComputationGraphSpaceJson() {
        ParameterSpace<Integer> p = new IntegerParameterSpace(10, 100);
        ComputationGraphSpace cgs = new ComputationGraphSpace.Builder().updater(new org.deeplearning4j.arbiter.conf.updater.AdamSpace(new org.deeplearning4j.arbiter.optimize.parameter.discrete.DiscreteParameterSpace(0.1, 0.5, 1.0))).seed(12345).addInputs("in").addLayer("0", new DenseLayerSpace.Builder().nIn(new IntegerParameterSpace(1, 100)).nOut(p).build(), "in").addLayer("1", new DenseLayerSpace.Builder().nIn(p).nOut(10).build(), "0").addLayer("2", new OutputLayerSpace.Builder().iLossFunction(MCXENT.getILossFunction()).nIn(10).nOut(5).build(), "1").setOutputs("2").build();
        String asJson = cgs.toJson();
        ComputationGraphSpace fromJson = ComputationGraphSpace.fromJson(asJson);
        Assert.assertEquals(cgs, fromJson);
    }

    @Test
    public void testScoreFunctionJson() throws Exception {
        ScoreFunction[] scoreFunctions = new ScoreFunction[]{ ScoreFunctions.testSetAccuracy(), ScoreFunctions.testSetF1(), ScoreFunctions.testSetLoss(true), ScoreFunctions.testSetRegression(MAE), ScoreFunctions.testSetRegression(RMSE) };
        for (ScoreFunction sc : scoreFunctions) {
            String json = JsonMapper.getMapper().writeValueAsString(sc);
            ScoreFunction fromJson = JsonMapper.getMapper().readValue(json, ScoreFunction.class);
            Assert.assertEquals(sc, fromJson);
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
                return new MnistDataSetIterator(minibatch, true, 12345);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Object testData() {
            try {
                return new MnistDataSetIterator(minibatch, true, 12345);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Class<?> getDataType() {
            return DataSetIterator.class;
        }
    }
}

