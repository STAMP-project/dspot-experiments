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
package org.apache.beam.sdk.io;


import MoveOptions.StandardMoveOptions.IGNORE_MISSING_FILES;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Sets;
import org.apache.beam.vendor.guava.v20_0.com.google.common.io.Files;
import org.apache.commons.lang3.SystemUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link FileSystems}.
 */
@RunWith(JUnit4.class)
public class FileSystemsTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private LocalFileSystem localFileSystem = new LocalFileSystem();

    @Test
    public void testGetLocalFileSystem() throws Exception {
        Assert.assertTrue(((FileSystems.getFileSystemInternal(toLocalResourceId("~/home/").getScheme())) instanceof LocalFileSystem));
        Assert.assertTrue(((FileSystems.getFileSystemInternal(toLocalResourceId("file://home").getScheme())) instanceof LocalFileSystem));
        Assert.assertTrue(((FileSystems.getFileSystemInternal(toLocalResourceId("FILE://home").getScheme())) instanceof LocalFileSystem));
        Assert.assertTrue(((FileSystems.getFileSystemInternal(toLocalResourceId("File://home").getScheme())) instanceof LocalFileSystem));
        if (SystemUtils.IS_OS_WINDOWS) {
            Assert.assertTrue(((FileSystems.getFileSystemInternal(toLocalResourceId("c:\\home\\").getScheme())) instanceof LocalFileSystem));
        }
    }

    @Test
    public void testVerifySchemesAreUnique() throws Exception {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Scheme: [file] has conflicting filesystems");
        FileSystems.verifySchemesAreUnique(PipelineOptionsFactory.create(), Sets.newHashSet(new LocalFileSystemRegistrar(), new LocalFileSystemRegistrar()));
    }

    @Test
    public void testDeleteIgnoreMissingFiles() throws Exception {
        Path existingPath = temporaryFolder.newFile().toPath();
        Path nonExistentPath = existingPath.resolveSibling("non-existent");
        createFileWithContent(existingPath, "content1");
        FileSystems.delete(/* isDirectory */
        toResourceIds(ImmutableList.of(existingPath, nonExistentPath), false));
    }

    @Test
    public void testCopyThrowsNoSuchFileException() throws Exception {
        Path existingPath = temporaryFolder.newFile().toPath();
        Path nonExistentPath = existingPath.resolveSibling("non-existent");
        Path destPath1 = existingPath.resolveSibling("dest1");
        Path destPath2 = nonExistentPath.resolveSibling("dest2");
        createFileWithContent(existingPath, "content1");
        thrown.expect(NoSuchFileException.class);
        FileSystems.copy(/* isDirectory */
        toResourceIds(ImmutableList.of(existingPath, nonExistentPath), false), /* isDirectory */
        toResourceIds(ImmutableList.of(destPath1, destPath2), false));
    }

    @Test
    public void testCopyIgnoreMissingFiles() throws Exception {
        Path srcPath1 = temporaryFolder.newFile().toPath();
        Path nonExistentPath = srcPath1.resolveSibling("non-existent");
        Path srcPath3 = temporaryFolder.newFile().toPath();
        Path destPath1 = srcPath1.resolveSibling("dest1");
        Path destPath2 = nonExistentPath.resolveSibling("dest2");
        Path destPath3 = srcPath1.resolveSibling("dest3");
        createFileWithContent(srcPath1, "content1");
        createFileWithContent(srcPath3, "content3");
        FileSystems.copy(/* isDirectory */
        toResourceIds(ImmutableList.of(srcPath1, nonExistentPath, srcPath3), false), /* isDirectory */
        toResourceIds(ImmutableList.of(destPath1, destPath2, destPath3), false), IGNORE_MISSING_FILES);
        Assert.assertTrue(srcPath1.toFile().exists());
        Assert.assertTrue(srcPath3.toFile().exists());
        Assert.assertThat(Files.readLines(srcPath1.toFile(), StandardCharsets.UTF_8), Matchers.containsInAnyOrder("content1"));
        Assert.assertFalse(destPath2.toFile().exists());
        Assert.assertThat(Files.readLines(srcPath3.toFile(), StandardCharsets.UTF_8), Matchers.containsInAnyOrder("content3"));
    }

    @Test
    public void testRenameThrowsNoSuchFileException() throws Exception {
        Path existingPath = temporaryFolder.newFile().toPath();
        Path nonExistentPath = existingPath.resolveSibling("non-existent");
        Path destPath1 = existingPath.resolveSibling("dest1");
        Path destPath2 = nonExistentPath.resolveSibling("dest2");
        createFileWithContent(existingPath, "content1");
        thrown.expect(NoSuchFileException.class);
        FileSystems.rename(/* isDirectory */
        toResourceIds(ImmutableList.of(existingPath, nonExistentPath), false), /* isDirectory */
        toResourceIds(ImmutableList.of(destPath1, destPath2), false));
    }

    @Test
    public void testRenameIgnoreMissingFiles() throws Exception {
        Path srcPath1 = temporaryFolder.newFile().toPath();
        Path nonExistentPath = srcPath1.resolveSibling("non-existent");
        Path srcPath3 = temporaryFolder.newFile().toPath();
        Path destPath1 = srcPath1.resolveSibling("dest1");
        Path destPath2 = nonExistentPath.resolveSibling("dest2");
        Path destPath3 = srcPath1.resolveSibling("dest3");
        createFileWithContent(srcPath1, "content1");
        createFileWithContent(srcPath3, "content3");
        FileSystems.rename(/* isDirectory */
        toResourceIds(ImmutableList.of(srcPath1, nonExistentPath, srcPath3), false), /* isDirectory */
        toResourceIds(ImmutableList.of(destPath1, destPath2, destPath3), false), IGNORE_MISSING_FILES);
        Assert.assertFalse(srcPath1.toFile().exists());
        Assert.assertFalse(srcPath3.toFile().exists());
        Assert.assertThat(Files.readLines(destPath1.toFile(), StandardCharsets.UTF_8), Matchers.containsInAnyOrder("content1"));
        Assert.assertFalse(destPath2.toFile().exists());
        Assert.assertThat(Files.readLines(destPath3.toFile(), StandardCharsets.UTF_8), Matchers.containsInAnyOrder("content3"));
    }

    @Test
    public void testValidMatchNewResourceForLocalFileSystem() {
        Assert.assertEquals("file", FileSystems.matchNewResource("/tmp/f1", false).getScheme());
        Assert.assertEquals("file", FileSystems.matchNewResource("tmp/f1", false).getScheme());
        Assert.assertEquals("file", FileSystems.matchNewResource("c:\\tmp\\f1", false).getScheme());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidSchemaMatchNewResource() {
        Assert.assertEquals("file", FileSystems.matchNewResource("invalidschema://tmp/f1", false));
        Assert.assertEquals("file", FileSystems.matchNewResource("c:/tmp/f1", false));
    }
}

