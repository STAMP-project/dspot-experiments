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
import java.util.function.Function;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ManualFilterTest {
    public static final Logger LOG = LoggerFactory.getLogger(ManualFilterTest.class);

    public static class ManualFilterExpression implements CompiledFEELExpression {
        public static final BigDecimal K_1 = new BigDecimal(1, MathContext.DECIMAL128);

        public static final BigDecimal K_2 = new BigDecimal(2, MathContext.DECIMAL128);

        public static final BigDecimal K_3 = new BigDecimal(3, MathContext.DECIMAL128);

        public static final BigDecimal K_4 = new BigDecimal(4, MathContext.DECIMAL128);

        /**
         * FEEL: [1, 2, 3, 4][item > 2]
         */
        @Override
        public Object apply(EvaluationContext feelExprCtx) {
            return CompiledFEELSupport.filter(feelExprCtx, Arrays.asList(ManualFilterTest.ManualFilterExpression.K_1, ManualFilterTest.ManualFilterExpression.K_2, ManualFilterTest.ManualFilterExpression.K_3, ManualFilterTest.ManualFilterExpression.K_4)).with(new Function<EvaluationContext, Object>() {
                @Override
                public Object apply(EvaluationContext feelExprCtx) {
                    return CompiledFEELSemanticMappings.gt(feelExprCtx.getValue("item"), ManualFilterTest.ManualFilterExpression.K_2);
                }
            });
        }
    }

    @Test
    public void testManualContext() {
        CompiledFEELExpression compiledExpression = new ManualFilterTest.ManualFilterExpression();
        ManualFilterTest.LOG.debug("{}", compiledExpression);
        EvaluationContext emptyContext = CodegenTestUtil.newEmptyEvaluationContext();
        Object result = compiledExpression.apply(emptyContext);
        ManualFilterTest.LOG.debug("{}", result);
        Assert.assertThat(result, CoreMatchers.is(Arrays.asList(BigDecimal.valueOf(3), BigDecimal.valueOf(4))));
    }
}

