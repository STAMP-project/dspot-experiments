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
import java.nio.file.FileSystem;
import java.nio.file.Path;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for the {@link SerializedAndroidData}.
 */
@RunWith(JUnit4.class)
public class SerializedAndroidDataTest {
    private FileSystem fileSystem;

    private Path res;

    private Path otherRes;

    private Path assets;

    private Path otherAssets;

    private String label;

    private Path symbols;

    @Test
    public void flagFullParse() throws Exception {
        Truth.assertThat(SerializedAndroidData.valueOf("res#otherres;assets#otherassets;//some_target/foo:foo;symbols.bin", fileSystem)).isEqualTo(new SerializedAndroidData(ImmutableList.of(res, otherRes), ImmutableList.of(assets, otherAssets), label, symbols));
    }

    @Test
    public void flagParseWithNoSymbolsFile() throws Exception {
        Truth.assertThat(SerializedAndroidData.valueOf("res#otherres;assets#otherassets;//some_target/foo:foo;", fileSystem)).isEqualTo(new SerializedAndroidData(ImmutableList.of(res, otherRes), ImmutableList.of(assets, otherAssets), label, null));
    }

    @Test
    public void flagParseWithEmptyResources() throws Exception {
        Truth.assertThat(SerializedAndroidData.valueOf(";assets;//some_target/foo:foo;symbols.bin", fileSystem)).isEqualTo(new SerializedAndroidData(ImmutableList.<Path>of(), ImmutableList.of(assets), label, symbols));
    }

    @Test
    public void flagParseWithEmptyAssets() throws Exception {
        Truth.assertThat(SerializedAndroidData.valueOf("res;;//some_target/foo:foo;symbols.bin", fileSystem)).isEqualTo(new SerializedAndroidData(ImmutableList.of(res), ImmutableList.<Path>of(), label, symbols));
    }

    @Test
    public void flagParseWithEmptyResourcesAndAssets() throws Exception {
        Truth.assertThat(SerializedAndroidData.valueOf(";;//some_target/foo:foo;symbols.bin", fileSystem)).isEqualTo(new SerializedAndroidData(ImmutableList.<Path>of(), ImmutableList.<Path>of(), label, symbols));
    }

    @Test
    public void flagNoLabelFails() {
        try {
            SerializedAndroidData.valueOf(";;symbols.bin", fileSystem);
            Assert.fail("expected exception for bad flag format");
        } catch (IllegalArgumentException expected) {
        }
    }
}

