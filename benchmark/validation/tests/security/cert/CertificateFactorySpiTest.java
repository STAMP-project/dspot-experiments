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


import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactorySpi;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import junit.framework.TestCase;
import org.apache.harmony.security.tests.support.cert.MyCertificateFactorySpi;


/**
 * Tests for <code>CertificateFactorySpi</code> class constructors and methods
 */
public class CertificateFactorySpiTest extends TestCase {
    /**
     * Test for <code>CertificateFactorySpi</code> constructor
     * Assertion: constructs CertificateFactorySpi
     */
    public void testCertificateFactorySpi01() throws CRLException, CertificateException {
        CertificateFactorySpi certFactorySpi = new CertificateFactorySpiTest.extCertificateFactorySpi();
        ByteArrayInputStream bais = new ByteArrayInputStream(new byte[0]);
        try {
            certFactorySpi.engineGenerateCertPath(bais);
            TestCase.fail("UnsupportedOperationException must be thrown");
        } catch (UnsupportedOperationException e) {
        }
        try {
            certFactorySpi.engineGenerateCertPath(bais, "");
            TestCase.fail("UnsupportedOperationException must be thrown");
        } catch (UnsupportedOperationException e) {
        }
        try {
            List<Certificate> list = null;
            certFactorySpi.engineGenerateCertPath(list);
            TestCase.fail("UnsupportedOperationException must be thrown");
        } catch (UnsupportedOperationException e) {
        }
        try {
            certFactorySpi.engineGetCertPathEncodings();
            TestCase.fail("UnsupportedOperationException must be thrown");
        } catch (UnsupportedOperationException e) {
        }
        Certificate cc = certFactorySpi.engineGenerateCertificate(bais);
        TestCase.assertNull("Not null Cerificate", cc);
        try {
            certFactorySpi.engineGenerateCertificate(null);
            TestCase.fail("CertificateException must be thrown");
        } catch (CertificateException e) {
        }
        Collection<? extends Certificate> col = certFactorySpi.engineGenerateCertificates(bais);
        TestCase.assertNull("Not null Collection", col);
        try {
            certFactorySpi.engineGenerateCertificates(null);
            TestCase.fail("CertificateException must be thrown");
        } catch (CertificateException e) {
        }
        CRL ccCRL = certFactorySpi.engineGenerateCRL(bais);
        TestCase.assertNull("Not null CRL", ccCRL);
        try {
            certFactorySpi.engineGenerateCRL(null);
            TestCase.fail("CRLException must be thrown");
        } catch (CRLException e) {
        }
        Collection<? extends CRL> colCRL = certFactorySpi.engineGenerateCRLs(bais);
        TestCase.assertNull("Not null CRL", colCRL);
        try {
            certFactorySpi.engineGenerateCRLs(null);
            TestCase.fail("CRLException must be thrown");
        } catch (CRLException e) {
        }
    }

    /**
     * Test for <code>CertificateFactorySpi</code> constructor
     * Assertion: constructs CertificateFactorySpi
     */
    public void testCertificateFactorySpi02() throws CRLException, CertificateException {
        CertificateFactorySpi certFactorySpi = new MyCertificateFactorySpi();
        MyCertificateFactorySpi.putMode(true);
        ByteArrayInputStream bais = new ByteArrayInputStream(new byte[0]);
        DataInputStream dis = new DataInputStream(bais);
        try {
            certFactorySpi.engineGenerateCertPath(bais);
            TestCase.fail("CertificateException must be thrown");
        } catch (CertificateException e) {
        }
        certFactorySpi.engineGenerateCertPath(dis);
        try {
            certFactorySpi.engineGenerateCertPath(bais, "aa");
            TestCase.fail("CertificateException must be thrown");
        } catch (CertificateException e) {
        }
        try {
            certFactorySpi.engineGenerateCertPath(dis, "");
            TestCase.fail("IllegalArgumentException must be thrown");
        } catch (IllegalArgumentException e) {
        }
        certFactorySpi.engineGenerateCertPath(dis, "ss");
        try {
            certFactorySpi.engineGenerateCertificate(bais);
            TestCase.fail("CertificateException must be thrown");
        } catch (CertificateException e) {
        }
        try {
            certFactorySpi.engineGenerateCertificates(null);
            TestCase.fail("CertificateException must be thrown");
        } catch (CertificateException e) {
        }
        Certificate cert = certFactorySpi.engineGenerateCertificate(dis);
        TestCase.assertNull("Result must be null", cert);
        Collection<? extends Certificate> col = certFactorySpi.engineGenerateCertificates(dis);
        TestCase.assertNull("Result must be null", col);
        try {
            certFactorySpi.engineGenerateCRL(bais);
            TestCase.fail("CRLException must be thrown");
        } catch (CRLException e) {
        }
        try {
            certFactorySpi.engineGenerateCRLs(null);
            TestCase.fail("CRLException must be thrown");
        } catch (CRLException e) {
        }
        CRL crl = certFactorySpi.engineGenerateCRL(dis);
        TestCase.assertNull("Result must be null", crl);
        Collection<? extends CRL> colcrl = certFactorySpi.engineGenerateCRLs(dis);
        TestCase.assertNull("Result must be null", colcrl);
        List<Certificate> list = null;
        try {
            certFactorySpi.engineGenerateCertPath(list);
            TestCase.fail("NullPointerException must be thrown");
        } catch (NullPointerException e) {
        }
        Iterator<String> enc = certFactorySpi.engineGetCertPathEncodings();
        TestCase.assertTrue("Incorrect Iterator", enc.hasNext());
    }

    /**
     * Test for <code>CertificateFactorySpi</code> constructor
     * Assertion: constructs CertificateFactorySpi
     */
    public void testCertificateFactorySpi03() throws CRLException, CertificateException {
        CertificateFactorySpi certFactorySpi = new MyCertificateFactorySpi();
        MyCertificateFactorySpi.putMode(false);
        ByteArrayInputStream bais = new ByteArrayInputStream(new byte[0]);
        DataInputStream dis = new DataInputStream(bais);
        try {
            certFactorySpi.engineGenerateCertPath(bais);
            TestCase.fail("CertificateException must be thrown");
        } catch (CertificateException e) {
        }
        try {
            certFactorySpi.engineGenerateCertPath(dis);
            TestCase.fail("CertificateException must be thrown");
        } catch (CertificateException e) {
        }
        try {
            certFactorySpi.engineGenerateCertPath(bais, "aa");
            TestCase.fail("CertificateException must be thrown");
        } catch (CertificateException e) {
        }
        certFactorySpi.engineGenerateCertPath(dis, "");
        certFactorySpi.engineGenerateCertPath(dis, "ss");
        try {
            certFactorySpi.engineGenerateCertificate(bais);
            TestCase.fail("CertificateException must be thrown");
        } catch (CertificateException e) {
        }
        try {
            certFactorySpi.engineGenerateCertificates(null);
            TestCase.fail("CertificateException must be thrown");
        } catch (CertificateException e) {
        }
        Certificate cert = certFactorySpi.engineGenerateCertificate(dis);
        TestCase.assertNull("Result must be null", cert);
        Collection<? extends Certificate> col = certFactorySpi.engineGenerateCertificates(dis);
        TestCase.assertNull("Result must be null", col);
        try {
            certFactorySpi.engineGenerateCRL(bais);
            TestCase.fail("CRLException must be thrown");
        } catch (CRLException e) {
        }
        try {
            certFactorySpi.engineGenerateCRLs(null);
            TestCase.fail("CRLException must be thrown");
        } catch (CRLException e) {
        }
        CRL crl = certFactorySpi.engineGenerateCRL(dis);
        TestCase.assertNull("Result must be null", crl);
        Collection<? extends CRL> colcrl = certFactorySpi.engineGenerateCRLs(dis);
        TestCase.assertNull("Result must be null", colcrl);
        List<Certificate> list = null;
        certFactorySpi.engineGenerateCertPath(list);
        Iterator<String> enc = certFactorySpi.engineGetCertPathEncodings();
        TestCase.assertFalse("Incorrect Iterator", enc.hasNext());
    }

    /**
     * Test for <code>engineGenerateCertPath(InputStream)</code> method.
     * Assertion: Generates a <code>CertPath</code> object and initializes it
     * with the data read from the <code>InputStream</code>
     */
    public void testEngineGenerateCertPathLjava_io_InputStream01() {
        CertificateFactorySpi certFactorySpi = new MyCertificateFactorySpi();
        MyCertificateFactorySpi.putMode(true);
        ByteArrayInputStream bais = new ByteArrayInputStream(new byte[0]);
        DataInputStream dis = new DataInputStream(bais);
        try {
            TestCase.assertNull(certFactorySpi.engineGenerateCertPath(dis));
        } catch (CertificateException e) {
            TestCase.fail(("Unexpected CertificateException " + (e.getMessage())));
        }
    }

    /**
     * Test for <code>engineGenerateCertPath(InputStream)</code> method.
     * Assertion: Generates a <code>CertPath</code> object and initializes it
     * with the data read from the <code>InputStream</code>
     */
    public void testEngineGenerateCertPathLjava_io_InputStream02() {
        CertificateFactorySpi certFactorySpi = new CertificateFactorySpiTest.extCertificateFactorySpi();
        ByteArrayInputStream bais = new ByteArrayInputStream(new byte[0]);
        DataInputStream dis = new DataInputStream(bais);
        try {
            certFactorySpi.engineGenerateCertPath(dis);
            TestCase.fail("UnsupportedOperationException expected");
        } catch (UnsupportedOperationException e) {
            // expected
        } catch (CertificateException e) {
            TestCase.fail(("Unexpected CertificateException " + (e.getMessage())));
        }
    }

    /**
     * Test for <code>engineGenerateCertPath(InputStream, String)</code>
     * method. Assertion: generates a <code>CertPath</code> object and
     * initializes it with the data read from the <code>InputStream</code>
     */
    public void testEngineGenerateCertPathLjava_io_InputStream_Ljava_lang_String01() {
        CertificateFactorySpi certFactorySpi = new MyCertificateFactorySpi();
        MyCertificateFactorySpi.putMode(true);
        ByteArrayInputStream bais = new ByteArrayInputStream(new byte[0]);
        DataInputStream dis = new DataInputStream(bais);
        try {
            certFactorySpi.engineGenerateCertPath(dis, "");
            TestCase.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        } catch (CertificateException e) {
            TestCase.fail(("Unexpected CertificateException " + (e.getMessage())));
        }
        try {
            TestCase.assertNull(certFactorySpi.engineGenerateCertPath(dis, "encoding"));
        } catch (CertificateException e) {
            TestCase.fail(("Unexpected CertificateException " + (e.getMessage())));
        }
    }

    /**
     * Test for <code>engineGenerateCertPath(InputStream, String)</code>
     * method. Assertion: generates a <code>CertPath</code> object and
     * initializes it with the data read from the <code>InputStream</code>
     */
    public void testEngineGenerateCertPathLjava_io_InputStream_Ljava_lang_String02() {
        CertificateFactorySpi certFactorySpi = new CertificateFactorySpiTest.extCertificateFactorySpi();
        ByteArrayInputStream bais = new ByteArrayInputStream(new byte[0]);
        DataInputStream dis = new DataInputStream(bais);
        try {
            certFactorySpi.engineGenerateCertPath(dis, "encoding");
            TestCase.fail("UnsupportedOperationException expected");
        } catch (UnsupportedOperationException e) {
            // expected
        } catch (CertificateException e) {
            TestCase.fail(("Unexpected CertificateException " + (e.getMessage())));
        }
    }

    /**
     * Test for <code>engineGenerateCertPath(List<? extends Certificate>)</code>
     * method Assertion: generates a <code>CertPath</code> object and
     * initializes it with a <code>List</code> of <code>Certificates</code>
     */
    public void testEngineGenerateCertPathLJava_util_List01() {
        CertificateFactorySpi certFactorySpi = new MyCertificateFactorySpi();
        MyCertificateFactorySpi.putMode(true);
        List<Certificate> list = new ArrayList<Certificate>();
        try {
            TestCase.assertNull(certFactorySpi.engineGenerateCertPath(list));
        } catch (CertificateException e) {
            TestCase.fail(("Unexpected CertificateException " + (e.getMessage())));
        }
        try {
            certFactorySpi.engineGenerateCertPath(((List<? extends Certificate>) (null)));
            TestCase.fail("expected NullPointerException");
        } catch (NullPointerException e) {
            // ok
        } catch (CertificateException e) {
            TestCase.fail(("Unexpected CertificateException " + (e.getMessage())));
        }
    }

    /**
     * Test for <code>engineGenerateCertPath(List<? extends Certificate>)</code>
     * method Assertion: generates a <code>CertPath</code> object and
     * initializes it with a <code>List</code> of <code>Certificates</code>
     */
    public void testEngineGenerateCertPathLJava_util_List02() {
        CertificateFactorySpi certFactorySpi = new CertificateFactorySpiTest.extCertificateFactorySpi();
        List<Certificate> list = new ArrayList<Certificate>();
        try {
            certFactorySpi.engineGenerateCertPath(list);
            TestCase.fail("UnsupportedOperationException expected");
        } catch (UnsupportedOperationException e) {
            // expected
        } catch (CertificateException e) {
            TestCase.fail(("Unexpected CertificateException " + (e.getMessage())));
        }
    }

    public void testAbstractMethods() {
        CertificateFactorySpi certFactorySpi = new CertificateFactorySpiTest.extCertificateFactorySpi();
        ByteArrayInputStream bais = new ByteArrayInputStream(new byte[3]);
        DataInputStream dis = new DataInputStream(bais);
        try {
            certFactorySpi.engineGenerateCRL(dis);
            certFactorySpi.engineGenerateCRLs(dis);
            certFactorySpi.engineGenerateCertificate(dis);
            certFactorySpi.engineGenerateCertificates(dis);
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception " + (e.getMessage())));
        }
    }

    private static class extCertificateFactorySpi extends CertificateFactorySpi {
        public Certificate engineGenerateCertificate(InputStream inStream) throws CertificateException {
            if (inStream == null) {
                throw new CertificateException("InputStream null");
            }
            return null;
        }

        @SuppressWarnings("unchecked")
        public Collection engineGenerateCertificates(InputStream inStream) throws CertificateException {
            if (inStream == null) {
                throw new CertificateException("InputStream null");
            }
            return null;
        }

        public CRL engineGenerateCRL(InputStream inStream) throws CRLException {
            if (inStream == null) {
                throw new CRLException("InputStream null");
            }
            return null;
        }

        @SuppressWarnings("unchecked")
        public Collection engineGenerateCRLs(InputStream inStream) throws CRLException {
            if (inStream == null) {
                throw new CRLException("InputStream null");
            }
            return null;
        }
    }
}

