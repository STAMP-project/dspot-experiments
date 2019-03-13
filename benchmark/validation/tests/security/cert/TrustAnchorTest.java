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
 * @author Vladimir N. Molotkov
 * @version $Revision$
 */
package tests.security.cert;


import java.io.ByteArrayInputStream;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import javax.security.auth.x500.X500Principal;
import junit.framework.TestCase;
import org.apache.harmony.security.tests.support.TestCertUtils;
import org.apache.harmony.security.tests.support.TestKeyPair;
import org.apache.harmony.security.tests.support.cert.TestUtils;


/**
 * Unit tests for <code>TrustAnchor</code>
 */
public class TrustAnchorTest extends TestCase {
    private static final String keyAlg = "DSA";

    // Sample of some valid CA name
    private static final String validCaNameRfc2253 = "CN=Test CA," + (((("OU=Testing Division," + "O=Test It All,") + "L=Test Town,") + "ST=Testifornia,") + "C=Testland");

    /**
     * Test #1 for <code>TrustAnchor(String, PublicKey, byte[])</code> constructor<br>
     * Assertion: creates <code>TrustAnchor</code> instance<br>
     * Test preconditions: valid parameters passed<br>
     * Expected: must pass without any exceptions
     *
     * @throws InvalidKeySpecException
     * 		
     */
    public final void testTrustAnchorStringPublicKeybyteArray01() throws Exception {
        PublicKey pk = new TestKeyPair(TrustAnchorTest.keyAlg).getPublic();
        // sub testcase 1
        new TrustAnchor(TrustAnchorTest.validCaNameRfc2253, pk, TrustAnchorTest.getFullEncoding());
        // sub testcase 2
        new TrustAnchor(TrustAnchorTest.validCaNameRfc2253, pk, TrustAnchorTest.getEncodingPSOnly());
        // sub testcase 3
        new TrustAnchor(TrustAnchorTest.validCaNameRfc2253, pk, TrustAnchorTest.getEncodingESOnly());
        // sub testcase 4
        new TrustAnchor(TrustAnchorTest.validCaNameRfc2253, pk, TrustAnchorTest.getEncodingNoMinMax());
    }

    /**
     * Test #2 for <code>TrustAnchor(String, PublicKey, byte[])</code> constructor<br>
     * Assertion: creates <code>TrustAnchor</code> instance<br>
     * Test preconditions: <code>null</code> as nameConstraints passed<br>
     * Expected: must pass without any exceptions
     *
     * @throws InvalidKeySpecException
     * 		
     */
    public final void testTrustAnchorStringPublicKeybyteArray02() throws Exception {
        PublicKey pk = new TestKeyPair(TrustAnchorTest.keyAlg).getPublic();
        new TrustAnchor(TrustAnchorTest.validCaNameRfc2253, pk, null);
    }

    /**
     * Test #3 for <code>TrustAnchor(String, PublicKey, byte[])</code> constructor<br>
     * Assertion: nameConstraints cloned by the constructor<br>
     * Test preconditions: modify passed nameConstraints<br>
     * Expected: modification must not change object internal state
     *
     * @throws InvalidKeySpecException
     * 		
     */
    public final void testTrustAnchorStringPublicKeybyteArray03() throws Exception {
        PublicKey pk = new TestKeyPair(TrustAnchorTest.keyAlg).getPublic();
        byte[] nc = TrustAnchorTest.getEncodingPSOnly();
        byte[] ncCopy = nc.clone();
        // sub testcase 5 - nameConstraints can be null
        TrustAnchor ta = new TrustAnchor(TrustAnchorTest.validCaNameRfc2253, pk, ncCopy);
        // modify
        ncCopy[0] = ((byte) (0));
        // check that above modification did not change
        // object internal state
        TestCase.assertTrue(Arrays.equals(nc, ta.getNameConstraints()));
    }

    /**
     * Test #4 for <code>TrustAnchor(String, PublicKey, byte[])</code> constructor<br>
     * Assertion: <code>NullPointerException</code> if <code>caName</code>
     * or <code>caPublicKey</code> parameter is <code>null</code><br>
     * Test preconditions: pass <code>null</code> as mentioned parameter<br>
     * Expected: NullPointerException
     */
    public final void testTrustAnchorStringPublicKeybyteArray04() throws Exception {
        PublicKey pk = new TestKeyPair(TrustAnchorTest.keyAlg).getPublic();
        // sub testcase 1: 'caName' param is null
        try {
            new TrustAnchor(((String) (null)), pk, TrustAnchorTest.getEncodingPSOnly());
            TestCase.fail("NullPointerException has not been thrown");
        } catch (NullPointerException ok) {
        }
        // sub testcase 2: 'caPublicKey' param is null
        try {
            new TrustAnchor(TrustAnchorTest.validCaNameRfc2253, null, TrustAnchorTest.getEncodingPSOnly());
            TestCase.fail("NullPointerException has not been thrown");
        } catch (NullPointerException ok) {
        }
        // sub testcase 3: 'caName' and 'caPublicKey' params are null
        try {
            new TrustAnchor(((String) (null)), null, TrustAnchorTest.getEncodingPSOnly());
            TestCase.fail("NullPointerException has not been thrown");
        } catch (NullPointerException ok) {
        }
        // sub testcase 4: 'caName' param is empty
        try {
            new TrustAnchor("", pk, TrustAnchorTest.getEncodingPSOnly());
            TestCase.fail("IllegalArgumentException has not been thrown");
        } catch (IllegalArgumentException ok) {
        }
        // sub testcase 5: 'caName' param is incorrect distinguished name
        try {
            new TrustAnchor("AID.11.12=A", pk, TrustAnchorTest.getEncodingPSOnly());
            TestCase.fail("IllegalArgumentException has not been thrown");
        } catch (IllegalArgumentException ok) {
        }
    }

    /**
     * Test #1 for <code>TrustAnchor(X500Principal, PublicKey, byte[])</code> constructor<br>
     * Assertion: creates <code>TrustAnchor</code> instance<br>
     * Test preconditions: valid parameters passed<br>
     * Expected: must pass without any exceptions
     *
     * @throws InvalidKeySpecException
     * 		
     */
    public final void testTrustAnchorX500PrincipalPublicKeybyteArray01() throws Exception {
        PublicKey pk = new TestKeyPair(TrustAnchorTest.keyAlg).getPublic();
        X500Principal x500p = new X500Principal(TrustAnchorTest.validCaNameRfc2253);
        // sub testcase 1
        new TrustAnchor(x500p, pk, TrustAnchorTest.getFullEncoding());
        // sub testcase 2
        new TrustAnchor(x500p, pk, TrustAnchorTest.getEncodingPSOnly());
        // sub testcase 3
        new TrustAnchor(x500p, pk, TrustAnchorTest.getEncodingESOnly());
        // sub testcase 4
        new TrustAnchor(x500p, pk, TrustAnchorTest.getEncodingNoMinMax());
    }

    /**
     * Test #2 for <code>TrustAnchor(X500Principal, PublicKey, byte[])</code> constructor<br>
     * Assertion: creates <code>TrustAnchor</code> instance<br>
     * Test preconditions: <code>null</code> as nameConstraints passed<br>
     * Expected: must pass without any exceptions
     *
     * @throws InvalidKeySpecException
     * 		
     */
    public final void testTrustAnchorX500PrincipalPublicKeybyteArray02() throws Exception {
        PublicKey pk = new TestKeyPair(TrustAnchorTest.keyAlg).getPublic();
        X500Principal x500p = new X500Principal(TrustAnchorTest.validCaNameRfc2253);
        new TrustAnchor(x500p, pk, null);
    }

    /**
     * Test #3 for <code>TrustAnchor(X500Principal, PublicKey, byte[])</code> constructor<br>
     * Assertion: nameConstraints cloned by the constructor<br>
     * Test preconditions: modify passed nameConstraints<br>
     * Expected: modification must not change object internal state
     *
     * @throws InvalidKeySpecException
     * 		
     */
    public final void testTrustAnchorX500PrincipalPublicKeybyteArray03() throws Exception {
        PublicKey pk = new TestKeyPair(TrustAnchorTest.keyAlg).getPublic();
        byte[] nc = TrustAnchorTest.getEncodingPSOnly();
        byte[] ncCopy = nc.clone();
        // sub testcase 5 - nameConstraints can be null
        TrustAnchor ta = new TrustAnchor(new X500Principal(TrustAnchorTest.validCaNameRfc2253), pk, ncCopy);
        // modify
        ncCopy[0] = ((byte) (0));
        // check that above modification did not change
        // object internal state
        TestCase.assertTrue(Arrays.equals(nc, ta.getNameConstraints()));
    }

    /**
     * Test #4 for <code>TrustAnchor(X500Principal, PublicKey, byte[])</code> constructor<br>
     * Assertion: <code>NullPointerException</code> if <code>caPrincipal</code>
     * or <code>caPublicKey</code> parameter is <code>null</code><br>
     * Test preconditions: pass <code>null</code> as mentioned parameter<br>
     * Expected: NullPointerException
     *
     * @throws InvalidKeySpecException
     * 		
     */
    public final void testTrustAnchorX500PrincipalPublicKeybyteArray04() throws Exception {
        PublicKey pk = new TestKeyPair(TrustAnchorTest.keyAlg).getPublic();
        X500Principal x500p = new X500Principal(TrustAnchorTest.validCaNameRfc2253);
        // sub testcase 1
        try {
            new TrustAnchor(((X500Principal) (null)), pk, TrustAnchorTest.getEncodingPSOnly());
            TestCase.fail("NullPointerException has not been thrown");
        } catch (NullPointerException ok) {
        }
        // sub testcase 2
        try {
            new TrustAnchor(x500p, null, TrustAnchorTest.getEncodingPSOnly());
            TestCase.fail("NullPointerException has not been thrown");
        } catch (NullPointerException ok) {
        }
        // sub testcase 3
        try {
            new TrustAnchor(((X500Principal) (null)), null, TrustAnchorTest.getEncodingPSOnly());
            TestCase.fail("NullPointerException has not been thrown");
        } catch (NullPointerException ok) {
        }
    }

    /**
     * Test #1 for <code>TrustAnchor(X509Certificate, byte[])</code>
     * constructor<br>
     * Assertion: creates <code>TrustAnchor</code> instance<br>
     * Test preconditions: valid parameters passed<br>
     * Expected: must pass without any exceptions
     */
    public final void testTrustAnchorX509CertificatebyteArray01() throws CertificateException {
        CertificateFactory certFact = CertificateFactory.getInstance("X509");
        X509Certificate pemCert = ((X509Certificate) (certFact.generateCertificate(new ByteArrayInputStream(TestUtils.getX509Certificate_v3()))));
        // sub testcase 1
        TrustAnchor ta1 = new TrustAnchor(pemCert, TrustAnchorTest.getFullEncoding());
        TestCase.assertNull(ta1.getCA());
        TestCase.assertNull(ta1.getCAName());
        TestCase.assertNull(ta1.getCAPublicKey());
        TestCase.assertTrue(Arrays.equals(TrustAnchorTest.getFullEncoding(), ta1.getNameConstraints()));
        TestCase.assertEquals(pemCert, ta1.getTrustedCert());
        // sub testcase 2
        TrustAnchor ta2 = new TrustAnchor(pemCert, TrustAnchorTest.getEncodingPSOnly());
        TestCase.assertNull(ta2.getCA());
        TestCase.assertNull(ta2.getCAName());
        TestCase.assertNull(ta2.getCAPublicKey());
        TestCase.assertTrue(Arrays.equals(TrustAnchorTest.getEncodingPSOnly(), ta2.getNameConstraints()));
        TestCase.assertEquals(pemCert, ta2.getTrustedCert());
        // sub testcase 3
        TrustAnchor ta3 = new TrustAnchor(pemCert, TrustAnchorTest.getEncodingESOnly());
        TestCase.assertNull(ta3.getCA());
        TestCase.assertNull(ta3.getCAName());
        TestCase.assertNull(ta3.getCAPublicKey());
        TestCase.assertTrue(Arrays.equals(TrustAnchorTest.getEncodingESOnly(), ta3.getNameConstraints()));
        TestCase.assertEquals(pemCert, ta3.getTrustedCert());
        // sub testcase 4
        TrustAnchor ta4 = new TrustAnchor(pemCert, TrustAnchorTest.getEncodingNoMinMax());
        TestCase.assertNull(ta4.getCA());
        TestCase.assertNull(ta4.getCAName());
        TestCase.assertNull(ta4.getCAPublicKey());
        TestCase.assertTrue(Arrays.equals(TrustAnchorTest.getEncodingNoMinMax(), ta4.getNameConstraints()));
        TestCase.assertEquals(pemCert, ta4.getTrustedCert());
    }

    /**
     * Test #2 for <code>TrustAnchor(X509Certificate, byte[])</code>
     * constructor<br>
     * Assertion: creates <code>TrustAnchor</code> instance<br>
     * Test preconditions: <code>null</code> as X509Certificate passed<br>
     * Expected: <code>NullPointerException</code>
     */
    public final void testTrustAnchorX509CertificatebyteArray02() throws Exception {
        try {
            new TrustAnchor(null, TrustAnchorTest.getFullEncoding());
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * Test #3 for <code>TrustAnchor(X509Certificate, byte[])</code>
     * constructor<br>
     * Assertion: creates <code>TrustAnchor</code> instance<br>
     * Test preconditions: <code>null</code> as nameConstraints passed<br>
     * Expected: must pass without any exceptions
     */
    public final void testTrustAnchorX509CertificatebyteArray03() throws Exception {
        CertificateFactory certFact = CertificateFactory.getInstance("X509");
        X509Certificate pemCert = ((X509Certificate) (certFact.generateCertificate(new ByteArrayInputStream(TestUtils.getX509Certificate_v3()))));
        try {
            new TrustAnchor(pemCert, null);
        } catch (Exception e) {
            TestCase.fail(("Unexpected exeption " + (e.getMessage())));
        }
    }

    /**
     * Test #4 for <code>TrustAnchor(X509Certificate, byte[])</code>
     * constructor<br>
     * Assertion: creates <code>TrustAnchor</code> instance<br>
     * Test preconditions: pass not valid name constraints array Expected:
     * IllegalArgumentException
     */
    public final void testTrustAnchorX509CertificatebyteArray04() throws Exception {
        CertificateFactory certFact = CertificateFactory.getInstance("X509");
        X509Certificate pemCert = ((X509Certificate) (certFact.generateCertificate(new ByteArrayInputStream(TestUtils.getX509Certificate_v3()))));
        try {
            new TrustAnchor(pemCert, new byte[]{ ((byte) (1)), ((byte) (2)), ((byte) (3)) });
            TestCase.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    /**
     * Test #5 for <code>TrustAnchor(X509Certificate, byte[])</code>
     * constructor<br>
     * Assertion: creates <code>TrustAnchor</code> instance<br>
     * Test preconditions: both parameters are passed as null<br>
     * Expected: <code>NullPointerException</code>
     */
    public final void testTrustAnchorX509CertificatebyteArray05() throws Exception {
        try {
            new TrustAnchor(null, null);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * Test #1 for <code>getCAPublicKey()</code> method<br>
     *
     * Assertion: returns most trusted CA public key</code><br>
     * Test preconditions: valid name passed to the constructor<br>
     * Expected: the same name must be returned by the method<br>
     */
    public final void testGetCAPublicKey01() throws Exception {
        PublicKey pk = new TestKeyPair(TrustAnchorTest.keyAlg).getPublic();
        // sub testcase 1
        TrustAnchor ta = new TrustAnchor(TrustAnchorTest.validCaNameRfc2253, pk, null);
        TestCase.assertEquals("equals1", pk, ta.getCAPublicKey());
        // sub testcase 2
        X500Principal x500p = new X500Principal(TrustAnchorTest.validCaNameRfc2253);
        ta = new TrustAnchor(x500p, pk, null);
        TestCase.assertEquals("equals2", pk, ta.getCAPublicKey());
    }

    /**
     * Test #1 for <code>getCAName()</code> method<br>
     *
     * Assertion: returns most trusted CA name as <code>String</code><br>
     * Test preconditions: valid name passed to the constructor<br>
     * Expected: the same name must be returned by the method<br>
     *
     * @throws InvalidKeySpecException
     * 		
     */
    public final void testGetCAName01() throws Exception {
        PublicKey pk = new TestKeyPair(TrustAnchorTest.keyAlg).getPublic();
        // sub testcase 1
        TrustAnchor ta = new TrustAnchor(TrustAnchorTest.validCaNameRfc2253, pk, null);
        TestCase.assertEquals("equals1", TrustAnchorTest.validCaNameRfc2253, ta.getCAName());
        // sub testcase 2
        X500Principal x500p = new X500Principal(TrustAnchorTest.validCaNameRfc2253);
        ta = new TrustAnchor(x500p, pk, null);
        TestCase.assertEquals("equals2", TrustAnchorTest.validCaNameRfc2253, ta.getCAName());
    }

    /**
     * Test #2 for <code>getCAName()</code> method<br>
     *
     * Assertion: returns ... <code>null</code> if <code>TrustAnchor</code>
     * was not specified as trusted certificate<br>
     * Test preconditions: test object is not specified as trusted certificate<br>
     * Expected: <code>null</code> as return value<br>
     *
     * @throws InvalidKeySpecException
     * 		
     */
    public final void testGetTrustedCer02() throws Exception {
        PublicKey pk = new TestKeyPair(TrustAnchorTest.keyAlg).getPublic();
        // sub testcase 1
        TrustAnchor ta = new TrustAnchor(TrustAnchorTest.validCaNameRfc2253, pk, null);
        TestCase.assertNull("null1", ta.getTrustedCert());
        // sub testcase 2
        X500Principal x500p = new X500Principal(TrustAnchorTest.validCaNameRfc2253);
        ta = new TrustAnchor(x500p, pk, null);
        TestCase.assertNull("null2", ta.getTrustedCert());
        X509Certificate cert = new TestCertUtils.TestX509Certificate(x500p, x500p);
        TrustAnchor ta2 = new TrustAnchor(cert, null);
        TestCase.assertSame(cert, ta2.getTrustedCert());
    }

    /**
     * Test #1 for <code>getNameConstraints()</code> method<br>
     *
     * Assertion: Returns the name constraints parameter.<br>
     * Test preconditions: valid parameters are passed to the constructors<br>
     * Expected: the valid parameters must be returned by the method<br>
     */
    public final void testGetNameConstraints01() throws Exception {
        PublicKey pk = new TestKeyPair(TrustAnchorTest.keyAlg).getPublic();
        TrustAnchor ta1 = new TrustAnchor(TrustAnchorTest.validCaNameRfc2253, pk, TrustAnchorTest.getFullEncoding());
        TestCase.assertTrue(Arrays.equals(TrustAnchorTest.getFullEncoding(), ta1.getNameConstraints()));
        X500Principal x500p = new X500Principal(TrustAnchorTest.validCaNameRfc2253);
        TrustAnchor ta2 = new TrustAnchor(x500p, pk, TrustAnchorTest.getEncodingNoMinMax());
        TestCase.assertTrue(Arrays.equals(TrustAnchorTest.getEncodingNoMinMax(), ta2.getNameConstraints()));
        CertificateFactory certFact = CertificateFactory.getInstance("X509");
        X509Certificate pemCert = ((X509Certificate) (certFact.generateCertificate(new ByteArrayInputStream(TestUtils.getX509Certificate_v3()))));
        TrustAnchor ta3 = new TrustAnchor(pemCert, TrustAnchorTest.getEncodingPSOnly());
        TestCase.assertTrue(Arrays.equals(TrustAnchorTest.getEncodingPSOnly(), ta3.getNameConstraints()));
    }

    /**
     * Test #2 for <code>getNameConstraints()</code> method<br>
     *
     * Assertion: Returns the name constraints parameter.<br>
     * Test preconditions: null parameters are passed to the constructors<br>
     * Expected: the null parameters must be returned by the method<br>
     */
    public final void testGetNameConstraints02() throws Exception {
        PublicKey pk = new TestKeyPair(TrustAnchorTest.keyAlg).getPublic();
        TrustAnchor ta1 = new TrustAnchor(TrustAnchorTest.validCaNameRfc2253, pk, null);
        TestCase.assertNull(ta1.getNameConstraints());
        X500Principal x500p = new X500Principal(TrustAnchorTest.validCaNameRfc2253);
        TrustAnchor ta2 = new TrustAnchor(x500p, pk, null);
        TestCase.assertNull(ta2.getNameConstraints());
        CertificateFactory certFact = CertificateFactory.getInstance("X509");
        X509Certificate pemCert = ((X509Certificate) (certFact.generateCertificate(new ByteArrayInputStream(TestUtils.getX509Certificate_v3()))));
        TrustAnchor ta3 = new TrustAnchor(pemCert, null);
        TestCase.assertNull(ta3.getNameConstraints());
    }

    /**
     * Test #1 for <code>toString()</code> method<br>
     *
     * Assertion: returns a formatted string describing the TrustAnchor<br>
     * Test preconditions: valid parameters are passed to the constructors<br>
     * Expected: not null string<br>
     */
    public final void testToString() throws Exception {
        PublicKey pk = new TestKeyPair(TrustAnchorTest.keyAlg).getPublic();
        TrustAnchor ta1 = new TrustAnchor(TrustAnchorTest.validCaNameRfc2253, pk, TrustAnchorTest.getFullEncoding());
        TestCase.assertNotNull(ta1.toString());
        X500Principal x500p = new X500Principal(TrustAnchorTest.validCaNameRfc2253);
        TrustAnchor ta2 = new TrustAnchor(x500p, pk, TrustAnchorTest.getEncodingNoMinMax());
        TestCase.assertNotNull(ta2.toString());
        CertificateFactory certFact = CertificateFactory.getInstance("X509");
        X509Certificate pemCert = ((X509Certificate) (certFact.generateCertificate(new ByteArrayInputStream(TestUtils.getX509Certificate_v3()))));
        TrustAnchor ta3 = new TrustAnchor(pemCert, TrustAnchorTest.getEncodingPSOnly());
        TestCase.assertNotNull(ta3.toString());
    }

    /**
     * Test #1 for <code>getCA()</code> method<br>
     *
     * Assertion: returns most trusted CA<br>
     * Test preconditions: valid CA or CA name passed to the constructor<br>
     * Expected: the same CA ot the CA with the same name must be returned
     * by the method<br>
     *
     * @throws InvalidKeySpecException
     * 		
     */
    public final void testGetCA01() throws Exception {
        PublicKey pk = new TestKeyPair(TrustAnchorTest.keyAlg).getPublic();
        // sub testcase 1
        TrustAnchor ta = new TrustAnchor(TrustAnchorTest.validCaNameRfc2253, pk, null);
        X500Principal ca = ta.getCA();
        TestCase.assertEquals("equals1", TrustAnchorTest.validCaNameRfc2253, ca.getName());
        // sub testcase 2
        X500Principal x500p = new X500Principal(TrustAnchorTest.validCaNameRfc2253);
        ta = new TrustAnchor(x500p, pk, null);
        TestCase.assertEquals("equals2", x500p, ta.getCA());
    }
}

