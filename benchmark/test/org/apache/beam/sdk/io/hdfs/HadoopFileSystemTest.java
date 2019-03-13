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
package org.apache.beam.sdk.io.hdfs;


import HadoopFileSystem.LOG_CREATE_DIRECTORY;
import HadoopFileSystem.LOG_DELETING_EXISTING_FILE;
import Status.NOT_FOUND;
import Status.OK;
import java.io.FileNotFoundException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import org.apache.beam.sdk.io.fs.MatchResult;
import org.apache.beam.sdk.io.fs.MatchResult.Metadata;
import org.apache.beam.sdk.testing.ExpectedLogs;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterables;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link HadoopFileSystem}.
 */
@RunWith(JUnit4.class)
public class HadoopFileSystemTest {
    @Rule
    public TestPipeline p = TestPipeline.create();

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public final ExpectedLogs expectedLogs = ExpectedLogs.none(HadoopFileSystem.class);

    private MiniDFSCluster hdfsCluster;

    private URI hdfsClusterBaseUri;

    private HadoopFileSystem fileSystem;

    @Test
    public void testCreateAndReadFile() throws Exception {
        byte[] bytes = "testData".getBytes(StandardCharsets.UTF_8);
        create("testFile", bytes);
        Assert.assertArrayEquals(bytes, read("testFile", 0));
    }

    @Test
    public void testCreateAndReadFileWithShift() throws Exception {
        byte[] bytes = "testData".getBytes(StandardCharsets.UTF_8);
        create("testFile", bytes);
        int bytesToSkip = 3;
        byte[] expected = Arrays.copyOfRange(bytes, bytesToSkip, bytes.length);
        byte[] actual = read("testFile", bytesToSkip);
        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void testCreateAndReadFileWithShiftToEnd() throws Exception {
        byte[] bytes = "testData".getBytes(StandardCharsets.UTF_8);
        create("testFile", bytes);
        int bytesToSkip = bytes.length;
        byte[] expected = Arrays.copyOfRange(bytes, bytesToSkip, bytes.length);
        Assert.assertArrayEquals(expected, read("testFile", bytesToSkip));
    }

    @Test
    public void testCopy() throws Exception {
        create("testFileA", "testDataA".getBytes(StandardCharsets.UTF_8));
        create("testFileB", "testDataB".getBytes(StandardCharsets.UTF_8));
        fileSystem.copy(ImmutableList.of(testPath("testFileA"), testPath("testFileB")), ImmutableList.of(testPath("copyTestFileA"), testPath("copyTestFileB")));
        Assert.assertArrayEquals("testDataA".getBytes(StandardCharsets.UTF_8), read("testFileA", 0));
        Assert.assertArrayEquals("testDataB".getBytes(StandardCharsets.UTF_8), read("testFileB", 0));
        Assert.assertArrayEquals("testDataA".getBytes(StandardCharsets.UTF_8), read("copyTestFileA", 0));
        Assert.assertArrayEquals("testDataB".getBytes(StandardCharsets.UTF_8), read("copyTestFileB", 0));
    }

    @Test(expected = FileNotFoundException.class)
    public void testCopySourceMissing() throws Exception {
        fileSystem.copy(ImmutableList.of(testPath("missingFile")), ImmutableList.of(testPath("copyTestFile")));
    }

    @Test
    public void testDelete() throws Exception {
        create("testFileA", "testDataA".getBytes(StandardCharsets.UTF_8));
        create("testFileB", "testDataB".getBytes(StandardCharsets.UTF_8));
        create("testFileC", "testDataC".getBytes(StandardCharsets.UTF_8));
        // ensure files exist
        Assert.assertArrayEquals("testDataA".getBytes(StandardCharsets.UTF_8), read("testFileA", 0));
        Assert.assertArrayEquals("testDataB".getBytes(StandardCharsets.UTF_8), read("testFileB", 0));
        Assert.assertArrayEquals("testDataC".getBytes(StandardCharsets.UTF_8), read("testFileC", 0));
        fileSystem.delete(ImmutableList.of(testPath("testFileA"), testPath("testFileC")));
        List<MatchResult> results = fileSystem.match(ImmutableList.of(testPath("testFile*").toString()));
        Assert.assertThat(results, Matchers.contains(MatchResult.create(OK, ImmutableList.of(Metadata.builder().setResourceId(testPath("testFileB")).setIsReadSeekEfficient(true).setSizeBytes("testDataB".getBytes(StandardCharsets.UTF_8).length).setLastModifiedMillis(lastModified("testFileB")).build()))));
    }

    /**
     * Verifies that an attempt to delete a non existing file is silently ignored.
     */
    @Test
    public void testDeleteNonExisting() throws Exception {
        fileSystem.delete(ImmutableList.of(testPath("MissingFile")));
    }

    @Test
    public void testMatch() throws Exception {
        create("testFileAA", "testDataAA".getBytes(StandardCharsets.UTF_8));
        create("testFileA", "testDataA".getBytes(StandardCharsets.UTF_8));
        create("testFileB", "testDataB".getBytes(StandardCharsets.UTF_8));
        // ensure files exist
        Assert.assertArrayEquals("testDataAA".getBytes(StandardCharsets.UTF_8), read("testFileAA", 0));
        Assert.assertArrayEquals("testDataA".getBytes(StandardCharsets.UTF_8), read("testFileA", 0));
        Assert.assertArrayEquals("testDataB".getBytes(StandardCharsets.UTF_8), read("testFileB", 0));
        List<MatchResult> results = fileSystem.match(ImmutableList.of(testPath("testFileA*").toString()));
        Assert.assertEquals(OK, Iterables.getOnlyElement(results).status());
        Assert.assertThat(Iterables.getOnlyElement(results).metadata(), Matchers.containsInAnyOrder(Metadata.builder().setResourceId(testPath("testFileAA")).setIsReadSeekEfficient(true).setSizeBytes("testDataAA".getBytes(StandardCharsets.UTF_8).length).setLastModifiedMillis(lastModified("testFileAA")).build(), Metadata.builder().setResourceId(testPath("testFileA")).setIsReadSeekEfficient(true).setSizeBytes("testDataA".getBytes(StandardCharsets.UTF_8).length).setLastModifiedMillis(lastModified("testFileA")).build()));
    }

    @Test
    public void testMatchForNonExistentFile() throws Exception {
        create("testFileAA", "testDataAA".getBytes(StandardCharsets.UTF_8));
        create("testFileBB", "testDataBB".getBytes(StandardCharsets.UTF_8));
        // ensure files exist
        Assert.assertArrayEquals("testDataAA".getBytes(StandardCharsets.UTF_8), read("testFileAA", 0));
        Assert.assertArrayEquals("testDataBB".getBytes(StandardCharsets.UTF_8), read("testFileBB", 0));
        List<MatchResult> matchResults = fileSystem.match(ImmutableList.of(testPath("testFileAA").toString(), testPath("testFileA").toString(), testPath("testFileBB").toString()));
        Assert.assertThat(matchResults, Matchers.hasSize(3));
        final List<MatchResult> expected = ImmutableList.of(MatchResult.create(OK, ImmutableList.of(Metadata.builder().setResourceId(testPath("testFileAA")).setIsReadSeekEfficient(true).setSizeBytes("testDataAA".getBytes(StandardCharsets.UTF_8).length).setLastModifiedMillis(lastModified("testFileAA")).build())), MatchResult.create(NOT_FOUND, ImmutableList.of()), MatchResult.create(OK, ImmutableList.of(Metadata.builder().setResourceId(testPath("testFileBB")).setIsReadSeekEfficient(true).setSizeBytes("testDataBB".getBytes(StandardCharsets.UTF_8).length).setLastModifiedMillis(lastModified("testFileBB")).build())));
        Assert.assertThat(matchResults, Matchers.equalTo(expected));
    }

    @Test
    public void testRename() throws Exception {
        create("testFileA", "testDataA".getBytes(StandardCharsets.UTF_8));
        create("testFileB", "testDataB".getBytes(StandardCharsets.UTF_8));
        // ensure files exist
        Assert.assertArrayEquals("testDataA".getBytes(StandardCharsets.UTF_8), read("testFileA", 0));
        Assert.assertArrayEquals("testDataB".getBytes(StandardCharsets.UTF_8), read("testFileB", 0));
        fileSystem.rename(ImmutableList.of(testPath("testFileA"), testPath("testFileB")), ImmutableList.of(testPath("renameFileA"), testPath("renameFileB")));
        List<MatchResult> results = fileSystem.match(ImmutableList.of(testPath("*").toString()));
        Assert.assertEquals(OK, Iterables.getOnlyElement(results).status());
        Assert.assertThat(Iterables.getOnlyElement(results).metadata(), Matchers.containsInAnyOrder(Metadata.builder().setResourceId(testPath("renameFileA")).setIsReadSeekEfficient(true).setSizeBytes("testDataA".getBytes(StandardCharsets.UTF_8).length).setLastModifiedMillis(lastModified("renameFileA")).build(), Metadata.builder().setResourceId(testPath("renameFileB")).setIsReadSeekEfficient(true).setSizeBytes("testDataB".getBytes(StandardCharsets.UTF_8).length).setLastModifiedMillis(lastModified("renameFileB")).build()));
        // ensure files exist
        Assert.assertArrayEquals("testDataA".getBytes(StandardCharsets.UTF_8), read("renameFileA", 0));
        Assert.assertArrayEquals("testDataB".getBytes(StandardCharsets.UTF_8), read("renameFileB", 0));
    }

    /**
     * Ensure that missing parent directories are created when required.
     */
    @Test
    public void testRenameMissingTargetDir() throws Exception {
        create("pathA/testFileA", "testDataA".getBytes(StandardCharsets.UTF_8));
        create("pathA/testFileB", "testDataB".getBytes(StandardCharsets.UTF_8));
        // ensure files exist
        Assert.assertArrayEquals("testDataA".getBytes(StandardCharsets.UTF_8), read("pathA/testFileA", 0));
        Assert.assertArrayEquals("testDataB".getBytes(StandardCharsets.UTF_8), read("pathA/testFileB", 0));
        // move to a directory that does not exist
        fileSystem.rename(ImmutableList.of(testPath("pathA/testFileA"), testPath("pathA/testFileB")), ImmutableList.of(testPath("pathB/testFileA"), testPath("pathB/pathC/pathD/testFileB")));
        // ensure the directories were created and the files can be read
        expectedLogs.verifyDebug(String.format(LOG_CREATE_DIRECTORY, "/pathB"));
        expectedLogs.verifyDebug(String.format(LOG_CREATE_DIRECTORY, "/pathB/pathC/pathD"));
        Assert.assertArrayEquals("testDataA".getBytes(StandardCharsets.UTF_8), read("pathB/testFileA", 0));
        Assert.assertArrayEquals("testDataB".getBytes(StandardCharsets.UTF_8), read("pathB/pathC/pathD/testFileB", 0));
    }

    @Test(expected = FileNotFoundException.class)
    public void testRenameMissingSource() throws Exception {
        fileSystem.rename(ImmutableList.of(testPath("missingFile")), ImmutableList.of(testPath("testFileA")));
    }

    /**
     * Test that rename overwrites existing files.
     */
    @Test
    public void testRenameExistingDestination() throws Exception {
        create("testFileA", "testDataA".getBytes(StandardCharsets.UTF_8));
        create("testFileB", "testDataB".getBytes(StandardCharsets.UTF_8));
        // ensure files exist
        Assert.assertArrayEquals("testDataA".getBytes(StandardCharsets.UTF_8), read("testFileA", 0));
        Assert.assertArrayEquals("testDataB".getBytes(StandardCharsets.UTF_8), read("testFileB", 0));
        fileSystem.rename(ImmutableList.of(testPath("testFileA")), ImmutableList.of(testPath("testFileB")));
        expectedLogs.verifyDebug(String.format(LOG_DELETING_EXISTING_FILE, "/testFileB"));
        Assert.assertArrayEquals("testDataA".getBytes(StandardCharsets.UTF_8), read("testFileB", 0));
    }

    /**
     * Test that rename throws predictably when source doesn't exist and destination does.
     */
    @Test(expected = FileNotFoundException.class)
    public void testRenameRetryScenario() throws Exception {
        testRename();
        // retry the knowing that sources are already moved to destination
        fileSystem.rename(ImmutableList.of(testPath("testFileA"), testPath("testFileB")), ImmutableList.of(testPath("renameFileA"), testPath("renameFileB")));
    }

    @Test
    public void testMatchNewResource() {
        // match file spec
        Assert.assertEquals(testPath("file"), fileSystem.matchNewResource(testPath("file").toString(), false));
        // match dir spec missing '/'
        Assert.assertEquals(testPath("dir/"), fileSystem.matchNewResource(testPath("dir").toString(), true));
        // match dir spec with '/'
        Assert.assertEquals(testPath("dir/"), fileSystem.matchNewResource(testPath("dir/").toString(), true));
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Expected file path but received directory path");
        fileSystem.matchNewResource(testPath("dir/").toString(), false);
    }
}

