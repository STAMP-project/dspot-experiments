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


import MatchResult.Status.NOT_FOUND;
import StandardResolveOptions.RESOLVE_DIRECTORY;
import StandardResolveOptions.RESOLVE_FILE;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import org.apache.beam.sdk.io.fs.MatchResult;
import org.apache.beam.sdk.testing.RestoreSystemProperties;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Lists;
import org.apache.beam.vendor.guava.v20_0.com.google.common.io.Files;
import org.apache.beam.vendor.guava.v20_0.com.google.common.io.LineReader;
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
 * Tests for {@link LocalFileSystem}.
 */
@RunWith(JUnit4.class)
public class LocalFileSystemTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public RestoreSystemProperties restoreSystemProperties = new RestoreSystemProperties();

    private LocalFileSystem localFileSystem = new LocalFileSystem();

    @Test
    public void testCreateWithExistingFile() throws Exception {
        File existingFile = temporaryFolder.newFile();
        testCreate(existingFile.toPath());
    }

    @Test
    public void testCreateWithinExistingDirectory() throws Exception {
        testCreate(temporaryFolder.getRoot().toPath().resolve("file.txt"));
    }

    @Test
    public void testCreateWithNonExistentSubDirectory() throws Exception {
        testCreate(temporaryFolder.getRoot().toPath().resolve("non-existent-dir").resolve("file.txt"));
    }

    @Test
    public void testReadWithExistingFile() throws Exception {
        String expected = "my test string";
        File existingFile = temporaryFolder.newFile();
        Files.write(expected, existingFile, StandardCharsets.UTF_8);
        String data;
        try (Reader reader = Channels.newReader(localFileSystem.open(/* isDirectory */
        LocalResourceId.fromPath(existingFile.toPath(), false)), StandardCharsets.UTF_8.name())) {
            data = new LineReader(reader).readLine();
        }
        Assert.assertEquals(expected, data);
    }

    @Test
    public void testReadNonExistentFile() throws Exception {
        thrown.expect(FileNotFoundException.class);
        localFileSystem.open(/* isDirectory */
        LocalResourceId.fromPath(temporaryFolder.getRoot().toPath().resolve("non-existent-file.txt"), false)).close();
    }

    @Test
    public void testCopyWithExistingSrcFile() throws Exception {
        Path srcPath1 = temporaryFolder.newFile().toPath();
        Path srcPath2 = temporaryFolder.newFile().toPath();
        Path destPath1 = temporaryFolder.getRoot().toPath().resolve("nonexistentdir").resolve("dest1");
        Path destPath2 = srcPath2.resolveSibling("dest2");
        createFileWithContent(srcPath1, "content1");
        createFileWithContent(srcPath2, "content2");
        localFileSystem.copy(/* isDirectory */
        toLocalResourceIds(ImmutableList.of(srcPath1, srcPath2), false), /* isDirectory */
        toLocalResourceIds(ImmutableList.of(destPath1, destPath2), false));
        assertContents(ImmutableList.of(destPath1, destPath2), ImmutableList.of("content1", "content2"));
    }

    @Test
    public void testMoveWithExistingSrcFile() throws Exception {
        Path srcPath1 = temporaryFolder.newFile().toPath();
        Path srcPath2 = temporaryFolder.newFile().toPath();
        Path destPath1 = temporaryFolder.getRoot().toPath().resolve("nonexistentdir").resolve("dest1");
        Path destPath2 = srcPath2.resolveSibling("dest2");
        createFileWithContent(srcPath1, "content1");
        createFileWithContent(srcPath2, "content2");
        localFileSystem.rename(/* isDirectory */
        toLocalResourceIds(ImmutableList.of(srcPath1, srcPath2), false), /* isDirectory */
        toLocalResourceIds(ImmutableList.of(destPath1, destPath2), false));
        assertContents(ImmutableList.of(destPath1, destPath2), ImmutableList.of("content1", "content2"));
        Assert.assertFalse((srcPath1 + "exists"), srcPath1.toFile().exists());
        Assert.assertFalse((srcPath2 + "exists"), srcPath2.toFile().exists());
    }

    @Test
    public void testDelete() throws Exception {
        File f1 = temporaryFolder.newFile("file1");
        File f2 = temporaryFolder.newFile("file2");
        File f3 = temporaryFolder.newFile("other-file");
        localFileSystem.delete(/* isDirectory */
        toLocalResourceIds(Lists.newArrayList(f1.toPath(), f2.toPath()), false));
        Assert.assertFalse(f1.exists());
        Assert.assertFalse(f2.exists());
        Assert.assertTrue(f3.exists());
    }

    @Test
    public void testMatchExact() throws Exception {
        List<String> expected = ImmutableList.of(temporaryFolder.newFile("a").toString());
        temporaryFolder.newFile("aa");
        temporaryFolder.newFile("ab");
        List<MatchResult> matchResults = localFileSystem.match(ImmutableList.of(temporaryFolder.getRoot().toPath().resolve("a").toString()));
        Assert.assertThat(toFilenames(matchResults), Matchers.containsInAnyOrder(expected.toArray(new String[expected.size()])));
    }

    @Test
    public void testMatchPatternNone() throws Exception {
        temporaryFolder.newFile("a");
        temporaryFolder.newFile("aa");
        temporaryFolder.newFile("ab");
        List<MatchResult> matchResults = matchGlobWithPathPrefix(temporaryFolder.getRoot().toPath().resolve("b"), "*");
        Assert.assertEquals(1, matchResults.size());
        Assert.assertEquals(NOT_FOUND, matchResults.get(0).status());
    }

    @Test
    public void testMatchForNonExistentFile() throws Exception {
        temporaryFolder.newFile("aa");
        List<MatchResult> matchResults = localFileSystem.match(ImmutableList.of(temporaryFolder.getRoot().toPath().resolve("a").toString()));
        Assert.assertEquals(1, matchResults.size());
        Assert.assertEquals(NOT_FOUND, matchResults.get(0).status());
    }

    @Test
    public void testMatchMultipleWithFileExtension() throws Exception {
        List<String> expected = ImmutableList.of(temporaryFolder.newFile("a.txt").toString(), temporaryFolder.newFile("aa.txt").toString(), temporaryFolder.newFile("ab.txt").toString());
        temporaryFolder.newFile("a.avro");
        temporaryFolder.newFile("ab.avro");
        List<MatchResult> matchResults = matchGlobWithPathPrefix(temporaryFolder.getRoot().toPath().resolve("a"), "*.txt");
        Assert.assertThat(toFilenames(matchResults), Matchers.containsInAnyOrder(expected.toArray(new String[expected.size()])));
    }

    @Test
    public void testMatchInDirectory() throws Exception {
        List<String> expected = ImmutableList.of(temporaryFolder.newFile("a").toString());
        temporaryFolder.newFile("aa");
        temporaryFolder.newFile("ab");
        String expectedFile = expected.get(0);
        int slashIndex = expectedFile.lastIndexOf('/');
        if (SystemUtils.IS_OS_WINDOWS) {
            slashIndex = expectedFile.lastIndexOf('\\');
        }
        String directory = expectedFile.substring(0, slashIndex);
        String relative = expectedFile.substring((slashIndex + 1));
        System.setProperty("user.dir", directory);
        List<MatchResult> results = localFileSystem.match(ImmutableList.of(relative));
        Assert.assertThat(toFilenames(results), Matchers.containsInAnyOrder(expected.toArray(new String[expected.size()])));
    }

    @Test
    public void testMatchWithFileSlashPrefix() throws Exception {
        List<String> expected = ImmutableList.of(temporaryFolder.newFile("a").toString());
        temporaryFolder.newFile("aa");
        temporaryFolder.newFile("ab");
        String file = "file:/" + (temporaryFolder.getRoot().toPath().resolve("a").toString());
        List<MatchResult> results = localFileSystem.match(ImmutableList.of(file));
        Assert.assertThat(toFilenames(results), Matchers.containsInAnyOrder(expected.toArray(new String[expected.size()])));
    }

    @Test
    public void testMatchWithFileThreeSlashesPrefix() throws Exception {
        List<String> expected = ImmutableList.of(temporaryFolder.newFile("a").toString());
        temporaryFolder.newFile("aa");
        temporaryFolder.newFile("ab");
        String file = "file:///" + (temporaryFolder.getRoot().toPath().resolve("a").toString());
        List<MatchResult> results = localFileSystem.match(ImmutableList.of(file));
        Assert.assertThat(toFilenames(results), Matchers.containsInAnyOrder(expected.toArray(new String[expected.size()])));
    }

    @Test
    public void testMatchMultipleWithoutSubdirectoryExpansion() throws Exception {
        File unmatchedSubDir = temporaryFolder.newFolder("aaa");
        File unmatchedSubDirFile = File.createTempFile("sub-dir-file", "", unmatchedSubDir);
        unmatchedSubDirFile.deleteOnExit();
        List<String> expected = ImmutableList.of(temporaryFolder.newFile("a").toString(), temporaryFolder.newFile("aa").toString(), temporaryFolder.newFile("ab").toString());
        temporaryFolder.newFile("ba");
        temporaryFolder.newFile("bb");
        List<MatchResult> matchResults = matchGlobWithPathPrefix(temporaryFolder.getRoot().toPath().resolve("a"), "*");
        Assert.assertThat(toFilenames(matchResults), Matchers.containsInAnyOrder(expected.toArray(new String[expected.size()])));
    }

    @Test
    public void testMatchMultipleWithSubdirectoryExpansion() throws Exception {
        File matchedSubDir = temporaryFolder.newFolder("a");
        File matchedSubDirFile = File.createTempFile("sub-dir-file", "", matchedSubDir);
        matchedSubDirFile.deleteOnExit();
        File unmatchedSubDir = temporaryFolder.newFolder("b");
        File unmatchedSubDirFile = File.createTempFile("sub-dir-file", "", unmatchedSubDir);
        unmatchedSubDirFile.deleteOnExit();
        List<String> expected = ImmutableList.of(matchedSubDirFile.toString(), temporaryFolder.newFile("aa").toString(), temporaryFolder.newFile("ab").toString());
        temporaryFolder.newFile("ba");
        temporaryFolder.newFile("bb");
        List<MatchResult> matchResults = matchGlobWithPathPrefix(temporaryFolder.getRoot().toPath().resolve("a"), "**");
        Assert.assertThat(toFilenames(matchResults), Matchers.hasItems(expected.toArray(new String[expected.size()])));
    }

    @Test
    public void testMatchWithDirectoryFiltersOutDirectory() throws Exception {
        List<String> expected = ImmutableList.of(temporaryFolder.newFile("a").toString());
        temporaryFolder.newFolder("a_dir_that_should_not_be_matched");
        List<MatchResult> matchResults = matchGlobWithPathPrefix(temporaryFolder.getRoot().toPath().resolve("a"), "*");
        Assert.assertThat(toFilenames(matchResults), Matchers.containsInAnyOrder(expected.toArray(new String[expected.size()])));
    }

    @Test
    public void testMatchWithoutParentDirectory() throws Exception {
        Path pattern = /* isDirectory */
        LocalResourceId.fromPath(temporaryFolder.getRoot().toPath(), true).resolve("non_existing_dir", RESOLVE_DIRECTORY).resolve("*", RESOLVE_FILE).getPath();
        Assert.assertTrue(toFilenames(localFileSystem.match(ImmutableList.of(pattern.toString()))).isEmpty());
    }

    @Test
    public void testMatchNewResource() throws Exception {
        LocalResourceId fileResource = /* isDirectory */
        localFileSystem.matchNewResource("/some/test/resource/path", false);
        LocalResourceId dirResource = /* isDirectory */
        localFileSystem.matchNewResource("/some/test/resource/path", true);
        Assert.assertNotEquals(fileResource, dirResource);
        Assert.assertThat(fileResource.getCurrentDirectory().resolve("path", RESOLVE_DIRECTORY), Matchers.equalTo(dirResource.getCurrentDirectory()));
        Assert.assertThat(fileResource.getCurrentDirectory().resolve("path", RESOLVE_DIRECTORY), Matchers.equalTo(dirResource.getCurrentDirectory()));
        Assert.assertThat(dirResource.toString(), Matchers.equalTo("/some/test/resource/path/"));
    }
}

