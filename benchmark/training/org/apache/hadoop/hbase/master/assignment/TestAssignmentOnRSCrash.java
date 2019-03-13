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
package org.apache.hadoop.hbase.master.assignment;


import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, LargeTests.class })
public class TestAssignmentOnRSCrash {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAssignmentOnRSCrash.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestAssignmentOnRSCrash.class);

    private static final TableName TEST_TABLE = TableName.valueOf("testb");

    private static final String FAMILY_STR = "f";

    private static final byte[] FAMILY = Bytes.toBytes(TestAssignmentOnRSCrash.FAMILY_STR);

    private static final int NUM_RS = 3;

    private HBaseTestingUtility UTIL;

    @Test
    public void testKillRsWithUserRegionWithData() throws Exception {
        testCrashRsWithUserRegion(true, true);
    }

    @Test
    public void testKillRsWithUserRegionWithoutData() throws Exception {
        testCrashRsWithUserRegion(true, false);
    }

    @Test
    public void testStopRsWithUserRegionWithData() throws Exception {
        testCrashRsWithUserRegion(false, true);
    }

    @Test
    public void testStopRsWithUserRegionWithoutData() throws Exception {
        testCrashRsWithUserRegion(false, false);
    }

    @Test
    public void testKillRsWithMetaRegion() throws Exception {
        testCrashRsWithMetaRegion(true);
    }

    @Test
    public void testStopRsWithMetaRegion() throws Exception {
        testCrashRsWithMetaRegion(false);
    }
}

