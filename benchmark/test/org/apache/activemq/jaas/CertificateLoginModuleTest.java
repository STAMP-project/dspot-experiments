/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.jaas;


import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;
import junit.framework.TestCase;


public class CertificateLoginModuleTest extends TestCase {
    private static final String USER_NAME = "testUser";

    private static final List<String> GROUP_NAMES = new Vector<String>();

    private StubCertificateLoginModule loginModule;

    private Subject subject;

    public CertificateLoginModuleTest() {
        CertificateLoginModuleTest.GROUP_NAMES.add("testGroup1");
        CertificateLoginModuleTest.GROUP_NAMES.add("testGroup2");
        CertificateLoginModuleTest.GROUP_NAMES.add("testGroup3");
        CertificateLoginModuleTest.GROUP_NAMES.add("testGroup4");
    }

    public void testLoginSuccess() throws IOException {
        try {
            loginWithCredentials(CertificateLoginModuleTest.USER_NAME, new HashSet<String>(CertificateLoginModuleTest.GROUP_NAMES));
        } catch (Exception e) {
            TestCase.fail(("Unable to login: " + (e.getMessage())));
        }
        checkPrincipalsMatch(subject);
    }

    public void testLoginFailure() throws IOException {
        boolean loginFailed = false;
        try {
            loginWithCredentials(null, new HashSet<String>());
        } catch (LoginException e) {
            loginFailed = true;
        }
        if (!loginFailed) {
            TestCase.fail("Logged in with unknown certificate.");
        }
    }

    public void testLogOut() throws IOException {
        try {
            loginWithCredentials(CertificateLoginModuleTest.USER_NAME, new HashSet<String>(CertificateLoginModuleTest.GROUP_NAMES));
        } catch (Exception e) {
            TestCase.fail(("Unable to login: " + (e.getMessage())));
        }
        logout();
        TestCase.assertEquals("logout should have cleared Subject principals.", 0, subject.getPrincipals().size());
    }
}

