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
package org.springframework.security.config.http;


import java.security.Principal;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.apache.http.HttpStatus;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus.I_AM_A_TEAPOT;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.config.test.SpringTestRule;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Tests session-related functionality for the &lt;http&gt; namespace element and &lt;session-management&gt;
 *
 * @author Luke Taylor
 * @author Rob Winch
 * @author Josh Cummings
 */
public class SessionManagementConfigTests {
    private static final String CONFIG_LOCATION_PREFIX = "classpath:org/springframework/security/config/http/SessionManagementConfigTests";

    @Rule
    public final SpringTestRule spring = new SpringTestRule();

    @Autowired
    MockMvc mvc;

    @Test
    public void requestWhenCreateSessionAlwaysThenAlwaysCreatesSession() throws Exception {
        this.spring.configLocations(this.xml("CreateSessionAlways")).autowire();
        MockHttpServletRequest request = get("/").buildRequest(this.servletContext());
        MockHttpServletResponse response = SessionManagementConfigTests.request(request, this.spring.getContext());
        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_OK);
        assertThat(request.getSession(false)).isNotNull();
    }

    @Test
    public void requestWhenCreateSessionIsSetToNeverThenDoesNotCreateSessionOnLoginChallenge() throws Exception {
        this.spring.configLocations(this.xml("CreateSessionNever")).autowire();
        MockHttpServletRequest request = get("/auth").buildRequest(this.servletContext());
        MockHttpServletResponse response = SessionManagementConfigTests.request(request, this.spring.getContext());
        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_MOVED_TEMPORARILY);
        assertThat(request.getSession(false)).isNull();
    }

    @Test
    public void requestWhenCreateSessionIsSetToNeverThenDoesNotCreateSessionOnLogin() throws Exception {
        this.spring.configLocations(this.xml("CreateSessionNever")).autowire();
        MockHttpServletRequest request = post("/login").param("username", "user").param("password", "password").buildRequest(this.servletContext());
        request = csrf().postProcessRequest(request);
        MockHttpServletResponse response = SessionManagementConfigTests.request(request, this.spring.getContext());
        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_MOVED_TEMPORARILY);
        assertThat(request.getSession(false)).isNull();
    }

    @Test
    public void requestWhenCreateSessionIsSetToNeverThenUsesExistingSession() throws Exception {
        this.spring.configLocations(this.xml("CreateSessionNever")).autowire();
        MockHttpServletRequest request = post("/login").param("username", "user").param("password", "password").buildRequest(this.servletContext());
        request = csrf().postProcessRequest(request);
        MockHttpSession session = new MockHttpSession();
        request.setSession(session);
        MockHttpServletResponse response = SessionManagementConfigTests.request(request, this.spring.getContext());
        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_MOVED_TEMPORARILY);
        assertThat(request.getSession(false)).isNotNull();
        assertThat(request.getSession(false).getAttribute(SPRING_SECURITY_CONTEXT_KEY)).isNotNull();
    }

    @Test
    public void requestWhenCreateSessionIsSetToStatelessThenDoesNotCreateSessionOnLoginChallenge() throws Exception {
        this.spring.configLocations(this.xml("CreateSessionStateless")).autowire();
        this.mvc.perform(get("/auth")).andExpect(status().isFound()).andExpect(SessionManagementConfigTests.session().exists(false));
    }

    @Test
    public void requestWhenCreateSessionIsSetToStatelessThenDoesNotCreateSessionOnLogin() throws Exception {
        this.spring.configLocations(this.xml("CreateSessionStateless")).autowire();
        this.mvc.perform(post("/login").param("username", "user").param("password", "password").with(csrf())).andExpect(status().isFound()).andExpect(SessionManagementConfigTests.session().exists(false));
    }

    @Test
    public void requestWhenCreateSessionIsSetToStatelessThenIgnoresExistingSession() throws Exception {
        this.spring.configLocations(this.xml("CreateSessionStateless")).autowire();
        MvcResult result = this.mvc.perform(post("/login").param("username", "user").param("password", "password").session(new MockHttpSession()).with(csrf())).andExpect(status().isFound()).andExpect(SessionManagementConfigTests.session()).andReturn();
        assertThat(result.getRequest().getSession(false).getAttribute(SPRING_SECURITY_CONTEXT_KEY)).isNull();
    }

    @Test
    public void requestWhenCreateSessionIsSetToIfRequiredThenDoesNotCreateSessionOnPublicInvocation() throws Exception {
        this.spring.configLocations(this.xml("CreateSessionIfRequired")).autowire();
        ServletContext servletContext = this.mvc.getDispatcherServlet().getServletContext();
        MockHttpServletRequest request = get("/").buildRequest(servletContext);
        MockHttpServletResponse response = SessionManagementConfigTests.request(request, this.spring.getContext());
        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_OK);
        assertThat(request.getSession(false)).isNull();
    }

    @Test
    public void requestWhenCreateSessionIsSetToIfRequiredThenCreatesSessionOnLoginChallenge() throws Exception {
        this.spring.configLocations(this.xml("CreateSessionIfRequired")).autowire();
        ServletContext servletContext = this.mvc.getDispatcherServlet().getServletContext();
        MockHttpServletRequest request = get("/auth").buildRequest(servletContext);
        MockHttpServletResponse response = SessionManagementConfigTests.request(request, this.spring.getContext());
        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_MOVED_TEMPORARILY);
        assertThat(request.getSession(false)).isNotNull();
    }

    @Test
    public void requestWhenCreateSessionIsSetToIfRequiredThenCreatesSessionOnLogin() throws Exception {
        this.spring.configLocations(this.xml("CreateSessionIfRequired")).autowire();
        ServletContext servletContext = this.mvc.getDispatcherServlet().getServletContext();
        MockHttpServletRequest request = post("/login").param("username", "user").param("password", "password").buildRequest(servletContext);
        request = csrf().postProcessRequest(request);
        MockHttpServletResponse response = SessionManagementConfigTests.request(request, this.spring.getContext());
        assertThat(response.getStatus()).isEqualTo(HttpStatus.SC_MOVED_TEMPORARILY);
        assertThat(request.getSession(false)).isNotNull();
    }

    /**
     * SEC-1208
     */
    @Test
    public void requestWhenRejectingUserBasedOnMaxSessionsExceededThenDoesNotCreateSession() throws Exception {
        this.spring.configLocations(this.xml("Sec1208")).autowire();
        this.mvc.perform(get("/auth").with(httpBasic("user", "password"))).andExpect(status().isOk()).andExpect(SessionManagementConfigTests.session());
        this.mvc.perform(get("/auth").with(httpBasic("user", "password"))).andExpect(status().isUnauthorized()).andExpect(SessionManagementConfigTests.session().exists(false));
    }

    /**
     * SEC-2137
     */
    @Test
    public void requestWhenSessionFixationProtectionDisabledAndConcurrencyControlEnabledThenSessionNotInvalidated() throws Exception {
        this.spring.configLocations(this.xml("Sec2137")).autowire();
        MockHttpSession session = new MockHttpSession();
        this.mvc.perform(get("/auth").session(session).with(httpBasic("user", "password"))).andExpect(status().isOk()).andExpect(SessionManagementConfigTests.session().id(session.getId()));
    }

    @Test
    public void autowireWhenExportingSessionRegistryBeanThenAvailableForWiring() {
        this.spring.configLocations(this.xml("ConcurrencyControlSessionRegistryAlias")).autowire();
        this.sessionRegistryIsValid();
    }

    @Test
    public void requestWhenExpiredUrlIsSetThenInvalidatesSessionAndRedirects() throws Exception {
        this.spring.configLocations(this.xml("ConcurrencyControlExpiredUrl")).autowire();
        this.mvc.perform(get("/auth").session(this.expiredSession()).with(httpBasic("user", "password"))).andExpect(redirectedUrl("/expired")).andExpect(SessionManagementConfigTests.session().exists(false));
    }

    @Test
    public void requestWhenConcurrencyControlAndCustomLogoutHandlersAreSetThenAllAreInvokedWhenSessionExpires() throws Exception {
        this.spring.configLocations(this.xml("ConcurrencyControlLogoutAndRememberMeHandlers")).autowire();
        this.mvc.perform(get("/auth").session(this.expiredSession()).with(httpBasic("user", "password"))).andExpect(status().isOk()).andExpect(cookie().maxAge("testCookie", 0)).andExpect(cookie().exists("rememberMeCookie")).andExpect(SessionManagementConfigTests.session().valid(true));
    }

    @Test
    public void requestWhenConcurrencyControlAndRememberMeAreSetThenInvokedWhenSessionExpires() throws Exception {
        this.spring.configLocations(this.xml("ConcurrencyControlRememberMeHandler")).autowire();
        this.mvc.perform(get("/auth").session(this.expiredSession()).with(httpBasic("user", "password"))).andExpect(status().isOk()).andExpect(cookie().exists("rememberMeCookie")).andExpect(SessionManagementConfigTests.session().exists(false));
    }

    /**
     * SEC-2057
     */
    @Test
    public void autowireWhenConcurrencyControlIsSetThenLogoutHandlersGetAuthenticationObject() throws Exception {
        this.spring.configLocations(this.xml("ConcurrencyControlCustomLogoutHandler")).autowire();
        MvcResult result = this.mvc.perform(get("/auth").with(httpBasic("user", "password"))).andExpect(SessionManagementConfigTests.session()).andReturn();
        MockHttpSession session = ((MockHttpSession) (result.getRequest().getSession(false)));
        SessionRegistry sessionRegistry = this.spring.getContext().getBean(SessionRegistry.class);
        sessionRegistry.getSessionInformation(session.getId()).expireNow();
        this.mvc.perform(get("/auth").session(session)).andExpect(header().string("X-Username", "user"));
    }

    @Test
    public void requestWhenConcurrencyControlIsSetThenDefaultsToResponseBodyExpirationResponse() throws Exception {
        this.spring.configLocations(this.xml("ConcurrencyControlSessionRegistryAlias")).autowire();
        this.mvc.perform(get("/auth").session(this.expiredSession()).with(httpBasic("user", "password"))).andExpect(content().string(("This session has been expired (possibly due to multiple concurrent " + "logins being attempted as the same user).")));
    }

    @Test
    public void requestWhenCustomSessionAuthenticationStrategyThenInvokesOnAuthentication() throws Exception {
        this.spring.configLocations(this.xml("SessionAuthenticationStrategyRef")).autowire();
        this.mvc.perform(get("/auth").with(httpBasic("user", "password"))).andExpect(status().isIAmATeapot());
    }

    @Test
    public void autowireWhenSessionRegistryRefIsSetThenAvailableForWiring() {
        this.spring.configLocations(this.xml("ConcurrencyControlSessionRegistryRef")).autowire();
        this.sessionRegistryIsValid();
    }

    @Test
    public void requestWhenMaxSessionsIsSetThenErrorsWhenExceeded() throws Exception {
        this.spring.configLocations(this.xml("ConcurrencyControlMaxSessions")).autowire();
        this.mvc.perform(get("/auth").with(httpBasic("user", "password"))).andExpect(status().isOk());
        this.mvc.perform(get("/auth").with(httpBasic("user", "password"))).andExpect(status().isOk());
        this.mvc.perform(get("/auth").with(httpBasic("user", "password"))).andExpect(redirectedUrl("/max-exceeded"));
    }

    @Test
    public void autowireWhenSessionFixationProtectionIsNoneAndCsrfDisabledThenSessionManagementFilterIsNotWired() {
        this.spring.configLocations(this.xml("NoSessionManagementFilter")).autowire();
        assertThat(this.getFilter(SessionManagementFilter.class)).isNull();
    }

    @Test
    public void requestWhenSessionFixationProtectionIsNoneThenSessionNotInvalidated() throws Exception {
        this.spring.configLocations(this.xml("SessionFixationProtectionNone")).autowire();
        MockHttpSession session = new MockHttpSession();
        String sessionId = session.getId();
        this.mvc.perform(get("/auth").session(session).with(httpBasic("user", "password"))).andExpect(SessionManagementConfigTests.session().id(sessionId));
    }

    @Test
    public void requestWhenSessionFixationProtectionIsMigrateSessionThenSessionIsReplaced() throws Exception {
        this.spring.configLocations(this.xml("SessionFixationProtectionMigrateSession")).autowire();
        MockHttpSession session = new MockHttpSession();
        String sessionId = session.getId();
        MvcResult result = this.mvc.perform(get("/auth").session(session).with(httpBasic("user", "password"))).andExpect(SessionManagementConfigTests.session()).andReturn();
        assertThat(result.getRequest().getSession(false).getId()).isNotEqualTo(sessionId);
    }

    @Test
    public void requestWhenSessionFixationProtectionIsNoneAndInvalidSessionUrlIsSetThenStillRedirectsOnInvalidSession() throws Exception {
        this.spring.configLocations(this.xml("SessionFixationProtectionNoneWithInvalidSessionUrl")).autowire();
        this.mvc.perform(get("/auth").with(( request) -> {
            request.setRequestedSessionId("1");
            request.setRequestedSessionIdValid(false);
            return request;
        })).andExpect(redirectedUrl("/timeoutUrl"));
    }

    static class TeapotSessionAuthenticationStrategy implements SessionAuthenticationStrategy {
        @Override
        public void onAuthentication(Authentication authentication, HttpServletRequest request, HttpServletResponse response) throws SessionAuthenticationException {
            response.setStatus(I_AM_A_TEAPOT.value());
        }
    }

    static class CustomRememberMeServices implements RememberMeServices , LogoutHandler {
        @Override
        public Authentication autoLogin(HttpServletRequest request, HttpServletResponse response) {
            return null;
        }

        @Override
        public void loginFail(HttpServletRequest request, HttpServletResponse response) {
        }

        @Override
        public void loginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication successfulAuthentication) {
        }

        @Override
        public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
            response.addHeader("X-Username", authentication.getName());
        }
    }

    @RestController
    static class BasicController {
        @GetMapping("/")
        public String ok() {
            return "ok";
        }

        @GetMapping("/auth")
        public String auth(Principal principal) {
            return principal.getName();
        }
    }

    private static class SessionResultMatcher implements ResultMatcher {
        private String id;

        private Boolean valid;

        private Boolean exists = true;

        public ResultMatcher exists(boolean exists) {
            this.exists = exists;
            return this;
        }

        public ResultMatcher valid(boolean valid) {
            this.valid = valid;
            return this.exists(true);
        }

        public ResultMatcher id(String id) {
            this.id = id;
            return this.exists(true);
        }

        @Override
        public void match(MvcResult result) {
            if (!(this.exists)) {
                assertThat(result.getRequest().getSession(false)).isNull();
                return;
            }
            assertThat(result.getRequest().getSession(false)).isNotNull();
            MockHttpSession session = ((MockHttpSession) (result.getRequest().getSession(false)));
            if ((this.valid) != null) {
                if (this.valid) {
                    assertThat(session.isInvalid()).isFalse();
                } else {
                    assertThat(session.isInvalid()).isTrue();
                }
            }
            if ((this.id) != null) {
                assertThat(session.getId()).isEqualTo(this.id);
            }
        }
    }

    private static class EncodeUrlDenyingHttpServletResponseWrapper extends HttpServletResponseWrapper {
        public EncodeUrlDenyingHttpServletResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        @Override
        public String encodeURL(String url) {
            throw new RuntimeException("Unexpected invocation of encodeURL");
        }

        @Override
        public String encodeRedirectURL(String url) {
            throw new RuntimeException("Unexpected invocation of encodeURL");
        }

        @Override
        public String encodeUrl(String url) {
            throw new RuntimeException("Unexpected invocation of encodeURL");
        }

        @Override
        public String encodeRedirectUrl(String url) {
            throw new RuntimeException("Unexpected invocation of encodeURL");
        }
    }
}

