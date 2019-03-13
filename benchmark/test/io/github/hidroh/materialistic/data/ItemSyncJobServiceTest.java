/**
 * Copyright (c) 2016 Ha Duy Trung
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.hidroh.materialistic.data;


import Build.VERSION_CODES;
import ConnectivityManager.TYPE_WIFI;
import Context.CONNECTIVITY_SERVICE;
import Context.JOB_SCHEDULER_SERVICE;
import NetworkInfo.State.CONNECTED;
import R.string.pref_saved_item_sync;
import RuntimeEnvironment.application;
import SyncDelegate.Job;
import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import io.github.hidroh.materialistic.test.TestRunner;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ServiceController;
import org.robolectric.shadows.ShadowNetworkInfo;


@TargetApi(VERSION_CODES.LOLLIPOP)
@RunWith(TestRunner.class)
public class ItemSyncJobServiceTest {
    private ServiceController<ItemSyncJobServiceTest.TestItemSyncJobService> controller;

    private ItemSyncJobServiceTest.TestItemSyncJobService service;

    @Captor
    ArgumentCaptor<SyncDelegate.ProgressListener> listenerCaptor;

    @Test
    public void testScheduledJob() {
        PreferenceManager.getDefaultSharedPreferences(application).edit().putBoolean(application.getString(pref_saved_item_sync), true).apply();
        Shadows.shadowOf(((ConnectivityManager) (application.getSystemService(CONNECTIVITY_SERVICE)))).setActiveNetworkInfo(ShadowNetworkInfo.newInstance(null, TYPE_WIFI, 0, true, CONNECTED));
        new SyncScheduler().scheduleSync(application, "1");
        List<JobInfo> pendingJobs = Shadows.shadowOf(((JobScheduler) (application.getSystemService(JOB_SCHEDULER_SERVICE)))).getAllPendingJobs();
        assertThat(pendingJobs).isNotEmpty();
        JobInfo actual = pendingJobs.get(0);
        assertThat(actual.getService().getClassName()).isEqualTo(ItemSyncJobService.class.getName());
    }

    @Test
    public void testStartJob() {
        JobParameters jobParameters = Mockito.mock(JobParameters.class);
        Mockito.when(jobParameters.getExtras()).thenReturn(new SyncDelegate.JobBuilder(service, "1").build().toPersistableBundle());
        Mockito.when(jobParameters.getJobId()).thenReturn(2);
        service.onStartJob(jobParameters);
        Mockito.verify(service.syncDelegate).subscribe(listenerCaptor.capture());
        Mockito.verify(service.syncDelegate).performSync(ArgumentMatchers.any(Job.class));
        listenerCaptor.getValue().onDone("2");
    }

    public static class TestItemSyncJobService extends ItemSyncJobService {
        SyncDelegate syncDelegate = Mockito.mock(SyncDelegate.class);

        @NonNull
        @Override
        SyncDelegate createSyncDelegate() {
            return syncDelegate;
        }
    }
}

