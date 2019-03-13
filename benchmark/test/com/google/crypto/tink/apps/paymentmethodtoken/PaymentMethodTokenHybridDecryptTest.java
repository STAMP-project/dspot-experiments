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
package com.google.crypto.tink.apps.paymentmethodtoken;


import com.google.crypto.tink.HybridDecrypt;
import com.google.crypto.tink.HybridEncrypt;
import com.google.crypto.tink.apps.paymentmethodtoken.PaymentMethodTokenConstants.ProtocolVersionConfig;
import com.google.crypto.tink.subtle.EllipticCurves;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@code PaymentMethodTokenHybridDecrypt}.
 */
@RunWith(JUnit4.class)
public class PaymentMethodTokenHybridDecryptTest {
    @Test
    public void testModifyDecrypt() throws Exception {
        ECParameterSpec spec = EllipticCurves.getNistP256Params();
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        keyGen.initialize(spec);
        KeyPair recipientKey = keyGen.generateKeyPair();
        ECPublicKey recipientPublicKey = ((ECPublicKey) (recipientKey.getPublic()));
        ECPrivateKey recipientPrivateKey = ((ECPrivateKey) (recipientKey.getPrivate()));
        HybridEncrypt hybridEncrypt = new PaymentMethodTokenHybridEncrypt(recipientPublicKey, ProtocolVersionConfig.EC_V1);
        HybridDecrypt hybridDecrypt = new PaymentMethodTokenHybridDecrypt(recipientPrivateKey, ProtocolVersionConfig.EC_V1);
        testModifyDecrypt(hybridEncrypt, hybridDecrypt);
    }
}

