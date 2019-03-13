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


import CreateFlag.APPEND;
import CreateFlag.CREATE;
import DFSConfigKeys.DFS_BLOCK_SIZE_DEFAULT;
import DatanodeReportType.ALL;
import ErasureCodingPolicyState.DISABLED;
import HdfsFileStatus.EMPTY_NAME;
import STATE.INITED;
import STATE.STARTED;
import STATE.STOPPED;
import SafeModeAction.SAFEMODE_ENTER;
import SafeModeAction.SAFEMODE_GET;
import SafeModeAction.SAFEMODE_LEAVE;
import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.crypto.CryptoProtocolVersion;
import org.apache.hadoop.fs.CreateFlag;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.client.HdfsDataOutputStream;
import org.apache.hadoop.hdfs.protocol.AddErasureCodingPolicyResponse;
import org.apache.hadoop.hdfs.protocol.BlockStoragePolicy;
import org.apache.hadoop.hdfs.protocol.ClientProtocol;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.hdfs.protocol.DirectoryListing;
import org.apache.hadoop.hdfs.protocol.ECBlockGroupStats;
import org.apache.hadoop.hdfs.protocol.ErasureCodingPolicy;
import org.apache.hadoop.hdfs.protocol.ErasureCodingPolicyInfo;
import org.apache.hadoop.hdfs.protocol.HdfsFileStatus;
import org.apache.hadoop.hdfs.protocol.LocatedBlock;
import org.apache.hadoop.hdfs.protocol.LocatedBlocks;
import org.apache.hadoop.hdfs.security.token.block.ExportedBlockKeys;
import org.apache.hadoop.hdfs.server.federation.FederationTestUtils;
import org.apache.hadoop.hdfs.server.federation.MiniRouterDFSCluster;
import org.apache.hadoop.hdfs.server.federation.MockResolver;
import org.apache.hadoop.hdfs.server.federation.metrics.NamenodeBeanMetrics;
import org.apache.hadoop.hdfs.server.federation.resolver.FileSubclusterResolver;
import org.apache.hadoop.hdfs.server.protocol.BlocksWithLocations;
import org.apache.hadoop.hdfs.server.protocol.BlocksWithLocations.BlockWithLocations;
import org.apache.hadoop.hdfs.server.protocol.DatanodeStorageReport;
import org.apache.hadoop.hdfs.server.protocol.NamenodeProtocol;
import org.apache.hadoop.hdfs.server.protocol.NamespaceInfo;
import org.apache.hadoop.io.EnumSetWritable;
import org.apache.hadoop.io.erasurecode.ECSchema;
import org.apache.hadoop.io.erasurecode.ErasureCodeConstants;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.test.GenericTestUtils;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The the RPC interface of the {@link Router} implemented by
 * {@link RouterRpcServer}.
 */
public class TestRouterRpc {
    private static final Logger LOG = LoggerFactory.getLogger(TestRouterRpc.class);

    private static final Comparator<ErasureCodingPolicyInfo> EC_POLICY_CMP = new Comparator<ErasureCodingPolicyInfo>() {
        public int compare(ErasureCodingPolicyInfo ec0, ErasureCodingPolicyInfo ec1) {
            String name0 = ec0.getPolicy().getName();
            String name1 = ec1.getPolicy().getName();
            return name0.compareTo(name1);
        }
    };

    /**
     * Federated HDFS cluster.
     */
    private static MiniRouterDFSCluster cluster;

    /**
     * Random Router for this federated cluster.
     */
    private MiniRouterDFSCluster.RouterContext router;

    /**
     * Random nameservice in the federated cluster.
     */
    private String ns;

    /**
     * First namenode in the nameservice.
     */
    private MiniRouterDFSCluster.NamenodeContext namenode;

    /**
     * Client interface to the Router.
     */
    private ClientProtocol routerProtocol;

    /**
     * Client interface to the Namenode.
     */
    private ClientProtocol nnProtocol;

    /**
     * NameNodeProtocol interface to the Router.
     */
    private NamenodeProtocol routerNamenodeProtocol;

    /**
     * NameNodeProtocol interface to the Namenode.
     */
    private NamenodeProtocol nnNamenodeProtocol;

    /**
     * Filesystem interface to the Router.
     */
    private FileSystem routerFS;

    /**
     * Filesystem interface to the Namenode.
     */
    private FileSystem nnFS;

    /**
     * File in the Router.
     */
    private String routerFile;

    /**
     * File in the Namenode.
     */
    private String nnFile;

    @Test
    public void testRpcService() throws IOException {
        Router testRouter = new Router();
        List<String> nss = TestRouterRpc.cluster.getNameservices();
        String ns0 = nss.get(0);
        Configuration routerConfig = TestRouterRpc.cluster.generateRouterConfiguration(ns0, null);
        RouterRpcServer server = new RouterRpcServer(routerConfig, testRouter, testRouter.getNamenodeResolver(), testRouter.getSubclusterResolver());
        server.init(routerConfig);
        Assert.assertEquals(INITED, server.getServiceState());
        server.start();
        Assert.assertEquals(STARTED, server.getServiceState());
        server.stop();
        Assert.assertEquals(STOPPED, server.getServiceState());
        server.close();
        testRouter.close();
    }

    @Test
    public void testProxyListFiles() throws IOException, InterruptedException, NoSuchMethodException, SecurityException, URISyntaxException {
        // Verify that the root listing is a union of the mount table destinations
        // and the files stored at all nameservices mounted at the root (ns0 + ns1)
        // 
        // / -->
        // /ns0 (from mount table)
        // /ns1 (from mount table)
        // all items in / of ns0 (default NS)
        // Collect the mount table entries from the root mount point
        Set<String> requiredPaths = new TreeSet<>();
        FileSubclusterResolver fileResolver = router.getRouter().getSubclusterResolver();
        for (String mount : fileResolver.getMountPoints("/")) {
            requiredPaths.add(mount);
        }
        // Collect all files/dirs on the root path of the default NS
        String defaultNs = TestRouterRpc.cluster.getNameservices().get(0);
        MiniRouterDFSCluster.NamenodeContext nn = TestRouterRpc.cluster.getNamenode(defaultNs, null);
        FileStatus[] iterator = nn.getFileSystem().listStatus(new Path("/"));
        for (FileStatus file : iterator) {
            requiredPaths.add(file.getPath().getName());
        }
        // Fetch listing
        DirectoryListing listing = routerProtocol.getListing("/", EMPTY_NAME, false);
        Iterator<String> requiredPathsIterator = requiredPaths.iterator();
        // Match each path returned and verify order returned
        for (HdfsFileStatus f : listing.getPartialListing()) {
            String fileName = requiredPathsIterator.next();
            String currentFile = f.getFullPath(new Path("/")).getName();
            Assert.assertEquals(currentFile, fileName);
        }
        // Verify the total number of results found/matched
        Assert.assertEquals(requiredPaths.size(), listing.getPartialListing().length);
        // List a path that doesn't exist and validate error response with NN
        // behavior.
        Method m = ClientProtocol.class.getMethod("getListing", String.class, byte[].class, boolean.class);
        String badPath = "/unknownlocation/unknowndir";
        TestRouterRpc.compareResponses(routerProtocol, nnProtocol, m, new Object[]{ badPath, HdfsFileStatus.EMPTY_NAME, false });
    }

    @Test
    public void testProxyListFilesWithConflict() throws IOException, InterruptedException {
        // Add a directory to the namespace that conflicts with a mount point
        MiniRouterDFSCluster.NamenodeContext nn = TestRouterRpc.cluster.getNamenode(ns, null);
        FileSystem nnFs = nn.getFileSystem();
        FederationTestUtils.addDirectory(nnFs, TestRouterRpc.cluster.getFederatedTestDirectoryForNS(ns));
        FileSystem routerFs = router.getFileSystem();
        int initialCount = FederationTestUtils.countContents(routerFs, "/");
        // Root file system now for NS X:
        // / ->
        // /ns0 (mount table)
        // /ns1 (mount table)
        // /target-ns0 (the target folder for the NS0 mapped to /
        // /nsX (local directory that duplicates mount table)
        int newCount = FederationTestUtils.countContents(routerFs, "/");
        Assert.assertEquals(initialCount, newCount);
        // Verify that each root path is readable and contains one test directory
        Assert.assertEquals(1, FederationTestUtils.countContents(routerFs, TestRouterRpc.cluster.getFederatedPathForNS(ns)));
        // Verify that real folder for the ns contains a single test directory
        Assert.assertEquals(1, FederationTestUtils.countContents(nnFs, TestRouterRpc.cluster.getNamenodePathForNS(ns)));
    }

    @Test
    public void testProxyRenameFiles() throws IOException, InterruptedException {
        Thread.sleep(5000);
        List<String> nss = TestRouterRpc.cluster.getNameservices();
        String ns0 = nss.get(0);
        String ns1 = nss.get(1);
        // Rename within the same namespace
        // /ns0/testdir/testrename -> /ns0/testdir/testrename-append
        String filename = (TestRouterRpc.cluster.getFederatedTestDirectoryForNS(ns0)) + "/testrename";
        String renamedFile = filename + "-append";
        testRename(router, filename, renamedFile, false);
        testRename2(router, filename, renamedFile, false);
        // Rename a file to a destination that is in a different namespace (fails)
        filename = (TestRouterRpc.cluster.getFederatedTestDirectoryForNS(ns0)) + "/testrename";
        renamedFile = (TestRouterRpc.cluster.getFederatedTestDirectoryForNS(ns1)) + "/testrename";
        testRename(router, filename, renamedFile, true);
        testRename2(router, filename, renamedFile, true);
    }

    @Test
    public void testProxyChownFiles() throws Exception {
        String newUsername = "TestUser";
        String newGroup = "TestGroup";
        // change owner
        routerProtocol.setOwner(routerFile, newUsername, newGroup);
        // Verify with NN
        FileStatus file = FederationTestUtils.getFileStatus(namenode.getFileSystem(), nnFile);
        Assert.assertEquals(file.getOwner(), newUsername);
        Assert.assertEquals(file.getGroup(), newGroup);
        // Bad request and validate router response matches NN response.
        Method m = ClientProtocol.class.getMethod("setOwner", String.class, String.class, String.class);
        String badPath = "/unknownlocation/unknowndir";
        TestRouterRpc.compareResponses(routerProtocol, nnProtocol, m, new Object[]{ badPath, newUsername, newGroup });
    }

    @Test
    public void testProxyGetStats() throws Exception {
        // Some of the statistics are out of sync because of the mini cluster
        Supplier<Boolean> check = new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                try {
                    long[] combinedData = routerProtocol.getStats();
                    long[] individualData = getAggregateStats();
                    int len = Math.min(combinedData.length, individualData.length);
                    for (int i = 0; i < len; i++) {
                        if ((combinedData[i]) != (individualData[i])) {
                            TestRouterRpc.LOG.error("Stats for {} don't match: {} != {}", i, combinedData[i], individualData[i]);
                            return false;
                        }
                    }
                    return true;
                } catch (Exception e) {
                    TestRouterRpc.LOG.error("Cannot get stats: {}", e.getMessage());
                    return false;
                }
            }
        };
        GenericTestUtils.waitFor(check, 500, (5 * 1000));
    }

    @Test
    public void testProxyGetDatanodeReport() throws Exception {
        DatanodeInfo[] combinedData = routerProtocol.getDatanodeReport(ALL);
        Set<Integer> individualData = new HashSet<Integer>();
        for (String nameservice : TestRouterRpc.cluster.getNameservices()) {
            MiniRouterDFSCluster.NamenodeContext n = TestRouterRpc.cluster.getNamenode(nameservice, null);
            DFSClient client = n.getClient();
            ClientProtocol clientProtocol = client.getNamenode();
            DatanodeInfo[] data = clientProtocol.getDatanodeReport(ALL);
            for (int i = 0; i < (data.length); i++) {
                // Collect unique DNs based on their xfer port
                DatanodeInfo info = data[i];
                individualData.add(info.getXferPort());
            }
        }
        Assert.assertEquals(combinedData.length, individualData.size());
    }

    @Test
    public void testProxyGetDatanodeStorageReport() throws IOException, InterruptedException, URISyntaxException {
        DatanodeStorageReport[] combinedData = routerProtocol.getDatanodeStorageReport(ALL);
        Set<String> individualData = new HashSet<>();
        for (String nameservice : TestRouterRpc.cluster.getNameservices()) {
            MiniRouterDFSCluster.NamenodeContext n = TestRouterRpc.cluster.getNamenode(nameservice, null);
            DFSClient client = n.getClient();
            ClientProtocol clientProtocol = client.getNamenode();
            DatanodeStorageReport[] data = clientProtocol.getDatanodeStorageReport(ALL);
            for (DatanodeStorageReport report : data) {
                // Determine unique DN instances
                DatanodeInfo dn = report.getDatanodeInfo();
                individualData.add(dn.toString());
            }
        }
        Assert.assertEquals(combinedData.length, individualData.size());
    }

    @Test
    public void testProxyMkdir() throws Exception {
        // Check the initial folders
        FileStatus[] filesInitial = routerFS.listStatus(new Path("/"));
        // Create a directory via the router at the root level
        String dirPath = "/testdir";
        FsPermission permission = new FsPermission("705");
        routerProtocol.mkdirs(dirPath, permission, false);
        // Verify the root listing has the item via the router
        FileStatus[] files = routerFS.listStatus(new Path("/"));
        Assert.assertEquals((((((Arrays.toString(files)) + " should be ") + (Arrays.toString(filesInitial))) + " + ") + dirPath), ((filesInitial.length) + 1), files.length);
        Assert.assertTrue(FederationTestUtils.verifyFileExists(routerFS, dirPath));
        // Verify the directory is present in only 1 Namenode
        int foundCount = 0;
        for (MiniRouterDFSCluster.NamenodeContext n : TestRouterRpc.cluster.getNamenodes()) {
            if (FederationTestUtils.verifyFileExists(n.getFileSystem(), dirPath)) {
                foundCount++;
            }
        }
        Assert.assertEquals(1, foundCount);
        Assert.assertTrue(FederationTestUtils.deleteFile(routerFS, dirPath));
        // Validate router failure response matches NN failure response.
        Method m = ClientProtocol.class.getMethod("mkdirs", String.class, FsPermission.class, boolean.class);
        String badPath = "/unknownlocation/unknowndir";
        TestRouterRpc.compareResponses(routerProtocol, nnProtocol, m, new Object[]{ badPath, permission, false });
    }

    @Test
    public void testProxyChmodFiles() throws Exception {
        FsPermission permission = new FsPermission("444");
        // change permissions
        routerProtocol.setPermission(routerFile, permission);
        // Validate permissions NN
        FileStatus file = FederationTestUtils.getFileStatus(namenode.getFileSystem(), nnFile);
        Assert.assertEquals(permission, file.getPermission());
        // Validate router failure response matches NN failure response.
        Method m = ClientProtocol.class.getMethod("setPermission", String.class, FsPermission.class);
        String badPath = "/unknownlocation/unknowndir";
        TestRouterRpc.compareResponses(routerProtocol, nnProtocol, m, new Object[]{ badPath, permission });
    }

    @Test
    public void testProxySetReplication() throws Exception {
        // Check current replication via NN
        FileStatus file = FederationTestUtils.getFileStatus(nnFS, nnFile);
        Assert.assertEquals(1, file.getReplication());
        // increment replication via router
        routerProtocol.setReplication(routerFile, ((short) (2)));
        // Verify via NN
        file = FederationTestUtils.getFileStatus(nnFS, nnFile);
        Assert.assertEquals(2, file.getReplication());
        // Validate router failure response matches NN failure response.
        Method m = ClientProtocol.class.getMethod("setReplication", String.class, short.class);
        String badPath = "/unknownlocation/unknowndir";
        TestRouterRpc.compareResponses(routerProtocol, nnProtocol, m, new Object[]{ badPath, ((short) (2)) });
    }

    @Test
    public void testProxyTruncateFile() throws Exception {
        // Check file size via NN
        FileStatus file = FederationTestUtils.getFileStatus(nnFS, nnFile);
        Assert.assertTrue(((file.getLen()) > 0));
        // Truncate to 0 bytes via router
        routerProtocol.truncate(routerFile, 0, "testclient");
        // Verify via NN
        file = FederationTestUtils.getFileStatus(nnFS, nnFile);
        Assert.assertEquals(0, file.getLen());
        // Validate router failure response matches NN failure response.
        Method m = ClientProtocol.class.getMethod("truncate", String.class, long.class, String.class);
        String badPath = "/unknownlocation/unknowndir";
        TestRouterRpc.compareResponses(routerProtocol, nnProtocol, m, new Object[]{ badPath, ((long) (0)), "testclient" });
    }

    @Test
    public void testProxyGetBlockLocations() throws Exception {
        // Fetch block locations via router
        LocatedBlocks locations = routerProtocol.getBlockLocations(routerFile, 0, 1024);
        Assert.assertEquals(1, locations.getLocatedBlocks().size());
        // Validate router failure response matches NN failure response.
        Method m = ClientProtocol.class.getMethod("getBlockLocations", String.class, long.class, long.class);
        String badPath = "/unknownlocation/unknowndir";
        TestRouterRpc.compareResponses(routerProtocol, nnProtocol, m, new Object[]{ badPath, ((long) (0)), ((long) (0)) });
    }

    @Test
    public void testProxyStoragePolicy() throws Exception {
        // Query initial policy via NN
        HdfsFileStatus status = namenode.getClient().getFileInfo(nnFile);
        // Set a random policy via router
        BlockStoragePolicy[] policies = namenode.getClient().getStoragePolicies();
        BlockStoragePolicy policy = policies[0];
        while (policy.isCopyOnCreateFile()) {
            // Pick a non copy on create policy
            Random rand = new Random();
            int randIndex = rand.nextInt(policies.length);
            policy = policies[randIndex];
        } 
        routerProtocol.setStoragePolicy(routerFile, policy.getName());
        // Verify policy via NN
        HdfsFileStatus newStatus = namenode.getClient().getFileInfo(nnFile);
        Assert.assertTrue(((newStatus.getStoragePolicy()) == (policy.getId())));
        Assert.assertTrue(((newStatus.getStoragePolicy()) != (status.getStoragePolicy())));
        // Validate router failure response matches NN failure response.
        Method m = ClientProtocol.class.getMethod("setStoragePolicy", String.class, String.class);
        String badPath = "/unknownlocation/unknowndir";
        TestRouterRpc.compareResponses(routerProtocol, nnProtocol, m, new Object[]{ badPath, "badpolicy" });
    }

    @Test
    public void testProxyGetPreferedBlockSize() throws Exception {
        // Query via NN and Router and verify
        long namenodeSize = nnProtocol.getPreferredBlockSize(nnFile);
        long routerSize = routerProtocol.getPreferredBlockSize(routerFile);
        Assert.assertEquals(routerSize, namenodeSize);
        // Validate router failure response matches NN failure response.
        Method m = ClientProtocol.class.getMethod("getPreferredBlockSize", String.class);
        String badPath = "/unknownlocation/unknowndir";
        TestRouterRpc.compareResponses(routerProtocol, nnProtocol, m, new Object[]{ badPath });
    }

    @Test
    public void testProxyConcatFile() throws Exception {
        // Create a stub file in the primary ns
        String sameNameservice = ns;
        String existingFile = (TestRouterRpc.cluster.getFederatedTestDirectoryForNS(sameNameservice)) + "_concatfile";
        int existingFileSize = 32;
        FederationTestUtils.createFile(routerFS, existingFile, existingFileSize);
        // Identify an alternate nameservice that doesn't match the existing file
        String alternateNameservice = null;
        for (String n : TestRouterRpc.cluster.getNameservices()) {
            if (!(n.equals(sameNameservice))) {
                alternateNameservice = n;
                break;
            }
        }
        // Create new files, must be a full block to use concat. One file is in the
        // same namespace as the target file, the other is in a different namespace.
        String altRouterFile = (TestRouterRpc.cluster.getFederatedTestDirectoryForNS(alternateNameservice)) + "_newfile";
        String sameRouterFile = (TestRouterRpc.cluster.getFederatedTestDirectoryForNS(sameNameservice)) + "_newfile";
        FederationTestUtils.createFile(routerFS, altRouterFile, DFS_BLOCK_SIZE_DEFAULT);
        FederationTestUtils.createFile(routerFS, sameRouterFile, DFS_BLOCK_SIZE_DEFAULT);
        // Concat in different namespaces, fails
        testConcat(existingFile, altRouterFile, true);
        // Concat in same namespaces, succeeds
        testConcat(existingFile, sameRouterFile, false);
        // Check target file length
        FileStatus status = FederationTestUtils.getFileStatus(routerFS, sameRouterFile);
        Assert.assertEquals((existingFileSize + (DFSConfigKeys.DFS_BLOCK_SIZE_DEFAULT)), status.getLen());
        // Validate router failure response matches NN failure response.
        Method m = ClientProtocol.class.getMethod("concat", String.class, String[].class);
        String badPath = "/unknownlocation/unknowndir";
        TestRouterRpc.compareResponses(routerProtocol, nnProtocol, m, new Object[]{ badPath, new String[]{ routerFile } });
    }

    @Test
    public void testProxyAppend() throws Exception {
        // Append a test string via router
        EnumSet<CreateFlag> createFlag = EnumSet.of(APPEND);
        DFSClient routerClient = getRouterContext().getClient();
        HdfsDataOutputStream stream = routerClient.append(routerFile, 1024, createFlag, null, null);
        stream.writeBytes(MiniRouterDFSCluster.TEST_STRING);
        stream.close();
        // Verify file size via NN
        FileStatus status = FederationTestUtils.getFileStatus(nnFS, nnFile);
        Assert.assertTrue(((status.getLen()) > (MiniRouterDFSCluster.TEST_STRING.length())));
        // Validate router failure response matches NN failure response.
        Method m = ClientProtocol.class.getMethod("append", String.class, String.class, EnumSetWritable.class);
        String badPath = "/unknownlocation/unknowndir";
        EnumSetWritable<CreateFlag> createFlagWritable = new EnumSetWritable<CreateFlag>(createFlag);
        TestRouterRpc.compareResponses(routerProtocol, nnProtocol, m, new Object[]{ badPath, "testClient", createFlagWritable });
    }

    @Test
    public void testProxyGetAdditionalDatanode() throws IOException, InterruptedException, URISyntaxException {
        // Use primitive APIs to open a file, add a block, and get datanode location
        EnumSet<CreateFlag> createFlag = EnumSet.of(CREATE);
        String clientName = getRouterContext().getClient().getClientName();
        String newRouterFile = (routerFile) + "_additionalDatanode";
        HdfsFileStatus status = routerProtocol.create(newRouterFile, new FsPermission("777"), clientName, new EnumSetWritable<CreateFlag>(createFlag), true, ((short) (1)), ((long) (1024)), CryptoProtocolVersion.supported(), null, null);
        // Add a block via router (requires client to have same lease)
        LocatedBlock block = routerProtocol.addBlock(newRouterFile, clientName, null, null, status.getFileId(), null, null);
        DatanodeInfo[] exclusions = new DatanodeInfo[0];
        LocatedBlock newBlock = routerProtocol.getAdditionalDatanode(newRouterFile, status.getFileId(), block.getBlock(), block.getLocations(), block.getStorageIDs(), exclusions, 1, clientName);
        Assert.assertNotNull(newBlock);
    }

    @Test
    public void testProxyCreateFileAlternateUser() throws IOException, InterruptedException, URISyntaxException {
        // Create via Router
        String routerDir = TestRouterRpc.cluster.getFederatedTestDirectoryForNS(ns);
        String namenodeDir = TestRouterRpc.cluster.getNamenodeTestDirectoryForNS(ns);
        String newRouterFile = routerDir + "/unknownuser";
        String newNamenodeFile = namenodeDir + "/unknownuser";
        String username = "unknownuser";
        // Allow all user access to dir
        namenode.getFileContext().setPermission(new Path(namenodeDir), new FsPermission("777"));
        UserGroupInformation ugi = UserGroupInformation.createRemoteUser(username);
        DFSClient client = getRouterContext().getClient(ugi);
        client.create(newRouterFile, true);
        // Fetch via NN and check user
        FileStatus status = FederationTestUtils.getFileStatus(nnFS, newNamenodeFile);
        Assert.assertEquals(status.getOwner(), username);
    }

    @Test
    public void testProxyGetFileInfoAcessException() throws IOException {
        UserGroupInformation ugi = UserGroupInformation.createRemoteUser("unknownuser");
        // List files from the NN and trap the exception
        Exception nnFailure = null;
        try {
            String testFile = TestRouterRpc.cluster.getNamenodeTestFileForNS(ns);
            namenode.getClient(ugi).getLocatedBlocks(testFile, 0);
        } catch (Exception e) {
            nnFailure = e;
        }
        Assert.assertNotNull(nnFailure);
        // List files from the router and trap the exception
        Exception routerFailure = null;
        try {
            String testFile = TestRouterRpc.cluster.getFederatedTestFileForNS(ns);
            getRouterContext().getClient(ugi).getLocatedBlocks(testFile, 0);
        } catch (Exception e) {
            routerFailure = e;
        }
        Assert.assertNotNull(routerFailure);
        Assert.assertEquals(routerFailure.getClass(), nnFailure.getClass());
    }

    @Test
    public void testProxyVersionRequest() throws Exception {
        NamespaceInfo rVersion = routerNamenodeProtocol.versionRequest();
        NamespaceInfo nnVersion = nnNamenodeProtocol.versionRequest();
        Assert.assertEquals(nnVersion.getBlockPoolID(), rVersion.getBlockPoolID());
        Assert.assertEquals(nnVersion.getNamespaceID(), rVersion.getNamespaceID());
        Assert.assertEquals(nnVersion.getClusterID(), rVersion.getClusterID());
        Assert.assertEquals(nnVersion.getLayoutVersion(), rVersion.getLayoutVersion());
        Assert.assertEquals(nnVersion.getCTime(), rVersion.getCTime());
    }

    @Test
    public void testProxyGetBlockKeys() throws Exception {
        ExportedBlockKeys rKeys = routerNamenodeProtocol.getBlockKeys();
        ExportedBlockKeys nnKeys = nnNamenodeProtocol.getBlockKeys();
        Assert.assertEquals(nnKeys.getCurrentKey(), rKeys.getCurrentKey());
        Assert.assertEquals(nnKeys.getKeyUpdateInterval(), rKeys.getKeyUpdateInterval());
        Assert.assertEquals(nnKeys.getTokenLifetime(), rKeys.getTokenLifetime());
    }

    @Test
    public void testProxyGetBlocks() throws Exception {
        // Get datanodes
        DatanodeInfo[] dns = routerProtocol.getDatanodeReport(ALL);
        DatanodeInfo dn0 = dns[0];
        // Verify that checking that datanode works
        BlocksWithLocations routerBlockLocations = routerNamenodeProtocol.getBlocks(dn0, 1024, 0);
        BlocksWithLocations nnBlockLocations = nnNamenodeProtocol.getBlocks(dn0, 1024, 0);
        BlockWithLocations[] routerBlocks = routerBlockLocations.getBlocks();
        BlockWithLocations[] nnBlocks = nnBlockLocations.getBlocks();
        Assert.assertEquals(nnBlocks.length, routerBlocks.length);
        for (int i = 0; i < (routerBlocks.length); i++) {
            Assert.assertEquals(nnBlocks[i].getBlock().getBlockId(), routerBlocks[i].getBlock().getBlockId());
        }
    }

    @Test
    public void testProxyGetTransactionID() throws IOException {
        long routerTransactionID = routerNamenodeProtocol.getTransactionID();
        long nnTransactionID = nnNamenodeProtocol.getTransactionID();
        Assert.assertEquals(nnTransactionID, routerTransactionID);
    }

    @Test
    public void testProxyGetMostRecentCheckpointTxId() throws IOException {
        long routerCheckPointId = routerNamenodeProtocol.getMostRecentCheckpointTxId();
        long nnCheckPointId = nnNamenodeProtocol.getMostRecentCheckpointTxId();
        Assert.assertEquals(nnCheckPointId, routerCheckPointId);
    }

    @Test
    public void testProxySetSafemode() throws Exception {
        boolean routerSafemode = routerProtocol.setSafeMode(SAFEMODE_GET, false);
        boolean nnSafemode = nnProtocol.setSafeMode(SAFEMODE_GET, false);
        Assert.assertEquals(nnSafemode, routerSafemode);
        routerSafemode = routerProtocol.setSafeMode(SAFEMODE_GET, true);
        nnSafemode = nnProtocol.setSafeMode(SAFEMODE_GET, true);
        Assert.assertEquals(nnSafemode, routerSafemode);
        Assert.assertFalse(routerProtocol.setSafeMode(SAFEMODE_GET, false));
        Assert.assertTrue(routerProtocol.setSafeMode(SAFEMODE_ENTER, false));
        Assert.assertTrue(routerProtocol.setSafeMode(SAFEMODE_GET, false));
        Assert.assertFalse(routerProtocol.setSafeMode(SAFEMODE_LEAVE, false));
        Assert.assertFalse(routerProtocol.setSafeMode(SAFEMODE_GET, false));
    }

    @Test
    public void testProxyRestoreFailedStorage() throws Exception {
        boolean routerSuccess = routerProtocol.restoreFailedStorage("check");
        boolean nnSuccess = nnProtocol.restoreFailedStorage("check");
        Assert.assertEquals(nnSuccess, routerSuccess);
    }

    @Test
    public void testProxyExceptionMessages() throws IOException {
        // Install a mount point to a different path to check
        MockResolver resolver = ((MockResolver) (router.getRouter().getSubclusterResolver()));
        String ns0 = TestRouterRpc.cluster.getNameservices().get(0);
        resolver.addLocation("/mnt", ns0, "/");
        try {
            FsPermission permission = new FsPermission("777");
            routerProtocol.mkdirs("/mnt/folder0/folder1", permission, false);
            Assert.fail("mkdirs for non-existing parent folder should have failed");
        } catch (IOException ioe) {
            assertExceptionContains("/mnt/folder0", ioe, "Wrong path in exception for mkdirs");
        }
        try {
            FsPermission permission = new FsPermission("777");
            routerProtocol.setPermission("/mnt/testfile.txt", permission);
            Assert.fail("setPermission for non-existing file should have failed");
        } catch (IOException ioe) {
            assertExceptionContains("/mnt/testfile.txt", ioe, "Wrong path in exception for setPermission");
        }
        try {
            FsPermission permission = new FsPermission("777");
            routerProtocol.mkdirs("/mnt/folder0/folder1", permission, false);
            routerProtocol.delete("/mnt/folder0", false);
            Assert.fail("delete for non-existing file should have failed");
        } catch (IOException ioe) {
            assertExceptionContains("/mnt/folder0", ioe, "Wrong path in exception for delete");
        }
        resolver.cleanRegistrations();
        // Check corner cases
        Assert.assertEquals("Parent directory doesn't exist: /ns1/a/a/b", RouterRpcClient.processExceptionMsg("Parent directory doesn't exist: /a/a/b", "/a", "/ns1/a"));
    }

    @Test
    public void testErasureCoding() throws IOException {
        TestRouterRpc.LOG.info("List the available erasurce coding policies");
        ErasureCodingPolicyInfo[] policies = checkErasureCodingPolicies();
        for (ErasureCodingPolicyInfo policy : policies) {
            TestRouterRpc.LOG.info("  {}", policy);
        }
        TestRouterRpc.LOG.info("List the erasure coding codecs");
        Map<String, String> codecsRouter = routerProtocol.getErasureCodingCodecs();
        Map<String, String> codecsNamenode = nnProtocol.getErasureCodingCodecs();
        Assert.assertTrue(Maps.difference(codecsRouter, codecsNamenode).areEqual());
        for (Map.Entry<String, String> entry : codecsRouter.entrySet()) {
            TestRouterRpc.LOG.info("  {}: {}", entry.getKey(), entry.getValue());
        }
        TestRouterRpc.LOG.info("Create a testing directory via the router at the root level");
        String dirPath = "/testec";
        String filePath1 = dirPath + "/testfile1";
        FsPermission permission = new FsPermission("755");
        routerProtocol.mkdirs(dirPath, permission, false);
        FederationTestUtils.createFile(routerFS, filePath1, 32);
        Assert.assertTrue(FederationTestUtils.verifyFileExists(routerFS, filePath1));
        DFSClient file1Protocol = getFileDFSClient(filePath1);
        TestRouterRpc.LOG.info("The policy for the new file should not be set");
        Assert.assertNull(routerProtocol.getErasureCodingPolicy(filePath1));
        Assert.assertNull(file1Protocol.getErasureCodingPolicy(filePath1));
        String policyName = "RS-6-3-1024k";
        TestRouterRpc.LOG.info("Set policy \"{}\" for \"{}\"", policyName, dirPath);
        routerProtocol.setErasureCodingPolicy(dirPath, policyName);
        String filePath2 = dirPath + "/testfile2";
        TestRouterRpc.LOG.info("Create {} in the path with the new EC policy", filePath2);
        FederationTestUtils.createFile(routerFS, filePath2, 32);
        Assert.assertTrue(FederationTestUtils.verifyFileExists(routerFS, filePath2));
        DFSClient file2Protocol = getFileDFSClient(filePath2);
        TestRouterRpc.LOG.info("Check that the policy is set for {}", filePath2);
        ErasureCodingPolicy policyRouter1 = routerProtocol.getErasureCodingPolicy(filePath2);
        ErasureCodingPolicy policyNamenode1 = file2Protocol.getErasureCodingPolicy(filePath2);
        Assert.assertNotNull(policyRouter1);
        Assert.assertEquals(policyName, policyRouter1.getName());
        Assert.assertEquals(policyName, policyNamenode1.getName());
        TestRouterRpc.LOG.info("Create a new erasure coding policy");
        String newPolicyName = "RS-6-3-128k";
        ECSchema ecSchema = new ECSchema(ErasureCodeConstants.RS_CODEC_NAME, 6, 3);
        ErasureCodingPolicy ecPolicy = new ErasureCodingPolicy(newPolicyName, ecSchema, (128 * 1024), ((byte) (-1)));
        ErasureCodingPolicy[] newPolicies = new ErasureCodingPolicy[]{ ecPolicy };
        AddErasureCodingPolicyResponse[] responses = routerProtocol.addErasureCodingPolicies(newPolicies);
        Assert.assertEquals(1, responses.length);
        Assert.assertTrue(responses[0].isSucceed());
        routerProtocol.disableErasureCodingPolicy(newPolicyName);
        TestRouterRpc.LOG.info("The new policy should be there and disabled");
        policies = checkErasureCodingPolicies();
        boolean found = false;
        for (ErasureCodingPolicyInfo policy : policies) {
            TestRouterRpc.LOG.info(("  {}" + policy));
            if (policy.getPolicy().getName().equals(newPolicyName)) {
                found = true;
                Assert.assertEquals(DISABLED, policy.getState());
                break;
            }
        }
        Assert.assertTrue(found);
        TestRouterRpc.LOG.info("Set the test folder to use the new policy");
        routerProtocol.enableErasureCodingPolicy(newPolicyName);
        routerProtocol.setErasureCodingPolicy(dirPath, newPolicyName);
        TestRouterRpc.LOG.info("Create a file in the path with the new EC policy");
        String filePath3 = dirPath + "/testfile3";
        FederationTestUtils.createFile(routerFS, filePath3, 32);
        Assert.assertTrue(FederationTestUtils.verifyFileExists(routerFS, filePath3));
        DFSClient file3Protocol = getFileDFSClient(filePath3);
        ErasureCodingPolicy policyRouterFile3 = routerProtocol.getErasureCodingPolicy(filePath3);
        Assert.assertEquals(newPolicyName, policyRouterFile3.getName());
        ErasureCodingPolicy policyNamenodeFile3 = file3Protocol.getErasureCodingPolicy(filePath3);
        Assert.assertEquals(newPolicyName, policyNamenodeFile3.getName());
        TestRouterRpc.LOG.info("Remove the policy and check the one for the test folder");
        routerProtocol.removeErasureCodingPolicy(newPolicyName);
        ErasureCodingPolicy policyRouter3 = routerProtocol.getErasureCodingPolicy(filePath3);
        Assert.assertEquals(newPolicyName, policyRouter3.getName());
        ErasureCodingPolicy policyNamenode3 = file3Protocol.getErasureCodingPolicy(filePath3);
        Assert.assertEquals(newPolicyName, policyNamenode3.getName());
        TestRouterRpc.LOG.info("Check the stats");
        ECBlockGroupStats statsRouter = routerProtocol.getECBlockGroupStats();
        ECBlockGroupStats statsNamenode = nnProtocol.getECBlockGroupStats();
        Assert.assertEquals(statsNamenode.toString(), statsRouter.toString());
    }

    @Test
    public void testNamenodeMetrics() throws Exception {
        final NamenodeBeanMetrics metrics = router.getRouter().getNamenodeMetrics();
        final String jsonString0 = metrics.getLiveNodes();
        // We should have 12 nodes in total
        JSONObject jsonObject = new JSONObject(jsonString0);
        Assert.assertEquals(12, jsonObject.names().length());
        // We should be caching this information
        String jsonString1 = metrics.getLiveNodes();
        Assert.assertEquals(jsonString0, jsonString1);
        // We wait until the cached value is updated
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return !(jsonString0.equals(metrics.getLiveNodes()));
            }
        }, 500, (5 * 1000));
        // The cache should be updated now
        final String jsonString2 = metrics.getLiveNodes();
        Assert.assertNotEquals(jsonString0, jsonString2);
        // Without any subcluster available, we should return an empty list
        MockResolver resolver = ((MockResolver) (router.getRouter().getNamenodeResolver()));
        resolver.cleanRegistrations();
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return !(jsonString2.equals(metrics.getLiveNodes()));
            }
        }, 500, (5 * 1000));
        Assert.assertEquals("{}", metrics.getLiveNodes());
        // Reset the registrations again
        TestRouterRpc.cluster.registerNamenodes();
        TestRouterRpc.cluster.waitNamenodeRegistration();
    }
}

