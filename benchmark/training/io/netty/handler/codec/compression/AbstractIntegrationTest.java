/**
 * Copyright 2014 The Netty Project
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
package io.netty.handler.codec.compression;


import CharsetUtil.UTF_8;
import EmptyArrays.EMPTY_BYTES;
import io.netty.channel.embedded.EmbeddedChannel;
import java.util.Arrays;
import java.util.Random;
import org.junit.Test;


public abstract class AbstractIntegrationTest {
    protected static final Random rand = new Random();

    protected EmbeddedChannel encoder;

    protected EmbeddedChannel decoder;

    @Test
    public void testEmpty() throws Exception {
        testIdentity(EMPTY_BYTES);
    }

    @Test
    public void testOneByte() throws Exception {
        final byte[] data = new byte[]{ 'A' };
        testIdentity(data);
    }

    @Test
    public void testTwoBytes() throws Exception {
        final byte[] data = new byte[]{ 'B', 'A' };
        testIdentity(data);
    }

    @Test
    public void testRegular() throws Exception {
        final byte[] data = ("Netty is a NIO client server framework which enables " + ("quick and easy development of network applications such as protocol " + "servers and clients.")).getBytes(UTF_8);
        testIdentity(data);
    }

    @Test
    public void testLargeRandom() throws Exception {
        final byte[] data = new byte[1024 * 1024];
        AbstractIntegrationTest.rand.nextBytes(data);
        testIdentity(data);
    }

    @Test
    public void testPartRandom() throws Exception {
        final byte[] data = new byte[10240];
        AbstractIntegrationTest.rand.nextBytes(data);
        for (int i = 0; i < 1024; i++) {
            data[i] = 2;
        }
        testIdentity(data);
    }

    @Test
    public void testCompressible() throws Exception {
        final byte[] data = new byte[10240];
        for (int i = 0; i < (data.length); i++) {
            data[i] = ((i % 4) != 0) ? 0 : ((byte) (AbstractIntegrationTest.rand.nextInt()));
        }
        testIdentity(data);
    }

    @Test
    public void testLongBlank() throws Exception {
        final byte[] data = new byte[102400];
        testIdentity(data);
    }

    @Test
    public void testLongSame() throws Exception {
        final byte[] data = new byte[102400];
        Arrays.fill(data, ((byte) (123)));
        testIdentity(data);
    }

    @Test
    public void testSequential() throws Exception {
        final byte[] data = new byte[1024];
        for (int i = 0; i < (data.length); i++) {
            data[i] = ((byte) (i));
        }
        testIdentity(data);
    }
}

