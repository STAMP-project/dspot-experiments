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
package io.netty.handler.codec.bytes;


import EmptyArrays.EMPTY_BYTES;
import io.netty.buffer.ByteBuf;
import io.netty.channel.embedded.EmbeddedChannel;
import java.util.Random;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class ByteArrayEncoderTest {
    private EmbeddedChannel ch;

    @Test
    public void testEncode() {
        byte[] b = new byte[2048];
        new Random().nextBytes(b);
        ch.writeOutbound(b);
        ByteBuf encoded = ch.readOutbound();
        Assert.assertThat(encoded, CoreMatchers.is(wrappedBuffer(b)));
        encoded.release();
    }

    @Test
    public void testEncodeEmpty() {
        ch.writeOutbound(EMPTY_BYTES);
        Assert.assertThat(((ByteBuf) (ch.readOutbound())), CoreMatchers.is(CoreMatchers.sameInstance(EMPTY_BUFFER)));
    }

    @Test
    public void testEncodeOtherType() {
        String str = "Meep!";
        ch.writeOutbound(str);
        Assert.assertThat(ch.readOutbound(), CoreMatchers.is(((Object) (str))));
    }
}

