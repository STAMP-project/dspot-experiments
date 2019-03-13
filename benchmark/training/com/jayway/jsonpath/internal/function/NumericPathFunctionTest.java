package com.jayway.jsonpath.internal.function;


import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Configurations;
import com.jayway.jsonpath.JsonPathException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Defines functional tests around executing:
 *
 * - sum
 * - avg
 * - stddev
 *
 * for each of the above, executes the test and verifies that the results are as expected based on a static input
 * and static output.
 *
 * Created by mattg on 6/26/15.
 */
/**
 * Expect that for an invalid function name we'll get back the original input to the function
 */
// @Test
// @Ignore
// public void testInvalidFunctionNameNegative() {
// JSONArray numberSeries = new JSONArray();
// numberSeries.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
// assertThat(using(conf).parse(NUMBER_SERIES).read("$.numbers.foo()")).isEqualTo(numberSeries);
// }
@RunWith(Parameterized.class)
public class NumericPathFunctionTest extends BaseFunctionTest {
    private static final Logger logger = LoggerFactory.getLogger(NumericPathFunctionTest.class);

    private Configuration conf = Configurations.GSON_CONFIGURATION;

    public NumericPathFunctionTest(Configuration conf) {
        NumericPathFunctionTest.logger.debug("Testing with configuration {}", conf.getClass().getName());
        this.conf = conf;
    }

    @Test
    public void testAverageOfDoubles() {
        verifyMathFunction(conf, "$.numbers.avg()", 5.5);
    }

    @Test
    public void testAverageOfEmptyListNegative() {
        try {
            verifyMathFunction(conf, "$.empty.avg()", null);
        } catch (JsonPathException e) {
            Assert.assertEquals(e.getMessage(), "Aggregation function attempted to calculate value using empty array");
        }
    }

    @Test
    public void testSumOfDouble() {
        verifyMathFunction(conf, "$.numbers.sum()", ((10.0 * (10.0 + 1.0)) / 2.0));
    }

    @Test
    public void testSumOfEmptyListNegative() {
        try {
            verifyMathFunction(conf, "$.empty.sum()", null);
        } catch (JsonPathException e) {
            Assert.assertEquals(e.getMessage(), "Aggregation function attempted to calculate value using empty array");
        }
    }

    @Test
    public void testMaxOfDouble() {
        verifyMathFunction(conf, "$.numbers.max()", 10.0);
    }

    @Test
    public void testMaxOfEmptyListNegative() {
        try {
            verifyMathFunction(conf, "$.empty.max()", null);
        } catch (JsonPathException e) {
            Assert.assertEquals(e.getMessage(), "Aggregation function attempted to calculate value using empty array");
        }
    }

    @Test
    public void testMinOfDouble() {
        verifyMathFunction(conf, "$.numbers.min()", 1.0);
    }

    @Test
    public void testMinOfEmptyListNegative() {
        try {
            verifyMathFunction(conf, "$.empty.min()", null);
        } catch (JsonPathException e) {
            Assert.assertEquals(e.getMessage(), "Aggregation function attempted to calculate value using empty array");
        }
    }

    @Test
    public void testStdDevOfDouble() {
        verifyMathFunction(conf, "$.numbers.stddev()", 2.8722813232690143);
    }

    @Test
    public void testStddevOfEmptyListNegative() {
        try {
            verifyMathFunction(conf, "$.empty.stddev()", null);
        } catch (JsonPathException e) {
            Assert.assertEquals(e.getMessage(), "Aggregation function attempted to calculate value using empty array");
        }
    }
}

