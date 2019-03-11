/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.security.authentication.jwt;


import UserAuthenticationType.JWT;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.ambari.server.configuration.Configuration;
import org.apache.ambari.server.orm.entities.UserAuthenticationEntity;
import org.apache.ambari.server.orm.entities.UserEntity;
import org.apache.ambari.server.security.AmbariEntryPoint;
import org.apache.ambari.server.security.authentication.AmbariAuthenticationEventHandler;
import org.apache.ambari.server.security.authentication.AmbariAuthenticationException;
import org.apache.ambari.server.security.authentication.AmbariAuthenticationFilter;
import org.apache.ambari.server.security.authorization.User;
import org.apache.ambari.server.security.authorization.Users;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.AuthenticationEntryPoint;


public class AmbariJwtAuthenticationFilterTest extends EasyMockSupport {
    private static RSAPublicKey publicKey;

    private static RSAPrivateKey privateKey;

    private static RSAPrivateKey invalidPrivateKey;

    @Test
    public void testGetJWTFromCookie() throws Exception {
        HttpServletRequest request = createNiceMock(HttpServletRequest.class);
        Cookie cookie = createNiceMock(Cookie.class);
        expect(cookie.getName()).andReturn("non-default");
        expect(cookie.getValue()).andReturn("stubtokenstring");
        expect(request.getCookies()).andReturn(new Cookie[]{ cookie });
        JwtAuthenticationPropertiesProvider jwtAuthenticationPropertiesProvider = createMock(JwtAuthenticationPropertiesProvider.class);
        expect(jwtAuthenticationPropertiesProvider.get()).andReturn(createTestProperties()).anyTimes();
        AmbariAuthenticationEventHandler eventHandler = createNiceMock(AmbariAuthenticationEventHandler.class);
        replayAll();
        AmbariJwtAuthenticationFilter filter = new AmbariJwtAuthenticationFilter(null, jwtAuthenticationPropertiesProvider, null, eventHandler);
        String jwtFromCookie = filter.getJWTFromCookie(request);
        verifyAll();
        Assert.assertEquals("stubtokenstring", jwtFromCookie);
    }

    @Test
    public void testValidateSignature() throws Exception {
        JwtAuthenticationPropertiesProvider jwtAuthenticationPropertiesProvider = createMock(JwtAuthenticationPropertiesProvider.class);
        expect(jwtAuthenticationPropertiesProvider.get()).andReturn(createTestProperties()).anyTimes();
        AmbariAuthenticationEventHandler eventHandler = createNiceMock(AmbariAuthenticationEventHandler.class);
        replayAll();
        AmbariJwtAuthenticationFilter filter = new AmbariJwtAuthenticationFilter(null, jwtAuthenticationPropertiesProvider, null, eventHandler);
        Assert.assertTrue(filter.validateSignature(getSignedToken()));
        Assert.assertFalse(filter.validateSignature(getInvalidToken()));
        verifyAll();
    }

    @Test
    public void testValidateAudiences() throws Exception {
        JwtAuthenticationPropertiesProvider jwtAuthenticationPropertiesProvider = createMock(JwtAuthenticationPropertiesProvider.class);
        expect(jwtAuthenticationPropertiesProvider.get()).andReturn(createTestProperties()).anyTimes();
        AmbariAuthenticationEventHandler eventHandler = createNiceMock(AmbariAuthenticationEventHandler.class);
        replayAll();
        AmbariJwtAuthenticationFilter filter = new AmbariJwtAuthenticationFilter(null, jwtAuthenticationPropertiesProvider, null, eventHandler);
        Assert.assertTrue(filter.validateAudiences(getSignedToken()));
        Assert.assertFalse(filter.validateAudiences(getInvalidToken()));
        verifyAll();
    }

    @Test
    public void testValidateNullAudiences() throws Exception {
        JwtAuthenticationPropertiesProvider jwtAuthenticationPropertiesProvider = createMock(JwtAuthenticationPropertiesProvider.class);
        expect(jwtAuthenticationPropertiesProvider.get()).andReturn(createTestProperties(null)).anyTimes();
        AmbariAuthenticationEventHandler eventHandler = createNiceMock(AmbariAuthenticationEventHandler.class);
        replayAll();
        AmbariJwtAuthenticationFilter filter = new AmbariJwtAuthenticationFilter(null, jwtAuthenticationPropertiesProvider, null, eventHandler);
        Assert.assertTrue(filter.validateAudiences(getSignedToken()));
        Assert.assertTrue(filter.validateAudiences(getInvalidToken()));
        verifyAll();
    }

    @Test
    public void testValidateTokenWithoutAudiences() throws Exception {
        JwtAuthenticationPropertiesProvider jwtAuthenticationPropertiesProvider = createMock(JwtAuthenticationPropertiesProvider.class);
        expect(jwtAuthenticationPropertiesProvider.get()).andReturn(createTestProperties()).anyTimes();
        AmbariAuthenticationEventHandler eventHandler = createNiceMock(AmbariAuthenticationEventHandler.class);
        replayAll();
        AmbariJwtAuthenticationFilter filter = new AmbariJwtAuthenticationFilter(null, jwtAuthenticationPropertiesProvider, null, eventHandler);
        Assert.assertFalse(filter.validateAudiences(getSignedToken(null)));
        verifyAll();
    }

    @Test
    public void testValidateExpiration() throws Exception {
        JwtAuthenticationPropertiesProvider jwtAuthenticationPropertiesProvider = createMock(JwtAuthenticationPropertiesProvider.class);
        expect(jwtAuthenticationPropertiesProvider.get()).andReturn(createTestProperties()).anyTimes();
        AmbariAuthenticationEventHandler eventHandler = createNiceMock(AmbariAuthenticationEventHandler.class);
        replayAll();
        AmbariJwtAuthenticationFilter filter = new AmbariJwtAuthenticationFilter(null, jwtAuthenticationPropertiesProvider, null, eventHandler);
        Assert.assertTrue(filter.validateExpiration(getSignedToken()));
        Assert.assertFalse(filter.validateExpiration(getInvalidToken()));
        verifyAll();
    }

    @Test
    public void testValidateNoExpiration() throws Exception {
        JwtAuthenticationPropertiesProvider jwtAuthenticationPropertiesProvider = createMock(JwtAuthenticationPropertiesProvider.class);
        expect(jwtAuthenticationPropertiesProvider.get()).andReturn(createTestProperties()).anyTimes();
        AmbariAuthenticationEventHandler eventHandler = createNiceMock(AmbariAuthenticationEventHandler.class);
        replayAll();
        AmbariJwtAuthenticationFilter filter = new AmbariJwtAuthenticationFilter(null, jwtAuthenticationPropertiesProvider, null, eventHandler);
        Assert.assertTrue(filter.validateExpiration(getSignedToken(null, "test-audience")));
        Assert.assertFalse(filter.validateExpiration(getInvalidToken()));
        verifyAll();
    }

    @Test
    public void testShouldApplyTrue() throws JOSEException {
        JwtAuthenticationPropertiesProvider jwtAuthenticationPropertiesProvider = createMock(JwtAuthenticationPropertiesProvider.class);
        expect(jwtAuthenticationPropertiesProvider.get()).andReturn(createTestProperties()).anyTimes();
        SignedJWT token = getInvalidToken();
        Cookie cookie = createMock(Cookie.class);
        expect(cookie.getName()).andReturn("non-default").atLeastOnce();
        expect(cookie.getValue()).andReturn(token.serialize()).atLeastOnce();
        HttpServletRequest request = createMock(HttpServletRequest.class);
        expect(request.getCookies()).andReturn(new Cookie[]{ cookie });
        AmbariAuthenticationEventHandler eventHandler = createNiceMock(AmbariAuthenticationEventHandler.class);
        replayAll();
        AmbariJwtAuthenticationFilter filter = new AmbariJwtAuthenticationFilter(null, jwtAuthenticationPropertiesProvider, null, eventHandler);
        Assert.assertTrue(filter.shouldApply(request));
        verifyAll();
    }

    @Test
    public void testShouldApplyTrueBadToken() throws JOSEException {
        JwtAuthenticationPropertiesProvider jwtAuthenticationPropertiesProvider = createMock(JwtAuthenticationPropertiesProvider.class);
        expect(jwtAuthenticationPropertiesProvider.get()).andReturn(createTestProperties()).anyTimes();
        Cookie cookie = createMock(Cookie.class);
        expect(cookie.getName()).andReturn("non-default").atLeastOnce();
        expect(cookie.getValue()).andReturn("bad token").atLeastOnce();
        HttpServletRequest request = createMock(HttpServletRequest.class);
        expect(request.getCookies()).andReturn(new Cookie[]{ cookie });
        AmbariAuthenticationEventHandler eventHandler = createNiceMock(AmbariAuthenticationEventHandler.class);
        replayAll();
        AmbariJwtAuthenticationFilter filter = new AmbariJwtAuthenticationFilter(null, jwtAuthenticationPropertiesProvider, null, eventHandler);
        Assert.assertTrue(filter.shouldApply(request));
        verifyAll();
    }

    @Test
    public void testShouldApplyFalseMissingCookie() throws JOSEException {
        JwtAuthenticationPropertiesProvider jwtAuthenticationPropertiesProvider = createMock(JwtAuthenticationPropertiesProvider.class);
        expect(jwtAuthenticationPropertiesProvider.get()).andReturn(createTestProperties()).anyTimes();
        Cookie cookie = createMock(Cookie.class);
        expect(cookie.getName()).andReturn("some-other-cookie").atLeastOnce();
        HttpServletRequest request = createMock(HttpServletRequest.class);
        expect(request.getCookies()).andReturn(new Cookie[]{ cookie });
        AmbariAuthenticationEventHandler eventHandler = createNiceMock(AmbariAuthenticationEventHandler.class);
        replayAll();
        AmbariJwtAuthenticationFilter filter = new AmbariJwtAuthenticationFilter(null, jwtAuthenticationPropertiesProvider, null, eventHandler);
        Assert.assertFalse(filter.shouldApply(request));
        verifyAll();
    }

    @Test
    public void testShouldApplyFalseNotEnabled() throws JOSEException {
        JwtAuthenticationPropertiesProvider jwtAuthenticationPropertiesProvider = createMock(JwtAuthenticationPropertiesProvider.class);
        expect(jwtAuthenticationPropertiesProvider.get()).andReturn(null).anyTimes();
        HttpServletRequest request = createMock(HttpServletRequest.class);
        AmbariAuthenticationEventHandler eventHandler = createNiceMock(AmbariAuthenticationEventHandler.class);
        replayAll();
        AmbariJwtAuthenticationFilter filter = new AmbariJwtAuthenticationFilter(null, jwtAuthenticationPropertiesProvider, null, eventHandler);
        Assert.assertFalse(filter.shouldApply(request));
        verify(request);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureNonNullEventHandler() {
        new AmbariJwtAuthenticationFilter(createNiceMock(AmbariEntryPoint.class), createNiceMock(JwtAuthenticationPropertiesProvider.class), createNiceMock(AmbariJwtAuthenticationProvider.class), null);
    }

    @Test
    public void testDoFilterSuccessful() throws Exception {
        Capture<? extends AmbariAuthenticationFilter> captureFilter = newCapture(CaptureType.ALL);
        SignedJWT token = getSignedToken();
        JwtAuthenticationPropertiesProvider jwtAuthenticationPropertiesProvider = createMock(JwtAuthenticationPropertiesProvider.class);
        expect(jwtAuthenticationPropertiesProvider.get()).andReturn(createTestProperties()).anyTimes();
        Configuration configuration = createNiceMock(Configuration.class);
        expect(configuration.getMaxAuthenticationFailures()).andReturn(10).anyTimes();
        HttpServletRequest request = createMock(HttpServletRequest.class);
        HttpServletResponse response = createMock(HttpServletResponse.class);
        FilterChain filterChain = createMock(FilterChain.class);
        Cookie cookie = createMock(Cookie.class);
        expect(cookie.getName()).andReturn("non-default").once();
        expect(cookie.getValue()).andReturn(token.serialize()).once();
        expect(request.getCookies()).andReturn(new Cookie[]{ cookie }).once();
        UserAuthenticationEntity userAuthenticationEntity = createMock(UserAuthenticationEntity.class);
        expect(userAuthenticationEntity.getAuthenticationType()).andReturn(JWT).anyTimes();
        expect(userAuthenticationEntity.getAuthenticationKey()).andReturn("").anyTimes();
        UserEntity userEntity = createMock(UserEntity.class);
        expect(userEntity.getAuthenticationEntities()).andReturn(Collections.singletonList(userAuthenticationEntity)).atLeastOnce();
        User user = createMock(User.class);
        Users users = createMock(Users.class);
        expect(users.getUserEntity("test-user")).andReturn(userEntity).once();
        expect(users.getUser(userEntity)).andReturn(user).once();
        expect(users.getUserAuthorities(userEntity)).andReturn(Collections.emptyList()).once();
        users.validateLogin(userEntity, "test-user");
        expectLastCall().once();
        AmbariAuthenticationEventHandler eventHandler = createNiceMock(AmbariAuthenticationEventHandler.class);
        eventHandler.beforeAttemptAuthentication(capture(captureFilter), eq(request), eq(response));
        expectLastCall().once();
        eventHandler.onSuccessfulAuthentication(capture(captureFilter), eq(request), eq(response), anyObject(Authentication.class));
        expectLastCall().once();
        filterChain.doFilter(request, response);
        expectLastCall().once();
        AuthenticationEntryPoint entryPoint = createNiceMock(AmbariEntryPoint.class);
        replayAll();
        AmbariJwtAuthenticationProvider provider = new AmbariJwtAuthenticationProvider(users, configuration);
        AmbariJwtAuthenticationFilter filter = new AmbariJwtAuthenticationFilter(entryPoint, jwtAuthenticationPropertiesProvider, provider, eventHandler);
        filter.doFilter(request, response, filterChain);
        verifyAll();
        List<? extends AmbariAuthenticationFilter> capturedFilters = captureFilter.getValues();
        for (AmbariAuthenticationFilter capturedFiltered : capturedFilters) {
            Assert.assertSame(filter, capturedFiltered);
        }
    }

    @Test
    public void testDoFilterUnsuccessful() throws Exception {
        Capture<? extends AmbariAuthenticationFilter> captureFilter = newCapture(CaptureType.ALL);
        SignedJWT token = getSignedToken();
        Configuration configuration = createMock(Configuration.class);
        JwtAuthenticationPropertiesProvider jwtAuthenticationPropertiesProvider = createMock(JwtAuthenticationPropertiesProvider.class);
        expect(jwtAuthenticationPropertiesProvider.get()).andReturn(createTestProperties()).anyTimes();
        HttpServletRequest request = createMock(HttpServletRequest.class);
        HttpServletResponse response = createMock(HttpServletResponse.class);
        FilterChain filterChain = createMock(FilterChain.class);
        Cookie cookie = createMock(Cookie.class);
        expect(cookie.getName()).andReturn("non-default").once();
        expect(cookie.getValue()).andReturn(token.serialize()).once();
        expect(request.getCookies()).andReturn(new Cookie[]{ cookie }).once();
        Users users = createMock(Users.class);
        expect(users.getUserEntity("test-user")).andReturn(null).once();
        AmbariAuthenticationEventHandler eventHandler = createNiceMock(AmbariAuthenticationEventHandler.class);
        eventHandler.beforeAttemptAuthentication(capture(captureFilter), eq(request), eq(response));
        expectLastCall().once();
        eventHandler.onUnsuccessfulAuthentication(capture(captureFilter), eq(request), eq(response), anyObject(AmbariAuthenticationException.class));
        expectLastCall().once();
        AuthenticationEntryPoint entryPoint = createNiceMock(AmbariEntryPoint.class);
        entryPoint.commence(eq(request), eq(response), anyObject(AmbariAuthenticationException.class));
        expectLastCall().once();
        replayAll();
        AmbariJwtAuthenticationProvider provider = new AmbariJwtAuthenticationProvider(users, configuration);
        AmbariJwtAuthenticationFilter filter = new AmbariJwtAuthenticationFilter(entryPoint, jwtAuthenticationPropertiesProvider, provider, eventHandler);
        filter.doFilter(request, response, filterChain);
        verifyAll();
        List<? extends AmbariAuthenticationFilter> capturedFilters = captureFilter.getValues();
        for (AmbariAuthenticationFilter capturedFiltered : capturedFilters) {
            Assert.assertSame(filter, capturedFiltered);
        }
    }
}

