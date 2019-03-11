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
package org.assertj.core.api.map;


import java.util.HashMap;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


public class MapAssert_raw_map_assertions_chained_after_base_assertions_Test {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void test_bug_485() {
        // https://github.com/joel-costigliola/assertj-core/issues/485
        Map map1 = new HashMap<>();
        map1.put("Key1", "Value1");
        map1.put("Key2", "Value2");
        Assertions.assertThat(map1).as("").containsOnlyKeys("Key1", "Key2");
    }
}

