/**
 * Copyright 2016 The Netty Project
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
package io.netty.handler.codec.memcache.binary;


import CharsetUtil.UTF_8;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;


public class BinaryMemcacheMessageTest {
    @Test
    public void testSetLengths() {
        ByteBuf key = Unpooled.copiedBuffer("Netty  Rocks!", UTF_8);
        ByteBuf extras = Unpooled.copiedBuffer("some extras", UTF_8);
        ByteBuf content = Unpooled.copiedBuffer("content", UTF_8);
        try {
            BinaryMemcacheMessageTest.testSettingLengths(new DefaultBinaryMemcacheRequest(), 0, 0, 0);
            BinaryMemcacheMessageTest.testSettingLengths(new DefaultBinaryMemcacheRequest(key.retain()), key.readableBytes(), 0, 0);
            BinaryMemcacheMessageTest.testSettingLengths(new DefaultBinaryMemcacheRequest(key.retain(), extras.retain()), key.readableBytes(), extras.readableBytes(), 0);
            BinaryMemcacheMessageTest.testSettingLengths(new DefaultBinaryMemcacheResponse(), 0, 0, 0);
            BinaryMemcacheMessageTest.testSettingLengths(new DefaultBinaryMemcacheResponse(key.retain()), key.readableBytes(), 0, 0);
            BinaryMemcacheMessageTest.testSettingLengths(new DefaultBinaryMemcacheResponse(key.retain(), extras.retain()), key.readableBytes(), extras.readableBytes(), 0);
            BinaryMemcacheMessageTest.testSettingLengths(new DefaultFullBinaryMemcacheRequest(key.retain(), extras.retain()), key.readableBytes(), extras.readableBytes(), 0);
            BinaryMemcacheMessageTest.testSettingLengths(new DefaultFullBinaryMemcacheRequest(null, extras.retain()), 0, extras.readableBytes(), 0);
            BinaryMemcacheMessageTest.testSettingLengths(new DefaultFullBinaryMemcacheRequest(key.retain(), null), key.readableBytes(), 0, 0);
            BinaryMemcacheMessageTest.testSettingLengths(new DefaultFullBinaryMemcacheRequest(null, null), 0, 0, 0);
            BinaryMemcacheMessageTest.testSettingLengths(new DefaultFullBinaryMemcacheRequest(key.retain(), extras.retain(), content.retain()), key.readableBytes(), extras.readableBytes(), content.readableBytes());
            BinaryMemcacheMessageTest.testSettingLengths(new DefaultFullBinaryMemcacheRequest(null, extras.retain(), content.retain()), 0, extras.readableBytes(), content.readableBytes());
            BinaryMemcacheMessageTest.testSettingLengths(new DefaultFullBinaryMemcacheRequest(key.retain(), null, content.retain()), key.readableBytes(), 0, content.readableBytes());
            BinaryMemcacheMessageTest.testSettingLengths(new DefaultFullBinaryMemcacheRequest(null, null, content.retain()), 0, 0, content.readableBytes());
            BinaryMemcacheMessageTest.testSettingLengths(new DefaultFullBinaryMemcacheResponse(key.retain(), extras.retain()), key.readableBytes(), extras.readableBytes(), 0);
            BinaryMemcacheMessageTest.testSettingLengths(new DefaultFullBinaryMemcacheResponse(null, extras.retain()), 0, extras.readableBytes(), 0);
            BinaryMemcacheMessageTest.testSettingLengths(new DefaultFullBinaryMemcacheResponse(key.retain(), null), key.readableBytes(), 0, 0);
            BinaryMemcacheMessageTest.testSettingLengths(new DefaultFullBinaryMemcacheResponse(null, null), 0, 0, 0);
            BinaryMemcacheMessageTest.testSettingLengths(new DefaultFullBinaryMemcacheResponse(key.retain(), extras.retain(), content.retain()), key.readableBytes(), extras.readableBytes(), content.readableBytes());
            BinaryMemcacheMessageTest.testSettingLengths(new DefaultFullBinaryMemcacheResponse(null, extras.retain(), content.retain()), 0, extras.readableBytes(), content.readableBytes());
            BinaryMemcacheMessageTest.testSettingLengths(new DefaultFullBinaryMemcacheResponse(key.retain(), null, content.retain()), key.readableBytes(), 0, content.readableBytes());
            BinaryMemcacheMessageTest.testSettingLengths(new DefaultFullBinaryMemcacheResponse(null, null, content.retain()), 0, 0, content.readableBytes());
        } finally {
            key.release();
            extras.release();
            content.release();
        }
    }
}

