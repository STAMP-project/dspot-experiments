/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.web.reactive.result.method.annotation;


import HttpHeaders.ACCEPT;
import HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS;
import HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD;
import HttpHeaders.ORIGIN;
import HttpHeaders.VARY;
import HttpMethod.GET;
import HttpMethod.HEAD;
import HttpMethod.POST;
import HttpStatus.FORBIDDEN;
import HttpStatus.OK;
import MediaType.TEXT_HTML_VALUE;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurationSupport;


/**
 * Integration tests with {@code @RequestMapping} handler methods and global
 * CORS configuration.
 *
 * @author Sebastien Deleuze
 * @author Rossen Stoyanchev
 */
public class GlobalCorsConfigIntegrationTests extends AbstractRequestMappingIntegrationTests {
    private HttpHeaders headers;

    @Test
    public void actualRequestWithCorsEnabled() throws Exception {
        ResponseEntity<String> entity = performGet("/cors", this.headers, String.class);
        Assert.assertEquals(OK, entity.getStatusCode());
        Assert.assertEquals("*", entity.getHeaders().getAccessControlAllowOrigin());
        Assert.assertEquals("cors", entity.getBody());
    }

    @Test
    public void actualRequestWithCorsRejected() throws Exception {
        try {
            performGet("/cors-restricted", this.headers, String.class);
            Assert.fail();
        } catch (HttpClientErrorException e) {
            Assert.assertEquals(FORBIDDEN, e.getStatusCode());
        }
    }

    @Test
    public void actualRequestWithoutCorsEnabled() throws Exception {
        ResponseEntity<String> entity = performGet("/welcome", this.headers, String.class);
        Assert.assertEquals(OK, entity.getStatusCode());
        Assert.assertNull(entity.getHeaders().getAccessControlAllowOrigin());
        Assert.assertEquals("welcome", entity.getBody());
    }

    @Test
    public void actualRequestWithAmbiguousMapping() throws Exception {
        this.headers.add(ACCEPT, TEXT_HTML_VALUE);
        ResponseEntity<String> entity = performGet("/ambiguous", this.headers, String.class);
        Assert.assertEquals(OK, entity.getStatusCode());
        Assert.assertEquals("*", entity.getHeaders().getAccessControlAllowOrigin());
    }

    @Test
    public void preFlightRequestWithCorsEnabled() throws Exception {
        this.headers.add(ACCESS_CONTROL_REQUEST_METHOD, "GET");
        ResponseEntity<String> entity = performOptions("/cors", this.headers, String.class);
        Assert.assertEquals(OK, entity.getStatusCode());
        Assert.assertEquals("*", entity.getHeaders().getAccessControlAllowOrigin());
        Assert.assertThat(entity.getHeaders().getAccessControlAllowMethods(), Matchers.contains(GET, HEAD, POST));
    }

    @Test
    public void preFlightRequestWithCorsRejected() throws Exception {
        try {
            this.headers.add(ACCESS_CONTROL_REQUEST_METHOD, "GET");
            performOptions("/cors-restricted", this.headers, String.class);
            Assert.fail();
        } catch (HttpClientErrorException e) {
            Assert.assertEquals(FORBIDDEN, e.getStatusCode());
        }
    }

    @Test
    public void preFlightRequestWithoutCorsEnabled() throws Exception {
        try {
            this.headers.add(ACCESS_CONTROL_REQUEST_METHOD, "GET");
            performOptions("/welcome", this.headers, String.class);
            Assert.fail();
        } catch (HttpClientErrorException e) {
            Assert.assertEquals(FORBIDDEN, e.getStatusCode());
        }
    }

    @Test
    public void preFlightRequestWithCorsRestricted() throws Exception {
        this.headers.set(ORIGIN, "http://foo");
        this.headers.add(ACCESS_CONTROL_REQUEST_METHOD, "GET");
        ResponseEntity<String> entity = performOptions("/cors-restricted", this.headers, String.class);
        Assert.assertEquals(OK, entity.getStatusCode());
        Assert.assertEquals("http://foo", entity.getHeaders().getAccessControlAllowOrigin());
        Assert.assertThat(entity.getHeaders().getAccessControlAllowMethods(), Matchers.contains(GET, POST));
    }

    @Test
    public void preFlightRequestWithAmbiguousMapping() throws Exception {
        this.headers.add(ACCESS_CONTROL_REQUEST_METHOD, "GET");
        ResponseEntity<String> entity = performOptions("/ambiguous", this.headers, String.class);
        Assert.assertEquals(OK, entity.getStatusCode());
        Assert.assertEquals("http://localhost:9000", entity.getHeaders().getAccessControlAllowOrigin());
        Assert.assertThat(entity.getHeaders().getAccessControlAllowMethods(), Matchers.contains(GET));
        Assert.assertEquals(true, entity.getHeaders().getAccessControlAllowCredentials());
        Assert.assertThat(entity.getHeaders().get(VARY), Matchers.contains(ORIGIN, ACCESS_CONTROL_REQUEST_METHOD, ACCESS_CONTROL_REQUEST_HEADERS));
    }

    @Configuration
    @ComponentScan(resourcePattern = "**/GlobalCorsConfigIntegrationTests*.class")
    @SuppressWarnings({ "unused", "WeakerAccess" })
    static class WebConfig extends WebFluxConfigurationSupport {
        @Override
        protected void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/cors-restricted").allowedOrigins("http://foo").allowedMethods("GET", "POST");
            registry.addMapping("/cors");
            registry.addMapping("/ambiguous").allowedMethods("GET", "POST");
        }
    }

    @RestController
    @SuppressWarnings("unused")
    static class TestController {
        @GetMapping("/welcome")
        public String welcome() {
            return "welcome";
        }

        @GetMapping("/cors")
        public String cors() {
            return "cors";
        }

        @GetMapping("/cors-restricted")
        public String corsRestricted() {
            return "corsRestricted";
        }

        @GetMapping(value = "/ambiguous", produces = MediaType.TEXT_PLAIN_VALUE)
        public String ambiguous1() {
            return "ambiguous";
        }

        @GetMapping(value = "/ambiguous", produces = MediaType.TEXT_HTML_VALUE)
        public String ambiguous2() {
            return "<p>ambiguous</p>";
        }
    }
}

