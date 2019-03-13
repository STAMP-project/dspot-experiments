/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.remoting.buffer;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ChannelBufferStreamTest {
    @Test
    public void testAll() throws Exception {
        ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
        try {
            new ChannelBufferOutputStream(null);
            Assertions.fail();
        } catch (NullPointerException e) {
            // Expected
        }
        ChannelBufferOutputStream out = new ChannelBufferOutputStream(buf);
        Assertions.assertSame(buf, out.buffer());
        out.write(new byte[0]);
        out.write(new byte[]{ 1, 2, 3, 4 });
        out.write(new byte[]{ 1, 3, 3, 4 }, 0, 0);
        out.close();
        try {
            new ChannelBufferInputStream(null);
            Assertions.fail();
        } catch (NullPointerException e) {
            // Expected
        }
        try {
            new ChannelBufferInputStream(null, 0);
            Assertions.fail();
        } catch (NullPointerException e) {
            // Expected
        }
        try {
            new ChannelBufferInputStream(buf, (-1));
        } catch (IllegalArgumentException e) {
            // Expected
        }
        try {
            new ChannelBufferInputStream(buf, ((buf.capacity()) + 1));
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        ChannelBufferInputStream in = new ChannelBufferInputStream(buf);
        Assertions.assertTrue(in.markSupported());
        in.mark(Integer.MAX_VALUE);
        Assertions.assertEquals(buf.writerIndex(), in.skip(Long.MAX_VALUE));
        Assertions.assertFalse(buf.readable());
        in.reset();
        Assertions.assertEquals(0, buf.readerIndex());
        Assertions.assertEquals(4, in.skip(4));
        Assertions.assertEquals(4, buf.readerIndex());
        in.reset();
        byte[] tmp = new byte[13];
        in.read(tmp);
        Assertions.assertEquals(1, tmp[0]);
        Assertions.assertEquals(2, tmp[1]);
        Assertions.assertEquals(3, tmp[2]);
        Assertions.assertEquals(4, tmp[3]);
        Assertions.assertEquals((-1), in.read());
        Assertions.assertEquals((-1), in.read(tmp));
        in.close();
        Assertions.assertEquals(buf.readerIndex(), in.readBytes());
    }
}

