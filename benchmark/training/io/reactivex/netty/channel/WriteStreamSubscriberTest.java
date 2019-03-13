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


import BytesWriteInterceptor.MAX_PER_SUBSCRIBER_REQUEST;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.logging.LoggingHandler;
import io.reactivex.netty.channel.BackpressureManagingHandler.WriteStreamSubscriber;
import io.reactivex.netty.test.util.MockProducer;
import java.io.IOException;
import java.util.Queue;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;


public class WriteStreamSubscriberTest {
    @Rule
    public final WriteStreamSubscriberTest.SubscriberRule subscriberRule = new WriteStreamSubscriberTest.SubscriberRule();

    @Test(timeout = 60000)
    public void testOnStart() throws Exception {
        MatcherAssert.assertThat("Unexpected promise completion state.", subscriberRule.channelPromise.isDone(), is(false));
        subscriberRule.start();
        MatcherAssert.assertThat("Unexpected promise completion state.", subscriberRule.channelPromise.isDone(), is(false));
        MatcherAssert.assertThat("Unexpected request made to the producer.", subscriberRule.mockProducer.getRequested(), is(subscriberRule.defaultRequestN()));
    }

    @Test(timeout = 60000)
    public void testUnsubscribeOnPromiseCancel() throws Exception {
        subscriberRule.start();
        MatcherAssert.assertThat("Subsriber isn't subscribed.", subscriberRule.subscriber.isUnsubscribed(), is(false));
        subscriberRule.channelPromise.cancel(false);
        MatcherAssert.assertThat("Promise not cancelled.", subscriberRule.channelPromise.isCancelled(), is(true));
        MatcherAssert.assertThat("Subsriber isn't unsubscribed.", subscriberRule.subscriber.isUnsubscribed(), is(true));
    }

    @Test(timeout = 60000)
    public void testWriteCompleteBeforeStream() throws Exception {
        subscriberRule.start();
        String msg1 = "msg1";
        subscriberRule.writeAndFlushMessages(msg1);
        subscriberRule.assertMessagesWritten(msg1);
        MatcherAssert.assertThat("Unexpected promise completion state.", subscriberRule.channelPromise.isDone(), is(false));
        subscriberRule.subscriber.onCompleted();
        MatcherAssert.assertThat("Unexpected promise completion state.", subscriberRule.channelPromise.isDone(), is(true));
        MatcherAssert.assertThat("Unexpected promise result.", subscriberRule.channelPromise.isSuccess(), is(true));
    }

    @Test(timeout = 60000)
    public void testWriteCompleteAfterStream() throws Exception {
        subscriberRule.start();
        String msg1 = "msg1";
        subscriberRule.writeMessages(msg1);
        MatcherAssert.assertThat("Unexpected promise completion state.", subscriberRule.channelPromise.isDone(), is(false));
        subscriberRule.subscriber.onCompleted();
        /* Complete when write completes. */
        MatcherAssert.assertThat("Unexpected promise completion state.", subscriberRule.channelPromise.isDone(), is(false));
        subscriberRule.channel.flush();/* Completes write */

        subscriberRule.assertMessagesWritten(msg1);
        MatcherAssert.assertThat("Unexpected promise completion state.", subscriberRule.channelPromise.isDone(), is(true));
        MatcherAssert.assertThat("Unexpected promise result.", subscriberRule.channelPromise.isSuccess(), is(true));
    }

    @Test(timeout = 60000)
    public void testMultiWrite() throws Exception {
        subscriberRule.start();
        String msg1 = "msg1";
        String msg2 = "msg2";
        subscriberRule.writeMessages(msg1, msg2);
        MatcherAssert.assertThat("Unexpected promise completion state.", subscriberRule.channelPromise.isDone(), is(false));
        subscriberRule.subscriber.onCompleted();
        /* Complete when write completes. */
        MatcherAssert.assertThat("Unexpected promise completion state.", subscriberRule.channelPromise.isDone(), is(false));
        subscriberRule.channel.flush();/* Completes write */

        subscriberRule.assertMessagesWritten(msg1, msg2);
        MatcherAssert.assertThat("Unexpected promise completion state.", subscriberRule.channelPromise.isDone(), is(true));
        MatcherAssert.assertThat("Unexpected promise result.", subscriberRule.channelPromise.isSuccess(), is(true));
    }

    @Test(timeout = 60000)
    public void testWriteFailed() throws Exception {
        subscriberRule.start();
        String msg1 = "msg1";
        subscriberRule.writeMessages(msg1);
        MatcherAssert.assertThat("Unexpected promise completion state.", subscriberRule.channelPromise.isDone(), is(false));
        subscriberRule.channel.close();
        MatcherAssert.assertThat("Unexpected promise completion state.", subscriberRule.channelPromise.isDone(), is(true));
        MatcherAssert.assertThat("Unexpected promise result.", subscriberRule.channelPromise.isSuccess(), is(false));
    }

    @Test(timeout = 60000)
    public void testStreamError() throws Exception {
        subscriberRule.start();
        subscriberRule.sendMessagesAndAssert(1);
        subscriberRule.subscriber.onError(new IOException());
        MatcherAssert.assertThat("Unexpected promise completion state.", subscriberRule.channelPromise.isDone(), is(true));
        MatcherAssert.assertThat("Unexpected promise result.", subscriberRule.channelPromise.isSuccess(), is(false));
    }

    @Test(timeout = 60000)
    public void testRequestMoreNotRequired() throws Exception {
        subscriberRule.init(4);
        subscriberRule.start();
        MatcherAssert.assertThat("Unexpected request made to the producer.", subscriberRule.mockProducer.getRequested(), is(subscriberRule.defaultRequestN()));
        subscriberRule.sendMessagesAndAssert(2);// Pending: 4 - 2 : low water mark: 4/2

        subscriberRule.subscriber.requestMoreIfNeeded(subscriberRule.defaultRequestN);
        MatcherAssert.assertThat("Unexpected request made to the producer.", subscriberRule.mockProducer.getRequested(), is(subscriberRule.defaultRequestN()));
    }

    @Test(timeout = 60000)
    public void testRequestMoreRequired() throws Exception {
        subscriberRule.init(4);
        subscriberRule.start();
        MatcherAssert.assertThat("Unexpected request made to the producer.", subscriberRule.mockProducer.getRequested(), is(subscriberRule.defaultRequestN()));
        subscriberRule.sendMessagesAndAssert(3);// Pending: 4 - 3 : low water mark: 4/2

        subscriberRule.subscriber.requestMoreIfNeeded(subscriberRule.defaultRequestN);
        MatcherAssert.assertThat("Unexpected request made to the producer.", subscriberRule.mockProducer.getRequested(), is(7L));// request: 4 + 4 - (4 - 3)

    }

    @Test(timeout = 60000)
    public void testLowerMaxBufferSize() throws Exception {
        subscriberRule.init(4);
        subscriberRule.start();
        subscriberRule.subscriber.requestMoreIfNeeded(2);
        MatcherAssert.assertThat("Unexpected request made to the producer.", subscriberRule.mockProducer.getRequested(), is(subscriberRule.defaultRequestN()));
    }

    @Test(timeout = 60000)
    public void testLowerMaxBufferSizeAndThenMore() throws Exception {
        subscriberRule.init(8);
        subscriberRule.start();
        subscriberRule.subscriber.requestMoreIfNeeded(6);
        subscriberRule.sendMessagesAndAssert(6);// Pending: 8 - 6 : low water mark: 6/2

        subscriberRule.subscriber.requestMoreIfNeeded(6);
        MatcherAssert.assertThat("Unexpected request made to the producer.", subscriberRule.mockProducer.getRequested(), is(12L));// requestN: 8 + 6 - (8 - 6)

    }

    @Test(timeout = 60000)
    public void testHigherMaxBufferSize() throws Exception {
        subscriberRule.init(4);
        subscriberRule.start();
        subscriberRule.subscriber.requestMoreIfNeeded(6);
        MatcherAssert.assertThat("Unexpected request made to the producer.", subscriberRule.mockProducer.getRequested(), is(6L));// requestN: 4 + 6 - 4

    }

    public static class SubscriberRule extends ExternalResource {
        private WriteStreamSubscriber subscriber;

        private ChannelPromise channelPromise;

        private EmbeddedChannel channel;

        private MockProducer mockProducer;

        private int defaultRequestN;

        @Override
        public Statement apply(final Statement base, Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    init(MAX_PER_SUBSCRIBER_REQUEST);
                    base.evaluate();
                }
            };
        }

        protected void init(int defaultRequestN) {
            this.defaultRequestN = defaultRequestN;
            channel = new EmbeddedChannel(new LoggingHandler());
            channelPromise = channel.newPromise();
            ChannelHandlerContext ctx = channel.pipeline().firstContext();
            subscriber = new WriteStreamSubscriber(ctx, channelPromise, defaultRequestN().intValue());
            mockProducer = new MockProducer();
        }

        public void start() {
            subscriber.onStart();/* So that setProducer does not request Long.MAX_VALUE */

            subscriber.setProducer(mockProducer);
            mockProducer.assertBackpressureRequested();
            mockProducer.assertIllegalRequest();
        }

        public void writeAndFlushMessages(Object... msgs) {
            writeMessages(msgs);
            channel.flush();
        }

        public void writeMessages(Object... msgs) {
            for (Object msg : msgs) {
                subscriber.onNext(msg);
            }
        }

        public void assertMessagesWritten(Object... msgs) {
            Queue<Object> outboundMessages = channel.outboundMessages();
            if ((null == msgs) || ((msgs.length) == 0)) {
                MatcherAssert.assertThat("Unexpected number of messages written on the channel.", outboundMessages, is(empty()));
                return;
            }
            MatcherAssert.assertThat("Unexpected number of messages written on the channel.", outboundMessages, hasSize(msgs.length));
            MatcherAssert.assertThat("Unexpected messages written on the channel.", outboundMessages, contains(msgs));
        }

        protected void sendMessagesAndAssert(int count) {
            String[] msgs = new String[count];
            for (int i = 0; i < count; i++) {
                msgs[i] = "msg" + i;
            }
            writeAndFlushMessages(msgs);
            MatcherAssert.assertThat("Unexpected promise completion state.", channelPromise.isDone(), is(false));
            assertMessagesWritten(msgs);
        }

        public Long defaultRequestN() {
            return Long.valueOf(defaultRequestN);
        }
    }
}

