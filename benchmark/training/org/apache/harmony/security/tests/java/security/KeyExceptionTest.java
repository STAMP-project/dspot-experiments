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
/**
 *
 *
 * @author Vera Y. Petrashkova
 * @version $Revision$
 */
package org.apache.harmony.security.tests.java.security;


import java.security.KeyException;
import junit.framework.TestCase;


/**
 * Tests for <code>KeyException</code> class constructors and methods.
 */
public class KeyExceptionTest extends TestCase {
    private static String[] msgs = new String[]{ "", "Check new message", "Check new message Check new message Check new message Check new message Check new message" };

    private static Throwable tCause = new Throwable("Throwable for exception");

    /**
     * Test for <code>KeyException()</code> constructor Assertion: constructs
     * KeyException with no detail message
     */
    public void testKeyException01() {
        KeyException tE = new KeyException();
        TestCase.assertNull("getMessage() must return null.", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>KeyException(String)</code> constructor Assertion:
     * constructs KeyException with detail message msg. Parameter
     * <code>msg</code> is not null.
     */
    public void testKeyException02() {
        KeyException tE;
        for (int i = 0; i < (KeyExceptionTest.msgs.length); i++) {
            tE = new KeyException(KeyExceptionTest.msgs[i]);
            TestCase.assertEquals("getMessage() must return: ".concat(KeyExceptionTest.msgs[i]), tE.getMessage(), KeyExceptionTest.msgs[i]);
            TestCase.assertNull("getCause() must return null", tE.getCause());
        }
    }

    /**
     * Test for <code>KeyException(String)</code> constructor Assertion:
     * constructs KeyException when <code>msg</code> is null
     */
    public void testKeyException03() {
        String msg = null;
        KeyException tE = new KeyException(msg);
        TestCase.assertNull("getMessage() must return null.", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>KeyException(Throwable)</code> constructor Assertion:
     * constructs KeyException when <code>cause</code> is null
     */
    public void testKeyException04() {
        Throwable cause = null;
        KeyException tE = new KeyException(cause);
        TestCase.assertNull("getMessage() must return null.", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>KeyException(Throwable)</code> constructor Assertion:
     * constructs KeyException when <code>cause</code> is not null
     */
    public void testKeyException05() {
        KeyException tE = new KeyException(KeyExceptionTest.tCause);
        if ((tE.getMessage()) != null) {
            String toS = KeyExceptionTest.tCause.toString();
            String getM = tE.getMessage();
            TestCase.assertTrue("getMessage() should contain ".concat(toS), ((getM.indexOf(toS)) != (-1)));
        }
        TestCase.assertNotNull("getCause() must not return null", tE.getCause());
        TestCase.assertEquals("getCause() must return ".concat(KeyExceptionTest.tCause.toString()), tE.getCause(), KeyExceptionTest.tCause);
    }

    /**
     * Test for <code>KeyException(String, Throwable)</code> constructor
     * Assertion: constructs KeyException when <code>cause</code> is null
     * <code>msg</code> is null
     */
    public void testKeyException06() {
        KeyException tE = new KeyException(null, null);
        TestCase.assertNull("getMessage() must return null", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>KeyException(String, Throwable)</code> constructor
     * Assertion: constructs KeyException when <code>cause</code> is null
     * <code>msg</code> is not null
     */
    public void testKeyException07() {
        KeyException tE;
        for (int i = 0; i < (KeyExceptionTest.msgs.length); i++) {
            tE = new KeyException(KeyExceptionTest.msgs[i], null);
            TestCase.assertEquals("getMessage() must return: ".concat(KeyExceptionTest.msgs[i]), tE.getMessage(), KeyExceptionTest.msgs[i]);
            TestCase.assertNull("getCause() must return null", tE.getCause());
        }
    }

    /**
     * Test for <code>KeyException(String, Throwable)</code> constructor
     * Assertion: constructs KeyException when <code>cause</code> is not null
     * <code>msg</code> is null
     */
    public void testKeyException08() {
        KeyException tE = new KeyException(null, KeyExceptionTest.tCause);
        if ((tE.getMessage()) != null) {
            String toS = KeyExceptionTest.tCause.toString();
            String getM = tE.getMessage();
            TestCase.assertTrue("getMessage() must should ".concat(toS), ((getM.indexOf(toS)) != (-1)));
        }
        TestCase.assertNotNull("getCause() must not return null", tE.getCause());
        TestCase.assertEquals("getCause() must return ".concat(KeyExceptionTest.tCause.toString()), tE.getCause(), KeyExceptionTest.tCause);
    }

    /**
     * Test for <code>KeyException(String, Throwable)</code> constructor
     * Assertion: constructs KeyException when <code>cause</code> is not null
     * <code>msg</code> is not null
     */
    public void testKeyException09() {
        KeyException tE;
        for (int i = 0; i < (KeyExceptionTest.msgs.length); i++) {
            tE = new KeyException(KeyExceptionTest.msgs[i], KeyExceptionTest.tCause);
            String getM = tE.getMessage();
            String toS = KeyExceptionTest.tCause.toString();
            if ((KeyExceptionTest.msgs[i].length()) > 0) {
                TestCase.assertTrue("getMessage() must contain ".concat(KeyExceptionTest.msgs[i]), ((getM.indexOf(KeyExceptionTest.msgs[i])) != (-1)));
                if (!(getM.equals(KeyExceptionTest.msgs[i]))) {
                    TestCase.assertTrue("getMessage() should contain ".concat(toS), ((getM.indexOf(toS)) != (-1)));
                }
            }
            TestCase.assertNotNull("getCause() must not return null", tE.getCause());
            TestCase.assertEquals("getCause() must return ".concat(KeyExceptionTest.tCause.toString()), tE.getCause(), KeyExceptionTest.tCause);
        }
    }
}

