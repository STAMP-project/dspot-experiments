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
package com.google.devtools.build.lib.bazel.rules.android;


import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.analysis.FilesToRunProvider;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.collect.nestedset.NestedSet;
import com.google.devtools.build.lib.packages.AttributeContainer;
import com.google.devtools.build.lib.packages.BuildFileNotFoundException;
import com.google.devtools.build.lib.skyframe.ConfiguredTargetAndData;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.testutil.MoreAsserts;
import com.google.devtools.build.lib.vfs.FileSystemUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link AndroidNdkRepositoryTest}.
 */
@RunWith(JUnit4.class)
public class AndroidNdkRepositoryTest extends BuildViewTestCase {
    @Test
    public void testApiLevelHighestVersionDetection() throws Exception {
        scratchPlatformsDirectories("arch-x86", 19, 20, 22, 24);
        FileSystemUtils.appendIsoLatin1(scratch.resolve("WORKSPACE"), "android_ndk_repository(", "    name = 'androidndk',", "    path = '/ndk',", ")");
        invalidatePackages();
        NestedSet<Artifact> x86ClangHighestApiLevelFilesToRun = getConfiguredTarget("@androidndk//:x86-clang3.8-gnu-libstdcpp-all_files").getProvider(FilesToRunProvider.class).getFilesToRun();
        assertThat(artifactsToStrings(x86ClangHighestApiLevelFilesToRun)).contains("src external/androidndk/ndk/platforms/android-24/arch-x86/usr/lib/libandroid.so");
        assertThat(artifactsToStrings(x86ClangHighestApiLevelFilesToRun)).doesNotContain("src external/androidndk/ndk/platforms/android-22/arch-x86/usr/lib/libandroid.so");
    }

    @Test
    public void testInvalidNdkReleaseTxt() throws Exception {
        scratchPlatformsDirectories("arch-x86", 24);
        FileSystemUtils.appendIsoLatin1(scratch.resolve("WORKSPACE"), "android_ndk_repository(", "    name = 'androidndk',", "    path = '/ndk',", "    api_level = 24,", ")");
        scratch.deleteFile("/ndk/source.properties");
        scratch.file("/ndk/RELEASE.TXT", "not a valid release string");
        invalidatePackages();
        assertThat(getConfiguredTarget("@androidndk//:files")).isNotNull();
        MoreAsserts.assertContainsEvent(eventCollector, ("The revision of the Android NDK referenced by android_ndk_repository rule 'androidndk' " + ("could not be determined (the revision string found is 'not a valid release string')." + " Bazel will attempt to treat the NDK as if it was r18.")));
    }

    @Test
    public void testInvalidNdkSourceProperties() throws Exception {
        scratchPlatformsDirectories("arch-x86", 24);
        FileSystemUtils.appendIsoLatin1(scratch.resolve("WORKSPACE"), "android_ndk_repository(", "    name = 'androidndk',", "    path = '/ndk',", "    api_level = 24,", ")");
        scratch.overwriteFile("/ndk/source.properties", "Pkg.Desc = Android NDK", "Pkg.Revision = invalid package revision");
        invalidatePackages();
        assertThat(getConfiguredTarget("@androidndk//:files")).isNotNull();
        MoreAsserts.assertContainsEvent(eventCollector, ("The revision of the Android NDK referenced by android_ndk_repository rule 'androidndk' " + ("could not be determined (the revision string found is 'invalid package revision'). " + "Bazel will attempt to treat the NDK as if it was r18.")));
    }

    @Test
    public void testUnsupportedNdkVersion() throws Exception {
        scratchPlatformsDirectories("arch-x86", 24);
        FileSystemUtils.appendIsoLatin1(scratch.resolve("WORKSPACE"), "android_ndk_repository(", "    name = 'androidndk',", "    path = '/ndk',", "    api_level = 24,", ")");
        scratch.overwriteFile("/ndk/source.properties", "Pkg.Desc = Android NDK", "Pkg.Revision = 19.0.3675639-beta2");
        invalidatePackages();
        assertThat(getConfiguredTarget("@androidndk//:files")).isNotNull();
        MoreAsserts.assertContainsEvent(eventCollector, ("The major revision of the Android NDK referenced by android_ndk_repository rule " + (("'androidndk' is 19. The major revisions supported by Bazel are " + "[10, 11, 12, 13, 14, 15, 16, 17, 18]. Bazel will attempt to treat the NDK as if it ") + "was r18.")));
    }

    @Test
    public void testMiscLibraries() throws Exception {
        scratchPlatformsDirectories("arch-x86", 19, 20, 22, 24);
        scratch.file(String.format("/ndk/sources/android/cpufeatures/cpu-features.c"));
        scratch.file(String.format("/ndk/sources/android/cpufeatures/cpu-features.h"));
        FileSystemUtils.appendIsoLatin1(scratch.resolve("WORKSPACE"), "android_ndk_repository(", "    name = 'androidndk',", "    path = '/ndk',", ")");
        invalidatePackages();
        ConfiguredTargetAndData cpufeatures = getConfiguredTargetAndData("@androidndk//:cpufeatures");
        assertThat(cpufeatures).isNotNull();
        AttributeContainer attributes = cpufeatures.getTarget().getAssociatedRule().getAttributeContainer();
        assertThat(attributes.isAttributeValueExplicitlySpecified("srcs")).isTrue();
        assertThat(attributes.getAttr("srcs").toString()).isEqualTo("[@androidndk//:ndk/sources/android/cpufeatures/cpu-features.c]");
        assertThat(attributes.isAttributeValueExplicitlySpecified("hdrs")).isTrue();
        assertThat(attributes.getAttr("hdrs").toString()).isEqualTo("[@androidndk//:ndk/sources/android/cpufeatures/cpu-features.h]");
    }

    @Test
    public void testMissingPlatformsDirectory() throws Exception {
        FileSystemUtils.appendIsoLatin1(scratch.resolve("WORKSPACE"), "android_ndk_repository(", "    name = 'androidndk',", "    path = '/ndk',", ")");
        try {
            // Invalidating configs re-runs AndroidNdkRepositoryFunction which results in a
            // RuntimeException. This way we can catch a checked exception instead.
            invalidatePackages(false);
            getTarget("@androidndk//:files");
            Assert.fail("android_ndk_repository should have failed due to missing NDK platforms dir.");
        } catch (BuildFileNotFoundException e) {
            assertThat(e).hasMessageThat().contains(("Expected directory at /ndk/platforms but it is not a directory or it does not " + "exist."));
        }
    }
}

