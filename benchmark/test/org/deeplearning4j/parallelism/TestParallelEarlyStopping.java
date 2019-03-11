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
package org.deeplearning4j.parallelism;


import Activation.SOFTMAX;
import EarlyStoppingResult.TerminationReason.EpochTerminationCondition;
import EarlyStoppingResult.TerminationReason.IterationTerminationCondition;
import LossFunctions.LossFunction.MCXENT;
import OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT;
import WeightInit.XAVIER;
import java.util.concurrent.TimeUnit;
import org.deeplearning4j.datasets.iterator.impl.IrisDataSetIterator;
import org.deeplearning4j.earlystopping.EarlyStoppingConfiguration;
import org.deeplearning4j.earlystopping.EarlyStoppingModelSaver;
import org.deeplearning4j.earlystopping.EarlyStoppingResult;
import org.deeplearning4j.earlystopping.termination.MaxEpochsTerminationCondition;
import org.deeplearning4j.earlystopping.termination.MaxScoreIterationTerminationCondition;
import org.deeplearning4j.earlystopping.termination.MaxTimeIterationTerminationCondition;
import org.deeplearning4j.earlystopping.trainer.IEarlyStoppingTrainer;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Sgd;


public class TestParallelEarlyStopping {
    // parallel training results vary wildly with expected result
    // need to determine if this test is feasible, and how it should
    // be properly designed
    // @Test
    // public void testEarlyStoppingIris(){
    // MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
    // .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
    // .updater(Updater.SGD)
    // .weightInit(WeightInit.XAVIER)
    // .list()
    // .layer(0,new OutputLayer.Builder().nIn(4).nOut(3).lossFunction(LossFunctions.LossFunction.MCXENT).build())
    // 
    // .build();
    // MultiLayerNetwork net = new MultiLayerNetwork(conf);
    // net.setListeners(new ScoreIterationListener(1));
    // 
    // DataSetIterator irisIter = new IrisDataSetIterator(50,600);
    // EarlyStoppingModelSaver<MultiLayerNetwork> saver = new InMemoryModelSaver<>();
    // EarlyStoppingConfiguration<MultiLayerNetwork> esConf = new EarlyStoppingConfiguration.Builder<MultiLayerNetwork>()
    // .epochTerminationConditions(new MaxEpochsTerminationCondition(5))
    // .evaluateEveryNEpochs(1)
    // .iterationTerminationConditions(new MaxTimeIterationTerminationCondition(1, TimeUnit.MINUTES))
    // .scoreCalculator(new DataSetLossCalculator(irisIter,true))
    // .modelSaver(saver)
    // .build();
    // 
    // IEarlyStoppingTrainer<MultiLayerNetwork> trainer = new EarlyStoppingParallelTrainer<>(esConf,net,irisIter,null,2,2,1);
    // 
    // EarlyStoppingResult<MultiLayerNetwork> result = trainer.fit();
    // System.out.println(result);
    // 
    // assertEquals(5, result.getTotalEpochs());
    // assertEquals(EarlyStoppingResult.TerminationReason.EpochTerminationCondition,result.getTerminationReason());
    // Map<Integer,Double> scoreVsIter = result.getScoreVsEpoch();
    // assertEquals(5,scoreVsIter.size());
    // String expDetails = esConf.getEpochTerminationConditions().get(0).toString();
    // assertEquals(expDetails, result.getTerminationDetails());
    // 
    // MultiLayerNetwork out = result.getBestModel();
    // assertNotNull(out);
    // 
    // //Check that best score actually matches (returned model vs. manually calculated score)
    // MultiLayerNetwork bestNetwork = result.getBestModel();
    // irisIter.reset();
    // double score = bestNetwork.score(irisIter.next());
    // assertEquals(result.getBestModelScore(), score, 1e-4);
    // }
    @Test
    public void testEarlyStoppingEveryNEpoch() {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).updater(new Sgd()).weightInit(XAVIER).list().layer(0, new OutputLayer.Builder().nIn(4).nOut(3).lossFunction(MCXENT).activation(SOFTMAX).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.setListeners(new ScoreIterationListener(1));
        DataSetIterator irisIter = new IrisDataSetIterator(50, 600);
        EarlyStoppingModelSaver<MultiLayerNetwork> saver = new org.deeplearning4j.earlystopping.saver.InMemoryModelSaver();
        EarlyStoppingConfiguration<MultiLayerNetwork> esConf = new EarlyStoppingConfiguration.Builder<MultiLayerNetwork>().epochTerminationConditions(new MaxEpochsTerminationCondition(5)).scoreCalculator(new org.deeplearning4j.earlystopping.scorecalc.DataSetLossCalculator(irisIter, true)).evaluateEveryNEpochs(2).modelSaver(saver).build();
        IEarlyStoppingTrainer<MultiLayerNetwork> trainer = new EarlyStoppingParallelTrainer(esConf, net, irisIter, null, 2, 6, 1);
        EarlyStoppingResult<MultiLayerNetwork> result = trainer.fit();
        System.out.println(result);
        Assert.assertEquals(5, result.getTotalEpochs());
        Assert.assertEquals(EpochTerminationCondition, result.getTerminationReason());
    }

    @Test
    public void testBadTuning() {
        // Test poor tuning (high LR): should terminate on MaxScoreIterationTerminationCondition
        Nd4j.getRandom().setSeed(12345);
        MultiLayerConfiguration conf = // Intentionally huge LR
        new NeuralNetConfiguration.Builder().seed(12345).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).updater(new Sgd(1.0)).weightInit(XAVIER).list().layer(0, new OutputLayer.Builder().nIn(4).nOut(3).activation(SOFTMAX).lossFunction(MCXENT).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.setListeners(new ScoreIterationListener(1));
        DataSetIterator irisIter = new IrisDataSetIterator(10, 150);
        EarlyStoppingModelSaver<MultiLayerNetwork> saver = new org.deeplearning4j.earlystopping.saver.InMemoryModelSaver();
        EarlyStoppingConfiguration<MultiLayerNetwork> esConf = // Initial score is ~2.5
        new EarlyStoppingConfiguration.Builder<MultiLayerNetwork>().epochTerminationConditions(new MaxEpochsTerminationCondition(5000)).iterationTerminationConditions(new MaxTimeIterationTerminationCondition(1, TimeUnit.MINUTES), new MaxScoreIterationTerminationCondition(10)).scoreCalculator(new org.deeplearning4j.earlystopping.scorecalc.DataSetLossCalculator(irisIter, true)).modelSaver(saver).build();
        IEarlyStoppingTrainer<MultiLayerNetwork> trainer = new EarlyStoppingParallelTrainer(esConf, net, irisIter, null, 2, 2, 1);
        EarlyStoppingResult result = trainer.fit();
        Assert.assertTrue(((result.getTotalEpochs()) < 5));
        Assert.assertEquals(IterationTerminationCondition, result.getTerminationReason());
        String expDetails = new MaxScoreIterationTerminationCondition(10).toString();
        Assert.assertEquals(expDetails, result.getTerminationDetails());
        Assert.assertTrue(((result.getBestModelEpoch()) <= 0));
        Assert.assertNotNull(result.getBestModel());
    }
}

