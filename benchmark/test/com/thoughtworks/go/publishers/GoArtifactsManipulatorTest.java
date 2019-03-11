/**
 * Copyright 2018 ThoughtWorks, Inc.
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
package com.thoughtworks.go.publishers;


import HttpServletResponse.SC_OK;
import HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE;
import com.thoughtworks.go.domain.JobIdentifier;
import com.thoughtworks.go.remote.work.GoArtifactsManipulatorStub;
import com.thoughtworks.go.util.CachedDigestUtils;
import com.thoughtworks.go.util.HttpService;
import com.thoughtworks.go.util.ReflectionUtil;
import com.thoughtworks.go.work.DefaultGoPublisher;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class GoArtifactsManipulatorTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private HttpService httpService;

    private File tempFile;

    private GoArtifactsManipulatorStub goArtifactsManipulatorStub;

    private JobIdentifier jobIdentifier;

    private DefaultGoPublisher goPublisher;

    private File artifactFolder;

    @Test
    public void shouldBombWithErrorWhenStatusCodeReturnedIsRequestEntityTooLarge() throws IOException, InterruptedException {
        Mockito.when(httpService.upload(ArgumentMatchers.any(String.class), ArgumentMatchers.eq(tempFile.length()), ArgumentMatchers.any(File.class), ArgumentMatchers.any(Properties.class))).thenReturn(SC_REQUEST_ENTITY_TOO_LARGE);
        CircularFifoQueue buffer = ((CircularFifoQueue) (ReflectionUtil.getField(ReflectionUtil.getField(goPublisher, "consoleOutputTransmitter"), "buffer")));
        synchronized(buffer) {
            try {
                goArtifactsManipulatorStub.publish(goPublisher, "some_dest", tempFile, jobIdentifier);
                Assert.fail("should have thrown request entity too large error");
            } catch (RuntimeException e) {
                String expectedMessage = ((("Artifact upload for file " + (tempFile.getAbsolutePath())) + " (Size: ") + (tempFile.length())) + ") was denied by the server. This usually happens when server runs out of disk space.";
                Assert.assertThat(e.getMessage(), Matchers.is((("java.lang.RuntimeException: " + expectedMessage) + ".  HTTP return code is 413")));
                Assert.assertThat(buffer.toString().contains(expectedMessage), Matchers.is(true));
            }
        }
    }

    @Test
    public void uploadShouldBeGivenFileSize() throws IOException {
        Mockito.when(httpService.upload(ArgumentMatchers.any(String.class), ArgumentMatchers.eq(tempFile.length()), ArgumentMatchers.any(File.class), ArgumentMatchers.any(Properties.class))).thenReturn(SC_REQUEST_ENTITY_TOO_LARGE);
        try {
            goArtifactsManipulatorStub.publish(goPublisher, "dest", tempFile, jobIdentifier);
            Assert.fail("should have thrown request entity too large error");
        } catch (RuntimeException e) {
            Mockito.verify(httpService).upload(ArgumentMatchers.any(String.class), ArgumentMatchers.eq(tempFile.length()), ArgumentMatchers.any(File.class), ArgumentMatchers.any(Properties.class));
        }
    }

    @Test
    public void shouldUploadArtifactChecksumAlongWithArtifact() throws IOException {
        String data = "Some text whose checksum can be asserted";
        final String md5 = CachedDigestUtils.md5Hex(data);
        FileUtils.writeStringToFile(tempFile, data, StandardCharsets.UTF_8);
        Properties properties = new Properties();
        properties.setProperty("dest/path/file.txt", md5);
        Mockito.when(httpService.upload(ArgumentMatchers.any(String.class), ArgumentMatchers.eq(tempFile.length()), ArgumentMatchers.any(File.class), ArgumentMatchers.eq(properties))).thenReturn(SC_OK);
        goArtifactsManipulatorStub.publish(goPublisher, "/dest/path", tempFile, jobIdentifier);
    }

    @Test
    public void shouldUploadArtifactChecksumWithRightPathWhenArtifactDestinationPathIsEmpty() throws IOException {
        String data = "Some text whose checksum can be asserted";
        final String md5 = CachedDigestUtils.md5Hex(data);
        FileUtils.writeStringToFile(tempFile, data, StandardCharsets.UTF_8);
        Properties properties = new Properties();
        properties.setProperty("file.txt", md5);
        Mockito.when(httpService.upload(ArgumentMatchers.any(String.class), ArgumentMatchers.eq(tempFile.length()), ArgumentMatchers.any(File.class), ArgumentMatchers.eq(properties))).thenReturn(SC_OK);
        goArtifactsManipulatorStub.publish(goPublisher, "", tempFile, jobIdentifier);
    }

    @Test
    public void shouldUploadArtifactChecksumForADirectory() throws IOException {
        String data = "Some text whose checksum can be asserted";
        String secondData = "some more";
        FileUtils.writeStringToFile(tempFile, data, StandardCharsets.UTF_8);
        File anotherFile = new File(artifactFolder, "bond/james_bond/another_file");
        FileUtils.writeStringToFile(anotherFile, secondData, StandardCharsets.UTF_8);
        Mockito.when(httpService.upload(ArgumentMatchers.any(String.class), ArgumentMatchers.eq(FileUtils.sizeOfDirectory(artifactFolder)), ArgumentMatchers.any(File.class), ArgumentMatchers.eq(expectedProperties(data, secondData)))).thenReturn(SC_OK);
        goArtifactsManipulatorStub.publish(goPublisher, "dest", artifactFolder, jobIdentifier);
    }
}

