/**
 * Copyright 2002-2016 the original author or authors.
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


import AccessDecisionVoter.ACCESS_ABSTAIN;
import AccessDecisionVoter.ACCESS_DENIED;
import AccessDecisionVoter.ACCESS_GRANTED;
import java.util.ArrayList;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;


/**
 *
 *
 * @author Luke Taylor
 */
@SuppressWarnings({ "unchecked" })
public class WebExpressionVoterTests {
    private Authentication user = new TestingAuthenticationToken("user", "pass", "X");

    @Test
    public void supportsWebConfigAttributeAndFilterInvocation() throws Exception {
        WebExpressionVoter voter = new WebExpressionVoter();
        assertThat(voter.supports(new WebExpressionConfigAttribute(Mockito.mock(Expression.class), Mockito.mock(EvaluationContextPostProcessor.class)))).isTrue();
        assertThat(voter.supports(FilterInvocation.class)).isTrue();
        assertThat(voter.supports(MethodInvocation.class)).isFalse();
    }

    @Test
    public void abstainsIfNoAttributeFound() {
        WebExpressionVoter voter = new WebExpressionVoter();
        assertThat(voter.vote(user, new FilterInvocation("/path", "GET"), SecurityConfig.createList("A", "B", "C"))).isEqualTo(ACCESS_ABSTAIN);
    }

    @Test
    public void grantsAccessIfExpressionIsTrueDeniesIfFalse() {
        WebExpressionVoter voter = new WebExpressionVoter();
        Expression ex = Mockito.mock(Expression.class);
        EvaluationContextPostProcessor postProcessor = Mockito.mock(EvaluationContextPostProcessor.class);
        Mockito.when(postProcessor.postProcess(ArgumentMatchers.any(EvaluationContext.class), ArgumentMatchers.any(FilterInvocation.class))).thenAnswer(( invocation) -> invocation.getArgument(0));
        WebExpressionConfigAttribute weca = new WebExpressionConfigAttribute(ex, postProcessor);
        EvaluationContext ctx = Mockito.mock(EvaluationContext.class);
        SecurityExpressionHandler eh = Mockito.mock(SecurityExpressionHandler.class);
        FilterInvocation fi = new FilterInvocation("/path", "GET");
        voter.setExpressionHandler(eh);
        Mockito.when(eh.createEvaluationContext(user, fi)).thenReturn(ctx);
        Mockito.when(ex.getValue(ctx, Boolean.class)).thenReturn(Boolean.TRUE).thenReturn(Boolean.FALSE);
        ArrayList attributes = new ArrayList();
        attributes.addAll(SecurityConfig.createList("A", "B", "C"));
        attributes.add(weca);
        assertThat(voter.vote(user, fi, attributes)).isEqualTo(ACCESS_GRANTED);
        // Second time false
        assertThat(voter.vote(user, fi, attributes)).isEqualTo(ACCESS_DENIED);
    }

    // SEC-2507
    @Test
    public void supportFilterInvocationSubClass() {
        WebExpressionVoter voter = new WebExpressionVoter();
        assertThat(voter.supports(WebExpressionVoterTests.FilterInvocationChild.class)).isTrue();
    }

    private static class FilterInvocationChild extends FilterInvocation {
        public FilterInvocationChild(ServletRequest request, ServletResponse response, FilterChain chain) {
            super(request, response, chain);
        }
    }

    @Test
    public void supportFilterInvocation() {
        WebExpressionVoter voter = new WebExpressionVoter();
        assertThat(voter.supports(FilterInvocation.class)).isTrue();
    }

    @Test
    public void supportsObjectIsFalse() {
        WebExpressionVoter voter = new WebExpressionVoter();
        assertThat(voter.supports(Object.class)).isFalse();
    }
}

