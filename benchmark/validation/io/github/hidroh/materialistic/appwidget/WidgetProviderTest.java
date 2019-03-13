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


import AppWidgetManager.EXTRA_APPWIDGET_ID;
import AppWidgetManager.EXTRA_APPWIDGET_IDS;
import R.id.subtitle;
import R.id.title;
import R.string.loading_text;
import R.string.pref_widget_query;
import R.string.pref_widget_section;
import R.string.pref_widget_section_value_best;
import R.string.pref_widget_theme;
import R.string.pref_widget_theme_value_dark;
import R.string.pref_widget_theme_value_light;
import R.string.title_activity_best;
import RuntimeEnvironment.application;
import android.app.AlarmManager;
import android.app.job.JobScheduler;
import android.appwidget.AppWidgetManager;
import android.view.View;
import io.github.hidroh.materialistic.BuildConfig;
import io.github.hidroh.materialistic.test.TestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;


@RunWith(TestRunner.class)
public class WidgetProviderTest {
    private WidgetProvider widgetProvider;

    private AlarmManager alarmManager;

    private AppWidgetManager widgetManager;

    private JobScheduler jobScheduler;

    private int appWidgetId;

    @Config(sdk = 18)
    @Test
    public void testDeleteCancelAlarm() {
        configure(appWidgetId);
        assertThat(Shadows.shadowOf(alarmManager).getNextScheduledAlarm()).isNotNull();
        widgetProvider.onDeleted(application, new int[]{ appWidgetId });
        assertThat(Shadows.shadowOf(alarmManager).getNextScheduledAlarm()).isNull();
    }

    @Config(sdk = 21)
    @Test
    public void testDeleteCancelJob() {
        configure(appWidgetId);
        assertThat(Shadows.shadowOf(jobScheduler).getAllPendingJobs()).isNotEmpty();
        widgetProvider.onDeleted(application, new int[]{ appWidgetId });
        // TODO
        // assertThat(shadowOf(jobScheduler).getAllPendingJobs()).isEmpty();
    }

    @Config(sdk = 18)
    @Test
    public void testAlarmAfterReboot() {
        // rebooting should update widget again via update broadcast
        widgetProvider.onReceive(application, new android.content.Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE).putExtra(EXTRA_APPWIDGET_IDS, new int[]{ appWidgetId }));
        assertThat(Shadows.shadowOf(alarmManager).getNextScheduledAlarm()).isNotNull();
        widgetProvider.onDeleted(application, new int[]{ appWidgetId });
        assertThat(Shadows.shadowOf(alarmManager).getNextScheduledAlarm()).isNull();
    }

    @Config(sdk = 21)
    @Test
    public void testJobAfterReboot() {
        // rebooting should update widget again via update broadcast
        widgetProvider.onReceive(application, new android.content.Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE).putExtra(EXTRA_APPWIDGET_IDS, new int[]{ appWidgetId }));
        assertThat(Shadows.shadowOf(jobScheduler).getAllPendingJobs()).isNotEmpty();
    }

    @Test
    public void testUpdateBest() {
        application.getSharedPreferences(("WidgetConfiguration_" + (appWidgetId)), MODE_PRIVATE).edit().putString(application.getString(pref_widget_theme), application.getString(pref_widget_theme_value_dark)).putString(application.getString(pref_widget_section), application.getString(pref_widget_section_value_best)).apply();
        widgetProvider.onReceive(application, new android.content.Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE).putExtra(EXTRA_APPWIDGET_IDS, new int[]{ appWidgetId }));
        View view = Shadows.shadowOf(widgetManager).getViewFor(appWidgetId);
        assertThat(((android.widget.TextView) (view.findViewById(title)))).containsText(title_activity_best);
        assertThat(((android.widget.TextView) (view.findViewById(subtitle)))).doesNotContainText(loading_text);
    }

    @Test
    public void testRefreshQuery() {
        application.getSharedPreferences(("WidgetConfiguration_" + (appWidgetId)), MODE_PRIVATE).edit().putString(application.getString(pref_widget_theme), application.getString(pref_widget_theme_value_light)).putString(application.getString(pref_widget_query), "Google").apply();
        widgetProvider.onReceive(application, new android.content.Intent(((BuildConfig.APPLICATION_ID) + ".ACTION_REFRESH_WIDGET")).putExtra(EXTRA_APPWIDGET_ID, appWidgetId));
        View view = Shadows.shadowOf(widgetManager).getViewFor(appWidgetId);
        assertThat(((android.widget.TextView) (view.findViewById(title)))).containsText("Google");
        assertThat(((android.widget.TextView) (view.findViewById(subtitle)))).doesNotContainText(loading_text);
    }
}

