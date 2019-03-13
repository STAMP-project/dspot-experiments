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
package org.springframework.boot.actuate.endpoint.web.servlet;


import HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE;
import io.micrometer.core.instrument.Tag;
import org.junit.Test;
import org.springframework.boot.actuate.metrics.web.servlet.WebMvcTags;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.pattern.PathPatternParser;


/**
 * Tests for {@link WebMvcTags}.
 *
 * @author Andy Wilkinson
 * @author Brian Clozel
 * @author Michael McFadyen
 */
public class WebMvcTagsTests {
    private final MockHttpServletRequest request = new MockHttpServletRequest();

    private final MockHttpServletResponse response = new MockHttpServletResponse();

    @Test
    public void uriTagIsDataRestsEffectiveRepositoryLookupPathWhenAvailable() {
        this.request.setAttribute("org.springframework.data.rest.webmvc.RepositoryRestHandlerMapping.EFFECTIVE_REPOSITORY_RESOURCE_LOOKUP_PATH", new PathPatternParser().parse("/api/cities"));
        this.request.setAttribute(BEST_MATCHING_PATTERN_ATTRIBUTE, "/api/{repository}");
        Tag tag = WebMvcTags.uri(this.request, this.response);
        assertThat(tag.getValue()).isEqualTo("/api/cities");
    }

    @Test
    public void uriTagValueIsBestMatchingPatternWhenAvailable() {
        this.request.setAttribute(BEST_MATCHING_PATTERN_ATTRIBUTE, "/spring");
        this.response.setStatus(301);
        Tag tag = WebMvcTags.uri(this.request, this.response);
        assertThat(tag.getValue()).isEqualTo("/spring");
    }

    @Test
    public void uriTagValueIsRootWhenRequestHasNoPatternOrPathInfo() {
        assertThat(WebMvcTags.uri(this.request, null).getValue()).isEqualTo("root");
    }

    @Test
    public void uriTagValueIsRootWhenRequestHasNoPatternAndSlashPathInfo() {
        this.request.setPathInfo("/");
        assertThat(WebMvcTags.uri(this.request, null).getValue()).isEqualTo("root");
    }

    @Test
    public void uriTagValueIsUnknownWhenRequestHasNoPatternAndNonRootPathInfo() {
        this.request.setPathInfo("/example");
        assertThat(WebMvcTags.uri(this.request, null).getValue()).isEqualTo("UNKNOWN");
    }

    @Test
    public void uriTagValueIsRedirectionWhenResponseStatusIs3xx() {
        this.response.setStatus(301);
        Tag tag = WebMvcTags.uri(this.request, this.response);
        assertThat(tag.getValue()).isEqualTo("REDIRECTION");
    }

    @Test
    public void uriTagValueIsNotFoundWhenResponseStatusIs404() {
        this.response.setStatus(404);
        Tag tag = WebMvcTags.uri(this.request, this.response);
        assertThat(tag.getValue()).isEqualTo("NOT_FOUND");
    }

    @Test
    public void uriTagToleratesCustomResponseStatus() {
        this.response.setStatus(601);
        Tag tag = WebMvcTags.uri(this.request, this.response);
        assertThat(tag.getValue()).isEqualTo("root");
    }

    @Test
    public void uriTagIsUnknownWhenRequestIsNull() {
        Tag tag = WebMvcTags.uri(null, null);
        assertThat(tag.getValue()).isEqualTo("UNKNOWN");
    }

    @Test
    public void outcomeTagIsUnknownWhenResponseIsNull() {
        Tag tag = WebMvcTags.outcome(null);
        assertThat(tag.getValue()).isEqualTo("UNKNOWN");
    }

    @Test
    public void outcomeTagIsInformationalWhenResponseIs1xx() {
        this.response.setStatus(100);
        Tag tag = WebMvcTags.outcome(this.response);
        assertThat(tag.getValue()).isEqualTo("INFORMATIONAL");
    }

    @Test
    public void outcomeTagIsSuccessWhenResponseIs2xx() {
        this.response.setStatus(200);
        Tag tag = WebMvcTags.outcome(this.response);
        assertThat(tag.getValue()).isEqualTo("SUCCESS");
    }

    @Test
    public void outcomeTagIsRedirectionWhenResponseIs3xx() {
        this.response.setStatus(301);
        Tag tag = WebMvcTags.outcome(this.response);
        assertThat(tag.getValue()).isEqualTo("REDIRECTION");
    }

    @Test
    public void outcomeTagIsClientErrorWhenResponseIs4xx() {
        this.response.setStatus(400);
        Tag tag = WebMvcTags.outcome(this.response);
        assertThat(tag.getValue()).isEqualTo("CLIENT_ERROR");
    }

    @Test
    public void outcomeTagIsServerErrorWhenResponseIs5xx() {
        this.response.setStatus(500);
        Tag tag = WebMvcTags.outcome(this.response);
        assertThat(tag.getValue()).isEqualTo("SERVER_ERROR");
    }
}

