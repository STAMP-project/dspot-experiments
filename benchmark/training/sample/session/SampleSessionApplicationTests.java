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
package sample.session;


import java.net.URI;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


/**
 * Tests for {@link SampleSessionApplication}.
 *
 * @author Andy Wilkinson
 * @author Vedran Pavic
 */
public class SampleSessionApplicationTests {
    @Test
    public void sessionExpiry() throws Exception {
        ConfigurableApplicationContext context = createContext();
        String port = context.getEnvironment().getProperty("local.server.port");
        URI uri = URI.create((("http://localhost:" + port) + "/"));
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> firstResponse = firstRequest(restTemplate, uri);
        String sessionId1 = firstResponse.getBody();
        String cookie = firstResponse.getHeaders().getFirst("Set-Cookie");
        String sessionId2 = nextRequest(restTemplate, uri, cookie).getBody();
        assertThat(sessionId1).isEqualTo(sessionId2);
        Thread.sleep(1000);
        String loginPage = nextRequest(restTemplate, uri, cookie).getBody();
        assertThat(loginPage).containsIgnoringCase("login");
    }
}

