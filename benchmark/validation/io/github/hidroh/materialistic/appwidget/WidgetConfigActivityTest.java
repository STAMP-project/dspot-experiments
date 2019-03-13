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
package io.github.hidroh.materialistic.appwidget;


import Activity.RESULT_CANCELED;
import Activity.RESULT_OK;
import Build.VERSION_CODES;
import Context.JOB_SCHEDULER_SERVICE;
import R.id.button_ok;
import android.annotation.TargetApi;
import android.app.job.JobScheduler;
import io.github.hidroh.materialistic.test.TestRunner;
import io.github.hidroh.materialistic.test.shadow.ShadowPreferenceFragmentCompat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;


@SuppressWarnings("ConstantConditions")
@Config(shadows = { ShadowPreferenceFragmentCompat.class })
@RunWith(TestRunner.class)
public class WidgetConfigActivityTest {
    private ActivityController<WidgetConfigActivity> controller;

    private WidgetConfigActivity activity;

    @Test
    public void testCancel() {
        activity.onBackPressed();
        assertThat(Shadows.shadowOf(activity).getResultCode()).isEqualTo(RESULT_CANCELED);
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    @Test
    public void testOk() {
        activity.findViewById(button_ok).performClick();
        assertThat(Shadows.shadowOf(activity).getResultCode()).isEqualTo(RESULT_OK);
        assertThat(activity).isFinishing();
        assertThat(Shadows.shadowOf(((JobScheduler) (activity.getSystemService(JOB_SCHEDULER_SERVICE)))).getAllPendingJobs()).isNotEmpty();
    }
}

