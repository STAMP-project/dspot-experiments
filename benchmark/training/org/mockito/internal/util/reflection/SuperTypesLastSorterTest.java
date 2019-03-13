/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.util.reflection;


import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.junit.Test;


@SuppressWarnings("unused")
public class SuperTypesLastSorterTest {
    /**
     * A Comparator that behaves like the old one, so the existing tests
     * continue to work.
     */
    private static Comparator<Field> cmp = new Comparator<Field>() {
        public int compare(Field o1, Field o2) {
            if (o1.equals(o2)) {
                return 0;
            }
            List<Field> l = SuperTypesLastSorter.sortSuperTypesLast(Arrays.asList(o1, o2));
            if ((l.get(0)) == o1) {
                return -1;
            } else {
                return 1;
            }
        }
    };

    private Object objectA;

    private Object objectB;

    private Number numberA;

    private Number numberB;

    private Integer integerA;

    private Integer integerB;

    private Iterable<?> iterableA;

    private Number xNumber;

    private Iterable<?> yIterable;

    private Integer zInteger;

    @Test
    public void when_same_type_the_order_is_based_on_field_name() throws Exception {
        assertThat(SuperTypesLastSorterTest.cmp.compare(field("objectA"), field("objectB"))).isEqualTo((-1));
        assertThat(SuperTypesLastSorterTest.cmp.compare(field("objectB"), field("objectA"))).isEqualTo(1);
        assertThat(SuperTypesLastSorterTest.cmp.compare(field("objectB"), field("objectB"))).isEqualTo(0);
    }

    @Test
    public void when_type_is_different_the_supertype_comes_last() throws Exception {
        assertThat(SuperTypesLastSorterTest.cmp.compare(field("numberA"), field("objectB"))).isEqualTo((-1));
        assertThat(SuperTypesLastSorterTest.cmp.compare(field("objectB"), field("numberA"))).isEqualTo(1);
    }

    @Test
    public void using_Collections_dot_sort() throws Exception {
        List<Field> unsortedFields = Arrays.asList(field("objectB"), field("integerB"), field("numberA"), field("numberB"), field("objectA"), field("integerA"));
        List<Field> sortedFields = SuperTypesLastSorter.sortSuperTypesLast(unsortedFields);
        assertThat(sortedFields).containsSequence(field("integerA"), field("integerB"), field("numberA"), field("numberB"), field("objectA"), field("objectB"));
    }

    @Test
    public void issue_352_order_was_different_between_JDK6_and_JDK7() throws Exception {
        List<Field> unsortedFields = Arrays.asList(field("objectB"), field("objectA"));
        Collections.sort(unsortedFields, SuperTypesLastSorterTest.cmp);
        assertThat(unsortedFields).containsSequence(field("objectA"), field("objectB"));
    }

    @Test
    public void fields_sort_consistently_when_interfaces_are_included() throws NoSuchFieldException {
        SuperTypesLastSorterTest.assertSortConsistently(field("iterableA"), field("numberA"), field("integerA"));
    }

    @Test
    public void fields_sort_consistently_when_names_and_type_indicate_different_order() throws NoSuchFieldException {
        SuperTypesLastSorterTest.assertSortConsistently(field("xNumber"), field("yIterable"), field("zInteger"));
    }
}

