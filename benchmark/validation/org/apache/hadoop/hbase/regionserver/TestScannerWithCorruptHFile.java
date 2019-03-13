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
package org.apache.hadoop.hbase.regionserver;


import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.apache.hadoop.hbase.DoNotRetryIOException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.RegionObserver;
import org.apache.hadoop.hbase.io.hfile.CorruptHFileException;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


/**
 * Tests a scanner on a corrupt hfile.
 */
@Category(MediumTests.class)
public class TestScannerWithCorruptHFile {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestScannerWithCorruptHFile.class);

    @Rule
    public TestName name = new TestName();

    private static final byte[] FAMILY_NAME = Bytes.toBytes("f");

    private static final HBaseTestingUtility TEST_UTIL = HBaseTestingUtility.createLocalHTU();

    public static class CorruptHFileCoprocessor implements RegionCoprocessor , RegionObserver {
        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public boolean preScannerNext(ObserverContext<RegionCoprocessorEnvironment> e, InternalScanner s, List<Result> results, int limit, boolean hasMore) throws IOException {
            throw new CorruptHFileException("For test");
        }
    }

    @Test(expected = DoNotRetryIOException.class)
    public void testScanOnCorruptHFile() throws IOException {
        TableName tableName = TableName.valueOf(name.getMethodName());
        HTableDescriptor htd = new HTableDescriptor(tableName);
        htd.addCoprocessor(TestScannerWithCorruptHFile.CorruptHFileCoprocessor.class.getName());
        htd.addFamily(new HColumnDescriptor(TestScannerWithCorruptHFile.FAMILY_NAME));
        Table table = TestScannerWithCorruptHFile.TEST_UTIL.createTable(htd, null);
        try {
            loadTable(table, 1);
            scan(table);
        } finally {
            table.close();
        }
    }
}

