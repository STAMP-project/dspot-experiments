/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hive.minikdc;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.security.sasl.AuthenticationException;
import org.apache.hive.service.auth.PasswdAuthenticationProvider;
import org.junit.Assert;
import org.junit.Test;


public class TestJdbcNonKrbSASLWithMiniKdc extends TestJdbcWithMiniKdc {
    public static final String SASL_NONKRB_USER1 = "nonkrbuser";

    public static final String SASL_NONKRB_USER2 = "nonkrbuser@realm.com";

    public static final String SASL_NONKRB_PWD = "mypwd";

    public static class CustomAuthenticator implements PasswdAuthenticationProvider {
        @Override
        public void Authenticate(String user, String password) throws AuthenticationException {
            if ((!((TestJdbcNonKrbSASLWithMiniKdc.SASL_NONKRB_USER1.equals(user)) && (TestJdbcNonKrbSASLWithMiniKdc.SASL_NONKRB_PWD.equals(password)))) && (!((TestJdbcNonKrbSASLWithMiniKdc.SASL_NONKRB_USER2.equals(user)) && (TestJdbcNonKrbSASLWithMiniKdc.SASL_NONKRB_PWD.equals(password))))) {
                throw new AuthenticationException("Authentication failed");
            }
        }
    }

    /**
     * *
     * Test a nonkrb user could login the kerberized HS2 with authentication type SASL NONE
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testNonKrbSASLAuth() throws Exception {
        hs2Conn = DriverManager.getConnection((((((TestJdbcWithMiniKdc.miniHS2.getBaseJdbcURL()) + "default;user=") + (TestJdbcNonKrbSASLWithMiniKdc.SASL_NONKRB_USER1)) + ";password=") + (TestJdbcNonKrbSASLWithMiniKdc.SASL_NONKRB_PWD)));
        verifyProperty(TestJdbcWithMiniKdc.SESSION_USER_NAME, TestJdbcNonKrbSASLWithMiniKdc.SASL_NONKRB_USER1);
        hs2Conn.close();
    }

    /**
     * *
     * Test a nonkrb user could login the kerberized HS2 with authentication type SASL NONE
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testNonKrbSASLFullNameAuth() throws Exception {
        hs2Conn = DriverManager.getConnection((((((TestJdbcWithMiniKdc.miniHS2.getBaseJdbcURL()) + "default;user=") + (TestJdbcNonKrbSASLWithMiniKdc.SASL_NONKRB_USER2)) + ";password=") + (TestJdbcNonKrbSASLWithMiniKdc.SASL_NONKRB_PWD)));
        verifyProperty(TestJdbcWithMiniKdc.SESSION_USER_NAME, TestJdbcNonKrbSASLWithMiniKdc.SASL_NONKRB_USER1);
        hs2Conn.close();
    }

    /**
     * *
     * Negative test, verify that connection to secure HS2 fails if it is noSasl
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testNoSaslConnectionNeg() throws Exception {
        try {
            String url = (TestJdbcWithMiniKdc.miniHS2.getBaseJdbcURL()) + "default;auth=noSasl";
            hs2Conn = DriverManager.getConnection(url);
            Assert.fail("noSasl connection should fail");
        } catch (SQLException e) {
            // expected error
            Assert.assertEquals("08S01", e.getSQLState().trim());
        }
    }

    /**
     * *
     * Negative test, verify that NonKrb connection to secure HS2 fails if it is
     * user/pwd do not match.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testNoKrbConnectionNeg() throws Exception {
        try {
            String url = (TestJdbcWithMiniKdc.miniHS2.getBaseJdbcURL()) + "default;user=wronguser;pwd=mypwd";
            hs2Conn = DriverManager.getConnection(url);
            Assert.fail("noSasl connection should fail");
        } catch (SQLException e) {
            // expected error
            Assert.assertEquals("08S01", e.getSQLState().trim());
        }
    }

    /**
     * *
     * Negative test for token based authentication
     * Verify that token is not applicable to non-Kerberos SASL user
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testNoKrbSASLTokenAuthNeg() throws Exception {
        hs2Conn = DriverManager.getConnection((((((TestJdbcWithMiniKdc.miniHS2.getBaseJdbcURL()) + "default;user=") + (TestJdbcNonKrbSASLWithMiniKdc.SASL_NONKRB_USER1)) + ";password=") + (TestJdbcNonKrbSASLWithMiniKdc.SASL_NONKRB_PWD)));
        verifyProperty(TestJdbcWithMiniKdc.SESSION_USER_NAME, TestJdbcNonKrbSASLWithMiniKdc.SASL_NONKRB_USER1);
        try {
            // retrieve token and store in the cache
            String token = getDelegationToken(MiniHiveKdc.HIVE_TEST_USER_1, MiniHiveKdc.HIVE_SERVICE_PRINCIPAL);
            Assert.fail((((TestJdbcNonKrbSASLWithMiniKdc.SASL_NONKRB_USER1) + " shouldn't be allowed to retrieve token for ") + (MiniHiveKdc.HIVE_TEST_USER_2)));
        } catch (SQLException e) {
            // Expected error
            Assert.assertTrue(e.getMessage().contains("Delegation token only supported over remote client with kerberos authentication"));
        } finally {
            hs2Conn.close();
        }
    }
}

