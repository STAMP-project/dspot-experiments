/**
 * Copyright 2016 Google, Inc.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
/**
 *
 */
/**
 * //////////////////////////////////////////////////////////////////////////////
 */
package com.firebase.jobdispatcher;


import IRemoteJobService.Stub;
import JobService.ACTION_EXECUTE;
import JobService.RESULT_FAIL_NORETRY;
import JobService.RESULT_FAIL_RETRY;
import RetryStrategy.DEFAULT_EXPONENTIAL;
import Trigger.NOW;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import com.firebase.jobdispatcher.JobService.JobResult;
import com.google.common.util.concurrent.SettableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 * Tests for the {@link ExecutionDelegator}.
 */
@SuppressWarnings("WrongConstant")
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 21)
public class ExecutionDelegatorTest {
    private ExecutionDelegatorTest.TestJobReceiver receiver;

    private ExecutionDelegator executionDelegator;

    private Stub noopBinder;

    @Captor
    private ArgumentCaptor<Intent> intentCaptor;

    @Captor
    private ArgumentCaptor<JobServiceConnection> connCaptor;

    @Captor
    private ArgumentCaptor<IJobCallback> jobCallbackCaptor;

    @Captor
    private ArgumentCaptor<Bundle> bundleCaptor;

    @Mock
    private Context mockContext;

    @Mock
    private IRemoteJobService jobServiceMock;

    @Mock
    private IBinder iBinderMock;

    @Mock
    ConstraintChecker constraintChecker;

    @Test
    public void jobFinished() throws RemoteException {
        JobInvocation jobInvocation = new JobInvocation.Builder().setTag("tag").setService("service").setTrigger(NOW).build();
        Mockito.when(mockContext.bindService(ArgumentMatchers.any(Intent.class), ArgumentMatchers.any(ServiceConnection.class), ArgumentMatchers.eq(BIND_AUTO_CREATE))).thenReturn(true);
        executionDelegator.executeJob(jobInvocation);
        Mockito.verify(mockContext).bindService(intentCaptor.capture(), connCaptor.capture(), ArgumentMatchers.eq(BIND_AUTO_CREATE));
        JobServiceConnection connection = connCaptor.getValue();
        Mockito.when(iBinderMock.queryLocalInterface(IRemoteJobService.class.getName())).thenReturn(jobServiceMock);
        connection.onServiceConnected(null, iBinderMock);
        Mockito.verify(jobServiceMock).start(bundleCaptor.capture(), jobCallbackCaptor.capture());
        jobCallbackCaptor.getValue().jobFinished(bundleCaptor.getValue(), RESULT_FAIL_NORETRY);
        Assert.assertNull(ExecutionDelegator.getJobServiceConnection("service"));
        Assert.assertEquals(RESULT_FAIL_NORETRY, receiver.lastResult);
        Assert.assertTrue(connection.wasUnbound());
    }

    @Test
    public void testExecuteJob_sendsBroadcastWithJobAndMessage() throws Exception {
        for (JobInvocation input : TestUtil.getJobInvocationCombinations()) {
            verifyExecuteJob(input);
            // Reset mocks for the next invocation
            Mockito.reset(mockContext);
            receiver.lastResult = -1;
        }
    }

    @Test
    public void executeJob_constraintsUnsatisfied_schedulesJobRetry() throws RemoteException {
        // Simulate job constraints not being met.
        JobInvocation jobInvocation = new JobInvocation.Builder().setTag("tag").setService("service").setTrigger(NOW).build();
        Mockito.when(constraintChecker.areConstraintsSatisfied(ArgumentMatchers.eq(jobInvocation))).thenReturn(false);
        executionDelegator.executeJob(jobInvocation);
        // Confirm that service not bound
        Mockito.verify(mockContext, Mockito.never()).bindService(ArgumentMatchers.any(Intent.class), ArgumentMatchers.any(JobServiceConnection.class), ArgumentMatchers.anyInt());
        assertThat(ExecutionDelegator.getJobServiceConnection("service")).isNull();
        // Verify that job is set for a retry later.
        assertThat(receiver.lastResult).isEqualTo(RESULT_FAIL_RETRY);
    }

    @Test
    public void executeJob_alreadyRunning_doesNotBindSecondTime() {
        JobInvocation jobInvocation = new JobInvocation.Builder().setTag("tag").setService("service").setTrigger(NOW).build();
        Mockito.when(mockContext.bindService(ArgumentMatchers.any(Intent.class), ArgumentMatchers.any(ServiceConnection.class), ArgumentMatchers.eq(BIND_AUTO_CREATE))).thenReturn(true);
        executionDelegator.executeJob(jobInvocation);
        Mockito.verify(mockContext).bindService(intentCaptor.capture(), connCaptor.capture(), ArgumentMatchers.eq(BIND_AUTO_CREATE));
        connCaptor.getValue().onServiceConnected(null, noopBinder);
        Assert.assertFalse(connCaptor.getValue().wasUnbound());
        Assert.assertTrue(connCaptor.getValue().isConnected());
        Mockito.reset(mockContext);
        executionDelegator.executeJob(jobInvocation);
        Assert.assertFalse(connCaptor.getValue().wasUnbound());
        Mockito.verify(mockContext, Mockito.never()).unbindService(connCaptor.getValue());
        Mockito.verify(mockContext, Mockito.never()).bindService(ArgumentMatchers.any(Intent.class), ArgumentMatchers.any(ServiceConnection.class), ArgumentMatchers.eq(BIND_AUTO_CREATE));
    }

    @Test
    public void executeJob_startedButNotConnected_doNotStartAgain() {
        JobInvocation jobInvocation = new JobInvocation.Builder().setTag("tag").setService("service").setTrigger(NOW).build();
        Mockito.when(mockContext.bindService(ArgumentMatchers.any(Intent.class), ArgumentMatchers.any(ServiceConnection.class), ArgumentMatchers.eq(BIND_AUTO_CREATE))).thenReturn(true);
        executionDelegator.executeJob(jobInvocation);
        Mockito.verify(mockContext).bindService(intentCaptor.capture(), connCaptor.capture(), ArgumentMatchers.eq(BIND_AUTO_CREATE));
        Mockito.reset(mockContext);
        executionDelegator.executeJob(jobInvocation);
        Mockito.verify(mockContext, Mockito.never()).bindService(ArgumentMatchers.any(Intent.class), ArgumentMatchers.any(JobServiceConnection.class), ArgumentMatchers.eq(BIND_AUTO_CREATE));
    }

    @Test
    public void executeJob_wasStartedButDisconnected_startAgain() {
        JobInvocation jobInvocation = new JobInvocation.Builder().setTag("tag").setService("service").setTrigger(NOW).build();
        Mockito.when(mockContext.bindService(ArgumentMatchers.any(Intent.class), ArgumentMatchers.any(ServiceConnection.class), ArgumentMatchers.eq(BIND_AUTO_CREATE))).thenReturn(true);
        executionDelegator.executeJob(jobInvocation);
        Mockito.verify(mockContext).bindService(intentCaptor.capture(), connCaptor.capture(), ArgumentMatchers.eq(BIND_AUTO_CREATE));
        JobServiceConnection jobServiceConnection = connCaptor.getValue();
        jobServiceConnection.onServiceConnected(null, noopBinder);
        jobServiceConnection.unbind();
        Mockito.verify(mockContext).unbindService(jobServiceConnection);
        Mockito.reset(mockContext);
        executionDelegator.executeJob(jobInvocation);
        Mockito.verify(mockContext).bindService(ArgumentMatchers.any(Intent.class), ArgumentMatchers.any(JobServiceConnection.class), ArgumentMatchers.eq(BIND_AUTO_CREATE));
    }

    @Test
    public void testExecuteJob_handlesNull() {
        executionDelegator.executeJob(null);
        Mockito.verifyZeroInteractions(mockContext);
    }

    @Test
    public void testHandleMessage_doesntCrashOnBadJobData() {
        JobInvocation j = new JobInvocation.Builder().setService(TestJobService.class.getName()).setTag("tag").setTrigger(NOW).build();
        executionDelegator.executeJob(j);
        // noinspection WrongConstant
        Mockito.verify(mockContext).bindService(intentCaptor.capture(), connCaptor.capture(), ArgumentMatchers.anyInt());
        Intent executeReq = intentCaptor.getValue();
        Assert.assertEquals(ACTION_EXECUTE, executeReq.getAction());
    }

    @Test
    public void onStop_mock() throws Exception {
        JobInvocation job = new JobInvocation.Builder().setTag("TAG").setTrigger(TestUtil.getContentUriTrigger()).setService(TestJobService.class.getName()).setRetryStrategy(DEFAULT_EXPONENTIAL).build();
        Mockito.when(mockContext.bindService(ArgumentMatchers.any(Intent.class), ArgumentMatchers.any(ServiceConnection.class), ArgumentMatchers.eq(BIND_AUTO_CREATE))).thenReturn(true);
        executionDelegator.executeJob(job);
        Mockito.verify(mockContext).bindService(intentCaptor.capture(), connCaptor.capture(), ArgumentMatchers.eq(BIND_AUTO_CREATE));
        final Intent bindIntent = intentCaptor.getValue();
        // verify the intent was sent to the right place
        Assert.assertEquals(job.getService(), bindIntent.getComponent().getClassName());
        Assert.assertEquals(ACTION_EXECUTE, bindIntent.getAction());
        final SettableFuture<JobParameters> startedJobFuture = SettableFuture.create();
        final SettableFuture<JobParameters> stoppedJobFuture = SettableFuture.create();
        IRemoteJobService.Stub jobServiceBinder = new IRemoteJobService.Stub() {
            @Override
            public void start(Bundle invocationData, IJobCallback callback) {
                startedJobFuture.set(GooglePlayReceiver.getJobCoder().decode(invocationData).build());
            }

            @Override
            public void stop(Bundle invocationData, boolean needToSendResult) {
                stoppedJobFuture.set(GooglePlayReceiver.getJobCoder().decode(invocationData).build());
            }
        };
        final ServiceConnection connection = connCaptor.getValue();
        connection.onServiceConnected(null, jobServiceBinder);
        /* needToSendResult= */
        ExecutionDelegator.stopJob(job, false);
        TestUtil.assertJobsEqual(job, startedJobFuture.get(0, TimeUnit.SECONDS));
        Mockito.verify(mockContext, Mockito.timeout(1000)).unbindService(connection);
    }

    @Test
    public void failedToBind_unbind() throws Exception {
        JobInvocation job = new JobInvocation.Builder().setTag("TAG").setTrigger(TestUtil.getContentUriTrigger()).setService(TestJobService.class.getName()).setRetryStrategy(DEFAULT_EXPONENTIAL).build();
        Mockito.when(mockContext.bindService(ArgumentMatchers.any(Intent.class), ArgumentMatchers.any(ServiceConnection.class), ArgumentMatchers.eq(BIND_AUTO_CREATE))).thenReturn(false);
        executionDelegator.executeJob(job);
        Mockito.verify(mockContext).bindService(intentCaptor.capture(), connCaptor.capture(), ArgumentMatchers.eq(BIND_AUTO_CREATE));
        Mockito.verify(mockContext).unbindService(connCaptor.getValue());
        Assert.assertEquals(RESULT_FAIL_RETRY, receiver.lastResult);
    }

    @Test
    public void securityException_unbinds() throws Exception {
        JobInvocation job = new JobInvocation.Builder().setTag("TAG").setTrigger(TestUtil.getContentUriTrigger()).setService(TestJobService.class.getName()).setRetryStrategy(DEFAULT_EXPONENTIAL).build();
        Mockito.when(mockContext.bindService(ArgumentMatchers.any(Intent.class), ArgumentMatchers.any(ServiceConnection.class), ArgumentMatchers.eq(BIND_AUTO_CREATE))).thenThrow(new SecurityException("should have gracefully handled this security exception"));
        executionDelegator.executeJob(job);
        // Verify that bindService was actually called
        Mockito.verify(mockContext).bindService(ArgumentMatchers.any(Intent.class), connCaptor.capture(), ArgumentMatchers.eq(BIND_AUTO_CREATE));
        // And that unbindService was called in response to the security exception
        Mockito.verify(mockContext).unbindService(connCaptor.getValue());
        // And that we should have sent RESULT_FAIL_RETRY back to the calling component
        Assert.assertEquals(RESULT_FAIL_RETRY, receiver.lastResult);
    }

    private static final class TestJobReceiver implements ExecutionDelegator.JobFinishedCallback {
        int lastResult = -1;

        private CountDownLatch latch;

        @Override
        public void onJobFinished(@NonNull
        JobInvocation js, @JobResult
        int result) {
            lastResult = result;
            if ((latch) != null) {
                latch.countDown();
            }
        }

        /**
         * Convenience method for tests.
         */
        public void setLatch(CountDownLatch latch) {
            this.latch = latch;
        }
    }
}

