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
package org.apache.hadoop.yarn.server.resourcemanager.webapp;


import CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTHENTICATION;
import Status.OK;
import YarnConfiguration.DEFAULT_RM_AM_MAX_ATTEMPTS;
import YarnConfiguration.RM_AM_MAX_ATTEMPTS;
import YarnConfiguration.RM_KEYTAB;
import YarnConfiguration.RM_SCHEDULER;
import YarnConfiguration.YARN_ACL_ENABLE;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.minikdc.MiniKdc;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.authentication.KerberosTestUtils;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fifo.FifoScheduler;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/* Just a simple test class to ensure that the RM handles the static web user
correctly for secure and un-secure modes
 */
@RunWith(Parameterized.class)
public class TestRMWebappAuthentication {
    private static MockRM rm;

    private static Configuration simpleConf;

    private static Configuration kerberosConf;

    private static final File testRootDir = new File("target", ((TestRMWebServicesDelegationTokenAuthentication.class.getName()) + "-root"));

    private static File httpSpnegoKeytabFile = new File(KerberosTestUtils.getKeytabFile());

    private static boolean miniKDCStarted = false;

    private static MiniKdc testMiniKDC;

    static {
        TestRMWebappAuthentication.simpleConf = new Configuration();
        TestRMWebappAuthentication.simpleConf.setInt(RM_AM_MAX_ATTEMPTS, DEFAULT_RM_AM_MAX_ATTEMPTS);
        TestRMWebappAuthentication.simpleConf.setClass(RM_SCHEDULER, FifoScheduler.class, ResourceScheduler.class);
        TestRMWebappAuthentication.simpleConf.setBoolean("mockrm.webapp.enabled", true);
        TestRMWebappAuthentication.kerberosConf = new Configuration();
        TestRMWebappAuthentication.kerberosConf.setInt(RM_AM_MAX_ATTEMPTS, DEFAULT_RM_AM_MAX_ATTEMPTS);
        TestRMWebappAuthentication.kerberosConf.setClass(RM_SCHEDULER, FifoScheduler.class, ResourceScheduler.class);
        TestRMWebappAuthentication.kerberosConf.setBoolean(YARN_ACL_ENABLE, true);
        TestRMWebappAuthentication.kerberosConf.set(HADOOP_SECURITY_AUTHENTICATION, "kerberos");
        TestRMWebappAuthentication.kerberosConf.set(RM_KEYTAB, TestRMWebappAuthentication.httpSpnegoKeytabFile.getAbsolutePath());
        TestRMWebappAuthentication.kerberosConf.setBoolean("mockrm.webapp.enabled", true);
    }

    public TestRMWebappAuthentication(int run, Configuration conf) {
        super();
        TestRMWebappAuthentication.setupAndStartRM(conf);
    }

    // ensure that in a non-secure cluster users can access
    // the web pages as earlier and submit apps as anonymous
    // user or by identifying themselves
    @Test
    public void testSimpleAuth() throws Exception {
        start();
        // ensure users can access web pages
        // this should work for secure and non-secure clusters
        URL url = new URL("http://localhost:8088/cluster");
        HttpURLConnection conn = ((HttpURLConnection) (url.openConnection()));
        try {
            conn.getInputStream();
            Assert.assertEquals(OK.getStatusCode(), conn.getResponseCode());
        } catch (Exception e) {
            Assert.fail("Fetching url failed");
        }
        if (UserGroupInformation.isSecurityEnabled()) {
            testAnonymousKerberosUser();
        } else {
            testAnonymousSimpleUser();
        }
        stop();
    }
}

