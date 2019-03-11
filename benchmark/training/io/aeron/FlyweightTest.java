/**
 * Copyright 2014-2019 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.aeron;


import DataHeaderFlyweight.BEGIN_AND_END_FLAGS;
import DataHeaderFlyweight.HEADER_LENGTH;
import HeaderFlyweight.HDR_TYPE_DATA;
import HeaderFlyweight.HDR_TYPE_NAK;
import HeaderFlyweight.HDR_TYPE_SM;
import io.aeron.command.PublicationMessageFlyweight;
import io.aeron.protocol.DataHeaderFlyweight;
import io.aeron.protocol.HeaderFlyweight;
import io.aeron.protocol.NakFlyweight;
import java.nio.ByteBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class FlyweightTest {
    private final ByteBuffer buffer = ByteBuffer.allocateDirect(512);

    private final UnsafeBuffer aBuff = new UnsafeBuffer(buffer);

    private final HeaderFlyweight encodeHeader = new HeaderFlyweight();

    private final HeaderFlyweight decodeHeader = new HeaderFlyweight();

    private final DataHeaderFlyweight encodeDataHeader = new DataHeaderFlyweight();

    private final DataHeaderFlyweight decodeDataHeader = new DataHeaderFlyweight();

    private final PublicationMessageFlyweight encodePublication = new PublicationMessageFlyweight();

    private final PublicationMessageFlyweight decodePublication = new PublicationMessageFlyweight();

    private final NakFlyweight encodeNakHeader = new NakFlyweight();

    private final NakFlyweight decodeNakHeader = new NakFlyweight();

    @Test
    public void shouldWriteCorrectValuesForGenericHeaderFields() {
        encodeHeader.wrap(aBuff);
        encodeHeader.version(((short) (1)));
        encodeHeader.flags(BEGIN_AND_END_FLAGS);
        encodeHeader.headerType(HDR_TYPE_DATA);
        encodeHeader.frameLength(8);
        // little endian
        Assert.assertThat(buffer.get(0), Is.is(((byte) (8))));
        Assert.assertThat(buffer.get(1), Is.is(((byte) (0))));
        Assert.assertThat(buffer.get(2), Is.is(((byte) (0))));
        Assert.assertThat(buffer.get(3), Is.is(((byte) (0))));
        Assert.assertThat(buffer.get(4), Is.is(((byte) (1))));
        Assert.assertThat(buffer.get(5), Is.is(((byte) (192))));
        Assert.assertThat(buffer.get(6), Is.is(((byte) (HDR_TYPE_DATA))));
        Assert.assertThat(buffer.get(7), Is.is(((byte) (0))));
    }

    @Test
    public void shouldReadWhatIsWrittenToGenericHeaderFields() {
        encodeHeader.wrap(aBuff);
        encodeHeader.version(((short) (1)));
        encodeHeader.flags(((short) (0)));
        encodeHeader.headerType(HDR_TYPE_DATA);
        encodeHeader.frameLength(8);
        decodeHeader.wrap(aBuff);
        Assert.assertThat(decodeHeader.version(), Is.is(((short) (1))));
        Assert.assertThat(decodeHeader.headerType(), Is.is(HDR_TYPE_DATA));
        Assert.assertThat(decodeHeader.frameLength(), Is.is(8));
    }

    @Test
    public void shouldWriteAndReadMultipleFramesCorrectly() {
        encodeHeader.wrap(aBuff);
        encodeHeader.version(((short) (1)));
        encodeHeader.flags(((short) (0)));
        encodeHeader.headerType(HDR_TYPE_DATA);
        encodeHeader.frameLength(8);
        encodeHeader.wrap(aBuff, 8, ((aBuff.capacity()) - 8));
        encodeHeader.version(((short) (2)));
        encodeHeader.flags(((short) (1)));
        encodeHeader.headerType(HDR_TYPE_SM);
        encodeHeader.frameLength(8);
        decodeHeader.wrap(aBuff);
        Assert.assertThat(decodeHeader.version(), Is.is(((short) (1))));
        Assert.assertThat(decodeHeader.flags(), Is.is(((short) (0))));
        Assert.assertThat(decodeHeader.headerType(), Is.is(HDR_TYPE_DATA));
        Assert.assertThat(decodeHeader.frameLength(), Is.is(8));
        decodeHeader.wrap(aBuff, 8, ((aBuff.capacity()) - 8));
        Assert.assertThat(decodeHeader.version(), Is.is(((short) (2))));
        Assert.assertThat(decodeHeader.flags(), Is.is(((short) (1))));
        Assert.assertThat(decodeHeader.headerType(), Is.is(HDR_TYPE_SM));
        Assert.assertThat(decodeHeader.frameLength(), Is.is(8));
    }

    @Test
    public void shouldReadAndWriteDataHeaderCorrectly() {
        encodeDataHeader.wrap(aBuff);
        encodeDataHeader.version(((short) (1)));
        encodeDataHeader.flags(BEGIN_AND_END_FLAGS);
        encodeDataHeader.headerType(HDR_TYPE_DATA);
        encodeDataHeader.frameLength(HEADER_LENGTH);
        encodeDataHeader.sessionId(-559038737);
        encodeDataHeader.streamId(1144201745);
        encodeDataHeader.termId(-1719109786);
        decodeDataHeader.wrap(aBuff);
        Assert.assertThat(decodeDataHeader.version(), Is.is(((short) (1))));
        Assert.assertThat(decodeDataHeader.flags(), Is.is(BEGIN_AND_END_FLAGS));
        Assert.assertThat(decodeDataHeader.headerType(), Is.is(HDR_TYPE_DATA));
        Assert.assertThat(decodeDataHeader.frameLength(), Is.is(HEADER_LENGTH));
        Assert.assertThat(decodeDataHeader.sessionId(), Is.is(-559038737));
        Assert.assertThat(decodeDataHeader.streamId(), Is.is(1144201745));
        Assert.assertThat(decodeDataHeader.termId(), Is.is(-1719109786));
        Assert.assertThat(decodeDataHeader.dataOffset(), Is.is(HEADER_LENGTH));
    }

    @Test
    public void shouldEncodeAndDecodeNakCorrectly() {
        encodeNakHeader.wrap(aBuff);
        encodeNakHeader.version(((short) (1)));
        encodeNakHeader.flags(((byte) (0)));
        encodeNakHeader.headerType(HDR_TYPE_NAK);
        encodeNakHeader.frameLength(NakFlyweight.HEADER_LENGTH);
        encodeNakHeader.sessionId(-559038737);
        encodeNakHeader.streamId(1144201745);
        encodeNakHeader.termId(-1719109786);
        encodeNakHeader.termOffset(140084);
        encodeNakHeader.length(512);
        decodeNakHeader.wrap(aBuff);
        Assert.assertThat(decodeNakHeader.version(), Is.is(((short) (1))));
        Assert.assertThat(decodeNakHeader.flags(), Is.is(((short) (0))));
        Assert.assertThat(decodeNakHeader.headerType(), Is.is(HDR_TYPE_NAK));
        Assert.assertThat(decodeNakHeader.frameLength(), Is.is(NakFlyweight.HEADER_LENGTH));
        Assert.assertThat(decodeNakHeader.sessionId(), Is.is(-559038737));
        Assert.assertThat(decodeNakHeader.streamId(), Is.is(1144201745));
        Assert.assertThat(decodeNakHeader.termId(), Is.is(-1719109786));
        Assert.assertThat(decodeNakHeader.termOffset(), Is.is(140084));
        Assert.assertThat(decodeNakHeader.length(), Is.is(512));
    }

    @Test
    public void shouldEncodeAndDecodeChannelsCorrectly() {
        encodePublication.wrap(aBuff, 0);
        final String channel = "aeron:udp?endpoint=localhost:4000";
        encodePublication.channel(channel);
        decodePublication.wrap(aBuff, 0);
        Assert.assertThat(decodePublication.channel(), Is.is(channel));
    }
}

