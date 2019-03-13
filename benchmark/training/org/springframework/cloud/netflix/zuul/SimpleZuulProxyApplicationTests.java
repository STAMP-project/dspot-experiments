/**
 * Copyright 2013-2019 the original author or authors.
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
package org.springframework.cloud.netflix.zuul;


import HttpMethod.DELETE;
import HttpMethod.GET;
import HttpMethod.PATCH;
import HttpMethod.POST;
import HttpMethod.PUT;
import HttpStatus.OK;
import java.net.URI;
import java.net.URISyntaxException;
import javax.servlet.http.HttpServletRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.netflix.zuul.filters.discovery.DiscoveryClientRouteLocator;
import org.springframework.cloud.netflix.zuul.test.NoSecurityConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SimpleZuulProxyApplicationTests.SimpleZuulProxyApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT, value = { "zuul.forceOriginalQueryStringEncoding: true" })
@DirtiesContext
public class SimpleZuulProxyApplicationTests {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private DiscoveryClientRouteLocator routes;

    @Autowired
    private RoutesEndpoint endpoint;

    @Test
    public void getOnSelfViaSimpleHostRoutingFilter() {
        ResponseEntity<String> result = executeSimpleRequest(GET);
        assertResponseCodeAndBody(result, "get bar");
    }

    @Test
    public void postOnSelfViaSimpleHostRoutingFilter() {
        ResponseEntity<String> result = executeSimpleRequest(POST);
        assertResponseCodeAndBody(result, "post bar");
    }

    @Test
    public void putOnSelfViaSimpleHostRoutingFilter() {
        ResponseEntity<String> result = executeSimpleRequest(PUT);
        assertResponseCodeAndBody(result, "put bar");
    }

    @Test
    public void patchOnSelfViaSimpleHostRoutingFilter() {
        ResponseEntity<String> result = executeSimpleRequest(PATCH);
        assertResponseCodeAndBody(result, "patch bar");
    }

    @Test
    public void deleteOnSelfViaSimpleHostRoutingFilter() {
        ResponseEntity<String> result = executeSimpleRequest(DELETE);
        assertResponseCodeAndBody(result, "delete bar");
    }

    @Test
    public void getOnSelfWithComplexQueryParam() throws URISyntaxException {
        String encodedQueryString = "foo=%7B%22project%22%3A%22stream%22%2C%22logger" + ("%22%3A%22javascript%22%2C%22platform%22%3A%22javascript%22%2C%22" + "request%22%3A%7B%22url%22%3A%22https%3A%2F%2Ffoo%2Fadmin");
        ResponseEntity<String> result = testRestTemplate.exchange(new URI(("/foo?" + encodedQueryString)), GET, new org.springframework.http.HttpEntity(((Void) (null))), String.class);
        assertThat(result.getStatusCode()).isEqualTo(OK);
        assertThat(result.getBody()).isEqualTo(encodedQueryString);
    }

    // Don't use @SpringBootApplication because we don't want to component scan
    @Configuration
    @EnableAutoConfiguration
    @RestController
    @EnableZuulProxy
    @Import(NoSecurityConfiguration.class)
    static class SimpleZuulProxyApplication {
        @RequestMapping(value = "/bar", method = RequestMethod.GET)
        public String get(@RequestParam
        String id) {
            return "get " + id;
        }

        @RequestMapping(value = "/bar", method = RequestMethod.GET, params = { "foo" })
        public String complexGet(@RequestParam
        String foo, HttpServletRequest request) {
            return request.getQueryString();
        }

        @RequestMapping(value = "/bar", method = RequestMethod.POST)
        public String post(@RequestParam
        String id) {
            return "post " + id;
        }

        @RequestMapping(value = "/bar", method = RequestMethod.PUT)
        public String put(@RequestParam
        String id) {
            return "put " + id;
        }

        @RequestMapping(value = "/bar", method = RequestMethod.DELETE)
        public String delete(@RequestParam
        String id) {
            return "delete " + id;
        }

        @RequestMapping(value = "/bar", method = RequestMethod.PATCH)
        public String patch(@RequestParam
        String id) {
            return "patch " + id;
        }
    }
}

