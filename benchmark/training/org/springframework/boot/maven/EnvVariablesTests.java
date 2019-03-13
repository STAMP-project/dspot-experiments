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


import java.util.Map;
import org.junit.Test;


/**
 * Tests for {@link EnvVariables}.
 *
 * @author Dmytro Nosan
 */
public class EnvVariablesTests {
    @Test
    public void asNull() {
        Map<String, String> args = new EnvVariables(null).asMap();
        assertThat(args).isEmpty();
    }

    @Test
    public void asArray() {
        assertThat(new EnvVariables(getTestArgs()).asArray()).contains("key=My Value", "key1= tt ", "key2=   ", "key3=");
    }

    @Test
    public void asMap() {
        assertThat(new EnvVariables(getTestArgs()).asMap()).containsExactly(entry("key", "My Value"), entry("key1", " tt "), entry("key2", "   "), entry("key3", ""));
    }
}

