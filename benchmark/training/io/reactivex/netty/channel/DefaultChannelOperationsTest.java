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


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.FileRegion;
import io.netty.channel.embedded.EmbeddedChannel;
import io.reactivex.netty.test.util.FlushSelector;
import io.reactivex.netty.test.util.MockEventPublisher;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import rx.Observable;
import rx.functions.Action1;
import rx.observers.TestSubscriber;


public class DefaultChannelOperationsTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Rule
    public final DefaultChannelOperationsTest.ChannelOpRule channelOpRule = new DefaultChannelOperationsTest.ChannelOpRule();

    @Test(timeout = 60000)
    public void testWrite() throws Exception {
        final String msg = "Hello";
        Observable<Void> writeO = channelOpRule.channelOperations.write(DefaultChannelOperationsTest.ChannelOpRule.bbJust(msg));
        _testWrite(writeO, msg);
    }

    @Test(timeout = 60000)
    public void testWriteWithFlushSelector() throws Exception {
        final String msg1 = "Hello1";
        final String msg2 = "Hello2";
        Observable<Void> writeO = channelOpRule.channelOperations.write(DefaultChannelOperationsTest.ChannelOpRule.bbJust(msg1, msg2), new FlushSelector<ByteBuf>(1));
        _testWithFlushSelector(writeO, msg1, msg2);
    }

    @Test(timeout = 60000)
    public void testWriteAndFlushOnEach() throws Exception {
        final String msg1 = "Hello1";
        final String msg2 = "Hello2";
        Observable<Void> writeO = channelOpRule.channelOperations.writeAndFlushOnEach(DefaultChannelOperationsTest.ChannelOpRule.bbJust(msg1, msg2));
        _testWithFlushSelector(writeO, msg1, msg2);
    }

    @Test(timeout = 60000)
    public void testWriteString() throws Exception {
        final String msg = "Hello";
        Observable<Void> writeO = channelOpRule.channelOperations.writeString(Observable.just(msg));
        _testWrite(writeO, msg);
    }

    @Test(timeout = 60000)
    public void testWriteStringWithFlushSelector() throws Exception {
        final String msg1 = "Hello1";
        final String msg2 = "Hello2";
        Observable<Void> writeO = channelOpRule.channelOperations.writeString(Observable.just(msg1, msg2), new FlushSelector<String>(1));
        _testWithFlushSelector(writeO, msg1, msg2);
    }

    @Test(timeout = 60000)
    public void testWriteStringAndFlushOnEach() throws Exception {
        final String msg1 = "Hello1";
        final String msg2 = "Hello2";
        Observable<Void> writeO = channelOpRule.channelOperations.writeStringAndFlushOnEach(Observable.just(msg1, msg2));
        _testWithFlushSelector(writeO, msg1, msg2);
    }

    @Test(timeout = 60000)
    public void testWriteBytes() throws Exception {
        final String msg = "Hello";
        Observable<Void> writeO = channelOpRule.channelOperations.writeBytes(Observable.just(msg.getBytes()));
        _testWrite(writeO, msg);
    }

    @Test(timeout = 60000)
    public void testWriteBytesWithFlushSelector() throws Exception {
        final String msg1 = "Hello1";
        final String msg2 = "Hello2";
        Observable<Void> writeO = channelOpRule.channelOperations.writeBytes(Observable.just(msg1.getBytes(), msg2.getBytes()), new FlushSelector<byte[]>(1));
        _testWithFlushSelector(writeO, msg1, msg2);
    }

    @Test(timeout = 60000)
    public void testWriteBytesAndFlushOnEach() throws Exception {
        final String msg1 = "Hello1";
        final String msg2 = "Hello2";
        Observable<Void> writeO = channelOpRule.channelOperations.writeBytesAndFlushOnEach(Observable.just(msg1.getBytes(), msg2.getBytes()));
        _testWithFlushSelector(writeO, msg1, msg2);
    }

    @Test(timeout = 60000)
    public void testWriteFileRegion() throws Exception {
        FileRegion msg = new DefaultFileRegion(folder.newFile("msg.txt"), 0, 0);
        Observable<Void> writeO = channelOpRule.channelOperations.writeFileRegion(Observable.just(msg));
        _testWrite(writeO, msg);
    }

    @Test(timeout = 60000)
    public void testWriteFileRegionWithFlushSelector() throws Exception {
        FileRegion msg1 = new DefaultFileRegion(folder.newFile("msg1.txt"), 0, 0);
        FileRegion msg2 = new DefaultFileRegion(folder.newFile("msg2.txt"), 0, 0);
        Observable<Void> writeO = channelOpRule.channelOperations.writeFileRegion(Observable.just(msg1, msg2), new FlushSelector<FileRegion>(1));
        _testWithFlushSelector(writeO, msg1, msg2);
    }

    @Test(timeout = 60000)
    public void testWriteFileRegionAndFlushOnEach() throws Exception {
        FileRegion msg1 = new DefaultFileRegion(folder.newFile("msg1.txt"), 0, 0);
        FileRegion msg2 = new DefaultFileRegion(folder.newFile("msg2.txt"), 0, 0);
        Observable<Void> writeO = channelOpRule.channelOperations.writeFileRegionAndFlushOnEach(Observable.just(msg1, msg2));
        _testWithFlushSelector(writeO, msg1, msg2);
    }

    @Test(timeout = 60000)
    public void testFlush() throws Exception {
        String msg = "Hello";
        channelOpRule.channel.write(Unpooled.buffer().writeBytes(msg.getBytes()));
        channelOpRule.channelOperations.flush();
        channelOpRule.verifyOutboundMessages(msg);
    }

    @Test(timeout = 60000)
    public void testCloseWithFlush() throws Exception {
        TestSubscriber<Void> subscriber = new TestSubscriber();
        channelOpRule.channelOperations.close().subscribe(subscriber);
        subscriber.assertTerminalEvent();
        subscriber.assertNoErrors();
        MatcherAssert.assertThat("Channel not closed.", channelOpRule.channel.isOpen(), is(false));
    }

    @Test(timeout = 60000)
    public void testCloseWithoutFlush() throws Exception {
        TestSubscriber<Void> subscriber = new TestSubscriber();
        channelOpRule.channel.write("Hello");
        channelOpRule.channelOperations.close(false).subscribe(subscriber);
        subscriber.assertTerminalEvent();
        subscriber.assertNoErrors();
        channelOpRule.verifyOutboundMessages();
        MatcherAssert.assertThat("Channel not closed.", channelOpRule.channel.isOpen(), is(false));
    }

    @Test(timeout = 60000)
    public void testCloseListener() throws Exception {
        Observable<Void> closeListener = channelOpRule.channelOperations.closeListener();
        TestSubscriber<Void> subscriber = new TestSubscriber();
        closeListener.subscribe(subscriber);
        subscriber.assertNoTerminalEvent();
        subscriber.unsubscribe();
        subscriber.assertNoTerminalEvent();
        channelOpRule.channel.close().sync();
        subscriber.assertNoTerminalEvent();
    }

    public static class ChannelOpRule extends ExternalResource {
        private DefaultChannelOperations<ByteBuf> channelOperations;

        private EmbeddedChannel channel;

        private List<DefaultChannelOperationsTest.ChannelOpRule.TestWriteSubscriber> writeObservableSubscribers;

        @Override
        public Statement apply(final Statement base, Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    writeObservableSubscribers = new ArrayList<>();
                    /* Since, the appropriate handler is not added to the pipeline that handles O<> writes. */
                    channel = new EmbeddedChannel(new DefaultChannelOperationsTest.ChannelOpRule.HandleObservableWrite(writeObservableSubscribers));
                    channelOperations = new DefaultChannelOperations(channel, null, MockEventPublisher.disabled());
                    base.evaluate();
                }
            };
        }

        public static Observable<ByteBuf> bbJust(String... items) {
            List<ByteBuf> bbItems = new ArrayList<>();
            for (String item : items) {
                bbItems.add(Unpooled.buffer().writeBytes(item.getBytes()));
            }
            return Observable.from(bbItems);
        }

        public void verifyOutboundMessages(Object... msgs) {
            boolean stringConversionRequired = ((msgs != null) && ((msgs.length) != 0)) && ((msgs[0]) instanceof String);
            final List<Object> outMsgsToTest = new ArrayList<>();
            for (Object next : channel.outboundMessages()) {
                if (stringConversionRequired) {
                    if (next instanceof ByteBuf) {
                        outMsgsToTest.add(((ByteBuf) (next)).toString(Charset.defaultCharset()));
                    }
                } else {
                    outMsgsToTest.add(next);
                }
            }
            if ((null == msgs) || ((msgs.length) == 0)) {
                MatcherAssert.assertThat("Unexpected messages written on the channel.", outMsgsToTest, is(empty()));
            } else {
                MatcherAssert.assertThat("Unexpected messages written on the channel.", outMsgsToTest, contains(msgs));
            }
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        private static class HandleObservableWrite extends ChannelDuplexHandler {
            private final List<DefaultChannelOperationsTest.ChannelOpRule.TestWriteSubscriber> writeObservableSubscribers;

            public HandleObservableWrite(List<DefaultChannelOperationsTest.ChannelOpRule.TestWriteSubscriber> writeObservableSubscribers) {
                this.writeObservableSubscribers = writeObservableSubscribers;
            }

            @Override
            public void write(final ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                if (msg instanceof Observable) {
                    Observable msgO = ((Observable) (msg));
                    final DefaultChannelOperationsTest.ChannelOpRule.TestWriteSubscriber testSubscriber = new DefaultChannelOperationsTest.ChannelOpRule.TestWriteSubscriber(promise);
                    msgO.doOnNext(new Action1() {
                        @Override
                        public void call(Object o) {
                            final ChannelPromise channelPromise = ctx.newPromise();
                            testSubscriber.allPromises.add(channelPromise);
                            if (o instanceof String) {
                                o = Unpooled.buffer().writeBytes(((String) (o)).getBytes());
                            } else
                                if (o instanceof byte[]) {
                                    o = Unpooled.buffer().writeBytes(((byte[]) (o)));
                                }

                            ctx.write(o, channelPromise);
                        }
                    }).doOnError(new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            ctx.fireExceptionCaught(throwable);
                        }
                    }).subscribe(testSubscriber);
                    writeObservableSubscribers.add(testSubscriber);
                } else {
                    super.write(ctx, msg, promise);
                }
            }
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        private static class TestWriteSubscriber extends TestSubscriber {
            private final List<ChannelPromise> allPromises = new ArrayList<>();

            private final ChannelPromise overarchingPromise;

            public TestWriteSubscriber(ChannelPromise promise) {
                overarchingPromise = promise;
            }

            public void finishOverarchingWritePromiseIfAllPromisesFinished() {
                for (ChannelPromise aPromise : allPromises) {
                    if (aPromise.isDone()) {
                        if (!(aPromise.isSuccess())) {
                            overarchingPromise.tryFailure(aPromise.cause());
                            return;
                        }
                    } else {
                        overarchingPromise.tryFailure(new IllegalStateException("A write promise did not complete."));
                        return;
                    }
                }
                overarchingPromise.trySuccess();
            }

            @Override
            public void onStart() {
                request(1);
            }
        }
    }
}

