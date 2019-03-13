/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.util;


import java.util.Set;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class QueryIdGeneratorTest {
    private QueryIdGenerator generator;

    @Test
    public void shouldGenerateMonotonicallyIncrementingIds() {
        MatcherAssert.assertThat(generator.getNextId(), Matchers.is("0"));
        MatcherAssert.assertThat(generator.getNextId(), Matchers.is("1"));
        MatcherAssert.assertThat(generator.getNextId(), Matchers.is("2"));
    }

    @Test
    public void shouldCopy() {
        // When:
        final QueryIdGenerator copy = generator.copy();
        // Then:
        MatcherAssert.assertThat(copy.getNextId(), Matchers.is(generator.getNextId()));
        MatcherAssert.assertThat(copy.getNextId(), Matchers.is(generator.getNextId()));
    }

    @Test
    public void shouldBeThreadSafe() {
        // Given:
        final int iterations = 10000;
        // When:
        final Set<String> ids = IntStream.range(0, iterations).parallel().mapToObj(( idx) -> generator.getNextId()).collect(Collectors.toSet());
        // Then:
        MatcherAssert.assertThat(ids, Matchers.hasSize(iterations));
        MatcherAssert.assertThat(ids, Matchers.hasItems("0", String.valueOf((iterations - 1))));
    }
}

