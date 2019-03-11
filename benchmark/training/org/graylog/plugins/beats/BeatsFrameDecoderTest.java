/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog.plugins.beats;


import TypeReferences.MAP_STRING_OBJECT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.graylog2.shared.bindings.providers.ObjectMapperProvider;
import org.junit.Test;


public class BeatsFrameDecoderTest {
    private final ObjectMapper objectMapper = new ObjectMapperProvider().get();

    private EmbeddedChannel channel;

    private BeatsFrameDecoder decoder;

    @Test
    public void decodeWindowSizeFrame() throws Exception {
        final ByteBuf buffer = buildWindowSizeFrame(1234);
        while (buffer.isReadable()) {
            channel.writeInbound(buffer);
        } 
        channel.finish();
        assertThat(buffer.readableBytes()).isEqualTo(0);
        assertThat(((Object) (channel.readInbound()))).isNull();
        assertThat(decoder.getWindowSize()).isEqualTo(1234L);
    }

    @Test
    public void decodeJsonFrame() throws Exception {
        final String json = "{\"answer\": 42}";
        final byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);
        final ByteBuf buffer = buildJsonFrame(jsonBytes, 0);
        while (buffer.isReadable()) {
            channel.writeInbound(buffer);
        } 
        channel.finish();
        assertThat(buffer.readableBytes()).isEqualTo(0);
        final ByteBuf replyBuffer = channel.readOutbound();
        assertThat(extractSequenceNumber(replyBuffer)).isEqualTo(0L);
        final ByteBuf resultBuffer = channel.readInbound();
        final byte[] resultBytes = new byte[resultBuffer.readableBytes()];
        resultBuffer.readBytes(resultBytes);
        final Map<String, Object> result = objectMapper.readValue(resultBytes, MAP_STRING_OBJECT);
        assertThat(result).hasSize(1).containsEntry("answer", 42);
    }

    @Test
    public void decodeDataFrame() throws Exception {
        final Map<String, String> data = ImmutableMap.of("foo", "bar", "quux", "baz");
        final ByteBuf buffer = buildDataFrame(data, 0);
        while (buffer.isReadable()) {
            channel.writeInbound(buffer);
        } 
        channel.finish();
        assertThat(buffer.readableBytes()).isEqualTo(0);
        final ByteBuf replyBuffer = channel.readOutbound();
        assertThat(extractSequenceNumber(replyBuffer)).isEqualTo(0L);
        final ByteBuf resultBuffer = channel.readInbound();
        final byte[] resultBytes = new byte[resultBuffer.readableBytes()];
        resultBuffer.readBytes(resultBytes);
        final Map<String, Object> result = objectMapper.readValue(resultBytes, MAP_STRING_OBJECT);
        assertThat(result).isEqualTo(data);
    }

    @Test
    public void decodeCompressedFrame() throws Exception {
        final String json = "{\"answer\": 42}";
        final byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);
        final ByteBuf innerBuffer = buildJsonFrame(jsonBytes, 0);
        final ByteBuf buffer = buildCompressedFrame(innerBuffer.array(), 3);
        while (buffer.isReadable()) {
            channel.writeInbound(buffer);
        } 
        channel.finish();
        assertThat(buffer.readableBytes()).isEqualTo(0);
        final ByteBuf replyBuffer = channel.readOutbound();
        assertThat(extractSequenceNumber(replyBuffer)).isEqualTo(0L);
        final ByteBuf resultBuffer = channel.readInbound();
        final byte[] resultBytes = new byte[resultBuffer.readableBytes()];
        resultBuffer.readBytes(resultBytes);
        final Map<String, Object> result = objectMapper.readValue(resultBytes, MAP_STRING_OBJECT);
        assertThat(result).hasSize(1).containsEntry("answer", 42);
    }

    @Test
    public void decodeMultipleFrames() throws Exception {
        final Map<String, String> data = ImmutableMap.of("foo", "bar");
        final ByteBuf buffer = Unpooled.copiedBuffer(buildWindowSizeFrame(2), buildDataFrame(data, 0), buildDataFrame(data, 1), buildDataFrame(data, 2));
        while (buffer.isReadable()) {
            channel.writeInbound(buffer);
        } 
        channel.finish();
        assertThat(buffer.readableBytes()).isEqualTo(0);
        final ByteBuf output1 = channel.readInbound();
        final ByteBuf output2 = channel.readInbound();
        final ByteBuf output3 = channel.readInbound();
        assertThat(decoder.getWindowSize()).isEqualTo(2);
        assertThat(decoder.getSequenceNum()).isEqualTo(2L);
        final ByteBuf replyBuffer = channel.readOutbound();
        assertThat(extractSequenceNumber(replyBuffer)).isEqualTo(2L);
        final ByteBuf[] received = new ByteBuf[]{ output1, output2, output3 };
        for (ByteBuf resultBuffer : received) {
            final byte[] resultBytes = new byte[resultBuffer.readableBytes()];
            resultBuffer.readBytes(resultBytes);
            final Map<String, Object> result = objectMapper.readValue(resultBytes, MAP_STRING_OBJECT);
            assertThat(result).isEqualTo(data);
        }
    }
}

