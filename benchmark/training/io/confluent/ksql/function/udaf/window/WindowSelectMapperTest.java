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
package io.confluent.ksql.function.udaf.window;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.confluent.ksql.GenericRow;
import io.confluent.ksql.function.KsqlAggregateFunction;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.kafka.streams.kstream.Window;
import org.apache.kafka.streams.kstream.internals.SessionWindow;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.MockType;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(EasyMockRunner.class)
public class WindowSelectMapperTest {
    @Mock(MockType.NICE)
    private KsqlAggregateFunction windowStartFunc;

    @Mock(MockType.NICE)
    private KsqlAggregateFunction windowEndFunc;

    @Mock(MockType.NICE)
    private KsqlAggregateFunction otherFunc;

    @Test
    public void shouldNotDetectNonWindowBoundsSelects() {
        MatcherAssert.assertThat(hasSelects(), Matchers.is(false));
    }

    @Test
    public void shouldDetectWindowStartSelects() {
        MatcherAssert.assertThat(hasSelects(), Matchers.is(true));
    }

    @Test
    public void shouldDetectWindowEndSelects() {
        MatcherAssert.assertThat(hasSelects(), Matchers.is(true));
    }

    @Test
    public void shouldUpdateRowWithWindowBounds() {
        // Given:
        final WindowSelectMapper mapper = new WindowSelectMapper(ImmutableMap.of(0, otherFunc, 2, windowStartFunc, 3, windowEndFunc, 4, windowStartFunc));
        final Window window = new SessionWindow(12345L, 54321L);
        final GenericRow row = new GenericRow(Arrays.asList(0, 1, 2, 3, 4, 5));
        // When:
        final GenericRow result = mapper.apply(new org.apache.kafka.streams.kstream.Windowed("k", window), row);
        // Then:
        MatcherAssert.assertThat(result, Matchers.is(Matchers.sameInstance(row)));
        MatcherAssert.assertThat(row.getColumns(), Matchers.is(ImmutableList.of(0, 1, 12345L, 54321L, 12345L, 5)));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowIfRowNotBigEnough() {
        // Given:
        final WindowSelectMapper mapper = new WindowSelectMapper(ImmutableMap.of(0, windowStartFunc));
        final Window window = new SessionWindow(12345L, 54321L);
        final GenericRow row = new GenericRow(new ArrayList());
        // When:
        mapper.apply(new org.apache.kafka.streams.kstream.Windowed("k", window), row);
    }
}

