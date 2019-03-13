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
package org.deeplearning4j.samediff;


import Activation.SOFTMAX;
import Activation.TANH;
import DataType.DOUBLE;
import LossFunctions.LossFunction.MSE;
import WeightInit.XAVIER;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.datasets.iterator.impl.IrisDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.workspace.LayerWorkspaceMgr;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.autodiff.samediff.SDVariable;
import org.nd4j.autodiff.samediff.SameDiff;
import org.nd4j.autodiff.samediff.TrainingConfig;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.evaluation.regression.RegressionEvaluation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.regularization.L1Regularization;
import org.nd4j.linalg.learning.regularization.L2Regularization;
import org.nd4j.linalg.learning.regularization.Regularization;
import org.nd4j.linalg.learning.regularization.WeightDecay;
import org.nd4j.weightinit.impl.XavierInitScheme;


@Slf4j
public class CompareTrainingImplementations extends BaseDL4JTest {
    @Test
    public void testCompareMlpTrainingIris() {
        DataSetIterator iter = new IrisDataSetIterator(150, 150);
        NormalizerStandardize std = new NormalizerStandardize();
        std.fit(iter);
        iter.setPreProcessor(std);
        DataSet ds = iter.next();
        INDArray f = ds.getFeatures();
        INDArray l = ds.getLabels();
        double[] l1 = new double[]{ 0.0, 0.0, 0.01, 0.01, 0.0 };
        double[] l2 = new double[]{ 0.0, 0.02, 0.0, 0.02, 0.0 };
        double[] wd = new double[]{ 0.0, 0.0, 0.0, 0.0, 0.03 };
        // double[] l1 = new double[]{0.0};
        // double[] l2 = new double[]{0.0};
        // double[] wd = new double[]{0.03};
        for (String u : new String[]{ "sgd", "adam", "nesterov", "adamax", "amsgrad" }) {
            for (int i = 0; i < (l1.length); i++) {
                Nd4j.getRandom().setSeed(12345);
                double l1Val = l1[i];
                double l2Val = l2[i];
                double wdVal = wd[i];
                String testName = (((((u + ", l1=") + l1Val) + ", l2=") + l2Val) + ", wd=") + wdVal;
                log.info("Starting: {}", testName);
                SameDiff sd = SameDiff.create();
                SDVariable in = sd.placeHolder("input", DOUBLE, (-1), 4);
                SDVariable label = sd.placeHolder("label", DOUBLE, (-1), 3);
                SDVariable w0 = sd.var("w0", new XavierInitScheme('c', 4, 10), DOUBLE, 4, 10);
                SDVariable b0 = sd.zero("b0", 1, 10);
                SDVariable w1 = sd.var("w1", new XavierInitScheme('c', 10, 3), DOUBLE, 10, 3);
                SDVariable b1 = sd.zero("b1", 1, 3);
                SDVariable z0 = in.mmul(w0).add(b0);
                SDVariable a0 = sd.nn().tanh(z0);
                SDVariable z1 = a0.mmul(w1).add("prediction", b1);
                SDVariable a1 = sd.nn().softmax("softmax", z1);
                SDVariable diff = sd.f().squaredDifference(a1, label);
                SDVariable lossMse = diff.mean();
                IUpdater updater;
                double lr;
                switch (u) {
                    case "sgd" :
                        lr = 0.3;
                        updater = new Sgd(lr);
                        break;
                    case "adam" :
                        lr = 0.01;
                        updater = new Adam(lr);
                        break;
                    case "nesterov" :
                        lr = 0.1;
                        updater = new Nesterovs(lr);
                        break;
                    case "adamax" :
                        lr = 0.01;
                        updater = new AdaMax(lr);
                        break;
                    case "amsgrad" :
                        lr = 0.01;
                        updater = new AMSGrad(lr);
                        break;
                    default :
                        throw new RuntimeException();
                }
                List<Regularization> r = new ArrayList<>();
                if (l2Val > 0) {
                    r.add(new L2Regularization(l2Val));
                }
                if (l1Val > 0) {
                    r.add(new L1Regularization(l1Val));
                }
                if (wdVal > 0) {
                    r.add(new WeightDecay(wdVal, true));
                }
                TrainingConfig conf = new TrainingConfig.Builder().updater(updater).regularization(r).dataSetFeatureMapping("input").dataSetLabelMapping("label").build();
                sd.setTrainingConfig(conf);
                // Create equivalent DL4J net
                MultiLayerConfiguration mlc = // Exclicitly use SGD(1.0) for comparing PRE-UPDATE GRADIENTS (but with l1/l2/wd component added)
                new NeuralNetConfiguration.Builder().weightInit(XAVIER).seed(12345).l1(l1Val).l2(l2Val).l1Bias(l1Val).l2Bias(l2Val).weightDecay(wdVal, true).weightDecayBias(wdVal, true).updater(new Sgd(1.0)).list().layer(new DenseLayer.Builder().nIn(4).nOut(10).activation(TANH).build()).layer(new OutputLayer.Builder().nIn(10).nOut(3).activation(SOFTMAX).lossFunction(MSE).build()).build();
                MultiLayerNetwork net = new MultiLayerNetwork(mlc);
                net.init();
                Map<String, INDArray> oldParams = net.paramTable();
                // Assign parameters so we have identical models at the start:
                w0.getArr().assign(net.getParam("0_W"));
                b0.getArr().assign(net.getParam("0_b"));
                w1.getArr().assign(net.getParam("1_W"));
                b1.getArr().assign(net.getParam("1_b"));
                // Check output (forward pass)
                Map<String, INDArray> placeholders = new HashMap<>();
                placeholders.put("input", f);
                placeholders.put("label", l);
                sd.exec(placeholders, lossMse.getVarName());
                INDArray outSd = a1.getArr();
                INDArray outDl4j = net.output(f);
                Assert.assertEquals(testName, outDl4j, outSd);
                net.setInput(f);
                net.setLabels(l);
                net.computeGradientAndScore();
                net.getUpdater().update(net, net.gradient(), 0, 0, 150, LayerWorkspaceMgr.noWorkspacesImmutable());// Division by minibatch, apply L1/L2

                // Check score
                double scoreDl4j = net.score();
                double scoreSd = (lossMse.getArr().getDouble(0)) + (sd.calcRegularizationScore());
                Assert.assertEquals(testName, scoreDl4j, scoreSd, 1.0E-6);
                double lossRegScoreSD = sd.calcRegularizationScore();
                double lossRegScoreDL4J = net.calcRegularizationScore(true);
                Assert.assertEquals(lossRegScoreDL4J, lossRegScoreSD, 1.0E-6);
                // Check gradients (before updater applied)
                Map<String, INDArray> grads = net.gradient().gradientForVariable();
                sd.execBackwards(placeholders);
                // Note that the SameDiff gradients don't include the L1/L2 terms at present just from execBackwards()... these are added in fitting only
                // We can check correctness though with training param checks later
                if (((l1Val == 0) && (l2Val == 0)) && (wdVal == 0)) {
                    Assert.assertEquals(testName, grads.get("1_b"), b1.getGradient().getArr());
                    Assert.assertEquals(testName, grads.get("1_W"), w1.getGradient().getArr());
                    Assert.assertEquals(testName, grads.get("0_b"), b0.getGradient().getArr());
                    Assert.assertEquals(testName, grads.get("0_W"), w0.getGradient().getArr());
                }
                // Check training with updater
                mlc = new NeuralNetConfiguration.Builder().weightInit(XAVIER).seed(12345).l1(l1Val).l2(l2Val).l1Bias(l1Val).l2Bias(l2Val).weightDecay(wdVal, true).weightDecayBias(wdVal, true).updater(updater.clone()).list().layer(new DenseLayer.Builder().nIn(4).nOut(10).activation(TANH).build()).layer(new OutputLayer.Builder().nIn(10).nOut(3).activation(SOFTMAX).lossFunction(MSE).build()).build();
                net = new MultiLayerNetwork(mlc);
                net.init();
                net.setParamTable(oldParams);
                for (int j = 0; j < 3; j++) {
                    net.fit(ds);
                    sd.fit(ds);
                    String s = (testName + " - ") + j;
                    INDArray dl4j_0W = net.getParam("0_W");
                    INDArray sd_0W = w0.getArr();
                    Assert.assertEquals(s, dl4j_0W, sd_0W);
                    Assert.assertEquals(s, net.getParam("0_b"), b0.getArr());
                    Assert.assertEquals(s, net.getParam("1_W"), w1.getArr());
                    Assert.assertEquals(s, net.getParam("1_b"), b1.getArr());
                }
                // Compare evaluations
                Evaluation evalDl4j = net.doEvaluation(iter, new Evaluation())[0];
                Evaluation evalSd = new Evaluation();
                sd.evaluate(iter, "softmax", evalSd);
                Assert.assertEquals(evalDl4j, evalSd);
                RegressionEvaluation rEvalDl4j = net.doEvaluation(iter, new RegressionEvaluation())[0];
                RegressionEvaluation rEvalSd = new RegressionEvaluation();
                sd.evaluate(iter, "softmax", rEvalSd);
                Assert.assertEquals(rEvalDl4j, rEvalSd);
                System.out.println("---------------------------------");
            }
        }
    }
}

