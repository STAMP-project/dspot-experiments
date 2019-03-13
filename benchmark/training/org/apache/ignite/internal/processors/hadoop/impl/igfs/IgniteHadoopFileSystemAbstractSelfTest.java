/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.internal.processors.hadoop.impl.igfs;


import IgfsIpcEndpointType.TCP;
import IgfsPath.ROOT;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathExistsException;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.ignite.IgniteFileSystem;
import org.apache.ignite.configuration.FileSystemConfiguration;
import org.apache.ignite.hadoop.fs.v1.IgniteHadoopFileSystem;
import org.apache.ignite.igfs.IgfsBlockLocation;
import org.apache.ignite.igfs.IgfsFile;
import org.apache.ignite.igfs.IgfsIpcEndpointConfiguration;
import org.apache.ignite.igfs.IgfsMode;
import org.apache.ignite.igfs.IgfsPath;
import org.apache.ignite.internal.processors.igfs.IgfsCommonAbstractTest;
import org.apache.ignite.internal.processors.igfs.IgfsModeResolver;
import org.apache.ignite.internal.util.lang.GridAbsPredicate;
import org.apache.ignite.internal.util.typedef.F;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.lang.IgniteBiTuple;
import org.apache.ignite.spi.discovery.tcp.ipfinder.TcpDiscoveryIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.apache.ignite.testframework.GridTestUtils;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;


/**
 * Test hadoop file system implementation.
 */
@SuppressWarnings("all")
public abstract class IgniteHadoopFileSystemAbstractSelfTest extends IgfsCommonAbstractTest {
    /**
     * Primary file system authority.
     */
    private static final String PRIMARY_AUTHORITY = "igfs@";

    /**
     * Primary file systme URI.
     */
    protected static final String PRIMARY_URI = ("igfs://" + (IgniteHadoopFileSystemAbstractSelfTest.PRIMARY_AUTHORITY)) + "/";

    /**
     * Secondary file system authority.
     */
    private static final String SECONDARY_AUTHORITY = "igfs_secondary@127.0.0.1:11500";

    /**
     * Secondary file systme URI.
     */
    private static final String SECONDARY_URI = ("igfs://" + (IgniteHadoopFileSystemAbstractSelfTest.SECONDARY_AUTHORITY)) + "/";

    /**
     * Secondary file system configuration path.
     */
    private static final String SECONDARY_CFG_PATH = "/work/core-site-test.xml";

    /**
     * Secondary file system user.
     */
    private static final String SECONDARY_FS_USER = "secondary-default";

    /**
     * Secondary endpoint configuration.
     */
    protected static final IgfsIpcEndpointConfiguration SECONDARY_ENDPOINT_CFG;

    /**
     * Group size.
     */
    public static final int GRP_SIZE = 128;

    /**
     * Group size.
     */
    public static final int GRID_COUNT = 4;

    /**
     * Path to the default hadoop configuration.
     */
    public static final String HADOOP_FS_CFG = "examples/config/filesystem/core-site.xml";

    /**
     * Thread count for multithreaded tests.
     */
    private static final int THREAD_CNT = 8;

    /**
     * IP finder.
     */
    private final TcpDiscoveryIpFinder IP_FINDER = new TcpDiscoveryVmIpFinder(true);

    /**
     * Barrier for multithreaded tests.
     */
    private static CyclicBarrier barrier;

    /**
     * File system.
     */
    protected static FileSystem fs;

    /**
     * Default IGFS mode.
     */
    protected final IgfsMode mode;

    /**
     * Skip embedded mode flag.
     */
    private final boolean skipEmbed;

    /**
     * Skip local shmem flag.
     */
    private final boolean skipLocShmem;

    /**
     * Endpoint.
     */
    private final String endpoint;

    /**
     * Primary file system URI.
     */
    protected URI primaryFsUri;

    /**
     * Primary file system configuration.
     */
    protected Configuration primaryFsCfg;

    static {
        SECONDARY_ENDPOINT_CFG = new IgfsIpcEndpointConfiguration();
        IgniteHadoopFileSystemAbstractSelfTest.SECONDARY_ENDPOINT_CFG.setType(TCP);
        IgniteHadoopFileSystemAbstractSelfTest.SECONDARY_ENDPOINT_CFG.setPort(11500);
    }

    /**
     * File statuses comparator.
     */
    private static final Comparator<FileStatus> STATUS_COMPARATOR = new Comparator<FileStatus>() {
        @SuppressWarnings("deprecation")
        @Override
        public int compare(FileStatus o1, FileStatus o2) {
            if ((o1 == null) || (o2 == null))
                return o1 == o2 ? 0 : o1 == null ? -1 : 1;

            return (o1.isDir()) == (o2.isDir()) ? o1.getPath().compareTo(o2.getPath()) : o1.isDir() ? -1 : 1;
        }
    };

    /**
     * Constructor.
     *
     * @param mode
     * 		Default IGFS mode.
     * @param skipEmbed
     * 		Whether to skip embedded mode.
     * @param skipLocShmem
     * 		Whether to skip local shmem mode.
     * @param skipLocTcp
     * 		Whether to skip local TCP mode.
     */
    protected IgniteHadoopFileSystemAbstractSelfTest(IgfsMode mode, boolean skipEmbed, boolean skipLocShmem) {
        this.mode = mode;
        this.skipEmbed = skipEmbed;
        this.skipLocShmem = skipLocShmem;
        endpoint = (skipLocShmem) ? "127.0.0.1:10500" : "shmem:10500";
        setAddresses(Collections.singleton("127.0.0.1:47500..47509"));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testGetUriIfFSIsNotInitialized() throws Exception {
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return new IgniteHadoopFileSystem().getUri();
            }
        }, IllegalStateException.class, "URI is null (was IgniteHadoopFileSystem properly initialized?)");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @SuppressWarnings("NullableProblems")
    @Test
    public void testInitializeCheckParametersNameIsNull() throws Exception {
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                new IgniteHadoopFileSystem().initialize(null, new Configuration());
                return null;
            }
        }, NullPointerException.class, "Ouch! Argument cannot be null: name");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @SuppressWarnings("NullableProblems")
    @Test
    public void testInitializeCheckParametersCfgIsNull() throws Exception {
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                new IgniteHadoopFileSystem().initialize(new URI(""), null);
                return null;
            }
        }, NullPointerException.class, "Ouch! Argument cannot be null: cfg");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testInitialize() throws Exception {
        final IgniteHadoopFileSystem fs = new IgniteHadoopFileSystem();
        fs.initialize(primaryFsUri, primaryFsCfg);
        // Check repeatable initialization.
        try {
            fs.initialize(primaryFsUri, primaryFsCfg);
            fail();
        } catch (IOException e) {
            assertTrue(e.getMessage().contains("File system is already initialized"));
        }
        assertEquals(primaryFsUri, fs.getUri());
        assertEquals(0, fs.getUsed());
        fs.close();
    }

    /**
     * Test how IPC cache map works.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testIpcCache() throws Exception {
        HadoopIgfsEx hadoop = GridTestUtils.getFieldValue(IgniteHadoopFileSystemAbstractSelfTest.fs, "rmtClient", "delegateRef", "value", "hadoop");
        if (hadoop instanceof HadoopIgfsOutProc) {
            FileSystem fsOther = null;
            try {
                Field field = HadoopIgfsIpcIo.class.getDeclaredField("ipcCache");
                field.setAccessible(true);
                Map<String, HadoopIgfsIpcIo> cache = ((Map<String, HadoopIgfsIpcIo>) (field.get(null)));
                Configuration cfg = configuration(IgniteHadoopFileSystemAbstractSelfTest.PRIMARY_AUTHORITY, skipEmbed, skipLocShmem);
                // we disable caching in order to obtain new FileSystem instance.
                cfg.setBoolean("fs.igfs.impl.disable.cache", true);
                // Initial cache size.
                int initSize = cache.size();
                // Ensure that when IO is used by multiple file systems and one of them is closed, IO is not stopped.
                fsOther = FileSystem.get(new URI(IgniteHadoopFileSystemAbstractSelfTest.PRIMARY_URI), cfg);
                assert (IgniteHadoopFileSystemAbstractSelfTest.fs) != fsOther;
                assertEquals(initSize, cache.size());
                fsOther.close();
                assertEquals(initSize, cache.size());
                Field stopField = HadoopIgfsIpcIo.class.getDeclaredField("stopping");
                stopField.setAccessible(true);
                HadoopIgfsIpcIo io = null;
                for (Map.Entry<String, HadoopIgfsIpcIo> ioEntry : cache.entrySet()) {
                    if (endpoint.contains(ioEntry.getKey())) {
                        io = ioEntry.getValue();
                        break;
                    }
                }
                assert io != null;
                assert !((Boolean) (stopField.get(io)));
                // Ensure that IO is stopped when nobody else is need it.
                IgniteHadoopFileSystemAbstractSelfTest.fs.close();
                assert initSize >= (cache.size());
                assert ((Boolean) (stopField.get(io)));
            } finally {
                U.closeQuiet(fsOther);
            }
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCloseIfNotInitialized() throws Exception {
        final FileSystem fs = new IgniteHadoopFileSystem();
        // Check close makes nothing harmful.
        fs.close();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testClose() throws Exception {
        final Path path = new Path("dir");
        IgniteHadoopFileSystemAbstractSelfTest.fs.close();
        // Check double close makes nothing harmful.
        IgniteHadoopFileSystemAbstractSelfTest.fs.close();
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Nullable
            @Override
            public Object call() throws Exception {
                IgniteHadoopFileSystemAbstractSelfTest.fs.initialize(primaryFsUri, primaryFsCfg);
                return null;
            }
        }, IOException.class, "File system is stopped.");
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Nullable
            @Override
            public Object call() throws Exception {
                IgniteHadoopFileSystemAbstractSelfTest.fs.setPermission(path, FsPermission.createImmutable(((short) (777))));
                return null;
            }
        }, IOException.class, "File system is stopped.");
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Nullable
            @Override
            public Object call() throws Exception {
                IgniteHadoopFileSystemAbstractSelfTest.fs.setOwner(path, "user", "group");
                return null;
            }
        }, IOException.class, "File system is stopped.");
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return IgniteHadoopFileSystemAbstractSelfTest.fs.open(path, 256);
            }
        }, IOException.class, "File system is stopped.");
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return IgniteHadoopFileSystemAbstractSelfTest.fs.create(path);
            }
        }, IOException.class, "File system is stopped.");
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return IgniteHadoopFileSystemAbstractSelfTest.fs.append(path);
            }
        }, IOException.class, "File system is stopped.");
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return IgniteHadoopFileSystemAbstractSelfTest.fs.rename(path, new Path("newDir"));
            }
        }, IOException.class, "File system is stopped.");
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return IgniteHadoopFileSystemAbstractSelfTest.fs.delete(path, true);
            }
        }, IOException.class, "File system is stopped.");
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return IgniteHadoopFileSystemAbstractSelfTest.fs.listStatus(path);
            }
        }, IOException.class, "File system is stopped.");
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return IgniteHadoopFileSystemAbstractSelfTest.fs.mkdirs(path);
            }
        }, IOException.class, "File system is stopped.");
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return IgniteHadoopFileSystemAbstractSelfTest.fs.getFileStatus(path);
            }
        }, IOException.class, "File system is stopped.");
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return IgniteHadoopFileSystemAbstractSelfTest.fs.getFileBlockLocations(new FileStatus(1L, false, 1, 1L, 1L, new Path("path")), 0L, 256L);
            }
        }, IOException.class, "File system is stopped.");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateCheckParameters() throws Exception {
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return IgniteHadoopFileSystemAbstractSelfTest.fs.create(null);
            }
        }, NullPointerException.class, "Ouch! Argument cannot be null: f");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testCreateBase() throws Exception {
        Path fsHome = new Path(primaryFsUri);
        Path dir = new Path(fsHome, "/someDir1/someDir2/someDir3");
        Path file = new Path(dir, "someFile");
        assertPathDoesNotExist(IgniteHadoopFileSystemAbstractSelfTest.fs, file);
        FsPermission fsPerm = new FsPermission(((short) (644)));
        FSDataOutputStream os = IgniteHadoopFileSystemAbstractSelfTest.fs.create(file, fsPerm, false, 1, ((short) (1)), 1L, null);
        // Try to write something in file.
        os.write("abc".getBytes());
        os.close();
        // Check file status.
        FileStatus fileStatus = IgniteHadoopFileSystemAbstractSelfTest.fs.getFileStatus(file);
        assertFalse(fileStatus.isDir());
        assertEquals(file, fileStatus.getPath());
        assertEquals(fsPerm, fileStatus.getPermission());
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testCreateCheckOverwrite() throws Exception {
        Path fsHome = new Path(primaryFsUri);
        Path dir = new Path(fsHome, "/someDir1/someDir2/someDir3");
        final Path file = new Path(dir, "someFile");
        FSDataOutputStream out = IgniteHadoopFileSystemAbstractSelfTest.fs.create(file, FsPermission.getDefault(), false, (64 * 1024), IgniteHadoopFileSystemAbstractSelfTest.fs.getDefaultReplication(), IgniteHadoopFileSystemAbstractSelfTest.fs.getDefaultBlockSize(), null);
        out.close();
        // Check intermediate directory permissions.
        assertEquals(FsPermission.getDefault(), IgniteHadoopFileSystemAbstractSelfTest.fs.getFileStatus(dir).getPermission());
        assertEquals(FsPermission.getDefault(), IgniteHadoopFileSystemAbstractSelfTest.fs.getFileStatus(dir.getParent()).getPermission());
        assertEquals(FsPermission.getDefault(), IgniteHadoopFileSystemAbstractSelfTest.fs.getFileStatus(dir.getParent().getParent()).getPermission());
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return IgniteHadoopFileSystemAbstractSelfTest.fs.create(file, FsPermission.getDefault(), false, 1024, ((short) (1)), 2048, null);
            }
        }, PathExistsException.class, null);
        // Overwrite should be successful.
        FSDataOutputStream out1 = IgniteHadoopFileSystemAbstractSelfTest.fs.create(file, true);
        out1.close();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDeleteIfNoSuchPath() throws Exception {
        Path fsHome = new Path(primaryFsUri);
        Path dir = new Path(fsHome, "/someDir1/someDir2/someDir3");
        assertPathDoesNotExist(IgniteHadoopFileSystemAbstractSelfTest.fs, dir);
        assertFalse(IgniteHadoopFileSystemAbstractSelfTest.fs.delete(dir, true));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDeleteSuccessfulIfPathIsOpenedToRead() throws Exception {
        Path fsHome = new Path(primaryFsUri);
        final Path file = new Path(fsHome, "myFile");
        FSDataOutputStream os = IgniteHadoopFileSystemAbstractSelfTest.fs.create(file, false, 128);
        final int cnt = 5 * (FileSystemConfiguration.DFLT_BLOCK_SIZE);// Write 5 blocks.

        for (int i = 0; i < cnt; i++)
            os.writeInt(i);

        os.close();
        final FSDataInputStream is = IgniteHadoopFileSystemAbstractSelfTest.fs.open(file, (-1));
        for (int i = 0; i < (cnt / 2); i++)
            assertEquals(i, is.readInt());

        assert IgniteHadoopFileSystemAbstractSelfTest.fs.delete(file, false);
        assert !(IgniteHadoopFileSystemAbstractSelfTest.fs.exists(file));
        is.close();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDeleteIfFilePathExists() throws Exception {
        Path fsHome = new Path(primaryFsUri);
        Path file = new Path(fsHome, "myFile");
        FSDataOutputStream os = IgniteHadoopFileSystemAbstractSelfTest.fs.create(file);
        os.close();
        assertTrue(IgniteHadoopFileSystemAbstractSelfTest.fs.delete(file, false));
        assertPathDoesNotExist(IgniteHadoopFileSystemAbstractSelfTest.fs, file);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDeleteIfDirectoryPathExists() throws Exception {
        Path fsHome = new Path(primaryFsUri);
        Path dir = new Path(fsHome, "/someDir1/someDir2/someDir3");
        FSDataOutputStream os = IgniteHadoopFileSystemAbstractSelfTest.fs.create(dir);
        os.close();
        assertTrue(IgniteHadoopFileSystemAbstractSelfTest.fs.delete(dir, false));
        assertPathDoesNotExist(IgniteHadoopFileSystemAbstractSelfTest.fs, dir);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDeleteFailsIfNonRecursive() throws Exception {
        Path fsHome = new Path(primaryFsUri);
        Path someDir3 = new Path(fsHome, "/someDir1/someDir2/someDir3");
        IgniteHadoopFileSystemAbstractSelfTest.fs.create(someDir3).close();
        Path someDir2 = new Path(fsHome, "/someDir1/someDir2");
        assertFalse(IgniteHadoopFileSystemAbstractSelfTest.fs.delete(someDir2, false));
        assertPathExists(IgniteHadoopFileSystemAbstractSelfTest.fs, someDir2);
        assertPathExists(IgniteHadoopFileSystemAbstractSelfTest.fs, someDir3);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDeleteRecursively() throws Exception {
        Path fsHome = new Path(primaryFsUri);
        Path someDir3 = new Path(fsHome, "/someDir1/someDir2/someDir3");
        FSDataOutputStream os = IgniteHadoopFileSystemAbstractSelfTest.fs.create(someDir3);
        os.close();
        Path someDir2 = new Path(fsHome, "/someDir1/someDir2");
        assertTrue(IgniteHadoopFileSystemAbstractSelfTest.fs.delete(someDir2, true));
        assertPathDoesNotExist(IgniteHadoopFileSystemAbstractSelfTest.fs, someDir2);
        assertPathDoesNotExist(IgniteHadoopFileSystemAbstractSelfTest.fs, someDir3);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDeleteRecursivelyFromRoot() throws Exception {
        Path fsHome = new Path(primaryFsUri);
        Path someDir3 = new Path(fsHome, "/someDir1/someDir2/someDir3");
        FSDataOutputStream os = IgniteHadoopFileSystemAbstractSelfTest.fs.create(someDir3);
        os.close();
        Path root = new Path(fsHome, "/");
        assertFalse(IgniteHadoopFileSystemAbstractSelfTest.fs.delete(root, true));
        assertTrue(IgniteHadoopFileSystemAbstractSelfTest.fs.delete(new Path("/someDir1"), true));
        assertPathDoesNotExist(IgniteHadoopFileSystemAbstractSelfTest.fs, someDir3);
        assertPathDoesNotExist(IgniteHadoopFileSystemAbstractSelfTest.fs, new Path(fsHome, "/someDir1/someDir2"));
        assertPathDoesNotExist(IgniteHadoopFileSystemAbstractSelfTest.fs, new Path(fsHome, "/someDir1"));
        assertPathExists(IgniteHadoopFileSystemAbstractSelfTest.fs, root);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testSetPermissionCheckDefaultPermission() throws Exception {
        Path fsHome = new Path(primaryFsUri);
        Path file = new Path(fsHome, "/tmp/my");
        FSDataOutputStream os = IgniteHadoopFileSystemAbstractSelfTest.fs.create(file, FsPermission.getDefault(), false, (64 * 1024), IgniteHadoopFileSystemAbstractSelfTest.fs.getDefaultReplication(), IgniteHadoopFileSystemAbstractSelfTest.fs.getDefaultBlockSize(), null);
        os.close();
        IgniteHadoopFileSystemAbstractSelfTest.fs.setPermission(file, null);
        assertEquals(FsPermission.getDefault(), IgniteHadoopFileSystemAbstractSelfTest.fs.getFileStatus(file).getPermission());
        assertEquals(FsPermission.getDefault(), IgniteHadoopFileSystemAbstractSelfTest.fs.getFileStatus(file.getParent()).getPermission());
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testSetPermissionCheckNonRecursiveness() throws Exception {
        Path fsHome = new Path(primaryFsUri);
        Path file = new Path(fsHome, "/tmp/my");
        FSDataOutputStream os = IgniteHadoopFileSystemAbstractSelfTest.fs.create(file, FsPermission.getDefault(), false, (64 * 1024), IgniteHadoopFileSystemAbstractSelfTest.fs.getDefaultReplication(), IgniteHadoopFileSystemAbstractSelfTest.fs.getDefaultBlockSize(), null);
        os.close();
        Path tmpDir = new Path(fsHome, "/tmp");
        FsPermission perm = new FsPermission(((short) (123)));
        IgniteHadoopFileSystemAbstractSelfTest.fs.setPermission(tmpDir, perm);
        assertEquals(perm, IgniteHadoopFileSystemAbstractSelfTest.fs.getFileStatus(tmpDir).getPermission());
        assertEquals(FsPermission.getDefault(), IgniteHadoopFileSystemAbstractSelfTest.fs.getFileStatus(file).getPermission());
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @SuppressWarnings("OctalInteger")
    @Test
    public void testSetPermission() throws Exception {
        Path fsHome = new Path(primaryFsUri);
        Path file = new Path(fsHome, "/tmp/my");
        FSDataOutputStream os = IgniteHadoopFileSystemAbstractSelfTest.fs.create(file);
        os.close();
        for (short i = 0; i <= 511; i += 7) {
            FsPermission perm = new FsPermission(i);
            IgniteHadoopFileSystemAbstractSelfTest.fs.setPermission(file, perm);
            assertEquals(perm, IgniteHadoopFileSystemAbstractSelfTest.fs.getFileStatus(file).getPermission());
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSetPermissionIfOutputStreamIsNotClosed() throws Exception {
        Path fsHome = new Path(primaryFsUri);
        Path file = new Path(fsHome, "myFile");
        FsPermission perm = new FsPermission(((short) (123)));
        FSDataOutputStream os = IgniteHadoopFileSystemAbstractSelfTest.fs.create(file);
        IgniteHadoopFileSystemAbstractSelfTest.fs.setPermission(file, perm);
        os.close();
        assertEquals(perm, IgniteHadoopFileSystemAbstractSelfTest.fs.getFileStatus(file).getPermission());
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSetOwnerCheckParametersPathIsNull() throws Exception {
        Path fsHome = new Path(primaryFsUri);
        final Path file = new Path(fsHome, "/tmp/my");
        FSDataOutputStream os = IgniteHadoopFileSystemAbstractSelfTest.fs.create(file);
        os.close();
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                IgniteHadoopFileSystemAbstractSelfTest.fs.setOwner(null, "aUser", "aGroup");
                return null;
            }
        }, NullPointerException.class, "Ouch! Argument cannot be null: p");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSetOwnerCheckParametersUserIsNull() throws Exception {
        Path fsHome = new Path(primaryFsUri);
        final Path file = new Path(fsHome, "/tmp/my");
        FSDataOutputStream os = IgniteHadoopFileSystemAbstractSelfTest.fs.create(file);
        os.close();
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                IgniteHadoopFileSystemAbstractSelfTest.fs.setOwner(file, null, "aGroup");
                return null;
            }
        }, NullPointerException.class, "Ouch! Argument cannot be null: username");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSetOwnerCheckParametersGroupIsNull() throws Exception {
        Path fsHome = new Path(primaryFsUri);
        final Path file = new Path(fsHome, "/tmp/my");
        FSDataOutputStream os = IgniteHadoopFileSystemAbstractSelfTest.fs.create(file);
        os.close();
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                IgniteHadoopFileSystemAbstractSelfTest.fs.setOwner(file, "aUser", null);
                return null;
            }
        }, NullPointerException.class, "Ouch! Argument cannot be null: grpName");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSetOwner() throws Exception {
        Path fsHome = new Path(primaryFsUri);
        final Path file = new Path(fsHome, "/tmp/my");
        FSDataOutputStream os = IgniteHadoopFileSystemAbstractSelfTest.fs.create(file);
        os.close();
        assertEquals(getClientFsUser(), IgniteHadoopFileSystemAbstractSelfTest.fs.getFileStatus(file).getOwner());
        IgniteHadoopFileSystemAbstractSelfTest.fs.setOwner(file, "aUser", "aGroup");
        assertEquals("aUser", IgniteHadoopFileSystemAbstractSelfTest.fs.getFileStatus(file).getOwner());
        assertEquals("aGroup", IgniteHadoopFileSystemAbstractSelfTest.fs.getFileStatus(file).getGroup());
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSetTimes() throws Exception {
        Path fsHome = new Path(primaryFsUri);
        final Path file = new Path(fsHome, "/heartbeatTs");
        IgniteHadoopFileSystemAbstractSelfTest.fs.create(file).close();
        FileStatus status = IgniteHadoopFileSystemAbstractSelfTest.fs.getFileStatus(file);
        assertTrue(((status.getAccessTime()) > 0));
        assertTrue(((status.getModificationTime()) > 0));
        long mtime = (System.currentTimeMillis()) - 5000;
        long atime = (System.currentTimeMillis()) - 4000;
        IgniteHadoopFileSystemAbstractSelfTest.fs.setTimes(file, mtime, atime);
        status = IgniteHadoopFileSystemAbstractSelfTest.fs.getFileStatus(file);
        assertEquals(mtime, status.getModificationTime());
        assertEquals(atime, status.getAccessTime());
        mtime -= 5000;
        IgniteHadoopFileSystemAbstractSelfTest.fs.setTimes(file, mtime, (-1));
        status = IgniteHadoopFileSystemAbstractSelfTest.fs.getFileStatus(file);
        assertEquals(mtime, status.getModificationTime());
        assertEquals(atime, status.getAccessTime());
        atime -= 5000;
        IgniteHadoopFileSystemAbstractSelfTest.fs.setTimes(file, (-1), atime);
        status = IgniteHadoopFileSystemAbstractSelfTest.fs.getFileStatus(file);
        assertEquals(mtime, status.getModificationTime());
        assertEquals(atime, status.getAccessTime());
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSetOwnerIfOutputStreamIsNotClosed() throws Exception {
        Path fsHome = new Path(primaryFsUri);
        Path file = new Path(fsHome, "myFile");
        FSDataOutputStream os = IgniteHadoopFileSystemAbstractSelfTest.fs.create(file);
        IgniteHadoopFileSystemAbstractSelfTest.fs.setOwner(file, "aUser", "aGroup");
        os.close();
        assertEquals("aUser", IgniteHadoopFileSystemAbstractSelfTest.fs.getFileStatus(file).getOwner());
        assertEquals("aGroup", IgniteHadoopFileSystemAbstractSelfTest.fs.getFileStatus(file).getGroup());
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSetOwnerCheckNonRecursiveness() throws Exception {
        Path fsHome = new Path(primaryFsUri);
        Path file = new Path(fsHome, "/tmp/my");
        FSDataOutputStream os = IgniteHadoopFileSystemAbstractSelfTest.fs.create(file);
        os.close();
        Path tmpDir = new Path(fsHome, "/tmp");
        IgniteHadoopFileSystemAbstractSelfTest.fs.setOwner(file, "fUser", "fGroup");
        IgniteHadoopFileSystemAbstractSelfTest.fs.setOwner(tmpDir, "dUser", "dGroup");
        assertEquals("dUser", IgniteHadoopFileSystemAbstractSelfTest.fs.getFileStatus(tmpDir).getOwner());
        assertEquals("dGroup", IgniteHadoopFileSystemAbstractSelfTest.fs.getFileStatus(tmpDir).getGroup());
        assertEquals("fUser", IgniteHadoopFileSystemAbstractSelfTest.fs.getFileStatus(file).getOwner());
        assertEquals("fGroup", IgniteHadoopFileSystemAbstractSelfTest.fs.getFileStatus(file).getGroup());
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testOpenCheckParametersPathIsNull() throws Exception {
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return IgniteHadoopFileSystemAbstractSelfTest.fs.open(null, 1024);
            }
        }, NullPointerException.class, "Ouch! Argument cannot be null: f");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testOpenNoSuchPath() throws Exception {
        Path fsHome = new Path(primaryFsUri);
        final Path file = new Path(fsHome, "someFile");
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return IgniteHadoopFileSystemAbstractSelfTest.fs.open(file, 1024);
            }
        }, FileNotFoundException.class, null);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testOpenIfPathIsAlreadyOpened() throws Exception {
        Path fsHome = new Path(primaryFsUri);
        Path file = new Path(fsHome, "someFile");
        FSDataOutputStream os = IgniteHadoopFileSystemAbstractSelfTest.fs.create(file);
        os.close();
        FSDataInputStream is1 = IgniteHadoopFileSystemAbstractSelfTest.fs.open(file);
        FSDataInputStream is2 = IgniteHadoopFileSystemAbstractSelfTest.fs.open(file);
        is1.close();
        is2.close();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testOpen() throws Exception {
        Path fsHome = new Path(primaryFsUri);
        Path file = new Path(fsHome, "someFile");
        int cnt = 2 * 1024;
        try (FSDataOutputStream out = IgniteHadoopFileSystemAbstractSelfTest.fs.create(file, true, 1024)) {
            for (long i = 0; i < cnt; i++)
                out.writeLong(i);

        }
        assertEquals(getClientFsUser(), IgniteHadoopFileSystemAbstractSelfTest.fs.getFileStatus(file).getOwner());
        try (FSDataInputStream in = IgniteHadoopFileSystemAbstractSelfTest.fs.open(file, 1024)) {
            for (long i = 0; i < cnt; i++)
                assertEquals(i, in.readLong());

        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testAppendCheckParametersPathIsNull() throws Exception {
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return IgniteHadoopFileSystemAbstractSelfTest.fs.append(null);
            }
        }, NullPointerException.class, "Ouch! Argument cannot be null: f");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testAppendIfPathPointsToDirectory() throws Exception {
        final Path fsHome = new Path(primaryFsUri);
        final Path dir = new Path(fsHome, "/tmp");
        Path file = new Path(dir, "my");
        FSDataOutputStream os = IgniteHadoopFileSystemAbstractSelfTest.fs.create(file);
        os.close();
        GridTestUtils.assertThrowsInherited(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return IgniteHadoopFileSystemAbstractSelfTest.fs.append(new Path(fsHome, dir), 1024);
            }
        }, IOException.class, null);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testAppendIfFileIsAlreadyBeingOpenedToWrite() throws Exception {
        Path fsHome = new Path(primaryFsUri);
        final Path file = new Path(fsHome, "someFile");
        FSDataOutputStream os = IgniteHadoopFileSystemAbstractSelfTest.fs.create(file);
        os.close();
        FSDataOutputStream appendOs = IgniteHadoopFileSystemAbstractSelfTest.fs.append(file);
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return IgniteHadoopFileSystemAbstractSelfTest.fs.append(file);
            }
        }, IOException.class, null);
        appendOs.close();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testAppend() throws Exception {
        Path fsHome = new Path(primaryFsUri);
        Path file = new Path(fsHome, "someFile");
        int cnt = 1024;
        FSDataOutputStream out = IgniteHadoopFileSystemAbstractSelfTest.fs.create(file, true, 1024);
        for (int i = 0; i < cnt; i++)
            out.writeLong(i);

        out.close();
        out = IgniteHadoopFileSystemAbstractSelfTest.fs.append(file);
        for (int i = cnt; i < (cnt * 2); i++)
            out.writeLong(i);

        out.close();
        FSDataInputStream in = IgniteHadoopFileSystemAbstractSelfTest.fs.open(file, 1024);
        for (int i = 0; i < (cnt * 2); i++)
            assertEquals(i, in.readLong());

        in.close();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testRenameCheckParametersSrcPathIsNull() throws Exception {
        Path fsHome = new Path(primaryFsUri);
        final Path file = new Path(fsHome, "someFile");
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return IgniteHadoopFileSystemAbstractSelfTest.fs.rename(null, file);
            }
        }, NullPointerException.class, "Ouch! Argument cannot be null: src");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testRenameCheckParametersDstPathIsNull() throws Exception {
        Path fsHome = new Path(primaryFsUri);
        final Path file = new Path(fsHome, "someFile");
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return IgniteHadoopFileSystemAbstractSelfTest.fs.rename(file, null);
            }
        }, NullPointerException.class, "Ouch! Argument cannot be null: dst");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testRenameIfSrcPathDoesNotExist() throws Exception {
        Path fsHome = new Path(primaryFsUri);
        Path srcFile = new Path(fsHome, "srcFile");
        Path dstFile = new Path(fsHome, "dstFile");
        assertPathDoesNotExist(IgniteHadoopFileSystemAbstractSelfTest.fs, srcFile);
        assertFalse(IgniteHadoopFileSystemAbstractSelfTest.fs.rename(srcFile, dstFile));
        assertPathDoesNotExist(IgniteHadoopFileSystemAbstractSelfTest.fs, dstFile);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testRenameIfSrcPathIsAlreadyBeingOpenedToWrite() throws Exception {
        Path fsHome = new Path(primaryFsUri);
        Path srcFile = new Path(fsHome, "srcFile");
        Path dstFile = new Path(fsHome, "dstFile");
        FSDataOutputStream os = IgniteHadoopFileSystemAbstractSelfTest.fs.create(srcFile);
        os.close();
        os = IgniteHadoopFileSystemAbstractSelfTest.fs.append(srcFile);
        assertTrue(IgniteHadoopFileSystemAbstractSelfTest.fs.rename(srcFile, dstFile));
        assertPathExists(IgniteHadoopFileSystemAbstractSelfTest.fs, dstFile);
        String testStr = "Test";
        try {
            os.writeBytes(testStr);
        } finally {
            os.close();
        }
        try (FSDataInputStream is = IgniteHadoopFileSystemAbstractSelfTest.fs.open(dstFile)) {
            byte[] buf = new byte[testStr.getBytes().length];
            is.readFully(buf);
            assertEquals(testStr, new String(buf));
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testRenameFileIfDstPathExists() throws Exception {
        Path fsHome = new Path(primaryFsUri);
        Path srcFile = new Path(fsHome, "srcFile");
        Path dstFile = new Path(fsHome, "dstFile");
        FSDataOutputStream os = IgniteHadoopFileSystemAbstractSelfTest.fs.create(srcFile);
        os.close();
        os = IgniteHadoopFileSystemAbstractSelfTest.fs.create(dstFile);
        os.close();
        assertFalse(IgniteHadoopFileSystemAbstractSelfTest.fs.rename(srcFile, dstFile));
        assertPathExists(IgniteHadoopFileSystemAbstractSelfTest.fs, srcFile);
        assertPathExists(IgniteHadoopFileSystemAbstractSelfTest.fs, dstFile);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testRenameFile() throws Exception {
        Path fsHome = new Path(primaryFsUri);
        Path srcFile = new Path(fsHome, "/tmp/srcFile");
        Path dstFile = new Path(fsHome, "/tmp/dstFile");
        FSDataOutputStream os = IgniteHadoopFileSystemAbstractSelfTest.fs.create(srcFile);
        os.close();
        assertTrue(IgniteHadoopFileSystemAbstractSelfTest.fs.rename(srcFile, dstFile));
        assertPathDoesNotExist(IgniteHadoopFileSystemAbstractSelfTest.fs, srcFile);
        assertPathExists(IgniteHadoopFileSystemAbstractSelfTest.fs, dstFile);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testRenameIfSrcPathIsAlreadyBeingOpenedToRead() throws Exception {
        Path fsHome = new Path(primaryFsUri);
        Path srcFile = new Path(fsHome, "srcFile");
        Path dstFile = new Path(fsHome, "dstFile");
        FSDataOutputStream os = IgniteHadoopFileSystemAbstractSelfTest.fs.create(srcFile);
        int cnt = 1024;
        for (int i = 0; i < cnt; i++)
            os.writeInt(i);

        os.close();
        FSDataInputStream is = IgniteHadoopFileSystemAbstractSelfTest.fs.open(srcFile);
        for (int i = 0; i < cnt; i++) {
            // Rename file during the read process.
            if (i == 100)
                assertTrue(IgniteHadoopFileSystemAbstractSelfTest.fs.rename(srcFile, dstFile));

            assertEquals(i, is.readInt());
        }
        assertPathDoesNotExist(IgniteHadoopFileSystemAbstractSelfTest.fs, srcFile);
        assertPathExists(IgniteHadoopFileSystemAbstractSelfTest.fs, dstFile);
        os.close();
        is.close();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testRenameDirectoryIfDstPathExists() throws Exception {
        Path fsHome = new Path(primaryFsUri);
        Path srcDir = new Path(fsHome, "/tmp/");
        Path dstDir = new Path(fsHome, "/tmpNew/");
        FSDataOutputStream os = IgniteHadoopFileSystemAbstractSelfTest.fs.create(new Path(srcDir, "file1"));
        os.close();
        os = IgniteHadoopFileSystemAbstractSelfTest.fs.create(new Path(dstDir, "file2"));
        os.close();
        assertTrue((((("Rename succeeded [srcDir=" + srcDir) + ", dstDir=") + dstDir) + ']'), IgniteHadoopFileSystemAbstractSelfTest.fs.rename(srcDir, dstDir));
        assertPathExists(IgniteHadoopFileSystemAbstractSelfTest.fs, dstDir);
        assertPathExists(IgniteHadoopFileSystemAbstractSelfTest.fs, new Path(fsHome, "/tmpNew/tmp"));
        assertPathExists(IgniteHadoopFileSystemAbstractSelfTest.fs, new Path(fsHome, "/tmpNew/tmp/file1"));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testRenameDirectory() throws Exception {
        Path fsHome = new Path(primaryFsUri);
        Path dir = new Path(fsHome, "/tmp/");
        Path newDir = new Path(fsHome, "/tmpNew/");
        FSDataOutputStream os = IgniteHadoopFileSystemAbstractSelfTest.fs.create(new Path(dir, "myFile"));
        os.close();
        assertTrue((((("Rename failed [dir=" + dir) + ", newDir=") + newDir) + ']'), IgniteHadoopFileSystemAbstractSelfTest.fs.rename(dir, newDir));
        assertPathDoesNotExist(IgniteHadoopFileSystemAbstractSelfTest.fs, dir);
        assertPathExists(IgniteHadoopFileSystemAbstractSelfTest.fs, newDir);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testListStatusIfPathIsNull() throws Exception {
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return IgniteHadoopFileSystemAbstractSelfTest.fs.listStatus(((Path) (null)));
            }
        }, NullPointerException.class, "Ouch! Argument cannot be null: f");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testListStatusIfPathDoesNotExist() throws Exception {
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return IgniteHadoopFileSystemAbstractSelfTest.fs.listStatus(new Path("/tmp/some/dir"));
            }
        }, FileNotFoundException.class, null);
    }

    /**
     * Test directory listing.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testListStatus() throws Exception {
        Path igfsHome = new Path(IgniteHadoopFileSystemAbstractSelfTest.PRIMARY_URI);
        // Test listing of an empty directory.
        Path dir = new Path(igfsHome, "dir");
        assert IgniteHadoopFileSystemAbstractSelfTest.fs.mkdirs(dir);
        FileStatus[] list = IgniteHadoopFileSystemAbstractSelfTest.fs.listStatus(dir);
        assert (list.length) == 0;
        // Test listing of a not empty directory.
        Path subDir = new Path(dir, "subDir");
        assert IgniteHadoopFileSystemAbstractSelfTest.fs.mkdirs(subDir);
        Path file = new Path(dir, "file");
        FSDataOutputStream fos = IgniteHadoopFileSystemAbstractSelfTest.fs.create(file);
        fos.close();
        list = IgniteHadoopFileSystemAbstractSelfTest.fs.listStatus(dir);
        assert (list.length) == 2;
        String listRes1 = list[0].getPath().getName();
        String listRes2 = list[1].getPath().getName();
        assert (("subDir".equals(listRes1)) && ("file".equals(listRes2))) || (("subDir".equals(listRes2)) && ("file".equals(listRes1)));
        // Test listing of a file.
        list = IgniteHadoopFileSystemAbstractSelfTest.fs.listStatus(file);
        assert (list.length) == 1;
        assert "file".equals(list[0].getPath().getName());
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSetWorkingDirectoryIfPathIsNull() throws Exception {
        IgniteHadoopFileSystemAbstractSelfTest.fs.setWorkingDirectory(null);
        Path file = new Path("file");
        FSDataOutputStream os = IgniteHadoopFileSystemAbstractSelfTest.fs.create(file);
        os.close();
        String path = IgniteHadoopFileSystemAbstractSelfTest.fs.getFileStatus(file).getPath().toString();
        assertTrue(path.endsWith((("/user/" + (getClientFsUser())) + "/file")));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSetWorkingDirectoryIfPathDoesNotExist() throws Exception {
        // Should not throw any exceptions.
        IgniteHadoopFileSystemAbstractSelfTest.fs.setWorkingDirectory(new Path("/someDir"));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSetWorkingDirectory() throws Exception {
        Path dir = new Path("/tmp/nested/dir");
        Path file = new Path("file");
        IgniteHadoopFileSystemAbstractSelfTest.fs.mkdirs(dir);
        IgniteHadoopFileSystemAbstractSelfTest.fs.setWorkingDirectory(dir);
        FSDataOutputStream os = IgniteHadoopFileSystemAbstractSelfTest.fs.create(file);
        os.close();
        String filePath = IgniteHadoopFileSystemAbstractSelfTest.fs.getFileStatus(new Path(dir, file)).getPath().toString();
        assertTrue(filePath.contains("/tmp/nested/dir/file"));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testGetWorkingDirectoryIfDefault() throws Exception {
        String path = IgniteHadoopFileSystemAbstractSelfTest.fs.getWorkingDirectory().toString();
        assertTrue(path.endsWith(("/user/" + (getClientFsUser()))));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testGetWorkingDirectory() throws Exception {
        Path dir = new Path("/tmp/some/dir");
        IgniteHadoopFileSystemAbstractSelfTest.fs.mkdirs(dir);
        IgniteHadoopFileSystemAbstractSelfTest.fs.setWorkingDirectory(dir);
        String path = IgniteHadoopFileSystemAbstractSelfTest.fs.getWorkingDirectory().toString();
        assertTrue(path.endsWith("/tmp/some/dir"));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testMkdirsIfPathIsNull() throws Exception {
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return IgniteHadoopFileSystemAbstractSelfTest.fs.mkdirs(null);
            }
        }, NullPointerException.class, "Ouch! Argument cannot be null: f");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testMkdirsIfPermissionIsNull() throws Exception {
        Path dir = new Path("/tmp");
        assertTrue(IgniteHadoopFileSystemAbstractSelfTest.fs.mkdirs(dir, null));
        assertEquals(FsPermission.getDefault(), IgniteHadoopFileSystemAbstractSelfTest.fs.getFileStatus(dir).getPermission());
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @SuppressWarnings("OctalInteger")
    @Test
    public void testMkdirs() throws Exception {
        Path fsHome = new Path(IgniteHadoopFileSystemAbstractSelfTest.PRIMARY_URI);
        final Path dir = new Path(fsHome, "/tmp/staging");
        final Path nestedDir = new Path(dir, "nested");
        final FsPermission dirPerm = FsPermission.createImmutable(((short) (448)));
        final FsPermission nestedDirPerm = FsPermission.createImmutable(((short) (111)));
        assertTrue(IgniteHadoopFileSystemAbstractSelfTest.fs.mkdirs(dir, dirPerm));
        assertTrue(IgniteHadoopFileSystemAbstractSelfTest.fs.mkdirs(nestedDir, nestedDirPerm));
        assertEquals(dirPerm, IgniteHadoopFileSystemAbstractSelfTest.fs.getFileStatus(dir).getPermission());
        assertEquals(nestedDirPerm, IgniteHadoopFileSystemAbstractSelfTest.fs.getFileStatus(nestedDir).getPermission());
        assertEquals(getClientFsUser(), IgniteHadoopFileSystemAbstractSelfTest.fs.getFileStatus(dir).getOwner());
        assertEquals(getClientFsUser(), IgniteHadoopFileSystemAbstractSelfTest.fs.getFileStatus(nestedDir).getOwner());
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testGetFileStatusIfPathIsNull() throws Exception {
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return IgniteHadoopFileSystemAbstractSelfTest.fs.getFileStatus(null);
            }
        }, NullPointerException.class, "Ouch! Argument cannot be null: f");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testGetFileStatusIfPathDoesNotExist() throws Exception {
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return IgniteHadoopFileSystemAbstractSelfTest.fs.getFileStatus(new Path("someDir"));
            }
        }, FileNotFoundException.class, "File not found: someDir");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testGetFileBlockLocationsIfFileStatusIsNull() throws Exception {
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                // Argument is checked by Hadoop.
                return IgniteHadoopFileSystemAbstractSelfTest.fs.getFileBlockLocations(((Path) (null)), 1, 2);
            }
        }, NullPointerException.class, null);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testGetFileBlockLocationsIfFileStatusReferenceNotExistingPath() throws Exception {
        Path path = new Path("someFile");
        IgniteHadoopFileSystemAbstractSelfTest.fs.create(path).close();
        final FileStatus status = IgniteHadoopFileSystemAbstractSelfTest.fs.getFileStatus(path);
        IgniteHadoopFileSystemAbstractSelfTest.fs.delete(path, true);
        BlockLocation[] locations = IgniteHadoopFileSystemAbstractSelfTest.fs.getFileBlockLocations(status, 1, 2);
        assertEquals(0, locations.length);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testGetFileBlockLocations() throws Exception {
        Path igfsHome = new Path(IgniteHadoopFileSystemAbstractSelfTest.PRIMARY_URI);
        Path file = new Path(igfsHome, "someFile");
        try (OutputStream out = new BufferedOutputStream(IgniteHadoopFileSystemAbstractSelfTest.fs.create(file, true, (1024 * 1024)))) {
            byte[] data = new byte[128 * 1024];
            for (int i = 0; i < 100; i++)
                out.write(data);

            out.flush();
        }
        try (FSDataInputStream in = IgniteHadoopFileSystemAbstractSelfTest.fs.open(file, (1024 * 1024))) {
            byte[] data = new byte[128 * 1024];
            int read;
            do {
                read = in.read(data);
            } while (read > 0 );
        }
        FileStatus status = IgniteHadoopFileSystemAbstractSelfTest.fs.getFileStatus(file);
        int grpLen = (128 * 512) * 1024;
        int grpCnt = ((int) ((((status.getLen()) + grpLen) - 1) / grpLen));
        BlockLocation[] locations = IgniteHadoopFileSystemAbstractSelfTest.fs.getFileBlockLocations(status, 0, status.getLen());
        assertEquals(grpCnt, locations.length);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testGetDefaultBlockSize() throws Exception {
        assertEquals((1L << 26), IgniteHadoopFileSystemAbstractSelfTest.fs.getDefaultBlockSize());
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testZeroReplicationFactor() throws Exception {
        // This test doesn't make sense for any mode except of PRIMARY.
        if ((mode) == (PRIMARY)) {
            Path igfsHome = new Path(IgniteHadoopFileSystemAbstractSelfTest.PRIMARY_URI);
            Path file = new Path(igfsHome, "someFile");
            try (FSDataOutputStream out = IgniteHadoopFileSystemAbstractSelfTest.fs.create(file, ((short) (0)))) {
                out.write(new byte[1024 * 1024]);
            }
            IgniteFileSystem igfs = grid(0).fileSystem("igfs");
            IgfsPath filePath = new IgfsPath("/someFile");
            IgfsFile fileInfo = igfs.info(filePath);
            awaitPartitionMapExchange();
            Collection<IgfsBlockLocation> locations = igfs.affinity(filePath, 0, fileInfo.length());
            assertEquals(1, locations.size());
            IgfsBlockLocation location = F.first(locations);
            assertEquals(1, location.nodeIds().size());
        }
    }

    /**
     * Ensure that when running in multithreaded mode only one create() operation succeed.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testMultithreadedCreate() throws Exception {
        Path dir = new Path(new Path(IgniteHadoopFileSystemAbstractSelfTest.PRIMARY_URI), "/dir");
        assert IgniteHadoopFileSystemAbstractSelfTest.fs.mkdirs(dir);
        final Path file = new Path(dir, "file");
        IgniteHadoopFileSystemAbstractSelfTest.fs.create(file).close();
        final AtomicInteger cnt = new AtomicInteger();
        final Collection<Integer> errs = new org.apache.ignite.internal.util.GridConcurrentHashSet(IgniteHadoopFileSystemAbstractSelfTest.THREAD_CNT, 1.0F, IgniteHadoopFileSystemAbstractSelfTest.THREAD_CNT);
        final AtomicBoolean err = new AtomicBoolean();
        multithreaded(new Runnable() {
            @Override
            public void run() {
                int idx = cnt.getAndIncrement();
                byte[] data = new byte[256];
                Arrays.fill(data, ((byte) (idx)));
                FSDataOutputStream os = null;
                try {
                    os = IgniteHadoopFileSystemAbstractSelfTest.fs.create(file, true);
                } catch (IOException ignore) {
                    errs.add(idx);
                }
                U.awaitQuiet(IgniteHadoopFileSystemAbstractSelfTest.barrier);
                try {
                    if (os != null)
                        os.write(data);

                } catch (IOException ignore) {
                    err.set(true);
                } finally {
                    U.closeQuiet(os);
                }
            }
        }, IgniteHadoopFileSystemAbstractSelfTest.THREAD_CNT);
        assert !(err.get());
        // Only one thread could obtain write lock on the file.
        assert (errs.size()) == ((IgniteHadoopFileSystemAbstractSelfTest.THREAD_CNT) - 1);
        int idx = -1;
        for (int i = 0; i < (IgniteHadoopFileSystemAbstractSelfTest.THREAD_CNT); i++) {
            if (!(errs.remove(i))) {
                idx = i;
                break;
            }
        }
        byte[] expData = new byte[256];
        Arrays.fill(expData, ((byte) (idx)));
        FSDataInputStream is = IgniteHadoopFileSystemAbstractSelfTest.fs.open(file);
        byte[] data = new byte[256];
        is.read(data);
        is.close();
        assert Arrays.equals(expData, data) : (("Expected=" + (Arrays.toString(expData))) + ", actual=") + (Arrays.toString(data));
    }

    /**
     * Ensure that when running in multithreaded mode only one append() operation succeed.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testMultithreadedAppend() throws Exception {
        Path dir = new Path(new Path(IgniteHadoopFileSystemAbstractSelfTest.PRIMARY_URI), "/dir");
        assert IgniteHadoopFileSystemAbstractSelfTest.fs.mkdirs(dir);
        final Path file = new Path(dir, "file");
        IgniteHadoopFileSystemAbstractSelfTest.fs.create(file).close();
        final AtomicInteger cnt = new AtomicInteger();
        final Collection<Integer> errs = new org.apache.ignite.internal.util.GridConcurrentHashSet(IgniteHadoopFileSystemAbstractSelfTest.THREAD_CNT, 1.0F, IgniteHadoopFileSystemAbstractSelfTest.THREAD_CNT);
        final AtomicBoolean err = new AtomicBoolean();
        multithreaded(new Runnable() {
            @Override
            public void run() {
                int idx = cnt.getAndIncrement();
                byte[] data = new byte[256];
                Arrays.fill(data, ((byte) (idx)));
                U.awaitQuiet(IgniteHadoopFileSystemAbstractSelfTest.barrier);
                FSDataOutputStream os = null;
                try {
                    os = IgniteHadoopFileSystemAbstractSelfTest.fs.append(file);
                } catch (IOException ignore) {
                    errs.add(idx);
                }
                U.awaitQuiet(IgniteHadoopFileSystemAbstractSelfTest.barrier);
                try {
                    if (os != null)
                        os.write(data);

                } catch (IOException ignore) {
                    err.set(true);
                } finally {
                    U.closeQuiet(os);
                }
            }
        }, IgniteHadoopFileSystemAbstractSelfTest.THREAD_CNT);
        assert !(err.get());
        // Only one thread could obtain write lock on the file.
        assert (errs.size()) == ((IgniteHadoopFileSystemAbstractSelfTest.THREAD_CNT) - 1);
        int idx = -1;
        for (int i = 0; i < (IgniteHadoopFileSystemAbstractSelfTest.THREAD_CNT); i++) {
            if (!(errs.remove(i))) {
                idx = i;
                break;
            }
        }
        byte[] expData = new byte[256];
        Arrays.fill(expData, ((byte) (idx)));
        FSDataInputStream is = IgniteHadoopFileSystemAbstractSelfTest.fs.open(file);
        byte[] data = new byte[256];
        is.read(data);
        is.close();
        assert Arrays.equals(expData, data);
    }

    /**
     * Test concurrent reads within the file.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testMultithreadedOpen() throws Exception {
        final byte[] dataChunk = new byte[256];
        for (int i = 0; i < (dataChunk.length); i++)
            dataChunk[i] = ((byte) (i));

        Path dir = new Path(new Path(IgniteHadoopFileSystemAbstractSelfTest.PRIMARY_URI), "/dir");
        assert IgniteHadoopFileSystemAbstractSelfTest.fs.mkdirs(dir);
        final Path file = new Path(dir, "file");
        FSDataOutputStream os = IgniteHadoopFileSystemAbstractSelfTest.fs.create(file);
        // Write 256 * 2048 = 512Kb of data.
        for (int i = 0; i < 2048; i++)
            os.write(dataChunk);

        os.close();
        final AtomicBoolean err = new AtomicBoolean();
        multithreaded(new Runnable() {
            @Override
            public void run() {
                FSDataInputStream is = null;
                try {
                    int pos = ThreadLocalRandom.current().nextInt(2048);
                    try {
                        is = IgniteHadoopFileSystemAbstractSelfTest.fs.open(file);
                    } finally {
                        U.awaitQuiet(IgniteHadoopFileSystemAbstractSelfTest.barrier);
                    }
                    is.seek((256 * pos));
                    byte[] buf = new byte[256];
                    for (int i = pos; i < 2048; i++) {
                        // First perform normal read.
                        int read = is.read(buf);
                        assert read == 256;
                        Arrays.equals(dataChunk, buf);
                    }
                    int res = is.read(buf);
                    assert res == (-1);
                } catch (IOException ignore) {
                    err.set(true);
                } finally {
                    U.closeQuiet(is);
                }
            }
        }, IgniteHadoopFileSystemAbstractSelfTest.THREAD_CNT);
        assert !(err.get());
    }

    /**
     * Test concurrent creation of multiple directories.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testMultithreadedMkdirs() throws Exception {
        final Path dir = new Path(new Path(IgniteHadoopFileSystemAbstractSelfTest.PRIMARY_URI), "/dir");
        assert IgniteHadoopFileSystemAbstractSelfTest.fs.mkdirs(dir);
        final int depth = 3;
        final int entryCnt = 5;
        final AtomicReference<IOException> err = new AtomicReference();
        multithreaded(new Runnable() {
            @Override
            public void run() {
                Deque<IgniteBiTuple<Integer, Path>> queue = new ArrayDeque<>();
                queue.add(F.t(0, dir));
                U.awaitQuiet(IgniteHadoopFileSystemAbstractSelfTest.barrier);
                while (!(queue.isEmpty())) {
                    IgniteBiTuple<Integer, Path> t = queue.pollFirst();
                    int curDepth = t.getKey();
                    Path curPath = t.getValue();
                    if (curDepth <= depth) {
                        int newDepth = curDepth + 1;
                        // Create directories.
                        for (int i = 0; i < entryCnt; i++) {
                            Path subDir = new Path(curPath, ((("dir-" + newDepth) + "-") + i));
                            try {
                                if (IgniteHadoopFileSystemAbstractSelfTest.fs.mkdirs(subDir))
                                    queue.addLast(F.t(newDepth, subDir));

                            } catch (IOException e) {
                                err.compareAndSet(null, e);
                            }
                        }
                    }
                } 
            }
        }, IgniteHadoopFileSystemAbstractSelfTest.THREAD_CNT);
        // Ensure there were no errors.
        assert (err.get()) == null : err.get();
        // Ensure correct folders structure.
        Deque<IgniteBiTuple<Integer, Path>> queue = new ArrayDeque<>();
        queue.add(F.t(0, dir));
        while (!(queue.isEmpty())) {
            IgniteBiTuple<Integer, Path> t = queue.pollFirst();
            int curDepth = t.getKey();
            Path curPath = t.getValue();
            if (curDepth <= depth) {
                int newDepth = curDepth + 1;
                // Create directories.
                for (int i = 0; i < entryCnt; i++) {
                    Path subDir = new Path(curPath, ((("dir-" + newDepth) + "-") + i));
                    assert IgniteHadoopFileSystemAbstractSelfTest.fs.exists(subDir) : "Expected directory doesn't exist: " + subDir;
                    queue.add(F.t(newDepth, subDir));
                }
            }
        } 
    }

    /**
     * Test concurrent deletion of the same directory with advanced structure.
     *
     * @throws Exception
     * 		If failed.
     */
    @SuppressWarnings("TooBroadScope")
    @Test
    public void testMultithreadedDelete() throws Exception {
        final Path dir = new Path(new Path(IgniteHadoopFileSystemAbstractSelfTest.PRIMARY_URI), "/dir");
        assert IgniteHadoopFileSystemAbstractSelfTest.fs.mkdirs(dir);
        int depth = 3;
        int entryCnt = 5;
        Deque<IgniteBiTuple<Integer, Path>> queue = new ArrayDeque<>();
        queue.add(F.t(0, dir));
        while (!(queue.isEmpty())) {
            IgniteBiTuple<Integer, Path> t = queue.pollFirst();
            int curDepth = t.getKey();
            Path curPath = t.getValue();
            if (curDepth < depth) {
                int newDepth = curDepth + 1;
                // Create directories.
                for (int i = 0; i < entryCnt; i++) {
                    Path subDir = new Path(curPath, ((("dir-" + newDepth) + "-") + i));
                    IgniteHadoopFileSystemAbstractSelfTest.fs.mkdirs(subDir);
                    queue.addLast(F.t(newDepth, subDir));
                }
            } else {
                // Create files.
                for (int i = 0; i < entryCnt; i++) {
                    Path file = new Path(curPath, ("file " + i));
                    IgniteHadoopFileSystemAbstractSelfTest.fs.create(file).close();
                }
            }
        } 
        final AtomicBoolean err = new AtomicBoolean();
        multithreaded(new Runnable() {
            @Override
            public void run() {
                try {
                    U.awaitQuiet(IgniteHadoopFileSystemAbstractSelfTest.barrier);
                    IgniteHadoopFileSystemAbstractSelfTest.fs.delete(dir, true);
                } catch (IOException ignore) {
                    err.set(true);
                }
            }
        }, IgniteHadoopFileSystemAbstractSelfTest.THREAD_CNT);
        // Ensure there were no errors.
        assert !(err.get());
        // Ensure the directory was actually deleted.
        assert GridTestUtils.waitForCondition(new GridAbsPredicate() {
            @Override
            public boolean apply() {
                try {
                    return !(IgniteHadoopFileSystemAbstractSelfTest.fs.exists(dir));
                } catch (IOException e) {
                    throw new AssertionError(e);
                }
            }
        }, 5000L);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testConsistency() throws Exception {
        // Default buffers values
        checkConsistency((-1), 1, (-1), (-1), 1, (-1));
        checkConsistency((-1), 10, (-1), (-1), 10, (-1));
        checkConsistency((-1), 100, (-1), (-1), 100, (-1));
        checkConsistency((-1), 1000, (-1), (-1), 1000, (-1));
        checkConsistency((-1), 10000, (-1), (-1), 10000, (-1));
        checkConsistency((-1), 100000, (-1), (-1), 100000, (-1));
        checkConsistency(((65 * 1024) + 13), 100000, (-1), (-1), 100000, (-1));
        checkConsistency((-1), 100000, (((2 * 4) * 1024) + 17), (-1), 100000, (-1));
        checkConsistency((-1), 100000, (-1), ((65 * 1024) + 13), 100000, (-1));
        checkConsistency((-1), 100000, (-1), (-1), 100000, (((2 * 4) * 1024) + 17));
        checkConsistency(((65 * 1024) + 13), 100000, (((2 * 4) * 1024) + 13), ((65 * 1024) + 149), 100000, (((2 * 4) * 1024) + 157));
    }

    /**
     * Verifies that client reconnects after connection to the server has been lost.
     *
     * @throws Exception
     * 		If error occurs.
     */
    @Test
    public void testClientReconnect() throws Exception {
        Path filePath = new Path(IgniteHadoopFileSystemAbstractSelfTest.PRIMARY_URI, "file1");
        final FSDataOutputStream s = IgniteHadoopFileSystemAbstractSelfTest.fs.create(filePath);// Open the stream before stopping IGFS.

        try {
            stopNodes();
            startNodes();// Start server again.

            // Check that client is again operational.
            assertTrue(IgniteHadoopFileSystemAbstractSelfTest.fs.mkdirs(new Path(IgniteHadoopFileSystemAbstractSelfTest.PRIMARY_URI, "dir1/dir2")));
            // However, the streams, opened before disconnect, should not be valid.
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Nullable
                @Override
                public Object call() throws Exception {
                    s.write("test".getBytes());
                    s.flush();// Flush data to the broken output stream.

                    return null;
                }
            }, IOException.class, null);
            assertFalse(IgniteHadoopFileSystemAbstractSelfTest.fs.exists(filePath));
        } finally {
            U.closeQuiet(s);// Safety.

        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testModeResolver() throws Exception {
        IgfsModeResolver mr = getModeResolver();
        assertEquals(mode, mr.resolveMode(ROOT));
    }

    /**
     * Verifies that client reconnects after connection to the server has been lost (multithreaded mode).
     *
     * @throws Exception
     * 		If error occurs.
     */
    @Test
    public void testClientReconnectMultithreaded() throws Exception {
        final ConcurrentLinkedQueue<FileSystem> q = new ConcurrentLinkedQueue<>();
        Configuration cfg = new Configuration();
        for (Map.Entry<String, String> entry : primaryFsCfg)
            cfg.set(entry.getKey(), entry.getValue());

        cfg.setBoolean("fs.igfs.impl.disable.cache", true);
        final int nClients = 1;
        // Initialize clients.
        for (int i = 0; i < nClients; i++)
            q.add(FileSystem.get(primaryFsUri, cfg));

        stopNodes();
        startNodes();// Start server again.

        GridTestUtils.runMultiThreaded(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                FileSystem fs = q.poll();
                try {
                    // Check that client is again operational.
                    assertTrue(fs.mkdirs(new Path(("/" + (Thread.currentThread().getName())))));
                    return true;
                } finally {
                    U.closeQuiet(fs);
                }
            }
        }, nClients, "test-client");
    }

    /**
     * Helper class to encapsulate source and destination folders.
     */
    @SuppressWarnings({ "PublicInnerClass", "PublicField" })
    public static final class Config {
        /**
         * Source file system.
         */
        public final FileSystem srcFs;

        /**
         * Source path to work with.
         */
        public final Path src;

        /**
         * Destination file system.
         */
        public final FileSystem destFs;

        /**
         * Destination path to work with.
         */
        public final Path dest;

        /**
         * Copying task configuration.
         *
         * @param srcFs
         * 		Source file system.
         * @param src
         * 		Source path.
         * @param destFs
         * 		Destination file system.
         * @param dest
         * 		Destination path.
         */
        public Config(FileSystem srcFs, Path src, FileSystem destFs, Path dest) {
            this.srcFs = srcFs;
            this.src = src;
            this.destFs = destFs;
            this.dest = dest;
        }
    }
}

