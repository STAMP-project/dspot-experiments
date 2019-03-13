/**
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.boot.cli.command.init;


import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


/**
 * Tests for {@link InitializrService}
 *
 * @author Stephane Nicoll
 */
public class InitializrServiceTests extends AbstractHttpClientMockTests {
    private final InitializrService invoker = new InitializrService(this.http);

    @Test
    public void loadMetadata() throws Exception {
        mockSuccessfulMetadataGet(false);
        InitializrServiceMetadata metadata = this.invoker.loadMetadata("http://foo/bar");
        assertThat(metadata).isNotNull();
    }

    @Test
    public void generateSimpleProject() throws Exception {
        ProjectGenerationRequest request = new ProjectGenerationRequest();
        AbstractHttpClientMockTests.MockHttpProjectGenerationRequest mockHttpRequest = new AbstractHttpClientMockTests.MockHttpProjectGenerationRequest("application/xml", "foo.zip");
        ProjectGenerationResponse entity = generateProject(request, mockHttpRequest);
        InitializrServiceTests.assertProjectEntity(entity, mockHttpRequest.contentType, mockHttpRequest.fileName);
    }

    @Test
    public void generateProjectCustomTargetFilename() throws Exception {
        ProjectGenerationRequest request = new ProjectGenerationRequest();
        request.setOutput("bar.zip");
        AbstractHttpClientMockTests.MockHttpProjectGenerationRequest mockHttpRequest = new AbstractHttpClientMockTests.MockHttpProjectGenerationRequest("application/xml", null);
        ProjectGenerationResponse entity = generateProject(request, mockHttpRequest);
        InitializrServiceTests.assertProjectEntity(entity, mockHttpRequest.contentType, null);
    }

    @Test
    public void generateProjectNoDefaultFileName() throws Exception {
        ProjectGenerationRequest request = new ProjectGenerationRequest();
        AbstractHttpClientMockTests.MockHttpProjectGenerationRequest mockHttpRequest = new AbstractHttpClientMockTests.MockHttpProjectGenerationRequest("application/xml", null);
        ProjectGenerationResponse entity = generateProject(request, mockHttpRequest);
        InitializrServiceTests.assertProjectEntity(entity, mockHttpRequest.contentType, null);
    }

    @Test
    public void generateProjectBadRequest() throws Exception {
        String jsonMessage = "Unknown dependency foo:bar";
        mockProjectGenerationError(400, jsonMessage);
        ProjectGenerationRequest request = new ProjectGenerationRequest();
        request.getDependencies().add("foo:bar");
        assertThatExceptionOfType(ReportableException.class).isThrownBy(() -> this.invoker.generate(request)).withMessageContaining(jsonMessage);
    }

    @Test
    public void generateProjectBadRequestNoExtraMessage() throws Exception {
        mockProjectGenerationError(400, null);
        ProjectGenerationRequest request = new ProjectGenerationRequest();
        assertThatExceptionOfType(ReportableException.class).isThrownBy(() -> this.invoker.generate(request)).withMessageContaining("unexpected 400 error");
    }

    @Test
    public void generateProjectNoContent() throws Exception {
        mockSuccessfulMetadataGet(false);
        CloseableHttpResponse response = Mockito.mock(CloseableHttpResponse.class);
        mockStatus(response, 500);
        BDDMockito.given(this.http.execute(ArgumentMatchers.isA(HttpGet.class))).willReturn(response);
        ProjectGenerationRequest request = new ProjectGenerationRequest();
        assertThatExceptionOfType(ReportableException.class).isThrownBy(() -> this.invoker.generate(request)).withMessageContaining("No content received from server");
    }

    @Test
    public void loadMetadataBadRequest() throws Exception {
        String jsonMessage = "whatever error on the server";
        mockMetadataGetError(500, jsonMessage);
        ProjectGenerationRequest request = new ProjectGenerationRequest();
        assertThatExceptionOfType(ReportableException.class).isThrownBy(() -> this.invoker.generate(request)).withMessageContaining(jsonMessage);
    }

    @Test
    public void loadMetadataInvalidJson() throws Exception {
        CloseableHttpResponse response = Mockito.mock(CloseableHttpResponse.class);
        mockHttpEntity(response, "Foo-Bar-Not-JSON".getBytes(), "application/json");
        mockStatus(response, 200);
        BDDMockito.given(this.http.execute(ArgumentMatchers.isA(HttpGet.class))).willReturn(response);
        ProjectGenerationRequest request = new ProjectGenerationRequest();
        assertThatExceptionOfType(ReportableException.class).isThrownBy(() -> this.invoker.generate(request)).withMessageContaining("Invalid content received from server");
    }

    @Test
    public void loadMetadataNoContent() throws Exception {
        CloseableHttpResponse response = Mockito.mock(CloseableHttpResponse.class);
        mockStatus(response, 500);
        BDDMockito.given(this.http.execute(ArgumentMatchers.isA(HttpGet.class))).willReturn(response);
        ProjectGenerationRequest request = new ProjectGenerationRequest();
        assertThatExceptionOfType(ReportableException.class).isThrownBy(() -> this.invoker.generate(request)).withMessageContaining("No content received from server");
    }
}

