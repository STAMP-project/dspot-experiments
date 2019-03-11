/**
 * Copyright 2015-2018 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package zipkin2.collector;


import LOTS_OF_SPANS.length;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import zipkin2.TestObjects;

import static zipkin2.TestObjects.LOTS_OF_SPANS.length;


public class CollectorSamplerTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Math.abs("8000000000000000") returns a negative, we coerse to "7fffffffffffffff" to avoid
     * always dropping when trace_id == "8000000000000000"
     */
    @Test
    public void mostNegativeNumberDefence() {
        CollectorSampler sampler = CollectorSampler.create(0.1F);
        assertThat(sampler.isSampled("8000000000000000", false)).isEqualTo(sampler.isSampled("7fffffffffffffff", false));
    }

    @Test
    public void debugWins() {
        CollectorSampler sampler = CollectorSampler.create(0.0F);
        assertThat(sampler.isSampled("8000000000000000", true)).isTrue();
    }

    @Test
    public void retain10Percent() {
        float sampleRate = 0.1F;
        CollectorSampler sampler = CollectorSampler.create(sampleRate);
        assertThat(CollectorSamplerTest.lotsOfSpans().filter(( s) -> sampler.isSampled(s.traceId(), false)).count()).isCloseTo(((long) ((length) * sampleRate)), withPercentage(3));
    }

    /**
     * The collector needs to apply the same decision to incremental updates in a trace.
     */
    @Test
    public void idempotent() {
        CollectorSampler sampler1 = CollectorSampler.create(0.1F);
        CollectorSampler sampler2 = CollectorSampler.create(0.1F);
        assertThat(CollectorSamplerTest.lotsOfSpans().filter(( s) -> sampler1.isSampled(s.traceId(), false)).toArray()).containsExactly(CollectorSamplerTest.lotsOfSpans().filter(( s) -> sampler2.isSampled(s.traceId(), false)).toArray());
    }

    @Test
    public void zeroMeansDropAllTraces() {
        CollectorSampler sampler = CollectorSampler.create(0.0F);
        assertThat(CollectorSamplerTest.lotsOfSpans().filter(( s) -> sampler.isSampled(s.traceId(), false))).isEmpty();
    }

    @Test
    public void oneMeansKeepAllTraces() {
        CollectorSampler sampler = CollectorSampler.create(1.0F);
        assertThat(CollectorSamplerTest.lotsOfSpans().filter(( s) -> sampler.isSampled(s.traceId(), false))).hasSize(length);
    }

    @Test
    public void rateCantBeNegative() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("rate should be between 0 and 1: was -1.0");
        CollectorSampler.create((-1.0F));
    }

    @Test
    public void rateCantBeOverOne() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("rate should be between 0 and 1: was 1.1");
        CollectorSampler.create(1.1F);
    }
}

