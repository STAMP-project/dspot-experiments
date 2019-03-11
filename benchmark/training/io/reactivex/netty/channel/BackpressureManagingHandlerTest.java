/**
 * Copyright 2016 Netflix, Inc.
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


import BytesWriteInterceptor.WRITE_INSPECTOR_HANDLER_NAME;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.logging.LoggingHandler;
import io.reactivex.netty.channel.BackpressureManagingHandler.RequestReadIfRequiredEvent;
import io.reactivex.netty.test.util.InboundRequestFeeder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mockito.Mockito;
import rx.Observable;


public class BackpressureManagingHandlerTest {
    @Rule
    public final BackpressureManagingHandlerTest.HandlerRule handlerRule = new BackpressureManagingHandlerTest.HandlerRule();

    @Test(timeout = 60000)
    public void testExactDemandAndSupply() throws Exception {
        MatcherAssert.assertThat("Unexpected handler state.", getCurrentState(), is(State.Buffering));
        final String msg1 = "hello1";
        final String msg2 = "hello2";
        handlerRule.feedMessagesForRead(msg1, msg2);/* Exact supply */

        handlerRule.setMaxMessagesPerRead(2);/* Send all msgs in one iteration */

        handlerRule.requestMessages(2);/* Exact demand */

        MatcherAssert.assertThat("Unexpected read requested count.", handlerRule.getReadRequestedCount(), is(1));
        handlerRule.assertMessagesReceived(msg1, msg2);
        /* Since, the demand is met (requested 2 and got 2) , we move to buffering. */
        MatcherAssert.assertThat("Unexpected handler state.", getCurrentState(), is(State.Buffering));
    }

    @Test(timeout = 60000)
    public void testExactDemandAndSupplyMultiRequests() throws Exception {
        MatcherAssert.assertThat("Unexpected handler state.", getCurrentState(), is(State.Buffering));
        final String msg1 = "hello1";
        final String msg2 = "hello2";
        handlerRule.feedMessagesForRead(msg1, msg2);/* Exact supply */

        handlerRule.setMaxMessagesPerRead(2);/* Send all msgs in one iteration */

        handlerRule.requestMessages(2);/* Exact demand */

        MatcherAssert.assertThat("Unexpected read requested count.", handlerRule.getReadRequestedCount(), is(1));
        handlerRule.assertMessagesReceived(msg1, msg2);
        /* Since, the demand is met (requested 2 and got 2) , we move to buffering. */
        MatcherAssert.assertThat("Unexpected handler state.", getCurrentState(), is(State.Buffering));
        handlerRule.resetReadCount();
        MatcherAssert.assertThat("Unexpected read requested count post reset.", handlerRule.getReadRequestedCount(), is(0));
        handlerRule.handler.reset();
        final String msg3 = "hello3";
        handlerRule.feedMessagesForRead(msg3);
        /* No demand, no read fired */
        MatcherAssert.assertThat("Unexpected read requested count post reset.", handlerRule.getReadRequestedCount(), is(0));
        handlerRule.assertMessagesReceived();
        handlerRule.requestMessages(1);
        /* Read on demand */
        MatcherAssert.assertThat("Unexpected read requested count.", handlerRule.getReadRequestedCount(), is(1));
        handlerRule.assertMessagesReceived(msg3);
        /* Since, the demand is met (requested 3 and got 3) , we move to buffering. */
        MatcherAssert.assertThat("Unexpected handler state.", getCurrentState(), is(State.Reading));
    }

    @Test(timeout = 60000)
    public void testMoreDemand() throws Exception {
        MatcherAssert.assertThat("Unexpected handler state.", getCurrentState(), is(State.Buffering));
        final String msg1 = "hello1";
        final String msg2 = "hello2";
        handlerRule.feedMessagesForRead(msg1, msg2);/* less supply */

        handlerRule.setMaxMessagesPerRead(2);/* Send all msgs in one iteration */

        handlerRule.requestMessages(4);/* More demand */

        /* One read for start and one when the supply completed but demand exists. */
        MatcherAssert.assertThat("Unexpected read requested count.", handlerRule.getReadRequestedCount(), is(2));
        handlerRule.assertMessagesReceived(msg1, msg2);
        /* Since, the demand is not met (requested 4 but got 2) , stay in read requested. */
        MatcherAssert.assertThat("Unexpected handler state.", getCurrentState(), is(State.ReadRequested));
    }

    @Test(timeout = 60000)
    public void testMoreSupply() throws Exception {
        MatcherAssert.assertThat("Unexpected handler state.", getCurrentState(), is(State.Buffering));
        final String msg1 = "hello1";
        final String msg2 = "hello2";
        final String msg3 = "hello3";
        handlerRule.feedMessagesForRead(msg1, msg2, msg3);/* more supply */

        handlerRule.setMaxMessagesPerRead(3);/* Send all msgs in one iteration */

        handlerRule.requestMessages(2);/* less demand */

        /* One read for start. */
        MatcherAssert.assertThat("Unexpected read requested count.", handlerRule.getReadRequestedCount(), is(1));
        handlerRule.assertMessagesReceived(msg1, msg2);
        /* Since, the demand was met (requested 2 and got 2) , but the supply was more (3), we should be buffering. */
        MatcherAssert.assertThat("Unexpected handler state.", getCurrentState(), is(State.Buffering));
        MatcherAssert.assertThat("Unexpected buffer size.", getBuffer(), hasSize(1));
        MatcherAssert.assertThat("Unexpected buffer contents.", getBuffer(), contains(((Object) (msg3))));
        MatcherAssert.assertThat("Unexpected buffer read index.", getCurrentBufferIndex(), is(0));
    }

    @Test(timeout = 60000)
    public void testBufferDrainSingleIteration() throws Exception {
        MatcherAssert.assertThat("Unexpected handler state.", getCurrentState(), is(State.Buffering));
        final String msg1 = "hello1";
        final String msg2 = "hello2";
        final String msg3 = "hello3";
        handlerRule.feedMessagesForRead(msg1, msg2, msg3);/* more supply */

        handlerRule.setMaxMessagesPerRead(3);/* Send all msgs in one iteration & cause buffer */

        handlerRule.requestMessages(2);/* less demand */

        /* One read for start. */
        MatcherAssert.assertThat("Unexpected read requested count.", handlerRule.getReadRequestedCount(), is(1));
        handlerRule.assertMessagesReceived(msg1, msg2);
        /* Since, the demand was met (requested 2 and got 2) , but the supply was more (3), we should be buffering. */
        MatcherAssert.assertThat("Unexpected handler state.", getCurrentState(), is(State.Buffering));
        MatcherAssert.assertThat("Unexpected buffer size.", getBuffer(), hasSize(1));
        MatcherAssert.assertThat("Unexpected buffer contents.", getBuffer(), contains(((Object) (msg3))));
        MatcherAssert.assertThat("Unexpected buffer read index.", getCurrentBufferIndex(), is(0));
        handlerRule.resetReadCount();
        MatcherAssert.assertThat("Unexpected read requested count post reset.", handlerRule.getReadRequestedCount(), is(0));
        handlerRule.handler.reset();
        handlerRule.requestMessages(1);/* Should come from the buffer. */

        MatcherAssert.assertThat("Unexpected read requested when expected to be fed from buffer.", handlerRule.getReadRequestedCount(), is(0));
        handlerRule.assertMessagesReceived(msg3);
        /* Since, the demand is now met (requested 3 and got 3) , we move to buffering. */
        MatcherAssert.assertThat("Unexpected handler state.", getCurrentState(), is(State.Buffering));
        MatcherAssert.assertThat("Unexpected buffer size.", getBuffer(), is(nullValue()));
        MatcherAssert.assertThat("Unexpected buffer read index.", getCurrentBufferIndex(), is(0));
    }

    @Test(timeout = 60000)
    public void testBufferDrainMultiIteration() throws Exception {
        MatcherAssert.assertThat("Unexpected handler state.", getCurrentState(), is(State.Buffering));
        final String msg1 = "hello1";
        final String msg2 = "hello2";
        final String msg3 = "hello3";
        final String msg4 = "hello4";
        handlerRule.feedMessagesForRead(msg1, msg2, msg3, msg4);/* more supply */

        handlerRule.setMaxMessagesPerRead(4);/* Send all msgs in one iteration & cause buffer */

        handlerRule.requestMessages(2);/* less demand */

        /* One read for start. */
        MatcherAssert.assertThat("Unexpected read requested count.", handlerRule.getReadRequestedCount(), is(1));
        handlerRule.assertMessagesReceived(msg1, msg2);
        /* Since, the demand was met (requested 2 and got 2) , but the supply was more (4), we should be buffering. */
        MatcherAssert.assertThat("Unexpected handler state.", getCurrentState(), is(State.Buffering));
        MatcherAssert.assertThat("Unexpected buffer size.", getBuffer(), hasSize(2));
        MatcherAssert.assertThat("Unexpected buffer contents.", getBuffer(), contains(((Object) (msg3)), msg4));
        MatcherAssert.assertThat("Unexpected buffer read index.", getCurrentBufferIndex(), is(0));
        /* Reset read state before next read */
        handlerRule.resetReadCount();
        MatcherAssert.assertThat("Unexpected read requested count post reset.", handlerRule.getReadRequestedCount(), is(0));
        handlerRule.handler.reset();
        handlerRule.requestMessages(1);/* Should come from the buffer. */

        MatcherAssert.assertThat("Unexpected read requested when expected to be fed from buffer.", handlerRule.getReadRequestedCount(), is(0));
        handlerRule.assertMessagesReceived(msg3);
        /* Since, the demand is now met (requested 3 and got 3) , we move to buffering. */
        MatcherAssert.assertThat("Unexpected handler state.", getCurrentState(), is(State.Buffering));
        /* Buffer does not change till it has data */
        MatcherAssert.assertThat("Unexpected buffer size.", getBuffer(), hasSize(2));
        /* Buffer reader index changes till it has data */
        MatcherAssert.assertThat("Unexpected buffer read index.", getCurrentBufferIndex(), is(1));
        /* Reset read state before next read */
        handlerRule.resetReadCount();
        MatcherAssert.assertThat("Unexpected read requested count post reset.", handlerRule.getReadRequestedCount(), is(0));
        handlerRule.handler.reset();
        handlerRule.requestMessages(1);/* Should come from the buffer. */

        MatcherAssert.assertThat("Unexpected read requested when expected to be fed from buffer.", handlerRule.getReadRequestedCount(), is(0));
        handlerRule.assertMessagesReceived(msg4);
        /* Since, the demand is now met (requested 4 and got 4) , we move to buffering. */
        MatcherAssert.assertThat("Unexpected handler state.", getCurrentState(), is(State.Buffering));
        MatcherAssert.assertThat("Unexpected buffer size.", getBuffer(), is(nullValue()));
        MatcherAssert.assertThat("Unexpected buffer read index.", getCurrentBufferIndex(), is(0));
    }

    @Test(timeout = 60000)
    public void testBufferDrainWithMoreDemand() throws Exception {
        MatcherAssert.assertThat("Unexpected handler state.", getCurrentState(), is(State.Buffering));
        final String msg1 = "hello1";
        final String msg2 = "hello2";
        final String msg3 = "hello3";
        handlerRule.feedMessagesForRead(msg1, msg2, msg3);/* more supply */

        handlerRule.setMaxMessagesPerRead(3);/* Send all msgs in one iteration & cause buffer */

        handlerRule.requestMessages(2);/* less demand */

        /* One read for start. */
        MatcherAssert.assertThat("Unexpected read requested count.", handlerRule.getReadRequestedCount(), is(1));
        handlerRule.assertMessagesReceived(msg1, msg2);
        /* Since, the demand was met (requested 2 and got 2) , but the supply was more (3), we should be buffering. */
        MatcherAssert.assertThat("Unexpected handler state.", getCurrentState(), is(State.Buffering));
        MatcherAssert.assertThat("Unexpected buffer size.", getBuffer(), hasSize(1));
        MatcherAssert.assertThat("Unexpected buffer contents.", getBuffer(), contains(((Object) (msg3))));
        MatcherAssert.assertThat("Unexpected buffer read index.", getCurrentBufferIndex(), is(0));
        handlerRule.resetReadCount();
        MatcherAssert.assertThat("Unexpected read requested count post reset.", handlerRule.getReadRequestedCount(), is(0));
        handlerRule.handler.reset();
        handlerRule.requestMessages(2);/* Should come from the buffer. */

        /* Since demand can not be fulfilled by the buffer, a read should be requested. */
        MatcherAssert.assertThat("Unexpected read requested.", handlerRule.getReadRequestedCount(), is(1));
        handlerRule.assertMessagesReceived(msg3);
        /* Since, the demand is now met (requested 3 and got 3) , we move to buffering. */
        MatcherAssert.assertThat("Unexpected handler state.", getCurrentState(), is(State.ReadRequested));
        MatcherAssert.assertThat("Unexpected buffer size.", getBuffer(), is(nullValue()));
        MatcherAssert.assertThat("Unexpected buffer read index.", getCurrentBufferIndex(), is(0));
    }

    @Test(timeout = 60000)
    public void testBufferDrainOnRemove() throws Exception {
        MatcherAssert.assertThat("Unexpected handler state.", getCurrentState(), is(State.Buffering));
        final ByteBuf msg1 = Unpooled.buffer().writeBytes("hello1".getBytes());
        final ByteBuf msg2 = Unpooled.buffer().writeBytes("hello2".getBytes());
        handlerRule.feedMessagesForRead(msg1, msg2);/* More supply then demand */

        handlerRule.setMaxMessagesPerRead(2);/* Send all msgs in one iteration and cause buffer */

        handlerRule.requestMessages(1);/* Less demand */

        MatcherAssert.assertThat("Unexpected read requested count.", handlerRule.getReadRequestedCount(), is(1));
        handlerRule.assertMessagesReceived(msg1);
        /* Since, the demand is met (requested 1 and got 1) , we move to buffering. */
        MatcherAssert.assertThat("Unexpected handler state.", getCurrentState(), is(State.Buffering));
        MatcherAssert.assertThat("Unexpected buffer size.", getBuffer(), hasSize(1));
        MatcherAssert.assertThat("Unexpected buffer contents.", getBuffer(), contains(((Object) (msg2))));
        MatcherAssert.assertThat("Unexpected buffer read index.", getCurrentBufferIndex(), is(0));
        handlerRule.channel.close();// Should remove handler.

        handlerRule.channel.runPendingTasks();
        MatcherAssert.assertThat("Unexpected handler state.", getCurrentState(), is(State.Stopped));
        MatcherAssert.assertThat("Unexpected buffer size.", getBuffer(), is(nullValue()));
        MatcherAssert.assertThat("Unexpected buffer read index.", getCurrentBufferIndex(), is(0));
        MatcherAssert.assertThat("Buffered item not released.", msg2.refCnt(), is(0));
    }

    @Test(timeout = 60000)
    public void testDiscardReadWhenStopped() throws Exception {
        MatcherAssert.assertThat("Unexpected handler state.", getCurrentState(), is(State.Buffering));
        handlerRule.channel.close();// Should remove handler.

        handlerRule.channel.runPendingTasks();
        MatcherAssert.assertThat("Unexpected handler state.", getCurrentState(), is(State.Stopped));
        final ByteBuf msg = Unpooled.buffer().writeBytes("Hello".getBytes());
        handlerRule.handler.channelRead(Mockito.mock(ChannelHandlerContext.class), msg);
        MatcherAssert.assertThat("Message not released when stopped.", msg.refCnt(), is(0));
    }

    @Test(timeout = 60000)
    public void testWriteWithBufferingHandler() throws Exception {
        BackpressureManagingHandlerTest.BufferingHandler bufferingHandler = new BackpressureManagingHandlerTest.BufferingHandler();
        handlerRule.channel.pipeline().addBefore(WRITE_INSPECTOR_HANDLER_NAME, "buffering-handler", bufferingHandler);
        final String[] dataToWrite = new String[]{ "Hello1", "Hello2" };
        handlerRule.channel.writeAndFlush(Observable.from(dataToWrite));/* Using Observable.from() to enable backpressure. */

        MatcherAssert.assertThat("Messages written to the channel, inspite of buffering", handlerRule.channel.outboundMessages(), is(empty()));
        /* Inspite of the messages, not reaching the channel, the extra demand should be generated and the buffering
        handler should contain all messages.
         */
        MatcherAssert.assertThat("Unexpected buffer size in buffering handler.", bufferingHandler.buffer, hasSize(2));
    }

    public static class HandlerRule extends ExternalResource {
        private BackpressureManagingHandlerTest.MockBackpressureManagingHandler handler;

        private EmbeddedChannel channel;

        private InboundRequestFeeder inboundRequestFeeder;

        private FixedRecvByteBufAllocator recvByteBufAllocator;

        @Override
        public Statement apply(final Statement base, Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    inboundRequestFeeder = new InboundRequestFeeder();
                    channel = new EmbeddedChannel(new LoggingHandler());
                    String bpName = "backpressure-manager";
                    channel.pipeline().addFirst(bpName, (handler = new BackpressureManagingHandlerTest.MockBackpressureManagingHandler(bpName)));
                    channel.pipeline().addBefore(bpName, "primitive-converter", new WriteTransformer());
                    channel.pipeline().addFirst(inboundRequestFeeder);
                    channel.config().setAutoRead(false);
                    recvByteBufAllocator = new FixedRecvByteBufAllocator(1024);
                    channel.config().setRecvByteBufAllocator(recvByteBufAllocator);
                    base.evaluate();
                }
            };
        }

        public void setMaxMessagesPerRead(int maxMessagesPerRead) {
            recvByteBufAllocator.maxMessagesPerRead(maxMessagesPerRead);
        }

        public void assertMessagesReceived(Object... expected) {
            final List<Object> msgsReceived = handler.getMsgsReceived();
            if ((null != expected) && ((expected.length) > 0)) {
                MatcherAssert.assertThat("Unexpected messages received count.", msgsReceived, hasSize(expected.length));
                MatcherAssert.assertThat("Unexpected messages received.", msgsReceived, contains(expected));
            } else {
                MatcherAssert.assertThat("Unexpected messages received.", msgsReceived, is(empty()));
            }
        }

        public int resetReadCount() {
            return inboundRequestFeeder.resetReadRequested();
        }

        public int getReadRequestedCount() {
            return inboundRequestFeeder.getReadRequestedCount();
        }

        public void requestMessages(long requested) throws Exception {
            handler.incrementRequested(requested);
            channel.pipeline().fireUserEventTriggered(new RequestReadIfRequiredEvent() {
                @Override
                protected boolean shouldReadMore(ChannelHandlerContext ctx) {
                    return true;
                }
            });
            channel.runPendingTasks();
        }

        public void feedMessagesForRead(Object... msgs) {
            inboundRequestFeeder.addToTheFeed(msgs);
        }
    }

    private static class MockBackpressureManagingHandler extends BackpressureManagingHandler {
        private final List<Object> msgsReceived = new ArrayList<>();

        private final AtomicLong requested = new AtomicLong();

        protected MockBackpressureManagingHandler(String thisHandlerName) {
            super(thisHandlerName);
        }

        @Override
        protected void newMessage(ChannelHandlerContext ctx, Object msg) {
            requested.decrementAndGet();
            msgsReceived.add(msg);
        }

        @Override
        protected boolean shouldReadMore(ChannelHandlerContext ctx) {
            return (requested.get()) > 0;
        }

        public List<Object> getMsgsReceived() {
            return msgsReceived;
        }

        public void reset() {
            msgsReceived.clear();
            requested.set(0);
        }

        public void incrementRequested(long requested) {
            this.requested.addAndGet(requested);
        }
    }

    private static class BufferingHandler extends ChannelOutboundHandlerAdapter {
        private final List<Object> buffer = new ArrayList<>();

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            buffer.add(msg);
        }
    }
}

