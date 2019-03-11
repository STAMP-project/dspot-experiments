/**
 * Copyright 2017 Google Inc.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
/**
 *
 */
/**
 * //////////////////////////////////////////////////////////////////////////////
 */
package com.google.crypto.tink.subtle;


import EcdsaEncoding.DER;
import EcdsaEncoding.IEEE_P1363;
import HashType.SHA256;
import HashType.SHA512;
import com.google.crypto.tink.TestUtil;
import com.google.crypto.tink.subtle.EllipticCurves.EcdsaEncoding;
import com.google.crypto.tink.subtle.Enums.HashType;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for EcdsaVerifyJce.
 */
@RunWith(JUnit4.class)
public class EcdsaVerifyJceTest {
    @Test
    public void testWycheproofVectors() throws Exception {
        EcdsaVerifyJceTest.testWycheproofVectors("../wycheproof/testvectors/ecdsa_secp256r1_sha256_test.json", DER);
        EcdsaVerifyJceTest.testWycheproofVectors("../wycheproof/testvectors/ecdsa_secp384r1_sha512_test.json", DER);
        EcdsaVerifyJceTest.testWycheproofVectors("../wycheproof/testvectors/ecdsa_secp521r1_sha512_test.json", DER);
        EcdsaVerifyJceTest.testWycheproofVectors("../wycheproof/testvectors/ecdsa_webcrypto_test.json", IEEE_P1363);
    }

    @Test
    public void testConstrutorExceptions() throws Exception {
        ECParameterSpec ecParams = EllipticCurves.getNistP256Params();
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        keyGen.initialize(ecParams);
        KeyPair keyPair = keyGen.generateKeyPair();
        ECPublicKey pub = ((ECPublicKey) (keyPair.getPublic()));
        // Verify with EcdsaVerifyJce.
        try {
            new EcdsaVerifyJce(pub, HashType.SHA1, EcdsaEncoding.DER);
            Assert.fail("Unsafe hash, should have thrown exception.");
        } catch (GeneralSecurityException e) {
            // Expected.
            TestUtil.assertExceptionContains(e, "Unsupported hash: SHA1");
        }
    }

    @Test
    public void testBasic() throws Exception {
        EcdsaVerifyJceTest.testAgainstJceSignatureInstance(EllipticCurves.getNistP256Params(), SHA256);
        EcdsaVerifyJceTest.testAgainstJceSignatureInstance(EllipticCurves.getNistP384Params(), SHA512);
        EcdsaVerifyJceTest.testAgainstJceSignatureInstance(EllipticCurves.getNistP521Params(), SHA512);
        EcdsaVerifyJceTest.testSignVerify(EllipticCurves.getNistP256Params(), SHA256);
        EcdsaVerifyJceTest.testSignVerify(EllipticCurves.getNistP384Params(), SHA512);
        EcdsaVerifyJceTest.testSignVerify(EllipticCurves.getNistP521Params(), SHA512);
    }

    @Test
    public void testModification() throws Exception {
        ECParameterSpec ecParams = EllipticCurves.getNistP256Params();
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        keyGen.initialize(ecParams);
        KeyPair keyPair = keyGen.generateKeyPair();
        ECPublicKey pub = ((ECPublicKey) (keyPair.getPublic()));
        ECPrivateKey priv = ((ECPrivateKey) (keyPair.getPrivate()));
        EcdsaEncoding[] encodings = new EcdsaEncoding[]{ EcdsaEncoding.IEEE_P1363, EcdsaEncoding.DER };
        for (EcdsaEncoding encoding : encodings) {
            // Sign with EcdsaSignJce
            EcdsaSignJce signer = new EcdsaSignJce(priv, HashType.SHA256, encoding);
            byte[] message = "Hello".getBytes("UTF-8");
            byte[] signature = signer.sign(message);
            // Verify with EcdsaVerifyJce.
            EcdsaVerifyJce verifier = new EcdsaVerifyJce(pub, HashType.SHA256, encoding);
            for (TestUtil.BytesMutation mutation : TestUtil.generateMutations(signature)) {
                try {
                    verifier.verify(mutation.value, message);
                    Assert.fail(String.format(("Invalid signature, should have thrown exception : signature = %s, message = %s, " + " description = %s"), Hex.encode(mutation.value), message, mutation.description));
                } catch (GeneralSecurityException expected) {
                    // Expected.
                }
            }
            // Encodings mismatch.
            verifier = new EcdsaVerifyJce(pub, HashType.SHA256, (encoding == (EcdsaEncoding.IEEE_P1363) ? EcdsaEncoding.DER : EcdsaEncoding.IEEE_P1363));
            try {
                verifier.verify(signature, message);
                Assert.fail("Invalid signature, should have thrown exception");
            } catch (GeneralSecurityException expected) {
                // Expected.
            }
        }
    }
}

