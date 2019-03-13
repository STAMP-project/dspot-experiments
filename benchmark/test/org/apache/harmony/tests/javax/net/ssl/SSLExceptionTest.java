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
package org.apache.harmony.tests.javax.net.ssl;


import javax.net.ssl.SSLException;
import junit.framework.TestCase;


/**
 * Tests for <code>SSLException</code> class constructors and methods.
 */
public class SSLExceptionTest extends TestCase {
    private static String[] msgs = new String[]{ "", "Check new message", "Check new message Check new message Check new message Check new message Check new message" };

    private static Throwable tCause = new Throwable("Throwable for exception");

    /**
     * Test for <code>SSLException(String)</code> constructor Assertion:
     * constructs SSLException with detail message msg. Parameter
     * <code>msg</code> is not null.
     */
    public void testSSLException01() {
        SSLException sE;
        for (int i = 0; i < (SSLExceptionTest.msgs.length); i++) {
            sE = new SSLException(SSLExceptionTest.msgs[i]);
            TestCase.assertEquals("getMessage() must return: ".concat(SSLExceptionTest.msgs[i]), sE.getMessage(), SSLExceptionTest.msgs[i]);
            TestCase.assertNull("getCause() must return null", sE.getCause());
        }
    }

    /**
     * Test for <code>SSLException(String)</code> constructor Assertion:
     * constructs SSLException when <code>msg</code> is null
     */
    public void testSSLException02() {
        String msg = null;
        SSLException sE = new SSLException(msg);
        TestCase.assertNull("getMessage() must return null.", sE.getMessage());
        TestCase.assertNull("getCause() must return null", sE.getCause());
    }

    /**
     * Test for <code>SSLException(Throwable)</code> constructor
     * Assertion: constructs SSLException when <code>cause</code> is null
     */
    public void testSSLException03() {
        Throwable cause = null;
        SSLException sE = new SSLException(cause);
        TestCase.assertNull("getMessage() must return null.", sE.getMessage());
        TestCase.assertNull("getCause() must return null", sE.getCause());
    }

    /**
     * Test for <code>SSLException(Throwable)</code> constructor
     * Assertion: constructs SSLException when <code>cause</code> is not
     * null
     */
    public void testSSLException04() {
        SSLException sE = new SSLException(SSLExceptionTest.tCause);
        if ((sE.getMessage()) != null) {
            String toS = SSLExceptionTest.tCause.toString();
            String getM = sE.getMessage();
            TestCase.assertTrue("getMessage() should contain ".concat(toS), ((getM.indexOf(toS)) != (-1)));
        }
        TestCase.assertNotNull("getCause() must not return null", sE.getCause());
        TestCase.assertEquals("getCause() must return ".concat(SSLExceptionTest.tCause.toString()), sE.getCause(), SSLExceptionTest.tCause);
    }

    /**
     * Test for <code>SSLException(String, Throwable)</code> constructor
     * Assertion: constructs SSLException when <code>cause</code> is null
     * <code>msg</code> is null
     */
    public void testSSLException05() {
        SSLException sE = new SSLException(null, null);
        TestCase.assertNull("getMessage() must return null", sE.getMessage());
        TestCase.assertNull("getCause() must return null", sE.getCause());
    }

    /**
     * Test for <code>SSLException(String, Throwable)</code> constructor
     * Assertion: constructs SSLException when <code>cause</code> is null
     * <code>msg</code> is not null
     */
    public void testSSLException06() {
        SSLException sE;
        for (int i = 0; i < (SSLExceptionTest.msgs.length); i++) {
            sE = new SSLException(SSLExceptionTest.msgs[i], null);
            TestCase.assertEquals("getMessage() must return: ".concat(SSLExceptionTest.msgs[i]), sE.getMessage(), SSLExceptionTest.msgs[i]);
            TestCase.assertNull("getCause() must return null", sE.getCause());
        }
    }

    /**
     * Test for <code>SSLException(String, Throwable)</code> constructor
     * Assertion: constructs SSLException when <code>cause</code> is not
     * null <code>msg</code> is null
     */
    public void testSSLException07() {
        SSLException sE = new SSLException(null, SSLExceptionTest.tCause);
        if ((sE.getMessage()) != null) {
            String toS = SSLExceptionTest.tCause.toString();
            String getM = sE.getMessage();
            TestCase.assertTrue("getMessage() must should ".concat(toS), ((getM.indexOf(toS)) != (-1)));
        }
        TestCase.assertNotNull("getCause() must not return null", sE.getCause());
        TestCase.assertEquals("getCause() must return ".concat(SSLExceptionTest.tCause.toString()), sE.getCause(), SSLExceptionTest.tCause);
    }

    /**
     * Test for <code>SSLException(String, Throwable)</code> constructor
     * Assertion: constructs SSLException when <code>cause</code> is not
     * null <code>msg</code> is not null
     */
    public void testSSLException08() {
        SSLException sE;
        for (int i = 0; i < (SSLExceptionTest.msgs.length); i++) {
            sE = new SSLException(SSLExceptionTest.msgs[i], SSLExceptionTest.tCause);
            String getM = sE.getMessage();
            String toS = SSLExceptionTest.tCause.toString();
            if ((SSLExceptionTest.msgs[i].length()) > 0) {
                TestCase.assertTrue("getMessage() must contain ".concat(SSLExceptionTest.msgs[i]), ((getM.indexOf(SSLExceptionTest.msgs[i])) != (-1)));
                if (!(getM.equals(SSLExceptionTest.msgs[i]))) {
                    TestCase.assertTrue("getMessage() should contain ".concat(toS), ((getM.indexOf(toS)) != (-1)));
                }
            }
            TestCase.assertNotNull("getCause() must not return null", sE.getCause());
            TestCase.assertEquals("getCause() must return ".concat(SSLExceptionTest.tCause.toString()), sE.getCause(), SSLExceptionTest.tCause);
        }
    }
}

