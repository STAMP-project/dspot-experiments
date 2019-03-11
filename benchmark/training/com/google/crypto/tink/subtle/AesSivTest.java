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


import AesUtil.BLOCK_SIZE;
import com.google.crypto.tink.DeterministicAead;
import com.google.crypto.tink.WycheproofTestUtil;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.util.Arrays;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for AesSiv
 */
@RunWith(JUnit4.class)
public class AesSivTest {
    private Integer[] keySizeInBytes;

    @Test
    public void testWycheproofVectors() throws Exception {
        JSONObject json = WycheproofTestUtil.readJson("../wycheproof/testvectors/aes_siv_cmac_test.json");
        JSONArray testGroups = json.getJSONArray("testGroups");
        int cntSkippedTests = 0;
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
                byte[] key = Hex.decode(testcase.getString("key"));
                byte[] msg = Hex.decode(testcase.getString("msg"));
                byte[] aad = Hex.decode(testcase.getString("aad"));
                byte[] ct = Hex.decode(testcase.getString("ct"));
                // Result is one of "valid" and "invalid".
                // "valid" are test vectors with matching plaintext and ciphertext.
                // "invalid" are test vectors with invalid parameters or invalid ciphertext.
                String result = testcase.getString("result");
                DeterministicAead daead = new AesSiv(key);
                if (result.equals("valid")) {
                    byte[] ciphertext = daead.encryptDeterministically(msg, aad);
                    Assert.assertEquals(tcId, Hex.encode(ct), Hex.encode(ciphertext));
                    byte[] plaintext = daead.decryptDeterministically(ct, aad);
                    Assert.assertEquals(tcId, Hex.encode(msg), Hex.encode(plaintext));
                } else {
                    try {
                        byte[] plaintext = daead.decryptDeterministically(ct, aad);
                        Assert.fail(String.format("FAIL %s: decrypted invalid ciphertext as %s", tcId, Hex.encode(plaintext)));
                    } catch (GeneralSecurityException ex) {
                        // This is expected
                    }
                }
            }
        }
        System.out.printf("Number of tests skipped: %d", cntSkippedTests);
    }

    @Test
    public void testEncryptDecryptWithEmptyPlaintext() throws GeneralSecurityException {
        for (int keySize : keySizeInBytes) {
            DeterministicAead dead = new AesSiv(Random.randBytes(keySize));
            for (int triesPlaintext = 0; triesPlaintext < 100; triesPlaintext++) {
                byte[] plaintext = new byte[0];
                byte[] aad = Random.randBytes(((Random.randInt(128)) + 1));
                byte[] ciphertext = dead.encryptDeterministically(plaintext, aad);
                byte[] rebuiltPlaintext = dead.decryptDeterministically(ciphertext, aad);
                Assert.assertEquals(BLOCK_SIZE, ciphertext.length);
                Assert.assertEquals(Hex.encode(plaintext), Hex.encode(rebuiltPlaintext));
            }
        }
    }

    @Test
    public void testEncryptDecryptWithEmptyAssociatedData() throws GeneralSecurityException {
        for (int keySize : keySizeInBytes) {
            DeterministicAead dead = new AesSiv(Random.randBytes(keySize));
            for (int triesPlaintext = 0; triesPlaintext < 100; triesPlaintext++) {
                byte[] plaintext = Random.randBytes(((Random.randInt(1024)) + 1));
                byte[] aad = new byte[0];
                byte[] rebuiltPlaintext = dead.decryptDeterministically(dead.encryptDeterministically(plaintext, aad), aad);
                Assert.assertEquals(Hex.encode(plaintext), Hex.encode(rebuiltPlaintext));
            }
        }
    }

    @Test
    public void testEncryptDecryptWithEmptyPlaintextAndEmptyAssociatedData() throws GeneralSecurityException {
        for (int keySize : keySizeInBytes) {
            DeterministicAead dead = new AesSiv(Random.randBytes(keySize));
            for (int triesPlaintext = 0; triesPlaintext < 100; triesPlaintext++) {
                byte[] plaintext = new byte[0];
                byte[] aad = new byte[0];
                byte[] rebuiltPlaintext = dead.decryptDeterministically(dead.encryptDeterministically(plaintext, aad), aad);
                Assert.assertEquals(Hex.encode(plaintext), Hex.encode(rebuiltPlaintext));
            }
        }
    }

    @Test
    public void testEncryptDecrypt() throws GeneralSecurityException {
        for (int keySize : keySizeInBytes) {
            DeterministicAead dead = new AesSiv(Random.randBytes(keySize));
            for (int triesPlaintext = 0; triesPlaintext < 100; triesPlaintext++) {
                byte[] plaintext = Random.randBytes(((Random.randInt(1024)) + 1));
                byte[] aad = Random.randBytes(((Random.randInt(128)) + 1));
                byte[] rebuiltPlaintext = dead.decryptDeterministically(dead.encryptDeterministically(plaintext, aad), aad);
                Assert.assertEquals(Hex.encode(plaintext), Hex.encode(rebuiltPlaintext));
            }
        }
    }

    @Test
    public void testModifiedCiphertext() throws GeneralSecurityException {
        for (int keySize : keySizeInBytes) {
            AesSivTest.testModifiedCiphertext(keySize);
        }
    }

    @Test
    public void testModifiedAssociatedData() throws GeneralSecurityException {
        for (int keySize : keySizeInBytes) {
            AesSivTest.testModifiedAssociatedData(keySize);
        }
    }

    @Test
    public void testInvalidKeySizes() throws GeneralSecurityException {
        try {
            // AesSiv doesn't accept 32-byte keys.
            new AesSiv(Random.randBytes(32));
            Assert.fail("32-byte keys should not be accepted");
        } catch (InvalidKeyException ex) {
            // expected.
        }
        for (int j = 0; j < 100; j++) {
            if ((j == 48) || (j == 64)) {
                continue;
            }
            try {
                new AesSiv(Random.randBytes(j));
                Assert.fail(("Keys with invalid size should not be accepted: " + j));
            } catch (InvalidKeyException ex) {
                // expected.
            }
        }
    }
}

