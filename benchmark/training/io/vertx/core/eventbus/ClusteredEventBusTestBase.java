/**
 * Copyright (c) 2011-2017 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package io.vertx.core.eventbus;


import io.vertx.test.core.TestUtils;
import io.vertx.test.core.VertxTestBase;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;


/**
 *
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class ClusteredEventBusTestBase extends EventBusTestBase {
    protected static final String ADDRESS1 = "some-address1";

    @Test
    public void testRegisterRemote1() {
        startNodes(2);
        String str = TestUtils.randomUnicodeString(100);
        vertices[0].eventBus().<String>consumer(ClusteredEventBusTestBase.ADDRESS1).handler((Message<String> msg) -> {
            assertEquals(str, msg.body());
            testComplete();
        }).completionHandler(( ar) -> {
            assertTrue(ar.succeeded());
            vertices[1].eventBus().send(ADDRESS1, str);
        });
        await();
    }

    @Test
    public void testRegisterRemote2() {
        startNodes(2);
        String str = TestUtils.randomUnicodeString(100);
        vertices[0].eventBus().consumer(ClusteredEventBusTestBase.ADDRESS1, (Message<String> msg) -> {
            assertEquals(str, msg.body());
            testComplete();
        }).completionHandler(( ar) -> {
            assertTrue(ar.succeeded());
            vertices[1].eventBus().send(ADDRESS1, str);
        });
        await();
    }

    @Test
    public void testSendWhileUnsubscribing() throws Exception {
        startNodes(2);
        AtomicBoolean unregistered = new AtomicBoolean();
        Verticle sender = new AbstractVerticle() {
            @Override
            public void start() throws Exception {
                getVertx().runOnContext(( v) -> sendMsg());
            }

            private void sendMsg() {
                if (!(unregistered.get())) {
                    getVertx().eventBus().send("whatever", "marseille");
                    vertx.setTimer(1, ( id) -> {
                        sendMsg();
                    });
                } else {
                    getVertx().eventBus().send("whatever", "marseille", ( ar) -> {
                        Throwable cause = ar.cause();
                        assertThat(cause, instanceOf(.class));
                        ReplyException replyException = ((ReplyException) (cause));
                        assertEquals(ReplyFailure.NO_HANDLERS, replyException.failureType());
                        testComplete();
                    });
                }
            }
        };
        Verticle receiver = new AbstractVerticle() {
            boolean unregisterCalled;

            @Override
            public void start(Future<Void> startFuture) throws Exception {
                EventBus eventBus = getVertx().eventBus();
                MessageConsumer<String> consumer = eventBus.consumer("whatever");
                consumer.handler(( m) -> {
                    if (!(unregisterCalled)) {
                        consumer.unregister(( v) -> unregistered.set(true));
                        unregisterCalled = true;
                    }
                    m.reply("ok");
                }).completionHandler(startFuture);
            }
        };
        CountDownLatch deployLatch = new CountDownLatch(1);
        vertices[0].exceptionHandler(this::fail).deployVerticle(receiver, onSuccess(( receiverId) -> {
            vertices[1].exceptionHandler(this::fail).deployVerticle(sender, onSuccess(( senderId) -> {
                deployLatch.countDown();
            }));
        }));
        awaitLatch(deployLatch);
        await();
        CountDownLatch closeLatch = new CountDownLatch(2);
        vertices[0].close(( v) -> closeLatch.countDown());
        vertices[1].close(( v) -> closeLatch.countDown());
        awaitLatch(closeLatch);
    }

    @Test
    public void testMessageBodyInterceptor() throws Exception {
        String content = TestUtils.randomUnicodeString(13);
        startNodes(2);
        waitFor(2);
        CountDownLatch latch = new CountDownLatch(1);
        vertices[0].eventBus().registerCodec(new EventBusTestBase.StringLengthCodec()).<Integer>consumer("whatever", ( msg) -> {
            assertEquals(content.length(), ((int) (msg.body())));
            complete();
        }).completionHandler(( ar) -> latch.countDown());
        awaitLatch(latch);
        EventBusTestBase.StringLengthCodec codec = new EventBusTestBase.StringLengthCodec();
        vertices[1].eventBus().registerCodec(codec).addOutboundInterceptor(( sc) -> {
            if ("whatever".equals(sc.message().address())) {
                assertEquals(content, sc.body());
                complete();
            }
            sc.next();
        }).send("whatever", content, new DeliveryOptions().setCodecName(codec.name()));
        await();
    }
}

