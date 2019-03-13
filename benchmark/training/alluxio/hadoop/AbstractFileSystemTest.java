/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.hadoop;


import PropertyKey.MASTER_HOSTNAME;
import PropertyKey.MASTER_JOURNAL_TYPE;
import PropertyKey.MASTER_RPC_ADDRESSES;
import PropertyKey.MASTER_RPC_PORT;
import PropertyKey.ZOOKEEPER_ADDRESS;
import PropertyKey.ZOOKEEPER_ENABLED;
import alluxio.ConfigurationTestUtils;
import alluxio.Constants;
import alluxio.SystemPropertyRule;
import alluxio.client.block.AlluxioBlockStore;
import alluxio.client.file.FileSystemContext;
import alluxio.client.file.FileSystemMasterClient;
import alluxio.conf.InstancedConfiguration;
import alluxio.conf.PropertyKey;
import alluxio.util.ConfigurationUtils;
import alluxio.wire.FileInfo;
import alluxio.wire.WorkerNetAddress;
import com.google.common.collect.Lists;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Unit tests for {@link AbstractFileSystem}.
 */
/* [ALLUXIO-1384] Tell PowerMock to defer the loading of javax.security classes to the system
classloader in order to avoid linkage error when running this test with CDH.
See https://code.google.com/p/powermock/wiki/FAQ.
 */
/**
 * Tests for {@link AbstractFileSystem}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AlluxioBlockStore.class, FileSystemContext.class, FileSystemMasterClient.class, UserGroupInformation.class })
@PowerMockIgnore("javax.security.*")
public class AbstractFileSystemTest {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractFileSystemTest.class);

    private InstancedConfiguration mConfiguration = ConfigurationTestUtils.defaults();

    private FileSystemContext mMockFileSystemContext = Mockito.mock(FileSystemContext.class);

    @Rule
    public ExpectedException mExpectedException = ExpectedException.none();

    @Test
    public void hadoopShouldLoadFaultTolerantFileSystemWhenConfigured() throws Exception {
        URI uri = URI.create(((Constants.HEADER_FT) + "localhost:19998/tmp/path.txt"));
        try (Closeable c = toResource()) {
            final FileSystem fs = org.apache.hadoop.fs.FileSystem.get(uri, getConf());
            Assert.assertTrue((fs instanceof FaultTolerantFileSystem));
        }
    }

    @Test
    public void hadoopShouldLoadFileSystemWithSingleZkUri() throws Exception {
        Configuration conf = getConf();
        URI uri = URI.create(((Constants.HEADER) + "zk@zkHost:2181/tmp/path.txt"));
        FileSystem hfs = getHadoopFilesystem(org.apache.hadoop.fs.FileSystem.get(uri, conf));
        Assert.assertTrue(hfs.mFileSystem.getConf().getBoolean(ZOOKEEPER_ENABLED));
        Assert.assertEquals("zkHost:2181", hfs.mFileSystem.getConf().get(ZOOKEEPER_ADDRESS));
    }

    @Test
    public void hadoopShouldLoadFileSystemWithMultipleZkUri() throws Exception {
        Configuration conf = getConf();
        URI uri = URI.create(((Constants.HEADER) + "zk@host1:2181,host2:2181,host3:2181/tmp/path.txt"));
        FileSystem fs = org.apache.hadoop.fs.FileSystem.get(uri, conf);
        FileSystem hfs = getHadoopFilesystem(fs);
        Assert.assertTrue(hfs.mFileSystem.getConf().getBoolean(ZOOKEEPER_ENABLED));
        Assert.assertEquals("host1:2181,host2:2181,host3:2181", hfs.mFileSystem.getConf().get(ZOOKEEPER_ADDRESS));
        uri = URI.create(((Constants.HEADER) + "zk@host1:2181;host2:2181;host3:2181/tmp/path.txt"));
        fs = getHadoopFilesystem(org.apache.hadoop.fs.FileSystem.get(uri, conf));
        Assert.assertTrue(hfs.mFileSystem.getConf().getBoolean(ZOOKEEPER_ENABLED));
        Assert.assertEquals("host1:2181,host2:2181,host3:2181", hfs.mFileSystem.getConf().get(ZOOKEEPER_ADDRESS));
    }

    @Test
    public void fsShouldSetPropertyConfWithMultiMasterUri() throws Exception {
        URI uri = URI.create("alluxio://host1:19998,host2:19998,host3:19998/path");
        AbstractFileSystem afs = new alluxio.hadoop.FileSystem();
        afs.initialize(uri, getConf());
        Assert.assertFalse(afs.mFileSystem.getConf().getBoolean(ZOOKEEPER_ENABLED));
        Assert.assertEquals("host1:19998,host2:19998,host3:19998", afs.mFileSystem.getConf().get(MASTER_RPC_ADDRESSES));
        uri = URI.create("alluxio://host1:19998;host2:19998;host3:19998/path");
        afs = new FileSystem();
        afs.initialize(uri, getConf());
        Assert.assertFalse(afs.mFileSystem.getConf().getBoolean(ZOOKEEPER_ENABLED));
        Assert.assertEquals("host1:19998,host2:19998,host3:19998", afs.mFileSystem.getConf().get(MASTER_RPC_ADDRESSES));
    }

    @Test
    public void hadoopShouldLoadFsWithMultiMasterUri() throws Exception {
        URI uri = URI.create("alluxio://host1:19998,host2:19998,host3:19998/path");
        FileSystem fs = org.apache.hadoop.fs.FileSystem.get(uri, getConf());
        Assert.assertTrue((fs instanceof FileSystem));
        uri = URI.create("alluxio://host1:19998;host2:19998;host3:19998/path");
        fs = org.apache.hadoop.fs.FileSystem.get(uri, getConf());
        Assert.assertTrue((fs instanceof FileSystem));
    }

    /**
     * Hadoop should be able to load uris like alluxio-ft:///path/to/file.
     */
    @Test
    public void loadFaultTolerantSystemWhenUsingNoAuthority() throws Exception {
        URI uri = URI.create(((Constants.HEADER_FT) + "/tmp/path.txt"));
        try (Closeable c = toResource()) {
            final FileSystem fs = org.apache.hadoop.fs.FileSystem.get(uri, getConf());
            Assert.assertTrue((fs instanceof FaultTolerantFileSystem));
        }
    }

    /**
     * Tests that using an alluxio-ft:/// URI is still possible after using an alluxio://host:port/
     * URI.
     */
    @Test
    public void loadRegularThenFaultTolerant() throws Exception {
        try (Closeable c = toResource()) {
            org.apache.hadoop.fs.FileSystem.get(URI.create(((Constants.HEADER) + "host:1/")), getConf());
            FileSystem fs = org.apache.hadoop.fs.FileSystem.get(URI.create(((Constants.HEADER_FT) + "/")), getConf());
            Assert.assertTrue((fs instanceof FaultTolerantFileSystem));
        }
    }

    @Test
    public void hadoopShouldLoadFileSystemWhenConfigured() throws Exception {
        Configuration conf = getConf();
        URI uri = URI.create(((Constants.HEADER) + "localhost:19998/tmp/path.txt"));
        Map<PropertyKey, String> properties = new HashMap<>();
        properties.put(MASTER_HOSTNAME, uri.getHost());
        properties.put(MASTER_RPC_PORT, Integer.toString(uri.getPort()));
        properties.put(ZOOKEEPER_ENABLED, "false");
        properties.put(ZOOKEEPER_ADDRESS, null);
        try (Closeable c = toResource()) {
            final FileSystem fs = org.apache.hadoop.fs.FileSystem.get(uri, conf);
            Assert.assertTrue((fs instanceof FileSystem));
        }
    }

    @Test
    public void resetContextUsingZookeeperUris() throws Exception {
        // Change to signle zookeeper uri
        URI uri = URI.create(((Constants.HEADER) + "zk@zkHost:2181/"));
        FileSystem fs = getHadoopFilesystem(org.apache.hadoop.fs.FileSystem.get(uri, getConf()));
        Assert.assertTrue(fs.mFileSystem.getConf().getBoolean(ZOOKEEPER_ENABLED));
        Assert.assertEquals("zkHost:2181", fs.mFileSystem.getConf().get(ZOOKEEPER_ADDRESS));
        uri = URI.create(((Constants.HEADER) + "zk@host1:2181,host2:2181,host3:2181/tmp/path.txt"));
        fs = getHadoopFilesystem(org.apache.hadoop.fs.FileSystem.get(uri, getConf()));
        Assert.assertTrue(fs.mFileSystem.getConf().getBoolean(ZOOKEEPER_ENABLED));
        Assert.assertEquals("host1:2181,host2:2181,host3:2181", fs.mFileSystem.getConf().get(ZOOKEEPER_ADDRESS));
        uri = URI.create(((Constants.HEADER) + "zk@host1:2181;host2:2181;host3:2181/tmp/path.txt"));
        fs = getHadoopFilesystem(org.apache.hadoop.fs.FileSystem.get(uri, getConf()));
        Assert.assertTrue(fs.mFileSystem.getConf().getBoolean(ZOOKEEPER_ENABLED));
        Assert.assertEquals("host1:2181,host2:2181,host3:2181", fs.mFileSystem.getConf().get(ZOOKEEPER_ADDRESS));
    }

    @Test
    public void resetContextFromZkUriToNonZkUri() throws Exception {
        Configuration conf = getConf();
        URI uri = URI.create(((Constants.HEADER) + "zk@zkHost:2181/tmp/path.txt"));
        FileSystem fs = getHadoopFilesystem(org.apache.hadoop.fs.FileSystem.get(uri, conf));
        Assert.assertTrue(fs.mFileSystem.getConf().getBoolean(ZOOKEEPER_ENABLED));
        Assert.assertEquals("zkHost:2181", fs.mFileSystem.getConf().get(ZOOKEEPER_ADDRESS));
        URI otherUri = URI.create(((Constants.HEADER) + "alluxioHost:19998/tmp/path.txt"));
        fs = getHadoopFilesystem(org.apache.hadoop.fs.FileSystem.get(otherUri, conf));
        Assert.assertEquals("alluxioHost", fs.mFileSystem.getConf().get(MASTER_HOSTNAME));
        Assert.assertEquals("19998", fs.mFileSystem.getConf().get(MASTER_RPC_PORT));
        Assert.assertFalse(fs.mFileSystem.getConf().getBoolean(ZOOKEEPER_ENABLED));
        Assert.assertFalse(fs.mFileSystem.getConf().isSet(ZOOKEEPER_ADDRESS));
    }

    @Test
    public void resetContextUsingMultiMasterUris() throws Exception {
        // Change to multi-master uri
        URI uri = URI.create(((Constants.HEADER) + "host1:19998,host2:19998,host3:19998/tmp/path.txt"));
        FileSystem fs = getHadoopFilesystem(org.apache.hadoop.fs.FileSystem.get(uri, getConf()));
        Assert.assertFalse(fs.mFileSystem.getConf().getBoolean(ZOOKEEPER_ENABLED));
        Assert.assertEquals("host1:19998,host2:19998,host3:19998", fs.mFileSystem.getConf().get(MASTER_RPC_ADDRESSES));
    }

    @Test
    public void resetContextFromZookeeperToMultiMaster() throws Exception {
        URI uri = URI.create(((Constants.HEADER) + "zk@zkHost:2181/tmp/path.txt"));
        FileSystem fs = getHadoopFilesystem(org.apache.hadoop.fs.FileSystem.get(uri, getConf()));
        Assert.assertTrue(fs.mFileSystem.getConf().getBoolean(ZOOKEEPER_ENABLED));
        Assert.assertEquals("zkHost:2181", fs.mFileSystem.getConf().get(ZOOKEEPER_ADDRESS));
        uri = URI.create(((Constants.HEADER) + "host1:19998,host2:19998,host3:19998/tmp/path.txt"));
        fs = getHadoopFilesystem(org.apache.hadoop.fs.FileSystem.get(uri, getConf()));
        Assert.assertFalse(fs.mFileSystem.getConf().getBoolean(ZOOKEEPER_ENABLED));
        Assert.assertEquals(3, ConfigurationUtils.getMasterRpcAddresses(fs.mFileSystem.getConf()).size());
        Assert.assertEquals("host1:19998,host2:19998,host3:19998", fs.mFileSystem.getConf().get(MASTER_RPC_ADDRESSES));
    }

    @Test
    public void resetContextFromMultiMasterToSingleMaster() throws Exception {
        URI uri = URI.create(((Constants.HEADER) + "host1:19998,host2:19998,host3:19998/tmp/path.txt"));
        FileSystem fs = getHadoopFilesystem(org.apache.hadoop.fs.FileSystem.get(uri, getConf()));
        Assert.assertFalse(fs.mFileSystem.getConf().getBoolean(ZOOKEEPER_ENABLED));
        Assert.assertEquals(3, ConfigurationUtils.getMasterRpcAddresses(fs.mFileSystem.getConf()).size());
        Assert.assertEquals("host1:19998,host2:19998,host3:19998", fs.mFileSystem.getConf().get(MASTER_RPC_ADDRESSES));
        uri = URI.create(((Constants.HEADER) + "host:19998/tmp/path.txt"));
        fs = getHadoopFilesystem(org.apache.hadoop.fs.FileSystem.get(uri, getConf()));
        Assert.assertFalse(fs.mFileSystem.getConf().getBoolean(ZOOKEEPER_ENABLED));
        Assert.assertEquals(MASTER_JOURNAL_TYPE.getDefaultValue(), fs.mFileSystem.getConf().get(MASTER_JOURNAL_TYPE));
        Assert.assertEquals(1, ConfigurationUtils.getMasterRpcAddresses(fs.mFileSystem.getConf()).size());
    }

    /**
     * Verifies that the initialize method is only called once even when there are many concurrent
     * initializers during the initialization phase.
     */
    @Test
    public void concurrentInitialize() throws Exception {
        List<Thread> threads = new ArrayList<>();
        final Configuration conf = getConf();
        for (int i = 0; i < 100; i++) {
            Thread t = new Thread(() -> {
                URI uri = URI.create(((Constants.HEADER) + "randomhost:410/"));
                try {
                    org.apache.hadoop.fs.FileSystem.get(uri, conf);
                } catch (IOException e) {
                    Assert.fail();
                }
            });
            threads.add(t);
        }
        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }
    }

    /**
     * Tests that after initialization, reinitialize with a different URI.
     */
    @Test
    public void reinitializeWithDifferentURI() throws Exception {
        Configuration conf = getConf();
        URI uri = URI.create("alluxio://host1:1");
        FileSystem fs = getHadoopFilesystem(org.apache.hadoop.fs.FileSystem.get(uri, conf));
        Assert.assertEquals("host1", fs.mFileSystem.getConf().get(MASTER_HOSTNAME));
        Assert.assertEquals("1", fs.mFileSystem.getConf().get(MASTER_RPC_PORT));
        uri = URI.create("alluxio://host2:2");
        fs = getHadoopFilesystem(org.apache.hadoop.fs.FileSystem.get(uri, conf));
        Assert.assertEquals("host2", fs.mFileSystem.getConf().get(MASTER_HOSTNAME));
        Assert.assertEquals("2", fs.mFileSystem.getConf().get(MASTER_RPC_PORT));
    }

    /**
     * Tests that the {@link AbstractFileSystem#listStatus(Path)} method uses
     * {@link URIStatus#getLastModificationTimeMs()} correctly.
     */
    @Test
    public void listStatus() throws Exception {
        FileInfo fileInfo1 = new FileInfo().setLastModificationTimeMs(111L).setFolder(false).setOwner("user1").setGroup("group1").setMode(493);
        FileInfo fileInfo2 = new FileInfo().setLastModificationTimeMs(222L).setFolder(true).setOwner("user2").setGroup("group2").setMode(420);
        Path path = new Path("/dir");
        alluxio.client.file.FileSystem alluxioFs = Mockito.mock(alluxio.client.file.FileSystem.class);
        Mockito.when(alluxioFs.listStatus(new alluxio.AlluxioURI(HadoopUtils.getPathWithoutScheme(path)))).thenReturn(Lists.newArrayList(new alluxio.client.file.URIStatus(fileInfo1), new alluxio.client.file.URIStatus(fileInfo2)));
        FileSystem alluxioHadoopFs = new FileSystem(alluxioFs);
        FileStatus[] fileStatuses = alluxioHadoopFs.listStatus(path);
        assertFileInfoEqualsFileStatus(fileInfo1, fileStatuses[0]);
        assertFileInfoEqualsFileStatus(fileInfo2, fileStatuses[1]);
        alluxioHadoopFs.close();
    }

    /**
     * Tests that the {@link AbstractFileSystem#listStatus(Path)} method throws
     * FileNotFound Exception.
     */
    @Test
    public void throwFileNotFoundExceptionWhenListStatusNonExistingTest() throws Exception {
        FileSystem alluxioHadoopFs = null;
        try {
            Path path = new Path("/ALLUXIO-2036");
            alluxio.client.file.FileSystem alluxioFs = Mockito.mock(alluxio.client.file.FileSystem.class);
            Mockito.when(alluxioFs.listStatus(new alluxio.AlluxioURI(HadoopUtils.getPathWithoutScheme(path)))).thenThrow(new FileNotFoundException("ALLUXIO-2036 not Found"));
            alluxioHadoopFs = new FileSystem(alluxioFs);
            FileStatus[] fileStatuses = alluxioHadoopFs.listStatus(path);
            // if we reach here, FileNotFoundException is not thrown hence Fail the test case
            Assert.assertTrue(false);
        } catch (FileNotFoundException fnf) {
            Assert.assertEquals("ALLUXIO-2036 not Found", fnf.getMessage());
        } finally {
            if (null != alluxioHadoopFs) {
                try {
                    alluxioHadoopFs.close();
                } catch (Exception ex) {
                    // nothing to catch, ignore it.
                }
            }
        }
    }

    @Test
    public void getStatus() throws Exception {
        FileInfo fileInfo = new FileInfo().setLastModificationTimeMs(111L).setFolder(false).setOwner("user1").setGroup("group1").setMode(493);
        Path path = new Path("/dir");
        alluxio.client.file.FileSystem alluxioFs = Mockito.mock(alluxio.client.file.FileSystem.class);
        Mockito.when(alluxioFs.getStatus(new alluxio.AlluxioURI(HadoopUtils.getPathWithoutScheme(path)))).thenReturn(new alluxio.client.file.URIStatus(fileInfo));
        FileSystem alluxioHadoopFs = new FileSystem(alluxioFs);
        FileStatus fileStatus = alluxioHadoopFs.getFileStatus(path);
        assertFileInfoEqualsFileStatus(fileInfo, fileStatus);
    }

    @Test
    public void initializeWithCustomizedUgi() throws Exception {
        mockUserGroupInformation("testuser");
        final Configuration conf = getConf();
        URI uri = URI.create(((Constants.HEADER) + "host:1"));
        org.apache.hadoop.fs.FileSystem.get(uri, conf);
        // FileSystem.create would have thrown an exception if the initialization failed.
    }

    @Test
    public void initializeWithFullPrincipalUgi() throws Exception {
        mockUserGroupInformation("testuser@ALLUXIO.COM");
        final Configuration conf = getConf();
        URI uri = URI.create(((Constants.HEADER) + "host:1"));
        org.apache.hadoop.fs.FileSystem.get(uri, conf);
        // FileSystem.create would have thrown an exception if the initialization failed.
    }

    @Test
    public void initializeWithZookeeperSystemProperties() throws Exception {
        HashMap<String, String> sysProps = new HashMap<>();
        sysProps.put(ZOOKEEPER_ENABLED.getName(), "true");
        sysProps.put(ZOOKEEPER_ADDRESS.getName(), "zkHost:2181");
        try (Closeable p = new SystemPropertyRule(sysProps).toResource()) {
            ConfigurationUtils.reloadProperties();
            URI uri = URI.create("alluxio:///");
            FileSystem fs = getHadoopFilesystem(org.apache.hadoop.fs.FileSystem.get(uri, getConf()));
            Assert.assertTrue(fs.mFileSystem.getConf().getBoolean(ZOOKEEPER_ENABLED));
            Assert.assertEquals("zkHost:2181", fs.mFileSystem.getConf().get(ZOOKEEPER_ADDRESS));
        }
    }

    @Test
    public void initializeWithZookeeperUriAndSystemProperty() throws Exception {
        // When URI and system property both have Zookeeper configuration,
        // those in the URI has the highest priority.
        try (Closeable p = toResource()) {
            ConfigurationUtils.reloadProperties();
            URI uri = URI.create("alluxio://zk@zkHost:2181");
            FileSystem fs = getHadoopFilesystem(org.apache.hadoop.fs.FileSystem.get(uri, getConf()));
            Assert.assertTrue(fs.mFileSystem.getConf().getBoolean(ZOOKEEPER_ENABLED));
            Assert.assertEquals("zkHost:2181", fs.mFileSystem.getConf().get(ZOOKEEPER_ADDRESS));
            fs.close();
        }
        HashMap<String, String> sysProps = new HashMap<>();
        sysProps.put(ZOOKEEPER_ENABLED.getName(), "true");
        sysProps.put(ZOOKEEPER_ADDRESS.getName(), "zkHost1:2181");
        try (Closeable p = new SystemPropertyRule(sysProps).toResource()) {
            ConfigurationUtils.reloadProperties();
            URI uri = URI.create("alluxio://zk@zkHost2:2181");
            FileSystem fs = getHadoopFilesystem(org.apache.hadoop.fs.FileSystem.get(uri, getConf()));
            Assert.assertTrue(fs.mFileSystem.getConf().getBoolean(ZOOKEEPER_ENABLED));
            Assert.assertEquals("zkHost2:2181", fs.mFileSystem.getConf().get(ZOOKEEPER_ADDRESS));
            fs.close();
        }
        ConfigurationUtils.reloadProperties();
    }

    @Test
    public void getBlockLocationsOnlyInAlluxio() throws Exception {
        WorkerNetAddress worker1 = new WorkerNetAddress().setHost("worker1").setDataPort(1234);
        WorkerNetAddress worker2 = new WorkerNetAddress().setHost("worker2").setDataPort(1234);
        List<WorkerNetAddress> blockWorkers = Arrays.asList(worker1);
        List<String> ufsLocations = Arrays.asList();
        List<WorkerNetAddress> allWorkers = Arrays.asList(worker1, worker2);
        List<WorkerNetAddress> expectedWorkers = Arrays.asList(worker1);
        verifyBlockLocations(blockWorkers, ufsLocations, allWorkers, expectedWorkers);
    }

    @Test
    public void getBlockLocationsInUfs() throws Exception {
        WorkerNetAddress worker1 = new WorkerNetAddress().setHost("worker1").setDataPort(1234);
        WorkerNetAddress worker2 = new WorkerNetAddress().setHost("worker2").setDataPort(1234);
        List<WorkerNetAddress> blockWorkers = Arrays.asList();
        List<String> ufsLocations = Arrays.asList(worker2.getHost());
        List<WorkerNetAddress> allWorkers = Arrays.asList(worker1, worker2);
        List<WorkerNetAddress> expectedWorkers = Arrays.asList(worker2);
        verifyBlockLocations(blockWorkers, ufsLocations, allWorkers, expectedWorkers);
    }

    @Test
    public void getBlockLocationsInUfsAndAlluxio() throws Exception {
        WorkerNetAddress worker1 = new WorkerNetAddress().setHost("worker1").setDataPort(1234);
        WorkerNetAddress worker2 = new WorkerNetAddress().setHost("worker2").setDataPort(1234);
        List<WorkerNetAddress> blockWorkers = Arrays.asList(worker1);
        List<String> ufsLocations = Arrays.asList(worker2.getHost());
        List<WorkerNetAddress> allWorkers = Arrays.asList(worker1, worker2);
        List<WorkerNetAddress> expectedWorkers = Arrays.asList(worker1);
        verifyBlockLocations(blockWorkers, ufsLocations, allWorkers, expectedWorkers);
    }

    @Test
    public void getBlockLocationsOnlyMatchingWorkers() throws Exception {
        WorkerNetAddress worker1 = new WorkerNetAddress().setHost("worker1").setDataPort(1234);
        WorkerNetAddress worker2 = new WorkerNetAddress().setHost("worker2").setDataPort(1234);
        List<WorkerNetAddress> blockWorkers = Arrays.asList();
        List<String> ufsLocations = Arrays.asList("worker0", worker2.getHost(), "worker3");
        List<WorkerNetAddress> allWorkers = Arrays.asList(worker1, worker2);
        List<WorkerNetAddress> expectedWorkers = Arrays.asList(worker2);
        verifyBlockLocations(blockWorkers, ufsLocations, allWorkers, expectedWorkers);
    }

    @Test
    public void getBlockLocationsNoMatchingWorkersDefault() throws Exception {
        WorkerNetAddress worker1 = new WorkerNetAddress().setHost("worker1").setDataPort(1234);
        WorkerNetAddress worker2 = new WorkerNetAddress().setHost("worker2").setDataPort(1234);
        List<WorkerNetAddress> blockWorkers = Arrays.asList();
        List<String> ufsLocations = Arrays.asList("worker0", "worker3");
        List<WorkerNetAddress> allWorkers = Arrays.asList(worker1, worker2);
        List<WorkerNetAddress> expectedWorkers = Collections.EMPTY_LIST;
        verifyBlockLocations(blockWorkers, ufsLocations, allWorkers, expectedWorkers);
    }

    @Test
    public void getBlockLocationsNoMatchingWorkersWithFallback() throws Exception {
        WorkerNetAddress worker1 = new WorkerNetAddress().setHost("worker1").setDataPort(1234);
        WorkerNetAddress worker2 = new WorkerNetAddress().setHost("worker2").setDataPort(1234);
        List<WorkerNetAddress> blockWorkers = Arrays.asList();
        List<String> ufsLocations = Arrays.asList("worker0", "worker3");
        List<WorkerNetAddress> allWorkers = Arrays.asList(worker1, worker2);
        List<WorkerNetAddress> expectedWorkers = Arrays.asList(worker1, worker2);
        try (Closeable conf = toResource()) {
            verifyBlockLocations(blockWorkers, ufsLocations, allWorkers, expectedWorkers);
        }
    }

    @Test
    public void getBlockLocationsNoUfsLocationsDefault() throws Exception {
        WorkerNetAddress worker1 = new WorkerNetAddress().setHost("worker1").setDataPort(1234);
        WorkerNetAddress worker2 = new WorkerNetAddress().setHost("worker2").setDataPort(1234);
        List<WorkerNetAddress> blockWorkers = Arrays.asList();
        List<String> ufsLocations = Arrays.asList();
        List<WorkerNetAddress> allWorkers = Arrays.asList(worker1, worker2);
        List<WorkerNetAddress> expectedWorkers = Collections.EMPTY_LIST;
        verifyBlockLocations(blockWorkers, ufsLocations, allWorkers, expectedWorkers);
    }

    @Test
    public void getBlockLocationsNoUfsLocationsWithFallback() throws Exception {
        WorkerNetAddress worker1 = new WorkerNetAddress().setHost("worker1").setDataPort(1234);
        WorkerNetAddress worker2 = new WorkerNetAddress().setHost("worker2").setDataPort(1234);
        List<WorkerNetAddress> blockWorkers = Arrays.asList();
        List<String> ufsLocations = Arrays.asList();
        List<WorkerNetAddress> allWorkers = Arrays.asList(worker1, worker2);
        List<WorkerNetAddress> expectedWorkers = Arrays.asList(worker1, worker2);
        try (Closeable conf = toResource()) {
            verifyBlockLocations(blockWorkers, ufsLocations, allWorkers, expectedWorkers);
        }
    }
}

