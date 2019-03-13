/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.license;


import java.security.KeyPair;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;


public class CryptoUtilsTest {
    @Test
    public void testGenerateRsaKeysDoNotProduceNullKeys() {
        KeyPair keyPair = CryptoUtils.generateRSAKeyPair();
        Assert.assertThat(keyPair.getPrivate(), Is.is(IsNull.notNullValue()));
        Assert.assertThat(keyPair.getPublic(), Is.is(IsNull.notNullValue()));
    }

    @Test
    public void testRsaEncryptionDecryption() {
        KeyPair keyPair = CryptoUtils.generateRSAKeyPair();
        String data = "data";
        byte[] encrypt = CryptoUtilsTest.encryptRsaUsingPrivateKey(data.getBytes(), CryptoUtils.getPrivateKeyBytes(keyPair.getPrivate()));
        Assert.assertThat(encrypt, Is.is(IsNull.notNullValue()));
        byte[] decrypt = CryptoUtils.decryptRSAUsingPublicKey(encrypt, CryptoUtils.getPublicKeyBytes(keyPair.getPublic()));
        Assert.assertThat(decrypt, Is.is(IsNull.notNullValue()));
        Assert.assertThat(new String(decrypt), Is.is(data));
    }

    @Test
    public void testAesEncryptionDecryption() {
        String data = "data";
        byte[] encrypt = CryptoUtils.encryptAES(data.getBytes());
        Assert.assertThat(encrypt, Is.is(IsNull.notNullValue()));
        byte[] decrypt = CryptoUtils.decryptAES(encrypt);
        Assert.assertThat(decrypt, Is.is(IsNull.notNullValue()));
        Assert.assertThat(new String(decrypt), Is.is(data));
    }
}

