package org.nd4j.linalg.rng;


import Builder.Default;
import DataType.BOOL;
import DataType.INT;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.OpValidationSuite;
import org.nd4j.base.Preconditions;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.CustomOp;
import org.nd4j.linalg.api.ops.Op;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.conditions.Conditions;


@Slf4j
public class RngValidationTests {
    @Builder(builderClassName = "TestCaseBuilder")
    @Data
    public static class TestCase {
        private String opType;

        private DataType dataType;

        @Builder.Default
        private long rngSeed = 12345;

        private long[] shape;

        private double minValue;

        private double maxValue;

        private boolean minValueInclusive;

        private boolean maxValueInclusive;

        private Double expectedMean;

        private Double expectedStd;

        @Builder.Default
        private double meanRelativeErrorTolerance = 0.01;

        @Builder.Default
        private double stdRelativeErrorTolerance = 0.01;

        private Double meanMinAbsErrorTolerance;// Consider relative error between 0 and 0.001: relative error is 1.0, but absolute error is small


        private Double stdMinAbsErrorTolerance;

        @Builder.Default
        private Map<String, Object> args = new LinkedHashMap<>();

        public static class TestCaseBuilder {
            public RngValidationTests.TestCase.TestCaseBuilder arg(String arg, Object value) {
                if ((RngValidationTests.TestCase.this.args) == null) {
                    args(new LinkedHashMap());
                }
                RngValidationTests.TestCase.this.args.put(arg, value);
                return this;
            }

            public RngValidationTests.TestCase.TestCaseBuilder shape(long... shape) {
                this.shape = shape;
                return this;
            }
        }

        public INDArray arr() {
            Preconditions.checkState(((shape) != null), "Shape is null");
            INDArray arr = Nd4j.createUninitialized(dataType, shape);
            arr.assign(Double.NaN);// Assign NaNs to help detect implementation issues

            return arr;
        }

        public <T> T prop(String s) {
            Preconditions.checkState((((args) != null) && (args.containsKey(s))), "Property \"%s\" not found. All properties: %s", s, args);
            return ((T) (args.get(s)));
        }
    }

    @Test
    public void validateRngDistributions() {
        OpValidationSuite.ignoreFailing();// https://github.com/deeplearning4j/deeplearning4j/issues/6958 - 2018-01-09

        List<RngValidationTests.TestCase> testCases = new ArrayList<>();
        for (DataType type : new DataType[]{ DataType.DOUBLE, DataType.FLOAT, DataType.HALF }) {
            // Legacy (non-custom) RNG ops:
            testCases.add(builder().opType("bernoulli").dataType(type).shape(new long[0]).minValue(0).maxValue(1).minValueInclusive(true).maxValueInclusive(true).arg("prob", 0.5).build());// Don't check mean/std for 1 element

            testCases.add(/* var = p*(1-p) */
            builder().opType("bernoulli").dataType(type).shape(1000).minValue(0).maxValue(1).minValueInclusive(true).maxValueInclusive(true).arg("prob", 0.5).expectedMean(0.5).expectedStd(Math.sqrt((0.5 * 0.5))).build());
            testCases.add(/* var = p*(1-p) */
            builder().opType("bernoulli").dataType(type).shape(100, 10000).minValue(0).maxValue(1).minValueInclusive(true).maxValueInclusive(true).arg("prob", 0.2).expectedMean(0.2).expectedStd(Math.sqrt((0.2 * (1 - 0.2)))).meanRelativeErrorTolerance(0.005).stdRelativeErrorTolerance(0.01).build());
            testCases.add(builder().opType("uniform").dataType(type).shape(new long[0]).minValue(0).maxValue(1).minValueInclusive(true).maxValueInclusive(true).arg("min", 0.0).arg("max", 1.0).build());// Don't check mean/std for 1 element

            testCases.add(/* Var: 1/12 * (b-a)^2 */
            builder().opType("uniform").dataType(type).shape(1000).minValue(1).maxValue(2).minValueInclusive(true).maxValueInclusive(true).arg("min", 1.0).arg("max", 2.0).expectedMean(((1 + 2) / 2.0)).expectedStd(Math.sqrt(((1 / 12.0) * (Math.pow((2.0 - 1.0), 2))))).build());
            testCases.add(/* Var: 1/12 * (b-a)^2 */
            builder().opType("uniform").dataType(type).shape(100, 10000).minValue((-4)).maxValue((-2)).minValueInclusive(true).maxValueInclusive(true).arg("min", (-4.0)).arg("max", (-2.0)).expectedMean((-3.0)).expectedStd(Math.sqrt(((1 / 12.0) * (Math.pow(((-4.0) + 2.0), 2))))).meanRelativeErrorTolerance(0.005).stdRelativeErrorTolerance(0.01).build());
            testCases.add(builder().opType("gaussian").dataType(type).shape(new long[0]).minValue(RngValidationTests.minValue(type)).maxValue(RngValidationTests.maxValue(type)).minValueInclusive(true).maxValueInclusive(true).arg("mean", 0.0).arg("std", 1.0).build());// Don't check mean/std for 1 element

            testCases.add(builder().opType("gaussian").dataType(type).shape(1000).minValue(RngValidationTests.minValue(type)).maxValue(RngValidationTests.maxValue(type)).minValueInclusive(true).maxValueInclusive(true).arg("mean", 0.0).arg("std", 1.0).expectedMean(0.0).expectedStd(1.0).stdRelativeErrorTolerance(0.03).meanMinAbsErrorTolerance(0.1).stdMinAbsErrorTolerance(0.1).build());
            testCases.add(builder().opType("gaussian").dataType(type).shape(100, 1000).minValue(RngValidationTests.minValue(type)).maxValue(RngValidationTests.maxValue(type)).minValueInclusive(true).maxValueInclusive(true).arg("mean", 2.0).arg("std", 0.5).expectedMean(2.0).expectedStd(0.5).meanRelativeErrorTolerance(0.01).stdRelativeErrorTolerance(0.01).meanMinAbsErrorTolerance(0.001).build());
            testCases.add(builder().opType("binomial").dataType(type).shape(new long[0]).minValue(0).maxValue(5).minValueInclusive(true).maxValueInclusive(true).arg("n", 5).arg("p", 0.5).build());// Don't check mean/std for 1 element

            testCases.add(/* var = np(1-p) */
            builder().opType("binomial").dataType(type).shape(1000).minValue(0).maxValue(10).minValueInclusive(true).maxValueInclusive(true).arg("n", 10).arg("p", 0.5).stdRelativeErrorTolerance(0.02).expectedMean((10 * 0.5)).expectedStd(Math.sqrt(((10 * 0.5) * (1 - 0.5)))).build());
            testCases.add(/* var = np(1-p) */
            builder().opType("binomial").dataType(type).shape(100, 10000).minValue(0).maxValue(20).minValueInclusive(true).maxValueInclusive(true).arg("n", 20).arg("p", 0.2).expectedMean((20 * 0.2)).expectedStd(Math.sqrt(((20 * 0.2) * (1 - 0.2)))).meanRelativeErrorTolerance(0.001).stdRelativeErrorTolerance(0.01).build());
            // truncated normal clips at (mean-2*std, mean+2*std). Mean for equal 2 sided clipping about mean is same as original mean. Variance is difficult to calculate...
            // Assume variance is similar to non-truncated normal (should be a bit less in practice) but use large relative error here
            testCases.add(builder().opType("truncated_normal").dataType(type).shape(new long[0]).minValue((-2.0)).maxValue(2.0).minValueInclusive(true).maxValueInclusive(true).arg("mean", 0.0).arg("std", 1.0).build());// Don't check mean/std for 1 element

            testCases.add(builder().opType("truncated_normal").dataType(type).shape(1000).minValue((-2.0)).maxValue(2.0).minValueInclusive(true).maxValueInclusive(true).arg("mean", 0.0).arg("std", 1.0).expectedMean(0.0).expectedStd(1.0).stdRelativeErrorTolerance(0.2).meanMinAbsErrorTolerance(0.1).build());
            testCases.add(builder().opType("truncated_normal").dataType(type).shape(100, 10000).minValue(1.0).maxValue(3.0).minValueInclusive(true).maxValueInclusive(true).arg("mean", 2.0).arg("std", 0.5).expectedMean(2.0).expectedStd(0.5).meanRelativeErrorTolerance(0.001).stdRelativeErrorTolerance(0.2).meanMinAbsErrorTolerance(0.001).build());
            // Dropout (non-inverted): same as bernoulli distribution, when dropout applied to "ones" array
            testCases.add(builder().opType("dropout").dataType(type).shape(new long[0]).minValue(0).maxValue(1).minValueInclusive(true).maxValueInclusive(true).arg("p", 0.5).build());// Don't check mean/std for 1 element

            testCases.add(/* var = p*(1-p) */
            builder().opType("dropout").dataType(type).shape(1000).minValue(0).maxValue(1).minValueInclusive(true).maxValueInclusive(true).arg("p", 0.4).expectedMean(0.4).expectedStd(Math.sqrt((0.4 * (1 - 0.4)))).meanMinAbsErrorTolerance(0.05).stdMinAbsErrorTolerance(0.05).build());
            testCases.add(/* var = p*(1-p) */
            builder().opType("dropout").dataType(type).shape(100, 10000).minValue(0).maxValue(1).minValueInclusive(true).maxValueInclusive(true).arg("p", 0.3).expectedMean(0.3).expectedStd(Math.sqrt((0.3 * (1 - 0.3)))).meanRelativeErrorTolerance(0.005).stdRelativeErrorTolerance(0.01).build());
            // Dropout (inverted): basically bernoulli distribution * 2, when inverted dropout applied to "ones" array
            testCases.add(builder().opType("dropout_inverted").dataType(type).shape(new long[0]).minValue(0).maxValue(1).minValueInclusive(true).maxValueInclusive(true).arg("p", 0.5).build());// Don't check mean/std for 1 element

            testCases.add(/* var = p*(1-p) */
            // Mean: 0.4 probability of  being retained - mean is 0.4 probability * (1.0/0.4) = 1.0. i.e., expected mean is unchanged by inverted dropout
            builder().opType("dropout_inverted").dataType(type).shape(1000).minValue(0).maxValue((1.0 / 0.4)).minValueInclusive(true).maxValueInclusive(true).arg("p", 0.4).expectedMean(1.0).expectedStd(((1 / 0.4) * (Math.sqrt((0.4 * (1 - 0.4)))))).meanMinAbsErrorTolerance(0.05).stdMinAbsErrorTolerance(0.05).build());
            testCases.add(/* var = p*(1-p); note var(aX) = a^2 var(X) */
            builder().opType("dropout_inverted").dataType(type).shape(100, 10000).minValue(0).maxValue((1.0 / 0.3)).minValueInclusive(true).maxValueInclusive(true).arg("p", 0.3).expectedMean(1.0).expectedStd(((1 / 0.3) * (Math.sqrt((0.3 * (1 - 0.3)))))).meanRelativeErrorTolerance(0.005).stdRelativeErrorTolerance(0.01).build());
            // Linspace: we'll treat is as basically a uniform distribution for the purposes of these tests...
            testCases.add(/* Var: 1/12 * (b-a)^2 */
            builder().opType("linspace").dataType(type).shape(1000).minValue(1).maxValue(2).minValueInclusive(true).maxValueInclusive(true).arg("from", 1.0).arg("to", 2.0).expectedMean(1.5).expectedStd(Math.sqrt(((1 / 12.0) * (Math.pow((2.0 - 1.0), 2))))).build());
            // Log normal distribution: parameterized such that if X~lognormal(m,s) then mean(log(X))=m and std(log(X))=s
            // mean is given by exp(mu+s^2/2), variance [exp(s^2)-1]*[exp(2*mu+s^2)]
            testCases.add(builder().opType("lognormal").dataType(type).shape(new long[0]).minValue(0).maxValue(RngValidationTests.maxValue(type)).minValueInclusive(true).maxValueInclusive(true).arg("mu", 0.0).arg("s", 1.0).build());// Don't check mean/std for 1 element

            testCases.add(builder().opType("lognormal").dataType(type).shape(1000).minValue(0).maxValue(RngValidationTests.maxValue(type)).minValueInclusive(true).maxValueInclusive(true).arg("mu", 0.0).arg("s", 1.0).expectedMean(Math.exp((0.0 + (1.0 / 2.0)))).expectedStd(Math.sqrt((((Math.exp(1.0)) - 1) * (Math.exp(1.0))))).meanRelativeErrorTolerance(0.1).stdRelativeErrorTolerance(0.1).meanMinAbsErrorTolerance(0.1).stdMinAbsErrorTolerance(0.1).build());
            testCases.add(builder().opType("lognormal").dataType(type).shape(100, 10000).minValue(0).maxValue(RngValidationTests.maxValue(type)).minValueInclusive(true).maxValueInclusive(true).arg("mu", 2.0).arg("s", 0.5).expectedMean(Math.exp((2.0 + ((0.5 * 0.5) / 2.0)))).expectedStd(Math.sqrt((((Math.exp((0.5 * 0.5))) - 1) * (Math.exp(((2.0 * 2.0) + (0.5 * 0.5))))))).meanRelativeErrorTolerance(0.01).stdRelativeErrorTolerance(0.01).meanMinAbsErrorTolerance(0.001).build());
            // Choice op. For the purposes of this test, use discrete uniform distribution with values 0 to 10 inclusive
            testCases.add(builder().opType("choice").dataType(type).shape(new long[0]).minValue(0).maxValue(10).minValueInclusive(true).maxValueInclusive(true).build());// Don't check mean/std for 1 element

            testCases.add(/* variance = ((b-a+1)^2-1)/12 */
            /* (a+b)/2 */
            builder().opType("choice").dataType(type).shape(1000).minValue(0).maxValue(10).minValueInclusive(true).maxValueInclusive(true).expectedMean(5.0).expectedStd(Math.sqrt((((Math.pow(((10 - 0) + 1), 2)) - 1) / 12.0))).meanRelativeErrorTolerance(0.05).stdRelativeErrorTolerance(0.05).meanMinAbsErrorTolerance(0.05).stdMinAbsErrorTolerance(0.05).build());
            testCases.add(/* variance = ((b-a+1)^2-1)/12 */
            /* (a+b)/2 */
            builder().opType("choice").dataType(type).shape(100, 10000).minValue(0).maxValue(10).minValueInclusive(true).maxValueInclusive(true).expectedMean(5.0).expectedStd(Math.sqrt((((Math.pow(((10 - 0) + 1), 2)) - 1) / 12.0))).meanRelativeErrorTolerance(0.01).stdRelativeErrorTolerance(0.01).meanMinAbsErrorTolerance(0.001).build());
            // Probabilistic merge: use 0 and 1, 0.5 probability. Then it's same as bernoulli distribution
            testCases.add(builder().opType("probabilisticmerge").dataType(type).shape(new long[0]).minValue(0).maxValue(1).minValueInclusive(true).maxValueInclusive(true).arg("prob", 0.5).build());// Don't check mean/std for 1 element

            testCases.add(/* var = p*(1-p) */
            builder().opType("probabilisticmerge").dataType(type).shape(1000).minValue(0).maxValue(1).minValueInclusive(true).maxValueInclusive(true).arg("prob", 0.5).expectedMean(0.5).expectedStd(Math.sqrt((0.5 * 0.5))).build());
            testCases.add(/* var = p*(1-p) */
            builder().opType("probabilisticmerge").dataType(type).shape(100, 10000).minValue(0).maxValue(1).minValueInclusive(true).maxValueInclusive(true).arg("prob", 0.2).expectedMean(0.2).expectedStd(Math.sqrt((0.2 * (1 - 0.2)))).meanRelativeErrorTolerance(0.005).stdRelativeErrorTolerance(0.01).build());
            // Range: x to y in N steps - essentially same statistical properties as uniform distribution
            testCases.add(builder().opType("range").dataType(type).shape(10).minValue(0).maxValue(1).minValueInclusive(true).maxValueInclusive(true).arg("min", 0.0).arg("max", 1.0).build());// Don't check mean/std for 1 element

            testCases.add(/* Var: 1/12 * (b-a)^2 */
            builder().opType("range").dataType(type).shape(1000).minValue(1).maxValue(2).minValueInclusive(true).maxValueInclusive(true).arg("min", 1.0).arg("max", 2.0).expectedMean(((1 + 2) / 2.0)).expectedStd(Math.sqrt(((1 / 12.0) * (Math.pow((2.0 - 1.0), 2))))).build());
            // AlphaDropout: implements a * (x * d + alphaPrime * (1-d)) + b, where d ~ Bernoulli(p), i.e., d \in {0,1}.
            // For ones input and p=0.5, this should give us values (a+b or a*alphaPrime+b) with probability 0.5
            // Mean should be same as input - i.e., 1
            testCases.add(builder().opType("alphaDropout").dataType(type).shape(new long[0]).maxValue(((RngValidationTests.alphaDropoutA(0.5)) + (RngValidationTests.alphaDropoutB(0.5)))).minValue((((RngValidationTests.alphaDropoutA(0.5)) * (RngValidationTests.ALPHA_PRIME)) + (RngValidationTests.alphaDropoutB(0.5)))).minValueInclusive(true).maxValueInclusive(true).arg("p", 0.5).build());// Don't check mean/std for 1 element

            testCases.add(// Mean: 0.4 probability of  being retained - mean is 0.4 probability * (1.0/0.4) = 1.0. i.e., expected mean is unchanged by inverted dropout
            builder().opType("alphaDropout").dataType(type).shape(1000).maxValue(((RngValidationTests.alphaDropoutA(0.4)) + (RngValidationTests.alphaDropoutB(0.4)))).minValue((((RngValidationTests.alphaDropoutA(0.4)) * (RngValidationTests.ALPHA_PRIME)) + (RngValidationTests.alphaDropoutB(0.4)))).minValueInclusive(true).maxValueInclusive(true).arg("p", 0.4).expectedMean(1.0).build());
            testCases.add(builder().opType("alphaDropout").dataType(type).shape(100, 10000).maxValue(((RngValidationTests.alphaDropoutA(0.3)) + (RngValidationTests.alphaDropoutB(0.3)))).minValue((((RngValidationTests.alphaDropoutA(0.3)) * (RngValidationTests.ALPHA_PRIME)) + (RngValidationTests.alphaDropoutB(0.3)))).minValueInclusive(true).maxValueInclusive(true).arg("p", 0.3).expectedMean(1.0).meanRelativeErrorTolerance(0.005).stdRelativeErrorTolerance(0.01).build());
            // --- Custom ops ---
            // DistributionUniform, RandomBernoulli, RandomExponential, RandomNormal, RandomStandardNormal
            testCases.add(builder().opType("distributionuniform").dataType(type).shape(new long[0]).minValue(0).maxValue(1).minValueInclusive(true).maxValueInclusive(true).arg("min", 0.0).arg("max", 1.0).build());// Don't check mean/std for 1 element

            testCases.add(/* Var: 1/12 * (b-a)^2 */
            builder().opType("distributionuniform").dataType(type).shape(1000).minValue(1).maxValue(2).minValueInclusive(true).maxValueInclusive(true).arg("min", 1.0).arg("max", 2.0).expectedMean(((1 + 2) / 2.0)).expectedStd(Math.sqrt(((1 / 12.0) * (Math.pow((2.0 - 1.0), 2))))).build());
            testCases.add(/* Var: 1/12 * (b-a)^2 */
            builder().opType("distributionuniform").dataType(type).shape(100, 10000).minValue((-4)).maxValue((-2)).minValueInclusive(true).maxValueInclusive(true).arg("min", (-4.0)).arg("max", (-2.0)).expectedMean((-3.0)).expectedStd(Math.sqrt(((1 / 12.0) * (Math.pow(((-4.0) + 2.0), 2))))).meanRelativeErrorTolerance(0.005).stdRelativeErrorTolerance(0.01).build());
            testCases.add(builder().opType("randombernoulli").dataType(type).shape(new long[0]).minValue(0).maxValue(1).minValueInclusive(true).maxValueInclusive(true).arg("prob", 0.5).build());// Don't check mean/std for 1 element

            testCases.add(/* var = p*(1-p) */
            builder().opType("randombernoulli").dataType(type).shape(1000).minValue(0).maxValue(1).minValueInclusive(true).maxValueInclusive(true).arg("prob", 0.5).expectedMean(0.5).expectedStd(Math.sqrt((0.5 * 0.5))).build());
            testCases.add(/* var = p*(1-p) */
            builder().opType("randombernoulli").dataType(type).shape(100, 10000).minValue(0).maxValue(1).minValueInclusive(true).maxValueInclusive(true).arg("prob", 0.2).expectedMean(0.2).expectedStd(Math.sqrt((0.2 * (1 - 0.2)))).meanRelativeErrorTolerance(0.005).stdRelativeErrorTolerance(0.01).build());
            // 3 cases: lambda = 1, 1, 0.4
            testCases.add(builder().opType("randomexponential").dataType(type).shape(new long[0]).minValue(0).maxValue(RngValidationTests.maxValue(type)).minValueInclusive(false).maxValueInclusive(true).arg("lambda", 1.0).build());// Don't check mean/std for 1 element

            testCases.add(/* var = 1 / lambda^2 */
            builder().opType("randomexponential").dataType(type).shape(1000).minValue(0.0).maxValue(RngValidationTests.maxValue(type)).minValueInclusive(false).maxValueInclusive(true).arg("lambda", 1.0).expectedMean(1.0).expectedStd(1.0).build());
            testCases.add(/* var = 1 / lambda^2 */
            builder().opType("randomexponential").dataType(type).shape(100, 10000).minValue(0.0).maxValue(RngValidationTests.maxValue(type)).minValueInclusive(false).maxValueInclusive(true).arg("lambda", 0.4).expectedMean((1.0 / 0.4)).expectedStd((1.0 / (Math.pow(0.4, 2)))).meanRelativeErrorTolerance(0.005).stdRelativeErrorTolerance(0.01).build());
            testCases.add(builder().opType("randomnormal").dataType(type).shape(new long[0]).minValue(RngValidationTests.minValue(type)).maxValue(RngValidationTests.maxValue(type)).minValueInclusive(true).maxValueInclusive(true).arg("mean", 0.0).arg("std", 1.0).build());// Don't check mean/std for 1 element

            testCases.add(builder().opType("randomnormal").dataType(type).shape(1000).minValue(RngValidationTests.minValue(type)).maxValue(RngValidationTests.maxValue(type)).minValueInclusive(true).maxValueInclusive(true).arg("mean", 0.0).arg("std", 1.0).expectedMean(0.0).expectedStd(1.0).meanMinAbsErrorTolerance(0.05).stdMinAbsErrorTolerance(0.05).build());
            testCases.add(builder().opType("randomnormal").dataType(type).shape(100, 1000).minValue(RngValidationTests.minValue(type)).maxValue(RngValidationTests.maxValue(type)).minValueInclusive(true).maxValueInclusive(true).arg("mean", 2.0).arg("std", 0.5).expectedMean(2.0).expectedStd(0.5).meanRelativeErrorTolerance(0.01).stdRelativeErrorTolerance(0.01).meanMinAbsErrorTolerance(0.001).build());
            testCases.add(builder().opType("randomstandardnormal").dataType(type).shape(new long[0]).minValue(RngValidationTests.minValue(type)).maxValue(RngValidationTests.maxValue(type)).minValueInclusive(true).maxValueInclusive(true).build());// Don't check mean/std for 1 element

            testCases.add(builder().opType("randomstandardnormal").dataType(type).shape(1000).minValue(RngValidationTests.minValue(type)).maxValue(RngValidationTests.maxValue(type)).minValueInclusive(true).maxValueInclusive(true).expectedMean(0.0).expectedStd(1.0).meanMinAbsErrorTolerance(0.05).stdMinAbsErrorTolerance(0.05).build());
            testCases.add(builder().opType("randomstandardnormal").dataType(type).shape(100, 1000).minValue(RngValidationTests.minValue(type)).maxValue(RngValidationTests.maxValue(type)).minValueInclusive(true).maxValueInclusive(true).expectedMean(0.0).expectedStd(1.0).meanRelativeErrorTolerance(0.01).stdRelativeErrorTolerance(0.01).meanMinAbsErrorTolerance(0.001).build());
        }
        int count = 1;
        for (RngValidationTests.TestCase tc : testCases) {
            log.info("Starting test case: {} of {}", count, testCases.size());
            log.info("{}", tc);
            Object op = RngValidationTests.getOp(tc);
            INDArray z = null;
            Nd4j.getRandom().setSeed(getRngSeed());
            if (op instanceof Op) {
                Op o = ((Op) (op));
                Nd4j.getExecutioner().exec(o);
                z = o.z();
            } else {
                CustomOp o = ((CustomOp) (op));
                Nd4j.getExecutioner().exec(o);
                z = o.getOutputArgument(0);
            }
            // Check for NaNs, Infs, etc
            int countNaN = Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.impl.transforms.bool.MatchConditionTransform(z, Nd4j.create(BOOL, z.shape()), Conditions.isNan())).castTo(INT).sumNumber().intValue();
            int countInf = Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.impl.transforms.bool.MatchConditionTransform(z, Nd4j.create(BOOL, z.shape()), Conditions.isInfinite())).castTo(INT).sumNumber().intValue();
            Assert.assertEquals("NaN - expected 0 values", 0, countNaN);
            Assert.assertEquals("Infinite - expected 0 values", 0, countInf);
            // Check min/max values
            double min = z.minNumber().doubleValue();
            if (((isMinValueInclusive()) && (min < (getMinValue()))) || ((!(isMinValueInclusive())) && (min <= (getMinValue())))) {
                Assert.fail(((((((("Minimum value (" + min) + ") is less than allowed minimum value (") + (getMinValue())) + ", inclusive=") + (isMinValueInclusive())) + "): test case: ") + tc));
            }
            double max = z.maxNumber().doubleValue();
            if (((isMaxValueInclusive()) && (max > (getMaxValue()))) || ((!(isMaxValueInclusive())) && (max >= (getMaxValue())))) {
                Assert.fail(((((((("Maximum value (" + max) + ") is greater than allowed maximum value (") + (getMaxValue())) + ", inclusive=") + (isMaxValueInclusive())) + "): test case: ") + tc));
            }
            // Check RNG seed repeatability
            Object op2 = RngValidationTests.getOp(tc);
            Nd4j.getRandom().setSeed(getRngSeed());
            INDArray z2;
            if (op2 instanceof Op) {
                Op o = ((Op) (op2));
                Nd4j.getExecutioner().exec(o);
                z2 = o.z();
            } else {
                CustomOp o = ((CustomOp) (op2));
                Nd4j.getExecutioner().exec(o);
                z2 = o.getOutputArgument(0);
            }
            Assert.assertEquals(z, z2);
            // Check mean, stdev
            if ((getExpectedMean()) != null) {
                double mean = z.meanNumber().doubleValue();
                double re = RngValidationTests.relError(getExpectedMean(), mean);
                double ae = Math.abs(((getExpectedMean()) - mean));
                if ((re > (getMeanRelativeErrorTolerance())) && (((getMeanMinAbsErrorTolerance()) == null) || (ae > (getMeanMinAbsErrorTolerance())))) {
                    Assert.fail(((((((((("Relative error for mean (" + re) + ") exceeds maximum (") + (getMeanRelativeErrorTolerance())) + ") - expected mean = ") + (getExpectedMean())) + " vs. observed mean = ") + mean) + " - test: ") + tc));
                }
            }
            if ((getExpectedStd()) != null) {
                double std = z.std(true).getDouble(0);
                double re = RngValidationTests.relError(getExpectedStd(), std);
                double ae = Math.abs(((getExpectedStd()) - std));
                if ((re > (getStdRelativeErrorTolerance())) && (((getStdMinAbsErrorTolerance()) == null) || (ae > (getStdMinAbsErrorTolerance())))) {
                    /* //Histogram for debugging
                    INDArray range = Nd4j.create(new double[]{z.minNumber().doubleValue(), z.maxNumber().doubleValue()}).castTo(tc.getDataType());
                    INDArray n = Nd4j.scalar(DataType.INT,100);
                    INDArray out = Nd4j.create(DataType.INT, 100);
                    DynamicCustomOp histogram = DynamicCustomOp.builder("histogram_fixed_width")
                    .addInputs(z, range, n)
                    .addOutputs(out)
                    .build();
                    Nd4j.getExecutioner().exec(histogram);
                    System.out.println(range);
                    System.out.println(out.toString().replaceAll("\\s", ""));
                     */
                    Assert.fail(((((((((("Relative error for stdev (" + re) + ") exceeds maximum (") + (getStdRelativeErrorTolerance())) + ") - expected stdev = ") + (getExpectedStd())) + " vs. observed stdev = ") + std) + " - test: ") + tc));
                }
            }
            count++;
        }
    }

    public static final double DEFAULT_ALPHA = 1.6732632423543772;

    public static final double DEFAULT_LAMBDA = 1.0507009873554805;

    public static final double ALPHA_PRIME = (-(RngValidationTests.DEFAULT_LAMBDA)) * (RngValidationTests.DEFAULT_ALPHA);
}

