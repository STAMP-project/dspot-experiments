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


import java.security.KeyManagementException;
import junit.framework.TestCase;


/**
 * Tests for <code>KeyManagementException</code> class constructors and
 * methods.
 */
public class KeyManagementExceptionTest extends TestCase {
    private static String[] msgs = new String[]{ "", "Check new message", "Check new message Check new message Check new message Check new message Check new message" };

    private static Throwable tCause = new Throwable("Throwable for exception");

    /**
     * Test for <code>KeyManagementException()</code> constructor Assertion:
     * constructs KeyManagementException with no detail message
     */
    public void testKeyManagementException01() {
        KeyManagementException tE = new KeyManagementException();
        TestCase.assertNull("getMessage() must return null.", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>KeyManagementException(String)</code> constructor
     * Assertion: constructs KeyManagementException with detail message msg.
     * Parameter <code>msg</code> is not null.
     */
    public void testKeyManagementException02() {
        KeyManagementException tE;
        for (int i = 0; i < (KeyManagementExceptionTest.msgs.length); i++) {
            tE = new KeyManagementException(KeyManagementExceptionTest.msgs[i]);
            TestCase.assertEquals("getMessage() must return: ".concat(KeyManagementExceptionTest.msgs[i]), tE.getMessage(), KeyManagementExceptionTest.msgs[i]);
            TestCase.assertNull("getCause() must return null", tE.getCause());
        }
    }

    /**
     * Test for <code>KeyManagementException(String)</code> constructor
     * Assertion: constructs KeyManagementException when <code>msg</code> is
     * null
     */
    public void testKeyManagementException03() {
        String msg = null;
        KeyManagementException tE = new KeyManagementException(msg);
        TestCase.assertNull("getMessage() must return null.", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>KeyManagementException(Throwable)</code> constructor
     * Assertion: constructs KeyManagementException when <code>cause</code> is
     * null
     */
    public void testKeyManagementException04() {
        Throwable cause = null;
        KeyManagementException tE = new KeyManagementException(cause);
        TestCase.assertNull("getMessage() must return null.", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>KeyManagementException(Throwable)</code> constructor
     * Assertion: constructs KeyManagementException when <code>cause</code> is
     * not null
     */
    public void testKeyManagementException05() {
        KeyManagementException tE = new KeyManagementException(KeyManagementExceptionTest.tCause);
        if ((tE.getMessage()) != null) {
            String toS = KeyManagementExceptionTest.tCause.toString();
            String getM = tE.getMessage();
            TestCase.assertTrue("getMessage() should contain ".concat(toS), ((getM.indexOf(toS)) != (-1)));
        }
        TestCase.assertNotNull("getCause() must not return null", tE.getCause());
        TestCase.assertEquals("getCause() must return ".concat(KeyManagementExceptionTest.tCause.toString()), tE.getCause(), KeyManagementExceptionTest.tCause);
    }

    /**
     * Test for <code>KeyManagementException(String, Throwable)</code>
     * constructor Assertion: constructs KeyManagementException when
     * <code>cause</code> is null <code>msg</code> is null
     */
    public void testKeyManagementException06() {
        KeyManagementException tE = new KeyManagementException(null, null);
        TestCase.assertNull("getMessage() must return null", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>KeyManagementException(String, Throwable)</code>
     * constructor Assertion: constructs KeyManagementException when
     * <code>cause</code> is null <code>msg</code> is not null
     */
    public void testKeyManagementException07() {
        KeyManagementException tE;
        for (int i = 0; i < (KeyManagementExceptionTest.msgs.length); i++) {
            tE = new KeyManagementException(KeyManagementExceptionTest.msgs[i], null);
            TestCase.assertEquals("getMessage() must return: ".concat(KeyManagementExceptionTest.msgs[i]), tE.getMessage(), KeyManagementExceptionTest.msgs[i]);
            TestCase.assertNull("getCause() must return null", tE.getCause());
        }
    }

    /**
     * Test for <code>KeyManagementException(String, Throwable)</code>
     * constructor Assertion: constructs KeyManagementException when
     * <code>cause</code> is not null <code>msg</code> is null
     */
    public void testKeyManagementException08() {
        KeyManagementException tE = new KeyManagementException(null, KeyManagementExceptionTest.tCause);
        if ((tE.getMessage()) != null) {
            String toS = KeyManagementExceptionTest.tCause.toString();
            String getM = tE.getMessage();
            TestCase.assertTrue("getMessage() must should ".concat(toS), ((getM.indexOf(toS)) != (-1)));
        }
        TestCase.assertNotNull("getCause() must not return null", tE.getCause());
        TestCase.assertEquals("getCause() must return ".concat(KeyManagementExceptionTest.tCause.toString()), tE.getCause(), KeyManagementExceptionTest.tCause);
    }

    /**
     * Test for <code>KeyManagementException(String, Throwable)</code>
     * constructor Assertion: constructs KeyManagementException when
     * <code>cause</code> is not null <code>msg</code> is not null
     */
    public void testKeyManagementException09() {
        KeyManagementException tE;
        for (int i = 0; i < (KeyManagementExceptionTest.msgs.length); i++) {
            tE = new KeyManagementException(KeyManagementExceptionTest.msgs[i], KeyManagementExceptionTest.tCause);
            String getM = tE.getMessage();
            String toS = KeyManagementExceptionTest.tCause.toString();
            if ((KeyManagementExceptionTest.msgs[i].length()) > 0) {
                TestCase.assertTrue("getMessage() must contain ".concat(KeyManagementExceptionTest.msgs[i]), ((getM.indexOf(KeyManagementExceptionTest.msgs[i])) != (-1)));
                if (!(getM.equals(KeyManagementExceptionTest.msgs[i]))) {
                    TestCase.assertTrue("getMessage() should contain ".concat(toS), ((getM.indexOf(toS)) != (-1)));
                }
            }
            TestCase.assertNotNull("getCause() must not return null", tE.getCause());
            TestCase.assertEquals("getCause() must return ".concat(KeyManagementExceptionTest.tCause.toString()), tE.getCause(), KeyManagementExceptionTest.tCause);
        }
    }
}

