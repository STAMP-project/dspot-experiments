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
package org.springframework.boot.test.context;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.config.EnableWebFlux;


/**
 * Tests for {@link SpringBootTest} in a reactive environment configured with a
 * user-defined {@link RestTemplate} that is named {@code testRestTemplate}.
 *
 * @author Madhura Bhave
 */
@DirtiesContext
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = { "spring.main.web-application-type=reactive", "value=123" })
@RunWith(SpringRunner.class)
public class SpringBootTestReactiveWebEnvironmentUserDefinedTestRestTemplateTests extends AbstractSpringBootTestEmbeddedReactiveWebEnvironmentTests {
    @Test
    public void restTemplateIsUserDefined() {
        assertThat(getContext().getBean("testRestTemplate")).isInstanceOf(RestTemplate.class);
    }

    @Configuration
    @EnableWebFlux
    @RestController
    protected static class Config extends AbstractSpringBootTestEmbeddedReactiveWebEnvironmentTests.AbstractConfig {
        @Bean
        public RestTemplate testRestTemplate() {
            return new RestTemplate();
        }
    }
}

