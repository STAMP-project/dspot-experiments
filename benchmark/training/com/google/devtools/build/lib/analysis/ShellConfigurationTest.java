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
package com.google.devtools.build.lib.analysis;


import OS.FREEBSD;
import OS.LINUX;
import OS.WINDOWS;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.vfs.PathFragment;
import com.google.devtools.common.options.Options;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link ShellConfiguration}.
 */
@RunWith(JUnit4.class)
public class ShellConfigurationTest extends BuildViewTestCase {
    @Test
    public void getShellExecutableUnset() {
        assertThat(ShellConfigurationTest.determineShellExecutable(LINUX, null)).isEqualTo(PathFragment.create("/bin/bash"));
        assertThat(ShellConfigurationTest.determineShellExecutable(FREEBSD, null)).isEqualTo(PathFragment.create("/usr/local/bin/bash"));
        assertThat(ShellConfigurationTest.determineShellExecutable(WINDOWS, null)).isEqualTo(PathFragment.create("c:/tools/msys64/usr/bin/bash.exe"));
    }

    @Test
    public void getShellExecutableIfSet() {
        PathFragment binBash = PathFragment.create("/bin/bash");
        assertThat(ShellConfigurationTest.determineShellExecutable(LINUX, binBash)).isEqualTo(PathFragment.create("/bin/bash"));
        assertThat(ShellConfigurationTest.determineShellExecutable(FREEBSD, binBash)).isEqualTo(PathFragment.create("/bin/bash"));
        assertThat(ShellConfigurationTest.determineShellExecutable(WINDOWS, binBash)).isEqualTo(PathFragment.create("/bin/bash"));
    }

    @Test
    public void optionsAlsoApplyToHost() {
        ShellConfiguration.Options o = Options.getDefaults(Options.class);
        o.shellExecutable = PathFragment.create("/my/shell/binary");
        ShellConfiguration.Options h = o.getHost();
        assertThat(h.shellExecutable).isEqualTo(PathFragment.create("/my/shell/binary"));
    }
}

