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


import com.google.common.base.Ticker;
import java.io.File;
import java.util.concurrent.TimeUnit;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class CredentialStoreTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test
    public void testFileBasedCredentialStoreService_AddCredentialToStoreWithPersistMaster() throws Exception {
        addCredentialToStoreWithPersistMasterTest(new CredentialStoreTest.FileBasedCredentialStoreServiceFactory(), new CredentialStoreTest.DefaultMasterKeyServiceFactory());
    }

    @Test
    public void testFileBasedCredentialStoreService_AddCredentialToStore() throws Exception {
        addCredentialToStoreTest(new CredentialStoreTest.FileBasedCredentialStoreServiceFactory(), new CredentialStoreTest.DefaultMasterKeyServiceFactory());
    }

    @Test
    public void testFileBasedCredentialStoreService_GetCredential() throws Exception {
        getCredentialTest(new CredentialStoreTest.FileBasedCredentialStoreServiceFactory(), new CredentialStoreTest.DefaultMasterKeyServiceFactory());
    }

    @Test
    public void testFileBasedCredentialStoreService_RemoveCredential() throws Exception {
        removeCredentialTest(new CredentialStoreTest.FileBasedCredentialStoreServiceFactory(), new CredentialStoreTest.DefaultMasterKeyServiceFactory());
    }

    @Test
    public void testInMemoryCredentialStoreService_AddCredentialToStoreWithPersistMaster() throws Exception {
        addCredentialToStoreWithPersistMasterTest(new CredentialStoreTest.InMemoryCredentialStoreServiceFactory(), new CredentialStoreTest.DefaultMasterKeyServiceFactory());
    }

    @Test
    public void testInMemoryCredentialStoreService_AddCredentialToStore() throws Exception {
        addCredentialToStoreTest(new CredentialStoreTest.InMemoryCredentialStoreServiceFactory(), new CredentialStoreTest.DefaultMasterKeyServiceFactory());
    }

    @Test
    public void testInMemoryCredentialStoreService_GetCredential() throws Exception {
        getCredentialTest(new CredentialStoreTest.InMemoryCredentialStoreServiceFactory(), new CredentialStoreTest.DefaultMasterKeyServiceFactory());
    }

    @Test
    public void testInMemoryCredentialStoreService_RemoveCredential() throws Exception {
        removeCredentialTest(new CredentialStoreTest.InMemoryCredentialStoreServiceFactory(), new CredentialStoreTest.DefaultMasterKeyServiceFactory());
    }

    @Test
    public void testInMemoryCredentialStoreService_CredentialExpired() throws Exception {
        getExpiredCredentialTest(new CredentialStoreTest.InMemoryCredentialStoreServiceFactory(), new CredentialStoreTest.DefaultMasterKeyServiceFactory());
    }

    private interface CredentialStoreServiceFactory {
        CredentialStore create(File path, MasterKeyService masterKeyService);
    }

    private class FileBasedCredentialStoreServiceFactory implements CredentialStoreTest.CredentialStoreServiceFactory {
        @Override
        public CredentialStore create(File path, MasterKeyService masterKeyService) {
            CredentialStore credentialStore = new FileBasedCredentialStore(path);
            credentialStore.setMasterKeyService(masterKeyService);
            return credentialStore;
        }
    }

    private class InMemoryCredentialStoreServiceFactory implements CredentialStoreTest.CredentialStoreServiceFactory {
        @Override
        public CredentialStore create(File path, MasterKeyService masterKeyService) {
            CredentialStore credentialStore = new InMemoryCredentialStore(500, TimeUnit.MILLISECONDS, false);
            credentialStore.setMasterKeyService(masterKeyService);
            return credentialStore;
        }
    }

    private interface MasterKeyServiceFactory {
        MasterKeyService create(String masterKey);

        MasterKeyService createPersisted(File masterKeyFile, String masterKey);
    }

    private class DefaultMasterKeyServiceFactory implements CredentialStoreTest.MasterKeyServiceFactory {
        @Override
        public MasterKeyService create(String masterKey) {
            return new MasterKeyServiceImpl(masterKey);
        }

        @Override
        public MasterKeyService createPersisted(File masterKeyFile, String masterKey) {
            new MasterKeyServiceImpl("dummyKey").initializeMasterKeyFile(masterKeyFile, masterKey);
            return new MasterKeyServiceImpl(masterKeyFile);
        }
    }

    private class TestTicker extends Ticker {
        private long currentNanos;

        public TestTicker(long currentNanos) {
            this.currentNanos = currentNanos;
        }

        @Override
        public long read() {
            return currentNanos;
        }

        public void setCurrentNanos(long currentNanos) {
            this.currentNanos = currentNanos;
        }
    }
}

