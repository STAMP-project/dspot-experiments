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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateFactorySpi;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import junit.framework.TestCase;
import org.apache.harmony.security.tests.support.SpiEngUtils;
import org.apache.harmony.security.tests.support.cert.MyCertPath;
import org.apache.harmony.security.tests.support.cert.MyCertificate;
import org.apache.harmony.security.tests.support.cert.MyCertificateFactorySpi;


/**
 * Tests for <code>CertificateFactory</code> class methods and constructor
 */
public class CertificateFactory1Test extends TestCase {
    public static final String srvCertificateFactory = "CertificateFactory";

    private static String defaultProviderName = null;

    private static Provider defaultProvider = null;

    private static boolean X509Support = false;

    public static String defaultType = "X.509";

    public static final String[] validValues = new String[]{ "X.509", "x.509" };

    private static final String[] invalidValues = SpiEngUtils.invalidValues;

    private static String NotSupportMsg = "";

    static {
        CertificateFactory1Test.defaultProvider = SpiEngUtils.isSupport(CertificateFactory1Test.defaultType, CertificateFactory1Test.srvCertificateFactory);
        CertificateFactory1Test.X509Support = (CertificateFactory1Test.defaultProvider) != null;
        CertificateFactory1Test.defaultProviderName = (CertificateFactory1Test.X509Support) ? CertificateFactory1Test.defaultProvider.getName() : null;
        CertificateFactory1Test.NotSupportMsg = CertificateFactory1Test.defaultType.concat(" is not supported");
    }

    /**
     * Test for <code>getInstance(String type)</code> method
     * Assertion: returns CertificateFactory if type is X.509
     */
    public void testCertificateFactory01() throws CertificateException {
        if (!(CertificateFactory1Test.X509Support)) {
            TestCase.fail(CertificateFactory1Test.NotSupportMsg);
            return;
        }
        for (int i = 0; i < (CertificateFactory1Test.validValues.length); i++) {
            CertificateFactory certF = CertificateFactory.getInstance(CertificateFactory1Test.validValues[i]);
            TestCase.assertEquals("Incorrect type: ", CertificateFactory1Test.validValues[i], certF.getType());
        }
    }

    /**
     * Test for <code>getInstance(String type)</code> method
     * Assertion:
     * throws NullPointerException when type is null
     * throws CertificateException when type is not available
     */
    public void testCertificateFactory02() {
        try {
            CertificateFactory.getInstance(null);
            TestCase.fail("NullPointerException or CertificateException must be thrown when type is null");
        } catch (CertificateException e) {
        } catch (NullPointerException e) {
        }
        for (int i = 0; i < (CertificateFactory1Test.invalidValues.length); i++) {
            try {
                CertificateFactory.getInstance(CertificateFactory1Test.invalidValues[i]);
                TestCase.fail("CertificateException must be thrown when type: ".concat(CertificateFactory1Test.invalidValues[i]));
            } catch (CertificateException e) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String type, String provider)</code> method
     * Assertion: throws IllegalArgumentException when provider is null or empty
     */
    public void testCertificateFactory03() throws NoSuchProviderException, CertificateException {
        if (!(CertificateFactory1Test.X509Support)) {
            TestCase.fail(CertificateFactory1Test.NotSupportMsg);
            return;
        }
        String provider = null;
        for (int i = 0; i < (CertificateFactory1Test.validValues.length); i++) {
            try {
                CertificateFactory.getInstance(CertificateFactory1Test.validValues[i], provider);
                TestCase.fail("IllegalArgumentException must be thrown when provider is null");
            } catch (IllegalArgumentException e) {
            }
            try {
                CertificateFactory.getInstance(CertificateFactory1Test.validValues[i], "");
                TestCase.fail("IllegalArgumentException  must be thrown when provider is empty");
            } catch (IllegalArgumentException e) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String type, String provider)</code> method
     * Assertion:
     * throws NullPointerException when type is null
     * throws CertificateException when type is not available
     */
    public void testCertificateFactory04() throws NoSuchProviderException {
        if (!(CertificateFactory1Test.X509Support)) {
            TestCase.fail(CertificateFactory1Test.NotSupportMsg);
            return;
        }
        try {
            CertificateFactory.getInstance(null, CertificateFactory1Test.defaultProviderName);
            TestCase.fail("NullPointerException or CertificateException must be thrown when type is null");
        } catch (CertificateException e) {
        } catch (NullPointerException e) {
        }
        for (int i = 0; i < (CertificateFactory1Test.invalidValues.length); i++) {
            try {
                CertificateFactory.getInstance(CertificateFactory1Test.invalidValues[i], CertificateFactory1Test.defaultProviderName);
                TestCase.fail("CertificateException must be thrown (type: ".concat(CertificateFactory1Test.invalidValues[i]).concat(" provider: ").concat(CertificateFactory1Test.defaultProviderName).concat(")"));
            } catch (CertificateException e) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String type, String provider)</code> method
     * Assertion: returns CertificateFactory when type and provider have valid
     * values
     */
    public void testCertificateFactory05() throws NoSuchProviderException, CertificateException {
        if (!(CertificateFactory1Test.X509Support)) {
            TestCase.fail(CertificateFactory1Test.NotSupportMsg);
            return;
        }
        CertificateFactory certF;
        for (int i = 0; i < (CertificateFactory1Test.validValues.length); i++) {
            certF = CertificateFactory.getInstance(CertificateFactory1Test.validValues[i], CertificateFactory1Test.defaultProviderName);
            TestCase.assertEquals("Incorrect type", certF.getType(), CertificateFactory1Test.validValues[i]);
            TestCase.assertEquals("Incorrect provider name", certF.getProvider().getName(), CertificateFactory1Test.defaultProviderName);
        }
    }

    /**
     * Test for <code>getInstance(String type, Provider provider)</code>
     * method
     * Assertion: throws IllegalArgumentException when provider is null
     */
    public void testCertificateFactory06() throws CertificateException {
        if (!(CertificateFactory1Test.X509Support)) {
            TestCase.fail(CertificateFactory1Test.NotSupportMsg);
            return;
        }
        Provider provider = null;
        for (int i = 0; i < (CertificateFactory1Test.validValues.length); i++) {
            try {
                CertificateFactory.getInstance(CertificateFactory1Test.validValues[i], provider);
                TestCase.fail("IllegalArgumentException must be thrown  when provider is null");
            } catch (IllegalArgumentException e) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String type, Provider provider)</code>
     * method
     * Assertion:
     * throws NullPointerException when type is null
     * throws CertificateException when type is not available
     */
    public void testCertificateFactory07() {
        if (!(CertificateFactory1Test.X509Support)) {
            TestCase.fail(CertificateFactory1Test.NotSupportMsg);
            return;
        }
        try {
            CertificateFactory.getInstance(null, CertificateFactory1Test.defaultProvider);
            TestCase.fail("NullPointerException or CertificateException must be thrown when type is null");
        } catch (CertificateException e) {
        } catch (NullPointerException e) {
        }
        for (int i = 0; i < (CertificateFactory1Test.invalidValues.length); i++) {
            try {
                CertificateFactory.getInstance(CertificateFactory1Test.invalidValues[i], CertificateFactory1Test.defaultProvider);
                TestCase.fail("CertificateException was not thrown as expected (type:".concat(CertificateFactory1Test.invalidValues[i]).concat(" provider: ").concat(CertificateFactory1Test.defaultProvider.getName()).concat(")"));
            } catch (CertificateException e) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String type, Provider provider)</code>
     * method
     * Assertion: returns CertificateFactorythrows when type and provider
     * have valid values
     */
    public void testCertificateFactory08() throws CertificateException {
        if (!(CertificateFactory1Test.X509Support)) {
            TestCase.fail(CertificateFactory1Test.NotSupportMsg);
            return;
        }
        CertificateFactory certF;
        for (int i = 0; i < (CertificateFactory1Test.validValues.length); i++) {
            certF = CertificateFactory.getInstance(CertificateFactory1Test.validValues[i], CertificateFactory1Test.defaultProvider);
            TestCase.assertEquals("Incorrect provider", certF.getProvider(), CertificateFactory1Test.defaultProvider);
            TestCase.assertEquals("Incorrect type", certF.getType(), CertificateFactory1Test.validValues[i]);
        }
    }

    /**
     * Test for <code>getCertPathEncodings()</code> method
     * Assertion: returns encodings
     */
    public void testCertificateFactory09() {
        if (!(CertificateFactory1Test.X509Support)) {
            TestCase.fail(CertificateFactory1Test.NotSupportMsg);
            return;
        }
        CertificateFactory[] certFs = CertificateFactory1Test.initCertFs();
        TestCase.assertNotNull("CertificateFactory objects were not created", certFs);
        Iterator<String> it1 = certFs[0].getCertPathEncodings();
        Iterator<String> it2 = certFs[1].getCertPathEncodings();
        TestCase.assertEquals("Incorrect encodings", it1.hasNext(), it2.hasNext());
        while (it1.hasNext()) {
            it2 = certFs[1].getCertPathEncodings();
            String s1 = it1.next();
            boolean yesNo = false;
            while (it2.hasNext()) {
                if (s1.equals(it2.next())) {
                    yesNo = true;
                    break;
                }
            } 
            TestCase.assertTrue("Encoding: ".concat(s1).concat(" does not define for certF2 CertificateFactory"), yesNo);
        } 
        it1 = certFs[0].getCertPathEncodings();
        it2 = certFs[2].getCertPathEncodings();
        TestCase.assertEquals("Incorrect encodings", it1.hasNext(), it2.hasNext());
        while (it1.hasNext()) {
            it2 = certFs[2].getCertPathEncodings();
            String s1 = it1.next();
            boolean yesNo = false;
            while (it2.hasNext()) {
                if (s1.equals(it2.next())) {
                    yesNo = true;
                    break;
                }
            } 
            TestCase.assertTrue("Encoding: ".concat(s1).concat(" does not define for certF3 CertificateFactory"), yesNo);
        } 
    }

    /**
     * Test for <code>generateCertificate(InputStream inStream)</code>
     * <code>generateCertificates(InputStream inStream)</code>
     * <code>generateCRL(InputStream inStream)</code>
     * <code>generateCRLs(InputStream inStream)</code>
     * methods
     * Assertion: throw CertificateException and CRLException when
     * inStream is null or empty
     */
    public void testCertificateFactory10() {
        if (!(CertificateFactory1Test.X509Support)) {
            TestCase.fail(CertificateFactory1Test.NotSupportMsg);
            return;
        }
        CertificateFactory[] certFs = CertificateFactory1Test.initCertFs();
        TestCase.assertNotNull("CertificateFactory objects were not created", certFs);
        byte[] bb = new byte[]{  };
        InputStream is = new ByteArrayInputStream(bb);
        Collection<?> colCer;
        Collection<?> colCrl;
        for (int i = 0; i < (certFs.length); i++) {
            try {
                certFs[i].generateCertificate(null);
                TestCase.fail("generateCertificate must thrown CertificateException or NullPointerEXception when input stream is null");
            } catch (CertificateException e) {
            } catch (NullPointerException e) {
            }
            is = new ByteArrayInputStream(bb);
            try {
                certFs[i].generateCertificates(null);
                TestCase.fail("generateCertificates must throw CertificateException or NullPointerException when input stream is null");
            } catch (CertificateException e) {
            } catch (NullPointerException e) {
            }
            is = new ByteArrayInputStream(bb);
            try {
                certFs[i].generateCertificate(is);
            } catch (CertificateException e) {
            }
            is = new ByteArrayInputStream(bb);
            try {
                colCer = certFs[i].generateCertificates(is);
                if (colCer != null) {
                    TestCase.assertTrue("Not empty certificate collection", colCer.isEmpty());
                }
            } catch (CertificateException e) {
            }
        }
        for (int i = 0; i < (certFs.length); i++) {
            try {
                certFs[i].generateCRL(null);
            } catch (CRLException e) {
            } catch (NullPointerException e) {
            }
            try {
                colCrl = certFs[i].generateCRLs(null);
                if (colCrl != null) {
                    TestCase.assertTrue("Not empty CRL collection was returned from null stream", colCrl.isEmpty());
                }
            } catch (CRLException e) {
            } catch (NullPointerException e) {
            }
            is = new ByteArrayInputStream(bb);
            try {
                certFs[i].generateCRL(is);
            } catch (CRLException e) {
            }
            is = new ByteArrayInputStream(bb);
            try {
                certFs[i].generateCRLs(is);
                colCrl = certFs[i].generateCRLs(null);
                if (colCrl != null) {
                    TestCase.assertTrue("Not empty CRL collection was returned from empty stream", colCrl.isEmpty());
                }
            } catch (CRLException e) {
            }
        }
    }

    /* Test for <code> generateCertificate(InputStream inStream) </code><code>
    generateCertificates(InputStream inStream) </code><code>
    generateCRL(InputStream inStream) </code><code>
    generateCRLs(InputStream inStream) </code>
    methods
    Assertion: throw CertificateException and CRLException when inStream
    contains incompatible datas
     */
    public void testCertificateFactory11() throws IOException {
        if (!(CertificateFactory1Test.X509Support)) {
            TestCase.fail(CertificateFactory1Test.NotSupportMsg);
            return;
        }
        CertificateFactory[] certFs = CertificateFactory1Test.initCertFs();
        TestCase.assertNotNull("CertificateFactory objects were not created", certFs);
        MyCertificate mc = CertificateFactory1Test.createMC();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject(mc);
        oos.flush();
        oos.close();
        Certificate cer;
        Collection<?> colCer;
        CRL crl;
        Collection<?> colCrl;
        byte[] arr = os.toByteArray();
        ByteArrayInputStream is;
        for (int i = 0; i < (certFs.length); i++) {
            is = new ByteArrayInputStream(arr);
            try {
                cer = certFs[i].generateCertificate(is);
                TestCase.assertNull("Not null certificate was created", cer);
            } catch (CertificateException e) {
            }
            is = new ByteArrayInputStream(arr);
            try {
                colCer = certFs[i].generateCertificates(is);
                if (colCer != null) {
                    TestCase.assertTrue("Not empty certificate Collection was created", colCer.isEmpty());
                }
            } catch (CertificateException e) {
            }
            is = new ByteArrayInputStream(arr);
            try {
                crl = certFs[i].generateCRL(is);
                TestCase.assertNull("Not null CRL was created", crl);
            } catch (CRLException e) {
            }
            is = new ByteArrayInputStream(arr);
            try {
                colCrl = certFs[i].generateCRLs(is);
                if (colCrl != null) {
                    TestCase.assertTrue("Not empty CRL Collection was created", colCrl.isEmpty());
                }
            } catch (CRLException e) {
            }
        }
    }

    /**
     * Test for <code>generateCertPath(InputStream inStream)</code>
     * <code>generateCertPath(InputStream inStream, String encoding)</code>
     * methods
     * Assertion: throws CertificateException when inStream is null or
     * when isStream contains invalid datas
     */
    public void testCertificateFactory12() {
        if (!(CertificateFactory1Test.X509Support)) {
            TestCase.fail(CertificateFactory1Test.NotSupportMsg);
            return;
        }
        CertificateFactory[] certFs = CertificateFactory1Test.initCertFs();
        TestCase.assertNotNull("CertificateFactory objects were not created", certFs);
        InputStream is1 = null;
        InputStream is2 = new ByteArrayInputStream(new byte[10]);
        for (int i = 0; i < (certFs.length); i++) {
            try {
                certFs[i].generateCertPath(is1);
                TestCase.fail("generateCertificate must thrown CertificateException or NullPointerException when input stream is null");
            } catch (CertificateException e) {
            } catch (NullPointerException e) {
            }
            try {
                certFs[i].generateCertPath(is2);
                TestCase.fail("generateCertificate must thrown CertificateException when input stream contains invalid datas");
            } catch (CertificateException e) {
            }
            Iterator<String> it = certFs[i].getCertPathEncodings();
            while (it.hasNext()) {
                String enc = it.next();
                try {
                    certFs[i].generateCertPath(is1, enc);
                    TestCase.fail("generateCertificate must thrown CertificateException or NullPointerException when input stream is null and encodings ".concat(enc));
                } catch (CertificateException e) {
                } catch (NullPointerException e) {
                }
                try {
                    certFs[i].generateCertPath(is2, enc);
                    TestCase.fail("generateCertificate must thrown CertificateException when input stream contains invalid datas  and encodings ".concat(enc));
                } catch (CertificateException e) {
                }
            } 
        }
    }

    /**
     * Test for <code>generateCertPath(InputStream inStream)</code>
     * <code>generateCertPath(InputStream inStream, String encoding)</code>
     * methods
     * Assertion: throw CertificateException when isStream contains invalid datas
     */
    // Test passed on RI
    public void testCertificateFactory13() throws IOException {
        if (!(CertificateFactory1Test.X509Support)) {
            TestCase.fail(CertificateFactory1Test.NotSupportMsg);
            return;
        }
        CertificateFactory[] certFs = CertificateFactory1Test.initCertFs();
        TestCase.assertNotNull("CertificateFactory objects were not created", certFs);
        byte[] enc = new byte[]{ ((byte) (0)), ((byte) (2)), ((byte) (3)), ((byte) (4)), ((byte) (5)) };
        MyCertPath mc = new MyCertPath(enc);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject(mc);
        oos.flush();
        oos.close();
        byte[] arr = os.toByteArray();
        ByteArrayInputStream is = new ByteArrayInputStream(arr);
        for (int i = 0; i < (certFs.length); i++) {
            try {
                certFs[i].generateCertPath(is);
                TestCase.fail("CertificateException must be thrown because input stream contains incorrect datas");
            } catch (CertificateException e) {
            }
            Iterator<String> it = certFs[i].getCertPathEncodings();
            while (it.hasNext()) {
                try {
                    certFs[i].generateCertPath(is, it.next());
                    TestCase.fail("CertificateException must be thrown because input stream contains incorrect datas");
                } catch (CertificateException e) {
                }
            } 
        }
    }

    /**
     * Test for <code>generateCertPath(List certificates)</code> method
     * Assertion: throw NullPointerException certificates is null
     */
    public void testCertificateFactory14() throws CertificateException {
        if (!(CertificateFactory1Test.X509Support)) {
            TestCase.fail(CertificateFactory1Test.NotSupportMsg);
            return;
        }
        CertificateFactory[] certFs = CertificateFactory1Test.initCertFs();
        TestCase.assertNotNull("CertificateFactory objects were not created", certFs);
        List<Certificate> list = null;
        for (int i = 0; i < (certFs.length); i++) {
            try {
                certFs[i].generateCertPath(list);
                TestCase.fail("generateCertificate must thrown CertificateException when list is null");
            } catch (NullPointerException e) {
            }
        }
    }

    /**
     * Test for <code>generateCertPath(List certificates)</code> method
     * Assertion: returns empty CertPath if certificates is empty
     */
    public void testCertificateFactory15() throws CertificateException {
        if (!(CertificateFactory1Test.X509Support)) {
            TestCase.fail(CertificateFactory1Test.NotSupportMsg);
            return;
        }
        CertificateFactory[] certFs = CertificateFactory1Test.initCertFs();
        TestCase.assertNotNull("CertificateFactory objects were not created", certFs);
        List<Certificate> list = new Vector<Certificate>();
        for (int i = 0; i < (certFs.length); i++) {
            CertPath cp = certFs[i].generateCertPath(list);
            List<? extends Certificate> list1 = cp.getCertificates();
            TestCase.assertTrue("List should be empty", list1.isEmpty());
        }
    }

    /**
     * Test for <code>generateCertPath(List certificates)</code> method
     * Assertion: throws CertificateException when certificates contains
     * incorrect Certificate
     */
    public void testCertificateFactory16() {
        if (!(CertificateFactory1Test.X509Support)) {
            TestCase.fail(CertificateFactory1Test.NotSupportMsg);
            return;
        }
        CertificateFactory[] certFs = CertificateFactory1Test.initCertFs();
        TestCase.assertNotNull("CertificateFactory objects were not created", certFs);
        MyCertificate ms = CertificateFactory1Test.createMC();
        List<Certificate> list = new Vector<Certificate>();
        list.add(ms);
        for (int i = 0; i < (certFs.length); i++) {
            try {
                certFs[i].generateCertPath(list);
                TestCase.fail("CertificateException must be thrown");
            } catch (CertificateException e) {
            }
        }
    }

    /**
     * Test for <code>CertificateFactory</code> constructor
     * Assertion: returns CertificateFactory object
     */
    public void testCertificateFactory17() throws CRLException {
        if (!(CertificateFactory1Test.X509Support)) {
            TestCase.fail(CertificateFactory1Test.NotSupportMsg);
            return;
        }
        CertificateFactorySpi spi = new MyCertificateFactorySpi();
        CertificateFactory cf = new myCertificateFactory(spi, CertificateFactory1Test.defaultProvider, CertificateFactory1Test.defaultType);
        TestCase.assertEquals("Incorrect type", cf.getType(), CertificateFactory1Test.defaultType);
        TestCase.assertEquals("Incorrect provider", cf.getProvider(), CertificateFactory1Test.defaultProvider);
        try {
            cf.generateCRLs(null);
            TestCase.fail("CRLException must be thrown");
        } catch (CRLException e) {
        }
        cf = new myCertificateFactory(null, null, null);
        TestCase.assertNull("Incorrect type", cf.getType());
        TestCase.assertNull("Incorrect provider", cf.getProvider());
        try {
            cf.generateCRLs(null);
            TestCase.fail("NullPointerException must be thrown");
        } catch (NullPointerException e) {
        }
    }

    /**
     * Test for <code>getType()</code> method
     */
    public void testCertificateFactory18() throws CertificateException {
        if (!(CertificateFactory1Test.X509Support)) {
            TestCase.fail(CertificateFactory1Test.NotSupportMsg);
            return;
        }
        for (int i = 0; i < (CertificateFactory1Test.validValues.length); i++) {
            try {
                CertificateFactory certF = CertificateFactory.getInstance(CertificateFactory1Test.validValues[i]);
                TestCase.assertEquals("Incorrect type: ", CertificateFactory1Test.validValues[i], certF.getType());
                certF = CertificateFactory.getInstance(CertificateFactory1Test.validValues[i], CertificateFactory1Test.defaultProviderName);
                TestCase.assertEquals("Incorrect type", certF.getType(), CertificateFactory1Test.validValues[i]);
                certF = CertificateFactory.getInstance(CertificateFactory1Test.validValues[i], CertificateFactory1Test.defaultProvider);
                TestCase.assertEquals("Incorrect provider", certF.getProvider(), CertificateFactory1Test.defaultProvider);
                TestCase.assertEquals("Incorrect type", certF.getType(), CertificateFactory1Test.validValues[i]);
            } catch (NoSuchProviderException e) {
                TestCase.fail(("Unexpected NoSuchProviderException " + (e.getMessage())));
            }
        }
    }
}

