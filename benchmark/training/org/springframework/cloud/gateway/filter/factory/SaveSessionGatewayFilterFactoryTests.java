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
package org.springframework.cloud.gateway.filter.factory;


import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.test.BaseWebClientTests;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.WebSession;
import org.springframework.web.server.session.WebSessionManager;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


/**
 *
 *
 * @author Greg Turnquist
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext
@ActiveProfiles(profiles = "save-session-web-filter")
public class SaveSessionGatewayFilterFactoryTests extends BaseWebClientTests {
    static WebSession mockWebSession = Mockito.mock(WebSession.class);

    @Test
    public void webCallShouldTriggerWebSessionSaveAction() {
        Mockito.when(SaveSessionGatewayFilterFactoryTests.mockWebSession.getAttributes()).thenReturn(new HashMap());
        Mockito.when(SaveSessionGatewayFilterFactoryTests.mockWebSession.save()).thenReturn(Mono.empty());
        Mono<Map> result = webClient.get().uri("/get").exchange().flatMap(( response) -> response.body(toMono(.class)));
        StepVerifier.create(result).consumeNextWith(( response) -> {
            // Don't care about data, just need to catch signal
        }).expectComplete().verify(Duration.ofMinutes(10));
        Mockito.verify(SaveSessionGatewayFilterFactoryTests.mockWebSession).save();
    }

    @EnableAutoConfiguration
    @SpringBootConfiguration
    @Import(BaseWebClientTests.DefaultTestConfig.class)
    static class TestConfig {
        @Bean
        WebSessionManager webSessionManager() {
            return ( exchange) -> Mono.just(org.springframework.cloud.gateway.filter.factory.mockWebSession);
        }
    }
}

