/**
 * Copyright (C) 2014 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package okio;


import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


/**
 * This test uses four timeouts of varying durations: 250ms, 500ms, 750ms and
 * 1000ms, named 'a', 'b', 'c' and 'd'.
 */
public class AsyncTimeoutTest {
    private final List<Timeout> timedOut = new CopyOnWriteArrayList<Timeout>();

    private final AsyncTimeout a = new AsyncTimeoutTest.RecordingAsyncTimeout();

    private final AsyncTimeout b = new AsyncTimeoutTest.RecordingAsyncTimeout();

    private final AsyncTimeout c = new AsyncTimeoutTest.RecordingAsyncTimeout();

    private final AsyncTimeout d = new AsyncTimeoutTest.RecordingAsyncTimeout();

    @Test
    public void zeroTimeoutIsNoTimeout() throws Exception {
        AsyncTimeout timeout = new AsyncTimeoutTest.RecordingAsyncTimeout();
        timeout.timeout(0, TimeUnit.MILLISECONDS);
        timeout.enter();
        Thread.sleep(250);
        Assert.assertFalse(timeout.exit());
        assertTimedOut();
    }

    @Test
    public void singleInstanceTimedOut() throws Exception {
        a.enter();
        Thread.sleep(500);
        Assert.assertTrue(a.exit());
        assertTimedOut(a);
    }

    @Test
    public void singleInstanceNotTimedOut() throws Exception {
        b.enter();
        Thread.sleep(250);
        b.exit();
        Assert.assertFalse(b.exit());
        assertTimedOut();
    }

    @Test
    public void instancesAddedAtEnd() throws Exception {
        a.enter();
        b.enter();
        c.enter();
        d.enter();
        Thread.sleep(1250);
        Assert.assertTrue(a.exit());
        Assert.assertTrue(b.exit());
        Assert.assertTrue(c.exit());
        Assert.assertTrue(d.exit());
        assertTimedOut(a, b, c, d);
    }

    @Test
    public void instancesAddedAtFront() throws Exception {
        d.enter();
        c.enter();
        b.enter();
        a.enter();
        Thread.sleep(1250);
        Assert.assertTrue(d.exit());
        Assert.assertTrue(c.exit());
        Assert.assertTrue(b.exit());
        Assert.assertTrue(a.exit());
        assertTimedOut(a, b, c, d);
    }

    @Test
    public void instancesRemovedAtFront() throws Exception {
        a.enter();
        b.enter();
        c.enter();
        d.enter();
        Assert.assertFalse(a.exit());
        Assert.assertFalse(b.exit());
        Assert.assertFalse(c.exit());
        Assert.assertFalse(d.exit());
        assertTimedOut();
    }

    @Test
    public void instancesRemovedAtEnd() throws Exception {
        a.enter();
        b.enter();
        c.enter();
        d.enter();
        Assert.assertFalse(d.exit());
        Assert.assertFalse(c.exit());
        Assert.assertFalse(b.exit());
        Assert.assertFalse(a.exit());
        assertTimedOut();
    }

    /**
     * Detecting double-enters is not guaranteed.
     */
    @Test
    public void doubleEnter() throws Exception {
        a.enter();
        try {
            a.enter();
            Assert.fail();
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    public void deadlineOnly() throws Exception {
        AsyncTimeoutTest.RecordingAsyncTimeout timeout = new AsyncTimeoutTest.RecordingAsyncTimeout();
        deadline(250, TimeUnit.MILLISECONDS);
        enter();
        Thread.sleep(500);
        Assert.assertTrue(exit());
        assertTimedOut(timeout);
    }

    @Test
    public void deadlineBeforeTimeout() throws Exception {
        AsyncTimeoutTest.RecordingAsyncTimeout timeout = new AsyncTimeoutTest.RecordingAsyncTimeout();
        deadline(250, TimeUnit.MILLISECONDS);
        timeout(750, TimeUnit.MILLISECONDS);
        enter();
        Thread.sleep(500);
        Assert.assertTrue(exit());
        assertTimedOut(timeout);
    }

    @Test
    public void deadlineAfterTimeout() throws Exception {
        AsyncTimeoutTest.RecordingAsyncTimeout timeout = new AsyncTimeoutTest.RecordingAsyncTimeout();
        timeout(250, TimeUnit.MILLISECONDS);
        deadline(750, TimeUnit.MILLISECONDS);
        enter();
        Thread.sleep(500);
        Assert.assertTrue(exit());
        assertTimedOut(timeout);
    }

    @Test
    public void deadlineStartsBeforeEnter() throws Exception {
        AsyncTimeoutTest.RecordingAsyncTimeout timeout = new AsyncTimeoutTest.RecordingAsyncTimeout();
        deadline(500, TimeUnit.MILLISECONDS);
        Thread.sleep(500);
        enter();
        Thread.sleep(250);
        Assert.assertTrue(exit());
        assertTimedOut(timeout);
    }

    @Test
    public void deadlineInThePast() throws Exception {
        AsyncTimeoutTest.RecordingAsyncTimeout timeout = new AsyncTimeoutTest.RecordingAsyncTimeout();
        deadlineNanoTime(((System.nanoTime()) - 1));
        enter();
        Thread.sleep(250);
        Assert.assertTrue(exit());
        assertTimedOut(timeout);
    }

    @Test
    public void wrappedSinkTimesOut() throws Exception {
        Sink sink = new ForwardingSink(new Buffer()) {
            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new AssertionError();
                }
            }
        };
        AsyncTimeout timeout = new AsyncTimeout();
        timeout.timeout(250, TimeUnit.MILLISECONDS);
        Sink timeoutSink = timeout.sink(sink);
        try {
            timeoutSink.write(null, 0);
            Assert.fail();
        } catch (InterruptedIOException expected) {
        }
    }

    @Test
    public void wrappedSourceTimesOut() throws Exception {
        Source source = new ForwardingSource(new Buffer()) {
            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                try {
                    Thread.sleep(500);
                    return -1;
                } catch (InterruptedException e) {
                    throw new AssertionError();
                }
            }
        };
        AsyncTimeout timeout = new AsyncTimeout();
        timeout.timeout(250, TimeUnit.MILLISECONDS);
        Source timeoutSource = timeout.source(source);
        try {
            timeoutSource.read(null, 0);
            Assert.fail();
        } catch (InterruptedIOException expected) {
        }
    }

    @Test
    public void wrappedThrowsWithTimeout() throws Exception {
        Sink sink = new ForwardingSink(new Buffer()) {
            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                try {
                    Thread.sleep(500);
                    throw new IOException("exception and timeout");
                } catch (InterruptedException e) {
                    throw new AssertionError();
                }
            }
        };
        AsyncTimeout timeout = new AsyncTimeout();
        timeout.timeout(250, TimeUnit.MILLISECONDS);
        Sink timeoutSink = timeout.sink(sink);
        try {
            timeoutSink.write(null, 0);
            Assert.fail();
        } catch (InterruptedIOException expected) {
            Assert.assertEquals("timeout", expected.getMessage());
            Assert.assertEquals("exception and timeout", expected.getCause().getMessage());
        }
    }

    @Test
    public void wrappedThrowsWithoutTimeout() throws Exception {
        Sink sink = new ForwardingSink(new Buffer()) {
            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                throw new IOException("no timeout occurred");
            }
        };
        AsyncTimeout timeout = new AsyncTimeout();
        timeout.timeout(250, TimeUnit.MILLISECONDS);
        Sink timeoutSink = timeout.sink(sink);
        try {
            timeoutSink.write(null, 0);
            Assert.fail();
        } catch (IOException expected) {
            Assert.assertEquals("no timeout occurred", expected.getMessage());
        }
    }

    class RecordingAsyncTimeout extends AsyncTimeout {
        @Override
        protected void timedOut() {
            timedOut.add(this);
        }
    }
}

