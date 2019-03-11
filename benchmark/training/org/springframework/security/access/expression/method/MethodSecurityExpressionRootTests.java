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
package org.springframework.security.access.expression.method;


import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.ExpressionUtils;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.core.Authentication;


/**
 * Tests for {@link MethodSecurityExpressionRoot}
 *
 * @author Luke Taylor
 */
public class MethodSecurityExpressionRootTests {
    SpelExpressionParser parser = new SpelExpressionParser();

    MethodSecurityExpressionRoot root;

    StandardEvaluationContext ctx;

    private AuthenticationTrustResolver trustResolver;

    private Authentication user;

    @Test
    public void canCallMethodsOnVariables() throws Exception {
        ctx.setVariable("var", "somestring");
        Expression e = parser.parseExpression("#var.length() == 10");
        assertThat(ExpressionUtils.evaluateAsBoolean(e, ctx)).isTrue();
    }

    @Test
    public void isAnonymousReturnsTrueIfTrustResolverReportsAnonymous() {
        Mockito.when(trustResolver.isAnonymous(user)).thenReturn(true);
        assertThat(root.isAnonymous()).isTrue();
    }

    @Test
    public void isAnonymousReturnsFalseIfTrustResolverReportsNonAnonymous() {
        Mockito.when(trustResolver.isAnonymous(user)).thenReturn(false);
        assertThat(root.isAnonymous()).isFalse();
    }

    @Test
    public void hasPermissionOnDomainObjectReturnsFalseIfPermissionEvaluatorDoes() throws Exception {
        final Object dummyDomainObject = new Object();
        final PermissionEvaluator pe = Mockito.mock(PermissionEvaluator.class);
        ctx.setVariable("domainObject", dummyDomainObject);
        root.setPermissionEvaluator(pe);
        Mockito.when(pe.hasPermission(user, dummyDomainObject, "ignored")).thenReturn(false);
        assertThat(root.hasPermission(dummyDomainObject, "ignored")).isFalse();
    }

    @Test
    public void hasPermissionOnDomainObjectReturnsTrueIfPermissionEvaluatorDoes() throws Exception {
        final Object dummyDomainObject = new Object();
        final PermissionEvaluator pe = Mockito.mock(PermissionEvaluator.class);
        ctx.setVariable("domainObject", dummyDomainObject);
        root.setPermissionEvaluator(pe);
        Mockito.when(pe.hasPermission(user, dummyDomainObject, "ignored")).thenReturn(true);
        assertThat(root.hasPermission(dummyDomainObject, "ignored")).isTrue();
    }

    @Test
    public void hasPermissionOnDomainObjectWorksWithIntegerExpressions() throws Exception {
        final Object dummyDomainObject = new Object();
        ctx.setVariable("domainObject", dummyDomainObject);
        final PermissionEvaluator pe = Mockito.mock(PermissionEvaluator.class);
        root.setPermissionEvaluator(pe);
        Mockito.when(pe.hasPermission(ArgumentMatchers.eq(user), ArgumentMatchers.eq(dummyDomainObject), ArgumentMatchers.any(Integer.class))).thenReturn(true).thenReturn(true).thenReturn(false);
        Expression e = parser.parseExpression("hasPermission(#domainObject, 0xA)");
        // evaluator returns true
        assertThat(ExpressionUtils.evaluateAsBoolean(e, ctx)).isTrue();
        e = parser.parseExpression("hasPermission(#domainObject, 10)");
        // evaluator returns true
        assertThat(ExpressionUtils.evaluateAsBoolean(e, ctx)).isTrue();
        e = parser.parseExpression("hasPermission(#domainObject, 0xFF)");
        // evaluator returns false, make sure return value matches
        assertThat(ExpressionUtils.evaluateAsBoolean(e, ctx)).isFalse();
    }

    @Test
    public void hasPermissionWorksWithThisObject() throws Exception {
        Object targetObject = new Object() {
            public String getX() {
                return "x";
            }
        };
        root.setThis(targetObject);
        Integer i = 2;
        PermissionEvaluator pe = Mockito.mock(PermissionEvaluator.class);
        root.setPermissionEvaluator(pe);
        Mockito.when(pe.hasPermission(user, targetObject, i)).thenReturn(true).thenReturn(false);
        Mockito.when(pe.hasPermission(user, "x", i)).thenReturn(true);
        Expression e = parser.parseExpression("hasPermission(this, 2)");
        assertThat(ExpressionUtils.evaluateAsBoolean(e, ctx)).isTrue();
        e = parser.parseExpression("hasPermission(this, 2)");
        assertThat(ExpressionUtils.evaluateAsBoolean(e, ctx)).isFalse();
        e = parser.parseExpression("hasPermission(this.x, 2)");
        assertThat(ExpressionUtils.evaluateAsBoolean(e, ctx)).isTrue();
    }
}

