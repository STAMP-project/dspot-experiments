/**
 * Copyright 2015 The gRPC Authors
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
package io.grpc.internal;


import Codec.Identity.NONE;
import Status.CANCELLED;
import Status.UNKNOWN;
import io.grpc.Attributes;
import io.grpc.Attributes.Key;
import io.grpc.DecompressorRegistry;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.internal.testing.SingleMessageProducer;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


/**
 * Tests for {@link DelayedStream}.  Most of the state checking is enforced by
 * {@link ClientCallImpl} so we don't check it here.
 */
@RunWith(JUnit4.class)
public class DelayedStreamTest {
    @Rule
    public final MockitoRule mocks = MockitoJUnit.rule();

    @Mock
    private ClientStreamListener listener;

    @Mock
    private ClientStream realStream;

    @Captor
    private ArgumentCaptor<ClientStreamListener> listenerCaptor;

    private DelayedStream stream = new DelayedStream();

    @Test
    public void setStream_setAuthority() {
        final String authority = "becauseIsaidSo";
        stream.setAuthority(authority);
        stream.start(listener);
        stream.setStream(realStream);
        InOrder inOrder = Mockito.inOrder(realStream);
        inOrder.verify(realStream).setAuthority(authority);
        inOrder.verify(realStream).start(ArgumentMatchers.any(ClientStreamListener.class));
    }

    @Test(expected = IllegalStateException.class)
    public void setAuthority_afterStart() {
        stream.start(listener);
        stream.setAuthority("notgonnawork");
    }

    @Test(expected = IllegalStateException.class)
    public void start_afterStart() {
        stream.start(listener);
        stream.start(Mockito.mock(ClientStreamListener.class));
    }

    @Test
    public void setStream_sendsAllMessages() {
        stream.start(listener);
        stream.setCompressor(NONE);
        stream.setDecompressorRegistry(DecompressorRegistry.getDefaultInstance());
        stream.setMessageCompression(true);
        InputStream message = new ByteArrayInputStream(new byte[]{ 'a' });
        stream.writeMessage(message);
        stream.setMessageCompression(false);
        stream.writeMessage(message);
        stream.setStream(realStream);
        Mockito.verify(realStream).setCompressor(NONE);
        Mockito.verify(realStream).setDecompressorRegistry(DecompressorRegistry.getDefaultInstance());
        Mockito.verify(realStream).setMessageCompression(true);
        Mockito.verify(realStream).setMessageCompression(false);
        Mockito.verify(realStream, Mockito.times(2)).writeMessage(message);
        Mockito.verify(realStream).start(listenerCaptor.capture());
        stream.writeMessage(message);
        Mockito.verify(realStream, Mockito.times(3)).writeMessage(message);
        Mockito.verifyNoMoreInteractions(listener);
        listenerCaptor.getValue().onReady();
        Mockito.verify(listener).onReady();
    }

    @Test
    public void setStream_halfClose() {
        stream.start(listener);
        stream.halfClose();
        stream.setStream(realStream);
        Mockito.verify(realStream).halfClose();
    }

    @Test
    public void setStream_flush() {
        stream.start(listener);
        stream.flush();
        stream.setStream(realStream);
        Mockito.verify(realStream).flush();
        stream.flush();
        Mockito.verify(realStream, Mockito.times(2)).flush();
    }

    @Test
    public void setStream_flowControl() {
        stream.start(listener);
        stream.request(1);
        stream.request(2);
        stream.setStream(realStream);
        Mockito.verify(realStream).request(1);
        Mockito.verify(realStream).request(2);
        stream.request(3);
        Mockito.verify(realStream).request(3);
    }

    @Test
    public void setStream_setMessageCompression() {
        stream.start(listener);
        stream.setMessageCompression(false);
        stream.setStream(realStream);
        Mockito.verify(realStream).setMessageCompression(false);
        stream.setMessageCompression(true);
        Mockito.verify(realStream).setMessageCompression(true);
    }

    @Test
    public void setStream_isReady() {
        stream.start(listener);
        Assert.assertFalse(stream.isReady());
        stream.setStream(realStream);
        Mockito.verify(realStream, Mockito.never()).isReady();
        Assert.assertFalse(stream.isReady());
        Mockito.verify(realStream).isReady();
        Mockito.when(realStream.isReady()).thenReturn(true);
        Assert.assertTrue(stream.isReady());
        Mockito.verify(realStream, Mockito.times(2)).isReady();
    }

    @Test
    public void setStream_getAttributes() {
        Attributes attributes = Attributes.newBuilder().set(Key.<String>create("fakeKey"), "fakeValue").build();
        Mockito.when(realStream.getAttributes()).thenReturn(attributes);
        stream.start(listener);
        try {
            stream.getAttributes();// expect to throw IllegalStateException, otherwise fail()

            Assert.fail();
        } catch (IllegalStateException expected) {
            // ignore
        }
        stream.setStream(realStream);
        Assert.assertEquals(attributes, stream.getAttributes());
    }

    @Test
    public void startThenCancelled() {
        stream.start(listener);
        stream.cancel(CANCELLED);
        Mockito.verify(listener).closed(ArgumentMatchers.eq(CANCELLED), ArgumentMatchers.any(Metadata.class));
    }

    @Test
    public void startThenSetStreamThenCancelled() {
        stream.start(listener);
        stream.setStream(realStream);
        stream.cancel(CANCELLED);
        Mockito.verify(realStream).start(ArgumentMatchers.any(ClientStreamListener.class));
        Mockito.verify(realStream).cancel(ArgumentMatchers.same(CANCELLED));
    }

    @Test
    public void setStreamThenStartThenCancelled() {
        stream.setStream(realStream);
        stream.start(listener);
        stream.cancel(CANCELLED);
        Mockito.verify(realStream).start(ArgumentMatchers.same(listener));
        Mockito.verify(realStream).cancel(ArgumentMatchers.same(CANCELLED));
    }

    @Test
    public void setStreamThenCancelled() {
        stream.setStream(realStream);
        stream.cancel(CANCELLED);
        Mockito.verify(realStream).cancel(ArgumentMatchers.same(CANCELLED));
    }

    @Test
    public void setStreamTwice() {
        stream.start(listener);
        stream.setStream(realStream);
        Mockito.verify(realStream).start(ArgumentMatchers.any(ClientStreamListener.class));
        stream.setStream(Mockito.mock(ClientStream.class));
        stream.flush();
        Mockito.verify(realStream).flush();
    }

    @Test
    public void cancelThenSetStream() {
        stream.cancel(CANCELLED);
        stream.setStream(realStream);
        stream.start(listener);
        stream.isReady();
        Mockito.verifyNoMoreInteractions(realStream);
    }

    @Test
    public void cancel_beforeStart() {
        Status status = CANCELLED.withDescription("that was quick");
        stream.cancel(status);
        stream.start(listener);
        Mockito.verify(listener).closed(ArgumentMatchers.same(status), ArgumentMatchers.any(Metadata.class));
    }

    @Test
    public void cancelledThenStart() {
        stream.cancel(CANCELLED);
        stream.start(listener);
        Mockito.verify(listener).closed(ArgumentMatchers.eq(CANCELLED), ArgumentMatchers.any(Metadata.class));
    }

    @Test
    public void listener_onReadyDelayedUntilPassthrough() {
        class IsReadyListener extends NoopClientStreamListener {
            boolean onReadyCalled;

            @Override
            public void onReady() {
                // If onReady was not delayed, then passthrough==false and isReady will return false.
                Assert.assertTrue(stream.isReady());
                onReadyCalled = true;
            }
        }
        IsReadyListener isReadyListener = new IsReadyListener();
        stream.start(isReadyListener);
        stream.setStream(new NoopClientStream() {
            @Override
            public void start(ClientStreamListener listener) {
                // This call to the listener should end up being delayed.
                listener.onReady();
            }

            @Override
            public boolean isReady() {
                return true;
            }
        });
        Assert.assertTrue(isReadyListener.onReadyCalled);
    }

    @Test
    public void listener_allQueued() {
        final Metadata headers = new Metadata();
        final InputStream message1 = Mockito.mock(InputStream.class);
        final InputStream message2 = Mockito.mock(InputStream.class);
        final SingleMessageProducer producer1 = new SingleMessageProducer(message1);
        final SingleMessageProducer producer2 = new SingleMessageProducer(message2);
        final Metadata trailers = new Metadata();
        final Status status = UNKNOWN.withDescription("unique status");
        final InOrder inOrder = Mockito.inOrder(listener);
        stream.start(listener);
        stream.setStream(new NoopClientStream() {
            @Override
            public void start(ClientStreamListener passedListener) {
                passedListener.onReady();
                passedListener.headersRead(headers);
                passedListener.messagesAvailable(producer1);
                passedListener.onReady();
                passedListener.messagesAvailable(producer2);
                passedListener.closed(status, trailers);
                Mockito.verifyNoMoreInteractions(listener);
            }
        });
        inOrder.verify(listener).onReady();
        inOrder.verify(listener).headersRead(headers);
        inOrder.verify(listener).messagesAvailable(producer1);
        inOrder.verify(listener).onReady();
        inOrder.verify(listener).messagesAvailable(producer2);
        inOrder.verify(listener).closed(status, trailers);
    }

    @Test
    public void listener_noQueued() {
        final Metadata headers = new Metadata();
        final InputStream message = Mockito.mock(InputStream.class);
        final SingleMessageProducer producer = new SingleMessageProducer(message);
        final Metadata trailers = new Metadata();
        final Status status = UNKNOWN.withDescription("unique status");
        stream.start(listener);
        stream.setStream(realStream);
        Mockito.verify(realStream).start(listenerCaptor.capture());
        ClientStreamListener delayedListener = listenerCaptor.getValue();
        delayedListener.onReady();
        Mockito.verify(listener).onReady();
        delayedListener.headersRead(headers);
        Mockito.verify(listener).headersRead(headers);
        delayedListener.messagesAvailable(producer);
        Mockito.verify(listener).messagesAvailable(producer);
        delayedListener.closed(status, trailers);
        Mockito.verify(listener).closed(status, trailers);
    }
}

