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


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import org.junit.Assert;
import org.junit.Test;


public class SslUtilsTest {
    @SuppressWarnings("deprecation")
    @Test
    public void testPacketLength() throws NoSuchAlgorithmException, SSLException {
        SSLEngine engineLE = SslUtilsTest.newEngine();
        SSLEngine engineBE = SslUtilsTest.newEngine();
        ByteBuffer empty = ByteBuffer.allocate(0);
        ByteBuffer cTOsLE = ByteBuffer.allocate((17 * 1024)).order(ByteOrder.LITTLE_ENDIAN);
        ByteBuffer cTOsBE = ByteBuffer.allocate((17 * 1024));
        Assert.assertTrue(((engineLE.wrap(empty, cTOsLE).bytesProduced()) > 0));
        cTOsLE.flip();
        Assert.assertTrue(((engineBE.wrap(empty, cTOsBE).bytesProduced()) > 0));
        cTOsBE.flip();
        ByteBuf bufferLE = Unpooled.buffer().order(ByteOrder.LITTLE_ENDIAN).writeBytes(cTOsLE);
        ByteBuf bufferBE = Unpooled.buffer().writeBytes(cTOsBE);
        // Test that the packet-length for BE and LE is the same
        Assert.assertEquals(SslUtils.getEncryptedPacketLength(bufferBE, 0), SslUtils.getEncryptedPacketLength(bufferLE, 0));
        Assert.assertEquals(SslUtils.getEncryptedPacketLength(new ByteBuffer[]{ bufferBE.nioBuffer() }, 0), SslUtils.getEncryptedPacketLength(new ByteBuffer[]{ bufferLE.nioBuffer().order(ByteOrder.LITTLE_ENDIAN) }, 0));
    }

    @Test
    public void testIsTLSv13Cipher() {
        Assert.assertTrue(SslUtils.isTLSv13Cipher("TLS_AES_128_GCM_SHA256"));
        Assert.assertTrue(SslUtils.isTLSv13Cipher("TLS_AES_256_GCM_SHA384"));
        Assert.assertTrue(SslUtils.isTLSv13Cipher("TLS_CHACHA20_POLY1305_SHA256"));
        Assert.assertTrue(SslUtils.isTLSv13Cipher("TLS_AES_128_CCM_SHA256"));
        Assert.assertTrue(SslUtils.isTLSv13Cipher("TLS_AES_128_CCM_8_SHA256"));
        Assert.assertFalse(SslUtils.isTLSv13Cipher("TLS_DHE_RSA_WITH_AES_128_GCM_SHA256"));
    }
}

