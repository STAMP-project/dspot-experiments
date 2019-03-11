/**
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.security.web.access.expression;


import java.util.Collections;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.junit.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.FilterInvocation;


/**
 *
 *
 * @author Rob Winch
 */
public class AbstractVariableEvaluationContextPostProcessorTests {
    static final String KEY = "a";

    static final String VALUE = "b";

    AbstractVariableEvaluationContextPostProcessorTests.VariableEvaluationContextPostProcessor processor;

    FilterInvocation invocation;

    MockHttpServletRequest request;

    MockHttpServletResponse response;

    EvaluationContext context;

    @Test
    public void extractVariables() {
        this.context = this.processor.postProcess(this.context, this.invocation);
        assertThat(this.context.lookupVariable(AbstractVariableEvaluationContextPostProcessorTests.KEY)).isEqualTo(AbstractVariableEvaluationContextPostProcessorTests.VALUE);
    }

    @Test
    public void extractVariablesOnlyUsedOnce() {
        this.context = this.processor.postProcess(this.context, this.invocation);
        assertThat(this.context.lookupVariable(AbstractVariableEvaluationContextPostProcessorTests.KEY)).isEqualTo(AbstractVariableEvaluationContextPostProcessorTests.VALUE);
        this.processor.results = Collections.emptyMap();
        assertThat(this.context.lookupVariable(AbstractVariableEvaluationContextPostProcessorTests.KEY)).isEqualTo(AbstractVariableEvaluationContextPostProcessorTests.VALUE);
    }

    static class VariableEvaluationContextPostProcessor extends AbstractVariableEvaluationContextPostProcessor {
        Map<String, String> results = Collections.singletonMap(AbstractVariableEvaluationContextPostProcessorTests.KEY, AbstractVariableEvaluationContextPostProcessorTests.VALUE);

        @Override
        protected Map<String, String> extractVariables(HttpServletRequest request) {
            return this.results;
        }
    }
}

