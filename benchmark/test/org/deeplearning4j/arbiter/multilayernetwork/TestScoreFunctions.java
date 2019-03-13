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


import LossFunctions.LossFunction;
import ROCScoreFunction.Metric;
import ROCScoreFunction.ROCType;
import WeightInit.XAVIER;
import WorkspaceMode.NONE;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.arbiter.MultiLayerSpace;
import org.deeplearning4j.arbiter.layers.OutputLayerSpace;
import org.deeplearning4j.arbiter.optimize.api.CandidateGenerator;
import org.deeplearning4j.arbiter.optimize.api.OptimizationResult;
import org.deeplearning4j.arbiter.optimize.api.ParameterSpace;
import org.deeplearning4j.arbiter.optimize.api.data.DataProvider;
import org.deeplearning4j.arbiter.optimize.api.saving.InMemoryResultSaver;
import org.deeplearning4j.arbiter.optimize.api.saving.ResultReference;
import org.deeplearning4j.arbiter.optimize.api.saving.ResultSaver;
import org.deeplearning4j.arbiter.optimize.api.score.ScoreFunction;
import org.deeplearning4j.arbiter.optimize.api.termination.MaxCandidatesCondition;
import org.deeplearning4j.arbiter.optimize.config.OptimizationConfiguration;
import org.deeplearning4j.arbiter.optimize.parameter.continuous.ContinuousParameterSpace;
import org.deeplearning4j.arbiter.optimize.runner.IOptimizationRunner;
import org.deeplearning4j.arbiter.scoring.impl.ROCScoreFunction;
import org.deeplearning4j.arbiter.task.MultiLayerNetworkTaskCreator;
import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.deeplearning4j.eval.ROC;
import org.deeplearning4j.eval.ROCBinary;
import org.deeplearning4j.eval.ROCMultiClass;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;


@Slf4j
public class TestScoreFunctions {
    @Test
    public void testROCScoreFunctions() throws Exception {
        for (boolean auc : new boolean[]{ true, false }) {
            for (ROCScoreFunction.ROCType rocType : ROCType.values()) {
                String msg = ((auc ? "AUC" : "AUPRC") + " - ") + rocType;
                log.info(("Starting: " + msg));
                ParameterSpace<Double> lr = new ContinuousParameterSpace(1.0E-5, 0.001);
                int nOut = (rocType == (ROCType.ROC)) ? 2 : 10;
                LossFunctions.LossFunction lf = (rocType == (ROCType.BINARY)) ? LossFunction.XENT : LossFunction.MCXENT;
                Activation a = (rocType == (ROCType.BINARY)) ? Activation.SIGMOID : Activation.SOFTMAX;
                MultiLayerSpace mls = new MultiLayerSpace.Builder().trainingWorkspaceMode(NONE).inferenceWorkspaceMode(NONE).updater(new org.deeplearning4j.arbiter.conf.updater.AdamSpace(lr)).weightInit(XAVIER).layer(new OutputLayerSpace.Builder().nIn(784).nOut(nOut).activation(a).lossFunction(lf).build()).build();
                CandidateGenerator cg = new org.deeplearning4j.arbiter.optimize.generator.RandomSearchGenerator(mls);
                ResultSaver rs = new InMemoryResultSaver();
                ScoreFunction sf = new ROCScoreFunction(rocType, (auc ? Metric.AUC : Metric.AUPRC));
                OptimizationConfiguration oc = new OptimizationConfiguration.Builder().candidateGenerator(cg).dataProvider(new TestScoreFunctions.DP(rocType)).modelSaver(rs).scoreFunction(sf).terminationConditions(new MaxCandidatesCondition(3)).rngSeed(12345).build();
                IOptimizationRunner runner = new org.deeplearning4j.arbiter.optimize.runner.LocalOptimizationRunner(oc, new MultiLayerNetworkTaskCreator());
                runner.execute();
                List<ResultReference> list = runner.getResults();
                for (ResultReference rr : list) {
                    DataSetIterator testIter = new MnistDataSetIterator(32, 2000, false, false, true, 12345);
                    testIter.setPreProcessor(new TestScoreFunctions.PreProc(rocType));
                    OptimizationResult or = rr.getResult();
                    MultiLayerNetwork net = ((MultiLayerNetwork) (or.getResultReference().getResultModel()));
                    double expScore;
                    switch (rocType) {
                        case ROC :
                            if (auc) {
                                expScore = net.doEvaluation(testIter, new ROC())[0].calculateAUC();
                            } else {
                                expScore = net.doEvaluation(testIter, new ROC())[0].calculateAUCPR();
                            }
                            break;
                        case BINARY :
                            if (auc) {
                                expScore = net.doEvaluation(testIter, new ROCBinary())[0].calculateAverageAuc();
                            } else {
                                expScore = net.doEvaluation(testIter, new ROCBinary())[0].calculateAverageAUCPR();
                            }
                            break;
                        case MULTICLASS :
                            if (auc) {
                                expScore = net.doEvaluation(testIter, new ROCMultiClass())[0].calculateAverageAUC();
                            } else {
                                expScore = net.doEvaluation(testIter, new ROCMultiClass())[0].calculateAverageAUCPR();
                            }
                            break;
                        default :
                            throw new RuntimeException();
                    }
                    DataSetIterator iter = new MnistDataSetIterator(32, 8000, false, true, true, 12345);
                    iter.setPreProcessor(new TestScoreFunctions.PreProc(rocType));
                    Assert.assertEquals(msg, expScore, or.getScore(), 1.0E-5);
                }
            }
        }
    }

    @AllArgsConstructor
    public static class DP implements DataProvider {
        protected ROCType rocType;

        @Override
        public Object trainData(Map<String, Object> dataParameters) {
            try {
                DataSetIterator iter = new MnistDataSetIterator(32, 8000, false, true, true, 12345);
                iter.setPreProcessor(new TestScoreFunctions.PreProc(rocType));
                return iter;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Object testData(Map<String, Object> dataParameters) {
            try {
                DataSetIterator iter = new MnistDataSetIterator(32, 2000, false, false, true, 12345);
                iter.setPreProcessor(new TestScoreFunctions.PreProc(rocType));
                return iter;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Class<?> getDataType() {
            return DataSetIterator.class;
        }
    }

    @AllArgsConstructor
    public static class PreProc implements DataSetPreProcessor {
        protected ROCType rocType;

        @Override
        public void preProcess(DataSet toPreProcess) {
            switch (rocType) {
                case ROC :
                    // Convert to binary
                    long mb = toPreProcess.getLabels().size(0);
                    INDArray argMax = Nd4j.argMax(toPreProcess.getLabels(), 1);
                    INDArray newLabel = Nd4j.create(mb, 2);
                    for (int i = 0; i < mb; i++) {
                        int idx = ((int) (argMax.getDouble(i, 0)));
                        newLabel.putScalar(i, (idx < 5 ? 0 : 1), 1.0);
                    }
                    toPreProcess.setLabels(newLabel);
                    break;
                case BINARY :
                case MULTICLASS :
                    // Return as is
                    break;
                default :
                    throw new RuntimeException();
            }
        }
    }
}

