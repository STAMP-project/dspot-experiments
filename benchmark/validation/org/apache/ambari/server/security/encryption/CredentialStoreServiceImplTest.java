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
package org.apache.ambari.server.security.encryption;


import CredentialStoreType.PERSISTED;
import CredentialStoreType.TEMPORARY;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import junit.framework.Assert;
import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.configuration.Configuration;
import org.apache.ambari.server.security.SecurePasswordHelper;
import org.apache.ambari.server.security.credential.PrincipalKeyCredential;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class CredentialStoreServiceImplTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    private CredentialStoreServiceImpl credentialStoreService;

    private static final String CLUSTER_NAME = "C1";

    @Test
    public void testSetAndGetCredential_Temporary() throws Exception {
        PrincipalKeyCredential credential = new PrincipalKeyCredential("username", "password");
        credentialStoreService.setCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1", credential, TEMPORARY);
        Assert.assertEquals(credential, credentialStoreService.getCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1", TEMPORARY));
        Assert.assertEquals(credential, credentialStoreService.getCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1"));
        Assert.assertNull(credentialStoreService.getCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1", PERSISTED));
    }

    @Test
    public void testSetAndGetCredential_Persisted() throws Exception {
        PrincipalKeyCredential credential = new PrincipalKeyCredential("username", "password");
        credentialStoreService.setCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1", credential, PERSISTED);
        Assert.assertEquals(credential, credentialStoreService.getCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1", PERSISTED));
        Assert.assertEquals(credential, credentialStoreService.getCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1"));
        Assert.assertNull(credentialStoreService.getCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1", TEMPORARY));
    }

    @Test
    public void testRemoveCredential_Temporary() throws Exception {
        PrincipalKeyCredential credential1 = new PrincipalKeyCredential("username1", "password1");
        PrincipalKeyCredential credential2 = new PrincipalKeyCredential("username2", "password2");
        credentialStoreService.setCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1", credential1, TEMPORARY);
        credentialStoreService.setCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test2", credential2, TEMPORARY);
        // Nothing should happen if forcing remove from persistent CredentialStore
        credentialStoreService.removeCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1", PERSISTED);
        Assert.assertEquals(credential1, credentialStoreService.getCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1", TEMPORARY));
        Assert.assertEquals(credential1, credentialStoreService.getCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1"));
        // Remove should happen if forcing remove from temporary CredentialStore
        credentialStoreService.removeCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1", TEMPORARY);
        Assert.assertNull(credentialStoreService.getCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1", TEMPORARY));
        Assert.assertNull(credentialStoreService.getCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1"));
        // The other credentials should remain untouched
        Assert.assertEquals(credential2, credentialStoreService.getCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test2"));
    }

    @Test
    public void testRemoveCredential_Persisted() throws Exception {
        PrincipalKeyCredential credential1 = new PrincipalKeyCredential("username1", "password1");
        PrincipalKeyCredential credential2 = new PrincipalKeyCredential("username2", "password2");
        credentialStoreService.setCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1", credential1, PERSISTED);
        credentialStoreService.setCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test2", credential2, PERSISTED);
        // Nothing should happen if forcing remove from temporary CredentialStore
        credentialStoreService.removeCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1", TEMPORARY);
        Assert.assertEquals(credential1, credentialStoreService.getCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1", PERSISTED));
        Assert.assertEquals(credential1, credentialStoreService.getCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1"));
        // Remove should happen if forcing remove from persistent CredentialStore
        credentialStoreService.removeCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1", PERSISTED);
        Assert.assertNull(credentialStoreService.getCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1", PERSISTED));
        Assert.assertNull(credentialStoreService.getCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1"));
        // The other credentials should remain untouched
        Assert.assertEquals(credential2, credentialStoreService.getCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test2"));
    }

    @Test
    public void testRemoveCredential_Either() throws Exception {
        PrincipalKeyCredential credential1 = new PrincipalKeyCredential("username1", "password1");
        PrincipalKeyCredential credential2 = new PrincipalKeyCredential("username2", "password2");
        PrincipalKeyCredential credential3 = new PrincipalKeyCredential("username3", "password3");
        PrincipalKeyCredential credential4 = new PrincipalKeyCredential("username4", "password4");
        credentialStoreService.setCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1", credential1, PERSISTED);
        credentialStoreService.setCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test2", credential2, PERSISTED);
        credentialStoreService.setCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test3", credential3, TEMPORARY);
        credentialStoreService.setCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test4", credential4, TEMPORARY);
        credentialStoreService.removeCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1");
        Assert.assertNull(credentialStoreService.getCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1", PERSISTED));
        Assert.assertNull(credentialStoreService.getCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1"));
        Assert.assertEquals(credential2, credentialStoreService.getCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test2"));
        Assert.assertEquals(credential3, credentialStoreService.getCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test3"));
        Assert.assertEquals(credential4, credentialStoreService.getCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test4"));
        credentialStoreService.removeCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test3");
        Assert.assertNull(credentialStoreService.getCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1"));
        Assert.assertEquals(credential2, credentialStoreService.getCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test2"));
        Assert.assertNull(credentialStoreService.getCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test3"));
        Assert.assertEquals(credential4, credentialStoreService.getCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test4"));
    }

    @Test
    public void testUpdateCredential() throws Exception {
        PrincipalKeyCredential credential1 = new PrincipalKeyCredential("username1", "password1");
        PrincipalKeyCredential credential2 = new PrincipalKeyCredential("username2", "password2");
        credentialStoreService.setCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1", credential1, PERSISTED);
        Assert.assertEquals(credential1, credentialStoreService.getCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1", PERSISTED));
        Assert.assertNull(credentialStoreService.getCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1", TEMPORARY));
        Assert.assertEquals(credential1, credentialStoreService.getCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1"));
        credentialStoreService.setCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1", credential1, TEMPORARY);
        Assert.assertEquals(credential1, credentialStoreService.getCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1", TEMPORARY));
        Assert.assertNull(credentialStoreService.getCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1", PERSISTED));
        Assert.assertEquals(credential1, credentialStoreService.getCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1"));
        credentialStoreService.setCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1", credential2, PERSISTED);
        Assert.assertEquals(credential2, credentialStoreService.getCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1", PERSISTED));
        Assert.assertNull(credentialStoreService.getCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1", TEMPORARY));
        Assert.assertEquals(credential2, credentialStoreService.getCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1"));
    }

    @Test
    public void testContainsCredential() throws Exception {
        PrincipalKeyCredential credential1 = new PrincipalKeyCredential("username1", "password1");
        PrincipalKeyCredential credential2 = new PrincipalKeyCredential("username2", "password2");
        PrincipalKeyCredential credential3 = new PrincipalKeyCredential("username3", "password3");
        PrincipalKeyCredential credential4 = new PrincipalKeyCredential("username4", "password4");
        credentialStoreService.setCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1", credential1, PERSISTED);
        credentialStoreService.setCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test2", credential2, PERSISTED);
        credentialStoreService.setCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test3", credential3, TEMPORARY);
        credentialStoreService.setCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test4", credential4, TEMPORARY);
        Assert.assertTrue(credentialStoreService.containsCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1", PERSISTED));
        Assert.assertFalse(credentialStoreService.containsCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1", TEMPORARY));
        Assert.assertFalse(credentialStoreService.containsCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test3", PERSISTED));
        Assert.assertFalse(credentialStoreService.containsCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1", TEMPORARY));
        Assert.assertFalse(credentialStoreService.containsCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test3", PERSISTED));
        Assert.assertTrue(credentialStoreService.containsCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test3", TEMPORARY));
        Assert.assertTrue(credentialStoreService.containsCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1"));
        Assert.assertTrue(credentialStoreService.containsCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test3"));
    }

    @Test
    public void testIsCredentialPersisted() throws Exception {
        PrincipalKeyCredential credential1 = new PrincipalKeyCredential("username1", "password1");
        PrincipalKeyCredential credential2 = new PrincipalKeyCredential("username2", "password2");
        PrincipalKeyCredential credential3 = new PrincipalKeyCredential("username3", "password3");
        PrincipalKeyCredential credential4 = new PrincipalKeyCredential("username4", "password4");
        credentialStoreService.setCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1", credential1, PERSISTED);
        credentialStoreService.setCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test2", credential2, PERSISTED);
        credentialStoreService.setCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test3", credential3, TEMPORARY);
        credentialStoreService.setCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test4", credential4, TEMPORARY);
        Assert.assertEquals(PERSISTED, credentialStoreService.getCredentialStoreType(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1"));
        Assert.assertEquals(PERSISTED, credentialStoreService.getCredentialStoreType(CredentialStoreServiceImplTest.CLUSTER_NAME, "test2"));
        Assert.assertEquals(TEMPORARY, credentialStoreService.getCredentialStoreType(CredentialStoreServiceImplTest.CLUSTER_NAME, "test3"));
        Assert.assertEquals(TEMPORARY, credentialStoreService.getCredentialStoreType(CredentialStoreServiceImplTest.CLUSTER_NAME, "test4"));
        try {
            credentialStoreService.getCredentialStoreType(CredentialStoreServiceImplTest.CLUSTER_NAME, "test5");
            Assert.fail("Expected AmbariException to be thrown");
        } catch (AmbariException e) {
            // expected
        }
    }

    @Test
    public void testListCredentials() throws Exception {
        PrincipalKeyCredential credential1 = new PrincipalKeyCredential("username1", "password1");
        PrincipalKeyCredential credential2 = new PrincipalKeyCredential("username2", "password2");
        PrincipalKeyCredential credential3 = new PrincipalKeyCredential("username3", "password3");
        PrincipalKeyCredential credential4 = new PrincipalKeyCredential("username4", "password4");
        credentialStoreService.setCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1", credential1, PERSISTED);
        credentialStoreService.setCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test2", credential2, PERSISTED);
        credentialStoreService.setCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test3", credential3, TEMPORARY);
        credentialStoreService.setCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test4", credential4, TEMPORARY);
        Map<String, CredentialStoreType> credentials = credentialStoreService.listCredentials(CredentialStoreServiceImplTest.CLUSTER_NAME);
        Assert.assertNotNull(credentials);
        Assert.assertEquals(4, credentials.size());
        Assert.assertEquals(PERSISTED, credentials.get("test1"));
        Assert.assertEquals(PERSISTED, credentials.get("test2"));
        Assert.assertEquals(TEMPORARY, credentials.get("test3"));
        Assert.assertEquals(TEMPORARY, credentials.get("test4"));
    }

    @Test(expected = AmbariException.class)
    public void testFailToReinitialize_Persisted() throws Exception {
        // This should throw an exception not matter what the arguments are....
        credentialStoreService.initializePersistedCredentialStore(null, null);
    }

    @Test(expected = AmbariException.class)
    public void testFailToReinitialize_Temporary() throws Exception {
        // This should throw an exception not matter what the arguments are....
        credentialStoreService.initializeTemporaryCredentialStore(1, TimeUnit.MINUTES, false);
    }

    @Test
    public void testFailNotInitialized() throws Exception {
        Configuration configuration = new Configuration(new Properties());
        CredentialStoreService uninitializedCredentialStoreService = new CredentialStoreServiceImpl(configuration, new SecurePasswordHelper());
        PrincipalKeyCredential credential1 = new PrincipalKeyCredential("username1", "password1");
        // The temporary store should always be initialized.... this should succeed.
        uninitializedCredentialStoreService.setCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1", credential1, TEMPORARY);
        try {
            uninitializedCredentialStoreService.setCredential(CredentialStoreServiceImplTest.CLUSTER_NAME, "test1", credential1, PERSISTED);
            Assert.fail("AmbariException should have been thrown");
        } catch (AmbariException e) {
            // This is expected...
        }
    }
}

