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
package com.thoughtworks.go.agent;


import HttpServletResponse.SC_BAD_GATEWAY;
import HttpServletResponse.SC_OK;
import HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE;
import com.thoughtworks.go.buildsession.ArtifactsRepository;
import com.thoughtworks.go.domain.Property;
import com.thoughtworks.go.helper.TestStreamConsumer;
import com.thoughtworks.go.util.CachedDigestUtils;
import com.thoughtworks.go.util.HttpService;
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
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class UrlBasedArtifactsRepositoryTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Mock
    private HttpService httpService;

    private File tempFile;

    private File artifactFolder;

    private ArtifactsRepository artifactsRepository;

    private TestStreamConsumer console;

    @Test
    public void shouldBombWithErrorWhenStatusCodeReturnedIsRequestEntityTooLarge() throws IOException, InterruptedException {
        Mockito.when(httpService.upload(ArgumentMatchers.any(String.class), ArgumentMatchers.eq(tempFile.length()), ArgumentMatchers.any(File.class), ArgumentMatchers.any(Properties.class))).thenReturn(SC_REQUEST_ENTITY_TOO_LARGE);
        try {
            artifactsRepository.upload(console, tempFile, "some_dest", "build42");
            Assert.fail("should have thrown request entity too large error");
        } catch (RuntimeException e) {
            String expectedMessage = ((("Artifact upload for file " + (tempFile.getAbsolutePath())) + " (Size: ") + (tempFile.length())) + ") was denied by the server. This usually happens when server runs out of disk space.";
            Assert.assertThat(e.getMessage(), Matchers.is((("java.lang.RuntimeException: " + expectedMessage) + ".  HTTP return code is 413")));
            Assert.assertThat(console.output().contains(expectedMessage), Matchers.is(true));
        }
    }

    @Test
    public void uploadShouldBeGivenFileSize() throws IOException {
        Mockito.when(httpService.upload(ArgumentMatchers.any(String.class), ArgumentMatchers.eq(tempFile.length()), ArgumentMatchers.any(File.class), ArgumentMatchers.any(Properties.class))).thenReturn(SC_REQUEST_ENTITY_TOO_LARGE);
        try {
            artifactsRepository.upload(console, tempFile, "dest", "build42");
            Assert.fail("should have thrown request entity too large error");
        } catch (RuntimeException e) {
            Mockito.verify(httpService).upload(ArgumentMatchers.eq("http://baseurl/artifacts/dest?attempt=1&buildId=build42"), ArgumentMatchers.eq(tempFile.length()), ArgumentMatchers.any(File.class), ArgumentMatchers.any(Properties.class));
        }
    }

    @Test
    public void shouldRetryUponUploadFailure() throws IOException {
        String data = "Some text whose checksum can be asserted";
        final String md5 = CachedDigestUtils.md5Hex(data);
        FileUtils.writeStringToFile(tempFile, data, StandardCharsets.UTF_8);
        Properties properties = new Properties();
        properties.setProperty("dest/path/file.txt", md5);
        Mockito.when(httpService.upload(ArgumentMatchers.eq("http://baseurl/artifacts/dest/path?attempt=1&buildId=build42"), ArgumentMatchers.eq(tempFile.length()), ArgumentMatchers.any(File.class), ArgumentMatchers.eq(properties))).thenReturn(SC_BAD_GATEWAY);
        Mockito.when(httpService.upload(ArgumentMatchers.eq("http://baseurl/artifacts/dest/path?attempt=2&buildId=build42"), ArgumentMatchers.eq(tempFile.length()), ArgumentMatchers.any(File.class), ArgumentMatchers.eq(properties))).thenReturn(SC_BAD_GATEWAY);
        Mockito.when(httpService.upload(ArgumentMatchers.eq("http://baseurl/artifacts/dest/path?attempt=3&buildId=build42"), ArgumentMatchers.eq(tempFile.length()), ArgumentMatchers.any(File.class), ArgumentMatchers.eq(properties))).thenReturn(SC_OK);
        artifactsRepository.upload(console, tempFile, "dest/path", "build42");
    }

    @Test
    public void shouldPrintFailureMessageToConsoleWhenUploadFailed() throws IOException {
        String data = "Some text whose checksum can be asserted";
        final String md5 = CachedDigestUtils.md5Hex(data);
        FileUtils.writeStringToFile(tempFile, data, StandardCharsets.UTF_8);
        Properties properties = new Properties();
        properties.setProperty("dest/path/file.txt", md5);
        Mockito.when(httpService.upload(ArgumentMatchers.eq("http://baseurl/artifacts/dest/path?attempt=1&buildId=build42"), ArgumentMatchers.eq(tempFile.length()), ArgumentMatchers.any(File.class), ArgumentMatchers.eq(properties))).thenReturn(SC_BAD_GATEWAY);
        Mockito.when(httpService.upload(ArgumentMatchers.eq("http://baseurl/artifacts/dest/path?attempt=2&buildId=build42"), ArgumentMatchers.eq(tempFile.length()), ArgumentMatchers.any(File.class), ArgumentMatchers.eq(properties))).thenReturn(SC_BAD_GATEWAY);
        Mockito.when(httpService.upload(ArgumentMatchers.eq("http://baseurl/artifacts/dest/path?attempt=3&buildId=build42"), ArgumentMatchers.eq(tempFile.length()), ArgumentMatchers.any(File.class), ArgumentMatchers.eq(properties))).thenReturn(SC_BAD_GATEWAY);
        try {
            artifactsRepository.upload(console, tempFile, "dest/path", "build42");
            Assert.fail("should have thrown request entity too large error");
        } catch (RuntimeException e) {
            Assert.assertThat(console.output(), printedUploadingFailure(tempFile));
        }
    }

    @Test
    public void shouldUploadArtifactChecksumAlongWithArtifact() throws IOException {
        String data = "Some text whose checksum can be asserted";
        final String md5 = CachedDigestUtils.md5Hex(data);
        FileUtils.writeStringToFile(tempFile, data, StandardCharsets.UTF_8);
        Properties properties = new Properties();
        properties.setProperty("dest/path/file.txt", md5);
        Mockito.when(httpService.upload(ArgumentMatchers.eq("http://baseurl/artifacts/dest/path?attempt=1&buildId=build42"), ArgumentMatchers.eq(tempFile.length()), ArgumentMatchers.any(File.class), ArgumentMatchers.eq(properties))).thenReturn(SC_OK);
        artifactsRepository.upload(console, tempFile, "dest/path", "build42");
    }

    @Test
    public void shouldUploadArtifactChecksumWithRightPathWhenArtifactDestinationPathIsEmpty() throws IOException {
        String data = "Some text whose checksum can be asserted";
        final String md5 = CachedDigestUtils.md5Hex(data);
        FileUtils.writeStringToFile(tempFile, data, StandardCharsets.UTF_8);
        Properties properties = new Properties();
        properties.setProperty("file.txt", md5);
        Mockito.when(httpService.upload(ArgumentMatchers.eq("http://baseurl/artifacts/?attempt=1&buildId=build42"), ArgumentMatchers.eq(tempFile.length()), ArgumentMatchers.any(File.class), ArgumentMatchers.eq(properties))).thenReturn(SC_OK);
        artifactsRepository.upload(console, tempFile, "", "build42");
    }

    @Test
    public void shouldUploadArtifactChecksumForADirectory() throws IOException {
        String data = "Some text whose checksum can be asserted";
        String secondData = "some more";
        FileUtils.writeStringToFile(tempFile, data, StandardCharsets.UTF_8);
        File anotherFile = new File(artifactFolder, "bond/james_bond/another_file");
        FileUtils.writeStringToFile(anotherFile, secondData, StandardCharsets.UTF_8);
        Mockito.when(httpService.upload(ArgumentMatchers.eq("http://baseurl/artifacts/dest?attempt=1&buildId=build42"), ArgumentMatchers.eq(FileUtils.sizeOfDirectory(artifactFolder)), ArgumentMatchers.any(File.class), ArgumentMatchers.eq(expectedProperties(data, secondData)))).thenReturn(SC_OK);
        artifactsRepository.upload(console, artifactFolder, "dest", "build42");
    }

    @Test
    public void setRemoteBuildPropertyShouldEncodePropertyName() throws IOException {
        ArgumentCaptor<String> url = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> value = ArgumentCaptor.forClass(String.class);
        artifactsRepository.setProperty(new Property("fo,o", "bar"));
        Mockito.verify(httpService).postProperty(url.capture(), value.capture());
        Assert.assertThat(value.getValue(), Matchers.is("bar"));
        Assert.assertThat(url.getValue(), Matchers.is("http://baseurl/properties/fo%2Co"));
    }
}

