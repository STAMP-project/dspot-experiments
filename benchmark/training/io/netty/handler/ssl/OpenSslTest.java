/**
 * Copyright 2017 The Netty Project
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


import OpenSsl.DEFAULT_CIPHERS;
import SslUtils.DEFAULT_CIPHER_SUITES;
import org.junit.Assert;
import org.junit.Test;


public class OpenSslTest {
    @Test
    public void testDefaultCiphers() {
        if (!(OpenSsl.isTlsv13Supported())) {
            Assert.assertTrue(((DEFAULT_CIPHERS.size()) <= (DEFAULT_CIPHER_SUITES.length)));
        }
    }
}

