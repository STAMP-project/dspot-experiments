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


import com.github.rholder.retry.RetryException;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import stormpot.Allocator;
import stormpot.Poolable;
import stormpot.Slot;


@RunWith(MockitoJUnitRunner.class)
public class RetryingAllocatorTest {
    @Mock
    private Allocator<RetryingAllocatorTest.TestPoolable> allocator;

    private RetryingAllocator<RetryingAllocatorTest.TestPoolable> retryingAllocator;

    @Mock
    private Slot slot;

    @Test
    public void testDeallocate() throws Exception {
        RetryingAllocatorTest.TestPoolable value = new RetryingAllocatorTest.TestPoolable("value");
        retryingAllocator.deallocate(value);
        Mockito.verify(allocator).deallocate(value);
    }

    @Test
    public void testAllocate() throws Exception {
        RetryingAllocatorTest.TestPoolable value = new RetryingAllocatorTest.TestPoolable("value");
        Mockito.when(allocator.allocate(slot)).thenReturn(value);
        Assert.assertSame(value, retryingAllocator.allocate(slot));
    }

    @Test
    public void testAllocate_FailThenSucceed() throws Exception {
        RetryingAllocatorTest.TestPoolable value = new RetryingAllocatorTest.TestPoolable("value");
        Mockito.when(allocator.allocate(slot)).thenThrow(new IOException()).thenThrow(new IOException()).thenThrow(new IOException()).thenReturn(value);
        Assert.assertSame(value, retryingAllocator.allocate(slot));
        Mockito.verify(allocator, Mockito.atLeast(2)).allocate(slot);
    }

    @Test(expected = RetryException.class)
    public void testAllocate_Fail() throws Exception {
        Mockito.when(allocator.allocate(slot)).thenThrow(new IOException());
        retryingAllocator.allocate(slot);
    }

    private static class TestPoolable implements Poolable {
        public String value;

        public TestPoolable(String value) {
            this.value = value;
        }

        @Override
        public void release() {
        }
    }
}

