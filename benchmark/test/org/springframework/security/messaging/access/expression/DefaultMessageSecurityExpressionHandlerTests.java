/**
 * Copyright 2002-2013 the original author or authors.
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
package org.springframework.security.messaging.access.expression;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.messaging.Message;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.ExpressionUtils;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;


@RunWith(MockitoJUnitRunner.class)
public class DefaultMessageSecurityExpressionHandlerTests {
    @Mock
    AuthenticationTrustResolver trustResolver;

    @Mock
    PermissionEvaluator permissionEvaluator;

    DefaultMessageSecurityExpressionHandler<Object> handler;

    Message<Object> message;

    Authentication authentication;

    // SEC-2705
    @Test
    public void trustResolverPopulated() {
        EvaluationContext context = handler.createEvaluationContext(authentication, message);
        Expression expression = handler.getExpressionParser().parseExpression("authenticated");
        assertThat(ExpressionUtils.evaluateAsBoolean(expression, context)).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void trustResolverNull() {
        handler.setTrustResolver(null);
    }

    @Test
    public void trustResolverCustom() {
        handler.setTrustResolver(trustResolver);
        EvaluationContext context = handler.createEvaluationContext(authentication, message);
        Expression expression = handler.getExpressionParser().parseExpression("authenticated");
        Mockito.when(trustResolver.isAnonymous(authentication)).thenReturn(false);
        assertThat(ExpressionUtils.evaluateAsBoolean(expression, context)).isTrue();
    }

    @Test
    public void roleHierarchy() {
        authentication = new TestingAuthenticationToken("admin", "pass", "ROLE_ADMIN");
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
        handler.setRoleHierarchy(roleHierarchy);
        EvaluationContext context = handler.createEvaluationContext(authentication, message);
        Expression expression = handler.getExpressionParser().parseExpression("hasRole('ROLE_USER')");
        assertThat(ExpressionUtils.evaluateAsBoolean(expression, context)).isTrue();
    }

    @Test
    public void permissionEvaluator() {
        handler.setPermissionEvaluator(permissionEvaluator);
        EvaluationContext context = handler.createEvaluationContext(authentication, message);
        Expression expression = handler.getExpressionParser().parseExpression("hasPermission(message, 'read')");
        Mockito.when(permissionEvaluator.hasPermission(authentication, message, "read")).thenReturn(true);
        assertThat(ExpressionUtils.evaluateAsBoolean(expression, context)).isTrue();
    }
}

