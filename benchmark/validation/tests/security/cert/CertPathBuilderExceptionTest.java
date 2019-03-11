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


import java.security.cert.CertPathBuilderException;
import junit.framework.TestCase;


/**
 * Tests for <code>CertPathBuilderException</code> class constructors and
 * methods.
 */
public class CertPathBuilderExceptionTest extends TestCase {
    private static String[] msgs = new String[]{ "", "Check new message", "Check new message Check new message Check new message Check new message Check new message" };

    private static Throwable tCause = new Throwable("Throwable for exception");

    /**
     * Test for <code>CertPathBuilderException()</code> constructor Assertion:
     * constructs CertPathBuilderException with no detail message
     */
    public void testCertPathBuilderException01() {
        CertPathBuilderException tE = new CertPathBuilderException();
        TestCase.assertNull("getMessage() must return null.", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>CertPathBuilderException(String)</code> constructor
     * Assertion: constructs CertPathBuilderException with detail message msg.
     * Parameter <code>msg</code> is not null.
     */
    public void testCertPathBuilderException02() {
        CertPathBuilderException tE;
        for (int i = 0; i < (CertPathBuilderExceptionTest.msgs.length); i++) {
            tE = new CertPathBuilderException(CertPathBuilderExceptionTest.msgs[i]);
            TestCase.assertEquals("getMessage() must return: ".concat(CertPathBuilderExceptionTest.msgs[i]), tE.getMessage(), CertPathBuilderExceptionTest.msgs[i]);
            TestCase.assertNull("getCause() must return null", tE.getCause());
        }
    }

    /**
     * Test for <code>CertPathBuilderException(String)</code> constructor
     * Assertion: constructs CertPathBuilderException when <code>msg</code> is
     * null
     */
    public void testCertPathBuilderException03() {
        String msg = null;
        CertPathBuilderException tE = new CertPathBuilderException(msg);
        TestCase.assertNull("getMessage() must return null.", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>CertPathBuilderException(Throwable)</code> constructor
     * Assertion: constructs CertPathBuilderException when <code>cause</code>
     * is null
     */
    public void testCertPathBuilderException04() {
        Throwable cause = null;
        CertPathBuilderException tE = new CertPathBuilderException(cause);
        TestCase.assertNull("getMessage() must return null.", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>CertPathBuilderException(Throwable)</code> constructor
     * Assertion: constructs CertPathBuilderException when <code>cause</code>
     * is not null
     */
    public void testCertPathBuilderException05() {
        CertPathBuilderException tE = new CertPathBuilderException(CertPathBuilderExceptionTest.tCause);
        if ((tE.getMessage()) != null) {
            String toS = CertPathBuilderExceptionTest.tCause.toString();
            String getM = tE.getMessage();
            TestCase.assertTrue("getMessage() should contain ".concat(toS), ((getM.indexOf(toS)) != (-1)));
        }
        TestCase.assertNotNull("getCause() must not return null", tE.getCause());
        TestCase.assertEquals("getCause() must return ".concat(CertPathBuilderExceptionTest.tCause.toString()), tE.getCause(), CertPathBuilderExceptionTest.tCause);
    }

    /**
     * Test for <code>CertPathBuilderException(String, Throwable)</code>
     * constructor Assertion: constructs CertPathBuilderException when
     * <code>cause</code> is null <code>msg</code> is null
     */
    public void testCertPathBuilderException06() {
        CertPathBuilderException tE = new CertPathBuilderException(null, null);
        TestCase.assertNull("getMessage() must return null", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>CertPathBuilderException(String, Throwable)</code>
     * constructor Assertion: constructs CertPathBuilderException when
     * <code>cause</code> is null <code>msg</code> is not null
     */
    public void testCertPathBuilderException07() {
        CertPathBuilderException tE;
        for (int i = 0; i < (CertPathBuilderExceptionTest.msgs.length); i++) {
            tE = new CertPathBuilderException(CertPathBuilderExceptionTest.msgs[i], null);
            TestCase.assertEquals("getMessage() must return: ".concat(CertPathBuilderExceptionTest.msgs[i]), tE.getMessage(), CertPathBuilderExceptionTest.msgs[i]);
            TestCase.assertNull("getCause() must return null", tE.getCause());
        }
    }

    /**
     * Test for <code>CertPathBuilderException(String, Throwable)</code>
     * constructor Assertion: constructs CertPathBuilderException when
     * <code>cause</code> is not null <code>msg</code> is null
     */
    public void testCertPathBuilderException08() {
        CertPathBuilderException tE = new CertPathBuilderException(null, CertPathBuilderExceptionTest.tCause);
        if ((tE.getMessage()) != null) {
            String toS = CertPathBuilderExceptionTest.tCause.toString();
            String getM = tE.getMessage();
            TestCase.assertTrue("getMessage() must should ".concat(toS), ((getM.indexOf(toS)) != (-1)));
        }
        TestCase.assertNotNull("getCause() must not return null", tE.getCause());
        TestCase.assertEquals("getCause() must return ".concat(CertPathBuilderExceptionTest.tCause.toString()), tE.getCause(), CertPathBuilderExceptionTest.tCause);
    }

    /**
     * Test for <code>CertPathBuilderException(String, Throwable)</code>
     * constructor Assertion: constructs CertPathBuilderException when
     * <code>cause</code> is not null <code>msg</code> is not null
     */
    public void testCertPathBuilderException09() {
        CertPathBuilderException tE;
        for (int i = 0; i < (CertPathBuilderExceptionTest.msgs.length); i++) {
            tE = new CertPathBuilderException(CertPathBuilderExceptionTest.msgs[i], CertPathBuilderExceptionTest.tCause);
            String getM = tE.getMessage();
            String toS = CertPathBuilderExceptionTest.tCause.toString();
            if ((CertPathBuilderExceptionTest.msgs[i].length()) > 0) {
                TestCase.assertTrue("getMessage() must contain ".concat(CertPathBuilderExceptionTest.msgs[i]), ((getM.indexOf(CertPathBuilderExceptionTest.msgs[i])) != (-1)));
                if (!(getM.equals(CertPathBuilderExceptionTest.msgs[i]))) {
                    TestCase.assertTrue("getMessage() should contain ".concat(toS), ((getM.indexOf(toS)) != (-1)));
                }
            }
            TestCase.assertNotNull("getCause() must not return null", tE.getCause());
            TestCase.assertEquals("getCause() must return ".concat(CertPathBuilderExceptionTest.tCause.toString()), tE.getCause(), CertPathBuilderExceptionTest.tCause);
        }
    }
}

