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


import com.google.common.collect.ImmutableList;
import com.google.common.truth.Truth;
import com.google.devtools.build.android.aapt2.CompiledResources;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for the {@link DependencyAndroidData}.
 */
@RunWith(JUnit4.class)
public class DependencyAndroidDataTest {
    private FileSystem fileSystem;

    private Path root;

    private Path rTxt;

    private Path manifest;

    private Path res;

    private Path otherRes;

    private Path assets;

    private Path otherAssets;

    private Path symbols;

    private CompiledResources compiledResources;

    @Test
    public void flagFullParse() {
        Truth.assertThat(DependencyAndroidData.valueOf("res#otherres:assets#otherassets:AndroidManifest.xml:r.txt:symbols.zip:symbols.bin", fileSystem)).isEqualTo(new DependencyAndroidData(ImmutableList.of(res, otherRes), ImmutableList.of(assets, otherAssets), manifest, rTxt, symbols, compiledResources));
    }

    @Test
    public void flagParseWithNoSymbolsFile() {
        Truth.assertThat(DependencyAndroidData.valueOf("res#otherres:assets#otherassets:AndroidManifest.xml:r.txt:", fileSystem)).isEqualTo(new DependencyAndroidData(ImmutableList.of(res, otherRes), ImmutableList.of(assets, otherAssets), manifest, rTxt, null, null));
    }

    @Test
    public void flagParseOmittedSymbolsFile() {
        Truth.assertThat(DependencyAndroidData.valueOf("res#otherres:assets#otherassets:AndroidManifest.xml:r.txt", fileSystem)).isEqualTo(new DependencyAndroidData(ImmutableList.of(res, otherRes), ImmutableList.of(assets, otherAssets), manifest, rTxt, null, null));
    }

    @Test
    public void flagParseWithEmptyResources() {
        Truth.assertThat(DependencyAndroidData.valueOf(":assets:AndroidManifest.xml:r.txt:symbols.bin", fileSystem)).isEqualTo(new DependencyAndroidData(ImmutableList.<Path>of(), ImmutableList.of(assets), manifest, rTxt, symbols, null));
    }

    @Test
    public void flagParseWithEmptyAssets() {
        Truth.assertThat(DependencyAndroidData.valueOf("res::AndroidManifest.xml:r.txt:symbols.bin", fileSystem)).isEqualTo(new DependencyAndroidData(ImmutableList.of(res), ImmutableList.<Path>of(), manifest, rTxt, symbols, null));
    }

    @Test
    public void flagParseWithEmptyResourcesAndAssets() {
        Truth.assertThat(DependencyAndroidData.valueOf("::AndroidManifest.xml:r.txt:symbols.bin", fileSystem)).isEqualTo(new DependencyAndroidData(ImmutableList.<Path>of(), ImmutableList.<Path>of(), manifest, rTxt, symbols, null));
    }

    @Test
    public void flagNoManifestFails() {
        try {
            DependencyAndroidData.valueOf(":::r.txt", fileSystem);
            Assert.fail("expected exception for bad flag format");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void flagMissingManifestFails() {
        try {
            DependencyAndroidData.valueOf("::Manifest.xml:r.txt:symbols.bin", fileSystem);
            Assert.fail("expected exception for bad flag format");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void flagNoRTxtFails() {
        try {
            DependencyAndroidData.valueOf("::AndroidManifest.xml:", fileSystem);
            Assert.fail("expected exception for bad flag format");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void flagNoRTxtWithSymbolsFails() {
        try {
            DependencyAndroidData.valueOf("::AndroidManifest.xml:::symbols.bin", fileSystem);
            Assert.fail("expected exception for bad flag format");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void flagMissingRTxtFails() {
        try {
            DependencyAndroidData.valueOf("::Manifest.xml:missing_file", fileSystem);
            Assert.fail("expected exception for bad flag format");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void flagMissingSymbolsFails() {
        try {
            DependencyAndroidData.valueOf("::Manifest.xml:r.txt:missing_file", fileSystem);
            Assert.fail("expected exception for bad flag format");
        } catch (IllegalArgumentException expected) {
        }
    }
}

