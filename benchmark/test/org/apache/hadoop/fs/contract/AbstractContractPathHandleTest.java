/**
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.hadoop.fs.contract;


import HandleOpt.Data;
import HandleOpt.Location;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.InvalidPathHandleException;
import org.apache.hadoop.fs.Options.HandleOpt;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathHandle;
import org.apache.hadoop.test.LambdaTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Test {@link PathHandle} operations and semantics.
 *
 * @see ContractOptions#SUPPORTS_FILE_REFERENCE
 * @see ContractOptions#SUPPORTS_CONTENT_CHECK
 * @see org.apache.hadoop.fs.FileSystem#getPathHandle(FileStatus, HandleOpt...)
 * @see org.apache.hadoop.fs.FileSystem#open(PathHandle)
 * @see org.apache.hadoop.fs.FileSystem#open(PathHandle, int)
 */
@RunWith(Parameterized.class)
public abstract class AbstractContractPathHandleTest extends AbstractFSContractTestBase {
    private final HandleOpt[] opts;

    private final boolean serialized;

    private static final byte[] B1 = ContractTestUtils.dataset(AbstractFSContractTestBase.TEST_FILE_LEN, 43, 255);

    private static final byte[] B2 = ContractTestUtils.dataset(AbstractFSContractTestBase.TEST_FILE_LEN, 44, 255);

    /**
     * Create an instance of the test from {@link #params()}.
     *
     * @param testname
     * 		Name of the set of options under test
     * @param opts
     * 		Set of {@link HandleOpt} params under test.
     * @param serialized
     * 		Serialize the handle before using it.
     */
    public AbstractContractPathHandleTest(String testname, HandleOpt[] opts, boolean serialized) {
        this.opts = opts;
        this.serialized = serialized;
    }

    @Test
    public void testIdent() throws IOException {
        describe("verify simple open, no changes");
        FileStatus stat = testFile(AbstractContractPathHandleTest.B1);
        PathHandle fd = getHandleOrSkip(stat);
        ContractTestUtils.verifyFileContents(getFileSystem(), stat.getPath(), AbstractContractPathHandleTest.B1);
        try (FSDataInputStream in = getFileSystem().open(fd)) {
            ContractTestUtils.verifyRead(in, AbstractContractPathHandleTest.B1, 0, AbstractFSContractTestBase.TEST_FILE_LEN);
        }
    }

    @Test
    public void testChanged() throws IOException {
        describe("verify open(PathHandle, changed(*))");
        assumeSupportsContentCheck();
        HandleOpt.Data data = HandleOpt.getOpt(Data.class, opts).orElseThrow(IllegalArgumentException::new);
        FileStatus stat = testFile(AbstractContractPathHandleTest.B1);
        try {
            // Temporary workaround while RawLocalFS supports only second precision
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
        // modify the file by appending data
        ContractTestUtils.appendFile(getFileSystem(), stat.getPath(), AbstractContractPathHandleTest.B2);
        byte[] b12 = Arrays.copyOf(AbstractContractPathHandleTest.B1, ((AbstractContractPathHandleTest.B1.length) + (AbstractContractPathHandleTest.B2.length)));
        System.arraycopy(AbstractContractPathHandleTest.B2, 0, b12, AbstractContractPathHandleTest.B1.length, AbstractContractPathHandleTest.B2.length);
        // verify fd entity contains contents of file1 + appended bytes
        ContractTestUtils.verifyFileContents(getFileSystem(), stat.getPath(), b12);
        // get the handle *after* the file has been modified
        PathHandle fd = getHandleOrSkip(stat);
        try (FSDataInputStream in = getFileSystem().open(fd)) {
            Assert.assertTrue("Failed to detect content change", data.allowChange());
            ContractTestUtils.verifyRead(in, b12, 0, b12.length);
        } catch (InvalidPathHandleException e) {
            Assert.assertFalse("Failed to allow content change", data.allowChange());
        }
    }

    @Test
    public void testMoved() throws IOException {
        describe("verify open(PathHandle, moved(*))");
        assumeSupportsFileReference();
        HandleOpt.Location loc = HandleOpt.getOpt(Location.class, opts).orElseThrow(IllegalArgumentException::new);
        FileStatus stat = testFile(AbstractContractPathHandleTest.B1);
        // rename the file after obtaining FileStatus
        ContractTestUtils.rename(getFileSystem(), stat.getPath(), path(((stat.getPath()) + "2")));
        // obtain handle to entity from #getFileStatus call
        PathHandle fd = getHandleOrSkip(stat);
        try (FSDataInputStream in = getFileSystem().open(fd)) {
            Assert.assertTrue("Failed to detect location change", loc.allowChange());
            ContractTestUtils.verifyRead(in, AbstractContractPathHandleTest.B1, 0, AbstractContractPathHandleTest.B1.length);
        } catch (InvalidPathHandleException e) {
            Assert.assertFalse("Failed to allow location change", loc.allowChange());
        }
    }

    @Test
    public void testChangedAndMoved() throws IOException {
        describe("verify open(PathHandle, changed(*), moved(*))");
        assumeSupportsFileReference();
        assumeSupportsContentCheck();
        HandleOpt.Data data = HandleOpt.getOpt(Data.class, opts).orElseThrow(IllegalArgumentException::new);
        HandleOpt.Location loc = HandleOpt.getOpt(Location.class, opts).orElseThrow(IllegalArgumentException::new);
        FileStatus stat = testFile(AbstractContractPathHandleTest.B1);
        Path dst = path(((stat.getPath()) + "2"));
        ContractTestUtils.rename(getFileSystem(), stat.getPath(), dst);
        ContractTestUtils.appendFile(getFileSystem(), dst, AbstractContractPathHandleTest.B2);
        PathHandle fd = getHandleOrSkip(stat);
        byte[] b12 = Arrays.copyOf(AbstractContractPathHandleTest.B1, ((AbstractContractPathHandleTest.B1.length) + (AbstractContractPathHandleTest.B2.length)));
        System.arraycopy(AbstractContractPathHandleTest.B2, 0, b12, AbstractContractPathHandleTest.B1.length, AbstractContractPathHandleTest.B2.length);
        try (FSDataInputStream in = getFileSystem().open(fd)) {
            Assert.assertTrue("Failed to detect location change", loc.allowChange());
            Assert.assertTrue("Failed to detect content change", data.allowChange());
            ContractTestUtils.verifyRead(in, b12, 0, b12.length);
        } catch (InvalidPathHandleException e) {
            if (data.allowChange()) {
                Assert.assertFalse("Failed to allow location change", loc.allowChange());
            }
            if (loc.allowChange()) {
                Assert.assertFalse("Failed to allow content change", data.allowChange());
            }
        }
    }

    @Test
    public void testOpenFileApplyRead() throws Throwable {
        describe("use the apply sequence to read a whole file");
        CompletableFuture<Long> readAllBytes = getFileSystem().openFile(getHandleOrSkip(testFile(AbstractContractPathHandleTest.B1))).build().thenApply(ContractTestUtils::readStream);
        Assert.assertEquals("Wrong number of bytes read value", AbstractFSContractTestBase.TEST_FILE_LEN, ((long) (readAllBytes.get())));
    }

    @Test
    public void testOpenFileDelete() throws Throwable {
        describe("use the apply sequence to read a whole file");
        FileStatus testFile = testFile(AbstractContractPathHandleTest.B1);
        PathHandle handle = getHandleOrSkip(testFile);
        // delete that file
        FileSystem fs = getFileSystem();
        fs.delete(testFile.getPath(), false);
        // now construct the builder.
        // even if the open happens in the build operation,
        // the failure must not surface until later.
        CompletableFuture<FSDataInputStream> builder = fs.openFile(handle).opt("fs.test.something", true).build();
        IOException ioe = LambdaTestUtils.interceptFuture(IOException.class, "", builder);
        if ((!(ioe instanceof FileNotFoundException)) && (!(ioe instanceof InvalidPathHandleException))) {
            // support both FileNotFoundException
            // and InvalidPathHandleException as different implementations
            // support either -and with non-atomic open sequences, possibly
            // both
            throw ioe;
        }
    }

    @Test
    public void testOpenFileLazyFail() throws Throwable {
        describe("openFile fails on a misssng file in the get() and not before");
        FileStatus stat = testFile(AbstractContractPathHandleTest.B1);
        CompletableFuture<Long> readAllBytes = getFileSystem().openFile(getHandleOrSkip(stat)).build().thenApply(ContractTestUtils::readStream);
        Assert.assertEquals("Wrong number of bytes read value", AbstractFSContractTestBase.TEST_FILE_LEN, ((long) (readAllBytes.get())));
    }
}

