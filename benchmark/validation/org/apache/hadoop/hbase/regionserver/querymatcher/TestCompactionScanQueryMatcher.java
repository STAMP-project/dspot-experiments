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
package org.apache.hadoop.hbase.regionserver.querymatcher;


import HConstants.EMPTY_END_ROW;
import HConstants.EMPTY_START_ROW;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ RegionServerTests.class, SmallTests.class })
public class TestCompactionScanQueryMatcher extends AbstractTestScanQueryMatcher {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCompactionScanQueryMatcher.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestCompactionScanQueryMatcher.class);

    @Test
    public void testMatch_PartialRangeDropDeletes() throws Exception {
        // Some ranges.
        testDropDeletes(row2, row3, new byte[][]{ row1, row2, row2, row3 }, MatchCode.INCLUDE, MatchCode.SKIP, MatchCode.SKIP, MatchCode.INCLUDE);
        testDropDeletes(row2, row3, new byte[][]{ row1, row1, row2 }, MatchCode.INCLUDE, MatchCode.INCLUDE, MatchCode.SKIP);
        testDropDeletes(row2, row3, new byte[][]{ row2, row3, row3 }, MatchCode.SKIP, MatchCode.INCLUDE, MatchCode.INCLUDE);
        testDropDeletes(row1, row3, new byte[][]{ row1, row2, row3 }, MatchCode.SKIP, MatchCode.SKIP, MatchCode.INCLUDE);
        // Open ranges.
        testDropDeletes(EMPTY_START_ROW, row3, new byte[][]{ row1, row2, row3 }, MatchCode.SKIP, MatchCode.SKIP, MatchCode.INCLUDE);
        testDropDeletes(row2, EMPTY_END_ROW, new byte[][]{ row1, row2, row3 }, MatchCode.INCLUDE, MatchCode.SKIP, MatchCode.SKIP);
        testDropDeletes(EMPTY_START_ROW, EMPTY_END_ROW, new byte[][]{ row1, row2, row3, row3 }, MatchCode.SKIP, MatchCode.SKIP, MatchCode.SKIP, MatchCode.SKIP);
        // No KVs in range.
        testDropDeletes(row2, row3, new byte[][]{ row1, row1, row3 }, MatchCode.INCLUDE, MatchCode.INCLUDE, MatchCode.INCLUDE);
        testDropDeletes(row2, row3, new byte[][]{ row3, row3 }, MatchCode.INCLUDE, MatchCode.INCLUDE);
        testDropDeletes(row2, row3, new byte[][]{ row1, row1 }, MatchCode.INCLUDE, MatchCode.INCLUDE);
    }
}

