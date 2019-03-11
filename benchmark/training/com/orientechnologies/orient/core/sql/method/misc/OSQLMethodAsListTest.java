package com.orientechnologies.orient.core.sql.method.misc;


import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the "asList()" method implemented by the OSQLMethodAsList class.  Note
 * that the only input to the execute() method from the OSQLMethod interface
 * that is used is the ioResult argument (the 4th argument).
 *
 * @author Michael MacFadden
 */
public class OSQLMethodAsListTest {
    private OSQLMethodAsList function;

    @Test
    public void testList() {
        // The expected behavior is to return the list itself.
        ArrayList<Object> aList = new ArrayList<Object>();
        aList.add(1);
        aList.add("2");
        Object result = function.execute(null, null, null, aList, null);
        Assert.assertEquals(result, aList);
    }

    @Test
    public void testNull() {
        // The expected behavior is to return an empty list.
        Object result = function.execute(null, null, null, null, null);
        Assert.assertEquals(result, new ArrayList<Object>());
    }

    @Test
    public void testCollection() {
        // The expected behavior is to return a list with all of the elements
        // of the collection in it.
        Set<Object> aCollection = new LinkedHashSet<Object>();
        aCollection.add(1);
        aCollection.add("2");
        Object result = function.execute(null, null, null, aCollection, null);
        ArrayList<Object> expected = new ArrayList<Object>();
        expected.add(1);
        expected.add("2");
        Assert.assertEquals(result, expected);
    }
}

