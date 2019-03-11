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
package com.thoughtworks.go.domain.builder;


import JobResult.Failed;
import JobResult.Passed;
import com.thoughtworks.go.buildsession.BuildSessionBasedTestCase;
import com.thoughtworks.go.util.FileUtil;
import com.thoughtworks.go.util.URLService;
import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class FetchArtifactBuilderBuildCommandTest extends BuildSessionBasedTestCase {
    private File zip;

    @Test
    public void shouldUnzipWhenFetchingFolder() throws Exception {
        httpService.setupDownload(String.format("%s/remoting/files/cruise/1/dev/1/windows/log.zip", new URLService().baseRemoteURL()), zip);
        FetchArtifactBuilder builder = getBuilder(new JobIdentifier("cruise", (-10), "1", "dev", "1", "windows", 1L), "log", "dest", new DirHandler("log", new File("pipelines/cruise/dest")));
        runBuilder(builder, Passed);
        assertDownloaded(new File(sandbox, "pipelines/cruise/dest"));
    }

    @Test
    public void shouldGiveWarningWhenMd5FileNotExists() throws Exception {
        httpService.setupDownload(String.format("%s/remoting/files/cruise/1/dev/1/windows/a.jar", new URLService().baseRemoteURL()), "some content");
        FetchArtifactBuilder builder = getBuilder(new JobIdentifier("cruise", (-1), "1", "dev", "1", "windows", 1L), "a.jar", "foo", new FileHandler(new File("pipelines/cruise/foo/a.jar"), "a.jar"));
        runBuilder(builder, Passed);
        Assert.assertThat(new File(sandbox, "pipelines/cruise/foo/a.jar").isFile(), Matchers.is(true));
        Assert.assertThat(console.output(), Matchers.containsString("[WARN] The md5checksum property file was not found"));
    }

    @Test
    public void shouldFailBuildWhenChecksumNotValidForArtifact() throws Exception {
        httpService.setupDownload(String.format("%s/remoting/files/cruise/1/dev/1/windows/cruise-output/md5.checksum", new URLService().baseRemoteURL()), "a.jar=invalid-checksum");
        httpService.setupDownload(String.format("%s/remoting/files/cruise/1/dev/1/windows/a.jar", new URLService().baseRemoteURL()), "some content");
        FetchArtifactBuilder builder = getBuilder(new JobIdentifier("cruise", (-1), "1", "dev", "1", "windows", 1L), "a.jar", "foo", new FileHandler(new File("pipelines/cruise/foo/a.jar"), "a.jar"));
        runBuilder(builder, Failed);
        Assert.assertThat(console.output(), Matchers.containsString("[ERROR] Verification of the integrity of the artifact [a.jar] failed"));
        Assert.assertThat(new File(sandbox, "pipelines/cruise/foo/a.jar").isFile(), Matchers.is(true));
    }

    @Test
    public void shouldBuildWhenChecksumValidForArtifact() throws Exception {
        httpService.setupDownload(String.format("%s/remoting/files/cruise/1/dev/1/windows/cruise-output/md5.checksum", new URLService().baseRemoteURL()), "a.jar=9893532233caff98cd083a116b013c0b");
        httpService.setupDownload(String.format("%s/remoting/files/cruise/1/dev/1/windows/a.jar", new URLService().baseRemoteURL()), "some content");
        FetchArtifactBuilder builder = getBuilder(new JobIdentifier("cruise", (-1), "1", "dev", "1", "windows", 1L), "a.jar", "foo", new FileHandler(new File("pipelines/cruise/foo/a.jar"), "a.jar"));
        runBuilder(builder, Passed);
        Assert.assertThat(console.output(), Matchers.containsString(String.format("Saved artifact to [%s] after verifying the integrity of its contents", new File(sandbox, "pipelines/cruise/foo/a.jar").getPath())));
    }

    @Test
    public void shouldFailBuildAndPrintErrorMessageToConsoleWhenArtifactNotExisit() throws Exception {
        FetchArtifactBuilder builder = getBuilder(new JobIdentifier("cruise", (-1), "1", "dev", "1", "windows", 1L), "a.jar", "foo", new FileHandler(new File("pipelines/cruise/foo/a.jar"), "a.jar"));
        runBuilder(builder, Failed);
        Assert.assertThat(console.output(), Matchers.not(Matchers.containsString("Saved artifact")));
        Assert.assertThat(console.output(), Matchers.containsString("Could not fetch artifact"));
    }

    @Test
    public void shouldDownloadWithURLContainsSHA1WhenFileExists() throws Exception {
        File artifactOnAgent = new File(sandbox, "pipelines/cruise/foo/a.jar");
        new File(sandbox, "pipelines/cruise/foo").mkdirs();
        FileUtils.writeStringToFile(artifactOnAgent, "foobar", StandardCharsets.UTF_8);
        String sha1 = URLEncoder.encode(FileUtil.sha1Digest(artifactOnAgent), "UTF-8");
        httpService.setupDownload(String.format("%s/remoting/files/cruise/1/dev/1/windows/a.jar", new URLService().baseRemoteURL()), "content for url without sha1");
        httpService.setupDownload(String.format("%s/remoting/files/cruise/1/dev/1/windows/a.jar?sha1=%s", new URLService().baseRemoteURL(), sha1), "content for url with sha1");
        FetchArtifactBuilder builder = getBuilder(new JobIdentifier("cruise", (-1), "1", "dev", "1", "windows", 1L), "a.jar", "foo", new FileHandler(new File("pipelines/cruise/foo/a.jar"), "a.jar"));
        runBuilder(builder, Passed);
        Assert.assertThat(artifactOnAgent.isFile(), Matchers.is(true));
        Assert.assertThat(FileUtils.readFileToString(artifactOnAgent, StandardCharsets.UTF_8), Matchers.is("content for url with sha1"));
    }
}

