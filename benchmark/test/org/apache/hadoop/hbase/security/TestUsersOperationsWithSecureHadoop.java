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
package org.apache.hadoop.hbase.security;


import AuthUtil.HBASE_CLIENT_KERBEROS_PRINCIPAL;
import AuthUtil.HBASE_CLIENT_KEYTAB_FILE;
import java.io.File;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.AuthUtil;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.testclassification.SecurityTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.minikdc.MiniKdc;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ SecurityTests.class, SmallTests.class })
public class TestUsersOperationsWithSecureHadoop {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestUsersOperationsWithSecureHadoop.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final File KEYTAB_FILE = new File(getDataTestDir("keytab").toUri().getPath());

    private static MiniKdc KDC;

    private static String HOST = "localhost";

    private static String PRINCIPAL;

    private static String CLIENT_NAME;

    /**
     * test login with security enabled configuration To run this test, we must specify the following
     * system properties:
     * <p>
     * <b> hbase.regionserver.kerberos.principal </b>
     * <p>
     * <b> hbase.regionserver.keytab.file </b>
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testUserLoginInSecureHadoop() throws Exception {
        // Default login is system user.
        UserGroupInformation defaultLogin = UserGroupInformation.getCurrentUser();
        String nnKeyTab = HBaseKerberosUtils.getKeytabFileForTesting();
        String dnPrincipal = HBaseKerberosUtils.getPrincipalForTesting();
        Assert.assertNotNull("KerberosKeytab was not specified", nnKeyTab);
        Assert.assertNotNull("KerberosPrincipal was not specified", dnPrincipal);
        Configuration conf = HBaseKerberosUtils.getSecuredConfiguration();
        UserGroupInformation.setConfiguration(conf);
        User.login(conf, HBaseKerberosUtils.KRB_KEYTAB_FILE, HBaseKerberosUtils.KRB_PRINCIPAL, "localhost");
        UserGroupInformation successLogin = UserGroupInformation.getLoginUser();
        Assert.assertFalse("ugi should be different in in case success login", defaultLogin.equals(successLogin));
    }

    @Test
    public void testLoginWithUserKeytabAndPrincipal() throws Exception {
        String clientKeytab = HBaseKerberosUtils.getClientKeytabForTesting();
        String clientPrincipal = HBaseKerberosUtils.getClientPrincipalForTesting();
        Assert.assertNotNull("Path for client keytab is not specified.", clientKeytab);
        Assert.assertNotNull("Client principal is not specified.", clientPrincipal);
        Configuration conf = HBaseKerberosUtils.getSecuredConfiguration();
        conf.set(HBASE_CLIENT_KEYTAB_FILE, clientKeytab);
        conf.set(HBASE_CLIENT_KERBEROS_PRINCIPAL, clientPrincipal);
        UserGroupInformation.setConfiguration(conf);
        UserProvider provider = UserProvider.instantiate(conf);
        Assert.assertTrue("Client principal or keytab is empty", provider.shouldLoginFromKeytab());
        provider.login(HBASE_CLIENT_KEYTAB_FILE, HBASE_CLIENT_KERBEROS_PRINCIPAL);
        User loginUser = provider.getCurrent();
        Assert.assertEquals(TestUsersOperationsWithSecureHadoop.CLIENT_NAME, loginUser.getShortName());
        Assert.assertEquals(HBaseKerberosUtils.getClientPrincipalForTesting(), loginUser.getName());
    }

    @Test
    public void testAuthUtilLogin() throws Exception {
        String clientKeytab = HBaseKerberosUtils.getClientKeytabForTesting();
        String clientPrincipal = HBaseKerberosUtils.getClientPrincipalForTesting();
        Configuration conf = HBaseKerberosUtils.getSecuredConfiguration();
        conf.set(HBASE_CLIENT_KEYTAB_FILE, clientKeytab);
        conf.set(HBASE_CLIENT_KERBEROS_PRINCIPAL, clientPrincipal);
        UserGroupInformation.setConfiguration(conf);
        User user = AuthUtil.loginClient(conf);
        Assert.assertTrue(user.isLoginFromKeytab());
        Assert.assertEquals(TestUsersOperationsWithSecureHadoop.CLIENT_NAME, user.getShortName());
        Assert.assertEquals(HBaseKerberosUtils.getClientPrincipalForTesting(), user.getName());
    }
}

