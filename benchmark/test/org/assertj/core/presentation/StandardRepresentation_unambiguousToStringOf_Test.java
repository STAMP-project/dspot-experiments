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
import java.util.function.ToIntFunction;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.MapEntry;
import org.assertj.core.groups.Tuple;
import org.assertj.core.util.Arrays;
import org.assertj.core.util.Lists;
import org.assertj.core.util.OtherStringTestComparator;
import org.assertj.core.util.OtherStringTestComparatorWithAt;
import org.assertj.core.util.StringTestComparator;
import org.junit.jupiter.api.Test;


/**
 * Tests for {@link StandardRepresentation#unambiguousToStringOf(Object)}.
 *
 * @author Alexandre Dutra
 */
public class StandardRepresentation_unambiguousToStringOf_Test extends AbstractBaseRepresentationTest {
    private static final StandardRepresentation STANDARD_REPRESENTATION = new StandardRepresentation();

    @Test
    public void should_return_null_if_object_is_null() {
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(null)).isNull();
    }

    @Test
    public void should_quote_String() {
        String obj = "Hello";
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(obj)).isEqualTo(String.format("\"Hello\" (String@%s)", Integer.toHexString(System.identityHashCode(obj))));
    }

    @Test
    public void should_quote_empty_String() {
        String obj = "";
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(obj)).isEqualTo(String.format("\"\" (String@%s)", Integer.toHexString(System.identityHashCode(obj))));
    }

    @Test
    public void should_return_toString_of_File() {
        File obj = new StandardRepresentation_unambiguousToStringOf_Test.MyTestFile("/someFile.txt");
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(obj)).isEqualTo(String.format("/someFile.txt (MyTestFile@%s)", Integer.toHexString(System.identityHashCode(obj))));
    }

    @Test
    public void should_return_toString_of_anonymous_class() {
        Object obj = new Object() {
            @Override
            public String toString() {
                return "my object";
            }
        };
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(obj)).isEqualTo(String.format("my object (%s@%s)", obj.getClass().getName(), Integer.toHexString(System.identityHashCode(obj))));
    }

    @Test
    public void should_return_toString_of_Class_with_its_name() {
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(Object.class)).isEqualTo(String.format("java.lang.Object (Class@%s)", Integer.toHexString(System.identityHashCode(Object.class))));
    }

    @Test
    public void should_return_toString_of_Collection_of_String() {
        Collection<String> collection = Lists.newArrayList("s1", "s2");
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(collection)).isEqualTo(String.format("[\"s1\", \"s2\"] (ArrayList@%s)", Integer.toHexString(System.identityHashCode(collection))));
    }

    @Test
    public void should_return_toString_of_Collection_of_arrays() {
        List<Boolean[]> collection = Lists.newArrayList(Arrays.array(true, false), Arrays.array(true, false, true));
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(collection)).isEqualTo(String.format("[[true, false], [true, false, true]] (ArrayList@%s)", Integer.toHexString(System.identityHashCode(collection))));
    }

    @Test
    public void should_return_toString_of_Collection_of_arrays_up_to_the_maximum_allowed_elements() {
        List<Boolean[]> collection = Lists.newArrayList(Arrays.array(true, false), Arrays.array(true, false, true), Arrays.array(true, true));
        StandardRepresentation.setMaxElementsForPrinting(2);
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(collection)).isEqualTo(String.format("[[true, false], [true, false, ...], ...] (ArrayList@%s)", Integer.toHexString(System.identityHashCode(collection))));
    }

    @Test
    public void should_return_toString_of_Collection_of_Collections() {
        Collection<List<String>> collection = Lists.newArrayList(Lists.newArrayList("s1", "s2"), Lists.newArrayList("s3", "s4", "s5"));
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(collection)).isEqualTo(String.format("[[\"s1\", \"s2\"], [\"s3\", \"s4\", \"s5\"]] (ArrayList@%s)", Integer.toHexString(System.identityHashCode(collection))));
    }

    @Test
    public void should_return_toString_of_Collection_of_Collections_up_to_the_maximum_allowed_elements() {
        Collection<List<String>> collection = Lists.newArrayList(Lists.newArrayList("s1", "s2"), Lists.newArrayList("s3", "s4", "s5"), Lists.newArrayList("s6", "s7"));
        StandardRepresentation.setMaxElementsForPrinting(2);
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(collection)).isEqualTo(String.format("[[\"s1\", \"s2\"], [\"s3\", \"s4\", ...], ...] (ArrayList@%s)", Integer.toHexString(System.identityHashCode(collection))));
    }

    @Test
    public void should_return_toString_of_Map() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(map)).isEqualTo(String.format("{\"key1\"=\"value1\", \"key2\"=\"value2\"} (LinkedHashMap@%s)", Integer.toHexString(System.identityHashCode(map))));
    }

    @Test
    public void should_return_toString_of_array() {
        String[] array = Arrays.array("s1", "s2");
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(array)).isEqualTo(String.format("[\"s1\", \"s2\"] (String[]@%s)", Integer.toHexString(System.identityHashCode(array))));
    }

    @Test
    public void should_return_toString_of_array_of_arrays() {
        String[][] array = Arrays.array(Arrays.array("s1", "s2"), Arrays.array("s3", "s4", "s5"));
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(array)).isEqualTo(String.format("[[\"s1\", \"s2\"], [\"s3\", \"s4\", \"s5\"]] (String[][]@%s)", Integer.toHexString(System.identityHashCode(array))));
    }

    @Test
    public void should_return_toString_of_array_of_arrays_up_to_the_maximum_allowed_elements() {
        String[][] array = Arrays.array(Arrays.array("s1", "s2"), Arrays.array("s3", "s4", "s5"), Arrays.array("s6", "s7"));
        StandardRepresentation.setMaxElementsForPrinting(2);
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(array)).isEqualTo(String.format("[[\"s1\", \"s2\"], [\"s3\", \"s4\", ...], ...] (String[][]@%s)", Integer.toHexString(System.identityHashCode(array))));
    }

    @Test
    public void should_return_toString_of_array_of_Class() {
        Class<?>[] array = new Class<?>[]{ String.class, File.class };
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(array)).isEqualTo(String.format("[java.lang.String, java.io.File] (Class[]@%s)", Integer.toHexString(System.identityHashCode(array))));
    }

    @Test
    public void should_return_toString_of_calendar() {
        GregorianCalendar calendar = new GregorianCalendar(2011, Calendar.JANUARY, 18, 23, 53, 17);
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(calendar)).isEqualTo(String.format("2011-01-18T23:53:17 (GregorianCalendar@%s)", Integer.toHexString(System.identityHashCode(calendar))));
    }

    @Test
    public void should_return_toString_of_date() {
        Date date = new GregorianCalendar(2011, Calendar.JUNE, 18, 23, 53, 17).getTime();
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(date)).isEqualTo(String.format("2011-06-18T23:53:17.000 (Date@%s)", Integer.toHexString(System.identityHashCode(date))));
    }

    @Test
    public void should_return_toString_of_AtomicReference() {
        AtomicReference<String> atomicReference = new AtomicReference<>("actual");
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(atomicReference)).isEqualTo(String.format("AtomicReference[\"actual\"] (AtomicReference@%s)", Integer.toHexString(System.identityHashCode(atomicReference))));
    }

    @Test
    public void should_return_toString_of_AtomicMarkableReference() {
        AtomicMarkableReference<String> atomicMarkableReference = new AtomicMarkableReference<>("actual", true);
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(atomicMarkableReference)).isEqualTo(String.format("AtomicMarkableReference[marked=true, reference=\"actual\"] (AtomicMarkableReference@%s)", Integer.toHexString(System.identityHashCode(atomicMarkableReference))));
    }

    @Test
    public void should_return_toString_of_AtomicStampedReference() {
        AtomicStampedReference<String> atomicStampedReference = new AtomicStampedReference<>("actual", 123);
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(atomicStampedReference)).isEqualTo(String.format("AtomicStampedReference[stamp=123, reference=\"actual\"] (AtomicStampedReference@%s)", Integer.toHexString(System.identityHashCode(atomicStampedReference))));
    }

    @Test
    public void should_return_toString_of_AtomicIntegerFieldUpdater() {
        AtomicIntegerFieldUpdater<StandardRepresentation_unambiguousToStringOf_Test.Person> updater = AtomicIntegerFieldUpdater.newUpdater(StandardRepresentation_unambiguousToStringOf_Test.Person.class, "age");
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(updater)).isEqualTo(String.format("AtomicIntegerFieldUpdater (%s@%s)", updater.getClass().getSimpleName(), Integer.toHexString(System.identityHashCode(updater))));
    }

    @Test
    public void should_return_toString_of_AtomicLongFieldUpdater() {
        AtomicLongFieldUpdater<StandardRepresentation_unambiguousToStringOf_Test.Person> updater = AtomicLongFieldUpdater.newUpdater(StandardRepresentation_unambiguousToStringOf_Test.Person.class, "account");
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(updater)).isEqualTo(String.format("AtomicLongFieldUpdater (%s@%s)", updater.getClass().getSimpleName(), Integer.toHexString(System.identityHashCode(updater))));
    }

    @Test
    public void should_return_toString_of_AtomicReferenceFieldUpdater() {
        AtomicReferenceFieldUpdater<StandardRepresentation_unambiguousToStringOf_Test.Person, String> updater = AtomicReferenceFieldUpdater.newUpdater(StandardRepresentation_unambiguousToStringOf_Test.Person.class, String.class, "name");
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(updater)).isEqualTo(String.format("AtomicReferenceFieldUpdater (%s@%s)", updater.getClass().getSimpleName(), Integer.toHexString(System.identityHashCode(updater))));
    }

    @Test
    public void toString_with_anonymous_comparator() {
        Comparator<String> anonymousComparator = new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return (s1.length()) - (s2.length());
            }
        };
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(anonymousComparator)).isEqualTo(String.format("'anonymous comparator class' (%s@%s)", anonymousComparator.getClass().getName(), Integer.toHexString(System.identityHashCode(anonymousComparator))));
    }

    @Test
    public void toString_with_lambda_comparator() {
        Comparator<String> lambda = ( s1, s2) -> (s1.length()) - (s2.length());
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(lambda)).isEqualTo(String.format("%s (%s@%s)", lambda.getClass().getSimpleName(), lambda.getClass().getSimpleName(), Integer.toHexString(System.identityHashCode(lambda))));
    }

    @Test
    public void toString_with_builtin_comparator() {
        Comparator<String> comparator = Comparator.comparingInt(String::length);
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(comparator)).isEqualTo(String.format("%s (%s@%s)", comparator.getClass().getSimpleName(), comparator.getClass().getSimpleName(), Integer.toHexString(System.identityHashCode(comparator))));
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
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(anonymousComparator)).isEqualTo(String.format("foo (%s@%s)", anonymousComparator.getClass().getName(), Integer.toHexString(System.identityHashCode(anonymousComparator))));
    }

    @Test
    public void toString_with_comparator_not_overriding_toString() {
        StringTestComparator obj = new StringTestComparator();
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(obj)).isEqualTo(String.format("StringTestComparator (StringTestComparator@%s)", Integer.toHexString(System.identityHashCode(obj))));
    }

    @Test
    public void toString_with_comparator_overriding_toString() {
        OtherStringTestComparator obj = new OtherStringTestComparator();
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(obj)).isEqualTo(String.format("other String comparator (OtherStringTestComparator@%s)", Integer.toHexString(System.identityHashCode(obj))));
    }

    @Test
    public void toString_with_comparator_overriding_toString_and_having_at() {
        OtherStringTestComparatorWithAt obj = new OtherStringTestComparatorWithAt();
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(obj)).isEqualTo(String.format("other String comparator with @ (OtherStringTestComparatorWithAt@%s)", Integer.toHexString(System.identityHashCode(obj))));
    }

    @Test
    public void should_format_longs_and_integers() {
        Long l = 20L;
        Integer i = 20;
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(l)).isNotEqualTo(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(i));
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(i)).isEqualTo(String.format("20 (Integer@%s)", Integer.toHexString(System.identityHashCode(i))));
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(l)).isEqualTo(String.format("20L (Long@%s)", Integer.toHexString(System.identityHashCode(l))));
    }

    @Test
    public void should_format_bytes_chars_and_shorts() {
        Byte b = ((byte) (20));
        Character c = ((char) (20));
        Short s = ((short) (20));
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(b)).isNotEqualTo(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(c));
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(b)).isNotEqualTo(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(s));
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(c)).isNotEqualTo(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(s));
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(b)).isEqualTo(String.format("20 (Byte@%s)", Integer.toHexString(System.identityHashCode(b))));
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(c)).isEqualTo(String.format("\'\u0014\' (Character@%s)", Integer.toHexString(System.identityHashCode(c))));
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(s)).isEqualTo(String.format("20 (Short@%s)", Integer.toHexString(System.identityHashCode(s))));
    }

    @Test
    public void should_format_doubles_and_floats() {
        Float f = 20.0F;
        Double d = 20.0;
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(f)).isNotEqualTo(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(d));
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(d)).isEqualTo(String.format("20.0 (Double@%s)", Integer.toHexString(System.identityHashCode(d))));
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(f)).isEqualTo(String.format("20.0f (Float@%s)", Integer.toHexString(System.identityHashCode(f))));
    }

    @Test
    public void should_format_tuples() {
        Tuple tuple = Assertions.tuple(1, 2, 3);
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(tuple)).isEqualTo(String.format("(1, 2, 3) (Tuple@%s)", Integer.toHexString(System.identityHashCode(tuple))));
    }

    @Test
    public void should_format_tuples_up_to_the_maximum_allowed_elements() {
        StandardRepresentation.setMaxElementsForPrinting(2);
        Tuple tuple = Assertions.tuple(1, 2, 3);
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(tuple)).isEqualTo(String.format("(1, 2, ...) (Tuple@%s)", Integer.toHexString(System.identityHashCode(tuple))));
    }

    @Test
    public void should_format_simple_date_format() {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(sdf)).isEqualTo(String.format("ddMMyyyy (SimpleDateFormat@%s)", Integer.toHexString(System.identityHashCode(sdf))));
    }

    @Test
    public void should_format_assertj_map_entry() {
        MapEntry<String, Integer> entry = Assertions.entry("A", 1);
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(entry)).isEqualTo(String.format("MapEntry[key=\"A\", value=1] (MapEntry@%s)", Integer.toHexString(System.identityHashCode(entry))));
    }

    @Test
    public void should_return_unambiguousToStringOf_method() throws NoSuchMethodException {
        Method method = StandardRepresentation_unambiguousToStringOf_Test.GenericClass.class.getDeclaredMethod("someGenericMethod", StandardRepresentation_unambiguousToStringOf_Test.Person.class, List.class, Object.class);
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(method)).isEqualTo(String.format("%s (Method@%s)", method.toGenericString(), Integer.toHexString(System.identityHashCode(method))));
    }

    @Test
    public void should_disambiguate_non_equal_objects_with_same_hash_code_and_toString_representations() {
        Assertions.assertThat(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(new StandardRepresentation_unambiguousToStringOf_Test.Ambiguous(0, 1))).isNotEqualTo(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(new StandardRepresentation_unambiguousToStringOf_Test.Ambiguous(0, 2)));
    }

    @Test
    public void isEqualTo_should_show_disambiguated_objects_with_same_hash_code_and_toString_representations() {
        // GIVEN
        StandardRepresentation_unambiguousToStringOf_Test.Ambiguous ambiguous1 = new StandardRepresentation_unambiguousToStringOf_Test.Ambiguous(0, 1);
        StandardRepresentation_unambiguousToStringOf_Test.Ambiguous ambiguous2 = new StandardRepresentation_unambiguousToStringOf_Test.Ambiguous(0, 2);
        // WHEN
        AssertionError error = Assertions.catchThrowableOfType(() -> assertThat(ambiguous1).isEqualTo(ambiguous2), AssertionError.class);
        // THEN
        Assertions.assertThat(error).hasMessageContaining(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(ambiguous1)).hasMessageContaining(StandardRepresentation_unambiguousToStringOf_Test.unambiguousToStringOf(ambiguous2));
    }

    private static class MyTestFile extends File {
        private static final long serialVersionUID = 1L;

        private final String path;

        MyTestFile(String path) {
            super(path);
            this.path = path;
        }

        @Override
        public String getAbsolutePath() {
            return path;
        }
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
        public <R extends StandardRepresentation_unambiguousToStringOf_Test.Person> T someGenericMethod(R input, List<? extends R> list, T input2) {
            return input2;
        }
    }

    private static class Ambiguous {
        int x;

        int y;

        Ambiguous(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            StandardRepresentation_unambiguousToStringOf_Test.Ambiguous that = ((StandardRepresentation_unambiguousToStringOf_Test.Ambiguous) (o));
            return ((this.x) == (that.x)) && ((this.y) == (that.y));
        }

        @Override
        public int hashCode() {
            return x;
        }

        @Override
        public String toString() {
            return String.format("Ambiguous(%d)", x);
        }
    }
}

