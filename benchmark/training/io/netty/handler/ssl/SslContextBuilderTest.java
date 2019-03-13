/**
 * Copyright 2015 The Netty Project
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


import SslProvider.JDK;
import SslProvider.OPENSSL;
import javax.net.ssl.SSLException;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


public class SslContextBuilderTest {
    @Test
    public void testClientContextFromFileJdk() throws Exception {
        SslContextBuilderTest.testClientContextFromFile(JDK);
    }

    @Test
    public void testClientContextFromFileOpenssl() throws Exception {
        Assume.assumeTrue(OpenSsl.isAvailable());
        SslContextBuilderTest.testClientContextFromFile(OPENSSL);
    }

    @Test
    public void testClientContextJdk() throws Exception {
        SslContextBuilderTest.testClientContext(JDK);
    }

    @Test
    public void testClientContextOpenssl() throws Exception {
        Assume.assumeTrue(OpenSsl.isAvailable());
        SslContextBuilderTest.testClientContext(OPENSSL);
    }

    @Test
    public void testServerContextFromFileJdk() throws Exception {
        SslContextBuilderTest.testServerContextFromFile(JDK);
    }

    @Test
    public void testServerContextFromFileOpenssl() throws Exception {
        Assume.assumeTrue(OpenSsl.isAvailable());
        SslContextBuilderTest.testServerContextFromFile(OPENSSL);
    }

    @Test
    public void testServerContextJdk() throws Exception {
        SslContextBuilderTest.testServerContext(JDK);
    }

    @Test
    public void testServerContextOpenssl() throws Exception {
        Assume.assumeTrue(OpenSsl.isAvailable());
        SslContextBuilderTest.testServerContext(OPENSSL);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCipherJdk() throws Exception {
        Assume.assumeTrue(OpenSsl.isAvailable());
        SslContextBuilderTest.testInvalidCipher(JDK);
    }

    @Test
    public void testInvalidCipherOpenSSL() throws Exception {
        Assume.assumeTrue(OpenSsl.isAvailable());
        try {
            // This may fail or not depending on the OpenSSL version used
            // See https://github.com/openssl/openssl/issues/7196
            SslContextBuilderTest.testInvalidCipher(OPENSSL);
            if (!(OpenSsl.versionString().contains("1.1.1"))) {
                Assert.fail();
            }
        } catch (SSLException expected) {
            // ok
        }
    }
}

