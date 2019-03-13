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
package org.apache.ambari.server.security.authentication;


import com.google.inject.Injector;
import org.apache.ambari.server.orm.entities.UserEntity;
import org.apache.ambari.server.security.authorization.Users;
import org.easymock.EasyMockSupport;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;


public abstract class AbstractAuthenticationProviderTest extends EasyMockSupport {
    static final String TEST_USER_NAME = "userName";

    @Test
    public void testAuthenticationSuccess() {
        Injector injector = getInjector();
        UserEntity userEntity = getUserEntity(injector, AbstractAuthenticationProviderTest.TEST_USER_NAME, 9, true);
        Users users = injector.getInstance(Users.class);
        expect(users.getUserEntity(AbstractAuthenticationProviderTest.TEST_USER_NAME)).andReturn(userEntity).atLeastOnce();
        expect(users.getUserAuthorities(userEntity)).andReturn(null).atLeastOnce();
        Authentication authentication = getAuthentication(true, true);
        replayAll();
        AuthenticationProvider provider = getAuthenticationProvider(injector);
        Authentication result = provider.authenticate(authentication);
        verifyAll();
        Assert.assertNotNull(result);
        Assert.assertEquals(true, result.isAuthenticated());
        Assert.assertTrue((result instanceof AmbariUserAuthentication));
        validateAuthenticationResult(((AmbariUserAuthentication) (result)));
    }

    @Test(expected = AmbariAuthenticationException.class)
    public void testAuthenticationWithIncorrectUserName() {
        Injector injector = getInjector();
        Authentication authentication = getAuthentication(false, true);
        Users users = injector.getInstance(Users.class);
        expect(users.getUserEntity(anyString())).andReturn(null).atLeastOnce();
        replayAll();
        AuthenticationProvider provider = getAuthenticationProvider(injector);
        provider.authenticate(authentication);
    }

    @Test(expected = AmbariAuthenticationException.class)
    public void testAuthenticationWithoutCredentials() {
        Injector injector = getInjector();
        UserEntity userEntity = getUserEntity(injector, AbstractAuthenticationProviderTest.TEST_USER_NAME, 0, true);
        Users users = injector.getInstance(Users.class);
        expect(users.getUserEntity(AbstractAuthenticationProviderTest.TEST_USER_NAME)).andReturn(userEntity).atLeastOnce();
        expect(users.getUserAuthorities(userEntity)).andReturn(null).atLeastOnce();
        Authentication authentication = createMock(Authentication.class);
        expect(authentication.getName()).andReturn(AbstractAuthenticationProviderTest.TEST_USER_NAME).atLeastOnce();
        expect(authentication.getCredentials()).andReturn(null).atLeastOnce();
        replayAll();
        AuthenticationProvider provider = getAuthenticationProvider(injector);
        provider.authenticate(authentication);
    }

    @Test(expected = AmbariAuthenticationException.class)
    public void testAuthenticationWithIncorrectCredential() {
        Injector injector = getInjector();
        UserEntity userEntity = getUserEntity(injector, AbstractAuthenticationProviderTest.TEST_USER_NAME, 0, true);
        Users users = injector.getInstance(Users.class);
        expect(users.getUserEntity(AbstractAuthenticationProviderTest.TEST_USER_NAME)).andReturn(userEntity).atLeastOnce();
        expect(users.getUserAuthorities(userEntity)).andReturn(null).atLeastOnce();
        Authentication authentication = getAuthentication(true, false);
        replayAll();
        AuthenticationProvider provider = getAuthenticationProvider(injector);
        provider.authenticate(authentication);
    }

    @Test(expected = TooManyLoginFailuresException.class)
    public void testUserIsLockedOutAfterConsecutiveFailures() {
        Injector injector = getInjector();
        // Force the user to have more than 10 consecutive failures
        UserEntity userEntity = getUserEntity(injector, AbstractAuthenticationProviderTest.TEST_USER_NAME, 11, true);
        Users users = injector.getInstance(Users.class);
        expect(users.getUserEntity(AbstractAuthenticationProviderTest.TEST_USER_NAME)).andReturn(userEntity).atLeastOnce();
        Authentication authentication = getAuthentication(true, true);
        replayAll();
        AmbariLocalAuthenticationProvider ambariLocalAuthenticationProvider = injector.getInstance(AmbariLocalAuthenticationProvider.class);
        ambariLocalAuthenticationProvider.authenticate(authentication);
    }

    @Test(expected = AccountDisabledException.class)
    public void testUserIsInactive() {
        Injector injector = getInjector();
        // Force the user to be inactive
        UserEntity userEntity = getUserEntity(injector, AbstractAuthenticationProviderTest.TEST_USER_NAME, 10, false);
        Users users = injector.getInstance(Users.class);
        expect(users.getUserEntity(AbstractAuthenticationProviderTest.TEST_USER_NAME)).andReturn(userEntity).atLeastOnce();
        Authentication authentication = getAuthentication(true, true);
        replayAll();
        AmbariLocalAuthenticationProvider ambariLocalAuthenticationProvider = injector.getInstance(AmbariLocalAuthenticationProvider.class);
        ambariLocalAuthenticationProvider.authenticate(authentication);
    }
}

