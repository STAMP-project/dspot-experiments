/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.rpc.user.security;


import DrillProperties.KERBEROS_FROM_SUBJECT;
import DrillProperties.KEYTAB;
import DrillProperties.SERVICE_PRINCIPAL;
import DrillProperties.USER;
import java.security.PrivilegedExceptionAction;
import java.util.Properties;
import javax.security.auth.Subject;
import junit.framework.TestCase;
import org.apache.drill.categories.SecurityTest;
import org.apache.drill.exec.rpc.control.ControlRpcMetrics;
import org.apache.drill.exec.rpc.data.DataRpcMetrics;
import org.apache.drill.exec.rpc.security.KerberosHelper;
import org.apache.drill.exec.rpc.user.UserRpcMetrics;
import org.apache.drill.test.BaseTestQuery;
import org.apache.kerby.kerberos.kerb.client.JaasKrbUtil;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Ignore("See DRILL-5387")
@Category(SecurityTest.class)
public class TestUserBitKerberos extends BaseTestQuery {
    // private static final org.slf4j.Logger logger =org.slf4j.LoggerFactory.getLogger(TestUserBitKerberos.class);
    private static KerberosHelper krbHelper;

    @Test
    public void successKeytab() throws Exception {
        final Properties connectionProps = new Properties();
        connectionProps.setProperty(SERVICE_PRINCIPAL, TestUserBitKerberos.krbHelper.SERVER_PRINCIPAL);
        connectionProps.setProperty(USER, TestUserBitKerberos.krbHelper.CLIENT_PRINCIPAL);
        connectionProps.setProperty(KEYTAB, TestUserBitKerberos.krbHelper.clientKeytab.getAbsolutePath());
        BaseTestQuery.updateClient(connectionProps);
        // Run few queries using the new client
        BaseTestQuery.testBuilder().sqlQuery("SELECT session_user FROM (SELECT * FROM sys.drillbits LIMIT 1)").unOrdered().baselineColumns("session_user").baselineValues(TestUserBitKerberos.krbHelper.CLIENT_SHORT_NAME).go();
        BaseTestQuery.test("SHOW SCHEMAS");
        BaseTestQuery.test("USE INFORMATION_SCHEMA");
        BaseTestQuery.test("SHOW TABLES");
        BaseTestQuery.test("SELECT * FROM INFORMATION_SCHEMA.`TABLES` WHERE TABLE_NAME LIKE 'COLUMNS'");
        BaseTestQuery.test("SELECT * FROM cp.`region.json` LIMIT 5");
    }

    @Test
    public void successTicket() throws Exception {
        final Properties connectionProps = new Properties();
        connectionProps.setProperty(SERVICE_PRINCIPAL, TestUserBitKerberos.krbHelper.SERVER_PRINCIPAL);
        connectionProps.setProperty(KERBEROS_FROM_SUBJECT, "true");
        final Subject clientSubject = JaasKrbUtil.loginUsingKeytab(TestUserBitKerberos.krbHelper.CLIENT_PRINCIPAL, TestUserBitKerberos.krbHelper.clientKeytab.getAbsoluteFile());
        Subject.doAs(clientSubject, new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                BaseTestQuery.updateClient(connectionProps);
                return null;
            }
        });
        // Run few queries using the new client
        BaseTestQuery.testBuilder().sqlQuery("SELECT session_user FROM (SELECT * FROM sys.drillbits LIMIT 1)").unOrdered().baselineColumns("session_user").baselineValues(TestUserBitKerberos.krbHelper.CLIENT_SHORT_NAME).go();
        BaseTestQuery.test("SHOW SCHEMAS");
        BaseTestQuery.test("USE INFORMATION_SCHEMA");
        BaseTestQuery.test("SHOW TABLES");
        BaseTestQuery.test("SELECT * FROM INFORMATION_SCHEMA.`TABLES` WHERE TABLE_NAME LIKE 'COLUMNS'");
        BaseTestQuery.test("SELECT * FROM cp.`region.json` LIMIT 5");
    }

    @Test
    public void testUnecryptedConnectionCounter() throws Exception {
        final Properties connectionProps = new Properties();
        connectionProps.setProperty(SERVICE_PRINCIPAL, TestUserBitKerberos.krbHelper.SERVER_PRINCIPAL);
        connectionProps.setProperty(KERBEROS_FROM_SUBJECT, "true");
        final Subject clientSubject = JaasKrbUtil.loginUsingKeytab(TestUserBitKerberos.krbHelper.CLIENT_PRINCIPAL, TestUserBitKerberos.krbHelper.clientKeytab.getAbsoluteFile());
        Subject.doAs(clientSubject, new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                BaseTestQuery.updateClient(connectionProps);
                return null;
            }
        });
        // Run few queries using the new client
        BaseTestQuery.testBuilder().sqlQuery("SELECT session_user FROM (SELECT * FROM sys.drillbits LIMIT 1)").unOrdered().baselineColumns("session_user").baselineValues(TestUserBitKerberos.krbHelper.CLIENT_SHORT_NAME).go();
        // Check encrypted counters value
        TestCase.assertTrue((0 == (UserRpcMetrics.getInstance().getEncryptedConnectionCount())));
        TestCase.assertTrue((0 == (ControlRpcMetrics.getInstance().getEncryptedConnectionCount())));
        TestCase.assertTrue((0 == (DataRpcMetrics.getInstance().getEncryptedConnectionCount())));
        // Check unencrypted counters value
        TestCase.assertTrue((1 == (UserRpcMetrics.getInstance().getUnEncryptedConnectionCount())));
        TestCase.assertTrue((0 == (ControlRpcMetrics.getInstance().getUnEncryptedConnectionCount())));
        TestCase.assertTrue((0 == (DataRpcMetrics.getInstance().getUnEncryptedConnectionCount())));
    }

    @Test
    public void testUnecryptedConnectionCounter_LocalControlMessage() throws Exception {
        final Properties connectionProps = new Properties();
        connectionProps.setProperty(SERVICE_PRINCIPAL, TestUserBitKerberos.krbHelper.SERVER_PRINCIPAL);
        connectionProps.setProperty(KERBEROS_FROM_SUBJECT, "true");
        final Subject clientSubject = JaasKrbUtil.loginUsingKeytab(TestUserBitKerberos.krbHelper.CLIENT_PRINCIPAL, TestUserBitKerberos.krbHelper.clientKeytab.getAbsoluteFile());
        Subject.doAs(clientSubject, new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                BaseTestQuery.updateClient(connectionProps);
                return null;
            }
        });
        // Run query on memory system table this sends remote fragments to all Drillbit and Drillbits then send data
        // using data channel. In this test we have only 1 Drillbit so there should not be any control connection but a
        // local data connections
        BaseTestQuery.testSql("SELECT * FROM sys.memory");
        // Check encrypted counters value
        TestCase.assertTrue((0 == (UserRpcMetrics.getInstance().getEncryptedConnectionCount())));
        TestCase.assertTrue((0 == (ControlRpcMetrics.getInstance().getEncryptedConnectionCount())));
        TestCase.assertTrue((0 == (DataRpcMetrics.getInstance().getEncryptedConnectionCount())));
        // Check unencrypted counters value
        TestCase.assertTrue((1 == (UserRpcMetrics.getInstance().getUnEncryptedConnectionCount())));
        TestCase.assertTrue((0 == (ControlRpcMetrics.getInstance().getUnEncryptedConnectionCount())));
        TestCase.assertTrue((2 == (DataRpcMetrics.getInstance().getUnEncryptedConnectionCount())));
    }
}

