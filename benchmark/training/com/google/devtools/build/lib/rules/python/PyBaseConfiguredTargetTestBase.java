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
package com.google.devtools.build.lib.rules.python;


import PyInfo.PROVIDER;
import PyStructUtils.PROVIDER_NAME;
import PythonVersion.PY2;
import PythonVersion.PY3;
import com.google.devtools.build.lib.analysis.ConfiguredTarget;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import org.junit.Test;


/**
 * Tests that are common to {@code py_binary}, {@code py_test}, and {@code py_library}.
 */
public abstract class PyBaseConfiguredTargetTestBase extends BuildViewTestCase {
    private final String ruleName;

    protected PyBaseConfiguredTargetTestBase(String ruleName) {
        this.ruleName = ruleName;
    }

    @Test
    public void badSrcsVersionValue() throws Exception {
        // error:
        // build file:
        checkError("pkg", "foo", ("invalid value in 'srcs_version' attribute: " + ("has to be one of 'PY2', 'PY3', 'PY2AND3', 'PY2ONLY' " + "or 'PY3ONLY' instead of 'doesnotexist'")), ((ruleName) + "("), "    name = 'foo',", "    srcs_version = 'doesnotexist',", "    srcs = ['foo.py'])");
    }

    @Test
    public void goodSrcsVersionValue() throws Exception {
        scratch.file("pkg/BUILD", ((ruleName) + "("), "    name = 'foo',", "    srcs_version = 'PY2',", "    srcs = ['foo.py'])");
        getConfiguredTarget("//pkg:foo");
        assertNoEvents();
    }

    @Test
    public void srcsVersionClashesWithVersionFlagUnderOldSemantics() throws Exception {
        // Under the old version semantics, we fail on any Python target the moment a conflict between
        // srcs_version and the configuration is detected. Under the new semantics, py_binary and
        // py_test care if there's a conflict but py_library does not. This test case checks the old
        // semantics; the new semantics are checked in PyLibraryConfiguredTargetTest and
        // PyExecutableConfiguredTargetTestBase. Note that under the new semantics py_binary and
        // py_library ignore the version flag, so those tests use the attribute to set the version
        // instead.
        useConfiguration("--incompatible_allow_python_version_transitions=false", "--python_version=PY3");
        // error:
        // build file:
        checkError("pkg", "foo", "'//pkg:foo' can only be used with Python 2", ((ruleName) + "("), "    name = 'foo',", "    srcs = [':foo.py'],", "    srcs_version = 'PY2ONLY')");
    }

    @Test
    public void versionIs2IfUnspecified() throws Exception {
        PythonTestUtils.ensureDefaultIsPY2();
        // 
        scratch.file("pkg/BUILD", ((ruleName) + "("), "    name = 'foo',", "    srcs = ['foo.py'])");
        assertThat(getPythonVersion(getConfiguredTarget("//pkg:foo"))).isEqualTo(PY2);
    }

    @Test
    public void versionIs3IfForcedByFlagUnderOldSemantics() throws Exception {
        // Under the old version semantics, --force_python takes precedence over the rule's own
        // default_python_version attribute, so this test case applies equally well to py_library,
        // py_binary, and py_test. Under the new semantics the rule attribute takes precedence, so this
        // would only make sense for py_library; see PyLibraryConfiguredTargetTest for the analogous
        // test.
        PythonTestUtils.ensureDefaultIsPY2();
        useConfiguration("--incompatible_allow_python_version_transitions=false", "--python_version=PY3");
        // 
        scratch.file("pkg/BUILD", ((ruleName) + "("), "    name = 'foo',", "    srcs = ['foo.py'])");
        assertThat(getPythonVersion(getConfiguredTarget("//pkg:foo"))).isEqualTo(PY3);
    }

    @Test
    public void packageNameCannotHaveHyphen() throws Exception {
        // error:
        // build file:
        checkError("pkg-hyphenated", "foo", "paths to Python packages may not contain '-'", ((ruleName) + "("), "    name = 'foo',", "    srcs = ['foo.py'])");
    }

    @Test
    public void srcsPackageNameCannotHaveHyphen() throws Exception {
        // 
        scratch.file("pkg-hyphenated/BUILD", "exports_files(['bar.py'])");
        // error:
        // build file:
        checkError("otherpkg", "foo", "paths to Python packages may not contain '-'", ((ruleName) + "("), "    name = 'foo',", "    srcs = ['foo.py', '//pkg-hyphenated:bar.py'])");
    }

    @Test
    public void producesBothModernAndLegacyProviders_WithoutIncompatibleFlag() throws Exception {
        useConfiguration("--incompatible_disallow_legacy_py_provider=false");
        // 
        scratch.file("pkg/BUILD", ((ruleName) + "("), "    name = 'foo',", "    srcs = ['foo.py'])");
        ConfiguredTarget target = getConfiguredTarget("//pkg:foo");
        assertThat(target.get(PROVIDER)).isNotNull();
        assertThat(target.get(PROVIDER_NAME)).isNotNull();
    }

    @Test
    public void producesOnlyModernProvider_WithIncompatibleFlag() throws Exception {
        useConfiguration("--incompatible_disallow_legacy_py_provider=true");
        // 
        scratch.file("pkg/BUILD", ((ruleName) + "("), "    name = 'foo',", "    srcs = ['foo.py'])");
        ConfiguredTarget target = getConfiguredTarget("//pkg:foo");
        assertThat(target.get(PROVIDER)).isNotNull();
        assertThat(target.get(PROVIDER_NAME)).isNull();
    }

    @Test
    public void consumesLegacyProvider_WithoutIncompatibleFlag() throws Exception {
        useConfiguration("--incompatible_disallow_legacy_py_provider=false");
        scratch.file("pkg/rules.bzl", "def _myrule_impl(ctx):", "    return struct(py=struct(transitive_sources=depset([])))", "myrule = rule(", "    implementation = _myrule_impl,", ")");
        scratch.file("pkg/BUILD", "load(':rules.bzl', 'myrule')", "myrule(", "    name = 'dep',", ")", ((ruleName) + "("), "    name = 'foo',", "    srcs = ['foo.py'],", "    deps = [':dep'],", ")");
        ConfiguredTarget target = getConfiguredTarget("//pkg:foo");
        assertThat(target).isNotNull();
        assertNoEvents();
    }

    @Test
    public void rejectsLegacyProvider_WithIncompatibleFlag() throws Exception {
        useConfiguration("--incompatible_disallow_legacy_py_provider=true");
        scratch.file("pkg/rules.bzl", "def _myrule_impl(ctx):", "    return struct(py=struct(transitive_sources=depset([])))", "myrule = rule(", "    implementation = _myrule_impl,", ")");
        // error:
        // build file:
        checkError("pkg", "foo", "In dep '//pkg:dep': The legacy 'py' provider is disallowed.", "load(':rules.bzl', 'myrule')", "myrule(", "    name = 'dep',", ")", ((ruleName) + "("), "    name = 'foo',", "    srcs = ['foo.py'],", "    deps = [':dep'],", ")");
    }

    @Test
    public void consumesModernProvider() throws Exception {
        scratch.file("pkg/rules.bzl", "def _myrule_impl(ctx):", "    return [PyInfo(transitive_sources=depset([]))]", "myrule = rule(", "    implementation = _myrule_impl,", ")");
        scratch.file("pkg/BUILD", "load(':rules.bzl', 'myrule')", "myrule(", "    name = 'dep',", ")", ((ruleName) + "("), "    name = 'foo',", "    srcs = ['foo.py'],", "    deps = [':dep'],", ")");
        ConfiguredTarget target = getConfiguredTarget("//pkg:foo");
        assertThat(target).isNotNull();
        assertNoEvents();
    }

    @Test
    public void requiresProvider() throws Exception {
        scratch.file("pkg/rules.bzl", "def _myrule_impl(ctx):", "    return []", "myrule = rule(", "    implementation = _myrule_impl,", ")");
        // error:
        // build file:
        checkError("pkg", "foo", "'//pkg:dep' does not have mandatory providers", "load(':rules.bzl', 'myrule')", "myrule(", "    name = 'dep',", ")", ((ruleName) + "("), "    name = 'foo',", "    srcs = ['foo.py'],", "    deps = [':dep'],", ")");
    }
}

