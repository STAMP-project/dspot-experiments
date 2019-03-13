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
package org.springframework.boot.test.autoconfigure.web.client;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.MockServerRestTemplateCustomizer;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;


/**
 * Tests for {@link RestClientTest} with two clients.
 *
 * @author Phillip Webb
 */
@RunWith(SpringRunner.class)
@RestClientTest({ ExampleRestClient.class, AnotherExampleRestClient.class })
public class RestClientTestTwoComponentsIntegrationTests {
    @Autowired
    private ExampleRestClient client1;

    @Autowired
    private AnotherExampleRestClient client2;

    @Autowired
    private MockServerRestTemplateCustomizer customizer;

    @Autowired
    private MockRestServiceServer server;

    @Test
    public void serverShouldNotWork() {
        assertThatIllegalStateException().isThrownBy(() -> this.server.expect(requestTo("/test")).andRespond(withSuccess("hello", MediaType.TEXT_HTML))).withMessageContaining("Unable to use auto-configured");
    }

    @Test
    public void client1RestCallViaCustomizer() {
        this.customizer.getServer(this.client1.getRestTemplate()).expect(requestTo("/test")).andRespond(withSuccess("hello", MediaType.TEXT_HTML));
        assertThat(this.client1.test()).isEqualTo("hello");
    }

    @Test
    public void client2RestCallViaCustomizer() {
        this.customizer.getServer(this.client2.getRestTemplate()).expect(requestTo("/test")).andRespond(withSuccess("there", MediaType.TEXT_HTML));
        assertThat(this.client2.test()).isEqualTo("there");
    }
}

