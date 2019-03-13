/**
 * Copyright (C) 2015 Square, Inc.
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
package keywhiz.auth.cookie;


import java.nio.charset.StandardCharsets;
import javax.crypto.AEADBadTagException;
import org.junit.Test;


public class GCMEncryptorTest {
    final byte[] testMessage = "encryption works".getBytes(StandardCharsets.UTF_8);

    GCMEncryptor encryptor;

    @Test
    public void encryptsAndDecrypts() throws Exception {
        byte[] ciphertext = encryptor.encrypt(testMessage);
        assertThat(ciphertext).isNotEqualTo(testMessage);
        byte[] result = encryptor.decrypt(ciphertext);
        assertThat(testMessage).isEqualTo(result);
    }

    @Test(expected = AEADBadTagException.class)
    public void failsWhenMacIsIncorrect() throws Exception {
        byte[] ciphertext = encryptor.encrypt(testMessage);
        int flipIndex = (ciphertext.length) - 2;
        byte flipped = ((byte) (((int) (ciphertext[flipIndex])) ^ 1));
        ciphertext[flipIndex] = flipped;
        encryptor.decrypt(ciphertext);
    }

    @Test
    public void generatesUniqueNonces() throws Exception {
        byte[] ciphertext = encryptor.encrypt(testMessage);
        byte[] firstIV = GCMEncryptor.getNonce(ciphertext);
        ciphertext = encryptor.encrypt(testMessage);
        byte[] secondIV = GCMEncryptor.getNonce(ciphertext);
        assertThat(firstIV).isNotEqualTo(secondIV);
    }
}

