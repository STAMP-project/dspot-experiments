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


import java.security.NoSuchAlgorithmException;
import junit.framework.TestCase;


/**
 * Tests for <code>NoSuchAlgorithmException</code> class constructors and
 * methods.
 */
public class NoSuchAlgorithmExceptionTest extends TestCase {
    private static String[] msgs = new String[]{ "", "Check new message", "Check new message Check new message Check new message Check new message Check new message" };

    private static Throwable tCause = new Throwable("Throwable for exception");

    /**
     * Test for <code>NoSuchAlgorithmException()</code> constructor Assertion:
     * constructs NoSuchAlgorithmException with no detail message
     */
    public void testNoSuchAlgorithmException01() {
        NoSuchAlgorithmException tE = new NoSuchAlgorithmException();
        TestCase.assertNull("getMessage() must return null.", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>NoSuchAlgorithmException(String)</code> constructor
     * Assertion: constructs NoSuchAlgorithmException with detail message msg.
     * Parameter <code>msg</code> is not null.
     */
    public void testNoSuchAlgorithmException02() {
        NoSuchAlgorithmException tE;
        for (int i = 0; i < (NoSuchAlgorithmExceptionTest.msgs.length); i++) {
            tE = new NoSuchAlgorithmException(NoSuchAlgorithmExceptionTest.msgs[i]);
            TestCase.assertEquals("getMessage() must return: ".concat(NoSuchAlgorithmExceptionTest.msgs[i]), tE.getMessage(), NoSuchAlgorithmExceptionTest.msgs[i]);
            TestCase.assertNull("getCause() must return null", tE.getCause());
        }
    }

    /**
     * Test for <code>NoSuchAlgorithmException(String)</code> constructor
     * Assertion: constructs NoSuchAlgorithmException when <code>msg</code> is
     * null
     */
    public void testNoSuchAlgorithmException03() {
        String msg = null;
        NoSuchAlgorithmException tE = new NoSuchAlgorithmException(msg);
        TestCase.assertNull("getMessage() must return null.", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>NoSuchAlgorithmException(Throwable)</code> constructor
     * Assertion: constructs NoSuchAlgorithmException when <code>cause</code>
     * is null
     */
    public void testNoSuchAlgorithmException04() {
        Throwable cause = null;
        NoSuchAlgorithmException tE = new NoSuchAlgorithmException(cause);
        TestCase.assertNull("getMessage() must return null.", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>NoSuchAlgorithmException(Throwable)</code> constructor
     * Assertion: constructs NoSuchAlgorithmException when <code>cause</code>
     * is not null
     */
    public void testNoSuchAlgorithmException05() {
        NoSuchAlgorithmException tE = new NoSuchAlgorithmException(NoSuchAlgorithmExceptionTest.tCause);
        if ((tE.getMessage()) != null) {
            String toS = NoSuchAlgorithmExceptionTest.tCause.toString();
            String getM = tE.getMessage();
            TestCase.assertTrue("getMessage() should contain ".concat(toS), ((getM.indexOf(toS)) != (-1)));
        }
        TestCase.assertNotNull("getCause() must not return null", tE.getCause());
        TestCase.assertEquals("getCause() must return ".concat(NoSuchAlgorithmExceptionTest.tCause.toString()), tE.getCause(), NoSuchAlgorithmExceptionTest.tCause);
    }

    /**
     * Test for <code>NoSuchAlgorithmException(String, Throwable)</code>
     * constructor Assertion: constructs NoSuchAlgorithmException when
     * <code>cause</code> is null <code>msg</code> is null
     */
    public void testNoSuchAlgorithmException06() {
        NoSuchAlgorithmException tE = new NoSuchAlgorithmException(null, null);
        TestCase.assertNull("getMessage() must return null", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>NoSuchAlgorithmException(String, Throwable)</code>
     * constructor Assertion: constructs NoSuchAlgorithmException when
     * <code>cause</code> is null <code>msg</code> is not null
     */
    public void testNoSuchAlgorithmException07() {
        NoSuchAlgorithmException tE;
        for (int i = 0; i < (NoSuchAlgorithmExceptionTest.msgs.length); i++) {
            tE = new NoSuchAlgorithmException(NoSuchAlgorithmExceptionTest.msgs[i], null);
            TestCase.assertEquals("getMessage() must return: ".concat(NoSuchAlgorithmExceptionTest.msgs[i]), tE.getMessage(), NoSuchAlgorithmExceptionTest.msgs[i]);
            TestCase.assertNull("getCause() must return null", tE.getCause());
        }
    }

    /**
     * Test for <code>NoSuchAlgorithmException(String, Throwable)</code>
     * constructor Assertion: constructs NoSuchAlgorithmException when
     * <code>cause</code> is not null <code>msg</code> is null
     */
    public void testNoSuchAlgorithmException08() {
        NoSuchAlgorithmException tE = new NoSuchAlgorithmException(null, NoSuchAlgorithmExceptionTest.tCause);
        if ((tE.getMessage()) != null) {
            String toS = NoSuchAlgorithmExceptionTest.tCause.toString();
            String getM = tE.getMessage();
            TestCase.assertTrue("getMessage() must should ".concat(toS), ((getM.indexOf(toS)) != (-1)));
        }
        TestCase.assertNotNull("getCause() must not return null", tE.getCause());
        TestCase.assertEquals("getCause() must return ".concat(NoSuchAlgorithmExceptionTest.tCause.toString()), tE.getCause(), NoSuchAlgorithmExceptionTest.tCause);
    }

    /**
     * Test for <code>NoSuchAlgorithmException(String, Throwable)</code>
     * constructor Assertion: constructs NoSuchAlgorithmException when
     * <code>cause</code> is not null <code>msg</code> is not null
     */
    public void testNoSuchAlgorithmException09() {
        NoSuchAlgorithmException tE;
        for (int i = 0; i < (NoSuchAlgorithmExceptionTest.msgs.length); i++) {
            tE = new NoSuchAlgorithmException(NoSuchAlgorithmExceptionTest.msgs[i], NoSuchAlgorithmExceptionTest.tCause);
            String getM = tE.getMessage();
            String toS = NoSuchAlgorithmExceptionTest.tCause.toString();
            if ((NoSuchAlgorithmExceptionTest.msgs[i].length()) > 0) {
                TestCase.assertTrue("getMessage() must contain ".concat(NoSuchAlgorithmExceptionTest.msgs[i]), ((getM.indexOf(NoSuchAlgorithmExceptionTest.msgs[i])) != (-1)));
                if (!(getM.equals(NoSuchAlgorithmExceptionTest.msgs[i]))) {
                    TestCase.assertTrue("getMessage() should contain ".concat(toS), ((getM.indexOf(toS)) != (-1)));
                }
            }
            TestCase.assertNotNull("getCause() must not return null", tE.getCause());
            TestCase.assertEquals("getCause() must return ".concat(NoSuchAlgorithmExceptionTest.tCause.toString()), tE.getCause(), NoSuchAlgorithmExceptionTest.tCause);
        }
    }
}

