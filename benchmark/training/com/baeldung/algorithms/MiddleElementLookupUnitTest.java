package com.baeldung.algorithms;


import com.baeldung.algorithms.middleelementlookup.MiddleElementLookup;
import com.baeldung.algorithms.middleelementlookup.Node;
import java.util.LinkedList;
import org.junit.Assert;
import org.junit.Test;


public class MiddleElementLookupUnitTest {
    @Test
    public void whenFindingMiddleLinkedList_thenMiddleFound() {
        Assert.assertEquals("3", MiddleElementLookup.findMiddleElementLinkedList(MiddleElementLookupUnitTest.createLinkedList(5)).get());
        Assert.assertEquals("2", MiddleElementLookup.findMiddleElementLinkedList(MiddleElementLookupUnitTest.createLinkedList(4)).get());
    }

    @Test
    public void whenFindingMiddleFromHead_thenMiddleFound() {
        Assert.assertEquals("3", MiddleElementLookup.findMiddleElementFromHead(MiddleElementLookupUnitTest.createNodesList(5)).get());
        Assert.assertEquals("2", MiddleElementLookup.findMiddleElementFromHead(MiddleElementLookupUnitTest.createNodesList(4)).get());
    }

    @Test
    public void whenFindingMiddleFromHead1PassRecursively_thenMiddleFound() {
        Assert.assertEquals("3", MiddleElementLookup.findMiddleElementFromHead1PassRecursively(MiddleElementLookupUnitTest.createNodesList(5)).get());
        Assert.assertEquals("2", MiddleElementLookup.findMiddleElementFromHead1PassRecursively(MiddleElementLookupUnitTest.createNodesList(4)).get());
    }

    @Test
    public void whenFindingMiddleFromHead1PassIteratively_thenMiddleFound() {
        Assert.assertEquals("3", MiddleElementLookup.findMiddleElementFromHead1PassIteratively(MiddleElementLookupUnitTest.createNodesList(5)).get());
        Assert.assertEquals("2", MiddleElementLookup.findMiddleElementFromHead1PassIteratively(MiddleElementLookupUnitTest.createNodesList(4)).get());
    }

    @Test
    public void whenListEmptyOrNull_thenMiddleNotFound() {
        // null list
        Assert.assertFalse(MiddleElementLookup.findMiddleElementLinkedList(null).isPresent());
        Assert.assertFalse(MiddleElementLookup.findMiddleElementFromHead(null).isPresent());
        Assert.assertFalse(MiddleElementLookup.findMiddleElementFromHead1PassIteratively(null).isPresent());
        Assert.assertFalse(MiddleElementLookup.findMiddleElementFromHead1PassRecursively(null).isPresent());
        // empty LinkedList
        Assert.assertFalse(MiddleElementLookup.findMiddleElementLinkedList(new LinkedList()).isPresent());
        // LinkedList with nulls
        LinkedList<String> nullsList = new LinkedList<>();
        nullsList.add(null);
        nullsList.add(null);
        Assert.assertFalse(MiddleElementLookup.findMiddleElementLinkedList(nullsList).isPresent());
        // nodes with null values
        Assert.assertFalse(MiddleElementLookup.findMiddleElementFromHead(new Node(null)).isPresent());
        Assert.assertFalse(MiddleElementLookup.findMiddleElementFromHead1PassIteratively(new Node(null)).isPresent());
        Assert.assertFalse(MiddleElementLookup.findMiddleElementFromHead1PassRecursively(new Node(null)).isPresent());
    }
}

