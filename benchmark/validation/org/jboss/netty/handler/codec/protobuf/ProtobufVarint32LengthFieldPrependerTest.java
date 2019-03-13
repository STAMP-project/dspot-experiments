/**
 * Copyright 2012 The Netty Project
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
package org.jboss.netty.handler.codec.protobuf;


import org.hamcrest.core.Is;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.embedder.EncoderEmbedder;
import org.junit.Assert;
import org.junit.Test;


public class ProtobufVarint32LengthFieldPrependerTest {
    private EncoderEmbedder<ChannelBuffer> embedder;

    @Test
    public void testTinyEncode() {
        byte[] b = new byte[]{ 4, 1, 1, 1, 1 };
        embedder.offer(wrappedBuffer(b, 1, ((b.length) - 1)));
        Assert.assertThat(embedder.poll(), Is.is(wrappedBuffer(b)));
    }

    @Test
    public void testRegularDecode() {
        byte[] b = new byte[2048];
        for (int i = 2; i < 2048; i++) {
            b[i] = 1;
        }
        b[0] = -2;
        b[1] = 15;
        embedder.offer(wrappedBuffer(b, 2, ((b.length) - 2)));
        Assert.assertThat(embedder.poll(), Is.is(wrappedBuffer(b)));
    }
}

