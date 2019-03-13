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


import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import junit.framework.TestCase;


public class WeakReferenceTest extends TestCase {
    static Boolean bool;

    /**
     * java.lang.ref.WeakReference#WeakReference(java.lang.Object,
     *        java.lang.ref.ReferenceQueue)
     */
    public void test_ConstructorLjava_lang_ObjectLjava_lang_ref_ReferenceQueue() {
        ReferenceQueue rq = new ReferenceQueue();
        WeakReferenceTest.bool = new Boolean(true);
        try {
            // Allow the finalizer to run to potentially enqueue
            WeakReference wr = new WeakReference(WeakReferenceTest.bool, rq);
            TestCase.assertTrue("Initialization failed.", ((Boolean) (wr.get())).booleanValue());
        } catch (Exception e) {
            TestCase.fail(("Exception during test : " + (e.getMessage())));
        }
        // need a reference to bool so the jit does not optimize it away
        TestCase.assertTrue("should always pass", WeakReferenceTest.bool.booleanValue());
        boolean exception = false;
        try {
            new WeakReference(WeakReferenceTest.bool, null);
        } catch (NullPointerException e) {
            exception = true;
        }
        TestCase.assertTrue("Should not throw NullPointerException", (!exception));
    }

    /**
     * java.lang.ref.WeakReference#WeakReference(java.lang.Object)
     */
    public void test_ConstructorLjava_lang_Object() {
        WeakReferenceTest.bool = new Boolean(true);
        try {
            WeakReference wr = new WeakReference(WeakReferenceTest.bool);
            // Allow the finalizer to run to potentially enqueue
            Thread.sleep(1000);
            TestCase.assertTrue("Initialization failed.", ((Boolean) (wr.get())).booleanValue());
        } catch (Exception e) {
            TestCase.fail(("Exception during test : " + (e.getMessage())));
        }
        // need a reference to bool so the jit does not optimize it away
        TestCase.assertTrue("should always pass", WeakReferenceTest.bool.booleanValue());
    }
}

