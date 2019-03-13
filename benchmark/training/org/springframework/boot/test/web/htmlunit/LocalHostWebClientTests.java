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
package org.springframework.boot.test.web.htmlunit;


import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebConnection;
import com.gargoylesoftware.htmlunit.WebRequest;
import java.net.URL;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.env.MockEnvironment;


/**
 * Tests for {@link LocalHostWebClient}.
 *
 * @author Phillip Webb
 */
@SuppressWarnings("resource")
public class LocalHostWebClientTests {
    @Captor
    private ArgumentCaptor<WebRequest> requestCaptor;

    public LocalHostWebClientTests() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void createWhenEnvironmentIsNullWillThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new LocalHostWebClient(null)).withMessageContaining("Environment must not be null");
    }

    @Test
    public void getPageWhenUrlIsRelativeAndNoPortWillUseLocalhost8080() throws Exception {
        MockEnvironment environment = new MockEnvironment();
        WebClient client = new LocalHostWebClient(environment);
        WebConnection connection = mockConnection();
        client.setWebConnection(connection);
        client.getPage("/test");
        Mockito.verify(connection).getResponse(this.requestCaptor.capture());
        assertThat(this.requestCaptor.getValue().getUrl()).isEqualTo(new URL("http://localhost:8080/test"));
    }

    @Test
    public void getPageWhenUrlIsRelativeAndHasPortWillUseLocalhostPort() throws Exception {
        MockEnvironment environment = new MockEnvironment();
        environment.setProperty("local.server.port", "8181");
        WebClient client = new LocalHostWebClient(environment);
        WebConnection connection = mockConnection();
        client.setWebConnection(connection);
        client.getPage("/test");
        Mockito.verify(connection).getResponse(this.requestCaptor.capture());
        assertThat(this.requestCaptor.getValue().getUrl()).isEqualTo(new URL("http://localhost:8181/test"));
    }
}

