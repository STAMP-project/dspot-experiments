package tests.security.cert;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import junit.framework.TestCase;
import org.apache.harmony.security.asn1.ASN1Integer;
import org.apache.harmony.security.asn1.ASN1OctetString;
import org.apache.harmony.security.tests.support.cert.TestUtils;


public class X509CRLSelector2Test extends TestCase {
    /**
     * constructor testing.
     */
    public void testX509CRLSelector() {
        X509CRLSelector selector = new X509CRLSelector();
        TestCase.assertNull(selector.getDateAndTime());
        TestCase.assertNull(selector.getCertificateChecking());
        TestCase.assertNull(selector.getIssuerNames());
        TestCase.assertNull(selector.getIssuers());
        TestCase.assertNull(selector.getMaxCRL());
        TestCase.assertNull(selector.getMinCRL());
    }

    /**
     * addIssuer(X500Principal issuer) method testing. Tests if CRLs with
     * specified issuers match the selector, and if not specified issuer does
     * not match the selector.
     */
    public void testAddIssuerLjavax_security_auth_x500_X500Principal02() {
        X509CRLSelector selector = new X509CRLSelector();
        X500Principal iss1 = new X500Principal("O=First Org.");
        X500Principal iss2 = new X500Principal("O=Second Org.");
        CRL crl1 = new X509CRLSelector2Test.TestCRL(iss1);
        CRL crl2 = new X509CRLSelector2Test.TestCRL(iss2);
        selector.addIssuer(iss1);
        TestCase.assertTrue("The CRL should match the selection criteria.", selector.match(crl1));
        TestCase.assertFalse("The CRL should not match the selection criteria.", selector.match(crl2));
        selector.addIssuer(iss2);
        TestCase.assertTrue("The CRL should match the selection criteria.", selector.match(crl2));
    }

    /**
     * addIssuerName(String name) method testing. Tests if CRLs with specified
     * issuers match the selector, and if not specified issuer does not match
     * the selector.
     */
    public void testAddIssuerNameLjava_lang_String03() {
        X509CRLSelector selector = new X509CRLSelector();
        String iss1 = "O=First Org.";
        String iss2 = "O=Second Org.";
        X509CRLSelector2Test.TestCRL crl1 = new X509CRLSelector2Test.TestCRL(new X500Principal(iss1));
        X509CRLSelector2Test.TestCRL crl2 = new X509CRLSelector2Test.TestCRL(new X500Principal(iss2));
        try {
            selector.addIssuerName(iss1);
        } catch (IOException e) {
            e.printStackTrace();
            TestCase.fail("Unexpected IOException was thrown.");
        }
        TestCase.assertTrue("The CRL should match the selection criteria.", selector.match(crl1));
        TestCase.assertFalse("The CRL should not match the selection criteria.", selector.match(crl2));
        try {
            selector.addIssuerName(iss2);
        } catch (IOException e) {
            e.printStackTrace();
            TestCase.fail("Unexpected IOException was thrown.");
        }
        TestCase.assertTrue("The CRL should match the selection criteria.", selector.match(crl2));
    }

    /**
     * setIssuers(Collection <X500Principal> issuers) method testing. Tests if
     * CRLs with any issuers match the selector in the case of null issuerNames
     * criteria, if specified issuers match the selector, and if not specified
     * issuer does not match the selector.
     */
    public void testSetIssuersLjava_util_Collection() {
        X509CRLSelector selector = new X509CRLSelector();
        X500Principal iss1 = new X500Principal("O=First Org.");
        X500Principal iss2 = new X500Principal("O=Second Org.");
        X500Principal iss3 = new X500Principal("O=Third Org.");
        X509CRLSelector2Test.TestCRL crl1 = new X509CRLSelector2Test.TestCRL(iss1);
        X509CRLSelector2Test.TestCRL crl2 = new X509CRLSelector2Test.TestCRL(iss2);
        X509CRLSelector2Test.TestCRL crl3 = new X509CRLSelector2Test.TestCRL(iss3);
        selector.setIssuers(null);
        TestCase.assertTrue("Any CRL issuers should match in the case of null issuers.", ((selector.match(crl1)) && (selector.match(crl2))));
        ArrayList<X500Principal> issuers = new ArrayList<X500Principal>(2);
        issuers.add(iss1);
        issuers.add(iss2);
        selector.setIssuers(issuers);
        TestCase.assertTrue("The CRL should match the selection criteria.", ((selector.match(crl1)) && (selector.match(crl2))));
        TestCase.assertFalse("The CRL should not match the selection criteria.", selector.match(crl3));
        issuers.add(iss3);
        TestCase.assertFalse(("The internal issuer collection is not protected " + "against the modifications."), selector.match(crl3));
    }

    /**
     * addIssuerName(byte[] name) method testing. Tests if CRLs with specified
     * issuers match the selector, and if not specified issuer does not match
     * the selector.
     */
    public void testAddIssuerName$B() {
        X509CRLSelector selector = new X509CRLSelector();
        byte[] iss1 = new byte[]{ // manually obtained DER encoding of "O=First Org." issuer name;
        48, 21, 49, 19, 48, 17, 6, 3, 85, 4, 10, 19, 10, 70, 105, 114, 115, 116, 32, 79, 114, 103, 46 };
        byte[] iss2 = new byte[]{ // manually obtained DER encoding of "O=Second Org." issuer name;
        48, 22, 49, 20, 48, 18, 6, 3, 85, 4, 10, 19, 11, 83, 101, 99, 111, 110, 100, 32, 79, 114, 103, 46 };
        X509CRLSelector2Test.TestCRL crl1 = new X509CRLSelector2Test.TestCRL(new X500Principal(iss1));
        X509CRLSelector2Test.TestCRL crl2 = new X509CRLSelector2Test.TestCRL(new X500Principal(iss2));
        try {
            selector.addIssuerName(iss1);
        } catch (IOException e) {
            e.printStackTrace();
            TestCase.fail("Unexpected IOException was thrown.");
        }
        TestCase.assertTrue("The CRL should match the selection criteria.", selector.match(crl1));
        TestCase.assertFalse("The CRL should not match the selection criteria.", selector.match(crl2));
        try {
            selector.addIssuerName(iss2);
        } catch (IOException e) {
            e.printStackTrace();
            TestCase.fail("Unexpected IOException was thrown.");
        }
        TestCase.assertTrue("The CRL should match the selection criteria.", selector.match(crl2));
    }

    /**
     * setDateAndTime(Date dateAndTime) method testing. Tests if CRLs with any
     * update dates match the selector in the case of null dateAndTime criteria,
     * if correct dates match and incorrect do not match the selector.
     */
    public void testSetDateAndTimeLjava_util_Date() {
        X509CRLSelector selector = new X509CRLSelector();
        X509CRLSelector2Test.TestCRL crl = new X509CRLSelector2Test.TestCRL(new Date(200), new Date(300));
        selector.setDateAndTime(null);
        TestCase.assertTrue("Any CRL should match in the case of null dateAndTime.", selector.match(crl));
        selector.setDateAndTime(new Date(200));
        TestCase.assertTrue("The CRL should match the selection criteria.", selector.match(crl));
        selector.setDateAndTime(new Date(250));
        TestCase.assertTrue("The CRL should match the selection criteria.", selector.match(crl));
        selector.setDateAndTime(new Date(300));
        TestCase.assertTrue("The CRL should match the selection criteria.", selector.match(crl));
        selector.setDateAndTime(new Date(150));
        TestCase.assertFalse("The CRL should not match the selection criteria.", selector.match(crl));
        selector.setDateAndTime(new Date(350));
        TestCase.assertFalse("The CRL should not match the selection criteria.", selector.match(crl));
    }

    /**
     * setCertificateChecking(X509Certificate) method testing.
     */
    public void testSetCertificateCheckingLjava_X509Certificate() throws CertificateException {
        X509CRLSelector selector = new X509CRLSelector();
        CertificateFactory certFact = CertificateFactory.getInstance("X509");
        X509Certificate cert = ((X509Certificate) (certFact.generateCertificate(new ByteArrayInputStream(TestUtils.getX509Certificate_v3()))));
        X509CRLSelector2Test.TestCRL crl = new X509CRLSelector2Test.TestCRL();
        selector.setCertificateChecking(cert);
        TestCase.assertTrue("The CRL should match the selection criteria.", selector.match(crl));
        TestCase.assertEquals(cert, selector.getCertificateChecking());
        selector.setCertificateChecking(null);
        TestCase.assertTrue("The CRL should match the selection criteria.", selector.match(crl));
        TestCase.assertNull(selector.getCertificateChecking());
    }

    /**
     * getIssuers() method testing. Tests if the method return null in the case
     * of not specified issuers, if the returned collection corresponds to the
     * specified issuers and this collection is unmodifiable.
     */
    public void testGetIssuers() {
        X509CRLSelector selector = new X509CRLSelector();
        X500Principal iss1 = new X500Principal("O=First Org.");
        X500Principal iss2 = new X500Principal("O=Second Org.");
        X500Principal iss3 = new X500Principal("O=Third Org.");
        TestCase.assertNull("The collection should be null.", selector.getIssuers());
        selector.addIssuer(iss1);
        selector.addIssuer(iss2);
        Collection<X500Principal> result = selector.getIssuers();
        try {
            result.add(iss3);
            TestCase.fail("The returned collection should be unmodifiable.");
        } catch (UnsupportedOperationException e) {
        }
        TestCase.assertTrue("The collection should contain the specified DN.", result.contains(iss2));
    }

    /**
     * getIssuerNames() method testing. Tests if the method return null in the
     * case of not specified issuers, if the returned collection corresponds to
     * the specified issuers.
     */
    public void testGetIssuerNames() {
        X509CRLSelector selector = new X509CRLSelector();
        byte[] iss1 = new byte[]{ // manually obtained DER encoding of "O=First Org." issuer name;
        48, 21, 49, 19, 48, 17, 6, 3, 85, 4, 10, 19, 10, 70, 105, 114, 115, 116, 32, 79, 114, 103, 46 };
        byte[] iss2 = new byte[]{ // manually obtained DER encoding of "O=Second Org." issuer name;
        48, 22, 49, 20, 48, 18, 6, 3, 85, 4, 10, 19, 11, 83, 101, 99, 111, 110, 100, 32, 79, 114, 103, 46 };
        TestCase.assertNull("The collection should be null.", selector.getIssuerNames());
        try {
            selector.addIssuerName(iss1);
            selector.addIssuerName(iss2);
        } catch (IOException e) {
            e.printStackTrace();
            TestCase.fail("Unexpected IOException was thrown.");
        }
        Collection<Object> result = selector.getIssuerNames();
        TestCase.assertEquals("The collection should contain all of the specified DNs.", 2, result.size());
    }

    /**
     * getMinCRL() method testing. Tests if the method return null in the case
     * of not specified minCRL criteria, and if the returned value corresponds
     * to the specified one.
     */
    public void testGetMinCRL() {
        X509CRLSelector selector = new X509CRLSelector();
        TestCase.assertNull("Initially the minCRL should be null.", selector.getMinCRL());
        BigInteger minCRL = new BigInteger("10000");
        selector.setMinCRLNumber(minCRL);
        TestCase.assertTrue("The result should be equal to specified.", minCRL.equals(selector.getMinCRL()));
    }

    /**
     * getMaxCRL() method testing. Tests if the method return null in the case
     * of not specified maxCRL criteria, and if the returned value corresponds
     * to the specified one.
     */
    public void testGetMaxCRL() {
        X509CRLSelector selector = new X509CRLSelector();
        TestCase.assertNull("Initially the maxCRL should be null.", selector.getMaxCRL());
        BigInteger maxCRL = new BigInteger("10000");
        selector.setMaxCRLNumber(maxCRL);
        TestCase.assertTrue("The result should be equal to specified.", maxCRL.equals(selector.getMaxCRL()));
    }

    /**
     * getDateAndTime() method testing. Tests if the method return null in the
     * case of not specified dateAndTime criteria, and if the returned value
     * corresponds to the specified one.
     */
    public void testGetDateAndTime() {
        X509CRLSelector selector = new X509CRLSelector();
        TestCase.assertNull("Initially the dateAndTime criteria should be null.", selector.getDateAndTime());
        Date date = new Date(200);
        selector.setDateAndTime(date);
        TestCase.assertTrue("The result should be equal to specified.", date.equals(selector.getDateAndTime()));
    }

    /**
     * getCertificateChecking() method testing.
     */
    public void testGetCertificateCheckingLjava_X509Certificate() throws CertificateException {
        X509CRLSelector selector = new X509CRLSelector();
        CertificateFactory certFact = CertificateFactory.getInstance("X509");
        X509Certificate cert = ((X509Certificate) (certFact.generateCertificate(new ByteArrayInputStream(TestUtils.getX509Certificate_v3()))));
        selector.setCertificateChecking(cert);
        TestCase.assertEquals(cert, selector.getCertificateChecking());
        selector.setCertificateChecking(null);
        TestCase.assertNull(selector.getCertificateChecking());
    }

    /**
     * match(CRL crl) method testing. Tests if the null object matches to the
     * selector or not.
     */
    public void testMatchLjava_security_cert_X509CRL() {
        X509CRLSelector selector = new X509CRLSelector();
        TestCase.assertFalse("The null object should not match", selector.match(((X509CRL) (null))));
    }

    public void testToString() {
        X509CRLSelector selector = new X509CRLSelector();
        X500Principal iss1 = new X500Principal("O=First Org.");
        X500Principal iss2 = new X500Principal("O=Second Org.");
        BigInteger minCRL = new BigInteger("10000");
        BigInteger maxCRL = new BigInteger("10000");
        Date date = new Date(200);
        selector.addIssuer(iss1);
        selector.addIssuer(iss2);
        selector.setMinCRLNumber(minCRL);
        selector.setMaxCRLNumber(maxCRL);
        selector.setDateAndTime(date);
        TestCase.assertNotNull("The result should not be null.", selector.toString());
    }

    /**
     * The abstract class stub implementation.
     */
    private class TestCRL extends X509CRL {
        private X500Principal principal = null;

        private BigInteger crlNumber = null;

        private Date thisUpdate = null;

        private Date nextUpdate = null;

        public TestCRL() {
        }

        public TestCRL(X500Principal principal) {
            this.principal = principal;
        }

        public TestCRL(Date thisUpdate, Date nextUpdate) {
            setUpdateDates(thisUpdate, nextUpdate);
        }

        public TestCRL(BigInteger crlNumber) {
            setCrlNumber(crlNumber);
        }

        public void setUpdateDates(Date thisUpdate, Date nextUpdate) {
            this.thisUpdate = thisUpdate;
            this.nextUpdate = nextUpdate;
        }

        public void setCrlNumber(BigInteger crlNumber) {
            this.crlNumber = crlNumber;
        }

        public X500Principal getIssuerX500Principal() {
            return principal;
        }

        public String toString() {
            return null;
        }

        public boolean isRevoked(Certificate cert) {
            return true;
        }

        public Set<String> getNonCriticalExtensionOIDs() {
            return null;
        }

        public Set<String> getCriticalExtensionOIDs() {
            return null;
        }

        public byte[] getExtensionValue(String oid) {
            if (("2.5.29.20".equals(oid)) && ((crlNumber) != null)) {
                return ASN1OctetString.getInstance().encode(ASN1Integer.getInstance().encode(crlNumber.toByteArray()));
            }
            return null;
        }

        public boolean hasUnsupportedCriticalExtension() {
            return false;
        }

        public byte[] getEncoded() {
            return null;
        }

        @SuppressWarnings("unused")
        public void verify(PublicKey key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException, CRLException {
        }

        @SuppressWarnings("unused")
        public void verify(PublicKey key, String sigProvider) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException, CRLException {
        }

        public int getVersion() {
            return 2;
        }

        public Principal getIssuerDN() {
            return null;
        }

        public Date getThisUpdate() {
            return thisUpdate;
        }

        public Date getNextUpdate() {
            return nextUpdate;
        }

        public X509CRLEntry getRevokedCertificate(BigInteger serialNumber) {
            return null;
        }

        public Set<X509CRLEntry> getRevokedCertificates() {
            return null;
        }

        public byte[] getTBSCertList() {
            return null;
        }

        public byte[] getSignature() {
            return null;
        }

        public String getSigAlgName() {
            return null;
        }

        public String getSigAlgOID() {
            return null;
        }

        public byte[] getSigAlgParams() {
            return null;
        }
    }
}

