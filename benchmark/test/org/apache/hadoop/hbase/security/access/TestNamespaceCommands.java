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


import AccessControlLists.ACL_TABLE_NAME;
import AccessControlService.BlockingInterface;
import HConstants.EMPTY_START_ROW;
import Permission.Action.WRITE;
import com.google.protobuf.BlockingRpcChannel;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.coprocessor.MasterCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.ObserverContextImpl;
import org.apache.hadoop.hbase.protobuf.generated.AccessControlProtos.AccessControlService;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.security.access.Permission.Action;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.SecurityTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hbase.thirdparty.com.google.common.collect.ListMultimap;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ SecurityTests.class, MediumTests.class })
public class TestNamespaceCommands extends SecureTestUtil {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestNamespaceCommands.class);

    private static HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static final Logger LOG = LoggerFactory.getLogger(TestNamespaceCommands.class);

    private static String TEST_NAMESPACE = "ns1";

    private static String TEST_NAMESPACE2 = "ns2";

    private static Configuration conf;

    private static MasterCoprocessorEnvironment CP_ENV;

    private static AccessController ACCESS_CONTROLLER;

    // user with all permissions
    private static User SUPERUSER;

    // user with A permission on global
    private static User USER_GLOBAL_ADMIN;

    // user with C permission on global
    private static User USER_GLOBAL_CREATE;

    // user with W permission on global
    private static User USER_GLOBAL_WRITE;

    // user with R permission on global
    private static User USER_GLOBAL_READ;

    // user with X permission on global
    private static User USER_GLOBAL_EXEC;

    // user with A permission on namespace
    private static User USER_NS_ADMIN;

    // user with C permission on namespace
    private static User USER_NS_CREATE;

    // user with W permission on namespace
    private static User USER_NS_WRITE;

    // user with R permission on namespace.
    private static User USER_NS_READ;

    // user with X permission on namespace.
    private static User USER_NS_EXEC;

    // user with rw permissions
    private static User USER_TABLE_WRITE;// TODO: WE DO NOT GIVE ANY PERMS TO THIS USER


    // user with create table permissions alone
    private static User USER_TABLE_CREATE;// TODO: WE DO NOT GIVE ANY PERMS TO THIS USER


    private static final String GROUP_ADMIN = "group_admin";

    private static final String GROUP_NS_ADMIN = "group_ns_admin";

    private static final String GROUP_CREATE = "group_create";

    private static final String GROUP_READ = "group_read";

    private static final String GROUP_WRITE = "group_write";

    private static User USER_GROUP_ADMIN;

    private static User USER_GROUP_NS_ADMIN;

    private static User USER_GROUP_CREATE;

    private static User USER_GROUP_READ;

    private static User USER_GROUP_WRITE;

    private static String TEST_TABLE = (TestNamespaceCommands.TEST_NAMESPACE) + ":testtable";

    private static byte[] TEST_FAMILY = Bytes.toBytes("f1");

    @Test
    public void testAclTableEntries() throws Exception {
        String userTestNamespace = "userTestNsp";
        Table acl = TestNamespaceCommands.UTIL.getConnection().getTable(ACL_TABLE_NAME);
        try {
            ListMultimap<String, UserPermission> perms = AccessControlLists.getNamespacePermissions(TestNamespaceCommands.conf, TestNamespaceCommands.TEST_NAMESPACE);
            for (Map.Entry<String, UserPermission> entry : perms.entries()) {
                TestNamespaceCommands.LOG.debug(Objects.toString(entry));
            }
            Assert.assertEquals(6, perms.size());
            // Grant and check state in ACL table
            SecureTestUtil.grantOnNamespace(TestNamespaceCommands.UTIL, userTestNamespace, TestNamespaceCommands.TEST_NAMESPACE, WRITE);
            Result result = acl.get(new org.apache.hadoop.hbase.client.Get(Bytes.toBytes(userTestNamespace)));
            Assert.assertTrue((result != null));
            perms = AccessControlLists.getNamespacePermissions(TestNamespaceCommands.conf, TestNamespaceCommands.TEST_NAMESPACE);
            Assert.assertEquals(7, perms.size());
            List<UserPermission> namespacePerms = perms.get(userTestNamespace);
            Assert.assertTrue(perms.containsKey(userTestNamespace));
            Assert.assertEquals(1, namespacePerms.size());
            Assert.assertEquals(TestNamespaceCommands.TEST_NAMESPACE, getNamespace());
            Assert.assertEquals(1, namespacePerms.get(0).getPermission().getActions().length);
            Assert.assertEquals(WRITE, namespacePerms.get(0).getPermission().getActions()[0]);
            // Revoke and check state in ACL table
            SecureTestUtil.revokeFromNamespace(TestNamespaceCommands.UTIL, userTestNamespace, TestNamespaceCommands.TEST_NAMESPACE, WRITE);
            perms = AccessControlLists.getNamespacePermissions(TestNamespaceCommands.conf, TestNamespaceCommands.TEST_NAMESPACE);
            Assert.assertEquals(6, perms.size());
        } finally {
            acl.close();
        }
    }

    @Test
    public void testModifyNamespace() throws Exception {
        SecureTestUtil.AccessTestAction modifyNamespace = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                // not needed by AccessController
                TestNamespaceCommands.ACCESS_CONTROLLER.preModifyNamespace(ObserverContextImpl.createAndPrepare(TestNamespaceCommands.CP_ENV), null, NamespaceDescriptor.create(TestNamespaceCommands.TEST_NAMESPACE).addConfiguration("abc", "156").build());
                return null;
            }
        };
        // modifyNamespace: superuser | global(A) | NS(A)
        SecureTestUtil.verifyAllowed(modifyNamespace, TestNamespaceCommands.SUPERUSER, TestNamespaceCommands.USER_GLOBAL_ADMIN, TestNamespaceCommands.USER_GROUP_ADMIN);
        SecureTestUtil.verifyDenied(modifyNamespace, TestNamespaceCommands.USER_GLOBAL_CREATE, TestNamespaceCommands.USER_GLOBAL_WRITE, TestNamespaceCommands.USER_GLOBAL_READ, TestNamespaceCommands.USER_GLOBAL_EXEC, TestNamespaceCommands.USER_NS_ADMIN, TestNamespaceCommands.USER_NS_CREATE, TestNamespaceCommands.USER_NS_WRITE, TestNamespaceCommands.USER_NS_READ, TestNamespaceCommands.USER_NS_EXEC, TestNamespaceCommands.USER_GROUP_READ, TestNamespaceCommands.USER_GROUP_WRITE, TestNamespaceCommands.USER_GROUP_CREATE);
    }

    @Test
    public void testCreateAndDeleteNamespace() throws Exception {
        SecureTestUtil.AccessTestAction createNamespace = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestNamespaceCommands.ACCESS_CONTROLLER.preCreateNamespace(ObserverContextImpl.createAndPrepare(TestNamespaceCommands.CP_ENV), NamespaceDescriptor.create(TestNamespaceCommands.TEST_NAMESPACE2).build());
                return null;
            }
        };
        SecureTestUtil.AccessTestAction deleteNamespace = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestNamespaceCommands.ACCESS_CONTROLLER.preDeleteNamespace(ObserverContextImpl.createAndPrepare(TestNamespaceCommands.CP_ENV), TestNamespaceCommands.TEST_NAMESPACE2);
                return null;
            }
        };
        // createNamespace: superuser | global(A)
        SecureTestUtil.verifyAllowed(createNamespace, TestNamespaceCommands.SUPERUSER, TestNamespaceCommands.USER_GLOBAL_ADMIN, TestNamespaceCommands.USER_GROUP_ADMIN);
        // all others should be denied
        SecureTestUtil.verifyDenied(createNamespace, TestNamespaceCommands.USER_GLOBAL_CREATE, TestNamespaceCommands.USER_GLOBAL_WRITE, TestNamespaceCommands.USER_GLOBAL_READ, TestNamespaceCommands.USER_GLOBAL_EXEC, TestNamespaceCommands.USER_NS_ADMIN, TestNamespaceCommands.USER_NS_CREATE, TestNamespaceCommands.USER_NS_WRITE, TestNamespaceCommands.USER_NS_READ, TestNamespaceCommands.USER_NS_EXEC, TestNamespaceCommands.USER_TABLE_CREATE, TestNamespaceCommands.USER_TABLE_WRITE, TestNamespaceCommands.USER_GROUP_READ, TestNamespaceCommands.USER_GROUP_WRITE, TestNamespaceCommands.USER_GROUP_CREATE);
        // deleteNamespace: superuser | global(A) | NS(A)
        SecureTestUtil.verifyAllowed(deleteNamespace, TestNamespaceCommands.SUPERUSER, TestNamespaceCommands.USER_GLOBAL_ADMIN, TestNamespaceCommands.USER_GROUP_ADMIN);
        SecureTestUtil.verifyDenied(deleteNamespace, TestNamespaceCommands.USER_GLOBAL_CREATE, TestNamespaceCommands.USER_GLOBAL_WRITE, TestNamespaceCommands.USER_GLOBAL_READ, TestNamespaceCommands.USER_GLOBAL_EXEC, TestNamespaceCommands.USER_NS_ADMIN, TestNamespaceCommands.USER_NS_CREATE, TestNamespaceCommands.USER_NS_WRITE, TestNamespaceCommands.USER_NS_READ, TestNamespaceCommands.USER_NS_EXEC, TestNamespaceCommands.USER_TABLE_CREATE, TestNamespaceCommands.USER_TABLE_WRITE, TestNamespaceCommands.USER_GROUP_READ, TestNamespaceCommands.USER_GROUP_WRITE, TestNamespaceCommands.USER_GROUP_CREATE);
    }

    @Test
    public void testGetNamespaceDescriptor() throws Exception {
        SecureTestUtil.AccessTestAction getNamespaceAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestNamespaceCommands.ACCESS_CONTROLLER.preGetNamespaceDescriptor(ObserverContextImpl.createAndPrepare(TestNamespaceCommands.CP_ENV), TestNamespaceCommands.TEST_NAMESPACE);
                return null;
            }
        };
        // getNamespaceDescriptor : superuser | global(A) | NS(A)
        SecureTestUtil.verifyAllowed(getNamespaceAction, TestNamespaceCommands.SUPERUSER, TestNamespaceCommands.USER_GLOBAL_ADMIN, TestNamespaceCommands.USER_NS_ADMIN, TestNamespaceCommands.USER_GROUP_ADMIN);
        SecureTestUtil.verifyDenied(getNamespaceAction, TestNamespaceCommands.USER_GLOBAL_CREATE, TestNamespaceCommands.USER_GLOBAL_WRITE, TestNamespaceCommands.USER_GLOBAL_READ, TestNamespaceCommands.USER_GLOBAL_EXEC, TestNamespaceCommands.USER_NS_CREATE, TestNamespaceCommands.USER_NS_WRITE, TestNamespaceCommands.USER_NS_READ, TestNamespaceCommands.USER_NS_EXEC, TestNamespaceCommands.USER_TABLE_CREATE, TestNamespaceCommands.USER_TABLE_WRITE, TestNamespaceCommands.USER_GROUP_READ, TestNamespaceCommands.USER_GROUP_WRITE, TestNamespaceCommands.USER_GROUP_CREATE);
    }

    @Test
    public void testListNamespaces() throws Exception {
        SecureTestUtil.AccessTestAction listAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                Connection unmanagedConnection = ConnectionFactory.createConnection(TestNamespaceCommands.UTIL.getConfiguration());
                Admin admin = unmanagedConnection.getAdmin();
                try {
                    return Arrays.asList(admin.listNamespaceDescriptors());
                } finally {
                    admin.close();
                    unmanagedConnection.close();
                }
            }
        };
        // listNamespaces         : All access*
        // * Returned list will only show what you can call getNamespaceDescriptor()
        SecureTestUtil.verifyAllowed(listAction, TestNamespaceCommands.SUPERUSER, TestNamespaceCommands.USER_GLOBAL_ADMIN, TestNamespaceCommands.USER_NS_ADMIN, TestNamespaceCommands.USER_GROUP_ADMIN);
        // we have 3 namespaces: [default, hbase, TEST_NAMESPACE, TEST_NAMESPACE2]
        Assert.assertEquals(4, ((List) (TestNamespaceCommands.SUPERUSER.runAs(listAction))).size());
        Assert.assertEquals(4, ((List) (TestNamespaceCommands.USER_GLOBAL_ADMIN.runAs(listAction))).size());
        Assert.assertEquals(4, ((List) (TestNamespaceCommands.USER_GROUP_ADMIN.runAs(listAction))).size());
        Assert.assertEquals(2, ((List) (TestNamespaceCommands.USER_NS_ADMIN.runAs(listAction))).size());
        Assert.assertEquals(0, ((List) (TestNamespaceCommands.USER_GLOBAL_CREATE.runAs(listAction))).size());
        Assert.assertEquals(0, ((List) (TestNamespaceCommands.USER_GLOBAL_WRITE.runAs(listAction))).size());
        Assert.assertEquals(0, ((List) (TestNamespaceCommands.USER_GLOBAL_READ.runAs(listAction))).size());
        Assert.assertEquals(0, ((List) (TestNamespaceCommands.USER_GLOBAL_EXEC.runAs(listAction))).size());
        Assert.assertEquals(0, ((List) (TestNamespaceCommands.USER_NS_CREATE.runAs(listAction))).size());
        Assert.assertEquals(0, ((List) (TestNamespaceCommands.USER_NS_WRITE.runAs(listAction))).size());
        Assert.assertEquals(0, ((List) (TestNamespaceCommands.USER_NS_READ.runAs(listAction))).size());
        Assert.assertEquals(0, ((List) (TestNamespaceCommands.USER_NS_EXEC.runAs(listAction))).size());
        Assert.assertEquals(0, ((List) (TestNamespaceCommands.USER_TABLE_CREATE.runAs(listAction))).size());
        Assert.assertEquals(0, ((List) (TestNamespaceCommands.USER_TABLE_WRITE.runAs(listAction))).size());
        Assert.assertEquals(0, ((List) (TestNamespaceCommands.USER_GROUP_CREATE.runAs(listAction))).size());
        Assert.assertEquals(0, ((List) (TestNamespaceCommands.USER_GROUP_READ.runAs(listAction))).size());
        Assert.assertEquals(0, ((List) (TestNamespaceCommands.USER_GROUP_WRITE.runAs(listAction))).size());
    }

    @Test
    public void testGrantRevoke() throws Exception {
        final String testUser = "testUser";
        // Test if client API actions are authorized
        SecureTestUtil.AccessTestAction grantAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                try (Connection connection = ConnectionFactory.createConnection(TestNamespaceCommands.conf)) {
                    connection.getAdmin().grant(testUser, new NamespacePermission(TestNamespaceCommands.TEST_NAMESPACE, Action.WRITE), false);
                }
                return null;
            }
        };
        SecureTestUtil.AccessTestAction grantNamespaceAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                try (Connection conn = ConnectionFactory.createConnection(TestNamespaceCommands.conf)) {
                    conn.getAdmin().grant(TestNamespaceCommands.USER_GROUP_NS_ADMIN.getShortName(), new NamespacePermission(TestNamespaceCommands.TEST_NAMESPACE, Action.READ), false);
                }
                return null;
            }
        };
        SecureTestUtil.AccessTestAction revokeAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                try (Connection connection = ConnectionFactory.createConnection(TestNamespaceCommands.conf)) {
                    connection.getAdmin().revoke(testUser, new NamespacePermission(TestNamespaceCommands.TEST_NAMESPACE, Action.WRITE));
                }
                return null;
            }
        };
        SecureTestUtil.AccessTestAction revokeNamespaceAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                try (Connection connection = ConnectionFactory.createConnection(TestNamespaceCommands.conf)) {
                    connection.getAdmin().revoke(TestNamespaceCommands.USER_GROUP_NS_ADMIN.getShortName(), new NamespacePermission(TestNamespaceCommands.TEST_NAMESPACE, Action.READ));
                }
                return null;
            }
        };
        SecureTestUtil.AccessTestAction getPermissionsAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                try (Connection connection = ConnectionFactory.createConnection(TestNamespaceCommands.conf);Table acl = connection.getTable(ACL_TABLE_NAME)) {
                    BlockingRpcChannel service = acl.coprocessorService(EMPTY_START_ROW);
                    AccessControlService.BlockingInterface protocol = AccessControlService.newBlockingStub(service);
                    AccessControlUtil.getUserPermissions(null, protocol, Bytes.toBytes(TestNamespaceCommands.TEST_NAMESPACE));
                }
                return null;
            }
        };
        SecureTestUtil.AccessTestAction preGrantAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestNamespaceCommands.ACCESS_CONTROLLER.preGrant(ObserverContextImpl.createAndPrepare(TestNamespaceCommands.CP_ENV), new UserPermission(testUser, new NamespacePermission(TestNamespaceCommands.TEST_NAMESPACE, Action.WRITE)), false);
                return null;
            }
        };
        SecureTestUtil.AccessTestAction preRevokeAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestNamespaceCommands.ACCESS_CONTROLLER.preRevoke(ObserverContextImpl.createAndPrepare(TestNamespaceCommands.CP_ENV), new UserPermission(testUser, new NamespacePermission(TestNamespaceCommands.TEST_NAMESPACE, Action.WRITE)));
                return null;
            }
        };
        SecureTestUtil.AccessTestAction grantCPAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                try (Connection connection = ConnectionFactory.createConnection(TestNamespaceCommands.conf);Table acl = connection.getTable(ACL_TABLE_NAME)) {
                    BlockingRpcChannel service = acl.coprocessorService(EMPTY_START_ROW);
                    AccessControlService.BlockingInterface protocol = AccessControlService.newBlockingStub(service);
                    AccessControlUtil.grant(null, protocol, testUser, TestNamespaceCommands.TEST_NAMESPACE, false, Action.WRITE);
                }
                return null;
            }
        };
        SecureTestUtil.AccessTestAction revokeCPAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                try (Connection connection = ConnectionFactory.createConnection(TestNamespaceCommands.conf);Table acl = connection.getTable(ACL_TABLE_NAME)) {
                    BlockingRpcChannel service = acl.coprocessorService(EMPTY_START_ROW);
                    AccessControlService.BlockingInterface protocol = AccessControlService.newBlockingStub(service);
                    AccessControlUtil.revoke(null, protocol, testUser, TestNamespaceCommands.TEST_NAMESPACE, Action.WRITE);
                }
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(grantAction, TestNamespaceCommands.SUPERUSER, TestNamespaceCommands.USER_GLOBAL_ADMIN, TestNamespaceCommands.USER_GROUP_ADMIN, TestNamespaceCommands.USER_NS_ADMIN);
        SecureTestUtil.verifyDenied(grantAction, TestNamespaceCommands.USER_GLOBAL_CREATE, TestNamespaceCommands.USER_GLOBAL_WRITE, TestNamespaceCommands.USER_GLOBAL_READ, TestNamespaceCommands.USER_GLOBAL_EXEC, TestNamespaceCommands.USER_NS_CREATE, TestNamespaceCommands.USER_NS_WRITE, TestNamespaceCommands.USER_NS_READ, TestNamespaceCommands.USER_NS_EXEC, TestNamespaceCommands.USER_TABLE_CREATE, TestNamespaceCommands.USER_TABLE_WRITE, TestNamespaceCommands.USER_GROUP_READ, TestNamespaceCommands.USER_GROUP_WRITE, TestNamespaceCommands.USER_GROUP_CREATE);
        SecureTestUtil.verifyAllowed(grantNamespaceAction, TestNamespaceCommands.SUPERUSER, TestNamespaceCommands.USER_GLOBAL_ADMIN, TestNamespaceCommands.USER_GROUP_ADMIN, TestNamespaceCommands.USER_NS_ADMIN, TestNamespaceCommands.USER_GROUP_NS_ADMIN);
        SecureTestUtil.verifyDenied(grantNamespaceAction, TestNamespaceCommands.USER_GLOBAL_CREATE, TestNamespaceCommands.USER_GLOBAL_WRITE, TestNamespaceCommands.USER_GLOBAL_READ, TestNamespaceCommands.USER_GLOBAL_EXEC, TestNamespaceCommands.USER_NS_CREATE, TestNamespaceCommands.USER_NS_WRITE, TestNamespaceCommands.USER_NS_READ, TestNamespaceCommands.USER_NS_EXEC, TestNamespaceCommands.USER_TABLE_CREATE, TestNamespaceCommands.USER_TABLE_WRITE, TestNamespaceCommands.USER_GROUP_READ, TestNamespaceCommands.USER_GROUP_WRITE, TestNamespaceCommands.USER_GROUP_CREATE);
        SecureTestUtil.verifyAllowed(revokeAction, TestNamespaceCommands.SUPERUSER, TestNamespaceCommands.USER_GLOBAL_ADMIN, TestNamespaceCommands.USER_GROUP_ADMIN, TestNamespaceCommands.USER_NS_ADMIN);
        SecureTestUtil.verifyDenied(revokeAction, TestNamespaceCommands.USER_GLOBAL_CREATE, TestNamespaceCommands.USER_GLOBAL_WRITE, TestNamespaceCommands.USER_GLOBAL_READ, TestNamespaceCommands.USER_GLOBAL_EXEC, TestNamespaceCommands.USER_NS_CREATE, TestNamespaceCommands.USER_NS_WRITE, TestNamespaceCommands.USER_NS_READ, TestNamespaceCommands.USER_NS_EXEC, TestNamespaceCommands.USER_TABLE_CREATE, TestNamespaceCommands.USER_TABLE_WRITE, TestNamespaceCommands.USER_GROUP_READ, TestNamespaceCommands.USER_GROUP_WRITE, TestNamespaceCommands.USER_GROUP_CREATE);
        SecureTestUtil.verifyAllowed(revokeNamespaceAction, TestNamespaceCommands.SUPERUSER, TestNamespaceCommands.USER_GLOBAL_ADMIN, TestNamespaceCommands.USER_GROUP_ADMIN, TestNamespaceCommands.USER_NS_ADMIN, TestNamespaceCommands.USER_GROUP_NS_ADMIN);
        SecureTestUtil.verifyDenied(revokeNamespaceAction, TestNamespaceCommands.USER_GLOBAL_CREATE, TestNamespaceCommands.USER_GLOBAL_WRITE, TestNamespaceCommands.USER_GLOBAL_READ, TestNamespaceCommands.USER_GLOBAL_EXEC, TestNamespaceCommands.USER_NS_CREATE, TestNamespaceCommands.USER_NS_WRITE, TestNamespaceCommands.USER_NS_READ, TestNamespaceCommands.USER_NS_EXEC, TestNamespaceCommands.USER_TABLE_CREATE, TestNamespaceCommands.USER_TABLE_WRITE, TestNamespaceCommands.USER_GROUP_READ, TestNamespaceCommands.USER_GROUP_WRITE, TestNamespaceCommands.USER_GROUP_CREATE);
        SecureTestUtil.verifyAllowed(getPermissionsAction, TestNamespaceCommands.SUPERUSER, TestNamespaceCommands.USER_GLOBAL_ADMIN, TestNamespaceCommands.USER_NS_ADMIN, TestNamespaceCommands.USER_GROUP_ADMIN);
        SecureTestUtil.verifyDenied(getPermissionsAction, TestNamespaceCommands.USER_GLOBAL_CREATE, TestNamespaceCommands.USER_GLOBAL_WRITE, TestNamespaceCommands.USER_GLOBAL_READ, TestNamespaceCommands.USER_GLOBAL_EXEC, TestNamespaceCommands.USER_NS_CREATE, TestNamespaceCommands.USER_NS_WRITE, TestNamespaceCommands.USER_NS_READ, TestNamespaceCommands.USER_NS_EXEC, TestNamespaceCommands.USER_TABLE_CREATE, TestNamespaceCommands.USER_TABLE_WRITE, TestNamespaceCommands.USER_GROUP_READ, TestNamespaceCommands.USER_GROUP_WRITE, TestNamespaceCommands.USER_GROUP_CREATE);
        SecureTestUtil.verifyAllowed(preGrantAction, TestNamespaceCommands.SUPERUSER, TestNamespaceCommands.USER_GLOBAL_ADMIN, TestNamespaceCommands.USER_GROUP_ADMIN, TestNamespaceCommands.USER_NS_ADMIN);
        SecureTestUtil.verifyDenied(preGrantAction, TestNamespaceCommands.USER_GLOBAL_CREATE, TestNamespaceCommands.USER_GLOBAL_WRITE, TestNamespaceCommands.USER_GLOBAL_READ, TestNamespaceCommands.USER_GLOBAL_EXEC, TestNamespaceCommands.USER_NS_CREATE, TestNamespaceCommands.USER_NS_WRITE, TestNamespaceCommands.USER_NS_READ, TestNamespaceCommands.USER_NS_EXEC, TestNamespaceCommands.USER_TABLE_CREATE, TestNamespaceCommands.USER_TABLE_WRITE, TestNamespaceCommands.USER_GROUP_READ, TestNamespaceCommands.USER_GROUP_WRITE, TestNamespaceCommands.USER_GROUP_CREATE);
        SecureTestUtil.verifyAllowed(preRevokeAction, TestNamespaceCommands.SUPERUSER, TestNamespaceCommands.USER_GLOBAL_ADMIN, TestNamespaceCommands.USER_GROUP_ADMIN, TestNamespaceCommands.USER_NS_ADMIN);
        SecureTestUtil.verifyDenied(preRevokeAction, TestNamespaceCommands.USER_GLOBAL_CREATE, TestNamespaceCommands.USER_GLOBAL_WRITE, TestNamespaceCommands.USER_GLOBAL_READ, TestNamespaceCommands.USER_GLOBAL_EXEC, TestNamespaceCommands.USER_NS_CREATE, TestNamespaceCommands.USER_NS_WRITE, TestNamespaceCommands.USER_NS_READ, TestNamespaceCommands.USER_NS_EXEC, TestNamespaceCommands.USER_TABLE_CREATE, TestNamespaceCommands.USER_TABLE_WRITE, TestNamespaceCommands.USER_GROUP_READ, TestNamespaceCommands.USER_GROUP_WRITE, TestNamespaceCommands.USER_GROUP_CREATE);
        SecureTestUtil.verifyAllowed(grantCPAction, TestNamespaceCommands.SUPERUSER, TestNamespaceCommands.USER_GLOBAL_ADMIN, TestNamespaceCommands.USER_GROUP_ADMIN, TestNamespaceCommands.USER_NS_ADMIN);
        SecureTestUtil.verifyDenied(grantCPAction, TestNamespaceCommands.USER_GLOBAL_CREATE, TestNamespaceCommands.USER_GLOBAL_WRITE, TestNamespaceCommands.USER_GLOBAL_READ, TestNamespaceCommands.USER_GLOBAL_EXEC, TestNamespaceCommands.USER_NS_CREATE, TestNamespaceCommands.USER_NS_WRITE, TestNamespaceCommands.USER_NS_READ, TestNamespaceCommands.USER_NS_EXEC, TestNamespaceCommands.USER_TABLE_CREATE, TestNamespaceCommands.USER_TABLE_WRITE, TestNamespaceCommands.USER_GROUP_READ, TestNamespaceCommands.USER_GROUP_WRITE, TestNamespaceCommands.USER_GROUP_CREATE);
        SecureTestUtil.verifyAllowed(revokeCPAction, TestNamespaceCommands.SUPERUSER, TestNamespaceCommands.USER_GLOBAL_ADMIN, TestNamespaceCommands.USER_GROUP_ADMIN, TestNamespaceCommands.USER_NS_ADMIN);
        SecureTestUtil.verifyDenied(revokeCPAction, TestNamespaceCommands.USER_GLOBAL_CREATE, TestNamespaceCommands.USER_GLOBAL_WRITE, TestNamespaceCommands.USER_GLOBAL_READ, TestNamespaceCommands.USER_GLOBAL_EXEC, TestNamespaceCommands.USER_NS_CREATE, TestNamespaceCommands.USER_NS_WRITE, TestNamespaceCommands.USER_NS_READ, TestNamespaceCommands.USER_NS_EXEC, TestNamespaceCommands.USER_TABLE_CREATE, TestNamespaceCommands.USER_TABLE_WRITE, TestNamespaceCommands.USER_GROUP_READ, TestNamespaceCommands.USER_GROUP_WRITE, TestNamespaceCommands.USER_GROUP_CREATE);
    }

    @Test
    public void testCreateTableWithNamespace() throws Exception {
        SecureTestUtil.AccessTestAction createTable = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                HTableDescriptor htd = new HTableDescriptor(TableName.valueOf(TestNamespaceCommands.TEST_TABLE));
                htd.addFamily(new HColumnDescriptor(TestNamespaceCommands.TEST_FAMILY));
                TestNamespaceCommands.ACCESS_CONTROLLER.preCreateTable(ObserverContextImpl.createAndPrepare(TestNamespaceCommands.CP_ENV), htd, null);
                return null;
            }
        };
        // createTable            : superuser | global(C) | NS(C)
        SecureTestUtil.verifyAllowed(createTable, TestNamespaceCommands.SUPERUSER, TestNamespaceCommands.USER_GLOBAL_CREATE, TestNamespaceCommands.USER_NS_CREATE, TestNamespaceCommands.USER_GROUP_CREATE);
        SecureTestUtil.verifyDenied(createTable, TestNamespaceCommands.USER_GLOBAL_ADMIN, TestNamespaceCommands.USER_GLOBAL_WRITE, TestNamespaceCommands.USER_GLOBAL_READ, TestNamespaceCommands.USER_GLOBAL_EXEC, TestNamespaceCommands.USER_NS_ADMIN, TestNamespaceCommands.USER_NS_WRITE, TestNamespaceCommands.USER_NS_READ, TestNamespaceCommands.USER_NS_EXEC, TestNamespaceCommands.USER_TABLE_CREATE, TestNamespaceCommands.USER_TABLE_WRITE, TestNamespaceCommands.USER_GROUP_READ, TestNamespaceCommands.USER_GROUP_WRITE, TestNamespaceCommands.USER_GROUP_ADMIN);
    }
}

