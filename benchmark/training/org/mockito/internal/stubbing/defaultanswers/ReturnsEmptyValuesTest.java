/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.stubbing.defaultanswers;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.Invocation;
import org.mockitoutil.TestBase;


public class ReturnsEmptyValuesTest extends TestBase {
    private final ReturnsEmptyValues values = new ReturnsEmptyValues();

    @Test
    public void should_return_empty_collections_or_null_for_non_collections() {
        Assert.assertTrue(((Collection<?>) (values.returnValueFor(Collection.class))).isEmpty());
        Assert.assertTrue(((Set<?>) (values.returnValueFor(Set.class))).isEmpty());
        Assert.assertTrue(((SortedSet<?>) (values.returnValueFor(SortedSet.class))).isEmpty());
        Assert.assertTrue(((HashSet<?>) (values.returnValueFor(HashSet.class))).isEmpty());
        Assert.assertTrue(((TreeSet<?>) (values.returnValueFor(TreeSet.class))).isEmpty());
        Assert.assertTrue(((LinkedHashSet<?>) (values.returnValueFor(LinkedHashSet.class))).isEmpty());
        Assert.assertTrue(((List<?>) (values.returnValueFor(List.class))).isEmpty());
        Assert.assertTrue(((ArrayList<?>) (values.returnValueFor(ArrayList.class))).isEmpty());
        Assert.assertTrue(((LinkedList<?>) (values.returnValueFor(LinkedList.class))).isEmpty());
        Assert.assertTrue(((Map<?, ?>) (values.returnValueFor(Map.class))).isEmpty());
        Assert.assertTrue(((SortedMap<?, ?>) (values.returnValueFor(SortedMap.class))).isEmpty());
        Assert.assertTrue(((HashMap<?, ?>) (values.returnValueFor(HashMap.class))).isEmpty());
        Assert.assertTrue(((TreeMap<?, ?>) (values.returnValueFor(TreeMap.class))).isEmpty());
        Assert.assertTrue(((LinkedHashMap<?, ?>) (values.returnValueFor(LinkedHashMap.class))).isEmpty());
        Assert.assertNull(values.returnValueFor(String.class));
    }

    @Test
    public void should_return_empty_iterable() throws Exception {
        Assert.assertFalse(((Iterable<?>) (values.returnValueFor(Iterable.class))).iterator().hasNext());
    }

    @Test
    public void should_return_primitive() {
        Assert.assertEquals(false, values.returnValueFor(Boolean.TYPE));
        Assert.assertEquals(((char) (0)), values.returnValueFor(Character.TYPE));
        Assert.assertEquals(((byte) (0)), values.returnValueFor(Byte.TYPE));
        Assert.assertEquals(((short) (0)), values.returnValueFor(Short.TYPE));
        Assert.assertEquals(0, values.returnValueFor(Integer.TYPE));
        Assert.assertEquals(0L, values.returnValueFor(Long.TYPE));
        Assert.assertEquals(0.0F, values.returnValueFor(Float.TYPE));
        Assert.assertEquals(0.0, values.returnValueFor(Double.TYPE));
    }

    @Test
    public void should_return_non_zero_for_compareTo_method() {
        // 
        // given
        Date d = Mockito.mock(Date.class);
        d.compareTo(new Date());
        Invocation compareTo = this.getLastInvocation();
        // when
        Object result = values.answer(compareTo);
        // then
        Assert.assertTrue((result != ((Object) (0))));
    }

    @SuppressWarnings("SelfComparison")
    @Test
    public void should_return_zero_if_mock_is_compared_to_itself() {
        // given
        Date d = Mockito.mock(Date.class);
        d.compareTo(d);
        Invocation compareTo = this.getLastInvocation();
        // when
        Object result = values.answer(compareTo);
        // then
        Assert.assertEquals(0, result);
    }

    @Test
    public void should_return_empty_Optional() throws Exception {
        verify_empty_Optional_is_returned("java.util.stream.Stream", "java.util.Optional");
    }

    @Test
    public void should_return_empty_OptionalDouble() throws Exception {
        verify_empty_Optional_is_returned("java.util.stream.DoubleStream", "java.util.OptionalDouble");
    }

    @Test
    public void should_return_empty_OptionalInt() throws Exception {
        verify_empty_Optional_is_returned("java.util.stream.IntStream", "java.util.OptionalInt");
    }

    @Test
    public void should_return_empty_OptionalLong() throws Exception {
        verify_empty_Optional_is_returned("java.util.stream.LongStream", "java.util.OptionalLong");
    }

    @Test
    public void should_return_empty_Stream() throws Exception {
        verify_empty_Stream_is_returned("java.util.stream.Stream");
    }

    @Test
    public void should_return_empty_DoubleStream() throws Exception {
        verify_empty_Stream_is_returned("java.util.stream.DoubleStream");
    }

    @Test
    public void should_return_empty_IntStream() throws Exception {
        verify_empty_Stream_is_returned("java.util.stream.IntStream");
    }

    @Test
    public void should_return_empty_LongStream() throws Exception {
        verify_empty_Stream_is_returned("java.util.stream.LongStream");
    }
}

