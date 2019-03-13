/**
 * Copyright 2019 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.channel;


import io.netty.util.internal.PlatformDependent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import org.junit.Assert;
import org.junit.Test;


public class DefaultFileRegionTest {
    private static final byte[] data = new byte[1048576 * 10];

    static {
        PlatformDependent.threadLocalRandom().nextBytes(DefaultFileRegionTest.data);
    }

    @Test
    public void testCreateFromFile() throws IOException {
        File file = DefaultFileRegionTest.newFile();
        try {
            DefaultFileRegionTest.testFileRegion(new DefaultFileRegion(file, 0, DefaultFileRegionTest.data.length));
        } finally {
            file.delete();
        }
    }

    @Test
    public void testCreateFromFileChannel() throws IOException {
        File file = DefaultFileRegionTest.newFile();
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        try {
            DefaultFileRegionTest.testFileRegion(new DefaultFileRegion(randomAccessFile.getChannel(), 0, DefaultFileRegionTest.data.length));
        } finally {
            randomAccessFile.close();
            file.delete();
        }
    }

    @Test
    public void testTruncated() throws IOException {
        File file = DefaultFileRegionTest.newFile();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        WritableByteChannel channel = Channels.newChannel(outputStream);
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        try {
            FileRegion region = new DefaultFileRegion(randomAccessFile.getChannel(), 0, DefaultFileRegionTest.data.length);
            randomAccessFile.getChannel().truncate(((DefaultFileRegionTest.data.length) - 1024));
            Assert.assertEquals(DefaultFileRegionTest.data.length, region.count());
            Assert.assertEquals(0, region.transferred());
            Assert.assertEquals(((DefaultFileRegionTest.data.length) - 1024), region.transferTo(channel, 0));
            Assert.assertEquals(DefaultFileRegionTest.data.length, region.count());
            Assert.assertEquals(((DefaultFileRegionTest.data.length) - 1024), region.transferred());
            try {
                region.transferTo(channel, ((DefaultFileRegionTest.data.length) - 1024));
                Assert.fail();
            } catch (IOException expected) {
                // expected
            }
        } finally {
            channel.close();
            randomAccessFile.close();
            file.delete();
        }
    }
}

