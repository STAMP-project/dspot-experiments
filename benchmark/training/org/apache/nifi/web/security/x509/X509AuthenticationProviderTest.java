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
package org.apache.nifi.web.security.x509;


import StandardNiFiUser.ANONYMOUS_IDENTITY;
import org.apache.nifi.authorization.Authorizer;
import org.apache.nifi.authorization.user.NiFiUser;
import org.apache.nifi.authorization.user.StandardNiFiUser;
import org.apache.nifi.web.security.InvalidAuthenticationException;
import org.apache.nifi.web.security.UntrustedProxyException;
import org.apache.nifi.web.security.token.NiFiAuthenticationToken;
import org.junit.Assert;
import org.junit.Test;


public class X509AuthenticationProviderTest {
    private static final String INVALID_CERTIFICATE = "invalid-certificate";

    private static final String IDENTITY_1 = "identity-1";

    private static final String ANONYMOUS = "";

    private static final String UNTRUSTED_PROXY = "untrusted-proxy";

    private static final String PROXY_1 = "proxy-1";

    private static final String PROXY_2 = "proxy-2";

    private static final String GT = ">";

    private static final String ESCAPED_GT = "\\\\>";

    private static final String LT = "<";

    private static final String ESCAPED_LT = "\\\\<";

    private X509AuthenticationProvider x509AuthenticationProvider;

    private X509IdentityProvider certificateIdentityProvider;

    private SubjectDnX509PrincipalExtractor extractor;

    private Authorizer authorizer;

    @Test(expected = InvalidAuthenticationException.class)
    public void testInvalidCertificate() {
        x509AuthenticationProvider.authenticate(getX509Request("", X509AuthenticationProviderTest.INVALID_CERTIFICATE));
    }

    @Test
    public void testNoProxyChain() {
        final NiFiAuthenticationToken auth = ((NiFiAuthenticationToken) (x509AuthenticationProvider.authenticate(getX509Request("", X509AuthenticationProviderTest.IDENTITY_1))));
        final NiFiUser user = getNiFiUser();
        Assert.assertNotNull(user);
        Assert.assertEquals(X509AuthenticationProviderTest.IDENTITY_1, user.getIdentity());
        Assert.assertFalse(user.isAnonymous());
    }

    @Test(expected = UntrustedProxyException.class)
    public void testUntrustedProxy() {
        x509AuthenticationProvider.authenticate(getX509Request(buildProxyChain(X509AuthenticationProviderTest.IDENTITY_1), X509AuthenticationProviderTest.UNTRUSTED_PROXY));
    }

    @Test
    public void testOneProxy() {
        final NiFiAuthenticationToken auth = ((NiFiAuthenticationToken) (x509AuthenticationProvider.authenticate(getX509Request(buildProxyChain(X509AuthenticationProviderTest.IDENTITY_1), X509AuthenticationProviderTest.PROXY_1))));
        final NiFiUser user = getNiFiUser();
        Assert.assertNotNull(user);
        Assert.assertEquals(X509AuthenticationProviderTest.IDENTITY_1, user.getIdentity());
        Assert.assertFalse(user.isAnonymous());
        Assert.assertNotNull(user.getChain());
        Assert.assertEquals(X509AuthenticationProviderTest.PROXY_1, user.getChain().getIdentity());
        Assert.assertFalse(user.getChain().isAnonymous());
    }

    @Test
    public void testAnonymousWithOneProxy() {
        final NiFiAuthenticationToken auth = ((NiFiAuthenticationToken) (x509AuthenticationProvider.authenticate(getX509Request(buildProxyChain(X509AuthenticationProviderTest.ANONYMOUS), X509AuthenticationProviderTest.PROXY_1))));
        final NiFiUser user = getNiFiUser();
        Assert.assertNotNull(user);
        Assert.assertEquals(ANONYMOUS_IDENTITY, user.getIdentity());
        Assert.assertTrue(user.isAnonymous());
        Assert.assertNotNull(user.getChain());
        Assert.assertEquals(X509AuthenticationProviderTest.PROXY_1, user.getChain().getIdentity());
        Assert.assertFalse(user.getChain().isAnonymous());
    }

    @Test
    public void testTwoProxies() {
        final NiFiAuthenticationToken auth = ((NiFiAuthenticationToken) (x509AuthenticationProvider.authenticate(getX509Request(buildProxyChain(X509AuthenticationProviderTest.IDENTITY_1, X509AuthenticationProviderTest.PROXY_2), X509AuthenticationProviderTest.PROXY_1))));
        final NiFiUser user = getNiFiUser();
        Assert.assertNotNull(user);
        Assert.assertEquals(X509AuthenticationProviderTest.IDENTITY_1, user.getIdentity());
        Assert.assertFalse(user.isAnonymous());
        Assert.assertNotNull(user.getChain());
        Assert.assertEquals(X509AuthenticationProviderTest.PROXY_2, user.getChain().getIdentity());
        Assert.assertFalse(user.getChain().isAnonymous());
        Assert.assertNotNull(user.getChain().getChain());
        Assert.assertEquals(X509AuthenticationProviderTest.PROXY_1, user.getChain().getChain().getIdentity());
        Assert.assertFalse(user.getChain().getChain().isAnonymous());
    }

    @Test(expected = UntrustedProxyException.class)
    public void testUntrustedProxyInChain() {
        x509AuthenticationProvider.authenticate(getX509Request(buildProxyChain(X509AuthenticationProviderTest.IDENTITY_1, X509AuthenticationProviderTest.UNTRUSTED_PROXY), X509AuthenticationProviderTest.PROXY_1));
    }

    @Test
    public void testAnonymousProxyInChain() {
        final NiFiAuthenticationToken auth = ((NiFiAuthenticationToken) (x509AuthenticationProvider.authenticate(getX509Request(buildProxyChain(X509AuthenticationProviderTest.IDENTITY_1, X509AuthenticationProviderTest.ANONYMOUS), X509AuthenticationProviderTest.PROXY_1))));
        final NiFiUser user = getNiFiUser();
        Assert.assertNotNull(user);
        Assert.assertEquals(X509AuthenticationProviderTest.IDENTITY_1, user.getIdentity());
        Assert.assertFalse(user.isAnonymous());
        Assert.assertNotNull(user.getChain());
        Assert.assertEquals(ANONYMOUS_IDENTITY, user.getChain().getIdentity());
        Assert.assertTrue(user.getChain().isAnonymous());
        Assert.assertNotNull(user.getChain().getChain());
        Assert.assertEquals(X509AuthenticationProviderTest.PROXY_1, user.getChain().getChain().getIdentity());
        Assert.assertFalse(user.getChain().getChain().isAnonymous());
    }

    @Test
    public void testShouldCreateAnonymousUser() {
        // Arrange
        String identity = "someone";
        // Act
        NiFiUser user = X509AuthenticationProvider.createUser(identity, null, null, null, true);
        // Assert
        assert user != null;
        assert user instanceof StandardNiFiUser;
        assert user.getIdentity().equals(ANONYMOUS_IDENTITY);
        assert user.isAnonymous();
    }

    @Test
    public void testShouldCreateKnownUser() {
        // Arrange
        String identity = "someone";
        // Act
        NiFiUser user = X509AuthenticationProvider.createUser(identity, null, null, null, false);
        // Assert
        assert user != null;
        assert user instanceof StandardNiFiUser;
        assert user.getIdentity().equals(identity);
        assert !(user.isAnonymous());
    }
}

