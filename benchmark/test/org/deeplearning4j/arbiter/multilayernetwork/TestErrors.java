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


import Activation.SOFTMAX;
import Activation.TANH;
import LossFunctions.LossFunction.MCXENT;
import java.io.File;
import org.deeplearning4j.arbiter.ComputationGraphSpace;
import org.deeplearning4j.arbiter.MultiLayerSpace;
import org.deeplearning4j.arbiter.layers.DenseLayerSpace;
import org.deeplearning4j.arbiter.layers.OutputLayerSpace;
import org.deeplearning4j.arbiter.optimize.api.CandidateGenerator;
import org.deeplearning4j.arbiter.optimize.api.termination.MaxCandidatesCondition;
import org.deeplearning4j.arbiter.optimize.config.OptimizationConfiguration;
import org.deeplearning4j.arbiter.optimize.runner.IOptimizationRunner;
import org.deeplearning4j.arbiter.saver.local.FileModelSaver;
import org.deeplearning4j.arbiter.scoring.impl.TestSetLossScoreFunction;
import org.deeplearning4j.arbiter.task.MultiLayerNetworkTaskCreator;
import org.deeplearning4j.arbiter.util.TestDataProviderMnist;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class TestErrors {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test(timeout = 20000L)
    public void testAllInvalidConfig() throws Exception {
        // Invalid config - basically check that this actually terminates
        File f = temp.newFolder();
        MultiLayerSpace mls = new MultiLayerSpace.Builder().addLayer(// INVALID: nOut of 0
        new DenseLayerSpace.Builder().nIn(4).nOut(new org.deeplearning4j.arbiter.optimize.parameter.FixedValue(0)).activation(TANH).build()).addLayer(new OutputLayerSpace.Builder().nOut(3).activation(SOFTMAX).lossFunction(MCXENT).build()).build();
        CandidateGenerator candidateGenerator = new org.deeplearning4j.arbiter.optimize.generator.RandomSearchGenerator(mls);
        OptimizationConfiguration configuration = new OptimizationConfiguration.Builder().candidateGenerator(candidateGenerator).dataProvider(new TestDataProviderMnist(32, 10)).modelSaver(new FileModelSaver(f)).scoreFunction(new TestSetLossScoreFunction(true)).terminationConditions(new MaxCandidatesCondition(5)).build();
        IOptimizationRunner runner = new org.deeplearning4j.arbiter.optimize.runner.LocalOptimizationRunner(configuration);
        runner.execute();
    }

    @Test(timeout = 20000L)
    public void testAllInvalidDataConfigMismatch() throws Exception {
        // Valid config - but mismatched with provided data
        File f = temp.newFolder();
        MultiLayerSpace mls = new MultiLayerSpace.Builder().addLayer(// INVALID: nOut of 0
        new DenseLayerSpace.Builder().nIn(4).nOut(10).activation(TANH).build()).addLayer(new OutputLayerSpace.Builder().nIn(10).nOut(3).activation(SOFTMAX).lossFunction(MCXENT).build()).build();
        CandidateGenerator candidateGenerator = new org.deeplearning4j.arbiter.optimize.generator.RandomSearchGenerator(mls);
        OptimizationConfiguration configuration = new OptimizationConfiguration.Builder().candidateGenerator(candidateGenerator).dataProvider(new TestDataProviderMnist(32, 10)).modelSaver(new FileModelSaver(f)).scoreFunction(new TestSetLossScoreFunction(true)).terminationConditions(new MaxCandidatesCondition(5)).build();
        IOptimizationRunner runner = new org.deeplearning4j.arbiter.optimize.runner.LocalOptimizationRunner(configuration);
        runner.execute();
    }

    @Test(timeout = 20000L)
    public void testAllInvalidConfigCG() throws Exception {
        // Invalid config - basically check that this actually terminates
        File f = temp.newFolder();
        ComputationGraphSpace mls = new ComputationGraphSpace.Builder().addInputs("in").layer("0", // INVALID: nOut of 0
        new DenseLayerSpace.Builder().nIn(4).nOut(new org.deeplearning4j.arbiter.optimize.parameter.FixedValue(0)).activation(TANH).build(), "in").layer("1", new OutputLayerSpace.Builder().nOut(3).activation(SOFTMAX).lossFunction(MCXENT).build(), "0").setOutputs("1").build();
        CandidateGenerator candidateGenerator = new org.deeplearning4j.arbiter.optimize.generator.RandomSearchGenerator(mls);
        OptimizationConfiguration configuration = new OptimizationConfiguration.Builder().candidateGenerator(candidateGenerator).dataProvider(new TestDataProviderMnist(32, 10)).modelSaver(new FileModelSaver(f)).scoreFunction(new TestSetLossScoreFunction(true)).terminationConditions(new MaxCandidatesCondition(5)).build();
        IOptimizationRunner runner = new org.deeplearning4j.arbiter.optimize.runner.LocalOptimizationRunner(configuration);
        runner.execute();
    }

    @Test(timeout = 20000L)
    public void testAllInvalidDataConfigMismatchCG() throws Exception {
        // Valid config - but mismatched with provided data
        File f = temp.newFolder();
        ComputationGraphSpace mls = new ComputationGraphSpace.Builder().addInputs("in").layer("0", new DenseLayerSpace.Builder().nIn(4).nOut(10).activation(TANH).build(), "in").addLayer("1", new OutputLayerSpace.Builder().nIn(10).nOut(3).activation(SOFTMAX).lossFunction(MCXENT).build(), "0").setOutputs("1").build();
        CandidateGenerator candidateGenerator = new org.deeplearning4j.arbiter.optimize.generator.RandomSearchGenerator(mls);
        OptimizationConfiguration configuration = new OptimizationConfiguration.Builder().candidateGenerator(candidateGenerator).dataProvider(new TestDataProviderMnist(32, 10)).modelSaver(new FileModelSaver(f)).scoreFunction(new TestSetLossScoreFunction(true)).terminationConditions(new MaxCandidatesCondition(5)).build();
        IOptimizationRunner runner = new org.deeplearning4j.arbiter.optimize.runner.LocalOptimizationRunner(configuration, new MultiLayerNetworkTaskCreator());
        runner.execute();
    }
}

