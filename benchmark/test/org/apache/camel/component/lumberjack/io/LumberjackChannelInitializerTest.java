/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.lumberjack.io;


import io.netty.buffer.ByteBuf;
import io.netty.channel.embedded.EmbeddedChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class LumberjackChannelInitializerTest {
    @Test
    public void shouldDecodeTwoWindowsWithCompressedMessages() throws Exception {
        // Given a properly configured netty channel
        List<Object> messages = new ArrayList<>();
        EmbeddedChannel channel = new EmbeddedChannel(new LumberjackChannelInitializer(null, null, ( payload, callback) -> {
            messages.add(payload);
            callback.onComplete(true);
        }));
        // When writing the stream byte per byte in order to ensure that we support splits everywhere
        // It contains 2 windows with compressed messages
        writeResourceBytePerByte(channel, "window10");
        writeResourceBytePerByte(channel, "window15");
        // Then we must have 25 messages with only maps
        Assert.assertEquals(25, messages.size());
        // And the first map should contains valid data (we're assuming it's also valid for the other ones)
        Map first = ((Map) (messages.get(0)));
        Assert.assertEquals("log", first.get("type"));
        Assert.assertEquals("/home/qatest/collectNetwork/log/data-integration/00000000-f000-0000-1541-8da26f200001/absorption.log", first.get("source"));
        // And we should have replied twice (one per window)
        Assert.assertEquals(2, channel.outboundMessages().size());
        checkAck(((ByteBuf) (channel.outboundMessages().poll())), 10);
        checkAck(((ByteBuf) (channel.outboundMessages().poll())), 15);
    }
}

