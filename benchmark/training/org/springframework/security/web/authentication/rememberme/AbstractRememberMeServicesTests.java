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
package org.springframework.security.web.authentication.rememberme;


import AbstractRememberMeServices.SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.ReflectionUtils;

import static AbstractRememberMeServices.SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY;


/**
 *
 *
 * @author Luke Taylor
 */
@SuppressWarnings("unchecked")
@RunWith(PowerMockRunner.class)
@PrepareOnlyThisForTest(ReflectionUtils.class)
@PowerMockIgnore("javax.security.auth.*")
public class AbstractRememberMeServicesTests {
    static User joe = new User("joe", "password", true, true, true, true, AuthorityUtils.createAuthorityList("ROLE_A"));

    AbstractRememberMeServicesTests.MockUserDetailsService uds;

    @Test(expected = InvalidCookieException.class)
    public void nonBase64CookieShouldBeDetected() {
        decodeCookie("nonBase64CookieValue%");
    }

    @Test
    public void setAndGetAreConsistent() throws Exception {
        AbstractRememberMeServicesTests.MockRememberMeServices services = new AbstractRememberMeServicesTests.MockRememberMeServices(uds);
        assertThat(getCookieName()).isNotNull();
        assertThat(getParameter()).isNotNull();
        assertThat(getKey()).isEqualTo("xxxx");
        setParameter("rm");
        assertThat(getParameter()).isEqualTo("rm");
        setCookieName("kookie");
        assertThat(getCookieName()).isEqualTo("kookie");
        setTokenValiditySeconds(600);
        assertThat(getTokenValiditySeconds()).isEqualTo(600);
        assertThat(getUserDetailsService()).isSameAs(uds);
        AuthenticationDetailsSource ads = Mockito.mock(AuthenticationDetailsSource.class);
        services.setAuthenticationDetailsSource(ads);
        assertThat(getAuthenticationDetailsSource()).isSameAs(ads);
        afterPropertiesSet();
    }

    @Test
    public void cookieShouldBeCorrectlyEncodedAndDecoded() throws Exception {
        String[] cookie = new String[]{ "name:with:colon", "cookie", "tokens", "blah" };
        AbstractRememberMeServicesTests.MockRememberMeServices services = new AbstractRememberMeServicesTests.MockRememberMeServices(uds);
        String encoded = encodeCookie(cookie);
        // '=' aren't allowed in version 0 cookies.
        assertThat(encoded).doesNotEndWith("=");
        String[] decoded = services.decodeCookie(encoded);
        assertThat(decoded).containsExactly("name:with:colon", "cookie", "tokens", "blah");
    }

    @Test
    public void cookieWithOpenIDidentifierAsNameIsEncodedAndDecoded() throws Exception {
        String[] cookie = new String[]{ "http://id.openid.zz", "cookie", "tokens", "blah" };
        AbstractRememberMeServicesTests.MockRememberMeServices services = new AbstractRememberMeServicesTests.MockRememberMeServices(uds);
        String[] decoded = services.decodeCookie(services.encodeCookie(cookie));
        assertThat(decoded).hasSize(4);
        assertThat(decoded[0]).isEqualTo("http://id.openid.zz");
        // Check https (SEC-1410)
        cookie[0] = "https://id.openid.zz";
        decoded = services.decodeCookie(services.encodeCookie(cookie));
        assertThat(decoded).hasSize(4);
        assertThat(decoded[0]).isEqualTo("https://id.openid.zz");
    }

    @Test
    public void autoLoginShouldReturnNullIfNoLoginCookieIsPresented() {
        AbstractRememberMeServicesTests.MockRememberMeServices services = new AbstractRememberMeServicesTests.MockRememberMeServices(uds);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        assertThat(services.autoLogin(request, response)).isNull();
        // shouldn't try to invalidate our cookie
        assertThat(response.getCookie(SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY)).isNull();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        // set non-login cookie
        request.setCookies(new Cookie("mycookie", "cookie"));
        assertThat(services.autoLogin(request, response)).isNull();
        assertThat(response.getCookie(SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY)).isNull();
    }

    @Test
    public void successfulAutoLoginReturnsExpectedAuthentication() throws Exception {
        AbstractRememberMeServicesTests.MockRememberMeServices services = new AbstractRememberMeServicesTests.MockRememberMeServices(uds);
        afterPropertiesSet();
        assertThat(getUserDetailsService()).isNotNull();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(createLoginCookie("cookie:1:2"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        Authentication result = services.autoLogin(request, response);
        assertThat(result).isNotNull();
    }

    @Test
    public void autoLoginShouldFailIfCookieIsNotBase64() throws Exception {
        AbstractRememberMeServicesTests.MockRememberMeServices services = new AbstractRememberMeServicesTests.MockRememberMeServices(uds);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setCookies(new Cookie(SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY, "ZZZ"));
        Authentication result = services.autoLogin(request, response);
        assertThat(result).isNull();
        assertCookieCancelled(response);
    }

    @Test
    public void autoLoginShouldFailIfCookieIsEmpty() throws Exception {
        AbstractRememberMeServicesTests.MockRememberMeServices services = new AbstractRememberMeServicesTests.MockRememberMeServices(uds);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setCookies(new Cookie(SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY, ""));
        Authentication result = services.autoLogin(request, response);
        assertThat(result).isNull();
        assertCookieCancelled(response);
    }

    @Test
    public void autoLoginShouldFailIfInvalidCookieExceptionIsRaised() {
        AbstractRememberMeServicesTests.MockRememberMeServices services = new AbstractRememberMeServicesTests.MockRememberMeServices(new AbstractRememberMeServicesTests.MockUserDetailsService(AbstractRememberMeServicesTests.joe, true));
        MockHttpServletRequest request = new MockHttpServletRequest();
        // Wrong number of tokens
        request.setCookies(createLoginCookie("cookie:1"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        Authentication result = services.autoLogin(request, response);
        assertThat(result).isNull();
        assertCookieCancelled(response);
    }

    @Test
    public void autoLoginShouldFailIfUserNotFound() {
        uds.setThrowException(true);
        AbstractRememberMeServicesTests.MockRememberMeServices services = new AbstractRememberMeServicesTests.MockRememberMeServices(uds);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(createLoginCookie("cookie:1:2"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        Authentication result = services.autoLogin(request, response);
        assertThat(result).isNull();
        assertCookieCancelled(response);
    }

    @Test
    public void autoLoginShouldFailIfUserAccountIsLocked() {
        AbstractRememberMeServicesTests.MockRememberMeServices services = new AbstractRememberMeServicesTests.MockRememberMeServices(uds);
        services.setUserDetailsChecker(new AccountStatusUserDetailsChecker());
        uds.toReturn = new User("joe", "password", false, true, true, true, AbstractRememberMeServicesTests.joe.getAuthorities());
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(createLoginCookie("cookie:1:2"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        Authentication result = services.autoLogin(request, response);
        assertThat(result).isNull();
        assertCookieCancelled(response);
    }

    @Test
    public void loginFailShouldCancelCookie() {
        uds.setThrowException(true);
        AbstractRememberMeServicesTests.MockRememberMeServices services = new AbstractRememberMeServicesTests.MockRememberMeServices(uds);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContextPath("contextpath");
        request.setCookies(createLoginCookie("cookie:1:2"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        services.loginFail(request, response);
        assertCookieCancelled(response);
    }

    @Test
    public void logoutShouldCancelCookie() throws Exception {
        AbstractRememberMeServicesTests.MockRememberMeServices services = new AbstractRememberMeServicesTests.MockRememberMeServices(uds);
        setCookieDomain("spring.io");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContextPath("contextpath");
        request.setCookies(createLoginCookie("cookie:1:2"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        services.logout(request, response, Mockito.mock(Authentication.class));
        // Try again with null Authentication
        response = new MockHttpServletResponse();
        services.logout(request, response, null);
        assertCookieCancelled(response);
        Cookie returnedCookie = response.getCookie(SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY);
        assertThat(returnedCookie.getDomain()).isEqualTo("spring.io");
    }

    @Test(expected = CookieTheftException.class)
    public void cookieTheftExceptionShouldBeRethrown() {
        AbstractRememberMeServicesTests.MockRememberMeServices services = new AbstractRememberMeServicesTests.MockRememberMeServices(uds) {
            protected UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request, HttpServletResponse response) {
                throw new CookieTheftException("Pretending cookie was stolen");
            }
        };
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(createLoginCookie("cookie:1:2"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        services.autoLogin(request, response);
    }

    @Test
    public void loginSuccessCallsOnLoginSuccessCorrectly() {
        AbstractRememberMeServicesTests.MockRememberMeServices services = new AbstractRememberMeServicesTests.MockRememberMeServices(uds);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Authentication auth = new UsernamePasswordAuthenticationToken("joe", "password");
        // No parameter set
        services.loginSuccess(request, response, auth);
        assertThat(services.loginSuccessCalled).isFalse();
        // Parameter set to true
        services = new AbstractRememberMeServicesTests.MockRememberMeServices(uds);
        request.setParameter(DEFAULT_PARAMETER, "true");
        services.loginSuccess(request, response, auth);
        assertThat(services.loginSuccessCalled).isTrue();
        // Different parameter name, set to true
        services = new AbstractRememberMeServicesTests.MockRememberMeServices(uds);
        setParameter("my_parameter");
        request.setParameter("my_parameter", "true");
        services.loginSuccess(request, response, auth);
        assertThat(services.loginSuccessCalled).isTrue();
        // Parameter set to false
        services = new AbstractRememberMeServicesTests.MockRememberMeServices(uds);
        request.setParameter(DEFAULT_PARAMETER, "false");
        services.loginSuccess(request, response, auth);
        assertThat(services.loginSuccessCalled).isFalse();
        // alwaysRemember set to true
        services = new AbstractRememberMeServicesTests.MockRememberMeServices(uds);
        setAlwaysRemember(true);
        services.loginSuccess(request, response, auth);
        assertThat(services.loginSuccessCalled).isTrue();
    }

    @Test
    public void setCookieUsesCorrectNamePathAndValue() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setContextPath("contextpath");
        AbstractRememberMeServicesTests.MockRememberMeServices services = new AbstractRememberMeServicesTests.MockRememberMeServices(uds) {
            protected String encodeCookie(String[] cookieTokens) {
                return cookieTokens[0];
            }
        };
        setCookieName("mycookiename");
        services.setCookie(new String[]{ "mycookie" }, 1000, request, response);
        Cookie cookie = response.getCookie("mycookiename");
        assertThat(cookie).isNotNull();
        assertThat(cookie.getValue()).isEqualTo("mycookie");
        assertThat(cookie.getName()).isEqualTo("mycookiename");
        assertThat(cookie.getPath()).isEqualTo("contextpath");
        assertThat(cookie.getSecure()).isFalse();
    }

    @Test
    public void setCookieSetsSecureFlagIfConfigured() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setContextPath("contextpath");
        AbstractRememberMeServicesTests.MockRememberMeServices services = new AbstractRememberMeServicesTests.MockRememberMeServices(uds) {
            protected String encodeCookie(String[] cookieTokens) {
                return cookieTokens[0];
            }
        };
        setUseSecureCookie(true);
        services.setCookie(new String[]{ "mycookie" }, 1000, request, response);
        Cookie cookie = response.getCookie(SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY);
        assertThat(cookie.getSecure()).isTrue();
    }

    @Test
    public void setCookieSetsIsHttpOnlyFlagByDefault() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setContextPath("contextpath");
        AbstractRememberMeServicesTests.MockRememberMeServices services = new AbstractRememberMeServicesTests.MockRememberMeServices(uds);
        services.setCookie(new String[]{ "mycookie" }, 1000, request, response);
        Cookie cookie = response.getCookie(SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY);
        assertThat(cookie.isHttpOnly()).isTrue();
    }

    // SEC-2791
    @Test
    public void setCookieMaxAge0VersionSet() {
        AbstractRememberMeServicesTests.MockRememberMeServices services = new AbstractRememberMeServicesTests.MockRememberMeServices();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        services.setCookie(new String[]{ "value" }, 0, request, response);
        Cookie cookie = response.getCookie(SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY);
        assertThat(cookie.getVersion()).isEqualTo(1);
    }

    // SEC-2791
    @Test
    public void setCookieMaxAgeNegativeVersionSet() {
        AbstractRememberMeServicesTests.MockRememberMeServices services = new AbstractRememberMeServicesTests.MockRememberMeServices();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        services.setCookie(new String[]{ "value" }, (-1), request, response);
        Cookie cookie = response.getCookie(SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY);
        assertThat(cookie.getVersion()).isEqualTo(1);
    }

    // SEC-2791
    @Test
    public void setCookieMaxAge1VersionSet() {
        AbstractRememberMeServicesTests.MockRememberMeServices services = new AbstractRememberMeServicesTests.MockRememberMeServices();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        services.setCookie(new String[]{ "value" }, 1, request, response);
        Cookie cookie = response.getCookie(SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY);
        assertThat(cookie.getVersion()).isZero();
    }

    @Test
    public void setCookieDomainValue() {
        AbstractRememberMeServicesTests.MockRememberMeServices services = new AbstractRememberMeServicesTests.MockRememberMeServices();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        setCookieName("mycookiename");
        setCookieDomain("spring.io");
        services.setCookie(new String[]{ "mycookie" }, 1000, request, response);
        Cookie cookie = response.getCookie("mycookiename");
        assertThat(cookie).isNotNull();
        assertThat(cookie.getDomain()).isEqualTo("spring.io");
    }

    // ~ Inner Classes
    // ==================================================================================================
    static class MockRememberMeServices extends AbstractRememberMeServices {
        boolean loginSuccessCalled;

        MockRememberMeServices(String key, UserDetailsService userDetailsService) {
            super(key, userDetailsService);
        }

        MockRememberMeServices(UserDetailsService userDetailsService) {
            super("xxxx", userDetailsService);
        }

        MockRememberMeServices() {
            this(new AbstractRememberMeServicesTests.MockUserDetailsService(null, false));
        }

        protected void onLoginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication successfulAuthentication) {
            loginSuccessCalled = true;
        }

        protected UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request, HttpServletResponse response) throws RememberMeAuthenticationException {
            if ((cookieTokens.length) != 3) {
                throw new InvalidCookieException("deliberate exception");
            }
            UserDetails user = getUserDetailsService().loadUserByUsername("joe");
            return user;
        }
    }

    public static class MockUserDetailsService implements UserDetailsService {
        private UserDetails toReturn;

        private boolean throwException;

        public MockUserDetailsService() {
            this(null, false);
        }

        public MockUserDetailsService(UserDetails toReturn, boolean throwException) {
            this.toReturn = toReturn;
            this.throwException = throwException;
        }

        public UserDetails loadUserByUsername(String username) {
            if (throwException) {
                throw new UsernameNotFoundException("as requested by mock");
            }
            return toReturn;
        }

        public void setThrowException(boolean value) {
            this.throwException = value;
        }
    }
}

