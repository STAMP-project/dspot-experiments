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
package org.deeplearning4j.optimize.solver;


import Activation.RELU;
import Activation.SIGMOID;
import Activation.SOFTMAX;
import LossFunctions.LossFunction.MCXENT;
import LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD;
import java.util.Collections;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.datasets.iterator.impl.IrisDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.workspace.LayerWorkspaceMgr;
import org.deeplearning4j.optimize.api.TrainingListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.optimize.solvers.BackTrackLineSearch;
import org.deeplearning4j.optimize.stepfunctions.DefaultStepFunction;
import org.deeplearning4j.optimize.stepfunctions.NegativeDefaultStepFunction;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;


/**
 *
 *
 * @author Adam Gibson
 */
public class BackTrackLineSearchTest extends BaseDL4JTest {
    private DataSetIterator irisIter;

    private DataSet irisData;

    @Test
    public void testSingleMinLineSearch() throws Exception {
        OutputLayer layer = BackTrackLineSearchTest.getIrisLogisticLayerConfig(SOFTMAX, 100, NEGATIVELOGLIKELIHOOD);
        int nParams = ((int) (layer.numParams()));
        layer.setBackpropGradientsViewArray(Nd4j.create(1, nParams));
        layer.setInput(irisData.getFeatures(), LayerWorkspaceMgr.noWorkspaces());
        layer.setLabels(irisData.getLabels());
        layer.computeGradientAndScore(LayerWorkspaceMgr.noWorkspaces());
        BackTrackLineSearch lineSearch = new BackTrackLineSearch(layer, layer.getOptimizer());
        double step = lineSearch.optimize(layer.params(), layer.gradient().gradient(), layer.gradient().gradient(), LayerWorkspaceMgr.noWorkspacesImmutable());
        Assert.assertEquals(1.0, step, 0.001);
    }

    @Test
    public void testSingleMaxLineSearch() throws Exception {
        double score1;
        double score2;
        OutputLayer layer = BackTrackLineSearchTest.getIrisLogisticLayerConfig(SOFTMAX, 100, NEGATIVELOGLIKELIHOOD);
        int nParams = ((int) (layer.numParams()));
        layer.setBackpropGradientsViewArray(Nd4j.create(1, nParams));
        layer.setInput(irisData.getFeatures(), LayerWorkspaceMgr.noWorkspaces());
        layer.setLabels(irisData.getLabels());
        layer.computeGradientAndScore(LayerWorkspaceMgr.noWorkspaces());
        score1 = layer.score();
        BackTrackLineSearch lineSearch = new BackTrackLineSearch(layer, new NegativeDefaultStepFunction(), layer.getOptimizer());
        double step = lineSearch.optimize(layer.params(), layer.gradient().gradient(), layer.gradient().gradient(), LayerWorkspaceMgr.noWorkspacesImmutable());
        Assert.assertEquals(1.0, step, 0.001);
    }

    @Test
    public void testMultMinLineSearch() throws Exception {
        double score1;
        double score2;
        OutputLayer layer = BackTrackLineSearchTest.getIrisLogisticLayerConfig(SOFTMAX, 100, NEGATIVELOGLIKELIHOOD);
        int nParams = ((int) (layer.numParams()));
        layer.setBackpropGradientsViewArray(Nd4j.create(1, nParams));
        layer.setInput(irisData.getFeatures(), LayerWorkspaceMgr.noWorkspaces());
        layer.setLabels(irisData.getLabels());
        layer.computeGradientAndScore(LayerWorkspaceMgr.noWorkspaces());
        score1 = layer.score();
        INDArray origGradient = layer.gradient().gradient().dup();
        NegativeDefaultStepFunction sf = new NegativeDefaultStepFunction();
        BackTrackLineSearch lineSearch = new BackTrackLineSearch(layer, sf, layer.getOptimizer());
        double step = lineSearch.optimize(layer.params(), layer.gradient().gradient(), layer.gradient().gradient(), LayerWorkspaceMgr.noWorkspacesImmutable());
        INDArray currParams = layer.params();
        sf.step(currParams, origGradient, step);
        layer.setParams(currParams);
        layer.computeGradientAndScore(LayerWorkspaceMgr.noWorkspaces());
        score2 = layer.score();
        Assert.assertTrue(((("score1=" + score1) + ", score2=") + score2), (score1 > score2));
    }

    @Test
    public void testMultMaxLineSearch() throws Exception {
        double score1;
        double score2;
        irisData.normalizeZeroMeanZeroUnitVariance();
        OutputLayer layer = BackTrackLineSearchTest.getIrisLogisticLayerConfig(SOFTMAX, 100, MCXENT);
        int nParams = ((int) (layer.numParams()));
        layer.setBackpropGradientsViewArray(Nd4j.create(1, nParams));
        layer.setInput(irisData.getFeatures(), LayerWorkspaceMgr.noWorkspaces());
        layer.setLabels(irisData.getLabels());
        layer.computeGradientAndScore(LayerWorkspaceMgr.noWorkspaces());
        score1 = layer.score();
        INDArray origGradient = layer.gradient().gradient().dup();
        DefaultStepFunction sf = new DefaultStepFunction();
        BackTrackLineSearch lineSearch = new BackTrackLineSearch(layer, sf, layer.getOptimizer());
        double step = lineSearch.optimize(layer.params().dup(), layer.gradient().gradient().dup(), layer.gradient().gradient().dup(), LayerWorkspaceMgr.noWorkspacesImmutable());
        INDArray currParams = layer.params();
        sf.step(currParams, origGradient, step);
        layer.setParams(currParams);
        layer.computeGradientAndScore(LayerWorkspaceMgr.noWorkspaces());
        score2 = layer.score();
        Assert.assertTrue(((("score1 = " + score1) + ", score2 = ") + score2), (score1 < score2));
    }

    // /////////////////////////////////////////////////////////////////////////
    @Test
    public void testBackTrackLineGradientDescent() {
        OptimizationAlgorithm optimizer = OptimizationAlgorithm.LINE_GRADIENT_DESCENT;
        DataSetIterator irisIter = new IrisDataSetIterator(1, 1);
        DataSet data = irisIter.next();
        MultiLayerNetwork network = new MultiLayerNetwork(BackTrackLineSearchTest.getIrisMultiLayerConfig(SIGMOID, optimizer));
        network.init();
        TrainingListener listener = new ScoreIterationListener(1);
        network.setListeners(Collections.singletonList(listener));
        double oldScore = network.score(data);
        for (int i = 0; i < 100; i++) {
            network.fit(data.getFeatures(), data.getLabels());
        }
        double score = network.score();
        Assert.assertTrue((score < oldScore));
    }

    @Test
    public void testBackTrackLineCG() {
        OptimizationAlgorithm optimizer = OptimizationAlgorithm.CONJUGATE_GRADIENT;
        DataSet data = irisIter.next();
        data.normalizeZeroMeanZeroUnitVariance();
        MultiLayerNetwork network = new MultiLayerNetwork(BackTrackLineSearchTest.getIrisMultiLayerConfig(RELU, optimizer));
        network.init();
        TrainingListener listener = new ScoreIterationListener(1);
        network.setListeners(Collections.singletonList(listener));
        double firstScore = network.score(data);
        for (int i = 0; i < 5; i++) {
            network.fit(data.getFeatures(), data.getLabels());
        }
        double score = network.score();
        Assert.assertTrue((score < firstScore));
    }

    @Test
    public void testBackTrackLineLBFGS() {
        OptimizationAlgorithm optimizer = OptimizationAlgorithm.LBFGS;
        DataSet data = irisIter.next();
        data.normalizeZeroMeanZeroUnitVariance();
        MultiLayerNetwork network = new MultiLayerNetwork(BackTrackLineSearchTest.getIrisMultiLayerConfig(RELU, optimizer));
        network.init();
        TrainingListener listener = new ScoreIterationListener(1);
        network.setListeners(Collections.singletonList(listener));
        double oldScore = network.score(data);
        for (int i = 0; i < 5; i++) {
            network.fit(data.getFeatures(), data.getLabels());
        }
        double score = network.score();
        Assert.assertTrue((score < oldScore));
    }
}

