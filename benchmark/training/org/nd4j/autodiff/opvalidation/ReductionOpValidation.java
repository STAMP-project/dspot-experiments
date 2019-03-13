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
package org.nd4j.autodiff.opvalidation;


import DataType.DOUBLE;
import DataType.FLOAT;
import DataType.INT;
import DataType.LONG;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.OpValidationSuite;
import org.nd4j.autodiff.samediff.SDVariable;
import org.nd4j.autodiff.samediff.SameDiff;
import org.nd4j.autodiff.validation.OpTestCase;
import org.nd4j.autodiff.validation.OpValidation;
import org.nd4j.autodiff.validation.TestCase;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.checkutil.NDArrayCreationUtil;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.linalg.indexing.BooleanIndexing;
import org.nd4j.linalg.indexing.conditions.Conditions;
import org.nd4j.linalg.ops.transforms.Transforms;
import org.nd4j.linalg.primitives.Pair;


@Slf4j
@RunWith(Parameterized.class)
public class ReductionOpValidation extends BaseOpValidation {
    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

    public ReductionOpValidation(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testStdev() {
        List<String> errors = new ArrayList<>();
        for (Pair<INDArray, String> p : NDArrayCreationUtil.getAllTestMatricesWithShape(3, 4, 12345, DOUBLE)) {
            for (boolean biasCorrected : new boolean[]{ false, true }) {
                SameDiff sd = SameDiff.create();
                SDVariable var = sd.var("in", p.getFirst());
                SDVariable stdev = var.std(biasCorrected);
                INDArray expOut = p.getFirst().std(biasCorrected);
                TestCase tc = new TestCase(sd).testName((((p.getSecond()) + " - biasCorrected=") + biasCorrected)).expected(stdev, expOut).gradientCheck(false);
                String err = OpValidation.validate(tc);
                if (err != null) {
                    errors.add(err);
                }
            }
        }
        Assert.assertEquals(errors.toString(), 0, errors.size());
    }

    @Test
    public void testZeroCount() {
        List<String> allFailed = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            SameDiff sd = SameDiff.create();
            INDArray ia;
            if (i == 0) {
                // Not gradient checkable for 0 and 1 values
                ia = Nd4j.create(new int[]{ 2, 2 }, new float[]{ 0, 1, 0, 1 }).castTo(DOUBLE);
            } else {
                ia = Nd4j.rand(DOUBLE, 2, 2);
            }
            SDVariable input = sd.var("in", DOUBLE, 2, 2);
            sd.associateArrayWithVariable(ia, input);
            SDVariable nonZero = sd.math().countNonZero(input);
            SDVariable zero = sd.math().countZero(input);
            SDVariable loss = nonZero.add(zero).castTo(DOUBLE).std(true);
            String error = OpValidation.validate(gradientCheck((i != 0)));
            if (error != null)
                allFailed.add(error);

        }
        Assert.assertEquals(allFailed.toString(), 0, allFailed.size());
    }

    @Test
    public void testZeroFraction() {
        List<String> allFailed = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            SameDiff sd = SameDiff.create();
            INDArray ia;
            if (i == 0) {
                // Not gradient checkable for 0 and 1 values
                ia = Nd4j.create(new int[]{ 2, 2 }, new float[]{ 0, 1, 0, 1 });
            } else {
                ia = Nd4j.rand(FLOAT, 2, 2);
            }
            SDVariable input = sd.var("in", 2, 2);
            sd.associateArrayWithVariable(ia, input);
            SDVariable zeroFraction = sd.math().zeroFraction(input);
            String error = OpValidation.validate(gradientCheck((i != 0)));
            if (error != null)
                allFailed.add(error);

        }
        Assert.assertEquals(allFailed.toString(), 0, allFailed.size());
    }

    @Test
    public void testReductionGradientsSimple() {
        OpValidationSuite.ignoreFailing();// TODO TEMPORARY DUE TO CRASHES

        // Test reductions: final and only function
        Nd4j.getRandom().setSeed(12345);
        List<String> failed = new ArrayList<>();
        for (int i = 0; i < 21; i++) {
            SameDiff sd = SameDiff.create();
            int nOut = 4;
            int minibatch = 10;
            SDVariable input = sd.var("in", (-1), nOut);
            INDArray inputArr = Nd4j.randn(minibatch, nOut).muli(100);
            long length = nOut * minibatch;
            SDVariable loss;
            String name;
            TestCase tc = new TestCase(sd);
            switch (i) {
                case 0 :
                    loss = sd.mean("loss", input);
                    name = "mean";
                    tc.expectedOutput("loss", inputArr.mean());
                    break;
                case 1 :
                    loss = sd.sum("loss", input);
                    name = "sum";
                    tc.expectedOutput("loss", inputArr.sum());
                    break;
                case 2 :
                    loss = sd.standardDeviation("loss", input, true);
                    name = "stdev";
                    tc.expectedOutput("loss", inputArr.std(true));
                    break;
                case 3 :
                    loss = sd.min("loss", input);
                    name = "min";
                    tc.expectedOutput("loss", inputArr.min());
                    break;
                case 4 :
                    loss = sd.max("loss", input);
                    name = "max";
                    tc.expectedOutput("loss", inputArr.max());
                    break;
                case 5 :
                    loss = sd.variance("loss", input, true);
                    name = "variance";
                    tc.expectedOutput("loss", inputArr.var());
                    break;
                case 6 :
                    inputArr = Nd4j.rand(minibatch, nOut).addi(0.5);
                    loss = sd.prod("loss", input);
                    tc.expectedOutput("loss", inputArr.prod());
                    name = "prod";
                    break;
                case 7 :
                    loss = sd.norm1("loss", input);
                    name = "norm1";
                    tc.expectedOutput("loss", inputArr.norm1());
                    break;
                case 8 :
                    loss = sd.norm2("loss", input);
                    name = "norm2";
                    tc.expectedOutput("loss", inputArr.norm2());
                    break;
                case 9 :
                    loss = sd.normmax("loss", input);
                    name = "normmax";
                    tc.expectedOutput("loss", inputArr.normmax());
                    break;
                case 10 :
                    loss = sd.math().countNonZero("loss", input);
                    name = "countNonZero";
                    tc.expectedOutput("loss", Nd4j.trueScalar(inputArr.length()));
                    break;
                case 11 :
                    loss = sd.math().countZero("loss", input);
                    name = "countZero";
                    tc.expectedOutput("loss", Nd4j.trueScalar(0));
                    break;
                case 12 :
                    loss = sd.math().amax("loss", input);
                    name = "amax";
                    tc.expectedOutput("loss", inputArr.amax());
                    break;
                case 13 :
                    loss = sd.math().amin("loss", input);
                    name = "amin";
                    tc.expectedOutput("loss", inputArr.amin());
                    break;
                case 14 :
                    loss = sd.math().asum("loss", input);
                    name = "asum";
                    tc.expectedOutput("loss", Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.impl.reduce.same.ASum(inputArr.dup())));
                    break;
                case 15 :
                    loss = sd.math().amean("loss", input);
                    name = "amean";
                    tc.expectedOutput("loss", Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.impl.reduce.floating.AMean(inputArr.dup())));
                    break;
                case 16 :
                    loss = sd.math().entropy("loss", input);
                    name = "entropy";
                    inputArr = Nd4j.linspace(0.01, 0.99, length, DOUBLE).reshape('c', minibatch, nOut);
                    tc.expected("loss", inputArr.mul(Transforms.log(inputArr, true)).sum(Integer.MAX_VALUE).negi());
                    break;
                case 17 :
                    inputArr = Nd4j.rand(minibatch, nOut);
                    name = "logsumexp";
                    loss = sd.math().logSumExp("loss", input);
                    INDArray expArr = Transforms.exp(inputArr);
                    double sum = expArr.sumNumber().doubleValue();
                    tc.expected("loss", Nd4j.create(new double[]{ Math.log(sum) }));
                    break;
                case 18 :
                    inputArr = Nd4j.rand(minibatch, nOut);
                    name = "sqnorm";
                    loss = sd.squaredNorm("loss", input);
                    double norm2 = inputArr.norm2Number().doubleValue();
                    tc.expected("loss", Nd4j.trueScalar((norm2 * norm2)));
                    break;
                case 19 :
                    inputArr = Nd4j.rand(minibatch, nOut);
                    name = "logEntropy";
                    loss = sd.math().logEntropy("loss", input);
                    double logEntropy = inputArr.logEntropyNumber().doubleValue();
                    tc.expected(loss, Nd4j.trueScalar(logEntropy));
                    break;
                case 20 :
                    inputArr = Nd4j.rand(minibatch, nOut);
                    name = "shannonEntropy";
                    loss = sd.math().shannonEntropy("loss", input);
                    double shannonEntropy = inputArr.shannonEntropyNumber().doubleValue();
                    tc.expected(loss, Nd4j.trueScalar(shannonEntropy));
                    if (OpValidationSuite.IGNORE_FAILING) {
                        continue;
                    }
                    break;
                default :
                    throw new RuntimeException();
            }
            String msg = (("test: " + i) + " - ") + name;
            log.info(("*** Starting test: " + msg));
            sd.associateArrayWithVariable(inputArr, input);
            tc.testName(msg);
            String error = OpValidation.validate(tc, true);
            if (error != null)
                failed.add(error);

        }
        Assert.assertEquals(failed.toString(), 0, failed.size());
    }

    @Test
    public void testReductionGradients1() {
        // Test reductions: final, but *not* the only function
        Nd4j.getRandom().setSeed(12345);
        List<String> failed = new ArrayList<>();
        for (int dim : new int[]{ 0, Integer.MAX_VALUE }) {
            // These two cases are equivalent here
            // for (int i = 0; i < 18; i++) {
            for (int i = 2; i < 3; i++) {
                SameDiff sd = SameDiff.create();
                int nOut = 4;
                int minibatch = 10;
                SDVariable input = sd.placeHolder("in", DOUBLE, (-1), nOut);
                SDVariable label = sd.placeHolder("label", DOUBLE, (-1), nOut);
                SDVariable diff = input.sub(label);
                SDVariable sqDiff = diff.mul(diff);
                SDVariable msePerEx = sd.mean("msePerEx", sqDiff, 1);
                SDVariable loss;
                String name;
                TestCase tc = new TestCase(sd);
                boolean uDistInput = false;
                switch (i) {
                    case 0 :
                        loss = sd.mean("loss", msePerEx, dim);
                        name = "mean";
                        break;
                    case 1 :
                        loss = sd.sum("loss", msePerEx, dim);
                        name = "sum";
                        break;
                    case 2 :
                        loss = sd.standardDeviation("loss", msePerEx, true, dim);
                        name = "stdev";
                        break;
                    case 3 :
                        loss = sd.min("loss", msePerEx, dim);
                        name = "min";
                        break;
                    case 4 :
                        loss = sd.max("loss", msePerEx, dim);
                        name = "max";
                        break;
                    case 5 :
                        loss = sd.variance("loss", msePerEx, true, dim);
                        name = "variance";
                        break;
                    case 6 :
                        loss = sd.prod("loss", msePerEx, dim);
                        name = "prod";
                        break;
                    case 7 :
                        loss = sd.norm1("loss", msePerEx, dim);
                        name = "norm1";
                        break;
                    case 8 :
                        loss = sd.norm2("loss", msePerEx, dim);
                        name = "norm2";
                        break;
                    case 9 :
                        loss = sd.normmax("loss", msePerEx, dim);
                        name = "normmax";
                        break;
                    case 10 :
                        loss = sd.math().countNonZero("loss", msePerEx, dim).castTo(DOUBLE);
                        name = "countNonZero";
                        break;
                    case 11 :
                        loss = sd.math().countZero("loss", msePerEx, dim).castTo(DOUBLE);
                        name = "countZero";
                        break;
                    case 12 :
                        loss = sd.math().amax("loss", msePerEx, dim);
                        name = "amax";
                        break;
                    case 13 :
                        loss = sd.math().amin("loss", msePerEx, dim);
                        name = "amin";
                        break;
                    case 14 :
                        loss = sd.math().asum("loss", msePerEx, dim);
                        name = "asum";
                        break;
                    case 15 :
                        loss = sd.math().amean("loss", msePerEx, dim);
                        name = "amean";
                        break;
                    case 16 :
                        loss = sd.math().entropy("loss", msePerEx, dim);
                        name = "entropy";
                        break;
                    case 17 :
                        name = "logEntropy";
                        loss = sd.math().logEntropy("loss", msePerEx, dim);
                        uDistInput = true;
                        break;
                    default :
                        throw new RuntimeException();
                }
                String msg = ((((("(test " + i) + " - ") + name) + ", dimension=") + dim) + ")";
                log.info(("*** Starting test: " + msg));
                INDArray inputArr = (uDistInput) ? Nd4j.rand(DOUBLE, minibatch, nOut) : Nd4j.randn(DOUBLE, minibatch, nOut).muli(100);
                INDArray labelArr = (uDistInput) ? Nd4j.rand(DOUBLE, minibatch, nOut) : Nd4j.randn(DOUBLE, minibatch, nOut).muli(100);
                sd.associateArrayWithVariable(inputArr, input);
                sd.associateArrayWithVariable(labelArr, label);
                String error = OpValidation.validate(tc);
                if (error != null) {
                    failed.add(name);
                }
            }
        }
        Assert.assertEquals(("Failed: " + failed), 0, failed.size());
    }

    @Test
    public void testReductionGradients2() {
        // Test reductions: NON-final function
        Nd4j.getRandom().setSeed(12345);
        int d0 = 3;
        int d1 = 4;
        int d2 = 5;
        List<String> failed = new ArrayList<>();
        for (int reduceDim : new int[]{ 0, 1, 2 }) {
            for (int i = 0; i < 18; i++) {
                long[] outShape;
                switch (reduceDim) {
                    case 0 :
                        outShape = new long[]{ d1, d2 };
                        break;
                    case 1 :
                        outShape = new long[]{ d0, d2 };
                        break;
                    case 2 :
                        outShape = new long[]{ d0, d1 };
                        break;
                    default :
                        throw new RuntimeException();
                }
                SameDiff sd = SameDiff.create();
                sd.setLogExecution(false);
                SDVariable in = sd.var("in", (-1), d1, d2);
                SDVariable label = sd.var("label", outShape);
                SDVariable second = in.mul(2);
                double maxRelError = 1.0E-4;
                double minAbsError = 1.0E-4;
                INDArray inputArr = Nd4j.randn(DOUBLE, d0, d1, d2).muli(1000);
                INDArray labelArr = Nd4j.randn(DOUBLE, outShape).muli(1000);
                SDVariable reduced;
                String name;
                TestCase tc = new TestCase(sd);
                switch (i) {
                    case 0 :
                        reduced = sd.mean("reduced", second, reduceDim);
                        name = "mean";
                        break;
                    case 1 :
                        inputArr.divi(100);
                        labelArr.divi(100);
                        reduced = sd.sum("reduced", second, reduceDim);
                        name = "sum";
                        break;
                    case 2 :
                        reduced = sd.standardDeviation("reduced", second, true, reduceDim);
                        inputArr.divi(1000);
                        labelArr.divi(1000);
                        name = "stdev";
                        break;
                    case 3 :
                        reduced = sd.min("reduced", second, reduceDim);
                        name = "min";
                        break;
                    case 4 :
                        reduced = sd.max("reduced", second, reduceDim);
                        name = "max";
                        break;
                    case 5 :
                        // Variance is a bit finniky for gradient checks, due to huge score/output...
                        maxRelError = 0.001;
                        minAbsError = 1;// Most gradients are in the range 1k to >100k

                        inputArr.divi(10);
                        labelArr.divi(100);
                        BooleanIndexing.replaceWhere(inputArr, Nd4j.rand(inputArr.shape()).muli(100).addi(100), Conditions.absLessThan(1.0));
                        reduced = sd.variance("reduced", second, true, reduceDim);
                        name = "variance";
                        break;
                    case 6 :
                        inputArr.assign(Nd4j.rand(DOUBLE, new int[]{ d0, d1, d2 }).addi(0.5));
                        labelArr.assign(Nd4j.rand(DOUBLE, outShape).addi(0.5));
                        reduced = sd.prod("reduced", second, reduceDim);
                        name = "prod";
                        break;
                    case 7 :
                        maxRelError = 1.0E-4;
                        inputArr.assign(Nd4j.rand(DOUBLE, new int[]{ d0, d1, d2 }).muli(10));
                        labelArr.assign(Nd4j.rand(DOUBLE, outShape).muli(10));
                        reduced = sd.norm1("reduced", second, reduceDim);
                        name = "norm1";
                        break;
                    case 8 :
                        maxRelError = 1.0E-4;
                        reduced = sd.norm2("reduced", second, reduceDim);
                        name = "norm2";
                        break;
                    case 9 :
                        inputArr = Nd4j.rand(DOUBLE, new int[]{ d0, d1, d2 });
                        labelArr = Nd4j.rand(DOUBLE, outShape);
                        reduced = sd.normmax("reduced", second, reduceDim);
                        name = "normmax";
                        break;
                    case 10 :
                        reduced = sd.argmax("reduced", second, reduceDim);
                        name = "argmax";
                        break;
                    case 11 :
                        reduced = sd.argmin("reduced", second, reduceDim);
                        name = "argmin";
                        break;
                    case 12 :
                        reduced = sd.math().countNonZero("reduced", second, reduceDim);
                        name = "countNonZero";
                        break;
                    case 13 :
                        reduced = sd.math().countZero("reduced", second, reduceDim);
                        name = "countZero";
                        break;
                    case 14 :
                        reduced = sd.math().amax("reduced", second, reduceDim);
                        name = "amax";
                        break;
                    case 15 :
                        reduced = sd.math().amin("reduced", second, reduceDim);
                        name = "amin";
                        break;
                    case 16 :
                        reduced = sd.math().asum("reduced", second, reduceDim);
                        name = "asum";
                        break;
                    case 17 :
                        reduced = sd.math().amean("reduced", second, reduceDim);
                        name = "amean";
                        break;
                    default :
                        throw new RuntimeException();
                }
                SDVariable add = reduced.castTo(DOUBLE).add(1.0);
                SDVariable diff = label.sub(add);
                SDVariable sqDiff = diff.mul(diff);
                SDVariable mseLoss = sd.mean("loss", sqDiff);
                String msg = ((((("(test " + i) + " - ") + name) + ", dimension=") + reduceDim) + ")";
                log.info(("*** Starting test: " + msg));
                sd.associateArrayWithVariable(inputArr, in);
                sd.associateArrayWithVariable(labelArr, label);
                tc.gradCheckMaxRelativeError(maxRelError);
                tc.gradCheckMinAbsError(minAbsError);
                // sd.execAndEndResult();
                String error = OpValidation.validate(tc);
                if (error != null) {
                    failed.add(((name + " - ") + error));
                }
            }
        }
        Assert.assertEquals(("Failed: " + failed), 0, failed.size());
    }

    @Test
    public void testReduce3() {
        Nd4j.getRandom().setSeed(12345);
        int d0 = 3;
        int d1 = 4;
        int d2 = 5;
        List<String> failed = new ArrayList<>();
        for (int[] reduceDims : new int[][]{ new int[]{ Integer.MAX_VALUE }, new int[]{ 0, 1, 2 }, new int[]{ 0 }, new int[]{ 1 }, new int[]{ 2 }, new int[]{ 0, 1 }, new int[]{ 0, 2 }, new int[]{ 1, 2 } }) {
            for (int i = 6; i < 7; i++) {
                SameDiff sd = SameDiff.create();
                sd.setLogExecution(false);
                SDVariable in = sd.var("in", (-1), d1, d2);
                SDVariable in2 = sd.var("in2", (-1), d1, d2);
                INDArray inArr = Nd4j.randn(new int[]{ d0, d1, d2 }).muli(100);
                INDArray in2Arr = Nd4j.randn(inArr.shape()).muli(100);
                INDArray exp;
                SDVariable reduced;
                String name;
                TestCase tc = new TestCase(sd);
                switch (i) {
                    case 0 :
                        reduced = sd.math().manhattanDistance(in, in2, reduceDims);
                        name = "manhattan";
                        exp = Nd4j.getExecutioner().exec(new ManhattanDistance(inArr, in2Arr, null, true, false, reduceDims));
                        break;
                    case 1 :
                        reduced = sd.math().euclideanDistance(in, in2, reduceDims);
                        name = "euclidean";
                        exp = Nd4j.getExecutioner().exec(new EuclideanDistance(inArr, in2Arr, null, true, false, reduceDims));
                        break;
                    case 2 :
                        inArr.muli(1.0E-4);
                        in2Arr.muli(1.0E-4);
                        reduced = sd.math().cosineSimilarity(in, in2, reduceDims);
                        name = "cosine";
                        exp = Nd4j.getExecutioner().exec(new CosineSimilarity(inArr, in2Arr, null, true, false, reduceDims));
                        break;
                    case 3 :
                        reduced = sd.math().cosineDistance(in, in2, reduceDims);
                        name = "cosinedistance";
                        exp = Nd4j.getExecutioner().exec(new CosineDistance(inArr, in2Arr, null, true, false, reduceDims));
                        break;
                    case 4 :
                        reduced = sd.math().hammingDistance(in, in2, reduceDims);
                        name = "hamming";
                        exp = Nd4j.getExecutioner().exec(new HammingDistance(inArr, in2Arr, null, true, false, reduceDims));
                        break;
                    case 5 :
                        name = "jaccard";
                        reduced = sd.math().jaccardDistance(name, in, in2, reduceDims);
                        inArr.divi(100).addi(0.1);
                        in2Arr.divi(100).addi(0.1);
                        exp = Nd4j.getExecutioner().exec(new JaccardDistance(inArr, in2Arr, null, true, false, reduceDims));
                        if ((OpValidationSuite.IGNORE_FAILING) && ((reduceDims.length) == 2))
                            continue;

                        break;
                    case 6 :
                        if (OpValidationSuite.IGNORE_FAILING) {
                            // https://github.com/deeplearning4j/deeplearning4j/issues/6069
                            continue;
                        }
                        name = "dot";
                        reduced = sd.dot(name, in, in2, reduceDims);
                        exp = Nd4j.getExecutioner().exec(new Dot(inArr, in2Arr, null, true, false, reduceDims));
                        break;
                    default :
                        throw new RuntimeException();
                }
                // Sum: note that this should be a no-op for the full array cases
                SDVariable sum = sd.sum(reduced, Integer.MAX_VALUE);
                String msg = ((((("(test " + i) + " - ") + name) + ", dimensions=") + (Arrays.toString(reduceDims))) + ")";
                log.info(("*** Starting test: " + msg));
                sd.associateArrayWithVariable(inArr, in);
                sd.associateArrayWithVariable(in2Arr, in2);
                tc.expected(reduced, exp);
                String error = OpValidation.validate(tc, true);
                if (error != null) {
                    failed.add(((msg + " - ") + error));
                }
            }
        }
        Assert.assertEquals(("Failed: " + failed), 0, failed.size());
    }

    @Test
    public void testMoments() {
        for (int[] axes : new int[][]{ new int[]{ 0 }, new int[]{ 1 }, new int[]{ 0, 1 } }) {
            INDArray input = Nd4j.linspace(1, 12, 12).reshape(3, 4);
            SameDiff sd = SameDiff.create();
            SDVariable in = sd.var("in", input);
            SDVariable[] moments = sd.math().moments(in, axes);
            INDArray expMean = input.mean(axes);
            INDArray expVar = input.var(false, axes);
            SDVariable loss;
            if ((axes.length) < 2) {
                loss = moments[0].add(moments[1]).std(true);
            } else {
                loss = moments[0].add(moments[1]).mean();
            }
            String msg = Arrays.toString(axes);
            TestCase tc = testName(msg).expected(moments[0], expMean).expected(moments[1], expVar);
            String err = OpValidation.validate(tc);
            Assert.assertNull(err);
        }
    }

    @Test
    public void testMomentsOp() {
        int[] axes = new int[]{ 0 };
        INDArray input = Nd4j.linspace(1, 12, 12).reshape(3, 4);
        INDArray outMean = Nd4j.createUninitialized(new long[]{ 4 });
        INDArray outVar = Nd4j.createUninitialized(new long[]{ 4 });
        OpTestCase tc = new OpTestCase(new org.nd4j.linalg.api.ops.impl.reduce.Moments(input, outMean, outVar, axes));
        tc.expectedOutput(0, input.mean(axes).reshape(4));
        tc.expectedOutput(1, input.var(false, axes).reshape(4));
        String err = OpValidation.validate(tc);
        Assert.assertNull(err);
    }

    @Test
    public void testNormalizeMomentsOp() {
        INDArray data = Nd4j.linspace(1, 100, 100, DOUBLE).reshape(10, 10);
        INDArray ssSum = data.sum(0);
        INDArray ssSqSum = data.mul(data).sum(0);
        INDArray meanExp = data.mean(0);
        INDArray varExp = data.var(false, 0);
        INDArray mean = Nd4j.createUninitialized(DOUBLE, meanExp.shape());
        INDArray var = Nd4j.createUninitialized(DOUBLE, varExp.shape());
        OpTestCase op = new OpTestCase(new org.nd4j.linalg.api.ops.impl.reduce.NormalizeMoments(Nd4j.scalar(INT, 10), ssSum, ssSqSum, mean, var));
        op.expectedOutput(0, meanExp);
        op.expectedOutput(1, varExp);
        String err = OpValidation.validate(op);
        Assert.assertNull(err);
    }

    @Test
    public void testAllAny() {
        INDArray allZeros = Nd4j.zeros(FLOAT, 3, 4);
        INDArray allOnes = Nd4j.ones(FLOAT, 3, 4);
        INDArray mixed = Nd4j.zeros(FLOAT, 3, 4);
        mixed.getRow(1).assign(1.0);
        INDArray[] in = new INDArray[]{ allZeros, allOnes, mixed };
        boolean[] expAll = new boolean[]{ false, true, false };
        boolean[] expAny = new boolean[]{ false, true, true };
        for (int i = 0; i < 3; i++) {
            SameDiff sd = SameDiff.create();
            SDVariable s = sd.var("in", in[i]);
            SDVariable all = sd.f().all(s);
            SDVariable any = sd.f().any(s);
            String err = OpValidation.validate(gradientCheck(false).expected(all, Nd4j.create(new boolean[]{ expAll[i] })).expected(any, Nd4j.create(new boolean[]{ expAny[i] })));
            Assert.assertNull(err);
        }
    }

    @Test
    public void testIndexAccum() {
        List<String> failed = new ArrayList<>();
        List<int[]> dims = Arrays.asList(new int[]{ 0 }, new int[]{ 1 }, new int[]{ 0, 1 }, new int[0]);
        INDArray in = Nd4j.rand(3, 4);
        for (int t = 0; t < 4; t++) {
            int[] d = dims.get(t);
            for (int i = 0; i < 7; i++) {
                int[] dim = ((d.length) == 0) ? null : d;
                SameDiff sd = SameDiff.create();
                SDVariable s = sd.var("in", in);
                SDVariable reduce;
                String name;
                INDArray exp;
                switch (i) {
                    case 0 :
                        reduce = s.argmax(dim);
                        exp = Nd4j.argMax(in, dim).castTo(DOUBLE);
                        name = "argmax";
                        break;
                    case 1 :
                        reduce = s.argmin(dim);
                        exp = Nd4j.argMin(in, dim).castTo(DOUBLE);
                        name = "argmin";
                        break;
                    case 2 :
                        reduce = sd.math().iamax(s, dim);
                        exp = Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.impl.indexaccum.IAMax(in.dup(), dim));
                        exp = exp.castTo(DOUBLE);
                        name = "iamax";
                        break;
                    case 3 :
                        reduce = sd.math().iamin(s, dim);
                        exp = Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.impl.indexaccum.IAMin(in.dup(), dim));
                        exp = exp.castTo(DOUBLE);
                        name = "iamin";
                        break;
                    case 4 :
                        reduce = sd.math().firstIndex(s, Conditions.greaterThan(0), dim);
                        exp = in.sum(dim).assign(0);
                        exp = exp.castTo(DOUBLE);
                        name = "firstindex";
                        break;
                    case 5 :
                        reduce = sd.math().lastIndex(s, Conditions.greaterThan(0), dim);
                        if (t == 0)
                            exp = Nd4j.create(new double[]{ 2, 2, 2, 2 });
                        else
                            if (t == 1)
                                exp = Nd4j.create(new double[]{ 3, 3, 3 });
                            else
                                exp = Nd4j.create(new double[]{ 11 });


                        exp = exp.castTo(DOUBLE);
                        name = "lastindex";
                        break;
                    case 6 :
                        reduce = sd.matchConditionCount("count", s, Conditions.greaterThan(0), false, dim);
                        if (t == 0)
                            exp = Nd4j.create(new double[]{ 3, 3, 3, 3 });
                        else
                            if (t == 1)
                                exp = Nd4j.create(new double[]{ 4, 4, 4 });
                            else
                                exp = Nd4j.create(new double[]{ 12 });


                        exp = exp.castTo(DOUBLE);
                        name = "matchConditionCount";
                        break;
                    default :
                        throw new RuntimeException();
                }
                reduce = reduce.castTo(DOUBLE);
                SDVariable loss;
                if ((dim == null) || ((dim.length) == 2)) {
                    loss = reduce.mean();
                } else {
                    loss = reduce.std(true);
                }
                TestCase tc = new TestCase(sd).expected(reduce, exp).testName(((name + " - ") + (dim == null ? null : Arrays.toString(dim))));
                log.info("Starting: {}", tc.testName());
                String err = OpValidation.validate(tc, true);
                if (err != null) {
                    failed.add(err);
                }
            }
        }
        Assert.assertEquals(failed.toString(), 0, failed.size());
    }

    @Test
    public void testReduce3_2() {
        Nd4j.getRandom().setSeed(12345);
        int d0 = 3;
        int d1 = 4;
        int d2 = 5;
        for (int[] reduceDims : new int[][]{ new int[]{ Integer.MAX_VALUE }, new int[]{ 0, 1, 2 }, new int[]{ 0 }, new int[]{ 1 }, new int[]{ 2 }, new int[]{ 0, 1 }, new int[]{ 0, 2 }, new int[]{ 1, 2 } }) {
            for (int i = 0; i < 6; i++) {
                SameDiff sd = SameDiff.create();
                sd.setLogExecution(false);
                INDArray a = Nd4j.rand(DOUBLE, d0, d1, d2);
                INDArray b = Nd4j.rand(DOUBLE, d0, d1, d2);
                SDVariable in = sd.var("in", a);
                SDVariable in2 = sd.var("in2", b);
                INDArray expOut;
                SDVariable reduced;
                String name;
                switch (i) {
                    case 0 :
                        reduced = sd.math().manhattanDistance(in, in2, reduceDims);
                        name = "manhattan";
                        expOut = Nd4j.getExecutioner().exec(new ManhattanDistance(a, b, null, true, false, reduceDims));
                        break;
                    case 1 :
                        reduced = sd.math().euclideanDistance(in, in2, reduceDims);
                        name = "euclidean";
                        expOut = Nd4j.getExecutioner().exec(new EuclideanDistance(a, b, null, true, false, reduceDims));
                        break;
                    case 2 :
                        reduced = sd.math().cosineSimilarity(in, in2, reduceDims);
                        name = "cosine";
                        expOut = Nd4j.getExecutioner().exec(new CosineSimilarity(a, b, null, true, false, reduceDims));
                        break;
                    case 3 :
                        reduced = sd.math().jaccardDistance(in, in2, reduceDims);
                        name = "jaccard";
                        expOut = Nd4j.getExecutioner().exec(new JaccardDistance(a, b, null, true, false, reduceDims));
                        break;
                    case 4 :
                        reduced = sd.math().hammingDistance(in, in2, reduceDims);
                        name = "hamming";
                        expOut = Nd4j.getExecutioner().exec(new HammingDistance(a, b, null, true, false, reduceDims));
                        break;
                    case 5 :
                        reduced = sd.math().cosineDistance(in, in2, reduceDims);
                        name = "reduced";
                        expOut = Nd4j.getExecutioner().exec(new CosineDistance(a, b, null, true, false, reduceDims));
                        break;
                    default :
                        throw new RuntimeException();
                }
                long[] expShape;
                if (Arrays.equals(new int[]{ 0 }, reduceDims)) {
                    expShape = new long[]{ 4, 5 };
                } else
                    if (Arrays.equals(new int[]{ 1 }, reduceDims)) {
                        expShape = new long[]{ 3, 5 };
                    } else
                        if (Arrays.equals(new int[]{ 2 }, reduceDims)) {
                            expShape = new long[]{ 3, 4 };
                        } else
                            if (Arrays.equals(new int[]{ Integer.MAX_VALUE }, reduceDims)) {
                                expShape = new long[]{  };
                            } else
                                if (Arrays.equals(new int[]{ 0, 1 }, reduceDims)) {
                                    expShape = new long[]{ 5 };
                                } else
                                    if (Arrays.equals(new int[]{ 0, 2 }, reduceDims)) {
                                        expShape = new long[]{ 4 };
                                    } else
                                        if (Arrays.equals(new int[]{ 1, 2 }, reduceDims)) {
                                            expShape = new long[]{ 3 };
                                        } else
                                            if (Arrays.equals(new int[]{ 0, 1, 2 }, reduceDims)) {
                                                expShape = new long[]{  };
                                            } else {
                                                throw new RuntimeException();
                                            }







                String msg = (name + " - dims=") + (Arrays.toString(reduceDims));
                INDArray out = sd.execAndEndResult();
                log.info(((((((msg + " - expected shape: ") + (Arrays.toString(expShape))) + ", out=") + (Arrays.toString(out.shape()))) + ", outExp=") + (Arrays.toString(expOut.shape()))));
                BaseNd4jTest.assertArrayEquals(msg, expShape, out.shape());
                BaseNd4jTest.assertArrayEquals(msg, expShape, expOut.shape());
                Assert.assertEquals(msg, expOut, out);
            }
        }
    }

    @Test
    public void testReductionsBackwards() {
        for (int i = 0; i < 7; i++) {
            SameDiff sd = SameDiff.create();
            int nOut = 4;
            int minibatch = 3;
            SDVariable input = sd.var("in", new long[]{ -1, nOut });
            SDVariable label = sd.var("label", new long[]{ -1, nOut });
            SDVariable diff = input.sub(label);
            SDVariable sqDiff = diff.mul(diff);
            SDVariable msePerEx = sd.mean("msePerEx", sqDiff, 1);
            SDVariable loss;// Scalar value

            String name;
            switch (i) {
                case 0 :
                    loss = sd.mean("loss", msePerEx, 0);
                    name = "mean";
                    break;
                case 1 :
                    loss = sd.sum("loss", msePerEx, 0);
                    name = "sum";
                    break;
                case 2 :
                    loss = sd.standardDeviation("loss", msePerEx, true, 0);
                    name = "stdev";
                    break;
                case 3 :
                    loss = sd.min("loss", msePerEx, 0);
                    name = "min";
                    break;
                case 4 :
                    loss = sd.max("loss", msePerEx, 0);
                    name = "max";
                    break;
                case 5 :
                    loss = sd.variance("loss", msePerEx, true, 0);
                    name = "variance";
                    break;
                case 6 :
                    loss = sd.prod("loss", msePerEx, 0);
                    name = "prod";
                    break;
                default :
                    throw new RuntimeException();
            }
            String msg = (("test: " + i) + " - ") + name;
            log.info(("*** Starting test: " + msg));
            INDArray inputArr = Nd4j.rand(minibatch, nOut);
            INDArray labelArr = Nd4j.rand(minibatch, nOut);
            sd.associateArrayWithVariable(inputArr, input);
            sd.associateArrayWithVariable(labelArr, label);
            INDArray result = sd.execAndEndResult();
            Assert.assertEquals(1, result.length());
            sd.execBackwards(Collections.emptyMap());
        }
    }
}

