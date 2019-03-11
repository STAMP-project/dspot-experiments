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


import Constraint.DEVICE_CHARGING;
import FirebaseJobDispatcher.CANCEL_RESULT_NO_DRIVER_AVAILABLE;
import FirebaseJobDispatcher.SCHEDULE_RESULT_NO_DRIVER_AVAILABLE;
import FirebaseJobDispatcher.SCHEDULE_RESULT_SUCCESS;
import JobService.ACTION_EXECUTE;
import RetryStrategy.DEFAULT_EXPONENTIAL;
import ValidationEnforcer.ValidationException;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.firebase.jobdispatcher.ExecutionDelegator.JobFinishedCallback;
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

import static BundleProtocol.PACKED_PARAM_BUNDLE_PREFIX;


/**
 * Tests for the {@link GooglePlayDriver} class.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 23)
public class GooglePlayDriverTest {
    @Mock
    private Context mMockContext;

    @Mock
    private JobCallback jobCallbackMock;

    @Mock
    private JobFinishedCallback callbackMock;

    @Mock
    private ConstraintChecker constraintCheckerMock;

    @Captor
    ArgumentCaptor<JobServiceConnection> serviceConnectionCaptor;

    private GooglePlayDriverTest.TestJobDriver driver;

    private GooglePlayDriver googlePlayDriver;

    private FirebaseJobDispatcher dispatcher;

    private final GooglePlayReceiver googlePlayReceiver = new GooglePlayReceiver();

    @Test
    public void testSchedule_failsWhenPlayServicesIsUnavailable() throws Exception {
        markBackendUnavailable();
        mockPackageManagerInfo();
        Job job = null;
        try {
            job = dispatcher.newJobBuilder().setService(TestJobService.class).setTag("foobar").setConstraints(DEVICE_CHARGING).setTrigger(Trigger.executionWindow(0, 60)).build();
        } catch (ValidationEnforcer ve) {
            Assert.fail(TextUtils.join("\n", ve.getErrors()));
        }
        Assert.assertEquals("Expected schedule() request to fail when backend is unavailable", SCHEDULE_RESULT_NO_DRIVER_AVAILABLE, dispatcher.schedule(job));
    }

    @Test
    public void testCancelJobs_backendUnavailable() throws Exception {
        markBackendUnavailable();
        Assert.assertEquals("Expected cancelAll() request to fail when backend is unavailable", CANCEL_RESULT_NO_DRIVER_AVAILABLE, dispatcher.cancelAll());
    }

    @Test
    public void testSchedule_sendsAppropriateBroadcast() {
        ArgumentCaptor<Intent> pmQueryIntentCaptor = mockPackageManagerInfo();
        Job job = dispatcher.newJobBuilder().setConstraints(DEVICE_CHARGING).setService(TestJobService.class).setTrigger(Trigger.executionWindow(0, 60)).setRecurring(false).setRetryStrategy(DEFAULT_EXPONENTIAL).setTag("foobar").build();
        Intent pmQueryIntent = pmQueryIntentCaptor.getValue();
        Assert.assertEquals(ACTION_EXECUTE, pmQueryIntent.getAction());
        Assert.assertEquals(TestJobService.class.getName(), pmQueryIntent.getComponent().getClassName());
        Assert.assertEquals("Expected schedule() request to succeed", SCHEDULE_RESULT_SUCCESS, dispatcher.schedule(job));
        final ArgumentCaptor<Intent> captor = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(mMockContext).sendBroadcast(captor.capture());
        Intent broadcast = captor.getValue();
        Assert.assertNotNull(broadcast);
        Assert.assertEquals("com.google.android.gms.gcm.ACTION_SCHEDULE", broadcast.getAction());
        Assert.assertEquals("SCHEDULE_TASK", broadcast.getStringExtra("scheduler_action"));
        Assert.assertEquals("com.google.android.gms", broadcast.getPackage());
        Assert.assertEquals(8, broadcast.getIntExtra("source", (-1)));
        Assert.assertEquals(1, broadcast.getIntExtra("source_version", (-1)));
        final Parcelable parcelablePendingIntent = broadcast.getParcelableExtra("app");
        Assert.assertTrue("Expected 'app' value to be a PendingIntent", (parcelablePendingIntent instanceof PendingIntent));
    }

    @Test
    public void schedule_whenRunning_onStopIsCalled() {
        // simulate running job
        Bundle bundle = TestUtil.getBundleForContentJobExecution();
        JobCoder prefixedCoder = new JobCoder(PACKED_PARAM_BUNDLE_PREFIX);
        JobInvocation invocation = prefixedCoder.decodeIntentBundle(bundle);
        googlePlayReceiver.prepareJob(jobCallbackMock, bundle);
        Mockito.when(mMockContext.bindService(ArgumentMatchers.any(Intent.class), serviceConnectionCaptor.capture(), ArgumentMatchers.eq(BIND_AUTO_CREATE))).thenReturn(true);
        new ExecutionDelegator(mMockContext, callbackMock, constraintCheckerMock).executeJob(invocation);
        Job job = TestUtil.getBuilderWithNoopValidator().setService(TestJobService.class).setTrigger(invocation.getTrigger()).setTag(invocation.getTag()).build();
        googlePlayDriver.schedule(job);// reschedule request during the execution

        Mockito.verify(mMockContext).sendBroadcast(ArgumentMatchers.any(Intent.class));
        Assert.assertTrue(serviceConnectionCaptor.getValue().wasUnbound());
        Assert.assertNull("JobServiceConnection should be removed.", ExecutionDelegator.getJobServiceConnection(invocation.getService()));
    }

    @Test
    public void testCancel_sendsAppropriateBroadcast() {
        dispatcher.cancel("foobar");
        ArgumentCaptor<Intent> captor = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(mMockContext).sendBroadcast(captor.capture());
        Intent broadcast = captor.getValue();
        Assert.assertNotNull(broadcast);
        Assert.assertEquals("foobar", broadcast.getStringExtra("tag"));
    }

    /**
     * A simple {@link Driver} implementation that allows overriding the result of {@link #isAvailable}.
     */
    public static final class TestJobDriver implements Driver {
        public boolean available = true;

        private final Driver wrappedDriver;

        public TestJobDriver(Driver wrappedDriver) {
            this.wrappedDriver = wrappedDriver;
        }

        @Override
        public int schedule(@NonNull
        Job job) {
            return this.wrappedDriver.schedule(job);
        }

        @Override
        public int cancel(@NonNull
        String tag) {
            return this.wrappedDriver.cancel(tag);
        }

        @Override
        public int cancelAll() {
            return this.wrappedDriver.cancelAll();
        }

        @NonNull
        @Override
        public JobValidator getValidator() {
            return this.wrappedDriver.getValidator();
        }

        @Override
        public boolean isAvailable() {
            return available;
        }
    }
}

