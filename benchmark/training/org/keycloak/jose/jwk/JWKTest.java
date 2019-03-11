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
package org.keycloak.jose.jwk;


import JavaAlgorithm.ES256;
import JavaAlgorithm.RS256;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.ECGenParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.common.util.Base64Url;
import org.keycloak.common.util.KeyUtils;
import org.keycloak.util.JsonSerialization;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class JWKTest {
    @Test
    public void publicRs256() throws Exception {
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        JWK jwk = JWKBuilder.create().kid(KeyUtils.createKeyId(publicKey)).algorithm("RS256").rsa(publicKey);
        Assert.assertNotNull(jwk.getKeyId());
        Assert.assertEquals("RSA", jwk.getKeyType());
        Assert.assertEquals("RS256", jwk.getAlgorithm());
        Assert.assertEquals("sig", jwk.getPublicKeyUse());
        Assert.assertTrue((jwk instanceof RSAPublicJWK));
        Assert.assertNotNull(getModulus());
        Assert.assertNotNull(getPublicExponent());
        String jwkJson = JsonSerialization.writeValueAsString(jwk);
        PublicKey publicKeyFromJwk = JWKParser.create().parse(jwkJson).toPublicKey();
        // Parse
        Assert.assertArrayEquals(publicKey.getEncoded(), publicKeyFromJwk.getEncoded());
        byte[] data = "Some test string".getBytes("utf-8");
        byte[] sign = sign(data, RS256, keyPair.getPrivate());
        verify(data, sign, RS256, publicKeyFromJwk);
    }

    @Test
    public void publicEs256() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        SecureRandom randomGen = SecureRandom.getInstance("SHA1PRNG");
        ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256r1");
        keyGen.initialize(ecSpec, randomGen);
        KeyPair keyPair = keyGen.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        JWK jwk = JWKBuilder.create().kid(KeyUtils.createKeyId(keyPair.getPublic())).algorithm("ES256").ec(publicKey);
        Assert.assertEquals("EC", jwk.getKeyType());
        Assert.assertEquals("ES256", jwk.getAlgorithm());
        Assert.assertEquals("sig", jwk.getPublicKeyUse());
        Assert.assertTrue((jwk instanceof ECPublicJWK));
        ECPublicJWK ecJwk = ((ECPublicJWK) (jwk));
        Assert.assertNotNull(ecJwk.getCrv());
        Assert.assertNotNull(ecJwk.getX());
        Assert.assertNotNull(ecJwk.getY());
        byte[] xBytes = Base64Url.decode(ecJwk.getX());
        byte[] yBytes = Base64Url.decode(ecJwk.getY());
        Assert.assertEquals((256 / 8), xBytes.length);
        Assert.assertEquals((256 / 8), yBytes.length);
        String jwkJson = JsonSerialization.writeValueAsString(jwk);
        JWKParser parser = JWKParser.create().parse(jwkJson);
        PublicKey publicKeyFromJwk = parser.toPublicKey();
        Assert.assertArrayEquals(publicKey.getEncoded(), publicKeyFromJwk.getEncoded());
        byte[] data = "Some test string".getBytes("utf-8");
        byte[] sign = sign(data, ES256, keyPair.getPrivate());
        verify(data, sign, ES256, publicKeyFromJwk);
    }

    @Test
    public void parse() {
        String jwkJson = "{" + (((((("   \"kty\": \"RSA\"," + "   \"alg\": \"RS256\",") + "   \"use\": \"sig\",") + "   \"kid\": \"3121adaa80ace09f89d80899d4a5dc4ce33d0747\",") + "   \"n\": \"soFDjoZ5mQ8XAA7reQAFg90inKAHk0DXMTizo4JuOsgzUbhcplIeZ7ks83hsEjm8mP8lUVaHMPMAHEIp3gu6Xxsg-s73ofx1dtt_Fo7aj8j383MFQGl8-FvixTVobNeGeC0XBBQjN8lEl-lIwOa4ZoERNAShplTej0ntDp7TQm0=\",") + "   \"e\": \"AQAB\"") + "  }");
        PublicKey key = JWKParser.create().parse(jwkJson).toPublicKey();
        Assert.assertEquals("RSA", key.getAlgorithm());
        Assert.assertEquals("X.509", key.getFormat());
    }
}

