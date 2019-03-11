package com.birbit.android.jobqueue.scheduling;


import Build.VERSION_CODES;
import GcmNetworkManager.RESULT_SUCCESS;
import NetworkUtil.DISCONNECTED;
import NetworkUtil.METERED;
import NetworkUtil.UNMETERED;
import Scheduler.Callback;
import SharedPreferences.Editor;
import Task.NETWORK_STATE_ANY;
import Task.NETWORK_STATE_CONNECTED;
import Task.NETWORK_STATE_UNMETERED;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import com.birbit.android.jobqueue.BuildConfig;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.gcm.TaskParams;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
@TargetApi(VERSION_CODES.LOLLIPOP)
public class GcmSchedulerTest {
    GcmScheduler gcmScheduler;

    GcmNetworkManager mockGcmNetworkManager;

    Context mockContext;

    Callback mockCallback;

    SharedPreferences mockSharedPreferences;

    Editor mockEditor;

    @Test
    public void requestSimple() {
        networkTest(METERED, NETWORK_STATE_CONNECTED);
    }

    @Test
    public void requestUnmetered() {
        networkTest(UNMETERED, NETWORK_STATE_UNMETERED);
    }

    @Test
    public void requestAnyNetwork() {
        networkTest(DISCONNECTED, NETWORK_STATE_ANY);
    }

    @Test
    public void delay() {
        long delaySeconds = 3;
        SchedulerConstraint constraint = Mockito.mock(SchedulerConstraint.class);
        Mockito.when(constraint.getNetworkStatus()).thenReturn(DISCONNECTED);
        Mockito.when(constraint.getDelayInMs()).thenReturn(TimeUnit.SECONDS.toMillis(delaySeconds));
        Mockito.when(constraint.getOverrideDeadlineInMs()).thenReturn(null);
        OneoffTask jobInfo = schedule(constraint);
        MatcherAssert.assertThat(jobInfo.isPersisted(), CoreMatchers.is(true));
        MatcherAssert.assertThat(jobInfo.getWindowStart(), CoreMatchers.is(delaySeconds));
        MatcherAssert.assertThat(jobInfo.getWindowEnd(), CoreMatchers.is((delaySeconds + (gcmScheduler.getExecutionWindowSizeInSeconds()))));
    }

    @Test
    public void deadline() {
        SchedulerConstraint constraint = Mockito.mock(SchedulerConstraint.class);
        Mockito.when(constraint.getNetworkStatus()).thenReturn(DISCONNECTED);
        Mockito.when(constraint.getOverrideDeadlineInMs()).thenReturn(TimeUnit.SECONDS.toMillis(37));
        OneoffTask jobInfo = schedule(constraint);
        MatcherAssert.assertThat(jobInfo.isPersisted(), CoreMatchers.is(true));
        MatcherAssert.assertThat(jobInfo.getWindowStart(), CoreMatchers.is(0L));
        MatcherAssert.assertThat(jobInfo.getWindowEnd(), CoreMatchers.is(37L));
    }

    @Test
    public void deadlineAndDelay() {
        long delaySeconds = 43;
        long deadlineSeconds = 57;
        SchedulerConstraint constraint = Mockito.mock(SchedulerConstraint.class);
        Mockito.when(constraint.getNetworkStatus()).thenReturn(DISCONNECTED);
        Mockito.when(constraint.getOverrideDeadlineInMs()).thenReturn(TimeUnit.SECONDS.toMillis(deadlineSeconds));
        Mockito.when(constraint.getDelayInMs()).thenReturn(TimeUnit.SECONDS.toMillis(delaySeconds));
        OneoffTask jobInfo = schedule(constraint);
        MatcherAssert.assertThat(jobInfo.isPersisted(), CoreMatchers.is(true));
        MatcherAssert.assertThat(jobInfo.getWindowStart(), CoreMatchers.is(delaySeconds));
        MatcherAssert.assertThat(jobInfo.getWindowEnd(), CoreMatchers.is(deadlineSeconds));
    }

    @Test
    public void bundle() throws Exception {
        SchedulerConstraint constraint = Mockito.mock(SchedulerConstraint.class);
        Mockito.when(constraint.getNetworkStatus()).thenReturn(METERED);
        Mockito.when(constraint.getUuid()).thenReturn("abc");
        Mockito.when(constraint.getDelayInMs()).thenReturn(345L);
        Mockito.when(constraint.getOverrideDeadlineInMs()).thenReturn(235L);
        OneoffTask jobInfo = schedule(constraint);
        Bundle extras = jobInfo.getExtras();
        SchedulerConstraint fromBundle = GcmScheduler.fromBundle(extras);
        MatcherAssert.assertThat(fromBundle.getNetworkStatus(), CoreMatchers.is(METERED));
        MatcherAssert.assertThat(fromBundle.getUuid(), CoreMatchers.is("abc"));
        MatcherAssert.assertThat(fromBundle.getDelayInMs(), CoreMatchers.is(345L));
        MatcherAssert.assertThat(fromBundle.getOverrideDeadlineInMs(), CoreMatchers.is(235L));
    }

    @Test
    public void badBundleOnStart() {
        // see https://github.com/yigit/android-priority-jobqueue/issues/254
        TaskParams params = Mockito.mock(TaskParams.class);
        PersistableBundle badBundle = Mockito.mock(PersistableBundle.class);
        Mockito.when(badBundle.getString(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenThrow(new NullPointerException());
        Mockito.when(badBundle.getString(ArgumentMatchers.anyString())).thenThrow(new NullPointerException());
        // return success since we cannot handle this bad bundle
        MatcherAssert.assertThat(gcmScheduler.onStartJob(params), CoreMatchers.is(RESULT_SUCCESS));
    }

    @Test
    public void bundleNullDeadline() throws Exception {
        SchedulerConstraint constraint = Mockito.mock(SchedulerConstraint.class);
        Mockito.when(constraint.getNetworkStatus()).thenReturn(METERED);
        Mockito.when(constraint.getUuid()).thenReturn("abc");
        Mockito.when(constraint.getDelayInMs()).thenReturn(345L);
        Mockito.when(constraint.getOverrideDeadlineInMs()).thenReturn(null);
        OneoffTask jobInfo = schedule(constraint);
        Bundle extras = jobInfo.getExtras();
        SchedulerConstraint fromBundle = GcmScheduler.fromBundle(extras);
        MatcherAssert.assertThat(fromBundle.getNetworkStatus(), CoreMatchers.is(METERED));
        MatcherAssert.assertThat(fromBundle.getUuid(), CoreMatchers.is("abc"));
        MatcherAssert.assertThat(fromBundle.getDelayInMs(), CoreMatchers.is(345L));
        MatcherAssert.assertThat(fromBundle.getOverrideDeadlineInMs(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void onStart1() {
        onStartTest(DISCONNECTED, 0, null);
    }

    @Test
    public void onStart2() {
        onStartTest(METERED, 0, null);
    }

    @Test
    public void onStart3() {
        onStartTest(UNMETERED, 0, null);
    }

    @Test
    public void onStart4() {
        onStartTest(METERED, 10, null);
    }

    @Test
    public void onStart5() {
        onStartTest(METERED, 10, null);
    }

    @Test
    public void onStart6() {
        onStartTest(METERED, 0, 100L);
    }

    @Test
    public void onStart7() {
        onStartTest(UNMETERED, 35, 77L);
    }
}

