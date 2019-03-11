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
package org.assertj.core.api.recursive.comparison;


import java.util.Set;
import java.util.regex.Pattern;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


public class RecursiveComparisonConfiguration_shouldIgnoreFields_Test {
    private RecursiveComparisonConfiguration recursiveComparisonConfiguration;

    @Test
    public void should_register_fields_path_to_ignore_without_duplicates() {
        // GIVEN
        recursiveComparisonConfiguration.ignoreFields("foo", "bar", "foo.bar", "bar");
        // WHEN
        Set<FieldLocation> fields = recursiveComparisonConfiguration.getIgnoredFields();
        // THEN
        Assertions.assertThat(fields).containsExactlyInAnyOrder(new FieldLocation("foo"), new FieldLocation("bar"), new FieldLocation("foo.bar"));
    }

    @Test
    public void ignoring_fields_with_regex_does_not_replace_previous_regexes() {
        // WHEN
        recursiveComparisonConfiguration.ignoreFieldsMatchingRegexes("foo");
        recursiveComparisonConfiguration.ignoreFieldsMatchingRegexes("bar", "baz");
        // THEN
        Assertions.assertThat(recursiveComparisonConfiguration.getIgnoredFieldsRegexes()).extracting(Pattern::pattern).containsExactlyInAnyOrder("foo", "bar", "baz");
    }
}

