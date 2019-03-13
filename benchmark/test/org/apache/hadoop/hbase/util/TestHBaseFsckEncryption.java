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


import java.security.Key;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.util.hbck.HFileCorruptionChecker;
import org.apache.hadoop.hbase.util.hbck.HbckTestingUtil;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MiscTests.class, LargeTests.class })
public class TestHBaseFsckEncryption {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestHBaseFsckEncryption.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private Configuration conf;

    private HTableDescriptor htd;

    private Key cfKey;

    @Test
    public void testFsckWithEncryption() throws Exception {
        // Populate the table with some data
        Table table = TestHBaseFsckEncryption.TEST_UTIL.getConnection().getTable(htd.getTableName());
        try {
            byte[] values = new byte[]{ 'A', 'B', 'C', 'D' };
            for (int i = 0; i < (values.length); i++) {
                for (int j = 0; j < (values.length); j++) {
                    Put put = new Put(new byte[]{ values[i], values[j] });
                    put.addColumn(Bytes.toBytes("cf"), new byte[]{  }, new byte[]{ values[i], values[j] });
                    table.put(put);
                }
            }
        } finally {
            table.close();
        }
        // Flush it
        TestHBaseFsckEncryption.TEST_UTIL.getAdmin().flush(htd.getTableName());
        // Verify we have encrypted store files on disk
        final List<Path> paths = findStorefilePaths(htd.getTableName());
        Assert.assertTrue(((paths.size()) > 0));
        for (Path path : paths) {
            Assert.assertTrue((("Store file " + path) + " has incorrect key"), Bytes.equals(cfKey.getEncoded(), extractHFileKey(path)));
        }
        // Insure HBck doesn't consider them corrupt
        HBaseFsck res = HbckTestingUtil.doHFileQuarantine(conf, htd.getTableName());
        Assert.assertEquals(0, res.getRetCode());
        HFileCorruptionChecker hfcc = res.getHFilecorruptionChecker();
        Assert.assertEquals(0, hfcc.getCorrupted().size());
        Assert.assertEquals(0, hfcc.getFailures().size());
        Assert.assertEquals(0, hfcc.getQuarantined().size());
        Assert.assertEquals(0, hfcc.getMissing().size());
    }
}

