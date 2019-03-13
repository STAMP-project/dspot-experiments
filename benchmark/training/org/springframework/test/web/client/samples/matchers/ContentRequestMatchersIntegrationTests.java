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


import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.Person;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;


/**
 * Examples of defining expectations on request content and content type.
 *
 * @author Rossen Stoyanchev
 * @see JsonPathRequestMatchersIntegrationTests
 * @see XmlContentRequestMatchersIntegrationTests
 * @see XpathRequestMatchersIntegrationTests
 */
public class ContentRequestMatchersIntegrationTests {
    private MockRestServiceServer mockServer;

    private RestTemplate restTemplate;

    @Test
    public void contentType() throws Exception {
        this.mockServer.expect(content().contentType("application/json;charset=UTF-8")).andRespond(withSuccess());
        executeAndVerify(new Person());
    }

    @Test
    public void contentTypeNoMatch() throws Exception {
        this.mockServer.expect(content().contentType("application/json;charset=UTF-8")).andRespond(withSuccess());
        try {
            executeAndVerify("foo");
        } catch (AssertionError error) {
            String message = error.getMessage();
            Assert.assertTrue(message, message.startsWith("Content type expected:<application/json;charset=UTF-8>"));
        }
    }

    @Test
    public void contentAsString() throws Exception {
        this.mockServer.expect(content().string("foo")).andRespond(withSuccess());
        executeAndVerify("foo");
    }

    @Test
    public void contentStringStartsWith() throws Exception {
        this.mockServer.expect(content().string(startsWith("foo"))).andRespond(withSuccess());
        executeAndVerify("foo123");
    }

    @Test
    public void contentAsBytes() throws Exception {
        this.mockServer.expect(content().bytes("foo".getBytes())).andRespond(withSuccess());
        executeAndVerify("foo");
    }
}

