/**
 * Copyright 2013 The Netty Project
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
import ZlibWrapper.GZIP;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.ReferenceCountUtil;
import java.io.IOException;
import java.util.Arrays;
import java.util.Queue;
import org.apache.commons.compress.utils.IOUtils;
import org.junit.Assert;
import org.junit.Test;


public class JdkZlibTest extends ZlibTest {
    @Test(expected = DecompressionException.class)
    @Override
    public void testZLIB_OR_NONE3() throws Exception {
        super.testZLIB_OR_NONE3();
    }

    // verifies backward compatibility
    @Test
    public void testConcatenatedStreamsReadFirstOnly() throws IOException {
        EmbeddedChannel chDecoderGZip = new EmbeddedChannel(createDecoder(GZIP));
        try {
            byte[] bytes = IOUtils.toByteArray(getClass().getResourceAsStream("/multiple.gz"));
            Assert.assertTrue(chDecoderGZip.writeInbound(Unpooled.copiedBuffer(bytes)));
            Queue<Object> messages = chDecoderGZip.inboundMessages();
            Assert.assertEquals(1, messages.size());
            ByteBuf msg = ((ByteBuf) (messages.poll()));
            Assert.assertEquals("a", msg.toString(UTF_8));
            ReferenceCountUtil.release(msg);
        } finally {
            Assert.assertFalse(chDecoderGZip.finish());
            chDecoderGZip.close();
        }
    }

    @Test
    public void testConcatenatedStreamsReadFully() throws IOException {
        EmbeddedChannel chDecoderGZip = new EmbeddedChannel(new JdkZlibDecoder(true));
        try {
            byte[] bytes = IOUtils.toByteArray(getClass().getResourceAsStream("/multiple.gz"));
            Assert.assertTrue(chDecoderGZip.writeInbound(Unpooled.copiedBuffer(bytes)));
            Queue<Object> messages = chDecoderGZip.inboundMessages();
            Assert.assertEquals(2, messages.size());
            for (String s : Arrays.asList("a", "b")) {
                ByteBuf msg = ((ByteBuf) (messages.poll()));
                Assert.assertEquals(s, msg.toString(UTF_8));
                ReferenceCountUtil.release(msg);
            }
        } finally {
            Assert.assertFalse(chDecoderGZip.finish());
            chDecoderGZip.close();
        }
    }
}

