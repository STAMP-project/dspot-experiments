/**
 * Copyright 2011-2016 the original author or authors.
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
package org.springframework.security.crypto.encrypt;


import org.junit.Test;
import org.springframework.security.crypto.keygen.KeyGenerators;


public class BouncyCastleAesBytesEncryptorTest {
    private byte[] testData;

    private String password;

    private String salt;

    @Test
    public void bcCbcWithSecureIvGeneratesDifferentMessages() throws Exception {
        BytesEncryptor bcEncryptor = new BouncyCastleAesCbcBytesEncryptor(password, salt);
        generatesDifferentCipherTexts(bcEncryptor);
    }

    @Test
    public void bcGcmWithSecureIvGeneratesDifferentMessages() throws Exception {
        BytesEncryptor bcEncryptor = new BouncyCastleAesGcmBytesEncryptor(password, salt);
        generatesDifferentCipherTexts(bcEncryptor);
    }

    @Test(expected = IllegalArgumentException.class)
    public void bcCbcWithWrongLengthIv() throws Exception {
        new BouncyCastleAesCbcBytesEncryptor(password, salt, KeyGenerators.secureRandom(8));
    }

    @Test(expected = IllegalArgumentException.class)
    public void bcGcmWithWrongLengthIv() throws Exception {
        new BouncyCastleAesGcmBytesEncryptor(password, salt, KeyGenerators.secureRandom(8));
    }
}

