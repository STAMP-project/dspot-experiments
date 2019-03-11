/**
 * Copyright 2014 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.util.concurrent;


import io.netty.util.internal.ObjectCleaner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class FastThreadLocalTest {
    @Test(timeout = 10000)
    public void testRemoveAll() throws Exception {
        final AtomicBoolean removed = new AtomicBoolean();
        final FastThreadLocal<Boolean> var = new FastThreadLocal<Boolean>() {
            @Override
            protected void onRemoval(Boolean value) {
                removed.set(true);
            }
        };
        // Initialize a thread-local variable.
        Assert.assertThat(var.get(), CoreMatchers.is(Matchers.nullValue()));
        Assert.assertThat(FastThreadLocal.size(), CoreMatchers.is(1));
        // And then remove it.
        FastThreadLocal.removeAll();
        Assert.assertThat(removed.get(), CoreMatchers.is(true));
        Assert.assertThat(FastThreadLocal.size(), CoreMatchers.is(0));
    }

    @Test(timeout = 10000)
    public void testRemoveAllFromFTLThread() throws Throwable {
        final AtomicReference<Throwable> throwable = new AtomicReference<Throwable>();
        final Thread thread = new FastThreadLocalThread() {
            @Override
            public void run() {
                try {
                    testRemoveAll();
                } catch (Throwable t) {
                    throwable.set(t);
                }
            }
        };
        thread.start();
        thread.join();
        Throwable t = throwable.get();
        if (t != null) {
            throw t;
        }
    }

    @Test
    public void testMultipleSetRemove() throws Exception {
        final FastThreadLocal<String> threadLocal = new FastThreadLocal<String>();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                threadLocal.set("1");
                threadLocal.remove();
                threadLocal.set("2");
                threadLocal.remove();
            }
        };
        final int sizeWhenStart = ObjectCleaner.getLiveSetCount();
        Thread thread = new Thread(runnable);
        thread.start();
        thread.join();
        Assert.assertEquals(0, ((ObjectCleaner.getLiveSetCount()) - sizeWhenStart));
        Thread thread2 = new Thread(runnable);
        thread2.start();
        thread2.join();
        Assert.assertEquals(0, ((ObjectCleaner.getLiveSetCount()) - sizeWhenStart));
    }

    @Test
    public void testMultipleSetRemove_multipleThreadLocal() throws Exception {
        final FastThreadLocal<String> threadLocal = new FastThreadLocal<String>();
        final FastThreadLocal<String> threadLocal2 = new FastThreadLocal<String>();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                threadLocal.set("1");
                threadLocal.remove();
                threadLocal.set("2");
                threadLocal.remove();
                threadLocal2.set("1");
                threadLocal2.remove();
                threadLocal2.set("2");
                threadLocal2.remove();
            }
        };
        final int sizeWhenStart = ObjectCleaner.getLiveSetCount();
        Thread thread = new Thread(runnable);
        thread.start();
        thread.join();
        Assert.assertEquals(0, ((ObjectCleaner.getLiveSetCount()) - sizeWhenStart));
        Thread thread2 = new Thread(runnable);
        thread2.start();
        thread2.join();
        Assert.assertEquals(0, ((ObjectCleaner.getLiveSetCount()) - sizeWhenStart));
    }

    @Test(timeout = 4000)
    public void testOnRemoveCalledForFastThreadLocalGet() throws Exception {
        FastThreadLocalTest.testOnRemoveCalled(true, true);
    }

    @Test(timeout = 4000)
    public void testOnRemoveCalledForFastThreadLocalSet() throws Exception {
        FastThreadLocalTest.testOnRemoveCalled(true, false);
    }

    private static final class TestFastThreadLocal extends FastThreadLocal<String> {
        final AtomicReference<String> onRemovalCalled = new AtomicReference<String>();

        @Override
        protected String initialValue() throws Exception {
            return Thread.currentThread().getName();
        }

        @Override
        protected void onRemoval(String value) throws Exception {
            onRemovalCalled.set(value);
        }
    }
}

