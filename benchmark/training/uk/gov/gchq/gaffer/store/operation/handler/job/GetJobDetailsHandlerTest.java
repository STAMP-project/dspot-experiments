/**
 * Copyright 2016-2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.gchq.gaffer.store.operation.handler.job;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.jobtracker.JobDetail;
import uk.gov.gchq.gaffer.jobtracker.JobTracker;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.impl.job.GetJobDetails;
import uk.gov.gchq.gaffer.store.Context;
import uk.gov.gchq.gaffer.store.Store;
import uk.gov.gchq.gaffer.user.User;


public class GetJobDetailsHandlerTest {
    @Test
    public void shouldThrowExceptionIfJobTrackerIsNotConfigured() {
        // Given
        final GetJobDetailsHandler handler = new GetJobDetailsHandler();
        final GetJobDetails operation = Mockito.mock(GetJobDetails.class);
        final Store store = Mockito.mock(Store.class);
        final User user = Mockito.mock(User.class);
        BDDMockito.given(store.getJobTracker()).willReturn(null);
        // When / Then
        try {
            handler.doOperation(operation, new Context(user), store);
            Assert.fail("Exception expected");
        } catch (final OperationException e) {
            Assert.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldGetJobDetailsByDelegatingToJobTrackerWithOperationJobId() throws OperationException {
        // Given
        final String jobId = "jobId";
        final GetJobDetailsHandler handler = new GetJobDetailsHandler();
        final GetJobDetails operation = new GetJobDetails.Builder().jobId(jobId).build();
        final Store store = Mockito.mock(Store.class);
        final JobTracker jobTracker = Mockito.mock(JobTracker.class);
        final User user = Mockito.mock(User.class);
        final JobDetail jobsDetail = Mockito.mock(JobDetail.class);
        final Context context = new Context(user);
        BDDMockito.given(store.getJobTracker()).willReturn(jobTracker);
        BDDMockito.given(jobTracker.getJob(jobId, user)).willReturn(jobsDetail);
        // When
        final JobDetail result = handler.doOperation(operation, context, store);
        // Then
        Assert.assertSame(jobsDetail, result);
    }

    @Test
    public void shouldGetJobDetailsByDelegatingToJobTrackerWithContextJobId() throws OperationException {
        // Given
        final GetJobDetailsHandler handler = new GetJobDetailsHandler();
        final GetJobDetails operation = new GetJobDetails();
        final Store store = Mockito.mock(Store.class);
        final JobTracker jobTracker = Mockito.mock(JobTracker.class);
        final User user = Mockito.mock(User.class);
        final JobDetail jobsDetail = Mockito.mock(JobDetail.class);
        final Context context = new Context.Builder().user(user).build();
        final String jobId = context.getJobId();
        BDDMockito.given(store.getJobTracker()).willReturn(jobTracker);
        BDDMockito.given(jobTracker.getJob(jobId, user)).willReturn(jobsDetail);
        // When
        final JobDetail result = handler.doOperation(operation, context, store);
        // Then
        Assert.assertSame(jobsDetail, result);
    }
}

