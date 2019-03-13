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
import java.util.stream.Stream;
import org.assertj.core.test.StringStream;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class Assertions_assertThat_with_Stream_Test {
    private StringStream stringStream = new StringStream();

    @Test
    public void should_create_Assert() {
        Object assertions = Assertions.assertThat(Stream.of("Luke", "Leia"));
        Assertions.assertThat(assertions).isNotNull();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void should_initialise_actual() {
        Stream<String> iterator = Stream.of("Luke", "Leia");
        List<? extends String> actual = Assertions.assertThat(iterator).actual;
        Assertions.assertThat(((List<String>) (actual))).contains("Luke", Assertions.atIndex(0)).contains("Leia", Assertions.atIndex(1));
    }

    @Test
    public void should_allow_null() {
        Assertions.assertThat(Assertions.assertThat(((Stream<String>) (null))).actual).isNull();
    }

    @Test
    public void isEqualTo_should_honor_comparing_the_same_mocked_stream() {
        Stream<?> stream = Mockito.mock(Stream.class);
        Assertions.assertThat(stream).isEqualTo(stream);
    }

    @Test
    public void stream_can_be_asserted_twice() {
        Stream<String> names = Stream.of("Luke", "Leia");
        Assertions.assertThat(names).containsExactly("Luke", "Leia").containsExactly("Luke", "Leia");
    }

    @Test
    public void should_not_consume_stream_when_asserting_non_null() {
        Stream<?> stream = Mockito.mock(Stream.class);
        Assertions.assertThat(stream).isNotNull();
        Mockito.verifyZeroInteractions(stream);
    }

    @Test
    public void isInstanceOf_should_check_the_original_stream_without_consuming_it() {
        Stream<?> stream = Mockito.mock(Stream.class);
        Assertions.assertThat(stream).isInstanceOf(Stream.class);
        Mockito.verifyZeroInteractions(stream);
    }

    @Test
    public void isInstanceOfAny_should_check_the_original_stream_without_consuming_it() {
        Stream<?> stream = Mockito.mock(Stream.class);
        Assertions.assertThat(stream).isInstanceOfAny(Stream.class, String.class);
        Mockito.verifyZeroInteractions(stream);
    }

    @Test
    public void isOfAnyClassIn_should_check_the_original_stream_without_consuming_it() {
        Assertions.assertThat(stringStream).isOfAnyClassIn(Double.class, StringStream.class);
    }

    @Test
    public void isExactlyInstanceOf_should_check_the_original_stream() {
        Assertions.assertThat(new StringStream()).isExactlyInstanceOf(StringStream.class);
    }

    @Test
    public void isNotExactlyInstanceOf_should_check_the_original_stream() {
        Assertions.assertThat(stringStream).isNotExactlyInstanceOf(Stream.class);
        try {
            Assertions.assertThat(stringStream).isNotExactlyInstanceOf(StringStream.class);
        } catch (AssertionError e) {
            // ok
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void isNotInstanceOf_should_check_the_original_stream() {
        Assertions.assertThat(stringStream).isNotInstanceOf(Long.class);
    }

    @Test
    public void isNotInstanceOfAny_should_check_the_original_stream() {
        Assertions.assertThat(stringStream).isNotInstanceOfAny(Long.class, String.class);
    }

    @Test
    public void isNotOfAnyClassIn_should_check_the_original_stream() {
        Assertions.assertThat(stringStream).isNotOfAnyClassIn(Long.class, String.class);
    }

    @Test
    public void isSameAs_should_check_the_original_stream_without_consuming_it() {
        Stream<?> stream = Mockito.mock(Stream.class);
        Assertions.assertThat(stream).isSameAs(stream);
        Mockito.verifyZeroInteractions(stream);
    }

    @Test
    public void isNotSameAs_should_check_the_original_stream_without_consuming_it() {
        Stream<?> stream = Mockito.mock(Stream.class);
        try {
            Assertions.assertThat(stream).isNotSameAs(stream);
        } catch (AssertionError e) {
            Mockito.verifyZeroInteractions(stream);
            return;
        }
        Assertions.fail("Expected assertionError, because assert notSame on same stream.");
    }

    @Test
    public void test_issue_245() {
        Assertions_assertThat_with_Stream_Test.Foo foo1 = new Assertions_assertThat_with_Stream_Test.Foo("id", 1);
        foo1._f2 = "foo1";
        Assertions_assertThat_with_Stream_Test.Foo foo2 = new Assertions_assertThat_with_Stream_Test.Foo("id", 2);
        foo2._f2 = "foo1";
        List<Assertions_assertThat_with_Stream_Test.Foo> stream2 = Lists.newArrayList(foo2);
        Assertions.assertThat(Stream.of(foo1)).usingElementComparatorOnFields("_f2").isEqualTo(stream2);
        Assertions.assertThat(Stream.of(foo1)).usingElementComparatorOnFields("id").isEqualTo(stream2);
        Assertions.assertThat(Stream.of(foo1)).usingElementComparatorIgnoringFields("bar").isEqualTo(stream2);
    }

    @Test
    public void test_issue_236() {
        List<Assertions_assertThat_with_Stream_Test.Foo> stream2 = Lists.newArrayList(new Assertions_assertThat_with_Stream_Test.Foo("id", 2));
        Assertions.assertThat(Stream.of(new Assertions_assertThat_with_Stream_Test.Foo("id", 1))).usingElementComparatorOnFields("id").isEqualTo(stream2);
        Assertions.assertThat(Stream.of(new Assertions_assertThat_with_Stream_Test.Foo("id", 1))).usingElementComparatorIgnoringFields("bar").isEqualTo(stream2);
    }

    @Test
    public void stream_with_upper_bound_assertions() {
        // GIVEN
        Stream<? extends Assertions_assertThat_with_Stream_Test.Foo> foos = Stream.of();
        // THEN
        Assertions.assertThat(foos).hasSize(0);
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

