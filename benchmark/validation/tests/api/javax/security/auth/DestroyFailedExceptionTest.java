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
package tests.api.javax.security.auth;


import javax.security.auth.DestroyFailedException;
import junit.framework.TestCase;


/**
 * Tests for <code>DestroyFailedException</code> class constructors and methods.
 */
public class DestroyFailedExceptionTest extends TestCase {
    private static String[] msgs = new String[]{ "", "Check new message", "Check new message Check new message Check new message Check new message Check new message" };

    /**
     * javax.security.auth.DestroyFailedException#DestroyFailedException()
     * Assertion: constructs DestroyFailedException with no detail message
     */
    public void testDestroyFailedException01() {
        DestroyFailedException dfE = new DestroyFailedException();
        TestCase.assertNull("getMessage() must return null.", dfE.getMessage());
        TestCase.assertNull("getCause() must return null", dfE.getCause());
    }

    /**
     * javax.security.auth.DestroyFailedException#DestroyFailedException(String msg)
     * Assertion: constructs with not null parameter.
     */
    public void testDestroyFailedException02() {
        DestroyFailedException dfE;
        for (int i = 0; i < (DestroyFailedExceptionTest.msgs.length); i++) {
            dfE = new DestroyFailedException(DestroyFailedExceptionTest.msgs[i]);
            TestCase.assertEquals("getMessage() must return: ".concat(DestroyFailedExceptionTest.msgs[i]), dfE.getMessage(), DestroyFailedExceptionTest.msgs[i]);
            TestCase.assertNull("getCause() must return null", dfE.getCause());
        }
    }

    /**
     * javax.security.auth.DestroyFailedException#DestroyFailedException(String msg)
     * Assertion: constructs with null parameter.
     */
    public void testDestroyFailedException03() {
        String msg = null;
        DestroyFailedException dfE = new DestroyFailedException(msg);
        TestCase.assertNull("getMessage() must return null.", dfE.getMessage());
        TestCase.assertNull("getCause() must return null", dfE.getCause());
    }
}

