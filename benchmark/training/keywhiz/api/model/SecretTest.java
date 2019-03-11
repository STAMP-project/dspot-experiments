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
package keywhiz.api.model;


import java.nio.charset.StandardCharsets;
import java.util.Base64;
import keywhiz.api.ApiDate;
import org.junit.Test;


public class SecretTest {
    int called = 0;

    @Test
    public void decodedLengthCalculates() {
        Base64.Encoder encoder = Base64.getEncoder();
        String base64 = encoder.encodeToString("31337".getBytes(StandardCharsets.UTF_8));
        assertThat(Secret.decodedLength(base64)).isEqualTo(5);
        base64 = encoder.withoutPadding().encodeToString("31337".getBytes(StandardCharsets.UTF_8));
        assertThat(Secret.decodedLength(base64)).isEqualTo(5);
        String longerBase64 = encoder.encodeToString("313373133731337".getBytes(StandardCharsets.UTF_8));
        assertThat(Secret.decodedLength(longerBase64)).isEqualTo(15);
        longerBase64 = encoder.withoutPadding().encodeToString("313373133731337".getBytes(StandardCharsets.UTF_8));
        assertThat(Secret.decodedLength(longerBase64)).isEqualTo(15);
        assertThat(Secret.decodedLength("")).isZero();
    }

    @Test
    public void callsDecryptOnlyOnce() {
        Secret s = new Secret(42, "toto", null, () -> String.valueOf((++(called))), "checksum", ApiDate.now(), "", ApiDate.now(), "", null, null, null, 0, 1L, ApiDate.now(), "");
        assertThat(s.getSecret()).isEqualTo("1");
        assertThat(s.getSecret()).isEqualTo("1");
    }
}

