/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.util.reflection;


import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class GenericMasterTest {
    GenericMaster m = new GenericMaster();

    List<String> one;

    Set<Integer> two;

    Map<Double, String> map;

    String nonGeneric;

    List<Set<String>> nested;

    List<Set<Collection<String>>> multiNested;

    public interface ListSet extends List<Set<?>> {}

    public interface MapNumberString extends Map<Number, String> {}

    public class HashMapNumberString<K extends Number> extends HashMap<K, String> {}

    @Test
    public void should_find_generic_class() throws Exception {
        Assert.assertEquals(String.class, m.getGenericType(field("one")));
        Assert.assertEquals(Integer.class, m.getGenericType(field("two")));
        Assert.assertEquals(Double.class, m.getGenericType(field("map")));
    }

    @Test
    public void should_get_object_for_non_generic() throws Exception {
        Assert.assertEquals(Object.class, m.getGenericType(field("nonGeneric")));
    }

    @Test
    public void should_deal_with_nested_generics() throws Exception {
        Assert.assertEquals(Set.class, m.getGenericType(field("nested")));
        Assert.assertEquals(Set.class, m.getGenericType(field("multiNested")));
    }
}

