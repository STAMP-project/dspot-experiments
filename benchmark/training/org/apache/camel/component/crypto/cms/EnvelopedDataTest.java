/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.crypto.cms;


import org.apache.camel.component.crypto.cms.exception.CryptoCmsException;
import org.apache.camel.component.crypto.cms.exception.CryptoCmsFormatException;
import org.apache.camel.component.crypto.cms.exception.CryptoCmsNoCertificateForRecipientsException;
import org.apache.camel.component.crypto.cms.exception.CryptoCmsNoKeyOrCertificateForAliasException;
import org.junit.Assert;
import org.junit.Test;


public class EnvelopedDataTest {
    @Test
    public void executeDESedeCBClength192() throws Exception {
        encryptDecrypt("system.jks", "rsa", "DESede/CBC/PKCS5Padding", 192);
    }

    @Test
    public void executeDESedeCBClength128() throws Exception {
        encryptDecrypt("system.jks", "rsa", "DESede/CBC/PKCS5Padding", 128);
    }

    @Test
    public void executeDESCBCkeyLength64() throws Exception {
        encryptDecrypt("system.jks", "rsa", "DES/CBC/PKCS5Padding", 64);
    }

    @Test
    public void executeDESCBCkeyLength56() throws Exception {
        encryptDecrypt("system.jks", "rsa", "DES/CBC/PKCS5Padding", 56);
    }

    @Test
    public void executeCAST5CBCkeyLength128() throws Exception {
        encryptDecrypt("system.jks", "rsa", "CAST5/CBC/PKCS5Padding", 128);
    }

    @Test
    public void executeCAST5CBCkeyLength120() throws Exception {
        encryptDecrypt("system.jks", "rsa", "CAST5/CBC/PKCS5Padding", 120);
    }

    @Test
    public void executeCAST5CBCkeyLength112() throws Exception {
        encryptDecrypt("system.jks", "rsa", "CAST5/CBC/PKCS5Padding", 112);
    }

    @Test
    public void executeCAST5CBCkeyLength104() throws Exception {
        encryptDecrypt("system.jks", "rsa", "CAST5/CBC/PKCS5Padding", 104);
    }

    @Test
    public void executeCAST5CBCkeyLength96() throws Exception {
        encryptDecrypt("system.jks", "rsa", "CAST5/CBC/PKCS5Padding", 96);
    }

    @Test
    public void executeCAST5CBCkeyLength88() throws Exception {
        encryptDecrypt("system.jks", "rsa", "CAST5/CBC/PKCS5Padding", 88);
    }

    @Test
    public void executeCAST5CBCkeyLength80() throws Exception {
        encryptDecrypt("system.jks", "rsa", "CAST5/CBC/PKCS5Padding", 80);
    }

    @Test
    public void executeCAST5CBCkeyLength72() throws Exception {
        encryptDecrypt("system.jks", "rsa", "CAST5/CBC/PKCS5Padding", 72);
    }

    @Test
    public void executeCAST5CBCkeyLength64() throws Exception {
        encryptDecrypt("system.jks", "rsa", "CAST5/CBC/PKCS5Padding", 64);
    }

    @Test
    public void executeCAST5CBCkeyLength56() throws Exception {
        encryptDecrypt("system.jks", "rsa", "CAST5/CBC/PKCS5Padding", 56);
    }

    @Test
    public void executeCAST5CBCkeyLength48() throws Exception {
        encryptDecrypt("system.jks", "rsa", "CAST5/CBC/PKCS5Padding", 48);
    }

    @Test
    public void executeCAST5CBCkeyLength40() throws Exception {
        encryptDecrypt("system.jks", "rsa", "CAST5/CBC/PKCS5Padding", 40);
    }

    @Test
    public void executeRC2CBCkeyLength128() throws Exception {
        encryptDecrypt("system.jks", "rsa", "RC2/CBC/PKCS5Padding", 128);
    }

    @Test
    public void executeRC2CBCkeyLength120() throws Exception {
        encryptDecrypt("system.jks", "rsa", "RC2/CBC/PKCS5Padding", 120);
    }

    @Test
    public void executeRC2CBCkeyLength112() throws Exception {
        encryptDecrypt("system.jks", "rsa", "RC2/CBC/PKCS5Padding", 112);
    }

    @Test
    public void executeRC2CBCkeyLength104() throws Exception {
        encryptDecrypt("system.jks", "rsa", "RC2/CBC/PKCS5Padding", 104);
    }

    @Test
    public void executeRC2CBCkeyLength96() throws Exception {
        encryptDecrypt("system.jks", "rsa", "RC2/CBC/PKCS5Padding", 96);
    }

    @Test
    public void executeRC2CBCkeyLength88() throws Exception {
        encryptDecrypt("system.jks", "rsa", "RC2/CBC/PKCS5Padding", 88);
    }

    @Test
    public void executeRC2CBCkeyLength80() throws Exception {
        encryptDecrypt("system.jks", "rsa", "RC2/CBC/PKCS5Padding", 80);
    }

    @Test
    public void executeRC2CBCkeyLength72() throws Exception {
        encryptDecrypt("system.jks", "rsa", "RC2/CBC/PKCS5Padding", 72);
    }

    @Test
    public void executeRC2CBCkeyLength64() throws Exception {
        encryptDecrypt("system.jks", "rsa", "RC2/CBC/PKCS5Padding", 64);
    }

    @Test
    public void executeRC2CBCkeyLength56() throws Exception {
        encryptDecrypt("system.jks", "rsa", "RC2/CBC/PKCS5Padding", 56);
    }

    @Test
    public void executeRC2CBCkeyLength48() throws Exception {
        encryptDecrypt("system.jks", "rsa", "RC2/CBC/PKCS5Padding", 48);
    }

    @Test
    public void executeRC2CBCkeyLength40() throws Exception {
        encryptDecrypt("system.jks", "rsa", "RC2/CBC/PKCS5Padding", 40);
    }

    @Test
    public void executeCamelliaCBCKeySize128() throws Exception {
        encryptDecrypt("system.jks", "rsa", "Camellia/CBC/PKCS5Padding", 128);
    }

    @Test(expected = CryptoCmsException.class)
    public void executeNoInWhiteListCamellia256CBC() throws Exception {
        encryptDecrypt("system.jks", "rsa", "Camellia256/CBC/PKCS5Padding", 256);
    }

    @Test(expected = CryptoCmsException.class)
    public void executeNotInWhiteListCamellia192CBC() throws Exception {
        encryptDecrypt("system.jks", "rsa", "Camellia192/CBC/PKCS5Padding", 192);
    }

    @Test
    public void executeAESCBCKeySize128() throws Exception {
        encryptDecrypt("system.jks", "rsa", "AES/CBC/PKCS5Padding", 128);
    }

    @Test(expected = CryptoCmsException.class)
    public void executeNotInWhiteListAES256CBC() throws Exception {
        encryptDecrypt("system.jks", "rsa", "AES256/CBC/PKCS5Padding", 256);
    }

    @Test(expected = CryptoCmsException.class)
    public void executeNotInWhiteListAES192CBC() throws Exception {
        encryptDecrypt("system.jks", "rsa", "AES192/CBC/PKCS5Padding", 192);
    }

    @Test(expected = CryptoCmsException.class)
    public void executerNoImplRSAECB() throws Exception {
        encryptDecrypt("system.jks", "rsa", "RSA/ECB/OAEP", 0);
    }

    @Test(expected = CryptoCmsException.class)
    public void executeNotInWhiteListAESGCM() throws Exception {
        encryptDecrypt("system.jks", "rsa", "AES/GCM/NoPadding", 128);
    }

    @Test(expected = CryptoCmsException.class)
    public void executeNotInWhiteListAES192GCM() throws Exception {
        encryptDecrypt("system.jks", "rsa", "AES192/GCM/NoPadding", 192);
    }

    @Test(expected = CryptoCmsException.class)
    public void executeNotInWhiteListAES256GCM() throws Exception {
        encryptDecrypt("system.jks", "rsa", "AES256/GCM/NoPadding", 256);
    }

    @Test(expected = CryptoCmsException.class)
    public void executeNotInWhiteListAES256CCM() throws Exception {
        encryptDecrypt("system.jks", "rsa", "AES256/CCM/NoPadding", 256);
    }

    @Test(expected = CryptoCmsException.class)
    public void executeNotInWhiteListIDEACBC() throws Exception {
        encryptDecrypt("system.jks", "rsa", "IDEA/CBC/PKCS5Padding", 128);
    }

    @Test(expected = CryptoCmsException.class)
    public void executeNotInWhiteListAESCCM() throws Exception {
        encryptDecrypt("system.jks", "rsa", "AES/CCM/NoPadding", 128);
    }

    @Test(expected = CryptoCmsException.class)
    public void executeNotInWhiteListAES192CCM() throws Exception {
        encryptDecrypt("system.jks", "rsa", "AES192/CCM/NoPadding", 192);
    }

    @Test(expected = CryptoCmsException.class)
    public void executeNotInWhiteListRC5CBC() throws Exception {
        encryptDecrypt("system.jks", "rsa", "RC5/CBC/PKCS5Padding", 0);
    }

    @Test(expected = CryptoCmsException.class)
    public void wrongSecretKeyLength() throws Exception {
        encrypt("system.jks", "DESede/CBC/PKCS5Padding", 200, "testMessage", "rsa");
    }

    @Test(expected = CryptoCmsException.class)
    public void wrongContentEncryptionAlgorithm() throws Exception {
        encryptDecrypt("system.jks", "rsa", "WrongDESede/CBC/PKCS5Padding", 200);
    }

    @Test(expected = CryptoCmsNoKeyOrCertificateForAliasException.class)
    public void wrongEncryptAlias() throws Exception {
        encrypt("system.jks", "DESede/CBC/PKCS5Padding", 128, "testMessage", "wrongAlias");
    }

    @Test(expected = CryptoCmsNoKeyOrCertificateForAliasException.class)
    public void encryptWrongAliasAndCorrectAlias() throws Exception {
        encrypt("system.jks", "DESede/CBC/PKCS5Padding", 128, "testMessage", "wrongAlias", "rsa");
    }

    @Test(expected = CryptoCmsNoKeyOrCertificateForAliasException.class)
    public void encryptTwoWrongAliases() throws Exception {
        encrypt("system.jks", "DESede/CBC/PKCS5Padding", 128, "testMessage", "wrongAlias", "wrongAlias2");
    }

    @Test
    public void encryptTwoCorrectAliases() throws Exception {
        encrypt("system.jks", "DESede/CBC/PKCS5Padding", 128, "testMessage", "rsa2", "rsa");
    }

    @Test(expected = CryptoCmsFormatException.class)
    public void wrongEncryptedMessage() throws Exception {
        decrypt("system.jks", "TestMessage".getBytes());
    }

    @Test(expected = CryptoCmsFormatException.class)
    public void wrongEncryptedEmptyMessage() throws Exception {
        decrypt("system.jks", new byte[0]);
    }

    @Test
    public void decryptionWithEmptyAlias() throws Exception {
        byte[] bytes = null;
        try {
            bytes = encrypt("system.jks", "DESede/CBC/PKCS5Padding", 192, "Test Message", "rsa");
        } catch (Exception e) {
            Assert.fail(("Unexpected exception: " + (e.getMessage())));
        }
        decrypt("system.jks", bytes);
    }

    @Test(expected = CryptoCmsNoCertificateForRecipientsException.class)
    public void decryptionWithNullAliasWrongKeystore() throws Exception {
        byte[] bytes = null;
        try {
            bytes = encrypt("system.jks", "DESede/CBC/PKCS5Padding", 192, "Test Message", "rsa");
        } catch (Exception e) {
            Assert.fail(("Unexpected exception: " + (e.getMessage())));
        }
        decrypt("test.jks", bytes);
    }
}

