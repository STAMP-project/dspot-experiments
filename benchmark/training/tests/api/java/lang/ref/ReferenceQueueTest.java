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
package tests.api.java.lang.ref;


import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import junit.framework.TestCase;
import libcore.java.lang.ref.FinalizationTester;


public class ReferenceQueueTest extends TestCase {
    static Boolean b;

    static Integer integer;

    boolean isThrown = false;

    public class ChildThread implements Runnable {
        public ChildThread() {
        }

        public void run() {
            try {
                rq.wait(1000);
            } catch (Exception e) {
            }
            synchronized(rq) {
                // store in a static so it won't be gc'ed because the jit
                // optimized it out
                ReferenceQueueTest.integer = new Integer(667);
                SoftReference sr = new SoftReference(ReferenceQueueTest.integer, rq);
                sr.enqueue();
                rq.notify();
            }
        }
    }

    ReferenceQueue rq;

    /**
     * java.lang.ref.ReferenceQueue#poll()
     */
    public void test_poll() {
        // store in a static so it won't be gc'ed because the jit
        // optimized it out
        ReferenceQueueTest.b = new Boolean(true);
        Object obj = new Object();
        String str = "Test";
        SoftReference sr = new SoftReference(ReferenceQueueTest.b, rq);
        WeakReference wr = new WeakReference(obj, rq);
        PhantomReference pr = new PhantomReference(str, rq);
        TestCase.assertNull(rq.poll());
        sr.enqueue();
        wr.enqueue();
        pr.enqueue();
        try {
            TestCase.assertNull("Remove failed.", rq.poll().get());
        } catch (Exception e) {
            TestCase.fail(("Exception during the test : " + (e.getMessage())));
        }
        try {
            TestCase.assertEquals("Remove failed.", obj, rq.poll().get());
        } catch (Exception e) {
            TestCase.fail(("Exception during the test : " + (e.getMessage())));
        }
        try {
            TestCase.assertTrue("Remove failed.", ((Boolean) (rq.poll().get())).booleanValue());
        } catch (Exception e) {
            TestCase.fail(("Exception during the test : " + (e.getMessage())));
        }
        TestCase.assertNull(rq.poll());
        sr.enqueue();
        wr.enqueue();
        FinalizationTester.induceFinalization();
        TestCase.assertNull(rq.poll());
    }

    /**
     * java.lang.ref.ReferenceQueue#remove()
     */
    public void test_remove() {
        // store in a static so it won't be gc'ed because the jit
        // optimized it out
        ReferenceQueueTest.b = new Boolean(true);
        SoftReference sr = new SoftReference(ReferenceQueueTest.b, rq);
        sr.enqueue();
        try {
            TestCase.assertTrue("Remove failed.", ((Boolean) (rq.remove().get())).booleanValue());
        } catch (Exception e) {
            TestCase.fail(("Exception during the test : " + (e.getMessage())));
        }
        TestCase.assertNull(rq.poll());
        sr.enqueue();
        class RemoveThread extends Thread {
            public void run() {
                try {
                    rq.remove();
                } catch (InterruptedException ie) {
                    isThrown = true;
                }
            }
        }
        RemoveThread rt = new RemoveThread();
        rt.start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException ie) {
        }
        rt.interrupt();
        try {
            Thread.sleep(100);
        } catch (InterruptedException ie) {
        }
        TestCase.assertTrue(isThrown);
        TestCase.assertNull(rq.poll());
    }

    /**
     * java.lang.ref.ReferenceQueue#remove(long)
     */
    public void test_removeJ() {
        try {
            TestCase.assertNull("Queue should be empty. (poll)", rq.poll());
            TestCase.assertNull("Queue should be empty. (remove(1))", rq.remove(((long) (1))));
            Thread ct = new Thread(new ReferenceQueueTest.ChildThread());
            ct.start();
            Reference ret = rq.remove(0L);
            TestCase.assertNotNull("Delayed remove failed.", ret);
        } catch (InterruptedException e) {
            TestCase.fail(("InterruptedExeException during test : " + (e.getMessage())));
        } catch (Exception e) {
            TestCase.fail(("Exception during test : " + (e.getMessage())));
        }
        Object obj = new Object();
        WeakReference wr = new WeakReference(obj, rq);
        Boolean b = new Boolean(true);
        SoftReference sr = new SoftReference(b, rq);
        String str = "Test";
        PhantomReference pr = new PhantomReference(str, rq);
        pr.enqueue();
        wr.enqueue();
        sr.enqueue();
        try {
            Reference result = rq.remove(1L);
            TestCase.assertTrue(((Boolean) (result.get())));
            result = rq.remove(1L);
            TestCase.assertEquals(obj, result.get());
            result = rq.remove(1L);
            TestCase.assertNull(result.get());
        } catch (IllegalArgumentException e1) {
            TestCase.fail("IllegalArgumentException was thrown.");
        } catch (InterruptedException e1) {
            TestCase.fail("InterruptedException was thrown.");
        }
        rq = new ReferenceQueue();
        isThrown = false;
        TestCase.assertNull(rq.poll());
        class RemoveThread extends Thread {
            public void run() {
                try {
                    rq.remove(1000L);
                } catch (InterruptedException ie) {
                    isThrown = true;
                }
            }
        }
        RemoveThread rt = new RemoveThread();
        rt.start();
        try {
            Thread.sleep(10);
        } catch (InterruptedException ie) {
        }
        rt.interrupt();
        try {
            Thread.sleep(10);
        } catch (InterruptedException ie) {
        }
        TestCase.assertTrue(isThrown);
        TestCase.assertNull(rq.poll());
        try {
            rq.remove((-1));
            TestCase.fail("IllegalArgumentException expected.");
        } catch (IllegalArgumentException iae) {
            // expected
        } catch (InterruptedException e) {
            TestCase.fail("Unexpected InterruptedException.");
        }
    }

    /**
     * java.lang.ref.ReferenceQueue#ReferenceQueue()
     */
    public void test_Constructor() {
        ReferenceQueue rq = new ReferenceQueue();
        TestCase.assertNull(rq.poll());
        try {
            rq.remove(100L);
        } catch (InterruptedException e) {
            TestCase.fail("InterruptedException was thrown.");
        }
    }
}

