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
package com.thoughtworks.go.domain;


import HttpServletResponse.SC_FORBIDDEN;
import HttpServletResponse.SC_NOT_FOUND;
import HttpServletResponse.SC_NOT_MODIFIED;
import HttpServletResponse.SC_OK;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class ChecksumFileHandlerTest {
    private File file;

    private ChecksumFileHandler checksumFileHandler;

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldGenerateChecksumFileUrl() throws IOException {
        String url = checksumFileHandler.url("http://foo/go", "cruise/1/stage/1/job");
        Assert.assertThat(url, Matchers.is("http://foo/go/remoting/files/cruise/1/stage/1/job/cruise-output/md5.checksum"));
    }

    @Test
    public void shouldStoreTheMd5ChecksumOnTheAgent() throws IOException {
        checksumFileHandler.handle(new ByteArrayInputStream("Hello World".getBytes()));
        Assert.assertThat(FileUtils.readFileToString(file, StandardCharsets.UTF_8), Matchers.is("Hello World"));
    }

    @Test
    public void shouldDeleteOldMd5ChecksumFileIfItWasNotFoundOnTheServer() throws IOException {
        StubGoPublisher goPublisher = new StubGoPublisher();
        file.createNewFile();
        boolean isSuccessful = checksumFileHandler.handleResult(SC_NOT_FOUND, goPublisher);
        Assert.assertThat(isSuccessful, Matchers.is(true));
        Assert.assertThat(file.exists(), Matchers.is(false));
    }

    @Test
    public void shouldRetainMd5ChecksumFileIfItIsDownloadedSuccessfully() throws IOException {
        StubGoPublisher goPublisher = new StubGoPublisher();
        file.createNewFile();
        boolean isSuccessful = checksumFileHandler.handleResult(SC_OK, goPublisher);
        Assert.assertThat(isSuccessful, Matchers.is(true));
        Assert.assertThat(file.exists(), Matchers.is(true));
    }

    @Test
    public void shouldHandleResultIfHttpCodeSaysFileNotFound() {
        StubGoPublisher goPublisher = new StubGoPublisher();
        Assert.assertThat(checksumFileHandler.handleResult(SC_NOT_FOUND, goPublisher), Matchers.is(true));
        Assert.assertThat(goPublisher.getMessage(), Matchers.containsString(String.format("[WARN] The md5checksum property file was not found on the server. Hence, Go can not verify the integrity of the artifacts.", file)));
    }

    @Test
    public void shouldHandleResultIfHttpCodeIsSuccessful() {
        StubGoPublisher goPublisher = new StubGoPublisher();
        Assert.assertThat(checksumFileHandler.handleResult(SC_OK, goPublisher), Matchers.is(true));
    }

    @Test
    public void shouldHandleResultIfHttpCodeSaysFileNotModified() {
        StubGoPublisher goPublisher = new StubGoPublisher();
        Assert.assertThat(checksumFileHandler.handleResult(SC_NOT_MODIFIED, goPublisher), Matchers.is(true));
    }

    @Test
    public void shouldHandleResultIfHttpCodeSaysFilePermissionDenied() {
        StubGoPublisher goPublisher = new StubGoPublisher();
        Assert.assertThat(checksumFileHandler.handleResult(SC_FORBIDDEN, goPublisher), Matchers.is(false));
    }

    @Test
    public void shouldGetArtifactMd5Checksum() throws IOException {
        checksumFileHandler.handle(new ByteArrayInputStream("Hello!!!1".getBytes()));
        ArtifactMd5Checksums artifactMd5Checksums = checksumFileHandler.getArtifactMd5Checksums();
        Assert.assertThat(artifactMd5Checksums, Matchers.is(new ArtifactMd5Checksums(file)));
    }

    @Test
    public void shouldReturnNullArtifactMd5ChecksumIfFileDoesNotExist() {
        file.delete();
        Assert.assertThat(checksumFileHandler.getArtifactMd5Checksums(), Matchers.is(Matchers.nullValue()));
    }
}

