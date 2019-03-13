package water.network;


import SecurityUtils.SSLCredentials;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;


public class SecurityUtilsTest {
    @Test
    public void shouldGenerateKeystoreAndConfig() throws Exception {
        try {
            SecurityUtils.SSLCredentials testKeystore = SecurityUtils.generateSSLPair("test123", "h2o-keystore-test.jks", "");
            String configPath = SecurityUtils.generateSSLConfig(testKeystore, "test-ssl.properties");
            Assert.assertTrue(new File(testKeystore.jks.getLocation()).exists());
            Properties sslConfig = new Properties();
            sslConfig.load(new FileInputStream(configPath));
            Assert.assertEquals(SecurityUtils.defaultTLSVersion(), sslConfig.getProperty("h2o_ssl_protocol"));
            Assert.assertEquals("h2o-keystore-test.jks", sslConfig.getProperty("h2o_ssl_jks_internal"));
            Assert.assertEquals("test123", sslConfig.getProperty("h2o_ssl_jks_password"));
            Assert.assertEquals("h2o-keystore-test.jks", sslConfig.getProperty("h2o_ssl_jts"));
            Assert.assertEquals("test123", sslConfig.getProperty("h2o_ssl_jts_password"));
        } finally {
            File keystore = new File("h2o-keystore-test.jks");
            if (keystore.exists()) {
                keystore.deleteOnExit();
            }
            File props = new File("test-ssl.properties");
            if (props.exists()) {
                props.deleteOnExit();
            }
        }
    }
}

