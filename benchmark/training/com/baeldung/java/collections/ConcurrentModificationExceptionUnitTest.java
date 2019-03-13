package com.baeldung.java.collections;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import org.junit.Assert;
import org.junit.Test;


public class ConcurrentModificationExceptionUnitTest {
    @Test
    public void changingContentWithSetDoesNotThrowConcurrentModificationException() throws Exception {
        ArrayList<Object> array = new ArrayList<>(Arrays.asList(0, "one", 2, "three"));
        for (Object item : array) {
            array.set(3, 3);
        }
    }

    @Test
    public void removingElementUsingIteratorAPI() throws Exception {
        List<String> originalList = new ArrayList<>(Arrays.asList("zero", "one", "two", "three"));
        Iterator<String> iterator = originalList.iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            if (Objects.equals(next, "one"))
                iterator.remove();

        } 
        Assert.assertEquals(originalList, Arrays.asList("zero", "two", "three"));
    }

    @Test
    public void modifyingContentAndIteratingUsingListIteratorAPI() throws Exception {
        List<String> originalList = new ArrayList<>(Arrays.asList("zero", "one", "two", "three"));
        ListIterator<String> iterator = originalList.listIterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            if (Objects.equals(next, "one")) {
                iterator.set("another");
            }
            if (Objects.equals(next, "two")) {
                iterator.remove();
            }
            if (Objects.equals(next, "three")) {
                iterator.add("four");
            }
        } 
        Assert.assertEquals(originalList, Arrays.asList("zero", "another", "three", "four"));
    }

    @Test
    public void removingElementUsingCopyAndListAPI() throws Exception {
        List<String> originalList = new ArrayList<>(Arrays.asList("zero", "one", "two", "three"));
        List<String> listCopy = new ArrayList<>(originalList);
        for (String next : listCopy) {
            if (Objects.equals(next, "one"))
                originalList.remove(((originalList.indexOf(next)) - 1));

        }
        Assert.assertEquals(originalList, Arrays.asList("one", "two", "three"));
    }

    @Test
    public void copyOnWriteList() throws Exception {
        List<String> originalList = new CopyOnWriteArrayList<>(Arrays.asList("zero", "one", "two", "three"));
        for (String next : originalList) {
            if (Objects.equals(next, "one"))
                originalList.remove(((originalList.indexOf(next)) - 1));

        }
        Assert.assertEquals(originalList, Arrays.asList("one", "two", "three"));
    }
}

