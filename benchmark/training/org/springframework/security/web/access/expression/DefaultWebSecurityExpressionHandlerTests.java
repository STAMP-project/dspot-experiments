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


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;


@RunWith(MockitoJUnitRunner.class)
public class DefaultWebSecurityExpressionHandlerTests {
    @Mock
    private AuthenticationTrustResolver trustResolver;

    @Mock
    private Authentication authentication;

    @Mock
    private FilterInvocation invocation;

    private DefaultWebSecurityExpressionHandler handler;

    @Test
    public void expressionPropertiesAreResolvedAgainstAppContextBeans() throws Exception {
        StaticApplicationContext appContext = new StaticApplicationContext();
        RootBeanDefinition bean = new RootBeanDefinition(SecurityConfig.class);
        bean.getConstructorArgumentValues().addGenericArgumentValue("ROLE_A");
        appContext.registerBeanDefinition("role", bean);
        handler.setApplicationContext(appContext);
        EvaluationContext ctx = handler.createEvaluationContext(Mockito.mock(Authentication.class), Mockito.mock(FilterInvocation.class));
        ExpressionParser parser = handler.getExpressionParser();
        assertThat(parser.parseExpression("@role.getAttribute() == 'ROLE_A'").getValue(ctx, Boolean.class)).isTrue();
        assertThat(parser.parseExpression("@role.attribute == 'ROLE_A'").getValue(ctx, Boolean.class)).isTrue();
    }

    @Test(expected = IllegalArgumentException.class)
    public void setTrustResolverNull() {
        handler.setTrustResolver(null);
    }

    @Test
    public void createEvaluationContextCustomTrustResolver() {
        handler.setTrustResolver(trustResolver);
        Expression expression = handler.getExpressionParser().parseExpression("anonymous");
        EvaluationContext context = handler.createEvaluationContext(authentication, invocation);
        assertThat(expression.getValue(context, Boolean.class)).isFalse();
        Mockito.verify(trustResolver).isAnonymous(authentication);
    }
}

