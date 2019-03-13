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


import com.google.common.collect.ImmutableSet;
import com.google.common.testing.NullPointerTester;
import com.google.common.util.concurrent.Runnables;
import com.google.common.util.concurrent.SettableFuture;
import com.google.common.util.concurrent.Uninterruptibles;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.NonReadableChannelException;
import java.nio.channels.NonWritableChannelException;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Most of the behavior of {@link JimfsFileChannel} is handled by the {@link RegularFile}
 * implementations, so the thorough tests of that are in {@link RegularFileTest}. This mostly
 * tests interactions with the file and channel positions.
 *
 * @author Colin Decker
 */
@RunWith(JUnit4.class)
public class JimfsFileChannelTest {
    @Test
    public void testPosition() throws IOException {
        FileChannel channel = JimfsFileChannelTest.channel(TestUtils.regularFile(10), StandardOpenOption.READ);
        Assert.assertEquals(0, channel.position());
        Assert.assertSame(channel, channel.position(100));
        Assert.assertEquals(100, channel.position());
    }

    @Test
    public void testSize() throws IOException {
        RegularFile file = TestUtils.regularFile(10);
        FileChannel channel = JimfsFileChannelTest.channel(file, StandardOpenOption.READ);
        Assert.assertEquals(10, channel.size());
        file.write(10, new byte[90], 0, 90);
        Assert.assertEquals(100, channel.size());
    }

    @Test
    public void testRead() throws IOException {
        RegularFile file = TestUtils.regularFile(20);
        FileChannel channel = JimfsFileChannelTest.channel(file, StandardOpenOption.READ);
        Assert.assertEquals(0, channel.position());
        ByteBuffer buf = TestUtils.buffer("1234567890");
        ByteBuffer buf2 = TestUtils.buffer("123457890");
        Assert.assertEquals(10, channel.read(buf));
        Assert.assertEquals(10, channel.position());
        buf.flip();
        Assert.assertEquals(10, channel.read(new ByteBuffer[]{ buf, buf2 }));
        Assert.assertEquals(20, channel.position());
        buf.flip();
        buf2.flip();
        file.write(20, new byte[10], 0, 10);
        Assert.assertEquals(10, channel.read(new ByteBuffer[]{ buf, buf2 }, 0, 2));
        Assert.assertEquals(30, channel.position());
        buf.flip();
        Assert.assertEquals(10, channel.read(buf, 5));
        Assert.assertEquals(30, channel.position());
        buf.flip();
        Assert.assertEquals((-1), channel.read(buf));
        Assert.assertEquals(30, channel.position());
    }

    @Test
    public void testWrite() throws IOException {
        RegularFile file = TestUtils.regularFile(0);
        FileChannel channel = JimfsFileChannelTest.channel(file, StandardOpenOption.WRITE);
        Assert.assertEquals(0, channel.position());
        ByteBuffer buf = TestUtils.buffer("1234567890");
        ByteBuffer buf2 = TestUtils.buffer("1234567890");
        Assert.assertEquals(10, channel.write(buf));
        Assert.assertEquals(10, channel.position());
        buf.flip();
        Assert.assertEquals(20, channel.write(new ByteBuffer[]{ buf, buf2 }));
        Assert.assertEquals(30, channel.position());
        buf.flip();
        buf2.flip();
        Assert.assertEquals(20, channel.write(new ByteBuffer[]{ buf, buf2 }, 0, 2));
        Assert.assertEquals(50, channel.position());
        buf.flip();
        Assert.assertEquals(10, channel.write(buf, 5));
        Assert.assertEquals(50, channel.position());
    }

    @Test
    public void testAppend() throws IOException {
        RegularFile file = TestUtils.regularFile(0);
        FileChannel channel = JimfsFileChannelTest.channel(file, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
        Assert.assertEquals(0, channel.position());
        ByteBuffer buf = TestUtils.buffer("1234567890");
        ByteBuffer buf2 = TestUtils.buffer("1234567890");
        Assert.assertEquals(10, channel.write(buf));
        Assert.assertEquals(10, channel.position());
        buf.flip();
        channel.position(0);
        Assert.assertEquals(20, channel.write(new ByteBuffer[]{ buf, buf2 }));
        Assert.assertEquals(30, channel.position());
        buf.flip();
        buf2.flip();
        channel.position(0);
        Assert.assertEquals(20, channel.write(new ByteBuffer[]{ buf, buf2 }, 0, 2));
        Assert.assertEquals(50, channel.position());
        buf.flip();
        channel.position(0);
        Assert.assertEquals(10, channel.write(buf, 5));
        Assert.assertEquals(60, channel.position());
        buf.flip();
        channel.position(0);
        Assert.assertEquals(10, channel.transferFrom(new ByteBufferChannel(buf), 0, 10));
        Assert.assertEquals(70, channel.position());
    }

    @Test
    public void testTransferTo() throws IOException {
        RegularFile file = TestUtils.regularFile(10);
        FileChannel channel = JimfsFileChannelTest.channel(file, StandardOpenOption.READ);
        ByteBufferChannel writeChannel = new ByteBufferChannel(TestUtils.buffer("1234567890"));
        Assert.assertEquals(10, channel.transferTo(0, 100, writeChannel));
        Assert.assertEquals(0, channel.position());
    }

    @Test
    public void testTransferFrom() throws IOException {
        RegularFile file = TestUtils.regularFile(0);
        FileChannel channel = JimfsFileChannelTest.channel(file, StandardOpenOption.WRITE);
        ByteBufferChannel readChannel = new ByteBufferChannel(TestUtils.buffer("1234567890"));
        Assert.assertEquals(10, channel.transferFrom(readChannel, 0, 100));
        Assert.assertEquals(0, channel.position());
    }

    @Test
    public void testTruncate() throws IOException {
        RegularFile file = TestUtils.regularFile(10);
        FileChannel channel = JimfsFileChannelTest.channel(file, StandardOpenOption.WRITE);
        channel.truncate(10);// no resize, >= size

        Assert.assertEquals(10, file.size());
        channel.truncate(11);// no resize, > size

        Assert.assertEquals(10, file.size());
        channel.truncate(5);// resize down to 5

        Assert.assertEquals(5, file.size());
        channel.position(20);
        channel.truncate(10);
        Assert.assertEquals(10, channel.position());
        channel.truncate(2);
        Assert.assertEquals(2, channel.position());
    }

    @Test
    public void testFileTimeUpdates() throws IOException {
        RegularFile file = TestUtils.regularFile(10);
        FileChannel channel = new JimfsFileChannel(file, ImmutableSet.<OpenOption>of(StandardOpenOption.READ, StandardOpenOption.WRITE), new FileSystemState(Runnables.doNothing()));
        // accessed
        long accessTime = file.getLastAccessTime();
        Uninterruptibles.sleepUninterruptibly(2, TimeUnit.MILLISECONDS);
        channel.read(ByteBuffer.allocate(10));
        TestUtils.assertNotEquals(accessTime, file.getLastAccessTime());
        accessTime = file.getLastAccessTime();
        Uninterruptibles.sleepUninterruptibly(2, TimeUnit.MILLISECONDS);
        channel.read(ByteBuffer.allocate(10), 0);
        TestUtils.assertNotEquals(accessTime, file.getLastAccessTime());
        accessTime = file.getLastAccessTime();
        Uninterruptibles.sleepUninterruptibly(2, TimeUnit.MILLISECONDS);
        channel.read(new ByteBuffer[]{ ByteBuffer.allocate(10) });
        TestUtils.assertNotEquals(accessTime, file.getLastAccessTime());
        accessTime = file.getLastAccessTime();
        Uninterruptibles.sleepUninterruptibly(2, TimeUnit.MILLISECONDS);
        channel.read(new ByteBuffer[]{ ByteBuffer.allocate(10) }, 0, 1);
        TestUtils.assertNotEquals(accessTime, file.getLastAccessTime());
        accessTime = file.getLastAccessTime();
        Uninterruptibles.sleepUninterruptibly(2, TimeUnit.MILLISECONDS);
        channel.transferTo(0, 10, new ByteBufferChannel(10));
        TestUtils.assertNotEquals(accessTime, file.getLastAccessTime());
        // modified
        long modifiedTime = file.getLastModifiedTime();
        Uninterruptibles.sleepUninterruptibly(2, TimeUnit.MILLISECONDS);
        channel.write(ByteBuffer.allocate(10));
        TestUtils.assertNotEquals(modifiedTime, file.getLastModifiedTime());
        modifiedTime = file.getLastModifiedTime();
        Uninterruptibles.sleepUninterruptibly(2, TimeUnit.MILLISECONDS);
        channel.write(ByteBuffer.allocate(10), 0);
        TestUtils.assertNotEquals(modifiedTime, file.getLastModifiedTime());
        modifiedTime = file.getLastModifiedTime();
        Uninterruptibles.sleepUninterruptibly(2, TimeUnit.MILLISECONDS);
        channel.write(new ByteBuffer[]{ ByteBuffer.allocate(10) });
        TestUtils.assertNotEquals(modifiedTime, file.getLastModifiedTime());
        modifiedTime = file.getLastModifiedTime();
        Uninterruptibles.sleepUninterruptibly(2, TimeUnit.MILLISECONDS);
        channel.write(new ByteBuffer[]{ ByteBuffer.allocate(10) }, 0, 1);
        TestUtils.assertNotEquals(modifiedTime, file.getLastModifiedTime());
        modifiedTime = file.getLastModifiedTime();
        Uninterruptibles.sleepUninterruptibly(2, TimeUnit.MILLISECONDS);
        channel.truncate(0);
        TestUtils.assertNotEquals(modifiedTime, file.getLastModifiedTime());
        modifiedTime = file.getLastModifiedTime();
        Uninterruptibles.sleepUninterruptibly(2, TimeUnit.MILLISECONDS);
        channel.transferFrom(new ByteBufferChannel(10), 0, 10);
        TestUtils.assertNotEquals(modifiedTime, file.getLastModifiedTime());
    }

    @Test
    public void testClose() throws IOException {
        FileChannel channel = JimfsFileChannelTest.channel(TestUtils.regularFile(0), StandardOpenOption.READ, StandardOpenOption.WRITE);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Assert.assertTrue(channel.isOpen());
        channel.close();
        Assert.assertFalse(channel.isOpen());
        try {
            channel.position();
            Assert.fail();
        } catch (ClosedChannelException expected) {
        }
        try {
            channel.position(0);
            Assert.fail();
        } catch (ClosedChannelException expected) {
        }
        try {
            channel.lock();
            Assert.fail();
        } catch (ClosedChannelException expected) {
        }
        try {
            channel.lock(0, 10, true);
            Assert.fail();
        } catch (ClosedChannelException expected) {
        }
        try {
            channel.tryLock();
            Assert.fail();
        } catch (ClosedChannelException expected) {
        }
        try {
            channel.tryLock(0, 10, true);
            Assert.fail();
        } catch (ClosedChannelException expected) {
        }
        try {
            channel.force(true);
            Assert.fail();
        } catch (ClosedChannelException expected) {
        }
        try {
            channel.write(TestUtils.buffer("111"));
            Assert.fail();
        } catch (ClosedChannelException expected) {
        }
        try {
            channel.write(TestUtils.buffer("111"), 10);
            Assert.fail();
        } catch (ClosedChannelException expected) {
        }
        try {
            channel.write(new ByteBuffer[]{ TestUtils.buffer("111"), TestUtils.buffer("111") });
            Assert.fail();
        } catch (ClosedChannelException expected) {
        }
        try {
            channel.write(new ByteBuffer[]{ TestUtils.buffer("111"), TestUtils.buffer("111") }, 0, 2);
            Assert.fail();
        } catch (ClosedChannelException expected) {
        }
        try {
            channel.transferFrom(new ByteBufferChannel(TestUtils.bytes("1111")), 0, 4);
            Assert.fail();
        } catch (ClosedChannelException expected) {
        }
        try {
            channel.truncate(0);
            Assert.fail();
        } catch (ClosedChannelException expected) {
        }
        try {
            channel.read(TestUtils.buffer("111"));
            Assert.fail();
        } catch (ClosedChannelException expected) {
        }
        try {
            channel.read(TestUtils.buffer("111"), 10);
            Assert.fail();
        } catch (ClosedChannelException expected) {
        }
        try {
            channel.read(new ByteBuffer[]{ TestUtils.buffer("111"), TestUtils.buffer("111") });
            Assert.fail();
        } catch (ClosedChannelException expected) {
        }
        try {
            channel.read(new ByteBuffer[]{ TestUtils.buffer("111"), TestUtils.buffer("111") }, 0, 2);
            Assert.fail();
        } catch (ClosedChannelException expected) {
        }
        try {
            channel.transferTo(0, 10, new ByteBufferChannel(TestUtils.buffer("111")));
            Assert.fail();
        } catch (ClosedChannelException expected) {
        }
        executor.shutdown();
    }

    @Test
    public void testWritesInReadOnlyMode() throws IOException {
        FileChannel channel = JimfsFileChannelTest.channel(TestUtils.regularFile(0), StandardOpenOption.READ);
        try {
            channel.write(TestUtils.buffer("111"));
            Assert.fail();
        } catch (NonWritableChannelException expected) {
        }
        try {
            channel.write(TestUtils.buffer("111"), 10);
            Assert.fail();
        } catch (NonWritableChannelException expected) {
        }
        try {
            channel.write(new ByteBuffer[]{ TestUtils.buffer("111"), TestUtils.buffer("111") });
            Assert.fail();
        } catch (NonWritableChannelException expected) {
        }
        try {
            channel.write(new ByteBuffer[]{ TestUtils.buffer("111"), TestUtils.buffer("111") }, 0, 2);
            Assert.fail();
        } catch (NonWritableChannelException expected) {
        }
        try {
            channel.transferFrom(new ByteBufferChannel(TestUtils.bytes("1111")), 0, 4);
            Assert.fail();
        } catch (NonWritableChannelException expected) {
        }
        try {
            channel.truncate(0);
            Assert.fail();
        } catch (NonWritableChannelException expected) {
        }
        try {
            channel.lock(0, 10, false);
            Assert.fail();
        } catch (NonWritableChannelException expected) {
        }
    }

    @Test
    public void testReadsInWriteOnlyMode() throws IOException {
        FileChannel channel = JimfsFileChannelTest.channel(TestUtils.regularFile(0), StandardOpenOption.WRITE);
        try {
            channel.read(TestUtils.buffer("111"));
            Assert.fail();
        } catch (NonReadableChannelException expected) {
        }
        try {
            channel.read(TestUtils.buffer("111"), 10);
            Assert.fail();
        } catch (NonReadableChannelException expected) {
        }
        try {
            channel.read(new ByteBuffer[]{ TestUtils.buffer("111"), TestUtils.buffer("111") });
            Assert.fail();
        } catch (NonReadableChannelException expected) {
        }
        try {
            channel.read(new ByteBuffer[]{ TestUtils.buffer("111"), TestUtils.buffer("111") }, 0, 2);
            Assert.fail();
        } catch (NonReadableChannelException expected) {
        }
        try {
            channel.transferTo(0, 10, new ByteBufferChannel(TestUtils.buffer("111")));
            Assert.fail();
        } catch (NonReadableChannelException expected) {
        }
        try {
            channel.lock(0, 10, true);
            Assert.fail();
        } catch (NonReadableChannelException expected) {
        }
    }

    @Test
    public void testPositionNegative() throws IOException {
        FileChannel channel = JimfsFileChannelTest.channel(TestUtils.regularFile(0), StandardOpenOption.READ, StandardOpenOption.WRITE);
        try {
            channel.position((-1));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testTruncateNegative() throws IOException {
        FileChannel channel = JimfsFileChannelTest.channel(TestUtils.regularFile(0), StandardOpenOption.READ, StandardOpenOption.WRITE);
        try {
            channel.truncate((-1));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testWriteNegative() throws IOException {
        FileChannel channel = JimfsFileChannelTest.channel(TestUtils.regularFile(0), StandardOpenOption.READ, StandardOpenOption.WRITE);
        try {
            channel.write(TestUtils.buffer("111"), (-1));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        ByteBuffer[] bufs = new ByteBuffer[]{ TestUtils.buffer("111"), TestUtils.buffer("111") };
        try {
            channel.write(bufs, (-1), 10);
            Assert.fail();
        } catch (IndexOutOfBoundsException expected) {
        }
        try {
            channel.write(bufs, 0, (-1));
            Assert.fail();
        } catch (IndexOutOfBoundsException expected) {
        }
    }

    @Test
    public void testReadNegative() throws IOException {
        FileChannel channel = JimfsFileChannelTest.channel(TestUtils.regularFile(0), StandardOpenOption.READ, StandardOpenOption.WRITE);
        try {
            channel.read(TestUtils.buffer("111"), (-1));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        ByteBuffer[] bufs = new ByteBuffer[]{ TestUtils.buffer("111"), TestUtils.buffer("111") };
        try {
            channel.read(bufs, (-1), 10);
            Assert.fail();
        } catch (IndexOutOfBoundsException expected) {
        }
        try {
            channel.read(bufs, 0, (-1));
            Assert.fail();
        } catch (IndexOutOfBoundsException expected) {
        }
    }

    @Test
    public void testTransferToNegative() throws IOException {
        FileChannel channel = JimfsFileChannelTest.channel(TestUtils.regularFile(0), StandardOpenOption.READ, StandardOpenOption.WRITE);
        try {
            channel.transferTo((-1), 0, new ByteBufferChannel(10));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            channel.transferTo(0, (-1), new ByteBufferChannel(10));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testTransferFromNegative() throws IOException {
        FileChannel channel = JimfsFileChannelTest.channel(TestUtils.regularFile(0), StandardOpenOption.READ, StandardOpenOption.WRITE);
        try {
            channel.transferFrom(new ByteBufferChannel(10), (-1), 0);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            channel.transferFrom(new ByteBufferChannel(10), 0, (-1));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testLockNegative() throws IOException {
        FileChannel channel = JimfsFileChannelTest.channel(TestUtils.regularFile(0), StandardOpenOption.READ, StandardOpenOption.WRITE);
        try {
            channel.lock((-1), 10, true);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            channel.lock(0, (-1), true);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            channel.tryLock((-1), 10, true);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            channel.tryLock(0, (-1), true);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testNullPointerExceptions() throws IOException {
        FileChannel channel = JimfsFileChannelTest.channel(TestUtils.regularFile(100), StandardOpenOption.READ, StandardOpenOption.WRITE);
        NullPointerTester tester = new NullPointerTester();
        tester.testAllPublicInstanceMethods(channel);
    }

    @Test
    public void testLock() throws IOException {
        FileChannel channel = JimfsFileChannelTest.channel(TestUtils.regularFile(10), StandardOpenOption.READ, StandardOpenOption.WRITE);
        Assert.assertNotNull(channel.lock());
        Assert.assertNotNull(channel.lock(0, 10, false));
        Assert.assertNotNull(channel.lock(0, 10, true));
        Assert.assertNotNull(channel.tryLock());
        Assert.assertNotNull(channel.tryLock(0, 10, false));
        Assert.assertNotNull(channel.tryLock(0, 10, true));
        FileLock lock = channel.lock();
        Assert.assertTrue(lock.isValid());
        lock.release();
        Assert.assertFalse(lock.isValid());
    }

    @Test
    public void testAsynchronousClose() throws Exception {
        RegularFile file = TestUtils.regularFile(10);
        final FileChannel channel = JimfsFileChannelTest.channel(file, StandardOpenOption.READ, StandardOpenOption.WRITE);
        file.writeLock().lock();// ensure all operations on the channel will block

        ExecutorService executor = Executors.newCachedThreadPool();
        CountDownLatch latch = new CountDownLatch(JimfsFileChannelTest.BLOCKING_OP_COUNT);
        List<Future<?>> futures = queueAllBlockingOperations(channel, executor, latch);
        // wait for all the threads to have started running
        latch.await();
        // then ensure time for operations to start blocking
        Uninterruptibles.sleepUninterruptibly(20, TimeUnit.MILLISECONDS);
        // close channel on this thread
        channel.close();
        // the blocking operations are running on different threads, so they all get
        // AsynchronousCloseException
        for (Future<?> future : futures) {
            try {
                future.get();
                Assert.fail();
            } catch (ExecutionException expected) {
                assertThat(expected.getCause()).named("blocking thread exception").isInstanceOf(AsynchronousCloseException.class);
            }
        }
    }

    @Test
    public void testCloseByInterrupt() throws Exception {
        RegularFile file = TestUtils.regularFile(10);
        final FileChannel channel = JimfsFileChannelTest.channel(file, StandardOpenOption.READ, StandardOpenOption.WRITE);
        file.writeLock().lock();// ensure all operations on the channel will block

        ExecutorService executor = Executors.newCachedThreadPool();
        final CountDownLatch threadStartLatch = new CountDownLatch(1);
        final SettableFuture<Throwable> interruptException = SettableFuture.create();
        // This thread, being the first to run, will be blocking on the interruptible lock (the byte
        // file's write lock) and as such will be interrupted properly... the other threads will be
        // blocked on the lock that guards the position field and the specification that only one method
        // on the channel will be in progress at a time. That lock is not interruptible, so we must
        // interrupt this thread.
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                threadStartLatch.countDown();
                try {
                    channel.write(ByteBuffer.allocate(20));
                    interruptException.set(null);
                } catch (Throwable e) {
                    interruptException.set(e);
                }
            }
        });
        thread.start();
        // let the thread start running
        threadStartLatch.await();
        // then ensure time for thread to start blocking on the write lock
        Uninterruptibles.sleepUninterruptibly(10, TimeUnit.MILLISECONDS);
        CountDownLatch blockingStartLatch = new CountDownLatch(JimfsFileChannelTest.BLOCKING_OP_COUNT);
        List<Future<?>> futures = queueAllBlockingOperations(channel, executor, blockingStartLatch);
        // wait for all blocking threads to start
        blockingStartLatch.await();
        // then ensure time for the operations to start blocking
        Uninterruptibles.sleepUninterruptibly(20, TimeUnit.MILLISECONDS);
        // interrupting this blocking thread closes the channel and makes all the other threads
        // throw AsynchronousCloseException... the operation on this thread should throw
        // ClosedByInterruptException
        thread.interrupt();
        // get the exception that caused the interrupted operation to terminate
        assertThat(interruptException.get(200, TimeUnit.MILLISECONDS)).named("interrupted thread exception").isInstanceOf(ClosedByInterruptException.class);
        // check that each other thread got AsynchronousCloseException (since the interrupt, on a
        // different thread, closed the channel)
        for (Future<?> future : futures) {
            try {
                future.get();
                Assert.fail();
            } catch (ExecutionException expected) {
                assertThat(expected.getCause()).named("blocking thread exception").isInstanceOf(AsynchronousCloseException.class);
            }
        }
    }

    private static final int BLOCKING_OP_COUNT = 10;

    /**
     * Tests that the methods on the default FileChannel that support InterruptibleChannel behavior
     * also support it on JimfsFileChannel, by just interrupting the thread before calling the
     * method.
     */
    @Test
    public void testInterruptedThreads() throws IOException {
        final ByteBuffer buf = ByteBuffer.allocate(10);
        final ByteBuffer[] bufArray = new ByteBuffer[]{ buf };
        JimfsFileChannelTest.assertClosedByInterrupt(new JimfsFileChannelTest.FileChannelMethod() {
            @Override
            public void call(FileChannel channel) throws IOException {
                channel.size();
            }
        });
        JimfsFileChannelTest.assertClosedByInterrupt(new JimfsFileChannelTest.FileChannelMethod() {
            @Override
            public void call(FileChannel channel) throws IOException {
                channel.position();
            }
        });
        JimfsFileChannelTest.assertClosedByInterrupt(new JimfsFileChannelTest.FileChannelMethod() {
            @Override
            public void call(FileChannel channel) throws IOException {
                channel.position(0);
            }
        });
        JimfsFileChannelTest.assertClosedByInterrupt(new JimfsFileChannelTest.FileChannelMethod() {
            @Override
            public void call(FileChannel channel) throws IOException {
                channel.write(buf);
            }
        });
        JimfsFileChannelTest.assertClosedByInterrupt(new JimfsFileChannelTest.FileChannelMethod() {
            @Override
            public void call(FileChannel channel) throws IOException {
                channel.write(bufArray, 0, 1);
            }
        });
        JimfsFileChannelTest.assertClosedByInterrupt(new JimfsFileChannelTest.FileChannelMethod() {
            @Override
            public void call(FileChannel channel) throws IOException {
                channel.read(buf);
            }
        });
        JimfsFileChannelTest.assertClosedByInterrupt(new JimfsFileChannelTest.FileChannelMethod() {
            @Override
            public void call(FileChannel channel) throws IOException {
                channel.read(bufArray, 0, 1);
            }
        });
        JimfsFileChannelTest.assertClosedByInterrupt(new JimfsFileChannelTest.FileChannelMethod() {
            @Override
            public void call(FileChannel channel) throws IOException {
                channel.write(buf, 0);
            }
        });
        JimfsFileChannelTest.assertClosedByInterrupt(new JimfsFileChannelTest.FileChannelMethod() {
            @Override
            public void call(FileChannel channel) throws IOException {
                channel.read(buf, 0);
            }
        });
        JimfsFileChannelTest.assertClosedByInterrupt(new JimfsFileChannelTest.FileChannelMethod() {
            @Override
            public void call(FileChannel channel) throws IOException {
                channel.transferTo(0, 1, JimfsFileChannelTest.channel(TestUtils.regularFile(10), StandardOpenOption.READ, StandardOpenOption.WRITE));
            }
        });
        JimfsFileChannelTest.assertClosedByInterrupt(new JimfsFileChannelTest.FileChannelMethod() {
            @Override
            public void call(FileChannel channel) throws IOException {
                channel.transferFrom(JimfsFileChannelTest.channel(TestUtils.regularFile(10), StandardOpenOption.READ, StandardOpenOption.WRITE), 0, 1);
            }
        });
        JimfsFileChannelTest.assertClosedByInterrupt(new JimfsFileChannelTest.FileChannelMethod() {
            @Override
            public void call(FileChannel channel) throws IOException {
                channel.force(true);
            }
        });
        JimfsFileChannelTest.assertClosedByInterrupt(new JimfsFileChannelTest.FileChannelMethod() {
            @Override
            public void call(FileChannel channel) throws IOException {
                channel.truncate(0);
            }
        });
        JimfsFileChannelTest.assertClosedByInterrupt(new JimfsFileChannelTest.FileChannelMethod() {
            @Override
            public void call(FileChannel channel) throws IOException {
                channel.lock(0, 1, true);
            }
        });
        // tryLock() does not handle interruption
        // map() always throws UOE; it doesn't make sense for it to try to handle interruption
    }

    private interface FileChannelMethod {
        void call(FileChannel channel) throws IOException;
    }
}

