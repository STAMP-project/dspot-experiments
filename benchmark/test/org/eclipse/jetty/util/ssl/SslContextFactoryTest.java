/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.util.ssl;


import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.UnrecoverableKeyException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import org.eclipse.jetty.toolchain.test.matchers.RegexMatcher;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.log.StacklessLogging;
import org.eclipse.jetty.util.resource.Resource;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class SslContextFactoryTest {
    private SslContextFactory cf;

    @Test
    public void testSLOTH() throws Exception {
        cf.setKeyStorePassword("storepwd");
        cf.setKeyManagerPassword("keypwd");
        cf.start();
        // cf.dump(System.out, "");
        List<SslSelectionDump> dumps = cf.selectionDump();
        SslSelectionDump cipherDump = dumps.stream().filter(( dump) -> dump.type.contains("Cipher Suite")).findFirst().get();
        for (String enabledCipher : cipherDump.enabled) {
            MatcherAssert.assertThat("Enabled Cipher Suite", enabledCipher, Matchers.not(RegexMatcher.matchesPattern(".*_RSA_.*(SHA1|MD5|SHA)")));
        }
    }

    @Test
    public void testDump_IncludeTlsRsa() throws Exception {
        cf.setKeyStorePassword("storepwd");
        cf.setKeyManagerPassword("keypwd");
        cf.setIncludeCipherSuites("TLS_RSA_.*");
        cf.setExcludeCipherSuites("BOGUS");// just to not exclude anything

        cf.start();
        // cf.dump(System.out, "");
        List<SslSelectionDump> dumps = cf.selectionDump();
        SSLEngine ssl = SSLContext.getDefault().createSSLEngine();
        List<String> tlsRsaSuites = Stream.of(ssl.getSupportedCipherSuites()).filter(( suite) -> suite.startsWith("TLS_RSA_")).collect(Collectors.toList());
        List<String> selectedSuites = Arrays.asList(cf.getSelectedCipherSuites());
        SslSelectionDump cipherDump = dumps.stream().filter(( dump) -> dump.type.contains("Cipher Suite")).findFirst().get();
        MatcherAssert.assertThat("Dump Enabled List size is equal to selected list size", cipherDump.enabled.size(), Matchers.is(selectedSuites.size()));
        for (String expectedCipherSuite : tlsRsaSuites) {
            MatcherAssert.assertThat("Selected Cipher Suites", selectedSuites, Matchers.hasItem(expectedCipherSuite));
            MatcherAssert.assertThat("Dump Enabled Cipher Suites", cipherDump.enabled, Matchers.hasItem(expectedCipherSuite));
        }
    }

    @Test
    public void testNoTsFileKs() throws Exception {
        cf.setKeyStorePassword("storepwd");
        cf.setKeyManagerPassword("keypwd");
        cf.start();
        Assertions.assertTrue(((cf.getSslContext()) != null));
    }

    @Test
    public void testNoTsSetKs() throws Exception {
        KeyStore ks = KeyStore.getInstance("JKS");
        try (InputStream keystoreInputStream = this.getClass().getResourceAsStream("keystore")) {
            ks.load(keystoreInputStream, "storepwd".toCharArray());
        }
        cf.setKeyStore(ks);
        cf.setKeyManagerPassword("keypwd");
        cf.start();
        Assertions.assertTrue(((cf.getSslContext()) != null));
    }

    @Test
    public void testNoTsNoKs() throws Exception {
        cf.start();
        Assertions.assertTrue(((cf.getSslContext()) != null));
    }

    @Test
    public void testTrustAll() throws Exception {
        cf.start();
        Assertions.assertTrue(((cf.getSslContext()) != null));
    }

    @Test
    public void testNoTsResourceKs() throws Exception {
        Resource keystoreResource = Resource.newSystemResource("keystore");
        cf.setKeyStoreResource(keystoreResource);
        cf.setKeyStorePassword("storepwd");
        cf.setKeyManagerPassword("keypwd");
        cf.setTrustStoreResource(keystoreResource);
        cf.setTrustStorePassword(null);
        cf.start();
        Assertions.assertTrue(((cf.getSslContext()) != null));
    }

    @Test
    public void testResourceTsResourceKs() throws Exception {
        Resource keystoreResource = Resource.newSystemResource("keystore");
        Resource truststoreResource = Resource.newSystemResource("keystore");
        cf.setKeyStoreResource(keystoreResource);
        cf.setTrustStoreResource(truststoreResource);
        cf.setKeyStorePassword("storepwd");
        cf.setKeyManagerPassword("keypwd");
        cf.setTrustStorePassword("storepwd");
        cf.start();
        Assertions.assertTrue(((cf.getSslContext()) != null));
    }

    @Test
    public void testResourceTsResourceKsWrongPW() throws Exception {
        Resource keystoreResource = Resource.newSystemResource("keystore");
        Resource truststoreResource = Resource.newSystemResource("keystore");
        cf.setKeyStoreResource(keystoreResource);
        cf.setTrustStoreResource(truststoreResource);
        cf.setKeyStorePassword("storepwd");
        cf.setKeyManagerPassword("wrong_keypwd");
        cf.setTrustStorePassword("storepwd");
        try (StacklessLogging ignore = new StacklessLogging(AbstractLifeCycle.class)) {
            UnrecoverableKeyException x = Assertions.assertThrows(UnrecoverableKeyException.class, () -> cf.start());
            MatcherAssert.assertThat(x.getMessage(), Matchers.containsString("Cannot recover key"));
        }
    }

    @Test
    public void testResourceTsWrongPWResourceKs() throws Exception {
        Resource keystoreResource = Resource.newSystemResource("keystore");
        Resource truststoreResource = Resource.newSystemResource("keystore");
        cf.setKeyStoreResource(keystoreResource);
        cf.setTrustStoreResource(truststoreResource);
        cf.setKeyStorePassword("storepwd");
        cf.setKeyManagerPassword("keypwd");
        cf.setTrustStorePassword("wrong_storepwd");
        try (StacklessLogging ignore = new StacklessLogging(AbstractLifeCycle.class)) {
            IOException x = Assertions.assertThrows(IOException.class, () -> cf.start());
            MatcherAssert.assertThat(x.getMessage(), Matchers.containsString("Keystore was tampered with, or password was incorrect"));
        }
    }

    @Test
    public void testNoKeyConfig() throws Exception {
        try (StacklessLogging ignore = new StacklessLogging(AbstractLifeCycle.class)) {
            IllegalStateException x = Assertions.assertThrows(IllegalStateException.class, () -> {
                cf.setTrustStorePath("/foo");
                cf.start();
            });
            MatcherAssert.assertThat(x.getMessage(), Matchers.containsString("no valid keystore"));
        }
    }

    @Test
    public void testSetExcludeCipherSuitesRegex() throws Exception {
        cf.setExcludeCipherSuites(".*RC4.*");
        cf.start();
        SSLEngine sslEngine = cf.newSSLEngine();
        String[] enabledCipherSuites = sslEngine.getEnabledCipherSuites();
        MatcherAssert.assertThat("At least 1 cipherSuite is enabled", enabledCipherSuites.length, Matchers.greaterThan(0));
        for (String enabledCipherSuite : enabledCipherSuites)
            MatcherAssert.assertThat("CipherSuite does not contain RC4", enabledCipherSuite.contains("RC4"), Matchers.equalTo(false));

    }

    @Test
    public void testSetIncludeCipherSuitesRegex() throws Exception {
        cf.setIncludeCipherSuites(".*ECDHE.*", ".*WIBBLE.*");
        cf.start();
        SSLEngine sslEngine = cf.newSSLEngine();
        String[] enabledCipherSuites = sslEngine.getEnabledCipherSuites();
        MatcherAssert.assertThat("At least 1 cipherSuite is enabled", enabledCipherSuites.length, Matchers.greaterThan(1));
        for (String enabledCipherSuite : enabledCipherSuites)
            MatcherAssert.assertThat("CipherSuite contains ECDHE", enabledCipherSuite.contains("ECDHE"), Matchers.equalTo(true));

    }

    @Test
    public void testProtocolAndCipherSettingsAreNPESafe() {
        Assertions.assertNotNull(cf.getExcludeProtocols());
        Assertions.assertNotNull(cf.getIncludeProtocols());
        Assertions.assertNotNull(cf.getExcludeCipherSuites());
        Assertions.assertNotNull(cf.getIncludeCipherSuites());
    }

    @Test
    public void testSNICertificates() throws Exception {
        Resource keystoreResource = Resource.newSystemResource("snikeystore");
        cf.setKeyStoreResource(keystoreResource);
        cf.setKeyStorePassword("storepwd");
        cf.setKeyManagerPassword("keypwd");
        cf.start();
        MatcherAssert.assertThat(cf.getAliases(), Matchers.containsInAnyOrder("jetty", "other", "san", "wild"));
        MatcherAssert.assertThat(cf.getX509("jetty").getHosts(), Matchers.containsInAnyOrder("jetty.eclipse.org"));
        Assertions.assertTrue(cf.getX509("jetty").getWilds().isEmpty());
        Assertions.assertTrue(cf.getX509("jetty").matches("JETTY.Eclipse.Org"));
        Assertions.assertFalse(cf.getX509("jetty").matches("m.jetty.eclipse.org"));
        Assertions.assertFalse(cf.getX509("jetty").matches("eclipse.org"));
        MatcherAssert.assertThat(cf.getX509("other").getHosts(), Matchers.containsInAnyOrder("www.example.com"));
        Assertions.assertTrue(cf.getX509("other").getWilds().isEmpty());
        Assertions.assertTrue(cf.getX509("other").matches("www.example.com"));
        Assertions.assertFalse(cf.getX509("other").matches("eclipse.org"));
        MatcherAssert.assertThat(cf.getX509("san").getHosts(), Matchers.containsInAnyOrder("www.san.com", "m.san.com"));
        Assertions.assertTrue(cf.getX509("san").getWilds().isEmpty());
        Assertions.assertTrue(cf.getX509("san").matches("www.san.com"));
        Assertions.assertTrue(cf.getX509("san").matches("m.san.com"));
        Assertions.assertFalse(cf.getX509("san").matches("other.san.com"));
        Assertions.assertFalse(cf.getX509("san").matches("san.com"));
        Assertions.assertFalse(cf.getX509("san").matches("eclipse.org"));
        Assertions.assertTrue(cf.getX509("wild").getHosts().isEmpty());
        MatcherAssert.assertThat(cf.getX509("wild").getWilds(), Matchers.containsInAnyOrder("domain.com"));
        Assertions.assertTrue(cf.getX509("wild").matches("domain.com"));
        Assertions.assertTrue(cf.getX509("wild").matches("www.domain.com"));
        Assertions.assertTrue(cf.getX509("wild").matches("other.domain.com"));
        Assertions.assertFalse(cf.getX509("wild").matches("foo.bar.domain.com"));
        Assertions.assertFalse(cf.getX509("wild").matches("other.com"));
    }

    @Test
    public void testNonDefaultKeyStoreTypeUsedForTrustStore() throws Exception {
        cf = new SslContextFactory();
        cf.setKeyStoreResource(Resource.newSystemResource("keystore.p12"));
        cf.setKeyStoreType("pkcs12");
        cf.setKeyStorePassword("storepwd");
        cf.start();
        cf.stop();
        cf = new SslContextFactory();
        cf.setKeyStoreResource(Resource.newSystemResource("keystore.jce"));
        cf.setKeyStoreType("jceks");
        cf.setKeyStorePassword("storepwd");
        cf.start();
        cf.stop();
    }
}

