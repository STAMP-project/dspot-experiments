/**
 * Copyright (C) 2014 Square, Inc.
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
package okhttp3;


import Protocol.HTTP_2;
import java.io.IOException;
import java.security.Security;
import okhttp3.internal.platform.ConscryptPlatform;
import okhttp3.internal.platform.Platform;
import org.conscrypt.OpenSSLProvider;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import static CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256;
import static CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384;
import static CipherSuite.TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256;
import static CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256;
import static CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384;
import static CipherSuite.TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256;


public class ConscryptTest {
    public static final CipherSuite[] MANDATORY_CIPHER_SUITES = new CipherSuite[]{ TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256, TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256, TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384, TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384, TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256, TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256 };

    private OkHttpClient client = buildClient();

    @Test
    public void testMozilla() throws IOException {
        ConscryptTest.assumeNetwork();
        ConscryptTest.assumeConscrypt();
        Request request = new Request.Builder().url("https://mozilla.org/robots.txt").build();
        Response response = client.newCall(request).execute();
        Assert.assertEquals(HTTP_2, response.protocol());
    }

    @Test
    public void testGoogle() throws IOException {
        ConscryptTest.assumeNetwork();
        ConscryptTest.assumeConscrypt();
        Request request = new Request.Builder().url("https://google.com/robots.txt").build();
        Response response = client.newCall(request).execute();
        Assert.assertEquals(HTTP_2, response.protocol());
    }

    @Test
    public void testBuild() {
        Assert.assertNotNull(ConscryptPlatform.buildIfSupported());
    }

    @Test
    public void testPreferred() {
        Assume.assumeFalse(Platform.isConscryptPreferred());
        try {
            Security.insertProviderAt(new OpenSSLProvider(), 1);
            Assert.assertTrue(Platform.isConscryptPreferred());
        } finally {
            Security.removeProvider("Conscrypt");
        }
    }
}

