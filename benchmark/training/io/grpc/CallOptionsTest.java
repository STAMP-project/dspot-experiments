/**
 * Copyright 2015 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.grpc;


import CallOptions.DEFAULT;
import CallOptions.Key;
import ClientStreamTracer.Factory;
import Deadline.Ticker;
import com.google.common.util.concurrent.MoreExecutors;
import io.grpc.internal.SerializingExecutor;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import static CallOptions.DEFAULT;


/**
 * Unit tests for {@link CallOptions}.
 */
@RunWith(JUnit4.class)
public class CallOptionsTest {
    private static final CallOptions.Key<String> OPTION_1 = Key.createWithDefault("option1", "default");

    private static final CallOptions.Key<String> OPTION_2 = Key.createWithDefault("option2", "default");

    private final String sampleAuthority = "authority";

    private final String sampleCompressor = "compressor";

    private final Ticker ticker = new CallOptionsTest.FakeTicker();

    private final Deadline sampleDeadline = Deadline.after(1, TimeUnit.NANOSECONDS, ticker);

    private final CallCredentials sampleCreds = Mockito.mock(CallCredentials.class);

    private final Factory tracerFactory1 = new CallOptionsTest.FakeTracerFactory("tracerFactory1");

    private final Factory tracerFactory2 = new CallOptionsTest.FakeTracerFactory("tracerFactory2");

    private final CallOptions allSet = DEFAULT.withAuthority(sampleAuthority).withDeadline(sampleDeadline).withCallCredentials(sampleCreds).withCompression(sampleCompressor).withWaitForReady().withExecutor(MoreExecutors.directExecutor()).withOption(io.grpc.OPTION_1, "value1").withStreamTracerFactory(tracerFactory1).withOption(io.grpc.OPTION_2, "value2").withStreamTracerFactory(tracerFactory2);

    @Test
    public void defaultsAreAllNull() {
        assertThat(DEFAULT.getDeadline()).isNull();
        assertThat(DEFAULT.getAuthority()).isNull();
        assertThat(DEFAULT.getExecutor()).isNull();
        assertThat(DEFAULT.getCredentials()).isNull();
        assertThat(DEFAULT.getCompressor()).isNull();
        assertThat(DEFAULT.isWaitForReady()).isFalse();
        assertThat(DEFAULT.getStreamTracerFactories()).isEmpty();
    }

    @Test
    public void withAndWithoutWaitForReady() {
        assertThat(DEFAULT.withWaitForReady().isWaitForReady()).isTrue();
        assertThat(DEFAULT.withWaitForReady().withoutWaitForReady().isWaitForReady()).isFalse();
    }

    @Test
    public void allWiths() {
        assertThat(allSet.getAuthority()).isSameAs(sampleAuthority);
        assertThat(allSet.getDeadline()).isSameAs(sampleDeadline);
        assertThat(allSet.getCredentials()).isSameAs(sampleCreds);
        assertThat(allSet.getCompressor()).isSameAs(sampleCompressor);
        assertThat(allSet.getExecutor()).isSameAs(MoreExecutors.directExecutor());
        assertThat(allSet.getOption(io.grpc.OPTION_1)).isSameAs("value1");
        assertThat(allSet.getOption(io.grpc.OPTION_2)).isSameAs("value2");
        assertThat(allSet.isWaitForReady()).isTrue();
    }

    @Test
    public void noStrayModifications() {
        assertThat(CallOptionsTest.equal(allSet, allSet.withAuthority("blah").withAuthority(sampleAuthority))).isTrue();
        assertThat(CallOptionsTest.equal(allSet, allSet.withDeadline(Deadline.after(314, TimeUnit.NANOSECONDS)).withDeadline(sampleDeadline))).isTrue();
        assertThat(CallOptionsTest.equal(allSet, allSet.withCallCredentials(Mockito.mock(CallCredentials.class)).withCallCredentials(sampleCreds))).isTrue();
    }

    @Test
    public void mutation() {
        Deadline deadline = Deadline.after(10, TimeUnit.SECONDS);
        CallOptions options1 = DEFAULT.withDeadline(deadline);
        assertThat(DEFAULT.getDeadline()).isNull();
        assertThat(deadline).isSameAs(options1.getDeadline());
        CallOptions options2 = options1.withDeadline(null);
        assertThat(deadline).isSameAs(options1.getDeadline());
        assertThat(options2.getDeadline()).isNull();
    }

    @Test
    public void mutateExecutor() {
        Executor executor = MoreExecutors.directExecutor();
        CallOptions options1 = DEFAULT.withExecutor(executor);
        assertThat(DEFAULT.getExecutor()).isNull();
        assertThat(executor).isSameAs(options1.getExecutor());
        CallOptions options2 = options1.withExecutor(null);
        assertThat(executor).isSameAs(options1.getExecutor());
        assertThat(options2.getExecutor()).isNull();
    }

    @Test
    public void withDeadlineAfter() {
        Deadline actual = DEFAULT.withDeadlineAfter(1, TimeUnit.MINUTES).getDeadline();
        Deadline expected = Deadline.after(1, TimeUnit.MINUTES);
        assertAbout(deadline()).that(actual).isWithin(10, TimeUnit.MILLISECONDS).of(expected);
    }

    @Test
    public void toStringMatches_noDeadline_default() {
        String actual = allSet.withDeadline(null).withExecutor(new SerializingExecutor(MoreExecutors.directExecutor())).withCallCredentials(null).withMaxInboundMessageSize(44).withMaxOutboundMessageSize(55).toString();
        assertThat(actual).contains("deadline=null");
        assertThat(actual).contains("authority=authority");
        assertThat(actual).contains("callCredentials=null");
        assertThat(actual).contains("executor=class io.grpc.internal.SerializingExecutor");
        assertThat(actual).contains("compressorName=compressor");
        assertThat(actual).contains("customOptions=[[option1, value1], [option2, value2]]");
        assertThat(actual).contains("waitForReady=true");
        assertThat(actual).contains("maxInboundMessageSize=44");
        assertThat(actual).contains("maxOutboundMessageSize=55");
        assertThat(actual).contains("streamTracerFactories=[tracerFactory1, tracerFactory2]");
    }

    @Test
    public void toStringMatches_noDeadline() {
        String actual = DEFAULT.toString();
        assertThat(actual).contains("deadline=null");
    }

    @Test
    public void toStringMatches_withDeadline() {
        assertThat(allSet.toString()).contains("0.000000001s from now");
    }

    @Test
    public void withCustomOptionDefault() {
        CallOptions opts = DEFAULT;
        assertThat(opts.getOption(io.grpc.OPTION_1)).isEqualTo("default");
    }

    @Test
    public void withCustomOption() {
        CallOptions opts = DEFAULT.withOption(io.grpc.OPTION_1, "v1");
        assertThat(opts.getOption(io.grpc.OPTION_1)).isEqualTo("v1");
    }

    @Test
    public void withCustomOptionLastOneWins() {
        CallOptions opts = DEFAULT.withOption(io.grpc.OPTION_1, "v1").withOption(io.grpc.OPTION_1, "v2");
        assertThat(opts.getOption(io.grpc.OPTION_1)).isEqualTo("v2");
    }

    @Test
    public void withMultipleCustomOption() {
        CallOptions opts = DEFAULT.withOption(io.grpc.OPTION_1, "v1").withOption(io.grpc.OPTION_2, "v2");
        assertThat(opts.getOption(io.grpc.OPTION_1)).isEqualTo("v1");
        assertThat(opts.getOption(io.grpc.OPTION_2)).isEqualTo("v2");
    }

    @Test
    public void withOptionDoesNotMutateOriginal() {
        CallOptions defaultOpt = DEFAULT;
        CallOptions opt1 = defaultOpt.withOption(io.grpc.OPTION_1, "v1");
        CallOptions opt2 = opt1.withOption(io.grpc.OPTION_1, "v2");
        assertThat(defaultOpt.getOption(io.grpc.OPTION_1)).isEqualTo("default");
        assertThat(opt1.getOption(io.grpc.OPTION_1)).isEqualTo("v1");
        assertThat(opt2.getOption(io.grpc.OPTION_1)).isEqualTo("v2");
    }

    @Test
    public void withStreamTracerFactory() {
        CallOptions opts1 = DEFAULT.withStreamTracerFactory(tracerFactory1);
        CallOptions opts2 = opts1.withStreamTracerFactory(tracerFactory2);
        CallOptions opts3 = opts2.withStreamTracerFactory(tracerFactory2);
        assertThat(opts1.getStreamTracerFactories()).containsExactly(tracerFactory1);
        assertThat(opts2.getStreamTracerFactories()).containsExactly(tracerFactory1, tracerFactory2).inOrder();
        assertThat(opts3.getStreamTracerFactories()).containsExactly(tracerFactory1, tracerFactory2, tracerFactory2).inOrder();
        try {
            DEFAULT.getStreamTracerFactories().add(tracerFactory1);
            Assert.fail("Should have thrown. The list should be unmodifiable.");
        } catch (UnsupportedOperationException e) {
            // Expected
        }
        try {
            opts2.getStreamTracerFactories().clear();
            Assert.fail("Should have thrown. The list should be unmodifiable.");
        } catch (UnsupportedOperationException e) {
            // Expected
        }
    }

    private static class FakeTicker extends Deadline.Ticker {
        private long time;

        @Override
        public long read() {
            return time;
        }

        public void reset(long time) {
            this.time = time;
        }

        public void increment(long period, TimeUnit unit) {
            if (period < 0) {
                throw new IllegalArgumentException();
            }
            this.time += unit.toNanos(period);
        }
    }

    private static class FakeTracerFactory extends ClientStreamTracer.Factory {
        final String name;

        FakeTracerFactory(String name) {
            this.name = name;
        }

        @Override
        public ClientStreamTracer newClientStreamTracer(ClientStreamTracer.StreamInfo info, Metadata headers) {
            return new ClientStreamTracer() {};
        }

        @Override
        public String toString() {
            return name;
        }
    }
}

