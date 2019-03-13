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
package bt.data;


import BitOrder.LITTLE_ENDIAN;
import bt.TestUtil;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class ChunkDescriptor_FileStorageUnitTest {
    @Rule
    public TestFileSystemStorage storage = new TestFileSystemStorage();

    private ChunkVerifier verifier;

    private IDataDescriptorFactory dataDescriptorFactory;

    /**
     * ***********************************************************************************
     */
    private byte[] SINGLE_FILE = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 1, 2, 3, 4, 1, 2, 3, 4, 1, 2, 3, 4, 1, 2, 1, 2, 3, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 1, 2, 3, 4, 1, 2, 3, 4, 1, 2, 3, 4, 1, 2, 3, 4 };

    @Test
    public void testDescriptors_WriteSingleFile() {
        String fileName = "1-single.bin";
        DataDescriptor descriptor = createDataDescriptor_SingleFile(fileName);
        List<ChunkDescriptor> chunks = descriptor.getChunkDescriptors();
        chunks.get(0).getData().putBytes(TestUtil.sequence(8));
        chunks.get(0).getData().getSubrange(8).putBytes(TestUtil.sequence(8));
        Assert.assertTrue(chunks.get(0).isComplete());
        Assert.assertTrue(verifier.verify(chunks.get(0)));
        chunks.get(1).getData().putBytes(TestUtil.sequence(4));
        chunks.get(1).getData().getSubrange(4).putBytes(TestUtil.sequence(4));
        chunks.get(1).getData().getSubrange(8).putBytes(TestUtil.sequence(4));
        chunks.get(1).getData().getSubrange(12).putBytes(TestUtil.sequence(4));
        Assert.assertTrue(chunks.get(1).isComplete());
        Assert.assertTrue(verifier.verify(chunks.get(1)));
        // reverse order
        chunks.get(2).getData().getSubrange(5).putBytes(TestUtil.sequence(11));
        chunks.get(2).getData().getSubrange(2).putBytes(TestUtil.sequence(3));
        chunks.get(2).getData().putBytes(TestUtil.sequence(2));
        Assert.assertFalse(chunks.get(2).isComplete());
        // "random" order
        chunks.get(3).getData().getSubrange(4).putBytes(TestUtil.sequence(4));
        chunks.get(3).getData().getSubrange(0).putBytes(TestUtil.sequence(4));
        chunks.get(3).getData().getSubrange(12).putBytes(TestUtil.sequence(4));
        chunks.get(3).getData().getSubrange(8).putBytes(TestUtil.sequence(4));
        Assert.assertTrue(chunks.get(3).isComplete());
        Assert.assertTrue(verifier.verify(chunks.get(3)));
        ChunkDescriptorTestUtil.assertFileHasContents(new File(storage.getRoot(), fileName), SINGLE_FILE);
    }

    @Test
    public void testDescriptors_WriteSingleEmptyFile() {
        String fileName = "1-single-empty.bin";
        DataDescriptor descriptor = createDataDescriptor_SingleEmptyFile(fileName);
        List<ChunkDescriptor> chunks = descriptor.getChunkDescriptors();
        Assert.assertTrue(chunks.isEmpty());
        Assert.assertEquals(0, descriptor.getBitfield().getPiecesRemaining());
        Assert.assertEquals(0, descriptor.getBitfield().getPiecesTotal());
        Assert.assertEquals(0, descriptor.getBitfield().getPiecesComplete());
        Assert.assertEquals(0, descriptor.getBitfield().getBitmask().size());
        Assert.assertEquals(0, descriptor.getBitfield().toByteArray(LITTLE_ENDIAN).length);
        ChunkDescriptorTestUtil.assertFileHasContents(new File(storage.getRoot(), fileName), new byte[0]);
    }

    /**
     * ***********************************************************************************
     */
    @Test
    public void testDescriptors_ReadSingleFile() {
        String fileName = "1-single-read.bin";
        ChunkDescriptorTestUtil.writeBytesToFile(new File(storage.getRoot(), fileName), SINGLE_FILE);
        DataDescriptor descriptor = createDataDescriptor_SingleFile(fileName);
        List<ChunkDescriptor> chunks = descriptor.getChunkDescriptors();
        byte[] block;
        // beginning
        block = chunks.get(0).getData().getSubrange(0, 8).getBytes();
        Assert.assertArrayEquals(Arrays.copyOfRange(SINGLE_FILE, 0, 8), block);
        // end
        block = chunks.get(0).getData().getSubrange(8, 8).getBytes();
        Assert.assertArrayEquals(Arrays.copyOfRange(SINGLE_FILE, 8, 16), block);
        // whole chunk
        block = chunks.get(0).getData().getSubrange(0, 16).getBytes();
        Assert.assertArrayEquals(Arrays.copyOfRange(SINGLE_FILE, 0, 16), block);
        // piece
        block = chunks.get(0).getData().getSubrange(1, 14).getBytes();
        Assert.assertArrayEquals(Arrays.copyOfRange(SINGLE_FILE, 1, 15), block);
    }

    /**
     * ***********************************************************************************
     */
    private byte[] MULTI_FILE_1 = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 1, 2, 3, 4, 1 };

    private byte[] MULTI_FILE_2 = new byte[]{ 2, 3, 4, 1, 2, 3, 4, 1, 2, 1, 2, 3, 1, 2, 3, 4, 5, 6 };

    private byte[] MULTI_FILE_3 = new byte[]{ 7, 8, 9, 1, 2 };

    private byte[] MULTI_FILE_4 = new byte[]{ 1, 2, 3, 4, 1, 2, 3, 4, 1, 2, 3, 4, 1, 2, 3, 4, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6 };

    private byte[] MULTI_FILE_5 = new byte[]{ 7, 1, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5 };

    private byte[] MULTI_FILE_6 = new byte[]{ 1 };

    @Test
    public void testDescriptors_WriteMultiFile() {
        String torrentName = "xyz-torrent";
        File torrentDirectory = new File(storage.getRoot(), torrentName);
        String extension = "-multi.bin";
        String fileName1 = 1 + extension;
        String fileName2 = 2 + extension;
        String fileName3 = 3 + extension;
        String fileName4 = 4 + extension;
        String fileName5 = 5 + extension;
        String fileName6 = 6 + extension;
        DataDescriptor descriptor = createDataDescriptor_MultiFile(fileName1, fileName2, fileName3, fileName4, fileName5, fileName6, torrentDirectory);
        List<ChunkDescriptor> chunks = descriptor.getChunkDescriptors();
        chunks.get(0).getData().putBytes(TestUtil.sequence(8));
        chunks.get(0).getData().getSubrange(8).putBytes(TestUtil.sequence(8));
        Assert.assertTrue(chunks.get(0).isComplete());
        Assert.assertTrue(verifier.verify(chunks.get(0)));
        chunks.get(1).getData().putBytes(TestUtil.sequence(4));
        chunks.get(1).getData().getSubrange(4).putBytes(TestUtil.sequence(4));
        chunks.get(1).getData().getSubrange(8).putBytes(TestUtil.sequence(4));
        chunks.get(1).getData().getSubrange(12).putBytes(TestUtil.sequence(4));
        Assert.assertTrue(chunks.get(1).isComplete());
        Assert.assertTrue(verifier.verify(chunks.get(1)));
        // reverse order
        chunks.get(2).getData().getSubrange(5).putBytes(TestUtil.sequence(11));
        chunks.get(2).getData().getSubrange(2).putBytes(TestUtil.sequence(3));
        chunks.get(2).getData().putBytes(TestUtil.sequence(2));
        Assert.assertFalse(chunks.get(2).isComplete());
        chunks.get(2).getData().putBytes(new byte[]{ 1, 2, 1, 2, 3, 1, 2, 3 });
        Assert.assertTrue(chunks.get(2).isComplete());
        Assert.assertTrue(verifier.verify(chunks.get(2)));
        // "random" order
        chunks.get(3).getData().getSubrange(4).putBytes(TestUtil.sequence(4));
        chunks.get(3).getData().putBytes(TestUtil.sequence(4));
        chunks.get(3).getData().getSubrange(12).putBytes(TestUtil.sequence(4));
        chunks.get(3).getData().getSubrange(8).putBytes(TestUtil.sequence(4));
        Assert.assertTrue(chunks.get(3).isComplete());
        Assert.assertTrue(verifier.verify(chunks.get(3)));
        // block size same as chunk size
        chunks.get(4).getData().putBytes(TestUtil.sequence(16));
        Assert.assertTrue(chunks.get(4).isComplete());
        Assert.assertTrue(verifier.verify(chunks.get(4)));
        // 1-byte blocks
        chunks.get(5).getData().putBytes(TestUtil.sequence(1));
        chunks.get(5).getData().getSubrange(15).putBytes(TestUtil.sequence(1));
        chunks.get(5).getData().getSubrange(1).putBytes(TestUtil.sequence(14));
        Assert.assertFalse(chunks.get(5).isComplete());
        chunks.get(5).getData().putBytes(new byte[]{ 1, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 1 });
        Assert.assertTrue(chunks.get(5).isComplete());
        Assert.assertTrue(verifier.verify(chunks.get(5)));
        ChunkDescriptorTestUtil.assertFileHasContents(new File(torrentDirectory, fileName1), MULTI_FILE_1);
        ChunkDescriptorTestUtil.assertFileHasContents(new File(torrentDirectory, fileName2), MULTI_FILE_2);
        ChunkDescriptorTestUtil.assertFileHasContents(new File(torrentDirectory, fileName3), MULTI_FILE_3);
        ChunkDescriptorTestUtil.assertFileHasContents(new File(torrentDirectory, fileName4), MULTI_FILE_4);
        ChunkDescriptorTestUtil.assertFileHasContents(new File(torrentDirectory, fileName5), MULTI_FILE_5);
        ChunkDescriptorTestUtil.assertFileHasContents(new File(torrentDirectory, fileName6), MULTI_FILE_6);
    }

    @Test
    public void testDescriptors_WriteMultiEmptyFile() {
        String torrentName = "xyz-torrent";
        File torrentDirectory = new File(storage.getRoot(), torrentName);
        String extension = "-multi.bin";
        String fileName1 = 1 + extension;
        String fileName2 = 2 + extension;
        String fileName3 = 3 + extension;
        String fileName4 = 4 + extension;
        String fileName5 = 5 + extension;
        String fileName6 = 6 + extension;
        DataDescriptor descriptor = createDataDescriptor_MultiEmptyFile(fileName1, fileName2, fileName3, fileName4, fileName5, fileName6, torrentDirectory);
        Assert.assertTrue(descriptor.getChunkDescriptors().isEmpty());
        Assert.assertEquals(0, descriptor.getBitfield().getPiecesRemaining());
        Assert.assertEquals(0, descriptor.getBitfield().getPiecesTotal());
        Assert.assertEquals(0, descriptor.getBitfield().getPiecesComplete());
        Assert.assertEquals(0, descriptor.getBitfield().getBitmask().size());
        Assert.assertEquals(0, descriptor.getBitfield().toByteArray(LITTLE_ENDIAN).length);
        ChunkDescriptorTestUtil.assertFileHasContents(new File(torrentDirectory, fileName1), new byte[0]);
        ChunkDescriptorTestUtil.assertFileHasContents(new File(torrentDirectory, fileName2), new byte[0]);
        ChunkDescriptorTestUtil.assertFileHasContents(new File(torrentDirectory, fileName3), new byte[0]);
        ChunkDescriptorTestUtil.assertFileHasContents(new File(torrentDirectory, fileName4), new byte[0]);
        ChunkDescriptorTestUtil.assertFileHasContents(new File(torrentDirectory, fileName5), new byte[0]);
        ChunkDescriptorTestUtil.assertFileHasContents(new File(torrentDirectory, fileName6), new byte[0]);
    }

    /**
     * ***********************************************************************************
     */
    @Test
    public void testDescriptors_ReadMultiFile() {
        String torrentName = "xyz-torrent-read";
        File torrentDirectory = new File(storage.getRoot(), torrentName);
        String extension = "-multi.bin";
        String fileName1 = 1 + extension;
        String fileName2 = 2 + extension;
        String fileName3 = 3 + extension;
        String fileName4 = 4 + extension;
        String fileName5 = 5 + extension;
        String fileName6 = 6 + extension;
        ChunkDescriptorTestUtil.writeBytesToFile(new File(torrentDirectory, fileName1), MULTI_FILE_1);
        ChunkDescriptorTestUtil.writeBytesToFile(new File(torrentDirectory, fileName2), MULTI_FILE_2);
        ChunkDescriptorTestUtil.writeBytesToFile(new File(torrentDirectory, fileName3), MULTI_FILE_3);
        ChunkDescriptorTestUtil.writeBytesToFile(new File(torrentDirectory, fileName4), MULTI_FILE_4);
        ChunkDescriptorTestUtil.writeBytesToFile(new File(torrentDirectory, fileName5), MULTI_FILE_5);
        ChunkDescriptorTestUtil.writeBytesToFile(new File(torrentDirectory, fileName6), MULTI_FILE_6);
        DataDescriptor descriptor = createDataDescriptor_MultiFile(fileName1, fileName2, fileName3, fileName4, fileName5, fileName6, torrentDirectory);
        List<ChunkDescriptor> chunks = descriptor.getChunkDescriptors();
        byte[] block;
        // beginning
        block = chunks.get(0).getData().getSubrange(0, 8).getBytes();
        Assert.assertArrayEquals(Arrays.copyOfRange(MULTI_FILE_1, 0, 8), block);
        // end
        block = chunks.get(0).getData().getSubrange(8, 8).getBytes();
        Assert.assertArrayEquals(Arrays.copyOfRange(MULTI_FILE_1, 8, 16), block);
        // whole chunk
        block = chunks.get(0).getData().getSubrange(0, 16).getBytes();
        Assert.assertArrayEquals(Arrays.copyOfRange(MULTI_FILE_1, 0, 16), block);
        // piece
        block = chunks.get(0).getData().getSubrange(1, 14).getBytes();
        Assert.assertArrayEquals(Arrays.copyOfRange(MULTI_FILE_1, 1, 15), block);
        // end of a file
        block = chunks.get(1).getData().getSubrange(0, 9).getBytes();
        Assert.assertArrayEquals(Arrays.copyOfRange(MULTI_FILE_1, 16, 25), block);
        // beginning of a file
        block = chunks.get(1).getData().getSubrange(9, 7).getBytes();
        Assert.assertArrayEquals(Arrays.copyOfRange(MULTI_FILE_2, 0, 7), block);
        // whole chunk that consists of 2 files
        block = chunks.get(1).getData().getSubrange(0, 16).getBytes();
        byte[] chunk1 = new byte[16];
        System.arraycopy(MULTI_FILE_1, 16, chunk1, 0, 9);
        System.arraycopy(MULTI_FILE_2, 0, chunk1, 9, 7);
        Assert.assertArrayEquals(chunk1, block);
        // piece of a chunk that consists of 2 files
        block = chunks.get(1).getData().getSubrange(8, 2).getBytes();
        byte[] chunk1piece = new byte[2];
        System.arraycopy(MULTI_FILE_1, 24, chunk1piece, 0, 1);
        System.arraycopy(MULTI_FILE_2, 0, chunk1piece, 1, 1);
        Assert.assertArrayEquals(chunk1piece, block);
        // 1-byte block
        block = chunks.get(5).getData().getSubrange(15, 1).getBytes();
        Assert.assertArrayEquals(Arrays.copyOfRange(MULTI_FILE_6, 0, 1), block);
    }
}

