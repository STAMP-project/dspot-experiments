/**
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.util;


import com.thoughtworks.go.domain.DirectoryEntry;
import com.thoughtworks.go.domain.FolderDirectoryEntry;
import com.thoughtworks.go.domain.JobIdentifier;
import java.io.File;
import java.net.URLEncoder;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class DirectoryReaderTest {
    private File testFolder;

    private JobIdentifier jobIdentifier;

    private String folderRoot;

    @Test
    public void shouldNotDieIfGivenBogusPath() throws Exception {
        DirectoryReader reader = new DirectoryReader(jobIdentifier);
        List<DirectoryEntry> entries = reader.listEntries(new File("totally bogus path!!!"), "");
        Assert.assertThat(entries.size(), Matchers.is(0));
    }

    @Test
    public void shouldNotDieIfGivenBogusFile() {
        DirectoryReader reader = new DirectoryReader(jobIdentifier);
        List<DirectoryEntry> entries = reader.listEntries(null, "");
        Assert.assertThat(entries.size(), Matchers.is(0));
    }

    @Test
    public void shouldGetFileList() throws Exception {
        String filename = "text.html$%";
        TestFileUtil.createTestFile(testFolder, filename);
        DirectoryReader reader = new DirectoryReader(jobIdentifier);
        List<DirectoryEntry> entries = reader.listEntries(testFolder, folderRoot);
        Assert.assertThat(entries.size(), Matchers.is(1));
        Assert.assertThat(entries.get(0).getFileName(), Matchers.is(filename));
        Assert.assertThat(entries.get(0).getUrl(), Matchers.is(((("/files/pipelineName/LATEST/stageName/LATEST/buildName" + (folderRoot)) + "/") + (URLEncoder.encode(filename)))));
    }

    @Test
    public void shouldGetSubSubFolder() throws Exception {
        TestFileUtil.createTestFile(TestFileUtil.createTestFolder(TestFileUtil.createTestFolder(testFolder, "primate"), "monkey"), "baboon.html");
        DirectoryReader reader = new DirectoryReader(jobIdentifier);
        List<DirectoryEntry> entries = reader.listEntries(testFolder, folderRoot);
        FolderDirectoryEntry folder = ((FolderDirectoryEntry) (entries.get(0)));
        Assert.assertThat(folder.getFileName(), Matchers.is("primate"));
        FolderDirectoryEntry subFolder = ((FolderDirectoryEntry) (folder.getSubDirectory().get(0)));
        Assert.assertThat(subFolder.getFileName(), Matchers.is("monkey"));
        Assert.assertThat(subFolder.getSubDirectory().get(0).getFileName(), Matchers.is("baboon.html"));
        Assert.assertThat(subFolder.getSubDirectory().get(0).getUrl(), Matchers.is((("/files/pipelineName/LATEST/stageName/LATEST/buildName" + (folderRoot)) + "/primate/monkey/baboon.html")));
    }

    @Test
    public void shouldGetListOfFilesAndFolders() throws Exception {
        TestFileUtil.createTestFile(testFolder, "text.html");
        File subFolder = TestFileUtil.createTestFolder(testFolder, "primate");
        TestFileUtil.createTestFile(subFolder, "baboon.html");
        DirectoryReader reader = new DirectoryReader(jobIdentifier);
        List<DirectoryEntry> entries = reader.listEntries(testFolder, folderRoot);
        Assert.assertThat(entries.size(), Matchers.is(2));
        FolderDirectoryEntry folder = ((FolderDirectoryEntry) (entries.get(0)));
        Assert.assertThat(folder.getFileName(), Matchers.is("primate"));
        Assert.assertThat(folder.getUrl(), Matchers.is((("/files/pipelineName/LATEST/stageName/LATEST/buildName" + (folderRoot)) + "/primate")));
        Assert.assertThat(entries.get(1).getFileName(), Matchers.is("text.html"));
        Assert.assertThat(folder.getSubDirectory().get(0).getFileName(), Matchers.is("baboon.html"));
        Assert.assertThat(folder.getSubDirectory().get(0).getUrl(), Matchers.is((("/files/pipelineName/LATEST/stageName/LATEST/buildName" + (folderRoot)) + "/primate/baboon.html")));
    }

    @Test
    public void shouldGetListOfFilesWithDirectoriesFirstAndFilesInAlphabeticOrder() throws Exception {
        TestFileUtil.createTestFile(testFolder, "build.html");
        File subFolder = TestFileUtil.createTestFolder(testFolder, "testoutput");
        TestFileUtil.createTestFile(subFolder, "baboon.html");
        TestFileUtil.createTestFile(subFolder, "apple.html");
        TestFileUtil.createTestFile(subFolder, "pear.html");
        DirectoryReader reader = new DirectoryReader(jobIdentifier);
        List<DirectoryEntry> entries = reader.listEntries(testFolder, folderRoot);
        Assert.assertThat(entries.size(), Matchers.is(2));
        FolderDirectoryEntry folder = ((FolderDirectoryEntry) (entries.get(0)));
        Assert.assertThat(folder.getFileName(), Matchers.is("testoutput"));
        Assert.assertThat(entries.get(1).getFileName(), Matchers.is("build.html"));
        Assert.assertThat(folder.getSubDirectory().get(0).getFileName(), Matchers.is("apple.html"));
        Assert.assertThat(folder.getSubDirectory().get(1).getFileName(), Matchers.is("baboon.html"));
        Assert.assertThat(folder.getSubDirectory().get(2).getFileName(), Matchers.is("pear.html"));
    }

    @Test
    public void shouldNotContainSerializedObjectFile() throws Exception {
        String filename = ".log200806041535.xml.ser";
        TestFileUtil.createTestFile(testFolder, filename);
        DirectoryReader reader = new DirectoryReader(jobIdentifier);
        List<DirectoryEntry> entries = reader.listEntries(testFolder, folderRoot);
        Assert.assertThat(entries.size(), Matchers.is(0));
    }

    @Test
    public void shouldKeepRootsInUrl() throws Exception {
        File b = TestFileUtil.createTestFolder(testFolder, "b");
        TestFileUtil.createTestFile(b, "c.xml");
        List<DirectoryEntry> entries = listEntries(b, ((folderRoot) + "/b"));
        Assert.assertThat(entries.size(), Matchers.is(1));
        String expectedUrl = ("/files/pipelineName/LATEST/stageName/LATEST/buildName/" + (testFolder.getName())) + "/b/c.xml";
        Assert.assertThat(entries.get(0).getUrl(), Matchers.is(expectedUrl));
    }
}

