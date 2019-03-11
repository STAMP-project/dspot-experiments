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


import java.security.cert.CertPath;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import junit.framework.TestCase;


/**
 * Tests for <code>CertPathValidatorException</code> class constructors and
 * methods.
 */
public class CertPathValidatorExceptionTest extends TestCase {
    private static String[] msgs = new String[]{ "", "Check new message", "Check new message Check new message Check new message Check new message Check new message" };

    private static Throwable tCause = new Throwable("Throwable for exception");

    /**
     * Test for <code>CertPathValidatorException()</code> constructor
     * Assertion: constructs CertPathValidatorException with no detail message
     */
    public void testCertPathValidatorException01() {
        CertPathValidatorException tE = new CertPathValidatorException();
        TestCase.assertNull("getMessage() must return null.", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>CertPathValidatorException(String)</code> constructor
     * Assertion: constructs CertPathValidatorException with detail message msg.
     * Parameter <code>msg</code> is not null.
     */
    public void testCertPathValidatorException02() {
        CertPathValidatorException tE;
        for (int i = 0; i < (CertPathValidatorExceptionTest.msgs.length); i++) {
            tE = new CertPathValidatorException(CertPathValidatorExceptionTest.msgs[i]);
            TestCase.assertEquals("getMessage() must return: ".concat(CertPathValidatorExceptionTest.msgs[i]), tE.getMessage(), CertPathValidatorExceptionTest.msgs[i]);
            TestCase.assertNull("getCause() must return null", tE.getCause());
        }
    }

    /**
     * Test for <code>CertPathValidatorException(String)</code> constructor
     * Assertion: constructs CertPathValidatorException when <code>msg</code>
     * is null
     */
    public void testCertPathValidatorException03() {
        String msg = null;
        CertPathValidatorException tE = new CertPathValidatorException(msg);
        TestCase.assertNull("getMessage() must return null.", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>CertPathValidatorException(Throwable)</code> constructor
     * Assertion: constructs CertPathValidatorException when <code>cause</code>
     * is null
     */
    public void testCertPathValidatorException04() {
        Throwable cause = null;
        CertPathValidatorException tE = new CertPathValidatorException(cause);
        TestCase.assertNull("getMessage() must return null.", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>CertPathValidatorException(Throwable)</code> constructor
     * Assertion: constructs CertPathValidatorException when <code>cause</code>
     * is not null
     */
    public void testCertPathValidatorException05() {
        CertPathValidatorException tE = new CertPathValidatorException(CertPathValidatorExceptionTest.tCause);
        if ((tE.getMessage()) != null) {
            String toS = CertPathValidatorExceptionTest.tCause.toString();
            String getM = tE.getMessage();
            TestCase.assertTrue("getMessage() should contain ".concat(toS), ((getM.indexOf(toS)) != (-1)));
        }
        TestCase.assertNotNull("getCause() must not return null", tE.getCause());
        TestCase.assertEquals("getCause() must return ".concat(CertPathValidatorExceptionTest.tCause.toString()), tE.getCause(), CertPathValidatorExceptionTest.tCause);
    }

    /**
     * Test for <code>CertPathValidatorException(String, Throwable)</code>
     * constructor Assertion: constructs CertPathValidatorException when
     * <code>cause</code> is null <code>msg</code> is null
     */
    public void testCertPathValidatorException06() {
        CertPathValidatorException tE = new CertPathValidatorException(null, null);
        TestCase.assertNull("getMessage() must return null", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>CertPathValidatorException(String, Throwable)</code>
     * constructor Assertion: constructs CertPathValidatorException when
     * <code>cause</code> is null <code>msg</code> is not null
     */
    public void testCertPathValidatorException07() {
        CertPathValidatorException tE;
        for (int i = 0; i < (CertPathValidatorExceptionTest.msgs.length); i++) {
            tE = new CertPathValidatorException(CertPathValidatorExceptionTest.msgs[i], null);
            TestCase.assertEquals("getMessage() must return: ".concat(CertPathValidatorExceptionTest.msgs[i]), tE.getMessage(), CertPathValidatorExceptionTest.msgs[i]);
            TestCase.assertNull("getCause() must return null", tE.getCause());
        }
    }

    /**
     * Test for <code>CertPathValidatorException(String, Throwable)</code>
     * constructor Assertion: constructs CertPathValidatorException when
     * <code>cause</code> is not null <code>msg</code> is null
     */
    public void testCertPathValidatorException08() {
        CertPathValidatorException tE = new CertPathValidatorException(null, CertPathValidatorExceptionTest.tCause);
        if ((tE.getMessage()) != null) {
            String toS = CertPathValidatorExceptionTest.tCause.toString();
            String getM = tE.getMessage();
            TestCase.assertTrue("getMessage() must should ".concat(toS), ((getM.indexOf(toS)) != (-1)));
        }
        TestCase.assertNotNull("getCause() must not return null", tE.getCause());
        TestCase.assertEquals("getCause() must return ".concat(CertPathValidatorExceptionTest.tCause.toString()), tE.getCause(), CertPathValidatorExceptionTest.tCause);
    }

    /**
     * Test for <code>CertPathValidatorException(String, Throwable)</code>
     * constructor Assertion: constructs CertPathValidatorException when
     * <code>cause</code> is not null <code>msg</code> is not null
     */
    public void testCertPathValidatorException09() {
        CertPathValidatorException tE;
        for (int i = 0; i < (CertPathValidatorExceptionTest.msgs.length); i++) {
            tE = new CertPathValidatorException(CertPathValidatorExceptionTest.msgs[i], CertPathValidatorExceptionTest.tCause);
            String getM = tE.getMessage();
            String toS = CertPathValidatorExceptionTest.tCause.toString();
            if ((CertPathValidatorExceptionTest.msgs[i].length()) > 0) {
                TestCase.assertTrue("getMessage() must contain ".concat(CertPathValidatorExceptionTest.msgs[i]), ((getM.indexOf(CertPathValidatorExceptionTest.msgs[i])) != (-1)));
                if (!(getM.equals(CertPathValidatorExceptionTest.msgs[i]))) {
                    TestCase.assertTrue("getMessage() should contain ".concat(toS), ((getM.indexOf(toS)) != (-1)));
                }
            }
            TestCase.assertNotNull("getCause() must not return null", tE.getCause());
            TestCase.assertEquals("getCause() must return ".concat(CertPathValidatorExceptionTest.tCause.toString()), tE.getCause(), CertPathValidatorExceptionTest.tCause);
        }
    }

    /**
     * Test for
     * <code>CertPathValidatorException(String, Throwable, CertPath, int)</code>
     * constructor Assertion: constructs CertPathValidatorException when
     * <code>cause</code> is null <code>msg</code> is null
     * <code>certPath</code> is null <code>index</code> is -1
     */
    public void testCertPathValidatorException10() {
        CertPathValidatorException tE = new CertPathValidatorException(null, null, null, (-1));
        TestCase.assertNull("getMessage() must return null", tE.getMessage());
        TestCase.assertNull("getCause() must return null", tE.getCause());
        TestCase.assertNull("getCertPath() must return null", tE.getCertPath());
        TestCase.assertEquals("getIndex() must be -1", tE.getIndex(), (-1));
    }

    /**
     * Test for
     * <code>CertPathValidatorException(String, Throwable, CertPath, int)</code>
     * constructor Assertion: constructs CertPathValidatorException when
     * <code>cause</code> is null <code>msg</code> is null
     * <code>certPath</code> is null <code>index</code> not -1 throws:
     * IllegalArgumentException
     */
    public void testCertPathValidatorException11() {
        int[] indx = new int[]{ 0, 1, 100, Integer.MAX_VALUE, Integer.MIN_VALUE };
        for (int j = 0; j < (indx.length); j++) {
            for (int i = 0; i < (CertPathValidatorExceptionTest.msgs.length); i++) {
                try {
                    new CertPathValidatorException(CertPathValidatorExceptionTest.msgs[i], CertPathValidatorExceptionTest.tCause, null, indx[j]);
                    TestCase.fail((((("Error. IllegalArgumentException was not thrown as expected. " + " msg: ") + (CertPathValidatorExceptionTest.msgs[i])) + ", certPath is null and index is ") + (indx[j])));
                } catch (IllegalArgumentException e) {
                }
            }
        }
    }

    /**
     * Test for
     * <code>CertPathValidatorException(String, Throwable, CertPath, int)</code>
     * constructor Assertion: constructs CertPathValidatorException when
     * <code>cause</code> not null <code>msg</code> not null
     * <code>certPath</code> is null <code>index</code> is -1
     */
    public void testCertPathValidatorException12() {
        CertPathValidatorException tE;
        for (int i = 0; i < (CertPathValidatorExceptionTest.msgs.length); i++) {
            try {
                tE = new CertPathValidatorException(CertPathValidatorExceptionTest.msgs[i], CertPathValidatorExceptionTest.tCause, null, (-1));
                String getM = tE.getMessage();
                String toS = CertPathValidatorExceptionTest.tCause.toString();
                if ((CertPathValidatorExceptionTest.msgs[i].length()) > 0) {
                    TestCase.assertTrue("getMessage() must contain ".concat(CertPathValidatorExceptionTest.msgs[i]), ((getM.indexOf(CertPathValidatorExceptionTest.msgs[i])) != (-1)));
                    if (!(getM.equals(CertPathValidatorExceptionTest.msgs[i]))) {
                        TestCase.assertTrue("getMessage() should contain ".concat(toS), ((getM.indexOf(toS)) != (-1)));
                    }
                }
                TestCase.assertNotNull("getCause() must not return null", tE.getCause());
                TestCase.assertEquals("getCause() must return ".concat(CertPathValidatorExceptionTest.tCause.toString()), tE.getCause(), CertPathValidatorExceptionTest.tCause);
                TestCase.assertNull("getCertPath() must return null", tE.getCertPath());
                TestCase.assertEquals("getIndex() must return -1", tE.getIndex(), (-1));
            } catch (IndexOutOfBoundsException e) {
                TestCase.fail((((("Unexpected exception: " + (e.toString())) + " Parameters: msg: ") + (CertPathValidatorExceptionTest.msgs[i])) + ", certPath is null and index is -1"));
            }
        }
    }

    /**
     * Test for
     * <code>CertPathValidatorException(String, Throwable, CertPath, int)</code>
     * constructor Assertion: constructs CertPathValidatorException when
     * <code>cause</code> not null <code>msg</code> not null
     * <code>certPath</code> not null <code>index</code>< -1 || >=
     * certPath.getCertificates().size() throws: IndexOutOfBoundsException
     */
    public void testCertPathValidatorException13() {
        CertPathValidatorExceptionTest.myCertPath mcp = new CertPathValidatorExceptionTest.myCertPath("X.509", "");
        CertPath cp = mcp.get("X.509");
        int[] indx = new int[]{ -2, -100, 0, 1, 100, Integer.MAX_VALUE, Integer.MIN_VALUE };
        for (int j = 0; j < (indx.length); j++) {
            for (int i = 0; i < (CertPathValidatorExceptionTest.msgs.length); i++) {
                try {
                    new CertPathValidatorException(CertPathValidatorExceptionTest.msgs[i], CertPathValidatorExceptionTest.tCause, cp, indx[j]);
                    TestCase.fail((((("IndexOutOfBoundsException was not thrown as expected. " + " msg: ") + (CertPathValidatorExceptionTest.msgs[i])) + ", certPath is null and index is ") + (indx[j])));
                } catch (IndexOutOfBoundsException e) {
                }
            }
        }
    }

    /**
     * Test for
     * <code>CertPathValidatorException(String, Throwable, CertPath, int)</code>
     * constructor Assertion: constructs CertPathValidatorException when
     * <code>cause</code> not null <code>msg</code> not null
     * <code>certPath</code> not null <code>index</code><
     * certPath.getCertificates().size()
     */
    public void testCertPathValidatorException14() {
        CertPathValidatorException tE;
        CertPathValidatorExceptionTest.myCertPath mcp = new CertPathValidatorExceptionTest.myCertPath("X.509", "");
        CertPath cp = mcp.get("X.509");
        for (int i = 0; i < (CertPathValidatorExceptionTest.msgs.length); i++) {
            try {
                tE = new CertPathValidatorException(CertPathValidatorExceptionTest.msgs[i], CertPathValidatorExceptionTest.tCause, cp, (-1));
                String getM = tE.getMessage();
                String toS = CertPathValidatorExceptionTest.tCause.toString();
                if ((CertPathValidatorExceptionTest.msgs[i].length()) > 0) {
                    TestCase.assertTrue("getMessage() must contain ".concat(CertPathValidatorExceptionTest.msgs[i]), ((getM.indexOf(CertPathValidatorExceptionTest.msgs[i])) != (-1)));
                    if (!(getM.equals(CertPathValidatorExceptionTest.msgs[i]))) {
                        TestCase.assertTrue("getMessage() should contain ".concat(toS), ((getM.indexOf(toS)) != (-1)));
                    }
                }
                TestCase.assertNotNull("getCause() must not return null", tE.getCause());
                TestCase.assertEquals("getCause() must return ".concat(CertPathValidatorExceptionTest.tCause.toString()), tE.getCause(), CertPathValidatorExceptionTest.tCause);
                TestCase.assertNotNull("getCertPath() must not return null", tE.getCertPath());
                TestCase.assertEquals("getCertPath() must return ".concat(cp.toString()), tE.getCertPath(), cp);
                TestCase.assertEquals("getIndex() must return -1", tE.getIndex(), (-1));
            } catch (IndexOutOfBoundsException e) {
                TestCase.fail(("Unexpected IndexOutOfBoundsException was thrown. " + (e.toString())));
            }
        }
    }

    /**
     * Test for <code>getCertPath()</code>. Returns the certification path
     * that was being validated when the exception was thrown.
     */
    public void testCertPathValidatorException15() {
        CertPathValidatorException tE = new CertPathValidatorException();
        TestCase.assertNull("getCertPath() must return null.", tE.getCertPath());
        for (int i = 0; i < (CertPathValidatorExceptionTest.msgs.length); i++) {
            tE = new CertPathValidatorException(CertPathValidatorExceptionTest.msgs[i]);
            TestCase.assertNull("getCertPath() must return null ", tE.getCertPath());
        }
        Throwable cause = null;
        tE = new CertPathValidatorException(cause);
        TestCase.assertNull("getCertPath() must return null.", tE.getCertPath());
        tE = new CertPathValidatorException(CertPathValidatorExceptionTest.tCause);
        TestCase.assertNull("getCertPath() must return null.", tE.getCertPath());
        for (int i = 0; i < (CertPathValidatorExceptionTest.msgs.length); i++) {
            tE = new CertPathValidatorException(CertPathValidatorExceptionTest.msgs[i], CertPathValidatorExceptionTest.tCause);
            TestCase.assertNull("getCertPath() must return null", tE.getCertPath());
        }
        tE = new CertPathValidatorException(null, null, null, (-1));
        TestCase.assertNull("getCertPath() must return null", tE.getCertPath());
        for (int i = 0; i < (CertPathValidatorExceptionTest.msgs.length); i++) {
            try {
                tE = new CertPathValidatorException(CertPathValidatorExceptionTest.msgs[i], CertPathValidatorExceptionTest.tCause, null, (-1));
                TestCase.assertNull("getCertPath() must return null", tE.getCertPath());
            } catch (IndexOutOfBoundsException e) {
                TestCase.fail(("Unexpected exception: " + (e.getMessage())));
            }
        }
        CertPathValidatorExceptionTest.myCertPath mcp = new CertPathValidatorExceptionTest.myCertPath("X.509", "");
        CertPath cp = mcp.get("X.509");
        for (int i = 0; i < (CertPathValidatorExceptionTest.msgs.length); i++) {
            try {
                tE = new CertPathValidatorException(CertPathValidatorExceptionTest.msgs[i], CertPathValidatorExceptionTest.tCause, cp, (-1));
                TestCase.assertNotNull("getCertPath() must not return null", tE.getCertPath());
                TestCase.assertEquals("getCertPath() must return ".concat(cp.toString()), tE.getCertPath(), cp);
            } catch (IndexOutOfBoundsException e) {
                TestCase.fail(("Unexpected IndexOutOfBoundsException was thrown. " + (e.toString())));
            }
        }
    }

    /**
     * Test for <code>getIndex()</code>. Returns the index of the certificate
     * in the certification path that caused the exception to be thrown. Note
     * that the list of certificates in a CertPath is zero based. If no index
     * has been set, -1 is returned.
     */
    public void testCertPathValidatorException16() {
        CertPathValidatorException tE = new CertPathValidatorException();
        TestCase.assertEquals("getIndex() must be equals -1", (-1), tE.getIndex());
        for (int i = 0; i < (CertPathValidatorExceptionTest.msgs.length); i++) {
            tE = new CertPathValidatorException(CertPathValidatorExceptionTest.msgs[i]);
            TestCase.assertEquals("getIndex() must be equals -1", (-1), tE.getIndex());
        }
        Throwable cause = null;
        tE = new CertPathValidatorException(cause);
        TestCase.assertEquals("getIndex() must be equals -1", (-1), tE.getIndex());
        tE = new CertPathValidatorException(CertPathValidatorExceptionTest.tCause);
        TestCase.assertEquals("getIndex() must be equals -1", (-1), tE.getIndex());
        for (int i = 0; i < (CertPathValidatorExceptionTest.msgs.length); i++) {
            tE = new CertPathValidatorException(CertPathValidatorExceptionTest.msgs[i], CertPathValidatorExceptionTest.tCause);
            TestCase.assertEquals("getIndex() must be equals -1", (-1), tE.getIndex());
        }
        tE = new CertPathValidatorException(null, null, null, (-1));
        TestCase.assertEquals("getIndex() must be equals -1", (-1), tE.getIndex());
        CertPathValidatorExceptionTest.myCertPath mcp = new CertPathValidatorExceptionTest.myCertPath("X.509", "");
        CertPath cp = mcp.get("X.509");
        for (int i = 0; i < (CertPathValidatorExceptionTest.msgs.length); i++) {
            try {
                tE = new CertPathValidatorException(CertPathValidatorExceptionTest.msgs[i], CertPathValidatorExceptionTest.tCause, cp, (-1));
                TestCase.assertNotNull("getIndex() must not return null", tE.getCertPath());
                TestCase.assertEquals("getIndex() must return ".concat(cp.toString()), tE.getCertPath(), cp);
            } catch (IndexOutOfBoundsException e) {
                TestCase.fail(("Unexpected IndexOutOfBoundsException was thrown. " + (e.getMessage())));
            }
        }
    }

    class myCertPath extends CertPath {
        private static final long serialVersionUID = 5871603047244722511L;

        public List<Certificate> getCertificates() {
            return new Vector<Certificate>();
        }

        public byte[] getEncoded() {
            return new byte[0];
        }

        public byte[] getEncoded(String s) {
            return new byte[0];
        }

        public Iterator<String> getEncodings() {
            return ((Iterator<String>) (new StringTokenizer("ss ss ss ss")));
        }

        protected myCertPath(String s) {
            super(s);
        }

        public CertPath get(String s) {
            return new CertPathValidatorExceptionTest.myCertPath(s);
        }

        public myCertPath(String s, String s1) {
            super(s);
        }
    }
}

