/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.undertow.servlet.test.security.basic;


import io.undertow.security.idm.Account;
import io.undertow.security.idm.Credential;
import io.undertow.security.idm.IdentityManager;
import io.undertow.security.idm.X509CertificateCredential;
import io.undertow.testutils.DefaultServer;
import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.net.ssl.SSLContext;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
public class ServletClientCertAuthTestCase {
    private static final String REALM_NAME = "Servlet_Realm";

    protected static final IdentityManager identityManager;

    private static SSLContext clientSSLContext;

    static {
        final Set<String> certUsers = new HashSet<>();
        certUsers.add("CN=Test Client,OU=OU,O=Org,L=City,ST=State,C=GB");
        identityManager = new IdentityManager() {
            @Override
            public Account verify(Account account) {
                // An existing account so for testing assume still valid.
                return account;
            }

            @Override
            public Account verify(String id, Credential credential) {
                return null;
            }

            @Override
            public Account verify(Credential credential) {
                if (credential instanceof X509CertificateCredential) {
                    final Principal p = getCertificate().getSubjectX500Principal();
                    if (certUsers.contains(p.getName())) {
                        return new Account() {
                            @Override
                            public Principal getPrincipal() {
                                return p;
                            }

                            @Override
                            public Set<String> getRoles() {
                                return Collections.singleton("role1");
                            }
                        };
                    }
                }
                return null;
            }
        };
    }

    @Test
    public void testUserName() throws Exception {
        testCall("username", "CN=Test Client,OU=OU,O=Org,L=City,ST=State,C=GB", 200);
    }

    @Test
    public void testAuthType() throws Exception {
        testCall("authType", "CLIENT_CERT", 200);
    }
}

