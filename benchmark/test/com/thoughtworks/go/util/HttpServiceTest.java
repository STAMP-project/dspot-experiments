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


import HttpService.HttpClientFactory;
import com.thoughtworks.go.agent.common.ssl.GoAgentServerHttpClient;
import com.thoughtworks.go.domain.FetchHandler;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpVersion;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.BasicStatusLine;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


public class HttpServiceTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static final String NOT_EXIST_URL = "http://bjcruiselablablab";

    private File folderToSaveDowloadFiles;

    private HttpService service;

    private HttpClientFactory httpClientFactory;

    private GoAgentServerHttpClient httpClient;

    @Test
    public void shouldPostArtifactsAlongWithMD5() throws IOException {
        File uploadingFile = Mockito.mock(File.class);
        Properties checksums = new Properties();
        String uploadUrl = "url";
        HttpPost mockPostMethod = Mockito.mock(HttpPost.class);
        CloseableHttpResponse response = Mockito.mock(CloseableHttpResponse.class);
        Mockito.when(response.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, 200, "OK"));
        Mockito.when(httpClient.execute(mockPostMethod)).thenReturn(response);
        Mockito.when(uploadingFile.exists()).thenReturn(true);
        Mockito.when(httpClientFactory.createPost(uploadUrl)).thenReturn(mockPostMethod);
        service.upload(uploadUrl, 100L, uploadingFile, checksums);
        Mockito.verify(mockPostMethod).setHeader(HttpService.GO_ARTIFACT_PAYLOAD_SIZE, "100");
        Mockito.verify(mockPostMethod).setHeader("Confirm", "true");
        Mockito.verify(httpClientFactory).createMultipartRequestEntity(uploadingFile, checksums);
        Mockito.verify(httpClient).execute(mockPostMethod);
    }

    @Test
    public void shouldDownloadArtifact() throws IOException {
        String url = "http://blah";
        FetchHandler fetchHandler = Mockito.mock(FetchHandler.class);
        HttpGet mockGetMethod = Mockito.mock(HttpGet.class);
        CloseableHttpResponse response = Mockito.mock(CloseableHttpResponse.class);
        Mockito.when(response.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, 200, "OK"));
        Mockito.when(httpClient.execute(mockGetMethod)).thenReturn(response);
        Mockito.when(httpClientFactory.createGet(url)).thenReturn(mockGetMethod);
        service.download(url, fetchHandler);
        Mockito.verify(httpClient).execute(mockGetMethod);
        Mockito.verify(fetchHandler).handle(null);
    }

    @Test
    public void shouldNotFailIfChecksumFileIsNotPresent() throws IOException {
        HttpService.HttpClientFactory factory = new HttpService.HttpClientFactory(null);
        File artifact = new File(folderToSaveDowloadFiles, "artifact");
        artifact.createNewFile();
        try {
            factory.createMultipartRequestEntity(artifact, null);
        } catch (FileNotFoundException e) {
            Assert.fail("Nulitpart should be created even in the absence of checksum file");
        }
    }

    @Test
    public void shouldSetTheAcceptHeaderWhilePostingProperties() throws Exception {
        HttpPost post = Mockito.mock(HttpPost.class);
        Mockito.when(httpClientFactory.createPost("url")).thenReturn(post);
        CloseableHttpResponse response = Mockito.mock(CloseableHttpResponse.class);
        Mockito.when(response.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, 200, "OK"));
        Mockito.when(httpClient.execute(post)).thenReturn(response);
        ArgumentCaptor<UrlEncodedFormEntity> entityCaptor = ArgumentCaptor.forClass(UrlEncodedFormEntity.class);
        service.postProperty("url", "value");
        Mockito.verify(post).setHeader("Confirm", "true");
        Mockito.verify(post).setEntity(entityCaptor.capture());
        UrlEncodedFormEntity expected = new UrlEncodedFormEntity(Arrays.asList(new BasicNameValuePair("value", "value")));
        UrlEncodedFormEntity actual = entityCaptor.getValue();
        Assert.assertEquals(IOUtils.toString(expected.getContent()), IOUtils.toString(actual.getContent()));
        Assert.assertEquals(expected.getContentLength(), expected.getContentLength());
        Assert.assertEquals(expected.getContentType(), expected.getContentType());
        Assert.assertEquals(expected.getContentEncoding(), expected.getContentEncoding());
        Assert.assertEquals(expected.isChunked(), expected.isChunked());
    }

    @Test
    public void shouldCreateMultipleRequestWithChecksumValues() throws IOException {
        HttpService.HttpClientFactory factory = new HttpService.HttpClientFactory(null);
        File artifact = new File(folderToSaveDowloadFiles, "artifact");
        artifact.createNewFile();
        try {
            Properties artifactChecksums = new Properties();
            artifactChecksums.setProperty("foo.txt", "323233333");
            factory.createMultipartRequestEntity(artifact, artifactChecksums);
        } catch (FileNotFoundException e) {
            Assert.fail("Nulitpart should be created even in the absence of checksum file");
        }
    }
}

