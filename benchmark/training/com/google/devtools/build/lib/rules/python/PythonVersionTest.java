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
import PythonVersion.PY2AND3;
import PythonVersion.PY2ONLY;
import PythonVersion.PY3;
import PythonVersion.PY3ONLY;
import PythonVersion._INTERNAL_SENTINEL;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link PythonVersion}.
 */
@RunWith(JUnit4.class)
public class PythonVersionTest {
    @Test
    public void isTargetValue() {
        assertThat(PY2.isTargetValue()).isTrue();
        assertThat(PY3.isTargetValue()).isTrue();
        assertThat(PY2AND3.isTargetValue()).isFalse();
        assertThat(PY2ONLY.isTargetValue()).isFalse();
        assertThat(PY3ONLY.isTargetValue()).isFalse();
        assertThat(_INTERNAL_SENTINEL.isTargetValue()).isFalse();
    }

    @Test
    public void parseTargetValue() {
        assertThat(PythonVersion.parseTargetValue("PY2")).isEqualTo(PY2);
        assertThat(PythonVersion.parseTargetValue("PY3")).isEqualTo(PY3);
        PythonVersionTest.assertIsInvalidForParseTargetValue("PY2AND3");
        PythonVersionTest.assertIsInvalidForParseTargetValue("PY2ONLY");
        PythonVersionTest.assertIsInvalidForParseTargetValue("PY3ONLY");
        PythonVersionTest.assertIsInvalidForParseTargetValue("_INTERNAL_SENTINEL");
        PythonVersionTest.assertIsInvalidForParseTargetValue("not an enum value");
    }

    @Test
    public void parseTargetOrSentinelValue() {
        assertThat(PythonVersion.parseTargetOrSentinelValue("PY2")).isEqualTo(PY2);
        assertThat(PythonVersion.parseTargetOrSentinelValue("PY3")).isEqualTo(PY3);
        PythonVersionTest.assertIsInvalidForParseTargetOrSentinelValue("PY2AND3");
        PythonVersionTest.assertIsInvalidForParseTargetOrSentinelValue("PY2ONLY");
        PythonVersionTest.assertIsInvalidForParseTargetOrSentinelValue("PY3ONLY");
        assertThat(PythonVersion.parseTargetOrSentinelValue("_INTERNAL_SENTINEL")).isEqualTo(_INTERNAL_SENTINEL);
        PythonVersionTest.assertIsInvalidForParseTargetOrSentinelValue("not an enum value");
    }

    @Test
    public void parseSrcsValue() {
        assertThat(PythonVersion.parseSrcsValue("PY2")).isEqualTo(PY2);
        assertThat(PythonVersion.parseSrcsValue("PY3")).isEqualTo(PY3);
        assertThat(PythonVersion.parseSrcsValue("PY2AND3")).isEqualTo(PY2AND3);
        assertThat(PythonVersion.parseSrcsValue("PY2ONLY")).isEqualTo(PY2ONLY);
        assertThat(PythonVersion.parseSrcsValue("PY3ONLY")).isEqualTo(PY3ONLY);
        PythonVersionTest.assertIsInvalidForParseSrcsValue("_INTERNAL_SENTINEL");
        PythonVersionTest.assertIsInvalidForParseSrcsValue("not an enum value");
    }
}

