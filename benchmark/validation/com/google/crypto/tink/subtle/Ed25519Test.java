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


import Ed25519.GROUP_ORDER;
import Ed25519.GROUP_ORDER.length;
import Field25519.FIELD_LEN;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for Ed25519.
 */
@RunWith(JUnit4.class)
public class Ed25519Test {
    @Test
    public void testGroupOrder() throws Exception {
        Assert.assertEquals(32, length);
        byte[] result = Ed25519.scalarMultWithBaseToBytes(GROUP_ORDER);
        Assert.assertEquals(1, result[0]);
        for (int i = 1; i < 32; i++) {
            Assert.assertEquals(0, result[i]);
        }
    }

    /**
     * Test whether sign/verify method accidentally changes the public key or hashedPrivateKey.
     */
    @Test
    public void testUnmodifiedKey() throws Exception {
        byte[] privateKey = Random.randBytes(FIELD_LEN);
        byte[] hashedPrivateKey = Ed25519.getHashedScalar(privateKey);
        byte[] originalHashedPrivateKey = Arrays.copyOfRange(hashedPrivateKey, 0, hashedPrivateKey.length);
        byte[] publicKey = Ed25519.scalarMultWithBaseToBytes(hashedPrivateKey);
        byte[] originalPublicKey = Arrays.copyOfRange(publicKey, 0, publicKey.length);
        for (int i = 0; i < 64; i++) {
            byte[] msg = Random.randBytes(1024);
            byte[] sig = Ed25519.sign(msg, publicKey, hashedPrivateKey);
            Assert.assertTrue(Ed25519.verify(msg, sig, publicKey));
            Assert.assertArrayEquals(originalHashedPrivateKey, hashedPrivateKey);
            Assert.assertArrayEquals(originalPublicKey, publicKey);
        }
    }
}

