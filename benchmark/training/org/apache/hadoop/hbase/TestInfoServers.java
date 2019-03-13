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
package org.apache.hadoop.hbase;


import TableName.META_TABLE_NAME;
import java.net.URL;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Testing, info servers are disabled.  This test enables then and checks that
 * they serve pages.
 */
@Category({ MiscTests.class, MediumTests.class })
public class TestInfoServers {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestInfoServers.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestInfoServers.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    @Rule
    public TestName name = new TestName();

    @Test
    public void testGetMasterInfoPort() throws Exception {
        try (Admin admin = TestInfoServers.UTIL.getAdmin()) {
            Assert.assertEquals(TestInfoServers.UTIL.getHBaseCluster().getMaster().getInfoServer().getPort(), admin.getMasterInfoPort());
        }
    }

    /**
     * Ensure when we go to top level index pages that we get redirected to an info-server specific status
     * page.
     */
    @Test
    public void testInfoServersRedirect() throws Exception {
        // give the cluster time to start up
        TestInfoServers.UTIL.getConnection().getTable(META_TABLE_NAME).close();
        int port = TestInfoServers.UTIL.getHBaseCluster().getMaster().getInfoServer().getPort();
        assertContainsContent(new URL((("http://localhost:" + port) + "/index.html")), "master-status");
        port = TestInfoServers.UTIL.getHBaseCluster().getRegionServerThreads().get(0).getRegionServer().getInfoServer().getPort();
        assertContainsContent(new URL((("http://localhost:" + port) + "/index.html")), "rs-status");
    }

    /**
     * Test that the status pages in the minicluster load properly.
     *
     * This is somewhat a duplicate of TestRSStatusServlet and
     * TestMasterStatusServlet, but those are true unit tests
     * whereas this uses a cluster.
     */
    @Test
    public void testInfoServersStatusPages() throws Exception {
        int port = TestInfoServers.UTIL.getHBaseCluster().getMaster().getInfoServer().getPort();
        assertContainsContent(new URL((("http://localhost:" + port) + "/master-status")), "meta");
        port = TestInfoServers.UTIL.getHBaseCluster().getRegionServerThreads().get(0).getRegionServer().getInfoServer().getPort();
        assertContainsContent(new URL((("http://localhost:" + port) + "/rs-status")), "meta");
    }

    @Test
    public void testMasterServerReadOnly() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        byte[] cf = Bytes.toBytes("d");
        TestInfoServers.UTIL.createTable(tableName, cf);
        TestInfoServers.UTIL.waitTableAvailable(tableName);
        int port = TestInfoServers.UTIL.getHBaseCluster().getMaster().getInfoServer().getPort();
        assertDoesNotContainContent(new URL((((("http://localhost:" + port) + "/table.jsp?name=") + tableName) + "&action=split&key=")), "Table action request accepted");
        assertDoesNotContainContent(new URL(((("http://localhost:" + port) + "/table.jsp?name=") + tableName)), "Actions:");
    }
}

