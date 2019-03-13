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
package tests.api.java.util;


import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import junit.framework.TestCase;


public class ConcurrentModificationExceptionTest extends TestCase {
    public static class CollectionModifier implements Runnable {
        Collection col;

        boolean keepGoing = true;

        public CollectionModifier(Collection c) {
            col = c;
        }

        public void stopNow() {
            keepGoing = false;
        }

        public void run() {
            Object someItem = new Integer((-1));
            while (keepGoing) {
                col.add(someItem);
                col.remove(someItem);
            } 
        }
    }

    /**
     * java.util.ConcurrentModificationException#ConcurrentModificationException()
     */
    public void test_Constructor() {
        // Test for method java.util.ConcurrentModificationException()
        Collection myCollection = new LinkedList();
        Iterator myIterator = myCollection.iterator();
        for (int counter = 0; counter < 50; counter++)
            myCollection.add(new Integer(counter));

        ConcurrentModificationExceptionTest.CollectionModifier cm = new ConcurrentModificationExceptionTest.CollectionModifier(myCollection);
        Thread collectionSlapper = new Thread(cm);
        try {
            collectionSlapper.start();
            while (myIterator.hasNext())
                myIterator.next();

        } catch (ConcurrentModificationException e) {
            cm.stopNow();
            return;
        }
        cm.stopNow();
        // The exception should have been thrown--if the code flow makes it here
        // the test has failed
        TestCase.fail("Failed to throw expected ConcurrentModificationException");
    }

    /**
     * java.util.ConcurrentModificationException#ConcurrentModificationException(java.lang.String)
     */
    public void test_ConstructorLjava_lang_String() {
        // Test for method
        // java.util.ConcurrentModificationException(java.lang.String)
        String errorMessage = "This is an error message";
        try {
            // This is here to stop "unreachable code" unresolved problem
            if (true)
                throw new ConcurrentModificationException(errorMessage);

        } catch (ConcurrentModificationException e) {
            TestCase.assertTrue("Exception thrown without error message", e.getMessage().equals(errorMessage));
            return;
        }
        TestCase.fail("Failed to throw expected ConcurrentModificationException");
    }
}

