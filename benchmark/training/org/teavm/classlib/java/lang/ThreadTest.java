/**
 * Copyright 2015 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.classlib.java.lang;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
public class ThreadTest {
    @Test
    public void sleeps() throws InterruptedException {
        long start = System.currentTimeMillis();
        Thread.sleep(100);
        long duration = (System.currentTimeMillis()) - start;
        Assert.assertTrue("Thread.sleep did not wait enough", (duration >= 100));
    }

    @Test
    public void sleepInterrupted() {
        long start = System.currentTimeMillis();
        final Thread mainThread = Thread.currentThread();
        new Thread(() -> {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                // ok
            }
            mainThread.interrupt();
        }).start();
        try {
            Thread.sleep(5000);
            Assert.fail("Exception expected");
        } catch (InterruptedException e) {
            Assert.assertEquals(Thread.currentThread(), mainThread);
            Assert.assertFalse(mainThread.isInterrupted());
            Assert.assertTrue((((System.currentTimeMillis()) - start) < 500));
        }
    }

    @Test
    public void catchesAsyncException() {
        try {
            throwException();
            Assert.fail("Exception should have been thrown");
        } catch (IllegalStateException e) {
            // all is ok
        }
    }

    @Test
    public void asyncVirtualCallsSupported() {
        List<ThreadTest.A> alist = new ArrayList<>();
        alist.add(new ThreadTest.A() {
            @Override
            int foo() {
                return 3;
            }
        });
        alist.add(new ThreadTest.A() {
            @Override
            int foo() {
                Thread.yield();
                return 5;
            }
        });
        int sum = 0;
        for (ThreadTest.A a : alist) {
            sum += a.foo();
        }
        Assert.assertEquals(8, sum);
    }

    abstract class A {
        abstract int foo();
    }
}

