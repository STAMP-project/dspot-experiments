/**
 * Copyright (C) 2010 The Android Open Source Project
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


import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import junit.framework.TestCase;


public class SecretKeyFactoryTest extends TestCase {
    private static final char[] PASSWORD = "google".toCharArray();

    /**
     * Salts should be random to reduce effectiveness of dictionary
     * attacks, but need not be kept secret from attackers. For more
     * information, see http://en.wikipedia.org/wiki/Salt_(cryptography)
     */
    private static final byte[] SALT = new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7 };

    /**
     * The number of iterations should be higher for production
     * strength protection. The tolerable value may vary from device
     * to device, but 8192 should be acceptable for PBKDF2 on a Nexus One.
     */
    private static final int ITERATIONS = 1024;

    private static final int KEY_LENGTH = 128;

    public void test_PBKDF2_required_parameters() throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        // PBEKeySpec validates arguments most to be non-null, non-empty, postive, etc.
        // Focus on insufficient PBEKeySpecs
        // PBEKeySpecs password only constructor
        try {
            KeySpec ks = new PBEKeySpec(null);
            factory.generateSecret(ks);
            TestCase.fail();
        } catch (InvalidKeySpecException expected) {
        }
        try {
            KeySpec ks = new PBEKeySpec(new char[0]);
            factory.generateSecret(ks);
            TestCase.fail();
        } catch (InvalidKeySpecException expected) {
        }
        try {
            KeySpec ks = new PBEKeySpec(SecretKeyFactoryTest.PASSWORD);
            factory.generateSecret(ks);
            TestCase.fail();
        } catch (InvalidKeySpecException expected) {
        }
        // PBEKeySpecs constructor without key length
        try {
            KeySpec ks = new PBEKeySpec(null, SecretKeyFactoryTest.SALT, SecretKeyFactoryTest.ITERATIONS);
            factory.generateSecret(ks);
            TestCase.fail();
        } catch (InvalidKeySpecException expected) {
        }
        try {
            KeySpec ks = new PBEKeySpec(new char[0], SecretKeyFactoryTest.SALT, SecretKeyFactoryTest.ITERATIONS);
            factory.generateSecret(ks);
            TestCase.fail();
        } catch (InvalidKeySpecException expected) {
        }
        try {
            KeySpec ks = new PBEKeySpec(SecretKeyFactoryTest.PASSWORD, SecretKeyFactoryTest.SALT, SecretKeyFactoryTest.ITERATIONS);
            factory.generateSecret(ks);
            TestCase.fail();
        } catch (InvalidKeySpecException expected) {
        }
        try {
            KeySpec ks = new PBEKeySpec(null, SecretKeyFactoryTest.SALT, SecretKeyFactoryTest.ITERATIONS, SecretKeyFactoryTest.KEY_LENGTH);
            factory.generateSecret(ks);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            KeySpec ks = new PBEKeySpec(new char[0], SecretKeyFactoryTest.SALT, SecretKeyFactoryTest.ITERATIONS, SecretKeyFactoryTest.KEY_LENGTH);
            factory.generateSecret(ks);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        KeySpec ks = new PBEKeySpec(SecretKeyFactoryTest.PASSWORD, SecretKeyFactoryTest.SALT, SecretKeyFactoryTest.ITERATIONS, SecretKeyFactoryTest.KEY_LENGTH);
        factory.generateSecret(ks);
    }

    public void test_PBKDF2_b3059950() throws Exception {
        byte[] expected = new byte[]{ ((byte) (112)), ((byte) (116)), ((byte) (219)), ((byte) (114)), ((byte) (53)), ((byte) (212)), ((byte) (17)), ((byte) (104)), ((byte) (131)), ((byte) (124)), ((byte) (20)), ((byte) (31)), ((byte) (246)), ((byte) (74)), ((byte) (176)), ((byte) (84)) };
        test_PBKDF2_UTF8(SecretKeyFactoryTest.PASSWORD, SecretKeyFactoryTest.SALT, SecretKeyFactoryTest.ITERATIONS, SecretKeyFactoryTest.KEY_LENGTH, expected);
        test_PBKDF2_8BIT(SecretKeyFactoryTest.PASSWORD, SecretKeyFactoryTest.SALT, SecretKeyFactoryTest.ITERATIONS, SecretKeyFactoryTest.KEY_LENGTH, expected);
    }

    /**
     * 64-bit Test vector from RFC 3211
     *
     * See also org.bouncycastle.crypto.test.PKCS5Test
     */
    public void test_PBKDF2_rfc3211_64() throws Exception {
        char[] password = "password".toCharArray();
        byte[] salt = new byte[]{ ((byte) (18)), ((byte) (52)), ((byte) (86)), ((byte) (120)), ((byte) (120)), ((byte) (86)), ((byte) (52)), ((byte) (18)) };
        int iterations = 5;
        int keyLength = 64;
        byte[] expected = new byte[]{ ((byte) (209)), ((byte) (218)), ((byte) (167)), ((byte) (134)), ((byte) (21)), ((byte) (242)), ((byte) (135)), ((byte) (230)) };
        test_PBKDF2_UTF8(password, salt, iterations, keyLength, expected);
        test_PBKDF2_8BIT(password, salt, iterations, keyLength, expected);
    }

    /**
     * 192-bit Test vector from RFC 3211
     *
     * See also org.bouncycastle.crypto.test.PKCS5Test
     */
    public void test_PBKDF2_rfc3211_192() throws Exception {
        char[] password = ("All n-entities must communicate with other " + "n-entities via n-1 entiteeheehees").toCharArray();
        byte[] salt = new byte[]{ ((byte) (18)), ((byte) (52)), ((byte) (86)), ((byte) (120)), ((byte) (120)), ((byte) (86)), ((byte) (52)), ((byte) (18)) };
        int iterations = 500;
        int keyLength = 192;
        byte[] expected = new byte[]{ ((byte) (106)), ((byte) (137)), ((byte) (112)), ((byte) (191)), ((byte) (104)), ((byte) (201)), ((byte) (44)), ((byte) (174)), ((byte) (168)), ((byte) (74)), ((byte) (141)), ((byte) (242)), ((byte) (133)), ((byte) (16)), ((byte) (133)), ((byte) (134)), ((byte) (7)), ((byte) (18)), ((byte) (99)), ((byte) (128)), ((byte) (204)), ((byte) (71)), ((byte) (171)), ((byte) (45)) };
        test_PBKDF2_UTF8(password, salt, iterations, keyLength, expected);
        test_PBKDF2_8BIT(password, salt, iterations, keyLength, expected);
    }

    /**
     * Unicode Test vector for b/8312059.
     *
     * See also https://code.google.com/p/android/issues/detail?id=40578
     */
    public void test_PBKDF2_b8312059() throws Exception {
        char[] password = "\u0141\u0142".toCharArray();
        byte[] salt = "salt".getBytes();
        int iterations = 4096;
        int keyLength = 160;
        byte[] expected_utf8 = new byte[]{ ((byte) (76)), ((byte) (224)), ((byte) (106)), ((byte) (184)), ((byte) (72)), ((byte) (4)), ((byte) (183)), ((byte) (231)), ((byte) (114)), ((byte) (242)), ((byte) (175)), ((byte) (94)), ((byte) (84)), ((byte) (233)), ((byte) (3)), ((byte) (173)), ((byte) (89)), ((byte) (100)), ((byte) (139)), ((byte) (171)) };
        byte[] expected_8bit = new byte[]{ ((byte) (110)), ((byte) (67)), ((byte) (224)), ((byte) (24)), ((byte) (197)), ((byte) (80)), ((byte) (13)), ((byte) (167)), ((byte) (254)), ((byte) (122)), ((byte) (68)), ((byte) (77)), ((byte) (153)), ((byte) (93)), ((byte) (140)), ((byte) (174)), ((byte) (193)), ((byte) (201)), ((byte) (23)), ((byte) (206)) };
        test_PBKDF2_UTF8(password, salt, iterations, keyLength, expected_utf8);
        test_PBKDF2_8BIT(password, salt, iterations, keyLength, expected_8bit);
    }
}

