/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.security.access.expression.method;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.core.Authentication;


@RunWith(MockitoJUnitRunner.class)
public class DefaultMethodSecurityExpressionHandlerTests {
    private DefaultMethodSecurityExpressionHandler handler;

    @Mock
    private Authentication authentication;

    @Mock
    private MethodInvocation methodInvocation;

    @Mock
    private AuthenticationTrustResolver trustResolver;

    @Test(expected = IllegalArgumentException.class)
    public void setTrustResolverNull() {
        handler.setTrustResolver(null);
    }

    @Test
    public void createEvaluationContextCustomTrustResolver() {
        handler.setTrustResolver(trustResolver);
        Expression expression = handler.getExpressionParser().parseExpression("anonymous");
        EvaluationContext context = handler.createEvaluationContext(authentication, methodInvocation);
        expression.getValue(context, Boolean.class);
        Mockito.verify(trustResolver).isAnonymous(authentication);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void filterWhenUsingStreamThenFiltersStream() {
        final Stream<String> stream = Stream.of("1", "2", "3");
        Expression expression = handler.getExpressionParser().parseExpression("filterObject ne '2'");
        EvaluationContext context = handler.createEvaluationContext(authentication, methodInvocation);
        Object filtered = handler.filter(stream, expression, context);
        assertThat(filtered).isInstanceOf(Stream.class);
        List<String> list = ((Stream<String>) (filtered)).collect(Collectors.toList());
        assertThat(list).containsExactly("1", "3");
    }

    @Test
    public void filterStreamWhenClosedThenUpstreamGetsClosed() {
        final Stream<?> upstream = Mockito.mock(Stream.class);
        Mockito.doReturn(Stream.<String>empty()).when(upstream).filter(ArgumentMatchers.any());
        Expression expression = handler.getExpressionParser().parseExpression("true");
        EvaluationContext context = handler.createEvaluationContext(authentication, methodInvocation);
        ((Stream) (handler.filter(upstream, expression, context))).close();
        Mockito.verify(upstream).close();
    }
}

