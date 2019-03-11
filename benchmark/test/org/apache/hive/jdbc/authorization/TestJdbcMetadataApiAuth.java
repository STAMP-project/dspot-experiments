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
package org.apache.hive.jdbc.authorization;


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.security.HiveAuthenticationProvider;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HiveAccessControlException;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HiveAuthorizer;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HiveAuthorizerFactory;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HiveAuthzContext;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HiveAuthzPluginException;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HiveAuthzSessionContext;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HiveMetastoreClientFactory;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HiveOperationType;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HivePrivilegeObject;
import org.apache.hadoop.hive.ql.security.authorization.plugin.sqlstd.SQLStdHiveAccessControllerWrapper;
import org.apache.hadoop.hive.ql.security.authorization.plugin.sqlstd.SQLStdHiveAuthorizationValidator;
import org.apache.hive.jdbc.miniHS2.MiniHS2;
import org.junit.Assert;
import org.junit.Test;


/**
 * Verify validation of jdbc metadata methods is happening
 */
public class TestJdbcMetadataApiAuth {
    private static MiniHS2 miniHS2 = null;

    /**
     * HiveAuthorizationValidator that allows/disallows actions based on
     * allowActions boolean value
     */
    public static class TestAuthValidator extends SQLStdHiveAuthorizationValidator {
        public static boolean allowActions;

        public static final String DENIED_ERR = "Actions not allowed because of allowActions=false";

        public TestAuthValidator(HiveMetastoreClientFactory metastoreClientFactory, HiveConf conf, HiveAuthenticationProvider authenticator, SQLStdHiveAccessControllerWrapper privilegeManager, HiveAuthzSessionContext ctx) throws HiveAuthzPluginException {
            super(metastoreClientFactory, conf, authenticator, privilegeManager, ctx);
        }

        @Override
        public void checkPrivileges(HiveOperationType hiveOpType, List<HivePrivilegeObject> inputHObjs, List<HivePrivilegeObject> outputHObjs, HiveAuthzContext context) throws HiveAccessControlException, HiveAuthzPluginException {
            if (!(TestJdbcMetadataApiAuth.TestAuthValidator.allowActions)) {
                throw new HiveAccessControlException(TestJdbcMetadataApiAuth.TestAuthValidator.DENIED_ERR);
            }
        }
    }

    /**
     * Factory that uses TestAuthValidator
     */
    public static class TestAuthorizerFactory implements HiveAuthorizerFactory {
        @Override
        public HiveAuthorizer createHiveAuthorizer(HiveMetastoreClientFactory metastoreClientFactory, HiveConf conf, HiveAuthenticationProvider authenticator, HiveAuthzSessionContext ctx) throws HiveAuthzPluginException {
            SQLStdHiveAccessControllerWrapper privilegeManager = new SQLStdHiveAccessControllerWrapper(metastoreClientFactory, conf, authenticator, ctx);
            return new org.apache.hadoop.hive.ql.security.authorization.plugin.HiveAuthorizerImpl(privilegeManager, new TestJdbcMetadataApiAuth.TestAuthValidator(metastoreClientFactory, conf, authenticator, privilegeManager, ctx));
        }
    }

    /**
     * Call the HS2 metadata api's with authorizer allowing those calls
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMetaApiAllowed() throws Exception {
        TestJdbcMetadataApiAuth.TestAuthValidator.allowActions = true;
        Connection hs2Conn = TestJdbcMetadataApiAuth.getConnection("user1");
        DatabaseMetaData dbmetadata = hs2Conn.getMetaData();
        ResultSet res;
        res = dbmetadata.getCatalogs();
        Assert.assertFalse(res.next());
        res = dbmetadata.getSchemas();
        Assert.assertTrue(res.next());
        Assert.assertTrue(res.next());
        res = dbmetadata.getTypeInfo();
        Assert.assertTrue(res.next());
        res = dbmetadata.getTables(null, "default", "t%", null);
        Assert.assertTrue(res.next());
        res = dbmetadata.getTableTypes();
        Assert.assertTrue(res.next());
        res = dbmetadata.getColumns(null, "default", "nosuchtable", null);
        Assert.assertFalse(res.next());
        res = dbmetadata.getFunctions(null, null, "trim");
        Assert.assertTrue(res.next());
    }

    /**
     * Call the HS2 metadata api's with authorizer disallowing those calls
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMetaApiDisAllowed() throws Exception {
        TestJdbcMetadataApiAuth.TestAuthValidator.allowActions = false;
        Connection hs2Conn = TestJdbcMetadataApiAuth.getConnection("user1");
        DatabaseMetaData dbmetadata = hs2Conn.getMetaData();
        try {
            dbmetadata.getCatalogs();
            Assert.fail("HiveAccessControlException expected");
        } catch (SQLException e) {
            assertErrorContains(e, TestJdbcMetadataApiAuth.TestAuthValidator.DENIED_ERR);
        } catch (Exception e) {
            Assert.fail("HiveAccessControlException expected");
        }
        try {
            dbmetadata.getSchemas();
            Assert.fail("HiveAccessControlException expected");
        } catch (SQLException e) {
            assertErrorContains(e, TestJdbcMetadataApiAuth.TestAuthValidator.DENIED_ERR);
        } catch (Exception e) {
            Assert.fail("HiveAccessControlException expected");
        }
        try {
            dbmetadata.getTypeInfo();
            Assert.fail("HiveAccessControlException expected");
        } catch (SQLException e) {
            assertErrorContains(e, TestJdbcMetadataApiAuth.TestAuthValidator.DENIED_ERR);
        } catch (Exception e) {
            Assert.fail("HiveAccessControlException expected");
        }
        try {
            dbmetadata.getTables(null, "default", "t%", null);
            Assert.fail("HiveAccessControlException expected");
        } catch (SQLException e) {
            assertErrorContains(e, TestJdbcMetadataApiAuth.TestAuthValidator.DENIED_ERR);
        } catch (Exception e) {
            Assert.fail("HiveAccessControlException expected");
        }
        try {
            dbmetadata.getTableTypes();
            Assert.fail("HiveAccessControlException expected");
        } catch (SQLException e) {
            assertErrorContains(e, TestJdbcMetadataApiAuth.TestAuthValidator.DENIED_ERR);
        } catch (Exception e) {
            Assert.fail("HiveAccessControlException expected");
        }
        try {
            dbmetadata.getColumns(null, "default", "nosuchtable", null);
            Assert.fail("HiveAccessControlException expected");
        } catch (SQLException e) {
            assertErrorContains(e, TestJdbcMetadataApiAuth.TestAuthValidator.DENIED_ERR);
        } catch (Exception e) {
            Assert.fail("HiveAccessControlException expected");
        }
        try {
            dbmetadata.getFunctions(null, null, "trim");
            Assert.fail("HiveAccessControlException expected");
        } catch (SQLException e) {
            assertErrorContains(e, TestJdbcMetadataApiAuth.TestAuthValidator.DENIED_ERR);
        } catch (Exception e) {
            Assert.fail("HiveAccessControlException expected");
        }
    }
}

