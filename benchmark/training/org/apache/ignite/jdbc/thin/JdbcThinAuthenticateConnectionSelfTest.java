/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.jdbc.thin;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.Test;


/**
 * Tests for authenticated an non authenticated JDBC thin connection.
 */
@SuppressWarnings("ThrowableNotThrown")
public class JdbcThinAuthenticateConnectionSelfTest extends JdbcThinAbstractSelfTest {
    /**
     *
     */
    private static final String URL = "jdbc:ignite:thin://127.0.0.1";

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testConnection() throws Exception {
        checkConnection(JdbcThinAuthenticateConnectionSelfTest.URL, "ignite", "ignite");
        checkConnection(JdbcThinAuthenticateConnectionSelfTest.URL, "another_user", "passwd");
        checkConnection(((JdbcThinAuthenticateConnectionSelfTest.URL) + "?user=ignite&password=ignite"), null, null);
        checkConnection(((JdbcThinAuthenticateConnectionSelfTest.URL) + "?user=another_user&password=passwd"), null, null);
    }

    /**
     *
     */
    @Test
    public void testInvalidUserPassword() {
        String err = "Unauthenticated sessions are prohibited";
        checkInvalidUserPassword(JdbcThinAuthenticateConnectionSelfTest.URL, null, null, err);
        err = "The user name or password is incorrect";
        checkInvalidUserPassword(JdbcThinAuthenticateConnectionSelfTest.URL, "ignite", null, err);
        checkInvalidUserPassword(JdbcThinAuthenticateConnectionSelfTest.URL, "another_user", null, err);
        checkInvalidUserPassword(JdbcThinAuthenticateConnectionSelfTest.URL, "ignite", "", err);
        checkInvalidUserPassword(JdbcThinAuthenticateConnectionSelfTest.URL, "ignite", "password", err);
        checkInvalidUserPassword(JdbcThinAuthenticateConnectionSelfTest.URL, "another_user", "ignite", err);
        checkInvalidUserPassword(JdbcThinAuthenticateConnectionSelfTest.URL, "another_user", "password", err);
        checkInvalidUserPassword(JdbcThinAuthenticateConnectionSelfTest.URL, "another_user", "password", err);
    }

    /**
     *
     *
     * @throws SQLException
     * 		On failed.
     */
    @Test
    public void testUserSqlOnAuthorized() throws SQLException {
        try (Connection conn = DriverManager.getConnection(JdbcThinAuthenticateConnectionSelfTest.URL, "ignite", "ignite")) {
            conn.createStatement().execute("CREATE USER test WITH PASSWORD 'test'");
            checkConnection(JdbcThinAuthenticateConnectionSelfTest.URL, "TEST", "test");
            conn.createStatement().execute("ALTER USER test WITH PASSWORD 'newpasswd'");
            checkConnection(JdbcThinAuthenticateConnectionSelfTest.URL, "TEST", "newpasswd");
            conn.createStatement().execute("DROP USER test");
            checkInvalidUserPassword(JdbcThinAuthenticateConnectionSelfTest.URL, "TEST", "newpasswd", "The user name or password is incorrect");
        }
    }

    /**
     *
     *
     * @throws SQLException
     * 		On error.
     */
    @Test
    public void testUserSqlWithNotIgniteUser() throws SQLException {
        try (Connection conn = DriverManager.getConnection(JdbcThinAuthenticateConnectionSelfTest.URL, "another_user", "passwd")) {
            String err = "User management operations are not allowed for user";
            checkUnauthorizedOperation(conn, "CREATE USER test WITH PASSWORD 'test'", err);
            checkUnauthorizedOperation(conn, "ALTER USER test WITH PASSWORD 'newpasswd'", err);
            checkUnauthorizedOperation(conn, "DROP USER test", err);
            checkUnauthorizedOperation(conn, "DROP USER \"another_user\"", err);
            conn.createStatement().execute("ALTER USER \"another_user\" WITH PASSWORD \'newpasswd\'");
            checkConnection(JdbcThinAuthenticateConnectionSelfTest.URL, "another_user", "newpasswd");
        }
    }

    /**
     *
     *
     * @throws SQLException
     * 		On error.
     */
    @Test
    public void testQuotedUsername() throws SQLException {
        // Spaces
        checkUserPassword(" test", "    ");
        checkUserPassword(" test ", " test ");
        checkUserPassword("test ", " ");
        checkUserPassword(" ", " ");
        checkUserPassword("user", "&/:=?");
        checkUserPassword("user", "&");
        checkUserPassword("user", "/");
        checkUserPassword("user", ":");
        checkUserPassword("user", "=");
        checkUserPassword("user", "?");
        // Nationals "user / password" in Russian.
        checkUserPassword("\\u044E\\u0437\\u0435\\u0440", "\\u043F\\u0430\\u0440\\u043E\\u043B\\u044C");
        // Nationals "user / password" in Chinese.
        checkUserPassword("\\u7528\\u6236", "\\u5BC6\\u78BC");
    }
}

