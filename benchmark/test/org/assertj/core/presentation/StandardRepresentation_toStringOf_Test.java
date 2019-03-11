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
package org.assertj.core.presentation;


import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.atomic.AtomicStampedReference;
import java.util.function.Predicate;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.MapEntry;
import org.assertj.core.util.Arrays;
import org.assertj.core.util.Lists;
import org.assertj.core.util.OtherStringTestComparator;
import org.assertj.core.util.OtherStringTestComparatorWithAt;
import org.assertj.core.util.StringTestComparator;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.stream;


/**
 * Tests for {@link org.assertj.core.presentation.StandardRepresentation#toStringOf(Object)}.
 *
 * @author Joel Costigliola
 */
public class StandardRepresentation_toStringOf_Test extends AbstractBaseRepresentationTest {
    private static final StandardRepresentation STANDARD_REPRESENTATION = new StandardRepresentation();

    @Test
    public void should_return_null_if_object_is_null() {
        Assertions.assertThat(StandardRepresentation_toStringOf_Test.STANDARD_REPRESENTATION.toStringOf(((Object) (null)))).isNull();
    }

    @Test
    public void should_quote_String() {
        Assertions.assertThat(StandardRepresentation_toStringOf_Test.STANDARD_REPRESENTATION.toStringOf("Hello")).isEqualTo("\"Hello\"");
    }

    @Test
    public void should_quote_empty_String() {
        Assertions.assertThat(StandardRepresentation_toStringOf_Test.STANDARD_REPRESENTATION.toStringOf("")).isEqualTo("\"\"");
    }

    @Test
    public void should_return_toString_of_File() {
        final String path = "/someFile.txt";
        @SuppressWarnings("serial")
        File o = new File(path) {
            @Override
            public String getAbsolutePath() {
                return path;
            }
        };
        Assertions.assertThat(StandardRepresentation_toStringOf_Test.STANDARD_REPRESENTATION.toStringOf(o)).isEqualTo(path);
    }

    @Test
    public void should_return_toString_of_Class_with_its_name() {
        Assertions.assertThat(StandardRepresentation_toStringOf_Test.STANDARD_REPRESENTATION.toStringOf(Object.class)).isEqualTo("java.lang.Object");
    }

    @Test
    public void should_return_toString_of_Collection_of_String() {
        Collection<String> collection = Lists.newArrayList("s1", "s2");
        // assertThat(STANDARD_REPRESENTATION.toStringOf(collection)).isEqualTo(format("[\"s1\",%n" +
        // " \"s2\"]"));
        Assertions.assertThat(StandardRepresentation_toStringOf_Test.STANDARD_REPRESENTATION.toStringOf(collection)).isEqualTo(String.format("[\"s1\", \"s2\"]"));
    }

    @Test
    public void should_return_toString_of_Collection_of_arrays() {
        List<Boolean[]> collection = Lists.newArrayList(Arrays.array(true, false), Arrays.array(true, false, true));
        Assertions.assertThat(StandardRepresentation_toStringOf_Test.STANDARD_REPRESENTATION.toStringOf(collection)).isEqualTo("[[true, false], [true, false, true]]");
    }

    @Test
    public void should_return_toString_of_Collection_of_arrays_up_to_the_maximum_allowed_elements() {
        List<Boolean[]> collection = Lists.newArrayList(Arrays.array(true, false), Arrays.array(true, false, true), Arrays.array(true, true));
        StandardRepresentation.setMaxElementsForPrinting(2);
        Assertions.assertThat(StandardRepresentation_toStringOf_Test.STANDARD_REPRESENTATION.toStringOf(collection)).isEqualTo("[[true, false], [true, false, ...], ...]");
    }

    @Test
    public void should_return_toString_of_Collection_of_Collections() {
        Collection<List<String>> collection = Lists.newArrayList(Lists.newArrayList("s1", "s2"), Lists.newArrayList("s3", "s4", "s5"));
        Assertions.assertThat(StandardRepresentation_toStringOf_Test.STANDARD_REPRESENTATION.toStringOf(collection)).isEqualTo("[[\"s1\", \"s2\"], [\"s3\", \"s4\", \"s5\"]]");
    }

    @Test
    public void should_return_toString_of_Collection_of_Collections_up_to_the_maximum_allowed_elements() {
        Collection<List<String>> collection = Lists.newArrayList(Lists.newArrayList("s1", "s2"), Lists.newArrayList("s3", "s4", "s5"), Lists.newArrayList("s6", "s7"));
        StandardRepresentation.setMaxElementsForPrinting(2);
        Assertions.assertThat(StandardRepresentation_toStringOf_Test.STANDARD_REPRESENTATION.toStringOf(collection)).isEqualTo("[[\"s1\", \"s2\"], [\"s3\", \"s4\", ...], ...]");
    }

    @Test
    public void should_return_toString_of_Map() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        Assertions.assertThat(StandardRepresentation_toStringOf_Test.STANDARD_REPRESENTATION.toStringOf(map)).isEqualTo("{\"key1\"=\"value1\", \"key2\"=\"value2\"}");
    }

    @Test
    public void should_return_toString_of_array() {
        Assertions.assertThat(StandardRepresentation_toStringOf_Test.STANDARD_REPRESENTATION.toStringOf(Arrays.array("s1", "s2"))).isEqualTo("[\"s1\", \"s2\"]");
    }

    @Test
    public void should_return_toString_of_array_of_arrays() {
        String[][] array = Arrays.array(Arrays.array("s1", "s2"), Arrays.array("s3", "s4", "s5"));
        Assertions.assertThat(StandardRepresentation_toStringOf_Test.STANDARD_REPRESENTATION.toStringOf(array)).isEqualTo("[[\"s1\", \"s2\"], [\"s3\", \"s4\", \"s5\"]]");
    }

    @Test
    public void should_return_toString_of_array_of_arrays_up_to_the_maximum_allowed_elements() {
        String[][] array = Arrays.array(Arrays.array("s1", "s2"), Arrays.array("s3", "s4", "s5"), Arrays.array("s6", "s7"));
        StandardRepresentation.setMaxElementsForPrinting(2);
        Assertions.assertThat(StandardRepresentation_toStringOf_Test.STANDARD_REPRESENTATION.toStringOf(array)).isEqualTo("[[\"s1\", \"s2\"], [\"s3\", \"s4\", ...], ...]");
    }

    @Test
    public void should_return_toString_of_array_of_Class() {
        Class<?>[] array = new Class<?>[]{ String.class, File.class };
        Assertions.assertThat(StandardRepresentation_toStringOf_Test.STANDARD_REPRESENTATION.toStringOf(array)).isEqualTo("[java.lang.String, java.io.File]");
    }

    @Test
    public void should_return_toString_of_calendar() {
        GregorianCalendar calendar = new GregorianCalendar(2011, Calendar.JANUARY, 18, 23, 53, 17);
        Assertions.assertThat(StandardRepresentation_toStringOf_Test.STANDARD_REPRESENTATION.toStringOf(calendar)).isEqualTo("2011-01-18T23:53:17");
    }

    @Test
    public void should_return_toString_of_date() {
        Date date = new GregorianCalendar(2011, Calendar.JUNE, 18, 23, 53, 17).getTime();
        Assertions.assertThat(StandardRepresentation_toStringOf_Test.STANDARD_REPRESENTATION.toStringOf(date)).isEqualTo("2011-06-18T23:53:17.000");
    }

    @Test
    public void should_return_toString_of_AtomicReference() {
        AtomicReference<String> atomicReference = new AtomicReference<>("actual");
        Assertions.assertThat(StandardRepresentation_toStringOf_Test.STANDARD_REPRESENTATION.toStringOf(atomicReference)).isEqualTo("AtomicReference[\"actual\"]");
    }

    @Test
    public void should_return_toString_of_AtomicMarkableReference() {
        AtomicMarkableReference<String> atomicMarkableReference = new AtomicMarkableReference<>("actual", true);
        Assertions.assertThat(StandardRepresentation_toStringOf_Test.STANDARD_REPRESENTATION.toStringOf(atomicMarkableReference)).isEqualTo("AtomicMarkableReference[marked=true, reference=\"actual\"]");
    }

    @Test
    public void should_return_toString_of_AtomicStampedReference() {
        AtomicStampedReference<String> atomicStampedReference = new AtomicStampedReference<>("actual", 123);
        Assertions.assertThat(StandardRepresentation_toStringOf_Test.STANDARD_REPRESENTATION.toStringOf(atomicStampedReference)).isEqualTo("AtomicStampedReference[stamp=123, reference=\"actual\"]");
    }

    @Test
    public void should_return_toString_of_AtomicIntegerFieldUpdater() {
        AtomicIntegerFieldUpdater<StandardRepresentation_toStringOf_Test.Person> updater = AtomicIntegerFieldUpdater.newUpdater(StandardRepresentation_toStringOf_Test.Person.class, "age");
        Assertions.assertThat(StandardRepresentation_toStringOf_Test.STANDARD_REPRESENTATION.toStringOf(updater)).isEqualTo("AtomicIntegerFieldUpdater");
    }

    @Test
    public void should_return_toString_of_AtomicLongFieldUpdater() {
        AtomicLongFieldUpdater<StandardRepresentation_toStringOf_Test.Person> updater = AtomicLongFieldUpdater.newUpdater(StandardRepresentation_toStringOf_Test.Person.class, "account");
        Assertions.assertThat(StandardRepresentation_toStringOf_Test.STANDARD_REPRESENTATION.toStringOf(updater)).isEqualTo("AtomicLongFieldUpdater");
    }

    @Test
    public void should_return_toString_of_AtomicReferenceFieldUpdater() {
        AtomicReferenceFieldUpdater<StandardRepresentation_toStringOf_Test.Person, String> updater = AtomicReferenceFieldUpdater.newUpdater(StandardRepresentation_toStringOf_Test.Person.class, String.class, "name");
        Assertions.assertThat(StandardRepresentation_toStringOf_Test.STANDARD_REPRESENTATION.toStringOf(updater)).isEqualTo("AtomicReferenceFieldUpdater");
    }

    @Test
    public void toString_with_anonymous_comparator() {
        Comparator<String> anonymousComparator = new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return (s1.length()) - (s2.length());
            }
        };
        Assertions.assertThat(StandardRepresentation_toStringOf_Test.STANDARD_REPRESENTATION.toStringOf(anonymousComparator)).isEqualTo("'anonymous comparator class'");
    }

    @Test
    public void toString_with_anonymous_comparator_overriding_toString() {
        Comparator<String> anonymousComparator = new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return (s1.length()) - (s2.length());
            }

            @Override
            public String toString() {
                return "foo";
            }
        };
        Assertions.assertThat(StandardRepresentation_toStringOf_Test.STANDARD_REPRESENTATION.toStringOf(anonymousComparator)).isEqualTo("foo");
    }

    @Test
    public void toString_with_comparator_not_overriding_toString() {
        Assertions.assertThat(StandardRepresentation_toStringOf_Test.STANDARD_REPRESENTATION.toStringOf(new StringTestComparator())).isEqualTo("StringTestComparator");
    }

    @Test
    public void toString_with_comparator_overriding_toString() {
        Assertions.assertThat(StandardRepresentation_toStringOf_Test.STANDARD_REPRESENTATION.toStringOf(new OtherStringTestComparator())).isEqualTo("other String comparator");
    }

    @Test
    public void toString_with_comparator_overriding_toString_and_having_at() {
        Assertions.assertThat(StandardRepresentation_toStringOf_Test.STANDARD_REPRESENTATION.toStringOf(new OtherStringTestComparatorWithAt())).isEqualTo("other String comparator with @");
    }

    @Test
    public void should_format_longs_and_integers() {
        Assertions.assertThat(StandardRepresentation_toStringOf_Test.STANDARD_REPRESENTATION.toStringOf(20L).equals(toStringOf(20))).isFalse();
        Assertions.assertThat(toStringOf(20)).isEqualTo("20");
        Assertions.assertThat(toStringOf(20L)).isEqualTo("20L");
    }

    @Test
    public void should_format_bytes_as_hex() {
        Assertions.assertThat(toStringOf(((byte) (20))).equals(toStringOf(((char) (20))))).isFalse();
        Assertions.assertThat(toStringOf(((short) (20)))).isEqualTo(toStringOf(((byte) (20))));
        Assertions.assertThat(toStringOf(((byte) (32)))).isEqualTo("32");
    }

    @Test
    public void should_format_doubles_and_floats() {
        Assertions.assertThat(toStringOf(20.0F).equals(toStringOf(20.0))).isFalse();
        Assertions.assertThat(toStringOf(20.0)).isEqualTo("20.0");
        Assertions.assertThat(toStringOf(20.0F)).isEqualTo("20.0f");
    }

    @Test
    public void should_format_tuples() {
        Assertions.assertThat(toStringOf(Assertions.tuple(1, 2, 3))).isEqualTo("(1, 2, 3)");
    }

    @Test
    public void should_format_tuples_up_to_the_maximum_allowed_elements() {
        StandardRepresentation.setMaxElementsForPrinting(2);
        Assertions.assertThat(toStringOf(Assertions.tuple(1, 2, 3))).isEqualTo("(1, 2, ...)");
    }

    @Test
    public void should_format_simple_date_format() {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        Assertions.assertThat(toStringOf(sdf)).isEqualTo("ddMMyyyy");
    }

    @Test
    public void should_format_assertj_map_entry() {
        MapEntry<String, Integer> entry = Assertions.entry("A", 1);
        Assertions.assertThat(toStringOf(entry)).isEqualTo("MapEntry[key=\"A\", value=1]");
    }

    @Test
    public void should_return_toStringOf_method() {
        Method method = stream(StandardRepresentation_toStringOf_Test.GenericClass.class.getMethods()).filter(( m) -> m.getName().equals("someGenericMethod")).findAny().get();
        Assertions.assertThat(StandardRepresentation_toStringOf_Test.STANDARD_REPRESENTATION.toStringOf(method)).isEqualTo(method.toGenericString());
    }

    private static class Person {
        volatile String name;

        volatile int age;

        volatile long account;

        @Override
        public String toString() {
            return String.format("Person [name=%s, age=%s, account=%s]", name, age, account);
        }
    }

    private static class GenericClass<T> {
        @SuppressWarnings("unused")
        public <R extends StandardRepresentation_toStringOf_Test.Person> T someGenericMethod(R input, List<? extends R> list, T input2) {
            return input2;
        }
    }
}

