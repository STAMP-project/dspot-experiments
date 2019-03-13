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
package com.google.devtools.build.lib.bazel.rules;


import BazelRuleClassProvider.ANDROID_RULES;
import BazelRuleClassProvider.PROTO_RULES;
import BazelRuleClassProvider.PYTHON_RULES;
import BazelRuleClassProvider.SHELL_ACTION_ENV;
import BazelRuleClassProvider.VARIOUS_WORKSPACE_RULES;
import CoreRules.INSTANCE;
import OS.LINUX;
import OS.WINDOWS;
import com.google.common.collect.ImmutableList;
import com.google.devtools.build.lib.actions.ActionEnvironment;
import com.google.devtools.build.lib.analysis.config.BuildOptions;
import com.google.devtools.build.lib.bazel.rules.BazelRuleClassProvider.StrictActionEnvOptions;
import com.google.devtools.build.lib.util.OS;
import com.google.devtools.build.lib.vfs.PathFragment;
import com.google.devtools.common.options.Options;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests consistency of {@link BazelRuleClassProvider}.
 */
@RunWith(JUnit4.class)
public class BazelRuleClassProviderTest {
    @Test
    public void coreConsistency() {
        checkModule(INSTANCE);
    }

    @Test
    public void coreWorkspaceConsistency() {
        checkModule(CoreWorkspaceRules.INSTANCE);
    }

    @Test
    public void genericConsistency() {
        checkModule(GenericRules.INSTANCE);
    }

    @Test
    public void configConsistency() {
        checkModule(ConfigRules.INSTANCE);
    }

    @Test
    public void shConsistency() {
        checkModule(ShRules.INSTANCE);
    }

    @Test
    public void protoConsistency() {
        checkModule(PROTO_RULES);
    }

    @Test
    public void cppConsistency() {
        checkModule(CcRules.INSTANCE);
    }

    @Test
    public void javaConsistency() {
        checkModule(JavaRules.INSTANCE);
    }

    @Test
    public void pythonConsistency() {
        checkModule(PYTHON_RULES);
    }

    @Test
    public void androidConsistency() {
        checkModule(ANDROID_RULES);
    }

    @Test
    public void objcConsistency() {
        checkModule(ObjcRules.INSTANCE);
    }

    @Test
    public void j2objcConsistency() {
        checkModule(J2ObjcRules.INSTANCE);
    }

    @Test
    public void variousWorkspaceConsistency() {
        checkModule(VARIOUS_WORKSPACE_RULES);
    }

    @Test
    public void toolchainConsistency() {
        checkModule(ToolchainRules.INSTANCE);
    }

    @Test
    public void strictActionEnv() throws Exception {
        if ((OS.getCurrent()) == (OS.WINDOWS)) {
            return;
        }
        BuildOptions options = BuildOptions.of(ImmutableList.of(Options.class, Options.class, StrictActionEnvOptions.class), "--experimental_strict_action_env", "--action_env=FOO=bar");
        ActionEnvironment env = SHELL_ACTION_ENV.getActionEnvironment(options);
        assertThat(env.getFixedEnv().toMap()).containsEntry("PATH", "/bin:/usr/bin");
        assertThat(env.getFixedEnv().toMap()).containsEntry("FOO", "bar");
    }

    @Test
    public void pathOrDefaultOnLinux() {
        assertThat(BazelRuleClassProvider.pathOrDefault(LINUX, null, null)).isEqualTo("/bin:/usr/bin");
        assertThat(BazelRuleClassProvider.pathOrDefault(LINUX, "/not/bin", null)).isEqualTo("/bin:/usr/bin");
    }

    @Test
    public void pathOrDefaultOnWindows() {
        assertThat(BazelRuleClassProvider.pathOrDefault(WINDOWS, null, null)).isNull();
        assertThat(BazelRuleClassProvider.pathOrDefault(WINDOWS, "C:/mypath", null)).isNull();
        assertThat(BazelRuleClassProvider.pathOrDefault(WINDOWS, "C:/mypath", PathFragment.create("D:/foo/shell"))).isEqualTo("D:\\foo;C:/mypath");
    }

    @Test
    public void optionsAlsoApplyToHost() {
        StrictActionEnvOptions o = Options.getDefaults(StrictActionEnvOptions.class);
        o.useStrictActionEnv = true;
        StrictActionEnvOptions h = o.getHost();
        assertThat(h.useStrictActionEnv).isTrue();
    }
}

