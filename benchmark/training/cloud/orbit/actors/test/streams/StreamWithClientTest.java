/**
 * Copyright (C) 2016 Electronic Arts Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1.  Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2.  Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3.  Neither the name of Electronic Arts, Inc. ("EA") nor the names of
 * its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY ELECTRONIC ARTS AND ITS CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL ELECTRONIC ARTS OR ITS CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package cloud.orbit.actors.test.streams;


import AsyncStream.DEFAULT_PROVIDER;
import cloud.orbit.actors.Actor;
import cloud.orbit.actors.Stage;
import cloud.orbit.actors.client.ClientPeer;
import cloud.orbit.actors.runtime.AbstractActor;
import cloud.orbit.actors.streams.AsyncStream;
import cloud.orbit.actors.streams.StreamSubscriptionHandle;
import cloud.orbit.actors.test.ActorBaseTest;
import cloud.orbit.concurrent.Task;
import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public class StreamWithClientTest extends ActorBaseTest {
    public interface Hello extends Actor {
        Task<Void> doPush(String streamId, final String message);

        Task<Void> doPushGeneric(final String streamId, final Object message);
    }

    public static class HelloActor extends AbstractActor implements StreamWithClientTest.Hello {
        @Override
        public Task<Void> doPush(final String streamId, final String message) {
            return AsyncStream.getStream(String.class, streamId).publish(message);
        }

        @Override
        public Task<Void> doPushGeneric(final String streamId, final Object message) {
            final Class aClass = message.getClass();
            return AsyncStream.getStream(aClass, streamId).publish(message);
        }
    }

    @Test(timeout = 30000L)
    public void test() throws InterruptedException {
        final Stage stage1 = createStage();
        StreamWithClientTest.Hello hello = Actor.getReference(StreamWithClientTest.Hello.class, "0");
        hello.doPush("testStream", "hello").join();
        final ClientPeer client = createRemoteClient(stage1);
        AsyncStream<String> testStream = client.getStream(DEFAULT_PROVIDER, String.class, "testStream");
        testStream.subscribe(( d, t) -> {
            fakeSync.deque("received").add(d);
            return Task.done();
        }).join();
        stage1.bind();
        hello.doPush("testStream", "hello2").join();
        Assert.assertEquals("hello2", fakeSync.deque("received").poll(20, TimeUnit.SECONDS));
        dumpMessages();
    }

    @Test(timeout = 30000L)
    public void testUnsubscribe() throws InterruptedException {
        final Stage stage1 = createStage();
        StreamWithClientTest.Hello hello = Actor.getReference(StreamWithClientTest.Hello.class, "0");
        // client subscribes
        logger.info("subscribing");
        final ClientPeer client = createRemoteClient(stage1);
        AsyncStream<String> testStream = client.getStream(DEFAULT_PROVIDER, String.class, "testStream");
        BlockingQueue<Object> messagesReceived = fakeSync.deque("received");
        final StreamSubscriptionHandle<String> handle = testStream.subscribe(( d, t) -> {
            messagesReceived.add(d);
            return Task.done();
        }).join();
        // first push
        hello.doPush("testStream", "hello").join();
        Assert.assertEquals("hello", messagesReceived.poll(20, TimeUnit.SECONDS));
        Assert.assertEquals(0, messagesReceived.size());
        // client unsubscribes
        logger.info("unsubscribing");
        testStream.unsubscribe(handle).join();
        Assert.assertEquals(0, messagesReceived.size());
        // second push
        hello.doPush("testStream", "hello2").join();
        // nothing should be sent
        // client subscribes again
        logger.info("subscribing again");
        testStream.subscribe(( d, t) -> {
            messagesReceived.add(d);
            return Task.done();
        }).join();
        // another push
        hello.doPush("testStream", "hello3").join();
        Assert.assertEquals("hello3", messagesReceived.poll(10, TimeUnit.SECONDS));
        Assert.assertEquals(0, messagesReceived.size());
        dumpMessages();
    }

    public static class SomeData implements Serializable {
        int x;

        Object obj;

        public SomeData() {
            // required by the serializer
        }

        public SomeData(final int x, final Object obj) {
            this.x = x;
            this.obj = obj;
        }
    }

    @Test(timeout = 30000L)
    public void testMessageClass() throws InterruptedException {
        final Stage stage1 = createStage();
        StreamWithClientTest.Hello hello = Actor.getReference(StreamWithClientTest.Hello.class, "0");
        hello.doPush("testStream", "hello").join();
        final ClientPeer client = createRemoteClient(stage1);
        AsyncStream<StreamWithClientTest.SomeData> testStream = client.getStream(DEFAULT_PROVIDER, StreamWithClientTest.SomeData.class, "testStream");
        testStream.subscribe(( d, t) -> {
            fakeSync.deque("received").add(d);
            return Task.done();
        }).join();
        stage1.bind();
        hello.doPushGeneric("testStream", new StreamWithClientTest.SomeData(5, null)).join();
        final StreamWithClientTest.SomeData received = fakeSync.<StreamWithClientTest.SomeData>deque("received").poll(20, TimeUnit.SECONDS);
        Assert.assertEquals(5, received.x);
        Assert.assertNull(received.obj);
        dumpMessages();
    }

    @Test(timeout = 30000L)
    public void testMessageClassJsonTypeInfo() throws InterruptedException {
        final Stage stage1 = createStage();
        StreamWithClientTest.Hello hello = Actor.getReference(StreamWithClientTest.Hello.class, "0");
        hello.doPush("testStream", "hello").join();
        final ClientPeer client = createRemoteClient(stage1);
        AsyncStream<StreamWithClientTest.SomeData> testStream = client.getStream(DEFAULT_PROVIDER, StreamWithClientTest.SomeData.class, "testStream");
        testStream.subscribe(( d, t) -> {
            fakeSync.deque("received").add(d);
            return Task.done();
        }).join();
        stage1.bind();
        final StreamWithClientTest.SomeData someData = new StreamWithClientTest.SomeData(5, new Object[]{ new StreamWithClientTest.SomeData(6, null) });
        hello.doPushGeneric("testStream", someData).join();
        final StreamWithClientTest.SomeData received = fakeSync.<StreamWithClientTest.SomeData>deque("received").poll(20, TimeUnit.SECONDS);
        Assert.assertEquals(5, received.x);
        // tests the json type information.
        Assert.assertEquals(6, ((StreamWithClientTest.SomeData) (((Object[]) (received.obj))[0])).x);
        dumpMessages();
    }
}

