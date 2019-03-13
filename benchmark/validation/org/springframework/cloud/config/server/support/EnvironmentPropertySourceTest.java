/**
 * Copyright 2018-2019 the original author or authors.
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
package org.springframework.cloud.config.server.support;


import org.junit.Test;
import org.springframework.core.env.StandardEnvironment;


public class EnvironmentPropertySourceTest {
    private final StandardEnvironment env = new StandardEnvironment();

    @Test
    public void testEscapedPlaceholdersRemoved() {
        assertThat(EnvironmentPropertySource.resolvePlaceholders(this.env, "\\${abc}")).isEqualTo("${abc}");
        // JSON generated from jackson will be double escaped
        assertThat(EnvironmentPropertySource.resolvePlaceholders(this.env, "\\\\${abc}")).isEqualTo("${abc}");
    }
}

