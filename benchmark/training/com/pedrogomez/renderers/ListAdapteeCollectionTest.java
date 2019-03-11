package com.pedrogomez.renderers;


import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test class created to check the correct behaviour of ListAdapteeCollection.
 *
 * @author Pedro Vicente G?mez S?nchez.
 */
public class ListAdapteeCollectionTest {
    private AdapteeCollection<Object> collection;

    @Test
    public void shouldAddElement() {
        Object element = new Object();
        collection.add(element);
        Assert.assertEquals(1, collection.size());
        Assert.assertEquals(element, collection.get(0));
    }

    @Test
    public void shouldCountNumberOfElementsAdded() {
        collection.add(new Object());
        collection.add(new Object());
        Assert.assertEquals(2, collection.size());
    }

    @Test
    public void shouldRemoveElement() {
        Object element1 = new Object();
        Object element2 = new Object();
        collection.add(element1);
        collection.add(element2);
        collection.remove(element1);
        Assert.assertEquals(1, collection.size());
        Assert.assertEquals(element2, collection.get(0));
    }

    @Test
    public void shouldAddCollection() {
        Object element1 = new Object();
        Object element2 = new Object();
        List<Object> elements = Arrays.asList(element1, element2);
        collection.addAll(elements);
        Assert.assertEquals(2, collection.size());
        Assert.assertEquals(element1, collection.get(0));
        Assert.assertEquals(element2, collection.get(1));
    }

    @Test
    public void shouldRemoveCollection() {
        Object element1 = new Object();
        Object element2 = new Object();
        Object element3 = new Object();
        List<Object> elements = Arrays.asList(element1, element2);
        collection.addAll(elements);
        collection.add(element3);
        collection.removeAll(elements);
        Assert.assertEquals(1, collection.size());
        Assert.assertEquals(element3, collection.get(0));
    }

    @Test
    public void shouldClearCollection() {
        Object element1 = new Object();
        Object element2 = new Object();
        List<Object> elements = Arrays.asList(element1, element2);
        collection.addAll(elements);
        collection.clear();
        Assert.assertEquals(0, collection.size());
    }
}

