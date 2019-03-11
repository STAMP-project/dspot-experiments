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
package org.apache.hadoop.tools.mapred;


import CopyMapper.Counter.BYTESCOPIED;
import CopyMapper.Counter.COPY;
import DistCpConstants.CONF_LABEL_PRESERVE_STATUS;
import DistCpConstants.CONF_LABEL_TARGET_FINAL_PATH;
import DistCpConstants.CONF_LABEL_TARGET_WORK_PATH;
import DistCpOptionSwitch.APPEND;
import DistCpOptionSwitch.COPY_BUFFER_SIZE;
import DistCpOptionSwitch.PRESERVE_STATUS;
import DistCpOptionSwitch.VERBOSE_LOG;
import DistCpOptions.FileAttribute;
import DistCpOptions.FileAttribute.ACL;
import DistCpOptions.FileAttribute.TIMES;
import DistCpOptions.FileAttribute.XATTR;
import Mapper.Context;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.metrics2.MetricsRecordBuilder;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.tools.CopyListingFileStatus;
import org.apache.hadoop.tools.DistCpOptions;
import org.apache.hadoop.tools.StubContext;
import org.apache.hadoop.tools.util.DistCpUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestCopyMapper {
    private static final Logger LOG = LoggerFactory.getLogger(TestCopyMapper.class);

    private static List<Path> pathList = new ArrayList<Path>();

    private static int nFiles = 0;

    private static final int DEFAULT_FILE_SIZE = 1024;

    private static final long NON_DEFAULT_BLOCK_SIZE = 4096;

    private static MiniDFSCluster cluster;

    private static final String SOURCE_PATH = "/tmp/source";

    private static final String TARGET_PATH = "/tmp/target";

    @Test
    public void testCopyWithDifferentChecksumType() throws Exception {
        testCopy(true);
    }

    @Test(timeout = 40000)
    public void testRun() throws Exception {
        testCopy(false);
    }

    @Test
    public void testCopyWithAppend() throws Exception {
        final FileSystem fs = TestCopyMapper.cluster.getFileSystem();
        // do the first distcp
        testCopy(false);
        // start appending data to source
        TestCopyMapper.appendSourceData();
        // do the distcp again with -update and -append option
        CopyMapper copyMapper = new CopyMapper();
        Configuration conf = TestCopyMapper.getConfiguration();
        // set the buffer size to 1/10th the size of the file.
        conf.setInt(COPY_BUFFER_SIZE.getConfigLabel(), ((TestCopyMapper.DEFAULT_FILE_SIZE) / 10));
        StubContext stubContext = new StubContext(conf, null, 0);
        Context context = getContext();
        // Enable append
        context.getConfiguration().setBoolean(APPEND.getConfigLabel(), true);
        copyMapper.setup(context);
        int numFiles = 0;
        MetricsRecordBuilder rb = getMetrics(TestCopyMapper.cluster.getDataNodes().get(0).getMetrics().name());
        String readCounter = "ReadsFromLocalClient";
        long readsFromClient = getLongCounter(readCounter, rb);
        for (Path path : TestCopyMapper.pathList) {
            if (fs.getFileStatus(path).isFile()) {
                numFiles++;
            }
            copyMapper.map(new Text(DistCpUtils.getRelativePath(new Path(TestCopyMapper.SOURCE_PATH), path)), new CopyListingFileStatus(TestCopyMapper.cluster.getFileSystem().getFileStatus(path)), context);
        }
        verifyCopy(fs, false, true);
        // verify that we only copied new appended data
        Assert.assertEquals((((TestCopyMapper.nFiles) * (TestCopyMapper.DEFAULT_FILE_SIZE)) * 2), stubContext.getReporter().getCounter(BYTESCOPIED).getValue());
        Assert.assertEquals(numFiles, stubContext.getReporter().getCounter(COPY).getValue());
        rb = getMetrics(TestCopyMapper.cluster.getDataNodes().get(0).getMetrics().name());
        /* added as part of HADOOP-15292 to ensure that multiple readBlock()
        operations are not performed to read a block from a single Datanode.
        assert assumes that there is only one block per file, and that the number
        of files appended to in appendSourceData() above is captured by the
        variable numFiles.
         */
        assertCounter(readCounter, (readsFromClient + numFiles), rb);
    }

    @Test(timeout = 40000)
    public void testMakeDirFailure() {
        try {
            TestCopyMapper.deleteState();
            TestCopyMapper.createSourceData();
            FileSystem fs = TestCopyMapper.cluster.getFileSystem();
            CopyMapper copyMapper = new CopyMapper();
            StubContext stubContext = new StubContext(TestCopyMapper.getConfiguration(), null, 0);
            Context context = getContext();
            Configuration configuration = context.getConfiguration();
            String workPath = new Path("webhdfs://localhost:1234/*/*/*/?/").makeQualified(fs.getUri(), fs.getWorkingDirectory()).toString();
            configuration.set(CONF_LABEL_TARGET_WORK_PATH, workPath);
            copyMapper.setup(context);
            copyMapper.map(new Text(DistCpUtils.getRelativePath(new Path(TestCopyMapper.SOURCE_PATH), TestCopyMapper.pathList.get(0))), new CopyListingFileStatus(fs.getFileStatus(TestCopyMapper.pathList.get(0))), context);
            Assert.assertTrue("There should have been an exception.", false);
        } catch (Exception ignore) {
        }
    }

    @Test(timeout = 40000)
    public void testIgnoreFailures() {
        doTestIgnoreFailures(true);
        doTestIgnoreFailures(false);
        doTestIgnoreFailuresDoubleWrapped(true);
        doTestIgnoreFailuresDoubleWrapped(false);
    }

    @Test(timeout = 40000)
    public void testDirToFile() {
        try {
            TestCopyMapper.deleteState();
            TestCopyMapper.createSourceData();
            FileSystem fs = TestCopyMapper.cluster.getFileSystem();
            CopyMapper copyMapper = new CopyMapper();
            StubContext stubContext = new StubContext(TestCopyMapper.getConfiguration(), null, 0);
            Context context = getContext();
            TestCopyMapper.mkdirs(((TestCopyMapper.SOURCE_PATH) + "/src/file"));
            TestCopyMapper.touchFile(((TestCopyMapper.TARGET_PATH) + "/src/file"));
            try {
                copyMapper.setup(context);
                copyMapper.map(new Text("/src/file"), new CopyListingFileStatus(fs.getFileStatus(new Path(((TestCopyMapper.SOURCE_PATH) + "/src/file")))), context);
            } catch (IOException e) {
                Assert.assertTrue(e.getMessage().startsWith("Can't replace"));
            }
        } catch (Exception e) {
            TestCopyMapper.LOG.error("Exception encountered ", e);
            Assert.fail(("Test failed: " + (e.getMessage())));
        }
    }

    @Test(timeout = 40000)
    public void testPreserve() {
        try {
            TestCopyMapper.deleteState();
            TestCopyMapper.createSourceData();
            UserGroupInformation tmpUser = UserGroupInformation.createRemoteUser("guest");
            final CopyMapper copyMapper = new CopyMapper();
            final Context context = tmpUser.doAs(new java.security.PrivilegedAction<Context>() {
                @Override
                public Context run() {
                    try {
                        StubContext stubContext = new StubContext(getConfiguration(), null, 0);
                        return stubContext.getContext();
                    } catch ( e) {
                        TestCopyMapper.LOG.error("Exception encountered ", e);
                        throw new <e>RuntimeException();
                    }
                }
            });
            EnumSet<DistCpOptions.FileAttribute> preserveStatus = EnumSet.allOf(FileAttribute.class);
            preserveStatus.remove(ACL);
            preserveStatus.remove(XATTR);
            context.getConfiguration().set(CONF_LABEL_PRESERVE_STATUS, DistCpUtils.packAttributes(preserveStatus));
            TestCopyMapper.touchFile(((TestCopyMapper.SOURCE_PATH) + "/src/file"));
            TestCopyMapper.mkdirs(TestCopyMapper.TARGET_PATH);
            TestCopyMapper.cluster.getFileSystem().setPermission(new Path(TestCopyMapper.TARGET_PATH), new FsPermission(((short) (511))));
            final FileSystem tmpFS = tmpUser.doAs(new java.security.PrivilegedAction<FileSystem>() {
                @Override
                public FileSystem run() {
                    try {
                        return FileSystem.get(TestCopyMapper.cluster.getConfiguration(0));
                    } catch (IOException e) {
                        TestCopyMapper.LOG.error("Exception encountered ", e);
                        Assert.fail(("Test failed: " + (e.getMessage())));
                        throw new RuntimeException("Test ought to fail here");
                    }
                }
            });
            tmpUser.doAs(new java.security.PrivilegedAction<Integer>() {
                @Override
                public Integer run() {
                    try {
                        copyMapper.setup(context);
                        copyMapper.map(new Text("/src/file"), new CopyListingFileStatus(tmpFS.getFileStatus(new Path(((TestCopyMapper.SOURCE_PATH) + "/src/file")))), context);
                        Assert.fail("Expected copy to fail");
                    } catch (AccessControlException e) {
                        Assert.assertTrue(("Got exception: " + (e.getMessage())), true);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            TestCopyMapper.LOG.error("Exception encountered ", e);
            Assert.fail(("Test failed: " + (e.getMessage())));
        }
    }

    @Test(timeout = 40000)
    public void testCopyReadableFiles() {
        try {
            TestCopyMapper.deleteState();
            TestCopyMapper.createSourceData();
            UserGroupInformation tmpUser = UserGroupInformation.createRemoteUser("guest");
            final CopyMapper copyMapper = new CopyMapper();
            final Context context = tmpUser.doAs(new java.security.PrivilegedAction<Context>() {
                @Override
                public Context run() {
                    try {
                        StubContext stubContext = new StubContext(getConfiguration(), null, 0);
                        return stubContext.getContext();
                    } catch ( e) {
                        TestCopyMapper.LOG.error("Exception encountered ", e);
                        throw new <e>RuntimeException();
                    }
                }
            });
            TestCopyMapper.touchFile(((TestCopyMapper.SOURCE_PATH) + "/src/file"));
            TestCopyMapper.mkdirs(TestCopyMapper.TARGET_PATH);
            TestCopyMapper.cluster.getFileSystem().setPermission(new Path(((TestCopyMapper.SOURCE_PATH) + "/src/file")), new FsPermission(FsAction.READ, FsAction.READ, FsAction.READ));
            TestCopyMapper.cluster.getFileSystem().setPermission(new Path(TestCopyMapper.TARGET_PATH), new FsPermission(((short) (511))));
            final FileSystem tmpFS = tmpUser.doAs(new java.security.PrivilegedAction<FileSystem>() {
                @Override
                public FileSystem run() {
                    try {
                        return FileSystem.get(TestCopyMapper.cluster.getConfiguration(0));
                    } catch (IOException e) {
                        TestCopyMapper.LOG.error("Exception encountered ", e);
                        Assert.fail(("Test failed: " + (e.getMessage())));
                        throw new RuntimeException("Test ought to fail here");
                    }
                }
            });
            tmpUser.doAs(new java.security.PrivilegedAction<Integer>() {
                @Override
                public Integer run() {
                    try {
                        copyMapper.setup(context);
                        copyMapper.map(new Text("/src/file"), new CopyListingFileStatus(tmpFS.getFileStatus(new Path(((TestCopyMapper.SOURCE_PATH) + "/src/file")))), context);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            TestCopyMapper.LOG.error("Exception encountered ", e);
            Assert.fail(("Test failed: " + (e.getMessage())));
        }
    }

    @Test(timeout = 40000)
    public void testSkipCopyNoPerms() {
        try {
            TestCopyMapper.deleteState();
            TestCopyMapper.createSourceData();
            UserGroupInformation tmpUser = UserGroupInformation.createRemoteUser("guest");
            final CopyMapper copyMapper = new CopyMapper();
            final StubContext stubContext = tmpUser.doAs(new java.security.PrivilegedAction<StubContext>() {
                @Override
                public StubContext run() {
                    try {
                        return new StubContext(TestCopyMapper.getConfiguration(), null, 0);
                    } catch (Exception e) {
                        TestCopyMapper.LOG.error("Exception encountered ", e);
                        throw new RuntimeException(e);
                    }
                }
            });
            final Context context = getContext();
            EnumSet<DistCpOptions.FileAttribute> preserveStatus = EnumSet.allOf(FileAttribute.class);
            preserveStatus.remove(ACL);
            preserveStatus.remove(XATTR);
            preserveStatus.remove(TIMES);
            context.getConfiguration().set(CONF_LABEL_PRESERVE_STATUS, DistCpUtils.packAttributes(preserveStatus));
            TestCopyMapper.touchFile(((TestCopyMapper.SOURCE_PATH) + "/src/file"));
            TestCopyMapper.touchFile(((TestCopyMapper.TARGET_PATH) + "/src/file"));
            TestCopyMapper.cluster.getFileSystem().setPermission(new Path(((TestCopyMapper.SOURCE_PATH) + "/src/file")), new FsPermission(FsAction.READ, FsAction.READ, FsAction.READ));
            TestCopyMapper.cluster.getFileSystem().setPermission(new Path(((TestCopyMapper.TARGET_PATH) + "/src/file")), new FsPermission(FsAction.READ, FsAction.READ, FsAction.READ));
            final FileSystem tmpFS = tmpUser.doAs(new java.security.PrivilegedAction<FileSystem>() {
                @Override
                public FileSystem run() {
                    try {
                        return FileSystem.get(TestCopyMapper.cluster.getConfiguration(0));
                    } catch (IOException e) {
                        TestCopyMapper.LOG.error("Exception encountered ", e);
                        Assert.fail(("Test failed: " + (e.getMessage())));
                        throw new RuntimeException("Test ought to fail here");
                    }
                }
            });
            tmpUser.doAs(new java.security.PrivilegedAction<Integer>() {
                @Override
                public Integer run() {
                    try {
                        copyMapper.setup(context);
                        copyMapper.map(new Text("/src/file"), new CopyListingFileStatus(tmpFS.getFileStatus(new Path(((TestCopyMapper.SOURCE_PATH) + "/src/file")))), context);
                        Assert.assertEquals(stubContext.getWriter().values().size(), 1);
                        Assert.assertTrue(stubContext.getWriter().values().get(0).toString().startsWith("SKIP"));
                        Assert.assertTrue(stubContext.getWriter().values().get(0).toString().contains(((TestCopyMapper.SOURCE_PATH) + "/src/file")));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            TestCopyMapper.LOG.error("Exception encountered ", e);
            Assert.fail(("Test failed: " + (e.getMessage())));
        }
    }

    @Test(timeout = 40000)
    public void testFailCopyWithAccessControlException() {
        try {
            TestCopyMapper.deleteState();
            TestCopyMapper.createSourceData();
            UserGroupInformation tmpUser = UserGroupInformation.createRemoteUser("guest");
            final CopyMapper copyMapper = new CopyMapper();
            final StubContext stubContext = tmpUser.doAs(new java.security.PrivilegedAction<StubContext>() {
                @Override
                public StubContext run() {
                    try {
                        return new StubContext(TestCopyMapper.getConfiguration(), null, 0);
                    } catch (Exception e) {
                        TestCopyMapper.LOG.error("Exception encountered ", e);
                        throw new RuntimeException(e);
                    }
                }
            });
            EnumSet<DistCpOptions.FileAttribute> preserveStatus = EnumSet.allOf(FileAttribute.class);
            preserveStatus.remove(ACL);
            preserveStatus.remove(XATTR);
            final Context context = getContext();
            context.getConfiguration().set(CONF_LABEL_PRESERVE_STATUS, DistCpUtils.packAttributes(preserveStatus));
            TestCopyMapper.touchFile(((TestCopyMapper.SOURCE_PATH) + "/src/file"));
            OutputStream out = TestCopyMapper.cluster.getFileSystem().create(new Path(((TestCopyMapper.TARGET_PATH) + "/src/file")));
            out.write("hello world".getBytes());
            out.close();
            TestCopyMapper.cluster.getFileSystem().setPermission(new Path(((TestCopyMapper.SOURCE_PATH) + "/src/file")), new FsPermission(FsAction.READ, FsAction.READ, FsAction.READ));
            TestCopyMapper.cluster.getFileSystem().setPermission(new Path(((TestCopyMapper.TARGET_PATH) + "/src/file")), new FsPermission(FsAction.READ, FsAction.READ, FsAction.READ));
            final FileSystem tmpFS = tmpUser.doAs(new java.security.PrivilegedAction<FileSystem>() {
                @Override
                public FileSystem run() {
                    try {
                        return FileSystem.get(TestCopyMapper.cluster.getConfiguration(0));
                    } catch (IOException e) {
                        TestCopyMapper.LOG.error("Exception encountered ", e);
                        Assert.fail(("Test failed: " + (e.getMessage())));
                        throw new RuntimeException("Test ought to fail here");
                    }
                }
            });
            tmpUser.doAs(new java.security.PrivilegedAction<Integer>() {
                @Override
                public Integer run() {
                    try {
                        copyMapper.setup(context);
                        copyMapper.map(new Text("/src/file"), new CopyListingFileStatus(tmpFS.getFileStatus(new Path(((TestCopyMapper.SOURCE_PATH) + "/src/file")))), context);
                        Assert.fail("Didn't expect the file to be copied");
                    } catch (AccessControlException ignore) {
                    } catch (Exception e) {
                        // We want to make sure the underlying cause of the exception is
                        // due to permissions error. The exception we're interested in is
                        // wrapped twice - once in RetriableCommand and again in CopyMapper
                        // itself.
                        if ((((e.getCause()) == null) || ((e.getCause().getCause()) == null)) || (!((e.getCause().getCause()) instanceof AccessControlException))) {
                            throw new RuntimeException(e);
                        }
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            TestCopyMapper.LOG.error("Exception encountered ", e);
            Assert.fail(("Test failed: " + (e.getMessage())));
        }
    }

    @Test(timeout = 40000)
    public void testFileToDir() {
        try {
            TestCopyMapper.deleteState();
            TestCopyMapper.createSourceData();
            FileSystem fs = TestCopyMapper.cluster.getFileSystem();
            CopyMapper copyMapper = new CopyMapper();
            StubContext stubContext = new StubContext(TestCopyMapper.getConfiguration(), null, 0);
            Context context = getContext();
            TestCopyMapper.touchFile(((TestCopyMapper.SOURCE_PATH) + "/src/file"));
            TestCopyMapper.mkdirs(((TestCopyMapper.TARGET_PATH) + "/src/file"));
            try {
                copyMapper.setup(context);
                copyMapper.map(new Text("/src/file"), new CopyListingFileStatus(fs.getFileStatus(new Path(((TestCopyMapper.SOURCE_PATH) + "/src/file")))), context);
            } catch (IOException e) {
                Assert.assertTrue(e.getMessage().startsWith("Can't replace"));
            }
        } catch (Exception e) {
            TestCopyMapper.LOG.error("Exception encountered ", e);
            Assert.fail(("Test failed: " + (e.getMessage())));
        }
    }

    @Test(timeout = 40000)
    public void testPreserveBlockSizeAndReplication() {
        testPreserveBlockSizeAndReplicationImpl(true);
        testPreserveBlockSizeAndReplicationImpl(false);
    }

    @Test(timeout = 40000)
    public void testCopyWithDifferentBlockSizes() throws Exception {
        try {
            TestCopyMapper.deleteState();
            TestCopyMapper.createSourceDataWithDifferentBlockSize();
            FileSystem fs = TestCopyMapper.cluster.getFileSystem();
            CopyMapper copyMapper = new CopyMapper();
            StubContext stubContext = new StubContext(TestCopyMapper.getConfiguration(), null, 0);
            Context context = getContext();
            Configuration configuration = context.getConfiguration();
            EnumSet<DistCpOptions.FileAttribute> fileAttributes = EnumSet.noneOf(FileAttribute.class);
            configuration.set(PRESERVE_STATUS.getConfigLabel(), DistCpUtils.packAttributes(fileAttributes));
            copyMapper.setup(context);
            for (Path path : TestCopyMapper.pathList) {
                final FileStatus fileStatus = fs.getFileStatus(path);
                copyMapper.map(new Text(DistCpUtils.getRelativePath(new Path(TestCopyMapper.SOURCE_PATH), path)), new CopyListingFileStatus(fileStatus), context);
            }
            if (expectDifferentBlockSizesMultipleBlocksToSucceed()) {
                verifyCopy(fs, false, false);
            } else {
                Assert.fail("Copy should have failed because of block-size difference.");
            }
        } catch (Exception exception) {
            if (expectDifferentBlockSizesMultipleBlocksToSucceed()) {
                throw exception;
            } else {
                // Check that the exception suggests the use of -pb/-skipcrccheck.
                // This could be refactored to use LambdaTestUtils if we add support
                // for listing multiple different independent substrings to expect
                // in the exception message and add support for LambdaTestUtils to
                // inspect the transitive cause and/or suppressed exceptions as well.
                Throwable cause = exception.getCause().getCause();
                GenericTestUtils.assertExceptionContains("-pb", cause);
                GenericTestUtils.assertExceptionContains("-skipcrccheck", cause);
            }
        }
    }

    @Test(timeout = 40000)
    public void testCopyWithDifferentBytesPerCrc() throws Exception {
        try {
            TestCopyMapper.deleteState();
            TestCopyMapper.createSourceDataWithDifferentBytesPerCrc();
            FileSystem fs = TestCopyMapper.cluster.getFileSystem();
            CopyMapper copyMapper = new CopyMapper();
            StubContext stubContext = new StubContext(TestCopyMapper.getConfiguration(), null, 0);
            Context context = getContext();
            Configuration configuration = context.getConfiguration();
            EnumSet<DistCpOptions.FileAttribute> fileAttributes = EnumSet.noneOf(FileAttribute.class);
            configuration.set(PRESERVE_STATUS.getConfigLabel(), DistCpUtils.packAttributes(fileAttributes));
            copyMapper.setup(context);
            for (Path path : TestCopyMapper.pathList) {
                final FileStatus fileStatus = fs.getFileStatus(path);
                copyMapper.map(new Text(DistCpUtils.getRelativePath(new Path(TestCopyMapper.SOURCE_PATH), path)), new CopyListingFileStatus(fileStatus), context);
            }
            if (expectDifferentBytesPerCrcToSucceed()) {
                verifyCopy(fs, false, false);
            } else {
                Assert.fail("Copy should have failed because of bytes-per-crc difference.");
            }
        } catch (Exception exception) {
            if (expectDifferentBytesPerCrcToSucceed()) {
                throw exception;
            } else {
                // This could be refactored to use LambdaTestUtils if we add support
                // for LambdaTestUtils to inspect the transitive cause and/or
                // suppressed exceptions as well.
                Throwable cause = exception.getCause().getCause();
                GenericTestUtils.assertExceptionContains("mismatch", cause);
            }
        }
    }

    /**
     * If a single file is being copied to a location where the file (of the same
     * name) already exists, then the file shouldn't be skipped.
     */
    @Test(timeout = 40000)
    public void testSingleFileCopy() {
        try {
            TestCopyMapper.deleteState();
            TestCopyMapper.touchFile(((TestCopyMapper.SOURCE_PATH) + "/1"));
            Path sourceFilePath = TestCopyMapper.pathList.get(0);
            Path targetFilePath = new Path(sourceFilePath.toString().replaceAll(TestCopyMapper.SOURCE_PATH, TestCopyMapper.TARGET_PATH));
            TestCopyMapper.touchFile(targetFilePath.toString());
            FileSystem fs = TestCopyMapper.cluster.getFileSystem();
            CopyMapper copyMapper = new CopyMapper();
            StubContext stubContext = new StubContext(TestCopyMapper.getConfiguration(), null, 0);
            Context context = getContext();
            context.getConfiguration().set(CONF_LABEL_TARGET_FINAL_PATH, targetFilePath.getParent().toString());// Parent directory.

            copyMapper.setup(context);
            final CopyListingFileStatus sourceFileStatus = new CopyListingFileStatus(fs.getFileStatus(sourceFilePath));
            long before = fs.getFileStatus(targetFilePath).getModificationTime();
            copyMapper.map(new Text(DistCpUtils.getRelativePath(new Path(TestCopyMapper.SOURCE_PATH), sourceFilePath)), sourceFileStatus, context);
            long after = fs.getFileStatus(targetFilePath).getModificationTime();
            Assert.assertTrue("File should have been skipped", (before == after));
            context.getConfiguration().set(CONF_LABEL_TARGET_FINAL_PATH, targetFilePath.toString());// Specify the file path.

            copyMapper.setup(context);
            before = fs.getFileStatus(targetFilePath).getModificationTime();
            try {
                Thread.sleep(2);
            } catch (Throwable ignore) {
            }
            copyMapper.map(new Text(DistCpUtils.getRelativePath(new Path(TestCopyMapper.SOURCE_PATH), sourceFilePath)), sourceFileStatus, context);
            after = fs.getFileStatus(targetFilePath).getModificationTime();
            Assert.assertTrue("File should have been overwritten.", (before < after));
        } catch (Exception exception) {
            Assert.fail(("Unexpected exception: " + (exception.getMessage())));
            exception.printStackTrace();
        }
    }

    @Test(timeout = 40000)
    public void testPreserveUserGroup() {
        testPreserveUserGroupImpl(true);
        testPreserveUserGroupImpl(false);
    }

    @Test
    public void testVerboseLogging() throws Exception {
        TestCopyMapper.deleteState();
        TestCopyMapper.createSourceData();
        FileSystem fs = TestCopyMapper.cluster.getFileSystem();
        CopyMapper copyMapper = new CopyMapper();
        StubContext stubContext = new StubContext(TestCopyMapper.getConfiguration(), null, 0);
        Context context = getContext();
        copyMapper.setup(context);
        int numFiles = 0;
        for (Path path : TestCopyMapper.pathList) {
            if (fs.getFileStatus(path).isFile()) {
                numFiles++;
            }
            copyMapper.map(new Text(DistCpUtils.getRelativePath(new Path(TestCopyMapper.SOURCE_PATH), path)), new CopyListingFileStatus(fs.getFileStatus(path)), context);
        }
        // Check that the maps worked.
        Assert.assertEquals(numFiles, stubContext.getReporter().getCounter(COPY).getValue());
        testCopyingExistingFiles(fs, copyMapper, context);
        // verify the verbose log
        // we shouldn't print verbose log since this option is disabled
        for (Text value : stubContext.getWriter().values()) {
            Assert.assertTrue((!(value.toString().startsWith("FILE_COPIED:"))));
            Assert.assertTrue((!(value.toString().startsWith("FILE_SKIPPED:"))));
        }
        // test with verbose logging
        TestCopyMapper.deleteState();
        TestCopyMapper.createSourceData();
        stubContext = new StubContext(TestCopyMapper.getConfiguration(), null, 0);
        context = getContext();
        copyMapper.setup(context);
        // enables verbose logging
        context.getConfiguration().setBoolean(VERBOSE_LOG.getConfigLabel(), true);
        copyMapper.setup(context);
        for (Path path : TestCopyMapper.pathList) {
            copyMapper.map(new Text(DistCpUtils.getRelativePath(new Path(TestCopyMapper.SOURCE_PATH), path)), new CopyListingFileStatus(fs.getFileStatus(path)), context);
        }
        Assert.assertEquals(numFiles, stubContext.getReporter().getCounter(COPY).getValue());
        // verify the verbose log of COPY log
        int numFileCopied = 0;
        for (Text value : stubContext.getWriter().values()) {
            if (value.toString().startsWith("FILE_COPIED:")) {
                numFileCopied++;
            }
        }
        Assert.assertEquals(numFiles, numFileCopied);
        // verify the verbose log of SKIP log
        int numFileSkipped = 0;
        testCopyingExistingFiles(fs, copyMapper, context);
        for (Text value : stubContext.getWriter().values()) {
            if (value.toString().startsWith("FILE_SKIPPED:")) {
                numFileSkipped++;
            }
        }
        Assert.assertEquals(numFiles, numFileSkipped);
    }
}

