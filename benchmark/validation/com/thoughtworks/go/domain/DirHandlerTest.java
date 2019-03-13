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


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.commons.codec.digest.DigestUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;


public class DirHandlerTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File artifactDest;

    private File agentDest;

    private ArtifactMd5Checksums checksums;

    private StubGoPublisher goPublisher;

    private File zip;

    private DirHandler dirHandler;

    @Test
    public void shouldComputeMd5ForEveryFileInADirectory() throws IOException {
        zip = createZip("under_dir");
        dirHandler.useArtifactMd5Checksums(checksums);
        Mockito.when(checksums.md5For("fetch_dest/first")).thenReturn(DigestUtils.md5Hex("First File"));
        Mockito.when(checksums.md5For("fetch_dest/under_dir/second")).thenReturn(DigestUtils.md5Hex("Second File"));
        dirHandler.handle(new FileInputStream(zip));
        dirHandler.handleResult(200, goPublisher);
        Assert.assertThat(goPublisher.getMessage(), Matchers.containsString(String.format("Saved artifact to [%s] after verifying the integrity of its contents.", agentDest)));
        Assert.assertThat(goPublisher.getMessage(), Matchers.not(Matchers.containsString("[WARN]")));
        assertArtifactWasSaved("under_dir");
    }

    @Test
    public void shouldSuccessfullyProceedIfNoMd5IsPresentForTheFileUnderInspection() throws IOException {
        Mockito.when(checksums.md5For("fetch_dest/first")).thenReturn(null);
        zip = createZip("under_dir");
        dirHandler.useArtifactMd5Checksums(checksums);
        try {
            dirHandler.handle(new FileInputStream(zip));
            dirHandler.handleResult(200, goPublisher);
        } catch (RuntimeException e) {
            Assert.fail("should not have failed");
        }
        Mockito.verify(checksums).md5For("fetch_dest/first");
        Mockito.verify(checksums).md5For("fetch_dest/under_dir/second");
        assertArtifactWasSaved("under_dir");
    }

    @Test
    public void shouldProceedSuccessfullyWhenNoChecksumFileIsPresent() throws IOException {
        zip = createZip("under_dir");
        dirHandler.useArtifactMd5Checksums(null);
        dirHandler.handle(new FileInputStream(zip));
        dirHandler.handleResult(200, goPublisher);
        Assert.assertThat(goPublisher.getMessage(), Matchers.not(Matchers.containsString(String.format("[WARN] The md5checksum value of the artifact [%s] was not found on the server. Hence, Go could not verify the integrity of its contents.", agentDest))));
        Assert.assertThat(goPublisher.getMessage(), Matchers.containsString(String.format("Saved artifact to [%s] without verifying the integrity of its contents.", agentDest)));
        assertArtifactWasSaved("under_dir");
    }

    @Test
    public void shouldProceedSuccessfullyWhenNoChecksumFileIsPresentJustForASingleFile() throws IOException {
        zip = createZip("under_dir");
        dirHandler.useArtifactMd5Checksums(checksums);
        dirHandler.handle(new FileInputStream(zip));
        dirHandler.handleResult(200, goPublisher);
        Assert.assertThat(goPublisher.getMessage(), Matchers.containsString(String.format("[WARN] The md5checksum value of the artifact [%s] was not found on the server. Hence, Go could not verify the integrity of its contents.", "fetch_dest/under_dir/second")));
        Assert.assertThat(goPublisher.getMessage(), Matchers.containsString(String.format("[WARN] The md5checksum value of the artifact [%s] was not found on the server. Hence, Go could not verify the integrity of its contents.", "fetch_dest/first")));
        Assert.assertThat(goPublisher.getMessage(), Matchers.containsString(String.format("Saved artifact to [%s] without verifying the integrity of its contents.", agentDest)));
        assertArtifactWasSaved("under_dir");
    }

    @Test
    public void shouldThrowExceptionWhenChecksumsDoNotMatch() throws IOException {
        Mockito.when(checksums.md5For("fetch_dest/first")).thenReturn("foo");
        dirHandler.useArtifactMd5Checksums(checksums);
        zip = createZip("under_dir");
        try {
            dirHandler.handle(new FileInputStream(zip));
            dirHandler.handleResult(200, goPublisher);
            Assert.fail("Should throw exception when check sums do not match.");
        } catch (RuntimeException e) {
            Assert.assertThat(e.getMessage(), Matchers.is("Artifact download failed for [fetch_dest/first]"));
            Assert.assertThat(goPublisher.getMessage(), Matchers.containsString("[ERROR] Verification of the integrity of the artifact [fetch_dest/first] failed. The artifact file on the server may have changed since its original upload."));
        }
    }

    @Test
    public void shouldUseCompletePathToLookUpMd5Checksum() throws IOException {
        DirHandler handler = new DirHandler("server/fetch_dest", agentDest);
        zip = createZip("under_dir");
        handler.useArtifactMd5Checksums(checksums);
        Mockito.when(checksums.md5For("server/fetch_dest/first")).thenReturn(DigestUtils.md5Hex("First File"));
        Mockito.when(checksums.md5For("server/fetch_dest/under_dir/second")).thenReturn(DigestUtils.md5Hex("Second File"));
        handler.handle(new FileInputStream(zip));
        handler.handleResult(200, goPublisher);
        Assert.assertThat(goPublisher.getMessage(), Matchers.containsString(String.format("Saved artifact to [%s] after verifying the integrity of its contents.", agentDest)));
        Assert.assertThat(goPublisher.getMessage(), Matchers.not(Matchers.containsString("[WARN]")));
        assertArtifactWasSaved("under_dir");
    }

    @Test
    public void shouldUseCorrectPathOnServerToLookUpMd5Checksum() throws IOException {
        DirHandler handler = new DirHandler("fetch_dest", agentDest);
        zip = createZip("fetch_dest");
        handler.useArtifactMd5Checksums(checksums);
        Mockito.when(checksums.md5For("fetch_dest/first")).thenReturn(DigestUtils.md5Hex("First File"));
        Mockito.when(checksums.md5For("fetch_dest/fetch_dest/second")).thenReturn(DigestUtils.md5Hex("Second File"));
        handler.handle(new FileInputStream(zip));
        handler.handleResult(200, goPublisher);
        Mockito.verify(checksums).md5For("fetch_dest/first");
        Mockito.verify(checksums).md5For("fetch_dest/fetch_dest/second");
        Assert.assertThat(goPublisher.getMessage(), Matchers.containsString(String.format("Saved artifact to [%s] after verifying the integrity of its contents.", agentDest)));
        Assert.assertThat(goPublisher.getMessage(), Matchers.not(Matchers.containsString("[WARN]")));
        assertArtifactWasSaved("fetch_dest");
    }
}

