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
package org.kie.dmn.feel.codegen.feel11;


import Quantifier.SOME;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ManualFilterExpression.K_100;
import static ManualFilterExpression.K_11;
import static ManualFilterExpression.K_110;
import static ManualFilterExpression.K_80;


public class ManualQuantTest {
    public static final Logger LOG = LoggerFactory.getLogger(ManualQuantTest.class);

    public static class ManualFilterExpression implements CompiledFEELExpression {
        public static final BigDecimal K_80 = new BigDecimal(80, MathContext.DECIMAL128);

        public static final BigDecimal K_11 = new BigDecimal(11, MathContext.DECIMAL128);

        public static final BigDecimal K_100 = new BigDecimal(100, MathContext.DECIMAL128);

        public static final BigDecimal K_110 = new BigDecimal(110, MathContext.DECIMAL128);

        /**
         * FEEL: some price in [ 80, 11, 110 ] satisfies price > 100
         */
        @Override
        public Object apply(EvaluationContext feelExprCtx) {
            return CompiledFEELSupport.quant(SOME, feelExprCtx).with(( c) -> "price", ( c) -> Arrays.asList(ManualFilterExpression.K_80, ManualFilterExpression.K_11, ManualFilterExpression.K_110)).satisfies(( c) -> gt(feelExprCtx.getValue("price"), ManualFilterExpression.K_100));
        }
    }

    @Test
    public void testManualContext() {
        CompiledFEELExpression compiledExpression = new ManualQuantTest.ManualFilterExpression();
        ManualQuantTest.LOG.debug("{}", compiledExpression);
        EvaluationContext emptyContext = CodegenTestUtil.newEmptyEvaluationContext();
        Object result = compiledExpression.apply(emptyContext);
        ManualQuantTest.LOG.debug("{}", result);
        Assert.assertThat(result, CoreMatchers.is(true));
    }
}

