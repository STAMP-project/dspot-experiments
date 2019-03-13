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
package org.apache.hadoop.hive.ql.security;


import java.io.File;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.shims.ShimLoader;
import org.junit.Assert;
import org.junit.Test;


public class TestPasswordWithCredentialProvider {
    private static final File tmpDir = new File(System.getProperty("test.tmp.dir"), "creds");

    @Test
    public void testPassword() throws Exception {
        if (!(TestPasswordWithCredentialProvider.doesHadoopPasswordAPIExist())) {
            System.out.println(("Skipping Password API test" + " because this version of hadoop-2 does not support the password API."));
            return;
        }
        String credName = "my.password";
        String credName2 = "my.password2";
        String credName3 = "my.password3";
        String hiveConfPassword = "conf value";
        String credPassword = "cred value";
        String confOnlyPassword = "abcdefg";
        String credOnlyPassword = "12345";
        // Set up conf
        Configuration conf = new Configuration();
        conf.set(credName, hiveConfPassword);// Will be superceded by credential provider

        conf.set(credName2, confOnlyPassword);// Will not be superceded

        Assert.assertEquals(hiveConfPassword, conf.get(credName));
        Assert.assertEquals(confOnlyPassword, conf.get(credName2));
        Assert.assertNull("credName3 should not exist in HiveConf", conf.get(credName3));
        // Configure getPassword() to fall back to conf if credential doesn't have entry
        conf.set("hadoop.security.credential.clear-text-fallback", "true");
        // Set up CredentialProvider
        conf.set("hadoop.security.credential.provider.path", (("jceks://file/" + (TestPasswordWithCredentialProvider.tmpDir.toURI().getPath())) + "/test.jks"));
        // CredentialProvider/CredentialProviderFactory may not exist, depending on the version of
        // hadoop-2 being used to build Hive. Use reflection to do the following lines
        // to allow the test to compile regardless of what version of hadoop-2.
        // Update credName entry in the credential provider.
        // CredentialProvider provider = CredentialProviderFactory.getProviders(conf).get(0);
        // provider.createCredentialEntry(credName, credPassword.toCharArray());
        // provider.createCredentialEntry(credName3, credOnlyPassword.toCharArray());
        // provider.flush();
        Class credentialProviderClass = Class.forName("org.apache.hadoop.security.alias.CredentialProvider");
        Class credentialProviderFactoryClass = Class.forName("org.apache.hadoop.security.alias.CredentialProviderFactory");
        Object provider = ((List) (TestPasswordWithCredentialProvider.invoke(credentialProviderFactoryClass, null, "getProviders", conf))).get(0);
        TestPasswordWithCredentialProvider.invoke(credentialProviderClass, provider, "createCredentialEntry", credName, credPassword.toCharArray());
        TestPasswordWithCredentialProvider.invoke(credentialProviderClass, provider, "createCredentialEntry", credName3, credOnlyPassword.toCharArray());
        TestPasswordWithCredentialProvider.invoke(credentialProviderClass, provider, "flush");
        // If credential provider has entry for our credential, then it should be used
        Assert.assertEquals("getPassword() should use match value in credential provider", credPassword, ShimLoader.getHadoopShims().getPassword(conf, credName));
        // If cred provider doesn't have entry, fall back to conf
        Assert.assertEquals("getPassword() should match value from conf", confOnlyPassword, ShimLoader.getHadoopShims().getPassword(conf, credName2));
        // If cred provider has entry and conf does not, cred provider is used.
        // This is our use case of not having passwords stored in in the clear in hive conf files.
        Assert.assertEquals("getPassword() should use credential provider if conf has no value", credOnlyPassword, ShimLoader.getHadoopShims().getPassword(conf, credName3));
        // If neither cred provider or conf have entry, return null;
        Assert.assertNull("null if neither cred provider or conf have entry", ShimLoader.getHadoopShims().getPassword(conf, "nonexistentkey"));
    }
}

