package redis.clients.jedis.tests;


import java.net.URI;
import java.security.InvalidAlgorithmParameterException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.exceptions.JedisConnectionException;


public class SSLJedisTest {
    /**
     * Tests opening a default SSL/TLS connection to redis using "rediss://" scheme url.
     */
    @Test
    public void connectWithUrl() {
        // The "rediss" scheme instructs jedis to open a SSL/TLS connection.
        Jedis jedis = new Jedis("rediss://localhost:6390");
        jedis.auth("foobared");
        Assert.assertEquals("PONG", jedis.ping());
        jedis.close();
    }

    /**
     * Tests opening a default SSL/TLS connection to redis.
     */
    @Test
    public void connectWithoutShardInfo() {
        // The "rediss" scheme instructs jedis to open a SSL/TLS connection.
        Jedis jedis = new Jedis(URI.create("rediss://localhost:6390"));
        jedis.auth("foobared");
        Assert.assertEquals("PONG", jedis.ping());
        jedis.close();
    }

    /**
     * Tests opening an SSL/TLS connection to redis.
     * NOTE: This test relies on a feature that is only available as of Java 7 and later.
     * It is commented out but not removed in case support for Java 6 is dropped or
     * we find a way to have the CI run a specific set of tests on Java 7 and above.
     */
    @Test
    public void connectWithShardInfo() throws Exception {
        final URI uri = URI.create("rediss://localhost:6390");
        final SSLSocketFactory sslSocketFactory = ((SSLSocketFactory) (SSLSocketFactory.getDefault()));
        // These SSL parameters ensure that we use the same hostname verifier used
        // for HTTPS.
        // Note: this options is only available in Java 7.
        final SSLParameters sslParameters = new SSLParameters();
        sslParameters.setEndpointIdentificationAlgorithm("HTTPS");
        JedisShardInfo shardInfo = new JedisShardInfo(uri, sslSocketFactory, sslParameters, null);
        shardInfo.setPassword("foobared");
        Jedis jedis = new Jedis(shardInfo);
        Assert.assertEquals("PONG", jedis.ping());
        jedis.disconnect();
        jedis.close();
    }

    /**
     * Tests opening an SSL/TLS connection to redis using the loopback address of
     * 127.0.0.1. This test should fail because "127.0.0.1" does not match the
     * certificate subject common name and there are no subject alternative names
     * in the certificate.
     *
     * NOTE: This test relies on a feature that is only available as of Java 7 and later.
     * It is commented out but not removed in case support for Java 6 is dropped or
     * we find a way to have the CI run a specific set of tests on Java 7 and above.
     */
    @Test
    public void connectWithShardInfoByIpAddress() throws Exception {
        final URI uri = URI.create("rediss://127.0.0.1:6390");
        final SSLSocketFactory sslSocketFactory = SSLJedisTest.createTrustStoreSslSocketFactory();
        // These SSL parameters ensure that we use the same hostname verifier used
        // for HTTPS.
        // Note: this options is only available in Java 7.
        final SSLParameters sslParameters = new SSLParameters();
        sslParameters.setEndpointIdentificationAlgorithm("HTTPS");
        JedisShardInfo shardInfo = new JedisShardInfo(uri, sslSocketFactory, sslParameters, null);
        shardInfo.setPassword("foobared");
        Jedis jedis = new Jedis(shardInfo);
        try {
            Assert.assertEquals("PONG", jedis.ping());
            Assert.fail("The code did not throw the expected JedisConnectionException.");
        } catch (JedisConnectionException e) {
            Assert.assertEquals("Unexpected first inner exception.", SSLHandshakeException.class, e.getCause().getClass());
            Assert.assertEquals("Unexpected second inner exception.", CertificateException.class, e.getCause().getCause().getClass());
        }
        try {
            jedis.close();
        } catch (Throwable e1) {
            // Expected.
        }
    }

    /**
     * Tests opening an SSL/TLS connection to redis with a custom hostname
     * verifier.
     */
    @Test
    public void connectWithShardInfoAndCustomHostnameVerifier() {
        final URI uri = URI.create("rediss://localhost:6390");
        final SSLSocketFactory sslSocketFactory = ((SSLSocketFactory) (SSLSocketFactory.getDefault()));
        final SSLParameters sslParameters = new SSLParameters();
        HostnameVerifier hostnameVerifier = new SSLJedisTest.BasicHostnameVerifier();
        JedisShardInfo shardInfo = new JedisShardInfo(uri, sslSocketFactory, sslParameters, hostnameVerifier);
        shardInfo.setPassword("foobared");
        Jedis jedis = new Jedis(shardInfo);
        Assert.assertEquals("PONG", jedis.ping());
        jedis.disconnect();
        jedis.close();
    }

    /**
     * Tests opening an SSL/TLS connection to redis with a custom socket factory.
     */
    @Test
    public void connectWithShardInfoAndCustomSocketFactory() throws Exception {
        final URI uri = URI.create("rediss://localhost:6390");
        final SSLSocketFactory sslSocketFactory = SSLJedisTest.createTrustStoreSslSocketFactory();
        final SSLParameters sslParameters = new SSLParameters();
        HostnameVerifier hostnameVerifier = new SSLJedisTest.BasicHostnameVerifier();
        JedisShardInfo shardInfo = new JedisShardInfo(uri, sslSocketFactory, sslParameters, hostnameVerifier);
        shardInfo.setPassword("foobared");
        Jedis jedis = new Jedis(shardInfo);
        Assert.assertEquals("PONG", jedis.ping());
        jedis.disconnect();
        jedis.close();
    }

    /**
     * Tests opening an SSL/TLS connection to redis with a custom hostname
     * verifier. This test should fail because "127.0.0.1" does not match the
     * certificate subject common name and there are no subject alternative names
     * in the certificate.
     */
    @Test
    public void connectWithShardInfoAndCustomHostnameVerifierByIpAddress() {
        final URI uri = URI.create("rediss://127.0.0.1:6390");
        final SSLSocketFactory sslSocketFactory = ((SSLSocketFactory) (SSLSocketFactory.getDefault()));
        final SSLParameters sslParameters = new SSLParameters();
        HostnameVerifier hostnameVerifier = new SSLJedisTest.BasicHostnameVerifier();
        JedisShardInfo shardInfo = new JedisShardInfo(uri, sslSocketFactory, sslParameters, hostnameVerifier);
        shardInfo.setPassword("foobared");
        Jedis jedis = new Jedis(shardInfo);
        try {
            Assert.assertEquals("PONG", jedis.ping());
            Assert.fail("The code did not throw the expected JedisConnectionException.");
        } catch (JedisConnectionException e) {
            Assert.assertEquals("The JedisConnectionException does not contain the expected message.", "The connection to '127.0.0.1' failed ssl/tls hostname verification.", e.getMessage());
        }
        try {
            jedis.close();
        } catch (Throwable e1) {
            // Expected.
        }
    }

    /**
     * Tests opening an SSL/TLS connection to redis with an empty certificate
     * trust store. This test should fail because there is no trust anchor for the
     * redis server certificate.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void connectWithShardInfoAndEmptyTrustStore() throws Exception {
        final URI uri = URI.create("rediss://localhost:6390");
        final SSLSocketFactory sslSocketFactory = SSLJedisTest.createTrustNoOneSslSocketFactory();
        JedisShardInfo shardInfo = new JedisShardInfo(uri, sslSocketFactory, null, null);
        shardInfo.setPassword("foobared");
        Jedis jedis = new Jedis(shardInfo);
        try {
            Assert.assertEquals("PONG", jedis.ping());
            Assert.fail("The code did not throw the expected JedisConnectionException.");
        } catch (JedisConnectionException e) {
            Assert.assertEquals("Unexpected first inner exception.", SSLException.class, e.getCause().getClass());
            Assert.assertEquals("Unexpected second inner exception.", RuntimeException.class, e.getCause().getCause().getClass());
            Assert.assertEquals("Unexpected third inner exception.", InvalidAlgorithmParameterException.class, e.getCause().getCause().getCause().getClass());
        }
        try {
            jedis.close();
        } catch (Throwable e1) {
            // Expected.
        }
    }

    /**
     * Very basic hostname verifier implementation for testing. NOT recommended
     * for production.
     */
    private static class BasicHostnameVerifier implements HostnameVerifier {
        private static final String COMMON_NAME_RDN_PREFIX = "CN=";

        @Override
        public boolean verify(String hostname, SSLSession session) {
            X509Certificate peerCertificate;
            try {
                peerCertificate = ((X509Certificate) (session.getPeerCertificates()[0]));
            } catch (SSLPeerUnverifiedException e) {
                throw new IllegalStateException("The session does not contain a peer X.509 certificate.");
            }
            String peerCertificateCN = getCommonName(peerCertificate);
            return hostname.equals(peerCertificateCN);
        }

        private String getCommonName(X509Certificate peerCertificate) {
            String subjectDN = peerCertificate.getSubjectDN().getName();
            String[] dnComponents = subjectDN.split(",");
            for (String dnComponent : dnComponents) {
                if (dnComponent.startsWith(SSLJedisTest.BasicHostnameVerifier.COMMON_NAME_RDN_PREFIX)) {
                    return dnComponent.substring(SSLJedisTest.BasicHostnameVerifier.COMMON_NAME_RDN_PREFIX.length());
                }
            }
            throw new IllegalArgumentException("The certificate has no common name.");
        }
    }
}

