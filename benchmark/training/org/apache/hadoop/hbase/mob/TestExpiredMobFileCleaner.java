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
package org.apache.hadoop.hbase.mob;


import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.util.ToolRunner;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(MediumTests.class)
public class TestExpiredMobFileCleaner {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestExpiredMobFileCleaner.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final TableName tableName = TableName.valueOf("TestExpiredMobFileCleaner");

    private static final String family = "family";

    private static final byte[] row1 = Bytes.toBytes("row1");

    private static final byte[] row2 = Bytes.toBytes("row2");

    private static final byte[] qf = Bytes.toBytes("qf");

    private static BufferedMutator table;

    private static Admin admin;

    /**
     * Creates a 3 day old hfile and an 1 day old hfile then sets expiry to 2 days.
     * Verifies that the 3 day old hfile is removed but the 1 day one is still present
     * after the expiry based cleaner is run.
     */
    @Test
    public void testCleaner() throws Exception {
        init();
        Path mobDirPath = MobUtils.getMobFamilyPath(TestExpiredMobFileCleaner.TEST_UTIL.getConfiguration(), TestExpiredMobFileCleaner.tableName, TestExpiredMobFileCleaner.family);
        byte[] dummyData = makeDummyData(600);
        long ts = (System.currentTimeMillis()) - ((3 * (secondsOfDay())) * 1000);// 3 days before

        putKVAndFlush(TestExpiredMobFileCleaner.table, TestExpiredMobFileCleaner.row1, dummyData, ts);
        FileStatus[] firstFiles = TestExpiredMobFileCleaner.TEST_UTIL.getTestFileSystem().listStatus(mobDirPath);
        // the first mob file
        Assert.assertEquals("Before cleanup without delay 1", 1, firstFiles.length);
        String firstFile = firstFiles[0].getPath().getName();
        ts = (System.currentTimeMillis()) - ((1 * (secondsOfDay())) * 1000);// 1 day before

        putKVAndFlush(TestExpiredMobFileCleaner.table, TestExpiredMobFileCleaner.row2, dummyData, ts);
        FileStatus[] secondFiles = TestExpiredMobFileCleaner.TEST_UTIL.getTestFileSystem().listStatus(mobDirPath);
        // now there are 2 mob files
        Assert.assertEquals("Before cleanup without delay 2", 2, secondFiles.length);
        String f1 = secondFiles[0].getPath().getName();
        String f2 = secondFiles[1].getPath().getName();
        String secondFile = (f1.equals(firstFile)) ? f2 : f1;
        modifyColumnExpiryDays(2);// ttl = 2, make the first row expired

        // run the cleaner
        String[] args = new String[2];
        args[0] = TestExpiredMobFileCleaner.tableName.getNameAsString();
        args[1] = TestExpiredMobFileCleaner.family;
        ToolRunner.run(TestExpiredMobFileCleaner.TEST_UTIL.getConfiguration(), new ExpiredMobFileCleaner(), args);
        FileStatus[] filesAfterClean = TestExpiredMobFileCleaner.TEST_UTIL.getTestFileSystem().listStatus(mobDirPath);
        String lastFile = filesAfterClean[0].getPath().getName();
        // the first mob fie is removed
        Assert.assertEquals("After cleanup without delay 1", 1, filesAfterClean.length);
        Assert.assertEquals("After cleanup without delay 2", secondFile, lastFile);
    }
}

