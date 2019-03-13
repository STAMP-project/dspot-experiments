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


import HttpHeaderNames.ACCESS_CONTROL_REQUEST_METHOD;
import HttpHeaderNames.AUTHORIZATION;
import HttpHeaderNames.ORIGIN;
import io.crate.integrationtests.SQLTransportIntegrationTest;
import io.crate.shade.org.postgresql.util.PSQLException;
import io.crate.testing.UseJdbc;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Properties;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.elasticsearch.http.HttpServerTransport;
import org.hamcrest.Matchers;
import org.junit.Test;


@UseJdbc(1)
public class AuthenticationIntegrationTest extends SQLTransportIntegrationTest {
    @Test
    public void testOptionsRequestDoesNotRequireAuth() throws Exception {
        HttpServerTransport httpTransport = internalCluster().getInstance(HttpServerTransport.class);
        InetSocketAddress address = httpTransport.boundAddress().publishAddress().address();
        String uri = String.format(Locale.ENGLISH, "http://%s:%s/", address.getHostName(), address.getPort());
        HttpOptions request = new HttpOptions(uri);
        request.setHeader(AUTHORIZATION.toString(), "Basic QXJ0aHVyOkV4Y2FsaWJ1cg==");
        request.setHeader(ORIGIN.toString(), "http://example.com");
        request.setHeader(ACCESS_CONTROL_REQUEST_METHOD.toString(), "GET");
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse resp = httpClient.execute(request);
        assertThat(resp.getStatusLine().getReasonPhrase(), Matchers.is("OK"));
    }

    @Test
    public void testValidCrateUser() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("user", "crate");
        DriverManager.getConnection(sqlExecutor.jdbcUrl(), properties);
    }

    @Test
    public void testInvalidUser() throws Exception {
        expectedException.expect(PSQLException.class);
        expectedException.expectMessage("FATAL: No valid auth.host_based entry found for host \"127.0.0.1\", user \"me\"");
        Properties properties = new Properties();
        properties.setProperty("user", "me");
        Connection conn = DriverManager.getConnection(sqlExecutor.jdbcUrl(), properties);
        conn.close();
    }

    @Test
    public void testUserInHbaThatDoesNotExist() throws Exception {
        expectedException.expect(PSQLException.class);
        expectedException.expectMessage("FATAL: trust authentication failed for user \"cr8\"");
        Properties properties = new Properties();
        properties.setProperty("user", "cr8");
        DriverManager.getConnection(sqlExecutor.jdbcUrl(), properties);
    }

    @Test
    public void testInvalidAuthenticationMethod() throws Exception {
        expectedException.expect(PSQLException.class);
        expectedException.expectMessage("FATAL: No valid auth.host_based entry found for host \"127.0.0.1\", user \"foo\"");
        Properties properties = new Properties();
        properties.setProperty("user", "foo");
        DriverManager.getConnection(sqlExecutor.jdbcUrl(), properties);
    }

    @Test
    public void testAuthenticationWithCreatedUser() throws Exception {
        // create a user with the crate user
        Properties properties = new Properties();
        properties.setProperty("user", "crate");
        try (Connection conn = DriverManager.getConnection(sqlExecutor.jdbcUrl(), properties)) {
            conn.createStatement().execute("CREATE USER arthur");
            conn.createStatement().execute("Grant DQL to arthur");
        }
        // connection with user arthur is possible
        properties.setProperty("user", "arthur");
        try (Connection conn = DriverManager.getConnection(sqlExecutor.jdbcUrl(), properties)) {
            assertThat(conn, Matchers.is(Matchers.notNullValue()));
        }
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
            conn.createStatement().execute("GRANT DQL to requiredssluser, optionalssluser, neverssluser");
        }
        // We don't have SSL available in the following tests:
        properties.setProperty("user", "optionalssluser");
        try (Connection ignored = DriverManager.getConnection(sqlExecutor.jdbcUrl(), properties)) {
        }
        properties.setProperty("user", "neverssluser");
        try (Connection ignored = DriverManager.getConnection(sqlExecutor.jdbcUrl(), properties)) {
        }
        try {
            properties.setProperty("user", "requiredssluser");
            try (Connection ignored = DriverManager.getConnection(sqlExecutor.jdbcUrl(), properties)) {
            }
            fail("We were able to proceed without SSL although requireSSL=required was set.");
        } catch (PSQLException e) {
            assertThat(e.getMessage(), Matchers.containsString("FATAL: No valid auth.host_based entry found"));
        }
    }
}

