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
package com.thoughtworks.go.buildsession;


import com.thoughtworks.go.domain.BuildCommand;
import com.thoughtworks.go.domain.JobResult;
import com.thoughtworks.go.util.FileUtil;
import com.thoughtworks.go.util.MapBuilder;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class DownloadFileCommandExecutorTest extends BuildSessionBasedTestCase {
    @Test
    public void downloadFilePrintErrorWhenFailed() {
        runBuild(BuildCommand.downloadFile(MapBuilder.map("url", "http://far.far.away/foo.jar", "dest", new File(sandbox, "bar.jar").getPath())), JobResult.Failed);
        Assert.assertThat(console.output(), Matchers.containsString("Could not fetch artifact"));
    }

    @Test
    public void downloadFileWithoutMD5Check() throws IOException {
        File dest = new File(sandbox, "bar.jar");
        httpService.setupDownload("http://far.far.away/foo.jar", "some content");
        runBuild(BuildCommand.downloadFile(MapBuilder.map("url", "http://far.far.away/foo.jar", "dest", "bar.jar")), JobResult.Passed);
        Assert.assertThat(console.output(), Matchers.containsString("without verifying the integrity"));
        Assert.assertThat(FileUtils.readFileToString(dest, StandardCharsets.UTF_8), Matchers.is("some content"));
    }

    @Test
    public void downloadFileWithMD5Check() throws IOException {
        httpService.setupDownload("http://far.far.away/foo.jar", "some content");
        httpService.setupDownload("http://far.far.away/foo.jar.md5", "foo.jar=9893532233caff98cd083a116b013c0b");
        runBuild(BuildCommand.downloadFile(MapBuilder.map("url", "http://far.far.away/foo.jar", "dest", "dest.jar", "src", "foo.jar", "checksumUrl", "http://far.far.away/foo.jar.md5")), JobResult.Passed);
        Assert.assertThat(console.output(), Matchers.containsString(String.format("Saved artifact to [%s] after verifying the integrity of its contents", new File(sandbox, "dest.jar").getPath())));
        Assert.assertThat(FileUtils.readFileToString(new File(sandbox, "dest.jar"), StandardCharsets.UTF_8), Matchers.is("some content"));
    }

    @Test
    public void downloadFileShouldAppendSha1IntoDownloadUrlIfDestFileAlreadyExists() throws IOException {
        File dest = new File(sandbox, "bar.jar");
        Files.write(Paths.get(dest.getPath()), "foobar".getBytes());
        String sha1 = URLEncoder.encode(FileUtil.sha1Digest(dest), "UTF-8");
        httpService.setupDownload("http://far.far.away/foo.jar", "content without sha1");
        httpService.setupDownload(("http://far.far.away/foo.jar?sha1=" + sha1), "content with sha1");
        runBuild(BuildCommand.downloadFile(MapBuilder.map("url", "http://far.far.away/foo.jar", "dest", "bar.jar")), JobResult.Passed);
        Assert.assertThat(console.output(), Matchers.containsString("Saved artifact"));
        Assert.assertThat(FileUtils.readFileToString(dest, StandardCharsets.UTF_8), Matchers.is("content with sha1"));
    }
}

