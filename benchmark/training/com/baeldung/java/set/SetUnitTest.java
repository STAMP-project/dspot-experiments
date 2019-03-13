package com.baeldung.java.set;


import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SetUnitTest {
    private static final Logger LOG = LoggerFactory.getLogger(SetUnitTest.class);

    @Test
    public void givenTreeSet_whenRetrievesObjects_thenNaturalOrder() {
        Set<String> set = new TreeSet<>();
        set.add("Baeldung");
        set.add("is");
        set.add("Awesome");
        Assert.assertEquals(3, set.size());
        Assert.assertTrue(set.iterator().next().equals("Awesome"));
    }

    @Test(expected = NullPointerException.class)
    public void givenTreeSet_whenAddNullObject_thenNullPointer() {
        Set<String> set = new TreeSet<>();
        set.add("Baeldung");
        set.add("is");
        set.add(null);
    }

    @Test
    public void givenHashSet_whenAddNullObject_thenOK() {
        Set<String> set = new HashSet<>();
        set.add("Baeldung");
        set.add("is");
        set.add(null);
        Assert.assertEquals(3, set.size());
    }

    @Test
    public void givenHashSetAndTreeSet_whenAddObjects_thenHashSetIsFaster() {
        long hashSetInsertionTime = SetUnitTest.measureExecution(() -> {
            Set<String> set = new HashSet<>();
            set.add("Baeldung");
            set.add("is");
            set.add("Awesome");
        });
        long treeSetInsertionTime = SetUnitTest.measureExecution(() -> {
            Set<String> set = new TreeSet<>();
            set.add("Baeldung");
            set.add("is");
            set.add("Awesome");
        });
        SetUnitTest.LOG.debug("HashSet insertion time: {}", hashSetInsertionTime);
        SetUnitTest.LOG.debug("TreeSet insertion time: {}", treeSetInsertionTime);
    }

    @Test
    public void givenHashSetAndTreeSet_whenAddDuplicates_thenOnlyUnique() {
        Set<String> set = new HashSet<>();
        set.add("Baeldung");
        set.add("Baeldung");
        Assert.assertTrue(((set.size()) == 1));
        Set<String> set2 = new TreeSet<>();
        set2.add("Baeldung");
        set2.add("Baeldung");
        Assert.assertTrue(((set2.size()) == 1));
    }

    @Test(expected = ConcurrentModificationException.class)
    public void givenHashSet_whenModifyWhenIterator_thenFailFast() {
        Set<String> set = new HashSet<>();
        set.add("Baeldung");
        Iterator<String> it = set.iterator();
        while (it.hasNext()) {
            set.add("Awesome");
            it.next();
        } 
    }
}

