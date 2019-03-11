/**
 * Copyright 2018 The Data Transfer Project Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.datatransferproject.transfer;


import JobAuthorization.State.CREDS_ENCRYPTION_KEY_GENERATED;
import State.CREDS_AVAILABLE;
import State.CREDS_STORED;
import State.INITIAL;
import java.security.KeyPair;
import java.util.UUID;
import org.datatransferproject.security.AsymmetricKeyGenerator;
import org.datatransferproject.spi.cloud.storage.JobStore;
import org.datatransferproject.spi.cloud.types.JobAuthorization;
import org.datatransferproject.spi.cloud.types.PortabilityJob;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class JobPollingServiceTest {
    private static final UUID TEST_ID = UUID.randomUUID();

    private static final KeyPair TEST_KEY_PAIR = JobPollingServiceTest.createTestKeyPair();

    @Mock
    private AsymmetricKeyGenerator asymmetricKeyGenerator;

    private JobStore store;

    private JobPollingService jobPollingService;

    // TODO(data-transfer-project/issues/43): Make this an integration test which uses both the API
    // and transfer worker, rather than simulating API calls, in case this test ever diverges from
    // what the API actually does.
    @Test
    public void pollingLifeCycle() throws Exception {
        Mockito.when(asymmetricKeyGenerator.generate()).thenReturn(JobPollingServiceTest.TEST_KEY_PAIR);
        // Initial state
        assertThat(JobMetadata.isInitialized()).isFalse();
        // Run once with no data in the database
        jobPollingService.runOneIteration();
        assertThat(JobMetadata.isInitialized()).isFalse();
        PortabilityJob job = store.findJob(JobPollingServiceTest.TEST_ID);
        assertThat(job).isNull();// No existing ready job

        // API inserts an job in initial authorization state
        job = PortabilityJob.builder().setTransferDataType("photo").setExportService("DummyExportService").setImportService("DummyImportService").setAndValidateJobAuthorization(JobAuthorization.builder().setEncryptionScheme("cleartext").setState(INITIAL).setSessionSecretKey("fooBar").build()).build();
        store.createJob(JobPollingServiceTest.TEST_ID, job);
        // Verify initial authorization state
        job = store.findJob(JobPollingServiceTest.TEST_ID);
        assertThat(job.jobAuthorization().state()).isEqualTo(INITIAL);
        // no auth data should exist yet
        assertThat(job.jobAuthorization().encryptedAuthData()).isNull();
        // API atomically updates job to from 'initial' to 'creds available'
        job = job.toBuilder().setAndValidateJobAuthorization(job.jobAuthorization().toBuilder().setState(CREDS_AVAILABLE).build()).build();
        store.updateJob(JobPollingServiceTest.TEST_ID, job);
        // Verify 'creds available' state
        job = store.findJob(JobPollingServiceTest.TEST_ID);
        assertThat(job.jobAuthorization().state()).isEqualTo(CREDS_AVAILABLE);
        // no auth data should exist yet
        assertThat(job.jobAuthorization().encryptedAuthData()).isNull();
        // Worker initiates the JobPollingService
        jobPollingService.runOneIteration();
        assertThat(JobMetadata.isInitialized()).isTrue();
        assertThat(JobMetadata.getJobId()).isEqualTo(JobPollingServiceTest.TEST_ID);
        // Verify assigned without auth data state
        job = store.findJob(JobPollingServiceTest.TEST_ID);
        assertThat(job.jobAuthorization().state()).isEqualTo(CREDS_ENCRYPTION_KEY_GENERATED);
        assertThat(job.jobAuthorization().authPublicKey()).isNotEmpty();
        // Client encrypts data and updates the job
        job = job.toBuilder().setAndValidateJobAuthorization(job.jobAuthorization().toBuilder().setEncryptedAuthData("dummy export data").setState(CREDS_STORED).build()).build();
        store.updateJob(JobPollingServiceTest.TEST_ID, job);
        // Run another iteration of the polling service
        // Worker should pick up encrypted data and update job
        jobPollingService.runOneIteration();
        job = store.findJob(JobPollingServiceTest.TEST_ID);
        JobAuthorization jobAuthorization = job.jobAuthorization();
        assertThat(jobAuthorization.state()).isEqualTo(JobAuthorization.State.CREDS_STORED);
        assertThat(jobAuthorization.encryptedAuthData()).isNotEmpty();
        store.remove(JobPollingServiceTest.TEST_ID);
    }
}

