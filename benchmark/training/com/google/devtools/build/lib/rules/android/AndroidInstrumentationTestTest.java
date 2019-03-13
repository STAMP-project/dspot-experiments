/**
 * Copyright 2017 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.rules.android;


import com.google.common.collect.Iterables;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.analysis.ConfiguredTarget;
import com.google.devtools.build.lib.analysis.FileProvider;
import com.google.devtools.build.lib.analysis.RunfilesProvider;
import com.google.devtools.build.lib.collect.nestedset.NestedSet;
import com.google.devtools.build.lib.skyframe.ConfiguredTargetAndData;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link AndroidInstrumentationTest}.
 */
@RunWith(JUnit4.class)
public class AndroidInstrumentationTestTest extends AndroidBuildViewTestCase {
    @Test
    public void testTestExecutableRunfiles() throws Exception {
        ConfiguredTargetAndData androidInstrumentationTest = getConfiguredTargetAndData("//javatests/com/app/ait");
        NestedSet<Artifact> runfiles = androidInstrumentationTest.getConfiguredTarget().getProvider(RunfilesProvider.class).getDefaultRunfiles().getAllArtifacts();
        assertThat(runfiles).containsAllIn(getHostConfiguredTarget("//tools/android/emulated_device:nexus_6").getProvider(RunfilesProvider.class).getDefaultRunfiles().getAllArtifacts());
        assertThat(runfiles).containsAllIn(getHostConfiguredTarget("//java/com/server").getProvider(RunfilesProvider.class).getDefaultRunfiles().getAllArtifacts());
        assertThat(runfiles).containsAllIn(getHostConfiguredTarget(androidInstrumentationTest.getTarget().getAssociatedRule().getAttrDefaultValue("$test_entry_point").toString()).getProvider(RunfilesProvider.class).getDefaultRunfiles().getAllArtifacts());
        assertThat(runfiles).containsAllOf(AndroidInstrumentationTestTest.getDeviceFixtureScript(getConfiguredTarget("//javatests/com/app:device_fixture")), AndroidInstrumentationTestTest.getInstrumentationApk(getConfiguredTarget("//javatests/com/app:instrumentation_app")), AndroidInstrumentationTestTest.getTargetApk(getConfiguredTarget("//javatests/com/app:instrumentation_app")), Iterables.getOnlyElement(getConfiguredTarget("//javatests/com/app/ait:foo.txt").getProvider(FileProvider.class).getFilesToBuild()));
    }

    @Test
    public void testTestExecutableContents() throws Exception {
        ConfiguredTarget androidInstrumentationTest = getConfiguredTarget("//javatests/com/app/ait");
        assertThat(androidInstrumentationTest).isNotNull();
        String testExecutableScript = getTestStubContents(androidInstrumentationTest);
        assertThat(testExecutableScript).contains("instrumentation_apk=\"javatests/com/app/instrumentation_app.apk\"");
        assertThat(testExecutableScript).contains("target_apk=\"java/com/app/app.apk\"");
        assertThat(testExecutableScript).contains("support_apks=\"java/com/app/support.apk\"");
        assertThat(testExecutableScript).contains(("declare -A device_script_fixtures=( " + "[javatests/com/app/cmd_device_fixtures/device_fixture/cmd.sh]=false,true )"));
        assertThat(testExecutableScript).contains("host_service_fixture=\"java/com/server/server\"");
        assertThat(testExecutableScript).contains("host_service_fixture_services=\"foo,bar\"");
        assertThat(testExecutableScript).contains("device_script=\"${WORKSPACE_DIR}/tools/android/emulated_device/nexus_6\"");
        assertThat(testExecutableScript).contains("data_deps=\"javatests/com/app/ait/foo.txt\"");
    }

    @Test
    public void testAtMostOneHostServiceFixture() throws Exception {
        checkError("javatests/com/app/ait2", "ait", "android_instrumentation_test accepts at most one android_host_service_fixture", "android_host_service_fixture(", "  name = 'host_fixture',", "  executable = '//java/com/server',", "  service_names = ['foo', 'bar'],", ")", "android_instrumentation_test(", "  name = 'ait',", "  test_app = '//javatests/com/app:instrumentation_app',", "  target_device = '//tools/android/emulated_device:nexus_6',", "  fixtures = [", "    ':host_fixture',", "    '//javatests/com/app:host_fixture',", "  ],", ")");
    }

    @Test
    public void testInstrumentationBinaryIsInstrumenting() throws Exception {
        checkError("javatests/com/app/instr", "ait", ("The android_binary target //javatests/com/app/instr:app " + "is missing an 'instruments' attribute"), "android_binary(", "  name = 'app',", "  srcs = ['a.java'],", "  manifest = 'AndroidManifest.xml',", ")", "android_instrumentation_test(", "  name = 'ait',", "  test_app = ':app',", "  target_device = '//tools/android/emulated_device:nexus_6',", ")");
    }

    @Test
    public void testAndroidInstrumentationTestWithSkylarkDevice() throws Exception {
        scratch.file("javatests/com/app/skylarkdevice/local_adb_device.bzl", "def _impl(ctx):", "  ctx.actions.write(output=ctx.outputs.executable, content='', is_executable=True)", "  return [android_common.create_device_broker_info('LOCAL_ADB_SERVER')]", "local_adb_device = rule(implementation=_impl, executable=True)");
        scratch.file("javatests/com/app/skylarkdevice/BUILD", "load(':local_adb_device.bzl', 'local_adb_device')", "local_adb_device(name = 'local_adb_device')", "android_instrumentation_test(", "  name = 'ait',", "  test_app = '//javatests/com/app:instrumentation_app',", "  target_device = ':local_adb_device',", ")");
        String testExecutableScript = getTestStubContents(getConfiguredTarget("//javatests/com/app/skylarkdevice:ait"));
        assertThat(testExecutableScript).contains("device_broker_type=\"LOCAL_ADB_SERVER\"");
    }
}

