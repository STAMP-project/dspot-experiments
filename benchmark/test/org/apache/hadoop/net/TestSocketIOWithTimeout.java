/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.net;


import NativeIO.POSIX;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.nio.channels.Pipe;
import java.util.Arrays;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.MultithreadedTestUtil;
import org.apache.hadoop.util.Shell;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This tests timout out from SocketInputStream and
 * SocketOutputStream using pipes.
 *
 * Normal read and write using these streams are tested by pretty much
 * every DFS unit test.
 */
public class TestSocketIOWithTimeout {
    static final Logger LOG = LoggerFactory.getLogger(TestSocketIOWithTimeout.class);

    private static int TIMEOUT = 1 * 1000;

    private static String TEST_STRING = "1234567890";

    private MultithreadedTestUtil.TestContext ctx = new MultithreadedTestUtil.TestContext();

    private static final int PAGE_SIZE = ((int) (POSIX.getCacheManipulator().getOperatingSystemPageSize()));

    @Test
    public void testSocketIOWithTimeout() throws Exception {
        // first open pipe:
        Pipe pipe = Pipe.open();
        Pipe.SourceChannel source = pipe.source();
        Pipe.SinkChannel sink = pipe.sink();
        try {
            final InputStream in = new SocketInputStream(source, TestSocketIOWithTimeout.TIMEOUT);
            OutputStream out = new SocketOutputStream(sink, TestSocketIOWithTimeout.TIMEOUT);
            byte[] writeBytes = TestSocketIOWithTimeout.TEST_STRING.getBytes();
            byte[] readBytes = new byte[writeBytes.length];
            byte byteWithHighBit = ((byte) (128));
            out.write(writeBytes);
            out.write(byteWithHighBit);
            doIO(null, out, TestSocketIOWithTimeout.TIMEOUT);
            in.read(readBytes);
            Assert.assertTrue(Arrays.equals(writeBytes, readBytes));
            Assert.assertEquals((byteWithHighBit & 255), in.read());
            doIO(in, null, TestSocketIOWithTimeout.TIMEOUT);
            // Change timeout on the read side.
            setTimeout(((TestSocketIOWithTimeout.TIMEOUT) * 2));
            doIO(in, null, ((TestSocketIOWithTimeout.TIMEOUT) * 2));
            /* Verify that it handles interrupted threads properly.
            Use a large timeout and expect the thread to return quickly
            upon interruption.
             */
            setTimeout(0);
            MultithreadedTestUtil.TestingThread thread = new MultithreadedTestUtil.TestingThread(ctx) {
                @Override
                public void doWork() throws Exception {
                    try {
                        in.read();
                        Assert.fail("Did not fail with interrupt");
                    } catch (InterruptedIOException ste) {
                        TestSocketIOWithTimeout.LOG.info(("Got expection while reading as expected : " + (ste.getMessage())));
                    }
                }
            };
            ctx.addThread(thread);
            ctx.startThreads();
            // If the thread is interrupted before it calls read()
            // then it throws ClosedByInterruptException due to
            // some Java quirk. Waiting for it to call read()
            // gets it into select(), so we get the expected
            // InterruptedIOException.
            Thread.sleep(1000);
            thread.interrupt();
            ctx.stop();
            // make sure the channels are still open
            Assert.assertTrue(source.isOpen());
            Assert.assertTrue(sink.isOpen());
            // Nevertheless, the output stream is closed, because
            // a partial write may have succeeded (see comment in
            // SocketOutputStream#write(byte[]), int, int)
            // This portion of the test cannot pass on Windows due to differences in
            // behavior of partial writes.  Windows appears to buffer large amounts of
            // written data and send it all atomically, thus making it impossible to
            // simulate a partial write scenario.  Attempts were made to switch the
            // test from using a pipe to a network socket and also to use larger and
            // larger buffers in doIO.  Nothing helped the situation though.
            if (!(Shell.WINDOWS)) {
                try {
                    out.write(1);
                    Assert.fail("Did not throw");
                } catch (IOException ioe) {
                    GenericTestUtils.assertExceptionContains("stream is closed", ioe);
                }
            }
            out.close();
            Assert.assertFalse(sink.isOpen());
            // close sink and expect -1 from source.read()
            Assert.assertEquals((-1), in.read());
            // make sure close() closes the underlying channel.
            in.close();
            Assert.assertFalse(source.isOpen());
        } finally {
            if (source != null) {
                source.close();
            }
            if (sink != null) {
                sink.close();
            }
        }
    }
}

