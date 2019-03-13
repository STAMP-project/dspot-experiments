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


import Action.READ;
import Action.WRITE;
import SpaceViolationPolicy.NO_WRITES_COMPACTIONS;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.hadoop.hbase.DoNotRetryIOException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.security.access.AccessControlClient;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test class to verify that the HBase superuser can override quotas.
 */
@Category(MediumTests.class)
public class TestSuperUserQuotaPermissions {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSuperUserQuotaPermissions.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestSuperUserQuotaPermissions.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    // Default to the user running the tests
    private static final String SUPERUSER_NAME = System.getProperty("user.name");

    private static final UserGroupInformation SUPERUSER_UGI = UserGroupInformation.createUserForTesting(TestSuperUserQuotaPermissions.SUPERUSER_NAME, new String[0]);

    private static final String REGULARUSER_NAME = "quota_regularuser";

    private static final UserGroupInformation REGULARUSER_UGI = UserGroupInformation.createUserForTesting(TestSuperUserQuotaPermissions.REGULARUSER_NAME, new String[0]);

    private static final AtomicLong COUNTER = new AtomicLong(0);

    @Rule
    public TestName testName = new TestName();

    private SpaceQuotaHelperForTests helper;

    @Test
    public void testSuperUserCanStillCompact() throws Exception {
        // Create a table and write enough data to push it into quota violation
        final TableName tn = doAsSuperUser(new Callable<TableName>() {
            @Override
            public TableName call() throws Exception {
                try (Connection conn = getConnection()) {
                    Admin admin = conn.getAdmin();
                    final TableName tn = helper.createTableWithRegions(admin, 5);
                    final long sizeLimit = 2L * (SpaceQuotaHelperForTests.ONE_MEGABYTE);
                    QuotaSettings settings = QuotaSettingsFactory.limitTableSpace(tn, sizeLimit, NO_WRITES_COMPACTIONS);
                    admin.setQuota(settings);
                    // Grant the normal user permissions
                    try {
                        AccessControlClient.grant(conn, tn, TestSuperUserQuotaPermissions.REGULARUSER_NAME, null, null, READ, WRITE);
                    } catch (Throwable t) {
                        if (t instanceof Exception) {
                            throw ((Exception) (t));
                        }
                        throw new Exception(t);
                    }
                    return tn;
                }
            }
        });
        // Write a bunch of data as our end-user
        doAsRegularUser(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try (Connection conn = getConnection()) {
                    helper.writeData(tn, (3L * (SpaceQuotaHelperForTests.ONE_MEGABYTE)));
                    return null;
                }
            }
        });
        waitForTableToEnterQuotaViolation(tn);
        // Should throw an exception, unprivileged users cannot compact due to the quota
        try {
            doAsRegularUser(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    try (Connection conn = getConnection()) {
                        conn.getAdmin().majorCompact(tn);
                        return null;
                    }
                }
            });
            Assert.fail("Expected an exception trying to compact a table with a quota violation");
        } catch (DoNotRetryIOException e) {
            // Expected
        }
        // Should not throw an exception (superuser can do anything)
        doAsSuperUser(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try (Connection conn = getConnection()) {
                    conn.getAdmin().majorCompact(tn);
                    return null;
                }
            }
        });
    }

    @Test
    public void testSuperuserCanRemoveQuota() throws Exception {
        // Create a table and write enough data to push it into quota violation
        final TableName tn = doAsSuperUser(new Callable<TableName>() {
            @Override
            public TableName call() throws Exception {
                try (Connection conn = getConnection()) {
                    final Admin admin = conn.getAdmin();
                    final TableName tn = helper.createTableWithRegions(admin, 5);
                    final long sizeLimit = 2L * (SpaceQuotaHelperForTests.ONE_MEGABYTE);
                    QuotaSettings settings = QuotaSettingsFactory.limitTableSpace(tn, sizeLimit, NO_WRITES_COMPACTIONS);
                    admin.setQuota(settings);
                    // Grant the normal user permission to create a table and set a quota
                    try {
                        AccessControlClient.grant(conn, tn, TestSuperUserQuotaPermissions.REGULARUSER_NAME, null, null, READ, WRITE);
                    } catch (Throwable t) {
                        if (t instanceof Exception) {
                            throw ((Exception) (t));
                        }
                        throw new Exception(t);
                    }
                    return tn;
                }
            }
        });
        // Write a bunch of data as our end-user
        doAsRegularUser(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try (Connection conn = getConnection()) {
                    helper.writeData(tn, (3L * (SpaceQuotaHelperForTests.ONE_MEGABYTE)));
                    return null;
                }
            }
        });
        // Wait for the table to hit quota violation
        waitForTableToEnterQuotaViolation(tn);
        // Try to be "bad" and remove the quota as the end user (we want to write more data!)
        doAsRegularUser(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try (Connection conn = getConnection()) {
                    final Admin admin = conn.getAdmin();
                    QuotaSettings qs = QuotaSettingsFactory.removeTableSpaceLimit(tn);
                    try {
                        admin.setQuota(qs);
                        Assert.fail("Expected that an unprivileged user should not be allowed to remove a quota");
                    } catch (Exception e) {
                        // pass
                    }
                    return null;
                }
            }
        });
        // Verify that the superuser can remove the quota
        doAsSuperUser(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try (Connection conn = getConnection()) {
                    final Admin admin = conn.getAdmin();
                    QuotaSettings qs = QuotaSettingsFactory.removeTableSpaceLimit(tn);
                    admin.setQuota(qs);
                    Assert.assertNull(helper.getTableSpaceQuota(conn, tn));
                    return null;
                }
            }
        });
    }
}

