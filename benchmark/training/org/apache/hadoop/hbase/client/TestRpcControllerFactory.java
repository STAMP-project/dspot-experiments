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


import HConstants.ADMIN_QOS;
import HConstants.HBASE_RPC_TIMEOUT_KEY;
import RpcControllerFactory.CUSTOM_CONTROLLER_CONF_KEY;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.CellScannable;
import org.apache.hadoop.hbase.CellScanner;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.ipc.DelegatingHBaseRpcController;
import org.apache.hadoop.hbase.ipc.HBaseRpcController;
import org.apache.hadoop.hbase.ipc.RpcControllerFactory;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hbase.thirdparty.com.google.common.collect.ConcurrentHashMultiset;
import org.apache.hbase.thirdparty.com.google.common.collect.Lists;
import org.apache.hbase.thirdparty.com.google.common.collect.Multiset;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


@Category({ MediumTests.class, ClientTests.class })
public class TestRpcControllerFactory {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRpcControllerFactory.class);

    public static class StaticRpcControllerFactory extends RpcControllerFactory {
        public StaticRpcControllerFactory(Configuration conf) {
            super(conf);
        }

        @Override
        public HBaseRpcController newController() {
            return new TestRpcControllerFactory.CountingRpcController(super.newController());
        }

        @Override
        public HBaseRpcController newController(final CellScanner cellScanner) {
            return new TestRpcControllerFactory.CountingRpcController(super.newController(cellScanner));
        }

        @Override
        public HBaseRpcController newController(final List<CellScannable> cellIterables) {
            return new TestRpcControllerFactory.CountingRpcController(super.newController(cellIterables));
        }
    }

    public static class CountingRpcController extends DelegatingHBaseRpcController {
        private static Multiset<Integer> GROUPED_PRIORITY = ConcurrentHashMultiset.create();

        private static AtomicInteger INT_PRIORITY = new AtomicInteger();

        private static AtomicInteger TABLE_PRIORITY = new AtomicInteger();

        public CountingRpcController(HBaseRpcController delegate) {
            super(delegate);
        }

        @Override
        public void setPriority(int priority) {
            int oldPriority = getPriority();
            super.setPriority(priority);
            int newPriority = getPriority();
            if (newPriority != oldPriority) {
                TestRpcControllerFactory.CountingRpcController.INT_PRIORITY.incrementAndGet();
                TestRpcControllerFactory.CountingRpcController.GROUPED_PRIORITY.add(priority);
            }
        }

        @Override
        public void setPriority(TableName tn) {
            super.setPriority(tn);
            // ignore counts for system tables - it could change and we really only want to check on what
            // the client should change
            if ((tn != null) && (!(tn.isSystemTable()))) {
                TestRpcControllerFactory.CountingRpcController.TABLE_PRIORITY.incrementAndGet();
            }
        }
    }

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    @Rule
    public TestName name = new TestName();

    /**
     * check some of the methods and make sure we are incrementing each time. Its a bit tediuous to
     * cover all methods here and really is a bit brittle since we can always add new methods but
     * won't be sure to add them here. So we just can cover the major ones.
     *
     * @throws Exception
     * 		on failure
     */
    @Test
    public void testCountController() throws Exception {
        Configuration conf = new Configuration(TestRpcControllerFactory.UTIL.getConfiguration());
        // setup our custom controller
        conf.set(CUSTOM_CONTROLLER_CONF_KEY, TestRpcControllerFactory.StaticRpcControllerFactory.class.getName());
        final TableName tableName = TableName.valueOf(name.getMethodName());
        TestRpcControllerFactory.UTIL.createTable(tableName, HBaseTestingUtility.fam1).close();
        // change one of the connection properties so we get a new Connection with our configuration
        conf.setInt(HBASE_RPC_TIMEOUT_KEY, ((HConstants.DEFAULT_HBASE_RPC_TIMEOUT) + 1));
        Connection connection = ConnectionFactory.createConnection(conf);
        Table table = connection.getTable(tableName);
        byte[] row = Bytes.toBytes("row");
        Put p = new Put(row);
        p.addColumn(HBaseTestingUtility.fam1, HBaseTestingUtility.fam1, Bytes.toBytes("val0"));
        table.put(p);
        Integer counter = 1;
        counter = verifyCount(counter);
        Delete d = new Delete(row);
        d.addColumn(HBaseTestingUtility.fam1, HBaseTestingUtility.fam1);
        table.delete(d);
        counter = verifyCount(counter);
        Put p2 = new Put(row);
        p2.addColumn(HBaseTestingUtility.fam1, Bytes.toBytes("qual"), Bytes.toBytes("val1"));
        table.batch(Lists.newArrayList(p, p2), null);
        // this only goes to a single server, so we don't need to change the count here
        counter = verifyCount(counter);
        Append append = new Append(row);
        append.addColumn(HBaseTestingUtility.fam1, HBaseTestingUtility.fam1, Bytes.toBytes("val2"));
        table.append(append);
        counter = verifyCount(counter);
        // and check the major lookup calls as well
        Get g = new Get(row);
        table.get(g);
        counter = verifyCount(counter);
        ResultScanner scan = table.getScanner(HBaseTestingUtility.fam1);
        scan.next();
        scan.close();
        counter = verifyCount((counter + 1));
        Get g2 = new Get(row);
        table.get(Lists.newArrayList(g, g2));
        // same server, so same as above for not changing count
        counter = verifyCount(counter);
        // make sure all the scanner types are covered
        Scan scanInfo = new Scan(row);
        // regular small
        scanInfo.setSmall(true);
        counter = doScan(table, scanInfo, counter);
        // reversed, small
        scanInfo.setReversed(true);
        counter = doScan(table, scanInfo, counter);
        // reversed, regular
        scanInfo.setSmall(false);
        counter = doScan(table, scanInfo, (counter + 1));
        // make sure we have no priority count
        verifyPriorityGroupCount(ADMIN_QOS, 0);
        // lets set a custom priority on a get
        Get get = new Get(row);
        get.setPriority(ADMIN_QOS);
        table.get(get);
        verifyPriorityGroupCount(ADMIN_QOS, 1);
        table.close();
        connection.close();
    }

    @Test
    public void testFallbackToDefaultRpcControllerFactory() {
        Configuration conf = new Configuration(TestRpcControllerFactory.UTIL.getConfiguration());
        conf.set(CUSTOM_CONTROLLER_CONF_KEY, "foo.bar.Baz");
        // Should not fail
        RpcControllerFactory factory = RpcControllerFactory.instantiate(conf);
        Assert.assertNotNull(factory);
        Assert.assertEquals(factory.getClass(), RpcControllerFactory.class);
    }
}

