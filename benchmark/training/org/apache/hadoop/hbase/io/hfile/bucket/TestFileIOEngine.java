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
package org.apache.hadoop.hbase.io.hfile.bucket;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.nio.ByteBuff;
import org.apache.hadoop.hbase.testclassification.IOTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Basic test for {@link FileIOEngine}
 */
@Category({ IOTests.class, SmallTests.class })
public class TestFileIOEngine {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestFileIOEngine.class);

    private static final long TOTAL_CAPACITY = (6 * 1024) * 1024;// 6 MB


    private static final String[] FILE_PATHS = new String[]{ "testFileIOEngine1", "testFileIOEngine2", "testFileIOEngine3" };

    private static final long SIZE_PER_FILE = (TestFileIOEngine.TOTAL_CAPACITY) / (TestFileIOEngine.FILE_PATHS.length);// 2 MB per File


    private static final List<Long> boundaryStartPositions = new ArrayList<Long>();

    private static final List<Long> boundaryStopPositions = new ArrayList<Long>();

    private FileIOEngine fileIOEngine;

    static {
        TestFileIOEngine.boundaryStartPositions.add(0L);
        for (int i = 1; i < (TestFileIOEngine.FILE_PATHS.length); i++) {
            TestFileIOEngine.boundaryStartPositions.add((((TestFileIOEngine.SIZE_PER_FILE) * i) - 1));
            TestFileIOEngine.boundaryStartPositions.add(((TestFileIOEngine.SIZE_PER_FILE) * i));
            TestFileIOEngine.boundaryStartPositions.add((((TestFileIOEngine.SIZE_PER_FILE) * i) + 1));
        }
        for (int i = 1; i < (TestFileIOEngine.FILE_PATHS.length); i++) {
            TestFileIOEngine.boundaryStopPositions.add((((TestFileIOEngine.SIZE_PER_FILE) * i) - 1));
            TestFileIOEngine.boundaryStopPositions.add(((TestFileIOEngine.SIZE_PER_FILE) * i));
            TestFileIOEngine.boundaryStopPositions.add((((TestFileIOEngine.SIZE_PER_FILE) * i) + 1));
        }
        TestFileIOEngine.boundaryStopPositions.add((((TestFileIOEngine.SIZE_PER_FILE) * (TestFileIOEngine.FILE_PATHS.length)) - 1));
    }

    @Test
    public void testFileIOEngine() throws IOException {
        for (int i = 0; i < 500; i++) {
            int len = ((int) (Math.floor(((Math.random()) * 100)))) + 1;
            long offset = ((long) (Math.floor((((Math.random()) * (TestFileIOEngine.TOTAL_CAPACITY)) % ((TestFileIOEngine.TOTAL_CAPACITY) - len)))));
            if (i < (TestFileIOEngine.boundaryStartPositions.size())) {
                // make the boundary start positon
                offset = TestFileIOEngine.boundaryStartPositions.get(i);
            } else
                if ((i - (TestFileIOEngine.boundaryStartPositions.size())) < (TestFileIOEngine.boundaryStopPositions.size())) {
                    // make the boundary stop positon
                    offset = ((TestFileIOEngine.boundaryStopPositions.get((i - (TestFileIOEngine.boundaryStartPositions.size())))) - len) + 1;
                } else
                    if ((i % 2) == 0) {
                        // make the cross-files block writing/reading
                        offset = ((Math.max(1, (i % (TestFileIOEngine.FILE_PATHS.length)))) * (TestFileIOEngine.SIZE_PER_FILE)) - (len / 2);
                    }


            byte[] data1 = new byte[len];
            for (int j = 0; j < (data1.length); ++j) {
                data1[j] = ((byte) ((Math.random()) * 255));
            }
            fileIOEngine.write(ByteBuffer.wrap(data1), offset);
            TestByteBufferIOEngine.BufferGrabbingDeserializer deserializer = new TestByteBufferIOEngine.BufferGrabbingDeserializer();
            fileIOEngine.read(offset, len, deserializer);
            ByteBuff data2 = deserializer.getDeserializedByteBuff();
            Assert.assertArrayEquals(data1, data2.array());
        }
    }

    @Test
    public void testFileIOEngineHandlesZeroLengthInput() throws IOException {
        byte[] data1 = new byte[0];
        fileIOEngine.write(ByteBuffer.wrap(data1), 0);
        TestByteBufferIOEngine.BufferGrabbingDeserializer deserializer = new TestByteBufferIOEngine.BufferGrabbingDeserializer();
        fileIOEngine.read(0, 0, deserializer);
        ByteBuff data2 = deserializer.getDeserializedByteBuff();
        Assert.assertArrayEquals(data1, data2.array());
    }

    @Test
    public void testClosedChannelException() throws IOException {
        fileIOEngine.closeFileChannels();
        int len = 5;
        long offset = 0L;
        byte[] data1 = new byte[len];
        for (int j = 0; j < (data1.length); ++j) {
            data1[j] = ((byte) ((Math.random()) * 255));
        }
        fileIOEngine.write(ByteBuffer.wrap(data1), offset);
        TestByteBufferIOEngine.BufferGrabbingDeserializer deserializer = new TestByteBufferIOEngine.BufferGrabbingDeserializer();
        fileIOEngine.read(offset, len, deserializer);
        ByteBuff data2 = deserializer.getDeserializedByteBuff();
        Assert.assertArrayEquals(data1, data2.array());
    }

    @Test
    public void testRefreshFileConnection() throws IOException {
        FileChannel[] fileChannels = fileIOEngine.getFileChannels();
        FileChannel fileChannel = fileChannels[0];
        Assert.assertNotNull(fileChannel);
        fileChannel.close();
        fileIOEngine.refreshFileConnection(0, new IOException("Test Exception"));
        FileChannel[] reopenedFileChannels = fileIOEngine.getFileChannels();
        FileChannel reopenedFileChannel = reopenedFileChannels[0];
        Assert.assertNotEquals(fileChannel, reopenedFileChannel);
        Assert.assertEquals(fileChannels.length, reopenedFileChannels.length);
        for (int i = 1; i < (fileChannels.length); i++) {
            Assert.assertEquals(fileChannels[i], reopenedFileChannels[i]);
        }
    }
}

