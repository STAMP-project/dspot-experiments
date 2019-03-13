package org.whispersystems.dispatch;


import PubSubReply.Type;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.whispersystems.dispatch.io.RedisPubSubConnectionFactory;
import org.whispersystems.dispatch.redis.PubSubConnection;
import org.whispersystems.dispatch.redis.PubSubReply;


public class DispatchManagerTest {
    private PubSubConnection pubSubConnection;

    private RedisPubSubConnectionFactory socketFactory;

    private DispatchManager dispatchManager;

    private DispatchManagerTest.PubSubReplyInputStream pubSubReplyInputStream;

    @Rule
    public ExternalResource resource = new ExternalResource() {
        @Override
        protected void before() throws Throwable {
            pubSubConnection = Mockito.mock(PubSubConnection.class);
            socketFactory = Mockito.mock(RedisPubSubConnectionFactory.class);
            pubSubReplyInputStream = new DispatchManagerTest.PubSubReplyInputStream();
            Mockito.when(socketFactory.connect()).thenReturn(pubSubConnection);
            Mockito.when(pubSubConnection.read()).thenAnswer(new Answer<PubSubReply>() {
                @Override
                public PubSubReply answer(InvocationOnMock invocationOnMock) throws Throwable {
                    return pubSubReplyInputStream.read();
                }
            });
            dispatchManager = new DispatchManager(socketFactory, Optional.empty());
            dispatchManager.start();
        }

        @Override
        protected void after() {
        }
    };

    @Test
    public void testConnect() {
        Mockito.verify(socketFactory).connect();
    }

    @Test
    public void testSubscribe() throws IOException {
        DispatchChannel dispatchChannel = Mockito.mock(DispatchChannel.class);
        dispatchManager.subscribe("foo", dispatchChannel);
        pubSubReplyInputStream.write(new PubSubReply(Type.SUBSCRIBE, "foo", Optional.empty()));
        Mockito.verify(dispatchChannel, Mockito.timeout(1000)).onDispatchSubscribed(ArgumentMatchers.eq("foo"));
    }

    @Test
    public void testSubscribeUnsubscribe() throws IOException {
        DispatchChannel dispatchChannel = Mockito.mock(DispatchChannel.class);
        dispatchManager.subscribe("foo", dispatchChannel);
        dispatchManager.unsubscribe("foo", dispatchChannel);
        pubSubReplyInputStream.write(new PubSubReply(Type.SUBSCRIBE, "foo", Optional.empty()));
        pubSubReplyInputStream.write(new PubSubReply(Type.UNSUBSCRIBE, "foo", Optional.empty()));
        Mockito.verify(dispatchChannel, Mockito.timeout(1000)).onDispatchUnsubscribed(ArgumentMatchers.eq("foo"));
    }

    @Test
    public void testMessages() throws IOException {
        DispatchChannel fooChannel = Mockito.mock(DispatchChannel.class);
        DispatchChannel barChannel = Mockito.mock(DispatchChannel.class);
        dispatchManager.subscribe("foo", fooChannel);
        dispatchManager.subscribe("bar", barChannel);
        pubSubReplyInputStream.write(new PubSubReply(Type.SUBSCRIBE, "foo", Optional.empty()));
        pubSubReplyInputStream.write(new PubSubReply(Type.SUBSCRIBE, "bar", Optional.empty()));
        Mockito.verify(fooChannel, Mockito.timeout(1000)).onDispatchSubscribed(ArgumentMatchers.eq("foo"));
        Mockito.verify(barChannel, Mockito.timeout(1000)).onDispatchSubscribed(ArgumentMatchers.eq("bar"));
        pubSubReplyInputStream.write(new PubSubReply(Type.MESSAGE, "foo", Optional.of("hello".getBytes())));
        pubSubReplyInputStream.write(new PubSubReply(Type.MESSAGE, "bar", Optional.of("there".getBytes())));
        ArgumentCaptor<byte[]> captor = ArgumentCaptor.forClass(byte[].class);
        Mockito.verify(fooChannel, Mockito.timeout(1000)).onDispatchMessage(ArgumentMatchers.eq("foo"), captor.capture());
        Assert.assertArrayEquals("hello".getBytes(), captor.getValue());
        Mockito.verify(barChannel, Mockito.timeout(1000)).onDispatchMessage(ArgumentMatchers.eq("bar"), captor.capture());
        Assert.assertArrayEquals("there".getBytes(), captor.getValue());
    }

    private static class PubSubReplyInputStream {
        private final List<PubSubReply> pubSubReplyList = new LinkedList<>();

        public synchronized PubSubReply read() {
            try {
                while (pubSubReplyList.isEmpty())
                    wait();

                return pubSubReplyList.remove(0);
            } catch (InterruptedException e) {
                throw new AssertionError(e);
            }
        }

        public synchronized void write(PubSubReply pubSubReply) {
            pubSubReplyList.add(pubSubReply);
            notifyAll();
        }
    }
}

