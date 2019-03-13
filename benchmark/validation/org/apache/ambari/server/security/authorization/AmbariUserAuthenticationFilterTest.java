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
package org.apache.ambari.server.security.authorization;


import ExecutionScheduleManager.USER_ID_HEADER;
import HttpServletResponse.SC_FORBIDDEN;
import InternalTokenClientFilter.INTERNAL_TOKEN_HEADER;
import java.io.IOException;
import java.util.HashSet;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.ambari.server.orm.entities.UserEntity;
import org.apache.ambari.server.security.authorization.internal.InternalTokenStorage;
import org.easymock.Capture;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


public class AmbariUserAuthenticationFilterTest {
    private static final String TEST_INTERNAL_TOKEN = "test token";

    private static final String TEST_USER_ID_HEADER = "1";

    private static final String TEST_USER_NAME = "userName";

    private static final int TEST_USER_ID = 1;

    @Test
    public void testDoFilterValid() throws IOException, ServletException {
        final Users users = createMock(Users.class);
        HttpServletRequest request = createMock(HttpServletRequest.class);
        HttpServletResponse response = createMock(HttpServletResponse.class);
        FilterChain chain = createMock(FilterChain.class);
        InternalTokenStorage tokenStorage = createMock(InternalTokenStorage.class);
        expect(request.getHeader(INTERNAL_TOKEN_HEADER)).andReturn(AmbariUserAuthenticationFilterTest.TEST_INTERNAL_TOKEN);
        expect(tokenStorage.isValidInternalToken(AmbariUserAuthenticationFilterTest.TEST_INTERNAL_TOKEN)).andReturn(true);
        expect(request.getHeader(USER_ID_HEADER)).andReturn(AmbariUserAuthenticationFilterTest.TEST_USER_ID_HEADER);
        UserEntity userEntity = createUserEntity();
        expect(users.getUserEntity(AmbariUserAuthenticationFilterTest.TEST_USER_ID)).andReturn(userEntity);
        expect(users.getUserAuthorities(userEntity)).andReturn(new HashSet<AmbariGrantedAuthority>());
        expect(users.getUser(userEntity)).andReturn(new User(userEntity));
        Capture<String> userHeaderValue = newCapture();
        response.setHeader(eq("User"), capture(userHeaderValue));
        expectLastCall();
        chain.doFilter(request, response);
        expectLastCall();
        replay(users, request, response, chain, tokenStorage);
        AmbariUserAuthorizationFilter filter = new AmbariUserAuthorizationFilter(tokenStorage, users);
        filter.doFilter(request, response, chain);
        verify(users, request, response, chain, tokenStorage);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assert.assertNotNull(authentication);
        Assert.assertEquals(true, authentication.isAuthenticated());
        Assert.assertEquals(AmbariUserAuthenticationFilterTest.TEST_USER_NAME.toLowerCase(), userHeaderValue.getValue());
    }

    @Test
    public void testDoFilterWithoutInternalToken() throws IOException, ServletException {
        final Users users = createMock(Users.class);
        HttpServletRequest request = createMock(HttpServletRequest.class);
        HttpServletResponse response = createMock(HttpServletResponse.class);
        FilterChain chain = createMock(FilterChain.class);
        InternalTokenStorage tokenStorage = createMock(InternalTokenStorage.class);
        expect(request.getHeader(INTERNAL_TOKEN_HEADER)).andReturn(null);
        chain.doFilter(request, response);
        expectLastCall();
        replay(users, request, response, chain, tokenStorage);
        AmbariUserAuthorizationFilter filter = new AmbariUserAuthorizationFilter(tokenStorage, users);
        filter.doFilter(request, response, chain);
        verify(users, request, response, chain, tokenStorage);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assert.assertNull(authentication);
    }

    @Test
    public void testDoFilterWithoutUserToken() throws IOException, ServletException {
        final Users users = createMock(Users.class);
        HttpServletRequest request = createMock(HttpServletRequest.class);
        HttpServletResponse response = createMock(HttpServletResponse.class);
        FilterChain chain = createMock(FilterChain.class);
        InternalTokenStorage tokenStorage = createMock(InternalTokenStorage.class);
        expect(request.getHeader(INTERNAL_TOKEN_HEADER)).andReturn(AmbariUserAuthenticationFilterTest.TEST_INTERNAL_TOKEN);
        expect(tokenStorage.isValidInternalToken(AmbariUserAuthenticationFilterTest.TEST_INTERNAL_TOKEN)).andReturn(true);
        expect(request.getHeader(USER_ID_HEADER)).andReturn(null);
        chain.doFilter(request, response);
        expectLastCall();
        replay(users, request, response, chain, tokenStorage);
        AmbariUserAuthorizationFilter filter = new AmbariUserAuthorizationFilter(tokenStorage, users);
        filter.doFilter(request, response, chain);
        verify(users, request, response, chain, tokenStorage);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assert.assertNull(authentication);
    }

    @Test
    public void testDoFilterWithIncorrectUser() throws IOException, ServletException {
        final Users users = createMock(Users.class);
        HttpServletRequest request = createMock(HttpServletRequest.class);
        HttpServletResponse response = createMock(HttpServletResponse.class);
        FilterChain chain = createMock(FilterChain.class);
        InternalTokenStorage tokenStorage = createMock(InternalTokenStorage.class);
        expect(request.getHeader(INTERNAL_TOKEN_HEADER)).andReturn(AmbariUserAuthenticationFilterTest.TEST_INTERNAL_TOKEN);
        expect(tokenStorage.isValidInternalToken(AmbariUserAuthenticationFilterTest.TEST_INTERNAL_TOKEN)).andReturn(true);
        expect(request.getHeader(USER_ID_HEADER)).andReturn(AmbariUserAuthenticationFilterTest.TEST_USER_ID_HEADER);
        expect(users.getUserEntity(AmbariUserAuthenticationFilterTest.TEST_USER_ID)).andReturn(null);
        response.sendError(SC_FORBIDDEN, "Authentication required");
        expectLastCall();
        response.flushBuffer();
        expectLastCall();
        replay(users, request, response, chain, tokenStorage);
        AmbariUserAuthorizationFilter filter = new AmbariUserAuthorizationFilter(tokenStorage, users);
        filter.doFilter(request, response, chain);
        verify(users, request, response, chain, tokenStorage);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assert.assertNull(authentication);
    }

    @Test
    public void testDoFilterWithInvalidUserID() throws IOException, ServletException {
        final Users users = createMock(Users.class);
        HttpServletRequest request = createMock(HttpServletRequest.class);
        HttpServletResponse response = createMock(HttpServletResponse.class);
        FilterChain chain = createMock(FilterChain.class);
        InternalTokenStorage tokenStorage = createMock(InternalTokenStorage.class);
        expect(request.getHeader(INTERNAL_TOKEN_HEADER)).andReturn(AmbariUserAuthenticationFilterTest.TEST_INTERNAL_TOKEN);
        expect(tokenStorage.isValidInternalToken(AmbariUserAuthenticationFilterTest.TEST_INTERNAL_TOKEN)).andReturn(true);
        expect(request.getHeader(USER_ID_HEADER)).andReturn("admin");
        response.sendError(SC_FORBIDDEN, "Invalid user ID");
        expectLastCall();
        response.flushBuffer();
        expectLastCall();
        replay(users, request, response, chain, tokenStorage);
        AmbariUserAuthorizationFilter filter = new AmbariUserAuthorizationFilter(tokenStorage, users);
        filter.doFilter(request, response, chain);
        verify(users, request, response, chain, tokenStorage);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assert.assertNull(authentication);
    }
}

