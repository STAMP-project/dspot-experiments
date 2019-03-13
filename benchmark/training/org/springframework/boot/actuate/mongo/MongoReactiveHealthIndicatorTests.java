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
package org.springframework.boot.actuate.mongo;


import com.mongodb.MongoException;
import org.bson.Document;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.actuate.health.Health;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


/**
 * Tests for {@link MongoReactiveHealthIndicator}.
 *
 * @author Yulin Qin
 */
public class MongoReactiveHealthIndicatorTests {
    @Test
    public void testMongoIsUp() {
        Document buildInfo = Mockito.mock(Document.class);
        BDDMockito.given(buildInfo.getString("version")).willReturn("2.6.4");
        ReactiveMongoTemplate reactiveMongoTemplate = Mockito.mock(ReactiveMongoTemplate.class);
        BDDMockito.given(reactiveMongoTemplate.executeCommand("{ buildInfo: 1 }")).willReturn(Mono.just(buildInfo));
        MongoReactiveHealthIndicator mongoReactiveHealthIndicator = new MongoReactiveHealthIndicator(reactiveMongoTemplate);
        Mono<Health> health = mongoReactiveHealthIndicator.health();
        StepVerifier.create(health).consumeNextWith(( h) -> {
            assertThat(h.getStatus()).isEqualTo(Status.UP);
            assertThat(h.getDetails()).containsOnlyKeys("version");
            assertThat(h.getDetails().get("version")).isEqualTo("2.6.4");
        }).verifyComplete();
    }

    @Test
    public void testMongoIsDown() {
        ReactiveMongoTemplate reactiveMongoTemplate = Mockito.mock(ReactiveMongoTemplate.class);
        BDDMockito.given(reactiveMongoTemplate.executeCommand("{ buildInfo: 1 }")).willThrow(new MongoException("Connection failed"));
        MongoReactiveHealthIndicator mongoReactiveHealthIndicator = new MongoReactiveHealthIndicator(reactiveMongoTemplate);
        Mono<Health> health = mongoReactiveHealthIndicator.health();
        StepVerifier.create(health).consumeNextWith(( h) -> {
            assertThat(h.getStatus()).isEqualTo(Status.DOWN);
            assertThat(h.getDetails()).containsOnlyKeys("error");
            assertThat(h.getDetails().get("error")).isEqualTo(((.class.getName()) + ": Connection failed"));
        }).verifyComplete();
    }
}

