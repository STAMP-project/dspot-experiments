/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2019 the original author or authors.
 */
package org.assertj.core.data;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests for <{@link MapEntry#toString()}.
 *
 * @author Alex Ruiz
 */
public class MapEntry_toString_Test {
    @Test
    public void should_implement_toString() {
        MapEntry<String, String> entry = MapEntry.entry("name", "Yoda");
        Assertions.assertThat(entry).hasToString("MapEntry[key=\"name\", value=\"Yoda\"]");
    }

    @Test
    public void should_implement_toString_using_standard_representation() {
        MapEntry<String, String[]> entry = MapEntry.entry("name", new String[]{ "Yoda" });
        Assertions.assertThat(entry).hasToString("MapEntry[key=\"name\", value=[\"Yoda\"]]");
    }
}

