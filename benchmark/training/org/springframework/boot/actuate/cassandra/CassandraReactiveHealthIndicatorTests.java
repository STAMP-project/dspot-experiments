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
package org.springframework.boot.actuate.cassandra;


import com.datastax.driver.core.querybuilder.Select;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.actuate.health.Health;
import org.springframework.data.cassandra.CassandraInternalException;
import org.springframework.data.cassandra.core.ReactiveCassandraOperations;
import org.springframework.data.cassandra.core.cql.ReactiveCqlOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


/**
 * Tests for {@link CassandraReactiveHealthIndicator}.
 *
 * @author Artsiom Yudovin
 */
public class CassandraReactiveHealthIndicatorTests {
    @Test
    public void testCassandraIsUp() {
        ReactiveCqlOperations reactiveCqlOperations = Mockito.mock(ReactiveCqlOperations.class);
        BDDMockito.given(reactiveCqlOperations.queryForObject(ArgumentMatchers.any(Select.class), ArgumentMatchers.eq(String.class))).willReturn(Mono.just("6.0.0"));
        ReactiveCassandraOperations reactiveCassandraOperations = Mockito.mock(ReactiveCassandraOperations.class);
        BDDMockito.given(reactiveCassandraOperations.getReactiveCqlOperations()).willReturn(reactiveCqlOperations);
        CassandraReactiveHealthIndicator cassandraReactiveHealthIndicator = new CassandraReactiveHealthIndicator(reactiveCassandraOperations);
        Mono<Health> health = cassandraReactiveHealthIndicator.health();
        StepVerifier.create(health).consumeNextWith(( h) -> {
            assertThat(h.getStatus()).isEqualTo(Status.UP);
            assertThat(h.getDetails()).containsOnlyKeys("version");
            assertThat(h.getDetails().get("version")).isEqualTo("6.0.0");
        }).verifyComplete();
    }

    @Test
    public void testCassandraIsDown() {
        ReactiveCassandraOperations reactiveCassandraOperations = Mockito.mock(ReactiveCassandraOperations.class);
        BDDMockito.given(reactiveCassandraOperations.getReactiveCqlOperations()).willThrow(new CassandraInternalException("Connection failed"));
        CassandraReactiveHealthIndicator cassandraReactiveHealthIndicator = new CassandraReactiveHealthIndicator(reactiveCassandraOperations);
        Mono<Health> health = cassandraReactiveHealthIndicator.health();
        StepVerifier.create(health).consumeNextWith(( h) -> {
            assertThat(h.getStatus()).isEqualTo(Status.DOWN);
            assertThat(h.getDetails()).containsOnlyKeys("error");
            assertThat(h.getDetails().get("error")).isEqualTo(((.class.getName()) + ": Connection failed"));
        }).verifyComplete();
    }
}

