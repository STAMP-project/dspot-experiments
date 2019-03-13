/**
 * Copyright 2010-2016 the original author or authors.
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
package org.springframework.security.web.jaasapi;


import java.security.AccessController;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 * Tests the JaasApiIntegrationFilter.
 *
 * @author Rob Winch
 */
public class JaasApiIntegrationFilterTests {
    // ~ Instance fields
    // ================================================================================================
    private JaasApiIntegrationFilter filter;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    private Authentication token;

    private Subject authenticatedSubject;

    private Configuration testConfiguration;

    private CallbackHandler callbackHandler;

    /**
     * Ensure a Subject was not setup in some other manner.
     */
    @Test
    public void currentSubjectNull() {
        assertThat(Subject.getSubject(AccessController.getContext())).isNull();
    }

    @Test
    public void obtainSubjectNullAuthentication() {
        assertNullSubject(filter.obtainSubject(request));
    }

    @Test
    public void obtainSubjectNonJaasAuthentication() {
        Authentication authentication = new TestingAuthenticationToken("un", "pwd");
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        assertNullSubject(filter.obtainSubject(request));
    }

    @Test
    public void obtainSubjectNullLoginContext() {
        token = new org.springframework.security.authentication.jaas.JaasAuthenticationToken("un", "pwd", AuthorityUtils.createAuthorityList("ROLE_ADMIN"), null);
        SecurityContextHolder.getContext().setAuthentication(token);
        assertNullSubject(filter.obtainSubject(request));
    }

    @Test
    public void obtainSubjectNullSubject() throws Exception {
        LoginContext ctx = new LoginContext("obtainSubjectNullSubject", null, callbackHandler, testConfiguration);
        assertThat(ctx.getSubject()).isNull();
        token = new org.springframework.security.authentication.jaas.JaasAuthenticationToken("un", "pwd", AuthorityUtils.createAuthorityList("ROLE_ADMIN"), ctx);
        SecurityContextHolder.getContext().setAuthentication(token);
        assertNullSubject(filter.obtainSubject(request));
    }

    @Test
    public void obtainSubject() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(token);
        assertThat(filter.obtainSubject(request)).isEqualTo(authenticatedSubject);
    }

    @Test
    public void doFilterCurrentSubjectPopulated() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(token);
        assertJaasSubjectEquals(authenticatedSubject);
    }

    @Test
    public void doFilterAuthenticationNotAuthenticated() throws Exception {
        // Authentication is null, so no Subject is populated.
        token.setAuthenticated(false);
        SecurityContextHolder.getContext().setAuthentication(token);
        assertJaasSubjectEquals(null);
        filter.setCreateEmptySubject(true);
        assertJaasSubjectEquals(new Subject());
    }

    @Test
    public void doFilterAuthenticationNull() throws Exception {
        assertJaasSubjectEquals(null);
        filter.setCreateEmptySubject(true);
        assertJaasSubjectEquals(new Subject());
    }
}

