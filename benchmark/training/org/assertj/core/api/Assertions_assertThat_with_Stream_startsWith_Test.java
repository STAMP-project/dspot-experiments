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
package org.assertj.core.api;


import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.test.ObjectArrays;
import org.assertj.core.util.Arrays;
import org.assertj.core.util.CaseInsensitiveStringComparator;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;


public class Assertions_assertThat_with_Stream_startsWith_Test {
    Stream<String> infiniteStream = Stream.generate(() -> "");

    @Test
    public void should_reuse_stream_after_assertion() {
        Stream<String> names = Stream.of("Luke", "Leia");
        Assertions.assertThat(names).startsWith(Arrays.array("Luke", "Leia")).endsWith("Leia");
    }

    @Test
    public void should_throw_error_if_sequence_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> assertThat(infiniteStream).startsWith(((String[]) (null)))).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_pass_if_actual_and_sequence_are_empty() {
        Stream<Object> empty = Stream.of();
        Assertions.assertThat(empty).startsWith(ObjectArrays.emptyArray());
    }

    @Test
    public void should_fail_if_sequence_to_look_for_is_empty_and_actual_is_not() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> {
            Stream<String> names = Stream.of("Luke", "Leia");
            assertThat(names).startsWith(new String[0]);
        });
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> {
            Stream<Object> names = null;
            assertThat(names).startsWith(emptyArray());
        }).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_sequence_is_bigger_than_actual() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> {
            String[] sequence = { "Luke", "Leia", "Obi-Wan", "Han", "C-3PO", "R2-D2", "Anakin" };
            Stream<String> names = Stream.of("Luke", "Leia");
            assertThat(names).startsWith(sequence);
        });
    }

    @Test
    public void should_fail_if_actual_does_not_start_with_sequence() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> {
            String[] sequence = { "Han", "C-3PO" };
            Stream<String> names = Stream.of("Luke", "Leia");
            assertThat(names).startsWith(sequence);
        });
    }

    @Test
    public void should_fail_if_actual_starts_with_first_elements_of_sequence_only() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> {
            String[] sequence = { "Luke", "Yoda" };
            Stream<String> names = Stream.of("Luke", "Leia");
            assertThat(names).startsWith(sequence);
        });
    }

    @Test
    public void should_pass_if_actual_starts_with_sequence() {
        Stream<String> names = Stream.of("Luke", "Leia", "Yoda");
        Assertions.assertThat(names).startsWith(Arrays.array("Luke", "Leia"));
    }

    @Test
    public void should_pass_if_actual_and_sequence_are_equal() {
        Stream<String> names = Stream.of("Luke", "Leia");
        Assertions.assertThat(names).startsWith(Arrays.array("Luke", "Leia"));
    }

    // ------------------------------------------------------------------------------------------------------------------
    // tests using a custom comparison strategy
    // ------------------------------------------------------------------------------------------------------------------
    @Test
    public void should_fail_if_actual_does_not_start_with_sequence_according_to_custom_comparison_strategy() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> {
            Stream<String> names = Stream.of("Luke", "Leia");
            String[] sequence = { "Han", "C-3PO" };
            assertThat(names).usingElementComparator(CaseInsensitiveStringComparator.instance).startsWith(sequence);
        });
    }

    @Test
    public void should_fail_if_actual_starts_with_first_elements_of_sequence_only_according_to_custom_comparison_strategy() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> {
            Stream<String> names = Stream.of("Luke", "Leia");
            String[] sequence = { "Luke", "Obi-Wan", "Han" };
            assertThat(names).usingElementComparator(CaseInsensitiveStringComparator.instance).startsWith(sequence);
        });
    }

    @Test
    public void should_pass_if_actual_starts_with_sequence_according_to_custom_comparison_strategy() {
        Stream<String> names = Stream.of("Luke", "Leia");
        String[] sequence = new String[]{ "LUKE" };
        Assertions.assertThat(names).usingElementComparator(CaseInsensitiveStringComparator.instance).startsWith(sequence);
    }

    @Test
    public void should_pass_if_actual_and_sequence_are_equal_according_to_custom_comparison_strategy() {
        Stream<String> names = Stream.of("Luke", "Leia");
        String[] sequence = new String[]{ "LUKE", "lEIA" };
        Assertions.assertThat(names).usingElementComparator(CaseInsensitiveStringComparator.instance).startsWith(sequence);
    }

    @Test
    public void test_issue_245() {
        Assertions_assertThat_with_Stream_startsWith_Test.Foo foo1 = new Assertions_assertThat_with_Stream_startsWith_Test.Foo("id", 1);
        foo1._f2 = "foo1";
        Assertions_assertThat_with_Stream_startsWith_Test.Foo foo2 = new Assertions_assertThat_with_Stream_startsWith_Test.Foo("id", 2);
        foo2._f2 = "foo1";
        List<Assertions_assertThat_with_Stream_startsWith_Test.Foo> stream2 = Lists.newArrayList(foo2);
        Assertions.assertThat(Stream.of(foo1)).usingElementComparatorOnFields("_f2").isEqualTo(stream2);
        Assertions.assertThat(Stream.of(foo1)).usingElementComparatorOnFields("id").isEqualTo(stream2);
        Assertions.assertThat(Stream.of(foo1)).usingElementComparatorIgnoringFields("bar").isEqualTo(stream2);
    }

    @Test
    public void test_issue_236() {
        List<Assertions_assertThat_with_Stream_startsWith_Test.Foo> stream2 = Lists.newArrayList(new Assertions_assertThat_with_Stream_startsWith_Test.Foo("id", 2));
        Assertions.assertThat(Stream.of(new Assertions_assertThat_with_Stream_startsWith_Test.Foo("id", 1))).usingElementComparatorOnFields("id").isEqualTo(stream2);
        Assertions.assertThat(Stream.of(new Assertions_assertThat_with_Stream_startsWith_Test.Foo("id", 1))).usingElementComparatorIgnoringFields("bar").isEqualTo(stream2);
    }

    public static class Foo {
        private String id;

        private int bar;

        public String _f2;

        public String getId() {
            return id;
        }

        public int getBar() {
            return bar;
        }

        public Foo(String id, int bar) {
            super();
            this.id = id;
            this.bar = bar;
        }

        @Override
        public String toString() {
            return ((("Foo [id=" + (id)) + ", bar=") + (bar)) + "]";
        }
    }
}

