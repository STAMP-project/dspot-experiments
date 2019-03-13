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
package io.netty.handler.codec.stomp;


import CharsetUtil.UTF_8;
import LastStompContentSubframe.EMPTY_LAST_CONTENT;
import StompHeaders.ACCEPT_VERSION;
import StompHeaders.HOST;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Assert;
import org.junit.Test;

import static StompCommand.CONNECT;


public class StompSubframeEncoderTest {
    private EmbeddedChannel channel;

    @Test
    public void testFrameAndContentEncoding() {
        StompHeadersSubframe frame = new DefaultStompHeadersSubframe(CONNECT);
        StompHeaders headers = frame.headers();
        headers.set(HOST, "stomp.github.org");
        headers.set(ACCEPT_VERSION, "1.1,1.2");
        channel.writeOutbound(frame);
        channel.writeOutbound(EMPTY_LAST_CONTENT);
        ByteBuf aggregatedBuffer = Unpooled.buffer();
        ByteBuf byteBuf = channel.readOutbound();
        Assert.assertNotNull(byteBuf);
        aggregatedBuffer.writeBytes(byteBuf);
        byteBuf.release();
        byteBuf = channel.readOutbound();
        Assert.assertNotNull(byteBuf);
        aggregatedBuffer.writeBytes(byteBuf);
        byteBuf.release();
        aggregatedBuffer.resetReaderIndex();
        String content = aggregatedBuffer.toString(UTF_8);
        Assert.assertEquals(StompTestConstants.CONNECT_FRAME, content);
        aggregatedBuffer.release();
    }
}

