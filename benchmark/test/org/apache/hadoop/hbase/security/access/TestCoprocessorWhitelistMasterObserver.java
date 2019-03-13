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
package org.apache.hadoop.hbase.security.access;


import Coprocessor.PRIORITY_USER;
import CoprocessorHost.MASTER_COPROCESSOR_CONF_KEY;
import CoprocessorWhitelistMasterObserver.CP_COPROCESSOR_WHITELIST_PATHS_KEY;
import java.io.IOException;
import java.util.Optional;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionObserver;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.SecurityTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Performs coprocessor loads for various paths and malformed strings
 */
@Category({ SecurityTests.class, LargeTests.class })
public class TestCoprocessorWhitelistMasterObserver extends SecureTestUtil {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCoprocessorWhitelistMasterObserver.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestCoprocessorWhitelistMasterObserver.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static final TableName TEST_TABLE = TableName.valueOf("testTable");

    private static final byte[] TEST_FAMILY = Bytes.toBytes("fam1");

    /**
     * Test a table modification adding a coprocessor path
     * which is not whitelisted
     *
     * @unknown An IOException should be thrown and caught
    to show coprocessor is working as desired
     */
    @Test
    public void testSubstringNonWhitelisted() throws Exception {
        TestCoprocessorWhitelistMasterObserver.positiveTestCase(new String[]{ "/permitted/*" }, "file:///notpermitted/couldnotpossiblyexist.jar");
    }

    /**
     * Test a table creation including a coprocessor path
     * which is not whitelisted
     *
     * @unknown Coprocessor should be added to table descriptor
    Table is disabled to avoid an IOException due to
    the added coprocessor not actually existing on disk
     */
    @Test
    public void testDifferentFileSystemNonWhitelisted() throws Exception {
        TestCoprocessorWhitelistMasterObserver.positiveTestCase(new String[]{ "hdfs://foo/bar" }, "file:///notpermitted/couldnotpossiblyexist.jar");
    }

    /**
     * Test a table modification adding a coprocessor path
     * which is whitelisted
     *
     * @unknown Coprocessor should be added to table descriptor
    Table is disabled to avoid an IOException due to
    the added coprocessor not actually existing on disk
     */
    @Test
    public void testSchemeAndDirectorywhitelisted() throws Exception {
        TestCoprocessorWhitelistMasterObserver.negativeTestCase(new String[]{ "/tmp", "file:///permitted/*" }, "file:///permitted/couldnotpossiblyexist.jar");
    }

    /**
     * Test a table modification adding a coprocessor path
     * which is whitelisted
     *
     * @unknown Coprocessor should be added to table descriptor
    Table is disabled to avoid an IOException due to
    the added coprocessor not actually existing on disk
     */
    @Test
    public void testSchemeWhitelisted() throws Exception {
        TestCoprocessorWhitelistMasterObserver.negativeTestCase(new String[]{ "file:///" }, "file:///permitted/couldnotpossiblyexist.jar");
    }

    /**
     * Test a table modification adding a coprocessor path
     * which is whitelisted
     *
     * @unknown Coprocessor should be added to table descriptor
    Table is disabled to avoid an IOException due to
    the added coprocessor not actually existing on disk
     */
    @Test
    public void testDFSNameWhitelistedWorks() throws Exception {
        TestCoprocessorWhitelistMasterObserver.negativeTestCase(new String[]{ "hdfs://Your-FileSystem" }, "hdfs://Your-FileSystem/permitted/couldnotpossiblyexist.jar");
    }

    /**
     * Test a table modification adding a coprocessor path
     * which is whitelisted
     *
     * @unknown Coprocessor should be added to table descriptor
    Table is disabled to avoid an IOException due to
    the added coprocessor not actually existing on disk
     */
    @Test
    public void testDFSNameNotWhitelistedFails() throws Exception {
        TestCoprocessorWhitelistMasterObserver.positiveTestCase(new String[]{ "hdfs://Your-FileSystem" }, "hdfs://My-FileSystem/permitted/couldnotpossiblyexist.jar");
    }

    /**
     * Test a table modification adding a coprocessor path
     * which is whitelisted
     *
     * @unknown Coprocessor should be added to table descriptor
    Table is disabled to avoid an IOException due to
    the added coprocessor not actually existing on disk
     */
    @Test
    public void testBlanketWhitelist() throws Exception {
        TestCoprocessorWhitelistMasterObserver.negativeTestCase(new String[]{ "*" }, "hdfs:///permitted/couldnotpossiblyexist.jar");
    }

    /**
     * Test a table creation including a coprocessor path
     * which is not whitelisted
     *
     * @unknown Table will not be created due to the offending coprocessor
     */
    @Test
    public void testCreationNonWhitelistedCoprocessorPath() throws Exception {
        Configuration conf = TestCoprocessorWhitelistMasterObserver.UTIL.getConfiguration();
        // load coprocessor under test
        conf.set(MASTER_COPROCESSOR_CONF_KEY, CoprocessorWhitelistMasterObserver.class.getName());
        conf.setStrings(CP_COPROCESSOR_WHITELIST_PATHS_KEY, new String[]{  });
        // set retries low to raise exception quickly
        conf.setInt("hbase.client.retries.number", 5);
        TestCoprocessorWhitelistMasterObserver.UTIL.startMiniCluster();
        HTableDescriptor htd = new HTableDescriptor(TestCoprocessorWhitelistMasterObserver.TEST_TABLE);
        HColumnDescriptor hcd = new HColumnDescriptor(TestCoprocessorWhitelistMasterObserver.TEST_FAMILY);
        htd.addFamily(hcd);
        htd.addCoprocessor("net.clayb.hbase.coprocessor.NotWhitelisted", new Path("file:///notpermitted/couldnotpossiblyexist.jar"), PRIORITY_USER, null);
        Connection connection = ConnectionFactory.createConnection(conf);
        Admin admin = connection.getAdmin();
        TestCoprocessorWhitelistMasterObserver.LOG.info("Creating Table");
        try {
            admin.createTable(htd);
            Assert.fail("Expected coprocessor to raise IOException");
        } catch (IOException e) {
            // swallow exception from coprocessor
        }
        TestCoprocessorWhitelistMasterObserver.LOG.info("Done Creating Table");
        // ensure table was not created
        Assert.assertEquals(new HTableDescriptor[0], admin.listTables((("^" + (TestCoprocessorWhitelistMasterObserver.TEST_TABLE.getNameAsString())) + "$")));
    }

    public static class TestRegionObserver implements RegionCoprocessor , RegionObserver {
        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }
    }

    /**
     * Test a table creation including a coprocessor path
     * which is on the classpath
     *
     * @unknown Table will be created with the coprocessor
     */
    @Test
    public void testCreationClasspathCoprocessor() throws Exception {
        Configuration conf = TestCoprocessorWhitelistMasterObserver.UTIL.getConfiguration();
        // load coprocessor under test
        conf.set(MASTER_COPROCESSOR_CONF_KEY, CoprocessorWhitelistMasterObserver.class.getName());
        conf.setStrings(CP_COPROCESSOR_WHITELIST_PATHS_KEY, new String[]{  });
        // set retries low to raise exception quickly
        conf.setInt("hbase.client.retries.number", 5);
        TestCoprocessorWhitelistMasterObserver.UTIL.startMiniCluster();
        HTableDescriptor htd = new HTableDescriptor(TestCoprocessorWhitelistMasterObserver.TEST_TABLE);
        HColumnDescriptor hcd = new HColumnDescriptor(TestCoprocessorWhitelistMasterObserver.TEST_FAMILY);
        htd.addFamily(hcd);
        htd.addCoprocessor(TestCoprocessorWhitelistMasterObserver.TestRegionObserver.class.getName());
        Connection connection = ConnectionFactory.createConnection(conf);
        Admin admin = connection.getAdmin();
        TestCoprocessorWhitelistMasterObserver.LOG.info("Creating Table");
        admin.createTable(htd);
        // ensure table was created and coprocessor is added to table
        TestCoprocessorWhitelistMasterObserver.LOG.info("Done Creating Table");
        Table t = connection.getTable(TestCoprocessorWhitelistMasterObserver.TEST_TABLE);
        Assert.assertEquals(1, t.getTableDescriptor().getCoprocessors().size());
    }
}

