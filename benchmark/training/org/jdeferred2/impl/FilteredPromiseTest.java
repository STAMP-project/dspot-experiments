/**
 * Copyright 2013-2018 Ray Tsang
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
package org.jdeferred2.impl;


import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import org.jdeferred2.DoneFilter;
import org.junit.Assert;
import org.junit.Test;


public class FilteredPromiseTest extends AbstractDeferredTest {
    @Test
    public void testNoOpFilter() {
        final AtomicInteger doneCount = new AtomicInteger();
        final AtomicInteger failCount = new AtomicInteger();
        deferredManager.when(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "DONE";
            }
        }).filter(new FilteredPromise.NoOpDoneFilter<String>()).done(new org.jdeferred2.DoneCallback<String>() {
            @Override
            public void onDone(String result) {
                Assert.assertEquals("DONE", result);
                doneCount.incrementAndGet();
            }
        });
        deferredManager.when(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "DONE2";
            }
        }).filter(((DoneFilter<String, String>) (null)), new FilteredPromise.NoOpFailFilter<Throwable>()).done(new org.jdeferred2.DoneCallback<String>() {
            @Override
            public void onDone(String result) {
                Assert.assertEquals("DONE2", result);
                doneCount.incrementAndGet();
            }
        });
        deferredManager.when(new Callable<String>() {
            @Override
            public String call() throws Exception {
                throw new RuntimeException("FAIL");
            }
        }).<String, Throwable, Void>filter(null, null, new FilteredPromise.NoOpProgressFilter<Void>()).fail(new org.jdeferred2.FailCallback<Throwable>() {
            @Override
            public void onFail(Throwable result) {
                Assert.assertEquals("FAIL", result.getMessage());
                failCount.incrementAndGet();
            }
        });
        final AtomicInteger progressCount = new AtomicInteger();
        deferredManager.when(new org.jdeferred2.DeferredRunnable<String>() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                    }
                    notify("HI");
                }
            }
        }).filter(null, null, new FilteredPromise.NoOpProgressFilter<String>()).progress(new org.jdeferred2.ProgressCallback<String>() {
            @Override
            public void onProgress(String progress) {
                Assert.assertEquals("HI", progress);
                progressCount.incrementAndGet();
            }
        });
        waitForCompletion();
        Assert.assertEquals(2, doneCount.get());
        Assert.assertEquals(1, failCount.get());
        Assert.assertEquals(10, progressCount.get());
    }

    @Test
    public void testDoneFilter() {
        final ValueHolder<String> holder = new ValueHolder<String>();
        Callable<Integer> task = new Callable<Integer>() {
            public Integer call() {
                return 100;
            }
        };
        deferredManager.when(task).filter(new DoneFilter<Integer, String>() {
            @Override
            public String filterDone(Integer result) {
                return "TEST-" + (result.toString());
            }
        }).done(new org.jdeferred2.DoneCallback<String>() {
            @Override
            public void onDone(String result) {
                holder.set(result);
            }
        });
        waitForCompletion();
        holder.assertEquals("TEST-100");
    }

    @Test
    public void testFailFilter() {
        final ValueHolder<String> holder = new ValueHolder<String>();
        Callable<Integer> task = new Callable<Integer>() {
            public Integer call() {
                throw new RuntimeException("TEST");
            }
        };
        deferredManager.when(task).filter(null, new org.jdeferred2.FailFilter<Throwable, String>() {
            @Override
            public String filterFail(Throwable result) {
                return result.getMessage();
            }
        }).fail(new org.jdeferred2.FailCallback<String>() {
            @Override
            public void onFail(String result) {
                holder.set(result);
            }
        });
        waitForCompletion();
        holder.assertEquals("TEST");
    }
}

