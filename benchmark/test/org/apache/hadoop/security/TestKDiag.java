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
package org.apache.hadoop.security;


import UserGroupInformation.AuthenticationMethod.SIMPLE;
import java.io.File;
import java.util.Properties;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.minikdc.MiniKdc;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestKDiag extends Assert {
    private static final Logger LOG = LoggerFactory.getLogger(TestKDiag.class);

    public static final String KEYLEN = "128";

    public static final String HDFS_SITE_XML = "org/apache/hadoop/security/secure-hdfs-site.xml";

    @Rule
    public TestName methodName = new TestName();

    @Rule
    public Timeout testTimeout = new Timeout(30000);

    private static MiniKdc kdc;

    private static File workDir;

    private static File keytab;

    private static Properties securityProperties;

    private static Configuration conf;

    @Test
    public void testBasicLoginFailure() throws Throwable {
        kdiagFailure(CAT_LOGIN, ARG_KEYLEN, TestKDiag.KEYLEN);
    }

    @Test
    public void testBasicLoginSkipped() throws Throwable {
        kdiagFailure(CAT_LOGIN, ARG_KEYLEN, TestKDiag.KEYLEN, ARG_NOLOGIN);
    }

    /**
     * This fails as the default cluster config is checked along with
     * the CLI
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testSecure() throws Throwable {
        kdiagFailure(CAT_CONFIG, ARG_KEYLEN, TestKDiag.KEYLEN, ARG_SECURE);
    }

    @Test
    public void testNoKeytab() throws Throwable {
        kdiagFailure(CAT_KERBEROS, ARG_KEYLEN, TestKDiag.KEYLEN, ARG_KEYTAB, "target/nofile");
    }

    @Test
    public void testKeytabNoPrincipal() throws Throwable {
        kdiagFailure(CAT_KERBEROS, ARG_KEYLEN, TestKDiag.KEYLEN, ARG_KEYTAB, TestKDiag.keytab.getAbsolutePath());
    }

    @Test
    public void testConfIsSecure() throws Throwable {
        Assert.assertFalse(SecurityUtil.getAuthenticationMethod(TestKDiag.conf).equals(SIMPLE));
    }

    @Test
    public void testKeytabAndPrincipal() throws Throwable {
        kdiag(ARG_KEYLEN, TestKDiag.KEYLEN, ARG_KEYTAB, TestKDiag.keytab.getAbsolutePath(), ARG_PRINCIPAL, "foo@EXAMPLE.COM");
    }

    @Test
    public void testKerberosName() throws Throwable {
        kdiagFailure(ARG_KEYLEN, TestKDiag.KEYLEN, ARG_VERIFYSHORTNAME, ARG_PRINCIPAL, "foo/foo/foo@BAR.COM");
    }

    @Test
    public void testShortName() throws Throwable {
        kdiag(ARG_KEYLEN, TestKDiag.KEYLEN, ARG_KEYTAB, TestKDiag.keytab.getAbsolutePath(), ARG_PRINCIPAL, ARG_VERIFYSHORTNAME, ARG_PRINCIPAL, "foo@EXAMPLE.COM");
    }

    @Test
    public void testFileOutput() throws Throwable {
        File f = new File("target/kdiag.txt");
        kdiag(ARG_KEYLEN, TestKDiag.KEYLEN, ARG_KEYTAB, TestKDiag.keytab.getAbsolutePath(), ARG_PRINCIPAL, "foo@EXAMPLE.COM", ARG_OUTPUT, f.getAbsolutePath());
        TestKDiag.LOG.info("Output of {}", f);
        dump(f);
    }

    @Test
    public void testLoadResource() throws Throwable {
        kdiag(ARG_KEYLEN, TestKDiag.KEYLEN, ARG_RESOURCE, TestKDiag.HDFS_SITE_XML, ARG_KEYTAB, TestKDiag.keytab.getAbsolutePath(), ARG_PRINCIPAL, "foo@EXAMPLE.COM");
    }

    @Test
    public void testLoadInvalidResource() throws Throwable {
        kdiagFailure(CAT_CONFIG, ARG_KEYLEN, TestKDiag.KEYLEN, ARG_RESOURCE, "no-such-resource.xml", ARG_KEYTAB, TestKDiag.keytab.getAbsolutePath(), ARG_PRINCIPAL, "foo@EXAMPLE.COM");
    }

    @Test
    public void testRequireJAAS() throws Throwable {
        kdiagFailure(CAT_JAAS, ARG_KEYLEN, TestKDiag.KEYLEN, ARG_JAAS, ARG_KEYTAB, TestKDiag.keytab.getAbsolutePath(), ARG_PRINCIPAL, "foo@EXAMPLE.COM");
    }
}

