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


import java.security.cert.CertificateParsingException;
import junit.framework.TestCase;


/**
 * Tests for <code>CertificateParsingException</code> class constructors and
 * methods.
 */
public class CertificateParsingExceptionTest extends TestCase {
    private static String[] msgs = new String[]{ "", "Check new message", "Check new message Check new message Check new message Check new message Check new message" };

    private static Throwable tCause = new Throwable("Throwable for exception");

    /**
     * Test for <code>CertificateParsingException()</code> constructor
     * Assertion: constructs CertificateParsingException with no detail message
     */
    public void testCertificateParsingException01() {
        CertificateParsingException tE = new CertificateParsingException();
        TestCase.assertNull("getMessage() must return null.", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>CertificateParsingException(String)</code> constructor
     * Assertion: constructs CertificateParsingException with detail message
     * msg. Parameter <code>msg</code> is not null.
     */
    public void testCertificateParsingException02() {
        CertificateParsingException tE;
        for (int i = 0; i < (CertificateParsingExceptionTest.msgs.length); i++) {
            tE = new CertificateParsingException(CertificateParsingExceptionTest.msgs[i]);
            TestCase.assertEquals("getMessage() must return: ".concat(CertificateParsingExceptionTest.msgs[i]), tE.getMessage(), CertificateParsingExceptionTest.msgs[i]);
            TestCase.assertNull("getCause() must return null", tE.getCause());
        }
    }

    /**
     * Test for <code>CertificateParsingException(String)</code> constructor
     * Assertion: constructs CertificateParsingException when <code>msg</code>
     * is null
     */
    public void testCertificateParsingException03() {
        String msg = null;
        CertificateParsingException tE = new CertificateParsingException(msg);
        TestCase.assertNull("getMessage() must return null.", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>CertificateParsingException(Throwable)</code>
     * constructor Assertion: constructs CertificateParsingException when
     * <code>cause</code> is null
     */
    public void testCertificateParsingException04() {
        Throwable cause = null;
        CertificateParsingException tE = new CertificateParsingException(cause);
        TestCase.assertNull("getMessage() must return null.", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>CertificateParsingException(Throwable)</code>
     * constructor Assertion: constructs CertificateParsingException when
     * <code>cause</code> is not null
     */
    public void testCertificateParsingException05() {
        CertificateParsingException tE = new CertificateParsingException(CertificateParsingExceptionTest.tCause);
        if ((tE.getMessage()) != null) {
            String toS = CertificateParsingExceptionTest.tCause.toString();
            String getM = tE.getMessage();
            TestCase.assertTrue("getMessage() should contain ".concat(toS), ((getM.indexOf(toS)) != (-1)));
        }
        TestCase.assertNotNull("getCause() must not return null", tE.getCause());
        TestCase.assertEquals("getCause() must return ".concat(CertificateParsingExceptionTest.tCause.toString()), tE.getCause(), CertificateParsingExceptionTest.tCause);
    }

    /**
     * Test for <code>CertificateParsingException(String, Throwable)</code>
     * constructor Assertion: constructs CertificateParsingException when
     * <code>cause</code> is null <code>msg</code> is null
     */
    public void testCertificateParsingException06() {
        CertificateParsingException tE = new CertificateParsingException(null, null);
        TestCase.assertNull("getMessage() must return null", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>CertificateParsingException(String, Throwable)</code>
     * constructor Assertion: constructs CertificateParsingException when
     * <code>cause</code> is null <code>msg</code> is not null
     */
    public void testCertificateParsingException07() {
        CertificateParsingException tE;
        for (int i = 0; i < (CertificateParsingExceptionTest.msgs.length); i++) {
            tE = new CertificateParsingException(CertificateParsingExceptionTest.msgs[i], null);
            TestCase.assertEquals("getMessage() must return: ".concat(CertificateParsingExceptionTest.msgs[i]), tE.getMessage(), CertificateParsingExceptionTest.msgs[i]);
            TestCase.assertNull("getCause() must return null", tE.getCause());
        }
    }

    /**
     * Test for <code>CertificateParsingException(String, Throwable)</code>
     * constructor Assertion: constructs CertificateParsingException when
     * <code>cause</code> is not null <code>msg</code> is null
     */
    public void testCertificateParsingException08() {
        CertificateParsingException tE = new CertificateParsingException(null, CertificateParsingExceptionTest.tCause);
        if ((tE.getMessage()) != null) {
            String toS = CertificateParsingExceptionTest.tCause.toString();
            String getM = tE.getMessage();
            TestCase.assertTrue("getMessage() must should ".concat(toS), ((getM.indexOf(toS)) != (-1)));
        }
        TestCase.assertNotNull("getCause() must not return null", tE.getCause());
        TestCase.assertEquals("getCause() must return ".concat(CertificateParsingExceptionTest.tCause.toString()), tE.getCause(), CertificateParsingExceptionTest.tCause);
    }

    /**
     * Test for <code>CertificateParsingException(String, Throwable)</code>
     * constructor Assertion: constructs CertificateParsingException when
     * <code>cause</code> is not null <code>msg</code> is not null
     */
    public void testCertificateParsingException09() {
        CertificateParsingException tE;
        for (int i = 0; i < (CertificateParsingExceptionTest.msgs.length); i++) {
            tE = new CertificateParsingException(CertificateParsingExceptionTest.msgs[i], CertificateParsingExceptionTest.tCause);
            String getM = tE.getMessage();
            String toS = CertificateParsingExceptionTest.tCause.toString();
            if ((CertificateParsingExceptionTest.msgs[i].length()) > 0) {
                TestCase.assertTrue("getMessage() must contain ".concat(CertificateParsingExceptionTest.msgs[i]), ((getM.indexOf(CertificateParsingExceptionTest.msgs[i])) != (-1)));
                if (!(getM.equals(CertificateParsingExceptionTest.msgs[i]))) {
                    TestCase.assertTrue("getMessage() should contain ".concat(toS), ((getM.indexOf(toS)) != (-1)));
                }
            }
            TestCase.assertNotNull("getCause() must not return null", tE.getCause());
            TestCase.assertEquals("getCause() must return ".concat(CertificateParsingExceptionTest.tCause.toString()), tE.getCause(), CertificateParsingExceptionTest.tCause);
        }
    }
}

