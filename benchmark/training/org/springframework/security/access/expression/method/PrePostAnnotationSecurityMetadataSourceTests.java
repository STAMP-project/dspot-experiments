/**
 * Copyright 2002-2017 the original author or authors.
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


import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.List;
import org.junit.Test;
import org.springframework.expression.Expression;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.annotation.sec2150.MethodInvocationFactory;
import org.springframework.security.access.intercept.method.MockMethodInvocation;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.security.access.prepost.PrePostAnnotationSecurityMetadataSource;
import org.springframework.test.util.ReflectionTestUtils;


/**
 *
 *
 * @author Luke Taylor
 * @since 3.0
 */
public class PrePostAnnotationSecurityMetadataSourceTests {
    private PrePostAnnotationSecurityMetadataSource mds = new PrePostAnnotationSecurityMetadataSource(new ExpressionBasedAnnotationAttributeFactory(new DefaultMethodSecurityExpressionHandler()));

    private MockMethodInvocation voidImpl1;

    private MockMethodInvocation voidImpl2;

    private MockMethodInvocation voidImpl3;

    private MockMethodInvocation listImpl1;

    private MockMethodInvocation notherListImpl1;

    private MockMethodInvocation notherListImpl2;

    private MockMethodInvocation annotatedAtClassLevel;

    private MockMethodInvocation annotatedAtInterfaceLevel;

    private MockMethodInvocation annotatedAtMethodLevel;

    @Test
    public void classLevelPreAnnotationIsPickedUpWhenNoMethodLevelExists() throws Exception {
        ConfigAttribute[] attrs = mds.getAttributes(voidImpl1).toArray(new ConfigAttribute[0]);
        assertThat(attrs).hasSize(1);
        assertThat(((attrs[0]) instanceof PreInvocationExpressionAttribute)).isTrue();
        PreInvocationExpressionAttribute pre = ((PreInvocationExpressionAttribute) (attrs[0]));
        assertThat(pre.getAuthorizeExpression()).isNotNull();
        assertThat(pre.getAuthorizeExpression().getExpressionString()).isEqualTo("someExpression");
        assertThat(pre.getFilterExpression()).isNull();
    }

    @Test
    public void mixedClassAndMethodPreAnnotationsAreBothIncluded() {
        ConfigAttribute[] attrs = mds.getAttributes(voidImpl2).toArray(new ConfigAttribute[0]);
        assertThat(attrs).hasSize(1);
        assertThat(((attrs[0]) instanceof PreInvocationExpressionAttribute)).isTrue();
        PreInvocationExpressionAttribute pre = ((PreInvocationExpressionAttribute) (attrs[0]));
        assertThat(pre.getAuthorizeExpression().getExpressionString()).isEqualTo("someExpression");
        assertThat(pre.getFilterExpression()).isNotNull();
        assertThat(pre.getFilterExpression().getExpressionString()).isEqualTo("somePreFilterExpression");
    }

    @Test
    public void methodWithPreFilterOnlyIsAllowed() {
        ConfigAttribute[] attrs = mds.getAttributes(voidImpl3).toArray(new ConfigAttribute[0]);
        assertThat(attrs).hasSize(1);
        assertThat(((attrs[0]) instanceof PreInvocationExpressionAttribute)).isTrue();
        PreInvocationExpressionAttribute pre = ((PreInvocationExpressionAttribute) (attrs[0]));
        assertThat(pre.getAuthorizeExpression().getExpressionString()).isEqualTo("permitAll");
        assertThat(pre.getFilterExpression()).isNotNull();
        assertThat(pre.getFilterExpression().getExpressionString()).isEqualTo("somePreFilterExpression");
    }

    @Test
    public void methodWithPostFilterOnlyIsAllowed() {
        ConfigAttribute[] attrs = mds.getAttributes(listImpl1).toArray(new ConfigAttribute[0]);
        assertThat(attrs).hasSize(2);
        assertThat(((attrs[0]) instanceof PreInvocationExpressionAttribute)).isTrue();
        assertThat(((attrs[1]) instanceof PostInvocationExpressionAttribute)).isTrue();
        PreInvocationExpressionAttribute pre = ((PreInvocationExpressionAttribute) (attrs[0]));
        PostInvocationExpressionAttribute post = ((PostInvocationExpressionAttribute) (attrs[1]));
        assertThat(pre.getAuthorizeExpression().getExpressionString()).isEqualTo("permitAll");
        assertThat(post.getFilterExpression()).isNotNull();
        assertThat(post.getFilterExpression().getExpressionString()).isEqualTo("somePostFilterExpression");
    }

    @Test
    public void interfaceAttributesAreIncluded() {
        ConfigAttribute[] attrs = mds.getAttributes(notherListImpl1).toArray(new ConfigAttribute[0]);
        assertThat(attrs).hasSize(1);
        assertThat(((attrs[0]) instanceof PreInvocationExpressionAttribute)).isTrue();
        PreInvocationExpressionAttribute pre = ((PreInvocationExpressionAttribute) (attrs[0]));
        assertThat(pre.getFilterExpression()).isNotNull();
        assertThat(pre.getAuthorizeExpression()).isNotNull();
        assertThat(pre.getAuthorizeExpression().getExpressionString()).isEqualTo("interfaceMethodAuthzExpression");
        assertThat(pre.getFilterExpression().getExpressionString()).isEqualTo("interfacePreFilterExpression");
    }

    @Test
    public void classAttributesTakesPrecedeceOverInterfaceAttributes() {
        ConfigAttribute[] attrs = mds.getAttributes(notherListImpl2).toArray(new ConfigAttribute[0]);
        assertThat(attrs).hasSize(1);
        assertThat(((attrs[0]) instanceof PreInvocationExpressionAttribute)).isTrue();
        PreInvocationExpressionAttribute pre = ((PreInvocationExpressionAttribute) (attrs[0]));
        assertThat(pre.getFilterExpression()).isNotNull();
        assertThat(pre.getAuthorizeExpression()).isNotNull();
        assertThat(pre.getAuthorizeExpression().getExpressionString()).isEqualTo("interfaceMethodAuthzExpression");
        assertThat(pre.getFilterExpression().getExpressionString()).isEqualTo("classMethodPreFilterExpression");
    }

    @Test
    public void customAnnotationAtClassLevelIsDetected() throws Exception {
        ConfigAttribute[] attrs = mds.getAttributes(annotatedAtClassLevel).toArray(new ConfigAttribute[0]);
        assertThat(attrs).hasSize(1);
    }

    @Test
    public void customAnnotationAtInterfaceLevelIsDetected() throws Exception {
        ConfigAttribute[] attrs = mds.getAttributes(annotatedAtInterfaceLevel).toArray(new ConfigAttribute[0]);
        assertThat(attrs).hasSize(1);
    }

    @Test
    public void customAnnotationAtMethodLevelIsDetected() throws Exception {
        ConfigAttribute[] attrs = mds.getAttributes(annotatedAtMethodLevel).toArray(new ConfigAttribute[0]);
        assertThat(attrs).hasSize(1);
    }

    @Test
    public void proxyFactoryInterfaceAttributesFound() throws Exception {
        MockMethodInvocation mi = MethodInvocationFactory.createSec2150MethodInvocation();
        Collection<ConfigAttribute> attributes = mds.getAttributes(mi);
        assertThat(attributes).hasSize(1);
        Expression expression = ((Expression) (ReflectionTestUtils.getField(attributes.iterator().next(), "authorizeExpression")));
        assertThat(expression.getExpressionString()).isEqualTo("hasRole('ROLE_PERSON')");
    }

    // ~ Inner Classes
    // ==================================================================================================
    public static interface ReturnVoid {
        public void doSomething(List<?> param);
    }

    public static interface ReturnAList {
        public List<?> doSomething(List<?> param);
    }

    @PreAuthorize("interfaceAuthzExpression")
    public static interface ReturnAnotherList {
        @PreAuthorize("interfaceMethodAuthzExpression")
        @PreFilter(filterTarget = "param", value = "interfacePreFilterExpression")
        public List<?> doSomething(List<?> param);
    }

    @PreAuthorize("someExpression")
    public static class ReturnVoidImpl1 implements PrePostAnnotationSecurityMetadataSourceTests.ReturnVoid {
        public void doSomething(List<?> param) {
        }
    }

    @PreAuthorize("someExpression")
    public static class ReturnVoidImpl2 implements PrePostAnnotationSecurityMetadataSourceTests.ReturnVoid {
        @PreFilter(filterTarget = "param", value = "somePreFilterExpression")
        public void doSomething(List<?> param) {
        }
    }

    public static class ReturnVoidImpl3 implements PrePostAnnotationSecurityMetadataSourceTests.ReturnVoid {
        @PreFilter(filterTarget = "param", value = "somePreFilterExpression")
        public void doSomething(List<?> param) {
        }
    }

    public static class ReturnAListImpl1 implements PrePostAnnotationSecurityMetadataSourceTests.ReturnAList {
        @PostFilter("somePostFilterExpression")
        public List<?> doSomething(List<?> param) {
            return param;
        }
    }

    public static class ReturnAListImpl2 implements PrePostAnnotationSecurityMetadataSourceTests.ReturnAList {
        @PreAuthorize("someExpression")
        @PreFilter(filterTarget = "param", value = "somePreFilterExpression")
        @PostFilter("somePostFilterExpression")
        @PostAuthorize("somePostAuthorizeExpression")
        public List<?> doSomething(List<?> param) {
            return param;
        }
    }

    public static class ReturnAnotherListImpl1 implements PrePostAnnotationSecurityMetadataSourceTests.ReturnAnotherList {
        public List<?> doSomething(List<?> param) {
            return param;
        }
    }

    public static class ReturnAnotherListImpl2 implements PrePostAnnotationSecurityMetadataSourceTests.ReturnAnotherList {
        @PreFilter(filterTarget = "param", value = "classMethodPreFilterExpression")
        public List<?> doSomething(List<?> param) {
            return param;
        }
    }

    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @PreAuthorize("customAnnotationExpression")
    public @interface CustomAnnotation {}

    @PrePostAnnotationSecurityMetadataSourceTests.CustomAnnotation
    public static interface ReturnVoid2 {
        public void doSomething(List<?> param);
    }

    @PrePostAnnotationSecurityMetadataSourceTests.CustomAnnotation
    public static class CustomAnnotationAtClassLevel implements PrePostAnnotationSecurityMetadataSourceTests.ReturnVoid {
        public void doSomething(List<?> param) {
        }
    }

    public static class CustomAnnotationAtInterfaceLevel implements PrePostAnnotationSecurityMetadataSourceTests.ReturnVoid2 {
        public void doSomething(List<?> param) {
        }
    }

    public static class CustomAnnotationAtMethodLevel implements PrePostAnnotationSecurityMetadataSourceTests.ReturnVoid {
        @PrePostAnnotationSecurityMetadataSourceTests.CustomAnnotation
        public void doSomething(List<?> param) {
        }
    }
}

