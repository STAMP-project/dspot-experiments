/**
 * Copyright 2012-2017 the original author or authors.
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
package org.springframework.boot.test.autoconfigure.web.client;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;


/**
 * Tests for {@link RestClientTest} gets reset after test methods.
 *
 * @author Phillip Webb
 */
@RunWith(SpringRunner.class)
@RestClientTest(ExampleRestClient.class)
public class RestClientRestIntegrationTests {
    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ExampleRestClient client;

    @Test
    public void mockServerCall1() {
        this.server.expect(requestTo("/test")).andRespond(withSuccess("1", MediaType.TEXT_HTML));
        assertThat(this.client.test()).isEqualTo("1");
    }

    @Test
    public void mockServerCall2() {
        this.server.expect(requestTo("/test")).andRespond(withSuccess("2", MediaType.TEXT_HTML));
        assertThat(this.client.test()).isEqualTo("2");
    }

    @Test
    public void mockServerCallWithContent() {
        this.server.expect(requestTo("/test")).andExpect(content().string("test")).andRespond(withSuccess("1", MediaType.TEXT_HTML));
        this.client.testPostWithBody("test");
    }
}

