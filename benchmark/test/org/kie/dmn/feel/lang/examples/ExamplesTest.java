/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.dmn.feel.lang.examples;


import java.math.BigDecimal;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.kie.dmn.feel.FEEL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ExamplesTest extends ExamplesBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(ExamplesTest.class);

    private static Map context;

    private static FEEL feel;

    @Test
    public void testLoadApplicantContext() {
        String expression = ExamplesBaseTest.loadExpression("applicant.feel");
        Map applicant = ((Map) (ExamplesTest.feel.evaluate(expression)));
        System.out.println(printContext(applicant));
        Assert.assertThat(applicant.size(), CoreMatchers.is(5));
    }

    @Test
    public void testLoadExample_10_6_1() {
        System.out.println(printContext(ExamplesTest.context));
        Assert.assertThat(ExamplesTest.context.size(), CoreMatchers.is(6));
    }

    @Test
    public void testLoadExample_10_6_2() {
        Number yearlyIncome = ((Number) (ExamplesTest.feel.evaluate("monthly income * 12", ExamplesTest.context)));
        System.out.println(("Yearly income = " + yearlyIncome));
        Assert.assertThat(yearlyIncome, CoreMatchers.is(new BigDecimal("120000.00")));
    }

    @Test
    public void testLoadExample_10_6_3() {
        String expression = ExamplesBaseTest.loadExpression("example_10_6_3.feel");
        String maritalStatus = ((String) (ExamplesTest.feel.evaluate(expression, ExamplesTest.context)));
        System.out.println(("Marital status = " + maritalStatus));
        Assert.assertThat(maritalStatus, CoreMatchers.is("valid"));
    }

    @Test
    public void testLoadExample_10_6_4() {
        Number totalExpenses = ((Number) (ExamplesTest.feel.evaluate("sum( monthly outgoings )", ExamplesTest.context)));
        System.out.println(("Monthly total expenses = " + totalExpenses));
        Assert.assertThat(totalExpenses, CoreMatchers.is(new BigDecimal("5500.00")));
    }

    @Test
    public void testLoadExample_10_6_5() {
        String expression = ExamplesBaseTest.loadExpression("example_10_6_5.feel");
        Number pmt = ((Number) (ExamplesTest.feel.evaluate(expression, ExamplesTest.context)));
        System.out.println(("PMT = " + pmt));
        Assert.assertThat(pmt, CoreMatchers.is(new BigDecimal("3975.982590125552338278440100112431")));
    }

    @Test
    public void testLoadExample_10_6_6() {
        String expression = ExamplesBaseTest.loadExpression("example_10_6_6.feel");
        Number total = ((Number) (ExamplesTest.feel.evaluate(expression, ExamplesTest.context)));
        System.out.println(("Weight = " + total));
        Assert.assertThat(total, CoreMatchers.is(new BigDecimal("150")));
    }

    @Test
    public void testLoadExample_10_6_7() {
        String expression = ExamplesBaseTest.loadExpression("example_10_6_7.feel");
        Boolean bankrupcy = ((Boolean) (ExamplesTest.feel.evaluate(expression, ExamplesTest.context)));
        System.out.println(("Is there bankrupcy event? " + bankrupcy));
        Assert.assertThat(bankrupcy, CoreMatchers.is(Boolean.FALSE));
    }

    @Test
    public void testJavaCall() {
        String expression = ExamplesBaseTest.loadExpression("javacall.feel");
        Map context = ((Map) (ExamplesTest.feel.evaluate(expression)));
        System.out.println(printContext(context));
    }

    @Test
    public void testAdhocExpression() {
        String expression = ExamplesBaseTest.loadExpression("custom.feel");
        Object result = ExamplesTest.feel.evaluate(expression);
        if (result instanceof Map) {
            System.out.println(printContext(((Map) (result))));
        } else {
            System.out.println(("Result: " + result));
        }
    }
}

