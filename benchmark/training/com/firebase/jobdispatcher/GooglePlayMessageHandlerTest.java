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


import GooglePlayDriver.BACKEND_PACKAGE;
import JobService.RESULT_FAIL_NORETRY;
import Trigger.NOW;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import com.firebase.jobdispatcher.ExecutionDelegator.JobFinishedCallback;
import com.firebase.jobdispatcher.JobInvocation.Builder;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static GooglePlayMessageHandler.MSG_START_EXEC;
import static GooglePlayMessageHandler.MSG_STOP_EXEC;


/**
 * Tests {@link GooglePlayMessageHandler}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 21)
public class GooglePlayMessageHandlerTest {
    @Mock
    Looper looper;

    @Mock
    GooglePlayReceiver receiverMock;

    @Mock
    Context contextMock;

    @Mock
    AppOpsManager appOpsManager;

    @Mock
    Messenger messengerMock;

    @Mock
    JobFinishedCallback jobFinishedCallbackMock;

    @Captor
    ArgumentCaptor<JobServiceConnection> jobServiceConnectionCaptor;

    @Captor
    private ArgumentCaptor<IJobCallback> jobCallbackCaptor;

    @Captor
    private ArgumentCaptor<Bundle> bundleCaptor;

    @Mock
    private IRemoteJobService jobServiceMock;

    @Mock
    private IBinder iBinderMock;

    @Mock
    private ConstraintChecker constraintCheckerMock;

    ExecutionDelegator executionDelegator;

    GooglePlayMessageHandler handler;

    @Test
    public void handleMessage_nullNoException() throws Exception {
        handler.handleMessage(null);
    }

    @Test
    public void handleMessage_ignoreIfSenderIsNotGcm() throws Exception {
        Message message = Message.obtain();
        message.what = MSG_START_EXEC;
        Bundle data = new Bundle();
        data.putString(GooglePlayJobWriter.REQUEST_PARAM_TAG, "TAG");
        message.setData(data);
        message.replyTo = messengerMock;
        Mockito.doThrow(new SecurityException()).when(appOpsManager).checkPackage(message.sendingUid, BACKEND_PACKAGE);
        handler.handleMessage(message);
        Mockito.verify(receiverMock, Mockito.never()).prepareJob(ArgumentMatchers.any(GooglePlayMessengerCallback.class), ArgumentMatchers.eq(data));
    }

    @Test
    public void handleMessage_startExecution_noData() throws Exception {
        Message message = Message.obtain();
        message.what = MSG_START_EXEC;
        message.replyTo = messengerMock;
        handler.handleMessage(message);
        Mockito.verify(receiverMock, Mockito.never()).prepareJob(ArgumentMatchers.any(GooglePlayMessengerCallback.class), ArgumentMatchers.any(Bundle.class));
    }

    @Test
    public void handleMessage_startExecution() throws Exception {
        Message startExecutionMsg = Message.obtain();
        startExecutionMsg.what = MSG_START_EXEC;
        Bundle data = new Bundle();
        data.putString(GooglePlayJobWriter.REQUEST_PARAM_TAG, "TAG");
        startExecutionMsg.setData(data);
        startExecutionMsg.replyTo = messengerMock;
        JobInvocation jobInvocation = new Builder().setTag("tag").setService(TestJobService.class.getName()).setTrigger(NOW).build();
        Mockito.when(receiverMock.prepareJob(ArgumentMatchers.any(GooglePlayMessengerCallback.class), ArgumentMatchers.eq(data))).thenReturn(jobInvocation);
        Mockito.when(contextMock.bindService(ArgumentMatchers.any(Intent.class), ArgumentMatchers.any(JobServiceConnection.class), ArgumentMatchers.eq(BIND_AUTO_CREATE))).thenReturn(true);
        handler.handleMessage(startExecutionMsg);
        Mockito.verify(contextMock).bindService(ArgumentMatchers.any(Intent.class), jobServiceConnectionCaptor.capture(), ArgumentMatchers.eq(BIND_AUTO_CREATE));
        Assert.assertTrue(jobServiceConnectionCaptor.getValue().hasJobInvocation(jobInvocation));
    }

    @Test
    public void handleMessage_stopExecution_noUnbindWaitForTheResult() throws Exception {
        Message message = Message.obtain();
        message.what = MSG_STOP_EXEC;
        JobCoder jobCoder = GooglePlayReceiver.getJobCoder();
        Bundle data = TestUtil.encodeContentUriJob(TestUtil.getContentUriTrigger(), jobCoder);
        JobInvocation jobInvocation = jobCoder.decode(data).build();
        message.setData(data);
        message.replyTo = messengerMock;
        Mockito.when(contextMock.bindService(ArgumentMatchers.any(Intent.class), ArgumentMatchers.any(JobServiceConnection.class), ArgumentMatchers.eq(BIND_AUTO_CREATE))).thenReturn(true);
        executionDelegator.executeJob(jobInvocation);
        Mockito.verify(contextMock).bindService(ArgumentMatchers.any(Intent.class), jobServiceConnectionCaptor.capture(), ArgumentMatchers.eq(BIND_AUTO_CREATE));
        JobServiceConnection serviceConnection = jobServiceConnectionCaptor.getValue();
        Assert.assertTrue(serviceConnection.hasJobInvocation(jobInvocation));
        Mockito.when(iBinderMock.queryLocalInterface(ArgumentMatchers.anyString())).thenReturn(jobServiceMock);
        serviceConnection.onServiceConnected(null, iBinderMock);
        Mockito.verify(jobServiceMock).start(bundleCaptor.capture(), jobCallbackCaptor.capture());
        handler.handleMessage(message);
        Assert.assertEquals(serviceConnection, ExecutionDelegator.getJobServiceConnection(jobInvocation.getService()));
        Assert.assertFalse(serviceConnection.wasUnbound());
        // The connection must be active to process jobFinished
        Mockito.verify(contextMock, Mockito.never()).unbindService(serviceConnection);
        jobCallbackCaptor.getValue().jobFinished(bundleCaptor.getValue(), RESULT_FAIL_NORETRY);
        Mockito.verify(contextMock).unbindService(serviceConnection);
        Assert.assertTrue(serviceConnection.wasUnbound());
    }

    @Test
    public void handleMessage_stopExecution_invalidNoCrash() throws Exception {
        Message message = Message.obtain();
        message.what = MSG_STOP_EXEC;
        message.replyTo = messengerMock;
        handler.handleMessage(message);
    }
}

