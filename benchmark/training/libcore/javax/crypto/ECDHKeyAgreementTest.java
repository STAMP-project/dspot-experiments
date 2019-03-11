/**
 * Copyright (C) 2013 The Android Open Source Project
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
package libcore.javax.crypto;


import java.security.Provider;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import junit.framework.TestCase;
import libcore.java.security.SignatureTest;


/**
 * Tests for all registered Elliptic Curve Diffie-Hellman {@link KeyAgreement} providers.
 */
public class ECDHKeyAgreementTest extends TestCase {
    // Two key pairs and the resulting shared secret for the Known Answer Test
    private static final byte[] KAT_PUBLIC_KEY1_X509 = SignatureTest.hexToBytes(("3059301306072a8648ce3d020106082a8648ce3d030107034200049fc2f71f85446b1371244491d83" + ("9cf97b5d27cedbb04d2c0058b59709df3a216e6b4ca1b2d622588c5a0e6968144a8965e816a600c" + "05305a1da3df2bf02b41d1")));

    private static final byte[] KAT_PRIVATE_KEY1_PKCS8 = SignatureTest.hexToBytes(("308193020100301306072a8648ce3d020106082a8648ce3d030107047930770201010420e1e683003" + (("c8b963a92742e5f955ce7fddc81d0c3ae9b149d6af86a0cacb2271ca00a06082a8648ce3d030107" + "a144034200049fc2f71f85446b1371244491d839cf97b5d27cedbb04d2c0058b59709df3a216e6b") + "4ca1b2d622588c5a0e6968144a8965e816a600c05305a1da3df2bf02b41d1")));

    private static final byte[] KAT_PUBLIC_KEY2_X509 = SignatureTest.hexToBytes(("3059301306072a8648ce3d020106082a8648ce3d03010703420004358efb6d91e5bbcae21774af3f6" + ("d85d0848630e7e61dbeb5ac9e47036ed0f8d38c7a1d1bb249f92861c7c9153fff33f45ab5b171eb" + "e8cad741125e6bb4fc6b07")));

    private static final byte[] KAT_PRIVATE_KEY2_PKCS8 = SignatureTest.hexToBytes(("308193020100301306072a8648ce3d020106082a8648ce3d0301070479307702010104202b1810a69" + (("e12b74d50bf0343168f705f0104f76299855268aa526fdb31e6eec0a00a06082a8648ce3d030107" + "a14403420004358efb6d91e5bbcae21774af3f6d85d0848630e7e61dbeb5ac9e47036ed0f8d38c7") + "a1d1bb249f92861c7c9153fff33f45ab5b171ebe8cad741125e6bb4fc6b07")));

    private static final byte[] KAT_SECRET = SignatureTest.hexToBytes("4faa0594c0e773eb26c8df2163af2443e88aab9578b9e1f324bc61e42d222783");

    private static final ECPublicKey KAT_PUBLIC_KEY1;

    private static final ECPrivateKey KAT_PRIVATE_KEY1;

    private static final ECPublicKey KAT_PUBLIC_KEY2;

    private static final ECPrivateKey KAT_PRIVATE_KEY2;

    static {
        try {
            KAT_PUBLIC_KEY1 = ECDHKeyAgreementTest.getPublicKey(ECDHKeyAgreementTest.KAT_PUBLIC_KEY1_X509);
            KAT_PRIVATE_KEY1 = ECDHKeyAgreementTest.getPrivateKey(ECDHKeyAgreementTest.KAT_PRIVATE_KEY1_PKCS8);
            KAT_PUBLIC_KEY2 = ECDHKeyAgreementTest.getPublicKey(ECDHKeyAgreementTest.KAT_PUBLIC_KEY2_X509);
            KAT_PRIVATE_KEY2 = ECDHKeyAgreementTest.getPrivateKey(ECDHKeyAgreementTest.KAT_PRIVATE_KEY2_PKCS8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decode KAT key pairs using default provider", e);
        }
    }

    /**
     * Performs a known-answer test of the shared secret for all permutations of {@code Providers}
     * of: first key pair, second key pair, and the {@code KeyAgreement}. This is to check that
     * the {@code KeyAgreement} instances work with keys of all registered providers.
     */
    public void testKnownAnswer() throws Exception {
        for (Provider keyFactoryProvider1 : ECDHKeyAgreementTest.getKeyFactoryProviders()) {
            ECPrivateKey privateKey1 = ECDHKeyAgreementTest.getPrivateKey(ECDHKeyAgreementTest.KAT_PRIVATE_KEY1_PKCS8, keyFactoryProvider1);
            ECPublicKey publicKey1 = ECDHKeyAgreementTest.getPublicKey(ECDHKeyAgreementTest.KAT_PUBLIC_KEY1_X509, keyFactoryProvider1);
            for (Provider keyFactoryProvider2 : ECDHKeyAgreementTest.getKeyFactoryProviders()) {
                ECPrivateKey privateKey2 = ECDHKeyAgreementTest.getPrivateKey(ECDHKeyAgreementTest.KAT_PRIVATE_KEY2_PKCS8, keyFactoryProvider2);
                ECPublicKey publicKey2 = ECDHKeyAgreementTest.getPublicKey(ECDHKeyAgreementTest.KAT_PUBLIC_KEY2_X509, keyFactoryProvider2);
                for (Provider keyAgreementProvider : ECDHKeyAgreementTest.getKeyAgreementProviders()) {
                    try {
                        testKnownAnswer(publicKey1, privateKey1, publicKey2, privateKey2, keyAgreementProvider);
                    } catch (Throwable e) {
                        throw new RuntimeException(((((((((getClass().getSimpleName()) + ".testKnownAnswer(") + (keyFactoryProvider1.getName())) + ", ") + (keyFactoryProvider2.getName())) + ", ") + (keyAgreementProvider.getName())) + ")"), e);
                    }
                }
            }
        }
    }

    public void testGetAlgorithm() throws Exception {
        invokeCallingMethodForEachKeyAgreementProvider();
    }

    public void testGetProvider() throws Exception {
        invokeCallingMethodForEachKeyAgreementProvider();
    }

    public void testInit_withNullPrivateKey() throws Exception {
        invokeCallingMethodForEachKeyAgreementProvider();
    }

    public void testInit_withUnsupportedPrivateKeyType() throws Exception {
        invokeCallingMethodForEachKeyAgreementProvider();
    }

    public void testInit_withUnsupportedAlgorithmParameterSpec() throws Exception {
        invokeCallingMethodForEachKeyAgreementProvider();
    }

    public void testDoPhase_whenNotInitialized() throws Exception {
        invokeCallingMethodForEachKeyAgreementProvider();
    }

    public void testDoPhaseReturnsNull() throws Exception {
        invokeCallingMethodForEachKeyAgreementProvider();
    }

    public void testDoPhase_withPhaseWhichIsNotLast() throws Exception {
        invokeCallingMethodForEachKeyAgreementProvider();
    }

    public void testDoPhase_withNullKey() throws Exception {
        invokeCallingMethodForEachKeyAgreementProvider();
    }

    public void testDoPhase_withInvalidKeyType() throws Exception {
        invokeCallingMethodForEachKeyAgreementProvider();
    }

    public void testGenerateSecret_withNullOutputBuffer() throws Exception {
        invokeCallingMethodForEachKeyAgreementProvider();
    }

    public void testGenerateSecret_withBufferOfTheRightSize() throws Exception {
        invokeCallingMethodForEachKeyAgreementProvider();
    }

    public void testGenerateSecret_withLargerThatNeededBuffer() throws Exception {
        invokeCallingMethodForEachKeyAgreementProvider();
    }

    public void testGenerateSecret_withSmallerThanNeededBuffer() throws Exception {
        invokeCallingMethodForEachKeyAgreementProvider();
    }

    public void testGenerateSecret_withoutBuffer() throws Exception {
        invokeCallingMethodForEachKeyAgreementProvider();
    }

    public void testGenerateSecret_withAlgorithm() throws Exception {
        invokeCallingMethodForEachKeyAgreementProvider();
    }
}

