/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2015-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.jersey.jdk.connector.internal;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritePendingException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.junit.Assert;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;


/**
 *
 *
 * @author Petr Janouch (petr.janouch at oracle.com)
 */
public class AsynchronousBodyOutputStreamTest {
    @Test
    public void testBasicAsyncWrite() throws IOException {
        doTestAsyncWrite(false);
    }

    @Test
    public void testBasicAsyncArrayWrite() throws IOException {
        doTestAsyncWrite(true);
    }

    @Test
    public void testSetListenerAfterOpeningStream() throws IOException {
        AsynchronousBodyOutputStreamTest.TestStream stream = new AsynchronousBodyOutputStreamTest.TestStream(6);
        AsynchronousBodyOutputStreamTest.MockTransportFilter transportFilter = new AsynchronousBodyOutputStreamTest.MockTransportFilter();
        String msg1 = "AAAAAAAAAAAAAAAAAAAA";
        String msg2 = "BBBBBBBBBBBBB";
        String msg3 = "CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC";
        AsynchronousBodyOutputStreamTest.TestWriteListener writeListener = new AsynchronousBodyOutputStreamTest.TestWriteListener(stream, (-1));
        writeListener.write(msg1);
        open(transportFilter);
        setWriteListener(writeListener);
        writeListener.write(msg2);
        writeListener.write(msg3);
        close();
        if ((writeListener.getError()) != null) {
            writeListener.getError().printStackTrace();
            Assert.fail();
        }
        Assert.assertEquals(((msg1 + msg2) + msg3), transportFilter.getWrittenData());
    }

    @Test
    public void testTestAsyncWriteWithDelay() throws IOException {
        doTestAsyncWriteWithDelay(false);
    }

    @Test
    public void testTestAsyncWriteArrayWithDelay() throws IOException {
        doTestAsyncWriteWithDelay(true);
    }

    @Test
    public void testAsyncFlush() {
        AsynchronousBodyOutputStreamTest.TestStream stream = new AsynchronousBodyOutputStreamTest.TestStream(6);
        String msg1 = "AAAAAAAAAAAAAAAAAAAA";
        String msg2 = "BBBBBBBBBBBBB";
        String msg3 = "CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC";
        String msg4 = "DDDDDDD";
        AsynchronousBodyOutputStreamTest.TestWriteListener writeListener = new AsynchronousBodyOutputStreamTest.TestWriteListener(stream, (-1));
        setWriteListener(writeListener);
        AsynchronousBodyOutputStreamTest.MockTransportFilter transportFilter = new AsynchronousBodyOutputStreamTest.MockTransportFilter();
        writeListener.write(msg1);
        transportFilter.block();
        open(transportFilter);
        writeListener.flush();
        // test someone going crazy with flush
        writeListener.flush();
        transportFilter.unblock();
        transportFilter.block();
        writeListener.write(msg2);
        transportFilter.unblock();
        writeListener.flush();
        transportFilter.block();
        writeListener.write(msg3);
        writeListener.flush();
        writeListener.write(msg4);
        writeListener.flush();
        writeListener.close();
        transportFilter.unblock();
        if ((writeListener.getError()) != null) {
            writeListener.getError().printStackTrace();
            Assert.fail();
        }
        Assert.assertEquals((((msg1 + msg2) + msg3) + msg4), transportFilter.getWrittenData());
    }

    @Test
    public void testAsyncException() {
        AsynchronousBodyOutputStreamTest.TestStream stream = new AsynchronousBodyOutputStreamTest.TestStream(6);
        String msg1 = "AAAAAAAAAAAAAAAAAAAA";
        AsynchronousBodyOutputStreamTest.TestWriteListener writeListener = new AsynchronousBodyOutputStreamTest.TestWriteListener(stream, (-1));
        setWriteListener(writeListener);
        AsynchronousBodyOutputStreamTest.MockTransportFilter transportFilter = new AsynchronousBodyOutputStreamTest.MockTransportFilter();
        open(transportFilter);
        Throwable t = new Throwable();
        transportFilter.setException(t);
        writeListener.write(msg1);
        assertNotNull(writeListener.getError());
        Assert.assertTrue((t == (writeListener.getError())));
    }

    @Test
    public void testBasicSyncWrite() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        doTestSyncWrite(false);
    }

    @Test
    public void testBasicSyncArrayWrite() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        doTestSyncWrite(true);
    }

    @Test
    public void testSyncWriteWithDelay() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        doTestSyncWriteWithDelay(false);
    }

    @Test
    public void testSyncArrayWriteWithDelay() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        doTestSyncWriteWithDelay(true);
    }

    @Test
    public void testAsyncWriteWhenNotReady() throws IOException {
        AsynchronousBodyOutputStreamTest.TestStream stream = new AsynchronousBodyOutputStreamTest.TestStream(6);
        AsynchronousBodyOutputStreamTest.TestWriteListener writeListener = new AsynchronousBodyOutputStreamTest.TestWriteListener(stream, (-1));
        setWriteListener(writeListener);
        try {
            stream.write(((byte) ('a')));
            Assert.fail();
        } catch (IllegalStateException e) {
            // expected
        }
    }

    @Test
    public void testUnsupportedSync() {
        final AsynchronousBodyOutputStreamTest.TestStream stream = new AsynchronousBodyOutputStreamTest.TestStream(10);
        open(new AsynchronousBodyOutputStreamTest.MockTransportFilter());
        try {
            // touch this stream to make it synchronous
            stream.write(((byte) ('a')));
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
        AsynchronousBodyOutputStreamTest.assertUnsupported(() -> {
            isReady();
            return null;
        });
        AsynchronousBodyOutputStreamTest.assertUnsupported(() -> {
            setWriteListener(new AsynchronousBodyOutputStreamTest.TestWriteListener(stream));
            return null;
        });
    }

    @Test
    public void testSyncException() throws IOException {
        AsynchronousBodyOutputStreamTest.TestStream stream = new AsynchronousBodyOutputStreamTest.TestStream(1);
        AsynchronousBodyOutputStreamTest.MockTransportFilter transportFilter = new AsynchronousBodyOutputStreamTest.MockTransportFilter();
        open(transportFilter);
        Throwable t = new Throwable();
        transportFilter.setException(t);
        try {
            write("aaa".getBytes());
            Assert.fail();
        } catch (IOException e) {
            Assert.assertTrue((t == (e.getCause())));
        }
    }

    private static class TestWriteListener implements WriteListener {
        private static final ByteBuffer CLOSE = ByteBuffer.allocate(0);

        private static final ByteBuffer FLUSH = ByteBuffer.allocate(0);

        private final ChunkedBodyOutputStream outputStream;

        private final Queue<ByteBuffer> message = new LinkedList<>();

        private final int outputArraySize;

        private volatile boolean listenerCallExpected = true;

        private volatile Throwable error;

        TestWriteListener(ChunkedBodyOutputStream outputStream) {
            this(outputStream, (-1));
        }

        TestWriteListener(ChunkedBodyOutputStream outputStream, int outputArraySize) {
            this.outputStream = outputStream;
            this.outputArraySize = outputArraySize;
        }

        void write(String message) {
            byte[] bytes = message.getBytes();
            this.message.add(ByteBuffer.wrap(bytes));
            doWrite();
        }

        void close() {
            message.add(AsynchronousBodyOutputStreamTest.TestWriteListener.CLOSE);
            doWrite();
        }

        void flush() {
            message.add(AsynchronousBodyOutputStreamTest.TestWriteListener.FLUSH);
            doWrite();
        }

        @Override
        public void onWritePossible() {
            if (!(listenerCallExpected)) {
                Assert.fail();
            }
            listenerCallExpected = false;
            doWrite();
        }

        private void doWrite() {
            while (((message.peek()) != null) && (((outputStream.isReady()) || ((message.peek()) == (AsynchronousBodyOutputStreamTest.TestWriteListener.CLOSE))) || ((message.peek()) == (AsynchronousBodyOutputStreamTest.TestWriteListener.FLUSH)))) {
                try {
                    ByteBuffer headBuffer = message.peek();
                    if (headBuffer == (AsynchronousBodyOutputStreamTest.TestWriteListener.CLOSE)) {
                        outputStream.close();
                        message.poll();
                        continue;
                    }
                    if (headBuffer == (AsynchronousBodyOutputStreamTest.TestWriteListener.FLUSH)) {
                        outputStream.flush();
                        message.poll();
                        continue;
                    }
                    if ((outputArraySize) == (-1)) {
                        outputStream.write(headBuffer.get());
                    } else {
                        int arraySize = outputArraySize;
                        if ((headBuffer.remaining()) < arraySize) {
                            arraySize = headBuffer.remaining();
                        }
                        byte[] outputArray = new byte[arraySize];
                        headBuffer.get(outputArray);
                        outputStream.write(outputArray);
                    }
                    if (!(headBuffer.hasRemaining())) {
                        message.poll();
                    }
                } catch (IOException e) {
                    error = e;
                }
            } 
            if (!(outputStream.isReady())) {
                listenerCallExpected = true;
            }
        }

        @Override
        public void onError(Throwable t) {
            error = t;
        }

        public Throwable getError() {
            return error;
        }
    }

    private static class TestStream extends ChunkedBodyOutputStream {
        TestStream(int bufferSize) {
            super(bufferSize);
        }

        @Override
        protected ByteBuffer encodeToHttp(ByteBuffer byteBuffer) {
            return byteBuffer;
        }
    }

    private static class MockTransportFilter extends Filter<ByteBuffer, Void, Void, Void> {
        private final ByteArrayOutputStream writtenData = new ByteArrayOutputStream();

        private volatile boolean pendingWrite = false;

        private volatile boolean block = false;

        private volatile CountDownLatch blockLatch;

        private volatile CompletionHandler<ByteBuffer> completionHandler;

        private volatile Throwable exception;

        MockTransportFilter() {
            super(null);
        }

        @Override
        void write(ByteBuffer data, CompletionHandler<ByteBuffer> completionHandler) {
            if (pendingWrite) {
                completionHandler.failed(new WritePendingException());
            }
            pendingWrite = true;
            while (data.hasRemaining()) {
                writtenData.write(data.get());
            } 
            if (block) {
                this.completionHandler = completionHandler;
                if ((blockLatch) != null) {
                    blockLatch.countDown();
                }
                return;
            }
            pendingWrite = false;
            if ((exception) == null) {
                completionHandler.completed(data);
            } else {
                completionHandler.failed(exception);
            }
        }

        String getWrittenData() {
            return new String(writtenData.toByteArray());
        }

        void block(CountDownLatch blockLatch) {
            this.blockLatch = blockLatch;
            block = true;
        }

        void block() {
            block = true;
        }

        void unblock() {
            block = false;
            pendingWrite = false;
            completionHandler.completed(null);
            completionHandler = null;
        }

        public void setException(Throwable exception) {
            this.exception = exception;
        }
    }
}

