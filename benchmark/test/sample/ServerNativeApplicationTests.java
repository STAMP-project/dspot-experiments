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


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, properties = "spring.application.name:bad", webEnvironment = RANDOM_PORT)
public class ServerNativeApplicationTests {
    private static int configPort = 0;

    private static ConfigurableApplicationContext server;

    @Autowired
    private ConfigurableEnvironment environment;

    @LocalServerPort
    private int port;

    @Test
    public void contextLoads() {
        // The remote config was bad so there is no bootstrap
        assertThat(this.environment.getPropertySources().contains("bootstrap")).isFalse();
    }
}

