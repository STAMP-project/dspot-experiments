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
package org.assertj.core.api.object;


import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


public class ObjectAssert_flatExtracting_Test {
    private Map<String, List<String>> mapOfList;

    @Test
    public void should_allow_assertions_on_flattened_values_extracted_from_given_map_keys() {
        Assertions.assertThat(mapOfList).flatExtracting("name", "job", "city").containsExactly("Dave", "Jeff", "Plumber", "Builder", "Dover", "Boston", "Paris");
        // order of values is the order of key then key values
        Assertions.assertThat(mapOfList).flatExtracting("city", "job", "name").containsExactly("Dover", "Boston", "Paris", "Plumber", "Builder", "Dave", "Jeff");
    }

    @Test
    public void should_extract_null_from_unknown_key() {
        Assertions.assertThat(mapOfList).flatExtracting("name", "id", "city").containsExactly("Dave", "Jeff", null, "Dover", "Boston", "Paris");
        Assertions.assertThat(mapOfList).flatExtracting("foo", "bar").containsOnlyNulls();
    }
}

