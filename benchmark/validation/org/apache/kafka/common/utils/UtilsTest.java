/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.common.utils;


import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.kafka.test.TestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class UtilsTest {
    @Test
    public void testGetHost() {
        Assert.assertEquals("127.0.0.1", Utils.getHost("127.0.0.1:8000"));
        Assert.assertEquals("mydomain.com", Utils.getHost("PLAINTEXT://mydomain.com:8080"));
        Assert.assertEquals("MyDomain.com", Utils.getHost("PLAINTEXT://MyDomain.com:8080"));
        Assert.assertEquals("My_Domain.com", Utils.getHost("PLAINTEXT://My_Domain.com:8080"));
        Assert.assertEquals("::1", Utils.getHost("[::1]:1234"));
        Assert.assertEquals("2001:db8:85a3:8d3:1319:8a2e:370:7348", Utils.getHost("PLAINTEXT://[2001:db8:85a3:8d3:1319:8a2e:370:7348]:5678"));
        Assert.assertEquals("2001:DB8:85A3:8D3:1319:8A2E:370:7348", Utils.getHost("PLAINTEXT://[2001:DB8:85A3:8D3:1319:8A2E:370:7348]:5678"));
        Assert.assertEquals("fe80::b1da:69ca:57f7:63d8%3", Utils.getHost("PLAINTEXT://[fe80::b1da:69ca:57f7:63d8%3]:5678"));
    }

    @Test
    public void testHostPattern() {
        Assert.assertTrue(Utils.validHostPattern("127.0.0.1"));
        Assert.assertTrue(Utils.validHostPattern("mydomain.com"));
        Assert.assertTrue(Utils.validHostPattern("MyDomain.com"));
        Assert.assertTrue(Utils.validHostPattern("My_Domain.com"));
        Assert.assertTrue(Utils.validHostPattern("::1"));
        Assert.assertTrue(Utils.validHostPattern("2001:db8:85a3:8d3:1319:8a2e:370"));
    }

    @Test
    public void testGetPort() {
        Assert.assertEquals(8000, Utils.getPort("127.0.0.1:8000").intValue());
        Assert.assertEquals(8080, Utils.getPort("mydomain.com:8080").intValue());
        Assert.assertEquals(8080, Utils.getPort("MyDomain.com:8080").intValue());
        Assert.assertEquals(1234, Utils.getPort("[::1]:1234").intValue());
        Assert.assertEquals(5678, Utils.getPort("[2001:db8:85a3:8d3:1319:8a2e:370:7348]:5678").intValue());
        Assert.assertEquals(5678, Utils.getPort("[2001:DB8:85A3:8D3:1319:8A2E:370:7348]:5678").intValue());
        Assert.assertEquals(5678, Utils.getPort("[fe80::b1da:69ca:57f7:63d8%3]:5678").intValue());
    }

    @Test
    public void testFormatAddress() {
        Assert.assertEquals("127.0.0.1:8000", Utils.formatAddress("127.0.0.1", 8000));
        Assert.assertEquals("mydomain.com:8080", Utils.formatAddress("mydomain.com", 8080));
        Assert.assertEquals("[::1]:1234", Utils.formatAddress("::1", 1234));
        Assert.assertEquals("[2001:db8:85a3:8d3:1319:8a2e:370:7348]:5678", Utils.formatAddress("2001:db8:85a3:8d3:1319:8a2e:370:7348", 5678));
    }

    @Test
    public void testFormatBytes() {
        Assert.assertEquals("-1", Utils.formatBytes((-1)));
        Assert.assertEquals("1023 B", Utils.formatBytes(1023));
        Assert.assertEquals("1 KB", Utils.formatBytes(1024));
        Assert.assertEquals("1024 KB", Utils.formatBytes(((1024 * 1024) - 1)));
        Assert.assertEquals("1 MB", Utils.formatBytes((1024 * 1024)));
        Assert.assertEquals("1.1 MB", Utils.formatBytes(((long) ((1.1 * 1024) * 1024))));
        Assert.assertEquals("10 MB", Utils.formatBytes(((10 * 1024) * 1024)));
    }

    @Test
    public void testJoin() {
        Assert.assertEquals("", Utils.join(Collections.emptyList(), ","));
        Assert.assertEquals("1", Utils.join(Arrays.asList("1"), ","));
        Assert.assertEquals("1,2,3", Utils.join(Arrays.asList(1, 2, 3), ","));
    }

    @Test
    public void testAbs() {
        Assert.assertEquals(0, Utils.abs(Integer.MIN_VALUE));
        Assert.assertEquals(10, Utils.abs((-10)));
        Assert.assertEquals(10, Utils.abs(10));
        Assert.assertEquals(0, Utils.abs(0));
        Assert.assertEquals(1, Utils.abs((-1)));
    }

    @Test
    public void writeToBuffer() throws IOException {
        byte[] input = new byte[]{ 0, 1, 2, 3, 4, 5 };
        ByteBuffer source = ByteBuffer.wrap(input);
        doTestWriteToByteBuffer(source, ByteBuffer.allocate(input.length));
        doTestWriteToByteBuffer(source, ByteBuffer.allocateDirect(input.length));
        Assert.assertEquals(0, source.position());
        source.position(2);
        doTestWriteToByteBuffer(source, ByteBuffer.allocate(input.length));
        doTestWriteToByteBuffer(source, ByteBuffer.allocateDirect(input.length));
    }

    @Test
    public void toArray() {
        byte[] input = new byte[]{ 0, 1, 2, 3, 4 };
        ByteBuffer buffer = ByteBuffer.wrap(input);
        Assert.assertArrayEquals(input, Utils.toArray(buffer));
        Assert.assertEquals(0, buffer.position());
        Assert.assertArrayEquals(new byte[]{ 1, 2 }, Utils.toArray(buffer, 1, 2));
        Assert.assertEquals(0, buffer.position());
        buffer.position(2);
        Assert.assertArrayEquals(new byte[]{ 2, 3, 4 }, Utils.toArray(buffer));
        Assert.assertEquals(2, buffer.position());
    }

    @Test
    public void toArrayDirectByteBuffer() {
        byte[] input = new byte[]{ 0, 1, 2, 3, 4 };
        ByteBuffer buffer = ByteBuffer.allocateDirect(5);
        buffer.put(input);
        buffer.rewind();
        Assert.assertArrayEquals(input, Utils.toArray(buffer));
        Assert.assertEquals(0, buffer.position());
        Assert.assertArrayEquals(new byte[]{ 1, 2 }, Utils.toArray(buffer, 1, 2));
        Assert.assertEquals(0, buffer.position());
        buffer.position(2);
        Assert.assertArrayEquals(new byte[]{ 2, 3, 4 }, Utils.toArray(buffer));
        Assert.assertEquals(2, buffer.position());
    }

    @Test
    public void utf8ByteArraySerde() {
        String utf8String = "A\u00ea\u00f1\u00fcC";
        byte[] utf8Bytes = utf8String.getBytes(StandardCharsets.UTF_8);
        Assert.assertArrayEquals(utf8Bytes, Utils.utf8(utf8String));
        Assert.assertEquals(utf8Bytes.length, Utils.utf8Length(utf8String));
        Assert.assertEquals(utf8String, Utils.utf8(utf8Bytes));
    }

    @Test
    public void utf8ByteBufferSerde() {
        doTestUtf8ByteBuffer(ByteBuffer.allocate(20));
        doTestUtf8ByteBuffer(ByteBuffer.allocateDirect(20));
    }

    @Test
    public void testReadBytes() {
        byte[] myvar = "Any String you want".getBytes();
        ByteBuffer buffer = ByteBuffer.allocate(myvar.length);
        buffer.put(myvar);
        buffer.rewind();
        this.subTest(buffer);
        // test readonly buffer, different path
        buffer = ByteBuffer.wrap(myvar).asReadOnlyBuffer();
        this.subTest(buffer);
    }

    @Test
    public void testMin() {
        Assert.assertEquals(1, Utils.min(1));
        Assert.assertEquals(1, Utils.min(1, 2, 3));
        Assert.assertEquals(1, Utils.min(2, 1, 3));
        Assert.assertEquals(1, Utils.min(2, 3, 1));
    }

    @Test
    public void testCloseAll() {
        UtilsTest.TestCloseable[] closeablesWithoutException = UtilsTest.TestCloseable.createCloseables(false, false, false);
        try {
            Utils.closeAll(closeablesWithoutException);
            UtilsTest.TestCloseable.checkClosed(closeablesWithoutException);
        } catch (IOException e) {
            Assert.fail(("Unexpected exception: " + e));
        }
        UtilsTest.TestCloseable[] closeablesWithException = UtilsTest.TestCloseable.createCloseables(true, true, true);
        try {
            Utils.closeAll(closeablesWithException);
            Assert.fail("Expected exception not thrown");
        } catch (IOException e) {
            UtilsTest.TestCloseable.checkClosed(closeablesWithException);
            UtilsTest.TestCloseable.checkException(e, closeablesWithException);
        }
        UtilsTest.TestCloseable[] singleExceptionCloseables = UtilsTest.TestCloseable.createCloseables(false, true, false);
        try {
            Utils.closeAll(singleExceptionCloseables);
            Assert.fail("Expected exception not thrown");
        } catch (IOException e) {
            UtilsTest.TestCloseable.checkClosed(singleExceptionCloseables);
            UtilsTest.TestCloseable.checkException(e, singleExceptionCloseables[1]);
        }
        UtilsTest.TestCloseable[] mixedCloseables = UtilsTest.TestCloseable.createCloseables(false, true, false, true, true);
        try {
            Utils.closeAll(mixedCloseables);
            Assert.fail("Expected exception not thrown");
        } catch (IOException e) {
            UtilsTest.TestCloseable.checkClosed(mixedCloseables);
            UtilsTest.TestCloseable.checkException(e, mixedCloseables[1], mixedCloseables[3], mixedCloseables[4]);
        }
    }

    @Test
    public void testReadFullyOrFailWithRealFile() throws IOException {
        try (FileChannel channel = FileChannel.open(TestUtils.tempFile().toPath(), StandardOpenOption.READ, StandardOpenOption.WRITE)) {
            // prepare channel
            String msg = "hello, world";
            channel.write(ByteBuffer.wrap(msg.getBytes()), 0);
            channel.force(true);
            Assert.assertEquals("Message should be written to the file channel", channel.size(), msg.length());
            ByteBuffer perfectBuffer = ByteBuffer.allocate(msg.length());
            ByteBuffer smallBuffer = ByteBuffer.allocate(5);
            ByteBuffer largeBuffer = ByteBuffer.allocate(((msg.length()) + 1));
            // Scenario 1: test reading into a perfectly-sized buffer
            Utils.readFullyOrFail(channel, perfectBuffer, 0, "perfect");
            Assert.assertFalse("Buffer should be filled up", perfectBuffer.hasRemaining());
            Assert.assertEquals("Buffer should be populated correctly", msg, new String(perfectBuffer.array()));
            // Scenario 2: test reading into a smaller buffer
            Utils.readFullyOrFail(channel, smallBuffer, 0, "small");
            Assert.assertFalse("Buffer should be filled", smallBuffer.hasRemaining());
            Assert.assertEquals("Buffer should be populated correctly", "hello", new String(smallBuffer.array()));
            // Scenario 3: test reading starting from a non-zero position
            smallBuffer.clear();
            Utils.readFullyOrFail(channel, smallBuffer, 7, "small");
            Assert.assertFalse("Buffer should be filled", smallBuffer.hasRemaining());
            Assert.assertEquals("Buffer should be populated correctly", "world", new String(smallBuffer.array()));
            // Scenario 4: test end of stream is reached before buffer is filled up
            try {
                Utils.readFullyOrFail(channel, largeBuffer, 0, "large");
                Assert.fail("Expected EOFException to be raised");
            } catch (EOFException e) {
                // expected
            }
        }
    }

    /**
     * Tests that `readFullyOrFail` behaves correctly if multiple `FileChannel.read` operations are required to fill
     * the destination buffer.
     */
    @Test
    public void testReadFullyOrFailWithPartialFileChannelReads() throws IOException {
        FileChannel channelMock = Mockito.mock(FileChannel.class);
        final int bufferSize = 100;
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        String expectedBufferContent = fileChannelMockExpectReadWithRandomBytes(channelMock, bufferSize);
        Utils.readFullyOrFail(channelMock, buffer, 0L, "test");
        Assert.assertEquals("The buffer should be populated correctly", expectedBufferContent, new String(buffer.array()));
        Assert.assertFalse("The buffer should be filled", buffer.hasRemaining());
        Mockito.verify(channelMock, Mockito.atLeastOnce()).read(ArgumentMatchers.any(), ArgumentMatchers.anyLong());
    }

    /**
     * Tests that `readFullyOrFail` behaves correctly if multiple `FileChannel.read` operations are required to fill
     * the destination buffer.
     */
    @Test
    public void testReadFullyWithPartialFileChannelReads() throws IOException {
        FileChannel channelMock = Mockito.mock(FileChannel.class);
        final int bufferSize = 100;
        String expectedBufferContent = fileChannelMockExpectReadWithRandomBytes(channelMock, bufferSize);
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        Utils.readFully(channelMock, buffer, 0L);
        Assert.assertEquals("The buffer should be populated correctly.", expectedBufferContent, new String(buffer.array()));
        Assert.assertFalse("The buffer should be filled", buffer.hasRemaining());
        Mockito.verify(channelMock, Mockito.atLeastOnce()).read(ArgumentMatchers.any(), ArgumentMatchers.anyLong());
    }

    @Test
    public void testReadFullyIfEofIsReached() throws IOException {
        final FileChannel channelMock = Mockito.mock(FileChannel.class);
        final int bufferSize = 100;
        final String fileChannelContent = "abcdefghkl";
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        Mockito.when(channelMock.read(ArgumentMatchers.any(), ArgumentMatchers.anyLong())).then(( invocation) -> {
            ByteBuffer bufferArg = invocation.getArgument(0);
            bufferArg.put(fileChannelContent.getBytes());
            return -1;
        });
        Utils.readFully(channelMock, buffer, 0L);
        Assert.assertEquals("abcdefghkl", new String(buffer.array(), 0, buffer.position()));
        Assert.assertEquals(fileChannelContent.length(), buffer.position());
        Assert.assertTrue(buffer.hasRemaining());
        Mockito.verify(channelMock, Mockito.atLeastOnce()).read(ArgumentMatchers.any(), ArgumentMatchers.anyLong());
    }

    private static class TestCloseable implements Closeable {
        private final int id;

        private final IOException closeException;

        private boolean closed;

        TestCloseable(int id, boolean exceptionOnClose) {
            this.id = id;
            this.closeException = (exceptionOnClose) ? new IOException(("Test close exception " + id)) : null;
        }

        @Override
        public void close() throws IOException {
            closed = true;
            if ((closeException) != null)
                throw closeException;

        }

        static UtilsTest.TestCloseable[] createCloseables(boolean... exceptionOnClose) {
            UtilsTest.TestCloseable[] closeables = new UtilsTest.TestCloseable[exceptionOnClose.length];
            for (int i = 0; i < (closeables.length); i++)
                closeables[i] = new UtilsTest.TestCloseable(i, exceptionOnClose[i]);

            return closeables;
        }

        static void checkClosed(UtilsTest.TestCloseable... closeables) {
            for (UtilsTest.TestCloseable closeable : closeables)
                Assert.assertTrue(("Close not invoked for " + (closeable.id)), closeable.closed);

        }

        static void checkException(IOException e, UtilsTest.TestCloseable... closeablesWithException) {
            Assert.assertEquals(closeablesWithException[0].closeException, e);
            Throwable[] suppressed = e.getSuppressed();
            Assert.assertEquals(((closeablesWithException.length) - 1), suppressed.length);
            for (int i = 1; i < (closeablesWithException.length); i++)
                Assert.assertEquals(closeablesWithException[i].closeException, suppressed[(i - 1)]);

        }
    }

    @Test(timeout = 120000)
    public void testRecursiveDelete() throws IOException {
        Utils.delete(null);// delete of null does nothing.

        // Test that deleting a temporary file works.
        File tempFile = TestUtils.tempFile();
        Utils.delete(tempFile);
        Assert.assertFalse(Files.exists(tempFile.toPath()));
        // Test recursive deletes
        File tempDir = TestUtils.tempDirectory();
        File tempDir2 = TestUtils.tempDirectory(tempDir.toPath(), "a");
        TestUtils.tempDirectory(tempDir.toPath(), "b");
        TestUtils.tempDirectory(tempDir2.toPath(), "c");
        Utils.delete(tempDir);
        Assert.assertFalse(Files.exists(tempDir.toPath()));
        Assert.assertFalse(Files.exists(tempDir2.toPath()));
        // Test that deleting a non-existent directory hierarchy works.
        Utils.delete(tempDir);
        Assert.assertFalse(Files.exists(tempDir.toPath()));
    }

    @Test
    public void testConvertTo32BitField() {
        Set<Byte> bytes = Utils.mkSet(((byte) (0)), ((byte) (1)), ((byte) (5)), ((byte) (10)), ((byte) (31)));
        int bitField = Utils.to32BitField(bytes);
        Assert.assertEquals(bytes, Utils.from32BitField(bitField));
        bytes = new HashSet<>();
        bitField = Utils.to32BitField(bytes);
        Assert.assertEquals(bytes, Utils.from32BitField(bitField));
        bytes = Utils.mkSet(((byte) (0)), ((byte) (11)), ((byte) (32)));
        try {
            Utils.to32BitField(bytes);
            Assert.fail("Expected exception not thrown");
        } catch (IllegalArgumentException e) {
        }
    }
}

