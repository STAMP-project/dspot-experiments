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


import PythonVersion.PY2;
import PythonVersion.PY3;
import TriState.YES;
import com.google.devtools.build.lib.analysis.util.ConfigurationTestCase;
import com.google.devtools.build.lib.testutil.MoreAsserts;
import com.google.devtools.common.options.OptionsParsingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link PythonOptions} and {@link PythonConfiguration}.
 */
@RunWith(JUnit4.class)
public class PythonConfigurationTest extends ConfigurationTestCase {
    @Test
    public void invalidTargetPythonValue_NotATargetValue() {
        OptionsParsingException expected = MoreAsserts.assertThrows(OptionsParsingException.class, () -> create("--force_python=PY2AND3"));
        assertThat(expected).hasMessageThat().contains("Not a valid Python major version");
    }

    @Test
    public void invalidTargetPythonValue_UnknownValue() {
        OptionsParsingException expected = MoreAsserts.assertThrows(OptionsParsingException.class, () -> create("--force_python=BEETLEJUICE"));
        assertThat(expected).hasMessageThat().contains("Not a valid Python major version");
    }

    @Test
    public void oldVersionFlagGatedByIncompatibleFlag() throws Exception {
        create("--incompatible_remove_old_python_version_api=false", "--force_python=PY2");
        checkError("`--force_python` is disabled by `--incompatible_remove_old_python_version_api`", "--incompatible_remove_old_python_version_api=true", "--force_python=PY2");
    }

    @Test
    public void py3IsDefaultFlagRequiresNewSemanticsFlag() throws Exception {
        checkError(("cannot enable `--incompatible_py3_is_default` without also enabling " + "`--incompatible_allow_python_version_transitions`"), "--incompatible_allow_python_version_transitions=false", "--incompatible_py3_is_default=true");
    }

    @Test
    public void getDefaultPythonVersion() throws Exception {
        // --incompatible_py3_is_default requires --incompatible_allow_python_version_transitions
        PythonOptions withoutPy3IsDefaultOpts = parsePythonOptions("--incompatible_allow_python_version_transitions=true", "--incompatible_py3_is_default=false");
        PythonOptions withPy3IsDefaultOpts = parsePythonOptions("--incompatible_allow_python_version_transitions=true", "--incompatible_py3_is_default=true");
        assertThat(withoutPy3IsDefaultOpts.getDefaultPythonVersion()).isEqualTo(PY2);
        assertThat(withPy3IsDefaultOpts.getDefaultPythonVersion()).isEqualTo(PY3);
    }

    @Test
    public void getPythonVersion_FallBackOnDefaultPythonVersion() throws Exception {
        // Run it twice with two different values for the incompatible flag to confirm it's actually
        // reading getDefaultPythonVersion() and not some other source of default values. Note that
        // --incompatible_py3_is_default requires --incompatible_allow_python_version_transitions.
        PythonOptions py2Opts = parsePythonOptions("--incompatible_allow_python_version_transitions=true", "--incompatible_py3_is_default=false");
        PythonOptions py3Opts = parsePythonOptions("--incompatible_allow_python_version_transitions=true", "--incompatible_py3_is_default=true");
        assertThat(py2Opts.getPythonVersion()).isEqualTo(PY2);
        assertThat(py3Opts.getPythonVersion()).isEqualTo(PY3);
    }

    @Test
    public void getPythonVersion_NewFlagTakesPrecedence() throws Exception {
        PythonTestUtils.ensureDefaultIsPY2();
        // --force_python is superseded by --python_version.
        PythonOptions opts = parsePythonOptions("--incompatible_remove_old_python_version_api=false", "--force_python=PY2", "--python_version=PY3");
        assertThat(opts.getPythonVersion()).isEqualTo(PY3);
    }

    @Test
    public void getPythonVersion_FallBackOnOldFlag() throws Exception {
        PythonTestUtils.ensureDefaultIsPY2();
        // --force_python is used because --python_version is absent.
        PythonOptions opts = parsePythonOptions("--incompatible_remove_old_python_version_api=false", "--force_python=PY3");
        assertThat(opts.getPythonVersion()).isEqualTo(PY3);
    }

    @Test
    public void canTransitionPythonVersion_OldSemantics_Yes() throws Exception {
        PythonTestUtils.ensureDefaultIsPY2();
        PythonOptions opts = parsePythonOptions("--incompatible_allow_python_version_transitions=false");
        assertThat(opts.canTransitionPythonVersion(PY3)).isTrue();
    }

    @Test
    public void canTransitionPythonVersion_OldSemantics_NoBecauseAlreadySet() throws Exception {
        PythonTestUtils.ensureDefaultIsPY2();
        PythonOptions optsWithOldFlag = parsePythonOptions("--incompatible_allow_python_version_transitions=false", "--incompatible_remove_old_python_version_api=false", "--force_python=PY2");
        PythonOptions optsWithNewFlag = parsePythonOptions("--incompatible_allow_python_version_transitions=false", "--python_version=PY2");
        assertThat(optsWithOldFlag.canTransitionPythonVersion(PY3)).isFalse();
        assertThat(optsWithNewFlag.canTransitionPythonVersion(PY3)).isFalse();
    }

    @Test
    public void canTransitionPythonVersion_OldSemantics_NoBecauseNewValueSameAsDefault() throws Exception {
        PythonTestUtils.ensureDefaultIsPY2();
        PythonOptions opts = parsePythonOptions("--incompatible_allow_python_version_transitions=false");
        assertThat(opts.canTransitionPythonVersion(PY2)).isFalse();
    }

    @Test
    public void canTransitionPythonVersion_NewSemantics_Yes() throws Exception {
        PythonOptions opts = parsePythonOptions("--incompatible_allow_python_version_transitions=true", "--python_version=PY3");
        assertThat(opts.canTransitionPythonVersion(PY2)).isTrue();
    }

    @Test
    public void canTransitionPythonVersion_NewSemantics_NoBecauseSameAsCurrent() throws Exception {
        PythonOptions opts = // Set --force_python too, or else we fall into the "make --force_python consistent"
        // case.
        parsePythonOptions("--incompatible_allow_python_version_transitions=true", "--incompatible_remove_old_python_version_api=false", "--force_python=PY3", "--python_version=PY3");
        assertThat(opts.canTransitionPythonVersion(PY3)).isFalse();
    }

    @Test
    public void canTransitionPythonVersion_NewApi_YesBecauseForcePythonDisagrees() throws Exception {
        PythonOptions opts = // Test that even though getPythonVersion() would not be affected by a transition (it is
        // PY3 before and after), the transition is still considered necessary because
        // --force_python's value needs to be brought in sync.
        parsePythonOptions("--incompatible_allow_python_version_transitions=true", "--incompatible_remove_old_python_version_api=false", "--force_python=PY2", "--python_version=PY3");
        assertThat(opts.canTransitionPythonVersion(PY3)).isTrue();
    }

    @Test
    public void setPythonVersion() throws Exception {
        PythonOptions opts = parsePythonOptions("--incompatible_remove_old_python_version_api=false", "--force_python=PY2", "--python_version=PY2");
        opts.setPythonVersion(PY3);
        assertThat(opts.forcePython).isEqualTo(PY3);
        assertThat(opts.pythonVersion).isEqualTo(PY3);
    }

    @Test
    public void getHost_CopiesMostValues() throws Exception {
        PythonOptions opts = parsePythonOptions("--incompatible_allow_python_version_transitions=true", "--incompatible_remove_old_python_version_api=true", "--incompatible_py3_is_default=true", "--incompatible_py2_outputs_are_suffixed=true", "--build_python_zip=true", "--incompatible_disallow_legacy_py_provider=true", "--experimental_use_python_toolchains=true");
        PythonOptions hostOpts = ((PythonOptions) (opts.getHost()));
        assertThat(hostOpts.incompatibleAllowPythonVersionTransitions).isTrue();
        assertThat(hostOpts.incompatibleRemoveOldPythonVersionApi).isTrue();
        assertThat(hostOpts.incompatiblePy3IsDefault).isTrue();
        assertThat(hostOpts.incompatiblePy2OutputsAreSuffixed).isTrue();
        assertThat(hostOpts.buildPythonZip).isEqualTo(YES);
        assertThat(hostOpts.incompatibleDisallowLegacyPyProvider).isTrue();
        assertThat(hostOpts.incompatibleUsePythonToolchains).isTrue();
    }

    @Test
    public void getHost_AppliesHostForcePython() throws Exception {
        PythonTestUtils.ensureDefaultIsPY2();
        PythonOptions optsWithForcePythonFlag = parsePythonOptions("--incompatible_remove_old_python_version_api=false", "--force_python=PY2", "--host_force_python=PY3");
        PythonOptions optsWithPythonVersionFlag = parsePythonOptions("--python_version=PY2", "--host_force_python=PY3");
        // --incompatible_py3_is_default requires --incompatible_allow_python_version_transitions
        PythonOptions optsWithPy3IsDefaultFlag = // It's more interesting to set the incompatible flag true and force host to PY2, than
        // it is to set the flag false and force host to PY3.
        parsePythonOptions("--incompatible_allow_python_version_transitions=true", "--incompatible_py3_is_default=true", "--host_force_python=PY2");
        PythonOptions hostOptsWithForcePythonFlag = ((PythonOptions) (optsWithForcePythonFlag.getHost()));
        PythonOptions hostOptsWithPythonVersionFlag = ((PythonOptions) (optsWithPythonVersionFlag.getHost()));
        PythonOptions hostOptsWithPy3IsDefaultFlag = ((PythonOptions) (optsWithPy3IsDefaultFlag.getHost()));
        assertThat(hostOptsWithForcePythonFlag.getPythonVersion()).isEqualTo(PY3);
        assertThat(hostOptsWithPythonVersionFlag.getPythonVersion()).isEqualTo(PY3);
        assertThat(hostOptsWithPy3IsDefaultFlag.getPythonVersion()).isEqualTo(PY2);
    }

    @Test
    public void getHost_Py3IsDefaultFlagChangesHost() throws Exception {
        PythonTestUtils.ensureDefaultIsPY2();
        // --incompatible_py3_is_default requires --incompatible_allow_python_version_transitions
        PythonOptions opts = parsePythonOptions("--incompatible_allow_python_version_transitions=true", "--incompatible_py3_is_default=true");
        PythonOptions hostOpts = ((PythonOptions) (opts.getHost()));
        assertThat(hostOpts.getPythonVersion()).isEqualTo(PY3);
    }
}

