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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.shiro.aspectj;


import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.junit.Test;


/**
 *
 */
public class DummyServiceTest {
    private static DummyService SECURED_SERVICE;

    private static DummyService RESTRICTED_SERVICE;

    private Subject subject;

    // TEST ANONYMOUS
    @Test
    public void testAnonymous_asAnonymous() throws Exception {
        DummyServiceTest.SECURED_SERVICE.anonymous();
    }

    @Test
    public void testAnonymous_asUser() throws Exception {
        loginAsUser();
        DummyServiceTest.SECURED_SERVICE.anonymous();
    }

    @Test
    public void testAnonymous_asAdmin() throws Exception {
        loginAsAdmin();
        DummyServiceTest.SECURED_SERVICE.anonymous();
    }

    // TEST GUEST
    @Test
    public void testGuest_asAnonymous() throws Exception {
        DummyServiceTest.SECURED_SERVICE.guest();
    }

    @Test(expected = UnauthenticatedException.class)
    public void testGuest_asUser() throws Exception {
        loginAsUser();
        DummyServiceTest.SECURED_SERVICE.guest();
    }

    @Test(expected = UnauthenticatedException.class)
    public void testGuest_asAdmin() throws Exception {
        loginAsAdmin();
        DummyServiceTest.SECURED_SERVICE.guest();
    }

    // TEST PEEK
    @Test(expected = UnauthenticatedException.class)
    public void testPeek_asAnonymous() throws Exception {
        DummyServiceTest.SECURED_SERVICE.peek();
    }

    @Test
    public void testPeek_asUser() throws Exception {
        loginAsUser();
        DummyServiceTest.SECURED_SERVICE.peek();
    }

    @Test
    public void testPeek_asAdmin() throws Exception {
        loginAsAdmin();
        DummyServiceTest.SECURED_SERVICE.peek();
    }

    // TEST RETRIEVE
    // UnauthenticatedException per SHIRO-146
    @Test(expected = UnauthenticatedException.class)
    public void testRetrieve_asAnonymous() throws Exception {
        DummyServiceTest.SECURED_SERVICE.retrieve();
    }

    @Test
    public void testRetrieve_asUser() throws Exception {
        loginAsUser();
        DummyServiceTest.SECURED_SERVICE.retrieve();
    }

    @Test
    public void testRetrieve_asAdmin() throws Exception {
        loginAsAdmin();
        DummyServiceTest.SECURED_SERVICE.retrieve();
    }

    // TEST CHANGE
    // UnauthenticatedException per SHIRO-146
    @Test(expected = UnauthenticatedException.class)
    public void testChange_asAnonymous() throws Exception {
        DummyServiceTest.SECURED_SERVICE.change();
    }

    @Test(expected = UnauthorizedException.class)
    public void testChange_asUser() throws Exception {
        loginAsUser();
        DummyServiceTest.SECURED_SERVICE.change();
    }

    @Test
    public void testChange_asAdmin() throws Exception {
        loginAsAdmin();
        DummyServiceTest.SECURED_SERVICE.change();
    }

    // TEST RETRIEVE RESTRICTED
    // UnauthenticatedException per SHIRO-146
    @Test(expected = UnauthenticatedException.class)
    public void testRetrieveRestricted_asAnonymous() throws Exception {
        DummyServiceTest.RESTRICTED_SERVICE.retrieve();
    }

    @Test(expected = UnauthorizedException.class)
    public void testRetrieveRestricted_asUser() throws Exception {
        loginAsUser();
        DummyServiceTest.RESTRICTED_SERVICE.retrieve();
    }

    @Test
    public void testRetrieveRestricted_asAdmin() throws Exception {
        loginAsAdmin();
        DummyServiceTest.RESTRICTED_SERVICE.retrieve();
    }
}

