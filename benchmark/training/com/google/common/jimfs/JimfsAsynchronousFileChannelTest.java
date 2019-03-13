/**
 * Copyright 2013 Google Inc.
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
package com.google.common.jimfs;


import com.google.common.util.concurrent.SettableFuture;
import com.google.common.util.concurrent.Uninterruptibles;
import java.nio.ByteBuffer;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link JimfsAsynchronousFileChannel}.
 *
 * @author Colin Decker
 */
@RunWith(JUnit4.class)
public class JimfsAsynchronousFileChannelTest {
    /**
     * Just tests the main read/write methods... the methods all delegate to the non-async channel
     * anyway.
     */
    @Test
    public void testAsyncChannel() throws Throwable {
        RegularFile file = TestUtils.regularFile(15);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        JimfsAsynchronousFileChannel channel = JimfsAsynchronousFileChannelTest.channel(file, executor, StandardOpenOption.READ, StandardOpenOption.WRITE);
        try {
            Assert.assertEquals(15, channel.size());
            Assert.assertSame(channel, channel.truncate(5));
            Assert.assertEquals(5, channel.size());
            file.write(5, new byte[5], 0, 5);
            JimfsAsynchronousFileChannelTest.checkAsyncRead(channel);
            JimfsAsynchronousFileChannelTest.checkAsyncWrite(channel);
            JimfsAsynchronousFileChannelTest.checkAsyncLock(channel);
            channel.close();
            Assert.assertFalse(channel.isOpen());
        } finally {
            executor.shutdown();
        }
    }

    @Test
    public void testClosedChannel() throws Throwable {
        RegularFile file = TestUtils.regularFile(15);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            JimfsAsynchronousFileChannel channel = JimfsAsynchronousFileChannelTest.channel(file, executor, StandardOpenOption.READ, StandardOpenOption.WRITE);
            channel.close();
            JimfsAsynchronousFileChannelTest.assertClosed(channel.read(ByteBuffer.allocate(10), 0));
            JimfsAsynchronousFileChannelTest.assertClosed(channel.write(ByteBuffer.allocate(10), 15));
            JimfsAsynchronousFileChannelTest.assertClosed(channel.lock());
            JimfsAsynchronousFileChannelTest.assertClosed(channel.lock(0, 10, true));
        } finally {
            executor.shutdown();
        }
    }

    @Test
    public void testAsyncClose_write() throws Throwable {
        RegularFile file = TestUtils.regularFile(15);
        ExecutorService executor = Executors.newFixedThreadPool(4);
        try {
            JimfsAsynchronousFileChannel channel = JimfsAsynchronousFileChannelTest.channel(file, executor, StandardOpenOption.READ, StandardOpenOption.WRITE);
            file.writeLock().lock();// cause another thread trying to write to block

            // future-returning write
            Future<Integer> future = channel.write(ByteBuffer.allocate(10), 0);
            // completion handler write
            SettableFuture<Integer> completionHandlerFuture = SettableFuture.create();
            channel.write(ByteBuffer.allocate(10), 0, null, JimfsAsynchronousFileChannelTest.setFuture(completionHandlerFuture));
            // Despite this 10ms sleep to allow plenty of time, it's possible, though very rare, for a
            // race to cause the channel to be closed before the asynchronous calls get to the initial
            // check that the channel is open, causing ClosedChannelException to be thrown rather than
            // AsynchronousCloseException. This is not a problem in practice, just a quirk of how these
            // tests work and that we don't have a way of waiting for the operations to get past that
            // check.
            Uninterruptibles.sleepUninterruptibly(10, TimeUnit.MILLISECONDS);
            channel.close();
            JimfsAsynchronousFileChannelTest.assertAsynchronousClose(future);
            JimfsAsynchronousFileChannelTest.assertAsynchronousClose(completionHandlerFuture);
        } finally {
            executor.shutdown();
        }
    }

    @Test
    public void testAsyncClose_read() throws Throwable {
        RegularFile file = TestUtils.regularFile(15);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            JimfsAsynchronousFileChannel channel = JimfsAsynchronousFileChannelTest.channel(file, executor, StandardOpenOption.READ, StandardOpenOption.WRITE);
            file.writeLock().lock();// cause another thread trying to read to block

            // future-returning read
            Future<Integer> future = channel.read(ByteBuffer.allocate(10), 0);
            // completion handler read
            SettableFuture<Integer> completionHandlerFuture = SettableFuture.create();
            channel.read(ByteBuffer.allocate(10), 0, null, JimfsAsynchronousFileChannelTest.setFuture(completionHandlerFuture));
            // Despite this 10ms sleep to allow plenty of time, it's possible, though very rare, for a
            // race to cause the channel to be closed before the asynchronous calls get to the initial
            // check that the channel is open, causing ClosedChannelException to be thrown rather than
            // AsynchronousCloseException. This is not a problem in practice, just a quirk of how these
            // tests work and that we don't have a way of waiting for the operations to get past that
            // check.
            Uninterruptibles.sleepUninterruptibly(10, TimeUnit.MILLISECONDS);
            channel.close();
            JimfsAsynchronousFileChannelTest.assertAsynchronousClose(future);
            JimfsAsynchronousFileChannelTest.assertAsynchronousClose(completionHandlerFuture);
        } finally {
            executor.shutdown();
        }
    }
}

