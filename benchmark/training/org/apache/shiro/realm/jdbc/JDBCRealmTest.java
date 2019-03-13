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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.shiro.realm.jdbc;


import JdbcRealm.SaltStyle.COLUMN;
import JdbcRealm.SaltStyle.EXTERNAL;
import JdbcRealm.SaltStyle.NO_SALT;
import Subject.Builder;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import javax.sql.DataSource;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


/**
 * Test case for JDBCRealm.
 */
public class JDBCRealmTest {
    protected DefaultSecurityManager securityManager = null;

    protected AuthorizingRealm realm;

    protected final String username = "testUser";

    protected final String plainTextPassword = "testPassword";

    protected final String salt = username;// Default impl of getSaltForUser returns username


    protected final String testRole = "testRole";

    protected final String testPermissionString = "testDomain:testTarget:testAction";

    // Maps keyed on test method name so setup/teardown can manage per test resources
    protected HashMap<String, JdbcRealm> realmMap = new HashMap<String, JdbcRealm>();

    protected HashMap<String, DataSource> dsMap = new HashMap<String, DataSource>();

    @Rule
    public TestName name = new TestName();

    @Test
    public void testUnSaltedSuccess() throws Exception {
        String testMethodName = name.getMethodName();
        JdbcRealm realm = realmMap.get(testMethodName);
        createDefaultSchema(testMethodName, false);
        realm.setSaltStyle(NO_SALT);
        Subject.Builder builder = new Subject.Builder(securityManager);
        Subject currentUser = builder.buildSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, plainTextPassword);
        currentUser.login(token);
        currentUser.logout();
    }

    @Test
    public void testUnSaltedWrongPassword() throws Exception {
        String testMethodName = name.getMethodName();
        JdbcRealm realm = realmMap.get(testMethodName);
        createDefaultSchema(testMethodName, false);
        realm.setSaltStyle(NO_SALT);
        Subject.Builder builder = new Subject.Builder(securityManager);
        Subject currentUser = builder.buildSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, "passwrd");
        try {
            currentUser.login(token);
        } catch (IncorrectCredentialsException ex) {
            // Expected
        }
    }

    @Test
    public void testUnSaltedMultipleRows() throws Exception {
        String testMethodName = name.getMethodName();
        JdbcRealm realm = realmMap.get(testMethodName);
        createDefaultSchema(testMethodName, false);
        realm.setSaltStyle(NO_SALT);
        Connection conn = dsMap.get(testMethodName).getConnection();
        Statement sql = conn.createStatement();
        sql.executeUpdate((("insert into users values ('" + (username)) + "', 'dupe')"));
        Subject.Builder builder = new Subject.Builder(securityManager);
        Subject currentUser = builder.buildSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, "passwrd");
        try {
            currentUser.login(token);
        } catch (AuthenticationException ex) {
            // Expected
        }
    }

    @Test
    public void testSaltColumnSuccess() throws Exception {
        String testMethodName = name.getMethodName();
        JdbcRealm realm = realmMap.get(testMethodName);
        createSaltColumnSchema(testMethodName);
        realm.setSaltStyle(COLUMN);
        Subject.Builder builder = new Subject.Builder(securityManager);
        Subject currentUser = builder.buildSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, plainTextPassword);
        currentUser.login(token);
        currentUser.logout();
    }

    @Test
    public void testSaltColumnWrongPassword() throws Exception {
        String testMethodName = name.getMethodName();
        JdbcRealm realm = realmMap.get(testMethodName);
        createSaltColumnSchema(testMethodName);
        realm.setSaltStyle(COLUMN);
        Subject.Builder builder = new Subject.Builder(securityManager);
        Subject currentUser = builder.buildSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, "passwrd");
        try {
            currentUser.login(token);
        } catch (IncorrectCredentialsException ex) {
            // Expected
        }
    }

    @Test
    public void testExternalSuccess() throws Exception {
        String testMethodName = name.getMethodName();
        JdbcRealm realm = realmMap.get(testMethodName);
        createDefaultSchema(testMethodName, true);
        realm.setSaltStyle(EXTERNAL);
        Subject.Builder builder = new Subject.Builder(securityManager);
        Subject currentUser = builder.buildSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, plainTextPassword);
        currentUser.login(token);
        currentUser.logout();
    }

    @Test
    public void testExternalWrongPassword() throws Exception {
        String testMethodName = name.getMethodName();
        JdbcRealm realm = realmMap.get(testMethodName);
        createDefaultSchema(testMethodName, true);
        realm.setSaltStyle(EXTERNAL);
        Subject.Builder builder = new Subject.Builder(securityManager);
        Subject currentUser = builder.buildSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, "passwrd");
        try {
            currentUser.login(token);
        } catch (IncorrectCredentialsException ex) {
            // Expected
        }
    }

    @Test
    public void testRolePresent() throws Exception {
        String testMethodName = name.getMethodName();
        JdbcRealm realm = realmMap.get(testMethodName);
        createDefaultSchema(testMethodName, false);
        realm.setSaltStyle(NO_SALT);
        Subject.Builder builder = new Subject.Builder(securityManager);
        Subject currentUser = builder.buildSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, plainTextPassword);
        currentUser.login(token);
        Assert.assertTrue(currentUser.hasRole(testRole));
    }

    @Test
    public void testRoleNotPresent() throws Exception {
        String testMethodName = name.getMethodName();
        JdbcRealm realm = realmMap.get(testMethodName);
        createDefaultSchema(testMethodName, false);
        realm.setSaltStyle(NO_SALT);
        Subject.Builder builder = new Subject.Builder(securityManager);
        Subject currentUser = builder.buildSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, plainTextPassword);
        currentUser.login(token);
        Assert.assertFalse(currentUser.hasRole("Game Overall Director"));
    }

    @Test
    public void testPermissionPresent() throws Exception {
        String testMethodName = name.getMethodName();
        JdbcRealm realm = realmMap.get(testMethodName);
        createDefaultSchema(testMethodName, false);
        realm.setSaltStyle(NO_SALT);
        realm.setPermissionsLookupEnabled(true);
        Subject.Builder builder = new Subject.Builder(securityManager);
        Subject currentUser = builder.buildSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, plainTextPassword);
        currentUser.login(token);
        Assert.assertTrue(currentUser.isPermitted(testPermissionString));
    }

    @Test
    public void testPermissionNotPresent() throws Exception {
        String testMethodName = name.getMethodName();
        JdbcRealm realm = realmMap.get(testMethodName);
        createDefaultSchema(testMethodName, false);
        realm.setSaltStyle(NO_SALT);
        realm.setPermissionsLookupEnabled(true);
        Subject.Builder builder = new Subject.Builder(securityManager);
        Subject currentUser = builder.buildSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, plainTextPassword);
        currentUser.login(token);
        Assert.assertFalse(currentUser.isPermitted("testDomain:testTarget:specialAction"));
    }
}

