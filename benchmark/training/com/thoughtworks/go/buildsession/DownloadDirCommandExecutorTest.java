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
import com.thoughtworks.go.util.MapBuilder;
import com.thoughtworks.go.util.ZipUtil;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.Deflater;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class DownloadDirCommandExecutorTest extends BuildSessionBasedTestCase {
    @Test
    public void downloadDirWithChecksum() throws Exception {
        File folder = temporaryFolder.newFolder("log");
        Files.write(Paths.get(folder.getPath(), "a"), "content for a".getBytes());
        Files.write(Paths.get(folder.getPath(), "b"), "content for b".getBytes());
        File zip = new ZipUtil().zip(folder, temporaryFolder.newFile("log.zip"), Deflater.NO_COMPRESSION);
        httpService.setupDownload("http://far.far.away/log.zip", zip);
        httpService.setupDownload("http://far.far.away/log.zip.md5", "s/log/a=524ebd45bd7de3616317127f6e639bd6\ns/log/b=83c0aa3048df233340203c74e8a93d7d");
        runBuild(BuildCommand.downloadDir(MapBuilder.map("url", "http://far.far.away/log.zip", "dest", "dest", "src", "s/log", "checksumUrl", "http://far.far.away/log.zip.md5")), JobResult.Passed);
        File dest = new File(sandbox, "dest");
        Assert.assertThat(console.output(), Matchers.containsString(String.format("Saved artifact to [%s] after verifying the integrity of its contents", dest.getPath())));
        Assert.assertThat(FileUtils.readFileToString(new File(dest, "log/a"), StandardCharsets.UTF_8), Matchers.is("content for a"));
        Assert.assertThat(FileUtils.readFileToString(new File(dest, "log/b"), StandardCharsets.UTF_8), Matchers.is("content for b"));
    }
}

