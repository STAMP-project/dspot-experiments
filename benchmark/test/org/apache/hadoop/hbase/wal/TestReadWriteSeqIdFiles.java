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
package org.apache.hadoop.hbase.wal;


import java.io.IOException;
import java.util.NavigableSet;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseCommonTestingUtility;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.FSUtils;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ RegionServerTests.class, SmallTests.class })
public class TestReadWriteSeqIdFiles {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestReadWriteSeqIdFiles.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestReadWriteSeqIdFiles.class);

    private static final HBaseCommonTestingUtility UTIL = new HBaseCommonTestingUtility();

    private static FileSystem walFS;

    private static Path REGION_DIR;

    @Test
    public void test() throws IOException {
        WALSplitter.writeRegionSequenceIdFile(TestReadWriteSeqIdFiles.walFS, TestReadWriteSeqIdFiles.REGION_DIR, 1000L);
        Assert.assertEquals(1000L, WALSplitter.getMaxRegionSequenceId(TestReadWriteSeqIdFiles.walFS, TestReadWriteSeqIdFiles.REGION_DIR));
        WALSplitter.writeRegionSequenceIdFile(TestReadWriteSeqIdFiles.walFS, TestReadWriteSeqIdFiles.REGION_DIR, 2000L);
        Assert.assertEquals(2000L, WALSplitter.getMaxRegionSequenceId(TestReadWriteSeqIdFiles.walFS, TestReadWriteSeqIdFiles.REGION_DIR));
        // can not write a sequence id which is smaller
        try {
            WALSplitter.writeRegionSequenceIdFile(TestReadWriteSeqIdFiles.walFS, TestReadWriteSeqIdFiles.REGION_DIR, 1500L);
        } catch (IOException e) {
            // expected
            TestReadWriteSeqIdFiles.LOG.info("Expected error", e);
        }
        Path editsdir = WALSplitter.getRegionDirRecoveredEditsDir(TestReadWriteSeqIdFiles.REGION_DIR);
        FileStatus[] files = FSUtils.listStatus(TestReadWriteSeqIdFiles.walFS, editsdir, new PathFilter() {
            @Override
            public boolean accept(Path p) {
                return WALSplitter.isSequenceIdFile(p);
            }
        });
        // only one seqid file should exist
        Assert.assertEquals(1, files.length);
        // verify all seqId files aren't treated as recovered.edits files
        NavigableSet<Path> recoveredEdits = WALSplitter.getSplitEditFilesSorted(TestReadWriteSeqIdFiles.walFS, TestReadWriteSeqIdFiles.REGION_DIR);
        Assert.assertEquals(0, recoveredEdits.size());
    }
}

