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
package org.nd4j.evaluation;


import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.evaluation.curves.Histogram;
import org.nd4j.evaluation.curves.PrecisionRecallCurve;
import org.nd4j.evaluation.curves.RocCurve;
import org.nd4j.evaluation.regression.RegressionEvaluation;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;


public class EvalJsonTest extends BaseNd4jTest {
    public EvalJsonTest(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testSerdeEmpty() {
        boolean print = false;
        IEvaluation[] arr = new IEvaluation[]{ new Evaluation(), new EvaluationBinary(), new ROCBinary(10), new ROCMultiClass(10), new RegressionEvaluation(3), new RegressionEvaluation(), new EvaluationCalibration() };
        for (IEvaluation e : arr) {
            String json = e.toJson();
            String stats = e.stats();
            if (print) {
                System.out.println(((((e.getClass()) + "\n") + json) + "\n\n"));
            }
            IEvaluation fromJson = BaseEvaluation.fromJson(json, BaseEvaluation.class);
            Assert.assertEquals(e.toJson(), fromJson.toJson());
        }
    }

    @Test
    public void testSerde() {
        boolean print = true;
        Nd4j.getRandom().setSeed(12345);
        Evaluation evaluation = new Evaluation();
        EvaluationBinary evaluationBinary = new EvaluationBinary();
        ROC roc = new ROC(2);
        ROCBinary roc2 = new ROCBinary(2);
        ROCMultiClass roc3 = new ROCMultiClass(2);
        RegressionEvaluation regressionEvaluation = new RegressionEvaluation();
        EvaluationCalibration ec = new EvaluationCalibration();
        IEvaluation[] arr = new IEvaluation[]{ evaluation, evaluationBinary, roc, roc2, roc3, regressionEvaluation, ec };
        INDArray evalLabel = Nd4j.create(10, 3);
        for (int i = 0; i < 10; i++) {
            evalLabel.putScalar(i, (i % 3), 1.0);
        }
        INDArray evalProb = Nd4j.rand(10, 3);
        evalProb.diviColumnVector(evalProb.sum(1));
        evaluation.eval(evalLabel, evalProb);
        roc3.eval(evalLabel, evalProb);
        ec.eval(evalLabel, evalProb);
        evalLabel = Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.random.impl.BernoulliDistribution(Nd4j.createUninitialized(10, 3), 0.5));
        evalProb = Nd4j.rand(10, 3);
        evaluationBinary.eval(evalLabel, evalProb);
        roc2.eval(evalLabel, evalProb);
        evalLabel = Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.random.impl.BernoulliDistribution(Nd4j.createUninitialized(10, 1), 0.5));
        evalProb = Nd4j.rand(10, 1);
        roc.eval(evalLabel, evalProb);
        regressionEvaluation.eval(Nd4j.rand(10, 3), Nd4j.rand(10, 3));
        for (IEvaluation e : arr) {
            String json = e.toJson();
            if (print) {
                System.out.println(((((e.getClass()) + "\n") + json) + "\n\n"));
            }
            IEvaluation fromJson = BaseEvaluation.fromJson(json, BaseEvaluation.class);
            Assert.assertEquals(e.toJson(), fromJson.toJson());
        }
    }

    @Test
    public void testSerdeExactRoc() {
        Nd4j.getRandom().setSeed(12345);
        boolean print = true;
        ROC roc = new ROC(0);
        ROCBinary roc2 = new ROCBinary(0);
        ROCMultiClass roc3 = new ROCMultiClass(0);
        IEvaluation[] arr = new IEvaluation[]{ roc, roc2, roc3 };
        INDArray evalLabel = Nd4j.create(100, 3);
        for (int i = 0; i < 100; i++) {
            evalLabel.putScalar(i, (i % 3), 1.0);
        }
        INDArray evalProb = Nd4j.rand(100, 3);
        evalProb.diviColumnVector(evalProb.sum(1));
        roc3.eval(evalLabel, evalProb);
        evalLabel = Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.random.impl.BernoulliDistribution(Nd4j.createUninitialized(100, 3), 0.5));
        evalProb = Nd4j.rand(100, 3);
        roc2.eval(evalLabel, evalProb);
        evalLabel = Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.random.impl.BernoulliDistribution(Nd4j.createUninitialized(100, 1), 0.5));
        evalProb = Nd4j.rand(100, 1);
        roc.eval(evalLabel, evalProb);
        for (IEvaluation e : arr) {
            System.out.println(e.getClass());
            String json = e.toJson();
            String stats = e.stats();
            if (print) {
                System.out.println((json + "\n\n"));
            }
            IEvaluation fromJson = BaseEvaluation.fromJson(json, BaseEvaluation.class);
            Assert.assertEquals(e, fromJson);
            if (fromJson instanceof ROC) {
                // Shouldn't have probAndLabel, but should have stored AUC and AUPRC
                TestCase.assertNull(getProbAndLabel());
                Assert.assertTrue(((calculateAUC()) > 0.0));
                Assert.assertTrue(((calculateAUCPR()) > 0.0));
                Assert.assertEquals(getRocCurve(), getRocCurve());
                Assert.assertEquals(getPrecisionRecallCurve(), getPrecisionRecallCurve());
            } else
                if (e instanceof ROCBinary) {
                    org.nd4j.evaluation.classification[] rocs = getUnderlying();
                    org.nd4j.evaluation.classification[] origRocs = getUnderlying();
                    // for(ROC r : rocs ){
                    for (int i = 0; i < (origRocs.length); i++) {
                        org.nd4j.evaluation.classification.ROC r = rocs[i];
                        org.nd4j.evaluation.classification.ROC origR = origRocs[i];
                        // Shouldn't have probAndLabel, but should have stored AUC and AUPRC, AND stored curves
                        TestCase.assertNull(r.getProbAndLabel());
                        Assert.assertEquals(origR.calculateAUC(), origR.calculateAUC(), 1.0E-6);
                        Assert.assertEquals(origR.calculateAUCPR(), origR.calculateAUCPR(), 1.0E-6);
                        Assert.assertEquals(origR.getRocCurve(), origR.getRocCurve());
                        Assert.assertEquals(origR.getPrecisionRecallCurve(), origR.getPrecisionRecallCurve());
                    }
                } else
                    if (e instanceof ROCMultiClass) {
                        org.nd4j.evaluation.classification[] rocs = getUnderlying();
                        org.nd4j.evaluation.classification[] origRocs = getUnderlying();
                        for (int i = 0; i < (origRocs.length); i++) {
                            org.nd4j.evaluation.classification.ROC r = rocs[i];
                            org.nd4j.evaluation.classification.ROC origR = origRocs[i];
                            // Shouldn't have probAndLabel, but should have stored AUC and AUPRC, AND stored curves
                            TestCase.assertNull(r.getProbAndLabel());
                            Assert.assertEquals(origR.calculateAUC(), origR.calculateAUC(), 1.0E-6);
                            Assert.assertEquals(origR.calculateAUCPR(), origR.calculateAUCPR(), 1.0E-6);
                            Assert.assertEquals(origR.getRocCurve(), origR.getRocCurve());
                            Assert.assertEquals(origR.getPrecisionRecallCurve(), origR.getPrecisionRecallCurve());
                        }
                    }


        }
    }

    @Test
    public void testJsonYamlCurves() {
        ROC roc = new ROC(0);
        INDArray evalLabel = Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.random.impl.BernoulliDistribution(Nd4j.createUninitialized(100, 1), 0.5));
        INDArray evalProb = Nd4j.rand(100, 1);
        roc.eval(evalLabel, evalProb);
        RocCurve c = roc.getRocCurve();
        PrecisionRecallCurve prc = roc.getPrecisionRecallCurve();
        String json1 = c.toJson();
        String json2 = prc.toJson();
        RocCurve c2 = RocCurve.fromJson(json1);
        PrecisionRecallCurve prc2 = PrecisionRecallCurve.fromJson(json2);
        Assert.assertEquals(c, c2);
        Assert.assertEquals(prc, prc2);
        // System.out.println(json1);
        // Also test: histograms
        EvaluationCalibration ec = new EvaluationCalibration();
        evalLabel = Nd4j.create(10, 3);
        for (int i = 0; i < 10; i++) {
            evalLabel.putScalar(i, (i % 3), 1.0);
        }
        evalProb = Nd4j.rand(10, 3);
        evalProb.diviColumnVector(evalProb.sum(1));
        ec.eval(evalLabel, evalProb);
        Histogram[] histograms = new Histogram[]{ ec.getResidualPlotAllClasses(), ec.getResidualPlot(0), ec.getResidualPlot(1), ec.getProbabilityHistogramAllClasses(), ec.getProbabilityHistogram(0), ec.getProbabilityHistogram(1) };
        for (Histogram h : histograms) {
            String json = h.toJson();
            String yaml = h.toYaml();
            Histogram h2 = Histogram.fromJson(json);
            Histogram h3 = Histogram.fromYaml(yaml);
            Assert.assertEquals(h, h2);
            Assert.assertEquals(h2, h3);
        }
    }

    @Test
    public void testJsonWithCustomThreshold() {
        // Evaluation - binary threshold
        Evaluation e = new Evaluation(0.25);
        String json = e.toJson();
        String yaml = e.toYaml();
        Evaluation eFromJson = Evaluation.fromJson(json);
        Evaluation eFromYaml = Evaluation.fromYaml(yaml);
        Assert.assertEquals(0.25, eFromJson.getBinaryDecisionThreshold(), 1.0E-6);
        Assert.assertEquals(0.25, eFromYaml.getBinaryDecisionThreshold(), 1.0E-6);
        // Evaluation: custom cost array
        INDArray costArray = Nd4j.create(new double[]{ 1.0, 2.0, 3.0 });
        Evaluation e2 = new Evaluation(costArray);
        json = e2.toJson();
        yaml = e2.toYaml();
        eFromJson = Evaluation.fromJson(json);
        eFromYaml = Evaluation.fromYaml(yaml);
        Assert.assertEquals(costArray, eFromJson.getCostArray());
        Assert.assertEquals(costArray, eFromYaml.getCostArray());
        // EvaluationBinary - per-output binary threshold
        INDArray threshold = Nd4j.create(new double[]{ 1.0, 0.5, 0.25 });
        EvaluationBinary eb = new EvaluationBinary(threshold);
        json = eb.toJson();
        yaml = eb.toYaml();
        EvaluationBinary ebFromJson = EvaluationBinary.fromJson(json);
        EvaluationBinary ebFromYaml = EvaluationBinary.fromYaml(yaml);
        Assert.assertEquals(threshold, ebFromJson.getDecisionThreshold());
        Assert.assertEquals(threshold, ebFromYaml.getDecisionThreshold());
    }
}

