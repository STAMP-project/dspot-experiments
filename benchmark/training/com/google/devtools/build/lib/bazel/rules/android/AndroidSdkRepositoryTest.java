/**
 * Copyright 2016 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.bazel.rules.android;


import AndroidSdkProvider.PROVIDER;
import com.google.devtools.build.lib.analysis.ConfiguredTarget;
import com.google.devtools.build.lib.analysis.FilesToRunProvider;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.packages.BuildFileNotFoundException;
import com.google.devtools.build.lib.skyframe.ConfiguredTargetAndData;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.vfs.FileSystemUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link AndroidSdkRepositoryFunction}.
 */
@RunWith(JUnit4.class)
public class AndroidSdkRepositoryTest extends BuildViewTestCase {
    @Test
    public void testGeneratedAarImport() throws Exception {
        scratchPlatformsDirectories(25);
        scratchBuildToolsDirectories("26.0.1");
        scratchExtrasLibrary("extras/google/m2repository", "com.google.android", "foo", "1.0.0", "aar");
        FileSystemUtils.appendIsoLatin1(scratch.resolve("WORKSPACE"), "local_repository(name = 'bazel_tools', path = '/bazel_tools_workspace')", "android_sdk_repository(", "    name = 'androidsdk',", "    path = '/sdk',", ")");
        invalidatePackages();
        ConfiguredTargetAndData aarImportTarget = getConfiguredTargetAndData("@androidsdk//com.google.android:foo-1.0.0");
        assertThat(aarImportTarget).isNotNull();
        assertThat(aarImportTarget.getTarget().getAssociatedRule().getRuleClass()).isEqualTo("aar_import");
    }

    @Test
    public void testExportsExtrasLibraryArtifacts() throws Exception {
        scratchPlatformsDirectories(25);
        scratchBuildToolsDirectories("26.0.1");
        scratchExtrasLibrary("extras/google/m2repository", "com.google.android", "foo", "1.0.0", "aar");
        FileSystemUtils.appendIsoLatin1(scratch.resolve("WORKSPACE"), "local_repository(name = 'bazel_tools', path = '/bazel_tools_workspace')", "android_sdk_repository(", "    name = 'androidsdk',", "    path = '/sdk',", ")");
        invalidatePackages();
        ConfiguredTarget aarTarget = getConfiguredTarget("@androidsdk//:extras/google/m2repository/com/google/android/foo/1.0.0/foo.aar");
        assertThat(aarTarget).isNotNull();
    }

    @Test
    public void testKnownSdkMavenRepositories() throws Exception {
        scratchPlatformsDirectories(25);
        scratchBuildToolsDirectories("26.0.1");
        scratchExtrasLibrary("extras/google/m2repository", "com.google.android", "a", "1.0.0", "jar");
        scratchExtrasLibrary("extras/android/m2repository", "com.android.support", "b", "1.0.0", "aar");
        scratchExtrasLibrary("extras/m2repository", "com.android.support", "c", "1.0.1", "aar");
        FileSystemUtils.appendIsoLatin1(scratch.resolve("WORKSPACE"), "local_repository(name = 'bazel_tools', path = '/bazel_tools_workspace')", "android_sdk_repository(", "    name = 'androidsdk',", "    path = '/sdk',", ")");
        invalidatePackages();
        assertThat(getConfiguredTarget("@androidsdk//:extras/google/m2repository/com/google/android/a/1.0.0/a.jar")).isNotNull();
        assertThat(getConfiguredTarget("@androidsdk//:extras/android/m2repository/com/android/support/b/1.0.0/b.aar")).isNotNull();
        assertThat(getConfiguredTarget("@androidsdk//:extras/m2repository/com/android/support/c/1.0.1/c.aar")).isNotNull();
    }

    @Test
    public void testSystemImageDirectoriesAreFound() throws Exception {
        scratchPlatformsDirectories(25);
        scratchBuildToolsDirectories("26.0.1");
        FileSystemUtils.appendIsoLatin1(scratch.resolve("WORKSPACE"), "local_repository(name = 'bazel_tools', path = '/bazel_tools_workspace')", "android_sdk_repository(", "    name = 'androidsdk',", "    path = '/sdk',", ")");
        scratchSystemImagesDirectories("android-25/default/armeabi-v7a", "android-24/google_apis/x86");
        invalidatePackages();
        ConfiguredTarget android25ArmFilegroup = getConfiguredTarget("@androidsdk//:emulator_images_android_25_arm");
        assertThat(android25ArmFilegroup).isNotNull();
        assertThat(artifactsToStrings(android25ArmFilegroup.getProvider(FilesToRunProvider.class).getFilesToRun())).containsExactly("src external/androidsdk/system-images/android-25/default/armeabi-v7a/system.img");
        ConfiguredTarget android24X86Filegroup = getConfiguredTarget("@androidsdk//:emulator_images_google_24_x86");
        assertThat(android24X86Filegroup).isNotNull();
        assertThat(artifactsToStrings(android24X86Filegroup.getProvider(FilesToRunProvider.class).getFilesToRun())).containsExactly("src external/androidsdk/system-images/android-24/google_apis/x86/system.img");
    }

    // Regression test for https://github.com/bazelbuild/bazel/issues/3672.
    @Test
    public void testMalformedSystemImageDirectories() throws Exception {
        scratchPlatformsDirectories(25, 26);
        scratchBuildToolsDirectories("26.0.1");
        scratchSystemImagesDirectories("android-25/default/armeabi-v7a", "android-O/google_apis/x86");
        FileSystemUtils.appendIsoLatin1(scratch.resolve("WORKSPACE"), "local_repository(name = 'bazel_tools', path = '/bazel_tools_workspace')", "android_sdk_repository(", "    name = 'androidsdk',", "    path = '/sdk',", ")");
        invalidatePackages();
        assertThat(getConfiguredTarget("@androidsdk//:emulator_images_android_25_arm")).isNotNull();
    }

    @Test
    public void testBuildToolsHighestVersionDetection() throws Exception {
        scratchPlatformsDirectories(25);
        scratchBuildToolsDirectories("26.0.1", "26.0.2");
        FileSystemUtils.appendIsoLatin1(scratch.resolve("WORKSPACE"), "local_repository(name = 'bazel_tools', path = '/bazel_tools_workspace')", "android_sdk_repository(", "    name = 'androidsdk',", "    path = '/sdk',", "    api_level = 25,", ")");
        invalidatePackages();
        ConfiguredTarget androidSdk = getConfiguredTarget("@androidsdk//:sdk");
        assertThat(androidSdk).isNotNull();
        assertThat(androidSdk.get(PROVIDER).getBuildToolsVersion()).isEqualTo("26.0.2");
    }

    @Test
    public void testApiLevelHighestVersionDetection() throws Exception {
        scratchPlatformsDirectories(24, 25, 23);
        scratchBuildToolsDirectories("26.0.1");
        FileSystemUtils.appendIsoLatin1(scratch.resolve("WORKSPACE"), "local_repository(name = 'bazel_tools', path = '/bazel_tools_workspace')", "android_sdk_repository(", "    name = 'androidsdk',", "    path = '/sdk',", "    build_tools_version = '26.0.1',", ")");
        invalidatePackages();
        ConfiguredTarget androidSdk = getConfiguredTarget("@androidsdk//:sdk");
        assertThat(androidSdk).isNotNull();
        assertThat(androidSdk.get(PROVIDER).getAndroidJar().getExecPathString()).isEqualTo("external/androidsdk/platforms/android-25/android.jar");
    }

    @Test
    public void testMultipleAndroidSdkApiLevels() throws Exception {
        int[] apiLevels = new int[]{ 23, 24, 25 };
        scratchPlatformsDirectories(apiLevels);
        scratchBuildToolsDirectories("26.0.1");
        FileSystemUtils.appendIsoLatin1(scratch.resolve("WORKSPACE"), "local_repository(name = 'bazel_tools', path = '/bazel_tools_workspace')", "android_sdk_repository(", "    name = 'androidsdk',", "    path = '/sdk',", "    api_level = 24,", "    build_tools_version = '26.0.1',", ")");
        invalidatePackages();
        for (int apiLevel : apiLevels) {
            ConfiguredTarget androidSdk = getConfiguredTarget(("@androidsdk//:sdk-" + apiLevel));
            assertThat(androidSdk).isNotNull();
            assertThat(androidSdk.get(PROVIDER).getAndroidJar().getExecPathString()).isEqualTo(String.format("external/androidsdk/platforms/android-%d/android.jar", apiLevel));
        }
    }

    @Test
    public void testMissingApiLevel() throws Exception {
        scratchPlatformsDirectories(24);
        scratchBuildToolsDirectories("26.0.1");
        FileSystemUtils.appendIsoLatin1(scratch.resolve("WORKSPACE"), "local_repository(name = 'bazel_tools', path = '/bazel_tools_workspace')", "android_sdk_repository(", "    name = 'androidsdk',", "    path = '/sdk',", "    api_level = 25,", "    build_tools_version = '26.0.1',", ")");
        invalidatePackages();
        try {
            getTarget("@androidsdk//:files");
            Assert.fail("android_sdk_repository should have failed due to missing SDK api level.");
        } catch (BuildFileNotFoundException e) {
            assertThat(e.getMessage()).contains(("Android SDK api level 25 was requested but it is not installed in the Android SDK " + ("at /sdk. The api levels found were [24]. Please choose an available api level " + "or install api level 25 from the Android SDK Manager.")));
        }
    }

    // Regression test for https://github.com/bazelbuild/bazel/issues/2739.
    @Test
    public void testFilesInSystemImagesDirectories() throws Exception {
        scratchPlatformsDirectories(24);
        scratchBuildToolsDirectories("26.0.1");
        scratch.file("/sdk/system-images/.DS_Store");
        FileSystemUtils.appendIsoLatin1(scratch.resolve("WORKSPACE"), "local_repository(name = 'bazel_tools', path = '/bazel_tools_workspace')", "android_sdk_repository(", "    name = 'androidsdk',", "    path = '/sdk',", ")");
        invalidatePackages();
        assertThat(getConfiguredTarget("@androidsdk//:sdk")).isNotNull();
    }

    @Test
    public void testMissingPlatformsDirectory() throws Exception {
        scratchBuildToolsDirectories("26.0.1");
        FileSystemUtils.appendIsoLatin1(scratch.resolve("WORKSPACE"), "local_repository(name = 'bazel_tools', path = '/bazel_tools_workspace')", "android_sdk_repository(", "    name = 'androidsdk',", "    path = '/sdk',", ")");
        invalidatePackages();
        try {
            getTarget("@androidsdk//:files");
            Assert.fail("android_sdk_repository should have failed due to missing SDK platforms dir.");
        } catch (BuildFileNotFoundException e) {
            assertThat(e.getMessage()).contains(("Expected directory at /sdk/platforms but it is not a directory or it does " + "not exist."));
        }
    }

    @Test
    public void testMissingBuildToolsDirectory() throws Exception {
        scratchPlatformsDirectories(24);
        FileSystemUtils.appendIsoLatin1(scratch.resolve("WORKSPACE"), "local_repository(name = 'bazel_tools', path = '/bazel_tools_workspace')", "android_sdk_repository(", "    name = 'androidsdk',", "    path = '/sdk',", ")");
        invalidatePackages();
        try {
            getTarget("@androidsdk//:files");
            Assert.fail("android_sdk_repository should have failed due to missing SDK build tools dir.");
        } catch (BuildFileNotFoundException e) {
            assertThat(e.getMessage()).contains(("Expected directory at /sdk/build-tools but it is not a directory or it does " + "not exist."));
        }
    }
}

