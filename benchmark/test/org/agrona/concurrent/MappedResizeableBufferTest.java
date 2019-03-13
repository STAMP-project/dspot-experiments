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
package org.agrona.concurrent;


import java.nio.channels.FileChannel;
import org.agrona.IoUtil;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MappedResizeableBufferTest {
    private static final long SIZE = 2 * ((long) (Integer.MAX_VALUE));

    private static final int VALUE = 4;

    private static final String PATH = (IoUtil.tmpDirName()) + "/eg-buffer";

    private static FileChannel channel;

    private MappedResizeableBuffer buffer;

    @Test
    public void shouldWriteDataToBuffer() {
        buffer = new MappedResizeableBuffer(MappedResizeableBufferTest.channel, 0, 100);
        exchangeDataAt(50L);
    }

    @Test
    public void shouldResizeBufferToOver2GB() {
        buffer = new MappedResizeableBuffer(MappedResizeableBufferTest.channel, 0, 100);
        buffer.resize(MappedResizeableBufferTest.SIZE);
        exchangeDataAt(((MappedResizeableBufferTest.SIZE) - 4));
    }

    @Test
    public void shouldReadPreviousWrites() {
        buffer = new MappedResizeableBuffer(MappedResizeableBufferTest.channel, 0, 100);
        exchangeDataAt(50L);
        buffer.resize(MappedResizeableBufferTest.SIZE);
        Assert.assertEquals(MappedResizeableBufferTest.VALUE, buffer.getInt(50L));
    }

    @Test
    public void shouldReadBytesFromOtherBuffer() {
        buffer = new MappedResizeableBuffer(MappedResizeableBufferTest.channel, 0, MappedResizeableBufferTest.SIZE);
        exchangeDataAt(((MappedResizeableBufferTest.SIZE) - 4));
        buffer.close();
        buffer = new MappedResizeableBuffer(MappedResizeableBufferTest.channel, 0, MappedResizeableBufferTest.SIZE);
        Assert.assertEquals(MappedResizeableBufferTest.VALUE, buffer.getInt(((MappedResizeableBufferTest.SIZE) - 4)));
    }

    @Test
    public void shouldNotCloseChannelUponBufferClose() {
        buffer = new MappedResizeableBuffer(MappedResizeableBufferTest.channel, 0, MappedResizeableBufferTest.SIZE);
        buffer.close();
        Assert.assertTrue(MappedResizeableBufferTest.channel.isOpen());
        buffer = null;
    }

    @Test
    public void shouldPutBytesFromDirectBuffer() {
        buffer = new MappedResizeableBuffer(MappedResizeableBufferTest.channel, 0, 100);
        final long value = 6148914691236517205L;
        final UnsafeBuffer onHeapDirectBuffer = new UnsafeBuffer(new byte[24]);
        onHeapDirectBuffer.putLong(16, value);
        buffer.putBytes(24, onHeapDirectBuffer, 16, 8);
        Assert.assertThat(buffer.getLong(24), CoreMatchers.is(value));
        final UnsafeBuffer offHeapDirectBuffer = new UnsafeBuffer(buffer.addressOffset(), ((int) (buffer.capacity())));
        buffer.putBytes(96, offHeapDirectBuffer, 24, 4);
        Assert.assertThat(buffer.getInt(96), CoreMatchers.is(((int) (value))));
    }
}

