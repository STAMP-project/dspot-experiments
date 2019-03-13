/**
 * Copyright (C) 2007 The Android Open Source Project
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
package libcore.java.lang;


import junit.framework.TestCase;


public class OldAndroidMonitorTest extends TestCase {
    public void testWaitArgumentsTest() throws Exception {
        /* Try some valid arguments.  These should all
        return very quickly.
         */
        try {
            synchronized(this) {
                /* millisecond version */
                wait(1);
                wait(10);
                /* millisecond + nanosecond version */
                wait(0, 1);
                wait(0, 999999);
                wait(1, 1);
                wait(1, 999999);
            }
        } catch (InterruptedException ex) {
            throw new RuntimeException("good Object.wait() interrupted", ex);
        } catch (Exception ex) {
            throw new RuntimeException(("Unexpected exception when calling" + "Object.wait() with good arguments"), ex);
        }
        /* Try some invalid arguments. */
        boolean sawException = false;
        try {
            synchronized(this) {
                wait((-1));
            }
        } catch (InterruptedException ex) {
            throw new RuntimeException("bad Object.wait() interrupted", ex);
        } catch (IllegalArgumentException ex) {
            sawException = true;
        } catch (Exception ex) {
            throw new RuntimeException(("Unexpected exception when calling" + "Object.wait() with bad arguments"), ex);
        }
        if (!sawException) {
            throw new RuntimeException(("bad call to Object.wait() should " + "have thrown IllegalArgumentException"));
        }
        sawException = false;
        try {
            synchronized(this) {
                wait(0, (-1));
            }
        } catch (InterruptedException ex) {
            throw new RuntimeException("bad Object.wait() interrupted", ex);
        } catch (IllegalArgumentException ex) {
            sawException = true;
        } catch (Exception ex) {
            throw new RuntimeException(("Unexpected exception when calling" + "Object.wait() with bad arguments"), ex);
        }
        if (!sawException) {
            throw new RuntimeException(("bad call to Object.wait() should " + "have thrown IllegalArgumentException"));
        }
        sawException = false;
        try {
            synchronized(this) {
                /* The legal range of nanos is 0-999999. */
                wait(0, 1000000);
            }
        } catch (InterruptedException ex) {
            throw new RuntimeException("bad Object.wait() interrupted", ex);
        } catch (IllegalArgumentException ex) {
            sawException = true;
        } catch (Exception ex) {
            throw new RuntimeException(("Unexpected exception when calling" + "Object.wait() with bad arguments"), ex);
        }
        if (!sawException) {
            throw new RuntimeException(("bad call to Object.wait() should " + "have thrown IllegalArgumentException"));
        }
    }

    private class Interrupter extends Thread {
        OldAndroidMonitorTest.Waiter waiter;

        Interrupter(String name, OldAndroidMonitorTest.Waiter waiter) {
            super(name);
            this.waiter = waiter;
        }

        public void run() {
            try {
                run_inner();
            } catch (Throwable t) {
                OldAndroidMonitorTest.errorException = t;
                OldAndroidMonitorTest.testThread.interrupt();
            }
        }

        void run_inner() {
            waiter.spin = true;
            // System.out.println("InterruptTest: starting waiter");
            waiter.start();
            try {
                Thread.currentThread().sleep(500);
            } catch (InterruptedException ex) {
                throw new RuntimeException("Test sleep interrupted.", ex);
            }
            /* Waiter is spinning, and its monitor should still be thin. */
            // System.out.println("Test interrupting waiter");
            waiter.interrupt();
            waiter.spin = false;
            for (int i = 0; i < 3; i++) {
                /* Wait for the waiter to start waiting. */
                synchronized(waiter.interrupterLock) {
                    try {
                        waiter.interrupterLock.wait();
                    } catch (InterruptedException ex) {
                        throw new RuntimeException("Test wait interrupted.", ex);
                    }
                }
                /* Before interrupting, grab the waiter lock, which
                guarantees that the waiter is already sitting in wait().
                 */
                synchronized(waiter) {
                    // System.out.println("Test interrupting waiter (" + i + ")");
                    waiter.interrupt();
                }
            }
            // System.out.println("Test waiting for waiter to die.");
            try {
                waiter.join();
            } catch (InterruptedException ex) {
                throw new RuntimeException("Test join interrupted.", ex);
            }
            // System.out.println("InterruptTest done.");
        }
    }

    private class Waiter extends Thread {
        Object interrupterLock = new Object();

        Boolean spin = false;

        Waiter(String name) {
            super(name);
        }

        public void run() {
            try {
                run_inner();
            } catch (Throwable t) {
                OldAndroidMonitorTest.errorException = t;
                OldAndroidMonitorTest.testThread.interrupt();
            }
        }

        void run_inner() {
            // System.out.println("Waiter spinning");
            while (spin) {
                // We're going to get interrupted while we spin.
            } 
            if (Thread.interrupted()) {
                // System.out.println("Waiter done spinning; interrupted.");
            } else {
                throw new RuntimeException(("Thread not interrupted " + "during spin"));
            }
            synchronized(this) {
                Boolean sawEx = false;
                try {
                    synchronized(interrupterLock) {
                        interrupterLock.notify();
                    }
                    // System.out.println("Waiter calling wait()");
                    this.wait();
                } catch (InterruptedException ex) {
                    sawEx = true;
                    // System.out.println("wait(): Waiter caught " + ex);
                }
                // System.out.println("wait() finished");
                if (!sawEx) {
                    throw new RuntimeException(("Thread not interrupted " + "during wait()"));
                }
            }
            synchronized(this) {
                Boolean sawEx = false;
                try {
                    synchronized(interrupterLock) {
                        interrupterLock.notify();
                    }
                    // System.out.println("Waiter calling wait(1000)");
                    this.wait(1000);
                } catch (InterruptedException ex) {
                    sawEx = true;
                    // System.out.println("wait(1000): Waiter caught " + ex);
                }
                // System.out.println("wait(1000) finished");
                if (!sawEx) {
                    throw new RuntimeException(("Thread not interrupted " + "during wait(1000)"));
                }
            }
            synchronized(this) {
                Boolean sawEx = false;
                try {
                    synchronized(interrupterLock) {
                        interrupterLock.notify();
                    }
                    // System.out.println("Waiter calling wait(1000, 5000)");
                    this.wait(1000, 5000);
                } catch (InterruptedException ex) {
                    sawEx = true;
                    // System.out.println("wait(1000, 5000): Waiter caught " + ex);
                }
                // System.out.println("wait(1000, 5000) finished");
                if (!sawEx) {
                    throw new RuntimeException(("Thread not interrupted " + "during wait(1000, 5000)"));
                }
            }
            // System.out.println("Waiter returning");
        }
    }

    private static Throwable errorException;

    private static Thread testThread;

    // TODO: Flaky test. Add back MediumTest annotation once fixed
    public void testInterruptTest() throws Exception {
        OldAndroidMonitorTest.testThread = Thread.currentThread();
        OldAndroidMonitorTest.errorException = null;
        OldAndroidMonitorTest.Waiter waiter = new OldAndroidMonitorTest.Waiter("InterruptTest Waiter");
        OldAndroidMonitorTest.Interrupter interrupter = new OldAndroidMonitorTest.Interrupter("InterruptTest Interrupter", waiter);
        interrupter.start();
        try {
            interrupter.join();
            waiter.join();
        } catch (InterruptedException ex) {
            throw new RuntimeException("Test join interrupted.", ex);
        }
        if ((OldAndroidMonitorTest.errorException) != null) {
            throw new RuntimeException("InterruptTest failed", OldAndroidMonitorTest.errorException);
        }
    }

    private class Worker extends Thread {
        Object lock;

        int id;

        Worker(int id, Object lock) {
            super((("Worker(" + id) + ")"));
            this.id = id;
            this.lock = lock;
        }

        public void run() {
            int iterations = 0;
            while (OldAndroidMonitorTest.running) {
                OldAndroidMonitorTest.deepWait(id, lock);
                iterations++;
            } 
            // System.out.println(getName() + " done after " + iterations + " iterations.");
        }
    }

    private static Object commonLock = new Object();

    private static Boolean running = false;

    public void testNestedMonitors() throws Exception {
        final int NUM_WORKERS = 5;
        OldAndroidMonitorTest.Worker[] w = new OldAndroidMonitorTest.Worker[NUM_WORKERS];
        int i;
        for (i = 0; i < NUM_WORKERS; i++) {
            w[i] = new OldAndroidMonitorTest.Worker(((i * 2) - 1), new Object());
        }
        OldAndroidMonitorTest.running = true;
        // System.out.println("NestedMonitors: starting workers");
        for (i = 0; i < NUM_WORKERS; i++) {
            w[i].start();
        }
        try {
            Thread.currentThread().sleep(1000);
        } catch (InterruptedException ex) {
            // System.out.println("Test sleep interrupted.");
        }
        for (i = 0; i < 100; i++) {
            for (int j = 0; j < NUM_WORKERS; j++) {
                synchronized(w[j].lock) {
                    w[j].lock.notify();
                }
            }
        }
        // System.out.println("NesterMonitors: stopping workers");
        OldAndroidMonitorTest.running = false;
        for (i = 0; i < NUM_WORKERS; i++) {
            synchronized(w[i].lock) {
                w[i].lock.notifyAll();
            }
        }
    }

    private static class CompareAndExchange extends Thread {
        static Object toggleLock = null;

        static int toggle = -1;

        static Boolean running = false;

        public void run() {
            OldAndroidMonitorTest.CompareAndExchange.toggleLock = new Object();
            OldAndroidMonitorTest.CompareAndExchange.toggle = -1;
            OldAndroidMonitorTest.CompareAndExchange.Worker w1 = new OldAndroidMonitorTest.CompareAndExchange.Worker(0, 1);
            OldAndroidMonitorTest.CompareAndExchange.Worker w2 = new OldAndroidMonitorTest.CompareAndExchange.Worker(2, 3);
            OldAndroidMonitorTest.CompareAndExchange.Worker w3 = new OldAndroidMonitorTest.CompareAndExchange.Worker(4, 5);
            OldAndroidMonitorTest.CompareAndExchange.Worker w4 = new OldAndroidMonitorTest.CompareAndExchange.Worker(6, 7);
            OldAndroidMonitorTest.CompareAndExchange.running = true;
            // System.out.println("CompareAndExchange: starting workers");
            w1.start();
            w2.start();
            w3.start();
            w4.start();
            try {
                this.sleep(10000);
            } catch (InterruptedException ex) {
                // System.out.println(getName() + " interrupted.");
            }
            // System.out.println("MonitorTest: stopping workers");
            OldAndroidMonitorTest.CompareAndExchange.running = false;
            OldAndroidMonitorTest.CompareAndExchange.toggleLock = null;
        }

        class Worker extends Thread {
            int i1;

            int i2;

            Worker(int i1, int i2) {
                super((((("Worker(" + i1) + ", ") + i2) + ")"));
                this.i1 = i1;
                this.i2 = i2;
            }

            public void run() {
                int iterations = 0;
                /* Latch this because run() may set the static field to
                null at some point.
                 */
                Object toggleLock = OldAndroidMonitorTest.CompareAndExchange.toggleLock;
                // System.out.println(getName() + " running");
                try {
                    while (OldAndroidMonitorTest.CompareAndExchange.running) {
                        synchronized(toggleLock) {
                            int test;
                            int check;
                            if ((OldAndroidMonitorTest.CompareAndExchange.toggle) == (i1)) {
                                this.sleep((5 + (i2)));
                                OldAndroidMonitorTest.CompareAndExchange.toggle = test = i2;
                            } else {
                                this.sleep((5 + (i1)));
                                OldAndroidMonitorTest.CompareAndExchange.toggle = test = i1;
                            }
                            if ((check = OldAndroidMonitorTest.CompareAndExchange.toggle) != test) {
                                // System.out.println("Worker(" + i1 + ", " +
                                // i2 + ") " + "test " + test +
                                // " != toggle " + check);
                                throw new RuntimeException("locked value changed");
                            }
                        }
                        iterations++;
                    } 
                } catch (InterruptedException ex) {
                    // System.out.println(getName() + " interrupted.");
                }
                // System.out.println(getName() + " done after " +
                // iterations + " iterations.");
            }
        }
    }
}

