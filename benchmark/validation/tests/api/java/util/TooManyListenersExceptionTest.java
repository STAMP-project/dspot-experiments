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


import java.util.TooManyListenersException;
import junit.framework.TestCase;


public class TooManyListenersExceptionTest extends TestCase {
    /**
     * java.util.TooManyListenersException#TooManyListenersException()
     */
    public void test_Constructor() {
        // Test for method java.util.TooManyListenersException()
        try {
            throw new TooManyListenersException();
        } catch (TooManyListenersException e) {
            TestCase.assertNull("Message thrown with exception constructed with no message", e.getMessage());
        }
    }

    /**
     * java.util.TooManyListenersException#TooManyListenersException(java.lang.String)
     */
    public void test_ConstructorLjava_lang_String() {
        // Test for method java.util.TooManyListenersException(java.lang.String)
        try {
            throw new TooManyListenersException("Gah");
        } catch (TooManyListenersException e) {
            TestCase.assertEquals("Incorrect message thrown with exception", "Gah", e.getMessage());
        }
    }
}

