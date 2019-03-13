/**
 * Copyright 2015 Netflix, Inc.
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
package io.reactivex.netty.channel;


import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.logging.LoggingHandler;
import io.reactivex.netty.channel.AbstractConnectionToChannelBridge.ReadProducer;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import rx.Subscriber;
import rx.exceptions.MissingBackpressureException;
import rx.observers.Subscribers;


public class ReadProducerTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public final ReadProducerTest.ProducerRule producerRule = new ReadProducerTest.ProducerRule();

    @Test(timeout = 60000)
    public void testTurnOffBackpressureAtStart() throws Exception {
        producerRule.producer.request(Long.MAX_VALUE);
        MatcherAssert.assertThat("Unexpected requested count.", producerRule.producer.getRequested(), is(Long.MAX_VALUE));
        producerRule.producer.sendOnNext("Hello");
        /* Backpressure turned off, don't decrement */
        MatcherAssert.assertThat("Unexpected requested count.", producerRule.producer.getRequested(), is(Long.MAX_VALUE));
    }

    @Test(timeout = 60000)
    public void testTurnOffBackpressureLater() throws Exception {
        producerRule.producer.request(1);
        MatcherAssert.assertThat("Unexpected requested count.", producerRule.producer.getRequested(), is(1L));
        producerRule.producer.request(Long.MAX_VALUE);/* now turn off */

        MatcherAssert.assertThat("Unexpected requested count.", producerRule.producer.getRequested(), is(Long.MAX_VALUE));
        producerRule.producer.sendOnNext("Hello");
        /* Backpressure turned off, don't decrement */
        MatcherAssert.assertThat("Unexpected requested count.", producerRule.producer.getRequested(), is(Long.MAX_VALUE));
    }

    @Test(timeout = 60000)
    public void testBackpressure() throws Exception {
        producerRule.producer.request(1);
        MatcherAssert.assertThat("Unexpected requested count.", producerRule.producer.getRequested(), is(1L));
        producerRule.producer.sendOnNext("Hello");
        /* Backpressure on, so decrement */
        MatcherAssert.assertThat("Unexpected requested count.", producerRule.producer.getRequested(), is(0L));
    }

    @Test(timeout = 60000)
    public void testRequestedOverflow() throws Exception {
        producerRule.producer.request(1);
        MatcherAssert.assertThat("Unexpected requested count.", producerRule.producer.getRequested(), is(1L));
        producerRule.producer.request(((Long.MAX_VALUE) - 1));/* Adding overflows & turn off backpressure */

        MatcherAssert.assertThat("Unexpected requested count.", producerRule.producer.getRequested(), is(Long.MAX_VALUE));
        producerRule.producer.sendOnNext("Hello");
        /* Backpressure turned off, don't decrement */
        MatcherAssert.assertThat("Unexpected requested count.", producerRule.producer.getRequested(), is(Long.MAX_VALUE));
    }

    @Test(timeout = 60000)
    public void testSupplyMoreThanDemand() throws Exception {
        thrown.expectCause(isA(MissingBackpressureException.class));
        producerRule.producer.request(1);
        MatcherAssert.assertThat("Unexpected requested count.", producerRule.producer.getRequested(), is(1L));
        producerRule.producer.sendOnNext("Hello");
        /* Backpressure on, so decrement */
        MatcherAssert.assertThat("Unexpected requested count.", producerRule.producer.getRequested(), is(0L));
        producerRule.producer.sendOnNext("Hello");/* overflow */

    }

    @Test(timeout = 60000)
    public void testShouldReadMoreOnLessDemand() throws Exception {
        producerRule.producer.request(0);
        MatcherAssert.assertThat("Unexpected requested count.", producerRule.producer.getRequested(), is(0L));
        MatcherAssert.assertThat("Unexpected read more status.", producerRule.producer.shouldReadMore(producerRule.channel.pipeline().firstContext()), is(false));
    }

    @Test(timeout = 60000)
    public void testShouldReadMoreOnUnsubscribe() throws Exception {
        producerRule.producer.request(1);
        MatcherAssert.assertThat("Unexpected requested count.", producerRule.producer.getRequested(), is(1L));
        MatcherAssert.assertThat("Unexpected read more status.", producerRule.producer.shouldReadMore(producerRule.channel.pipeline().firstContext()), is(true));
        producerRule.subscriber.unsubscribe();
        MatcherAssert.assertThat("Unexpected read more status.", producerRule.producer.shouldReadMore(producerRule.channel.pipeline().firstContext()), is(false));
    }

    public static class ProducerRule extends ExternalResource {
        private ReadProducer<String> producer;

        private EmbeddedChannel channel;

        private Subscriber<Object> subscriber;

        @Override
        public Statement apply(final Statement base, Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    channel = new EmbeddedChannel(new LoggingHandler());
                    subscriber = Subscribers.empty();
                    producer = new ReadProducer(subscriber, channel);
                    base.evaluate();
                }
            };
        }
    }
}

