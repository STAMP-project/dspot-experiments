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


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.CoprocessorEnvironment;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.coprocessor.MasterCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.ObserverContextImpl;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.RegionServerCoprocessorEnvironment;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.SecurityTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Performs checks for reference counting w.r.t. AuthManager which is used by
 * AccessController.
 *
 * NOTE: Only one test in  here. In AMv2, there is problem deleting because
 * we are missing auth. For now disabled. See the cleanup method.
 */
@Category({ SecurityTests.class, MediumTests.class })
public class TestAccessController3 extends SecureTestUtil {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAccessController3.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestAccessController.class);

    private static TableName TEST_TABLE = TableName.valueOf("testtable1");

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static Configuration conf;

    /**
     * The systemUserConnection created here is tied to the system user. In case, you are planning
     * to create AccessTestAction, DON'T use this systemUserConnection as the 'doAs' user
     * gets  eclipsed by the system user.
     */
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

    // user with admin rights on the column family
    private static User USER_ADMIN_CF;

    private static final String GROUP_ADMIN = "group_admin";

    private static final String GROUP_CREATE = "group_create";

    private static final String GROUP_READ = "group_read";

    private static final String GROUP_WRITE = "group_write";

    private static User USER_GROUP_ADMIN;

    private static User USER_GROUP_CREATE;

    private static User USER_GROUP_READ;

    private static User USER_GROUP_WRITE;

    // TODO: convert this test to cover the full matrix in
    // https://hbase.apache.org/book/appendix_acl_matrix.html
    // creating all Scope x Permission combinations
    private static byte[] TEST_FAMILY = Bytes.toBytes("f1");

    private static MasterCoprocessorEnvironment CP_ENV;

    private static AccessController ACCESS_CONTROLLER;

    private static RegionServerCoprocessorEnvironment RSCP_ENV;

    private static RegionCoprocessorEnvironment RCP_ENV;

    private static boolean callSuperTwice = true;

    @Rule
    public TestName name = new TestName();

    // class with faulty stop() method, controlled by flag
    public static class FaultyAccessController extends AccessController {
        public FaultyAccessController() {
        }

        @Override
        public void stop(CoprocessorEnvironment env) {
            super.stop(env);
            if (TestAccessController3.callSuperTwice) {
                super.stop(env);
            }
        }
    }

    @Test
    public void testTableCreate() throws Exception {
        SecureTestUtil.AccessTestAction createTable = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                HTableDescriptor htd = new HTableDescriptor(TableName.valueOf(name.getMethodName()));
                htd.addFamily(new HColumnDescriptor(TestAccessController3.TEST_FAMILY));
                TestAccessController3.ACCESS_CONTROLLER.preCreateTable(ObserverContextImpl.createAndPrepare(TestAccessController3.CP_ENV), htd, null);
                return null;
            }
        };
        // verify that superuser can create tables
        SecureTestUtil.verifyAllowed(createTable, TestAccessController3.SUPERUSER, TestAccessController3.USER_ADMIN, TestAccessController3.USER_GROUP_CREATE);
        // all others should be denied
        SecureTestUtil.verifyDenied(createTable, TestAccessController3.USER_CREATE, TestAccessController3.USER_RW, TestAccessController3.USER_RO, TestAccessController3.USER_NONE, TestAccessController3.USER_GROUP_ADMIN, TestAccessController3.USER_GROUP_READ, TestAccessController3.USER_GROUP_WRITE);
    }
}

