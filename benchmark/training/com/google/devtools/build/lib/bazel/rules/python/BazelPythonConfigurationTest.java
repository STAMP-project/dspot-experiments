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
package com.google.devtools.build.lib.bazel.rules.python;


import com.google.devtools.build.lib.analysis.config.InvalidConfigurationException;
import com.google.devtools.build.lib.analysis.util.ConfigurationTestCase;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.testutil.MoreAsserts;
import com.google.devtools.common.options.OptionsParsingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link BazelPythonConfiguration}.
 */
@RunWith(JUnit4.class)
public class BazelPythonConfigurationTest extends ConfigurationTestCase {
    @Test
    public void pythonTop() throws Exception {
        scratch.file("a/BUILD", "py_runtime(name='b', files=[], interpreter='c')");
        BazelPythonConfiguration config = create("--python_top=//a:b").getFragment(BazelPythonConfiguration.class);
        assertThat(config.getPythonTop()).isNotNull();
    }

    @Test
    public void pythonTop_malformedLabel() {
        OptionsParsingException expected = MoreAsserts.assertThrows(OptionsParsingException.class, () -> create("--python_top=//a:!b:"));
        assertThat(expected).hasMessageThat().contains("While parsing option --python_top");
    }

    @Test
    public void pythonPath() throws Exception {
        BazelPythonConfiguration config = create("--python_path=/pajama").getFragment(BazelPythonConfiguration.class);
        assertThat(config.getPythonPath()).isEqualTo("/pajama");
    }

    @Test
    public void pythonPathMustBeAbsolute() {
        InvalidConfigurationException expected = MoreAsserts.assertThrows(InvalidConfigurationException.class, () -> create("--python_path=pajama"));
        assertThat(expected).hasMessageThat().contains("python_path must be an absolute path");
    }
}

