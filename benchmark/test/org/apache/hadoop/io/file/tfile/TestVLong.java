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
package org.apache.hadoop.io.file.tfile;


import java.io.IOException;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestVLong {
    private static String ROOT = GenericTestUtils.getTestDir().getAbsolutePath();

    private Configuration conf;

    private FileSystem fs;

    private Path path;

    private String outputFile = "TestVLong";

    @Test
    public void testVLongByte() throws IOException {
        FSDataOutputStream out = fs.create(path);
        for (int i = Byte.MIN_VALUE; i <= (Byte.MAX_VALUE); ++i) {
            Utils.writeVLong(out, i);
        }
        out.close();
        Assert.assertEquals("Incorrect encoded size", ((1 << (Byte.SIZE)) + 96), fs.getFileStatus(path).getLen());
        FSDataInputStream in = fs.open(path);
        for (int i = Byte.MIN_VALUE; i <= (Byte.MAX_VALUE); ++i) {
            long n = Utils.readVLong(in);
            Assert.assertEquals(n, i);
        }
        in.close();
        fs.delete(path, false);
    }

    @Test
    public void testVLongShort() throws IOException {
        long size = writeAndVerify(0);
        Assert.assertEquals("Incorrect encoded size", (((((1 << (Short.SIZE)) * 2) + (((1 << (Byte.SIZE)) - 40) * (1 << (Byte.SIZE)))) - 128) - 32), size);
    }

    @Test
    public void testVLong3Bytes() throws IOException {
        long size = writeAndVerify(Byte.SIZE);
        Assert.assertEquals("Incorrect encoded size", (((((1 << (Short.SIZE)) * 3) + (((1 << (Byte.SIZE)) - 32) * (1 << (Byte.SIZE)))) - 40) - 1), size);
    }

    @Test
    public void testVLong4Bytes() throws IOException {
        long size = writeAndVerify(((Byte.SIZE) * 2));
        Assert.assertEquals("Incorrect encoded size", (((((1 << (Short.SIZE)) * 4) + (((1 << (Byte.SIZE)) - 16) * (1 << (Byte.SIZE)))) - 32) - 2), size);
    }

    @Test
    public void testVLong5Bytes() throws IOException {
        long size = writeAndVerify(((Byte.SIZE) * 3));
        Assert.assertEquals("Incorrect encoded size", (((((1 << (Short.SIZE)) * 6) - 256) - 16) - 3), size);
    }

    @Test
    public void testVLong6Bytes() throws IOException {
        verifySixOrMoreBytes(6);
    }

    @Test
    public void testVLong7Bytes() throws IOException {
        verifySixOrMoreBytes(7);
    }

    @Test
    public void testVLong8Bytes() throws IOException {
        verifySixOrMoreBytes(8);
    }

    @Test
    public void testVLongRandom() throws IOException {
        int count = 1024 * 1024;
        long[] data = new long[count];
        Random rng = new Random();
        for (int i = 0; i < (data.length); ++i) {
            int shift = (rng.nextInt(Long.SIZE)) + 1;
            long mask = (1L << shift) - 1;
            long a = ((long) (rng.nextInt())) << 32;
            long b = ((long) (rng.nextInt())) & 4294967295L;
            data[i] = (a + b) & mask;
        }
        FSDataOutputStream out = fs.create(path);
        for (int i = 0; i < (data.length); ++i) {
            Utils.writeVLong(out, data[i]);
        }
        out.close();
        FSDataInputStream in = fs.open(path);
        for (int i = 0; i < (data.length); ++i) {
            Assert.assertEquals(Utils.readVLong(in), data[i]);
        }
        in.close();
        fs.delete(path, false);
    }
}

