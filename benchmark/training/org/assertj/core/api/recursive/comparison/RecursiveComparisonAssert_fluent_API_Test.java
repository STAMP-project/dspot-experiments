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


import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.assertj.core.internal.TypeComparators;
import org.assertj.core.test.AlwaysDifferentComparator;
import org.assertj.core.test.AlwaysEqualComparator;
import org.junit.jupiter.api.Test;


public class RecursiveComparisonAssert_fluent_API_Test {
    private static final Object ACTUAL = "";

    @Test
    public void usingRecursiveComparison_should_set_a_default_RecursiveComparisonConfiguration() {
        // WHEN
        RecursiveComparisonConfiguration recursiveComparisonConfiguration = Assertions.assertThat(RecursiveComparisonAssert_fluent_API_Test.ACTUAL).usingRecursiveComparison().getRecursiveComparisonConfiguration();
        // THEN
        Assertions.assertThat(recursiveComparisonConfiguration.isInStrictTypeCheckingMode()).isFalse();
        List<Map.Entry<Class<?>, Comparator<?>>> defaultComparators = TypeComparators.defaultTypeComparators().comparatorByTypes().collect(Collectors.toList());
        Assertions.assertThat(recursiveComparisonConfiguration.comparatorByTypes()).containsExactlyElementsOf(defaultComparators);
        Assertions.assertThat(recursiveComparisonConfiguration.comparatorByFields()).isEmpty();
        Assertions.assertThat(recursiveComparisonConfiguration.getIgnoreAllActualNullFields()).isFalse();
        Assertions.assertThat(recursiveComparisonConfiguration.getIgnoredFields()).isEmpty();
        Assertions.assertThat(recursiveComparisonConfiguration.getIgnoredFieldsRegexes()).isEmpty();
        Assertions.assertThat(recursiveComparisonConfiguration.getIgnoredOverriddenEqualsForFields()).isEmpty();
        Assertions.assertThat(recursiveComparisonConfiguration.getIgnoredOverriddenEqualsForTypes()).isEmpty();
        Assertions.assertThat(recursiveComparisonConfiguration.getIgnoredOverriddenEqualsRegexes()).isEmpty();
        Assertions.assertThat(recursiveComparisonConfiguration.hasCustomComparators()).isTrue();
    }

    @Test
    public void should_allow_to_enable_strict_mode_comparison() {
        // GIVEN
        RecursiveComparisonConfiguration recursiveComparisonConfiguration = new RecursiveComparisonConfiguration();
        // WHEN
        RecursiveComparisonConfiguration configuration = Assertions.assertThat(RecursiveComparisonAssert_fluent_API_Test.ACTUAL).usingRecursiveComparison(recursiveComparisonConfiguration).withStrictTypeChecking().getRecursiveComparisonConfiguration();
        // THEN
        Assertions.assertThat(configuration.isInStrictTypeCheckingMode()).isTrue();
    }

    @Test
    public void should_allow_to_use_its_own_RecursiveComparisonConfiguration() {
        // GIVEN
        RecursiveComparisonConfiguration recursiveComparisonConfiguration = new RecursiveComparisonConfiguration();
        // WHEN
        RecursiveComparisonConfiguration configuration = Assertions.assertThat(RecursiveComparisonAssert_fluent_API_Test.ACTUAL).usingRecursiveComparison(recursiveComparisonConfiguration).getRecursiveComparisonConfiguration();
        // THEN
        Assertions.assertThat(configuration).isSameAs(recursiveComparisonConfiguration);
    }

    @Test
    public void should_allow_to_ignore_all_actual_null_fields() {
        // WHEN
        RecursiveComparisonConfiguration configuration = Assertions.assertThat(RecursiveComparisonAssert_fluent_API_Test.ACTUAL).usingRecursiveComparison().ignoringActualNullFields().getRecursiveComparisonConfiguration();
        // THEN
        Assertions.assertThat(configuration.getIgnoreAllActualNullFields()).isTrue();
    }

    @Test
    public void should_allow_to_ignore_fields() {
        // GIVEN
        String field1 = "foo";
        String field2 = "foo.bar";
        // WHEN
        RecursiveComparisonConfiguration configuration = Assertions.assertThat(RecursiveComparisonAssert_fluent_API_Test.ACTUAL).usingRecursiveComparison().ignoringFields(field1, field2).getRecursiveComparisonConfiguration();
        // THEN
        Assertions.assertThat(configuration.getIgnoredFields()).containsExactly(FieldLocation.fielLocation(field1), FieldLocation.fielLocation(field2));
    }

    @Test
    public void should_allow_to_ignore_fields_matching_regexes() {
        // GIVEN
        String regex1 = "foo";
        String regex2 = ".*foo.*";
        // WHEN
        RecursiveComparisonConfiguration configuration = Assertions.assertThat(RecursiveComparisonAssert_fluent_API_Test.ACTUAL).usingRecursiveComparison().ignoringFieldsMatchingRegexes(regex1, regex2).getRecursiveComparisonConfiguration();
        // THEN
        Assertions.assertThat(configuration.getIgnoredFieldsRegexes()).extracting(Pattern::pattern).containsExactly(regex1, regex2);
    }

    @Test
    public void should_allow_to_ignore_overridden_equals_for_fields() {
        // GIVEN
        String field1 = "foo";
        String field2 = "foo.bar";
        // WHEN
        RecursiveComparisonConfiguration configuration = Assertions.assertThat(RecursiveComparisonAssert_fluent_API_Test.ACTUAL).usingRecursiveComparison().ignoringOverriddenEqualsForFields(field1, field2).getRecursiveComparisonConfiguration();
        // THEN
        Assertions.assertThat(configuration.getIgnoredOverriddenEqualsForFields()).containsExactly(FieldLocation.fielLocation(field1), FieldLocation.fielLocation(field2));
    }

    @Test
    public void should_allow_to_ignore_overridden_equals_by_regexes() {
        // GIVEN
        String regex1 = "foo";
        String regex2 = ".*foo.*";
        // WHEN
        RecursiveComparisonConfiguration configuration = Assertions.assertThat(RecursiveComparisonAssert_fluent_API_Test.ACTUAL).usingRecursiveComparison().ignoringOverriddenEqualsForFieldsMatchingRegexes(regex1, regex2).getRecursiveComparisonConfiguration();
        // THEN
        Assertions.assertThat(configuration.getIgnoredOverriddenEqualsRegexes()).extracting(Pattern::pattern).containsExactly(regex1, regex2);
    }

    @Test
    public void should_allow_to_ignore_overridden_equals_for_types() {
        // GIVEN
        Class<String> type1 = String.class;
        Class<Date> type2 = Date.class;
        // WHEN
        RecursiveComparisonConfiguration configuration = Assertions.assertThat(RecursiveComparisonAssert_fluent_API_Test.ACTUAL).usingRecursiveComparison().ignoringOverriddenEqualsForTypes(type1, type2).getRecursiveComparisonConfiguration();
        // THEN
        Assertions.assertThat(configuration.getIgnoredOverriddenEqualsForTypes()).containsExactly(type1, type2);
    }

    @Test
    public void should_allow_to_register_field_comparators() {
        // GIVEN
        String field1 = "foo";
        String field2 = "foo.bar";
        String field3 = "bar";
        AlwaysEqualComparator<?> alwaysEqualComparator = AlwaysEqualComparator.alwaysEqual();
        AlwaysDifferentComparator<?> alwaysDifferentComparator = AlwaysDifferentComparator.alwaysDifferent();
        // WHEN
        // @format:off
        RecursiveComparisonConfiguration configuration = Assertions.assertThat(RecursiveComparisonAssert_fluent_API_Test.ACTUAL).usingRecursiveComparison().withComparatorForFields(alwaysEqualComparator, field1, field3).withComparatorForFields(alwaysDifferentComparator, field2).getRecursiveComparisonConfiguration();
        // @format:on
        // THEN
        Assertions.assertThat(configuration.comparatorByFields()).containsExactly(Assertions.entry(FieldLocation.fielLocation(field3), alwaysEqualComparator), Assertions.entry(FieldLocation.fielLocation(field1), alwaysEqualComparator), Assertions.entry(FieldLocation.fielLocation(field2), alwaysDifferentComparator));
    }

    @Test
    public void should_allow_to_register_type_comparators() {
        // GIVEN
        Class<String> type1 = String.class;
        Class<Timestamp> type2 = Timestamp.class;
        Class<Tuple> type3 = Tuple.class;
        // WHEN
        // @format:off
        RecursiveComparisonConfiguration configuration = Assertions.assertThat(RecursiveComparisonAssert_fluent_API_Test.ACTUAL).usingRecursiveComparison().withComparatorForType(AlwaysEqualComparator.ALWAY_EQUALS_STRING, type1).withComparatorForType(AlwaysEqualComparator.ALWAY_EQUALS_TIMESTAMP, type2).withComparatorForType(AlwaysEqualComparator.ALWAY_EQUALS_TUPLE, type3).getRecursiveComparisonConfiguration();
        // @format:on
        // THEN
        Assertions.assertThat(configuration.comparatorByTypes()).contains(Assertions.entry(type1, AlwaysEqualComparator.ALWAY_EQUALS_STRING), Assertions.entry(type2, AlwaysEqualComparator.ALWAY_EQUALS_TIMESTAMP), Assertions.entry(type3, AlwaysEqualComparator.ALWAY_EQUALS_TUPLE));
    }

    @Test
    public void should_allow_to_override_field_comparator() {
        // GIVEN
        String field = "foo.bar";
        AlwaysEqualComparator<?> alwaysEqualComparator = AlwaysEqualComparator.alwaysEqual();
        AlwaysDifferentComparator<?> alwaysDifferentComparator = AlwaysDifferentComparator.alwaysDifferent();
        // WHEN
        RecursiveComparisonConfiguration configuration = Assertions.assertThat(RecursiveComparisonAssert_fluent_API_Test.ACTUAL).usingRecursiveComparison().withComparatorForFields(alwaysEqualComparator, field).withComparatorForFields(alwaysDifferentComparator, field).getRecursiveComparisonConfiguration();
        // THEN
        Assertions.assertThat(configuration.getComparatorForField(field)).isSameAs(alwaysDifferentComparator);
    }

    @Test
    public void should_allow_to_override_type_comparator() {
        // GIVEN
        Class<?> type = String.class;
        AlwaysEqualComparator<Object> alwaysEqualComparator = AlwaysEqualComparator.alwaysEqual();
        AlwaysDifferentComparator<Object> alwaysDifferentComparator = AlwaysDifferentComparator.alwaysDifferent();
        // WHEN
        RecursiveComparisonConfiguration configuration = Assertions.assertThat(RecursiveComparisonAssert_fluent_API_Test.ACTUAL).usingRecursiveComparison().withComparatorForType(alwaysEqualComparator, type).withComparatorForType(alwaysDifferentComparator, type).getRecursiveComparisonConfiguration();
        // THEN
        Assertions.assertThat(configuration.getComparatorForType(type)).isSameAs(alwaysDifferentComparator);
    }
}

