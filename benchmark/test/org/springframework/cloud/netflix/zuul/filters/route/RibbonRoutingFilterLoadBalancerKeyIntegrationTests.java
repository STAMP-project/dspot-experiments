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
package org.springframework.cloud.netflix.zuul.filters.route;


import HttpMethod.GET;
import HttpStatus.INTERNAL_SERVER_ERROR;
import HttpStatus.OK;
import SpringBootTest.WebEnvironment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;


/**
 *
 *
 * @author Yongsung Yoon
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CanaryTestZuulProxyApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT, value = { "zuul.routes.simple.path: /simple/**" })
@DirtiesContext
public class RibbonRoutingFilterLoadBalancerKeyIntegrationTests {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void invokeWithUserDefinedCanaryHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Canary-Test", "true");
        ResponseEntity<String> result = testRestTemplate.exchange("/simple/hello", GET, new org.springframework.http.HttpEntity(headers), String.class);
        assertThat(result.getStatusCode()).isEqualTo(OK);
        assertThat(result.getBody()).isEqualTo("canary");
    }

    @Test
    public void invokeWithoutUserDefinedCanaryHeader() {
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<String> result = testRestTemplate.exchange("/simple/hello", GET, new org.springframework.http.HttpEntity(headers), String.class);
        assertThat(result.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
    }
}

