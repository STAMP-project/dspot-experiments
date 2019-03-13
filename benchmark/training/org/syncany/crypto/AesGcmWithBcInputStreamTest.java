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


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import javax.crypto.NoSuchPaddingException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.io.InvalidCipherTextIOException;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.syncany.config.Logging;


/**
 * Tests to test the Bouncy Castle implementation of the CipherInputStream
 *
 * @see https://github.com/binwiederhier/cipherinputstream-aes-gcm/blob/e9759ca71557e5d1da26ae72f6ce5aac918e34b0/src/CipherInputStreamIssuesTests.java
 * @see http://blog.philippheckel.com/2014/03/01/cipherinputstream-for-aead-modes-is-broken-in-jdk7-gcm/
 */
public class AesGcmWithBcInputStreamTest {
    private static final SecureRandom secureRandom = new SecureRandom();

    static {
        Logging.init();
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    public void testD_BouncyCastleCipherInputStreamWithAesGcm() throws IOException, InvalidAlgorithmParameterException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException {
        // Encrypt (not interesting in this example)
        byte[] randomKey = AesGcmWithBcInputStreamTest.createRandomArray(16);
        byte[] randomIv = AesGcmWithBcInputStreamTest.createRandomArray(16);
        byte[] originalPlaintext = "Confirm 100$ pay".getBytes("ASCII");
        byte[] originalCiphertext = AesGcmWithBcInputStreamTest.encryptWithAesGcm(originalPlaintext, randomKey, randomIv);
        // Attack / alter ciphertext (an attacker would do this!)
        byte[] alteredCiphertext = Arrays.clone(originalCiphertext);
        alteredCiphertext[8] = ((byte) ((alteredCiphertext[8]) ^ 8));// <<< Change 100$ to 900$

        // Decrypt with BouncyCastle implementation of CipherInputStream
        AEADBlockCipher cipher = new org.bouncycastle.crypto.modes.GCMBlockCipher(new AESEngine());
        cipher.init(false, new org.bouncycastle.crypto.params.AEADParameters(new KeyParameter(randomKey), 128, randomIv));
        try {
            AesGcmWithBcInputStreamTest.readFromStream(new org.bouncycastle.crypto.io.CipherInputStream(new ByteArrayInputStream(alteredCiphertext), cipher));
            // ^^^^^^^^^^^^^^^ INTERESTING PART ^^^^^^^^^^^^^^^^
            // 
            // The BouncyCastle implementation of the CipherInputStream detects MAC verification errors and
            // throws a InvalidCipherTextIOException if an error occurs. Nice! A more or less minor issue
            // however is that it is incompatible with the standard JCE Cipher class from the javax.crypto
            // package. The new interface AEADBlockCipher must be used. The code below is not executed.
            Assert.fail("Test D: org.bouncycastle.crypto.io.CipherInputStream:        NOT OK, tampering not detected");
        } catch (InvalidCipherTextIOException e) {
            System.out.println("Test D: org.bouncycastle.crypto.io.CipherInputStream:        OK, tampering detected");
        }
    }

    @Test
    public void testE_BouncyCastleCipherInputStreamWithAesGcmLongPlaintext() throws IOException, InvalidAlgorithmParameterException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException {
        // Encrypt (not interesting in this example)
        byte[] randomKey = AesGcmWithBcInputStreamTest.createRandomArray(16);
        byte[] randomIv = AesGcmWithBcInputStreamTest.createRandomArray(16);
        byte[] originalPlaintext = AesGcmWithBcInputStreamTest.createRandomArray(4080);// <<<< 4080 bytes fails, 4079 bytes works!

        byte[] originalCiphertext = AesGcmWithBcInputStreamTest.encryptWithAesGcm(originalPlaintext, randomKey, randomIv);
        // Decrypt with BouncyCastle implementation of CipherInputStream
        AEADBlockCipher cipher = new org.bouncycastle.crypto.modes.GCMBlockCipher(new AESEngine());
        cipher.init(false, new org.bouncycastle.crypto.params.AEADParameters(new KeyParameter(randomKey), 128, randomIv));
        try {
            AesGcmWithBcInputStreamTest.readFromStream(new org.bouncycastle.crypto.io.CipherInputStream(new ByteArrayInputStream(originalCiphertext), cipher));
            // ^^^^^^^^^^^^^^^ INTERESTING PART ^^^^^^^^^^^^^^^^
            // 
            // In this example, the BouncyCastle implementation of the CipherInputStream throws an ArrayIndexOutOfBoundsException.
            // The only difference to the example above is that the plaintext is now 4080 bytes long! For 4079 bytes plaintexts,
            // everything works just fine.
            System.out.println("Test E: org.bouncycastle.crypto.io.CipherInputStream:        OK, throws no exception");
        } catch (IOException e) {
            Assert.fail(("Test E: org.bouncycastle.crypto.io.CipherInputStream:        NOT OK throws: " + (e.getMessage())));
        }
    }
}

