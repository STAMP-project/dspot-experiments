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
import JobService.JobResult;
import JobService.RESULT_FAIL_NORETRY;
import JobService.RESULT_FAIL_RETRY;
import JobService.RESULT_SUCCESS;
import Lifetime.FOREVER;
import RetryStrategy.DEFAULT_EXPONENTIAL;
import Trigger.NOW;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import com.firebase.jobdispatcher.JobInvocation.Builder;
import com.google.common.util.concurrent.SettableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowSystemClock;

import static IJobCallback.Stub.<init>;
import static JobService.ACTION_EXECUTE;


/**
 * Tests for the {@link JobService} class.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 23, shadows = { ShadowSystemClock.class })
public class JobServiceTest {
    private static final int TIMEOUT_MS = 3000;

    private static CountDownLatch countDownLatch;

    private final IJobCallback noopCallback = new IJobCallback.Stub() {
        @Override
        public void jobFinished(Bundle invocationData, @JobService.JobResult
        int result) {
        }
    };

    @Test
    public void testOnStartCommand_handlesNullIntent() {
        JobService service = Mockito.spy(new JobServiceTest.ExampleJobService());
        int startId = 7;
        try {
            service.onStartCommand(null, 0, startId);
            Mockito.verify(service).stopSelf(startId);
        } catch (NullPointerException npe) {
            Assert.fail("Unexpected NullPointerException after calling onStartCommand with a null Intent.");
        }
    }

    @Test
    public void testOnStartCommand_handlesNullAction() {
        JobService service = Mockito.spy(new JobServiceTest.ExampleJobService());
        int startId = 7;
        Intent nullActionIntent = new Intent();
        service.onStartCommand(nullActionIntent, 0, startId);
        Mockito.verify(service).stopSelf(startId);
    }

    @Test
    public void testOnStartCommand_handlesEmptyAction() {
        JobService service = Mockito.spy(new JobServiceTest.ExampleJobService());
        int startId = 7;
        Intent emptyActionIntent = new Intent("");
        service.onStartCommand(emptyActionIntent, 0, startId);
        Mockito.verify(service).stopSelf(startId);
    }

    @Test
    public void testOnStartCommand_handlesUnknownAction() {
        JobService service = Mockito.spy(new JobServiceTest.ExampleJobService());
        int startId = 7;
        Intent emptyActionIntent = new Intent("foo.bar.baz");
        service.onStartCommand(emptyActionIntent, 0, startId);
        Mockito.verify(service).stopSelf(startId);
    }

    @Test
    public void testOnStartCommand_handlesStartJob_nullData() {
        JobService service = Mockito.spy(new JobServiceTest.ExampleJobService());
        int startId = 7;
        Intent executeJobIntent = new Intent(ACTION_EXECUTE);
        service.onStartCommand(executeJobIntent, 0, startId);
        Mockito.verify(service).stopSelf(startId);
    }

    @Test
    public void testOnStartCommand_handlesStartJob_noTag() {
        JobService service = Mockito.spy(new JobServiceTest.ExampleJobService());
        int startId = 7;
        Intent executeJobIntent = new Intent(ACTION_EXECUTE);
        Parcel p = Parcel.obtain();
        p.writeStrongBinder(Mockito.mock(IBinder.class));
        executeJobIntent.putExtra("callback", new com.google.android.gms.gcm.PendingCallback(p));
        service.onStartCommand(executeJobIntent, 0, startId);
        Mockito.verify(service).stopSelf(startId);
        p.recycle();
    }

    @Test
    public void testOnStartCommand_handlesStartJob_noCallback() {
        JobService service = Mockito.spy(new JobServiceTest.ExampleJobService());
        int startId = 7;
        Intent executeJobIntent = new Intent(ACTION_EXECUTE);
        executeJobIntent.putExtra("tag", "foobar");
        service.onStartCommand(executeJobIntent, 0, startId);
        Mockito.verify(service).stopSelf(startId);
    }

    @Test
    public void testOnStartCommand_handlesStartJob_validRequest() throws Exception {
        JobService service = Mockito.spy(new JobServiceTest.ExampleJobService());
        Job jobSpec = TestUtil.getBuilderWithNoopValidator().setTag("tag").setService(JobServiceTest.ExampleJobService.class).setRetryStrategy(DEFAULT_EXPONENTIAL).setTrigger(NOW).setLifetime(FOREVER).build();
        JobServiceTest.countDownLatch = new CountDownLatch(1);
        Bundle jobSpecData = GooglePlayReceiver.getJobCoder().encode(jobSpec, new Bundle());
        IRemoteJobService remoteJobService = Stub.asInterface(service.onBind(new Intent(ACTION_EXECUTE)));
        remoteJobService.start(jobSpecData, noopCallback);
        JobServiceTest.flush(service);
        Assert.assertTrue("Expected job to run to completion", JobServiceTest.countDownLatch.await(5, TimeUnit.SECONDS));
    }

    @Test
    public void testOnStartCommand_handlesStartJob_doNotStartRunningJobAgain() throws Exception {
        JobServiceTest.StoppableJobService service = /* shouldReschedule= */
        new JobServiceTest.StoppableJobService(false);
        Job jobSpec = TestUtil.getBuilderWithNoopValidator().setTag("tag").setService(JobServiceTest.StoppableJobService.class).setTrigger(NOW).build();
        Bundle jobSpecData = GooglePlayReceiver.getJobCoder().encode(jobSpec, new Bundle());
        Stub.asInterface(onBind(null)).start(jobSpecData, null);
        Stub.asInterface(onBind(null)).start(jobSpecData, null);
        JobServiceTest.flush(service);
        Assert.assertEquals(1, service.getNumberOfStartRequestsReceived());
    }

    @Test
    public void stop_noCallback_finished() throws Exception {
        JobService service = Mockito.spy(/* shouldReschedule= */
        new JobServiceTest.StoppableJobService(false));
        JobInvocation job = new Builder().setTag("Tag").setTrigger(NOW).setService(JobServiceTest.StoppableJobService.class.getName()).build();
        Stub.asInterface(service.onBind(null)).stop(GooglePlayReceiver.getJobCoder().encode(job, new Bundle()), true);
        JobServiceTest.flush(service);
        Mockito.verify(service, Mockito.never()).onStopJob(job);
    }

    @Test
    public void stop_withCallback_retry() throws Exception {
        JobServiceTest.StoppableJobService service = Mockito.spy(/* shouldReschedule= */
        new JobServiceTest.StoppableJobService(false));
        JobInvocation job = new Builder().setTag("Tag").setTrigger(NOW).setService(JobServiceTest.StoppableJobService.class.getName()).build();
        Bundle jobSpecData = GooglePlayReceiver.getJobCoder().encode(job, new Bundle());
        JobServiceTest.FutureSettingJobCallback callback = new JobServiceTest.FutureSettingJobCallback();
        // start the service
        Stub.asInterface(onBind(null)).start(jobSpecData, callback);
        Stub.asInterface(onBind(null)).stop(jobSpecData, true);
        JobServiceTest.flush(service);
        Assert.assertEquals(1, service.getNumberOfStopRequestsReceived());
        callback.verifyCalledWithJobAndResult(job, RESULT_SUCCESS);
    }

    @Test
    public void stop_withCallback_done() throws Exception {
        JobServiceTest.StoppableJobService service = Mockito.spy(/* shouldReschedule= */
        new JobServiceTest.StoppableJobService(true));
        JobInvocation job = new Builder().setTag("Tag").setTrigger(NOW).setService(JobServiceTest.StoppableJobService.class.getName()).build();
        Bundle jobSpecData = GooglePlayReceiver.getJobCoder().encode(job, new Bundle());
        JobServiceTest.FutureSettingJobCallback callback = new JobServiceTest.FutureSettingJobCallback();
        Stub.asInterface(onBind(null)).start(jobSpecData, callback);
        Stub.asInterface(onBind(null)).stop(jobSpecData, true);
        JobServiceTest.flush(service);
        Assert.assertEquals(1, service.getNumberOfStopRequestsReceived());
        callback.verifyCalledWithJobAndResult(job, RESULT_FAIL_RETRY);
    }

    @Test
    public void onStartJob_jobFinishedReschedule() throws Exception {
        // Verify that a retry request from within onStartJob will cause the retry result to be sent
        // to the bouncer service's handler, regardless of what value is ultimately returned from
        // onStartJob.
        JobService reschedulingService = new JobService() {
            @Override
            public boolean onStartJob(@NonNull
            JobParameters job) {
                // Reschedules job.
                /* retry this job */
                jobFinished(job, true);
                return false;
            }

            @Override
            public boolean onStopJob(@NonNull
            JobParameters job) {
                return false;
            }
        };
        Job jobSpec = TestUtil.getBuilderWithNoopValidator().setTag("tag").setService(reschedulingService.getClass()).setTrigger(NOW).build();
        JobServiceTest.FutureSettingJobCallback callback = new JobServiceTest.FutureSettingJobCallback();
        Stub.asInterface(reschedulingService.onBind(null)).start(GooglePlayReceiver.getJobCoder().encode(jobSpec, new Bundle()), callback);
        JobServiceTest.flush(reschedulingService);
        callback.verifyCalledWithJobAndResult(jobSpec, RESULT_FAIL_RETRY);
    }

    @Test
    public void onStartJob_jobFinishedNotReschedule() throws Exception {
        // Verify that a termination request from within onStartJob will cause the result to be sent
        // to the bouncer service's handler, regardless of what value is ultimately returned from
        // onStartJob.
        JobService reschedulingService = new JobService() {
            @Override
            public boolean onStartJob(@NonNull
            JobParameters job) {
                /* don't retry this job */
                jobFinished(job, false);
                return false;
            }

            @Override
            public boolean onStopJob(@NonNull
            JobParameters job) {
                return false;
            }
        };
        Job jobSpec = TestUtil.getBuilderWithNoopValidator().setTag("tag").setService(reschedulingService.getClass()).setTrigger(NOW).build();
        JobServiceTest.FutureSettingJobCallback callback = new JobServiceTest.FutureSettingJobCallback();
        Stub.asInterface(reschedulingService.onBind(null)).start(GooglePlayReceiver.getJobCoder().encode(jobSpec, new Bundle()), callback);
        JobServiceTest.flush(reschedulingService);
        callback.verifyCalledWithJobAndResult(jobSpec, RESULT_SUCCESS);
    }

    @Test
    public void onUnbind_removesUsedCallbacks_withBackgroundWork() throws Exception {
        JobServiceTest.verifyOnUnbindCausesResult(new JobService() {
            @Override
            public boolean onStartJob(@NonNull
            JobParameters job) {
                return true;// More work to do in background

            }

            @Override
            public boolean onStopJob(@NonNull
            JobParameters job) {
                return true;// Still doing background work

            }
        }, RESULT_FAIL_RETRY);
    }

    @Test
    public void onUnbind_removesUsedCallbacks_noBackgroundWork() throws Exception {
        JobServiceTest.verifyOnUnbindCausesResult(new JobService() {
            @Override
            public boolean onStartJob(@NonNull
            JobParameters job) {
                return true;// more work to do in background

            }

            @Override
            public boolean onStopJob(@NonNull
            JobParameters job) {
                return false;// Done with background work

            }
        }, RESULT_FAIL_NORETRY);
    }

    @Test
    public void onStop_calledOnMainThread() throws Exception {
        final SettableFuture<Looper> looperFuture = SettableFuture.create();
        final JobService service = new JobService() {
            @Override
            public boolean onStartJob(@NonNull
            JobParameters job) {
                return true;// more work to do

            }

            @Override
            public boolean onStopJob(@NonNull
            JobParameters job) {
                looperFuture.set(Looper.myLooper());
                return false;
            }
        };
        Job jobSpec = TestUtil.getBuilderWithNoopValidator().setTag("tag").setService(service.getClass()).setTrigger(NOW).build();
        final Bundle jobSpecData = GooglePlayReceiver.getJobCoder().encode(jobSpec, new Bundle());
        Stub.asInterface(service.onBind(null)).start(jobSpecData, noopCallback);
        // call stopJob on a background thread and wait for it
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Stub.asInterface(service.onBind(null)).stop(jobSpecData, true);
                } catch (RemoteException e) {
                    throw new AssertionError("calling stop on binder unexpectedly failed", e);
                }
            }
        }).get(1, TimeUnit.SECONDS);
        JobServiceTest.flush(service);
        Assert.assertEquals("onStopJob was not called on main thread", Looper.getMainLooper(), looperFuture.get(1, TimeUnit.SECONDS));
    }

    @Test
    public void dump_noTasksStarted() throws Exception {
        assertThat(JobServiceTest.dump(new JobServiceTest.ExampleJobService())).isEqualTo("No running jobs\n");
    }

    @Test
    public void dump_oneRunningJob() throws Exception {
        JobServiceTest.countDownLatch = new CountDownLatch(1);
        JobService service = new JobService() {
            @Override
            public boolean onStartJob(JobParameters job) {
                JobServiceTest.countDownLatch.countDown();
                return true;// more work to do

            }

            @Override
            public boolean onStopJob(JobParameters job) {
                return false;
            }
        };
        Job jobSpec = TestUtil.getBuilderWithNoopValidator().setTag("one_running_job").setService(service.getClass()).setTrigger(NOW).build();
        Bundle jobSpecData = GooglePlayReceiver.getJobCoder().encode(jobSpec, new Bundle());
        JobServiceTest.FutureSettingJobCallback callback = new JobServiceTest.FutureSettingJobCallback();
        IRemoteJobService stub = Stub.asInterface(service.onBind(null));
        ShadowSystemClock.setCurrentTimeMillis(10000L);
        // Start the job
        stub.start(jobSpecData, callback);
        JobServiceTest.flush(service);
        // Make sure it was started
        assertThat(JobServiceTest.countDownLatch.await(1, TimeUnit.SECONDS)).isTrue();
        // Fast forward 30s
        ShadowSystemClock.setCurrentTimeMillis(40000L);
        assertThat(JobServiceTest.dump(service)).isEqualTo("Running jobs:\n    * \"one_running_job\" has been running for 00:30\n");
        /* needToSendResult= */
        stub.stop(jobSpecData, false);
        JobServiceTest.flush(service);
        assertThat(JobServiceTest.dump(service)).isEqualTo("No running jobs\n");
    }

    private static class FutureSettingJobCallback extends IJobCallback.Stub {
        SettableFuture<Pair<Bundle, Integer>> jobFinishedFuture = SettableFuture.create();

        SettableFuture<Pair<Bundle, Integer>> getJobFinishedFuture() {
            return jobFinishedFuture;
        }

        void reset() {
            jobFinishedFuture = SettableFuture.create();
        }

        void verifyCalledWithJobAndResult(JobParameters job, int result) throws Exception {
            Pair<Bundle, Integer> jobFinishedResult = getJobFinishedFuture().get(JobServiceTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
            Assert.assertNotNull(jobFinishedResult);
            JobCoder jc = GooglePlayReceiver.getJobCoder();
            // re-encode so they're the same class
            Assert.assertEquals(jc.decode(jc.encode(job, new Bundle())).build(), jc.decode(jobFinishedResult.first).build());
            Assert.assertEquals(result, ((int) (jobFinishedResult.second)));
        }

        @Override
        public void jobFinished(Bundle invocationData, @JobService.JobResult
        int result) {
            jobFinishedFuture.set(Pair.create(invocationData, result));
        }
    }

    /**
     * A simple JobService that just counts down the {@link #countDownLatch}.
     */
    public static class ExampleJobService extends JobService {
        @Override
        public boolean onStartJob(@NonNull
        JobParameters job) {
            JobServiceTest.countDownLatch.countDown();
            return false;
        }

        @Override
        public boolean onStopJob(@NonNull
        JobParameters job) {
            return false;
        }
    }

    /**
     * A JobService that allows customizing the onStopJob result.
     */
    public static class StoppableJobService extends JobService {
        private final boolean shouldReschedule;

        private final AtomicInteger numberOfStartRequestsReceived = new AtomicInteger();

        private final AtomicInteger numberOfStopRequestsReceived = new AtomicInteger();

        public StoppableJobService(boolean shouldReschedule) {
            this.shouldReschedule = shouldReschedule;
        }

        @Override
        public boolean onStartJob(@NonNull
        JobParameters job) {
            numberOfStartRequestsReceived.incrementAndGet();
            return true;
        }

        @Override
        public boolean onStopJob(@NonNull
        JobParameters job) {
            numberOfStopRequestsReceived.incrementAndGet();
            return shouldReschedule;
        }

        public int getNumberOfStartRequestsReceived() {
            return numberOfStartRequestsReceived.get();
        }

        public int getNumberOfStopRequestsReceived() {
            return numberOfStopRequestsReceived.get();
        }
    }
}

