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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class ArtifactMd5ChecksumsTest {
    private File file;

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldReturnTrueIfTheChecksumFileContainsAGivenPath() throws IOException {
        Properties properties = new Properties();
        properties.setProperty("first/path", "md5");
        ArtifactMd5Checksums artifactMd5Checksums = new ArtifactMd5Checksums(properties);
        Assert.assertThat(artifactMd5Checksums.md5For("first/path"), Matchers.is("md5"));
    }

    @Test
    public void shouldReturnNullIfTheChecksumFileDoesNotContainsAGivenPath() throws IOException {
        Properties properties = new Properties();
        properties.setProperty("first/path", "md5");
        ArtifactMd5Checksums artifactMd5Checksums = new ArtifactMd5Checksums(properties);
        Assert.assertThat(artifactMd5Checksums.md5For("foo"), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldLoadThePropertiesFromTheGivenFile() throws IOException {
        FileUtils.writeStringToFile(file, "first/path:md5=", StandardCharsets.UTF_8);
        ArtifactMd5Checksums artifactMd5Checksums = new ArtifactMd5Checksums(file);
        Assert.assertThat(artifactMd5Checksums.md5For("first/path"), Matchers.is("md5="));
    }

    @Test
    public void shouldThrowAnExceptionIfTheLoadingFails() throws IOException {
        try {
            file.delete();
            new ArtifactMd5Checksums(file);
            Assert.fail("Should have failed because of an invalid properites file");
        } catch (RuntimeException e) {
            Assert.assertThat(e.getCause(), Matchers.instanceOf(IOException.class));
            Assert.assertThat(e.getMessage(), Matchers.is(String.format("[Checksum Verification] Could not load the MD5 from the checksum file '%s'", file)));
        }
    }
}

