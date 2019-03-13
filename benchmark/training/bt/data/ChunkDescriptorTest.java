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


import org.junit.Assert;
import org.junit.Test;


public class ChunkDescriptorTest {
    @Test
    public void testChunk_Lifecycle_SingleFile_NoOverlap() {
        long blockSize = 4;
        long fileSize = blockSize * 4;
        ChunkDescriptor chunkDescriptor = ChunkDescriptorTestUtil.buildChunk(ChunkDescriptorTestUtil.mockStorageUnits(fileSize), blockSize);
        Assert.assertTrue(chunkDescriptor.isEmpty());
        chunkDescriptor.getData().putBytes(new byte[4]);
        Assert.assertFalse(((chunkDescriptor.isEmpty()) || (chunkDescriptor.isComplete())));
        chunkDescriptor.getData().getSubrange(blockSize).putBytes(new byte[4]);
        Assert.assertFalse(((chunkDescriptor.isEmpty()) || (chunkDescriptor.isComplete())));
        chunkDescriptor.getData().getSubrange((blockSize * 2)).putBytes(new byte[4]);
        Assert.assertFalse(((chunkDescriptor.isEmpty()) || (chunkDescriptor.isComplete())));
        chunkDescriptor.getData().getSubrange((blockSize * 3)).putBytes(new byte[4]);
        Assert.assertTrue(chunkDescriptor.isComplete());
        ChunkDescriptorTest.assertHasBlockStatuses(chunkDescriptor, new byte[]{ 1, 1, 1, 1 });
    }

    @Test
    public void testChunk_Lifecycle_PartialLastBlock_NoOverlap() {
        long blockSize = 4;
        long fileSize = (blockSize * 2) + 3;
        ChunkDescriptor chunkDescriptor = ChunkDescriptorTestUtil.buildChunk(ChunkDescriptorTestUtil.mockStorageUnits(fileSize), blockSize);
        Assert.assertTrue(chunkDescriptor.isEmpty());
        chunkDescriptor.getData().putBytes(new byte[4]);
        Assert.assertFalse(((chunkDescriptor.isEmpty()) || (chunkDescriptor.isComplete())));
        chunkDescriptor.getData().getSubrange(blockSize).putBytes(new byte[4]);
        Assert.assertFalse(((chunkDescriptor.isEmpty()) || (chunkDescriptor.isComplete())));
        chunkDescriptor.getData().getSubrange((blockSize * 2)).putBytes(new byte[3]);
        Assert.assertTrue(chunkDescriptor.isComplete());
        ChunkDescriptorTest.assertHasBlockStatuses(chunkDescriptor, new byte[]{ 1, 1, 1 });
    }

    @Test
    public void testChunk_Lifecycle_PartialLastBlock_Incomplete() {
        long blockSize = 4;
        long fileSize = blockSize + 3;
        ChunkDescriptor chunkDescriptor = ChunkDescriptorTestUtil.buildChunk(ChunkDescriptorTestUtil.mockStorageUnits(fileSize), blockSize);
        Assert.assertTrue(chunkDescriptor.isEmpty());
        chunkDescriptor.getData().putBytes(new byte[4]);
        Assert.assertFalse(((chunkDescriptor.isEmpty()) || (chunkDescriptor.isComplete())));
        chunkDescriptor.getData().getSubrange((blockSize + 1)).putBytes(new byte[2]);
        Assert.assertFalse(((chunkDescriptor.isEmpty()) || (chunkDescriptor.isComplete())));
        ChunkDescriptorTest.assertHasBlockStatuses(chunkDescriptor, new byte[]{ 1, 0 });
    }

    @Test
    public void testChunk_Lifecycle_PartialLastBlock_Overlaps() {
        long blockSize = 4;
        long fileSize = (blockSize * 2) + 3;
        ChunkDescriptor chunkDescriptor = ChunkDescriptorTestUtil.buildChunk(ChunkDescriptorTestUtil.mockStorageUnits(fileSize), blockSize);
        Assert.assertTrue(chunkDescriptor.isEmpty());
        chunkDescriptor.getData().putBytes(new byte[4]);
        Assert.assertFalse(((chunkDescriptor.isEmpty()) || (chunkDescriptor.isComplete())));
        chunkDescriptor.getData().getSubrange(blockSize).putBytes(new byte[7]);
        Assert.assertTrue(chunkDescriptor.isComplete());
        ChunkDescriptorTest.assertHasBlockStatuses(chunkDescriptor, new byte[]{ 1, 1, 1 });
    }

    @Test
    public void testChunk_Lifecycle_MultiFile_NoOverlap() {
        long blockSize = 4;
        long fileSize1 = (blockSize * 2) - 3;
        long fileSize2 = (blockSize * 2) + 3;
        ChunkDescriptor chunkDescriptor = ChunkDescriptorTestUtil.buildChunk(ChunkDescriptorTestUtil.mockStorageUnits(fileSize1, fileSize2), blockSize);
        Assert.assertTrue(chunkDescriptor.isEmpty());
        chunkDescriptor.getData().putBytes(new byte[4]);
        Assert.assertFalse(((chunkDescriptor.isEmpty()) || (chunkDescriptor.isComplete())));
        chunkDescriptor.getData().getSubrange(blockSize).putBytes(new byte[4]);
        Assert.assertFalse(((chunkDescriptor.isEmpty()) || (chunkDescriptor.isComplete())));
        chunkDescriptor.getData().getSubrange((blockSize * 2)).putBytes(new byte[4]);
        Assert.assertFalse(((chunkDescriptor.isEmpty()) || (chunkDescriptor.isComplete())));
        chunkDescriptor.getData().getSubrange((blockSize * 3)).putBytes(new byte[4]);
        Assert.assertTrue(chunkDescriptor.isComplete());
        ChunkDescriptorTest.assertHasBlockStatuses(chunkDescriptor, new byte[]{ 1, 1, 1, 1 });
    }

    @Test
    public void testChunk_Lifecycle_SingleFile_Overlaps() {
        long blockSize = 4;
        long fileSize = blockSize * 4;
        ChunkDescriptor chunkDescriptor = ChunkDescriptorTestUtil.buildChunk(ChunkDescriptorTestUtil.mockStorageUnits(fileSize), blockSize);
        Assert.assertTrue(chunkDescriptor.isEmpty());
        chunkDescriptor.getData().getSubrange(1).putBytes(new byte[4]);
        Assert.assertTrue(chunkDescriptor.isEmpty());
        chunkDescriptor.getData().getSubrange(1).putBytes(new byte[7]);
        Assert.assertFalse(((chunkDescriptor.isEmpty()) || (chunkDescriptor.isComplete())));
        ChunkDescriptorTest.assertHasBlockStatuses(chunkDescriptor, new byte[]{ 0, 1, 0, 0 });
        chunkDescriptor.getData().getSubrange((blockSize * 2)).putBytes(new byte[6]);
        Assert.assertFalse(((chunkDescriptor.isEmpty()) || (chunkDescriptor.isComplete())));
        ChunkDescriptorTest.assertHasBlockStatuses(chunkDescriptor, new byte[]{ 0, 1, 1, 0 });
        chunkDescriptor.getData().getSubrange(((blockSize * 3) + 1)).putBytes(new byte[1]);
        Assert.assertFalse(((chunkDescriptor.isEmpty()) || (chunkDescriptor.isComplete())));
        ChunkDescriptorTest.assertHasBlockStatuses(chunkDescriptor, new byte[]{ 0, 1, 1, 0 });
        chunkDescriptor.getData().getSubrange((blockSize - 1)).putBytes(new byte[1]);
        Assert.assertFalse(((chunkDescriptor.isEmpty()) || (chunkDescriptor.isComplete())));
        ChunkDescriptorTest.assertHasBlockStatuses(chunkDescriptor, new byte[]{ 0, 1, 1, 0 });
        chunkDescriptor.getData().putBytes(new byte[5]);
        Assert.assertFalse(((chunkDescriptor.isEmpty()) || (chunkDescriptor.isComplete())));
        ChunkDescriptorTest.assertHasBlockStatuses(chunkDescriptor, new byte[]{ 1, 1, 1, 0 });
        chunkDescriptor.getData().getSubrange(((blockSize * 3) - 1)).putBytes(new byte[5]);
        Assert.assertTrue(chunkDescriptor.isComplete());
        ChunkDescriptorTest.assertHasBlockStatuses(chunkDescriptor, new byte[]{ 1, 1, 1, 1 });
    }

    @Test
    public void testChunk_Lifecycle_MultiFile_Overlaps() {
        long blockSize = 4;
        long fileSize1 = blockSize - 1;
        long fileSize2 = blockSize + 1;
        long fileSize3 = blockSize - 2;
        long fileSize4 = blockSize + 2;
        ChunkDescriptor chunkDescriptor = ChunkDescriptorTestUtil.buildChunk(ChunkDescriptorTestUtil.mockStorageUnits(fileSize1, fileSize2, fileSize3, fileSize4), blockSize);
        Assert.assertTrue(chunkDescriptor.isEmpty());
        chunkDescriptor.getData().getSubrange(1).putBytes(new byte[4]);
        Assert.assertTrue(chunkDescriptor.isEmpty());
        chunkDescriptor.getData().getSubrange(1).putBytes(new byte[7]);
        Assert.assertFalse(((chunkDescriptor.isEmpty()) || (chunkDescriptor.isComplete())));
        ChunkDescriptorTest.assertHasBlockStatuses(chunkDescriptor, new byte[]{ 0, 1, 0, 0 });
        chunkDescriptor.getData().getSubrange((blockSize * 2)).putBytes(new byte[6]);
        Assert.assertFalse(((chunkDescriptor.isEmpty()) || (chunkDescriptor.isComplete())));
        ChunkDescriptorTest.assertHasBlockStatuses(chunkDescriptor, new byte[]{ 0, 1, 1, 0 });
        chunkDescriptor.getData().getSubrange(((blockSize * 3) + 1)).putBytes(new byte[1]);
        Assert.assertFalse(((chunkDescriptor.isEmpty()) || (chunkDescriptor.isComplete())));
        ChunkDescriptorTest.assertHasBlockStatuses(chunkDescriptor, new byte[]{ 0, 1, 1, 0 });
        chunkDescriptor.getData().getSubrange((blockSize - 1)).putBytes(new byte[1]);
        Assert.assertFalse(((chunkDescriptor.isEmpty()) || (chunkDescriptor.isComplete())));
        ChunkDescriptorTest.assertHasBlockStatuses(chunkDescriptor, new byte[]{ 0, 1, 1, 0 });
        chunkDescriptor.getData().putBytes(new byte[5]);
        Assert.assertFalse(((chunkDescriptor.isEmpty()) || (chunkDescriptor.isComplete())));
        ChunkDescriptorTest.assertHasBlockStatuses(chunkDescriptor, new byte[]{ 1, 1, 1, 0 });
        chunkDescriptor.getData().getSubrange(((blockSize * 3) - 1)).putBytes(new byte[5]);
        Assert.assertTrue(chunkDescriptor.isComplete());
        ChunkDescriptorTest.assertHasBlockStatuses(chunkDescriptor, new byte[]{ 1, 1, 1, 1 });
    }
}

