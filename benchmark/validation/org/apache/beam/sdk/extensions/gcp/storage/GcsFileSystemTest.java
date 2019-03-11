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
package org.apache.beam.sdk.extensions.gcp.storage;


import Status.ERROR;
import Status.NOT_FOUND;
import Status.OK;
import com.google.api.services.storage.model.Objects;
import com.google.api.services.storage.model.StorageObject;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.beam.sdk.io.fs.MatchResult;
import org.apache.beam.sdk.util.GcsUtil;
import org.apache.beam.sdk.util.GcsUtil.StorageObjectOrIOException;
import org.apache.beam.sdk.util.gcsfs.GcsPath;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link GcsFileSystem}.
 */
@RunWith(JUnit4.class)
public class GcsFileSystemTest {
    @Rule
    public transient ExpectedException thrown = ExpectedException.none();

    @Mock
    private GcsUtil mockGcsUtil;

    private GcsFileSystem gcsFileSystem;

    @Test
    public void testMatch() throws Exception {
        Objects modelObjects = new Objects();
        List<StorageObject> items = new ArrayList<>();
        // A directory
        items.add(new StorageObject().setBucket("testbucket").setName("testdirectory/"));
        // Files within the directory
        items.add(/* fileSize */
        createStorageObject("gs://testbucket/testdirectory/file1name", 1L));
        items.add(/* fileSize */
        createStorageObject("gs://testbucket/testdirectory/file2name", 2L));
        items.add(/* fileSize */
        createStorageObject("gs://testbucket/testdirectory/file3name", 3L));
        items.add(/* fileSize */
        createStorageObject("gs://testbucket/testdirectory/file4name", 4L));
        items.add(/* fileSize */
        createStorageObject("gs://testbucket/testdirectory/otherfile", 5L));
        items.add(/* fileSize */
        createStorageObject("gs://testbucket/testdirectory/anotherfile", 6L));
        modelObjects.setItems(items);
        Mockito.when(mockGcsUtil.listObjects(ArgumentMatchers.eq("testbucket"), ArgumentMatchers.anyString(), ArgumentMatchers.isNull(String.class))).thenReturn(modelObjects);
        List<GcsPath> gcsPaths = ImmutableList.of(GcsPath.fromUri("gs://testbucket/testdirectory/non-exist-file"), GcsPath.fromUri("gs://testbucket/testdirectory/otherfile"));
        Mockito.when(mockGcsUtil.getObjects(ArgumentMatchers.eq(gcsPaths))).thenReturn(ImmutableList.of(StorageObjectOrIOException.create(new FileNotFoundException()), StorageObjectOrIOException.create(createStorageObject("gs://testbucket/testdirectory/otherfile", 4L))));
        List<String> specs = ImmutableList.of("gs://testbucket/testdirectory/file[1-3]*", "gs://testbucket/testdirectory/non-exist-file", "gs://testbucket/testdirectory/otherfile");
        List<MatchResult> matchResults = gcsFileSystem.match(specs);
        Assert.assertEquals(3, matchResults.size());
        Assert.assertEquals(OK, matchResults.get(0).status());
        Assert.assertThat(ImmutableList.of("gs://testbucket/testdirectory/file1name", "gs://testbucket/testdirectory/file2name", "gs://testbucket/testdirectory/file3name"), Matchers.contains(toFilenames(matchResults.get(0)).toArray()));
        Assert.assertEquals(NOT_FOUND, matchResults.get(1).status());
        Assert.assertEquals(OK, matchResults.get(2).status());
        Assert.assertThat(ImmutableList.of("gs://testbucket/testdirectory/otherfile"), Matchers.contains(toFilenames(matchResults.get(2)).toArray()));
    }

    @Test
    public void testGlobExpansion() throws IOException {
        Objects modelObjects = new Objects();
        List<StorageObject> items = new ArrayList<>();
        // A directory
        items.add(new StorageObject().setBucket("testbucket").setName("testdirectory/"));
        // Files within the directory
        items.add(/* fileSize */
        createStorageObject("gs://testbucket/testdirectory/file1name", 1L));
        items.add(/* fileSize */
        createStorageObject("gs://testbucket/testdirectory/file2name", 2L));
        items.add(/* fileSize */
        createStorageObject("gs://testbucket/testdirectory/file3name", 3L));
        items.add(/* fileSize */
        createStorageObject("gs://testbucket/testdirectory/otherfile", 4L));
        items.add(/* fileSize */
        createStorageObject("gs://testbucket/testdirectory/anotherfile", 5L));
        items.add(/* fileSize */
        createStorageObject("gs://testbucket/testotherdirectory/file4name", 6L));
        modelObjects.setItems(items);
        Mockito.when(mockGcsUtil.listObjects(ArgumentMatchers.eq("testbucket"), ArgumentMatchers.anyString(), ArgumentMatchers.isNull(String.class))).thenReturn(modelObjects);
        // Test patterns.
        {
            GcsPath pattern = GcsPath.fromUri("gs://testbucket/testdirectory/file*");
            List<String> expectedFiles = ImmutableList.of("gs://testbucket/testdirectory/file1name", "gs://testbucket/testdirectory/file2name", "gs://testbucket/testdirectory/file3name");
            Assert.assertThat(expectedFiles, Matchers.contains(toFilenames(gcsFileSystem.expand(pattern)).toArray()));
        }
        {
            GcsPath pattern = GcsPath.fromUri("gs://testbucket/testdirectory/file*");
            List<String> expectedFiles = ImmutableList.of("gs://testbucket/testdirectory/file1name", "gs://testbucket/testdirectory/file2name", "gs://testbucket/testdirectory/file3name");
            Assert.assertThat(expectedFiles, Matchers.contains(toFilenames(gcsFileSystem.expand(pattern)).toArray()));
        }
        {
            GcsPath pattern = GcsPath.fromUri("gs://testbucket/testdirectory/file[1-3]*");
            List<String> expectedFiles = ImmutableList.of("gs://testbucket/testdirectory/file1name", "gs://testbucket/testdirectory/file2name", "gs://testbucket/testdirectory/file3name");
            Assert.assertThat(expectedFiles, Matchers.contains(toFilenames(gcsFileSystem.expand(pattern)).toArray()));
        }
        {
            GcsPath pattern = GcsPath.fromUri("gs://testbucket/testdirectory/file?name");
            List<String> expectedFiles = ImmutableList.of("gs://testbucket/testdirectory/file1name", "gs://testbucket/testdirectory/file2name", "gs://testbucket/testdirectory/file3name");
            Assert.assertThat(expectedFiles, Matchers.contains(toFilenames(gcsFileSystem.expand(pattern)).toArray()));
        }
        {
            GcsPath pattern = GcsPath.fromUri("gs://testbucket/test*ectory/fi*name");
            List<String> expectedFiles = ImmutableList.of("gs://testbucket/testdirectory/file1name", "gs://testbucket/testdirectory/file2name", "gs://testbucket/testdirectory/file3name", "gs://testbucket/testotherdirectory/file4name");
            Assert.assertThat(expectedFiles, Matchers.contains(toFilenames(gcsFileSystem.expand(pattern)).toArray()));
        }
    }

    @Test
    public void testExpandNonGlob() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Glob expression: [testdirectory/otherfile] is not expandable.");
        gcsFileSystem.expand(GcsPath.fromUri("gs://testbucket/testdirectory/otherfile"));
    }

    @Test
    public void testMatchNonGlobs() throws Exception {
        List<StorageObjectOrIOException> items = new ArrayList<>();
        // Files within the directory
        items.add(StorageObjectOrIOException.create(/* fileSize */
        createStorageObject("gs://testbucket/testdirectory/file1name", 1L)));
        items.add(StorageObjectOrIOException.create(/* fileSize */
        createStorageObject("gs://testbucket/testdirectory/dir2name/", 0L)));
        items.add(StorageObjectOrIOException.create(new FileNotFoundException()));
        items.add(StorageObjectOrIOException.create(new IOException()));
        items.add(StorageObjectOrIOException.create(/* fileSize */
        createStorageObject("gs://testbucket/testdirectory/file4name", 4L)));
        List<GcsPath> gcsPaths = ImmutableList.of(GcsPath.fromUri("gs://testbucket/testdirectory/file1name"), GcsPath.fromUri("gs://testbucket/testdirectory/dir2name/"), GcsPath.fromUri("gs://testbucket/testdirectory/file2name"), GcsPath.fromUri("gs://testbucket/testdirectory/file3name"), GcsPath.fromUri("gs://testbucket/testdirectory/file4name"));
        Mockito.when(mockGcsUtil.getObjects(ArgumentMatchers.eq(gcsPaths))).thenReturn(items);
        List<MatchResult> matchResults = gcsFileSystem.matchNonGlobs(gcsPaths);
        Assert.assertEquals(5, matchResults.size());
        Assert.assertThat(ImmutableList.of("gs://testbucket/testdirectory/file1name"), Matchers.contains(toFilenames(matchResults.get(0)).toArray()));
        Assert.assertThat(ImmutableList.of("gs://testbucket/testdirectory/dir2name/"), Matchers.contains(toFilenames(matchResults.get(1)).toArray()));
        Assert.assertEquals(NOT_FOUND, matchResults.get(2).status());
        Assert.assertEquals(ERROR, matchResults.get(3).status());
        Assert.assertThat(ImmutableList.of("gs://testbucket/testdirectory/file4name"), Matchers.contains(toFilenames(matchResults.get(4)).toArray()));
    }
}

