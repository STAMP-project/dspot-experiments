/**
 * Copyright 2004, 2005, 2006 Acegi Technology Pty Limited
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


import Tag.EVAL_BODY_INCLUDE;
import Tag.SKIP_BODY;
import javax.servlet.jsp.JspException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.WebInvocationPrivilegeEvaluator;


/**
 *
 *
 * @author Francois Beausoleil
 * @author Luke Taylor
 */
@RunWith(MockitoJUnitRunner.class)
public class AuthorizeTagTests {
    // ~ Instance fields
    // ================================================================================================
    @Mock
    private PermissionEvaluator permissionEvaluator;

    private JspAuthorizeTag authorizeTag;

    private MockHttpServletRequest request = new MockHttpServletRequest();

    private final TestingAuthenticationToken currentUser = new TestingAuthenticationToken("abc", "123", "ROLE SUPERVISOR", "ROLE_TELLER");

    // access attribute tests
    @Test
    public void taglibsDocumentationHasPermissionOr() throws Exception {
        Object domain = new Object();
        request.setAttribute("domain", domain);
        authorizeTag.setAccess("hasPermission(#domain,'read') or hasPermission(#domain,'write')");
        Mockito.when(permissionEvaluator.hasPermission(ArgumentMatchers.eq(currentUser), ArgumentMatchers.eq(domain), ArgumentMatchers.anyString())).thenReturn(true);
        assertThat(authorizeTag.doStartTag()).isEqualTo(EVAL_BODY_INCLUDE);
    }

    @Test
    public void skipsBodyIfNoAuthenticationPresent() throws Exception {
        SecurityContextHolder.clearContext();
        authorizeTag.setAccess("permitAll");
        assertThat(authorizeTag.doStartTag()).isEqualTo(SKIP_BODY);
    }

    @Test
    public void skipsBodyIfAccessExpressionDeniesAccess() throws Exception {
        authorizeTag.setAccess("denyAll");
        assertThat(authorizeTag.doStartTag()).isEqualTo(SKIP_BODY);
    }

    @Test
    public void showsBodyIfAccessExpressionAllowsAccess() throws Exception {
        authorizeTag.setAccess("permitAll");
        assertThat(authorizeTag.doStartTag()).isEqualTo(EVAL_BODY_INCLUDE);
    }

    @Test
    public void requestAttributeIsResolvedAsElVariable() throws JspException {
        request.setAttribute("blah", "blah");
        authorizeTag.setAccess("#blah == 'blah'");
        assertThat(authorizeTag.doStartTag()).isEqualTo(EVAL_BODY_INCLUDE);
    }

    // url attribute tests
    @Test
    public void skipsBodyWithUrlSetIfNoAuthenticationPresent() throws Exception {
        SecurityContextHolder.clearContext();
        authorizeTag.setUrl("/something");
        assertThat(authorizeTag.doStartTag()).isEqualTo(SKIP_BODY);
    }

    @Test
    public void skipsBodyIfUrlIsNotAllowed() throws Exception {
        authorizeTag.setUrl("/notallowed");
        assertThat(authorizeTag.doStartTag()).isEqualTo(SKIP_BODY);
    }

    @Test
    public void evaluatesBodyIfUrlIsAllowed() throws Exception {
        authorizeTag.setUrl("/allowed");
        authorizeTag.setMethod("GET");
        assertThat(authorizeTag.doStartTag()).isEqualTo(EVAL_BODY_INCLUDE);
    }

    @Test
    public void skipsBodyIfMethodIsNotAllowed() throws Exception {
        authorizeTag.setUrl("/allowed");
        authorizeTag.setMethod("POST");
        assertThat(authorizeTag.doStartTag()).isEqualTo(SKIP_BODY);
    }

    public static class MockWebInvocationPrivilegeEvaluator implements WebInvocationPrivilegeEvaluator {
        public boolean isAllowed(String uri, Authentication authentication) {
            return "/allowed".equals(uri);
        }

        public boolean isAllowed(String contextPath, String uri, String method, Authentication authentication) {
            return ("/allowed".equals(uri)) && ((method == null) || ("GET".equals(method)));
        }
    }
}

