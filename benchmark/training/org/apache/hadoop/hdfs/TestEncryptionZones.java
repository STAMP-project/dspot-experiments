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


import CipherSuite.AES_CTR_NOPADDING;
import CommonConfigurationKeysPublic.HADOOP_SECURITY_KEY_PROVIDER_PATH;
import CreateEncryptionZoneFlag.NO_TRASH;
import CreateEncryptionZoneFlag.PROVISION_TRASH;
import DistributedFileSystem.LOG;
import HdfsServerConstants.CRYPTO_XATTR_FILE_ENCRYPTION_INFO;
import KeyProvider.KeyVersion;
import NamenodeFsck.HEALTHY_STATUS;
import SafeModeAction.SAFEMODE_ENTER;
import SafeModeAction.SAFEMODE_LEAVE;
import WebHdfsConstants.WEBHDFS_SCHEME;
import com.google.common.collect.Lists;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.crypto.CryptoInputStream;
import org.apache.hadoop.crypto.CryptoProtocolVersion;
import org.apache.hadoop.crypto.key.JavaKeyStoreProvider;
import org.apache.hadoop.crypto.key.KeyProvider;
import org.apache.hadoop.crypto.key.KeyProviderCryptoExtension.CryptoExtension;
import org.apache.hadoop.crypto.key.KeyProviderDelegationTokenExtension.DelegationTokenExtension;
import org.apache.hadoop.crypto.key.KeyProviderFactory;
import org.apache.hadoop.fs.CommonConfigurationKeysPublic;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileContextTestWrapper;
import org.apache.hadoop.fs.FileEncryptionInfo;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileSystemTestHelper;
import org.apache.hadoop.fs.FileSystemTestWrapper;
import org.apache.hadoop.fs.FsServerDefaults;
import org.apache.hadoop.fs.FsShell;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.client.CreateEncryptionZoneFlag;
import org.apache.hadoop.hdfs.client.HdfsAdmin;
import org.apache.hadoop.hdfs.protocol.EncryptionZone;
import org.apache.hadoop.hdfs.protocol.SnapshotDiffReport.DiffType;
import org.apache.hadoop.hdfs.server.namenode.EncryptionFaultInjector;
import org.apache.hadoop.hdfs.server.namenode.FSImageTestUtil;
import org.apache.hadoop.hdfs.tools.CryptoAdmin;
import org.apache.hadoop.hdfs.tools.offlineImageViewer.PBImageXmlWriter;
import org.apache.hadoop.hdfs.web.WebHdfsFileSystem;
import org.apache.hadoop.hdfs.web.WebHdfsTestUtil;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.ipc.RemoteException;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.authorize.AuthorizationException;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.MetricsAsserts;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

import static DFSOutputStream.CREATE_RETRY_COUNT;
import static DFSOutputStream.SUPPORTED_CRYPTO_VERSIONS;


public class TestEncryptionZones {
    static final Logger LOG = Logger.getLogger(TestEncryptionZones.class);

    protected Configuration conf;

    private FileSystemTestHelper fsHelper;

    protected MiniDFSCluster cluster;

    protected HdfsAdmin dfsAdmin;

    protected DistributedFileSystem fs;

    private File testRootDir;

    protected final String TEST_KEY = "test_key";

    private static final String NS_METRICS = "FSNamesystem";

    private static final String AUTHORIZATION_EXCEPTION_MESSAGE = "User [root] is not authorized to perform [READ] on key " + "with ACL name [key2]!!";

    protected FileSystemTestWrapper fsWrapper;

    protected FileContextTestWrapper fcWrapper;

    protected static final EnumSet<CreateEncryptionZoneFlag> NO_TRASH = EnumSet.of(CreateEncryptionZoneFlag.NO_TRASH);

    @Rule
    public Timeout globalTimeout = new Timeout((120 * 1000));

    /**
     * Make sure hdfs crypto -createZone command creates a trash directory
     * with sticky bits.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testTrashStickyBit() throws Exception {
        // create an EZ /zones/zone1, make it world writable.
        final Path zoneParent = new Path("/zones");
        final Path zone1 = new Path(zoneParent, "zone1");
        CryptoAdmin cryptoAdmin = new CryptoAdmin(conf);
        fsWrapper.mkdir(zone1, FsPermission.getDirDefault(), true);
        fsWrapper.setPermission(zone1, new FsPermission(FsAction.ALL, FsAction.ALL, FsAction.ALL));
        String[] cryptoArgv = new String[]{ "-createZone", "-keyName", TEST_KEY, "-path", zone1.toUri().getPath() };
        cryptoAdmin.run(cryptoArgv);
        // create a file in EZ
        final Path ezfile1 = new Path(zone1, "file1");
        // Create the encrypted file in zone1
        final int len = 8192;
        DFSTestUtil.createFile(fs, ezfile1, len, ((short) (1)), 65261);
        // enable trash, delete /zones/zone1/file1,
        // which moves the file to
        // /zones/zone1/.Trash/$SUPERUSER/Current/zones/zone1/file1
        Configuration clientConf = new Configuration(conf);
        clientConf.setLong(CommonConfigurationKeysPublic.FS_TRASH_INTERVAL_KEY, 1);
        final FsShell shell = new FsShell(clientConf);
        String[] argv = new String[]{ "-rm", ezfile1.toString() };
        int res = ToolRunner.run(shell, argv);
        Assert.assertEquals("Can't remove a file in EZ as superuser", 0, res);
        final Path trashDir = new Path(zone1, FileSystem.TRASH_PREFIX);
        Assert.assertTrue(fsWrapper.exists(trashDir));
        FileStatus trashFileStatus = fsWrapper.getFileStatus(trashDir);
        Assert.assertTrue(trashFileStatus.getPermission().getStickyBit());
        // create a non-privileged user
        final UserGroupInformation user = UserGroupInformation.createUserForTesting("user", new String[]{ "mygroup" });
        user.doAs(new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() throws Exception {
                final Path ezfile2 = new Path(zone1, "file2");
                final int len = 8192;
                // create a file /zones/zone1/file2 in EZ
                // this file is owned by user:mygroup
                FileSystem fs2 = FileSystem.get(cluster.getConfiguration(0));
                DFSTestUtil.createFile(fs2, ezfile2, len, ((short) (1)), 65261);
                // delete /zones/zone1/file2,
                // which moves the file to
                // /zones/zone1/.Trash/user/Current/zones/zone1/file2
                String[] argv = new String[]{ "-rm", ezfile2.toString() };
                int res = ToolRunner.run(shell, argv);
                Assert.assertEquals("Can't remove a file in EZ as user:mygroup", 0, res);
                return null;
            }
        });
    }

    /**
     * Make sure hdfs crypto -provisionTrash command creates a trash directory
     * with sticky bits.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testProvisionTrash() throws Exception {
        // create an EZ /zones/zone1
        final Path zoneParent = new Path("/zones");
        final Path zone1 = new Path(zoneParent, "zone1");
        CryptoAdmin cryptoAdmin = new CryptoAdmin(conf);
        fsWrapper.mkdir(zone1, FsPermission.getDirDefault(), true);
        String[] cryptoArgv = new String[]{ "-createZone", "-keyName", TEST_KEY, "-path", zone1.toUri().getPath() };
        cryptoAdmin.run(cryptoArgv);
        // remove the trash directory
        Configuration clientConf = new Configuration(conf);
        clientConf.setLong(CommonConfigurationKeysPublic.FS_TRASH_INTERVAL_KEY, 1);
        final FsShell shell = new FsShell(clientConf);
        final Path trashDir = new Path(zone1, FileSystem.TRASH_PREFIX);
        String[] argv = new String[]{ "-rmdir", trashDir.toUri().getPath() };
        int res = ToolRunner.run(shell, argv);
        Assert.assertEquals("Unable to delete trash directory.", 0, res);
        Assert.assertFalse(fsWrapper.exists(trashDir));
        // execute -provisionTrash command option and make sure the trash
        // directory has sticky bit.
        String[] provisionTrashArgv = new String[]{ "-provisionTrash", "-path", zone1.toUri().getPath() };
        cryptoAdmin.run(provisionTrashArgv);
        Assert.assertTrue(fsWrapper.exists(trashDir));
        FileStatus trashFileStatus = fsWrapper.getFileStatus(trashDir);
        Assert.assertTrue(trashFileStatus.getPermission().getStickyBit());
    }

    // CHECKSTYLE:OFF:MethodLengthCheck
    @Test
    public void testBasicOperations() throws Exception {
        Assert.assertNotNull("key provider is not present", dfsAdmin.getKeyProvider());
        int numZones = 0;
        /* Number of EZs should be 0 if no EZ is created */
        Assert.assertEquals("Unexpected number of encryption zones!", numZones, cluster.getNamesystem().getNumEncryptionZones());
        /* Test failure of create EZ on a directory that doesn't exist. */
        final Path zoneParent = new Path("/zones");
        final Path zone1 = new Path(zoneParent, "zone1");
        try {
            dfsAdmin.createEncryptionZone(zone1, TEST_KEY, TestEncryptionZones.NO_TRASH);
            Assert.fail("expected /test doesn't exist");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("cannot find", e);
        }
        /* Normal creation of an EZ */
        fsWrapper.mkdir(zone1, FsPermission.getDirDefault(), true);
        dfsAdmin.createEncryptionZone(zone1, TEST_KEY, TestEncryptionZones.NO_TRASH);
        assertNumZones((++numZones));
        assertZonePresent(null, zone1.toString());
        /* Test failure of create EZ on a directory which is already an EZ. */
        try {
            dfsAdmin.createEncryptionZone(zone1, TEST_KEY, TestEncryptionZones.NO_TRASH);
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("is already an encryption zone", e);
        }
        /* create EZ on parent of an EZ should fail */
        try {
            dfsAdmin.createEncryptionZone(zoneParent, TEST_KEY, TestEncryptionZones.NO_TRASH);
            Assert.fail("EZ over an EZ");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("encryption zone for a non-empty directory", e);
        }
        /* create EZ on a folder with a folder fails */
        final Path notEmpty = new Path("/notEmpty");
        final Path notEmptyChild = new Path(notEmpty, "child");
        fsWrapper.mkdir(notEmptyChild, FsPermission.getDirDefault(), true);
        try {
            dfsAdmin.createEncryptionZone(notEmpty, TEST_KEY, TestEncryptionZones.NO_TRASH);
            Assert.fail("Created EZ on an non-empty directory with folder");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("create an encryption zone", e);
        }
        fsWrapper.delete(notEmptyChild, false);
        /* create EZ on a folder with a file fails */
        fsWrapper.createFile(notEmptyChild);
        try {
            dfsAdmin.createEncryptionZone(notEmpty, TEST_KEY, TestEncryptionZones.NO_TRASH);
            Assert.fail("Created EZ on an non-empty directory with file");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("create an encryption zone", e);
        }
        /* Test failure of create EZ on a file. */
        try {
            dfsAdmin.createEncryptionZone(notEmptyChild, TEST_KEY, TestEncryptionZones.NO_TRASH);
            Assert.fail("Created EZ on a file");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("create an encryption zone for a file.", e);
        }
        /* Test failure of creating an EZ passing a key that doesn't exist. */
        final Path zone2 = new Path("/zone2");
        fsWrapper.mkdir(zone2, FsPermission.getDirDefault(), false);
        final String myKeyName = "mykeyname";
        try {
            dfsAdmin.createEncryptionZone(zone2, myKeyName, TestEncryptionZones.NO_TRASH);
            Assert.fail("expected key doesn't exist");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("doesn't exist.", e);
        }
        /* Test failure of empty and null key name */
        try {
            dfsAdmin.createEncryptionZone(zone2, "", TestEncryptionZones.NO_TRASH);
            Assert.fail("created a zone with empty key name");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("Must specify a key name when creating", e);
        }
        try {
            dfsAdmin.createEncryptionZone(zone2, null, TestEncryptionZones.NO_TRASH);
            Assert.fail("created a zone with null key name");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("Must specify a key name when creating", e);
        }
        assertNumZones(1);
        /* Test success of creating an EZ when they key exists. */
        DFSTestUtil.createKey(myKeyName, cluster, conf);
        dfsAdmin.createEncryptionZone(zone2, myKeyName, TestEncryptionZones.NO_TRASH);
        assertNumZones((++numZones));
        assertZonePresent(myKeyName, zone2.toString());
        /* Test failure of create encryption zones as a non super user. */
        final UserGroupInformation user = UserGroupInformation.createUserForTesting("user", new String[]{ "mygroup" });
        final Path nonSuper = new Path("/nonSuper");
        fsWrapper.mkdir(nonSuper, FsPermission.getDirDefault(), false);
        user.doAs(new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() throws Exception {
                final HdfsAdmin userAdmin = new HdfsAdmin(FileSystem.getDefaultUri(conf), conf);
                try {
                    userAdmin.createEncryptionZone(nonSuper, TEST_KEY, TestEncryptionZones.NO_TRASH);
                    Assert.fail("createEncryptionZone is superuser-only operation");
                } catch (AccessControlException e) {
                    GenericTestUtils.assertExceptionContains("Superuser privilege is required", e);
                }
                return null;
            }
        });
        // Test success of creating an encryption zone a few levels down.
        Path deepZone = new Path("/d/e/e/p/zone");
        fsWrapper.mkdir(deepZone, FsPermission.getDirDefault(), true);
        dfsAdmin.createEncryptionZone(deepZone, TEST_KEY, TestEncryptionZones.NO_TRASH);
        assertNumZones((++numZones));
        assertZonePresent(null, deepZone.toString());
        // Create and list some zones to test batching of listEZ
        for (int i = 1; i < 6; i++) {
            final Path zonePath = new Path(("/listZone" + i));
            fsWrapper.mkdir(zonePath, FsPermission.getDirDefault(), false);
            dfsAdmin.createEncryptionZone(zonePath, TEST_KEY, TestEncryptionZones.NO_TRASH);
            numZones++;
            assertNumZones(numZones);
            assertZonePresent(null, zonePath.toString());
        }
        fs.setSafeMode(SAFEMODE_ENTER);
        fs.saveNamespace();
        fs.setSafeMode(SAFEMODE_LEAVE);
        cluster.restartNameNode(true);
        assertNumZones(numZones);
        Assert.assertEquals("Unexpected number of encryption zones!", numZones, cluster.getNamesystem().getNumEncryptionZones());
        MetricsAsserts.assertGauge("NumEncryptionZones", numZones, MetricsAsserts.getMetrics(TestEncryptionZones.NS_METRICS));
        assertZonePresent(null, zone1.toString());
        // Verify newly added ez is present after restarting the NameNode
        // without persisting the namespace.
        Path nonpersistZone = new Path("/nonpersistZone");
        fsWrapper.mkdir(nonpersistZone, FsPermission.getDirDefault(), false);
        dfsAdmin.createEncryptionZone(nonpersistZone, TEST_KEY, TestEncryptionZones.NO_TRASH);
        numZones++;
        cluster.restartNameNode(true);
        assertNumZones(numZones);
        assertZonePresent(null, nonpersistZone.toString());
    }

    // CHECKSTYLE:ON:MethodLengthCheck
    @Test
    public void testBasicOperationsRootDir() throws Exception {
        int numZones = 0;
        final Path rootDir = new Path("/");
        final Path zone1 = new Path(rootDir, "zone1");
        /* Normal creation of an EZ on rootDir */
        dfsAdmin.createEncryptionZone(rootDir, TEST_KEY, TestEncryptionZones.NO_TRASH);
        assertNumZones((++numZones));
        assertZonePresent(null, rootDir.toString());
        // Verify rootDir ez is present after restarting the NameNode
        // and saving/loading from fsimage.
        fs.setSafeMode(SAFEMODE_ENTER);
        fs.saveNamespace();
        fs.setSafeMode(SAFEMODE_LEAVE);
        cluster.restartNameNode(true);
        assertNumZones(numZones);
        assertZonePresent(null, rootDir.toString());
    }

    @Test
    public void testEZwithFullyQualifiedPath() throws Exception {
        /* Test failure of create EZ on a directory that doesn't exist. */
        final Path zoneParent = new Path("/zones");
        final Path zone1 = new Path(zoneParent, "zone1");
        final Path zone1FQP = new Path(cluster.getURI().toString(), zone1);
        final Path zone2 = new Path(zoneParent, "zone2");
        final Path zone2FQP = new Path(cluster.getURI().toString(), zone2);
        int numZones = 0;
        EnumSet<CreateEncryptionZoneFlag> withTrash = EnumSet.of(PROVISION_TRASH);
        // Create EZ with Trash using FQP
        fsWrapper.mkdir(zone1FQP, FsPermission.getDirDefault(), true);
        dfsAdmin.createEncryptionZone(zone1FQP, TEST_KEY, withTrash);
        assertNumZones((++numZones));
        assertZonePresent(TEST_KEY, zone1.toString());
        // Check that zone1 contains a .Trash directory
        final Path zone1Trash = new Path(zone1, fs.TRASH_PREFIX);
        Assert.assertTrue(("CreateEncryptionZone with trash enabled should create a " + ".Trash directory in the EZ"), fs.exists(zone1Trash));
        // getEncryptionZoneForPath for FQP should return the path component
        EncryptionZone ezForZone1 = dfsAdmin.getEncryptionZoneForPath(zone1FQP);
        Assert.assertTrue(("getEncryptionZoneForPath for fully qualified path should " + "return the path component"), ezForZone1.getPath().equals(zone1.toString()));
        // Create EZ without Trash
        fsWrapper.mkdir(zone2FQP, FsPermission.getDirDefault(), true);
        dfsAdmin.createEncryptionZone(zone2FQP, TEST_KEY, TestEncryptionZones.NO_TRASH);
        assertNumZones((++numZones));
        assertZonePresent(TEST_KEY, zone2.toString());
        // Provision Trash on zone2 using FQP
        dfsAdmin.provisionEncryptionZoneTrash(zone2FQP);
        EncryptionZone ezForZone2 = dfsAdmin.getEncryptionZoneForPath(zone2FQP);
        Path ezTrashForZone2 = new Path(ezForZone2.getPath(), FileSystem.TRASH_PREFIX);
        Assert.assertTrue(("provisionEZTrash with fully qualified path should create " + "trash directory "), fsWrapper.exists(ezTrashForZone2));
    }

    /**
     * Test listing encryption zones as a non super user.
     */
    @Test
    public void testListEncryptionZonesAsNonSuperUser() throws Exception {
        final UserGroupInformation user = UserGroupInformation.createUserForTesting("user", new String[]{ "mygroup" });
        final Path testRoot = new Path("/tmp/TestEncryptionZones");
        final Path superPath = new Path(testRoot, "superuseronly");
        final Path allPath = new Path(testRoot, "accessall");
        fsWrapper.mkdir(superPath, new FsPermission(((short) (448))), true);
        dfsAdmin.createEncryptionZone(superPath, TEST_KEY, TestEncryptionZones.NO_TRASH);
        fsWrapper.mkdir(allPath, new FsPermission(((short) (455))), true);
        dfsAdmin.createEncryptionZone(allPath, TEST_KEY, TestEncryptionZones.NO_TRASH);
        user.doAs(new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() throws Exception {
                final HdfsAdmin userAdmin = new HdfsAdmin(FileSystem.getDefaultUri(conf), conf);
                try {
                    userAdmin.listEncryptionZones();
                } catch (AccessControlException e) {
                    GenericTestUtils.assertExceptionContains("Superuser privilege is required", e);
                }
                return null;
            }
        });
    }

    /**
     * Test getEncryptionZoneForPath as a non super user.
     */
    @Test
    public void testGetEZAsNonSuperUser() throws Exception {
        final UserGroupInformation user = UserGroupInformation.createUserForTesting("user", new String[]{ "mygroup" });
        final Path testRoot = new Path("/tmp/TestEncryptionZones");
        final Path superPath = new Path(testRoot, "superuseronly");
        final Path superPathFile = new Path(superPath, "file1");
        final Path allPath = new Path(testRoot, "accessall");
        final Path allPathFile = new Path(allPath, "file1");
        final Path nonEZDir = new Path(testRoot, "nonEZDir");
        final Path nonEZFile = new Path(nonEZDir, "file1");
        final Path nonexistent = new Path("/nonexistent");
        final int len = 8192;
        fsWrapper.mkdir(testRoot, new FsPermission(((short) (511))), true);
        fsWrapper.mkdir(superPath, new FsPermission(((short) (448))), false);
        fsWrapper.mkdir(allPath, new FsPermission(((short) (511))), false);
        fsWrapper.mkdir(nonEZDir, new FsPermission(((short) (511))), false);
        dfsAdmin.createEncryptionZone(superPath, TEST_KEY, TestEncryptionZones.NO_TRASH);
        dfsAdmin.createEncryptionZone(allPath, TEST_KEY, TestEncryptionZones.NO_TRASH);
        dfsAdmin.allowSnapshot(new Path("/"));
        final Path newSnap = fs.createSnapshot(new Path("/"));
        DFSTestUtil.createFile(fs, superPathFile, len, ((short) (1)), 65261);
        DFSTestUtil.createFile(fs, allPathFile, len, ((short) (1)), 65261);
        DFSTestUtil.createFile(fs, nonEZFile, len, ((short) (1)), 65261);
        user.doAs(new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() throws Exception {
                final HdfsAdmin userAdmin = new HdfsAdmin(FileSystem.getDefaultUri(conf), conf);
                // Check null arg
                try {
                    userAdmin.getEncryptionZoneForPath(null);
                    Assert.fail("should have thrown NPE");
                } catch (NullPointerException e) {
                    /* IWBNI we could use assertExceptionContains, but the NPE that is
                    thrown has no message text.
                     */
                }
                // Check operation with accessible paths
                Assert.assertEquals("expected ez path", allPath.toString(), userAdmin.getEncryptionZoneForPath(allPath).getPath().toString());
                Assert.assertEquals("expected ez path", allPath.toString(), userAdmin.getEncryptionZoneForPath(allPathFile).getPath().toString());
                // Check operation with inaccessible (lack of permissions) path
                try {
                    userAdmin.getEncryptionZoneForPath(superPathFile);
                    Assert.fail("expected AccessControlException");
                } catch (AccessControlException e) {
                    GenericTestUtils.assertExceptionContains("Permission denied:", e);
                }
                Assert.assertNull("expected null for nonexistent path", userAdmin.getEncryptionZoneForPath(nonexistent));
                // Check operation with non-ez paths
                Assert.assertNull("expected null for non-ez path", userAdmin.getEncryptionZoneForPath(nonEZDir));
                Assert.assertNull("expected null for non-ez path", userAdmin.getEncryptionZoneForPath(nonEZFile));
                // Check operation with snapshots
                String snapshottedAllPath = (newSnap.toString()) + (allPath.toString());
                Assert.assertEquals("expected ez path", allPath.toString(), userAdmin.getEncryptionZoneForPath(new Path(snapshottedAllPath)).getPath().toString());
                /* Delete the file from the non-snapshot and test that it is still ok
                in the ez.
                 */
                fs.delete(allPathFile, false);
                Assert.assertEquals("expected ez path", allPath.toString(), userAdmin.getEncryptionZoneForPath(new Path(snapshottedAllPath)).getPath().toString());
                // Delete the ez and make sure ss's ez is still ok.
                fs.delete(allPath, true);
                Assert.assertEquals("expected ez path", allPath.toString(), userAdmin.getEncryptionZoneForPath(new Path(snapshottedAllPath)).getPath().toString());
                Assert.assertNull("expected null for deleted file path", userAdmin.getEncryptionZoneForPath(allPathFile));
                Assert.assertNull("expected null for deleted directory path", userAdmin.getEncryptionZoneForPath(allPath));
                return null;
            }
        });
    }

    @Test
    public void testRenameFileSystem() throws Exception {
        doRenameEncryptionZone(fsWrapper);
    }

    @Test
    public void testRenameFileContext() throws Exception {
        doRenameEncryptionZone(fcWrapper);
    }

    @Test
    public void testReadWrite() throws Exception {
        final HdfsAdmin dfsAdmin = new HdfsAdmin(FileSystem.getDefaultUri(conf), conf);
        // Create a base file for comparison
        final Path baseFile = new Path("/base");
        final int len = 8192;
        DFSTestUtil.createFile(fs, baseFile, len, ((short) (1)), 65261);
        // Create the first enc file
        final Path zone = new Path("/zone");
        fs.mkdirs(zone);
        dfsAdmin.createEncryptionZone(zone, TEST_KEY, TestEncryptionZones.NO_TRASH);
        final Path encFile1 = new Path(zone, "myfile");
        DFSTestUtil.createFile(fs, encFile1, len, ((short) (1)), 65261);
        // Read them back in and compare byte-by-byte
        DFSTestUtil.verifyFilesEqual(fs, baseFile, encFile1, len);
        // Roll the key of the encryption zone
        assertNumZones(1);
        String keyName = dfsAdmin.listEncryptionZones().next().getKeyName();
        cluster.getNamesystem().getProvider().rollNewVersion(keyName);
        cluster.getNamesystem().getProvider().invalidateCache(keyName);
        // Read them back in and compare byte-by-byte
        DFSTestUtil.verifyFilesEqual(fs, baseFile, encFile1, len);
        // Write a new enc file and validate
        final Path encFile2 = new Path(zone, "myfile2");
        DFSTestUtil.createFile(fs, encFile2, len, ((short) (1)), 65261);
        // FEInfos should be different
        FileEncryptionInfo feInfo1 = getFileEncryptionInfo(encFile1);
        FileEncryptionInfo feInfo2 = getFileEncryptionInfo(encFile2);
        Assert.assertFalse("EDEKs should be different", Arrays.equals(feInfo1.getEncryptedDataEncryptionKey(), feInfo2.getEncryptedDataEncryptionKey()));
        Assert.assertNotEquals("Key was rolled, versions should be different", feInfo1.getEzKeyVersionName(), feInfo2.getEzKeyVersionName());
        // Contents still equal
        DFSTestUtil.verifyFilesEqual(fs, encFile1, encFile2, len);
    }

    @Test
    public void testReadWriteUsingWebHdfs() throws Exception {
        final HdfsAdmin dfsAdmin = new HdfsAdmin(FileSystem.getDefaultUri(conf), conf);
        final FileSystem webHdfsFs = WebHdfsTestUtil.getWebHdfsFileSystem(conf, WEBHDFS_SCHEME);
        final Path zone = new Path("/zone");
        fs.mkdirs(zone);
        dfsAdmin.createEncryptionZone(zone, TEST_KEY, TestEncryptionZones.NO_TRASH);
        /* Create an unencrypted file for comparison purposes. */
        final Path unencFile = new Path("/unenc");
        final int len = 8192;
        DFSTestUtil.createFile(webHdfsFs, unencFile, len, ((short) (1)), 65261);
        /* Create the same file via webhdfs, but this time encrypted. Compare it
        using both webhdfs and DFS.
         */
        final Path encFile1 = new Path(zone, "myfile");
        DFSTestUtil.createFile(webHdfsFs, encFile1, len, ((short) (1)), 65261);
        DFSTestUtil.verifyFilesEqual(webHdfsFs, unencFile, encFile1, len);
        DFSTestUtil.verifyFilesEqual(fs, unencFile, encFile1, len);
        /* Same thing except this time create the encrypted file using DFS. */
        final Path encFile2 = new Path(zone, "myfile2");
        DFSTestUtil.createFile(fs, encFile2, len, ((short) (1)), 65261);
        DFSTestUtil.verifyFilesEqual(webHdfsFs, unencFile, encFile2, len);
        DFSTestUtil.verifyFilesEqual(fs, unencFile, encFile2, len);
        /* Verify appending to files works correctly. */
        appendOneByte(fs, unencFile);
        appendOneByte(webHdfsFs, encFile1);
        appendOneByte(fs, encFile2);
        DFSTestUtil.verifyFilesEqual(webHdfsFs, unencFile, encFile1, len);
        DFSTestUtil.verifyFilesEqual(fs, unencFile, encFile1, len);
        DFSTestUtil.verifyFilesEqual(webHdfsFs, unencFile, encFile2, len);
        DFSTestUtil.verifyFilesEqual(fs, unencFile, encFile2, len);
    }

    @Test
    public void testVersionAndSuiteNegotiation() throws Exception {
        final HdfsAdmin dfsAdmin = new HdfsAdmin(FileSystem.getDefaultUri(conf), conf);
        final Path zone = new Path("/zone");
        fs.mkdirs(zone);
        dfsAdmin.createEncryptionZone(zone, TEST_KEY, TestEncryptionZones.NO_TRASH);
        // Create a file in an EZ, which should succeed
        DFSTestUtil.createFile(fs, new Path(zone, "success1"), 0, ((short) (1)), 65261);
        // Pass no supported versions, fail
        SUPPORTED_CRYPTO_VERSIONS = new CryptoProtocolVersion[]{  };
        try {
            DFSTestUtil.createFile(fs, new Path(zone, "fail"), 0, ((short) (1)), 65261);
            Assert.fail("Created a file without specifying a crypto protocol version");
        } catch (UnknownCryptoProtocolVersionException e) {
            GenericTestUtils.assertExceptionContains("No crypto protocol versions", e);
        }
        // Pass some unknown versions, fail
        SUPPORTED_CRYPTO_VERSIONS = new CryptoProtocolVersion[]{ CryptoProtocolVersion.UNKNOWN, CryptoProtocolVersion.UNKNOWN };
        try {
            DFSTestUtil.createFile(fs, new Path(zone, "fail"), 0, ((short) (1)), 65261);
            Assert.fail("Created a file without specifying a known crypto protocol version");
        } catch (UnknownCryptoProtocolVersionException e) {
            GenericTestUtils.assertExceptionContains("No crypto protocol versions", e);
        }
        // Pass some unknown and a good cipherSuites, success
        SUPPORTED_CRYPTO_VERSIONS = new CryptoProtocolVersion[]{ CryptoProtocolVersion.UNKNOWN, CryptoProtocolVersion.UNKNOWN, CryptoProtocolVersion.ENCRYPTION_ZONES };
        DFSTestUtil.createFile(fs, new Path(zone, "success2"), 0, ((short) (1)), 65261);
        SUPPORTED_CRYPTO_VERSIONS = new CryptoProtocolVersion[]{ CryptoProtocolVersion.ENCRYPTION_ZONES, CryptoProtocolVersion.UNKNOWN, CryptoProtocolVersion.UNKNOWN };
        DFSTestUtil.createFile(fs, new Path(zone, "success3"), 4096, ((short) (1)), 65261);
        // Check KeyProvider state
        // Flushing the KP on the NN, since it caches, and init a test one
        cluster.getNamesystem().getProvider().flush();
        KeyProvider provider = KeyProviderFactory.get(new URI(conf.getTrimmed(HADOOP_SECURITY_KEY_PROVIDER_PATH)), conf);
        List<String> keys = provider.getKeys();
        Assert.assertEquals("Expected NN to have created one key per zone", 1, keys.size());
        List<KeyProvider.KeyVersion> allVersions = Lists.newArrayList();
        for (String key : keys) {
            List<KeyProvider.KeyVersion> versions = provider.getKeyVersions(key);
            Assert.assertEquals("Should only have one key version per key", 1, versions.size());
            allVersions.addAll(versions);
        }
        // Check that the specified CipherSuite was correctly saved on the NN
        for (int i = 2; i <= 3; i++) {
            FileEncryptionInfo feInfo = getFileEncryptionInfo(new Path((((zone.toString()) + "/success") + i)));
            Assert.assertEquals(feInfo.getCipherSuite(), AES_CTR_NOPADDING);
        }
        DFSClient old = fs.dfs;
        try {
            TestEncryptionZones.testCipherSuiteNegotiation(fs, conf);
        } finally {
            fs.dfs = old;
        }
    }

    @Test
    public void testCreateEZWithNoProvider() throws Exception {
        // Unset the key provider and make sure EZ ops don't work
        final Configuration clusterConf = cluster.getConfiguration(0);
        clusterConf.unset(HADOOP_SECURITY_KEY_PROVIDER_PATH);
        cluster.restartNameNode(true);
        final Path zone1 = new Path("/zone1");
        fsWrapper.mkdir(zone1, FsPermission.getDirDefault(), true);
        try {
            dfsAdmin.createEncryptionZone(zone1, TEST_KEY, TestEncryptionZones.NO_TRASH);
            Assert.fail("expected exception");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("since no key provider is available", e);
        }
        final Path jksPath = new Path(testRootDir.toString(), "test.jks");
        clusterConf.set(HADOOP_SECURITY_KEY_PROVIDER_PATH, (((JavaKeyStoreProvider.SCHEME_NAME) + "://file") + (jksPath.toUri())));
        // Try listing EZs as well
        assertNumZones(0);
    }

    @Test
    public void testIsEncryptedMethod() throws Exception {
        doTestIsEncryptedMethod(new Path("/"));
        doTestIsEncryptedMethod(new Path("/.reserved/raw"));
    }

    private class AuthorizationExceptionInjector extends EncryptionFaultInjector {
        @Override
        public void ensureKeyIsInitialized() throws IOException {
            throw new AuthorizationException(TestEncryptionZones.AUTHORIZATION_EXCEPTION_MESSAGE);
        }
    }

    @Test
    public void testExceptionInformationReturn() {
        /* Test exception information can be returned when
        creating transparent encryption zone.
         */
        final Path zone1 = new Path("/zone1");
        EncryptionFaultInjector.instance = new TestEncryptionZones.AuthorizationExceptionInjector();
        try {
            dfsAdmin.createEncryptionZone(zone1, TEST_KEY, TestEncryptionZones.NO_TRASH);
            Assert.fail(("exception information can be returned when creating " + "transparent encryption zone"));
        } catch (IOException e) {
            Assert.assertTrue((e instanceof RemoteException));
            Assert.assertTrue(((unwrapRemoteException()) instanceof AuthorizationException));
            GenericTestUtils.assertExceptionContains(TestEncryptionZones.AUTHORIZATION_EXCEPTION_MESSAGE, e);
        }
    }

    private class MyInjector extends EncryptionFaultInjector {
        volatile int generateCount;

        CountDownLatch ready;

        CountDownLatch wait;

        public MyInjector() {
            this.ready = new CountDownLatch(1);
            this.wait = new CountDownLatch(1);
        }

        @Override
        public void startFileNoKey() throws IOException {
            generateCount = -1;
            syncWithLatches();
        }

        @Override
        public void startFileBeforeGenerateKey() throws IOException {
            syncWithLatches();
        }

        private void syncWithLatches() throws IOException {
            ready.countDown();
            try {
                wait.await();
            } catch (InterruptedException e) {
                throw new IOException(e);
            }
        }

        @Override
        public void startFileAfterGenerateKey() throws IOException {
            (generateCount)++;
        }
    }

    private class CreateFileTask implements Callable<Void> {
        private FileSystemTestWrapper fsWrapper;

        private Path name;

        CreateFileTask(FileSystemTestWrapper fsWrapper, Path name) {
            this.fsWrapper = fsWrapper;
            this.name = name;
        }

        @Override
        public Void call() throws Exception {
            fsWrapper.createFile(name);
            return null;
        }
    }

    private class InjectFaultTask implements Callable<Void> {
        final Path zone1 = new Path("/zone1");

        final Path file = new Path(zone1, "file1");

        final ExecutorService executor = Executors.newSingleThreadExecutor();

        TestEncryptionZones.MyInjector injector;

        @Override
        public Void call() throws Exception {
            // Set up the injector
            injector = new TestEncryptionZones.MyInjector();
            EncryptionFaultInjector.instance = injector;
            Future<Void> future = executor.submit(new TestEncryptionZones.CreateFileTask(fsWrapper, file));
            injector.ready.await();
            try {
                // Do the fault
                doFault();
                // Allow create to proceed
            } finally {
                // Always decrement latch to avoid hanging the tests on failure.
                injector.wait.countDown();
            }
            future.get();
            // Cleanup and postconditions
            doCleanup();
            return null;
        }

        public void doFault() throws Exception {
        }

        public void doCleanup() throws Exception {
        }
    }

    /**
     * Tests the retry logic in startFile. We release the lock while generating
     * an EDEK, so tricky things can happen in the intervening time.
     */
    @Test
    public void testStartFileRetry() throws Exception {
        final Path zone1 = new Path("/zone1");
        final Path file = new Path(zone1, "file1");
        fsWrapper.mkdir(zone1, FsPermission.getDirDefault(), true);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        // Test when the parent directory becomes an EZ.  With no initial EZ,
        // the fsn lock must not be yielded.
        executor.submit(new TestEncryptionZones.InjectFaultTask() {
            @Override
            public void doCleanup() throws Exception {
                Assert.assertEquals("Expected no startFile key generation", (-1), injector.generateCount);
                fsWrapper.delete(file, false);
            }
        }).get();
        // Test when the parent directory unbecomes an EZ.  The generation of
        // the EDEK will yield the lock, then re-resolve the path and use the
        // previous EDEK.
        dfsAdmin.createEncryptionZone(zone1, TEST_KEY, TestEncryptionZones.NO_TRASH);
        executor.submit(new TestEncryptionZones.InjectFaultTask() {
            @Override
            public void doFault() throws Exception {
                fsWrapper.delete(zone1, true);
            }

            @Override
            public void doCleanup() throws Exception {
                Assert.assertEquals("Expected no startFile retries", 1, injector.generateCount);
                fsWrapper.delete(file, false);
            }
        }).get();
        // Test when the parent directory becomes a different EZ.  The generation
        // of the EDEK will yield the lock, re-resolve will detect the EZ has
        // changed, and client will be asked to retry a 2nd time
        fsWrapper.mkdir(zone1, FsPermission.getDirDefault(), true);
        final String otherKey = "other_key";
        DFSTestUtil.createKey(otherKey, cluster, conf);
        dfsAdmin.createEncryptionZone(zone1, TEST_KEY, TestEncryptionZones.NO_TRASH);
        executor.submit(new TestEncryptionZones.InjectFaultTask() {
            @Override
            public void doFault() throws Exception {
                fsWrapper.delete(zone1, true);
                fsWrapper.mkdir(zone1, FsPermission.getDirDefault(), true);
                dfsAdmin.createEncryptionZone(zone1, otherKey, TestEncryptionZones.NO_TRASH);
            }

            @Override
            public void doCleanup() throws Exception {
                Assert.assertEquals("Expected a startFile retry", 2, injector.generateCount);
                fsWrapper.delete(zone1, true);
            }
        }).get();
        // Test that the retry limit leads to an error
        fsWrapper.mkdir(zone1, FsPermission.getDirDefault(), true);
        final String anotherKey = "another_key";
        DFSTestUtil.createKey(anotherKey, cluster, conf);
        dfsAdmin.createEncryptionZone(zone1, anotherKey, TestEncryptionZones.NO_TRASH);
        String keyToUse = otherKey;
        TestEncryptionZones.MyInjector injector = new TestEncryptionZones.MyInjector();
        EncryptionFaultInjector.instance = injector;
        Future<?> future = executor.submit(new TestEncryptionZones.CreateFileTask(fsWrapper, file));
        // Flip-flop between two EZs to repeatedly fail
        for (int i = 0; i < ((CREATE_RETRY_COUNT) + 1); i++) {
            injector.ready.await();
            fsWrapper.delete(zone1, true);
            fsWrapper.mkdir(zone1, FsPermission.getDirDefault(), true);
            dfsAdmin.createEncryptionZone(zone1, keyToUse, TestEncryptionZones.NO_TRASH);
            if (keyToUse == otherKey) {
                keyToUse = anotherKey;
            } else {
                keyToUse = otherKey;
            }
            injector.wait.countDown();
            injector = new TestEncryptionZones.MyInjector();
            EncryptionFaultInjector.instance = injector;
        }
        try {
            future.get();
            Assert.fail("Expected exception from too many retries");
        } catch (ExecutionException e) {
            GenericTestUtils.assertExceptionContains("Too many retries because of encryption zone operations", e.getCause());
        }
    }

    /**
     * Tests obtaining delegation token from stored key
     */
    @Test
    public void testDelegationToken() throws Exception {
        UserGroupInformation.createRemoteUser("JobTracker");
        DistributedFileSystem dfs = cluster.getFileSystem();
        KeyProvider keyProvider = Mockito.mock(KeyProvider.class, Mockito.withSettings().extraInterfaces(DelegationTokenExtension.class, CryptoExtension.class));
        Mockito.when(keyProvider.getConf()).thenReturn(conf);
        byte[] testIdentifier = "Test identifier for delegation token".getBytes();
        @SuppressWarnings("rawtypes")
        Token testToken = new Token(testIdentifier, new byte[0], new Text(), new Text());
        Mockito.when(getCanonicalServiceName()).thenReturn("service");
        Mockito.when(getDelegationToken(ArgumentMatchers.anyString())).thenReturn(testToken);
        dfs.getClient().setKeyProvider(keyProvider);
        Credentials creds = new Credentials();
        final Token<?>[] tokens = dfs.addDelegationTokens("JobTracker", creds);
        DistributedFileSystem.LOG.debug(("Delegation tokens: " + (Arrays.asList(tokens))));
        Assert.assertEquals(2, tokens.length);
        Assert.assertEquals(tokens[1], testToken);
        Assert.assertEquals(2, creds.numberOfTokens());
    }

    /**
     * Test running fsck on a system with encryption zones.
     */
    @Test
    public void testFsckOnEncryptionZones() throws Exception {
        final int len = 8196;
        final Path zoneParent = new Path("/zones");
        final Path zone1 = new Path(zoneParent, "zone1");
        final Path zone1File = new Path(zone1, "file");
        fsWrapper.mkdir(zone1, FsPermission.getDirDefault(), true);
        dfsAdmin.createEncryptionZone(zone1, TEST_KEY, TestEncryptionZones.NO_TRASH);
        DFSTestUtil.createFile(fs, zone1File, len, ((short) (1)), 65261);
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bStream, true);
        int errCode = ToolRunner.run(new org.apache.hadoop.hdfs.tools.DFSck(conf, out), new String[]{ "/" });
        Assert.assertEquals("Fsck ran with non-zero error code", 0, errCode);
        String result = bStream.toString();
        Assert.assertTrue("Fsck did not return HEALTHY status", result.contains(HEALTHY_STATUS));
        // Run fsck directly on the encryption zone instead of root
        errCode = ToolRunner.run(new org.apache.hadoop.hdfs.tools.DFSck(conf, out), new String[]{ zoneParent.toString() });
        Assert.assertEquals("Fsck ran with non-zero error code", 0, errCode);
        result = bStream.toString();
        Assert.assertTrue("Fsck did not return HEALTHY status", result.contains(HEALTHY_STATUS));
    }

    /**
     * Test correctness of successive snapshot creation and deletion
     * on a system with encryption zones.
     */
    @Test
    public void testSnapshotsOnEncryptionZones() throws Exception {
        final String TEST_KEY2 = "testkey2";
        DFSTestUtil.createKey(TEST_KEY2, cluster, conf);
        final int len = 8196;
        final Path zoneParent = new Path("/zones");
        final Path zone = new Path(zoneParent, "zone");
        final Path zoneFile = new Path(zone, "zoneFile");
        fsWrapper.mkdir(zone, FsPermission.getDirDefault(), true);
        dfsAdmin.allowSnapshot(zoneParent);
        dfsAdmin.createEncryptionZone(zone, TEST_KEY, TestEncryptionZones.NO_TRASH);
        DFSTestUtil.createFile(fs, zoneFile, len, ((short) (1)), 65261);
        String contents = DFSTestUtil.readFile(fs, zoneFile);
        final Path snap1 = fs.createSnapshot(zoneParent, "snap1");
        final Path snap1Zone = new Path(snap1, zone.getName());
        Assert.assertEquals("Got unexpected ez path", zone.toString(), dfsAdmin.getEncryptionZoneForPath(snap1Zone).getPath().toString());
        // Now delete the encryption zone, recreate the dir, and take another
        // snapshot
        fsWrapper.delete(zone, true);
        fsWrapper.mkdir(zone, FsPermission.getDirDefault(), true);
        final Path snap2 = fs.createSnapshot(zoneParent, "snap2");
        final Path snap2Zone = new Path(snap2, zone.getName());
        Assert.assertEquals("Got unexpected ez path", zone.toString(), dfsAdmin.getEncryptionZoneForPath(snap1Zone).getPath().toString());
        Assert.assertNull("Expected null ez path", dfsAdmin.getEncryptionZoneForPath(snap2Zone));
        // Create the encryption zone again, and that shouldn't affect old snapshot
        dfsAdmin.createEncryptionZone(zone, TEST_KEY2, TestEncryptionZones.NO_TRASH);
        EncryptionZone ezSnap1 = dfsAdmin.getEncryptionZoneForPath(snap1Zone);
        Assert.assertEquals("Got unexpected ez path", zone.toString(), ezSnap1.getPath().toString());
        Assert.assertEquals("Unexpected ez key", TEST_KEY, ezSnap1.getKeyName());
        Assert.assertNull("Expected null ez path", dfsAdmin.getEncryptionZoneForPath(snap2Zone));
        final Path snap3 = fs.createSnapshot(zoneParent, "snap3");
        final Path snap3Zone = new Path(snap3, zone.getName());
        // Check that snap3's EZ has the correct settings
        EncryptionZone ezSnap3 = dfsAdmin.getEncryptionZoneForPath(snap3Zone);
        Assert.assertEquals("Got unexpected ez path", zone.toString(), ezSnap3.getPath().toString());
        Assert.assertEquals("Unexpected ez key", TEST_KEY2, ezSnap3.getKeyName());
        // Check that older snapshots still have the old EZ settings
        ezSnap1 = dfsAdmin.getEncryptionZoneForPath(snap1Zone);
        Assert.assertEquals("Got unexpected ez path", zone.toString(), ezSnap1.getPath().toString());
        Assert.assertEquals("Unexpected ez key", TEST_KEY, ezSnap1.getKeyName());
        Assert.assertNull("Expected null ez path", dfsAdmin.getEncryptionZoneForPath(snap2Zone));
        // Check that listEZs only shows the current filesystem state
        ArrayList<EncryptionZone> listZones = Lists.newArrayList();
        RemoteIterator<EncryptionZone> it = dfsAdmin.listEncryptionZones();
        while (it.hasNext()) {
            listZones.add(it.next());
        } 
        for (EncryptionZone z : listZones) {
            System.out.println(z);
        }
        Assert.assertEquals("Did not expect additional encryption zones!", 1, listZones.size());
        EncryptionZone listZone = listZones.get(0);
        Assert.assertEquals("Got unexpected ez path", zone.toString(), listZone.getPath().toString());
        Assert.assertEquals("Unexpected ez key", TEST_KEY2, listZone.getKeyName());
        // Verify contents of the snapshotted file
        final Path snapshottedZoneFile = new Path((((((snap1.toString()) + "/") + (zone.getName())) + "/") + (zoneFile.getName())));
        Assert.assertEquals("Contents of snapshotted file have changed unexpectedly", contents, DFSTestUtil.readFile(fs, snapshottedZoneFile));
        // Now delete the snapshots out of order and verify the zones are still
        // correct
        fs.deleteSnapshot(zoneParent, snap2.getName());
        Assert.assertEquals("Got unexpected ez path", zone.toString(), dfsAdmin.getEncryptionZoneForPath(snap1Zone).getPath().toString());
        Assert.assertEquals("Got unexpected ez path", zone.toString(), dfsAdmin.getEncryptionZoneForPath(snap3Zone).getPath().toString());
        fs.deleteSnapshot(zoneParent, snap1.getName());
        Assert.assertEquals("Got unexpected ez path", zone.toString(), dfsAdmin.getEncryptionZoneForPath(snap3Zone).getPath().toString());
    }

    /**
     * Test correctness of encryption zones on a existing snapshot path.
     * Specifically, test the file in encryption zones with no encryption info
     */
    @Test
    public void testSnapshotWithFile() throws Exception {
        final int len = 8196;
        final Path zoneParent = new Path("/zones");
        final Path zone = new Path(zoneParent, "zone");
        final Path zoneFile = new Path(zone, "zoneFile");
        fsWrapper.mkdir(zone, FsPermission.getDirDefault(), true);
        DFSTestUtil.createFile(fs, zoneFile, len, ((short) (1)), 65261);
        String contents = DFSTestUtil.readFile(fs, zoneFile);
        // Create the snapshot which contains the file
        dfsAdmin.allowSnapshot(zoneParent);
        final Path snap1 = fs.createSnapshot(zoneParent, "snap1");
        // Now delete the file and create encryption zone
        fsWrapper.delete(zoneFile, false);
        dfsAdmin.createEncryptionZone(zone, TEST_KEY, TestEncryptionZones.NO_TRASH);
        Assert.assertEquals("Got unexpected ez path", zone.toString(), dfsAdmin.getEncryptionZoneForPath(zone).getPath());
        // The file in snapshot shouldn't have any encryption info
        final Path snapshottedZoneFile = new Path(((((snap1 + "/") + (zone.getName())) + "/") + (zoneFile.getName())));
        FileEncryptionInfo feInfo = getFileEncryptionInfo(snapshottedZoneFile);
        Assert.assertNull("Expected null ez info", feInfo);
        Assert.assertEquals("Contents of snapshotted file have changed unexpectedly", contents, DFSTestUtil.readFile(fs, snapshottedZoneFile));
    }

    /**
     * Test correctness of snapshotDiff for encryption zone.
     * snapshtoDiff should work when the path parameter is prefixed with
     * /.reserved/raw for path that's both snapshottable and encryption zone.
     */
    @Test
    public void testSnapshotDiffOnEncryptionZones() throws Exception {
        final String TEST_KEY2 = "testkey2";
        DFSTestUtil.createKey(TEST_KEY2, cluster, conf);
        final int len = 8196;
        final Path zone = new Path("/zone");
        final Path rawZone = new Path("/.reserved/raw/zone");
        final Path zoneFile = new Path(zone, "zoneFile");
        fsWrapper.mkdir(zone, FsPermission.getDirDefault(), true);
        dfsAdmin.allowSnapshot(zone);
        dfsAdmin.createEncryptionZone(zone, TEST_KEY, TestEncryptionZones.NO_TRASH);
        DFSTestUtil.createFile(fs, zoneFile, len, ((short) (1)), 65261);
        fs.createSnapshot(zone, "snap1");
        fsWrapper.delete(zoneFile, true);
        fs.createSnapshot(zone, "snap2");
        verifyDiffReport(zone, "snap1", "snap2", new org.apache.hadoop.hdfs.protocol.SnapshotDiffReport.DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("")), new org.apache.hadoop.hdfs.protocol.SnapshotDiffReport.DiffReportEntry(DiffType.DELETE, DFSUtil.string2Bytes("zoneFile")));
        verifyDiffReport(rawZone, "snap1", "snap2", new org.apache.hadoop.hdfs.protocol.SnapshotDiffReport.DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("")), new org.apache.hadoop.hdfs.protocol.SnapshotDiffReport.DiffReportEntry(DiffType.DELETE, DFSUtil.string2Bytes("zoneFile")));
    }

    /**
     * Verify symlinks can be created in encryption zones and that
     * they function properly when the target is in the same
     * or different ez.
     */
    @Test
    public void testEncryptionZonesWithSymlinks() throws Exception {
        // Verify we can create an encryption zone over both link and target
        final int len = 8192;
        final Path parent = new Path("/parent");
        final Path linkParent = new Path(parent, "symdir1");
        final Path targetParent = new Path(parent, "symdir2");
        final Path link = new Path(linkParent, "link");
        final Path target = new Path(targetParent, "target");
        fs.mkdirs(parent);
        dfsAdmin.createEncryptionZone(parent, TEST_KEY, TestEncryptionZones.NO_TRASH);
        fs.mkdirs(linkParent);
        fs.mkdirs(targetParent);
        DFSTestUtil.createFile(fs, target, len, ((short) (1)), 65261);
        String content = DFSTestUtil.readFile(fs, target);
        fs.createSymlink(target, link, false);
        Assert.assertEquals("Contents read from link are not the same as target", content, DFSTestUtil.readFile(fs, link));
        fs.delete(parent, true);
        // Now let's test when the symlink and target are in different
        // encryption zones
        fs.mkdirs(linkParent);
        fs.mkdirs(targetParent);
        dfsAdmin.createEncryptionZone(linkParent, TEST_KEY, TestEncryptionZones.NO_TRASH);
        dfsAdmin.createEncryptionZone(targetParent, TEST_KEY, TestEncryptionZones.NO_TRASH);
        DFSTestUtil.createFile(fs, target, len, ((short) (1)), 65261);
        content = DFSTestUtil.readFile(fs, target);
        fs.createSymlink(target, link, false);
        Assert.assertEquals("Contents read from link are not the same as target", content, DFSTestUtil.readFile(fs, link));
        fs.delete(link, true);
        fs.delete(target, true);
    }

    @Test
    public void testConcatFailsInEncryptionZones() throws Exception {
        final int len = 8192;
        final Path ez = new Path("/ez");
        fs.mkdirs(ez);
        dfsAdmin.createEncryptionZone(ez, TEST_KEY, TestEncryptionZones.NO_TRASH);
        final Path src1 = new Path(ez, "src1");
        final Path src2 = new Path(ez, "src2");
        final Path target = new Path(ez, "target");
        DFSTestUtil.createFile(fs, src1, len, ((short) (1)), 65261);
        DFSTestUtil.createFile(fs, src2, len, ((short) (1)), 65261);
        DFSTestUtil.createFile(fs, target, len, ((short) (1)), 65261);
        try {
            fs.concat(target, new Path[]{ src1, src2 });
            Assert.fail("expected concat to throw en exception for files in an ez");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("concat can not be called for files in an encryption zone", e);
        }
        fs.delete(ez, true);
    }

    /**
     * Test running the OfflineImageViewer on a system with encryption zones.
     */
    @Test
    public void testOfflineImageViewerOnEncryptionZones() throws Exception {
        final int len = 8196;
        final Path zoneParent = new Path("/zones");
        final Path zone1 = new Path(zoneParent, "zone1");
        final Path zone1File = new Path(zone1, "file");
        fsWrapper.mkdir(zone1, FsPermission.getDirDefault(), true);
        dfsAdmin.createEncryptionZone(zone1, TEST_KEY, TestEncryptionZones.NO_TRASH);
        DFSTestUtil.createFile(fs, zone1File, len, ((short) (1)), 65261);
        fs.setSafeMode(SAFEMODE_ENTER, false);
        fs.saveNamespace();
        File originalFsimage = FSImageTestUtil.findLatestImageFile(FSImageTestUtil.getFSImage(cluster.getNameNode()).getStorage().getStorageDir(0));
        if (originalFsimage == null) {
            throw new RuntimeException("Didn't generate or can't find fsimage");
        }
        // Run the XML OIV processor
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream pw = new PrintStream(output);
        PBImageXmlWriter v = new PBImageXmlWriter(new Configuration(), pw);
        v.visit(new RandomAccessFile(originalFsimage, "r"));
        final String xml = output.toString();
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        parser.parse(new InputSource(new StringReader(xml)), new DefaultHandler());
    }

    /**
     * Test creating encryption zone on the root path
     */
    @Test
    public void testEncryptionZonesOnRootPath() throws Exception {
        final int len = 8196;
        final Path rootDir = new Path("/");
        final Path zoneFile = new Path(rootDir, "file");
        final Path rawFile = new Path("/.reserved/raw/file");
        dfsAdmin.createEncryptionZone(rootDir, TEST_KEY, TestEncryptionZones.NO_TRASH);
        DFSTestUtil.createFile(fs, zoneFile, len, ((short) (1)), 65261);
        Assert.assertEquals(("File can be created on the root encryption zone " + "with correct length"), len, fs.getFileStatus(zoneFile).getLen());
        Assert.assertEquals("Root dir is encrypted", true, fs.getFileStatus(rootDir).isEncrypted());
        Assert.assertEquals("File is encrypted", true, fs.getFileStatus(zoneFile).isEncrypted());
        DFSTestUtil.verifyFilesNotEqual(fs, zoneFile, rawFile, len);
    }

    @Test
    public void testEncryptionZonesOnRelativePath() throws Exception {
        final int len = 8196;
        final Path baseDir = new Path("/somewhere/base");
        final Path zoneDir = new Path("zone");
        final Path zoneFile = new Path("file");
        fs.setWorkingDirectory(baseDir);
        fs.mkdirs(zoneDir);
        dfsAdmin.createEncryptionZone(zoneDir, TEST_KEY, TestEncryptionZones.NO_TRASH);
        DFSTestUtil.createFile(fs, zoneFile, len, ((short) (1)), 65261);
        assertNumZones(1);
        assertZonePresent(TEST_KEY, "/somewhere/base/zone");
        Assert.assertEquals("Got unexpected ez path", "/somewhere/base/zone", dfsAdmin.getEncryptionZoneForPath(zoneDir).getPath().toString());
    }

    @Test
    public void testGetEncryptionZoneOnANonExistentPaths() throws Exception {
        final Path ezPath = new Path("/ez");
        fs.mkdirs(ezPath);
        dfsAdmin.createEncryptionZone(ezPath, TEST_KEY, TestEncryptionZones.NO_TRASH);
        Path zoneFile = new Path(ezPath, "file");
        EncryptionZone ez = fs.getEZForPath(zoneFile);
        Assert.assertNotNull("Expected EZ for non-existent path in EZ", ez);
        ez = dfsAdmin.getEncryptionZoneForPath(zoneFile);
        Assert.assertNotNull("Expected EZ for non-existent path in EZ", ez);
        ez = dfsAdmin.getEncryptionZoneForPath(new Path("/does/not/exist"));
        Assert.assertNull("Expected null for non-existent path not in EZ", ez);
    }

    @Test
    public void testEncryptionZoneWithTrash() throws Exception {
        // Create the encryption zone1
        final HdfsAdmin dfsAdmin = new HdfsAdmin(FileSystem.getDefaultUri(conf), conf);
        final Path zone1 = new Path("/zone1");
        fs.mkdirs(zone1);
        dfsAdmin.createEncryptionZone(zone1, TEST_KEY, TestEncryptionZones.NO_TRASH);
        // Create the encrypted file in zone1
        final Path encFile1 = new Path(zone1, "encFile1");
        final int len = 8192;
        DFSTestUtil.createFile(fs, encFile1, len, ((short) (1)), 65261);
        Configuration clientConf = new Configuration(conf);
        clientConf.setLong(CommonConfigurationKeysPublic.FS_TRASH_INTERVAL_KEY, 1);
        FsShell shell = new FsShell(clientConf);
        // Delete encrypted file from the shell with trash enabled
        // Verify the file is moved to appropriate trash within the zone
        verifyShellDeleteWithTrash(shell, encFile1);
        // Delete encryption zone from the shell with trash enabled
        // Verify the zone is moved to appropriate trash location in user's home dir
        verifyShellDeleteWithTrash(shell, zone1);
        final Path topEZ = new Path("/topEZ");
        fs.mkdirs(topEZ);
        dfsAdmin.createEncryptionZone(topEZ, TEST_KEY, TestEncryptionZones.NO_TRASH);
        final String NESTED_EZ_TEST_KEY = "nested_ez_test_key";
        DFSTestUtil.createKey(NESTED_EZ_TEST_KEY, cluster, conf);
        final Path nestedEZ = new Path(topEZ, "nestedEZ");
        fs.mkdirs(nestedEZ);
        dfsAdmin.createEncryptionZone(nestedEZ, NESTED_EZ_TEST_KEY, TestEncryptionZones.NO_TRASH);
        final Path topEZFile = new Path(topEZ, "file");
        final Path nestedEZFile = new Path(nestedEZ, "file");
        DFSTestUtil.createFile(fs, topEZFile, len, ((short) (1)), 65261);
        DFSTestUtil.createFile(fs, nestedEZFile, len, ((short) (1)), 65261);
        verifyShellDeleteWithTrash(shell, topEZFile);
        verifyShellDeleteWithTrash(shell, nestedEZFile);
        // Test nested EZ with webHDFS
        final WebHdfsFileSystem webFS = WebHdfsTestUtil.getWebHdfsFileSystem(conf, WEBHDFS_SCHEME);
        final String currentUser = UserGroupInformation.getCurrentUser().getShortUserName();
        final Path expectedTopTrash = new Path(topEZ, new Path(FileSystem.TRASH_PREFIX, currentUser));
        final Path expectedNestedTrash = new Path(nestedEZ, new Path(FileSystem.TRASH_PREFIX, currentUser));
        final Path topTrash = webFS.getTrashRoot(topEZFile);
        final Path nestedTrash = webFS.getTrashRoot(nestedEZFile);
        Assert.assertEquals(expectedTopTrash.toUri().getPath(), topTrash.toUri().getPath());
        Assert.assertEquals(expectedNestedTrash.toUri().getPath(), nestedTrash.toUri().getPath());
        verifyShellDeleteWithTrash(shell, nestedEZ);
        verifyShellDeleteWithTrash(shell, topEZ);
    }

    @Test
    public void testRootDirEZTrash() throws Exception {
        final HdfsAdmin dfsAdmin = new HdfsAdmin(FileSystem.getDefaultUri(conf), conf);
        final String currentUser = UserGroupInformation.getCurrentUser().getShortUserName();
        final Path rootDir = new Path("/");
        dfsAdmin.createEncryptionZone(rootDir, TEST_KEY, TestEncryptionZones.NO_TRASH);
        final Path encFile = new Path("/encFile");
        final int len = 8192;
        DFSTestUtil.createFile(fs, encFile, len, ((short) (1)), 65261);
        Configuration clientConf = new Configuration(conf);
        clientConf.setLong(CommonConfigurationKeysPublic.FS_TRASH_INTERVAL_KEY, 1);
        FsShell shell = new FsShell(clientConf);
        verifyShellDeleteWithTrash(shell, encFile);
        // Trash path should be consistent
        // if root path is an encryption zone
        Path encFileCurrentTrash = shell.getCurrentTrashDir(encFile);
        Path rootDirCurrentTrash = shell.getCurrentTrashDir(rootDir);
        Assert.assertEquals("Root trash should be equal with ezFile trash", encFileCurrentTrash, rootDirCurrentTrash);
        // Use webHDFS client to test trash root path
        final WebHdfsFileSystem webFS = WebHdfsTestUtil.getWebHdfsFileSystem(conf, WEBHDFS_SCHEME);
        final Path expectedTrash = new Path(rootDir, new Path(FileSystem.TRASH_PREFIX, currentUser));
        Path webHDFSTrash = webFS.getTrashRoot(encFile);
        Assert.assertEquals(expectedTrash.toUri().getPath(), webHDFSTrash.toUri().getPath());
        Assert.assertEquals(encFileCurrentTrash.getParent().toUri().getPath(), webHDFSTrash.toUri().getPath());
    }

    @Test
    public void testGetTrashRoots() throws Exception {
        final HdfsAdmin dfsAdmin = new HdfsAdmin(FileSystem.getDefaultUri(conf), conf);
        Path ezRoot1 = new Path("/ez1");
        fs.mkdirs(ezRoot1);
        dfsAdmin.createEncryptionZone(ezRoot1, TEST_KEY, TestEncryptionZones.NO_TRASH);
        Path ezRoot2 = new Path("/ez2");
        fs.mkdirs(ezRoot2);
        dfsAdmin.createEncryptionZone(ezRoot2, TEST_KEY, TestEncryptionZones.NO_TRASH);
        Path ezRoot3 = new Path("/ez3");
        fs.mkdirs(ezRoot3);
        dfsAdmin.createEncryptionZone(ezRoot3, TEST_KEY, TestEncryptionZones.NO_TRASH);
        Collection<FileStatus> trashRootsBegin = fs.getTrashRoots(true);
        Assert.assertEquals("Unexpected getTrashRoots result", 0, trashRootsBegin.size());
        final Path encFile = new Path(ezRoot2, "encFile");
        final int len = 8192;
        DFSTestUtil.createFile(fs, encFile, len, ((short) (1)), 65261);
        Configuration clientConf = new Configuration(conf);
        clientConf.setLong(CommonConfigurationKeysPublic.FS_TRASH_INTERVAL_KEY, 1);
        FsShell shell = new FsShell(clientConf);
        verifyShellDeleteWithTrash(shell, encFile);
        Collection<FileStatus> trashRootsDelete1 = fs.getTrashRoots(true);
        Assert.assertEquals("Unexpected getTrashRoots result", 1, trashRootsDelete1.size());
        final Path nonEncFile = new Path("/nonEncFile");
        DFSTestUtil.createFile(fs, nonEncFile, len, ((short) (1)), 65261);
        verifyShellDeleteWithTrash(shell, nonEncFile);
        Collection<FileStatus> trashRootsDelete2 = fs.getTrashRoots(true);
        Assert.assertEquals("Unexpected getTrashRoots result", 2, trashRootsDelete2.size());
    }

    /**
     * This test tests that client will first lookup secrets map
     * for key provider uri from {@link Credentials} in
     * {@link UserGroupInformation}
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testProviderUriInCredentials() throws Exception {
        String dummyKeyProvider = "dummy://foo:bar@test_provider1";
        DFSClient client = cluster.getFileSystem().getClient();
        Credentials credentials = new Credentials();
        // Key provider uri should be in the secret map of credentials object with
        // namenode uri as key
        Text lookUpKey = HdfsKMSUtil.getKeyProviderMapKey(cluster.getFileSystem().getUri());
        credentials.addSecretKey(lookUpKey, DFSUtilClient.string2Bytes(dummyKeyProvider));
        client.ugi.addCredentials(credentials);
        Assert.assertEquals(("Client Key provider is different from provider in " + "credentials map"), dummyKeyProvider, client.getKeyProviderUri().toString());
    }

    /**
     * Testing the fallback behavior of keyProviderUri.
     * This test tests first the key provider uri is used from conf
     * and then used from serverDefaults.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testKeyProviderFallBackBehavior() throws IOException {
        Configuration clusterConf = cluster.getConfiguration(0);
        String dummyKeyProviderUri1 = "dummy://foo:bar@test_provider1";
        // set the key provider uri in conf.
        clusterConf.set(HADOOP_SECURITY_KEY_PROVIDER_PATH, dummyKeyProviderUri1);
        DFSClient mockClient = Mockito.spy(cluster.getFileSystem().getClient());
        // Namenode returning null as keyProviderUri in FSServerDefaults.
        FsServerDefaults serverDefaultsWithKeyProviderNull = getTestServerDefaults(null);
        Mockito.doReturn(serverDefaultsWithKeyProviderNull).when(mockClient).getServerDefaults();
        Assert.assertEquals("Key provider uri from client doesn't match with uri from conf", dummyKeyProviderUri1, mockClient.getKeyProviderUri().toString());
        Mockito.verify(mockClient, Mockito.times(1)).getServerDefaults();
        String dummyKeyProviderUri2 = "dummy://foo:bar@test_provider2";
        FsServerDefaults serverDefaultsWithDummyKeyProvider = getTestServerDefaults(dummyKeyProviderUri2);
        // Namenode returning dummyKeyProvider2 in serverDefaults.
        Mockito.doReturn(serverDefaultsWithDummyKeyProvider).when(mockClient).getServerDefaults();
        Assert.assertEquals("Key provider uri from client doesn't match with uri from namenode", dummyKeyProviderUri2, mockClient.getKeyProviderUri().toString());
        Mockito.verify(mockClient, Mockito.times(2)).getServerDefaults();
    }

    /**
     * This test makes sure the client gets the key provider uri from namenode
     * instead of its own conf.
     * This test assumes both the namenode and client are upgraded.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDifferentKMSProviderOnUpgradedNamenode() throws Exception {
        Configuration clusterConf = cluster.getConfiguration(0);
        URI namenodeKeyProviderUri = URI.create(getKeyProviderURI());
        Assert.assertEquals("Key Provider for client and namenode are different", namenodeKeyProviderUri, cluster.getFileSystem().getClient().getKeyProviderUri());
        // Unset the provider path in conf
        clusterConf.unset(HADOOP_SECURITY_KEY_PROVIDER_PATH);
        // Even after unsetting the local conf, the client key provider should be
        // the same as namenode's provider.
        Assert.assertEquals("Key Provider for client and namenode are different", namenodeKeyProviderUri, cluster.getFileSystem().getClient().getKeyProviderUri());
        // Set the provider path to some dummy scheme.
        clusterConf.set(HADOOP_SECURITY_KEY_PROVIDER_PATH, "dummy://foo:bar@test_provider1");
        // Even after pointing the conf to some dummy provider, the client key
        // provider should be the same as namenode's provider.
        Assert.assertEquals("Key Provider for client and namenode are different", namenodeKeyProviderUri, cluster.getFileSystem().getClient().getKeyProviderUri());
    }

    /**
     * This test makes sure the client trusts its local conf
     * This test assumes the client is upgraded but the namenode is not.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDifferentKMSProviderOnUnUpgradedNamenode() throws Exception {
        Configuration clusterConf = cluster.getConfiguration(0);
        URI namenodeKeyProviderUri = URI.create(getKeyProviderURI());
        URI clientKeyProviderUri = cluster.getFileSystem().getClient().getKeyProviderUri();
        Assert.assertNotNull(clientKeyProviderUri);
        // Since the client and the namenode share the same conf, they will have
        // identical key provider.
        Assert.assertEquals("Key Provider for client and namenode are different", namenodeKeyProviderUri, clientKeyProviderUri);
        String dummyKeyProviderUri = "dummy://foo:bar@test_provider";
        // Unset the provider path in conf.
        clusterConf.set(HADOOP_SECURITY_KEY_PROVIDER_PATH, dummyKeyProviderUri);
        FsServerDefaults spyServerDefaults = getTestServerDefaults(null);
        // Creating a fake serverdefaults so that we can simulate namenode not
        // being upgraded.
        DFSClient spyClient = Mockito.spy(cluster.getFileSystem().getClient());
        Mockito.doReturn(spyServerDefaults).when(spyClient).getServerDefaults();
        // Since FsServerDefaults#keyProviderUri is null, the client
        // will fallback to local conf which is null.
        clientKeyProviderUri = spyClient.getKeyProviderUri();
        Assert.assertEquals(("Client keyProvider should be " + dummyKeyProviderUri), dummyKeyProviderUri, clientKeyProviderUri.toString());
        Mockito.verify(spyClient, Mockito.times(1)).getServerDefaults();
    }

    /**
     * This test performs encrypted read/write and picks up the key provider uri
     * from the credentials and not the conf.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testEncryptedReadWriteUsingDiffKeyProvider() throws Exception {
        final HdfsAdmin dfsAdmin = new HdfsAdmin(FileSystem.getDefaultUri(conf), conf);
        Configuration clusterConf = cluster.getConfiguration(0);
        clusterConf.unset(HADOOP_SECURITY_KEY_PROVIDER_PATH);
        DFSClient client = cluster.getFileSystem().getClient();
        Credentials credentials = new Credentials();
        Text lookUpKey = HdfsKMSUtil.getKeyProviderMapKey(cluster.getFileSystem().getUri());
        credentials.addSecretKey(lookUpKey, DFSUtilClient.string2Bytes(getKeyProviderURI()));
        client.ugi.addCredentials(credentials);
        // Create a base file for comparison
        final Path baseFile = new Path("/base");
        final int len = 8192;
        DFSTestUtil.createFile(fs, baseFile, len, ((short) (1)), 65261);
        // Create the first enc file
        final Path zone = new Path("/zone");
        fs.mkdirs(zone);
        dfsAdmin.createEncryptionZone(zone, TEST_KEY, TestEncryptionZones.NO_TRASH);
        final Path encFile1 = new Path(zone, "myfile");
        DFSTestUtil.createFile(fs, encFile1, len, ((short) (1)), 65261);
        // Read them back in and compare byte-by-byte
        DFSTestUtil.verifyFilesEqual(fs, baseFile, encFile1, len);
    }

    /**
     * Test listing encryption zones after zones had been deleted,
     * but still exist under snapshots. This test first moves EZs
     * to trash folder, so that an inodereference is created for the EZ,
     * then it removes the EZ from trash folder to emulate condition where
     * the EZ inode will not be complete.
     */
    @Test
    public void testListEncryptionZonesWithSnapshots() throws Exception {
        final Path snapshottable = new Path("/zones");
        final Path zoneDirectChild = new Path(snapshottable, "zone1");
        final Path snapshottableChild = new Path(snapshottable, "child");
        final Path zoneSubChild = new Path(snapshottableChild, "zone2");
        fsWrapper.mkdir(zoneDirectChild, FsPermission.getDirDefault(), true);
        fsWrapper.mkdir(zoneSubChild, FsPermission.getDirDefault(), true);
        dfsAdmin.allowSnapshot(snapshottable);
        dfsAdmin.createEncryptionZone(zoneDirectChild, TEST_KEY, TestEncryptionZones.NO_TRASH);
        dfsAdmin.createEncryptionZone(zoneSubChild, TEST_KEY, TestEncryptionZones.NO_TRASH);
        final Path snap1 = fs.createSnapshot(snapshottable, "snap1");
        Configuration clientConf = new Configuration(conf);
        clientConf.setLong(CommonConfigurationKeysPublic.FS_TRASH_INTERVAL_KEY, 1);
        FsShell shell = new FsShell(clientConf);
        // will "trash" the zone under subfolder of snapshottable directory
        verifyShellDeleteWithTrash(shell, snapshottableChild);
        // permanently remove zone under subfolder of snapshottable directory
        fsWrapper.delete(shell.getCurrentTrashDir(snapshottableChild), true);
        final RemoteIterator<EncryptionZone> it = dfsAdmin.listEncryptionZones();
        boolean match = false;
        while (it.hasNext()) {
            EncryptionZone ez = it.next();
            Assert.assertNotEquals((("EncryptionZone " + (zoneSubChild.toString())) + " should not be listed."), ez.getPath(), zoneSubChild.toString());
        } 
        // will "trash" the zone direct child of snapshottable directory
        verifyShellDeleteWithTrash(shell, zoneDirectChild);
        // permanently remove zone direct child of snapshottable directory
        fsWrapper.delete(shell.getCurrentTrashDir(zoneDirectChild), true);
        Assert.assertFalse(("listEncryptionZones should not return anything, " + "since both EZs were deleted."), dfsAdmin.listEncryptionZones().hasNext());
    }

    /**
     * This test returns mocked kms token when
     * {@link WebHdfsFileSystem#addDelegationTokens(String, Credentials)} method
     * is called.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void addMockKmsToken() throws Exception {
        UserGroupInformation.createRemoteUser("JobTracker");
        WebHdfsFileSystem webfs = WebHdfsTestUtil.getWebHdfsFileSystem(conf, WEBHDFS_SCHEME);
        KeyProvider keyProvider = Mockito.mock(KeyProvider.class, Mockito.withSettings().extraInterfaces(DelegationTokenExtension.class, CryptoExtension.class));
        Mockito.when(keyProvider.getConf()).thenReturn(conf);
        byte[] testIdentifier = "Test identifier for delegation token".getBytes();
        Token testToken = new Token(testIdentifier, new byte[0], new Text("kms-dt"), new Text());
        Mockito.when(getCanonicalServiceName()).thenReturn("service");
        Mockito.when(getDelegationToken(ArgumentMatchers.anyString())).thenReturn(testToken);
        webfs.setTestProvider(keyProvider);
        Credentials creds = new Credentials();
        final Token<?>[] tokens = webfs.addDelegationTokens("JobTracker", creds);
        Assert.assertEquals(2, tokens.length);
        Assert.assertEquals(tokens[1], testToken);
        Assert.assertEquals(2, creds.numberOfTokens());
    }

    /**
     * Creates a file with stable {@link DistributedFileSystem}.
     * Tests the following 2 scenarios.
     * 1. The decrypted data using {@link WebHdfsFileSystem} should be same as
     * input data.
     * 2. Gets the underlying raw encrypted stream and verifies that the
     * encrypted data is different than input data.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testWebhdfsRead() throws Exception {
        Path zonePath = new Path("/TestEncryptionZone");
        fsWrapper.mkdir(zonePath, FsPermission.getDirDefault(), false);
        dfsAdmin.createEncryptionZone(zonePath, TEST_KEY, TestEncryptionZones.NO_TRASH);
        final Path encryptedFilePath = new Path("/TestEncryptionZone/encryptedFile.txt");
        final Path rawPath = new Path("/.reserved/raw/TestEncryptionZone/encryptedFile.txt");
        final String content = "hello world";
        // Create a file using DistributedFileSystem.
        DFSTestUtil.writeFile(fs, encryptedFilePath, content);
        final FileSystem webhdfs = WebHdfsTestUtil.getWebHdfsFileSystem(conf, WEBHDFS_SCHEME);
        // Verify whether decrypted input stream data is same as content.
        InputStream decryptedIputStream = webhdfs.open(encryptedFilePath);
        verifyStreamsSame(content, decryptedIputStream);
        // Get the underlying stream from CryptoInputStream which should be
        // raw encrypted bytes.
        InputStream cryptoStream = getWrappedStream();
        Assert.assertTrue(("cryptoStream should be an instance of " + "CryptoInputStream"), (cryptoStream instanceof CryptoInputStream));
        InputStream encryptedStream = getWrappedStream();
        // Verify that the data read from the raw input stream is different
        // from the original content. Also check it is identical to the raw
        // encrypted data from dfs.
        verifyRaw(content, encryptedStream, fs.open(rawPath));
    }

    /* Tests that if client is old and namenode is new then the
    data will be decrypted by datanode.
    @throws Exception
     */
    @Test
    public void testWebhdfsReadOldBehavior() throws Exception {
        Path zonePath = new Path("/TestEncryptionZone");
        fsWrapper.mkdir(zonePath, FsPermission.getDirDefault(), false);
        dfsAdmin.createEncryptionZone(zonePath, TEST_KEY, TestEncryptionZones.NO_TRASH);
        final Path encryptedFilePath = new Path("/TestEncryptionZone/foo");
        final String content = "hello world";
        // Create a file using DistributedFileSystem.
        DFSTestUtil.writeFile(fs, encryptedFilePath, content);
        InetSocketAddress addr = cluster.getNameNode().getHttpAddress();
        URL url = new URL("http", addr.getHostString(), addr.getPort(), (((WebHdfsFileSystem.PATH_PREFIX) + (encryptedFilePath.toString())) + "?op=OPEN"));
        // Return a connection with client not supporting EZ.
        HttpURLConnection namenodeConnection = TestEncryptionZones.returnConnection(url, "GET", false);
        String location = namenodeConnection.getHeaderField("Location");
        URL datanodeURL = new URL(location);
        String path = datanodeURL.getPath();
        Assert.assertEquals(((WebHdfsFileSystem.PATH_PREFIX) + (encryptedFilePath.toString())), path);
        HttpURLConnection datanodeConnection = TestEncryptionZones.returnConnection(datanodeURL, "GET", false);
        InputStream in = datanodeConnection.getInputStream();
        // Comparing with the original contents
        // and making sure they are decrypted.
        verifyStreamsSame(content, in);
    }

    /* Tests namenode returns path starting with /.reserved/raw if client
    supports EZ and not if otherwise
    @throws Exception
     */
    @Test
    public void testWebhfsEZRedirectLocation() throws Exception {
        Path zonePath = new Path("/TestEncryptionZone");
        fsWrapper.mkdir(zonePath, FsPermission.getDirDefault(), false);
        dfsAdmin.createEncryptionZone(zonePath, TEST_KEY, TestEncryptionZones.NO_TRASH);
        final Path encryptedFilePath = new Path("/TestEncryptionZone/foo");
        final String content = "hello world";
        // Create a file using DistributedFileSystem.
        DFSTestUtil.writeFile(fs, encryptedFilePath, content);
        InetSocketAddress addr = cluster.getNameNode().getHttpAddress();
        URL url = new URL("http", addr.getHostString(), addr.getPort(), (((WebHdfsFileSystem.PATH_PREFIX) + (encryptedFilePath.toString())) + "?op=OPEN"));
        // Return a connection with client not supporting EZ.
        HttpURLConnection namenodeConnection = TestEncryptionZones.returnConnection(url, "GET", false);
        Assert.assertNotNull(namenodeConnection.getHeaderField("Location"));
        URL datanodeUrl = new URL(namenodeConnection.getHeaderField("Location"));
        Assert.assertNotNull(datanodeUrl);
        String path = datanodeUrl.getPath();
        Assert.assertEquals(((WebHdfsFileSystem.PATH_PREFIX) + (encryptedFilePath.toString())), path);
        url = new URL("http", addr.getHostString(), addr.getPort(), (((WebHdfsFileSystem.PATH_PREFIX) + (encryptedFilePath.toString())) + "?op=OPEN"));
        // Return a connection with client supporting EZ.
        namenodeConnection = TestEncryptionZones.returnConnection(url, "GET", true);
        Assert.assertNotNull(namenodeConnection.getHeaderField("Location"));
        datanodeUrl = new URL(namenodeConnection.getHeaderField("Location"));
        Assert.assertNotNull(datanodeUrl);
        path = datanodeUrl.getPath();
        Assert.assertEquals((((WebHdfsFileSystem.PATH_PREFIX) + "/.reserved/raw") + (encryptedFilePath.toString())), path);
    }

    /* Test seek behavior of the webhdfs input stream which reads data from
    encryption zone.
     */
    @Test
    public void testPread() throws Exception {
        Path zonePath = new Path("/TestEncryptionZone");
        fsWrapper.mkdir(zonePath, FsPermission.getDirDefault(), false);
        dfsAdmin.createEncryptionZone(zonePath, TEST_KEY, TestEncryptionZones.NO_TRASH);
        final Path encryptedFilePath = new Path("/TestEncryptionZone/foo");
        // Create a file using DistributedFileSystem.
        WebHdfsFileSystem webfs = WebHdfsTestUtil.getWebHdfsFileSystem(conf, WEBHDFS_SCHEME);
        DFSTestUtil.createFile(webfs, encryptedFilePath, 1024, ((short) (1)), 65261);
        byte[] data = DFSTestUtil.readFileAsBytes(fs, encryptedFilePath);
        FSDataInputStream in = webfs.open(encryptedFilePath);
        for (int i = 0; i < 1024; i++) {
            in.seek(i);
            Assert.assertEquals(((data[i]) & 255), in.read());
        }
    }

    /**
     * Tests that namenode doesn't generate edek if we are writing to
     * /.reserved/raw directory.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testWriteToEZReservedRaw() throws Exception {
        String unEncryptedBytes = "hello world";
        // Create an Encryption Zone.
        final Path zonePath = new Path("/zone");
        fsWrapper.mkdir(zonePath, FsPermission.getDirDefault(), false);
        dfsAdmin.createEncryptionZone(zonePath, TEST_KEY, TestEncryptionZones.NO_TRASH);
        Path p1 = new Path(zonePath, "p1");
        Path reservedRawPath = new Path(("/.reserved/raw/" + (p1.toString())));
        // Create an empty file with /.reserved/raw/ path.
        OutputStream os = fs.create(reservedRawPath);
        os.close();
        try {
            fs.getXAttr(reservedRawPath, CRYPTO_XATTR_FILE_ENCRYPTION_INFO);
            Assert.fail("getXAttr should have thrown an exception");
        } catch (IOException ioe) {
            GenericTestUtils.assertExceptionContains(("At least one of the attributes provided was " + "not found."), ioe);
        }
    }
}

