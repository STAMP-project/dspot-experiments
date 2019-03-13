/**
 * Copyright 2018-2019 the original author or authors.
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
package sample;


import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.SocketUtils;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, properties = // Normally spring.cloud.config.enabled:true is the default but since we have the
// config
// server on the classpath we need to set it explicitly
{ "spring.cloud.config.enabled:true", "management.security.enabled=false", "management.endpoints.web.exposure.include=*" }, webEnvironment = RANDOM_PORT)
public class ApplicationTests {
    private static final String BASE_PATH = new WebEndpointProperties().getBasePath();

    private static int configPort = SocketUtils.findAvailableTcpPort();

    private static ConfigurableApplicationContext server;

    @LocalServerPort
    private int port;

    @Test
    @SuppressWarnings("unchecked")
    public void contextLoads() {
        Map res = new TestRestTemplate().getForObject(((("http://localhost:" + (this.port)) + (ApplicationTests.BASE_PATH)) + "/env/info.foo"), Map.class);
        assertThat(res).containsKey("propertySources");
        Map<String, Object> property = ((Map<String, Object>) (res.get("property")));
        assertThat(property).containsEntry("value", "bar");
    }
}

