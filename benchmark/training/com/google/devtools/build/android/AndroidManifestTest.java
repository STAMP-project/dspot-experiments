/**
 * Copyright 2018 The Bazel Authors. All rights reserved.
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


import com.google.common.truth.Truth;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for the AndroidManifest class
 */
@RunWith(JUnit4.class)
public class AndroidManifestTest {
    private FileSystem fileSystem;

    @Test
    public void parseMinSdkAndPackageName() throws Exception {
        final String packageName = "com.google.wooga";
        final String minSdk = "26";
        final AndroidManifest androidManifest = AndroidManifest.parseFrom(AndroidManifestTest.manifest(packageName, minSdk).write(fileSystem.getPath("tmp")));
        Truth.assertThat(androidManifest).isEqualTo(AndroidManifest.of(packageName, minSdk));
    }

    @Test
    public void parseMissingMinSdk() throws Exception {
        final String packageName = "com.google.wooga";
        final AndroidManifest androidManifest = AndroidManifest.parseFrom(AndroidManifestTest.manifest(packageName).write(fileSystem.getPath("tmp")));
        Truth.assertThat(androidManifest).isEqualTo(AndroidManifest.of(packageName, "1"));
    }

    @Test
    public void writeDummyManifestWithoutPlaceholdersNoMinSdk() throws Exception {
        final String packageName = "${applicationId}.wooga";
        final String packageForR = "com.google.android.wooga";
        final AndroidManifest androidManifest = AndroidManifest.parseFrom(AndroidManifestTest.manifest(packageName).write(fileSystem.getPath("tmp")));
        Truth.assertThat(Files.readAllLines(androidManifest.writeDummyManifestForAapt(fileSystem.getPath("dummy-manifest"), packageForR), StandardCharsets.UTF_8)).containsExactly("<?xml version='1.0' encoding='utf-8'?>", "<manifest xmlns:android='http://schemas.android.com/apk/res/android'", "package='com.google.android.wooga'>", "<application/>", "<uses-sdk android:minSdkVersion='1'/>", "</manifest>").inOrder();
    }

    @Test
    public void writeDummyManifestWithoutPlaceholdersAndMinSdk() throws Exception {
        final String packageName = "${applicationId}.wooga";
        final String packageForR = "com.google.android.wooga";
        final AndroidManifest androidManifest = AndroidManifest.parseFrom(AndroidManifestTest.manifest(packageName, "26").write(fileSystem.getPath("tmp")));
        Truth.assertThat(Files.readAllLines(androidManifest.writeDummyManifestForAapt(fileSystem.getPath("dummy-manifest"), packageForR), StandardCharsets.UTF_8)).containsExactly("<?xml version='1.0' encoding='utf-8'?>", "<manifest xmlns:android='http://schemas.android.com/apk/res/android'", "package='com.google.android.wooga'>", "<application/>", "<uses-sdk android:minSdkVersion='26'/>", "</manifest>").inOrder();
    }

    private interface Manifest {
        Path write(Path parent) throws IOException;
    }
}

