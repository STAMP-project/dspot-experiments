/**
 * Copyright 2015 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.skyframe;


import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for the hash code calculated for Skylark RuleClasses based on the transitive closure
 * of the imports of their respective definition SkylarkEnvironments.
 */
@RunWith(JUnit4.class)
public class SkylarkFileContentHashTests extends BuildViewTestCase {
    @Test
    public void testHashInvariance() throws Exception {
        assertThat(getHash("pkg", "foo1")).isEqualTo(getHash("pkg", "foo1"));
    }

    @Test
    public void testHashInvarianceAfterOverwritingFileWithSameContents() throws Exception {
        String bar1 = getHash("pkg", "bar1");
        scratch.overwriteFile("bar/ext.bzl", "load('//helper:ext.bzl', 'rule_impl')", "", "bar1 = rule(implementation = rule_impl)");
        invalidatePackages();
        assertThat(getHash("pkg", "bar1")).isEqualTo(bar1);
    }

    @Test
    public void testHashSameForRulesDefinedInSameFile() throws Exception {
        assertThat(getHash("pkg", "foo2")).isEqualTo(getHash("pkg", "foo1"));
    }

    @Test
    public void testHashNotSameForRulesDefinedInDifferentFiles() throws Exception {
        assertNotEquals(getHash("pkg", "foo1"), getHash("pkg", "bar1"));
    }

    @Test
    public void testImmediateFileChangeChangesHash() throws Exception {
        String bar1 = getHash("pkg", "bar1");
        scratch.overwriteFile("bar/ext.bzl", "load('//helper:ext.bzl', 'rule_impl')", "# Some comments to change file hash", "", "bar1 = rule(implementation = rule_impl)");
        invalidatePackages();
        assertNotEquals(bar1, getHash("pkg", "bar1"));
    }

    @Test
    public void testTransitiveFileChangeChangesHash() throws Exception {
        String bar1 = getHash("pkg", "bar1");
        String foo1 = getHash("pkg", "foo1");
        String foo2 = getHash("pkg", "foo2");
        scratch.overwriteFile("helper/ext.bzl", "# Some comments to change file hash", "def rule_impl(ctx):", "  return None");
        invalidatePackages();
        assertNotEquals(bar1, getHash("pkg", "bar1"));
        assertNotEquals(foo1, getHash("pkg", "foo1"));
        assertNotEquals(foo2, getHash("pkg", "foo2"));
    }

    @Test
    public void testFileChangeDoesNotAffectRulesDefinedOutsideOfTransitiveClosure() throws Exception {
        String foo1 = getHash("pkg", "foo1");
        String foo2 = getHash("pkg", "foo2");
        scratch.overwriteFile("bar/ext.bzl", "load('//helper:ext.bzl', 'rule_impl')", "# Some comments to change file hash", "", "bar1 = rule(implementation = rule_impl)");
        invalidatePackages();
        assertThat(getHash("pkg", "foo1")).isEqualTo(foo1);
        assertThat(getHash("pkg", "foo2")).isEqualTo(foo2);
    }
}

