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


import EvaluationAveraging.Macro;
import EvaluationAveraging.Micro;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.evaluation.classification.ConfusionMatrix;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.util.FeatureUtil;


/**
 * Created by agibsonccc on 12/22/14.
 */
public class EvalTest extends BaseNd4jTest {
    public EvalTest(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testEval() {
        int classNum = 5;
        Evaluation eval = new Evaluation(classNum);
        // Testing the edge case when some classes do not have true positive
        INDArray trueOutcome = FeatureUtil.toOutcomeVector(0, 5);// [1,0,0,0,0]

        INDArray predictedOutcome = FeatureUtil.toOutcomeVector(0, 5);// [1,0,0,0,0]

        eval.eval(trueOutcome, predictedOutcome);
        Assert.assertEquals(1, eval.classCount(0));
        Assert.assertEquals(1.0, eval.f1(), 0.1);
        // Testing more than one sample. eval() does not reset the Evaluation instance
        INDArray trueOutcome2 = FeatureUtil.toOutcomeVector(1, 5);// [0,1,0,0,0]

        INDArray predictedOutcome2 = FeatureUtil.toOutcomeVector(0, 5);// [1,0,0,0,0]

        eval.eval(trueOutcome2, predictedOutcome2);
        // Verified with sklearn in Python
        // from sklearn.metrics import classification_report
        // classification_report(['a', 'a'], ['a', 'b'], labels=['a', 'b', 'c', 'd', 'e'])
        Assert.assertEquals(eval.f1(), 0.6, 0.1);
        // The first entry is 0 label
        Assert.assertEquals(1, eval.classCount(0));
        // The first entry is 1 label
        Assert.assertEquals(1, eval.classCount(1));
        // Class 0: one positive, one negative -> (one true positive, one false positive); no true/false negatives
        Assert.assertEquals(1, eval.positive().get(0), 0);
        Assert.assertEquals(1, eval.negative().get(0), 0);
        Assert.assertEquals(1, eval.truePositives().get(0), 0);
        Assert.assertEquals(1, eval.falsePositives().get(0), 0);
        Assert.assertEquals(0, eval.trueNegatives().get(0), 0);
        Assert.assertEquals(0, eval.falseNegatives().get(0), 0);
        // The rest are negative
        Assert.assertEquals(1, eval.negative().get(0), 0);
        // 2 rows and only the first is correct
        Assert.assertEquals(0.5, eval.accuracy(), 0);
    }

    @Test
    public void testEval2() {
        // Confusion matrix:
        // actual 0      20      3
        // actual 1      10      5
        Evaluation evaluation = new Evaluation(Arrays.asList("class0", "class1"));
        INDArray predicted0 = Nd4j.create(new double[]{ 1, 0 }, new long[]{ 1, 2 });
        INDArray predicted1 = Nd4j.create(new double[]{ 0, 1 }, new long[]{ 1, 2 });
        INDArray actual0 = Nd4j.create(new double[]{ 1, 0 }, new long[]{ 1, 2 });
        INDArray actual1 = Nd4j.create(new double[]{ 0, 1 }, new long[]{ 1, 2 });
        for (int i = 0; i < 20; i++) {
            evaluation.eval(actual0, predicted0);
        }
        for (int i = 0; i < 3; i++) {
            evaluation.eval(actual0, predicted1);
        }
        for (int i = 0; i < 10; i++) {
            evaluation.eval(actual1, predicted0);
        }
        for (int i = 0; i < 5; i++) {
            evaluation.eval(actual1, predicted1);
        }
        Assert.assertEquals(20, evaluation.truePositives().get(0), 0);
        Assert.assertEquals(3, evaluation.falseNegatives().get(0), 0);
        Assert.assertEquals(10, evaluation.falsePositives().get(0), 0);
        Assert.assertEquals(5, evaluation.trueNegatives().get(0), 0);
        Assert.assertEquals(((20.0 + 5) / (((20 + 3) + 10) + 5)), evaluation.accuracy(), 1.0E-6);
        System.out.println(evaluation.confusionToString());
    }

    @Test
    public void testStringListLabels() {
        INDArray trueOutcome = FeatureUtil.toOutcomeVector(0, 2);
        INDArray predictedOutcome = FeatureUtil.toOutcomeVector(0, 2);
        List<String> labelsList = new ArrayList<>();
        labelsList.add("hobbs");
        labelsList.add("cal");
        Evaluation eval = new Evaluation(labelsList);
        eval.eval(trueOutcome, predictedOutcome);
        Assert.assertEquals(1, eval.classCount(0));
        Assert.assertEquals(labelsList.get(0), eval.getClassLabel(0));
    }

    @Test
    public void testStringHashLabels() {
        INDArray trueOutcome = FeatureUtil.toOutcomeVector(0, 2);
        INDArray predictedOutcome = FeatureUtil.toOutcomeVector(0, 2);
        Map<Integer, String> labelsMap = new HashMap<>();
        labelsMap.put(0, "hobbs");
        labelsMap.put(1, "cal");
        Evaluation eval = new Evaluation(labelsMap);
        eval.eval(trueOutcome, predictedOutcome);
        Assert.assertEquals(1, eval.classCount(0));
        Assert.assertEquals(labelsMap.get(0), eval.getClassLabel(0));
    }

    @Test
    public void testEvalMasking() {
        int miniBatch = 5;
        int nOut = 3;
        int tsLength = 6;
        INDArray labels = Nd4j.zeros(miniBatch, nOut, tsLength);
        INDArray predicted = Nd4j.zeros(miniBatch, nOut, tsLength);
        Nd4j.getRandom().setSeed(12345);
        Random r = new Random(12345);
        for (int i = 0; i < miniBatch; i++) {
            for (int j = 0; j < tsLength; j++) {
                INDArray rand = Nd4j.rand(1, nOut);
                rand.divi(rand.sumNumber());
                predicted.put(new INDArrayIndex[]{ NDArrayIndex.point(i), NDArrayIndex.all(), NDArrayIndex.point(j) }, rand);
                int idx = r.nextInt(nOut);
                labels.putScalar(new int[]{ i, idx, j }, 1.0);
            }
        }
        // Create a longer labels/predicted with mask for first and last time step
        // Expect masked evaluation to be identical to original evaluation
        INDArray labels2 = Nd4j.zeros(miniBatch, nOut, (tsLength + 2));
        labels2.put(new INDArrayIndex[]{ NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(1, (tsLength + 1)) }, labels);
        INDArray predicted2 = Nd4j.zeros(miniBatch, nOut, (tsLength + 2));
        predicted2.put(new INDArrayIndex[]{ NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(1, (tsLength + 1)) }, predicted);
        INDArray labelsMask = Nd4j.ones(miniBatch, (tsLength + 2));
        for (int i = 0; i < miniBatch; i++) {
            labelsMask.putScalar(new int[]{ i, 0 }, 0.0);
            labelsMask.putScalar(new int[]{ i, tsLength + 1 }, 0.0);
        }
        Evaluation evaluation = new Evaluation();
        evaluation.evalTimeSeries(labels, predicted);
        Evaluation evaluation2 = new Evaluation();
        evaluation2.evalTimeSeries(labels2, predicted2, labelsMask);
        System.out.println(evaluation.stats());
        System.out.println(evaluation2.stats());
        Assert.assertEquals(evaluation.accuracy(), evaluation2.accuracy(), 1.0E-12);
        Assert.assertEquals(evaluation.f1(), evaluation2.f1(), 1.0E-12);
        EvalTest.assertMapEquals(evaluation.falsePositives(), evaluation2.falsePositives());
        EvalTest.assertMapEquals(evaluation.falseNegatives(), evaluation2.falseNegatives());
        EvalTest.assertMapEquals(evaluation.truePositives(), evaluation2.truePositives());
        EvalTest.assertMapEquals(evaluation.trueNegatives(), evaluation2.trueNegatives());
        for (int i = 0; i < nOut; i++)
            Assert.assertEquals(evaluation.classCount(i), evaluation2.classCount(i));

    }

    @Test
    public void testFalsePerfectRecall() {
        int testSize = 100;
        int numClasses = 5;
        int winner = 1;
        int seed = 241;
        INDArray labels = Nd4j.zeros(testSize, numClasses);
        INDArray predicted = Nd4j.zeros(testSize, numClasses);
        Nd4j.getRandom().setSeed(seed);
        Random r = new Random(seed);
        // Modelling the situation when system predicts the same class every time
        for (int i = 0; i < testSize; i++) {
            // Generating random prediction but with a guaranteed winner
            INDArray rand = Nd4j.rand(1, numClasses);
            rand.put(0, winner, rand.sumNumber());
            rand.divi(rand.sumNumber());
            predicted.put(new INDArrayIndex[]{ NDArrayIndex.point(i), NDArrayIndex.all() }, rand);
            // Generating random label
            int label = r.nextInt(numClasses);
            labels.putScalar(new int[]{ i, label }, 1.0);
        }
        // Explicitly specify the amount of classes
        Evaluation eval = new Evaluation(numClasses);
        eval.eval(labels, predicted);
        // For sure we shouldn't arrive at 100% recall unless we guessed everything right for every class
        Assert.assertNotEquals(1.0, eval.recall());
    }

    @Test
    public void testEvaluationMerging() {
        int nRows = 20;
        int nCols = 3;
        Random r = new Random(12345);
        INDArray actual = Nd4j.create(nRows, nCols);
        INDArray predicted = Nd4j.create(nRows, nCols);
        for (int i = 0; i < nRows; i++) {
            int x1 = r.nextInt(nCols);
            int x2 = r.nextInt(nCols);
            actual.putScalar(new int[]{ i, x1 }, 1.0);
            predicted.putScalar(new int[]{ i, x2 }, 1.0);
        }
        Evaluation evalExpected = new Evaluation();
        evalExpected.eval(actual, predicted);
        // Now: split into 3 separate evaluation objects -> expect identical values after merging
        Evaluation eval1 = new Evaluation();
        eval1.eval(actual.get(NDArrayIndex.interval(0, 5), NDArrayIndex.all()), predicted.get(NDArrayIndex.interval(0, 5), NDArrayIndex.all()));
        Evaluation eval2 = new Evaluation();
        eval2.eval(actual.get(NDArrayIndex.interval(5, 10), NDArrayIndex.all()), predicted.get(NDArrayIndex.interval(5, 10), NDArrayIndex.all()));
        Evaluation eval3 = new Evaluation();
        eval3.eval(actual.get(NDArrayIndex.interval(10, nRows), NDArrayIndex.all()), predicted.get(NDArrayIndex.interval(10, nRows), NDArrayIndex.all()));
        eval1.merge(eval2);
        eval1.merge(eval3);
        EvalTest.checkEvaluationEquality(evalExpected, eval1);
        // Next: check evaluation merging with empty, and empty merging with non-empty
        eval1 = new Evaluation();
        eval1.eval(actual.get(NDArrayIndex.interval(0, 5), NDArrayIndex.all()), predicted.get(NDArrayIndex.interval(0, 5), NDArrayIndex.all()));
        Evaluation evalInitiallyEmpty = new Evaluation();
        evalInitiallyEmpty.merge(eval1);
        evalInitiallyEmpty.merge(eval2);
        evalInitiallyEmpty.merge(eval3);
        EvalTest.checkEvaluationEquality(evalExpected, evalInitiallyEmpty);
        eval1.merge(new Evaluation());
        eval1.merge(eval2);
        eval1.merge(new Evaluation());
        eval1.merge(eval3);
        EvalTest.checkEvaluationEquality(evalExpected, eval1);
    }

    @Test
    public void testSingleClassBinaryClassification() {
        Evaluation eval = new Evaluation(1);
        for (int xe = 0; xe < 3; xe++) {
            INDArray zero = Nd4j.create(1, 1);
            INDArray one = Nd4j.ones(1, 1);
            // One incorrect, three correct
            eval.eval(one, zero);
            eval.eval(one, one);
            eval.eval(one, one);
            eval.eval(zero, zero);
            System.out.println(eval.stats());
            Assert.assertEquals(0.75, eval.accuracy(), 1.0E-6);
            Assert.assertEquals(4, eval.getNumRowCounter());
            Assert.assertEquals(1, ((int) (eval.truePositives().get(0))));
            Assert.assertEquals(2, ((int) (eval.truePositives().get(1))));
            Assert.assertEquals(1, ((int) (eval.falseNegatives().get(1))));
            eval.reset();
        }
    }

    @Test
    public void testEvalInvalid() {
        Evaluation e = new Evaluation(5);
        e.eval(0, 1);
        e.eval(1, 0);
        e.eval(1, 1);
        System.out.println(e.stats());
        char c = "\ufffd".toCharArray()[0];
        System.out.println(c);
        Assert.assertFalse(e.stats().contains("\ufffd"));
    }

    @Test
    public void testEvalMethods() {
        // Check eval(int,int) vs. eval(INDArray,INDArray)
        Evaluation e1 = new Evaluation(4);
        Evaluation e2 = new Evaluation(4);
        INDArray i0 = Nd4j.create(new double[]{ 1, 0, 0, 0 }, new long[]{ 1, 4 });
        INDArray i1 = Nd4j.create(new double[]{ 0, 1, 0, 0 }, new long[]{ 1, 4 });
        INDArray i2 = Nd4j.create(new double[]{ 0, 0, 1, 0 }, new long[]{ 1, 4 });
        INDArray i3 = Nd4j.create(new double[]{ 0, 0, 0, 1 }, new long[]{ 1, 4 });
        e1.eval(i0, i0);// order: actual, predicted

        e2.eval(0, 0);// order: predicted, actual

        e1.eval(i0, i2);
        e2.eval(2, 0);
        e1.eval(i0, i2);
        e2.eval(2, 0);
        e1.eval(i1, i2);
        e2.eval(2, 1);
        e1.eval(i3, i3);
        e2.eval(3, 3);
        e1.eval(i3, i0);
        e2.eval(0, 3);
        e1.eval(i3, i0);
        e2.eval(0, 3);
        ConfusionMatrix<Integer> cm = e1.getConfusionMatrix();
        Assert.assertEquals(1, cm.getCount(0, 0));// Order: actual, predicted

        Assert.assertEquals(2, cm.getCount(0, 2));
        Assert.assertEquals(1, cm.getCount(1, 2));
        Assert.assertEquals(1, cm.getCount(3, 3));
        Assert.assertEquals(2, cm.getCount(3, 0));
        System.out.println(e1.stats());
        System.out.println(e2.stats());
        Assert.assertEquals(e1.stats(), e2.stats());
    }

    @Test
    public void testTopNAccuracy() {
        Evaluation e = new Evaluation(null, 3);
        INDArray i0 = Nd4j.create(new double[]{ 1, 0, 0, 0, 0 }, new long[]{ 1, 5 });
        INDArray i1 = Nd4j.create(new double[]{ 0, 1, 0, 0, 0 }, new long[]{ 1, 5 });
        INDArray p0_0 = Nd4j.create(new double[]{ 0.8, 0.05, 0.05, 0.05, 0.05 }, new long[]{ 1, 5 });// class 0: highest prob

        INDArray p0_1 = Nd4j.create(new double[]{ 0.4, 0.45, 0.05, 0.05, 0.05 }, new long[]{ 1, 5 });// class 0: 2nd highest prob

        INDArray p0_2 = Nd4j.create(new double[]{ 0.1, 0.45, 0.35, 0.05, 0.05 }, new long[]{ 1, 5 });// class 0: 3rd highest prob

        INDArray p0_3 = Nd4j.create(new double[]{ 0.1, 0.4, 0.3, 0.15, 0.05 }, new long[]{ 1, 5 });// class 0: 4th highest prob

        INDArray p1_0 = Nd4j.create(new double[]{ 0.05, 0.8, 0.05, 0.05, 0.05 }, new long[]{ 1, 5 });// class 1: highest prob

        INDArray p1_1 = Nd4j.create(new double[]{ 0.45, 0.4, 0.05, 0.05, 0.05 }, new long[]{ 1, 5 });// class 1: 2nd highest prob

        INDArray p1_2 = Nd4j.create(new double[]{ 0.35, 0.1, 0.45, 0.05, 0.05 }, new long[]{ 1, 5 });// class 1: 3rd highest prob

        INDArray p1_3 = Nd4j.create(new double[]{ 0.4, 0.1, 0.3, 0.15, 0.05 }, new long[]{ 1, 5 });// class 1: 4th highest prob

        // Correct     TopNCorrect     Total
        e.eval(i0, p0_0);// 1           1               1

        Assert.assertEquals(1.0, e.accuracy(), 1.0E-6);
        Assert.assertEquals(1.0, e.topNAccuracy(), 1.0E-6);
        Assert.assertEquals(1, e.getTopNCorrectCount());
        Assert.assertEquals(1, e.getTopNTotalCount());
        e.eval(i0, p0_1);// 1           2               2

        Assert.assertEquals(0.5, e.accuracy(), 1.0E-6);
        Assert.assertEquals(1.0, e.topNAccuracy(), 1.0E-6);
        Assert.assertEquals(2, e.getTopNCorrectCount());
        Assert.assertEquals(2, e.getTopNTotalCount());
        e.eval(i0, p0_2);// 1           3               3

        Assert.assertEquals((1.0 / 3), e.accuracy(), 1.0E-6);
        Assert.assertEquals(1.0, e.topNAccuracy(), 1.0E-6);
        Assert.assertEquals(3, e.getTopNCorrectCount());
        Assert.assertEquals(3, e.getTopNTotalCount());
        e.eval(i0, p0_3);// 1           3               4

        Assert.assertEquals(0.25, e.accuracy(), 1.0E-6);
        Assert.assertEquals(0.75, e.topNAccuracy(), 1.0E-6);
        Assert.assertEquals(3, e.getTopNCorrectCount());
        Assert.assertEquals(4, e.getTopNTotalCount());
        e.eval(i1, p1_0);// 2           4               5

        Assert.assertEquals((2.0 / 5), e.accuracy(), 1.0E-6);
        Assert.assertEquals((4.0 / 5), e.topNAccuracy(), 1.0E-6);
        e.eval(i1, p1_1);// 2           5               6

        Assert.assertEquals((2.0 / 6), e.accuracy(), 1.0E-6);
        Assert.assertEquals((5.0 / 6), e.topNAccuracy(), 1.0E-6);
        e.eval(i1, p1_2);// 2           6               7

        Assert.assertEquals((2.0 / 7), e.accuracy(), 1.0E-6);
        Assert.assertEquals((6.0 / 7), e.topNAccuracy(), 1.0E-6);
        e.eval(i1, p1_3);// 2           6               8

        Assert.assertEquals((2.0 / 8), e.accuracy(), 1.0E-6);
        Assert.assertEquals((6.0 / 8), e.topNAccuracy(), 1.0E-6);
        Assert.assertEquals(6, e.getTopNCorrectCount());
        Assert.assertEquals(8, e.getTopNTotalCount());
        System.out.println(e.stats());
    }

    @Test
    public void testTopNAccuracyMerging() {
        Evaluation e1 = new Evaluation(null, 3);
        Evaluation e2 = new Evaluation(null, 3);
        INDArray i0 = Nd4j.create(new double[]{ 1, 0, 0, 0, 0 }, new long[]{ 1, 5 });
        INDArray i1 = Nd4j.create(new double[]{ 0, 1, 0, 0, 0 }, new long[]{ 1, 5 });
        INDArray p0_0 = Nd4j.create(new double[]{ 0.8, 0.05, 0.05, 0.05, 0.05 }, new long[]{ 1, 5 });// class 0: highest prob

        INDArray p0_1 = Nd4j.create(new double[]{ 0.4, 0.45, 0.05, 0.05, 0.05 }, new long[]{ 1, 5 });// class 0: 2nd highest prob

        INDArray p0_2 = Nd4j.create(new double[]{ 0.1, 0.45, 0.35, 0.05, 0.05 }, new long[]{ 1, 5 });// class 0: 3rd highest prob

        INDArray p0_3 = Nd4j.create(new double[]{ 0.1, 0.4, 0.3, 0.15, 0.05 }, new long[]{ 1, 5 });// class 0: 4th highest prob

        INDArray p1_0 = Nd4j.create(new double[]{ 0.05, 0.8, 0.05, 0.05, 0.05 }, new long[]{ 1, 5 });// class 1: highest prob

        INDArray p1_1 = Nd4j.create(new double[]{ 0.45, 0.4, 0.05, 0.05, 0.05 }, new long[]{ 1, 5 });// class 1: 2nd highest prob

        INDArray p1_2 = Nd4j.create(new double[]{ 0.35, 0.1, 0.45, 0.05, 0.05 }, new long[]{ 1, 5 });// class 1: 3rd highest prob

        INDArray p1_3 = Nd4j.create(new double[]{ 0.4, 0.1, 0.3, 0.15, 0.05 }, new long[]{ 1, 5 });// class 1: 4th highest prob

        // Correct     TopNCorrect     Total
        e1.eval(i0, p0_0);// 1           1               1

        e1.eval(i0, p0_1);// 1           2               2

        e1.eval(i0, p0_2);// 1           3               3

        e1.eval(i0, p0_3);// 1           3               4

        Assert.assertEquals(0.25, e1.accuracy(), 1.0E-6);
        Assert.assertEquals(0.75, e1.topNAccuracy(), 1.0E-6);
        Assert.assertEquals(3, e1.getTopNCorrectCount());
        Assert.assertEquals(4, e1.getTopNTotalCount());
        e2.eval(i1, p1_0);// 1           1               1

        e2.eval(i1, p1_1);// 1           2               2

        e2.eval(i1, p1_2);// 1           3               3

        e2.eval(i1, p1_3);// 1           3               4

        Assert.assertEquals((1.0 / 4), e2.accuracy(), 1.0E-6);
        Assert.assertEquals((3.0 / 4), e2.topNAccuracy(), 1.0E-6);
        Assert.assertEquals(3, e2.getTopNCorrectCount());
        Assert.assertEquals(4, e2.getTopNTotalCount());
        e1.merge(e2);
        Assert.assertEquals(8, e1.getNumRowCounter());
        Assert.assertEquals(8, e1.getTopNTotalCount());
        Assert.assertEquals(6, e1.getTopNCorrectCount());
        Assert.assertEquals((2.0 / 8), e1.accuracy(), 1.0E-6);
        Assert.assertEquals((6.0 / 8), e1.topNAccuracy(), 1.0E-6);
    }

    @Test
    public void testBinaryCase() {
        INDArray ones10 = Nd4j.ones(10, 1);
        INDArray ones4 = Nd4j.ones(4, 1);
        INDArray zeros4 = Nd4j.zeros(4, 1);
        INDArray ones3 = Nd4j.ones(3, 1);
        INDArray zeros3 = Nd4j.zeros(3, 1);
        INDArray zeros2 = Nd4j.zeros(2, 1);
        Evaluation e = new Evaluation();
        e.eval(ones10, ones10);// 10 true positives

        e.eval(ones3, zeros3);// 3 false negatives

        e.eval(zeros4, ones4);// 4 false positives

        e.eval(zeros2, zeros2);// 2 true negatives

        Assert.assertEquals(((10 + 2) / ((double) (((10 + 3) + 4) + 2))), e.accuracy(), 1.0E-6);
        Assert.assertEquals(10, ((int) (e.truePositives().get(1))));
        Assert.assertEquals(3, ((int) (e.falseNegatives().get(1))));
        Assert.assertEquals(4, ((int) (e.falsePositives().get(1))));
        Assert.assertEquals(2, ((int) (e.trueNegatives().get(1))));
        // If we switch the label around: tp becomes tn, fp becomes fn, etc
        Assert.assertEquals(10, ((int) (e.trueNegatives().get(0))));
        Assert.assertEquals(3, ((int) (e.falsePositives().get(0))));
        Assert.assertEquals(4, ((int) (e.falseNegatives().get(0))));
        Assert.assertEquals(2, ((int) (e.truePositives().get(0))));
    }

    @Test
    public void testF1FBeta_MicroMacroAveraging() {
        // Confusion matrix: rows = actual, columns = predicted
        // [3, 1, 0]
        // [2, 2, 1]
        // [0, 3, 4]
        INDArray zero = Nd4j.create(new double[]{ 1, 0, 0 }, new long[]{ 1, 3 });
        INDArray one = Nd4j.create(new double[]{ 0, 1, 0 }, new long[]{ 1, 3 });
        INDArray two = Nd4j.create(new double[]{ 0, 0, 1 }, new long[]{ 1, 3 });
        Evaluation e = new Evaluation();
        EvalTest.apply(e, 3, zero, zero);
        EvalTest.apply(e, 1, one, zero);
        EvalTest.apply(e, 2, zero, one);
        EvalTest.apply(e, 2, one, one);
        EvalTest.apply(e, 1, two, one);
        EvalTest.apply(e, 3, one, two);
        EvalTest.apply(e, 4, two, two);
        Assert.assertEquals(3, e.getConfusionMatrix().getCount(0, 0));
        Assert.assertEquals(1, e.getConfusionMatrix().getCount(0, 1));
        Assert.assertEquals(0, e.getConfusionMatrix().getCount(0, 2));
        Assert.assertEquals(2, e.getConfusionMatrix().getCount(1, 0));
        Assert.assertEquals(2, e.getConfusionMatrix().getCount(1, 1));
        Assert.assertEquals(1, e.getConfusionMatrix().getCount(1, 2));
        Assert.assertEquals(0, e.getConfusionMatrix().getCount(2, 0));
        Assert.assertEquals(3, e.getConfusionMatrix().getCount(2, 1));
        Assert.assertEquals(4, e.getConfusionMatrix().getCount(2, 2));
        double beta = 3.5;
        double[] prec = new double[3];
        double[] rec = new double[3];
        for (int i = 0; i < 3; i++) {
            prec[i] = (e.truePositives().get(i)) / ((double) ((e.truePositives().get(i)) + (e.falsePositives().get(i))));
            rec[i] = (e.truePositives().get(i)) / ((double) ((e.truePositives().get(i)) + (e.falseNegatives().get(i))));
        }
        // Binarized confusion
        // class 0:
        // [3, 1]       [tp fn]
        // [2, 10]      [fp tn]
        Assert.assertEquals(3, ((int) (e.truePositives().get(0))));
        Assert.assertEquals(1, ((int) (e.falseNegatives().get(0))));
        Assert.assertEquals(2, ((int) (e.falsePositives().get(0))));
        Assert.assertEquals(10, ((int) (e.trueNegatives().get(0))));
        // class 1:
        // [2, 3]       [tp fn]
        // [4, 7]       [fp tn]
        Assert.assertEquals(2, ((int) (e.truePositives().get(1))));
        Assert.assertEquals(3, ((int) (e.falseNegatives().get(1))));
        Assert.assertEquals(4, ((int) (e.falsePositives().get(1))));
        Assert.assertEquals(7, ((int) (e.trueNegatives().get(1))));
        // class 2:
        // [4, 3]       [tp fn]
        // [1, 8]       [fp tn]
        Assert.assertEquals(4, ((int) (e.truePositives().get(2))));
        Assert.assertEquals(3, ((int) (e.falseNegatives().get(2))));
        Assert.assertEquals(1, ((int) (e.falsePositives().get(2))));
        Assert.assertEquals(8, ((int) (e.trueNegatives().get(2))));
        double[] fBeta = new double[3];
        double[] f1 = new double[3];
        double[] mcc = new double[3];
        for (int i = 0; i < 3; i++) {
            fBeta[i] = (((1 + (beta * beta)) * (prec[i])) * (rec[i])) / (((beta * beta) * (prec[i])) + (rec[i]));
            f1[i] = ((2 * (prec[i])) * (rec[i])) / ((prec[i]) + (rec[i]));
            Assert.assertEquals(fBeta[i], e.fBeta(beta, i), 1.0E-6);
            Assert.assertEquals(f1[i], e.f1(i), 1.0E-6);
            double gmeasure = Math.sqrt(((prec[i]) * (rec[i])));
            Assert.assertEquals(gmeasure, e.gMeasure(i), 1.0E-6);
            double tp = e.truePositives().get(i);
            double tn = e.trueNegatives().get(i);
            double fp = e.falsePositives().get(i);
            double fn = e.falseNegatives().get(i);
            mcc[i] = ((tp * tn) - (fp * fn)) / (Math.sqrt(((((tp + fp) * (tp + fn)) * (tn + fp)) * (tn + fn))));
            Assert.assertEquals(mcc[i], e.matthewsCorrelation(i), 1.0E-6);
        }
        // Test macro and micro averaging:
        int tp = 0;
        int fn = 0;
        int fp = 0;
        int tn = 0;
        double macroPrecision = 0.0;
        double macroRecall = 0.0;
        double macroF1 = 0.0;
        double macroFBeta = 0.0;
        double macroMcc = 0.0;
        for (int i = 0; i < 3; i++) {
            tp += e.truePositives().get(i);
            fn += e.falseNegatives().get(i);
            fp += e.falsePositives().get(i);
            tn += e.trueNegatives().get(i);
            macroPrecision += prec[i];
            macroRecall += rec[i];
            macroF1 += f1[i];
            macroFBeta += fBeta[i];
            macroMcc += mcc[i];
        }
        macroPrecision /= 3;
        macroRecall /= 3;
        macroF1 /= 3;
        macroFBeta /= 3;
        macroMcc /= 3;
        double microPrecision = tp / ((double) (tp + fp));
        double microRecall = tp / ((double) (tp + fn));
        double microFBeta = (((1 + (beta * beta)) * microPrecision) * microRecall) / (((beta * beta) * microPrecision) + microRecall);
        double microF1 = ((2 * microPrecision) * microRecall) / (microPrecision + microRecall);
        double microMcc = ((tp * tn) - (fp * fn)) / (Math.sqrt(((((tp + fp) * (tp + fn)) * (tn + fp)) * (tn + fn))));
        Assert.assertEquals(microPrecision, e.precision(Micro), 1.0E-6);
        Assert.assertEquals(microRecall, e.recall(Micro), 1.0E-6);
        Assert.assertEquals(macroPrecision, e.precision(Macro), 1.0E-6);
        Assert.assertEquals(macroRecall, e.recall(Macro), 1.0E-6);
        Assert.assertEquals(microFBeta, e.fBeta(beta, Micro), 1.0E-6);
        Assert.assertEquals(macroFBeta, e.fBeta(beta, Macro), 1.0E-6);
        Assert.assertEquals(microF1, e.f1(Micro), 1.0E-6);
        Assert.assertEquals(macroF1, e.f1(Macro), 1.0E-6);
        Assert.assertEquals(microMcc, e.matthewsCorrelation(Micro), 1.0E-6);
        Assert.assertEquals(macroMcc, e.matthewsCorrelation(Macro), 1.0E-6);
    }

    @Test
    public void testConfusionMatrixStats() {
        Evaluation e = new Evaluation();
        INDArray c0 = Nd4j.create(new double[]{ 1, 0, 0 }, new long[]{ 1, 3 });
        INDArray c1 = Nd4j.create(new double[]{ 0, 1, 0 }, new long[]{ 1, 3 });
        INDArray c2 = Nd4j.create(new double[]{ 0, 0, 1 }, new long[]{ 1, 3 });
        EvalTest.apply(e, 3, c2, c0);// Predicted class 2 when actually class 0, 3 times

        EvalTest.apply(e, 2, c0, c1);// Predicted class 0 when actually class 1, 2 times

        String s1 = " 0 0 3 | 0 = 0";// First row: predicted 2, actual 0 - 3 times

        String s2 = " 2 0 0 | 1 = 1";// Second row: predicted 0, actual 1 - 2 times

        String stats = e.stats();
        Assert.assertTrue(stats, stats.contains(s1));
        Assert.assertTrue(stats, stats.contains(s2));
    }

    @Test
    public void testEvalBinaryMetrics() {
        Evaluation ePosClass1_nOut2 = new Evaluation(2, 1);
        Evaluation ePosClass0_nOut2 = new Evaluation(2, 0);
        Evaluation ePosClass1_nOut1 = new Evaluation(2, 1);
        Evaluation ePosClass0_nOut1 = new Evaluation(2, 0);
        Evaluation ePosClassNull_nOut2 = new Evaluation(2, null);
        Evaluation ePosClassNull_nOut1 = new Evaluation(2, null);
        Evaluation[] evals = new Evaluation[]{ ePosClass1_nOut2, ePosClass0_nOut2, ePosClass1_nOut1, ePosClass0_nOut1 };
        int[] posClass = new int[]{ 1, 0, 1, 0, -1, -1 };
        // Correct, actual positive class -> TP
        INDArray p1_1 = Nd4j.create(new double[]{ 0.3, 0.7 }, new long[]{ 1, 2 });
        INDArray l1_1 = Nd4j.create(new double[]{ 0, 1 }, new long[]{ 1, 2 });
        INDArray p1_0 = Nd4j.create(new double[]{ 0.7, 0.3 }, new long[]{ 1, 2 });
        INDArray l1_0 = Nd4j.create(new double[]{ 1, 0 }, new long[]{ 1, 2 });
        // Incorrect, actual positive class -> FN
        INDArray p2_1 = Nd4j.create(new double[]{ 0.6, 0.4 }, new long[]{ 1, 2 });
        INDArray l2_1 = Nd4j.create(new double[]{ 0, 1 }, new long[]{ 1, 2 });
        INDArray p2_0 = Nd4j.create(new double[]{ 0.4, 0.6 }, new long[]{ 1, 2 });
        INDArray l2_0 = Nd4j.create(new double[]{ 1, 0 }, new long[]{ 1, 2 });
        // Correct, actual negative class -> TN
        INDArray p3_1 = Nd4j.create(new double[]{ 0.8, 0.2 }, new long[]{ 1, 2 });
        INDArray l3_1 = Nd4j.create(new double[]{ 1, 0 }, new long[]{ 1, 2 });
        INDArray p3_0 = Nd4j.create(new double[]{ 0.2, 0.8 }, new long[]{ 1, 2 });
        INDArray l3_0 = Nd4j.create(new double[]{ 0, 1 }, new long[]{ 1, 2 });
        // Incorrect, actual negative class -> FP
        INDArray p4_1 = Nd4j.create(new double[]{ 0.45, 0.55 }, new long[]{ 1, 2 });
        INDArray l4_1 = Nd4j.create(new double[]{ 1, 0 }, new long[]{ 1, 2 });
        INDArray p4_0 = Nd4j.create(new double[]{ 0.55, 0.45 }, new long[]{ 1, 2 });
        INDArray l4_0 = Nd4j.create(new double[]{ 0, 1 }, new long[]{ 1, 2 });
        int tp = 7;
        int fn = 5;
        int tn = 3;
        int fp = 1;
        for (int i = 0; i < tp; i++) {
            ePosClass1_nOut2.eval(l1_1, p1_1);
            ePosClass1_nOut1.eval(l1_1.getColumn(1).reshape(1, (-1)), p1_1.getColumn(1).reshape(1, (-1)));
            ePosClass0_nOut2.eval(l1_0, p1_0);
            ePosClass0_nOut1.eval(l1_0.getColumn(1).reshape(1, (-1)), p1_0.getColumn(1).reshape(1, (-1)));// label 0 = instance of positive class

            ePosClassNull_nOut2.eval(l1_1, p1_1);
            ePosClassNull_nOut1.eval(l1_0.getColumn(0).reshape(1, (-1)), p1_0.getColumn(0).reshape(1, (-1)));
        }
        for (int i = 0; i < fn; i++) {
            ePosClass1_nOut2.eval(l2_1, p2_1);
            ePosClass1_nOut1.eval(l2_1.getColumn(1).reshape(1, (-1)), p2_1.getColumn(1).reshape(1, (-1)));
            ePosClass0_nOut2.eval(l2_0, p2_0);
            ePosClass0_nOut1.eval(l2_0.getColumn(1).reshape(1, (-1)), p2_0.getColumn(1).reshape(1, (-1)));
            ePosClassNull_nOut2.eval(l2_1, p2_1);
            ePosClassNull_nOut1.eval(l2_0.getColumn(0).reshape(1, (-1)), p2_0.getColumn(0).reshape(1, (-1)));
        }
        for (int i = 0; i < tn; i++) {
            ePosClass1_nOut2.eval(l3_1, p3_1);
            ePosClass1_nOut1.eval(l3_1.getColumn(1).reshape(1, (-1)), p3_1.getColumn(1).reshape(1, (-1)));
            ePosClass0_nOut2.eval(l3_0, p3_0);
            ePosClass0_nOut1.eval(l3_0.getColumn(1).reshape(1, (-1)), p3_0.getColumn(1).reshape(1, (-1)));
            ePosClassNull_nOut2.eval(l3_1, p3_1);
            ePosClassNull_nOut1.eval(l3_0.getColumn(0).reshape(1, (-1)), p3_0.getColumn(0).reshape(1, (-1)));
        }
        for (int i = 0; i < fp; i++) {
            ePosClass1_nOut2.eval(l4_1, p4_1);
            ePosClass1_nOut1.eval(l4_1.getColumn(1).reshape(1, (-1)), p4_1.getColumn(1).reshape(1, (-1)));
            ePosClass0_nOut2.eval(l4_0, p4_0);
            ePosClass0_nOut1.eval(l4_0.getColumn(1).reshape(1, (-1)), p4_0.getColumn(1).reshape(1, (-1)));
            ePosClassNull_nOut2.eval(l4_1, p4_1);
            ePosClassNull_nOut1.eval(l4_0.getColumn(0).reshape(1, (-1)), p4_0.getColumn(0).reshape(1, (-1)));
        }
        for (int i = 0; i < 4; i++) {
            int positiveClass = posClass[i];
            String m = String.valueOf(i);
            int tpAct = evals[i].truePositives().get(positiveClass);
            int tnAct = evals[i].trueNegatives().get(positiveClass);
            int fpAct = evals[i].falsePositives().get(positiveClass);
            int fnAct = evals[i].falseNegatives().get(positiveClass);
            // System.out.println(evals[i].stats());
            Assert.assertEquals(m, tp, tpAct);
            Assert.assertEquals(m, tn, tnAct);
            Assert.assertEquals(m, fp, fpAct);
            Assert.assertEquals(m, fn, fnAct);
        }
        double acc = (tp + tn) / ((double) (((tp + fn) + tn) + fp));
        double rec = tp / ((double) (tp + fn));
        double prec = tp / ((double) (tp + fp));
        double f1 = (2 * (prec * rec)) / (prec + rec);
        for (int i = 0; i < (evals.length); i++) {
            String m = String.valueOf(i);
            Assert.assertEquals(m, acc, evals[i].accuracy(), 1.0E-5);
            Assert.assertEquals(m, prec, evals[i].precision(), 1.0E-5);
            Assert.assertEquals(m, rec, evals[i].recall(), 1.0E-5);
            Assert.assertEquals(m, f1, evals[i].f1(), 1.0E-5);
        }
        // Also check macro-averaged versions (null positive class):
        Assert.assertEquals(acc, ePosClassNull_nOut2.accuracy(), 1.0E-6);
        Assert.assertEquals(ePosClass1_nOut2.recall(Macro), ePosClassNull_nOut2.recall(), 1.0E-6);
        Assert.assertEquals(ePosClass1_nOut2.precision(Macro), ePosClassNull_nOut2.precision(), 1.0E-6);
        Assert.assertEquals(ePosClass1_nOut2.f1(Macro), ePosClassNull_nOut2.f1(), 1.0E-6);
        Assert.assertEquals(acc, ePosClassNull_nOut1.accuracy(), 1.0E-6);
        Assert.assertEquals(ePosClass1_nOut2.recall(Macro), ePosClassNull_nOut1.recall(), 1.0E-6);
        Assert.assertEquals(ePosClass1_nOut2.precision(Macro), ePosClassNull_nOut1.precision(), 1.0E-6);
        Assert.assertEquals(ePosClass1_nOut2.f1(Macro), ePosClassNull_nOut1.f1(), 1.0E-6);
    }

    @Test
    public void testConfusionMatrixString() {
        Evaluation e = new Evaluation(Arrays.asList("a", "b", "c"));
        INDArray class0 = Nd4j.create(new double[]{ 1, 0, 0 }, new long[]{ 1, 3 });
        INDArray class1 = Nd4j.create(new double[]{ 0, 1, 0 }, new long[]{ 1, 3 });
        INDArray class2 = Nd4j.create(new double[]{ 0, 0, 1 }, new long[]{ 1, 3 });
        // Predicted class 0, actual class 1 x2
        e.eval(class0, class1);
        e.eval(class0, class1);
        e.eval(class2, class2);
        e.eval(class2, class2);
        e.eval(class2, class2);
        String s = e.confusionMatrix();
        // System.out.println(s);
        String exp = " 0 1 2\n" + (((("-------\n" + " 0 2 0 | 0 = a\n")// 0 predicted as 1, 2 times
         + " 0 0 0 | 1 = b\n") + " 0 0 3 | 2 = c\n")// 2 predicted as 2, 3 times
         + "\nConfusion matrix format: Actual (rowClass) predicted as (columnClass) N times");
        Assert.assertEquals(exp, s);
        System.out.println("============================");
        System.out.println(e.stats());
        System.out.println("\n\n\n\n");
        // Test with 21 classes (> threshold)
        e = new Evaluation();
        class0 = Nd4j.create(1, 31);
        class0.putScalar(0, 1);
        e.eval(class0, class0);
        System.out.println(e.stats());
        System.out.println("\n\n\n\n");
        System.out.println(e.stats(false, true));
    }

    @Test
    public void testEvaluationNaNs() {
        Evaluation e = new Evaluation();
        INDArray predictions = Nd4j.create(new double[]{ 0.1, Double.NaN, 0.3 }, new long[]{ 1, 3 });
        INDArray labels = Nd4j.create(new double[]{ 0, 0, 1 }, new long[]{ 1, 3 });
        try {
            e.eval(labels, predictions);
        } catch (IllegalStateException ex) {
            Assert.assertTrue(ex.getMessage().contains("NaN"));
        }
    }
}

