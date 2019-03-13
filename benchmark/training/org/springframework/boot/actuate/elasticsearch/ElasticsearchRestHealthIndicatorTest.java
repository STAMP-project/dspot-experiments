/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.boot.actuate.elasticsearch;


import Status.DOWN;
import Status.OUT_OF_SERVICE;
import Status.UP;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.http.StatusLine;
import org.apache.http.entity.BasicHttpEntity;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.actuate.health.Health;


/**
 * Tests for {@link ElasticsearchRestHealthIndicator}.
 *
 * @author Artsiom Yudovin
 * @author Filip Hrisafov
 */
public class ElasticsearchRestHealthIndicatorTest {
    private final RestClient restClient = Mockito.mock(RestClient.class);

    private final ElasticsearchRestHealthIndicator elasticsearchRestHealthIndicator = new ElasticsearchRestHealthIndicator(this.restClient);

    @Test
    public void elasticsearchIsUp() throws IOException {
        BasicHttpEntity httpEntity = new BasicHttpEntity();
        httpEntity.setContent(new ByteArrayInputStream(createJsonResult(200, "green").getBytes()));
        Response response = Mockito.mock(Response.class);
        StatusLine statusLine = Mockito.mock(StatusLine.class);
        BDDMockito.given(statusLine.getStatusCode()).willReturn(200);
        BDDMockito.given(response.getStatusLine()).willReturn(statusLine);
        BDDMockito.given(response.getEntity()).willReturn(httpEntity);
        BDDMockito.given(this.restClient.performRequest(ArgumentMatchers.any(Request.class))).willReturn(response);
        Health health = this.elasticsearchRestHealthIndicator.health();
        assertThat(health.getStatus()).isEqualTo(UP);
        assertHealthDetailsWithStatus(health.getDetails(), "green");
    }

    @Test
    public void elasticsearchWithYellowStatusIsUp() throws IOException {
        BasicHttpEntity httpEntity = new BasicHttpEntity();
        httpEntity.setContent(new ByteArrayInputStream(createJsonResult(200, "yellow").getBytes()));
        Response response = Mockito.mock(Response.class);
        StatusLine statusLine = Mockito.mock(StatusLine.class);
        BDDMockito.given(statusLine.getStatusCode()).willReturn(200);
        BDDMockito.given(response.getStatusLine()).willReturn(statusLine);
        BDDMockito.given(response.getEntity()).willReturn(httpEntity);
        BDDMockito.given(this.restClient.performRequest(ArgumentMatchers.any(Request.class))).willReturn(response);
        Health health = this.elasticsearchRestHealthIndicator.health();
        assertThat(health.getStatus()).isEqualTo(UP);
        assertHealthDetailsWithStatus(health.getDetails(), "yellow");
    }

    @Test
    public void elasticsearchIsDown() throws IOException {
        BDDMockito.given(this.restClient.performRequest(ArgumentMatchers.any(Request.class))).willThrow(new IOException("Couldn't connect"));
        Health health = this.elasticsearchRestHealthIndicator.health();
        assertThat(health.getStatus()).isEqualTo(DOWN);
        assertThat(health.getDetails()).contains(entry("error", "java.io.IOException: Couldn't connect"));
    }

    @Test
    public void elasticsearchIsDownByResponseCode() throws IOException {
        Response response = Mockito.mock(Response.class);
        StatusLine statusLine = Mockito.mock(StatusLine.class);
        BDDMockito.given(statusLine.getStatusCode()).willReturn(500);
        BDDMockito.given(statusLine.getReasonPhrase()).willReturn("Internal server error");
        BDDMockito.given(response.getStatusLine()).willReturn(statusLine);
        BDDMockito.given(this.restClient.performRequest(ArgumentMatchers.any(Request.class))).willReturn(response);
        Health health = this.elasticsearchRestHealthIndicator.health();
        assertThat(health.getStatus()).isEqualTo(DOWN);
        assertThat(health.getDetails()).contains(entry("statusCode", 500), entry("reasonPhrase", "Internal server error"));
    }

    @Test
    public void elasticsearchIsOutOfServiceByStatus() throws IOException {
        BasicHttpEntity httpEntity = new BasicHttpEntity();
        httpEntity.setContent(new ByteArrayInputStream(createJsonResult(200, "red").getBytes()));
        Response response = Mockito.mock(Response.class);
        StatusLine statusLine = Mockito.mock(StatusLine.class);
        BDDMockito.given(statusLine.getStatusCode()).willReturn(200);
        BDDMockito.given(response.getStatusLine()).willReturn(statusLine);
        BDDMockito.given(response.getEntity()).willReturn(httpEntity);
        BDDMockito.given(this.restClient.performRequest(ArgumentMatchers.any(Request.class))).willReturn(response);
        Health health = this.elasticsearchRestHealthIndicator.health();
        assertThat(health.getStatus()).isEqualTo(OUT_OF_SERVICE);
        assertHealthDetailsWithStatus(health.getDetails(), "red");
    }
}

