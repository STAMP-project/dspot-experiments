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
package tests.security.cert;


import java.security.cert.CertStoreException;
import junit.framework.TestCase;


/**
 * Tests for <code>CertStoreException</code> class constructors and methods.
 */
public class CertStoreExceptionTest extends TestCase {
    private static String[] msgs = new String[]{ "", "Check new message", "Check new message Check new message Check new message Check new message Check new message" };

    private static Throwable tCause = new Throwable("Throwable for exception");

    /**
     * Test for <code>CertStoreException()</code> constructor Assertion:
     * constructs CertStoreException with no detail message
     */
    public void testCertStoreException01() {
        CertStoreException tE = new CertStoreException();
        TestCase.assertNull("getMessage() must return null.", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>CertStoreException(String)</code> constructor Assertion:
     * constructs CertStoreException with detail message msg. Parameter
     * <code>msg</code> is not null.
     */
    public void testCertStoreException02() {
        CertStoreException tE;
        for (int i = 0; i < (CertStoreExceptionTest.msgs.length); i++) {
            tE = new CertStoreException(CertStoreExceptionTest.msgs[i]);
            TestCase.assertEquals("getMessage() must return: ".concat(CertStoreExceptionTest.msgs[i]), tE.getMessage(), CertStoreExceptionTest.msgs[i]);
            TestCase.assertNull("getCause() must return null", tE.getCause());
        }
    }

    /**
     * Test for <code>CertStoreException(String)</code> constructor Assertion:
     * constructs CertStoreException when <code>msg</code> is null
     */
    public void testCertStoreException03() {
        String msg = null;
        CertStoreException tE = new CertStoreException(msg);
        TestCase.assertNull("getMessage() must return null.", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>CertStoreException(Throwable)</code> constructor
     * Assertion: constructs CertStoreException when <code>cause</code> is
     * null
     */
    public void testCertStoreException04() {
        Throwable cause = null;
        CertStoreException tE = new CertStoreException(cause);
        TestCase.assertNull("getMessage() must return null.", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>CertStoreException(Throwable)</code> constructor
     * Assertion: constructs CertStoreException when <code>cause</code> is not
     * null
     */
    public void testCertStoreException05() {
        CertStoreException tE = new CertStoreException(CertStoreExceptionTest.tCause);
        if ((tE.getMessage()) != null) {
            String toS = CertStoreExceptionTest.tCause.toString();
            String getM = tE.getMessage();
            TestCase.assertTrue("getMessage() should contain ".concat(toS), ((getM.indexOf(toS)) != (-1)));
        }
        TestCase.assertNotNull("getCause() must not return null", tE.getCause());
        TestCase.assertEquals("getCause() must return ".concat(CertStoreExceptionTest.tCause.toString()), tE.getCause(), CertStoreExceptionTest.tCause);
    }

    /**
     * Test for <code>CertStoreException(String, Throwable)</code> constructor
     * Assertion: constructs CertStoreException when <code>cause</code> is
     * null <code>msg</code> is null
     */
    public void testCertStoreException06() {
        CertStoreException tE = new CertStoreException(null, null);
        TestCase.assertNull("getMessage() must return null", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>CertStoreException(String, Throwable)</code> constructor
     * Assertion: constructs CertStoreException when <code>cause</code> is
     * null <code>msg</code> is not null
     */
    public void testCertStoreException07() {
        CertStoreException tE;
        for (int i = 0; i < (CertStoreExceptionTest.msgs.length); i++) {
            tE = new CertStoreException(CertStoreExceptionTest.msgs[i], null);
            TestCase.assertEquals("getMessage() must return: ".concat(CertStoreExceptionTest.msgs[i]), tE.getMessage(), CertStoreExceptionTest.msgs[i]);
            TestCase.assertNull("getCause() must return null", tE.getCause());
        }
    }

    /**
     * Test for <code>CertStoreException(String, Throwable)</code> constructor
     * Assertion: constructs CertStoreException when <code>cause</code> is not
     * null <code>msg</code> is null
     */
    public void testCertStoreException08() {
        CertStoreException tE = new CertStoreException(null, CertStoreExceptionTest.tCause);
        if ((tE.getMessage()) != null) {
            String toS = CertStoreExceptionTest.tCause.toString();
            String getM = tE.getMessage();
            TestCase.assertTrue("getMessage() must should ".concat(toS), ((getM.indexOf(toS)) != (-1)));
        }
        TestCase.assertNotNull("getCause() must not return null", tE.getCause());
        TestCase.assertEquals("getCause() must return ".concat(CertStoreExceptionTest.tCause.toString()), tE.getCause(), CertStoreExceptionTest.tCause);
    }

    /**
     * Test for <code>CertStoreException(String, Throwable)</code> constructor
     * Assertion: constructs CertStoreException when <code>cause</code> is not
     * null <code>msg</code> is not null
     */
    public void testCertStoreException09() {
        CertStoreException tE;
        for (int i = 0; i < (CertStoreExceptionTest.msgs.length); i++) {
            tE = new CertStoreException(CertStoreExceptionTest.msgs[i], CertStoreExceptionTest.tCause);
            String getM = tE.getMessage();
            String toS = CertStoreExceptionTest.tCause.toString();
            if ((CertStoreExceptionTest.msgs[i].length()) > 0) {
                TestCase.assertTrue("getMessage() must contain ".concat(CertStoreExceptionTest.msgs[i]), ((getM.indexOf(CertStoreExceptionTest.msgs[i])) != (-1)));
                if (!(getM.equals(CertStoreExceptionTest.msgs[i]))) {
                    TestCase.assertTrue("getMessage() should contain ".concat(toS), ((getM.indexOf(toS)) != (-1)));
                }
            }
            TestCase.assertNotNull("getCause() must not return null", tE.getCause());
            TestCase.assertEquals("getCause() must return ".concat(CertStoreExceptionTest.tCause.toString()), tE.getCause(), CertStoreExceptionTest.tCause);
        }
    }
}

