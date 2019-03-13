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
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import libcore.java.lang.ref.FinalizationTester;


public class ReferenceTest extends TestCase {
    Object tmpA;

    Object tmpB;

    Object tmpC;

    Object obj;

    volatile Reference r;

    /* For test_subclass(). */
    static ReferenceTest.TestWeakReference twr;

    static AssertionFailedError error;

    static boolean testObjectFinalized;

    static class TestWeakReference<T> extends WeakReference<T> {
        public volatile boolean clearSeen = false;

        public volatile boolean enqueueSeen = false;

        public TestWeakReference(T referent) {
            super(referent);
        }

        public TestWeakReference(T referent, ReferenceQueue<? super T> q) {
            super(referent, q);
        }

        public void clear() {
            clearSeen = true;
            if (ReferenceTest.testObjectFinalized) {
                ReferenceTest.error = new AssertionFailedError(("Clear should happen " + "before finalization."));
                throw ReferenceTest.error;
            }
            if (enqueueSeen) {
                ReferenceTest.error = new AssertionFailedError(("Clear should happen " + "before enqueue."));
                throw ReferenceTest.error;
            }
            super.clear();
        }

        public boolean enqueue() {
            enqueueSeen = true;
            if (!(clearSeen)) {
                ReferenceTest.error = new AssertionFailedError(("Clear should happen " + "before enqueue."));
                throw ReferenceTest.error;
            }
            /* Do this last;  it may notify the main test thread,
            and anything we'd do after it (e.g., setting clearSeen)
            wouldn't be seen.
             */
            return super.enqueue();
        }
    }

    /**
     * java.lang.ref.Reference#clear()
     */
    public void test_clear() {
        tmpA = new Object();
        tmpB = new Object();
        tmpC = new Object();
        SoftReference sr = new SoftReference(tmpA, new ReferenceQueue());
        WeakReference wr = new WeakReference(tmpB, new ReferenceQueue());
        PhantomReference pr = new PhantomReference(tmpC, new ReferenceQueue());
        TestCase.assertTrue("Start: Object not cleared.", (((sr.get()) != null) && ((wr.get()) != null)));
        TestCase.assertNull("Referent is not null.", pr.get());
        sr.clear();
        wr.clear();
        pr.clear();
        TestCase.assertTrue("End: Object cleared.", (((sr.get()) == null) && ((wr.get()) == null)));
        TestCase.assertNull("Referent is not null.", pr.get());
        // Must reference tmpA and tmpB so the jit does not optimize them away
        TestCase.assertTrue("should always pass", (((tmpA) != (sr.get())) && ((tmpB) != (wr.get()))));
    }

    /**
     * java.lang.ref.Reference#enqueue()
     */
    public void test_enqueue() {
        ReferenceQueue rq = new ReferenceQueue();
        obj = new Object();
        Reference ref = new SoftReference(obj, rq);
        TestCase.assertTrue("Enqueue failed.", ((!(ref.isEnqueued())) && ((ref.enqueue()) && (ref.isEnqueued()))));
        TestCase.assertTrue("Not properly enqueued.", ((rq.poll().get()) == (obj)));
        // This fails...
        TestCase.assertTrue("Should remain enqueued.", (!(ref.isEnqueued())));
        TestCase.assertTrue("Can not enqueue twice.", ((!(ref.enqueue())) && ((rq.poll()) == null)));
        rq = new ReferenceQueue();
        obj = new Object();
        ref = new WeakReference(obj, rq);
        TestCase.assertTrue("Enqueue failed2.", ((!(ref.isEnqueued())) && ((ref.enqueue()) && (ref.isEnqueued()))));
        TestCase.assertTrue("Not properly enqueued2.", ((rq.poll().get()) == (obj)));
        TestCase.assertTrue("Should remain enqueued2.", (!(ref.isEnqueued())));// This

        // fails.
        TestCase.assertTrue("Can not enqueue twice2.", ((!(ref.enqueue())) && ((rq.poll()) == null)));
        ref = new PhantomReference(obj, rq);
        TestCase.assertTrue("Enqueue failed3.", ((!(ref.isEnqueued())) && ((ref.enqueue()) && (ref.isEnqueued()))));
        TestCase.assertNull("Not properly enqueued3.", rq.poll().get());
        TestCase.assertTrue("Should remain enqueued3.", (!(ref.isEnqueued())));// This

        // fails.
        TestCase.assertTrue("Can not enqueue twice3.", ((!(ref.enqueue())) && ((rq.poll()) == null)));
    }

    public void test_get_WeakReference() throws Exception {
        // Test the general/overall functionality of Reference.
        ReferenceQueue<Object> queue = new ReferenceQueue<Object>();
        r = newWeakReference(queue);
        FinalizationTester.induceFinalization();
        Reference ref = queue.remove((10 * 1000));// RoboVM note: Don't wait indefinitely.

        TestCase.assertNotNull("Object not enqueued.", ref);
        TestCase.assertSame("Unexpected ref1", ref, r);
        TestCase.assertNull("Object could not be reclaimed1.", r.get());
        r = newWeakReference(queue);
        FinalizationTester.induceFinalization();
        // wait for the reference queue thread to enqueue the newly-finalized object
        Thread.yield();
        Thread.sleep(200);
        ref = queue.poll();
        TestCase.assertNotNull("Object not enqueued.", ref);
        TestCase.assertSame("Unexpected ref2", ref, r);
        TestCase.assertNull("Object could not be reclaimed.", ref.get());
        TestCase.assertNull("Object could not be reclaimed.", r.get());
    }

    /**
     * Makes sure that overridden versions of clear() and enqueue()
     * get called, and that clear/enqueue/finalize happen in the
     * right order for WeakReferences.
     *
     * java.lang.ref.Reference#clear()
     * java.lang.ref.Reference#enqueue()
     * java.lang.Object#finalize()
     */
    public void test_subclass() {
        ReferenceTest.error = null;
        ReferenceTest.testObjectFinalized = false;
        ReferenceTest.twr = null;
        class TestObject {
            public ReferenceTest.TestWeakReference testWeakReference = null;

            public void setTestWeakReference(ReferenceTest.TestWeakReference twr) {
                testWeakReference = twr;
            }

            protected void finalize() {
                ReferenceTest.testObjectFinalized = true;
            }
        }
        final ReferenceQueue rq = new ReferenceQueue();
        class TestThread extends Thread {
            public void run() {
                // Create the object in a separate thread to ensure it will be
                // gc'ed
                TestObject testObj = new TestObject();
                ReferenceTest.twr = new ReferenceTest.TestWeakReference(testObj, rq);
                testObj.setTestWeakReference(ReferenceTest.twr);
                testObj = null;
            }
        }
        Reference ref;
        try {
            Thread t = new TestThread();
            t.start();
            t.join();
            FinalizationTester.induceFinalization();
            ref = rq.remove(5000L);// Give up after five seconds.

            TestCase.assertNotNull("Object not garbage collected.", ref);
            TestCase.assertTrue("Unexpected reference.", (ref == (ReferenceTest.twr)));
            TestCase.assertNull("Object could not be reclaimed.", ReferenceTest.twr.get());
            // assertTrue("Overridden clear() should have been called.",
            // twr.clearSeen);
            // assertTrue("Overridden enqueue() should have been called.",
            // twr.enqueueSeen);
            TestCase.assertTrue("finalize() should have been called.", ReferenceTest.testObjectFinalized);
        } catch (InterruptedException e) {
            TestCase.fail(("InterruptedException : " + (e.getMessage())));
        }
    }

    /**
     * java.lang.ref.Reference#get()
     */
    public void test_get() {
        WeakReference ref = newWeakReference(null);
        FinalizationTester.induceFinalization();
        TestCase.assertNull("get() doesn't return null after gc for WeakReference", ref.get());
        obj = new Object();
        ref = new WeakReference<Object>(obj, new ReferenceQueue<Object>());
        ref.clear();
        TestCase.assertNull("get() doesn't return null after clear for WeakReference", ref.get());
    }

    /**
     * java.lang.ref.Reference#isEnqueued()
     */
    public void test_isEnqueued() {
        ReferenceQueue rq = new ReferenceQueue();
        obj = new Object();
        Reference ref = new SoftReference(obj, rq);
        TestCase.assertTrue("Should start off not enqueued.", (!(ref.isEnqueued())));
        ref.enqueue();
        TestCase.assertTrue("Should now be enqueued.", ref.isEnqueued());
        ref.enqueue();
        TestCase.assertTrue("Should still be enqueued.", ref.isEnqueued());
        rq.poll();
        // This fails ...
        TestCase.assertTrue("Should now be not enqueued.", (!(ref.isEnqueued())));
    }

    /* Contrives a situation where the only reference to a string
    is a WeakReference from an object that is being finalized.
    Checks to make sure that the referent of the WeakReference
    is still pointing to a valid object.
     */
    public void test_finalizeReferenceInteraction() {
        ReferenceTest.error = null;
        ReferenceTest.testObjectFinalized = false;
        class TestObject {
            WeakReference<String> stringRef;

            public TestObject(String referent) {
                stringRef = new WeakReference<String>(referent);
            }

            protected void finalize() {
                try {
                    /* If a VM bug has caused the referent to get
                    freed without the reference getting cleared,
                    looking it up, assigning it to a local and
                    doing a GC should cause some sort of exception.
                     */
                    String s = stringRef.get();
                    System.gc();
                    ReferenceTest.testObjectFinalized = true;
                } catch (Throwable t) {
                    ReferenceTest.error = new AssertionFailedError((("something threw '" + t) + "' in finalize()"));
                }
            }
        }
        class TestThread extends Thread {
            public void run() {
                // Create the object in a separate thread to ensure it will be
                // gc'ed
                TestObject testObj = new TestObject(new String("sup /b/"));
            }
        }
        try {
            Thread t = new TestThread();
            t.start();
            t.join();
            FinalizationTester.induceFinalization();
            Thread.sleep(1000);
            if ((ReferenceTest.error) != null) {
                throw ReferenceTest.error;
            }
            TestCase.assertTrue("finalize() should have been called.", ReferenceTest.testObjectFinalized);
        } catch (InterruptedException e) {
            TestCase.fail(("InterruptedException : " + (e.getMessage())));
        }
    }
}

