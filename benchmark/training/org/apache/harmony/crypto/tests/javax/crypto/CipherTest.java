/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.harmony.crypto.tests.javax.crypto;


import StandardNames.IS_RI;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherSpi;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import junit.framework.TestCase;
import org.apache.harmony.crypto.tests.support.MyCipher;
import tests.support.resource.Support_Resources;


public class CipherTest extends TestCase {
    private static final Key CIPHER_KEY_3DES;

    private static final String ALGORITHM_3DES = "DESede";

    private static final Key CIPHER_KEY_DES;

    private static final String ALGORITHM_DES = "DES";

    private static final byte[] IV = new byte[]{ ((byte) (0)), ((byte) (1)), ((byte) (2)), ((byte) (3)), ((byte) (4)), ((byte) (5)), ((byte) (6)), ((byte) (7)) };

    static {
        try {
            byte[] encoded_3des = new byte[]{ ((byte) (-57)), ((byte) (-108)), ((byte) (-42)), ((byte) (47)), ((byte) (42)), ((byte) (-12)), ((byte) (-53)), ((byte) (-83)), ((byte) (-123)), ((byte) (91)), ((byte) (-99)), ((byte) (-101)), ((byte) (93)), ((byte) (-62)), ((byte) (-42)), ((byte) (-128)), ((byte) (-2)), ((byte) (47)), ((byte) (-8)), ((byte) (69)), ((byte) (-68)), ((byte) (69)), ((byte) (81)), ((byte) (74)) };
            CIPHER_KEY_3DES = new SecretKeySpec(encoded_3des, CipherTest.ALGORITHM_3DES);
            byte[] encoded_des = new byte[]{ ((byte) (2)), ((byte) (41)), ((byte) (-77)), ((byte) (107)), ((byte) (-99)), ((byte) (-63)), ((byte) (-42)), ((byte) (-89)) };
            CIPHER_KEY_DES = new SecretKeySpec(encoded_des, CipherTest.ALGORITHM_DES);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    /**
     * javax.crypto.Cipher#getInstance(java.lang.String)
     */
    public void test_getInstanceLjava_lang_String() throws Exception {
        Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        TestCase.assertNotNull("Received a null Cipher instance", cipher);
        try {
            Cipher.getInstance("WrongAlgorithmName");
            TestCase.fail();
        } catch (NoSuchAlgorithmException expected) {
        }
    }

    /**
     * javax.crypto.Cipher#getInstance(java.lang.String,
     *        java.lang.String)
     */
    public void test_getInstanceLjava_lang_StringLjava_lang_String() throws Exception {
        Provider[] providers = Security.getProviders("Cipher.DES");
        TestCase.assertNotNull("No installed providers support Cipher.DES", providers);
        for (int i = 0; i < (providers.length); i++) {
            Cipher cipher = Cipher.getInstance("DES", providers[i].getName());
            TestCase.assertNotNull("Cipher.getInstance() returned a null value", cipher);
            try {
                cipher = Cipher.getInstance("DoBeDoBeDo", providers[i]);
                TestCase.fail();
            } catch (NoSuchAlgorithmException expected) {
            }
        }
        try {
            Cipher.getInstance("DES", ((String) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Cipher.getInstance("DES", "IHaveNotBeenConfigured");
            TestCase.fail();
        } catch (NoSuchProviderException expected) {
        }
    }

    /**
     * javax.crypto.Cipher#getInstance(java.lang.String,
     *        java.security.Provider)
     */
    public void test_getInstanceLjava_lang_StringLjava_security_Provider() throws Exception {
        Provider[] providers = Security.getProviders("Cipher.DES");
        TestCase.assertNotNull("No installed providers support Cipher.DES", providers);
        for (int i = 0; i < (providers.length); i++) {
            Cipher cipher = Cipher.getInstance("DES", providers[i]);
            TestCase.assertNotNull("Cipher.getInstance() returned a null value", cipher);
        }
        try {
            Cipher.getInstance("DES", ((Provider) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Cipher.getInstance("WrongAlg", providers[0]);
            TestCase.fail();
        } catch (NoSuchAlgorithmException expected) {
        }
    }

    /**
     * javax.crypto.Cipher#getProvider()
     */
    public void test_getProvider() throws Exception {
        Provider[] providers = Security.getProviders("Cipher.AES");
        TestCase.assertNotNull("No providers support Cipher.AES", providers);
        for (int i = 0; i < (providers.length); i++) {
            Provider provider = providers[i];
            Cipher cipher = Cipher.getInstance("AES", provider.getName());
            Provider cipherProvider = cipher.getProvider();
            TestCase.assertTrue(("Cipher provider is not the same as that " + "provided as parameter to getInstance()"), cipherProvider.equals(provider));
        }
    }

    /**
     * javax.crypto.Cipher#getAlgorithm()
     */
    public void test_getAlgorithm() throws Exception {
        final String algorithm = "DESede/CBC/PKCS5Padding";
        Cipher cipher = Cipher.getInstance(algorithm);
        TestCase.assertTrue("Cipher algorithm does not match", cipher.getAlgorithm().equals(algorithm));
    }

    /**
     * javax.crypto.Cipher#getBlockSize()
     */
    public void test_getBlockSize() throws Exception {
        final String algorithm = "DESede/CBC/PKCS5Padding";
        Cipher cipher = Cipher.getInstance(algorithm);
        TestCase.assertEquals("Block size does not match", 8, cipher.getBlockSize());
    }

    /**
     * javax.crypto.Cipher#getOutputSize(int)
     */
    public void test_getOutputSizeI() throws Exception {
        Cipher cipher = Cipher.getInstance(((CipherTest.ALGORITHM_3DES) + "/ECB/PKCS5Padding"));
        try {
            cipher.getOutputSize(25);
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        cipher.init(Cipher.ENCRYPT_MODE, CipherTest.CIPHER_KEY_3DES, new SecureRandom());
        // A 25-byte input could result in at least 4 8-byte blocks
        int result = cipher.getOutputSize(25);
        TestCase.assertTrue("Output size too small", (result > 31));
        // A 8-byte input should result in 2 8-byte blocks
        result = cipher.getOutputSize(8);
        TestCase.assertTrue("Output size too small", (result > 15));
    }

    /**
     * javax.crypto.Cipher#init(int, java.security.Key)
     */
    public void test_initWithKey() throws Exception {
        Cipher cipher = Cipher.getInstance(((CipherTest.ALGORITHM_3DES) + "/ECB/PKCS5Padding"));
        cipher.init(Cipher.ENCRYPT_MODE, CipherTest.CIPHER_KEY_3DES);
        cipher = Cipher.getInstance("DES/CBC/NoPadding");
        try {
            cipher.init(Cipher.ENCRYPT_MODE, CipherTest.CIPHER_KEY_3DES);
            TestCase.fail();
        } catch (InvalidKeyException expected) {
        }
    }

    /**
     * javax.crypto.Cipher#init(int, java.security.Key,
     *        java.security.SecureRandom)
     */
    public void test_initWithSecureRandom() throws Exception {
        Cipher cipher = Cipher.getInstance(((CipherTest.ALGORITHM_3DES) + "/ECB/PKCS5Padding"));
        cipher.init(Cipher.ENCRYPT_MODE, CipherTest.CIPHER_KEY_3DES, new SecureRandom());
        cipher = Cipher.getInstance("DES/CBC/NoPadding");
        try {
            cipher.init(Cipher.ENCRYPT_MODE, CipherTest.CIPHER_KEY_3DES, new SecureRandom());
            TestCase.fail();
        } catch (InvalidKeyException expected) {
        }
    }

    /**
     * javax.crypto.Cipher#init(int, java.security.Key,
     *        java.security.spec.AlgorithmParameterSpec)
     */
    public void test_initWithAlgorithmParameterSpec() throws Exception {
        AlgorithmParameterSpec ap = new IvParameterSpec(CipherTest.IV);
        Cipher cipher = Cipher.getInstance(((CipherTest.ALGORITHM_3DES) + "/CBC/PKCS5Padding"));
        cipher.init(Cipher.ENCRYPT_MODE, CipherTest.CIPHER_KEY_3DES, ap);
        byte[] cipherIV = cipher.getIV();
        TestCase.assertTrue("IVs differ", Arrays.equals(cipherIV, CipherTest.IV));
        cipher = Cipher.getInstance("DES/CBC/NoPadding");
        try {
            cipher.init(Cipher.ENCRYPT_MODE, CipherTest.CIPHER_KEY_3DES, ap);
            TestCase.fail();
        } catch (InvalidKeyException expected) {
        }
        cipher = Cipher.getInstance("DES/CBC/NoPadding");
        ap = new RSAKeyGenParameterSpec(10, new BigInteger("10"));
        try {
            cipher.init(Cipher.ENCRYPT_MODE, CipherTest.CIPHER_KEY_DES, ap);
            TestCase.fail();
        } catch (InvalidAlgorithmParameterException expected) {
        }
    }

    /**
     * javax.crypto.Cipher#init(int, java.security.Key,
     *        java.security.spec.AlgorithmParameterSpec,
     *        java.security.SecureRandom)
     */
    public void test_initWithKeyAlgorithmParameterSpecSecureRandom() throws Exception {
        AlgorithmParameterSpec ap = new IvParameterSpec(CipherTest.IV);
        Cipher cipher = Cipher.getInstance(((CipherTest.ALGORITHM_3DES) + "/CBC/PKCS5Padding"));
        cipher.init(Cipher.ENCRYPT_MODE, CipherTest.CIPHER_KEY_3DES, ap, new SecureRandom());
        byte[] cipherIV = cipher.getIV();
        TestCase.assertTrue("IVs differ", Arrays.equals(cipherIV, CipherTest.IV));
        cipher = Cipher.getInstance("DES/CBC/NoPadding");
        try {
            cipher.init(Cipher.ENCRYPT_MODE, CipherTest.CIPHER_KEY_3DES, ap, new SecureRandom());
            TestCase.fail();
        } catch (InvalidKeyException expected) {
        }
        cipher = Cipher.getInstance("DES/CBC/NoPadding");
        ap = new RSAKeyGenParameterSpec(10, new BigInteger("10"));
        try {
            cipher.init(Cipher.ENCRYPT_MODE, CipherTest.CIPHER_KEY_DES, ap, new SecureRandom());
            TestCase.fail();
        } catch (InvalidAlgorithmParameterException expected) {
        }
    }

    /**
     * javax.crypto.Cipher#update(byte[], int, int)
     */
    public void test_update$BII() throws Exception {
        for (int index = 1; index < 4; index++) {
            Cipher c = Cipher.getInstance("DESEDE/CBC/PKCS5Padding");
            byte[] keyMaterial = loadBytes(((("hyts_" + "des-ede3-cbc.test") + index) + ".key"));
            DESedeKeySpec keySpec = new DESedeKeySpec(keyMaterial);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("DESEDE");
            Key k = skf.generateSecret(keySpec);
            byte[] ivMaterial = loadBytes(((("hyts_" + "des-ede3-cbc.test") + index) + ".iv"));
            IvParameterSpec iv = new IvParameterSpec(ivMaterial);
            c.init(Cipher.DECRYPT_MODE, k, iv);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] input = new byte[256];
            String resPath = (("hyts_" + "des-ede3-cbc.test") + index) + ".ciphertext";
            File resources = Support_Resources.createTempFolder();
            Support_Resources.copyFile(resources, null, resPath);
            InputStream is = Support_Resources.getStream(resPath);
            int bytesRead = is.read(input, 0, 256);
            while (bytesRead > 0) {
                byte[] output = c.update(input, 0, bytesRead);
                if (output != null) {
                    baos.write(output);
                }
                bytesRead = is.read(input, 0, 256);
            } 
            byte[] output = c.doFinal();
            if (output != null) {
                baos.write(output);
            }
            byte[] decipheredCipherText = baos.toByteArray();
            is.close();
            byte[] plaintextBytes = loadBytes(((("hyts_" + "des-ede3-cbc.test") + index) + ".plaintext"));
            TestCase.assertEquals(("Operation produced incorrect results for index " + index), Arrays.toString(plaintextBytes), Arrays.toString(decipheredCipherText));
        }
        Cipher cipher = Cipher.getInstance("DESEDE/CBC/PKCS5Padding");
        try {
            cipher.update(new byte[64], 0, 32);
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
    }

    /**
     * javax.crypto.Cipher#doFinal()
     */
    public void test_doFinal() throws Exception {
        for (int index = 1; index < 4; index++) {
            Cipher c = Cipher.getInstance("DESEDE/CBC/PKCS5Padding");
            byte[] keyMaterial = loadBytes(((("hyts_" + "des-ede3-cbc.test") + index) + ".key"));
            DESedeKeySpec keySpec = new DESedeKeySpec(keyMaterial);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("DESEDE");
            Key k = skf.generateSecret(keySpec);
            byte[] ivMaterial = loadBytes(((("hyts_" + "des-ede3-cbc.test") + index) + ".iv"));
            IvParameterSpec iv = new IvParameterSpec(ivMaterial);
            c.init(Cipher.ENCRYPT_MODE, k, iv);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] input = new byte[256];
            String resPath = (("hyts_" + "des-ede3-cbc.test") + index) + ".plaintext";
            File resources = Support_Resources.createTempFolder();
            Support_Resources.copyFile(resources, null, resPath);
            InputStream is = Support_Resources.getStream(resPath);
            int bytesRead = is.read(input, 0, 256);
            while (bytesRead > 0) {
                byte[] output = c.update(input, 0, bytesRead);
                if (output != null) {
                    baos.write(output);
                }
                bytesRead = is.read(input, 0, 256);
            } 
            byte[] output = c.doFinal();
            if (output != null) {
                baos.write(output);
            }
            byte[] encryptedPlaintext = baos.toByteArray();
            is.close();
            byte[] cipherText = loadBytes(((("hyts_" + "des-ede3-cbc.test") + index) + ".ciphertext"));
            TestCase.assertEquals(("Operation produced incorrect results for index " + index), Arrays.toString(cipherText), Arrays.toString(encryptedPlaintext));
        }
        byte[] b = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 };
        byte[] b1 = new byte[30];
        AlgorithmParameterSpec ap = new IvParameterSpec(CipherTest.IV);
        Cipher c = Cipher.getInstance("DES/CBC/NoPadding");
        c.init(Cipher.ENCRYPT_MODE, CipherTest.CIPHER_KEY_DES, ap);
        c.update(b, 0, 10, b1, 5);
        try {
            c.doFinal();
            TestCase.fail();
        } catch (IllegalBlockSizeException expected) {
        }
        c = Cipher.getInstance("DES/CBC/NoPadding");
        try {
            c.doFinal();
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        c = Cipher.getInstance("DES/CBC/NoPadding");
        c.init(Cipher.ENCRYPT_MODE, CipherTest.CIPHER_KEY_DES, ap);
        int len = c.doFinal(b, 0, 16, b1, 0);
        TestCase.assertEquals(16, len);
        c = Cipher.getInstance("DES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, CipherTest.CIPHER_KEY_DES, ap);
        TestCase.assertTrue(Arrays.equals(c.getIV(), CipherTest.IV));
        c.update(b1, 0, 24, b, 0);
        try {
            c.doFinal();
            TestCase.fail();
        } catch (BadPaddingException expected) {
        }
    }

    public void testGetParameters() throws Exception {
        Cipher c = Cipher.getInstance("DES");
        TestCase.assertNull(c.getParameters());
    }

    /* Class under test for int update(byte[], int, int, byte[], int) */
    public void testUpdatebyteArrayintintbyteArrayint() throws Exception {
        byte[] b = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        byte[] b1 = new byte[6];
        Cipher c = Cipher.getInstance("DESede");
        try {
            c.update(b, 0, 10, b1, 5);
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        c.init(Cipher.ENCRYPT_MODE, CipherTest.CIPHER_KEY_3DES);
        try {
            c.update(b, 0, 10, b1, 5);
            TestCase.fail();
        } catch (ShortBufferException expected) {
        }
        b1 = new byte[30];
        c.update(b, 0, 10, b1, 5);
    }

    /* Class under test for int doFinal(byte[], int, int, byte[], int) */
    public void testDoFinalbyteArrayintintbyteArrayint() throws Exception {
        byte[] b = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 };
        byte[] b1 = new byte[30];
        AlgorithmParameterSpec ap = new IvParameterSpec(CipherTest.IV);
        Cipher c = Cipher.getInstance("DES/CBC/NoPadding");
        c.init(Cipher.ENCRYPT_MODE, CipherTest.CIPHER_KEY_DES, ap);
        try {
            c.doFinal(b, 0, 10, b1, 5);
            TestCase.fail();
        } catch (IllegalBlockSizeException expected) {
        }
        c = Cipher.getInstance("DES/CBC/NoPadding");
        try {
            c.doFinal(b, 0, 10, b1, 5);
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        c = Cipher.getInstance("DES/CBC/NoPadding");
        c.init(Cipher.ENCRYPT_MODE, CipherTest.CIPHER_KEY_DES, ap);
        int len = c.doFinal(b, 0, 16, b1, 0);
        TestCase.assertEquals(16, len);
        c = Cipher.getInstance("DES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, CipherTest.CIPHER_KEY_DES, ap);
        try {
            c.doFinal(b1, 0, 24, new byte[42], 0);
            TestCase.fail();
        } catch (BadPaddingException expected) {
        }
        b1 = new byte[6];
        c = Cipher.getInstance("DESede");
        c.init(Cipher.ENCRYPT_MODE, CipherTest.CIPHER_KEY_3DES);
        try {
            c.doFinal(b, 3, 6, b1, 5);
            TestCase.fail();
        } catch (IllegalBlockSizeException maybeExpected) {
            TestCase.assertTrue(IS_RI);
        } catch (ShortBufferException maybeExpected) {
            TestCase.assertFalse(IS_RI);
        }
    }

    public void testGetMaxAllowedKeyLength() throws Exception {
        try {
            Cipher.getMaxAllowedKeyLength(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        try {
            Cipher.getMaxAllowedKeyLength("");
        } catch (NoSuchAlgorithmException expected) {
        }
        try {
            Cipher.getMaxAllowedKeyLength("//CBC/PKCS5Paddin");
            TestCase.fail();
        } catch (NoSuchAlgorithmException expected) {
        }
        try {
            Cipher.getMaxAllowedKeyLength("/DES/CBC/PKCS5Paddin/1");
            TestCase.fail();
        } catch (NoSuchAlgorithmException expected) {
        }
        TestCase.assertTrue(((Cipher.getMaxAllowedKeyLength("/DES/CBC/PKCS5Paddin")) > 0));
    }

    public void testGetMaxAllowedParameterSpec() throws Exception {
        try {
            Cipher.getMaxAllowedParameterSpec(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        try {
            Cipher.getMaxAllowedParameterSpec("/DES//PKCS5Paddin");
            TestCase.fail();
        } catch (NoSuchAlgorithmException expected) {
        }
        try {
            Cipher.getMaxAllowedParameterSpec("/DES/CBC/ /1");
            TestCase.fail();
        } catch (NoSuchAlgorithmException expected) {
        }
        Cipher.getMaxAllowedParameterSpec("DES/CBC/PKCS5Paddin");
        Cipher.getMaxAllowedParameterSpec("RSA");
    }

    /**
     * javax.crypto.Cipher#Cipher(CipherSpi cipherSpi, Provider provider,
     *        String transformation)
     */
    public void test_Ctor() throws Exception {
        // Regression for Harmony-1184
        try {
            new CipherTest.testCipher(null, null, "s");
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        try {
            new CipherTest.testCipher(new MyCipher(), null, "s");
            TestCase.fail("NullPointerException expected for 'null' provider");
        } catch (NullPointerException expected) {
        }
        try {
            new CipherTest.testCipher(null, new Provider("qwerty", 1.0, "qwerty") {}, "s");
            TestCase.fail("NullPointerException expected for 'null' cipherSpi");
        } catch (NullPointerException expected) {
        }
    }

    public void test_doFinalLjava_nio_ByteBufferLjava_nio_ByteBuffer() throws Exception {
        byte[] b = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 };
        ByteBuffer bInput = ByteBuffer.allocate(64);
        ByteBuffer bOutput = ByteBuffer.allocate(64);
        AlgorithmParameterSpec ap = new IvParameterSpec(CipherTest.IV);
        Cipher c = Cipher.getInstance("DES/CBC/NoPadding");
        c.init(Cipher.ENCRYPT_MODE, CipherTest.CIPHER_KEY_DES, ap);
        bInput.put(b, 0, 10);
        try {
            c.doFinal(bInput, bOutput);
            TestCase.fail();
        } catch (IllegalBlockSizeException expected) {
        }
        c = Cipher.getInstance("DES/CBC/NoPadding");
        try {
            c.doFinal(bInput, bOutput);
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        c = Cipher.getInstance("DES/CBC/NoPadding");
        c.init(Cipher.ENCRYPT_MODE, CipherTest.CIPHER_KEY_DES, ap);
        bInput = ByteBuffer.allocate(16);
        bInput.put(b, 0, 16);
        int len = c.doFinal(bInput, bOutput);
        TestCase.assertEquals(0, len);
        c = Cipher.getInstance("DES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, CipherTest.CIPHER_KEY_DES, ap);
        bInput = ByteBuffer.allocate(64);
        try {
            c.doFinal(bOutput, bInput);
            TestCase.fail();
        } catch (BadPaddingException expected) {
        }
        c = Cipher.getInstance("DES/CBC/NoPadding");
        c.init(Cipher.ENCRYPT_MODE, CipherTest.CIPHER_KEY_DES);
        bInput.put(b, 0, 16);
        try {
            c.doFinal(bInput, bInput);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        c = Cipher.getInstance("DES/CBC/NoPadding");
        c.init(Cipher.ENCRYPT_MODE, CipherTest.CIPHER_KEY_DES);
        bInput.put(b, 0, 16);
        try {
            c.doFinal(bInput, bOutput.asReadOnlyBuffer());
            TestCase.fail();
        } catch (ReadOnlyBufferException expected) {
        }
        bInput.rewind();
        bInput.put(b, 0, 16);
        bOutput = ByteBuffer.allocate(8);
        c = Cipher.getInstance("DESede");
        c.init(Cipher.ENCRYPT_MODE, CipherTest.CIPHER_KEY_3DES);
        try {
            c.doFinal(bInput, bOutput);
            TestCase.fail();
        } catch (ShortBufferException expected) {
        }
    }

    public void test_initWithKeyAlgorithmParameters() throws Exception {
        AlgorithmParameterSpec ap = new IvParameterSpec(CipherTest.IV);
        Cipher c = Cipher.getInstance("DES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, CipherTest.CIPHER_KEY_DES, ap);
        TestCase.assertNotNull(c.getParameters());
        try {
            c.init(Cipher.DECRYPT_MODE, CipherTest.CIPHER_KEY_3DES, ap);
            TestCase.fail();
        } catch (InvalidKeyException expected) {
        }
        try {
            c.init(Cipher.DECRYPT_MODE, CipherTest.CIPHER_KEY_DES, ((AlgorithmParameters) (null)));
            TestCase.fail();
        } catch (InvalidAlgorithmParameterException expected) {
        }
    }

    public void test_initWithKeyAlgorithmParametersSecureRandom() throws Exception {
        AlgorithmParameterSpec ap = new IvParameterSpec(CipherTest.IV);
        Cipher c = Cipher.getInstance("DES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, CipherTest.CIPHER_KEY_DES, ap, new SecureRandom());
        TestCase.assertNotNull(c.getParameters());
        try {
            c.init(Cipher.DECRYPT_MODE, CipherTest.CIPHER_KEY_3DES, ap, new SecureRandom());
            TestCase.fail();
        } catch (InvalidKeyException expected) {
        }
        try {
            c.init(Cipher.DECRYPT_MODE, CipherTest.CIPHER_KEY_DES, ((AlgorithmParameters) (null)), new SecureRandom());
            TestCase.fail();
        } catch (InvalidAlgorithmParameterException expected) {
        }
        c.init(Cipher.DECRYPT_MODE, CipherTest.CIPHER_KEY_DES, ap, ((SecureRandom) (null)));
        TestCase.assertNotNull(c.getParameters());
    }

    public void test_initWithCertificate() throws Exception {
        /* Certificate creation notes: certificate should be valid 37273 starting
        from 13 Nov 2008
        If it brcomes invalidated regenerate it using following commands:
        1. openssl genrsa -des3 -out test.key 1024
        2. openssl req -new -key test.key -out test.csr
        3. cp test.key test.key.org
        4. openssl rsa -in test.key.org -out test.key
        5. openssl x509 -req -days 37273 -in test.csr -signkey test.key -out test.cert
         */
        String certName = Support_Resources.getURL("test.cert");
        InputStream is = new URL(certName).openConnection().getInputStream();
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate cert = cf.generateCertificate(is);
        is.close();
        Cipher c = Cipher.getInstance("RSA");
        c.init(Cipher.ENCRYPT_MODE, cert);
        c = Cipher.getInstance("DES/CBC/PKCS5Padding");
        try {
            c.init(Cipher.ENCRYPT_MODE, cert);
            TestCase.fail();
        } catch (InvalidKeyException expected) {
        }
    }

    public void test_initWithCertificateSecureRandom() throws Exception {
        /* Certificate creation notes: certificate should be valid 37273 starting
        from 13 Nov 2008
        If it brcomes invalidated regenerate it using following commands:
        1. openssl genrsa -des3 -out test.key 1024
        2. openssl req -new -key test.key -out test.csr
        3. cp test.key test.key.org
        4. openssl rsa -in test.key.org -out test.key
        5. openssl x509 -req -days 37273 -in test.csr -signkey test.key -out test.cert
         */
        String certName = Support_Resources.getURL("test.cert");
        InputStream is = new URL(certName).openConnection().getInputStream();
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate cert = cf.generateCertificate(is);
        is.close();
        Cipher c = Cipher.getInstance("RSA");
        c.init(Cipher.ENCRYPT_MODE, cert, new SecureRandom());
        c = Cipher.getInstance("DES/CBC/PKCS5Padding");
        try {
            c.init(Cipher.ENCRYPT_MODE, cert, new SecureRandom());
            TestCase.fail();
        } catch (InvalidKeyException expected) {
        }
    }

    public void test_unwrap$BLjava_lang_StringI() throws Exception {
        AlgorithmParameterSpec ap = new IvParameterSpec(CipherTest.IV);
        Cipher c = Cipher.getInstance("DES/CBC/PKCS5Padding");
        c.init(Cipher.WRAP_MODE, CipherTest.CIPHER_KEY_DES, ap, new SecureRandom());
        byte[] arDES = c.wrap(CipherTest.CIPHER_KEY_DES);
        byte[] ar = c.wrap(CipherTest.CIPHER_KEY_3DES);
        try {
            c.unwrap(arDES, "DES", Cipher.SECRET_KEY);
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        c.init(Cipher.UNWRAP_MODE, CipherTest.CIPHER_KEY_DES, ap, new SecureRandom());
        TestCase.assertTrue(CipherTest.CIPHER_KEY_DES.equals(c.unwrap(arDES, "DES", Cipher.SECRET_KEY)));
        TestCase.assertFalse(CipherTest.CIPHER_KEY_DES.equals(c.unwrap(ar, "DES", Cipher.SECRET_KEY)));
        try {
            c.unwrap(arDES, "RSA38", Cipher.PUBLIC_KEY);
            TestCase.fail();
        } catch (NoSuchAlgorithmException expected) {
        }
        c = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        c.init(Cipher.UNWRAP_MODE, CipherTest.CIPHER_KEY_3DES, ap, new SecureRandom());
        try {
            c.unwrap(arDES, "DESede", Cipher.SECRET_KEY);
            TestCase.fail();
        } catch (InvalidKeyException expected) {
        }
    }

    public void test_updateLjava_nio_ByteBufferLjava_nio_ByteBuffer() throws Exception {
        byte[] b = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 };
        ByteBuffer bInput = ByteBuffer.allocate(256);
        ByteBuffer bOutput = ByteBuffer.allocate(256);
        Cipher c = Cipher.getInstance("DES/CBC/NoPadding");
        c.init(Cipher.ENCRYPT_MODE, CipherTest.CIPHER_KEY_DES);
        bInput.put(b, 0, 10);
        bInput.rewind();
        bOutput.rewind();
        c.update(bInput, bOutput);
        c = Cipher.getInstance("DES/CBC/NoPadding");
        try {
            c.update(bInput, bOutput);
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        c = Cipher.getInstance("DES/CBC/NoPadding");
        c.init(Cipher.ENCRYPT_MODE, CipherTest.CIPHER_KEY_DES);
        bInput = ByteBuffer.allocate(16);
        bInput.put(b, 0, 16);
        bInput.rewind();
        bOutput.rewind();
        c.update(bInput, bOutput);
        AlgorithmParameterSpec ap = new IvParameterSpec(CipherTest.IV);
        c = Cipher.getInstance("DES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, CipherTest.CIPHER_KEY_DES, ap);
        bInput = ByteBuffer.allocate(64);
        c = Cipher.getInstance("DES/CBC/NoPadding");
        c.init(Cipher.ENCRYPT_MODE, CipherTest.CIPHER_KEY_DES);
        bInput.put(b, 0, 16);
        bInput.rewind();
        try {
            c.update(bInput, bInput);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        c = Cipher.getInstance("DES/CBC/NoPadding");
        c.init(Cipher.ENCRYPT_MODE, CipherTest.CIPHER_KEY_DES);
        bInput.put(b, 0, 16);
        bInput.rewind();
        bOutput.rewind();
        try {
            c.update(bInput, bOutput.asReadOnlyBuffer());
            TestCase.fail();
        } catch (ReadOnlyBufferException expected) {
        }
        bInput.rewind();
        bInput.put(b, 0, 16);
        bInput.rewind();
        bOutput = ByteBuffer.allocate(8);
        c = Cipher.getInstance("DESede");
        c.init(Cipher.ENCRYPT_MODE, CipherTest.CIPHER_KEY_3DES);
        try {
            c.update(bInput, bOutput);
            TestCase.fail();
        } catch (ShortBufferException expected) {
        }
    }

    class Mock_Key implements Key {
        public String getAlgorithm() {
            return null;
        }

        public byte[] getEncoded() {
            return null;
        }

        public String getFormat() {
            return null;
        }
    }

    public void test_wrap_java_security_Key() throws Exception {
        AlgorithmParameterSpec ap = new IvParameterSpec(CipherTest.IV);
        Cipher c = Cipher.getInstance("DES/CBC/PKCS5Padding");
        c.init(Cipher.WRAP_MODE, CipherTest.CIPHER_KEY_DES, ap, new SecureRandom());
        TestCase.assertNotNull(c.wrap(CipherTest.CIPHER_KEY_DES));
        TestCase.assertNotNull(c.wrap(CipherTest.CIPHER_KEY_3DES));
        String certName = Support_Resources.getURL("test.cert");
        InputStream is = new URL(certName).openConnection().getInputStream();
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate cert = cf.generateCertificate(is);
        TestCase.assertNotNull(c.wrap(cert.getPublicKey()));
        c = Cipher.getInstance("DES/CBC/NoPadding");
        c.init(Cipher.WRAP_MODE, CipherTest.CIPHER_KEY_DES, ap, new SecureRandom());
        try {
            TestCase.assertNotNull(c.wrap(cert.getPublicKey()));
            TestCase.fail();
        } catch (IllegalBlockSizeException expected) {
        }
        c.init(Cipher.DECRYPT_MODE, CipherTest.CIPHER_KEY_DES, ap, new SecureRandom());
        try {
            c.wrap(CipherTest.CIPHER_KEY_DES);
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        c.init(Cipher.WRAP_MODE, CipherTest.CIPHER_KEY_DES, ap, new SecureRandom());
        try {
            c.wrap(new CipherTest.Mock_Key());
            TestCase.fail();
        } catch (InvalidKeyException expected) {
        }
    }

    public void test_doFinal$BI() throws Exception {
        byte[] b = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 };
        byte[] b1 = new byte[30];
        AlgorithmParameterSpec ap = new IvParameterSpec(CipherTest.IV);
        Cipher c = Cipher.getInstance("DES/CBC/NoPadding");
        c.init(Cipher.ENCRYPT_MODE, CipherTest.CIPHER_KEY_DES, ap);
        c.update(b, 0, 10);
        try {
            c.doFinal(b1, 5);
            TestCase.fail();
        } catch (IllegalBlockSizeException expected) {
        }
        c = Cipher.getInstance("DES/CBC/NoPadding");
        try {
            c.doFinal(b1, 5);
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        c = Cipher.getInstance("DES/CBC/NoPadding");
        c.init(Cipher.ENCRYPT_MODE, CipherTest.CIPHER_KEY_DES, ap);
        c.update(b, 3, 8);
        int len = c.doFinal(b1, 0);
        TestCase.assertEquals(0, len);
        c = Cipher.getInstance("DES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, CipherTest.CIPHER_KEY_DES, ap);
        c.update(b1, 0, 24);
        try {
            c.doFinal(b, 0);
            TestCase.fail();
        } catch (BadPaddingException expected) {
        }
        b1 = new byte[6];
        c = Cipher.getInstance("DESede");
        c.init(Cipher.ENCRYPT_MODE, CipherTest.CIPHER_KEY_3DES);
        c.update(b, 3, 6);
        try {
            c.doFinal(b1, 5);
            TestCase.fail();
        } catch (ShortBufferException expected) {
        }
    }

    public void test_doFinal$B() throws Exception {
        byte[] b1 = new byte[32];
        byte[] bI1 = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        byte[] bI2 = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
        byte[] bI3 = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 };
        byte[] bI4 = new byte[]{ 1, 2, 3 };
        AlgorithmParameterSpec ap = new IvParameterSpec(CipherTest.IV);
        Cipher c = Cipher.getInstance("DES/CBC/NoPadding");
        c.init(Cipher.ENCRYPT_MODE, CipherTest.CIPHER_KEY_DES, ap);
        try {
            c.doFinal(bI1);
            TestCase.fail();
        } catch (IllegalBlockSizeException expected) {
        }
        c = Cipher.getInstance("DES/CBC/NoPadding");
        try {
            c.doFinal(bI1);
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        c = Cipher.getInstance("DES/CBC/NoPadding");
        c.init(Cipher.ENCRYPT_MODE, CipherTest.CIPHER_KEY_DES, ap);
        int len1 = c.doFinal(bI2).length;
        TestCase.assertEquals(16, len1);
        c.init(Cipher.ENCRYPT_MODE, CipherTest.CIPHER_KEY_DES, ap);
        int len2 = c.doFinal(bI3, 0, 16, b1, 0);
        TestCase.assertEquals(16, len2);
        c = Cipher.getInstance("DES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, CipherTest.CIPHER_KEY_DES, ap);
        try {
            c.doFinal(b1);
            TestCase.fail();
        } catch (BadPaddingException expected) {
        }
    }

    public void test_doFinal$BII() throws Exception {
        byte[] b = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 };
        byte[] b1 = new byte[30];
        AlgorithmParameterSpec ap = new IvParameterSpec(CipherTest.IV);
        Cipher c = Cipher.getInstance("DES/CBC/NoPadding");
        c.init(Cipher.ENCRYPT_MODE, CipherTest.CIPHER_KEY_DES, ap);
        try {
            c.doFinal(b, 0, 10);
            TestCase.fail();
        } catch (IllegalBlockSizeException expected) {
        }
        c = Cipher.getInstance("DES/CBC/NoPadding");
        try {
            c.doFinal(b, 0, 10);
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        c = Cipher.getInstance("DES/CBC/NoPadding");
        c.init(Cipher.ENCRYPT_MODE, CipherTest.CIPHER_KEY_DES, ap);
        int len1 = c.doFinal(b, 0, 16).length;
        TestCase.assertEquals(16, len1);
        c.init(Cipher.ENCRYPT_MODE, CipherTest.CIPHER_KEY_DES, ap);
        int len2 = c.doFinal(b, 0, 16, b1, 0);
        TestCase.assertEquals(16, len2);
        c = Cipher.getInstance("DES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, CipherTest.CIPHER_KEY_DES, ap);
        try {
            c.doFinal(b1, 0, 24);
            TestCase.fail();
        } catch (BadPaddingException expected) {
        }
    }

    public void test_doFinal$BII$B() throws Exception {
        byte[] b = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 };
        byte[] b1 = new byte[30];
        AlgorithmParameterSpec ap = new IvParameterSpec(CipherTest.IV);
        Cipher c = Cipher.getInstance("DES/CBC/NoPadding");
        c.init(Cipher.ENCRYPT_MODE, CipherTest.CIPHER_KEY_DES, ap);
        try {
            c.doFinal(b, 0, 10, b1);
            TestCase.fail();
        } catch (IllegalBlockSizeException expected) {
        }
        c = Cipher.getInstance("DES/CBC/NoPadding");
        try {
            c.doFinal(b, 0, 10, b1);
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        c = Cipher.getInstance("DES/CBC/NoPadding");
        c.init(Cipher.ENCRYPT_MODE, CipherTest.CIPHER_KEY_DES, ap);
        int len = c.doFinal(b, 0, 16, b1);
        TestCase.assertEquals(16, len);
        c = Cipher.getInstance("DES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, CipherTest.CIPHER_KEY_DES, ap);
        try {
            c.doFinal(b1, 0, 24, new byte[42]);
            TestCase.fail();
        } catch (BadPaddingException expected) {
        }
        b1 = new byte[6];
        c = Cipher.getInstance("DESede");
        c.init(Cipher.ENCRYPT_MODE, CipherTest.CIPHER_KEY_3DES);
        try {
            c.doFinal(b, 3, 6, b1);
            TestCase.fail();
        } catch (ShortBufferException expected) {
        }
    }

    public void test_update$B() throws Exception {
        Cipher cipher = Cipher.getInstance("DESEDE/CBC/PKCS5Padding");
        try {
            cipher.update(new byte[64]);
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
    }

    public void test_() throws Exception {
        byte[] b = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 };
        byte[] b1 = new byte[30];
        Cipher c = Cipher.getInstance("DES/CBC/NoPadding");
        try {
            c.update(b, 0, 10, b1);
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        c = Cipher.getInstance("DES/CBC/NoPadding");
        c.init(Cipher.ENCRYPT_MODE, CipherTest.CIPHER_KEY_DES);
        c.update(b, 0, 16, b1);
        b1 = new byte[3];
        try {
            c.update(b, 3, 15, b1);
            TestCase.fail();
        } catch (ShortBufferException expected) {
        }
    }

    class testCipher extends Cipher {
        testCipher(CipherSpi c, Provider p, String s) {
            super(c, p, s);
        }
    }
}

