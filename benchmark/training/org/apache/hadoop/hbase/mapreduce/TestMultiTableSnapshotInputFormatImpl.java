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
package org.apache.hadoop.hbase.mapreduce;


import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@Category({ SmallTests.class })
public class TestMultiTableSnapshotInputFormatImpl {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMultiTableSnapshotInputFormatImpl.class);

    private MultiTableSnapshotInputFormatImpl subject;

    private Map<String, Collection<Scan>> snapshotScans;

    private Path restoreDir;

    private Configuration conf;

    private Path rootDir;

    public static class ScanWithEquals {
        private final String startRow;

        private final String stopRow;

        /**
         * Creates a new instance of this class while copying all values.
         *
         * @param scan
         * 		The scan instance to copy from.
         * @throws java.io.IOException
         * 		When copying the values fails.
         */
        public ScanWithEquals(Scan scan) throws IOException {
            this.startRow = Bytes.toStringBinary(scan.getStartRow());
            this.stopRow = Bytes.toStringBinary(scan.getStopRow());
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof TestMultiTableSnapshotInputFormatImpl.ScanWithEquals)) {
                return false;
            }
            TestMultiTableSnapshotInputFormatImpl.ScanWithEquals otherScan = ((TestMultiTableSnapshotInputFormatImpl.ScanWithEquals) (obj));
            return (Objects.equals(this.startRow, otherScan.startRow)) && (Objects.equals(this.stopRow, otherScan.stopRow));
        }

        @Override
        public String toString() {
            return org.apache.hbase.thirdparty.com.google.common.base.MoreObjects.toStringHelper(this).add("startRow", startRow).add("stopRow", stopRow).toString();
        }
    }

    @Test
    public void testSetInputSetsSnapshotToScans() throws Exception {
        callSetInput();
        Map<String, Collection<Scan>> actual = subject.getSnapshotsToScans(conf);
        // convert to scans we can use .equals on
        Map<String, Collection<TestMultiTableSnapshotInputFormatImpl.ScanWithEquals>> actualWithEquals = toScanWithEquals(actual);
        Map<String, Collection<TestMultiTableSnapshotInputFormatImpl.ScanWithEquals>> expectedWithEquals = toScanWithEquals(snapshotScans);
        Assert.assertEquals(expectedWithEquals, actualWithEquals);
    }

    @Test
    public void testSetInputPushesRestoreDirectories() throws Exception {
        callSetInput();
        Map<String, Path> restoreDirs = subject.getSnapshotDirs(conf);
        Assert.assertEquals(this.snapshotScans.keySet(), restoreDirs.keySet());
    }

    @Test
    public void testSetInputCreatesRestoreDirectoriesUnderRootRestoreDir() throws Exception {
        callSetInput();
        Map<String, Path> restoreDirs = subject.getSnapshotDirs(conf);
        for (Path snapshotDir : restoreDirs.values()) {
            Assert.assertEquals(((("Expected " + snapshotDir) + " to be a child of ") + (restoreDir)), restoreDir, snapshotDir.getParent());
        }
    }

    @Test
    public void testSetInputRestoresSnapshots() throws Exception {
        callSetInput();
        Map<String, Path> snapshotDirs = subject.getSnapshotDirs(conf);
        for (Map.Entry<String, Path> entry : snapshotDirs.entrySet()) {
            Mockito.verify(this.subject).restoreSnapshot(ArgumentMatchers.eq(this.conf), ArgumentMatchers.eq(entry.getKey()), ArgumentMatchers.eq(this.rootDir), ArgumentMatchers.eq(entry.getValue()), ArgumentMatchers.any());
        }
    }
}

