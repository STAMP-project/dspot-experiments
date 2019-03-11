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


import com.thoughtworks.go.domain.ChecksumFileHandler;
import com.thoughtworks.go.domain.StubGoPublisher;
import com.thoughtworks.go.util.HttpService;
import com.thoughtworks.go.util.TestingClock;
import com.thoughtworks.go.util.URLService;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class FetchArtifactBuilderTest {
    private File zip;

    private List<File> toClean = new ArrayList<>();

    private static final String URL = "http://10.18.7.51:8153/go/remoting/files/cruise/1.0.2341/dev/1/windows-3/cruise-output/console.log";

    private File dest;

    private TestingClock clock;

    private StubGoPublisher publisher;

    private ChecksumFileHandler checksumFileHandler;

    private URLService urlService;

    private DownloadAction downloadAction;

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldUnzipWhenFetchingFolder() throws Exception {
        ChecksumFileHandler checksumFileHandler = Mockito.mock(ChecksumFileHandler.class);
        Mockito.when(checksumFileHandler.handleResult(SC_OK, publisher)).thenReturn(true);
        File destOnAgent = new File("pipelines/cruise/", dest.getPath());
        FetchArtifactBuilder builder = getBuilder(new JobIdentifier("cruise", (-10), "1", "dev", "1", "windows", 1L), "log", dest.getPath(), new DirHandler("log", destOnAgent), checksumFileHandler);
        builder.fetch(new DownloadAction(new FetchArtifactBuilderTest.StubFetchZipHttpService(), publisher, clock), new FetchArtifactBuilderTest.StubURLService());
        assertDownloaded(destOnAgent);
    }

    @Test
    public void shouldSaveFileWhenFetchingFile() throws Exception {
        ChecksumFileHandler checksumFileHandler = Mockito.mock(ChecksumFileHandler.class);
        Mockito.when(checksumFileHandler.handleResult(SC_OK, publisher)).thenReturn(true);
        File artifactOnAgent = new File("pipelines/cruise/a.jar");
        toClean.add(artifactOnAgent);
        FetchArtifactBuilder builder = getBuilder(new JobIdentifier("cruise", (-1), "1", "dev", "1", "windows", 1L), "log", "some where do download", new FileHandler(artifactOnAgent, getSrc()), checksumFileHandler);
        builder.fetch(new DownloadAction(new FetchArtifactBuilderTest.StubFetchZipHttpService(), publisher, clock), new FetchArtifactBuilderTest.StubURLService());
        Assert.assertThat(artifactOnAgent.isFile(), Matchers.is(true));
    }

    @Test
    public void shouldReturnURLWithoutSHA1WhenFileDoesNotExist() throws Exception {
        String src = "cruise-output/console.log";
        File destOnAgent = new File((((("pipelines" + '/') + "cruise") + '/') + (dest)));
        File consolelog = new File(destOnAgent, "console.log");
        consolelog.delete();
        FetchArtifactBuilder builder = getBuilder(new JobIdentifier("foo", (-1), "label-1", "dev", "1", "linux", 1L), src, "lib/a.jar", new FileHandler(consolelog, getSrc()), checksumFileHandler);
        Mockito.when(urlService.baseRemoteURL()).thenReturn("http://foo.bar:8153/go");
        Mockito.when(checksumFileHandler.url("http://foo.bar:8153/go", "foo/label-1/dev/1/linux")).thenReturn("http://foo.bar:8153/go/files/foo/label-1/dev/1/linux/cruise-output/md5.checksum");
        Properties properties = new Properties();
        Mockito.when(checksumFileHandler.getArtifactMd5Checksums()).thenReturn(new ArtifactMd5Checksums(properties));
        builder.fetch(downloadAction, urlService);
        Mockito.verify(downloadAction).perform(ArgumentMatchers.eq("http://foo.bar:8153/go/files/foo/label-1/dev/1/linux/cruise-output/md5.checksum"), ArgumentMatchers.isA(FetchHandler.class));
        Mockito.verify(downloadAction).perform(ArgumentMatchers.eq("http://foo.bar:8153/go/remoting/files/foo/label-1/dev/1/linux/cruise-output/console.log"), ArgumentMatchers.isA(com.thoughtworks.go.domain.FileHandler.class));
        Mockito.verifyNoMoreInteractions(downloadAction);
    }

    @Test
    public void shouldReturnURLWithSHA1WhenFileExists() throws Exception {
        String src = "cruise-output/console.log";
        File destOnAgent = new File((((("pipelines" + '/') + "cruise") + '/') + (dest)));
        File consolelog = new File(destOnAgent, "console.log");
        consolelog.getParentFile().mkdirs();
        consolelog.createNewFile();
        toClean.add(destOnAgent);
        FetchArtifactBuilder builder = getBuilder(new JobIdentifier("foo", (-1), "label-1", "dev", "1", "linux", 1L), src, "lib/a.jar", new FileHandler(consolelog, getSrc()), checksumFileHandler);
        Mockito.when(urlService.baseRemoteURL()).thenReturn("http://foo.bar:8153/go");
        Mockito.when(checksumFileHandler.url("http://foo.bar:8153/go", "foo/label-1/dev/1/linux")).thenReturn("http://foo.bar:8153/go/files/foo/label-1/dev/1/linux/cruise-output/md5.checksum");
        Properties properties = new Properties();
        Mockito.when(checksumFileHandler.getArtifactMd5Checksums()).thenReturn(new ArtifactMd5Checksums(properties));
        builder.fetch(downloadAction, urlService);
        Mockito.verify(downloadAction).perform(ArgumentMatchers.eq("http://foo.bar:8153/go/files/foo/label-1/dev/1/linux/cruise-output/md5.checksum"), ArgumentMatchers.any(FetchHandler.class));
        Mockito.verify(downloadAction).perform(ArgumentMatchers.eq("http://foo.bar:8153/go/remoting/files/foo/label-1/dev/1/linux/cruise-output/console.log?sha1=2jmj7l5rSw0yVb%2FvlWAYkK%2FYBwk%3D"), ArgumentMatchers.any(com.thoughtworks.go.domain.FileHandler.class));
        Mockito.verifyNoMoreInteractions(downloadAction);
    }

    @Test
    public void shouldReturnURLEndsWithDotZipWhenRequestingFolder() throws Exception {
        String src = "cruise-output";
        File destOnAgent = new File("pipelines/cruise/", dest.getPath());
        FetchArtifactBuilder builder = getBuilder(new JobIdentifier("foo", (-1), "label-1", "dev", "1", "linux", 1L), src, "lib/a.jar", new DirHandler(src, destOnAgent), checksumFileHandler);
        Mockito.when(urlService.baseRemoteURL()).thenReturn("http://foo.bar:8153/go");
        Mockito.when(checksumFileHandler.url("http://foo.bar:8153/go", "foo/label-1/dev/1/linux")).thenReturn("http://foo.bar:8153/go/files/foo/label-1/dev/1/linux/cruise-output/md5.checksum");
        Properties properties = new Properties();
        Mockito.when(checksumFileHandler.getArtifactMd5Checksums()).thenReturn(new ArtifactMd5Checksums(properties));
        builder.fetch(downloadAction, urlService);
        Mockito.verify(downloadAction).perform(ArgumentMatchers.eq("http://foo.bar:8153/go/files/foo/label-1/dev/1/linux/cruise-output/md5.checksum"), ArgumentMatchers.isA(FetchHandler.class));
        Mockito.verify(downloadAction).perform(ArgumentMatchers.eq("http://foo.bar:8153/go/remoting/files/foo/label-1/dev/1/linux/cruise-output.zip"), ArgumentMatchers.isA(com.thoughtworks.go.domain.DirHandler.class));
        Mockito.verifyNoMoreInteractions(downloadAction);
    }

    @Test
    public void shouldValidateChecksumOnArtifact() throws Exception {
        Mockito.when(urlService.baseRemoteURL()).thenReturn("http://10.10.1.1/go/files");
        Mockito.when(checksumFileHandler.url("http://10.10.1.1/go/files", "cruise/10/dev/1/windows")).thenReturn("http://10.10.1.1/go/files/cruise/10/dev/1/windows/cruise-output/md5.checksum");
        FetchArtifactBuilder builder = getBuilder(new JobIdentifier("cruise", 10, "1", "dev", "1", "windows", 1L), "log", dest.getPath(), Mockito.mock(FetchHandler.class), checksumFileHandler);
        builder.fetch(downloadAction, urlService);
        Mockito.verify(downloadAction).perform("http://10.10.1.1/go/files/cruise/10/dev/1/windows/cruise-output/md5.checksum", checksumFileHandler);
    }

    @Test
    public void shouldMakeTheFetchHandlerUseTheArtifactMd5Checksum() throws Exception {
        ArtifactMd5Checksums artifactMd5Checksums = Mockito.mock(com.thoughtworks.go.domain.ArtifactMd5Checksums.class);
        Mockito.when(urlService.baseRemoteURL()).thenReturn("http://10.10.1.1/go/files");
        Mockito.when(checksumFileHandler.url("http://10.10.1.1/go/files", "cruise/10/dev/1/windows")).thenReturn("http://10.10.1.1/go/files/cruise/10/dev/1/windows/cruise-output/md5.checksum");
        Mockito.when(checksumFileHandler.getArtifactMd5Checksums()).thenReturn(artifactMd5Checksums);
        FetchHandler fetchHandler = Mockito.mock(FetchHandler.class);
        FetchArtifactBuilder builder = getBuilder(new JobIdentifier("cruise", 10, "1", "dev", "1", "windows", 1L), "log", dest.getPath(), fetchHandler, checksumFileHandler);
        builder.fetch(downloadAction, urlService);
        Mockito.verify(fetchHandler).useArtifactMd5Checksums(artifactMd5Checksums);
    }

    private class StubFetchZipHttpService extends HttpService {
        public int download(String url, FetchHandler handler) throws IOException {
            handler.handle(new FileInputStream(zip));
            return SC_OK;
        }
    }

    private static class StubURLService extends URLService {
        public String baseRemoteURL() {
            return "";
        }
    }
}

