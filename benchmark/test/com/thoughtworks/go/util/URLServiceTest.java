/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ************************GO-LICENSE-END**********************************
 */
package com.thoughtworks.go.util;


import com.thoughtworks.go.domain.JobIdentifier;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class URLServiceTest {
    private static final String BASE_URL = "http://localhost:9090/go";

    private URLService urlService;

    private JobIdentifier jobIdentifier;

    @Test
    public void shouldReturnRepositoryURLWhenBaseURLIsEndedCorrectly() throws Exception {
        new SystemEnvironment().setProperty("serviceUrl", URLServiceTest.BASE_URL);
        Assert.assertThat(new URLService().getBuildRepositoryURL(), Matchers.is(((URLServiceTest.BASE_URL) + "/remoting/remoteBuildRepository")));
    }

    @Test
    public void shouldReturnRepositoryURLWhenBaseURLIsNotEndedCorrectly() throws Exception {
        new SystemEnvironment().setProperty("serviceUrl", ((URLServiceTest.BASE_URL) + "/"));
        Assert.assertThat(new URLService().getBuildRepositoryURL(), Matchers.is(((URLServiceTest.BASE_URL) + "/remoting/remoteBuildRepository")));
    }

    @Test
    public void propertiesURLShouldGoThroughtSecurityCheck() {
        String url = urlService.getPropertiesUrl(jobIdentifier, "failedcount");
        Assert.assertThat(url, Matchers.endsWith("/remoting/properties/pipelineName/LATEST/stageName/LATEST/buildName/failedcount"));
    }

    @Test
    public void shouldReturnProperDownloadUrl() throws Exception {
        String downloadUrl1 = urlService.getRestfulArtifactUrl(jobIdentifier, "file");
        String downloadUrl2 = urlService.getRestfulArtifactUrl(jobIdentifier, "/file");
        Assert.assertThat(downloadUrl1, Matchers.is("/files/pipelineName/LATEST/stageName/LATEST/buildName/file"));
        Assert.assertThat(downloadUrl1, Matchers.is(downloadUrl2));
    }

    @Test
    public void shouldReturnProperRestfulUrlOfArtifact() throws Exception {
        String downloadUrl1 = urlService.getUploadUrlOfAgent(jobIdentifier, "file");
        String downloadUrl2 = urlService.getUploadUrlOfAgent(jobIdentifier, "/file");
        Assert.assertThat(downloadUrl1, Matchers.endsWith("/files/pipelineName/LATEST/stageName/LATEST/buildName/file?attempt=1&buildId=123"));
        Assert.assertThat(downloadUrl1, Matchers.endsWith(downloadUrl2));
    }

    @Test
    public void shouldReturnRestfulUrlOfAgentWithAttemptCounter() throws Exception {
        String uploadUrl1 = urlService.getUploadUrlOfAgent(jobIdentifier, "file", 1);
        Assert.assertThat(uploadUrl1, Matchers.endsWith("/files/pipelineName/LATEST/stageName/LATEST/buildName/file?attempt=1&buildId=123"));
    }

    @Test
    public void shouldReturnServerUrlWithSubpath() {
        new SystemEnvironment().setProperty("serviceUrl", ((URLServiceTest.BASE_URL) + "/"));
        Assert.assertThat(new URLService().serverUrlFor("someSubPath/xyz"), Matchers.is(((URLServiceTest.BASE_URL) + "/someSubPath/xyz")));
    }

    @Test
    public void agentRemoteWebSocketUrl() {
        Assert.assertThat(urlService.getAgentRemoteWebSocketUrl(), Matchers.is("wss://localhost:8443/go/agent-websocket"));
    }
}

