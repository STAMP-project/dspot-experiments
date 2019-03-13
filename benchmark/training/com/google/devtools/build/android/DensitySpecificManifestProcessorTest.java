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
package com.google.devtools.build.android;


import AndroidManifestProcessor.ManifestProcessingException;
import com.google.common.collect.ImmutableList;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link DensitySpecificManifestProcessor}.
 */
@RunWith(JUnit4.class)
public class DensitySpecificManifestProcessorTest {
    private FileSystem fs;

    private Path tmp;

    @Test
    public void testNoDensities() throws Exception {
        Path manifest = createManifest("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "<manifest xmlns:android='http://schemas.android.com/apk/res/android'", "          package='com.google.test'>", "</manifest>");
        Path modified = new DensitySpecificManifestProcessor(ImmutableList.<String>of(), tmp.resolve("manifest-filtered/AndroidManifest.xml")).process(manifest);
        assertThat(((Object) (modified))).isEqualTo(manifest);
    }

    @Test
    public void testSingleDensity() throws Exception {
        ImmutableList<String> densities = ImmutableList.of("xhdpi");
        Path manifest = createManifest("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "<manifest xmlns:android='http://schemas.android.com/apk/res/android'", "          package='com.google.test'>", "</manifest>");
        Path modified = new DensitySpecificManifestProcessor(densities, tmp.resolve("manifest-filtered/AndroidManifest.xml")).process(manifest);
        assertThat(((Object) (modified))).isNotNull();
        checkModification(modified, densities);
    }

    @Test
    public void test280Density() throws Exception {
        ImmutableList<String> densities = ImmutableList.of("280dpi");
        Path manifest = createManifest("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "<manifest xmlns:android='http://schemas.android.com/apk/res/android'", "          package='com.google.test'>", "</manifest>");
        Path modified = new DensitySpecificManifestProcessor(densities, tmp.resolve("manifest-filtered/AndroidManifest.xml")).process(manifest);
        assertThat(((Object) (modified))).isNotNull();
        checkModification(modified, densities);
    }

    @Test
    public void testMultipleDensities() throws Exception {
        ImmutableList<String> densities = ImmutableList.of("xhdpi", "xxhdpi", "560dpi", "xxxhdpi");
        Path manifest = createManifest("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "<manifest xmlns:android='http://schemas.android.com/apk/res/android'", "          package='com.google.test'>", "</manifest>");
        Path modified = new DensitySpecificManifestProcessor(densities, tmp.resolve("manifest-filtered/AndroidManifest.xml")).process(manifest);
        assertThat(((Object) (modified))).isNotNull();
        checkModification(modified, densities);
    }

    @Test
    public void omitCompatibleScreensIfDensityUnsupported() throws Exception {
        ImmutableList<String> densities = ImmutableList.of("xhdpi", "340dpi", "xxhdpi");
        Path manifest = createManifest("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "<manifest xmlns:android='http://schemas.android.com/apk/res/android'", "          package='com.google.test'>", "</manifest>");
        Path modified = new DensitySpecificManifestProcessor(densities, tmp.resolve("manifest-filtered/AndroidManifest.xml")).process(manifest);
        assertThat(((Object) (modified))).isNotNull();
        checkCompatibleScreensOmitted(modified);
    }

    @Test
    public void testExistingCompatibleScreens() throws Exception {
        ImmutableList<String> densities = ImmutableList.of("xhdpi");
        Path manifest = createManifest("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "<manifest xmlns:android='http://schemas.android.com/apk/res/android'", "          package='com.google.test'>", "<compatible-screens>", "</compatible-screens>", "</manifest>");
        Path modified = new DensitySpecificManifestProcessor(densities, tmp.resolve("manifest-filtered/AndroidManifest.xml")).process(manifest);
        assertThat(((Object) (modified))).isNotNull();
        checkModification(modified, densities);
    }

    @Test
    public void testExistingSupersetCompatibleScreens() throws Exception {
        ImmutableList<String> densities = ImmutableList.of("ldpi");
        Path manifest = createManifest("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "<manifest xmlns:android='http://schemas.android.com/apk/res/android'", "          package='com.google.test'>", "<compatible-screens>", "  <screen android:screenSize='small' android:screenDensity='ldpi' />", "  <screen android:screenSize='normal' android:screenDensity='ldpi' />", "  <screen android:screenSize='large' android:screenDensity='ldpi' />", "  <screen android:screenSize='xlarge' android:screenDensity='ldpi' />", "  <screen android:screenSize='small' android:screenDensity='480' />", "  <screen android:screenSize='normal' android:screenDensity='480' />", "  <screen android:screenSize='large' android:screenDensity='480' />", "  <screen android:screenSize='xlarge' android:screenDensity='480' />", "</compatible-screens>", "</manifest>");
        Path modified = new DensitySpecificManifestProcessor(densities, tmp.resolve("manifest-filtered/AndroidManifest.xml")).process(manifest);
        assertThat(((Object) (modified))).isNotNull();
        checkModification(modified, ImmutableList.<String>of("ldpi", "xxhdpi"));
    }

    @Test
    public void testMalformedManifest() throws Exception {
        Path manifest = createManifest("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "<manifest xmlns:android='http://schemas.android.com/apk/res/android'", "          package='com.google.test'>", "</manifest>", "<manifest xmlns:android='http://schemas.android.com/apk/res/android'", "          package='com.google.test'>", "</manifest>");
        try {
            new DensitySpecificManifestProcessor(ImmutableList.of("xhdpi"), tmp.resolve("manifest-filtered/AndroidManifest.xml")).process(manifest);
            Assert.fail();
        } catch (AndroidManifestProcessor e) {
            assertThat(e).hasMessageThat().contains("must be well-formed");
        }
    }

    @Test
    public void testNoManifest() throws Exception {
        Path manifest = createManifest("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        try {
            new DensitySpecificManifestProcessor(ImmutableList.of("xhdpi"), tmp.resolve("manifest-filtered/AndroidManifest.xml")).process(manifest);
            Assert.fail();
        } catch (AndroidManifestProcessor e) {
            assertThat(e).hasMessageThat().contains("Premature end of file.");
        }
    }

    @Test
    public void testNestedManifest() throws Exception {
        Path manifest = createManifest("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "<manifest xmlns:android='http://schemas.android.com/apk/res/android'", "          package='com.google.test'>", "  <manifest xmlns:android='http://schemas.android.com/apk/res/android'", "            package='com.google.test'>", "  </manifest>", "</manifest>");
        try {
            new DensitySpecificManifestProcessor(ImmutableList.of("xhdpi"), tmp.resolve("manifest-filtered/AndroidManifest.xml")).process(manifest);
            Assert.fail();
        } catch (AndroidManifestProcessor e) {
            assertThat(e).hasMessageThat().contains("does not contain exactly one <manifest>");
        }
    }
}

