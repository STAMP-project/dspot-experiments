/**
 * The MIT License
 * Copyright ? 2010 JmxTrans team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.googlecode.jmxtrans.model.output.support;


import com.google.common.collect.ImmutableList;
import com.googlecode.jmxtrans.model.Query;
import com.googlecode.jmxtrans.model.QueryFixtures;
import com.googlecode.jmxtrans.model.ResultFixtures;
import com.googlecode.jmxtrans.model.Server;
import com.googlecode.jmxtrans.model.ServerFixtures;
import com.googlecode.jmxtrans.model.output.support.pool.NeverFlush;
import com.googlecode.jmxtrans.model.output.support.pool.WriterPoolable;
import com.googlecode.jmxtrans.test.IntegrationTest;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import stormpot.Allocator;
import stormpot.Config;
import stormpot.LifecycledPool;
import stormpot.Slot;
import stormpot.Timeout;


@RunWith(MockitoJUnitRunner.class)
public class WriterPoolOutputWriterTest {
    private LifecycledPool<WriterPoolable> pool;

    @Spy
    private WriterPoolOutputWriterTest.DummyAllocator allocator = new WriterPoolOutputWriterTest.DummyAllocator();

    @Mock
    private WriterBasedOutputWriter target;

    private Writer writer = new StringWriter();

    @Captor
    private ArgumentCaptor<Writer> writerCaptor;

    @Test
    public void writerIsPassedToTargetOutputWriter() throws Exception {
        WriterPoolOutputWriter<WriterBasedOutputWriter> outputWriter = new WriterPoolOutputWriter(target, pool, new Timeout(1, TimeUnit.SECONDS));
        outputWriter.doWrite(ServerFixtures.dummyServer(), QueryFixtures.dummyQuery(), ResultFixtures.dummyResults());
        Mockito.verify(target).write(writerCaptor.capture(), ArgumentMatchers.any(Server.class), ArgumentMatchers.any(Query.class), ArgumentMatchers.any(ImmutableList.class));
        assertThat(writerCaptor.getValue()).isSameAs(writer);
    }

    @Test(expected = IllegalStateException.class)
    public void claimTimeout() throws Exception {
        Timeout sleepTime = new Timeout(10, TimeUnit.SECONDS);
        Timeout claimTime = new Timeout(1, TimeUnit.SECONDS);
        Config<WriterPoolable> config = new Config<WriterPoolable>().setAllocator(new WriterPoolOutputWriterTest.TimeoutAllocator(sleepTime));
        pool = new stormpot.BlazePool(config);
        WriterPoolOutputWriter<WriterBasedOutputWriter> outputWriter = new WriterPoolOutputWriter(target, pool, claimTime);
        outputWriter.doWrite(ServerFixtures.dummyServer(), QueryFixtures.dummyQuery(), ResultFixtures.dummyResults());
    }

    @Test(expected = IOException.class)
    @Category(IntegrationTest.class)
    public void poolableIsReleasedOnIOException() throws Exception {
        Mockito.doThrow(IOException.class).when(target).write(ArgumentMatchers.any(Writer.class), ArgumentMatchers.any(Server.class), ArgumentMatchers.any(Query.class), ArgumentMatchers.any(ImmutableList.class));
        WriterPoolOutputWriter<WriterBasedOutputWriter> outputWriter = new WriterPoolOutputWriter(target, pool, new Timeout(1, TimeUnit.SECONDS));
        try {
            outputWriter.doWrite(ServerFixtures.dummyServer(), QueryFixtures.dummyQuery(), ResultFixtures.dummyResults());
        } finally {
            Mockito.verify(allocator, Mockito.timeout(500)).deallocate(ArgumentMatchers.any(WriterPoolable.class));
        }
    }

    private class DummyAllocator implements Allocator<WriterPoolable> {
        @Override
        public WriterPoolable allocate(Slot slot) throws Exception {
            return new com.googlecode.jmxtrans.model.output.support.pool.SocketPoolable(slot, null, writer, new NeverFlush());
        }

        @Override
        public void deallocate(WriterPoolable poolable) throws Exception {
            poolable.release();
        }
    }

    private class TimeoutAllocator implements Allocator<WriterPoolable> {
        private Timeout timeout;

        public TimeoutAllocator(Timeout timeout) {
            this.timeout = timeout;
        }

        @Override
        public WriterPoolable allocate(Slot slot) throws Exception {
            try {
                Thread.sleep(timeout.getUnit().toMillis(timeout.getTimeout()));
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            return new com.googlecode.jmxtrans.model.output.support.pool.SocketPoolable(slot, null, writer, new NeverFlush());
        }

        @Override
        public void deallocate(WriterPoolable poolable) throws Exception {
        }
    }
}

