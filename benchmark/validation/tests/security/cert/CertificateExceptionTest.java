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


import java.security.cert.CertificateException;
import junit.framework.TestCase;


/**
 * Tests for <code>CertificateException</code> class constructors and methods.
 */
public class CertificateExceptionTest extends TestCase {
    private static String[] msgs = new String[]{ "", "Check new message", "Check new message Check new message Check new message Check new message Check new message" };

    private static Throwable tCause = new Throwable("Throwable for exception");

    /**
     * Test for <code>CertificateException()</code> constructor Assertion:
     * constructs CertificateException with no detail message
     */
    public void testCertificateException01() {
        CertificateException tE = new CertificateException();
        TestCase.assertNull("getMessage() must return null.", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>CertificateException(String)</code> constructor
     * Assertion: constructs CertificateException with detail message msg.
     * Parameter <code>msg</code> is not null.
     */
    public void testCertificateException02() {
        CertificateException tE;
        for (int i = 0; i < (CertificateExceptionTest.msgs.length); i++) {
            tE = new CertificateException(CertificateExceptionTest.msgs[i]);
            TestCase.assertEquals("getMessage() must return: ".concat(CertificateExceptionTest.msgs[i]), tE.getMessage(), CertificateExceptionTest.msgs[i]);
            TestCase.assertNull("getCause() must return null", tE.getCause());
        }
    }

    /**
     * Test for <code>CertificateException(String)</code> constructor
     * Assertion: constructs CertificateException when <code>msg</code> is
     * null
     */
    public void testCertificateException03() {
        String msg = null;
        CertificateException tE = new CertificateException(msg);
        TestCase.assertNull("getMessage() must return null.", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>CertificateException(Throwable)</code> constructor
     * Assertion: constructs CertificateException when <code>cause</code> is
     * null
     */
    public void testCertificateException04() {
        Throwable cause = null;
        CertificateException tE = new CertificateException(cause);
        TestCase.assertNull("getMessage() must return null.", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>CertificateException(Throwable)</code> constructor
     * Assertion: constructs CertificateException when <code>cause</code> is
     * not null
     */
    public void testCertificateException05() {
        CertificateException tE = new CertificateException(CertificateExceptionTest.tCause);
        if ((tE.getMessage()) != null) {
            String toS = CertificateExceptionTest.tCause.toString();
            String getM = tE.getMessage();
            TestCase.assertTrue("getMessage() should contain ".concat(toS), ((getM.indexOf(toS)) != (-1)));
        }
        TestCase.assertNotNull("getCause() must not return null", tE.getCause());
        TestCase.assertEquals("getCause() must return ".concat(CertificateExceptionTest.tCause.toString()), tE.getCause(), CertificateExceptionTest.tCause);
    }

    /**
     * Test for <code>CertificateException(String, Throwable)</code>
     * constructor Assertion: constructs CertificateException when
     * <code>cause</code> is null <code>msg</code> is null
     */
    public void testCertificateException06() {
        CertificateException tE = new CertificateException(null, null);
        TestCase.assertNull("getMessage() must return null", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>CertificateException(String, Throwable)</code>
     * constructor Assertion: constructs CertificateException when
     * <code>cause</code> is null <code>msg</code> is not null
     */
    public void testCertificateException07() {
        CertificateException tE;
        for (int i = 0; i < (CertificateExceptionTest.msgs.length); i++) {
            tE = new CertificateException(CertificateExceptionTest.msgs[i], null);
            TestCase.assertEquals("getMessage() must return: ".concat(CertificateExceptionTest.msgs[i]), tE.getMessage(), CertificateExceptionTest.msgs[i]);
            TestCase.assertNull("getCause() must return null", tE.getCause());
        }
    }

    /**
     * Test for <code>CertificateException(String, Throwable)</code>
     * constructor Assertion: constructs CertificateException when
     * <code>cause</code> is not null <code>msg</code> is null
     */
    public void testCertificateException08() {
        CertificateException tE = new CertificateException(null, CertificateExceptionTest.tCause);
        if ((tE.getMessage()) != null) {
            String toS = CertificateExceptionTest.tCause.toString();
            String getM = tE.getMessage();
            TestCase.assertTrue("getMessage() must should ".concat(toS), ((getM.indexOf(toS)) != (-1)));
        }
        TestCase.assertNotNull("getCause() must not return null", tE.getCause());
        TestCase.assertEquals("getCause() must return ".concat(CertificateExceptionTest.tCause.toString()), tE.getCause(), CertificateExceptionTest.tCause);
    }

    /**
     * Test for <code>CertificateException(String, Throwable)</code>
     * constructor Assertion: constructs CertificateException when
     * <code>cause</code> is not null <code>msg</code> is not null
     */
    public void testCertificateException09() {
        CertificateException tE;
        for (int i = 0; i < (CertificateExceptionTest.msgs.length); i++) {
            tE = new CertificateException(CertificateExceptionTest.msgs[i], CertificateExceptionTest.tCause);
            String getM = tE.getMessage();
            String toS = CertificateExceptionTest.tCause.toString();
            if ((CertificateExceptionTest.msgs[i].length()) > 0) {
                TestCase.assertTrue("getMessage() must contain ".concat(CertificateExceptionTest.msgs[i]), ((getM.indexOf(CertificateExceptionTest.msgs[i])) != (-1)));
                if (!(getM.equals(CertificateExceptionTest.msgs[i]))) {
                    TestCase.assertTrue("getMessage() should contain ".concat(toS), ((getM.indexOf(toS)) != (-1)));
                }
            }
            TestCase.assertNotNull("getCause() must not return null", tE.getCause());
            TestCase.assertEquals("getCause() must return ".concat(CertificateExceptionTest.tCause.toString()), tE.getCause(), CertificateExceptionTest.tCause);
        }
    }
}

