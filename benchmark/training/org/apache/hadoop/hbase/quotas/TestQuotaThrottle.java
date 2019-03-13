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
package org.apache.hadoop.hbase.quotas;


import QuotaTableUtil.QUOTA_REGION_SERVER_ROW_KEY;
import ThrottleType.READ_CAPACITY_UNIT;
import ThrottleType.READ_NUMBER;
import ThrottleType.REQUEST_NUMBER;
import ThrottleType.WRITE_CAPACITY_UNIT;
import ThrottleType.WRITE_NUMBER;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// Disabled because flakey. Fails ~30% on a resource constrained GCE though not on Apache.
@Ignore
@Category({ RegionServerTests.class, MediumTests.class })
public class TestQuotaThrottle {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestQuotaThrottle.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestQuotaThrottle.class);

    private static final int REFRESH_TIME = 30 * 60000;

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final byte[] FAMILY = Bytes.toBytes("cf");

    private static final byte[] QUALIFIER = Bytes.toBytes("q");

    private static final TableName[] TABLE_NAMES = new TableName[]{ TableName.valueOf("TestQuotaAdmin0"), TableName.valueOf("TestQuotaAdmin1"), TableName.valueOf("TestQuotaAdmin2") };

    private static Table[] tables;

    @Test
    public void testUserGlobalThrottle() throws Exception {
        final Admin admin = TestQuotaThrottle.TEST_UTIL.getAdmin();
        final String userName = User.getCurrent().getShortName();
        // Add 6req/min limit
        admin.setQuota(QuotaSettingsFactory.throttleUser(userName, REQUEST_NUMBER, 6, TimeUnit.MINUTES));
        ThrottleQuotaTestUtil.triggerUserCacheRefresh(TestQuotaThrottle.TEST_UTIL, false, TestQuotaThrottle.TABLE_NAMES);
        // should execute at max 6 requests
        Assert.assertEquals(6, ThrottleQuotaTestUtil.doPuts(100, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables));
        // wait a minute and you should get other 6 requests executed
        ThrottleQuotaTestUtil.waitMinuteQuota();
        Assert.assertEquals(6, ThrottleQuotaTestUtil.doPuts(100, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables));
        // Remove all the limits
        admin.setQuota(QuotaSettingsFactory.unthrottleUser(userName));
        ThrottleQuotaTestUtil.triggerUserCacheRefresh(TestQuotaThrottle.TEST_UTIL, true, TestQuotaThrottle.TABLE_NAMES);
        Assert.assertEquals(60, ThrottleQuotaTestUtil.doPuts(60, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables));
        Assert.assertEquals(60, ThrottleQuotaTestUtil.doGets(60, TestQuotaThrottle.tables));
    }

    @Test
    public void testUserGlobalReadAndWriteThrottle() throws Exception {
        final Admin admin = TestQuotaThrottle.TEST_UTIL.getAdmin();
        final String userName = User.getCurrent().getShortName();
        // Add 6req/min limit for read request
        admin.setQuota(QuotaSettingsFactory.throttleUser(userName, READ_NUMBER, 6, TimeUnit.MINUTES));
        ThrottleQuotaTestUtil.triggerUserCacheRefresh(TestQuotaThrottle.TEST_UTIL, false, TestQuotaThrottle.TABLE_NAMES);
        // not limit for write request and should execute at max 6 read requests
        Assert.assertEquals(60, ThrottleQuotaTestUtil.doPuts(60, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables));
        Assert.assertEquals(6, ThrottleQuotaTestUtil.doGets(100, TestQuotaThrottle.tables));
        ThrottleQuotaTestUtil.waitMinuteQuota();
        // Add 6req/min limit for write request
        admin.setQuota(QuotaSettingsFactory.throttleUser(userName, WRITE_NUMBER, 6, TimeUnit.MINUTES));
        ThrottleQuotaTestUtil.triggerUserCacheRefresh(TestQuotaThrottle.TEST_UTIL, false, TestQuotaThrottle.TABLE_NAMES);
        // should execute at max 6 read requests and at max 6 write write requests
        Assert.assertEquals(6, ThrottleQuotaTestUtil.doGets(100, TestQuotaThrottle.tables));
        Assert.assertEquals(6, ThrottleQuotaTestUtil.doPuts(60, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables));
        // Remove all the limits
        admin.setQuota(QuotaSettingsFactory.unthrottleUser(userName));
        ThrottleQuotaTestUtil.triggerUserCacheRefresh(TestQuotaThrottle.TEST_UTIL, true, TestQuotaThrottle.TABLE_NAMES);
        Assert.assertEquals(60, ThrottleQuotaTestUtil.doPuts(60, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables));
        Assert.assertEquals(60, ThrottleQuotaTestUtil.doGets(60, TestQuotaThrottle.tables));
    }

    @Test
    public void testUserTableThrottle() throws Exception {
        final Admin admin = TestQuotaThrottle.TEST_UTIL.getAdmin();
        final String userName = User.getCurrent().getShortName();
        // Add 6req/min limit
        admin.setQuota(QuotaSettingsFactory.throttleUser(userName, TestQuotaThrottle.TABLE_NAMES[0], REQUEST_NUMBER, 6, TimeUnit.MINUTES));
        ThrottleQuotaTestUtil.triggerUserCacheRefresh(TestQuotaThrottle.TEST_UTIL, false, TestQuotaThrottle.TABLE_NAMES[0]);
        // should execute at max 6 requests on tables[0] and have no limit on tables[1]
        Assert.assertEquals(6, ThrottleQuotaTestUtil.doPuts(100, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables[0]));
        Assert.assertEquals(30, ThrottleQuotaTestUtil.doPuts(30, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables[1]));
        // wait a minute and you should get other 6 requests executed
        ThrottleQuotaTestUtil.waitMinuteQuota();
        Assert.assertEquals(6, ThrottleQuotaTestUtil.doPuts(100, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables[0]));
        // Remove all the limits
        admin.setQuota(QuotaSettingsFactory.unthrottleUser(userName, TestQuotaThrottle.TABLE_NAMES[0]));
        ThrottleQuotaTestUtil.triggerUserCacheRefresh(TestQuotaThrottle.TEST_UTIL, true, TestQuotaThrottle.TABLE_NAMES);
        Assert.assertEquals(60, ThrottleQuotaTestUtil.doPuts(60, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables));
        Assert.assertEquals(60, ThrottleQuotaTestUtil.doGets(60, TestQuotaThrottle.tables));
    }

    @Test
    public void testUserTableReadAndWriteThrottle() throws Exception {
        final Admin admin = TestQuotaThrottle.TEST_UTIL.getAdmin();
        final String userName = User.getCurrent().getShortName();
        // Add 6req/min limit for write request on tables[0]
        admin.setQuota(QuotaSettingsFactory.throttleUser(userName, TestQuotaThrottle.TABLE_NAMES[0], WRITE_NUMBER, 6, TimeUnit.MINUTES));
        ThrottleQuotaTestUtil.triggerUserCacheRefresh(TestQuotaThrottle.TEST_UTIL, false, TestQuotaThrottle.TABLE_NAMES[0]);
        // should execute at max 6 write requests and have no limit for read request
        Assert.assertEquals(6, ThrottleQuotaTestUtil.doPuts(100, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables[0]));
        Assert.assertEquals(60, ThrottleQuotaTestUtil.doGets(60, TestQuotaThrottle.tables[0]));
        // no limit on tables[1]
        Assert.assertEquals(60, ThrottleQuotaTestUtil.doPuts(60, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables[1]));
        Assert.assertEquals(60, ThrottleQuotaTestUtil.doGets(60, TestQuotaThrottle.tables[1]));
        // wait a minute and you should get other 6 write requests executed
        ThrottleQuotaTestUtil.waitMinuteQuota();
        // Add 6req/min limit for read request on tables[0]
        admin.setQuota(QuotaSettingsFactory.throttleUser(userName, TestQuotaThrottle.TABLE_NAMES[0], READ_NUMBER, 6, TimeUnit.MINUTES));
        ThrottleQuotaTestUtil.triggerUserCacheRefresh(TestQuotaThrottle.TEST_UTIL, false, TestQuotaThrottle.TABLE_NAMES[0]);
        // should execute at max 6 read requests and at max 6 write requests
        Assert.assertEquals(6, ThrottleQuotaTestUtil.doPuts(100, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables[0]));
        Assert.assertEquals(6, ThrottleQuotaTestUtil.doGets(60, TestQuotaThrottle.tables[0]));
        // no limit on tables[1]
        Assert.assertEquals(30, ThrottleQuotaTestUtil.doPuts(30, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables[1]));
        Assert.assertEquals(30, ThrottleQuotaTestUtil.doGets(30, TestQuotaThrottle.tables[1]));
        // Remove all the limits
        admin.setQuota(QuotaSettingsFactory.unthrottleUser(userName, TestQuotaThrottle.TABLE_NAMES[0]));
        ThrottleQuotaTestUtil.triggerUserCacheRefresh(TestQuotaThrottle.TEST_UTIL, true, TestQuotaThrottle.TABLE_NAMES);
        Assert.assertEquals(60, ThrottleQuotaTestUtil.doPuts(60, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables));
        Assert.assertEquals(60, ThrottleQuotaTestUtil.doGets(60, TestQuotaThrottle.tables));
    }

    @Test
    public void testUserNamespaceThrottle() throws Exception {
        final Admin admin = TestQuotaThrottle.TEST_UTIL.getAdmin();
        final String userName = User.getCurrent().getShortName();
        final String NAMESPACE = "default";
        // Add 6req/min limit
        admin.setQuota(QuotaSettingsFactory.throttleUser(userName, NAMESPACE, REQUEST_NUMBER, 6, TimeUnit.MINUTES));
        ThrottleQuotaTestUtil.triggerUserCacheRefresh(TestQuotaThrottle.TEST_UTIL, false, TestQuotaThrottle.TABLE_NAMES[0]);
        // should execute at max 6 requests on tables[0] and have no limit on tables[1]
        Assert.assertEquals(6, ThrottleQuotaTestUtil.doPuts(100, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables[0]));
        // wait a minute and you should get other 6 requests executed
        ThrottleQuotaTestUtil.waitMinuteQuota();
        Assert.assertEquals(6, ThrottleQuotaTestUtil.doPuts(100, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables[1]));
        // Remove all the limits
        admin.setQuota(QuotaSettingsFactory.unthrottleUser(userName, NAMESPACE));
        ThrottleQuotaTestUtil.triggerUserCacheRefresh(TestQuotaThrottle.TEST_UTIL, true, TestQuotaThrottle.TABLE_NAMES);
        Assert.assertEquals(60, ThrottleQuotaTestUtil.doPuts(60, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables));
        Assert.assertEquals(60, ThrottleQuotaTestUtil.doGets(60, TestQuotaThrottle.tables));
    }

    @Test
    public void testUserNamespaceReadAndWriteThrottle() throws Exception {
        final Admin admin = TestQuotaThrottle.TEST_UTIL.getAdmin();
        final String userName = User.getCurrent().getShortName();
        final String NAMESPACE = "default";
        // Add 6req/min limit for read request
        admin.setQuota(QuotaSettingsFactory.throttleUser(userName, NAMESPACE, READ_NUMBER, 6, TimeUnit.MINUTES));
        ThrottleQuotaTestUtil.triggerUserCacheRefresh(TestQuotaThrottle.TEST_UTIL, false, TestQuotaThrottle.TABLE_NAMES[0]);
        // should execute at max 6 read requests and have no limit for write request
        Assert.assertEquals(6, ThrottleQuotaTestUtil.doGets(60, TestQuotaThrottle.tables[0]));
        Assert.assertEquals(60, ThrottleQuotaTestUtil.doPuts(60, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables[0]));
        ThrottleQuotaTestUtil.waitMinuteQuota();
        // Add 6req/min limit for write request, too
        admin.setQuota(QuotaSettingsFactory.throttleUser(userName, NAMESPACE, WRITE_NUMBER, 6, TimeUnit.MINUTES));
        ThrottleQuotaTestUtil.triggerUserCacheRefresh(TestQuotaThrottle.TEST_UTIL, false, TestQuotaThrottle.TABLE_NAMES[0]);
        // should execute at max 6 read requests and at max 6 write requests
        Assert.assertEquals(6, ThrottleQuotaTestUtil.doGets(60, TestQuotaThrottle.tables[0]));
        Assert.assertEquals(6, ThrottleQuotaTestUtil.doPuts(60, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables[0]));
        // Remove all the limits
        admin.setQuota(QuotaSettingsFactory.unthrottleUser(userName, NAMESPACE));
        ThrottleQuotaTestUtil.triggerUserCacheRefresh(TestQuotaThrottle.TEST_UTIL, true, TestQuotaThrottle.TABLE_NAMES);
        Assert.assertEquals(60, ThrottleQuotaTestUtil.doPuts(60, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables));
        Assert.assertEquals(60, ThrottleQuotaTestUtil.doGets(60, TestQuotaThrottle.tables));
    }

    @Test
    public void testTableGlobalThrottle() throws Exception {
        final Admin admin = TestQuotaThrottle.TEST_UTIL.getAdmin();
        // Add 6req/min limit
        admin.setQuota(QuotaSettingsFactory.throttleTable(TestQuotaThrottle.TABLE_NAMES[0], REQUEST_NUMBER, 6, TimeUnit.MINUTES));
        ThrottleQuotaTestUtil.triggerTableCacheRefresh(TestQuotaThrottle.TEST_UTIL, false, TestQuotaThrottle.TABLE_NAMES[0]);
        // should execute at max 6 requests
        Assert.assertEquals(6, ThrottleQuotaTestUtil.doPuts(100, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables[0]));
        // should have no limits
        Assert.assertEquals(30, ThrottleQuotaTestUtil.doPuts(30, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables[1]));
        // wait a minute and you should get other 6 requests executed
        ThrottleQuotaTestUtil.waitMinuteQuota();
        Assert.assertEquals(6, ThrottleQuotaTestUtil.doPuts(100, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables[0]));
        // Remove all the limits
        admin.setQuota(QuotaSettingsFactory.unthrottleTable(TestQuotaThrottle.TABLE_NAMES[0]));
        ThrottleQuotaTestUtil.triggerTableCacheRefresh(TestQuotaThrottle.TEST_UTIL, true, TestQuotaThrottle.TABLE_NAMES[0]);
        Assert.assertEquals(80, ThrottleQuotaTestUtil.doGets(80, TestQuotaThrottle.tables[0], TestQuotaThrottle.tables[1]));
    }

    @Test
    public void testTableGlobalReadAndWriteThrottle() throws Exception {
        final Admin admin = TestQuotaThrottle.TEST_UTIL.getAdmin();
        // Add 6req/min limit for read request
        admin.setQuota(QuotaSettingsFactory.throttleTable(TestQuotaThrottle.TABLE_NAMES[0], READ_NUMBER, 6, TimeUnit.MINUTES));
        ThrottleQuotaTestUtil.triggerTableCacheRefresh(TestQuotaThrottle.TEST_UTIL, false, TestQuotaThrottle.TABLE_NAMES[0]);
        // should execute at max 6 read requests and have no limit for write request
        Assert.assertEquals(6, ThrottleQuotaTestUtil.doGets(100, TestQuotaThrottle.tables[0]));
        Assert.assertEquals(100, ThrottleQuotaTestUtil.doPuts(100, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables[0]));
        // should have no limits on tables[1]
        Assert.assertEquals(30, ThrottleQuotaTestUtil.doPuts(30, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables[1]));
        Assert.assertEquals(30, ThrottleQuotaTestUtil.doGets(30, TestQuotaThrottle.tables[1]));
        // wait a minute and you should get other 6 requests executed
        ThrottleQuotaTestUtil.waitMinuteQuota();
        // Add 6req/min limit for write request, too
        admin.setQuota(QuotaSettingsFactory.throttleTable(TestQuotaThrottle.TABLE_NAMES[0], WRITE_NUMBER, 6, TimeUnit.MINUTES));
        ThrottleQuotaTestUtil.triggerTableCacheRefresh(TestQuotaThrottle.TEST_UTIL, false, TestQuotaThrottle.TABLE_NAMES[0]);
        // should execute at max 6 read requests and at max 6 write requests
        Assert.assertEquals(6, ThrottleQuotaTestUtil.doGets(100, TestQuotaThrottle.tables[0]));
        Assert.assertEquals(6, ThrottleQuotaTestUtil.doPuts(100, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables[0]));
        // should have no limits on tables[1]
        Assert.assertEquals(30, ThrottleQuotaTestUtil.doPuts(30, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables[1]));
        Assert.assertEquals(30, ThrottleQuotaTestUtil.doGets(30, TestQuotaThrottle.tables[1]));
        // Remove all the limits
        admin.setQuota(QuotaSettingsFactory.unthrottleTable(TestQuotaThrottle.TABLE_NAMES[0]));
        ThrottleQuotaTestUtil.triggerTableCacheRefresh(TestQuotaThrottle.TEST_UTIL, true, TestQuotaThrottle.TABLE_NAMES[0]);
        Assert.assertEquals(80, ThrottleQuotaTestUtil.doGets(80, TestQuotaThrottle.tables[0], TestQuotaThrottle.tables[1]));
    }

    @Test
    public void testNamespaceGlobalThrottle() throws Exception {
        final Admin admin = TestQuotaThrottle.TEST_UTIL.getAdmin();
        final String NAMESPACE = "default";
        // Add 6req/min limit
        admin.setQuota(QuotaSettingsFactory.throttleNamespace(NAMESPACE, REQUEST_NUMBER, 6, TimeUnit.MINUTES));
        ThrottleQuotaTestUtil.triggerNamespaceCacheRefresh(TestQuotaThrottle.TEST_UTIL, false, TestQuotaThrottle.TABLE_NAMES[0]);
        // should execute at max 6 requests
        Assert.assertEquals(6, ThrottleQuotaTestUtil.doPuts(100, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables[0]));
        // wait a minute and you should get other 6 requests executed
        ThrottleQuotaTestUtil.waitMinuteQuota();
        Assert.assertEquals(6, ThrottleQuotaTestUtil.doPuts(100, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables[1]));
        admin.setQuota(QuotaSettingsFactory.unthrottleNamespace(NAMESPACE));
        ThrottleQuotaTestUtil.triggerNamespaceCacheRefresh(TestQuotaThrottle.TEST_UTIL, true, TestQuotaThrottle.TABLE_NAMES[0]);
        Assert.assertEquals(40, ThrottleQuotaTestUtil.doPuts(40, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables[0]));
    }

    @Test
    public void testNamespaceGlobalReadAndWriteThrottle() throws Exception {
        final Admin admin = TestQuotaThrottle.TEST_UTIL.getAdmin();
        final String NAMESPACE = "default";
        // Add 6req/min limit for write request
        admin.setQuota(QuotaSettingsFactory.throttleNamespace(NAMESPACE, WRITE_NUMBER, 6, TimeUnit.MINUTES));
        ThrottleQuotaTestUtil.triggerNamespaceCacheRefresh(TestQuotaThrottle.TEST_UTIL, false, TestQuotaThrottle.TABLE_NAMES[0]);
        // should execute at max 6 write requests and no limit for read request
        Assert.assertEquals(6, ThrottleQuotaTestUtil.doPuts(100, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables[0]));
        Assert.assertEquals(100, ThrottleQuotaTestUtil.doGets(100, TestQuotaThrottle.tables[0]));
        // wait a minute and you should get other 6 requests executed
        ThrottleQuotaTestUtil.waitMinuteQuota();
        // Add 6req/min limit for read request, too
        admin.setQuota(QuotaSettingsFactory.throttleNamespace(NAMESPACE, READ_NUMBER, 6, TimeUnit.MINUTES));
        ThrottleQuotaTestUtil.triggerNamespaceCacheRefresh(TestQuotaThrottle.TEST_UTIL, false, TestQuotaThrottle.TABLE_NAMES[0]);
        // should execute at max 6 write requests and at max 6 read requests
        Assert.assertEquals(6, ThrottleQuotaTestUtil.doPuts(100, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables[0]));
        Assert.assertEquals(6, ThrottleQuotaTestUtil.doGets(100, TestQuotaThrottle.tables[0]));
        admin.setQuota(QuotaSettingsFactory.unthrottleNamespace(NAMESPACE));
        ThrottleQuotaTestUtil.triggerNamespaceCacheRefresh(TestQuotaThrottle.TEST_UTIL, true, TestQuotaThrottle.TABLE_NAMES[0]);
        Assert.assertEquals(40, ThrottleQuotaTestUtil.doPuts(40, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables[0]));
    }

    @Test
    public void testUserAndTableThrottle() throws Exception {
        final Admin admin = TestQuotaThrottle.TEST_UTIL.getAdmin();
        final String userName = User.getCurrent().getShortName();
        // Add 6req/min limit for the user on tables[0]
        admin.setQuota(QuotaSettingsFactory.throttleUser(userName, TestQuotaThrottle.TABLE_NAMES[0], REQUEST_NUMBER, 6, TimeUnit.MINUTES));
        ThrottleQuotaTestUtil.triggerUserCacheRefresh(TestQuotaThrottle.TEST_UTIL, false, TestQuotaThrottle.TABLE_NAMES[0]);
        // Add 12req/min limit for the user
        admin.setQuota(QuotaSettingsFactory.throttleUser(userName, REQUEST_NUMBER, 12, TimeUnit.MINUTES));
        ThrottleQuotaTestUtil.triggerUserCacheRefresh(TestQuotaThrottle.TEST_UTIL, false, TestQuotaThrottle.TABLE_NAMES[1], TestQuotaThrottle.TABLE_NAMES[2]);
        // Add 8req/min limit for the tables[1]
        admin.setQuota(QuotaSettingsFactory.throttleTable(TestQuotaThrottle.TABLE_NAMES[1], REQUEST_NUMBER, 8, TimeUnit.MINUTES));
        ThrottleQuotaTestUtil.triggerTableCacheRefresh(TestQuotaThrottle.TEST_UTIL, false, TestQuotaThrottle.TABLE_NAMES[1]);
        // Add a lower table level throttle on tables[0]
        admin.setQuota(QuotaSettingsFactory.throttleTable(TestQuotaThrottle.TABLE_NAMES[0], REQUEST_NUMBER, 3, TimeUnit.MINUTES));
        ThrottleQuotaTestUtil.triggerTableCacheRefresh(TestQuotaThrottle.TEST_UTIL, false, TestQuotaThrottle.TABLE_NAMES[0]);
        // should execute at max 12 requests
        Assert.assertEquals(12, ThrottleQuotaTestUtil.doGets(100, TestQuotaThrottle.tables[2]));
        // should execute at max 8 requests
        ThrottleQuotaTestUtil.waitMinuteQuota();
        Assert.assertEquals(8, ThrottleQuotaTestUtil.doGets(100, TestQuotaThrottle.tables[1]));
        // should execute at max 3 requests
        ThrottleQuotaTestUtil.waitMinuteQuota();
        Assert.assertEquals(3, ThrottleQuotaTestUtil.doPuts(100, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables[0]));
        // Remove all the throttling rules
        admin.setQuota(QuotaSettingsFactory.unthrottleUser(userName, TestQuotaThrottle.TABLE_NAMES[0]));
        admin.setQuota(QuotaSettingsFactory.unthrottleUser(userName));
        ThrottleQuotaTestUtil.triggerUserCacheRefresh(TestQuotaThrottle.TEST_UTIL, true, TestQuotaThrottle.TABLE_NAMES[0], TestQuotaThrottle.TABLE_NAMES[1]);
        admin.setQuota(QuotaSettingsFactory.unthrottleTable(TestQuotaThrottle.TABLE_NAMES[1]));
        ThrottleQuotaTestUtil.triggerTableCacheRefresh(TestQuotaThrottle.TEST_UTIL, true, TestQuotaThrottle.TABLE_NAMES[1]);
        ThrottleQuotaTestUtil.waitMinuteQuota();
        Assert.assertEquals(40, ThrottleQuotaTestUtil.doGets(40, TestQuotaThrottle.tables[1]));
        admin.setQuota(QuotaSettingsFactory.unthrottleTable(TestQuotaThrottle.TABLE_NAMES[0]));
        ThrottleQuotaTestUtil.triggerTableCacheRefresh(TestQuotaThrottle.TEST_UTIL, true, TestQuotaThrottle.TABLE_NAMES[0]);
        ThrottleQuotaTestUtil.waitMinuteQuota();
        Assert.assertEquals(40, ThrottleQuotaTestUtil.doGets(40, TestQuotaThrottle.tables[0]));
    }

    @Test
    public void testUserGlobalBypassThrottle() throws Exception {
        final Admin admin = TestQuotaThrottle.TEST_UTIL.getAdmin();
        final String userName = User.getCurrent().getShortName();
        final String NAMESPACE = "default";
        // Add 6req/min limit for tables[0]
        admin.setQuota(QuotaSettingsFactory.throttleTable(TestQuotaThrottle.TABLE_NAMES[0], REQUEST_NUMBER, 6, TimeUnit.MINUTES));
        ThrottleQuotaTestUtil.triggerTableCacheRefresh(TestQuotaThrottle.TEST_UTIL, false, TestQuotaThrottle.TABLE_NAMES[0]);
        // Add 13req/min limit for the user
        admin.setQuota(QuotaSettingsFactory.throttleNamespace(NAMESPACE, REQUEST_NUMBER, 13, TimeUnit.MINUTES));
        ThrottleQuotaTestUtil.triggerNamespaceCacheRefresh(TestQuotaThrottle.TEST_UTIL, false, TestQuotaThrottle.TABLE_NAMES[1]);
        // should execute at max 6 requests on table[0] and (13 - 6) on table[1]
        Assert.assertEquals(6, ThrottleQuotaTestUtil.doPuts(100, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables[0]));
        Assert.assertEquals(7, ThrottleQuotaTestUtil.doGets(100, TestQuotaThrottle.tables[1]));
        ThrottleQuotaTestUtil.waitMinuteQuota();
        // Set the global bypass for the user
        admin.setQuota(QuotaSettingsFactory.bypassGlobals(userName, true));
        admin.setQuota(QuotaSettingsFactory.throttleUser(userName, TestQuotaThrottle.TABLE_NAMES[2], REQUEST_NUMBER, 6, TimeUnit.MINUTES));
        ThrottleQuotaTestUtil.triggerUserCacheRefresh(TestQuotaThrottle.TEST_UTIL, false, TestQuotaThrottle.TABLE_NAMES[2]);
        Assert.assertEquals(30, ThrottleQuotaTestUtil.doGets(30, TestQuotaThrottle.tables[0]));
        Assert.assertEquals(30, ThrottleQuotaTestUtil.doGets(30, TestQuotaThrottle.tables[1]));
        ThrottleQuotaTestUtil.waitMinuteQuota();
        // Remove the global bypass
        // should execute at max 6 requests on table[0] and (13 - 6) on table[1]
        admin.setQuota(QuotaSettingsFactory.bypassGlobals(userName, false));
        admin.setQuota(QuotaSettingsFactory.unthrottleUser(userName, TestQuotaThrottle.TABLE_NAMES[2]));
        ThrottleQuotaTestUtil.triggerUserCacheRefresh(TestQuotaThrottle.TEST_UTIL, true, TestQuotaThrottle.TABLE_NAMES[2]);
        Assert.assertEquals(6, ThrottleQuotaTestUtil.doPuts(100, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables[0]));
        Assert.assertEquals(7, ThrottleQuotaTestUtil.doGets(100, TestQuotaThrottle.tables[1]));
        // unset throttle
        admin.setQuota(QuotaSettingsFactory.unthrottleTable(TestQuotaThrottle.TABLE_NAMES[0]));
        admin.setQuota(QuotaSettingsFactory.unthrottleNamespace(NAMESPACE));
        ThrottleQuotaTestUtil.waitMinuteQuota();
        ThrottleQuotaTestUtil.triggerTableCacheRefresh(TestQuotaThrottle.TEST_UTIL, true, TestQuotaThrottle.TABLE_NAMES[0]);
        ThrottleQuotaTestUtil.triggerNamespaceCacheRefresh(TestQuotaThrottle.TEST_UTIL, true, TestQuotaThrottle.TABLE_NAMES[1]);
        Assert.assertEquals(30, ThrottleQuotaTestUtil.doGets(30, TestQuotaThrottle.tables[0]));
        Assert.assertEquals(30, ThrottleQuotaTestUtil.doGets(30, TestQuotaThrottle.tables[1]));
    }

    @Test
    public void testTableWriteCapacityUnitThrottle() throws Exception {
        final Admin admin = TestQuotaThrottle.TEST_UTIL.getAdmin();
        // Add 6CU/min limit
        admin.setQuota(QuotaSettingsFactory.throttleTable(TestQuotaThrottle.TABLE_NAMES[0], WRITE_CAPACITY_UNIT, 6, TimeUnit.MINUTES));
        ThrottleQuotaTestUtil.triggerTableCacheRefresh(TestQuotaThrottle.TEST_UTIL, false, TestQuotaThrottle.TABLE_NAMES[0]);
        // should execute at max 6 capacity units because each put size is 1 capacity unit
        Assert.assertEquals(6, ThrottleQuotaTestUtil.doPuts(20, 10, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables[0]));
        // wait a minute and you should execute at max 3 capacity units because each put size is 2
        // capacity unit
        ThrottleQuotaTestUtil.waitMinuteQuota();
        Assert.assertEquals(3, ThrottleQuotaTestUtil.doPuts(20, 1025, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables[0]));
        admin.setQuota(QuotaSettingsFactory.unthrottleTable(TestQuotaThrottle.TABLE_NAMES[0]));
        ThrottleQuotaTestUtil.triggerTableCacheRefresh(TestQuotaThrottle.TEST_UTIL, true, TestQuotaThrottle.TABLE_NAMES[0]);
    }

    @Test
    public void testTableReadCapacityUnitThrottle() throws Exception {
        final Admin admin = TestQuotaThrottle.TEST_UTIL.getAdmin();
        // Add 6CU/min limit
        admin.setQuota(QuotaSettingsFactory.throttleTable(TestQuotaThrottle.TABLE_NAMES[0], READ_CAPACITY_UNIT, 6, TimeUnit.MINUTES));
        ThrottleQuotaTestUtil.triggerTableCacheRefresh(TestQuotaThrottle.TEST_UTIL, false, TestQuotaThrottle.TABLE_NAMES[0]);
        Assert.assertEquals(20, ThrottleQuotaTestUtil.doPuts(20, 10, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables[0]));
        // should execute at max 6 capacity units because each get size is 1 capacity unit
        Assert.assertEquals(6, ThrottleQuotaTestUtil.doGets(20, TestQuotaThrottle.tables[0]));
        Assert.assertEquals(20, ThrottleQuotaTestUtil.doPuts(20, 2015, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables[0]));
        // wait a minute and you should execute at max 3 capacity units because each get size is 2
        // capacity unit on tables[0]
        ThrottleQuotaTestUtil.waitMinuteQuota();
        Assert.assertEquals(3, ThrottleQuotaTestUtil.doGets(20, TestQuotaThrottle.tables[0]));
        admin.setQuota(QuotaSettingsFactory.unthrottleTable(TestQuotaThrottle.TABLE_NAMES[0]));
        ThrottleQuotaTestUtil.triggerTableCacheRefresh(TestQuotaThrottle.TEST_UTIL, true, TestQuotaThrottle.TABLE_NAMES[0]);
    }

    @Test
    public void testTableExistsGetThrottle() throws Exception {
        final Admin admin = TestQuotaThrottle.TEST_UTIL.getAdmin();
        // Add throttle quota
        admin.setQuota(QuotaSettingsFactory.throttleTable(TestQuotaThrottle.TABLE_NAMES[0], REQUEST_NUMBER, 100, TimeUnit.MINUTES));
        ThrottleQuotaTestUtil.triggerTableCacheRefresh(TestQuotaThrottle.TEST_UTIL, false, TestQuotaThrottle.TABLE_NAMES[0]);
        Table table = TestQuotaThrottle.TEST_UTIL.getConnection().getTable(TestQuotaThrottle.TABLE_NAMES[0]);
        // An exists call when having throttle quota
        table.exists(new org.apache.hadoop.hbase.client.Get(Bytes.toBytes("abc")));
        admin.setQuota(QuotaSettingsFactory.unthrottleTable(TestQuotaThrottle.TABLE_NAMES[0]));
        ThrottleQuotaTestUtil.triggerTableCacheRefresh(TestQuotaThrottle.TEST_UTIL, true, TestQuotaThrottle.TABLE_NAMES[0]);
    }

    @Test
    public void testRegionServerThrottle() throws Exception {
        final Admin admin = TestQuotaThrottle.TEST_UTIL.getAdmin();
        admin.setQuota(QuotaSettingsFactory.throttleTable(TestQuotaThrottle.TABLE_NAMES[0], WRITE_NUMBER, 5, TimeUnit.MINUTES));
        // requests are throttled by table quota
        admin.setQuota(QuotaSettingsFactory.throttleRegionServer(QUOTA_REGION_SERVER_ROW_KEY, WRITE_NUMBER, 7, TimeUnit.MINUTES));
        ThrottleQuotaTestUtil.triggerTableCacheRefresh(TestQuotaThrottle.TEST_UTIL, false, TestQuotaThrottle.TABLE_NAMES[0]);
        ThrottleQuotaTestUtil.triggerRegionServerCacheRefresh(TestQuotaThrottle.TEST_UTIL, false);
        Assert.assertEquals(5, ThrottleQuotaTestUtil.doPuts(10, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables[0]));
        ThrottleQuotaTestUtil.triggerRegionServerCacheRefresh(TestQuotaThrottle.TEST_UTIL, false);
        Assert.assertEquals(5, ThrottleQuotaTestUtil.doPuts(10, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables[0]));
        // requests are throttled by region server quota
        admin.setQuota(QuotaSettingsFactory.throttleRegionServer(QUOTA_REGION_SERVER_ROW_KEY, WRITE_NUMBER, 4, TimeUnit.MINUTES));
        ThrottleQuotaTestUtil.triggerRegionServerCacheRefresh(TestQuotaThrottle.TEST_UTIL, false);
        Assert.assertEquals(4, ThrottleQuotaTestUtil.doPuts(10, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables[0]));
        ThrottleQuotaTestUtil.triggerRegionServerCacheRefresh(TestQuotaThrottle.TEST_UTIL, false);
        Assert.assertEquals(4, ThrottleQuotaTestUtil.doPuts(10, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables[0]));
        // unthrottle
        admin.setQuota(QuotaSettingsFactory.unthrottleRegionServer(QUOTA_REGION_SERVER_ROW_KEY));
        ThrottleQuotaTestUtil.triggerRegionServerCacheRefresh(TestQuotaThrottle.TEST_UTIL, true);
        admin.setQuota(QuotaSettingsFactory.unthrottleTable(TestQuotaThrottle.TABLE_NAMES[0]));
        ThrottleQuotaTestUtil.triggerTableCacheRefresh(TestQuotaThrottle.TEST_UTIL, true, TestQuotaThrottle.TABLE_NAMES[0]);
        ThrottleQuotaTestUtil.triggerRegionServerCacheRefresh(TestQuotaThrottle.TEST_UTIL, true);
    }

    @Test
    public void testExceedThrottleQuota() throws Exception {
        final Admin admin = TestQuotaThrottle.TEST_UTIL.getAdmin();
        admin.setQuota(QuotaSettingsFactory.throttleTable(TestQuotaThrottle.TABLE_NAMES[0], WRITE_NUMBER, 5, TimeUnit.MINUTES));
        ThrottleQuotaTestUtil.triggerTableCacheRefresh(TestQuotaThrottle.TEST_UTIL, false, TestQuotaThrottle.TABLE_NAMES[0]);
        admin.setQuota(QuotaSettingsFactory.throttleRegionServer(QUOTA_REGION_SERVER_ROW_KEY, WRITE_NUMBER, 20, TimeUnit.SECONDS));
        admin.setQuota(QuotaSettingsFactory.throttleRegionServer(QUOTA_REGION_SERVER_ROW_KEY, READ_NUMBER, 10, TimeUnit.SECONDS));
        ThrottleQuotaTestUtil.triggerRegionServerCacheRefresh(TestQuotaThrottle.TEST_UTIL, false);
        // enable exceed throttle quota
        admin.exceedThrottleQuotaSwitch(true);
        // exceed table limit and allowed by region server limit
        ThrottleQuotaTestUtil.triggerExceedThrottleQuotaCacheRefresh(TestQuotaThrottle.TEST_UTIL, true);
        ThrottleQuotaTestUtil.waitMinuteQuota();
        Assert.assertEquals(10, ThrottleQuotaTestUtil.doPuts(10, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables[0]));
        // exceed table limit and throttled by region server limit
        ThrottleQuotaTestUtil.waitMinuteQuota();
        Assert.assertEquals(20, ThrottleQuotaTestUtil.doPuts(25, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables[0]));
        // set region server limiter is lower than table limiter
        admin.setQuota(QuotaSettingsFactory.throttleRegionServer(QUOTA_REGION_SERVER_ROW_KEY, WRITE_NUMBER, 2, TimeUnit.SECONDS));
        ThrottleQuotaTestUtil.triggerRegionServerCacheRefresh(TestQuotaThrottle.TEST_UTIL, false);
        // throttled by region server limiter
        ThrottleQuotaTestUtil.waitMinuteQuota();
        Assert.assertEquals(2, ThrottleQuotaTestUtil.doPuts(10, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables[0]));
        admin.setQuota(QuotaSettingsFactory.throttleRegionServer(QUOTA_REGION_SERVER_ROW_KEY, WRITE_NUMBER, 20, TimeUnit.SECONDS));
        ThrottleQuotaTestUtil.triggerRegionServerCacheRefresh(TestQuotaThrottle.TEST_UTIL, false);
        // disable exceed throttle quota
        admin.exceedThrottleQuotaSwitch(false);
        ThrottleQuotaTestUtil.triggerExceedThrottleQuotaCacheRefresh(TestQuotaThrottle.TEST_UTIL, false);
        ThrottleQuotaTestUtil.waitMinuteQuota();
        // throttled by table limit
        Assert.assertEquals(5, ThrottleQuotaTestUtil.doPuts(10, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables[0]));
        // enable exceed throttle quota and unthrottle region server
        admin.exceedThrottleQuotaSwitch(true);
        ThrottleQuotaTestUtil.triggerExceedThrottleQuotaCacheRefresh(TestQuotaThrottle.TEST_UTIL, true);
        ThrottleQuotaTestUtil.waitMinuteQuota();
        admin.setQuota(QuotaSettingsFactory.unthrottleRegionServer(QUOTA_REGION_SERVER_ROW_KEY));
        ThrottleQuotaTestUtil.triggerRegionServerCacheRefresh(TestQuotaThrottle.TEST_UTIL, true);
        ThrottleQuotaTestUtil.waitMinuteQuota();
        // throttled by table limit
        Assert.assertEquals(5, ThrottleQuotaTestUtil.doPuts(10, TestQuotaThrottle.FAMILY, TestQuotaThrottle.QUALIFIER, TestQuotaThrottle.tables[0]));
        // disable exceed throttle quota
        admin.exceedThrottleQuotaSwitch(false);
        ThrottleQuotaTestUtil.triggerExceedThrottleQuotaCacheRefresh(TestQuotaThrottle.TEST_UTIL, false);
        // unthrottle table
        admin.setQuota(QuotaSettingsFactory.unthrottleTable(TestQuotaThrottle.TABLE_NAMES[0]));
        ThrottleQuotaTestUtil.triggerTableCacheRefresh(TestQuotaThrottle.TEST_UTIL, true, TestQuotaThrottle.TABLE_NAMES[0]);
    }
}

