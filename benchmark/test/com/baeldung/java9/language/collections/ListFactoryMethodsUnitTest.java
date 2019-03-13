package com.baeldung.java9.language.collections;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ListFactoryMethodsUnitTest {
    @Test
    public void whenListCreated_thenSuccess() {
        List<String> traditionlList = new ArrayList<String>();
        traditionlList.add("foo");
        traditionlList.add("bar");
        traditionlList.add("baz");
        List<String> factoryCreatedList = List.of(List, "foo", "bar", "baz");
        Assert.assertEquals(traditionlList, factoryCreatedList);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void onElemAdd_ifUnSupportedOpExpnThrown_thenSuccess() {
        List<String> list = List.of(List, "foo", "bar");
        list.add("baz");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void onElemModify_ifUnSupportedOpExpnThrown_thenSuccess() {
        List<String> list = List.of(List, "foo", "bar");
        list.set(0, "baz");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void onElemRemove_ifUnSupportedOpExpnThrown_thenSuccess() {
        List<String> list = List.of(List, "foo", "bar");
        list.remove("foo");
    }

    @Test(expected = NullPointerException.class)
    public void onNullElem_ifNullPtrExpnThrown_thenSuccess() {
        List.of(List, "foo", "bar", null);
    }

    @Test
    public void ifNotArrayList_thenSuccess() {
        List<String> list = List.of(List, "foo", "bar");
        Assert.assertFalse((list instanceof ArrayList));
    }

    @Test
    public void ifListSizeIsOne_thenSuccess() {
        int[] arr = new int[]{ 1, 2, 3, 4 };
        List<int[]> list = List.of(List, arr);
        Assert.assertEquals(1, list.size());
        Assert.assertArrayEquals(arr, list.get(0));
    }
}

