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
package org.apache.beam.runners.dataflow.util;


import HttpStatusCodes.STATUS_CODE_FORBIDDEN;
import StandardResolveOptions.RESOLVE_FILE;
import com.google.api.services.dataflow.model.DataflowPackage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.Pipe;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.beam.runners.dataflow.util.PackageUtil.PackageAttributes;
import org.apache.beam.sdk.io.FileSystems;
import org.apache.beam.sdk.io.fs.CreateOptions;
import org.apache.beam.sdk.testing.ExpectedLogs;
import org.apache.beam.sdk.testing.RegexMatcher;
import org.apache.beam.sdk.util.FastNanoClockAndSleeper;
import org.apache.beam.sdk.util.GcsUtil;
import org.apache.beam.sdk.util.GcsUtil.StorageObjectOrIOException;
import org.apache.beam.sdk.util.gcsfs.GcsPath;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterables;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Lists;
import org.apache.beam.vendor.guava.v20_0.com.google.common.io.LineReader;
import org.apache.beam.vendor.guava.v20_0.com.google.common.util.concurrent.MoreExecutors;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link PackageUtil}.
 */
@RunWith(JUnit4.class)
public class PackageUtilTest {
    @Rule
    public ExpectedLogs logged = ExpectedLogs.none(PackageUtil.class);

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Rule
    public FastNanoClockAndSleeper fastNanoClockAndSleeper = new FastNanoClockAndSleeper();

    @Mock
    GcsUtil mockGcsUtil;

    // 128 bits, base64 encoded is 171 bits, rounds to 22 bytes
    private static final String HASH_PATTERN = "[a-zA-Z0-9+-]{22}";

    private CreateOptions createOptions;

    private PackageUtil defaultPackageUtil;

    static final GcsPath STAGING_GCS_PATH = GcsPath.fromComponents("somebucket", "base/path/");

    static final String STAGING_PATH = PackageUtilTest.STAGING_GCS_PATH.toString();

    @Test
    public void testFileWithExtensionPackageNamingAndSize() throws Exception {
        String contents = "This is a test!";
        File tmpFile = makeFileWithContents("file.txt", contents);
        PackageAttributes attr = PackageUtilTest.makePackageAttributes(tmpFile, null);
        DataflowPackage target = attr.getDestination();
        Assert.assertThat(target.getName(), RegexMatcher.matches((("file-" + (PackageUtilTest.HASH_PATTERN)) + ".txt")));
        Assert.assertThat(target.getLocation(), Matchers.equalTo(((PackageUtilTest.STAGING_PATH) + (target.getName()))));
        Assert.assertThat(attr.getSize(), Matchers.equalTo(((long) (contents.length()))));
    }

    @Test
    public void testPackageNamingWithFileNoExtension() throws Exception {
        File tmpFile = makeFileWithContents("file", "This is a test!");
        DataflowPackage target = PackageUtilTest.makePackageAttributes(tmpFile, null).getDestination();
        Assert.assertThat(target.getName(), RegexMatcher.matches(("file-" + (PackageUtilTest.HASH_PATTERN))));
        Assert.assertThat(target.getLocation(), Matchers.equalTo(((PackageUtilTest.STAGING_PATH) + (target.getName()))));
    }

    @Test
    public void testPackageNamingWithDirectory() throws Exception {
        File tmpDirectory = tmpFolder.newFolder("folder");
        DataflowPackage target = PackageUtilTest.makePackageAttributes(tmpDirectory, null).getDestination();
        Assert.assertThat(target.getName(), RegexMatcher.matches((("folder-" + (PackageUtilTest.HASH_PATTERN)) + ".jar")));
        Assert.assertThat(target.getLocation(), Matchers.equalTo(((PackageUtilTest.STAGING_PATH) + (target.getName()))));
    }

    @Test
    public void testPackageNamingWithFilesHavingSameContentsAndSameNames() throws Exception {
        File tmpDirectory1 = tmpFolder.newFolder("folder1", "folderA");
        makeFileWithContents("folder1/folderA/sameName", "This is a test!");
        DataflowPackage target1 = PackageUtilTest.makePackageAttributes(tmpDirectory1, null).getDestination();
        File tmpDirectory2 = tmpFolder.newFolder("folder2", "folderA");
        makeFileWithContents("folder2/folderA/sameName", "This is a test!");
        DataflowPackage target2 = PackageUtilTest.makePackageAttributes(tmpDirectory2, null).getDestination();
        Assert.assertEquals(target1.getName(), target2.getName());
        Assert.assertEquals(target1.getLocation(), target2.getLocation());
    }

    @Test
    public void testPackageNamingWithFilesHavingSameContentsButDifferentNames() throws Exception {
        File tmpDirectory1 = tmpFolder.newFolder("folder1", "folderA");
        makeFileWithContents("folder1/folderA/uniqueName1", "This is a test!");
        DataflowPackage target1 = PackageUtilTest.makePackageAttributes(tmpDirectory1, null).getDestination();
        File tmpDirectory2 = tmpFolder.newFolder("folder2", "folderA");
        makeFileWithContents("folder2/folderA/uniqueName2", "This is a test!");
        DataflowPackage target2 = PackageUtilTest.makePackageAttributes(tmpDirectory2, null).getDestination();
        Assert.assertNotEquals(target1.getName(), target2.getName());
        Assert.assertNotEquals(target1.getLocation(), target2.getLocation());
    }

    @Test
    public void testPackageNamingWithDirectoriesHavingSameContentsButDifferentNames() throws Exception {
        File tmpDirectory1 = tmpFolder.newFolder("folder1", "folderA");
        tmpFolder.newFolder("folder1", "folderA", "uniqueName1");
        DataflowPackage target1 = PackageUtilTest.makePackageAttributes(tmpDirectory1, null).getDestination();
        File tmpDirectory2 = tmpFolder.newFolder("folder2", "folderA");
        tmpFolder.newFolder("folder2", "folderA", "uniqueName2");
        DataflowPackage target2 = PackageUtilTest.makePackageAttributes(tmpDirectory2, null).getDestination();
        Assert.assertNotEquals(target1.getName(), target2.getName());
        Assert.assertNotEquals(target1.getLocation(), target2.getLocation());
    }

    @Test
    public void testPackageUploadWithLargeClasspathLogsWarning() throws Exception {
        File tmpFile = makeFileWithContents("file.txt", "This is a test!");
        Mockito.when(mockGcsUtil.getObjects(ArgumentMatchers.anyListOf(GcsPath.class))).thenReturn(ImmutableList.of(StorageObjectOrIOException.create(createStorageObject(PackageUtilTest.STAGING_PATH, tmpFile.length()))));
        List<String> classpathElements = Lists.newLinkedList();
        for (int i = 0; i < 1005; ++i) {
            String eltName = "element" + i;
            classpathElements.add(((eltName + '=') + (tmpFile.getAbsolutePath())));
        }
        defaultPackageUtil.stageClasspathElements(classpathElements, PackageUtilTest.STAGING_PATH, createOptions);
        logged.verifyWarn("Your classpath contains 1005 elements, which Google Cloud Dataflow");
    }

    @Test
    public void testPackageUploadWithFileSucceeds() throws Exception {
        Pipe pipe = Pipe.open();
        String contents = "This is a test!";
        File tmpFile = makeFileWithContents("file.txt", contents);
        Mockito.when(mockGcsUtil.getObjects(ArgumentMatchers.anyListOf(GcsPath.class))).thenReturn(ImmutableList.of(StorageObjectOrIOException.create(new FileNotFoundException("some/path"))));
        Mockito.when(mockGcsUtil.create(ArgumentMatchers.any(GcsPath.class), ArgumentMatchers.anyString())).thenReturn(pipe.sink());
        List<DataflowPackage> targets = defaultPackageUtil.stageClasspathElements(ImmutableList.of(tmpFile.getAbsolutePath()), PackageUtilTest.STAGING_PATH, createOptions);
        DataflowPackage target = Iterables.getOnlyElement(targets);
        Mockito.verify(mockGcsUtil).getObjects(ArgumentMatchers.anyListOf(GcsPath.class));
        Mockito.verify(mockGcsUtil).create(ArgumentMatchers.any(GcsPath.class), ArgumentMatchers.anyString());
        Mockito.verifyNoMoreInteractions(mockGcsUtil);
        Assert.assertThat(target.getName(), RegexMatcher.matches((("file-" + (PackageUtilTest.HASH_PATTERN)) + ".txt")));
        Assert.assertThat(target.getLocation(), Matchers.equalTo(((PackageUtilTest.STAGING_PATH) + (target.getName()))));
        Assert.assertThat(new LineReader(Channels.newReader(pipe.source(), StandardCharsets.UTF_8.name())).readLine(), Matchers.equalTo(contents));
    }

    @Test
    public void testStagingPreservesClasspath() throws Exception {
        File smallFile = makeFileWithContents("small.txt", "small");
        File largeFile = makeFileWithContents("large.txt", "large contents");
        Mockito.when(mockGcsUtil.getObjects(ArgumentMatchers.anyListOf(GcsPath.class))).thenReturn(ImmutableList.of(StorageObjectOrIOException.create(new FileNotFoundException("some/path"))));
        Mockito.when(mockGcsUtil.create(ArgumentMatchers.any(GcsPath.class), ArgumentMatchers.anyString())).thenAnswer(( invocation) -> Pipe.open().sink());
        List<DataflowPackage> targets = defaultPackageUtil.stageClasspathElements(ImmutableList.of(smallFile.getAbsolutePath(), largeFile.getAbsolutePath()), PackageUtilTest.STAGING_PATH, createOptions);
        // Verify that the packages are returned small, then large, matching input order even though
        // the large file would be uploaded first.
        Assert.assertThat(targets.get(0).getName(), Matchers.startsWith("small"));
        Assert.assertThat(targets.get(1).getName(), Matchers.startsWith("large"));
    }

    @Test
    public void testPackageUploadWithDirectorySucceeds() throws Exception {
        Pipe pipe = Pipe.open();
        File tmpDirectory = tmpFolder.newFolder("folder");
        tmpFolder.newFolder("folder", "empty_directory");
        tmpFolder.newFolder("folder", "directory");
        makeFileWithContents("folder/file.txt", "This is a test!");
        makeFileWithContents("folder/directory/file.txt", "This is also a test!");
        Mockito.when(mockGcsUtil.getObjects(ArgumentMatchers.anyListOf(GcsPath.class))).thenReturn(ImmutableList.of(StorageObjectOrIOException.create(new FileNotFoundException("some/path"))));
        Mockito.when(mockGcsUtil.create(ArgumentMatchers.any(GcsPath.class), ArgumentMatchers.anyString())).thenReturn(pipe.sink());
        defaultPackageUtil.stageClasspathElements(ImmutableList.of(tmpDirectory.getAbsolutePath()), PackageUtilTest.STAGING_PATH, createOptions);
        Mockito.verify(mockGcsUtil).getObjects(ArgumentMatchers.anyListOf(GcsPath.class));
        Mockito.verify(mockGcsUtil).create(ArgumentMatchers.any(GcsPath.class), ArgumentMatchers.anyString());
        Mockito.verifyNoMoreInteractions(mockGcsUtil);
        List<String> zipEntryNames = new ArrayList<>();
        try (ZipInputStream inputStream = new ZipInputStream(Channels.newInputStream(pipe.source()))) {
            for (ZipEntry entry = inputStream.getNextEntry(); entry != null; entry = inputStream.getNextEntry()) {
                zipEntryNames.add(entry.getName());
            }
        }
        Assert.assertThat(zipEntryNames, containsInAnyOrder("directory/file.txt", "empty_directory/", "file.txt"));
    }

    @Test
    public void testPackageUploadWithEmptyDirectorySucceeds() throws Exception {
        Pipe pipe = Pipe.open();
        File tmpDirectory = tmpFolder.newFolder("folder");
        Mockito.when(mockGcsUtil.getObjects(ArgumentMatchers.anyListOf(GcsPath.class))).thenReturn(ImmutableList.of(StorageObjectOrIOException.create(new FileNotFoundException("some/path"))));
        Mockito.when(mockGcsUtil.create(ArgumentMatchers.any(GcsPath.class), ArgumentMatchers.anyString())).thenReturn(pipe.sink());
        List<DataflowPackage> targets = defaultPackageUtil.stageClasspathElements(ImmutableList.of(tmpDirectory.getAbsolutePath()), PackageUtilTest.STAGING_PATH, createOptions);
        DataflowPackage target = Iterables.getOnlyElement(targets);
        Mockito.verify(mockGcsUtil).getObjects(ArgumentMatchers.anyListOf(GcsPath.class));
        Mockito.verify(mockGcsUtil).create(ArgumentMatchers.any(GcsPath.class), ArgumentMatchers.anyString());
        Mockito.verifyNoMoreInteractions(mockGcsUtil);
        Assert.assertThat(target.getName(), RegexMatcher.matches((("folder-" + (PackageUtilTest.HASH_PATTERN)) + ".jar")));
        Assert.assertThat(target.getLocation(), Matchers.equalTo(((PackageUtilTest.STAGING_PATH) + (target.getName()))));
        try (ZipInputStream zipInputStream = new ZipInputStream(Channels.newInputStream(pipe.source()))) {
            Assert.assertNull(zipInputStream.getNextEntry());
        }
    }

    @Test(expected = RuntimeException.class)
    public void testPackageUploadFailsWhenIOExceptionThrown() throws Exception {
        File tmpFile = makeFileWithContents("file.txt", "This is a test!");
        Mockito.when(mockGcsUtil.getObjects(ArgumentMatchers.anyListOf(GcsPath.class))).thenReturn(ImmutableList.of(StorageObjectOrIOException.create(new FileNotFoundException("some/path"))));
        Mockito.when(mockGcsUtil.create(ArgumentMatchers.any(GcsPath.class), ArgumentMatchers.anyString())).thenThrow(new IOException("Fake Exception: Upload error"));
        try (PackageUtil directPackageUtil = PackageUtil.withExecutorService(MoreExecutors.newDirectExecutorService())) {
            directPackageUtil.stageClasspathElements(ImmutableList.of(tmpFile.getAbsolutePath()), PackageUtilTest.STAGING_PATH, fastNanoClockAndSleeper, createOptions);
        } finally {
            Mockito.verify(mockGcsUtil).getObjects(ArgumentMatchers.anyListOf(GcsPath.class));
            Mockito.verify(mockGcsUtil, Mockito.times(5)).create(ArgumentMatchers.any(GcsPath.class), ArgumentMatchers.anyString());
            Mockito.verifyNoMoreInteractions(mockGcsUtil);
        }
    }

    @Test
    public void testPackageUploadFailsWithPermissionsErrorGivesDetailedMessage() throws Exception {
        File tmpFile = makeFileWithContents("file.txt", "This is a test!");
        Mockito.when(mockGcsUtil.getObjects(ArgumentMatchers.anyListOf(GcsPath.class))).thenReturn(ImmutableList.of(StorageObjectOrIOException.create(new FileNotFoundException("some/path"))));
        Mockito.when(mockGcsUtil.create(ArgumentMatchers.any(GcsPath.class), ArgumentMatchers.anyString())).thenThrow(new IOException(("Failed to write to GCS path " + (PackageUtilTest.STAGING_PATH)), PackageUtilTest.googleJsonResponseException(STATUS_CODE_FORBIDDEN, "Permission denied", "Test message")));
        try (PackageUtil directPackageUtil = PackageUtil.withExecutorService(MoreExecutors.newDirectExecutorService())) {
            directPackageUtil.stageClasspathElements(ImmutableList.of(tmpFile.getAbsolutePath()), PackageUtilTest.STAGING_PATH, fastNanoClockAndSleeper, createOptions);
            Assert.fail("Expected RuntimeException");
        } catch (RuntimeException e) {
            Assert.assertThat("Expected RuntimeException wrapping IOException.", e.getCause(), Matchers.instanceOf(RuntimeException.class));
            Assert.assertThat("Expected IOException containing detailed message.", e.getCause().getCause(), Matchers.instanceOf(IOException.class));
            Assert.assertThat(e.getCause().getCause().getMessage(), Matchers.allOf(Matchers.containsString("Uploaded failed due to permissions error"), Matchers.containsString(("Stale credentials can be resolved by executing 'gcloud auth application-default " + "login'"))));
        } finally {
            Mockito.verify(mockGcsUtil).getObjects(ArgumentMatchers.anyListOf(GcsPath.class));
            Mockito.verify(mockGcsUtil).create(ArgumentMatchers.any(GcsPath.class), ArgumentMatchers.anyString());
            Mockito.verifyNoMoreInteractions(mockGcsUtil);
        }
    }

    @Test
    public void testPackageUploadEventuallySucceeds() throws Exception {
        Pipe pipe = Pipe.open();
        File tmpFile = makeFileWithContents("file.txt", "This is a test!");
        Mockito.when(mockGcsUtil.getObjects(ArgumentMatchers.anyListOf(GcsPath.class))).thenReturn(ImmutableList.of(StorageObjectOrIOException.create(new FileNotFoundException("some/path"))));
        // First attempt fails
        Mockito.when(mockGcsUtil.create(ArgumentMatchers.any(GcsPath.class), ArgumentMatchers.anyString())).thenThrow(new IOException("Fake Exception: 410 Gone")).thenReturn(pipe.sink());// second attempt succeeds

        try (PackageUtil directPackageUtil = PackageUtil.withExecutorService(MoreExecutors.newDirectExecutorService())) {
            directPackageUtil.stageClasspathElements(ImmutableList.of(tmpFile.getAbsolutePath()), PackageUtilTest.STAGING_PATH, fastNanoClockAndSleeper, createOptions);
        } finally {
            Mockito.verify(mockGcsUtil).getObjects(ArgumentMatchers.anyListOf(GcsPath.class));
            Mockito.verify(mockGcsUtil, Mockito.times(2)).create(ArgumentMatchers.any(GcsPath.class), ArgumentMatchers.anyString());
            Mockito.verifyNoMoreInteractions(mockGcsUtil);
        }
    }

    @Test
    public void testPackageUploadIsSkippedWhenFileAlreadyExists() throws Exception {
        File tmpFile = makeFileWithContents("file.txt", "This is a test!");
        Mockito.when(mockGcsUtil.getObjects(ArgumentMatchers.anyListOf(GcsPath.class))).thenReturn(ImmutableList.of(StorageObjectOrIOException.create(createStorageObject(PackageUtilTest.STAGING_PATH, tmpFile.length()))));
        defaultPackageUtil.stageClasspathElements(ImmutableList.of(tmpFile.getAbsolutePath()), PackageUtilTest.STAGING_PATH, createOptions);
        Mockito.verify(mockGcsUtil).getObjects(ArgumentMatchers.anyListOf(GcsPath.class));
        Mockito.verifyNoMoreInteractions(mockGcsUtil);
    }

    @Test
    public void testPackageUploadIsNotSkippedWhenSizesAreDifferent() throws Exception {
        Pipe pipe = Pipe.open();
        File tmpDirectory = tmpFolder.newFolder("folder");
        tmpFolder.newFolder("folder", "empty_directory");
        tmpFolder.newFolder("folder", "directory");
        makeFileWithContents("folder/file.txt", "This is a test!");
        makeFileWithContents("folder/directory/file.txt", "This is also a test!");
        Mockito.when(mockGcsUtil.getObjects(ArgumentMatchers.anyListOf(GcsPath.class))).thenReturn(ImmutableList.of(StorageObjectOrIOException.create(createStorageObject(PackageUtilTest.STAGING_PATH, Long.MAX_VALUE))));
        Mockito.when(mockGcsUtil.create(ArgumentMatchers.any(GcsPath.class), ArgumentMatchers.anyString())).thenReturn(pipe.sink());
        defaultPackageUtil.stageClasspathElements(ImmutableList.of(tmpDirectory.getAbsolutePath()), PackageUtilTest.STAGING_PATH, createOptions);
        Mockito.verify(mockGcsUtil).getObjects(ArgumentMatchers.anyListOf(GcsPath.class));
        Mockito.verify(mockGcsUtil).create(ArgumentMatchers.any(GcsPath.class), ArgumentMatchers.anyString());
        Mockito.verifyNoMoreInteractions(mockGcsUtil);
    }

    @Test
    public void testPackageUploadWithExplicitPackageName() throws Exception {
        Pipe pipe = Pipe.open();
        File tmpFile = makeFileWithContents("file.txt", "This is a test!");
        final String overriddenName = "alias.txt";
        Mockito.when(mockGcsUtil.getObjects(ArgumentMatchers.anyListOf(GcsPath.class))).thenReturn(ImmutableList.of(StorageObjectOrIOException.create(new FileNotFoundException("some/path"))));
        Mockito.when(mockGcsUtil.create(ArgumentMatchers.any(GcsPath.class), ArgumentMatchers.anyString())).thenReturn(pipe.sink());
        List<DataflowPackage> targets = defaultPackageUtil.stageClasspathElements(ImmutableList.of(((overriddenName + "=") + (tmpFile.getAbsolutePath()))), PackageUtilTest.STAGING_PATH, createOptions);
        DataflowPackage target = Iterables.getOnlyElement(targets);
        Mockito.verify(mockGcsUtil).getObjects(ArgumentMatchers.anyListOf(GcsPath.class));
        Mockito.verify(mockGcsUtil).create(ArgumentMatchers.any(GcsPath.class), ArgumentMatchers.anyString());
        Mockito.verifyNoMoreInteractions(mockGcsUtil);
        Assert.assertThat(target.getName(), Matchers.equalTo(overriddenName));
        Assert.assertThat(target.getLocation(), RegexMatcher.matches(((((PackageUtilTest.STAGING_PATH) + "file-") + (PackageUtilTest.HASH_PATTERN)) + ".txt")));
    }

    @Test
    public void testPackageUploadIsSkippedWithNonExistentResource() throws Exception {
        String nonExistentFile = FileSystems.matchNewResource(tmpFolder.getRoot().getPath(), true).resolve("non-existent-file", RESOLVE_FILE).toString();
        Assert.assertEquals(Collections.EMPTY_LIST, defaultPackageUtil.stageClasspathElements(ImmutableList.of(nonExistentFile), PackageUtilTest.STAGING_PATH, createOptions));
    }
}

