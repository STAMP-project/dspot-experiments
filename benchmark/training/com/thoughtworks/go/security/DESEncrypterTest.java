/**
 * Copyright 2018 ThoughtWorks, Inc.
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
package com.thoughtworks.go.security;


import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;
import org.mockito.Mockito;


public class DESEncrypterTest {
    private DESEncrypter desEncrypter;

    @Test
    public void shouldNotEncryptText() {
        assertThatCode(() -> desEncrypter.encrypt(null)).isInstanceOf(UnsupportedOperationException.class).hasMessage("Encrypting using DES is no longer supported!");
    }

    @Test
    public void shouldDecryptText() throws CryptoException {
        String plainText = desEncrypter.decrypt("mvcX9yrQsM4iPgm1tDxN1A==");
        assertThat(plainText).isEqualTo("user-password!");
    }

    @Test
    public void canDecryptShouldAnswerTrueIfPasswordLooksLikeItIsNotAES() {
        assertThat(desEncrypter.canDecrypt("AES:foo:bar")).isFalse();
        assertThat(desEncrypter.canDecrypt("foobar")).isTrue();
    }

    @Test
    public void shouldErrorOutWhenCipherTextIsTamperedWith() {
        assertThatCode(() -> desEncrypter.decrypt("some bad junk")).hasMessageContaining("Illegal base64 character 20").hasCauseInstanceOf(IllegalArgumentException.class).isInstanceOf(CryptoException.class);
    }

    @Test
    public void shouldReEncryptUsingNewKey() throws CryptoException, DecoderException {
        String originalCipherText = "mvcX9yrQsM4iPgm1tDxN1A==";
        String newCipherText = DESEncrypter.reEncryptUsingNewKey(Hex.decodeHex("269298bc31c44620"), Hex.decodeHex("02644c13cb892962"), originalCipherText);
        assertThat(originalCipherText).isNotEqualTo(newCipherText);
        DESCipherProvider newCipher = Mockito.mock(DESCipherProvider.class);
        Mockito.when(newCipher.getKey()).thenReturn(Hex.decodeHex("02644c13cb892962"));
        DESEncrypter newEncrypter = new DESEncrypter(newCipher);
        assertThat(newEncrypter.decrypt(newCipherText)).isEqualTo("user-password!");
    }
}

