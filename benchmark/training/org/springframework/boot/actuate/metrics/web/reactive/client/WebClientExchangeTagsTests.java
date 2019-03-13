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
package org.springframework.boot.actuate.metrics.web.reactive.client;


import HttpMethod.GET;
import HttpStatus.BAD_GATEWAY;
import HttpStatus.BAD_REQUEST;
import HttpStatus.CONTINUE;
import HttpStatus.MOVED_PERMANENTLY;
import HttpStatus.OK;
import io.micrometer.core.instrument.Tag;
import java.io.IOException;
import java.net.URI;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;


/**
 * Tests for {@link WebClientExchangeTags}.
 *
 * @author Brian Clozel
 * @author Nishant Raut
 */
public class WebClientExchangeTagsTests {
    private static final String URI_TEMPLATE_ATTRIBUTE = (WebClient.class.getName()) + ".uriTemplate";

    private ClientRequest request;

    private ClientResponse response;

    @Test
    public void method() {
        assertThat(WebClientExchangeTags.method(this.request)).isEqualTo(Tag.of("method", "GET"));
    }

    @Test
    public void uriWhenAbsoluteTemplateIsAvailableShouldReturnTemplate() {
        assertThat(WebClientExchangeTags.uri(this.request)).isEqualTo(Tag.of("uri", "/projects/{project}"));
    }

    @Test
    public void uriWhenRelativeTemplateIsAvailableShouldReturnTemplate() {
        this.request = ClientRequest.create(GET, URI.create("http://example.org/projects/spring-boot")).attribute(WebClientExchangeTagsTests.URI_TEMPLATE_ATTRIBUTE, "/projects/{project}").build();
        assertThat(WebClientExchangeTags.uri(this.request)).isEqualTo(Tag.of("uri", "/projects/{project}"));
    }

    @Test
    public void uriWhenTemplateIsMissingShouldReturnPath() {
        this.request = ClientRequest.create(GET, URI.create("http://example.org/projects/spring-boot")).build();
        assertThat(WebClientExchangeTags.uri(this.request)).isEqualTo(Tag.of("uri", "/projects/spring-boot"));
    }

    @Test
    public void clientName() {
        assertThat(WebClientExchangeTags.clientName(this.request)).isEqualTo(Tag.of("clientName", "example.org"));
    }

    @Test
    public void status() {
        assertThat(WebClientExchangeTags.status(this.response)).isEqualTo(Tag.of("status", "200"));
    }

    @Test
    public void statusWhenIOException() {
        assertThat(WebClientExchangeTags.status(new IOException())).isEqualTo(Tag.of("status", "IO_ERROR"));
    }

    @Test
    public void statusWhenClientException() {
        assertThat(WebClientExchangeTags.status(new IllegalArgumentException())).isEqualTo(Tag.of("status", "CLIENT_ERROR"));
    }

    @Test
    public void outcomeTagIsUnknownWhenResponseIsNull() {
        Tag tag = WebClientExchangeTags.outcome(null);
        assertThat(tag.getValue()).isEqualTo("UNKNOWN");
    }

    @Test
    public void outcomeTagIsInformationalWhenResponseIs1xx() {
        BDDMockito.given(this.response.statusCode()).willReturn(CONTINUE);
        Tag tag = WebClientExchangeTags.outcome(this.response);
        assertThat(tag.getValue()).isEqualTo("INFORMATIONAL");
    }

    @Test
    public void outcomeTagIsSuccessWhenResponseIs2xx() {
        BDDMockito.given(this.response.statusCode()).willReturn(OK);
        Tag tag = WebClientExchangeTags.outcome(this.response);
        assertThat(tag.getValue()).isEqualTo("SUCCESS");
    }

    @Test
    public void outcomeTagIsRedirectionWhenResponseIs3xx() {
        BDDMockito.given(this.response.statusCode()).willReturn(MOVED_PERMANENTLY);
        Tag tag = WebClientExchangeTags.outcome(this.response);
        assertThat(tag.getValue()).isEqualTo("REDIRECTION");
    }

    @Test
    public void outcomeTagIsClientErrorWhenResponseIs4xx() {
        BDDMockito.given(this.response.statusCode()).willReturn(BAD_REQUEST);
        Tag tag = WebClientExchangeTags.outcome(this.response);
        assertThat(tag.getValue()).isEqualTo("CLIENT_ERROR");
    }

    @Test
    public void outcomeTagIsServerErrorWhenResponseIs5xx() {
        BDDMockito.given(this.response.statusCode()).willReturn(BAD_GATEWAY);
        Tag tag = WebClientExchangeTags.outcome(this.response);
        assertThat(tag.getValue()).isEqualTo("SERVER_ERROR");
    }

    @Test
    public void outcomeTagIsUnknownWhenResponseStatusIsUnknown() {
        BDDMockito.given(this.response.statusCode()).willThrow(IllegalArgumentException.class);
        Tag tag = WebClientExchangeTags.outcome(this.response);
        assertThat(tag.getValue()).isEqualTo("UNKNOWN");
    }
}

