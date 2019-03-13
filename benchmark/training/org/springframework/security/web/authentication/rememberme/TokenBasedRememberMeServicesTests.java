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
package org.springframework.security.web.authentication.rememberme;


import java.util.Date;
import javax.servlet.http.Cookie;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;


/**
 * Tests
 * {@link org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices}
 * .
 *
 * @author Ben Alex
 */
public class TokenBasedRememberMeServicesTests {
    private UserDetailsService uds;

    private UserDetails user = new org.springframework.security.core.userdetails.User("someone", "password", true, true, true, true, AuthorityUtils.createAuthorityList("ROLE_ABC"));

    private TokenBasedRememberMeServices.TokenBasedRememberMeServices services;

    @Test
    public void autoLoginReturnsNullIfNoCookiePresented() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        Authentication result = services.autoLogin(new MockHttpServletRequest(), response);
        assertThat(result).isNull();
        // No cookie set
        assertThat(response.getCookie(SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY)).isNull();
    }

    @Test
    public void autoLoginIgnoresUnrelatedCookie() throws Exception {
        Cookie cookie = new Cookie("unrelated_cookie", "foobar");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(cookie);
        MockHttpServletResponse response = new MockHttpServletResponse();
        Authentication result = services.autoLogin(request, response);
        assertThat(result).isNull();
        assertThat(response.getCookie(SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY)).isNull();
    }

    @Test
    public void autoLoginReturnsNullForExpiredCookieAndClearsCookie() throws Exception {
        Cookie cookie = new Cookie(SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY, generateCorrectCookieContentForToken(((System.currentTimeMillis()) - 1000000), "someone", "password", "key"));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(cookie);
        MockHttpServletResponse response = new MockHttpServletResponse();
        assertThat(services.autoLogin(request, response)).isNull();
        Cookie returnedCookie = response.getCookie(SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY);
        assertThat(returnedCookie).isNotNull();
        assertThat(returnedCookie.getMaxAge()).isZero();
    }

    @Test
    public void autoLoginReturnsNullAndClearsCookieIfMissingThreeTokensInCookieValue() throws Exception {
        Cookie cookie = new Cookie(SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY, new String(Base64.encodeBase64("x".getBytes())));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(cookie);
        MockHttpServletResponse response = new MockHttpServletResponse();
        assertThat(services.autoLogin(request, response)).isNull();
        Cookie returnedCookie = response.getCookie(SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY);
        assertThat(returnedCookie).isNotNull();
        assertThat(returnedCookie.getMaxAge()).isZero();
    }

    @Test
    public void autoLoginClearsNonBase64EncodedCookie() throws Exception {
        Cookie cookie = new Cookie(SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY, "NOT_BASE_64_ENCODED");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(cookie);
        MockHttpServletResponse response = new MockHttpServletResponse();
        assertThat(services.autoLogin(request, response)).isNull();
        Cookie returnedCookie = response.getCookie(SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY);
        assertThat(returnedCookie).isNotNull();
        assertThat(returnedCookie.getMaxAge()).isZero();
    }

    @Test
    public void autoLoginClearsCookieIfSignatureBlocksDoesNotMatchExpectedValue() throws Exception {
        udsWillReturnUser();
        Cookie cookie = new Cookie(SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY, generateCorrectCookieContentForToken(((System.currentTimeMillis()) + 1000000), "someone", "password", "WRONG_KEY"));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(cookie);
        MockHttpServletResponse response = new MockHttpServletResponse();
        assertThat(services.autoLogin(request, response)).isNull();
        Cookie returnedCookie = response.getCookie(SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY);
        assertThat(returnedCookie).isNotNull();
        assertThat(returnedCookie.getMaxAge()).isZero();
    }

    @Test
    public void autoLoginClearsCookieIfTokenDoesNotContainANumberInCookieValue() throws Exception {
        Cookie cookie = new Cookie(SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY, new String(Base64.encodeBase64("username:NOT_A_NUMBER:signature".getBytes())));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(cookie);
        MockHttpServletResponse response = new MockHttpServletResponse();
        assertThat(services.autoLogin(request, response)).isNull();
        Cookie returnedCookie = response.getCookie(SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY);
        assertThat(returnedCookie).isNotNull();
        assertThat(returnedCookie.getMaxAge()).isZero();
    }

    @Test
    public void autoLoginClearsCookieIfUserNotFound() throws Exception {
        udsWillThrowNotFound();
        Cookie cookie = new Cookie(SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY, generateCorrectCookieContentForToken(((System.currentTimeMillis()) + 1000000), "someone", "password", "key"));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(cookie);
        MockHttpServletResponse response = new MockHttpServletResponse();
        assertThat(services.autoLogin(request, response)).isNull();
        Cookie returnedCookie = response.getCookie(SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY);
        assertThat(returnedCookie).isNotNull();
        assertThat(returnedCookie.getMaxAge()).isZero();
    }

    @Test
    public void autoLoginWithValidTokenAndUserSucceeds() throws Exception {
        udsWillReturnUser();
        Cookie cookie = new Cookie(SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY, generateCorrectCookieContentForToken(((System.currentTimeMillis()) + 1000000), "someone", "password", "key"));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(cookie);
        MockHttpServletResponse response = new MockHttpServletResponse();
        Authentication result = services.autoLogin(request, response);
        assertThat(result).isNotNull();
        assertThat(result.getPrincipal()).isEqualTo(user);
    }

    @Test
    public void testGettersSetters() {
        assertThat(services.getUserDetailsService()).isEqualTo(uds);
        assertThat(services.getKey()).isEqualTo("key");
        assertThat(services.getParameter()).isEqualTo(DEFAULT_PARAMETER);
        services.setParameter("some_param");
        assertThat(services.getParameter()).isEqualTo("some_param");
        services.setTokenValiditySeconds(12);
        assertThat(services.getTokenValiditySeconds()).isEqualTo(12);
    }

    @Test
    public void loginFailClearsCookie() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        services.loginFail(request, response);
        Cookie cookie = response.getCookie(SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY);
        assertThat(cookie).isNotNull();
        assertThat(cookie.getMaxAge()).isZero();
    }

    @Test
    public void loginSuccessIgnoredIfParameterNotSetOrFalse() {
        TokenBasedRememberMeServices.TokenBasedRememberMeServices services = new TokenBasedRememberMeServices.TokenBasedRememberMeServices("key", new AbstractRememberMeServicesTests.MockUserDetailsService(null, false));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter(DEFAULT_PARAMETER, "false");
        MockHttpServletResponse response = new MockHttpServletResponse();
        services.loginSuccess(request, response, new TestingAuthenticationToken("someone", "password", "ROLE_ABC"));
        Cookie cookie = response.getCookie(SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY);
        assertThat(cookie).isNull();
    }

    @Test
    public void loginSuccessNormalWithNonUserDetailsBasedPrincipalSetsExpectedCookie() {
        // SEC-822
        services.setTokenValiditySeconds(500000000);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter(TokenBasedRememberMeServices.DEFAULT_PARAMETER, "true");
        MockHttpServletResponse response = new MockHttpServletResponse();
        services.loginSuccess(request, response, new TestingAuthenticationToken("someone", "password", "ROLE_ABC"));
        Cookie cookie = response.getCookie(SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY);
        String expiryTime = services.decodeCookie(cookie.getValue())[1];
        long expectedExpiryTime = 1000L * 500000000;
        expectedExpiryTime += System.currentTimeMillis();
        assertThat(((Long.parseLong(expiryTime)) > (expectedExpiryTime - 10000))).isTrue();
        assertThat(cookie).isNotNull();
        assertThat(cookie.getMaxAge()).isEqualTo(services.getTokenValiditySeconds());
        assertThat(Base64.isArrayByteBase64(cookie.getValue().getBytes())).isTrue();
        assertThat(new Date().before(new Date(determineExpiryTimeFromBased64EncodedToken(cookie.getValue())))).isTrue();
    }

    @Test
    public void loginSuccessNormalWithUserDetailsBasedPrincipalSetsExpectedCookie() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter(TokenBasedRememberMeServices.DEFAULT_PARAMETER, "true");
        MockHttpServletResponse response = new MockHttpServletResponse();
        services.loginSuccess(request, response, new TestingAuthenticationToken("someone", "password", "ROLE_ABC"));
        Cookie cookie = response.getCookie(SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY);
        assertThat(cookie).isNotNull();
        assertThat(cookie.getMaxAge()).isEqualTo(services.getTokenValiditySeconds());
        assertThat(Base64.isArrayByteBase64(cookie.getValue().getBytes())).isTrue();
        assertThat(new Date().before(new Date(determineExpiryTimeFromBased64EncodedToken(cookie.getValue())))).isTrue();
    }

    // SEC-933
    @Test
    public void obtainPasswordReturnsNullForTokenWithNullCredentials() throws Exception {
        TestingAuthenticationToken token = new TestingAuthenticationToken("username", null);
        assertThat(services.retrievePassword(token)).isNull();
    }

    // SEC-949
    @Test
    public void negativeValidityPeriodIsSetOnCookieButExpiryTimeRemainsAtTwoWeeks() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter(DEFAULT_PARAMETER, "true");
        MockHttpServletResponse response = new MockHttpServletResponse();
        services.setTokenValiditySeconds((-1));
        services.loginSuccess(request, response, new TestingAuthenticationToken("someone", "password", "ROLE_ABC"));
        Cookie cookie = response.getCookie(SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY);
        assertThat(cookie).isNotNull();
        // Check the expiry time is within 50ms of two weeks from current time
        assertThat((((determineExpiryTimeFromBased64EncodedToken(cookie.getValue())) - (System.currentTimeMillis())) > ((TWO_WEEKS_S) - 50))).isTrue();
        assertThat(cookie.getMaxAge()).isEqualTo((-1));
        assertThat(Base64.isArrayByteBase64(cookie.getValue().getBytes())).isTrue();
    }
}

