/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.authorization.resource;


import RequestAction.READ;
import Result.Approved;
import Result.Denied;
import org.apache.nifi.authorization.AccessDeniedException;
import org.apache.nifi.authorization.AuthorizationRequest;
import org.apache.nifi.authorization.AuthorizationResult;
import org.apache.nifi.authorization.Authorizer;
import org.apache.nifi.authorization.user.NiFiUser;
import org.apache.nifi.authorization.user.StandardNiFiUser.Builder;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class DataAuthorizableTest {
    private static final String IDENTITY_1 = "identity-1";

    private static final String PROXY_1 = "proxy-1";

    private static final String PROXY_2 = "proxy-2";

    private Authorizable testProcessorAuthorizable;

    private Authorizer testAuthorizer;

    private DataAuthorizable testDataAuthorizable;

    @Test(expected = AccessDeniedException.class)
    public void testAuthorizeNullUser() {
        testDataAuthorizable.authorize(testAuthorizer, READ, null, null);
    }

    @Test
    public void testCheckAuthorizationNullUser() {
        final AuthorizationResult result = testDataAuthorizable.checkAuthorization(testAuthorizer, READ, null, null);
        Assert.assertEquals(Denied, result.getResult());
    }

    @Test(expected = AccessDeniedException.class)
    public void testAuthorizeUnauthorizedUser() {
        final NiFiUser user = new Builder().identity("unknown").build();
        testDataAuthorizable.authorize(testAuthorizer, READ, user, null);
    }

    @Test
    public void testCheckAuthorizationUnauthorizedUser() {
        final NiFiUser user = new Builder().identity("unknown").build();
        final AuthorizationResult result = testDataAuthorizable.checkAuthorization(testAuthorizer, READ, user, null);
        Assert.assertEquals(Denied, result.getResult());
    }

    @Test
    public void testAuthorizedUser() {
        final NiFiUser user = new Builder().identity(DataAuthorizableTest.IDENTITY_1).build();
        testDataAuthorizable.authorize(testAuthorizer, READ, user, null);
        Mockito.verify(testAuthorizer, Mockito.times(1)).authorize(ArgumentMatchers.argThat(new ArgumentMatcher<AuthorizationRequest>() {
            @Override
            public boolean matches(Object o) {
                return DataAuthorizableTest.IDENTITY_1.equals(getIdentity());
            }
        }));
    }

    @Test
    public void testCheckAuthorizationUser() {
        final NiFiUser user = new Builder().identity(DataAuthorizableTest.IDENTITY_1).build();
        final AuthorizationResult result = testDataAuthorizable.checkAuthorization(testAuthorizer, READ, user, null);
        Assert.assertEquals(Approved, result.getResult());
        Mockito.verify(testAuthorizer, Mockito.times(1)).authorize(ArgumentMatchers.argThat(new ArgumentMatcher<AuthorizationRequest>() {
            @Override
            public boolean matches(Object o) {
                return DataAuthorizableTest.IDENTITY_1.equals(getIdentity());
            }
        }));
    }

    @Test
    public void testAuthorizedUserChain() {
        final NiFiUser proxy2 = new Builder().identity(DataAuthorizableTest.PROXY_2).build();
        final NiFiUser proxy1 = new Builder().identity(DataAuthorizableTest.PROXY_1).chain(proxy2).build();
        final NiFiUser user = new Builder().identity(DataAuthorizableTest.IDENTITY_1).chain(proxy1).build();
        testDataAuthorizable.authorize(testAuthorizer, READ, user, null);
        Mockito.verify(testAuthorizer, Mockito.times(3)).authorize(ArgumentMatchers.any(AuthorizationRequest.class));
        verifyAuthorizeForUser(DataAuthorizableTest.IDENTITY_1);
        verifyAuthorizeForUser(DataAuthorizableTest.PROXY_1);
        verifyAuthorizeForUser(DataAuthorizableTest.PROXY_2);
    }

    @Test
    public void testCheckAuthorizationUserChain() {
        final NiFiUser proxy2 = new Builder().identity(DataAuthorizableTest.PROXY_2).build();
        final NiFiUser proxy1 = new Builder().identity(DataAuthorizableTest.PROXY_1).chain(proxy2).build();
        final NiFiUser user = new Builder().identity(DataAuthorizableTest.IDENTITY_1).chain(proxy1).build();
        final AuthorizationResult result = testDataAuthorizable.checkAuthorization(testAuthorizer, READ, user, null);
        Assert.assertEquals(Approved, result.getResult());
        Mockito.verify(testAuthorizer, Mockito.times(3)).authorize(ArgumentMatchers.any(AuthorizationRequest.class));
        verifyAuthorizeForUser(DataAuthorizableTest.IDENTITY_1);
        verifyAuthorizeForUser(DataAuthorizableTest.PROXY_1);
        verifyAuthorizeForUser(DataAuthorizableTest.PROXY_2);
    }
}

