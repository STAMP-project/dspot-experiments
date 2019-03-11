/**
 * This file is part of a module with proprietary Enterprise Features.
 *
 * Licensed to Crate.io Inc. ("Crate.io") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 *
 * To use this file, Crate.io must have given you permission to enable and
 * use such Enterprise Features and you must have a valid Enterprise or
 * Subscription Agreement with Crate.io.  If you enable or use the Enterprise
 * Features, you represent and warrant that you have a valid Enterprise or
 * Subscription Agreement with Crate.io.  Your use of the Enterprise Features
 * if governed by the terms and conditions of your Enterprise or Subscription
 * Agreement with Crate.io.
 */
package io.crate.auth;


import HostBasedAuthentication.SSL;
import HostBasedAuthentication.SSL.OPTIONAL.VALUE;
import io.crate.protocols.postgres.ConnectionProperties;
import io.crate.test.integration.CrateUnitTest;
import java.net.InetAddress;
import java.security.cert.Certificate;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import javax.net.ssl.SSLSession;
import org.apache.commons.lang3.RandomStringUtils;
import org.elasticsearch.common.network.InetAddresses;
import org.elasticsearch.common.settings.Settings;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockito.Mockito;

import static HostBasedAuthentication.KEY_PROTOCOL;
import static Matchers.isValidAddress;
import static Matchers.isValidProtocol;
import static Matchers.isValidUser;
import static Protocol.HTTP;
import static Protocol.POSTGRES;


public class HostBasedAuthenticationTest extends CrateUnitTest {
    private static final Settings HBA_1 = Settings.builder().put("auth.host_based.config.1.user", "crate").put("auth.host_based.config.1.address", "127.0.0.1").put("auth.host_based.config.1.method", "trust").put("auth.host_based.config.1.protocol", "pg").build();

    private static final Settings HBA_2 = Settings.builder().put("auth.host_based.config.2.user", "crate").put("auth.host_based.config.2.address", "0.0.0.0/0").put("auth.host_based.config.2.method", "fake").put("auth.host_based.config.2.protocol", "pg").build();

    private static final Settings HBA_3 = Settings.builder().put("auth.host_based.config.3.address", "127.0.0.1").put("auth.host_based.config.3.method", "md5").put("auth.host_based.config.3.protocol", "pg").build();

    private static final Settings HBA_4 = Settings.builder().put("auth.host_based.config.4.address", "_local_").build();

    private static final InetAddress LOCALHOST = InetAddresses.forString("127.0.0.1");

    private SSLSession sslSession;

    @Test
    public void testMissingUserOrAddress() {
        HostBasedAuthentication authService = new HostBasedAuthentication(Settings.EMPTY, null);
        AuthenticationMethod method;
        method = authService.resolveAuthenticationType(null, new ConnectionProperties(HostBasedAuthenticationTest.LOCALHOST, POSTGRES, null));
        assertNull(method);
        method = authService.resolveAuthenticationType("crate", new ConnectionProperties(null, POSTGRES, null));
        assertNull(method);
    }

    @Test
    public void testEmptyHbaConf() {
        HostBasedAuthentication authService = new HostBasedAuthentication(Settings.EMPTY, null);
        AuthenticationMethod method = authService.resolveAuthenticationType("crate", new ConnectionProperties(HostBasedAuthenticationTest.LOCALHOST, POSTGRES, null));
        assertNull(method);
    }

    @Test
    public void testResolveAuthMethod() {
        HostBasedAuthentication authService = new HostBasedAuthentication(HostBasedAuthenticationTest.HBA_1, null);
        AuthenticationMethod method = authService.resolveAuthenticationType("crate", new ConnectionProperties(HostBasedAuthenticationTest.LOCALHOST, POSTGRES, null));
        assertThat(method, Matchers.instanceOf(TrustAuthenticationMethod.class));
    }

    @Test
    public void testFilterEntriesSimple() {
        HostBasedAuthentication authService = new HostBasedAuthentication(HostBasedAuthenticationTest.HBA_1, null);
        Optional entry;
        entry = authService.getEntry("crate", new ConnectionProperties(HostBasedAuthenticationTest.LOCALHOST, POSTGRES, null));
        assertThat(entry.isPresent(), Is.is(true));
        entry = authService.getEntry("cr8", new ConnectionProperties(HostBasedAuthenticationTest.LOCALHOST, POSTGRES, null));
        assertThat(entry.isPresent(), Is.is(false));
        entry = authService.getEntry("crate", new ConnectionProperties(InetAddresses.forString("10.0.0.1"), POSTGRES, null));
        assertThat(entry.isPresent(), Is.is(false));
    }

    @Test
    public void testFilterEntriesCIDR() {
        Settings settings = Settings.builder().put(HostBasedAuthenticationTest.HBA_2).put(HostBasedAuthenticationTest.HBA_3).build();
        HostBasedAuthentication authService = new HostBasedAuthentication(settings, null);
        Optional<Map.Entry<String, Map<String, String>>> entry;
        entry = authService.getEntry("crate", new ConnectionProperties(InetAddresses.forString("123.45.67.89"), POSTGRES, null));
        assertTrue(entry.isPresent());
        assertThat(entry.get().getValue().get("method"), Is.is("fake"));
        entry = authService.getEntry("cr8", new ConnectionProperties(InetAddresses.forString("127.0.0.1"), POSTGRES, null));
        assertTrue(entry.isPresent());
        assertThat(entry.get().getValue().get("method"), Is.is("md5"));
        entry = authService.getEntry("cr8", new ConnectionProperties(InetAddresses.forString("123.45.67.89"), POSTGRES, null));
        assertThat(entry.isPresent(), Is.is(false));
    }

    @Test
    public void testLocalhostMatchesBothIpv4AndIpv6() {
        HostBasedAuthentication authService = new HostBasedAuthentication(HostBasedAuthenticationTest.HBA_4, null);
        Optional<Map.Entry<String, Map<String, String>>> entry;
        entry = authService.getEntry("crate", new ConnectionProperties(InetAddresses.forString("127.0.0.1"), POSTGRES, null));
        assertTrue(entry.isPresent());
        entry = authService.getEntry("crate", new ConnectionProperties(InetAddresses.forString("::1"), POSTGRES, null));
        assertTrue(entry.isPresent());
    }

    @Test
    public void testMatchUser() throws Exception {
        // only "crate" matches
        Map.Entry<String, Map<String, String>> entry = new AbstractMap.SimpleEntry<>("0", Collections.singletonMap("user", "crate"));
        assertTrue(isValidUser(entry, "crate"));
        assertFalse(isValidUser(entry, "postgres"));
        // any user matches
        entry = // key "user" not present in map
        new AbstractMap.SimpleEntry<>("0", Collections.emptyMap());
        assertTrue(isValidUser(entry, RandomStringUtils.random(8)));
    }

    @Test
    public void testMatchProtocol() throws Exception {
        assertTrue(Matchers.isValidProtocol("pg", Protocol.POSTGRES));
        assertFalse(Matchers.isValidProtocol("http", Protocol.POSTGRES));
        assertTrue(isValidProtocol(null, Protocol.POSTGRES));
    }

    @Test
    public void testMatchAddress() throws Exception {
        String hbaAddress = "10.0.1.100";
        assertTrue(Matchers.isValidAddress(hbaAddress, InetAddresses.forString("10.0.1.100")));
        assertFalse(Matchers.isValidAddress(hbaAddress, InetAddresses.forString("10.0.1.99")));
        assertFalse(Matchers.isValidAddress(hbaAddress, InetAddresses.forString("10.0.1.101")));
        hbaAddress = "10.0.1.0/24";// 10.0.1.0 -- 10.0.1.255

        assertTrue(Matchers.isValidAddress(hbaAddress, InetAddresses.forString("10.0.1.0")));
        assertTrue(Matchers.isValidAddress(hbaAddress, InetAddresses.forString("10.0.1.255")));
        assertFalse(Matchers.isValidAddress(hbaAddress, InetAddresses.forString("10.0.0.255")));
        assertFalse(Matchers.isValidAddress(hbaAddress, InetAddresses.forString("10.0.2.0")));
        assertTrue(isValidAddress(null, InetAddresses.forString(String.format("%s.%s.%s.%s", randomInt(255), randomInt(255), randomInt(255), randomInt(255)))));
    }

    @Test
    public void testConvertSettingsToConf() throws Exception {
        Settings settings = // ignored because empty
        Settings.builder().put("auth.host_based.enabled", true).put(HostBasedAuthenticationTest.HBA_1).put(HostBasedAuthenticationTest.HBA_2).put("auth.host_based.config", "3", new String[]{  }, new String[]{  }).build();
        HostBasedAuthentication authService = new HostBasedAuthentication(settings, null);
        Settings confirmSettings = Settings.builder().put(HostBasedAuthenticationTest.HBA_1).put(HostBasedAuthenticationTest.HBA_2).build();
        assertThat(authService.hbaConf(), Is.is(authService.convertHbaSettingsToHbaConf(confirmSettings)));
    }

    @Test
    public void testPSQLSslOption() {
        Settings sslConfig;
        HostBasedAuthentication authService;
        sslConfig = Settings.builder().put(HostBasedAuthenticationTest.HBA_1).put(("auth.host_based.config.1." + (SSL.KEY)), VALUE).build();
        authService = new HostBasedAuthentication(sslConfig, null);
        assertThat(authService.getEntry("crate", new ConnectionProperties(HostBasedAuthenticationTest.LOCALHOST, POSTGRES, null)), Matchers.not(Optional.empty()));
        assertThat(authService.getEntry("crate", new ConnectionProperties(HostBasedAuthenticationTest.LOCALHOST, POSTGRES, sslSession)), Matchers.not(Optional.empty()));
        sslConfig = Settings.builder().put(HostBasedAuthenticationTest.HBA_1).put(("auth.host_based.config.1." + (SSL.KEY)), HostBasedAuthentication.SSL.REQUIRED.VALUE).build();
        authService = new HostBasedAuthentication(sslConfig, null);
        assertThat(authService.getEntry("crate", new ConnectionProperties(HostBasedAuthenticationTest.LOCALHOST, POSTGRES, null)), Is.is(Optional.empty()));
        assertThat(authService.getEntry("crate", new ConnectionProperties(HostBasedAuthenticationTest.LOCALHOST, POSTGRES, sslSession)), Matchers.not(Optional.empty()));
        sslConfig = Settings.builder().put(HostBasedAuthenticationTest.HBA_1).put(("auth.host_based.config.1." + (SSL.KEY)), HostBasedAuthentication.SSL.NEVER.VALUE).build();
        authService = new HostBasedAuthentication(sslConfig, null);
        assertThat(authService.getEntry("crate", new ConnectionProperties(HostBasedAuthenticationTest.LOCALHOST, POSTGRES, null)), Matchers.not(Optional.empty()));
        assertThat(authService.getEntry("crate", new ConnectionProperties(HostBasedAuthenticationTest.LOCALHOST, POSTGRES, sslSession)), Is.is(Optional.empty()));
    }

    @Test
    public void testHttpSSLOption() throws Exception {
        Settings baseConfig = Settings.builder().put(HostBasedAuthenticationTest.HBA_1).put(("auth.host_based.config.1." + (KEY_PROTOCOL)), "http").build();
        SSLSession sslSession = Mockito.mock(SSLSession.class);
        Mockito.when(sslSession.getPeerCertificates()).thenReturn(new Certificate[0]);
        ConnectionProperties sslConnProperties = new ConnectionProperties(HostBasedAuthenticationTest.LOCALHOST, HTTP, sslSession);
        ConnectionProperties noSslConnProperties = new ConnectionProperties(HostBasedAuthenticationTest.LOCALHOST, HTTP, null);
        Settings sslConfig;
        HostBasedAuthentication authService;
        sslConfig = Settings.builder().put(baseConfig).put(("auth.host_based.config.1." + (SSL.KEY)), VALUE).build();
        authService = new HostBasedAuthentication(sslConfig, null);
        assertThat(authService.getEntry("crate", noSslConnProperties), Matchers.not(Optional.empty()));
        assertThat(authService.getEntry("crate", sslConnProperties), Matchers.not(Optional.empty()));
        sslConfig = Settings.builder().put(baseConfig).put(("auth.host_based.config.1." + (SSL.KEY)), HostBasedAuthentication.SSL.REQUIRED.VALUE).build();
        authService = new HostBasedAuthentication(sslConfig, null);
        assertThat(authService.getEntry("crate", noSslConnProperties), Is.is(Optional.empty()));
        assertThat(authService.getEntry("crate", sslConnProperties), Matchers.not(Optional.empty()));
        sslConfig = Settings.builder().put(baseConfig).put(("auth.host_based.config.1." + (SSL.KEY)), HostBasedAuthentication.SSL.NEVER.VALUE).build();
        authService = new HostBasedAuthentication(sslConfig, null);
        assertThat(authService.getEntry("crate", noSslConnProperties), Matchers.not(Optional.empty()));
        assertThat(authService.getEntry("crate", sslConnProperties), Is.is(Optional.empty()));
    }
}

