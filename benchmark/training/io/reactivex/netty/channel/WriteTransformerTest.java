/**
 * Copyright 2016 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.reactivex.netty.channel;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;


public class WriteTransformerTest {
    @Rule
    public final WriteTransformerTest.ConverterRule converterRule = new WriteTransformerTest.ConverterRule();

    @Test(timeout = 60000)
    public void testWriteString() throws Exception {
        String msg = "Hello";
        converterRule.channel.writeAndFlush(msg);
        ByteBuf written = converterRule.readNextOutboundBuffer();
        MatcherAssert.assertThat("Unexpected content of buffer written.", written.toString(Charset.defaultCharset()), equalTo(msg));
    }

    @Test(timeout = 60000)
    public void testWriteByteArray() throws Exception {
        byte[] msg = "Hello".getBytes();
        converterRule.channel.writeAndFlush(msg);
        ByteBuf writtenMsg = converterRule.readNextOutboundBuffer();
        byte[] asBytes = new byte[msg.length];
        writtenMsg.readBytes(asBytes);
        MatcherAssert.assertThat("Unexpected content of buffer written.", asBytes, equalTo(msg));
    }

    @Test(timeout = 60000)
    public void testTransformerSingle() throws Exception {
        converterRule.appendTransformer(new AllocatingTransformer<Integer, ByteBuf>() {
            @Override
            public List<ByteBuf> transform(Integer toTransform, ByteBufAllocator allocator) {
                return Arrays.asList(allocator.buffer().writeInt(toTransform), allocator.buffer().writeInt((++toTransform)));
            }
        });
        converterRule.channel.writeAndFlush(1);
        ByteBuf written = converterRule.readNextOutboundBuffer(2);
        MatcherAssert.assertThat("Unexpected message written on the channel", written.readInt(), is(1));
        written = converterRule.readNextOutboundBuffer();
        MatcherAssert.assertThat("Unexpected message written on the channel", written.readInt(), is(2));
    }

    @Test(timeout = 60000)
    public void testTransformerChained() throws Exception {
        converterRule.appendTransformer(new AllocatingTransformer<Integer, ByteBuf>() {
            @Override
            public List<ByteBuf> transform(Integer toTransform, ByteBufAllocator allocator) {
                return Arrays.asList(allocator.buffer().writeInt(toTransform), allocator.buffer().writeInt((++toTransform)));
            }
        });
        converterRule.appendTransformer(new AllocatingTransformer<Long, Integer>() {
            @Override
            public List<Integer> transform(Long toTransform, ByteBufAllocator allocator) {
                int i = toTransform.intValue();
                return Arrays.asList(i, (++i));
            }
        });
        converterRule.channel.writeAndFlush(1L);
        ByteBuf written = converterRule.readNextOutboundBuffer(4);
        MatcherAssert.assertThat("Unexpected message written on the channel", written.readInt(), is(1));
        written = converterRule.readNextOutboundBuffer(3);
        MatcherAssert.assertThat("Unexpected message written on the channel", written.readInt(), is(2));
        written = converterRule.readNextOutboundBuffer(2);
        MatcherAssert.assertThat("Unexpected message written on the channel", written.readInt(), is(2));
        written = converterRule.readNextOutboundBuffer(1);
        MatcherAssert.assertThat("Unexpected message written on the channel", written.readInt(), is(3));
    }

    public static class ConverterRule extends ExternalResource {
        private WriteTransformer converter;

        private EmbeddedChannel channel;

        @Override
        public Statement apply(final Statement base, Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    converter = new WriteTransformer();
                    channel = new EmbeddedChannel(converter);
                    base.evaluate();
                }
            };
        }

        public ByteBuf readNextOutboundBuffer() {
            return readNextOutboundBuffer(1);
        }

        public ByteBuf readNextOutboundBuffer(int expectedWrittenMessages) {
            MatcherAssert.assertThat("Unexpected outbound messages size.", channel.outboundMessages(), hasSize(expectedWrittenMessages));
            Object writtenMsg = channel.readOutbound();
            MatcherAssert.assertThat("Unexpected message type written on the channel.", writtenMsg, is(instanceOf(ByteBuf.class)));
            return ((ByteBuf) (writtenMsg));
        }

        public void appendTransformer(AllocatingTransformer transformer) {
            @SuppressWarnings({ "rawtypes", "unchecked" })
            AppendTransformerEvent event = new AppendTransformerEvent(transformer);
            channel.pipeline().fireUserEventTriggered(event);
        }
    }
}

