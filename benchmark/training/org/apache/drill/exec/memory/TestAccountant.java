/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.memory;


import org.apache.drill.categories.MemoryTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(MemoryTest.class)
public class TestAccountant {
    @Test
    public void basic() {
        ensureAccurateReservations(null);
    }

    @Test
    public void nested() {
        final Accountant parent = new Accountant(null, 0, Long.MAX_VALUE);
        ensureAccurateReservations(parent);
        Assert.assertEquals(0, parent.getAllocatedMemory());
    }

    @Test
    public void multiThread() throws InterruptedException {
        final Accountant parent = new Accountant(null, 0, Long.MAX_VALUE);
        final int numberOfThreads = 32;
        final int loops = 100;
        Thread[] threads = new Thread[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++) {
            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        for (int i = 0; i < loops; i++) {
                            ensureAccurateReservations(parent);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Assert.fail(ex.getMessage());
                    }
                }
            };
            threads[i] = t;
            t.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        Assert.assertEquals(0, parent.getAllocatedMemory());
    }
}

