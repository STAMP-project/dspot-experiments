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


import java.security.InvalidAlgorithmParameterException;
import junit.framework.TestCase;


/**
 * Tests for <code>InvalidAlgorithmParameterException</code> class
 * constructors and methods.
 */
public class InvalidAlgorithmParameterExceptionTest extends TestCase {
    private static String[] msgs = new String[]{ "", "Check new message", "Check new message Check new message Check new message Check new message Check new message" };

    private static Throwable tCause = new Throwable("Throwable for exception");

    /**
     * Test for <code>InvalidAlgorithmParameterException()</code> constructor
     * Assertion: constructs InvalidAlgorithmParameterException with no detail
     * message
     */
    public void testInvalidAlgorithmParameterException01() {
        InvalidAlgorithmParameterException tE = new InvalidAlgorithmParameterException();
        TestCase.assertNull("getMessage() must return null.", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>InvalidAlgorithmParameterException(String)</code>
     * constructor Assertion: constructs InvalidAlgorithmParameterException with
     * detail message msg. Parameter <code>msg</code> is not null.
     */
    public void testInvalidAlgorithmParameterException02() {
        InvalidAlgorithmParameterException tE;
        for (int i = 0; i < (InvalidAlgorithmParameterExceptionTest.msgs.length); i++) {
            tE = new InvalidAlgorithmParameterException(InvalidAlgorithmParameterExceptionTest.msgs[i]);
            TestCase.assertEquals("getMessage() must return: ".concat(InvalidAlgorithmParameterExceptionTest.msgs[i]), tE.getMessage(), InvalidAlgorithmParameterExceptionTest.msgs[i]);
            TestCase.assertNull("getCause() must return null", tE.getCause());
        }
    }

    /**
     * Test for <code>InvalidAlgorithmParameterException(String)</code>
     * constructor Assertion: constructs InvalidAlgorithmParameterException when
     * <code>msg</code> is null
     */
    public void testInvalidAlgorithmParameterException03() {
        String msg = null;
        InvalidAlgorithmParameterException tE = new InvalidAlgorithmParameterException(msg);
        TestCase.assertNull("getMessage() must return null.", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>InvalidAlgorithmParameterException(Throwable)</code>
     * constructor Assertion: constructs InvalidAlgorithmParameterException when
     * <code>cause</code> is null
     */
    public void testInvalidAlgorithmParameterException04() {
        Throwable cause = null;
        InvalidAlgorithmParameterException tE = new InvalidAlgorithmParameterException(cause);
        TestCase.assertNull("getMessage() must return null.", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>InvalidAlgorithmParameterException(Throwable)</code>
     * constructor Assertion: constructs InvalidAlgorithmParameterException when
     * <code>cause</code> is not null
     */
    public void testInvalidAlgorithmParameterException05() {
        InvalidAlgorithmParameterException tE = new InvalidAlgorithmParameterException(InvalidAlgorithmParameterExceptionTest.tCause);
        if ((tE.getMessage()) != null) {
            String toS = InvalidAlgorithmParameterExceptionTest.tCause.toString();
            String getM = tE.getMessage();
            TestCase.assertTrue("getMessage() should contain ".concat(toS), ((getM.indexOf(toS)) != (-1)));
        }
        TestCase.assertNotNull("getCause() must not return null", tE.getCause());
        TestCase.assertEquals("getCause() must return ".concat(InvalidAlgorithmParameterExceptionTest.tCause.toString()), tE.getCause(), InvalidAlgorithmParameterExceptionTest.tCause);
    }

    /**
     * Test for
     * <code>InvalidAlgorithmParameterException(String, Throwable)</code>
     * constructor Assertion: constructs InvalidAlgorithmParameterException when
     * <code>cause</code> is null <code>msg</code> is null
     */
    public void testInvalidAlgorithmParameterException06() {
        InvalidAlgorithmParameterException tE = new InvalidAlgorithmParameterException(null, null);
        TestCase.assertNull("getMessage() must return null", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for
     * <code>InvalidAlgorithmParameterException(String, Throwable)</code>
     * constructor Assertion: constructs InvalidAlgorithmParameterException when
     * <code>cause</code> is null <code>msg</code> is not null
     */
    public void testInvalidAlgorithmParameterException07() {
        InvalidAlgorithmParameterException tE;
        for (int i = 0; i < (InvalidAlgorithmParameterExceptionTest.msgs.length); i++) {
            tE = new InvalidAlgorithmParameterException(InvalidAlgorithmParameterExceptionTest.msgs[i], null);
            TestCase.assertEquals("getMessage() must return: ".concat(InvalidAlgorithmParameterExceptionTest.msgs[i]), tE.getMessage(), InvalidAlgorithmParameterExceptionTest.msgs[i]);
            TestCase.assertNull("getCause() must return null", tE.getCause());
        }
    }

    /**
     * Test for
     * <code>InvalidAlgorithmParameterException(String, Throwable)</code>
     * constructor Assertion: constructs InvalidAlgorithmParameterException when
     * <code>cause</code> is not null <code>msg</code> is null
     */
    public void testInvalidAlgorithmParameterException08() {
        InvalidAlgorithmParameterException tE = new InvalidAlgorithmParameterException(null, InvalidAlgorithmParameterExceptionTest.tCause);
        if ((tE.getMessage()) != null) {
            String toS = InvalidAlgorithmParameterExceptionTest.tCause.toString();
            String getM = tE.getMessage();
            TestCase.assertTrue("getMessage() must should ".concat(toS), ((getM.indexOf(toS)) != (-1)));
        }
        TestCase.assertNotNull("getCause() must not return null", tE.getCause());
        TestCase.assertEquals("getCause() must return ".concat(InvalidAlgorithmParameterExceptionTest.tCause.toString()), tE.getCause(), InvalidAlgorithmParameterExceptionTest.tCause);
    }

    /**
     * Test for
     * <code>InvalidAlgorithmParameterException(String, Throwable)</code>
     * constructor Assertion: constructs InvalidAlgorithmParameterException when
     * <code>cause</code> is not null <code>msg</code> is not null
     */
    public void testInvalidAlgorithmParameterException09() {
        InvalidAlgorithmParameterException tE;
        for (int i = 0; i < (InvalidAlgorithmParameterExceptionTest.msgs.length); i++) {
            tE = new InvalidAlgorithmParameterException(InvalidAlgorithmParameterExceptionTest.msgs[i], InvalidAlgorithmParameterExceptionTest.tCause);
            String getM = tE.getMessage();
            String toS = InvalidAlgorithmParameterExceptionTest.tCause.toString();
            if ((InvalidAlgorithmParameterExceptionTest.msgs[i].length()) > 0) {
                TestCase.assertTrue("getMessage() must contain ".concat(InvalidAlgorithmParameterExceptionTest.msgs[i]), ((getM.indexOf(InvalidAlgorithmParameterExceptionTest.msgs[i])) != (-1)));
                if (!(getM.equals(InvalidAlgorithmParameterExceptionTest.msgs[i]))) {
                    TestCase.assertTrue("getMessage() should contain ".concat(toS), ((getM.indexOf(toS)) != (-1)));
                }
            }
            TestCase.assertNotNull("getCause() must not return null", tE.getCause());
            TestCase.assertEquals("getCause() must return ".concat(InvalidAlgorithmParameterExceptionTest.tCause.toString()), tE.getCause(), InvalidAlgorithmParameterExceptionTest.tCause);
        }
    }
}

