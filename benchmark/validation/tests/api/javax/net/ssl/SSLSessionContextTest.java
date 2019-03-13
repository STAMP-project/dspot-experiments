package tests.api.javax.net.ssl;


import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSessionContext;
import junit.framework.TestCase;


/**
 * Tests for <code>SSLSessionContext</code> class constructors and methods.
 */
public class SSLSessionContextTest extends TestCase {
    /**
     *
     *
     * @throws NoSuchAlgorithmException
     * 		
     * @throws KeyManagementException
     * 		javax.net.ssl.SSLSessionContex#getSessionCacheSize()
     * 		javax.net.ssl.SSLSessionContex#setSessionCacheSize(int size)
     */
    public final void test_sessionCacheSize() throws KeyManagementException, NoSuchAlgorithmException {
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, null, null);
        SSLSessionContext sc = context.getClientSessionContext();
        sc.setSessionCacheSize(10);
        TestCase.assertEquals("10 wasn't returned", 10, sc.getSessionCacheSize());
        sc.setSessionCacheSize(5);
        TestCase.assertEquals("5 wasn't returned", 5, sc.getSessionCacheSize());
        try {
            sc.setSessionCacheSize((-1));
            TestCase.fail("IllegalArgumentException wasn't thrown");
        } catch (IllegalArgumentException iae) {
            // expected
        }
    }

    /**
     *
     *
     * @throws NoSuchAlgorithmException
     * 		
     * @throws KeyManagementException
     * 		javax.net.ssl.SSLSessionContex#getSessionTimeout()
     * 		javax.net.ssl.SSLSessionContex#setSessionTimeout(int seconds)
     */
    public final void test_sessionTimeout() throws KeyManagementException, NoSuchAlgorithmException {
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, null, null);
        SSLSessionContext sc = context.getClientSessionContext();
        sc.setSessionTimeout(100);
        TestCase.assertEquals("100 wasn't returned", 100, sc.getSessionTimeout());
        sc.setSessionTimeout(5000);
        TestCase.assertEquals("5000 wasn't returned", 5000, sc.getSessionTimeout());
        try {
            sc.setSessionTimeout((-1));
            TestCase.fail("IllegalArgumentException wasn't thrown");
        } catch (IllegalArgumentException iae) {
            // expected
        }
    }

    /**
     *
     *
     * @throws NoSuchAlgorithmException
     * 		
     * @throws KeyManagementException
     * 		javax.net.ssl.SSLSessionContex#getSession(byte[] sessionId)
     */
    public final void test_getSession() throws KeyManagementException, NoSuchAlgorithmException {
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, null, null);
        SSLSessionContext sc = context.getClientSessionContext();
        try {
            sc.getSession(null);
        } catch (NullPointerException e) {
            // expected
        }
        TestCase.assertNull(sc.getSession(new byte[5]));
    }

    /**
     *
     *
     * @throws NoSuchAlgorithmException
     * 		
     * @throws KeyManagementException
    javax.net.ssl.SSLSessionContex#getIds()
     * 		
     */
    public final void test_getIds() throws KeyManagementException, NoSuchAlgorithmException {
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, null, null);
        SSLSessionContext sc = context.getClientSessionContext();
        TestCase.assertFalse(sc.getIds().hasMoreElements());
    }
}

