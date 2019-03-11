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


import java.util.Map;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;


public class MapAssert_extracting_Test {
    private static final Object NAME = "name";

    private Map<Object, Object> map;

    @Test
    public void should_allow_assertions_on_values_extracted_from_given_map_keys() {
        Assertions.assertThat(map).extracting(MapAssert_extracting_Test.NAME, "age").contains("kawhi", 25);
    }

    @Test
    public void should_allow_assertions_on_values_extracted_from_given_extractors() {
        Assertions.assertThat(map).extracting(( m) -> m.get(NAME), ( m) -> m.get("age")).contains("kawhi", 25);
    }

    @Test
    public void should_extract_null_from_unknown_key() {
        Assertions.assertThat(map).extracting(MapAssert_extracting_Test.NAME, "id").contains("kawhi", ((Object) (null)));
    }

    @Test
    public void should_use_key_names_as_description() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(map).extracting(NAME, "age").isEmpty()).withMessageContaining("[Extracted: name, age]");
    }

    @Test
    public void should_keep_existing_description_if_set_when_extracting_values_list() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(map).as("check name and age").extracting(NAME, "age").isEmpty()).withMessageContaining("[check name and age]");
    }

    @Test
    public void should_fail_if_actual_is_null() {
        // GIVEN
        map = null;
        // WHEN
        Throwable error = Assertions.catchThrowable(() -> assertThat(map).extracting(NAME, "age"));
        // THEN
        Assertions.assertThat(error).hasMessage(FailureMessages.actualIsNull());
    }
}

