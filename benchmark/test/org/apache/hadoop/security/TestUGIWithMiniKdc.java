/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.security;


import UserGroupInformation.AuthenticationMethod.KERBEROS;
import UserGroupInformation.LOG;
import UserGroupInformation.metrics;
import java.io.File;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.login.LoginContext;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeysPublic;
import org.apache.hadoop.minikdc.MiniKdc;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.LambdaTestUtils;
import org.junit.Test;
import org.slf4j.event.Level;


/**
 * Test {@link UserGroupInformation} with a minikdc.
 */
public class TestUGIWithMiniKdc {
    private static MiniKdc kdc;

    @Test(timeout = 120000)
    public void testAutoRenewalThreadRetryWithKdc() throws Exception {
        GenericTestUtils.setLogLevel(LOG, Level.DEBUG);
        final Configuration conf = new Configuration();
        // can't rely on standard kinit, else test fails when user running
        // the test is kinit'ed because the test renews _their TGT_.
        conf.set("hadoop.kerberos.kinit.command", "bogus-kinit-cmd");
        // Relogin every 1 second
        conf.setLong(CommonConfigurationKeysPublic.HADOOP_KERBEROS_MIN_SECONDS_BEFORE_RELOGIN, 1);
        SecurityUtil.setAuthenticationMethod(KERBEROS, conf);
        UserGroupInformation.setConfiguration(conf);
        LoginContext loginContext = null;
        try {
            final String principal = "foo";
            final File workDir = new File(System.getProperty("test.dir", "target"));
            final File keytab = new File(workDir, "foo.keytab");
            final Set<Principal> principals = new HashSet<>();
            principals.add(new KerberosPrincipal(principal));
            setupKdc();
            TestUGIWithMiniKdc.kdc.createPrincipal(keytab, principal);
            UserGroupInformation.loginUserFromKeytab(principal, keytab.getPath());
            UserGroupInformation ugi = UserGroupInformation.getLoginUser();
            // no ticket cache, so force the thread to test for failures.
            ugi.spawnAutoRenewalThreadForUserCreds(true);
            // Verify retry happens. Do not verify retry count to reduce flakiness.
            // Detailed back-off logic is tested separately in
            // TestUserGroupInformation#testGetNextRetryTime
            LambdaTestUtils.await(30000, 500, () -> {
                final int count = metrics.getRenewalFailures().value();
                LOG.info("Renew failure count is {}", count);
                return count > 0;
            });
        } finally {
            if (loginContext != null) {
                loginContext.logout();
            }
        }
    }
}

