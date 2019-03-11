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
package org.apache.hadoop.hdfs;


import CommonConfigurationKeys.IO_FILE_BUFFER_SIZE_KEY;
import CommonConfigurationKeysPublic.HADOOP_SECURITY_KEY_PROVIDER_PATH;
import CreateFlag.OVERWRITE;
import DFSClient.LOG;
import DFSConfigKeys.DFS_LIST_LIMIT;
import DFSConfigKeys.DFS_NAMENODE_HTTP_ADDRESS_KEY;
import DFSConfigKeys.DFS_STORAGE_POLICY_SATISFIER_MODE_KEY;
import DFSOpsCountStatistics.NAME;
import DataChecksum.Type;
import DataChecksum.Type.CRC32;
import DataChecksum.Type.CRC32C;
import FsDatasetSpi.FsVolumeReferences;
import HdfsClientConfigKeys.DFS_BYTES_PER_CHECKSUM_KEY;
import HdfsClientConfigKeys.DFS_CLIENT_SOCKET_TIMEOUT_KEY;
import HdfsConstants.HDFS_URI_SCHEME;
import KeyProvider.Options;
import OpType.ADD_CACHE_DIRECTIVE;
import OpType.ADD_CACHE_POOL;
import OpType.ADD_EC_POLICY;
import OpType.CREATE;
import OpType.CREATE_ENCRYPTION_ZONE;
import OpType.DELETE;
import OpType.DISABLE_EC_POLICY;
import OpType.ENABLE_EC_POLICY;
import OpType.GET_CONTENT_SUMMARY;
import OpType.GET_EC_CODECS;
import OpType.GET_EC_POLICIES;
import OpType.GET_EC_POLICY;
import OpType.GET_ENCRYPTION_ZONE;
import OpType.GET_FILE_BLOCK_LOCATIONS;
import OpType.GET_FILE_CHECKSUM;
import OpType.GET_FILE_STATUS;
import OpType.GET_QUOTA_USAGE;
import OpType.GET_STATUS;
import OpType.LIST_CACHE_DIRECTIVE;
import OpType.LIST_CACHE_POOL;
import OpType.LIST_ENCRYPTION_ZONE;
import OpType.LIST_LOCATED_STATUS;
import OpType.LIST_STATUS;
import OpType.MKDIRS;
import OpType.MODIFY_CACHE_DIRECTIVE;
import OpType.MODIFY_CACHE_POOL;
import OpType.OPEN;
import OpType.REMOVE_CACHE_DIRECTIVE;
import OpType.REMOVE_CACHE_POOL;
import OpType.REMOVE_EC_POLICY;
import OpType.RENAME;
import OpType.SATISFY_STORAGE_POLICY;
import OpType.SET_EC_POLICY;
import OpType.SET_OWNER;
import OpType.SET_PERMISSION;
import OpType.SET_QUOTA_BYTSTORAGEYPE;
import OpType.SET_QUOTA_USAGE;
import OpType.SET_REPLICATION;
import OpType.SET_TIMES;
import OpType.UNSET_EC_POLICY;
import StoragePolicySatisfierMode.EXTERNAL;
import StorageType.ARCHIVE;
import StorageType.DEFAULT;
import StorageType.DISK;
import StorageType.SSD;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.crypto.key.JavaKeyStoreProvider;
import org.apache.hadoop.crypto.key.KeyProvider;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.CreateFlag;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileAlreadyExistsException;
import org.apache.hadoop.fs.FileChecksum;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileSystem.Statistics.StatisticsData;
import org.apache.hadoop.fs.FsServerDefaults;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.MD5MD5CRC32FileChecksum;
import org.apache.hadoop.fs.Options.ChecksumOpt;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathIsNotEmptyDirectoryException;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.fs.StorageStatistics.LongStatistic;
import org.apache.hadoop.fs.StorageType;
import org.apache.hadoop.fs.contract.ContractTestUtils;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DistributedFileSystem.HdfsDataOutputStreamBuilder;
import org.apache.hadoop.hdfs.client.impl.LeaseRenewer;
import org.apache.hadoop.hdfs.net.Peer;
import org.apache.hadoop.hdfs.protocol.CacheDirectiveInfo;
import org.apache.hadoop.hdfs.protocol.CachePoolInfo;
import org.apache.hadoop.hdfs.protocol.ErasureCodingPolicy;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.FsDatasetSpi;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.FsVolumeSpi;
import org.apache.hadoop.hdfs.server.namenode.ErasureCodingPolicyManager;
import org.apache.hadoop.hdfs.web.WebHdfsConstants;
import org.apache.hadoop.io.erasurecode.ECSchema;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.Whitebox;
import org.apache.hadoop.util.Time;
import org.apache.hadoop.util.concurrent.HadoopExecutors;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;


public class TestDistributedFileSystem {
    private static final Random RAN = new Random();

    private static final Logger LOG = LoggerFactory.getLogger(TestDistributedFileSystem.class);

    static {
        GenericTestUtils.setLogLevel(DFSClient.LOG, Level.TRACE);
        GenericTestUtils.setLogLevel(LeaseRenewer.LOG, Level.DEBUG);
    }

    private boolean dualPortTesting = false;

    private boolean noXmlDefaults = false;

    @Test
    public void testEmptyDelegationToken() throws IOException {
        Configuration conf = getTestConfiguration();
        MiniDFSCluster cluster = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
            FileSystem fileSys = cluster.getFileSystem();
            fileSys.getDelegationToken("");
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test
    public void testFileSystemCloseAll() throws Exception {
        Configuration conf = getTestConfiguration();
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(0).build();
        URI address = FileSystem.getDefaultUri(conf);
        try {
            FileSystem.closeAll();
            conf = getTestConfiguration();
            FileSystem.setDefaultUri(conf, address);
            FileSystem.get(conf);
            FileSystem.get(conf);
            FileSystem.closeAll();
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    /**
     * Tests DFSClient.close throws no ConcurrentModificationException if
     * multiple files are open.
     * Also tests that any cached sockets are closed. (HDFS-3359)
     */
    @Test
    public void testDFSClose() throws Exception {
        Configuration conf = getTestConfiguration();
        MiniDFSCluster cluster = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(2).build();
            DistributedFileSystem fileSys = cluster.getFileSystem();
            // create two files, leaving them open
            fileSys.create(new Path("/test/dfsclose/file-0"));
            fileSys.create(new Path("/test/dfsclose/file-1"));
            // create another file, close it, and read it, so
            // the client gets a socket in its SocketCache
            Path p = new Path("/non-empty-file");
            DFSTestUtil.createFile(fileSys, p, 1L, ((short) (1)), 0L);
            DFSTestUtil.readFile(fileSys, p);
            fileSys.close();
            DFSClient dfsClient = fileSys.getClient();
            verifyOpsUsingClosedClient(dfsClient);
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test
    public void testDFSCloseOrdering() throws Exception {
        DistributedFileSystem fs = new TestDistributedFileSystem.MyDistributedFileSystem();
        Path path = new Path("/a");
        fs.deleteOnExit(path);
        fs.close();
        InOrder inOrder = Mockito.inOrder(fs.dfs);
        inOrder.verify(fs.dfs).closeOutputStreams(ArgumentMatchers.eq(false));
        inOrder.verify(fs.dfs).delete(ArgumentMatchers.eq(path.toString()), ArgumentMatchers.eq(true));
        inOrder.verify(fs.dfs).close();
    }

    private static class MyDistributedFileSystem extends DistributedFileSystem {
        MyDistributedFileSystem() {
            dfs = Mockito.mock(DFSClient.class);
        }

        @Override
        public boolean exists(Path p) {
            return true;// trick out deleteOnExit

        }

        // Symlink resolution doesn't work with a mock, since it doesn't
        // have a valid Configuration to resolve paths to the right FileSystem.
        // Just call the DFSClient directly to register the delete
        @Override
        public boolean delete(Path f, final boolean recursive) throws IOException {
            return dfs.delete(f.toUri().getPath(), recursive);
        }
    }

    @Test
    public void testDFSSeekExceptions() throws IOException {
        Configuration conf = getTestConfiguration();
        MiniDFSCluster cluster = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(2).build();
            FileSystem fileSys = cluster.getFileSystem();
            String file = "/test/fileclosethenseek/file-0";
            Path path = new Path(file);
            // create file
            FSDataOutputStream output = fileSys.create(path);
            output.writeBytes("Some test data to write longer than 10 bytes");
            output.close();
            FSDataInputStream input = fileSys.open(path);
            input.seek(10);
            boolean threw = false;
            try {
                input.seek(100);
            } catch (IOException e) {
                // success
                threw = true;
            }
            Assert.assertTrue("Failed to throw IOE when seeking past end", threw);
            input.close();
            threw = false;
            try {
                input.seek(1);
            } catch (IOException e) {
                // success
                threw = true;
            }
            Assert.assertTrue("Failed to throw IOE when seeking after close", threw);
            fileSys.close();
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test
    public void testDFSClient() throws Exception {
        Configuration conf = getTestConfiguration();
        final long grace = 1000L;
        MiniDFSCluster cluster = null;
        LeaseRenewer.setLeaseRenewerGraceDefault(grace);
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(2).build();
            final String filepathstring = "/test/LeaseChecker/foo";
            final Path[] filepaths = new Path[4];
            for (int i = 0; i < (filepaths.length); i++) {
                filepaths[i] = new Path((filepathstring + i));
            }
            final long millis = Time.now();
            {
                final DistributedFileSystem dfs = cluster.getFileSystem();
                Method checkMethod = dfs.dfs.getLeaseRenewer().getClass().getDeclaredMethod("isRunning");
                checkMethod.setAccessible(true);
                Assert.assertFalse(((boolean) (checkMethod.invoke(dfs.dfs.getLeaseRenewer()))));
                {
                    // create a file
                    final FSDataOutputStream out = dfs.create(filepaths[0]);
                    Assert.assertTrue(((boolean) (checkMethod.invoke(dfs.dfs.getLeaseRenewer()))));
                    // write something
                    out.writeLong(millis);
                    Assert.assertTrue(((boolean) (checkMethod.invoke(dfs.dfs.getLeaseRenewer()))));
                    // close
                    out.close();
                    Thread.sleep(((grace / 4) * 3));
                    // within grace period
                    Assert.assertTrue(((boolean) (checkMethod.invoke(dfs.dfs.getLeaseRenewer()))));
                    for (int i = 0; i < 3; i++) {
                        if (((boolean) (checkMethod.invoke(dfs.dfs.getLeaseRenewer())))) {
                            Thread.sleep((grace / 2));
                        }
                    }
                    // passed grace period
                    Assert.assertFalse(((boolean) (checkMethod.invoke(dfs.dfs.getLeaseRenewer()))));
                }
                {
                    // create file1
                    final FSDataOutputStream out1 = dfs.create(filepaths[1]);
                    Assert.assertTrue(((boolean) (checkMethod.invoke(dfs.dfs.getLeaseRenewer()))));
                    // create file2
                    final FSDataOutputStream out2 = dfs.create(filepaths[2]);
                    Assert.assertTrue(((boolean) (checkMethod.invoke(dfs.dfs.getLeaseRenewer()))));
                    // write something to file1
                    out1.writeLong(millis);
                    Assert.assertTrue(((boolean) (checkMethod.invoke(dfs.dfs.getLeaseRenewer()))));
                    // close file1
                    out1.close();
                    Assert.assertTrue(((boolean) (checkMethod.invoke(dfs.dfs.getLeaseRenewer()))));
                    // write something to file2
                    out2.writeLong(millis);
                    Assert.assertTrue(((boolean) (checkMethod.invoke(dfs.dfs.getLeaseRenewer()))));
                    // close file2
                    out2.close();
                    Thread.sleep(((grace / 4) * 3));
                    // within grace period
                    Assert.assertTrue(((boolean) (checkMethod.invoke(dfs.dfs.getLeaseRenewer()))));
                }
                {
                    // create file3
                    final FSDataOutputStream out3 = dfs.create(filepaths[3]);
                    Assert.assertTrue(((boolean) (checkMethod.invoke(dfs.dfs.getLeaseRenewer()))));
                    Thread.sleep(((grace / 4) * 3));
                    // passed previous grace period, should still running
                    Assert.assertTrue(((boolean) (checkMethod.invoke(dfs.dfs.getLeaseRenewer()))));
                    // write something to file3
                    out3.writeLong(millis);
                    Assert.assertTrue(((boolean) (checkMethod.invoke(dfs.dfs.getLeaseRenewer()))));
                    // close file3
                    out3.close();
                    Assert.assertTrue(((boolean) (checkMethod.invoke(dfs.dfs.getLeaseRenewer()))));
                    Thread.sleep(((grace / 4) * 3));
                    // within grace period
                    Assert.assertTrue(((boolean) (checkMethod.invoke(dfs.dfs.getLeaseRenewer()))));
                    for (int i = 0; i < 3; i++) {
                        if (((boolean) (checkMethod.invoke(dfs.dfs.getLeaseRenewer())))) {
                            Thread.sleep((grace / 2));
                        }
                    }
                    // passed grace period
                    Assert.assertFalse(((boolean) (checkMethod.invoke(dfs.dfs.getLeaseRenewer()))));
                }
                dfs.close();
            }
            {
                // Check to see if opening a non-existent file triggers a FNF
                FileSystem fs = cluster.getFileSystem();
                Path dir = new Path("/wrwelkj");
                Assert.assertFalse("File should not exist for test.", fs.exists(dir));
                try {
                    FSDataInputStream in = fs.open(dir);
                    try {
                        in.close();
                        fs.close();
                    } finally {
                        Assert.assertTrue(("Did not get a FileNotFoundException for non-existing" + " file."), false);
                    }
                } catch (FileNotFoundException fnf) {
                    // This is the proper exception to catch; move on.
                }
            }
            {
                final DistributedFileSystem dfs = cluster.getFileSystem();
                Method checkMethod = dfs.dfs.getLeaseRenewer().getClass().getDeclaredMethod("isRunning");
                checkMethod.setAccessible(true);
                Assert.assertFalse(((boolean) (checkMethod.invoke(dfs.dfs.getLeaseRenewer()))));
                // open and check the file
                FSDataInputStream in = dfs.open(filepaths[0]);
                Assert.assertFalse(((boolean) (checkMethod.invoke(dfs.dfs.getLeaseRenewer()))));
                Assert.assertEquals(millis, in.readLong());
                Assert.assertFalse(((boolean) (checkMethod.invoke(dfs.dfs.getLeaseRenewer()))));
                in.close();
                Assert.assertFalse(((boolean) (checkMethod.invoke(dfs.dfs.getLeaseRenewer()))));
                dfs.close();
            }
            {
                // test accessing DFS with ip address. should work with any hostname
                // alias or ip address that points to the interface that NameNode
                // is listening on. In this case, it is localhost.
                String uri = ("hdfs://127.0.0.1:" + (cluster.getNameNodePort())) + "/test/ipAddress/file";
                Path path = new Path(uri);
                FileSystem fs = FileSystem.get(path.toUri(), conf);
                FSDataOutputStream out = fs.create(path);
                byte[] buf = new byte[1024];
                out.write(buf);
                out.close();
                FSDataInputStream in = fs.open(path);
                in.readFully(buf);
                in.close();
                fs.close();
            }
            {
                // Test PathIsNotEmptyDirectoryException while deleting non-empty dir
                FileSystem fs = cluster.getFileSystem();
                fs.mkdirs(new Path("/test/nonEmptyDir"));
                fs.create(new Path("/tmp/nonEmptyDir/emptyFile")).close();
                try {
                    fs.delete(new Path("/tmp/nonEmptyDir"), false);
                    Assert.fail("Expecting PathIsNotEmptyDirectoryException");
                } catch (PathIsNotEmptyDirectoryException ex) {
                    // This is the proper exception to catch; move on.
                }
                Assert.assertTrue(fs.exists(new Path("/test/nonEmptyDir")));
                fs.delete(new Path("/tmp/nonEmptyDir"), true);
            }
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    /**
     * This is to test that the {@link FileSystem#clearStatistics()} resets all
     * the global storage statistics.
     */
    @Test
    public void testClearStatistics() throws Exception {
        final Configuration conf = getTestConfiguration();
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).build();
        try {
            cluster.waitActive();
            FileSystem dfs = cluster.getFileSystem();
            final Path dir = new Path("/testClearStatistics");
            final long mkdirCount = TestDistributedFileSystem.getOpStatistics(MKDIRS);
            long writeCount = DFSTestUtil.getStatistics(dfs).getWriteOps();
            dfs.mkdirs(dir);
            TestDistributedFileSystem.checkOpStatistics(MKDIRS, (mkdirCount + 1));
            Assert.assertEquals((++writeCount), DFSTestUtil.getStatistics(dfs).getWriteOps());
            final long createCount = TestDistributedFileSystem.getOpStatistics(CREATE);
            FSDataOutputStream out = dfs.create(new Path(dir, "tmpFile"), ((short) (1)));
            out.write(40);
            out.close();
            TestDistributedFileSystem.checkOpStatistics(CREATE, (createCount + 1));
            Assert.assertEquals((++writeCount), DFSTestUtil.getStatistics(dfs).getWriteOps());
            FileSystem.clearStatistics();
            TestDistributedFileSystem.checkOpStatistics(MKDIRS, 0);
            TestDistributedFileSystem.checkOpStatistics(CREATE, 0);
            checkStatistics(dfs, 0, 0, 0);
        } finally {
            cluster.shutdown();
        }
    }

    @Test
    public void testStatistics() throws IOException {
        FileSystem.getStatistics(HDFS_URI_SCHEME, DistributedFileSystem.class).reset();
        @SuppressWarnings("unchecked")
        ThreadLocal<StatisticsData> data = ((ThreadLocal<StatisticsData>) (Whitebox.getInternalState(FileSystem.getStatistics(HDFS_URI_SCHEME, DistributedFileSystem.class), "threadData")));
        data.set(null);
        int lsLimit = 2;
        final Configuration conf = getTestConfiguration();
        conf.setInt(DFS_LIST_LIMIT, lsLimit);
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).build();
        try {
            cluster.waitActive();
            final FileSystem fs = cluster.getFileSystem();
            Path dir = new Path("/test");
            Path file = new Path(dir, "file");
            int readOps = 0;
            int writeOps = 0;
            int largeReadOps = 0;
            long opCount = TestDistributedFileSystem.getOpStatistics(MKDIRS);
            fs.mkdirs(dir);
            checkStatistics(fs, readOps, (++writeOps), largeReadOps);
            TestDistributedFileSystem.checkOpStatistics(MKDIRS, (opCount + 1));
            opCount = TestDistributedFileSystem.getOpStatistics(CREATE);
            FSDataOutputStream out = fs.create(file, ((short) (1)));
            out.close();
            checkStatistics(fs, readOps, (++writeOps), largeReadOps);
            TestDistributedFileSystem.checkOpStatistics(CREATE, (opCount + 1));
            opCount = TestDistributedFileSystem.getOpStatistics(GET_FILE_STATUS);
            FileStatus status = fs.getFileStatus(file);
            checkStatistics(fs, (++readOps), writeOps, largeReadOps);
            TestDistributedFileSystem.checkOpStatistics(GET_FILE_STATUS, (opCount + 1));
            opCount = TestDistributedFileSystem.getOpStatistics(GET_FILE_BLOCK_LOCATIONS);
            fs.getFileBlockLocations(file, 0, 0);
            checkStatistics(fs, (++readOps), writeOps, largeReadOps);
            TestDistributedFileSystem.checkOpStatistics(GET_FILE_BLOCK_LOCATIONS, (opCount + 1));
            fs.getFileBlockLocations(status, 0, 0);
            checkStatistics(fs, (++readOps), writeOps, largeReadOps);
            TestDistributedFileSystem.checkOpStatistics(GET_FILE_BLOCK_LOCATIONS, (opCount + 2));
            opCount = TestDistributedFileSystem.getOpStatistics(OPEN);
            FSDataInputStream in = fs.open(file);
            in.close();
            checkStatistics(fs, (++readOps), writeOps, largeReadOps);
            TestDistributedFileSystem.checkOpStatistics(OPEN, (opCount + 1));
            opCount = TestDistributedFileSystem.getOpStatistics(SET_REPLICATION);
            fs.setReplication(file, ((short) (2)));
            checkStatistics(fs, readOps, (++writeOps), largeReadOps);
            TestDistributedFileSystem.checkOpStatistics(SET_REPLICATION, (opCount + 1));
            opCount = TestDistributedFileSystem.getOpStatistics(RENAME);
            Path file1 = new Path(dir, "file1");
            fs.rename(file, file1);
            checkStatistics(fs, readOps, (++writeOps), largeReadOps);
            TestDistributedFileSystem.checkOpStatistics(RENAME, (opCount + 1));
            opCount = TestDistributedFileSystem.getOpStatistics(GET_CONTENT_SUMMARY);
            fs.getContentSummary(file1);
            checkStatistics(fs, (++readOps), writeOps, largeReadOps);
            TestDistributedFileSystem.checkOpStatistics(GET_CONTENT_SUMMARY, (opCount + 1));
            // Iterative ls test
            long mkdirOp = TestDistributedFileSystem.getOpStatistics(MKDIRS);
            long listStatusOp = TestDistributedFileSystem.getOpStatistics(LIST_STATUS);
            long locatedListStatusOP = TestDistributedFileSystem.getOpStatistics(LIST_LOCATED_STATUS);
            for (int i = 0; i < 10; i++) {
                Path p = new Path(dir, Integer.toString(i));
                fs.mkdirs(p);
                mkdirOp++;
                FileStatus[] list = fs.listStatus(dir);
                if ((list.length) > lsLimit) {
                    // if large directory, then count readOps and largeReadOps by
                    // number times listStatus iterates
                    int iterations = ((int) (Math.ceil((((double) (list.length)) / lsLimit))));
                    largeReadOps += iterations;
                    readOps += iterations;
                    listStatusOp += iterations;
                } else {
                    // Single iteration in listStatus - no large read operation done
                    readOps++;
                    listStatusOp++;
                }
                // writeOps incremented by 1 for mkdirs
                // readOps and largeReadOps incremented by 1 or more
                checkStatistics(fs, readOps, (++writeOps), largeReadOps);
                TestDistributedFileSystem.checkOpStatistics(MKDIRS, mkdirOp);
                TestDistributedFileSystem.checkOpStatistics(LIST_STATUS, listStatusOp);
                fs.listLocatedStatus(dir);
                locatedListStatusOP++;
                readOps++;
                checkStatistics(fs, readOps, writeOps, largeReadOps);
                TestDistributedFileSystem.checkOpStatistics(LIST_LOCATED_STATUS, locatedListStatusOP);
            }
            opCount = TestDistributedFileSystem.getOpStatistics(GET_STATUS);
            fs.getStatus(file1);
            checkStatistics(fs, (++readOps), writeOps, largeReadOps);
            TestDistributedFileSystem.checkOpStatistics(GET_STATUS, (opCount + 1));
            opCount = TestDistributedFileSystem.getOpStatistics(GET_FILE_CHECKSUM);
            fs.getFileChecksum(file1);
            checkStatistics(fs, (++readOps), writeOps, largeReadOps);
            TestDistributedFileSystem.checkOpStatistics(GET_FILE_CHECKSUM, (opCount + 1));
            opCount = TestDistributedFileSystem.getOpStatistics(SET_PERMISSION);
            fs.setPermission(file1, new FsPermission(((short) (511))));
            checkStatistics(fs, readOps, (++writeOps), largeReadOps);
            TestDistributedFileSystem.checkOpStatistics(SET_PERMISSION, (opCount + 1));
            opCount = TestDistributedFileSystem.getOpStatistics(SET_TIMES);
            fs.setTimes(file1, 0L, 0L);
            checkStatistics(fs, readOps, (++writeOps), largeReadOps);
            TestDistributedFileSystem.checkOpStatistics(SET_TIMES, (opCount + 1));
            opCount = TestDistributedFileSystem.getOpStatistics(SET_OWNER);
            UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
            fs.setOwner(file1, ugi.getUserName(), ugi.getGroupNames()[0]);
            TestDistributedFileSystem.checkOpStatistics(SET_OWNER, (opCount + 1));
            checkStatistics(fs, readOps, (++writeOps), largeReadOps);
            opCount = TestDistributedFileSystem.getOpStatistics(DELETE);
            fs.delete(dir, true);
            checkStatistics(fs, readOps, (++writeOps), largeReadOps);
            TestDistributedFileSystem.checkOpStatistics(DELETE, (opCount + 1));
        } finally {
            if (cluster != null)
                cluster.shutdown();

        }
    }

    @Test
    public void testStatistics2() throws IOException, NoSuchAlgorithmException {
        HdfsConfiguration conf = new HdfsConfiguration();
        conf.set(DFS_STORAGE_POLICY_SATISFIER_MODE_KEY, EXTERNAL.toString());
        File tmpDir = GenericTestUtils.getTestDir(UUID.randomUUID().toString());
        final Path jksPath = new Path(tmpDir.toString(), "test.jks");
        conf.set(HADOOP_SECURITY_KEY_PROVIDER_PATH, (((JavaKeyStoreProvider.SCHEME_NAME) + "://file") + (jksPath.toUri())));
        try (MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).build()) {
            cluster.waitActive();
            final DistributedFileSystem dfs = cluster.getFileSystem();
            Path dir = new Path("/testStat");
            dfs.mkdirs(dir);
            int readOps = 0;
            int writeOps = 0;
            FileSystem.clearStatistics();
            // Quota Commands.
            long opCount = TestDistributedFileSystem.getOpStatistics(SET_QUOTA_USAGE);
            dfs.setQuota(dir, 100, 1000);
            checkStatistics(dfs, readOps, (++writeOps), 0);
            TestDistributedFileSystem.checkOpStatistics(SET_QUOTA_USAGE, (opCount + 1));
            opCount = TestDistributedFileSystem.getOpStatistics(SET_QUOTA_BYTSTORAGEYPE);
            dfs.setQuotaByStorageType(dir, DEFAULT, 2000);
            checkStatistics(dfs, readOps, (++writeOps), 0);
            TestDistributedFileSystem.checkOpStatistics(SET_QUOTA_BYTSTORAGEYPE, (opCount + 1));
            opCount = TestDistributedFileSystem.getOpStatistics(GET_QUOTA_USAGE);
            dfs.getQuotaUsage(dir);
            checkStatistics(dfs, (++readOps), writeOps, 0);
            TestDistributedFileSystem.checkOpStatistics(GET_QUOTA_USAGE, (opCount + 1));
            // Satisfy Storage Policy.
            opCount = TestDistributedFileSystem.getOpStatistics(SATISFY_STORAGE_POLICY);
            dfs.satisfyStoragePolicy(dir);
            checkStatistics(dfs, readOps, (++writeOps), 0);
            TestDistributedFileSystem.checkOpStatistics(SATISFY_STORAGE_POLICY, (opCount + 1));
            // Cache Commands.
            CachePoolInfo cacheInfo = new CachePoolInfo("pool1").setMode(new FsPermission(((short) (0))));
            opCount = TestDistributedFileSystem.getOpStatistics(ADD_CACHE_POOL);
            dfs.addCachePool(cacheInfo);
            checkStatistics(dfs, readOps, (++writeOps), 0);
            TestDistributedFileSystem.checkOpStatistics(ADD_CACHE_POOL, (opCount + 1));
            CacheDirectiveInfo directive = new CacheDirectiveInfo.Builder().setPath(new Path(".")).setPool("pool1").build();
            opCount = TestDistributedFileSystem.getOpStatistics(ADD_CACHE_DIRECTIVE);
            long id = dfs.addCacheDirective(directive);
            checkStatistics(dfs, readOps, (++writeOps), 0);
            TestDistributedFileSystem.checkOpStatistics(ADD_CACHE_DIRECTIVE, (opCount + 1));
            opCount = TestDistributedFileSystem.getOpStatistics(LIST_CACHE_DIRECTIVE);
            dfs.listCacheDirectives(null);
            checkStatistics(dfs, (++readOps), writeOps, 0);
            TestDistributedFileSystem.checkOpStatistics(LIST_CACHE_DIRECTIVE, (opCount + 1));
            opCount = TestDistributedFileSystem.getOpStatistics(MODIFY_CACHE_DIRECTIVE);
            dfs.modifyCacheDirective(new CacheDirectiveInfo.Builder().setId(id).setReplication(((short) (2))).build());
            checkStatistics(dfs, readOps, (++writeOps), 0);
            TestDistributedFileSystem.checkOpStatistics(MODIFY_CACHE_DIRECTIVE, (opCount + 1));
            opCount = TestDistributedFileSystem.getOpStatistics(REMOVE_CACHE_DIRECTIVE);
            dfs.removeCacheDirective(id);
            checkStatistics(dfs, readOps, (++writeOps), 0);
            TestDistributedFileSystem.checkOpStatistics(REMOVE_CACHE_DIRECTIVE, (opCount + 1));
            opCount = TestDistributedFileSystem.getOpStatistics(MODIFY_CACHE_POOL);
            dfs.modifyCachePool(cacheInfo);
            checkStatistics(dfs, readOps, (++writeOps), 0);
            TestDistributedFileSystem.checkOpStatistics(MODIFY_CACHE_POOL, (opCount + 1));
            opCount = TestDistributedFileSystem.getOpStatistics(LIST_CACHE_POOL);
            dfs.listCachePools();
            checkStatistics(dfs, (++readOps), writeOps, 0);
            TestDistributedFileSystem.checkOpStatistics(LIST_CACHE_POOL, (opCount + 1));
            opCount = TestDistributedFileSystem.getOpStatistics(REMOVE_CACHE_POOL);
            dfs.removeCachePool(cacheInfo.getPoolName());
            checkStatistics(dfs, readOps, (++writeOps), 0);
            TestDistributedFileSystem.checkOpStatistics(REMOVE_CACHE_POOL, (opCount + 1));
            // Crypto Commands.
            final KeyProvider provider = cluster.getNameNode().getNamesystem().getProvider();
            final KeyProvider.Options options = KeyProvider.options(conf);
            provider.createKey("key", options);
            provider.flush();
            opCount = TestDistributedFileSystem.getOpStatistics(CREATE_ENCRYPTION_ZONE);
            dfs.createEncryptionZone(dir, "key");
            checkStatistics(dfs, readOps, (++writeOps), 0);
            TestDistributedFileSystem.checkOpStatistics(CREATE_ENCRYPTION_ZONE, (opCount + 1));
            opCount = TestDistributedFileSystem.getOpStatistics(LIST_ENCRYPTION_ZONE);
            dfs.listEncryptionZones();
            checkStatistics(dfs, (++readOps), writeOps, 0);
            TestDistributedFileSystem.checkOpStatistics(LIST_ENCRYPTION_ZONE, (opCount + 1));
            opCount = TestDistributedFileSystem.getOpStatistics(GET_ENCRYPTION_ZONE);
            dfs.getEZForPath(dir);
            checkStatistics(dfs, (++readOps), writeOps, 0);
            TestDistributedFileSystem.checkOpStatistics(GET_ENCRYPTION_ZONE, (opCount + 1));
        }
    }

    @Test
    public void testECStatistics() throws IOException {
        try (MiniDFSCluster cluster = new MiniDFSCluster.Builder(getTestConfiguration()).build()) {
            cluster.waitActive();
            final DistributedFileSystem dfs = cluster.getFileSystem();
            Path dir = new Path("/test");
            dfs.mkdirs(dir);
            int readOps = 0;
            int writeOps = 0;
            FileSystem.clearStatistics();
            long opCount = TestDistributedFileSystem.getOpStatistics(ENABLE_EC_POLICY);
            dfs.enableErasureCodingPolicy("RS-10-4-1024k");
            checkStatistics(dfs, readOps, (++writeOps), 0);
            TestDistributedFileSystem.checkOpStatistics(ENABLE_EC_POLICY, (opCount + 1));
            opCount = TestDistributedFileSystem.getOpStatistics(SET_EC_POLICY);
            dfs.setErasureCodingPolicy(dir, "RS-10-4-1024k");
            checkStatistics(dfs, readOps, (++writeOps), 0);
            TestDistributedFileSystem.checkOpStatistics(SET_EC_POLICY, (opCount + 1));
            opCount = TestDistributedFileSystem.getOpStatistics(GET_EC_POLICY);
            dfs.getErasureCodingPolicy(dir);
            checkStatistics(dfs, (++readOps), writeOps, 0);
            TestDistributedFileSystem.checkOpStatistics(GET_EC_POLICY, (opCount + 1));
            opCount = TestDistributedFileSystem.getOpStatistics(UNSET_EC_POLICY);
            dfs.unsetErasureCodingPolicy(dir);
            checkStatistics(dfs, readOps, (++writeOps), 0);
            TestDistributedFileSystem.checkOpStatistics(UNSET_EC_POLICY, (opCount + 1));
            opCount = TestDistributedFileSystem.getOpStatistics(GET_EC_POLICIES);
            dfs.getAllErasureCodingPolicies();
            checkStatistics(dfs, (++readOps), writeOps, 0);
            TestDistributedFileSystem.checkOpStatistics(GET_EC_POLICIES, (opCount + 1));
            opCount = TestDistributedFileSystem.getOpStatistics(GET_EC_CODECS);
            dfs.getAllErasureCodingCodecs();
            checkStatistics(dfs, (++readOps), writeOps, 0);
            TestDistributedFileSystem.checkOpStatistics(GET_EC_CODECS, (opCount + 1));
            ErasureCodingPolicy newPolicy = new ErasureCodingPolicy(new ECSchema("rs", 5, 3), (1024 * 1024));
            opCount = TestDistributedFileSystem.getOpStatistics(ADD_EC_POLICY);
            dfs.addErasureCodingPolicies(new ErasureCodingPolicy[]{ newPolicy });
            checkStatistics(dfs, readOps, (++writeOps), 0);
            TestDistributedFileSystem.checkOpStatistics(ADD_EC_POLICY, (opCount + 1));
            opCount = TestDistributedFileSystem.getOpStatistics(REMOVE_EC_POLICY);
            dfs.removeErasureCodingPolicy("RS-5-3-1024k");
            checkStatistics(dfs, readOps, (++writeOps), 0);
            TestDistributedFileSystem.checkOpStatistics(REMOVE_EC_POLICY, (opCount + 1));
            opCount = TestDistributedFileSystem.getOpStatistics(DISABLE_EC_POLICY);
            dfs.disableErasureCodingPolicy("RS-10-4-1024k");
            checkStatistics(dfs, readOps, (++writeOps), 0);
            TestDistributedFileSystem.checkOpStatistics(DISABLE_EC_POLICY, (opCount + 1));
        }
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test(timeout = 180000)
    public void testConcurrentStatistics() throws IOException, InterruptedException {
        FileSystem.getStatistics(HDFS_URI_SCHEME, DistributedFileSystem.class).reset();
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(new Configuration()).build();
        cluster.waitActive();
        final FileSystem fs = cluster.getFileSystem();
        final int numThreads = 5;
        final ExecutorService threadPool = HadoopExecutors.newFixedThreadPool(numThreads);
        try {
            final CountDownLatch allExecutorThreadsReady = new CountDownLatch(numThreads);
            final CountDownLatch startBlocker = new CountDownLatch(1);
            final CountDownLatch allDone = new CountDownLatch(numThreads);
            final AtomicReference<Throwable> childError = new AtomicReference<>();
            for (int i = 0; i < numThreads; i++) {
                threadPool.submit(new Runnable() {
                    @Override
                    public void run() {
                        allExecutorThreadsReady.countDown();
                        try {
                            startBlocker.await();
                            final FileSystem fs = cluster.getFileSystem();
                            fs.mkdirs(new Path("/testStatisticsParallelChild"));
                        } catch (Throwable t) {
                            TestDistributedFileSystem.LOG.error("Child failed when calling mkdir", t);
                            childError.compareAndSet(null, t);
                        } finally {
                            allDone.countDown();
                        }
                    }
                });
            }
            final long oldMkdirOpCount = TestDistributedFileSystem.getOpStatistics(MKDIRS);
            // wait until all threads are ready
            allExecutorThreadsReady.await();
            // all threads start making directories
            startBlocker.countDown();
            // wait until all threads are done
            allDone.await();
            Assert.assertNull(("Child failed with exception " + (childError.get())), childError.get());
            checkStatistics(fs, 0, numThreads, 0);
            // check the single operation count stat
            TestDistributedFileSystem.checkOpStatistics(MKDIRS, (numThreads + oldMkdirOpCount));
            // iterate all the operation counts
            for (Iterator<LongStatistic> opCountIter = FileSystem.getGlobalStorageStatistics().get(NAME).getLongStatistics(); opCountIter.hasNext();) {
                final LongStatistic opCount = opCountIter.next();
                if (MKDIRS.getSymbol().equals(opCount.getName())) {
                    Assert.assertEquals("Unexpected op count from iterator!", (numThreads + oldMkdirOpCount), opCount.getValue());
                }
                TestDistributedFileSystem.LOG.info((((opCount.getName()) + "\t") + (opCount.getValue())));
            }
        } finally {
            threadPool.shutdownNow();
            cluster.shutdown();
        }
    }

    @Test
    public void testLocalHostReadStatistics() throws Exception {
        testReadFileSystemStatistics(0, false, false);
    }

    @Test
    public void testLocalRackReadStatistics() throws Exception {
        testReadFileSystemStatistics(2, false, false);
    }

    @Test
    public void testRemoteRackOfFirstDegreeReadStatistics() throws Exception {
        testReadFileSystemStatistics(4, false, false);
    }

    @Test
    public void testInvalidScriptMappingFileReadStatistics() throws Exception {
        // Even though network location of the client machine is unknown,
        // MiniDFSCluster's datanode is on the local host and thus the network
        // distance is 0.
        testReadFileSystemStatistics(0, true, true);
    }

    @Test
    public void testEmptyScriptMappingFileReadStatistics() throws Exception {
        // Network location of the client machine is resolved to
        // {@link NetworkTopology#DEFAULT_RACK} when there is no script file
        // defined. This is equivalent to unknown network location.
        // MiniDFSCluster's datanode is on the local host and thus the network
        // distance is 0.
        testReadFileSystemStatistics(0, true, false);
    }

    @Test
    public void testFileChecksum() throws Exception {
        final long seed = TestDistributedFileSystem.RAN.nextLong();
        System.out.println(("seed=" + seed));
        TestDistributedFileSystem.RAN.setSeed(seed);
        final Configuration conf = getTestConfiguration();
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(2).build();
        final FileSystem hdfs = cluster.getFileSystem();
        final String nnAddr = conf.get(DFS_NAMENODE_HTTP_ADDRESS_KEY);
        final UserGroupInformation current = UserGroupInformation.getCurrentUser();
        final UserGroupInformation ugi = UserGroupInformation.createUserForTesting(((current.getShortUserName()) + "x"), new String[]{ "user" });
        try {
            hdfs.getFileChecksum(new Path("/test/TestNonExistingFile"));
            Assert.fail("Expecting FileNotFoundException");
        } catch (FileNotFoundException e) {
            Assert.assertTrue("Not throwing the intended exception message", e.getMessage().contains("File does not exist: /test/TestNonExistingFile"));
        }
        try {
            Path path = new Path("/test/TestExistingDir/");
            hdfs.mkdirs(path);
            hdfs.getFileChecksum(path);
            Assert.fail("Expecting FileNotFoundException");
        } catch (FileNotFoundException e) {
            Assert.assertTrue("Not throwing the intended exception message", e.getMessage().contains("Path is not a file: /test/TestExistingDir"));
        }
        // webhdfs
        final String webhdfsuri = ((WebHdfsConstants.WEBHDFS_SCHEME) + "://") + nnAddr;
        System.out.println(("webhdfsuri=" + webhdfsuri));
        final FileSystem webhdfs = ugi.doAs(new PrivilegedExceptionAction<FileSystem>() {
            @Override
            public FileSystem run() throws Exception {
                return new Path(webhdfsuri).getFileSystem(conf);
            }
        });
        final Path dir = new Path("/filechecksum");
        final int block_size = 1024;
        final int buffer_size = conf.getInt(IO_FILE_BUFFER_SIZE_KEY, 4096);
        conf.setInt(DFS_BYTES_PER_CHECKSUM_KEY, 512);
        // try different number of blocks
        for (int n = 0; n < 5; n++) {
            // generate random data
            final byte[] data = new byte[((TestDistributedFileSystem.RAN.nextInt(((block_size / 2) - 1))) + (n * block_size)) + 1];
            TestDistributedFileSystem.RAN.nextBytes(data);
            System.out.println(("data.length=" + (data.length)));
            // write data to a file
            final Path foo = new Path(dir, ("foo" + n));
            {
                final FSDataOutputStream out = hdfs.create(foo, false, buffer_size, ((short) (2)), block_size);
                out.write(data);
                out.close();
            }
            // compute checksum
            final FileChecksum hdfsfoocs = hdfs.getFileChecksum(foo);
            System.out.println(("hdfsfoocs=" + hdfsfoocs));
            // webhdfs
            final FileChecksum webhdfsfoocs = webhdfs.getFileChecksum(foo);
            System.out.println(("webhdfsfoocs=" + webhdfsfoocs));
            final Path webhdfsqualified = new Path((webhdfsuri + dir), ("foo" + n));
            final FileChecksum webhdfs_qfoocs = webhdfs.getFileChecksum(webhdfsqualified);
            System.out.println(("webhdfs_qfoocs=" + webhdfs_qfoocs));
            // create a zero byte file
            final Path zeroByteFile = new Path(dir, ("zeroByteFile" + n));
            {
                final FSDataOutputStream out = hdfs.create(zeroByteFile, false, buffer_size, ((short) (2)), block_size);
                out.close();
            }
            // write another file
            final Path bar = new Path(dir, ("bar" + n));
            {
                final FSDataOutputStream out = hdfs.create(bar, false, buffer_size, ((short) (2)), block_size);
                out.write(data);
                out.close();
            }
            {
                final FileChecksum zeroChecksum = hdfs.getFileChecksum(zeroByteFile);
                final String magicValue = "MD5-of-0MD5-of-0CRC32:70bc8f4b72a86921468bf8e8441dce51";
                // verify the magic val for zero byte files
                Assert.assertEquals(magicValue, zeroChecksum.toString());
                // verify checksums for empty file and 0 request length
                final FileChecksum checksumWith0 = hdfs.getFileChecksum(bar, 0);
                Assert.assertEquals(zeroChecksum, checksumWith0);
                // verify checksum
                final FileChecksum barcs = hdfs.getFileChecksum(bar);
                final int barhashcode = barcs.hashCode();
                Assert.assertEquals(hdfsfoocs.hashCode(), barhashcode);
                Assert.assertEquals(hdfsfoocs, barcs);
                // webhdfs
                Assert.assertEquals(webhdfsfoocs.hashCode(), barhashcode);
                Assert.assertEquals(webhdfsfoocs, barcs);
                Assert.assertEquals(webhdfs_qfoocs.hashCode(), barhashcode);
                Assert.assertEquals(webhdfs_qfoocs, barcs);
            }
            hdfs.setPermission(dir, new FsPermission(((short) (0))));
            {
                // test permission error on webhdfs
                try {
                    webhdfs.getFileChecksum(webhdfsqualified);
                    Assert.fail();
                } catch (IOException ioe) {
                    FileSystem.LOG.info("GOOD: getting an exception", ioe);
                }
            }
            hdfs.setPermission(dir, new FsPermission(((short) (511))));
        }
        cluster.shutdown();
    }

    @Test
    public void testAllWithDualPort() throws Exception {
        dualPortTesting = true;
        try {
            testFileSystemCloseAll();
            testDFSClose();
            testDFSClient();
            testFileChecksum();
        } finally {
            dualPortTesting = false;
        }
    }

    @Test
    public void testAllWithNoXmlDefaults() throws Exception {
        // Do all the tests with a configuration that ignores the defaults in
        // the XML files.
        noXmlDefaults = true;
        try {
            testFileSystemCloseAll();
            testDFSClose();
            testDFSClient();
            testFileChecksum();
        } finally {
            noXmlDefaults = false;
        }
    }

    @Test(timeout = 120000)
    public void testLocatedFileStatusStorageIdsTypes() throws Exception {
        final Configuration conf = getTestConfiguration();
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(3).build();
        try {
            final DistributedFileSystem fs = cluster.getFileSystem();
            final Path testFile = new Path("/testListLocatedStatus");
            final int blockSize = 4096;
            final int numBlocks = 10;
            // Create a test file
            final int repl = 2;
            DFSTestUtil.createFile(fs, testFile, blockSize, (numBlocks * blockSize), blockSize, ((short) (repl)), 712173);
            DFSTestUtil.waitForReplication(fs, testFile, ((short) (repl)), 30000);
            // Get the listing
            RemoteIterator<LocatedFileStatus> it = fs.listLocatedStatus(testFile);
            Assert.assertTrue("Expected file to be present", it.hasNext());
            LocatedFileStatus stat = it.next();
            BlockLocation[] locs = stat.getBlockLocations();
            Assert.assertEquals("Unexpected number of locations", numBlocks, locs.length);
            Set<String> dnStorageIds = new HashSet<>();
            for (DataNode d : cluster.getDataNodes()) {
                try (FsDatasetSpi.FsVolumeReferences volumes = d.getFSDataset().getFsVolumeReferences()) {
                    for (FsVolumeSpi vol : volumes) {
                        dnStorageIds.add(vol.getStorageID());
                    }
                }
            }
            for (BlockLocation loc : locs) {
                String[] ids = loc.getStorageIds();
                // Run it through a set to deduplicate, since there should be no dupes
                Set<String> storageIds = new HashSet<>();
                Collections.addAll(storageIds, ids);
                Assert.assertEquals("Unexpected num storage ids", repl, storageIds.size());
                // Make sure these are all valid storage IDs
                Assert.assertTrue("Unknown storage IDs found!", dnStorageIds.containsAll(storageIds));
                // Check storage types are the default, since we didn't set any
                StorageType[] types = loc.getStorageTypes();
                Assert.assertEquals("Unexpected num storage types", repl, types.length);
                for (StorageType t : types) {
                    Assert.assertEquals("Unexpected storage type", DEFAULT, t);
                }
            }
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test
    public void testCreateWithCustomChecksum() throws Exception {
        Configuration conf = getTestConfiguration();
        MiniDFSCluster cluster = null;
        Path testBasePath = new Path("/test/csum");
        // create args
        Path path1 = new Path(testBasePath, "file_wtih_crc1");
        Path path2 = new Path(testBasePath, "file_with_crc2");
        ChecksumOpt opt1 = new ChecksumOpt(Type.CRC32C, 512);
        ChecksumOpt opt2 = new ChecksumOpt(Type.CRC32, 512);
        // common args
        FsPermission perm = FsPermission.getDefault().applyUMask(FsPermission.getUMask(conf));
        EnumSet<CreateFlag> flags = EnumSet.of(OVERWRITE, CreateFlag.CREATE);
        short repl = 1;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
            FileSystem dfs = cluster.getFileSystem();
            dfs.mkdirs(testBasePath);
            // create two files with different checksum types
            FSDataOutputStream out1 = dfs.create(path1, perm, flags, 4096, repl, 131072L, null, opt1);
            FSDataOutputStream out2 = dfs.create(path2, perm, flags, 4096, repl, 131072L, null, opt2);
            for (int i = 0; i < 1024; i++) {
                out1.write(i);
                out2.write(i);
            }
            out1.close();
            out2.close();
            // the two checksums must be different.
            MD5MD5CRC32FileChecksum sum1 = ((MD5MD5CRC32FileChecksum) (dfs.getFileChecksum(path1)));
            MD5MD5CRC32FileChecksum sum2 = ((MD5MD5CRC32FileChecksum) (dfs.getFileChecksum(path2)));
            Assert.assertFalse(sum1.equals(sum2));
            // check the individual params
            Assert.assertEquals(CRC32C, sum1.getCrcType());
            Assert.assertEquals(CRC32, sum2.getCrcType());
        } finally {
            if (cluster != null) {
                cluster.getFileSystem().delete(testBasePath, true);
                cluster.shutdown();
            }
        }
    }

    @Test(timeout = 60000)
    public void testFileCloseStatus() throws IOException {
        Configuration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).build();
        DistributedFileSystem fs = cluster.getFileSystem();
        try {
            // create a new file.
            Path file = new Path("/simpleFlush.dat");
            FSDataOutputStream output = fs.create(file);
            // write to file
            output.writeBytes("Some test data");
            output.flush();
            Assert.assertFalse("File status should be open", fs.isFileClosed(file));
            output.close();
            Assert.assertTrue("File status should be closed", fs.isFileClosed(file));
        } finally {
            cluster.shutdown();
        }
    }

    @Test
    public void testCreateWithStoragePolicy() throws Throwable {
        Configuration conf = new HdfsConfiguration();
        try (MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).storageTypes(new StorageType[]{ StorageType.DISK, StorageType.ARCHIVE, StorageType.SSD }).storagesPerDatanode(3).build()) {
            DistributedFileSystem fs = cluster.getFileSystem();
            Path file1 = new Path("/tmp/file1");
            Path file2 = new Path("/tmp/file2");
            fs.mkdirs(new Path("/tmp"));
            fs.setStoragePolicy(new Path("/tmp"), "ALL_SSD");
            FSDataOutputStream outputStream = fs.createFile(file1).storagePolicyName("COLD").build();
            outputStream.write(1);
            outputStream.close();
            Assert.assertEquals(ARCHIVE, DFSTestUtil.getAllBlocks(fs, file1).get(0).getStorageTypes()[0]);
            Assert.assertEquals(fs.getStoragePolicy(file1).getName(), "COLD");
            // Check with storage policy not specified.
            outputStream = fs.createFile(file2).build();
            outputStream.write(1);
            outputStream.close();
            Assert.assertEquals(SSD, DFSTestUtil.getAllBlocks(fs, file2).get(0).getStorageTypes()[0]);
            Assert.assertEquals(fs.getStoragePolicy(file2).getName(), "ALL_SSD");
            // Check with default storage policy.
            outputStream = fs.createFile(new Path("/default")).build();
            outputStream.write(1);
            outputStream.close();
            Assert.assertEquals(DISK, DFSTestUtil.getAllBlocks(fs, new Path("/default")).get(0).getStorageTypes()[0]);
            Assert.assertEquals(fs.getStoragePolicy(new Path("/default")).getName(), "HOT");
        }
    }

    @Test(timeout = 60000)
    public void testListFiles() throws IOException {
        Configuration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).build();
        try {
            DistributedFileSystem fs = cluster.getFileSystem();
            final Path relative = new Path("relative");
            fs.create(new Path(relative, "foo")).close();
            final List<LocatedFileStatus> retVal = new ArrayList<>();
            final RemoteIterator<LocatedFileStatus> iter = fs.listFiles(relative, true);
            while (iter.hasNext()) {
                retVal.add(iter.next());
            } 
            System.out.println(("retVal = " + retVal));
        } finally {
            cluster.shutdown();
        }
    }

    @Test
    public void testListStatusOfSnapshotDirs() throws IOException {
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(new HdfsConfiguration()).build();
        try {
            DistributedFileSystem dfs = cluster.getFileSystem();
            dfs.create(new Path("/parent/test1/dfsclose/file-0"));
            Path snapShotDir = new Path("/parent/test1/");
            dfs.allowSnapshot(snapShotDir);
            FileStatus status = dfs.getFileStatus(new Path("/parent/test1"));
            Assert.assertTrue(status.isSnapshotEnabled());
            status = dfs.getFileStatus(new Path("/parent/"));
            Assert.assertFalse(status.isSnapshotEnabled());
        } finally {
            cluster.shutdown();
        }
    }

    @Test(timeout = 10000)
    public void testDFSClientPeerReadTimeout() throws IOException {
        final int timeout = 1000;
        final Configuration conf = new HdfsConfiguration();
        conf.setInt(DFS_CLIENT_SOCKET_TIMEOUT_KEY, timeout);
        // only need cluster to create a dfs client to get a peer
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).build();
        try {
            cluster.waitActive();
            DistributedFileSystem dfs = cluster.getFileSystem();
            // use a dummy socket to ensure the read timesout
            ServerSocket socket = new ServerSocket(0);
            Peer peer = dfs.getClient().newConnectedPeer(((InetSocketAddress) (socket.getLocalSocketAddress())), null, null);
            long start = Time.now();
            try {
                peer.getInputStream().read();
                Assert.fail("read should timeout");
            } catch (SocketTimeoutException ste) {
                long delta = (Time.now()) - start;
                if (delta < (timeout * 0.9)) {
                    throw new IOException((("read timedout too soon in " + delta) + " ms."), ste);
                }
                if (delta > (timeout * 1.1)) {
                    throw new IOException((("read timedout too late in " + delta) + " ms."), ste);
                }
            }
        } finally {
            cluster.shutdown();
        }
    }

    @Test(timeout = 60000)
    public void testGetServerDefaults() throws IOException {
        Configuration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).build();
        try {
            cluster.waitActive();
            DistributedFileSystem dfs = cluster.getFileSystem();
            FsServerDefaults fsServerDefaults = dfs.getServerDefaults();
            Assert.assertNotNull(fsServerDefaults);
        } finally {
            cluster.shutdown();
        }
    }

    @Test(timeout = 10000)
    public void testDFSClientPeerWriteTimeout() throws IOException {
        final int timeout = 1000;
        final Configuration conf = new HdfsConfiguration();
        conf.setInt(DFS_CLIENT_SOCKET_TIMEOUT_KEY, timeout);
        // only need cluster to create a dfs client to get a peer
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).build();
        try {
            cluster.waitActive();
            DistributedFileSystem dfs = cluster.getFileSystem();
            // Write 10 MB to a dummy socket to ensure the write times out
            ServerSocket socket = new ServerSocket(0);
            Peer peer = dfs.getClient().newConnectedPeer(((InetSocketAddress) (socket.getLocalSocketAddress())), null, null);
            long start = Time.now();
            try {
                byte[] buf = new byte[(10 * 1024) * 1024];
                peer.getOutputStream().write(buf);
                long delta = (Time.now()) - start;
                Assert.fail(((("write finish in " + delta) + " ms") + "but should timedout"));
            } catch (SocketTimeoutException ste) {
                long delta = (Time.now()) - start;
                if (delta < (timeout * 0.9)) {
                    throw new IOException((("write timedout too soon in " + delta) + " ms."), ste);
                }
                if (delta > (timeout * 1.2)) {
                    throw new IOException((("write timedout too late in " + delta) + " ms."), ste);
                }
            }
        } finally {
            cluster.shutdown();
        }
    }

    @Test(timeout = 30000)
    public void testTotalDfsUsed() throws Exception {
        Configuration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
            FileSystem fs = cluster.getFileSystem();
            // create file under root
            FSDataOutputStream File1 = fs.create(new Path("/File1"));
            File1.write("hi".getBytes());
            File1.close();
            // create file under sub-folder
            FSDataOutputStream File2 = fs.create(new Path("/Folder1/File2"));
            File2.write("hi".getBytes());
            File2.close();
            // getUsed(Path) should return total len of all the files from a path
            Assert.assertEquals(2, fs.getUsed(new Path("/Folder1")));
            // getUsed() should return total length of all files in filesystem
            Assert.assertEquals(4, fs.getUsed());
        } finally {
            if (cluster != null) {
                cluster.shutdown();
                cluster = null;
            }
        }
    }

    @Test
    public void testDFSCloseFilesBeingWritten() throws Exception {
        Configuration conf = getTestConfiguration();
        MiniDFSCluster cluster = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
            DistributedFileSystem fileSys = cluster.getFileSystem();
            // Create one file then delete it to trigger the FileNotFoundException
            // when closing the file.
            fileSys.create(new Path("/test/dfsclose/file-0"));
            fileSys.delete(new Path("/test/dfsclose/file-0"), true);
            DFSClient dfsClient = fileSys.getClient();
            // Construct a new dfsClient to get the same LeaseRenewer instance,
            // to avoid the original client being added to the leaseRenewer again.
            DFSClient newDfsClient = new DFSClient(cluster.getFileSystem(0).getUri(), conf);
            LeaseRenewer leaseRenewer = newDfsClient.getLeaseRenewer();
            dfsClient.closeAllFilesBeingWritten(false);
            // Remove new dfsClient in leaseRenewer
            leaseRenewer.closeClient(newDfsClient);
            // The list of clients corresponding to this renewer should be empty
            Assert.assertEquals(true, leaseRenewer.isEmpty());
            Assert.assertEquals(true, dfsClient.isFilesBeingWrittenEmpty());
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test
    public void testHdfsDataOutputStreamBuilderSetParameters() throws IOException {
        Configuration conf = getTestConfiguration();
        try (MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build()) {
            cluster.waitActive();
            DistributedFileSystem fs = cluster.getFileSystem();
            testBuilderSetters(fs);
        }
    }

    @Test
    public void testDFSDataOutputStreamBuilderForCreation() throws Exception {
        Configuration conf = getTestConfiguration();
        String testFile = "/testDFSDataOutputStreamBuilder";
        Path testFilePath = new Path(testFile);
        try (MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build()) {
            DistributedFileSystem fs = cluster.getFileSystem();
            // Before calling build(), no change was made in the file system
            HdfsDataOutputStreamBuilder builder = fs.createFile(testFilePath).blockSize(4096).replication(((short) (1)));
            Assert.assertFalse(fs.exists(testFilePath));
            // Test create an empty file
            try (FSDataOutputStream out = fs.createFile(testFilePath).build()) {
                TestDistributedFileSystem.LOG.info("Test create an empty file");
            }
            // Test create a file with content, and verify the content
            String content = "This is a test!";
            try (FSDataOutputStream out1 = fs.createFile(testFilePath).bufferSize(4096).replication(((short) (1))).blockSize(4096).build()) {
                byte[] contentOrigin = content.getBytes("UTF8");
                out1.write(contentOrigin);
            }
            ContractTestUtils.verifyFileContents(fs, testFilePath, content.getBytes());
            try (FSDataOutputStream out = fs.createFile(testFilePath).overwrite(false).build()) {
                Assert.fail("it should fail to overwrite an existing file");
            } catch (FileAlreadyExistsException e) {
                // As expected, ignore.
            }
            Path nonParentFile = new Path("/parent/test");
            try (FSDataOutputStream out = fs.createFile(nonParentFile).build()) {
                Assert.fail("parent directory not exist");
            } catch (FileNotFoundException e) {
                // As expected.
            }
            Assert.assertFalse("parent directory should not be created", fs.exists(new Path("/parent")));
            try (FSDataOutputStream out = fs.createFile(nonParentFile).recursive().build()) {
                out.write(1);
            }
            Assert.assertTrue("parent directory has not been created", fs.exists(new Path("/parent")));
        }
    }

    @Test
    public void testDFSDataOutputStreamBuilderForAppend() throws IOException {
        Configuration conf = getTestConfiguration();
        String testFile = "/testDFSDataOutputStreamBuilderForAppend";
        Path path = new Path(testFile);
        Random random = new Random();
        try (MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build()) {
            DistributedFileSystem fs = cluster.getFileSystem();
            byte[] buf = new byte[16];
            random.nextBytes(buf);
            try (FSDataOutputStream out = fs.appendFile(path).build()) {
                out.write(buf);
                Assert.fail("should fail on appending to non-existent file");
            } catch (IOException e) {
                GenericTestUtils.assertExceptionContains("non-existent", e);
            }
            random.nextBytes(buf);
            try (FSDataOutputStream out = fs.createFile(path).build()) {
                out.write(buf);
            }
            random.nextBytes(buf);
            try (FSDataOutputStream out = fs.appendFile(path).build()) {
                out.write(buf);
            }
            FileStatus status = fs.getFileStatus(path);
            Assert.assertEquals((16 * 2), status.getLen());
        }
    }

    @Test
    public void testRemoveErasureCodingPolicy() throws Exception {
        Configuration conf = getTestConfiguration();
        MiniDFSCluster cluster = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
            DistributedFileSystem fs = cluster.getFileSystem();
            ECSchema toAddSchema = new ECSchema("rs", 3, 2);
            ErasureCodingPolicy toAddPolicy = new ErasureCodingPolicy(toAddSchema, (128 * 1024), ((byte) (254)));
            String policyName = toAddPolicy.getName();
            ErasureCodingPolicy[] policies = new ErasureCodingPolicy[]{ toAddPolicy };
            fs.addErasureCodingPolicies(policies);
            Assert.assertEquals(policyName, ErasureCodingPolicyManager.getInstance().getByName(policyName).getName());
            fs.removeErasureCodingPolicy(policyName);
            Assert.assertEquals(policyName, ErasureCodingPolicyManager.getInstance().getRemovedPolicies().get(0).getName());
            // remove erasure coding policy as a user without privilege
            UserGroupInformation fakeUGI = UserGroupInformation.createUserForTesting("ProbablyNotARealUserName", new String[]{ "ShangriLa" });
            final MiniDFSCluster finalCluster = cluster;
            fakeUGI.doAs(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    DistributedFileSystem fs = finalCluster.getFileSystem();
                    try {
                        fs.removeErasureCodingPolicy(policyName);
                        Assert.fail();
                    } catch (AccessControlException ace) {
                        GenericTestUtils.assertExceptionContains(("Access denied for user " + "ProbablyNotARealUserName. Superuser privilege is required"), ace);
                    }
                    return null;
                }
            });
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test
    public void testEnableAndDisableErasureCodingPolicy() throws Exception {
        Configuration conf = getTestConfiguration();
        MiniDFSCluster cluster = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
            DistributedFileSystem fs = cluster.getFileSystem();
            ECSchema toAddSchema = new ECSchema("rs", 3, 2);
            ErasureCodingPolicy toAddPolicy = new ErasureCodingPolicy(toAddSchema, (128 * 1024), ((byte) (254)));
            String policyName = toAddPolicy.getName();
            ErasureCodingPolicy[] policies = new ErasureCodingPolicy[]{ toAddPolicy };
            fs.addErasureCodingPolicies(policies);
            Assert.assertEquals(policyName, ErasureCodingPolicyManager.getInstance().getByName(policyName).getName());
            fs.disableErasureCodingPolicy(policyName);
            fs.enableErasureCodingPolicy(policyName);
            Assert.assertEquals(policyName, ErasureCodingPolicyManager.getInstance().getByName(policyName).getName());
            // test enable a policy that doesn't exist
            try {
                fs.enableErasureCodingPolicy("notExistECName");
                Assert.fail("enable the policy that doesn't exist should fail");
            } catch (Exception e) {
                GenericTestUtils.assertExceptionContains("does not exist", e);
                // pass
            }
            // test disable a policy that doesn't exist
            try {
                fs.disableErasureCodingPolicy("notExistECName");
                Assert.fail("disable the policy that doesn't exist should fail");
            } catch (Exception e) {
                GenericTestUtils.assertExceptionContains("does not exist", e);
                // pass
            }
            // disable and enable erasure coding policy as a user without privilege
            UserGroupInformation fakeUGI = UserGroupInformation.createUserForTesting("ProbablyNotARealUserName", new String[]{ "ShangriLa" });
            final MiniDFSCluster finalCluster = cluster;
            fakeUGI.doAs(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    DistributedFileSystem fs = finalCluster.getFileSystem();
                    try {
                        fs.disableErasureCodingPolicy(policyName);
                        Assert.fail();
                    } catch (AccessControlException ace) {
                        GenericTestUtils.assertExceptionContains(("Access denied for user " + "ProbablyNotARealUserName. Superuser privilege is required"), ace);
                    }
                    try {
                        fs.enableErasureCodingPolicy(policyName);
                        Assert.fail();
                    } catch (AccessControlException ace) {
                        GenericTestUtils.assertExceptionContains(("Access denied for user " + "ProbablyNotARealUserName. Superuser privilege is required"), ace);
                    }
                    return null;
                }
            });
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }
}

