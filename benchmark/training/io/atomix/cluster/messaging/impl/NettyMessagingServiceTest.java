/**
 * Copyright 2017-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.atomix.cluster.messaging.impl;


import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.Uninterruptibles;
import io.atomix.cluster.messaging.ManagedMessagingService;
import io.atomix.utils.net.Address;
import java.net.ConnectException;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Netty messaging service test.
 */
public class NettyMessagingServiceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyMessagingServiceTest.class);

    ManagedMessagingService netty1;

    ManagedMessagingService netty2;

    ManagedMessagingService nettyv11;

    ManagedMessagingService nettyv12;

    ManagedMessagingService nettyv21;

    ManagedMessagingService nettyv22;

    private static final String IP_STRING = "127.0.0.1";

    Address address1;

    Address address2;

    Address addressv11;

    Address addressv12;

    Address addressv21;

    Address addressv22;

    Address invalidAddress;

    @Test
    public void testSendAsync() {
        String subject = nextSubject();
        CountDownLatch latch1 = new CountDownLatch(1);
        CompletableFuture<Void> response = netty1.sendAsync(address2, subject, "hello world".getBytes());
        response.whenComplete(( r, e) -> {
            Assert.assertNull(e);
            latch1.countDown();
        });
        Uninterruptibles.awaitUninterruptibly(latch1);
        CountDownLatch latch2 = new CountDownLatch(1);
        response = netty1.sendAsync(invalidAddress, subject, "hello world".getBytes());
        response.whenComplete(( r, e) -> {
            Assert.assertNotNull(e);
            Assert.assertTrue((e instanceof ConnectException));
            latch2.countDown();
        });
        Uninterruptibles.awaitUninterruptibly(latch2);
    }

    @Test
    public void testSendAndReceive() {
        String subject = nextSubject();
        AtomicBoolean handlerInvoked = new AtomicBoolean(false);
        AtomicReference<byte[]> request = new AtomicReference<>();
        AtomicReference<Address> sender = new AtomicReference<>();
        BiFunction<Address, byte[], byte[]> handler = ( ep, data) -> {
            handlerInvoked.set(true);
            sender.set(ep);
            request.set(data);
            return "hello there".getBytes();
        };
        netty2.registerHandler(subject, handler, MoreExecutors.directExecutor());
        CompletableFuture<byte[]> response = netty1.sendAndReceive(address2, subject, "hello world".getBytes());
        Assert.assertTrue(Arrays.equals("hello there".getBytes(), response.join()));
        Assert.assertTrue(handlerInvoked.get());
        Assert.assertTrue(Arrays.equals(request.get(), "hello world".getBytes()));
        Assert.assertEquals(address1.address(), sender.get().address());
    }

    @Test
    public void testTransientSendAndReceive() {
        String subject = nextSubject();
        AtomicBoolean handlerInvoked = new AtomicBoolean(false);
        AtomicReference<byte[]> request = new AtomicReference<>();
        AtomicReference<Address> sender = new AtomicReference<>();
        BiFunction<Address, byte[], byte[]> handler = ( ep, data) -> {
            handlerInvoked.set(true);
            sender.set(ep);
            request.set(data);
            return "hello there".getBytes();
        };
        netty2.registerHandler(subject, handler, MoreExecutors.directExecutor());
        CompletableFuture<byte[]> response = netty1.sendAndReceive(address2, subject, "hello world".getBytes(), false);
        Assert.assertTrue(Arrays.equals("hello there".getBytes(), response.join()));
        Assert.assertTrue(handlerInvoked.get());
        Assert.assertTrue(Arrays.equals(request.get(), "hello world".getBytes()));
        Assert.assertEquals(address1.address(), sender.get().address());
    }

    @Test
    public void testSendAndReceiveWithFixedTimeout() {
        String subject = nextSubject();
        BiFunction<Address, byte[], CompletableFuture<byte[]>> handler = ( ep, payload) -> new CompletableFuture<>();
        netty2.registerHandler(subject, handler);
        try {
            netty1.sendAndReceive(address2, subject, "hello world".getBytes(), Duration.ofSeconds(1)).join();
            Assert.fail();
        } catch (CompletionException e) {
            Assert.assertTrue(((e.getCause()) instanceof TimeoutException));
        }
    }

    @Test
    public void testSendAndReceiveWithDynamicTimeout() {
        String subject = nextSubject();
        BiFunction<Address, byte[], CompletableFuture<byte[]>> handler = ( ep, payload) -> new CompletableFuture<>();
        netty2.registerHandler(subject, handler);
        try {
            netty1.sendAndReceive(address2, subject, "hello world".getBytes()).join();
            Assert.fail();
        } catch (CompletionException e) {
            Assert.assertTrue(((e.getCause()) instanceof TimeoutException));
        }
    }

    @Test
    public void testV1() throws Exception {
        String subject;
        byte[] payload = "Hello world!".getBytes();
        byte[] response;
        subject = nextSubject();
        nettyv11.registerHandler(subject, ( address, bytes) -> CompletableFuture.completedFuture(bytes));
        response = nettyv12.sendAndReceive(addressv11, subject, payload).get(10, TimeUnit.SECONDS);
        Assert.assertArrayEquals(payload, response);
    }

    @Test
    public void testV2() throws Exception {
        String subject;
        byte[] payload = "Hello world!".getBytes();
        byte[] response;
        subject = nextSubject();
        nettyv21.registerHandler(subject, ( address, bytes) -> CompletableFuture.completedFuture(bytes));
        response = nettyv22.sendAndReceive(addressv21, subject, payload).get(10, TimeUnit.SECONDS);
        Assert.assertArrayEquals(payload, response);
    }

    @Test
    public void testVersionNegotiation() throws Exception {
        String subject;
        byte[] payload = "Hello world!".getBytes();
        byte[] response;
        subject = nextSubject();
        nettyv11.registerHandler(subject, ( address, bytes) -> CompletableFuture.completedFuture(bytes));
        response = nettyv21.sendAndReceive(addressv11, subject, payload).get(10, TimeUnit.SECONDS);
        Assert.assertArrayEquals(payload, response);
        subject = nextSubject();
        nettyv22.registerHandler(subject, ( address, bytes) -> CompletableFuture.completedFuture(bytes));
        response = nettyv12.sendAndReceive(addressv22, subject, payload).get(10, TimeUnit.SECONDS);
        Assert.assertArrayEquals(payload, response);
    }
}

