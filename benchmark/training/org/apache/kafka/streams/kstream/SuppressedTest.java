/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.streams.kstream;


import java.time.Duration;
import org.apache.kafka.streams.kstream.internals.suppress.BufferFullStrategy;
import org.apache.kafka.streams.kstream.internals.suppress.EagerBufferConfigImpl;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static BufferConfig.maxBytes;
import static BufferConfig.maxRecords;
import static BufferConfig.unbounded;


public class SuppressedTest {
    @Test
    public void bufferBuilderShouldBeConsistent() {
        MatcherAssert.assertThat("noBound should remove bounds", maxBytes(2L).withMaxRecords(4L).withNoBound(), CoreMatchers.is(unbounded()));
        MatcherAssert.assertThat("keys alone should be set", maxRecords(2L), CoreMatchers.is(new EagerBufferConfigImpl(2L, Long.MAX_VALUE)));
        MatcherAssert.assertThat("size alone should be set", maxBytes(2L), CoreMatchers.is(new EagerBufferConfigImpl(Long.MAX_VALUE, 2L)));
    }

    @Test
    public void intermediateEventsShouldAcceptAnyBufferAndSetBounds() {
        MatcherAssert.assertThat("name should be set", Suppressed.untilTimeLimit(Duration.ofMillis(2), unbounded()).withName("myname"), CoreMatchers.is(new org.apache.kafka.streams.kstream.internals.suppress.SuppressedInternal("myname", Duration.ofMillis(2), unbounded(), null, false)));
        MatcherAssert.assertThat("time alone should be set", Suppressed.untilTimeLimit(Duration.ofMillis(2), unbounded()), CoreMatchers.is(new org.apache.kafka.streams.kstream.internals.suppress.SuppressedInternal(null, Duration.ofMillis(2), unbounded(), null, false)));
        MatcherAssert.assertThat("time and unbounded buffer should be set", Suppressed.untilTimeLimit(Duration.ofMillis(2), unbounded()), CoreMatchers.is(new org.apache.kafka.streams.kstream.internals.suppress.SuppressedInternal(null, Duration.ofMillis(2), unbounded(), null, false)));
        MatcherAssert.assertThat("time and keys buffer should be set", Suppressed.untilTimeLimit(Duration.ofMillis(2), BufferConfig.maxRecords(2)), CoreMatchers.is(new org.apache.kafka.streams.kstream.internals.suppress.SuppressedInternal(null, Duration.ofMillis(2), BufferConfig.maxRecords(2), null, false)));
        MatcherAssert.assertThat("time and size buffer should be set", Suppressed.untilTimeLimit(Duration.ofMillis(2), BufferConfig.maxBytes(2)), CoreMatchers.is(new org.apache.kafka.streams.kstream.internals.suppress.SuppressedInternal(null, Duration.ofMillis(2), BufferConfig.maxBytes(2), null, false)));
        MatcherAssert.assertThat("all constraints should be set", Suppressed.untilTimeLimit(Duration.ofMillis(2L), maxRecords(3L).withMaxBytes(2L)), CoreMatchers.is(new org.apache.kafka.streams.kstream.internals.suppress.SuppressedInternal(null, Duration.ofMillis(2), new EagerBufferConfigImpl(3L, 2L), null, false)));
    }

    @Test
    public void finalEventsShouldAcceptStrictBuffersAndSetBounds() {
        MatcherAssert.assertThat(Suppressed.untilWindowCloses(unbounded()), CoreMatchers.is(new org.apache.kafka.streams.kstream.internals.suppress.FinalResultsSuppressionBuilder(null, unbounded())));
        MatcherAssert.assertThat(Suppressed.untilWindowCloses(maxRecords(2L).shutDownWhenFull()), CoreMatchers.is(new org.apache.kafka.streams.kstream.internals.suppress.FinalResultsSuppressionBuilder(null, new org.apache.kafka.streams.kstream.internals.suppress.StrictBufferConfigImpl(2L, Long.MAX_VALUE, BufferFullStrategy.SHUT_DOWN))));
        MatcherAssert.assertThat(Suppressed.untilWindowCloses(maxBytes(2L).shutDownWhenFull()), CoreMatchers.is(new org.apache.kafka.streams.kstream.internals.suppress.FinalResultsSuppressionBuilder(null, new org.apache.kafka.streams.kstream.internals.suppress.StrictBufferConfigImpl(Long.MAX_VALUE, 2L, BufferFullStrategy.SHUT_DOWN))));
        MatcherAssert.assertThat(Suppressed.untilWindowCloses(unbounded()).withName("name"), CoreMatchers.is(new org.apache.kafka.streams.kstream.internals.suppress.FinalResultsSuppressionBuilder("name", unbounded())));
        MatcherAssert.assertThat(Suppressed.untilWindowCloses(maxRecords(2L).shutDownWhenFull()).withName("name"), CoreMatchers.is(new org.apache.kafka.streams.kstream.internals.suppress.FinalResultsSuppressionBuilder("name", new org.apache.kafka.streams.kstream.internals.suppress.StrictBufferConfigImpl(2L, Long.MAX_VALUE, BufferFullStrategy.SHUT_DOWN))));
        MatcherAssert.assertThat(Suppressed.untilWindowCloses(maxBytes(2L).shutDownWhenFull()).withName("name"), CoreMatchers.is(new org.apache.kafka.streams.kstream.internals.suppress.FinalResultsSuppressionBuilder("name", new org.apache.kafka.streams.kstream.internals.suppress.StrictBufferConfigImpl(Long.MAX_VALUE, 2L, BufferFullStrategy.SHUT_DOWN))));
    }
}

