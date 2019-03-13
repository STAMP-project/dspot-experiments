/**
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.security.oauth2.provider.token.store.jwk;


import JwkDefinition.CryptoAlgorithm.RS256;
import JwkDefinition.CryptoAlgorithm.RS512;
import JwkDefinitionSource.JwkDefinitionHolder;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;


/**
 *
 *
 * @author Joe Grandja
 */
public class JwkVerifyingJwtAccessTokenConverterTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void encodeWhenCalledThenThrowJwkException() throws Exception {
        this.thrown.expect(JwkException.class);
        this.thrown.expectMessage("JWT signing (JWS) is not supported.");
        JwkVerifyingJwtAccessTokenConverter accessTokenConverter = new JwkVerifyingJwtAccessTokenConverter(Mockito.mock(JwkDefinitionSource.class));
        accessTokenConverter.encode(null, null);
    }

    @Test
    public void decodeWhenKeyIdHeaderMissingThenThrowJwkException() throws Exception {
        this.thrown.expect(InvalidTokenException.class);
        this.thrown.expectMessage("Invalid JWT/JWS: kid is a required JOSE Header");
        JwkVerifyingJwtAccessTokenConverter accessTokenConverter = new JwkVerifyingJwtAccessTokenConverter(Mockito.mock(JwkDefinitionSource.class));
        String jwt = JwtTestUtil.createJwt(JwtTestUtil.createJwtHeader(null, RS256));
        accessTokenConverter.decode(jwt);
    }

    @Test
    public void decodeWhenKeyIdHeaderInvalidThenThrowJwkException() throws Exception {
        this.thrown.expect(InvalidTokenException.class);
        this.thrown.expectMessage("Invalid JOSE Header kid (invalid-key-id)");
        JwkDefinition jwkDefinition = this.createRSAJwkDefinition("key-id-1", RS256);
        JwkDefinitionSource jwkDefinitionSource = Mockito.mock(JwkDefinitionSource.class);
        JwkDefinitionSource.JwkDefinitionHolder jwkDefinitionHolder = Mockito.mock(JwkDefinitionHolder.class);
        Mockito.when(jwkDefinitionHolder.getJwkDefinition()).thenReturn(jwkDefinition);
        Mockito.when(jwkDefinitionSource.getDefinitionLoadIfNecessary("key-id-1")).thenReturn(jwkDefinitionHolder);
        JwkVerifyingJwtAccessTokenConverter accessTokenConverter = new JwkVerifyingJwtAccessTokenConverter(jwkDefinitionSource);
        String jwt = JwtTestUtil.createJwt(JwtTestUtil.createJwtHeader("invalid-key-id", RS256));
        accessTokenConverter.decode(jwt);
    }

    // gh-1129
    @Test
    public void decodeWhenJwkAlgorithmNullAndJwtAlgorithmPresentThenDecodeStillSucceeds() throws Exception {
        JwkDefinition jwkDefinition = this.createRSAJwkDefinition("key-id-1", null);
        JwkDefinitionSource jwkDefinitionSource = Mockito.mock(JwkDefinitionSource.class);
        JwkDefinitionSource.JwkDefinitionHolder jwkDefinitionHolder = Mockito.mock(JwkDefinitionHolder.class);
        SignatureVerifier signatureVerifier = Mockito.mock(SignatureVerifier.class);
        Mockito.when(jwkDefinitionHolder.getJwkDefinition()).thenReturn(jwkDefinition);
        Mockito.when(jwkDefinitionSource.getDefinitionLoadIfNecessary("key-id-1")).thenReturn(jwkDefinitionHolder);
        Mockito.when(jwkDefinitionHolder.getSignatureVerifier()).thenReturn(signatureVerifier);
        JwkVerifyingJwtAccessTokenConverter accessTokenConverter = new JwkVerifyingJwtAccessTokenConverter(jwkDefinitionSource);
        String jwt = JwtTestUtil.createJwt(JwtTestUtil.createJwtHeader("key-id-1", RS256));
        String jws = (jwt + ".") + (utf8Decode(b64UrlEncode("junkSignature".getBytes())));
        Map<String, Object> decodedJwt = accessTokenConverter.decode(jws);
        Assert.assertNotNull(decodedJwt);
    }

    @Test
    public void decodeWhenAlgorithmHeaderMissingThenThrowJwkException() throws Exception {
        this.thrown.expect(InvalidTokenException.class);
        this.thrown.expectMessage("Invalid JWT/JWS: alg is a required JOSE Header");
        JwkDefinition jwkDefinition = this.createRSAJwkDefinition("key-id-1", RS256);
        JwkDefinitionSource jwkDefinitionSource = Mockito.mock(JwkDefinitionSource.class);
        JwkDefinitionSource.JwkDefinitionHolder jwkDefinitionHolder = Mockito.mock(JwkDefinitionHolder.class);
        Mockito.when(jwkDefinitionHolder.getJwkDefinition()).thenReturn(jwkDefinition);
        Mockito.when(jwkDefinitionSource.getDefinitionLoadIfNecessary("key-id-1")).thenReturn(jwkDefinitionHolder);
        JwkVerifyingJwtAccessTokenConverter accessTokenConverter = new JwkVerifyingJwtAccessTokenConverter(jwkDefinitionSource);
        String jwt = JwtTestUtil.createJwt(JwtTestUtil.createJwtHeader("key-id-1", null));
        accessTokenConverter.decode(jwt);
    }

    @Test
    public void decodeWhenAlgorithmHeaderDoesNotMatchJwkAlgorithmThenThrowJwkException() throws Exception {
        this.thrown.expect(InvalidTokenException.class);
        this.thrown.expectMessage(("Invalid JOSE Header alg (RS512) " + "does not match algorithm associated to JWK with kid (key-id-1)"));
        JwkDefinition jwkDefinition = this.createRSAJwkDefinition("key-id-1", RS256);
        JwkDefinitionSource jwkDefinitionSource = Mockito.mock(JwkDefinitionSource.class);
        JwkDefinitionSource.JwkDefinitionHolder jwkDefinitionHolder = Mockito.mock(JwkDefinitionHolder.class);
        Mockito.when(jwkDefinitionHolder.getJwkDefinition()).thenReturn(jwkDefinition);
        Mockito.when(jwkDefinitionSource.getDefinitionLoadIfNecessary("key-id-1")).thenReturn(jwkDefinitionHolder);
        JwkVerifyingJwtAccessTokenConverter accessTokenConverter = new JwkVerifyingJwtAccessTokenConverter(jwkDefinitionSource);
        String jwt = JwtTestUtil.createJwt(JwtTestUtil.createJwtHeader("key-id-1", RS512));
        accessTokenConverter.decode(jwt);
    }
}

