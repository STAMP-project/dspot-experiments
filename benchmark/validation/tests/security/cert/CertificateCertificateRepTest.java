package tests.security.cert;


import java.io.ObjectStreamException;
import java.security.cert.Certificate;
import java.util.Arrays;
import junit.framework.TestCase;
import org.apache.harmony.security.tests.support.cert.MyCertificate;
import org.apache.harmony.security.tests.support.cert.MyCertificate.MyCertificateRep;
import org.apache.harmony.security.tests.support.cert.TestUtils;


public class CertificateCertificateRepTest extends TestCase {
    private static final byte[] testEncoding = new byte[]{ ((byte) (1)), ((byte) (2)), ((byte) (3)), ((byte) (4)), ((byte) (5)) };

    /**
     * Test for
     * <code>Certificate.CertificateRep(String type, byte[] data)</code>
     * method<br>
     */
    public final void testCertificateCertificateRep() {
        MyCertificate c1 = new MyCertificate("TEST_TYPE", CertificateCertificateRepTest.testEncoding);
        MyCertificateRep rep = c1.new MyCertificateRep("TEST_TYPE", new byte[]{ ((byte) (1)), ((byte) (2)), ((byte) (3)) });
        TestCase.assertTrue(Arrays.equals(new byte[]{ ((byte) (1)), ((byte) (2)), ((byte) (3)) }, rep.getData()));
        TestCase.assertEquals("TEST_TYPE", rep.getType());
        try {
            c1.new MyCertificateRep(null, null);
        } catch (Exception e) {
            TestCase.fail(("Unexpected exeption " + (e.getMessage())));
        }
        try {
            MyCertificate.MyCertificateRep rep1 = c1.new MyCertificateRep("X509", TestUtils.getX509Certificate_v3());
            TestCase.assertEquals("X509", rep1.getType());
            TestCase.assertTrue(Arrays.equals(TestUtils.getX509Certificate_v3(), rep1.getData()));
        } catch (Exception e) {
            TestCase.fail(("Unexpected exeption " + (e.getMessage())));
        }
    }

    /**
     * Test for <code>readResolve()</code> method<br>
     */
    public final void testReadResolve() {
        MyCertificate c1 = new MyCertificate("TEST_TYPE", CertificateCertificateRepTest.testEncoding);
        MyCertificateRep rep = c1.new MyCertificateRep("TEST_TYPE", new byte[]{ ((byte) (1)), ((byte) (2)), ((byte) (3)) });
        try {
            rep.readResolve();
            TestCase.fail("ObjectStreamException expected");
        } catch (ObjectStreamException e) {
            // expected
        }
        MyCertificateRep rep1 = c1.new MyCertificateRep("X509", TestUtils.getX509Certificate_v3());
        try {
            Certificate obj = ((Certificate) (rep1.readResolve()));
            TestCase.assertEquals("0.3.5", obj.getPublicKey().getAlgorithm());
            TestCase.assertEquals("X.509", obj.getPublicKey().getFormat());
            TestCase.assertEquals("X.509", obj.getType());
        } catch (ObjectStreamException e) {
            TestCase.fail(("Unexpected ObjectStreamException " + (e.getMessage())));
        }
    }
}

