/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.io;


import BufferUtil.EMPTY_BUFFER;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.FutureCallback;
import org.eclipse.jetty.util.thread.Scheduler;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ByteArrayEndPointTest {
    private Scheduler _scheduler;

    @Test
    public void testFill() throws Exception {
        ByteArrayEndPoint endp = new ByteArrayEndPoint();
        endp.addInput("test input");
        ByteBuffer buffer = BufferUtil.allocate(1024);
        Assertions.assertEquals(10, endp.fill(buffer));
        Assertions.assertEquals("test input", BufferUtil.toString(buffer));
        Assertions.assertEquals(0, endp.fill(buffer));
        endp.addInput(" more");
        Assertions.assertEquals(5, endp.fill(buffer));
        Assertions.assertEquals("test input more", BufferUtil.toString(buffer));
        Assertions.assertEquals(0, endp.fill(buffer));
        endp.addInput(((ByteBuffer) (null)));
        Assertions.assertEquals((-1), endp.fill(buffer));
        endp.close();
        try {
            endp.fill(buffer);
            Assertions.fail("Expected IOException");
        } catch (IOException e) {
            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("CLOSED"));
        }
        endp.reset();
        endp.addInput("and more");
        buffer = BufferUtil.allocate(4);
        Assertions.assertEquals(4, endp.fill(buffer));
        Assertions.assertEquals("and ", BufferUtil.toString(buffer));
        Assertions.assertEquals(0, endp.fill(buffer));
        BufferUtil.clear(buffer);
        Assertions.assertEquals(4, endp.fill(buffer));
        Assertions.assertEquals("more", BufferUtil.toString(buffer));
    }

    @Test
    public void testGrowingFlush() throws Exception {
        ByteArrayEndPoint endp = new ByteArrayEndPoint(((byte[]) (null)), 15);
        endp.setGrowOutput(true);
        Assertions.assertEquals(true, endp.flush(BufferUtil.toBuffer("some output")));
        Assertions.assertEquals("some output", endp.getOutputString());
        Assertions.assertEquals(true, endp.flush(BufferUtil.toBuffer(" some more")));
        Assertions.assertEquals("some output some more", endp.getOutputString());
        Assertions.assertEquals(true, endp.flush());
        Assertions.assertEquals("some output some more", endp.getOutputString());
        Assertions.assertEquals(true, endp.flush(EMPTY_BUFFER));
        Assertions.assertEquals("some output some more", endp.getOutputString());
        Assertions.assertEquals(true, endp.flush(EMPTY_BUFFER, BufferUtil.toBuffer(" and"), BufferUtil.toBuffer(" more")));
        Assertions.assertEquals("some output some more and more", endp.getOutputString());
        endp.close();
    }

    @Test
    public void testFlush() throws Exception {
        ByteArrayEndPoint endp = new ByteArrayEndPoint(((byte[]) (null)), 15);
        endp.setGrowOutput(false);
        endp.setOutput(BufferUtil.allocate(10));
        ByteBuffer data = BufferUtil.toBuffer("Some more data.");
        Assertions.assertEquals(false, endp.flush(data));
        Assertions.assertEquals("Some more ", endp.getOutputString());
        Assertions.assertEquals("data.", BufferUtil.toString(data));
        Assertions.assertEquals("Some more ", endp.takeOutputString());
        Assertions.assertEquals(true, endp.flush(data));
        Assertions.assertEquals("data.", BufferUtil.toString(endp.takeOutput()));
        endp.close();
    }

    @Test
    public void testReadable() throws Exception {
        ByteArrayEndPoint endp = new ByteArrayEndPoint(_scheduler, 5000);
        endp.addInput("test input");
        ByteBuffer buffer = BufferUtil.allocate(1024);
        FutureCallback fcb = new FutureCallback();
        endp.fillInterested(fcb);
        fcb.get(100, TimeUnit.MILLISECONDS);
        Assertions.assertTrue(fcb.isDone());
        Assertions.assertEquals(null, fcb.get());
        Assertions.assertEquals(10, endp.fill(buffer));
        Assertions.assertEquals("test input", BufferUtil.toString(buffer));
        fcb = new FutureCallback();
        endp.fillInterested(fcb);
        Thread.sleep(100);
        Assertions.assertFalse(fcb.isDone());
        Assertions.assertEquals(0, endp.fill(buffer));
        endp.addInput(" more");
        fcb.get(1000, TimeUnit.MILLISECONDS);
        Assertions.assertTrue(fcb.isDone());
        Assertions.assertEquals(null, fcb.get());
        Assertions.assertEquals(5, endp.fill(buffer));
        Assertions.assertEquals("test input more", BufferUtil.toString(buffer));
        fcb = new FutureCallback();
        endp.fillInterested(fcb);
        Thread.sleep(100);
        Assertions.assertFalse(fcb.isDone());
        Assertions.assertEquals(0, endp.fill(buffer));
        endp.addInput(((ByteBuffer) (null)));
        Assertions.assertTrue(fcb.isDone());
        Assertions.assertEquals(null, fcb.get());
        Assertions.assertEquals((-1), endp.fill(buffer));
        fcb = new FutureCallback();
        endp.fillInterested(fcb);
        fcb.get(1000, TimeUnit.MILLISECONDS);
        Assertions.assertTrue(fcb.isDone());
        Assertions.assertEquals(null, fcb.get());
        Assertions.assertEquals((-1), endp.fill(buffer));
        endp.close();
        fcb = new FutureCallback();
        endp.fillInterested(fcb);
        try {
            fcb.get(1000, TimeUnit.MILLISECONDS);
            Assertions.fail("Expected ExecutionException");
        } catch (ExecutionException e) {
            MatcherAssert.assertThat(e.toString(), Matchers.containsString("Closed"));
        }
    }

    @Test
    public void testWrite() throws Exception {
        ByteArrayEndPoint endp = new ByteArrayEndPoint(_scheduler, 5000, ((byte[]) (null)), 15);
        endp.setGrowOutput(false);
        endp.setOutput(BufferUtil.allocate(10));
        ByteBuffer data = BufferUtil.toBuffer("Data.");
        ByteBuffer more = BufferUtil.toBuffer(" Some more.");
        FutureCallback fcb = new FutureCallback();
        endp.write(fcb, data);
        Assertions.assertTrue(fcb.isDone());
        Assertions.assertEquals(null, fcb.get());
        Assertions.assertEquals("Data.", endp.getOutputString());
        fcb = new FutureCallback();
        endp.write(fcb, more);
        Assertions.assertFalse(fcb.isDone());
        Assertions.assertEquals("Data. Some", endp.getOutputString());
        Assertions.assertEquals("Data. Some", endp.takeOutputString());
        Assertions.assertTrue(fcb.isDone());
        Assertions.assertEquals(null, fcb.get());
        Assertions.assertEquals(" more.", endp.getOutputString());
        endp.close();
    }

    /**
     * Simulate AbstractConnection.ReadCallback.failed()
     */
    public static class Closer extends FutureCallback {
        private EndPoint endp;

        public Closer(EndPoint endp) {
            this.endp = endp;
        }

        @Override
        public void failed(Throwable cause) {
            endp.close();
            super.failed(cause);
        }
    }

    @Test
    public void testIdle() throws Exception {
        long idleTimeout = 1500;
        long halfIdleTimeout = idleTimeout / 2;
        long oneAndHalfIdleTimeout = idleTimeout + halfIdleTimeout;
        ByteArrayEndPoint endp = new ByteArrayEndPoint(_scheduler, idleTimeout);
        endp.setGrowOutput(false);
        endp.addInput("test");
        endp.setOutput(BufferUtil.allocate(5));
        Assertions.assertTrue(endp.isOpen());
        Thread.sleep(oneAndHalfIdleTimeout);
        // Still open because it has not been oshut or closed explicitly
        // and there are no callbacks, so idle timeout is ignored.
        Assertions.assertTrue(endp.isOpen());
        // Normal read is immediate, since there is data to read.
        ByteBuffer buffer = BufferUtil.allocate(1024);
        FutureCallback fcb = new FutureCallback();
        endp.fillInterested(fcb);
        fcb.get(idleTimeout, TimeUnit.MILLISECONDS);
        Assertions.assertTrue(fcb.isDone());
        Assertions.assertEquals(4, endp.fill(buffer));
        Assertions.assertEquals("test", BufferUtil.toString(buffer));
        // Wait for a read timeout.
        long start = System.nanoTime();
        fcb = new FutureCallback();
        endp.fillInterested(fcb);
        try {
            fcb.get();
            Assertions.fail("Expected ExecutionException");
        } catch (ExecutionException t) {
            MatcherAssert.assertThat(t.getCause(), Matchers.instanceOf(TimeoutException.class));
        }
        MatcherAssert.assertThat(TimeUnit.NANOSECONDS.toMillis(((System.nanoTime()) - start)), Matchers.greaterThan(halfIdleTimeout));
        MatcherAssert.assertThat("Endpoint open", endp.isOpen(), Matchers.is(true));
    }
}

