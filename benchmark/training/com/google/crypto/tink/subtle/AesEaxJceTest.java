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
import javax.crypto.AEADBadTagException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for AesEax.
 *
 * <p>TODO: Add more tests:
 *
 * <ul>
 *   <li>- maybe add NIST style verification.
 *   <li>- tests with long ciphertexts (e.g. BC had a bug with messages of size 8k or longer)
 *   <li>- check that IVs are distinct.
 *   <li>- use Github Wycheproof test vectors once they're published (b/66825199).
 * </ul>
 */
@RunWith(JUnit4.class)
public class AesEaxJceTest {
    private static final int KEY_SIZE = 16;

    private static final int IV_SIZE = 16;

    private Integer[] keySizeInBytes;

    private Integer[] ivSizeInBytes;

    @Test
    public void testWycheproofVectors() throws Exception {
        JSONObject json = WycheproofTestUtil.readJson("../wycheproof/testvectors/aes_eax_test.json");
        int errors = 0;
        int cntSkippedTests = 0;
        JSONArray testGroups = json.getJSONArray("testGroups");
        for (int i = 0; i < (testGroups.length()); i++) {
            JSONObject group = testGroups.getJSONObject(i);
            int keySize = group.getInt("keySize");
            int ivSize = group.getInt("ivSize");
            JSONArray tests = group.getJSONArray("tests");
            if ((!(Arrays.asList(keySizeInBytes).contains((keySize / 8)))) || (!(Arrays.asList(ivSizeInBytes).contains((ivSize / 8))))) {
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
                String result = testcase.getString("result");
                try {
                    AesEaxJce eax = new AesEaxJce(key, iv.length);
                    byte[] decrypted = eax.decrypt(ciphertext, aad);
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
    public void testEncryptDecrypt() throws Exception {
        byte[] aad = new byte[]{ 1, 2, 3 };
        byte[] key = Random.randBytes(AesEaxJceTest.KEY_SIZE);
        AesEaxJce eax = new AesEaxJce(key, AesEaxJceTest.IV_SIZE);
        for (int messageSize = 0; messageSize < 75; messageSize++) {
            byte[] message = Random.randBytes(messageSize);
            byte[] ciphertext = eax.encrypt(message, aad);
            byte[] decrypted = eax.decrypt(ciphertext, aad);
            Assert.assertArrayEquals(message, decrypted);
        }
    }

    @Test
    public void testModifyCiphertext() throws Exception {
        testModifyCiphertext(16, 16);
        testModifyCiphertext(16, 12);
        // TODO(bleichen): Skipping test with key sizes larger than 128 bits because of b/35928521.
        // testModifyCiphertext(24, 16);
        // testModifyCiphertext(32, 16);
    }

    @Test
    public void testNullPlaintextOrCiphertext() throws Exception {
        AesEaxJce eax = new AesEaxJce(Random.randBytes(AesEaxJceTest.KEY_SIZE), AesEaxJceTest.IV_SIZE);
        try {
            byte[] aad = new byte[]{ 1, 2, 3 };
            byte[] unused = eax.encrypt(null, aad);
            Assert.fail("Encrypting a null plaintext should fail");
        } catch (NullPointerException ex) {
            // This is expected.
        }
        try {
            byte[] unused = eax.encrypt(null, null);
            Assert.fail("Encrypting a null plaintext should fail");
        } catch (NullPointerException ex) {
            // This is expected.
        }
        try {
            byte[] aad = new byte[]{ 1, 2, 3 };
            byte[] unused = eax.decrypt(null, aad);
            Assert.fail("Decrypting a null ciphertext should fail");
        } catch (NullPointerException ex) {
            // This is expected.
        }
        try {
            byte[] unused = eax.decrypt(null, null);
            Assert.fail("Decrypting a null ciphertext should fail");
        } catch (NullPointerException ex) {
            // This is expected.
        }
    }

    @Test
    public void testEmptyAssociatedData() throws Exception {
        byte[] aad = new byte[0];
        byte[] key = Random.randBytes(AesEaxJceTest.KEY_SIZE);
        AesEaxJce eax = new AesEaxJce(key, AesEaxJceTest.IV_SIZE);
        for (int messageSize = 0; messageSize < 75; messageSize++) {
            byte[] message = Random.randBytes(messageSize);
            {
                // encrypting with aad as a 0-length array
                byte[] ciphertext = eax.encrypt(message, aad);
                byte[] decrypted = eax.decrypt(ciphertext, aad);
                Assert.assertArrayEquals(message, decrypted);
                byte[] decrypted2 = eax.decrypt(ciphertext, null);
                Assert.assertArrayEquals(message, decrypted2);
                try {
                    byte[] badAad = new byte[]{ 1, 2, 3 };
                    byte[] unused = eax.decrypt(ciphertext, badAad);
                    Assert.fail("Decrypting with modified aad should fail");
                } catch (AEADBadTagException ex) {
                    // This is expected.
                }
            }
            {
                // encrypting with aad equal to null
                byte[] ciphertext = eax.encrypt(message, null);
                byte[] decrypted = eax.decrypt(ciphertext, aad);
                Assert.assertArrayEquals(message, decrypted);
                byte[] decrypted2 = eax.decrypt(ciphertext, null);
                Assert.assertArrayEquals(message, decrypted2);
                try {
                    byte[] badAad = new byte[]{ 1, 2, 3 };
                    byte[] unused = eax.decrypt(ciphertext, badAad);
                    Assert.fail("Decrypting with modified aad should fail");
                } catch (AEADBadTagException ex) {
                    // This is expected.
                }
            }
        }
    }
}

