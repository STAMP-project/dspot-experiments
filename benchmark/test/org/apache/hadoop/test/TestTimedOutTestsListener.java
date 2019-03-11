/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.test;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.notification.Failure;


public class TestTimedOutTestsListener {
    public static class Deadlock {
        private CyclicBarrier barrier = new CyclicBarrier(6);

        public Deadlock() {
            TestTimedOutTestsListener.Deadlock.DeadlockThread[] dThreads = new TestTimedOutTestsListener.Deadlock.DeadlockThread[6];
            TestTimedOutTestsListener.Deadlock.Monitor a = new TestTimedOutTestsListener.Deadlock.Monitor("a");
            TestTimedOutTestsListener.Deadlock.Monitor b = new TestTimedOutTestsListener.Deadlock.Monitor("b");
            TestTimedOutTestsListener.Deadlock.Monitor c = new TestTimedOutTestsListener.Deadlock.Monitor("c");
            dThreads[0] = new TestTimedOutTestsListener.Deadlock.DeadlockThread("MThread-1", a, b);
            dThreads[1] = new TestTimedOutTestsListener.Deadlock.DeadlockThread("MThread-2", b, c);
            dThreads[2] = new TestTimedOutTestsListener.Deadlock.DeadlockThread("MThread-3", c, a);
            Lock d = new ReentrantLock();
            Lock e = new ReentrantLock();
            Lock f = new ReentrantLock();
            dThreads[3] = new TestTimedOutTestsListener.Deadlock.DeadlockThread("SThread-4", d, e);
            dThreads[4] = new TestTimedOutTestsListener.Deadlock.DeadlockThread("SThread-5", e, f);
            dThreads[5] = new TestTimedOutTestsListener.Deadlock.DeadlockThread("SThread-6", f, d);
            // make them daemon threads so that the test will exit
            for (int i = 0; i < 6; i++) {
                dThreads[i].setDaemon(true);
                dThreads[i].start();
            }
        }

        class DeadlockThread extends Thread {
            private Lock lock1 = null;

            private Lock lock2 = null;

            private TestTimedOutTestsListener.Deadlock.Monitor mon1 = null;

            private TestTimedOutTestsListener.Deadlock.Monitor mon2 = null;

            private boolean useSync;

            DeadlockThread(String name, Lock lock1, Lock lock2) {
                super(name);
                this.lock1 = lock1;
                this.lock2 = lock2;
                this.useSync = true;
            }

            DeadlockThread(String name, TestTimedOutTestsListener.Deadlock.Monitor mon1, TestTimedOutTestsListener.Deadlock.Monitor mon2) {
                super(name);
                this.mon1 = mon1;
                this.mon2 = mon2;
                this.useSync = false;
            }

            public void run() {
                if (useSync) {
                    syncLock();
                } else {
                    monitorLock();
                }
            }

            private void syncLock() {
                lock1.lock();
                try {
                    try {
                        barrier.await();
                    } catch (Exception e) {
                    }
                    goSyncDeadlock();
                } finally {
                    lock1.unlock();
                }
            }

            private void goSyncDeadlock() {
                try {
                    barrier.await();
                } catch (Exception e) {
                }
                lock2.lock();
                throw new RuntimeException("should not reach here.");
            }

            private void monitorLock() {
                synchronized(mon1) {
                    try {
                        barrier.await();
                    } catch (Exception e) {
                    }
                    goMonitorDeadlock();
                }
            }

            private void goMonitorDeadlock() {
                try {
                    barrier.await();
                } catch (Exception e) {
                }
                synchronized(mon2) {
                    throw new RuntimeException(((getName()) + " should not reach here."));
                }
            }
        }

        class Monitor {
            String name;

            Monitor(String name) {
                this.name = name;
            }
        }
    }

    @Test(timeout = 30000)
    public void testThreadDumpAndDeadlocks() throws Exception {
        new TestTimedOutTestsListener.Deadlock();
        String s = null;
        while (true) {
            s = TimedOutTestsListener.buildDeadlockInfo();
            if (s != null)
                break;

            Thread.sleep(100);
        } 
        Assert.assertEquals(3, countStringOccurrences(s, "BLOCKED"));
        Failure failure = new Failure(null, new Exception(TimedOutTestsListener.TEST_TIMED_OUT_PREFIX));
        StringWriter writer = new StringWriter();
        new TimedOutTestsListener(new PrintWriter(writer)).testFailure(failure);
        String out = writer.toString();
        Assert.assertTrue(out.contains("THREAD DUMP"));
        Assert.assertTrue(out.contains("DEADLOCKS DETECTED"));
        System.out.println(out);
    }
}

