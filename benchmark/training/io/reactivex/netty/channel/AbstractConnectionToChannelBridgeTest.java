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


import EmitConnectionEvent.INSTANCE;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.reactivex.netty.channel.BackpressureManagingHandler.RequestReadIfRequiredEvent;
import io.reactivex.netty.channel.events.ConnectionEventListener;
import io.reactivex.netty.test.util.MockEventPublisher;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import rx.observers.TestSubscriber;


public class AbstractConnectionToChannelBridgeTest {
    @Rule
    public final AbstractConnectionToChannelBridgeTest.ConnectionHandlerRule connectionHandlerRule = new AbstractConnectionToChannelBridgeTest.ConnectionHandlerRule();

    @Test(timeout = 60000)
    public void testChannelActive() throws Exception {
        AbstractConnectionToChannelBridgeTest.ConnectionSubscriber subscriber = connectionHandlerRule.enableConnectionSubscriberAndAssert(false);
        connectionHandlerRule.activateConnectionAndAssert(subscriber);
        Assert.assertThat("Duplicate channel active event sent a notification", getOnNextEvents(), hasSize(1));
        connectionHandlerRule.handler.channelActive(connectionHandlerRule.ctx);// duplicate event should not trigger onNext.

        /* One item from activation */
        Assert.assertThat("Duplicate channel active event sent a notification", getOnNextEvents(), hasSize(1));
    }

    @Test(timeout = 60000)
    public void testEagerContentSubscriptionFail() throws Exception {
        connectionHandlerRule.channel.config().setAutoRead(true);// should mandate eager content subscription

        AbstractConnectionToChannelBridgeTest.ConnectionSubscriber subscriber = connectionHandlerRule.enableConnectionSubscriberAndAssert(false);
        connectionHandlerRule.activateConnectionAndAssert(subscriber);
        AbstractConnectionToChannelBridgeTest.ConnectionInputSubscriber inputSubscriber = connectionHandlerRule.enableConnectionInputSubscriber();
        assertTerminalEvent();
        Assert.assertThat("Unexpected first notification kind.", getOnErrorEvents(), hasSize(1));
    }

    @Test(timeout = 60000)
    public void testEagerContentSubscriptionPass() throws Exception {
        connectionHandlerRule.channel.config().setAutoRead(true);// should mandate eager content subscription

        AbstractConnectionToChannelBridgeTest.ConnectionSubscriber subscriber = connectionHandlerRule.enableConnectionSubscriberAndAssert(true);
        connectionHandlerRule.activateConnectionAndAssert(subscriber);// eagerly subscribes to input.

        AbstractConnectionToChannelBridgeTest.ConnectionInputSubscriber inputSubscriber = subscriber.getInputSubscriber();
        Assert.assertThat("Unexpected notifications count after channel active.", getOnNextEvents(), hasSize(0));
        assertNoErrors();
        Assert.assertThat("Input subscriber is unsubscribed.", isUnsubscribed(), is(false));
    }

    @Test(timeout = 60000)
    public void testLazyContentSubscription() throws Exception {
        connectionHandlerRule.channel.config().setAutoRead(false);
        AbstractConnectionToChannelBridgeTest.ConnectionSubscriber subscriber = connectionHandlerRule.enableConnectionSubscriberAndAssert(false);// lazy input sub.

        connectionHandlerRule.activateConnectionAndAssert(subscriber);
        AbstractConnectionToChannelBridgeTest.ConnectionInputSubscriber inputSubscriber = connectionHandlerRule.enableConnectionInputSubscriber();
        assertNoErrors();
        Assert.assertThat("Unexpected on next events after channel active.", getOnNextEvents(), hasSize(0));
        Assert.assertThat("Unexpected on completed events after channel active.", getOnCompletedEvents(), hasSize(0));
        Assert.assertThat("Input subscriber is unsubscribed.", isUnsubscribed(), is(false));
        connectionHandlerRule.startRead();
        connectionHandlerRule.testSendInputMsgs(inputSubscriber, "hello1");
    }

    @Test(timeout = 60000)
    public void testInputCompleteOnChannelUnregister() throws Exception {
        connectionHandlerRule.channel.config().setAutoRead(false);
        AbstractConnectionToChannelBridgeTest.ConnectionSubscriber subscriber = connectionHandlerRule.enableConnectionSubscriberAndAssert(true);
        connectionHandlerRule.activateConnectionAndAssert(subscriber);
        AbstractConnectionToChannelBridgeTest.ConnectionInputSubscriber inputSubscriber = subscriber.getInputSubscriber();// since sub is eager.

        connectionHandlerRule.startRead();
        connectionHandlerRule.testSendInputMsgs(inputSubscriber, "hello1");
        Assert.assertThat("Unexpected notifications count after channel active.", getOnNextEvents(), hasSize(1));
        unsubscribe();// else channel close will generate error if subscribed

        connectionHandlerRule.handler.channelUnregistered(connectionHandlerRule.ctx);
        assertNoErrors();
        Assert.assertThat("Unexpected notifications count after channel active.", getOnNextEvents(), hasSize(1));
    }

    @Test(timeout = 60000)
    public void testMultipleInputSubscriptions() throws Exception {
        connectionHandlerRule.channel.config().setAutoRead(false);
        AbstractConnectionToChannelBridgeTest.ConnectionSubscriber subscriber = connectionHandlerRule.enableConnectionSubscriberAndAssert(true);
        connectionHandlerRule.activateConnectionAndAssert(subscriber);// one subscription

        AbstractConnectionToChannelBridgeTest.ConnectionInputSubscriber inputSubscriber = connectionHandlerRule.enableConnectionInputSubscriber();
        assertTerminalEvent();
        Assert.assertThat("Unexpected on next events for second subscriber.", getOnNextEvents(), hasSize(0));
        Assert.assertThat("Unexpected notification type for second subscriber.", getOnErrorEvents(), hasSize(1));
    }

    @Test(timeout = 60000)
    public void testInputSubscriptionReset() throws Exception {
        connectionHandlerRule.channel.config().setAutoRead(false);
        AbstractConnectionToChannelBridgeTest.ConnectionSubscriber subscriber = connectionHandlerRule.enableConnectionSubscriberAndAssert(true);
        connectionHandlerRule.activateConnectionAndAssert(subscriber);// one subscription

        AbstractConnectionToChannelBridgeTest.ConnectionInputSubscriber inputSubscriber = connectionHandlerRule.enableConnectionInputSubscriber();
        assertTerminalEvent();
        Assert.assertThat("Unexpected on next events for second subscriber.", getOnNextEvents(), hasSize(0));
        connectionHandlerRule.handler.userEventTriggered(connectionHandlerRule.ctx, new ConnectionInputSubscriberResetEvent() {});
        inputSubscriber = connectionHandlerRule.enableConnectionInputSubscriber();
        Assert.assertThat("Unexpected on next count for input subscriber post reset.", getOnNextEvents(), hasSize(0));
        Assert.assertThat("Unexpected on error count for input subscriber post reset.", getOnErrorEvents(), hasSize(0));
        Assert.assertThat("Unexpected on completed count for input subscriber post reset.", getOnCompletedEvents(), hasSize(0));
    }

    @Test(timeout = 60000)
    public void testErrorBeforeConnectionActive() throws Exception {
        AbstractConnectionToChannelBridgeTest.ConnectionSubscriber subscriber = connectionHandlerRule.enableConnectionSubscriberAndAssert(true);
        final NullPointerException exception = new NullPointerException();
        connectionHandlerRule.handler.exceptionCaught(connectionHandlerRule.ctx, exception);
        assertTerminalEvent();
        Assert.assertThat("Unexpected on next notifications count post exception.", getOnNextEvents(), hasSize(0));
        Assert.assertThat("Unexpected notification type post exception.", getOnErrorEvents(), hasSize(1));
    }

    @Test(timeout = 60000)
    public void testErrorPostInputSubscribe() throws Exception {
        AbstractConnectionToChannelBridgeTest.ConnectionSubscriber subscriber = connectionHandlerRule.enableConnectionSubscriberAndAssert(true);
        connectionHandlerRule.activateConnectionAndAssert(subscriber);
        AbstractConnectionToChannelBridgeTest.ConnectionInputSubscriber inputSubscriber = subscriber.getInputSubscriber();// since sub is eager.

        Assert.assertThat("Unexpected on next notifications count pre exception.", getOnNextEvents(), hasSize(0));
        Assert.assertThat("Unexpected on error notifications count pre exception.", getOnErrorEvents(), hasSize(0));
        Assert.assertThat("Unexpected on completed notifications count pre exception.", getOnCompletedEvents(), hasSize(0));
        final NullPointerException exception = new NullPointerException();
        connectionHandlerRule.handler.exceptionCaught(connectionHandlerRule.ctx, exception);
        assertTerminalEvent();
        Assert.assertThat("Unexpected on next notifications count post exception.", getOnNextEvents(), hasSize(0));
        Assert.assertThat("Unexpected on error notifications count post exception.", getOnErrorEvents(), hasSize(1));
    }

    public static class ConnectionHandlerRule extends ExternalResource {
        private Channel channel;

        private ChannelHandlerContext ctx;

        private AbstractConnectionToChannelBridge<String, String> handler;

        @Override
        public Statement apply(final Statement base, Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    channel = new io.netty.channel.embedded.EmbeddedChannel(new ChannelDuplexHandler());
                    ctx = channel.pipeline().firstContext();
                    handler = new AbstractConnectionToChannelBridge<String, String>("foo", new ConnectionEventListener() {}, MockEventPublisher.disabled()) {};
                    base.evaluate();
                }
            };
        }

        public void startRead() throws Exception {
            handler.userEventTriggered(ctx, new RequestReadIfRequiredEvent() {
                @Override
                protected boolean shouldReadMore(ChannelHandlerContext ctx) {
                    return true;
                }
            });
        }

        public AbstractConnectionToChannelBridgeTest.ConnectionSubscriber enableConnectionSubscriberAndAssert(boolean eagerSubToInput) throws Exception {
            AbstractConnectionToChannelBridgeTest.ConnectionSubscriber toReturn = new AbstractConnectionToChannelBridgeTest.ConnectionSubscriber(eagerSubToInput, this);
            handler.userEventTriggered(ctx, new ChannelSubscriberEvent(toReturn));
            Assert.assertThat("Unexpected on next notifications count before channel active.", getOnNextEvents(), hasSize(0));
            Assert.assertThat("Unexpected on error notifications count before channel active.", getOnErrorEvents(), hasSize(0));
            Assert.assertThat("Unexpected on complete notifications count before channel active.", getOnCompletedEvents(), hasSize(0));
            return toReturn;
        }

        public AbstractConnectionToChannelBridgeTest.ConnectionInputSubscriber enableConnectionInputSubscriber() throws Exception {
            AbstractConnectionToChannelBridgeTest.ConnectionInputSubscriber toReturn = new AbstractConnectionToChannelBridgeTest.ConnectionInputSubscriber();
            handler.userEventTriggered(ctx, new ConnectionInputSubscriberEvent(toReturn));
            return toReturn;
        }

        public void activateConnectionAndAssert(AbstractConnectionToChannelBridgeTest.ConnectionSubscriber subscriber) throws Exception {
            handler.userEventTriggered(ctx, INSTANCE);
            assertTerminalEvent();
            assertNoErrors();
            Assert.assertThat("No connections received.", getOnNextEvents(), is(not(empty())));
            Assert.assertThat("Unexpected channel in new connection.", getOnNextEvents().get(0), is(channel));
        }

        public void testSendInputMsgs(AbstractConnectionToChannelBridgeTest.ConnectionInputSubscriber inputSubscriber, String... msgs) throws Exception {
            for (String msg : msgs) {
                handler.channelRead(ctx, msg);
            }
            Assert.assertThat("Unexpected notifications count after read.", getOnNextEvents(), hasSize(msgs.length));
            Assert.assertThat("Unexpected notifications count after read.", getOnNextEvents(), contains(msgs));
            Assert.assertThat("Input subscriber is unsubscribed after read.", isUnsubscribed(), is(false));
        }
    }

    public static class ConnectionSubscriber extends TestSubscriber<Channel> {
        private final boolean subscribeToInput;

        private final AbstractConnectionToChannelBridgeTest.ConnectionHandlerRule rule;

        private AbstractConnectionToChannelBridgeTest.ConnectionInputSubscriber inputSubscriber;

        public ConnectionSubscriber(boolean subscribeToInput, AbstractConnectionToChannelBridgeTest.ConnectionHandlerRule rule) {
            this.subscribeToInput = subscribeToInput;
            this.rule = rule;
        }

        @Override
        public void onNext(Channel channel) {
            super.onNext(channel);
            try {
                if (subscribeToInput) {
                    inputSubscriber = rule.enableConnectionInputSubscriber();
                }
            } catch (Exception e) {
                onError(e);
            }
        }

        public AbstractConnectionToChannelBridgeTest.ConnectionInputSubscriber getInputSubscriber() {
            return inputSubscriber;
        }
    }

    public static class ConnectionInputSubscriber extends TestSubscriber<String> {
        private final long requestAtStart;

        public ConnectionInputSubscriber() {
            this(Long.MAX_VALUE);
        }

        public ConnectionInputSubscriber(long requestAtStart) {
            this.requestAtStart = requestAtStart;
        }

        @Override
        public void onStart() {
            request(requestAtStart);
        }
    }
}

