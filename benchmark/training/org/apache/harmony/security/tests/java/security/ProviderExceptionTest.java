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


import java.security.ProviderException;
import junit.framework.TestCase;


/**
 * Tests for <code>ProviderException</code> class constructors and methods.
 */
public class ProviderExceptionTest extends TestCase {
    private static String[] msgs = new String[]{ "", "Check new message", "Check new message Check new message Check new message Check new message Check new message" };

    private static Throwable tCause = new Throwable("Throwable for exception");

    /**
     * Test for <code>ProviderException()</code> constructor Assertion:
     * constructs ProviderException with no detail message
     */
    public void testProviderException01() {
        ProviderException tE = new ProviderException();
        TestCase.assertNull("getMessage() must return null.", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>ProviderException(String)</code> constructor Assertion:
     * constructs ProviderException with detail message msg. Parameter
     * <code>msg</code> is not null.
     */
    public void testProviderException02() {
        ProviderException tE;
        for (int i = 0; i < (ProviderExceptionTest.msgs.length); i++) {
            tE = new ProviderException(ProviderExceptionTest.msgs[i]);
            TestCase.assertEquals("getMessage() must return: ".concat(ProviderExceptionTest.msgs[i]), tE.getMessage(), ProviderExceptionTest.msgs[i]);
            TestCase.assertNull("getCause() must return null", tE.getCause());
        }
    }

    /**
     * Test for <code>ProviderException(String)</code> constructor Assertion:
     * constructs ProviderException when <code>msg</code> is null
     */
    public void testProviderException03() {
        String msg = null;
        ProviderException tE = new ProviderException(msg);
        TestCase.assertNull("getMessage() must return null.", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>ProviderException(Throwable)</code> constructor
     * Assertion: constructs ProviderException when <code>cause</code> is null
     */
    public void testProviderException04() {
        Throwable cause = null;
        ProviderException tE = new ProviderException(cause);
        TestCase.assertNull("getMessage() must return null.", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>ProviderException(Throwable)</code> constructor
     * Assertion: constructs ProviderException when <code>cause</code> is not
     * null
     */
    public void testProviderException05() {
        ProviderException tE = new ProviderException(ProviderExceptionTest.tCause);
        if ((tE.getMessage()) != null) {
            String toS = ProviderExceptionTest.tCause.toString();
            String getM = tE.getMessage();
            TestCase.assertTrue("getMessage() should contain ".concat(toS), ((getM.indexOf(toS)) != (-1)));
        }
        TestCase.assertNotNull("getCause() must not return null", tE.getCause());
        TestCase.assertEquals("getCause() must return ".concat(ProviderExceptionTest.tCause.toString()), tE.getCause(), ProviderExceptionTest.tCause);
    }

    /**
     * Test for <code>ProviderException(String, Throwable)</code> constructor
     * Assertion: constructs ProviderException when <code>cause</code> is null
     * <code>msg</code> is null
     */
    public void testProviderException06() {
        ProviderException tE = new ProviderException(null, null);
        TestCase.assertNull("getMessage() must return null", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>ProviderException(String, Throwable)</code> constructor
     * Assertion: constructs ProviderException when <code>cause</code> is null
     * <code>msg</code> is not null
     */
    public void testProviderException07() {
        ProviderException tE;
        for (int i = 0; i < (ProviderExceptionTest.msgs.length); i++) {
            tE = new ProviderException(ProviderExceptionTest.msgs[i], null);
            TestCase.assertEquals("getMessage() must return: ".concat(ProviderExceptionTest.msgs[i]), tE.getMessage(), ProviderExceptionTest.msgs[i]);
            TestCase.assertNull("getCause() must return null", tE.getCause());
        }
    }

    /**
     * Test for <code>ProviderException(String, Throwable)</code> constructor
     * Assertion: constructs ProviderException when <code>cause</code> is not
     * null <code>msg</code> is null
     */
    public void testProviderException08() {
        ProviderException tE = new ProviderException(null, ProviderExceptionTest.tCause);
        if ((tE.getMessage()) != null) {
            String toS = ProviderExceptionTest.tCause.toString();
            String getM = tE.getMessage();
            TestCase.assertTrue("getMessage() must should ".concat(toS), ((getM.indexOf(toS)) != (-1)));
        }
        TestCase.assertNotNull("getCause() must not return null", tE.getCause());
        TestCase.assertEquals("getCause() must return ".concat(ProviderExceptionTest.tCause.toString()), tE.getCause(), ProviderExceptionTest.tCause);
    }

    /**
     * Test for <code>ProviderException(String, Throwable)</code> constructor
     * Assertion: constructs ProviderException when <code>cause</code> is not
     * null <code>msg</code> is not null
     */
    public void testProviderException09() {
        ProviderException tE;
        for (int i = 0; i < (ProviderExceptionTest.msgs.length); i++) {
            tE = new ProviderException(ProviderExceptionTest.msgs[i], ProviderExceptionTest.tCause);
            String getM = tE.getMessage();
            String toS = ProviderExceptionTest.tCause.toString();
            if ((ProviderExceptionTest.msgs[i].length()) > 0) {
                TestCase.assertTrue("getMessage() must contain ".concat(ProviderExceptionTest.msgs[i]), ((getM.indexOf(ProviderExceptionTest.msgs[i])) != (-1)));
                if (!(getM.equals(ProviderExceptionTest.msgs[i]))) {
                    TestCase.assertTrue("getMessage() should contain ".concat(toS), ((getM.indexOf(toS)) != (-1)));
                }
            }
            TestCase.assertNotNull("getCause() must not return null", tE.getCause());
            TestCase.assertEquals("getCause() must return ".concat(ProviderExceptionTest.tCause.toString()), tE.getCause(), ProviderExceptionTest.tCause);
        }
    }
}

