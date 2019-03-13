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
package org.springframework.security.taglibs.authz;


import WebAttributes.WEB_INVOCATION_PRIVILEGE_EVALUATOR_ATTRIBUTE;
import java.io.IOException;
import java.util.Collections;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.WebInvocationPrivilegeEvaluator;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.web.context.WebApplicationContext;


/**
 *
 *
 * @author Rob Winch
 */
public class AbstractAuthorizeTagTests {
    private AbstractAuthorizeTag tag;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    private MockServletContext servletContext;

    @Test
    public void privilegeEvaluatorFromRequest() throws IOException {
        String uri = "/something";
        WebInvocationPrivilegeEvaluator expected = Mockito.mock(WebInvocationPrivilegeEvaluator.class);
        tag.setUrl(uri);
        request.setAttribute(WEB_INVOCATION_PRIVILEGE_EVALUATOR_ATTRIBUTE, expected);
        tag.authorizeUsingUrlCheck();
        Mockito.verify(expected).isAllowed(ArgumentMatchers.eq(""), ArgumentMatchers.eq(uri), ArgumentMatchers.eq("GET"), ArgumentMatchers.any());
    }

    @Test
    public void privilegeEvaluatorFromChildContext() throws IOException {
        String uri = "/something";
        WebInvocationPrivilegeEvaluator expected = Mockito.mock(WebInvocationPrivilegeEvaluator.class);
        tag.setUrl(uri);
        WebApplicationContext wac = Mockito.mock(WebApplicationContext.class);
        Mockito.when(wac.getBeansOfType(WebInvocationPrivilegeEvaluator.class)).thenReturn(Collections.singletonMap("wipe", expected));
        servletContext.setAttribute("org.springframework.web.servlet.FrameworkServlet.CONTEXT.dispatcher", wac);
        tag.authorizeUsingUrlCheck();
        Mockito.verify(expected).isAllowed(ArgumentMatchers.eq(""), ArgumentMatchers.eq(uri), ArgumentMatchers.eq("GET"), ArgumentMatchers.any());
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void expressionFromChildContext() throws IOException {
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("user", "pass", "USER"));
        DefaultWebSecurityExpressionHandler expected = new DefaultWebSecurityExpressionHandler();
        tag.setAccess("permitAll");
        WebApplicationContext wac = Mockito.mock(WebApplicationContext.class);
        Mockito.when(wac.getBeansOfType(SecurityExpressionHandler.class)).thenReturn(Collections.<String, SecurityExpressionHandler>singletonMap("wipe", expected));
        servletContext.setAttribute("org.springframework.web.servlet.FrameworkServlet.CONTEXT.dispatcher", wac);
        assertThat(tag.authorize()).isTrue();
    }

    private class AuthzTag extends AbstractAuthorizeTag {
        @Override
        protected ServletRequest getRequest() {
            return request;
        }

        @Override
        protected ServletResponse getResponse() {
            return response;
        }

        @Override
        protected ServletContext getServletContext() {
            return servletContext;
        }
    }
}

