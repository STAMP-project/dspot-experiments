/**
 * Copyright (C) 2015 RoboVM AB
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
package org.robovm.rt.bro;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;
import org.robovm.apple.dispatch.DispatchQueue;


/**
 * Tests that attaching of native threads in {@link Callback}s are only done
 * once even if the {@link Callback} is called multiple times from native code.
 */
public class CallbackAttachTest {
    @Test
    public void testAttachesOnlyOnce() throws Exception {
        final Set<Thread> threads = Collections.synchronizedSet(new HashSet<Thread>());
        final CountDownLatch latch = new CountDownLatch(20);
        DispatchQueue q1 = DispatchQueue.create("q1", null);
        DispatchQueue q2 = DispatchQueue.create("q2", null);
        for (int i = 0; i < 20; i++) {
            ((i & 1) == 0 ? q1 : q2).async(new Runnable() {
                public void run() {
                    threads.add(Thread.currentThread());
                    latch.countDown();
                    try {
                        // Sleep some to prevent dispatch from using the same
                        // worker thread for both queues
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                    }
                }
            });
        }
        latch.await();
        Assert.assertEquals(2, threads.size());
        List<Thread> l = new ArrayList<>(threads);
        Thread t1 = l.get(0);
        Thread t2 = l.get(1);
        Assert.assertTrue(t1.isDaemon());
        Assert.assertTrue(t2.isDaemon());
        Assert.assertTrue(t1.isAlive());
        Assert.assertTrue(t2.isAlive());
        Assert.assertEquals(0, t1.getStackTrace().length);
        Assert.assertEquals(0, t2.getStackTrace().length);
        q1.release();
        q2.release();
        t1.join(15000);
        t2.join(15000);
        Assert.assertFalse(t1.isAlive());
        Assert.assertFalse(t2.isAlive());
    }
}

