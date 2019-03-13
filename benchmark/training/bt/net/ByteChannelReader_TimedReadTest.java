/**
 * Copyright (c) 2016?2017 Andrei Tomashpolskiy and individual contributors.
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
package bt.net;


import bt.TestUtil;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.Arrays;
import java.util.function.Function;
import org.junit.Assert;
import org.junit.Test;


public class ByteChannelReader_TimedReadTest {
    private static final Duration receiveTimeout = Duration.ofMillis(10);

    private static final Duration waitBetweenReads = Duration.ofMillis(0);

    @Test
    public void testReader_timedRead_ExactAmount_SingleBlock() throws IOException {
        ReadByBlockChannel channel = ByteChannelReader_TimedReadTest.createChannelWithTestData();
        int[] limits = new int[]{ 10, 20, 30, 40, 50 };
        LimitingChannel limitedChannel = new LimitingChannel(channel, limits);
        ByteChannelReader reader = ByteChannelReader_TimedReadTest.testReader(limitedChannel);
        ByteBuffer buf = ByteBuffer.allocate(100);
        int read = reader.readExactly(50).read(buf);
        Assert.assertEquals(50, read);
        Assert.assertEquals(50, buf.position());
        byte[] expected = new byte[50];
        byte[] actual = new byte[50];
        buf.get(actual);
        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void testReader_timedRead_ExactAmount_MultipleBlocks() throws IOException {
        ReadByBlockChannel channel = ByteChannelReader_TimedReadTest.createChannelWithTestData();
        int[] limits = new int[]{ 10, 20, 100, 125, 160, 160, 230, 230, 300 };
        LimitingChannel limitedChannel = new LimitingChannel(channel, limits);
        ByteChannelReader reader = ByteChannelReader_TimedReadTest.testReader(limitedChannel);
        ByteBuffer buf = ByteBuffer.allocate(300);
        int read = reader.readExactly(300).read(buf);
        Assert.assertEquals(300, read);
        Assert.assertEquals(buf.limit(), buf.position());
        byte[] expected = new byte[300];
        Arrays.fill(expected, 100, 200, ((byte) (1)));
        Arrays.fill(expected, 200, 300, ((byte) (2)));
        byte[] actual = new byte[300];
        buf.flip();
        buf.get(actual);
        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void testReader_timedRead_InsufficientDataRead() throws IOException {
        ReadByBlockChannel channel = ByteChannelReader_TimedReadTest.createChannelWithTestData();
        int[] limits = new int[]{ 10, 20, 30, 40, 50 };
        LimitingChannel limitedChannel = new LimitingChannel(channel, limits);
        ByteChannelReader reader = ByteChannelReader_TimedReadTest.testReader(limitedChannel);
        ByteBuffer buf = ByteBuffer.allocate(100);
        TestUtil.assertExceptionWithMessage(( it) -> {
            try {
                return reader.readExactly(60).read(buf);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, "Less than 60 bytes received: 50");
    }

    @Test
    public void testReader_timedRead_ExcessiveDataRead() throws IOException {
        ReadByBlockChannel channel = ByteChannelReader_TimedReadTest.createChannelWithTestData();
        int[] limits = new int[]{ 10, 20, 40 };
        LimitingChannel limitedChannel = new LimitingChannel(channel, limits);
        ByteChannelReader reader = ByteChannelReader_TimedReadTest.testReader(limitedChannel);
        ByteBuffer buf = ByteBuffer.allocate(100);
        TestUtil.assertExceptionWithMessage(( it) -> {
            try {
                return reader.readExactly(30).read(buf);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, "More than 30 bytes received: 40");
    }

    @Test
    public void testReader_timedRead_EOF() throws IOException {
        ReadByBlockChannel channel = ByteChannelReader_TimedReadTest.createChannelWithTestData();
        int[] limits = new int[]{ 10, 20, 30, 40, 50 };
        LimitingChannel limitedChannel = LimitingChannel.withEOF(channel, limits);
        ByteChannelReader reader = ByteChannelReader_TimedReadTest.testReader(limitedChannel);
        ByteBuffer buf = ByteBuffer.allocate(100);
        TestUtil.assertExceptionWithMessage(( it) -> {
            try {
                return reader.readExactly(60).read(buf);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, "Received EOF, total bytes read: 50, expected: 60..60");
    }
}

