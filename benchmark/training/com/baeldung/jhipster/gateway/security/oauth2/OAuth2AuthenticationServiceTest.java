package com.baeldung.jhipster.gateway.security.oauth2;


import HttpMethod.GET;
import OAuth2CookieHelper.ACCESS_TOKEN_COOKIE;
import OAuth2CookieHelper.REFRESH_TOKEN_COOKIE;
import OAuth2CookieHelper.SESSION_TOKEN_COOKIE;
import com.baeldung.jhipster.gateway.config.oauth2.OAuth2Properties;
import com.baeldung.jhipster.gateway.web.filter.RefreshTokenFilter;
import com.baeldung.jhipster.gateway.web.rest.errors.InvalidPasswordException;
import io.github.jhipster.config.JHipsterProperties;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.client.RestTemplate;

import static OAuth2CookieHelper.ACCESS_TOKEN_COOKIE;
import static OAuth2CookieHelper.REFRESH_TOKEN_COOKIE;
import static OAuth2CookieHelper.SESSION_TOKEN_COOKIE;


/**
 * Test password and refresh token grants.
 *
 * @see OAuth2AuthenticationService
 */
@RunWith(MockitoJUnitRunner.class)
public class OAuth2AuthenticationServiceTest {
    public static final String CLIENT_AUTHORIZATION = "Basic d2ViX2FwcDpjaGFuZ2VpdA==";

    public static final String ACCESS_TOKEN_VALUE = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE0OTQyNzI4NDQsInVzZXJfbmFtZSI6InVzZXIiLCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXSwianRpIjoiNzc1ZTJkYWUtYWYzZi00YTdhLWExOTktNzNiZTU1MmIxZDVkIiwiY2xpZW50X2lkIjoid2ViX2FwcCIsInNjb3BlIjpbIm9wZW5pZCJdfQ.gEK0YcX2IpkpxnkxXXHQ4I0xzTjcy7edqb89ukYE0LPe7xUcZVwkkCJF_nBxsGJh2jtA6NzNLfY5zuL6nP7uoAq3fmvsyrcyR2qPk8JuuNzGtSkICx3kPDRjAT4ST8SZdeh7XCbPVbySJ7ZmPlRWHyedzLA1wXN0NUf8yZYS4ELdUwVBYIXSjkNoKqfWm88cwuNr0g0teypjPtjDqCnXFt1pibwdfIXn479Y1neNAdvSpHcI4Ost-c7APCNxW2gqX-0BItZQearxRgKDdBQ7CGPAIky7dA0gPuKUpp_VCoqowKCXqkE9yKtRQGIISewtj2UkDRZePmzmYrUBXRzfYw";

    public static final String REFRESH_TOKEN_VALUE = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJ1c2VyIiwic2NvcGUiOlsib3BlbmlkIl0sImF0aSI6Ijc3NWUyZGFlLWFmM2YtNGE3YS1hMTk5LTczYmU1NTJiMWQ1ZCIsImV4cCI6MTQ5Njg2NDc0MywiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIl0sImp0aSI6IjhmYjI2YTllLTdjYzQtNDFlMi1hNzBjLTk4MDc0N2U2YWFiOSIsImNsaWVudF9pZCI6IndlYl9hcHAifQ.q1-Df9_AFO6TJNiLKV2YwTjRbnd7qcXv52skXYnog5siHYRoR6cPtm6TNQ04iDAoIHljTSTNnD6DS3bHk41mV55gsSVxGReL8VCb_R8ZmhVL4-5yr90sfms0wFp6lgD2bPmZ-TXiS2Oe9wcbNWagy5RsEplZ-sbXu3tjmDao4FN35ojPsXmUs84XnNQH3Y_-PY9GjZG0JEfLQIvE0J5BkXS18Z015GKyA6GBIoLhAGBQQYyG9m10ld_a9fD5SmCyCF72Jad_pfP1u8Z_WyvO-wrlBvm2x-zBthreVrXU5mOb9795wJEP-xaw3dXYGjht_grcW4vKUFtj61JgZk98CQ";

    public static final String EXPIRED_SESSION_TOKEN_VALUE = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJ1c2VyIiwic2NvcGUiOlsib3BlbmlkIl0sImF0aSI6IjE0NTkwYzdkLTQ5M2YtNDU0NS05MzlmLTg1ODM4ZjRmNzNmNSIsImV4cCI6MTQ5NTU3Mjg5MywiaWF0IjoxNDk1MzIwODkzLCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXSwianRpIjoiNzVhYTIxNzEtMzFmNi00MWJmLWExZGUtYWU0YTg1ZjZiMjEyIiwiY2xpZW50X2lkIjoid2ViX2FwcCJ9.gAH-yly7WAslQUeGhyHmjYXwQN3dluvoT84iOJ2mVWYGVlnDRsoxN3_d1ozqtiso9UM7dWpAr80o3gK7AyK-cO1GGBXa3lg0ETsbucFoqHLivgGZA2qVOsFlDq8E7DZENAbOWmywmhFUOogCfZ-BqsuFSi8waMLL-1qlhehBPuK1KzGxIZbjSVUFFFYTxoWPKi2NNTBzYSwwCV0ixj-gHyFC6Gl5ByA4EvYygGUZF2pACxs4tIRkmT90pXWCjWeKS9k9MlxZ7C4UHqyTRW-IYzqAm8OHdwsnXeu0GkFYc08gxoUuPcjMby8ziYLG5uWj0Ua0msmiSjoafzs-5xfH-Q";

    public static final String NEW_ACCESS_TOKEN_VALUE = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE0OTQyNzY2NDEsInVzZXJfbmFtZSI6InVzZXIiLCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXSwianRpIjoiYzIyY2YzMDgtZTIyYi00YzNjLWI5MjctOTYwYzA2YmY1ZmU0IiwiY2xpZW50X2lkIjoid2ViX2FwcCIsInNjb3BlIjpbIm9wZW5pZCJdfQ.IAhE39GCqWRUuXdWy-raOcE9NYXRhGiqkeJH649501LeqNPH5HtRUNWmudVRgwT52Bj7HcbJapMLGetKIMEASqC1-WARfcZ_PR0r7Kfg3OlFALWOH_oVT5kvi2H-QCoSAF9mRYK6abCh_tPk5KryVB5c7YxTMIXDT2nTsSexD8eNQOMBWRCg0RaLHZ9bKfeyVgncQJsu7-vTo1xJyh-keYpdNZ0TA2SjYJgezmB7gwW1Kmc7_83htr8VycG7XA_PuD9--yRNlrN0LtNHEBqNypZsOe6NvpKiNlodFYHlsU1CaumzcF9U7dpVanjIUKJ5VRWVUlSFY6JJ755W29VCTw";

    public static final String NEW_REFRESH_TOKEN_VALUE = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJ1c2VyIiwic2NvcGUiOlsib3BlbmlkIl0sImF0aSI6ImMyMmNmMzA4LWUyMmItNGMzYy1iOTI3LTk2MGMwNmJmNWZlNCIsImV4cCI6MTQ5Njg2ODU4MSwiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIl0sImp0aSI6ImU4YmZhZWJlLWYzMDItNGNjZS1hZGY1LWQ4MzE5OWM1MjBlOSIsImNsaWVudF9pZCI6IndlYl9hcHAifQ.OemWBUfc-2rl4t4VVqolYxul3L527PbSbX2Xvo7oyy3Vy5nmmblqp4hVGdTEjivrlldGVQX03ERbrA-oFkpmfWbBzLvnKS6AUq1MGjut6dXZJeiEqNYmiAABn6jSgK26S0k6b2ADgmf7mxJO8EBypb5sT1DMAbY5cbOe7r4ZG7zMTVSvlvjHTXp_FM8Y9i6nehLD4XDYY57cb_ZA89vAXNzvTAjoopDliExgR0bApG6nvvDEhEYgTS65lccEQocoev6bISJ3RvNYNPJxWcNPftKDp4HrEt2E2WP28K5IivRtQgDQNlQeormf1tp6AG-Oj__NXyAPM7yhAKXNy2zWdQ";

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private TokenStore tokenStore;

    private OAuth2TokenEndpointClient authorizationClient;

    private OAuth2AuthenticationService authenticationService;

    private RefreshTokenFilter refreshTokenFilter;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private OAuth2Properties oAuth2Properties;

    private JHipsterProperties jHipsterProperties;

    @Test
    public void testAuthenticationCookies() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServerName("www.test.com");
        request.addHeader("Authorization", OAuth2AuthenticationServiceTest.CLIENT_AUTHORIZATION);
        Map<String, String> params = new HashMap<>();
        params.put("username", "user");
        params.put("password", "user");
        params.put("rememberMe", "true");
        MockHttpServletResponse response = new MockHttpServletResponse();
        authenticationService.authenticate(request, response, params);
        // check that cookies are set correctly
        Cookie accessTokenCookie = response.getCookie(ACCESS_TOKEN_COOKIE);
        Assert.assertEquals(OAuth2AuthenticationServiceTest.ACCESS_TOKEN_VALUE, accessTokenCookie.getValue());
        Cookie refreshTokenCookie = response.getCookie(REFRESH_TOKEN_COOKIE);
        Assert.assertEquals(OAuth2AuthenticationServiceTest.REFRESH_TOKEN_VALUE, OAuth2CookieHelper.getRefreshTokenValue(refreshTokenCookie));
        Assert.assertTrue(OAuth2CookieHelper.isRememberMe(refreshTokenCookie));
    }

    @Test
    public void testAuthenticationNoRememberMe() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServerName("www.test.com");
        Map<String, String> params = new HashMap<>();
        params.put("username", "user");
        params.put("password", "user");
        params.put("rememberMe", "false");
        MockHttpServletResponse response = new MockHttpServletResponse();
        authenticationService.authenticate(request, response, params);
        // check that cookies are set correctly
        Cookie accessTokenCookie = response.getCookie(ACCESS_TOKEN_COOKIE);
        Assert.assertEquals(OAuth2AuthenticationServiceTest.ACCESS_TOKEN_VALUE, accessTokenCookie.getValue());
        Cookie refreshTokenCookie = response.getCookie(SESSION_TOKEN_COOKIE);
        Assert.assertEquals(OAuth2AuthenticationServiceTest.REFRESH_TOKEN_VALUE, OAuth2CookieHelper.getRefreshTokenValue(refreshTokenCookie));
        Assert.assertFalse(OAuth2CookieHelper.isRememberMe(refreshTokenCookie));
    }

    @Test
    public void testInvalidPassword() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServerName("www.test.com");
        Map<String, String> params = new HashMap<>();
        params.put("username", "user");
        params.put("password", "user2");
        params.put("rememberMe", "false");
        MockHttpServletResponse response = new MockHttpServletResponse();
        expectedException.expect(InvalidPasswordException.class);
        authenticationService.authenticate(request, response, params);
    }

    @Test
    public void testRefreshGrant() {
        MockHttpServletRequest request = OAuth2AuthenticationServiceTest.createMockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpServletRequest newRequest = refreshTokenFilter.refreshTokensIfExpiring(request, response);
        Cookie newAccessTokenCookie = response.getCookie(ACCESS_TOKEN_COOKIE);
        Assert.assertEquals(OAuth2AuthenticationServiceTest.NEW_ACCESS_TOKEN_VALUE, newAccessTokenCookie.getValue());
        Cookie newRefreshTokenCookie = response.getCookie(REFRESH_TOKEN_COOKIE);
        Assert.assertEquals(OAuth2AuthenticationServiceTest.NEW_REFRESH_TOKEN_VALUE, newRefreshTokenCookie.getValue());
        Cookie requestAccessTokenCookie = OAuth2CookieHelper.getAccessTokenCookie(newRequest);
        Assert.assertEquals(OAuth2AuthenticationServiceTest.NEW_ACCESS_TOKEN_VALUE, requestAccessTokenCookie.getValue());
    }

    @Test
    public void testSessionExpired() {
        MockHttpServletRequest request = new MockHttpServletRequest(GET.name(), "http://www.test.com");
        Cookie accessTokenCookie = new Cookie(ACCESS_TOKEN_COOKIE, OAuth2AuthenticationServiceTest.ACCESS_TOKEN_VALUE);
        Cookie refreshTokenCookie = new Cookie(SESSION_TOKEN_COOKIE, OAuth2AuthenticationServiceTest.EXPIRED_SESSION_TOKEN_VALUE);
        request.setCookies(accessTokenCookie, refreshTokenCookie);
        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpServletRequest newRequest = refreshTokenFilter.refreshTokensIfExpiring(request, response);
        // cookies in response are deleted
        Cookie newAccessTokenCookie = response.getCookie(ACCESS_TOKEN_COOKIE);
        Assert.assertEquals(0, newAccessTokenCookie.getMaxAge());
        Cookie newRefreshTokenCookie = response.getCookie(REFRESH_TOKEN_COOKIE);
        Assert.assertEquals(0, newRefreshTokenCookie.getMaxAge());
        // request no longer contains cookies
        Cookie requestAccessTokenCookie = OAuth2CookieHelper.getAccessTokenCookie(newRequest);
        Assert.assertNull(requestAccessTokenCookie);
        Cookie requestRefreshTokenCookie = OAuth2CookieHelper.getRefreshTokenCookie(newRequest);
        Assert.assertNull(requestRefreshTokenCookie);
    }

    /**
     * If no refresh token is found and the access token has expired, then expect an exception.
     */
    @Test
    public void testRefreshGrantNoRefreshToken() {
        MockHttpServletRequest request = new MockHttpServletRequest(GET.name(), "http://www.test.com");
        Cookie accessTokenCookie = new Cookie(ACCESS_TOKEN_COOKIE, OAuth2AuthenticationServiceTest.ACCESS_TOKEN_VALUE);
        request.setCookies(accessTokenCookie);
        MockHttpServletResponse response = new MockHttpServletResponse();
        expectedException.expect(InvalidTokenException.class);
        refreshTokenFilter.refreshTokensIfExpiring(request, response);
    }

    @Test
    public void testLogout() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Cookie accessTokenCookie = new Cookie(ACCESS_TOKEN_COOKIE, OAuth2AuthenticationServiceTest.ACCESS_TOKEN_VALUE);
        Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN_COOKIE, OAuth2AuthenticationServiceTest.REFRESH_TOKEN_VALUE);
        request.setCookies(accessTokenCookie, refreshTokenCookie);
        MockHttpServletResponse response = new MockHttpServletResponse();
        authenticationService.logout(request, response);
        Cookie newAccessTokenCookie = response.getCookie(ACCESS_TOKEN_COOKIE);
        Assert.assertEquals(0, newAccessTokenCookie.getMaxAge());
        Cookie newRefreshTokenCookie = response.getCookie(REFRESH_TOKEN_COOKIE);
        Assert.assertEquals(0, newRefreshTokenCookie.getMaxAge());
    }

    @Test
    public void testStripTokens() {
        MockHttpServletRequest request = OAuth2AuthenticationServiceTest.createMockHttpServletRequest();
        HttpServletRequest newRequest = authenticationService.stripTokens(request);
        CookieCollection cookies = new CookieCollection(newRequest.getCookies());
        Assert.assertFalse(cookies.contains(ACCESS_TOKEN_COOKIE));
        Assert.assertFalse(cookies.contains(REFRESH_TOKEN_COOKIE));
    }
}

