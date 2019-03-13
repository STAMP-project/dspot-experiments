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
package io.undertow.server.security;


import io.undertow.testutils.DefaultServer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test case for Digest authentication based on RFC2617 with QOP of auth.
 *
 * @author <a href="mailto:darran.lofthouse@jboss.com">Darran Lofthouse</a>
 */
@RunWith(DefaultServer.class)
public class DigestAuthenticationAuthTestCase extends AuthenticationTestBase {
    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    private static final String REALM_NAME = "Digest_Realm";

    private static final String ZERO = "00000000";

    /**
     * Test for a successful authentication.
     *
     * Also makes two additional calls to demonstrate nonce re-use with an incrementing nonce count.
     */
    @Test
    public void testDigestSuccess() throws Exception {
        DigestAuthenticationAuthTestCase._testDigestSuccess();
    }

    /**
     * Test for a successful authentication.
     *
     * Also makes two additional calls to demonstrate nonce re-use with an incrementing nonce count.
     */
    @Test
    public void testDigestBadUri() throws Exception {
        DigestAuthenticationAuthTestCase._testDigestBadUri();
    }

    /**
     * Test for a failed authentication where a bad username is provided.
     */
    @Test
    public void testBadUsername() throws Exception {
        DigestAuthenticationAuthTestCase._testBadUsername();
    }

    /**
     * Test for a failed authentication where a bad password is provided.
     */
    @Test
    public void testBadPassword() throws Exception {
        DigestAuthenticationAuthTestCase._testBadPassword();
    }

    /**
     * Test for a failed authentication where a bad nonce is provided.
     */
    @Test
    public void testBadNonce() throws Exception {
        DigestAuthenticationAuthTestCase._testBadNonce();
    }

    /**
     * Test for a failed authentication where the nonce count is re-used.
     *
     * Where a nonce count is used the nonce can now be re-used, however each time the nonce count must be different.
     */
    @Test
    public void testNonceCountReUse() throws Exception {
        DigestAuthenticationAuthTestCase._testNonceCountReUse();
    }
}

