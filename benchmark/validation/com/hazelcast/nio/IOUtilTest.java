/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
package com.hazelcast.nio;


import IOUtil.PRIMITIVE_TYPE_BOOLEAN;
import IOUtil.PRIMITIVE_TYPE_BYTE;
import IOUtil.PRIMITIVE_TYPE_DOUBLE;
import IOUtil.PRIMITIVE_TYPE_FLOAT;
import IOUtil.PRIMITIVE_TYPE_INTEGER;
import IOUtil.PRIMITIVE_TYPE_LONG;
import IOUtil.PRIMITIVE_TYPE_SHORT;
import IOUtil.PRIMITIVE_TYPE_UTF;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.serialization.impl.DefaultSerializationServiceBuilder;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category(QuickTest.class)
public class IOUtilTest extends HazelcastTestSupport {
    private static final int SIZE = 3;

    private static final byte[] NON_EMPTY_BYTE_ARRAY = new byte[100];

    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    private static final byte[] STREAM_INPUT = new byte[]{ 1, 2, 3, 4 };

    @Rule
    public TestName testName = new TestName();

    private final InternalSerializationService serializationService = new DefaultSerializationServiceBuilder().build();

    private final List<File> files = new ArrayList<File>();

    @Test
    public void testConstructor() {
        HazelcastTestSupport.assertUtilityConstructor(IOUtil.class);
    }

    @Test
    public void testWriteAndReadByteArray() throws Exception {
        byte[] bytes = new byte[IOUtilTest.SIZE];
        bytes[0] = (IOUtilTest.SIZE) - 1;
        bytes[1] = 23;
        bytes[2] = 42;
        byte[] output = writeAndReadByteArray(bytes);
        Assert.assertNotNull(output);
        Assert.assertEquals(((IOUtilTest.SIZE) - 1), output[0]);
        Assert.assertEquals(23, output[1]);
        Assert.assertEquals(42, output[2]);
    }

    @Test
    public void testWriteAndReadByteArray_withNull() throws Exception {
        byte[] output = writeAndReadByteArray(null);
        Assert.assertNull(output);
    }

    @Test
    public void testWriteAndReadObject() throws Exception {
        String expected = "test input";
        String actual = ((String) (writeAndReadObject(expected)));
        Assert.assertNotNull(actual);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testWriteAndReadObject_withData() throws Exception {
        Data expected = serializationService.toData("test input");
        Data actual = ((Data) (writeAndReadObject(expected)));
        Assert.assertNotNull(actual);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testReadFullyOrNothing() throws Exception {
        InputStream in = new ByteArrayInputStream(IOUtilTest.STREAM_INPUT);
        byte[] buffer = new byte[4];
        boolean result = IOUtil.readFullyOrNothing(in, buffer);
        Assert.assertTrue(result);
        for (int i = 0; i < (buffer.length); i++) {
            Assert.assertEquals(buffer[i], IOUtilTest.STREAM_INPUT[i]);
        }
    }

    @Test
    public void testReadFullyOrNothing_whenThereIsNoData_thenReturnFalse() throws Exception {
        InputStream in = new ByteArrayInputStream(new byte[0]);
        byte[] buffer = new byte[4];
        boolean result = IOUtil.readFullyOrNothing(in, buffer);
        Assert.assertFalse(result);
    }

    @Test(expected = EOFException.class)
    public void testReadFullyOrNothing_whenThereIsNotEnoughData_thenThrowException() throws Exception {
        InputStream in = new ByteArrayInputStream(IOUtilTest.STREAM_INPUT);
        byte[] buffer = new byte[8];
        IOUtil.readFullyOrNothing(in, buffer);
    }

    @Test
    public void testReadFully() throws Exception {
        InputStream in = new ByteArrayInputStream(IOUtilTest.STREAM_INPUT);
        byte[] buffer = new byte[4];
        IOUtil.readFully(in, buffer);
        for (int i = 0; i < (buffer.length); i++) {
            Assert.assertEquals(buffer[i], IOUtilTest.STREAM_INPUT[i]);
        }
    }

    @Test(expected = EOFException.class)
    public void testReadFully_whenThereIsNoData_thenThrowException() throws Exception {
        InputStream in = new ByteArrayInputStream(new byte[0]);
        byte[] buffer = new byte[4];
        IOUtil.readFully(in, buffer);
    }

    @Test(expected = EOFException.class)
    public void testReadFully_whenThereIsNotEnoughData_thenThrowException() throws Exception {
        InputStream in = new ByteArrayInputStream(IOUtilTest.STREAM_INPUT);
        byte[] buffer = new byte[8];
        IOUtil.readFully(in, buffer);
    }

    @Test
    public void testNewOutputStream_shouldWriteWholeByteBuffer() throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[IOUtilTest.SIZE]);
        OutputStream outputStream = IOUtil.newOutputStream(buffer);
        Assert.assertEquals(IOUtilTest.SIZE, buffer.remaining());
        outputStream.write(new byte[IOUtilTest.SIZE]);
        Assert.assertEquals(0, buffer.remaining());
    }

    @Test
    public void testNewOutputStream_shouldWriteSingleByte() throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[IOUtilTest.SIZE]);
        OutputStream outputStream = IOUtil.newOutputStream(buffer);
        Assert.assertEquals(IOUtilTest.SIZE, buffer.remaining());
        outputStream.write(23);
        Assert.assertEquals(((IOUtilTest.SIZE) - 1), buffer.remaining());
    }

    @Test
    public void testNewOutputStream_shouldWriteInChunks() throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[IOUtilTest.SIZE]);
        OutputStream outputStream = IOUtil.newOutputStream(buffer);
        Assert.assertEquals(IOUtilTest.SIZE, buffer.remaining());
        outputStream.write(new byte[1], 0, 1);
        outputStream.write(new byte[(IOUtilTest.SIZE) - 1], 0, ((IOUtilTest.SIZE) - 1));
        Assert.assertEquals(0, buffer.remaining());
    }

    @Test(expected = BufferOverflowException.class)
    public void testNewOutputStream_shouldThrowWhenTryingToWriteToEmptyByteBuffer() throws Exception {
        ByteBuffer empty = ByteBuffer.wrap(IOUtilTest.EMPTY_BYTE_ARRAY);
        OutputStream outputStream = IOUtil.newOutputStream(empty);
        outputStream.write(23);
    }

    @Test
    public void testNewInputStream_shouldReturnMinusOneWhenEmptyByteBufferProvidedAndReadingOneByte() throws Exception {
        ByteBuffer empty = ByteBuffer.wrap(IOUtilTest.EMPTY_BYTE_ARRAY);
        InputStream inputStream = IOUtil.newInputStream(empty);
        int read = inputStream.read();
        Assert.assertEquals((-1), read);
    }

    @Test
    public void testNewInputStream_shouldReadWholeByteBuffer() throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[IOUtilTest.SIZE]);
        InputStream inputStream = IOUtil.newInputStream(buffer);
        int read = inputStream.read(new byte[IOUtilTest.SIZE]);
        Assert.assertEquals(IOUtilTest.SIZE, read);
    }

    @Test
    public void testNewInputStream_shouldAllowReadingByteBufferInChunks() throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[IOUtilTest.SIZE]);
        InputStream inputStream = IOUtil.newInputStream(buffer);
        int firstRead = inputStream.read(new byte[1]);
        int secondRead = inputStream.read(new byte[(IOUtilTest.SIZE) - 1]);
        Assert.assertEquals(1, firstRead);
        Assert.assertEquals(((IOUtilTest.SIZE) - 1), secondRead);
    }

    @Test
    public void testNewInputStream_shouldReturnMinusOneWhenNothingRemainingInByteBuffer() throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[IOUtilTest.SIZE]);
        InputStream inputStream = IOUtil.newInputStream(buffer);
        int firstRead = inputStream.read(new byte[IOUtilTest.SIZE]);
        int secondRead = inputStream.read();
        Assert.assertEquals(IOUtilTest.SIZE, firstRead);
        Assert.assertEquals((-1), secondRead);
    }

    @Test
    public void testNewInputStream_shouldReturnMinusOneWhenEmptyByteBufferProvidedAndReadingSeveralBytes() throws Exception {
        ByteBuffer empty = ByteBuffer.wrap(IOUtilTest.EMPTY_BYTE_ARRAY);
        InputStream inputStream = IOUtil.newInputStream(empty);
        int read = inputStream.read(IOUtilTest.NON_EMPTY_BYTE_ARRAY);
        Assert.assertEquals((-1), read);
    }

    @Test(expected = EOFException.class)
    public void testNewInputStream_shouldThrowWhenTryingToReadFullyFromEmptyByteBuffer() throws Exception {
        ByteBuffer empty = ByteBuffer.wrap(IOUtilTest.EMPTY_BYTE_ARRAY);
        DataInputStream inputStream = new DataInputStream(IOUtil.newInputStream(empty));
        inputStream.readFully(IOUtilTest.NON_EMPTY_BYTE_ARRAY);
    }

    @Test(expected = EOFException.class)
    public void testNewInputStream_shouldThrowWhenByteBufferExhaustedAndTryingToReadFully() throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[IOUtilTest.SIZE]);
        DataInputStream inputStream = new DataInputStream(IOUtil.newInputStream(buffer));
        inputStream.readFully(new byte[IOUtilTest.SIZE]);
        inputStream.readFully(IOUtilTest.NON_EMPTY_BYTE_ARRAY);
    }

    @Test
    public void testCompressAndDecompress() {
        String expected = "But I must explain to you how all this mistaken idea of denouncing pleasure and praising pain was born" + (" and I will give you a complete account of the system, and expound the actual teachings of the great explorer" + " of the truth, the master-builder of human happiness.");
        byte[] compressed = IOUtil.compress(expected.getBytes());
        byte[] decompressed = IOUtil.decompress(compressed);
        Assert.assertEquals(expected, new String(decompressed));
    }

    @Test
    public void testCompressAndDecompress_withEmptyInput() {
        byte[] compressed = IOUtil.compress(IOUtilTest.EMPTY_BYTE_ARRAY);
        byte[] decompressed = IOUtil.decompress(compressed);
        Assert.assertArrayEquals(IOUtilTest.EMPTY_BYTE_ARRAY, decompressed);
    }

    @Test
    public void testCompressAndDecompress_withSingleByte() {
        byte[] input = new byte[]{ 111 };
        byte[] compressed = IOUtil.compress(input);
        byte[] decompressed = IOUtil.decompress(compressed);
        Assert.assertArrayEquals(input, decompressed);
    }

    @Test
    public void testCloseResource() throws Exception {
        Closeable closeable = Mockito.mock(Closeable.class);
        IOUtil.closeResource(closeable);
        Mockito.verify(closeable).close();
        Mockito.verifyNoMoreInteractions(closeable);
    }

    @Test
    public void testCloseResource_withException() throws Exception {
        Closeable closeable = Mockito.mock(Closeable.class);
        Mockito.doThrow(new IOException("expected")).when(closeable).close();
        IOUtil.closeResource(closeable);
        Mockito.verify(closeable).close();
        Mockito.verifyNoMoreInteractions(closeable);
    }

    @Test
    public void testCloseResource_withNull() {
        IOUtil.closeResource(null);
    }

    @Test
    public void testTouch() {
        File file = newFile("touchMe");
        Assert.assertFalse("file should not exist yet", file.exists());
        IOUtil.touch(file);
        Assert.assertTrue("file should exist now", file.exists());
    }

    @Test(expected = HazelcastException.class)
    public void testTouch_failsWhenLastModifiedCannotBeSet() {
        File file = Mockito.spy(newFile("touchMe"));
        Mockito.when(file.setLastModified(ArgumentMatchers.anyLong())).thenReturn(false);
        IOUtil.touch(file);
    }

    @Test
    public void testCopy_withRecursiveDirectory() {
        File parentDir = newFile("parent");
        Assert.assertFalse("parentDir should not exist yet", parentDir.exists());
        Assert.assertTrue("parentDir should have been created", parentDir.mkdir());
        File childDir = newFile(parentDir, "child");
        Assert.assertFalse("childDir should not exist yet", childDir.exists());
        Assert.assertTrue("childDir folder should have been created", childDir.mkdir());
        File parentFile = newFile(parentDir, "parentFile");
        IOUtilTest.writeTo(parentFile, "parentContent");
        File childFile = newFile(childDir, "childFile");
        IOUtilTest.writeTo(childFile, "childContent");
        File target = newFile("target");
        Assert.assertFalse("target should not exist yet", target.exists());
        IOUtil.copy(parentDir, target);
        Assert.assertTrue("target should exist now", target.exists());
        IOUtilTest.assertEqualFiles(parentDir, new File(target, parentDir.getName()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCopy_failsWhenSourceNotExist() {
        IOUtil.copy(newFile("nonExistent"), newFile("target"));
    }

    @Test(expected = HazelcastException.class)
    public void testCopy_failsWhenSourceCannotBeListed() {
        File source = Mockito.mock(File.class);
        Mockito.when(source.exists()).thenReturn(true);
        Mockito.when(source.isDirectory()).thenReturn(true);
        Mockito.when(source.listFiles()).thenReturn(null);
        Mockito.when(source.getName()).thenReturn("dummy");
        File target = newFile("dest");
        Assert.assertFalse("Target folder should not exist yet", target.exists());
        Assert.assertTrue("Target folder should have been created", target.mkdir());
        IOUtil.copy(source, target);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCopy_failsWhenSourceIsDirAndTargetIsFile() throws Exception {
        File source = newFile("dir1");
        Assert.assertFalse("Source folder should not exist yet", source.exists());
        Assert.assertTrue("Source folder should have been created", source.mkdir());
        File target = newFile("file1");
        Assert.assertFalse("Target file should not exist yet", target.exists());
        Assert.assertTrue("Target file should have been created successfully", target.createNewFile());
        IOUtil.copy(source, target);
        Assert.fail("Expected a IllegalArgumentException thrown by copy()");
    }

    @Test
    public void testCopy_withInputStream() throws Exception {
        InputStream inputStream = null;
        try {
            File source = createFile("source");
            File target = createFile("target");
            IOUtilTest.writeTo(source, "test content");
            inputStream = new FileInputStream(source);
            IOUtil.copy(inputStream, target);
            Assert.assertTrue("source and target should have the same content", IOUtilTest.isEqualsContents(source, target));
        } finally {
            IOUtil.closeResource(inputStream);
        }
    }

    @Test(expected = HazelcastException.class)
    public void testCopy_withInputStream_failsWhenTargetNotExist() {
        InputStream source = Mockito.mock(InputStream.class);
        File target = Mockito.mock(File.class);
        Mockito.when(target.exists()).thenReturn(false);
        IOUtil.copy(source, target);
    }

    @Test(expected = HazelcastException.class)
    public void testCopy_withInputStream_failsWhenSourceCannotBeRead() throws Exception {
        InputStream source = Mockito.mock(InputStream.class);
        Mockito.when(source.read(ArgumentMatchers.any(byte[].class))).thenThrow(new IOException("expected"));
        File target = createFile("target");
        IOUtil.copy(source, target);
    }

    @Test(expected = HazelcastException.class)
    public void testCopyFile_failsWhenTargetDoesntExistAndCannotBeCreated() throws Exception {
        File source = newFile("newFile");
        Assert.assertFalse("Source file should not exist yet", source.exists());
        Assert.assertTrue("Source file should have been created successfully", source.createNewFile());
        File target = Mockito.mock(File.class);
        Mockito.when(target.exists()).thenReturn(false);
        Mockito.when(target.mkdirs()).thenReturn(false);
        IOUtil.copyFile(source, target, (-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCopyFile_failsWhenSourceDoesntExist() {
        File source = newFile("nonExistent");
        File target = newFile("target");
        IOUtil.copyFile(source, target, (-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCopyFile_failsWhenSourceIsNotAFile() {
        File source = newFile("source");
        Assert.assertFalse("Source folder should not exist yet", source.exists());
        Assert.assertTrue("Source folder should have been created", source.mkdir());
        File target = newFile("target");
        IOUtil.copyFile(source, target, (-1));
    }

    @Test
    public void testDelete() {
        File file = createFile("file");
        IOUtil.delete(file);
        Assert.assertFalse("file should be deleted", file.exists());
    }

    @Test
    public void testDelete_shouldDoNothingWithNonExistentFile() {
        File file = newFile("notFound");
        IOUtil.delete(file);
    }

    @Test
    public void testDelete_shouldDeleteDirectoryRecursively() {
        File parentDir = createDirectory("parent");
        File file1 = createFile(parentDir, "file1");
        File file2 = createFile(parentDir, "file2");
        File childDir = createDirectory(parentDir, "child");
        File childFile1 = createFile(childDir, "childFile1");
        File childFile2 = createFile(childDir, "childFile2");
        IOUtil.delete(parentDir);
        Assert.assertFalse("parentDir should be deleted", parentDir.exists());
        Assert.assertFalse("file1 should be deleted", file1.exists());
        Assert.assertFalse("file2 should be deleted", file2.exists());
        Assert.assertFalse("childDir should be deleted", childDir.exists());
        Assert.assertFalse("childFile1 should be deleted", childFile1.exists());
        Assert.assertFalse("childFile2 should be deleted", childFile2.exists());
    }

    @Test(expected = HazelcastException.class)
    public void testDelete_shouldThrowIfFileCouldNotBeDeleted() {
        File file = Mockito.mock(File.class);
        Mockito.when(file.exists()).thenReturn(true);
        Mockito.when(file.delete()).thenReturn(false);
        IOUtil.delete(file);
    }

    @Test
    public void testDeleteQuietly() {
        File file = createFile("file");
        IOUtil.deleteQuietly(file);
        Assert.assertFalse("file should be deleted", file.exists());
    }

    @Test
    public void testDeleteQuietly_shouldDoNothingWithNonExistentFile() {
        File file = newFile("notFound");
        IOUtil.deleteQuietly(file);
    }

    @Test
    public void testDeleteQuietly_shouldDeleteDirectoryRecursively() {
        File parentDir = createDirectory("parent");
        File file1 = createFile(parentDir, "file1");
        File file2 = createFile(parentDir, "file2");
        File childDir = createDirectory(parentDir, "child");
        File childFile1 = createFile(childDir, "childFile1");
        File childFile2 = createFile(childDir, "childFile2");
        IOUtil.deleteQuietly(parentDir);
        Assert.assertFalse("parentDir should be deleted", parentDir.exists());
        Assert.assertFalse("file1 should be deleted", file1.exists());
        Assert.assertFalse("file2 should be deleted", file2.exists());
        Assert.assertFalse("childDir should be deleted", childDir.exists());
        Assert.assertFalse("childFile1 should be deleted", childFile1.exists());
        Assert.assertFalse("childFile2 should be deleted", childFile2.exists());
    }

    @Test
    public void testDeleteQuietly_shouldDoNothingIfFileCouldNotBeDeleted() {
        File file = Mockito.mock(File.class);
        Mockito.when(file.exists()).thenReturn(true);
        Mockito.when(file.delete()).thenReturn(false);
        IOUtil.deleteQuietly(file);
    }

    @Test
    public void testToFileName_shouldNotChangeValidFileName() {
        String expected = "valid-fileName_23.txt";
        String actual = IOUtil.toFileName(expected);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testToFileName_shouldChangeInvalidFileName() {
        String expected = "a_b_c_d_e_f_g_h_j_k_l_m.txt";
        String actual = IOUtil.toFileName("a:b?c*d\"e|f<g>h\'j,k\\l/m.txt");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetFileFromResources_shouldReturnExistingFile() {
        File file = IOUtil.getFileFromResources("logging.properties");
        Assert.assertTrue(file.exists());
    }

    @Test(expected = HazelcastException.class)
    public void testGetFileFromResources_shouldThrowExceptionIfFileDoesNotExist() {
        IOUtil.getFileFromResources("doesNotExist");
    }

    @Test
    public void testCompactOrClearByteBuffer() {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[IOUtilTest.SIZE]);
        buffer.put(((byte) (255)));
        buffer.put(((byte) (255)));
        buffer.flip();
        buffer.position(1);
        IOUtil.compactOrClear(buffer);
        Assert.assertEquals("Buffer position invalid", 1, buffer.position());
        buffer.put(((byte) (255)));
        buffer.put(((byte) (255)));
        IOUtil.compactOrClear(buffer);
        Assert.assertEquals("Buffer position invalid", 0, buffer.position());
    }

    @Test
    public void testCopyToHeapBuffer_whenSourceIsNull() {
        ByteBuffer dst = ByteBuffer.wrap(new byte[IOUtilTest.SIZE]);
        Assert.assertEquals(0, IOUtil.copyToHeapBuffer(null, dst));
    }

    @Test
    public void testReadAttributeValue_whenTypeBoolean() throws Exception {
        final boolean expected = true;
        ObjectDataInput input = Mockito.mock(ObjectDataInput.class);
        Mockito.when(input.readByte()).thenReturn(PRIMITIVE_TYPE_BOOLEAN);
        Mockito.when(input.readBoolean()).thenReturn(expected);
        Object actual = IOUtil.readAttributeValue(input);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testReadAttributeValue_whenTypeByte() throws Exception {
        final byte expected = ((byte) (255));
        ObjectDataInput input = Mockito.mock(ObjectDataInput.class);
        Mockito.when(input.readByte()).thenReturn(PRIMITIVE_TYPE_BYTE).thenReturn(expected);
        Object actual = IOUtil.readAttributeValue(input);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testReadAttributeValue_whenTypeShort() throws Exception {
        final short expected = 42;
        ObjectDataInput input = Mockito.mock(ObjectDataInput.class);
        Mockito.when(input.readByte()).thenReturn(PRIMITIVE_TYPE_SHORT);
        Mockito.when(input.readShort()).thenReturn(expected);
        Object actual = IOUtil.readAttributeValue(input);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testReadAttributeValue_whenTypeInteger() throws Exception {
        final int expected = 42;
        ObjectDataInput input = Mockito.mock(ObjectDataInput.class);
        Mockito.when(input.readByte()).thenReturn(PRIMITIVE_TYPE_INTEGER);
        Mockito.when(input.readInt()).thenReturn(expected);
        Object actual = IOUtil.readAttributeValue(input);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testReadAttributeValue_whenTypeLong() throws Exception {
        final long expected = 42L;
        ObjectDataInput input = Mockito.mock(ObjectDataInput.class);
        Mockito.when(input.readByte()).thenReturn(PRIMITIVE_TYPE_LONG);
        Mockito.when(input.readLong()).thenReturn(expected);
        Object actual = IOUtil.readAttributeValue(input);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testReadAttributeValue_whenTypeFloat() throws Exception {
        final float expected = 0.42F;
        ObjectDataInput input = Mockito.mock(ObjectDataInput.class);
        Mockito.when(input.readByte()).thenReturn(PRIMITIVE_TYPE_FLOAT);
        Mockito.when(input.readFloat()).thenReturn(expected);
        Object actual = IOUtil.readAttributeValue(input);
        Assert.assertEquals(Float.floatToIntBits(expected), Float.floatToIntBits(((Float) (actual))));
    }

    @Test
    public void testReadAttributeValue_whenTypeDouble() throws Exception {
        final double expected = 42.42F;
        ObjectDataInput input = Mockito.mock(ObjectDataInput.class);
        Mockito.when(input.readByte()).thenReturn(PRIMITIVE_TYPE_DOUBLE);
        Mockito.when(input.readDouble()).thenReturn(expected);
        Object actual = IOUtil.readAttributeValue(input);
        Assert.assertEquals(Double.doubleToLongBits(expected), Double.doubleToLongBits(((Double) (actual))));
    }

    @Test
    public void testReadAttributeValue_whenTypeUTF() throws Exception {
        final String expected = "UTF";
        ObjectDataInput input = Mockito.mock(ObjectDataInput.class);
        Mockito.when(input.readByte()).thenReturn(PRIMITIVE_TYPE_UTF);
        Mockito.when(input.readUTF()).thenReturn(expected);
        Object actual = IOUtil.readAttributeValue(input);
        Assert.assertEquals(expected, actual);
    }

    @Test(expected = IllegalStateException.class)
    public void testReadAttributeValue_whenInvalidType() throws Exception {
        ObjectDataInput input = Mockito.mock(ObjectDataInput.class);
        Mockito.when(input.readByte()).thenReturn(((byte) (255)));
        IOUtil.readAttributeValue(input);
    }

    @Test
    public void testCloseServerSocket_whenServerSocketThrows() throws Exception {
        ServerSocket serverSocket = Mockito.mock(ServerSocket.class);
        Mockito.doThrow(new IOException()).when(serverSocket).close();
        try {
            IOUtil.close(serverSocket);
        } catch (Exception ex) {
            Assert.fail("IOUtils should silently close server socket when exception thrown");
        }
    }

    @Test(expected = HazelcastException.class)
    public void testRename_whenFileNowNotExist() {
        File toBe = Mockito.mock(File.class);
        File now = Mockito.mock(File.class);
        Mockito.when(now.renameTo(toBe)).thenReturn(false);
        Mockito.when(now.exists()).thenReturn(false);
        IOUtil.rename(now, toBe);
    }

    @Test(expected = HazelcastException.class)
    public void testRename_whenFileToBeNotExist() {
        File toBe = Mockito.mock(File.class);
        Mockito.when(toBe.exists()).thenReturn(false);
        File now = Mockito.mock(File.class);
        Mockito.when(now.renameTo(toBe)).thenReturn(false);
        Mockito.when(now.exists()).thenReturn(true);
        IOUtil.rename(now, toBe);
    }

    @Test(expected = HazelcastException.class)
    public void testRename_whenFileToBeNotDeleted() {
        File toBe = Mockito.mock(File.class);
        Mockito.when(toBe.exists()).thenReturn(true);
        Mockito.when(toBe.delete()).thenReturn(false);
        File now = Mockito.mock(File.class);
        Mockito.when(now.renameTo(toBe)).thenReturn(false);
        Mockito.when(now.exists()).thenReturn(true);
        IOUtil.rename(now, toBe);
    }

    @Test
    public void testGetPath_shouldFormat() {
        String root = "root";
        String parent = "parent";
        String child = "child";
        String expected = String.format("%s%s%s%s%s", root, File.separator, parent, File.separator, child);
        String actual = IOUtil.getPath(root, parent, child);
        Assert.assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPath_whenPathsInvalid() {
        IOUtil.getPath();
    }
}

