/**
 * Copyright 2013-2019 the original author or authors.
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
package org.springframework.cloud.gateway.test;


import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


// TODO: why does this have to be in a separate test?
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext
@SuppressWarnings("unchecked")
public class PostTests extends BaseWebClientTests {
    @Test
    public void postWorks() {
        Mono<Map> result = webClient.post().uri("/post").header("Host", "www.example.org").syncBody("testdata").exchange().flatMap(( response) -> response.body(toMono(.class)));
        StepVerifier.create(result).consumeNextWith(( map) -> assertThat(map).containsEntry("data", "testdata")).expectComplete().verify(BaseWebClientTests.DURATION);
    }

    @EnableAutoConfiguration
    @SpringBootConfiguration
    @Import(BaseWebClientTests.DefaultTestConfig.class)
    public static class TestConfig {}
}

