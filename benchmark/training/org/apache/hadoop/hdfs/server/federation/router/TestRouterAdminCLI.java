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
package org.apache.hadoop.hdfs.server.federation.router;


import DestinationOrder.HASH;
import DestinationOrder.HASH_ALL;
import DestinationOrder.LOCAL;
import DestinationOrder.RANDOM;
import HdfsConstants.QUOTA_RESET;
import RouterQuotaUsage.QUOTA_USAGE_COUNT_DEFAULT;
import RouterServiceState.RUNNING;
import RouterServiceState.SAFEMODE;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import org.apache.hadoop.hdfs.server.federation.MiniRouterDFSCluster;
import org.apache.hadoop.hdfs.server.federation.StateStoreDFSCluster;
import org.apache.hadoop.hdfs.server.federation.resolver.RemoteLocation;
import org.apache.hadoop.hdfs.server.federation.resolver.order.DestinationOrder;
import org.apache.hadoop.hdfs.server.federation.store.StateStoreService;
import org.apache.hadoop.hdfs.server.federation.store.impl.DisabledNameserviceStoreImpl;
import org.apache.hadoop.hdfs.server.federation.store.impl.MountTableStoreImpl;
import org.apache.hadoop.hdfs.server.federation.store.protocol.GetMountTableEntriesRequest;
import org.apache.hadoop.hdfs.server.federation.store.protocol.GetMountTableEntriesResponse;
import org.apache.hadoop.hdfs.server.federation.store.records.MountTable;
import org.apache.hadoop.hdfs.tools.federation.RouterAdmin;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.util.ToolRunner;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests Router admin commands.
 */
public class TestRouterAdminCLI {
    private static StateStoreDFSCluster cluster;

    private static MiniRouterDFSCluster.RouterContext routerContext;

    private static StateStoreService stateStore;

    private static RouterAdmin admin;

    private static RouterClient client;

    private static final String TEST_USER = "test-user";

    private final ByteArrayOutputStream out = new ByteArrayOutputStream();

    private final ByteArrayOutputStream err = new ByteArrayOutputStream();

    private static final PrintStream OLD_OUT = System.out;

    private static final PrintStream OLD_ERR = System.err;

    @Test
    public void testAddMountTable() throws Exception {
        String nsId = "ns0";
        String src = "/test-addmounttable";
        String dest = "/addmounttable";
        String[] argv = new String[]{ "-add", src, nsId, dest };
        Assert.assertEquals(0, ToolRunner.run(TestRouterAdminCLI.admin, argv));
        TestRouterAdminCLI.stateStore.loadCache(MountTableStoreImpl.class, true);
        GetMountTableEntriesRequest getRequest = GetMountTableEntriesRequest.newInstance(src);
        GetMountTableEntriesResponse getResponse = TestRouterAdminCLI.client.getMountTableManager().getMountTableEntries(getRequest);
        MountTable mountTable = getResponse.getEntries().get(0);
        List<RemoteLocation> destinations = mountTable.getDestinations();
        Assert.assertEquals(1, destinations.size());
        Assert.assertEquals(src, mountTable.getSourcePath());
        Assert.assertEquals(nsId, destinations.get(0).getNameserviceId());
        Assert.assertEquals(dest, destinations.get(0).getDest());
        Assert.assertFalse(mountTable.isReadOnly());
        // test mount table update behavior
        dest = dest + "-new";
        argv = new String[]{ "-add", src, nsId, dest, "-readonly" };
        Assert.assertEquals(0, ToolRunner.run(TestRouterAdminCLI.admin, argv));
        TestRouterAdminCLI.stateStore.loadCache(MountTableStoreImpl.class, true);
        getResponse = TestRouterAdminCLI.client.getMountTableManager().getMountTableEntries(getRequest);
        mountTable = getResponse.getEntries().get(0);
        Assert.assertEquals(2, mountTable.getDestinations().size());
        Assert.assertEquals(nsId, mountTable.getDestinations().get(1).getNameserviceId());
        Assert.assertEquals(dest, mountTable.getDestinations().get(1).getDest());
        Assert.assertTrue(mountTable.isReadOnly());
    }

    @Test
    public void testAddMountTableNotNormalized() throws Exception {
        String nsId = "ns0";
        String src = "/test-addmounttable-notnormalized";
        String srcWithSlash = src + "/";
        String dest = "/addmounttable-notnormalized";
        String[] argv = new String[]{ "-add", srcWithSlash, nsId, dest };
        Assert.assertEquals(0, ToolRunner.run(TestRouterAdminCLI.admin, argv));
        TestRouterAdminCLI.stateStore.loadCache(MountTableStoreImpl.class, true);
        GetMountTableEntriesRequest getRequest = GetMountTableEntriesRequest.newInstance(src);
        GetMountTableEntriesResponse getResponse = TestRouterAdminCLI.client.getMountTableManager().getMountTableEntries(getRequest);
        MountTable mountTable = getResponse.getEntries().get(0);
        List<RemoteLocation> destinations = mountTable.getDestinations();
        Assert.assertEquals(1, destinations.size());
        Assert.assertEquals(src, mountTable.getSourcePath());
        Assert.assertEquals(nsId, destinations.get(0).getNameserviceId());
        Assert.assertEquals(dest, destinations.get(0).getDest());
        Assert.assertFalse(mountTable.isReadOnly());
        // test mount table update behavior
        dest = dest + "-new";
        argv = new String[]{ "-add", srcWithSlash, nsId, dest, "-readonly" };
        Assert.assertEquals(0, ToolRunner.run(TestRouterAdminCLI.admin, argv));
        TestRouterAdminCLI.stateStore.loadCache(MountTableStoreImpl.class, true);
        getResponse = TestRouterAdminCLI.client.getMountTableManager().getMountTableEntries(getRequest);
        mountTable = getResponse.getEntries().get(0);
        Assert.assertEquals(2, mountTable.getDestinations().size());
        Assert.assertEquals(nsId, mountTable.getDestinations().get(1).getNameserviceId());
        Assert.assertEquals(dest, mountTable.getDestinations().get(1).getDest());
        Assert.assertTrue(mountTable.isReadOnly());
    }

    @Test
    public void testAddOrderMountTable() throws Exception {
        testAddOrderMountTable(HASH);
        testAddOrderMountTable(LOCAL);
        testAddOrderMountTable(RANDOM);
        testAddOrderMountTable(HASH_ALL);
    }

    @Test
    public void testAddOrderErrorMsg() throws Exception {
        DestinationOrder order = DestinationOrder.HASH;
        final String mnt = "/newAdd1" + order;
        final String nsId = "ns0,ns1";
        final String dest = "/changAdd";
        String[] argv1 = new String[]{ "-add", mnt, nsId, dest, "-order", order.toString() };
        Assert.assertEquals(0, ToolRunner.run(TestRouterAdminCLI.admin, argv1));
        // Add the order with wrong command
        String[] argv = new String[]{ "-add", mnt, nsId, dest, "-orde", order.toString() };
        Assert.assertEquals((-1), ToolRunner.run(TestRouterAdminCLI.admin, argv));
    }

    @Test
    public void testListMountTable() throws Exception {
        String nsId = "ns0";
        String src = "/test-lsmounttable";
        String srcWithSlash = src + "/";
        String dest = "/lsmounttable";
        String[] argv = new String[]{ "-add", src, nsId, dest };
        Assert.assertEquals(0, ToolRunner.run(TestRouterAdminCLI.admin, argv));
        // re-set system out for testing
        System.setOut(new PrintStream(out));
        TestRouterAdminCLI.stateStore.loadCache(MountTableStoreImpl.class, true);
        argv = new String[]{ "-ls", src };
        Assert.assertEquals(0, ToolRunner.run(TestRouterAdminCLI.admin, argv));
        Assert.assertTrue(out.toString().contains(src));
        // Test with not-normalized src input
        argv = new String[]{ "-ls", srcWithSlash };
        Assert.assertEquals(0, ToolRunner.run(TestRouterAdminCLI.admin, argv));
        Assert.assertTrue(out.toString().contains(src));
        // Test with wrong number of arguments
        argv = new String[]{ "-ls", srcWithSlash, "check", "check2" };
        System.setErr(new PrintStream(err));
        ToolRunner.run(TestRouterAdminCLI.admin, argv);
        Assert.assertTrue(err.toString().contains("Too many arguments, Max=1 argument allowed"));
        out.reset();
        GetMountTableEntriesRequest getRequest = GetMountTableEntriesRequest.newInstance("/");
        GetMountTableEntriesResponse getResponse = TestRouterAdminCLI.client.getMountTableManager().getMountTableEntries(getRequest);
        // Test ls command without input path, it will list
        // mount table under root path.
        argv = new String[]{ "-ls" };
        Assert.assertEquals(0, ToolRunner.run(TestRouterAdminCLI.admin, argv));
        Assert.assertTrue(out.toString().contains(src));
        String outStr = out.toString();
        // verify if all the mount table are listed
        for (MountTable entry : getResponse.getEntries()) {
            Assert.assertTrue(outStr.contains(entry.getSourcePath()));
        }
    }

    @Test
    public void testRemoveMountTable() throws Exception {
        String nsId = "ns0";
        String src = "/test-rmmounttable";
        String dest = "/rmmounttable";
        String[] argv = new String[]{ "-add", src, nsId, dest };
        Assert.assertEquals(0, ToolRunner.run(TestRouterAdminCLI.admin, argv));
        TestRouterAdminCLI.stateStore.loadCache(MountTableStoreImpl.class, true);
        GetMountTableEntriesRequest getRequest = GetMountTableEntriesRequest.newInstance(src);
        GetMountTableEntriesResponse getResponse = TestRouterAdminCLI.client.getMountTableManager().getMountTableEntries(getRequest);
        // ensure mount table added successfully
        MountTable mountTable = getResponse.getEntries().get(0);
        Assert.assertEquals(src, mountTable.getSourcePath());
        argv = new String[]{ "-rm", src };
        Assert.assertEquals(0, ToolRunner.run(TestRouterAdminCLI.admin, argv));
        TestRouterAdminCLI.stateStore.loadCache(MountTableStoreImpl.class, true);
        getResponse = TestRouterAdminCLI.client.getMountTableManager().getMountTableEntries(getRequest);
        Assert.assertEquals(0, getResponse.getEntries().size());
        // remove an invalid mount table
        String invalidPath = "/invalid";
        System.setOut(new PrintStream(out));
        argv = new String[]{ "-rm", invalidPath };
        Assert.assertEquals(0, ToolRunner.run(TestRouterAdminCLI.admin, argv));
        Assert.assertTrue(out.toString().contains(("Cannot remove mount point " + invalidPath)));
        // test wrong number of arguments
        System.setErr(new PrintStream(err));
        argv = new String[]{ "-rm", src, "check" };
        ToolRunner.run(TestRouterAdminCLI.admin, argv);
        Assert.assertTrue(err.toString().contains("Too many arguments, Max=1 argument allowed"));
    }

    @Test
    public void testRemoveMountTableNotNormalized() throws Exception {
        String nsId = "ns0";
        String src = "/test-rmmounttable-notnormalized";
        String srcWithSlash = src + "/";
        String dest = "/rmmounttable-notnormalized";
        String[] argv = new String[]{ "-add", src, nsId, dest };
        Assert.assertEquals(0, ToolRunner.run(TestRouterAdminCLI.admin, argv));
        TestRouterAdminCLI.stateStore.loadCache(MountTableStoreImpl.class, true);
        GetMountTableEntriesRequest getRequest = GetMountTableEntriesRequest.newInstance(src);
        GetMountTableEntriesResponse getResponse = TestRouterAdminCLI.client.getMountTableManager().getMountTableEntries(getRequest);
        // ensure mount table added successfully
        MountTable mountTable = getResponse.getEntries().get(0);
        Assert.assertEquals(src, mountTable.getSourcePath());
        argv = new String[]{ "-rm", srcWithSlash };
        Assert.assertEquals(0, ToolRunner.run(TestRouterAdminCLI.admin, argv));
        TestRouterAdminCLI.stateStore.loadCache(MountTableStoreImpl.class, true);
        getResponse = TestRouterAdminCLI.client.getMountTableManager().getMountTableEntries(getRequest);
        Assert.assertEquals(0, getResponse.getEntries().size());
    }

    @Test
    public void testMountTableDefaultACL() throws Exception {
        String[] argv = new String[]{ "-add", "/testpath0", "ns0", "/testdir0" };
        Assert.assertEquals(0, ToolRunner.run(TestRouterAdminCLI.admin, argv));
        TestRouterAdminCLI.stateStore.loadCache(MountTableStoreImpl.class, true);
        GetMountTableEntriesRequest getRequest = GetMountTableEntriesRequest.newInstance("/testpath0");
        GetMountTableEntriesResponse getResponse = TestRouterAdminCLI.client.getMountTableManager().getMountTableEntries(getRequest);
        MountTable mountTable = getResponse.getEntries().get(0);
        UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
        String group = (ugi.getGroups().isEmpty()) ? ugi.getShortUserName() : ugi.getPrimaryGroupName();
        Assert.assertEquals(ugi.getShortUserName(), mountTable.getOwnerName());
        Assert.assertEquals(group, mountTable.getGroupName());
        Assert.assertEquals(((short) (493)), mountTable.getMode().toShort());
    }

    @Test
    public void testMountTablePermissions() throws Exception {
        // re-set system out for testing
        System.setOut(new PrintStream(out));
        // use superuser to add new mount table with only read permission
        String[] argv = new String[]{ "-add", "/testpath2-1", "ns0", "/testdir2-1", "-owner", TestRouterAdminCLI.TEST_USER, "-group", TestRouterAdminCLI.TEST_USER, "-mode", "0455" };
        Assert.assertEquals(0, ToolRunner.run(TestRouterAdminCLI.admin, argv));
        String superUser = UserGroupInformation.getCurrentUser().getShortUserName();
        // use normal user as current user to test
        UserGroupInformation remoteUser = UserGroupInformation.createRemoteUser(TestRouterAdminCLI.TEST_USER);
        UserGroupInformation.setLoginUser(remoteUser);
        // verify read permission by executing other commands
        verifyExecutionResult("/testpath2-1", true, (-1), (-1));
        // add new mount table with only write permission
        argv = new String[]{ "-add", "/testpath2-2", "ns0", "/testdir2-2", "-owner", TestRouterAdminCLI.TEST_USER, "-group", TestRouterAdminCLI.TEST_USER, "-mode", "0255" };
        Assert.assertEquals(0, ToolRunner.run(TestRouterAdminCLI.admin, argv));
        verifyExecutionResult("/testpath2-2", false, (-1), 0);
        // set mount table entry with read and write permission
        argv = new String[]{ "-add", "/testpath2-3", "ns0", "/testdir2-3", "-owner", TestRouterAdminCLI.TEST_USER, "-group", TestRouterAdminCLI.TEST_USER, "-mode", "0755" };
        Assert.assertEquals(0, ToolRunner.run(TestRouterAdminCLI.admin, argv));
        verifyExecutionResult("/testpath2-3", true, 0, 0);
        // set back login user
        remoteUser = UserGroupInformation.createRemoteUser(superUser);
        UserGroupInformation.setLoginUser(remoteUser);
    }

    @Test
    public void testInvalidArgumentMessage() throws Exception {
        String nsId = "ns0";
        String src = "/testSource";
        System.setOut(new PrintStream(out));
        String[] argv = new String[]{ "-add", src, nsId };
        Assert.assertEquals((-1), ToolRunner.run(TestRouterAdminCLI.admin, argv));
        Assert.assertTrue(out.toString().contains(("\t[-add <source> <nameservice1, nameservice2, ...> <destination> " + ("[-readonly] [-order HASH|LOCAL|RANDOM|HASH_ALL] " + "-owner <owner> -group <group> -mode <mode>]"))));
        out.reset();
        argv = new String[]{ "-update", src, nsId };
        Assert.assertEquals((-1), ToolRunner.run(TestRouterAdminCLI.admin, argv));
        Assert.assertTrue(out.toString().contains(("\t[-update <source> <nameservice1, nameservice2, ...> <destination> " + ("[-readonly] [-order HASH|LOCAL|RANDOM|HASH_ALL] " + "-owner <owner> -group <group> -mode <mode>]"))));
        out.reset();
        argv = new String[]{ "-rm" };
        Assert.assertEquals((-1), ToolRunner.run(TestRouterAdminCLI.admin, argv));
        Assert.assertTrue(out.toString().contains("\t[-rm <source>]"));
        out.reset();
        argv = new String[]{ "-setQuota", src };
        Assert.assertEquals((-1), ToolRunner.run(TestRouterAdminCLI.admin, argv));
        Assert.assertTrue(out.toString().contains(("\t[-setQuota <path> -nsQuota <nsQuota> -ssQuota " + "<quota in bytes or quota size string>]")));
        out.reset();
        argv = new String[]{ "-clrQuota" };
        Assert.assertEquals((-1), ToolRunner.run(TestRouterAdminCLI.admin, argv));
        Assert.assertTrue(out.toString().contains("\t[-clrQuota <path>]"));
        out.reset();
        argv = new String[]{ "-safemode" };
        Assert.assertEquals((-1), ToolRunner.run(TestRouterAdminCLI.admin, argv));
        Assert.assertTrue(out.toString().contains("\t[-safemode enter | leave | get]"));
        out.reset();
        argv = new String[]{ "-nameservice", nsId };
        Assert.assertEquals((-1), ToolRunner.run(TestRouterAdminCLI.admin, argv));
        Assert.assertTrue(out.toString().contains("\t[-nameservice enable | disable <nameservice>]"));
        out.reset();
        argv = new String[]{ "-Random" };
        Assert.assertEquals((-1), ToolRunner.run(TestRouterAdminCLI.admin, argv));
        String expected = "Usage: hdfs routeradmin :\n" + (((((((((((((("\t[-add <source> <nameservice1, nameservice2, ...> <destination> " + "[-readonly] [-order HASH|LOCAL|RANDOM|HASH_ALL] ") + "-owner <owner> -group <group> -mode <mode>]\n") + "\t[-update <source> <nameservice1, nameservice2, ...> ") + "<destination> ") + "[-readonly] [-order HASH|LOCAL|RANDOM|HASH_ALL] ") + "-owner <owner> -group <group> -mode <mode>]\n") + "\t[-rm <source>]\n") + "\t[-ls <path>]\n") + "\t[-setQuota <path> -nsQuota <nsQuota> -ssQuota ") + "<quota in bytes or quota size string>]\n") + "\t[-clrQuota <path>]\n") + "\t[-safemode enter | leave | get]\n") + "\t[-nameservice enable | disable <nameservice>]\n") + "\t[-getDisabledNameservices]");
        Assert.assertTrue(out.toString(), out.toString().contains(expected));
        out.reset();
    }

    @Test
    public void testSetAndClearQuota() throws Exception {
        String nsId = "ns0";
        String src = "/test-QuotaMounttable";
        String dest = "/QuotaMounttable";
        String[] argv = new String[]{ "-add", src, nsId, dest };
        Assert.assertEquals(0, ToolRunner.run(TestRouterAdminCLI.admin, argv));
        TestRouterAdminCLI.stateStore.loadCache(MountTableStoreImpl.class, true);
        GetMountTableEntriesRequest getRequest = GetMountTableEntriesRequest.newInstance(src);
        GetMountTableEntriesResponse getResponse = TestRouterAdminCLI.client.getMountTableManager().getMountTableEntries(getRequest);
        MountTable mountTable = getResponse.getEntries().get(0);
        RouterQuotaUsage quotaUsage = mountTable.getQuota();
        // verify the default quota set
        Assert.assertEquals(QUOTA_USAGE_COUNT_DEFAULT, quotaUsage.getFileAndDirectoryCount());
        Assert.assertEquals(QUOTA_RESET, quotaUsage.getQuota());
        Assert.assertEquals(QUOTA_USAGE_COUNT_DEFAULT, quotaUsage.getSpaceConsumed());
        Assert.assertEquals(QUOTA_RESET, quotaUsage.getSpaceQuota());
        long nsQuota = 50;
        long ssQuota = 100;
        argv = new String[]{ "-setQuota", src, "-nsQuota", String.valueOf(nsQuota), "-ssQuota", String.valueOf(ssQuota) };
        Assert.assertEquals(0, ToolRunner.run(TestRouterAdminCLI.admin, argv));
        TestRouterAdminCLI.stateStore.loadCache(MountTableStoreImpl.class, true);
        getResponse = TestRouterAdminCLI.client.getMountTableManager().getMountTableEntries(getRequest);
        mountTable = getResponse.getEntries().get(0);
        quotaUsage = mountTable.getQuota();
        // verify if the quota is set
        Assert.assertEquals(nsQuota, quotaUsage.getQuota());
        Assert.assertEquals(ssQuota, quotaUsage.getSpaceQuota());
        // use quota string for setting ss quota
        String newSsQuota = "2m";
        argv = new String[]{ "-setQuota", src, "-ssQuota", newSsQuota };
        Assert.assertEquals(0, ToolRunner.run(TestRouterAdminCLI.admin, argv));
        TestRouterAdminCLI.stateStore.loadCache(MountTableStoreImpl.class, true);
        getResponse = TestRouterAdminCLI.client.getMountTableManager().getMountTableEntries(getRequest);
        mountTable = getResponse.getEntries().get(0);
        quotaUsage = mountTable.getQuota();
        // verify if ns quota keeps quondam value
        Assert.assertEquals(nsQuota, quotaUsage.getQuota());
        // verify if ss quota is correctly set
        Assert.assertEquals(((2 * 1024) * 1024), quotaUsage.getSpaceQuota());
        // test clrQuota command
        argv = new String[]{ "-clrQuota", src };
        Assert.assertEquals(0, ToolRunner.run(TestRouterAdminCLI.admin, argv));
        TestRouterAdminCLI.stateStore.loadCache(MountTableStoreImpl.class, true);
        getResponse = TestRouterAdminCLI.client.getMountTableManager().getMountTableEntries(getRequest);
        mountTable = getResponse.getEntries().get(0);
        quotaUsage = mountTable.getQuota();
        // verify if quota unset successfully
        Assert.assertEquals(QUOTA_RESET, quotaUsage.getQuota());
        Assert.assertEquals(QUOTA_RESET, quotaUsage.getSpaceQuota());
        // verify wrong arguments
        System.setErr(new PrintStream(err));
        argv = new String[]{ "-clrQuota", src, "check" };
        ToolRunner.run(TestRouterAdminCLI.admin, argv);
        Assert.assertTrue(err.toString(), err.toString().contains("Too many arguments, Max=1 argument allowed"));
        argv = new String[]{ "-setQuota", src, "check", "check2" };
        err.reset();
        ToolRunner.run(TestRouterAdminCLI.admin, argv);
        Assert.assertTrue(err.toString().contains("Invalid argument : check"));
    }

    @Test
    public void testManageSafeMode() throws Exception {
        // ensure the Router become RUNNING state
        waitState(RUNNING);
        Assert.assertFalse(TestRouterAdminCLI.routerContext.getRouter().getSafemodeService().isInSafeMode());
        Assert.assertEquals(0, ToolRunner.run(TestRouterAdminCLI.admin, new String[]{ "-safemode", "enter" }));
        // verify state
        Assert.assertEquals(SAFEMODE, TestRouterAdminCLI.routerContext.getRouter().getRouterState());
        Assert.assertTrue(TestRouterAdminCLI.routerContext.getRouter().getSafemodeService().isInSafeMode());
        System.setOut(new PrintStream(out));
        System.setErr(new PrintStream(err));
        Assert.assertEquals(0, ToolRunner.run(TestRouterAdminCLI.admin, new String[]{ "-safemode", "get" }));
        Assert.assertTrue(out.toString().contains("true"));
        Assert.assertEquals(0, ToolRunner.run(TestRouterAdminCLI.admin, new String[]{ "-safemode", "leave" }));
        // verify state
        Assert.assertEquals(RUNNING, TestRouterAdminCLI.routerContext.getRouter().getRouterState());
        Assert.assertFalse(TestRouterAdminCLI.routerContext.getRouter().getSafemodeService().isInSafeMode());
        out.reset();
        Assert.assertEquals(0, ToolRunner.run(TestRouterAdminCLI.admin, new String[]{ "-safemode", "get" }));
        Assert.assertTrue(out.toString().contains("false"));
        out.reset();
        Assert.assertEquals((-1), ToolRunner.run(TestRouterAdminCLI.admin, new String[]{ "-safemode", "get", "-random", "check" }));
        Assert.assertTrue(err.toString(), err.toString().contains("safemode: Too many arguments, Max=1 argument allowed only"));
        err.reset();
        Assert.assertEquals((-1), ToolRunner.run(TestRouterAdminCLI.admin, new String[]{ "-safemode", "check" }));
        Assert.assertTrue(err.toString(), err.toString().contains("safemode: Invalid argument: check"));
        err.reset();
    }

    @Test
    public void testCreateInvalidEntry() throws Exception {
        String[] argv = new String[]{ "-add", "test-createInvalidEntry", "ns0", "/createInvalidEntry" };
        Assert.assertEquals((-1), ToolRunner.run(TestRouterAdminCLI.admin, argv));
        argv = new String[]{ "-add", "/test-createInvalidEntry", "ns0", "createInvalidEntry" };
        Assert.assertEquals((-1), ToolRunner.run(TestRouterAdminCLI.admin, argv));
        argv = new String[]{ "-add", null, "ns0", "/createInvalidEntry" };
        Assert.assertEquals((-1), ToolRunner.run(TestRouterAdminCLI.admin, argv));
        argv = new String[]{ "-add", "/test-createInvalidEntry", "ns0", null };
        Assert.assertEquals((-1), ToolRunner.run(TestRouterAdminCLI.admin, argv));
        argv = new String[]{ "-add", "", "ns0", "/createInvalidEntry" };
        Assert.assertEquals((-1), ToolRunner.run(TestRouterAdminCLI.admin, argv));
        argv = new String[]{ "-add", "/test-createInvalidEntry", null, "/createInvalidEntry" };
        Assert.assertEquals((-1), ToolRunner.run(TestRouterAdminCLI.admin, argv));
        argv = new String[]{ "-add", "/test-createInvalidEntry", "", "/createInvalidEntry" };
        Assert.assertEquals((-1), ToolRunner.run(TestRouterAdminCLI.admin, argv));
    }

    @Test
    public void testNameserviceManager() throws Exception {
        // Disable a name service and check if it's disabled
        Assert.assertEquals(0, ToolRunner.run(TestRouterAdminCLI.admin, new String[]{ "-nameservice", "disable", "ns0" }));
        TestRouterAdminCLI.stateStore.loadCache(DisabledNameserviceStoreImpl.class, true);
        System.setOut(new PrintStream(out));
        Assert.assertEquals(0, ToolRunner.run(TestRouterAdminCLI.admin, new String[]{ "-getDisabledNameservices" }));
        Assert.assertTrue(("ns0 should be reported: " + (out)), out.toString().contains("ns0"));
        // Enable a name service and check if it's there
        Assert.assertEquals(0, ToolRunner.run(TestRouterAdminCLI.admin, new String[]{ "-nameservice", "enable", "ns0" }));
        out.reset();
        TestRouterAdminCLI.stateStore.loadCache(DisabledNameserviceStoreImpl.class, true);
        Assert.assertEquals(0, ToolRunner.run(TestRouterAdminCLI.admin, new String[]{ "-getDisabledNameservices" }));
        Assert.assertFalse(("ns0 should not be reported: " + (out)), out.toString().contains("ns0"));
        // Wrong commands
        System.setErr(new PrintStream(err));
        Assert.assertEquals((-1), ToolRunner.run(TestRouterAdminCLI.admin, new String[]{ "-nameservice", "enable" }));
        String msg = "Not enough parameters specificed for cmd -nameservice";
        Assert.assertTrue(("Got error: " + (err.toString())), err.toString().startsWith(msg));
        err.reset();
        Assert.assertEquals((-1), ToolRunner.run(TestRouterAdminCLI.admin, new String[]{ "-nameservice", "wrong", "ns0" }));
        Assert.assertTrue(("Got error: " + (err.toString())), err.toString().startsWith("nameservice: Unknown command: wrong"));
        err.reset();
        ToolRunner.run(TestRouterAdminCLI.admin, new String[]{ "-nameservice", "enable", "ns0", "check" });
        Assert.assertTrue(err.toString().contains("Too many arguments, Max=2 arguments allowed"));
        err.reset();
        ToolRunner.run(TestRouterAdminCLI.admin, new String[]{ "-getDisabledNameservices", "check" });
        Assert.assertTrue(err.toString().contains("No arguments allowed"));
    }

    @Test
    public void testUpdateNonExistingMountTable() throws Exception {
        System.setOut(new PrintStream(out));
        String nsId = "ns0";
        String src = "/test-updateNonExistingMounttable";
        String dest = "/updateNonExistingMounttable";
        String[] argv = new String[]{ "-update", src, nsId, dest };
        Assert.assertEquals(0, ToolRunner.run(TestRouterAdminCLI.admin, argv));
        TestRouterAdminCLI.stateStore.loadCache(MountTableStoreImpl.class, true);
        GetMountTableEntriesRequest getRequest = GetMountTableEntriesRequest.newInstance(src);
        GetMountTableEntriesResponse getResponse = TestRouterAdminCLI.client.getMountTableManager().getMountTableEntries(getRequest);
        // Ensure the destination updated successfully
        MountTable mountTable = getResponse.getEntries().get(0);
        Assert.assertEquals(src, mountTable.getSourcePath());
        Assert.assertEquals(nsId, mountTable.getDestinations().get(0).getNameserviceId());
        Assert.assertEquals(dest, mountTable.getDestinations().get(0).getDest());
    }

    @Test
    public void testUpdateDestinationForExistingMountTable() throws Exception {
        // Add a mount table firstly
        String nsId = "ns0";
        String src = "/test-updateDestinationForExistingMountTable";
        String dest = "/UpdateDestinationForExistingMountTable";
        String[] argv = new String[]{ "-add", src, nsId, dest };
        Assert.assertEquals(0, ToolRunner.run(TestRouterAdminCLI.admin, argv));
        TestRouterAdminCLI.stateStore.loadCache(MountTableStoreImpl.class, true);
        GetMountTableEntriesRequest getRequest = GetMountTableEntriesRequest.newInstance(src);
        GetMountTableEntriesResponse getResponse = TestRouterAdminCLI.client.getMountTableManager().getMountTableEntries(getRequest);
        // Ensure mount table added successfully
        MountTable mountTable = getResponse.getEntries().get(0);
        Assert.assertEquals(src, mountTable.getSourcePath());
        Assert.assertEquals(nsId, mountTable.getDestinations().get(0).getNameserviceId());
        Assert.assertEquals(dest, mountTable.getDestinations().get(0).getDest());
        // Update the destination
        String newNsId = "ns1";
        String newDest = "/newDestination";
        argv = new String[]{ "-update", src, newNsId, newDest };
        Assert.assertEquals(0, ToolRunner.run(TestRouterAdminCLI.admin, argv));
        TestRouterAdminCLI.stateStore.loadCache(MountTableStoreImpl.class, true);
        getResponse = TestRouterAdminCLI.client.getMountTableManager().getMountTableEntries(getRequest);
        // Ensure the destination updated successfully
        mountTable = getResponse.getEntries().get(0);
        Assert.assertEquals(src, mountTable.getSourcePath());
        Assert.assertEquals(newNsId, mountTable.getDestinations().get(0).getNameserviceId());
        Assert.assertEquals(newDest, mountTable.getDestinations().get(0).getDest());
    }

    @Test
    public void testUpdateDestinationForExistingMountTableNotNormalized() throws Exception {
        // Add a mount table firstly
        String nsId = "ns0";
        String src = "/test-updateDestinationForExistingMountTableNotNormalized";
        String srcWithSlash = src + "/";
        String dest = "/UpdateDestinationForExistingMountTableNotNormalized";
        String[] argv = new String[]{ "-add", src, nsId, dest };
        Assert.assertEquals(0, ToolRunner.run(TestRouterAdminCLI.admin, argv));
        TestRouterAdminCLI.stateStore.loadCache(MountTableStoreImpl.class, true);
        GetMountTableEntriesRequest getRequest = GetMountTableEntriesRequest.newInstance(src);
        GetMountTableEntriesResponse getResponse = TestRouterAdminCLI.client.getMountTableManager().getMountTableEntries(getRequest);
        // Ensure mount table added successfully
        MountTable mountTable = getResponse.getEntries().get(0);
        Assert.assertEquals(src, mountTable.getSourcePath());
        Assert.assertEquals(nsId, mountTable.getDestinations().get(0).getNameserviceId());
        Assert.assertEquals(dest, mountTable.getDestinations().get(0).getDest());
        // Update the destination
        String newNsId = "ns1";
        String newDest = "/newDestination";
        argv = new String[]{ "-update", srcWithSlash, newNsId, newDest };
        Assert.assertEquals(0, ToolRunner.run(TestRouterAdminCLI.admin, argv));
        TestRouterAdminCLI.stateStore.loadCache(MountTableStoreImpl.class, true);
        getResponse = TestRouterAdminCLI.client.getMountTableManager().getMountTableEntries(getRequest);
        // Ensure the destination updated successfully
        mountTable = getResponse.getEntries().get(0);
        Assert.assertEquals(src, mountTable.getSourcePath());
        Assert.assertEquals(newNsId, mountTable.getDestinations().get(0).getNameserviceId());
        Assert.assertEquals(newDest, mountTable.getDestinations().get(0).getDest());
    }

    @Test
    public void testUpdateReadonlyUserGroupPermissionMountable() throws Exception {
        // Add a mount table
        String nsId = "ns0";
        String src = "/test-updateReadonlyUserGroupPermissionMountTable";
        String dest = "/UpdateReadonlyUserGroupPermissionMountTable";
        String[] argv = new String[]{ "-add", src, nsId, dest };
        Assert.assertEquals(0, ToolRunner.run(TestRouterAdminCLI.admin, argv));
        TestRouterAdminCLI.stateStore.loadCache(MountTableStoreImpl.class, true);
        GetMountTableEntriesRequest getRequest = GetMountTableEntriesRequest.newInstance(src);
        GetMountTableEntriesResponse getResponse = TestRouterAdminCLI.client.getMountTableManager().getMountTableEntries(getRequest);
        // Ensure mount table added successfully
        MountTable mountTable = getResponse.getEntries().get(0);
        Assert.assertEquals(src, mountTable.getSourcePath());
        Assert.assertEquals(nsId, mountTable.getDestinations().get(0).getNameserviceId());
        Assert.assertEquals(dest, mountTable.getDestinations().get(0).getDest());
        Assert.assertFalse(mountTable.isReadOnly());
        // Update the readonly, owner, group and permission
        String testOwner = "test_owner";
        String testGroup = "test_group";
        argv = new String[]{ "-update", src, nsId, dest, "-readonly", "-owner", testOwner, "-group", testGroup, "-mode", "0455" };
        Assert.assertEquals(0, ToolRunner.run(TestRouterAdminCLI.admin, argv));
        TestRouterAdminCLI.stateStore.loadCache(MountTableStoreImpl.class, true);
        getResponse = TestRouterAdminCLI.client.getMountTableManager().getMountTableEntries(getRequest);
        // Ensure the destination updated successfully
        mountTable = getResponse.getEntries().get(0);
        Assert.assertEquals(src, mountTable.getSourcePath());
        Assert.assertEquals(nsId, mountTable.getDestinations().get(0).getNameserviceId());
        Assert.assertEquals(dest, mountTable.getDestinations().get(0).getDest());
        Assert.assertTrue(mountTable.isReadOnly());
        Assert.assertEquals(testOwner, mountTable.getOwnerName());
        Assert.assertEquals(testGroup, mountTable.getGroupName());
        Assert.assertEquals(((short) (301)), mountTable.getMode().toShort());
    }

    @Test
    public void testUpdateOrderMountTable() throws Exception {
        testUpdateOrderMountTable(HASH);
        testUpdateOrderMountTable(LOCAL);
        testUpdateOrderMountTable(RANDOM);
        testUpdateOrderMountTable(HASH_ALL);
    }

    @Test
    public void testOrderErrorMsg() throws Exception {
        String nsId = "ns0";
        DestinationOrder order = DestinationOrder.HASH;
        String src = "/testod" + (order.toString());
        String dest = "/testUpd";
        String[] argv = new String[]{ "-add", src, nsId, dest };
        Assert.assertEquals(0, ToolRunner.run(TestRouterAdminCLI.admin, argv));
        TestRouterAdminCLI.stateStore.loadCache(MountTableStoreImpl.class, true);
        GetMountTableEntriesRequest getRequest = GetMountTableEntriesRequest.newInstance(src);
        GetMountTableEntriesResponse getResponse = TestRouterAdminCLI.client.getMountTableManager().getMountTableEntries(getRequest);
        // Ensure mount table added successfully
        MountTable mountTable = getResponse.getEntries().get(0);
        Assert.assertEquals(src, mountTable.getSourcePath());
        Assert.assertEquals(nsId, mountTable.getDestinations().get(0).getNameserviceId());
        Assert.assertEquals(dest, mountTable.getDestinations().get(0).getDest());
        Assert.assertEquals(HASH, mountTable.getDestOrder());
        argv = new String[]{ "-update", src, nsId, dest, "-order", order.toString() };
        Assert.assertEquals(0, ToolRunner.run(TestRouterAdminCLI.admin, argv));
        // Update the order with wrong command
        argv = new String[]{ "-update", src + "a", nsId, dest + "a", "-orde", order.toString() };
        Assert.assertEquals((-1), ToolRunner.run(TestRouterAdminCLI.admin, argv));
        // Update without order argument
        argv = new String[]{ "-update", src, nsId, dest, order.toString() };
        Assert.assertEquals((-1), ToolRunner.run(TestRouterAdminCLI.admin, argv));
    }
}

