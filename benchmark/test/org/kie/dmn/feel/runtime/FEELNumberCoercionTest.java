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
package org.kie.dmn.feel.runtime;


import InfixOperator.GT;
import InfixOperator.GTE;
import InfixOperator.LT;
import InfixOperator.LTE;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.util.EvalHelper;


public class FEELNumberCoercionTest {
    private final FEEL feel = FEEL.newInstance();

    @Test
    public void test() {
        Assert.assertThat(evaluateInfix(1, LT, 2.0), CoreMatchers.is(true));
        Assert.assertThat(evaluateInfix(2.0, LT, 1), CoreMatchers.is(false));
        Assert.assertThat(evaluateInfix(1, LTE, 2.0), CoreMatchers.is(true));
        Assert.assertThat(evaluateInfix(2.0, LTE, 1), CoreMatchers.is(false));
        Assert.assertThat(evaluateInfix(1, GT, 2.0), CoreMatchers.is(false));
        Assert.assertThat(evaluateInfix(2.0, GT, 1), CoreMatchers.is(true));
        Assert.assertThat(evaluateInfix(1, GTE, 2.0), CoreMatchers.is(false));
        Assert.assertThat(evaluateInfix(2.0, GTE, 1), CoreMatchers.is(true));
    }

    @Test
    public void testOthers() {
        Assert.assertThat(evaluate("ceiling( 1.01 )"), CoreMatchers.is(EvalHelper.getBigDecimalOrNull(2.0)));
        Assert.assertThat(evaluate("ceiling( x )", FEELNumberCoercionTest.var("x", 1.01)), CoreMatchers.is(EvalHelper.getBigDecimalOrNull(2.0)));
        Assert.assertThat(((Map) (evaluate("{ myf : function( v1, v2 ) ceiling(v1), invoked: myf(v2: false, v1: x) }", FEELNumberCoercionTest.var("x", 1.01)))).get("invoked"), CoreMatchers.is(EvalHelper.getBigDecimalOrNull(2.0)));
        Assert.assertThat(((Map) (evaluate("{ myf : function( v1, v2 ) v1, invoked: myf(v2: false, v1: x) }", FEELNumberCoercionTest.var("x", 1.01)))).get("invoked"), CoreMatchers.is(EvalHelper.getBigDecimalOrNull(1.01)));
        Assert.assertThat(evaluate(" x.y ", FEELNumberCoercionTest.var("x", new HashMap<String, Object>() {
            {
                put("y", 1.01);
            }
        })), CoreMatchers.is(EvalHelper.getBigDecimalOrNull(1.01)));
        Assert.assertThat(evaluate("ceiling( x.y )", FEELNumberCoercionTest.var("x", new HashMap<String, Object>() {
            {
                put("y", 1.01);
            }
        })), CoreMatchers.is(EvalHelper.getBigDecimalOrNull(2.0)));
    }

    @Test
    public void testMethodGetBigDecimalOrNull() {
        Assert.assertThat(EvalHelper.getBigDecimalOrNull(((short) (1))), CoreMatchers.is(BigDecimal.ONE));
        Assert.assertThat(EvalHelper.getBigDecimalOrNull(((byte) (1))), CoreMatchers.is(BigDecimal.ONE));
        Assert.assertThat(EvalHelper.getBigDecimalOrNull(1), CoreMatchers.is(BigDecimal.ONE));
        Assert.assertThat(EvalHelper.getBigDecimalOrNull(1L), CoreMatchers.is(BigDecimal.ONE));
        Assert.assertThat(EvalHelper.getBigDecimalOrNull(1.0F), CoreMatchers.is(BigDecimal.ONE));
        Assert.assertThat(EvalHelper.getBigDecimalOrNull(1.1F), CoreMatchers.is(BigDecimal.valueOf(1.1)));
        Assert.assertThat(EvalHelper.getBigDecimalOrNull(1.0), CoreMatchers.is(BigDecimal.ONE));
        Assert.assertThat(EvalHelper.getBigDecimalOrNull(1.1), CoreMatchers.is(BigDecimal.valueOf(1.1)));
        Assert.assertThat(EvalHelper.getBigDecimalOrNull("1"), CoreMatchers.is(BigDecimal.ONE));
        Assert.assertThat(EvalHelper.getBigDecimalOrNull("1.1"), CoreMatchers.is(BigDecimal.valueOf(1.1)));
        Assert.assertThat(EvalHelper.getBigDecimalOrNull("1.1000000"), CoreMatchers.is(BigDecimal.valueOf(1.1).setScale(7, BigDecimal.ROUND_HALF_EVEN)));
        Assert.assertThat(EvalHelper.getBigDecimalOrNull(Double.POSITIVE_INFINITY), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(EvalHelper.getBigDecimalOrNull(Double.NEGATIVE_INFINITY), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(EvalHelper.getBigDecimalOrNull(Double.NaN), CoreMatchers.is(CoreMatchers.nullValue()));
    }
}

