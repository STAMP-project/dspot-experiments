/**
 * Copyright (c) 2011-2018 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package io.vertx.core.eventbus;


import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.impl.VertxInternal;
import io.vertx.test.core.TestUtils;
import io.vertx.test.core.VertxTestBase;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import org.junit.Test;


/**
 *
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class ClusteredEventBusTest extends ClusteredEventBusTestBase {
    @Test
    public void testLocalHandlerNotReceive() throws Exception {
        startNodes(2);
        vertices[1].eventBus().localConsumer(ClusteredEventBusTestBase.ADDRESS1).handler(( msg) -> {
            fail("Should not receive message");
        });
        vertices[0].eventBus().send(ClusteredEventBusTestBase.ADDRESS1, "foo");
        vertices[0].setTimer(1000, ( id) -> testComplete());
        await();
    }

    @Test
    public void testDecoderSendAsymmetric() throws Exception {
        startNodes(2);
        MessageCodec codec = new EventBusTestBase.MyPOJOEncoder1();
        vertices[0].eventBus().registerCodec(codec);
        vertices[1].eventBus().registerCodec(codec);
        String str = TestUtils.randomAlphaString(100);
        testSend(new EventBusTestBase.MyPOJO(str), str, null, new DeliveryOptions().setCodecName(codec.name()));
    }

    @Test
    public void testDecoderReplyAsymmetric() throws Exception {
        startNodes(2);
        MessageCodec codec = new EventBusTestBase.MyPOJOEncoder1();
        vertices[0].eventBus().registerCodec(codec);
        vertices[1].eventBus().registerCodec(codec);
        String str = TestUtils.randomAlphaString(100);
        testReply(new EventBusTestBase.MyPOJO(str), str, null, new DeliveryOptions().setCodecName(codec.name()));
    }

    @Test
    public void testDecoderSendSymmetric() throws Exception {
        startNodes(2);
        MessageCodec codec = new EventBusTestBase.MyPOJOEncoder2();
        vertices[0].eventBus().registerCodec(codec);
        vertices[1].eventBus().registerCodec(codec);
        String str = TestUtils.randomAlphaString(100);
        EventBusTestBase.MyPOJO pojo = new EventBusTestBase.MyPOJO(str);
        testSend(pojo, pojo, null, new DeliveryOptions().setCodecName(codec.name()));
    }

    @Test
    public void testDecoderReplySymmetric() throws Exception {
        startNodes(2);
        MessageCodec codec = new EventBusTestBase.MyPOJOEncoder2();
        vertices[0].eventBus().registerCodec(codec);
        vertices[1].eventBus().registerCodec(codec);
        String str = TestUtils.randomAlphaString(100);
        EventBusTestBase.MyPOJO pojo = new EventBusTestBase.MyPOJO(str);
        testReply(pojo, pojo, null, new DeliveryOptions().setCodecName(codec.name()));
    }

    @Test
    public void testDefaultDecoderSendAsymmetric() throws Exception {
        startNodes(2);
        MessageCodec codec = new EventBusTestBase.MyPOJOEncoder1();
        vertices[0].eventBus().registerDefaultCodec(EventBusTestBase.MyPOJO.class, codec);
        vertices[1].eventBus().registerDefaultCodec(EventBusTestBase.MyPOJO.class, codec);
        String str = TestUtils.randomAlphaString(100);
        testSend(new EventBusTestBase.MyPOJO(str), str, null, null);
    }

    @Test
    public void testDefaultDecoderReplyAsymmetric() throws Exception {
        startNodes(2);
        MessageCodec codec = new EventBusTestBase.MyPOJOEncoder1();
        vertices[0].eventBus().registerDefaultCodec(EventBusTestBase.MyPOJO.class, codec);
        vertices[1].eventBus().registerDefaultCodec(EventBusTestBase.MyPOJO.class, codec);
        String str = TestUtils.randomAlphaString(100);
        testReply(new EventBusTestBase.MyPOJO(str), str, null, null);
    }

    @Test
    public void testDefaultDecoderSendSymetric() throws Exception {
        startNodes(2);
        MessageCodec codec = new EventBusTestBase.MyPOJOEncoder2();
        vertices[0].eventBus().registerDefaultCodec(EventBusTestBase.MyPOJO.class, codec);
        vertices[1].eventBus().registerDefaultCodec(EventBusTestBase.MyPOJO.class, codec);
        String str = TestUtils.randomAlphaString(100);
        EventBusTestBase.MyPOJO pojo = new EventBusTestBase.MyPOJO(str);
        testSend(pojo, pojo, null, null);
    }

    @Test
    public void testDefaultDecoderReplySymetric() throws Exception {
        startNodes(2);
        MessageCodec codec = new EventBusTestBase.MyPOJOEncoder2();
        vertices[0].eventBus().registerDefaultCodec(EventBusTestBase.MyPOJO.class, codec);
        vertices[1].eventBus().registerDefaultCodec(EventBusTestBase.MyPOJO.class, codec);
        String str = TestUtils.randomAlphaString(100);
        EventBusTestBase.MyPOJO pojo = new EventBusTestBase.MyPOJO(str);
        testReply(pojo, pojo, null, null);
    }

    @Test
    public void testDefaultCodecReplyExceptionSubclass() throws Exception {
        startNodes(2);
        EventBusTestBase.MyReplyException myReplyException = new EventBusTestBase.MyReplyException(23, "my exception");
        EventBusTestBase.MyReplyExceptionMessageCodec codec = new EventBusTestBase.MyReplyExceptionMessageCodec();
        vertices[0].eventBus().registerDefaultCodec(EventBusTestBase.MyReplyException.class, codec);
        vertices[1].eventBus().registerDefaultCodec(EventBusTestBase.MyReplyException.class, codec);
        MessageConsumer<ReplyException> reg = vertices[0].eventBus().<ReplyException>consumer(ClusteredEventBusTestBase.ADDRESS1, ( msg) -> {
            assertTrue(((msg.body()) instanceof MyReplyException));
            testComplete();
        });
        reg.completionHandler(( ar) -> {
            vertices[1].eventBus().send(ClusteredEventBusTestBase.ADDRESS1, myReplyException);
        });
        await();
    }

    // Make sure ping/pong works ok
    @Test
    public void testClusteredPong() throws Exception {
        startNodes(2, new VertxOptions().setClusterPingInterval(500).setClusterPingReplyInterval(500));
        AtomicBoolean sending = new AtomicBoolean();
        MessageConsumer<String> consumer = vertices[0].eventBus().<String>consumer("foobar").handler(( msg) -> {
            if (!(sending.get())) {
                sending.set(true);
                vertx.setTimer(4000, ( id) -> {
                    vertices[1].eventBus().send("foobar", "whatever2");
                });
            } else {
                testComplete();
            }
        });
        consumer.completionHandler(( ar) -> {
            assertTrue(ar.succeeded());
            vertices[1].eventBus().send("foobar", "whatever");
        });
        await();
    }

    @Test
    public void testConsumerHandlesCompletionAsynchronously1() {
        startNodes(2);
        MessageConsumer<Object> consumer = vertices[0].eventBus().consumer(ClusteredEventBusTestBase.ADDRESS1);
        ThreadLocal<Object> stack = new ThreadLocal<>();
        stack.set(true);
        consumer.completionHandler(( v) -> {
            assertTrue(Vertx.currentContext().isEventLoopContext());
            assertNull(stack.get());
            testComplete();
        });
        consumer.handler(( msg) -> {
        });
        await();
    }

    @Test
    public void testConsumerHandlesCompletionAsynchronously2() {
        startNodes(2);
        MessageConsumer<Object> consumer = vertices[0].eventBus().consumer(ClusteredEventBusTestBase.ADDRESS1);
        consumer.handler(( msg) -> {
        });
        ThreadLocal<Object> stack = new ThreadLocal<>();
        stack.set(true);
        consumer.completionHandler(( v) -> {
            assertTrue(Vertx.currentContext().isEventLoopContext());
            assertNull(stack.get());
            testComplete();
        });
        await();
    }

    @Test
    public void testSubsRemovedForClosedNode() throws Exception {
        testSubsRemoved(( latch) -> {
            vertices[1].close(onSuccess(( v) -> {
                latch.countDown();
            }));
        });
    }

    @Test
    public void testSubsRemovedForKilledNode() throws Exception {
        testSubsRemoved(( latch) -> {
            VertxInternal vi = ((VertxInternal) (vertices[1]));
            vi.getClusterManager().leave(onSuccess(( v) -> {
                latch.countDown();
            }));
        });
    }

    @Test
    public void sendNoContext() throws Exception {
        int size = 1000;
        ConcurrentLinkedDeque<Integer> expected = new ConcurrentLinkedDeque<>();
        ConcurrentLinkedDeque<Integer> obtained = new ConcurrentLinkedDeque<>();
        startNodes(2);
        CountDownLatch latch = new CountDownLatch(1);
        vertices[1].eventBus().<Integer>consumer(ClusteredEventBusTestBase.ADDRESS1, ( msg) -> {
            obtained.add(msg.body());
            if ((obtained.size()) == (expected.size())) {
                assertEquals(new ArrayList<>(expected), new ArrayList<>(obtained));
                testComplete();
            }
        }).completionHandler(( ar) -> {
            assertTrue(ar.succeeded());
            latch.countDown();
        });
        latch.await();
        EventBus bus = vertices[0].eventBus();
        for (int i = 0; i < size; i++) {
            expected.add(i);
            bus.send(ClusteredEventBusTestBase.ADDRESS1, i);
        }
        await();
    }

    @Test
    public void testSendLocalOnly() {
        testDeliveryOptionsLocalOnly(true);
    }

    @Test
    public void testPublishLocalOnly() {
        testDeliveryOptionsLocalOnly(false);
    }

    @Test
    public void testLocalOnlyDoesNotApplyToReplies() {
        startNodes(2);
        vertices[1].eventBus().consumer(ClusteredEventBusTestBase.ADDRESS1).handler(( msg) -> {
            msg.reply("pong", new DeliveryOptions().setLocalOnly(true));
        }).completionHandler(onSuccess(( v) -> {
            vertices[0].eventBus().send(ClusteredEventBusTestBase.ADDRESS1, "ping", new DeliveryOptions().setSendTimeout(500), onSuccess(( msg) -> testComplete()));
        }));
        await();
    }
}

