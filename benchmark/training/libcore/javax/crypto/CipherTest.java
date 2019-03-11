/**
 * Copyright (C) 2011 The Android Open Source Project
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


import KeyUsage.cRLSign;
import KeyUsage.dataEncipherment;
import KeyUsage.decipherOnly;
import KeyUsage.digitalSignature;
import KeyUsage.encipherOnly;
import KeyUsage.keyAgreement;
import KeyUsage.keyCertSign;
import KeyUsage.keyEncipherment;
import KeyUsage.nonRepudiation;
import com.android.org.bouncycastle.asn1.x509.KeyUsage;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import junit.framework.TestCase;
import libcore.java.security.StandardNames;


public final class CipherTest extends TestCase {
    private static final String[] RSA_PROVIDERS = (StandardNames.IS_RI) ? new String[]{ "SunJCE" } : new String[]{ "BC", "AndroidOpenSSL" };

    private static final String[] AES_PROVIDERS = (StandardNames.IS_RI) ? new String[]{ "SunJCE" } : new String[]{ "BC", "AndroidOpenSSL" };

    private static final boolean IS_UNLIMITED;

    static {
        boolean is_unlimited;
        if (StandardNames.IS_RI) {
            try {
                String algorithm = "PBEWITHMD5ANDTRIPLEDES";
                Cipher.getInstance(algorithm).init(CipherTest.getEncryptMode(algorithm), CipherTest.getEncryptKey(algorithm), CipherTest.getEncryptAlgorithmParameterSpec(algorithm));
                is_unlimited = true;
            } catch (Exception e) {
                is_unlimited = false;
                System.out.println(("WARNING: Some tests disabled due to lack of " + "'Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files'"));
            }
        } else {
            is_unlimited = true;
        }
        IS_UNLIMITED = is_unlimited;
    }

    private static Map<String, Key> ENCRYPT_KEYS = new HashMap<String, Key>();

    private static Map<String, Key> DECRYPT_KEYS = new HashMap<String, Key>();

    private static Map<String, Integer> EXPECTED_BLOCK_SIZE = new HashMap<String, Integer>();

    static {
        CipherTest.setExpectedBlockSize("AES", 16);
        CipherTest.setExpectedBlockSize("AES/CBC/PKCS5PADDING", 16);
        CipherTest.setExpectedBlockSize("AES/CBC/NOPADDING", 16);
        CipherTest.setExpectedBlockSize("AES/CFB/PKCS5PADDING", 16);
        CipherTest.setExpectedBlockSize("AES/CFB/NOPADDING", 16);
        CipherTest.setExpectedBlockSize("AES/CTR/PKCS5PADDING", 16);
        CipherTest.setExpectedBlockSize("AES/CTR/NOPADDING", 16);
        CipherTest.setExpectedBlockSize("AES/CTS/PKCS5PADDING", 16);
        CipherTest.setExpectedBlockSize("AES/CTS/NOPADDING", 16);
        CipherTest.setExpectedBlockSize("AES/ECB/PKCS5PADDING", 16);
        CipherTest.setExpectedBlockSize("AES/ECB/NOPADDING", 16);
        CipherTest.setExpectedBlockSize("AES/OFB/PKCS5PADDING", 16);
        CipherTest.setExpectedBlockSize("AES/OFB/NOPADDING", 16);
        CipherTest.setExpectedBlockSize("PBEWITHMD5AND128BITAES-CBC-OPENSSL", 16);
        CipherTest.setExpectedBlockSize("PBEWITHMD5AND192BITAES-CBC-OPENSSL", 16);
        CipherTest.setExpectedBlockSize("PBEWITHMD5AND256BITAES-CBC-OPENSSL", 16);
        CipherTest.setExpectedBlockSize("PBEWITHSHA256AND128BITAES-CBC-BC", 16);
        CipherTest.setExpectedBlockSize("PBEWITHSHA256AND192BITAES-CBC-BC", 16);
        CipherTest.setExpectedBlockSize("PBEWITHSHA256AND256BITAES-CBC-BC", 16);
        CipherTest.setExpectedBlockSize("PBEWITHSHAAND128BITAES-CBC-BC", 16);
        CipherTest.setExpectedBlockSize("PBEWITHSHAAND192BITAES-CBC-BC", 16);
        CipherTest.setExpectedBlockSize("PBEWITHSHAAND256BITAES-CBC-BC", 16);
        if (StandardNames.IS_RI) {
            CipherTest.setExpectedBlockSize("AESWRAP", 16);
        } else {
            CipherTest.setExpectedBlockSize("AESWRAP", 0);
        }
        CipherTest.setExpectedBlockSize("ARC4", 0);
        CipherTest.setExpectedBlockSize("ARCFOUR", 0);
        CipherTest.setExpectedBlockSize("PBEWITHSHAAND40BITRC4", 0);
        CipherTest.setExpectedBlockSize("PBEWITHSHAAND128BITRC4", 0);
        CipherTest.setExpectedBlockSize("BLOWFISH", 8);
        CipherTest.setExpectedBlockSize("DES", 8);
        CipherTest.setExpectedBlockSize("PBEWITHMD5ANDDES", 8);
        CipherTest.setExpectedBlockSize("PBEWITHSHA1ANDDES", 8);
        CipherTest.setExpectedBlockSize("DESEDE", 8);
        CipherTest.setExpectedBlockSize("PBEWITHSHAAND2-KEYTRIPLEDES-CBC", 8);
        CipherTest.setExpectedBlockSize("PBEWITHSHAAND3-KEYTRIPLEDES-CBC", 8);
        CipherTest.setExpectedBlockSize("PBEWITHMD5ANDTRIPLEDES", 8);
        CipherTest.setExpectedBlockSize("PBEWITHSHA1ANDDESEDE", 8);
        if (StandardNames.IS_RI) {
            CipherTest.setExpectedBlockSize("DESEDEWRAP", 8);
        } else {
            CipherTest.setExpectedBlockSize("DESEDEWRAP", 0);
        }
        if (StandardNames.IS_RI) {
            CipherTest.setExpectedBlockSize("RSA", 0);
            CipherTest.setExpectedBlockSize("RSA/ECB/NoPadding", 0);
            CipherTest.setExpectedBlockSize("RSA/ECB/PKCS1Padding", 0);
        } else {
            CipherTest.setExpectedBlockSize("RSA", Cipher.ENCRYPT_MODE, 256);
            CipherTest.setExpectedBlockSize("RSA/ECB/NoPadding", Cipher.ENCRYPT_MODE, 256);
            CipherTest.setExpectedBlockSize("RSA/ECB/PKCS1Padding", Cipher.ENCRYPT_MODE, 245);
            // BC strips the leading 0 for us even when NoPadding is specified
            CipherTest.setExpectedBlockSize("RSA", Cipher.ENCRYPT_MODE, "BC", 255);
            CipherTest.setExpectedBlockSize("RSA/ECB/NoPadding", Cipher.ENCRYPT_MODE, "BC", 255);
            CipherTest.setExpectedBlockSize("RSA", Cipher.DECRYPT_MODE, 256);
            CipherTest.setExpectedBlockSize("RSA/ECB/NoPadding", Cipher.DECRYPT_MODE, 256);
            CipherTest.setExpectedBlockSize("RSA/ECB/PKCS1Padding", Cipher.DECRYPT_MODE, 256);
        }
    }

    private static Map<String, Integer> EXPECTED_OUTPUT_SIZE = new HashMap<String, Integer>();

    static {
        CipherTest.setExpectedOutputSize("AES/CBC/NOPADDING", 0);
        CipherTest.setExpectedOutputSize("AES/CFB/NOPADDING", 0);
        CipherTest.setExpectedOutputSize("AES/CTR/NOPADDING", 0);
        CipherTest.setExpectedOutputSize("AES/CTS/NOPADDING", 0);
        CipherTest.setExpectedOutputSize("AES/ECB/NOPADDING", 0);
        CipherTest.setExpectedOutputSize("AES/OFB/NOPADDING", 0);
        CipherTest.setExpectedOutputSize("AES", Cipher.ENCRYPT_MODE, 16);
        CipherTest.setExpectedOutputSize("AES/CBC/PKCS5PADDING", Cipher.ENCRYPT_MODE, 16);
        CipherTest.setExpectedOutputSize("AES/CFB/PKCS5PADDING", Cipher.ENCRYPT_MODE, 16);
        CipherTest.setExpectedOutputSize("AES/CTR/PKCS5PADDING", Cipher.ENCRYPT_MODE, 16);
        CipherTest.setExpectedOutputSize("AES/CTS/PKCS5PADDING", Cipher.ENCRYPT_MODE, 16);
        CipherTest.setExpectedOutputSize("AES/ECB/PKCS5PADDING", Cipher.ENCRYPT_MODE, 16);
        CipherTest.setExpectedOutputSize("AES/OFB/PKCS5PADDING", Cipher.ENCRYPT_MODE, 16);
        CipherTest.setExpectedOutputSize("PBEWITHMD5AND128BITAES-CBC-OPENSSL", 16);
        CipherTest.setExpectedOutputSize("PBEWITHMD5AND192BITAES-CBC-OPENSSL", 16);
        CipherTest.setExpectedOutputSize("PBEWITHMD5AND256BITAES-CBC-OPENSSL", 16);
        CipherTest.setExpectedOutputSize("PBEWITHSHA256AND128BITAES-CBC-BC", 16);
        CipherTest.setExpectedOutputSize("PBEWITHSHA256AND192BITAES-CBC-BC", 16);
        CipherTest.setExpectedOutputSize("PBEWITHSHA256AND256BITAES-CBC-BC", 16);
        CipherTest.setExpectedOutputSize("PBEWITHSHAAND128BITAES-CBC-BC", 16);
        CipherTest.setExpectedOutputSize("PBEWITHSHAAND192BITAES-CBC-BC", 16);
        CipherTest.setExpectedOutputSize("PBEWITHSHAAND256BITAES-CBC-BC", 16);
        // AndroidOpenSSL returns zero for the non-block ciphers
        CipherTest.setExpectedOutputSize("AES/CFB/PKCS5PADDING", Cipher.ENCRYPT_MODE, "AndroidOpenSSL", 0);
        CipherTest.setExpectedOutputSize("AES/CTR/PKCS5PADDING", Cipher.ENCRYPT_MODE, "AndroidOpenSSL", 0);
        CipherTest.setExpectedOutputSize("AES/CTS/PKCS5PADDING", Cipher.ENCRYPT_MODE, "AndroidOpenSSL", 0);
        CipherTest.setExpectedOutputSize("AES/OFB/PKCS5PADDING", Cipher.ENCRYPT_MODE, "AndroidOpenSSL", 0);
        CipherTest.setExpectedOutputSize("AES", Cipher.DECRYPT_MODE, 0);
        CipherTest.setExpectedOutputSize("AES/CBC/PKCS5PADDING", Cipher.DECRYPT_MODE, 0);
        CipherTest.setExpectedOutputSize("AES/CFB/PKCS5PADDING", Cipher.DECRYPT_MODE, 0);
        CipherTest.setExpectedOutputSize("AES/CTR/PKCS5PADDING", Cipher.DECRYPT_MODE, 0);
        CipherTest.setExpectedOutputSize("AES/CTS/PKCS5PADDING", Cipher.DECRYPT_MODE, 0);
        CipherTest.setExpectedOutputSize("AES/ECB/PKCS5PADDING", Cipher.DECRYPT_MODE, 0);
        CipherTest.setExpectedOutputSize("AES/OFB/PKCS5PADDING", Cipher.DECRYPT_MODE, 0);
        CipherTest.setExpectedOutputSize("PBEWITHMD5AND128BITAES-CBC-OPENSSL", Cipher.DECRYPT_MODE, 0);
        CipherTest.setExpectedOutputSize("PBEWITHMD5AND192BITAES-CBC-OPENSSL", Cipher.DECRYPT_MODE, 0);
        CipherTest.setExpectedOutputSize("PBEWITHMD5AND256BITAES-CBC-OPENSSL", Cipher.DECRYPT_MODE, 0);
        CipherTest.setExpectedOutputSize("PBEWITHSHA256AND128BITAES-CBC-BC", Cipher.DECRYPT_MODE, 0);
        CipherTest.setExpectedOutputSize("PBEWITHSHA256AND192BITAES-CBC-BC", Cipher.DECRYPT_MODE, 0);
        CipherTest.setExpectedOutputSize("PBEWITHSHA256AND256BITAES-CBC-BC", Cipher.DECRYPT_MODE, 0);
        CipherTest.setExpectedOutputSize("PBEWITHSHAAND128BITAES-CBC-BC", Cipher.DECRYPT_MODE, 0);
        CipherTest.setExpectedOutputSize("PBEWITHSHAAND192BITAES-CBC-BC", Cipher.DECRYPT_MODE, 0);
        CipherTest.setExpectedOutputSize("PBEWITHSHAAND256BITAES-CBC-BC", Cipher.DECRYPT_MODE, 0);
        // AndroidOpenSSL returns the block size for the block ciphers
        CipherTest.setExpectedOutputSize("AES/CBC/PKCS5PADDING", Cipher.DECRYPT_MODE, "AndroidOpenSSL", 16);
        CipherTest.setExpectedOutputSize("AES/ECB/PKCS5PADDING", Cipher.DECRYPT_MODE, "AndroidOpenSSL", 16);
        if (StandardNames.IS_RI) {
            CipherTest.setExpectedOutputSize("AESWRAP", Cipher.WRAP_MODE, 8);
            CipherTest.setExpectedOutputSize("AESWRAP", Cipher.UNWRAP_MODE, 0);
        } else {
            CipherTest.setExpectedOutputSize("AESWRAP", (-1));
        }
        CipherTest.setExpectedOutputSize("ARC4", 0);
        CipherTest.setExpectedOutputSize("ARCFOUR", 0);
        CipherTest.setExpectedOutputSize("PBEWITHSHAAND40BITRC4", 0);
        CipherTest.setExpectedOutputSize("PBEWITHSHAAND128BITRC4", 0);
        CipherTest.setExpectedOutputSize("BLOWFISH", Cipher.ENCRYPT_MODE, 8);
        CipherTest.setExpectedOutputSize("BLOWFISH", Cipher.DECRYPT_MODE, 0);
        CipherTest.setExpectedOutputSize("DES", Cipher.ENCRYPT_MODE, 8);
        CipherTest.setExpectedOutputSize("PBEWITHMD5ANDDES", Cipher.ENCRYPT_MODE, 8);
        CipherTest.setExpectedOutputSize("PBEWITHSHA1ANDDES", Cipher.ENCRYPT_MODE, 8);
        CipherTest.setExpectedOutputSize("DES", Cipher.DECRYPT_MODE, 0);
        CipherTest.setExpectedOutputSize("PBEWITHMD5ANDDES", Cipher.DECRYPT_MODE, 0);
        CipherTest.setExpectedOutputSize("PBEWITHSHA1ANDDES", Cipher.DECRYPT_MODE, 0);
        CipherTest.setExpectedOutputSize("DESEDE", Cipher.ENCRYPT_MODE, 8);
        CipherTest.setExpectedOutputSize("PBEWITHSHAAND2-KEYTRIPLEDES-CBC", Cipher.ENCRYPT_MODE, 8);
        CipherTest.setExpectedOutputSize("PBEWITHSHAAND3-KEYTRIPLEDES-CBC", Cipher.ENCRYPT_MODE, 8);
        CipherTest.setExpectedOutputSize("PBEWITHMD5ANDTRIPLEDES", Cipher.ENCRYPT_MODE, 8);
        CipherTest.setExpectedOutputSize("PBEWITHSHA1ANDDESEDE", Cipher.ENCRYPT_MODE, 8);
        CipherTest.setExpectedOutputSize("DESEDE", Cipher.DECRYPT_MODE, 0);
        CipherTest.setExpectedOutputSize("PBEWITHSHAAND2-KEYTRIPLEDES-CBC", Cipher.DECRYPT_MODE, 0);
        CipherTest.setExpectedOutputSize("PBEWITHSHAAND3-KEYTRIPLEDES-CBC", Cipher.DECRYPT_MODE, 0);
        CipherTest.setExpectedOutputSize("PBEWITHMD5ANDTRIPLEDES", Cipher.DECRYPT_MODE, 0);
        CipherTest.setExpectedOutputSize("PBEWITHSHA1ANDDESEDE", Cipher.DECRYPT_MODE, 0);
        if (StandardNames.IS_RI) {
            CipherTest.setExpectedOutputSize("DESEDEWRAP", Cipher.WRAP_MODE, 16);
            CipherTest.setExpectedOutputSize("DESEDEWRAP", Cipher.UNWRAP_MODE, 0);
        } else {
            CipherTest.setExpectedOutputSize("DESEDEWRAP", (-1));
        }
        CipherTest.setExpectedOutputSize("RSA", Cipher.ENCRYPT_MODE, 256);
        CipherTest.setExpectedOutputSize("RSA/ECB/NoPadding", Cipher.ENCRYPT_MODE, 256);
        CipherTest.setExpectedOutputSize("RSA/ECB/PKCS1Padding", Cipher.ENCRYPT_MODE, 256);
        CipherTest.setExpectedOutputSize("RSA", Cipher.DECRYPT_MODE, 256);
        CipherTest.setExpectedOutputSize("RSA/ECB/NoPadding", Cipher.DECRYPT_MODE, 256);
        CipherTest.setExpectedOutputSize("RSA/ECB/PKCS1Padding", Cipher.DECRYPT_MODE, 245);
        // SunJCE returns the full for size even when PKCS1Padding is specified
        CipherTest.setExpectedOutputSize("RSA/ECB/PKCS1Padding", Cipher.DECRYPT_MODE, "SunJCE", 256);
        // BC strips the leading 0 for us even when NoPadding is specified
        CipherTest.setExpectedOutputSize("RSA", Cipher.DECRYPT_MODE, "BC", 255);
        CipherTest.setExpectedOutputSize("RSA/ECB/NoPadding", Cipher.DECRYPT_MODE, "BC", 255);
    }

    private static byte[] ORIGINAL_PLAIN_TEXT = new byte[]{ 10, 11, 12 };

    private static byte[] SIXTEEN_BYTE_BLOCK_PLAIN_TEXT = new byte[]{ 10, 11, 12, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

    private static byte[] PKCS1_BLOCK_TYPE_00_PADDED_PLAIN_TEXT = new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 11, 12 };

    private static byte[] PKCS1_BLOCK_TYPE_01_PADDED_PLAIN_TEXT = new byte[]{ ((byte) (0)), ((byte) (1)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (0)), ((byte) (10)), ((byte) (11)), ((byte) (12)) };

    private static byte[] PKCS1_BLOCK_TYPE_02_PADDED_PLAIN_TEXT = new byte[]{ ((byte) (0)), ((byte) (2)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (0)), ((byte) (10)), ((byte) (11)), ((byte) (12)) };

    public void test_getInstance() throws Exception {
        final ByteArrayOutputStream errBuffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(errBuffer);
        Set<String> seenBaseCipherNames = new HashSet<String>();
        Set<String> seenCiphersWithModeAndPadding = new HashSet<String>();
        Provider[] providers = Security.getProviders();
        for (Provider provider : providers) {
            Set<Provider.Service> services = provider.getServices();
            for (Provider.Service service : services) {
                String type = service.getType();
                if (!(type.equals("Cipher"))) {
                    continue;
                }
                String algorithm = service.getAlgorithm();
                /* Any specific modes and paddings aren't tested directly here,
                but we need to make sure we see the bare algorithm from some
                provider. We will test each mode specifically when we get the
                base cipher.
                 */
                final int firstSlash = algorithm.indexOf('/');
                if (firstSlash == (-1)) {
                    seenBaseCipherNames.add(algorithm);
                } else {
                    final String baseCipherName = algorithm.substring(0, firstSlash);
                    if (!(seenBaseCipherNames.contains(baseCipherName))) {
                        seenCiphersWithModeAndPadding.add(baseCipherName);
                    }
                    continue;
                }
                try {
                    test_Cipher_Algorithm(provider, algorithm);
                } catch (Throwable e) {
                    out.append((((("Error encountered checking " + algorithm) + " with provider ") + (provider.getName())) + "\n"));
                    e.printStackTrace(out);
                }
                Set<String> modes = StandardNames.getModesForCipher(algorithm);
                if (modes != null) {
                    for (String mode : modes) {
                        Set<String> paddings = StandardNames.getPaddingsForCipher(algorithm);
                        if (paddings != null) {
                            for (String padding : paddings) {
                                final String algorithmName = (((algorithm + "/") + mode) + "/") + padding;
                                try {
                                    test_Cipher_Algorithm(provider, algorithmName);
                                } catch (Throwable e) {
                                    out.append((((("Error encountered checking " + algorithmName) + " with provider ") + (provider.getName())) + "\n"));
                                    e.printStackTrace(out);
                                }
                            }
                        }
                    }
                }
            }
        }
        seenCiphersWithModeAndPadding.removeAll(seenBaseCipherNames);
        TestCase.assertEquals("Ciphers seen with mode and padding but not base cipher", Collections.EMPTY_SET, seenCiphersWithModeAndPadding);
        out.flush();
        if ((errBuffer.size()) > 0) {
            throw new Exception((("Errors encountered:\n\n" + (errBuffer.toString())) + "\n\n"));
        }
    }

    public void testInputPKCS1Padding() throws Exception {
        for (String provider : CipherTest.RSA_PROVIDERS) {
            testInputPKCS1Padding(provider);
        }
    }

    public void testOutputPKCS1Padding() throws Exception {
        for (String provider : CipherTest.RSA_PROVIDERS) {
            testOutputPKCS1Padding(provider);
        }
    }

    public void testCipherInitWithCertificate() throws Exception {
        // no key usage specified, everything is fine
        assertCipherInitWithKeyUsage(0, true, true, true, true);
        // common case is that encrypt/wrap is prohibited when special usage is specified
        assertCipherInitWithKeyUsage(digitalSignature, false, true, false, true);
        assertCipherInitWithKeyUsage(nonRepudiation, false, true, false, true);
        assertCipherInitWithKeyUsage(keyAgreement, false, true, false, true);
        assertCipherInitWithKeyUsage(keyCertSign, false, true, false, true);
        assertCipherInitWithKeyUsage(cRLSign, false, true, false, true);
        // Note they encipherOnly/decipherOnly don't have to do with
        // ENCRYPT_MODE or DECRYPT_MODE, but restrict usage relative
        // to keyAgreement. There is not a *_MODE option that
        // corresponds to this in Cipher, the RI does not enforce
        // anything in Cipher.
        // http://code.google.com/p/android/issues/detail?id=12955
        assertCipherInitWithKeyUsage(encipherOnly, false, true, false, true);
        assertCipherInitWithKeyUsage(decipherOnly, false, true, false, true);
        assertCipherInitWithKeyUsage(((KeyUsage.keyAgreement) | (KeyUsage.encipherOnly)), false, true, false, true);
        assertCipherInitWithKeyUsage(((KeyUsage.keyAgreement) | (KeyUsage.decipherOnly)), false, true, false, true);
        // except when wrapping a key is specifically allowed or
        assertCipherInitWithKeyUsage(keyEncipherment, false, true, true, true);
        // except when wrapping data encryption is specifically allowed
        assertCipherInitWithKeyUsage(dataEncipherment, true, true, false, true);
    }

    /* Test vectors generated with this private key:

    -----BEGIN RSA PRIVATE KEY-----
    MIIEpAIBAAKCAQEA4Ec+irjyKE/rnnQv+XSPoRjtmGM8kvUq63ouvg075gMpvnZq
    0Q62pRXQ0s/ZvqeTDwwwZTeJn3lYzT6FsB+IGFJNMSWEqUslHjYltUFB7b/uGYgI
    4buX/Hy0m56qr2jpyY19DtxTu8D6ADQ1bWMF+7zDxwAUBThqu8hzyw8+90JfPTPf
    ezFa4DbSoLZq/UdQOxab8247UWJRW3Ff2oPeryxYrrmr+zCXw8yd2dvl7ylsF2E5
    Ao6KZx5jBW1F9AGI0sQTNJCEXeUsJTTpxrJHjAe9rpKII7YtBmx3cPn2Pz26JH9T
    CER0e+eqqF2FO4vSRKzsPePImrRkU6tNJMOsaQIDAQABAoIBADd4R3al8XaY9ayW
    DfuDobZ1ZOZIvQWXz4q4CHGG8macJ6nsvdSA8Bl6gNBzCebGqW+SUzHlf4tKxvTU
    XtpFojJpwJ/EKMB6Tm7fc4oV3sl/q9Lyu0ehTyDqcvz+TDbgGtp3vRN82NTaELsW
    LpSkZilx8XX5hfoYjwVsuX7igW9Dq503R2Ekhs2owWGWwwgYqZXshdOEZ3kSZ7O/
    IfJzcQppJYYldoQcW2cSwS1L0govMpmtt8E12l6VFavadufK8qO+gFUdBzt4vxFi
    xIrSt/R0OgI47k0lL31efmUzzK5kzLOTYAdaL9HgNOw65c6cQIzL8OJeQRQCFoez
    3UdUroECgYEA9UGIS8Nzeyki1BGe9F4t7izUy7dfRVBaFXqlAJ+Zxzot8HJKxGAk
    MGMy6omBd2NFRl3G3x4KbxQK/ztzluaomUrF2qloc0cv43dJ0U6z4HXmKdvrNYMz
    im82SdCiZUp6Qv2atr+krE1IHTkLsimwZL3DEcwb4bYxidp8QM3s8rECgYEA6hp0
    LduIHO23KIyH442GjdekCdFaQ/RF1Td6C1cx3b/KLa8oqOE81cCvzsM0fXSjniNa
    PNljPydN4rlPkt9DgzkR2enxz1jyfeLgj/RZZMcg0+whOdx8r8kSlTzeyy81Wi4s
    NaUPrXVMs7IxZkJLo7bjESoriYw4xcFe2yOGkzkCgYBRgo8exv2ZYCmQG68dfjN7
    pfCvJ+mE6tiVrOYr199O5FoiQInyzBUa880XP84EdLywTzhqLNzA4ANrokGfVFeS
    YtRxAL6TGYSj76Bb7PFBV03AebOpXEqD5sQ/MhTW3zLVEt4ZgIXlMeYWuD/X3Z0f
    TiYHwzM9B8VdEH0dOJNYcQKBgQDbT7UPUN6O21P/NMgJMYigUShn2izKBIl3WeWH
    wkQBDa+GZNWegIPRbBZHiTAfZ6nweAYNg0oq29NnV1toqKhCwrAqibPzH8zsiiL+
    OVeVxcbHQitOXXSh6ajzDndZufwtY5wfFWc+hOk6XvFQb0MVODw41Fy9GxQEj0ch
    3IIyYQKBgQDYEUWTr0FfthLb8ZI3ENVNB0hiBadqO0MZSWjA3/HxHvD2GkozfV/T
    dBu8lkDkR7i2tsR8OsEgQ1fTsMVbqShr2nP2KSlvX6kUbYl2NX08dR51FIaWpAt0
    aFyCzjCQLWOdck/yTV4ulAfuNO3tLjtN9lqpvP623yjQe6aQPxZXaA==
    -----END RSA PRIVATE KEY-----
     */
    private static final BigInteger RSA_2048_modulus = new BigInteger(new byte[]{ ((byte) (0)), ((byte) (224)), ((byte) (71)), ((byte) (62)), ((byte) (138)), ((byte) (184)), ((byte) (242)), ((byte) (40)), ((byte) (79)), ((byte) (235)), ((byte) (158)), ((byte) (116)), ((byte) (47)), ((byte) (249)), ((byte) (116)), ((byte) (143)), ((byte) (161)), ((byte) (24)), ((byte) (237)), ((byte) (152)), ((byte) (99)), ((byte) (60)), ((byte) (146)), ((byte) (245)), ((byte) (42)), ((byte) (235)), ((byte) (122)), ((byte) (46)), ((byte) (190)), ((byte) (13)), ((byte) (59)), ((byte) (230)), ((byte) (3)), ((byte) (41)), ((byte) (190)), ((byte) (118)), ((byte) (106)), ((byte) (209)), ((byte) (14)), ((byte) (182)), ((byte) (165)), ((byte) (21)), ((byte) (208)), ((byte) (210)), ((byte) (207)), ((byte) (217)), ((byte) (190)), ((byte) (167)), ((byte) (147)), ((byte) (15)), ((byte) (12)), ((byte) (48)), ((byte) (101)), ((byte) (55)), ((byte) (137)), ((byte) (159)), ((byte) (121)), ((byte) (88)), ((byte) (205)), ((byte) (62)), ((byte) (133)), ((byte) (176)), ((byte) (31)), ((byte) (136)), ((byte) (24)), ((byte) (82)), ((byte) (77)), ((byte) (49)), ((byte) (37)), ((byte) (132)), ((byte) (169)), ((byte) (75)), ((byte) (37)), ((byte) (30)), ((byte) (54)), ((byte) (37)), ((byte) (181)), ((byte) (65)), ((byte) (65)), ((byte) (237)), ((byte) (191)), ((byte) (238)), ((byte) (25)), ((byte) (136)), ((byte) (8)), ((byte) (225)), ((byte) (187)), ((byte) (151)), ((byte) (252)), ((byte) (124)), ((byte) (180)), ((byte) (155)), ((byte) (158)), ((byte) (170)), ((byte) (175)), ((byte) (104)), ((byte) (233)), ((byte) (201)), ((byte) (141)), ((byte) (125)), ((byte) (14)), ((byte) (220)), ((byte) (83)), ((byte) (187)), ((byte) (192)), ((byte) (250)), ((byte) (0)), ((byte) (52)), ((byte) (53)), ((byte) (109)), ((byte) (99)), ((byte) (5)), ((byte) (251)), ((byte) (188)), ((byte) (195)), ((byte) (199)), ((byte) (0)), ((byte) (20)), ((byte) (5)), ((byte) (56)), ((byte) (106)), ((byte) (187)), ((byte) (200)), ((byte) (115)), ((byte) (203)), ((byte) (15)), ((byte) (62)), ((byte) (247)), ((byte) (66)), ((byte) (95)), ((byte) (61)), ((byte) (51)), ((byte) (223)), ((byte) (123)), ((byte) (49)), ((byte) (90)), ((byte) (224)), ((byte) (54)), ((byte) (210)), ((byte) (160)), ((byte) (182)), ((byte) (106)), ((byte) (253)), ((byte) (71)), ((byte) (80)), ((byte) (59)), ((byte) (22)), ((byte) (155)), ((byte) (243)), ((byte) (110)), ((byte) (59)), ((byte) (81)), ((byte) (98)), ((byte) (81)), ((byte) (91)), ((byte) (113)), ((byte) (95)), ((byte) (218)), ((byte) (131)), ((byte) (222)), ((byte) (175)), ((byte) (44)), ((byte) (88)), ((byte) (174)), ((byte) (185)), ((byte) (171)), ((byte) (251)), ((byte) (48)), ((byte) (151)), ((byte) (195)), ((byte) (204)), ((byte) (157)), ((byte) (217)), ((byte) (219)), ((byte) (229)), ((byte) (239)), ((byte) (41)), ((byte) (108)), ((byte) (23)), ((byte) (97)), ((byte) (57)), ((byte) (2)), ((byte) (142)), ((byte) (138)), ((byte) (103)), ((byte) (30)), ((byte) (99)), ((byte) (5)), ((byte) (109)), ((byte) (69)), ((byte) (244)), ((byte) (1)), ((byte) (136)), ((byte) (210)), ((byte) (196)), ((byte) (19)), ((byte) (52)), ((byte) (144)), ((byte) (132)), ((byte) (93)), ((byte) (229)), ((byte) (44)), ((byte) (37)), ((byte) (52)), ((byte) (233)), ((byte) (198)), ((byte) (178)), ((byte) (71)), ((byte) (140)), ((byte) (7)), ((byte) (189)), ((byte) (174)), ((byte) (146)), ((byte) (136)), ((byte) (35)), ((byte) (182)), ((byte) (45)), ((byte) (6)), ((byte) (108)), ((byte) (119)), ((byte) (112)), ((byte) (249)), ((byte) (246)), ((byte) (63)), ((byte) (61)), ((byte) (186)), ((byte) (36)), ((byte) (127)), ((byte) (83)), ((byte) (8)), ((byte) (68)), ((byte) (116)), ((byte) (123)), ((byte) (231)), ((byte) (170)), ((byte) (168)), ((byte) (93)), ((byte) (133)), ((byte) (59)), ((byte) (139)), ((byte) (210)), ((byte) (68)), ((byte) (172)), ((byte) (236)), ((byte) (61)), ((byte) (227)), ((byte) (200)), ((byte) (154)), ((byte) (180)), ((byte) (100)), ((byte) (83)), ((byte) (171)), ((byte) (77)), ((byte) (36)), ((byte) (195)), ((byte) (172)), ((byte) (105)) });

    private static final BigInteger RSA_2048_privateExponent = new BigInteger(new byte[]{ ((byte) (55)), ((byte) (120)), ((byte) (71)), ((byte) (118)), ((byte) (165)), ((byte) (241)), ((byte) (118)), ((byte) (152)), ((byte) (245)), ((byte) (172)), ((byte) (150)), ((byte) (13)), ((byte) (251)), ((byte) (131)), ((byte) (161)), ((byte) (182)), ((byte) (117)), ((byte) (100)), ((byte) (230)), ((byte) (72)), ((byte) (189)), ((byte) (5)), ((byte) (151)), ((byte) (207)), ((byte) (138)), ((byte) (184)), ((byte) (8)), ((byte) (113)), ((byte) (134)), ((byte) (242)), ((byte) (102)), ((byte) (156)), ((byte) (39)), ((byte) (169)), ((byte) (236)), ((byte) (189)), ((byte) (212)), ((byte) (128)), ((byte) (240)), ((byte) (25)), ((byte) (122)), ((byte) (128)), ((byte) (208)), ((byte) (115)), ((byte) (9)), ((byte) (230)), ((byte) (198)), ((byte) (169)), ((byte) (111)), ((byte) (146)), ((byte) (83)), ((byte) (49)), ((byte) (229)), ((byte) (127)), ((byte) (139)), ((byte) (74)), ((byte) (198)), ((byte) (244)), ((byte) (212)), ((byte) (94)), ((byte) (218)), ((byte) (69)), ((byte) (162)), ((byte) (50)), ((byte) (105)), ((byte) (192)), ((byte) (159)), ((byte) (196)), ((byte) (40)), ((byte) (192)), ((byte) (122)), ((byte) (78)), ((byte) (110)), ((byte) (223)), ((byte) (115)), ((byte) (138)), ((byte) (21)), ((byte) (222)), ((byte) (201)), ((byte) (127)), ((byte) (171)), ((byte) (210)), ((byte) (242)), ((byte) (187)), ((byte) (71)), ((byte) (161)), ((byte) (79)), ((byte) (32)), ((byte) (234)), ((byte) (114)), ((byte) (252)), ((byte) (254)), ((byte) (76)), ((byte) (54)), ((byte) (224)), ((byte) (26)), ((byte) (218)), ((byte) (119)), ((byte) (189)), ((byte) (19)), ((byte) (124)), ((byte) (216)), ((byte) (212)), ((byte) (218)), ((byte) (16)), ((byte) (187)), ((byte) (22)), ((byte) (46)), ((byte) (148)), ((byte) (164)), ((byte) (102)), ((byte) (41)), ((byte) (113)), ((byte) (241)), ((byte) (117)), ((byte) (249)), ((byte) (133)), ((byte) (250)), ((byte) (24)), ((byte) (143)), ((byte) (5)), ((byte) (108)), ((byte) (185)), ((byte) (126)), ((byte) (226)), ((byte) (129)), ((byte) (111)), ((byte) (67)), ((byte) (171)), ((byte) (157)), ((byte) (55)), ((byte) (71)), ((byte) (97)), ((byte) (36)), ((byte) (134)), ((byte) (205)), ((byte) (168)), ((byte) (193)), ((byte) (97)), ((byte) (150)), ((byte) (195)), ((byte) (8)), ((byte) (24)), ((byte) (169)), ((byte) (149)), ((byte) (236)), ((byte) (133)), ((byte) (211)), ((byte) (132)), ((byte) (103)), ((byte) (121)), ((byte) (18)), ((byte) (103)), ((byte) (179)), ((byte) (191)), ((byte) (33)), ((byte) (242)), ((byte) (115)), ((byte) (113)), ((byte) (10)), ((byte) (105)), ((byte) (37)), ((byte) (134)), ((byte) (37)), ((byte) (118)), ((byte) (132)), ((byte) (28)), ((byte) (91)), ((byte) (103)), ((byte) (18)), ((byte) (193)), ((byte) (45)), ((byte) (75)), ((byte) (210)), ((byte) (10)), ((byte) (47)), ((byte) (50)), ((byte) (153)), ((byte) (173)), ((byte) (183)), ((byte) (193)), ((byte) (53)), ((byte) (218)), ((byte) (94)), ((byte) (149)), ((byte) (21)), ((byte) (171)), ((byte) (218)), ((byte) (118)), ((byte) (231)), ((byte) (202)), ((byte) (242)), ((byte) (163)), ((byte) (190)), ((byte) (128)), ((byte) (85)), ((byte) (29)), ((byte) (7)), ((byte) (59)), ((byte) (120)), ((byte) (191)), ((byte) (17)), ((byte) (98)), ((byte) (196)), ((byte) (138)), ((byte) (210)), ((byte) (183)), ((byte) (244)), ((byte) (116)), ((byte) (58)), ((byte) (2)), ((byte) (56)), ((byte) (238)), ((byte) (77)), ((byte) (37)), ((byte) (47)), ((byte) (125)), ((byte) (94)), ((byte) (126)), ((byte) (101)), ((byte) (51)), ((byte) (204)), ((byte) (174)), ((byte) (100)), ((byte) (204)), ((byte) (179)), ((byte) (147)), ((byte) (96)), ((byte) (7)), ((byte) (90)), ((byte) (47)), ((byte) (209)), ((byte) (224)), ((byte) (52)), ((byte) (236)), ((byte) (58)), ((byte) (229)), ((byte) (206)), ((byte) (156)), ((byte) (64)), ((byte) (140)), ((byte) (203)), ((byte) (240)), ((byte) (226)), ((byte) (94)), ((byte) (65)), ((byte) (20)), ((byte) (2)), ((byte) (22)), ((byte) (135)), ((byte) (179)), ((byte) (221)), ((byte) (71)), ((byte) (84)), ((byte) (174)), ((byte) (129)) });

    private static final BigInteger RSA_2048_publicExponent = new BigInteger(new byte[]{ ((byte) (1)), ((byte) (0)), ((byte) (1)) });

    private static final BigInteger RSA_2048_primeP = new BigInteger(new byte[]{ ((byte) (0)), ((byte) (245)), ((byte) (65)), ((byte) (136)), ((byte) (75)), ((byte) (195)), ((byte) (115)), ((byte) (123)), ((byte) (41)), ((byte) (34)), ((byte) (212)), ((byte) (17)), ((byte) (158)), ((byte) (244)), ((byte) (94)), ((byte) (45)), ((byte) (238)), ((byte) (44)), ((byte) (212)), ((byte) (203)), ((byte) (183)), ((byte) (95)), ((byte) (69)), ((byte) (80)), ((byte) (90)), ((byte) (21)), ((byte) (122)), ((byte) (165)), ((byte) (0)), ((byte) (159)), ((byte) (153)), ((byte) (199)), ((byte) (58)), ((byte) (45)), ((byte) (240)), ((byte) (114)), ((byte) (74)), ((byte) (196)), ((byte) (96)), ((byte) (36)), ((byte) (48)), ((byte) (99)), ((byte) (50)), ((byte) (234)), ((byte) (137)), ((byte) (129)), ((byte) (119)), ((byte) (99)), ((byte) (69)), ((byte) (70)), ((byte) (93)), ((byte) (198)), ((byte) (223)), ((byte) (30)), ((byte) (10)), ((byte) (111)), ((byte) (20)), ((byte) (10)), ((byte) (255)), ((byte) (59)), ((byte) (115)), ((byte) (150)), ((byte) (230)), ((byte) (168)), ((byte) (153)), ((byte) (74)), ((byte) (197)), ((byte) (218)), ((byte) (169)), ((byte) (104)), ((byte) (115)), ((byte) (71)), ((byte) (47)), ((byte) (227)), ((byte) (119)), ((byte) (73)), ((byte) (209)), ((byte) (78)), ((byte) (179)), ((byte) (224)), ((byte) (117)), ((byte) (230)), ((byte) (41)), ((byte) (219)), ((byte) (235)), ((byte) (53)), ((byte) (131)), ((byte) (51)), ((byte) (138)), ((byte) (111)), ((byte) (54)), ((byte) (73)), ((byte) (208)), ((byte) (162)), ((byte) (101)), ((byte) (74)), ((byte) (122)), ((byte) (66)), ((byte) (253)), ((byte) (154)), ((byte) (182)), ((byte) (191)), ((byte) (164)), ((byte) (172)), ((byte) (77)), ((byte) (72)), ((byte) (29)), ((byte) (57)), ((byte) (11)), ((byte) (178)), ((byte) (41)), ((byte) (176)), ((byte) (100)), ((byte) (189)), ((byte) (195)), ((byte) (17)), ((byte) (204)), ((byte) (27)), ((byte) (225)), ((byte) (182)), ((byte) (49)), ((byte) (137)), ((byte) (218)), ((byte) (124)), ((byte) (64)), ((byte) (205)), ((byte) (236)), ((byte) (242)), ((byte) (177)) });

    private static final BigInteger RSA_2048_primeQ = new BigInteger(new byte[]{ ((byte) (0)), ((byte) (234)), ((byte) (26)), ((byte) (116)), ((byte) (45)), ((byte) (219)), ((byte) (136)), ((byte) (28)), ((byte) (237)), ((byte) (183)), ((byte) (40)), ((byte) (140)), ((byte) (135)), ((byte) (227)), ((byte) (141)), ((byte) (134)), ((byte) (141)), ((byte) (215)), ((byte) (164)), ((byte) (9)), ((byte) (209)), ((byte) (90)), ((byte) (67)), ((byte) (244)), ((byte) (69)), ((byte) (213)), ((byte) (55)), ((byte) (122)), ((byte) (11)), ((byte) (87)), ((byte) (49)), ((byte) (221)), ((byte) (191)), ((byte) (202)), ((byte) (45)), ((byte) (175)), ((byte) (40)), ((byte) (168)), ((byte) (225)), ((byte) (60)), ((byte) (213)), ((byte) (192)), ((byte) (175)), ((byte) (206)), ((byte) (195)), ((byte) (52)), ((byte) (125)), ((byte) (116)), ((byte) (163)), ((byte) (158)), ((byte) (35)), ((byte) (90)), ((byte) (60)), ((byte) (217)), ((byte) (99)), ((byte) (63)), ((byte) (39)), ((byte) (77)), ((byte) (226)), ((byte) (185)), ((byte) (79)), ((byte) (146)), ((byte) (223)), ((byte) (67)), ((byte) (131)), ((byte) (57)), ((byte) (17)), ((byte) (217)), ((byte) (233)), ((byte) (241)), ((byte) (207)), ((byte) (88)), ((byte) (242)), ((byte) (125)), ((byte) (226)), ((byte) (224)), ((byte) (143)), ((byte) (244)), ((byte) (89)), ((byte) (100)), ((byte) (199)), ((byte) (32)), ((byte) (211)), ((byte) (236)), ((byte) (33)), ((byte) (57)), ((byte) (220)), ((byte) (124)), ((byte) (175)), ((byte) (201)), ((byte) (18)), ((byte) (149)), ((byte) (60)), ((byte) (222)), ((byte) (203)), ((byte) (47)), ((byte) (53)), ((byte) (90)), ((byte) (46)), ((byte) (44)), ((byte) (53)), ((byte) (165)), ((byte) (15)), ((byte) (173)), ((byte) (117)), ((byte) (76)), ((byte) (179)), ((byte) (178)), ((byte) (49)), ((byte) (102)), ((byte) (66)), ((byte) (75)), ((byte) (163)), ((byte) (182)), ((byte) (227)), ((byte) (17)), ((byte) (42)), ((byte) (43)), ((byte) (137)), ((byte) (140)), ((byte) (56)), ((byte) (197)), ((byte) (193)), ((byte) (94)), ((byte) (219)), ((byte) (35)), ((byte) (134)), ((byte) (147)), ((byte) (57)) });

    /**
     * Test data is PKCS#1 padded "Android.\n" which can be generated by:
     * echo "Android." | openssl rsautl -inkey rsa.key -sign | openssl rsautl -inkey rsa.key -raw -verify | recode ../x1
     */
    private static final byte[] RSA_2048_Vector1 = new byte[]{ ((byte) (0)), ((byte) (1)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (0)), ((byte) (65)), ((byte) (110)), ((byte) (100)), ((byte) (114)), ((byte) (111)), ((byte) (105)), ((byte) (100)), ((byte) (46)), ((byte) (10)) };

    /**
     * This vector is simply "Android.\n" which is too short.
     */
    private static final byte[] TooShort_Vector = new byte[]{ ((byte) (65)), ((byte) (110)), ((byte) (100)), ((byte) (114)), ((byte) (111)), ((byte) (105)), ((byte) (100)), ((byte) (46)), ((byte) (10)) };

    /**
     * This vector is simply "Android.\n" padded with zeros.
     */
    private static final byte[] TooShort_Vector_Zero_Padded = new byte[]{ ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (65)), ((byte) (110)), ((byte) (100)), ((byte) (114)), ((byte) (111)), ((byte) (105)), ((byte) (100)), ((byte) (46)), ((byte) (10)) };

    /**
     * openssl rsautl -raw -sign -inkey rsa.key | recode ../x1 | sed 's/0x/(byte) 0x/g'
     */
    private static final byte[] RSA_Vector1_Encrypt_Private = new byte[]{ ((byte) (53)), ((byte) (67)), ((byte) (56)), ((byte) (68)), ((byte) (173)), ((byte) (63)), ((byte) (151)), ((byte) (2)), ((byte) (251)), ((byte) (89)), ((byte) (31)), ((byte) (74)), ((byte) (43)), ((byte) (185)), ((byte) (6)), ((byte) (236)), ((byte) (102)), ((byte) (230)), ((byte) (210)), ((byte) (197)), ((byte) (139)), ((byte) (123)), ((byte) (227)), ((byte) (24)), ((byte) (191)), ((byte) (7)), ((byte) (214)), ((byte) (1)), ((byte) (249)), ((byte) (217)), ((byte) (137)), ((byte) (196)), ((byte) (219)), ((byte) (0)), ((byte) (104)), ((byte) (255)), ((byte) (155)), ((byte) (67)), ((byte) (144)), ((byte) (242)), ((byte) (219)), ((byte) (131)), ((byte) (244)), ((byte) (126)), ((byte) (198)), ((byte) (129)), ((byte) (1)), ((byte) (58)), ((byte) (11)), ((byte) (229)), ((byte) (237)), ((byte) (8)), ((byte) (115)), ((byte) (62)), ((byte) (225)), ((byte) (63)), ((byte) (223)), ((byte) (31)), ((byte) (7)), ((byte) (109)), ((byte) (34)), ((byte) (141)), ((byte) (204)), ((byte) (78)), ((byte) (227)), ((byte) (154)), ((byte) (188)), ((byte) (204)), ((byte) (143)), ((byte) (158)), ((byte) (155)), ((byte) (2)), ((byte) (72)), ((byte) (0)), ((byte) (172)), ((byte) (159)), ((byte) (164)), ((byte) (143)), ((byte) (135)), ((byte) (161)), ((byte) (168)), ((byte) (230)), ((byte) (157)), ((byte) (205)), ((byte) (139)), ((byte) (5)), ((byte) (233)), ((byte) (210)), ((byte) (5)), ((byte) (141)), ((byte) (201)), ((byte) (149)), ((byte) (22)), ((byte) (208)), ((byte) (205)), ((byte) (67)), ((byte) (37)), ((byte) (138)), ((byte) (17)), ((byte) (70)), ((byte) (215)), ((byte) (116)), ((byte) (76)), ((byte) (207)), ((byte) (88)), ((byte) (249)), ((byte) (161)), ((byte) (48)), ((byte) (132)), ((byte) (82)), ((byte) (201)), ((byte) (1)), ((byte) (95)), ((byte) (36)), ((byte) (76)), ((byte) (177)), ((byte) (159)), ((byte) (125)), ((byte) (18)), ((byte) (56)), ((byte) (39)), ((byte) (15)), ((byte) (94)), ((byte) (255)), ((byte) (224)), ((byte) (85)), ((byte) (139)), ((byte) (163)), ((byte) (173)), ((byte) (96)), ((byte) (53)), ((byte) (131)), ((byte) (88)), ((byte) (175)), ((byte) (153)), ((byte) (222)), ((byte) (63)), ((byte) (93)), ((byte) (128)), ((byte) (128)), ((byte) (255)), ((byte) (155)), ((byte) (222)), ((byte) (92)), ((byte) (171)), ((byte) (151)), ((byte) (67)), ((byte) (100)), ((byte) (217)), ((byte) (159)), ((byte) (251)), ((byte) (103)), ((byte) (101)), ((byte) (165)), ((byte) (153)), ((byte) (231)), ((byte) (230)), ((byte) (235)), ((byte) (5)), ((byte) (149)), ((byte) (252)), ((byte) (70)), ((byte) (40)), ((byte) (75)), ((byte) (216)), ((byte) (140)), ((byte) (245)), ((byte) (10)), ((byte) (235)), ((byte) (31)), ((byte) (48)), ((byte) (234)), ((byte) (231)), ((byte) (103)), ((byte) (17)), ((byte) (37)), ((byte) (240)), ((byte) (68)), ((byte) (117)), ((byte) (116)), ((byte) (148)), ((byte) (6)), ((byte) (120)), ((byte) (208)), ((byte) (33)), ((byte) (244)), ((byte) (63)), ((byte) (200)), ((byte) (196)), ((byte) (74)), ((byte) (87)), ((byte) (190)), ((byte) (2)), ((byte) (60)), ((byte) (147)), ((byte) (246)), ((byte) (149)), ((byte) (251)), ((byte) (209)), ((byte) (119)), ((byte) (139)), ((byte) (67)), ((byte) (240)), ((byte) (185)), ((byte) (125)), ((byte) (224)), ((byte) (50)), ((byte) (225)), ((byte) (114)), ((byte) (181)), ((byte) (98)), ((byte) (63)), ((byte) (134)), ((byte) (195)), ((byte) (212)), ((byte) (95)), ((byte) (94)), ((byte) (84)), ((byte) (27)), ((byte) (91)), ((byte) (230)), ((byte) (116)), ((byte) (161)), ((byte) (11)), ((byte) (229)), ((byte) (24)), ((byte) (210)), ((byte) (79)), ((byte) (147)), ((byte) (243)), ((byte) (9)), ((byte) (88)), ((byte) (206)), ((byte) (240)), ((byte) (163)), ((byte) (97)), ((byte) (228)), ((byte) (110)), ((byte) (70)), ((byte) (69)), ((byte) (137)), ((byte) (80)), ((byte) (189)), ((byte) (3)), ((byte) (63)), ((byte) (56)), ((byte) (218)), ((byte) (93)), ((byte) (208)), ((byte) (27)), ((byte) (31)), ((byte) (177)), ((byte) (238)), ((byte) (137)), ((byte) (89)), ((byte) (197)) };

    private static final byte[] RSA_Vector1_ZeroPadded_Encrypted = new byte[]{ ((byte) (96)), ((byte) (74)), ((byte) (18)), ((byte) (163)), ((byte) (167)), ((byte) (74)), ((byte) (164)), ((byte) (191)), ((byte) (108)), ((byte) (54)), ((byte) (173)), ((byte) (102)), ((byte) (223)), ((byte) (206)), ((byte) (241)), ((byte) (228)), ((byte) (15)), ((byte) (212)), ((byte) (84)), ((byte) (95)), ((byte) (3)), ((byte) (21)), ((byte) (75)), ((byte) (158)), ((byte) (235)), ((byte) (254)), ((byte) (158)), ((byte) (36)), ((byte) (206)), ((byte) (142)), ((byte) (195)), ((byte) (54)), ((byte) (165)), ((byte) (118)), ((byte) (246)), ((byte) (84)), ((byte) (183)), ((byte) (132)), ((byte) (72)), ((byte) (47)), ((byte) (212)), ((byte) (69)), ((byte) (116)), ((byte) (72)), ((byte) (95)), ((byte) (8)), ((byte) (78)), ((byte) (156)), ((byte) (137)), ((byte) (204)), ((byte) (52)), ((byte) (64)), ((byte) (177)), ((byte) (95)), ((byte) (167)), ((byte) (14)), ((byte) (17)), ((byte) (75)), ((byte) (181)), ((byte) (148)), ((byte) (190)), ((byte) (20)), ((byte) (170)), ((byte) (170)), ((byte) (224)), ((byte) (56)), ((byte) (28)), ((byte) (206)), ((byte) (64)), ((byte) (97)), ((byte) (252)), ((byte) (8)), ((byte) (203)), ((byte) (20)), ((byte) (43)), ((byte) (166)), ((byte) (84)), ((byte) (223)), ((byte) (5)), ((byte) (92)), ((byte) (155)), ((byte) (79)), ((byte) (20)), ((byte) (147)), ((byte) (176)), ((byte) (112)), ((byte) (217)), ((byte) (50)), ((byte) (220)), ((byte) (36)), ((byte) (224)), ((byte) (174)), ((byte) (72)), ((byte) (252)), ((byte) (83)), ((byte) (238)), ((byte) (124)), ((byte) (159)), ((byte) (105)), ((byte) (52)), ((byte) (244)), ((byte) (118)), ((byte) (238)), ((byte) (103)), ((byte) (178)), ((byte) (167)), ((byte) (51)), ((byte) (28)), ((byte) (71)), ((byte) (255)), ((byte) (92)), ((byte) (240)), ((byte) (184)), ((byte) (4)), ((byte) (44)), ((byte) (253)), ((byte) (226)), ((byte) (177)), ((byte) (74)), ((byte) (10)), ((byte) (105)), ((byte) (28)), ((byte) (128)), ((byte) (43)), ((byte) (180)), ((byte) (80)), ((byte) (101)), ((byte) (92)), ((byte) (118)), ((byte) (120)), ((byte) (154)), ((byte) (12)), ((byte) (5)), ((byte) (98)), ((byte) (240)), ((byte) (196)), ((byte) (28)), ((byte) (56)), ((byte) (21)), ((byte) (208)), ((byte) (226)), ((byte) (90)), ((byte) (61)), ((byte) (182)), ((byte) (224)), ((byte) (136)), ((byte) (133)), ((byte) (209)), ((byte) (79)), ((byte) (126)), ((byte) (252)), ((byte) (119)), ((byte) (13)), ((byte) (42)), ((byte) (69)), ((byte) (213)), ((byte) (248)), ((byte) (60)), ((byte) (123)), ((byte) (45)), ((byte) (27)), ((byte) (130)), ((byte) (254)), ((byte) (88)), ((byte) (34)), ((byte) (71)), ((byte) (6)), ((byte) (88)), ((byte) (139)), ((byte) (79)), ((byte) (251)), ((byte) (155)), ((byte) (28)), ((byte) (112)), ((byte) (54)), ((byte) (18)), ((byte) (4)), ((byte) (23)), ((byte) (71)), ((byte) (138)), ((byte) (10)), ((byte) (236)), ((byte) (18)), ((byte) (59)), ((byte) (248)), ((byte) (210)), ((byte) (220)), ((byte) (60)), ((byte) (200)), ((byte) (70)), ((byte) (198)), ((byte) (81)), ((byte) (6)), ((byte) (6)), ((byte) (203)), ((byte) (132)), ((byte) (103)), ((byte) (181)), ((byte) (104)), ((byte) (217)), ((byte) (156)), ((byte) (212)), ((byte) (22)), ((byte) (92)), ((byte) (180)), ((byte) (226)), ((byte) (85)), ((byte) (230)), ((byte) (58)), ((byte) (115)), ((byte) (1)), ((byte) (29)), ((byte) (111)), ((byte) (48)), ((byte) (49)), ((byte) (89)), ((byte) (139)), ((byte) (47)), ((byte) (76)), ((byte) (231)), ((byte) (134)), ((byte) (76)), ((byte) (57)), ((byte) (78)), ((byte) (103)), ((byte) (59)), ((byte) (34)), ((byte) (155)), ((byte) (133)), ((byte) (90)), ((byte) (195)), ((byte) (41)), ((byte) (175)), ((byte) (140)), ((byte) (124)), ((byte) (89)), ((byte) (74)), ((byte) (36)), ((byte) (250)), ((byte) (186)), ((byte) (85)), ((byte) (64)), ((byte) (19)), ((byte) (100)), ((byte) (216)), ((byte) (203)), ((byte) (75)), ((byte) (152)), ((byte) (63)), ((byte) (174)), ((byte) (32)), ((byte) (253)), ((byte) (138)), ((byte) (80)), ((byte) (115)), ((byte) (228)) };

    public void testRSA_ECB_NoPadding_Private_OnlyDoFinal_Success() throws Exception {
        for (String provider : CipherTest.RSA_PROVIDERS) {
            testRSA_ECB_NoPadding_Private_OnlyDoFinal_Success(provider);
        }
    }

    public void testRSA_ECB_NoPadding_Private_UpdateThenEmptyDoFinal_Success() throws Exception {
        for (String provider : CipherTest.RSA_PROVIDERS) {
            testRSA_ECB_NoPadding_Private_UpdateThenEmptyDoFinal_Success(provider);
        }
    }

    public void testRSA_ECB_NoPadding_Private_SingleByteUpdateThenEmptyDoFinal_Success() throws Exception {
        for (String provider : CipherTest.RSA_PROVIDERS) {
            testRSA_ECB_NoPadding_Private_SingleByteUpdateThenEmptyDoFinal_Success(provider);
        }
    }

    public void testRSA_ECB_NoPadding_Private_OnlyDoFinalWithOffset_Success() throws Exception {
        for (String provider : CipherTest.RSA_PROVIDERS) {
            testRSA_ECB_NoPadding_Private_OnlyDoFinalWithOffset_Success(provider);
        }
    }

    public void testRSA_ECB_NoPadding_Public_OnlyDoFinal_Success() throws Exception {
        for (String provider : CipherTest.RSA_PROVIDERS) {
            testRSA_ECB_NoPadding_Public_OnlyDoFinal_Success(provider);
        }
    }

    public void testRSA_ECB_NoPadding_Public_OnlyDoFinalWithOffset_Success() throws Exception {
        for (String provider : CipherTest.RSA_PROVIDERS) {
            testRSA_ECB_NoPadding_Public_OnlyDoFinalWithOffset_Success(provider);
        }
    }

    public void testRSA_ECB_NoPadding_Public_UpdateThenEmptyDoFinal_Success() throws Exception {
        for (String provider : CipherTest.RSA_PROVIDERS) {
            testRSA_ECB_NoPadding_Public_UpdateThenEmptyDoFinal_Success(provider);
        }
    }

    public void testRSA_ECB_NoPadding_Public_SingleByteUpdateThenEmptyDoFinal_Success() throws Exception {
        for (String provider : CipherTest.RSA_PROVIDERS) {
            testRSA_ECB_NoPadding_Public_SingleByteUpdateThenEmptyDoFinal_Success(provider);
        }
    }

    public void testRSA_ECB_NoPadding_Public_TooSmall_Success() throws Exception {
        for (String provider : CipherTest.RSA_PROVIDERS) {
            testRSA_ECB_NoPadding_Public_TooSmall_Success(provider);
        }
    }

    public void testRSA_ECB_NoPadding_Private_TooSmall_Success() throws Exception {
        for (String provider : CipherTest.RSA_PROVIDERS) {
            testRSA_ECB_NoPadding_Private_TooSmall_Success(provider);
        }
    }

    public void testRSA_ECB_NoPadding_Private_CombinedUpdateAndDoFinal_TooBig_Failure() throws Exception {
        for (String provider : CipherTest.RSA_PROVIDERS) {
            testRSA_ECB_NoPadding_Private_CombinedUpdateAndDoFinal_TooBig_Failure(provider);
        }
    }

    public void testRSA_ECB_NoPadding_Private_UpdateInAndOutPlusDoFinal_TooBig_Failure() throws Exception {
        for (String provider : CipherTest.RSA_PROVIDERS) {
            testRSA_ECB_NoPadding_Private_UpdateInAndOutPlusDoFinal_TooBig_Failure(provider);
        }
    }

    public void testRSA_ECB_NoPadding_Private_OnlyDoFinal_TooBig_Failure() throws Exception {
        for (String provider : CipherTest.RSA_PROVIDERS) {
            testRSA_ECB_NoPadding_Private_OnlyDoFinal_TooBig_Failure(provider);
        }
    }

    public void testRSA_ECB_NoPadding_GetBlockSize_Success() throws Exception {
        for (String provider : CipherTest.RSA_PROVIDERS) {
            testRSA_ECB_NoPadding_GetBlockSize_Success(provider);
        }
    }

    public void testRSA_ECB_NoPadding_GetOutputSize_NoInit_Failure() throws Exception {
        for (String provider : CipherTest.RSA_PROVIDERS) {
            testRSA_ECB_NoPadding_GetOutputSize_NoInit_Failure(provider);
        }
    }

    public void testRSA_ECB_NoPadding_GetOutputSize_Success() throws Exception {
        for (String provider : CipherTest.RSA_PROVIDERS) {
            testRSA_ECB_NoPadding_GetOutputSize_Success(provider);
        }
    }

    public void testRSA_ECB_NoPadding_GetIV_Success() throws Exception {
        for (String provider : CipherTest.RSA_PROVIDERS) {
            testRSA_ECB_NoPadding_GetIV_Success(provider);
        }
    }

    public void testRSA_ECB_NoPadding_GetParameters_NoneProvided_Success() throws Exception {
        for (String provider : CipherTest.RSA_PROVIDERS) {
            testRSA_ECB_NoPadding_GetParameters_NoneProvided_Success(provider);
        }
    }

    /* Test vector generation:
    openssl rand -hex 16
    echo '3d4f8970b1f27537f40a39298a41555f' | sed 's/\(..\)/(byte) 0x\1, /g'
     */
    private static final byte[] AES_128_KEY = new byte[]{ ((byte) (61)), ((byte) (79)), ((byte) (137)), ((byte) (112)), ((byte) (177)), ((byte) (242)), ((byte) (117)), ((byte) (55)), ((byte) (244)), ((byte) (10)), ((byte) (57)), ((byte) (41)), ((byte) (138)), ((byte) (65)), ((byte) (85)), ((byte) (95)) };

    /* Test key generation:
    openssl rand -hex 24
    echo '5a7a3d7e40b64ed996f7afa15f97fd595e27db6af428e342' | sed 's/\(..\)/(byte) 0x\1, /g'
     */
    private static final byte[] AES_192_KEY = new byte[]{ ((byte) (90)), ((byte) (122)), ((byte) (61)), ((byte) (126)), ((byte) (64)), ((byte) (182)), ((byte) (78)), ((byte) (217)), ((byte) (150)), ((byte) (247)), ((byte) (175)), ((byte) (161)), ((byte) (95)), ((byte) (151)), ((byte) (253)), ((byte) (89)), ((byte) (94)), ((byte) (39)), ((byte) (219)), ((byte) (106)), ((byte) (244)), ((byte) (40)), ((byte) (227)), ((byte) (66)) };

    /* Test key generation:
    openssl rand -hex 32
    echo 'ec53c6d51d2c4973585fb0b8e51cd2e39915ff07a1837872715d6121bf861935' | sed 's/\(..\)/(byte) 0x\1, /g'
     */
    private static final byte[] AES_256_KEY = new byte[]{ ((byte) (236)), ((byte) (83)), ((byte) (198)), ((byte) (213)), ((byte) (29)), ((byte) (44)), ((byte) (73)), ((byte) (115)), ((byte) (88)), ((byte) (95)), ((byte) (176)), ((byte) (184)), ((byte) (229)), ((byte) (28)), ((byte) (210)), ((byte) (227)), ((byte) (153)), ((byte) (21)), ((byte) (255)), ((byte) (7)), ((byte) (161)), ((byte) (131)), ((byte) (120)), ((byte) (114)), ((byte) (113)), ((byte) (93)), ((byte) (97)), ((byte) (33)), ((byte) (191)), ((byte) (134)), ((byte) (25)), ((byte) (53)) };

    private static final byte[][] AES_KEYS = new byte[][]{ CipherTest.AES_128_KEY, CipherTest.AES_192_KEY, CipherTest.AES_256_KEY };

    private static final String[] AES_MODES = new String[]{ "AES/ECB", "AES/CBC", "AES/CFB", "AES/CTR", "AES/OFB" };

    /* Test vector creation:
    echo -n 'Hello, world!' | recode ../x1 | sed 's/0x/(byte) 0x/g'
     */
    private static final byte[] AES_128_ECB_PKCS5Padding_TestVector_1_Plaintext = new byte[]{ ((byte) (72)), ((byte) (101)), ((byte) (108)), ((byte) (108)), ((byte) (111)), ((byte) (44)), ((byte) (32)), ((byte) (119)), ((byte) (111)), ((byte) (114)), ((byte) (108)), ((byte) (100)), ((byte) (33)) };

    /* Test vector creation:
    openssl enc -aes-128-ecb -K 3d4f8970b1f27537f40a39298a41555f -in blah|openssl enc -aes-128-ecb -K 3d4f8970b1f27537f40a39298a41555f -nopad -d|recode ../x1 | sed 's/0x/(byte) 0x/g'
     */
    private static final byte[] AES_128_ECB_PKCS5Padding_TestVector_1_Plaintext_Padded = new byte[]{ ((byte) (72)), ((byte) (101)), ((byte) (108)), ((byte) (108)), ((byte) (111)), ((byte) (44)), ((byte) (32)), ((byte) (119)), ((byte) (111)), ((byte) (114)), ((byte) (108)), ((byte) (100)), ((byte) (33)), ((byte) (3)), ((byte) (3)), ((byte) (3)) };

    /* Test vector generation:
    openssl enc -aes-128-ecb -K 3d4f8970b1f27537f40a39298a41555f -in blah|recode ../x1 | sed 's/0x/(byte) 0x/g'
     */
    private static final byte[] AES_128_ECB_PKCS5Padding_TestVector_1_Encrypted = new byte[]{ ((byte) (101)), ((byte) (62)), ((byte) (134)), ((byte) (251)), ((byte) (5)), ((byte) (90)), ((byte) (82)), ((byte) (234)), ((byte) (221)), ((byte) (8)), ((byte) (231)), ((byte) (72)), ((byte) (51)), ((byte) (1)), ((byte) (252)), ((byte) (90)) };

    /* Test key generation:
    openssl rand -hex 16
    echo 'ceaa31952dfd3d0f5af4b2042ba06094' | sed 's/\(..\)/(byte) 0x\1, /g'
     */
    private static final byte[] AES_256_CBC_PKCS5Padding_TestVector_1_IV = new byte[]{ ((byte) (206)), ((byte) (170)), ((byte) (49)), ((byte) (149)), ((byte) (45)), ((byte) (253)), ((byte) (61)), ((byte) (15)), ((byte) (90)), ((byte) (244)), ((byte) (178)), ((byte) (4)), ((byte) (43)), ((byte) (160)), ((byte) (96)), ((byte) (148)) };

    /* Test vector generation:
    echo -n 'I only regret that I have but one test to write.' | recode ../x1 | sed 's/0x/(byte) 0x/g'
     */
    private static final byte[] AES_256_CBC_PKCS5Padding_TestVector_1_Plaintext = new byte[]{ ((byte) (73)), ((byte) (32)), ((byte) (111)), ((byte) (110)), ((byte) (108)), ((byte) (121)), ((byte) (32)), ((byte) (114)), ((byte) (101)), ((byte) (103)), ((byte) (114)), ((byte) (101)), ((byte) (116)), ((byte) (32)), ((byte) (116)), ((byte) (104)), ((byte) (97)), ((byte) (116)), ((byte) (32)), ((byte) (73)), ((byte) (32)), ((byte) (104)), ((byte) (97)), ((byte) (118)), ((byte) (101)), ((byte) (32)), ((byte) (98)), ((byte) (117)), ((byte) (116)), ((byte) (32)), ((byte) (111)), ((byte) (110)), ((byte) (101)), ((byte) (32)), ((byte) (116)), ((byte) (101)), ((byte) (115)), ((byte) (116)), ((byte) (32)), ((byte) (116)), ((byte) (111)), ((byte) (32)), ((byte) (119)), ((byte) (114)), ((byte) (105)), ((byte) (116)), ((byte) (101)), ((byte) (46)) };

    /* Test vector generation:
    echo -n 'I only regret that I have but one test to write.' | openssl enc -aes-256-cbc -K ec53c6d51d2c4973585fb0b8e51cd2e39915ff07a1837872715d6121bf861935 -iv ceaa31952dfd3d0f5af4b2042ba06094 | openssl enc -aes-256-cbc -K ec53c6d51d2c4973585fb0b8e51cd2e39915ff07a1837872715d6121bf861935 -iv ceaa31952dfd3d0f5af4b2042ba06094 -d -nopad | recode ../x1 | sed 's/0x/(byte) 0x/g'
     */
    private static final byte[] AES_256_CBC_PKCS5Padding_TestVector_1_Plaintext_Padded = new byte[]{ ((byte) (73)), ((byte) (32)), ((byte) (111)), ((byte) (110)), ((byte) (108)), ((byte) (121)), ((byte) (32)), ((byte) (114)), ((byte) (101)), ((byte) (103)), ((byte) (114)), ((byte) (101)), ((byte) (116)), ((byte) (32)), ((byte) (116)), ((byte) (104)), ((byte) (97)), ((byte) (116)), ((byte) (32)), ((byte) (73)), ((byte) (32)), ((byte) (104)), ((byte) (97)), ((byte) (118)), ((byte) (101)), ((byte) (32)), ((byte) (98)), ((byte) (117)), ((byte) (116)), ((byte) (32)), ((byte) (111)), ((byte) (110)), ((byte) (101)), ((byte) (32)), ((byte) (116)), ((byte) (101)), ((byte) (115)), ((byte) (116)), ((byte) (32)), ((byte) (116)), ((byte) (111)), ((byte) (32)), ((byte) (119)), ((byte) (114)), ((byte) (105)), ((byte) (116)), ((byte) (101)), ((byte) (46)), ((byte) (16)), ((byte) (16)), ((byte) (16)), ((byte) (16)), ((byte) (16)), ((byte) (16)), ((byte) (16)), ((byte) (16)), ((byte) (16)), ((byte) (16)), ((byte) (16)), ((byte) (16)), ((byte) (16)), ((byte) (16)), ((byte) (16)), ((byte) (16)) };

    /* Test vector generation:
    echo -n 'I only regret that I have but one test to write.' | openssl enc -aes-256-cbc -K ec53c6d51d2c4973585fb0b8e51cd2e39915ff07a1837872715d6121bf861935 -iv ceaa31952dfd3d0f5af4b2042ba06094 | recode ../x1 | sed 's/0x/(byte) 0x/g'
     */
    private static final byte[] AES_256_CBC_PKCS5Padding_TestVector_1_Ciphertext = new byte[]{ ((byte) (144)), ((byte) (101)), ((byte) (221)), ((byte) (175)), ((byte) (122)), ((byte) (206)), ((byte) (174)), ((byte) (191)), ((byte) (232)), ((byte) (246)), ((byte) (158)), ((byte) (219)), ((byte) (234)), ((byte) (101)), ((byte) (40)), ((byte) (196)), ((byte) (154)), ((byte) (40)), ((byte) (234)), ((byte) (163)), ((byte) (149)), ((byte) (46)), ((byte) (255)), ((byte) (241)), ((byte) (160)), ((byte) (202)), ((byte) (194)), ((byte) (164)), ((byte) (101)), ((byte) (205)), ((byte) (191)), ((byte) (206)), ((byte) (158)), ((byte) (241)), ((byte) (87)), ((byte) (246)), ((byte) (50)), ((byte) (46)), ((byte) (143)), ((byte) (147)), ((byte) (46)), ((byte) (174)), ((byte) (65)), ((byte) (51)), ((byte) (84)), ((byte) (208)), ((byte) (239)), ((byte) (140)), ((byte) (82)), ((byte) (20)), ((byte) (172)), ((byte) (45)), ((byte) (213)), ((byte) (164)), ((byte) (249)), ((byte) (32)), ((byte) (119)), ((byte) (37)), ((byte) (145)), ((byte) (63)), ((byte) (209)), ((byte) (185)), ((byte) (0)), ((byte) (62)) };

    private static class CipherTestParam {
        public final String mode;

        public final byte[] key;

        public final byte[] iv;

        public final byte[] plaintext;

        public final byte[] ciphertext;

        public final byte[] plaintextPadded;

        public CipherTestParam(String mode, byte[] key, byte[] iv, byte[] plaintext, byte[] plaintextPadded, byte[] ciphertext) {
            this.mode = mode;
            this.key = key;
            this.iv = iv;
            this.plaintext = plaintext;
            this.plaintextPadded = plaintextPadded;
            this.ciphertext = ciphertext;
        }
    }

    private static List<CipherTest.CipherTestParam> CIPHER_TEST_PARAMS = new ArrayList<CipherTest.CipherTestParam>();

    static {
        CipherTest.CIPHER_TEST_PARAMS.add(new CipherTest.CipherTestParam("AES/ECB", CipherTest.AES_128_KEY, null, CipherTest.AES_128_ECB_PKCS5Padding_TestVector_1_Plaintext, CipherTest.AES_128_ECB_PKCS5Padding_TestVector_1_Plaintext_Padded, CipherTest.AES_128_ECB_PKCS5Padding_TestVector_1_Encrypted));
        if (CipherTest.IS_UNLIMITED) {
            CipherTest.CIPHER_TEST_PARAMS.add(new CipherTest.CipherTestParam("AES/CBC", CipherTest.AES_256_KEY, CipherTest.AES_256_CBC_PKCS5Padding_TestVector_1_IV, CipherTest.AES_256_CBC_PKCS5Padding_TestVector_1_Plaintext, CipherTest.AES_256_CBC_PKCS5Padding_TestVector_1_Plaintext_Padded, CipherTest.AES_256_CBC_PKCS5Padding_TestVector_1_Ciphertext));
        }
    }

    public void testCipher_Success() throws Exception {
        for (String provider : CipherTest.AES_PROVIDERS) {
            testCipher_Success(provider);
        }
    }

    public void testCipher_updateAAD_BeforeInit_Failure() throws Exception {
        Cipher c = Cipher.getInstance("AES/ECB/NoPadding");
        try {
            c.updateAAD(((byte[]) (null)));
            TestCase.fail("should not be able to call updateAAD before Cipher is initialized");
        } catch (IllegalArgumentException expected) {
        }
        try {
            c.updateAAD(((ByteBuffer) (null)));
            TestCase.fail("should not be able to call updateAAD before Cipher is initialized");
        } catch (IllegalStateException expected) {
        }
        try {
            c.updateAAD(new byte[8]);
            TestCase.fail("should not be able to call updateAAD before Cipher is initialized");
        } catch (IllegalStateException expected) {
        }
        try {
            c.updateAAD(null, 0, 8);
            TestCase.fail("should not be able to call updateAAD before Cipher is initialized");
        } catch (IllegalStateException expected) {
        }
        ByteBuffer bb = ByteBuffer.allocate(8);
        try {
            c.updateAAD(bb);
            TestCase.fail("should not be able to call updateAAD before Cipher is initialized");
        } catch (IllegalStateException expected) {
        }
    }

    public void testCipher_updateAAD_AfterInit_Failure() throws Exception {
        Cipher c = Cipher.getInstance("AES/ECB/NoPadding");
        c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(new byte[128 / 8], "AES"));
        try {
            c.updateAAD(((byte[]) (null)));
            TestCase.fail("should not be able to call updateAAD with null input");
        } catch (IllegalArgumentException expected) {
        }
        try {
            c.updateAAD(((ByteBuffer) (null)));
            TestCase.fail("should not be able to call updateAAD with null input");
        } catch (IllegalArgumentException expected) {
        }
        try {
            c.updateAAD(null, 0, 8);
            TestCase.fail("should not be able to call updateAAD with null input");
        } catch (IllegalArgumentException expected) {
        }
        try {
            c.updateAAD(new byte[8], (-1), 7);
            TestCase.fail("should not be able to call updateAAD with invalid offset");
        } catch (IllegalArgumentException expected) {
        }
        try {
            c.updateAAD(new byte[8], 0, (-1));
            TestCase.fail("should not be able to call updateAAD with negative length");
        } catch (IllegalArgumentException expected) {
        }
        try {
            c.updateAAD(new byte[8], 0, (8 + 1));
            TestCase.fail("should not be able to call updateAAD with too large length");
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testCipher_ShortBlock_Failure() throws Exception {
        for (String provider : CipherTest.AES_PROVIDERS) {
            testCipher_ShortBlock_Failure(provider);
        }
    }

    public void testAES_ECB_PKCS5Padding_ShortBuffer_Failure() throws Exception {
        for (String provider : CipherTest.AES_PROVIDERS) {
            testAES_ECB_PKCS5Padding_ShortBuffer_Failure(provider);
        }
    }

    public void testAES_ECB_NoPadding_IncrementalUpdate_Success() throws Exception {
        for (String provider : CipherTest.AES_PROVIDERS) {
            testAES_ECB_NoPadding_IncrementalUpdate_Success(provider);
        }
    }

    private static final byte[] AES_IV_ZEROES = new byte[]{ ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)) };

    public void testAES_ECB_NoPadding_IvParameters_Failure() throws Exception {
        for (String provider : CipherTest.AES_PROVIDERS) {
            testAES_ECB_NoPadding_IvParameters_Failure(provider);
        }
    }

    public void testRC4_MultipleKeySizes() throws Exception {
        final int SMALLEST_KEY_SIZE = 40;
        final int LARGEST_KEY_SIZE = 1024;
        /* Make an array of keys for our tests */
        SecretKey[] keys = new SecretKey[LARGEST_KEY_SIZE - SMALLEST_KEY_SIZE];
        {
            KeyGenerator kg = KeyGenerator.getInstance("ARC4");
            for (int keysize = SMALLEST_KEY_SIZE; keysize < LARGEST_KEY_SIZE; keysize++) {
                final int index = keysize - SMALLEST_KEY_SIZE;
                kg.init(keysize);
                keys[index] = kg.generateKey();
            }
        }
        /* Use this to compare the output of the first provider against
        subsequent providers.
         */
        String[] expected = new String[LARGEST_KEY_SIZE - SMALLEST_KEY_SIZE];
        /* Find all providers that provide ARC4. We must have at least one! */
        Map<String, String> filter = new HashMap<String, String>();
        filter.put("Cipher.ARC4", "");
        Provider[] providers = Security.getProviders(filter);
        TestCase.assertTrue("There must be security providers of Cipher.ARC4", ((providers.length) > 0));
        /* Keep track of this for later error messages */
        String firstProvider = providers[0].getName();
        for (Provider p : providers) {
            Cipher c = Cipher.getInstance("ARC4", p);
            for (int keysize = SMALLEST_KEY_SIZE; keysize < LARGEST_KEY_SIZE; keysize++) {
                final int index = keysize - SMALLEST_KEY_SIZE;
                final SecretKey sk = keys[index];
                /* Test that encryption works. Donig this in a loop also has the
                benefit of testing that re-initialization works for this
                cipher.
                 */
                c.init(Cipher.ENCRYPT_MODE, sk);
                byte[] cipherText = c.doFinal(CipherTest.ORIGINAL_PLAIN_TEXT);
                TestCase.assertNotNull(cipherText);
                /* Compare providers against eachother to make sure they're all
                in agreement. This helps when you add a brand new provider.
                 */
                if ((expected[index]) == null) {
                    expected[index] = Arrays.toString(cipherText);
                } else {
                    TestCase.assertEquals(((((firstProvider + " should output the same as ") + (p.getName())) + " for key size ") + keysize), expected[index], Arrays.toString(cipherText));
                }
                c.init(Cipher.DECRYPT_MODE, sk);
                byte[] actualPlaintext = c.doFinal(cipherText);
                TestCase.assertEquals(("Key size: " + keysize), Arrays.toString(CipherTest.ORIGINAL_PLAIN_TEXT), Arrays.toString(actualPlaintext));
            }
        }
    }
}

