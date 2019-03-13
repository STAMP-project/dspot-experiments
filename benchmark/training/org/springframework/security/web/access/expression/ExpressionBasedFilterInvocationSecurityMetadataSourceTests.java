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


import AnyRequestMatcher.INSTANCE;
import java.util.Collection;
import java.util.LinkedHashMap;
import org.junit.Test;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.util.matcher.RequestMatcher;


/**
 *
 *
 * @author Luke Taylor
 */
public class ExpressionBasedFilterInvocationSecurityMetadataSourceTests {
    @Test
    public void expectedAttributeIsReturned() {
        final String expression = "hasRole('X')";
        LinkedHashMap<RequestMatcher, Collection<ConfigAttribute>> requestMap = new LinkedHashMap<RequestMatcher, Collection<ConfigAttribute>>();
        requestMap.put(INSTANCE, SecurityConfig.createList(expression));
        ExpressionBasedFilterInvocationSecurityMetadataSource mds = new ExpressionBasedFilterInvocationSecurityMetadataSource(requestMap, new DefaultWebSecurityExpressionHandler());
        assertThat(mds.getAllConfigAttributes()).hasSize(1);
        Collection<ConfigAttribute> attrs = mds.getAttributes(new FilterInvocation("/path", "GET"));
        assertThat(attrs).hasSize(1);
        WebExpressionConfigAttribute attribute = ((WebExpressionConfigAttribute) (attrs.toArray()[0]));
        assertThat(attribute.getAttribute()).isNull();
        assertThat(attribute.getAuthorizeExpression().getExpressionString()).isEqualTo(expression);
        assertThat(attribute.toString()).isEqualTo(expression);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidExpressionIsRejected() throws Exception {
        LinkedHashMap<RequestMatcher, Collection<ConfigAttribute>> requestMap = new LinkedHashMap<RequestMatcher, Collection<ConfigAttribute>>();
        requestMap.put(INSTANCE, SecurityConfig.createList("hasRole('X'"));
        ExpressionBasedFilterInvocationSecurityMetadataSource mds = new ExpressionBasedFilterInvocationSecurityMetadataSource(requestMap, new DefaultWebSecurityExpressionHandler());
    }
}

