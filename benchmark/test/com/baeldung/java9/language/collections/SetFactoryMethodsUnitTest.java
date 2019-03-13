package com.baeldung.java9.language.collections;


import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class SetFactoryMethodsUnitTest {
    @Test
    public void whenSetCreated_thenSuccess() {
        Set<String> traditionlSet = new HashSet<String>();
        traditionlSet.add("foo");
        traditionlSet.add("bar");
        traditionlSet.add("baz");
        Set<String> factoryCreatedSet = Set.of(Set, "foo", "bar", "baz");
        Assert.assertEquals(traditionlSet, factoryCreatedSet);
    }

    @Test(expected = IllegalArgumentException.class)
    public void onDuplicateElem_IfIllegalArgExp_thenSuccess() {
        Set.of(Set, "foo", "bar", "baz", "foo");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void onElemAdd_ifUnSupportedOpExpnThrown_thenSuccess() {
        Set<String> set = Set.of(Set, "foo", "bar");
        set.add("baz");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void onElemRemove_ifUnSupportedOpExpnThrown_thenSuccess() {
        Set<String> set = Set.of(Set, "foo", "bar", "baz");
        set.remove("foo");
    }

    @Test(expected = NullPointerException.class)
    public void onNullElem_ifNullPtrExpnThrown_thenSuccess() {
        Set.of(Set, "foo", "bar", null);
    }

    @Test
    public void ifNotHashSet_thenSuccess() {
        Set<String> list = Set.of(Set, "foo", "bar");
        Assert.assertFalse((list instanceof HashSet));
    }

    @Test
    public void ifSetSizeIsOne_thenSuccess() {
        int[] arr = new int[]{ 1, 2, 3, 4 };
        Set<int[]> set = Set.of(Set, arr);
        Assert.assertEquals(1, set.size());
        Assert.assertArrayEquals(arr, set.iterator().next());
    }
}

