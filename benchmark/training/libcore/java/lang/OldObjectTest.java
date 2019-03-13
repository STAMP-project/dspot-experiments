/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package libcore.java.lang;


import junit.framework.TestCase;


public class OldObjectTest extends TestCase {
    public boolean isCalled = false;

    /**
     * Test objects.
     */
    Object obj1 = new Object();

    /**
     * Generic state indicator.
     */
    int status = 0;

    int ready = 0;

    OldObjectTest.TestThread1 thr1;

    OldObjectTest.TestThread2 thr2;

    public void test_clone() {
        OldObjectTest.MockCloneableObject mco = new OldObjectTest.MockCloneableObject();
        try {
            TestCase.assertFalse(mco.equals(mco.clone()));
            TestCase.assertEquals(mco.getClass(), mco.clone().getClass());
        } catch (CloneNotSupportedException cnse) {
            TestCase.fail("CloneNotSupportedException was thrown.");
        }
        OldObjectTest.MockObject mo = new OldObjectTest.MockObject();
        try {
            mo.clone();
            TestCase.fail("CloneNotSupportedException was not thrown.");
        } catch (CloneNotSupportedException cnse) {
            // expected
        }
    }

    class MockCloneableObject extends Object implements Cloneable {
        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }

    class MockObject extends Object {
        boolean isCalled = false;

        public void finalize() throws Throwable {
            super.finalize();
            isCalled = true;
        }

        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }

    public void test_notify() {
        try {
            Object obj = new Object();
            obj.notify();
            TestCase.fail("IllegalMonitorStateException was not thrown.");
        } catch (IllegalMonitorStateException imse) {
            // expected
        }
    }

    public void test_notifyAll() {
        try {
            Object obj = new Object();
            obj.notifyAll();
            TestCase.fail("IllegalMonitorStateException was not thrown.");
        } catch (IllegalMonitorStateException imse) {
            // expected
        }
    }

    public void test_wait() {
        try {
            Object obj = new Object();
            obj.wait();
            TestCase.fail("IllegalMonitorStateException was not thrown.");
        } catch (IllegalMonitorStateException imse) {
            // expected
        } catch (InterruptedException ex) {
            TestCase.fail("InterruptedException was thrown.");
        }
        try {
            thr1 = new OldObjectTest.TestThread1(OldObjectTest.TestThread1.CASE_WAIT);
            thr2 = new OldObjectTest.TestThread2();
            thr1.start();
            thr2.start();
            thr2.join();
            thr1.join();
            thr1 = null;
            thr2 = null;
        } catch (InterruptedException e) {
            TestCase.fail("InterruptedException was thrown.");
        }
        TestCase.assertEquals(3, status);
    }

    class TestThread1 extends Thread {
        static final int CASE_WAIT = 0;

        static final int CASE_WAIT_LONG = 1;

        static final int CASE_WAIT_LONG_INT = 2;

        int testCase = OldObjectTest.TestThread1.CASE_WAIT;

        public TestThread1(int option) {
            testCase = option;
        }

        public void run() {
            synchronized(obj1) {
                try {
                    switch (testCase) {
                        case OldObjectTest.TestThread1.CASE_WAIT :
                            obj1.wait();// Wait for ever.

                            break;
                        case OldObjectTest.TestThread1.CASE_WAIT_LONG :
                            obj1.wait(5000L);
                            break;
                        case OldObjectTest.TestThread1.CASE_WAIT_LONG_INT :
                            obj1.wait(10000L, 999999);
                            break;
                    }
                } catch (InterruptedException ex) {
                    status = 3;
                }
            }
        }
    }

    class TestThread2 extends Thread {
        public void run() {
            thr1.interrupt();
        }
    }

    public void test_waitJI() {
        try {
            Object obj = new Object();
            obj.wait(5000L, 1);
            TestCase.fail("IllegalMonitorStateException was not thrown.");
        } catch (IllegalMonitorStateException imse) {
            // expected
        } catch (InterruptedException ex) {
            TestCase.fail("InterruptedException was thrown.");
        }
        try {
            thr1 = new OldObjectTest.TestThread1(OldObjectTest.TestThread1.CASE_WAIT_LONG_INT);
            thr2 = new OldObjectTest.TestThread2();
            thr1.start();
            thr2.start();
            thr2.join();
            thr1.join();
            thr1 = null;
            thr2 = null;
        } catch (InterruptedException e) {
            TestCase.fail("InterruptedException was thrown.");
        }
        TestCase.assertEquals(3, status);
    }

    public void test_waitJI_invalid() throws Exception {
        Object o = new Object();
        synchronized(o) {
            try {
                o.wait((-1), 0);
                TestCase.fail();
            } catch (IllegalArgumentException expected) {
            }
            try {
                o.wait(0, (-1));
                TestCase.fail();
            } catch (IllegalArgumentException expected) {
            }
            try {
                o.wait((-1), (-1));
                TestCase.fail();
            } catch (IllegalArgumentException expected) {
            }
            // The ms timeout must fit in 32 bits.
            try {
                o.wait(((Integer.MAX_VALUE) + 1), 0);
                TestCase.fail();
            } catch (IllegalArgumentException expected) {
            }
        }
    }

    public void test_waitJ() {
        try {
            Object obj = new Object();
            obj.wait(5000L);
            TestCase.fail("IllegalMonitorStateException was not thrown.");
        } catch (IllegalMonitorStateException imse) {
            // expected
        } catch (InterruptedException ex) {
            TestCase.fail("InterruptedException was thrown.");
        }
        try {
            thr1 = new OldObjectTest.TestThread1(OldObjectTest.TestThread1.CASE_WAIT_LONG);
            thr2 = new OldObjectTest.TestThread2();
            thr1.start();
            thr2.start();
            thr2.join();
            thr1.join();
            thr1 = null;
            thr2 = null;
        } catch (InterruptedException e) {
            TestCase.fail("InterruptedException was thrown.");
        }
        TestCase.assertEquals(3, status);
    }
}

