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
package org.apache.hadoop.hdfs;


import java.io.IOException;
import java.util.Random;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.Time;
import org.junit.Test;


/**
 * Test cases for trying to append to a file with a different
 * checksum than the file was originally written with.
 */
public class TestAppendDifferentChecksum {
    private static final int SEGMENT_LENGTH = 1500;

    // run the randomized test for 5 seconds
    private static final long RANDOM_TEST_RUNTIME = 5000;

    private static MiniDFSCluster cluster;

    private static FileSystem fs;

    /**
     * Simple unit test which writes some data with one algorithm,
     * then appends with another.
     */
    @Test
    public void testSwitchAlgorithms() throws IOException {
        FileSystem fsWithCrc32 = createFsWithChecksum("CRC32", 512);
        FileSystem fsWithCrc32C = createFsWithChecksum("CRC32C", 512);
        Path p = new Path("/testSwitchAlgorithms");
        appendWithTwoFs(p, fsWithCrc32, fsWithCrc32C);
        // Regardless of which FS is used to read, it should pick up
        // the on-disk checksum!
        AppendTestUtil.check(fsWithCrc32C, p, ((TestAppendDifferentChecksum.SEGMENT_LENGTH) * 2));
        AppendTestUtil.check(fsWithCrc32, p, ((TestAppendDifferentChecksum.SEGMENT_LENGTH) * 2));
    }

    /**
     * Test which randomly alternates between appending with
     * CRC32 and with CRC32C, crossing several block boundaries.
     * Then, checks that all of the data can be read back correct.
     */
    @Test(timeout = (TestAppendDifferentChecksum.RANDOM_TEST_RUNTIME) * 2)
    public void testAlgoSwitchRandomized() throws IOException {
        FileSystem fsWithCrc32 = createFsWithChecksum("CRC32", 512);
        FileSystem fsWithCrc32C = createFsWithChecksum("CRC32C", 512);
        Path p = new Path("/testAlgoSwitchRandomized");
        long seed = Time.now();
        System.out.println(("seed: " + seed));
        Random r = new Random(seed);
        // Create empty to start
        IOUtils.closeStream(fsWithCrc32.create(p));
        long st = Time.now();
        int len = 0;
        while (((Time.now()) - st) < (TestAppendDifferentChecksum.RANDOM_TEST_RUNTIME)) {
            int thisLen = r.nextInt(500);
            FileSystem fs = (r.nextBoolean()) ? fsWithCrc32 : fsWithCrc32C;
            FSDataOutputStream stm = fs.append(p);
            try {
                AppendTestUtil.write(stm, len, thisLen);
            } finally {
                stm.close();
            }
            len += thisLen;
        } 
        AppendTestUtil.check(fsWithCrc32, p, len);
        AppendTestUtil.check(fsWithCrc32C, p, len);
    }
}

