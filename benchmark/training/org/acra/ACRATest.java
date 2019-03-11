/**
 * Copyright (c) 2018
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
package org.acra;


import ReportField.STACK_TRACE;
import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import org.acra.builder.ReportBuilder;
import org.acra.collector.StacktraceCollector;
import org.acra.config.CoreConfiguration;
import org.acra.config.CoreConfigurationBuilder;
import org.acra.config.ReportingAdministrator;
import org.acra.data.CrashReportData;
import org.acra.plugins.SimplePluginLoader;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;


/**
 *
 *
 * @author lukas
 * @since 02.07.18
 */
@RunWith(RobolectricTestRunner.class)
public class ACRATest {
    @Test
    public void init() {
        Application application = RuntimeEnvironment.application;
        CoreConfigurationBuilder builder = new CoreConfigurationBuilder(application).setPluginLoader(new SimplePluginLoader(StacktraceCollector.class, ACRATest.TestAdministrator.class));
        ACRA.init(application, builder);
        ACRA.getErrorReporter().handleException(new RuntimeException());
    }

    @Test(expected = AssertionError.class)
    public void failing() {
        Application application = RuntimeEnvironment.application;
        CoreConfigurationBuilder builder = new CoreConfigurationBuilder(application).setPluginLoader(new SimplePluginLoader(ACRATest.FailingTestAdministrator.class));
        ACRA.init(application, builder);
        ACRA.getErrorReporter().handleException(new RuntimeException());
    }

    public static class TestAdministrator implements ReportingAdministrator {
        @Override
        public boolean shouldSendReport(@NonNull
        Context context, @NonNull
        CoreConfiguration config, @NonNull
        CrashReportData crashReportData) {
            Assert.assertTrue(crashReportData.containsKey(STACK_TRACE));
            Assert.assertThat(crashReportData.getString(STACK_TRACE), Matchers.containsString("RuntimeException"));
            return false;
        }
    }

    public static class FailingTestAdministrator implements ReportingAdministrator {
        @Override
        public boolean shouldStartCollecting(@NonNull
        Context context, @NonNull
        CoreConfiguration config, @NonNull
        ReportBuilder reportBuilder) {
            Assert.fail("Intended failure to test if assertions work");
            return false;
        }
    }
}

