/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.common.utils;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import org.junit.Assert;
import org.junit.Test;


public class SerializerUtilsTest {
    private SerializerUtils serializerUtils;

    private final float delta = 0;

    private final String[] strings = new String[]{ "1#", "2", "3" };

    private final int[] ints = new int[]{ 1, 2, 3 };

    private final float[] floats = new float[]{ 1.1F, 2, 3 };

    private final long[] longs = new long[]{ 3, 2, 1 };

    private byte[] stringsByte;

    private byte[] intsByte;

    private byte[] floatsByte;

    private byte[] longsByte;

    private ByteArrayOutputStream outStream;

    @Test
    public void testWriteInts() throws IOException {
        serializerUtils.writeInts(outStream, ints);
        byte[] actuals = outStream.toByteArray();
        Assert.assertArrayEquals(intsByte, actuals);
    }

    @Test
    public void testWriteFloats() throws IOException {
        serializerUtils.writeFloats(outStream, floats);
        byte[] actuals = outStream.toByteArray();
        Assert.assertArrayEquals(floatsByte, actuals);
    }

    @Test
    public void testChannelWritefloat() throws IOException {
        final int index = 0;
        WritableByteChannel channelOutput = Channels.newChannel(outStream);
        serializerUtils.writeFloat(channelOutput, floats[index]);
        ByteArrayInputStream inputstream = new ByteArrayInputStream(outStream.toByteArray());
        if (channelOutput != null) {
            channelOutput.close();
        }
        float expected = serializerUtils.readFloat(inputstream);
        float actuals = floats[index];
        Assert.assertEquals(expected, actuals, delta);
    }

    @Test
    public void testWriteLongs() throws IOException {
        serializerUtils.writeLongs(outStream, longs);
        byte[] actuals = outStream.toByteArray();
        Assert.assertArrayEquals(longsByte, actuals);
    }

    @Test
    public void testChannelWritelong() throws IOException {
        final int index = 0;
        WritableByteChannel channelOutput = Channels.newChannel(outStream);
        serializerUtils.writeLong(channelOutput, longs[index]);
        ByteArrayInputStream inputstream = new ByteArrayInputStream(outStream.toByteArray());
        channelOutput.close();
        inputstream.close();
        long expected = serializerUtils.readLong(inputstream);
        long actuals = longs[index];
        Assert.assertEquals(expected, actuals);
    }

    @Test
    public void testReadInts() throws IOException {
        ByteArrayInputStream inputstream = new ByteArrayInputStream(intsByte);
        int[] actuals = serializerUtils.readInts(inputstream);
        inputstream.close();
        Assert.assertArrayEquals(ints, actuals);
    }

    @Test
    public void testReadFloats() throws IOException {
        ByteArrayInputStream inputstream = new ByteArrayInputStream(floatsByte);
        float[] actuals = serializerUtils.readFloats(inputstream);
        inputstream.close();
        Assert.assertArrayEquals(floats, actuals, delta);
    }

    @Test
    public void testReadLongs() throws IOException {
        ByteArrayInputStream inputstream = new ByteArrayInputStream(longsByte);
        long[] actuals = serializerUtils.readLongs(inputstream);
        inputstream.close();
        Assert.assertArrayEquals(longs, actuals);
    }

    @Test
    public void testReadStrings() throws IOException {
        ByteArrayInputStream inputstream = new ByteArrayInputStream(stringsByte);
        String[] actuals = serializerUtils.readStrings(inputstream);
        inputstream.close();
        Assert.assertArrayEquals(strings, actuals);
    }

    @Test
    public void testChannelWriteString() throws IOException {
        final int index = 0;
        WritableByteChannel channelOutput = Channels.newChannel(outStream);
        serializerUtils.writeString(channelOutput, strings[index]);
        ByteArrayInputStream inputstream = new ByteArrayInputStream(outStream.toByteArray());
        channelOutput.close();
        inputstream.close();
        String expected = serializerUtils.readString(inputstream);
        String actuals = strings[index];
        Assert.assertEquals(expected, actuals);
    }

    @Test
    public void testByteBufferReadStrings() {
        ByteBuffer buffer = ByteBuffer.allocate(stringsByte.length);
        buffer.put(stringsByte);
        buffer.flip();
        String[] actuals = serializerUtils.readStrings(buffer);
        Assert.assertArrayEquals(strings, actuals);
    }
}

