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


import com.google.crypto.tink.PublicKeySign;
import com.google.crypto.tink.TestUtil;
import com.google.crypto.tink.subtle.EllipticCurves.EcdsaEncoding;
import com.google.crypto.tink.subtle.Enums.HashType;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test for thread safety of {@code Signature}-primitives. This test covers signers.
 *
 * <p>If possible then this unit test should be run using a thread sanitizer. Otherwise only race
 * conditions that actually happend during the test will be detected.
 */
@RunWith(JUnit4.class)
public class SignatureThreadSafetyTest {
    /**
     * Exception handler for uncaught exceptions in a thread.
     *
     * <p>TODO(bleichen): Surely there must be a better way to catch exceptions in threads in unit
     * tests. junit ought to do this. However, at least for some setups, tests can pass despite
     * uncaught exceptions in threads.
     *
     * <p>TODO(bleichen): Nonce reuse of non-deterministic signatures.
     *
     * <p>TODO(bleichen): Overwriting nonces in deterministic signature schemes.
     */
    public static class ExceptionHandler implements Thread.UncaughtExceptionHandler {
        private Throwable firstException = null;

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            if ((firstException) == null) {
                firstException = ex;
            }
        }

        public void check() throws Exception {
            if ((firstException) != null) {
                throw new Exception("Thread failed", firstException);
            }
        }
    }

    /**
     * A thread that signs the same message multiple times.
     */
    public static class SigningThread extends Thread {
        private PublicKeySign signer;

        private Set<String> signatures;

        private byte[] message;

        private int count;

        /**
         * Constructs a thread that generates a number of signatures of a given message.
         *
         * @param signer
         * 		an instance that is signing messages.
         * @param signatures
         * 		a set to which the generated signatures are added. This must be a
         * 		synchronized set if it is shared among multiple threads.
         * @param message
         * 		the number message to be signed.
         * @param count
         * 		the number of signatures that are generated.
         */
        SigningThread(PublicKeySign signer, Set<String> signatures, byte[] message, int count) {
            this.signer = signer;
            this.signatures = signatures;
            this.message = message;
            this.count = count;
        }

        /**
         * Read the plaintext from the channel. This implementation assumes that the channel is blocking
         * and throws an AssertionError if an attempt to read plaintext from the channel is incomplete.
         */
        @Override
        public void run() {
            try {
                for (int i = 0; i < (count); i++) {
                    byte[] signature = signer.sign(message);
                    signatures.add(TestUtil.hexEncode(signature));
                }
            } catch (Exception ex) {
                getUncaughtExceptionHandler().uncaughtException(this, ex);
            }
        }
    }

    @Test
    public void testEcdsa() throws Exception {
        ECParameterSpec ecParams = EllipticCurves.getNistP256Params();
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        keyGen.initialize(ecParams);
        KeyPair keyPair = keyGen.generateKeyPair();
        ECPublicKey pub = ((ECPublicKey) (keyPair.getPublic()));
        ECPrivateKey priv = ((ECPrivateKey) (keyPair.getPrivate()));
        EcdsaSignJce signer = new EcdsaSignJce(priv, HashType.SHA256, EcdsaEncoding.DER);
        EcdsaVerifyJce verifier = new EcdsaVerifyJce(pub, HashType.SHA256, EcdsaEncoding.DER);
        byte[] msg = Random.randBytes(20);
        testSigningSameMessage(signer, verifier, false, msg, 5, 20);
        testSigningDistinctMessages(signer, verifier, false, 64, 5, 20);
    }

    @Test
    public void testEddsa() throws Exception {
        Ed25519Sign.KeyPair keyPair = Ed25519Sign.KeyPair.newKeyPair();
        Ed25519Sign signer = new Ed25519Sign(keyPair.getPrivateKey());
        Ed25519Verify verifier = new Ed25519Verify(keyPair.getPublicKey());
        byte[] msg = Random.randBytes(20);
        testSigningSameMessage(signer, verifier, true, msg, 5, 20);
        testSigningDistinctMessages(signer, verifier, true, 64, 5, 20);
    }
}

