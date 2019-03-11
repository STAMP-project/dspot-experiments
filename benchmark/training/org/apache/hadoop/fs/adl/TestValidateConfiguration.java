/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.fs.adl;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Validate configuration keys defined for adl storage file system instance.
 */
public class TestValidateConfiguration {
    @Test
    public void validateConfigurationKeys() {
        Assert.assertEquals("fs.adl.oauth2.refresh.url", AdlConfKeys.AZURE_AD_REFRESH_URL_KEY);
        Assert.assertEquals("fs.adl.oauth2.access.token.provider", AdlConfKeys.AZURE_AD_TOKEN_PROVIDER_CLASS_KEY);
        Assert.assertEquals("fs.adl.oauth2.client.id", AdlConfKeys.AZURE_AD_CLIENT_ID_KEY);
        Assert.assertEquals("fs.adl.oauth2.refresh.token", AdlConfKeys.AZURE_AD_REFRESH_TOKEN_KEY);
        Assert.assertEquals("fs.adl.oauth2.credential", AdlConfKeys.AZURE_AD_CLIENT_SECRET_KEY);
        Assert.assertEquals("adl.debug.override.localuserasfileowner", AdlConfKeys.ADL_DEBUG_OVERRIDE_LOCAL_USER_AS_OWNER);
        Assert.assertEquals("fs.adl.oauth2.access.token.provider.type", AdlConfKeys.AZURE_AD_TOKEN_PROVIDER_TYPE_KEY);
        Assert.assertEquals("adl.feature.client.cache.readahead", AdlConfKeys.READ_AHEAD_BUFFER_SIZE_KEY);
        Assert.assertEquals("adl.feature.client.cache.drop.behind.writes", AdlConfKeys.WRITE_BUFFER_SIZE_KEY);
        Assert.assertEquals("RefreshToken", AdlConfKeys.TOKEN_PROVIDER_TYPE_REFRESH_TOKEN);
        Assert.assertEquals("ClientCredential", AdlConfKeys.TOKEN_PROVIDER_TYPE_CLIENT_CRED);
        Assert.assertEquals("adl.enable.client.latency.tracker", AdlConfKeys.LATENCY_TRACKER_KEY);
        Assert.assertEquals(true, AdlConfKeys.LATENCY_TRACKER_DEFAULT);
        Assert.assertEquals(true, AdlConfKeys.ADL_EXPERIMENT_POSITIONAL_READ_DEFAULT);
        Assert.assertEquals("adl.feature.experiment.positional.read.enable", AdlConfKeys.ADL_EXPERIMENT_POSITIONAL_READ_KEY);
        Assert.assertEquals(1, AdlConfKeys.ADL_REPLICATION_FACTOR);
        Assert.assertEquals(((256 * 1024) * 1024), AdlConfKeys.ADL_BLOCK_SIZE);
        Assert.assertEquals(false, AdlConfKeys.ADL_DEBUG_SET_LOCAL_USER_AS_OWNER_DEFAULT);
        Assert.assertEquals(((4 * 1024) * 1024), AdlConfKeys.DEFAULT_READ_AHEAD_BUFFER_SIZE);
        Assert.assertEquals(((4 * 1024) * 1024), AdlConfKeys.DEFAULT_WRITE_AHEAD_BUFFER_SIZE);
        Assert.assertEquals("adl.feature.ownerandgroup.enableupn", AdlConfKeys.ADL_ENABLEUPN_FOR_OWNERGROUP_KEY);
        Assert.assertEquals(false, AdlConfKeys.ADL_ENABLEUPN_FOR_OWNERGROUP_DEFAULT);
    }

    @Test
    public void testSetDeprecatedKeys() throws ClassNotFoundException {
        Configuration conf = new Configuration(true);
        setDeprecatedKeys(conf);
        // Force AdlFileSystem static initialization to register deprecated keys.
        Class.forName(AdlFileSystem.class.getName());
        assertDeprecatedKeys(conf);
    }

    @Test
    public void testLoadDeprecatedKeys() throws IOException, ClassNotFoundException {
        Configuration saveConf = new Configuration(false);
        setDeprecatedKeys(saveConf);
        final File testRootDir = GenericTestUtils.getTestDir();
        File confXml = new File(testRootDir, "testLoadDeprecatedKeys.xml");
        OutputStream out = new FileOutputStream(confXml);
        saveConf.writeXml(out);
        out.close();
        Configuration conf = new Configuration(true);
        conf.addResource(confXml.toURI().toURL());
        // Trigger loading the configuration resources by getting any key.
        conf.get("dummy.key");
        // Force AdlFileSystem static initialization to register deprecated keys.
        Class.forName(AdlFileSystem.class.getName());
        assertDeprecatedKeys(conf);
    }

    @Test
    public void testGetAccountNameFromFQDN() {
        Assert.assertEquals("dummy", AdlFileSystem.getAccountNameFromFQDN("dummy.azuredatalakestore.net"));
        Assert.assertEquals("localhost", AdlFileSystem.getAccountNameFromFQDN("localhost"));
    }

    @Test
    public void testPropagateAccountOptionsDefault() {
        Configuration conf = new Configuration(false);
        conf.set("fs.adl.oauth2.client.id", "defaultClientId");
        conf.set("fs.adl.oauth2.credential", "defaultCredential");
        conf.set("some.other.config", "someValue");
        Configuration propagatedConf = AdlFileSystem.propagateAccountOptions(conf, "dummy");
        Assert.assertEquals("defaultClientId", propagatedConf.get(AdlConfKeys.AZURE_AD_CLIENT_ID_KEY));
        Assert.assertEquals("defaultCredential", propagatedConf.get(AdlConfKeys.AZURE_AD_CLIENT_SECRET_KEY));
        Assert.assertEquals("someValue", propagatedConf.get("some.other.config"));
    }

    @Test
    public void testPropagateAccountOptionsSpecified() {
        Configuration conf = new Configuration(false);
        conf.set("fs.adl.account.dummy.oauth2.client.id", "dummyClientId");
        conf.set("fs.adl.account.dummy.oauth2.credential", "dummyCredential");
        conf.set("some.other.config", "someValue");
        Configuration propagatedConf = AdlFileSystem.propagateAccountOptions(conf, "dummy");
        Assert.assertEquals("dummyClientId", propagatedConf.get(AdlConfKeys.AZURE_AD_CLIENT_ID_KEY));
        Assert.assertEquals("dummyCredential", propagatedConf.get(AdlConfKeys.AZURE_AD_CLIENT_SECRET_KEY));
        Assert.assertEquals("someValue", propagatedConf.get("some.other.config"));
        propagatedConf = AdlFileSystem.propagateAccountOptions(conf, "anotherDummy");
        Assert.assertEquals(null, propagatedConf.get(AdlConfKeys.AZURE_AD_CLIENT_ID_KEY));
        Assert.assertEquals(null, propagatedConf.get(AdlConfKeys.AZURE_AD_CLIENT_SECRET_KEY));
        Assert.assertEquals("someValue", propagatedConf.get("some.other.config"));
    }

    @Test
    public void testPropagateAccountOptionsAll() {
        Configuration conf = new Configuration(false);
        conf.set("fs.adl.oauth2.client.id", "defaultClientId");
        conf.set("fs.adl.oauth2.credential", "defaultCredential");
        conf.set("some.other.config", "someValue");
        conf.set("fs.adl.account.dummy1.oauth2.client.id", "dummyClientId1");
        conf.set("fs.adl.account.dummy1.oauth2.credential", "dummyCredential1");
        conf.set("fs.adl.account.dummy2.oauth2.client.id", "dummyClientId2");
        conf.set("fs.adl.account.dummy2.oauth2.credential", "dummyCredential2");
        Configuration propagatedConf = AdlFileSystem.propagateAccountOptions(conf, "dummy1");
        Assert.assertEquals("dummyClientId1", propagatedConf.get(AdlConfKeys.AZURE_AD_CLIENT_ID_KEY));
        Assert.assertEquals("dummyCredential1", propagatedConf.get(AdlConfKeys.AZURE_AD_CLIENT_SECRET_KEY));
        Assert.assertEquals("someValue", propagatedConf.get("some.other.config"));
        propagatedConf = AdlFileSystem.propagateAccountOptions(conf, "dummy2");
        Assert.assertEquals("dummyClientId2", propagatedConf.get(AdlConfKeys.AZURE_AD_CLIENT_ID_KEY));
        Assert.assertEquals("dummyCredential2", propagatedConf.get(AdlConfKeys.AZURE_AD_CLIENT_SECRET_KEY));
        Assert.assertEquals("someValue", propagatedConf.get("some.other.config"));
        propagatedConf = AdlFileSystem.propagateAccountOptions(conf, "anotherDummy");
        Assert.assertEquals("defaultClientId", propagatedConf.get(AdlConfKeys.AZURE_AD_CLIENT_ID_KEY));
        Assert.assertEquals("defaultCredential", propagatedConf.get(AdlConfKeys.AZURE_AD_CLIENT_SECRET_KEY));
        Assert.assertEquals("someValue", propagatedConf.get("some.other.config"));
    }
}

