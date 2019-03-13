/**
 * Copyright 2017 Pivotal Software, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micrometer.spring.web.client;


import io.micrometer.core.instrument.Tag;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpRequest;


@RunWith(MockitoJUnitRunner.class)
public class DefaultRestTemplateExchangeTagsProviderTest {
    private DefaultRestTemplateExchangeTagsProvider tagsProvider = new DefaultRestTemplateExchangeTagsProvider();

    private MockClientHttpRequest httpRequest = new MockClientHttpRequest();

    private ClientHttpResponse httpResponse = new org.springframework.mock.http.client.MockClientHttpResponse(new byte[]{  }, HttpStatus.OK);

    @Test
    public void uriTagSetFromUriTemplate() {
        Iterable<Tag> tags = tagsProvider.getTags("/test/{id}", httpRequest, httpResponse);
        assertThat(tags).contains(Tag.of("uri", "/test/{id}"));
    }

    @Test
    public void uriTagSetFromRequestWhenNoUriTemplate() {
        Iterable<Tag> tags = tagsProvider.getTags(null, httpRequest, httpResponse);
        assertThat(tags).contains(Tag.of("uri", "/test/123"));
    }

    @Test
    public void uriTagSetFromRequestWhenUriTemplateIsBlank() {
        Iterable<Tag> tags = tagsProvider.getTags(" ", httpRequest, httpResponse);
        assertThat(tags).contains(Tag.of("uri", "/test/123"));
    }

    @Test
    public void outcomeTagInformational() {
        httpResponse = new org.springframework.mock.http.client.MockClientHttpResponse(new byte[]{  }, HttpStatus.CONTINUE);
        Iterable<Tag> tags = tagsProvider.getTags(" ", httpRequest, httpResponse);
        assertThat(tags).contains(Tag.of("outcome", "INFORMATIONAL"));
    }

    @Test
    public void outcomeTagSuccess() {
        httpResponse = new org.springframework.mock.http.client.MockClientHttpResponse(new byte[]{  }, HttpStatus.OK);
        Iterable<Tag> tags = tagsProvider.getTags(" ", httpRequest, httpResponse);
        assertThat(tags).contains(Tag.of("outcome", "SUCCESS"));
    }

    @Test
    public void outcomeTagRedirection() {
        httpResponse = new org.springframework.mock.http.client.MockClientHttpResponse(new byte[]{  }, HttpStatus.PERMANENT_REDIRECT);
        Iterable<Tag> tags = tagsProvider.getTags(" ", httpRequest, httpResponse);
        assertThat(tags).contains(Tag.of("outcome", "REDIRECTION"));
    }

    @Test
    public void outcomeTagClientError() {
        httpResponse = new org.springframework.mock.http.client.MockClientHttpResponse(new byte[]{  }, HttpStatus.BAD_REQUEST);
        Iterable<Tag> tags = tagsProvider.getTags(" ", httpRequest, httpResponse);
        assertThat(tags).contains(Tag.of("outcome", "CLIENT_ERROR"));
    }

    @Test
    public void outcomeTagServerError() {
        httpResponse = new org.springframework.mock.http.client.MockClientHttpResponse(new byte[]{  }, HttpStatus.INTERNAL_SERVER_ERROR);
        Iterable<Tag> tags = tagsProvider.getTags(" ", httpRequest, httpResponse);
        assertThat(tags).contains(Tag.of("outcome", "SERVER_ERROR"));
    }

    @Test
    public void outcomeTagUnknownStatusCode() throws IOException {
        ClientHttpResponse clientHttpResponse = Mockito.mock(ClientHttpResponse.class);
        Mockito.when(clientHttpResponse.getStatusCode()).thenThrow(new IllegalArgumentException());
        Iterable<Tag> tags = tagsProvider.getTags(" ", httpRequest, clientHttpResponse);
        assertThat(tags).contains(Tag.of("outcome", "UNKNOWN"));
    }

    @Test
    public void outcomeTagIOException() throws IOException {
        ClientHttpResponse clientHttpResponse = Mockito.mock(ClientHttpResponse.class);
        Mockito.when(clientHttpResponse.getStatusCode()).thenThrow(new IOException());
        Iterable<Tag> tags = tagsProvider.getTags(" ", httpRequest, clientHttpResponse);
        assertThat(tags).contains(Tag.of("outcome", "UNKNOWN"));
    }

    @Test
    public void outcomeTagNullResponse() {
        Iterable<Tag> tags = tagsProvider.getTags(" ", httpRequest, null);
        assertThat(tags).contains(Tag.of("outcome", "UNKNOWN"));
    }
}

