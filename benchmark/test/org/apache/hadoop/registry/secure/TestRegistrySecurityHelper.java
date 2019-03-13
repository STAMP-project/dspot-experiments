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
package org.apache.hadoop.registry.secure;


import RegistryConstants.DEFAULT_REGISTRY_SYSTEM_ACCOUNTS;
import RegistrySecurity.ALL_READWRITE_ACCESS;
import RegistrySecurity.E_NO_KERBEROS;
import ZooDefs.Perms.ALL;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.registry.client.impl.zk.RegistrySecurity;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.zookeeper.data.ACL;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test for registry security operations
 */
public class TestRegistrySecurityHelper extends Assert {
    private static final Logger LOG = LoggerFactory.getLogger(TestRegistrySecurityHelper.class);

    public static final String YARN_EXAMPLE_COM = "yarn@example.com";

    public static final String SASL_YARN_EXAMPLE_COM = "sasl:" + (TestRegistrySecurityHelper.YARN_EXAMPLE_COM);

    public static final String MAPRED_EXAMPLE_COM = "mapred@example.com";

    public static final String SASL_MAPRED_EXAMPLE_COM = "sasl:" + (TestRegistrySecurityHelper.MAPRED_EXAMPLE_COM);

    public static final String SASL_MAPRED_APACHE = "sasl:mapred@APACHE";

    public static final String DIGEST_F0AF = "digest:f0afbeeb00baa";

    public static final String SASL_YARN_SHORT = "sasl:yarn@";

    public static final String SASL_MAPRED_SHORT = "sasl:mapred@";

    public static final String REALM_EXAMPLE_COM = "example.com";

    private static RegistrySecurity registrySecurity;

    @Test
    public void testACLSplitRealmed() throws Throwable {
        List<String> pairs = TestRegistrySecurityHelper.registrySecurity.splitAclPairs((((TestRegistrySecurityHelper.SASL_YARN_EXAMPLE_COM) + ", ") + (TestRegistrySecurityHelper.SASL_MAPRED_EXAMPLE_COM)), "");
        Assert.assertEquals(TestRegistrySecurityHelper.SASL_YARN_EXAMPLE_COM, pairs.get(0));
        Assert.assertEquals(TestRegistrySecurityHelper.SASL_MAPRED_EXAMPLE_COM, pairs.get(1));
    }

    @Test
    public void testBuildAclsRealmed() throws Throwable {
        List<ACL> acls = TestRegistrySecurityHelper.registrySecurity.buildACLs((((TestRegistrySecurityHelper.SASL_YARN_EXAMPLE_COM) + ", ") + (TestRegistrySecurityHelper.SASL_MAPRED_EXAMPLE_COM)), "", ALL);
        Assert.assertEquals(TestRegistrySecurityHelper.YARN_EXAMPLE_COM, acls.get(0).getId().getId());
        Assert.assertEquals(TestRegistrySecurityHelper.MAPRED_EXAMPLE_COM, acls.get(1).getId().getId());
    }

    @Test
    public void testACLDefaultRealm() throws Throwable {
        List<String> pairs = TestRegistrySecurityHelper.registrySecurity.splitAclPairs((((TestRegistrySecurityHelper.SASL_YARN_SHORT) + ", ") + (TestRegistrySecurityHelper.SASL_MAPRED_SHORT)), TestRegistrySecurityHelper.REALM_EXAMPLE_COM);
        Assert.assertEquals(TestRegistrySecurityHelper.SASL_YARN_EXAMPLE_COM, pairs.get(0));
        Assert.assertEquals(TestRegistrySecurityHelper.SASL_MAPRED_EXAMPLE_COM, pairs.get(1));
    }

    @Test
    public void testBuildAclsDefaultRealm() throws Throwable {
        List<ACL> acls = TestRegistrySecurityHelper.registrySecurity.buildACLs((((TestRegistrySecurityHelper.SASL_YARN_SHORT) + ", ") + (TestRegistrySecurityHelper.SASL_MAPRED_SHORT)), TestRegistrySecurityHelper.REALM_EXAMPLE_COM, ALL);
        Assert.assertEquals(TestRegistrySecurityHelper.YARN_EXAMPLE_COM, acls.get(0).getId().getId());
        Assert.assertEquals(TestRegistrySecurityHelper.MAPRED_EXAMPLE_COM, acls.get(1).getId().getId());
    }

    @Test
    public void testACLSplitNullRealm() throws Throwable {
        List<String> pairs = TestRegistrySecurityHelper.registrySecurity.splitAclPairs((((TestRegistrySecurityHelper.SASL_YARN_SHORT) + ", ") + (TestRegistrySecurityHelper.SASL_MAPRED_SHORT)), "");
        Assert.assertEquals(TestRegistrySecurityHelper.SASL_YARN_SHORT, pairs.get(0));
        Assert.assertEquals(TestRegistrySecurityHelper.SASL_MAPRED_SHORT, pairs.get(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildAclsNullRealm() throws Throwable {
        TestRegistrySecurityHelper.registrySecurity.buildACLs((((TestRegistrySecurityHelper.SASL_YARN_SHORT) + ", ") + (TestRegistrySecurityHelper.SASL_MAPRED_SHORT)), "", ALL);
        Assert.fail("");
    }

    @Test
    public void testACLDefaultRealmOnlySASL() throws Throwable {
        List<String> pairs = TestRegistrySecurityHelper.registrySecurity.splitAclPairs((((TestRegistrySecurityHelper.SASL_YARN_SHORT) + ", ") + (TestRegistrySecurityHelper.DIGEST_F0AF)), TestRegistrySecurityHelper.REALM_EXAMPLE_COM);
        Assert.assertEquals(TestRegistrySecurityHelper.SASL_YARN_EXAMPLE_COM, pairs.get(0));
        Assert.assertEquals(TestRegistrySecurityHelper.DIGEST_F0AF, pairs.get(1));
    }

    @Test
    public void testACLSplitMixed() throws Throwable {
        List<String> pairs = TestRegistrySecurityHelper.registrySecurity.splitAclPairs((((((TestRegistrySecurityHelper.SASL_YARN_SHORT) + ", ") + (TestRegistrySecurityHelper.SASL_MAPRED_APACHE)) + ", ,,") + (TestRegistrySecurityHelper.DIGEST_F0AF)), TestRegistrySecurityHelper.REALM_EXAMPLE_COM);
        Assert.assertEquals(TestRegistrySecurityHelper.SASL_YARN_EXAMPLE_COM, pairs.get(0));
        Assert.assertEquals(TestRegistrySecurityHelper.SASL_MAPRED_APACHE, pairs.get(1));
        Assert.assertEquals(TestRegistrySecurityHelper.DIGEST_F0AF, pairs.get(2));
    }

    @Test
    public void testDefaultAClsValid() throws Throwable {
        TestRegistrySecurityHelper.registrySecurity.buildACLs(DEFAULT_REGISTRY_SYSTEM_ACCOUNTS, TestRegistrySecurityHelper.REALM_EXAMPLE_COM, ALL);
    }

    @Test
    public void testDefaultRealm() throws Throwable {
        String realm = RegistrySecurity.getDefaultRealmInJVM();
        TestRegistrySecurityHelper.LOG.info("Realm {}", realm);
    }

    @Test
    public void testUGIProperties() throws Throwable {
        UserGroupInformation user = UserGroupInformation.getCurrentUser();
        ACL acl = TestRegistrySecurityHelper.registrySecurity.createACLForUser(user, ALL);
        Assert.assertFalse(ALL_READWRITE_ACCESS.equals(acl));
        TestRegistrySecurityHelper.LOG.info("User {} has ACL {}", user, acl);
    }

    @Test
    public void testSecurityImpliesKerberos() throws Throwable {
        Configuration conf = new Configuration();
        conf.setBoolean("hadoop.security.authentication", true);
        conf.setBoolean(KEY_REGISTRY_SECURE, true);
        conf.set(KEY_REGISTRY_KERBEROS_REALM, "KERBEROS");
        RegistrySecurity security = new RegistrySecurity("registry security");
        try {
            security.init(conf);
        } catch (Exception e) {
            Assert.assertTrue(((("did not find " + (RegistrySecurity.E_NO_KERBEROS)) + " in ") + e), e.toString().contains(E_NO_KERBEROS));
        }
    }
}

