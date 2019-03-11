package org.mockserver.configuration;


import KeyStoreFactory.CERTIFICATE_DOMAIN;
import KeyStoreFactory.KEY_STORE_PASSWORD;
import io.netty.util.NettyRuntime;
import io.netty.util.internal.SystemPropertyUtil;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockserver.socket.tls.KeyStoreFactory;
import org.slf4j.event.Level;


/**
 *
 *
 * @author jamesdbloom
 */
public class ConfigurationPropertiesTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    String propertiesBeforeTest;

    @Test
    public void shouldSetAndReadEnableCORSSettingForAPI() {
        // given
        System.clearProperty("mockserver.enableCORSForAPI");
        // when
        Assert.assertEquals(true, ConfigurationProperties.enableCORSForAPI());
        ConfigurationProperties.enableCORSForAPI(false);
        // then
        Assert.assertEquals(false, ConfigurationProperties.enableCORSForAPI());
        Assert.assertEquals("false", System.getProperty("mockserver.enableCORSForAPI"));
    }

    @Test
    public void shouldDetectEnableCORSSettingForAPIHasBeenExplicitlySet() {
        // given
        System.clearProperty("mockserver.enableCORSForAPI");
        // when
        Assert.assertEquals(false, ConfigurationProperties.enableCORSForAPIHasBeenSetExplicitly());
        ConfigurationProperties.enableCORSForAPI(true);
        Assert.assertEquals(true, ConfigurationProperties.enableCORSForAPIHasBeenSetExplicitly());
        // given
        System.clearProperty("mockserver.enableCORSForAPI");
        // when
        Assert.assertEquals(false, ConfigurationProperties.enableCORSForAPIHasBeenSetExplicitly());
        System.setProperty("mockserver.enableCORSForAPI", ("" + true));
        Assert.assertEquals(true, ConfigurationProperties.enableCORSForAPIHasBeenSetExplicitly());
    }

    @Test
    public void shouldSetAndReadEnableCORSSettingForAllResponses() {
        // given
        System.clearProperty("mockserver.enableCORSForAllResponses");
        // when
        Assert.assertEquals(false, ConfigurationProperties.enableCORSForAllResponses());
        ConfigurationProperties.enableCORSForAllResponses(false);
        // then
        Assert.assertEquals(false, ConfigurationProperties.enableCORSForAllResponses());
        Assert.assertEquals("false", System.getProperty("mockserver.enableCORSForAllResponses"));
    }

    @Test
    public void shouldSetAndReadNIOEventLoopThreadCount() {
        // given
        System.clearProperty("mockserver.nioEventLoopThreadCount");
        int eventLoopCount = Math.max(1, SystemPropertyUtil.getInt("io.netty.eventLoopThreads", ((NettyRuntime.availableProcessors()) * 5)));
        // when
        Assert.assertEquals(eventLoopCount, ConfigurationProperties.nioEventLoopThreadCount());
        ConfigurationProperties.nioEventLoopThreadCount(2);
        // then
        Assert.assertEquals(2, ConfigurationProperties.nioEventLoopThreadCount());
    }

    @Test
    public void shouldHandleInvalidNIOEventLoopThreadCount() {
        // given
        System.setProperty("mockserver.nioEventLoopThreadCount", "invalid");
        int eventLoopCount = Math.max(1, SystemPropertyUtil.getInt("io.netty.eventLoopThreads", ((NettyRuntime.availableProcessors()) * 5)));
        // then
        Assert.assertEquals(eventLoopCount, ConfigurationProperties.nioEventLoopThreadCount());
    }

    @Test
    public void shouldSetAndReadMaxExpectations() {
        // given
        System.clearProperty("mockserver.maxExpectations");
        // when
        Assert.assertEquals(1000, ConfigurationProperties.maxExpectations());
        ConfigurationProperties.maxExpectations(100);
        // then
        Assert.assertEquals(100, ConfigurationProperties.maxExpectations());
    }

    @Test
    public void shouldHandleInvalidMaxExpectations() {
        // given
        System.setProperty("mockserver.maxExpectations", "invalid");
        // then
        Assert.assertEquals(1000, ConfigurationProperties.maxExpectations());
    }

    @Test
    public void shouldSetAndReadMaxWebSocketExpectations() {
        // given
        System.clearProperty("mockserver.maxWebSocketExpectations");
        // when
        Assert.assertEquals(1000, ConfigurationProperties.maxWebSocketExpectations());
        ConfigurationProperties.maxWebSocketExpectations(100);
        // then
        Assert.assertEquals(100, ConfigurationProperties.maxWebSocketExpectations());
    }

    @Test
    public void shouldHandleInvalidMaxWebSocketExpectations() {
        // given
        System.setProperty("mockserver.maxWebSocketExpectations", "invalid");
        // then
        Assert.assertEquals(1000, ConfigurationProperties.maxWebSocketExpectations());
    }

    @Test
    public void shouldSetAndReadMaxInitialLineLength() {
        // given
        System.clearProperty("mockserver.maxInitialLineLength");
        // when
        Assert.assertEquals(4096, ConfigurationProperties.maxInitialLineLength());
        ConfigurationProperties.maxInitialLineLength(100);
        // then
        Assert.assertEquals(100, ConfigurationProperties.maxInitialLineLength());
    }

    @Test
    public void shouldHandleInvalidMaxInitialLineLength() {
        // given
        System.setProperty("mockserver.maxInitialLineLength", "invalid");
        // then
        Assert.assertEquals(4096, ConfigurationProperties.maxInitialLineLength());
    }

    @Test
    public void shouldSetAndReadMaxHeaderSize() {
        // given
        System.clearProperty("mockserver.maxHeaderSize");
        // when
        Assert.assertEquals(8192, ConfigurationProperties.maxHeaderSize());
        ConfigurationProperties.maxHeaderSize(100);
        // then
        Assert.assertEquals(100, ConfigurationProperties.maxHeaderSize());
    }

    @Test
    public void shouldHandleInvalidMaxHeaderSize() {
        // given
        System.setProperty("mockserver.maxHeaderSize", "invalid");
        // then
        Assert.assertEquals(8192, ConfigurationProperties.maxHeaderSize());
    }

    @Test
    public void shouldSetAndReadMaxChunkSize() {
        // given
        System.clearProperty("mockserver.maxChunkSize");
        // when
        Assert.assertEquals(8192, ConfigurationProperties.maxChunkSize());
        ConfigurationProperties.maxChunkSize(100);
        // then
        Assert.assertEquals(100, ConfigurationProperties.maxChunkSize());
    }

    @Test
    public void shouldHandleInvalidMaxChunkSize() {
        // given
        System.setProperty("mockserver.maxChunkSize", "invalid");
        // then
        Assert.assertEquals(8192, ConfigurationProperties.maxChunkSize());
    }

    @Test
    public void shouldSetAndReadMaxSocketTimeout() {
        // given
        System.clearProperty("mockserver.maxSocketTimeout");
        // when
        Assert.assertEquals(TimeUnit.SECONDS.toMillis(20), ConfigurationProperties.maxSocketTimeout());
        ConfigurationProperties.maxSocketTimeout(100);
        // then
        Assert.assertEquals(100, ConfigurationProperties.maxSocketTimeout());
    }

    @Test
    public void shouldHandleInvalidMaxSocketTimeout() {
        // given
        System.setProperty("mockserver.maxSocketTimeout", "invalid");
        // then
        Assert.assertEquals(TimeUnit.SECONDS.toMillis(20), ConfigurationProperties.maxSocketTimeout());
    }

    @Test
    public void shouldSetAndReadSocketConnectionTimeout() {
        // given
        System.clearProperty("mockserver.socketConnectionTimeout");
        // when
        Assert.assertEquals(TimeUnit.SECONDS.toMillis(20), ConfigurationProperties.socketConnectionTimeout());
        ConfigurationProperties.socketConnectionTimeout(100);
        // then
        Assert.assertEquals(100, ConfigurationProperties.socketConnectionTimeout());
    }

    @Test
    public void shouldHandleInvalidSocketConnectionTimeout() {
        // given
        System.setProperty("mockserver.socketConnectionTimeout", "invalid");
        // then
        Assert.assertEquals(TimeUnit.SECONDS.toMillis(20), ConfigurationProperties.socketConnectionTimeout());
    }

    @Test
    public void shouldSetAndReadJavaKeyStoreFilePath() {
        // given
        System.clearProperty("mockserver.javaKeyStoreFilePath");
        // when
        Assert.assertEquals(KeyStoreFactory.defaultKeyStoreFileName(), ConfigurationProperties.javaKeyStoreFilePath());
        ConfigurationProperties.javaKeyStoreFilePath("newKeyStoreFile.jks");
        // then
        Assert.assertEquals("newKeyStoreFile.jks", ConfigurationProperties.javaKeyStoreFilePath());
        Assert.assertEquals("newKeyStoreFile.jks", System.getProperty("mockserver.javaKeyStoreFilePath"));
        Assert.assertEquals(true, ConfigurationProperties.rebuildKeyStore());
    }

    @Test
    public void shouldSetAndReadJavaKeyStorePassword() {
        // given
        System.clearProperty("mockserver.javaKeyStorePassword");
        // when
        Assert.assertEquals(KEY_STORE_PASSWORD, ConfigurationProperties.javaKeyStorePassword());
        ConfigurationProperties.javaKeyStorePassword("newPassword");
        // then
        Assert.assertEquals("newPassword", ConfigurationProperties.javaKeyStorePassword());
        Assert.assertEquals("newPassword", System.getProperty("mockserver.javaKeyStorePassword"));
        Assert.assertEquals(true, ConfigurationProperties.rebuildKeyStore());
    }

    @Test
    public void shouldSetAndReadJavaKeyStoreType() {
        // given
        System.clearProperty("mockserver.javaKeyStoreType");
        // when
        Assert.assertEquals(KeyStore.getDefaultType(), ConfigurationProperties.javaKeyStoreType());
        ConfigurationProperties.javaKeyStoreType("PKCS11");
        // then
        Assert.assertEquals("PKCS11", ConfigurationProperties.javaKeyStoreType());
        Assert.assertEquals("PKCS11", System.getProperty("mockserver.javaKeyStoreType"));
        Assert.assertEquals(true, ConfigurationProperties.rebuildKeyStore());
    }

    @Test
    public void shouldSetAndReadDeleteGeneratedKeyStoreOnExit() {
        // given
        System.clearProperty("mockserver.deleteGeneratedKeyStoreOnExit");
        // when
        Assert.assertEquals(true, ConfigurationProperties.deleteGeneratedKeyStoreOnExit());
        ConfigurationProperties.deleteGeneratedKeyStoreOnExit(false);
        // then
        Assert.assertEquals(false, ConfigurationProperties.deleteGeneratedKeyStoreOnExit());
        Assert.assertEquals("false", System.getProperty("mockserver.deleteGeneratedKeyStoreOnExit"));
    }

    @Test
    public void shouldSetAndReadSslCertificateDomainName() {
        // given
        System.clearProperty("mockserver.sslCertificateDomainName");
        // when
        Assert.assertEquals(CERTIFICATE_DOMAIN, ConfigurationProperties.sslCertificateDomainName());
        ConfigurationProperties.sslCertificateDomainName("newDomain");
        // then
        Assert.assertEquals("newDomain", ConfigurationProperties.sslCertificateDomainName());
        Assert.assertEquals("newDomain", System.getProperty("mockserver.sslCertificateDomainName"));
        Assert.assertEquals(true, ConfigurationProperties.rebuildKeyStore());
    }

    @Test
    public void shouldSetAndReadSslSubjectAlternativeNameDomains() {
        // given
        ConfigurationProperties.clearSslSubjectAlternativeNameDomains();
        // when
        Assert.assertThat(Arrays.asList(ConfigurationProperties.sslSubjectAlternativeNameDomains()), empty());
        ConfigurationProperties.addSslSubjectAlternativeNameDomains("a", "b", "c", "d");
        // then
        Assert.assertThat(Arrays.asList(ConfigurationProperties.sslSubjectAlternativeNameDomains()), containsInAnyOrder("a", "b", "c", "d"));
        Assert.assertEquals("a,b,c,d", System.getProperty("mockserver.sslSubjectAlternativeNameDomains"));
        Assert.assertEquals(true, ConfigurationProperties.rebuildKeyStore());
    }

    @Test
    public void shouldAddSslSubjectAlternativeNameDomains() {
        // given
        ConfigurationProperties.clearSslSubjectAlternativeNameDomains();
        ConfigurationProperties.rebuildKeyStore(false);
        // when
        Assert.assertThat(Arrays.asList(ConfigurationProperties.sslSubjectAlternativeNameDomains()), empty());
        ConfigurationProperties.addSslSubjectAlternativeNameDomains("a", "b", "c", "d");
        // then
        Assert.assertThat(Arrays.asList(ConfigurationProperties.sslSubjectAlternativeNameDomains()), containsInAnyOrder("a", "b", "c", "d"));
        Assert.assertEquals("a,b,c,d", System.getProperty("mockserver.sslSubjectAlternativeNameDomains"));
        // when
        ConfigurationProperties.addSslSubjectAlternativeNameDomains("e", "f", "g");
        // then - add subject alternative domain names
        Assert.assertThat(Arrays.asList(ConfigurationProperties.sslSubjectAlternativeNameDomains()), containsInAnyOrder("a", "b", "c", "d", "e", "f", "g"));
        Assert.assertEquals("a,b,c,d,e,f,g", System.getProperty("mockserver.sslSubjectAlternativeNameDomains"));
        Assert.assertEquals(true, ConfigurationProperties.rebuildKeyStore());
        // given
        ConfigurationProperties.rebuildKeyStore(false);
        // when
        ConfigurationProperties.addSslSubjectAlternativeNameDomains("e", "f", "g");
        // then - do not add duplicate subject alternative domain names
        Assert.assertThat(Arrays.asList(ConfigurationProperties.sslSubjectAlternativeNameDomains()), containsInAnyOrder("a", "b", "c", "d", "e", "f", "g"));
        Assert.assertEquals("a,b,c,d,e,f,g", System.getProperty("mockserver.sslSubjectAlternativeNameDomains"));
        Assert.assertEquals(false, ConfigurationProperties.rebuildKeyStore());
    }

    @Test
    public void shouldSetAndReadSslSubjectAlternativeNameIps() {
        // given
        ConfigurationProperties.clearSslSubjectAlternativeNameIps();
        // when
        Assert.assertThat(Arrays.asList(ConfigurationProperties.sslSubjectAlternativeNameIps()), containsInAnyOrder("127.0.0.1", "0.0.0.0"));
        ConfigurationProperties.addSslSubjectAlternativeNameIps("1", "2", "3", "4");
        // then
        Assert.assertThat(Arrays.asList(ConfigurationProperties.sslSubjectAlternativeNameIps()), containsInAnyOrder("0.0.0.0", "1", "127.0.0.1", "2", "3", "4"));
        Assert.assertEquals("0.0.0.0,1,127.0.0.1,2,3,4", System.getProperty("mockserver.sslSubjectAlternativeNameIps"));
        Assert.assertEquals(true, ConfigurationProperties.rebuildKeyStore());
    }

    @Test
    public void shouldAddSslSubjectAlternativeNameIps() {
        // given
        ConfigurationProperties.clearSslSubjectAlternativeNameIps();
        ConfigurationProperties.rebuildKeyStore(false);
        // when
        Assert.assertThat(Arrays.asList(ConfigurationProperties.sslSubjectAlternativeNameIps()), containsInAnyOrder("127.0.0.1", "0.0.0.0"));
        ConfigurationProperties.addSslSubjectAlternativeNameIps("1", "2", "3", "4");
        // then
        Assert.assertThat(Arrays.asList(ConfigurationProperties.sslSubjectAlternativeNameIps()), containsInAnyOrder("0.0.0.0", "1", "127.0.0.1", "2", "3", "4"));
        Assert.assertEquals("0.0.0.0,1,127.0.0.1,2,3,4", System.getProperty("mockserver.sslSubjectAlternativeNameIps"));
        // when
        ConfigurationProperties.addSslSubjectAlternativeNameIps("5", "6", "7");
        // then - add subject alternative domain names
        Assert.assertThat(Arrays.asList(ConfigurationProperties.sslSubjectAlternativeNameIps()), containsInAnyOrder("0.0.0.0", "1", "127.0.0.1", "2", "3", "4", "5", "6", "7"));
        Assert.assertEquals("0.0.0.0,1,127.0.0.1,2,3,4,5,6,7", System.getProperty("mockserver.sslSubjectAlternativeNameIps"));
        Assert.assertEquals(true, ConfigurationProperties.rebuildKeyStore());
        // given
        ConfigurationProperties.rebuildKeyStore(false);
        // when
        ConfigurationProperties.addSslSubjectAlternativeNameIps("5", "6", "7");
        // then - do not add duplicate subject alternative domain names
        Assert.assertThat(Arrays.asList(ConfigurationProperties.sslSubjectAlternativeNameIps()), containsInAnyOrder("0.0.0.0", "1", "127.0.0.1", "2", "3", "4", "5", "6", "7"));
        Assert.assertEquals("0.0.0.0,1,127.0.0.1,2,3,4,5,6,7", System.getProperty("mockserver.sslSubjectAlternativeNameIps"));
        Assert.assertEquals(false, ConfigurationProperties.rebuildKeyStore());
    }

    @Test
    public void shouldSetAndReadRebuildKeyStore() {
        // given
        ConfigurationProperties.rebuildKeyStore(false);
        // when
        Assert.assertEquals(false, ConfigurationProperties.rebuildKeyStore());
        ConfigurationProperties.rebuildKeyStore(true);
        // then
        Assert.assertEquals(true, ConfigurationProperties.rebuildKeyStore());
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionForInvalidLogLevel() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(CoreMatchers.containsString("log level \"WRONG\" is not legal it must be one of \"TRACE\", \"DEBUG\", \"INFO\", \"WARN\", \"ERROR\", \"OFF\""));
        ConfigurationProperties.logLevel("WRONG");
    }

    @Test
    public void shouldSetAndReadLogLevel() {
        // given
        System.clearProperty("mockserver.logLevel");
        // when
        Assert.assertEquals(Level.INFO, ConfigurationProperties.logLevel());
        ConfigurationProperties.logLevel("TRACE");
        // then
        Assert.assertEquals(Level.TRACE, ConfigurationProperties.logLevel());
        Assert.assertEquals("TRACE", System.getProperty("mockserver.logLevel"));
    }

    @Test
    public void shouldSetAndReadDisableRequestAudit() {
        // given
        System.clearProperty("mockserver.disableRequestAudit");
        // when
        Assert.assertFalse(ConfigurationProperties.disableRequestAudit());
        ConfigurationProperties.disableRequestAudit(false);
        // then
        Assert.assertFalse(ConfigurationProperties.disableRequestAudit());
        Assert.assertEquals("false", System.getProperty("mockserver.disableRequestAudit"));
    }

    @Test
    public void shouldSetAndReadDisableSystemOut() {
        // given
        System.clearProperty("mockserver.disableSystemOut");
        // when
        Assert.assertFalse(ConfigurationProperties.disableSystemOut());
        ConfigurationProperties.disableSystemOut(false);
        // then
        Assert.assertFalse(ConfigurationProperties.disableSystemOut());
        Assert.assertEquals("false", System.getProperty("mockserver.disableSystemOut"));
    }

    @Test
    public void shouldSetAndReadHttpProxy() {
        // given
        System.clearProperty("mockserver.httpProxy");
        // when
        Assert.assertNull(ConfigurationProperties.httpProxy());
        ConfigurationProperties.httpProxy("127.0.0.1:1080");
        // then
        Assert.assertEquals("/127.0.0.1:1080", ConfigurationProperties.httpProxy().toString());
        Assert.assertEquals("127.0.0.1:1080", System.getProperty("mockserver.httpProxy"));
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionForInvalidHttpProxy() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(CoreMatchers.containsString("Invalid httpProxy property must include <host>:<port> for example \"127.0.0.1:1090\" or \"localhost:1090\""));
        ConfigurationProperties.httpProxy("abc.def");
    }

    @Test
    public void shouldSetAndReadHttpsProxy() {
        // given
        System.clearProperty("mockserver.httpsProxy");
        // when
        Assert.assertNull(ConfigurationProperties.httpsProxy());
        ConfigurationProperties.httpsProxy("127.0.0.1:1080");
        // then
        Assert.assertEquals("/127.0.0.1:1080", ConfigurationProperties.httpsProxy().toString());
        Assert.assertEquals("127.0.0.1:1080", System.getProperty("mockserver.httpsProxy"));
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionForInvalidHttpsProxy() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(CoreMatchers.containsString("Invalid httpsProxy property must include <host>:<port> for example \"127.0.0.1:1090\" or \"localhost:1090\""));
        ConfigurationProperties.httpsProxy("abc.def");
    }

    @Test
    public void shouldSetAndReadSocksProxy() {
        // given
        System.clearProperty("mockserver.socksProxy");
        // when
        Assert.assertNull(ConfigurationProperties.socksProxy());
        ConfigurationProperties.socksProxy("127.0.0.1:1080");
        // then
        Assert.assertEquals("/127.0.0.1:1080", ConfigurationProperties.socksProxy().toString());
        Assert.assertEquals("127.0.0.1:1080", System.getProperty("mockserver.socksProxy"));
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionForInvalidSocksProxy() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(CoreMatchers.containsString("Invalid socksProxy property must include <host>:<port> for example \"127.0.0.1:1090\" or \"localhost:1090\""));
        ConfigurationProperties.socksProxy("abc.def");
    }

    @Test
    public void shouldSetAndReadLocalBoundIP() {
        // given
        System.clearProperty("mockserver.localBoundIP");
        // when
        Assert.assertEquals("", ConfigurationProperties.localBoundIP());
        ConfigurationProperties.localBoundIP("127.0.0.1");
        // then
        Assert.assertEquals("127.0.0.1", ConfigurationProperties.localBoundIP());
        Assert.assertEquals("127.0.0.1", System.getProperty("mockserver.localBoundIP"));
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionForInvalidLocalBoundIP() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(CoreMatchers.containsString("'abc.def' is not an IP string literal"));
        ConfigurationProperties.localBoundIP("abc.def");
    }

    @Test
    public void shouldSetAndReadProxyAuthenticationRealm() {
        // given
        System.clearProperty("mockserver.proxyAuthenticationRealm");
        // when
        Assert.assertEquals("MockServer HTTP Proxy", ConfigurationProperties.proxyAuthenticationRealm());
        ConfigurationProperties.proxyAuthenticationRealm("my realm");
        // then
        Assert.assertEquals("my realm", ConfigurationProperties.proxyAuthenticationRealm());
        Assert.assertEquals("my realm", System.getProperty("mockserver.proxyAuthenticationRealm"));
    }

    @Test
    public void shouldSetAndReadProxyAuthenticationUsername() {
        // given
        System.clearProperty("mockserver.proxyAuthenticationUsername");
        // when
        Assert.assertEquals("", ConfigurationProperties.proxyAuthenticationUsername());
        ConfigurationProperties.proxyAuthenticationUsername("john.doe");
        // then
        Assert.assertEquals("john.doe", ConfigurationProperties.proxyAuthenticationUsername());
        Assert.assertEquals("john.doe", System.getProperty("mockserver.proxyAuthenticationUsername"));
    }

    @Test
    public void shouldSetAndReadProxyAuthenticationPassword() {
        // given
        System.clearProperty("mockserver.proxyAuthenticationPassword");
        // when
        Assert.assertEquals("", ConfigurationProperties.proxyAuthenticationPassword());
        ConfigurationProperties.proxyAuthenticationPassword("p@ssw0rd");
        // then
        Assert.assertEquals("p@ssw0rd", ConfigurationProperties.proxyAuthenticationPassword());
        Assert.assertEquals("p@ssw0rd", System.getProperty("mockserver.proxyAuthenticationPassword"));
    }
}

