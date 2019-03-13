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


import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ManualBasicFunctionInvocationTest {
    public static final Logger LOG = LoggerFactory.getLogger(ManualBasicFunctionInvocationTest.class);

    public static class ManualFilterExpression implements CompiledFEELExpression {
        public static final BigDecimal K_1 = new BigDecimal(1, MathContext.DECIMAL128);

        public static final BigDecimal K_2 = new BigDecimal(2, MathContext.DECIMAL128);

        public static final BigDecimal K_3 = new BigDecimal(3, MathContext.DECIMAL128);

        /**
         * FEEL: max( 1, 2, 3 )
         */
        @Override
        public Object apply(EvaluationContext feelExprCtx) {
            return CompiledFEELSupport.invoke(feelExprCtx, feelExprCtx.getValue("max"), Arrays.asList(ManualBasicFunctionInvocationTest.ManualFilterExpression.K_1, ManualBasicFunctionInvocationTest.ManualFilterExpression.K_2, ManualBasicFunctionInvocationTest.ManualFilterExpression.K_3));
        }
    }

    @Test
    public void testManualContext() {
        CompiledFEELExpression compiledExpression = new ManualBasicFunctionInvocationTest.ManualFilterExpression();
        ManualBasicFunctionInvocationTest.LOG.debug("{}", compiledExpression);
        EvaluationContext emptyContext = CodegenTestUtil.newEmptyEvaluationContext();
        Object result = compiledExpression.apply(emptyContext);
        ManualBasicFunctionInvocationTest.LOG.debug("{}", result);
        Assert.assertThat(result, CoreMatchers.is(BigDecimal.valueOf(3)));
    }
}

