package tests.api.javax.net.ssl;


import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import junit.framework.TestCase;
import org.apache.harmony.xnet.tests.support.X509TrustManagerImpl;


public class X509TrustManagerTest extends TestCase {
    public void test_checkClientTrusted_01() throws Exception {
        X509TrustManagerImpl xtm = new X509TrustManagerImpl();
        X509Certificate[] xcert = null;
        try {
            xtm.checkClientTrusted(xcert, "SSL");
            TestCase.fail("IllegalArgumentException wasn't thrown");
        } catch (IllegalArgumentException expected) {
        }
        xcert = new X509Certificate[0];
        try {
            xtm.checkClientTrusted(xcert, "SSL");
            TestCase.fail("IllegalArgumentException wasn't thrown");
        } catch (IllegalArgumentException expected) {
        }
        xcert = setX509Certificate();
        try {
            xtm.checkClientTrusted(xcert, null);
            TestCase.fail("IllegalArgumentException wasn't thrown");
        } catch (IllegalArgumentException expected) {
        }
        try {
            xtm.checkClientTrusted(xcert, "");
            TestCase.fail("IllegalArgumentException wasn't thrown");
        } catch (IllegalArgumentException expected) {
        }
    }

    /**
     * javax.net.ssl.X509TrustManager#checkClientTrusted(X509Certificate[] chain, String authType)
     */
    public void test_checkClientTrusted_02() throws Exception {
        X509TrustManagerImpl xtm = new X509TrustManagerImpl();
        X509Certificate[] xcert = setInvalid();
        try {
            xtm.checkClientTrusted(xcert, "SSL");
            TestCase.fail("CertificateException wasn't thrown");
        } catch (CertificateException expected) {
        }
    }

    /**
     * javax.net.ssl.X509TrustManager#checkClientTrusted(X509Certificate[] chain, String authType)
     */
    public void test_checkClientTrusted_03() throws Exception {
        X509TrustManagerImpl xtm = new X509TrustManagerImpl();
        X509Certificate[] xcert = setX509Certificate();
        xtm.checkClientTrusted(xcert, "SSL");
    }

    /**
     * javax.net.ssl.X509TrustManager#checkServerTrusted(X509Certificate[] chain, String authType)
     */
    public void test_checkServerTrusted_01() throws Exception {
        X509TrustManagerImpl xtm = new X509TrustManagerImpl();
        X509Certificate[] xcert = null;
        try {
            xtm.checkServerTrusted(xcert, "SSL");
            TestCase.fail("IllegalArgumentException wasn't thrown");
        } catch (IllegalArgumentException expected) {
        }
        xcert = new X509Certificate[0];
        try {
            xtm.checkServerTrusted(xcert, "SSL");
            TestCase.fail("IllegalArgumentException wasn't thrown");
        } catch (IllegalArgumentException expected) {
        }
        xcert = setX509Certificate();
        try {
            xtm.checkServerTrusted(xcert, null);
            TestCase.fail("IllegalArgumentException wasn't thrown");
        } catch (IllegalArgumentException expected) {
        }
        try {
            xtm.checkServerTrusted(xcert, "");
            TestCase.fail("IllegalArgumentException wasn't thrown");
        } catch (IllegalArgumentException expected) {
        }
    }

    /**
     * javax.net.ssl.X509TrustManager#checkServerTrusted(X509Certificate[] chain, String authType)
     */
    public void test_checkServerTrusted_02() throws Exception {
        X509TrustManagerImpl xtm = new X509TrustManagerImpl();
        X509Certificate[] xcert = setInvalid();
        try {
            xtm.checkServerTrusted(xcert, "SSL");
            TestCase.fail("CertificateException wasn't thrown");
        } catch (CertificateException expected) {
        }
    }

    /**
     * javax.net.ssl.X509TrustManager#checkServerTrusted(X509Certificate[] chain, String authType)
     */
    public void test_checkServerTrusted_03() throws Exception {
        X509TrustManagerImpl xtm = new X509TrustManagerImpl();
        X509Certificate[] xcert = setX509Certificate();
        xtm.checkServerTrusted(xcert, "SSL");
    }

    /**
     * javax.net.ssl.X509TrustManager#getAcceptedIssuers()
     */
    public void test_getAcceptedIssuers() throws Exception {
        X509TrustManagerImpl xtm = new X509TrustManagerImpl();
        TestCase.assertNotNull(xtm.getAcceptedIssuers());
    }
}

