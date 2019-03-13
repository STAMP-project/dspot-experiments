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
package org.apache.hadoop.mapred.gridmix;


import FilePool.GRIDMIX_MIN_FILE;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestFilePool {
    static final Logger LOG = LoggerFactory.getLogger(TestFileQueue.class);

    static final int NFILES = 26;

    static final Path base = TestFilePool.getBaseDir();

    @Test
    public void testUnsuitable() throws Exception {
        try {
            final Configuration conf = new Configuration();
            // all files 13k or less
            conf.setLong(GRIDMIX_MIN_FILE, (14 * 1024));
            final FilePool pool = new FilePool(conf, TestFilePool.base);
            pool.refresh();
        } catch (IOException e) {
            return;
        }
        Assert.fail();
    }

    @Test
    public void testPool() throws Exception {
        final Random r = new Random();
        final Configuration conf = new Configuration();
        conf.setLong(GRIDMIX_MIN_FILE, (3 * 1024));
        final FilePool pool = new FilePool(conf, TestFilePool.base);
        pool.refresh();
        final ArrayList<FileStatus> files = new ArrayList<FileStatus>();
        // ensure 1k, 2k files excluded
        final int expectedPoolSize = ((((TestFilePool.NFILES) / 2) * (((TestFilePool.NFILES) / 2) + 1)) - 6) * 1024;
        Assert.assertEquals(expectedPoolSize, pool.getInputFiles(Long.MAX_VALUE, files));
        Assert.assertEquals(((TestFilePool.NFILES) - 4), files.size());
        // exact match
        files.clear();
        Assert.assertEquals(expectedPoolSize, pool.getInputFiles(expectedPoolSize, files));
        // match random within 12k
        files.clear();
        final long rand = r.nextInt(expectedPoolSize);
        Assert.assertTrue(("Missed: " + rand), ((((TestFilePool.NFILES) / 2) * 1024) > (rand - (pool.getInputFiles(rand, files)))));
        // all files
        conf.setLong(GRIDMIX_MIN_FILE, 0);
        pool.refresh();
        files.clear();
        Assert.assertEquals(((((TestFilePool.NFILES) / 2) * (((TestFilePool.NFILES) / 2) + 1)) * 1024), pool.getInputFiles(Long.MAX_VALUE, files));
    }

    @Test
    public void testStriper() throws Exception {
        final Random r = new Random();
        final Configuration conf = new Configuration();
        final FileSystem fs = FileSystem.getLocal(conf).getRaw();
        conf.setLong(GRIDMIX_MIN_FILE, (3 * 1024));
        final FilePool pool = new FilePool(conf, TestFilePool.base) {
            @Override
            public BlockLocation[] locationsFor(FileStatus stat, long start, long len) throws IOException {
                return new BlockLocation[]{ new BlockLocation() };
            }
        };
        pool.refresh();
        final int expectedPoolSize = ((((TestFilePool.NFILES) / 2) * (((TestFilePool.NFILES) / 2) + 1)) - 6) * 1024;
        final InputStriper striper = new InputStriper(pool, expectedPoolSize);
        int last = 0;
        for (int i = 0; i < expectedPoolSize; last = Math.min((expectedPoolSize - i), r.nextInt(expectedPoolSize))) {
            checkSplitEq(fs, striper.splitFor(pool, last, 0), last);
            i += last;
        }
        final InputStriper striper2 = new InputStriper(pool, expectedPoolSize);
        checkSplitEq(fs, striper2.splitFor(pool, expectedPoolSize, 0), expectedPoolSize);
    }
}

