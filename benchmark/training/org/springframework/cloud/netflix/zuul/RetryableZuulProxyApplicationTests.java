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


import HttpMethod.POST;
import HttpStatus.OK;
import MediaType.APPLICATION_FORM_URLENCODED;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.netflix.zuul.filters.discovery.DiscoveryClientRouteLocator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.MultiValueMap;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = RetryableZuulProxyApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT, value = { "zuul.routes[simplerzpat].path: /simplerzpat/**", "zuul.routes[simplerzpat].retryable: true", "zuul.routes[simplerzpat].serviceId: simplerzpat", "ribbon.OkToRetryOnAllOperations: true", "simplerzpat.ribbon.retryableStatusCodes: 404" })
@DirtiesContext
public class RetryableZuulProxyApplicationTests {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    @SuppressWarnings("unused")
    private DiscoveryClientRouteLocator routes;

    @Autowired
    @SuppressWarnings("unused")
    private RoutesEndpoint endpoint;

    @Test
    public void postWithForm() {
        MultiValueMap<String, String> form = new org.springframework.util.LinkedMultiValueMap<String, String>();
        form.set("foo", "bar");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_FORM_URLENCODED);
        ResponseEntity<String> result = testRestTemplate.exchange("/simplerzpat/poster", POST, new org.springframework.http.HttpEntity(form, headers), String.class);
        assertThat(result.getStatusCode()).isEqualTo(OK);
        assertThat(result.getBody()).isEqualTo("Posted! {foo=[bar]}");
    }
}

