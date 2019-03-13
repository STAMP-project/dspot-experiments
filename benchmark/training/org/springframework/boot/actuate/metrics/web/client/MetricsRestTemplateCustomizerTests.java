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
package org.springframework.boot.actuate.metrics.web.client;


import HttpMethod.GET;
import MediaType.APPLICATION_JSON;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import java.net.URI;
import java.net.URISyntaxException;
import org.junit.Test;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;


/**
 * Tests for {@link MetricsRestTemplateCustomizer}.
 *
 * @author Jon Schneider
 * @author Brian Clozel
 */
public class MetricsRestTemplateCustomizerTests {
    private MeterRegistry registry;

    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    private MetricsRestTemplateCustomizer customizer;

    @Test
    public void interceptRestTemplate() {
        this.mockServer.expect(MockRestRequestMatchers.requestTo("/test/123")).andExpect(MockRestRequestMatchers.method(GET)).andRespond(MockRestResponseCreators.withSuccess("OK", APPLICATION_JSON));
        String result = this.restTemplate.getForObject("/test/{id}", String.class, 123);
        assertThat(this.registry.find("http.client.requests").meters()).anySatisfy(( m) -> assertThat(m.getId().getTags().stream().map(Tag::getKey)).doesNotContain("bucket"));
        assertThat(this.registry.get("http.client.requests").tags("method", "GET", "uri", "/test/{id}", "status", "200").timer().count()).isEqualTo(1);
        assertThat(result).isEqualTo("OK");
        this.mockServer.verify();
    }

    @Test
    public void avoidDuplicateRegistration() {
        this.customizer.customize(this.restTemplate);
        assertThat(this.restTemplate.getInterceptors()).hasSize(1);
        this.customizer.customize(this.restTemplate);
        assertThat(this.restTemplate.getInterceptors()).hasSize(1);
    }

    @Test
    public void normalizeUriToContainLeadingSlash() {
        this.mockServer.expect(MockRestRequestMatchers.requestTo("/test/123")).andExpect(MockRestRequestMatchers.method(GET)).andRespond(MockRestResponseCreators.withSuccess("OK", APPLICATION_JSON));
        String result = this.restTemplate.getForObject("test/{id}", String.class, 123);
        this.registry.get("http.client.requests").tags("uri", "/test/{id}").timer();
        assertThat(result).isEqualTo("OK");
        this.mockServer.verify();
    }

    @Test
    public void interceptRestTemplateWithUri() throws URISyntaxException {
        this.mockServer.expect(MockRestRequestMatchers.requestTo("http://localhost/test/123")).andExpect(MockRestRequestMatchers.method(GET)).andRespond(MockRestResponseCreators.withSuccess("OK", APPLICATION_JSON));
        String result = this.restTemplate.getForObject(new URI("http://localhost/test/123"), String.class);
        assertThat(result).isEqualTo("OK");
        this.registry.get("http.client.requests").tags("uri", "/test/123").timer();
        this.mockServer.verify();
    }
}

