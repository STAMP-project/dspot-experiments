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
package org.springframework.test.web.client.samples.matchers;


import org.junit.Test;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;


/**
 * Examples of defining expectations on request headers.
 *
 * @author Rossen Stoyanchev
 */
public class HeaderRequestMatchersIntegrationTests {
    private static final String RESPONSE_BODY = "{\"name\" : \"Ludwig van Beethoven\", \"someDouble\" : \"1.6035\"}";

    private MockRestServiceServer mockServer;

    private RestTemplate restTemplate;

    @Test
    public void testString() throws Exception {
        this.mockServer.expect(requestTo("/person/1")).andExpect(header("Accept", "application/json, application/*+json")).andRespond(withSuccess(HeaderRequestMatchersIntegrationTests.RESPONSE_BODY, MediaType.APPLICATION_JSON));
        executeAndVerify();
    }

    @Test
    public void testStringContains() throws Exception {
        this.mockServer.expect(requestTo("/person/1")).andExpect(header("Accept", containsString("json"))).andRespond(withSuccess(HeaderRequestMatchersIntegrationTests.RESPONSE_BODY, MediaType.APPLICATION_JSON));
        executeAndVerify();
    }
}

