/**
 * Syncany, www.syncany.org
 * Copyright (C) 2011-2016 Philipp C. Heckel <philipp.heckel@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.syncany.crypto;


import CipherSpecs.AES_128_GCM;
import CipherSpecs.AES_256_GCM;
import CipherSpecs.TWOFISH_128_GCM;
import CipherSpecs.TWOFISH_256_GCM;
import MultiCipherOutputStream.STREAM_MAGIC;
import MultiCipherOutputStream.STREAM_VERSION;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.syncany.config.Logging;
import org.syncany.tests.util.TestFileUtil;
import org.syncany.util.StringUtil;


public class CipherUtilTest {
    private static final Logger logger = Logger.getLogger(CipherUtilTest.class.getSimpleName());

    static {
        Logging.init();
    }

    @Test
    public void testCreateDerivedKeys() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        SaltedSecretKey masterKey = createDummyMasterKey();
        CipherSpec cipherSpec = CipherSpecs.getCipherSpec(AES_128_GCM);
        byte[] derivedKeySalt1 = new byte[]{ 1, 2, 3 };
        byte[] derivedKeySalt2 = new byte[]{ 1, 2, 3, 4 };
        SaltedSecretKey derivedKey1 = CipherUtil.createDerivedKey(masterKey, derivedKeySalt1, cipherSpec);
        SaltedSecretKey derivedKey2 = CipherUtil.createDerivedKey(masterKey, derivedKeySalt2, cipherSpec);
        CipherUtilTest.logger.log(Level.INFO, ("- Derived key 1: " + (StringUtil.toHex(derivedKey1.getEncoded()))));
        CipherUtilTest.logger.log(Level.INFO, ("      with salt: " + (StringUtil.toHex(derivedKey1.getSalt()))));
        CipherUtilTest.logger.log(Level.INFO, ("- Derived key 2: " + (StringUtil.toHex(derivedKey2.getEncoded()))));
        CipherUtilTest.logger.log(Level.INFO, ("      with salt: " + (StringUtil.toHex(derivedKey2.getSalt()))));
        Assert.assertEquals((128 / 8), derivedKey1.getEncoded().length);
        Assert.assertEquals((128 / 8), derivedKey2.getEncoded().length);
        Assert.assertFalse(Arrays.equals(derivedKey1.getSalt(), derivedKey2.getSalt()));
        Assert.assertFalse(Arrays.equals(derivedKey1.getEncoded(), derivedKey2.getEncoded()));
    }

    @Test
    public void testCreateRandomArray() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        byte[] randomArray1 = CipherUtil.createRandomArray(10);
        byte[] randomArray2 = CipherUtil.createRandomArray(10);
        Assert.assertEquals(10, randomArray1.length);
        Assert.assertEquals(10, randomArray2.length);
        Assert.assertFalse(Arrays.equals(randomArray1, randomArray2));
    }

    @Test
    public void testIsEncryptedFileFalse() throws Exception {
        File tempDirectory = TestFileUtil.createTempDirectoryInSystemTemp();
        File testFile = new File((tempDirectory + "/somefile"));
        TestFileUtil.writeToFile(new byte[]{ 1, 2, 3 }, testFile);
        Assert.assertFalse(CipherUtil.isEncrypted(testFile));
        TestFileUtil.deleteDirectory(tempDirectory);
    }

    @Test
    public void testIsEncryptedFileTrue() throws Exception {
        File tempDirectory = TestFileUtil.createTempDirectoryInSystemTemp();
        File testFile = new File((tempDirectory + "/somefile"));
        RandomAccessFile testFileRaf = new RandomAccessFile(testFile, "rw");
        testFileRaf.write(STREAM_MAGIC);
        testFileRaf.write(STREAM_VERSION);
        testFileRaf.close();
        Assert.assertTrue(CipherUtil.isEncrypted(testFile));
        TestFileUtil.deleteDirectory(tempDirectory);
    }

    @Test
    public void testEncryptShortArrayAes128Gcm() throws Exception {
        testEncrypt(new byte[]{ 1, 2, 3, 4 }, Arrays.asList(new CipherSpec[]{ CipherSpecs.getCipherSpec(AES_128_GCM) }));
    }

    @Test
    public void testEncryptLongArrayAes128Gcm() throws Exception {
        testEncrypt(TestFileUtil.createRandomArray((1024 * 1024)), Arrays.asList(new CipherSpec[]{ CipherSpecs.getCipherSpec(AES_128_GCM) }));
    }

    @Test
    public void testEncryptShortArrayAes128Twofish128() throws Exception {
        testEncrypt(new byte[]{ 1, 2, 3, 4 }, Arrays.asList(new CipherSpec[]{ CipherSpecs.getCipherSpec(AES_128_GCM), CipherSpecs.getCipherSpec(TWOFISH_128_GCM) }));
    }

    @Test
    public void testEncryptLongArrayAes128Twofish128() throws Exception {
        testEncrypt(TestFileUtil.createRandomArray((1024 * 1024)), Arrays.asList(new CipherSpec[]{ CipherSpecs.getCipherSpec(AES_128_GCM), CipherSpecs.getCipherSpec(TWOFISH_128_GCM) }));
    }

    @Test
    public void testEncryptLongArrayAes258Twofish256UnlimitedStrength() throws Exception {
        testEncrypt(TestFileUtil.createRandomArray((1024 * 1024)), Arrays.asList(new CipherSpec[]{ CipherSpecs.getCipherSpec(AES_256_GCM), CipherSpecs.getCipherSpec(TWOFISH_256_GCM) }));
    }

    @Test(expected = Exception.class)
    public void testIntegrityHeaderMagic() throws Exception {
        SaltedSecretKey masterKey = createDummyMasterKey();
        byte[] originalPlaintext = TestFileUtil.createRandomArray(50);
        byte[] ciphertext = CipherUtil.encrypt(new ByteArrayInputStream(originalPlaintext), Arrays.asList(CipherSpecs.getCipherSpec(AES_128_GCM)), masterKey);
        // Alter header MAGIC BYTES
        ciphertext[0] = 18;
        ciphertext[1] = 52;
        byte[] plaintext = CipherUtil.decrypt(new ByteArrayInputStream(ciphertext), masterKey);
        System.out.println(StringUtil.toHex(originalPlaintext));
        System.out.println(StringUtil.toHex(plaintext));
        Assert.fail("TEST FAILED: Ciphertext was altered without exception.");
    }

    @Test(expected = Exception.class)
    public void testIntegrityHeaderVersion() throws Exception {
        SaltedSecretKey masterKey = createDummyMasterKey();
        byte[] originalPlaintext = TestFileUtil.createRandomArray(50);
        byte[] ciphertext = CipherUtil.encrypt(new ByteArrayInputStream(originalPlaintext), Arrays.asList(CipherSpecs.getCipherSpec(AES_128_GCM)), masterKey);
        // Alter header VERSION
        ciphertext[4] = ((byte) (255));
        byte[] plaintext = CipherUtil.decrypt(new ByteArrayInputStream(ciphertext), masterKey);
        System.out.println(StringUtil.toHex(originalPlaintext));
        System.out.println(StringUtil.toHex(plaintext));
        Assert.fail("TEST FAILED: Ciphertext was altered without exception.");
    }

    @Test(expected = Exception.class)
    public void testIntegrityHeaderCipherSpecId() throws Exception {
        SaltedSecretKey masterKey = createDummyMasterKey();
        byte[] originalPlaintext = TestFileUtil.createRandomArray(50);
        byte[] ciphertext = CipherUtil.encrypt(new ByteArrayInputStream(originalPlaintext), Arrays.asList(CipherSpecs.getCipherSpec(AES_128_GCM)), masterKey);
        Assert.assertEquals(AES_128_GCM, ciphertext[18]);// If this fails, fix test!

        // Alter header CIPHER SPEC ID
        ciphertext[18] = ((byte) (255));
        byte[] plaintext = CipherUtil.decrypt(new ByteArrayInputStream(ciphertext), masterKey);
        System.out.println(StringUtil.toHex(originalPlaintext));
        System.out.println(StringUtil.toHex(plaintext));
        Assert.fail("TEST FAILED: Ciphertext was altered without exception.");
    }

    @Test(expected = Exception.class)
    public void testIntegrityHeaderCipherSalt() throws Exception {
        SaltedSecretKey masterKey = createDummyMasterKey();
        byte[] originalPlaintext = TestFileUtil.createRandomArray(50);
        byte[] ciphertext = CipherUtil.encrypt(new ByteArrayInputStream(originalPlaintext), Arrays.asList(CipherSpecs.getCipherSpec(AES_128_GCM)), masterKey);
        // Alter header CIPHER SALT
        ciphertext[19] = ((byte) (255));
        ciphertext[20] = ((byte) (255));
        ciphertext[21] = ((byte) (255));
        byte[] plaintext = CipherUtil.decrypt(new ByteArrayInputStream(ciphertext), masterKey);
        System.out.println(StringUtil.toHex(originalPlaintext));
        System.out.println(StringUtil.toHex(plaintext));
        Assert.fail("TEST FAILED: Ciphertext was altered without exception.");
    }

    @Test(expected = Exception.class)
    public void testIntegrityHeaderCipherIV() throws Exception {
        SaltedSecretKey masterKey = createDummyMasterKey();
        byte[] originalPlaintext = TestFileUtil.createRandomArray(50);
        byte[] ciphertext = CipherUtil.encrypt(new ByteArrayInputStream(originalPlaintext), Arrays.asList(CipherSpecs.getCipherSpec(AES_128_GCM)), masterKey);
        // Alter header CIPHER SALT
        ciphertext[32] = ((byte) (255));
        ciphertext[33] = ((byte) (255));
        ciphertext[34] = ((byte) (255));
        byte[] plaintext = CipherUtil.decrypt(new ByteArrayInputStream(ciphertext), masterKey);
        System.out.println(StringUtil.toHex(originalPlaintext));
        System.out.println(StringUtil.toHex(plaintext));
        Assert.fail("TEST FAILED: Ciphertext was altered without exception.");
    }

    @Test(expected = CipherException.class)
    public void testIntegrityAesGcmCiphertext() throws Exception {
        SaltedSecretKey masterKey = createDummyMasterKey();
        byte[] originalPlaintext = TestFileUtil.createRandomArray(50);
        byte[] ciphertext = CipherUtil.encrypt(new ByteArrayInputStream(originalPlaintext), Arrays.asList(CipherSpecs.getCipherSpec(AES_128_GCM)), masterKey);
        // Alter ciphertext (after header!); ciphertext starts after 75 bytes
        ciphertext[80] = ((byte) ((ciphertext[80]) ^ 1));
        ciphertext[81] = ((byte) ((ciphertext[81]) ^ 2));
        ciphertext[82] = ((byte) ((ciphertext[82]) ^ 3));
        CipherUtil.decrypt(new ByteArrayInputStream(ciphertext), masterKey);
        Assert.fail("TEST FAILED: Ciphertext was altered without exception.");
    }

    @Test(expected = Exception.class)
    public void testIntegrityTwofishGcmCiphertext() throws Exception {
        SaltedSecretKey masterKey = createDummyMasterKey();
        byte[] originalPlaintext = TestFileUtil.createRandomArray(50);
        byte[] ciphertext = CipherUtil.encrypt(new ByteArrayInputStream(originalPlaintext), Arrays.asList(CipherSpecs.getCipherSpec(TWOFISH_128_GCM)), masterKey);
        // Alter ciphertext (after header!); ciphertext starts after 75 bytes
        ciphertext[80] = ((byte) ((ciphertext[80]) ^ 1));
        byte[] plaintext = CipherUtil.decrypt(new ByteArrayInputStream(ciphertext), masterKey);
        System.out.println(StringUtil.toHex(originalPlaintext));
        System.out.println(StringUtil.toHex(plaintext));
        Assert.fail("TEST FAILED: Ciphertext was altered without exception.");
    }
}

