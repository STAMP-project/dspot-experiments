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
package org.apache.hadoop.hbase.rsgroup;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.security.access.SecureTestUtil;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.SecurityTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Performs authorization checks for rsgroup operations, according to different
 * levels of authorized users.
 */
@Category({ SecurityTests.class, MediumTests.class })
public class TestRSGroupsWithACL extends SecureTestUtil {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRSGroupsWithACL.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestRSGroupsWithACL.class);

    private static TableName TEST_TABLE = TableName.valueOf("testtable1");

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static Configuration conf;

    private static Connection systemUserConnection;

    // user with all permissions
    private static User SUPERUSER;

    // user granted with all global permission
    private static User USER_ADMIN;

    // user with rw permissions on column family.
    private static User USER_RW;

    // user with read-only permissions
    private static User USER_RO;

    // user is table owner. will have all permissions on table
    private static User USER_OWNER;

    // user with create table permissions alone
    private static User USER_CREATE;

    // user with no permissions
    private static User USER_NONE;

    private static final String GROUP_ADMIN = "group_admin";

    private static final String GROUP_CREATE = "group_create";

    private static final String GROUP_READ = "group_read";

    private static final String GROUP_WRITE = "group_write";

    private static User USER_GROUP_ADMIN;

    private static User USER_GROUP_CREATE;

    private static User USER_GROUP_READ;

    private static User USER_GROUP_WRITE;

    private static byte[] TEST_FAMILY = Bytes.toBytes("f1");

    private static RSGroupAdminEndpoint rsGroupAdminEndpoint;

    @Test
    public void testGetRSGroupInfo() throws Exception {
        AccessTestAction action = () -> {
            TestRSGroupsWithACL.rsGroupAdminEndpoint.checkPermission("getRSGroupInfo");
            return null;
        };
        validateAdminPermissions(action);
    }

    @Test
    public void testGetRSGroupInfoOfTable() throws Exception {
        AccessTestAction action = () -> {
            TestRSGroupsWithACL.rsGroupAdminEndpoint.checkPermission("getRSGroupInfoOfTable");
            return null;
        };
        validateAdminPermissions(action);
    }

    @Test
    public void testMoveServers() throws Exception {
        AccessTestAction action = () -> {
            TestRSGroupsWithACL.rsGroupAdminEndpoint.checkPermission("moveServers");
            return null;
        };
        validateAdminPermissions(action);
    }

    @Test
    public void testMoveTables() throws Exception {
        AccessTestAction action = () -> {
            TestRSGroupsWithACL.rsGroupAdminEndpoint.checkPermission("moveTables");
            return null;
        };
        validateAdminPermissions(action);
    }

    @Test
    public void testAddRSGroup() throws Exception {
        AccessTestAction action = () -> {
            TestRSGroupsWithACL.rsGroupAdminEndpoint.checkPermission("addRSGroup");
            return null;
        };
        validateAdminPermissions(action);
    }

    @Test
    public void testRemoveRSGroup() throws Exception {
        AccessTestAction action = () -> {
            TestRSGroupsWithACL.rsGroupAdminEndpoint.checkPermission("removeRSGroup");
            return null;
        };
        validateAdminPermissions(action);
    }

    @Test
    public void testBalanceRSGroup() throws Exception {
        AccessTestAction action = () -> {
            TestRSGroupsWithACL.rsGroupAdminEndpoint.checkPermission("balanceRSGroup");
            return null;
        };
        validateAdminPermissions(action);
    }

    @Test
    public void testListRSGroup() throws Exception {
        AccessTestAction action = () -> {
            TestRSGroupsWithACL.rsGroupAdminEndpoint.checkPermission("listRSGroup");
            return null;
        };
        validateAdminPermissions(action);
    }

    @Test
    public void testGetRSGroupInfoOfServer() throws Exception {
        AccessTestAction action = () -> {
            TestRSGroupsWithACL.rsGroupAdminEndpoint.checkPermission("getRSGroupInfoOfServer");
            return null;
        };
        validateAdminPermissions(action);
    }

    @Test
    public void testMoveServersAndTables() throws Exception {
        AccessTestAction action = () -> {
            TestRSGroupsWithACL.rsGroupAdminEndpoint.checkPermission("moveServersAndTables");
            return null;
        };
        validateAdminPermissions(action);
    }

    @Test
    public void testRemoveServers() throws Exception {
        AccessTestAction action = () -> {
            TestRSGroupsWithACL.rsGroupAdminEndpoint.checkPermission("removeServers");
            return null;
        };
        validateAdminPermissions(action);
    }
}

