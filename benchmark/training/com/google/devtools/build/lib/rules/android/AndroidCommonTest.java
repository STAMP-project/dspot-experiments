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


import MultidexMode.LEGACY;
import MultidexMode.MANUAL_MAIN_DEX;
import MultidexMode.NATIVE;
import MultidexMode.OFF;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.rules.android.AndroidRuleClasses.MultidexMode;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests common for Android rules.
 */
@RunWith(JUnit4.class)
public class AndroidCommonTest extends BuildViewTestCase {
    // regression test for #3169099
    @Test
    public void testLibrarySrcs() throws Exception {
        scratch.file("java/srcs/BUILD", "android_library(name = 'valid', srcs = ['a.java', 'b.srcjar', ':gvalid', ':gmix'])", "android_library(name = 'invalid', srcs = ['a.properties', ':ginvalid'])", "android_library(name = 'mix', srcs = ['a.java', 'a.properties'])", "genrule(name = 'gvalid', srcs = ['a.java'], outs = ['b.java'], cmd = '')", "genrule(name = 'ginvalid', srcs = ['a.java'], outs = ['b.properties'], cmd = '')", "genrule(name = 'gmix', srcs = ['a.java'], outs = ['c.java', 'c.properties'], cmd = '')");
        assertSrcsValidityForRuleType("android_library", ".java or .srcjar");
    }

    // regression test for #3169099
    @Test
    public void testBinarySrcs() throws Exception {
        scratch.file("java/srcs/BUILD", "android_binary(name = 'empty', manifest = 'AndroidManifest.xml', srcs = [])", ("android_binary(name = 'valid', manifest = 'AndroidManifest.xml', " + "srcs = ['a.java', 'b.srcjar', ':gvalid', ':gmix'])"), ("android_binary(name = 'invalid', manifest = 'AndroidManifest.xml', " + "srcs = ['a.properties', ':ginvalid'])"), ("android_binary(name = 'mix', manifest = 'AndroidManifest.xml', " + "srcs = ['a.java', 'a.properties'])"), "genrule(name = 'gvalid', srcs = ['a.java'], outs = ['b.java'], cmd = '')", "genrule(name = 'ginvalid', srcs = ['a.java'], outs = ['b.properties'], cmd = '')", "genrule(name = 'gmix', srcs = ['a.java'], outs = ['c.java', 'c.properties'], cmd = '')");
        assertSrcsValidityForRuleType("android_binary", ".java or .srcjar");
    }

    /**
     * Tests expected values of
     * {@link com.google.devtools.build.lib.rules.android.AndroidRuleClasses.MultidexMode}.
     */
    @Test
    public void testMultidexModeEnum() throws Exception {
        assertThat(MultidexMode.getValidValues()).containsExactly("native", "legacy", "manual_main_dex", "off");
        assertThat(MultidexMode.fromValue("native")).isSameAs(NATIVE);
        assertThat(NATIVE.getAttributeValue()).isEqualTo("native");
        assertThat(MultidexMode.fromValue("legacy")).isSameAs(LEGACY);
        assertThat(LEGACY.getAttributeValue()).isEqualTo("legacy");
        assertThat(MultidexMode.fromValue("manual_main_dex")).isSameAs(MANUAL_MAIN_DEX);
        assertThat(MANUAL_MAIN_DEX.getAttributeValue()).isEqualTo("manual_main_dex");
        assertThat(MultidexMode.fromValue("off")).isSameAs(OFF);
        assertThat(OFF.getAttributeValue()).isEqualTo("off");
    }

    /**
     * Tests that each multidex mode produces the expected output dex classes file name.
     */
    @Test
    public void testOutputDexforMultidexModes() throws Exception {
        assertThat(OFF.getOutputDexFilename()).isEqualTo("classes.dex");
        assertThat(LEGACY.getOutputDexFilename()).isEqualTo("classes.dex.zip");
        assertThat(NATIVE.getOutputDexFilename()).isEqualTo("classes.dex.zip");
    }
}

