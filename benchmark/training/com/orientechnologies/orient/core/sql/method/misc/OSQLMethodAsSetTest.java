package com.orientechnologies.orient.core.sql.method.misc;


import java.util.ArrayList;
import java.util.HashSet;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the "asSet()" method implemented by the OSQLMethodAsSet class. Note
 * that the only input to the execute() method from the OSQLMethod interface
 * that is used is the ioResult argument (the 4th argument).
 *
 * @author Michael MacFadden
 */
public class OSQLMethodAsSetTest {
    private OSQLMethodAsSet function;

    @Test
    public void testSet() {
        // The expected behavior is to return the set itself.
        HashSet<Object> aSet = new HashSet<Object>();
        aSet.add(1);
        aSet.add("2");
        Object result = function.execute(null, null, null, aSet, null);
        Assert.assertEquals(result, aSet);
    }

    @Test
    public void testNull() {
        // The expected behavior is to return an empty set.
        Object result = function.execute(null, null, null, null, null);
        Assert.assertEquals(result, new HashSet<Object>());
    }

    @Test
    public void testCollection() {
        // The expected behavior is to return a set with all of the elements
        // of the collection in it.
        ArrayList<Object> aCollection = new ArrayList<Object>();
        aCollection.add(1);
        aCollection.add("2");
        Object result = function.execute(null, null, null, aCollection, null);
        HashSet<Object> expected = new HashSet<Object>();
        expected.add(1);
        expected.add("2");
        Assert.assertEquals(result, expected);
    }
}

