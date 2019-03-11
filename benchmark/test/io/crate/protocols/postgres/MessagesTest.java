/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.protocols.postgres;


import DataTypes.INTEGER;
import DataTypes.STRING;
import com.carrotsearch.randomizedtesting.RandomizedTest;
import io.crate.data.Buckets;
import io.crate.data.Row;
import io.crate.test.integration.CrateUnitTest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.embedded.EmbeddedChannel;
import java.util.Arrays;
import java.util.Collections;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class MessagesTest extends CrateUnitTest {
    @Test
    public void testBufferInSendDataRowIsReleasedIfGetValueFromRowFails() {
        Channel channel = Mockito.mock(Channel.class);
        ByteBufAllocator byteBufAllocator = Mockito.mock(ByteBufAllocator.class);
        ByteBuf buf = Unpooled.buffer();
        Mockito.when(byteBufAllocator.buffer()).thenReturn(buf);
        Mockito.when(channel.alloc()).thenReturn(byteBufAllocator);
        try {
            Messages.sendDataRow(channel, new Row() {
                @Override
                public int numColumns() {
                    return 1;
                }

                @Override
                public Object get(int index) {
                    throw new IllegalArgumentException("Dummy");
                }

                @Override
                public Object[] materialize() {
                    return Buckets.materialize(this);
                }
            }, Collections.singletonList(INTEGER), null);
            Assert.fail("sendDataRow should raise an exception");
        } catch (Exception ignored) {
        }
        assertThat(buf.refCnt(), Is.is(0));
    }

    @Test
    public void testNullValuesAddToLength() throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel();
        Messages.sendDataRow(channel, new io.crate.data.RowN(RandomizedTest.$(10, null)), Arrays.asList(INTEGER, STRING), null);
        channel.flush();
        ByteBuf buffer = channel.readOutbound();
        try {
            // message type
            assertThat(((char) (buffer.readByte())), Is.is('D'));
            // size of the message
            assertThat(buffer.readInt(), Is.is(16));
            assertThat(buffer.readableBytes(), Is.is(12));// 16 - INT4 because the size was already read

        } finally {
            buffer.release();
            channel.finishAndReleaseAll();
        }
    }

    @Test
    public void testCommandCompleteWithWhitespace() throws Exception {
        final EmbeddedChannel channel = new EmbeddedChannel();
        try {
            final String response = "SELECT 42";
            Messages.sendCommandComplete(channel, "Select 1", 42);
            MessagesTest.verifyResponse(channel, response);
            Messages.sendCommandComplete(channel, " Select 1", 42);
            MessagesTest.verifyResponse(channel, response);
            Messages.sendCommandComplete(channel, "  Select 1 ", 42);
            MessagesTest.verifyResponse(channel, response);
            Messages.sendCommandComplete(channel, "\n  Select 1", 42);
            MessagesTest.verifyResponse(channel, response);
        } finally {
            channel.finishAndReleaseAll();
        }
    }
}

