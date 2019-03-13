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
package org.apache.hadoop.hbase.filter;


import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.testclassification.FilterTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static ReturnCode.INCLUDE;


/**
 * Test the invocation logic of the filters. A filter must be invoked only for
 * the columns that are requested for.
 */
@Category({ FilterTests.class, SmallTests.class })
public class TestInvocationRecordFilter {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestInvocationRecordFilter.class);

    private static final byte[] TABLE_NAME_BYTES = Bytes.toBytes("invocationrecord");

    private static final byte[] FAMILY_NAME_BYTES = Bytes.toBytes("mycf");

    private static final byte[] ROW_BYTES = Bytes.toBytes("row");

    private static final String QUALIFIER_PREFIX = "qualifier";

    private static final String VALUE_PREFIX = "value";

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private HRegion region;

    @Test
    public void testFilterInvocation() throws Exception {
        List<Integer> selectQualifiers = new ArrayList<>();
        List<Integer> expectedQualifiers = new ArrayList<>();
        selectQualifiers.add((-1));
        verifyInvocationResults(selectQualifiers.toArray(new Integer[selectQualifiers.size()]), expectedQualifiers.toArray(new Integer[expectedQualifiers.size()]));
        selectQualifiers.clear();
        selectQualifiers.add(0);
        expectedQualifiers.add(0);
        verifyInvocationResults(selectQualifiers.toArray(new Integer[selectQualifiers.size()]), expectedQualifiers.toArray(new Integer[expectedQualifiers.size()]));
        selectQualifiers.add(3);
        verifyInvocationResults(selectQualifiers.toArray(new Integer[selectQualifiers.size()]), expectedQualifiers.toArray(new Integer[expectedQualifiers.size()]));
        selectQualifiers.add(4);
        expectedQualifiers.add(4);
        verifyInvocationResults(selectQualifiers.toArray(new Integer[selectQualifiers.size()]), expectedQualifiers.toArray(new Integer[expectedQualifiers.size()]));
        selectQualifiers.add(5);
        verifyInvocationResults(selectQualifiers.toArray(new Integer[selectQualifiers.size()]), expectedQualifiers.toArray(new Integer[expectedQualifiers.size()]));
        selectQualifiers.add(8);
        expectedQualifiers.add(8);
        verifyInvocationResults(selectQualifiers.toArray(new Integer[selectQualifiers.size()]), expectedQualifiers.toArray(new Integer[expectedQualifiers.size()]));
    }

    /**
     * Filter which gives the list of keyvalues for which the filter is invoked.
     */
    private static class InvocationRecordFilter extends FilterBase {
        private List<Cell> visitedKeyValues = new ArrayList<>();

        @Override
        public void reset() {
            visitedKeyValues.clear();
        }

        @Override
        public ReturnCode filterCell(final Cell ignored) {
            visitedKeyValues.add(ignored);
            return INCLUDE;
        }

        @Override
        public void filterRowCells(List<Cell> kvs) {
            kvs.clear();
            kvs.addAll(visitedKeyValues);
        }

        @Override
        public boolean hasFilterRow() {
            return true;
        }
    }
}

