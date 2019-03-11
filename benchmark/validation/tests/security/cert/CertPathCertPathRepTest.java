package tests.security.cert;


import java.io.ObjectStreamException;
import junit.framework.TestCase;
import org.apache.harmony.security.tests.support.cert.MyCertPath;
import org.apache.harmony.security.tests.support.cert.MyCertPath.MyCertPathRep;


public class CertPathCertPathRepTest extends TestCase {
    private static final byte[] testEncoding = new byte[]{ ((byte) (1)), ((byte) (2)), ((byte) (3)), ((byte) (4)), ((byte) (5)) };

    /**
     * Test for <code>CertPath.CertPathRep(String type, byte[] data)</code>
     * method<br>
     */
    public final void testCertPathCertPathRep() {
        MyCertPath cp = new MyCertPath(CertPathCertPathRepTest.testEncoding);
        MyCertPathRep rep = cp.new MyCertPathRep("MyEncoding", CertPathCertPathRepTest.testEncoding);
        TestCase.assertEquals(CertPathCertPathRepTest.testEncoding, rep.getData());
        TestCase.assertEquals("MyEncoding", rep.getType());
        try {
            cp.new MyCertPathRep(null, null);
        } catch (Exception e) {
            TestCase.fail(("Unexpected exeption " + (e.getMessage())));
        }
    }

    public final void testReadResolve() {
        MyCertPath cp = new MyCertPath(CertPathCertPathRepTest.testEncoding);
        MyCertPathRep rep = cp.new MyCertPathRep("MyEncoding", CertPathCertPathRepTest.testEncoding);
        try {
            Object obj = rep.readResolve();
            TestCase.fail("ObjectStreamException was not thrown.");
        } catch (ObjectStreamException e) {
            // expected
        }
        rep = cp.new MyCertPathRep("MyEncoding", new byte[]{ ((byte) (1)), ((byte) (2)), ((byte) (3)) });
        try {
            rep.readResolve();
            TestCase.fail("ObjectStreamException expected");
        } catch (ObjectStreamException e) {
            // expected
            System.out.println(e);
        }
    }
}

