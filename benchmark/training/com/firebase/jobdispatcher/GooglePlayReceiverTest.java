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


import BundleProtocol.PACKED_PARAM_TRIGGERED_URIS;
import ContactsContract.AUTHORITY_URI;
import GooglePlayJobWriter.REQUEST_PARAM_EXTRAS;
import JobService.RESULT_FAIL_NORETRY;
import JobService.RESULT_FAIL_RETRY;
import JobService.RESULT_SUCCESS;
import Lifetime.UNTIL_NEXT_BOOT;
import Media.EXTERNAL_CONTENT_URI;
import RetryStrategy.DEFAULT_EXPONENTIAL;
import TestUtil.JOB_CODER;
import Trigger.NOW;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.os.Parcel;
import android.provider.ContactsContract.Contacts;
import com.firebase.jobdispatcher.ExecutionDelegator.JobFinishedCallback;
import com.firebase.jobdispatcher.JobInvocation.Builder;
import com.firebase.jobdispatcher.TestUtil.InspectableBinder;
import com.google.android.gms.gcm.PendingCallback;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implements;

import static BundleProtocol.PACKED_PARAM_BUNDLE_PREFIX;
import static Constraint.DEVICE_IDLE;
import static GooglePlayReceiver.ACTION_EXECUTE;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;


/**
 * Tests for the {@link GooglePlayReceiver} class.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 21, shadows = { GooglePlayReceiverTest.ShadowMessenger.class })
public class GooglePlayReceiverTest {
    /**
     * The default ShadowMessenger implementation causes NPEs when using the {@link Messenger#Messenger(Handler)} constructor. We create our own empty Shadow so we can just use
     * the standard Android implementation, which is totally fine.
     *
     * @see <a href="https://github.com/robolectric/robolectric/issues/2246">Robolectric issue</a>
     */
    @Implements(Messenger.class)
    public static class ShadowMessenger {}

    private GooglePlayReceiver receiver;

    @Mock
    private Messenger messengerMock;

    @Mock
    private Context contextMock;

    @Mock
    private IBinder binderMock;

    @Mock
    private JobCallback callbackMock;

    @Mock
    private ExecutionDelegator executionDelegatorMock;

    @Mock
    private JobCallback jobCallbackMock;

    @Mock
    private JobServiceConnection jobServiceConnectionMock;

    @Mock
    private Driver driverMock;

    @Mock
    private ConstraintChecker contraintCheckerMock;

    @Captor
    private ArgumentCaptor<Job> jobArgumentCaptor;

    @Captor
    ArgumentCaptor<JobServiceConnection> jobServiceConnectionCaptor;

    private final Builder jobInvocationBuilder = new Builder().setTag("tag").setService(TestJobService.class.getName()).setTrigger(NOW);

    @Test
    public void onReschedule_notRunning_noException() {
        Job job = TestUtil.getBuilderWithNoopValidator().setService(TestJobService.class).setTrigger(NOW).setTag("TAG").build();
        GooglePlayReceiver.onSchedule(job);
    }

    @Test
    public void onReschedule_notRunningWrongTag_noException() {
        Bundle bundle = TestUtil.getBundleForContentJobExecution();
        Job job = TestUtil.getBuilderWithNoopValidator().setService(TestJobService.class).setTrigger(NOW).setTag("TAG").build();
        receiver.prepareJob(jobCallbackMock, bundle);
        GooglePlayReceiver.onSchedule(job);
    }

    @Test
    public void onReschedule_stopJob() {
        Bundle bundle = TestUtil.getBundleForContentJobExecution();
        JobCoder prefixedCoder = new JobCoder(PACKED_PARAM_BUNDLE_PREFIX);
        JobInvocation invocation = prefixedCoder.decodeIntentBundle(bundle);
        Job job = TestUtil.getBuilderWithNoopValidator().setService(TestJobService.class).setTrigger(invocation.getTrigger()).setTag(invocation.getTag()).build();
        receiver.prepareJob(jobCallbackMock, bundle);
        Mockito.when(contextMock.bindService(ArgumentMatchers.any(Intent.class), ArgumentMatchers.any(JobServiceConnection.class), ArgumentMatchers.eq(BIND_AUTO_CREATE))).thenReturn(true);
        new ExecutionDelegator(contextMock, Mockito.mock(JobFinishedCallback.class), contraintCheckerMock).executeJob(invocation);
        Mockito.verify(contextMock).bindService(ArgumentMatchers.any(Intent.class), jobServiceConnectionCaptor.capture(), ArgumentMatchers.eq(BIND_AUTO_CREATE));
        Assert.assertTrue(jobServiceConnectionCaptor.getValue().hasJobInvocation(invocation));
        GooglePlayReceiver.onSchedule(job);
        assertFalse(jobServiceConnectionCaptor.getValue().hasJobInvocation(invocation));
        junit.framework.Assert.assertNull("JobServiceConnection should be removed.", ExecutionDelegator.getJobServiceConnection(invocation.getService()));
    }

    @Test
    public void onJobFinished_unknownJobCallbackIsNotPresent_ignoreNoException() {
        receiver.onJobFinished(jobInvocationBuilder.build(), RESULT_SUCCESS);
        Mockito.verifyZeroInteractions(driverMock);
    }

    @Test
    public void onJobFinished_notRecurringContentJob_sendResult() {
        jobInvocationBuilder.setTrigger(Trigger.contentUriTrigger(Arrays.asList(new ObservedUri(Contacts.CONTENT_URI, 0))));
        JobInvocation jobInvocation = receiver.prepareJob(callbackMock, TestUtil.getBundleForContentJobExecution());
        receiver.onJobFinished(jobInvocation, RESULT_SUCCESS);
        Mockito.verify(callbackMock).jobFinished(RESULT_SUCCESS);
        Mockito.verifyZeroInteractions(driverMock);
    }

    @Test
    public void onJobFinished_successRecurringContentJob_reschedule() {
        JobInvocation jobInvocation = receiver.prepareJob(callbackMock, getBundleForContentJobExecutionRecurring());
        receiver.onJobFinished(jobInvocation, RESULT_SUCCESS);
        Mockito.verify(driverMock).schedule(jobArgumentCaptor.capture());
        // No need to callback when job finished.
        // Reschedule request is treated as two events: completion of old job and scheduling of new
        // job with the same parameters.
        Mockito.verifyZeroInteractions(callbackMock);
        Job rescheduledJob = jobArgumentCaptor.getValue();
        TestUtil.assertJobsEqual(jobInvocation, rescheduledJob);
    }

    @Test
    public void onJobFinished_failWithRetryRecurringContentJob_sendResult() {
        JobInvocation jobInvocation = receiver.prepareJob(callbackMock, getBundleForContentJobExecutionRecurring());
        receiver.onJobFinished(jobInvocation, RESULT_FAIL_RETRY);
        // If a job finishes with RESULT_FAIL_RETRY we don't need to send a reschedule request.
        // Rescheduling will erase previously triggered URIs.
        Mockito.verify(callbackMock).jobFinished(RESULT_FAIL_RETRY);
        Mockito.verifyZeroInteractions(driverMock);
    }

    @Test
    public void prepareJob() {
        Intent intent = new Intent();
        Bundle encode = TestUtil.encodeContentUriJob(TestUtil.getContentUriTrigger(), JOB_CODER);
        intent.putExtra(REQUEST_PARAM_EXTRAS, encode);
        Parcel container = Parcel.obtain();
        container.writeStrongBinder(new Binder());
        PendingCallback pcb = new PendingCallback(container);
        intent.putExtra("callback", pcb);
        ArrayList<Uri> uris = new ArrayList<>();
        uris.add(AUTHORITY_URI);
        uris.add(EXTERNAL_CONTENT_URI);
        intent.putParcelableArrayListExtra(PACKED_PARAM_TRIGGERED_URIS, uris);
        JobInvocation jobInvocation = receiver.prepareJob(intent);
        assertEquals(jobInvocation.getTriggerReason().getTriggeredContentUris(), uris);
    }

    @Test
    public void prepareJob_messenger() {
        JobInvocation jobInvocation = receiver.prepareJob(callbackMock, new Bundle());
        assertNull(jobInvocation);
        Mockito.verify(callbackMock).jobFinished(RESULT_FAIL_NORETRY);
    }

    @Test
    public void prepareJob_messenger_noExtras() {
        Bundle bundle = TestUtil.getBundleForContentJobExecution();
        JobInvocation jobInvocation = receiver.prepareJob(callbackMock, bundle);
        assertEquals(jobInvocation.getTriggerReason().getTriggeredContentUris(), TestUtil.URIS);
    }

    @Test
    public void onBind() {
        Intent intent = new Intent(ACTION_EXECUTE);
        IBinder binderA = receiver.onBind(intent);
        IBinder binderB = receiver.onBind(intent);
        assertEquals(binderA, binderB);
    }

    @Test
    public void onBind_nullIntent() {
        IBinder binder = receiver.onBind(null);
        assertNull(binder);
    }

    @Test
    public void onBind_wrongAction() {
        Intent intent = new Intent("test");
        IBinder binder = receiver.onBind(intent);
        assertNull(binder);
    }

    @Test
    @Config(sdk = VERSION_CODES.KITKAT)
    public void onBind_wrongBuild() {
        Intent intent = new Intent(ACTION_EXECUTE);
        IBinder binder = receiver.onBind(intent);
        assertNull(binder);
    }

    @Test
    public void onStartCommand_nullIntent() {
        GooglePlayReceiverTest.assertResultWasStartNotSticky(receiver.onStartCommand(null, 0, 101));
        Mockito.verify(receiver).stopSelf(101);
    }

    @Test
    public void onStartCommand_initAction() {
        Intent initIntent = new Intent("com.google.android.gms.gcm.SERVICE_ACTION_INITIALIZE");
        GooglePlayReceiverTest.assertResultWasStartNotSticky(receiver.onStartCommand(initIntent, 0, 101));
        Mockito.verify(receiver).stopSelf(101);
    }

    @Test
    public void onStartCommand_unknownAction() {
        Intent unknownIntent = new Intent("com.example.foo.bar");
        GooglePlayReceiverTest.assertResultWasStartNotSticky(receiver.onStartCommand(unknownIntent, 0, 101));
        GooglePlayReceiverTest.assertResultWasStartNotSticky(receiver.onStartCommand(unknownIntent, 0, 102));
        GooglePlayReceiverTest.assertResultWasStartNotSticky(receiver.onStartCommand(unknownIntent, 0, 103));
        InOrder inOrder = Mockito.inOrder(receiver);
        inOrder.verify(receiver).stopSelf(101);
        inOrder.verify(receiver).stopSelf(102);
        inOrder.verify(receiver).stopSelf(103);
    }

    @Test
    public void onStartCommand_executeActionWithEmptyExtras() {
        Intent execIntent = new Intent("com.google.android.gms.gcm.ACTION_TASK_READY");
        GooglePlayReceiverTest.assertResultWasStartNotSticky(receiver.onStartCommand(execIntent, 0, 101));
        Mockito.verify(receiver).stopSelf(101);
    }

    @Test
    public void onStartCommand_executeAction() {
        JobInvocation job = new JobInvocation.Builder().setTag("tag").setService("com.example.foo.FooService").setTrigger(NOW).setRetryStrategy(DEFAULT_EXPONENTIAL).setLifetime(UNTIL_NEXT_BOOT).setConstraints(new int[]{ DEVICE_IDLE }).build();
        Intent execIntent = new Intent("com.google.android.gms.gcm.ACTION_TASK_READY").putExtra("extras", new JobCoder(PACKED_PARAM_BUNDLE_PREFIX).encode(job, new Bundle())).putExtra("callback", new InspectableBinder().toPendingCallback());
        GooglePlayReceiverTest.assertResultWasStartNotSticky(receiver.onStartCommand(execIntent, 0, 101));
        Mockito.verify(receiver, Mockito.never()).stopSelf(ArgumentMatchers.anyInt());
        Mockito.verify(executionDelegatorMock).executeJob(ArgumentMatchers.any(JobInvocation.class));
        receiver.onJobFinished(job, RESULT_SUCCESS);
        Mockito.verify(receiver).stopSelf(101);
    }
}

