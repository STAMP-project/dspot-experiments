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
import java.lang.ref.SoftReference;
import junit.framework.TestCase;


public class SoftReferenceTest extends TestCase {
    static Boolean bool;

    SoftReference r;

    /**
     * java.lang.ref.SoftReference#SoftReference(java.lang.Object,
     *        java.lang.ref.ReferenceQueue)
     */
    public void test_ConstructorLjava_lang_ObjectLjava_lang_ref_ReferenceQueue() {
        ReferenceQueue rq = new ReferenceQueue();
        SoftReferenceTest.bool = new Boolean(true);
        try {
            SoftReference sr = new SoftReference(SoftReferenceTest.bool, rq);
            TestCase.assertTrue("Initialization failed.", ((Boolean) (sr.get())).booleanValue());
        } catch (Exception e) {
            TestCase.fail(("Exception during test : " + (e.getMessage())));
        }
        boolean exception = false;
        try {
            new SoftReference(SoftReferenceTest.bool, null);
        } catch (NullPointerException e) {
            exception = true;
        }
        TestCase.assertTrue("Should not throw NullPointerException", (!exception));
    }

    /**
     * java.lang.ref.SoftReference#SoftReference(java.lang.Object)
     */
    public void test_ConstructorLjava_lang_Object() {
        SoftReferenceTest.bool = new Boolean(true);
        try {
            SoftReference sr = new SoftReference(SoftReferenceTest.bool);
            TestCase.assertTrue("Initialization failed.", ((Boolean) (sr.get())).booleanValue());
        } catch (Exception e) {
            TestCase.fail(("Exception during test : " + (e.getMessage())));
        }
    }

    /**
     * java.lang.ref.SoftReference#get()
     */
    public void test_get() {
        SoftReferenceTest.bool = new Boolean(false);
        SoftReference sr = new SoftReference(SoftReferenceTest.bool);
        TestCase.assertTrue("Same object not returned.", ((SoftReferenceTest.bool) == (sr.get())));
    }
}

