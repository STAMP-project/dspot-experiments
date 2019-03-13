/**
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package libcore.javax.crypto;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import junit.framework.TestCase;


public final class CipherInputStreamTest extends TestCase {
    private final byte[] keyBytes = new byte[]{ 127, -2, -95, -39, 35, 118, 121, -92 };

    private final String plainText = "abcde";

    private final byte[] cipherText = new byte[]{ 121, -124, -106, 43, -55, -67, -105, -75 };

    private SecretKey key;

    public void testEncrypt() throws Exception {
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        InputStream in = new CipherInputStream(new ByteArrayInputStream(plainText.getBytes("UTF-8")), cipher);
        byte[] bytes = readAll(in);
        TestCase.assertEquals(Arrays.toString(cipherText), Arrays.toString(bytes));
    }

    public void testDecrypt() throws Exception {
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        InputStream in = new CipherInputStream(new ByteArrayInputStream(cipherText), cipher);
        byte[] bytes = readAll(in);
        TestCase.assertEquals(plainText, new String(bytes, "UTF-8"));
    }

    public void testSkip() throws Exception {
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        InputStream in = new CipherInputStream(new ByteArrayInputStream(cipherText), cipher);
        TestCase.assertTrue(((in.skip(5)) > 0));
    }

    public void testCipherInputStream_TruncatedInput_Failure() throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(new byte[16], "AES"), new IvParameterSpec(new byte[16]));
        InputStream is = new CipherInputStream(new ByteArrayInputStream(new byte[31]), cipher);
        is.read(new byte[4]);
        is.close();
    }
}

