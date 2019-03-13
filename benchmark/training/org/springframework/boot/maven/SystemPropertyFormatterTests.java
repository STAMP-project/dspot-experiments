/**
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.boot.maven;


import org.junit.Test;
import org.springframework.boot.maven.AbstractRunMojo.SystemPropertyFormatter;


/**
 * Tests for {@link AbstractRunMojo.SystemPropertyFormatter}.
 */
public class SystemPropertyFormatterTests {
    @Test
    public void parseEmpty() {
        assertThat(SystemPropertyFormatter.format(null, null)).isEqualTo("");
    }

    @Test
    public void parseOnlyKey() {
        assertThat(SystemPropertyFormatter.format("key1", null)).isEqualTo("-Dkey1");
    }

    @Test
    public void parseKeyWithValue() {
        assertThat(SystemPropertyFormatter.format("key1", "value1")).isEqualTo("-Dkey1=\"value1\"");
    }

    @Test
    public void parseKeyWithEmptyValue() {
        assertThat(SystemPropertyFormatter.format("key1", "")).isEqualTo("-Dkey1");
    }

    @Test
    public void parseKeyWithOnlySpaces() {
        assertThat(SystemPropertyFormatter.format("key1", "   ")).isEqualTo("-Dkey1=\"   \"");
    }
}

