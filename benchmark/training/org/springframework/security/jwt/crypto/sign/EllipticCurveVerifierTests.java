/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.security.jwt.crypto.sign;


import java.math.BigInteger;
import java.security.KeyPair;
import java.security.Security;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Test;
import org.springframework.security.jwt.codec.Codecs;


/**
 * Tests for {@link EllipticCurveVerifier}.
 *
 * @author Joe Grandja
 */
public class EllipticCurveVerifierTests {
    private static final String P256_CURVE = "P-256";

    private static final String P384_CURVE = "P-384";

    private static final String P521_CURVE = "P-521";

    private static final String SHA256_ECDSA_ALG = "SHA256withECDSA";

    private static final String SHA384_ECDSA_ALG = "SHA384withECDSA";

    private static final String SHA512_ECDSA_ALG = "SHA512withECDSA";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorWhenUnsupportedCurveThenThrowIllegalArgumentException() {
        new EllipticCurveVerifier(BigInteger.ONE, BigInteger.ONE, "unsupported-curve", EllipticCurveVerifierTests.SHA256_ECDSA_ALG);
    }

    @Test
    public void verifyWhenP256CurveAndSignatureMatchesThenVerificationPasses() throws Exception {
        this.verifyWhenSignatureMatchesThenVerificationPasses(EllipticCurveVerifierTests.P256_CURVE, EllipticCurveVerifierTests.SHA256_ECDSA_ALG);
    }

    @Test(expected = InvalidSignatureException.class)
    public void verifyWhenP256CurveAndSignatureDoesNotMatchThenThrowInvalidSignatureException() throws Exception {
        this.verifyWhenSignatureDoesNotMatchThenThrowInvalidSignatureException(EllipticCurveVerifierTests.P256_CURVE, EllipticCurveVerifierTests.SHA256_ECDSA_ALG);
    }

    @Test
    public void verifyWhenP384CurveAndSignatureMatchesThenVerificationPasses() throws Exception {
        this.verifyWhenSignatureMatchesThenVerificationPasses(EllipticCurveVerifierTests.P384_CURVE, EllipticCurveVerifierTests.SHA384_ECDSA_ALG);
    }

    @Test(expected = InvalidSignatureException.class)
    public void verifyWhenP384CurveAndSignatureDoesNotMatchThenThrowInvalidSignatureException() throws Exception {
        this.verifyWhenSignatureDoesNotMatchThenThrowInvalidSignatureException(EllipticCurveVerifierTests.P384_CURVE, EllipticCurveVerifierTests.SHA384_ECDSA_ALG);
    }

    @Test
    public void verifyWhenP521CurveAndSignatureMatchesThenVerificationPasses() throws Exception {
        this.verifyWhenSignatureMatchesThenVerificationPasses(EllipticCurveVerifierTests.P521_CURVE, EllipticCurveVerifierTests.SHA512_ECDSA_ALG);
    }

    @Test(expected = InvalidSignatureException.class)
    public void verifyWhenP521CurveAndSignatureDoesNotMatchThenThrowInvalidSignatureException() throws Exception {
        this.verifyWhenSignatureDoesNotMatchThenThrowInvalidSignatureException(EllipticCurveVerifierTests.P521_CURVE, EllipticCurveVerifierTests.SHA512_ECDSA_ALG);
    }

    @Test(expected = InvalidSignatureException.class)
    public void verifyWhenSignatureAlgorithmNotSameAsVerificationAlgorithmThenThrowInvalidSignatureException() throws Exception {
        KeyPair keyPair = this.generateKeyPair(EllipticCurveVerifierTests.P256_CURVE);
        ECPublicKey publicKey = ((ECPublicKey) (keyPair.getPublic()));
        ECPrivateKey privateKey = ((ECPrivateKey) (keyPair.getPrivate()));
        byte[] data = "Some data".getBytes();
        byte[] jwsSignature = Codecs.b64UrlEncode(this.generateJwsSignature(data, EllipticCurveVerifierTests.SHA256_ECDSA_ALG, privateKey));
        EllipticCurveVerifier verifier = new EllipticCurveVerifier(publicKey.getW().getAffineX(), publicKey.getW().getAffineY(), EllipticCurveVerifierTests.P256_CURVE, EllipticCurveVerifierTests.SHA512_ECDSA_ALG);
        verifier.verify(data, Codecs.b64UrlDecode(jwsSignature));
    }
}

