/**
 * Copyright 2017 The gRPC Authors
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


import com.google.common.io.ByteStreams;
import com.google.common.primitives.Bytes;
import io.grpc.internal.StreamListener.MessageProducer;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Queue;
import javax.annotation.Nullable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


/**
 * Unit tests for {@link ApplicationThreadDeframer}.
 */
@RunWith(JUnit4.class)
public class ApplicationThreadDeframerTest {
    private MessageDeframer mockDeframer = Mockito.mock(MessageDeframer.class);

    private ApplicationThreadDeframerTest.DeframerListener listener = new ApplicationThreadDeframerTest.DeframerListener();

    private ApplicationThreadDeframerTest.TransportExecutor transportExecutor = new ApplicationThreadDeframerTest.TransportExecutor();

    private ApplicationThreadDeframer applicationThreadDeframer = new ApplicationThreadDeframer(listener, transportExecutor, mockDeframer);

    @Test
    public void requestInvokesMessagesAvailableOnListener() {
        applicationThreadDeframer.request(1);
        Mockito.verifyZeroInteractions(mockDeframer);
        listener.runStoredProducer();
        Mockito.verify(mockDeframer).request(1);
    }

    @Test
    public void deframeInvokesMessagesAvailableOnListener() {
        ReadableBuffer frame = ReadableBuffers.wrap(new byte[1]);
        applicationThreadDeframer.deframe(frame);
        Mockito.verifyZeroInteractions(mockDeframer);
        listener.runStoredProducer();
        Mockito.verify(mockDeframer).deframe(frame);
    }

    @Test
    public void closeWhenCompleteInvokesMessagesAvailableOnListener() {
        applicationThreadDeframer.closeWhenComplete();
        Mockito.verifyZeroInteractions(mockDeframer);
        listener.runStoredProducer();
        Mockito.verify(mockDeframer).closeWhenComplete();
    }

    @Test
    public void closeInvokesMessagesAvailableOnListener() {
        applicationThreadDeframer.close();
        Mockito.verify(mockDeframer).stopDelivery();
        Mockito.verifyNoMoreInteractions(mockDeframer);
        listener.runStoredProducer();
        Mockito.verify(mockDeframer).close();
    }

    @Test
    public void bytesReadInvokesTransportExecutor() {
        applicationThreadDeframer.bytesRead(1);
        Assert.assertEquals(0, listener.bytesRead);
        transportExecutor.runStoredRunnable();
        Assert.assertEquals(1, listener.bytesRead);
    }

    @Test
    public void deframerClosedInvokesTransportExecutor() {
        applicationThreadDeframer.deframerClosed(true);
        Assert.assertFalse(listener.deframerClosedWithPartialMessage);
        transportExecutor.runStoredRunnable();
        Assert.assertTrue(listener.deframerClosedWithPartialMessage);
    }

    @Test
    public void deframeFailedInvokesTransportExecutor() {
        Throwable cause = new Throwable("error");
        applicationThreadDeframer.deframeFailed(cause);
        Assert.assertNull(listener.deframeFailedCause);
        transportExecutor.runStoredRunnable();
        Assert.assertEquals(cause, listener.deframeFailedCause);
    }

    @Test
    public void messagesAvailableDrainsToMessageReadQueue_returnedByInitializingMessageProducer() throws Exception {
        byte[][] messageBytes = new byte[][]{ new byte[]{ 1, 2, 3 }, new byte[]{ 4 }, new byte[]{ 5, 6 } };
        Queue<InputStream> messages = new LinkedList<>();
        for (int i = 0; i < (messageBytes.length); i++) {
            messages.add(new ByteArrayInputStream(messageBytes[i]));
        }
        ApplicationThreadDeframerTest.MultiMessageProducer messageProducer = new ApplicationThreadDeframerTest.MultiMessageProducer(messages);
        applicationThreadDeframer.messagesAvailable(messageProducer);
        /* value is ignored */
        applicationThreadDeframer.request(1);
        for (int i = 0; i < (messageBytes.length); i++) {
            InputStream message = listener.storedProducer.next();
            Assert.assertNotNull(message);
            Assert.assertEquals(Bytes.asList(messageBytes[i]), Bytes.asList(ByteStreams.toByteArray(message)));
        }
        Assert.assertNull(listener.storedProducer.next());
    }

    private static class DeframerListener implements MessageDeframer.Listener {
        private MessageProducer storedProducer;

        private int bytesRead;

        private boolean deframerClosedWithPartialMessage;

        private Throwable deframeFailedCause;

        private void runStoredProducer() {
            Assert.assertNotNull(storedProducer);
            storedProducer.next();
        }

        @Override
        public void bytesRead(int numBytes) {
            Assert.assertEquals(0, bytesRead);
            bytesRead = numBytes;
        }

        @Override
        public void messagesAvailable(MessageProducer producer) {
            Assert.assertNull(storedProducer);
            storedProducer = producer;
        }

        @Override
        public void deframerClosed(boolean hasPartialMessage) {
            Assert.assertFalse(deframerClosedWithPartialMessage);
            deframerClosedWithPartialMessage = hasPartialMessage;
        }

        @Override
        public void deframeFailed(Throwable cause) {
            Assert.assertNull(deframeFailedCause);
            deframeFailedCause = cause;
        }
    }

    private static class TransportExecutor implements ApplicationThreadDeframer.TransportExecutor {
        private Runnable storedRunnable;

        private void runStoredRunnable() {
            Assert.assertNotNull(storedRunnable);
            storedRunnable.run();
        }

        @Override
        public void runOnTransportThread(Runnable r) {
            Assert.assertNull(storedRunnable);
            storedRunnable = r;
        }
    }

    private static class MultiMessageProducer implements StreamListener.MessageProducer {
        private final Queue<InputStream> messages;

        private MultiMessageProducer(Queue<InputStream> messages) {
            this.messages = messages;
        }

        @Nullable
        @Override
        public InputStream next() {
            return messages.poll();
        }
    }
}

