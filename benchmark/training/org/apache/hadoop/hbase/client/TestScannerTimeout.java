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
package org.apache.hadoop.hbase.client;


import HConstants.HBASE_CLIENT_SCANNER_TIMEOUT_PERIOD;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.MetaTableAccessor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test various scanner timeout issues.
 */
@Category({ LargeTests.class, ClientTests.class })
public class TestScannerTimeout {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestScannerTimeout.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final Logger LOG = LoggerFactory.getLogger(TestScannerTimeout.class);

    private static final byte[] SOME_BYTES = Bytes.toBytes("f");

    private static final TableName TABLE_NAME = TableName.valueOf("t");

    private static final int NB_ROWS = 10;

    // Be careful w/ what you set this timer to... it can get in the way of
    // the mini cluster coming up -- the verification in particular.
    private static final int THREAD_WAKE_FREQUENCY = 1000;

    private static final int SCANNER_TIMEOUT = 15000;

    private static final int SCANNER_CACHING = 5;

    /**
     * Test that scanner can continue even if the region server it was reading
     * from failed. Before 2772, it reused the same scanner id.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void test2772() throws Exception {
        TestScannerTimeout.LOG.info("START************ test2772");
        HRegionServer rs = TestScannerTimeout.TEST_UTIL.getRSForFirstRegionInTable(TestScannerTimeout.TABLE_NAME);
        Scan scan = new Scan();
        // Set a very high timeout, we want to test what happens when a RS
        // fails but the region is recovered before the lease times out.
        // Since the RS is already created, this conf is client-side only for
        // this new table
        Configuration conf = new Configuration(TestScannerTimeout.TEST_UTIL.getConfiguration());
        conf.setInt(HBASE_CLIENT_SCANNER_TIMEOUT_PERIOD, ((TestScannerTimeout.SCANNER_TIMEOUT) * 100));
        Connection connection = ConnectionFactory.createConnection(conf);
        Table higherScanTimeoutTable = connection.getTable(TestScannerTimeout.TABLE_NAME);
        ResultScanner r = higherScanTimeoutTable.getScanner(scan);
        // This takes way less than SCANNER_TIMEOUT*100
        rs.abort("die!");
        Result[] results = r.next(TestScannerTimeout.NB_ROWS);
        Assert.assertEquals(TestScannerTimeout.NB_ROWS, results.length);
        r.close();
        higherScanTimeoutTable.close();
        connection.close();
        TestScannerTimeout.LOG.info("END ************ test2772");
    }

    /**
     * Test that scanner won't miss any rows if the region server it was reading
     * from failed. Before 3686, it would skip rows in the scan.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void test3686a() throws Exception {
        TestScannerTimeout.LOG.info("START ************ TEST3686A---1");
        HRegionServer rs = TestScannerTimeout.TEST_UTIL.getRSForFirstRegionInTable(TestScannerTimeout.TABLE_NAME);
        TestScannerTimeout.LOG.info("START ************ TEST3686A---1111");
        Scan scan = new Scan();
        scan.setCaching(TestScannerTimeout.SCANNER_CACHING);
        TestScannerTimeout.LOG.info("************ TEST3686A");
        MetaTableAccessor.fullScanMetaAndPrint(TestScannerTimeout.TEST_UTIL.getAdmin().getConnection());
        // Set a very high timeout, we want to test what happens when a RS
        // fails but the region is recovered before the lease times out.
        // Since the RS is already created, this conf is client-side only for
        // this new table
        Configuration conf = new Configuration(TestScannerTimeout.TEST_UTIL.getConfiguration());
        conf.setInt(HBASE_CLIENT_SCANNER_TIMEOUT_PERIOD, ((TestScannerTimeout.SCANNER_TIMEOUT) * 100));
        Connection connection = ConnectionFactory.createConnection(conf);
        Table table = connection.getTable(TestScannerTimeout.TABLE_NAME);
        TestScannerTimeout.LOG.info("START ************ TEST3686A---22");
        ResultScanner r = table.getScanner(scan);
        TestScannerTimeout.LOG.info("START ************ TEST3686A---33");
        int count = 1;
        r.next();
        TestScannerTimeout.LOG.info("START ************ TEST3686A---44");
        // Kill after one call to next(), which got 5 rows.
        rs.abort("die!");
        while ((r.next()) != null) {
            count++;
        } 
        Assert.assertEquals(TestScannerTimeout.NB_ROWS, count);
        r.close();
        table.close();
        connection.close();
        TestScannerTimeout.LOG.info("************ END TEST3686A");
    }

    /**
     * Make sure that no rows are lost if the scanner timeout is longer on the
     * client than the server, and the scan times out on the server but not the
     * client.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void test3686b() throws Exception {
        TestScannerTimeout.LOG.info("START ************ test3686b");
        HRegionServer rs = TestScannerTimeout.TEST_UTIL.getRSForFirstRegionInTable(TestScannerTimeout.TABLE_NAME);
        Scan scan = new Scan();
        scan.setCaching(TestScannerTimeout.SCANNER_CACHING);
        // Set a very high timeout, we want to test what happens when a RS
        // fails but the region is recovered before the lease times out.
        // Since the RS is already created, this conf is client-side only for
        // this new table
        Configuration conf = new Configuration(TestScannerTimeout.TEST_UTIL.getConfiguration());
        conf.setInt(HBASE_CLIENT_SCANNER_TIMEOUT_PERIOD, ((TestScannerTimeout.SCANNER_TIMEOUT) * 100));
        Connection connection = ConnectionFactory.createConnection(conf);
        Table higherScanTimeoutTable = connection.getTable(TestScannerTimeout.TABLE_NAME);
        ResultScanner r = higherScanTimeoutTable.getScanner(scan);
        int count = 1;
        r.next();
        // Sleep, allowing the scan to timeout on the server but not on the client.
        Thread.sleep(((TestScannerTimeout.SCANNER_TIMEOUT) + 2000));
        while ((r.next()) != null) {
            count++;
        } 
        Assert.assertEquals(TestScannerTimeout.NB_ROWS, count);
        r.close();
        higherScanTimeoutTable.close();
        connection.close();
        TestScannerTimeout.LOG.info("END ************ END test3686b");
    }
}

