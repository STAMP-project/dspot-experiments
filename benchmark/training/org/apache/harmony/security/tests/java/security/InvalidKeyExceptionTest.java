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


import java.security.InvalidKeyException;
import junit.framework.TestCase;


/**
 * Tests for <code>InvalidKeyException</code> class constructors and methods.
 */
public class InvalidKeyExceptionTest extends TestCase {
    private static String[] msgs = new String[]{ "", "Check new message", "Check new message Check new message Check new message Check new message Check new message" };

    private static Throwable tCause = new Throwable("Throwable for exception");

    /**
     * Test for <code>InvalidKeyException()</code> constructor Assertion:
     * constructs InvalidKeyException with no detail message
     */
    public void testInvalidKeyException01() {
        InvalidKeyException tE = new InvalidKeyException();
        TestCase.assertNull("getMessage() must return null.", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>InvalidKeyException(String)</code> constructor
     * Assertion: constructs InvalidKeyException with detail message msg.
     * Parameter <code>msg</code> is not null.
     */
    public void testInvalidKeyException02() {
        InvalidKeyException tE;
        for (int i = 0; i < (InvalidKeyExceptionTest.msgs.length); i++) {
            tE = new InvalidKeyException(InvalidKeyExceptionTest.msgs[i]);
            TestCase.assertEquals("getMessage() must return: ".concat(InvalidKeyExceptionTest.msgs[i]), tE.getMessage(), InvalidKeyExceptionTest.msgs[i]);
            TestCase.assertNull("getCause() must return null", tE.getCause());
        }
    }

    /**
     * Test for <code>InvalidKeyException(String)</code> constructor
     * Assertion: constructs InvalidKeyException when <code>msg</code> is null
     */
    public void testInvalidKeyException03() {
        String msg = null;
        InvalidKeyException tE = new InvalidKeyException(msg);
        TestCase.assertNull("getMessage() must return null.", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>InvalidKeyException(Throwable)</code> constructor
     * Assertion: constructs InvalidKeyException when <code>cause</code> is
     * null
     */
    public void testInvalidKeyException04() {
        Throwable cause = null;
        InvalidKeyException tE = new InvalidKeyException(cause);
        TestCase.assertNull("getMessage() must return null.", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>InvalidKeyException(Throwable)</code> constructor
     * Assertion: constructs InvalidKeyException when <code>cause</code> is
     * not null
     */
    public void testInvalidKeyException05() {
        InvalidKeyException tE = new InvalidKeyException(InvalidKeyExceptionTest.tCause);
        if ((tE.getMessage()) != null) {
            String toS = InvalidKeyExceptionTest.tCause.toString();
            String getM = tE.getMessage();
            TestCase.assertTrue("getMessage() should contain ".concat(toS), ((getM.indexOf(toS)) != (-1)));
        }
        TestCase.assertNotNull("getCause() must not return null", tE.getCause());
        TestCase.assertEquals("getCause() must return ".concat(InvalidKeyExceptionTest.tCause.toString()), tE.getCause(), InvalidKeyExceptionTest.tCause);
    }

    /**
     * Test for <code>InvalidKeyException(String, Throwable)</code>
     * constructor Assertion: constructs InvalidKeyException when
     * <code>cause</code> is null <code>msg</code> is null
     */
    public void testInvalidKeyException06() {
        InvalidKeyException tE = new InvalidKeyException(null, null);
        TestCase.assertNull("getMessage() must return null", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>InvalidKeyException(String, Throwable)</code>
     * constructor Assertion: constructs InvalidKeyException when
     * <code>cause</code> is null <code>msg</code> is not null
     */
    public void testInvalidKeyException07() {
        InvalidKeyException tE;
        for (int i = 0; i < (InvalidKeyExceptionTest.msgs.length); i++) {
            tE = new InvalidKeyException(InvalidKeyExceptionTest.msgs[i], null);
            TestCase.assertEquals("getMessage() must return: ".concat(InvalidKeyExceptionTest.msgs[i]), tE.getMessage(), InvalidKeyExceptionTest.msgs[i]);
            TestCase.assertNull("getCause() must return null", tE.getCause());
        }
    }

    /**
     * Test for <code>InvalidKeyException(String, Throwable)</code>
     * constructor Assertion: constructs InvalidKeyException when
     * <code>cause</code> is not null <code>msg</code> is null
     */
    public void testInvalidKeyException08() {
        InvalidKeyException tE = new InvalidKeyException(null, InvalidKeyExceptionTest.tCause);
        if ((tE.getMessage()) != null) {
            String toS = InvalidKeyExceptionTest.tCause.toString();
            String getM = tE.getMessage();
            TestCase.assertTrue("getMessage() must should ".concat(toS), ((getM.indexOf(toS)) != (-1)));
        }
        TestCase.assertNotNull("getCause() must not return null", tE.getCause());
        TestCase.assertEquals("getCause() must return ".concat(InvalidKeyExceptionTest.tCause.toString()), tE.getCause(), InvalidKeyExceptionTest.tCause);
    }

    /**
     * Test for <code>InvalidKeyException(String, Throwable)</code>
     * constructor Assertion: constructs InvalidKeyException when
     * <code>cause</code> is not null <code>msg</code> is not null
     */
    public void testInvalidKeyException09() {
        InvalidKeyException tE;
        for (int i = 0; i < (InvalidKeyExceptionTest.msgs.length); i++) {
            tE = new InvalidKeyException(InvalidKeyExceptionTest.msgs[i], InvalidKeyExceptionTest.tCause);
            String getM = tE.getMessage();
            String toS = InvalidKeyExceptionTest.tCause.toString();
            if ((InvalidKeyExceptionTest.msgs[i].length()) > 0) {
                TestCase.assertTrue("getMessage() must contain ".concat(InvalidKeyExceptionTest.msgs[i]), ((getM.indexOf(InvalidKeyExceptionTest.msgs[i])) != (-1)));
                if (!(getM.equals(InvalidKeyExceptionTest.msgs[i]))) {
                    TestCase.assertTrue("getMessage() should contain ".concat(toS), ((getM.indexOf(toS)) != (-1)));
                }
            }
            TestCase.assertNotNull("getCause() must not return null", tE.getCause());
            TestCase.assertEquals("getCause() must return ".concat(InvalidKeyExceptionTest.tCause.toString()), tE.getCause(), InvalidKeyExceptionTest.tCause);
        }
    }
}

