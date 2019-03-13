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
import AccessControlProtos.Permission.Action.CREATE;
import AccessControlProtos.TablePermission;
import AccessControlService.BlockingInterface;
import Action.READ;
import Coprocessor.PRIORITY_USER;
import FlushLifeCycleTracker.DUMMY;
import HConstants.EMPTY_BYTE_ARRAY;
import HConstants.EMPTY_START_ROW;
import HConstants.EMPTY_STRING;
import HFile.Writer;
import JVMClusterUtil.RegionServerThread;
import MasterSwitchType.MERGE;
import NamespaceDescriptor.DEFAULT_NAMESPACE;
import Permission.Action.ADMIN;
import Permission.Action.EXEC;
import Permission.Action.WRITE;
import ScanType.COMPACT_RETAIN_DELETES;
import SecurityCapability.AUTHORIZATION;
import SecurityCapability.CELL_AUTHORIZATION;
import SyncReplicationState.NONE;
import TestProcedureProtos.TestTableDDLStateData.Builder;
import com.google.protobuf.BlockingRpcChannel;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.google.protobuf.Service;
import com.google.protobuf.ServiceException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hbase.AuthUtil;
import org.apache.hadoop.hbase.CoprocessorEnvironment;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseIOException;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.HRegionLocation;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.MiniHBaseCluster;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Append;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.RegionLocator;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.SnapshotDescription;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.security.SecurityCapability;
import org.apache.hadoop.hbase.coprocessor.MasterCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.ObserverContextImpl;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.RegionServerCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.protobuf.generated.PingProtos.CountRequest;
import org.apache.hadoop.hbase.coprocessor.protobuf.generated.PingProtos.CountResponse;
import org.apache.hadoop.hbase.coprocessor.protobuf.generated.PingProtos.HelloRequest;
import org.apache.hadoop.hbase.coprocessor.protobuf.generated.PingProtos.HelloResponse;
import org.apache.hadoop.hbase.coprocessor.protobuf.generated.PingProtos.IncrementCountRequest;
import org.apache.hadoop.hbase.coprocessor.protobuf.generated.PingProtos.IncrementCountResponse;
import org.apache.hadoop.hbase.coprocessor.protobuf.generated.PingProtos.NoopRequest;
import org.apache.hadoop.hbase.coprocessor.protobuf.generated.PingProtos.NoopResponse;
import org.apache.hadoop.hbase.coprocessor.protobuf.generated.PingProtos.PingRequest;
import org.apache.hadoop.hbase.coprocessor.protobuf.generated.PingProtos.PingResponse;
import org.apache.hadoop.hbase.coprocessor.protobuf.generated.PingProtos.PingService;
import org.apache.hadoop.hbase.io.hfile.HFile;
import org.apache.hadoop.hbase.io.hfile.HFileContext;
import org.apache.hadoop.hbase.io.hfile.HFileContextBuilder;
import org.apache.hadoop.hbase.master.HMaster;
import org.apache.hadoop.hbase.master.locking.LockProcedure;
import org.apache.hadoop.hbase.master.procedure.MasterProcedureEnv;
import org.apache.hadoop.hbase.master.procedure.TableProcedureInterface;
import org.apache.hadoop.hbase.procedure2.LockType;
import org.apache.hadoop.hbase.procedure2.Procedure;
import org.apache.hadoop.hbase.procedure2.ProcedureExecutor;
import org.apache.hadoop.hbase.procedure2.ProcedureStateSerializer;
import org.apache.hadoop.hbase.procedure2.ProcedureYieldException;
import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.protobuf.generated.AccessControlProtos.AccessControlService;
import org.apache.hadoop.hbase.protobuf.generated.AccessControlProtos.CheckPermissionsRequest;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.replication.ReplicationPeerConfig;
import org.apache.hadoop.hbase.security.Superusers;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.security.access.Permission.Action;
import org.apache.hadoop.hbase.shaded.ipc.protobuf.generated.TestProcedureProtos;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.SecurityTests;
import org.apache.hadoop.hbase.tool.LoadIncrementalHFiles;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.JVMClusterUtil;
import org.apache.hadoop.hbase.util.Threads;
import org.apache.hadoop.security.GroupMappingServiceProvider;
import org.apache.hadoop.security.ShellBasedUnixGroupsMapping;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static TableOperationType.EDIT;


/**
 * Performs authorization checks for common operations, according to different
 * levels of authorized users.
 */
@Category({ SecurityTests.class, LargeTests.class })
public class TestAccessController extends SecureTestUtil {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAccessController.class);

    private static final FsPermission FS_PERMISSION_ALL = FsPermission.valueOf("-rwxrwxrwx");

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
    private static TableName TEST_TABLE2 = TableName.valueOf("testtable2");

    private static byte[] TEST_FAMILY = Bytes.toBytes("f1");

    private static byte[] TEST_QUALIFIER = Bytes.toBytes("q1");

    private static byte[] TEST_ROW = Bytes.toBytes("r1");

    private static MasterCoprocessorEnvironment CP_ENV;

    private static AccessController ACCESS_CONTROLLER;

    private static RegionServerCoprocessorEnvironment RSCP_ENV;

    private static RegionCoprocessorEnvironment RCP_ENV;

    @Rule
    public TestName name = new TestName();

    @Test
    public void testUnauthorizedShutdown() throws Exception {
        SecureTestUtil.AccessTestAction action = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                HMaster master = TestAccessController.TEST_UTIL.getHBaseCluster().getMaster();
                master.shutdown();
                return null;
            }
        };
        SecureTestUtil.verifyDenied(action, TestAccessController.USER_CREATE, TestAccessController.USER_OWNER, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE, TestAccessController.USER_GROUP_CREATE);
    }

    @Test
    public void testUnauthorizedStopMaster() throws Exception {
        SecureTestUtil.AccessTestAction action = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                HMaster master = TestAccessController.TEST_UTIL.getHBaseCluster().getMaster();
                master.stopMaster();
                return null;
            }
        };
        SecureTestUtil.verifyDenied(action, TestAccessController.USER_CREATE, TestAccessController.USER_OWNER, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE, TestAccessController.USER_GROUP_CREATE);
    }

    @Test
    public void testSecurityCapabilities() throws Exception {
        List<SecurityCapability> capabilities = TestAccessController.TEST_UTIL.getConnection().getAdmin().getSecurityCapabilities();
        Assert.assertTrue("AUTHORIZATION capability is missing", capabilities.contains(AUTHORIZATION));
        Assert.assertTrue("CELL_AUTHORIZATION capability is missing", capabilities.contains(CELL_AUTHORIZATION));
    }

    @Test
    public void testTableCreate() throws Exception {
        SecureTestUtil.AccessTestAction createTable = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                HTableDescriptor htd = new HTableDescriptor(TableName.valueOf(name.getMethodName()));
                htd.addFamily(new HColumnDescriptor(TestAccessController.TEST_FAMILY));
                TestAccessController.ACCESS_CONTROLLER.preCreateTable(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), htd, null);
                return null;
            }
        };
        // verify that superuser can create tables
        SecureTestUtil.verifyAllowed(createTable, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_GROUP_CREATE);
        // all others should be denied
        SecureTestUtil.verifyDenied(createTable, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_GROUP_ADMIN, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE);
    }

    @Test
    public void testTableModify() throws Exception {
        SecureTestUtil.AccessTestAction modifyTable = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                HTableDescriptor htd = new HTableDescriptor(TestAccessController.TEST_TABLE);
                htd.addFamily(new HColumnDescriptor(TestAccessController.TEST_FAMILY));
                htd.addFamily(new HColumnDescriptor(("fam_" + (User.getCurrent().getShortName()))));
                // not needed by AccessController
                TestAccessController.ACCESS_CONTROLLER.preModifyTable(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), TestAccessController.TEST_TABLE, null, htd);
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(modifyTable, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_CREATE, TestAccessController.USER_OWNER, TestAccessController.USER_GROUP_CREATE, TestAccessController.USER_GROUP_ADMIN);
        SecureTestUtil.verifyDenied(modifyTable, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE);
    }

    @Test
    public void testTableDelete() throws Exception {
        SecureTestUtil.AccessTestAction deleteTable = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preDeleteTable(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), TestAccessController.TEST_TABLE);
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(deleteTable, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_CREATE, TestAccessController.USER_OWNER, TestAccessController.USER_GROUP_CREATE, TestAccessController.USER_GROUP_ADMIN);
        SecureTestUtil.verifyDenied(deleteTable, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE);
    }

    @Test
    public void testTableTruncate() throws Exception {
        SecureTestUtil.AccessTestAction truncateTable = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preTruncateTable(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), TestAccessController.TEST_TABLE);
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(truncateTable, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_CREATE, TestAccessController.USER_OWNER, TestAccessController.USER_GROUP_CREATE, TestAccessController.USER_GROUP_ADMIN);
        SecureTestUtil.verifyDenied(truncateTable, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE);
    }

    @Test
    public void testTableDisable() throws Exception {
        SecureTestUtil.AccessTestAction disableTable = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preDisableTable(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), TestAccessController.TEST_TABLE);
                return null;
            }
        };
        SecureTestUtil.AccessTestAction disableAclTable = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preDisableTable(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), ACL_TABLE_NAME);
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(disableTable, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_CREATE, TestAccessController.USER_OWNER, TestAccessController.USER_GROUP_CREATE, TestAccessController.USER_GROUP_ADMIN);
        SecureTestUtil.verifyDenied(disableTable, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE);
        // No user should be allowed to disable _acl_ table
        SecureTestUtil.verifyDenied(disableAclTable, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_CREATE, TestAccessController.USER_OWNER, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_GROUP_CREATE, TestAccessController.USER_GROUP_ADMIN, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE);
    }

    @Test
    public void testTableEnable() throws Exception {
        SecureTestUtil.AccessTestAction enableTable = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preEnableTable(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), TestAccessController.TEST_TABLE);
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(enableTable, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_CREATE, TestAccessController.USER_OWNER, TestAccessController.USER_GROUP_CREATE, TestAccessController.USER_GROUP_ADMIN);
        SecureTestUtil.verifyDenied(enableTable, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE);
    }

    public static class TestTableDDLProcedure extends Procedure<MasterProcedureEnv> implements TableProcedureInterface {
        private TableName tableName;

        public TestTableDDLProcedure() {
        }

        public TestTableDDLProcedure(final MasterProcedureEnv env, final TableName tableName) throws IOException {
            this.tableName = tableName;
            setTimeout(180000);// Timeout in 3 minutes

            this.setOwner(env.getRequestUser());
        }

        @Override
        public TableName getTableName() {
            return tableName;
        }

        @Override
        public TableOperationType getTableOperationType() {
            return EDIT;
        }

        @Override
        protected boolean abort(MasterProcedureEnv env) {
            return true;
        }

        @Override
        protected void serializeStateData(ProcedureStateSerializer serializer) throws IOException {
            TestProcedureProtos.TestTableDDLStateData.Builder testTableDDLMsg = TestProcedureProtos.TestTableDDLStateData.newBuilder().setTableName(tableName.getNameAsString());
            serializer.serialize(testTableDDLMsg.build());
        }

        @Override
        protected void deserializeStateData(ProcedureStateSerializer serializer) throws IOException {
            TestProcedureProtos.TestTableDDLStateData testTableDDLMsg = serializer.deserialize(TestProcedureProtos.TestTableDDLStateData.class);
            tableName = TableName.valueOf(testTableDDLMsg.getTableName());
        }

        @Override
        protected Procedure[] execute(MasterProcedureEnv env) throws InterruptedException, ProcedureYieldException {
            // Not letting the procedure to complete until timed out
            setState(ProcedureState.WAITING_TIMEOUT);
            return null;
        }

        @Override
        protected void rollback(MasterProcedureEnv env) throws IOException, InterruptedException {
        }
    }

    @Test
    public void testAbortProcedure() throws Exception {
        long procId = 1;
        SecureTestUtil.AccessTestAction abortProcedureAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preAbortProcedure(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), procId);
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(abortProcedureAction, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_GROUP_ADMIN);
    }

    @Test
    public void testGetProcedures() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final ProcedureExecutor<MasterProcedureEnv> procExec = TestAccessController.TEST_UTIL.getHBaseCluster().getMaster().getMasterProcedureExecutor();
        Procedure proc = new TestAccessController.TestTableDDLProcedure(procExec.getEnvironment(), tableName);
        proc.setOwner(TestAccessController.USER_OWNER);
        procExec.submitProcedure(proc);
        final List<Procedure<MasterProcedureEnv>> procList = procExec.getProcedures();
        SecureTestUtil.AccessTestAction getProceduresAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.postGetProcedures(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV));
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(getProceduresAction, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_GROUP_ADMIN);
        SecureTestUtil.verifyAllowed(getProceduresAction, TestAccessController.USER_OWNER);
        SecureTestUtil.verifyIfNull(getProceduresAction, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE);
    }

    @Test
    public void testGetLocks() throws Exception {
        SecureTestUtil.AccessTestAction action = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preGetLocks(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV));
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(action, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_GROUP_ADMIN);
        SecureTestUtil.verifyDenied(action, TestAccessController.USER_CREATE, TestAccessController.USER_OWNER, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE, TestAccessController.USER_GROUP_CREATE);
    }

    @Test
    public void testMove() throws Exception {
        List<HRegionLocation> regions;
        try (RegionLocator locator = TestAccessController.systemUserConnection.getRegionLocator(TestAccessController.TEST_TABLE)) {
            regions = locator.getAllRegionLocations();
        }
        HRegionLocation location = regions.get(0);
        final HRegionInfo hri = location.getRegionInfo();
        final ServerName server = location.getServerName();
        SecureTestUtil.AccessTestAction action = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preMove(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), hri, server, server);
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(action, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_OWNER, TestAccessController.USER_GROUP_ADMIN);
        SecureTestUtil.verifyDenied(action, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE, TestAccessController.USER_GROUP_CREATE);
    }

    @Test
    public void testAssign() throws Exception {
        List<HRegionLocation> regions;
        try (RegionLocator locator = TestAccessController.systemUserConnection.getRegionLocator(TestAccessController.TEST_TABLE)) {
            regions = locator.getAllRegionLocations();
        }
        HRegionLocation location = regions.get(0);
        final HRegionInfo hri = location.getRegionInfo();
        SecureTestUtil.AccessTestAction action = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preAssign(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), hri);
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(action, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_OWNER, TestAccessController.USER_GROUP_ADMIN);
        SecureTestUtil.verifyDenied(action, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE, TestAccessController.USER_GROUP_CREATE);
    }

    @Test
    public void testUnassign() throws Exception {
        List<HRegionLocation> regions;
        try (RegionLocator locator = TestAccessController.systemUserConnection.getRegionLocator(TestAccessController.TEST_TABLE)) {
            regions = locator.getAllRegionLocations();
        }
        HRegionLocation location = regions.get(0);
        final HRegionInfo hri = location.getRegionInfo();
        SecureTestUtil.AccessTestAction action = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preUnassign(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), hri, false);
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(action, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_OWNER, TestAccessController.USER_GROUP_ADMIN);
        SecureTestUtil.verifyDenied(action, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE, TestAccessController.USER_GROUP_CREATE);
    }

    @Test
    public void testRegionOffline() throws Exception {
        List<HRegionLocation> regions;
        try (RegionLocator locator = TestAccessController.systemUserConnection.getRegionLocator(TestAccessController.TEST_TABLE)) {
            regions = locator.getAllRegionLocations();
        }
        HRegionLocation location = regions.get(0);
        final HRegionInfo hri = location.getRegionInfo();
        SecureTestUtil.AccessTestAction action = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preRegionOffline(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), hri);
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(action, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_OWNER, TestAccessController.USER_GROUP_ADMIN);
        SecureTestUtil.verifyDenied(action, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE, TestAccessController.USER_GROUP_CREATE);
    }

    @Test
    public void testSetSplitOrMergeEnabled() throws Exception {
        SecureTestUtil.AccessTestAction action = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preSetSplitOrMergeEnabled(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), true, MERGE);
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(action, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_GROUP_ADMIN);
        SecureTestUtil.verifyDenied(action, TestAccessController.USER_CREATE, TestAccessController.USER_OWNER, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE, TestAccessController.USER_GROUP_CREATE);
    }

    @Test
    public void testBalance() throws Exception {
        SecureTestUtil.AccessTestAction action = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preBalance(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV));
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(action, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_GROUP_ADMIN);
        SecureTestUtil.verifyDenied(action, TestAccessController.USER_CREATE, TestAccessController.USER_OWNER, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE, TestAccessController.USER_GROUP_CREATE);
    }

    @Test
    public void testBalanceSwitch() throws Exception {
        SecureTestUtil.AccessTestAction action = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preBalanceSwitch(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), true);
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(action, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_GROUP_ADMIN);
        SecureTestUtil.verifyDenied(action, TestAccessController.USER_CREATE, TestAccessController.USER_OWNER, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE, TestAccessController.USER_GROUP_CREATE);
    }

    @Test
    public void testShutdown() throws Exception {
        SecureTestUtil.AccessTestAction action = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preShutdown(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV));
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(action, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_GROUP_ADMIN);
        SecureTestUtil.verifyDenied(action, TestAccessController.USER_CREATE, TestAccessController.USER_OWNER, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE, TestAccessController.USER_GROUP_CREATE);
    }

    @Test
    public void testStopMaster() throws Exception {
        SecureTestUtil.AccessTestAction action = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preStopMaster(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV));
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(action, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_GROUP_ADMIN);
        SecureTestUtil.verifyDenied(action, TestAccessController.USER_CREATE, TestAccessController.USER_OWNER, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE, TestAccessController.USER_GROUP_CREATE);
    }

    @Test
    public void testSplitWithSplitRow() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        createTestTable(tableName);
        SecureTestUtil.AccessTestAction action = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preSplitRegion(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), tableName, TestAccessController.TEST_ROW);
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(action, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_OWNER, TestAccessController.USER_GROUP_ADMIN);
        SecureTestUtil.verifyDenied(action, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE, TestAccessController.USER_GROUP_CREATE);
    }

    @Test
    public void testFlush() throws Exception {
        SecureTestUtil.AccessTestAction action = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preFlush(ObserverContextImpl.createAndPrepare(TestAccessController.RCP_ENV), DUMMY);
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(action, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_OWNER, TestAccessController.USER_CREATE, TestAccessController.USER_GROUP_CREATE, TestAccessController.USER_GROUP_ADMIN);
        SecureTestUtil.verifyDenied(action, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE);
    }

    @Test
    public void testCompact() throws Exception {
        SecureTestUtil.AccessTestAction action = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preCompact(ObserverContextImpl.createAndPrepare(TestAccessController.RCP_ENV), null, null, COMPACT_RETAIN_DELETES, null, null);
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(action, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_OWNER, TestAccessController.USER_CREATE, TestAccessController.USER_GROUP_CREATE, TestAccessController.USER_GROUP_ADMIN);
        SecureTestUtil.verifyDenied(action, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE);
    }

    @Test
    public void testRead() throws Exception {
        // get action
        SecureTestUtil.AccessTestAction getAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                Get g = new Get(TestAccessController.TEST_ROW);
                g.addFamily(TestAccessController.TEST_FAMILY);
                try (Connection conn = ConnectionFactory.createConnection(TestAccessController.conf);Table t = conn.getTable(TestAccessController.TEST_TABLE)) {
                    t.get(g);
                }
                return null;
            }
        };
        verifyRead(getAction);
        // action for scanning
        SecureTestUtil.AccessTestAction scanAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                Scan s = new Scan();
                s.addFamily(TestAccessController.TEST_FAMILY);
                try (Connection conn = ConnectionFactory.createConnection(TestAccessController.conf);Table table = conn.getTable(TestAccessController.TEST_TABLE)) {
                    ResultScanner scanner = table.getScanner(s);
                    try {
                        for (Result r = scanner.next(); r != null; r = scanner.next()) {
                            // do nothing
                        }
                    } finally {
                        scanner.close();
                    }
                }
                return null;
            }
        };
        verifyRead(scanAction);
    }

    // test put, delete, increment
    @Test
    public void testWrite() throws Exception {
        // put action
        SecureTestUtil.AccessTestAction putAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                Put p = new Put(TestAccessController.TEST_ROW);
                p.addColumn(TestAccessController.TEST_FAMILY, TestAccessController.TEST_QUALIFIER, Bytes.toBytes(1));
                try (Connection conn = ConnectionFactory.createConnection(TestAccessController.conf);Table t = conn.getTable(TestAccessController.TEST_TABLE)) {
                    t.put(p);
                }
                return null;
            }
        };
        verifyWrite(putAction);
        // delete action
        SecureTestUtil.AccessTestAction deleteAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                Delete d = new Delete(TestAccessController.TEST_ROW);
                d.addFamily(TestAccessController.TEST_FAMILY);
                try (Connection conn = ConnectionFactory.createConnection(TestAccessController.conf);Table t = conn.getTable(TestAccessController.TEST_TABLE)) {
                    t.delete(d);
                }
                return null;
            }
        };
        verifyWrite(deleteAction);
        // increment action
        SecureTestUtil.AccessTestAction incrementAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                Increment inc = new Increment(TestAccessController.TEST_ROW);
                inc.addColumn(TestAccessController.TEST_FAMILY, TestAccessController.TEST_QUALIFIER, 1);
                try (Connection conn = ConnectionFactory.createConnection(TestAccessController.conf);Table t = conn.getTable(TestAccessController.TEST_TABLE)) {
                    t.increment(inc);
                }
                return null;
            }
        };
        verifyWrite(incrementAction);
    }

    @Test
    public void testReadWrite() throws Exception {
        // action for checkAndDelete
        SecureTestUtil.AccessTestAction checkAndDeleteAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                Delete d = new Delete(TestAccessController.TEST_ROW);
                d.addFamily(TestAccessController.TEST_FAMILY);
                try (Connection conn = ConnectionFactory.createConnection(TestAccessController.conf);Table t = conn.getTable(TestAccessController.TEST_TABLE)) {
                    t.checkAndMutate(TestAccessController.TEST_ROW, TestAccessController.TEST_FAMILY).qualifier(TestAccessController.TEST_QUALIFIER).ifEquals(Bytes.toBytes("test_value")).thenDelete(d);
                }
                return null;
            }
        };
        verifyReadWrite(checkAndDeleteAction);
        // action for checkAndPut()
        SecureTestUtil.AccessTestAction checkAndPut = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                Put p = new Put(TestAccessController.TEST_ROW);
                p.addColumn(TestAccessController.TEST_FAMILY, TestAccessController.TEST_QUALIFIER, Bytes.toBytes(1));
                try (Connection conn = ConnectionFactory.createConnection(TestAccessController.conf);Table t = conn.getTable(TestAccessController.TEST_TABLE)) {
                    t.checkAndMutate(TestAccessController.TEST_ROW, TestAccessController.TEST_FAMILY).qualifier(TestAccessController.TEST_QUALIFIER).ifEquals(Bytes.toBytes("test_value")).thenPut(p);
                }
                return null;
            }
        };
        verifyReadWrite(checkAndPut);
    }

    @Test
    public void testBulkLoad() throws Exception {
        try {
            FileSystem fs = TestAccessController.TEST_UTIL.getTestFileSystem();
            final Path dir = TestAccessController.TEST_UTIL.getDataTestDirOnTestFS("testBulkLoad");
            fs.mkdirs(dir);
            // need to make it globally writable
            // so users creating HFiles have write permissions
            fs.setPermission(dir, TestAccessController.FS_PERMISSION_ALL);
            SecureTestUtil.AccessTestAction bulkLoadAction = new SecureTestUtil.AccessTestAction() {
                @Override
                public Object run() throws Exception {
                    int numRows = 3;
                    // Making the assumption that the test table won't split between the range
                    byte[][][] hfileRanges = new byte[][][]{ new byte[][]{ new byte[]{ ((byte) (0)) }, new byte[]{ ((byte) (9)) } } };
                    Path bulkLoadBasePath = new Path(dir, new Path(User.getCurrent().getName()));
                    new TestAccessController.BulkLoadHelper(bulkLoadBasePath).initHFileData(TestAccessController.TEST_FAMILY, TestAccessController.TEST_QUALIFIER, hfileRanges, numRows, TestAccessController.FS_PERMISSION_ALL).bulkLoadHFile(TestAccessController.TEST_TABLE);
                    return null;
                }
            };
            // User performing bulk loads must have privilege to read table metadata
            // (ADMIN or CREATE)
            SecureTestUtil.verifyAllowed(bulkLoadAction, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_OWNER, TestAccessController.USER_CREATE, TestAccessController.USER_GROUP_CREATE);
            SecureTestUtil.verifyDenied(bulkLoadAction, TestAccessController.USER_RW, TestAccessController.USER_NONE, TestAccessController.USER_RO, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE, TestAccessController.USER_GROUP_ADMIN);
        } finally {
            // Reinit after the bulk upload
            TestAccessController.TEST_UTIL.getAdmin().disableTable(TestAccessController.TEST_TABLE);
            TestAccessController.TEST_UTIL.getAdmin().enableTable(TestAccessController.TEST_TABLE);
        }
    }

    private class BulkLoadAccessTestAction implements SecureTestUtil.AccessTestAction {
        private FsPermission filePermission;

        private Path testDataDir;

        public BulkLoadAccessTestAction(FsPermission perm, Path testDataDir) {
            this.filePermission = perm;
            this.testDataDir = testDataDir;
        }

        @Override
        public Object run() throws Exception {
            FileSystem fs = TestAccessController.TEST_UTIL.getTestFileSystem();
            fs.mkdirs(testDataDir);
            fs.setPermission(testDataDir, TestAccessController.FS_PERMISSION_ALL);
            // Making the assumption that the test table won't split between the range
            byte[][][] hfileRanges = new byte[][][]{ new byte[][]{ new byte[]{ ((byte) (0)) }, new byte[]{ ((byte) (9)) } } };
            Path bulkLoadBasePath = new Path(testDataDir, new Path(User.getCurrent().getName()));
            new TestAccessController.BulkLoadHelper(bulkLoadBasePath).initHFileData(TestAccessController.TEST_FAMILY, TestAccessController.TEST_QUALIFIER, hfileRanges, 3, filePermission).bulkLoadHFile(TestAccessController.TEST_TABLE);
            return null;
        }
    }

    @Test
    public void testBulkLoadWithoutWritePermission() throws Exception {
        // Use the USER_CREATE to initialize the source directory.
        Path testDataDir0 = TestAccessController.TEST_UTIL.getDataTestDirOnTestFS("testBulkLoadWithoutWritePermission0");
        Path testDataDir1 = TestAccessController.TEST_UTIL.getDataTestDirOnTestFS("testBulkLoadWithoutWritePermission1");
        SecureTestUtil.AccessTestAction bulkLoadAction1 = new TestAccessController.BulkLoadAccessTestAction(FsPermission.valueOf("-r-xr-xr-x"), testDataDir0);
        SecureTestUtil.AccessTestAction bulkLoadAction2 = new TestAccessController.BulkLoadAccessTestAction(TestAccessController.FS_PERMISSION_ALL, testDataDir1);
        // Test the incorrect case.
        TestAccessController.BulkLoadHelper.setPermission(TestAccessController.TEST_UTIL.getTestFileSystem(), TestAccessController.TEST_UTIL.getTestFileSystem().getWorkingDirectory(), TestAccessController.FS_PERMISSION_ALL);
        try {
            TestAccessController.USER_CREATE.runAs(bulkLoadAction1);
            Assert.fail("Should fail because the hbase user has no write permission on hfiles.");
        } catch (IOException e) {
        }
        // Ensure the correct case.
        TestAccessController.USER_CREATE.runAs(bulkLoadAction2);
    }

    public static class BulkLoadHelper {
        private final FileSystem fs;

        private final Path loadPath;

        private final Configuration conf;

        public BulkLoadHelper(Path loadPath) throws IOException {
            fs = TestAccessController.TEST_UTIL.getTestFileSystem();
            conf = TestAccessController.TEST_UTIL.getConfiguration();
            loadPath = loadPath.makeQualified(fs);
            this.loadPath = loadPath;
        }

        private void createHFile(Path path, byte[] family, byte[] qualifier, byte[] startKey, byte[] endKey, int numRows) throws IOException {
            HFile.Writer writer = null;
            long now = System.currentTimeMillis();
            try {
                HFileContext context = new HFileContextBuilder().build();
                writer = HFile.getWriterFactory(conf, new org.apache.hadoop.hbase.io.hfile.CacheConfig(conf)).withPath(fs, path).withFileContext(context).create();
                // subtract 2 since numRows doesn't include boundary keys
                for (byte[] key : Bytes.iterateOnSplits(startKey, endKey, true, (numRows - 2))) {
                    KeyValue kv = new KeyValue(key, family, qualifier, now, key);
                    writer.append(kv);
                }
            } finally {
                if (writer != null) {
                    writer.close();
                }
            }
        }

        private TestAccessController.BulkLoadHelper initHFileData(byte[] family, byte[] qualifier, byte[][][] hfileRanges, int numRowsPerRange, FsPermission filePermission) throws Exception {
            Path familyDir = new Path(loadPath, Bytes.toString(family));
            fs.mkdirs(familyDir);
            int hfileIdx = 0;
            List<Path> hfiles = new ArrayList<>();
            for (byte[][] range : hfileRanges) {
                byte[] from = range[0];
                byte[] to = range[1];
                Path hfile = new Path(familyDir, ("hfile_" + (hfileIdx++)));
                hfiles.add(hfile);
                createHFile(hfile, family, qualifier, from, to, numRowsPerRange);
            }
            // set global read so RegionServer can move it
            TestAccessController.BulkLoadHelper.setPermission(fs, loadPath, TestAccessController.FS_PERMISSION_ALL);
            // Ensure the file permission as requested.
            for (Path hfile : hfiles) {
                TestAccessController.BulkLoadHelper.setPermission(fs, hfile, filePermission);
            }
            return this;
        }

        private void bulkLoadHFile(TableName tableName) throws Exception {
            try (Connection conn = ConnectionFactory.createConnection(conf);Admin admin = conn.getAdmin();RegionLocator locator = conn.getRegionLocator(tableName);Table table = conn.getTable(tableName)) {
                TestAccessController.TEST_UTIL.waitUntilAllRegionsAssigned(tableName);
                LoadIncrementalHFiles loader = new LoadIncrementalHFiles(conf);
                loader.doBulkLoad(loadPath, admin, table, locator);
            }
        }

        private static void setPermission(FileSystem fs, Path dir, FsPermission perm) throws IOException {
            if (!(fs.getFileStatus(dir).isDirectory())) {
                fs.setPermission(dir, perm);
            } else {
                for (FileStatus el : fs.listStatus(dir)) {
                    fs.setPermission(el.getPath(), perm);
                    TestAccessController.BulkLoadHelper.setPermission(fs, el.getPath(), perm);
                }
            }
        }
    }

    @Test
    public void testAppend() throws Exception {
        SecureTestUtil.AccessTestAction appendAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                byte[] row = TestAccessController.TEST_ROW;
                byte[] qualifier = TestAccessController.TEST_QUALIFIER;
                Put put = new Put(row);
                put.addColumn(TestAccessController.TEST_FAMILY, qualifier, Bytes.toBytes(1));
                Append append = new Append(row);
                append.addColumn(TestAccessController.TEST_FAMILY, qualifier, Bytes.toBytes(2));
                try (Connection conn = ConnectionFactory.createConnection(TestAccessController.conf);Table t = conn.getTable(TestAccessController.TEST_TABLE)) {
                    t.put(put);
                    t.append(append);
                }
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(appendAction, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_OWNER, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_GROUP_WRITE);
        SecureTestUtil.verifyDenied(appendAction, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_GROUP_CREATE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_ADMIN);
    }

    @Test
    public void testGrantRevoke() throws Exception {
        SecureTestUtil.AccessTestAction grantAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                try (Connection conn = ConnectionFactory.createConnection(TestAccessController.conf)) {
                    conn.getAdmin().grant(TestAccessController.USER_RO.getShortName(), new TablePermission(TestAccessController.TEST_TABLE, TestAccessController.TEST_FAMILY, Action.READ), false);
                }
                return null;
            }
        };
        SecureTestUtil.AccessTestAction revokeAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                try (Connection conn = ConnectionFactory.createConnection(TestAccessController.conf)) {
                    conn.getAdmin().revoke(TestAccessController.USER_RO.getShortName(), Permission.newBuilder(TestAccessController.TEST_TABLE).withFamily(TestAccessController.TEST_FAMILY).withActions(READ).build());
                }
                return null;
            }
        };
        SecureTestUtil.AccessTestAction getTablePermissionsAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                try (Connection conn = ConnectionFactory.createConnection(TestAccessController.conf);Table acl = conn.getTable(ACL_TABLE_NAME)) {
                    BlockingRpcChannel service = acl.coprocessorService(TestAccessController.TEST_TABLE.getName());
                    AccessControlService.BlockingInterface protocol = AccessControlService.newBlockingStub(service);
                    AccessControlUtil.getUserPermissions(null, protocol, TestAccessController.TEST_TABLE);
                }
                return null;
            }
        };
        SecureTestUtil.AccessTestAction getGlobalPermissionsAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                try (Connection conn = ConnectionFactory.createConnection(TestAccessController.conf);Table acl = conn.getTable(ACL_TABLE_NAME)) {
                    BlockingRpcChannel service = acl.coprocessorService(EMPTY_START_ROW);
                    AccessControlService.BlockingInterface protocol = AccessControlService.newBlockingStub(service);
                    AccessControlUtil.getUserPermissions(null, protocol);
                }
                return null;
            }
        };
        SecureTestUtil.AccessTestAction preGrantAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preGrant(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), new UserPermission(TestAccessController.USER_RO.getShortName(), new TablePermission(TestAccessController.TEST_TABLE, TestAccessController.TEST_FAMILY, Action.READ)), false);
                return null;
            }
        };
        SecureTestUtil.AccessTestAction preRevokeAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preRevoke(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), new UserPermission(TestAccessController.USER_RO.getShortName(), new TablePermission(TestAccessController.TEST_TABLE, TestAccessController.TEST_FAMILY, Action.READ)));
                return null;
            }
        };
        SecureTestUtil.AccessTestAction grantCPAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                try (Connection conn = ConnectionFactory.createConnection(TestAccessController.conf);Table acl = conn.getTable(ACL_TABLE_NAME)) {
                    BlockingRpcChannel service = acl.coprocessorService(TestAccessController.TEST_TABLE.getName());
                    AccessControlService.BlockingInterface protocol = AccessControlService.newBlockingStub(service);
                    AccessControlUtil.grant(null, protocol, TestAccessController.USER_RO.getShortName(), TestAccessController.TEST_TABLE, TestAccessController.TEST_FAMILY, null, false, READ);
                }
                return null;
            }
        };
        SecureTestUtil.AccessTestAction revokeCPAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                try (Connection conn = ConnectionFactory.createConnection(TestAccessController.conf);Table acl = conn.getTable(ACL_TABLE_NAME)) {
                    BlockingRpcChannel service = acl.coprocessorService(TestAccessController.TEST_TABLE.getName());
                    AccessControlService.BlockingInterface protocol = AccessControlService.newBlockingStub(service);
                    AccessControlUtil.revoke(null, protocol, TestAccessController.USER_RO.getShortName(), TestAccessController.TEST_TABLE, TestAccessController.TEST_FAMILY, null, READ);
                }
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(grantAction, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_OWNER, TestAccessController.USER_GROUP_ADMIN);
        SecureTestUtil.verifyDenied(grantAction, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE, TestAccessController.USER_GROUP_CREATE);
        try {
            SecureTestUtil.verifyAllowed(revokeAction, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_OWNER, TestAccessController.USER_GROUP_ADMIN);
            SecureTestUtil.verifyDenied(revokeAction, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE, TestAccessController.USER_GROUP_CREATE);
            SecureTestUtil.verifyAllowed(getTablePermissionsAction, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_OWNER, TestAccessController.USER_GROUP_ADMIN);
            SecureTestUtil.verifyDenied(getTablePermissionsAction, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE, TestAccessController.USER_GROUP_CREATE);
            SecureTestUtil.verifyAllowed(getGlobalPermissionsAction, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_GROUP_ADMIN);
            SecureTestUtil.verifyDenied(getGlobalPermissionsAction, TestAccessController.USER_CREATE, TestAccessController.USER_OWNER, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE, TestAccessController.USER_GROUP_CREATE);
            SecureTestUtil.verifyAllowed(preGrantAction, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_OWNER, TestAccessController.USER_GROUP_ADMIN);
            SecureTestUtil.verifyDenied(preGrantAction, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE, TestAccessController.USER_GROUP_CREATE);
            SecureTestUtil.verifyAllowed(preRevokeAction, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_OWNER, TestAccessController.USER_GROUP_ADMIN);
            SecureTestUtil.verifyDenied(preRevokeAction, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE, TestAccessController.USER_GROUP_CREATE);
            SecureTestUtil.verifyAllowed(grantCPAction, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_OWNER, TestAccessController.USER_GROUP_ADMIN);
            SecureTestUtil.verifyDenied(grantCPAction, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE, TestAccessController.USER_GROUP_CREATE);
            SecureTestUtil.verifyAllowed(revokeCPAction, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_OWNER, TestAccessController.USER_GROUP_ADMIN);
            SecureTestUtil.verifyDenied(revokeCPAction, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE, TestAccessController.USER_GROUP_CREATE);
        } finally {
            // Cleanup, Grant the revoked permission back to the user
            SecureTestUtil.grantOnTable(TestAccessController.TEST_UTIL, TestAccessController.USER_RO.getShortName(), TestAccessController.TEST_TABLE, TestAccessController.TEST_FAMILY, null, Permission.Action.READ);
        }
    }

    @Test
    public void testPostGrantRevoke() throws Exception {
        final TableName tableName = TableName.valueOf("TempTable");
        final byte[] family1 = Bytes.toBytes("f1");
        final byte[] family2 = Bytes.toBytes("f2");
        final byte[] qualifier = Bytes.toBytes("q");
        // create table
        Admin admin = TestAccessController.TEST_UTIL.getAdmin();
        if (admin.tableExists(tableName)) {
            SecureTestUtil.deleteTable(TestAccessController.TEST_UTIL, tableName);
        }
        HTableDescriptor htd = new HTableDescriptor(tableName);
        htd.addFamily(new HColumnDescriptor(family1));
        htd.addFamily(new HColumnDescriptor(family2));
        SecureTestUtil.createTable(TestAccessController.TEST_UTIL, htd);
        try {
            // create temp users
            User tblUser = User.createUserForTesting(TestAccessController.TEST_UTIL.getConfiguration(), "tbluser", new String[0]);
            User gblUser = User.createUserForTesting(TestAccessController.TEST_UTIL.getConfiguration(), "gbluser", new String[0]);
            // prepare actions:
            SecureTestUtil.AccessTestAction putActionAll = new SecureTestUtil.AccessTestAction() {
                @Override
                public Object run() throws Exception {
                    Put p = new Put(Bytes.toBytes("a"));
                    p.addColumn(family1, qualifier, Bytes.toBytes("v1"));
                    p.addColumn(family2, qualifier, Bytes.toBytes("v2"));
                    try (Connection conn = ConnectionFactory.createConnection(TestAccessController.conf);Table t = conn.getTable(tableName)) {
                        t.put(p);
                    }
                    return null;
                }
            };
            SecureTestUtil.AccessTestAction putAction1 = new SecureTestUtil.AccessTestAction() {
                @Override
                public Object run() throws Exception {
                    Put p = new Put(Bytes.toBytes("a"));
                    p.addColumn(family1, qualifier, Bytes.toBytes("v1"));
                    try (Connection conn = ConnectionFactory.createConnection(TestAccessController.conf);Table t = conn.getTable(tableName)) {
                        t.put(p);
                    }
                    return null;
                }
            };
            SecureTestUtil.AccessTestAction putAction2 = new SecureTestUtil.AccessTestAction() {
                @Override
                public Object run() throws Exception {
                    Put p = new Put(Bytes.toBytes("a"));
                    p.addColumn(family2, qualifier, Bytes.toBytes("v2"));
                    try (Connection conn = ConnectionFactory.createConnection(TestAccessController.conf);Table t = conn.getTable(tableName)) {
                        t.put(p);
                    }
                    return null;
                }
            };
            SecureTestUtil.AccessTestAction getActionAll = new SecureTestUtil.AccessTestAction() {
                @Override
                public Object run() throws Exception {
                    Get g = new Get(TestAccessController.TEST_ROW);
                    g.addFamily(family1);
                    g.addFamily(family2);
                    try (Connection conn = ConnectionFactory.createConnection(TestAccessController.conf);Table t = conn.getTable(tableName)) {
                        t.get(g);
                    }
                    return null;
                }
            };
            SecureTestUtil.AccessTestAction getAction1 = new SecureTestUtil.AccessTestAction() {
                @Override
                public Object run() throws Exception {
                    Get g = new Get(TestAccessController.TEST_ROW);
                    g.addFamily(family1);
                    try (Connection conn = ConnectionFactory.createConnection(TestAccessController.conf);Table t = conn.getTable(tableName)) {
                        t.get(g);
                    }
                    return null;
                }
            };
            SecureTestUtil.AccessTestAction getAction2 = new SecureTestUtil.AccessTestAction() {
                @Override
                public Object run() throws Exception {
                    Get g = new Get(TestAccessController.TEST_ROW);
                    g.addFamily(family2);
                    try (Connection conn = ConnectionFactory.createConnection(TestAccessController.conf);Table t = conn.getTable(tableName)) {
                        t.get(g);
                    }
                    return null;
                }
            };
            SecureTestUtil.AccessTestAction deleteActionAll = new SecureTestUtil.AccessTestAction() {
                @Override
                public Object run() throws Exception {
                    Delete d = new Delete(TestAccessController.TEST_ROW);
                    d.addFamily(family1);
                    d.addFamily(family2);
                    try (Connection conn = ConnectionFactory.createConnection(TestAccessController.conf);Table t = conn.getTable(tableName)) {
                        t.delete(d);
                    }
                    return null;
                }
            };
            SecureTestUtil.AccessTestAction deleteAction1 = new SecureTestUtil.AccessTestAction() {
                @Override
                public Object run() throws Exception {
                    Delete d = new Delete(TestAccessController.TEST_ROW);
                    d.addFamily(family1);
                    try (Connection conn = ConnectionFactory.createConnection(TestAccessController.conf);Table t = conn.getTable(tableName)) {
                        t.delete(d);
                    }
                    return null;
                }
            };
            SecureTestUtil.AccessTestAction deleteAction2 = new SecureTestUtil.AccessTestAction() {
                @Override
                public Object run() throws Exception {
                    Delete d = new Delete(TestAccessController.TEST_ROW);
                    d.addFamily(family2);
                    try (Connection conn = ConnectionFactory.createConnection(TestAccessController.conf);Table t = conn.getTable(tableName)) {
                        t.delete(d);
                    }
                    return null;
                }
            };
            // initial check:
            SecureTestUtil.verifyDenied(tblUser, getActionAll, getAction1, getAction2);
            SecureTestUtil.verifyDenied(tblUser, putActionAll, putAction1, putAction2);
            SecureTestUtil.verifyDenied(tblUser, deleteActionAll, deleteAction1, deleteAction2);
            SecureTestUtil.verifyDenied(gblUser, getActionAll, getAction1, getAction2);
            SecureTestUtil.verifyDenied(gblUser, putActionAll, putAction1, putAction2);
            SecureTestUtil.verifyDenied(gblUser, deleteActionAll, deleteAction1, deleteAction2);
            // grant table read permission
            SecureTestUtil.grantGlobal(TestAccessController.TEST_UTIL, gblUser.getShortName(), Permission.Action.READ);
            SecureTestUtil.grantOnTable(TestAccessController.TEST_UTIL, tblUser.getShortName(), tableName, null, null, Permission.Action.READ);
            // check
            SecureTestUtil.verifyAllowed(tblUser, getActionAll, getAction1, getAction2);
            SecureTestUtil.verifyDenied(tblUser, putActionAll, putAction1, putAction2);
            SecureTestUtil.verifyDenied(tblUser, deleteActionAll, deleteAction1, deleteAction2);
            SecureTestUtil.verifyAllowed(gblUser, getActionAll, getAction1, getAction2);
            SecureTestUtil.verifyDenied(gblUser, putActionAll, putAction1, putAction2);
            SecureTestUtil.verifyDenied(gblUser, deleteActionAll, deleteAction1, deleteAction2);
            // grant table write permission while revoking read permissions
            SecureTestUtil.grantGlobal(TestAccessController.TEST_UTIL, gblUser.getShortName(), WRITE);
            SecureTestUtil.grantOnTable(TestAccessController.TEST_UTIL, tblUser.getShortName(), tableName, null, null, WRITE);
            SecureTestUtil.verifyDenied(tblUser, getActionAll, getAction1, getAction2);
            SecureTestUtil.verifyAllowed(tblUser, putActionAll, putAction1, putAction2);
            SecureTestUtil.verifyAllowed(tblUser, deleteActionAll, deleteAction1, deleteAction2);
            SecureTestUtil.verifyDenied(gblUser, getActionAll, getAction1, getAction2);
            SecureTestUtil.verifyAllowed(gblUser, putActionAll, putAction1, putAction2);
            SecureTestUtil.verifyAllowed(gblUser, deleteActionAll, deleteAction1, deleteAction2);
            // revoke table permissions
            SecureTestUtil.revokeGlobal(TestAccessController.TEST_UTIL, gblUser.getShortName());
            SecureTestUtil.revokeFromTable(TestAccessController.TEST_UTIL, tblUser.getShortName(), tableName, null, null);
            SecureTestUtil.verifyDenied(tblUser, getActionAll, getAction1, getAction2);
            SecureTestUtil.verifyDenied(tblUser, putActionAll, putAction1, putAction2);
            SecureTestUtil.verifyDenied(tblUser, deleteActionAll, deleteAction1, deleteAction2);
            SecureTestUtil.verifyDenied(gblUser, getActionAll, getAction1, getAction2);
            SecureTestUtil.verifyDenied(gblUser, putActionAll, putAction1, putAction2);
            SecureTestUtil.verifyDenied(gblUser, deleteActionAll, deleteAction1, deleteAction2);
            // grant column family read permission
            SecureTestUtil.grantGlobal(TestAccessController.TEST_UTIL, gblUser.getShortName(), Permission.Action.READ);
            SecureTestUtil.grantOnTable(TestAccessController.TEST_UTIL, tblUser.getShortName(), tableName, family1, null, Permission.Action.READ);
            // Access should be denied for family2
            SecureTestUtil.verifyAllowed(tblUser, getActionAll, getAction1);
            SecureTestUtil.verifyDenied(tblUser, getAction2);
            SecureTestUtil.verifyDenied(tblUser, putActionAll, putAction1, putAction2);
            SecureTestUtil.verifyDenied(tblUser, deleteActionAll, deleteAction1, deleteAction2);
            SecureTestUtil.verifyAllowed(gblUser, getActionAll, getAction1, getAction2);
            SecureTestUtil.verifyDenied(gblUser, putActionAll, putAction1, putAction2);
            SecureTestUtil.verifyDenied(gblUser, deleteActionAll, deleteAction1, deleteAction2);
            // grant column family write permission
            SecureTestUtil.grantGlobal(TestAccessController.TEST_UTIL, gblUser.getShortName(), WRITE);
            SecureTestUtil.grantOnTable(TestAccessController.TEST_UTIL, tblUser.getShortName(), tableName, family2, null, WRITE);
            // READ from family1, WRITE to family2 are allowed
            SecureTestUtil.verifyAllowed(tblUser, getActionAll, getAction1);
            SecureTestUtil.verifyAllowed(tblUser, putAction2, deleteAction2);
            SecureTestUtil.verifyDenied(tblUser, getAction2);
            SecureTestUtil.verifyDenied(tblUser, putActionAll, putAction1);
            SecureTestUtil.verifyDenied(tblUser, deleteActionAll, deleteAction1);
            SecureTestUtil.verifyDenied(gblUser, getActionAll, getAction1, getAction2);
            SecureTestUtil.verifyAllowed(gblUser, putActionAll, putAction1, putAction2);
            SecureTestUtil.verifyAllowed(gblUser, deleteActionAll, deleteAction1, deleteAction2);
            // revoke column family permission
            SecureTestUtil.revokeGlobal(TestAccessController.TEST_UTIL, gblUser.getShortName());
            SecureTestUtil.revokeFromTable(TestAccessController.TEST_UTIL, tblUser.getShortName(), tableName, family2, null);
            // Revoke on family2 should not have impact on family1 permissions
            SecureTestUtil.verifyAllowed(tblUser, getActionAll, getAction1);
            SecureTestUtil.verifyDenied(tblUser, getAction2);
            SecureTestUtil.verifyDenied(tblUser, putActionAll, putAction1, putAction2);
            SecureTestUtil.verifyDenied(tblUser, deleteActionAll, deleteAction1, deleteAction2);
            // Should not have access as global permissions are completely revoked
            SecureTestUtil.verifyDenied(gblUser, getActionAll, getAction1, getAction2);
            SecureTestUtil.verifyDenied(gblUser, putActionAll, putAction1, putAction2);
            SecureTestUtil.verifyDenied(gblUser, deleteActionAll, deleteAction1, deleteAction2);
        } finally {
            // delete table
            SecureTestUtil.deleteTable(TestAccessController.TEST_UTIL, tableName);
        }
    }

    @Test
    public void testPostGrantRevokeAtQualifierLevel() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final byte[] family1 = Bytes.toBytes("f1");
        final byte[] family2 = Bytes.toBytes("f2");
        final byte[] qualifier = Bytes.toBytes("q");
        // create table
        Admin admin = TestAccessController.TEST_UTIL.getAdmin();
        if (admin.tableExists(tableName)) {
            SecureTestUtil.deleteTable(TestAccessController.TEST_UTIL, tableName);
        }
        HTableDescriptor htd = new HTableDescriptor(tableName);
        htd.addFamily(new HColumnDescriptor(family1));
        htd.addFamily(new HColumnDescriptor(family2));
        SecureTestUtil.createTable(TestAccessController.TEST_UTIL, htd);
        try {
            // create temp users
            User user = User.createUserForTesting(TestAccessController.TEST_UTIL.getConfiguration(), "user", new String[0]);
            SecureTestUtil.AccessTestAction getQualifierAction = new SecureTestUtil.AccessTestAction() {
                @Override
                public Object run() throws Exception {
                    Get g = new Get(TestAccessController.TEST_ROW);
                    g.addColumn(family1, qualifier);
                    try (Connection conn = ConnectionFactory.createConnection(TestAccessController.conf);Table t = conn.getTable(tableName)) {
                        t.get(g);
                    }
                    return null;
                }
            };
            SecureTestUtil.AccessTestAction putQualifierAction = new SecureTestUtil.AccessTestAction() {
                @Override
                public Object run() throws Exception {
                    Put p = new Put(TestAccessController.TEST_ROW);
                    p.addColumn(family1, qualifier, Bytes.toBytes("v1"));
                    try (Connection conn = ConnectionFactory.createConnection(TestAccessController.conf);Table t = conn.getTable(tableName)) {
                        t.put(p);
                    }
                    return null;
                }
            };
            SecureTestUtil.AccessTestAction deleteQualifierAction = new SecureTestUtil.AccessTestAction() {
                @Override
                public Object run() throws Exception {
                    Delete d = new Delete(TestAccessController.TEST_ROW);
                    d.addColumn(family1, qualifier);
                    // d.deleteFamily(family1);
                    try (Connection conn = ConnectionFactory.createConnection(TestAccessController.conf);Table t = conn.getTable(tableName)) {
                        t.delete(d);
                    }
                    return null;
                }
            };
            SecureTestUtil.revokeFromTable(TestAccessController.TEST_UTIL, user.getShortName(), tableName, family1, null);
            SecureTestUtil.verifyDenied(user, getQualifierAction);
            SecureTestUtil.verifyDenied(user, putQualifierAction);
            SecureTestUtil.verifyDenied(user, deleteQualifierAction);
            SecureTestUtil.grantOnTable(TestAccessController.TEST_UTIL, user.getShortName(), tableName, family1, qualifier, Permission.Action.READ);
            SecureTestUtil.verifyAllowed(user, getQualifierAction);
            SecureTestUtil.verifyDenied(user, putQualifierAction);
            SecureTestUtil.verifyDenied(user, deleteQualifierAction);
            // only grant write permission
            // TODO: comment this portion after HBASE-3583
            SecureTestUtil.grantOnTable(TestAccessController.TEST_UTIL, user.getShortName(), tableName, family1, qualifier, WRITE);
            SecureTestUtil.verifyDenied(user, getQualifierAction);
            SecureTestUtil.verifyAllowed(user, putQualifierAction);
            SecureTestUtil.verifyAllowed(user, deleteQualifierAction);
            // grant both read and write permission
            SecureTestUtil.grantOnTable(TestAccessController.TEST_UTIL, user.getShortName(), tableName, family1, qualifier, Permission.Action.READ, WRITE);
            SecureTestUtil.verifyAllowed(user, getQualifierAction);
            SecureTestUtil.verifyAllowed(user, putQualifierAction);
            SecureTestUtil.verifyAllowed(user, deleteQualifierAction);
            // revoke family level permission won't impact column level
            SecureTestUtil.revokeFromTable(TestAccessController.TEST_UTIL, user.getShortName(), tableName, family1, qualifier);
            SecureTestUtil.verifyDenied(user, getQualifierAction);
            SecureTestUtil.verifyDenied(user, putQualifierAction);
            SecureTestUtil.verifyDenied(user, deleteQualifierAction);
        } finally {
            // delete table
            SecureTestUtil.deleteTable(TestAccessController.TEST_UTIL, tableName);
        }
    }

    @Test
    public void testPermissionList() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final byte[] family1 = Bytes.toBytes("f1");
        final byte[] family2 = Bytes.toBytes("f2");
        final byte[] qualifier = Bytes.toBytes("q");
        // create table
        Admin admin = TestAccessController.TEST_UTIL.getAdmin();
        if (admin.tableExists(tableName)) {
            SecureTestUtil.deleteTable(TestAccessController.TEST_UTIL, tableName);
        }
        HTableDescriptor htd = new HTableDescriptor(tableName);
        htd.addFamily(new HColumnDescriptor(family1));
        htd.addFamily(new HColumnDescriptor(family2));
        htd.setOwner(TestAccessController.USER_OWNER);
        SecureTestUtil.createTable(TestAccessController.TEST_UTIL, htd);
        try {
            List<UserPermission> perms;
            Table acl = TestAccessController.systemUserConnection.getTable(ACL_TABLE_NAME);
            try {
                BlockingRpcChannel service = acl.coprocessorService(tableName.getName());
                AccessControlService.BlockingInterface protocol = AccessControlService.newBlockingStub(service);
                perms = AccessControlUtil.getUserPermissions(null, protocol, tableName);
            } finally {
                acl.close();
            }
            UserPermission ownerperm = new UserPermission(TestAccessController.USER_OWNER.getName(), tableName, Action.values());
            Assert.assertTrue("Owner should have all permissions on table", hasFoundUserPermission(ownerperm, perms));
            User user = User.createUserForTesting(TestAccessController.TEST_UTIL.getConfiguration(), "user", new String[0]);
            String userName = user.getShortName();
            UserPermission up = new UserPermission(userName, tableName, family1, qualifier, Action.READ);
            Assert.assertFalse(("User should not be granted permission: " + (up.toString())), hasFoundUserPermission(up, perms));
            // grant read permission
            SecureTestUtil.grantOnTable(TestAccessController.TEST_UTIL, user.getShortName(), tableName, family1, qualifier, Permission.Action.READ);
            acl = TestAccessController.systemUserConnection.getTable(ACL_TABLE_NAME);
            try {
                BlockingRpcChannel service = acl.coprocessorService(tableName.getName());
                AccessControlService.BlockingInterface protocol = AccessControlService.newBlockingStub(service);
                perms = AccessControlUtil.getUserPermissions(null, protocol, tableName);
            } finally {
                acl.close();
            }
            UserPermission upToVerify = new UserPermission(userName, tableName, family1, qualifier, Action.READ);
            Assert.assertTrue(("User should be granted permission: " + (upToVerify.toString())), hasFoundUserPermission(upToVerify, perms));
            upToVerify = new UserPermission(userName, tableName, family1, qualifier, Action.WRITE);
            Assert.assertFalse(("User should not be granted permission: " + (upToVerify.toString())), hasFoundUserPermission(upToVerify, perms));
            // grant read+write
            SecureTestUtil.grantOnTable(TestAccessController.TEST_UTIL, user.getShortName(), tableName, family1, qualifier, WRITE, Permission.Action.READ);
            acl = TestAccessController.systemUserConnection.getTable(ACL_TABLE_NAME);
            try {
                BlockingRpcChannel service = acl.coprocessorService(tableName.getName());
                AccessControlService.BlockingInterface protocol = AccessControlService.newBlockingStub(service);
                perms = AccessControlUtil.getUserPermissions(null, protocol, tableName);
            } finally {
                acl.close();
            }
            upToVerify = new UserPermission(userName, tableName, family1, qualifier, Action.WRITE, Action.READ);
            Assert.assertTrue(("User should be granted permission: " + (upToVerify.toString())), hasFoundUserPermission(upToVerify, perms));
            // revoke
            SecureTestUtil.revokeFromTable(TestAccessController.TEST_UTIL, user.getShortName(), tableName, family1, qualifier, WRITE, Permission.Action.READ);
            acl = TestAccessController.systemUserConnection.getTable(ACL_TABLE_NAME);
            try {
                BlockingRpcChannel service = acl.coprocessorService(tableName.getName());
                AccessControlService.BlockingInterface protocol = AccessControlService.newBlockingStub(service);
                perms = AccessControlUtil.getUserPermissions(null, protocol, tableName);
            } finally {
                acl.close();
            }
            Assert.assertFalse(("User should not be granted permission: " + (upToVerify.toString())), hasFoundUserPermission(upToVerify, perms));
            // disable table before modification
            admin.disableTable(tableName);
            User newOwner = User.createUserForTesting(TestAccessController.conf, "new_owner", new String[]{  });
            htd.setOwner(newOwner);
            admin.modifyTable(tableName, htd);
            acl = TestAccessController.systemUserConnection.getTable(ACL_TABLE_NAME);
            try {
                BlockingRpcChannel service = acl.coprocessorService(tableName.getName());
                AccessControlService.BlockingInterface protocol = AccessControlService.newBlockingStub(service);
                perms = AccessControlUtil.getUserPermissions(null, protocol, tableName);
            } finally {
                acl.close();
            }
            UserPermission newOwnerperm = new UserPermission(newOwner.getName(), tableName, Action.values());
            Assert.assertTrue("New owner should have all permissions on table", hasFoundUserPermission(newOwnerperm, perms));
        } finally {
            // delete table
            SecureTestUtil.deleteTable(TestAccessController.TEST_UTIL, tableName);
        }
    }

    @Test
    public void testGlobalPermissionList() throws Exception {
        List<UserPermission> perms;
        Table acl = TestAccessController.systemUserConnection.getTable(ACL_TABLE_NAME);
        try {
            BlockingRpcChannel service = acl.coprocessorService(EMPTY_START_ROW);
            AccessControlService.BlockingInterface protocol = AccessControlService.newBlockingStub(service);
            perms = AccessControlUtil.getUserPermissions(null, protocol);
        } finally {
            acl.close();
        }
        Collection<String> superUsers = Superusers.getSuperUsers();
        List<UserPermission> adminPerms = new ArrayList<>(((superUsers.size()) + 1));
        adminPerms.add(new UserPermission(TestAccessController.USER_ADMIN.getShortName(), Bytes.toBytes("ACRW")));
        for (String user : superUsers) {
            // Global permission
            adminPerms.add(new UserPermission(user, Action.values()));
        }
        Assert.assertTrue(("Only super users, global users and user admin has permission on table hbase:acl " + "per setup"), (((perms.size()) == (5 + (superUsers.size()))) && (hasFoundUserPermission(adminPerms, perms))));
    }

    @Test
    public void testCheckPermissions() throws Exception {
        // --------------------------------------
        // test global permissions
        SecureTestUtil.AccessTestAction globalAdmin = new SecureTestUtil.AccessTestAction() {
            @Override
            public Void run() throws Exception {
                SecureTestUtil.checkGlobalPerms(TestAccessController.TEST_UTIL, ADMIN);
                return null;
            }
        };
        // verify that only superuser can admin
        verifyGlobal(globalAdmin);
        // --------------------------------------
        // test multiple permissions
        SecureTestUtil.AccessTestAction globalReadWrite = new SecureTestUtil.AccessTestAction() {
            @Override
            public Void run() throws Exception {
                SecureTestUtil.checkGlobalPerms(TestAccessController.TEST_UTIL, Permission.Action.READ, WRITE);
                return null;
            }
        };
        verifyGlobal(globalReadWrite);
        // --------------------------------------
        // table/column/qualifier level permissions
        final byte[] TEST_Q1 = Bytes.toBytes("q1");
        final byte[] TEST_Q2 = Bytes.toBytes("q2");
        User userTable = User.createUserForTesting(TestAccessController.conf, "user_check_perms_table", new String[0]);
        User userColumn = User.createUserForTesting(TestAccessController.conf, "user_check_perms_family", new String[0]);
        User userQualifier = User.createUserForTesting(TestAccessController.conf, "user_check_perms_q", new String[0]);
        SecureTestUtil.grantOnTable(TestAccessController.TEST_UTIL, userTable.getShortName(), TestAccessController.TEST_TABLE, null, null, Permission.Action.READ);
        SecureTestUtil.grantOnTable(TestAccessController.TEST_UTIL, userColumn.getShortName(), TestAccessController.TEST_TABLE, TestAccessController.TEST_FAMILY, null, Permission.Action.READ);
        SecureTestUtil.grantOnTable(TestAccessController.TEST_UTIL, userQualifier.getShortName(), TestAccessController.TEST_TABLE, TestAccessController.TEST_FAMILY, TEST_Q1, Permission.Action.READ);
        try {
            SecureTestUtil.AccessTestAction tableRead = new SecureTestUtil.AccessTestAction() {
                @Override
                public Void run() throws Exception {
                    SecureTestUtil.checkTablePerms(TestAccessController.TEST_UTIL, TestAccessController.TEST_TABLE, null, null, Permission.Action.READ);
                    return null;
                }
            };
            SecureTestUtil.AccessTestAction columnRead = new SecureTestUtil.AccessTestAction() {
                @Override
                public Void run() throws Exception {
                    SecureTestUtil.checkTablePerms(TestAccessController.TEST_UTIL, TestAccessController.TEST_TABLE, TestAccessController.TEST_FAMILY, null, Permission.Action.READ);
                    return null;
                }
            };
            SecureTestUtil.AccessTestAction qualifierRead = new SecureTestUtil.AccessTestAction() {
                @Override
                public Void run() throws Exception {
                    SecureTestUtil.checkTablePerms(TestAccessController.TEST_UTIL, TestAccessController.TEST_TABLE, TestAccessController.TEST_FAMILY, TEST_Q1, Permission.Action.READ);
                    return null;
                }
            };
            SecureTestUtil.AccessTestAction multiQualifierRead = new SecureTestUtil.AccessTestAction() {
                @Override
                public Void run() throws Exception {
                    SecureTestUtil.checkTablePerms(TestAccessController.TEST_UTIL, TestAccessController.TEST_TABLE, new Permission[]{ new TablePermission(TestAccessController.TEST_TABLE, TestAccessController.TEST_FAMILY, TEST_Q1, Action.READ), new TablePermission(TestAccessController.TEST_TABLE, TestAccessController.TEST_FAMILY, TEST_Q2, Action.READ) });
                    return null;
                }
            };
            SecureTestUtil.AccessTestAction globalAndTableRead = new SecureTestUtil.AccessTestAction() {
                @Override
                public Void run() throws Exception {
                    SecureTestUtil.checkTablePerms(TestAccessController.TEST_UTIL, TestAccessController.TEST_TABLE, new Permission[]{ new Permission(Action.READ), new TablePermission(TestAccessController.TEST_TABLE, null, ((byte[]) (null)), Action.READ) });
                    return null;
                }
            };
            SecureTestUtil.AccessTestAction noCheck = new SecureTestUtil.AccessTestAction() {
                @Override
                public Void run() throws Exception {
                    SecureTestUtil.checkTablePerms(TestAccessController.TEST_UTIL, TestAccessController.TEST_TABLE, new Permission[0]);
                    return null;
                }
            };
            SecureTestUtil.verifyAllowed(tableRead, TestAccessController.SUPERUSER, userTable);
            SecureTestUtil.verifyDenied(tableRead, userColumn, userQualifier);
            SecureTestUtil.verifyAllowed(columnRead, TestAccessController.SUPERUSER, userTable, userColumn);
            SecureTestUtil.verifyDenied(columnRead, userQualifier);
            SecureTestUtil.verifyAllowed(qualifierRead, TestAccessController.SUPERUSER, userTable, userColumn, userQualifier);
            SecureTestUtil.verifyAllowed(multiQualifierRead, TestAccessController.SUPERUSER, userTable, userColumn);
            SecureTestUtil.verifyDenied(multiQualifierRead, userQualifier);
            SecureTestUtil.verifyAllowed(globalAndTableRead, TestAccessController.SUPERUSER);
            SecureTestUtil.verifyDenied(globalAndTableRead, userTable, userColumn, userQualifier);
            SecureTestUtil.verifyAllowed(noCheck, TestAccessController.SUPERUSER, userTable, userColumn, userQualifier);
            // --------------------------------------
            // test family level multiple permissions
            SecureTestUtil.AccessTestAction familyReadWrite = new SecureTestUtil.AccessTestAction() {
                @Override
                public Void run() throws Exception {
                    SecureTestUtil.checkTablePerms(TestAccessController.TEST_UTIL, TestAccessController.TEST_TABLE, TestAccessController.TEST_FAMILY, null, Permission.Action.READ, WRITE);
                    return null;
                }
            };
            SecureTestUtil.verifyAllowed(familyReadWrite, TestAccessController.SUPERUSER, TestAccessController.USER_OWNER, TestAccessController.USER_CREATE, TestAccessController.USER_RW);
            SecureTestUtil.verifyDenied(familyReadWrite, TestAccessController.USER_NONE, TestAccessController.USER_RO);
            // --------------------------------------
            // check for wrong table region
            CheckPermissionsRequest checkRequest = CheckPermissionsRequest.newBuilder().addPermission(AccessControlProtos.Permission.newBuilder().setType(AccessControlProtos.Permission.Type.Table).setTablePermission(TablePermission.newBuilder().setTableName(ProtobufUtil.toProtoTableName(TestAccessController.TEST_TABLE)).addAction(CREATE))).build();
            Table acl = TestAccessController.systemUserConnection.getTable(ACL_TABLE_NAME);
            try {
                BlockingRpcChannel channel = acl.coprocessorService(new byte[0]);
                AccessControlService.BlockingInterface protocol = AccessControlService.newBlockingStub(channel);
                try {
                    // but ask for TablePermissions for TEST_TABLE
                    protocol.checkPermissions(null, checkRequest);
                    Assert.fail("this should have thrown CoprocessorException");
                } catch (ServiceException ex) {
                    // expected
                }
            } finally {
                acl.close();
            }
        } finally {
            SecureTestUtil.revokeFromTable(TestAccessController.TEST_UTIL, userTable.getShortName(), TestAccessController.TEST_TABLE, null, null, Permission.Action.READ);
            SecureTestUtil.revokeFromTable(TestAccessController.TEST_UTIL, userColumn.getShortName(), TestAccessController.TEST_TABLE, TestAccessController.TEST_FAMILY, null, Permission.Action.READ);
            SecureTestUtil.revokeFromTable(TestAccessController.TEST_UTIL, userQualifier.getShortName(), TestAccessController.TEST_TABLE, TestAccessController.TEST_FAMILY, TEST_Q1, Permission.Action.READ);
        }
    }

    @Test
    public void testStopRegionServer() throws Exception {
        SecureTestUtil.AccessTestAction action = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preStopRegionServer(ObserverContextImpl.createAndPrepare(TestAccessController.RSCP_ENV));
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(action, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_GROUP_ADMIN);
        SecureTestUtil.verifyDenied(action, TestAccessController.USER_CREATE, TestAccessController.USER_OWNER, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE, TestAccessController.USER_GROUP_CREATE);
    }

    @Test
    public void testRollWALWriterRequest() throws Exception {
        SecureTestUtil.AccessTestAction action = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preRollWALWriterRequest(ObserverContextImpl.createAndPrepare(TestAccessController.RSCP_ENV));
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(action, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_GROUP_ADMIN);
        SecureTestUtil.verifyDenied(action, TestAccessController.USER_CREATE, TestAccessController.USER_OWNER, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE, TestAccessController.USER_GROUP_CREATE);
    }

    @Test
    public void testOpenRegion() throws Exception {
        SecureTestUtil.AccessTestAction action = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preOpen(ObserverContextImpl.createAndPrepare(TestAccessController.RCP_ENV));
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(action, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_GROUP_ADMIN);
        SecureTestUtil.verifyDenied(action, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_OWNER, TestAccessController.USER_GROUP_CREATE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE);
    }

    @Test
    public void testCloseRegion() throws Exception {
        SecureTestUtil.AccessTestAction action = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preClose(ObserverContextImpl.createAndPrepare(TestAccessController.RCP_ENV), false);
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(action, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_GROUP_ADMIN);
        SecureTestUtil.verifyDenied(action, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_OWNER, TestAccessController.USER_GROUP_CREATE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE);
    }

    @Test
    public void testSnapshot() throws Exception {
        Admin admin = TestAccessController.TEST_UTIL.getAdmin();
        final HTableDescriptor htd = admin.getTableDescriptor(TestAccessController.TEST_TABLE);
        final SnapshotDescription snapshot = new SnapshotDescription(((TestAccessController.TEST_TABLE.getNameAsString()) + "-snapshot"), TestAccessController.TEST_TABLE);
        SecureTestUtil.AccessTestAction snapshotAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preSnapshot(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), snapshot, htd);
                return null;
            }
        };
        SecureTestUtil.AccessTestAction deleteAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preDeleteSnapshot(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), snapshot);
                return null;
            }
        };
        SecureTestUtil.AccessTestAction restoreAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preRestoreSnapshot(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), snapshot, htd);
                return null;
            }
        };
        SecureTestUtil.AccessTestAction cloneAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preCloneSnapshot(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), snapshot, null);
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(snapshotAction, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_OWNER, TestAccessController.USER_GROUP_ADMIN);
        SecureTestUtil.verifyDenied(snapshotAction, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE, TestAccessController.USER_GROUP_CREATE);
        SecureTestUtil.verifyAllowed(cloneAction, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_GROUP_ADMIN);
        SecureTestUtil.verifyDenied(deleteAction, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_OWNER, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE, TestAccessController.USER_GROUP_CREATE);
        SecureTestUtil.verifyAllowed(restoreAction, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_GROUP_ADMIN);
        SecureTestUtil.verifyDenied(restoreAction, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_OWNER, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE, TestAccessController.USER_GROUP_CREATE);
        SecureTestUtil.verifyAllowed(deleteAction, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_GROUP_ADMIN);
        SecureTestUtil.verifyDenied(cloneAction, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_OWNER, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE, TestAccessController.USER_GROUP_CREATE);
    }

    @Test
    public void testSnapshotWithOwner() throws Exception {
        Admin admin = TestAccessController.TEST_UTIL.getAdmin();
        final HTableDescriptor htd = admin.getTableDescriptor(TestAccessController.TEST_TABLE);
        final SnapshotDescription snapshot = new SnapshotDescription(((TestAccessController.TEST_TABLE.getNameAsString()) + "-snapshot"), TestAccessController.TEST_TABLE, null, TestAccessController.USER_OWNER.getName());
        SecureTestUtil.AccessTestAction snapshotAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preSnapshot(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), snapshot, htd);
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(snapshotAction, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_OWNER, TestAccessController.USER_GROUP_ADMIN);
        SecureTestUtil.verifyDenied(snapshotAction, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE, TestAccessController.USER_GROUP_CREATE);
        SecureTestUtil.AccessTestAction deleteAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preDeleteSnapshot(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), snapshot);
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(deleteAction, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_OWNER, TestAccessController.USER_GROUP_ADMIN);
        SecureTestUtil.verifyDenied(deleteAction, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE, TestAccessController.USER_GROUP_CREATE);
        SecureTestUtil.AccessTestAction restoreAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preRestoreSnapshot(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), snapshot, htd);
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(restoreAction, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_OWNER, TestAccessController.USER_GROUP_ADMIN);
        SecureTestUtil.verifyDenied(restoreAction, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE, TestAccessController.USER_GROUP_CREATE);
        SecureTestUtil.AccessTestAction cloneAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preCloneSnapshot(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), snapshot, htd);
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(cloneAction, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_GROUP_ADMIN, TestAccessController.USER_OWNER);
        SecureTestUtil.verifyDenied(cloneAction, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE, TestAccessController.USER_GROUP_CREATE);
    }

    @Test
    public void testGlobalAuthorizationForNewRegisteredRS() throws Exception {
        TestAccessController.LOG.debug("Test for global authorization for a new registered RegionServer.");
        MiniHBaseCluster hbaseCluster = TestAccessController.TEST_UTIL.getHBaseCluster();
        final Admin admin = TestAccessController.TEST_UTIL.getAdmin();
        HTableDescriptor htd = new HTableDescriptor(TestAccessController.TEST_TABLE2);
        htd.addFamily(new HColumnDescriptor(TestAccessController.TEST_FAMILY));
        SecureTestUtil.createTable(TestAccessController.TEST_UTIL, htd);
        // Starting a new RegionServer.
        JVMClusterUtil.RegionServerThread newRsThread = hbaseCluster.startRegionServer();
        final HRegionServer newRs = newRsThread.getRegionServer();
        // Move region to the new RegionServer.
        List<HRegionLocation> regions;
        try (RegionLocator locator = TestAccessController.systemUserConnection.getRegionLocator(TestAccessController.TEST_TABLE2)) {
            regions = locator.getAllRegionLocations();
        }
        HRegionLocation location = regions.get(0);
        final HRegionInfo hri = location.getRegionInfo();
        final ServerName server = location.getServerName();
        try (Table table = TestAccessController.systemUserConnection.getTable(TestAccessController.TEST_TABLE2)) {
            SecureTestUtil.AccessTestAction moveAction = new SecureTestUtil.AccessTestAction() {
                @Override
                public Object run() throws Exception {
                    admin.move(hri.getEncodedNameAsBytes(), Bytes.toBytes(newRs.getServerName().getServerName()));
                    return null;
                }
            };
            TestAccessController.SUPERUSER.runAs(moveAction);
            final int RETRIES_LIMIT = 10;
            int retries = 0;
            while (((newRs.getRegions(TestAccessController.TEST_TABLE2).size()) < 1) && (retries < RETRIES_LIMIT)) {
                TestAccessController.LOG.debug((("Waiting for region to be opened. Already retried " + retries) + " times."));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                retries++;
                if (retries == (RETRIES_LIMIT - 1)) {
                    Assert.fail("Retry exhaust for waiting region to be opened.");
                }
            } 
            // Verify write permission for user "admin2" who has the global
            // permissions.
            SecureTestUtil.AccessTestAction putAction = new SecureTestUtil.AccessTestAction() {
                @Override
                public Object run() throws Exception {
                    Put put = new Put(Bytes.toBytes("test"));
                    put.addColumn(TestAccessController.TEST_FAMILY, Bytes.toBytes("qual"), Bytes.toBytes("value"));
                    table.put(put);
                    return null;
                }
            };
            TestAccessController.USER_ADMIN.runAs(putAction);
        }
    }

    @Test
    public void testTableDescriptorsEnumeration() throws Exception {
        User TABLE_ADMIN = User.createUserForTesting(TestAccessController.conf, "UserA", new String[0]);
        // Grant TABLE ADMIN privs
        SecureTestUtil.grantOnTable(TestAccessController.TEST_UTIL, TABLE_ADMIN.getShortName(), TestAccessController.TEST_TABLE, null, null, ADMIN);
        try {
            SecureTestUtil.AccessTestAction listTablesAction = new SecureTestUtil.AccessTestAction() {
                @Override
                public Object run() throws Exception {
                    try (Connection conn = ConnectionFactory.createConnection(TestAccessController.TEST_UTIL.getConfiguration());Admin admin = conn.getAdmin()) {
                        return Arrays.asList(admin.listTables());
                    }
                }
            };
            SecureTestUtil.AccessTestAction getTableDescAction = new SecureTestUtil.AccessTestAction() {
                @Override
                public Object run() throws Exception {
                    try (Connection conn = ConnectionFactory.createConnection(TestAccessController.TEST_UTIL.getConfiguration());Admin admin = conn.getAdmin()) {
                        return admin.getTableDescriptor(TestAccessController.TEST_TABLE);
                    }
                }
            };
            SecureTestUtil.verifyAllowed(listTablesAction, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_CREATE, TestAccessController.USER_OWNER, TABLE_ADMIN, TestAccessController.USER_GROUP_CREATE, TestAccessController.USER_GROUP_ADMIN);
            SecureTestUtil.verifyIfEmptyList(listTablesAction, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE);
            SecureTestUtil.verifyAllowed(getTableDescAction, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_CREATE, TestAccessController.USER_OWNER, TABLE_ADMIN, TestAccessController.USER_GROUP_CREATE, TestAccessController.USER_GROUP_ADMIN);
            SecureTestUtil.verifyDenied(getTableDescAction, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE);
        } finally {
            // Cleanup, revoke TABLE ADMIN privs
            SecureTestUtil.revokeFromTable(TestAccessController.TEST_UTIL, TABLE_ADMIN.getShortName(), TestAccessController.TEST_TABLE, null, null, ADMIN);
        }
    }

    @Test
    public void testTableNameEnumeration() throws Exception {
        SecureTestUtil.AccessTestAction listTablesAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                Connection unmanagedConnection = ConnectionFactory.createConnection(TestAccessController.TEST_UTIL.getConfiguration());
                Admin admin = unmanagedConnection.getAdmin();
                try {
                    return Arrays.asList(admin.listTableNames());
                } finally {
                    admin.close();
                    unmanagedConnection.close();
                }
            }
        };
        SecureTestUtil.verifyAllowed(listTablesAction, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_CREATE, TestAccessController.USER_OWNER, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_GROUP_CREATE, TestAccessController.USER_GROUP_ADMIN, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE);
        SecureTestUtil.verifyIfEmptyList(listTablesAction, TestAccessController.USER_NONE);
    }

    @Test
    public void testTableDeletion() throws Exception {
        User TABLE_ADMIN = User.createUserForTesting(TestAccessController.conf, "TestUser", new String[0]);
        final TableName tableName = TableName.valueOf(name.getMethodName());
        createTestTable(tableName);
        // Grant TABLE ADMIN privs
        SecureTestUtil.grantOnTable(TestAccessController.TEST_UTIL, TABLE_ADMIN.getShortName(), tableName, null, null, ADMIN);
        SecureTestUtil.AccessTestAction deleteTableAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                Connection unmanagedConnection = ConnectionFactory.createConnection(TestAccessController.TEST_UTIL.getConfiguration());
                Admin admin = unmanagedConnection.getAdmin();
                try {
                    SecureTestUtil.deleteTable(TestAccessController.TEST_UTIL, admin, tableName);
                } finally {
                    admin.close();
                    unmanagedConnection.close();
                }
                return null;
            }
        };
        SecureTestUtil.verifyDenied(deleteTableAction, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE);
        SecureTestUtil.verifyAllowed(deleteTableAction, TABLE_ADMIN);
    }

    @Test
    public void testNamespaceUserGrant() throws Exception {
        SecureTestUtil.AccessTestAction getAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                try (Connection conn = ConnectionFactory.createConnection(TestAccessController.conf);Table t = conn.getTable(TestAccessController.TEST_TABLE)) {
                    return t.get(new Get(TestAccessController.TEST_ROW));
                }
            }
        };
        String namespace = TestAccessController.TEST_TABLE.getNamespaceAsString();
        // Grant namespace READ to USER_NONE, this should supersede any table permissions
        SecureTestUtil.grantOnNamespace(TestAccessController.TEST_UTIL, TestAccessController.USER_NONE.getShortName(), namespace, Permission.Action.READ);
        // Now USER_NONE should be able to read
        SecureTestUtil.verifyAllowed(getAction, TestAccessController.USER_NONE);
        // Revoke namespace READ to USER_NONE
        SecureTestUtil.revokeFromNamespace(TestAccessController.TEST_UTIL, TestAccessController.USER_NONE.getShortName(), namespace, Permission.Action.READ);
        SecureTestUtil.verifyDenied(getAction, TestAccessController.USER_NONE);
    }

    @Test
    public void testAccessControlClientGrantRevoke() throws Exception {
        // Create user for testing, who has no READ privileges by default.
        User testGrantRevoke = User.createUserForTesting(TestAccessController.conf, "testGrantRevoke", new String[0]);
        SecureTestUtil.AccessTestAction getAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                try (Connection conn = ConnectionFactory.createConnection(TestAccessController.conf);Table t = conn.getTable(TestAccessController.TEST_TABLE)) {
                    return t.get(new Get(TestAccessController.TEST_ROW));
                }
            }
        };
        SecureTestUtil.verifyDenied(getAction, testGrantRevoke);
        // Grant table READ permissions to testGrantRevoke.
        try {
            SecureTestUtil.grantOnTableUsingAccessControlClient(TestAccessController.TEST_UTIL, TestAccessController.systemUserConnection, testGrantRevoke.getShortName(), TestAccessController.TEST_TABLE, null, null, Permission.Action.READ);
        } catch (Throwable e) {
            TestAccessController.LOG.error("error during call of AccessControlClient.grant. ", e);
        }
        // Now testGrantRevoke should be able to read also
        SecureTestUtil.verifyAllowed(getAction, testGrantRevoke);
        // Revoke table READ permission to testGrantRevoke.
        try {
            SecureTestUtil.revokeFromTableUsingAccessControlClient(TestAccessController.TEST_UTIL, TestAccessController.systemUserConnection, testGrantRevoke.getShortName(), TestAccessController.TEST_TABLE, null, null, Permission.Action.READ);
        } catch (Throwable e) {
            TestAccessController.LOG.error("error during call of AccessControlClient.revoke ", e);
        }
        // Now testGrantRevoke shouldn't be able read
        SecureTestUtil.verifyDenied(getAction, testGrantRevoke);
    }

    @Test
    public void testAccessControlClientGlobalGrantRevoke() throws Exception {
        // Create user for testing, who has no READ privileges by default.
        User testGlobalGrantRevoke = User.createUserForTesting(TestAccessController.conf, "testGlobalGrantRevoke", new String[0]);
        SecureTestUtil.AccessTestAction getAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                try (Connection conn = ConnectionFactory.createConnection(TestAccessController.conf);Table t = conn.getTable(TestAccessController.TEST_TABLE)) {
                    return t.get(new Get(TestAccessController.TEST_ROW));
                }
            }
        };
        SecureTestUtil.verifyDenied(getAction, testGlobalGrantRevoke);
        // Grant table READ permissions to testGlobalGrantRevoke.
        String userName = testGlobalGrantRevoke.getShortName();
        try {
            SecureTestUtil.grantGlobalUsingAccessControlClient(TestAccessController.TEST_UTIL, TestAccessController.systemUserConnection, userName, Permission.Action.READ);
        } catch (Throwable e) {
            TestAccessController.LOG.error("error during call of AccessControlClient.grant. ", e);
        }
        try {
            // Now testGlobalGrantRevoke should be able to read also
            SecureTestUtil.verifyAllowed(getAction, testGlobalGrantRevoke);
        } catch (Exception e) {
            SecureTestUtil.revokeGlobal(TestAccessController.TEST_UTIL, userName, Permission.Action.READ);
            throw e;
        }
        // Revoke table READ permission to testGlobalGrantRevoke.
        try {
            SecureTestUtil.revokeGlobalUsingAccessControlClient(TestAccessController.TEST_UTIL, TestAccessController.systemUserConnection, userName, Permission.Action.READ);
        } catch (Throwable e) {
            TestAccessController.LOG.error("error during call of AccessControlClient.revoke ", e);
        }
        // Now testGlobalGrantRevoke shouldn't be able read
        SecureTestUtil.verifyDenied(getAction, testGlobalGrantRevoke);
    }

    @Test
    public void testAccessControlClientMultiGrantRevoke() throws Exception {
        User testGrantRevoke = User.createUserForTesting(TestAccessController.conf, "testGrantRevoke", new String[0]);
        SecureTestUtil.AccessTestAction getAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                try (Connection conn = ConnectionFactory.createConnection(TestAccessController.conf);Table t = conn.getTable(TestAccessController.TEST_TABLE)) {
                    return t.get(new Get(TestAccessController.TEST_ROW));
                }
            }
        };
        SecureTestUtil.AccessTestAction putAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                Put p = new Put(TestAccessController.TEST_ROW);
                p.addColumn(TestAccessController.TEST_FAMILY, TestAccessController.TEST_QUALIFIER, Bytes.toBytes(1));
                try (Connection conn = ConnectionFactory.createConnection(TestAccessController.conf);Table t = conn.getTable(TestAccessController.TEST_TABLE)) {
                    t.put(p);
                    return null;
                }
            }
        };
        SecureTestUtil.verifyDenied(getAction, testGrantRevoke);
        SecureTestUtil.verifyDenied(putAction, testGrantRevoke);
        // Grant global READ permissions to testGrantRevoke.
        String userName = testGrantRevoke.getShortName();
        try {
            SecureTestUtil.grantGlobalUsingAccessControlClient(TestAccessController.TEST_UTIL, TestAccessController.systemUserConnection, userName, Permission.Action.READ);
        } catch (Throwable e) {
            TestAccessController.LOG.error("error during call of AccessControlClient.grant. ", e);
        }
        SecureTestUtil.verifyAllowed(getAction, testGrantRevoke);
        SecureTestUtil.verifyDenied(putAction, testGrantRevoke);
        // Grant global WRITE permissions to testGrantRevoke.
        try {
            SecureTestUtil.grantGlobalUsingAccessControlClient(TestAccessController.TEST_UTIL, TestAccessController.systemUserConnection, userName, WRITE);
        } catch (Throwable e) {
            TestAccessController.LOG.error("error during call of AccessControlClient.grant. ", e);
        }
        SecureTestUtil.verifyAllowed(getAction, testGrantRevoke);
        SecureTestUtil.verifyAllowed(putAction, testGrantRevoke);
        // Revoke global READ permission to testGrantRevoke.
        try {
            SecureTestUtil.revokeGlobalUsingAccessControlClient(TestAccessController.TEST_UTIL, TestAccessController.systemUserConnection, userName, Permission.Action.READ, WRITE);
        } catch (Throwable e) {
            TestAccessController.LOG.error("error during call of AccessControlClient.revoke ", e);
        }
        SecureTestUtil.verifyDenied(getAction, testGrantRevoke);
        SecureTestUtil.verifyDenied(putAction, testGrantRevoke);
        // Grant table READ & WRITE permissions to testGrantRevoke
        try {
            SecureTestUtil.grantOnTableUsingAccessControlClient(TestAccessController.TEST_UTIL, TestAccessController.systemUserConnection, userName, TestAccessController.TEST_TABLE, null, null, Permission.Action.READ);
        } catch (Throwable e) {
            TestAccessController.LOG.error("error during call of AccessControlClient.grant. ", e);
        }
        SecureTestUtil.verifyAllowed(getAction, testGrantRevoke);
        SecureTestUtil.verifyDenied(putAction, testGrantRevoke);
        // Grant table WRITE permissions to testGrantRevoke
        try {
            SecureTestUtil.grantOnTableUsingAccessControlClient(TestAccessController.TEST_UTIL, TestAccessController.systemUserConnection, userName, TestAccessController.TEST_TABLE, null, null, Action.WRITE);
        } catch (Throwable e) {
            TestAccessController.LOG.error("error during call of AccessControlClient.grant. ", e);
        }
        SecureTestUtil.verifyAllowed(getAction, testGrantRevoke);
        SecureTestUtil.verifyAllowed(putAction, testGrantRevoke);
        // Revoke table READ & WRITE permission to testGrantRevoke.
        try {
            SecureTestUtil.revokeFromTableUsingAccessControlClient(TestAccessController.TEST_UTIL, TestAccessController.systemUserConnection, userName, TestAccessController.TEST_TABLE, null, null, Permission.Action.READ, WRITE);
        } catch (Throwable e) {
            TestAccessController.LOG.error("error during call of AccessControlClient.revoke ", e);
        }
        SecureTestUtil.verifyDenied(getAction, testGrantRevoke);
        SecureTestUtil.verifyDenied(putAction, testGrantRevoke);
        // Grant Namespace READ permissions to testGrantRevoke
        String namespace = TestAccessController.TEST_TABLE.getNamespaceAsString();
        try {
            SecureTestUtil.grantOnNamespaceUsingAccessControlClient(TestAccessController.TEST_UTIL, TestAccessController.systemUserConnection, userName, namespace, Permission.Action.READ);
        } catch (Throwable e) {
            TestAccessController.LOG.error("error during call of AccessControlClient.grant. ", e);
        }
        SecureTestUtil.verifyAllowed(getAction, testGrantRevoke);
        SecureTestUtil.verifyDenied(putAction, testGrantRevoke);
        // Grant Namespace WRITE permissions to testGrantRevoke
        try {
            SecureTestUtil.grantOnNamespaceUsingAccessControlClient(TestAccessController.TEST_UTIL, TestAccessController.systemUserConnection, userName, namespace, WRITE);
        } catch (Throwable e) {
            TestAccessController.LOG.error("error during call of AccessControlClient.grant. ", e);
        }
        SecureTestUtil.verifyAllowed(getAction, testGrantRevoke);
        SecureTestUtil.verifyAllowed(putAction, testGrantRevoke);
        // Revoke table READ & WRITE permission to testGrantRevoke.
        try {
            SecureTestUtil.revokeFromNamespaceUsingAccessControlClient(TestAccessController.TEST_UTIL, TestAccessController.systemUserConnection, userName, TestAccessController.TEST_TABLE.getNamespaceAsString(), Permission.Action.READ, WRITE);
        } catch (Throwable e) {
            TestAccessController.LOG.error("error during call of AccessControlClient.revoke ", e);
        }
        SecureTestUtil.verifyDenied(getAction, testGrantRevoke);
        SecureTestUtil.verifyDenied(putAction, testGrantRevoke);
    }

    @Test
    public void testAccessControlClientGrantRevokeOnNamespace() throws Exception {
        // Create user for testing, who has no READ privileges by default.
        User testNS = User.createUserForTesting(TestAccessController.conf, "testNS", new String[0]);
        SecureTestUtil.AccessTestAction getAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                try (Connection conn = ConnectionFactory.createConnection(TestAccessController.conf);Table t = conn.getTable(TestAccessController.TEST_TABLE)) {
                    return t.get(new Get(TestAccessController.TEST_ROW));
                }
            }
        };
        SecureTestUtil.verifyDenied(getAction, testNS);
        String userName = testNS.getShortName();
        String namespace = TestAccessController.TEST_TABLE.getNamespaceAsString();
        // Grant namespace READ to testNS, this should supersede any table permissions
        try {
            SecureTestUtil.grantOnNamespaceUsingAccessControlClient(TestAccessController.TEST_UTIL, TestAccessController.systemUserConnection, userName, namespace, Permission.Action.READ);
        } catch (Throwable e) {
            TestAccessController.LOG.error("error during call of AccessControlClient.grant. ", e);
        }
        try {
            // Now testNS should be able to read also
            SecureTestUtil.verifyAllowed(getAction, testNS);
        } catch (Exception e) {
            SecureTestUtil.revokeFromNamespace(TestAccessController.TEST_UTIL, userName, namespace, Permission.Action.READ);
            throw e;
        }
        // Revoke namespace READ to testNS, this should supersede any table permissions
        try {
            SecureTestUtil.revokeFromNamespaceUsingAccessControlClient(TestAccessController.TEST_UTIL, TestAccessController.systemUserConnection, userName, namespace, Permission.Action.READ);
        } catch (Throwable e) {
            TestAccessController.LOG.error("error during call of AccessControlClient.revoke ", e);
        }
        // Now testNS shouldn't be able read
        SecureTestUtil.verifyDenied(getAction, testNS);
    }

    public static class PingCoprocessor extends PingService implements RegionCoprocessor {
        @Override
        public void start(CoprocessorEnvironment env) throws IOException {
        }

        @Override
        public void stop(CoprocessorEnvironment env) throws IOException {
        }

        @Override
        public Iterable<Service> getServices() {
            return Collections.singleton(this);
        }

        @Override
        public void ping(RpcController controller, PingRequest request, RpcCallback<PingResponse> callback) {
            callback.run(PingResponse.newBuilder().setPong("Pong!").build());
        }

        @Override
        public void count(RpcController controller, CountRequest request, RpcCallback<CountResponse> callback) {
            callback.run(CountResponse.newBuilder().build());
        }

        @Override
        public void increment(RpcController controller, IncrementCountRequest requet, RpcCallback<IncrementCountResponse> callback) {
            callback.run(IncrementCountResponse.newBuilder().build());
        }

        @Override
        public void hello(RpcController controller, HelloRequest request, RpcCallback<HelloResponse> callback) {
            callback.run(HelloResponse.newBuilder().setResponse("Hello!").build());
        }

        @Override
        public void noop(RpcController controller, NoopRequest request, RpcCallback<NoopResponse> callback) {
            callback.run(NoopResponse.newBuilder().build());
        }
    }

    @Test
    public void testCoprocessorExec() throws Exception {
        // Set up our ping endpoint service on all regions of our test table
        for (JVMClusterUtil.RegionServerThread thread : TestAccessController.TEST_UTIL.getMiniHBaseCluster().getRegionServerThreads()) {
            HRegionServer rs = thread.getRegionServer();
            for (HRegion region : rs.getRegions(TestAccessController.TEST_TABLE)) {
                region.getCoprocessorHost().load(TestAccessController.PingCoprocessor.class, PRIORITY_USER, TestAccessController.conf);
            }
        }
        // Create users for testing, and grant EXEC privileges on our test table
        // only to user A
        User userA = User.createUserForTesting(TestAccessController.conf, "UserA", new String[0]);
        User userB = User.createUserForTesting(TestAccessController.conf, "UserB", new String[0]);
        SecureTestUtil.grantOnTable(TestAccessController.TEST_UTIL, userA.getShortName(), TestAccessController.TEST_TABLE, null, null, EXEC);
        try {
            // Create an action for invoking our test endpoint
            SecureTestUtil.AccessTestAction execEndpointAction = new SecureTestUtil.AccessTestAction() {
                @Override
                public Object run() throws Exception {
                    try (Connection conn = ConnectionFactory.createConnection(TestAccessController.conf);Table t = conn.getTable(TestAccessController.TEST_TABLE)) {
                        BlockingRpcChannel service = t.coprocessorService(EMPTY_BYTE_ARRAY);
                        TestAccessController.PingCoprocessor.newBlockingStub(service).noop(null, NoopRequest.newBuilder().build());
                    }
                    return null;
                }
            };
            String namespace = TestAccessController.TEST_TABLE.getNamespaceAsString();
            // Now grant EXEC to the entire namespace to user B
            SecureTestUtil.grantOnNamespace(TestAccessController.TEST_UTIL, userB.getShortName(), namespace, EXEC);
            // User B should now be allowed also
            SecureTestUtil.verifyAllowed(execEndpointAction, userA, userB);
            SecureTestUtil.revokeFromNamespace(TestAccessController.TEST_UTIL, userB.getShortName(), namespace, EXEC);
            // Verify that EXEC permission is checked correctly
            SecureTestUtil.verifyDenied(execEndpointAction, userB);
            SecureTestUtil.verifyAllowed(execEndpointAction, userA);
        } finally {
            // Cleanup, revoke the userA privileges
            SecureTestUtil.revokeFromTable(TestAccessController.TEST_UTIL, userA.getShortName(), TestAccessController.TEST_TABLE, null, null, EXEC);
        }
    }

    @Test
    public void testSetQuota() throws Exception {
        SecureTestUtil.AccessTestAction setUserQuotaAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preSetUserQuota(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), null, null);
                return null;
            }
        };
        SecureTestUtil.AccessTestAction setUserTableQuotaAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preSetUserQuota(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), null, TestAccessController.TEST_TABLE, null);
                return null;
            }
        };
        SecureTestUtil.AccessTestAction setUserNamespaceQuotaAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preSetUserQuota(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), null, ((String) (null)), null);
                return null;
            }
        };
        SecureTestUtil.AccessTestAction setTableQuotaAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preSetTableQuota(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), TestAccessController.TEST_TABLE, null);
                return null;
            }
        };
        SecureTestUtil.AccessTestAction setNamespaceQuotaAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preSetNamespaceQuota(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), null, null);
                return null;
            }
        };
        SecureTestUtil.AccessTestAction setRegionServerQuotaAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preSetRegionServerQuota(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), null, null);
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(setUserQuotaAction, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_GROUP_ADMIN);
        SecureTestUtil.verifyDenied(setUserQuotaAction, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_OWNER, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE, TestAccessController.USER_GROUP_CREATE);
        SecureTestUtil.verifyAllowed(setUserTableQuotaAction, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_OWNER, TestAccessController.USER_GROUP_ADMIN);
        SecureTestUtil.verifyDenied(setUserTableQuotaAction, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE, TestAccessController.USER_GROUP_CREATE);
        SecureTestUtil.verifyAllowed(setUserNamespaceQuotaAction, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_GROUP_ADMIN);
        SecureTestUtil.verifyDenied(setUserNamespaceQuotaAction, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_OWNER, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE, TestAccessController.USER_GROUP_CREATE);
        SecureTestUtil.verifyAllowed(setTableQuotaAction, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_OWNER, TestAccessController.USER_GROUP_ADMIN);
        SecureTestUtil.verifyDenied(setTableQuotaAction, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE);
        SecureTestUtil.verifyAllowed(setNamespaceQuotaAction, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_GROUP_ADMIN);
        SecureTestUtil.verifyDenied(setNamespaceQuotaAction, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_OWNER, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE, TestAccessController.USER_GROUP_CREATE);
        SecureTestUtil.verifyAllowed(setRegionServerQuotaAction, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_GROUP_ADMIN);
        SecureTestUtil.verifyDenied(setRegionServerQuotaAction, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_OWNER, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_WRITE, TestAccessController.USER_GROUP_CREATE);
    }

    @Test
    public void testGetNamespacePermission() throws Exception {
        String namespace = "testGetNamespacePermission";
        NamespaceDescriptor desc = NamespaceDescriptor.create(namespace).build();
        SecureTestUtil.createNamespace(TestAccessController.TEST_UTIL, desc);
        SecureTestUtil.grantOnNamespace(TestAccessController.TEST_UTIL, TestAccessController.USER_NONE.getShortName(), namespace, Permission.Action.READ);
        // Test 1: A specific namespace
        getNamespacePermissionsAndVerify(namespace, 1, namespace);
        // Test 2: '@.*'
        getNamespacePermissionsAndVerify(".*", 1, namespace);
        // Test 3: A more complex regex
        getNamespacePermissionsAndVerify("^test[a-zA-Z]*", 1, namespace);
        SecureTestUtil.deleteNamespace(TestAccessController.TEST_UTIL, namespace);
    }

    @Test
    public void testTruncatePerms() throws Exception {
        try {
            List<UserPermission> existingPerms = AccessControlClient.getUserPermissions(TestAccessController.systemUserConnection, TestAccessController.TEST_TABLE.getNameAsString());
            Assert.assertTrue((existingPerms != null));
            Assert.assertTrue(((existingPerms.size()) > 1));
            TestAccessController.TEST_UTIL.getAdmin().disableTable(TestAccessController.TEST_TABLE);
            TestAccessController.TEST_UTIL.truncateTable(TestAccessController.TEST_TABLE);
            TestAccessController.TEST_UTIL.waitTableAvailable(TestAccessController.TEST_TABLE);
            List<UserPermission> perms = AccessControlClient.getUserPermissions(TestAccessController.systemUserConnection, TestAccessController.TEST_TABLE.getNameAsString());
            Assert.assertTrue((perms != null));
            Assert.assertEquals(existingPerms.size(), perms.size());
        } catch (Throwable e) {
            throw new HBaseIOException(e);
        }
    }

    @Test
    public void testAccessControlClientUserPerms() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        createTestTable(tableName);
        try {
            final String regex = tableName.getNameWithNamespaceInclAsString();
            User testUserPerms = User.createUserForTesting(TestAccessController.conf, "testUserPerms", new String[0]);
            Assert.assertEquals(0, testUserPerms.runAs(getPrivilegedAction(regex)).size());
            // Grant TABLE ADMIN privs to testUserPerms
            SecureTestUtil.grantOnTable(TestAccessController.TEST_UTIL, testUserPerms.getShortName(), tableName, null, null, Action.ADMIN);
            List<UserPermission> perms = testUserPerms.runAs(getPrivilegedAction(regex));
            Assert.assertNotNull(perms);
            // Superuser, testUserPerms
            Assert.assertEquals(2, perms.size());
        } finally {
            SecureTestUtil.deleteTable(TestAccessController.TEST_UTIL, tableName);
        }
    }

    @Test
    public void testAccessControllerUserPermsRegexHandling() throws Exception {
        User testRegexHandler = User.createUserForTesting(TestAccessController.conf, "testRegexHandling", new String[0]);
        final String REGEX_ALL_TABLES = ".*";
        final String tableName = name.getMethodName();
        final TableName table1 = TableName.valueOf(tableName);
        final byte[] family = Bytes.toBytes("f1");
        // create table in default ns
        Admin admin = TestAccessController.TEST_UTIL.getAdmin();
        HTableDescriptor htd = new HTableDescriptor(table1);
        htd.addFamily(new HColumnDescriptor(family));
        SecureTestUtil.createTable(TestAccessController.TEST_UTIL, htd);
        // creating the ns and table in it
        String ns = "testNamespace";
        NamespaceDescriptor desc = NamespaceDescriptor.create(ns).build();
        final TableName table2 = TableName.valueOf(ns, tableName);
        SecureTestUtil.createNamespace(TestAccessController.TEST_UTIL, desc);
        htd = new HTableDescriptor(table2);
        htd.addFamily(new HColumnDescriptor(family));
        SecureTestUtil.createTable(TestAccessController.TEST_UTIL, htd);
        // Verify that we can read sys-tables
        String aclTableName = ACL_TABLE_NAME.getNameAsString();
        Assert.assertEquals(5, TestAccessController.SUPERUSER.runAs(getPrivilegedAction(aclTableName)).size());
        Assert.assertEquals(0, testRegexHandler.runAs(getPrivilegedAction(aclTableName)).size());
        // Grant TABLE ADMIN privs to testUserPerms
        Assert.assertEquals(0, testRegexHandler.runAs(getPrivilegedAction(REGEX_ALL_TABLES)).size());
        SecureTestUtil.grantOnTable(TestAccessController.TEST_UTIL, testRegexHandler.getShortName(), table1, null, null, Action.ADMIN);
        Assert.assertEquals(2, testRegexHandler.runAs(getPrivilegedAction(REGEX_ALL_TABLES)).size());
        SecureTestUtil.grantOnTable(TestAccessController.TEST_UTIL, testRegexHandler.getShortName(), table2, null, null, Action.ADMIN);
        Assert.assertEquals(4, testRegexHandler.runAs(getPrivilegedAction(REGEX_ALL_TABLES)).size());
        // USER_ADMIN, testUserPerms must have a row each.
        Assert.assertEquals(2, testRegexHandler.runAs(getPrivilegedAction(tableName)).size());
        Assert.assertEquals(2, testRegexHandler.runAs(getPrivilegedAction((((NamespaceDescriptor.DEFAULT_NAMESPACE_NAME_STR) + (TableName.NAMESPACE_DELIM)) + tableName))).size());
        Assert.assertEquals(2, testRegexHandler.runAs(getPrivilegedAction(((ns + (TableName.NAMESPACE_DELIM)) + tableName))).size());
        Assert.assertEquals(0, testRegexHandler.runAs(getPrivilegedAction("notMatchingAny")).size());
        SecureTestUtil.deleteTable(TestAccessController.TEST_UTIL, table1);
        SecureTestUtil.deleteTable(TestAccessController.TEST_UTIL, table2);
        SecureTestUtil.deleteNamespace(TestAccessController.TEST_UTIL, ns);
    }

    @Test
    public void testPrepareAndCleanBulkLoad() throws Exception {
        SecureTestUtil.AccessTestAction prepareBulkLoadAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.prePrepareBulkLoad(ObserverContextImpl.createAndPrepare(TestAccessController.RCP_ENV));
                return null;
            }
        };
        SecureTestUtil.AccessTestAction cleanupBulkLoadAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preCleanupBulkLoad(ObserverContextImpl.createAndPrepare(TestAccessController.RCP_ENV));
                return null;
            }
        };
        verifyAnyCreate(prepareBulkLoadAction);
        verifyAnyCreate(cleanupBulkLoadAction);
    }

    @Test
    public void testReplicateLogEntries() throws Exception {
        SecureTestUtil.AccessTestAction replicateLogEntriesAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preReplicateLogEntries(ObserverContextImpl.createAndPrepare(TestAccessController.RSCP_ENV));
                TestAccessController.ACCESS_CONTROLLER.postReplicateLogEntries(ObserverContextImpl.createAndPrepare(TestAccessController.RSCP_ENV));
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(replicateLogEntriesAction, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_GROUP_WRITE);
        SecureTestUtil.verifyDenied(replicateLogEntriesAction, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_OWNER, TestAccessController.USER_GROUP_READ, TestAccessController.USER_GROUP_ADMIN, TestAccessController.USER_GROUP_CREATE);
    }

    @Test
    public void testAddReplicationPeer() throws Exception {
        SecureTestUtil.AccessTestAction action = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preAddReplicationPeer(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), "test", null);
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(action, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN);
        SecureTestUtil.verifyDenied(action, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_OWNER);
    }

    @Test
    public void testRemoveReplicationPeer() throws Exception {
        SecureTestUtil.AccessTestAction action = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preRemoveReplicationPeer(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), "test");
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(action, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN);
        SecureTestUtil.verifyDenied(action, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_OWNER);
    }

    @Test
    public void testEnableReplicationPeer() throws Exception {
        SecureTestUtil.AccessTestAction action = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preEnableReplicationPeer(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), "test");
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(action, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN);
        SecureTestUtil.verifyDenied(action, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_OWNER);
    }

    @Test
    public void testDisableReplicationPeer() throws Exception {
        SecureTestUtil.AccessTestAction action = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preDisableReplicationPeer(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), "test");
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(action, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN);
        SecureTestUtil.verifyDenied(action, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_OWNER);
    }

    @Test
    public void testGetReplicationPeerConfig() throws Exception {
        SecureTestUtil.AccessTestAction action = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preGetReplicationPeerConfig(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), "test");
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(action, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN);
        SecureTestUtil.verifyDenied(action, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_OWNER);
    }

    @Test
    public void testUpdateReplicationPeerConfig() throws Exception {
        SecureTestUtil.AccessTestAction action = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preUpdateReplicationPeerConfig(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), "test", new ReplicationPeerConfig());
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(action, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN);
        SecureTestUtil.verifyDenied(action, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_OWNER);
    }

    @Test
    public void testTransitSyncReplicationPeerState() throws Exception {
        SecureTestUtil.AccessTestAction action = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preTransitReplicationPeerSyncReplicationState(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), "test", NONE);
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(action, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN);
        SecureTestUtil.verifyDenied(action, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_OWNER);
    }

    @Test
    public void testListReplicationPeers() throws Exception {
        SecureTestUtil.AccessTestAction action = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preListReplicationPeers(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), "test");
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(action, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN);
        SecureTestUtil.verifyDenied(action, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_OWNER);
    }

    @Test
    public void testRemoteLocks() throws Exception {
        String namespace = "preQueueNs";
        final TableName tableName = TableName.valueOf(namespace, name.getMethodName());
        HRegionInfo[] regionInfos = new HRegionInfo[]{ new HRegionInfo(tableName) };
        // Setup Users
        // User will be granted ADMIN and CREATE on namespace. Should be denied before grant.
        User namespaceUser = User.createUserForTesting(TestAccessController.conf, "qLNSUser", new String[0]);
        // User will be granted ADMIN and CREATE on table. Should be denied before grant.
        User tableACUser = User.createUserForTesting(TestAccessController.conf, "qLTableACUser", new String[0]);
        // User will be granted READ, WRITE, EXECUTE on table. Should be denied.
        User tableRWXUser = User.createUserForTesting(TestAccessController.conf, "qLTableRWXUser", new String[0]);
        SecureTestUtil.grantOnTable(TestAccessController.TEST_UTIL, tableRWXUser.getShortName(), tableName, null, null, READ, Action.WRITE, Action.EXEC);
        // User with global READ, WRITE, EXECUTE should be denied lock access.
        User globalRWXUser = User.createUserForTesting(TestAccessController.conf, "qLGlobalRWXUser", new String[0]);
        SecureTestUtil.grantGlobal(TestAccessController.TEST_UTIL, globalRWXUser.getShortName(), READ, Action.WRITE, Action.EXEC);
        SecureTestUtil.AccessTestAction namespaceLockAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preRequestLock(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), namespace, null, null, null);
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(namespaceLockAction, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN);
        SecureTestUtil.verifyDenied(namespaceLockAction, globalRWXUser, tableACUser, namespaceUser, tableRWXUser);
        SecureTestUtil.grantOnNamespace(TestAccessController.TEST_UTIL, namespaceUser.getShortName(), namespace, Action.ADMIN);
        // Why I need this pause? I don't need it elsewhere.
        Threads.sleep(1000);
        SecureTestUtil.verifyAllowed(namespaceLockAction, namespaceUser);
        SecureTestUtil.AccessTestAction tableLockAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preRequestLock(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), null, tableName, null, null);
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(tableLockAction, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, namespaceUser);
        SecureTestUtil.verifyDenied(tableLockAction, globalRWXUser, tableACUser, tableRWXUser);
        SecureTestUtil.grantOnTable(TestAccessController.TEST_UTIL, tableACUser.getShortName(), tableName, null, null, Action.ADMIN, Action.CREATE);
        SecureTestUtil.verifyAllowed(tableLockAction, tableACUser);
        SecureTestUtil.AccessTestAction regionsLockAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preRequestLock(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), null, null, regionInfos, null);
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(regionsLockAction, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, namespaceUser, tableACUser);
        SecureTestUtil.verifyDenied(regionsLockAction, globalRWXUser, tableRWXUser);
        // Test heartbeats
        // Create a lock procedure and try sending heartbeat to it. It doesn't matter how the lock
        // was created, we just need namespace from the lock's tablename.
        LockProcedure proc = new LockProcedure(TestAccessController.conf, tableName, LockType.EXCLUSIVE, "test", null);
        SecureTestUtil.AccessTestAction regionLockHeartbeatAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preLockHeartbeat(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), proc.getTableName(), proc.getDescription());
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(regionLockHeartbeatAction, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, namespaceUser, tableACUser);
        SecureTestUtil.verifyDenied(regionLockHeartbeatAction, globalRWXUser, tableRWXUser);
    }

    @Test
    public void testAccessControlRevokeOnlyFewPermission() throws Throwable {
        TableName tname = TableName.valueOf("revoke");
        try {
            TestAccessController.TEST_UTIL.createTable(tname, TestAccessController.TEST_FAMILY);
            User testUserPerms = User.createUserForTesting(TestAccessController.conf, "revokePerms", new String[0]);
            Permission[] actions = new Action[]{ Action.READ, Action.WRITE };
            AccessControlClient.grant(TestAccessController.TEST_UTIL.getConnection(), tname, testUserPerms.getShortName(), null, null, actions);
            List<UserPermission> userPermissions = AccessControlClient.getUserPermissions(TestAccessController.TEST_UTIL.getConnection(), tname.getNameAsString());
            Assert.assertEquals(2, userPermissions.size());
            AccessControlClient.revoke(TestAccessController.TEST_UTIL.getConnection(), tname, testUserPerms.getShortName(), null, null, Action.WRITE);
            userPermissions = AccessControlClient.getUserPermissions(TestAccessController.TEST_UTIL.getConnection(), tname.getNameAsString());
            Assert.assertEquals(2, userPermissions.size());
            Permission[] expectedAction = new Action[]{ Action.READ };
            boolean userFound = false;
            for (UserPermission p : userPermissions) {
                if (testUserPerms.getShortName().equals(p.getUser())) {
                    Assert.assertArrayEquals(expectedAction, p.getPermission().getActions());
                    userFound = true;
                    break;
                }
            }
            Assert.assertTrue(userFound);
        } finally {
            TestAccessController.TEST_UTIL.deleteTable(tname);
        }
    }

    @Test
    public void testGetClusterStatus() throws Exception {
        SecureTestUtil.AccessTestAction action = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preGetClusterMetrics(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV));
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(action, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_OWNER);
    }

    @Test
    public void testExecuteProcedures() throws Exception {
        SecureTestUtil.AccessTestAction action = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preExecuteProcedures(ObserverContextImpl.createAndPrepare(TestAccessController.RSCP_ENV));
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(action, TestAccessController.SUPERUSER);
        SecureTestUtil.verifyDenied(action, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_OWNER, TestAccessController.USER_ADMIN);
    }

    @Test(timeout = 180000)
    public void testGetUserPermissions() throws Throwable {
        Connection conn = null;
        try {
            conn = ConnectionFactory.createConnection(TestAccessController.conf);
            User nSUser1 = User.createUserForTesting(TestAccessController.conf, "nsuser1", new String[0]);
            User nSUser2 = User.createUserForTesting(TestAccessController.conf, "nsuser2", new String[0]);
            User nSUser3 = User.createUserForTesting(TestAccessController.conf, "nsuser3", new String[0]);
            // Global access groups
            User globalGroupUser1 = User.createUserForTesting(TestAccessController.conf, "globalGroupUser1", new String[]{ "group_admin" });
            User globalGroupUser2 = User.createUserForTesting(TestAccessController.conf, "globalGroupUser2", new String[]{ "group_admin", "group_create" });
            // Namespace access groups
            User nsGroupUser1 = User.createUserForTesting(TestAccessController.conf, "nsGroupUser1", new String[]{ "ns_group1" });
            User nsGroupUser2 = User.createUserForTesting(TestAccessController.conf, "nsGroupUser2", new String[]{ "ns_group2" });
            // table Access groups
            User tableGroupUser1 = User.createUserForTesting(TestAccessController.conf, "tableGroupUser1", new String[]{ "table_group1" });
            User tableGroupUser2 = User.createUserForTesting(TestAccessController.conf, "tableGroupUser2", new String[]{ "table_group2" });
            // Create namespaces
            String nsPrefix = "testNS";
            final String namespace1 = nsPrefix + "1";
            NamespaceDescriptor desc1 = NamespaceDescriptor.create(namespace1).build();
            SecureTestUtil.createNamespace(TestAccessController.TEST_UTIL, desc1);
            String namespace2 = nsPrefix + "2";
            NamespaceDescriptor desc2 = NamespaceDescriptor.create(namespace2).build();
            SecureTestUtil.createNamespace(TestAccessController.TEST_UTIL, desc2);
            // Grant namespace permission
            SecureTestUtil.grantOnNamespace(TestAccessController.TEST_UTIL, nSUser1.getShortName(), namespace1, ADMIN);
            SecureTestUtil.grantOnNamespace(TestAccessController.TEST_UTIL, nSUser3.getShortName(), namespace1, Permission.Action.READ);
            SecureTestUtil.grantOnNamespace(TestAccessController.TEST_UTIL, AuthUtil.toGroupEntry("ns_group1"), namespace1, ADMIN);
            SecureTestUtil.grantOnNamespace(TestAccessController.TEST_UTIL, nSUser2.getShortName(), namespace2, ADMIN);
            SecureTestUtil.grantOnNamespace(TestAccessController.TEST_UTIL, nSUser3.getShortName(), namespace2, ADMIN);
            SecureTestUtil.grantOnNamespace(TestAccessController.TEST_UTIL, AuthUtil.toGroupEntry("ns_group2"), namespace2, Permission.Action.READ, WRITE);
            // Create tables
            TableName table1 = TableName.valueOf(((namespace1 + (TableName.NAMESPACE_DELIM)) + "t1"));
            TableName table2 = TableName.valueOf(((namespace2 + (TableName.NAMESPACE_DELIM)) + "t2"));
            byte[] TEST_FAMILY2 = Bytes.toBytes("f2");
            byte[] TEST_QUALIFIER2 = Bytes.toBytes("q2");
            createTestTable(table1, TestAccessController.TEST_FAMILY);
            createTestTable(table2, TEST_FAMILY2);
            // Grant table permissions
            SecureTestUtil.grantOnTable(TestAccessController.TEST_UTIL, AuthUtil.toGroupEntry("table_group1"), table1, null, null, ADMIN);
            SecureTestUtil.grantOnTable(TestAccessController.TEST_UTIL, TestAccessController.USER_ADMIN.getShortName(), table1, null, null, ADMIN);
            SecureTestUtil.grantOnTable(TestAccessController.TEST_UTIL, TestAccessController.USER_ADMIN_CF.getShortName(), table1, TestAccessController.TEST_FAMILY, null, ADMIN);
            SecureTestUtil.grantOnTable(TestAccessController.TEST_UTIL, TestAccessController.USER_RW.getShortName(), table1, TestAccessController.TEST_FAMILY, TestAccessController.TEST_QUALIFIER, Permission.Action.READ);
            SecureTestUtil.grantOnTable(TestAccessController.TEST_UTIL, TestAccessController.USER_RW.getShortName(), table1, TestAccessController.TEST_FAMILY, TEST_QUALIFIER2, WRITE);
            SecureTestUtil.grantOnTable(TestAccessController.TEST_UTIL, AuthUtil.toGroupEntry("table_group2"), table2, null, null, ADMIN);
            SecureTestUtil.grantOnTable(TestAccessController.TEST_UTIL, TestAccessController.USER_ADMIN.getShortName(), table2, null, null, ADMIN);
            SecureTestUtil.grantOnTable(TestAccessController.TEST_UTIL, TestAccessController.USER_ADMIN_CF.getShortName(), table2, TEST_FAMILY2, null, ADMIN);
            SecureTestUtil.grantOnTable(TestAccessController.TEST_UTIL, TestAccessController.USER_RW.getShortName(), table2, TEST_FAMILY2, TestAccessController.TEST_QUALIFIER, Permission.Action.READ);
            SecureTestUtil.grantOnTable(TestAccessController.TEST_UTIL, TestAccessController.USER_RW.getShortName(), table2, TEST_FAMILY2, TEST_QUALIFIER2, WRITE);
            List<UserPermission> userPermissions = null;
            Collection<String> superUsers = Superusers.getSuperUsers();
            int superUserCount = superUsers.size();
            // Global User ACL
            validateGlobalUserACLForGetUserPermissions(conn, nSUser1, globalGroupUser1, globalGroupUser2, superUsers, superUserCount);
            // Namespace ACL
            validateNamespaceUserACLForGetUserPermissions(conn, nSUser1, nSUser3, nsGroupUser1, nsGroupUser2, nsPrefix, namespace1, namespace2);
            // Table + Users
            validateTableACLForGetUserPermissions(conn, nSUser1, tableGroupUser1, tableGroupUser2, nsPrefix, table1, table2, TEST_QUALIFIER2, superUsers);
            // exception scenarios
            try {
                // test case with table name as null
                Assert.assertEquals(3, AccessControlClient.getUserPermissions(conn, null, TestAccessController.TEST_FAMILY).size());
                Assert.fail("this should have thrown IllegalArgumentException");
            } catch (IllegalArgumentException ex) {
                // expected
            }
            try {
                // test case with table name as emplty
                Assert.assertEquals(3, AccessControlClient.getUserPermissions(conn, EMPTY_STRING, TestAccessController.TEST_FAMILY).size());
                Assert.fail("this should have thrown IllegalArgumentException");
            } catch (IllegalArgumentException ex) {
                // expected
            }
            try {
                // test case with table name as namespace name
                Assert.assertEquals(3, AccessControlClient.getUserPermissions(conn, ("@" + namespace2), TestAccessController.TEST_FAMILY).size());
                Assert.fail("this should have thrown IllegalArgumentException");
            } catch (IllegalArgumentException ex) {
                // expected
            }
            // Clean the table and namespace
            SecureTestUtil.deleteTable(TestAccessController.TEST_UTIL, table1);
            SecureTestUtil.deleteTable(TestAccessController.TEST_UTIL, table2);
            SecureTestUtil.deleteNamespace(TestAccessController.TEST_UTIL, namespace1);
            SecureTestUtil.deleteNamespace(TestAccessController.TEST_UTIL, namespace2);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    @Test(timeout = 180000)
    public void testHasPermission() throws Throwable {
        Connection conn = null;
        try {
            conn = ConnectionFactory.createConnection(TestAccessController.conf);
            // Create user and set namespace ACL
            User user1 = User.createUserForTesting(TestAccessController.conf, "testHasPermissionUser1", new String[0]);
            // Grant namespace permission
            SecureTestUtil.grantOnNamespaceUsingAccessControlClient(TestAccessController.TEST_UTIL, conn, user1.getShortName(), DEFAULT_NAMESPACE.getName(), ADMIN, Permission.Action.CREATE, Permission.Action.READ);
            // Create user and set table ACL
            User user2 = User.createUserForTesting(TestAccessController.conf, "testHasPermissionUser2", new String[0]);
            // Grant namespace permission
            SecureTestUtil.grantOnTableUsingAccessControlClient(TestAccessController.TEST_UTIL, conn, user2.getShortName(), TestAccessController.TEST_TABLE, TestAccessController.TEST_FAMILY, TestAccessController.TEST_QUALIFIER, Permission.Action.READ, WRITE);
            // Verify action privilege
            SecureTestUtil.AccessTestAction hasPermissionAction = new SecureTestUtil.AccessTestAction() {
                @Override
                public Object run() throws Exception {
                    try (Connection conn = ConnectionFactory.createConnection(TestAccessController.conf);Table acl = conn.getTable(ACL_TABLE_NAME)) {
                        BlockingRpcChannel service = acl.coprocessorService(TestAccessController.TEST_TABLE.getName());
                        AccessControlService.BlockingInterface protocol = AccessControlService.newBlockingStub(service);
                        Permission[] actions = new Action[]{ Action.READ, Action.WRITE };
                        AccessControlUtil.hasPermission(null, protocol, TestAccessController.TEST_TABLE, TestAccessController.TEST_FAMILY, EMPTY_BYTE_ARRAY, "dummy", actions);
                    }
                    return null;
                }
            };
            SecureTestUtil.verifyAllowed(hasPermissionAction, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN, TestAccessController.USER_GROUP_ADMIN, TestAccessController.USER_OWNER, TestAccessController.USER_ADMIN_CF, user1);
            SecureTestUtil.verifyDenied(hasPermissionAction, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, user2);
            // Check for global user
            Assert.assertTrue(AccessControlClient.hasPermission(conn, TestAccessController.TEST_TABLE.getNameAsString(), EMPTY_BYTE_ARRAY, EMPTY_BYTE_ARRAY, TestAccessController.USER_ADMIN.getShortName(), Permission.Action.READ, WRITE, Permission.Action.CREATE, ADMIN));
            Assert.assertFalse(AccessControlClient.hasPermission(conn, TestAccessController.TEST_TABLE.getNameAsString(), EMPTY_BYTE_ARRAY, EMPTY_BYTE_ARRAY, TestAccessController.USER_ADMIN.getShortName(), Permission.Action.READ, WRITE, Permission.Action.CREATE, ADMIN, EXEC));
            // Check for namespace access user
            Assert.assertTrue(AccessControlClient.hasPermission(conn, TestAccessController.TEST_TABLE.getNameAsString(), EMPTY_BYTE_ARRAY, EMPTY_BYTE_ARRAY, user1.getShortName(), ADMIN, Permission.Action.CREATE));
            Assert.assertFalse(AccessControlClient.hasPermission(conn, TestAccessController.TEST_TABLE.getNameAsString(), EMPTY_BYTE_ARRAY, EMPTY_BYTE_ARRAY, user1.getShortName(), ADMIN, Permission.Action.READ, EXEC));
            // Check for table owner
            Assert.assertTrue(AccessControlClient.hasPermission(conn, TestAccessController.TEST_TABLE.getNameAsString(), EMPTY_BYTE_ARRAY, EMPTY_BYTE_ARRAY, TestAccessController.USER_OWNER.getShortName(), Permission.Action.READ, WRITE, EXEC, Permission.Action.CREATE, ADMIN));
            // Check for table user
            Assert.assertTrue(AccessControlClient.hasPermission(conn, TestAccessController.TEST_TABLE.getNameAsString(), EMPTY_BYTE_ARRAY, EMPTY_BYTE_ARRAY, TestAccessController.USER_CREATE.getShortName(), Permission.Action.READ, WRITE));
            Assert.assertFalse(AccessControlClient.hasPermission(conn, TestAccessController.TEST_TABLE.getNameAsString(), EMPTY_BYTE_ARRAY, EMPTY_BYTE_ARRAY, TestAccessController.USER_RO.getShortName(), Permission.Action.READ, WRITE));
            // Check for family access user
            Assert.assertTrue(AccessControlClient.hasPermission(conn, TestAccessController.TEST_TABLE.getNameAsString(), TestAccessController.TEST_FAMILY, EMPTY_BYTE_ARRAY, TestAccessController.USER_RO.getShortName(), Permission.Action.READ));
            Assert.assertTrue(AccessControlClient.hasPermission(conn, TestAccessController.TEST_TABLE.getNameAsString(), TestAccessController.TEST_FAMILY, EMPTY_BYTE_ARRAY, TestAccessController.USER_RW.getShortName(), Permission.Action.READ, WRITE));
            Assert.assertFalse(AccessControlClient.hasPermission(conn, TestAccessController.TEST_TABLE.getNameAsString(), EMPTY_BYTE_ARRAY, EMPTY_BYTE_ARRAY, TestAccessController.USER_ADMIN_CF.getShortName(), ADMIN, Permission.Action.CREATE));
            Assert.assertTrue(AccessControlClient.hasPermission(conn, TestAccessController.TEST_TABLE.getNameAsString(), TestAccessController.TEST_FAMILY, EMPTY_BYTE_ARRAY, TestAccessController.USER_ADMIN_CF.getShortName(), ADMIN, Permission.Action.CREATE));
            Assert.assertFalse(AccessControlClient.hasPermission(conn, TestAccessController.TEST_TABLE.getNameAsString(), TestAccessController.TEST_FAMILY, EMPTY_BYTE_ARRAY, TestAccessController.USER_ADMIN_CF.getShortName(), Permission.Action.READ));
            // Check for qualifier access user
            Assert.assertTrue(AccessControlClient.hasPermission(conn, TestAccessController.TEST_TABLE.getNameAsString(), TestAccessController.TEST_FAMILY, TestAccessController.TEST_QUALIFIER, user2.getShortName(), Permission.Action.READ, WRITE));
            Assert.assertFalse(AccessControlClient.hasPermission(conn, TestAccessController.TEST_TABLE.getNameAsString(), TestAccessController.TEST_FAMILY, TestAccessController.TEST_QUALIFIER, user2.getShortName(), EXEC, Permission.Action.READ));
            Assert.assertFalse(AccessControlClient.hasPermission(conn, TestAccessController.TEST_TABLE.getNameAsString(), EMPTY_BYTE_ARRAY, TestAccessController.TEST_QUALIFIER, TestAccessController.USER_RW.getShortName(), WRITE, Permission.Action.READ));
            // exception scenarios
            try {
                // test case with table name as null
                Assert.assertTrue(AccessControlClient.hasPermission(conn, null, EMPTY_BYTE_ARRAY, EMPTY_BYTE_ARRAY, null, Permission.Action.READ));
                Assert.fail("this should have thrown IllegalArgumentException");
            } catch (IllegalArgumentException ex) {
                // expected
            }
            try {
                // test case with username as null
                Assert.assertTrue(AccessControlClient.hasPermission(conn, TestAccessController.TEST_TABLE.getNameAsString(), EMPTY_BYTE_ARRAY, EMPTY_BYTE_ARRAY, null, Permission.Action.READ));
                Assert.fail("this should have thrown IllegalArgumentException");
            } catch (IllegalArgumentException ex) {
                // expected
            }
            SecureTestUtil.revokeFromNamespaceUsingAccessControlClient(TestAccessController.TEST_UTIL, conn, user1.getShortName(), DEFAULT_NAMESPACE.getName(), ADMIN, Permission.Action.CREATE, Permission.Action.READ);
            SecureTestUtil.revokeFromTableUsingAccessControlClient(TestAccessController.TEST_UTIL, conn, user2.getShortName(), TestAccessController.TEST_TABLE, TestAccessController.TEST_FAMILY, TestAccessController.TEST_QUALIFIER, Permission.Action.READ, WRITE);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    @Test
    public void testSwitchRpcThrottle() throws Exception {
        SecureTestUtil.AccessTestAction action = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preSwitchRpcThrottle(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), true);
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(action, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN);
        SecureTestUtil.verifyDenied(action, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_OWNER);
    }

    @Test
    public void testIsRpcThrottleEnabled() throws Exception {
        SecureTestUtil.AccessTestAction action = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preIsRpcThrottleEnabled(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV));
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(action, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN);
        SecureTestUtil.verifyDenied(action, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_OWNER);
    }

    @Test
    public void testSwitchExceedThrottleQuota() throws Exception {
        SecureTestUtil.AccessTestAction action = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestAccessController.ACCESS_CONTROLLER.preSwitchExceedThrottleQuota(ObserverContextImpl.createAndPrepare(TestAccessController.CP_ENV), true);
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(action, TestAccessController.SUPERUSER, TestAccessController.USER_ADMIN);
        SecureTestUtil.verifyDenied(action, TestAccessController.USER_CREATE, TestAccessController.USER_RW, TestAccessController.USER_RO, TestAccessController.USER_NONE, TestAccessController.USER_OWNER);
    }

    /* Dummy ShellBasedUnixGroupsMapping class to retrieve the groups for the test users. */
    public static class MyShellBasedUnixGroupsMapping extends ShellBasedUnixGroupsMapping implements GroupMappingServiceProvider {
        @Override
        public List<String> getGroups(String user) throws IOException {
            if (user.equals("globalGroupUser1")) {
                return Arrays.asList(new String[]{ "group_admin" });
            } else
                if (user.equals("globalGroupUser2")) {
                    return Arrays.asList(new String[]{ "group_admin", "group_create" });
                } else
                    if (user.equals("nsGroupUser1")) {
                        return Arrays.asList(new String[]{ "ns_group1" });
                    } else
                        if (user.equals("nsGroupUser2")) {
                            return Arrays.asList(new String[]{ "ns_group2" });
                        } else
                            if (user.equals("tableGroupUser1")) {
                                return Arrays.asList(new String[]{ "table_group1" });
                            } else
                                if (user.equals("tableGroupUser2")) {
                                    return Arrays.asList(new String[]{ "table_group2" });
                                } else {
                                    return super.getGroups(user);
                                }





        }
    }
}

