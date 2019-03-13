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
package org.apache.hadoop.hbase.util;


import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.io.hfile.TestHFile;
import org.apache.hadoop.hbase.mob.MobUtils;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.util.hbck.HFileCorruptionChecker;
import org.apache.hadoop.hbase.util.hbck.HbckTestingUtil;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MiscTests.class, LargeTests.class })
public class TestHBaseFsckMOB extends BaseTestHBaseFsck {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestHBaseFsckMOB.class);

    /**
     * This creates a table and then corrupts a mob file.  Hbck should quarantine the file.
     */
    @Test
    public void testQuarantineCorruptMobFile() throws Exception {
        TableName table = TableName.valueOf(name.getMethodName());
        try {
            setupMobTable(table);
            Assert.assertEquals(TestHBaseFsckMOB.ROWKEYS.length, countRows());
            BaseTestHBaseFsck.admin.flush(table);
            FileSystem fs = FileSystem.get(BaseTestHBaseFsck.conf);
            Path mobFile = getFlushedMobFile(fs, table);
            BaseTestHBaseFsck.admin.disableTable(table);
            // create new corrupt mob file.
            String corruptMobFile = createMobFileName(mobFile.getName());
            Path corrupt = new Path(mobFile.getParent(), corruptMobFile);
            TestHFile.truncateFile(fs, mobFile, corrupt);
            BaseTestHBaseFsck.LOG.info(("Created corrupted mob file " + corrupt));
            HBaseFsck.debugLsr(BaseTestHBaseFsck.conf, FSUtils.getRootDir(BaseTestHBaseFsck.conf));
            HBaseFsck.debugLsr(BaseTestHBaseFsck.conf, MobUtils.getMobHome(BaseTestHBaseFsck.conf));
            // A corrupt mob file doesn't abort the start of regions, so we can enable the table.
            BaseTestHBaseFsck.admin.enableTable(table);
            HBaseFsck res = HbckTestingUtil.doHFileQuarantine(BaseTestHBaseFsck.conf, table);
            Assert.assertEquals(0, res.getRetCode());
            HFileCorruptionChecker hfcc = res.getHFilecorruptionChecker();
            Assert.assertEquals(4, hfcc.getHFilesChecked());
            Assert.assertEquals(0, hfcc.getCorrupted().size());
            Assert.assertEquals(0, hfcc.getFailures().size());
            Assert.assertEquals(0, hfcc.getQuarantined().size());
            Assert.assertEquals(0, hfcc.getMissing().size());
            Assert.assertEquals(5, hfcc.getMobFilesChecked());
            Assert.assertEquals(1, hfcc.getCorruptedMobFiles().size());
            Assert.assertEquals(0, hfcc.getFailureMobFiles().size());
            Assert.assertEquals(1, hfcc.getQuarantinedMobFiles().size());
            Assert.assertEquals(0, hfcc.getMissedMobFiles().size());
            String quarantinedMobFile = hfcc.getQuarantinedMobFiles().iterator().next().getName();
            Assert.assertEquals(corruptMobFile, quarantinedMobFile);
        } finally {
            cleanupTable(table);
        }
    }
}

