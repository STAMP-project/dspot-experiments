/**
 * Copyright 2016 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.ssl;


import SslProvider.OPENSSL;
import SslProvider.OPENSSL_REFCNT;
import UnpooledByteBufAllocator.DEFAULT;
import java.security.PrivateKey;
import org.junit.Test;


public class PemEncodedTest {
    @Test
    public void testPemEncodedOpenSsl() throws Exception {
        PemEncodedTest.testPemEncoded(OPENSSL);
    }

    @Test
    public void testPemEncodedOpenSslRef() throws Exception {
        PemEncodedTest.testPemEncoded(OPENSSL_REFCNT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEncodedReturnsNull() throws Exception {
        PemPrivateKey.toPEM(DEFAULT, true, new PrivateKey() {
            @Override
            public String getAlgorithm() {
                return null;
            }

            @Override
            public String getFormat() {
                return null;
            }

            @Override
            public byte[] getEncoded() {
                return null;
            }
        });
    }
}

