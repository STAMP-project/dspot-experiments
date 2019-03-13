/**
 * Copyright 2014-2016 CyberVision, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaaproject.kaa.server.common.nosql.cassandra.dao;


import DirtiesContext.ClassMode;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kaaproject.kaa.common.dto.credentials.CredentialsDto;
import org.kaaproject.kaa.server.common.nosql.cassandra.dao.model.CassandraCredentials;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/cassandra-client-test-context.xml")
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class CredentialsCassandraDaoTest extends AbstractCassandraTest {
    private static final String CREDENTIALS_ID = "credential_id";

    private static final String CREDENTIALS_APPLICATION_ID = "application_id";

    private static final byte[] CREDENTIALS_BODY = "credentials_body".getBytes();

    @Test
    public void testFindCredentialsById() {
        CredentialsDto saved = this.generateCredentials(CredentialsCassandraDaoTest.CREDENTIALS_APPLICATION_ID, CredentialsCassandraDaoTest.CREDENTIALS_ID, CredentialsCassandraDaoTest.CREDENTIALS_BODY, AVAILABLE);
        Assert.assertNotNull(saved);
        Assert.assertNotNull(saved.getId());
        Optional<CassandraCredentials> found = this.credentialsDao.find(CredentialsCassandraDaoTest.CREDENTIALS_APPLICATION_ID, CredentialsCassandraDaoTest.CREDENTIALS_ID);
        Assert.assertTrue(found.isPresent());
        Assert.assertEquals(saved, found.map(Credentials::toDto).get());
    }

    @Test
    public void testUpdateStatus() {
        CredentialsDto credentials = this.generateCredentials(CredentialsCassandraDaoTest.CREDENTIALS_APPLICATION_ID, CredentialsCassandraDaoTest.CREDENTIALS_ID, CredentialsCassandraDaoTest.CREDENTIALS_BODY, AVAILABLE);
        Assert.assertNotNull(credentials);
        Assert.assertNotNull(credentials.getId());
        Optional<CassandraCredentials> updated = this.credentialsDao.updateStatus(CredentialsCassandraDaoTest.CREDENTIALS_APPLICATION_ID, CredentialsCassandraDaoTest.CREDENTIALS_ID, REVOKED);
        Assert.assertTrue(updated.isPresent());
        Assert.assertEquals(REVOKED, updated.get().getStatus());
    }

    @Test
    public void testRemoveCredentials() {
        CredentialsDto credentials = this.generateCredentials(CredentialsCassandraDaoTest.CREDENTIALS_APPLICATION_ID, CredentialsCassandraDaoTest.CREDENTIALS_ID, CredentialsCassandraDaoTest.CREDENTIALS_BODY, AVAILABLE);
        Assert.assertNotNull(credentials);
        Assert.assertNotNull(credentials.getId());
        this.credentialsDao.remove(CredentialsCassandraDaoTest.CREDENTIALS_APPLICATION_ID, credentials.getId());
        Optional<CassandraCredentials> removed = this.credentialsDao.find(CredentialsCassandraDaoTest.CREDENTIALS_APPLICATION_ID, CredentialsCassandraDaoTest.CREDENTIALS_ID);
        Assert.assertFalse(removed.isPresent());
    }

    @Test
    public void testRemoveAllCredentials() {
        CredentialsDto firstCredentials = this.generateCredentials(CredentialsCassandraDaoTest.CREDENTIALS_APPLICATION_ID, CredentialsCassandraDaoTest.CREDENTIALS_ID, CredentialsCassandraDaoTest.CREDENTIALS_BODY, AVAILABLE);
        CredentialsDto secondCredentials = this.generateCredentials(CredentialsCassandraDaoTest.CREDENTIALS_APPLICATION_ID.concat("1"), CredentialsCassandraDaoTest.CREDENTIALS_ID, CredentialsCassandraDaoTest.CREDENTIALS_BODY, AVAILABLE);
        List<CassandraCredentials> credentials = credentialsDao.find();
        Assert.assertEquals(credentials.size(), 2);
        credentialsDao.removeAll();
        List<CassandraCredentials> removed = credentialsDao.find();
        Assert.assertTrue(removed.isEmpty());
    }
}

