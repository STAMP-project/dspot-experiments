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
package com.googlecode.jmxtrans.model.output.support.pool;


import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import stormpot.BlazePool;
import stormpot.Timeout;


@RunWith(MockitoJUnitRunner.class)
public class WriterPoolableTest {
    @Mock
    private Writer writer;

    @Test
    public void writerIsFlushedIfStrategyRequiresIt() throws IOException, InterruptedException {
        BlazePool<WriterPoolable> pool = createPool();
        WriterPoolable writerPoolable = pool.claim(new Timeout(1, TimeUnit.SECONDS));
        writerPoolable.getWriter().write("some message");
        writerPoolable.release();
        Mockito.verify(writer).flush();
    }

    @Test
    public void ensureSlotIsReleasedOnException() throws IOException, InterruptedException {
        BlazePool<WriterPoolable> pool = createPool(new WriterPoolableTest.ExceptionOnFlush());
        WriterPoolable writerPoolable = pool.claim(new Timeout(1, TimeUnit.SECONDS));
        writerPoolable.getWriter().write("some message");
        writerPoolable.release();
        // Slot should be able to be reclaimed
        writerPoolable = pool.claim(new Timeout(1, TimeUnit.SECONDS));
        Assert.assertNotNull(writerPoolable);
    }

    private class ExceptionOnFlush implements FlushStrategy {
        @Override
        public void flush(@Nonnull
        Flushable flushable) throws IOException {
            throw new IOException();
        }
    }
}

