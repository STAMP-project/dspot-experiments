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


import com.google.crypto.tink.TestUtil;
import com.google.crypto.tink.WycheproofTestUtil;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.HashSet;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for AesGcm.
 */
@RunWith(JUnit4.class)
public class AesGcmJceTest {
    private Integer[] keySizeInBytes;

    @Test
    public void testEncryptDecrypt() throws Exception {
        byte[] aad = new byte[]{ 1, 2, 3 };
        for (int keySize : keySizeInBytes) {
            byte[] key = Random.randBytes(keySize);
            AesGcmJce gcm = new AesGcmJce(key);
            for (int messageSize = 0; messageSize < 75; messageSize++) {
                byte[] message = Random.randBytes(messageSize);
                byte[] ciphertext = gcm.encrypt(message, aad);
                byte[] decrypted = gcm.decrypt(ciphertext, aad);
                Assert.assertArrayEquals(message, decrypted);
            }
        }
    }

    /**
     * BC had a bug, where GCM failed for messages of size > 8192
     */
    @Test
    public void testLongMessages() throws Exception {
        if (TestUtil.isAndroid()) {
            System.out.println("testLongMessages doesn't work on Android, skipping");
            return;
        }
        int dataSize = 16;
        while (dataSize <= (1 << 24)) {
            byte[] plaintext = Random.randBytes(dataSize);
            byte[] aad = Random.randBytes((dataSize / 3));
            for (int keySize : keySizeInBytes) {
                byte[] key = Random.randBytes(keySize);
                AesGcmJce gcm = new AesGcmJce(key);
                byte[] ciphertext = gcm.encrypt(plaintext, aad);
                byte[] decrypted = gcm.decrypt(ciphertext, aad);
                Assert.assertArrayEquals(plaintext, decrypted);
            }
            dataSize += (5 * dataSize) / 11;
        } 
    }

    @Test
    public void testModifyCiphertext() throws Exception {
        byte[] aad = Random.randBytes(33);
        byte[] key = Random.randBytes(16);
        byte[] message = Random.randBytes(32);
        AesGcmJce gcm = new AesGcmJce(key);
        byte[] ciphertext = gcm.encrypt(message, aad);
        for (TestUtil.BytesMutation mutation : TestUtil.generateMutations(ciphertext)) {
            try {
                byte[] unused = gcm.decrypt(mutation.value, aad);
                Assert.fail(String.format(("Decrypting modified ciphertext should fail : ciphertext = %s, aad = %s," + " description = %s"), Hex.encode(mutation.value), Hex.encode(aad), mutation.description));
            } catch (GeneralSecurityException ex) {
                // This is expected.
                // This could be a AeadBadTagException when the tag verification
                // fails or some not yet specified Exception when the ciphertext is too short.
                // In all cases a GeneralSecurityException or a subclass of it must be thrown.
            }
        }
        // Modify AAD
        for (TestUtil.BytesMutation mutation : TestUtil.generateMutations(aad)) {
            try {
                byte[] unused = gcm.decrypt(ciphertext, mutation.value);
                Assert.fail(String.format(("Decrypting with modified aad should fail: ciphertext = %s, aad = %s," + " description = %s"), ciphertext, mutation.value, mutation.description));
            } catch (GeneralSecurityException ex) {
                // This is expected.
                // This could be a AeadBadTagException when the tag verification
                // fails or some not yet specified Exception when the ciphertext is too short.
                // In all cases a GeneralSecurityException or a subclass of it must be thrown.
            }
        }
    }

    @Test
    public void testWycheproofVectors() throws Exception {
        JSONObject json = WycheproofTestUtil.readJson("../wycheproof/testvectors/aes_gcm_test.json");
        int errors = 0;
        int cntSkippedTests = 0;
        JSONArray testGroups = json.getJSONArray("testGroups");
        for (int i = 0; i < (testGroups.length()); i++) {
            JSONObject group = testGroups.getJSONObject(i);
            int keySize = group.getInt("keySize");
            JSONArray tests = group.getJSONArray("tests");
            if (!(Arrays.asList(keySizeInBytes).contains((keySize / 8)))) {
                cntSkippedTests += tests.length();
                continue;
            }
            for (int j = 0; j < (tests.length()); j++) {
                JSONObject testcase = tests.getJSONObject(j);
                String tcId = String.format("testcase %d (%s)", testcase.getInt("tcId"), testcase.getString("comment"));
                byte[] iv = Hex.decode(testcase.getString("iv"));
                byte[] key = Hex.decode(testcase.getString("key"));
                byte[] msg = Hex.decode(testcase.getString("msg"));
                byte[] aad = Hex.decode(testcase.getString("aad"));
                byte[] ct = Hex.decode(testcase.getString("ct"));
                byte[] tag = Hex.decode(testcase.getString("tag"));
                byte[] ciphertext = Bytes.concat(iv, ct, tag);
                // Result is one of "valid", "invalid", "acceptable".
                // "valid" are test vectors with matching plaintext, ciphertext and tag.
                // "invalid" are test vectors with invalid parameters or invalid ciphertext and tag.
                // "acceptable" are test vectors with weak parameters or legacy formats.
                String result = testcase.getString("result");
                // Tink only supports 12-byte iv.
                if ((iv.length) != 12) {
                    result = "invalid";
                }
                try {
                    AesGcmJce gcm = new AesGcmJce(key);
                    byte[] decrypted = gcm.decrypt(ciphertext, aad);
                    boolean eq = TestUtil.arrayEquals(decrypted, msg);
                    if (result.equals("invalid")) {
                        System.out.printf("FAIL %s: accepting invalid ciphertext, cleartext: %s, decrypted: %s%n", tcId, Hex.encode(msg), Hex.encode(decrypted));
                        errors++;
                    } else {
                        if (!eq) {
                            System.out.printf("FAIL %s: incorrect decryption, result: %s, expected: %s%n", tcId, Hex.encode(decrypted), Hex.encode(msg));
                            errors++;
                        }
                    }
                } catch (GeneralSecurityException ex) {
                    if (result.equals("valid")) {
                        System.out.printf("FAIL %s: cannot decrypt, exception %s%n", tcId, ex);
                        errors++;
                    }
                }
            }
        }
        System.out.printf("Number of tests skipped: %d", cntSkippedTests);
        Assert.assertEquals(0, errors);
    }

    @Test
    public void testNullPlaintextOrCiphertext() throws Exception {
        for (int keySize : keySizeInBytes) {
            AesGcmJce gcm = new AesGcmJce(Random.randBytes(keySize));
            try {
                byte[] aad = new byte[]{ 1, 2, 3 };
                byte[] unused = gcm.encrypt(null, aad);
                Assert.fail("Encrypting a null plaintext should fail");
            } catch (NullPointerException ex) {
                // This is expected.
            }
            try {
                byte[] unused = gcm.encrypt(null, null);
                Assert.fail("Encrypting a null plaintext should fail");
            } catch (NullPointerException ex) {
                // This is expected.
            }
            try {
                byte[] aad = new byte[]{ 1, 2, 3 };
                byte[] unused = gcm.decrypt(null, aad);
                Assert.fail("Decrypting a null ciphertext should fail");
            } catch (NullPointerException ex) {
                // This is expected.
            }
            try {
                byte[] unused = gcm.decrypt(null, null);
                Assert.fail("Decrypting a null ciphertext should fail");
            } catch (NullPointerException ex) {
                // This is expected.
            }
        }
    }

    @Test
    public void testEmptyAssociatedData() throws Exception {
        byte[] aad = new byte[0];
        for (int keySize : keySizeInBytes) {
            byte[] key = Random.randBytes(keySize);
            AesGcmJce gcm = new AesGcmJce(key);
            for (int messageSize = 0; messageSize < 75; messageSize++) {
                byte[] message = Random.randBytes(messageSize);
                {
                    // encrypting with aad as a 0-length array
                    byte[] ciphertext = gcm.encrypt(message, aad);
                    byte[] decrypted = gcm.decrypt(ciphertext, aad);
                    Assert.assertArrayEquals(message, decrypted);
                    byte[] decrypted2 = gcm.decrypt(ciphertext, null);
                    Assert.assertArrayEquals(message, decrypted2);
                    try {
                        byte[] badAad = new byte[]{ 1, 2, 3 };
                        byte[] unused = gcm.decrypt(ciphertext, badAad);
                        Assert.fail("Decrypting with modified aad should fail");
                    } catch (GeneralSecurityException ex) {
                        // This is expected.
                        // This could be a AeadBadTagException when the tag verification
                        // fails or some not yet specified Exception when the ciphertext is too short.
                        // In all cases a GeneralSecurityException or a subclass of it must be thrown.
                    }
                }
                {
                    // encrypting with aad equal to null
                    byte[] ciphertext = gcm.encrypt(message, null);
                    byte[] decrypted = gcm.decrypt(ciphertext, aad);
                    Assert.assertArrayEquals(message, decrypted);
                    byte[] decrypted2 = gcm.decrypt(ciphertext, null);
                    Assert.assertArrayEquals(message, decrypted2);
                    try {
                        byte[] badAad = new byte[]{ 1, 2, 3 };
                        byte[] unused = gcm.decrypt(ciphertext, badAad);
                        Assert.fail("Decrypting with modified aad should fail");
                    } catch (GeneralSecurityException ex) {
                        // This is expected.
                        // This could be a AeadBadTagException when the tag verification
                        // fails or some not yet specified Exception when the ciphertext is too short.
                        // In all cases a GeneralSecurityException or a subclass of it must be thrown.
                    }
                }
            }
        }
    }

    /**
     * This is a very simple test for the randomness of the nonce. The test simply checks that the
     * multiple ciphertexts of the same message are distinct.
     */
    @Test
    public void testRandomNonce() throws Exception {
        final int samples = 1 << 17;
        byte[] key = Random.randBytes(16);
        byte[] message = new byte[0];
        byte[] aad = new byte[0];
        AesGcmJce gcm = new AesGcmJce(key);
        HashSet<String> ciphertexts = new HashSet<String>();
        for (int i = 0; i < samples; i++) {
            byte[] ct = gcm.encrypt(message, aad);
            String ctHex = TestUtil.hexEncode(ct);
            Assert.assertFalse(ciphertexts.contains(ctHex));
            ciphertexts.add(ctHex);
        }
    }
}

