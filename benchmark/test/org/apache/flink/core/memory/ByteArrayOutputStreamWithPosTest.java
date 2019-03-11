/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.core.memory;


import ConfigConstants.DEFAULT_CHARSET;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Tests for {@link ByteArrayOutputStreamWithPos}.
 */
public class ByteArrayOutputStreamWithPosTest {
    private static final int BUFFER_SIZE = 32;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ByteArrayOutputStreamWithPos stream;

    /**
     * Test setting position which is exactly the same with the buffer size.
     */
    @Test
    public void testSetPositionWhenBufferIsFull() throws Exception {
        stream.write(new byte[ByteArrayOutputStreamWithPosTest.BUFFER_SIZE]);
        // check whether the buffer is filled fully
        Assert.assertEquals(ByteArrayOutputStreamWithPosTest.BUFFER_SIZE, stream.getBuf().length);
        // check current position is the end of the buffer
        Assert.assertEquals(ByteArrayOutputStreamWithPosTest.BUFFER_SIZE, stream.getPosition());
        stream.setPosition(ByteArrayOutputStreamWithPosTest.BUFFER_SIZE);
        // confirm current position is at where we expect.
        Assert.assertEquals(ByteArrayOutputStreamWithPosTest.BUFFER_SIZE, stream.getPosition());
    }

    /**
     * Test setting negative position.
     */
    @Test
    public void testSetNegativePosition() throws Exception {
        stream.write(new byte[ByteArrayOutputStreamWithPosTest.BUFFER_SIZE]);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Position out of bounds");
        stream.setPosition((-1));
    }

    /**
     * Test setting position larger than buffer size.
     */
    @Test
    public void testSetPositionLargerThanBufferSize() throws Exception {
        // fully fill the buffer
        stream.write(new byte[ByteArrayOutputStreamWithPosTest.BUFFER_SIZE]);
        Assert.assertEquals(ByteArrayOutputStreamWithPosTest.BUFFER_SIZE, stream.getBuf().length);
        // expand the buffer by setting position beyond the buffer length
        stream.setPosition(((ByteArrayOutputStreamWithPosTest.BUFFER_SIZE) + 1));
        Assert.assertEquals(((ByteArrayOutputStreamWithPosTest.BUFFER_SIZE) * 2), stream.getBuf().length);
        Assert.assertEquals(((ByteArrayOutputStreamWithPosTest.BUFFER_SIZE) + 1), stream.getPosition());
    }

    /**
     * Test that toString returns a substring of the buffer with range(0, position).
     */
    @Test
    public void testToString() throws IOException {
        byte[] data = "1234567890".getBytes(DEFAULT_CHARSET);
        ByteArrayOutputStreamWithPos stream = new ByteArrayOutputStreamWithPos(data.length);
        stream.write(data);
        Assert.assertArrayEquals(data, stream.toString().getBytes(DEFAULT_CHARSET));
        for (int i = 0; i < (data.length); i++) {
            stream.setPosition(i);
            Assert.assertArrayEquals(Arrays.copyOf(data, i), stream.toString().getBytes(DEFAULT_CHARSET));
        }
        // validate that the stored bytes are still tracked properly even when expanding array
        stream.setPosition(((data.length) + 1));
        Assert.assertArrayEquals(Arrays.copyOf(data, ((data.length) + 1)), stream.toString().getBytes(DEFAULT_CHARSET));
    }
}

