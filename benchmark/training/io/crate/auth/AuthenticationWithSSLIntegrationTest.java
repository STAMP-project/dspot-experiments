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


import io.crate.integrationtests.SQLTransportIntegrationTest;
import io.crate.shade.org.postgresql.util.PSQLException;
import io.crate.testing.UseJdbc;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.hamcrest.Matchers;
import org.junit.Test;


/**
 * Additional tests to the SSL tests in {@link AuthenticationIntegrationTest} where SSL
 * support is disabled. In this test we have SSL support.
 */
@UseJdbc(1)
public class AuthenticationWithSSLIntegrationTest extends SQLTransportIntegrationTest {
    private static File trustStoreFile;

    private static File keyStoreFile;

    public AuthenticationWithSSLIntegrationTest() {
        super(true);
    }

    @Test
    @SuppressWarnings("EmptyTryBlock")
    public void checkSslConfigOption() throws SQLException {
        Properties properties = new Properties();
        properties.setProperty("user", "crate");
        try (Connection conn = DriverManager.getConnection(sqlExecutor.jdbcUrl(), properties)) {
            conn.createStatement().execute("CREATE USER requiredssluser");
            conn.createStatement().execute("CREATE USER optionalssluser");
            conn.createStatement().execute("CREATE USER neverssluser");
            conn.createStatement().execute("GRANT DQL TO requiredssluser, optionalssluser, neverssluser");
        }
        // We have SSL available in the following tests:
        properties.setProperty("user", "optionalssluser");
        try (Connection ignored = DriverManager.getConnection(sqlExecutor.jdbcUrl(), properties)) {
        }
        try {
            properties.setProperty("user", "neverssluser");
            try (Connection ignored = DriverManager.getConnection(sqlExecutor.jdbcUrl(), properties)) {
            }
            fail("User was able to use SSL although HBA config had requireSSL=never set.");
        } catch (PSQLException e) {
            assertThat(e.getMessage(), Matchers.containsString("FATAL: No valid auth.host_based entry found"));
        }
        properties.setProperty("user", "requiredssluser");
        try (Connection ignored = DriverManager.getConnection(sqlExecutor.jdbcUrl(), properties)) {
        }
    }

    @Test
    public void testClientCertAuthWithValidCert() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("user", "crate");
        try (Connection conn = DriverManager.getConnection(sqlExecutor.jdbcUrl(), properties)) {
            conn.createStatement().execute("CREATE USER localhost");
            conn.createStatement().execute("GRANT DQL TO localhost");
        }
        try {
            System.setProperty("javax.net.ssl.trustStore", AuthenticationWithSSLIntegrationTest.keyStoreFile.getAbsolutePath());
            System.setProperty("javax.net.ssl.trustStorePassword", "keystorePassword");
            properties.setProperty("user", "localhost");
            properties.setProperty("ssl", "true");
            properties.setProperty("sslfactory", "io.crate.shade.org.postgresql.ssl.DefaultJavaSSLFactory");
            try (Connection ignored = DriverManager.getConnection(sqlExecutor.jdbcUrl(), properties)) {
            }
        } finally {
            System.clearProperty("javax.net.ssl.trustStore");
            System.clearProperty("javax.net.ssl.trustStorePassword");
        }
    }

    @Test
    public void testClientCertAuthWithoutCert() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("user", "localhost");
        properties.setProperty("ssl", "true");
        expectedException.expectMessage("Client certificate authentication failed for user \"localhost\"");
        try (Connection ignored = DriverManager.getConnection(sqlExecutor.jdbcUrl(), properties)) {
        }
    }
}

