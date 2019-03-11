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


import java.security.InvalidAlgorithmParameterException;
import javax.crypto.BadPaddingException;
import org.junit.Test;


public class AESEncrypterTest {
    private AESEncrypter aesEncrypter;

    @Test
    public void shouldGenerateEncryptedText() throws CryptoException {
        String encrypt = aesEncrypter.encrypt("p@ssw0rd");
        assertThat(encrypt).startsWith("AES");
        assertThat(encrypt.split(":")).hasSize(3);
    }

    @Test
    public void shouldDecryptEncryptedText() throws CryptoException {
        String decrypt = aesEncrypter.decrypt("AES:lzcCuNSe4vUx+CsWgN11Uw==:DelRFu6mCN7kC/2oYmeLRA==");
        assertThat(decrypt).isEqualTo("p@ssw0rd");
    }

    @Test
    public void canDecryptShouldAnswerTrueIfPasswordLooksLikeAES() {
        assertThat(aesEncrypter.canDecrypt("AES:foo:bar")).isTrue();
        assertThat(aesEncrypter.canDecrypt("aes:bar:baz")).isFalse();
        assertThat(aesEncrypter.canDecrypt("")).isFalse();
        assertThat(aesEncrypter.canDecrypt("\t\n")).isFalse();
        assertThat(aesEncrypter.canDecrypt(null)).isFalse();
        assertThat(aesEncrypter.canDecrypt("foo:bar:baz")).isFalse();
        assertThat(aesEncrypter.canDecrypt("aes::")).isFalse();
        assertThat(aesEncrypter.canDecrypt("aes:asdf:")).isFalse();
        assertThat(aesEncrypter.canDecrypt("aes::asdf")).isFalse();
    }

    @Test
    public void shouldNotKillLeadingAndTrailingSpacesDuringEncryption() throws CryptoException {
        String plainText = "  \tfoobar\nbaz\t";
        String encrypt = aesEncrypter.encrypt(plainText);
        String decrypt = aesEncrypter.decrypt(encrypt);
        assertThat(decrypt).isEqualTo(plainText);
    }

    @Test
    public void shouldErrorOutWhenCipherTextIsTamperedWith() {
        assertThatCode(() -> aesEncrypter.decrypt("some junk that is not base 64 encoded")).isInstanceOf(CryptoException.class).hasCauseInstanceOf(IllegalArgumentException.class).hasMessageContaining("bad cipher text");
        assertThatCode(() -> aesEncrypter.decrypt("AES:foo:bar")).isInstanceOf(CryptoException.class).hasCauseInstanceOf(InvalidAlgorithmParameterException.class).hasMessageContaining("Wrong IV length: must be 16 bytes long");
        assertThatCode(() -> aesEncrypter.decrypt("AES:lzcCuNSe4vUx+CsWgN11Uw==z:junk")).isInstanceOf(CryptoException.class).hasCauseInstanceOf(IllegalArgumentException.class).hasMessageContaining("Input byte array has incorrect ending byte at 24");
        assertThatCode(() -> aesEncrypter.decrypt("AES:lzcCuNSe4vUx+CsWgN11Uw==z:@")).isInstanceOf(CryptoException.class).hasCauseInstanceOf(IllegalArgumentException.class).hasMessageContaining("Input byte array has incorrect ending byte at 24");
        assertThatCode(() -> aesEncrypter.decrypt("AES:lzcCuNSe4vUx+CsWgN11Uw==:DelRFu6mCN7kA/2oYmeLRA==")).isInstanceOf(CryptoException.class).hasCauseInstanceOf(BadPaddingException.class).hasMessageContaining("Given final block not properly padded");
    }
}

