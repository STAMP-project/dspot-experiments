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
package io.netty.handler.ssl.ocsp;


import SslProvider.JDK;
import SslProvider.OPENSSL;
import SslProvider.OPENSSL_REFCNT;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ssl.ReferenceCountedOpenSslEngine;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.Assert;
import org.junit.Test;


public class OcspTest {
    @Test(expected = IllegalArgumentException.class)
    public void testJdkClientEnableOcsp() throws Exception {
        SslContextBuilder.forClient().sslProvider(JDK).enableOcsp(true).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJdkServerEnableOcsp() throws Exception {
        SelfSignedCertificate ssc = new SelfSignedCertificate();
        try {
            SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).sslProvider(JDK).enableOcsp(true).build();
        } finally {
            ssc.delete();
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testClientOcspNotEnabledOpenSsl() throws Exception {
        OcspTest.testClientOcspNotEnabled(OPENSSL);
    }

    @Test(expected = IllegalStateException.class)
    public void testClientOcspNotEnabledOpenSslRefCnt() throws Exception {
        OcspTest.testClientOcspNotEnabled(OPENSSL_REFCNT);
    }

    @Test(expected = IllegalStateException.class)
    public void testServerOcspNotEnabledOpenSsl() throws Exception {
        OcspTest.testServerOcspNotEnabled(OPENSSL);
    }

    @Test(expected = IllegalStateException.class)
    public void testServerOcspNotEnabledOpenSslRefCnt() throws Exception {
        OcspTest.testServerOcspNotEnabled(OPENSSL_REFCNT);
    }

    @Test(timeout = 10000L)
    public void testClientAcceptingOcspStapleOpenSsl() throws Exception {
        OcspTest.testClientAcceptingOcspStaple(OPENSSL);
    }

    @Test(timeout = 10000L)
    public void testClientAcceptingOcspStapleOpenSslRefCnt() throws Exception {
        OcspTest.testClientAcceptingOcspStaple(OPENSSL_REFCNT);
    }

    @Test(timeout = 10000L)
    public void testClientRejectingOcspStapleOpenSsl() throws Exception {
        OcspTest.testClientRejectingOcspStaple(OPENSSL);
    }

    @Test(timeout = 10000L)
    public void testClientRejectingOcspStapleOpenSslRefCnt() throws Exception {
        OcspTest.testClientRejectingOcspStaple(OPENSSL_REFCNT);
    }

    @Test(timeout = 10000L)
    public void testServerHasNoStapleOpenSsl() throws Exception {
        OcspTest.testServerHasNoStaple(OPENSSL);
    }

    @Test(timeout = 10000L)
    public void testServerHasNoStapleOpenSslRefCnt() throws Exception {
        OcspTest.testServerHasNoStaple(OPENSSL_REFCNT);
    }

    @Test(timeout = 10000L)
    public void testClientExceptionOpenSsl() throws Exception {
        OcspTest.testClientException(OPENSSL);
    }

    @Test(timeout = 10000L)
    public void testClientExceptionOpenSslRefCnt() throws Exception {
        OcspTest.testClientException(OPENSSL_REFCNT);
    }

    private interface OcspClientCallback {
        boolean verify(byte[] staple) throws Exception;
    }

    private static final class TestClientOcspContext implements OcspTest.OcspClientCallback {
        private final CountDownLatch latch = new CountDownLatch(1);

        private final boolean valid;

        private volatile byte[] response;

        TestClientOcspContext(boolean valid) {
            this.valid = valid;
        }

        public byte[] response() throws InterruptedException, TimeoutException {
            Assert.assertTrue(latch.await(10L, TimeUnit.SECONDS));
            return response;
        }

        @Override
        public boolean verify(byte[] response) throws Exception {
            this.response = response;
            latch.countDown();
            return valid;
        }
    }

    private static final class OcspClientCallbackHandler extends OcspClientHandler {
        private final OcspTest.OcspClientCallback callback;

        OcspClientCallbackHandler(ReferenceCountedOpenSslEngine engine, OcspTest.OcspClientCallback callback) {
            super(engine);
            this.callback = callback;
        }

        @Override
        protected boolean verify(ChannelHandlerContext ctx, ReferenceCountedOpenSslEngine engine) throws Exception {
            byte[] response = engine.getOcspResponse();
            return callback.verify(response);
        }
    }

    private static final class OcspTestException extends IllegalStateException {
        private static final long serialVersionUID = 4516426833250228159L;

        OcspTestException(String message) {
            super(message);
        }
    }
}

