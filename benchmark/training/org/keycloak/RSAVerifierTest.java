/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak;


import java.io.IOException;
import java.io.StringWriter;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import junit.framework.Assert;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMWriter;
import org.junit.Test;
import org.keycloak.common.VerificationException;
import org.keycloak.common.util.Time;
import org.keycloak.jose.jws.JWSBuilder;
import org.keycloak.representations.AccessToken;


/**
 *
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RSAVerifierTest {
    private static X509Certificate[] idpCertificates;

    private static KeyPair idpPair;

    private static KeyPair badPair;

    private static KeyPair clientPair;

    private static X509Certificate[] clientCertificateChain;

    private AccessToken token;

    static {
        if ((Security.getProvider("BC")) == null)
            Security.addProvider(new BouncyCastleProvider());

    }

    @Test
    public void testPemWriter() throws Exception {
        PublicKey realmPublicKey = RSAVerifierTest.idpPair.getPublic();
        StringWriter sw = new StringWriter();
        PEMWriter writer = new PEMWriter(sw);
        try {
            writer.writeObject(realmPublicKey);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(sw.toString());
    }

    @Test
    public void testSimpleVerification() throws Exception {
        String encoded = new JWSBuilder().jsonContent(token).rsa256(RSAVerifierTest.idpPair.getPrivate());
        System.out.print(("encoded size: " + (encoded.length())));
        AccessToken token = verifySkeletonKeyToken(encoded);
        Assert.assertTrue(token.getResourceAccess("service").getRoles().contains("admin"));
        Assert.assertEquals("CN=Client", token.getSubject());
    }

    @Test
    public void testBadSignature() throws Exception {
        String encoded = new JWSBuilder().jsonContent(token).rsa256(RSAVerifierTest.badPair.getPrivate());
        AccessToken v = null;
        try {
            v = verifySkeletonKeyToken(encoded);
            Assert.fail();
        } catch (VerificationException ignored) {
        }
    }

    @Test
    public void testNotBeforeGood() throws Exception {
        token.notBefore(((Time.currentTime()) - 100));
        String encoded = new JWSBuilder().jsonContent(token).rsa256(RSAVerifierTest.idpPair.getPrivate());
        AccessToken v = null;
        try {
            v = verifySkeletonKeyToken(encoded);
        } catch (VerificationException ignored) {
            throw ignored;
        }
    }

    @Test
    public void testNotBeforeBad() throws Exception {
        token.notBefore(((Time.currentTime()) + 100));
        String encoded = new JWSBuilder().jsonContent(token).rsa256(RSAVerifierTest.idpPair.getPrivate());
        AccessToken v = null;
        try {
            v = verifySkeletonKeyToken(encoded);
            Assert.fail();
        } catch (VerificationException ignored) {
            System.out.println(ignored.getMessage());
        }
    }

    @Test
    public void testExpirationGood() throws Exception {
        token.expiration(((Time.currentTime()) + 100));
        String encoded = new JWSBuilder().jsonContent(token).rsa256(RSAVerifierTest.idpPair.getPrivate());
        AccessToken v = null;
        try {
            v = verifySkeletonKeyToken(encoded);
        } catch (VerificationException ignored) {
            throw ignored;
        }
    }

    @Test
    public void testExpirationBad() throws Exception {
        token.expiration(((Time.currentTime()) - 100));
        String encoded = new JWSBuilder().jsonContent(token).rsa256(RSAVerifierTest.idpPair.getPrivate());
        AccessToken v = null;
        try {
            v = verifySkeletonKeyToken(encoded);
            Assert.fail();
        } catch (VerificationException ignored) {
        }
    }

    @Test
    public void testTokenAuth() throws Exception {
        token = new AccessToken();
        token.subject("CN=Client").issuer("http://localhost:8080/auth/realms/demo").addAccess("service").addRole("admin").verifyCaller(true);
        token.setEmail("bill@jboss.org");
        String encoded = new JWSBuilder().jsonContent(token).rsa256(RSAVerifierTest.idpPair.getPrivate());
        System.out.println(("token size: " + (encoded.length())));
        AccessToken v = null;
        try {
            v = verifySkeletonKeyToken(encoded);
            Assert.fail();
        } catch (VerificationException ignored) {
        }
    }

    @Test
    public void testAudience() throws Exception {
        token.addAudience("my-app");
        token.addAudience("your-app");
        String encoded = new JWSBuilder().jsonContent(token).rsa256(RSAVerifierTest.idpPair.getPrivate());
        verifyAudience(encoded, "my-app");
        verifyAudience(encoded, "your-app");
        try {
            verifyAudience(encoded, "other-app");
            Assert.fail();
        } catch (VerificationException ignored) {
            System.out.println(ignored.getMessage());
        }
        try {
            verifyAudience(encoded, null);
            Assert.fail();
        } catch (VerificationException ignored) {
            System.out.println(ignored.getMessage());
        }
    }
}

