package com.baeldung.java.list;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class CustomListUnitTest {
    @Test
    public void givenEmptyList_whenIsEmpty_thenTrueIsReturned() {
        List<Object> list = new CustomList();
        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void givenNonEmptyList_whenIsEmpty_thenFalseIsReturned() {
        List<Object> list = new CustomList();
        list.add(null);
        Assert.assertFalse(list.isEmpty());
    }

    @Test
    public void givenListWithAnElement_whenSize_thenOneIsReturned() {
        List<Object> list = new CustomList();
        list.add(null);
        Assert.assertEquals(1, list.size());
    }

    @Test
    public void givenListWithAnElement_whenGet_thenThatElementIsReturned() {
        List<Object> list = new CustomList();
        list.add("baeldung");
        Object element = list.get(0);
        Assert.assertEquals("baeldung", element);
    }

    @Test
    public void givenEmptyList_whenElementIsAdded_thenGetReturnsThatElement() {
        List<Object> list = new CustomList();
        boolean succeeded = list.add(null);
        Assert.assertTrue(succeeded);
    }

    @Test
    public void givenListWithAnElement_whenAnotherIsAdded_thenGetReturnsBoth() {
        List<Object> list = new CustomList();
        list.add("baeldung");
        list.add(".com");
        Object element1 = list.get(0);
        Object element2 = list.get(1);
        Assert.assertEquals("baeldung", element1);
        Assert.assertEquals(".com", element2);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void whenAddToSpecifiedIndex_thenExceptionIsThrown() {
        new CustomList().add(0, null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void whenAddAllToTheEnd_thenExceptionIsThrown() {
        Collection<Object> collection = new ArrayList<>();
        List<Object> list = new CustomList();
        list.addAll(collection);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void whenAddAllToSpecifiedIndex_thenExceptionIsThrown() {
        Collection<Object> collection = new ArrayList<>();
        List<Object> list = new CustomList();
        list.addAll(0, collection);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void whenRemoveAtSpecifiedIndex_thenExceptionIsThrown() {
        List<Object> list = new CustomList();
        list.add("baeldung");
        list.remove(0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void whenRemoveSpecifiedElement_thenExceptionIsThrown() {
        List<Object> list = new CustomList();
        list.add("baeldung");
        list.remove("baeldung");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void whenRemoveAll_thenExceptionIsThrown() {
        Collection<Object> collection = new ArrayList<>();
        collection.add("baeldung");
        List<Object> list = new CustomList();
        list.removeAll(collection);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void whenRetainAll_thenExceptionIsThrown() {
        Collection<Object> collection = new ArrayList<>();
        collection.add("baeldung");
        List<Object> list = new CustomList();
        list.add("baeldung");
        list.retainAll(collection);
    }

    @Test
    public void givenEmptyList_whenContains_thenFalseIsReturned() {
        List<Object> list = new CustomList();
        Assert.assertFalse(list.contains(null));
    }

    @Test
    public void givenListWithAnElement_whenContains_thenTrueIsReturned() {
        List<Object> list = new CustomList();
        list.add("baeldung");
        Assert.assertTrue(list.contains("baeldung"));
    }

    @Test
    public void givenListWithAnElement_whenContainsAll_thenTrueIsReturned() {
        Collection<Object> collection = new ArrayList<>();
        collection.add("baeldung");
        List<Object> list = new CustomList();
        list.add("baeldung");
        Assert.assertTrue(list.containsAll(collection));
    }

    @Test
    public void givenList_whenContainsAll_thenEitherTrueOrFalseIsReturned() {
        Collection<Object> collection1 = new ArrayList<>();
        collection1.add("baeldung");
        collection1.add(".com");
        Collection<Object> collection2 = new ArrayList<>();
        collection2.add("baeldung");
        List<Object> list = new CustomList();
        list.add("baeldung");
        Assert.assertFalse(list.containsAll(collection1));
        Assert.assertTrue(list.containsAll(collection2));
    }

    @Test
    public void givenList_whenSet_thenOldElementIsReturned() {
        List<Object> list = new CustomList();
        list.add("baeldung");
        Object element = list.set(0, null);
        Assert.assertEquals("baeldung", element);
        Assert.assertNull(list.get(0));
    }

    @Test
    public void givenList_whenClear_thenAllElementsAreRemoved() {
        List<Object> list = new CustomList();
        list.add("baeldung");
        list.clear();
        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void givenList_whenIndexOf_thenIndexZeroIsReturned() {
        List<Object> list = new CustomList();
        list.add("baeldung");
        Assert.assertEquals(0, list.indexOf("baeldung"));
    }

    @Test
    public void givenList_whenIndexOf_thenPositiveIndexOrMinusOneIsReturned() {
        List<Object> list = new CustomList();
        list.add("baeldung");
        list.add(".com");
        list.add(".com");
        Assert.assertEquals(1, list.indexOf(".com"));
        Assert.assertEquals((-1), list.indexOf("com"));
    }

    @Test
    public void whenLastIndexOf_thenIndexZeroIsReturned() {
        List<Object> list = new CustomList();
        list.add("baeldung");
        Assert.assertEquals(0, list.lastIndexOf("baeldung"));
    }

    @Test
    public void whenLastIndexOf_thenPositiveIndexOrMinusOneIsReturned() {
        List<Object> list = new CustomList();
        list.add("baeldung");
        list.add("baeldung");
        list.add(".com");
        Assert.assertEquals(1, list.lastIndexOf("baeldung"));
        Assert.assertEquals((-1), list.indexOf("com"));
    }

    @Test
    public void whenSubListZeroToOne_thenListContainingFirstElementIsReturned() {
        List<Object> list = new CustomList();
        list.add("baeldung");
        List<Object> subList = list.subList(0, 1);
        Assert.assertEquals("baeldung", subList.get(0));
    }

    @Test
    public void whenSubListOneToTwo_thenListContainingSecondElementIsReturned() {
        List<Object> list = new CustomList();
        list.add("baeldung");
        list.add(".");
        list.add("com");
        List<Object> subList = list.subList(1, 2);
        Assert.assertEquals(1, subList.size());
        Assert.assertEquals(".", subList.get(0));
    }

    @Test
    public void givenListWithElements_whenToArray_thenArrayContainsThose() {
        List<Object> list = new CustomList();
        list.add("baeldung");
        list.add(".com");
        Object[] array = list.toArray();
        Assert.assertArrayEquals(new Object[]{ "baeldung", ".com" }, array);
    }

    @Test
    public void givenListWithAnElement_whenToArray_thenInputArrayIsReturned() {
        List<Object> list = new CustomList();
        list.add("baeldung");
        String[] input = new String[1];
        String[] output = list.toArray(input);
        Assert.assertArrayEquals(new String[]{ "baeldung" }, input);
    }

    @Test
    public void whenToArrayIsCalledWithEmptyInputArray_thenNewArrayIsReturned() {
        List<Object> list = new CustomList();
        list.add("baeldung");
        String[] input = new String[]{  };
        String[] output = list.toArray(input);
        Assert.assertArrayEquals(new String[]{ "baeldung" }, output);
    }

    @Test
    public void whenToArrayIsCalledWithLargerInput_thenOutputHasTrailingNull() {
        List<Object> list = new CustomList();
        list.add("baeldung");
        String[] input = new String[2];
        String[] output = list.toArray(input);
        Assert.assertArrayEquals(new String[]{ "baeldung", null }, output);
    }

    @Test
    public void givenListWithOneElement_whenIterator_thenThisElementIsNext() {
        List<Object> list = new CustomList();
        list.add("baeldung");
        Iterator<Object> iterator = list.iterator();
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals("baeldung", iterator.next());
    }

    @Test
    public void whenIteratorHasNextIsCalledTwice_thenTheSecondReturnsFalse() {
        List<Object> list = new CustomList();
        list.add("baeldung");
        Iterator<Object> iterator = list.iterator();
        iterator.next();
        Assert.assertFalse(iterator.hasNext());
    }
}

